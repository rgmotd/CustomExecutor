import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue

class CustomExecutor<T, K>(private val scope: CoroutineScope, private val maxCoroutines: Int) {

    private val queue = ConcurrentLinkedQueue<WorkItem<T, K>>()
    private val jobs = mutableListOf<Job>()

    fun execute(func: suspend (T) -> K, arg: T): Future<K> {
        val future = Future<K>()
        val workItem = WorkItem(func, arg, future)
        queue.add(workItem)
        if (isJobAvailable()) {
            runJob()
        }
        return future

    }

    fun map(func: suspend (T) -> K, args: List<T>): List<Future<K>> {
        val futures = mutableListOf<Future<K>>()
        args.forEach {
            futures.add(execute(func, it))
        }
        return futures
    }

    suspend fun shutdown() {
        jobs.forEach { job ->
            job.join()
        }
    }

    private fun runJob() {
        val job = scope.launch {
            var element = queue.poll()
            while (element != null) {
                val res = element.func(element.arg)
                element.future.setResult(res)
                element = queue.poll()
            }
        }
        jobs.add(job)
    }

    private fun isJobAvailable(): Boolean {
        return jobs.size < maxCoroutines
    }

    data class WorkItem<T, K>(
        val func: suspend (T) -> K,
        val arg: T,
        val future: Future<K>
    )
}