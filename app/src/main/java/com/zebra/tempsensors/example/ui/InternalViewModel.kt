package com.zebra.tempsensors.example.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.tempsensors.example.models.Event
import com.zebra.zsfinder.IZebraSensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternalViewModel : ViewModel() {

    val sensorDiscoveredResponse: MutableLiveData<Event<List<String>>> = MutableLiveData()
    val sensorsScanStartEvent: MutableLiveData<Boolean> = MutableLiveData()

    val recordedTemperatures: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }
    val currentTemperature: MutableLiveData<Event<Float>> by lazy {
        MutableLiveData<Event<Float>>()
    }

    fun getCurrentTemperatureForSensor(sensor: IZebraSensor) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentTemperature.postValue(Event(sensor.readTemperature()))
            } catch (ex: Exception) {
                currentTemperature.postValue(Event(Float.MIN_VALUE))
            }
        }
    }

    fun getRecordedTemperaturesForSensor(sensor: IZebraSensor) {
        viewModelScope.launch(Dispatchers.IO) {
            var recordedTemperaturesTxt = ""
            try {
                val recordedTemperaturesArray = sensor.readTemperatureSamples()

                if (recordedTemperaturesArray.isEmpty()) {
                    recordedTemperaturesTxt = "0"
                } else {
                    recordedTemperaturesArray.forEach {
                        recordedTemperaturesTxt = recordedTemperaturesTxt + it.toString() + "\n\n"
                    }
                }
            } catch (ex: Exception) {
                recordedTemperaturesTxt = "0"
            }
            recordedTemperatures.postValue(Event(recordedTemperaturesTxt))
        }
    }
}