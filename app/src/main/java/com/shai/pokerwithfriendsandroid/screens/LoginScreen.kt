package com.shai.pokerwithfriendsandroid.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.BottomComponent
import com.shai.pokerwithfriendsandroid.components.BottomLoginTextComponent
import com.shai.pokerwithfriendsandroid.components.EmailField
import com.shai.pokerwithfriendsandroid.components.ForgotPasswordLink
import com.shai.pokerwithfriendsandroid.components.HeadingText
import com.shai.pokerwithfriendsandroid.components.MyTextField
import com.shai.pokerwithfriendsandroid.components.PasswordInputComponent
import com.shai.pokerwithfriendsandroid.components.WelcomeText
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import com.shai.pokerwithfriendsandroid.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val screenMode by viewModel.screenMode.collectAsState()
    val screenContentMap: Map<LoginScreenState, @Composable (LoginViewModel) -> Unit> = mapOf(
        LoginScreenState.Login to { vm -> LoginView(vm) },
        LoginScreenState.Register to { vm -> RegisterView(vm) },
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
fun ForgotPasswordView(viewModel: LoginViewModel) {
    Column {
        HeadingText(LoginScreenState.ForgotPassword)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        BottomComponent(LoginScreenState.ForgotPassword)
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun LoginView(viewModel: LoginViewModel) {
    Column {
        HeadingText(LoginScreenState.Login)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordInputComponent(
            placeholder = "Password",
            fieldValue = viewModel.password,
            onValueChange = viewModel::updatePassword
        )
        Spacer(modifier = Modifier.height(8.dp))
        ForgotPasswordLink { viewModel.onForgotPassword() }
        BottomComponent(LoginScreenState.Login)
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        BottomLoginTextComponent(initialText = "Don't have an account? ",
            actionText = "Register Now!",
            onActionTextClick = { viewModel.onRegister() })
    }
}

@Composable
fun RegisterView(viewModel: LoginViewModel) {
    Column {
        HeadingText(LoginScreenState.Register)
        Spacer(modifier = Modifier.height(16.dp))
        EmailField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        MyTextField(
            labelVal = "Name",
            vector = Icons.Filled.Face,
            icon = null,
            fieldValue = viewModel.name,
            onValueChange = viewModel::updateName
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordInputComponent(
            placeholder = "Password",
            fieldValue = viewModel.password,
            onValueChange = viewModel::updatePassword
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordInputComponent(
            placeholder = "Confirm Password",
            fieldValue = viewModel.confirmPassword,
            onValueChange = viewModel::updateConfirmPassword
        )
        BottomComponent(LoginScreenState.Register)
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        BottomLoginTextComponent(initialText = "Already have an account? ",
            actionText = "Login!",
            onActionTextClick = { viewModel.onRegister() })
    }
}