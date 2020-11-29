package com.hjsj.hrms.utils.syssqllog;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.syssqllog.LogFileFilter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryLogFilesTrans extends IBusiness{

	public void execute() throws GeneralException {
		String filePath = SystemConfig.getPropertyValue("sql_log_file");
		//正在操作的日志
  	    File currentLog = new File(filePath);
  	    //获取日志文件目录
  	    File parent = currentLog.getParentFile();
  	    //日志文件全名
  	    String fileName = currentLog.getName();
  	    //文件名不带格式后缀
  	    String preName = fileName.substring(0,fileName.lastIndexOf("."));
  	    //文件格式 例如：.log
  	    String ext = fileName.substring(fileName.lastIndexOf("."));
  	    //查找出所有备份文件
		String[] backupFiles = parent.list(new LogFileFilter(preName,ext));
		
		ArrayList logList = new ArrayList();
		HashMap logObj = new HashMap();
		logObj.put("logname", fileName);
		logObj.put("createtime","");
		logObj.put("filepath",PubFunc.encrypt(filePath));
		logList.add(logObj);
		
		String backName = "";
		for(int i=0;i<backupFiles.length;i++){
			backName = backupFiles[i];
			String dateStr = backName.substring(preName.length()+1,backName.lastIndexOf('.'));
			String[] dateValues = dateStr.split("_");
			dateStr = dateValues[0]+" "+dateValues[1]+":"+dateValues[2]+":"+dateValues[3];
			logObj = new HashMap();
			logObj.put("logname", backName);
			logObj.put("createtime",dateStr);
			logObj.put("filepath",PubFunc.encrypt(parent.getAbsolutePath()+File.separator+backName));
			logList.add(logObj);
		}
		
		this.formHM.put("logData", logList);
	}

}
