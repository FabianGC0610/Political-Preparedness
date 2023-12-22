package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listUpcomingElections")
fun RecyclerView.bindUpcomingElectionsList(data: List<Election>?) {
    val adapter = this.adapter as ElectionListAdapter
    adapter.submitList(data)
}

@BindingAdapter("setupToolbar")
fun Toolbar.setupToolbar(electionName: String?) {
    val navController = Navigation.findNavController(this)
    this.setNavigationOnClickListener {
        navController.popBackStack()
    }
    electionName?.let {
        this.title = electionName
    } ?: run {
        this.title = resources.getString(R.string.voter_info_election_default_name_text)
    }
}

@BindingAdapter("setDynamicPresentation")
fun TextView.setDynamicPresentation(info: String?) {
    info?.let {
        this.visibility = View.VISIBLE
    } ?: run {
        this.visibility = View.GONE
    }
}

@BindingAdapter("setDynamicText")
fun TextView.setDynamicText(info: String?) {
    info?.let {
        this.text = info
    }
}

@BindingAdapter("setDynamicAddressPresentation")
fun TextView.setDynamicAddressPresentation(info: String?) {
    if (info?.replace(" ", "")?.replace(",", "").isNullOrBlank()) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

@BindingAdapter("setDynamicAddressText")
fun TextView.setDynamicAddressText(info: String?) {
    if (!info?.replace(" ", "")?.replace(",", "").isNullOrBlank()) {
        this.text = info
    }
}

@BindingAdapter("setDynamicStateHeaderPresentation")
fun TextView.setDynamicStateHeaderPresentation(canShow: Boolean) {
    if (canShow) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}
