package com.online.course.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.online.course.ui.SignInActivity


class ResultContracts {

    class SelectFile : ActivityResultContract<String, Uri?>() {

        override fun createIntent(context: Context, input: String): Intent =
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = input
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
            Activity.RESULT_OK -> intent?.data
            else -> null
        }
    }

    class GoogleLogin : ActivityResultContract<GoogleSignInClient, Task<GoogleSignInAccount>>() {

        override fun createIntent(context: Context, signInClient: GoogleSignInClient): Intent =
            signInClient.signInIntent

        override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount> {
            return GoogleSignIn.getSignedInAccountFromIntent(intent)
        }
    }

    class ActivityResultResponse : ActivityResultContract<Intent, Intent?>() {

        override fun createIntent(context: Context, intent: Intent): Intent = intent

        override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
            if (resultCode == Activity.RESULT_OK) {
                return intent
            }

            return null
        }
    }



}