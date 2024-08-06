package com.example.kotlinproject.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinproject.model.CategoryModel
import com.example.kotlinproject.repository.category.CategoryRepo

class CategoryViewModel(val repo: CategoryRepo) : ViewModel() {

    fun uploadImages(imageName: String, imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        repo.uploadImages(imageName, imageUri, callback)
    }

    fun addCategory(categoryModel: CategoryModel, callback: (Boolean, String?) -> Unit) {
        repo.addCategory(categoryModel, callback)
    }

    fun updateCategory(categoryId: String, data: MutableMap<String, Any?>, callback: (Boolean, String?) -> Unit) {
        repo.updateCategory(categoryId, data, callback)
    }

    fun deleteCategory(categoryId: String, callback: (Boolean, String?) -> Unit) {
        repo.deleteCategory(categoryId, callback)
    }

    private val _categoryData = MutableLiveData<List<CategoryModel>?>()
    val categoryData: MutableLiveData<List<CategoryModel>?>
        get() = _categoryData

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: MutableLiveData<Boolean>
        get() = _loadingState

    fun getAllCategories() {
        _loadingState.value = true
        repo.getAllCategory { categoryList, success, message ->
            if (categoryList != null) {
                _loadingState.value = false
                _categoryData.value = categoryList
            } else {
                _loadingState.value = false
                // Handle the case where categoryList is null
            }
        }
    }
    fun deleteImage(imageName:String,callback: (Boolean, String?) -> Unit){
        repo.deleteImage(imageName,callback)
    }
}
