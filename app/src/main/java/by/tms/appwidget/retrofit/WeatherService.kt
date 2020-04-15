package by.tms.appwidget.retrofit

import by.tms.appwidget.retrofit.entity.Weather
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun loadWeather(
        @Query("q") city: String,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Deferred<Weather>
}