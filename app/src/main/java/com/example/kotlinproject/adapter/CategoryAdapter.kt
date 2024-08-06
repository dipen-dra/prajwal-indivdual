package com.example.kotlinproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinproject.R
import com.example.kotlinproject.model.CategoryModel
import com.example.kotlinproject.ui.activity.admin.UpdateCategoryActivity
import com.squareup.picasso.Picasso

class CategoryAdapter(var context: Context,var data:ArrayList<CategoryModel>):RecyclerView.Adapter<CategoryAdapter.categoryViewHolder>() {

    class categoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryName: TextView = view.findViewById(R.id.categoryName)
        var categoryDesc: TextView = view.findViewById(R.id.categoryDescription)
        var editlabel: TextView = view.findViewById(R.id.categoryEditLabel)

        var imageView: ImageView = view.findViewById(R.id.imageCategory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): categoryViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sample_category_admin, parent, false)
        return categoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: categoryViewHolder, position: Int) {
        holder.categoryName.text = data[position].categoryName
        holder.categoryDesc.text = data[position].categoryDescription
        Picasso.get().load(data[position].categoryImageUrl).into(holder.imageView)

        holder.editlabel.setOnClickListener {

            var intent = Intent(context, UpdateCategoryActivity::class.java)
            intent.putExtra("category", data[position])
            context.startActivity(intent)

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(category: List<CategoryModel>) {
        data.clear()
        data.addAll(category)
        notifyDataSetChanged()

    }

    fun getCategoryId(position: Int): String {
        return data[position].categoryId

    }

    fun getImageName(position: Int): String {
        return data[position].categoryImageName

    }
}