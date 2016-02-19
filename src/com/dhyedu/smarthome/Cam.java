package com.dhyedu.smarthome;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class Cam extends Activity {

	private static String TAG = "Cam";

	private SurfaceHolder holder;
	private Thread mythread;
	private Canvas canvas;
	URL videoUrl;
	private String url;
	private int w;
	private int h;
	HttpURLConnection conn;
	Bitmap bmp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.cam);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		h = dm.heightPixels;
		w = dm.widthPixels;

		SurfaceView surface = (SurfaceView) findViewById(R.id.surface);

		surface.setKeepScreenOn(true);// 保持屏幕常亮
		mythread = new Thread(mRunnable);
		holder = surface.getHolder();

		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				mythread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void draw() {
		// TODO Auto-generated method stub
		try {
			InputStream inputstream = null;
			// 创建一个URL对象
			url = "http://192.168.100.1:8080/?action=snapshot";
			videoUrl = new URL(url);
			// 利用HttpURLConnection对象从网络中获取网页数据
			conn = (HttpURLConnection) videoUrl.openConnection();
			// 设置输入流
			conn.setDoInput(true);
			// 连接
			conn.connect();
			// 得到网络返回的输入流
			inputstream = conn.getInputStream();
			bmp = BitmapFactory.decodeStream(inputstream); // 创建出一个bitmap 画笔
			canvas = holder.lockCanvas(); // 获取画布
			RectF rectf = new RectF(0, 0, w, h);
			canvas.drawBitmap(bmp, null, rectf, null);
			holder.unlockCanvasAndPost(canvas); // 解锁画布，提交画好的图像
			conn.disconnect();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				draw();
			}
		}

	};

	@Override
	protected void onResume() {
		/**
		 * 横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}

}
