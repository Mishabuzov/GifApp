package ru.home.gifapp.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private const val GIF_SERVICE_ENDPOINT = "https://developerslife.ru"
    private const val JSON_PARAM_NAME = "json"
    private const val JSON_PARAM_VALUE = true

    val gifService: GifService by lazy { buildRetrofit().create(GifService::class.java) }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private val requestInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val url: HttpUrl = chain.request().url.newBuilder()
                .addQueryParameter(JSON_PARAM_NAME, JSON_PARAM_VALUE.toString())
                .build()

            chain.proceed(
                chain.request().newBuilder().url(url).build()
            )
        }
    }

    private fun buildRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(GIF_SERVICE_ENDPOINT)
        .client(buildClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) //rx wrapper
        .build()

    private fun buildClient() = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor)
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

}
