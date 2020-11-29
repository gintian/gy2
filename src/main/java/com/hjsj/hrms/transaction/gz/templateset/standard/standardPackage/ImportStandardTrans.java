package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

public class ImportStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String flag=(String)hm.get("flag");   // 1: 覆盖导入   2:追加导入
			ArrayList gzStandardPackageInfo=(ArrayList)this.getFormHM().get("gzStandardPackageInfo");
			String[] importStandardIds=(String[])this.getFormHM().get("importStandardIds");
			FormFile form_file = (FormFile) getFormHM().get("file");
			String value="";
     		boolean noManage = false;
     		if(this.userView.isSuper_admin())
     			value="UN";
     		else{
         		String unit_id = this.userView.getUnit_id();
        		if(unit_id!=null&&unit_id.trim().length()>2)
        		{
	         		if("UN`".equalsIgnoreCase(unit_id))
	        		{
	         			value="UN";
	        		}
	         		else 
	        		{
	        			String arr[] = unit_id.split("`");
	    	    		value = arr[0].substring(2);
	         		}
	        	}
        		else
         		{
        			if(this.userView.getManagePrivCode()==null|| "".equals(this.userView.getManagePrivCode()))
        				noManage=true;
        			if(this.userView.getManagePrivCode()!=null&& "".equals(this.userView.getManagePrivCode())){
        		    	String codevalue = (this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue()))?"UN":this.userView.getManagePrivCodeValue();
        		    	value = codevalue;
        			}
        		}
     		}
     		if(noManage)
     			value=null;
     		
			DownLoadXml.impotFile(this.getFrameconn(),flag,importStandardIds,form_file,gzStandardPackageInfo,value,this.userView);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
