package com.hjsj.hrms.actionform.hire.zp_options.statestat;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateStatForm extends FrameForm {
	private String startime ;
//	开始时间
	private String endtime;
//	　结束时间
	private String zp_pos_id;
//	职位id 
	private List  itemlist;
//	统计指标list
	private List  retlist;
//	招聘单位/部门/职位名称
	private String zp_fullname;
	private String zp_name;
//	统计叶面位置
	private String pos;
//	招聘职位列表
	private ArrayList zp_poslist;
	
	private String codeitemid;
	
//	private String codeitemid;
	private ArrayList codelist;
	private ArrayList alist = new ArrayList(); 
	private String schoolPosition;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSchoolPosition((String)hm.get("schoolPosition"));
		this.setReturnflag((String)hm.get("returnflag"));
		this.setAlist((ArrayList)hm.get("alist"));
		this.setStartime((String) hm.get("startime"));
		this.setEndtime((String) hm.get("endtime"));
		this.setZp_pos_id((String) hm.get("zp_pos_id"));
		this.setItemlist((List) hm.get("itemlist"));
		this.setRetlist((List) hm.get("retlist"));
		this.setZp_fullname((String) hm.get("zp_fullname"));
		this.setPos((String) hm.get("pos"));
		this.setZp_poslist((ArrayList) hm.get("zp_poslist"));
		this.setCodeitemid((String) hm.get("codeitemid"));
		this.setCodelist((ArrayList) hm.get("codelist"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("returnflag", this.getReturnflag());
		hm.put("startime",this.getStartime());
		hm.put("endtime",this.getEndtime());
		hm.put("zp_pos_id",this.getZp_pos_id());
//		hm.put("zp_fullname",this.getZp_fullname());
		hm.put("zp_fullname",this.getZp_name());
		hm.put("pos",this.getPos());
		hm.put("codeitemid",this.getCodeitemid());
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public List getItemlist() {
		return itemlist;
	}

	public void setItemlist(List itemlist) {
		this.itemlist = itemlist;
	}

	public List getRetlist() {
		return retlist;
	}

	public void setRetlist(List retlist) {
		this.retlist = retlist;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getZp_pos_id() {
		return zp_pos_id;
	}

	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}

	public String getZp_fullname() {
		return zp_fullname;
	}

	public void setZp_fullname(String zp_fullname) {
		this.zp_fullname = zp_fullname;
	}

	public String getZp_name() {
		return zp_name;
	}

	public void setZp_name(String zp_name) {
		this.zp_name = zp_name;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public ArrayList getZp_poslist() {
		return zp_poslist;
	}

	public void setZp_poslist(ArrayList zp_poslist) {
		this.zp_poslist = zp_poslist;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public ArrayList getCodelist() {
		return codelist;
	}

	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}

	public ArrayList getAlist() {
		return alist;
	}

	public void setAlist(ArrayList alist) {
		this.alist = alist;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

}
