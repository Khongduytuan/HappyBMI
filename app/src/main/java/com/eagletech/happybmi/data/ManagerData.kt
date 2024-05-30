package com.eagletech.happybmi.data

import android.content.Context
import android.content.SharedPreferences

class ManagerData constructor(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("MyDataPref", Context.MODE_PRIVATE)
    }

    companion object {
        @Volatile
        private var instance: ManagerData? = null

        fun getInstance(context: Context): ManagerData {
            return instance ?: synchronized(this) {
                instance ?: ManagerData(context).also { instance = it }
            }
        }
    }

    // Lấy ra thông tin mua theo lượt
    fun getB(): Int {
        return sharedPreferences.getInt("times", 0)
    }

    fun setB(lives: Int) {
        sharedPreferences.edit().putInt("times", lives).apply()
    }

    fun addB(amount: Int) {
        val current = getB()
        setB(current + amount)
    }

    fun removeB() {
        val current = getB()
        if (current > 0) {
            setB(current - 1)
        }
    }


    // Lấy thông tin mua premium
    var isPremium: Boolean?
        get() {
            val userId = sharedPreferences.getString("UserId", "")
            return sharedPreferences.getBoolean("PremiumPlan_\$userId$userId", false)
        }
        set(state) {
            val userId = sharedPreferences.getString("UserId", "")
            sharedPreferences.edit().putBoolean("PremiumPlan_\$userId$userId", state!!).apply()
        }

    // Lưu thông tin người dùng
    fun userId(id: String?) {
        sharedPreferences.edit().putString("UserId", id).apply()
    }

    // Lấy ra thông tin id người dùng
    fun getUserId(): String? {
        return sharedPreferences.getString("UserId", null)
    }

}