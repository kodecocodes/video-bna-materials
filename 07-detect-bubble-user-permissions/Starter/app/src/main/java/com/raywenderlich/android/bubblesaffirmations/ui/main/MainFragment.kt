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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.bubblesaffirmations.databinding.MainFragmentBinding
import com.raywenderlich.android.bubblesaffirmations.util.getNavigationController


class MainFragment : Fragment() {
  private lateinit var mainFragmentBinding: MainFragmentBinding

  companion object {
    private const val ARG_PREPOPULATE_TEXT = "prepopulate_text"

    fun newInstance(prepopulateText: String? = null): MainFragment {
      return MainFragment().apply {
        arguments = Bundle().apply {
          putString(ARG_PREPOPULATE_TEXT, prepopulateText)
        }
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val binding: MainFragmentBinding = MainFragmentBinding.inflate(
        inflater, container, false)
    mainFragmentBinding = binding
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val navigationController = getNavigationController()
    val prepopulateText = arguments?.getString(ARG_PREPOPULATE_TEXT)
    navigationController.updateAppBar(false)
    val viewModel: MainViewModel by viewModels()

    val categoryAdapter = CategoryAdapter { categoryId ->
      navigationController.openQuote(categoryId, prepopulateText)
    }

    viewModel.categoriesData.observe(viewLifecycleOwner, { categories ->
      categoryAdapter.submitList(categories)
    })

    mainFragmentBinding.categories.run {
      layoutManager = LinearLayoutManager(view.context)
      setHasFixedSize(true)
      adapter = categoryAdapter
    }
  }
}