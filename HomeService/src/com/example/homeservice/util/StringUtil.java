package com.example.homeservice.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextPaint;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA. User: weiguo.ren Date: 13-9-16 Time: 涓嬪崍3:10 To
 * change this template use File | Settings | File Templates.
 */
public class StringUtil {

	private static String hexString = "1234567GHJKLMNBV";

	/**
	 * 瀛楃涓茶浆鎹㈡垚鍗佸叚杩涘埗鍊�
	 * 
	 * @param bin
	 *            String 鎴戜滑鐪嬪埌鐨勮杞崲鎴愬崄鍏繘鍒剁殑瀛楃涓�
	 * @return
	 */
	public static String bin2hex(String bin) {
		char[] digital = "0123456789ABCDEF".toCharArray();
		StringBuffer sb = new StringBuffer("");
		byte[] bs = bin.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(digital[bit]);
			bit = bs[i] & 0x0f;
			sb.append(digital[bit]);
		}
		return sb.toString();
	}

	/**
	 * 鍗佸叚杩涘埗杞崲瀛楃涓�
	 * 
	 * @param hex
	 *            String 鍗佸叚杩涘埗
	 * @return String 杞崲鍚庣殑瀛楃涓�
	 */
	public static String hex2bin(String hex) {
		String digital = "0123456789ABCDEF";
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return new String(bytes);
	}

	public static String toStringVersion(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 灏嗘瘡2浣�6杩涘埗鏁存暟缁勮鎴愪竴涓瓧鑺�
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));

		return new String(baos.toByteArray());
	}

	public static boolean isBlank(String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0)
				|| str.equals("null")) {
			return true;
		}
		for (int i = 0; i < strLen; ++i) {
			if (!(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return (!(isBlank(str)));
	}

	public static String isString(String str) {
		if (str == null || str.equals("") || str.length() == 0
				|| str.equals("null")) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * 鎴彇瀛楃涓�
	 * 
	 * @param s
	 *            瑕佽鎴彇鐨勫瓧绗︿覆
	 * @param iLen
	 *            鎴彇鐨勯暱搴�
	 * @param hasDot
	 *            鏄惁鍔�..
	 * @return
	 */
	public static String getSubString(String s, int iLen, boolean hasDot) {
		if (StringUtil.isNotBlank(s)) {
			char c = ' ';
			String sAsc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.+-";
			String lsResult = " ";
			int iGetedLen = 0;
			boolean flag = false;
			for (int i = 0; i < s.length(); i++) {
				c = s.charAt(i);
				if (sAsc.indexOf(c) >= 0) {
					lsResult += c;
					iGetedLen += 1;
				} else {
					lsResult += c;
					iGetedLen += 2;
				}
				if (iGetedLen >= iLen) {
					if (i + 1 < s.length()) {
						flag = true;
					}
					break;
				}
			}
			if (iGetedLen <= iLen) {
				if (flag) {
					if (hasDot) {
						return lsResult.trim() + "...";
					} else {
						return lsResult.trim();
					}
				} else {
					return lsResult.trim();
				}
			}
			if (hasDot) {
				lsResult = lsResult + "...";
			}
			return (lsResult.trim());
		} else {
			return "";
		}
	}

	/**
	 * 缁欐帶浠舵枃瀛楄缃�绮椾綋
	 * 
	 * @param view
	 *            闇�璁剧疆 瀛椾綋绮椾綋 鐨勬帶浠�
	 */
	public static void setFakeBoldText(TextView view) {
		TextPaint tp = view.getPaint();
		tp.setFakeBoldText(true);

	}

	/**
	 * 鍒ゆ柇瀛楃涓叉槸鍚︽湁鐗规畩瀛楃
	 */

	public static boolean isSpecialChar(String str) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~锛丂#锟�鈥︹�&*锛堬級鈥斺�+|{}銆愩�鈥橈紱锛氣�鈥溾�銆傦紝銆侊紵_]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	// 鏈�ぇ浣嶆暟
	public static void lengthFilter(final Context context,
			final EditText editText, final int max_length, final String err_msg) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(max_length) {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				int destLen = getCharacterNum(dest.toString()); // 鑾峰彇瀛楃涓暟(涓�釜涓枃绠�涓瓧绗�
				int sourceLen = getCharacterNum(source.toString());
				if (destLen + sourceLen > max_length) {
					// Util.sendToast(context, err_msg);
					return "";
				}
				return source;
			}
		};

		editText.setFilters(filters);

	}

	public static int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	public static int getChineseNum(String s) {
		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;

	}

	/**
	 * 鍗婅 杞叏瑙�锛�
	 * 
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * 鍘婚櫎鐗规畩瀛楃鎴栧皢鎵�湁涓枃鏍囧彿鏇挎崲涓鸿嫳鏂囨爣鍙�
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 瀛楃涓插ぇ鍐欏彉灏忓啓 灏忓啓鍙樺ぇ鍐�
	 * 
	 * @param b
	 * @return
	 */
	public static String change(String b) {
		char letters[] = new char[b.length()];
		for (int i = 0; i < b.length(); i++) {

			char letter = b.charAt(i);
			if (letter >= 'a' && letter <= 'z')
				letter = (char) (letter - 32);
			else if (letter >= 'A' && letter <= 'Z')
				letter = (char) (letter + 32);
			letters[i] = letter;
		}

		return new String(letters);
	}

	/**
	 * 灏忓啓鍙樺ぇ鍐�
	 * 
	 * @param b
	 * @return
	 */
	public static String changeBig(String b) {
		char letters[] = new char[b.length()];
		for (int i = 0; i < b.length(); i++) {

			char letter = b.charAt(i);
			if (letter >= 'a' && letter <= 'z')
				letter = (char) (letter - 32);
			letters[i] = letter;
		}

		return new String(letters);
	}

	/*
	 * 鍒ゆ柇ssid鏄惁鐩哥瓑锛屼笁鏄�.3涓繑鍥炵殑ssid鍙兘浼氬寘鍚弻寮曞彿
	 */
	public static boolean isSsidSame(String str1, String str2) {
		if (str1 == null) {
			return false;
		}
		if (str2 == null) {
			return false;
		}
		if (str1.equals(str2)) {
			return true;
		}
		if (str1.equals(str2.substring(1, str2.length() - 1))) {
			return true;
		}
		if (str2.equals(str1.substring(1, str1.length() - 1))) {
			return true;
		}
		return false;
	}

	/*
	 * 鍒ゆ柇鏄惁鏈夋晥鐨勯偖绠卞湴鍧�
	 */
	public static boolean isMailAddr(String str) {
		if (str == null) {
			return false;
		}
		Pattern p = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/*
	 * 鍒ゆ柇鏄惁鏈夋晥鐨勫煙鍚�
	 */
	public static boolean isDomain(String str) {
		if (str == null) {
			return false;
		}
		Pattern p = Pattern.compile("([\\w\\-]+\\.)+[\\w\\-]+");
		Matcher m = p.matcher(str);
		return m.matches();
	}
}
