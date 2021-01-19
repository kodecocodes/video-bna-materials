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

import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.raywenderlich.android.bubblesaffirmations.R
import com.raywenderlich.android.bubblesaffirmations.databinding.ActivityMainBinding
import com.raywenderlich.android.bubblesaffirmations.ui.quote.QuoteFragment
import com.raywenderlich.android.bubblesaffirmations.util.NavigationController

class MainActivity : AppCompatActivity(R.layout.activity_main),
    NavigationController {

  companion object {
    private const val FRAGMENT_QUOTE = "quote"
  }

  private lateinit var transition: Transition

  private lateinit var activityMainBinding: ActivityMainBinding


  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme_NoActionBar)
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)
    setSupportActionBar(activityMainBinding.toolbar)
    transition = TransitionInflater.from(this).inflateTransition(R.transition.app_bar)

    if (savedInstanceState == null) {
      supportFragmentManager.commitNow {
        replace(R.id.container, MainFragment.newInstance(
            intent?.getStringExtra(Intent.EXTRA_TEXT)))
      }
      intent?.let(::handleIntent)
    }
  }


  private fun handleIntent(intent: Intent) {
    when (intent.action) {
      // Invoked when a dynamic shortcut is tapped.
      Intent.ACTION_VIEW -> {
        val id = intent.data?.lastPathSegment?.toIntOrNull()
        if (id != null) {
          openQuote(id, null)
        }
      }
      // Invoked when app or contact selected from the share menu
      Intent.ACTION_SEND -> {
        val shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        val id = shortcutId?.filter { it.isDigit() }?.toInt()

        if (id != null) {
          openQuote(id, text)
        } else if (text != null) {
          openMain(text)
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intent?.let { handleIntent(it) }
  }

  override fun openQuote(id: Int, prepopulateText: String?) {
    supportFragmentManager.popBackStack(FRAGMENT_QUOTE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    supportFragmentManager.commit {
      addToBackStack(FRAGMENT_QUOTE)
      replace(R.id.container, QuoteFragment.newInstance(id, true, prepopulateText))
    }
  }

  override fun openMain(prepopulateText: String?) {
    supportFragmentManager.popBackStack(FRAGMENT_QUOTE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    supportFragmentManager.commit {
      addToBackStack(FRAGMENT_QUOTE)
      replace(R.id.container, MainFragment.newInstance(prepopulateText))
    }
  }


  override fun updateAppBar(
      showCategory: Boolean,
      hidden: Boolean,
      body: (name: TextView, icon: ImageView) -> Unit
  ) {

    if (hidden) {
      activityMainBinding.appBar.visibility = View.GONE
    } else {
      activityMainBinding.appBar.visibility = View.VISIBLE
      TransitionManager.beginDelayedTransition(activityMainBinding.appBar, transition)
      if (showCategory) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        activityMainBinding.name.visibility = View.VISIBLE
        activityMainBinding.icon.visibility = View.VISIBLE

      } else {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        activityMainBinding.name.visibility = View.GONE
        activityMainBinding.icon.visibility = View.GONE
      }
    }

    body(activityMainBinding.name, activityMainBinding.icon)
  }
}
