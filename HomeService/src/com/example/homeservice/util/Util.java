package com.example.homeservice.util;

import com.example.homeservice.base.MyApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * 
 * @author weiguo.ren
 * 
 */
public class Util {

	// 日志Tag
	private static String TAG = "smartoffice.wafersystems.com";
	// 日志打印开关
	private static boolean print = true;

	public static void print(String msg) {
		if (print) {
			Log.i(TAG, msg);
		}
	}

	public static void print(int id) {
		print(MyApplication.getContext().getString(id));
	}

	public static void print(CharSequence msg) {
		if (msg != null) {
			print(msg.toString());
		}
	}

	public static void print(String tag, String msg) {
		print(msg);
		if (print) {
			Log.i(tag, msg);
		}
	}

	/**
	 * 发送Toast
	 * 
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(Context mContext, String text) {
		Toast.makeText(mContext, text, 1).show();
	}

	/**
	 * 发送Toast
	 * 
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(CharSequence text) {
		sendToast(MyApplication.getContext(), text);
	}

	/**
	 * 发送Toast
	 * 
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(int StringResId) {
		sendToast(MyApplication.getContext(), MyApplication.getContext()
				.getString(StringResId));
	}

	/**
	 * 发送Toast
	 * 
	 * @param mContext
	 * @param text
	 */
	public static void sendToast(Context mContext, CharSequence text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 删除线
	 * 
	 * @param text
	 * @return
	 */
	public static SpannableString strickout(String text) {
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new StrikethroughSpan(), 0, ss.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ss;
	}

	/**
	 * 读取本机电话本
	 * 
	 * @param activity
	 *            当前调用改方法的Activity
	 * @return
	 */
	public static String readerPhone(Activity activity) {
		// List<String> listData = new ArrayList<String>();
		Cursor cur = activity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		activity.startManagingCursor(cur);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (cur.moveToNext()) {
			// Map<String, String> mp = new HashMap<String, String>();

			long id = cur.getLong(cur.getColumnIndex("_id"));
			Cursor pcur = activity.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ Long.toString(id), null, null);
			String phoneNumbers = "";
			while (pcur.moveToNext()) {
				String strPhoneNumber = pcur
						.getString(pcur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumbers += strPhoneNumber + "|";
			}
			// phoneNumbers += "\n";
			pcur.close();

			String name = cur.getString(cur.getColumnIndex("display_name"));
			// mp.put("name", name);
			// mp.put("number", phoneNumbers);
			if (phoneNumbers.length() > 1) {
				String flag = phoneNumbers.substring(phoneNumbers.length() - 1,
						phoneNumbers.length());
				if (flag.length() > 0 && flag.equals("|")) {
					phoneNumbers = phoneNumbers.substring(0,
							phoneNumbers.length() - 1);
				}
			}
			// listData.add(phoneNumbers);
			sb.append(i == 0 ? "" : ",");
			sb.append("{'userName':'" + name + "','phoneNumber':'"
					+ phoneNumbers + "'}");
			i++;
			// sb.append(name+":"+phoneNumbers+"\n");
		}
		cur.close();
		return sb.toString();
	}

	public static void hideKeyboard(View view, Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

	}

	public static void showKeyboard(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
