package com.finiotech.ekycdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.finiotech.ekycdemo.databinding.ActivityFaceVerificationBinding
import com.pickmyid.verification.UserVerification
import com.pickmyid.verification.callback.FetchSelfieInformationListener
import com.pickmyid.verification.model.data.FaceAngle

class FaceVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceVerificationBinding
    private lateinit var userVerification: UserVerification
    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFaceVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.extras!!.getString("userId").toString()
        Log.e("TAG", "onCreate: "+userId )

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }  else {
            configFaceVerification()
        }
    }

    fun configFaceVerification(){
        userVerification = UserVerification(
            this@FaceVerificationActivity,
            binding.faceView,
            true,
            userId,
            BuildConfig.EKYC_STORE_ID,
            BuildConfig.EKYC_STORE_PASSWORD,
            BuildConfig.EKYC_IS_PRE_PRODUCTION
        )
        userVerification.setInformationListener(object : FetchSelfieInformationListener {
            override fun onStart() {
                CommonUtil.showProgress(this@FaceVerificationActivity)
            }

            override fun onInstruction(faceAngle: Int) {
                when (faceAngle) {
                    FaceAngle.LEFT -> {
                        Toast.makeText(
                            this@FaceVerificationActivity,
                            "Turn your head on your Left",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FaceAngle.RIGHT -> {
                        Toast.makeText(
                            this@FaceVerificationActivity,
                            "Turn your head on your Right",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    FaceAngle.CENTER -> {
                        Toast.makeText(
                            this@FaceVerificationActivity,
                            "Turn your head on your Center",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onSuccess() {
                CommonUtil.dismissProgress()
                CommonUtil.showDialog(this@FaceVerificationActivity, "User verification completed")
            }

            override fun onFail(code: Int, message: String?) {
                CommonUtil.dismissProgress()
                CommonUtil.showDialog(this@FaceVerificationActivity, message!!)
                userVerification.startProcess()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        userVerification.startProcess()
    }

    override fun onPause() {
        userVerification.stopProcess()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            configFaceVerification()
            userVerification.startProcess()
        }
    }
}