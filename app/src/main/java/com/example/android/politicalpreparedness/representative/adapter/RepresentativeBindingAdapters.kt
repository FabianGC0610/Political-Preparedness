package com.example.android.politicalpreparedness.representative.adapter

import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.model.Representative

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = Uri.parse(it)
        Glide.with(view)
            .load(uri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_profile)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    } ?: run {
        view.setImageResource(R.drawable.ic_profile)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@BindingAdapter("listRepresentatives")
fun RecyclerView.bindRepresentativesList(data: List<Representative>?) {
    val adapter = this.adapter as RepresentativeListAdapter
    adapter.submitList(data)
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}
