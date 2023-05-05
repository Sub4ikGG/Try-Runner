package ru.efremovkirill.tryrunner.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/*fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}*/

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Double.toQuantityString(): String {
    if(this < 0) return "отсутствует"
    return when(this) {
        0.0 -> "нет"
        in 1.0..3.0 -> "${this.toInt()} шт."
        in 3.1..7.0 -> "средне"
        in 7.1..10.0 -> "много"
        else -> "неизвестно"
    }
}

fun Double.hasFractionalPart(): Boolean {
    return this.rem(1) != 0.0
}

fun openUrl(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    ContextCompat.startActivity(context, browserIntent, null)
}