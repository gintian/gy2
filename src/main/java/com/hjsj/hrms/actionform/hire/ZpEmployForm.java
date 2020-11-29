/*
 * Created on 2005-9-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ZpEmployForm</p>
 * <p>Description:员工录用表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 19, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class ZpEmployForm extends FrameForm {
	private String userBase = "Usr";
	private int current=1;
	private ArrayList toAddrlist = new ArrayList();
	private String fromName = "";
	private String topic = "";
	private String content = "";
	private FormFile file;
	private String filename = "";
	private String count = "0";
	/**
	 * 候选人对象
	 */
	private RecordVo zpEmployvo = new RecordVo("ZP_POS_TACHE");
	/**
	 * 候选人对象列表
	 */
	private PaginationForm zpEmployForm = new PaginationForm();
	
	private String dbpre = "Usr";
	
	@Override
    public void outPutFormHM() {
		this.setZpEmployvo((RecordVo) this.getFormHM().get("zpEmployvo"));
		this.getZpEmployForm().setList(
				(ArrayList) this.getFormHM().get("zpEmploylist"));
		this.getZpEmployForm().getPagination().gotoPage(current);
		this.setDbpre((String) this.getFormHM().get("dbpre"));
		this.setToAddrlist((ArrayList) this.getFormHM().get("toAddrlist"));
		this.setFromName((String) this.getFormHM().get("fromName"));
		this.setCount((String) this.getFormHM().get("count"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zpEmployvo", this.getZpEmployvo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpEmployForm().getSelectedList());
		this.getFormHM().put("userBase", this.getUserBase());
		this.getFormHM().put("toAddrlist", this.getToAddrlist());
		this.getFormHM().put("fromName", this.getFromName());
		this.getFormHM().put("topic", this.getTopic());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("filename", this.getFilename());
	}

	/**
	 * @return Returns the zpEmployForm.
	 */
	public PaginationForm getZpEmployForm() {
		return zpEmployForm;
	}
	/**
	 * @param zpEmployForm The zpEmployForm to set.
	 */
	public void setZpEmployForm(PaginationForm zpEmployForm) {
		this.zpEmployForm = zpEmployForm;
	}
	/**
	 * @return Returns the zpEmployvo.
	 */
	public RecordVo getZpEmployvo() {
		return zpEmployvo;
	}
	/**
	 * @param zpEmployvo The zpEmployvo to set.
	 */
	public void setZpEmployvo(RecordVo zpEmployvo) {
		this.zpEmployvo = zpEmployvo;
	}
	/**
	 * @return Returns the userBase.
	 */
	public String getUserBase() {
		return userBase;
	}
	/**
	 * @param userBase The userBase to set.
	 */
	public void setUserBase(String userBase) {
		this.userBase = userBase;
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/zp_employ/search_hire_employee".equals(arg0.getPath())&&arg1.getParameter("b_ok")!=null)
        {
            if(this.getPagination()!=null)
            	 current=this.zpEmployForm.getPagination().getCurrent();
        }	
		return super.validate(arg0, arg1);
	}
	/**
	 * @return Returns the current.
	 */
	public int getCurrent() {
		return current;
	}
	/**
	 * @param current The current to set.
	 */
	public void setCurrent(int current) {
		this.current = current;
	}
	/**
	 * @return Returns the dbpre.
	 */
	public String getDbpre() {
		return dbpre;
	}
	/**
	 * @param dbpre The dbpre to set.
	 */
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	/**
	 * @return Returns the content.
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content The content to set.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return Returns the topic.
	 */
	public String getTopic() {
		return topic;
	}
	/**
	 * @param topic The topic to set.
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	/**
	 * @return Returns the fromName.
	 */
	public String getFromName() {
		return fromName;
	}
	/**
	 * @param fromName The fromName to set.
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return Returns the file.
	 */
	public FormFile getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(FormFile file) {
		this.file = file;
	}
	/**
	 * @return Returns the count.
	 */
	public String getCount() {
		return count;
	}
	/**
	 * @param count The count to set.
	 */
	public void setCount(String count) {
		this.count = count;
	}
	/**
	 * @return Returns the toAddrlist.
	 */
	public ArrayList getToAddrlist() {
		return toAddrlist;
	}
	/**
	 * @param toAddrlist The toAddrlist to set.
	 */
	public void setToAddrlist(ArrayList toAddrlist) {
		this.toAddrlist = toAddrlist;
	}
}
