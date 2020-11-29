package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseDataExportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GzAnalyseExportDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String opt=this.getFormHM().get("opt")==null?null:(String)this.getFormHM().get("opt");
			if(opt==null|| "1".equals(opt))
			{
		    	byte[] data_bytes=(byte[])this.getFormHM().get("data_bytes"); 
	    		ArrayList datalist=PubFunc.unzipBytes_object(data_bytes);
		    	String rsid=(String)this.getFormHM().get("rsid");
		    	String rsdtlid=(String)this.getFormHM().get("rsdtlid");
		    	String bgroup = (String)this.getFormHM().get("bgroup");
		    	byte[] head_bytes=(byte[])this.getFormHM().get("head_bytes");
		    	ArrayList tableHeadList=PubFunc.unzipBytes_object(head_bytes);
		    	String isShowHead=(String)this.getFormHM().get("isShowHead");
		    	String isShowSeria=(String)this.getFormHM().get("isShowSeria");
		    	String isShowUnitData=(String)this.getFormHM().get("isShowUnitData");
		    	String name=(String)this.getFormHM().get("name");
		    	String recordNums=(String)this.getFormHM().get("recordNums");
				String id = (String)this.getFormHM().get("id");
				if(id==null||id.length()==0){
					id="00";
				}
			    GzAnalyseDataExportBo bo = new GzAnalyseDataExportBo(this.getFrameconn());
			    bo.setId(id);
		    	String filename=bo.executeExport(rsdtlid,rsid, datalist, tableHeadList,bgroup,isShowHead,isShowSeria,isShowUnitData,this.getUserView(),name,recordNums);
		    	filename = PubFunc.encrypt(filename);
		    	filename = SafeCode.encode(filename);
		    	this.getFormHM().put("fileName",filename);
			}
			else
			{
				String rsid=(String)this.getFormHM().get("rsid");
				String rsdtlid=(String)this.getFormHM().get("rsdtlid");
				String year = (String)this.getFormHM().get("year");
				String pre=(String)this.getFormHM().get("pre");
				String salaryid=(String)this.getFormHM().get("salaryid");
				String firstsalaryid=(String)this.getFormHM().get("firstsalaryid");
				if("9".equals(rsid))
				{
					if(firstsalaryid!=null&&!"-1".equals(firstsalaryid))
						salaryid=firstsalaryid;
				}
				String a0100s=(String)this.getFormHM().get("a0100s");
				String isShowHead=(String)this.getFormHM().get("isShowHead");
		    	String isShowSeria=(String)this.getFormHM().get("isShowSeria");
		    	String archive =(String)this.getFormHM().get("archive");
		    	String noPage = (String)this.getFormHM().get("noPage");
				byte[] head_bytes=(byte[])this.getFormHM().get("head_bytes");
		    	ArrayList tableHeadList=PubFunc.unzipBytes_object(head_bytes);
		    	HashMap nameMap=(HashMap)this.getFormHM().get("nameMap");
		    	String recordNums=(String)this.getFormHM().get("recordNums");
		    	GzAnalyseDataExportBo bo = new GzAnalyseDataExportBo(this.getFrameconn());
		    	/*HashMap nameMap =new HashMap();

		    	nameMap.put("USR00000009", "张军");
		    	nameMap.put("USR00000049", "王光艳");
		    	nameMap.put("USR00000058", "司文辉");
		    	nameMap.put("USR00000003", "龚务军");
		    	nameMap.put("USR00000030", "刘兵");*/
		    	//,USR00000009,USR00000049,USR00000058,USR00000003,
		    	//String filename=bo.bacthExportExcel(rsid, rsdtlid, pre, salaryid, year, a0100s, tableHeadList, isShowHead, isShowSeria, "1",nameMap);
		    	String filename=bo.bacthExportExcel(this.getFrameconn(),rsid,rsdtlid,pre,salaryid,year,a0100s,tableHeadList,isShowHead,isShowSeria,"1",nameMap,this.getUserView(),archive,noPage,recordNums);
		    	filename = PubFunc.encrypt(filename);
		    	filename = SafeCode.encode(filename);
		    	this.getFormHM().put("fileName",filename);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

}
