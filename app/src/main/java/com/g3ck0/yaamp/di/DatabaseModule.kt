package com.g3ck0.yaamp.di

import android.content.Context
import androidx.room.Room
import com.g3ck0.yaamp.data.local.PlaylistDao
import com.g3ck0.yaamp.data.local.SongDao
import com.g3ck0.yaamp.data.local.YaampDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideYaampDatabase(
        @ApplicationContext context: Context
    ): YaampDatabase {
        return Room.databaseBuilder(
            context,
            YaampDatabase::class.java,
            YaampDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideSongDao(database: YaampDatabase): SongDao {
        return database.songDao()
    }
    
    @Provides
    fun providePlaylistDao(database: YaampDatabase): PlaylistDao {
        return database.playlistDao()
    }
}
