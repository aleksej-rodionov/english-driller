package space.rodionov.englishdriller.util

import android.content.res.Resources
import android.util.TypedValue
import space.rodionov.englishdriller.R

fun fetchTheme(mode: Int, res: Resources) : Resources.Theme {
    val theme = res.newTheme()
    when (mode) {
        0 -> theme.applyStyle(R.style.DayTheme, false)
        1->theme.applyStyle(R.style.NightTheme, false)
        else -> theme.applyStyle(R.style.DayTheme, false)
    }
    return theme
}

fun Resources.Theme.fetchColors() : Array<Int> {
    val tv1 = TypedValue()
    this.resolveAttribute(R.attr.toolbarBG, tv1, true)
    val toolbarBG = tv1.data

    val tv3 = TypedValue()
    this.resolveAttribute(R.attr.cardBG, tv3, true)
    val cardBG = tv3.data

    val tv4 = TypedValue()
    this.resolveAttribute(R.attr.text1, tv4, true)
    val text1 = tv4.data

    val tv5 = TypedValue()
    this.resolveAttribute(R.attr.text2, tv5, true)
    val text2 = tv5.data

    val tv6 = TypedValue()
    this.resolveAttribute(R.attr.switchBtn, tv6, true)
    val switchBtn = tv6.data

    val tv7 = TypedValue()
    this.resolveAttribute(R.attr.iconUnselected, tv7, true)
    val iconUnselected = tv7.data

    val tv8 = TypedValue()
    this.resolveAttribute(R.attr.iconSelected, tv8, true)
    val iconSelected = tv8.data

    val tv2 = TypedValue()
    this.resolveAttribute(R.attr.text3, tv2, true)
    val text3 = tv2.data

    val tv9 = TypedValue()
    this.resolveAttribute(R.attr.bottomNavBG, tv9, true)
    val bottomNavBG = tv9.data

    val colors = arrayOf(
        toolbarBG,
        cardBG,
        text1,
        text2,
        switchBtn,
        iconUnselected,
        iconSelected,
        text3,
        bottomNavBG
    )

    return colors
}





