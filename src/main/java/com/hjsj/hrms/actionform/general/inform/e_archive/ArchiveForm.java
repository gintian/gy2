/**
 * 
 */
package com.hjsj.hrms.actionform.general.inform.e_archive;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 *<p>Title:电子档案</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:上午11:31:04</p> 
 *@author cmq
 *@version 4.0
 */
public class ArchiveForm extends FrameForm {

	/**文件名称*/
	private String filename;
	/**档案文件目录列表*/
	private PaginationForm archiveListform=new PaginationForm();
	/**单位名称*/
	private String b0110;
	/**部门名称*/
	private String e0122;
	/**人员姓名*/
	private String a0101;
	/**日志id*/
	private String logid;
	private String width;
	private String height;
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedList", this.getArchiveListform().getSelectedList());

	}

	@Override
    public void outPutFormHM() {
		this.getArchiveListform().setList((ArrayList)this.getFormHM().get("archiveList"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setE0122((String)this.getFormHM().get("b0110"));
		this.setFilename((String)this.getFormHM().get("filename"));
        this.setLogid((String)this.getFormHM().get("logid"));
        this.setWidth((String)this.getFormHM().get("width"));
        this.setHeight((String)this.getFormHM().get("height"));
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public PaginationForm getArchiveListform() {
		return archiveListform;
	}

	public void setArchiveListform(PaginationForm archiveListform) {
		this.archiveListform = archiveListform;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getLogid() {
		return logid;
	}

	public void setLogid(String logid) {
		this.logid = logid;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
