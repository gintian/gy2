/**
 * 
 */
package com.hjsj.hrms.actionform.general.muster;

import com.hrms.struts.action.FrameForm;

/**
 * <p>Title:CodeSelectForm</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-18:14:49:02</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CodeSelectForm extends FrameForm {
//	/**查询语句*/
//	private String sql;
//	/**字段列表*/
//	private ArrayList fieldlist;
	
	private String codesetid;
	/**单位|部门|职位关联更新*/
	private String parent_id="";
	
	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	@Override
    public void outPutFormHM() {
//		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
//		this.setSql((String)this.getFormHM().get("sql"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
	}

	@Override
    public void inPutTransHM() {
		
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

//	public ArrayList getFieldlist() {
//		return fieldlist;
//	}
//
//	public void setFieldlist(ArrayList fieldlist) {
//		this.fieldlist = fieldlist;
//	}
//
//	public String getSql() {
//		return sql;
//	}
//
//	public void setSql(String sql) {
//		this.sql = sql;
//	}

}
