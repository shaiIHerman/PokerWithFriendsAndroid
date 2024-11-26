package com.shai.pokerwithfriendsandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.ui.theme.PokerWithFriendsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firestoreClient: FireStoreClient

    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        authService.signOut()
//        val currentUser = authService.getCurrentUser()
//
//        GlobalScope.launch {
//            firestoreClient.getUsers().onSuccess { Log.d("TAG", "${it.size}") }
//                .onFailure { Log.e("TAG", "$it") }
//        }
        setContent {
            val navController = rememberNavController()

            PokerWithFriendsTheme {
                Scaffold { innerPadding ->
                    NavigationHost(
                        navController = navController, innerPadding = innerPadding
                    )
                }
            }
        }
    }
}