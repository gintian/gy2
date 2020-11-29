package com.hjsj.hrms.actionform.gz.gz_accounting.voucher;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class VoucherForm extends FrameForm {
	private ArrayList voucherList=new ArrayList();     //凭证列表
	private String voucher_id="";                      //凭证id
	private ArrayList timeList=new ArrayList();
	private String    timeInfo="";
	private ArrayList statusList=new ArrayList();
	private String    status="";
	
	private ArrayList dbilltimesList=new ArrayList();
	private String    dbilltimes="";
	
	private ArrayList headList=new ArrayList();
	private String    type="";   //1:财务凭证  2:按月汇总
    private PaginationForm voucherInfoListform=new PaginationForm();
	
	private String _code=""; 
    
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("timeInfo",this.getTimeInfo());
		this.getFormHM().put("voucher_id",this.getVoucher_id());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("dbilltimes",this.getDbilltimes());
	}

	
	@Override
    public void outPutFormHM() {
		this.set_code((String)this.getFormHM().get("_code")); 
		
		this.setHeadList((ArrayList)this.getFormHM().get("headList"));
		this.getVoucherInfoListform().setList((ArrayList)this.getFormHM().get("voucherInfoList"));
		this.setVoucherList((ArrayList)this.getFormHM().get("voucherList"));
		this.setVoucher_id((String)this.getFormHM().get("voucher_id"));
		this.setTimeList((ArrayList)this.getFormHM().get("timeList"));
		this.setTimeInfo((String)this.getFormHM().get("timeInfo"));
		this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setDbilltimesList((ArrayList)this.getFormHM().get("dbilltimesList"));
		this.setDbilltimes((String)this.getFormHM().get("dbilltimes"));
		this.setType((String)this.getFormHM().get("type"));

	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/gz_accounting/voucher/financial_voucher".equals(arg0.getPath())&&arg1.getParameter("br_query")!=null){
			this.voucher_id="";
            this.timeInfo="";          
            this.status="";
            this.dbilltimes="";
            this._code=""; 
        }
		
		if(arg1.getParameter("a_code")!=null||arg1.getParameter("b_query")!=null)
		{
			if(this.getVoucherInfoListform().getPagination()!=null)
				this.getVoucherInfoListform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}

	public ArrayList getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(ArrayList voucherList) {
		this.voucherList = voucherList;
	}

	public String getVoucher_id() {
		return voucher_id;
	}

	public void setVoucher_id(String voucher_id) {
		this.voucher_id = voucher_id;
	}

	public ArrayList getTimeList() {
		return timeList;
	}

	public void setTimeList(ArrayList timeList) {
		this.timeList = timeList;
	}

	public String getTimeInfo() {
		return timeInfo;
	}

	public void setTimeInfo(String timeInfo) {
		this.timeInfo = timeInfo;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
 

	public PaginationForm getVoucherInfoListform() {
		return voucherInfoListform;
	}

	public void setVoucherInfoListform(PaginationForm voucherInfoListform) {
		this.voucherInfoListform = voucherInfoListform;
	}

	public ArrayList getHeadList() {
		return headList;
	}

	public void setHeadList(ArrayList headList) {
		this.headList = headList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String get_code() {
		return _code;
	}


	public void set_code(String _code) {
		this._code = _code;
	}


	public ArrayList getDbilltimesList() {
		return dbilltimesList;
	}


	public void setDbilltimesList(ArrayList dbilltimesList) {
		this.dbilltimesList = dbilltimesList;
	}


	public String getDbilltimes() {
		return dbilltimes;
	}


	public void setDbilltimes(String dbilltimes) {
		this.dbilltimes = dbilltimes;
	}

 

}
