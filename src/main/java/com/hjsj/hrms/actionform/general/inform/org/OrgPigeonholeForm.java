/*
 * Created on 2006-3-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.general.inform.org;

import com.hrms.struts.action.FrameForm;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrgPigeonholeForm extends FrameForm {

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	private String catalog_id;
	private String historyorgname;
	private String archive_date;
	private String description;
	private String scceeddesc;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
       this.setScceeddesc((String)this.getFormHM().get("scceeddesc"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("historyorgname",historyorgname);
		this.getFormHM().put("archive_date",archive_date);
		this.getFormHM().put("description",description);
	}

	/**
	 * @return Returns the archive_date.
	 */
	public String getArchive_date() {
		return archive_date;
	}
	/**
	 * @param archive_date The archive_date to set.
	 */
	public void setArchive_date(String archive_date) {
		this.archive_date = archive_date;
	}
	/**
	 * @return Returns the catalog_id.
	 */
	public String getCatalog_id() {
		return catalog_id;
	}
	/**
	 * @param catalog_id The catalog_id to set.
	 */
	public void setCatalog_id(String catalog_id) {
		this.catalog_id = catalog_id;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return Returns the historyorgname.
	 */
	public String getHistoryorgname() {
		return historyorgname;
	}
	/**
	 * @param historyorgname The historyorgname to set.
	 */
	public void setHistoryorgname(String historyorgname) {
		this.historyorgname = historyorgname;
	}
	/**
	 * @return Returns the scceeddesc.
	 */
	public String getScceeddesc() {
		return scceeddesc;
	}
	/**
	 * @param scceeddesc The scceeddesc to set.
	 */
	public void setScceeddesc(String scceeddesc) {
		this.scceeddesc = scceeddesc;
	}
}
