package com.forreview.di

import android.content.Context
import androidx.room.Room
import com.forreview.DATABASE_NAME
import com.forreview.PREFS_NAME
import com.forreview.database.DataInitializer
import com.forreview.database.MyDatabase
import com.forreview.database.mapper.AudioMapper
import com.forreview.database.mapper.ExplanationMapper
import com.forreview.database.mapper.MeditationMapper
import com.forreview.database.mapper.StageMapper
import com.forreview.datamanager.*
import com.forreview.helper.*
import com.forreview.datamanager.*
import com.forreview.helper.*
import com.forreview.repository.MeditationRepository
import com.forreview.repository.MeditationRepositoryImpl
import com.forreview.repository.StageRepository
import com.forreview.repository.StageRepositoryImpl
import com.forreview.ui.MainViewModel
import com.forreview.ui.MainViewModelImpl
import com.forreview.ui.main.stages.StagesViewModel
import com.forreview.ui.main.stages.StagesViewModelImpl
import com.forreview.ui.meditation.main.MeditationMainViewModel
import com.forreview.ui.meditation.main.MeditationMainViewModelImpl
import com.forreview.ui.menu.help.HelpViewModel
import com.forreview.ui.menu.help.HelpViewModelImpl
import com.forreview.ui.menu.main.MenuViewModel
import com.forreview.ui.menu.main.MenuViewModelImpl
import com.forreview.ui.menu.more_meditations.MoreMeditationsViewModel
import com.forreview.ui.menu.reminders.RemindersViewModel
import com.forreview.ui.menu.reminders.RemindersViewModelImpl
import com.forreview.ui.menu.skip_stage.SkipToStageViewModel
import com.forreview.ui.menu.skip_stage.SkipToStageViewModelImpl
import com.forreview.ui.menu.statistics.StatisticsViewModel
import com.forreview.ui.menu.statistics.StatisticsViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

private val appModule = module {
    single { AppSchedulerProvider() as SchedulerProvider }
    single { AppCoroutineExecutor(get()) as CoroutineExecutor }
//    single {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//            .client(
//                OkHttpClient.Builder()
//                    .addInterceptor(
//                        LoggingInterceptor.Builder()
//                            .loggable(BuildConfig.DEBUG)
//                            .setLevel(Level.BASIC)
//                            .log(Platform.INFO)
//                            .tag("MyRequests")
//                            .build())
//                    .build())
//            .build()
//    }

    single { get<Context>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    single {
        Room.databaseBuilder(get(), MyDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { PurchaseController(androidContext(), get()) }
}

private val mapperModule = module {
    single { MeditationMapper(get(), get(), get()) }
    single { StageMapper() }
    single { AudioMapper() }
    single { ExplanationMapper() }
}

private val dbModule = module {
    single { get<MyDatabase>().meditationDao() }
    single { get<MyDatabase>().stageDao() }
}

private val viewModelModule = module {
    viewModel { MainViewModelImpl(get(), get()) as MainViewModel }
    viewModel { StagesViewModelImpl(
        get(),
        get()
    ) as StagesViewModel
    }
    viewModel { MenuViewModelImpl(get()) as MenuViewModel }
    viewModel { StatisticsViewModelImpl(
        get(),
        get()
    ) as StatisticsViewModel
    }
    viewModel { RemindersViewModelImpl() as RemindersViewModel }
    viewModel { HelpViewModelImpl() as HelpViewModel }
    viewModel { SkipToStageViewModelImpl(
        get(),
        get()
    ) as SkipToStageViewModel
    }
    viewModel {
        MeditationMainViewModelImpl(
            get(),
            get()
        ) as MeditationMainViewModel
    }

    viewModel { ExpansionViewModelImpl(
        get(),
        get(),
        get()
    ) as ExpansionViewModel
    }

    viewModel { MoreMeditationsViewModel(get(), get(), get(), get()) }
}

private val dataManagerModule = module {
    single { DataManagerImpl(
        get(),
        get(),
        get(),
        get(),
        get(),
        get()
    ) as DataManager
    }
    single { MeditationNavigationHelperImpl() as MeditationNavigationHelper }
}

private val repoModule = module {
    single { MeditationRepositoryImpl(
        get(),
        get(),
        get(),
        get()
    ) as MeditationRepository
    }
    single { StageRepositoryImpl(get(), get()) as StageRepository }
}

private val helperModule = module {
    single { PrefsHelperImpl(get()) as PrefsHelper }
    single { DataInitializer(get()) }
    single { MyAlarmManager(get()) }
    single { StatResources(get()) }
    single { MyNotificationManager(get()) }
}

val moduleList =
    mutableListOf(
        appModule,
        mapperModule,
        dbModule,
        dataManagerModule,
        repoModule,
        viewModelModule,
        helperModule
    )