package com.hjsj.hrms.module.system.hrcloud;

public class HrConstanCodeItemPojo {
	private String codesetid = "";
	private String codeitemid = "";
	private String codeitemdesc = "";
	private String parentid = "";
	private String hr_codesetid = "";
	private String hr_codeitemid = "";
	private String hr_codeitemdesc = "";
	private String hr_codesetdesc = "";
	
	
	public HrConstanCodeItemPojo() {
	}


	public HrConstanCodeItemPojo(String codesetid, String codeitemid, String codeitemdesc, String parentid,
			String hr_codesetid, String hr_codeitemid, String hr_codeitemdesc, String hr_codesetdesc) {
		this.codesetid = codesetid;
		this.codeitemid = codeitemid;
		this.codeitemdesc = codeitemdesc;
		this.parentid = parentid;
		this.hr_codesetid = hr_codesetid;
		this.hr_codeitemid = hr_codeitemid;
		this.hr_codeitemdesc = hr_codeitemdesc;
		this.hr_codesetdesc = hr_codesetdesc;
	}


	public String getCodesetid() {
		return codesetid;
	}


	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}


	public String getCodeitemid() {
		return codeitemid;
	}


	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}


	public String getCodeitemdesc() {
		return codeitemdesc;
	}


	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}


	public String getParentid() {
		return parentid;
	}


	public void setParentid(String parentid) {
		this.parentid = parentid;
	}


	public String getHr_codesetid() {
		return hr_codesetid;
	}


	public void setHr_codesetid(String hr_codesetid) {
		this.hr_codesetid = hr_codesetid;
	}


	public String getHr_codeitemid() {
		return hr_codeitemid;
	}


	public void setHr_codeitemid(String hr_codeitemid) {
		this.hr_codeitemid = hr_codeitemid;
	}


	public String getHr_codeitemdesc() {
		return hr_codeitemdesc;
	}


	public void setHr_codeitemdesc(String hr_codeitemdesc) {
		this.hr_codeitemdesc = hr_codeitemdesc;
	}


	public String getHr_codesetdesc() {
		return hr_codesetdesc;
	}


	public void setHr_codesetdesc(String hr_codesetdesc) {
		this.hr_codesetdesc = hr_codesetdesc;
	}
	
	public boolean isTopItem(){
		return this.getHr_codeitemid().equals(this.getParentid());
	}
}
