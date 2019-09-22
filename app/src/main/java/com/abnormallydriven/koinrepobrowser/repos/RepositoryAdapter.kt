package com.abnormallydriven.koinrepobrowser.repos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abnormallydriven.koinrepobrowser.common.RepositoryDto
import com.abnormallydriven.koinrepobrowser.databinding.ListItemRepositoryBinding

class RepositoryAdapter(diffCallback: RepositoryDiffUtilItemCallback) : RecyclerView.Adapter<RepositoryViewHolder>() {

    var itemClickHandler: ((RepositoryDto) -> Unit)? = null

    private val repositoryList = AsyncListDiffer<RepositoryDto>(this, diffCallback)

    fun updateAdapter(updatedList: List<RepositoryDto>){
        repositoryList.submitList(updatedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val binding = ListItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = RepositoryViewHolder(binding)

        binding.root.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition

            if(adapterPosition != RecyclerView.NO_POSITION){
                itemClickHandler?.invoke(repositoryList.currentList[adapterPosition])
            }
        }

        return viewHolder
    }

    override fun getItemCount() = repositoryList.currentList.size

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(repositoryList.currentList[position])
    }

    override fun onViewRecycled(holder: RepositoryViewHolder) {
        holder.unbind()
    }
}


class RepositoryViewHolder(private val binding: ListItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(repositoryDto: RepositoryDto){
        binding.repositoryNameTextView.text = repositoryDto.name
    }

    fun unbind(){
        binding.repositoryNameTextView.text = ""
    }
}

class RepositoryDiffUtilItemCallback : DiffUtil.ItemCallback<RepositoryDto>() {
    override fun areItemsTheSame(oldItem: RepositoryDto, newItem: RepositoryDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepositoryDto, newItem: RepositoryDto): Boolean {
        return oldItem.name == newItem.name
    }

}