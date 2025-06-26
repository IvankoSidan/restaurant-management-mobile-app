package com.example.myfirstapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Interfaces.AuthRepository
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.data.Models.User
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>(LoginResult.Idle)
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registrationResult = MutableLiveData<RegistrationResult>(RegistrationResult.Idle)
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginResult.postValue(LoginResult.Loading)
        val result = authRepository.login(email, password)
        _loginResult.postValue(result)
    }

    fun registration(email: String, name: String, password: String, rememberMe: Boolean) = viewModelScope.launch {
        _registrationResult.postValue(RegistrationResult.Loading)
        val result = authRepository.register(email, name, password, rememberMe)
        _registrationResult.postValue(result)
    }

    fun clearLoginResult() {
        _loginResult.value = LoginResult.Idle
    }

    fun clearRegistrationResult() {
        _registrationResult.value = RegistrationResult.Idle
    }

    fun isTokenValid(token: String): LiveData<Boolean> = liveData {
        emit(authRepository.isTokenValid(token))
    }

    fun getUserFromPreferences(): User? {
        return authRepository.getUserFromPreferences()
    }

    fun updateUserInPreferences(user: User) {
        authRepository.updateUserInPreferences(user)
    }

    fun getTokenFromPreferences(): String? {
        return authRepository.getTokenFromPreferences()
    }

    fun clearToken() {
        authRepository.clearToken()
    }
}
