package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.general.muster.ExecutePdf;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExecuteMusterPdfTrans extends IBusiness {

	public void execute() throws GeneralException {
		String fieldWidths=(String)this.getFormHM().get("fieldWidths");
		fieldWidths=fieldWidths!=null?fieldWidths:"";
		String mustername=(String)this.getFormHM().get("mustername");
		String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		String dbpre=(String)this.getFormHM().get("dbpre");
		String tabid=(String)this.getFormHM().get("tabid");
		String flag=(String)this.getFormHM().get("flag");
		flag=flag!=null?flag:"";
		String outName="";
		if("1".equals(flag))
		{
			ExecutePdf executePdf=new ExecutePdf(this.getFrameconn(),mustername);
			executePdf.setUserview(this.getUserView());
			executePdf.setInfor_Flag(infor_Flag);
			executePdf.setDbpre(dbpre);
			executePdf.setTabid(tabid);
			outName=executePdf.createPdf(this.getUserView().getUserName(),fieldWidths,mustername);
		}else{
			ExecuteExcel executeExcel=new ExecuteExcel(this.getFrameconn());
			executeExcel.setUserview(this.getUserView());			
			executeExcel.setInfor_Flag(infor_Flag);
			executeExcel.setDbpre(dbpre);
			executeExcel.setTabid(tabid);
			/* 测试 xiaoyun 2014-6-4 start */
			outName=executeExcel.createExcel(this.getUserView().getUserName(),mustername);
			//outName=executeExcel.createExcel2(this.getUserView().getUserName(),mustername);
			/* 测试 xiaoyun 2014-6-4 end */
			//outName=outName.replaceAll(".xls","#");
		}
		outName=PubFunc.encrypt(outName);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("outName",outName);
	}

}
