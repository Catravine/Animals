package com.carolinecourtney.animals

import android.app.Application
import com.carolinecourtney.animals.di.PrefsModule
import com.carolinecourtney.animals.util.SharedPreferencesHelper

class PrefsModuleTest(val mockPrefs: SharedPreferencesHelper): PrefsModule() {
    override fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return mockPrefs
    }
}