package com.example.myfirstapp.DI

import com.example.myfirstapp.Impl.StringProviderImpl
import com.example.myfirstapp.Interfaces.AuthRepository
import com.example.myfirstapp.Interfaces.GuestRepository
import com.example.myfirstapp.Interfaces.OrderRepository
import com.example.myfirstapp.Interfaces.PaymentRepository
import com.example.myfirstapp.Interfaces.ReservationTableRepository
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.Objects.AuthInterceptor
import com.example.myfirstapp.Objects.SharedPreferencesProvider
import com.example.myfirstapp.Repositories.AuthRepositoryImpl
import com.example.myfirstapp.Repositories.GuestRepositoryImpl
import com.example.myfirstapp.Repositories.OrderRepositoryImpl
import com.example.myfirstapp.Repositories.PaymentRepositoryImpl
import com.example.myfirstapp.Repositories.ReservationTableRepositoryImpl
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    // Интерсептор для авторизации
    single { AuthInterceptor() }

    // Сетевые компоненты
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideAuthApi(get()) }
    single { provideHomeApi(get()) }
    single { provideOrderApi(get()) }
    single { providePaymentApi(get()) }
    single { provideReserveTableApi(get()) }

    // SharedPreferences для хранения данных
    single { SharedPreferencesProvider(androidContext()) }
    single<StringProvider> { StringProviderImpl(androidContext()) }

    // Репозитории
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }
    single<GuestRepository> { GuestRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single<ReservationTableRepository> { ReservationTableRepositoryImpl(get()) }

    // ViewModel'ы
    viewModel { LoginViewModel(get()) }
    viewModel { GuestViewModel(get()) }

    // Исправление для OrderViewModel
    viewModel {
        OrderViewModel(get())
    }

    // ViewModel для платежей и бронирования
    viewModel { PaymentViewModel(get(), get()) }
    viewModel { ReservationTableViewModel(get()) }
}

