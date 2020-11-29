package com.hjsj.hrms.businessobject.general.template.templateanalyse;

import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ParseAsyXml {
   private Connection conn;
   private Document doc;
   public ParseAsyXml(Connection conn)
   {
	   this.conn=conn;
   }
   /**
    * 解析子集xml
    * @param sql
    * @param fieldname
    * <?xml version='1.0' encoding="GB2312"?>
       <records columns="A0405` A0425`…">
			<record I9999="-1">1`22.00`223`30303`0`…``</record>
			<record I9999="-1">1`22.00`223`30303`0`…``</record>
       </records>
    */   
   public ArrayList getFiledsetValue(String sql,String fieldname,String columnName,String sub_domain)
   {
   	ContentDAO dao=new ContentDAO(this.conn);
   	String records="";
   	ArrayList recordList=new ArrayList();
   	ArrayList valueList=new ArrayList();
   	try
   	{
   	   RowSet rs=dao.search(sql);
   	   while(rs.next())//liuyz 单人模版多人模版支持word输出
   	   {
	   	   String xmlContent=Sql_switcher.readMemo(rs,fieldname);
	   	   if(xmlContent==null||xmlContent.length()<=0) {
               return valueList;
           }
		   Document doc=PubFunc.generateDom(xmlContent.toString());
		   String str_path="/records";
		   XPath xpath=XPath.newInstance(str_path);
		   List childlist=xpath.selectNodes(doc);
		   Iterator r =childlist.iterator();
		   if(r.hasNext())//有记录
	       {
			   Element recordE=(Element)r.next(); 
			   records=recordE.getAttributeValue("columns");
		   }
		   str_path="/records/record";
		   xpath=XPath.newInstance(str_path);
		   List recordlist=xpath.selectNodes(doc);
		   Iterator c =recordlist.iterator();
		   while(c.hasNext())
		   {
			   Element record=(Element)c.next();
			   recordList.add(record.getText());
		   }
		   if(records==null||records.length()<=0) {
               return valueList;
           }
		   String[] recordnames=records.split("`");
		   int i=0;//子集中那个字段
		   if(columnName.trim().length()==0)//指标未构库
		   {
			   valueList.add("");
		   }else{
			   for(i=0;i<recordnames.length;i++)
			   {
				   if(recordnames[i].toLowerCase().equals(columnName.toLowerCase())) {
                       break;
                   }
			   }
			   SubSetDomain subDomain=new SubSetDomain(sub_domain);
			   ArrayList subFieldList = subDomain.getSubFieldList();
			   SubField subfield=null;
			   for(int fieldNum=0;fieldNum<subFieldList.size();fieldNum++)
			   {
				   subfield= (SubField) subFieldList.get(fieldNum);
				   if(subfield.getFieldname().toString().toLowerCase().equals(columnName.toLowerCase())) {
                       break;
                   }
			   }
			   if(i<recordnames.length){
				   for(int n=0;n<recordList.size();n++)
				   {
					   String values=recordList.get(n).toString();
					   if(values==null||values.length()<=0) {
                           continue;
                       }
					   String recordvalues[]=values.split("`",-1);
					   if(recordvalues.length!=recordnames.length) {
                           continue;
                       }
					   String value ="";
					   if(recordvalues[i].trim().length()>0&&subfield!=null&&subfield.getFieldItem()!=null&&subfield.getFieldItem().isDate())//bug 35812 报空指针异常。没有考虑subfield为null
					   {
						   value=this.formatDateFiledsetValue(recordvalues[i], String.valueOf(subfield.getPre()), Integer.valueOf(subfield.getSlop()));
					   }
					   else {
						   value=recordvalues[i];
						   if(value.endsWith("\n")) {
							   value = value.substring(0,value.length()-1);
						   }
					   }
					   valueList.add(value);
				   }
			   }
		   }
   	   }
   	}catch(Exception e)
   	{
   		e.printStackTrace();
   	}
   	return valueList;
   }

	/**
	 * 子集中格式化日期字符串
	 * 
	 * @param value
	 *            日期字段值 yyyy-mm-dd
	 * @param ext
	 *            扩展
	 * @return
	 */
	public String formatDateFiledsetValue(String value, String ext, int disformat) {
		StringBuffer buf = new StringBuffer();
		int idx = ext.indexOf(","); // -,至今
		String prefix = "", strext = "";
		if (idx == -1) {
			String[] preCond = getPrefixCond(ext);
			prefix = preCond[0];
		} else {
			prefix = ext.substring(0, idx);
			strext = ext.substring(idx + 1);
		}
		if ("".equals(value)) {
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		} else {
			buf.append(prefix);
		}
		value=value.replace(".", "-");
		Date date = DateUtils.getDate(value, "yyyy-MM-dd");
		int year = DateUtils.getYear(date);
		int month = DateUtils.getMonth(date);
		int day = DateUtils.getDay(date);
		String strv[] = exchangNumToCn(year, month, day);
		value = value.replace("-", ".");
		String temp="";
		switch (disformat) {
		case 0: // 1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 1: // 91.12.3
			temp = String.valueOf(year);
			buf.append(temp.substring(2));
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 2:// 1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);
			break;
		case 3:// 1992.02
			buf.append(year);
			buf.append(".");
			if (month > 9) {
				buf.append(month);
			} else {
				buf.append("0" + month);
			}

			break;
		case 4:// 92.2
			temp = String.valueOf(year);
			buf.append(temp.substring(2));
			buf.append(".");
			buf.append(month);
			break;
		case 5:// 98.02
			temp = String.valueOf(year);
			buf.append(temp.substring(2));
			buf.append(".");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			break;
		case 6:// 一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 7:// 一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			break;
		case 8:// 1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 9:// 1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 10:// 91年1月2日
			temp = String.valueOf(year);
			buf.append(temp.substring(2));
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 11:// 91年1月
			temp = String.valueOf(year);
			buf.append(temp.substring(2));
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 12:// 年龄
			buf.append(getAge(year, month, day));
			break;
		case 13:// 1991（年）
			buf.append(year);
			break;
		case 14:// 1 （月）
			buf.append(month);
			break;
		case 15:// 23 （日）
			buf.append(day);
			break;
		case 16:// 1999年02月
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 17:// 1999年02月03日
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			buf.append("日");
			break;
		case 18:// 1992.02.01
			buf.append(year);
			buf.append(".");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		}
		return buf.toString();
	}

	/**
	 * 计算年龄
	 * 
	 * @param nyear
	 * @param nmonth
	 * @param nday
	 * @return
	 */
	private String getAge(int nyear, int nmonth, int nday) {
		int ncyear, ncmonth, ncday;
		Date curdate = new Date();
		ncyear = DateUtils.getYear(curdate);
		ncmonth = DateUtils.getMonth(curdate);
		ncday = DateUtils.getDay(curdate);
		StringBuffer buf = new StringBuffer();

		int result = ncyear - nyear;
		if (nmonth > ncmonth) {
			result = result - 1;
		} else {
			if (nmonth == ncmonth) {
				if (nday > ncday) {
					result = result - 1;
				}
			}
		}
		buf.append(result);
		return buf.toString();
	}

	/**
	 * 数字换算
	 * 
	 * @param strV
	 * @param flag
	 * @return
	 */
	private String[] exchangNumToCn(int year, int month, int day) {
		String[] strarr = new String[3];
		StringBuffer buf = new StringBuffer();
		String value = String.valueOf(year);
		for (int i = 0; i < value.length(); i++) {
			switch (value.charAt(i)) {
			case '1':
				buf.append("一");
				break;
			case '2':
				buf.append("二");
				break;
			case '3':
				buf.append("三");
				break;
			case '4':
				buf.append("四");
				break;
			case '5':
				buf.append("五");
				break;
			case '6':
				buf.append("六");
				break;
			case '7':
				buf.append("七");
				break;
			case '8':
				buf.append("八");
				break;
			case '9':
				buf.append("九");
				break;
			case '0':
				buf.append("零");
				break;
			}
		}
		strarr[0] = buf.toString();
		buf.setLength(0);
		switch (month) {
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;
		}
		strarr[1] = buf.toString();
		buf.setLength(0);
		switch (day) {
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;
		case 13:
			buf.append("十三");
			break;
		case 14:
			buf.append("十四");
			break;
		case 15:
			buf.append("十五");
			break;
		case 16:
			buf.append("十六");
			break;
		case 17:
			buf.append("十七");
			break;
		case 18:
			buf.append("十八");
			break;
		case 19:
			buf.append("十九");
			break;
		case 20:
			buf.append("二十");
			break;
		case 21:
			buf.append("二十一");
			break;
		case 22:
			buf.append("二十二");
			break;
		case 23:
			buf.append("二十三");
			break;
		case 24:
			buf.append("二十四");
			break;
		case 25:
			buf.append("二十五");
			break;
		case 26:
			buf.append("二十六");
			break;
		case 27:
			buf.append("二十七");
			break;
		case 28:
			buf.append("二十八");
			break;
		case 29:
			buf.append("二十九");
			break;
		case 30:
			buf.append("三十");
			break;
		case 31:
			buf.append("三十一");
			break;
		}
		strarr[2] = buf.toString();
		return strarr;
	}

	/**
	 * 解释Formula字段的内容 for example ssssfsf<EXPR>1+2</EXPR>
	 * <FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * 
	 * @return
	 */
	private String[] getPrefixCond(String formula) {
		String[] preCond = new String[3];
		int idx = formula.indexOf("<");
		if (idx == -1) {
			preCond[0] = formula;
		} else {
			preCond[0] = formula.substring(0, idx);
			preCond[2] = getPattern("FACTOR", formula) + ",";
			preCond[2] = preCond[2].replaceAll(",", "`");
			preCond[1] = getPattern("EXPR", formula);
		}
		return preCond;
	}

	private String getPattern(String strPattern, String formula) {
		int iS, iE;
		String result = "";
		String sSP = "<" + strPattern + ">";
		iS = formula.indexOf(sSP);
		String sEP = "</" + strPattern + ">";
		iE = formula.indexOf(sEP);
		if (iS >= 0 && iS < iE) {
			result = formula.substring(iS + sSP.length(), iE);
		}
		return result;
	}
}
