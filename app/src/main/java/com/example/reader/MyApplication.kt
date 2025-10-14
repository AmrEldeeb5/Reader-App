package com.example.reader

import android.app.Application
import com.example.reader.data.realm.RealmDatabase
import com.example.reader.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Realm Database
        RealmDatabase.initialize()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(AppModule)
        }
    }

}