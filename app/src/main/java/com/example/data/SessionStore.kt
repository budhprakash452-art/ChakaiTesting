package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "chakaigo_session")

data class UserSession(
    val isLoggedIn: Boolean = false,
    val mobileNumber: String = "",
    val activeRole: UserRole? = null,
    val customerName: String = ""
)

class SessionStore(private val context: Context) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val MOBILE_NUMBER = stringPreferencesKey("mobile_number")
        val ACTIVE_ROLE = stringPreferencesKey("active_role")
        val CUSTOMER_NAME = stringPreferencesKey("customer_name")
        val DEMO_SEEDED = booleanPreferencesKey("demo_seeded")
    }

    val sessionFlow: Flow<UserSession> = context.sessionDataStore.data.map { prefs ->
        val roleStr = prefs[Keys.ACTIVE_ROLE]
        val role = roleStr?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
        UserSession(
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            mobileNumber = prefs[Keys.MOBILE_NUMBER] ?: "",
            activeRole = role,
            customerName = prefs[Keys.CUSTOMER_NAME] ?: ""
        )
    }

    suspend fun saveLogin(mobile: String, role: UserRole? = null, customerName: String = "") {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = true
            prefs[Keys.MOBILE_NUMBER] = mobile
            if (role != null) {
                prefs[Keys.ACTIVE_ROLE] = role.name
            } else {
                prefs.remove(Keys.ACTIVE_ROLE)
            }
            if (customerName.isNotBlank()) {
                prefs[Keys.CUSTOMER_NAME] = customerName
            }
        }
    }

    suspend fun setActiveRole(role: UserRole?) {
        context.sessionDataStore.edit { prefs ->
            if (role != null) {
                prefs[Keys.ACTIVE_ROLE] = role.name
            } else {
                prefs.remove(Keys.ACTIVE_ROLE)
            }
        }
    }

    suspend fun updateCustomerName(name: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.CUSTOMER_NAME] = name
        }
    }

    suspend fun logout() {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
            prefs.remove(Keys.ACTIVE_ROLE)
        }
    }

    suspend fun markDemoSeeded() {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.DEMO_SEEDED] = true
        }
    }
}
