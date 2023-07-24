package com.zebra.tempsensors.example.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.tempsensors.example.models.Event
import com.zebra.zsfinder.IZebraSensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternalViewModel : ViewModel() {

    val sensorDiscoveredResponse: MutableLiveData<Event<String>> = MutableLiveData()

    val recordedTemperatures: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val currentTemperature: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }

    fun getCurrentTemperatureForSensor(sensor: IZebraSensor) {
        viewModelScope.launch(Dispatchers.IO) {
            currentTemperature.postValue(sensor.readTemperature())
        }
    }

    fun getRecordedTemperaturesForSensor(sensor: IZebraSensor) {
        viewModelScope.launch(Dispatchers.IO) {
            val recordedTemperaturesArray = sensor.readTemperatureSamples()
            var recordedTemperaturesTxt = ""

            if (recordedTemperaturesArray.isEmpty()) {
                recordedTemperaturesTxt = "0"
            } else {
                recordedTemperaturesArray.forEach {
                    recordedTemperaturesTxt = recordedTemperaturesTxt + it.toString() + "\n\n"
                }
            }
            recordedTemperatures.postValue(recordedTemperaturesTxt)
        }
    }
}