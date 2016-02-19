package com.dhyedu.smarthome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button startbtn;
	private ImageButton btn_Y;
	private ImageButton btn_C;

	private Button btn_F;
	private Button btn_L;
	private Button btn_R;
	private Button btn_B;

	private Toast mtoast;
	private EditText IPText;
	private TextView recvText;

	private String recvMessageClient = "";
	private String recvMessageServer = "";

	private boolean isConnecting = false; // 判断是否连接
	private Thread mThreadClient = null;
	private Socket socket = null;
	static BufferedReader mBufferedReaderServer = null;
	static PrintWriter mPrintWriterServer = null;
	static BufferedReader mBufferedReaderClient = null;
	static PrintWriter mPrintWriterClient = null;

	private static String TAG = "main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.activity_main);

		btn_Y = (ImageButton) findViewById(R.id.iButtonY);
		btn_C = (ImageButton) findViewById(R.id.iButtonC);

		startbtn = (Button) findViewById(R.id.startB);
		btn_F = (Button) findViewById(R.id.ButtonF);
		btn_L = (Button) findViewById(R.id.ButtonL);
		btn_R = (Button) findViewById(R.id.ButtonR);
		btn_B = (Button) findViewById(R.id.ButtonB);

		IPText = (EditText) findViewById(R.id.iptext);
		IPText.setText("192.168.0.1:6000");                 //ip地址网络 端口

		recvText = (TextView) findViewById(R.id.RecvText);
		recvText.setMovementMethod(ScrollingMovementMethod.getInstance());

		btn_Y.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Yuyin.class);
				startActivity(intent);
			}
		});

		btn_C.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Cam.class);
				startActivity(intent);
			}
		});

		startbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isConnecting) {
					isConnecting = false;
					try {
						if (socket != null) {
							socket.close();
							socket = null;

							mPrintWriterClient.close();
							mPrintWriterClient = null;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mThreadClient.interrupt();

					startbtn.setText("开始连接");
					IPText.setEnabled(true);
					recvText.setText(null);
				} else {
					isConnecting = true;
					startbtn.setText("停止连接");
					IPText.setEnabled(false);

					mThreadClient = new Thread(mRunnable);
					mThreadClient.start();
				}
			}
		});

		// 前进
		btn_F.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String message;
				byte[] msgBuffer;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (isConnecting && socket != null) {
						message = "F";

						// msgBuffer = message.getBytes();

						try {
							mPrintWriterClient.println(message);
							mPrintWriterClient.flush();

						} catch (Exception e) {
							Log.e(TAG, "ON RESUME: Exception during write.", e);
						}
					} else {
						showToast("没有连接");
					}
					break;
				case MotionEvent.ACTION_UP:

					message = "S";

					// msgBuffer = message.getBytes();

					try {
						mPrintWriterClient.println(message);
						mPrintWriterClient.flush();

					} catch (Exception e) {
						Log.e(TAG, "ON RESUME: Exception during write.", e);
					}
					break;
				}
				return false;
			}
		});

		// 后退
		btn_B.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String message;
				byte[] msgBuffer;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (isConnecting && socket != null) {
						message = "B";

						// msgBuffer = message.getBytes();

						try {
							mPrintWriterClient.println(message);
							mPrintWriterClient.flush();

						} catch (Exception e) {
							Log.e(TAG, "ON RESUME: Exception during write.", e);
						}
					} else {
						showToast("没有连接");
					}
					break;
				case MotionEvent.ACTION_UP:

					message = "S";

					// msgBuffer = message.getBytes();

					try {
						mPrintWriterClient.println(message);
						mPrintWriterClient.flush();

					} catch (Exception e) {
						Log.e(TAG, "ON RESUME: Exception during write.", e);
					}
					break;
				}
				return false;
			}
		});

		// 左转
		btn_L.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String message;
				byte[] msgBuffer;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (isConnecting && socket != null) {
						message = "L";

						// msgBuffer = message.getBytes();

						try {
							mPrintWriterClient.println(message);
							mPrintWriterClient.flush();

						} catch (Exception e) {
							Log.e(TAG, "ON RESUME: Exception during write.", e);
						}
					} else {
						showToast("没有连接");
					}
					break;
				case MotionEvent.ACTION_UP:

					message = "S";

					// msgBuffer = message.getBytes();

					try {
						mPrintWriterClient.println(message);
						mPrintWriterClient.flush();

					} catch (Exception e) {
						Log.e(TAG, "ON RESUME: Exception during write.", e);
					}
					break;
				}
				return false;
			}
		});

		// 右转
		btn_R.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				String message;
				byte[] msgBuffer;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (isConnecting && socket != null) {
						message = "R";

						// msgBuffer = message.getBytes();

						try {
							mPrintWriterClient.println(message);
							mPrintWriterClient.flush();

						} catch (Exception e) {
							Log.e(TAG, "ON RESUME: Exception during write.", e);
						}
					} else {
						showToast("没有连接");
					}
					break;
				case MotionEvent.ACTION_UP:

					message = "S";

					// msgBuffer = message.getBytes();

					try {
						mPrintWriterClient.println(message);
						mPrintWriterClient.flush();

					} catch (Exception e) {
						Log.e(TAG, "ON RESUME: Exception during write.", e);
					}
					break;
				}
				return false;
			}
		});
	}

	// 线程:监听服务器发来的消息
	private Runnable mRunnable = new Runnable() {
		public void run() {
			String msgText = IPText.getText().toString();
			if (msgText.length() <= 0) {
				recvMessageClient = "IP不能为空！\n";
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}
			int start = msgText.indexOf(":");
			if ((start == -1) || (start + 1 >= msgText.length())) {
				recvMessageClient = "IP地址不合法！\n";
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}
			String sIP = msgText.substring(0, start);
			String sPort = msgText.substring(start + 1);
			int port = Integer.parseInt(sPort);

			Log.d(TAG, "IP:" + sIP + ":" + port);

			try {
				// 连接服务器
				socket = new Socket(sIP, port);
				// 取得输入、输出流
				mBufferedReaderClient = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				mPrintWriterClient = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);

				mPrintWriterClient.println("ok!");
				mPrintWriterClient.flush();
				
				recvMessageClient = "已连接server!\n";// 消息换行
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				// break;
			} catch (Exception e) {
				recvMessageClient = "连接IP异常!\n";
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				return;
			}

			char[] buffer = new char[256];
			int count = 0;
			while (isConnecting) {
				try {
					// if ( (recvMessageClient =
					// mBufferedReaderClient.readLine()) != null )
					if ((count = mBufferedReaderClient.read(buffer)) > 0) {
						recvMessageClient = getInfoBuff(buffer, count) + "\n";// 消息换行
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					recvMessageClient = "接收异常!";
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				recvText.append("Server: " + recvMessageServer); // 刷新
			} else if (msg.what == 1) {
				recvText.append("Client: " + recvMessageClient); // 刷新

			}
		}
	};

	private String getInfoBuff(char[] buff, int count) {
		char[] temp = new char[count];
		for (int i = 0; i < count; i++) {
			temp[i] = buff[i];
		}
		return new String(temp);
	}

	public void onDestroy() {
		super.onDestroy();
		if (isConnecting) {
			isConnecting = false;
			try {
				if (socket != null) {
					socket.close();
					socket = null;

					mPrintWriterClient.close();
					mPrintWriterClient = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mThreadClient.interrupt();
		}

	}

	public void showToast(String text) {
		if (mtoast == null) {
			mtoast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mtoast.setText(text);
			mtoast.setDuration(Toast.LENGTH_SHORT);
		}
		mtoast.show();
		mtoast.setGravity(Gravity.CENTER, 0, 0);
	}

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
