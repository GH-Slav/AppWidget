package by.tms.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import by.tms.appwidget.retrofit.NetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
const val APP_WIDGET_ID = "APP_WIDGET_ID"

class AppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val ctx = context?:return
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetId = intent?.let { it.getIntExtra(APP_WIDGET_ID, -1) }
        appWidgetId?.let { updateWeather(context, appWidgetManager, it) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
    views.setTextViewText(R.id.autoCompleteEditText, widgetText)

    val intent = Intent(context, AppWidget::class.java)
    intent.putExtra(APP_WIDGET_ID,appWidgetId)

    val pendingIntent =
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    views.setOnClickPendingIntent(R.id.updateButton, pendingIntent)

    CoroutineScope(Dispatchers.IO).launch {
        if (widgetText.isNotEmpty()) {
            val result = NetProvider.provideAPI()
                .loadWeather(widgetText, "ru", "metric", "531d3303c19b26dd0922093f7a17723c").await()
            views.setTextViewText(R.id.temperatureTextView, result.main.temp.toString())
            views.setTextViewText(R.id.weatherStateTextView, result.weather[0].description)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun updateWeather(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.app_widget)
    views.setViewVisibility(R.id.temperatureTextView, View.INVISIBLE)
    views.setViewVisibility(R.id.weatherStateTextView, View.INVISIBLE)
    views.setViewVisibility(R.id.widgetProgressBar, View.VISIBLE)

    appWidgetManager.updateAppWidget(appWidgetId, views)

    val widgetText = loadTitlePref(context, appWidgetId)
    CoroutineScope(Dispatchers.IO).launch {
        if (widgetText.isNotEmpty()) {
            val result = NetProvider.provideAPI()
                .loadWeather(widgetText, "ru", "metric", "531d3303c19b26dd0922093f7a17723c").await()
            views.setTextViewText(R.id.temperatureTextView, result.main.temp.toString())
            views.setTextViewText(R.id.weatherStateTextView, result.weather[0].description)

            views.setViewVisibility(R.id.temperatureTextView, View.VISIBLE)
            views.setViewVisibility(R.id.weatherStateTextView, View.VISIBLE)
            views.setViewVisibility(R.id.widgetProgressBar, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}