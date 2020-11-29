package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class PortalTailorForm extends FrameForm {
   private String bulletintwinkle;
   private String bulletinscroll;
   private String bulletinshow;
   private String warntwinkle;
   private String warnscroll;
   private String warnshow;
   private String mustertwinkle;
   private String musterscroll;
   private String mustershow;
   private String querytwinkle;
   private String queryscroll;
   private String queryshow;
   private String stattwinkle;
   private String statscroll;
   private String statshow;
   private String cardtwinkle;
   private String cardscroll;
   private String cardshow;
   private String reporttwinkle;
   private String reportscroll;
   private String reportshow;
   private String mattertwinkle;
   private String matterscroll;
   private String mattershow;
   private String salarytwinkle;
   private String salaryscroll;
   private String salaryshow;
   private String[] right_fields;
   private ArrayList showitem= new ArrayList();
   private ArrayList list=new ArrayList();
   private String portalid;
   private String checkvalues;
   
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setBulletintwinkle((String)this.getFormHM().get("bulletintwinkle"));
		this.setBulletinscroll((String)this.getFormHM().get("bulletinscroll"));
		this.setBulletinshow((String)this.getFormHM().get("bulletinshow"));
		this.setWarntwinkle((String)this.getFormHM().get("warntwinkle"));
		this.setWarnscroll((String)this.getFormHM().get("warnscroll"));
		this.setWarnshow((String)this.getFormHM().get("warnshow"));
		this.setMustertwinkle((String)this.getFormHM().get("mustertwinkle"));
		this.setMusterscroll((String)this.getFormHM().get("musterscroll"));
		this.setMustershow((String)this.getFormHM().get("mustershow"));
		this.setQuerytwinkle((String)this.getFormHM().get("querytwinkle"));
		this.setQueryscroll((String)this.getFormHM().get("queryscroll"));
		this.setQueryshow((String)this.getFormHM().get("queryshow"));
		this.setStattwinkle((String)this.getFormHM().get("stattwinkle"));
		this.setStatscroll((String)this.getFormHM().get("statscroll"));
		this.setStatshow((String)this.getFormHM().get("statshow"));
		this.setCardtwinkle((String)this.getFormHM().get("cardtwinkle"));
		this.setCardscroll((String)this.getFormHM().get("cardscroll"));
		this.setCardshow((String)this.getFormHM().get("cardshow"));
		this.setReporttwinkle((String)this.getFormHM().get("reporttwinkle"));
		this.setReportscroll((String)this.getFormHM().get("reportscroll"));
		this.setReportshow((String)this.getFormHM().get("reportshow"));
		this.setList((ArrayList)this.getFormHM().get("inforlist"));
		this.setShowitem((ArrayList)this.getFormHM().get("showitem"));
		this.setMatterscroll((String)this.getFormHM().get("matterscroll"));
		this.setMattershow((String)this.getFormHM().get("mattershow"));
		this.setMattertwinkle((String)this.getFormHM().get("mattertwinkle"));
		this.setSalaryscroll((String)this.getFormHM().get("salaryscroll"));
		this.setSalarytwinkle((String)this.getFormHM().get("salarytwinkle"));
		this.setSalaryshow((String)this.getFormHM().get("salaryshow"));
		this.setPortalid((String)this.getFormHM().get("portalid"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("bulletintwinkle",this.bulletintwinkle);
		this.getFormHM().put("bulletinscroll",this.bulletinscroll);
		this.getFormHM().put("bulletinshow",this.bulletinshow);
		this.getFormHM().put("warntwinkle",this.warntwinkle);
		this.getFormHM().put("warnscroll",this.warnscroll);
		this.getFormHM().put("warnshow",this.warnshow);
		this.getFormHM().put("mustertwinkle",this.mustertwinkle);
		this.getFormHM().put("musterscroll",this.musterscroll);
		this.getFormHM().put("mustershow",this.mustershow);
		this.getFormHM().put("querytwinkle",this.querytwinkle);
		this.getFormHM().put("queryscroll",this.queryscroll);
		this.getFormHM().put("queryshow",this.queryshow);
		this.getFormHM().put("stattwinkle",this.stattwinkle);
		this.getFormHM().put("statscroll",this.statscroll);
		this.getFormHM().put("statshow",this.statshow);
		this.getFormHM().put("cardtwinkle",this.cardtwinkle);
		this.getFormHM().put("cardscroll",this.cardscroll);
		this.getFormHM().put("cardshow",this.cardshow);
		this.getFormHM().put("reporttwinkle",this.reporttwinkle);
		this.getFormHM().put("reportscroll",this.reportscroll);
		this.getFormHM().put("reportshow",this.reportshow);
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("showitem",this.showitem);
		this.getFormHM().put("mattertwinkle", this.mattertwinkle);
		this.getFormHM().put("mattershow", this.mattershow);
		this.getFormHM().put("matterscroll", matterscroll);
		this.getFormHM().put("salaryscroll", this.salaryscroll);
		this.getFormHM().put("salarytwinkle", this.salarytwinkle);
		this.getFormHM().put("salaryshow", this.salaryshow);
		this.getFormHM().put("portalid", portalid);
		this.getFormHM().put("checkvalues", checkvalues);
	}

	public String getBulletinscroll() {
		return bulletinscroll;
	}

	public void setBulletinscroll(String bulletinscroll) {
		this.bulletinscroll = bulletinscroll;
	}

	public String getBulletinshow() {
		return bulletinshow;
	}

	public void setBulletinshow(String bulletinshow) {
		this.bulletinshow = bulletinshow;
	}

	public String getBulletintwinkle() {
		return bulletintwinkle;
	}

	public void setBulletintwinkle(String bulletintwinkle) {
		this.bulletintwinkle = bulletintwinkle;
	}

	public String getMusterscroll() {
		return musterscroll;
	}

	public void setMusterscroll(String musterscroll) {
		this.musterscroll = musterscroll;
	}

	public String getMustershow() {
		return mustershow;
	}

	public void setMustershow(String mustershow) {
		this.mustershow = mustershow;
	}

	public String getMustertwinkle() {
		return mustertwinkle;
	}

	public void setMustertwinkle(String mustertwinkle) {
		this.mustertwinkle = mustertwinkle;
	}

	public String getWarnscroll() {
		return warnscroll;
	}

	public void setWarnscroll(String warnscroll) {
		this.warnscroll = warnscroll;
	}

	public String getWarnshow() {
		return warnshow;
	}

	public void setWarnshow(String warnshow) {
		this.warnshow = warnshow;
	}

	public String getWarntwinkle() {
		return warntwinkle;
	}

	public void setWarntwinkle(String warntwinkle) {
		this.warntwinkle = warntwinkle;
	}

	public String getCardscroll() {
		return cardscroll;
	}

	public void setCardscroll(String cardscroll) {
		this.cardscroll = cardscroll;
	}

	public String getCardshow() {
		return cardshow;
	}

	public void setCardshow(String cardshow) {
		this.cardshow = cardshow;
	}

	public String getCardtwinkle() {
		return cardtwinkle;
	}

	public void setCardtwinkle(String cardtwinkle) {
		this.cardtwinkle = cardtwinkle;
	}

	public String getQueryscroll() {
		return queryscroll;
	}

	public void setQueryscroll(String queryscroll) {
		this.queryscroll = queryscroll;
	}

	public String getQueryshow() {
		return queryshow;
	}

	public void setQueryshow(String queryshow) {
		this.queryshow = queryshow;
	}

	public String getQuerytwinkle() {
		return querytwinkle;
	}

	public void setQuerytwinkle(String querytwinkle) {
		this.querytwinkle = querytwinkle;
	}

	public String getReportscroll() {
		return reportscroll;
	}

	public void setReportscroll(String reportscroll) {
		this.reportscroll = reportscroll;
	}

	public String getReportshow() {
		return reportshow;
	}

	public void setReportshow(String reportshow) {
		this.reportshow = reportshow;
	}

	public String getReporttwinkle() {
		return reporttwinkle;
	}

	public void setReporttwinkle(String reporttwinkle) {
		this.reporttwinkle = reporttwinkle;
	}

	public String getStatscroll() {
		return statscroll;
	}

	public void setStatscroll(String statscroll) {
		this.statscroll = statscroll;
	}

	public String getStatshow() {
		return statshow;
	}

	public void setStatshow(String statshow) {
		this.statshow = statshow;
	}

	public String getStattwinkle() {
		return stattwinkle;
	}

	public void setStattwinkle(String stattwinkle) {
		this.stattwinkle = stattwinkle;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getShowitem() {
		return showitem;
	}

	public void setShowitem(ArrayList showitem) {
		this.showitem = showitem;
	}

	public String getMattertwinkle() {
		return mattertwinkle;
	}

	public void setMattertwinkle(String mattertwinkle) {
		this.mattertwinkle = mattertwinkle;
	}

	public String getMatterscroll() {
		return matterscroll;
	}

	public void setMatterscroll(String matterscroll) {
		this.matterscroll = matterscroll;
	}

	public String getMattershow() {
		return mattershow;
	}

	public void setMattershow(String mattershow) {
		this.mattershow = mattershow;
	}

	public String getSalarytwinkle() {
		return salarytwinkle;
	}

	public void setSalarytwinkle(String salarytwinkle) {
		this.salarytwinkle = salarytwinkle;
	}

	public String getSalaryscroll() {
		return salaryscroll;
	}

	public void setSalaryscroll(String salaryscroll) {
		this.salaryscroll = salaryscroll;
	}

	public String getSalaryshow() {
		return salaryshow;
	}

	public void setSalaryshow(String salaryshow) {
		this.salaryshow = salaryshow;
	}

	public String getCheckvalues() {
		return checkvalues;
	}

	public void setCheckvalues(String checkvalues) {
		this.checkvalues = checkvalues;
	}

	public String getPortalid() {
		return portalid;
	}

	public void setPortalid(String portalid) {
		this.portalid = portalid;
	}

}
