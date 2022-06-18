package com.finiotech.ekycdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.finiotech.ekycdemo.databinding.ActivityIdVerificationBinding
import com.pickmyid.verification.IDVerification
import com.pickmyid.verification.callback.FetchIdInformationListener
import com.pickmyid.verification.model.data.CountryCode
import com.pickmyid.verification.model.data.IDType

class IDVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdVerificationBinding
    private lateinit var idVerification: IDVerification;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIdVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }  else {
            configEkyc()
        }
    }

    override fun onResume() {
        super.onResume()
        idVerification.startProcess()
    }

    override fun onPause() {
        super.onPause()
        binding.message.text = "Scan Frontside of your ID Card"
        idVerification.stopProcess()
    }

    fun configEkyc(){
        idVerification = IDVerification(
            this,
            binding.idView,
            BuildConfig.EKYC_STORE_ID,
            BuildConfig.EKYC_STORE_PASSWORD,
            CountryCode.BANGLADESH,
            IDType.NID,
            true)
        idVerification.setWaitingTime(1500)
        idVerification.setOnResultListener(object : FetchIdInformationListener {
            override fun onStart() {
                CommonUtil.showProgress(this@IDVerificationActivity)
            }

            override fun onCardFlip() {
                binding.message.text = "Scan Backside of your ID Card"
            }

            override fun onSuccess(userId: String?) {
                CommonUtil.dismissProgress()
                startActivity(Intent(this@IDVerificationActivity, FaceVerificationActivity::class.java).putExtra("userId", userId))
            }

            override fun onFail(code: Int, message: String?) {
                CommonUtil.dismissProgress()
                CommonUtil.showDialog(this@IDVerificationActivity, message!!)
                idVerification.startProcess()
            }
        })
        binding.message.text = "Scan Frontside of your ID Card"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            configEkyc()
            idVerification.startProcess()
        }
    }
}