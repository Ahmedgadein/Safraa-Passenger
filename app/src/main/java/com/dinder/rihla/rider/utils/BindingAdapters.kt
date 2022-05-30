package com.dinder.rihla.rider.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dinder.rihla.rider.data.model.Company
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.model.Trip
import java.util.Locale

@BindingAdapter("price")
fun setPriceLabel(view: TextView, trip: Trip?) {
    trip?.let {
        view.text = "${trip.price} SDG"
    }
}

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

@BindingAdapter("priceSum")
fun setTicketPriceSum(view: TextView, ticket: Ticket?) {
    ticket?.let {
        val ticketsCount = ticket.seats.size
        view.text = "${ticketsCount * ticket.trip.price} SDG"
    }
}
