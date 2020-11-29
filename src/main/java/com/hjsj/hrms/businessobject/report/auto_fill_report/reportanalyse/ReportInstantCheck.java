package com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportCheckErrorInfo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;


/*
返回值格式
	校验错误表达式@校验错误信息@校验公式语法错误信息@左表达式错误(逗号分割)@右表达式错误@列校验中错误的行号集合@行校验中错误的列号集合#	
	@一条内部分割
	#条目之间分割
 */

/**
 * <p>Title:报表即时表内校验</p>
 * <p>Description:报表即时校验-特定报表内校验，对传入的二维数组数据进行分析</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportInstantCheck {
	
	private Connection conn;         //数据库连接
	private double [][] reportValues;//实时数据，不排除编号和甲，下标从0开始
	
	private TnameBo tbo;             //报表信息封装类
	private int [][]digitalResults;  //小数点
	
	private HashMap colsMap;		 //报表的列信息：封装了列在报表中的实际位置，传入公式中列数得到实际的列针对与二维数组的下标
	private HashMap rowsMap;		 //报表的行信息：封装了行在报表中的实际位置，传入公式中行数得到实际的列针对与二维数组的下标
	
	private StringBuffer errorRows = null ; //保存特定报表列校验中错误行信息(逗号分割)
	private StringBuffer errorCols = null ; //保存特定报表列校验中错误列信息(逗号分割)
	
	private double lExprValue;	//坐表达式计算结果
	private double rExprValue;  //右表达式计算结果
	
	private String reportPrefix; //操作报表前缀
	private String whereSQL;     //操作报表SQL语句的where片段
	
	public ReportInstantCheck(){
		
	}
	
	/**
	 * 针对特定报表校验的构造器-报表编辑中使用
	 * @param conn       DB连接
	 * @param value      特定表的行列数据二维数组
	 * @param tbo        报表类
	 * @param reportFlag 校验的报表类型 1 tb  2 tt_表
	 * @param whereSQL   操作数据依据 userName 或 unitCode
	 */
	public ReportInstantCheck(Connection conn , double [][] value , TnameBo tbo ,int reportFlag,String whereSQL){
		this.conn = conn;
		this.reportValues= value;
		this.tbo = tbo;
		this.digitalResults = tbo.getDigitalResults();
		this.colsMap = tbo.getColMap();
		this.rowsMap = tbo.getRowMap();
		if(reportFlag == 1){
			this.reportPrefix = "tb";
			this.whereSQL = " and username = '" + whereSQL + "' ";
		}else if(reportFlag == 2){
			this.reportPrefix = "tt_";
			this.whereSQL = " and unitcode = '" + whereSQL + "' ";
		}
		
	/*	System.out.println("小数点位数。。。。。。。。");
		for(int i = 0 ; i< this.digitalResults.length; i++){
			for(int j = 0 ; j< this.digitalResults[i].length; j++){
				System.out.print(this.digitalResults[i][j] + "    ");
			}
			System.out.println();
		}*/
	}
	
	
	/**
	 *  针对特定报表表内校验-报表编辑中使用
	 * @return 错误信息字符串
	 * @throws GeneralException
	 */
	public String reportInstantCheck() throws GeneralException{
		StringBuffer reportInstantCheckErrors = new StringBuffer();
		
		reportInstantCheckErrors.append(this.colCheck());
		reportInstantCheckErrors.append(this.rowCheck());
		
		String tt = reportInstantCheckErrors.toString();
		
		if(tt == null || "".equals(tt.trim())){
			return "null";
		}else{
			//规范化错误信息
			if(reportInstantCheckErrors.charAt(reportInstantCheckErrors.length()-1)=='#'){
				String temp = reportInstantCheckErrors.toString().substring(0,reportInstantCheckErrors.length()-1);
				//System.out.println(temp);
				return temp;
			}else{
				//System.out.println(reportInstantCheckErrors.toString());
				return reportInstantCheckErrors.toString();
			}	
			
		}
	
		
	}
	
	////////////////////////////////////----列校验部分-----/////////////////////////////////////////////////////
	
	/**
	 * 特定报表列校验-分析行数据
	 * @return
	 * @throws GeneralException
	 */
	public String colCheck() throws GeneralException{
		
		StringBuffer colCheckErrors = new StringBuffer();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from colchk where tabid = ");
		sql.append(tbo.getTabid());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				//原始列校验公式
				String le = rs.getString("lexpr");
				String o = String.valueOf(rs.getInt("opsign"));
				String re = rs.getString("rexpr");
				
				//列公式语法分析类 ，只是对列校验公式语法分析是否正确
				ColCheckAnalyse cca = new ColCheckAnalyse(le , o , re , tbo , this.reportPrefix ,this.whereSQL);			
				//表达式描述信息
				String exprInfo = cca.getExprInfo();				
				//通过返回SQL语句来判断是否语法出错！-->需要改进
				String colCheckSql = cca.getColCheckSQL();
				
				if(colCheckSql == null || "".equals(colCheckSql)){
					//语法错误					
					//报表校验错误处理类-语法错误
					ReportCheckErrorInfo ecei = new ReportCheckErrorInfo(exprInfo , cca.getCError());
					String temp = ecei.toString();
					colCheckErrors.append(temp);
					colCheckErrors.append("#");
					
				}else{//进行列的数据校验
					String colCheckValue = this.getColCheckValue(le , o ,re);
					
					if(colCheckValue == null || "".equals(colCheckValue)){
					}else{
						//报表校验错误处理类-公式校验错误
						ReportCheckErrorInfo ecei = 
							new ReportCheckErrorInfo(exprInfo ,colCheckValue,
									this.getTempExprNumbers(ExprUtil.getExpr(le),this.tbo.getColMap()),
									this.getTempExprNumbers(ExprUtil.getExpr(re),this.tbo.getColMap()),this.errorRows.toString(),0);
						
						String temp = ecei.toString();
						colCheckErrors.append(temp);
						colCheckErrors.append("#");
					}
				}
				
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
		
		return colCheckErrors.toString();
		
	}
	
	/**
	 * 特定报表表内列校验-行分析
	 * 1.将传入的数据二维数组替换校验公式中的数据
	 * 2.对替换后字符串表达式求值
	 * 3.比较左/右表达式值
	 * @param lexpr		左表达式
	 * @param operator  操作符
	 * @param rexpr     右表达式
	 * @return
	 * @throws GeneralException 
	 */
	public String getColCheckValue( String lexpr , String operator ,String rexpr) throws GeneralException {
		StringBuffer errors = new StringBuffer(); 
		SQL_Util su = new SQL_Util();
		//规范表达式
		String le = ExprUtil.getExpr(lexpr);
		String re = ExprUtil.getExpr(rexpr);
		List eList = ExprUtil.getEliminatesList(lexpr); //排除行信息
		//甲行不必考虑
		
		//错误行信息
		this.errorRows = new StringBuffer();
		
		int n = 1;//行
		while(true){
			//遍例所有行除了甲
			if(this.rowsMap.get(String.valueOf(n)) != null){
				//是否是排除行
				if(!ExprUtil.isEliminates(eList ,n)){
					//不是排除行
					
					//对应数组的行下标
					String row = (String)this.tbo.getRowMap().get(String.valueOf(n));
					
					//获得字符串表达式数据已替换公式 如： 1列= 2列+3列 
					int ln=0;
					if(ExprUtil.getExprNumbers(le)!= null){
						//获得保留精度
						 ln = this.getColCheckExprResultDigitaValue(Integer.parseInt(row),ExprUtil.getExprNumbers(le));						
					}
					String les = this.getRowTempString(le ,Integer.parseInt(row));
					String tep="0";
					for(int i=0;i<4;i++){
						tep+="0";
					}
					String res = this.getRowTempString2(re ,Integer.parseInt(row),tep);
					
					//对字符串表达式进行除零处理
					
					StringBuffer sql = new StringBuffer();
					sql.append("select (");
					sql.append(su.sqlSwitch(les));
					sql.append(" )as lexpr ,(");
					sql.append(su.sqlSwitch(res));
					sql.append(" ) as rexpr from colchk ");
					
					this.getExprValue(sql.toString());
					
					String lev = "";
					String rev = "";
					if(ExprUtil.getExprNumbers(le)!= null){
						//获得保留精度
						 ln = this.getColCheckExprResultDigitaValue(Integer.parseInt(row),ExprUtil.getExprNumbers(le));						
						lev = this.getFormatExprValue(String.valueOf(this.lExprValue),ln);
		
					}else{
						lev = String.valueOf(this.lExprValue);
					}
					
					if(ExprUtil.getExprNumbers(re) != null){
						//获得保留精度
						//int rn = this.getColCheckExprResultDigitaValue(Integer.parseInt(row),ExprUtil.getExprNumbers(re));
						//规范计算值
						rev = this.getFormatExprValue(String.valueOf(this.rExprValue),ln);
			
						
					}else{
						rev = String.valueOf(this.rExprValue);
					}
				
					
					//比较是否相同
					if(!this.isValueRight(lev,operator,rev)){//如果不相同
						StringBuffer temp = new StringBuffer();
						temp.append(" 第");
						temp.append(n);
						temp.append("行 :");
						temp.append(lev);
						temp.append(ExprUtil.getReverseOperator(ExprUtil.getOperator(operator)));
						temp.append(rev);
						temp.append("\\n");
						
						errors.append(temp.toString());
						
						//行号为二维数组下标.JSP页面颜色对比显示
						this.errorRows.append((String)this.tbo.getRowMap().get(String.valueOf(n)));
						this.errorRows.append(",");
					}
				}
			}else{
				break;
			}
			n++;
		}
		
		return errors.toString();
	}
	
	
	/**
	 * 针对某一行进行数据替换得到字符串表达式
	 * @param expr 列校验公式
	 * @param row  列校验公式对应的行
	 * @return
	 */
	public String getRowTempString(String expr , int row){
		StringBuffer temp = new StringBuffer();
		if(expr == null || "".equals(expr)){
		}else{
			expr = expr.replaceAll("--","+");
		}
		for(int i = 0 ; i< expr.length(); i++){
			if(Character.isDigit(expr.charAt(i))){
				StringBuffer num = new StringBuffer();
				num.append(expr.charAt(i));
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j))){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				int n = Integer.parseInt((String)this.colsMap.get(num.toString()));								
				String rel = reportValues[row][n]+"";								
				if("-".equals(rel.substring(0,1))) {
                    rel = "("+rel+")";
                }
				temp.append(rel);
				
			}else if (expr.charAt(i) == 'c' || expr.charAt(i)=='C'){
				StringBuffer num = new StringBuffer();
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j)) || expr.charAt(j) =='.' ){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				temp.append(num.toString());			
			}else{
				temp.append(expr.charAt(i));
			}
		}		
		return temp.toString();
	}
	/**
	 * 针对某一行进行数据替换得到字符串表达式
	 * @param expr 列校验公式
	 * @param row  列校验公式对应的行
	 * @return temp 小数位
	 */
	public String getRowTempString2(String expr , int row,String tem){
		StringBuffer temp = new StringBuffer();
		if(expr == null || "".equals(expr)){
		}else{
			expr = expr.replaceAll("--","+");
		}
		for(int i = 0 ; i< expr.length(); i++){
			if(Character.isDigit(expr.charAt(i))){
				StringBuffer num = new StringBuffer();
				num.append(expr.charAt(i));
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j))){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				int n = Integer.parseInt((String)this.colsMap.get(num.toString()));
				String rel =reportValues[row][n]+"";
				String mm = rel.substring(rel.indexOf(".")+1,rel.length());
				if(mm.length()<tem.length()){
					rel=rel+tem.substring(mm.length(), tem.length());
				}				
				if("-".equals(rel.substring(0,1))) {
                    rel = "("+rel+")";
                }
				temp.append(rel);
				
			}else if (expr.charAt(i) == 'c' || expr.charAt(i)=='C'){
				StringBuffer num = new StringBuffer();
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j)) || expr.charAt(j) =='.' ){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				temp.append(num.toString());			
			}else{
				temp.append(expr.charAt(i));
			}
		}		
		return temp.toString();
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
			String nn = (String)colList.get(i);
			if(nn == null || "".equals(nn)){
			}else{
				int m = Integer.parseInt((String)this.colsMap.get(nn));
			//	int m = Integer.parseInt(nn);  //2008-10-31
				if(digitalResults[row][m] > n){
					n = digitalResults[row][m];
				}
			}
		}
		return n;
	}
	
    ////////////////////////////////////----行校验部分-----/////////////////////////////////////////////////////
	
	/**
	 * 行校验--列分析
	 * @return
	 * @throws GeneralException
	 */
	public String rowCheck() throws GeneralException{
		StringBuffer rowCheckValues = new StringBuffer();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from rowchk where tabid = ");
		sql.append(tbo.getTabid());
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			while(rs.next()){
				//原始表达式
				String le = rs.getString("lexpr");
				String o = String.valueOf(rs.getInt("opsign"));
				String re = rs.getString("rexpr");

				
				//行校验语法分析类 分析行校验公式语法是否正确
				RowCheckAnalyse rca = new RowCheckAnalyse(le , o , re , tbo , this.reportPrefix,this.whereSQL);				
				//表达式描述信息
				String exprInfo = rca.getExprInfo();								
				//依据返回SQL语句是否为空判断语法是否错误
				String rowchecksql = rca.getRowCheckSQL();
				
				if(rowchecksql == null || "".equals(rowchecksql)){
					//语法错误				
					//报表校验错误处理类-语法错误
					ReportCheckErrorInfo ecei = new ReportCheckErrorInfo(exprInfo , rca.getCError());
					String temp = ecei.toString();
					rowCheckValues.append(temp);
					rowCheckValues.append("#");
				}else{
					//特定报表行校验
					String rowCheckValue = this.getRowChekValue(le,o ,re);
					if(rowCheckValue == null || "".equals(rowCheckValue)){
					}else{						
						//校验错误信息
						ReportCheckErrorInfo ecei = new ReportCheckErrorInfo(exprInfo ,rowCheckValue,
								this.getTempExprNumbers(ExprUtil.getExpr(le),this.tbo.getRowMap()),
								this.getTempExprNumbers(ExprUtil.getExpr(re),this.tbo.getRowMap()),this.errorCols.toString(),1);
						String temp = ecei.toString();

						rowCheckValues.append(temp);
						rowCheckValues.append("#");
					}

				}
				
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
		
		return rowCheckValues.toString();
	}
	
	
	
	
	/**
	 * 特定报表的行校验 -列分析
	 * @param lexpr    左表达式
	 * @param operator 运算符
	 * @param rexpr    右表达式
	 * @return 
	 * @throws GeneralException 
	 */
	public String getRowChekValue(String lexpr ,String operator , String rexpr) throws GeneralException{
		StringBuffer errors = new StringBuffer();
		SQL_Util su = new SQL_Util();
		//规范表达式
		String le = ExprUtil.getExpr(lexpr);
		String re = ExprUtil.getExpr(rexpr);
		List eList = ExprUtil.getEliminatesList(lexpr); //排除列信息
		
		this.errorCols = new StringBuffer();
		
		int n = 1; //列
		while(true){
			//遍例列除了编号
			if(this.colsMap.get(String.valueOf(n)) != null){
				//判断是否是排除列
				if(!ExprUtil.isEliminates(eList , n)){
					
					//获得对应数组列下标
					String col = (String)this.tbo.getColMap().get(String.valueOf(n));
					
					String les = this.getColTempString(le ,Integer.parseInt(col));
					String res = this.getColTempString(re ,Integer.parseInt(col));
					
					/*System.out.println("公式=" + le + " " + re);
					System.out.println("les=" + les);
					System.out.println("res=" + res);
					*/
					
					StringBuffer sql = new StringBuffer();
					sql.append("select (");
					sql.append(su.sqlSwitch(les));
					sql.append(" )as lexpr ,(");
					sql.append(su.sqlSwitch(res));
					sql.append(" ) as rexpr from colchk ");
					
					//SQL语句除零错误排除
					
					this.getExprValue(sql.toString());
					
					//获得保留精度
					int ln = this.getRowCheckExprResultDigitaValue(ExprUtil.getExprNumbers(le) , Integer.parseInt(col));
					int rn = this.getRowCheckExprResultDigitaValue(ExprUtil.getExprNumbers(re) , Integer.parseInt(col));
					
					//规范计算值
					String  lev = this.getFormatExprValue(String.valueOf(this.lExprValue),ln);
					String rev = this.getFormatExprValue(String.valueOf(this.rExprValue),rn);
					
					//比较是否相同
					if(!this.isValueRight(lev,operator,rev)){//如果不相同
						StringBuffer temp = new StringBuffer();
						temp.append(" 第");
						temp.append(n);
						temp.append("列 :");
						temp.append(lev);
						temp.append(ExprUtil.getReverseOperator(ExprUtil.getOperator(operator)));
						temp.append(rev);
						temp.append("\\n");
						
						errors.append(temp.toString());
						
						//转换为二维数组下标
						this.errorCols.append((String)this.tbo.getColMap().get(String.valueOf(n)));
						this.errorCols.append(",");
					}
				}
			}else{
				break;
			}
			n++;
		}
		
		return errors.toString();
	}
	

	/**
	 * 将特定行校验公式替换为报表数据的字符串表达式
	 * @param expr 校验公式
	 * @param row  特定行
	 * @return 字符串表达式
	 */
	public String getColTempString(String expr , int col){
		if(expr == null || "".equals(expr)){
		}else{
			expr = expr.replaceAll("--","+");
		}
		StringBuffer temp = new StringBuffer();
		for(int i = 0 ; i< expr.length(); i++){
			if(Character.isDigit(expr.charAt(i))){
				StringBuffer num = new StringBuffer();
				num.append(expr.charAt(i));
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j))){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				int n = Integer.parseInt((String)this.rowsMap.get(num.toString()));
				temp.append(reportValues[n][col]);
			}else if (expr.charAt(i) == 'c' || expr.charAt(i)=='C'){
				StringBuffer num = new StringBuffer();
				for(int j = i+1; j< expr.length(); j++){
					if(Character.isDigit(expr.charAt(j)) || expr.charAt(j) =='.' ){
						num.append(expr.charAt(j));
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j-1;
						break;
					}
				}
				temp.append(num.toString());			
			}else{
				temp.append(expr.charAt(i));
			}
		}		
		return temp.toString();
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
			nn=(String)rowsMap.get(nn);
			if(nn == null || "".equals(nn)){
			}else{
				int m = Integer.parseInt((String)rowsMap.get((String)rowList.get(i)));
				if(m<digitalResults.length&&n<digitalResults[0].length)
				{
					if(digitalResults[m][col] > n){
						n = digitalResults[m][col];
					}
				}
			}
		
		}
		return n;
	}
	
	
	
	/**
	 * 比较左/右表达式的值是否符合公式
	 * @param lev 左表达式值
	 * @param operator 比较运算符
	 * @param rev 右表达式
	 * @return true/flase
	 */
	public boolean isValueRight(String  lev1 , String operator , String  rev1){
		boolean b = false;
		int n = Integer.parseInt(operator);
		double lev = Double.parseDouble(lev1);
		double rev = Double.parseDouble(rev1);
		switch(n){
			case 0:
				b=(lev == rev);
				break;
			case 1:
				b=(lev > rev);
				break;
			case 2:
				b=(lev < rev);
				break;
			case 3:
				b=(lev != rev);
				break;
			case 4:
				b=(lev >= rev);
				break;
			case 5:
				b=(lev <= rev);
				break;				
		}
		
		return b;
	}
	
	
	
	/**
	 * 获取规范的表达式的值,自动四舍五入
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
	//	System.out.println("传入数据=" + exprValue + "小数位=" + flag + "规范化数据为=" + dstr) ;
		return dstr;*/
	}
	
	
	
	/**
	 * 通过SQL语句计算表达式
	 * @param sql
	 * @throws GeneralException
	 */
	public void getExprValue(String sql) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs =dao.search(sql.toString());
			if(rs.next()){
				this.lExprValue = Double.parseDouble(rs.getString("lexpr"));
				this.rExprValue = Double.parseDouble(rs.getString("rexpr"));
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

		}		*/
	}

	/**
	 * 获得表达式相对于二维数组的下标
	 * @param expr 表达式
	 * @param map  下标封装集合
	 * @return
	 */
	public String getTempExprNumbers(String expr , HashMap map){
		if(expr == null){
			return null;
		}
		StringBuffer exprNumber = new StringBuffer();
		for(int i=0; i<expr.length(); i++){
			StringBuffer temp = new StringBuffer();
			if(Character.isDigit(expr.charAt(i))){
				temp.append(expr.charAt(i));
				for(int j = i+1; j<expr.length(); j++){
					if(Character.isDigit(expr.charAt(j))){
						temp.append(expr.charAt(j));
						if(j == expr.length()-1){
							i = j;
						}
					}else{						
						i = j-1;											
						break;
					}				
				}//end for
				exprNumber.append((String)map.get(temp.toString()));
				exprNumber.append(",");
			}else if(expr.charAt(i) =='C' || expr.charAt(i) == 'c'){
				
				for(int j = i+1; j<expr.length(); j++){
					if(Character.isDigit(expr.charAt(j)) || expr.charAt(j)=='.'){	
						if(j == expr.length()-1){
							i=j;
						}
					}else{
						i = j;
						break;
					}
				}//end for
				
			}
			
		}//end for
		
		return exprNumber.toString();
	}
	
	
	public static void main(String [] args){
		ReportInstantCheck ric = new ReportInstantCheck();
		System.out.println(ric.getFormatExprValue("3.556",2));
	}
}
