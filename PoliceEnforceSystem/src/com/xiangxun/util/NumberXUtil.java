package com.xiangxun.util;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xiangxun.activity.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/12/14.
 */

public class NumberXUtil {
	private Context mContext;
	private Activity mActivity;
	private KeyboardView keyboardView;   //keyBoardView组建
	private Keyboard KeyNum;// 数字键盘,
	private EditText mEditText;
	private RelativeLayout mkeyboard;

	public NumberXUtil(Activity mActivity, Context mContext, EditText edit) {
		this.mActivity = mActivity;
		this.mContext = mContext;
		this.mEditText = edit;

		KeyNum = new Keyboard(mContext, R.xml.number_x);
		keyboardView = (KeyboardView) mActivity.findViewById(R.id.keyboard_view);
		keyboardView.setKeyboard(KeyNum);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setOnKeyboardActionListener(listener);

		mkeyboard = (RelativeLayout) mActivity.findViewById(R.id.my_keyboard);

		hideSoftInputMethod();
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void onPress(int primaryCode) {

		}

		@Override
		public void onRelease(int primaryCode) {

		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = mEditText.getText();
			int start = mEditText.getSelectionStart();
			if (primaryCode == -5) // 完成
			{
				hideKeyboard();
			} else if (primaryCode == -2){
				if (editable != null && editable.length() > 0) {
					editable.clear();
				}
			} else if (primaryCode == -1) {
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == -3) { //left
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						mEditText.setSelection(start-1);
					}
				}
			} else if (primaryCode == -4) { //right
				if (editable != null && editable.length() > 0) {
					if (start + 1 <= editable.length())
						mEditText.setSelection(start+1);
				}
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}

		@Override
		public void onText(CharSequence text) {

		}

		@Override
		public void swipeLeft() {

		}

		@Override
		public void swipeRight() {

		}

		@Override
		public void swipeDown() {

		}

		@Override
		public void swipeUp() {

		}
	};

	public void showKeyboard() {
		int visibility = mkeyboard.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			mkeyboard.setVisibility(View.VISIBLE);
		}

//		int visibility = keyboardView.getVisibility();
//		if (visibility == View.GONE || visibility == View.INVISIBLE) {
//			keyboardView.setVisibility(View.VISIBLE);
//		}
	}

	public void hideKeyboard() {
		int visibility = mkeyboard.getVisibility();
		if (visibility == View.VISIBLE) {
			mkeyboard.setVisibility(View.GONE);
		}
//		int visibility = keyboardView.getVisibility();
//		if (visibility == View.VISIBLE) {
//			keyboardView.setVisibility(View.GONE);
//		}
	}

	public boolean getKeyboardVisible() {
		int visibility = mkeyboard.getVisibility();
		if (visibility == View.VISIBLE) {
			return true;
		}
		return false;
	}

	public void hideSoftInputMethod() {
		mActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		String methodName = null;
		if (currentVersion >= 16) {
			// 4.2
			methodName = "setShowSoftInputOnFocus";
		} else if (currentVersion >= 14) {
			// 4.0
			methodName = "setSoftInputShownOnFocus";
		}
		if (methodName == null) {
			mEditText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName,	boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(mEditText, false);
			} catch (NoSuchMethodException e) {
				mEditText.setInputType(InputType.TYPE_NULL);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}

