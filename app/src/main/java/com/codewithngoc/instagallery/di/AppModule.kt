package com.codewithngoc.instagallery.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import androidx.core.os.BuildCompat
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Dùng trên máy thật: chạy trước lệnh ADB: D:\Android\SDK\platform-tools\adb.exe reverse tcp:8080 tcp:8080
// or D:\Android\SDK\platform-tools\adb.exe -d reverse tcp:8080 tcp:8080
private const val BASE_URL = "http://127.0.0.1:8080/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideClient(session: InstaGallerySession, @ApplicationContext context: Context): OkHttpClient {
        val client = OkHttpClient.Builder()
//        client.addInterceptor { chain ->
//            val request = chain.request().newBuilder()
//                .addHeader("Authorization", "Bearer ${session.getToken()}")
//                .addHeader("X-Package-Name", context.packageName)
//                .build()
//            chain.proceed(request)
//        }
        client.addInterceptor { chain ->
            val token = session.getToken()
            val requestBuilder = chain.request().newBuilder()
                .addHeader("X-Package-Name", context.packageName)

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        return client.build()
    }


    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideInstaGallery(retrofit: Retrofit): InstaGalleryApi {
        return retrofit.create(InstaGalleryApi::class.java)
    }

    @Provides
    fun provideSession(@ApplicationContext context: Context): InstaGallerySession {
        return InstaGallerySession(context)
    }

}