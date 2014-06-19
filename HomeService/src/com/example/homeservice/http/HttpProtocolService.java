package com.example.homeservice.http;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.homeservice.Config;
import com.example.homeservice.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: zhangzhen Date: 14-2-26 Time: 14:47
 */
public class HttpProtocolService {

	public final static int SEND_SUCCESS = 10;
	public final static int SEND_FAILED = 20;
	public final static int SEND_RECEIVE_ERROR = 30;
	public final static int SEND_EXCEPTION = 40;
	private static boolean isCanceled = false;

	private final static int MAX_HTTP_THREAD_COUNT = 5;

	private static ExecutorService mExecutorService = Executors
			.newFixedThreadPool(MAX_HTTP_THREAD_COUNT);

	@SuppressLint("HandlerLeak")
	public static void sendProtocol(final String protocol, final Object pram,
			final String method, final ProtocolType type,
			final RequestResult requestResult) {
		final ObjectMapper oMapper = new ObjectMapper();
		isCanceled = false;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (requestResult != null) {
					switch (msg.what) {
					// 处理结果
					case SEND_SUCCESS:
						requestResult.onSuccess(msg.obj);
						break;
					case SEND_FAILED:
						requestResult.onFailure((CharSequence) msg.obj);
						break;
					case 30:
						requestResult.onFailure((CharSequence) msg.obj);
						break;
					case 40:
						requestResult.OnErrorResult((CharSequence) msg.obj);
						break;
					}
				}
            }
		};
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				InputStream in = null;
				Bundle data = new Bundle();
				try {
					if (!isCanceled) {
						Util.print("isInterrupt 正常运行！");
						if (method.equals(Config.GET)) {
							in = HttpRequest.GET(protocol, pram);
						}
						String json = HttpRequest.InputStreamTOString(in);
						Util.print("json----------------" + json);
						Message msg = new Message();
						JSONObject jsonObject = new JSONObject(json);
						if (jsonObject.getInt("result") == 1) {
							msg.what = SEND_RECEIVE_ERROR;
							msg.obj = jsonObject.getString("msg");
						} else {
							@SuppressWarnings("unchecked")
							Object object = oMapper.readValue(json,
									type.getCls());

							msg.what = 10;
							msg.obj = object;
						}
						msg.setData(data);
						if (!isCanceled) {
							handler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Util.print("isInterrupt 中断运行！");
					Message msg = new Message();
					msg.what = SEND_EXCEPTION;
					msg.setData(data);
					if (e.getMessage() != null)
						msg.obj = e.getMessage();
					else
						e.printStackTrace();
					if (!isCanceled) {
						handler.sendMessage(msg);
					}
					e.printStackTrace();
				}

			}
		});
	}

	public static void cancel() {
		isCanceled = true;
	}
}
