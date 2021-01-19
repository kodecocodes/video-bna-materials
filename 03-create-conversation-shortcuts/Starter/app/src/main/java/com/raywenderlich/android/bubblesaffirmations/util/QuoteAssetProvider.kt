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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.webkit.MimeTypeMap

class QuoteAssetFileProvider : ContentProvider() {
  override fun onCreate(): Boolean {
    return true
  }

  override fun getType(uri: Uri): String? {
    val segments = uri.pathSegments
    return when (segments[0]) {
      "icon" -> MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")
      else -> "application/octet-stream"
    }
  }

  override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
    val segments = uri.pathSegments
    val name = segments[1]
    return context?.resources?.assets?.openFd("$name.png")
  }

  override fun query(
      uri: Uri,
      projection: Array<out String>?,
      selection: String?,
      selectionArgs: Array<out String>?,
      sortOrder: String?
  ): Cursor? {
    throw UnsupportedOperationException("No query")
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    throw UnsupportedOperationException("No insert")
  }

  override fun update(
      uri: Uri,
      values: ContentValues?,
      selection: String?,
      selectionArgs: Array<out String>?
  ): Int {
    throw UnsupportedOperationException("No update")
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
    throw UnsupportedOperationException("No delete")
  }
}