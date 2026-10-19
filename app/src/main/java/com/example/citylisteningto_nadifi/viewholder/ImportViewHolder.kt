package com.example.citylisteningto_nadifi.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citylisteningto_nadifi.R


class ImportViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    val buttonImport: Button = itemView.findViewById(R.id.btnImportSong)
    val buttonDelete: Button = itemView.findViewById(R.id.btnDelete)
    val tvSongTitle: TextView = itemView.findViewById(R.id.txtTitle)
    val buttonSong: Button = itemView.findViewById(R.id.btnPlaySong)
}