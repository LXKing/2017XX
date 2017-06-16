package com.xiangxun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.xiangxun.activity.R;

/**
 * Created by Administrator on 2017/2/24.
 */

public class WaterMark extends View{
	private String waterStr = "水印";
	private  Paint mPaint = null;

	public WaterMark(Context context) {
		super(context);
		mPaint = new Paint();
	}

	public WaterMark(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
	}

	public void setWaterStr(String waterStr) {
		this.waterStr = waterStr;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mPaint.setTextSize(100);
		mPaint.setColor(getResources().getColor(R.color.color_888888));
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setAntiAlias(true);
		mPaint.setAlpha(50);
		Rect bounds = new Rect();
		mPaint.getTextBounds(waterStr, 0, waterStr.length(), bounds);
		canvas.save();
		canvas.rotate(-45.0f);
		float x = 0;
		float y = 200;
		float x_start = 0;
		int offsetX = 200;
		int offsetY = 200;
		int MaxX = getMeasuredWidth();
		int MaxY = getMeasuredHeight() * 2;
		int StrWidth = bounds.width();
		int StrHeight = bounds.height();
		int MaxLine = MaxY/(StrHeight + offsetY) + 2;
		int MaxCount = MaxX/(StrWidth + offsetX) + 3;

		int xAgain = MaxX / 250 + 1;
		for (int i = 0; i < MaxLine; i++) {
			if (x < xAgain)
				x = i * 250;
			else
				x = (i - xAgain) * 250;
//
//			if (x > MaxX) {
//				x = 0 - offsetX * ((( x - MaxX) / 250) + 1);
//			}
			x = x - (StrWidth + offsetX) * (MaxCount - 1);
			for (int j = 0; j < MaxCount; j++) {
				canvas.drawText(waterStr, x, y, mPaint);
				x = x + StrWidth + offsetX;
			}
			y = y + offsetY;

//			x = 0 - i * 250;
//
//			for (int j = 0; j < MaxCount; j++) {
//				canvas.drawText(waterStr, x, y, mPaint);
//				x = x + StrWidth + offsetX;
//			}
//			y = y + offsetY;
		}

		canvas.restore();
	}
}
