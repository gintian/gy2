/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.mobileapp.statis;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
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
import java.util.*;

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
	private String[] display;
	private String[] norder_display;
    private int recordCount = 0;
	private List verticalArray=new ArrayList();
	private List horizonArray=new ArrayList();
	private String SNameDisplay = "";
	private int totalValue = 0;
	private double totalValues=0;
	private String whereIN;
	
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
			String infokind,boolean bresult) throws GeneralException {               
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
			    	strQuery =getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind))
			         	strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct K01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
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
		           throw GeneralExceptionHandler.Handle(e);
			}
		}
	public int[] getLexprData(	
			ArrayList conditionlist,      /*name|表达式|因子*/
			boolean ishavehistory,         /*历史纪录*/
			UserView userView,            
			String pre,                     /*人员库*/
			String infokind,boolean bresult,String userbases) throws GeneralException {               
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
			    	strQuery =getCondQueryString(
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
			    	}else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct K01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
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
		           throw GeneralExceptionHandler.Handle(e);
			}
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
			    	strQuery =getCondQueryString(
							strLexpr,
							strFactor,
							pre,
							ishavehistory,
							userView.getUserName(),
							scopeQuery,userView,infokind,bresult,blike);
			    	cat.debug("---->sql======" + strQuery);
			    	if("1".equals(infokind))
			         	strQuery = "select count(distinct " + pre + "a01.a0100) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
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
			String infokind,boolean bresult,String scopeQuery,boolean blike,String userbases) throws GeneralException {               
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
			    	strQuery =getCondQueryString(
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
			    	}else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery ;//+ getUserMangerWheresql(userView,infokind);
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
			}
		}

	/*拆分标题,表达式和因子*/
	private ArrayList getConditionItem(String conditem)
	{
		ArrayList items=new ArrayList();
		StringTokenizer Stok = new StringTokenizer(conditem, "|");
		if(Stok.hasMoreTokens())
		 items.add(Stok.nextToken());
		if(Stok.hasMoreTokens())
	     items.add(Stok.nextToken());
		if(Stok.hasMoreTokens())
	     items.add(Stok.nextToken());
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
			List dataset = ExecuteSQL.executeMyQuery(sql.toString());
			fieldValues = new int[dataset.size()];
			display = new String[dataset.size()];
			norder_display=new String[dataset.size()];
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
				if("1".equals(flag))
					ishavehistory=true;
				else if("1".equals(history))
					ishavehistory=true;
						strQuery =getCondQueryString(
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
				if("1".equals(infokind))
		         	strQuery = "select count(distinct " + userbase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
		    	else if("2".equals(infokind))
		    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
		    	else if("3".equals(infokind))
		    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
				//System.out.println(strQuery);
				List rsset = ExecuteSQL.executeMyQuery(strQuery);
				if (rsset != null && rsset.size()>0) {
					//保存该图例的统计数
					LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
					fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
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
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
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
					if("1".equals(flag))
						ishavehistory=true;
					else if("1".equals(history))
						ishavehistory=true;
							strQuery =getCondQueryString(
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
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userbase);
					yp.setCon(conn);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					
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
					
					//System.out.println(strQuery);
					if("1".equals(infokind))
			         	strQuery = "select "+type+"("+field+") as lexprData " + basesql+" and "+userbase+"a01.a0100 in(select a0100 "+ strQuery+")";// + getUserMangerWheresql(userView,infokind);
			    	else if("2".equals(infokind))
			    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						String tmp=rdata.get("lexprdata").toString();
						if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
							tmp="0";
						fieldValues[i] = Double.parseDouble(tmp);
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
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new int[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
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
					if("1".equals(flag))
						ishavehistory=true;
					else if("1".equals(history))
						ishavehistory=true;
							strQuery = getCondQueryString(
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
					}else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						fieldValues[i] = Integer.parseInt(rdata.get("lexprdata").toString());
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
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from SLegend where id=");
				sql.append(queryId);          
				List dataset = ExecuteSQL.executeMyQuery(sql.toString());
				fieldValues = new double[dataset.size()];
				display = new String[dataset.size()];
				norder_display=new String[dataset.size()];
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
					if("1".equals(flag))
						ishavehistory=true;
					else if("1".equals(history))
						ishavehistory=true;
							strQuery =getCondQueryString(
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
					YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userbase);
					yp.setCon(conn);
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					String basesql=" FROM "+userbase.toUpperCase()+"A01 WHERE 1=1";
					for(int n=0;n<usedsets.size();n++){
						String set = (String)usedsets.get(n);
						if("1".equals(infokind)){
							set = (" "+userbase+set).toUpperCase();
							StringBuffer sb = new StringBuffer();
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
							sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON B01.A0100="+yp.getTempTableName()+".A0100");
							sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
							strQuery= sb.toString();
						} else if("3".equals(infokind)) {
							sb.append(strQuery.substring(0,strQuery.indexOf(" WHERE")));
							sb.append(" LEFT JOIN " + yp.getTempTableName() + " ON K01.E01A1="+yp.getTempTableName()+".A0100");	
							sb.append(strQuery.substring(strQuery.indexOf(" WHERE")));
							strQuery= sb.toString();
						}
					}
					
					//System.out.println(strQuery);
					if("1".equals(infokind)){
						StringBuffer sb = new StringBuffer();
						String tmpsql = ("select "+field+" as lexprData" + basesql+" and "+userbase+"A01.a0100 in(select a0100 "+strQuery+")").toUpperCase();
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
					}else if("2".equals(infokind))
			    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery);
					if (rsset != null && rsset.size()>0) {
						//保存该图例的统计数
						LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
						String tmp=rdata.get("lexprdata").toString();
						if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
							tmp="0";
						fieldValues[i] = Double.parseDouble(tmp);
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
					sbase=rec.get("sbase")!=null?rec.get("sbase").toString():"";
					if(sbase.endsWith(","))
						sbase=sbase.substring(0,sbase.length()-1);
					sbase=filterPrivDB(sbase,userView);
					if(sbase.length()==0)
						sbase="#";
				}
				//获得各个统计项的图例
				sql.delete(0,sql.length());
				sql.append("select * from hr_hisdata_SLegend where id=");
				sql.append(queryId);          
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
							strQuery =getCondQueryString(
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
						strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n);
					}
					strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase()+"\\.", "heh.");
					strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
					strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase(), "hr_emp_hisdata");
					//strQuery=strQuery.replaceAll("FROM", "FROM hr_emp_hisdata ");
					//System.out.println(strQuery);
					if("1".equals(infokind))
			         	strQuery = selectsql.toString()+" from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery;
			    	else if("2".equals(infokind))
			    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
			    	else if("3".equals(infokind))
			    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					//System.out.println(strQuery);
					List rsset = ExecuteSQL.executeMyQuery(strQuery+" and nbase in('"+sbase.replace(",","','")+"')");
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
					if(sbase.endsWith(","))
						sbase=sbase.substring(0,sbase.length()-1);
					sbase=this.filterPrivDB(sbase, userView);
					if(sbase.length()==0)
						sbase="#";
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
								
								strQuery =getCondQueryString(
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
								size = fieldsetlist.size();
								for	(int n=0;n<size;n++){
									String fieldsetid = (String)fieldsetlist.get(n);
									strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase()+"\\.", "heh"+n+".");
									strQuery=strQuery.replaceAll((userbase+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n);
								}
								strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase()+"\\.", "heh.");
								strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
								strQuery=strQuery.replaceAll((userbase+"A01").toUpperCase(), "hr_emp_hisdata");
								//System.out.println(strQuery);
								if("1".equals(infokind))
						         	strQuery = selectsql.toString()+" from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery;
						    	else if("2".equals(infokind))
						    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
						    	else if("3".equals(infokind))
						    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
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
						if(n!=0)
							html.append("<tr>");
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
	
	public String creatExcel(String statname,String [][] fieldValues,String[] ths,String[] tvs,HashMap hsnameMap,HashMap hslegendMap,HashMap vsnameMap,HashMap vslegendMap,ArrayList hslegendlist,ArrayList vslegendlist,UserView userView) throws GeneralException {
		String excel_filename = userView.getUserName() + "ry_tj_123456.xls";
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet sheet = workbook.createSheet(statname);
			HSSFRow row = null;
			HSSFCell csCell = null;
			short nn = 0;

			int allcolumns = fieldValues[0].length;

			for (int i = 0; i < allcolumns + 2; i++) {
				sheet.setColumnWidth(i, 5000);
			}
//		 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (allcolumns + 1));
			row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);
			csCell = row.getCell(0);
			if (csCell == null)
				csCell = row.createCell(0);
			csCell.setCellValue(statname);
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			csCell.setCellStyle(cellStyle);
			ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 2, (short) (1));

			HSSFCellStyle cellStyle1 = workbook.createCellStyle();
			cellStyle1.setAlignment(HorizontalAlignment.CENTER);

			HSSFCellStyle cellStyle2 = workbook.createCellStyle();
			cellStyle2.setFont(font);
			cellStyle2.setAlignment(HorizontalAlignment.CENTER);
			cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);

			nn++;
			row = sheet.getRow(nn);
			if (row == null)
				row = sheet.createRow(nn);
			StringBuffer html = new StringBuffer();
			int sx = 1, sy = 2, ex = 1, ey = 2;
			for (int i = 0; i < ths.length; i++) {
				String name = (String) hsnameMap.get(ths[i]);
				ArrayList legendlist = (ArrayList) hslegendMap.get(ths[i]);
				int gg = legendlist.size();
				ey = ey + gg - 1;
				ExportExcelUtil.mergeCell(sheet, sx, (short) sy, ex, (short) (ey));
				csCell = row.getCell(sy);
				if (csCell == null)
					csCell = row.createCell(sy);
				csCell.setCellValue(name);
				csCell.setCellStyle(cellStyle);
				ey++;
				sy = ey;
			}
			nn++;
			row = sheet.getRow(nn);
			if (row == null)
				row = sheet.createRow(nn);
			int index = 0;
			for (int i = 0; i < ths.length; i++) {
				ArrayList legendlist = (ArrayList) hslegendMap.get(ths[i]);
				for (int n = 0; n < legendlist.size(); n++) {
					HashMap map = (HashMap) legendlist.get(n);
					csCell = row.getCell(index + 2);
					if (csCell == null)
						csCell = row.createCell(index + 2);
					csCell.setCellValue((String) map.get("legend"));
					csCell.setCellStyle(cellStyle);
					index++;
				}
			}

			index = 0;
			sx = 3;
			ex = 3;
			nn++;
			for (int i = 0; i < tvs.length; i++) {
				String name = (String) vsnameMap.get(tvs[i]);
				ArrayList legendlist = (ArrayList) vslegendMap.get(tvs[i]);
				int gg = legendlist.size();
				for (int n = 0; n < gg; n++) {
					row = sheet.getRow(nn + index);
					if (row == null)
						row = sheet.createRow(nn + index);

					HashMap map = (HashMap) legendlist.get(n);
					if (n == 0) {
						ex += gg - 1;
						ExportExcelUtil.mergeCell(sheet, sx, (short) 0, ex, (short) 0);
						csCell = row.getCell(0);
						if (csCell == null)
							csCell = row.createCell(0);
						csCell.setCellValue(name);
						csCell.setCellStyle(cellStyle2);
						ex++;
						sx = ex;
					}
					csCell = row.getCell(1);
					if (csCell == null)
						csCell = row.createCell(1);
					csCell.setCellValue((String) map.get("legend"));
					csCell.setCellStyle(cellStyle);
					HashMap legendmap = (HashMap) vslegendlist.get(index);
					for (int m = 0; m < fieldValues[index].length; m++) {
						HashMap hlegendmap = (HashMap) hslegendlist.get(m);
						csCell = row.getCell(m + 2);
						if (csCell == null)
							csCell = row.createCell(m + 2);
						csCell.setCellValue(fieldValues[index][m] == null ? "0" : fieldValues[index][m]);
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
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
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
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
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

			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + excel_filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws Exception {
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
					//System.out.println("====strLexpr ----->" + strLexpr +  " ==== strFactor---->" + strFactor);
					String userBase = "";
					for(int r=0;r<dblist.size();r++)
					{
						userBase=(String)dblist.get(r);
						if(userBase==null||userBase.length()<=0)
							continue;
						boolean ishavehistory=false;
						if("1".equals(flag))
							ishavehistory=true;
						else if("1".equals(history))
							ishavehistory=true;
								strQuery =getCondQueryString(
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
						if("1".equals(infokind))
				         	strQuery = "select count(distinct " + userBase + "a01.a0100) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
				    	else if("2".equals(infokind))
				    		strQuery = "select count(distinct b01.b0110) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
				    	else if("3".equals(infokind))
				    		strQuery = "select count(distinct k01.e01a1) as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
						//System.out.println(strQuery);
						List rsset = ExecuteSQL.executeMyQuery(strQuery);
						if (rsset != null && rsset.size()>0) {
							//保存该图例的统计数
							LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
							fieldValues[i] = fieldValues[i]+Integer.parseInt(rdata.get("lexprdata").toString());
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn) throws Exception {
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
							if("1".equals(flag))
								ishavehistory=true;
							else if("1".equals(history))
								ishavehistory=true;
									strQuery =getCondQueryString(
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
							YksjParser yp = new YksjParser(userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",userBase);
							yp.setCon(conn);
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
							}else if("2".equals(infokind))
					    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
					    	else if("3".equals(infokind))
					    		strQuery = "select "+type+"("+field+") as lexprData " + strQuery;// + getUserMangerWheresql(userView,infokind);
							//System.out.println(strQuery);
							List rsset = ExecuteSQL.executeMyQuery(strQuery);
							if (rsset != null && rsset.size()>0) {
								//保存该图例的统计数
								LazyDynaBean rdata=(LazyDynaBean)rsset.get(0);
								String tmp=rdata.get("lexprdata").toString();
								if(tmp==null||tmp.length()==0|| "null".equalsIgnoreCase(tmp))
									tmp="0";
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
	
	//获得权限的sql语句的条件
	private String getUserMangerWheresql(UserView userview,String infokind)
	{
		String tablename="";
		if("1".equals(infokind))
			tablename="";
		else if("2".equalsIgnoreCase(infokind))
			tablename="B01";
		else if("3".equalsIgnoreCase(infokind))
			tablename="K01";
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
		String infokind,boolean bresult) throws GeneralException {
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
						getCondQueryString(
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
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			//excSql.freeConn();	
		}
	}
	//根据子集获得所对应的各个属性列
	public String getTableFieldSet(String usertype) throws GeneralException {
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
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
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
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws GeneralException {
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
				if (doubleLexr != null)
					HVLexr = doubleLexr.split(",");
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0];
					String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
									strHV=getCondQueryString(
										strLexpr,
										strFactor,
										userBase,
										ishavehistory,
										username,
										sqlSelect,
										userView,infokind,bresult);
								cat.debug("------>sql====="+strHV);
								if(this.whereIN!=null&&this.whereIN.length()>0)
									strHV=strHV+" and "+this.getWhereIN();
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
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
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
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
									strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										//totalValue += dataValues[i][j];
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValue += dataValues[i][j];
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn) throws GeneralException {
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
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
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
									}else
										strHV =	"select "+type+"("+field+") as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String sformula,Connection conn,String vtotal,String htotal) throws GeneralException {
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
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
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
									}else
										strHV =	"select "+type+"("+field+") as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
										dataValues[i][j] = Double.parseDouble(tmp);
										//totalValues += dataValues[i][j];
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValues += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValues += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValues += dataValues[i][j];
										}else{
											totalValues += dataValues[i][j];
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases) throws GeneralException {
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
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
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
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
										dataValues[i][j] = Integer.parseInt(tmp);
										if(j<dataValues.length-1&&i<dataValues[i].length-1)
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String vtotal,String htotal) throws GeneralException {
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
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
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
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										String tmp=rscountc.get("recordcount").toString();
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
										dataValues[i][j] = Integer.parseInt(tmp);
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValue += dataValues[i][j];
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

				}

			} catch (Exception e) {
				System.out.println("生成二维的数据出错!");
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn) throws GeneralException {
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
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
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
									}else
										tmpsql = ("select "+field+" as recordCount" + strHV).toUpperCase();
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
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history,String userbases,String sformula,Connection conn,String vtotal,String htotal) throws GeneralException {
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
					yp.run(expr);
					
					String field = yp.getSQL();
					ArrayList usedsets = yp.getUsedSets();
					//SNameDisplay = rec.get("name")!=null?rec.get("name").toString().trim():"";
					String doubleLexr = rec.get("hv")!=null?rec.get("hv").toString().trim():"";
					if (doubleLexr != null)
						HVLexr = doubleLexr.split(",");
					if (HVLexr[0] != null
						&& HVLexr[1] != null
						&& !"x".equals(HVLexr[0])
						&& !"x".equals(HVLexr[1])) {
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
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
									}else
										tmpsql = ("select "+field+" as recordCount" + strHV).toUpperCase();
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
										if(tmp==null||"null".equalsIgnoreCase(tmp)||tmp.length()==0)
											tmp="0";
										dataValues[i][j] = Double.parseDouble(tmp);
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValues += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValues += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValues += dataValues[i][j];
										}else{
											totalValues += dataValues[i][j];
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws GeneralException {
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
				if (doubleLexr != null)
					HVLexr = doubleLexr.split(",");
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0];
					String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
									if(userBase==null||userBase.length()<=0)
										continue;
									strHV=getCondQueryString(
											strLexpr,
											strFactor,
											userBase,
											ishavehistory,
											username,
											sqlSelect,
											userView,infokind,bresult);
									cat.debug("------>sql====="+strHV);
									if(this.whereIN!=null&&this.whereIN.length()>0)
										strHV=strHV+" and "+this.getWhereIN();
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
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult) throws GeneralException {
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
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
				 					 strHV=getCondQueryString(
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String vtotal,String htotal) throws GeneralException {
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
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
				 					 strHV=getCondQueryString(
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
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValue += dataValues[i][j];
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String userbases) throws GeneralException {
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
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
				 					 strHV=getCondQueryString(
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult,String userbases,String vtotal,String htotal) throws GeneralException {
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
						String sqlV = "select * from SLegend where id=" + HVLexr[0];
						String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
				 					 strHV=getCondQueryString(
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
									
									//strHV =	"select count(*) as recordCount " + strHV;//  + getUserMangerWheresql(userView,infokind);
									List rscount = ExecuteSQL.executeMyQuery(strHV);
									if (rscount != null && rscount.size()>0) {
										LazyDynaBean rscountc=(LazyDynaBean)rscount.get(0);
										dataValues[i][j] = Integer.parseInt(rscountc.get("recordcount").toString());
										//totalValue += dataValues[i][j];
										if("1".equals(vtotal)&&"1".equals(htotal)){
											if(j<rsH.size()-1&&i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(vtotal)){
											if(i<rsV.size()-1)
												totalValue += dataValues[i][j];
										}else if("1".equals(htotal)){
											if(j<rsH.size()-1)
												totalValue += dataValues[i][j];
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws GeneralException {
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
				if (doubleLexr != null)
					HVLexr = doubleLexr.split(",");
				if (HVLexr[0] != null
					&& HVLexr[1] != null
					&& !"x".equals(HVLexr[0])
					&& !"x".equals(HVLexr[1])) {
					String sqlV = "select * from SLegend where id=" + HVLexr[0];
					String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV =getCondQueryString(
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
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
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
			String infokind,boolean bresult) throws GeneralException {
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
					String sqlV = "select * from SLegend where id=" + HVLexr[0];
					String sqlH = "select * from SLegend where id=" + HVLexr[1];
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
										strHV =getCondQueryString(
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
				 e.printStackTrace();
		           throw GeneralExceptionHandler.Handle(e);
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
		String infokind,boolean bresult,String commlexpr,String commfactor,String preresult,String history) throws GeneralException {
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
			//System.out.println("获得二维的纪录数出错!");
			 e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e);
		}
		finally{
		}
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
						|| "a0000".equalsIgnoreCase(itemid))
					continue;
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
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		da=new CommonData("12",ResourceFactory.getProperty("static.figure.vertical_bar_3d"));
		chartTypeList.add(da);
		da=new CommonData("5",ResourceFactory.getProperty("static.figure.pie_3d"));
		chartTypeList.add(da);
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
		if ("D".equals(fieldtype))
			varType = YksjParser.DATEVALUE;
		else if ("A".equals(fieldtype) || "M".equals(fieldtype))
			varType = YksjParser.STRVALUE;
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
		if(sb.length()>0)
			return sb.substring(1);
		else
			return "";
	}
	private String delI9999(String str){
		int index=0;
		int f=0;
		str=str.replaceAll("I9999 IS NULL", "1=2");
		while(f<20&&(index=str.indexOf("AND (I9999"))>5){
			str=str.substring(0,index)+str.substring(index+69);
			index=str.indexOf("AND (I9999");
			f++;
		}
		while(f<20&&(index=str.indexOf("AND I9999"))>5){
			str=str.substring(0,index)+str.substring(index+66);
			index=str.indexOf("AND I9999");
			f++;
		}
		return str;
	}
	public String getCondQueryString(String lexpr,String strFactor,String userbase,boolean ishistory,String username,String scopeQuery,UserView userView,String infokind,boolean bresult)
    throws GeneralException{
        strFactor=strFactor.trim();
        strFactor = PubFunc.keyWord_reback(strFactor);
        if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)&&"1".equalsIgnoreCase(infokind)) {
            String[] scopeField = scopeQuery.split(",");
            if (scopeField.length > 0) {
                ArrayList lexprFactor=new ArrayList();
                lexprFactor.add(getScopeFactor(scopeField));
                lexprFactor.add(lexpr + "|" + strFactor);
                CombineFactor combinefactor=new CombineFactor();
                String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
                StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
                if(Stok.hasMoreTokens())
                {
                    lexpr=Stok.nextToken();
                    strFactor=Stok.nextToken();
                }
            }
        }       
        String strwhere="";
        ArrayList fieldlist=new ArrayList();
         /**非超级用户且对人员库进行查询*/
        cat.debug("-----infokind----->" + infokind);
        if("1".equals(infokind))
        {
            
        }else if("2".equals(infokind))
            userbase="B";
        else if("3".equals(infokind))
            userbase="K";
       /*if((!userView.isSuper_admin()))
        {
            strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,bresult,fieldlist);
        }
        else
        {                    
           FactorList factorlist=new FactorList(lexpr,strFactor,userbase,ishistory,true,bresult,Integer.parseInt(infokind),username);
           strwhere=factorlist.getSqlExpression();
        }*/
        //System.out.println(lexpr+"|"+strFactor+"---"+userbase+"---"+ishistory+"---"+false+"---"+bresult+"---"+fieldlist);
        
        strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,false,bresult,fieldlist);
        if(strwhere!=null&&strwhere.length()>0)
        {
            if("2".equals(infokind))
            {
                String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
                strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=b01.b0110";
                if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
                    strwhere +=" and codeitemid like '"+scopeQuery.substring(2)+"%'";
                }
                strwhere +=")";
            }else if("3".equals(infokind))
            {
                String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
                strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=k01.e01a1";
                if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
                    strwhere +=" and codeitemid like '"+scopeQuery.substring(2)+"%'";
                }
                strwhere +=")";
            }
        }
        return strwhere;
    }
	public String getScopeFactor(String[] scopeField) {
        String codesetidstr = "";
        String codeitemidstr = "";
        String lexprstr="";
        String strfactor="";
        String result;
        for (int i = 0,j=1; i < scopeField.length; i++,j++) {
            if (scopeField[i] != null && scopeField[i].length() > 2) {
                codesetidstr = scopeField[i].substring(0, 2);
                codeitemidstr = scopeField[i].substring(2);
                if(i!=0)
                    lexprstr+="+" + j;
                else 
                    lexprstr="1";
                if ("UN".equals(codesetidstr)) {
                    strfactor+="B0110=" + codeitemidstr + "%`";         
                }else if("UM".equals(codesetidstr)) {
                    strfactor+="E0122=" + codeitemidstr + "%`"; 
                }else{
                    strfactor+="E01A1=" + codeitemidstr + "%`"; 
                }               
              }
            }
          result=lexprstr +"|" + strfactor;
       return result;   
    }
	/*带模糊查询的*/
    public String getCondQueryString(String lexpr,String strFactor,String userbase,boolean ishistory,String username,String scopeQuery,UserView userView,String infokind,boolean bresult,boolean blike)
    throws GeneralException{
        strFactor=strFactor.trim();
        if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
            String[] scopeField = scopeQuery.split(",");
            if (scopeField.length > 0) {
                ArrayList lexprFactor=new ArrayList();
                lexprFactor.add(lexpr + "|" + strFactor);
                lexprFactor.add(getScopeFactor(scopeField));
                CombineFactor combinefactor=new CombineFactor();
                String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
                StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
                if(Stok.hasMoreTokens())
                {
                    lexpr=Stok.nextToken();
                    strFactor=Stok.nextToken();
                }
            }
        }
        String strwhere="";
        ArrayList fieldlist=new ArrayList();
         /**非超级用户且对人员库进行查询*/
       try
       {
//         if(userView.getStatus()!=0)
//             bresult=true; 
           strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,blike,bresult,fieldlist);
           if(strwhere!=null&&strwhere.length()>0)
            {
                if("B".equalsIgnoreCase(userbase))
                {
                    String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
                    strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=b01.b0110)";
                }else if("K".equalsIgnoreCase(userbase))
                {
                    String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
                    strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=k01.e01a1)";
                }
            }
       }catch(Exception e)
       {
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
       }
       return strwhere;
    }
}
