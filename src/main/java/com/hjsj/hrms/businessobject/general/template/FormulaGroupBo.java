/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 *<p>Title:公式组</p> 
 *<p>Description:FormulaGroupBo</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-8:下午02:51:11</p> 
 *@author cmq
 *@version 4.0
 */
public class FormulaGroupBo {
	/**计算条件*/
	private String strWhere;
	/**公式组名*/
	private String groupName;
	/**计算公式串*/
	private String formula;
	/**公式列表*/
	private ArrayList formulalist=new ArrayList();
	
	public FormulaGroupBo() {
	
	}
	/**
	 * 分柝计算公式组
	 */
	private void splitFormulas()
	{
		String[] formulaarr=StringUtils.split(formula,"`");
		for(int i=0;i<formulaarr.length;i++)
		{
			int idx=formulaarr[i].indexOf("=");
			String lexpr=formulaarr[i].substring(0, idx);
			String rexpr=formulaarr[i].substring(idx+1);
			LazyDynaBean dynabean=new LazyDynaBean();
			dynabean.set("name", lexpr);
			dynabean.set("lexpr", lexpr);
			dynabean.set("rexpr", rexpr);
			formulalist.add(dynabean);
		}//for i loop end.
	}
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStrWhere() {
		return strWhere;
	}

	public void setStrWhere(String strWhere) {
		this.strWhere = strWhere;
	}

	public ArrayList getFormulalist() {
		return formulalist;
	}

	public void setFormula(String formula) {
		this.formula = formula;
		splitFormulas();
	}

}
