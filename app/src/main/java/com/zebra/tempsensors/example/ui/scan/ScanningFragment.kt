package com.zebra.tempsensors.example.ui.scan

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.zebra.tempsensors.example.ui.InternalViewModel
import com.zebra.tempsensors.example.databinding.ScanningFragmentBinding
import com.zebra.tempsensors.example.models.Event
import com.zebra.tempsensors.example.ui.MainActivity

class ScanningFragment : Fragment() {

    private var mBinder: ScanningFragmentBinding? = null
    private val binding get() = mBinder!!

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    private lateinit var scannedSensorsAdapter: ScannedSensorsAdapter

    private val internalViewModel: InternalViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinder = ScanningFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = requireActivity() as MainActivity
        setUpAdapter()

        internalViewModel.sensorDiscoveredResponse.observe(viewLifecycleOwner,
            object : Observer<Event<String>> {
                override fun onChanged(discoveredSensorId: Event<String>) {
                    val sensorId = discoveredSensorId.contentIfNotHandled

                    if (sensorId.isNullOrEmpty()) {
                        return
                    }

                    scannedSensorsAdapter.notifyAdapter(sensorId)
                    mBinder?.emptyListPlaceholder!!.visibility = View.GONE
                }
            })

        with(mBinder?.root!!) {
            setOnRefreshListener {
//                val discoveredSensors = mActivity.getCurrentDiscoveredSensors()
//                scannedSensorsAdapter.notifyAdapter(discoveredSensors)
                isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinder = null
    }

    private fun setUpAdapter() {
        scannedSensorsAdapter = ScannedSensorsAdapter(arrayListOf(), adapterCallBacks)
        mBinder?.sensorsList!!.apply {
            layoutManager = LinearLayoutManager(mContext)
            itemAnimator = DefaultItemAnimator()
            adapter = scannedSensorsAdapter
        }
    }

    private val adapterCallBacks = object : ScannedSensorsAdapter.CallBacks {
        override fun onSelectedSensor(id: String) {
            val retrievedSensor = mActivity.getSensor(id) ?: return
            mActivity.goToSensorInfoFragment(id)
        }
    }
}