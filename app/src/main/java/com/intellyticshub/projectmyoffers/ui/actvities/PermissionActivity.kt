package com.intellyticshub.projectmyoffers.ui.actvities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intellyticshub.projectmyoffers.R
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : AppCompatActivity() {

    private val permissons = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        btnPerm.setOnClickListener {
            if (btnPerm.text == "Allow") {
                ActivityCompat.requestPermissions(
                    this,
                    permissons,
                    25
                )
            } else {
                val intent = Intent(this, MainActivity::class.java)
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 25) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Please Allow READ_SMS", Toast.LENGTH_SHORT).show()
            }
            if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Please Allow RECEIVE_SMS", Toast.LENGTH_SHORT).show()
            }

            if (checkPermissions(permissons))
                btnPerm.text = "Next >>"
        }

    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }
}
