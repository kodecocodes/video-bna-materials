/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.bubblesaffirmations.ui.main

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.bubblesaffirmations.R
import com.raywenderlich.android.bubblesaffirmations.databinding.CategoryItemBinding
import com.raywenderlich.android.bubblesaffirmations.model.Category


class CategoryAdapter(
    private val onChatClicked: (id: Int) -> Unit
) : ListAdapter<Category, CategoryViewHolder>(DIFF_CALLBACK) {

  init {
    setHasStableIds(true)
  }

  override fun getItemId(position: Int): Long {
    return getItem(position).categoryId.toLong()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
      CategoryViewHolder {
    val holder = CategoryViewHolder(parent)
    holder.itemView.setOnClickListener {
      onChatClicked(holder.itemId.toInt())
    }
    return holder
  }

  override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
    val category: Category = getItem(position)
    holder.binding.icon.setImageIcon(Icon.createWithAdaptiveBitmapContentUri(category.iconUri))
    holder.binding.name.text = category.name
  }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Category>() {
  override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
    return oldItem.categoryId == newItem.categoryId
  }

  override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
    return oldItem == newItem
  }
}

class CategoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
) {
  val binding: CategoryItemBinding = CategoryItemBinding.bind(itemView)
}