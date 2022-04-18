import kotlinx.coroutines.delay
import java.util.*

class Future<T> {
    private var result: T? = null

    fun setResult(result: T) {
        this.result = result
    }

    suspend fun getResult(): T {
        while (true) {
            if (result != null) {
                println("${Calendar.getInstance().time}")
                return result!!
            }
            delay(1)
        }
    }
}