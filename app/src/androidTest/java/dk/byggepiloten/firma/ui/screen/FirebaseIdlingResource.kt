// app/src/androidTest/java/dk/byggepiloten/firma/ui/screen/FirebaseIdlingResource.kt
package dk.byggepiloten.firma.ui.screen

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class FirebaseIdlingResource(private val name: String) : IdlingResource {

    private val idle = AtomicBoolean(true)
    private var callback: IdlingResource.ResourceCallback? = null

    fun startWaiting() {
        idle.set(false)
    }

    fun finishWaiting() {
        if (idle.compareAndSet(false, true)) {
            callback?.onTransitionToIdle()
        }
    }

    override fun getName() = name
    override fun isIdleNow() = idle.get()
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}