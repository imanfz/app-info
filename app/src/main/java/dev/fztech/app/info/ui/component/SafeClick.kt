package io.github.imanfz.jetpackcomposedoc.ui.component

/**
 * Safe click
 *
 * @constructor Create empty Safe click
 */
internal interface SafeClick {
    /**
     * Process event
     *
     * @param event
     * @receiver
     */
    fun processEvent(event: () -> Unit)

    companion object
}

/**
 * Get
 *
 * @return
 */
internal fun SafeClick.Companion.get(): SafeClick =
    SafeClickImpl()

/**
 * Safe click impl
 *
 * @constructor Create empty Safe click impl
 */
private class SafeClickImpl : SafeClick {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= 300L) {
            event.invoke()
        }
        lastEventTimeMs = now
    }
}