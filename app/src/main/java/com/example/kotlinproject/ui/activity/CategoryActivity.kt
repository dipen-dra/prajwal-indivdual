package com.example.kotlinproject.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinproject.R
import com.example.kotlinproject.adapter.user.CategoryUserAdapter
import com.example.kotlinproject.adapter.user.ProductUserAdapter
import com.example.kotlinproject.databinding.ActivityCategoryBinding
import com.example.kotlinproject.model.CategoryModel
import com.example.kotlinproject.repository.product.ProductRepoImpl
import com.example.kotlinproject.viewModel.ProductViewModel

class CategoryActivity : AppCompatActivity() {

    lateinit var categoryBinding: ActivityCategoryBinding

    lateinit var productViewModel: ProductViewModel

    lateinit var productUserAdapter: ProductUserAdapter

    lateinit var categoryUserAdapter: CategoryUserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        categoryBinding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(categoryBinding.root)

        setSupportActionBar(categoryBinding.toolBarCategory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var repo = ProductRepoImpl()
        productViewModel = ProductViewModel(repo)

        productUserAdapter = ProductUserAdapter(
           this@CategoryActivity,
            ArrayList()
        )

        categoryUserAdapter = CategoryUserAdapter(
            this@CategoryActivity,
            ArrayList()
        )

        productViewModel.productByCategory.observe(this){product->
            product?.let {
                productUserAdapter.updateData(it)
            }
        }

        categoryBinding.recyclerCategoryProduct.apply {
            layoutManager = GridLayoutManager(this@CategoryActivity,2)

            adapter = categoryUserAdapter

        }


        var category:CategoryModel?=intent.getParcelableExtra("category")
        title = category?.categoryName.toString()
        productViewModel.getAllProductByCategory(category?.categoryName.toString())

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

