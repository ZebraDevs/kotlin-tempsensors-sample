package com.zebra.tempsensors.example.ui.scan

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zebra.tempsensors.example.databinding.SensorScannedRowBinding

class ScannedSensorsAdapter(
    var list: MutableList<String>,
    var callbacks: CallBacks?
) : RecyclerView.Adapter<ScannedSensorsAdapter.ViewHolder>() {

    private lateinit var mInflater: LayoutInflater
    private lateinit var mContext: Context

    private var mScannedSensorsIds: MutableList<String> =
        if (list.isNotEmpty()) list.toMutableList() else ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        mInflater = LayoutInflater.from(mContext)

        val view = SensorScannedRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sensorId = mScannedSensorsIds[position]

        holder.mBinder.sensorId.text = sensorId
        holder.mBinder.root.setOnClickListener {
            callbacks?.onSelectedSensor(sensorId)
        }
    }

    override fun getItemCount(): Int {
        return mScannedSensorsIds.size
    }

    fun notifyAdapter(items: MutableList<String>) {
        mScannedSensorsIds = items
        notifyDataSetChanged()
    }

    fun notifyAdapter(item: String) {
        if (mScannedSensorsIds.contains(item)) {
            return
        }
        mScannedSensorsIds.add(item)
        notifyItemInserted(mScannedSensorsIds.size - 1)
    }

    fun getScannedSensorsIdsList(): MutableList<String> {
        return mScannedSensorsIds
    }

    fun removeCallBacks() {
        this.callbacks = null
    }

    class ViewHolder internal constructor(binding: SensorScannedRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var mBinder = binding
    }

    interface CallBacks {
        fun onSelectedSensor(id: String)
    }
}