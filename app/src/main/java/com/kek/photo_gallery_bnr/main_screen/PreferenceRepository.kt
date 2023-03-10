package com.kek.photo_gallery_bnr.main_screen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


class PreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery: Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    val isPoling: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_POLING] ?: false
    }.distinctUntilChanged()

    suspend fun setPoling(isPoling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLING] = isPoling
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private var INSTANCE: PreferenceRepository? = null
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")
        private val PREF_IS_POLING = booleanPreferencesKey("isPoling")

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }

                INSTANCE = PreferenceRepository(dataStore)
            }
        }

        fun get(): PreferenceRepository {
            return INSTANCE ?: throw IllegalStateException("Preference repository must be initialized")
            }
        }
}
