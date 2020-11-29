package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.module.muster.showmuster.businessobject.ShowManageService;
import com.hjsj.hrms.module.muster.showmuster.businessobject.impl.ShowManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class PrintExcelTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbpre = (String)this.getFormHM().get("userbase");
		dbpre=dbpre!=null?dbpre:"";
		
		String a_code = (String)this.getFormHM().get("code");
		a_code=a_code!=null?a_code:"";
		//liuy 2014-10-23  常用花名册条件查询输出excel，条件统一保存为muster_excel_sql  start
		String wherestr = (String)this.getUserView().getHm().get("muster_excel_sql");
		wherestr=wherestr!=null?wherestr:"";
		wherestr=SafeCode.decode(wherestr);
		//liuy end
		String orgtype = (String)this.getFormHM().get("orgtype");
		orgtype=orgtype!=null?orgtype:"";
		
		String roster = (String)this.getFormHM().get("roster");
		roster=roster!=null?roster:"";
		
		String checksort = (String)this.getFormHM().get("checksort");
		checksort=checksort!=null?checksort:"0";
		
		if(wherestr.trim().length()>2){//2009.10.27 and 改成or ，解决问题是人员信息浏览查不到兼职人员2009.12.27or改回and，兼职条件已经在wherestr中了lzw
			wherestr =" where "+dbpre+"A01.A0100 in(select A0100 from "+dbpre+"A01 "+wherestr+")"; 
		}
		
		String outName= "";
		if(!"no".equalsIgnoreCase(roster)){
		    ShowManageService muster = new ShowManageServiceImpl(this.frameconn, this.userView);
		    outName = muster.exportExcel(roster, "1", "0", dbpre, wherestr, "a0000");
		    outName=PubFunc.encrypt(outName);
		}
		this.getFormHM().put("outName",outName);
		
	}
}
