package com.xiangxun.activity.contact;

import org.linphone.Contact;
import org.linphone.ContactFragment;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneManager.AddressType;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneFriend;
import org.linphone.ui.AddressText;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.xiangxun.activity.R;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.widget.TitleView;

/**
 * @package: com.xiangxun.activity.contact
 * @ClassName: ContactDetailActivity.java
 * @Description:联系人详情，添加好友。拨打电话功能模块
 * @author: ZhangYH
 * @date: 2017-4-14 下午1:41:23
 */
public class ContactDetailActivity extends BaseFragmentActivity implements OnClickListener, ContactPicked {
	private TitleView titleView;

	private ContactFragment fragment;

	private static ContactDetailActivity instance;

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final ContactDetailActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("ContactDetailActivity not instantiated yet");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_activity);
		instance = this;
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.contact_title);
	}

	@Override
	public void initData() {
		titleView.setTitle("联系人");
		Bundle extras = getIntent().getBundleExtra("Bundle");
		if (fragment == null) {
			fragment = new ContactFragment();
		}
		fragment.setArguments(extras);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.contact_fragment, fragment);
		ft.commit();
	}

	@Override
	public void initListener() {
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		}
	}
	
	/**
	 * @Description:添加好友状态
	 * @author: ZhangYH
	 * @date: 2017-4-14 下午3:19:22
	 */
	public boolean newFriend(Contact contact, String sipUri) {
		LinphoneFriend friend = LinphoneCoreFactory.instance().createLinphoneFriend(sipUri);
		friend.enableSubscribes(true);
		friend.setIncSubscribePolicy(LinphoneFriend.SubscribePolicy.SPAccept);
		try {
			LinphoneManager.getLc().addFriend(friend);
			contact.setFriend(friend);
			return true;
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @Description:移除好友标签
	 * @author: ZhangYH
	 * @date: 2017-4-14 下午3:19:40
	 */
	public boolean removeFriend(Contact contact, String sipUri) {
		LinphoneFriend friend = LinphoneManager.getLc().findFriendByAddress(sipUri);
		if (friend != null) {
			friend.enableSubscribes(false);
			LinphoneManager.getLc().removeFriend(friend);
			contact.setFriend(null);
			return true;
		}
		return false;
	}

	@Override
	public void setAddresGoToDialerAndCall(String number, String name, Uri photo) {
		// TODO Auto-generated method stub
		AddressType address = new AddressText(this, null);
		address.setDisplayedName(name);
		address.setText(number);
		LinphoneManager.getInstance().newOutgoingCall(address);
	}

	@Override
	public void goToDialer() {
		// TODO Auto-generated method stub
		onBackPressed();
	}

}

interface ContactPicked {
	void setAddresGoToDialerAndCall(String number, String name, Uri photo);

	void goToDialer();
}
