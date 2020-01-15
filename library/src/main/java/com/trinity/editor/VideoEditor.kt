/*
 * Copyright (C) 2019 Trinity. All rights reserved.
 * Copyright (C) 2019 Wang LianJie <wlanjie888@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.trinity.editor

import android.content.Context
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tencent.mars.xlog.Log
import com.trinity.ErrorCode
import com.trinity.listener.OnRenderListener
import java.io.File

class VideoEditor(
  context: Context
) : TrinityVideoEditor, SurfaceHolder.Callback {

  companion object {
    const val NO_ACTION = -1
  }

  private var mId: Long = 0
  private var mFilterActionId = NO_ACTION
  // texture callback
  // can do special effects textureId is TEXTURE_2D type
  private var mOnRenderListener: OnRenderListener ?= null

  init {
    val path = context.externalCacheDir?.absolutePath + "/resource.json"
    mId = create(path)
  }

  override fun setSurfaceView(surfaceView: SurfaceView) {
    surfaceView.holder.addCallback(this)
  }

  override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    onSurfaceChanged(mId, width, height)
  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    onSurfaceDestroyed(mId, holder.surface)
  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    onSurfaceCreated(mId, holder.surface)
  }

  private external fun onSurfaceCreated(handle: Long, surface: Surface)

  private external fun onSurfaceChanged(handle: Long, width: Int, height: Int)

  private external fun onSurfaceDestroyed(handle: Long, surface: Surface)

  /**
   * Create c ++ object
   * @return returns the address of the C ++ object
   */
  private external fun create(resourcePath: String): Long

  /**
   * Get video duration
   */
  override fun getVideoDuration(): Long {
    return getVideoDuration(mId)
  }

  private external fun getVideoDuration(id: Long): Long

  override fun getCurrentPosition(): Long {
    return getCurrentPosition(mId)
  }

  private external fun getCurrentPosition(handle: Long): Long

  /**
   * Get the number of video clips
   */
  override fun getClipsCount(): Int {
    return getClipsCount(mId)
  }

  private external fun getClipsCount(id: Long): Int

  /**
   * Get video clip
   * @param index the index of the current fragment
   * @return returns null if the subscript is invalid
   */
  override fun getClip(index: Int): MediaClip? {
    return getClip(mId, index)
  }

  private external fun getClip(id: Long, index: Int): MediaClip?

  /**
   * Broadcast a clip to the queue
   * @param clip The clip object inserted
    * @return returns 0 if inserted successfully
   */
  override fun insertClip(clip: MediaClip): Int {
    return insertClip(mId, clip)
  }

  private external fun insertClip(id: Long, clip: MediaClip): Int

  /**
     * Broadcast a clip to the queue
     * @param index broadcast index
     * @param clip The clip object inserted
     * @return returns 0 if inserted successfully
   */
  override fun insertClip(index: Int, clip: MediaClip): Int {
    return insertClip(mId, index, clip)
  }

  private external fun insertClip(id: Long, index: Int, clip: MediaClip): Int

  /**
   * Delete a clip
   * @param index delete the subscript of the fragment
   */
  override fun removeClip(index: Int) {
    removeClip(mId, index)
  }

  private external fun removeClip(id: Long, index: Int)

  /**
     * Replace a clip
     * @param index the index to be replaced
     * @param clip The object whose clip needs to be replaced
     * @return returns 0 if successful
   */
  override fun replaceClip(index: Int, clip: MediaClip): Int {
    return replaceClip(mId, index, clip)
  }

  private external fun replaceClip(id: Long, index: Int, clip: MediaClip): Int

  /**
   * Get all clips
   * @return returns all current fragments
   */
  override fun getVideoClips(): List<MediaClip> {
    return getVideoClips(mId)
  }

  private external fun getVideoClips(id: Long): List<MediaClip>

  /**
   * Get the time period of the current clip
   * @param index index
   * @return returns the time period of the current segment
   */
  override fun getClipTimeRange(index: Int): TimeRange {
    return getClipTimeRange(mId, index)
  }

  private external fun getClipTimeRange(id: Long, index: Int): TimeRange

  override fun getVideoTime(index: Int, clipTime: Long): Long {
    return getVideoTime(mId, index, clipTime)
  }

  private external fun getVideoTime(id: Long, index: Int, clipTime: Long): Long

  override fun getClipTime(index: Int, videoTime: Long): Long {
    return getClipTime(mId, index, videoTime)
  }

  private external fun getClipTime(id: Long, index: Int, videoTime: Long): Long

  /**
   * Get clip index based on time
   * @param time incoming time
   * @return coordinates found, -1 if not found
   */
  override fun getClipIndex(time: Long): Int {
    return getClipIndex(mId, time)
  }

  private external fun getClipIndex(id: Long, time: Long): Int

  private external fun addAction(): Int

  /**
   * Add filters
     * @param lut color map buffer
     * @param startTime where to start adding
     * @param endTime where to end
     * @return the id of the filter, which is needed when deleting or updating the filter
   */
  override fun addFilter(configPath: String): Int {
    mFilterActionId = addFilter(mId, configPath)
    return mFilterActionId
  }

  private external fun addFilter(id: Long, config: String): Int

  override fun updateFilter(configPath: String, startTime: Int, endTime: Int, actionId: Int) {
    updateFilter(mId, configPath, startTime, endTime, actionId)
  }

  private external fun updateFilter(id: Long, config: String, startTime: Int, endTime: Int, actionId: Int)

  override fun deleteFilter(actionId: Int) {
    deleteFilter(mId, actionId)
  }

  private external fun deleteFilter(id: Long, actionId: Int)

  /**
   * Add background music
     * @param path music path
     * @param startTime where to start
     * @param endTime where to end
     * @return
   */
  override fun addMusic(config: String): Int {
//    val file = File(path)
//    if (!file.exists() || file.length() == 0L) {
//      // 文件不存在
//      return ErrorCode.FILE_NOT_FOUND
//    }
    return addMusic(mId, config)
  }

  private external fun addMusic(id: Long, config: String): Int

  override fun updateMusic(config: String, actionId: Int) {
    if (mId <= 0) {
      return
    }
    updateMusic(mId, config, actionId)
  }

  private external fun updateMusic(id: Long, config: String, actionId: Int)

  override fun deleteMusic(actionId: Int) {
    if (mId <= 0) {
      return
    }
    deleteMusic(mId, actionId)
  }

  private external fun deleteMusic(id: Long, actionId: Int)

  override fun addAction(configPath: String): Int {
    if (mId <= 0) {
      return -1
    }
    return addAction(mId, configPath)
  }

  private external fun addAction(handle: Long, config: String): Int

  override fun updateAction(startTime: Int, endTime: Int, actionId: Int) {
    if (mId <= 0) {
      return
    }
    updateAction(mId, startTime, endTime, actionId)
  }

  private external fun updateAction(handle: Long, startTime: Int, endTime: Int, actionId: Int)

  override fun deleteAction(actionId: Int) {
    if (mId <= 0) {
      return
    }
    deleteAction(mId, actionId)
  }

  private external fun deleteAction(handle: Long, actionId: Int)

  override fun setOnRenderListener(l: OnRenderListener) {
    mOnRenderListener = l
  }

  /**
   * texture callback
   * Can do other special effects processing, textureId is ordinary TEXTURE_2D type
   * @param textureId Int
   * @param width Int
   * @param height Int
   */
  @Suppress("unused")
  private fun onDrawFrame(textureId: Int, width: Int, height: Int): Int {
    return mOnRenderListener?.onDrawFrame(textureId, width, height, null) ?: textureId
  }

  override fun seek(time: Int) {
    if (mId <= 0) {
      return
    }
    seek(mId, time)
  }

  private external fun seek(id: Long, time: Int)

  /**
   * Start playing
   * @param repeat whether to repeat playback
   * @return returns 0 if successful
   */
  override fun play(repeat: Boolean): Int {
    return play(mId, repeat)
  }

  private external fun play(id: Long, repeat: Boolean): Int

  /**
   * Pause playback
   */
  override fun pause() {
    pause(mId)
  }

  private external fun pause(id: Long)

  /**
   * Resume playback
   */
  override fun resume() {
    resume(mId)
  }

  private external fun resume(id: Long)

  /**
   *Stop playback, release resources
   */
  override fun stop() {
    stop(mId)
  }

  private external fun stop(id: Long)

  override fun destroy() {
    release(mId)
    mId = 0
  }

  private external fun release(id: Long)

  private fun onPlayStatusChanged(status: Int) {

  }

  private fun onPlayError(error: Int) {

  }
}