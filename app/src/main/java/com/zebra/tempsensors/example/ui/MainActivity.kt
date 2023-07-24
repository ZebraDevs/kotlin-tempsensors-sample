package com.zebra.tempsensors.example.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.zebra.tempsensors.example.R
import com.zebra.tempsensors.example.databinding.ActivityMainBinding
import com.zebra.tempsensors.example.models.Event
import com.zebra.zsfinder.IZebraSensor
import com.zebra.zsfinder.IZebraSensorService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var mNavigationController: NavController

    private var mZebraSensorService: IZebraSensorService? = null

    private val internalParcelViewModel: InternalViewModel by viewModels()

    private var mIsDiscoveryActive = false
    private var mIsDiscoveryFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        mNavigationController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(mNavigationController.graph)
        setupActionBarWithNavController(mNavigationController, appBarConfiguration)
    }

    override fun onResume() {
        super.onResume()
        bindZSFinderService()
    }

    override fun onPause() {
        super.onPause()
        unBindZSFinderService()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun bindZSFinderService() {
        val intent =
            Intent(IZebraSensorService.BIND_SERVICE_ACTION)
                .setPackage(IZebraSensorService.BIND_SERVICE_PACKAGE)
        bindService(intent, sensorServiceConnection, BIND_AUTO_CREATE)
    }

    private fun unBindZSFinderService() {
        unbindService(sensorServiceConnection)
        unregisterReceiver(sensorServiceEventsReceiver)
    }

    private fun registerForServiceEvents() {
        val filter = IntentFilter().apply {
            addAction(IZebraSensorService.ACTION_DISCOVERY_ERROR)
            addAction(IZebraSensorService.ACTION_DISCOVERY_FINISHED)
            addAction(IZebraSensorService.ACTION_DISCOVERY_STARTED)
            addAction(IZebraSensorService.ACTION_DISCOVERY_FOUND_SENSOR)
            addAction(IZebraSensorService.ACTION_DISCOVERY_ERROR_PERMISSIONS)
            addAction(IZebraSensorService.ACTION_DISCOVERY_PAUSED)
            addAction(IZebraSensorService.ACTION_DISCOVERY_UNPAUSED)
            addAction(IZebraSensorService.ACTION_DISCOVERY_ERROR_MISSING_AUTH_TOKEN)
            addAction(IZebraSensorService.ACTION_DISCOVERY_ERROR_INVALID_AUTH_TOKEN)
        }
        registerReceiver(sensorServiceEventsReceiver, filter)
    }

    fun goToSensorInfoFragment(sensorId: String) {
        mNavigationController.navigate(
            R.id.action_FirstFragment_to_SecondFragment,
            Bundle().apply {
                putString("discovered_sensor_id", sensorId)
            })
    }

    fun getSensor(sensorId: String): IZebraSensor? {
        if (mIsDiscoveryActive) {
            Toast.makeText(
                this@MainActivity,
                "Unable to retrieve sensor's data while discovery is active",
                Toast.LENGTH_LONG
            ).show()
            return null;
        }
        return mZebraSensorService?.getSensor(sensorId)
    }

    private val sensorServiceEventsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Logging Event Action from SensorService: ${intent.action}")
            if (intent.action == IZebraSensorService.ACTION_DISCOVERY_STARTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Discovery process has started..",
                    Toast.LENGTH_LONG
                ).show()
                mIsDiscoveryActive = true
                mIsDiscoveryFinished = false
            }

            if (intent.action == IZebraSensorService.ACTION_DISCOVERY_FINISHED) {
                Toast.makeText(
                    this@MainActivity,
                    "Discovery process has finished",
                    Toast.LENGTH_LONG
                ).show()
                mIsDiscoveryActive = false
                mIsDiscoveryFinished = true
            }

            if (intent.action == IZebraSensorService.ACTION_DISCOVERY_FOUND_SENSOR) {
                val sensorId =
                    intent.extras!!.getString(IZebraSensorService.EXTRA_DISCOVERED_SENSOR_ID)!!
                internalParcelViewModel.sensorDiscoveredResponse.value = Event(sensorId)
            }
        }
    }

    private val sensorServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mZebraSensorService = IZebraSensorService.Stub.asInterface(iBinder)

            if (mZebraSensorService != null && mZebraSensorService!!.isClientCompatibleWithService(
                    IZebraSensorService.VERSION,
                    IZebraSensorService.REVISION
                )
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "Successfully connected to ZSFinder Service!",
                    Toast.LENGTH_LONG
                ).show()
                registerForServiceEvents()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mZebraSensorService = null
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}