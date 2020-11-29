package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 考勤公用类
 * 
 * @Title: KqUtil.java
 * @Description: 考勤公用类
 * @Company: hjsj
 * @Create time: 2017年11月15日 上午10:18:51
 * @author chenxg
 * @version 1.0
 */
public class KqUtil {
	
    private Connection conn;

    public KqUtil(Connection conn) {
        this.conn = conn;
    }
	
	public static int getFieldType(String itemId) {
		int fieldType = -1;

		FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
		String itemType = fieldItem.getItemtype();
		if ("N".equalsIgnoreCase(itemType)) {
			if (0 == fieldItem.getDecimalwidth())
				fieldType = YksjParser.INT;
			else
				fieldType = YksjParser.FLOAT;
		} else if ("D".equalsIgnoreCase(itemType))
			fieldType = YksjParser.DATEVALUE;
		else
			fieldType = YksjParser.STRVALUE;

		return fieldType;
	}

	public static String getFieldTypes(String itemId) {
		FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
		return fieldItem.getItemtype();
	}
	/**
	 * 校验日期数据是否有效
	 * 
	 * @param str
	 *            需要校验的日期数据
	 * @return
	 */

	public static String checkdate(String str) {
		str = StringUtils.isEmpty(str) ? "" : str.replace("/", "-");
		if (str.indexOf("日") > -1)
			str = str.replace(" ", "");

		String dateStr = "false";
		if (str.length() < 4)
			dateStr = "false";
		else if (str.length() == 4) {
			Pattern p = Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str + "-01-01";
			else
				dateStr = "false";
		} else if (str.length() < 6) {
			Pattern p = Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if (m.matches())
				dateStr = str.replace("年", "-") + "01-01";
			else
				dateStr = "false";
		} else if (str.length() == 7) {
			if (str.indexOf("月") != -1) {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if (m.matches()) {
					if (str.indexOf("月") != -1)
						dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
					else
						dateStr = str.replace("年", "-").replace(".", "-") + "-01";
				} else
					dateStr = "false";
			} else {
				Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if (m.matches())
					dateStr = str.replace("年", "-").replace(".", "-") + "-01";
				else
					dateStr = "false";
			}
		} else if (str.length() < 8) {// 2010年3 2010年3月
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				if (str.indexOf("月") != -1)
					dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-") + "01";
				else
					dateStr = str.replace("年", "-").replace(".", "-") + "-01";
			} else
				dateStr = "false";
		} else if (str.length() == 8) {// 2010年3 2010年3月1
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				str = str.replace("年", "-").replace(".", "-").replace("月", "-");
				if (str.lastIndexOf("-") == str.length()) {
					if (str.length() < 10)
						dateStr = str + "01";
				} else {
					String[] temps = str.split("-");
					if (temps.length > 2)
						dateStr = checkMothAndDay(str);
					else
						dateStr = "false";
				}
			} else {
				dateStr = "false";
			}
		} else if (str.length() <= 11) {// 2017年1月1日
			Pattern p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				String temp = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
				dateStr = checkMothAndDay(temp);
			} else
				dateStr = "false";

		} else {// 2017年1月1日1时1分 2017年1月1日1时1分1秒
			str = str.replace("时", ":").replace("分", ":");
			if (str.endsWith(":"))
				str = str.substring(0, str.length() - 1);

			Pattern p = null;
			if (str.split(":").length < 3)
				p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]*$");
			else
				p = Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]([0-5]*\\d{1})[秒]*$");

			Matcher m = p.matcher(str);
			if (m.matches()) {
				String tempDate = str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", " ");
				String temp = tempDate.split(" ")[0];
				dateStr = checkMothAndDay(temp);
				if (!"false".equalsIgnoreCase(dateStr)) {
					String tempTime = tempDate.split(" ")[1];
					dateStr += " " + tempTime;
				}
			} else
				dateStr = "false";
		}

		if (!"false".equals(dateStr))
			dateStr = formatDate(dateStr);

		return dateStr;
	}
	


	/**
	 * 校验月与日是否符合规则
	 * 
	 * @param date
	 *            日期数据
	 * @return
	 */
	private static String checkMothAndDay(String date) {
		String tempDate = "false";
		String[] dates = date.split("-");
		if (dates[0].length() > 0 && dates[1].length() > 0 && dates[2].length() > 0) {
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12: {
				if (1 <= day && day <= 31)
					tempDate = date;

				break;
			}
			case 4:
			case 6:
			case 9:
			case 11: {
				if (1 <= day && day <= 30)
					tempDate = date;

				break;
			}
			case 2: {
				if (isLeapYear(year)) {
					if (1 <= day && day <= 29)
						tempDate = date;

				} else {
					if (1 <= day && day <= 28)
						tempDate = date;
				}
				break;
			}
			}
		}
		return tempDate;
	}

	/**
	 * 将日期数据1900-1-1 1:1:1转换成1900-01-01 01:01:01
	 * 
	 * @param date
	 *            校验完成的数据
	 * @return
	 */
	private static String formatDate(String date) {
		String newDate = "";
		String[] dates = date.split(" ");
		String year = dates[0].split("-")[0];
		String month = dates[0].split("-")[1];
		month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month : month;
		String day = dates[0].split("-")[2];
		day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day : day;
		newDate = year + "-" + month + "-" + day;

		if (dates.length == 2) {
			String[] oldTime = dates[1].split(":");
			String hour = oldTime[0];
			hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour : hour;
			newDate += " " + hour;
			if (oldTime.length > 1) {
				String min = oldTime[1];
				min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min : min;
				newDate += ":" + min;
			}

			if (oldTime.length > 2) {
				String second = oldTime[2];
				second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second : second;
				newDate += ":" + second;
			}
		}

		return newDate;
	}

	/**
	 * 闰年的条件是： ① 能被4整除，但不能被100整除； ② 能被100整除，又能被400整除。
	 * 
	 * @param year
	 * @return
	 */
	private static boolean isLeapYear(int year) {
		boolean t = false;
		if (year % 4 == 0) {
			if (year % 100 != 0) {
				t = true;
			} else if (year % 400 == 0) {
				t = true;
			}
		}
		return t;
	}
	/**
     * @Title: getTableNameByFieldName   
     * @Description: 根据字段名得到表  
     * @param @param fieldName
     * @param @return 
     * @return String    
     * @throws
     */
    public String getTableNameByFieldName(String fieldName) {
        if(StringUtils.isBlank(fieldName))
            return "";
        
        String tableName = "";
        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select fieldsetid from fielditem");
        sqlBuff.append(" where itemid=?"); 
        sqlBuff.append(" and useflag='1'");
        
        ArrayList sqlParams = new ArrayList();
        sqlParams.add(fieldName);

        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sqlBuff.toString(), sqlParams);
            if (rs.next()) {
                tableName = rs.getString("fieldsetid");
                tableName = tableName == null ? "" : tableName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return tableName;
    }
}
