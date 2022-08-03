package com.fiz.mono.common.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.common.ui.resources.databinding.NavigationBarBinding

class NavigationBarView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: NavigationBarBinding

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        R.style.DefaultNavigationBarViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.NavigationBarViewStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.navigation_bar, this, true)
        binding = NavigationBarBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    fun setTextTitle(text: String) {
        binding.titleTextView.text = text
    }

    fun setVisibilityChoiceButton(visibility: Boolean) {
        binding.choiceImageButton.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun setVisibilityBackButton(visibility: Boolean) {
        binding.backButton.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun setVisibilityActionButton(visibility: Boolean) {
        binding.actionButton.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun setOnClickListenerActionButton(onClickListener: OnClickListener) {
        binding.actionButton.setOnClickListener(onClickListener)
    }

    fun setOnClickListenerBackButton(onClickListener: OnClickListener) {
        binding.backButton.setOnClickListener(onClickListener)
    }

    fun setOnClickListenerChoiceButton(onClickListener: OnClickListener) {
        binding.choiceImageButton.setOnClickListener(onClickListener)
    }

    private fun initializeAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.NavigationBarView, defStyleAttr, defStyleRes
        )

        with(binding) {
            val colorTextAction = typedArray
                .getColor(R.styleable.NavigationBarView_colorTextAction, Color.BLACK)
            actionButton.setTextColor(colorTextAction)

            val textAction = typedArray
                .getString(R.styleable.NavigationBarView_textAction)
            actionButton.text = textAction

            val textTitle = typedArray
                .getString(R.styleable.NavigationBarView_textTitle)
            titleTextView.text = textTitle

            val visibilityBackButton = typedArray
                .getBoolean(R.styleable.NavigationBarView_visibilityBackButton, true)
            backButton.visibility = if (visibilityBackButton) View.VISIBLE else View.INVISIBLE

            val visibilityActionButton = typedArray
                .getBoolean(R.styleable.NavigationBarView_visibilityActionButton, true)
            actionButton.visibility = if (visibilityActionButton) View.VISIBLE else View.INVISIBLE

            val visibilityChoiceButton = typedArray
                .getBoolean(R.styleable.NavigationBarView_visibilityChoiceButton, true)
            choiceImageButton.visibility =
                if (visibilityChoiceButton) View.VISIBLE else View.INVISIBLE
        }

        typedArray.recycle()
    }
}