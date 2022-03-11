package ir.hajhosseini.payseracurrencyexchanger.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.hajhosseini.payseracurrencyexchanger.room.ExchangeDatabase
import ir.hajhosseini.payseracurrencyexchanger.room.balance.BalanceDao
import ir.hajhosseini.payseracurrencyexchanger.room.transaction.TransactionDao
import javax.inject.Singleton

/**
 * Provides room dependencies
 * @Singleton is used because we had only 1 scope. singleton scope is = application lifecycle scope
 * If it was a full application, i would provide dependencies into custom scopes (or activity scope)
 */

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideMovieDb(@ApplicationContext context: Context): ExchangeDatabase {
        return Room.databaseBuilder(
            context, ExchangeDatabase::class.java,
            ExchangeDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieDAO(movieDatabase: ExchangeDatabase): BalanceDao {
        return movieDatabase.balanceDao()
    }

    @Singleton
    @Provides
    fun provideMovieDetailDAO(movieDatabase: ExchangeDatabase): TransactionDao {
        return movieDatabase.transactionDao()
    }
}