package com.dhyedu.smarthome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dhyedu.setting.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class Yuyin extends Activity {
	private ImageButton btn_Y;
	private ImageButton btn_E;
	private TextView tx1;
	private static String TAG = "Yuyin";

	private Toast mtoast;
	private Socket socket = null;
	private boolean isConnecting = false; // 判断是否连接
	private Thread mThreadClient = null;

	static BufferedReader mBufferedReaderServer = null;
	static PrintWriter mPrintWriterServer = null;
	static BufferedReader mBufferedReaderClient = null;
	static PrintWriter mPrintWriterClient = null;

	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_LOCAL;

	private EditText mResultText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		setContentView(R.layout.yuyin);

		mThreadClient = new Thread(mRunnable);
		mThreadClient.start();

		SpeechUtility.createUtility(this, "appid=554f16c1"); // 设置appid
		mIatDialog = new RecognizerDialog(this, mInitListener); // 初始化UI
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener); // 初始化听写对象

		tx1 = ((TextView) findViewById(R.id.textview1));
		btn_E = (ImageButton) findViewById(R.id.imageButtonE);
		btn_Y = (ImageButton) findViewById(R.id.imageButtonY);

		btn_E.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
				finish();
			}
		});

		btn_Y.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				setParam();

				int ret = mIat.startListening(recognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					showToast("听写失败,错误码：" + ret);
				} else {
					showToast(getString(R.string.text_begin));
				}
			}
		});

	}

	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			Toast.makeText(getApplicationContext(),
					error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
		}
	};

	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			showToast("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
			showToast(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showToast("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			showToast("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		tx1.setText(resultBuffer);

		String find_F;
		String find_B;
		String find_L;
		String find_R;
		String find_S;
		find_F = "前";
		find_B = "后";
		find_L = "左";
		find_R = "右";
		find_S = "停";
		int s_F = resultBuffer.indexOf(find_F);
		int s_B = resultBuffer.indexOf(find_B);
		int s_L = resultBuffer.indexOf(find_L);
		int s_R = resultBuffer.indexOf(find_R);
		int s_S = resultBuffer.indexOf(find_S);

		if (s_F >= 0) {
			try {
				mPrintWriterClient.println("F");
				mPrintWriterClient.flush();

			} catch (Exception e) {
				Log.e(TAG, "传输失败", e);
			}

		}

		if (s_B >= 0) {
			try {
				mPrintWriterClient.println("B");
				mPrintWriterClient.flush();

			} catch (Exception e) {
				Log.e(TAG, "传输失败", e);
			}

		}

		if (s_L >= 0) {
			try {
				mPrintWriterClient.println("L");
				mPrintWriterClient.flush();

			} catch (Exception e) {
				Log.e(TAG, "传输失败", e);
			}

		}

		if (s_R >= 0) {
			try {
				mPrintWriterClient.println("R");
				mPrintWriterClient.flush();

			} catch (Exception e) {
				Log.e(TAG, "传输失败", e);
			}

		}

		if (s_S >= 0) {
			try {
				mPrintWriterClient.println("S");
				mPrintWriterClient.flush();

			} catch (Exception e) {
				Log.e(TAG, "传输失败", e);
			}

		}
	}

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
		}
	};

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

	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "1500");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "50");
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			try {
				// 连接服务器
				socket = new Socket("192.168.100.1", 7000);
				// 取得输入、输出流
				mBufferedReaderClient = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				mPrintWriterClient = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				Log.e(TAG, "连接成功！");
				// break;
			} catch (Exception e) {
				Log.e(TAG, "连接失败！", e);
				return;
			}
		}
	};

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

	protected void onResume() {
		/**
		 * 竖屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

}
