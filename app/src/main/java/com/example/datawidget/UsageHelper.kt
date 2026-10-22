package com.example.datawidget

import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Process
import java.util.Calendar
import java.util.Locale

object UsageHelper {

    /** 이번 달 집계 시작일 (통신사 결제 주기가 1일이 아니면 이 값을 바꾸세요) */
    private const val START_DAY = 1

    fun hasPermission(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = ops.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun monthBytes(context: Context): Long {
        val nsm = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val now = Calendar.getInstance()
        val start = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, START_DAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (after(now)) add(Calendar.MONTH, -1)
        }
        val bucket = nsm.querySummaryForDevice(
            ConnectivityManager.TYPE_MOBILE,
            null,
            start.timeInMillis,
            now.timeInMillis
        )
        return bucket.rxBytes + bucket.txBytes
    }

    fun format(bytes: Long): String {
        val gb = bytes / 1024.0 / 1024.0 / 1024.0
        return if (gb >= 1.0) {
            String.format(Locale.US, "%.2f GB", gb)
        } else {
            String.format(Locale.US, "%.0f MB", bytes / 1024.0 / 1024.0)
        }
    }

    fun text(context: Context): String {
        if (!hasPermission(context)) return context.getString(R.string.need_permission)
        return try {
            format(monthBytes(context))
        } catch (e: Exception) {
            context.getString(R.string.error)
        }
    }
}
