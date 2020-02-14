package com.example.test

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.*

class MainActivity : Activity() {
    var btnInternal: Button? = null
    var btnExternal: Button? = null
    var btnPrint: Button? = null
    var edtInput: EditText? = null
    var tvOutput: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnInternal = findViewById<View>(R.id.btnInternal) as Button
        btnExternal = findViewById<View>(R.id.btnExternal) as Button
        btnPrint = findViewById<View>(R.id.btnPrint) as Button
        edtInput = findViewById<View>(R.id.edtInput) as EditText
        tvOutput = findViewById<View>(R.id.tvOutput) as TextView
        btnInternal!!.setOnClickListener(listener)
        btnExternal!!.setOnClickListener(listener)
        btnPrint!!.setOnClickListener(listener)
    }

    var listener = View.OnClickListener { view ->
        val inputData = edtInput!!.text.toString()
        val isGrantStorage = grantExternalStoragePermission()
        when (view.id) {
            R.id.btnInternal -> {
                var fos: FileOutputStream? = null
                try {
                    fos = openFileOutput("internal.txt", Context.MODE_PRIVATE)
                    fos.write(inputData.toByteArray())
                    fos.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            R.id.btnExternal -> if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                if (isGrantStorage) {
                    val file = File(Environment.getExternalStorageDirectory(), "External.txt") // 파일 생성
                    try {
                        val fw = FileWriter(file, true) // 두번째 파라미터 true : 기존파일에 추가할지 여부
                        fw.write("클래스명: " + Thread.currentThread().stackTrace[2].className + "   메소드명: " + Thread.currentThread().stackTrace[2].methodName +
                                "   줄번호: " + Thread.currentThread().stackTrace[2].lineNumber + "   입력 값: " + inputData + "\n")
                        fw.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                Log.d(ContentValues.TAG, "External Storage is not ready")
            }
            R.id.btnPrint -> {
                val buffer = StringBuffer()
                var data: String? = null
                var fis: FileInputStream? = null
                try {
                    fis = openFileInput("internal.txt")
                    val iReader = BufferedReader(InputStreamReader(fis))
                    data = iReader.readLine()
                    while (data != null) {
                        buffer.append(data)
                        data = iReader.readLine()
                    }
                    buffer.append("\n")
                    iReader.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val path = Environment.getExternalStorageDirectory().toString() + "/External.txt"
                try {
                    val eReader = BufferedReader(FileReader(path))
                    data = eReader.readLine()
                    while (data != null) {
                        buffer.append(data)
                        data = eReader.readLine()
                    }
                    tvOutput!!.text = buffer.toString() + "\n"
                    eReader.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun grantExternalStoragePermission(): Boolean { // 권한 허용 되어 있는지 확인
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(ContentValues.TAG, "Permission is granted")
                true
            } else {
                Log.v(ContentValues.TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else {
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show()
            Log.d(ContentValues.TAG, "External Storage Permission is Grant ")
            true
        }
    }

    // 사용자에게 권한 요청
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(ContentValues.TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
                //resume tasks needing this permission
            }
        }
    }
}