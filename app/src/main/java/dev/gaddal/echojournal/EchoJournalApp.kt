package dev.gaddal.echojournal

import android.app.Application
import dev.gaddal.echojournal.core.data.di.coreDataModule
import dev.gaddal.echojournal.core.database.di.databaseModule
import dev.gaddal.echojournal.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class EchoJournalApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@EchoJournalApp)
            modules(
                appModule,
                coreDataModule,
                databaseModule,
            )
        }
    }
}