/*
 * Created on 2005-5-9
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.businessobject.parse.parsebusiness;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Factor {
	private String set; //保存的子集或者主集
	private String item; //保存的因子的field
	private String oper; //保存因子的操作符
	private String value; //保存因子的值
	private String usertype = ""; //人员的类型
	private String fac; //中间变量保存因子的目前没有用
	private String cfieldsetid; //子集或者住集码
	private String citemdesc; //指标的描述
	private String ccodesetid;
	private String citemtype = ""; //指标类型
	private String cresult = ""; //分析因子的结果sql
	private int year = Calendar.getInstance().get(Calendar.YEAR); //获取当前的年
	private int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //获取当前的月
	private int day = Calendar.getInstance().get(Calendar.DATE); //获取当前的日
	private String curdate = year + "." + month + "." + day; //生成当前的日期;
	private StringBuffer sql =new StringBuffer();
	/*
	 * 分析单个因子成生成该因子对应的条件sql项
	 * */
	public Factor(String usertype, String factor) {
		this.fac = factor;
		String userBase = usertype;
		this.item = factor.substring(0, 5);        //取出因子的field名称
		if ("E01A1".equals(this.item) || "B0110".equals(this.item)) {    //两个指标做特殊处理
			if ("E01A1".equals(this.item)) {
				this.set = userBase + "A01";
				this.cfieldsetid = "A01";
				citemdesc = "岗位编码";
				ccodesetid = "A";
				citemtype = "A";
			} else {
				this.set = userBase + "A01";
				this.cfieldsetid = "A01";
				citemdesc = "机构编码";
				ccodesetid = "A";
				citemtype = "A";
			}
		} else {
			sql.append("select fieldsetid,itemdesc,codesetid,itemtype from fielditem where ");
			sql.append("itemid='");
			sql.append(this.item);
			sql.append("'");
			ResultSet rset = null;
			Statement stmt = null;
			Connection conn = null;	
			try {
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				rset = dao.search(sql.toString());
				sql.delete(0,sql.length());
				if(rset.next())
				{
					this.set = userBase + rset.getString("fieldsetid");
					this.cfieldsetid = rset.getString("fieldsetid");
					citemdesc = rset.getString("itemdesc");
					ccodesetid = rset.getString("codesetid");
					citemtype = rset.getString("itemtype");
				}
				else
				{
					this.set=null;
					this.cfieldsetid=null;
					citemdesc=null;
					ccodesetid=null;
					citemtype=null;
				}
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}
			catch (GeneralException ge){
				ge.printStackTrace();
			}
			finally{
			  try{
				if (rset != null){
				 rset.close();
				}

			   if (conn != null){
				 conn.close();
			   }
			  }catch (SQLException sqldesc){
			  	sqldesc.printStackTrace();
			 }
			}
		}
		getCResultDifType(factor);
	}
	/**
	 * @param factor
	 */
	private void getCResultDifType(String factor) {
		/*
		 * 指标类型是A、M时
		 * */
		if ("A".equals(this.citemtype) || "M".equals(this.citemtype)) {
			//操作符是<>时
			if (factor.indexOf("<>") != -1) {
				this.oper =factor.substring(factor.indexOf("<>"),factor.indexOf("<>") + 2);
				this.value = factor.substring(factor.indexOf("<>") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				//如果在因子中含有%%用%代替
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="(" + this.set + "."	+ this.item	+ " IS NOT NULL OR " + this.set	+ "." + this.item + this.oper + "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
					//是否加有待讨论??????????????????????????
					this.cresult += " OR (" + this.item + " IS  NULL " + ")";
				}
				//操作符是>=时
			} else if (factor.indexOf(">=") != -1) {
				this.oper =factor.substring(factor.indexOf(">="),factor.indexOf(">=") + 2);
				this.value = factor.substring(factor.indexOf(">=") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="(" + this.set + "."	+ this.item	+ " IS NOT NULL OR "+ this.set	+ "." + this.item + this.oper + "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
				}
				// 操作符是<=时
			} else if (factor.indexOf("<=") != -1) {
				this.oper =factor.substring(factor.indexOf("<="),factor.indexOf("<=") + 2);
				this.value = factor.substring(factor.indexOf("<=") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="(" + this.set + "."	+ this.item	+ " IS NULL OR " + this.set	+ "." + this.item + this.oper + "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
				}
				//操作符是=时
			} else if (factor.indexOf("=") != -1) {
				this.oper =factor.substring(factor.indexOf("="),factor.indexOf("=") + 1);
				this.value = factor.substring(factor.indexOf("=") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="(" + this.set + "."	+ this.item	+ " IS NULL OR " + this.set	+ "." + this.item + this.oper + "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult = "(" + this.set + "." + this.item + " LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult = "(" + this.set + "." + this.item + " LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + "  LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
				}
				//操作符是>时
			} else if (factor.indexOf(">") != -1) {
				this.oper =factor.substring(factor.indexOf(">"),factor.indexOf(">") + 1);
				this.value = factor.substring(factor.indexOf(">") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="(" + this.set + "."	+ this.item	+ " IS NOT NULL OR "+ this.set	+ "." + this.item	+ this.oper	+ "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replaceAll("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
				}
				//操作符是<时
			} else if (factor.indexOf("<") != -1) {
				this.oper =factor.substring(factor.indexOf("<"),factor.indexOf("<") + 1);
				this.value = factor.substring(factor.indexOf("<") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set	+ "."+ this.item+ " IS NULL OR "+ this.set	+ "." + this.item + this.oper + "'')";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else if (this.value.indexOf("_") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult ="(" + this.set + "." + this.item + this.oper + "'" + this.value + "')";
				}
			}
			/*
			 * 指标类型是D时
			 * */
		} else if("D".equals(this.citemtype)) {
			//操作符是<>时
			if (factor.indexOf("<>") != -1) {
				this.oper =factor.substring(factor.indexOf("<>"),factor.indexOf("<>") + 2);
				this.value = factor.substring(factor.indexOf("<>") + 2);
				//操作符是>=时
			} else if (factor.indexOf(">=") != -1) {
				this.oper =factor.substring(factor.indexOf(">="),factor.indexOf(">=") + 2);
				this.value = factor.substring(factor.indexOf(">=") + 2);
				//操作符是<=时
			} else if (factor.indexOf("<=") != -1) {
				this.oper =factor.substring(factor.indexOf("<="),factor.indexOf("<=") + 2);
				this.value = factor.substring(factor.indexOf("<=") + 2);
				//操作符是=时
			} else if (factor.indexOf("=") != -1) {
				this.oper =factor.substring(factor.indexOf("="),factor.indexOf("=") + 1);
				this.value = factor.substring(factor.indexOf("=") + 1);
				//操作符是>时
			} else if (factor.indexOf(">") != -1) {
				this.oper =factor.substring(factor.indexOf(">"),factor.indexOf(">") + 1);
				this.value = factor.substring(factor.indexOf(">") + 1);
				//操作符是<时
			} else if (factor.indexOf("<") != -1) {
				this.oper =factor.substring(factor.indexOf("<"),factor.indexOf("<") + 1);
				this.value = factor.substring(factor.indexOf("<") + 1);
			}
			this.cresult = getDateExpr(this.set, this.item, this.oper, this.value);
		}
		/*
		 * 指标类型是N时
		 * */
		else if ("N".equals(this.citemtype)) {
			//操作符是<>时
			if (factor.indexOf("<>") != -1) {
				this.oper =factor.substring(factor.indexOf("<>"),factor.indexOf("<>") + 2);
				this.value = factor.substring(factor.indexOf("<>") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set	+ "."+ this.item+ " IS NOT NULL OR "+ this.set+ "."	+ this.item	+ this.oper	+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.item + oper + this.value + ")";
				}
				//操作符是>=时
			} else if (factor.indexOf(">=") != -1) {
				this.oper =factor.substring(factor.indexOf(">="),factor.indexOf(">=") + 2);
				this.value = factor.substring(factor.indexOf(">=") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set	+ "."+ this.item+ " IS NOT NULL OR "+ this.set	+ "."+ this.item+ this.oper	+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.set + "." + this.item + this.oper + this.value + ")";
				}
				//操作符是<=时
			} else if (factor.indexOf("<=") != -1) {
				this.oper =factor.substring(factor.indexOf("<="),factor.indexOf("<=") + 2);
				this.value = factor.substring(factor.indexOf("<=") + 2);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set+ "."+ this.item+ " IS NULL OR "+ this.set+ "."+ this.item+ this.oper+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.set + "." + this.item + this.oper + this.value + ")";
				}
				//操作符是=时
			} else if (factor.indexOf("=") != -1) {
				this.oper =factor.substring(factor.indexOf("="),factor.indexOf("=") + 1);
			    this.value = factor.substring(factor.indexOf("=") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set	+ "."+ this.item+ " IS NULL OR "+ this.set	+ "."+ this.item+ this.oper	+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult = "(" + this.set + "." + this.item + " LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult = "(" + this.set + "." + this.item + " LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.set + "." + this.item + this.oper + this.value + ")";
				}
				//操作符是>时
			} else if (factor.indexOf(">") != -1) {
				this.oper =factor.substring(factor.indexOf(">"),factor.indexOf(">") + 1);
				this.value = factor.substring(factor.indexOf(">") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set+ "."+ this.item+ " IS NOT NULL OR "+ this.set	+ "."+ this.item+ this.oper	+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.set + "." + this.item + this.oper + this.value + ")";
				}
				//操作符是<时
			} else if (factor.indexOf("<") != -1) {
				this.oper =factor.substring(factor.indexOf("<"),factor.indexOf("<") + 1);
				this.value = factor.substring(factor.indexOf("<") + 1);
				//如果在因子值中含有*用%代替
				this.value = this.value.replace("*", "%");
				this.value = this.value.replaceAll("%%", "%");
				if (this.value == null
					|| !(this.value.length() > 0)
					|| this.value == ""
					|| "null".equalsIgnoreCase(this.value)) {
					this.cresult ="("+ this.set	+ "."+ this.item+ " IS NULL OR "	+ this.set	+ "."+ this.item+ this.oper	+ "0)";
				}
				//在因子的值中含有?时的处理
				else if (this.value.indexOf("?") != -1) {
					this.value = this.value.replace("?", "_");
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				}
				//在因子的值中含有%时的处理
				else if (this.value.indexOf("%") != -1) {
					this.cresult ="(" + this.set + "." + this.item + " NOT LIKE '" + this.value + "')";
				} else {
					this.cresult = "(" + this.set + "." + this.item + this.oper + this.value + ")";
				}
			}
		}
	}
	/*
	 * 如果值是日期的分析生成对应的sql
	 * */
	public String getDateExpr(
		String cTable,
		String cField,
		String cOper,
		String cValue) {
		String dateResult = "";
		try {
			if (cValue.indexOf("$THISMONTH") != -1) {
				dateResult ="DATEPART(month,"+ cTable+ "."+ cField	+ ")"+ cOper+ month;	
			} else if (cValue.indexOf("$THISYS") != -1) {
				dateResult ="DATEPART(year,"+ cTable+ "."	+ cField+ ")"	+ cOper	+ year;
			} else if (cValue.indexOf("$YRS") != -1) {
				dateResult ="(CASE WHEN "+ cTable	+ "."+ cField	+ " IS NULL THEN 0 ELSE (  (DATEPART(year,"
						+ "'"	+ this.curdate	+ "'" + ")- DATEPART(year,"	+ cTable+ "."	+ cField + "))*10000+(DATEPART(month,"
						+ "'"	+ this.curdate	+ "'"	+ ")- DATEPART(month,"	+ cTable+ "."	+ cField + "))*100+DATEPART(day,"
						+ "'"	+ this.curdate	+ "'"	+ ")- DATEPART(day,"	+ cTable+ "."	+ cField + ") )/ 10000 END    )"
						+ cOper	+ cValue.substring(cValue.indexOf("[") + 1,	cValue.indexOf("]"));
			} else {
				int nI = 0;
				int nPos1;
				String cVar;
				int nExpr = 0;
				String cDate = cValue;
				do {
					nPos1 = cDate.indexOf(".");
					if (nPos1 == -1) {
						cVar = cDate;
						cDate = "";
					} else {
						cVar = cDate.substring(0, nPos1);
					    cDate = cDate.substring(nPos1 + 1, cDate.length());
					}
					if (cVar.indexOf("?") != -1) {
						nI += 1;
						continue;
					}
					Calendar cal = Calendar.getInstance();
					if("当年".equals(cVar)) {
                        cVar = ""+cal.get(Calendar.YEAR);
                    }
					switch (nI) {
						case 0 :
							{
								if ("oracle".equals(SystemConfig.getDataSourceName())) {
									dateResult = "";
								} else {
									dateResult ="Year("	+ cTable+ "."+ cField+ ") * 10000";
									if (cVar != null && cVar.length() > 0) {
                                        nExpr = Integer.parseInt(cVar) * 10000;
                                    }
								}
								break;
							}
						case 1 :
							{
								if ("oracle".equals(SystemConfig.getDataSourceName())) {

								} else {
									dateResult += " + "	+ "Month("	+ cTable+ "."+ cField+ ")*100";
									if (cVar != null && cVar.length() > 0) {
                                        nExpr =
                                            nExpr
                                                + Integer.parseInt(cVar) * 100;
                                    }
								}
								break;
							}
						case 2 :
							{
								if ("oracle".equals(SystemConfig.getDataSourceName())) {
								} else {
									dateResult += " + "	+ "Day("+ cTable+ "."+ cField+ ")";
									if (cVar != null && cVar.length() > 0) {
                                        nExpr = nExpr + Integer.parseInt(cVar);
                                    }
								}
								break;
							}
					}
					nI += 1;
				} while (!"".equals(cDate));
				dateResult += cOper + nExpr;
			}
		} catch (Exception e) {
			System.out.println("时间类型因子值转换sql出错!");
		}
		return dateResult;
	}
	/**
	 * @return Returns the ccodesetid.
	 */
	public String getCcodesetid() {
		return ccodesetid;
	}
	/**
	 * @param ccodesetid The ccodesetid to this.set.
	 */
	public void setCcodesetid(String ccodesetid) {
		this.ccodesetid = ccodesetid;
	}
	/**
	 * @return Returns the cfieldsetid.
	 */
	public String getCfieldsetid() {
		return cfieldsetid;
	}
	/**
	 * @param cfieldsetid The cfieldsetid to this.set.
	 */
	public void setCfieldsetid(String cfieldsetid) {
		this.cfieldsetid = cfieldsetid;
	}
	/**
	 * @return Returns the citemtype.
	 */
	public String getCitemtype() {
		return citemtype;
	}
	/**
	 * @param citemtype The citemtype to set.
	 */
	public void setCitemtype(String citemtype) {
		this.citemtype = citemtype;
	}
	/**
	 * @return Returns the this.cresult.
	 */
	public String getCresult() {
		return cresult;
	}
	/**
	 * @param cresult The cresult to set.
	 */
	public void setCresult(String cresult) {
		this.cresult = cresult;
	}
	/**
	 * @return Returns the cttemdesc.
	 */
	public String getCitemdesc() {
		return citemdesc;
	}
	/**
	 * @param cttemdesc The cttemdesc to set.
	 */
	public void setCitemdesc(String citemdesc) {
		this.citemdesc = citemdesc;
	}
	/**
	 * @return Returns the curdate.
	 */
	public String getCurdate() {
		return curdate;
	}
	/**
	 * @param curdate The curdate to set.
	 */
	public void setCurdate(String curdate) {
		this.curdate = curdate;
	}
	/**
	 * @return Returns the fac.
	 */
	public String getFac() {
		return fac;
	}
	/**
	 * @param fac The fac to set.
	 */
	public void setFac(String fac) {
		this.fac = fac;
	}
	/**
	 * @return Returns the this.item.
	 */
	public String getItem() {
		return item;
	}
	/**
	 * @param item The item to set.
	 */
	public void setItem(String item) {
		this.item = item;
	}
	/**
	 * @return Returns the oper.
	 */
	public String getOper() {
		return oper;
	}
	/**
	 * @param oper The oper to set.
	 */
	public void setOper(String oper) {
		this.oper = oper;
	}
	/**
	 * @return Returns the usertype.
	 */
	public String getUsertype() {
		return usertype;
	}
	/**
	 * @param usertype The usertype to set.
	 */
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	/**
	 * @return Returns the this.value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return Returns the set.
	 */
	public String getSet() {
		return set;
	}
	/**
	 * @param set The set to set.
	 */
	/*public void setSet(String set) {
		item = fac.substring(0, 5);
		if ("E01A1".equals(item) || "B0110".equals(item)) {
			set = usertype + "A01";
		} else {
			StringBuffer sql =new StringBuffer();
			sql
				"select fieldsetid from fielditem where "
					+ "itemid='"
					+ item
					+ "'";
			try {
				rs = db.execQuery(sql);
				set = userBase + rs.getString(0, "fieldsetid");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }*/
}
