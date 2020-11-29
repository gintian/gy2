/**
 * 自动计算
 */
package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.Constant;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:报表自动公式计算分析</p>
 * <p> Description:在有公式的单元格中修改数值关联单元格同时联动</p>
 * <p> Company:hjsj </p>
 * <p> create time:Jun 20, 2006:1:32:37 PM </p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportAutoFormulaAnalyse {

	private int i;
	private int j;
	private TnameBo tbo; // 当前报表类
	private double[][] result;// 数据
	private List formulaList; // 公式集合
	private int[][] digitalResults = null; // 小数点
	private Connection conn;
	private String lExpr;
	private String rExpr;
	private int flag;
	private StringBuffer formulaFlag = new StringBuffer(); // ij下标信息：i,j/i,j
	
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

	/**
	 * 报表自动计算
	 * 用户修改单元格，传入格的下标和数据二维数组和公式集合返回联动后的二维数组集合
	 * @param conn        DB连接
	 * @param i	          数组下标-行
	 * @param j           数组下标-列
	 * @param result[][]  报表对应数据
	 * @param tbo         报表类
	 * @param formulaList 操作的单元格对应的公式集合列表:RecordVo类
	 */
	public ReportAutoFormulaAnalyse(Connection conn, int i, int j, double result[][], TnameBo tbo, List formulaList) {
		this.conn = conn;
		this.i = i;
		this.j = j;
		this.tbo = tbo;
		this.result = result;
		this.formulaList = formulaList;
		this.digitalResults = tbo.getDigitalResults();
/*
		System.out.println("*************公式列表*********************");
		for (int k = 0; k < formulaList.size(); k++) {
			RecordVo vo = (RecordVo) this.formulaList.get(k);
			System.out.println("表计算公式=" + vo.getString("cname"));
			System.out.println("左表达式=" + vo.getString("lexpr"));
			System.out.println("右表达式=" + vo.getString("rexpr"));
		}
		System.out.println("****************************************");
		
		
		for(int ii=0;ii<digitalResults.length;ii++)
		{
			
			for(int jj=0;jj<digitalResults[ii].length;jj++)
			{
				System.out.print("  "+digitalResults[ii][jj]);
			}
			
		}
		*/
		
	}

	/**
	 * 报表自动公式计算
	 * 
	 * @return
	 */
	public String reportAutoFormulaAnalyse() {
		SQL_Util su = new SQL_Util();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			//wangcq 2014-11-15 利用liuy的方法判断是否有甲行和编号列 begin
			ArrayList colInfoBGridList = this.tbo.getColInfoBGrid();
			ArrayList rowInfoBGridList = this.tbo.getRowInfoBGrid();
			boolean colFlag = false;
			boolean rowFlag = false;

			for(int k = 0;k<colInfoBGridList.size();k++){
				RecordVo colVo = (RecordVo) colInfoBGridList.get(k);
				if (colVo.getInt("flag1") == 4){
					colFlag = true;
					break;
				}
			}
			for(int k = 0;k<rowInfoBGridList.size();k++){
				RecordVo rowVo = (RecordVo) rowInfoBGridList.get(k);
				if (rowVo.getInt("flag1") == 4){
					rowFlag = true;
					break;
				}
			}
			//wangcq 2014-11-15 end
			this.formulaFlag.append(String.valueOf(this.i));
			this.formulaFlag.append(",");
			this.formulaFlag.append(String.valueOf(this.j));
			this.formulaFlag.append("/");

			StringBuffer result = new StringBuffer();

			for (int i = 0; i < this.formulaList.size(); i++) {

				RecordVo vo = (RecordVo) this.formulaList.get(i);
				String exceptid="";
				HashMap ham=new HashMap();
				/*3,表间列公式 2,表间行公式 0,表内行公式 1,表内列公式 5,汇总公式 4,表间格公式*/

				if (vo.getInt("colrow") == 0) {// 表内行

					this.lExpr = vo.getString("lexpr");
					if(this.lExpr.indexOf("|")!=-1){

						exceptid=this.lExpr.substring(this.lExpr.indexOf("|")+1);

						this.lExpr=this.lExpr.substring(0,this.lExpr.indexOf("|"));
						String []ex=exceptid.split(",");
						for(int k=0;k<ex.length;k++){
							//add by wangchaoqun on 2014-11-15 如果横表栏上编号不存在，排除列相应-1
							if(!rowFlag  && ex[k]!=null && !"".equals(ex[k])) {
                                ex[k] = String.valueOf(Integer.parseInt(ex[k])-1);
                            }
							ham.put(ex[k], ex[k]);
						}
					}
					this.rExpr = vo.getString("rexpr");
					this.flag = 1;

					String temp = this.lExprAnalyse();
					if (temp != null) {// 左表达式错误
						result.append("#");
						result.append(temp);
						result.append("/");
					} else {
						// 公式涉及下标集合
						String[] coltemp = this.getFormulaFlags();
						for (int ii = 0; ii < coltemp.length; ii++) {

							this.getFormulaFlag(coltemp[ii]);
							boolean b = this.run(ExprUtil.getExpr(rExpr));
							if (!b) {
								result.append("#");
								result.append(this.cError);
								result.append("/");
							} else {
								//SQL_SUM内容是变化的，放到循环外有问题
								String sql = "select distinct (" + su.sqlSwitch(this.SQL_SUM )+ ") as value from tsort";
								double value = this.executeUpdateSQL(dao, rs, sql);
								// 行
								String rownum = (String) this.tbo.getRowMap().get(this.lExpr);
								// 小数点控制
								int n = this.digitalResults[Integer.parseInt(rownum)][this.j];
								String v = this.getFormatExprValue(String.valueOf(value), n);
								// 动态更新二维数组
								if(ham.get(String.valueOf(this.j))==null){
									if (n == 0) {
										//this.result[Integer.parseInt(rownum)][this.j] = Integer
										//		.parseInt(v);
										this.result[Integer.parseInt(rownum)][this.j] = Double.parseDouble(v);
									} else {
										this.result[Integer.parseInt(rownum)][this.j] = Double
												.parseDouble(v);
									}
								}else{
									if (n == 0) {
										//this.result[Integer.parseInt(rownum)][this.j] = Integer
										//		.parseInt(v);
										this.result[Integer.parseInt(rownum)][this.j] = Double.parseDouble(v);
									} else {
										this.result[Integer.parseInt(rownum)][this.j] = Double
												.parseDouble(v);
									}
								}

								this.formulaFlag.append(rownum);
								this.formulaFlag.append(",");
								this.formulaFlag.append(this.j);
								this.formulaFlag.append("/");
								if(ham.get(String.valueOf(this.j))==null&&result.indexOf(rownum+","+this.j+","+v+"/")==-1){
									result.append(rownum);
									result.append(",");
									result.append(this.j);
									result.append(",");
									result.append(v);
									result.append("/");
								}else{

								}
							}
						}

					}

				} else if (vo.getInt("colrow") == 1) {// 表内列

					this.lExpr = vo.getString("lexpr");
					if(this.lExpr.indexOf("|")!=-1){
						exceptid=this.lExpr.substring(this.lExpr.indexOf("|")+1);

						this.lExpr=this.lExpr.substring(0,this.lExpr.indexOf("|"));
						String []ex=exceptid.split(",");
						for(int k=0;k<ex.length;k++){
							//add by wangchaoqun on 2014-11-15 如果纵表栏上甲不存在，排除行相应-1
							if(!colFlag  && ex[k]!=null && !"".equals(ex[k])) {
                                ex[k] = String.valueOf(Integer.parseInt(ex[k])-1);
                            }
							ham.put(ex[k], ex[k]);
						}
					}
					this.rExpr = vo.getString("rexpr");
					this.flag = 2;

					String temp = this.lExprAnalyse();
					if (temp != null) {// 左表达式错误
						result.append("#");
						result.append(temp);
						result.append("/");
					} else {
						// 公式下标集合
						String[] rowtemp = this.getFormulaFlags();
						for (int ii = 0; ii < rowtemp.length; ii++) {
							this.getFormulaFlag(rowtemp[ii]);
							boolean b = this.run(ExprUtil.getExpr(rExpr));
							if (!b) {
								result.append("#");
								result.append(this.cError);
								result.append("/");
							} else {
								String sql = "select distinct (" +su.sqlSwitch(this.SQL_SUM ) + ") as value from tsort";
								//System.out.println(sql);
								double value = this.executeUpdateSQL(dao, rs, sql);
								// 列
								String colnum = (String) this.tbo.getColMap().get(this.lExpr);
								// 小数位控制
								int n = this.digitalResults[this.i][Integer.parseInt(colnum)];
								String v = this.getFormatExprValue(String.valueOf(value), n);
								// 动态更新二维数组
								if(ham.get(String.valueOf(this.i))!=null){
									if (n == 0) {
										//this.result[this.i][Integer.parseInt(colnum)] = Integer.parseInt(v);
										this.result[this.i][Integer.parseInt(colnum)] = Double.parseDouble(v);
									} else {
										this.result[this.i][Integer.parseInt(colnum)] = Double.parseDouble(v);
									}
								}else{
									if (n == 0) {
										//this.result[this.i][Integer.parseInt(colnum)] = Integer.parseInt(v);
										this.result[this.i][Integer.parseInt(colnum)] = Double.parseDouble(v);
									} else {
										this.result[this.i][Integer.parseInt(colnum)] = Double.parseDouble(v);
									}
								}

								this.formulaFlag.append(this.i);
								this.formulaFlag.append(",");
								this.formulaFlag.append(colnum);
								this.formulaFlag.append("/");

								if(ham.get(String.valueOf(this.i))==null&&result.indexOf(this.i+","+colnum+","+v+"/")==-1){
									result.append(this.i);
									result.append(",");
									result.append(colnum);
									result.append(",");
									result.append(v);
									result.append("/");
								}

							}
						}
					}

				}

			}// end for

			String temp = result.toString();
			if(result==null||result.length()==0){
				temp="";
			}else {
                temp = temp.substring(0, temp.length() - 1);
            }
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return "";

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
		if ("+-*/()".indexOf(fSource.charAt(nCurPos)) != -1) {
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

	public boolean level0() {
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

	public boolean level1() {
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

	public boolean level2() {
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

		}else if (tok == Constant.S_MINUS && token_type == Constant.DELIMITER) {// 负号处理
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

		} else {
			if (!this.primitive()) {
				return result;
			}
		}
		result = true;
		return result;
	}

	public boolean primitive() {
		boolean result = false;
		int nToken = 0;
		if (token_type == Constant.INT) {
			nToken = Integer.parseInt(token);
			// 因子合法性检测
			if (nToken > nMaxFactorNum) {
				this.setError(Constant.E_FACTORNOEXIST);
				return result;
			}

			if (this.flag == 1) {// 行

				// 行对应二维数组下标
				int row = Integer
						.parseInt((String) (tbo.getRowMap().get(token)));
				double value = this.result[row][this.j];
				SQL_SUM += value;
				//扩展小数位长度为5目的是为了例如：（1/6）*10000的小数位足够长 xgq20110329
				String temp = value+"";
				String temp2= "00000";
				if(temp.indexOf(".")!=-1){
					temp = temp.substring(temp.indexOf(".")+1, temp.length());
					if(temp2.length()>temp.length()){
						temp2= temp2.substring(temp.length());
						SQL_SUM += temp2;
					}
				}

			} else if (this.flag == 2) {// 列

				// 列集合封装的是针对与二维数组数据从0开始的
				int col = Integer
						.parseInt((String) (tbo.getColMap().get(token)));
				double value = this.result[this.i][col];
				SQL_SUM += value;
				//扩展小数位长度为5目的是为了例如：（1/6）*10000的小数位足够长 xgq20110329
				String temp = value+"";
				String temp2= "00000";
				if(temp.indexOf(".")!=-1){
					temp = temp.substring(temp.indexOf(".")+1, temp.length());
					if(temp2.length()>temp.length()){
						temp2= temp2.substring(temp.length());
						SQL_SUM += temp2;
					}
				}

			}

			if (!this.getToken()) {
				return result;
			}

		} else if (token_type == Constant.CONSTANT) {
			// 如果因子长度等于1那么元素只有‘C’
			if (token.length() == 1) {
				this.setError(Constant.E_LOSSCONSTANT);
				return result;
			}else if(token.endsWith(".")){
				this.setError(Constant.E_CONSTANT);
				return result;
			} else {
				//nToken = Integer.parseInt(token.substring(1, token.length()));
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
	public boolean run(String fSource) {
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
		if (!this.getToken()) { // 如果取因子失败
			this.setError(Constant.E_NOTEMPTY); // 空表达式
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
			String temp = ResourceFactory.getProperty("reportinnercheck.col");
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
		}else if(Constant.E_CONSTANT == num){
			this.cError = ResourceFactory.getProperty("colcheckanalyse.defineConstantError")+"!";
		}
		
		if (nCurPos <= 200) {
			this.cError = ResourceFactory.getProperty("colcheckanalyse.rexpr")+" \\'" + fSource.substring(0, nCurPos) + "\\'"
					+ ResourceFactory.getProperty("colcheckanalyse.zhong")
					+ ExprUtil.getLNumber(fSource.substring(0, nCurPos))
					+ this.cError;
		} else {
			this.cError = ResourceFactory.getProperty("colcheckanalyse.rexpr")+" \\'"
					+ fSource.substring(nCurPos - 200, nCurPos) + "\\'"
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
			message = ResourceFactory.getProperty("colcheckanalyse.nolexpr")+"！";
		} else {
			if (!this.lExpr.matches("[0-9]+")) {
				message =  ResourceFactory.getProperty("colcheckanalyse.lexprError");
			} else {
				if (this.flag == 1) {// 行
					if ((Integer.parseInt(lExpr) > this.tbo
							.getMaxRowNumber(this.tbo.getRowMap()))) {
						message =ResourceFactory.getProperty("colcheckanalyse.lexpr") + lExpr + ResourceFactory.getProperty("colcheckanalyse.rowNoExist")+"！";
					}
				} else if (this.flag == 2) {// 列

					if ((Integer.parseInt(lExpr) > this.tbo
							.getMaxRowNumber(this.tbo.getColMap()))) {
						message = ResourceFactory.getProperty("colcheckanalyse.lexpr") + lExpr + ResourceFactory.getProperty("colcheckanalyse.colNoExist")+"！";
					}
				}
			}
		}
		return message;
	}

	public double executeUpdateSQL(ContentDAO dao,RowSet rs,String sql) {
		//	System.out.println("执行的SQL=" + sql);
			double result = 0.0;
			try {
				rs = dao.search(sql);
				if (rs.next()) {
					result = rs.getDouble("value");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return result;
		}	
	/**
	 * 执行表内计算SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public double executeUpdateSQL(String sql) {
	//	System.out.println("执行的SQL=" + sql);
		double result = 0.0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				result = rs.getDouble("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}/*finally{
			   if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			   }

			}*/
		return result;
	}

	/**
	 * 获取规范的表达式的值
	 * 
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return          规范后的值
	 */
	public String getFormatExprValue(String exprValue, int flag) {
		return PubFunc.round(exprValue,flag);
	/*	StringBuffer sb = new StringBuffer();
		if (flag == 0) {
			sb.append("####");
		} else {
			sb.append("####.");
			for (int i = 0; i < flag; i++) {
				sb.append("0");
			}
		}
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		return dstr; */
	}

	/**
	 * 下标集字符串转换为数组
	 * @param str
	 * @return
	 */
	public String[] getFormulaFlags() {
		StringBuffer str = this.formulaFlag;
		if (str == null || "".equals(str.toString())) {
			return null;
		}
		String temp = str.toString();
		if (temp.charAt(temp.length() - 1) == '/') {
			temp = temp.substring(0, temp.length() - 1);
		}
		return temp.split("/");
	}

	/**
	 * 得到要操作的单元格的二维数组下标
	 * @param str
	 */
	public void getFormulaFlag(String str) {
		String[] t = str.split(",");
		this.i = Integer.parseInt(t[0]);
		this.j = Integer.parseInt(t[1]);
	}

}
