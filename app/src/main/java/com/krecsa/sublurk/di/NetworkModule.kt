package com.krecsa.sublurk.di

import com.krecsa.sublurk.data.network.CrtShApi
import com.krecsa.sublurk.data.network.GoogleDnsApi
import com.krecsa.sublurk.data.network.IpInfoApi
import com.krecsa.sublurk.data.network.RdapApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Named("crtsh")
    fun provideCrtShRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://crt.sh/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    @Named("googledns")
    fun provideGoogleDnsRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://dns.google/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    @Named("ipinfo")
    fun provideIpInfoRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://ipinfo.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    @Named("rdap")
    fun provideRdapRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://rdap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    fun provideCrtShApi(@Named("crtsh") retrofit: Retrofit): CrtShApi =
        retrofit.create(CrtShApi::class.java)

    @Provides
    @Singleton
    fun provideGoogleDnsApi(@Named("googledns") retrofit: Retrofit): GoogleDnsApi =
        retrofit.create(GoogleDnsApi::class.java)

    @Provides
    @Singleton
    fun provideIpInfoApi(@Named("ipinfo") retrofit: Retrofit): IpInfoApi =
        retrofit.create(IpInfoApi::class.java)

    @Provides
    @Singleton
    fun provideRdapApi(@Named("rdap") retrofit: Retrofit): RdapApi =
        retrofit.create(RdapApi::class.java)
}