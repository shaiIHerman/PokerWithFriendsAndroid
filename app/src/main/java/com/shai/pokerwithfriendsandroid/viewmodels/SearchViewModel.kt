package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val searchTextFieldState = TextFieldState()
}