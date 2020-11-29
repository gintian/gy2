package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ReportSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String operateObject = "1"; // 1：编辑没上报表 2：编辑上报后的表
		
		//ArrayList resultList = (ArrayList) this.getFormHM().get("results");
		ArrayList resultList=new ArrayList();
		String results= (String) this.getFormHM().get("results");
		if(results!=null&&results.trim().length()>0)
		{	
			results=results.substring(1);
			String[] temps=results.split("`");
			for(int i=0;i<temps.length;i++)
				resultList.add(temps[i]);
		}
	//	Category.getInstance("com.hrms.frame.dao.ContentDAO").error("resultList="+resultList.size());
		String username = SafeCode.decode((String) this.getFormHM().get("username"));
		String obj1 = (String) this.getFormHM().get("obj1");
		String paramValue = (String) this.getFormHM().get("param");
		String tabid = (String) this.getFormHM().get("tabid");

		int rows = Integer.parseInt((String) this.getFormHM().get("rows"));
		int cols = Integer.parseInt((String) this.getFormHM().get("cols"));
		operateObject = (String) this.getFormHM().get("operateObject");
		String unitcode = (String) this.getFormHM().get("unitcode");

		TnameBo tnameBo = new TnameBo(this.frameconn, tabid);
		String scopeid = (String) this.getFormHM().get("scopeid");
		if(scopeid==null)
			scopeid="0";
		tnameBo.setScopeid(scopeid);
		paramValue = SafeCode.decode(paramValue);
		if(username==null|| "".equals(username)){
			username = this.userView.getUserName();
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		}
		String info = tnameBo.saveReportInfo(resultList, paramValue, tabid,
				rows, cols, this.getUserView().getUserId(), username, 1, operateObject, unitcode);
		this.getFormHM().put("info", info);
		this.getFormHM().put("obj1", obj1);
	}

}
