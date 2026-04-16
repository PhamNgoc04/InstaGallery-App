package com.codewithngoc.instagallery.di

import android.content.Context
import com.codewithngoc.instagallery.data.InstaGalleryApi
import com.codewithngoc.instagallery.data.InstaGallerySession
import com.codewithngoc.instagallery.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import androidx.room.Room

// ─── Base URL ─────────────────────────────────────────────────────────────────
// Máy ảo Android Emulator → 10.0.2.2 map sang localhost máy host
// Máy thật + ADB reverse → 127.0.0.1
// Wifi cùng mạng → thay bằng IP máy tính (vd: 192.168.1.x)

// Dùng trên máy thật (Trước khi chạy thì phải run đoạn này trên terminal:
// D:\Android\SDK\platform-tools\adb.exe devices
// D:\Android\SDK\platform-tools\adb.exe -s R58N85Y4BMZ reverse tcp:8080 tcp:8080

// Lệnh cho Máy Ảo:
// D:\Android\SDK\platform-tools\adb.exe -s emulator-5554 reverse tcp:8080 tcp:8080
private const val BASE_URL = "http://127.0.0.1:8080/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── HTTP Client ────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideClient(
        session: InstaGallerySession,
        @ApplicationContext context: Context,
        authenticator: com.codewithngoc.instagallery.data.remote.TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authenticator)
            .addInterceptor { chain ->
                val token = session.getToken()
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("X-Package-Name", context.packageName)
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // ── Retrofit & API ─────────────────────────────────────────

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInstaGalleryApi(retrofit: Retrofit): InstaGalleryApi {
        return retrofit.create(InstaGalleryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSession(@ApplicationContext context: Context): InstaGallerySession {
        return InstaGallerySession(context)
    }

    // ── Repositories ───────────────────────────────────────────

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: InstaGalleryApi,
        session: InstaGallerySession
    ): AuthRepository = AuthRepository(api, session)

    @Provides
    @Singleton
    fun providePostRepository(api: InstaGalleryApi): PostRepository =
        PostRepository(api)

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: InstaGalleryApi,
        session: InstaGallerySession
    ): ProfileRepository = ProfileRepository(api, session)

    // NotificationRepository dùng @Inject @Singleton — Hilt tự inject, không cần @Provides ở đây

    @Provides
    @Singleton
    fun provideSocialRepository(api: InstaGalleryApi): SocialRepository =
        SocialRepository(api)

    @Provides
    @Singleton
    fun provideSearchRepository(api: InstaGalleryApi): SearchRepository =
        SearchRepository(api)

    @Provides
    @Singleton
    fun provideAlbumRepository(api: InstaGalleryApi): AlbumRepository =
        AlbumRepository(api)

    @Provides
    @Singleton
    fun providePortfolioRepository(api: InstaGalleryApi): PortfolioRepository =
        PortfolioRepository(api)

    @Provides
    @Singleton
    fun provideBookingRepository(api: InstaGalleryApi): BookingRepository =
        BookingRepository(api)

    @Provides
    @Singleton
    fun provideRatingRepository(api: InstaGalleryApi): RatingRepository =
        RatingRepository(api)

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): com.codewithngoc.instagallery.data.local.InstaGalleryDatabase {
        return Room.databaseBuilder(
            context,
            com.codewithngoc.instagallery.data.local.InstaGalleryDatabase::class.java,
            "instagallery_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: com.codewithngoc.instagallery.data.local.InstaGalleryDatabase): com.codewithngoc.instagallery.data.local.dao.ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideChatRepository(api: InstaGalleryApi, chatDao: com.codewithngoc.instagallery.data.local.dao.ChatDao): ChatRepository =
        ChatRepository(api, chatDao)

    @Provides
    @Singleton
    fun provideAdminRepository(api: InstaGalleryApi): AdminRepository =
        AdminRepository(api)

    @Provides
    @Singleton
    fun provideChatSocketService(
        client: OkHttpClient
    ): com.codewithngoc.instagallery.data.remote.ChatSocketService {
        return com.codewithngoc.instagallery.data.remote.ChatSocketService(client, BASE_URL)
    }
}