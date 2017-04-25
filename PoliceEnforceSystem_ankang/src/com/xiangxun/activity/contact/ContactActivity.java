package com.xiangxun.activity.contact;

import java.util.ArrayList;
import java.util.List;

import org.linphone.Contact;
import org.linphone.ContactsFragment;
import org.linphone.DialerFragment;
import org.linphone.compatibility.Compatibility;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MyInformationActivity;
import com.xiangxun.activity.setting.SettingActivity;
import com.xiangxun.app.BaseFragmentActivity;
import com.xiangxun.widget.TitleView;

/**
 * @package: com.xiangxun.activity.contact
 * @ClassName: ContactActivity.java
 * @Description:联系人，好友列表。调用的是手机联系人。根据需求从本地Linphone服务器端获取所有用户信息。方便进行沟通
 * @author: ZhangYH
 * @date: 2017-4-14 下午1:43:17
 */
public class ContactActivity extends BaseFragmentActivity implements OnClickListener {
	private TitleView titleView;

	private ContactsFragment fragment;
	private DialerFragment dialerFragment;

	private static ContactActivity instance;
	// 两个游标内容提供者的游标
	private Cursor contactCursor, sipContactCursor;
	// 两个List集合，里面保存的是联系人信息。
	private List<Contact> contactList, sipContactList;
	// 添加几个boolean类型
	private boolean isContactPresenceDisabled = true;

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final ContactActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("ContactActivity not instantiated yet");
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
		titleView.setRightViewRightOneListener(R.drawable.user_normal, this);
		titleView.setLeftBackOneListener(R.drawable.tab_setting, this);
		updateAnimationsState();
	}

	@Override
	public void initData() {
		titleView.setTitle("联系人");
		if (fragment == null) {
			fragment = new ContactsFragment();
		}
		if (dialerFragment == null) {
			dialerFragment = new DialerFragment();
		}
		prepareContactsInBackground();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.contact_fragment, fragment);
		// ft.add(R.id.contact_fragment, dialerFragment);
		ft.commit();
	}

	@Override
	public void initListener() {

	}

	public Cursor getAllContactsCursor() {
		return contactCursor;
	}

	public Cursor getSIPContactsCursor() {
		return sipContactCursor;
	}

	public List<Contact> getAllContacts() {
		return contactList;
	}

	public List<Contact> getSIPContacts() {
		return sipContactList;
	}

	public boolean isContactPresenceDisabled() {
		return isContactPresenceDisabled;
	}

	private void updateAnimationsState() {
		// isAnimationDisabled =
		// getResources().getBoolean(R.bool.disable_animations) ||
		// !LinphonePreferences.instance().areAnimationsEnabled();
		isContactPresenceDisabled = !getResources().getBoolean(R.bool.enable_linphone_friends);
	}

	public void removeContactFromLists(Contact contact) {
		for (Contact c : contactList) {
			if (c != null && c.getID().equals(contact.getID())) {
				contactList.remove(c);
				contactCursor = Compatibility.getContactsCursor(getContentResolver());
				break;
			}
		}

		for (Contact c : sipContactList) {
			if (c != null && c.getID().equals(contact.getID())) {
				sipContactList.remove(c);
				sipContactCursor = Compatibility.getSIPContactsCursor(getContentResolver());
				break;
			}
		}
	}

	public synchronized void prepareContactsInBackground() {
		if (contactCursor != null) {
			contactCursor.close();
		}
		if (sipContactCursor != null) {
			sipContactCursor.close();
		}

		contactCursor = Compatibility.getContactsCursor(getContentResolver());
		sipContactCursor = Compatibility.getSIPContactsCursor(getContentResolver());

		Thread sipContactsHandler = new Thread(new Runnable() {
			@Override
			public void run() {
				if (sipContactCursor != null) {
					for (int i = 0; i < sipContactCursor.getCount(); i++) {
						Contact contact = Compatibility.getContact(getContentResolver(), sipContactCursor, i);
						if (contact == null)
							continue;

						contact.refresh(getContentResolver());
						// if (!isContactPresenceDisabled) {
						// searchFriendAndAddToContact(contact);
						// }
						sipContactList.add(contact);
					}
				}
				if (contactCursor != null) {
					for (int i = 0; i < contactCursor.getCount(); i++) {
						Contact contact = Compatibility.getContact(getContentResolver(), contactCursor, i);
						if (contact == null)
							continue;

						for (Contact c : sipContactList) {
							if (c != null && c.getID().equals(contact.getID())) {
								contact = c;
								break;
							}
						}
						contactList.add(contact);
					}
				}
			}
		});

		contactList = new ArrayList<Contact>();
		sipContactList = new ArrayList<Contact>();

		sipContactsHandler.start();
	}

	public ContactsFragment getFragment() {
		return fragment;
	}

	public void addContact(String displayName, String sipUri) {
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
			Intent intent = Compatibility.prepareAddContactIntent(displayName, sipUri);
			startActivity(intent);
		} else {
			Bundle extras = new Bundle();
			extras.putSerializable("NewSipAdress", sipUri);
			toEditContact(extras);

		}
	}

	public void editContact(Contact contact) {
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
			Intent intent = Compatibility.prepareEditContactIntent(Integer.parseInt(contact.getID()));
			startActivity(intent);
		} else {
			Bundle extras = new Bundle();
			extras.putSerializable("Contact", contact);
			toEditContact(extras);
		}
	}

	/**
	 * @param extras
	 *            根据参数不同，跳转到同一个类中进行操作。
	 */
	private void toEditContact(Bundle extras) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AddContactActivity.class);
		intent.putExtra("Bundle", extras);
		startActivity(intent);
	}

	public void editContact(Contact contact, String sipAddress) {
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
			Intent intent = Compatibility.prepareEditContactIntentWithSipAddress(Integer.parseInt(contact.getID()), sipAddress);
			startActivity(intent);
		} else {
			Bundle extras = new Bundle();
			extras.putSerializable("Contact", contact);
			extras.putSerializable("NewSipAdress", sipAddress);
			toEditContact(extras);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_view_back_img:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.title_view_operation_imageview_right:
			startActivity(new Intent(this, MyInformationActivity.class));
			break;
		}
	}
}
