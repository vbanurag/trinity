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

import android.view.SurfaceView
import com.trinity.listener.OnRenderListener

interface TrinityVideoEditor {

  /**
   * Set preview view
   */
  fun setSurfaceView(surfaceView: SurfaceView)

  /**
   * Get video duration
   */
  fun getVideoDuration(): Long

  fun getCurrentPosition(): Long

  /**
   * Get the number of video clips
   */
  fun getClipsCount(): Int

  /**
   * Get video clip
   * @param index the index of the current fragment
   * @return returns null if the subscript is invalid
   */
  fun getClip(index: Int): MediaClip?

  /**
   * Broadcast a clip to the queue
   * @param clip The clip object inserted
   * @return returns 0 if inserted successfully
   */
  fun insertClip(clip: MediaClip): Int

  /**
   * Broadcast a clip to the queue
   * @param index broadcast index
   * @param clip The clip object inserted
   * @return returns 0 if inserted successfully
   */
  fun insertClip(index: Int, clip: MediaClip): Int

  /**
   * Delete a clip
   * @param index delete the subscript of the fragment
   */
  fun removeClip(index: Int)

  /**
   * Replace a clip
   * @param index the index to be replaced
   * @param clip The object whose clip needs to be replaced
   * @return returns 0 if successful
   */
  fun replaceClip(index: Int, clip: MediaClip): Int

  /**
   * Get all clips
   * @return returns all current fragments
   */
  fun getVideoClips(): List<MediaClip>

  /**
     * Get the time period of the current clip
     * @param index index
     * @return returns the time period of the current segment
     */
  fun getClipTimeRange(index: Int): TimeRange

  fun getVideoTime(index: Int, clipTime: Long): Long

  fun getClipTime(index: Int, videoTime: Long): Long

  /**
     * Get clip index based on time
     * @param time incoming time
     * @return coordinates found, -1 if not found
     */
  fun getClipIndex(time: Long): Int

  /**
     * Add filters
     * For example: the absolute path of content.json is /sdcard/Android/com.trinity.sample/cache/filters/config.json
     * The path only needs /sdcard/Android/com.trinity.sample/cache/filters
     * If the current path does not contain config.json, the addition fails
     * The specific json content is as follows:
     * {
       * "type": 0,
       * "intensity": 1.0,
       * "lut": "lut_124 / lut_124.png"
       *}
     *
     * json parameter explanation:
     * type: reserved field, currently has no effect
     * intensity: filter transparency, no difference between 0.0 and camera acquisition
     * lut: the address of the filter color table, which must be a local address and a relative path
     * Path splicing will be performed inside sdk
     * @param configPath filter parent directory of config.json
     * @return returns the unique id of the current filter
     */
  fun addFilter(configPath: String): Int

  /**
     * Update filters
     * @param configPath config.json path, currently addFilter description
     * @param startTime filter start time
     * @param endTime filter end time
     * @param actionId Which filter needs to be updated, must be the actionId returned by addFilter
     */
  fun updateFilter(configPath: String, startTime: Int, endTime: Int, actionId: Int)

  /**
     * Delete filter
     * @param actionId Which filter needs to be deleted, it must be the actionId returned when addFilter
     */
  fun deleteFilter(actionId: Int)

  /**
     * Add background music
     * * The specific json content is as follows:
     * {
       * "path": "/sdcard/trinity.mp3",
       * "startTime": 0,
       * "endTime": 2000
       *}
     * json parameter explanation:
     * path: the local address of the music
     * startTime: the start time of this music
     * endTime: the end time of this music 2000 means this music only plays for 2 seconds
     * @param config background music json content
     */
  fun addMusic(config: String): Int

  /**
     * Update background music
     * * The specific json content is as follows:
     * {
       * "path": "/sdcard/trinity.mp3",
       * "startTime": 2000,
       * "endTime": 4000
       *}
     * json parameter explanation:
     * path: the local address of the music
     * startTime: the start time of this music
     * endTime: the end time of this music 4000 means this music is played for 2 seconds from the start time to the end time
     * @param config background music json content
     */
  fun updateMusic(config: String, actionId: Int)

  /**
     * Remove background music
     * @param actionId must be the actionId returned when adding background music
     */
  fun deleteMusic(actionId: Int)

  /**
     * Add special effects
     * For example: the absolute path of content.json is /sdcard/Android/com.trinity.sample/cache/effects/config.json
     * The path only needs /sdcard/Android/com.trinity.sample/cache/effects
     * If the current path does not contain config.json, the addition fails
     * @param configPath filter parent directory of config.json
     * @return returns the unique id of the current effect
     */
  fun addAction(configPath: String): Int

  /**
     * Update specific effects
     * @param startTime The start time of the effect
     * @param endTime End time of the effect
     * @param actionId Which effect needs to be updated, must be the actionId returned by addAction
     */
  fun updateAction(startTime: Int, endTime: Int, actionId: Int)

  /**
     * Delete a special effect
     * @param actionId Which effect needs to be deleted, must be the actionId returned by addAction
     */
  fun deleteAction(actionId: Int)

  fun setOnRenderListener(l: OnRenderListener)

  /**
     * seek operation
     * @param time set the playback time, in milliseconds
     */
  fun seek(time: Int)

  /**
     * Start playing
     * @param repeat whether to repeat playback
     * @return returns 0 if successful
     */
  fun play(repeat: Boolean): Int

  /**
   * Pause playback
   */
  fun pause()

  /**
   * Resume playback
   */
  fun resume()

  /**
   * Stop playback, release resources
   */
  fun stop()

  /**
   * Release edited resources
   */
  fun destroy()
}