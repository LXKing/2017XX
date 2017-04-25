package org.linphone;

/*
 VideoCallFragment.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.linphone.compatibility.Compatibility;
import org.linphone.compatibility.CompatibilityScaleGestureDetector;
import org.linphone.compatibility.CompatibilityScaleGestureListener;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.VideoSize;
import org.linphone.core.LinphoneCall.State;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.xiangxun.activity.R;

/**
 * @author Sylvain Berfini
 */
public class VideoCallFragment extends Fragment implements OnGestureListener, OnDoubleTapListener, CompatibilityScaleGestureListener {
	private SurfaceView mVideoView;
	private SurfaceView mCaptureView;
	private AndroidVideoWindowImpl androidVideoWindowImpl;
	private GestureDetector mGestureDetector;
	private float mZoomFactor = 1.f;
	private float mZoomCenterX, mZoomCenterY;
	private CompatibilityScaleGestureDetector mScaleDetector;
	private InCallActivity inCallActivity;

	@SuppressWarnings("deprecation")
	// Warning useless because value is ignored and automatically set by new
	// APIs.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.video, container, false);

		mVideoView = (SurfaceView) view.findViewById(R.id.videoSurface);
		mCaptureView = (SurfaceView) view.findViewById(R.id.videoCaptureSurface);
		mCaptureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		fixZOrder(mVideoView, mCaptureView);

		androidVideoWindowImpl = new AndroidVideoWindowImpl(mVideoView, mCaptureView, new AndroidVideoWindowImpl.VideoWindowListener() {
			@Override
			public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				LinphoneManager.getLc().setVideoWindow(vw);
				mVideoView = surface;
			}

			@Override
			public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				LinphoneCore lc = LinphoneManager.getLc();
				if (lc != null) {
					lc.setVideoWindow(null);
				}
			}

			@Override
			public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				mCaptureView = surface;
				LinphoneManager.getLc().setPreviewWindow(mCaptureView);
			}

			@Override
			public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				// Remove references kept in jni code and restart camera
				LinphoneManager.getLc().setPreviewWindow(null);
			}
		});

		mCaptureView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mScaleDetector != null) {
					mScaleDetector.onTouchEvent(event);
				}

				mGestureDetector.onTouchEvent(event);
				if (inCallActivity != null) {
					inCallActivity.displayVideoCallControlsIfHidden();
				}
				return true;
			}
		});

		mVideoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchVideo();
			}
		});

		initVideoSize(LinphoneManager.getLc().getCurrentCall());
		return view;
	}

	private void fixZOrder(SurfaceView video, SurfaceView preview) {
		video.setZOrderOnTop(true);
		preview.setZOrderOnTop(false);
		preview.setZOrderMediaOverlay(true); // Needed to be able to display
												// control layout over
	}

	public void switchCamera() {
		try {
			int videoDeviceId = LinphoneManager.getLc().getVideoDevice();
			videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
			LinphoneManager.getLc().setVideoDevice(videoDeviceId);
			CallManager.getInstance().updateCall();

			// previous call will cause graph reconstruction -> regive preview
			// window
			if (mCaptureView != null) {
				LinphoneManager.getLc().setPreviewWindow(mCaptureView);
			}

		} catch (ArithmeticException ae) {
			Log.e("Cannot swtich camera : no camera");
		}
	}

	/**
	 * @Description:视频窗口切换方案一：可行。将两个SurfaceView位置相互调换 
	 *                                                方案二：不可行，将androidVideoWindowImpl传递回来的两个SurfaceView进行对调
	 * @author: ZhangYH
	 * @date: 2017-4-24 上午8:25:49
	 */
	public void switchVideo() {
		// 由于无法找到视频流进行切入。只能修改窗口大小进行适配。
		mTimer.cancel();
		RelativeLayout rl = (RelativeLayout) mVideoView.getParent();
		rl.removeAllViews();
		VideoSize vs = LinphoneManager.getLc().getCurrentCall().getCurrentParamsCopy().getReceivedVideoSize();
		if (mVideoView.getWidth() == vs.width) {
			// mVideoView处于小窗口状态
			fixZOrder(mCaptureView, mVideoView);
			mVideoView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			mVideoView.setOnClickListener(null);
			LayoutParams lp = new LayoutParams(vs.width, vs.height);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mCaptureView.setLayoutParams(lp);
			mCaptureView.setOnTouchListener(null);
			rl.addView(mVideoView);
			rl.addView(mCaptureView);

			// 重新绑定事件
			mVideoView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (mScaleDetector != null) {
						mScaleDetector.onTouchEvent(event);
					}

					mGestureDetector.onTouchEvent(event);
					if (inCallActivity != null) {
						inCallActivity.displayVideoCallControlsIfHidden();
					}
					return true;
				}
			});

			mCaptureView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switchVideo();
				}
			});
		} else {
			fixZOrder(mVideoView, mCaptureView);
			mCaptureView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			mCaptureView.setOnClickListener(null);
			LayoutParams lp = new LayoutParams(vs.width, vs.height);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mVideoView.setLayoutParams(lp);
			mVideoView.setOnTouchListener(null);
			rl.addView(mCaptureView);
			rl.addView(mVideoView);
			mCaptureView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (mScaleDetector != null) {
						mScaleDetector.onTouchEvent(event);
					}

					mGestureDetector.onTouchEvent(event);
					if (inCallActivity != null) {
						inCallActivity.displayVideoCallControlsIfHidden();
					}
					return true;
				}
			});

			mVideoView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switchVideo();
				}
			});
		}

	}


	@Override
	public void onResume() {
		super.onResume();

		if (mVideoView != null) {
			((GLSurfaceView) mVideoView).onResume();
		}
		if (androidVideoWindowImpl != null) {
			synchronized (androidVideoWindowImpl) {
				LinphoneManager.getLc().setVideoWindow(androidVideoWindowImpl);
			}
		}

		mGestureDetector = new GestureDetector(inCallActivity, this);
		mScaleDetector = Compatibility.getScaleGestureDetector(inCallActivity, this);
	}

	@Override
	public void onPause() {
		if (androidVideoWindowImpl != null) {
			synchronized (androidVideoWindowImpl) {
				/*
				 * this call will destroy native opengl renderer which is used
				 * by androidVideoWindowImpl
				 */
				LinphoneManager.getLc().setVideoWindow(null);
			}
		}

		if (mVideoView != null) {
			((GLSurfaceView) mVideoView).onPause();
		}

		super.onPause();
	}

	@Override
	public boolean onScale(CompatibilityScaleGestureDetector detector) {
		mZoomFactor *= detector.getScaleFactor();
		// Don't let the object get too small or too large.
		// Zoom to make the video fill the screen vertically
		float portraitZoomFactor = ((float) mVideoView.getHeight()) / (float) ((3 * mVideoView.getWidth()) / 4);
		// Zoom to make the video fill the screen horizontally
		float landscapeZoomFactor = ((float) mVideoView.getWidth()) / (float) ((3 * mVideoView.getHeight()) / 4);
		mZoomFactor = Math.max(0.1f, Math.min(mZoomFactor, Math.max(portraitZoomFactor, landscapeZoomFactor)));

		LinphoneCall currentCall = LinphoneManager.getLc().getCurrentCall();
		if (currentCall != null) {
			resetZoom();
			currentCall.zoomVideo(mZoomFactor, mZoomCenterX, mZoomCenterY);
			return true;
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (LinphoneUtils.isCallEstablished(LinphoneManager.getLc().getCurrentCall())) {
			if (mZoomFactor > 1) {
				// Video is zoomed, slide is used to change center of zoom
				if (distanceX > 0 && mZoomCenterX < 1) {
					mZoomCenterX += 0.01;
				} else if (distanceX < 0 && mZoomCenterX > 0) {
					mZoomCenterX -= 0.01;
				}
				if (distanceY < 0 && mZoomCenterY < 1) {
					mZoomCenterY += 0.01;
				} else if (distanceY > 0 && mZoomCenterY > 0) {
					mZoomCenterY -= 0.01;
				}

				if (mZoomCenterX > 1)
					mZoomCenterX = 1;
				if (mZoomCenterX < 0)
					mZoomCenterX = 0;
				if (mZoomCenterY > 1)
					mZoomCenterY = 1;
				if (mZoomCenterY < 0)
					mZoomCenterY = 0;

				LinphoneManager.getLc().getCurrentCall().zoomVideo(mZoomFactor, mZoomCenterX, mZoomCenterY);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if (LinphoneUtils.isCallEstablished(LinphoneManager.getLc().getCurrentCall())) {
			if (mZoomFactor == 1.f) {
				// Zoom to make the video fill the screen vertically
				float portraitZoomFactor = ((float) mCaptureView.getHeight()) / (float) ((3 * mCaptureView.getWidth()) / 4);
				// Zoom to make the video fill the screen horizontally
				float landscapeZoomFactor = ((float) mCaptureView.getWidth()) / (float) ((3 * mCaptureView.getHeight()) / 4);

				mZoomFactor = Math.max(portraitZoomFactor, landscapeZoomFactor);
			} else {
				resetZoom();
			}
			LinphoneManager.getLc().getCurrentCall().zoomVideo(mZoomFactor, mZoomCenterX, mZoomCenterY);
			return true;
		}

		return false;
	}

	private void resetZoom() {
		mZoomFactor = 1.f;
		mZoomCenterX = mZoomCenterY = 0.5f;
	}

	@Override
	public void onDestroy() {
		inCallActivity = null;

		mVideoView = null;
		if (mCaptureView != null) {
			mCaptureView.setOnTouchListener(null);
			mCaptureView = null;
		}
		if (androidVideoWindowImpl != null) {
			// Prevent linphone from crashing if correspondent hang up while you
			// are rotating
			androidVideoWindowImpl.release();
			androidVideoWindowImpl = null;
		}
		if (mGestureDetector != null) {
			mGestureDetector.setOnDoubleTapListener(null);
			mGestureDetector = null;
		}
		if (mScaleDetector != null) {
			mScaleDetector.destroy();
			mScaleDetector = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
		}

		super.onDestroy();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		inCallActivity = (InCallActivity) activity;
		if (inCallActivity != null) {
			inCallActivity.bindVideoFragment(this);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true; // Needed to make the GestureDetector working
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private Handler mHandler = new Handler();
	private Timer mTimer;
	private TimerTask mTask;

	/**
	 * @Description:起始开启计时器来获取视频参数。根据视频参数，不断修改视频大小。
	 * @author: ZhangYH
	 * @date: 2017-4-21 上午10:08:31
	 */
	private void initVideoSize(final LinphoneCall call) {
		if (mTimer != null && mTask != null) {
			return;
		}

		mTimer = new Timer();
		mTask = new TimerTask() {
			@Override
			public void run() {
				if (call == null) {
					mTimer.cancel();
					return;
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						synchronized (LinphoneManager.getLc()) {
							final LinphoneCallParams params = call.getCurrentParamsCopy();
							if (params.getVideoEnabled()) {
								if (mVideoView != null) {
									// 设置视频大小
									VideoSize vs = params.getReceivedVideoSize();
									LayoutParams lp = new LayoutParams(vs.width, vs.height);
									lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
									mVideoView.setLayoutParams(lp);
								}
							}
						}
					}
				});
			}
		};
		mTimer.scheduleAtFixedRate(mTask, 0, 1000);
	}
}
