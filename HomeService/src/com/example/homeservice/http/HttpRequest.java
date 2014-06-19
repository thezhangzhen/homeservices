package com.example.homeservice.http;

import android.graphics.Bitmap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.example.homeservice.util.Util;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 13-12-17.
 */
public class HttpRequest {
	final static int BUFFER_SIZE = 4096;
	final static String Tag = "RequestHttp";

	public static InputStream GET(String url, Object param) throws IOException,
			InvocationTargetException, NoSuchMethodException,
			InstantiationException, IllegalAccessException {

		String tokenUrl = addToken(url);

		HashMap<Object, Object> map = getMapObject(param);
		StringBuffer sb = new StringBuffer();
		if (map != null) {
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
			tokenUrl = tokenUrl + "&"
					+ sb.toString().substring(0, sb.toString().length() - 1);
		}

		HttpGet httpGet = new HttpGet(tokenUrl);
		System.out.println(tokenUrl);
		HttpClient httpClient = HttpsUtil.getNewHttpClient();
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			return entity.getContent();
		}
		return null;
	}

	private static String addToken(String url) {
		return url + "?format=json";
	}

	public static InputStream POST(String url, Object param)
			throws IOException, InvocationTargetException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException {
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		HashMap<Object, Object> map = getMapObject(param);
		StringBuffer sb = new StringBuffer();
		if (map != null) {
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key != null && value != null) {
					p.add(new BasicNameValuePair(key.toString(), value
							.toString()));
				}
				// p.add(new BasicNameValuePair(entry.getKey().toString(), entry
				// .getValue().toString()));
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		HttpPost httpPost = null;
		httpPost = new HttpPost(addToken(url));
		HttpEntity entity = new UrlEncodedFormEntity(p, "UTF-8");
		httpPost.setEntity(entity);
		System.out.println(url + "?" + sb.toString());
		HttpClient client = HttpsUtil.getNewHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}

	/*
	 * POST
	 * 
	 * @param url
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static InputStream POSTJson(String url, String json)
			throws Exception {
		HttpPost request = new HttpPost(url);
		HttpEntity entity = new StringEntity(json, "UTF-8");
		request.setEntity(entity);
		request.addHeader("Content-Type", "application/json;charset=UTF-8");
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpResponse response = client.execute(request);
		Util.print("POST:  " + response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}

	/*
	 * 将InputStream转换成String
	 * 
	 * @param in InputStream
	 * 
	 * @return String
	 * 
	 * @throws Exception
	 */
	public static String InputStreamTOString(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);
		String str = new String(outStream.toByteArray(), "UTF-8");
		outStream.close();
		return str;
	}

	public static HashMap<Object, Object> getMapObject(Object object)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		if (object == null) {
			return null;
		}
		Class<?> classType = object.getClass();
		Object objectCopy = classType.getConstructor(new Class[] {})
				.newInstance(new Object[] {});
		Field[] fields = classType.getDeclaredFields();
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			// public的方法不处理
			if (Modifier.toString(field.getModifiers()).contains("public")) {
				continue;
			}
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			String getName = "get" + stringLetter + fieldName.substring(1);
			String setName = "set" + stringLetter + fieldName.substring(1);
			Method getMethod = classType.getMethod(getName, new Class[] {});
			Method setMethod = classType.getMethod(setName,
					new Class[] { field.getType() });
			Object value = getMethod.invoke(object, new Object[] {});

			map.put(fieldName, value);
		}
		return map;
	}


	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";

	private static InputStream uploadImage(String urlstr, Bitmap bitmap)
			throws IOException {
		String end = "\r\n";
		ByteArrayOutputStream bos = null;
		HttpClient httpClient = HttpsUtil.getNewHttpClient();
		HttpPost httpPost = new HttpPost(urlstr);
		bos = new ByteArrayOutputStream(1024 * 50);
		httpPost.setHeader("Content-Type", "multipart/form-data"
				+ "; boundary=" + BOUNDARY);

		StringBuilder temp = new StringBuilder();
		temp.append(MP_BOUNDARY).append(end);
		temp.append(
				"Content-Disposition: form-data; name=\"fileUpload\"; filename=\"")
				.append("fileupload.jpeg").append(end);
		String filetype = "image/jpeg";
		temp.append("Content-Type: ").append(filetype).append(end + end);

		byte[] res = temp.toString().getBytes();
		BufferedInputStream bis = null;
		try {
			bos.write(res);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			InputStream its = new ByteArrayInputStream(baos.toByteArray());

			/* 设置每次写入1024bytes */
			byte[] buffer = new byte[1024];

			int length = -1;
			while ((length = its.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				bos.write(buffer, 0, length);
			}

			bos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
			bos.write(("\r\n").getBytes());
		} catch (IOException e) {
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
		}

		byte[] data = bos.toByteArray();
		bos.close();
		ByteArrayEntity formEntity = new ByteArrayEntity(data);

		httpPost.setEntity(formEntity);

		HttpResponse response = httpClient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;

	}


	public static File downLoadFile(String fileUrl, String storePath) {
		HttpClient httpClient = HttpsUtil.getNewHttpClient();
		HttpGet httpGet = new HttpGet(fileUrl);
		Util.print("fileUrl:  " + fileUrl);
		Util.print("storePath:  " + storePath);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream is = httpResponse.getEntity().getContent();
			File storeFile = new File(storePath);

			String pathString = storeFile.getPath();
			String nameString = storeFile.getName();
			File floderFile = new File(pathString.substring(0,
					pathString.length() - nameString.length()));
			if (!floderFile.exists()) {
				floderFile.mkdir();
			}

			storeFile.createNewFile();
			FileOutputStream output = new FileOutputStream(storeFile);

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}

			is.close();
			output.flush();
			output.close();
			return storeFile;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}
	}

}
