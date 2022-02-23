package space.rodionov.englishdriller.core

import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yuyakaido.android.cardstackview.CardStackView
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.feature_driller.utils.Constants.TAG_MODE

fun fetchTheme(mode: Int, res: Resources): Resources.Theme {
    val theme = res.newTheme()
    return when (mode) {
        0 -> {
            theme.applyStyle(R.style.ModeLight, false)
            theme
        }
        1 -> {
            theme.applyStyle(R.style.ModeDark, false)
            theme
        }

        else -> {
            theme.applyStyle(R.style.ModeLight, false)
            theme
        }
    }
}

fun Resources.Theme.fetchColors(): Array<Int> {

    val bgMainValue = TypedValue()
    this.resolveAttribute(R.attr.bg_main, bgMainValue, true)
    val bgMain = bgMainValue.data

    val bgBetaValue = TypedValue()
    this.resolveAttribute(R.attr.bg_beta, bgBetaValue, true)
    val bgBeta = bgBetaValue.data

    val bgContrastValue = TypedValue()
    this.resolveAttribute(R.attr.bg_contrast, bgContrastValue, true)
    val bgContrast = bgContrastValue.data

    val textMainValue = TypedValue()
    this.resolveAttribute(R.attr.text_main, textMainValue, true)
    val textMain = textMainValue.data

    val textBetaValue = TypedValue()
    this.resolveAttribute(R.attr.text_beta, textBetaValue, true)
    val textBeta = textBetaValue.data

    val bgAccentAlphaValue = TypedValue()
    this.resolveAttribute(R.attr.bg_accent_alpha, bgAccentAlphaValue, true)
    val bgAccentAlpha = bgAccentAlphaValue.data

    val bgAccentBravoValue = TypedValue()
    this.resolveAttribute(R.attr.bg_accent_bravo, bgAccentBravoValue, true)
    val bgAccentBravo = bgAccentBravoValue.data

    val bgAccentCharlieValue = TypedValue()
    this.resolveAttribute(R.attr.bg_accent_charlie, bgAccentCharlieValue, true)
    val bgAccentCharlie = bgAccentCharlieValue.data

    val bgAccentDeltaValue = TypedValue()
    this.resolveAttribute(R.attr.bg_accent_delta, bgAccentDeltaValue, true)
    val bgAccentDelta = bgAccentDeltaValue.data

    val colors = intArrayOf(
        bgMain,
        bgBeta,
        bgContrast,
        textMain,
        textBeta,
        bgAccentAlpha,
        bgAccentBravo,
        bgAccentCharlie,
        bgAccentDelta
    )
    return colors.toTypedArray()
}

fun fetchColors(mode: Int, res: Resources): Array<Int> {
    return fetchTheme(mode, res).fetchColors()
}

//===========================EXTENSIONS====================================

fun CoordinatorLayout.redrawCoord(colors: Array<Int>) {
    if (this.tag?.toString() != "dont_redraw") this.setBackgroundColor(colors[0])
}

fun ConstraintLayout.redrawConstraint(colors: Array<Int>) {
    if (this.tag?.toString() != "dont_redraw" && this.tag?.toString() != "bs_bg") {
        if (this.tag?.toString() == "bg_beta")  this.setBackgroundColor(colors[1])
        else this.setBackgroundColor(colors[0])
    }
}

fun CardView.redrawCardView(colors: Array<Int>) {
    if (this.tag?.toString() == "bg_contrast") {
        this.setCardBackgroundColor(colors[2])
    } else {
        this.setCardBackgroundColor(colors[1])
    }
}

fun EditText.redrawET(colors: Array<Int>) {
    this.backgroundTintList = null
    this.backgroundTintList = ColorStateList.valueOf(colors[1])
    this.setTextColor(colors[3])
    this.setHintTextColor(colors[4])
}

fun TextView.redrawTextView(colors: Array<Int>) {
    if (this.tag?.toString() != "dont_redraw") {
        if (this.tag?.toString() == "tv_beta") {
            this.setTextColor(colors[4])
        } else {
            this.setTextColor(colors[3])
        }
    }
    this.compoundDrawableTintList = null
    this.compoundDrawableTintList = ColorStateList.valueOf(colors[3])
}

fun ImageView.redrawImageView(colors: Array<Int>) {
    if (this.tag?.toString() != "dont_redraw") {
        when (this.tag?.toString()) {
            "iv_beta" -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[4])
            }
            "iv_accent_alpha" -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[5])
            }
            "iv_accent_bravo" -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[6])
            }
            "iv_accent_charlie" -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[7])
            }
            "iv_accent_delta" -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[8])
            }
            else -> {
                this.imageTintList = null
                this.imageTintList = ColorStateList.valueOf(colors[3])
            }
        }
    }
}

fun ChipGroup.redrawChips(colors: Array<Int>) {
    this.children.forEach {
        if (it is Chip) {
            val cslBg = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(
                    resources.getColor(R.color.gray200),
                    colors[5]
                )
            )
            it.chipBackgroundColor = cslBg
            val cslText = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(
                    resources.getColor(R.color.gray600),
                    resources.getColor(R.color.white)
                )
            )
            it.setTextColor(cslText)
        }
    }
}

fun SwitchCompat.redrawSwitch(colors: Array<Int>) {
    this.setTextColor(colors[3])
    val csl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
        ),
        intArrayOf(
            resources.getColor(R.color.gray600),
            colors[5]
        )
    )
    this.trackTintList = null
    this.trackTintList = csl
}

fun CheckBox.redrawCheckBox(colors: Array<Int>) {
    this.setTextColor(colors[4])
    val stateList = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(-android.R.attr.state_pressed)
    )
    this.buttonTintList = null
    val colorList = intArrayOf(colors[5], colors[5], colors[5])
    val csl = ColorStateList(stateList, colorList)
    this.buttonTintList = csl
}

fun View.redrawDivider(colors: Array<Int>) {this.setBackgroundColor(colors[1])}
fun View.redrawDividerContrast(colors: Array<Int>) {this.setBackgroundColor(colors[2])}
fun View.redrawBottomSheetTopLine(colors: Array<Int>) {
    this.backgroundTintList = null
    this.backgroundTintList = ColorStateList.valueOf(colors[2])
}
fun View.redrawBgEditText(colors: Array<Int>) {
    this.backgroundTintList = null
    this.backgroundTintList = ColorStateList.valueOf(colors[2])
}

//================================REDRAW RECYCLER ADAPTERS=========================================

fun ViewGroup.redrawAllRecyclerAdapters(mode: Int) {
    this.children.forEach { child ->
        if (child is RecyclerView) {
            val adapter = child.adapter
            if (adapter != null && adapter is ModeForAdapter) {
                Log.d(TAG_MODE, "redrawAllRecyclerAdapters: adapter tag = ${adapter.getTag()}, mode = $mode")
                adapter.updateMode(mode)
            }
        }
        if (child is CardStackView) {
            val adapter = child.adapter
            if (adapter != null && adapter is ModeForAdapter) {
                Log.d(TAG_MODE, "redrawAllRecyclerAdapters: adapter tag = ${adapter.getTag()}, mode = $mode")
                adapter.updateMode(mode)
            }
        }
        if (child is ViewGroup) child.redrawAllRecyclerAdapters(mode)
    }
}

//================================REDRAW VIEWGROUP=========================================

fun ViewGroup.redrawViewGroup(mode: Int) {
    Log.d(TAG_MODE, "redrawViewGroup: VG class = ${this.javaClass.toString()}")

    this.redrawAllRecyclerAdapters(mode)

    val theme = fetchTheme(mode, resources)
    val colors = fetchColors(mode, resources)

    // Redraw backgrounds
    if (this is CoordinatorLayout) {
        this.redrawCoord(colors)
    }
    if (this is ConstraintLayout) {
        this.redrawConstraint(colors)
    }
    if (this is CardView) {
        this.redrawCardView(colors)
    }

    // Run through children:
    this.children.forEach {

        if (it is TextView) it.redrawTextView(colors)
        if (it is ImageView) it.redrawImageView(colors)
        if (it is ChipGroup) it.redrawChips(colors)
        if (it is EditText) it.redrawET(colors)
        if (it is SwitchCompat) it.redrawSwitch(colors)
        if (it is CheckBox) it.redrawCheckBox(colors)

        if (it.tag?.toString() == "divider") it.redrawDivider(colors)
        if (it.tag?.toString() == "divider_contrast") it.redrawDividerContrast(colors)
        if (it.tag?.toString() == "bs_topline") it.redrawBottomSheetTopLine(colors)
//        if (it.tag?.toString() == "bg_et") it.redrawBgEditText(colors)

        if (it is CardView) {
            it.redrawCardView(colors)
            it.children.forEach { child->
                if (child is ConstraintLayout) {
                    child.redrawConstraint(colors)
                    child.redrawViewGroup(mode)
                }
            }
        }

        if (it is ConstraintLayout) {
            it.redrawConstraint(colors)
            it.redrawViewGroup(mode)
        }
        if (it is CardView) {
            it.redrawCardView(colors)
            (it as ViewGroup).redrawViewGroup(mode)
        }

        //====FOR BOTTOMSHEETS============
        if (this.tag?.toString() == "bs_bg") {
            this.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_top_corners,
                theme
            )
        }
        if (it.tag?.toString() == "bs_topline") {
            it.backgroundTintList = null
            it.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray600))
        }
    }
}

//===========================EXTENSIONS==END====================================

interface ModeForAdapter {
    fun updateMode(newMode: Int)
    fun getTag() : String
}

























