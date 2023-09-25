package dev.fztech.app.info.utils.extenstions

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toDate(pattern: String = "dd/MMM/yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}