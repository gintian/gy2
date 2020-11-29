package com.hjsj.hrms.businessobject.common;

import com.hrms.hjsj.utils.Sql_switcher;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringExt {
	
	/**
	 * 将iterator中的字符串列表用delimiterChar分隔组成字符串
	 * @param str
	 * @param delimiterChar 分隔符
	 * @return
	 */
	public static String delimitString(Iterator iterator, String delimiterChar) {
		String str = "";
		while(iterator.hasNext()) {
		   str += iterator.next() + delimiterChar;
		}
		str = str.substring(0, str.length()-delimiterChar.length());
		return str;
	}	
	
	/**
	 * 将strArray中的字符串列表用delimiterChar分隔组成字符串
	 * @param str
	 * @param delimiterChar 分隔符
	 * @param aroundStr 比如说key='aa'的aroundStr就是' 
	 * @return
	 */
	public static String delimitString(String[] strArray, String delimiterChar, String key, String aroundStr) {
		String str = "";
		for (int i = 0; i < strArray.length; i++) {
		   if ("".equals(strArray[i].trim())) {
               continue;
           }
		   str += key + "=" + aroundStr + strArray[i] + aroundStr + delimiterChar;
		}
		str = str.substring(0, str.length()-delimiterChar.length());
		return str;
	}
	
	/**
	 * 将strArray中的字符串列表用delimiterChar分隔组成字符串
	 * @param str
	 * @param delimiterChar 分隔符
	 * @return
	 */
	public static String delimitString(String[] strArray, String delimiterChar) {
		String str = "";
		for (int i = 0; i < strArray.length; i++) {
		   if ("".equals(strArray[i].trim())) {
               continue;
           }
		   str += strArray[i] + delimiterChar;
		}
		str = str.substring(0, str.length()-delimiterChar.length());
		return str;
	}
	
	/**
	 * 从ResultSetMetaData中的取出列名并用delimiterChar分隔组成字符串
	 * @param rsmd
	 * @param delimiterChar 分隔符
	 * @return
	 */
	public static String delimitString(ResultSetMetaData rsmd, String delimiterChar) {
		String str = "";
		try {
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				str += rsmd.getColumnName(i) + delimiterChar;	 
			}
			str = str.substring(0, str.length()-delimiterChar.length());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 从求值并用delimiterChar分隔组成字符串
	 * 每一条记录组合为一个字符串
	 * @param rsmd
	 * @param delimiterChar 分隔符
	 * @return
	 */
	public static List delimitString(ResultSet rs, String delimiterChar) {
		String str = "";
		ArrayList arrayList = new ArrayList();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
				str = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					
					String value ="";
					if(rsmd.getColumnType(i)==2005)  //解决oracle clob类型
					{
						value=Sql_switcher.readMemo(rs,rsmd.getColumnName(i));
					}
					else {
                        value=rs.getString(rsmd.getColumnName(i));
                    }
					if(value == null || "".equals(value)){
						value=" ";
					}
					str +=  value+ delimiterChar;	 
				}
				str = str.substring(0, str.length()-delimiterChar.length());
				arrayList.add(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return arrayList;
	}
	
}
