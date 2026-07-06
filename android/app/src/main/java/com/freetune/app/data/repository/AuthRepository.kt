package com.freetune.app.data.repository

import com.freetune.app.data.TokenStore
import com.freetune.app.data.api.ApiService
import com.freetune.app.data.model.AuthRequest

class AuthRepository(private val api: ApiService, private val tokenStore: TokenStore) {
    suspend fun register(email: String, password: String) {
        val response = api.register(AuthRequest(email, password))
        tokenStore.save(response.token)
    }

    suspend fun login(email: String, password: String) {
        val response = api.login(AuthRequest(email, password))
        tokenStore.save(response.token)
    }

    suspend fun logout() = tokenStore.clear()
}
