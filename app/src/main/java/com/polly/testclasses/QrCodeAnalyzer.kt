package com.polly.testclasses

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.polly.visuals.BarcodeBoxView
import com.polly.visuals.BarcodeScannerActivity

class QrCodeAnalyzer(
    private val context: Context,
    private val barcodeBoxView: BarcodeBoxView,
    private val previewViewWidth: Float,
    private val previewViewHeight: Float,

) : ImageAnalysis.Analyzer {
    private var alreadyDetected = ArrayList<String>()

    /**
     * This parameters will handle preview box scaling
     */
    private var scaleX = 1f
    private var scaleY = 1f
    private fun translateX(x: Float) = x * scaleX
    private fun translateY(y: Float) = y * scaleY

    private fun adjustBoundingRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image

        if (img != null) {
            // Update scale factors
            scaleX = previewViewWidth / img.height.toFloat()
            scaleY = previewViewHeight / img.width.toFloat()

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            // Process image searching for barcodes
            val options = BarcodeScannerOptions.Builder()
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        for (barcode in barcodes) {
                            // Handle received barcodes...
                                if(!alreadyDetected.contains(barcode.rawValue)) {
                                    Toast.makeText(
                                        context,
                                        "Value: " + barcode.rawValue,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    // Update bounding rect
                                    barcode.boundingBox?.let { rect ->
                                        barcodeBoxView.setRect(
                                            adjustBoundingRect(
                                                rect
                                            )
                                        )
                                    }
                                    when(barcode.rawValue.toCharArray()[0]){
                                        '1' -> BarcodeScannerActivity.votes1++
                                        '2' -> BarcodeScannerActivity.votes2++
                                        '3' -> BarcodeScannerActivity.votes3++
                                        '4' -> BarcodeScannerActivity.votes4++

                                    }
                                    alreadyDetected.add(barcode.rawValue)
                                }

                        }
                    } else {
                        // Remove bounding rect
                        barcodeBoxView.setRect(RectF())
                    }
                }
                .addOnFailureListener { }
        }

        image.close()
    }
}