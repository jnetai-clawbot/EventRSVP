package com.jnetai.eventrsvp.ui.about

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.eventrsvp.BuildConfig
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_about)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"

        val textVersion = findViewById<TextView>(R.id.textVersion)
        val textAppName = findViewById<TextView>(R.id.textAppName)
        val btnCheckUpdate = findViewById<Button>(R.id.btnCheckUpdate)
        val btnShare = findViewById<Button>(R.id.btnShareApp)

        textAppName.text = "EventRSVP"
        textVersion.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        btnCheckUpdate.setOnClickListener {
            checkForUpdates()
        }

        btnShare.setOnClickListener {
            shareApp()
        }
    }

    private fun checkForUpdates() {
        Toast.makeText(this, "Checking for updates...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val url = URL("https://api.github.com/repos/jnetai-clawbot/EventRSVP/releases/latest")
                    val conn = url.openConnection()
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    conn.connect()
                    val text = conn.getInputStream().bufferedReader().readText()
                    val json = JSONObject(text)
                    json.optString("tag_name", "") to json.optString("html_url", "")
                }

                val (latestVersion, releaseUrl) = result
                if (latestVersion.isEmpty()) {
                    Toast.makeText(this@AboutActivity, "No releases found yet", Toast.LENGTH_LONG).show()
                } else if (latestVersion == BuildConfig.VERSION_NAME || latestVersion == "v${BuildConfig.VERSION_NAME}") {
                    Toast.makeText(this@AboutActivity, "You're on the latest version!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@AboutActivity, "Update available: $latestVersion\n$releaseUrl", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AboutActivity, "Error checking updates: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun shareApp() {
        val text = """
            |EventRSVP - Event & RSVP Manager
            |Version: ${BuildConfig.VERSION_NAME}
            |
            |Plan events, track RSVPs, manage guests — all from your phone.
            |
            |Get it on GitHub:
            |https://github.com/jnetai-clawbot/EventRSVP
        """.trimMargin()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share EventRSVP"))
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}