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

import androidx.lifecycle.*
import com.raywenderlich.android.bubblesaffirmations.App
import com.raywenderlich.android.bubblesaffirmations.model.Category
import com.raywenderlich.android.bubblesaffirmations.model.Quote
import com.raywenderlich.android.bubblesaffirmations.model.relations.QuoteAndCategory
import com.raywenderlich.android.bubblesaffirmations.repository.QuoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuoteViewModel : ViewModel() {
  private val repository: QuoteRepository by lazy { App.repository }
  private val categoryId = MutableLiveData<Int>()

  var foreground = false
    set(value) {
      field = value
      categoryId.value?.let { id ->
        if (value) {
          repository.activateQuotesForCategory(id)
        } else {
          repository.deactivateQuotesForCategory(id)
        }
      }
    }

  var quoteData: MutableLiveData<Quote> = categoryId.switchMap { id ->
    MutableLiveData<Quote>().apply {
      val currentQuoteId = repository.getCategoryById(id).currentQuoteId
      if (currentQuoteId > 0) {
        val quote = repository.getQuoteById(currentQuoteId)
        postValue(quote)
      } else {
        getNewQuote()
      }
    }
  } as MutableLiveData<Quote>

  val showAsBubbleVisible = categoryId.map { id ->
    repository.canBubble(id)
  }

  val category = categoryId.switchMap { id ->
    MutableLiveData<Category>().apply {
      postValue(repository.getCategoryById(id))
    }
  }

  fun setCategoryId(id: Int) {
    categoryId.value = id
    if (foreground) {
      repository.activateQuotesForCategory(id)
    } else {
      repository.deactivateQuotesForCategory(id)
    }
  }

  fun addQuote(quoteText: String) {
    val id = repository.addQuote(
        Quote(
            quoteText = "“$quoteText”",
            author = " - Shared",
            categoryId = categoryId.value ?: -1)
    )
    val quote = repository.getQuoteById(id.toInt())
    categoryId.value?.let { repository.updateCategoryCurrentQuote(id.toInt(), it) }
    quoteData.postValue(quote)
  }

  fun showAsBubble() {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
      delay(5000)
      quoteData.value?.let { quote ->
        category.value?.let { category ->
          QuoteAndCategory(quote, category)
        }
      }?.let { repository.showAsBubble(it) }
    }
  }

  fun dismissBubble() {
    categoryId.value?.let { repository.dismissBubble(it) }
  }

  fun getNewQuote() {
    val quote = categoryId.value?.let { repository.getQuoteByCategory(it) }
    if (quote != null) {
      repository.updateCategoryCurrentQuote(quote.id, quote.categoryId)
    }
    quoteData.postValue(quote)
  }

  override fun onCleared() {
    categoryId.value?.let { repository.deactivateQuotesForCategory(it) }
  }
}