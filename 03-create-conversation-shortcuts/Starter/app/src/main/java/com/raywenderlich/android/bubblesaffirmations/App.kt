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

package com.raywenderlich.android.bubblesaffirmations

import android.app.Application
import android.content.Context
import com.raywenderlich.android.bubblesaffirmations.database.QuoteDatabase
import com.raywenderlich.android.bubblesaffirmations.model.Category
import com.raywenderlich.android.bubblesaffirmations.model.Quote
import com.raywenderlich.android.bubblesaffirmations.repository.QuoteRepository
import com.raywenderlich.android.bubblesaffirmations.repository.QuoteRepositoryImpl
import com.raywenderlich.android.bubblesaffirmations.util.NotificationHelper
import java.util.concurrent.Executors


class App : Application() {

  companion object {
    private lateinit var instance: App
    private lateinit var context: Context
    private val categories = listOf<Category>(
        Category(1, "Motivation"),
        Category(2, "Inspiration"),
        Category(3, "Confidence"),
        Category(4, "Consistency")
    )


    private val database: QuoteDatabase by lazy {
      QuoteDatabase.buildDatabase(instance)
    }

    val repository: QuoteRepository by lazy {
      QuoteRepositoryImpl(
          database.quoteDao(),
          database.categoryDao(),
          NotificationHelper(
              categories, context).apply {
            setUpNotificationChannels()
          }
      )
    }
  }

  override fun onCreate() {
    super.onCreate()
    context = applicationContext
    instance = this

    if (repository.getCategories().isEmpty()) {
      repository.addCategories(
          categories
      )

      repository.addQuotes(
          listOf(
              // Motivation
              Quote(quoteText = "“Push yourself, because no one else is going to do it for you.”",
                  author = "  - Unknown", categoryId = 1),
              Quote(quoteText = "“Sometimes later becomes never. Do it now.”",
                  author = "  - Unknown", categoryId = 1),
              Quote(
                  quoteText = "“The only way to go fast, is to go well.”",
                  author = " -  Robert C Martin", categoryId = 1),
              Quote(
                  quoteText = "“Wake up with determination. Go to bed with satisfaction.”",
                  author = " -  Unknown", categoryId = 1),
              Quote(
                  quoteText = "“Do something today that your future self will thank you for.”",
                  author = " -  Unknown", categoryId = 1),
              Quote(
                  quoteText = "“The successful warrior is the average man, with laser-like focus.”",
                  author = " -  Bruce Lee", categoryId = 1),
              Quote(
                  quoteText = "“Just when the caterpillar thought the world was ending, he turned into a butterfly.”",
                  author = " -  Proverb", categoryId = 2),

              // Inspiration
              Quote(
                  quoteText = "“As long as you are learning, you are not failing.”",
                  author = "  - Bob Ross", categoryId = 2),
              Quote(
                  quoteText = "“Everyday life is like programming, I guess. " +
                      "If you love something you can put beauty into it.”",
                  author = " -  Donald Knuth", categoryId = 2),
              Quote(
                  quoteText = "“I'm a programmer. I like programming. And the best way I've found to " +
                      "have a positive impact on code is to write it.”",
                  author = " -  Robert C Martin", categoryId = 2),
              Quote(
                  quoteText = "“Dream it. Wish it. Do it.”",
                  author = " -  Unknown", categoryId = 2),
              Quote(
                  quoteText = "“Little things make big days.”",
                  author = " -  Unknown", categoryId = 2),
              Quote(
                  quoteText = "“Sometimes we’re tested not to show our weaknesses, but to discover our strengths.”",
                  author = " -  Unknown", categoryId = 2),
              Quote(
                  quoteText = "“Dream it. Believe it. Build it.”",
                  author = " -  Unknown", categoryId = 2),
              Quote(
                  quoteText = "“All our dreams can come true if we have the courage to pursue them.”",
                  author = " -  Walt Disney", categoryId = 2),
              Quote(
                  quoteText = "“Just when the caterpillar thought the world was ending, he turned into a butterfly.”",
                  author = " -  Proverb", categoryId = 2),
              Quote(
                  quoteText = "“The ones who are crazy enough to think they can change the world, are the ones who do.”",
                  author = " -  Anonymous", categoryId = 2),

              // Confidence
              Quote(quoteText = "“Great things never come from comfort zones.”",
                  author = "  - Unknown", categoryId = 3),
              Quote(
                  quoteText = "“If you can write \"hello world\" you can change the world.”",
                  author = " -  Raghu Venkatesh", categoryId = 3),
              Quote(
                  quoteText = "“Programming isn't about what you know; it's about what you can figure out.”",
                  author = " -  Robert C Martin", categoryId = 3),
              Quote(
                  quoteText = "“Not all roots are buried down in the ground, some " +
                      "are at the top of a tree.”",
                  author = " -  Robert C Martin", categoryId = 3),
              Quote(
                  quoteText = "“Dream bigger. Do bigger.”",
                  author = " -  Unknown", categoryId = 3),
              Quote(
                  quoteText = "“It’s going to be hard, but hard does not mean impossible.”",
                  author = " -  Unknown", categoryId = 3),
              Quote(
                  quoteText = "“Don’t wait for opportunity. Create it.”",
                  author = " -  Unknown", categoryId = 3),
              Quote(
                  quoteText = "“If you want to achieve greatness stop asking for permission.”",
                  author = " -  Anonymous", categoryId = 3),
              Quote(
                  quoteText = "“Good things come to people who wait, but better things come to those who go out and get them.”",
                  author = " -  Anonymous", categoryId = 3),

              // Consistency
              Quote(
                  quoteText = "“The perfect kind of architecture decision is the one which never has to be made.”",
                  author = " -  Robert C Martin", categoryId = 4),
              Quote(
                  quoteText = "“Is it possible that software is not like anything else, that it is meant" +
                      " to be discarded: that the whole point is to always see it as a soap bubble?”",
                  author = " -  Alan J Perlis", categoryId = 4),
              Quote(
                  quoteText = "“I'm not a great programmer; I'm just a good " +
                      "programmer with great habits.”",
                  author = "  - Kent Beck", categoryId = 4),
              Quote(
                  quoteText = "“I'm not a great programmer; I'm just a good " +
                      "programmer with great habits.”",
                  author = "  - Kent Beck", categoryId = 4),
              Quote(
                  quoteText = "“The harder you work for something, the greater you’ll feel when you achieve it.”",
                  author = "  - Unknown", categoryId = 4),
              Quote(
                  quoteText = "“The key to success is to focus on goals, not obstacles.”",
                  author = " -  Unknown", categoryId = 4),
              Quote(
                  quoteText = "“Things work out best for those who make the best of how things work out.”",
                  author = " -  John Wooden", categoryId = 3),
              Quote(
                  quoteText = "“Success is walking from failure to failure with no loss of enthusiasm.”",
                  author = " -  Winston Churchill", categoryId = 3),
              Quote(
                  quoteText = "“I have not failed. I've just found 10,000 ways that won't work.”",
                  author = " -  Thomas A. Edison", categoryId = 3),

              )
      )
    }
  }
}