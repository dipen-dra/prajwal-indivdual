package com.example.kotlinproject.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlinproject.R
import com.example.kotlinproject.adapter.user.CategoryUserAdapter
import com.example.kotlinproject.adapter.user.ProductUserAdapter
import com.example.kotlinproject.databinding.ActivityCategoryBinding
import com.example.kotlinproject.model.CategoryModel
import com.example.kotlinproject.repository.product.ProductRepoImpl
import com.example.kotlinproject.viewModel.ProductViewModel

class CategoryActivity : AppCompatActivity() {

    private lateinit var categoryBinding: ActivityCategoryBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productUserAdapter: ProductUserAdapter
    private lateinit var categoryUserAdapter: CategoryUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        categoryBinding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(categoryBinding.root)

        setSupportActionBar(categoryBinding.toolBarCategory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repo = ProductRepoImpl()
        productViewModel = ProductViewModel(repo)

        productUserAdapter = ProductUserAdapter(
            this@CategoryActivity,
            ArrayList()
        )

        categoryUserAdapter = CategoryUserAdapter(
            this@CategoryActivity,
            ArrayList()
        )

        categoryBinding.recyclerCategoryProduct.apply {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            adapter = productUserAdapter // Use productUserAdapter to show products
        }

        val category: CategoryModel? = intent.getParcelableExtra("category")
        title = category?.categoryName.toString()
        Log.d("CategoryActivity", "Category: ${category?.categoryName}")

        // Fetch products for the selected category
        productViewModel.getAllProductByCategory(category?.categoryName.toString())

        // Observe the LiveData for products
        productViewModel.productByCategory.observe(this) { products ->
            if (products.isNullOrEmpty()) {
                // No products, show the message
                categoryBinding.categoryProductCheck.visibility = View.VISIBLE
                productUserAdapter.updateData(emptyList())
            } else {
                // Products available, hide the message and update the adapter
                categoryBinding.categoryProductCheck.visibility = View.GONE
                productUserAdapter.updateData(products)
            }
        }

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close this activity and return to the previous one
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
