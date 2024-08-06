package com.example.kotlinproject.repository.cart

import com.example.kotlinproject.model.CartModel

interface CartRepo {

    fun addCart(cartModel: CartModel, callback: (Boolean, String?) -> Unit)

    fun deleteCart(cartID:String,callback: (Boolean, String?) -> Unit)

    fun getCart(callback: (List<CartModel>?,Boolean, String?) -> Unit)

}