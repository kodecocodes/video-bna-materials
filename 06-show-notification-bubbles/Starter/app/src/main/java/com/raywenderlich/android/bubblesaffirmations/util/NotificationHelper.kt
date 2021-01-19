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

package com.raywenderlich.android.bubblesaffirmations.util

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.raywenderlich.android.bubblesaffirmations.R
import com.raywenderlich.android.bubblesaffirmations.model.Category
import com.raywenderlich.android.bubblesaffirmations.model.relations.QuoteAndCategory
import com.raywenderlich.android.bubblesaffirmations.ui.main.MainActivity
import com.raywenderlich.android.bubblesaffirmations.ui.quote.QuoteBubbleActivity
import java.lang.System.currentTimeMillis
import java.util.*

class NotificationHelper(
    private val categories: List<Category>,
    private val context: Context
) {
  companion object {
    private const val CHANNEL_QUOTES = "quotes"
    private const val REQUEST_CONTENT = 1
    private const val REQUEST_BUBBLE = 2
  }

  private fun createPerson(icon: Icon, category: Category): Person {
    return Person.Builder()
            .setName(category.name)
            .setIcon(icon)
            .build()
  }

  private fun createDynamicShortcutIntent(category: Category): Intent =
          Intent(context, MainActivity::class.java)
                  .setAction(Intent.ACTION_VIEW)
                  .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                  .setData(
                          Uri.parse(
                                  "https://raywenderlich.android.bubblesaffirmations.com"
                                          + "/quote/${category.categoryId}"
                          )
                  )

  private fun createShortcuts() = categories.map { category ->
    ShortcutInfo.Builder(context, category.shortcutId)
            // 1
            .setLocusId(LocusId(category.shortcutId))
            .setActivity(ComponentName(context, MainActivity::class.java))
            .setShortLabel(category.name)
            .setIcon(createShortcutIcon(category))
            // 2
            .setLongLived(true)
            // 3
            .setCategories(
                    setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET")
            )
            .setIntent(createDynamicShortcutIntent(category))
            // 4
            .setPerson(createPerson(createIcon(category), category)
            ).build()
  }

  private fun createShortcutIcon(category: Category): Icon{
    return  Icon.createWithAdaptiveBitmap(
            context.resources.assets.open(
                    "${category.name.toLowerCase(Locale.ROOT)}.png").use {
              input ->
              BitmapFactory.decodeStream(input)
            }
    )
  }

  // Get the notification manager
  private val notificationManager: NotificationManager =
      context.getSystemService() ?: throw IllegalStateException()

  // Get the shortcut manager
  private val shortcutManager: ShortcutManager =
      context.getSystemService() ?: throw IllegalStateException()

  // Dismiss a notification
  fun dismissNotification(id: Int) {

  }

  // sets up the notification channels
  fun setUpNotificationChannels() {
    if (notificationManager.getNotificationChannel(
            CHANNEL_QUOTES) == null) {
      val channel = NotificationChannel(
          CHANNEL_QUOTES,
          context.getString(
              R.string.channel_quotes),
          NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = context.getString(
            R.string.channel_quotes_description)
      }
      notificationManager.createNotificationChannel(channel)
    }
    updateShortcuts(null)
  }

  // Update the shortcuts so the most frequently used are at the bottom
  private fun updateShortcuts(importantCategory: Category?) {
    var shortcuts = createShortcuts()

    if (importantCategory != null){
      shortcuts = shortcuts.sortedByDescending { it.id == importantCategory.shortcutId }
    }

    val maxCount = shortcutManager.maxShortcutCountPerActivity
    if (shortcuts.size > maxCount){
      shortcuts = shortcuts.take(maxCount)
    }
    shortcutManager.addDynamicShortcuts(shortcuts)
  }

  fun showNotification(quoteAndCategory: QuoteAndCategory, fromUser: Boolean) {
    // Create Icon
    val icon = createIcon(quoteAndCategory.category)

    // Create the Person
    val person = createPerson(icon, quoteAndCategory.category)

    // Create the Notification
    val notification = createNotification(quoteAndCategory, icon, person)

    // Build and Display the Notification
    notificationManager.notify(quoteAndCategory.category.categoryId, notification.build())
  }

  // Create an icon
  private fun createIcon(category: Category): Icon =
      Icon.createWithAdaptiveBitmapContentUri(category.iconUri)


  // Create the notification
  private fun createNotification(
      quoteAndCategory: QuoteAndCategory,
      icon: Icon,
      person: Person
  ): Notification.Builder {
    return Notification.Builder(context, CHANNEL_QUOTES)
            // 1
            .setContentTitle(quoteAndCategory.category.name)
            .setSmallIcon(icon)
            // 2
            .setShortcutId(quoteAndCategory.category.shortcutId)
            .setLocusId(LocusId(quoteAndCategory.category.shortcutId))
            .setCategory(Notification.CATEGORY_MESSAGE)
            // 3
            .setStyle(Notification.MessagingStyle(person)
                    .setGroupConversation(false)
                    .addMessage(quoteAndCategory.quote.quoteText,
                            currentTimeMillis(), person)
            )
            // 4
            .setShowWhen(true)
            .setContentIntent(createPendingMainIntent(
                    REQUEST_CONTENT,
                    quoteAndCategory.category)
            )
  }

  // create the pending intent for the notification when it doesn't bubble
  private fun createPendingMainIntent(requestCode: Int, category: Category):
      PendingIntent {
    val contentUri =
        ("https://raywenderlich.android.bubblesaffirmations.com/quote/" +
            "${category.categoryId}")
            .toUri()
    return PendingIntent.getActivity(
        context,
        requestCode,
        Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .setData(contentUri),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
  }

  // Determines if this notification channel and shortcut id can bubble
  fun canBubble(category: Category): Boolean {
    return false
  }
}


