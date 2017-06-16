package com.xiangxun.activity.point;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxun.activity.R;
import com.xiangxun.util.Tools;
import com.xiangxun.widget.AutoClearEditText;

import org.linphone.Contact;


/**
 * Created by Zhangyuhui/Darly on 2017/6/6.
 * Copyright by [Zhangyuhui/Darly]
 * ©2017 XunXiang.Company. All rights reserved.
 *
 * @TODO: 巡检页面弹出的对话框
 */
public class PointAddDialog extends Dialog {


    public interface onSubmitClick {
        void changeState(Contact type);
    }

    private onSubmitClick selectItemClick;
    private Context mContext = null;
    private View mCustomView = null;
    private String mTitle = null;

    private View tag;

    public PointAddDialog(Context context, String mTitle, View v, onSubmitClick selectItemClick) {
        super(context, R.style.PublishDialog);
        this.mContext = context;
        this.mTitle = mTitle;
        this.selectItemClick = selectItemClick;
        this.tag = v;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mCustomView = inflater.inflate(R.layout.point_video_add, null);
        setContentView(mCustomView);
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - Tools.dip2px(mContext, 5.0f);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        initView();
    }

    @Override
    public void show() {
        super.show();
    }

    private void initView() {
        TextView mTvCancle = (TextView) mCustomView.findViewById(R.id.id_point_video_add_consel);
        TextView mTvSubmit = (TextView) mCustomView.findViewById(R.id.id_point_video_add_submint);
        final AutoClearEditText name = (AutoClearEditText) mCustomView.findViewById(R.id.id_point_video_add_name);
        final AutoClearEditText number = (AutoClearEditText) mCustomView.findViewById(R.id.id_point_video_add_number);
        TextView mTvPublishSelectTitle = (TextView) mCustomView.findViewById(R.id.id_point_video_add_title);
        mTvPublishSelectTitle.setText(mTitle);
        name.setText(tag.getTag(R.id.id_point_video_name).toString());
        number.setText(tag.getTag(R.id.id_point_video_arg).toString());


        mTvCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mTvSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String names = name.getText().toString().trim();
                String num = number.getText().toString().trim();
                if (TextUtils.isEmpty(names) || TextUtils.isEmpty(num)) {
                    Toast.makeText(mContext, "姓名和号码必须填写", Toast.LENGTH_SHORT).show();
                }

                if (selectItemClick != null) {
                    Contact contact = new Contact(num, names);
                    selectItemClick.changeState(contact);
                }
                dismiss();
            }
        });
    }
}
