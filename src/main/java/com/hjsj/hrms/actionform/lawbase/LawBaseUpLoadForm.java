package com.hjsj.hrms.actionform.lawbase;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

public class LawBaseUpLoadForm extends FrameForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FormFile file;

	private String base_id;
    private String basetype;
	public String getBasetype() {
		return basetype;
	}

	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("base_id", base_id);
		this.getFormHM().put("file", file);
		this.getFormHM().put("basetype",basetype);
	}

	@Override
    public void outPutFormHM() {
         
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getBase_id() {
		return base_id;
	}

	public void setBase_id(String base_id) {
		this.base_id = base_id;
	}

}
