package tech.altgreg.mygpsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.db.Marker
import tech.altgreg.mygpsapp.other.Constants
import tech.altgreg.mygpsapp.ui.ShowTripMap

class MarkerAdapter: RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>()  {

    inner class MarkerViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val ivMarkerImage: ImageView
        val tvLocalization: TextView
        val tvTypeOfTrip: TextView
        val tvDescription: TextView


        init {
            ivMarkerImage = itemView.findViewById(R.id.ivMarkerImage)
            tvLocalization = itemView.findViewById(R.id.tvLocalization)
            tvTypeOfTrip = itemView.findViewById(R.id.tvTypeOfTrip)
            tvDescription = itemView.findViewById(R.id.tvDescription)

        }

    }

    val diffCallback = object: DiffUtil.ItemCallback<Marker>(){

        override fun areItemsTheSame(oldItem: Marker, newItem: Marker): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Marker, newItem: Marker): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Marker>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerAdapter.MarkerViewHolder {
        return MarkerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                tech.altgreg.mygpsapp.R.layout.marker_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MarkerAdapter.MarkerViewHolder, position: Int) {
        val marker = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(marker.img).into(holder.ivMarkerImage)



            holder.tvLocalization.text = marker.localization
            holder.tvTypeOfTrip.text = marker.typeOfTrip
            holder.tvDescription.text = marker.description

        }
    }






}