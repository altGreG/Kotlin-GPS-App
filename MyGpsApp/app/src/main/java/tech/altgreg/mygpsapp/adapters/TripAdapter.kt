package tech.altgreg.mygpsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.altgreg.mygpsapp.R
import tech.altgreg.mygpsapp.db.Trip
import tech.altgreg.mygpsapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TripAdapter:RecyclerView.Adapter<TripAdapter.TripViewHolder>() {



    inner class TripViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val ivTripImage: ImageView
        val tvDate: TextView
        val tvDistance: TextView
        val tvTime: TextView

        init {
            ivTripImage = itemView.findViewById(R.id.ivMarkerImage)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            tvTime = itemView.findViewById(R.id.tvTime)
        }

    }

    val diffCallback = object: DiffUtil.ItemCallback<Trip>(){

        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Trip>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(
            LayoutInflater.from(parent.context).inflate(
                tech.altgreg.mygpsapp.R.layout.item_trip,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(trip.img).into(holder.ivTripImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = trip.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            holder.tvDate.text = dateFormat.format(calendar.time)



            val distanceInKm = "${trip.distanceInMeters / 1000f}km"
            holder.tvDistance.text = distanceInKm

            holder.tvTime.text = TrackingUtility.getFormattedStopWatchTime(trip.timeInMillis)

        }
    }




}