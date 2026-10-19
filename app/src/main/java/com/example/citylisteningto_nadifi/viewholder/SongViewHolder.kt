package com.example.citylisteningto_nadifi.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.R

class SongViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    val buttonHide: Button = itemView.findViewById(R.id.btnHide)
    val buttonInfo: Button = itemView.findViewById(R.id.btnInfo)
    val buttonDelete: Button = itemView.findViewById(R.id.btnDelete)
    val tvSongTitle: TextView = itemView.findViewById(R.id.txtTitle)
    val buttonSong: Button = itemView.findViewById(R.id.btnPlaySong)
}