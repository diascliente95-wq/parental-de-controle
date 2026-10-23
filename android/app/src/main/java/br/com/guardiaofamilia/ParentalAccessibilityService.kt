package br.com.guardiaofamilia

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class ParentalAccessibilityService : AccessibilityService() {
    private var blocker: View? = null
    private var blockedPackage: String? = null
    private val prefs by lazy { getSharedPreferences("family_settings", MODE_PRIVATE) }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName == this.packageName) {
            removeBlocker()
            return
        }
        val selected = prefs.getStringSet("blocked_packages", emptySet()) ?: emptySet()
        if (packageName in selected) showBlocker(packageName) else removeBlocker()
    }

    override fun onInterrupt() { removeBlocker() }

    override fun onDestroy() {
        removeBlocker()
        super.onDestroy()
    }

    private fun showBlocker(packageName: String) {
        if (blocker != null && blockedPackage == packageName) return
        removeBlocker()
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(36, 40, 36, 40)
            setBackgroundColor(Color.rgb(11, 18, 32))
            isClickable = true
            isFocusable = true
        }
        panel.addView(TextView(this).apply {
            text = "🛡️ Guardião Família"
            textSize = 26f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        })
        panel.addView(TextView(this).apply {
            text = "Este aplicativo está pausado"
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.rgb(99, 230, 190))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 14)
        })
        panel.addView(TextView(this).apply {
            text = "O responsável configurou uma pausa para este aplicativo. Converse com ele se precisar de acesso."
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        })
        panel.addView(Button(this).apply {
            text = "Voltar à tela inicial"
            setOnClickListener {
                removeBlocker()
                val home = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                    addCategory(android.content.Intent.CATEGORY_HOME)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(home)
            }
        })
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        try {
            wm.addView(panel, params)
            blocker = panel
            blockedPackage = packageName
        } catch (_: Exception) {
            blocker = null
            blockedPackage = null
        }
    }

    private fun removeBlocker() {
        val old = blocker ?: return
        try { (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(old) } catch (_: Exception) { }
        blocker = null
        blockedPackage = null
    }
}
