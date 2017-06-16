package com.xiangxun.activity.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.http.HttpUtil;
import com.xiangxun.request.ApiUrl;
import com.xiangxun.util.Utils;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.ProgressLoadingDialog;
import com.xiangxun.widget.TitleView;
import com.xiangxun.widget.ViewEditText;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改密码
 * 
 * @className Layout_userlogin
 * @author ChenXiangshi
 * @date 2015-6-10 上午9:52:07
 */
public class ChangePasswordActivity extends BaseActivity {
	private TitleView titleView;

	private Button btnOk;

	private String userid;
	private String oldpwd;
	private String oldpasswords;
	private String newpasswords;
	private String renewpasswords;
	private ProgressLoadingDialog mDialog;
	
	private ViewEditText oldPassword;
	private ViewEditText newPassword;
	private ViewEditText reNewPassword;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mDialog.dismiss();
			if (msg.what == 0) {
				MsgToast.geToast().setMsg("密码修改成功");
				finish();
			} else if (msg.what == 1) {
				MsgToast.geToast().setMsg("网络异常");
			} else {
				MsgToast.geToast().setMsg("密码修改失败");
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogin);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setTitle(R.string.modifyPwd);
		oldPassword = (ViewEditText) findViewById(R.id.old_password);
		newPassword = (ViewEditText) findViewById(R.id.new_password);
		reNewPassword = (ViewEditText) findViewById(R.id.renew_password);
		btnOk = (Button) this.findViewById(R.id.buttonloginok);
	}

	@Override
	public void initListener() {
		btnOk.setOnClickListener(new BtnOkOnClickListener());
		titleView.setLeftBackOneListener(R.drawable.back_normal, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void initData() {
		mDialog = new ProgressLoadingDialog(ChangePasswordActivity.this, "正在修改密码，请稍候...");
		oldpwd = SystemCfg.getWhitePwd(this);
		userid = SystemCfg.getUserId(this);
		
		oldPassword.setTextViewText("旧密码");
		oldPassword.setEditTextHint("请输入旧密码");
		newPassword.setTextViewText("新密码");
		newPassword.setEditTextHint("请输入新密码");
		reNewPassword.setTextViewText("确认新密码");
		reNewPassword.setEditTextHint("请输入新密码");
		
		int passwordType = InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD;
		oldPassword.setEditTextInputMode(passwordType);
		newPassword.setEditTextInputMode(passwordType);
		reNewPassword.setEditTextInputMode(passwordType);
	}

	private class BtnOkOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Utils.hideSoftInputFromWindow(ChangePasswordActivity.this);

			oldpasswords = oldPassword.getEditTextText();
			newpasswords = newPassword.getEditTextText();
			renewpasswords = reNewPassword.getEditTextText();
			if (oldpasswords == null || oldpasswords.length() <= 0) {
				MsgToast.geToast().setMsg("旧密码不能为空");
				return;
			}
			if (newpasswords == null || newpasswords.length() <= 0) {
				MsgToast.geToast().setMsg("新密码不能为空");
				return;
			}
			if (renewpasswords == null || renewpasswords.length() <= 0) {
				MsgToast.geToast().setMsg("确认密码不能为空");
				return;
			}
			if (!(newpasswords.equals(renewpasswords))) {
				MsgToast.geToast().setMsg(R.string.pwdNotEqual);
				newPassword.setEditTextText("");
				reNewPassword.setEditTextText("");
				return;
			}
			if (!oldpasswords.equals(oldpwd)) {
				MsgToast.geToast().setMsg(R.string.checkPwd);
				oldPassword.setEditTextText("");
				return;
			}

			mDialog.show();
			new Thread() {
				public void run() {
					HttpUtil hu = new HttpUtil();
					String url = ApiUrl.changePassword(ChangePasswordActivity.this);
					System.out.println("url=" + url);
					Map<String, String> params = new HashMap<String, String>();
					params.put("newpassword", newpasswords);
					params.put("userid", userid);
					String resStr = "";
					try {
						resStr = hu.queryStringForPost(url, params, HTTP.UTF_8);
						System.out.println("changepwd==" + resStr);
						if (resStr == null || (resStr != null && resStr.equals("网络异常"))) {
							handler.sendEmptyMessage(1);
						} else if (null != resStr && !resStr.equals("网络异常")) {
							JSONObject jsonObject = new JSONObject(resStr);
							HashMap<String, String> hm = new HashMap<String, String>();
							Object lobj = jsonObject.get("changepwd");
							hm = hu.getHashMap(lobj);
							String res = hm.get("res");
							if (res.equals("true")) {
								SystemCfg.setWhitePwd(ChangePasswordActivity.this, newpasswords);
								handler.sendEmptyMessage(0);
							} else if (res.equals("false")) {
								handler.sendEmptyMessage(2);
							}
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(1);
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

}
