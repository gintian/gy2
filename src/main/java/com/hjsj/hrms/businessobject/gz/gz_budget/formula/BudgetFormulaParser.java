package com.hjsj.hrms.businessobject.gz.gz_budget.formula;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: 预算表计算公式分析/执行</p>
 * <p>Description:</p>
 * <p>Company: hjsj</p>
 * <p>Create time: 2013.2.20</p> 
 * @author genglz
 * @version 1.0 
 */

public class BudgetFormulaParser 
{
	private Connection conn;
	private UserView userView;
	private BudgetFormulaBo formula;
	private int budgetId=0;
	private String orgId="";
	private BudgetFormulaResBo res;

	// 表达式分析
	private String expr;    // 表达式
	private int nExprLen;   // 表达式长度
	private int token_type; // 因子基本类型
	private int tok;        // 因子具体类型
	private String token;   // 因子
	private String tabExpr = "";  // 表公式定义
	private String rowExpr = "";
	private String colExpr = "";
	private Object tabObj = null;
	private String rowId = "";
	private String colId = "";
	private int nCurPos;    // 字符定位标识
    private boolean divFlag=false;  // 给 / 的除数加零判断  只有效一次
    private int divLevel=0;         // 处理 / 后带括号的情况，divFlag为TRUE有divLevel时，遇左+1, 遇右-1
	private String cError;  // 错误信息
	private String sqlExpr; // SQL表达式结果
	private static String REPLACE_VAL = "__VAL__";  // 方便用指标val_1,val_2...进行替换
	private ArrayList tokens = new ArrayList();
	private HashMap tabAlias = new HashMap();  // 子查询别名

	//表达式的基本元素类别
	private static int DELIMITER = 1; // 分隔符
	private static int IDENT     = 5;   // 标识符: 表格区域[]   
	private static int CONSTANT  = 6;  // 常数
	
	//分隔符类别
	private static int S_PLUS = 4;         //加
	private static int S_MINUS = 5;        //减
	private static int S_TIMES = 6;        //乘
	private static int S_DIVISION = 7;     //除
	private static int S_LPARENTHESIS = 20;//左小括号
	private static int S_RPARENTHESIS = 21;//右小括号
	private static int S_FINISHED = 24;    //结束符
	private static int S_LSQUARE = 26;     //左中括号
	private static int S_RSQUARE = 27;     //右中括号
	private static int S_SUM = 28;         //..运算符
	
	//异常类型
	private static int E_NOTEMPTY=1;        //表达式为空
	private static int E_IDENTNOEXIST=2;    //未知标识符
	private static int E_TABNOEXIST=3;      //未知子集
	private static int E_ROWNOEXIST=4;      //未知行
	private static int E_COLNOEXIST=5;      //未知列
	private static int E_LOSSLPARENTHESE=6; //缺少左小括号
	private static int E_LOSSRPARENTHESE=7; //缺少右小括号
	private static int E_LOSSLSQUARE=8;     //缺少左中括号
	private static int E_LOSSRSQUARE=9;     //缺少右中括号
	private static int E_SYNTAX=10;          //语法错误
	private static int E_ErrorChar=11;      //非法字符
	private static int E_ErrorTableRange=12;//表行、列、格公式格式错误
	private static int E_ErrorSum=13;       //..操作符使用错误
	
	private class Token
	{
		int token_type = 0;  // 因子基本类型
		int tok = 0;         // 因子具体类型
		String token = "";   // 因子

		String tabExpr = ""; // 表公式定义
		Object tabObj = null;// 预算表对象
		String rowExpr = "";
		String rowId = "";   // 行代码
		String colExpr = "";
		String colId = "";   // 列指标编号
		String getDataTabName(){
			if(tabObj instanceof FieldSet)
				return ((FieldSet)tabObj).getFieldsetid();
			else if(tabObj instanceof BudgetTabBo)
				return ((BudgetTabBo)tabObj).getDataTableName();
			else {
				return null;
			}
		}
		String getTabId(){
			if(tabObj instanceof FieldSet)
				return ((FieldSet)tabObj).getFieldsetid();
			else if(tabObj instanceof BudgetTabBo)
				return String.valueOf(((BudgetTabBo)tabObj).tabId);
			else {
				return null;
			}
		}
	}	
	
	/**
	 * 预算表计算公式分析/执行构造器
	 * 
	 */
	public BudgetFormulaParser(Connection conn, UserView userView, BudgetFormulaResBo res) {
		this.conn = conn;
		this.userView = userView;
		this.res = res;
	}

	/**
	 * 公式合法性效验
	 * 
	 * @return true校验通过，false校验失败
	 */
	public boolean verify(BudgetFormulaBo formula){
		init(formula, 0, "");
		if(!parse()){
			return false;
		}
		return true;
	}
	
	/**
	 * 执行公式
	 * 
	 * @return true成功, false失败
	 */
	public boolean run(BudgetFormulaBo formula, int budgetId, String orgId){
		init(formula, budgetId, orgId);
		if(!parse()) {
			return false;
		}
		if(!run0()){
			return false;
		}

		return true;
	}
	
	/**
	 * 返回公式校验和执行的错误信息
	 * @return 错误信息
	 */
	public String getError(){
		return cError;
	}
	
	private void init(BudgetFormulaBo formula, int budgetId, String orgId){
		expr = formula.getExpr();
		nExprLen = expr.length();
		this.formula = formula;
		this.budgetId = budgetId;
		this.orgId = orgId;
		
		nCurPos = 0;
		sqlExpr = "";
		cError = "";
		tabExpr = "";
		tabObj = null;
		rowExpr = "";
		rowId = "";
		colExpr = "";
		colId = "";
		divFlag = false;
		divLevel = 0;
		tokens.clear();
		tabAlias.clear();
	}

	/** 获得一个基本因子 */
	private boolean getToken() {
		tok = 0;
		token = "";
		token_type = 0;

		// 如果表达式长度为0 即：空表达式
		if (nExprLen == 0) {
			this.setError(E_NOTEMPTY);
			return false;
		}

		// 如果当前 字符位=表达式长度那么结束即：字符已取完
		if (nCurPos >= nExprLen) {
			tok = S_FINISHED;
			token_type = DELIMITER;
			return true;
		}

		// 空白字符处理
		while (isSpace(expr.charAt(nCurPos))) {
			nCurPos++;
		}

		// 如果当前 字符位=表达式长度那么结束即：字符已取完
		if (nCurPos == nExprLen) {
			tok = S_FINISHED;
			token_type = DELIMITER;
			return true;
		}

		// 分割符处理
		if ("+-*/()".indexOf(expr.charAt(nCurPos)) != -1) {
			char c = expr.charAt(nCurPos);
			token = String.valueOf(c);
			switch (c) {
			case '+':
				tok = S_PLUS;
				break;
			case '-':
				tok = S_MINUS;
				break;
			case '*':
				tok = S_TIMES;
				break;
			case '/':
				tok = S_DIVISION;
				break;
			case '(':
				tok = S_LPARENTHESIS;
				break;
			case ')':
				tok = S_RPARENTHESIS;
				break;
			case '[':
				tok = S_LSQUARE;
				break;
			case ']':
				tok = S_RSQUARE;
				break;
			}
			token_type = DELIMITER;
			nCurPos++;
			return true;
		}
		
		// 标识符: 表格区域
		if ("[]".indexOf(expr.charAt(nCurPos)) != -1) {
			// 获取[]之间的表达式
			if(expr.charAt(nCurPos) == ']'){
				this.setError(E_SYNTAX);
				return false;
			}
			
			nCurPos++;
			if(nCurPos == nExprLen){
				this.setError(E_SYNTAX);
				return false;
			}	

			StringBuffer s = new StringBuffer();
			boolean hasRSquare=false;
			for(int i= nCurPos; i< this.nExprLen; i++){
				if(expr.charAt(i)!='*'&&isDelimiter(expr.charAt(i))||expr.charAt(i)=='[')
					break;
				if(this.expr.charAt(i)==']'){
					hasRSquare = true;
					break;
				}
				s.append(this.expr.charAt(i));
				nCurPos++;
			}
			if(!hasRSquare){
				setError(E_LOSSRSQUARE);
				return false;
			}
			
			nCurPos++;
			if(!parseTableRange(s.toString()))
			{
				return false;
			}
			token = "["+s.toString()+"]";
			token_type = IDENT;
			return true;
		}		

		// 常数处理
		if (Character.isDigit(expr.charAt(nCurPos))) {
			char c = expr.charAt(nCurPos);
			token = String.valueOf(c);
			nCurPos++;
			// 需要位置标识验证
			if (nCurPos == nExprLen) {
				// 
			} else {
				while (Character.isDigit(expr.charAt(nCurPos))||expr.charAt(nCurPos)=='.') {
					token += String.valueOf(expr.charAt(nCurPos));
					nCurPos++;
					if (nCurPos == nExprLen) {
						break;
					}
				}
			}
			token_type = CONSTANT;
			return true;
		}
		
		// ..处理
		if (expr.charAt(nCurPos)=='.') {
			nCurPos++;
			if((nCurPos == nExprLen)||(expr.charAt(nCurPos)!='.')){
				this.setError(E_SYNTAX);
				return false;
			}
			
			token_type = DELIMITER;
			tok = S_SUM;
			token = "..";
			nCurPos++;
			return true;
		}
		if(isChinese(expr.charAt(nCurPos))||isAlpha(expr.charAt(nCurPos))){
			this.setError(E_LOSSLSQUARE);
			return false;
		}
		
		nCurPos++;
		this.setError(E_ErrorChar);// 非法字符出现
		return false;
	}
	
	/**
	 * 是不是汉字
	 * @param c
	 * @return
	 */
	private boolean isChinese(char c) {
		return (int) c > 127;
	}

	/**
	 * 是不是数字
	 * @param c
	 * @return
	 */
	private boolean isDigit(char c) {
		return "0123456789".indexOf("" + c) >= 0;
	}
	
	/**
	 * 判断是否是字符
	 */
	private boolean isAlpha(char c) {
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
				.indexOf(c) >= 0;
	}
	
	/**
	 * 是否分隔符
	 */ 
	private boolean isDelimiter(char c) {
		boolean b = "+-*/()\n\t\r ".indexOf(c) >= 0;
		return b;
	}
	
	/**
	 * 是否可忽略的空白内容
	 */ 
	private boolean isSpace(char c) {
		boolean b = "\n\t\r ".indexOf(c) >= 0;
		return b;
	}
	
	/**
	 * 是否是月份指标: 1月...12月
	 * @param fldname
	 * @return
	 */
	private boolean isBudgetMonthCol(String fldname) {
		if(fldname.toUpperCase().startsWith("VAL_")){
			String month=fldname.toUpperCase().replaceAll("VAL_", "");
			if(extractDigit(month)!=null)
				return true;
		}
		return false;
	}
	
	private void addToken(){
		if(tokens.size()>0){
			boolean isSum = ((Token)tokens.get(tokens.size()-1)).tok==S_SUM;
			if(isSum)
				expendSumExpr();
		}
		
		Token t = new Token();
		t.token_type = this.token_type;
		t.tok = this.tok;
		t.token = this.token;
		if(t.token_type == IDENT){
			t.tabExpr = this.tabExpr;
			t.tabObj = this.tabObj;
			t.rowExpr = this.rowExpr;
			t.rowId = this.rowId;
			t.colExpr = this.colExpr;
			t.colId = this.colId;
		}
		tokens.add(t);
		
		if(token_type==IDENT){
			addFldExpr(t, colId);
		}
		else{
			if(tok!=S_SUM)
				sqlExpr += token;
		}

	}
	
	/**
	 * 取出数字部分
	 * @param s
	 * @return
	 */
	private String extractDigit(String s) {
		String d="";
		for(int i=0;i<s.length();i++){
			if(isDigit(s.charAt(i)))
				d+=s.charAt(i);
		}
		return d.length()>0?d:null;
	}
	
	private void expendSumExpr() {
		Token t1=(Token)tokens.get(tokens.size()-2);
		int month1 = Integer.parseInt(extractDigit(t1.colExpr));
		int month2 = Integer.parseInt(extractDigit(this.colExpr));
		month1++; month2--;
		if(month2<month1){ 
			sqlExpr+="+";
			return;
		}
		for(int m=month1;m<=month2;m++){
			sqlExpr += "+";
			addFldExpr(t1, "val_"+m);
		}
		sqlExpr+="+";
	}
	
	/**
	 * 执行公式
	 * @return
	 */
	private boolean run0(){
		// 生成一个SQL并执行
		String destTab=getFormulaDataTabName();
		if(destTab == null){
			cError = ResourceFactory.getProperty("gz.budget.formula.calcfail");
			return false;
		}		
		String updateCols=formula.getUpdateColRange();
		if(updateCols == null){
			cError = ResourceFactory.getProperty("gz.budget.formula.calcfail");
			return false;
		}
		String[] fldarr = updateCols.split(",");
		if(fldarr.length==0){
			cError = ResourceFactory.getProperty("gz.budget.formula.calcfail");
			return false;
		}
		/*
		if (Sql_switcher.searchDbServer() == 1) {
		    String setflds="";
		    for(int i=0;i<fldarr.length;i++){
		        if(setflds.length()>0)
		            setflds+=",";
		        setflds+= destTab+"."+ fldarr[i]+"="+sqlExpr.replaceAll(REPLACE_VAL, fldarr[i]);
		    }
		    String srcTab=getSrcTab();
		    String destCond=getDestCond();
		    sql="update "+destTab+" set "+setflds+"\n"+
		    " from "+srcTab+"\n"+
		    " where "+destCond;
		    
		}
		else {
		    String srcFld="";
		    String destFld="";
		    for(int i=0;i<fldarr.length;i++){
		        if(destFld.length()>0)
		            destFld+=",";
		        destFld+= destTab+"."+ fldarr[i];
		    }
		    for(int i=0;i<fldarr.length;i++){
		        if(srcFld.length()>0)
		            srcFld+=",";
		        srcFld+= sqlExpr.replaceAll(REPLACE_VAL, fldarr[i]);
		    }
		    srcFld= srcFld.replace(destTab+".", "A.");
		    String srcTab=getSrcTab();
            String destCond=getDestCond();
            sql="update "+destTab+" set ("+destFld+") = ("
            +"select "+srcFld +" from (select * from "+srcTab+" where "+destCond+") A  "
            +" where a.itemid="+destTab+".itemid) "+
            " where "+destCond;
		    
		}
		
		*/
		String sql="";
		String setflds="";
		for(int i=0;i<fldarr.length;i++){
			if(setflds.length()>0)
				setflds+=",";
			setflds+= destTab+"."+ fldarr[i]+"="+sqlExpr.replaceAll(REPLACE_VAL, fldarr[i]);
		}
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			if(Sql_switcher.searchDbServer()==1){		
				String srcTab=getSrcTab();
				String destCond=getDestCond();
				sql="update "+destTab+" set "+setflds+"\n"+
				" from "+srcTab+"\n"+
				" where "+destCond;			
		    }
			else {
				String srcTab=getSrcTabOracle();
				String destCond=getDestCond();
				if(destTab.equalsIgnoreCase(srcTab)){		
					sql="update "+destTab+" set "+setflds +				
					" where "+destCond;
				}
				else {
					DbWizard dbw = new DbWizard(this.conn);
					String tmpTab="budf_tmp_"+this.userView.getUserName();
					for(int i=0;i<fldarr.length;i++){
						String srcFlds="";
						String destFlds="";
						String strFlds=getTmpTableFlds();
						strFlds=strFlds.replaceAll("\\*", fldarr[i]);					
						dbw.dropTable(tmpTab);
						dbw.createTempTable(srcTab,tmpTab,strFlds,"","");
					
						destFlds= destTab+"."+ fldarr[i];
						if (sqlExpr.indexOf(REPLACE_VAL)>-1){
							srcFlds=sqlExpr.replaceAll("\\."+REPLACE_VAL, "HJSJ");//先替换为其他字符，为了避免下面替换点的时候将此类特殊字段替换
							srcFlds=srcFlds.replaceAll("\\.", "");//将临时表A0.等替换成临时表 实际字段
							srcFlds=srcFlds.replaceAll("HJSJ","\\."+REPLACE_VAL);
							srcFlds=srcFlds.replaceAll("\\."+REPLACE_VAL, fldarr[i]);
							srcFlds=srcFlds.replaceAll(REPLACE_VAL, fldarr[i]);
						}
						else {
							srcFlds=sqlExpr.replaceAll("\\*", fldarr[i]);					
							srcFlds=srcFlds.replaceAll("\\.", ""); 
						}
						
						sql="update "+destTab+" set ("+destFlds +")=( select "+srcFlds+" from "
						+tmpTab+" where budget_id="+destTab+".budget_id"
						+" and b0110="+destTab+".b0110"
						+" and tab_id="+destTab+".tab_id"
						+" and itemid="+destTab+".itemid"
						+")"
						+ " where "+destCond;						
						dao.update(sql);						
					}
					sql="";
				}
			}
		
		
			if (sql.length()>0)
			  dao.update(sql);
		}
		catch (Exception e) {
			e.printStackTrace();
			cError = ResourceFactory.getProperty("gz.budget.formula.calcfail");
			return false;
		}
		return true;
	}
	/**
	 * 获取临时表名称 oralce
	 **/
	private String getTmpTableFlds(){
       String strflds="";
	   String srcTab=getFormulaDataTabName();
	   strflds=srcTab+".budget_id,"+srcTab+".tab_id,"+srcTab+".b0110,"+srcTab+".itemid";
		for(int i=0;i<tokens.size();i++){
			Token t=(Token)tokens.get(i);
			if(t.token_type == IDENT){
				String tabAlias=getTabAlias(t);
				strflds=strflds+","+tabAlias+"."+t.colId+ " as "+tabAlias+t.colId;
			}
		}		
	   
       return strflds;
	}
		
	private String getSrcTabOracle() {
		/*   From SC03 Left Join BB2 A2
                 On SC03.budget_id = A2.BB201 and SC03.B0110 = A2.B0110 
              left join (select * from SC02 where tab_id=3 and itemid='90') A3
                 On SC03.budget_id = A3.budget_id and SC03.B0110 = A3.B0110
              left join (select * from SC02 where tab_id=3) A4
                 On SC03.budget_id = A4.budget_id and SC03.B0110 = A4.B0110 and SC03.itemid = A4.itemid  */
		String destTab=getFormulaDataTabName();
		String srcTab=destTab;
		ArrayList tabs = new ArrayList();
		tabs.add(destTab);
		if(tabAlias.size()>0){
			for(int i=0;i<tokens.size();i++){
				Token t=(Token)tokens.get(i);
				if(t.token_type == IDENT){
					String tabAlias=getTabAlias(t);
					if(tabs.contains(tabAlias))
						continue;
					tabs.add(tabAlias);
					String subquery="";
					if(res.getFieldSetYSZE().getFieldsetid().equals(t.getDataTabName())||
							res.getFieldSetParam().getFieldsetid().equals(t.getDataTabName())){
						// left join BB2 A2 On SC03.budget_id = A2.BB201 and SC03.B0110 = A2.B0110 
						subquery=t.getDataTabName()+" "+tabAlias+ 
						" on "+destTab+"."+getBudgetIdFldName(destTab)+"="+tabAlias+"."+getBudgetIdFldName(t.getDataTabName())+
						" and "+destTab+".B0110="+tabAlias+".B0110";
					}
					else{
						//   left join (select * from SC02 where tab_id=3 and itemid='90') A8
						//      On SC03.budget_id = A8.budget_id and SC03.B0110 = A8.B0110
						//   left join (select * from SC02 where tab_id=3) A9
						//      On SC03.budget_id = A9.budget_id and SC03.B0110 = A9.B0110 and SC03.itemid = A9.itemid
						subquery="(select * from "+t.getDataTabName()+" where tab_id="+t.getTabId();
						if(!"*".equals(t.rowId))
							subquery+=" and "+getBudgetItemFldName(t.getDataTabName())+"='"+t.rowId+"'";
						subquery+=") "+tabAlias+
						" on "+destTab+"."+getBudgetIdFldName(destTab)+"="+tabAlias+"."+getBudgetIdFldName(t.getDataTabName())+
						" and "+destTab+".B0110="+tabAlias+".B0110";
						if("*".equals(t.rowId))
							subquery+=" and "+destTab+"."+getBudgetItemFldName(destTab)+"="+
							tabAlias+"."+getBudgetItemFldName(t.getDataTabName());
					}
					srcTab+=" left join "+subquery;
				}
			}
		}
		return srcTab;
	}
	
	private String getSrcTab() {
/*   From SC03 Left Join BB2 A2
                 On SC03.budget_id = A2.BB201 and SC03.B0110 = A2.B0110 
              left join (select * from SC02 where tab_id=3 and itemid='90') A3
                 On SC03.budget_id = A3.budget_id and SC03.B0110 = A3.B0110
              left join (select * from SC02 where tab_id=3) A4
                 On SC03.budget_id = A4.budget_id and SC03.B0110 = A4.B0110 and SC03.itemid = A4.itemid  */
		String destTab=getFormulaDataTabName();
		String srcTab=destTab;
		ArrayList tabs = new ArrayList();
		tabs.add(destTab);
		if(tabAlias.size()>0){
			for(int i=0;i<tokens.size();i++){
				Token t=(Token)tokens.get(i);
				if(t.token_type == IDENT){
					String tabAlias=getTabAlias(t);
					if(tabs.contains(tabAlias))
						continue;
					tabs.add(tabAlias);
					String subquery="";
					if(res.getFieldSetYSZE().getFieldsetid().equals(t.getDataTabName())||
						res.getFieldSetParam().getFieldsetid().equals(t.getDataTabName())){
						// left join BB2 A2 On SC03.budget_id = A2.BB201 and SC03.B0110 = A2.B0110 
						subquery=t.getDataTabName()+" "+tabAlias+ 
							" on "+destTab+"."+getBudgetIdFldName(destTab)+"="+tabAlias+"."+getBudgetIdFldName(t.getDataTabName())+
								" and "+destTab+".B0110="+tabAlias+".B0110";
					}
					else{
						//   left join (select * from SC02 where tab_id=3 and itemid='90') A8
						//      On SC03.budget_id = A8.budget_id and SC03.B0110 = A8.B0110
						//   left join (select * from SC02 where tab_id=3) A9
						//      On SC03.budget_id = A9.budget_id and SC03.B0110 = A9.B0110 and SC03.itemid = A9.itemid
						subquery="(select * from "+t.getDataTabName()+" where tab_id="+t.getTabId();
						if(!"*".equals(t.rowId))
							subquery+=" and "+getBudgetItemFldName(t.getDataTabName())+"='"+t.rowId+"'";
						subquery+=") "+tabAlias+
							" on "+destTab+"."+getBudgetIdFldName(destTab)+"="+tabAlias+"."+getBudgetIdFldName(t.getDataTabName())+
								" and "+destTab+".B0110="+tabAlias+".B0110";
						if("*".equals(t.rowId))
							subquery+=" and "+destTab+"."+getBudgetItemFldName(destTab)+"="+
											tabAlias+"."+getBudgetItemFldName(t.getDataTabName());
					}
					srcTab+=" left join "+subquery;
				}
			}
		}
		return srcTab;
	}
	
	private String getDestCond(){
		String destTab=getFormulaDataTabName();
		String destCond=destTab+"."+getBudgetIdFldName(destTab)+"="+budgetId+" and "+destTab+".B0110='"+orgId+"'";
		if(!res.getFieldSetYSZE().getFieldsetid().equals(destTab)&&
			!res.getFieldSetParam().getFieldsetid().equals(destTab))
			destCond += " and "+destTab+".tab_id="+formula.tabID;
		if(formula.getRowCond()!=null){
			destCond += " and "+formula.getRowCond();
		}
		return destCond;
	}
	
	private String getBudgetIdFldName(String dataTabName){
		if(res.getFieldSetYSZE().getFieldsetid().equals(dataTabName)){
			return (String)res.getSysMap().get("ysze_idx_menu");
		}
		else if(res.getFieldSetParam().getFieldsetid().equals(dataTabName)){
			return (String)res.getSysMap().get("ysparam_idx_menu");
		}
		else{
			return "budget_id";
		}
	}
	
	private String getBudgetItemFldName(String dataTabName) {
		if(res.getFieldSetYSZE().getFieldsetid().equals(dataTabName)){
			return null;
		}
		else if(res.getFieldSetParam().getFieldsetid().equals(dataTabName)){
			return null;
		}
		else if(dataTabName.equals(res.getBudgetTabMC().getDataTableName())){
			return res.mcSumField;
		}
		else{
			return "itemid";
		}
	}
	
	private void addFldExpr(Token t, String fld) {
		String tabAlias = getTabAlias(t);
		String s="";
		if("*".equals(fld)){
			s = tabAlias + "." + REPLACE_VAL;
		}
		else{
			s = tabAlias + "." + fld;
		}
        if (divFlag && (divLevel == 0))
        	sqlExpr += "NullIF(" + s + ", 0)";
        else
        	sqlExpr += Sql_switcher.isnull(s,"0");
	}
	
	private String getTabAlias(Token t){
		// 当前表，如：对各个列求和，不需要别名
		if(t.getTabId().equals(getFormulaTabId())&&"*".equals(t.rowId)){
			return t.getDataTabName();
		}
		String key = t.getTabId() +"." + t.rowId;  // 子查询由预算表和行决定，不同的行需要不同的子查询
		String alias = (String)tabAlias.get(key);
		if(alias==null){
			alias = "A"+tabAlias.size();
			tabAlias.put(key, alias);
		}
		return alias;
	}
	
	private String getFormulaDataTabName() {
		BudgetTabBo t=res.findBudgetTab(formula.tabID);
		if(t==null)
			return null;
		if(t==res.getBudgetTabYSZE())
			return res.getFieldSetYSZE().getFieldsetid();
		else {
			return t.getDataTableName();
		}
	}
	
	private String getFormulaTabName(){
		BudgetTabBo t=res.findBudgetTab(formula.tabID);
		if(t==null)
			return null;
		if(t==res.getBudgetTabYSZE())
			return res.getBudgetTabYSZE().tabName;
		else {
			return t.tabName;
		}
	}

	private String getFormulaTabId(){
		BudgetTabBo t=res.findBudgetTab(formula.tabID);
		if(t==null)
			return null;
		if(t==res.getBudgetTabYSZE())
			return res.getFieldSetYSZE().getFieldsetid();
		else {
			return String.valueOf(t.tabId);
		}
	}
	
	/**
	 * 公式分析
	 * 
	 * @return 
	 */
	private boolean parse(){
		boolean result = false;

		if (this.expr == null || "".equals(this.expr)) {
			this.setError(E_NOTEMPTY); // 空表达式
			return result;
		}
		if (!this.getToken()) { // 如果取因子失败
			//this.setError(E_ErrorChar); // 非法字符出现
			return result;
		}

		if (!this.level0()) {
			return result;
		}
		this.putBack();
		if (nCurPos != nExprLen) {
			this.setError(E_SYNTAX);
			return result;
		}

		result = true;
		return result;
	}
	
	private boolean level0(){
		boolean result = false;

		if (!level1()) {
			return result;
		}

		// 如果因子是加或减
		while (tok == S_PLUS || tok == S_MINUS || tok == S_SUM) {
			addToken();
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

	private boolean level1() {
		boolean result = false;

		if (!level2()) {
			return result;
		}

		// 如果因子是乘或除
		while (tok == S_TIMES || tok == S_DIVISION) {
		    if (divLevel == 0)
		    	divFlag = false;
		    if (tok != S_TIMES){
		    	divFlag = true;
		    	//divLevel = 0;
		    }

			addToken();
			if (!this.getToken()) {
				return result;
			}
			if (!level2()) {
				return result;
			}
		    if (divLevel == 0)
		    	divFlag = false;
		}
		result = true;
		return result;
	}

	private boolean level2() {
		boolean result = false;

		// 如果因子是左括号 并且因子类型是分割符
		if (tok == S_LPARENTHESIS && token_type == DELIMITER) {
		    if (divFlag){
		      if (divLevel == 0)  // 除数为0时转为null
		        sqlExpr += "NullIF(";
		      divLevel++;
		    };
			addToken();
			if (!this.getToken()) {
				return result;
			}
			if (!level0()) {
				return result;
			}
			addToken();
			if (tok != S_RPARENTHESIS) {
				this.putBack();
				this.setError(E_LOSSRPARENTHESE);
				return result;
			}
			if(divFlag){
				divLevel--;
				if (divLevel == 0)
					sqlExpr += ", 0)";
			}
		
			if (!this.getToken()) {
				return result;
			}			
		} else if (tok == S_MINUS && token_type == DELIMITER) {// 负号处理
			// 负号后面只能是常量,括号,标识符
			addToken();
			/*if (sqlExpr.endsWith("--")) {// "--" -> "+"
				sqlExpr = sqlExpr.substring(0, sqlExpr.length() - 2);
				sqlExpr += "+";
			}*/
			if (!this.getToken()) {
				return result;
			}
			if (tok == S_LPARENTHESIS || token_type == CONSTANT	|| token_type == IDENT) {
				if (!level0()) {
					return result;
				}
			} else {
				this.putBack();
				this.setError(E_SYNTAX);
				return result;
			}
/*		} else if(tok == S_LSQUARE && token_type == DELIMITER){
			// 中括号: 表格区域
			// 获取[]之间的表达式
			StringBuffer temp = new StringBuffer();
			for(int i= nCurPos ; i< this.nExprLen ; i++){
				if(this.expr.charAt(i)==']'){
					break;
				}
				temp.append(this.expr.charAt(i));
				nCurPos++;
			}
			
			if(nCurPos == nExprLen ){
				tok = S_FINISHED;
				token_type = DELIMITER;
				this.setError(E_SYNTAX);
				return result;
			}	
			nCurPos++;
			
			if(!parseTableRange(temp.toString()))
			{
				this.setError(E_ErrorTableRange);
				return result;
			}			
			if(!this.getToken()){
				return result;
			}
			*/			
		}else {
			if (!this.primitive()) {
				return result;
			}
		}
		result = true;
		return result;
	}
	
	private boolean primitive() {
		boolean result = false;
		if (token_type == IDENT) {
			// 检查..操作符
			if ((tokens.size()>0)&&(((Token)tokens.get(tokens.size()-1)).tok==S_SUM)){
				if(!checkSum()){
					this.setError(E_ErrorSum);
					return result;
				}
			}
			addToken();
			if (!this.getToken()) {
				return result;
			}

		} else if (token_type == CONSTANT) {
			addToken();
			if (!this.getToken()) {
				return result;
			}
		} else {
			this.putBack();
			this.setError(E_SYNTAX);
			return result;
		}

		result = true;
		return result;
	}
	
	/**
	 * 分析表格区域公式[]
	 * @param expr
	 * @return
	 */
	private boolean parseTableRange(String expr){
		// 行,列：[ROW],[COL],[TAB.ROW],[TAB.COL],
		// 格：[ROW:COL],[TAB.ROW:COL],[TAB.ROW:*],[TAB.*:*]
		tabExpr = "";
		rowExpr = "";
		colExpr = "";
		tabObj = null;
		rowId = "";
		colId = "";
		
		int saveCurPos=nCurPos;
		int startPos=nCurPos-(expr+"]").length();
		int tabEndPos=nCurPos;
		int rowEndPos=nCurPos;
		int colEndPos=nCurPos;
		
		if(expr.length()==0){
			setError(E_ErrorTableRange);
			return false;
		}

		if((expr.indexOf('.',0)!=-1)&&(expr.indexOf(':',0)!=-1)){
			int n=expr.indexOf(".", 0);
			String s="";
			if (n!=-1){
				tabExpr = expr.substring(0,n);
				tabEndPos = startPos + tabExpr.length();
				s = expr.substring(n+1,expr.length());
				n = s.indexOf(":", 0);
				rowExpr = s.substring(0,n);
				rowEndPos = tabEndPos +".".length()+ rowExpr.length();
				colExpr = s.substring(n+1,s.length());
				colEndPos = rowEndPos +":".length()+ colExpr.length();
			}			
		}
		else if(expr.indexOf('.',0)!=-1){
			int n=expr.indexOf(".", 0);
			if (n!=-1){
				tabExpr = expr.substring(0,n);
				tabEndPos = startPos + tabExpr.length();
				if(formula.rowColFlag==formula.BUDGET_FORMLAFLAG_ROW)
				{
					rowExpr = expr.substring(n+1,expr.length());
					rowEndPos = tabEndPos + ".".length() + rowExpr.length();
					colExpr = "*";
				}
				else{
					rowExpr = "*";
					colExpr = expr.substring(n+1,expr.length());
					colEndPos = tabEndPos + ".".length() + colExpr.length();
				}
			}
		}
		else if(expr.indexOf(':',0)!=-1){
			int n=expr.indexOf(":", 0);
			if (n!=-1){
				rowExpr = expr.substring(0,n);
				rowEndPos = startPos + rowExpr.length();
				colExpr = expr.substring(n+1,expr.length());
				colEndPos = rowEndPos + ":".length() + colExpr.length();
			}			
		}
		else {
			if(formula.rowColFlag==formula.BUDGET_FORMLAFLAG_ROW){
				rowExpr = expr;
				rowEndPos = startPos + rowExpr.length();
				colExpr = "*";
			}
			else{
				rowExpr = "*";
				colExpr = expr;
				colEndPos = startPos + colExpr.length();
			}
		}

		if(tabExpr.length()==0)
			tabExpr = getFormulaTabName();  // 当前表
		if(tabExpr!=null&& tabExpr.length()>0){
			// 检查表名称
			tabObj = findBudgetTab(tabExpr);
			if(tabObj==null){
				nCurPos=tabEndPos;
				setError(E_TABNOEXIST);
				return false;
			}
		}
		else{
			setError(E_TABNOEXIST);
			return false;
		}
		if(rowExpr!=null&& rowExpr.length()>0){
			if("*".equals(rowExpr)){
				rowId = "*";
			}
			else{
				// 检查行名称或代码
				String code = findCodeItem(tabObj, rowExpr);
				if(code==null){
					nCurPos=rowEndPos;
					setError(E_ROWNOEXIST);
					return false;
				}
				rowId = code;
			}
		}
		if(colExpr!=null && colExpr.length()>0){
			if("*".equals(colExpr)){
				colId = "*";
			}
			else{
				// 检查列名或编号
				FieldItem f = findBudgetFieldItem(colExpr);
				if(f==null){
					nCurPos=colEndPos;
					setError(E_COLNOEXIST);
					return false;
				}
				colId = f.getItemid();
			}
		}
		// 名册只能使用合计行，行列不能为*
		if(tabObj instanceof BudgetTabBo){
			if(((BudgetTabBo)tabObj).tabType == BudgetTabBo.TAB_TYPE_MC){
				if("*".equals(colId)){
					nCurPos=colEndPos;
					setError(E_ErrorTableRange);
					return false;
				}
				if("*".equals(rowId)){
					nCurPos=rowEndPos;
					setError(E_ErrorTableRange);
					return false;
				}			
			}
		}
		nCurPos = saveCurPos;
		return true;
	}
	
	/**
	 * 查找预算表
	 * @param tab
	 * @return
	 */	
	private Object findBudgetTab(String tab) {
		if(tab.equalsIgnoreCase(res.getBudgetTabYSZE().tabName))
			return res.getFieldSetYSZE();
		if(tab.equalsIgnoreCase(res.paramSetName))
			return res.getFieldSetParam();
		BudgetTabBo tabBo = res.findBudgetTabByName(tab);
		if(tabBo != null)
			return tabBo;
		
		return null;
	}
	
	/**
	 * 从总额、参数、名册、用工计划表、预算表中查找指标, 不包括人员库指标
	 * @param item
	 * @return
	 */
	private FieldItem findBudgetFieldItem(String item) {
		FieldItem f=null;
		f=findFieldItem(res.getZEItemList(), item, true);
		if(f==null)
			f=findFieldItem(res.getParamItemList(), item, true);
		if(f==null)
			f=findFieldItem(res.getMCItemList(), item, true);
		if(f==null)
			f=findFieldItem(res.getYgjhItemList(), item, true);
		if(f==null)
			f=findFieldItem(res.getYsbItemList(), item, true);
		
		if(f==null)
			f=findFieldItem(res.getZEItemList(), item, false);
		if(f==null)
			f=findFieldItem(res.getParamItemList(), item, false);
		if(f==null)
			f=findFieldItem(res.getMCItemList(), item, false);
		if(f==null)
			f=findFieldItem(res.getYgjhItemList(), item, false);
		if(f==null)
			f=findFieldItem(res.getYsbItemList(), item, false);
		return f;
	}
	
	/**
	 * 
	 * @param fieldList
	 * @param item
	 * @param descFind true根据名称查找, false根据编号查找
	 * @return
	 */
	private FieldItem findFieldItem(ArrayList fieldList, String item, boolean descFind){
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem f = (FieldItem) fieldList.get(i);
			if(!descFind&&f.getItemid().equalsIgnoreCase(item)||descFind&&f.getItemdesc().equalsIgnoreCase(item)){
				return f;
			}
		}
		return null;
	}
	
	/**
	 * 查找行代码
	 * @param code
	 * @param descFind
	 * @return
	 */
	private String findCodeItem(Object tabObj, String code) {
		CodeItem c=null;
		BudgetTabBo t=null;
		if(tabObj==null){
			t = res.findBudgetTab(formula.tabID);
		}
		else{
			if(tabObj instanceof BudgetTabBo){
				t=(BudgetTabBo)tabObj;
			}
		}
		if(t!=null){
			if(t.tabType==t.TAB_TYPE_MC&&code.equals(res.mcSumValue))
				return res.mcSumValue;
			c = t.findCodeItem(code, true);
			if(c==null)
				c = t.findCodeItem(code, false);			
		}
		return c!=null?c.getCodeitem():null;
	}

	private boolean checkSum(){
		if(tokens.size()<2)
			return false;
		Token t1=(Token)tokens.get(tokens.size()-2);
		if(t1.token_type!=IDENT)
			return false;
		if(t1.tabExpr.equals(this.tabExpr)&&t1.rowExpr.equals(this.rowExpr)&&
			isBudgetMonthCol(t1.colId)&&isBudgetMonthCol(this.colId)){
			try{
				String col1=extractDigit(t1.colExpr);
				String col2=extractDigit(this.colExpr);
				if(col1!=null && col2!=null && Integer.parseInt(col2)>Integer.parseInt(col1)){
					return true;
				}
			}
			catch(Exception e){
				return false;
			}
		}
		return false;
	}

	private boolean putBack() {
		nCurPos -= token.length();
		return true;
	}

	/**
	 * 设置错误信息
	 * @param num
	 */
	private void setError(int num) {
		if (E_NOTEMPTY == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_notempty");
		} else if (E_LOSSLPARENTHESE == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_losslparenthese");
		} else if (E_LOSSRPARENTHESE == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_lossrparenthese");
		} else if (E_LOSSLSQUARE == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_losslsquare");
		} else if (E_LOSSRSQUARE == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_lossrsquare");
		} else if (E_SYNTAX == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_syntax");
		} else if (E_IDENTNOEXIST == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_identnoexist");
		} else if (E_TABNOEXIST == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_tabnoexist");
		} else if (E_ROWNOEXIST == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_rownoexist");
		} else if (E_COLNOEXIST == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_colnoexist");
		} else if (E_ErrorChar == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_errorchar");
		} else if (E_ErrorTableRange == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_error_tablerange");
		} else if (E_ErrorSum == num) {
			this.cError = ResourceFactory.getProperty("gz.budget.formula.parser.e_errorsum");			
		}
		
		if (nCurPos <= 200) {
			this.cError = expr.substring(0, nCurPos) + "^^^^" + cError;
		} else {
			this.cError = expr.substring(nCurPos - 200, nCurPos) + "^^^^" + cError;
		}
	}	
}
