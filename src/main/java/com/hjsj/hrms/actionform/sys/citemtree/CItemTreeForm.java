package com.hjsj.hrms.actionform.sys.citemtree;

import com.hrms.struts.action.FrameForm;

import java.util.HashMap;

public class CItemTreeForm extends FrameForm {
	private String treecode;
	private String type;
	private String fsid;
	private String url;
	private String urlay;
	private String checkbox;
	private String checkvalue;
	private String target;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setTreecode((String) hm.get("treecode"));
//		System.out.println(hm.get("treecode"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getTreecode() {
		return treecode;
	}

	public void setTreecode(String treecode) {
		this.treecode = treecode;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getCheckvalue() {
		return checkvalue;
	}

	public void setCheckvalue(String checkvalue) {
		this.checkvalue = checkvalue;
	}

	public String getFsid() {
		return fsid;
	}

	public void setFsid(String fsid) {
		this.fsid = fsid;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlay() {
		return urlay;
	}

	public void setUrlay(String urlay) {
		this.urlay = urlay;
	}

}
