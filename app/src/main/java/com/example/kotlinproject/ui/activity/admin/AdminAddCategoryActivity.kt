package com.example.kotlinproject.ui.activity.admin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityAdminAddCategoryBinding
import com.example.kotlinproject.model.CategoryModel
import com.example.kotlinproject.repository.category.CategoryRepoImpl
import com.example.kotlinproject.utils.ImageUtils
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.CategoryViewModel
import com.squareup.picasso.Picasso
import java.util.UUID

class AdminAddCategoryActivity : AppCompatActivity() {
    lateinit var adminAddCategoryBinding: ActivityAdminAddCategoryBinding
    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null
    lateinit var loadingUtils: LoadingUtils
    lateinit var categoryViewModel: CategoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        adminAddCategoryBinding = ActivityAdminAddCategoryBinding.inflate(layoutInflater)
        setContentView(adminAddCategoryBinding.root)

        imageUtils = ImageUtils(this)
        loadingUtils = LoadingUtils(this)
        var repo = CategoryRepoImpl()
        categoryViewModel = CategoryViewModel(repo)
        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(adminAddCategoryBinding.imageView)
            }
        }
        adminAddCategoryBinding.imageViewCategoryBrowse.setOnClickListener {
            imageUtils.launchGallery(this)
        }

        adminAddCategoryBinding.btnCategoryAdd.setOnClickListener {

            if (imageUri == null) {
                Toast.makeText(applicationContext, "Please select image", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun uploadImage() {
        loadingUtils.showDialog()
        var imageName = UUID.randomUUID().toString()
        imageUri?.let { uri ->
            categoryViewModel.uploadImages(imageName, uri!!) { success, url, message ->
                if (success) {
                    addCategory(url, imageName)
                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    loadingUtils.dismiss()
                }
            }
        }
    }

    private fun addCategory(url: String?, imageName: String?) {
        loadingUtils.showDialog()
        val categoryName: String = adminAddCategoryBinding.editTextCategoryName.text.toString()
        val categoryDesc: String = adminAddCategoryBinding.editTextCategoryDesc.text.toString()

        val data = CategoryModel("", imageName.toString(), url.toString(), categoryName, categoryDesc)
        categoryViewModel.addCategory(data) { success, message ->
            if (success) {
                loadingUtils.dismiss()
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                loadingUtils.dismiss()
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()}
        }
    }
}
