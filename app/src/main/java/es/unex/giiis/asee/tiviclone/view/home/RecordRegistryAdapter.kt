package es.unex.giiis.asee.tiviclone.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.unex.giiis.asee.tiviclone.data.model.VideoRecord
import es.unex.giiis.asee.tiviclone.databinding.RecordItemListBinding

class RecordRegistryAdapter(
    private val videos: List<VideoRecord>,
    private val onClick: (video: VideoRecord) -> Unit,
    private val onLongClick: (uri: VideoRecord) -> Unit
) : RecyclerView.Adapter<RecordRegistryAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: RecordItemListBinding,
        private val onClick: (video: VideoRecord) -> Unit,
        private val onLongClick: (uri: VideoRecord) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(video: VideoRecord, totalItems: Int){
            with(binding){
                uriPath.text = video.uri
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding =
            RecordItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding, onClick, onLongClick)
    }

    override fun getItemCount() = videos.size

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(videos[position], videos.size)
    }
}