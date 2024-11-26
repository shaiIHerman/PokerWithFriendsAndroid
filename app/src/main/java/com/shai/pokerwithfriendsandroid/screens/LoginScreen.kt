package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.BottomComponent
import com.shai.pokerwithfriendsandroid.components.BottomLoginTextComponent
import com.shai.pokerwithfriendsandroid.components.ForgotPasswordTextComponent
import com.shai.pokerwithfriendsandroid.components.HeadingTextComponent
import com.shai.pokerwithfriendsandroid.components.MyTextField
import com.shai.pokerwithfriendsandroid.components.PasswordInputComponent
import com.shai.pokerwithfriendsandroid.ui.theme.BgSocial
import com.shai.pokerwithfriendsandroid.utils.LoginScreenModes
import com.shai.pokerwithfriendsandroid.utils.LoginScreenModes.Companion.isForgetPassword
import com.shai.pokerwithfriendsandroid.utils.LoginScreenModes.Companion.isLogin
import com.shai.pokerwithfriendsandroid.utils.LoginScreenModes.Companion.isRegister
import com.shai.pokerwithfriendsandroid.viewmodels.LoginViewModel


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {

    val user = viewModel.currentUser
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var screenMode by remember { mutableStateOf(LoginScreenModes.LOGIN) }
//    var googleSignInClient = viewModel.authService.getGoogleSignInClient()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Welcome to Poker with Friends!",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 24.sp,
            color = BgSocial,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            HeadingTextComponent(heading = if (screenMode.isLogin()) "Login" else if (screenMode.isRegister()) "Sign Up" else " Forgot Password")
            Spacer(modifier = Modifier.height(16.dp))
            MyTextField(
                labelVal = "E-mail", vector = Icons.Filled.Email, icon = null
            )
            if (screenMode.isRegister()) {
                Spacer(modifier = Modifier.height(16.dp))
                MyTextField(
                    labelVal = "Name", vector = Icons.Filled.Face, icon = null
                )
            }
            if (!screenMode.isForgetPassword()) {
                Spacer(modifier = Modifier.height(16.dp))
                PasswordInputComponent(labelVal = "Password")
                if (screenMode.isLogin()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                    ) {
                        ForgotPasswordTextComponent {
                            screenMode = LoginScreenModes.FORGOT_PASSWORD
                        }
                    }
                }
                if (screenMode.isRegister()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordInputComponent(labelVal = "Confirm Password")
                }
            }
        }
        BottomComponent(screenMode)
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.weight(1f))
        if (!screenMode.isForgetPassword()) {
            BottomLoginTextComponent(
                initialText = "${if (!screenMode.isRegister()) "Don't" else "Already"} have an account? ",
                action = if (!screenMode.isRegister()) "Register Now!" else "Sign In!",
                onRegisterClick = { screenMode = LoginScreenModes.changeMode(screenMode) },
            )
        }
    }
}
////        ImageComponent(image = R.drawable.sweet_franky)
//        Spacer(modifier = Modifier.height(10.dp))
//        HeadingTextComponent(heading = "Login")
//        Spacer(modifier = Modifier.height(20.dp))
//        Column (verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally(){
//            MyTextField(
//                labelVal = "E-mail", vector = Icons.Filled.Email, icon = null
//            )
//            Spacer(modifier = Modifier.height(15.dp))
//            PasswordInputComponent(labelVal = "Password")
//            Spacer(modifier = Modifier.height(15.dp))
//            Row(
//                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
//            ) {
////                ForgotPasswordTextComponent(navController)
//            }
//            Box(
//                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart
//            ) {
//                Column {
////                    BottomComponent(navController)
//                    Spacer(modifier = Modifier.height(12.dp))
//                    BottomLoginTextComponent(
//                        initialText = "Haven't we seen you around here before? ",
//                        action = "Join our coven!",
//                        null
////                        {null}
//                    )
//                }
//            }
//        }
//    }
//}
