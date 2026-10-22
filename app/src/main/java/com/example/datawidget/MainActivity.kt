package com.example.datawidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var info: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (24 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
        }
        info = TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
        }
        val button = Button(this).apply {
            text = getString(R.string.open_settings)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
        layout.addView(info)
        layout.addView(button)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        info.text = if (UsageHelper.hasPermission(this)) {
            getString(R.string.month_usage, UsageHelper.text(this))
        } else {
            getString(R.string.guide)
        }
        DataWidgetProvider.refreshAll(this)
    }
}
