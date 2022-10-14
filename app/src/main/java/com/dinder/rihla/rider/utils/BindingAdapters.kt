@file:OptIn(ExperimentalTime::class)

package com.dinder.rihla.rider.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.color
import com.dinder.rihla.rider.data.model.Company
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.PaymentInfo
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.model.Trip
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@BindingAdapter("destination")
fun setDestinationLabel(view: TextView, destination: Destination?) {
    val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
    destination?.let {
        view.text = if (isArabic) it.arabicName else it.name
    }
}

@BindingAdapter("company")
fun setCompanyLabel(view: TextView, company: Company?) {
    val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
    company?.let {
        view.text = if (isArabic) it.arabicName else it.name
    }
}

@BindingAdapter("seats")
fun setSeatsCount(view: TextView, list: List<String>) {
    view.text = list.size.toString()
}

@BindingAdapter("confirmation_message")
fun setPaymentConfirmationMessage(view: TextView, paymentInfo: PaymentInfo?) {
    paymentInfo?.ticket?.let {
        val count = it.seats.size
        val price = if (it.promoCode.isNullOrEmpty()) {
            ((1.0 + it.rate) * it.price * count).roundToInt().toString()
        } else {
            (it.price * count * (1 + it.rate * it.discountFactor)).roundToInt()
                .toString()
        }

        view.text = view.resources.getString(
            R.string.payment_confirmation_message,
            price,
            paymentInfo.accountName,
            paymentInfo.accountNumber,
            paymentInfo.billingWhatsappNumber
        )
    }
}

@BindingAdapter("price")
fun setTripPrice(view: TextView, trip: Trip?) {
    trip?.let {
        view.text = view.resources.getString(R.string.price_sdg, PriceUtils.getPrice(it))
    }
}

@BindingAdapter("commission")
fun setTripCommision(view: TextView, trip: Trip?) {
    trip?.let {
        view.text = view.resources.getString(R.string.price_sdg, PriceUtils.getCommission(it))
    }
}

@BindingAdapter("departure")
fun timeToDeparture(view: TextView, ticket: Ticket?) {
    ticket?.let {
        val departureTime = DateTimeUtils.departureWithin(it.departure, view.resources)
        if (departureTime == view.resources.getText(R.string.past_ticket).toString()) {
            view.setTextColor(
                view.context.color(android.R.color.holo_red_light)
            )
            view.text = departureTime
        } else {
            view.text = view.resources.getString(R.string.departure_within, departureTime)
        }
    }
}

@BindingAdapter("selected")
fun radioButtonSelectionBackground(view: View, selected: Boolean) {
    val resID = if (selected) R.drawable.radio_button_selected else R.drawable.radio_button_normal
    view.background = ResourcesCompat.getDrawable(view.context.resources, resID, view.context.theme)
}
