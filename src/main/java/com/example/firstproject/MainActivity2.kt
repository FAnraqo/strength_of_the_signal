package com.example.firstproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.telephony.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.E

class MainActivity2 : AppCompatActivity() {

    private lateinit var back_button: Button
    private lateinit var latTextView: TextView
    private lateinit var lonTextView: TextView
    private lateinit var signalStrengthTextView: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var lastReport: TextView

    private lateinit var StartTime: Date
    private lateinit var EndTime: Date
    private var diffInMillis: Long = 0L

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val REQUEST_CODE_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        back_button = findViewById(R.id.go_back_but)
        latTextView = findViewById(R.id.textV_lat)
        lonTextView = findViewById(R.id.textV_lon)
        signalStrengthTextView = findViewById(R.id.textV_sigStrength)

        lastReport = findViewById(R.id.last_report)

        back_button.setOnClickListener {
            finish()
        }

        StartTime = Date()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Создание LocationRequest для запроса обновлений местоположения
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(1000)
            .build()

        // Определение callback для обработки обновлений местоположения
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    getCellInfo(latitude, longitude)
                }
            }
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                ), REQUEST_CODE_PERMISSION
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getCellInfo(latitude: Double, longitude: Double) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val allCellInfo = telephonyManager.allCellInfo

            var signalStrength = "неизвестен" // Значение по умолчанию, если сигнал не найден

            for (cellInfo in allCellInfo) {
                when (cellInfo) {
                    is CellInfoLte -> {
                        val cellSignalStrengthLte = cellInfo.cellSignalStrength
                        signalStrength = "${cellSignalStrengthLte.dbm} dBm"
                        break // Останавливаем цикл, как только нашли сигнал LTE
                    }
                    is CellInfoGsm -> {
                        val cellSignalStrengthGsm = cellInfo.cellSignalStrength
                        signalStrength = "${cellSignalStrengthGsm.dbm} dBm"
                        break // Останавливаем цикл, как только нашли сигнал GSM
                    }
                }
            }

            EndTime = Date()
            diffInMillis = EndTime.time - StartTime.time
            StartTime = Date()
            timeWork()

            // Вывод в логи
            Log.d("Location Info", "Широта: $latitude, Долгота: $longitude, Сила сигнала: $signalStrength, Последний отчёт: $diffInMillis ms")

            latTextView.text = latitude.toString()
            lonTextView.text = longitude.toString()
            signalStrengthTextView.text = signalStrength.toString()
            lastReport.text = diffInMillis.toString() + " ms"
        }
    }

    private fun timeWork(){

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Log.d("Permissions", "Необходимо предоставить разрешения для корректной работы.")
            }
        }
    }
}
