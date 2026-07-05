package rabia.urdu.keyboard.service

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.net.Uri
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import rabia.urdu.keyboard.ui.keyboard.KeyboardMode
import rabia.urdu.keyboard.ui.keyboard.KeyboardScreen
import rabia.urdu.keyboard.ui.theme.AppTheme

/**
 * Rabia Keyboard IME service. Hosts a ComposeView as the soft keyboard view.
 *
 * NOTE: For Compose to work inside an InputMethodService we must act as the
 * LifecycleOwner, ViewModelStoreOwner AND SavedStateRegistryOwner, and wire
 * them onto the ComposeView's tree. Missing any of these causes the keyboard
 * view to stay blank after the user enables + selects the IME.
 */
class RabiaKeyboardService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStoreInstance
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateInputView(): View {
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@RabiaKeyboardService)
            setViewTreeViewModelStoreOwner(this@RabiaKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@RabiaKeyboardService)
            setContent {
                AppTheme {
                    KeyboardScreen(
                        onKeyPress = { key -> handleKey(key) },
                        onShift = { },
                        onDelete = { handleDelete() },
                        onSpace = { handleSpace() },
                        onEnter = { handleEnter() },
                        onSwitchSymbols = { },
                        onSwitchLanguage = { },
                        onBrowser = { openBrowser() },
                        onSettings = { openKeyboardSettings() },
                        onAlternate = { ch -> handleKey(ch) },
                        isShifted = false,
                        mode = KeyboardMode.URDU
                    )
                }
            }
        }
        return view
    }

    override fun onCreateCandidatesView(): View? = null

    override fun onStartInput(info: EditorInfo?, restarting: Boolean) {
        super.onStartInput(info, restarting)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStoreInstance.clear()
    }

    private fun handleKey(key: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(key, 1)
    }

    private fun handleDelete() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (selected.isNullOrEmpty()) {
            ic.deleteSurroundingText(1, 0)
        } else {
            ic.commitText("", 1)
        }
    }

    private fun handleSpace() {
        currentInputConnection?.commitText(" ", 1)
    }

    private fun handleEnter() {
        val ic = currentInputConnection ?: return
        val imeOptions = currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (imeOptions == EditorInfo.IME_ACTION_NONE ||
            imeOptions == EditorInfo.IME_ACTION_UNSPECIFIED
        ) {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        } else {
            ic.performEditorAction(imeOptions)
        }
    }

    /** Long-press space → open system input-method settings (Rabia-style). */
    private fun openKeyboardSettings() {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    /** Browser button → open default web browser (Rabia-style quick web access). */
    private fun openBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
