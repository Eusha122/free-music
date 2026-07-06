package com.freetune.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "freetune_prefs")
private val TOKEN_KEY = stringPreferencesKey("auth_token")
private val ANON_EMAIL_KEY = stringPreferencesKey("anon_email")
private val ANON_PASSWORD_KEY = stringPreferencesKey("anon_password")

class TokenStore(private val context: Context) {
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun currentToken(): String? = tokenFlow.first()

    suspend fun save(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    data class AnonymousCredentials(val email: String, val password: String)

    suspend fun currentAnonymousCredentials(): AnonymousCredentials? {
        val prefs = context.dataStore.data.first()
        val email = prefs[ANON_EMAIL_KEY] ?: return null
        val password = prefs[ANON_PASSWORD_KEY] ?: return null
        return AnonymousCredentials(email, password)
    }

    suspend fun saveAnonymousCredentials(email: String, password: String) {
        context.dataStore.edit {
            it[ANON_EMAIL_KEY] = email
            it[ANON_PASSWORD_KEY] = password
        }
    }
}
