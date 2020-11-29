package com.hjsj.hrms.businessobject.gz.gz_budget.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *类描述：薪资预算公式资源
 *内容：预算参数类、名册、人员库指标等
 *@author: wangjh
 *@date： 日期：Feb 20, 2013 时间：10:24:36 AM
 *@version 1.0
 */
public class BudgetFormulaResBo {
	/** 基数的A0000， 每个人员库+个数*基数，用于排序 */
	public static int BaseA0000 = 2000000;
	/** 合计行的A0000 */
	public static int SumA0000 = 99999999;
	/** 新员工的A0000 */
	public static int NewStaffA0000 = 99999990;
	/** 退休月份指标 */
	public static String RetMonthFld = "retmonth";
	
	/** 新员工姓名 */
	public String NewStaffA0101;
	
	private UserView userView;
	private Connection con;
	private HashMap sysMap;
	
	/** 是否生成退休记录 */
	public boolean isBuildTX;
	// 退休编码
	public String txItemCode;
	
	/** 预算总额信息集 */
	private String setYSZE;
	private FieldSet fieldSetYSZE;
	private BudgetTabBo budgetTabYSZE;
	
	/** 预算参数信息集 */
	private String setParam;
	private FieldSet fieldSetParam;
	/** 预算参数信息集名称(用于公式) */
	public String paramSetName;

	/** 人员库指标 */
	private ArrayList hmDBItemList;
	
	/** 参数指标 */
	private ArrayList paramItemList;
	
	/** 总额指标 */
	private ArrayList zeItemList;
	
	/** 名册指标/子集 */
	private ArrayList mcItemList;
	private FieldSet fieldSetMC;
	private BudgetTabBo budgetTabMC;
	/** 确定名册合计行的指标 */
	public String mcSumField = "A0101";
	/** 确定名册合计行的指标值 */
	public String mcSumValue;
	
	/** 用工计划指标/子集 */
	private ArrayList ygjhItemList;
	private FieldSet fieldSetYGJH;
	private BudgetTabBo budgetTabYGJH;
	
	/** 一般预算表指标 */
	private ArrayList ysbItemList;
	private FieldSet fieldSetYSB;
	
	/** 预算表 */
	private ArrayList budgetTabList;
	
	/** 导入公式中的计算月份参数 */
	public String calcMonth;
	
	/** 取字段中的月份 */
	public int getYsMonthValue(String fieldname) {
		if(fieldname == null || fieldname.length()<5)
			return 0;
		
		if("val_".equalsIgnoreCase(fieldname.substring(0, 4))){
			try{
				int m = Integer.parseInt(fieldname.substring(4));
				return m;
			}catch(Exception e){
			}
		}
		return 0;
	}
	
	public BudgetFormulaResBo(Connection con, UserView userView) {
		this.con = con;
		this.userView = userView;

		setYSZE = (String)getSysMap().get("ysze_set");
		setParam = (String)getSysMap().get("ysparam_set");
		isBuildTX = "1".equals((String)getSysMap().get("createTXrecord"));
		paramSetName = ResourceFactory.getProperty("gz.budget.formula.budgetparam");
		mcSumValue = ResourceFactory.getProperty("gz.budget.formula.mcsum");
		calcMonth = ResourceFactory.getProperty("gz.budget.formula.calcmonth");
		NewStaffA0101 = ResourceFactory.getProperty("gz.budget.newstaff");
		txItemCode = (String)getSysMap().get("txCode");
	}
	
	// 取人员库指标列表
	public ArrayList getDBItemList(){
		if(hmDBItemList==null){
			hmDBItemList = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		}
		return hmDBItemList;
	}

	// 取名册指标列表
	public ArrayList getMCItemList(){
		return getMCItemList(false);
	}

	private boolean mclFlag;
	// 取名册指标列表+参数列表，用于计算
	public ArrayList getMCItemList(boolean includeParam){
		if(mcItemList==null || mclFlag!=includeParam){
			mcItemList = null;
			mcItemList = DataDictionary.getFieldList("SC01", Constant.USED_FIELD_SET);
			mclFlag = false;
		}
		
		if(includeParam){
			mcItemList.addAll(getParamItemList());
			mclFlag = true;			
		}		
		return mcItemList;
	}

	// 取用工计划指标列表
	public ArrayList getYgjhItemList(){
		if(ygjhItemList==null){
			ygjhItemList = DataDictionary.getFieldList("SC02", Constant.USED_FIELD_SET);
		}
		return ygjhItemList;
	}
	
	// 取一般预算表指标列表
	public ArrayList getYsbItemList(){
		if(ysbItemList==null){
			ysbItemList = DataDictionary.getFieldList("SC03", Constant.USED_FIELD_SET);
		}
		return ysbItemList;
	}
	
	// 取总额和参数指标列表
	public ArrayList getParamItemList(){
		if(paramItemList==null){
			// 总额
			paramItemList = DataDictionary.getFieldList(setYSZE, Constant.USED_FIELD_SET);
			// 参数
			paramItemList.addAll(DataDictionary.getFieldList(setParam, Constant.USED_FIELD_SET));
			
			// 计算月份
			FieldItem item = new FieldItem(getSetParam(), "BBBBB");
			item.setItemdesc(calcMonth);
			item.setCodesetid("0");
			item.setItemtype("N");
			item.setItemlength(3);
			item.setDecimalwidth(0);
			paramItemList.add(item);			
		}
		return paramItemList;
	}

	// 取总额指标列表
	public ArrayList getZEItemList(){
		if(zeItemList==null){
			// 总额
			zeItemList = DataDictionary.getFieldList(setYSZE, Constant.USED_FIELD_SET);
		}
		return zeItemList;
	}
	
	public FieldSet getFieldSetYSZE(){
		if(fieldSetYSZE==null)
			fieldSetYSZE = DataDictionary.getFieldSetVo(setYSZE);
		return fieldSetYSZE;
	}
	
	public FieldSet getFieldSetParam(){
		if(fieldSetParam==null)
			fieldSetParam = DataDictionary.getFieldSetVo(setParam);
		return fieldSetParam;
	}
	
	public FieldSet getFieldSetMC(){
		if(fieldSetMC==null)
			fieldSetMC = DataDictionary.getFieldSetVo("SC01");
		return fieldSetMC;
	}

	public FieldSet getFieldSetYGJH(){
		if(fieldSetYGJH==null)
			fieldSetYGJH = DataDictionary.getFieldSetVo("SC02");
		return fieldSetYGJH;
	}

	public FieldSet getFieldSetYSB(){
		if(fieldSetYSB==null)
			fieldSetYSB = DataDictionary.getFieldSetVo("SC03");
		return fieldSetYSB;
	}	
	
	public BudgetTabBo getBudgetTabYSZE(){
		if(budgetTabYSZE==null)
			budgetTabYSZE = findBudgetTabByType(BudgetTabBo.TAB_TYPE_ZE);
		return budgetTabYSZE;
	}
	
	public BudgetTabBo getBudgetTabMC(){
		if(budgetTabMC==null)
			budgetTabMC = findBudgetTabByType(BudgetTabBo.TAB_TYPE_MC);
		return budgetTabMC;
	}
	
	public BudgetTabBo getBudgetTabYGJH(){
		if(budgetTabYGJH==null)
			budgetTabYGJH = findBudgetTabByType(BudgetTabBo.TAB_TYPE_YGJH);
		return budgetTabYGJH;
	}
	
	/**
	 * 加载预算表
	 * @return
	 */
	public ArrayList getBudgetTabList(){
		if(budgetTabList==null){
			budgetTabList = new ArrayList();
			ContentDAO dao = new ContentDAO(con);
			String sql = "select * from gz_budget_tab order by seq";
			RowSet rs = null;
			try{
				rs = dao.search(sql);
				while(rs.next()){
					BudgetTabBo tab = new BudgetTabBo(con, userView);
					tab.loadFromRowSet(rs);
					budgetTabList.add(tab);
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}finally{
	    		try{
	    			if(rs!=null){
	    				rs.close();
	    			}
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
		}
		return budgetTabList;
	}	

	public String getSetYSZE() {
		return setYSZE;
	}

	public String getSetParam() {
		return setParam;
	}

	public HashMap getSysMap() {
		if(sysMap==null){
			BudgetSysBo bo = new BudgetSysBo(this.con, this.userView);
			sysMap = bo.getSysValueMap();
		}
		return sysMap;
	}
	
	/**
	 * 查找预算表
	 * @param tabid
	 * @return
	 */
	public BudgetTabBo findBudgetTab(int tabid) {
		for (int i = 0; i < getBudgetTabList().size(); i++) {
			BudgetTabBo tab = (BudgetTabBo) getBudgetTabList().get(i);
			if(tab.tabId == tabid){
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * 查找预算表
	 * @param tabname
	 * @return
	 */
	public BudgetTabBo findBudgetTabByName(String tabname) {
		for (int i = 0; i < getBudgetTabList().size(); i++) {
			BudgetTabBo tab = (BudgetTabBo) getBudgetTabList().get(i);
			if(tab.tabName.equals(tabname)){
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * 查找预算表
	 * @param tabtype
	 * @return
	 */
	private BudgetTabBo findBudgetTabByType(int tabtype) {
		for (int i = 0; i < getBudgetTabList().size(); i++) {
			BudgetTabBo tab = (BudgetTabBo) getBudgetTabList().get(i);
			if(tab.tabType == tabtype){
				return tab;
			}
		}
		return null;
	}

	public boolean isBuildTX() {
		return isBuildTX;
	}

	public String getTxItemCode() {
		return txItemCode;
	}	
	
}
