package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.File;
import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 11, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class DeleteTemplateTrans  extends IBusiness {
	
	public void execute() throws GeneralException {
		try 
		{
			ArrayList deletelist=(ArrayList)this.getFormHM().get("selectedlist");   
			String path = SafeCode.decode((String)this.getFormHM().get("path"));
			if(deletelist.size()>0)
        	{
				for(int i=0;i<deletelist.size();i++)
 	            {   	
					LazyDynaBean rec=(LazyDynaBean)deletelist.get(i); 
					String fileName = (String)rec.get("filename"); 
					File file = new File(path,fileName);
					if(file.exists())
						file.delete();
 	            }  
    			
        	}else
        		return ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

