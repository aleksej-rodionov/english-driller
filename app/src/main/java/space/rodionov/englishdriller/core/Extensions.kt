package space.rodionov.englishdriller.core

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import java.math.BigDecimal
import kotlin.math.roundToInt

fun Float.roundToTwoDecimals(): Float {
    var bd = BigDecimal(java.lang.Float.toString(this))
    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
    return bd.toFloat()
}

//fun Float.roundToInt(): Float {
//    var bd = BigDecimal(java.lang.Float.toString(this))
//    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
//    return bd.toFloat()
//}

fun AutoCompleteTextView.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}