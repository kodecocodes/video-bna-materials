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

package com.raywenderlich.android.bubblesaffirmations.repository

import com.raywenderlich.android.bubblesaffirmations.database.CategoryDao
import com.raywenderlich.android.bubblesaffirmations.database.QuoteDao
import com.raywenderlich.android.bubblesaffirmations.model.Category
import com.raywenderlich.android.bubblesaffirmations.model.Quote
import com.raywenderlich.android.bubblesaffirmations.model.relations.QuoteAndCategory
import com.raywenderlich.android.bubblesaffirmations.util.NotificationHelper

interface QuoteRepository {
  fun getQuotes(): List<QuoteAndCategory>
  fun addQuotes(quotes: List<Quote>)
  fun addCategories(categories: List<Category>)
  fun getCategories(): List<Category>
  fun getCategoryById(categoryId: Int): Category
  fun getQuotesByCategory(quoteCategory: Int): List<QuoteAndCategory>
  fun getQuoteByCategory(quoteCategory: Int): Quote
  fun activateQuotesForCategory(id: Int)
  fun deactivateQuotesForCategory(id: Int)
  fun showAsBubble(quote: QuoteAndCategory)
  fun canBubble(id: Int): Boolean
  fun sendQuoteAsNotification(quoteAndCategory: QuoteAndCategory)
  fun updateCategoryCurrentQuote(currentQuoteId: Int, categoryId: Int)
  fun dismissBubble(id: Int)
  fun getQuoteById(id: Int): Quote
  fun addQuote(quote: Quote): Long
}

class QuoteRepositoryImpl internal constructor(
    private val quoteDao: QuoteDao,
    private val categoryDao: CategoryDao,
    private val notificationHelper: NotificationHelper
) : QuoteRepository {

  private var currentCategory: Int = 0


  override fun showAsBubble(quote: QuoteAndCategory) {
    notificationHelper.showNotification(quote, true)
  }

  override fun sendQuoteAsNotification(quoteAndCategory: QuoteAndCategory) {
    notificationHelper.showNotification(quoteAndCategory, false)
  }

  override fun canBubble(id: Int): Boolean {
    val category = getCategoryById(id)
    return notificationHelper.canBubble(category)
  }

  override fun dismissBubble(id: Int) {
    notificationHelper.dismissNotification(id)
  }

  override fun getQuotes(): List<QuoteAndCategory> =
      quoteDao.getQuotes().map {
        QuoteAndCategory(it, categoryDao.getCategoryById(it.categoryId))
      }

  override fun getQuotesByCategory(quoteCategory: Int): List<QuoteAndCategory> =
      quoteDao.getQuotesByCategory(quoteCategory).map {
        QuoteAndCategory(it, categoryDao.getCategoryById(it.categoryId))
      }

  override fun getQuoteByCategory(quoteCategory: Int): Quote =
      quoteDao.getQuoteByCategory(quoteCategory)

  override fun activateQuotesForCategory(id: Int) {
    currentCategory = id
    notificationHelper.dismissNotification(id)
  }

  override fun deactivateQuotesForCategory(id: Int) {
    if (currentCategory == id) {
      currentCategory = 0
    }
  }

  override fun updateCategoryCurrentQuote(currentQuoteId: Int, categoryId: Int) =
      categoryDao.updateCategoryCurrentQuote(currentQuoteId, categoryId)



  override fun getQuoteById(id: Int): Quote =
      quoteDao.getQuoteById(id)

  override fun addQuote(quote: Quote): Long = quoteDao.addQuote(quote)


  override fun getCategoryById(categoryId: Int): Category = categoryDao.getCategoryById(categoryId)
  override fun addQuotes(quotes: List<Quote>) = quoteDao.addQuotes(quotes)
  override fun addCategories(categories: List<Category>) = categoryDao.addCategories(categories)
  override fun getCategories(): List<Category> = categoryDao.getCategories()

}
