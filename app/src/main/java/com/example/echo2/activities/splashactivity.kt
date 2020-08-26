package com.example.echo2.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.echo2.R

class splashactivity : AppCompatActivity() {
    //variable declaration
    var permissionString = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )

    //end of variable declaration
    //function declaration
    fun splashscreen() {
        Handler().postDelayed({
            val startact = Intent(this, MainActivity::class.java)
            startActivity(startact)
            this.finish()

        }, 1000)
    }

    fun hasPermissionall(context: Context, vararg permissions: String): Boolean {
        var hasallpermission = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasallpermission = false
            }
        }
        return hasallpermission
    }


    //end of function declaration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashactivity)
        if (!hasPermissionall(this@splashactivity, *permissionString)) {
            //why?
            ActivityCompat.requestPermissions(this@splashactivity, permissionString, 131)
        } else {
            splashscreen()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {

                    splashscreen()

                } else {
                    Toast.makeText(
                        this@splashactivity,
                        "please grant all the permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@splashactivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

        }
    }


}
