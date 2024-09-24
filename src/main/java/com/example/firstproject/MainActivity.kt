package com.example.firstproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var number_field_1: EditText
    private lateinit var number_field_2: EditText
    private lateinit var add_button: Button
    private lateinit var exit_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //resultTextView = findViewById(R.id.resultTextView)
        number_field_1 = findViewById(R.id.number_field_1)
        number_field_2 = findViewById(R.id.number_field_2)
        add_button = findViewById(R.id.add_button)
        exit_button = findViewById(R.id.but_exit)

        add_button.setOnClickListener{
            val num1 = number_field_1.text.toString().toFloat()
            val num2 = number_field_2.text.toString().toFloat()
            val res = num1 + num2
            //resultTextView.text = res.toString()
            showInfo(res.toString())

            val intent = Intent(this, MainActivity2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        exit_button.setOnClickListener{
            showInfoAlert("Вы хотите закрыть приложение?")
        }

    }

    private fun showInfoAlert(text: String) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Большая подсказка")
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton("Конечно") { _, _ ->
                finish()
            }
            .setNegativeButton("Нет") { dialogInterface, _ ->
                dialogInterface.cancel()
            }

        val dialog = builder.create()
        dialog.show() // Не забудьте вызвать show() для отображения диалога
    }

    private fun showInfo (text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

}