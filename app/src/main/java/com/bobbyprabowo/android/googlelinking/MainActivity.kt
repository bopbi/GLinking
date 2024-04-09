package com.bobbyprabowo.android.googlelinking

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SaveAccountLinkingTokenRequest

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var serviceIdEditText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val view = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        serviceIdEditText = findViewById(R.id.serviceIdEditText)
        val isDebugBuildAndConfigContainServiceId = BuildConfig.DEBUG && BuildConfig.SERVICE_ID.isNotBlank()
        if (isDebugBuildAndConfigContainServiceId) {
            serviceIdEditText?.setText(BuildConfig.SERVICE_ID, TextView.BufferType.EDITABLE)
        }
        // Create an ActivityResultLauncher which registers a callback for the
        // Activity result contract
        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult())
        { result ->
            if (result.resultCode == RESULT_OK) {
                // Successfully finished the flow and saved the token
                Log.i(TAG, "activityResultLauncher: Success to save token result: ${result.resultCode}")
            } else {
                // Flow failed, for example the user may have canceled the flow
                Log.e(TAG, "activityResultLauncher : Failed to save token result: ${result.resultCode}")
            }
        }

        findViewById<Button>(R.id.linkingButton).setOnClickListener {
            openGoogleLink(activityResultLauncher)
        }
    }

    private fun openGoogleLink(activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val consentPendingIntent = ConsentActivity.createPendingIntent(this)

        val serviceId = serviceIdEditText?.getText()?.toString()
        if (serviceId.isNullOrBlank()) {
            Toast.makeText(this, "Service ID is empty", Toast.LENGTH_SHORT).show()
            return
        }
        // Build token save request
        val request = SaveAccountLinkingTokenRequest.builder()
            .setTokenType(SaveAccountLinkingTokenRequest.TOKEN_TYPE_AUTH_CODE)
            .setConsentPendingIntent(consentPendingIntent)
            .setServiceId(serviceId)
            //Set the scopes that the token is valid for on your platform
            .setScopes(
                listOf(
//                    "https://www.googleapis.com/auth/userinfo.email",
                    "https://www.googleapis.com/auth/userinfo.profile",
                )
            )
            .build()

        // Launch consent activity and retrieve token
        Identity.getCredentialSavingClient(this)
            .saveAccountLinkingToken(request)
            .addOnSuccessListener { saveAccountLinkingTokenResult ->
                if (saveAccountLinkingTokenResult.hasResolution()) {
                    val pendingIntent = saveAccountLinkingTokenResult
                        .pendingIntent
                    pendingIntent?.let {
                        // Start the intent sender to launch the consent flow
                        val intentSenderRequest = IntentSenderRequest
                            .Builder(pendingIntent).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    } ?: run {
                        // This should not happen, let’s log this
                        Log.e(TAG, "Failed to save token")
                    }
                } else {
                    // This should not happen, let’s log this
                    Log.e(TAG, "Failed to save token")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save token", e)

            }
    }
}
