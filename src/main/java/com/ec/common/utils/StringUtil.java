package com.ec.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 鐎涙顑佹稉鍙夋惙娴ｆ粎琚�
 * 
 */
public class StringUtil {

	

 

	/**
	 * 閸掋倖鏌囩�涙顑佹稉鍙夋Ц閸氾缚璐熺粚锟�
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null)
			return true;
		if (str.trim().equals(""))
			return true;
		return false;
	}
	
	/**
	 * 閸掋倖鏌囩�涙顑佹稉鏌ユ姜缁岋拷
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 閹搭亜褰囩�涙顑佹稉锟� 娑擃厽鏋冩稉杞拌⒈娑擃亜鐡ч懞鍌︾礉閼昏鲸鏋冩稉杞扮娑擃亜鐡ч懞鍌橈拷锟� 娑撱倓閲滈懟杈ㄦ瀮娑撹桨绔存稉顏冭厬閺傚洢锟斤拷
	 * 
	 * @param str
	 * @param len
	 * @return
	 */

	/**
	 * 閸掋倛顕版潏鎾冲弳鐎涙顑侀弰顖氭儊閺勵垱鏆熺�涳拷
	 * @param s
	 * @return
	 */
	public static boolean isNumberic(String s) {
		boolean rtn=validByRegex("^[-+]{0,1}\\d*\\.{0,1}\\d+$",s);
		if(rtn) return true;
		
		return validByRegex("^0[x|X][\\da-eA-E]+$",s);
	}
	
	/**
	 * 閺勵垰鎯侀弰顖涙殻閺佽埇锟斤拷
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s)
	{
		boolean rtn=validByRegex("^[-+]{0,1}\\d*$",s);
		return rtn;
		
	}
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmail(String s)
	{
		
		boolean rtn=validByRegex("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$",s);
		return rtn;
	}
	
	/**
	 * 閹靛婧�閸欓鐖�
	 * @param s
	 * @return
	 */
	public static boolean isMobile(String s)
	{
		
		boolean rtn=validByRegex("^(((13[0-9]{1})|(15[0-9]{1}))+\\d{8})$",s);
		return rtn;
	}
	/**
	 * 閻絻鐦介崣椋庣垳
	 * @param s
	 * @return
	 */
	public static boolean isPhone(String s)
	{
		
		boolean rtn=validByRegex("(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?",s);
		return rtn;
	}
	/**
	 * 闁喚绱�
	 * @param s
	 * @return
	 */
	public static boolean isZip(String s)
	{
		boolean rtn=validByRegex("^[0-9]{6}$",s);
		return rtn;
	}
	
	
	/**
	 * qq閸欓鐖�
	 * @param s
	 * @return
	 */
	public static boolean isQq(String s)
	{
		boolean rtn=validByRegex("^[1-9]\\d{4,9}$",s);
		return rtn;
	}
	
	/**
	 * ip閸︽澘娼�
	 * @param s
	 * @return
	 */
	public static boolean isIp(String s)
	{
		boolean rtn=validByRegex("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",s);
		return rtn;
	}
	/**
	 * 閸掋倖鏌囬弰顖氭儊娑擃厽鏋�
	 * @param s
	 * @return
	 */
	public static boolean isChinese(String s)
	{
		boolean rtn=validByRegex("^[\u4e00-\u9fa5]+$",s);
		return rtn;
	}
	
	/**
	 * 鐎涙顑侀崪灞炬殶鐎涳拷
	 * @param s
	 * @return
	 */
	public static boolean isChrNum(String s)
	{
		boolean rtn=validByRegex("^([a-zA-Z0-9]+)$",s);
		return rtn;
	}
	
 
	
	/**
	 * 閸掋倖鏌囬弰顖氭儊閺勭柗RL
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url)
	{
		return validByRegex("(http://|https://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?",url);
		
		
	}
	
	
	/**
	 * 閸掋倖鏌囬弻鎰嚋鐎涙顑佹稉鍙夋Ц閸氾缚璐熼弫鏉跨摟
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * 娴ｈ法鏁ゅ锝呭灟鐞涖劏鎻蹇涚崣鐠囦降锟斤拷
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean validByRegex(String regex,String input)
	{
		Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE );   
		Matcher regexMatcher = p.matcher(input);
		return regexMatcher.find();
	}
	
	
	/**
	 * 閹跺﹤鐡х粭锔胯閻ㄥ嫮顑囨稉锟芥稉顏勭摟濮ｅ秷娴嗘稉鍝勩亣閸愶拷
	 * @param newStr
	 * @return
	 */
	public static String makeFirstLetterUpperCase(String newStr) {
		if (newStr.length() == 0)
			return newStr;

		char[] oneChar = new char[1];
		oneChar[0] = newStr.charAt(0);
		String firstChar = new String(oneChar);
		return (firstChar.toUpperCase() + newStr.substring(1));
	}
	
	/**
	 * 閹跺﹤鐡х粭锔胯閻ㄥ嫮顑囨稉锟界�涙鐦℃潪顑胯礋鐏忓繐鍟�
	 * @param newStr
	 * @return
	 */
	public static String makeFirstLetterLowerCase(String newStr) {
		if (newStr.length() == 0)
			return newStr;

		char[] oneChar = new char[1];
		oneChar[0] = newStr.charAt(0);
		String firstChar = new String(oneChar);
		return (firstChar.toLowerCase() + newStr.substring(1));
	}
	
	
	/**
	 * 閸樼粯甯�閸撳秹娼伴惃鍕瘹鐎规艾鐡х粭锟�
	 * 
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trimPrefix(String toTrim, String trimStr) {
		while (toTrim.startsWith(trimStr)) {
			toTrim = toTrim.substring(trimStr.length());
		}
		return toTrim;
	}

	/**
	 * 閸掔娀娅庨崥搴ㄦ桨閹稿洤鐣鹃惃鍕摟缁楋拷
	 * 
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trimSufffix(String toTrim, String trimStr) {
		while (toTrim.endsWith(trimStr)) {
			toTrim = toTrim.substring(0, toTrim.length() - trimStr.length());
		}
		return toTrim;
	}

	/**
	 * 閸掔娀娅庨幐鍥х暰閻ㄥ嫬鐡х粭锟�
	 * 
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trim(String toTrim, String trimStr) {
		return trimSufffix(trimPrefix(toTrim, trimStr), trimStr);
	}
 	
	
	
	
	public static String encodeURL(String string) {
		if (string == null) {
			return null;
		}

		try {
			return URLEncoder.encode(string, java.nio.charset.StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e.getMessage(), e);
		}
	}
	 
	public static String encodeURI(String string) {
		if (string == null) {
			return null;
		}

		return encodeURL(string)
			.replace("+", "%20")
			.replace("%21", "!")
			.replace("%27", "'")
			.replace("%28", "(")
			.replace("%29", ")")
			.replace("%7E", "~");
	}
	
	public static boolean startsWithOneOf(String string, String... prefixes) {
		for (String prefix : prefixes) {
			if (string.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}
	
 


}
