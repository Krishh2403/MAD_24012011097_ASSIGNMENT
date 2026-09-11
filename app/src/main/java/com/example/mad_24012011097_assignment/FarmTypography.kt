package com.example.mad_24012011097_assignment

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView

object FarmTypography {

    fun apply(rootView: View) {
        val regular = loadFont(
            rootView,
            "fonts/Manrope Regular.ttf",
            Typeface.create("sans-serif", Typeface.NORMAL)
        )

        val medium = loadFont(
            rootView,
            "fonts/Manrope Medium.ttf",
            Typeface.create("sans-serif-medium", Typeface.NORMAL)
        )

        val bold = loadFont(
            rootView,
            "fonts/Manrope Bold.ttf",
            Typeface.create("sans-serif", Typeface.BOLD)
        )

        applyToView(rootView, regular, medium, bold)
    }

    private fun loadFont(
        view: View,
        assetPath: String,
        fallback: Typeface
    ): Typeface {
        return try {
            Typeface.createFromAsset(view.context.assets, assetPath)
        } catch (exception: Exception) {
            fallback
        }
    }

    private fun applyToView(
        view: View,
        regular: Typeface,
        medium: Typeface,
        bold: Typeface
    ) {
        if (view is TextView) {
            val headlineSize =
                20f * view.resources.displayMetrics.scaledDensity

            view.typeface = when {
                view.typeface?.isBold == true -> bold
                view.textSize >= headlineSize -> bold
                view is Button -> medium
                view is CompoundButton -> medium
                view.textSize <=
                        14f * view.resources.displayMetrics.scaledDensity -> medium
                else -> regular
            }
        }

        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                applyToView(
                    view.getChildAt(index),
                    regular,
                    medium,
                    bold
                )
            }
        }
    }
}