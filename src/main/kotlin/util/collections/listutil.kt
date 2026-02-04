
package moe.nea.notfimament.util.collections

fun <T, R> List<T>.lastNotNullOfOrNull(func: (T) -> R?): R? {
    for (i in indices.reversed()) {
        return func(this[i]) ?: continue
    }
    return null
}
