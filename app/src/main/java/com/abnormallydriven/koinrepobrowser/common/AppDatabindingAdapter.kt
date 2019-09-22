package com.abnormallydriven.koinrepobrowser.common

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abnormallydriven.koinrepobrowser.repos.RepositoryAdapter

class AppDatabindingAdapter{

    @BindingAdapter("visibility", requireAll = true)
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("repositories", requireAll = true)
    fun setRepositoryList(recyclerView: RecyclerView, repositoryList: List<RepositoryDto>?){
        if(repositoryList == null){
            return
        }

        (recyclerView.adapter as? RepositoryAdapter)?.updateAdapter(repositoryList)
    }

}