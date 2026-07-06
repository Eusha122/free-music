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

class TokenStore(private val context: Context) {
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun currentToken(): String? = tokenFlow.first()

    suspend fun save(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
