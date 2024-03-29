package com.arysugiarto.konsulin.data.di

import android.content.Context
import com.arysugiarto.konsulin.BuildConfig
import com.arysugiarto.konsulin.data.preferences.AccessManager
import com.arysugiarto.konsulin.ui.main.MainActivity.Companion.LOGIN_ATTEMPT_KEY
import com.arysugiarto.konsulin.util.Const.NETWORK.EQUALS
import com.arysugiarto.konsulin.util.Const.NETWORK.SESSION_ID
import com.arysugiarto.konsulin.util.orEmpty
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [
    ApiModule::class,
    ApplicationModule::class
])
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val DOKUTEST_ACCESS_KEY = "dokutest"

    @Provides
    @Named(DOKUTEST_ACCESS_KEY)
    fun provideBaseURL() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClientBuilder(okHttpClient: OkHttpClient) =
        okHttpClient.newBuilder()

    @Provides
    @Singleton
    fun provideCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB Cache
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkHttpCallback(
        accessManager: AccessManager,
        interceptor: HttpLoggingInterceptor,
        cache: Cache
    ) = OkHttpClient.Builder()
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor{
            val authorisedRequest : Request = it.request().newBuilder()
                .addHeader(BuildConfig.ACCEPT_KEY,BuildConfig.ACCEPT_VALUE)
                .addHeader(BuildConfig.APPLICATION_KEY_NAME,BuildConfig.APPLICATION_KEY_VALUE_NAME)
                .apply {
                    runBlocking {
                        val sessionId = accessManager.sessionId.firstOrNull()
                        val bearerToken = accessManager.access.firstOrNull()

                        if (bearerToken.orEmpty.isEmpty()
                            && sessionId != LOGIN_ATTEMPT_KEY
                            && sessionId.orEmpty.isNotEmpty()
                        ) {
                            addHeader(
                                name = BuildConfig.COOKIE_KEY,
                                value = "$SESSION_ID$EQUALS$sessionId"
                            )
                        }
                    }
                }.build()

            it.proceed(authorisedRequest)
        }
        .cache(cache)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @Named(DOKUTEST_ACCESS_KEY) baseURL: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

}