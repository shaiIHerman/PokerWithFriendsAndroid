package com.shai.pokerwithfriendsandroid.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shai.pokerwithfriendsandroid.R
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState.ForgotPassword.isForgot
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState.ForgotPassword.isLogin
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState.ForgotPassword.isRegister
import com.shai.pokerwithfriendsandroid.ui.theme.BgSocial
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.viewmodels.LoginViewModel

@Composable
fun EmailField(viewModel: LoginViewModel) {
    MyTextField(
        labelVal = "E-mail",
        vector = Icons.Filled.Email,
        fieldValue = viewModel.email,
        onValueChange = viewModel::updateEmail
    )
}

@Composable
fun WelcomeText() {
    Text(
        text = "Welcome to Poker with Friends!",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 24.sp,
        color = BgSocial,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun HeadingText(screenMode: LoginScreenState) {
    when (screenMode) {
        LoginScreenState.Login -> HeadingTextComponent("Login")
        LoginScreenState.Register -> HeadingTextComponent("Sign Up")
        else -> ForgotPasswordHeadingTextComponent("Forgot Password?")
    }
}

@Composable
fun ForgotPasswordLink(onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        ForgotPasswordTextComponent(onClick = onClick)
    }
}

@Composable
fun BottomComponent(screenMode: LoginScreenState) {
    Column {
        PrimaryButton(
            labelVal = when {
                screenMode.isLogin() -> "Login"
                screenMode.isRegister() -> "Sign Up"
                else -> "Submit"
            }
        )
        if (!screenMode.isForgot()) {
            Spacer(modifier = Modifier.height(10.dp))
            DividerWithText()
            Spacer(modifier = Modifier.height(5.dp))
            GoogleLoginButton()
        }
    }
}

@Composable
fun ForgotPasswordHeadingTextComponent(action: String) {
    val parts = action.split(" ", limit = 2)
    Column {
        HeadingTextComponent(parts.first())
        HeadingTextComponent(parts.last())
    }
}

@Composable
fun BottomLoginTextComponent(
    initialText: String, actionText: String, onActionTextClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Tertirary)) {
            append(initialText)
        }
        withStyle(style = SpanStyle(color = BrandColor, fontWeight = FontWeight.Bold)) {
            pushStringAnnotation(tag = actionText, annotation = actionText)
            append(actionText)
        }
    }

    ClickableText(modifier = Modifier
        .padding(bottom = 24.dp)
        .fillMaxWidth()
        .wrapContentWidth(),
        text = annotatedString,
        onClick = {
            annotatedString.getStringAnnotations(it, it).firstOrNull()?.also { span ->
                Log.d("BottomLoginTextComponent", "${span.item} is Clicked")
                onActionTextClick()
            }
        })
}

@Composable
fun GoogleLoginButton() {
    Button(
        onClick = { /*TODO: Implement Google login functionality*/ },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BgSocial
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google icon"
            )

            Text(
                text = "Login With Google",
                fontSize = 18.sp,
                color = Tertirary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}