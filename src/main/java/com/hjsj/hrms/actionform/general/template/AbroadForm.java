/**
 * 
 */
package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:AbroadForm</p>
 * <p>Description:出国政审业务</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 23, 20065:10:04 PM
 * @author chenmengqing
 * @version 4.0
 */
public class AbroadForm extends FrameForm {
	/**业务模板列表*/
	private ArrayList templist=new ArrayList();
	/**业务说明*/
	private String content;
	/**模板表格号*/
	private String tabid;
	/**业务模板是否需要审批*/
	private String sp_flag;

    /**模块标志*/
    private String module;
    
	public ArrayList getTemplist() {
		return templist;
	}

	public void setTemplist(ArrayList templist) {
		this.templist = templist;
	}

	@Override
    public void outPutFormHM() {
		this.setTemplist((ArrayList)this.getFormHM().get("templist"));
		this.setContent((String)this.getFormHM().get("content"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**加密锁*/
        this.getFormHM().put("lock",arg1.getSession().getServletContext().getAttribute("lock"));
		
		return super.validate(arg0, arg1);
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("module",this.getModule());
		//this.getFormHM().put("sp_flag",this.getFormHM());
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
