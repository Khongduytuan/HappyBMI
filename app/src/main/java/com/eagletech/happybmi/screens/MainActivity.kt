package com.eagletech.happybmi.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eagletech.happybmi.R
import com.eagletech.happybmi.data.ManagerData
import com.eagletech.happybmi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myDataApp: ManagerData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDataApp = ManagerData.getInstance(this)
        binding.btnBMI.setOnClickListener {
            val height = binding.edtHeight.text.toString()
            val weight = binding.edtWeight.text.toString()
            if (height.isEmpty()) {
                Toast.makeText(this, "Enter your height", Toast.LENGTH_LONG).show()
            } else if (weight.isEmpty()) {
                Toast.makeText(this, "Enter your weight", Toast.LENGTH_LONG).show()
            } else {
               if (myDataApp.isPremium == true){
                   val h = height.toFloat() / 100
                   val w = weight.toFloat()
                   val bmi = calculateBMI(h, w)
                   val bmiStatus = getBMIStatus(bmi)
                   binding.tvResult.text = "$bmiStatus"
                   binding.edtHeight.setText("")
                   binding.edtWeight.setText("")
               } else if (myDataApp.getB() > 0 ){
                   val h = height.toFloat() / 100
                   val w = weight.toFloat()
                   val bmi = calculateBMI(h, w)
                   val bmiStatus = getBMIStatus(bmi)
                   binding.tvResult.text = "$bmiStatus"
                   binding.edtHeight.setText("")
                   binding.edtWeight.setText("")
                   myDataApp.removeB()
               } else{
                   Toast.makeText(this, "Please buy usage to use", Toast.LENGTH_LONG).show()
                   val intent = Intent(this, ScreenPayActivity::class.java)
                   startActivity(intent)
               }
            }
        }

        binding.toolBar.buy.setOnClickListener {
            val intent = Intent(this, ScreenPayActivity::class.java)
            startActivity(intent)
        }
        binding.toolBar.info.setOnClickListener {
            showInfoBuy()
        }

    }

    private fun calculateBMI(height: Float, weight: Float): Float {
        return weight / (height * height)
    }

    private fun getBMIStatus(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Fat"
        }
    }

    private fun showInfoBuy() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Your usage BMI in my app")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .create()
        if (myDataApp.isPremium == true) {
            dialog.setMessage("Registered to use successfully")
        } else {
            dialog.setMessage("You have ${myDataApp.getB()} uses")
        }
        dialog.show()
    }
}

