package com.xiangxun.activity.point;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiangxun.activity.R;

import org.linphone.Contact;

import java.util.List;

/**
 * Created by Zhangyuhui/Darly on 2017/6/14.
 * Copyright by [Zhangyuhui/Darly]
 * ©2017 XunXiang.Company. All rights reserved.
 *
 * @TODO:
 */
public class PointVideoAdapter extends ParentAdapter<Contact> {
    public PointVideoAdapter(List<Contact> data, int resID, Context context) {
        super(data, resID, context);
    }

    @Override
    public View HockView(int position, View view, ViewGroup parent, int resID, Context context, Contact contact) {

        ViewHocker hocker = null;
        if (view == null) {
            hocker = new ViewHocker();
            view = LayoutInflater.from(context).inflate(resID, null);
            hocker.tv = (TextView) view.findViewById(R.id.id_item_point_video_text);
            hocker.num = (TextView) view.findViewById(R.id.id_item_point_video_nums);
            view.setTag(hocker);
        } else {
            hocker = (ViewHocker) view.getTag();
        }
        hocker.tv.setText(contact.getName());
        hocker.num.setText(contact.getID());
        return view;
    }

    class ViewHocker {
        TextView tv;
        TextView num;
    }

}
