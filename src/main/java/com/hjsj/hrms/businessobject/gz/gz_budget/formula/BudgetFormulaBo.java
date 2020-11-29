package com.hjsj.hrms.businessobject.gz.gz_budget.formula;

public class BudgetFormulaBo implements Cloneable {
	
	public static int BUDGET_FORMLATYPE_INPUT = 1;     	// 录入项
	public static int BUDGET_FORMLATYPE_CALC = 2;     	// 计算项
	public static int BUDGET_FORMLATYPE_IMPORT = 3;		// 导入项

	public static int BUDGET_FORMLAFLAG_ROW = 1;		// 行公式
	public static int BUDGET_FORMLAFLAG_COL = 2;		// 列公式
	
	//  公式Id	
	int formulaID;
	//	公式名称
	String formulaName;
	//	列范围
	String colRange;
	//  update的列范围
	String updateColRange;
	//	计算条件
	String rowRange;
	// 行条件
	String rowCond;
	//	统计条件
	String statRange;
	//	预算表Id
	int tabID;
	//	公式内容
	String expr;
	//	公式类别
	int formulaType;
	//行列标志
	int rowColFlag;
	//	归属单位
	String homeUnits="";
	//	开始月份
	int startMonth=0;
	//  结束月份 : 为实发计算某个月数据而加
	int endMonth=12;
	
	public String getUpdateColRange(){
		// 开始月份为1时，返回所有
		if(startMonth<=1 && endMonth>=12){
			return colRange;
		}		
		String[] colLst = colRange.split(",");
		if (colLst.length==0){
			return null;
		}
		// 月份字段：val_1..val_12 
		StringBuffer colbuf = new StringBuffer();
		for(int i=0; i<colLst.length; i++){
			String col = colLst[i].toLowerCase();
			if ("val_".equals(col.substring(0, 4))){
				try{
				  int m = Integer.parseInt(col.substring(4));
				  
				  // 月份小于开始月份,大于结束月份的忽略
				  if(m < startMonth || m > endMonth){
					  continue;
				  }
				}catch(Exception e){
					; // 不处理
				}				
			}
			if(colbuf.length()!=0){
				colbuf.append(",");
			}
			colbuf.append(colLst[i]);
		}
		if (colbuf.length()==0){
			return null;
		}else
			return colbuf.toString();		
	}
	
	// clone 
	@Override
    public Object clone()
    {
		try{
			return super.clone();
		}catch(CloneNotSupportedException e){
			return null;
		}
	}
	
	public String getFormulaName() {
		return formulaName;
	}
	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
	}
	public String getRowRange() {
		return rowRange;
	}
	public void setRowRange(String rowRange) {
		this.rowRange = rowRange;
	}
	public String getStatRange() {
		return statRange;
	}
	public void setStatRange(String statRange) {
		this.statRange = statRange;
	}
	public int getTabID() {
		return tabID;
	}
	public void setTabID(int i) {
		this.tabID = i;
	}
	public int getFormulaType() {
		return formulaType;
	}
	public void setFormulaType(int formulaType) {
		this.formulaType = formulaType;
	}
	public int getRowColFlag() {
		if (rowColFlag==0)
			rowColFlag = 1;  // 默认行公式
		return rowColFlag;
	}
	public void setRowColFlag(int rowColFlag) {
		this.rowColFlag = rowColFlag;
	}
	public String getHomeUnits() {
		return homeUnits;
	}
	public void setHomeUnits(String homeUnits) {
		this.homeUnits = homeUnits;
	}
	public int getFormulaID() {
		return formulaID;
	}
	public void setFormulaID(int formulaID) {
		this.formulaID = formulaID;
	}
	public String getColRange() {
		return colRange;
	}
	public void setColRange(String colRange) {
		this.colRange = colRange;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getRowCond() {
		return rowCond;
	}

	public void setRowCond(String rowCond) {
		this.rowCond = rowCond;
	}
	

}
