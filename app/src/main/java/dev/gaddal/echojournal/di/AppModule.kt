package dev.gaddal.echojournal.di

import android.content.Context
import android.content.SharedPreferences
import dev.gaddal.echojournal.EchoJournalApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences>(named("normal")) {
        androidApplication().getSharedPreferences(
            "settings_pref",
            Context.MODE_PRIVATE
        )
    }

    single<CoroutineScope> {
        (androidApplication() as EchoJournalApp).applicationScope
    }
}
