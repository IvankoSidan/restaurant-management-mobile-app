package com.example.myfirstapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Api.AuthApi
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.data.Enums.UserRole
import com.example.myfirstapp.data.Models.User
import kotlinx.coroutines.launch

class LoginViewModel(private val authApi: AuthApi) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    private val _registrationResult = MutableLiveData<RegistrationResult?>()
    val registrationResult: LiveData<RegistrationResult?> = _registrationResult

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            val response = authApi.authenticateUser (mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                val userMap = response.body() ?: throw Exception("Empty response body")
                val userData = (userMap["user"] as? Map<*, *>) ?: throw Exception("User  data is missing")

                val id = (userData["id"] as? Number)?.toLong() ?: throw Exception("User  ID is missing")
                val name = userData["name"] as? String ?: throw Exception("User  name is missing")
                val emailResponse = userData["email"] as? String ?: throw Exception("User  email is missing")
                val roleString = userData["role"] as? String ?: throw Exception("User  role is missing")
                val role = UserRole.valueOf(roleString)

                val user = User(
                    id = id,
                    role = role,
                    name = name,
                    email = emailResponse,
                    password = ""
                )
                _loginResult.postValue(LoginResult.Success(user))
            } else {
                _loginResult.postValue(LoginResult.Error(response.errorBody()?.string() ?: "Login error"))
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login error: ${e.message}")
            _loginResult.postValue(LoginResult.Error(e.message ?: "Unknown error"))
        }
    }

    fun registration(email: String, initials: String, password: String) = viewModelScope.launch {
        try {
            val user = User(0, UserRole.GUEST, initials, email, password)
            val response = authApi.registerUser (user)
            if (response.isSuccessful) {
                _registrationResult.postValue(RegistrationResult.Success)
            } else {
                _registrationResult.postValue(RegistrationResult.Failure(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Registration error: ${e.message}")
            _registrationResult.postValue(RegistrationResult.Failure(e.message ?: "Unknown error"))
        }
    }

    fun clearLoginResult() { _loginResult.value = null }
    fun clearRegistrationResult() { _registrationResult.value = null }
}
