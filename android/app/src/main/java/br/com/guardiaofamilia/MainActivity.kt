package br.com.guardiaofamilia

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var report: TextView
    private lateinit var limitInput: EditText
    private val prefs by lazy { getSharedPreferences("family_settings", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 28)
            setBackgroundColor(Color.rgb(11, 18, 32))
        }
        fun label(text: String, size: Float, color: Int = Color.WHITE) = TextView(this).apply {
            this.text = text; textSize = size; setTextColor(color); setPadding(0, 10, 0, 10)
        }
        fun addButton(text: String, action: () -> Unit) {
            root.addView(Button(this).apply { this.text = text; setOnClickListener { action() } },
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 8; bottomMargin = 8 })
        }
        root.addView(label("🛡️ Guardião Família", 26f))
        root.addView(label("Controle parental transparente", 15f, Color.rgb(99, 230, 190)))
        root.addView(label("Este relatório usa a permissão especial Acesso ao uso. O responsável deve ativá-la manualmente nas configurações Android.", 14f, Color.LTGRAY))
        addButton("Configurar acesso ao uso") { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
        status = label("", 14f, Color.rgb(99, 230, 190)); root.addView(status)
        root.addView(label("Limite diário combinado (minutos)", 14f, Color.LTGRAY))
        limitInput = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(prefs.getInt("daily_limit", 120).toString())
            setTextColor(Color.WHITE); setHint("Ex.: 120")
        }
        root.addView(limitInput, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        addButton("Salvar limite e atualizar") {
            val value = limitInput.text.toString().toIntOrNull()
            if (value == null || value !in 15..1440) status.text = "Informe um limite entre 15 e 1440 minutos."
            else { prefs.edit().putInt("daily_limit", value).apply(); refreshReport() }
        }
        report = label("", 15f); root.addView(report)
        root.addView(label("Privacidade: dados ficam neste aparelho. Não enviamos histórico a um servidor. Esta fase ainda não bloqueia apps nem monitora remotamente.", 12f, Color.rgb(255, 210, 140)))
        addButton("Atualizar relatório") { refreshReport() }
        setContentView(root)
        refreshReport()
    }

    override fun onResume() { super.onResume(); if (::status.isInitialized && ::report.isInitialized) refreshReport() }

    private fun matchWidth() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION")
        return appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
    }

    private fun refreshReport() {
        if (!::status.isInitialized || !::report.isInitialized) return
        if (!hasUsageAccess()) {
            status.text = "Permissão ainda não concedida."
            report.text = "Toque em Configurar acesso ao uso, encontre Guardião Família, permita o acesso e volte ao app."
            return
        }
        status.text = "Acesso ao uso autorizado neste aparelho."
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val manager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.timeInMillis, System.currentTimeMillis())
            .filter { it.totalTimeInForeground > 0 && it.packageName != packageName }
            .sortedByDescending { it.totalTimeInForeground }
        val totalMs = stats.sumOf { it.totalTimeInForeground }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMs)
        val limit = prefs.getInt("daily_limit", 120)
        val lines = stats.take(8).map {
            val name = try { packageManager.getApplicationLabel(packageManager.getApplicationInfo(it.packageName, 0)).toString() }
                catch (_: Exception) { it.packageName }
            "• " + name + " — " + TimeUnit.MILLISECONDS.toMinutes(it.totalTimeInForeground) + " min"
        }
        report.text = buildString {
            append("Uso registrado hoje (soma dos apps): " + minutes + " min\n")
            append("Limite combinado: " + limit + " min\n")
            append(if (minutes >= limit) "Atenção: total atingiu ou passou do limite. Este protótipo não bloqueia apps.\n" else "Total abaixo do limite configurado.\n")
            append("\nApps com maior uso:\n")
            append(if (lines.isEmpty()) "Ainda não há dados disponíveis." else lines.joinToString("\n"))
            append("\n\nAtualizado: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date()))
        }
    }
}
