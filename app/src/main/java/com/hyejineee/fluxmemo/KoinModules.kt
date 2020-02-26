package com.hyejineee.fluxmemo

import androidx.room.Room
import com.hyejineee.fluxmemo.datasources.MemoDataSource
import com.hyejineee.fluxmemo.datasources.MemoLocalDataSource
import com.hyejineee.fluxmemo.room.MemoAppDatabase
import com.hyejineee.fluxmemo.viewmodels.MemoViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.inMemoryDatabaseBuilder(
            androidApplication(),
            MemoAppDatabase::class.java
        ).build()
    }
}

val dataSourceModule = module {
    single<MemoDataSource> {
        MemoLocalDataSource(get())
    }
}

val memoViewModelModule = module {
    single {
        MemoViewModel(get())
    }
}

val koinModules = listOf(
    dbModule,
    dataSourceModule,
    memoViewModelModule
)
