package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ColCheckAnalyse;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.RowCheckAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * <p>Title:表内校验：行/列</p>
 * <p>Description:涉及到 自动生成/编辑报表/报表汇总 三模块 tb tt_ 表
 * 	自动生成模块表内校验是针对自动取数后的生成的统计结果表（tb+报表ID）操作
 *  报表编辑中
 *  报表汇总中
 * 
 * </p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0 
 */
public class ReportInnerCheck {
	
	
	private String rtabid;    //报表ID
	private Connection conn;  //数据库连接
	private TnameBo tbo;      //报表封装类
	private HashMap rowMap;	  //通过secid获得行号的集合
	private HashMap colMap;	  //通过secid获得行号的集合
	private int[][] digitalResults = null; //小数点	
	
	//用于确定操作的是哪个表和那些数据
	private String reportPrefix; //报表前缀：tb/tt
	private String whereSQL ;
	
	
	/**
	 * 表内校验
	 * @param conn       DB连接
	 * @param tabid      报表表号 
	 * @param reportFlag 报表标识：1 tb  /  2 tt_
	 * @param sqlFlag    sql语句的where部分
	 * @param sqlFlag    用户名或填报单位编码（username / unitcode）
	 */
	public ReportInnerCheck(Connection conn , String tabid ,int reportFlag ,String sqlFlag){
		this.conn = conn;
		this.rtabid = tabid;
		this.tbo = new TnameBo(this.conn,tabid);
		this.rowMap = tbo.getRowMap();//原来tbo为this 后来改表内校验一致时改的  不知道当初是否有特殊用处  zhaoxg 2013-4-17
		this.colMap = tbo.getColMap();
		this.digitalResults = this.tbo.getDigitalResults();
		if(reportFlag == 1){
			this.reportPrefix = "tb";
			this.whereSQL = " and username = '" + sqlFlag +"' ";
		}else if(reportFlag == 2){
			this.reportPrefix = "tt_";
			this.whereSQL = " and unitcode = '" + sqlFlag + "' ";
		}
	}
	
	/**
	 * 判断统计结果表是否存在
	 * @param tabid  报表ID
	 * @return
	 */
	public boolean isExistTable(String reportPrefix ,String tabid ){
		boolean b = false;
		DbWizard dbWizard=new DbWizard(this.conn);
		Table table=new Table(reportPrefix+tabid);
		if(dbWizard.isExistTable(table.getName(),false)){
			b= true;
		}
		return b;
	}
  
  
	
	/**
	 * 表内校验
	 * @return 错误信息结果输出到JSP
	 * @throws GeneralException
	 */
	public String reportInnerCheck() throws GeneralException{
		StringBuffer reportInnerCheckValues = new StringBuffer();
		
		//判断报表是否生成统计结果表即tb+报表ID
		if(this.isExistTable(this.reportPrefix,this.rtabid)){			
			//行校验-分析一列中的数据 rowchk表中行校验公式
			reportInnerCheckValues.append(this.rowCheck());			
			//列校验-分析一行中的数据 colchk表中的列校验公式
			reportInnerCheckValues.append(this.colCheck());	
		}else{
			reportInnerCheckValues.append("&nbsp;&nbsp;&nbsp;&nbsp;不能打开此表，是否未生成统计结果！<br>");
		}	
		return reportInnerCheckValues.toString();
	}
	
	
	
	
	/**
	 * 列校验-分析行
	 * 例如：1=2+3排除1,2行
	 * @return
	 * @throws GeneralException
	 */
	public String colCheck() throws GeneralException{
		/*
		 * 列校验公式中的运算符
		 * 0，等于 1，大于 2，小于 3，不等于 4，大于等于 5，小于等于
		*/
		
		StringBuffer colCheckValues = new StringBuffer();
		
		StringBuffer sql = new StringBuffer();
		
		//查找特定报表的列校验公式
		sql.append("select * from colchk where tabid = ");
		sql.append(tbo.getTabid());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			
			//遍例列校验公式
			while(rs.next()){
				
				//原始表达式
				String le = rs.getString("lexpr");
				String o = String.valueOf(rs.getInt("opsign"));
				String re = rs.getString("rexpr");
				
				//列校验处理类
				ColCheckAnalyse cca = new ColCheckAnalyse(le , o , re , tbo , this.reportPrefix ,this.whereSQL);
				
				//表达式描述信息
				String exprInfo = cca.getExprInfo();	
				
				//获得表达式SQL语句
				String colCheckSql = cca.getColCheckSQL();
				
				//语法错误
				if(colCheckSql == null || "".equals(colCheckSql)){
					//返回语法错误描述信息
					colCheckValues.append(exprInfo);
					String temp = "&nbsp;&nbsp;&nbsp;"
						+"<br>" + cca.getColCheckErrors() +"<br>";
					colCheckValues.append(temp);
				}else{
					//执行表达式SQL语句获得错误信息     SQL语句需要解决除零问题
					String colCheckValue = this.getColCheckValue(colCheckSql , le , o , re);
					//表达式正确
					if(colCheckValue == null || "".equals(colCheckValue)){
					}else{
						//错误
						colCheckValues.append(exprInfo);
						colCheckValues.append("<br>");
						colCheckValues.append(colCheckValue);
					}	
				}
				
			}//end while
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}
		return colCheckValues.toString();
		
	}
	

	/**
	 * 获得一个列校验公式分析结果--SQL运算
	 * @param sql       列校验处理SQL语句
	 * @param lexpr     左表达式
	 * @param operator  运算符  0，等于 1，大于 2，小于 3，不等于 4，大于等于 5，小于等于
	 * @param rexpr     右表达式
	 * @return          列校验错误信息
	 * @throws GeneralException
	 */
	public String getColCheckValue(String sql , String lexpr , String operator ,String rexpr) throws GeneralException{
		StringBuffer colCheckValue = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());			
			HashMap valueToKey=new HashMap();
			Set keySet=this.rowMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				String value=(String)this.rowMap.get(key);
				valueToKey.put(value,key);
			}
			while(rs.next()){//校验公式	
				//报表secid字段值
				int secid = rs.getInt("secid");
				//实际数据的行号
			//	String row = (String)this.rowMap.get(String.valueOf(secid-1));//zhaoxg 2013-5-8 排除甲行，直接获取数据行
				String row=(String)valueToKey.get(String.valueOf(secid-1));
				
				if(row==null || row.trim().length()<=0) {
                    continue;
                }
				// 校验行数和cs行数少一的问题，JinChunhai 2012.09.26
			/*
				String cspc = "false";
				if(row!=null && row.trim().length()>0 && row.equals(""+secid))
				{
					cspc = "true";
					row = secid-1+"";
				}
				*/
				
				String lExprValue ="";
				String rExprValue ="";
				//获得数据结果的小数位 即：表达式中小数位最大的哪个是整个表达式的小数位数
				if(ExprUtil.getExprNumbers(lexpr) != null){
					//int ln = this.getColCheckExprResultDigitaValue(Integer.parseInt(row) ,ExprUtil.getExprNumbers(lexpr));
					int ln = this.getColCheckExprResultDigitaValue(secid-1,ExprUtil.getExprNumbers(lexpr));
					lExprValue = this.getFormatExprValue(rs.getString("lexpr"),ln);
				}else{
					lExprValue = rs.getString("lexpr");
				}
				if(ExprUtil.getExprNumbers(rexpr)!= null){
					//int rn = this.getColCheckExprResultDigitaValue(Integer.parseInt(row) ,ExprUtil.getExprNumbers(rexpr));
					int rn = this.getColCheckExprResultDigitaValue(secid-1 ,ExprUtil.getExprNumbers(rexpr));
					rExprValue = this.getFormatExprValue(rs.getString("rexpr"),rn);
				}else{
					rExprValue = rs.getString("rexpr");	
				}
				
				//  解决精度不一致问题
				if(!getValueIsEquation(lExprValue,rExprValue,operator))
				{
				//JSP页面输出时显示的运算符
					String op =ExprUtil.getReverseOperator(ExprUtil.getOperator(operator));	
					
					// 校验行数和cs行数少一的问题，JinChunhai 2012.09.26
					/*
					if(cspc.equalsIgnoreCase("true"))
						row = Integer.parseInt(row)+1+"";
					*/
					String temp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
					ResourceFactory.getProperty("reportinnercheck.di") + row 
					+ResourceFactory.getProperty("reportinnercheck.row") +":"+ lExprValue + op + rExprValue;
					colCheckValue.append(temp);
					colCheckValue.append("<br>");
					
				}
			}//end while
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}
		
		return colCheckValue.toString();
	}
	
	
	//判断值是否一致
	public boolean getValueIsEquation(String lExprValue,String rExprValue,String operator)
	{
		//将左右计算值调为统一的精度
		int minScale=0;
		int l_temp=0;
		int r_temp=0;
		if(lExprValue.indexOf(".")!=-1) {
            l_temp=lExprValue.substring(lExprValue.indexOf(".")+1).length();
        }
		if(rExprValue.indexOf(".")!=-1) {
            r_temp=rExprValue.substring(rExprValue.indexOf(".")+1).length();
        }
		if(l_temp==0||r_temp==0) {
            minScale=0;
        } else
		{
			minScale=l_temp>r_temp?r_temp:l_temp;
		}
		lExprValue=PubFunc.round(lExprValue, minScale);
		rExprValue=PubFunc.round(rExprValue, minScale);
		
		
		double a_lexpr=Double.parseDouble(lExprValue);
		double a_rexpr=Double.parseDouble(rExprValue);
		boolean flag=false;
		if("0".equals(operator))
		{
			if(a_lexpr==a_rexpr) {
                flag=true;
            }
		}
		else if("1".equals(operator))
		{
			if(a_lexpr>a_rexpr) {
                flag=true;
            }
		}
		else if("2".equals(operator))
		{
			if(a_lexpr<a_rexpr) {
                flag=true;
            }
		}
		else if("3".equals(operator))
		{
			if(a_lexpr!=a_rexpr) {
                flag=true;
            }
		}
		else if("4".equals(operator))
		{
			if(a_lexpr>=a_rexpr) {
                flag=true;
            }
		}
		else if("5".equals(operator))
		{
			if(a_lexpr<=a_rexpr) {
                flag=true;
            }
		}
		return flag;
	}
	
	
	/**
	 * 行校验 -分析一列中的数据
	 * @return
	 * @throws GeneralException
	 */
	public String rowCheck() throws GeneralException{
		StringBuffer rowCheckValues = new StringBuffer();
		
		StringBuffer sql = new StringBuffer();
		//根据tabid得到行校验公式信息
		sql.append("select * from rowchk where tabid = ");
		sql.append(tbo.getTabid());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){//校验公式
				//原始的表达式
				String le = rs.getString("lexpr");
				String o = String.valueOf(rs.getInt("opsign"));
				String re = rs.getString("rexpr");
				
				//行分析类
				RowCheckAnalyse rca = new RowCheckAnalyse(le , o , re , tbo ,this.reportPrefix ,this.whereSQL);
				//行校验公式描述信息
				String exprInfo = rca.getExprInfo();
				
				//行校验中使用的SQL模板
				String rowchecksql = rca.getRowCheckSQL();
				
				if(rowchecksql == null || "".equals(rowchecksql)){
					//语法错误
					rowCheckValues.append(exprInfo);
					String temp = "&nbsp;&nbsp;&nbsp;<br>" + rca.getRowCheckErrors()+"<br>";
					rowCheckValues.append(temp);
				}else{
					//执行SQL得到SQL结果集  SQL语句需要解决除零
					String temp = this.getRowChekValue(rowchecksql,le,o ,re);
					if(temp == null || "".equals(temp)){
					}else{
						//错误
						rowCheckValues.append(exprInfo);
						rowCheckValues.append("<br>");
						rowCheckValues.append(temp);
					
					}
				}
				
			}//end while
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}
		
		return rowCheckValues.toString();
	}
	
	/**
	 * 获得行校验  一列校验信息
	 * @param sql      SQL语句模板
	 * @param lexpr    左表达式
	 * @param operator 运算符
	 * @param rexpr    右表达式
	 * @return         错误信息
	 * @throws GeneralException
	 */
	public String getRowChekValue(String sql ,String lexpr ,String operator , String rexpr) throws GeneralException{
		StringBuffer rowCheckValue = new StringBuffer();
		
		//获得排除列集合
		List list = ExprUtil.getEliminatesList(lexpr);
		
		//通过colMap遍例所有列
		HashMap colMap = tbo.getColMap();
		
		int i = 1; //第1列
		while(true){
			
			//如果当前列不是排除列
			if(!ExprUtil.isEliminates(list,i)){							
				//实际的列数值对应的二维数组下标
				String n = (String)colMap.get(String.valueOf(i));
				if(n == null){
					break;
				}else{	
					//替换SQL模板中的‘CN’
					int nc = Integer.parseInt(n) + 1;
					String col = "C"+nc;
					String rcsql = sql.replaceAll("CN" ,col);
					
					//System.out.println(rcsql);
					
					ContentDAO dao = new ContentDAO(this.conn);
					RowSet rs = null;
					try{
						rs =dao.search(rcsql);
						if(rs.next()){//校验公式	
							
							//获得小数位
							int ln = this.getRowCheckExprResultDigitaValue(ExprUtil.getExprNumbers(lexpr),Integer.parseInt(n));
							int rn = this.getRowCheckExprResultDigitaValue(ExprUtil.getExprNumbers(rexpr),Integer.parseInt(n));
							
							//得到最终SQL计算值
							String lExprValue = this.getFormatExprValue(rs.getString("lexpr"),ln);
							String rExprValue = this.getFormatExprValue(rs.getString("rexpr"),rn);	
							
							//JSP页面显示的运算符
							String op =ExprUtil.getReverseOperator(ExprUtil.getOperator(operator));		
							
//						  解决精度不一致问题
							if(!getValueIsEquation(lExprValue,rExprValue,operator))
							{
								String temp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
								ResourceFactory.getProperty("reportinnercheck.di")+ i + 
								ResourceFactory.getProperty("reportinnercheck.col")+ ":"+ lExprValue + op + rExprValue;						
								rowCheckValue.append(temp);
								rowCheckValue.append("<br>");	
							}
							
						}//end while
					}catch(Exception e){
						   e.printStackTrace();
						   throw GeneralExceptionHandler.Handle(e);
					}
				}//end if
			}//end if
			i++;
		}//end while		
		return rowCheckValue.toString();
	}
	
	
	/**
	 * 获取反转的行集合
	 * @return
	 */
	public HashMap getRowMap(){
		HashMap temp = new HashMap();
		HashMap rowMap = tbo.getRowMap();
		int i = 1;
		while(rowMap.get(String.valueOf(i)) != null){
			String n = (String)rowMap.get(String.valueOf(i));
			int nn = Integer.parseInt(n)+1;		
			temp.put(String.valueOf(nn) , String.valueOf(i));
			i++;
		}	
		return temp;
	}
	
	
	
	/**
	 * 返回列校验中的结果的小数点位数
	 * @param row
	 * @param colList
	 * @return
	 */
	public int  getColCheckExprResultDigitaValue(int row , List colList){
		int n = 0;		
		if(colList == null){
			return 0;
		}
		for(int i =0 ; i<colList.size() ; i++){
			int m = Integer.parseInt((String)colList.get(i));
			if(this.colMap.get(""+m)==null) {
                continue;
            }
			m = Integer.parseInt(""+this.colMap.get(""+m)) ;
			if(digitalResults[row][m] > n){
				n = digitalResults[row][m];
			}
		}
		return n;
	}

	

	/**
	 * 返回行校验中的结果的小数点位数
	 * @param rolList
	 * @param row
	 * @return
	 */
	public int  getRowCheckExprResultDigitaValue( List rowList , int col){
		int n = 0;
		if(rowList == null){
			return 0;
		}
		for(int i =0 ; i<rowList.size() ; i++){
			String nn = (String)rowList.get(i);
			nn=(String)rowMap.get(nn);
			if(nn == null || "".equals(nn)){
			}else{
				int m = Integer.parseInt((String)rowMap.get((String)rowList.get(i)));
				if(m<digitalResults.length&&n<digitalResults[0].length)
				{
					if(digitalResults[m][col] > n){
						n = digitalResults[m][col];
					}
				}
			}
		
		}
		return n;
//		int n = 0;
//		if(rowList == null){
//			return 0;
//		}
//		for(int i =0 ; i<rowList.size() ; i++){
//			int m = Integer.parseInt((String)rowList.get(i));
//			if(this.rowMap.get(""+m)==null)
//				continue;
//			m = Integer.parseInt(""+this.rowMap.get(""+m)) ;
//			if(m<digitalResults.length&&n<digitalResults[0].length)
//			{
//			if(digitalResults[m][col] > n){
//				n = digitalResults[m][col];
//			}
//			}
//		}
//		return n;
	}
	
	
	/**
	 * 获取规范的表达式的值
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String getFormatExprValue(String exprValue , int flag){
		return PubFunc.round(exprValue,flag);
	/*	StringBuffer sb = new StringBuffer();	
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}	
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		return dstr;*/
	}
	

}
