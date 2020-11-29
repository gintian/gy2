package com.hjsj.hrms.actionform.train.exchange;


import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ExchangeForm extends FrameForm {
	
	private String model;//奖品管理=1，兑换记录=2，个人兑换记录=3
	private String a_code;
	private String columns;
	private String strsql;
	private String strwhere;
	private String order_by;
	private ArrayList itemList=new ArrayList();
	private FormFile excelfile;
	
	private String a0101;
	private String r5713;
	private String searchstr;
	private String startdate;
	private String enddate;
	private String ncount;
	private String npoint;//所需积分
	private String usable_npoint;//可用积分
	
	private String r5701;
	private String r5703;
	private String r5709;
	private String orgparentcode;
	private String uplevel;
	private String users;
	private int ccount;
	private String counts;
	private String editStatus;


	public String getCounts() {
		return counts;
	}

	public void setCounts(String counts) {
		this.counts = counts;
	}

	public int getCcount() {
		return ccount;
	}

	public void setCcount(int ccount) {
		this.ccount = ccount;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setModel((String)this.getFormHM().get("model"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setSearchstr((String)this.getFormHM().get("searchstr"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setR5713((String)this.getFormHM().get("r5713"));
		this.setStartdate((String)this.getFormHM().get("startdate"));
		this.setEnddate((String)this.getFormHM().get("enddate"));
		this.setNcount((String)this.getFormHM().get("ncount"));
		this.setNpoint((String)this.getFormHM().get("npoint"));
		this.setUsable_npoint((String)this.getFormHM().get("usable_npoint"));
		this.setR5701((String)this.getFormHM().get("r5701"));
		this.setR5703((String)this.getFormHM().get("r5703"));
		this.setR5709((String)this.getFormHM().get("r5709"));
		
		this.setUsers((String)this.getFormHM().get("users"));
		this.setCcount(Integer.parseInt(this.getFormHM().get("ccount").toString()));
		this.setCounts((String)(this.getFormHM().get("counts")));
		this.setEditStatus((String)(this.getFormHM().get("editStatus")));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("model", this.getModel());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.getFormHM().put("a0101", this.getA0101());
		this.getFormHM().put("r5713", this.getR5713());
		this.getFormHM().put("startdate", this.getStartdate());
		this.getFormHM().put("enddate", this.getEnddate());
		this.getFormHM().put("npoint", this.getNpoint());
		this.getFormHM().put("r5701", this.getR5701());
		this.getFormHM().put("excelfile", this.getExcelfile());
		
		this.getFormHM().put("users",this.getUsers());
		this.getFormHM().put("ccount", this.getCcount() + "");
		this.getFormHM().put("counts", this.getCounts());
		this.getFormHM().put("editStatus", this.getEditStatus());
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/train/exchange/exchangemanage".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			if(this.getPagination()!=null){
            	this.getPagination().firstPage();
            	this.pagerows=20;
			}
        }else if("/train/exchange/exchangeinfo".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	if(this.getPagination()!=null){
            	this.getPagination().firstPage();
            	this.pagerows=20;
        	}
        }else if("/train/exchange/exchangerecord".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	if(this.getPagination()!=null){
            	this.getPagination().firstPage();
            	this.pagerows=20;
        	}
        }else if("/train/exchange/exchangeintegral".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	if(this.getPagination()!=null){
            	this.getPagination().firstPage();
            	this.pagerows=20;
        	}
        }
		return super.validate(arg0, arg1);
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getR5701() {
		return r5701;
	}

	public void setR5701(String r5701) {
		this.r5701 = r5701;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getNpoint() {
		return npoint;
	}

	public void setNpoint(String npoint) {
		this.npoint = npoint;
	}

	public String getR5713() {
		return r5713;
	}

	public void setR5713(String r5713) {
		this.r5713 = r5713;
	}

	public String getR5703() {
		return r5703;
	}

	public void setR5703(String r5703) {
		this.r5703 = r5703;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public FormFile getExcelfile() {
		return excelfile;
	}

	public void setExcelfile(FormFile excelfile) {
		this.excelfile = excelfile;
	}

	public String getR5709() {
		return r5709;
	}

	public void setR5709(String r5709) {
		this.r5709 = r5709;
	}

	public String getUsable_npoint() {
		return usable_npoint;
	}

	public void setUsable_npoint(String usable_npoint) {
		this.usable_npoint = usable_npoint;
	}

	public String getNcount() {
		return ncount;
	}

	public void setNcount(String ncount) {
		this.ncount = ncount;
	}
	
	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

    public String getEditStatus() {
        return editStatus;
    }

    public void setEditStatus(String editStatus) {
        this.editStatus = editStatus;
    }
	
}
