package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenGzAnalyseReportTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String rsdtlid=(String)map.get("rsdtlid");
			String rsid=(String)map.get("rsid");
			String salaryid=(String)map.get("salaryid");
			String pre=(String)map.get("pre");
			String width=(String)map.get("w");
			String height=(String)map.get("h");
			String pt=(String)map.get("pt");
			pt = pt.replaceAll("／", "/");
			/**=1分析历史数据，=0分析归档数据*/
			String archive=(String)map.get("archive");
			if("0".equals(archive))
			{
				HistoryDataBo bo = new HistoryDataBo(this.getFrameconn());
				bo.syncSalaryarchiveStrut();
			}
			String username=this.userView.getUserName();
//			String privCode = this.userView.getManagePrivCode();
//			String privCodeValue = this.userView.getManagePrivCodeValue();
			String privCode = "";
			String privCodeValue = "-1";//此处改为传入-1，用来区分第一次进去  废掉原有走人员范围的规则  zhaoxg 2014-12-11
			String role = "0";
			if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			{
				role = "1";
			}
			ArrayList privList = this.userView.getPrivDbList();
			StringBuffer privdb = new StringBuffer("");
			if(privList != null)
			{
				for(int i =0;i<privList.size();i++)
				{
					privdb.append(privList.get(i).toString().toUpperCase());
					privdb.append("#");
				}
				privdb.setLength(privdb.length()-1);
			}
			this.getFormHM().put("privDb",privdb.toString());
			this.getFormHM().put("role", role);
			this.getFormHM().put("privCode",privCode==null?"":privCode);
			this.getFormHM().put("privCodeValue",privCodeValue==null?"":privCodeValue);
			this.getFormHM().put("rsdtlid",rsdtlid);
			this.getFormHM().put("reportTabId",rsid);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("pre",pre);
			this.getFormHM().put("username",username);
			this.getFormHM().put("width",width);
			this.getFormHM().put("height",height);
			this.getFormHM().put("address",pt);
			this.getFormHM().put("archive", archive);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
