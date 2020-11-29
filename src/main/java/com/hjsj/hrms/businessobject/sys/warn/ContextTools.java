package com.hjsj.hrms.businessobject.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import org.apache.log4j.Category;

import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ContextTools implements IConstant{
	
	private transient static Category cat = Category.getInstance(ContextTools.class);
	
	private static final HashMap jvmCache = new HashMap();//预警设置缓存
	
	public static ServletContext context;
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	private static Date DATE = new Date();
	
	/*
	 * 预警设置缓存
	 * 预警设置本身数据量不大，可以存本地静态缓存中
	 * jvmCache中存放一个JVMWarnConfigCache的MAP,
	 * JVMWarnConfigCache中存放预警设置信息（key=wid value=dyan(封装一条预警设置对象)）
	 */
	public static HashMap getJvmCache(){
		return jvmCache;
	}
	
	public static HashMap getWarnConfigCache(){
		HashMap retMap = (HashMap)getJvmCache().get(Key_JVM_Cache_WarnConfig);
		if( retMap == null){
			retMap = new HashMap();
			//
			getJvmCache().put(Key_JVM_Cache_WarnConfig, retMap);
		}
		return retMap;
	}

	public static ServletContext getContext() {
		return context;
	}

	public static void setContext(ServletContext ct) {
		context = ct;
	}

	public static void log(String strMessage){
		cat.debug(strMessage);
	}
	
	/**
	 * @return String:字符型时间戳
	 * datetime with type: yyyy-MM-dd HH:mm:ss SSS
	 */
	public static String getMilliSecond(){
		DATE.setTime(System.currentTimeMillis());
		return SDF.format(DATE).toString();
	}
	
	/**
	 * @return String:字符型时间戳
	 * datetime with type: yyyy-MM-dd HH:mm:ss SSS
	 */
	public static String getMilliSecond(long lMillis){
		DATE.setTime(lMillis);
		return SDF.format(DATE).toString();
	}
	
	/**
	 * 取得中文星期。
	 * 0、7是星期日，1-6，正常转换，其他默认为星期一
	 * @param strWeek
	 * @return
	 */
	public static String getStringWeek(String strWeek){
		
		if( strWeek==null || strWeek.trim().length()>1 ){
			return getStringWeek(1);
		}
		
		int iTemp = Integer.parseInt(strWeek);
		if( iTemp == 0 ){
			iTemp = 7;
		}else if( iTemp > 7){
			iTemp = 1;
		}
		return getStringWeek(iTemp);
	}
	
	/**
	 * 取得中文星期。
	 * 0、7是星期日，1-6，正常转换，其他默认为星期一
	 * @param strWeek
	 * @return
	 */
	public static String getStringWeek(int iWeek){
		String strRet;
		switch (iWeek) {
		case 0:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.sunday");
			break;
		case 1:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.monday");
			break;
		case 2:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.tuesday");
			break;
		case 3:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.wednesday");
			break;
		case 4:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.thursday");
			break;
		case 5:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.friday");
			break;
		case 6:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.saturday");
			break;
		case 7:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.sunday");
			break;

		default:
			strRet = ResourceFactory.getProperty("label.sys.warn.freq.week.monday");
			break;
		}
		return strRet;
	}

}
