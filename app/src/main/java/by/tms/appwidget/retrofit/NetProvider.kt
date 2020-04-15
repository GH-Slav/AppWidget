package by.tms.appwidget.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object NetProvider {
    private const val base_url = "https://api.openweathermap.org/data/2.5/"

    fun provideAPI(): WeatherService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(base_url)
            .build()

        return retrofit.create()
    }
}