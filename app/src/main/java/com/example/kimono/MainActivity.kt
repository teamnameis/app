package com.example.kimono

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Base64
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.databinding.DataBindingUtil
import com.example.kimono.api.Frame
import com.example.kimono.api.HttpApi
import com.example.kimono.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    companion object {
        val a = "AAA"
        fun createIntent(context: Context, id: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(a, id)
            }
        }

        fun MainActivity.parseId(): Int {
            return intent.getIntExtra(a, 0)
        }
    }

    private val id :Int by lazy {
        parseId()
    }
    private lateinit var binding: ActivityMainBinding
    private val disposable = CompositeDisposable()

    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        startCameraWithPermissionCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startCamera() {
        // Create configuration object for the viewfinder use case
//        val previewConfig = PreviewConfig.Builder().apply {
//            setTargetResolution(Size(640, 480))
//        }.build()
//
//
//        // Build the viewfinder use case
//        val preview = Preview(previewConfig)
//
//        // Every time the viewfinder is updated, recompute layout
//        preview.setOnPreviewOutputUpdateListener {
//
//            // To update the SurfaceTexture, we have to remove it and re-add it
////            val parent = binding.viewFinder.parent as ViewGroup
////            parent.removeView(binding.viewFinder)
////            parent.addView(binding.viewFinder, 0)
////
////            binding.viewFinder.surfaceTexture = it.surfaceTexture
//            updateTransform()
//        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
//        CameraX.bindToLifecycle(this, preview)
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()
//        val imageCapture = ImageCapture(imageCaptureConfig)
//        binding.capt1
//        ureButton.setOnClickListener {
            //            imageCapture.takePicture(ThreadPerTaskExecutor(),
//                    object : ImageCapture.OnImageCapturedListener() {
//                        override fun onCaptureSuccess(image: ImageProxy?, rotationDegrees: Int) {
            // insert your code here.
//                            Api.sendApi.send(Flame.newBuilder().setData(ByteString.EMPTY).build())
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribeBy(onSuccess = {
//                                        Timber.d(it.toString() + "hogehoge")
//                                    }, onError = {
//                                        Timber.e(it)
//                                    }).addTo(disposable)
//                        }

//                        override fun onError(
//                                imageCaptureError: ImageCapture.ImageCaptureError,
//                                message: String,
//                                cause: Throwable?
//                        ) {
//                            Timber.e(cause)
//                        }
//                    })
//        }
        a()
//        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun a() {
        val imageAnalysisConfig = ImageAnalysisConfig.Builder()
            .setTargetResolution(Size(1280, 720))
            .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            .build()
        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

        imageAnalysis.setAnalyzer({
            binding.viewFinder2.post(it)
        }, { image: ImageProxy, rotationDegrees: Int ->
            // insert your code here.
            val planes = image.planes
            if (planes.size == 3 && !isLoading) {
                isLoading = true
                val yBuffer = planes[0].buffer // Y
                val uBuffer = planes[1].buffer // U
                val vBuffer = planes[2].buffer // V

                val ySize = yBuffer.remaining()
                val uSize = uBuffer.remaining()
                val vSize = vBuffer.remaining()

                val nv21 = ByteArray(ySize + uSize + vSize)

                //U and V are swapped
                yBuffer.get(nv21, 0, ySize)
                vBuffer.get(nv21, ySize, vSize)
                uBuffer.get(nv21, ySize + vSize, uSize)

                val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
                val out = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
                val encoded = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)

                HttpApi.sendApi.send(Frame(id, encoded)).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onSuccess = {
                        Timber.d("success! ${it.data}")
                        isLoading = false
                        val decode = Base64.decode(it.data, Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(decode, 0, decode.size)
                        binding.viewFinder2.setImageBitmap(bmp)
//                            image.close()
                    }, onError = {
                        Timber.e(it)
                        isLoading = false
//                            image.close()
                    }).addTo(disposable)

                Timber.d("come on")

//                Api.sendApi.send(frame).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(onSuccess = {
//                            Timber.d("success! ${it.data}")
//                            isLoading = false
////                            image.close()
//                        }, onError = {
//                            Timber.e(it)
//                            isLoading = false
////                            image.close()
//                        }).addTo(disposable)
            } else {
                Timber.e("access!?")
//                image.close()
            }
        })

        CameraX.bindToLifecycle(this, imageAnalysis)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = binding.viewFinder2.width / 2f
        val centerY = binding.viewFinder2.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (binding.viewFinder2.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
//        binding.viewFinder2.setTransform(matrix)
    }

    class ThreadPerTaskExecutor() : Executor {
        override fun execute(command: Runnable) {
            Thread(command).start()
        }
    }
}
