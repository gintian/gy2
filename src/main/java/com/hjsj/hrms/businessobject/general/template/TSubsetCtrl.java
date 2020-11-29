/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hrms.hjsj.sys.FieldItem;

import java.util.ArrayList;

/**
 * <p>Title:TSubsetCtrl</p>
 * <p>Description:子集数据提交</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 19, 20069:45:15 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TSubsetCtrl implements SubSetUpdateType{
	
	private String setcode;
	private String setname;
	
	/** false:子集指标的更新方式   true:子集列表的更新方式    空为默认都起作用*/
	private String subMenu="";
	/**变化后指标列表*/
	private ArrayList fieldlist;
	/**子集记录数据提交方式*/
	private int updatetype=NOCHANGE;
	/**内部调动子集记录数据提交方式*/
	private int innerupdatetype=NOCHANGE;
	private String CondFormula="";
	/** 引入上条记录 */
	private int refPreRec=0;
	
	private StringBuffer strfields=new StringBuffer(",");
	/**
	 * 增加指标
	 * @param item
	 */
	public void addField(FieldItem item)
	{
		String fieldname=(String)item.getItemid();
		if(strfields.indexOf(","+fieldname.toLowerCase()+",")==-1)
		{
			fieldlist.add(fieldname);
			strfields.append(fieldname);
			strfields.append(",");
		}
	}
	
	public void setUpdatetype(int updatetype) {
		this.updatetype = updatetype;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public String getSetcode() {
		return setcode;
	}
	public String getSetname() {
		return setname;
	}
	public int getUpdatetype() {
		return updatetype;
	}
	public TSubsetCtrl() {
		super();
		fieldlist=new ArrayList();
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public void setSetcode(String setcode) {
		this.setcode = setcode;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}

	public int getRefPreRec() {
		return refPreRec;
	}

	public void setRefPreRec(int refPreRec) {
		this.refPreRec = refPreRec;
	}

	public int getInnerupdatetype() {
		return innerupdatetype;
	}

	public void setInnerupdatetype(int innerupdatetype) {
		this.innerupdatetype = innerupdatetype;
	}

	public String getCondFormula() {
		return CondFormula;
	}

	public void setCondFormula(String condFormula) {
		CondFormula = condFormula;
	}

	public String getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(String subMenu) {
		this.subMenu = subMenu;
	}

}
