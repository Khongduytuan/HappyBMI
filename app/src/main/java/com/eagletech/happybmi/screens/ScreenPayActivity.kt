package com.eagletech.happybmi.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.amazon.device.drm.LicensingService
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.FulfillmentResult
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.amazon.device.iap.model.UserDataResponse
import com.eagletech.happybmi.data.ManagerData
import com.eagletech.happybmi.databinding.ActivityScreenPayBinding

class ScreenPayActivity : AppCompatActivity() {
    private lateinit var sBinding: ActivityScreenPayBinding
    private lateinit var myData: ManagerData
    private lateinit var currentUserId: String
    private lateinit var currentMarketplace: String

    // Phải thêm sku các gói vào ứng dụng
    companion object {
        const val bmi5 = "com.eagletech.happybmi.bmi5"
        const val bmi10 = "com.eagletech.happybmi.bmi10"
        const val bmi15 = "com.eagletech.happybmi.bmi15"
        const val sub = "com.eagletech.happybmi.bmisublongtimes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sBinding = ActivityScreenPayBinding.inflate(layoutInflater)
        setContentView(sBinding.root)
        myData = ManagerData.getInstance(this)
        setupIAPOnCreate()
        setClickItems()

    }

    private fun setClickItems() {
        sBinding.btn5.setOnClickListener {
//            myData.addB(2)
            PurchasingService.purchase(bmi5)
        }
        sBinding.btn10.setOnClickListener {
            PurchasingService.purchase(bmi10)
        }
        sBinding.btn15.setOnClickListener {
            PurchasingService.purchase(bmi15)
        }
        sBinding.btnSub.setOnClickListener {
            PurchasingService.purchase(sub)
        }
        sBinding.btnFinish.setOnClickListener { finish() }
    }

    private fun setupIAPOnCreate() {
        val purchasingListener: PurchasingListener = object : PurchasingListener {
            override fun onUserDataResponse(response: UserDataResponse) {
                when (response.requestStatus!!) {
                    UserDataResponse.RequestStatus.SUCCESSFUL -> {
                        currentUserId = response.userData.userId
                        currentMarketplace = response.userData.marketplace
                        myData.userId(currentUserId)
                    }

                    UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED -> Log.v(
                        "IAP SDK",
                        "loading failed"
                    )
                }
            }

            override fun onProductDataResponse(productDataResponse: ProductDataResponse) {
                when (productDataResponse.requestStatus) {
                    ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                        val products = productDataResponse.productData
                        for (key in products.keys) {
                            val product = products[key]
                            Log.v(
                                "Product:", String.format(
                                    "Product: %s\n Type: %s\n SKU: %s\n Price: %s\n Description: %s\n",
                                    product!!.title,
                                    product.productType,
                                    product.sku,
                                    product.price,
                                    product.description
                                )
                            )
                        }
                        //get all unavailable SKUs
                        for (s in productDataResponse.unavailableSkus) {
                            Log.v("Unavailable SKU:$s", "Unavailable SKU:$s")
                        }
                    }

                    ProductDataResponse.RequestStatus.FAILED -> Log.v("FAILED", "FAILED")
                    else -> {}
                }
            }

            override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
                when (purchaseResponse.requestStatus) {
                    PurchaseResponse.RequestStatus.SUCCESSFUL -> {

                        if (purchaseResponse.receipt.sku == bmi5) {
                            myData.addB(5)
                            finish()
                        }
                        if (purchaseResponse.receipt.sku == bmi10) {
                            myData.addB(10)
                            finish()
                        }
                        if (purchaseResponse.receipt.sku == bmi15) {
                            myData.addB(15)
                            finish()
                        }
                        if (purchaseResponse.receipt.sku == sub) {
                            myData.isPremium = true
                            finish()
                        }
                        PurchasingService.notifyFulfillment(
                            purchaseResponse.receipt.receiptId, FulfillmentResult.FULFILLED
                        )

                        Log.v("FAILED", "FAILED")
                    }

                    PurchaseResponse.RequestStatus.FAILED -> {}
                    else -> {}
                }
            }

            override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
                // Process receipts
                when (response.requestStatus) {
                    PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                        for (receipt in response.receipts) {
                            myData.isPremium = !receipt.isCanceled
                        }
                        if (response.hasMore()) {
                            PurchasingService.getPurchaseUpdates(false)
                        }

                    }

                    PurchaseUpdatesResponse.RequestStatus.FAILED -> Log.d("FAILED", "FAILED")
                    else -> {}
                }
            }
        }
        PurchasingService.registerListener(this, purchasingListener)
        Log.d(
            "DetailBuyAct", "Appstore SDK Mode: " + LicensingService.getAppstoreSDKMode()
        )
    }


    override fun onResume() {
        super.onResume()
        PurchasingService.getUserData()
        val productSkus: MutableSet<String> = HashSet()
        productSkus.add(sub)
        productSkus.add(bmi5)
        productSkus.add(bmi10)
        productSkus.add(bmi15)
        PurchasingService.getProductData(productSkus)
        PurchasingService.getPurchaseUpdates(false)
    }
}