package com.example.reader.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RealmDatabase {
    private var realm: Realm? = null

    fun initialize() {
        if (realm == null) {
            val config = RealmConfiguration.Builder(
                schema = setOf(
                    FeedbackRealm::class,
                    FavoriteBookRealm::class,
                    UserProfileRealm::class
                )
            )
                .name("reader_app.realm")
                .schemaVersion(1)
                .build()

            realm = Realm.open(config)
        }
    }

    fun getInstance(): Realm {
        if (realm == null) {
            initialize()
        }
        return realm!!
    }

    fun close() {
        realm?.close()
        realm = null
    }
}

