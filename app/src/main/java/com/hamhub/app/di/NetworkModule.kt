package com.hamhub.app.di

import com.hamhub.app.data.remote.api.CallookApi
import com.hamhub.app.data.remote.api.ContestsApi
import com.hamhub.app.data.remote.api.IssApi
import com.hamhub.app.data.remote.api.NewsApi
import com.hamhub.app.data.remote.api.PropagationApi
import com.hamhub.app.data.remote.api.RepeaterBookApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("propagation")
    fun providePropagationRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.hamqsl.com/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePropagationApi(@Named("propagation") retrofit: Retrofit): PropagationApi {
        return retrofit.create(PropagationApi::class.java)
    }

    @Provides
    @Singleton
    @Named("n2yo")
    fun provideN2yoRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.n2yo.com/rest/v1/satellite/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideIssApi(@Named("n2yo") retrofit: Retrofit): IssApi {
        return retrofit.create(IssApi::class.java)
    }

    @Provides
    @Singleton
    @Named("callook")
    fun provideCallookRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://callook.info/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideCallookApi(@Named("callook") retrofit: Retrofit): CallookApi {
        return retrofit.create(CallookApi::class.java)
    }

    @Provides
    @Singleton
    @Named("repeaterbook")
    fun provideRepeaterBookRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.repeaterbook.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideRepeaterBookApi(@Named("repeaterbook") retrofit: Retrofit): RepeaterBookApi {
        return retrofit.create(RepeaterBookApi::class.java)
    }

    @Provides
    @Singleton
    @Named("contestcalendar")
    fun provideContestCalendarRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.contestcalendar.com/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideContestsApi(@Named("contestcalendar") retrofit: Retrofit): ContestsApi {
        return retrofit.create(ContestsApi::class.java)
    }

    @Provides
    @Singleton
    @Named("arrl")
    fun provideArrlRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.arrl.org/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(@Named("arrl") retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }
}
