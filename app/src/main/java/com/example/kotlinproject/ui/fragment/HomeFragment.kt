package com.example.kotlinproject.ui.fragment

import ShakeDetector
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinproject.R
import com.example.kotlinproject.adapter.user.CategoryUserAdapter
import com.example.kotlinproject.adapter.user.ProductUserAdapter
import com.example.kotlinproject.databinding.FragmentHomeBinding
import com.example.kotlinproject.repository.category.CategoryRepoImpl
import com.example.kotlinproject.repository.product.ProductRepoImpl
import com.example.kotlinproject.ui.activity.LoginActivity
import com.example.kotlinproject.viewModel.CategoryViewModel
import com.example.kotlinproject.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    lateinit var homeBinding: FragmentHomeBinding
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var productViewModel: ProductViewModel

    lateinit var categoryUserAdapter: CategoryUserAdapter
    lateinit var productUserAdapter: ProductUserAdapter

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(layoutInflater)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the sensor manager and other sensor-related properties
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector(requireContext()){
            requireActivity().runOnUiThread{
                showLogoutDialog()
            }
        }

        val categoryRepo = CategoryRepoImpl()
        categoryViewModel = CategoryViewModel(categoryRepo)
        categoryViewModel.getAllCategories()
        val productRepo = ProductRepoImpl()
        productViewModel = ProductViewModel(productRepo)
        productViewModel.getAllProduct()

        productUserAdapter = ProductUserAdapter(
            requireContext(),
            ArrayList()
        )

        categoryUserAdapter = CategoryUserAdapter(
            requireContext(),
            ArrayList()
        )

        productViewModel.productData.observe(viewLifecycleOwner) { product ->
            product?.let {
                productUserAdapter.updateData(it)
            }
        }

        categoryViewModel.categoryData.observe(viewLifecycleOwner) { category ->
            category?.let {
                categoryUserAdapter.updateData(it)
            }
        }

        homeBinding.recyclerViewCategoryUser.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryUserAdapter
        }

        homeBinding.recyclerViewProductUser.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productUserAdapter
        }

        categoryViewModel.loadingState.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                homeBinding.progressBarUserCategory.visibility = View.VISIBLE
            } else {
                homeBinding.progressBarUserCategory.visibility = View.GONE
            }
        }

        productViewModel.loadingState.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                homeBinding.progressBarUserProduct.visibility = View.VISIBLE
            } else {
                homeBinding.progressBarUserProduct.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accelerometer ->
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Do you want to logout?")
            setPositiveButton("Yes") { dialog, _ ->
                // Perform logout actions here
                performLogout()
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}
