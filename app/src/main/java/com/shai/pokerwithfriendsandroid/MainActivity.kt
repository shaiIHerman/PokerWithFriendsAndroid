package com.shai.pokerwithfriendsandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.screens.LoginScreen
import com.shai.pokerwithfriendsandroid.ui.theme.PokerWithFriendsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firestoreClient: FireStoreClient

    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService.signOut()
        val currentUser = authService.getCurrentUser()

        enableEdgeToEdge()
        GlobalScope.launch {
            firestoreClient.getUsers().onSuccess { Log.d("TAG", "${it.size}") }
                .onFailure { Log.e("TAG", "$it") }
        }
        setContent {
            PokerWithFriendsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (currentUser != null) {

                        Greeting(
                            name = "Android", modifier = Modifier.padding(innerPadding)
                        )
                    }else{
                        LoginScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokerWithFriendsTheme {
        Greeting("Android")
    }
}