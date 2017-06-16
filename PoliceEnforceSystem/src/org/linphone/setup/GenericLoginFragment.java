package org.linphone.setup;

/*
 GenericLoginFragment.java
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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiangxun.activity.R;
import com.xiangxun.request.Api;
import com.xiangxun.widget.MsgToast;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneAuthInfo;

/**
 * @author Sylvain Berfini
 */
public class GenericLoginFragment extends Fragment implements OnClickListener {
	private EditText login, password, domain;
	private ImageView apply;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setup_generic_login, container, false);

		login = (EditText) view.findViewById(R.id.setup_username);
		TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		login.setText(tm.getDeviceId());
		password = (EditText) view.findViewById(R.id.setup_password);
		domain = (EditText) view.findViewById(R.id.setup_domain);
		apply = (ImageView) view.findViewById(R.id.setup_apply);
		password.setText(Api.password);
		domain.setText(Api.getSipIp());
		apply.setOnClickListener(this);
		boolean isFirst = getActivity().getIntent().getBooleanExtra("isFirst", false);
		if (isFirst) {
			SetupActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), domain.getText().toString());
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.setup_apply) {
			if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0 || domain.getText() == null || domain.length() == 0) {
				Toast.makeText(getActivity(), getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
				return;
			}

			String loginName = login.getText().toString();
			LinphoneAuthInfo[] linphoneAuthList = LinphoneManager.getLc().getAuthInfosList();

			int i;
			int len;

			for (i = 0, len = linphoneAuthList.length; i < len; i++){
				LinphoneAuthInfo linphoneAuthInfo = linphoneAuthList[i];
				if (linphoneAuthInfo.getUsername().equals(loginName)){
					MsgToast.geToast().setMsg("帐号已存在!");
					break;
				}
			}

			if (i == len)
				SetupActivity.instance().genericLogIn(login.getText().toString(), password.getText().toString(), domain.getText().toString());
		}
	}
}
