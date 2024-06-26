package com.bobbyprabowo.android.googlelinking

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.android.gms.auth.api.identity.SaveAccountLinkingTokenRequest

class ConsentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consent)
        val view = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.acceptConsentButton).setOnClickListener {
            onConsentAccepted()
        }

        findViewById<Button>(R.id.rejectConsentButton).setOnClickListener {
            onConsentRejectedOrCanceled()
        }
    }

    private fun onConsentAccepted() {
        // Obtain a token (for simplicity, we’ll ignore the async nature
        // of the following call)
        val token = getToken(this, "oauth-learn", "oauth2:profile email")
        Toast.makeText(this, "GAL Token: $token", Toast.LENGTH_LONG).show()
        val intent = Intent()
            .putExtra(
                SaveAccountLinkingTokenRequest.EXTRA_TOKEN,
                token)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onConsentRejectedOrCanceled() {
        Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        fun createPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(
                context,
                0,
                Intent(context, ConsentActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
