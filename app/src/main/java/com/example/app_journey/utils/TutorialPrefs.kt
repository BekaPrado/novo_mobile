package com.example.app_journey.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("tutorial_prefs")

object TutorialPrefs {
    private val KEY_SHOWN = booleanPreferencesKey("tutorial_shown")

    fun wasTutorialShown(context: Context): Boolean = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[KEY_SHOWN] ?: false
    }

    fun saveTutorialShown(context: Context) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOWN] = true
        }
    }

    fun resetTutorial(context: Context) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
