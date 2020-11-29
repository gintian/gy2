package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.axis.utils.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * <p>Title:ImportExcelTrans.java</p>
 * <p>Description>:KPI原始数据录入导入数据</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 20, 2011 10:15:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ImportExcelTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
	    try
	    {
	    	String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得系统当前时间
	    	
	    	//20/3/9 xus vfs改造
	    	String fileid = (String) this.getFormHM().get("fileid");
//	    	String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
//	    	fileName = PubFunc.decrypt(fileName);
	    	InputStream inputStream = null;
	    	if(StringUtils.isEmpty(fileid)) {
	    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
	    	}else {
	    		inputStream = VfsService.getFile(fileid);
	    		// 查询的参数
				String refreshKey = (String)this.getFormHM().get("refreshKey");
				String cycle = (String)this.getFormHM().get("cycle");	// 考核周期	
				String noYearCycle = (String)this.getFormHM().get("noYearCycle");	// 非年度考核周期	
				String objectType = (String) this.getFormHM().get("objectType"); // 对象类别：1 单位 2 人员
				String year = (String) this.getFormHM().get("year");
				String unionOrgCode = (String)this.getFormHM().get("unionOrgCode");	
				if(cycle==null || cycle.trim().length()<=0 || "-1".equalsIgnoreCase(cycle))
					cycle = "0";	
				if((noYearCycle==null || noYearCycle.trim().length()<=0) || (refreshKey!=null && refreshKey.trim().length()>0 && "changeCycle".equalsIgnoreCase(refreshKey)) )
					noYearCycle = "01";
				if("0".equalsIgnoreCase(cycle))
					noYearCycle = "";		
				if(year==null || year.trim().length()<=0)
					year = creatDate.substring(0, 4);
				
				String checkName = (String)this.getFormHM().get("checkName");				
				if(checkName.indexOf("'")!=-1)				
					checkName = checkName.replaceAll("'","‘"); 
				KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);							
			    ArrayList setlist = bo.searchKpiOriginalData(cycle,objectType,year,noYearCycle,checkName,unionOrgCode);
		
				bo.importData(inputStream,objectType);
				this.getFormHM().put("error", "0");
	    	}
	    	
	    	
//	    	String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
//            fileName = PubFunc.decrypt(fileName);
//            String path = (String) this.getFormHM().get("path");//路径
//            path = PubFunc.decrypt(path);
//            String filePath = path + fileName;
//            
//            File file = new File(filePath);
//	    	boolean flag = FileTypeUtil.isFileTypeEqual(file);
//	    	if(!flag){
//	    		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
//	    	}
//			// 查询的参数
//			String refreshKey = (String)this.getFormHM().get("refreshKey");
//			String cycle = (String)this.getFormHM().get("cycle");	// 考核周期	
//			String noYearCycle = (String)this.getFormHM().get("noYearCycle");	// 非年度考核周期	
//			String objectType = (String) this.getFormHM().get("objectType"); // 对象类别：1 单位 2 人员
//			String year = (String) this.getFormHM().get("year");
//			String unionOrgCode = (String)this.getFormHM().get("unionOrgCode");	
//			
//			if(cycle==null || cycle.trim().length()<=0 || cycle.equalsIgnoreCase("-1"))
//				cycle = "0";	
//			if((noYearCycle==null || noYearCycle.trim().length()<=0) || (refreshKey!=null && refreshKey.trim().length()>0 && refreshKey.equalsIgnoreCase("changeCycle")) )
//				noYearCycle = "01";
//			if(cycle.equalsIgnoreCase("0"))
//				noYearCycle = "";		
//			if(year==null || year.trim().length()<=0)
//				year = creatDate.substring(0, 4);
//			
//			String checkName = (String)this.getFormHM().get("checkName");				
//			if(checkName.indexOf("'")!=-1)				
//				checkName = checkName.replaceAll("'","‘");  
//			
//			KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);							
//		    ArrayList setlist = bo.searchKpiOriginalData(cycle,objectType,year,noYearCycle,checkName,unionOrgCode);
//	
//			bo.importData(file,objectType);
//			this.getFormHM().put("error", "0");
	    }catch(Exception e){		
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    
}