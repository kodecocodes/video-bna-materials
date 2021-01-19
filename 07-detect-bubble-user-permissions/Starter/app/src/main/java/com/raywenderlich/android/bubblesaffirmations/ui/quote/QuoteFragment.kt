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
package com.raywenderlich.android.bubblesaffirmations.ui.quote

import android.content.LocusId
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.raywenderlich.android.bubblesaffirmations.R
import com.raywenderlich.android.bubblesaffirmations.databinding.QuoteFragmentBinding
import com.raywenderlich.android.bubblesaffirmations.util.getNavigationController

class QuoteFragment : Fragment(R.layout.quote_fragment) {
  companion object {
    private const val ARG_ID = "id"
    private const val ARG_FOREGROUND = "foreground"
    private const val ARG_PREPOPULATE_TEXT = "prepopulate_text"
    private lateinit var quoteFragmentBinding: QuoteFragmentBinding

    fun newInstance(id: Int, foreground: Boolean, prepopulateText: String? = null) =
        QuoteFragment().apply {
          arguments = Bundle().apply {
            putInt(ARG_ID, id)
            putBoolean(ARG_FOREGROUND, foreground)
            putString(ARG_PREPOPULATE_TEXT, prepopulateText)
          }
        }
  }

  private val viewModel: QuoteViewModel by viewModels()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val id = arguments?.getInt(ARG_ID)
    val prepopulateText = arguments?.getString(ARG_PREPOPULATE_TEXT)

    if (id == null) {
      parentFragmentManager.popBackStack()
      return
    }
    val navigationController = getNavigationController()
    viewModel.setCategoryId(id)

    viewModel.category.observe(viewLifecycleOwner) { category ->
      if (category == null) {
        Toast.makeText(view.context, "Category not found", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
      } else {
        requireActivity().setLocusContext(LocusId(category.shortcutId), null)
        navigationController.updateAppBar { name, icon ->
          name.text = category.name
          icon.setImageIcon(Icon.createWithAdaptiveBitmapContentUri(category.iconUri))
          startPostponedEnterTransition()
        }
      }
    }

    quoteFragmentBinding.yesButton.setOnClickListener {
      viewModel.getNewQuote()
    }

    viewModel.quoteData.observe(viewLifecycleOwner) { quote ->
      quoteFragmentBinding.quoteTextView.text = quote.quoteText
      quoteFragmentBinding.authorTextView.text = quote.author
    }

    if (prepopulateText != null) {
      viewModel.addQuote(prepopulateText)
    }

    quoteFragmentBinding.noButton.setOnClickListener {
      if (!viewModel.foreground)
        viewModel.dismissBubble()
      else
        parentFragmentManager.popBackStack()
    }
  }

  override fun onStart() {
    super.onStart()
    val foreground = arguments?.getBoolean(ARG_FOREGROUND) == true
    viewModel.foreground = foreground
  }

  override fun onStop() {
    super.onStop()
    viewModel.foreground = false
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.quote_menu, menu)
    menu.findItem(R.id.action_show_as_bubble)?.let { item ->
      viewModel.showAsBubbleVisible.observe(viewLifecycleOwner) { visible ->
        if (visible)
          item.setIcon(R.drawable.ic_bubble)
        else
          item.setIcon(R.drawable.ic_notification)
      }
    }
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_show_as_bubble -> {
        viewModel.showAsBubble()
        if (isAdded) {
          parentFragmentManager.popBackStack()
        }
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    quoteFragmentBinding = QuoteFragmentBinding.inflate(layoutInflater)
    return quoteFragmentBinding.root
  }
}
