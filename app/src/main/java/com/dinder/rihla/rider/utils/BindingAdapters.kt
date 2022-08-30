package com.dinder.rihla.rider.utils

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.data.model.Company
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.PaymentInfo
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.model.Trip
import java.util.Locale
import kotlin.math.roundToInt

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

@BindingAdapter("priceSum")
fun setTicketPriceSum(view: TextView, ticket: Ticket?) {
    ticket?.let {
        val ticketsCount = ticket.seats.size
        val sum = ""
        view.text = sum
    }
}

@BindingAdapter("price")
fun setTripPrice(view: TextView, trip: Trip?) {
    trip?.let {
        view.text = view.resources.getString(R.string.price_sdg, PriceUtils.getPrice(it))
    }
}

@BindingAdapter("departure")
fun timeToDeparture(view: TextView, ticket: Ticket?) {
    ticket?.let {
        view.text = view.resources.getString(R.string.price_sdg, PriceUtils.getPrice(it))
    }
}

@BindingAdapter("selected")
fun radioButtonSelectionBackground(view: ConstraintLayout, selected: Boolean) {
    val resID = if (selected) R.drawable.radio_button_selected else R.drawable.radio_button_normal
    view.background = view.resources.getDrawable(resID, view.context.theme)
}
