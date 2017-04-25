package org.linphone.compatibility;

import java.util.ArrayList;
import java.util.List;

import org.linphone.Contact;
import org.linphone.core.LinphoneAddress;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.BaseTypes;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

import com.xiangxun.activity.R;

/*
 ApiNinePlus.java
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
/**
 * @author Sylvain Berfini
 */
@TargetApi(9)
public class ApiNinePlus {

	public static void addSipAddressToContact(Context context, ArrayList<ContentProviderOperation> ops, String sipAddress) {
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) //
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)//
				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)//
				.withValue(ContactsContract.CommonDataKinds.Im.DATA, sipAddress)//
				.withValue(ContactsContract.CommonDataKinds.Im.TYPE, BaseTypes.TYPE_CUSTOM)//
				.withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)//
				.withValue(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, "Sip")//
				.withValue(ContactsContract.CommonDataKinds.Im.LABEL, context.getString(R.string.addressbook_label))//
				.build());
	}

	public static void addSipAddressToContact(Context context, ArrayList<ContentProviderOperation> ops, String sipAddress, String rawContactID) {
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//
				.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactID) //
				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)//
				.withValue(ContactsContract.CommonDataKinds.Im.DATA, sipAddress)//
				.withValue(ContactsContract.CommonDataKinds.Im.TYPE, BaseTypes.TYPE_CUSTOM)//
				.withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)//
				.withValue(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, "Sip")//
				.withValue(ContactsContract.CommonDataKinds.Im.LABEL, context.getString(R.string.addressbook_label))//
				.build());
	}

	public static void updateSipAddressForContact(ArrayList<ContentProviderOperation> ops, String oldIm, String newIm, String contactID) {
		String select = ContactsContract.Data.CONTACT_ID + "=? AND " //
				+ ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE + "' AND " //
				+ ContactsContract.CommonDataKinds.Im.DATA + "=? AND lower(" + CommonDataKinds.Im.CUSTOM_PROTOCOL + ") = 'sip'"; //
		String[] args = new String[] { String.valueOf(contactID), oldIm };

		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) //
				.withSelection(select, args) //
				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)//
				.withValue(ContactsContract.CommonDataKinds.Im.DATA, newIm)//
				.build());
	}

	public static void deleteSipAddressFromContact(ArrayList<ContentProviderOperation> ops, String oldIm, String contactID) {
		String select = ContactsContract.Data.CONTACT_ID + "=? AND " //
				+ ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE + "' AND "//
				+ ContactsContract.CommonDataKinds.Im.DATA + "=? AND lower(" + CommonDataKinds.Im.CUSTOM_PROTOCOL + ") = 'sip'";
		String[] args = new String[] { String.valueOf(contactID), oldIm };

		ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).withSelection(select, args).build());
	}

	public static List<String> extractContactNumbersAndAddresses(String id, ContentResolver cr) {
		List<String> list = new ArrayList<String>();

		Uri uri = Data.CONTENT_URI;
		String[] projection = { ContactsContract.CommonDataKinds.Im.DATA };

		// Phone Numbers
		Cursor c = cr.query(Phone.CONTENT_URI, new String[] { Phone.NUMBER }, Phone.CONTACT_ID + " = " + id, null, null);
		if (c != null) {
			while (c.moveToNext()) {
				String number = c.getString(c.getColumnIndex(Phone.NUMBER));
				list.add(number);
			}
			c.close();
		}

		// IM addresses
		String selection = new StringBuilder()//
				.append(Data.CONTACT_ID)//
				.append(" = ? AND ")//
				.append(Data.MIMETYPE)//
				.append(" = '")//
				.append(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)//
				.append("' AND lower(")//
				.append(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL)//
				.append(") = 'sip'")//
				.toString();
		projection = new String[] { ContactsContract.CommonDataKinds.Im.DATA };
		c = cr.query(uri, projection, selection, new String[] { id }, null);
		if (c != null) {
			int nbId = c.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA);
			while (c.moveToNext()) {
				list.add("sip:" + c.getString(nbId));
			}
			c.close();
		}

		// SIP addresses
		String selection2 = new StringBuilder()//
				.append(Data.CONTACT_ID)//
				.append(" = ? AND ")//
				.append(Data.MIMETYPE)//
				.append(" = '")//
				.append(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)//
				.append("'")//
				.toString();
		projection = new String[] { ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS };
		c = cr.query(uri, projection, selection2, new String[] { id }, null);
		if (c != null) {
			int nbid = c.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS);
			while (c.moveToNext()) {
				list.add("sip:" + c.getString(nbid));
			}
			c.close();
		}

		return list;
	}

	public static Cursor getContactsCursor(ContentResolver cr, String search) {
		String req = "(" + Data.MIMETYPE + " = '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE//
				+ "' AND " + CommonDataKinds.Phone.NUMBER + " IS NOT NULL OR (" //
				+ Data.MIMETYPE + " = '" + CommonDataKinds.Im.CONTENT_ITEM_TYPE //
				+ "' AND lower(" + CommonDataKinds.Im.CUSTOM_PROTOCOL + ") = 'sip'"//
				+ " AND " + ContactsContract.CommonDataKinds.Im.DATA + " IS NOT NULL"//
				+ ") OR (" + Data.MIMETYPE + " = '" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE//
				+ "' AND " + ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS + " IS NOT NULL))";

		if (search != null) {
			req += " AND " + Data.DISPLAY_NAME + " LIKE '%" + search + "%'";
		}

		return ApiFivePlus.getGeneralContactCursor(cr, req, true);
	}

	public static Cursor getSIPContactsCursor(ContentResolver cr, String search) {
		String req = null;
		req = "(" + Data.MIMETYPE + " = '" + CommonDataKinds.Im.CONTENT_ITEM_TYPE //
				+ "' AND lower(" + CommonDataKinds.Im.CUSTOM_PROTOCOL + ") = 'sip'"//
				+ " AND " + ContactsContract.CommonDataKinds.Im.DATA + " IS NOT NULL"//
				+ " OR (" + Data.MIMETYPE + " = '" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE //
				+ "' AND " + ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS + " IS NOT NULL))";

		if (search != null) {
			req += " AND " + Data.DISPLAY_NAME + " LIKE '%" + search + "%'";
		}

		return ApiFivePlus.getGeneralContactCursor(cr, req, true);
	}

	private static Cursor getSIPContactCursor(ContentResolver cr, String id) {
		String req = null;
		req = "(" + Contacts.Data.MIMETYPE + " = '" + CommonDataKinds.Im.CONTENT_ITEM_TYPE //
				+ " AND lower(" + CommonDataKinds.Im.CUSTOM_PROTOCOL + ") = 'sip' AND "//
				+ android.provider.ContactsContract.CommonDataKinds.Im.DATA + " LIKE '" + id + "' " //
				+ " OR " + Contacts.Data.MIMETYPE + " = '" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE //
				+ " AND " + android.provider.ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS + " LIKE '" + id + "'";

		return ApiFivePlus.getGeneralContactCursor(cr, req, false);
	}

	public static Uri findUriPictureOfContactAndSetDisplayName(LinphoneAddress address, ContentResolver cr) {
		String username = address.getUserName();
		String domain = address.getDomain();
		String sipUri = username + "@" + domain;

		Cursor cursor = getSIPContactCursor(cr, sipUri);
		Contact contact = ApiFivePlus.getContact(cr, cursor, 0);
		if (contact != null && contact.getNumerosOrAddresses().contains(sipUri)) {
			address.setDisplayName(contact.getName());
			cursor.close();
			return contact.getPhotoUri();
		}

		cursor.close();
		return null;
	}
}
