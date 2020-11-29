/*
 * Created on 2006-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;
/**
 * @author 
 * 表内校验
 */
public class TableCheck {
	
	private String tabid;
	private Connection conn;
	private TnameBo tbo;    
	private HashMap rowMap; //封装表的行数据,列校验
	private HashMap colMap; //封装表的列数据,行校验
	
	
	public TableCheck(){
		
	}

	public TableCheck(String tabid , Connection conn ){
		this.tabid=tabid;
		this.conn=conn;
	}
	
	public TableCheck( Connection conn , String tabid , String userID){
		this.tabid=tabid;
		this.conn=conn;
	//	System.out.println("进入初始化TnameBo类。。。。。。。。。");
		tbo = new TnameBo(conn,tabid,userID,"");
	//	System.out.println("初始化TnameBo类OK。。。。。。。。。");
	}
	
	/**
	 * 封装报表数据
	 * @param               
	 * @return
	 * @throws GeneralException
	 */
	public void buildDB() throws GeneralException{
		
		HashMap rm = tbo.getRowMap();//行信息
		HashMap cm = tbo.getColMap();//列信息
		
	//	System.out.println("rmsize=" + rm.size());
	//	System.out.println("cmsize=" + cm.size());
		
		
		rowMap = new HashMap();
		colMap = new HashMap();
		
		StringBuffer sql = new StringBuffer();
		
		//列数据封装-行校验
		int i = 1;//列标识
		while(true){
		//	System.out.println("第" + String.valueOf(i) + "列信息");
			
			String tempcol = (String)cm.get(String.valueOf(i));		
			HashMap hm = new HashMap();
			if(tempcol != null){
				int c  = Integer.parseInt(tempcol)+1;
				String col = "C"+c;
				sql.delete(0,sql.length());
				sql.append("select ");
				sql.append(col);
				sql.append(" from tb");
				sql.append(this.tabid);
				
				System.out.println("sql=" + sql);
				
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				try{
					rs =dao.search(sql.toString());
					int j = 1;
					while(true){
						String temprow = (String)rm.get(String.valueOf(j));					
						if(temprow != null){
							int row = Integer.parseInt(temprow)+1;
							if(rs.absolute(row)){
								String rowDB = rs.getString(col);
								hm.put(String.valueOf(j),rowDB);
					//			System.out.println(col + "列信息中的一行  key=" + String.valueOf(j) +"  value=" + rowDB );
							}
						}else{
							break;
						}
						j++;
					}

				}catch(Exception e){
					   e.printStackTrace();
					   throw GeneralExceptionHandler.Handle(e);
				}/*finally{
				   if(rs!=null){
						try {
							rs.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
							throw GeneralExceptionHandler.Handle(e1);
						}
				   }

				}*/
				
			}else{
				break;
			}//end if
			
			colMap.put(String.valueOf(i),hm);
			i++;
		}//end while
		
		
		//行数据的封装-列校验
		sql.delete(0,sql.length());
		sql.append("select * from tb");
		sql.append(this.tabid);
		
	//	System.out.println("列校验SQL=" + sql.toString());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			int k = 1;//行标识
			while(true){	
				
			//System.out.println("第" + k + "行信息");
				
				String temprow = (String)rm.get(String.valueOf(k));
				HashMap hm = new HashMap();
				if(temprow != null){
					if(rs.absolute(Integer.parseInt(temprow)+1)){//定位到行
						int j =1;
						while(true){
							String tempcol = (String)cm.get(String.valueOf(j));//字段
							if(tempcol != null){
								String c = "C" + (Integer.parseInt(tempcol)+1);
								
							//	System.out.println("字段名=" + c);
								
								String colDB = rs.getString(c);
								hm.put(String.valueOf(j),colDB);
							//	System.out.println(k+"行中的列信息   key=" + String.valueOf(j) +"   value=" + colDB );
							}else{
								break;
							}
							j++;
						}
					}
				}else{
					break;
				}
				rowMap.put(String.valueOf(k),hm);
				k++;
			}//end while
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}/*finally{
		   if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					throw GeneralExceptionHandler.Handle(e1);
				}
		   }

		}*/
	}
	
	/**
	 * 表内校验
	 * @param               
	 * @return
	 * @throws GeneralException
	 * @throws GeneralException
	 */
	public String reportCheckResultToHTML() throws GeneralException{
	//	System.out.println("进入报表校验。。。。。。。。");
		this.buildDB();
		StringBuffer reportCheckResult = new StringBuffer();
		List list = this.rowCheck();
	//	System.out.println("校验完毕。。。。size=" + list.size());
		if(list != null){
			for(int i=0 ; i< list.size(); i++){
				List hm = (ArrayList)list.get(i);
				for(int j=0; j< hm.size();j++){
					reportCheckResult.append((String)hm.get(j));
				}
			}
		}
		
		return reportCheckResult.toString();
	}
	
	/**
	 * 表内校验-行校验(列比较)
	 * @param               
	 * @return
	 * @throws GeneralException
	 */
	public List rowCheck() throws GeneralException{
		
		List rowCheckResult = new  ArrayList();//行校验信息
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from rowchk where tabid = ");//行校验公式
		sql.append(this.tabid);
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){//一条行校验公式
				
				List colCheckResult = new ArrayList();//存放每一条行校验公式校验结果（列信息）
				
				//原始表达式
				String lexpr = rs.getString("lexpr");//左表达式
				String operator = String.valueOf(rs.getInt("opsign"));//操作符
				String rexpr = rs.getString("rexpr");//右表达式
				
			/*	System.out.println("原始表达式开始.............");
				System.out.println("左表达式=" + lexpr);
				System.out.println("操作符="+operator);
				System.out.println("右表达式="+ rexpr);
				System.out.println("原始表达式结束.............");
				*/

				//规范化表达式
				ExprAnalyse ea = new ExprAnalyse(lexpr,operator,rexpr);
				String clexpr = ea.getCLExpr();//左
				String crexpr = ea.getCRExpr();//右
				String o = ea.getOperator();//操作符
				String eecol = ea.getEliminates(lexpr);
				List elist = ea.getEliminates();
				
			/*	System.out.println("规范后的表达式开始...............");
				System.out.println("左=" + clexpr);
				System.out.println("右=" + crexpr);
				System.out.println("操作符=" + o);
				System.out.println("排除列信息" + eecol);
				System.out.println("规范后的表达式结束...............");
				System.out.println();
				*/
				if(elist == null){
					//添加校验标题(无排除列)
					colCheckResult.add("&nbsp;&nbsp;"+ResourceFactory.getProperty("rowcheckanalyse.rowcheck")+"： " + clexpr+o + rexpr);				
				}else{
					//添加校验标题(无排除列)
					colCheckResult.add("&nbsp;&nbsp;"+ResourceFactory.getProperty("rowcheckanalyse.rowcheck")+"： " + clexpr+o + rexpr + ResourceFactory.getProperty("rowcheckanalyse.paichu")+"("+eecol+")"+ResourceFactory.getProperty("rowcheckanalyse.col"));
				}				
				colCheckResult.add("<br>");
				
			//	System.out.println("title=" + clexpr+o + rexpr + "排除("+eecol+")列" );
				
				//遍利所有列信息
				for(int i=0 ; i< colMap.size(); i++){
					
					if(ea.isEliminate(elist,i+1) == false){//当前列是否是排除列
						
						//获得有效的一列
						HashMap hm = (HashMap) colMap.get(String.valueOf(i+1));
					//	System.out.println("开始分析" + (i+1) + "列");
						
						//表达式替换DB中值
						String cle = this.getExprString(clexpr,hm);
						String cre = this.getExprString(crexpr,hm);
						//得到计算公式的值
						StringExprOperation seo = new StringExprOperation();
					
				/*		System.out.println("字符串公式开始");
						System.out.println("cle=" + cle);
						System.out.println("cre=" + cre);
						System.out.println("字符串公式结束");
						*/
						String lexperValue = seo.getExprValue(cle);
						String rexperValue = seo.getExprValue(cre);
						
					//	System.out.println("左表达式的值=" + lexperValue);
					//	System.out.println("右表达式的值=" + rexperValue);
						
						//校验本列
						boolean b = seo.checkExpr(lexperValue , o ,rexperValue);
						
				//		System.out.println("本列分析结束 b=" + b);
						
						if(b){
						}else{
							colCheckResult.add("&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("reportinnercheck.di")+(i+1)+ResourceFactory.getProperty("reportinnercheck.col")+"    "+lexperValue+"≠"+rexperValue);	
							colCheckResult.add("<br>");
						}
					}//end if	
				}//end for
				
				rowCheckResult.add(colCheckResult);
				
			}//end while
		}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
		}/*finally{
		   if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					throw GeneralExceptionHandler.Handle(e1);
				}
		   }

		}*/
		
		return rowCheckResult;
		
	}
	
	//获取字符串表达式
	public String getExprString(String str , HashMap hm){

		if(str.indexOf('+')==-1 && str.indexOf('-')==-1 && str.indexOf('*')==-1 && str.indexOf('/')==-1){
			StringBuffer temp = new StringBuffer();
			for(int i=0 ; i<str.length();i++){
				if(str.charAt(i)== '(' || str.charAt(i) ==')'){	
				}else{
					temp.append(str.charAt(i));
				}
			}
			if(temp.toString().indexOf('C')!=-1){
				String tt = temp.toString().replaceAll("C","");
				return tt;
			}
			return (String)hm.get(temp.toString());
		}
		String temp="+-*/";
		if(str.indexOf('+')== -1 && str.indexOf('-')==-1 
				&& str.indexOf('*')==-1 && str.indexOf('/')==-1){
			String t = str.replaceAll("C","");
			String tt = (String)hm.get(t);
			return tt;
		}else{
			for(int i=0; i<str.length();i++ ){
				if(temp.indexOf(str.charAt(i))!=-1){//找到一个非数值+-*/
					
					String ln = this.getLNumber(str,i);
					String rn = this.getRNumber(str,i);	
					
					String dln = null;
					String drn = null;
					
					if(ln.indexOf('C')!=-1 && rn.indexOf('C') == -1){
						dln = null;
						drn = (String)hm.get(rn);
					}else if(rn.indexOf('C')!= -1 && ln.indexOf('C') == -1){
						dln = (String)hm.get(ln);
						drn = null;
					}else if(ln.indexOf('C')==-1 && rn.indexOf('C')== -1 ){
						dln = (String)hm.get(ln);
						drn = (String)hm.get(rn);
					}
					
					if(dln != null && drn== null){
						int n1 = ln.length();				
						int dn1 = dln.length();
						str = this.getTempString(str,i-ln.length(),i-1 ,dln);
						i=i+dn1-n1;
					}
					if(drn != null && dln ==null){
						int n2 = rn.length();
						int dn2 = drn.length();
						str = this.getTempString(str,i+1,i+rn.length() ,drn);
						i=i+dn2-n2;
					}
					
					if(dln != null && drn != null){
						int n2 = rn.length();
						int dn2 = drn.length();
						str = this.getTempString(str,i-ln.length(),i+rn.length() ,dln+str.charAt(i)+drn);
						i=i+dn2-n2;
					}
					
								
				}//end if 
			}//end for
		}//end if 
		
		return str.replaceAll("C","");
		
	}
	
	//获得运算符左边的数值
	private String getLNumber(String  expr , int flag){
		String str = expr.substring(0,flag);
		StringBuffer temp1 = new StringBuffer();
		for(int i=str.length()-1; i>=0; i--){
			if(Character.isDigit(str.charAt(i))|| str.charAt(i)=='.' || str.charAt(i)=='C'){
				temp1.append(str.charAt(i));
			}else{
				break;
			}
		}

		StringBuffer temp = new StringBuffer();
		for(int j = temp1.length()-1; j>=0 ; j--){
			temp.append(temp1.charAt(j));
		}
		return temp.toString();
	}
	
	//获得运算符右边的数值
	private String getRNumber(String expr ,int flag ){
		String str = expr.substring(flag+1,expr.length());
		StringBuffer temp = new StringBuffer();
		for(int i=0; i< str.length();i++){
			if(Character.isDigit(str.charAt(i))|| str.charAt(i)=='.' || str.charAt(i)=='C'){
				temp.append(str.charAt(i));
			}else{
				break;
			}
		}
		return temp.toString();
	}
	
	//	获得计算时的中间字符串
	private String getTempString(String expr ,int start , int end ,String t){
		StringBuffer temp = new StringBuffer();
		temp.append(expr.substring(0,start));
		temp.append(t);
		temp.append(expr.substring(end+1 , expr.length()));
		return temp.toString();
	}
	
	/**
	 * 将统计表达式转换为sql可执行的正规表达式
	 * @param statExpr         统计表达式     
	 * @param midVariableList  临时变量集合
	 * @param fieldType		   列数据类型 map
	 * @param appdate		   截止日期
	 * @return
	 */
	public String tranNormalExpr(String statExpr,ArrayList midVariableList,HashMap fieldType,String appdate)
	{
		ExprAnalyse exprAnalyse=new ExprAnalyse();
		String a_statExpr=statExpr;
		ArrayList factorList=exprAnalyse.analyseStatExpr(statExpr);
		int year=1900;
		int month=0;
		int day=0;
		if(appdate!=null&&appdate.indexOf("-")!=-1)
		{
			String[] date=appdate.split("-");
			year=Integer.parseInt(date[0]);
			month=Integer.parseInt(date[1]);
			day=Integer.parseInt(date[2]);
		}
		else
		{
			GregorianCalendar now=new GregorianCalendar();
			year=now.get(Calendar.YEAR);
			month=now.get(Calendar.MONTH)+1;
			day=now.get(Calendar.DAY_OF_MONTH);

		}
		for(Iterator t=factorList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			boolean isVariable=false;             //判断值是否是临时变量
			for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
			{
				RecordVo vo=(RecordVo)sub_t.next();							
				if(temp.equals(vo.getString("chz").trim()))
				{
					if(vo.getInt("ntype")==3)
					{
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(vo.getString("cname"))+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(vo.getString("cname"))+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(vo.getString("cname"))+")*0.0001)");			
						a_statExpr=a_statExpr.replaceAll(temp,tempstr.toString());
					
					}
					else if(vo.getInt("ntype")==1)
					{
						a_statExpr=a_statExpr.replaceAll(temp,vo.getString("cname"));
					}
					isVariable=true;
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c))
				{
					String a_type=(String)fieldType.get(temp);
					if("D".equals(a_type))
					{												
						StringBuffer tempstr=new StringBuffer(" (( "+year+"-"+Sql_switcher.year(temp)+" )+");
						tempstr.append("("+month+"-"+Sql_switcher.month(temp)+")*0.01+");
						tempstr.append("("+day+"-"+Sql_switcher.day(temp)+")*0.0001)");							
						a_statExpr=a_statExpr.replaceAll(temp,tempstr.toString());	
					}
				}
			}

		}	
		System.out.println("a_statExpr="+a_statExpr);
		return a_statExpr;
	}
	

	
	
	public static void main(String[] arg)
	{
	//	System.out.println("aaaaaaaaaaaaa");

	}
	
	

	/**
	 * 统计表达式分析，取得指标和临时变量列表
	 * @param statExpr 表达式
	 * @param midVariableList 当前表的临时变量列表
	 * @author dengc
	 * @return

	 */
	public ArrayList statExprAnalyse(String statExpr,ArrayList midVariableList)

	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();      //指标集和
		ArrayList variableList=new ArrayList();   //临时变量集合
		
		ExprAnalyse exprAnalyse=new ExprAnalyse();
		ArrayList factorList=exprAnalyse.analyseStatExpr(statExpr);
		for(Iterator t=factorList.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			boolean isVariable=false;             //判断值是否是临时变量
			for(Iterator sub_t=midVariableList.iterator();sub_t.hasNext();)
			{
				RecordVo vo=(RecordVo)sub_t.next();
			//	System.out.println("temp="+temp+"  ---"+vo.getString("chz").trim());
				if(temp.equals(vo.getString("chz").trim()))
				{
					isVariable=true;
					variableList.add(vo.getString("cname"));
					break;
				}
			}
			if(!isVariable)
			{
				char c=temp.toLowerCase().charAt(0);
				if(Character.isLowerCase(c)) {
                    fieldList.add(temp);
                }
			}
		}
		list.add(fieldList);
		list.add(variableList);
		return list;
		
	}
	
	
}
