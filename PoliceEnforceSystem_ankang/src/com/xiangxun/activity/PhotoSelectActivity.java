package com.xiangxun.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.xiangxun.app.BaseActivity;
import com.xiangxun.common.LocalNetWorkView;
import com.xiangxun.util.ImageCacheLoader;
import com.xiangxun.util.ImageCacheLoader.GetLocalCallBack;
import com.xiangxun.widget.MsgToast;
import com.xiangxun.widget.TitleView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.xiangxun.activity
 * @ClassName: PhotoSelectActivity.java
 * @Description: 图片选择器
 * @author: HanGJ
 * @date: 2015-8-24 下午2:53:41
 */
public class PhotoSelectActivity extends BaseActivity implements OnClickListener {
	private TitleView titleView;
	private GridView mGvPhotos = null;
	private MyGridAdapter mMyGridAdapter = null;
	private Button mBtnFinish = null;

	private ArrayList<String> mAllLocalPhotos = new ArrayList<String>();
	private List<String> mSelectedPhotos = new ArrayList<String>();
	private int mCount = 0;
	private int mSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_select_activity);
		initView();
		initData();
		initListener();
	}

	@Override
	public void initView() {
		titleView = (TitleView) findViewById(R.id.tv_comm_title);
		titleView.setLeftBackOneListener(R.drawable.back_normal, this);
		titleView.setTitle(R.string.publish_my_album);
		mGvPhotos = (GridView) findViewById(R.id.gv_photos);
		mBtnFinish = (Button) findViewById(R.id.btn_finish);
	}

	@Override
	public void initListener() {
		mBtnFinish.setOnClickListener(this);
		mGvPhotos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImageView ivSelectChecked = (ImageView) view.findViewById(R.id.iv_photo_select);
				if (ivSelectChecked.getVisibility() == View.GONE) {
					if (getIntent().getAction() != null && getIntent().getAction().equals("publishFourPhotosAccident")) {
						if (mSize + mCount >= 10) {
							MsgToast.geToast().setMsg("已经选取10张照片~");
							return;
						}
					} else if (getIntent().getAction() != null && getIntent().getAction().equals("publishFourPhotos")) {
						if (mSize + mCount >= 4) {
							MsgToast.geToast().setMsg("已经选取4张照片~");
							return;
						}
					} else if (getIntent().getAction() != null && getIntent().getAction().equals("publishThreePhotos")) {
						if (mSize + mCount >= 4) {
							MsgToast.geToast().setMsg("已经选取3张照片~");
							return;
						}
					}
					ivSelectChecked.setVisibility(View.VISIBLE);
					mSelectedPhotos.add(mAllLocalPhotos.get(position));
					mCount++;
				} else {
					ivSelectChecked.setVisibility(View.GONE);
					mSelectedPhotos.remove(mAllLocalPhotos.get(position));
					mCount--;
				}
				mBtnFinish.setText("完成(" + mCount + ")");
			}
		});
	}

	@Override
	public void initData() {
		mSize = getIntent().getIntExtra("size", 0);
		mMyGridAdapter = new MyGridAdapter();
		query();
		mGvPhotos.setAdapter(mMyGridAdapter);

	}

	/**
	 * @Title:
	 * @Description: 查询vio文件夹下的所有照片
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void query() {
		String path = Environment.getExternalStorageDirectory() + "/xiangxun/vio";
		File pictures = new File(path);
		if (pictures.exists()) {
			String[] list = pictures.list();
			for (int i = 0; i < list.length; i++) {
				mAllLocalPhotos.add(path + "/" + list[i]);
			}
		} else
			return;

		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		// Cursor cursor =
		// getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// filePathColumn, null, null,
		// MediaStore.Images.Media.DATE_ADDED.concat(" desc"));
		// for (cursor.moveToFirst(); !cursor.isAfterLast();
		// cursor.moveToNext()) {
		// String filepath =
		// cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
		// mAllLocalPhotos.add(filepath);
		// }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish:
			Intent intent = new Intent();
			intent.putExtra("album_picture", (Serializable) mSelectedPhotos);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;
		case R.id.title_view_back_img:
			onBackPressed();
			break;
		}
	}

	private class MyGridAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		public MyGridAdapter() {
			mInflater = LayoutInflater.from(PhotoSelectActivity.this);
		}

		@Override
		public int getCount() {
			return mAllLocalPhotos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mAllLocalPhotos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.photo_select_item, null);
				holder.ivPhoto = (LocalNetWorkView) convertView.findViewById(R.id.iv_photo);
				holder.ivSelectedLogo = (ImageView) convertView.findViewById(R.id.iv_photo_select);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Object o = holder.ivPhoto.getTag();
			if (o != null && !((String) o).equals(mAllLocalPhotos.get(position))) {
				holder.ivPhoto.setImageBitmap(null);
			}

			holder.ivPhoto.setTag(mAllLocalPhotos.get(position));
			holder.ivSelectedLogo.setVisibility(mSelectedPhotos.contains(mAllLocalPhotos.get(position)) ? View.VISIBLE : View.GONE);
			try {
				ImageCacheLoader.getInstance().getLocalImage(mAllLocalPhotos.get(position), holder.ivPhoto, new GetLocalCallBack() {
					@Override
					public void localSuccess(Object o) {
						LocalNetWorkView lv = (LocalNetWorkView) o;
						if (lv.getTag().equals(lv.filePath)) {
							lv.setImageBitmap(lv.bm);
						}
					}

					@Override
					public void localFalse(Object o) {
						// ToDo
					}
				}, false);
			} catch (Exception e) {
				// holder.ivPhoto.setBackgroundResource(R.drawable.ic_error);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		LocalNetWorkView ivPhoto = null;
		ImageView ivSelectedLogo = null;
	}

}
