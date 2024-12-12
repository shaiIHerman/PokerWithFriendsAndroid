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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.shai.pokerwithfriendsandroid.components.BottomComponent
import com.shai.pokerwithfriendsandroid.components.BottomLoginTextComponent
import com.shai.pokerwithfriendsandroid.components.ConfirmPasswordField
import com.shai.pokerwithfriendsandroid.components.EmailField
import com.shai.pokerwithfriendsandroid.components.ForgotPasswordLink
import com.shai.pokerwithfriendsandroid.components.HeadingText
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.components.NameField
import com.shai.pokerwithfriendsandroid.components.PasswordField
import com.shai.pokerwithfriendsandroid.components.WelcomeText
import com.shai.pokerwithfriendsandroid.screens.states.AuthState
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import com.shai.pokerwithfriendsandroid.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(), onAuthenticated: () -> Unit
) {

    val googleSignInClient by viewModel.googleSignInClient.observeAsState(null)
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
    val googleSignInAction = remember {
        {
            resultLauncher.launch(googleSignInClient!!.signInIntent)
            viewModel.initiateGoogleSignIn()
        }
    }
    val screenMode by viewModel.screenMode.collectAsState()
    val screenContentMap: Map<LoginScreenState, @Composable (LoginViewModel) -> Unit> = mapOf(
        LoginScreenState.Login to { vm -> LoginView(vm) { googleSignInAction() } },
        LoginScreenState.Register to { vm -> RegisterView(vm) { googleSignInAction() } },
        LoginScreenState.ForgotPassword to { vm -> ForgotPasswordView(vm) })
    val screenContent = screenContentMap[screenMode]

    val authState by viewModel.authState.collectAsState()
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthenticated()
        }
    }
    when (val state = authState) {
        AuthState.Authenticated -> {}
        AuthState.Initial -> InitialView(screenContent, viewModel)
        AuthState.SigningIn -> LoadingState()
        is AuthState.Error -> {
            // todo: Handle error state
            Log.e("LoginScreen", "Sign-In failed", Throwable(state.message))
        }
    }
}

@Composable
fun InitialView(
    screenContent: @Composable ((LoginViewModel) -> Unit)?, viewModel: LoginViewModel
) {
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
        AuthTopPart(state = LoginScreenState.ForgotPassword, viewModel)
        BottomComponent(LoginScreenState.ForgotPassword) {}
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun LoginView(
    viewModel: LoginViewModel, onGoogleLoginClick: () -> Unit
) {
    Column {
        AuthTopPart(state = LoginScreenState.Login, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(viewModel, isRegister = false)
        Spacer(modifier = Modifier.height(8.dp))
        ForgotPasswordLink { viewModel.onForgotPassword() }
        BottomComponent(LoginScreenState.Login, onGoogleLoginClick = onGoogleLoginClick) {
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
        AuthTopPart(state = LoginScreenState.Register, viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        NameField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(viewModel, true)
        Spacer(modifier = Modifier.height(16.dp))
        ConfirmPasswordField(viewModel)
        BottomComponent(
            LoginScreenState.Register, onGoogleLoginClick = onGoogleLoginClick
        ) { viewModel.signUpWithEmail() }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        BottomLoginTextComponent(initialText = "Already have an account? ",
            actionText = "Login!",
            onActionTextClick = { viewModel.onLogin() })
    }
}

@Composable
fun AuthTopPart(state: LoginScreenState, viewModel: LoginViewModel) {
    HeadingText(state)
    Spacer(modifier = Modifier.height(16.dp))
    EmailField(viewModel, isRegister = state == LoginScreenState.Register)
}