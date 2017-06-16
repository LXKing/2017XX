package com.xiangxun.activity.point;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiangxun.activity.R;
import com.xiangxun.activity.point.PointAddDialog.onSubmitClick;
import com.xiangxun.app.BaseActivity;
import com.xiangxun.widget.AutoClearEditText;
import com.xiangxun.widget.TitleView;
import com.xiangxun.wtone.SharedPreferencesHelper;

import org.linphone.Contact;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.ui.AddressText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zhangyuhui/Darly on 2017/6/15.
 * Copyright by [Zhangyuhui/Darly]
 * ©2017 XunXiang.Company. All rights reserved.
 *
 * @TODO: 点对点视频通话主要页面，进入页面后是一个ListView用户列表，上面添加一个新用户录入模块。点击条目进行视频呼叫。
 */
public class PointVideoActivity extends BaseActivity implements OnClickListener, TextWatcher, OnItemClickListener, onSubmitClick {

    private String cat = "POINTVIDEO";

    private TitleView header;
    //当搜索无用户时，提示添加用户。
    private ImageView add;
    //输入关键字进行关联搜索
    private AutoClearEditText text;
    private ListView list;
    private PointVideoAdapter adapter;
    private List<Contact> data;
    private List<Contact> dataSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_video_activity);
        initView();
        initData();
        initListener();
    }

    public void initView() {
        header = (TitleView) findViewById(R.id.id_point_video_title);
        header.setTitle("用户列表");
        add = (ImageView) findViewById(R.id.id_point_video_add);
        text = (AutoClearEditText) findViewById(R.id.id_point_video_text);
        list = (ListView) findViewById(R.id.id_point_video_list);
        String json = SharedPreferencesHelper.getString(this, cat, null);
        if (TextUtils.isEmpty(json)) {
            data = new ArrayList<Contact>();
        } else {
            data = new Gson().fromJson(json, new TypeToken<List<Contact>>() {
            }.getType());
        }
        dataSearch = new ArrayList<Contact>();
        adapter = new PointVideoAdapter(data, R.layout.point_video_item, this);
        list.setAdapter(adapter);
    }

    public void initListener() {
        header.setLeftBackOneListener(R.drawable.back_normal, this);
        text.addTextChangedListener(this);
        list.setOnItemClickListener(this);
        add.setOnClickListener(this);
    }

    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_view_back_img:
                onBackPressed();
                break;
            case R.id.id_point_video_add:
                //点击新增用户。
                new PointAddDialog(this, "新增用户", add, this).show();
                break;
            default:
                break;
        }
    }

    //输入匹配字符
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //没用到
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //没用到
    }

    @Override
    public void afterTextChanged(Editable s) {
        /**这是文本框改变之后 会执行的动作
         * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
         * 所以这里我们就需要加上数据的修改的动作了。
         */
        dataSearch.clear();
        if (s.length() == 0) {
            adapter.setData(data);
        } else {
            for (Contact contact : data) {
                if (contact.getName().contains(s) || contact.getID().contains(s)) {
                    dataSearch.add(contact);
                }
            }
            adapter.setData(dataSearch);
            if (dataSearch.size() == 0) {
                add.setTag(R.id.id_point_video_arg, "");
                add.setTag(R.id.id_point_video_name, "");
                add.setVisibility(View.VISIBLE);
                if (isNumeric(s.toString())) {
                    //0是号码
                    add.setTag(R.id.id_point_video_arg, s.toString());
                } else {
                    //1是名称
                    add.setTag(R.id.id_point_video_name, s.toString());
                }
            } else {
                add.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击进行呼叫操作。
        Contact contact = (Contact) parent.getItemAtPosition(position);

        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
            if (lpc != null) {
                String to = lpc.normalizePhoneNumber(contact.getID());
                setAddresGoToDialerAndCall(to, contact.getName(), null);
            } else {
                setAddresGoToDialerAndCall(contact.getID(), contact.getName(), null);
            }
            onBackPressed();
        }
    }

    public void setAddresGoToDialerAndCall(String number, String name, Uri photo) {
        // TODO Auto-generated method stub

        LinphoneManager.getInstance().newOutgoingCall(number, name);
    }


    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    @Override
    public void changeState(Contact type) {
        data.add(type);
        adapter.setData(data);
        //将数据保存到缓存中。
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Contact c : data) {
            builder.append("{" + "\"name\":" + c.getName());
            builder.append(",\"id\":" + c.getID() + "},");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");
        Log.i(getClass().getSimpleName(), builder.toString());
        SharedPreferencesHelper.putString(this, cat, builder.toString());
    }
}
