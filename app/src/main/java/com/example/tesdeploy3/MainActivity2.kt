package com.example.tesdeploy3

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tesdeploy3.ml.SuspiciousHuman
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity2 : AppCompatActivity(), SurfaceHolder.Callback {

    lateinit var startButton: Button
    lateinit var stopButton: Button
    private var predictionActive = false

    private lateinit var model: SuspiciousHuman
    lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi model
        model = SuspiciousHuman.newInstance(this)

        // Memeriksa dan meminta izin kamera jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }

        // Menghubungkan SurfaceView dengan holder dan menambahkan callback
        surfaceView.holder.addCallback(this)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        startButton.setOnClickListener {
            startPrediction()
        }

        stopButton.setOnClickListener {
            stopPrediction()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (predictionActive) {
            // ...
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
        }
    }

    private fun startPrediction() {
        predictionActive = true
    }

    private fun stopPrediction() {
        predictionActive = false
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 30 * 64 * 64 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(64 * 64)
        bitmap.getPixels(pixels, 0, 64, 0, 0, 64, 64)

        for (pixelValue in pixels) {
            byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) - 128) / 128.0f)
            byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) - 128) / 128.0f)
            byteBuffer.putFloat((pixelValue and 0xFF - 128) / 128.0f)
        }

        return byteBuffer
    }

    override fun onDestroy() {
        // Membebaskan sumber daya model
        model.close()
        super.onDestroy()
    }
}
