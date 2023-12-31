package com.zebra.tempsensors.example.ui.sensor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.zebra.tempsensors.example.databinding.SensorInfoFragmentBinding
import com.zebra.tempsensors.example.ui.InternalViewModel
import com.zebra.tempsensors.example.ui.MainActivity
import com.zebra.zsfinder.IZebraSensor
import kotlin.math.absoluteValue

class SensorInfoFragment : Fragment() {

    private var mBinder: SensorInfoFragmentBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private val internalViewModel: InternalViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private lateinit var ISelectedSensor: IZebraSensor

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = SensorInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity

        val sensorId = requireArguments().getString("discovered_sensor_id")!!
        ISelectedSensor = mActivity.getSensor(sensorId)!!

        internalViewModel.currentTemperature.observe(viewLifecycleOwner) { value ->
            mActivity.runOnUiThread {
                val content = value.contentIfNotHandled ?: return@runOnUiThread
                mBinder?.temperaturesContainer!!.currentTemperature.text = content.toString()
            }
        }

        internalViewModel.recordedTemperatures.observe(viewLifecycleOwner) { value ->
            mActivity.runOnUiThread {
                val content = value.contentIfNotHandled ?: return@runOnUiThread
                mBinder?.temperaturesContainer!!.recordedTemperatures.text = content
            }
        }

        fillUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinder = null

        internalViewModel.currentTemperature.removeObservers(viewLifecycleOwner)
        internalViewModel.recordedTemperatures.removeObservers(viewLifecycleOwner)
    }

    private fun fillUI() {
        mBinder?.infoContainer!!.sensorId.text = ISelectedSensor.id
        mBinder?.infoContainer!!.batteryLevel.text = """${ISelectedSensor.batteryLevel}%"""
        mBinder?.infoContainer!!.alarmStatus.text =
            if (ISelectedSensor.alarmStatus.absoluteValue == 0) {
                "INACTIVE"
            } else {
                "ACTIVE"
            }
        mBinder?.infoContainer!!.macAddress.text = ISelectedSensor.macAddress
        mBinder?.infoContainer!!.lastDiscovered.text = ISelectedSensor.lastDiscovered

        mBinder?.temperaturesContainer!!.mainContainer.visibility = View.VISIBLE
        mBinder?.temperaturesContainer!!.temperatureSensorStatus.text =
            ISelectedSensor.temperatureSensorStatus.toString()
        mBinder?.temperaturesContainer!!.temperatureSamplesCount.text =
            ISelectedSensor.temperatureSamplesCount.toString()

        internalViewModel.getCurrentTemperatureForSensor(ISelectedSensor)
        internalViewModel.getRecordedTemperaturesForSensor(ISelectedSensor)
    }
}