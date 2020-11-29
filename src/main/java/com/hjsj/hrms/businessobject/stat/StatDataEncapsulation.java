/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.stat;

import com.hjsj.hrms.businessobject.stat.crosstab.CrossTabStat;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatDataEncapsulation {
	
	 /**
	   * 日志跟踪器
	   */
	protected Category cat = Category.getInstance(this.getClass());

	public StatDataEncapsulation() {}
	StatCondAnalyse cond = new StatCondAnalyse();
	private String[] display;
	private String[] norder_display;
    private int recordCount = 0;
	private List verticalArray=new ArrayList();
	private List horizonArray=new ArrayList();
	private String SNameDisplay = "";
	private int totalValue = 0;
	private double totalValues=0;
	private String whereIN;
	// zgd 二维交叉统计表 start
	private List verticalFirstArray=new ArrayList();//第一行标题
	private List verticalSecondArray=new ArrayList();//第二行标题
	private List horizonFirstArray=new ArrayList();//第一列标题
	private List horizonSecondArray=new ArrayList();//第二列标题
	// zgd 二维交叉统计表 end
	private Connection conn = null;
	
	public void setConn(Connection conn) {
        this.conn = conn;
    }
    public String getWhereIN() {
		return whereIN;
	}
	public void setWhereIN(String whereIN) {
		this.whereIN = whereIN;
	}
	public int[] getLexprData(	
			ArrayList conditionlist,      /*name|表达式|因子*/
			boolean ishavehistory,         /*历史纪录*/
			UserView userView,            
			String pre,                     /*人员库*/
			String infokind,boolean bresult) {               
			int[] fieldValues =new int[conditionlist.size()];
			display = new String[conditionlist.size()];
			try {
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String scopeQuery="";
				//获得统计项	
				ArrayList conditem=new ArrayList();
				cat.debug("condition " + conditionlist);
			    for(int i=0;i<conditionlist.size();i++)
			    {
			    	conditem=getConditionItem(conditionlist.get(i).toString());
			    	display[i]=(String)conditem.get(0);
			    	strLexpr=(String)conditem.get(1);
			    	strFactor=(String)conditem.get(2);
			        //根据表达式和因子生成统计的sql语句
			    	cat.debug("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
			    	strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind)) {
                        strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct K01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    }
			    	List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
					}
			    }
				return fieldValues;
			} catch (Exception e) {
				System.out.println("查询图例出错!");
			}
			return null;
		}
	public int[] getLexprData(	
			ArrayList conditionlist,      /*name|表达式|因子*/
			boolean ishavehistory,         /*历史纪录*/
			UserView userView,            
			String pre,                     /*人员库*/
			String infokind,boolean bresult,String userbases) {               
			int[] fieldValues =new int[conditionlist.size()];
			display = new String[conditionlist.size()];
			try {
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String scopeQuery="";
				//获得统计项	
				ArrayList conditem=new ArrayList();
				cat.debug("condition " + conditionlist);
			    for(int i=0;i<conditionlist.size();i++)
			    {
			    	conditem=getConditionItem(conditionlist.get(i).toString());
			    	display[i]=(String)conditem.get(0);
			    	strLexpr=(String)conditem.get(1);
			    	strFactor=(String)conditem.get(2);
			        //根据表达式和因子生成统计的sql语句
			    	cat.debug("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
			    	strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind)){
			    		StringBuffer sb = new StringBuffer();
						String tmpsql = ("select distinct " + pre + "a01.a0100 as a0100" + strQuery).toUpperCase();
						if(userbases.indexOf("`")==-1){
							sb.append(tmpsql.replaceAll(pre, userbases));
						}else{
							String[] tmpdbpres=userbases.split("`");
							for(int n=tmpdbpres.length-1;n>=0;n--){
								String tmpdbpre=tmpdbpres[n];
								if(tmpdbpre.length()==3){
									if(sb.length()>0){
										sb.append(" union all "+tmpsql.replaceAll(pre, tmpdbpre));
									}else{
										sb.append(tmpsql.replaceAll(pre, tmpdbpre));
									}
								}
							}
						}
						strQuery = "select count(a0100) as lexprData from (" + sb.toString()+") tt";
			         	//strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	}else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct K01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    }
			    	List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
					}
			    }
				return fieldValues;
			} catch (Exception e) {
				System.out.println("查询图例出错!");
			}
			return null;
		}
	public int[] getLexprData(	
			ArrayList conditionlist,      /*name|表达式|因子*/
			boolean ishavehistory,         /*历史纪录*/
			UserView userView,            
			String pre,                     /*人员库*/
			String infokind,boolean bresult,String scopeQuery,boolean blike) {               
			int[] fieldValues =new int[conditionlist.size()];
			display = new String[conditionlist.size()];
			try 
			{
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				//获得统计项	
				ArrayList conditem=new ArrayList();
				cat.debug("condition " + conditionlist);
			    for(int i=0;i<conditionlist.size();i++)
			    {
			    	conditem=getConditionItem(conditionlist.get(i).toString());
			    	display[i]=(String)conditem.get(0);
			    	strLexpr=(String)conditem.get(1);
			    	strFactor=(String)conditem.get(2);
			        //根据表达式和因子生成统计的sql语句
			    	cat.debug("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
			    	strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult,blike);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind)) {
                        strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    }
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
					}
			    }
				return fieldValues;
			} catch (Exception e) {
				System.out.println("查询图例出错!");
				e.printStackTrace();
			}
			return null;
		}
	public int[] getLexprData(	
			ArrayList conditionlist,      /*name|表达式|因子*/
			boolean ishavehistory,         /*历史纪录*/
			UserView userView,            
			String pre,                     /*人员库*/
			String infokind,boolean bresult,String scopeQuery,boolean blike,String userbases) {               
			int[] fieldValues =new int[conditionlist.size()];
			display = new String[conditionlist.size()];
			try 
			{
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				//获得统计项	
				ArrayList conditem=new ArrayList();
				cat.debug("condition " + conditionlist);
			    for(int i=0;i<conditionlist.size();i++)
			    {
			    	conditem=getConditionItem(conditionlist.get(i).toString());
			    	display[i]=(String)conditem.get(0);
			    	strLexpr=(String)conditem.get(1);
			    	strFactor=(String)conditem.get(2);
			        //根据表达式和因子生成统计的sql语句
			    	cat.debug("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
			    	strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult,blike);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind)){
			    		StringBuffer sb = new StringBuffer();
						String tmpsql = ("select distinct " + pre + "a01.a0100 as a0100" + strQuery).toUpperCase();
						if(userbases.indexOf("`")==-1){
							sb.append(tmpsql.replaceAll(pre, userbases));
						}else{
							String[] tmpdbpres=userbases.split("`");
							for(int n=tmpdbpres.length-1;n>=0;n--){
								String tmpdbpre=tmpdbpres[n];
								if(tmpdbpre.length()==3){
									if(sb.length()>0){
										sb.append(" union all "+tmpsql.replaceAll(pre, tmpdbpre));
									}else{
										sb.append(tmpsql.replaceAll(pre, tmpdbpre));
									}
								}
							}
						}
						strQuery = "select count(a0100) as lexprData from (" + sb.toString()+") tt";
			    		
			         	//strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	}else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
                    }
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
					}
			    }
				return fieldValues;
			} catch (Exception e) {
				System.out.println("查询图例出错!");
				e.printStackTrace();
			}
			return null;
		}

	/*拆分标题,表达式和因子*/
	private ArrayList getConditionItem(String conditem)
	{
		ArrayList items=new ArrayList();
		StringTokenizer Stok = new StringTokenizer(conditem, "|");
		if(Stok.hasMoreTokens()) {
            items.add(Stok.nextToken());
        }
		if(Stok.hasMoreTokens()) {
            items.add(Stok.nextToken());
        }
		if(Stok.hasMoreTokens()) {
            items.add(Stok.nextToken());
        }
		return items;
	}
	
	
	//合并表达式
	public String[] getCombinLexprFactor(String lexpr,String factor,String seclexpr,String secfactor)
	{
		String[] style=new String[2];
		ArrayList lexprFactor=new ArrayList();
		factor = PubFunc.keyWord_reback(factor);
		lexprFactor.add(lexpr + "|" + factor);
		lexprFactor.add(seclexpr + "|" + secfactor);
		CombineFactor combinefactor=new CombineFactor();
		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		if(Stok.hasMoreTokens())
		{
			style[0]=Stok.nextToken();
			style[1]=Stok.nextToken();
		}
		return style;
	}
	
	
	//该方法是获得图例的各个统计项的数值保存到数组变量fieldValues并返回
	
	public int[] getLexprData(
		String userbase,
		int queryId,
		String sqlSelect,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws Exception {
		int[] fieldValues;
		//try {
			
			
			String userBase = userbase;
			String strFactor = "";
			String strLexpr = "";
			String strQuery = "";
			String flag="";
			//获得统计项
			StringBuffer sql =new StringBuffer();
			sql.append("select * from SName where id=");
			sql.append(queryId);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
			}
			//获得各个统计项的图例
			sql.delete(0,sql.length());
			sql.append("select * from SLegend where id=");
			sql.append(queryId);          
			sql.append(" order by norder");          
			List dataset = ExecuteSQL.executeMyQuery(sql.toString());
			fieldValues = new int[dataset.size()];
			display = new String[dataset.size()];
			norder_display=new String[dataset.size()];
			int lenght = 1;
			if("1,2,3".equals(infokind)){
				lenght = 3;
			}
			for(int n=1;n<=lenght;n++){
				//liuy 2014-10-24 4659：机构的统计分析图在首页上展示，展示的统计结果不对  start
				/*if(!"1,2,3".equals(infokind)){
					infokind = String.valueOf(n);
				}*/
				if(lenght!=1){						
					infokind = String.valueOf(n);
				}
				//liuy end
				for (int i = 0; i < dataset.size(); i++) {
					LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
					strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
					strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
					flag=rec.get("flag")!=null?rec.get("flag").toString():"";
					//System.out.println(strFactor);
					display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
					norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
					//根据表达式和因子生成统计的sql语句
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
					if(commlexpr!=null && commfactor!=null)
					{
						//CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 2017/09/07
						commfactor = commfactor.replaceAll("\\|", "~");
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						if(style!=null && style.length==2)
						{
							strLexpr=style[0];
							strFactor=style[1];
						}
					}
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					boolean ishavehistory=false;
					if("1".equals(flag)) {
                        ishavehistory=true;
                    } else if("1".equals(history)) {
                        ishavehistory=true;
                    }
					strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,userView,infokind,bresult);
					//cat.debug("---->sql======" + strQuery);
					//System.out.println(strQuery);
					if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
					{
						strQuery=strQuery+" and "+this.getWhereIN();
					}	
					//System.out.println(strQuery);
					if("1".equals(infokind)) {
                        strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                    } else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                    }
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
					}
				}
			}
			return fieldValues;
		/*} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询图例出错!");			
		}finally
		{
			//exeSql.freeConn();
	    }
		return null;*/
	}
	
	public double[] getLexprDataSformula(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn) throws Exception {
			double[] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				String decimalwidth=element.getAttributeValue("decimalwidth");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
				int lenght = 1;
				if("1,2,3".equals(infokind)){
					lenght = 3;
				}
				String type=element.getAttributeValue("type");
				String expr=element.getText();
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
						Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userbase);
				yp.setCon(conn);
				//yp.setTempTableName("temp_"+username + queryId);
				yp.run(expr);
				
				String field = yp.getSQL();
				ArrayList usedsets = yp.getUsedSets();
				for(int m=1;m<=lenght;m++){
					// bug 38808 wangb 20180725 菜单显示和点配置项统计图显示人数不一致
					if(lenght == 3/*"1,2,3".equals(infokind)*/){
						infokind = String.valueOf(m);
					}
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							//CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 2017/09/07
							commfactor = commfactor.replaceAll("\\|", "~");
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						boolean ishavehistory=false;
						if("1".equals(flag)) {
                            ishavehistory=true;
                        } else if("1".equals(history)) {
                            ishavehistory=true;
                        }
								strQuery =cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,userView,infokind,bresult);
						//cat.debug("---->sql======" + strQuery);
								//System.out.println(strQuery);
						if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
						{
							strQuery=strQuery+" and "+this.getWhereIN();
						}
						strQuery = strQuery.toUpperCase();
						
						
						String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
						for(int n=0;n<usedsets.size();n++){
							String set = (String)usedsets.get(n);
							if("1".equals(infokind)){
								set = (" "+userbase+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								/*if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}*/
								
								if(basesql.indexOf(set)==-1){
									sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
									sb.append(basesql.substring(basesql.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100) OR "+set+".I9999 IS NULL)");
									basesql= sb.toString();
								}
							}else if("2".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}else if("3".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}
						}
                        if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
                            StringBuffer sb = new StringBuffer();
                            if("1".equals(infokind)){
                                sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userbase+"A01.A0100="+yp.getTempTableName()+".A0100");
                                sb.append(basesql.substring(basesql.indexOf(" WHERE")));
                                basesql= sb.toString();
                            } else  if("2".equals(infokind)) {
                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
                                strQuery= sb.toString();
                            } else if("3".equals(infokind)) {
                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/ 
                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1"); 
                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
                                strQuery= sb.toString();
                            }
                        }						
						//System.out.println(strQuery);
						if("1".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + basesql+" and "+userbase+"a01.a0100 in(select "+userbase+"a01.a0100 "+ strQuery+")";//update by xiegh on 20170922 后台报a0100不明确
                        } else if("2".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        } else if("3".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        }
						//System.out.println(strQuery);
						if(Sql_switcher.searchDbServer() == 1 && "avg".equalsIgnoreCase(type) && Integer.parseInt(decimalwidth)>0){//sql server 数据库  平均值返回整数 处理   wangb  20190514
							strQuery = strQuery.replace(type+"("+field+")", "convert(decimal(15,"+decimalwidth+"),"+type+"("+field+"+0.0))");
						}
						List rsset = ExecuteSQL.executeMyQuery(strQuery);
						if (rsset != null && rsset.size()>0) {
							//保存该图例的统计数
							LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
							String tmp=rdata.get("lexprdata").toString();
							if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                tmp="0";
                            }
							fieldValues[i] = Double.parseDouble(tmp);
						}
					}
				}
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	public int[] getLexprData(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases) throws Exception {
			int[] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
				// 统计类型:1-人员 2-机构 3-岗位
				String temp_infokind = "";
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
				//获得统计项
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
					temp_infokind = rec.get("infokind") != null?rec.get("infokind").toString():"";
					/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
				}
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new int[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
				int lenght = 1;
				if("1,2,3".equals(infokind)){
					lenght = 3;
				}
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
				if("1,2,3".equals(infokind) && StringUtils.isNotEmpty(temp_infokind)){
					infokind = temp_infokind;
				}
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
				//for(int m=1;m<=lenght;m++){
					/*if("1,2,3".equals(infokind)){
						infokind = String.valueOf(m);
					}*/
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null){
							//xiegh CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 2017/09/07
							commfactor = commfactor.replaceAll("\\|", "~");
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						boolean ishavehistory=false;
						if("1".equals(flag)) {
                            ishavehistory=true;
                        } else if("1".equals(history)) {
                            ishavehistory=true;
                        }
								strQuery =cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,userView,infokind,bresult);
						//cat.debug("---->sql======" + strQuery);
								//System.out.println(strQuery);
						if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
						{
							strQuery=strQuery+" and "+this.getWhereIN();
						}	
						//System.out.println(strQuery);
						if("1".equals(infokind)){
							StringBuffer sb = new StringBuffer();
							String tmpsql = ("select distinct " + userbase + "a01.a0100 as a0100" + strQuery).toUpperCase();
							if(userbases.indexOf("`")==-1){
								sb.append(tmpsql.replaceAll(userbase, userbases));
							}else{
								String[] tmpdbpres=userbases.split("`");
								for(int n=tmpdbpres.length-1;n>=0;n--){
									String tmpdbpre=tmpdbpres[n];
									if(tmpdbpre.length()==3){
										if(sb.length()>0){
											sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
										}else{
											sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
										}
									}
								}
							}
							strQuery = "select count(a0100) as lexprData from (" + sb.toString()+") tt";
				         	//strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
						}else if("2".equals(infokind)) {
                            strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        } else if("3".equals(infokind)) {
                            strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        }
						//System.out.println(strQuery);
						List rsset = ExecuteSQL.executeMyQuery(strQuery);
						if (rsset != null && rsset.size()>0) {
							//保存该图例的统计数
							LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
							fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
						}
					}
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 start */
				//}
				/* 标识：2249 主页常用统计点更多进去和单位管理/信息维护/统计分析，“单位编制情况”统计出来的结果不同 xiaoyun 2014-7-3 end */
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	public double[] getLexprDataSformula(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn) throws Exception {
			double[] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				String decimalwidth = element.getAttributeValue("decimalwidth");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
//				int lenght = 1;
				String newInfo = infokind;//add by wangchaoqun on 2014-9-25 保存info的值便于后面的判断
				if("1,2,3".equals(newInfo)){
					infokind = currInfoKind(queryId);// add by liubq 2015-09-28 当infokind为"1,2,3"时,获取统计的infokind（1人,2单,3岗）
//					lenght = 3;
				}
//				for(int m=1;m<=lenght;m++){
//					if("1,2,3".equals(newInfo)){
//						infokind = String.valueOf(m);
//					}
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
						Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userbase);
				yp.setCon(conn);
//				yp.setTempTableName("temp_"+username + queryId); // 没有此临时表   wangb 20181023  bug 41306
				String type=element.getAttributeValue("type");
				String expr=element.getText();
				yp.run(expr);
				String field = yp.getSQL();
				ArrayList usedsets = yp.getUsedSets();
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							//CombineFactor把多个因子表达式组合成一个表达式，也是用|作为分割符，所以对于字符型多选，如要用到多个因子表达式合并，开发人员需将|替换成~ 2017/09/07
							commfactor = commfactor.replaceAll("\\|", "~");
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						boolean ishavehistory=false;
						if("1".equals(flag)) {
                            ishavehistory=true;
                        } else if("1".equals(history)) {
                            ishavehistory=true;
                        }
								strQuery =cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,userView,infokind,bresult);
						//cat.debug("---->sql======" + strQuery);
								//System.out.println(strQuery);
						if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
						{
							strQuery=strQuery+" and "+this.getWhereIN();
						}	
						strQuery = strQuery.toUpperCase();
						
						String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
						/**
						 * 【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列明白“A0100”不明确”
						 *  新增lsql，根据设置的统计条件获取数据
						 *  jingq add 2015.04.16
						 */
						FactorList factorlist= new FactorList(strLexpr,strFactor,userbase,ishavehistory,true,bresult,Integer.parseInt(infokind),username);
				        String lsql = factorlist.getSqlExpression();
				        lsql = lsql.substring(lsql.indexOf("WHERE")+5);
				        
						for(int n=0;n<usedsets.size();n++){
							String set = (String)usedsets.get(n);
							if("1".equals(infokind)){
								set = (" "+userbase+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(basesql.indexOf(set)==-1){
									sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
									sb.append(basesql.substring(basesql.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100 AND "+lsql+") OR "+set+".I9999 IS NULL)");
									basesql= sb.toString();
								}
							}else if("2".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}else if("3".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}
						}
						
						// WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
						if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
							StringBuffer sb = new StringBuffer();
							if("1".equals(infokind)){
								sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userbase+"A01.A0100="+yp.getTempTableName()+".A0100");
								sb.append(basesql.substring(basesql.indexOf(" WHERE")));
								basesql= sb.toString();
							} else  if("2".equals(infokind)) {
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								strQuery= sb.toString();
							} else if("3".equals(infokind)) {
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/	
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1");	
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								strQuery= sb.toString();
							}
						}
						
						//System.out.println(strQuery);
						ArrayList dbList = userView.getPrivDbList();
						String dblist = ","+StringUtils.join(dbList.toArray(new String[dbList.size()]),",")+",";
						if("1".equals(infokind)){
							StringBuffer sb = new StringBuffer();//【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列名称“A0100”不明确”  jingq upd 2015.04.15
							String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select "+userbase.toUpperCase()+"A01.a0100 "+strQuery+")").toUpperCase();
							if(userbases.indexOf("`")==-1){
								if(dblist.toLowerCase().indexOf(","+userbases.toLowerCase()+",") == -1)//人员库权限没满足时
                                {
                                    return fieldValues;
                                }
								userbases = userbases.trim().length()==0 || "".equals(userbases.trim())? userbase:userbases;//人员库条件没有设置处理 wangb 20180822
								sb.append(tmpsql.replaceAll(userbase,userbases));
							}else{
								String[] tmpdbpres=userbases.split("`");
								for(int n=tmpdbpres.length-1;n>=0;n--){
									String tmpdbpre=tmpdbpres[n];
									if(dblist.toLowerCase().indexOf(","+tmpdbpre.toLowerCase()+",") == -1 /*!dbList.contains(tmpdbpre)*/) //bug 38847 人员库不存在，不查询人员  wangb 20180714
                                    {
                                        continue;
                                    }
									if(tmpdbpre.length()==3){
										if(sb.length()>0){
											sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
										}else{
											sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
										}
									}
								}
							}
							strQuery = "select "+type+"(lexprData) as lexprData from (" + sb.toString()+") tt";
				         	//strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
						}else if("2".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        } else if("3".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        }
						//System.out.println(strQuery);
						if(Sql_switcher.searchDbServer() == 1 && "avg".equalsIgnoreCase(type) && Integer.parseInt(decimalwidth)>0){//sql server 数据库  平均值返回整数 处理   wangb  20190514
							if("1".equals(infokind)){
								strQuery = strQuery.replace(type+"(lexprData)", "convert(decimal(15,"+decimalwidth+"),"+type+"(lexprData+0.0))");
							}else{
								strQuery = strQuery.replace(type+"("+field+")", "convert(decimal(15,"+decimalwidth+"),"+type+"("+field+"+0.0))");
							}
						}
						List rsset = ExecuteSQL.executeMyQuery(strQuery);
						if (rsset != null && rsset.size()>0) {
							//保存该图例的统计数
							LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
							String tmp=rdata.get("lexprdata").toString();
							if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                tmp="0";
                            }
							fieldValues[i] = Double.parseDouble(tmp);
						}
					}
				return fieldValues;
//			}
				
				
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	/**
	 * 
	 * 根据id从sname中获取统计项的infokind 
	 * queryId 统计项ID
	 * 
	 **/
	private String currInfoKind(int queryId){
		String infoKind ="";
		StringBuffer sql = new StringBuffer();
		sql.append("select infokind from SName where id=");
		sql.append(queryId);
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			infoKind = rec.get("infokind")!=null?rec.get("infokind").toString():"";
		}
		return infoKind;
	}
	
	
	
	/**
     * 返回YksjParser infoGroup参数
     *
    */ 
	private int infoKindToInfoGroup(String infokind) {
        int infoGroup=0;
        if("1".equals(infokind)) {
            infoGroup = YksjParser.forPerson;
        } else if ("2".equals(infokind)) {
            infoGroup = YksjParser.forUnit;
        } else if ("3".equals(infokind)) {
            infoGroup = YksjParser.forPosition;
        }
        return infoGroup;
	}
	
    /**   
     * @Title: checkFactorItemIsInSnapFlds   
     * @Description: 判断查询因子使用的指标 是否在快照指标中，如果没有 则返回错误描述。   
     * @param @param sfactor 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String checkFactorItemIsInSnapFlds(String queryId){
        String strMessage="";
        String snap_fields="";
        Connection conn=null;
        try{    
            StringBuffer sql= new StringBuffer();
            sql.append("select * from hr_hisdata_SLegend where id=");
            sql.append(queryId);  
            sql.append(" order by norder");          
            List dataset = ExecuteSQL.executeMyQuery(sql.toString());
            
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            RowSet rs = dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
            if (rs.next()) {
                ConstantXml xml = new ConstantXml(conn, "HISPOINT_PARAMETER", "Emp_HisPoint");
                snap_fields = xml.getTextValue("/Emp_HisPoint/Struct");
            } else {
                // 设置的快照指标
                rs = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
                if (rs.next()) {
                    snap_fields = rs.getString("str_value");
                }

            }
            snap_fields = snap_fields + ",B0110,E0122,E01A1,A0101,";
            snap_fields = "," + snap_fields.toUpperCase() + ",";
            // 唯一性指标
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");// 身份证
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");// 唯一性指标
            String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "valid");
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
            if (chkvalid == null) {
                chkvalid = "0";
            }
            if (uniquenessvalid == null) {
                uniquenessvalid = "";
            }
            String chkcheck = "checked", uniquenesscheck = "checked";

            if ("0".equalsIgnoreCase(chkvalid) || "".equalsIgnoreCase(chkvalid)) {
                chkcheck = "";
            }
            if ("0".equalsIgnoreCase(uniquenessvalid) || "".equalsIgnoreCase(uniquenessvalid)) {
                uniquenesscheck = "";
            }
            if (chk == null) {
                chk = "";
            }
            if (onlyname == null) {
                onlyname = "";
            }
            String uniqueitem = "";
            if (chk.length() > 0 && "checked".equals(chkcheck)) {
                uniqueitem = chk.toLowerCase();
            } else if (onlyname.length() > 0 && "checked".equals(uniquenesscheck)) {
                uniqueitem = onlyname.toLowerCase();
            } else {
                uniqueitem = "a0100";
            }

            String flds = "";
            for (int i = 0; i < dataset.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) dataset.get(i);
                String strFactor = rec.get("factor") != null ? rec.get("factor").toString().trim() + "`" : "";
                String[] factorfields = strFactor.toUpperCase().split("`");
                for (int n = 0; n < factorfields.length; n++) {
                    String fld = factorfields[n];
                    if (fld.length() > 5) {
                        fld = fld.substring(0, 5).toUpperCase();
                        if (fld.equalsIgnoreCase(uniqueitem)) {
                            continue;
                        }
                        if (snap_fields.indexOf("," + fld + ",") < 0) {
                            FieldItem fielditem = DataDictionary.getFieldItem(fld);
                            if (fielditem != null) {
                                fld = fielditem.getItemdesc();
                                if ("".equals(flds)) {
                                    flds = "[" + fld + "]";
                                } else {
                                    if (flds.indexOf("[" + fld + "]")<0) {
                                        flds = flds + "," + "[" + fld + "]";
                                    }
                                }
                            }
                        }
                    }

                }
            }

            if (!"".equals(flds)) {
                strMessage = "指标" + flds + "已删除，请重新设置统计条件！";
            }
        }catch(Exception e){
            e.printStackTrace();
        }       
        
        finally{
            try{
                 if (conn != null){
                    conn.close();
                }
            }catch (SQLException sql){
            }

        }
        return strMessage;
    } 
            
            
	public float[] getLexprData(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,String backdate) throws Exception {
			float[] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String sbase="";
				StringBuffer selectsql = new StringBuffer();
				//计算语句 guodd 2015-04-15
				String ssql = "";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				sql.append("select * from hr_hisdata_sname where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					String stype = rec.get("stype").toString();
					String sformula = rec.get("sformula")!=null?rec.get("sformula").toString():"";
					if("0".equals(stype)){
						selectsql.append("select count(distinct a0100) as lexprData ");
					}else if("1".equals(stype)){
						ssql = getYksjParserSql(sformula,userView);
						selectsql.append("select sum(lexprData) as lexprData ");
					}else if("2".equals(stype)){
						ssql = getYksjParserSql(sformula,userView);
						selectsql.append("select min(lexprData) as lexprData ");
					}else if("3".equals(stype)){
						ssql = getYksjParserSql(sformula,userView);
						selectsql.append("select max(lexprData) as lexprData ");
					}else if("4".equals(stype)){
						ssql = getYksjParserSql(sformula,userView);
						selectsql.append("select avg(lexprData) as lexprData ");
					}
					sbase=rec.get("sbase")!=null?rec.get("sbase").toString():"";
					if(sbase.endsWith(",")) {
                        sbase=sbase.substring(0,sbase.length()-1);
                    }
					sbase=filterPrivDB(sbase,userView);
					if(sbase.length()==0) {
                        sbase="#";
                    }
				}
				if(ssql.length()>0){//将计算值 放到 lexprData 列中 guodd 2015-04-15
					ssql = ","+ssql+" as lexprData ";
				}
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from hr_hisdata_SLegend where id=");
				sql.append(queryId);   
				sql.append(" order by norder");  
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new float[dataset.size()];

				display = new String[dataset.size()];
				for (int i = 0; i < dataset.size(); i++) {
					LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
					strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
					strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
					//System.out.println(strFactor);
					display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
					
					//根据表达式和因子生成统计的sql语句
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
							strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							false,
							username,
							sqlSelect,userView,infokind,true);
					//cat.debug("---->sql======" + strQuery);
							//System.out.println(strQuery);
					String[] fields = strFactor.toUpperCase().split("`");
					int size=fields.length;
					ArrayList fieldsetlist = new ArrayList();
					for(int n=0;n<size;n++){
						String tmp = fields[n];
						if(tmp.length()>5){
							String itemid = tmp.substring(0,5);
							FieldItem fielditem = DataDictionary.getFieldItem(itemid);
							if(fielditem!=null){
								String fieldsetid = fielditem.getFieldsetid();
								FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
								if(!fieldset.isMainset()){
									fieldsetlist.add(fieldsetid.toUpperCase());
								}	
							}
						}
					}
					strQuery=strQuery.toUpperCase().substring(strQuery.indexOf("WHERE")+6);
					/*int indexI9999 = strQuery.indexOf("I9999");
					if(indexI9999>13)
						strQuery = strQuery.substring(0,indexI9999-5)+")))";*/
						//strQuery = strQuery.substring(0,indexI9999-5)+"))";
					strQuery=delI9999(strQuery);
					size = fieldsetlist.size();
					for	(int n=0;n<size;n++){
						String fieldsetid = (String)fieldsetlist.get(n);
						strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase()+"\\.", "heh"+n+".");
						//子查询添加快照时间点，否则查询的是所有的 guodd 2015-03-05
						while(strQuery.indexOf("SELECT A0100 FROM "+(userbase+fieldsetid).toUpperCase()+" WHERE")!=-1){
							strQuery = strQuery.replace("SELECT A0100 FROM "+(userbase+fieldsetid).toUpperCase()+" WHERE", "SELECT A0100 FROM hr_emp_hisdata heh"+n+" WHERE "+" heh"+n+".id="+userbase.toUpperCase()+"A01.id and ");
						}
						strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n);
					}
					strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase()+"\\.", "heh.");
					strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
					strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase(), "hr_emp_hisdata");
					//strQuery=strQuery.replaceAll("FROM", "FROM hr_emp_hisdata ");
					//System.out.println(strQuery);
					String sqlstr = "";
					if(Sql_switcher.searchDbServer()== Constant.ORACEL){
						sqlstr = "nbase||a0100";
					} else {
						sqlstr = "nbase+a0100";
					}
					if("1".equals(infokind)){//【7870】员工管理/查询浏览/历史时点，统计分析，统计人员库为在职和劳务时，统计结果和统计图中数量不一样  jingq upd 2015.03.11
						// 加上 ssql，将计算列放入查询表中  guodd 2015-04-15
			         	strQuery = selectsql.toString()+" from (select "+sqlstr+" a0100 "+ssql+" from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery +" and nbase in('"+sbase.replace(",","','")+"')) a";
					}else if("2".equals(infokind)) {
                        strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery +" and nbase in('"+sbase.replace(",","','")+"')";// + getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery +" and nbase in('"+sbase.replace(",","','")+"')";// + getUserMangerWheresql(userView,infokind);
                    }
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						String tmp = (String)rdata.get("lexprdata");
						fieldValues[i] = Float.parseFloat(tmp==null||tmp.length()==0?"0":tmp);
					}
				}
				return fieldValues;
		}
	
	public String[] getDoubleData(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,String backdate) throws Exception {
			String[][] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				StringBuffer selectsql = new StringBuffer();
				//获得统计项
				StringBuffer sql =new StringBuffer();
				sql.append("select name,hv,sbase from hr_hisdata_sname where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				String sbase="";
				String hs = "";
				String vs ="";
				String[] ths=null;
				String[] tvs=null;
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
					String hv = rec.get("hv").toString();
					String[] tmp=hv.split("\\|");
					hs = tmp[0];
					vs = tmp[1];
					ths = hs.split(",");
					tvs = vs.split(",");
					sbase=rec.get("sbase")!=null?rec.get("sbase").toString():"";
					if(sbase.endsWith(",")) {
                        sbase=sbase.substring(0,sbase.length()-1);
                    }
					sbase=this.filterPrivDB(sbase, userView);
					if(sbase.length()==0) {
                        sbase="#";
                    }
				}
				
				HashMap hsnameMap = new HashMap();
				HashMap vsnameMap = new HashMap();
				HashMap hslegendMap = new HashMap();//<key,Arraylist< map<key(legend,lexpr,factor),value> > >
				HashMap vslegendMap = new HashMap();
				ArrayList hslegendlist = new ArrayList();
				ArrayList vslegendlist = new ArrayList();
				
				sql.setLength(0);
				sql.append("select t2.id id,t2.name name,t2.stype stype,t2.sformula sformula from hr_hisdata_slegend t1 left join hr_hisdata_sname t2 on t1.id=t2.id where t2.id in(");
				sql.append(hs+") order by t2.snorder");
				rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					for(int i=0;i<rs.size();i++){
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						hsnameMap.put(rec.get("id").toString(),rec.get("name")!=null?rec.get("name").toString():"");
						if(i==0){
							String stype = rec.get("stype").toString();
							String sformula = rec.get("sformula")!=null?rec.get("sformula").toString():"";
							if("0".equals(stype)){
								selectsql.append("select count(distinct a0100) as lexprData ");
							}else if("1".equals(stype)){
								String ssql = getYksjParserSql(sformula,userView);
								selectsql.append("select sum("+ssql+") as lexprData ");
							}else if("2".equals(stype)){
								String ssql = getYksjParserSql(sformula,userView);
								selectsql.append("select min("+ssql+") as lexprData ");
							}else if("3".equals(stype)){
								String ssql = getYksjParserSql(sformula,userView);
								selectsql.append("select max("+ssql+") as lexprData ");
							}else if("4".equals(stype)){
								String ssql = getYksjParserSql(sformula,userView);
								selectsql.append("select avg("+ssql+") as lexprData ");
							}
						}
					}
				}
				sql.setLength(0);
				sql.append("select t2.id id,t2.name name,t2.stype stype,t2.sformula sformula from hr_hisdata_slegend t1 left join hr_hisdata_sname t2 on t1.id=t2.id where t2.id in(");
				sql.append(vs+") order by t2.snorder");
				rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					for(int i=0;i<rs.size();i++){
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						vsnameMap.put(rec.get("id").toString(),rec.get("name")!=null?rec.get("name").toString():"");
					}
				}
				
				sql.setLength(0);
				sql.append("select t1.id id,t1.legend legend,t1.lexpr lexpr,t1.factor factor from hr_hisdata_slegend t1 left join hr_hisdata_sname t2 on t1.id=t2.id where t2.id in(");
				sql.append(hs+") order by t2.snorder,t1.norder");
				rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					for(int i=0;i<rs.size();i++){
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						String id=rec.get("id").toString();
						ArrayList legendlist = null;
						if(hslegendMap.containsKey(id)){
							legendlist=(ArrayList)hslegendMap.get(id);
						}else{
							legendlist = new ArrayList();
							hslegendMap.put(id, legendlist);
						}
						
						HashMap map = new HashMap();
						map.put("legend", rec.get("legend").toString());
						map.put("lexpr", rec.get("lexpr").toString());
						map.put("factor", rec.get("factor").toString()+"`");
						legendlist.add(map);
						hslegendlist.add(map);
					}
				}
				sql.setLength(0);
				sql.append("select t1.id id,t1.legend legend,t1.lexpr lexpr,t1.factor factor from hr_hisdata_slegend t1 left join hr_hisdata_sname t2 on t1.id=t2.id where t2.id in(");
				sql.append(vs+") order by t2.snorder,t1.norder");
				rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					for(int i=0;i<rs.size();i++){
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						String id=rec.get("id").toString();
						ArrayList legendlist = null;
						if(vslegendMap.containsKey(id)){
							legendlist=(ArrayList)vslegendMap.get(id);
						}else{
							legendlist = new ArrayList();
							vslegendMap.put(id, legendlist);
						}
						
						HashMap map = new HashMap();
						map.put("legend", rec.get("legend").toString());
						map.put("lexpr", rec.get("lexpr").toString());
						map.put("factor", rec.get("factor").toString()+"`");
						legendlist.add(map);
						vslegendlist.add(map);
					}
				}
				
				//获得各个统计项的图例
				
				fieldValues = new String[vslegendlist.size()][hslegendlist.size()];
					for(int i=0;i<vslegendlist.size();i++){
						HashMap legendmap = (HashMap)vslegendlist.get(i);
						String vlexpr = (String)legendmap.get("lexpr");
						String vfactor = (String)legendmap.get("factor");
							for(int m=0;m<hslegendlist.size();m++){
								HashMap hlegendmap = (HashMap)hslegendlist.get(m);
								String hlexpr = (String)hlegendmap.get("lexpr");
								String hfactor = (String)hlegendmap.get("factor");
								
								ArrayList lexprFactor=new ArrayList();
								lexprFactor.add(hlexpr + "|" + hfactor);
								lexprFactor.add(vlexpr + "|" + vfactor);
								CombineFactor combinefactor=new CombineFactor();
								String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
								cat.debug("------>lexprFactorStr====="+lexprFactorStr);
								StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
								if(Stok.hasMoreTokens())
								{
									strLexpr=Stok.nextToken();
									strFactor=Stok.nextToken();
								}
								
								strQuery =cond.getCondQueryString(
										strLexpr,
										strFactor,
										userBase,
										false,
										username,
										sqlSelect,userView,infokind,true);
								//cat.debug("---->sql======" + strQuery);
										//System.out.println(strQuery);
								String[] fields = strFactor.toUpperCase().split("`");
								int size=fields.length;
								ArrayList fieldsetlist = new ArrayList();
								for(int n=0;n<size;n++){
									String tmp = fields[n];
									if(tmp.length()>5){
										String itemid = tmp.substring(0,5);
										FieldItem fielditem = DataDictionary.getFieldItem(itemid);
										if(fielditem!=null){
											String fieldsetid = fielditem.getFieldsetid();
											FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
											if(!fieldset.isMainset()){
												fieldsetlist.add(fieldsetid.toUpperCase());
											}	
										}
									}
								}
								
								strQuery=strQuery.toUpperCase().substring(strQuery.indexOf("WHERE")+6);
								/*int indexI9999 = strQuery.indexOf("I9999");
								if(indexI9999>13)
									strQuery = strQuery.substring(0,indexI9999-5) + "))))";*/
									//strQuery = strQuery.substring(0,indexI9999-13);
								strQuery=delI9999(strQuery);
								strQuery = strQuery.replaceAll("WHERE", "").replaceAll("where", "");
								size = fieldsetlist.size();
								for	(int n=0;n<size;n++){
									String fieldsetid = (String)fieldsetlist.get(n);
									strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase()+"\\.", "heh"+n+".");
									strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n+",hr_hisdata_list hh_"+n+" WHERE heh"+n+".id=hh_"+n+".id and "+Sql_switcher.dateToChar("hh_"+n+".create_date", "yyyy-MM-dd")+"='2019-12-06' and ");
									
								}
								strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase()+"\\.", "heh.");
								strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
								strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase(), "hr_emp_hisdata");
								//System.out.println(strQuery);
								if("1".equals(infokind)) {
                                    strQuery = selectsql.toString()+" from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery;
                                } else if("2".equals(infokind)) {
                                    strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                                } else if("3".equals(infokind)) {
                                    strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                                }
								//System.out.println(strQuery);
								List rsset = ExecuteSQL.executeMyQuery(strQuery+" and nbase in('"+sbase.replace(",","','")+"')");
								if (rsset != null && rsset.size()>0) {
									//保存该图例的统计数
									LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
									fieldValues[i][m]=rdata.get("lexprdata").toString();
								}
							}
					}

					
					//HashMap hsnameMap = new HashMap();
					//HashMap vsnameMap = new HashMap();
					//HashMap hslegendMap = new HashMap();//<key,Arraylist< map<key(legend,lexpr,factor),value> > >
					//HashMap vslegendMap = new HashMap();
					//ArrayList hslegendlist = new ArrayList();
					//ArrayList vslegendlist = new ArrayList();	
					//String[] ths=null;
					//String[] tvs=null;
				int allcolumns=fieldValues[0].length;
				StringBuffer html = new StringBuffer();
				html.append("<tr>");
				html.append("<td rowspan=\"2\" colspan=\"2\" width=\"20%\" class=\"TableRow\" nowrap></td>");
				for(int i=0;i<ths.length;i++){
					String name = (String)hsnameMap.get(ths[i]);
					ArrayList legendlist = (ArrayList)hslegendMap.get(ths[i]);
					int gg=legendlist.size();
					html.append("<td colspan=\""+gg+"\" width=\""+(80*gg/allcolumns)+"%\" align=\"center\" class=\"TableRow\" nowrap>"+name+"</td>");
				}
				html.append("</tr><tr>");
				for(int i=0;i<ths.length;i++){
					ArrayList legendlist = (ArrayList)hslegendMap.get(ths[i]);
					for(int n=0;n<legendlist.size();n++){
						HashMap map = (HashMap)legendlist.get(n);
						html.append("<td align=\"center\" class=\"TableRow\" nowrap>"+(String)map.get("legend")+"</td>");
					}
				}
				html.append("</tr>");
				
				int index=0;;
				for(int i=0;i<tvs.length;i++){
					String name = (String)vsnameMap.get(tvs[i]);
					ArrayList legendlist = (ArrayList)vslegendMap.get(tvs[i]);
					int gg=legendlist.size();
					html.append("<tr>");
					html.append("<td rowspan=\""+gg+"\" style=\"writing-mode:tb-rl\" align=\"center\" class=\"TableRow\" nowrap>"+name+"</td>");
					
					for(int n=0;n<gg;n++){
						HashMap map = (HashMap)legendlist.get(n);
						if(n!=0) {
                            html.append("<tr>");
                        }
						html.append("<td class=TableRow align=center nowrap>"+(String)map.get("legend")+"</td>");
						HashMap legendmap = (HashMap)vslegendlist.get(index);
						String vlexpr = (String)legendmap.get("lexpr");
						String vfactor = (String)legendmap.get("factor");
						for(int m=0;m<fieldValues[index].length;m++){
							HashMap hlegendmap = (HashMap)hslegendlist.get(m);
							String hlexpr = (String)hlegendmap.get("lexpr");
							String hfactor = (String)hlegendmap.get("factor");
							ArrayList lexprFactor=new ArrayList();
							lexprFactor.add(hlexpr + "|" + hfactor);
							lexprFactor.add(vlexpr + "|" + vfactor);
							CombineFactor combinefactor=new CombineFactor();
							String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
							StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
							if(Stok.hasMoreTokens())
							{
								strLexpr=Stok.nextToken();
								strLexpr=SafeCode.encode(strLexpr);
								strFactor=Stok.nextToken();
								strFactor = SafeCode.encode(strFactor);
							}
							html.append("<td class=RecordRow align=center nowrap=nowrap><a href='/general/static/commonstatic/history/statshow.do?b_double=data&strlexpr="+strLexpr+"&strfactor="+strFactor+"&nbase="+sbase+"&type=2' >"+(fieldValues[index][m]==null?"0":fieldValues[index][m])+"</a></td>");
						}
						html.append("</tr>");
						index++;
					}
				}
				String filename = this.creatExcel(SNameDisplay, fieldValues, ths, tvs, hsnameMap, hslegendMap, vsnameMap, vslegendMap, hslegendlist, vslegendlist, userView);
				return new String[]{html.toString(),filename};
		}
	
	public String creatExcel(String statname,String [][] fieldValues,String[] ths,String[] tvs,HashMap hsnameMap,HashMap hslegendMap,HashMap vsnameMap,HashMap vslegendMap,ArrayList hslegendlist,ArrayList vslegendlist,UserView userView)
	{
		String excel_filename=userView.getUserName()+"ry_tj_123456.xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = null;
		FileOutputStream fileOut = null;
		try {
			sheet = workbook.createSheet(statname);
			HSSFRow row=null;
			HSSFCell csCell=null;
			short nn=0;
			
			int allcolumns=fieldValues[0].length;
			
			for(int i=0;i<allcolumns+2;i++){
				sheet.setColumnWidth(i,5000);
			}
			// 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,(short)(allcolumns+1));
			row = sheet.getRow(0);
			if(row==null) {
                row = sheet.createRow(0);
            }
			csCell = row.getCell(0);
			if(csCell==null) {
                csCell = row.createCell(0);
            }
			csCell.setCellValue(statname);
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			csCell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, 1,(short)0,2,(short)(1));
			
			HSSFCellStyle cellStyle1= workbook.createCellStyle();
			cellStyle1.setAlignment(HorizontalAlignment.CENTER);
			
			HSSFCellStyle cellStyle2= workbook.createCellStyle();
			cellStyle2.setFont(font);
			cellStyle2.setAlignment(HorizontalAlignment.CENTER);
			cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
			
			nn++;
			row = sheet.getRow(nn);
			if(row==null) {
                row = sheet.createRow(nn);
            }
			StringBuffer html = new StringBuffer();
			int sx=1,sy=2,ex=1,ey=2;
			for(int i=0;i<ths.length;i++){
				String name = (String)hsnameMap.get(ths[i]);
				ArrayList legendlist = (ArrayList)hslegendMap.get(ths[i]);
				int gg=legendlist.size();
				ey=ey+gg-1;
				ExportExcelUtil.mergeCell(sheet, sx,(short)sy,ex,(short)(ey));
				csCell = row.getCell(sy);
				if(csCell==null) {
                    csCell = row.createCell(sy);
                }
				csCell.setCellValue(name);
				csCell.setCellStyle(cellStyle);
				ey++;
				sy=ey;
			}
			nn++;
			row = sheet.getRow(nn);
			if(row==null) {
                row = sheet.createRow(nn);
            }
			int index=0;
			for(int i=0;i<ths.length;i++){
				ArrayList legendlist = (ArrayList)hslegendMap.get(ths[i]);
				for(int n=0;n<legendlist.size();n++){
					HashMap map = (HashMap)legendlist.get(n);
					csCell = row.getCell(index+2);
					if(csCell==null) {
                        csCell = row.createCell(index+2);
                    }
					csCell.setCellValue((String)map.get("legend"));
					csCell.setCellStyle(cellStyle);
					index++;
				}
			}
			
			index=0;
			sx=3;ex=3;
			nn++;
			for(int i=0;i<tvs.length;i++){
				String name = (String)vsnameMap.get(tvs[i]);
				ArrayList legendlist = (ArrayList)vslegendMap.get(tvs[i]);
				int gg=legendlist.size();
				for(int n=0;n<gg;n++){
					row = sheet.getRow(nn+index);
					if(row==null) {
                        row = sheet.createRow(nn+index);
                    }
					
					HashMap map = (HashMap)legendlist.get(n);
					if(n==0){
						ex+=gg-1;
						ExportExcelUtil.mergeCell(sheet, sx,(short)0,ex,(short)0);
						csCell = row.getCell(0);
						if(csCell==null) {
                            csCell = row.createCell(0);
                        }
						csCell.setCellValue(name);
						csCell.setCellStyle(cellStyle2);
						ex++;
						sx=ex;
					}
					csCell = row.getCell(1);
					if(csCell==null) {
                        csCell = row.createCell(1);
                    }
					csCell.setCellValue((String)map.get("legend"));
					csCell.setCellStyle(cellStyle);
					HashMap legendmap = (HashMap)vslegendlist.get(index);
					for(int m=0;m<fieldValues[index].length;m++){
						HashMap hlegendmap = (HashMap)hslegendlist.get(m);
						csCell = row.getCell(m+2);
						if(csCell==null) {
                            csCell = row.createCell(m+2);
                        }
						csCell.setCellValue(fieldValues[index][m]==null?"0":fieldValues[index][m]);
						csCell.setCellStyle(cellStyle1);
					}
					index++;
				}
			}
			
			
			
			/*HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null)
				row = sheet.createRow(n);

			csCell=row.createCell(Short.parseShort(String.valueOf(dlist.size()/2)));
			csCell.setCellStyle(cellStyle);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(snameplay+" ("+tolvalue+")");
			n++;
			n++;
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null)
				row = sheet.createRow(n);
			csCell =row.createCell((short)0);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(cellStyle);
			LazyDynaBean bean = new LazyDynaBean();
			for(int i=0;i<dlist.size();i++){
				bean = (LazyDynaBean)dlist.get(i);
				csCell = row.createCell((short)(i+1));
				csCell.setCellStyle(cellStyle);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(bean.get("legend").toString());
			}
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			
			for(int i=0;i<getValues.length;i++){
				for(int j=0;j<getValues[i].length;j++){
					//row=sheet.createRow(n+j+1);
					row = sheet.getRow(n+j+1);
					if(row==null)
						row = sheet.createRow(n+j+1);

					csCell =row.createCell((short)(0));
					csCell.setCellStyle(cellStyle);
					bean = (LazyDynaBean)hlist.get(j);
					//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					csCell.setCellValue(bean.get("legend").toString());
					csCell =row.createCell((short)(i+1));
					csCell.setCellStyle(cellStyle);
					int number =getValues[i][j];
					//csCell.setEncoding((short) HSSFCell.CELL_TYPE_NUMERIC);					
					csCell.setCellValue(number);
					
				}
			}*/
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
			 
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		
		return excel_filename;
	}
	
	public int[] getLexprData(
			ArrayList dblist,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String year,String where,String years) throws Exception {
			int[] fieldValues;
			//try {
				
				
				
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new int[dataset.size()];
				display = new String[dataset.size()];
				for (int i = 0; i < dataset.size(); i++) {
					LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
					strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
					strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
					flag=rec.get("flag")!=null?rec.get("flag").toString():"";
					//System.out.println(strFactor);
					display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
					
					//根据表达式和因子生成统计的sql语句
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
					    if(style!=null && style.length==2)
					    {
					    	strLexpr=style[0];
					    	strFactor=style[1];
					    }
					}
					
					String sqlWhere = getWhere(strFactor, year);
					if((sqlWhere == null || sqlWhere.length() < 1) && year != null && year.length() > 0) {
                        sqlWhere = getArchive_set(queryId, conn, year);
                    }
					
					ArrayList<String> yearList = new ArrayList<String>();
					if(years != null && years.length() > 0) {
                        yearList = getYearList(strFactor, year, years);
                    }
					
                    if(yearList != null && yearList.size() > 0) {
                        where = "";
                    }
                    
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					String userBase = "";
					for(int r=0;r<dblist.size();r++)
					{
						userBase=(String)dblist.get(r);
						if(userBase==null||userBase.length()<=0) {
                            continue;
                        }
						boolean ishavehistory=false;
						if("1".equals(flag)) {
                            ishavehistory=true;
                        } else if("1".equals(history)) {
                            ishavehistory=true;
                        }
								strQuery =cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,userView,infokind,bresult);
						//cat.debug("---->sql======" + strQuery);
								//System.out.println(strQuery);
						if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
						{
							strQuery=strQuery+" and "+this.getWhereIN();
						}	
						//System.out.println(strQuery);
						if("1".equals(infokind)) {
                            strQuery = "select count(distinct " + userBase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        } else if("2".equals(infokind)) {
                            strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery  + sqlWhere + where;// + getUserMangerWheresql(userView,infokind);
                        } else if("3".equals(infokind)) {
                            strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                        }
                        // System.out.println(strQuery);
                        if (yearList == null || yearList.size() < 1) {
                            List rsset = ExecuteSQL.executeMyQuery(strQuery);
                            if (rsset != null && rsset.size() > 0) {
                                // 保存该图例的统计数
                                LazyDynaBean rdata = (LazyDynaBean) rsset.get(0);
                                fieldValues[i] = fieldValues[i] + Integer.parseInt(rdata.get("lexprdata").toString());
                            }
        
                        } else {
                            fieldValues = new int[yearList.size()];
                            for (int m = 0; m < yearList.size(); m++) {
                                List rsset = ExecuteSQL.executeMyQuery(strQuery + yearList.get(m));
                                if (rsset != null && rsset.size() > 0) {
                                    // 保存该图例的统计数
                                    LazyDynaBean rdata = (LazyDynaBean) rsset.get(0);
                                    fieldValues[m] = fieldValues[m] + Integer.parseInt(rdata.get("lexprdata").toString());
                                }
                            }
                            break;
                        }
					}
					
				}
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	public double[] getLexprDataSformula(
			ArrayList dblist,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String year,String whereSql) throws Exception {
			double[] fieldValues;
			//try {
				
				
				
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				String userBase = "";
				if(dblist.size()>0){
					userBase=(String)dblist.get(0);
					userBase=userBase.toUpperCase();
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
							boolean ishavehistory=false;
							if("1".equals(flag)) {
                                ishavehistory=true;
                            } else if("1".equals(history)) {
                                ishavehistory=true;
                            }
									strQuery =cond.getCondQueryString(
									strLexpr,
									strFactor,
									userBase,
									ishavehistory,
									username,
									sqlSelect,userView,infokind,bresult);
							//cat.debug("---->sql======" + strQuery);
									//System.out.println(strQuery);
							if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
							{
								strQuery=strQuery+" and "+this.getWhereIN();
							}	
							
							strQuery = strQuery.toUpperCase();
							String type=element.getAttributeValue("type");
							String expr=element.getText();
							ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
									Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
							YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userBase);
							yp.setCon(conn);
							yp.setTempTableName("temp_"+username + queryId);
							yp.run(expr);
							String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
							String field = yp.getSQL();
							ArrayList usedsets = yp.getUsedSets();
							for(int n=0;n<usedsets.size();n++){
								String set = (String)usedsets.get(n);
								if("1".equals(infokind)){
									set = (" "+userBase+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									/*if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}*/
									if(basesql.indexOf(set)==-1){
										sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(basesql.substring(basesql.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										basesql= sb.toString();
									}
								}else if("2".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}else if("3".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}
							}
	                        // WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
	                        if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
	                            StringBuffer sb = new StringBuffer();
	                            if("1".equals(infokind)){
	                                sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userBase+"A01.A0100="+yp.getTempTableName()+".A0100");
	                                sb.append(basesql.substring(basesql.indexOf(" WHERE")));
	                                basesql= sb.toString();
	                            } else  if("2".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                strQuery= sb.toString();
	                            } else if("3".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/ 
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1"); 
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                strQuery= sb.toString();
	                            }
	                        }							
							//System.out.println(strQuery);
							if("1".equals(infokind)){
								
								StringBuffer sb = new StringBuffer();
								//String tmpsql = ("select "+field+" as lexprData" + strQuery).toUpperCase();
								String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userBase+"A01.a0100 in(select a0100 "+strQuery+")").toUpperCase();
													
									for(int n=dblist.size()-1;n>=0;n--){
										String tmpdbpre=(String)dblist.get(n);
										if(tmpdbpre.length()==3){
											if(sb.length()>0){
												sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
											}else{
												sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
											}
										}
									}
								strQuery = "select "+type+"(lexprData) as lexprData from (" + sb.toString()+") tt";
					         	//strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
							}else if("2".equals(infokind)) {
                                strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                            } else if("3".equals(infokind)) {
                                strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                            }
							//System.out.println(strQuery);
							List rsset = ExecuteSQL.executeMyQuery(strQuery);
							if (rsset != null && rsset.size()>0) {
								//保存该图例的统计数
								LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
								String tmp=rdata.get("lexprdata").toString();
								if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp)) {
                                    tmp="0";
                                }
								fieldValues[i] = fieldValues[i]+Double.parseDouble(tmp);
							}
						}
				}
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	
	
	public double[] getLexprDataSformula(
			ArrayList dblist,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String year,String whereSql,String years) throws Exception {
			double[] fieldValues;
			//try {
				
				
				
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				String userBase = "";
				if(dblist.size()>0){
					userBase=(String)dblist.get(0);
					userBase=userBase.toUpperCase();
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
							boolean ishavehistory=false;
							if("1".equals(flag)) {
                                ishavehistory=true;
                            } else if("1".equals(history)) {
                                ishavehistory=true;
                            }
									strQuery =cond.getCondQueryString(
									strLexpr,
									strFactor,
									userBase,
									ishavehistory,
									username,
									sqlSelect,userView,infokind,bresult);
							//cat.debug("---->sql======" + strQuery);
									//System.out.println(strQuery);
							if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
							{
								strQuery=strQuery+" and "+this.getWhereIN();
							}	
							
							strQuery = strQuery.toUpperCase();
							String type=element.getAttributeValue("type");
							String expr=element.getText();
							ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
									Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
							YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userBase);
							yp.setCon(conn);
							yp.setTempTableName("temp_"+username + queryId);
							yp.run(expr);
							String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
							String field = yp.getSQL();
							ArrayList usedsets = yp.getUsedSets();
							String where = getWhere(strFactor, year);
							if((where == null || where.length() < 1) && year != null && year.length() > 0) {
                                where = getArchive_set(queryId, conn, year);
                            }
							
							ArrayList<String> yearList = new ArrayList<String>();
		                    if(years != null && years.length() > 0) {
                                yearList = getYearList(strFactor, year, years);
                            }
		                    
							if(yearList != null && yearList.size() > 0) {
                                where = "";
                            }
							
							for(int n=0;n<usedsets.size();n++){
								String set = (String)usedsets.get(n);
								if("1".equals(infokind)){
									set = (" "+userBase+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									/*if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}*/
									if(basesql.indexOf(set)==-1){
										sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(basesql.substring(basesql.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										basesql= sb.toString();
									}
								}else if("2".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}else if("3".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}
							}
	                        // WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
	                        if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
	                            StringBuffer sb = new StringBuffer();
	                            if("1".equals(infokind)){
	                                sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userBase+"A01.A0100="+yp.getTempTableName()+".A0100");
	                                sb.append(basesql.substring(basesql.indexOf(" WHERE")));
	                                basesql= sb.toString();
	                            } else  if("2".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                if(where != null) {
                                        sb.append(where);
                                    }
	                                
	                                strQuery= sb.toString();
	                            } else if("3".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/ 
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1"); 
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                strQuery= sb.toString();
	                            }
	                        }							
							//System.out.println(strQuery);
							if("1".equals(infokind)){
								
								StringBuffer sb = new StringBuffer();
								//String tmpsql = ("select "+field+" as lexprData" + strQuery).toUpperCase();
								String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userBase+"A01.a0100 in(select "+userBase+"A01.a0100 "+strQuery+")").toUpperCase();
													
									for(int n=dblist.size()-1;n>=0;n--){
										String tmpdbpre=(String)dblist.get(n);
										if(tmpdbpre.length()==3){
											if(sb.length()>0){
												sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
											}else{
												sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
											}
										}
									}
								strQuery = "select "+type+"(lexprData*1.0) as lexprData from (" + sb.toString()+") tt";
					         	//strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
							}else if("2".equals(infokind)) {
                                strQuery = "select "+type+"("+field+") as lexprData " + strQuery  + whereSql;//+ where + getUserMangerWheresql(userView,infokind);
                            } else if("3".equals(infokind)) {
                                strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                            }
							//System.out.println(strQuery);
							if(yearList == null || yearList.size() < 1){
    							List rsset = ExecuteSQL.executeMyQuery(strQuery);
    							if (rsset != null && rsset.size()>0) {
    								//保存该图例的统计数
    								LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
    								String tmp=rdata.get("lexprdata").toString();
    								if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp)) {
                                        tmp="0";
                                    }
    								fieldValues[i] = fieldValues[i]+Double.parseDouble(tmp);
    							}
							} else {
							    fieldValues = new double[yearList.size()];
							    for(int m = 0; m < yearList.size(); m++){
							        List rsset = ExecuteSQL.executeMyQuery(strQuery + yearList.get(m));
	                                if (rsset != null && rsset.size()>0) {
	                                    //保存该图例的统计数
	                                    LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
	                                    String tmp=rdata.get("lexprdata").toString();
	                                    if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp)) {
                                            tmp="0";
                                        }
	                                    fieldValues[m] = fieldValues[m]+Double.parseDouble(tmp);
	                                }
							    }
							    break;
							}
						}
				}
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	
	
	public double[] getLexprDataSformula(
			ArrayList dblist,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String year,String whereSql,String years,String showpage,String parentid,String statId,
			String lexprName,String treeuncode,String fromwhere,String result) throws Exception {
			double[] fieldValues;
			//try {
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				String userBase = "";
				if(dblist.size()>0){
					userBase=(String)dblist.get(0);
					userBase=userBase.toUpperCase();
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
							boolean ishavehistory=false;
							if("1".equals(flag)) {
                                ishavehistory=true;
                            } else if("1".equals(history)) {
                                ishavehistory=true;
                            }
									strQuery =cond.getCondQueryString(
									strLexpr,
									strFactor,
									userBase,
									ishavehistory,
									username,
									sqlSelect,userView,infokind,bresult);
							//cat.debug("---->sql======" + strQuery);
									//System.out.println(strQuery);
							if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
							{
								strQuery=strQuery+" and "+this.getWhereIN();
							}	
							
							strQuery = strQuery.toUpperCase();
							String type=element.getAttributeValue("type");
							String expr=element.getText();
							ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
									Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
							YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userBase);
							yp.setCon(conn);
							yp.setTempTableName("temp_"+username + queryId);
							yp.run(expr);
							String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
							String field = yp.getSQL();
							ArrayList usedsets = yp.getUsedSets();
							String where = getWhere(strFactor, year);
							if((where == null || where.length() < 1) && year != null && year.length() > 0) {
                                where = getArchive_set(queryId, conn, year);
                            }
							
							ArrayList<String> yearList = new ArrayList<String>();
		                    if(years != null && years.length() > 0) {
                                yearList = getYearList(strFactor, year, years);
                            }
		                    
							if(yearList != null && yearList.size() > 0) {
                                where = "";
                            }
							
							for(int n=0;n<usedsets.size();n++){
								String set = (String)usedsets.get(n);
								if("1".equals(infokind)){
									set = (" "+userBase+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									/*if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}*/
									if(basesql.indexOf(set)==-1){
										sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
										sb.append(basesql.substring(basesql.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
										basesql= sb.toString();
									}
								}else if("2".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}else if("3".equals(infokind)){
									set = (" "+set).toUpperCase();
									StringBuffer sb = new StringBuffer();
									if(strQuery.indexOf(set)==-1){
										sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
										sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
										sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
										sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
										strQuery= sb.toString();
									}
								}
							}
	                        // WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
	                        if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
	                            StringBuffer sb = new StringBuffer();
	                            if("1".equals(infokind)){
	                                sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userBase+"A01.A0100="+yp.getTempTableName()+".A0100");
	                                sb.append(basesql.substring(basesql.indexOf(" WHERE")));
	                                basesql= sb.toString();
	                            } else  if("2".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                if(where != null) {
                                        sb.append(where);
                                    }
	                                
	                                strQuery= sb.toString();
	                            } else if("3".equals(infokind)) {
	                                sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
	                                //sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/ 
	                                sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1"); 
	                                sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
	                                strQuery= sb.toString();
	                            }
	                        }							
							//System.out.println(strQuery);
							if("1".equals(infokind)){
								
								StringBuffer sb = new StringBuffer();
								//String tmpsql = ("select "+field+" as lexprData" + strQuery).toUpperCase();
								String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userBase+"A01.a0100 in(select "+userBase+"A01.a0100 "+strQuery+")").toUpperCase();
													
									for(int n=dblist.size()-1;n>=0;n--){
										String tmpdbpre=(String)dblist.get(n);
										if(tmpdbpre.length()==3){
											if(sb.length()>0){
												sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
											}else{
												sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
											}
										}
									}
								strQuery = "select "+type+"(lexprData*1.0) as lexprData from (" + sb.toString()+") tt";
					         	//strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
							}else if("2".equals(infokind)){
								if ("2".equalsIgnoreCase(infokind)) {
					                if ("pcw".equalsIgnoreCase(showpage) || "rate".equalsIgnoreCase(showpage)) {
					                    if (!statId.equalsIgnoreCase(parentid)) {
					                    	whereSql = getLexpr(parentid, lexprName, infokind,sqlSelect,result,userView);
					                        if (whereSql != null && where.length() > 1) {
                                                whereSql = where.replaceAll("usrA01", "B01");
                                            } else if(yearList != null && yearList.size() > 0 && StringUtils.isNotEmpty(whereSql)) {
                                                whereSql = whereSql.replace("usrA01", "B01");
                                            }
					                        	
					                    }
					                } else if ("tree".equals(showpage)) {
					                    if (treeuncode != null) {
					                        if (treeuncode.contains("*")) {
					                            treeuncode = treeuncode.replace("UN", "");
					                            whereSql = " and B01.B0110 like '" + treeuncode + "%'";
					                        } else {
					                            treeuncode = treeuncode.replace("UN", "");
					                            whereSql = " and B01.B0110 = '" + treeuncode + "'";
					                        }

					                    } else {
                                            whereSql = " and B01.B0110 = '" + "01'";
                                        }
					                }else if(fromwhere!=null&&fromwhere.length() > 0){
					                    if (treeuncode != null) {
					                        if (treeuncode.contains("*")) {
					                            treeuncode = treeuncode.replace("UN", "");
					                            whereSql = " and B01.B0110 like '" + treeuncode + "%'";
					                        } else {
					                            treeuncode = treeuncode.replace("UN", "");
					                            whereSql = " and B01.B0110 = '" + treeuncode + "'";
					                        }

					                    } else {
                                            whereSql = " and B01.B0110 = '" + "01'";
                                        }
					                }
					            }
					    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery  + whereSql;//+ where + getUserMangerWheresql(userView,infokind);
							}else if("3".equals(infokind)) {
                                strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                            }
							//System.out.println(strQuery);
							if(yearList == null || yearList.size() < 1){
    							List rsset = ExecuteSQL.executeMyQuery(strQuery + where);
    							if (rsset != null && rsset.size()>0) {
    								//保存该图例的统计数
    								LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
    								String tmp=rdata.get("lexprdata").toString();
    								if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp)) {
                                        tmp="0";
                                    }
    								fieldValues[i] = fieldValues[i]+Double.parseDouble(tmp);
    							}
							} else {
							    fieldValues = new double[yearList.size()];
							    for(int m = 0; m < yearList.size(); m++){
							        List rsset = ExecuteSQL.executeMyQuery(strQuery + yearList.get(m));
	                                if (rsset != null && rsset.size()>0) {
	                                    //保存该图例的统计数
	                                    LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
	                                    String tmp=rdata.get("lexprdata").toString();
	                                    if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp)) {
                                            tmp="0";
                                        }
	                                    fieldValues[m] = fieldValues[m]+Double.parseDouble(tmp);
	                                }
							    }
							    break;
							}
						}
				}
				return fieldValues;
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
    /**
     * 获取联动时点击图例的统计条件 注：此方法中的人员库固定为usr，只用于传递参数，目前只有单位的统计图会有联动
     * 
     * @param statid
     *            点击图例的统计id
     * @param lexprName
     *            统计条件的名称
     * @param infokind
     *            是单位还是人员的统计
     * @return
     */
    private String getLexpr(String statid, String lexprName, String infokind,String querycond,String result,UserView userView) {
        if (statid == null || statid.length() < 1) {
            return "";
        }

        String wheresql = "";
        try {
            if (lexprName != null && lexprName.length() > 0) {
                lexprName = lexprName.replaceAll("\n", "");
                lexprName = lexprName.replaceAll("\r", "");
            }
            LazyDynaBean bean = getStatDataForName(statid, lexprName);
            String history = (String) bean.get("history");
            String strlexpr = (String) bean.get("lexpr");
            String strfactor = (String) bean.get("factor");
            strfactor = strfactor + "`";
            strfactor = PubFunc.keyWord_reback(strfactor);
            StatCondAnalyse cond = new StatCondAnalyse();
            boolean ishavehistory = false;
            if (history != null && "1".equals(history)) {
                ishavehistory = true;
            }
            boolean isresult = true;
            if (result == null || "".equals(result)) {
                StringBuffer sql = new StringBuffer();
                sql.append("select flag from SName where id=");
                sql.append(statid);
                List rs = ExecuteSQL.executeMyQuery(sql.toString());
                if (!rs.isEmpty()) {
                    LazyDynaBean rec = (LazyDynaBean) rs.get(0);
                    String flag = rec.get("flag") != null ? rec.get("flag").toString() : "";
                    if (flag != null && "1".equals(flag)) {
                        isresult = false; // false时才查询，查询结果表
                    }
                }
            } else if ("1".equals(result)) {
                isresult = false;
            }

            wheresql = cond.getCondQueryString(strlexpr, strfactor, "usr", ishavehistory, userView.getUserName(), querycond, userView, infokind, isresult, false);
            if (wheresql.indexOf("WHERE") != -1) {
                wheresql = wheresql.substring(wheresql.indexOf("WHERE") + 5);
            }

            if (wheresql.trim().length() > 0) {
                wheresql = " and " + wheresql;
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return wheresql;

    }
    
    /**
     * 获取查询条件
     * 
     * @param statid
     *            统计图id
     * @param name
     *            统计图中某个查询条件的名字
     * @return
     */
    private LazyDynaBean getStatDataForName(String statid, String name) {
        String sql = "select * from slegend where id=" + statid;
        if (name != null && name.length() > 0) {
            sql = sql + " and legend='" + name + "'";
        }

        sql += " order by norder";
        ContentDAO dao =null;
        Connection conn =null;
		try {
			conn =AdminDb.getConnection();
			dao = new ContentDAO(conn);
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        LazyDynaBean bean = new LazyDynaBean();
        RowSet rs = null;
        try {
        	rs = dao.search(sql);
            if (rs.next()) {
                bean.set("lexpr", rs.getString("lexpr") != null ? rs.getString("lexpr") : "");
                bean.set("factor", rs.getString("factor") != null ? rs.getString("factor") : "");
                bean.set("norder", rs.getString("norder") != null ? rs.getString("norder") : "");
                bean.set("history", rs.getString("flag") != null ? rs.getString("flag") : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(conn);
        }
        return bean;
    }
	
	
	/**
	 * 按所选年份拼接查询条件
	 * @param strFactor 公式
	 * @param year 年份
	 * @return
	 */
	private String getWhere(String strFactor,String year) {
        if(strFactor == null || strFactor.length() < 1 || year == null || year.length() < 4) {
            return "";
        }
        
        String[] strFactors = strFactor.split("`");
        StringBuffer where = new StringBuffer();
        
        for(int i = 0; i < strFactors.length; i++){
            String lexpr = strFactors[i];
            if(lexpr == null || lexpr.length() < 1) {
                continue;
            }
            
            String field = lexpr.split("=")[0];
            if(field.indexOf("<>") != -1) {
                field = field.split("<>")[0];
            }
            
            if(field.length() > 5 || !field.toUpperCase().startsWith("B") || field.toUpperCase().startsWith("B01")) {
                continue;
            }
            
            where.append(" and " + Sql_switcher.year(field.substring(0, 3) + "." + field.substring(0, 3) +"z0") + "=" + year );
            
        }
        
        if(where == null) {
            where.append(" ");
        }
            
        return where.toString();
    }
    //获得权限的sql语句的条件
	private String getUserMangerWheresql(UserView userview,String infokind)
	{
		String tablename="";
		if("1".equals(infokind)) {
            tablename="";
        } else if("2".equalsIgnoreCase(infokind)) {
            tablename="B01";
        } else if("3".equalsIgnoreCase(infokind)) {
            tablename="K01";
        }
		StringBuffer strsql=new StringBuffer();
		if(!userview.isSuper_admin() && userview.getManagePrivCodeValue()!=null && !"".equals(userview.getManagePrivCodeValue()) &&userview.getManagePrivCodeValue().length()>0)
		{
			strsql.append(" and (");
			if("2".equals(infokind) || "3".equals(infokind))
			{
				if("UN".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append(tablename);
					strsql.append(".b0110 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or ");
					strsql.append(tablename);
					strsql.append(".b0110 is null or ");
					strsql.append(tablename);
					strsql.append(".b0110=''");
				}else if("UM".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append(tablename);
					strsql.append(".e0122 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or ");
					strsql.append(tablename);
					strsql.append(".e0122 is null or ");
					strsql.append(tablename);
					strsql.append(".e0122=''");
				}else if("@K".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append(tablename);				
					strsql.append(".e01a1 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or ");
					strsql.append(tablename);
					strsql.append(".e01a1 is null or ");
					strsql.append(tablename);
					strsql.append(".e01a1=''");
				}
				else
				{
					strsql.append(tablename);
					strsql.append(".b0110 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or ");
					strsql.append(tablename);
					strsql.append(".b0110 is null or ");
					strsql.append(tablename);
					strsql.append(".b0110=''");
				}
			}else
			{
				if("UN".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append("b0110 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or b0110 is null or b0110=''");
				}else if("UM".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append("e0122 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or e0122 is null or e0122=''");
				}else if("@K".equalsIgnoreCase(userview.getManagePrivCode()))
				{
					strsql.append("e01a1 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or e01a1 is null or e01a1=''");
				}
				else
				{
					strsql.append("b0110 like '");
					strsql.append(userview.getManagePrivCodeValue());
					strsql.append("%'");
					strsql.append(" or b0110 is null or b0110=''");
				}
			}
			strsql.append(")");
		}
		return strsql.toString();
	}
	//封装对应某个图例所统计的纪录数据
	public List getEncapsulationData(
		String userbase,
		int queryId,
		int nOrderId,
		int curPage,
		int pageSize,
		String sqlSelect,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult) {
		String userBase = userbase;
		String strFactor = "";
		String strLexpr = "";
		StringBuffer strQuery =new StringBuffer();
		List rsset = null;
		//ExecuteSQL excSql=new ExecuteSQL();
		try {
			String fieldsetstr = getTableFieldSet(userBase);
			StringBuffer sql =new StringBuffer();
			sql.append("select * from SLegend where id=");
			sql.append(queryId);
			sql.append(" AND nOrder=");
			sql.append(nOrderId);
			List rs = ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				for (int i = 0; i < rs.size(); i++) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(i);
					strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
					strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim()+ "`":"";
					boolean ishavehistory=false;
					String strQuerytemp =
						cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,
								userView,infokind,bresult);
					strQuery.append("select top ");
					strQuery.append(pageSize);
					strQuery.append(" ");
					strQuery.append(userBase);
					strQuery.append("A01.A0000 as A0000,");
					strQuery.append(userBase);
					strQuery.append("A01.A0100 as A0100,");
					strQuery.append(userBase);
					strQuery.append("A01.B0110 as B0110,");
					strQuery.append(userBase);
					strQuery.append("A01.E0122 as E0122,");
					strQuery.append(userBase);
					strQuery.append("A01.A0101 as A0101,");
					strQuery.append(userBase);
					strQuery.append("A01.E01A1 as E01A1 ");
					strQuery.append(strQuerytemp);
					//strQuery.append(getUserMangerWheresql(userView,infokind));
					strQuery.append(" AND ");
					strQuery.append(userBase);
					strQuery.append("A01.A0100 not in(select top ");
					strQuery.append((curPage - 1) * pageSize);
					strQuery.append(" ");
					strQuery.append(userBase);
					strQuery.append("A01.A0100 as A0100 ");
					strQuery.append(strQuerytemp);
					//strQuery.append(getUserMangerWheresql(userView,infokind));
					strQuery.append(" Order by ");
					strQuery.append(userBase);
					strQuery.append("A01.A0100) Order by ");
					strQuery.append(userBase);
					strQuery.append("A01.A0100");
					rsset = ExecuteSQL.executeMyQuery(strQuery.toString());
					strQuery.delete(0,strQuery.length());
					strQuery.append("select count(*) as recordCount ");
					strQuery.append(strQuerytemp);
					
					List rss = ExecuteSQL.executeMyQuery(strQuery.toString());
					if (rss != null && rss.size()>0) {
						LazyDynaBean rssc=(LazyDynaBean)rss.get(0);
						recordCount = Integer.parseInt(rssc.get("recordcount").toString());
					}
					//rss.close();
				}
			}
			return rsset;
		} catch (Exception e) {
			System.out.println("封装一维详细数据出错!");
		}
		finally{
			//excSql.freeConn();	
		}
		return null;
	}
	//根据子集获得所对应的各个属性列
	public String getTableFieldSet(String usertype) {
		List rs = null;
		StringBuffer fieldset =new StringBuffer();
		StringBuffer strsql =new StringBuffer();
		strsql.append("SELECT itemid FROM fielditem WHERE (fieldsetid = '");
		strsql.append(usertype);
		strsql.append("') AND (useflag = '1') ORDER BY displayid");
		try {
			rs = ExecuteSQL.executeMyQuery(strsql.toString());
			if (!rs.isEmpty()) {
				if ("A01".equals(usertype)) {
					for (int i = 0; i < rs.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						if (!"E0122".equals(rec.get("itemid"))){
							fieldset.append(",");
						    fieldset.append(usertype);
						    fieldset.append("A01.");
						    fieldset.append(rec.get("itemid"));
						 }
					}
				} else {
					for (int i = 0; i < rs.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)rs.get(i);
						fieldset.append(",");
						fieldset.append(rec.get("itemid"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return fieldset.toString();
	}
	//获得二维的统计数据数保存到二维数组dataValues
	public int[][] getDoubleLexprData(
		int queryId,
		String userbaseT,
		String sqlSelect,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		int dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		try {
			
       			
			StringBuffer sql =new StringBuffer();
			sql.append("select * from SName where id=");
			sql.append(queryId);
			List rs = ExecuteSQL.executeMyQuery(sql.toString());
			sql.delete(0,sql.length());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
				String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
				if (doubleLexr != null) {
                    HVLexr = doubleLexr.split(",");
                }
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
					String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
					List rsV = ExecuteSQL.executeMyQuery(sqlV);
					List rsH = ExecuteSQL.executeMyQuery(sqlH);
					dataValues = new int[rsV.size()][rsH.size()];
					if (!rsV.isEmpty() && !rsH.isEmpty()) {
						for (int j = 0; j < rsH.size(); j++) {
							rec=(LazyDynaBean)rsH.get(j);
							horizonArray.add(rec);
						}
						for (int i = 0; i < rsV.size(); i++) {
							rec=(LazyDynaBean)rsV.get(i);
							verticalArray.add(rec);
						}
						totalValue = 0;
						for (int i = 0; i < rsV.size(); i++) {
							LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
							strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
							strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
							boolean ishavehistory=false;
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								ArrayList lexprFactor=new ArrayList();
								lexprFactor.add(strLexprh + "|" + strFactorh);
								lexprFactor.add(strLexprv + "|" + strFactorv);
								CombineFactor combinefactor=new CombineFactor();
								String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
								cat.debug("------>lexprFactorStr====="+lexprFactorStr);
								StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
								if(Stok.hasMoreTokens())
								{
									strLexpr=Stok.nextToken();
									strFactor=Stok.nextToken();
								}
								
								if(commlexpr!=null && commfactor!=null)
								//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
								{
									String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
								    if(style!=null && style.length==2)
								    {
								    	strLexpr=style[0];
								    	strFactor=style[1];
								    }
								}
								 cat.debug("------>strLexprv====="+strLexprv);
								 cat.debug("------>strFactorv====="+strFactorv);
									strHV=cond.getCondQueryString(
										strLexpr,
										strFactor,
										userBase,
										ishavehistory,
										username,
										sqlSelect,
										userView,infokind,bresult);
								cat.debug("------>sql====="+strHV);
								if(this.whereIN!=null&&this.whereIN.length()>0) {
                                    strHV=strHV+" and "+this.getWhereIN();
                                }
								strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
								List rscount = ExecuteSQL.executeMyQuery(strHV);
								if (rscount != null && rscount.size()>0) {
									LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
									dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
									totalValue += dataValues[i][j];
								} else {
									dataValues[i][j] = 0;
								}
								cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
							}

						}
						return dataValues;
					}
				}

			}

		} catch (Exception e) {
			System.out.println("生成二维的数据出错!");
			System.out.println(e);
		}finally{
			//exeSql.freeConn();
		}
		return null;
	}
	
	public int[][] getDoubleLexprData(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String vtotal,String htotal) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new int[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new int[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new int[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new int[rsV.size()][rsH.size()];
						}
						
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							/*for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}*/
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							totalValue = 0;
							int lenght = 1;
							if("1,2,3".equals(infokind)){
								lenght = 3;
							}
							for(int m=1;m<=lenght;m++){
								if(!"1,2,3".equals(infokind)){
									infokind = String.valueOf(m);
								}
								for (int i = 0; i < rsV.size(); i++) {
									LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
									strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
									boolean ishavehistory=false;
									for (int j = 0; j < rsH.size(); j++) {
										rec=(LazyDynaBean)rsH.get(j);
										strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
										strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										cat.debug("------>lexprFactorStr====="+lexprFactorStr);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}
										
										if(commlexpr!=null && commfactor!=null)
										//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
										{
											String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
										    if(style!=null && style.length==2)
										    {
										    	strLexpr=style[0];
										    	strFactor=style[1];
										    }
										}
										 cat.debug("------>strLexprv====="+strLexprv);
										 cat.debug("------>strFactorv====="+strFactorv);
											strHV=cond.getCondQueryString(
												strLexpr,
												strFactor,
												userBase,
												ishavehistory,
												username,
												sqlSelect,
												userView,infokind,bresult);
										cat.debug("------>sql====="+strHV);
										if(this.whereIN!=null&&this.whereIN.length()>0) {
                                            strHV=strHV+" and "+this.getWhereIN();
                                        }
										strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
										List rscount = ExecuteSQL.executeMyQuery(strHV);
										if (rscount != null && rscount.size()>0) {
											LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
											dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
											//totalValue += dataValues[i][j];
											if("1".equals(vtotal)&&"1".equals(htotal)){
												if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else if("1".equals(vtotal)){
												if(i<rsV.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else if("1".equals(htotal)){
												if(j<rsH.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else{
												totalValue += dataValues[i][j];
											}
										} else {
											dataValues[i][j] = 0;
										}
										cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
									}
	
								}
							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	public double[][] getDoubleLexprDataSformula(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			double dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
					Element element = xml.getElement(sformula);
					SNameDisplay=element.getAttributeValue("title");
					String type=element.getAttributeValue("type");
					String expr=element.getText();
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userBase);
					yp.setCon(conn);
					yp.setTempTableName("temp_"+username + queryId);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						dataValues = new double[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									
									if(commlexpr!=null && commfactor!=null)
									//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
									{
										String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
									    if(style!=null && style.length==2)
									    {
									    	strLexpr=style[0];
									    	strFactor=style[1];
									    }
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
										strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0) {
                                        strHV=strHV+" and "+this.getWhereIN();
                                    }
									String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
									strHV = strHV.toUpperCase();
									for(int n=0;n<usedsets.size();n++){
										String set = (String)usedsets.get(n);
										if("1".equals(infokind)){
											set = (" "+userBase+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											/*if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}*/
											if(basesql.indexOf(set)==-1){
												sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
												sb.append(basesql.substring(basesql.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
												basesql= sb.toString();
											}
											
										}else if("2".equals(infokind)){
											set = (" "+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}
										}else if("3".equals(infokind)){
											set = (" "+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}
										}
									}
									if("1".equals(infokind)){
										strHV =	"select "+type+"("+field+") as recordCount " + basesql+" and "+userBase+"A01.a0100 in(select a0100 "+strHV+")";
									}else {
                                        strHV =	"select "+type+"("+field+") as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
                                    }
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                            tmp="0";
                                        }
										dataValues[i][j] = Double.parseDouble(tmp);
										totalValues += dataValues[i][j];
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}

							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	public double[][] getDoubleLexprDataSformula(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String vtotal,String htotal) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			double dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
					Element element = xml.getElement(sformula);
					SNameDisplay=element.getAttributeValue("title");
					String type=element.getAttributeValue("type");
					String expr=element.getText();
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userBase);
					yp.setCon(conn);
					yp.setTempTableName("temp_"+username + queryId);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new double[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new double[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new double[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new double[rsV.size()][rsH.size()];
						}
						//dataValues = new double[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							/*for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}*/
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							totalValues = 0;
							int lenght = 1;
							if("1,2,3".equals(infokind)){
								lenght = 3;
							}
							for(int m=1;m<=lenght;m++){
								if(!"1,2,3".equals(infokind)){
									infokind = String.valueOf(m);
								}
								for (int i = 0; i < rsV.size(); i++) {
									LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
									strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
									boolean ishavehistory=false;
									for (int j = 0; j < rsH.size(); j++) {
										rec=(LazyDynaBean)rsH.get(j);
										strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
										strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										cat.debug("------>lexprFactorStr====="+lexprFactorStr);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}
										
										if(commlexpr!=null && commfactor!=null)
										//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
										{
											String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
										    if(style!=null && style.length==2)
										    {
										    	strLexpr=style[0];
										    	strFactor=style[1];
										    }
										}
										 cat.debug("------>strLexprv====="+strLexprv);
										 cat.debug("------>strFactorv====="+strFactorv);
											strHV=cond.getCondQueryString(
												strLexpr,
												strFactor,
												userBase,
												ishavehistory,
												username,
												sqlSelect,
												userView,infokind,bresult);
										cat.debug("------>sql====="+strHV);
										if(this.whereIN!=null&&this.whereIN.length()>0) {
                                            strHV=strHV+" and "+this.getWhereIN();
                                        }
										String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
										strHV = strHV.toUpperCase();
										for(int n=0;n<usedsets.size();n++){
											String set = (String)usedsets.get(n);
											if("1".equals(infokind)){
												set = (" "+userBase+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												/*if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}*/
												if(basesql.indexOf(set)==-1){
													sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
													sb.append(basesql.substring(basesql.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
													basesql= sb.toString();
												}
												
											}else if("2".equals(infokind)){
												set = (" "+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}
											}else if("3".equals(infokind)){
												set = (" "+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}
											}
										}
										if("1".equals(infokind)){
											strHV =	"select "+type+"("+field+") as recordCount " + basesql+" and "+userBase+"A01.a0100 in(select a0100 "+strHV+")";
										}else {
                                            strHV =	"select "+type+"("+field+") as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
                                        }
										List rscount = ExecuteSQL.executeMyQuery(strHV);
										if (rscount != null && rscount.size()>0) {
											LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
											String tmp=rscountc.get("recordcount").toString();
											if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                                tmp="0";
                                            }
											dataValues[i][j] = Double.parseDouble(tmp);
											//totalValues += dataValues[i][j];
											if("1".equals(vtotal)&&"1".equals(htotal)){
												if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else if("1".equals(vtotal)){
												if(i<rsV.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else if("1".equals(htotal)){
												if(j<rsH.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else{
												totalValues += dataValues[i][j];
											}
										} else {
											dataValues[i][j] = 0;
										}
										cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
									}
	
								}
							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	public int[][] getDoubleLexprData(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						dataValues = new int[rsV.size()+1][rsH.size()+1];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorh.add(strLexprh + "|" + strFactorh);
							}
							CombineFactor combinefactorh=new CombineFactor();
							String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
							StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
							if(Stokh.hasMoreTokens())
							{
								strLexpr=Stokh.nextToken();
								strFactor=Stokh.nextToken();
							}
							rec = new LazyDynaBean();
							rec.set("factor", strFactor);
							rec.set("lexpr", strLexpr);
							rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
							rec.set("id", HVLexr[1]);
							rsH.add(rec);
							horizonArray.add(rec);
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								
								strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorv.add(strLexprv + "|" + strFactorv);
							}
							CombineFactor combinefactorv=new CombineFactor();
							String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
							StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
							if(Stokv.hasMoreTokens())
							{
								strLexpr=Stokv.nextToken();
								strFactor=Stokv.nextToken();
							}
							rec = new LazyDynaBean();
							rec.set("factor", strFactor);
							rec.set("lexpr", strLexpr);
							rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
							rec.set("id", HVLexr[0]);
							rsV.add(rec);
							verticalArray.add(rec);
							
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  +(rec.get("factor").toString().trim().endsWith("`")?"": "`"):"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									//System.out.println(lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									
									if(commlexpr!=null && commfactor!=null)
									//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
									{
										String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
									    if(style!=null && style.length==2)
									    {
									    	strLexpr=style[0];
									    	strFactor=style[1];
									    }
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
										strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0) {
                                        strHV=strHV+" and "+this.getWhereIN();
                                    }
									StringBuffer sb = new StringBuffer();
									String tmpsql ="";
									if("1".equals(infokind)) {
                                        tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                                    } else if("2".equals(infokind)) {
                                        tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                                    } else if("3".equals(infokind)) {
                                        tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                                    }
									
									if(userbases.indexOf("`")==-1){
										sb.append(tmpsql.replaceAll(userBase, userbases));
									}else{
										String[] tmpdbpres=userbases.split("`");
										for(int n=tmpdbpres.length-1;n>=0;n--){
											String tmpdbpre=tmpdbpres[n];
											if(tmpdbpre.length()==3){
												if(sb.length()>0){
													sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
												}else{
													sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
												}
											}
										}
									}
									strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                            tmp="0";
                                        }
										dataValues[i][j] = Integer.parseInt(tmp);
										if(j<dataValues.length-1&&i<dataValues[i].length-1) {
                                            totalValue += dataValues[i][j];
                                        }
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}

							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	public int[][] getDoubleLexprData(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String vtotal,String htotal) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new int[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new int[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new int[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new int[rsV.size()][rsH.size()];
						}
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							
							totalValue = 0;
							int lenght = 1;
							if("1,2,3".equals(infokind)){
								lenght = 3;
							}
							for(int m=1;m<=lenght;m++){
								if(!"1,2,3".equals(infokind)){
									infokind = String.valueOf(m);
								}
								for (int i = 0; i < rsV.size(); i++) {
									LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
									strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
									boolean ishavehistory=false;
									for (int j = 0; j < rsH.size(); j++) {
										rec=(LazyDynaBean)rsH.get(j);
										strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  +(rec.get("factor").toString().trim().endsWith("`")?"": "`"):"";
										strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										cat.debug("------>lexprFactorStr====="+lexprFactorStr);
										//System.out.println(lexprFactorStr);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}
										
										if(commlexpr!=null && commfactor!=null)
										//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
										{
											String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
										    if(style!=null && style.length==2)
										    {
										    	strLexpr=style[0];
										    	strFactor=style[1];
										    }
										}
										 cat.debug("------>strLexprv====="+strLexprv);
										 cat.debug("------>strFactorv====="+strFactorv);
											strHV=cond.getCondQueryString(
												strLexpr,
												strFactor,
												userBase,
												ishavehistory,
												username,
												sqlSelect,
												userView,infokind,bresult);
										cat.debug("------>sql====="+strHV);
										if(this.whereIN!=null&&this.whereIN.length()>0) {
                                            strHV=strHV+" and "+this.getWhereIN();
                                        }
										StringBuffer sb = new StringBuffer();
										String tmpsql ="";
										if("1".equals(infokind)) {
                                            tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                                        } else if("2".equals(infokind)) {
                                            tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                                        } else if("3".equals(infokind)) {
                                            tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                                        }
										
										if(userbases.indexOf("`")==-1){
											sb.append(tmpsql.replaceAll(userBase, userbases));
										}else{
											String[] tmpdbpres=userbases.split("`");
											for(int n=tmpdbpres.length-1;n>=0;n--){
												String tmpdbpre=tmpdbpres[n];
												if(tmpdbpre.length()==3){
													if(sb.length()>0){
														sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
													}else{
														sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
													}
												}
											}
										}
										strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
										//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
										List rscount = ExecuteSQL.executeMyQuery(strHV);
										if (rscount != null && rscount.size()>0) {
											LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
											String tmp=rscountc.get("recordcount").toString();
											if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                                tmp="0";
                                            }
											dataValues[i][j] = Integer.parseInt(tmp);
											if("1".equals(vtotal)&&"1".equals(htotal)){
												if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else if("1".equals(vtotal)){
												if(i<rsV.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else if("1".equals(htotal)){
												if(j<rsH.size()-1) {
                                                    totalValue += dataValues[i][j];
                                                }
											}else{
												totalValue += dataValues[i][j];
											}
										} else {
											dataValues[i][j] = 0;
										}
										cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
									}
	
								}
							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	public double[][] getDoubleLexprDataSformula(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			double dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
					Element element = xml.getElement(sformula);
					SNameDisplay=element.getAttributeValue("title");
					String type=element.getAttributeValue("type");
					String expr=element.getText();
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userBase);
					yp.setCon(conn);
					yp.setTempTableName("temp_"+username + queryId);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						dataValues = new double[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									
									if(commlexpr!=null && commfactor!=null)
									//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
									{
										String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
									    if(style!=null && style.length==2)
									    {
									    	strLexpr=style[0];
									    	strFactor=style[1];
									    }
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
										strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0) {
                                        strHV=strHV+" and "+this.getWhereIN();
                                    }
									String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
									strHV = strHV.toUpperCase();
									for(int n=0;n<usedsets.size();n++){
										String set = (String)usedsets.get(n);
										if("1".equals(infokind)){
											set = (" "+userBase+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											/*if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}*/
											if(basesql.indexOf(set)==-1){
												sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
												sb.append(basesql.substring(basesql.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
												basesql= sb.toString();
											}
										}else if("2".equals(infokind)){
											set = (" "+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}
										}else if("3".equals(infokind)){
											set = (" "+set).toUpperCase();
											StringBuffer sb = new StringBuffer();
											if(strHV.indexOf(set)==-1){
												sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
												sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
												sb.append(strHV.substring(strHV.indexOf(" WHERE")));
												sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
												strHV= sb.toString();
											}
										}
									}
									
									StringBuffer sb = new StringBuffer();
									String tmpsql="";
									if("1".equals(infokind)){
										tmpsql = ("select "+field+" as recordCount" + basesql+" and "+userBase+"A01.a0100 in(select a0100 "+strHV+")").toUpperCase();
									}else {
                                        tmpsql = ("select "+field+" as recordCount" + strHV).toUpperCase();
                                    }
									if(userbases.indexOf("`")==-1){
										sb.append(tmpsql.replaceAll(userBase, userbases));
									}else{
										String[] tmpdbpres=userbases.split("`");
										for(int n=tmpdbpres.length-1;n>=0;n--){
											String tmpdbpre=tmpdbpres[n];
											if(tmpdbpre.length()==3){
												if(sb.length()>0){
													sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
												}else{
													sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
												}
											}
										}
									}
									strHV =	"select "+type+"(recordCount) as recordCount from (" + sb.toString()+") tt";
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                            tmp="0";
                                        }
										dataValues[i][j] = Double.parseDouble(tmp);
										totalValues += dataValues[i][j];
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}

							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	public double[][] getDoubleLexprDataSformula(
			int queryId,
			String userbaseT,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn,String vtotal,String htotal) {
			String userBase = userbaseT;
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			double dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				
	       			
				StringBuffer sql =new StringBuffer();
				sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs = ExecuteSQL.executeMyQuery(sql.toString());
				sql.delete(0,sql.length());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
					Element element = xml.getElement(sformula);
					SNameDisplay=element.getAttributeValue("title");
					String type=element.getAttributeValue("type");
					String expr=element.getText();
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userBase);
					yp.setCon(conn);
					yp.setTempTableName("temp_"+username + queryId);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null) {
                        HVLexr = doubleLexr.split(",");
                    }
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new double[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new double[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new double[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new double[rsV.size()][rsH.size()];
						}
						
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							/*for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}*/
							
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							
							totalValues = 0;
							int lenght = 1;
							if("1,2,3".equals(infokind)){
								lenght = 3;
							}
							for(int m=1;m<=lenght;m++){
								if(!"1,2,3".equals(infokind)){
									infokind = String.valueOf(m);
								}
								for (int i = 0; i < rsV.size(); i++) {
									LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
									strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
									boolean ishavehistory=false;
									for (int j = 0; j < rsH.size(); j++) {
										rec=(LazyDynaBean)rsH.get(j);
										strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
										strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										cat.debug("------>lexprFactorStr====="+lexprFactorStr);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}
										
										if(commlexpr!=null && commfactor!=null)
										//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
										{
											String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
										    if(style!=null && style.length==2)
										    {
										    	strLexpr=style[0];
										    	strFactor=style[1];
										    }
										}
										 cat.debug("------>strLexprv====="+strLexprv);
										 cat.debug("------>strFactorv====="+strFactorv);
											strHV=cond.getCondQueryString(
												strLexpr,
												strFactor,
												userBase,
												ishavehistory,
												username,
												sqlSelect,
												userView,infokind,bresult);
										cat.debug("------>sql====="+strHV);
										if(this.whereIN!=null&&this.whereIN.length()>0) {
                                            strHV=strHV+" and "+this.getWhereIN();
                                        }
										String basesql=" FROM "+userBase.toUpperCase()+"A01 WHERE 1=1";
										strHV = strHV.toUpperCase();
										for(int n=0;n<usedsets.size();n++){
											String set = (String)usedsets.get(n);
											if("1".equals(infokind)){
												set = (" "+userBase+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												/*if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}*/
												if(basesql.indexOf(set)==-1){
													sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON "+userBase+"A01.A0100="+set+".A0100");
													sb.append(basesql.substring(basesql.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userBase+"A01.A0100) OR "+set+".I9999 IS NULL)");
													basesql= sb.toString();
												}
											}else if("2".equals(infokind)){
												set = (" "+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}
											}else if("3".equals(infokind)){
												set = (" "+set).toUpperCase();
												StringBuffer sb = new StringBuffer();
												if(strHV.indexOf(set)==-1){
													sb.append(strHV.substring(0,strHV.indexOf(" WHERE")));
													sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
													sb.append(strHV.substring(strHV.indexOf(" WHERE")));
													sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
													strHV= sb.toString();
												}
											}
										}
										
										StringBuffer sb = new StringBuffer();
										String tmpsql="";
										if("1".equals(infokind)){
											tmpsql = ("select "+field+" as recordCount" + basesql+" and "+userBase+"A01.a0100 in(select a0100 "+strHV+")").toUpperCase();
										}else {
                                            tmpsql = ("select "+field+" as recordCount" + strHV).toUpperCase();
                                        }
										if(userbases.indexOf("`")==-1){
											sb.append(tmpsql.replaceAll(userBase, userbases));
										}else{
											String[] tmpdbpres=userbases.split("`");
											for(int n=tmpdbpres.length-1;n>=0;n--){
												String tmpdbpre=tmpdbpres[n];
												if(tmpdbpre.length()==3){
													if(sb.length()>0){
														sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
													}else{
														sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
													}
												}
											}
										}
										strHV =	"select "+type+"(recordCount) as recordCount from (" + sb.toString()+") tt";
										//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
										List rscount = ExecuteSQL.executeMyQuery(strHV);
										if (rscount != null && rscount.size()>0) {
											LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
											String tmp=rscountc.get("recordcount").toString();
											if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                                tmp="0";
                                            }
											dataValues[i][j] = Double.parseDouble(tmp);
											if("1".equals(vtotal)&&"1".equals(htotal)){
												if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else if("1".equals(vtotal)){
												if(i<rsV.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else if("1".equals(htotal)){
												if(j<rsH.size()-1) {
                                                    totalValues += dataValues[i][j];
                                                }
											}else{
												totalValues += dataValues[i][j];
											}
										} else {
											dataValues[i][j] = 0;
										}
										cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
									}
	
								}
							}
							return dataValues;
						}
					}

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	//获得二维的统计数据数保存到二维数组dataValues
	public int[][] getDoubleLexprData(
		int queryId,
		ArrayList dblist,
		String sqlSelect,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) {
		String userBase = "";
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		int dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		try {
			
       			
			StringBuffer sql =new StringBuffer();
			sql.append("select * from SName where id=");
			sql.append(queryId);
			List rs = ExecuteSQL.executeMyQuery(sql.toString());
			sql.delete(0,sql.length());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
				String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
				if (doubleLexr != null) {
                    HVLexr = doubleLexr.split(",");
                }
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
					String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
					List rsV = ExecuteSQL.executeMyQuery(sqlV);
					List rsH = ExecuteSQL.executeMyQuery(sqlH);
					dataValues = new int[rsV.size()][rsH.size()];
					if (!rsV.isEmpty() && !rsH.isEmpty()) {
						for (int j = 0; j < rsH.size(); j++) {
							rec=(LazyDynaBean)rsH.get(j);
							horizonArray.add(rec);
						}
						for (int i = 0; i < rsV.size(); i++) {
							rec=(LazyDynaBean)rsV.get(i);
							verticalArray.add(rec);
						}
						totalValue = 0;
						for (int i = 0; i < rsV.size(); i++) {
							LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
							strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
							strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
							boolean ishavehistory=false;
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								ArrayList lexprFactor=new ArrayList();
								lexprFactor.add(strLexprh + "|" + strFactorh);
								lexprFactor.add(strLexprv + "|" + strFactorv);
								CombineFactor combinefactor=new CombineFactor();
								String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
								cat.debug("------>lexprFactorStr====="+lexprFactorStr);
								StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
								if(Stok.hasMoreTokens())
								{
									strLexpr=Stok.nextToken();
									strFactor=Stok.nextToken();
								}
								
								if(commlexpr!=null && commfactor!=null)
								//if("2".equalsIgnoreCase(preresult)&& commlexpr!=null && commfactor!=null)
								{
									String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
								    if(style!=null && style.length==2)
								    {
								    	strLexpr=style[0];
								    	strFactor=style[1];
								    }
								}
								 cat.debug("------>strLexprv====="+strLexprv);
								 cat.debug("------>strFactorv====="+strFactorv);
								 for(int r=0;r<dblist.size();r++)
								 {
									userBase=(String)dblist.get(r);
									if(userBase==null||userBase.length()<=0) {
                                        continue;
                                    }
									strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0) {
                                        strHV=strHV+" and "+this.getWhereIN();
                                    }
									strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = dataValues[i][j]+Integer.parseInt(rscountc.get("recordcount").toString());
										//totalValue += dataValues[i][j];
										totalValue += Integer.parseInt(rscountc.get("recordcount").toString());
									} else {
										//dataValues[i][j] = 0;
									}
								 }
									
								cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
							}

						}
						return dataValues;
					}
				}

			}

		} catch (Exception e) {
			System.out.println("生成二维的数据出错!");
			System.out.println(e);
		}finally{
			//exeSql.freeConn();
		}
		return null;

	}
	public int[][] getDoubleLexprData(
			String dimensionx,
			String dimensiony,
			String title,
			String userBase,
			String sqlSelect,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult) {
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				    StringBuffer sql =new StringBuffer();				
					SNameDisplay = title;
					HVLexr[0]=dimensionx;
					HVLexr[1]=dimensiony;
					LazyDynaBean rec=new LazyDynaBean();
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						dataValues = new int[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
				 					 strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											userView.getUserName(),
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										totalValue += dataValues[i][j];
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}
							}
							return dataValues;
						}
					}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	public int[][] getDoubleLexprData(
			String dimensionx,
			String dimensiony,
			String title,
			String userBase,
			String sqlSelect,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String vtotal,String htotal) {
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				    StringBuffer sql =new StringBuffer();				
					SNameDisplay = title;
					HVLexr[0]=dimensionx;
					HVLexr[1]=dimensiony;
					LazyDynaBean rec=new LazyDynaBean();
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new int[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new int[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new int[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new int[rsV.size()][rsH.size()];
						}
						//dataValues = new int[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							/*for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}*/
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
				 					 strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											userView.getUserName(),
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										//totalValue += dataValues[i][j];
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else{
											totalValue += dataValues[i][j];
										}
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}
							}
							return dataValues;
						}
					}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	public int[][] getDoubleLexprData(
			String dimensionx,
			String dimensiony,
			String title,
			String userBase,
			String sqlSelect,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String userbases) {
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				    StringBuffer sql =new StringBuffer();				
					SNameDisplay = title;
					HVLexr[0]=dimensionx;
					HVLexr[1]=dimensiony;
					LazyDynaBean rec=new LazyDynaBean();
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						dataValues = new int[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + "`":"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
				 					 strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											userView.getUserName(),
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									StringBuffer sb = new StringBuffer();
									String tmpsql ="";
									if("1".equals(infokind)) {
                                        tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                                    } else if("2".equals(infokind)) {
                                        tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                                    } else if("3".equals(infokind)) {
                                        tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                                    }
									
									if(userbases.indexOf("`")==-1){
										sb.append(tmpsql.replaceAll(userBase, userbases));
									}else{
										String[] tmpdbpres=userbases.split("`");
										for(int n=tmpdbpres.length-1;n>=0;n--){
											String tmpdbpre=tmpdbpres[n];
											if(tmpdbpre.length()==3){
												if(sb.length()>0){
													sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
												}else{
													sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
												}
											}
										}
									}
									strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
									
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										totalValue += dataValues[i][j];
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}
							}
							return dataValues;
						}
					}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	public int[][] getDoubleLexprData(
			String dimensionx,
			String dimensiony,
			String title,
			String userBase,
			String sqlSelect,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String userbases,String vtotal,String htotal) {
			String strFactorv = "";
			String strLexprv = "";
			String strFactor = "";
			String strLexpr = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			int dataValues[][];
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
				    StringBuffer sql =new StringBuffer();				
					SNameDisplay = title;
					HVLexr[0]=dimensionx;
					HVLexr[1]=dimensiony;
					LazyDynaBean rec=new LazyDynaBean();
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
						String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
						List rsV = ExecuteSQL.executeMyQuery(sqlV);
						List rsH = ExecuteSQL.executeMyQuery(sqlH);
						if("1".equals(vtotal)&&"1".equals(htotal)){
							dataValues = new int[rsV.size()+1][rsH.size()+1];
						}else if("1".equals(vtotal)){
							dataValues = new int[rsV.size()+1][rsH.size()];
						}else if("1".equals(htotal)){
							dataValues = new int[rsV.size()][rsH.size()+1];
						}else{
							dataValues = new int[rsV.size()][rsH.size()];
						}
						//dataValues = new int[rsV.size()][rsH.size()];
						if (!rsV.isEmpty() && !rsH.isEmpty()) {
							/*for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
							}
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
							}*/
							ArrayList lexprFactorh=new ArrayList();
							for (int j = 0; j < rsH.size(); j++) {
								rec=(LazyDynaBean)rsH.get(j);
								horizonArray.add(rec);
								if("1".equals(htotal)){
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
								}
							}
							if("1".equals(htotal)){
								CombineFactor combinefactorh=new CombineFactor();
								String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
								StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
								if(Stokh.hasMoreTokens())
								{
									strLexpr=Stokh.nextToken();
									strFactor=Stokh.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[1]);
								rsH.add(rec);
								horizonArray.add(rec);
							}
							
							ArrayList lexprFactorv=new ArrayList();
							for (int i = 0; i < rsV.size(); i++) {
								rec=(LazyDynaBean)rsV.get(i);
								verticalArray.add(rec);
								if("1".equals(vtotal)){
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
								}
							}
							if("1".equals(vtotal)){
								CombineFactor combinefactorv=new CombineFactor();
								String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
								StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
								if(Stokv.hasMoreTokens())
								{
									strLexpr=Stokv.nextToken();
									strFactor=Stokv.nextToken();
								}
								rec = new LazyDynaBean();
								rec.set("factor", strFactor);
								rec.set("lexpr", strLexpr);
								rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
								rec.set("id", HVLexr[0]);
								rsV.add(rec);
								verticalArray.add(rec);
							}
							totalValue = 0;
							for (int i = 0; i < rsV.size(); i++) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
								boolean ishavehistory=false;
								for (int j = 0; j < rsH.size(); j++) {
									rec=(LazyDynaBean)rsH.get(j);
									//【1983】员工管理，二维统计，只有横向和纵向统计条件都记录历史时，才会查询历史记录  jingq add 2014.11.27
									/*if("1".equals(recv.get("flag").toString())&&"1".equals(rec.get("flag").toString())){
										ishavehistory = true;
									}*/
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  + (rec.get("factor").toString().trim().endsWith("`")?"":"`"):"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									cat.debug("strFactorh:"+strFactorh+"######strLexprh:"+strLexprh);
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(strLexprh + "|" + strFactorh);
									lexprFactor.add(strLexprv + "|" + strFactorv);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									cat.debug("------>lexprFactorStr====="+lexprFactorStr);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									 cat.debug("------>strLexprv====="+strLexprv);
									 cat.debug("------>strFactorv====="+strFactorv);
				 					 strHV=cond.getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											userView.getUserName(),
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									StringBuffer sb = new StringBuffer();
									String tmpsql ="";
									if("1".equals(infokind)) {
                                        tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                                    } else if("2".equals(infokind)) {
                                        tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                                    } else if("3".equals(infokind)) {
                                        tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                                    }
									
									if(userbases.indexOf("`")==-1){
										sb.append(tmpsql.replaceAll(userBase, userbases));
									}else{
										String[] tmpdbpres=userbases.split("`");
										for(int n=tmpdbpres.length-1;n>=0;n--){
											String tmpdbpre=tmpdbpres[n];
											if(tmpdbpre.length()==3){
												if(sb.length()>0){
													sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
												}else{
													sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
												}
											}
										}
									}
									strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
									
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										//totalValue += dataValues[i][j];
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1) {
                                                totalValue += dataValues[i][j];
                                            }
										}else{
											totalValue += dataValues[i][j];
										}
									} else {
										dataValues[i][j] = 0;
									}
									cat.debug("------>dataValues[i][j]====="+dataValues[i][j]);
								}
							}
							return dataValues;
						}
					}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				System.out.println(e);
			}finally{
				//exeSql.freeConn();
			}
			return null;
		}
	
	public String getDataSQL(
		int queryId,
		String userbaseT,
		String sqlSelect,
		int v,
		int h,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) {
		String userBase = userbaseT;
		String strFactor = "";
		String strLexpr = "";
		String strFactorv = "";
		String strLexprv = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		try {
			 		
			String sql = "select * from SName where id=" + queryId;
			List rs = ExecuteSQL.executeMyQuery(sql);
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString():"";
				if (doubleLexr != null) {
                    HVLexr = doubleLexr.split(",");
                }
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
					String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
					List rsV = ExecuteSQL.executeMyQuery(sqlV);
					List rsH = ExecuteSQL.executeMyQuery(sqlH);
					ArrayList lexprFactorh=new ArrayList();
					for (int j = 0; j < rsH.size(); j++) {
						rec=(LazyDynaBean)rsH.get(j);
							strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorh.add(strLexprh + "|" + strFactorh);
					}
						CombineFactor combinefactorh=new CombineFactor();
						String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
						StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
						if(Stokh.hasMoreTokens())
						{
							strLexpr=Stokh.nextToken();
							strFactor=Stokh.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[1]);
						rsH.add(rec);
					
					ArrayList lexprFactorv=new ArrayList();
					for (int i = 0; i < rsV.size(); i++) {
						rec=(LazyDynaBean)rsV.get(i);
							strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorv.add(strLexprv + "|" + strFactorv);
					}
						CombineFactor combinefactorv=new CombineFactor();
						String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
						StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
						if(Stokv.hasMoreTokens())
						{
							strLexpr=Stokv.nextToken();
							strFactor=Stokv.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[0]);
						rsV.add(rec);
					
					if (!rsV.isEmpty() && !rsH.isEmpty()) {
						for (int i = 0; i < rsV.size(); i++) {
							if (i == v) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
							    boolean ishavehistory=false;								
								for (int j = 0; j < rsH.size(); j++) {
									if (j == h) {
										LazyDynaBean rech=(LazyDynaBean)rsH.get(j);
										strFactorh = rech.get("factor")!=null?rech.get("factor").toString().trim()  + (rech.get("factor").toString().trim().endsWith("`")?"":"`"):"";
										strLexprh = rech.get("lexpr")!=null?rech.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);										
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}	
										if(commlexpr!=null && commfactor!=null)
										//if("2".equalsIgnoreCase(preresult) &&commlexpr!=null && commfactor!=null)
										{
											String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
										    if(style!=null && style.length==2)
										    {
										    	strLexpr=style[0];
										    	strFactor=style[1];
										    }
										}
										strHV =cond.getCondQueryString(
													strLexpr,
													strFactor,
													userBase,
													ishavehistory,
													username,
													sqlSelect,
													userView,infokind,bresult);										
									}
								}
							}
						}
						return strHV;
					}
				}

			}

		} catch (Exception e) {
			System.out.println("获得二维的SQL语句出错!");
		}
		return null;
	}
	public String getDataSQL(
			String queryv,
			String queryh,
			String userBase,
			String sqlSelect,
			int v,
			int h,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult) {
			String strFactor = "";
			String strLexpr = "";
			String strFactorv = "";
			String strLexprv = "";
			String strFactorh = "";
			String strLexprh = "";
			String strQueryDouble = "";
			String strHV = "";
			String strVFrom = "";
			String strHFrom = "";
			String stokTemp = "";
			String[] HVLexr = new String[] { "x", "x" };
			try {
					HVLexr[0]=queryv;
					HVLexr[1]=queryh;
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0] + " order by norder";
					String sqlH = "select * from SLegend where id=" + HVLexr[1] + " order by norder";
					List rsV = ExecuteSQL.executeMyQuery(sqlV);
					List rsH = ExecuteSQL.executeMyQuery(sqlH);
					ArrayList lexprFactorh=new ArrayList();
					LazyDynaBean rec=null;
					for (int j = 0; j < rsH.size(); j++) {
						rec=(LazyDynaBean)rsH.get(j);
							strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorh.add(strLexprh + "|" + strFactorh);
					}
						CombineFactor combinefactorh=new CombineFactor();
						String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
						StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
						if(Stokh.hasMoreTokens())
						{
							strLexpr=Stokh.nextToken();
							strFactor=Stokh.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[1]);
						rsH.add(rec);
					
					ArrayList lexprFactorv=new ArrayList();
					for (int i = 0; i < rsV.size(); i++) {
						rec=(LazyDynaBean)rsV.get(i);
							strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
							strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
							lexprFactorv.add(strLexprv + "|" + strFactorv);
					}
						CombineFactor combinefactorv=new CombineFactor();
						String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
						StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
						if(Stokv.hasMoreTokens())
						{
							strLexpr=Stokv.nextToken();
							strFactor=Stokv.nextToken();
						}
						rec = new LazyDynaBean();
						rec.set("factor", strFactor);
						rec.set("lexpr", strLexpr);
						rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
						rec.set("id", HVLexr[0]);
						rsV.add(rec);
					if (!rsV.isEmpty() && !rsH.isEmpty()) {
						for (int i = 0; i < rsV.size(); i++) {
							if (i == v) {
								LazyDynaBean recv=(LazyDynaBean)rsV.get(i);
								strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
								strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
							    boolean ishavehistory=false;								
								for (int j = 0; j < rsH.size(); j++) {
									if (j == h) {
										LazyDynaBean rech=(LazyDynaBean)rsH.get(j);
										strFactorh = rech.get("factor")!=null?rech.get("factor").toString().trim()  + (rech.get("factor").toString().trim().endsWith("`")?"":"`"):"";
										strLexprh = rech.get("lexpr")!=null?rech.get("lexpr").toString().trim():"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(strLexprh + "|" + strFactorh);
										lexprFactor.add(strLexprv + "|" + strFactorv);										
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexpr=Stok.nextToken();
											strFactor=Stok.nextToken();
										}									
										strHV =cond.getCondQueryString(
													strLexpr,
													strFactor,
													userBase,
													ishavehistory,
													username,
													null,
													userView,infokind,bresult);										
									}
								}
							}
						}
						return strHV;
					}
				}
			} catch (Exception e) {
				System.out.println("获得二维的SQL语句出错!");
			}
			return null;

		}
	//获得二维并集的纪录数
	public int getRecordCount(
		int queryId,
		String userbaseT,
		String sqlSelect,
		int v,
		int h,
		String username,
		String manageprive,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) {
		try {
			String whereQuery =getDataSQL(queryId, userbaseT, sqlSelect, v, h, username,manageprive,userView,infokind,bresult,commlexpr,commfactor,preresult,history);
			
			int n = 0;
			String sqlQuery = "select count(*) as recordCount " + whereQuery;//  + getUserMangerWheresql(userView,infokind);
			List rscount = ExecuteSQL.executeMyQuery(sqlQuery);
			if (rscount != null && rscount.size()>0) {
				LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
				n = Integer.parseInt(rscountc.get("recordcount").toString());
			} else {
				n = 0;
			}
			return n;
		} catch (Exception e) {
			System.out.println("获得二维的纪录数出错!");
		}
		finally{
		}
		return 0;
	}
	//获得二维的纪录数
	public List getDoubleEncapsulationData(
		int queryId,
		String sqlSelect,
		String username,
		String manageprive,
		String userBase,
		String sqlQuery,
		int v,
		int h,
		int curPage,
		int pageSize,
		UserView userView,
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) {

		try {
			if (v >= 0 && h >= 0) {
				sqlQuery = getDataSQL(queryId, userBase, sqlSelect, v, h, username,manageprive,userView,infokind,bresult,commlexpr,commfactor,preresult,history);
				StringBuffer strQuery =new StringBuffer();
				List rs;
				strQuery.append("select top ");
				strQuery.append(pageSize);
				strQuery.append(" ");
				strQuery.append(userBase);
				strQuery.append("A01.A0000 as A0000,");
				strQuery.append(userBase);
				strQuery.append("A01.A0100 as A0100,");
				strQuery.append(userBase);
				strQuery.append("A01.B0110 as B0110,");
				strQuery.append(userBase);
				strQuery.append("A01.E0122 as E0122,");
				strQuery.append(userBase);
				strQuery.append("A01.A0101 as A0101,");
				strQuery.append(userBase);
				strQuery.append("A01.E01A1 as E01A1 ");
				strQuery.append(sqlQuery);
				//strQuery.append(getUserMangerWheresql(userView,infokind));
				strQuery.append(" AND ");
				strQuery.append(userBase);
				strQuery.append("A01.A0100 not in(select top ");
				strQuery.append((curPage - 1) * pageSize);
				strQuery.append(" ");
				strQuery.append(userBase);
				strQuery.append("A01.A0100 as A0100 ");
				strQuery.append(sqlQuery);
				//strQuery.append(getUserMangerWheresql(userView,infokind));
				strQuery.append(" Order by ");
				strQuery.append(userBase);
				strQuery.append("A01.A0100) Order by ");
				strQuery.append(userBase);
				strQuery.append("A01.A0100");
				rs = ExecuteSQL.executeMyQuery(strQuery.toString());
				return rs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return Returns the display.
	 */
	public String[] getDisplay() {
		return display;
	}
	/**
	 * @param display The display to set.
	 */
	public void setDisplay(String[] display) {
		this.display = display;
	}
	
	/**
	 * @return Returns the recordCount.
	 */
	public int getRecordCount() {
		return recordCount;
	}
	/**
	 * @param recordCount The recordCount to set.
	 */
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	/**
	 * @return Returns the sNameDisplay.
	 */
	public String getSNameDisplay() {
		return SNameDisplay;
	}
	/**
	 * @param nameDisplay The sNameDisplay to set.
	 */
	public void setSNameDisplay(String nameDisplay) {
		SNameDisplay = nameDisplay;
	}
	/**
	 * @return Returns the totalValue.
	 */
	public int getTotalValue() {
		return totalValue;
	}
	/**
	 * @param totalValue The totalValue to set.
	 */
	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}

	/**
	 * @return Returns the horizonArray.
	 */
	public List getHorizonArray() {
		return horizonArray;
	}
	/**
	 * @param horizonArray The horizonArray to set.
	 */
	public void setHorizonArray(List horizonArray) {
		this.horizonArray = horizonArray;
	}
	/**
	 * @return Returns the verticalArray.
	 */
	public List getVerticalArray() {
		return verticalArray;
	}
	/**
	 * @param verticalArray The verticalArray to set.
	 */
	public void setVerticalArray(List verticalArray) {
		this.verticalArray = verticalArray;
	}
	
	private String getYksjParserSql(String sformula,UserView userView) throws GeneralException{
		String sql = "";
		Connection conn = AdminDb.getConnection();
		try{
			YksjParser yp = new YksjParser(userView,getItemList(conn),YksjParser.forSearch,YksjParser.FLOAT,YksjParser.forPerson,"Ht","Usr");
			yp.setCon(conn);
			yp.run(sformula);
			sql = yp.getSQL();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
		return sql;
	}
	
	private ArrayList getItemList(Connection conn){
		ArrayList itemlist = new ArrayList();
		ResultSet rs = null;
		  try{
			  ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from hr_emp_hisdata where 1=2");
			rs = dao.search(sql.toString());
			ResultSetMetaData rsmd = rs.getMetaData();
			int size = rsmd.getColumnCount();

			for (int i = 1; i <= size; i++) {
				String itemid = rsmd.getColumnName(i).toUpperCase();
				if (itemid.length() < 4 || "nbase".equalsIgnoreCase(itemid)
						|| "a0000".equalsIgnoreCase(itemid)) {
                    continue;
                }
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					itemlist.add(fielditem);
				}
			}
			
		  }catch(Exception e){e.printStackTrace();}finally{
			  PubFunc.closeResource(rs);
		  }
		  return itemlist;
	  }
	public String[] getNorder_display() {
		return norder_display;
	}
	public void setNorder_display(String[] norder_display) {
		this.norder_display = norder_display;
	}
	
	public ArrayList getCondlist(String condid,ContentDAO dao)
	{
		ArrayList list=new ArrayList();
		CommonData da=new CommonData();
		
		if (condid != null && condid.length() > 0){
			String []condids = condid.split(",");
			RowSet rs=null;
			try
			{
				for (int i = 0; i < condids.length; i++) {
					String sql = "select id,name from lexpr where id='" + condids[i] + "'";
					rs = dao.search(sql);
					if (rs.next()) {
						da=new CommonData();
						da.setDataValue(rs.getInt("id")+"");
						da.setDataName(rs.getString("name"));
						list.add(da);
					}
				}				
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
		}
		return list;
	}
	public static ArrayList getChartTypeList()
	{
		ArrayList chartTypeList=new ArrayList();
		CommonData da=new CommonData("11",ResourceFactory.getProperty("static.figure.vertical_bar"));
		chartTypeList.add(da);
		/*da=new CommonData("12",ResourceFactory.getProperty("static.figure.vertical_bar_3d"));
		chartTypeList.add(da);
		da=new CommonData("5",ResourceFactory.getProperty("static.figure.pie_3d"));
		chartTypeList.add(da);*/
		da=new CommonData("20",ResourceFactory.getProperty("static.figure.pie"));
		chartTypeList.add(da);
		da=new CommonData("1000",ResourceFactory.getProperty("static.figure.line"));
		chartTypeList.add(da);
		da=new CommonData("40",ResourceFactory.getProperty("static.figure.bar_line"));
		chartTypeList.add(da);
		return chartTypeList;
	}
	
	private int getvarType(String fieldtype){
		int varType = YksjParser.FLOAT; // float
		if ("D".equals(fieldtype)) {
            varType = YksjParser.DATEVALUE;
        } else if ("A".equals(fieldtype) || "M".equals(fieldtype)) {
            varType = YksjParser.STRVALUE;
        }
		return varType;
	}
	public double getTotalValues() {
		return totalValues;
	}
	public void setTotalValues(double totalValues) {
		this.totalValues = totalValues;
	}
	
	private String filterPrivDB(String nbases,UserView userView){
		StringBuffer sb=new StringBuffer();
		String[] tmps=nbases.split(",");
		for(int i=tmps.length-1;i>=0;i--){
			String dbpre=tmps[i];
			if(userView.hasTheDbName(dbpre)){
				sb.append(","+dbpre);
			}
		}
		if(sb.length()>0) {
            return sb.substring(1);
        } else {
            return "";
        }
	}
	private String delI9999(String str){
		int index=0;
		int f=0;
		str=str.replaceAll("I9999 IS NULL", "1=2");
		while(f<20&&(index=str.indexOf("AND (I9999"))>5){
		    //由于and (I9999)的查询条件的长度发生了变化，故将原来的84改为77
			String endStr = str.substring(index+84);//xiegh add 之前将84改成77是不可行的  情况不同时截取的长度不一样   20170726
			if(Character.isLetter('.')) {
                endStr = str.substring(index+77);
            }
			str=str.substring(0,index)+endStr;
			index=str.indexOf("AND (I9999");
			f++;
		}
		while(f<20&&(index=str.indexOf("AND I9999"))>5){
		    //由于and I9999的查询条件的长度发生了变化，故将原来的74改为67
			String endStr = str.substring(index+74);
			if(Character.isLetter('.')) {
                endStr = str.substring(index+67);
            }
			str=str.substring(0,index)+endStr;
			index=str.indexOf("AND I9999");
			f++;
		}
		return str;
	}
	public double[][] getDoubleLexprDataSformula(String[] lengthwayslist,
			String[] crosswiselist, String userbaseT, String sqlSelect, String username,
			String manageprive, UserView userView, String infokind,boolean bresult,
			String commlexpr,String commfactor,String preresult,String history,
			String sformula,Connection conn,String vtotal,String htotal) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		double dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		return null;
	}
	public int[][] getDoubleLexprData(String[] lengthwayslist,
			String[] crosswiselist, String userbaseT,
			String sqlSelect, String username, String manageprive,
			UserView userView, String infokind,boolean bresult,String commlexpr,
			String commfactor,String preresult,String history,String vtotal,String htotal) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		int dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		return null;
	
	}
	public double[][] getDoubleLexprDataSformula(String[] lengthwayslist,
			String[] crosswiselist, String userbaseT,
			String sqlSelect, String username, String manageprive,
			UserView userView, String infokind,boolean bresult,String commlexpr,
			String commfactor,String preresult,String history,String userbases,
			String sformula,Connection conn,String vtotal,String htotal) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		double dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		return null;
	
	}
	public int[][] getDoubleLexprData(String[] lengthwayslist,
			String[] crosswiselist, String userbaseT,
			String sqlSelect, String username, String manageprive,
			UserView userView, String infokind,boolean bresult,String commlexpr,
			String commfactor,String preresult,String history,
			String userbases,String vtotal, String htotal, String vnull, String hnull) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		int dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		
		List cross1 = new ArrayList();
		List cross2 = new ArrayList();
		List cross3 = new ArrayList();
		int crossSize = 0;//数据列数
		int checkCrossSize = 0;
		List length1 = new ArrayList();
		List length2 = new ArrayList();
		List length3 = new ArrayList();
		int lengthSize = 0;//数据行数
		int checkLengthSize = 0;
		HashMap crosslistmap = new HashMap();
		HashMap lengthlistmap = new HashMap();
		ArrayList crossList = new ArrayList();
		ArrayList lengthList = new ArrayList();
		int ci = 0;
		int li = 0;
		try {
		    CrossTabStat stat = new CrossTabStat();
		    stat.setConn(conn);
		    stat.setUserView(userView);
		    stat.setBases(userbases.split("`"));
		    stat.setHVItems(crosswiselist, lengthwayslist);
		    stat.setQueryCond(commlexpr, commfactor);
		    stat.setHtotal(htotal);
		    stat.setVtotal(vtotal);
		    stat.load();  // 加载交叉表数据
		    
			LazyDynaBean rec = null;
			LazyDynaBean rec1 = null;
			LazyDynaBean rec2 = null;
			LazyDynaBean crossRec = null;
			LazyDynaBean lengthRec = null;
			ArrayList lexprFactorh=new ArrayList();
			ArrayList lexprFactorv=new ArrayList();
			ArrayList checkLexprFactorh=new ArrayList();
			ArrayList checkLexprFactorv=new ArrayList();
			ArrayList idxv = new ArrayList();  // 列序号, 隐藏后列序号不变
			ArrayList idxh = new ArrayList();  // 行序号
			for(int i = 0;i < crosswiselist.length;i++){// 行
				if (crosswiselist[i] != null && !"".equals(crosswiselist[i])) {
					String statId = crosswiselist[i].substring(crosswiselist[i].lastIndexOf("_")+1);
					String level1 = crosswiselist[i].substring(0,1);
					String sqlV = "select * from SLegend where id=" + statId + " order by norder";
					if("1".equals(level1)){
						cross1 = ExecuteSQL.executeMyQuery(sqlV);
						if(i<crosswiselist.length-1){
							String level2 = crosswiselist[i+1].substring(0,1);
							if("1".equals(level2)){
								crossSize += cross1.size();
								String crossSql = "select * from SName where id="+statId;
								for (int j = 0; j < cross1.size(); j++) {
									rec=(LazyDynaBean)cross1.get(j);
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									crossRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(crossSql).get(0);
									crossRec.set("legend", crossRec.get("name")!=null?crossRec.get("name").toString().trim():"");
									verticalFirstArray.add(crossRec);
									//一维标题 end
									//二维标题 start
									verticalSecondArray.add(rec);
									//二维标题 end
								}
							}
						}else if(i==crosswiselist.length-1){//横向维度最后一个
							crossSize += cross1.size();
							String crossSql = "select * from SName where id="+statId;
							for (int j = 0; j < cross1.size(); j++) {
								rec=(LazyDynaBean)cross1.get(j);
								strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorv.add(strLexprv + "|" + strFactorv);
								//一维标题 start
								//crossRec = new LazyDynaBean();
								crossRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(crossSql).get(0);
								crossRec.set("legend", crossRec.get("name")!=null?crossRec.get("name").toString().trim():"");
								verticalFirstArray.add(crossRec);
								//一维标题 end
								//二维标题 start
								verticalSecondArray.add(rec);
								//二维标题 end
							}
						}
					}else{
						cross2 = ExecuteSQL.executeMyQuery(sqlV);
						if(i<crosswiselist.length-1){
							String level2 = crosswiselist[i+1].substring(0,1);
							if("1".equals(level2)){
								cross3.addAll(cross2); 
								crossSize += cross1.size()*cross2.size();
								for (int j = 0; j < cross1.size(); j++) {
									rec1=(LazyDynaBean)cross1.get(j);
									for(int n = 0; n < cross3.size(); n++){
										rec2=(LazyDynaBean)cross3.get(n);
										//liuy 2014-9-4 修改人员结构和占比分析  二维交叉表数据显示和数据穿透（修改八处，数据显示和数据穿透各四处） begin
										String vlexpr = (String)rec1.get("lexpr");
										String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
										String hlexpr = (String)rec2.get("lexpr");
										String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(vlexpr + "|" + vfactor);
										lexprFactor.add(hlexpr + "|" + hfactor);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexprv=Stok.nextToken();
											strFactorv=Stok.nextToken();
										}
										//liuy 2014-9-4 修改人员结构和占比分析  二维交叉表数据显示和数据穿透（修改八处，数据显示和数据穿透各四处） end
										lexprFactorv.add(strLexprv + "|" + strFactorv);
										//一维标题 start
										//crossRec = new LazyDynaBean();
										crossRec = new LazyDynaBean();
										crossRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
										verticalFirstArray.add(crossRec);
										//一维标题 end
										//二维标题 start
										verticalSecondArray.add(rec2);
										//二维标题 end
									}
								}
								cross3 = new ArrayList();
							}else{
								cross3.addAll(cross2); 
								crossSize += cross1.size()*cross2.size();
							}
						}else if(i==crosswiselist.length-1){//横向维度最后一个
							cross3.addAll(cross2); 
							crossSize += cross1.size()*cross2.size();
							for(int j = 0;j < cross1.size(); j++){
								rec1=(LazyDynaBean)cross1.get(j);
								for(int n = 0; n < cross3.size(); n++){
									rec2=(LazyDynaBean)cross3.get(n);
									
									String vlexpr = (String)rec1.get("lexpr");
									String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
									String hlexpr = (String)rec2.get("lexpr");
									String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(vlexpr + "|" + vfactor);
									lexprFactor.add(hlexpr + "|" + hfactor);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexprv=Stok.nextToken();
										strFactorv=Stok.nextToken();
									}
									
									lexprFactorv.add(strLexprv + "|" + strFactorv);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									crossRec = new LazyDynaBean();
									crossRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
									verticalFirstArray.add(crossRec);
									//一维标题 end
									//二维标题 start
									verticalSecondArray.add(rec2);
									//二维标题 end
								}
							}
							cross3 = new ArrayList();
						}
					}
				}
			}
			//合计公式
			CombineFactor combinefactorv=new CombineFactor();
			String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
			StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
			if(Stokv.hasMoreTokens())
			{
				strLexprv=Stokv.nextToken();
				strFactorv=Stokv.nextToken();
			}
			lexprFactorv.add(strLexprv + "|" + strFactorv);
			rec = new LazyDynaBean();
			rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
			verticalFirstArray.add(rec);
			verticalSecondArray.add(rec);
			crossSize++;
			for(int i = 0;i < lengthwayslist.length;i++ ){// 列
				if (lengthwayslist[i] != null && !"".equals(lengthwayslist[i])) {
					String statId = lengthwayslist[i].substring(lengthwayslist[i].lastIndexOf("_")+1);
					String level1 = lengthwayslist[i].substring(0,1);
					String sqlH = "select * from SLegend where id=" + statId + " order by norder";
					if("1".equals(level1)){
						length1 = ExecuteSQL.executeMyQuery(sqlH);
						if(i<lengthwayslist.length-1){
							String level2 = lengthwayslist[i+1].substring(0,1);
							if("1".equals(level2)){
								lengthSize += length1.size();
								String lengthSql = "select * from SName where id="+statId;
								for (int j = 0; j < length1.size(); j++) {
									rec=(LazyDynaBean)length1.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
									//一维标题 start
									//lengthRec = new LazyDynaBean();
									lengthRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(lengthSql).get(0);
									lengthRec.set("legend", lengthRec.get("name")!=null?lengthRec.get("name").toString().trim():"");
									horizonFirstArray.add(lengthRec);
									//一维标题 end
									//二维标题 start
									horizonSecondArray.add(rec);
									//二维标题 end
								}
							}
						}else if(i==lengthwayslist.length-1){
							lengthSize += length1.size();
							String lengthSql = "select * from SName where id="+statId;
							for (int j = 0; j < length1.size(); j++) {
								rec=(LazyDynaBean)length1.get(j);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorh.add(strLexprh + "|" + strFactorh);
								//一维标题 start
								//lengthRec = new LazyDynaBean();
								lengthRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(lengthSql).get(0);
								lengthRec.set("legend", lengthRec.get("name")!=null?lengthRec.get("name").toString().trim():"");
								horizonFirstArray.add(lengthRec);
								//一维标题 end
								//二维标题 start
								horizonSecondArray.add(rec);
								//二维标题 end
							}
						}
					}else{
						length2 = ExecuteSQL.executeMyQuery(sqlH);
						if(i<lengthwayslist.length-1){
							String level2 = lengthwayslist[i+1].substring(0,1);
							if("1".equals(level2)){
								length3.addAll(length2); 
								lengthSize += length1.size()*length2.size();
								for(int j = 0;j < length1.size(); j++){
									rec1=(LazyDynaBean)length1.get(j);
									for(int n = 0; n < length3.size(); n++){
										rec2=(LazyDynaBean)length3.get(n);
										
										String vlexpr = (String)rec1.get("lexpr");
										String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
										String hlexpr = (String)rec2.get("lexpr");
										String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(vlexpr + "|" + vfactor);
										lexprFactor.add(hlexpr + "|" + hfactor);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexprh=Stok.nextToken();
											strFactorh=Stok.nextToken();
										}
										
										lexprFactorh.add(strLexprh + "|" + strFactorh);
										//一维标题 start
										//crossRec = new LazyDynaBean();
										lengthRec = new LazyDynaBean();
										lengthRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
										horizonFirstArray.add(lengthRec);
										//一维标题 end
										//二维标题 start
										horizonSecondArray.add(rec2);
										//二维标题 end
									}
								}
								length3 = new ArrayList();
							}else{
								length3.addAll(length2); 
								lengthSize += length1.size()*length2.size();
							}
						}else if(i==lengthwayslist.length-1){
							length3.addAll(length2); 
							lengthSize += length1.size()*length2.size();
							for(int j = 0;j < length1.size(); j++){
								rec1=(LazyDynaBean)length1.get(j);
								for(int n = 0; n < length3.size(); n++){
									rec2=(LazyDynaBean)length3.get(n);
									
									String vlexpr = (String)rec1.get("lexpr");
									String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
									String hlexpr = (String)rec2.get("lexpr");
									String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(vlexpr + "|" + vfactor);
									lexprFactor.add(hlexpr + "|" + hfactor);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexprh=Stok.nextToken();
										strFactorh=Stok.nextToken();
									}
									
									lexprFactorh.add(strLexprh + "|" + strFactorh);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									lengthRec = new LazyDynaBean();
									lengthRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
									horizonFirstArray.add(lengthRec);
									//一维标题 end
									//二维标题 start
									horizonSecondArray.add(rec2);
									//二维标题 end
								}
							}
							length3 = new ArrayList();
						}
					}
				}
			}
			//合计公式
			CombineFactor combinefactorh=new CombineFactor();
			String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
			StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
			if(Stokh.hasMoreTokens())
			{
				strLexprh=Stokh.nextToken();
				strFactorh=Stokh.nextToken();
			}
			lexprFactorh.add(strLexprh + "|" + strFactorh);
			rec = new LazyDynaBean();
			rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
			horizonFirstArray.add(rec);
			horizonSecondArray.add(rec);
			lengthSize++;
			if("1".equals(vnull)){// 隐藏空列
				ArrayList copyVerticalFirstArray = new ArrayList();
				ArrayList copyVerticalSecondArray = new ArrayList();
				for (int i = 0; i < crossSize; i++) {
				    /*
					boolean ishavehistory=false;
					ArrayList lexprFactor=new ArrayList();
					lexprFactor.add((String)lexprFactorh.get(lengthSize-1));
					lexprFactor.add((String)lexprFactorv.get(i));
					CombineFactor combinefactor=new CombineFactor();
					String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
					StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
					if(Stok.hasMoreTokens())
					{
						strLexpr=Stok.nextToken();
						strFactor=Stok.nextToken();
					}
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
					    if(style!=null && style.length==2)
					    {
					    	strLexpr=style[0];
					    	strFactor=style[1];
					    }
					}
						strHV=cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,
							userView,infokind,bresult);
					if(this.whereIN!=null&&this.whereIN.length()>0)
						strHV=strHV+" and "+this.getWhereIN();
					StringBuffer sb = new StringBuffer();
					String tmpsql ="";
					if("1".equals(infokind))
						tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
			    	else if("2".equals(infokind))
			    		tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
			    	else if("3".equals(infokind))
			    		tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
					if(userbases.indexOf("`")==-1){
						sb.append(tmpsql.replaceAll(userBase, userbases));
					}else{
						String[] tmpdbpres=userbases.split("`");
						for(int n=tmpdbpres.length-1;n>=0;n--){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
								}else{
									sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
								}
							}
						}
					}
					strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
					List rscount = ExecuteSQL.executeMyQuery(strHV);  // 检查列合计数是否为零
					*/
					//if (rscount != null && rscount.size()>0 && !"0".equals(((LazyDynaBean)ExecuteSQL.executeMyQuery(strHV).get(0)).get("recordcount"))) {
				    if(stat.getColSum(i) != 0) {
						checkLexprFactorv.add((String)lexprFactorv.get(i));
						idxv.add(Integer.valueOf(i));
						checkCrossSize++;
						copyVerticalFirstArray.add(verticalFirstArray.get(i));
						copyVerticalSecondArray.add(verticalSecondArray.get(i));
					}
				}
				verticalFirstArray = copyVerticalFirstArray;
				verticalSecondArray = copyVerticalSecondArray;
			}
			if("1".equals(hnull)){// 隐藏空行
				ArrayList copyHorizonFirstArray = new ArrayList();
				ArrayList copyHorizonSecondArray = new ArrayList();
				for (int i = 0; i < lengthSize; i++) {
					/*boolean ishavehistory=false;
					ArrayList lexprFactor=new ArrayList();
					lexprFactor.add((String)lexprFactorh.get(i));
					lexprFactor.add((String)lexprFactorv.get(crossSize-1));
					CombineFactor combinefactor=new CombineFactor();
					String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
					StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
					if(Stok.hasMoreTokens())
					{
						strLexpr=Stok.nextToken();
						strFactor=Stok.nextToken();
					}
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						if(style!=null && style.length==2)
						{
							strLexpr=style[0];
							strFactor=style[1];
						}
					}
					strHV=cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,
							userView,infokind,bresult);
					if(this.whereIN!=null&&this.whereIN.length()>0)
						strHV=strHV+" and "+this.getWhereIN();
					StringBuffer sb = new StringBuffer();
					String tmpsql ="";
					if("1".equals(infokind))
						tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
					else if("2".equals(infokind))
						tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
					else if("3".equals(infokind))
						tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
					if(userbases.indexOf("`")==-1){
						sb.append(tmpsql.replaceAll(userBase, userbases));
					}else{
						String[] tmpdbpres=userbases.split("`");
						for(int n=tmpdbpres.length-1;n>=0;n--){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
								}else{
									sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
								}
							}
						}
					}
					strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
					List rscount = ExecuteSQL.executeMyQuery(strHV);
					*/
//					if (rscount != null && rscount.size()>0 && !"0".equals(((LazyDynaBean)ExecuteSQL.executeMyQuery(strHV).get(0)).get("recordcount"))) {
				    if(stat.getRowSum(i) != 0) {
						checkLexprFactorh.add((String)lexprFactorh.get(i));
						idxh.add(Integer.valueOf(i));
						checkLengthSize++;
						copyHorizonFirstArray.add(horizonFirstArray.get(i));
						copyHorizonSecondArray.add(horizonSecondArray.get(i));
					}
				}
				horizonFirstArray = copyHorizonFirstArray;
				horizonSecondArray = copyHorizonSecondArray;
			}
			int vfsize = 0;
			ArrayList checkVerticalFirstArray = new ArrayList();
			for(int i = 0;i < verticalFirstArray.size();i++){
				if(i<verticalFirstArray.size()-1){
					if(((LazyDynaBean)verticalFirstArray.get(i)).get("legend").equals(((LazyDynaBean)verticalFirstArray.get(i+1)).get("legend"))){
						vfsize++;
					}else{
						vfsize++;
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)verticalFirstArray.get(i);
						rec.set("size", String.valueOf(vfsize));
						checkVerticalFirstArray.add(rec);
						vfsize = 0;
					}
				}else{
					if(verticalFirstArray.size()==1){
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)verticalFirstArray.get(i);
						rec.set("size", String.valueOf(1));
						checkVerticalFirstArray.add(rec);
						vfsize = 0;
					}else{
						if(((LazyDynaBean)verticalFirstArray.get(i-1)).get("legend").equals(((LazyDynaBean)verticalFirstArray.get(i)).get("legend"))){
							vfsize++;
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)verticalFirstArray.get(i);
							rec.set("size", String.valueOf(vfsize));
							checkVerticalFirstArray.add(rec);
						}else{
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)verticalFirstArray.get(i);
							rec.set("size", String.valueOf(1));
							checkVerticalFirstArray.add(rec);
							vfsize = 0;
						}
					}
				}
			}
			verticalFirstArray.clear();
			verticalFirstArray = checkVerticalFirstArray;
			
			int hfsize = 0;
			ArrayList checkHorizonFirstArray = new ArrayList();
			for(int i = 0;i < horizonFirstArray.size();i++){
				if(i<horizonFirstArray.size()-1){
					if(((LazyDynaBean)horizonFirstArray.get(i)).get("legend").equals(((LazyDynaBean)horizonFirstArray.get(i+1)).get("legend"))){
						hfsize++;
					}else{
						hfsize++;
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)horizonFirstArray.get(i);
						rec.set("size", String.valueOf(hfsize));
						checkHorizonFirstArray.add(rec);
						hfsize = 0;
					}
				}else{
					if(horizonFirstArray.size()==1){
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)horizonFirstArray.get(i);
						rec.set("size", String.valueOf(1));
						checkHorizonFirstArray.add(rec);
						hfsize = 0;
					}else{
						if(((LazyDynaBean)horizonFirstArray.get(i-1)).get("legend").equals(((LazyDynaBean)horizonFirstArray.get(i)).get("legend"))){
							hfsize++;
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)horizonFirstArray.get(i);
							rec.set("size", String.valueOf(hfsize));
							checkHorizonFirstArray.add(rec);
						}else{
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)horizonFirstArray.get(i);
							rec.set("size", String.valueOf(1));
							checkHorizonFirstArray.add(rec);
							hfsize = 0;
						}
					}
				}
			}
			horizonFirstArray.clear();
			horizonFirstArray = checkHorizonFirstArray;
			if("1".equals(vnull)){
				lexprFactorv=new ArrayList();
				lexprFactorv = checkLexprFactorv;
				crossSize = checkCrossSize;
			}
			if("1".equals(hnull)){
				lexprFactorh=new ArrayList();
				lexprFactorh = checkLexprFactorh;
				lengthSize = checkLengthSize;
			}
			if("1".equals(vtotal)&&"1".equals(htotal)){
			}else if("1".equals(vtotal)){
				lengthSize = lengthSize-1;
				horizonFirstArray.remove(horizonFirstArray.size()-1);
				horizonSecondArray.remove(horizonSecondArray.size()-1);
			}else if("1".equals(htotal)){
				crossSize = crossSize-1;
				verticalFirstArray.remove(verticalFirstArray.size()-1);
				verticalSecondArray.remove(verticalSecondArray.size()-1);
			}else{
				if(!"1".equals(hnull)) {//不隐藏行处理
					lengthSize = lengthSize-1;
					horizonFirstArray.remove(horizonFirstArray.size()-1);
					horizonSecondArray.remove(horizonSecondArray.size()-1);
				}
				if(!"1".equals(vnull)) {//不隐藏列处理
					crossSize = crossSize-1;
					verticalFirstArray.remove(verticalFirstArray.size()-1);
					verticalSecondArray.remove(verticalSecondArray.size()-1);
				}
			}
			dataValues = new int[crossSize][lengthSize];
			if (crossSize>0 && lengthSize>0){
				totalValue = 0;
				int lenght = 1;
				if("1,2,3".equals(infokind)){
					lenght = 3;
				}
				for(int m=1;m<=lenght;m++){
					if(!"1,2,3".equals(infokind)){
						infokind = String.valueOf(m);
					}
					for (int i = 0; i < crossSize; i++) {// 行
						//strFactorv = recv.get("factor")!=null?recv.get("factor").toString().trim() + (recv.get("factor").toString().trim().endsWith("`")?"":"`"):"";
						//strLexprv = recv.get("lexpr")!=null?recv.get("lexpr").toString().trim():"";
						boolean ishavehistory=false;
						for (int j = 0; j < lengthSize; j++) {// 列
							//strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim()  +(rec.get("factor").toString().trim().endsWith("`")?"": "`"):"";
							//strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
/*							ArrayList lexprFactor=new ArrayList();
							lexprFactor.add((String)lexprFactorh.get(j));
							lexprFactor.add((String)lexprFactorv.get(i));
							CombineFactor combinefactor=new CombineFactor();
							String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
							StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
							if(Stok.hasMoreTokens())
							{
								strLexpr=Stok.nextToken();
								strFactor=Stok.nextToken();
							}
							if(commlexpr!=null && commfactor!=null)
							{
								String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
							    if(style!=null && style.length==2)
							    {
							    	strLexpr=style[0];
							    	strFactor=style[1];
							    }
							}
								strHV=cond.getCondQueryString(
									strLexpr,
									strFactor,
									userBase,
									ishavehistory,
									username,
									sqlSelect,
									userView,infokind,bresult);
							if(this.whereIN!=null&&this.whereIN.length()>0)
								strHV=strHV+" and "+this.getWhereIN();
							StringBuffer sb = new StringBuffer();
							String tmpsql ="";
							if("1".equals(infokind))
								tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
					    	else if("2".equals(infokind))
					    		tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
					    	else if("3".equals(infokind))
					    		tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
							if(userbases.indexOf("`")==-1){
								sb.append(tmpsql.replaceAll(userBase, userbases));
							}else{
								String[] tmpdbpres=userbases.split("`");
								for(int n=tmpdbpres.length-1;n>=0;n--){
									String tmpdbpre=tmpdbpres[n];
									if(tmpdbpre.length()==3){
										if(sb.length()>0){
											sb.append(" union all "+tmpsql.replaceAll(userBase, tmpdbpre));
										}else{
											sb.append(tmpsql.replaceAll(userBase, tmpdbpre));
										}
									}
								}
							}
							strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
							List rscount = ExecuteSQL.executeMyQuery(strHV);
							if (rscount != null && rscount.size()>0) {
								LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
								String tmp=rscountc.get("recordcount").toString();
								if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
									tmp="0";
								dataValues[i][j] = Integer.parseInt(tmp);
								if("1".equals(vtotal)&&"1".equals(htotal)){
									if(j<lengthSize-1&&i<crossSize-1)
										totalValue += dataValues[i][j];
								}else if("1".equals(vtotal)){
									if(i<crossSize-1)
										totalValue += dataValues[i][j];
								}else if("1".equals(htotal)){
									if(j<lengthSize-1)
										totalValue += dataValues[i][j];
								}else{
									totalValue += dataValues[i][j];
								}
							} else {
								dataValues[i][j] = 0;
							}
							*/
						    int row = j;
						    if(idxh.size()>0) {
                                row = ((Integer)idxh.get(j)).intValue();
                            }
						    int col = i;
						    if(idxv.size()>0) {
                                col = ((Integer)idxv.get(i)).intValue();
                            }
                            dataValues[i][j] = stat.getIntValue(col, row);
						}
					}
				}
				totalValue = stat.getIntTotal();
				totalValues = stat.getIntTotal();
				stat.unload();
				return dataValues;
			}
		} catch (Exception e) {
			System.out.println("生成二维交叉的数据出错!");
			e.printStackTrace();
		}finally{
		}
		return null;
	}
	public List getVerticalFirstArray() {
		return verticalFirstArray;
	}
	public void setVerticalFirstArray(List verticalFirstArray) {
		this.verticalFirstArray = verticalFirstArray;
	}
	public List getVerticalSecondArray() {
		return verticalSecondArray;
	}
	public void setVerticalSecondArray(List verticalSecondArray) {
		this.verticalSecondArray = verticalSecondArray;
	}
	public List getHorizonFirstArray() {
		return horizonFirstArray;
	}
	public void setHorizonFirstArray(List horizonFirstArray) {
		this.horizonFirstArray = horizonFirstArray;
	}
	public List getHorizonSecondArray() {
		return horizonSecondArray;
	}
	public void setHorizonSecondArray(List horizonSecondArray) {
		this.horizonSecondArray = horizonSecondArray;
	}
	public String getDataSQL(String[] lengthwayslist, String[] crosswiselist,
			String userbaseT, String sqlSelect, int v, int h, String username,
			String managePrivCode, UserView userView, String infokind,
			boolean bresult, String commlexpr, String commfactor, String preresult,
			String history, String userbases, String vtotal, String htotal, String vnull, String hnull) {
		String userBase = userbaseT;
		String strFactorv = "";
		String strLexprv = "";
		String strFactor = "";
		String strLexpr = "";
		String strFactorh = "";
		String strLexprh = "";
		String strQueryDouble = "";
		String strHV = "";
		int dataValues[][];
		String strVFrom = "";
		String strHFrom = "";
		String stokTemp = "";
		String[] HVLexr = new String[] { "x", "x" };
		
		List cross1 = new ArrayList();
		List cross2 = new ArrayList();
		List cross3 = new ArrayList();
		int crossSize = 0;//数据列数
		int checkCrossSize = 0;
		List length1 = new ArrayList();
		List length2 = new ArrayList();
		List length3 = new ArrayList();
		int lengthSize = 0;//数据行数
		int checkLengthSize = 0;
		HashMap crosslistmap = new HashMap();
		HashMap lengthlistmap = new HashMap();
		ArrayList crossList = new ArrayList();
		ArrayList lengthList = new ArrayList();
		int ci = 0;
		int li = 0;
		try {
			LazyDynaBean rec = null;
			LazyDynaBean rec1 = null;
			LazyDynaBean rec2 = null;
			LazyDynaBean crossRec = null;
			LazyDynaBean lengthRec = null;
			ArrayList lexprFactorh=new ArrayList();
			ArrayList lexprFactorv=new ArrayList();
			ArrayList checkLexprFactorh=new ArrayList();
			ArrayList checkLexprFactorv=new ArrayList();
			for(int i = 0;i < crosswiselist.length;i++){
				if (crosswiselist[i] != null && !"".equals(crosswiselist[i])) {
					String statId = crosswiselist[i].substring(crosswiselist[i].lastIndexOf("_")+1);
					String level1 = crosswiselist[i].substring(0,1);
					String sqlV = "select * from SLegend where id=" + statId + " order by norder";
					if("1".equals(level1)){
						cross1 = ExecuteSQL.executeMyQuery(sqlV);
						if(i<crosswiselist.length-1){
							String level2 = crosswiselist[i+1].substring(0,1);
							if("1".equals(level2)){
								crossSize += cross1.size();
								String crossSql = "select * from SName where id="+statId;
								for (int j = 0; j < cross1.size(); j++) {
									rec=(LazyDynaBean)cross1.get(j);
									strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorv.add(strLexprv + "|" + strFactorv);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									crossRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(crossSql).get(0);
									crossRec.set("legend", crossRec.get("name")!=null?crossRec.get("name").toString().trim():"");
									verticalFirstArray.add(crossRec);
									//一维标题 end
									//二维标题 start
									verticalSecondArray.add(rec);
									//二维标题 end
								}
							}
						}else if(i==crosswiselist.length-1){//横向维度最后一个
							crossSize += cross1.size();
							String crossSql = "select * from SName where id="+statId;
							for (int j = 0; j < cross1.size(); j++) {
								rec=(LazyDynaBean)cross1.get(j);
								strFactorv = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprv = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorv.add(strLexprv + "|" + strFactorv);
								//一维标题 start
								//crossRec = new LazyDynaBean();
								crossRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(crossSql).get(0);
								crossRec.set("legend", crossRec.get("name")!=null?crossRec.get("name").toString().trim():"");
								verticalFirstArray.add(crossRec);
								//一维标题 end
								//二维标题 start
								verticalSecondArray.add(rec);
								//二维标题 end
							}
						}
					}else{
						cross2 = ExecuteSQL.executeMyQuery(sqlV);
						if(i<crosswiselist.length-1){
							String level2 = crosswiselist[i+1].substring(0,1);
							if("1".equals(level2)){
								cross3.addAll(cross2); 
								crossSize += cross1.size()*cross2.size();
								for (int j = 0; j < cross1.size(); j++) {
									rec1=(LazyDynaBean)cross1.get(j);
									for(int n = 0; n < cross3.size(); n++){
										rec2=(LazyDynaBean)cross3.get(n);
										
										String vlexpr = (String)rec1.get("lexpr");
										String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
										String hlexpr = (String)rec2.get("lexpr");
										String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(vlexpr + "|" + vfactor);
										lexprFactor.add(hlexpr + "|" + hfactor);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexprv=Stok.nextToken();
											strFactorv=Stok.nextToken();
										}
										lexprFactorv.add(strLexprv + "|" + strFactorv);
										//一维标题 start
										//crossRec = new LazyDynaBean();
										crossRec = new LazyDynaBean();
										crossRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
										verticalFirstArray.add(crossRec);
										//一维标题 end
										//二维标题 start
										verticalSecondArray.add(rec2);
										//二维标题 end
									}
								}
								cross3 = new ArrayList();
							}else{
								cross3.addAll(cross2); 
								crossSize += cross1.size()*cross2.size();
							}
						}else if(i==crosswiselist.length-1){//横向维度最后一个
							cross3.addAll(cross2); 
							crossSize += cross1.size()*cross2.size();
							for(int j = 0;j < cross1.size(); j++){
								rec1=(LazyDynaBean)cross1.get(j);
								for(int n = 0; n < cross3.size(); n++){
									rec2=(LazyDynaBean)cross3.get(n);
									
									String vlexpr = (String)rec1.get("lexpr");
									String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
									String hlexpr = (String)rec2.get("lexpr");
									String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(vlexpr + "|" + vfactor);
									lexprFactor.add(hlexpr + "|" + hfactor);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexprv=Stok.nextToken();
										strFactorv=Stok.nextToken();
									}
									
									lexprFactorv.add(strLexprv + "|" + strFactorv);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									crossRec = new LazyDynaBean();
									crossRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
									verticalFirstArray.add(crossRec);
									//一维标题 end
									//二维标题 start
									verticalSecondArray.add(rec2);
									//二维标题 end
								}
							}
							cross3 = new ArrayList();
						}
					}
				}
			}
			//合计公式
			CombineFactor combinefactorv=new CombineFactor();
			String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
			StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
			if(Stokv.hasMoreTokens())
			{
				strLexprv=Stokv.nextToken();
				strFactorv=Stokv.nextToken();
			}
			lexprFactorv.add(strLexprv + "|" + strFactorv);
			rec = new LazyDynaBean();
			rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
			verticalFirstArray.add(rec);
			verticalSecondArray.add(rec);
			crossSize++;
			for(int i = 0;i < lengthwayslist.length;i++ ){
				if (lengthwayslist[i] != null && !"".equals(lengthwayslist[i])) {
					String statId = lengthwayslist[i].substring(lengthwayslist[i].lastIndexOf("_")+1);
					String level1 = lengthwayslist[i].substring(0,1);
					String sqlH = "select * from SLegend where id=" + statId + " order by norder";
					if("1".equals(level1)){
						length1 = ExecuteSQL.executeMyQuery(sqlH);
						if(i<lengthwayslist.length-1){
							String level2 = lengthwayslist[i+1].substring(0,1);
							if("1".equals(level2)){
								lengthSize += length1.size();
								String lengthSql = "select * from SName where id="+statId;
								for (int j = 0; j < length1.size(); j++) {
									rec=(LazyDynaBean)length1.get(j);
									strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
									strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
									lexprFactorh.add(strLexprh + "|" + strFactorh);
									//一维标题 start
									//lengthRec = new LazyDynaBean();
									lengthRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(lengthSql).get(0);
									lengthRec.set("legend", lengthRec.get("name")!=null?lengthRec.get("name").toString().trim():"");
									horizonFirstArray.add(lengthRec);
									//一维标题 end
									//二维标题 start
									horizonSecondArray.add(rec);
									//二维标题 end
								}
							}
						}else if(i==lengthwayslist.length-1){
							lengthSize += length1.size();
							String lengthSql = "select * from SName where id="+statId;
							for (int j = 0; j < length1.size(); j++) {
								rec=(LazyDynaBean)length1.get(j);
								strFactorh = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
								strLexprh = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
								lexprFactorh.add(strLexprh + "|" + strFactorh);
								//一维标题 start
								//lengthRec = new LazyDynaBean();
								lengthRec = (LazyDynaBean)ExecuteSQL.executeMyQuery(lengthSql).get(0);
								lengthRec.set("legend", lengthRec.get("name")!=null?lengthRec.get("name").toString().trim():"");
								horizonFirstArray.add(lengthRec);
								//一维标题 end
								//二维标题 start
								horizonSecondArray.add(rec);
								//二维标题 end
							}
						}
					}else{
						length2 = ExecuteSQL.executeMyQuery(sqlH);
						if(i<lengthwayslist.length-1){
							String level2 = lengthwayslist[i+1].substring(0,1);
							if("1".equals(level2)){
								length3.addAll(length2); 
								lengthSize += length1.size()*length2.size();
								for(int j = 0;j < length1.size(); j++){
									rec1=(LazyDynaBean)length1.get(j);
									for(int n = 0; n < length3.size(); n++){
										rec2=(LazyDynaBean)length3.get(n);
										
										String vlexpr = (String)rec1.get("lexpr");
										String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
										String hlexpr = (String)rec2.get("lexpr");
										String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
										ArrayList lexprFactor=new ArrayList();
										lexprFactor.add(vlexpr + "|" + vfactor);
										lexprFactor.add(hlexpr + "|" + hfactor);
										CombineFactor combinefactor=new CombineFactor();
										String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
										StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
										if(Stok.hasMoreTokens())
										{
											strLexprh=Stok.nextToken();
											strFactorh=Stok.nextToken();
										}
										
										lexprFactorh.add(strLexprh + "|" + strFactorh);
										//一维标题 start
										//crossRec = new LazyDynaBean();
										lengthRec = new LazyDynaBean();
										lengthRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
										horizonFirstArray.add(lengthRec);
										//一维标题 end
										//二维标题 start
										horizonSecondArray.add(rec2);
										//二维标题 end
									}
								}
								length3 = new ArrayList();
							}else{
								length3.addAll(length2); 
								lengthSize += length1.size()*length2.size();
							}
						}else if(i==lengthwayslist.length-1){
							length3.addAll(length2); 
							lengthSize += length1.size()*length2.size();
							for(int j = 0;j < length1.size(); j++){
								rec1=(LazyDynaBean)length1.get(j);
								for(int n = 0; n < length3.size(); n++){
									rec2=(LazyDynaBean)length3.get(n);
									
									String vlexpr = (String)rec1.get("lexpr");
									String vfactor = (String)rec1.get("factor")!=null?rec1.get("factor").toString().trim() + "`":"";
									String hlexpr = (String)rec2.get("lexpr");
									String hfactor = (String)rec2.get("factor")!=null?rec2.get("factor").toString().trim() + "`":"";
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add(vlexpr + "|" + vfactor);
									lexprFactor.add(hlexpr + "|" + hfactor);
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexprh=Stok.nextToken();
										strFactorh=Stok.nextToken();
									}
									
									lexprFactorh.add(strLexprh + "|" + strFactorh);
									//一维标题 start
									//crossRec = new LazyDynaBean();
									lengthRec = new LazyDynaBean();
									lengthRec.set("legend", rec1.get("legend")!=null?rec1.get("legend").toString().trim():"");
									horizonFirstArray.add(lengthRec);
									//一维标题 end
									//二维标题 start
									horizonSecondArray.add(rec2);
									//二维标题 end
								}
							}
							length3 = new ArrayList();
						}
					}
				}
			}
			//合计公式
			CombineFactor combinefactorh=new CombineFactor();
			String lexprFactorStrh=combinefactorh.getCombineFactorExpr(lexprFactorh,1);
			StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
			if(Stokh.hasMoreTokens())
			{
				strLexprh=Stokh.nextToken();
				strFactorh=Stokh.nextToken();
			}
			lexprFactorh.add(strLexprh + "|" + strFactorh);
			rec = new LazyDynaBean();
			rec.set("legend", ResourceFactory.getProperty("planar.stat.total"));
			horizonFirstArray.add(rec);
			horizonSecondArray.add(rec);
			lengthSize++;
			if("1".equals(vnull)){
				ArrayList copyVerticalFirstArray = new ArrayList();
				ArrayList copyVerticalSecondArray = new ArrayList();
				for (int i = 0; i < crossSize; i++) {
					boolean ishavehistory=false;
					ArrayList lexprFactor=new ArrayList();
					lexprFactor.add((String)lexprFactorh.get(lengthSize-1));
					lexprFactor.add((String)lexprFactorv.get(i));
					CombineFactor combinefactor=new CombineFactor();
					String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
					StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
					if(Stok.hasMoreTokens())
					{
						strLexpr=Stok.nextToken();
						strFactor=Stok.nextToken();
					}
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
					    if(style!=null && style.length==2)
					    {
					    	strLexpr=style[0];
					    	strFactor=style[1];
					    }
					}
					
					//处理多维统计合计穿透，人数不对问题
					FactorList parser = new FactorList(strLexpr, strFactor, userBase, true, false, true, 1, userView.getUserName());
					ArrayList fieldList = parser.getFieldList();
					StringBuffer whereSql = new StringBuffer();
					for(int f = 0 ; f < fieldList.size() ; f++) {
						FieldItem fieldItem =(FieldItem)fieldList.get(f);
						if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
							continue;
						}
						whereSql.append(" "+userBase+fieldItem.getFieldsetid()+".A0100 is not null and ");
					}
					if(whereSql.length()>0) {
						whereSql.setLength(whereSql.length()-4);
					}	
						strHV=cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,
							userView,infokind,bresult);
						strHV = strHV.replaceAll("or I9999 IS NULL","or I9999 IS NULL and " + whereSql);
						
					if(this.whereIN!=null&&this.whereIN.length()>0) {
                        strHV=strHV+" and "+this.getWhereIN();
                    }
					StringBuffer sb = new StringBuffer();
					String tmpsql ="";
					if("1".equals(infokind)) {
                        tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                    } else if("2".equals(infokind)) {
                        tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                    } else if("3".equals(infokind)) {
                        tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                    }
					if(userbases.indexOf("`")==-1){
						sb.append(tmpsql.replaceAll(userBase, userbases));
					}else{
						String[] tmpdbpres=userbases.split("`");
						for(int n=tmpdbpres.length-1;n>=0;n--){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll(userBase.toUpperCase(), tmpdbpre));
								}else{
									sb.append(tmpsql.replaceAll(userBase.toUpperCase(), tmpdbpre));
								}
							}
						}
					}
					strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
					List rscount = ExecuteSQL.executeMyQuery(strHV);
					if (rscount != null && rscount.size()>0 && !"0".equals(((LazyDynaBean)ExecuteSQL.executeMyQuery(strHV).get(0)).get("recordcount"))) {
						checkLexprFactorv.add((String)lexprFactorv.get(i));
						checkCrossSize++;
						copyVerticalFirstArray.add(verticalFirstArray.get(i));
						copyVerticalSecondArray.add(verticalSecondArray.get(i));
					}
				}
				verticalFirstArray = copyVerticalFirstArray;
				verticalSecondArray = copyVerticalSecondArray;
			}
			if("1".equals(hnull)){
				ArrayList copyHorizonFirstArray = new ArrayList();
				ArrayList copyHorizonSecondArray = new ArrayList();
				for (int i = 0; i < lengthSize; i++) {
					boolean ishavehistory=false;
					ArrayList lexprFactor=new ArrayList();
					lexprFactor.add((String)lexprFactorh.get(i));
					lexprFactor.add((String)lexprFactorv.get(crossSize-1));
					CombineFactor combinefactor=new CombineFactor();
					String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
					StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
					if(Stok.hasMoreTokens())
					{
						strLexpr=Stok.nextToken();
						strFactor=Stok.nextToken();
					}
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						if(style!=null && style.length==2)
						{
							strLexpr=style[0];
							strFactor=style[1];
						}
					}
					//处理多维统计合计穿透，人数不对问题
					FactorList parser = new FactorList(strLexpr, strFactor, userBase, true, false, true, 1, userView.getUserName());
					ArrayList fieldList = parser.getFieldList();
					StringBuffer whereSql = new StringBuffer();
					for(int f = 0 ; f < fieldList.size() ; f++) {
						FieldItem fieldItem =(FieldItem)fieldList.get(f);
						if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
							continue;
						}
						whereSql.append(" "+userBase+fieldItem.getFieldsetid()+".A0100 is not null and ");
					}
					if(whereSql.length()>0) {
						whereSql.setLength(whereSql.length()-4);
					}	
						strHV=cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,
							userView,infokind,bresult);
						strHV = strHV.replaceAll("or I9999 IS NULL","or I9999 IS NULL and " + whereSql);
					if(this.whereIN!=null&&this.whereIN.length()>0) {
                        strHV=strHV+" and "+this.getWhereIN();
                    }
					StringBuffer sb = new StringBuffer();
					String tmpsql ="";
					if("1".equals(infokind)) {
                        tmpsql = ("select distinct " + userBase + "a01.a0100 as a0100" + strHV).toUpperCase();
                    } else if("2".equals(infokind)) {
                        tmpsql = ("select distinct b01.b0110 as b0110 " + strHV).toUpperCase();
                    } else if("3".equals(infokind)) {
                        tmpsql = ("select distinct k01.e01a1 as e01a1 " + strHV).toUpperCase();
                    }
					if(userbases.indexOf("`")==-1){
						sb.append(tmpsql.replaceAll(userBase, userbases));
					}else{
						String[] tmpdbpres=userbases.split("`");
						for(int n=tmpdbpres.length-1;n>=0;n--){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll(userBase.toUpperCase(), tmpdbpre));
								}else{
									sb.append(tmpsql.replaceAll(userBase.toUpperCase(), tmpdbpre));
								}
							}
						}
					}
					strHV =	"select count(*) as recordCount from (" + sb.toString()+") tt";
					List rscount = ExecuteSQL.executeMyQuery(strHV);
					if (rscount != null && rscount.size()>0 && !"0".equals(((LazyDynaBean)ExecuteSQL.executeMyQuery(strHV).get(0)).get("recordcount"))) {
						checkLexprFactorh.add((String)lexprFactorh.get(i));
						checkLengthSize++;
						copyHorizonFirstArray.add(horizonFirstArray.get(i));
						copyHorizonSecondArray.add(horizonSecondArray.get(i));
					}
				}
				horizonFirstArray = copyHorizonFirstArray;
				horizonSecondArray = copyHorizonSecondArray;
			}
			int vfsize = 0;
			ArrayList checkVerticalFirstArray = new ArrayList();
			for(int i = 0;i < verticalFirstArray.size();i++){
				if(i<verticalFirstArray.size()-1){
					if(((LazyDynaBean)verticalFirstArray.get(i)).get("legend").equals(((LazyDynaBean)verticalFirstArray.get(i+1)).get("legend"))){
						vfsize++;
					}else{
						vfsize++;
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)verticalFirstArray.get(i);
						rec.set("size", String.valueOf(vfsize));
						checkVerticalFirstArray.add(rec);
						vfsize = 0;
					}
				}else{
					if(verticalFirstArray.size()==1){
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)verticalFirstArray.get(i);
						rec.set("size", String.valueOf(1));
						checkVerticalFirstArray.add(rec);
						vfsize = 0;
					}else{
						if(((LazyDynaBean)verticalFirstArray.get(i-1)).get("legend").equals(((LazyDynaBean)verticalFirstArray.get(i)).get("legend"))){
							vfsize++;
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)verticalFirstArray.get(i);
							rec.set("size", String.valueOf(vfsize));
							checkVerticalFirstArray.add(rec);
						}else{
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)verticalFirstArray.get(i);
							rec.set("size", String.valueOf(1));
							checkVerticalFirstArray.add(rec);
							vfsize = 0;
						}
					}
				}
			}
			verticalFirstArray.clear();
			verticalFirstArray = checkVerticalFirstArray;
			
			int hfsize = 0;
			ArrayList checkHorizonFirstArray = new ArrayList();
			for(int i = 0;i < horizonFirstArray.size();i++){
				if(i<horizonFirstArray.size()-1){
					if(((LazyDynaBean)horizonFirstArray.get(i)).get("legend").equals(((LazyDynaBean)horizonFirstArray.get(i+1)).get("legend"))){
						hfsize++;
					}else{
						hfsize++;
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)horizonFirstArray.get(i);
						rec.set("size", String.valueOf(hfsize));
						checkHorizonFirstArray.add(rec);
						hfsize = 0;
					}
				}else{
					if(horizonFirstArray.size()==1){
						rec = new LazyDynaBean();
						rec = (LazyDynaBean)horizonFirstArray.get(i);
						rec.set("size", String.valueOf(1));
						checkHorizonFirstArray.add(rec);
						hfsize = 0;
					}else{
						if(((LazyDynaBean)horizonFirstArray.get(i-1)).get("legend").equals(((LazyDynaBean)horizonFirstArray.get(i)).get("legend"))){
							hfsize++;
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)horizonFirstArray.get(i);
							rec.set("size", String.valueOf(hfsize));
							checkHorizonFirstArray.add(rec);
						}else{
							rec = new LazyDynaBean();
							rec = (LazyDynaBean)horizonFirstArray.get(i);
							rec.set("size", String.valueOf(1));
							checkHorizonFirstArray.add(rec);
							hfsize = 0;
						}
					}
				}
			}
			horizonFirstArray.clear();
			horizonFirstArray = checkHorizonFirstArray;
			if("1".equals(vnull)){
				lexprFactorv=new ArrayList();
				lexprFactorv = checkLexprFactorv;
				crossSize = checkCrossSize;
			}
			if("1".equals(hnull)){
				lexprFactorh=new ArrayList();
				lexprFactorh = checkLexprFactorh;
				lengthSize = checkLengthSize;
			}
			if("1".equals(vtotal)&&"1".equals(htotal)){
			}else if("1".equals(vtotal)){
				lengthSize = lengthSize-1;
				horizonFirstArray.remove(horizonFirstArray.size()-1);
				horizonSecondArray.remove(horizonSecondArray.size()-1);
			}else if("1".equals(htotal)){
				crossSize = crossSize-1;
				verticalFirstArray.remove(verticalFirstArray.size()-1);
				verticalSecondArray.remove(verticalSecondArray.size()-1);
			}else{
				crossSize = crossSize-1;
				lengthSize = lengthSize-1;
				horizonFirstArray.remove(horizonFirstArray.size()-1);
				horizonSecondArray.remove(horizonSecondArray.size()-1);
				verticalFirstArray.remove(verticalFirstArray.size()-1);
				verticalSecondArray.remove(verticalSecondArray.size()-1);
			}
			dataValues = new int[crossSize][lengthSize];
			if (crossSize>0 && lengthSize>0){
				totalValue = 0;
				int lenght = 1;
				if("1,2,3".equals(infokind)){
					lenght = 3;
				}
				for(int m=1;m<=lenght;m++){
					if(!"1,2,3".equals(infokind)){
						infokind = String.valueOf(m);
					}
					for (int i = 0; i < crossSize; i++) {
						if (i == v) {
							boolean ishavehistory=false;
							for (int j = 0; j < lengthSize; j++) {
								if (j == h) {
									ArrayList lexprFactor=new ArrayList();
									lexprFactor.add((String)lexprFactorh.get(j));
									lexprFactor.add((String)lexprFactorv.get(i));
									CombineFactor combinefactor=new CombineFactor();
									String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
									StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
									if(Stok.hasMoreTokens())
									{
										strLexpr=Stok.nextToken();
										strFactor=Stok.nextToken();
									}
									if(commlexpr!=null && commfactor!=null)
									{
										String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
									    if(style!=null && style.length==2)
									    {
									    	strLexpr=style[0];
									    	strFactor=style[1];
									    }
									}
									//处理多维统计合计穿透，人数不对问题
									FactorList parser = new FactorList(strLexpr, strFactor, userBase, true, false, true, 1, userView.getUserName());
									ArrayList fieldList = parser.getFieldList();
									StringBuffer whereSql = new StringBuffer();
									for(int f = 0 ; f < fieldList.size() ; f++) {
										FieldItem fieldItem =(FieldItem)fieldList.get(f);
										if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
											continue;
										}
										whereSql.append(" "+userBase+fieldItem.getFieldsetid()+".A0100 is not null and ");
									}
									if(whereSql.length()>0) {
										whereSql.setLength(whereSql.length()-4);
									}
									strHV=cond.getCondQueryString(
										strLexpr,
										strFactor,
										userBase,
										ishavehistory,
										username,
										sqlSelect,
										userView,infokind,bresult);
									strHV = strHV.replaceAll("or I9999 IS NULL","or I9999 IS NULL and " + whereSql);
								}
							}
						}
					}
				}
				return strHV;
			}
		} catch (Exception e) {
			System.out.println("生成二维交叉的数据出错!");
			e.printStackTrace();
		}finally{
		}
		return null;
	}
	/** 
	 * liubq  2015-11-5
	 * 获取阀值的公式的值
	 * 
	 * 
	 * ***/
	
	public double[] getLexprDataSformula(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String valvetype,ArrayList valve,String userbases,String where) throws Exception {
			double[] fieldValues;
			//try {
				
				
			String userBase = userbase;
			String strFactor = "";
			String strLexpr = "";
			String strQuery = "";
			String flag="";
			//获得统计项
			StringBuffer sql =new StringBuffer();
			/*sql.append("select * from SName where id=");
			sql.append(queryId);
			List rs =ExecuteSQL.executeMyQuery(sql.toString());
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
			}*/
			SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
			Element element = xml.getElement(sformula);
			//获得各个统计项的图例
			sql.delete(0,sql.length());
			sql.append("select * from SLegend where id=");
			sql.append(queryId);          
			sql.append(" order by norder");          
			List dataset = ExecuteSQL.executeMyQuery(sql.toString());
			fieldValues = new double[dataset.size()];
			display = new String[dataset.size()];
			norder_display=new String[dataset.size()];
//			int lenght = 1;
			String newInfo = infokind;//add by wangchaoqun on 2014-9-25 保存info的值便于后面的判断
			if("1,2,3".equals(newInfo)){
				infokind = currInfoKind(queryId);// add by liubq 2015-09-28 当infokind为"1,2,3"时,获取统计的infokind（1人,2单,3岗）
//				lenght = 3;
			}
//			for(int m=1;m<=lenght;m++){
//				if("1,2,3".equals(newInfo)){
//					infokind = String.valueOf(m);
//				}
				for (int i = 0; i < dataset.size(); i++) {
					LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
					strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
					strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
					flag=rec.get("flag")!=null?rec.get("flag").toString():"";
					//System.out.println(strFactor);
					display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
					norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
					//根据表达式和因子生成统计的sql语句
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
					if(commlexpr!=null && commfactor!=null)
					{
						String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
					    if(style!=null && style.length==2)
					    {
					    	strLexpr=style[0];
					    	strFactor=style[1];
					    }
					}
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					boolean ishavehistory=false;
					if("1".equals(flag)) {
                        ishavehistory=true;
                    } else if("1".equals(history)) {
                        ishavehistory=true;
                    }
							strQuery =cond.getCondQueryString(
							strLexpr,
							strFactor,
							userBase,
							ishavehistory,
							username,
							sqlSelect,userView,infokind,bresult);
					//cat.debug("---->sql======" + strQuery);
							//System.out.println(strQuery);
					if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
					{
						strQuery=strQuery+" and "+this.getWhereIN();
					}	
					strQuery = strQuery.toUpperCase();
					String type=element.getAttributeValue("type");
					String expr=(String)valve.get(0);
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userbase);
					yp.setCon(conn);
                    yp.setTempTableName("temp_"+username + queryId);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
					/**
					 * 【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列明白“A0100”不明确”
					 *  新增lsql，根据设置的统计条件获取数据
					 *  jingq add 2015.04.16
					 */
					FactorList factorlist= new FactorList(strLexpr,strFactor,userbase,ishavehistory,true,bresult,Integer.parseInt(infokind),username);
			        String lsql = factorlist.getSqlExpression();
			        lsql = lsql.substring(lsql.indexOf("WHERE")+5);
			        
					for(int n=0;n<usedsets.size();n++){
						String set = (String)usedsets.get(n);
						if("1".equals(infokind)){
							set = (" "+userbase+set).toUpperCase();
							StringBuffer sb = new StringBuffer();
							if(basesql.indexOf(set)==-1){
								sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
								sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
								sb.append(basesql.substring(basesql.indexOf(" WHERE")));
								sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100 AND "+lsql+") OR "+set+".I9999 IS NULL)");
								basesql= sb.toString();
							}
						}else if("2".equals(infokind)){
							set = (" "+set).toUpperCase();
							StringBuffer sb = new StringBuffer();
							if(strQuery.indexOf(set)==-1){
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
								strQuery= sb.toString();
							}
						}else if("3".equals(infokind)){
							set = (" "+set).toUpperCase();
							StringBuffer sb = new StringBuffer();
							if(strQuery.indexOf(set)==-1){
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
								strQuery= sb.toString();
							}
						}
					}
					
					// WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
					if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
						StringBuffer sb = new StringBuffer();
						if("1".equals(infokind)){
							sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
							sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userbase+"A01.A0100="+yp.getTempTableName()+".A0100");
							sb.append(basesql.substring(basesql.indexOf(" WHERE")));
							basesql= sb.toString();
						} else  if("2".equals(infokind)) {
							sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
							//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
							sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
							sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
							strQuery= sb.toString();
						} else if("3".equals(infokind)) {
							sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
							//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/	
							sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1");	
							sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
							strQuery= sb.toString();
						}
					}
					
					//System.out.println(strQuery);
					if("1".equals(infokind)){
						StringBuffer sb = new StringBuffer();//【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列名称“A0100”不明确”  jingq upd 2015.04.15
						String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select "+userbase.toUpperCase()+"A01.a0100 "+strQuery+")").toUpperCase();
						if(userbases.indexOf("`")==-1){
							sb.append(tmpsql.replaceAll(userbase, userbases));
						}else{
							String[] tmpdbpres=userbases.split("`");
							for(int n=tmpdbpres.length-1;n>=0;n--){
								String tmpdbpre=tmpdbpres[n];
								if(tmpdbpre.length()==3){
									if(sb.length()>0){
										sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
									}else{
										sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
									}
								}
							}
						}
						strQuery = "select "+type+"(lexprData) as lexprData from (" + sb.toString()+") tt";
			         	//strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					}else if("2".equals(infokind)) {
                        strQuery = "select "+type+"("+field+") as lexprData " + strQuery+where;// + getUserMangerWheresql(userView,infokind);
                    } else if("3".equals(infokind)) {
                        strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
                    }
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						String tmp=rdata.get("lexprdata").toString();
						if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                            tmp="0";
                        }
						fieldValues[i] = Double.parseDouble(tmp);
					}
				}
			return fieldValues;
//		}
			
			
		/*} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询图例出错!");			
		}finally
		{
			//exeSql.freeConn();
	    }
		return null;*/
	}
	
	
	
	
	
	
	
	
	
	public double[] getLexprDataSformula(
			String userbase,
			int queryId,
			String sqlSelect,
			String username,
			String manageprive,
			UserView userView,
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn,String valvetype,ArrayList valve,String where) throws Exception {
			double[] fieldValues;
			//try {
				
				
				String userBase = userbase;
				String strFactor = "";
				String strLexpr = "";
				String strQuery = "";
				String flag="";
				//获得统计项
				StringBuffer sql =new StringBuffer();
				/*sql.append("select * from SName where id=");
				sql.append(queryId);
				List rs =ExecuteSQL.executeMyQuery(sql.toString());
				if (!rs.isEmpty()) {
					LazyDynaBean rec=(LazyDynaBean)rs.get(0);
					SNameDisplay = rec.get("name")!=null?rec.get("name").toString():"";
				}*/
				SformulaXml xml = new SformulaXml(conn,String.valueOf(queryId));
				Element element = xml.getElement(sformula);
				SNameDisplay=element.getAttributeValue("title");
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				sql.append(" order by norder");          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
//				int lenght = 1;
				String newInfo = infokind;//add by wangchaoqun on 2014-9-25 保存info的值便于后面的判断
				if("1,2,3".equals(newInfo)){
					infokind = currInfoKind(queryId);// add by liubq 2015-09-28 当infokind为"1,2,3"时,获取统计的infokind（1人,2单,3岗）
//					lenght = 3;
				}
//				for(int m=1;m<=lenght;m++){
//					if("1,2,3".equals(newInfo)){
//						infokind = String.valueOf(m);
//					}
					for (int i = 0; i < dataset.size(); i++) {
						LazyDynaBean rec=(LazyDynaBean)dataset.get(i);
						strLexpr = rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"";
						strFactor = rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"";
						flag=rec.get("flag")!=null?rec.get("flag").toString():"";
						//System.out.println(strFactor);
						display[i] = rec.get("legend")!=null?rec.get("legend").toString():"";
						norder_display[i]=rec.get("norder")!=null?rec.get("norder").toString():"";
						//根据表达式和因子生成统计的sql语句
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						//if("2".equalsIgnoreCase(preresult)&&commlexpr!=null && commfactor!=null)
						if(commlexpr!=null && commfactor!=null)
						{
							String[] style=getCombinLexprFactor(strLexpr,strFactor,commlexpr,commfactor);
						    if(style!=null && style.length==2)
						    {
						    	strLexpr=style[0];
						    	strFactor=style[1];
						    }
						}
						//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
						boolean ishavehistory=false;
						if("1".equals(flag)) {
                            ishavehistory=true;
                        } else if("1".equals(history)) {
                            ishavehistory=true;
                        }
								strQuery =cond.getCondQueryString(
								strLexpr,
								strFactor,
								userBase,
								ishavehistory,
								username,
								sqlSelect,userView,infokind,bresult);
						//cat.debug("---->sql======" + strQuery);
								//System.out.println(strQuery);
						if(this.getWhereIN()!=null&&this.getWhereIN().length()>0)
						{
							strQuery=strQuery+" and "+this.getWhereIN();
						}	
						strQuery = strQuery.toUpperCase();
						String type=element.getAttributeValue("type");
						String expr=(String)valve.get(0);
						ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
								Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
						YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),infoKindToInfoGroup(infokind),"Ht",userbase);
						yp.setCon(conn);
                        yp.setTempTableName("temp_"+username + queryId);
						yp.run(expr);
						
						String field = yp.getSQL();
						ArrayList usedsets = yp.getUsedSets();
						String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
						/**
						 * 【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列明白“A0100”不明确”
						 *  新增lsql，根据设置的统计条件获取数据
						 *  jingq add 2015.04.16
						 */
						FactorList factorlist= new FactorList(strLexpr,strFactor,userbase,ishavehistory,true,bresult,Integer.parseInt(infokind),username);
				        String lsql = factorlist.getSqlExpression();
				        lsql = lsql.substring(lsql.indexOf("WHERE")+5);
				        
						for(int n=0;n<usedsets.size();n++){
							String set = (String)usedsets.get(n);
							if("1".equals(infokind)){
								set = (" "+userbase+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(basesql.indexOf(set)==-1){
									sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON "+userbase+"A01.A0100="+set+".A0100");
									sb.append(basesql.substring(basesql.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".A0100="+userbase+"A01.A0100 AND "+lsql+") OR "+set+".I9999 IS NULL)");
									basesql= sb.toString();
								}
							}else if("2".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON B01.B0110="+set+".B0110");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".B0110=B01.B0110) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}else if("3".equals(infokind)){
								set = (" "+set).toUpperCase();
								StringBuffer sb = new StringBuffer();
								if(strQuery.indexOf(set)==-1){
									sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
									sb.append(" LEFT JOIN "+set+" ON K01.E01A1="+set+".E01A1");
									sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
									sb.append(" AND ("+set+".I9999=(select max(I9999) from "+set+" WHERE "+set+".E01A1=K01.E01A1) OR "+set+".I9999 IS NULL)");
									strQuery= sb.toString();
								}
							}
						}
						
						// WJH　２０１３－４－１６　　处理ＢＵＧ：统计函数时算不出来
						if ((field.indexOf("SELECT_") >= 0) && !"".equals(yp.getTempTableName())) {
							StringBuffer sb = new StringBuffer();
							if("1".equals(infokind)){
								sb.append(basesql.substring(0,basesql.indexOf(" WHERE")));
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON " + userbase+"A01.A0100="+yp.getTempTableName()+".A0100");
								sb.append(basesql.substring(basesql.indexOf(" WHERE")));
								basesql= sb.toString();
							} else  if("2".equals(infokind)) {
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.B0110="+yp.getTempTableName()+".B0110");
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								strQuery= sb.toString();
							} else if("3".equals(infokind)) {
								sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
								//sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");/*liuy 2014-12-23 注释*/	
								sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".E01A1");	
								sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
								strQuery= sb.toString();
							}
						}
						
						//System.out.println(strQuery);
						if("1".equals(infokind)){
							StringBuffer sb = new StringBuffer();//【8725】员工管理-常用统计-通过设置统计方式，想要实现求出一月份的工资总和，现在前台统计不出来，后台报“列名称“A0100”不明确”  jingq upd 2015.04.15
							String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select "+userbase.toUpperCase()+"A01.a0100 "+strQuery+")").toUpperCase();
							if(userbases.indexOf("`")==-1){
								sb.append(tmpsql.replaceAll(userbase, userbases));
							}else{
								String[] tmpdbpres=userbases.split("`");
								for(int n=tmpdbpres.length-1;n>=0;n--){
									String tmpdbpre=tmpdbpres[n];
									if(tmpdbpre.length()==3){
										if(sb.length()>0){
											sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre));
										}else{
											sb.append(tmpsql.replaceAll(userbase, tmpdbpre));
										}
									}
								}
							}
							strQuery = "select "+type+"(lexprData) as lexprData from (" + sb.toString()+") tt";
				         	//strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
						}else if("2".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery+where;// + getUserMangerWheresql(userView,infokind);
                        } else if("3".equals(infokind)) {
                            strQuery = "select "+type+"("+field+") as lexprData " + strQuery+where;// + getUserMangerWheresql(userView,infokind);
                        }
						//System.out.println(strQuery);
						List rsset = ExecuteSQL.executeMyQuery(strQuery);
						if (rsset != null && rsset.size()>0) {
							//保存该图例的统计数
							LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
							String tmp=rdata.get("lexprdata").toString();
							if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0) {
                                tmp="0";
                            }
							fieldValues[i] = Double.parseDouble(tmp);
						}
					}
				return fieldValues;
//			}
				
				
			/*} catch (Exception e) {
				e.printStackTrace();
				System.out.println("查询图例出错!");			
			}finally
			{
				//exeSql.freeConn();
		    }
			return null;*/
		}
	/**
	 * 获取统计多年数据的年份条件
	 * @param strFactor 查询条件
	 * @param year 起始年份
	 * @param years 多少年的数据  =futuren： 未来n年的数据| =lastn：过去n年的数据
	 * @return
	 */
    private ArrayList<String> getYearList(String strFactor, String year, String years) {
        ArrayList<String> yearList = new ArrayList<String>();
        try {
            if (years.startsWith("future")) {
                int countYear = Integer.valueOf(years.substring(6));
                for (int i = 0; i < countYear; i++) {
                    int futureYear = Integer.valueOf(year) + i;
                    yearList.add(getWhere(strFactor, futureYear + ""));
                }

            } else if (years.startsWith("last")) {
                int countYear = Integer.valueOf(years.substring(4));
                for (int i = countYear - 1; i >= 0; i--) {
                    int lastYear = Integer.valueOf(year) - i;
                    yearList.add(getWhere(strFactor, lastYear + ""));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yearList;
    }
    /**
     * 获取统计多年数据的统计图下显示的每年的年份
     * @param year 起始年度
     * @param years 统计多少年的数据 =futuren：未来n年 | =lastn：过去n年
     * @return
     */
    public String[] getYearDisplay(int year, String years) {
        String[] yearDisplay = null;
        try {
            if (years.startsWith("future")) {
                int countYear = Integer.valueOf(years.substring(6));
                yearDisplay = new String[countYear];
                for (int i = 0; i < countYear; i++) {
                    yearDisplay[i] = (year + i) + "";
                }

            } else if (years.startsWith("last")) {
                int countYear = Integer.valueOf(years.substring(4));
                yearDisplay = new String[countYear];
                for (int i = countYear - 1; i >= 0; i--) {
                    yearDisplay[countYear - 1 - i] = (year - i) + "";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yearDisplay;
    }
    
    private String getArchive_set(int id, Connection conn, String year) {
        String where = "";
        RowSet rs = null;
        try {
            if(id == 0) {
                return "";
            }
                
            String sql = " select Archive_set from sname where id ="+id;
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql);
            if(rs.next()){
                String ArchiveSet = rs.getString("Archive_set");
                if(ArchiveSet != null && ArchiveSet.length() > 0) {
                    where = " and " + Sql_switcher.year(ArchiveSet + "." + ArchiveSet +"z0") + "=" + year;
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return where;
    }
}
