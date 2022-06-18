package com.fiz.mono.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.themeColor
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.currency.data.currencies


@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel
) {
    val state = viewModel.viewState

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        currencies.forEachIndexed { index, currency ->
            CurrencyItem(
                currency = currency.name,
                currencySign = currency.sign,
                rightSideSign = currency.rightSideSign,
                value = state.currency == currency.name,
                onClick = {
                    viewModel.onEvent(CurrencyEvent.CurrencyItemClicked(currency.name))
                }
            )

            if (index != currencies.lastIndex)
                CurrencyDivider()
        }

    }
}

@Composable
private fun CurrencyDivider() {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(16.dp))

    Divider(
        color = Color(context.themeColor(R.attr.colorGray)),
        startIndent = 28.dp
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun CurrencyItem(
    currency: String,
    currencySign: String,
    rightSideSign: Boolean = false,
    value: Boolean,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val styleTextSize16ColorPrimary = TextStyle(
        fontSize = 16.sp,
        color = MaterialTheme.colors.primary
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            modifier = Modifier
                .size(width = 28.dp, height = 32.dp),
            selected = value,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(context.getColorCompat(R.color.blue)),
                unselectedColor = Color(context.themeColor(R.attr.colorGray)),
                disabledColor = MaterialTheme.colors.onSurface
            )
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = Color(context.themeColor(R.attr.colorMain))
                )
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(8.dp),
                    color = Color(context.themeColor(R.attr.colorGray))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currency,
                style = styleTextSize16ColorPrimary
            )
        }

        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = if (rightSideSign)
                "100.000$currency"
            else
                "${currency}100.000",
            style = styleTextSize16ColorPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = currencySign,
            style = styleTextSize16ColorPrimary
        )
    }
}