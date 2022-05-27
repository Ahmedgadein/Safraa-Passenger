package com.dinder.rihla.rider.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dinder.rihla.rider.data.model.Company
import com.dinder.rihla.rider.data.model.Destination
import java.util.Locale

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
