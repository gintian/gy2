package com.hjsj.hrms.businessobject.gz.gz_budget.formula;

import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title: 预算表定义</p>
 * <p>Description:</p>
 * <p>Company: hjsj</p>
 * <p>Create time: 2013.2.22</p> 
 * @author genglz
 * @version 1.0 
 */

public class BudgetTabBo {
	private Connection conn = null;
	private UserView userView = null;
	
	/** 工资总额 */
	static final int TAB_TYPE_ZE = 1;  
	/** 名册 */
	static final int TAB_TYPE_MC = 2;
	/** 用工计划 */
	static final int TAB_TYPE_YGJH = 3;
	/** 一般预算表 */
	static final int TAB_TYPE_YSB = 4;
	
	/** 有效 */
	static final int FLAG_VALID = 1;
	/** 无效 */
	static final int FLAG_INVALID = 0;
	
	/** 预演计划表id */
	int tabId;       
	String tabName; 
	/** 分类 */
	int tabType;    
	String budgetGroup;
	String codeSetId;
	int analyseFlag;
	int bpFlag;
	String tabCode;
	int seq;
	int validFlag;
	Date startDate;
	Date endDate;
	String extAttr;
	private ArrayList codeItemList;
	
	public BudgetTabBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}

	/**
	 * 从数据集加载数据
	 * @param rs
	 */
	public void loadFromRowSet(RowSet rs){
		try {
			tabId = rs.getInt("tab_id");
			tabName = rs.getString("tab_name");
			tabType = rs.getInt("tab_type");
			budgetGroup = rs.getString("budgetGroup");
			codeSetId = rs.getString("codesetid");
			analyseFlag = rs.getInt("analyseFlag");
			bpFlag = rs.getInt("bpFlag");
			tabCode = rs.getString("tabCode");
			seq = rs.getInt("seq");
			validFlag = rs.getInt("validFlag");
			startDate = rs.getDate("start_date");
			endDate = rs.getDate("end_date");
			extAttr = rs.getString("extAttr");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 查找代码
	 * @param code
	 * @param descFind 查找代码值或名称
	 * @return
	 */
	public CodeItem findCodeItem(String code, boolean descFind){
		if(codeSetId==null||codeSetId.length()==0)
			return null;
		if(codeItemList==null)
			codeItemList = AdminCode.getCodeItemList(codeSetId);
		for (int i = 0; i < codeItemList.size(); i++) {
			CodeItem c = (CodeItem) codeItemList.get(i);
			if(!descFind&&c.getCodeitem().equalsIgnoreCase(code)||descFind&&c.getCodename().equalsIgnoreCase(code)){
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 返回数据表名称
	 * @return
	 */
	public String getDataTableName() {
		switch (tabType) {
		case TAB_TYPE_MC:
			return "SC01";
		case TAB_TYPE_YGJH:
			return "SC02";
		case TAB_TYPE_YSB:
			return "SC03";
		}
		return null;
	}

}
