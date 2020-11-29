/**
 * 
 */
package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:TemplateOthForm</p>
 * <p>Description:用于保存人事异动中的其它业务</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 19, 200611:44:10 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TemplateOthForm extends FrameForm {
	/**查询语句*/
	private String sql;
	/**数据集*/
	private String setname="t_sys_role";
	/**字段列表*/
	private ArrayList fieldlist;
	/**角色对象列表*/
    private PaginationForm roleListForm=new PaginationForm();
    private ArrayList relist=new ArrayList();
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
    public void outPutFormHM() {
		this.setSql((String)this.getFormHM().get("sql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setRelist((ArrayList)this.getFormHM().get("relist"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",this.getRoleListForm().getSelectedList());		
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if(("/general/template/select_role_dialog".equals(arg0.getPath()))&&(arg1.getParameter("b_query")!=null))
        {
            if(this.roleListForm.getPagination()!=null)
              this.roleListForm.getPagination().firstPage();//?
        }
		return super.validate(arg0, arg1);
	}
	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public ArrayList getRelist() {
		return relist;
	}

	public void setRelist(ArrayList relist) {
		this.relist = relist;
	}


}
