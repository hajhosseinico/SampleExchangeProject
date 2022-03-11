package ir.hajhosseini.payseracurrencyexchanger.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.ExchangeRetrofitInterface
import ir.hajhosseini.payseracurrencyexchanger.repository.MainRepository
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceDao
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionDao
import ir.hajhosseini.payseracurrencyexchanger.util.InternetStatus
import javax.inject.Singleton

/**
 * MovieDetailRepositoryModule will provide all the dependencies that MovieListRepository requires
 * Retrofit interface and internet status are instantiated in RetrofitModule
 * MovieDao and MovieListCacheMapper are instantiated in RoomModule
 */

@InstallIn(SingletonComponent::class)
@Module
object DashboardRepositoryModule {
    @Singleton
    @Provides
    fun provideMovieListRepository(
        retrofitInterface: ExchangeRetrofitInterface,
        internetStatus: InternetStatus,
        transactionDao: TransactionDao,
        balanceDao: BalanceDao,
    ): MainRepository{
        return MainRepository(retrofitInterface,internetStatus,transactionDao,balanceDao)
    }
}