package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.AnalyseParams;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:特定报表的表内计算公式分析/运算</p>
 * <p>Description:表内</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class ReportInnerFormulaOperation 
{

	private Connection conn;
	private TnameBo tbo;
	private String lExpr;
	private String rExpr;
	private int flag;
	private String reportPrefix;
	private String whereSQL;

	// 右表达式分析
	private String fSource; // 表达式
	private String cError; // 错误信息
	private int nMaxFactorNum;// 最大因子
	private String SQL_SUM; // 表达式结果
	private int tok; // 因子具体类型
	private String token; // 因子
	private int token_type; // 因子基本类型
	private int nCurPos; // 字符定位标识
	private int nFSourceLen; // 表达式长度
	private String excludeexpr;// 排除行或列
	private UserView userView;

	/**
	 * 表内计算公式分析/运算构造器
	 * 
	 * @param lExpr
	 *            左表达式
	 * @param rExpr
	 *            右表达式
	 * @param tbo
	 *            报表封装类
	 * @param flag
	 *            行列公式标识 1 行 2 列
	 * @param reportPrefix
	 *            操作报表的前缀tb / tt_
	 * @param whereSQL
	 *            操作表的限制条件 根据用户名或填报单位编码区分所操作数据
	 */
	public ReportInnerFormulaOperation(Connection conn, String lExpr,
			String rExpr, TnameBo tbo, int flag, String reportPrefix,
			String whereSQL) {
		this.conn = conn;
		this.lExpr = lExpr;
		this.rExpr = rExpr;
		this.tbo = tbo;
		this.flag = flag;
		this.reportPrefix = reportPrefix;
		this.whereSQL = whereSQL;
	}

	// **************************************表内计算公式的添加和修改***********************************************//
	/**
	 * 表内特定计算公式分析效验
	 * 
	 * @param lExpr
	 *            左表达式
	 * @param rExpr
	 *            右表达式
	 * @param tbo
	 *            报表封装类
	 * @param flag
	 *            行列公式标识 1 行 2 列
	 * 
	 */
	public ReportInnerFormulaOperation(TnameBo tbo, String lExpr, String rExpr,
			int flag) {
		this.lExpr = lExpr;
		this.rExpr = rExpr;
		this.tbo = tbo;
		this.flag = flag;
	}

	/**
	 * 增加修改表内计算公式时的合法性效验
	 * 
	 * @return
	 */
	public String reportInnerFormulaCheck() throws GeneralException{
		StringBuffer result = new StringBuffer();
		String temp = this.lExprAnalyse();
		if (temp != null) {// 左表达式错误
			result.append(temp);
			return result.toString();
		} else {
			// 校验右表达式
			// 支持月(起始日期)或月(截止日期)
			// String temprexpr = this.rExpr.replace("月(起始日期)",
			// "C"+Constant.Month_tformula).replace("月(截止日期)",
			// "C"+Constant.Month_tformula);
			parseFormula();
			if (this.run(this.rExpr)) { // xieguiquan 20101102
										// 把this.rExpr改为temprexpr
			} else {
				result.append(this.cError);
			}
		}

		if (result == null || "".equals(result.toString())) {
			return "ok";
		} else {
			return result.toString();
		}
	}

	// *********************************表内计算公式的添加和修改END****************************************************//

	/**
	 * 表内计算公式分析/计算
	 * 
	 * @return 错误信息
	 */
	public String reportInnerFormulaAnalyse() throws GeneralException{
		StringBuffer result = new StringBuffer();
		SQL_Util su = new SQL_Util();
		String temp = this.lExprAnalyse();
		if (temp != null) {// 左表达式错误
			result.append(temp);
			return result.toString();
		} else {
			// 校验右表达式
			// 支持月(起始日期)或月(截止日期)
			// String temprexpr = this.rExpr.replace("月(起始日期)",
			// "C"+Constant.Month_tformula).replace("月(截止日期)",
			// "C"+Constant.Month_tformula);
			parseFormula();
			if (this.run(this.rExpr)) { // xieguiquan 20101102
										// 把this.rExpr改为temprexpr
				// 执行update语句
				boolean b = this.executeUpdateSQL(this.createuUpdateSQL(su
						.sqlSwitch(this.SQL_SUM)));
				if (b) {
					// 执行完毕
					return null;
				} else {

				}
			} else {
				result.append(this.cError);
			}
		}

		if (result == null || "".equals(result.toString())) {
			return "null";
		} else {
			return result.toString();
		}

	}

	// 获得一个基本因子
	public boolean getToken() {
		boolean result = true;
		tok = 0;
		token = "";
		token_type = 0;

		// 如果表达式长度为0 即：空表达式
		if (nFSourceLen == 0) {
			result = false;
			return result;
		}

		// 如果当前 字符位=表达式长度那么结束即：字符已取完
		if (nCurPos == nFSourceLen) {
			tok = Constant.S_FINISHED;
			token_type = Constant.DELIMITER;
			return result;
		}

		// 空白字符处理
		while (Character.isSpaceChar(fSource.charAt(nCurPos))) {
			nCurPos++;
		}

		// 如果当前 字符位=表达式长度那么结束即：字符已取完
		if (nCurPos == nFSourceLen) {
			tok = Constant.S_FINISHED;
			token_type = Constant.DELIMITER;
			return result;
		}

		// 分割符处理
		if ("+-*/()[]：".indexOf(fSource.charAt(nCurPos)) != -1) {
			char c = fSource.charAt(nCurPos);
			token = String.valueOf(c);
			switch (c) {
			case '+':
				tok = Constant.S_PLUS;
				break;
			case '-':
				tok = Constant.S_MINUS;
				break;
			case '*':
				tok = Constant.S_TIMES;
				break;
			case '/':
				tok = Constant.S_DIVISION;
				break;
			case '(':
				tok = Constant.S_LPARENTHESIS;
				break;
			case ')':
				tok = Constant.S_RPARENTHESIS;
				break;
			case '[':
				tok = Constant.S_L;
				break;
			case ']':
				tok = Constant.S_R;
				break;
			}
			token_type = Constant.DELIMITER;
			nCurPos++;
			return result;
		}

		// 数值处理
		if (Character.isDigit(fSource.charAt(nCurPos))) {
			char c = fSource.charAt(nCurPos);
			token = String.valueOf(c);
			nCurPos++;
			// 需要位置标识验证
			if (nCurPos == nFSourceLen) {
			} else {
				while (Character.isDigit(fSource.charAt(nCurPos))) {
					token += String.valueOf(fSource.charAt(nCurPos));
					nCurPos++;
					if (nCurPos == nFSourceLen) {
						break;
					}
				}
			}
			token_type = Constant.INT;
			return result;
		}

		// 处理常量
		if (fSource.charAt(nCurPos) == 'C' || fSource.charAt(nCurPos) == 'c') {
			token = String.valueOf(fSource.charAt(nCurPos));
			nCurPos++;
			if (nCurPos == nFSourceLen) {
			} else {
				while (Character.isDigit(fSource.charAt(nCurPos))
						|| fSource.charAt(nCurPos) == '.') {
					token += String.valueOf(fSource.charAt(nCurPos));
					nCurPos++;
					if (nCurPos == nFSourceLen) {
						break;
					}
				}
			}
			token_type = Constant.CONSTANT;
			return result;
		}

		result = false;
		nCurPos++;
		this.setError(Constant.E_ErrorNumber);// 非法字符出现
		return result;
	}

	public boolean level0() throws GeneralException, NumberFormatException{
		boolean result = false;

		if (!level1()) {
			return result;
		}

		// 如果因子是加或减
		while (tok == Constant.S_PLUS || tok == Constant.S_MINUS) {
			SQL_SUM += token;
			if (!this.getToken()) {
				return result;
			}
			if (!level1()) {
				return result;
			}
		}

		result = true;
		return result;
	}

	public boolean level1() throws GeneralException, NumberFormatException{
		boolean result = false;

		if (!level2()) {
			return result;
		}

		// 如果因子是乘或除
		while (tok == Constant.S_TIMES || tok == Constant.S_DIVISION) {
			SQL_SUM += token;
			if (!this.getToken()) {
				return result;
			}
			if (!level2()) {
				return result;
			}
		}
		result = true;
		return result;
	}

	public boolean level2() throws GeneralException, NumberFormatException{
		boolean result = false;

		// 如果因子是左括号 并且因子类型是分割符
		if (tok == Constant.S_LPARENTHESIS && token_type == Constant.DELIMITER) {
			SQL_SUM += token;
			if (!this.getToken()) {
				return result;
			}
			if (!level0()) {
				return result;
			}
			SQL_SUM += token;
			if (tok != Constant.S_RPARENTHESIS) {
				this.putBack();
				this.setError(Constant.E_LOSSRPARENTHESE);
				return result;
			}
			if (!this.getToken()) {
				return result;
			}

		} else if (tok == Constant.S_MINUS && token_type == Constant.DELIMITER) {// 负号处理
			// 负号后面只能是常量，括号,数值
			SQL_SUM += token;
			if (SQL_SUM.endsWith("--")) {
				SQL_SUM = SQL_SUM.substring(0, SQL_SUM.length() - 2);
				SQL_SUM += "+";
			}
			if (!this.getToken()) {
				return result;
			}
			if (tok == Constant.S_LPARENTHESIS
					|| token_type == Constant.CONSTANT
					|| token_type == Constant.INT) {
				if (!level0()) {
					return result;
				}
			} else {
				this.putBack();
				this.setError(Constant.E_SYNTAX);
				return result;
			}
			
		//中括号	JinChunhai 2012.12.28 表内行列计算公式支持对单元格取值运算
		} else if(tok == Constant.S_L && token_type == Constant.DELIMITER){
			//获取[]之间的表达式
			StringBuffer temp = new StringBuffer();
			for(int i= nCurPos ; i< this.nFSourceLen ; i++){
				if(this.fSource.charAt(i)==']'){
					break;
				}
				temp.append(this.fSource.charAt(i));
				nCurPos++;
			}
			
			if(nCurPos == nFSourceLen ){
				tok = Constant.S_FINISHED;
				token_type = Constant.DELIMITER;
				this.setError(Constant.E_SYNTAX);
				return result;
			}	
			nCurPos++;
			
			//验证规则是否成立[行：列]
			if(this.checkRepostString(temp.toString()))
			{
				//拆分字符串
				String expr = temp.toString();
				int n = expr.indexOf(":" , 0);
				String rowExpr = expr.substring(0,n);
				String colExpr = expr.substring(n+1,expr.length());
				//针对拆分的字符进行处理，M表示截止日期的月份，Q表示截止日期的季度
			//	rowExpr=parseFormula(rowExpr);
			//	colExpr=parseFormula(colExpr);
				
			//	if(this.isExistReport(this.tbo , tabid1))
				{
					TnameBo tb = new TnameBo(this.conn,this.tbo.getTabid());
					boolean flag =false;					
					try{
						Integer.parseInt(rowExpr);
					}catch(Exception e){
						this.cError = rowExpr +"为非法字符！";
						throw new GeneralException(this.cError);
					}
					try{
						Integer.parseInt(colExpr);
					}catch(Exception e){
						this.cError =  colExpr +"为非法字符！";
						throw new GeneralException(this.cError);
					}
					if(Integer.parseInt(rowExpr) > tb.getMaxRowNumber(tb.getRowMap())){
						//行不存在
						this.cError = "tb" + this.tbo.getTabid() +ResourceFactory.getProperty("reportspacecheck.bz") + rowExpr +ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
						flag=true;
						throw new GeneralException(this.cError);
					}
					if(Integer.parseInt(colExpr) > tb.getMaxRowNumber(tb.getColMap())){
						//列不存在
						this.cError = "tb" + this.tbo.getTabid() +ResourceFactory.getProperty("reportspacecheck.bz") + colExpr +ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
						flag=true;
						throw new GeneralException(this.cError);
					}
					if(!flag)
					{
						int r = Integer.parseInt((String)tb.getRowMap().get(rowExpr))+1;
						int c = Integer.parseInt((String)tb.getColMap().get(colExpr))+1;
						
						StringBuffer sql = new StringBuffer();
						sql.append(" ( select C");
						sql.append(c);
						sql.append(" from ");
						sql.append(this.reportPrefix);
						sql.append(this.tbo.getTabid());
						sql.append(" where secid=");
						sql.append(r);
						sql.append(this.whereSQL);
						sql.append(" )");						
						this.SQL_SUM += Sql_switcher.isnull(sql.toString(),"0");
					}else{
						this.SQL_SUM += Sql_switcher.isnull("0","0");
					}					
				}
			}else{
				//语法错误
				this.setError(Constant.E_SYNTAX);
				return result;
			}			
			if(!this.getToken()){
				return result;
			}
						
		}else {
			if (!this.primitive()) {
				return result;
			}
		}
		result = true;
		return result;
	}

	/**
	 * 判断表间效验[ : ]格式是否正确
	 * @param str
	 * @return
	 */
	public boolean checkRepostString(String str){
		 Pattern p = Pattern.compile("\\d+\\:([^:\\[])+");
		 Matcher m = p.matcher(str);
		 boolean b = m.matches();
		 return b;
	}
	
	public boolean primitive() {
		boolean result = false;
		int nToken = 0;
		if (token_type == Constant.INT) {
			if (token.trim().length() > 10) {
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}
			nToken = Integer.parseInt(token);
			// 因子合法性检测
			if (nToken > nMaxFactorNum) {
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}

			if (this.flag == 1) {// 行
				// secid从1开始所以加1
				if(tbo.getRowMap().get(this.token)==null){//dml 过滤不存在行列
					if(this.flag==1) {
                        this.cError="右表达式"+this.token+"行不是计算行！请以正确行列形式输入！";
                    } else{
						this.cError="右表达式"+this.token+"列不是计算列！请以正确行列形式输入！";
					}
					return result;
				}
				int row = Integer
						.parseInt((String) (tbo.getRowMap().get(token))) + 1;
				StringBuffer sql = new StringBuffer();
				sql.append("( select CN from ");
				sql.append(this.reportPrefix);
				sql.append(tbo.getTabid());
				sql.append(" where secid = ");
				sql.append(row);
				sql.append(this.whereSQL);
				sql.append(" )");

				SQL_SUM += sql.toString();
			} else if (this.flag == 2) {// 列
				// 列集合封装的是针对与二维数组数据从0开始的
				if(tbo.getColMap().get(token)==null){//dml 过滤不存在行列
					this.cError="右表达式"+this.token+"列不是计算列！请以正确行列形式输入！";
					return result;
				}
				int col = Integer
						.parseInt((String) (tbo.getColMap().get(token))) + 1;
				SQL_SUM += "C" + col;
			}

			if (!this.getToken()) {
				return result;
			}

		} else if (token_type == Constant.CONSTANT) {
			// 如果因子长度等于1那么元素只有‘C’
			if (token.length() == 1) {
				this.setError(Constant.E_LOSSCONSTANT);
				return result;
			} else if (token.endsWith(".")) {
				this.setError(Constant.E_CONSTANT);
				return result;
			} else {
				// DB中字段名
				SQL_SUM += token.substring(1, token.length());
				if (!this.getToken()) {
					return result;
				}
			}
		} else {
			this.putBack();
			this.setError(Constant.E_SYNTAX);
			return result;
		}

		result = true;
		return result;
	}

	public boolean putBack() {
		nCurPos -= token.length();
		return true;
	}

	/**
	 * 验证公式是否正确 获取SQL_SUM
	 * 
	 * @param fSource
	 *            表达式
	 * @param nMaxFactorNum
	 *            最大因子
	 * @return true/false
	 */
	public boolean run(String fSource) throws GeneralException{
		boolean result = false;

		this.fSource = fSource.trim();
		if (this.flag == 1) {
			this.nMaxFactorNum = this.tbo.getMaxRowNumber(this.tbo.getRowMap());
		} else if (this.flag == 2) {
			this.nMaxFactorNum = this.tbo.getMaxRowNumber(this.tbo.getColMap());
		}

		this.nFSourceLen = fSource.length();
		this.nCurPos = 0;
		this.SQL_SUM = "";
		this.cError = "";
		if (this.fSource == null || "".equals(this.fSource)) {
			this.setError(Constant.E_NOTEMPTY); // 空表达式
			return result;
		}
		if (!this.getToken()) { // 如果取因子失败
			this.setError(Constant.E_ErrorNumber); // 非法字符出现
			return result;
		}

		if (!this.level0()) {
			return result;
		}
		this.putBack();
		if (nCurPos != nFSourceLen) {
			this.setError(Constant.E_SYNTAX);
			return result;
		}

		result = true;
		return result;
	}

	/**
	 * 
	 * @param num
	 */
	public void setError(int num) {
		if (Constant.E_NOTEMPTY == num) {
			this.cError = ResourceFactory.getProperty("constant.e_notempty");
		} else if (Constant.E_LOSSLPARENTHESE == num) {
			this.cError = ResourceFactory
					.getProperty("constant.e_losslparenthese");
		} else if (Constant.E_LOSSRPARENTHESE == num) {
			this.cError = ResourceFactory
					.getProperty("constant.e_lossrparenthese");
		} else if (Constant.E_SYNTAX == num) {
			this.cError = ResourceFactory.getProperty("constant.e_syntax");
		} else if (Constant.E_MUSTBEINTEGER == num) {
			this.cError = ResourceFactory
					.getProperty("constant.e_mustbeinteger");
		} else if (Constant.E_FACTORNOEXIST == num) {
			String temp = ResourceFactory.getProperty("rowcheckanalyse.col");
			if (this.flag == 1) {
				temp = ResourceFactory.getProperty("colcheckanalyse.row");
			}
			this.cError = temp
					+ ResourceFactory.getProperty("constant.e_factornoexist");
		} else if (Constant.E_LOSSCONSTANT == num) {
			this.cError = ResourceFactory
					.getProperty("constant.e_lossconstant");
		} else if (Constant.E_ErrorNumber == num) {
			this.cError = ResourceFactory.getProperty("constant.e_errornumber");
		} else if (Constant.E_CONSTANT == num) {
			this.cError = ResourceFactory
					.getProperty("colcheckanalyse.defineConstantError")
					+ "!";
		}

		if (nCurPos <= 200) {
			this.cError = ResourceFactory.getProperty("colcheckanalyse.rexpr")
					+ " \\'" + fSource.substring(0, nCurPos) + "\\'"
					+ ResourceFactory.getProperty("colcheckanalyse.zhong")
					+ ExprUtil.getLNumber(fSource.substring(0, nCurPos))
					+ this.cError;
		} else {
			this.cError = ResourceFactory.getProperty("colcheckanalyse.rexpr")
					+ " \\'" + fSource.substring(nCurPos - 200, nCurPos)
					+ "\\'"
					+ ResourceFactory.getProperty("colcheckanalyse.zhong")
					+ ExprUtil.getLNumber(fSource.substring(0, nCurPos))
					+ this.cError;
		}
	}

	/**
	 * 验证表内计算公式的左表达式
	 * 
	 * @return
	 */
	public String lExprAnalyse() {
		String message = null;
		if (this.lExpr == null || "".equals(this.lExpr.trim())) {
			message = ResourceFactory.getProperty("colcheckanalyse.nolexpr")
					+ "！";
		} else if (this.lExpr.trim().length() > 10) {// 写死？
			if (this.flag == 1) {// 行
				message = ResourceFactory.getProperty("colcheckanalyse.lexpr")
						+ lExpr
						+ ResourceFactory
								.getProperty("colcheckanalyse.rowNoExist")
						+ "！";
			} else if (this.flag == 2) {// 列
				message = ResourceFactory.getProperty("colcheckanalyse.lexpr")
						+ lExpr
						+ ResourceFactory
								.getProperty("colcheckanalyse.colNoExist")
						+ "！";
			}

		} else {
			if (!this.lExpr.matches("[0-9]+")) {
				message = ResourceFactory
						.getProperty("colcheckanalyse.lexprError");
			} else {
				if (this.flag == 1) {// 行
					if ((Integer.parseInt(lExpr) > this.tbo
							.getMaxRowNumber(this.tbo.getRowMap()))) {
						message = ResourceFactory
								.getProperty("colcheckanalyse.lexpr")
								+ lExpr
								+ ResourceFactory
										.getProperty("colcheckanalyse.rowNoExist")
								+ "！";
					}else{
						if(this.tbo.getRowMap().get(this.lExpr)==null){//dml 过滤不存在行列
							message ="左表达式第"+this.lExpr+"行不是有效行！";
						}
					}
				} else if (this.flag == 2) {// 列
					if ((Integer.parseInt(lExpr) > this.tbo
							.getMaxRowNumber(this.tbo.getColMap()))) {
						message = ResourceFactory
								.getProperty("colcheckanalyse.lexpr")
								+ lExpr
								+ ResourceFactory
										.getProperty("colcheckanalyse.colNoExist")
								+ "！";
					}else{
						if(this.tbo.getColMap().get(this.lExpr)==null){//dml 过滤不存在行列
							message ="左表达式第"+this.lExpr+"列不是有效列！";
						}
					}
				}
			}
		}
		return message;
	}

	/**
	 * 创建表内计算公式最终SQL语句
	 * 
	 * @param tmepSql
	 *            校验SQL片段
	 * @return
	 */
	public String createuUpdateSQL(String tmepSql) {
		StringBuffer updatasql = new StringBuffer();
		if (tmepSql == null || "".equals(tmepSql)) {
			return null;
		}
		if (this.flag == 1) { // 行-SQL模板

			if (Sql_switcher.searchDbServer() == 2) // oracle
			{
				tmepSql = " ( select "
						+ tmepSql
						+ " CN from "
						+ this.reportPrefix
						+ this.tbo.getTabid()
						+ " where secid ="
						+ (Integer.parseInt((String) this.tbo.getRowMap().get(
								this.lExpr)) + 1) + this.whereSQL + " ) ";
			}

			//updatasql.append("update ");
			//updatasql.append(this.reportPrefix);
			//updatasql.append(this.tbo.getTabid());
			updatasql.append(" CN = ");
			updatasql.append(tmepSql);
			//updatasql.append(" where secid =");
			//updatasql.append(Integer.parseInt((String) this.tbo.getRowMap()
					//.get(this.lExpr)) + 1);
			//updatasql.append(this.whereSQL);

		} else if (this.flag == 2) {// 列-SQL语句
			updatasql.append("update ");
			updatasql.append(this.reportPrefix);
			updatasql.append(this.tbo.getTabid());
			updatasql.append(" set c");
			updatasql.append(Integer.parseInt((String) this.tbo.getColMap()
					.get(this.lExpr)) + 1);
			updatasql.append("=");
			updatasql.append(tmepSql);
			updatasql.append(" where secid not in (");

			// 甲行处理
			StringBuffer jia = new StringBuffer();
			String temp = this.tbo.getRowSerialNo();
			if (temp == null || "".equals(temp)) {
				updatasql.append("0");
				if (this.excludeexpr != null && this.excludeexpr.length() > 0) {
					if (!this.excludeexpr.startsWith(",")) {
						this.excludeexpr = "," + this.excludeexpr;
					}
					if (this.excludeexpr.endsWith(",")) {
						this.excludeexpr = this.excludeexpr.substring(0,
								this.excludeexpr.length() - 1);
					}
				}
				if (this.excludeexpr != null && this.excludeexpr.length() > 0) {
					String expr2[] = this.excludeexpr.split(",");
					for (int m = 0; m < expr2.length; m++) {
						if (expr2[m] != null && expr2[m].length() > 0) {
							// 实际的列数值
							String n = (String) this.tbo.getRowMap().get(
									expr2[m]);
							if (n == null) {

							} else {
								updatasql.append(",");
								updatasql.append(Integer.parseInt(n) + 1);
							}
						}
					}
				}
				updatasql.append(")");
			} else {
				// System.out.println("temp1=" + temp );
				if (temp.charAt(temp.length() - 1) == ',') {
					temp = temp.substring(0, temp.length() - 1);
				}
				// System.out.println("temp2=" + temp );

				String[] tt = temp.split(",");
				for (int i = 0; i < tt.length; i++) {
					// 实际的列数值
					if (tt[i] != null && tt[i].length() > 0) {
						String n = "";
						if ("0".equals(tt[i])) {
							n = "0";
						} else {
                            n = (String) this.tbo.getRowMap().get(tt[i]);
                        }
						if (n != null) {
							jia.append(Integer.parseInt(n) + 1);
							jia.append(",");
						}
					}
				}
				jia.deleteCharAt(jia.toString().length() - 1);
				if (this.excludeexpr != null && this.excludeexpr.length() > 0) {
					if (!this.excludeexpr.startsWith(",")) {
						this.excludeexpr = "," + this.excludeexpr;
					}
					if (this.excludeexpr.endsWith(",")) {
						this.excludeexpr = this.excludeexpr.substring(0,
								this.excludeexpr.length() - 1);
					}
				}
				if (this.excludeexpr != null && this.excludeexpr.length() > 0) {
					String expr2[] = this.excludeexpr.split(",");
					for (int m = 0; m < expr2.length; m++) {
						if (expr2[m] != null && expr2[m].length() > 0) {
							// 实际的列数值
							String n = (String) this.tbo.getRowMap().get(
									expr2[m]);
							if (n != null) {
								jia.append(",");
								jia.append(Integer.parseInt(n) + 1);
							}
						}
					}
				}
				updatasql.append(jia.toString());
				updatasql.append(")");
				updatasql.append(this.whereSQL);
			}

		}
		return updatasql.toString();
	}

	/**
	 * 执行表内计算SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public boolean executeUpdateSQL(String sql) {
		boolean b = false;
		ContentDAO dao = new ContentDAO(this.conn);

		// System.out.println("sql=" + sql );

		// System.out.println("flag=" + this.flag) ;
		StringBuffer bigSql=new StringBuffer();
		boolean tempflag =true;//先更新行或列记录
		if(rExpr.indexOf("/")!=-1&&rExpr.substring(0,rExpr.indexOf("/")).equals(lExpr))//add by xiegh on 20170926 bug:31837
        {
            tempflag = false;
        }
		if (this.flag == 1) { // 行计算公式
			int i = 1;
			bigSql.append("update ");
			bigSql.append(this.reportPrefix);
			bigSql.append(this.tbo.getTabid());
			bigSql.append(" set ");
			this.tbo.getColMap().keySet();
			int max =0;
			for(Iterator iter=this.tbo.getColMap().keySet().iterator();iter.hasNext();){
				String colmax=(String)iter.next();
				
				if(Integer.parseInt(colmax)>max) {
                    max = Integer.parseInt(colmax);
                }
			}
			while (true) {

				// 实际的列数值
				String n = (String) this.tbo.getColMap().get(String.valueOf(i));
				if (n == null) {

					i++;
					if (i > max) {
						break;
					} else {
                        continue; // xgq 20101101 break 改为 continue 排除行或列
                    }

				} else {
					// 替换SQL模板中的‘CN’
					int nc = Integer.parseInt(n) + 1;
					String col = "C" + nc + " ";//wangcq 2014-12-30增加空格标志位，防止行公式在计算时由于填报单位中包含CN影响sql语句
					String rcsql = sql.replaceAll("CN ", col);
					bigSql.append(rcsql);
					bigSql.append(",");
					// System.out.println("rcsql=" + rcsql);
					try {
						if(tempflag){
							java.util.Iterator it = this.tbo.getColMap().entrySet().iterator();
							String tabids = "";
							StringBuffer sb = new StringBuffer();
							while (it.hasNext()) {
								Map.Entry entry = (Map.Entry) it.next();
								String value = (String) entry.getValue();
								value = Integer.parseInt(value)+1+"";
								sb.append("C"+value+"=0,");
							}
							if(sb.length()>0){
								String sqls = "update " +this.reportPrefix
								+ this.tbo.getTabid()+" set "+sb.toString().substring(0,sb.toString().length()-1)
								+ " where secid ="
								+ (Integer.parseInt((String) this.tbo.getRowMap().get(
										this.lExpr)) + 1) + this.whereSQL + "  ";;
										dao.update(sqls);
							}
							
							tempflag= false;
						}
						
						dao.update( "update " +this.reportPrefix+ this.tbo.getTabid()+" set "+rcsql+" where secid = "+(Integer.parseInt((String) this.tbo.getRowMap().get(this.lExpr)) + 1)+this.whereSQL );

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				i++;
				b = true;
			}
			
//			bigSql.setLength(bigSql.length()-1);
//			bigSql.append(" where secid = ");
//			bigSql.append((Integer.parseInt((String) this.tbo.getRowMap().get(this.lExpr)) + 1));
//			bigSql.append(this.whereSQL);
			try{
				//System.out.println(bigSql.toString());
				//dao.update(bigSql.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
			// break;
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
			this.tbo.autoUpdateDigitalResults(operateObject, "1", ""+(Integer.parseInt((String) this.tbo.getRowMap()
					.get(this.lExpr)) + 1), "", this.tbo.getTabid(),userView.getUserName(),unitcode);	
		} else if (this.flag == 2) {// 列计算公式
			try {
				if(tempflag){
					String tabids = "";
					StringBuffer sb = new StringBuffer();
				
					int n =	Integer.parseInt((String) this.tbo.getColMap()
								.get(this.lExpr)) + 1;
						String sqls = "update " +this.reportPrefix
						+ this.tbo.getTabid()+" set C"+n
						+ "=0 where   1=1 "+this.whereSQL ;
						String excludstr ="";
						if (this.excludeexpr != null && this.excludeexpr.length() > 0) {
							String expr2[] = this.excludeexpr.split(",");
							for (int m = 0; m < expr2.length; m++) {
								if (expr2[m] != null && expr2[m].length() > 0) {
									// 实际的列数值
									String a = (String) this.tbo.getRowMap().get(
											expr2[m]);
									if (a != null) {
										excludstr+=Integer.parseInt(a) + 1+",";
									}
								}
							}
						}
						if(excludstr.length()>1){
							excludstr= " and (secid not in ("+excludstr.substring(0,excludstr.length()-1)+"))";
							sqls+= excludstr;
						}
								dao.update(sqls);
					
					tempflag= false;
				}
				dao.update(sql);
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
				this.tbo.autoUpdateDigitalResults(operateObject, "2", "", "c"+(Integer.parseInt((String) this.tbo.getColMap()
						.get(this.lExpr)) + 1), this.tbo.getTabid(),userView.getUserName(),unitcode);
				b = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	// 处理公式
	public void parseFormula() {
		// 校验右表达式
		// 支持月(起始日期)或月(截止日期)
		RowSet rs= null;
		if (this.rExpr != null && this.rExpr.indexOf("月") != -1) {
			ContentDAO dao = new ContentDAO(this.conn);
			String xml = "";
			String userName = userView.getUserName();
			try {
				// 常量表中查找rp_param常量
				 rs = dao
						.search("select STR_VALUE  from CONSTANT where CONSTANT='RP_PARAM'");
				if (rs.next()) {
					xml = Sql_switcher.readMemo(rs, "STR_VALUE");

					// xml文件分析类
					AnalyseParams aps = new AnalyseParams(xml);

					String appdate = ""; // 截止日期
					String startdate = ""; // 启始日期

					boolean flag = true;
					if(!this.userView.isSuper_admin()){
						if(this.userView.getFuncpriv().indexOf(",29011,")==-1) {
                            flag =false;
                        }
					}
					if (aps.checkUserid(userName)&&flag) {// DB中存在当前用户的扫描库配置信息


						// 用户配置信息封装在MAP内
						HashMap hm = aps.getAttributeValues(userName);
						appdate = (String) hm.get("appdate");// 截止日期
						startdate = (String) hm.get("startdate"); // 起始日期

					}else{
						if(ConstantParamter.getAppdate(this.userView.getUserName())!=null)
						{
							String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
							appdate=value;
							startdate=value;
						}
					}
					int app = 0;
					int start = 0;
					if (appdate != null && appdate.length() > 7) {
                        app = Integer.parseInt(appdate.substring(5, 7));
                    }
					if (startdate != null && startdate.length() > 7) {
                        start = Integer.parseInt(startdate.substring(5, 7));
                    }
					if (start != 0) {
						this.rExpr = this.rExpr.replace("月(起始日期)", "C" + start);
					}
					if (app != 0) {
						this.rExpr = this.rExpr.replace("月(截止日期)", "C" + app);
					}

				}else{
					if(ConstantParamter.getAppdate(this.userView.getUserName())!=null)
					{
						String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
						
					
					int app = 0;
					int start = 0;
					if (value != null && value.length() > 7) {
                        app = Integer.parseInt(value.substring(5, 7));
                    }
					if (value != null && value.length() > 7) {
                        start = Integer.parseInt(value.substring(5, 7));
                    }
					if (start != 0) {
						this.rExpr = this.rExpr.replace("月(起始日期)", "C" + start);
					}
					if (app != 0) {
						this.rExpr = this.rExpr.replace("月(截止日期)", "C" + app);
					}
					}
				}
				rs.close();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			finally{
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

	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public String getExcludeexpr() {
		return excludeexpr;
	}

	public void setExcludeexpr(String excludeexpr) {
		this.excludeexpr = excludeexpr;
	}
	
}
