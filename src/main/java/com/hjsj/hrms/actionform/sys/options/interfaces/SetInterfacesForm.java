package com.hjsj.hrms.actionform.sys.options.interfaces;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SetInterfacesForm extends FrameForm {


	private ArrayList fielditemlist=new ArrayList();	
	private ArrayList chklist=new ArrayList();
	private String chitemid="";
	private String impmode="";
	private String expmode="";
	private String marker=""; // 返回唯一标示 1：返回用户 0：返回HR平台
	private ArrayList orgitemlist=new ArrayList();  //组织机构
	private String treecode; //组织结构单位数
	private ArrayList listis = new ArrayList();   //常用查询
//	private ArrayList orgchklist=new ArrayList(); //组织关联
//	private String orgchitemid=""; //组织构属性对应	
	public String getImpmode() {
		return impmode;
	}

	public void setImpmode(String impmode) {
		this.impmode = impmode;
	}

	public String getExpmode() {
		return expmode;
	}

	public void setExpmode(String expmode) {
		this.expmode = expmode;
	}

	public String getChitemid() {
		return chitemid;
	}

	public void setChitemid(String chitemid) {
		this.chitemid = chitemid;
	}

	public ArrayList getChklist() {
		return chklist;
	}

	public void setChklist(ArrayList chklist) {
		this.chklist = chklist;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("chitemid",this.getChitemid());
	    if(this.getPagination()!=null)        
	        this.getFormHM().put("list",this.getPagination().getAllList());
	    this.getFormHM().put("fielditemlist", this.getFielditemlist());
	    this.getFormHM().put("impmode", this.getImpmode());
	    this.getFormHM().put("expmode", this.getExpmode());
	    this.getFormHM().put("orgitemlist", this.getOrgitemlist());
	    this.getFormHM().put("listis", this.getListis());
	    this.getFormHM().put("marker", this.getMarker());
//	    this.getFormHM().put("orgchitemid",this.getOrgchitemid());
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
         this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
         this.setChklist((ArrayList)this.getFormHM().get("chklist"));
         this.setChitemid((String)this.getFormHM().get("chitemid"));
         this.setImpmode((String)this.getFormHM().get("impmode"));
         this.setExpmode((String)this.getFormHM().get("expmode"));
         this.setOrgitemlist((ArrayList)this.getFormHM().get("orgitemlist"));
         this.setTreecode((String) this.getFormHM().get("treecode"));
         this.setListis((ArrayList)this.getFormHM().get("listis"));
         this.setMarker((String)this.getFormHM().get("marker"));
//         this.setOrgchklist((ArrayList)this.getFormHM().get("orgchklist"));
//         this.setOrgchitemid((String)this.getFormHM().get("orgchitemid"));
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
	public ArrayList getOrgitemlist() {
		return orgitemlist;
	}

	public void setOrgitemlist(ArrayList orgitemlist) {
		this.orgitemlist = orgitemlist;
	}
	
	public String getTreecode() {
		return treecode;
	}

	public void setTreecode(String treecode) {
		this.treecode = treecode;
	}
//	public ArrayList getOrgchklist() {
//		return orgchklist;
//	}
//
//	public void setOrgchklist(ArrayList orgchklist) {
//		this.orgchklist = orgchklist;
//	}
//	
//	public String getOrgchitemid() {
//		return orgchitemid;
//	}
//
//	public void setOrgchitemid(String orgchitemid) {
//		this.orgchitemid = orgchitemid;
//	}

	public ArrayList getListis() {
		return listis;
	}

	public void setListis(ArrayList listis) {
		this.listis = listis;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

}
