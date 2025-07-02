package com.example.myfirstapp.Repositories

import com.example.myfirstapp.Api.AuthApi
import com.example.myfirstapp.Interfaces.AuthRepository
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.Objects.AuthInterceptor
import com.example.myfirstapp.Objects.SharedPreferencesProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.data.DTO.LoginDTO
import com.example.myfirstapp.data.DTO.TokenDTO
import com.example.myfirstapp.data.DTO.UserDTO
import com.example.myfirstapp.data.Enums.UserRole
import com.example.myfirstapp.data.Models.User

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val stringProvider: StringProvider,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(email: String, password: String): LoginResult {
        return try {
            val loginDTO = LoginDTO(email = email, password = password)
            val response = authApi.loginUser(loginDTO)
            if (response.isSuccessful) {
                val responseBody = response.body()
                    ?: throw Exception(stringProvider.getStringResource(R.string.error_empty_response))

                val userData = responseBody["user"] as? Map<*, *>
                    ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_user_data))
                val token = responseBody["token"] as? String
                    ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_token))

                val user = parseUser(userData)
                saveUserToPreferences(user, token)
                authInterceptor.setToken(token)

                LoginResult.Success(user)
            } else {
                LoginResult.Error(stringProvider.getStringResource(R.string.error_login_failed))
            }
        } catch (e: Exception) {
            LoginResult.Error(stringProvider.getStringResource(R.string.error_login_generic))
        }
    }

    override suspend fun register(email: String, name: String, password: String, rememberMe: Boolean): RegistrationResult {
        return try {
            val userDTO = UserDTO(name = name, email = email, password = password)
            val response = authApi.registerUser(userDTO)
            if (response.isSuccessful) {
                val user = response.body()
                if (rememberMe) {
                    login(email, password)
                }
                RegistrationResult.Success(user = user)
            } else {
                RegistrationResult.Failure(stringProvider.getStringResource(R.string.error_registration_failed))
            }
        } catch (e: Exception) {
            RegistrationResult.Failure(stringProvider.getStringResource(R.string.error_registration_generic))
        }
    }

    override suspend fun isTokenValid(token: String): Boolean {
        return try {
            val response = authApi.validateToken(TokenDTO(token))
            if (response.isSuccessful) {
                response.body()?.get("valid") as? Boolean ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun saveUserToPreferences(user: User, token: String) {
        val sharedPreferences = sharedPreferencesProvider.sharedPreferences
        with(sharedPreferences.edit()) {
            putLong("user_id", user.idUser)
            putString("user_name", user.name)
            putString("user_email", user.email)
            putString("user_role", user.role.name)
            putString("auth_token", token)
            apply()
        }
    }

    override fun getUserFromPreferences(): User? {
        val sharedPreferences = sharedPreferencesProvider.sharedPreferences
        val id = sharedPreferences.getLong("user_id", -1)
        val name = sharedPreferences.getString("user_name", null)
        val email = sharedPreferences.getString("user_email", null)
        val roleString = sharedPreferences.getString("user_role", null)

        return if (id != -1L && name != null && email != null && roleString != null) {
            val role = UserRole.valueOf(roleString)
            User(id, role, name, email, "")
        } else {
            null
        }
    }

    override suspend fun loginWithGoogle(idToken: String): LoginResult = try {
        val response = authApi.loginWithGoogle(mapOf("idToken" to idToken))
        if (response.isSuccessful) {
            val body = response.body()!!
            val userData = body["user"] as Map<*, *>
            val token = body["token"] as String
            val user = parseUser(userData)
            saveUserToPreferences(user, token)
            authInterceptor.setToken(token)
            LoginResult.Success(user)
        } else {
            LoginResult.Error(stringProvider.getStringResource(R.string.error_login_failed))
        }
    } catch (e: Exception) {
        LoginResult.Error(stringProvider.getStringResource(R.string.error_login_generic))
    }

    override suspend fun registerWithGoogle(idToken: String): RegistrationResult = try {
        val response = authApi.loginWithGoogle(mapOf("idToken" to idToken))
        if (response.isSuccessful) {
            val body = response.body()!!
            val userData = body["user"] as Map<*, *>
            val token = body["token"] as String
            val user = parseUser(userData)
            saveUserToPreferences(user, token)
            authInterceptor.setToken(token)
            RegistrationResult.Success(user)
        } else {
            RegistrationResult.Failure(stringProvider.getStringResource(R.string.error_registration_failed))
        }
    } catch (e: Exception) {
        RegistrationResult.Failure(stringProvider.getStringResource(R.string.error_registration_generic))
    }

    override fun updateUserInPreferences(user: User) {
        val sharedPreferences = sharedPreferencesProvider.sharedPreferences
        with(sharedPreferences.edit()) {
            putLong("user_id", user.idUser)
            putString("user_name", user.name)
            putString("user_email", user.email)
            putString("user_role", user.role.name)
            apply()
        }
    }

    override fun getTokenFromPreferences(): String? {
        return sharedPreferencesProvider.sharedPreferences.getString("auth_token", null)
    }

    private fun parseUser(userData: Map<*, *>): User {
        val id = (userData["id"] as? Number)?.toLong()
            ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_user_id))
        val name = userData["name"] as? String
            ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_user_name))
        val email = userData["email"] as? String
            ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_user_email))
        val roleString = userData["role"] as? String
            ?: throw Exception(stringProvider.getStringResource(R.string.error_missing_user_role))
        val role = UserRole.valueOf(roleString)

        return User(id, role, name, email, "")
    }

    override fun clearToken() {
        authInterceptor.setToken(null)
        val prefs = sharedPreferencesProvider.sharedPreferences
        with(prefs.edit()) {
            remove("auth_token")
            remove("user_id")
            remove("user_name")
            remove("user_email")
            remove("user_role")
            apply()
        }
    }
}
