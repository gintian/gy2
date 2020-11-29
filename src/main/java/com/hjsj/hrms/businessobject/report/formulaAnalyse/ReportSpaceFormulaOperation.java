package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:特定表间公式校验/运算</p>
 * <p>Description:包含了公式的左表达式分析:1:all 1:3,4,5</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zfj
 * @version 1.0
 * 
 */
public class ReportSpaceFormulaOperation {

	private Connection conn;
	private TnameBo tbo;  //左表达式的TnameBo
	private String lExpr;
	private String rExpr;
	private int flag;
	private int tabid;
	private String reportPrefix;
	private String whereSQL;
	
	//语法错误信息
	private String errors;
	//SQL语句
	private List updateColSql = new ArrayList();//表间列计算公式SQL组
	private List updateRowSql = new ArrayList(); //表间行计算公式SQL组
	
	
	//左表达式语法正确后的分析结果
	//表间行计算公式 1:2,3
	private int  row;  //第N行 1
	private List colList =new ArrayList(); //所要计算的列信息 2,3
	
	//表间列计算公式1:2,3
	private int col;    //第N列 1
	private List rowList = new ArrayList();  //所要计算的行信息 2,3
	private UserView userView;
	/**
	 * 表间计算公式分析/计算
	 * 	示例：1:all=C100+[60:2..4+C100*5]
	 * @param conn  数据库连接
	 * @param tabid 表号  :update tb表号
	 * @param lExpr 左表达式
	 * @param rExpr 右表达式
	 * @param flag  1 行 2 列
	 * @param reportPrefix 操作报表的前缀tb / tt_
	 * @param whereSQL  操作表的限制条件 根据用户名或填报单位编码获得操作的数据
	 */
	public ReportSpaceFormulaOperation(Connection conn , int tabid , String lExpr , String rExpr  , int flag ,String reportPrefix,String whereSQL){
		this.conn = conn;
		this.tabid = tabid;
		this.tbo = new TnameBo(conn,String.valueOf(tabid));//公式所在表的信息
		this.lExpr = lExpr;
		this.rExpr = rExpr;
		this.flag = flag;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}
	
	//**********************************表间计算公式合法性分析********************************
	public String reportSpaceFormulaCheck() throws GeneralException{
		StringBuffer result = new StringBuffer();
		if(!this.lexprAnalyse()){//左表达式分析错误
			result.append(this.errors);
		}else{	
			if(this.flag ==1){//行
				//表间右表达式分析类
				ReportSpaceRExprFormulaAnalyse rsrefa = new ReportSpaceRExprFormulaAnalyse(this.conn ,1 , this.reportPrefix ,this.whereSQL);
				rsrefa.setUserView(this.userView);
				rsrefa.setTabid(tabid);
				rsrefa.setTbo(tbo);
				for(int i = 0 ; i< this.colList.size(); i++){
					boolean b = rsrefa.run(this.rExpr ,Integer.parseInt((String)this.colList.get(i)));
					if(!b){
						result.append(rsrefa.getErrors());
						return result.toString();
					}else{
					}
				}				
			}else if(this.flag == 2){//列
				ReportSpaceRExprFormulaAnalyse rsrefa = new ReportSpaceRExprFormulaAnalyse(this.conn,2 , this.reportPrefix ,this.whereSQL);			
				rsrefa.setUserView(this.userView);
				rsrefa.setTabid(tabid);
				rsrefa.setTbo(tbo);
				for(int i=0; i< this.rowList.size(); i++){
					boolean b = rsrefa.run(this.rExpr,Integer.parseInt((String)this.rowList.get(i)));
					//生成SQL语句组
					if(!b){
						result.append(rsrefa.getErrors());
						return result.toString();
					}else{	
					}			
				}
			}
			
		}
		if(result== null || "".equals(result.toString())){
			return "ok";
		}else{
			return result.toString();
		}
	}
		

	//**********************************表间计算公式合法性分析********************************
	
	
	
	/**
	 * 表间计算公式分析
	 * @return
	 * @throws GeneralException
	 */
	public String reportSpaceFormulaOperation() throws GeneralException{ 
		SQL_Util su = new SQL_Util();
		//计算公式的整体分析结果
		StringBuffer result = new StringBuffer();
		
		if(!this.lexprAnalyse()){//左表达式分析错误
			result.append(this.errors);
		}else{		
			
			//-------------------------右表达式分析开始-------------------
			
			if(this.flag ==1){//行
				
				//表间右表达式分析类
				ReportSpaceRExprFormulaAnalyse rsrefa = new ReportSpaceRExprFormulaAnalyse(this.conn ,1 , this.reportPrefix ,this.whereSQL);
				rsrefa.setUserView(this.userView);
				rsrefa.setTabid(tabid);
				rsrefa.setTbo(tbo);
				//生成update语句组
				for(int i = 0 ; i< this.colList.size(); i++){
					StringBuffer updateRowSql = new StringBuffer();
					boolean b = rsrefa.run(this.rExpr ,Integer.parseInt((String)this.colList.get(i)));

					if(!b){
						result.append(rsrefa.getErrors());
						return result.toString();
					}else{
						updateRowSql.append("update ");
						updateRowSql.append(this.reportPrefix);
						updateRowSql.append(this.tabid);
						updateRowSql.append(" set C");
						updateRowSql.append(Integer.parseInt((String)this.tbo.getColMap().get(String.valueOf(this.colList.get(i))))+1);
						updateRowSql.append("=");
						updateRowSql.append(su.sqlSwitch(rsrefa.getSql()));
						updateRowSql.append(" where secid = ");
						updateRowSql.append(Integer.parseInt((String)this.tbo.getRowMap().get(String.valueOf(this.row)))+1);
						updateRowSql.append(this.whereSQL);
					}
					this.updateRowSql.add(updateRowSql.toString());
				}
				
				//执行SQL语句组
				if(this.executeUpdateSQL(this.updateRowSql)){
				}else{
				}
				String operateObject="1";
				String unitcode ="";
				if("tt_".equalsIgnoreCase(reportPrefix)){
					operateObject="2";
					if(this.whereSQL.indexOf("=")!=-1){
						String temp ="";
						temp = this.whereSQL.substring(this.whereSQL.indexOf("=")+1, this.whereSQL.length());
						temp = temp.replace("'", "");
						unitcode=temp.trim();
					}
				}
				this.tbo.autoUpdateDigitalResults(operateObject, "3", ""+(Integer.parseInt((String)this.tbo.getRowMap().get(String.valueOf(this.row)))+1), "", this.tbo.getTabid(),userView.getUserName(),unitcode);
			}else if(this.flag == 2){//列
				ReportSpaceRExprFormulaAnalyse rsrefa = new ReportSpaceRExprFormulaAnalyse(this.conn,2 , this.reportPrefix ,this.whereSQL);
				rsrefa.setUserView(this.userView);
				rsrefa.setTabid(tabid);
				rsrefa.setTbo(tbo);
				for(int i=0; i< this.rowList.size(); i++){
					StringBuffer updateColSql = new StringBuffer();
					boolean b = rsrefa.run(this.rExpr,Integer.parseInt((String)this.rowList.get(i)));// FIXME 提速
					//生成SQL语句组
					if(!b){
						result.append(rsrefa.getErrors());
						return result.toString();
					}else{
						updateColSql.append("update ");
						updateColSql.append(this.reportPrefix);
						updateColSql.append(this.tabid);
						updateColSql.append(" set C");
						updateColSql.append(Integer.parseInt((String)this.tbo.getColMap().get(String.valueOf(this.col)))+1);
						updateColSql.append(" = ");
						updateColSql.append(su.sqlSwitch(rsrefa.getSql()));
						updateColSql.append(" where secid = ");		
						updateColSql.append(Integer.parseInt((String)this.tbo.getRowMap().get(String.valueOf(this.rowList.get(i))))+1);
						updateColSql.append(this.whereSQL);
					}
					this.updateColSql.add(updateColSql.toString());
				}
				
				//System.out.println(this.updateColSql);
				
				//执行SQL语句组
				if(this.executeUpdateSQL(this.updateColSql)){
				}else{
				}
				String operateObject="1";
				String unitcode="";
				if("tt_".equalsIgnoreCase(reportPrefix)){
					operateObject="2";
					if(this.whereSQL.indexOf("=")!=-1){
						String temp ="";
						temp = this.whereSQL.substring(this.whereSQL.indexOf("=")+1, this.whereSQL.length());
						temp = temp.replace("'", "");
						unitcode=temp.trim();
					}
				}
				this.tbo.autoUpdateDigitalResults(operateObject, "4", "", "c"+(Integer.parseInt((String)this.tbo.getColMap().get(String.valueOf(this.col)))+1), this.tbo.getTabid(),userView.getUserName(),unitcode);
			}
			
		}
		if(result== null || "".equals(result.toString())){
			return "null";
		}else{
			return result.toString();
		}
		
	}
	
	
	/**
	 * 判断表间计算公式的左表达式语法是否正确
	 * @return
	 */
	public boolean  lexprAnalyse(){
		 boolean b = true;
		/*
		 * 报表表间计算公式左表达式语法分析
		 * 	1:all
		 * 	1:2,3,4
		 */
		String regex="[\\d]+\\:(([(A|a)][(L|l)][(L|l)]){1}|([\\d]+\\,)*([\\d]+)$)";
		
		if(this.lExpr.matches(regex)){//语法正确		
			if(this.flag == 1){//行
				
				String t1 = this.lExpr.substring(0,this.lExpr.indexOf(':'));//行
				String t2 = this.lExpr.substring(this.lExpr.indexOf(':')+1,this.lExpr.length());//列信息

				//------------------------左表达式中的行分析--------------------
				//校验行的合法性-如果要计算的行不存在则抱错！
				if(t1.length() >10){
					b= false;
					//行不存在
					this.errors=ResourceFactory.getProperty("colcheckanalyse.lexpr2") + t1+ResourceFactory.getProperty("colcheckanalyse.rowNoExist") +"!";
				}
				//if(Integer.parseInt(t1) < this.tbo.getMaxRowNumber(this.tbo.getRowMap())){
				if(this.tbo.getRowMap().get(t1)!=null){
					this.row = Integer.parseInt(t1);
				}else{
					b= false;
					//行不存在
					this.errors=ResourceFactory.getProperty("colcheckanalyse.lexpr2") + t1 +ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"!";
				}
				
				//------------------------左表达式中的列分析------------------------
				
				if(t2.matches("[(A|a)][(L|l)][(L|l)]")){//所有列
					this.getAllCol_RowInfo();
				}else{
					String [] t3 = t2.split(",");
					for(int i = 0 ; i< t3.length; i++){
						if(t3[i].trim().length()>10){
							b= false;
							//列不存在	
							this.errors = ResourceFactory.getProperty("colcheckanalyse.lexpr") + t2 +ResourceFactory.getProperty("colcheckanalyse.zhong") + t3[i] +ResourceFactory.getProperty("colcheckanalyse.colNoExist")+ "！";
						}
						int col = Integer.parseInt(t3[i]);

						if(col > this.tbo.getMaxRowNumber(this.tbo.getColMap())){
							b= false;
							//列不存在	
							this.errors =ResourceFactory.getProperty("colcheckanalyse.lexpr")+ t2 +ResourceFactory.getProperty("colcheckanalyse.zhong") + col +ResourceFactory.getProperty("colcheckanalyse.colNoExist")+ "！";
						}else{
							colList.add(String.valueOf(col));
						}
						
					}
				}
				
				
			}else if(this.flag == 2){//列
				
				String t1 = this.lExpr.substring(0,this.lExpr.indexOf(':'));//列
				String t2 = this.lExpr.substring(this.lExpr.indexOf(':')+1,this.lExpr.length());//行信息
				
				if(t1.trim().length()>10){
					b= false;
					//列不存在	
					this.errors = ResourceFactory.getProperty("colcheckanalyse.lexpr2") + t1 + ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
				}
				
			/*	if(Integer.parseInt(t1) < this.tbo.getMaxRowNumber(this.tbo.getColMap())){
					this.col = Integer.parseInt(t1);
				}*/
				if(this.tbo.getColMap().get(t1)!=null) {
                    this.col=Integer.parseInt(t1);
                } else{
					b= false;
					//列不存在	
					this.errors = ResourceFactory.getProperty("colcheckanalyse.lexpr2") + t1 +ResourceFactory.getProperty("colcheckanalyse.colNoExist")+ "！";
				}
				
				if(t2.matches("[(A|a)][(L|l)][(L|l)]")){//所有行
					this.getAllCol_RowInfo();//所有行信息
				}else{
					String [] t3 = t2.split(",");
					for(int i = 0 ; i< t3.length; i++){
						if(t3[i].trim().length()>10){
							b= false;
							//行不存在
							this.errors = ResourceFactory.getProperty("colcheckanalyse.rexpr") + t2 + ResourceFactory.getProperty("colcheckanalyse.zhong") + t3[i] + ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
						}
						int row = Integer.parseInt(t3[i]);					
						if(row > this.tbo.getMaxRowNumber(this.tbo.getRowMap())){
							b= false;
							//行不存在
							this.errors = ResourceFactory.getProperty("colcheckanalyse.rexpr") + t2 + ResourceFactory.getProperty("colcheckanalyse.zhong") + row +ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+ "！";
						}else{
							rowList.add(String.valueOf(row));
						}
					}
				}
			}
		}else{
			b= false;
			//"左表达式语法错误！";
			this.errors=ResourceFactory.getProperty("edit_report.info15")+"！";
		}
		return b;
	}
	
	/**
	 * 处理坐表达式All情况即：所有行或列信息
	 */
	public void getAllCol_RowInfo(){
		if(this.flag == 1){//行统计所有列的信息
			//最大列
			int maxCol = this.tbo.getMaxRowNumber(this.tbo.getColMap());	
			for(int i=1; i<=maxCol; i++){
				this.colList.add(String.valueOf(i));
			}
		}else if(this.flag == 2){
			//最大行
			int maxRow = this.tbo.getMaxRowNumber(this.tbo.getRowMap());
			for(int i=1; i<= maxRow; i++){
				this.rowList.add(String.valueOf(i));
			}
		}
	}
	

	/**
	 * 执行UpdateSQL语句
	 * @return 
	 * @throws GeneralException 
	 */
	public boolean executeUpdateSQL(List sql) throws GeneralException {
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.batchUpdate(sql);
			b= true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return b;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	
}
