package com.hjsj.hrms.actionform.general.deci.leader;

import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class LeaderParamForm extends FrameForm {
	/**记录集名称*/
    private String setname="A01";
    /**构库标识*/
    private String usedflag=Integer.toString(Constant.USED_FIELD_SET);
    /**信息种类，对人员信息查询则选全部子集*/
    private String domainflag=Integer.toBinaryString(Constant.EMPLOY_FIELD_SET);
    /**单位子集 */
    private ArrayList user_field_list=new ArrayList();
    private ArrayList unit_field_list = new ArrayList();
    private String bz_fieldsetid;//班子人员集指标    
    private ArrayList bz_codesetlist=new ArrayList();//代码指标集
    private ArrayList bz_codeitemlist=new ArrayList();//代码值集
    private String bz_codesetid;//代码指标
    private String bz_codeitemid;//  代码值
    /**************后备干部****************/
    private String hb_fieldsetid;//后备人员集指标
    private String hb_codesetid;
    private String hb_codeitemid;
    private ArrayList hb_codesetlist=new ArrayList();
    private ArrayList hb_codeitemlist=new ArrayList();
    /********/
    /**************单位子集****************/
    private String unit_fieldsetid;
    private String unit_codesetid;
    private String unit_codeitemid;
    private ArrayList unit_codesetlist=new ArrayList();
    private ArrayList unit_codeitemlist=new ArrayList();
    /********/
    private ArrayList itemlist=new ArrayList();
    private String field_falg;
    /**选中的字段名数组*/
    private String left_fields[];
    /**选中的字段名数组*/
    private String right_fields[];  
    /*******常用统计**********/
    private ArrayList selectsname=new ArrayList();
    private ArrayList snamelist=new ArrayList();
    /**********登记表的**********/
    private ArrayList selectrname=new ArrayList();
    private ArrayList rnamelist=new ArrayList();
    
    private String output_mess;
    private String display_mess;
    private String condi_display_mess;
    private String gcond_mess;
    private String unit_mess;
    private String bz_mess;
    private String hb_mess;
    private String unitfile_mess;
    private String loadtype_mess;
    private String loadtype_sel;
    
    private ArrayList dbprelist = new ArrayList();
    private String[] dbstr = new String[0];
    
    private ArrayList photoitemlist = new ArrayList();
    
	public ArrayList getPhotoitemlist() {
		return photoitemlist;
	}
	public void setPhotoitemlist(ArrayList photoitemlist) {
		this.photoitemlist = photoitemlist;
	}
	public String[] getDbstr() {
		return dbstr;
	}
	public void setDbstr(String[] dbstr) {
		this.dbstr = dbstr;
	}
	public String getDisplay_mess() {
		return display_mess;
	}
	public void setDisplay_mess(String display_mess) {
		this.display_mess = display_mess;
	}
	public String getGcond_mess() {
		return gcond_mess;
	}
	public void setGcond_mess(String gcond_mess) {
		this.gcond_mess = gcond_mess;
	}
	public String getOutput_mess() {
		return output_mess;
	}
	public void setOutput_mess(String output_mess) {
		this.output_mess = output_mess;
	}
	public String getUnit_mess() {
		return unit_mess;
	}
	public void setUnit_mess(String unit_mess) {
		this.unit_mess = unit_mess;
	}
	public ArrayList getSelectsname() {
		return selectsname;
	}
	public void setSelectsname(ArrayList selectsname) {
		this.selectsname = selectsname;
	}
	public ArrayList getSnamelist() {
		return snamelist;
	}
	public void setSnamelist(ArrayList snamelist) {
		this.snamelist = snamelist;
	}
	public ArrayList getItemlist() {
		return itemlist;
	}
	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getBz_fieldsetid() {
		return bz_fieldsetid;
	}
	public void setBz_fieldsetid(String bz_fieldsetid) {
		this.bz_fieldsetid = bz_fieldsetid;
	}
	public String getHb_fieldsetid() {
		return hb_fieldsetid;
	}
	public void setHb_fieldsetid(String hb_fieldsetid) {
		this.hb_fieldsetid = hb_fieldsetid;
	}
	public ArrayList getUser_field_list() {
		return user_field_list;
	}
	public void setUser_field_list(ArrayList user_field_list) {
		this.user_field_list = user_field_list;
	}
	@Override
    public void outPutFormHM() {
		this.setUser_field_list((ArrayList)this.getFormHM().get("user_field_list"));
		this.setUnit_field_list((ArrayList)this.getFormHM().get("unit_field_list"));
		this.setBz_fieldsetid((String)this.getFormHM().get("bz_fieldsetid"));
		this.setHb_fieldsetid((String)this.getFormHM().get("hb_fieldsetid"));
		this.setBz_codesetlist((ArrayList)this.getFormHM().get("bz_codesetlist"));
		this.setBz_codeitemlist((ArrayList)this.getFormHM().get("bz_codeitemlist"));
		this.setBz_codesetid((String)this.getFormHM().get("bz_codesetid"));
		this.setBz_codeitemid((String)this.getFormHM().get("bz_codeitemid"));
		
		this.setUnit_fieldsetid((String)this.getFormHM().get("unit_fieldsetid"));
		this.setUnit_codesetlist((ArrayList)this.getFormHM().get("bz_codesetlist"));
		this.setUnit_codeitemlist((ArrayList)this.getFormHM().get("bz_codeitemlist"));
		this.setUnit_codesetid((String)this.getFormHM().get("bz_codesetid"));
		this.setUnit_codeitemid((String)this.getFormHM().get("bz_codeitemid"));
		/*后备*/
		this.setHb_codeitemid((String)this.getFormHM().get("hb_codeitemid"));
		this.setHb_codesetid((String)this.getFormHM().get("hb_codesetid"));
		this.setHb_codeitemlist((ArrayList)this.getFormHM().get("hb_codeitemlist"));
		this.setHb_codesetlist((ArrayList)this.getFormHM().get("hb_codesetlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setField_falg((String)this.getFormHM().get("field_falg"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setDomainflag("1");
		this.setSelectsname((ArrayList)this.getFormHM().get("selectsname"));
		this.setSnamelist((ArrayList)this.getFormHM().get("snamelist"));
		this.setSelectrname((ArrayList)this.getFormHM().get("selectrname"));
		this.setRnamelist((ArrayList)this.getFormHM().get("rnamelist"));
		this.setOutput_mess((String)this.getFormHM().get("output_mess"));
		this.setDisplay_mess((String)this.getFormHM().get("display_mess"));
		this.setCondi_display_mess((String)this.getFormHM().get("condi_display_mess"));
		this.setGcond_mess((String)this.getFormHM().get("gcond_mess"));
		this.setUnit_mess((String)this.getFormHM().get("unit_mess"));
		this.setBz_mess((String)this.getFormHM().get("bz_mess"));
		this.setHb_mess((String)this.getFormHM().get("hb_mess"));
		this.setUnitfile_mess((String)this.getFormHM().get("unitfile_mess"));
		this.setLoadtype_mess((String)this.getFormHM().get("loadtype_mess"));
		this.setLoadtype_sel((String)this.getFormHM().get("loadtype_sel"));
		
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
		this.setDbstr((String[])this.getFormHM().get("dbstr"));
		this.setPhotoitemlist((ArrayList)this.getFormHM().get("photoitemlist"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("bz_fieldsetid",this.getBz_fieldsetid());
		this.getFormHM().put("hb_fieldsetid",this.getHb_fieldsetid());
		this.getFormHM().put("bz_codeitemid",this.getBz_codeitemid());
		this.getFormHM().put("bz_codesetid",this.getBz_codesetid());
		this.getFormHM().put("hb_codesetid",this.getHb_codesetid());
		this.getFormHM().put("hb_codeitemid",this.getHb_codeitemid());
		this.getFormHM().put("unit_fieldsetid",this.getUnit_fieldsetid());
		this.getFormHM().put("unit_codeitemid",this.getUnit_codeitemid());
		this.getFormHM().put("unit_codesetid",this.getUnit_codesetid());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("setname",this.getSetname());
		this.getFormHM().put("field_falg",this.getField_falg());
		this.getFormHM().put("dbstr",this.getDbstr());
	}
	public String getBz_codeitemid() {
		return bz_codeitemid;
	}
	public void setBz_codeitemid(String bz_codeitemid) {
		this.bz_codeitemid = bz_codeitemid;
	}
	public ArrayList getBz_codeitemlist() {
		return bz_codeitemlist;
	}
	public void setBz_codeitemlist(ArrayList bz_codeitemlist) {
		this.bz_codeitemlist = bz_codeitemlist;
	}
	public String getBz_codesetid() {
		return bz_codesetid;
	}
	public void setBz_codesetid(String bz_codesetid) {
		this.bz_codesetid = bz_codesetid;
	}
	public ArrayList getBz_codesetlist() {
		return bz_codesetlist;
	}
	public void setBz_codesetlist(ArrayList bz_codesetlist) {
		this.bz_codesetlist = bz_codesetlist;
	}
	public String getHb_codeitemid() {
		return hb_codeitemid;
	}
	public void setHb_codeitemid(String hb_codeitemid) {
		this.hb_codeitemid = hb_codeitemid;
	}
	public ArrayList getHb_codeitemlist() {
		return hb_codeitemlist;
	}
	public void setHb_codeitemlist(ArrayList hb_codeitemlist) {
		this.hb_codeitemlist = hb_codeitemlist;
	}
	public String getHb_codesetid() {
		return hb_codesetid;
	}
	public void setHb_codesetid(String hb_codesetid) {
		this.hb_codesetid = hb_codesetid;
	}
	public ArrayList getHb_codesetlist() {
		return hb_codesetlist;
	}
	public void setHb_codesetlist(ArrayList hb_codesetlist) {
		this.hb_codesetlist = hb_codesetlist;
	}
	public String getDomainflag() {
		return domainflag;
	}
	public void setDomainflag(String domainflag) {
		this.domainflag = domainflag;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getUsedflag() {
		return usedflag;
	}
	public void setUsedflag(String usedflag) {
		this.usedflag = usedflag;
	}
	public String[] getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getField_falg() {
		return field_falg;
	}
	public void setField_falg(String field_falg) {
		this.field_falg = field_falg;
	}
	public ArrayList getRnamelist() {
		return rnamelist;
	}
	public void setRnamelist(ArrayList rnamelist) {
		this.rnamelist = rnamelist;
	}
	public ArrayList getSelectrname() {
		return selectrname;
	}
	public void setSelectrname(ArrayList selectrname) {
		this.selectrname = selectrname;
	}
	public ArrayList getDbprelist() {
		return dbprelist;
	}
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	public String getBz_mess() {
		return bz_mess;
	}
	public void setBz_mess(String bz_mess) {
		this.bz_mess = bz_mess;
	}
	public String getHb_mess() {
		return hb_mess;
	}
	public void setHb_mess(String hb_mess) {
		this.hb_mess = hb_mess;
	}
	public ArrayList getUnit_field_list() {
		return unit_field_list;
	}
	public void setUnit_field_list(ArrayList unit_field_list) {
		this.unit_field_list = unit_field_list;
	}
	public String getUnit_codeitemid() {
		return unit_codeitemid;
	}
	public void setUnit_codeitemid(String unit_codeitemid) {
		this.unit_codeitemid = unit_codeitemid;
	}
	public ArrayList getUnit_codeitemlist() {
		return unit_codeitemlist;
	}
	public void setUnit_codeitemlist(ArrayList unit_codeitemlist) {
		this.unit_codeitemlist = unit_codeitemlist;
	}
	public String getUnit_codesetid() {
		return unit_codesetid;
	}
	public void setUnit_codesetid(String unit_codesetid) {
		this.unit_codesetid = unit_codesetid;
	}
	public ArrayList getUnit_codesetlist() {
		return unit_codesetlist;
	}
	public void setUnit_codesetlist(ArrayList unit_codesetlist) {
		this.unit_codesetlist = unit_codesetlist;
	}
	public String getUnit_fieldsetid() {
		return unit_fieldsetid;
	}
	public void setUnit_fieldsetid(String unit_fieldsetid) {
		this.unit_fieldsetid = unit_fieldsetid;
	}
	public String getUnitfile_mess() {
		return unitfile_mess;
	}
	public void setUnitfile_mess(String unitfile_mess) {
		this.unitfile_mess = unitfile_mess;
	}
	public String getLoadtype_mess() {
		return loadtype_mess;
	}
	public void setLoadtype_mess(String loadtype_mess) {
		this.loadtype_mess = loadtype_mess;
	}
	public String getLoadtype_sel() {
		return loadtype_sel;
	}
	public void setLoadtype_sel(String loadtype_sel) {
		this.loadtype_sel = loadtype_sel;
	}
	public String getCondi_display_mess() {
		return condi_display_mess;
	}
	public void setCondi_display_mess(String condi_display_mess) {
		this.condi_display_mess = condi_display_mess;
	}

}
