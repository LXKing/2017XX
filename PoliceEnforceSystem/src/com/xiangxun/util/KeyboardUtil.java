package com.xiangxun.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xiangxun.activity.R;

public class KeyboardUtil {

	private Context mContext;
	private Activity mActivity;
	private KeyboardView mKeyboardView;
	private EditText mEdit;
	/**
	 * 省份简称键盘
	 */
	private Keyboard province_keyboard;
	/**
	 * 数字与大写字母键盘
	 */
	private Keyboard number_keyboar;
	/**
	 * 数字与 "X" 键盘
	 */
	private Keyboard numberX_keyboar;
	/**
	 * 软键盘切换判断
	 */
	private boolean isChange = true;
	/**
	 * 判定是否是中文的正则表达式 [\\u4e00-\\u9fa5]判断一个中文 [\\u4e00-\\u9fa5]+多个中文
	 */
	private String reg = "[\\u4e00-\\u9fa5]";

	private RelativeLayout mkeyboard;

	public KeyboardUtil(Activity activity, EditText edit) {
		mActivity = activity;
		this.mContext = (Context) activity;
		mEdit = edit;
		province_keyboard = new Keyboard(mContext, R.xml.province_abbreviation);
		number_keyboar = new Keyboard(mContext, R.xml.number_or_letters);
		numberX_keyboar = new Keyboard(mContext, R.xml.number_x);
		mKeyboardView = (KeyboardView) mActivity.findViewById(R.id.keyboard_view);
		mKeyboardView.setKeyboard(number_keyboar);
		mKeyboardView.setEnabled(true);
		mKeyboardView.setPreviewEnabled(true);
		mKeyboardView.setOnKeyboardActionListener(listener);

		mkeyboard = (RelativeLayout) mActivity.findViewById(R.id.my_keyboard);
		hideSoftInputMethod();
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = mEdit.getText();
			int start = mEdit.getSelectionStart();
			if (primaryCode == -1) {// 省份简称与数字键盘切换
				changeKeyboard();
			} else if (primaryCode == -3) {// 回退
				if (editable != null && editable.length() > 0) {
					//没有输入内容时软键盘重置为省份简称软键盘
					if(editable.length() == 1){
						if (mKeyboardView.getKeyboard() == numberX_keyboar)
							changeKeyboardNumberX();
						else
							changeKeyboard(false);
					}
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == -2){
				if (editable != null && editable.length() > 0) {
					editable.clear();
				}
			} else if (primaryCode == -6) { //left
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						mEdit.setSelection(start-1);
					}
				}
			} else if (primaryCode == -4) { //right
				if (editable != null && editable.length() > 0) {
					if (start + 1 <= editable.length())
						mEdit.setSelection(start + 1);
				}
			} else if (primaryCode == -5) {// 完成
				hideKeyboard();
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
				// 判断第一个字符是否是中文,是，则自动切换到数字软键盘
				if (mEdit.getText().toString().matches(reg)) {
					changeKeyboard(true);
				}
			}
		}
	};

	/**
	 * 按切换键时切换软键盘
	 * 
	 */
	public void changeKeyboard() {
		if (isChange) {
			mKeyboardView.setKeyboard(number_keyboar);
		} else {
			mKeyboardView.setKeyboard(province_keyboard);
		}
		isChange = !isChange;
	}

	/**
	 * 指定切换软键盘 isnumber false表示要切换为省份简称软键盘 true表示要切换为数字软键盘
	 */
	public void changeKeyboard(boolean isnumber) {
		if (isnumber) {
			mKeyboardView.setKeyboard(number_keyboar);
		} else {
			mKeyboardView.setKeyboard(province_keyboard);
		}
	}

	public void changeKeyboardNumberX(){
		mKeyboardView.setKeyboard(numberX_keyboar);
	}

	/**
	 * 软键盘展示状态
	 */
	public boolean isShow() {
		return mkeyboard.getVisibility() == View.VISIBLE;
	}
	public boolean getKeyboardVisible() {
		return mkeyboard.getVisibility() == View.VISIBLE;
	}
	/**
	 * 软键盘展示
	 */
	public void showKeyboard() {
		int visibility = mkeyboard.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			mkeyboard.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 软键盘隐藏
	 */
	public void hideKeyboard() {
		int visibility = mkeyboard.getVisibility();
		if (visibility == View.VISIBLE) {
			mkeyboard.setVisibility(View.GONE);
		}
	}

	/**
	 * 禁掉系统软键盘
	 */
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
			mEdit.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName,
						boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(mEdit, false);
			} catch (NoSuchMethodException e) {
				mEdit.setInputType(InputType.TYPE_NULL);
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

	public void SetKeyboardEdit(EditText edit) {
		mEdit = edit;
	}
}
