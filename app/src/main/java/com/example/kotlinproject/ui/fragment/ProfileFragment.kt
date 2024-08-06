package com.example.kotlinproject.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.FragmentProfileBinding
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.ui.activity.EditProfileActivity
import com.example.kotlinproject.ui.activity.LoginActivity
import com.example.kotlinproject.ui.activity.admin.CategoryDashBoardActivity
import com.example.kotlinproject.ui.activity.admin.ProductDashboardActivity
import com.example.kotlinproject.viewModel.AuthViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class ProfileFragment : Fragment() {

    lateinit var profileFragmentBinding: FragmentProfileBinding
    lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileFragmentBinding = FragmentProfileBinding.inflate(layoutInflater)
        return profileFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var repo= AuthRepoImpl()
        authViewModel=AuthViewModel(repo)

        var currentUser=authViewModel.getCurrentUser()
        currentUser.let{
            authViewModel.fetchUserData(currentUser?.uid.toString())
        }
        profileFragmentBinding.editCategoryAdmin.setOnClickListener {
            var intent = Intent(requireContext(), CategoryDashBoardActivity::class.java)
            startActivity(intent)
        }
        profileFragmentBinding.editProfileCard.setOnClickListener {
            var intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("userData",authViewModel.userData.value)
            startActivity(intent)

        }
        profileFragmentBinding.editProductAdmin.setOnClickListener{
            var intent = Intent(requireContext(), ProductDashboardActivity::class.java)
            startActivity(intent)
        }
//        profileFragmentBinding.editCategoryAdmin.setOnClickListener {
//            var intent = Intent(requireContext(), EditProfileActivity::class.java)
//
//            startActivity(intent)
//
//        }


        authViewModel.userData.observe(viewLifecycleOwner) { users ->
            users?.let {
                if (users.imageUrl.isNullOrEmpty()) {
                    profileFragmentBinding.innerImage.setImageResource(R.drawable.profile)
                    profileFragmentBinding.progressBarImage.visibility = View.GONE
                } else {
                    Picasso.get().load(users.imageUrl).into(profileFragmentBinding.innerImage, object : Callback {
                        override fun onSuccess() {
                            profileFragmentBinding.progressBarImage.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            // Check if fragment is still attached to an activity
                            if (isAdded) {
                                Toast.makeText(requireContext(), e?.message, Toast.LENGTH_LONG).show()
                            } else {
                                // Handle case where fragment is not attached
                                // For example, log the error or silently ignore it
                            }
                        }
                    })
                }
                profileFragmentBinding.profileFullName.text = users.fullname.orEmpty()
                profileFragmentBinding.profileEmail.text = users.email.orEmpty()
            }
        }


        profileFragmentBinding.editLogout.setOnClickListener {
            authViewModel.logout{
                success,message->
                if(success){
                    var intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }else{
                    Toast.makeText(requireContext(),message, Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}