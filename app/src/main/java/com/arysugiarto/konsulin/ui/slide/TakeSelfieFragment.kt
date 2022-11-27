package com.arysugiarto.konsulin.ui.slide

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.arysugiarto.konsulin.R
import com.arysugiarto.konsulin.data.remote.Result
import com.arysugiarto.konsulin.databinding.FragmentConditionBinding
import com.arysugiarto.konsulin.databinding.FragmentHomeBinding
import com.arysugiarto.konsulin.databinding.FragmentTakeSlefieBinding
import com.arysugiarto.konsulin.databinding.FragmentYearsBinding
import com.arysugiarto.konsulin.ui.home.HomeFragmentDirections
import com.arysugiarto.konsulin.ui.main.ImagePickerActivity
import com.arysugiarto.konsulin.ui.main.MainFragment.Companion.parentNavigation
import com.arysugiarto.konsulin.util.*
import com.google.android.gms.common.Scopes.PROFILE
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class TakeSelfieFragment : Fragment(R.layout.fragment_take_slefie) {

    private val binding by viewBinding<FragmentTakeSlefieBinding>()

    private var imageOrigin = emptyString

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCallback()
        initOnClick()
        forceFullscreenStatusBar()
        parentNavigation?.isVisible = false


    }

    private fun forceFullscreenStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(
                WindowInsets.Type.statusBars()
            )
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun initViewModel(){
    }

    private fun initCallback() {
    }

    private fun photoPicker() {
        val params =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val pickerActivity = ImagePickerActivity()

        if (EasyPermissions.hasPermissions(requireContext(), *params)) {
            pickerActivity.showImagePickerOptions(requireContext(), object :
                ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }
                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.image_picker_permission_access_camera),
                REQUEST_CAMERA_WRITE,
                *params
            )
        }
    }

    private fun launchCameraIntent() {
        val intent = Intent(context, ImagePickerActivity::class.java).apply {
            putExtra(
                ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
                ImagePickerActivity.REQUEST_IMAGE_CAPTURE
            )

            when (imageOrigin) {
                PROFILE -> {
                    putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
                    putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1)
                    putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
                }
            }
        }

        startActivityForResult(intent, REQUEST_CAMERA_WRITE)

    }

    private fun launchGalleryIntent() {
        val intent = Intent(context, ImagePickerActivity::class.java).apply {
            when (imageOrigin) {
                PROFILE -> {
                    putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
                    putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1)
                    putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
                }
            }
        }

        startActivityForResult(intent, READ_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data.let { result ->
            if (requestCode == READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result?.getParcelableExtra(Const.General.path)
                uri?.copyAndGetPath(requireContext())
                    ?.fileOf()
                    ?.let { file ->
                        when (imageOrigin) {
//                            PROFILE -> binding.ivProfile.apply {
//                                loadImage(
//                                    uri.toString(),
//                                    ImageCornerOptions.ROUNDED
//                                )
//                                background = null
//                                binding.tvUploadProfile.isVisible = false
//                            }
                        }
                    }
            }

            if (requestCode == REQUEST_CAMERA_WRITE && resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result?.getParcelableExtra(Const.General.path)
                uri?.copyAndGetPath(requireContext())
                    ?.fileOf()
                    ?.let { file ->
                        when (imageOrigin) {
//                            PROFILE -> binding.ivProfile.apply {
//                                loadImage(
//                                    uri.toString(),
//                                    ImageCornerOptions.ROUNDED
//                                )
//                                background = null
//                                binding.tvUploadProfile.isVisible = false
//                            }

                        }

                    }
            }
        }
    }

    private fun initOnClick() {
        binding.apply {
            button.setOnClickListener(onClickCallback)
        }
    }

    private val onClickCallback = View.OnClickListener { view ->
        when (view) {
            binding.button -> photoPicker()

        }
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE = 11
        private const val REQUEST_CAMERA_WRITE = 12
    }
}