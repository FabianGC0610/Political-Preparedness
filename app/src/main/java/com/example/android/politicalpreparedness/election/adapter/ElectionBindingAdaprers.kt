package com.example.android.politicalpreparedness.election.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listUpcomingElections")
fun RecyclerView.bindUpcomingElectionsList(data: List<Election>?) {
    val adapter = this.adapter as ElectionListAdapter
    adapter.submitList(data)
}
