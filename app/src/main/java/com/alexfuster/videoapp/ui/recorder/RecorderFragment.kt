package com.alexfuster.videoapp.ui.recorder

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.alexfuster.videoapp.R
import com.alexfuster.videoapp.app.constants.FileConfig
import com.alexfuster.videoapp.databinding.FragmentRecorderBinding
import com.alexfuster.videoapp.ui.main.MainActivity
import com.alexfuster.videoapp.ui.utils.getRecorderPermissions
import com.alexfuster.videoapp.ui.utils.isCameraPermissionsGranted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class RecorderFragment : Fragment() {

    private val viewModel: RecorderViewModel by viewModels()



    private var _binding: FragmentRecorderBinding? = null
    private val binding
        get() = _binding!!


    private var preview: Preview? = null
    private var videoCapture: VideoCapture? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private var isRecording = false;


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        //ViewBinding
        _binding = FragmentRecorderBinding.inflate(inflater, container, false)
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        (activity as MainActivity).supportActionBar?.hide()

        subscribeViewModelObservers()
        initButtonListeners()
        initData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRecorderPermissions(requireContext(), ::initCamera, ::showCameraPermissionsDeniedAlert)
    }


    override fun onStop() {
        super.onStop()
        if(isRecording) stopVideoRecorder()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeViewModelObservers() {
        viewModel.directoryPath.observe(viewLifecycleOwner, androidx.lifecycle.Observer { directoryPath ->
            outputDirectory = File(directoryPath)
        })
    }

    private fun initButtonListeners() {
        binding.buttonOpenGallery.setOnClickListener(
            Navigation.
            createNavigateOnClickListener(R.id.action_recorder_fragment_to_gallery_fragment))

        binding.cameraCaptureButton.setOnClickListener { takePhoto() }
        binding.videoCaptureButton.setOnClickListener { onRecordingButtonClick() }
    }

    private fun initData() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewModel.loadOutputDirectory()
    }

    private fun initCamera(){
        startCamera()
    }



    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Use cases
            preview = buildPreviewUseCase()
            imageCapture = buildImageCaptureUseCase()
            videoCapture = buildVideoCaptureUseCase()

            // Select back camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    imageCapture,
                    videoCapture,
                    preview)

                preview?.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun buildPreviewUseCase(): Preview {
        return Preview.Builder()
            .build()
    }

    private fun buildImageCaptureUseCase(): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()
    }

    @SuppressLint("RestrictedApi")
    private fun buildVideoCaptureUseCase(): VideoCapture {
        return VideoCaptureConfig.Builder().apply {
            setTargetRotation(binding.viewFinder.display.rotation)
        }.build()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FileConfig.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + FileConfig.PHOTO_EXTENSION)

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }


    @SuppressLint("RestrictedApi")
    private fun startVideo () {

        // Create timestamped output file to hold the image
        val videoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FileConfig.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + FileConfig.VIDEO_EXTENSION)


        val executor = Executor { Log.i(TAG, "Video was saved")  }
        videoCapture?.startRecording(videoFile, executor, object: VideoCapture.OnVideoSavedCallback{
            override fun onVideoSaved(file: File) {

            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Log.i(TAG, "Video capture error: $message")
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun stopVideo() {
        videoCapture?.stopRecording()
    }



    private fun onRecordingButtonClick() {
        if(isCameraPermissionsGranted(requireContext())) {
            if(isRecording)
                stopVideoRecorder()
            else
                startVideoRecorder()
        } else {
            getRecorderPermissions(requireContext(), ::initCamera,
                ::showCameraPermissionsDeniedAlert)
        }
    }

    private fun stopVideoRecorder() {
        binding.buttonOpenGallery.visibility = View.VISIBLE
        binding.timeChronometer.visibility = View.INVISIBLE
        binding.timeChronometer.stop()
        binding.videoCaptureButton.setBackgroundResource(R.drawable.recording_button)
        stopVideo()
        isRecording = false
    }

    private fun startVideoRecorder() {
        binding.buttonOpenGallery.visibility = View.INVISIBLE
        binding.timeChronometer.visibility = View.VISIBLE
        binding.timeChronometer.base = SystemClock.elapsedRealtime()
        binding.timeChronometer.start()
        binding.videoCaptureButton.setBackgroundResource(R.drawable.stop_button)
        startVideo()
        isRecording = true
    }


    private fun showCameraPermissionsDeniedAlert() {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.permission_alert_title)
            .setMessage(R.string.permission_alert_message)
            .setPositiveButton(R.string.common_ok) { _, _ -> }
            .show()
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }
}