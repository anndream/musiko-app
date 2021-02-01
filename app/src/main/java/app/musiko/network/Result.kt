
package app.musiko.network

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    object Loading : Result<Nothing>()
    data class Error(val error: Exception) : Result<Nothing>()
}
