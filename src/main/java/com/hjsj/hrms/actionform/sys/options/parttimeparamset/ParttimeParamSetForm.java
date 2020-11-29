package com.hjsj.hrms.actionform.sys.options.parttimeparamset;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ParttimeParamSetForm extends FrameForm{
	/**
	 * 启用|不启用true|false
	 */
	private String flag="false";
	/**
	 * 兼职子集代号
	 */
	private String setid;
	/**
	 * 兼职单位指标
	 */
	private String unit;
	/**
	 * 任免表示指标
	 */
    private String appoint;
    /**
     * 职务指标
     */
    private String pos;
    /**
     * 部门指标
     */
    private String dept="";
    /**
     * 排序指标
     */
    private String order="";
    /**
     * 格式化指标
     */
    private String format="";
    /**
     * 兼职编制指标
     */
    private String takeup_quota="";
    /**
     * 兼职是否占用单位部门指标
     */
    private String occupy_quota="";
    /**
     * 兼职子集列表
     */
    private ArrayList setList = new ArrayList();
    /**
     * 兼职单位指标列表
     */
    private ArrayList unitList = new ArrayList();
    /**
     * 任免标识指标列表
     */
    private ArrayList appointList = new ArrayList();
    private ArrayList itemlist=new ArrayList();
    private ArrayList poslist=new ArrayList();
    private ArrayList nitemlist = new ArrayList();
	public ArrayList getPoslist() {
		return poslist;
	}

	public void setPoslist(ArrayList poslist) {
		this.poslist = poslist;
	}

	@Override
    public void outPutFormHM() {
		this.setAppoint((String)this.getFormHM().get("appoint"));
		this.setAppointList((ArrayList)this.getFormHM().get("appointList"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setUnit((String)this.getFormHM().get("unit"));
		this.setUnitList((ArrayList)this.getFormHM().get("unitList"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setPos((String)this.getFormHM().get("pos"));
		this.setPoslist((ArrayList)this.getFormHM().get("poslist"));
		this.setDept((String)this.getFormHM().get("dept"));
		this.setOrder((String)this.getFormHM().get("order"));
		this.setFormat((String)this.getFormHM().get("format"));
		this.setTakeup_quota((String)this.getFormHM().get("takeup_quota"));
		this.setOccupy_quota((String)this.getFormHM().get("occupy_quota"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setNitemlist((ArrayList)this.getFormHM().get("nitemlist"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("setid",this.getSetid());
		this.getFormHM().put("unit",this.getUnit());
		this.getFormHM().put("appoint",this.getAppoint());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("pos", this.getPos());
		this.getFormHM().put("dept", this.getDept());
		this.getFormHM().put("order", this.getOrder());
		this.getFormHM().put("format", this.getFormat());
		this.getFormHM().put("takeup_quota", this.getTakeup_quota());
		this.getFormHM().put("occupy_quota", this.getOccupy_quota());
		
	}

	public String getAppoint() {
		return appoint;
	}

	public void setAppoint(String appoint) {
		this.appoint = appoint;
	}

	public ArrayList getAppointList() {
		return appointList;
	}

	public void setAppointList(ArrayList appointList) {
		this.appointList = appointList;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getSetList() {
		return setList;
	}

	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public ArrayList getUnitList() {
		return unitList;
	}

	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
		this.setFlag("false");
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getTakeup_quota() {
		return takeup_quota;
	}

	public void setTakeup_quota(String takeup_quota) {
		this.takeup_quota = takeup_quota;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public ArrayList getNitemlist() {
		return nitemlist;
	}

	public void setNitemlist(ArrayList nitemlist) {
		this.nitemlist = nitemlist;
	}
	
	public String getOccupy_quota() {
	        return occupy_quota;
	}

	public void setOccupy_quota(String occupy_quota) {
	        this.occupy_quota = occupy_quota;
	}

}
