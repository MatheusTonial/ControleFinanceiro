package com.tonial.controlefinanceiro.widget

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.tonial.controlefinanceiro.MainActivity
import com.tonial.controlefinanceiro.R
import com.tonial.controlefinanceiro.ui.navegacao.Routes

class QuickAddWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.quick_add_widget)

        val intent = Intent(context, MainActivity::class.java).apply {
            action = "com.tonial.controlefinanceiro.QUICK_ADD"
            putExtra("route", Routes.LANCAMENTO_CONTA)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
            addNextIntent(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        views.setOnClickPendingIntent(R.id.quick_add_button, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
