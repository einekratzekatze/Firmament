
package moe.nea.notfimament.util


fun runNull(block: () -> Unit): Nothing? {
    block()
    return null
}
