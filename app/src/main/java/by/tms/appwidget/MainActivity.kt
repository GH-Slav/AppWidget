package by.tms.appwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
//            val result = provideAPI().loadWeather("Minsk", "ru", "metric", "531d3303c19b26dd0922093f7a17723c").await()
//            withContext(Dispatchers.Main) {
//                weatherResult.text = result.toString()
//            }
        }
    }
}
