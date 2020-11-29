package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:工资报表数据导出</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GzReportDataExportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			byte[] data_bytes=(byte[])this.getFormHM().get("data_bytes"); 
			ArrayList datalist=PubFunc.unzipBytes_object(data_bytes);
			String rsid=(String)this.getFormHM().get("rsid"); //报表种类编号 
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			byte[] head_bytes=(byte[])this.getFormHM().get("head_bytes");
			ArrayList tableHeadList=PubFunc.unzipBytes_object(head_bytes);
			String isShowHead=(String)this.getFormHM().get("isShowHead");   //是否显示表头=1显示：=0不显示
			String isShowSeria=(String)this.getFormHM().get("isShowSeria");  //是否序号=1显示：=0不显示
			String isSign=(String)this.getFormHM().get("isSign");//是否显示签名=1显示=0不显示
			HashMap groupMap=(HashMap)this.getFormHM().get("groupMap");
			String recordNums=(String)this.getFormHM().get("recordNums");
			GzExcelBo bo=new GzExcelBo(this.getFrameconn());
			String fileName=bo.executeGzReportExcel(rsid,tableHeadList,datalist,isShowHead,isShowSeria,isSign,this.getUserView(),rsdtlid,groupMap,recordNums);
			fileName = PubFunc.encrypt(fileName);
			fileName = SafeCode.encode(fileName);
			this.getFormHM().put("fileName",fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
