package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.DeleteFile;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.log4j.Category;

import java.io.File;
/**
 * 
 *<p>Title:DeleteDempFileTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 14, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class DeleteDempFileTrans extends IBusiness {

	private Category log = Category.getInstance(DeleteDempFileTrans.class.getName());
	public void execute() throws GeneralException {
		try{
			String filename=System.getProperty("java.io.tmpdir");//+System.getProperty("file.separator")+"ole-52475.jpg";
			if("weblogic".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver"))){
				int index = filename.indexOf('~');
				if(index!=-1){
					filename=System.getProperty("user.home")+filename.substring(index+2);
				}
			}
			DeleteFile df = new DeleteFile();
			df.deleteDirs(new File(filename));
			if("oracle".equalsIgnoreCase(SystemConfig.getPropertyValue("dbserver"))){
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.update("Purge recyclebin");
			}
			log.error("临时文件目录："+filename);
		}catch(Exception e){
			log.error("异常信息："+e.getMessage());
			e.printStackTrace();
		}
	}

}
