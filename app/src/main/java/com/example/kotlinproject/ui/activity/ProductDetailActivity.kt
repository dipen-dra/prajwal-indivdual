package com.example.kotlinproject.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityProductDetailBinding
import com.example.kotlinproject.model.CartModel
import com.example.kotlinproject.model.ProductModel
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.repository.cart.CartRepoImpl
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.AuthViewModel
import com.example.kotlinproject.viewModel.CartViewModel
import com.squareup.picasso.Picasso

class ProductDetailActivity : AppCompatActivity() {


    lateinit var productDetailActivityBinding: ActivityProductDetailBinding

    lateinit var authViewModel: AuthViewModel

    lateinit var cartViewModel: CartViewModel

    lateinit var loadingUtils: LoadingUtils

    var quantity : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        productDetailActivityBinding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(productDetailActivityBinding.root)

        setSupportActionBar(productDetailActivityBinding.toolBarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "product detail"


        loadingUtils = LoadingUtils(this)
        var cartRepo = CartRepoImpl()
        cartViewModel = CartViewModel(cartRepo)

        var authRepo = AuthRepoImpl()
        authViewModel = AuthViewModel(authRepo)




        var products:ProductModel?=intent.getParcelableExtra("products")

        Picasso.get().load(products?.imageUrl).into(productDetailActivityBinding.imageViewDetail)





        productDetailActivityBinding.productNameDetail.text = products?.productName
        productDetailActivityBinding.descriptionDetail.text = products?.description
        productDetailActivityBinding.productPriceDetail.text = products?.price.toString()

        productDetailActivityBinding.btnAdds.setOnClickListener {
            if(quantity<100){
                quantity++
                productDetailActivityBinding.quantityDetail.text = quantity.toString()
            }

        }
        productDetailActivityBinding.btnSubtract.setOnClickListener {
            if(quantity>1){
                quantity--
                productDetailActivityBinding.quantityDetail.text = quantity.toString()

            }
        }

        productDetailActivityBinding.btnAddCart.setOnClickListener {
            addToCart(products)
        }









        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addToCart(products: ProductModel?) {
        loadingUtils.showDialog()
        var currentUser = authViewModel.getCurrentUser()
        var cartModel = CartModel("",
            products?.id.toString(),
            products?.price.toString().toInt(),
            products?.imageUrl.toString(),
            products?.productName.toString(),
            quantity,
            currentUser?.uid.toString()
        )

        cartViewModel.addCart(cartModel){
                success,message->
            if(success){
                loadingUtils.dismiss()
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                finish()
            }else{
                loadingUtils.dismiss()
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()

            }
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
