package com.shai.pokerwithfriendsandroid.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.shai.pokerwithfriendsandroid.components.BottomComponent
import com.shai.pokerwithfriendsandroid.components.BottomLoginTextComponent
import com.shai.pokerwithfriendsandroid.components.ConfirmPasswordField
import com.shai.pokerwithfriendsandroid.components.EmailField
import com.shai.pokerwithfriendsandroid.components.ForgotPasswordLink
import com.shai.pokerwithfriendsandroid.components.HeadingText
import com.shai.pokerwithfriendsandroid.components.MyTextField
import com.shai.pokerwithfriendsandroid.components.NameField
import com.shai.pokerwithfriendsandroid.components.PasswordField
import com.shai.pokerwithfriendsandroid.components.PasswordInputComponent
import com.shai.pokerwithfriendsandroid.components.WelcomeText
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import com.shai.pokerwithfriendsandroid.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as? Activity

    // Initialize Google Sign-In options
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("170448456692-69jo792vj6btse45eppt2f099ukrmulc.apps.googleusercontent.com")
            .requestEmail().build()

        GoogleSignIn.getClient(activity ?: context, gso)
    }

    // Handle Google sign-in result
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Get the Google account and authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    account?.let {
                        // Now authenticate with Firebase using the Google account credentials
                        viewModel.signInWithGoogle(it)
                    }
                } catch (e: ApiException) {
                    Log.e("LoginScreen", "Google sign in failed", e)
                }
            } else {
                Log.e("LoginScreen", "Google sign-in result was canceled or failed")
            }
        }

    val screenMode by viewModel.screenMode.collectAsState()
    val screenContentMap: Map<LoginScreenState, @Composable (LoginViewModel) -> Unit> = mapOf(
        LoginScreenState.Login to { vm ->
            LoginView(vm) {
                resultLauncher.launch(googleSignInClient.signInIntent)
                viewModel.initiateGoogleSignIn(googleSignInClient)
            }
        },
        LoginScreenState.Register to { vm -> RegisterView(vm) {} },
        LoginScreenState.ForgotPassword to { vm -> ForgotPasswordView(vm) })
    val screenContent = screenContentMap[screenMode]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        WelcomeText()
        Spacer(modifier = Modifier.height(32.dp))
        screenContent?.invoke(viewModel)
    }
}

@Composable
fun ForgotPasswordView(
    viewModel: LoginViewModel,
) {
    Column {
        HeadingText(LoginScreenState.ForgotPassword)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        BottomComponent(LoginScreenState.ForgotPassword){}
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun LoginView(
    viewModel: LoginViewModel, onGoogleLoginClick: () -> Unit
) {
    Column {
        HeadingText(LoginScreenState.Login)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(viewModel)
        Spacer(modifier = Modifier.height(8.dp))
        ForgotPasswordLink { viewModel.onForgotPassword() }
        BottomComponent(LoginScreenState.Login, onGoogleLoginClick = onGoogleLoginClick){
            viewModel.loginWithEmail()
        }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        BottomLoginTextComponent(initialText = "Don't have an account? ",
            actionText = "Register Now!",
            onActionTextClick = { viewModel.onRegister() })
    }
}

@Composable
fun RegisterView(
    viewModel: LoginViewModel, onGoogleLoginClick: () -> Unit
) {
    Column {
        HeadingText(LoginScreenState.Register)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        NameField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        ConfirmPasswordField(viewModel)
        BottomComponent(LoginScreenState.Register, onGoogleLoginClick = onGoogleLoginClick){}
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        BottomLoginTextComponent(initialText = "Already have an account? ",
            actionText = "Login!",
            onActionTextClick = { viewModel.onRegister() })
    }
}