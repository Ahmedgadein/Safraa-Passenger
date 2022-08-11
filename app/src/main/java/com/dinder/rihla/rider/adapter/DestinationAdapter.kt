package com.dinder.rihla.rider.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.databinding.DestinationItemBinding
import java.util.Locale

class DestinationAdapter(
    context: Context,
    resource: Int,
    private val objects: MutableList<Destination>
) :
    ArrayAdapter<Destination>(context, resource, objects) {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = objects.size

    override fun getItem(position: Int): Destination {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            if (convertView == null) DestinationItemBinding.inflate(
                inflater,
                parent,
                false
            ) else DestinationItemBinding.bind(convertView)

        val destination = getItem(position)

        binding.englishDestination.text = destination.name
        binding.arabicDestination.text = destination.arabicName

        return binding.root
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    private val nameFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            val destination = resultValue as Destination
            val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
            return if (isArabic) destination.arabicName else destination.name
        }
    }
}
