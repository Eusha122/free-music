package com.freetune.app.data.repository

import com.freetune.app.data.TokenStore
import com.freetune.app.data.api.ApiService
import com.freetune.app.data.model.AuthRequest
import java.util.UUID

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

    /**
     * There's no login screen - every install gets a silent, on-device
     * anonymous account so likes/playlists/recommendations still have a
     * userId to attach to. Reuses the same generated credentials to log
     * back in if the stored token has expired.
     */
    suspend fun ensureSession() {
        if (tokenStore.currentToken() != null) return

        val existing = tokenStore.currentAnonymousCredentials()
        if (existing != null) {
            try {
                login(existing.email, existing.password)
                return
            } catch (_: Exception) {
                // Fall through and provision a fresh anonymous account.
            }
        }

        val email = "guest-${UUID.randomUUID()}@freetune.local"
        val password = UUID.randomUUID().toString()
        register(email, password)
        tokenStore.saveAnonymousCredentials(email, password)
    }
}
