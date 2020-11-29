/**
 * 
 */
package com.hjsj.hrms.actionform.report.edit_report;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
/**
 * <p>Title:TemplateForm</p>
 * <p>Description:业务变动的表单</p> 
 * <p>Company:hjsj</p> 
 * create time  2010-05-13 
 * @author xieguiquan
 * @version 4.0
 */
public class PictureReportForm extends FrameForm {
	private String photo_maxsize="0";
	private String photofile="";
    transient private  FormFile picturefile; 
	private String tabid;
	private String gridno;
	private String tablename;
	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getGridno() {
		return gridno;
	}

	public void setGridno(String gridno) {
		this.gridno = gridno;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}


	@Override
    public void outPutFormHM() {
		this.setPhotofile((String)this.getFormHM().get("photofile"));
		this.setPhoto_maxsize((String)this.getFormHM().get("photo_maxsize"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setGridno((String)this.getFormHM().get("gridno"));
		this.setTablename((String)this.getFormHM().get("tablename"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.getFormHM().put("session",arg1.getSession());
        /**加密锁*/
        this.getFormHM().put("lock",arg1.getSession().getServletContext().getAttribute("lock"));
		return super.validate(arg0, arg1);
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("picturefile",this.getPicturefile());
	}




	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		//this.setTaskid("0");
		super.reset(arg0, arg1);
	}



	public String getPhotofile() {
		return photofile;
	}

	public void setPhotofile(String photofile) {
		this.photofile = photofile;
	}


	public String getPhoto_maxsize() {
		return photo_maxsize;
	}

	public void setPhoto_maxsize(String photo_maxsize) {
		this.photo_maxsize = photo_maxsize;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}



}
