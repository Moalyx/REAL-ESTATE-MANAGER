package com.tuto.realestatemanager.ui.addphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentAddPhotoDialogBinding
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyViewModel
import com.tuto.realestatemanager.ui.utils.RealPathUtil
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPhotoDialogFragment : Fragment() {

    companion object {
        private const val INTENT_REQUEST_CODE = 100
        private const val PERMISSION_REQUEST_CODE = 200
        private const val RESULT_DATA_OK = 300
    }

    private val viewModel by viewModels<AddPhotoDialogFragmentViewModel>()
    private var _binding: FragmentAddPhotoDialogBinding? = null
    private val binding get() = _binding!!
    private var title: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddPhotoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchIntent()

        binding.title.doAfterTextChanged {
            title = it.toString()
        }






    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCodes: Int, resultCodes: Int, data: Intent?) {
        super.onActivityResult(requestCodes, resultCodes, data)
        if (requestCodes == AddPhotoDialogFragment.INTENT_REQUEST_CODE && resultCodes == AppCompatActivity.RESULT_OK && data != null) {
            val uri: Uri = data.data!!
//            if (isFromCamera) {

//                val photo = intent.extras
//                viewModel.createTemporaryPhoto(photo.toString())

//            }else{
            val realPath: String? = RealPathUtil.getRealPathFromURI_API19(requireContext(), uri)
            //viewModel.createTemporaryPhoto(realPath!!)
//            }
            //val description = binding.address.text.toString()

            //val photo = intent.extras
            Glide.with(binding.image)
                .load(realPath)
                .into(binding.image)

//            binding.title.doAfterTextChanged {
//                title + it.toString()
//            }

            binding.addPictureButton.setOnClickListener{
                viewModel.onAddTemporaryPhoto(title, realPath!!)
                startActivity(Intent(context, CreatePropertyActivity::class.java))
            }


            //list.add(realPath!!)
            //list.add(photo.toString())
//            if(realPath != null){
//                viewModel.createTemporaryPhoto(realPath)
//            }else{
//                Toast.makeText(this, "no photo", Toast.LENGTH_SHORT).show()
//            }
        } else {
            Toast.makeText(requireContext(), "vous n'avez pas les autorisations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent() {
        //isFromCamera = false
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Sélectionnez une photo"),
            AddPhotoDialogFragment.INTENT_REQUEST_CODE
        )
    }
}