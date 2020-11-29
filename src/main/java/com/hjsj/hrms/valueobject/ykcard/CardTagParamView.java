/*
 * Created on 2005-5-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.ykcard;

import java.io.Serializable;
import java.util.Calendar;



/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-12:11:18:38</p>
 * @author Administrator
 * @version 1.0
 * 
 */
public class CardTagParamView implements Serializable{
	private String disting_pt;        //分辨率800*600或者1024*768
	private String nid;               //人员ID
	private String queryflag;          //0代表安条件查询1代表安时间查询
	private String userbase;          //人员库
	private int cyear;                //年
	private int cmonth;               //月
	private int tabid;                //登记表id
	private int pageid;               //登记表页id
	private int season;
	private int queryflagtype;
	private int cyyear;
	private int cymonth;
	private int csyear;
	private int cdyear;
	private int cdmonth;
	private int ctimes;
	private String cdatestart;
	private String cdateend;
	public CardTagParamView()
	{
		userbase="Usr";
		cyear=Calendar.getInstance().get(Calendar.YEAR);
		cmonth=Calendar.getInstance().get(Calendar.MONTH)+1;
		cdatestart=cyear+"-" + cmonth+"-" + Calendar.getInstance().get(Calendar.DATE);
		cdateend=cyear+"-" + cmonth+"-" + Calendar.getInstance().get(Calendar.DATE);
		season=1;
		queryflagtype=1;
		cyyear=cyear;
		cymonth=cmonth;
		csyear=cyear;
		cdyear=cyear;
		cdmonth=cmonth;
		ctimes=11;
		
		//disting_pt=String.valueOf(Toolkit.getDefaultToolkit().getScreenSize().width);
		disting_pt="1024";
		pageid=0;
	}
	/**
	 * @return Returns the cmonth.
	 */
	public int getCmonth() {
		return cmonth;
	}
	/**
	 * @return Returns the cyear.
	 */
	public int getCyear() {
		return cyear;
	}
	/**
	 * @return Returns the disting_pt.
	 */
	public String getDisting_pt() {
		return disting_pt;
	}
	/**
	 * @return Returns the nid.
	 */
	public String getNid() {
		return nid;
	}
	/**
	 * @return Returns the pageid.
	 */
	public int getPageid() {
		return pageid;
	}
	/**
	 * @return Returns the queryflag.
	 */
	public String getQueryflag() {
		return queryflag;
	}
	/**
	 * @return Returns the tabid.
	 */
	public int getTabid() {
		return tabid;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param cmonth The cmonth to set.
	 */
	public void setCmonth(int cmonth) {
		this.cmonth = cmonth;
	}
	/**
	 * @param cyear The cyear to set.
	 */
	public void setCyear(int cyear) {
		this.cyear = cyear;
	}
	/**
	 * @param disting_pt The disting_pt to set.
	 */
	public void setDisting_pt(String disting_pt) {
		if(disting_pt!="-1")
		  this.disting_pt = disting_pt;
	}
	/**
	 * @param nid The nid to set.
	 */
	public void setNid(String nid) {
		this.nid = nid;
	}
	/**
	 * @param pageid The pageid to set.
	 */
	public void setPageid(int pageid) {
		if(pageid!=-1)
		 this.pageid = pageid;
	}
	/**
	 * @param queryflag The queryflag to set.
	 */
	public void setQueryflag(String queryflag) {
		this.queryflag = queryflag;
	}
	/**
	 * @param tabid The tabid to set.
	 */
	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	/**
	 * @return Returns the season.
	 */
	public int getSeason() {
		return season;
	}
	/**
	 * @param season The season to set.
	 */
	public void setSeason(int season) {
		this.season = season;
	}
	/**
	 * @return Returns the queryflagtype.
	 */
	public int getQueryflagtype() {
		return queryflagtype;
	}
	/**
	 * @param queryflagtype The queryflagtype to set.
	 */
	public void setQueryflagtype(int queryflagtype) {
		this.queryflagtype = queryflagtype;
	}
	/**
	 * @return Returns the cdmonth.
	 */
	public int getCdmonth() {
		return cdmonth;
	}
	/**
	 * @param cdmonth The cdmonth to set.
	 */
	public void setCdmonth(int cdmonth) {
		this.cdmonth = cdmonth;
	}
	/**
	 * @return Returns the cdyear.
	 */
	public int getCdyear() {
		return cdyear;
	}
	/**
	 * @param cdyear The cdyear to set.
	 */
	public void setCdyear(int cdyear) {
		this.cdyear = cdyear;
	}
	/**
	 * @return Returns the csyear.
	 */
	public int getCsyear() {
		return csyear;
	}
	/**
	 * @param csyear The csyear to set.
	 */
	public void setCsyear(int csyear) {
		this.csyear = csyear;
	}
	/**
	 * @return Returns the cymonth.
	 */
	public int getCymonth() {
		return cymonth;
	}
	/**
	 * @param cymonth The cymonth to set.
	 */
	public void setCymonth(int cymonth) {
		this.cymonth = cymonth;
	}
	/**
	 * @return Returns the cyyear.
	 */
	public int getCyyear() {
		return cyyear;
	}
	/**
	 * @param cyyear The cyyear to set.
	 */
	public void setCyyear(int cyyear) {
		this.cyyear = cyyear;
	}
	/**
	 * @return Returns the cdateend.
	 */
	public String getCdateend() {
		return cdateend;
	}
	/**
	 * @param cdateend The cdateend to set.
	 */
	public void setCdateend(String cdateend) {
		this.cdateend = cdateend;
	}
	/**
	 * @return Returns the cdatestart.
	 */
	public String getCdatestart() {
		return cdatestart;
	}
	/**
	 * @param cdatestart The cdatestart to set.
	 */
	public void setCdatestart(String cdatestart) {
		this.cdatestart = cdatestart;
	}
	/**
	 * @return Returns the ctimes.
	 */
	public int getCtimes() {
		return ctimes;
	}
	/**
	 * @param ctimes The ctimes to set.
	 */
	public void setCtimes(int ctimes) {
		this.ctimes = ctimes;
	}
}
