package com.trinity.sample.view

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.trinity.sample.R
import com.trinity.sample.editor.EditorPage

class ThumbLineOverlay(
    private val mOverlayThumbLineBar: OverlayThumbLineBar,
    val startTime: Long,
    var mDuration: Long,
    val thumbLineOverlayView: ThumbLineOverlayView,
    maxDuration: Long,
    minDuration: Long,
    private val mIsInvert: Boolean,
    private var mSelectedDurationChange: OnSelectedDurationChangeListener?
) {
    private var mState: Byte = 0
    private var mMinDuration: Long = 2000000
    private var mMaxDuration: Long = 0
    private var mDistance: Int = 0

    private var mTailView: ThumbLineOverlayHandleView? = null
    private var mHeadView: ThumbLineOverlayHandleView? = null
    private var mSelectedMiddleView: View? = null
    private var mContext: Context? = null
    private var mOverlayContainer: ViewGroup? = null
    var uiEditorPage: EditorPage? = null
    private var middleViewColor = 0

    val overlayView: View?
        get() = mOverlayContainer

    init {
        this.mState = STATE_ACTIVE
        this.mMaxDuration = maxDuration
        this.mMinDuration = minDuration
        initView(startTime)
        invalidate()
    }

    private fun initView(time: Long) {
        var startTime = time
        mSelectedMiddleView = thumbLineOverlayView.middleView
        if (!mIsInvert) {
            when {
                mDuration < mMinDuration ->
                    mDuration = mMinDuration
                startTime == mMaxDuration -> {
                    startTime = mMaxDuration - 100000
                    mDuration = mMaxDuration - startTime
                }
                mDuration + startTime > mMaxDuration -> //If the animation duration + startTime is longer than the maximum duration, you must move forward to ensure that it does not exceed the range。
                    mDuration = mMaxDuration - startTime
            }
        } else {
            // Currently, only special effects have invert processing, and time effects and transition effects have a mutually exclusive relationship.
        }

        mSelectedDurationChange?.onDurationChange(startTime, startTime + mDuration, mDuration)
        if (mSelectedDurationChange != null) {
        }
        Log.d(
            TAG,
            "add TimelineBar Overlay startTime:" + startTime + " ,endTime:" + (startTime + mDuration) + " ,duration:" + mDuration
        )
        mTailView = ThumbLineOverlayHandleView(thumbLineOverlayView.tailView, startTime)
        mHeadView = ThumbLineOverlayHandleView(thumbLineOverlayView.headView, mDuration + startTime)
        mOverlayContainer = thumbLineOverlayView.container
        mOverlayContainer?.tag = this
        setVisibility(false)
        mOverlayThumbLineBar.addOverlayView(mOverlayContainer, mTailView, this, mIsInvert)
        this.mContext = mSelectedMiddleView!!.context
        mHeadView?.setPositionChangeListener(object : ThumbLineOverlayHandleView.OnPositionChangeListener {
            override fun onPositionChanged(distance: Float) {
                if (mState == STATE_FIX) {
                    return
                }
                var duration = mOverlayThumbLineBar.distance2Duration(distance)
                if (duration < 0 && mHeadView!!.duration + duration - mTailView!!.duration < mMinDuration) {
                    //Calculate the duration that can be reduced
                    duration = mMinDuration + mTailView!!.duration - mHeadView!!.duration
                } else if (duration > 0 && mHeadView!!.duration + duration > mMaxDuration) {
                    duration = mMaxDuration - mHeadView!!.duration
                }
                if (duration == 0L) {
                    return
                }
                mDuration += duration

                val layoutParams = mSelectedMiddleView!!.layoutParams
                layoutParams.width = mOverlayThumbLineBar.duration2Distance(mDuration)
                mHeadView!!.changeDuration(duration)
                mSelectedMiddleView!!.layoutParams = layoutParams
                if (mSelectedDurationChange != null) {
                    mSelectedDurationChange!!.onDurationChange(
                        mTailView!!.duration,
                        mHeadView!!.duration,
                        mDuration
                    )
                }
            }

            override fun onChangeComplete() {
                if (mState == STATE_ACTIVE) {
                    //Position to slide when active
                    mOverlayThumbLineBar.seekTo(mHeadView!!.duration, true)
                }
            }
        })

        mTailView?.setPositionChangeListener(object : ThumbLineOverlayHandleView.OnPositionChangeListener {
            override fun onPositionChanged(distance: Float) {
                if (mState == STATE_FIX) {
                    return
                }
                var duration = mOverlayThumbLineBar.distance2Duration(distance)
                if (duration > 0 && mDuration - duration < mMinDuration) {
                    duration = mDuration - mMinDuration
                } else if (duration < 0 && mTailView!!.duration + duration < 0) {
                    duration = -mTailView!!.duration
                }
                if (duration == 0L) {
                    return
                }
                mDuration -= duration
                mTailView!!.changeDuration(duration)
                if (mTailView!!.view != null) {
                    var layoutParams = mTailView?.view?.layoutParams as ViewGroup.MarginLayoutParams
                    var dx = if (mIsInvert) {
                        layoutParams.rightMargin
                    } else {
                        layoutParams.leftMargin
                    }
                    requestLayout()
                    dx = if (mIsInvert) {
                        layoutParams.rightMargin - dx
                    } else {
                        layoutParams.leftMargin - dx
                    }

                    mTailView?.view?.layoutParams = layoutParams
                    layoutParams = mSelectedMiddleView?.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.width -= dx // mOverlayThumbLineBar.duration2Distance(mDuration);
                    mSelectedMiddleView?.layoutParams = layoutParams
                }
                if (mSelectedDurationChange != null) {
                    mSelectedDurationChange!!.onDurationChange(
                        mTailView!!.duration, mHeadView!!.duration,
                        mDuration
                    )
                }
            }

            override fun onChangeComplete() {
                if (mState == STATE_ACTIVE) {
                    //Position to slide when active
                    mOverlayThumbLineBar.seekTo(mTailView!!.duration, true)
                }
            }
        })
    }

    fun switchState(state: Byte) {
        mState = state
        when (state) {
            STATE_ACTIVE//显示HeadView和TailView
            -> {
                mTailView!!.active()
                mHeadView!!.active()
                if (middleViewColor != 0) {
                    mSelectedMiddleView!!.setBackgroundColor(
                        mContext!!.resources
                            .getColor(middleViewColor)
                    )
                } else {
                    mSelectedMiddleView!!.setBackgroundColor(
                        mContext!!.resources
                            .getColor(R.color.timeline_bar_active_overlay)
                    )
                }
            }
            STATE_FIX -> {
                mTailView!!.fix()
                mHeadView!!.fix()
                if (middleViewColor != 0) {
                    mSelectedMiddleView!!.setBackgroundColor(
                        mContext!!.resources
                            .getColor(middleViewColor)
                    )
                } else {
                    mSelectedMiddleView!!.setBackgroundColor(
                        mContext!!.resources
                            .getColor(R.color.timeline_bar_active_overlay)
                    )
                }
            }
            else -> {
            }
        }
    }

    fun updateMiddleViewColor(middleViewColor: Int) {
        if (this.middleViewColor != middleViewColor) {
            this.middleViewColor = middleViewColor
            mSelectedMiddleView?.setBackgroundColor(middleViewColor)
        }

    }

    fun invalidate() {
        //First calculate the width of the middleView based on the duration
        mDistance = mOverlayThumbLineBar.duration2Distance(mDuration)
        val layoutParams = mSelectedMiddleView!!.layoutParams
        layoutParams.width = mDistance
        mSelectedMiddleView!!.layoutParams = layoutParams
        when (mState) {
            STATE_ACTIVE//Display HeadView and TailView
            -> {
                mTailView!!.active()
                mHeadView!!.active()
                if (middleViewColor != 0) {
                    mSelectedMiddleView?.setBackgroundColor(middleViewColor)
                } else {
                    mSelectedMiddleView!!.setBackgroundColor(
                        mContext!!.resources
                            .getColor(R.color.timeline_bar_active_overlay)
                    )
                }
            }
            STATE_FIX -> {
                mTailView!!.fix()
                mHeadView!!.fix()
                if (middleViewColor != 0) {
                    mSelectedMiddleView?.setBackgroundColor(middleViewColor)
                } else {
                    mSelectedMiddleView?.setBackgroundColor(
                        mContext!!.resources
                            .getColor(R.color.timeline_bar_active_overlay)
                    )
                }
            }
            else -> {
            }
        }
    }

    fun setVisibility(isVisible: Boolean) {
        if (isVisible) {
            mTailView?.view?.alpha = 1f
            mHeadView?.view?.alpha = 1f
            mSelectedMiddleView?.alpha = 1f
        } else {
            mTailView?.view?.alpha = 0f
            mHeadView?.view?.alpha = 0f
            mSelectedMiddleView?.alpha = 0f
        }
    }

    fun requestLayout() {
        val layoutParams = mTailView?.view?.layoutParams as ViewGroup.MarginLayoutParams
        val margin: Int
        if (mIsInvert) {
            layoutParams.rightMargin = mOverlayThumbLineBar.calculateTailViewInvertPosition(mTailView)
            margin = layoutParams.rightMargin
        } else {
            layoutParams.leftMargin = mOverlayThumbLineBar.calculateTailViewPosition(mTailView)
            margin = layoutParams.leftMargin
        }
        mTailView?.view?.layoutParams = layoutParams
    }

    private fun setLeftMargin(view: View, leftMargin: Int) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.leftMargin = leftMargin
        view.requestLayout()
    }

    interface ThumbLineOverlayView {
        val container: ViewGroup

        val headView: View

        val tailView: View

        val middleView: View
    }

    interface OnSelectedDurationChangeListener {
        fun onDurationChange(startTime: Long, endTime: Long, duration: Long)
    }

    fun setOnSelectedDurationChangeListener(selectedDurationChange: OnSelectedDurationChangeListener) {
        mSelectedDurationChange = selectedDurationChange
    }

    fun updateDuration(duration: Long) {
        mDuration = duration
        invalidate()
        requestLayout()
    }

    companion object {

        private val TAG = ThumbLineOverlay::class.java.name
        private const val STATE_ACTIVE: Byte = 1 //Active state (edited state)
        private const val STATE_FIX: Byte = 2    //Fixed (non-edited)
    }

}
