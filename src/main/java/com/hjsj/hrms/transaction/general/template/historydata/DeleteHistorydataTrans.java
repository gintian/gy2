package com.hjsj.hrms.transaction.general.template.historydata;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:DeleteTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:06 8, 2010</p> 
 *@author xieguiquan
 *@version 4.0
 */
public class DeleteHistorydataTrans  extends IBusiness {
	
	public void execute() throws GeneralException {
		try 
		{
			ArrayList deletelist=(ArrayList)this.getFormHM().get("selectedlist");   
			HashMap HM=(HashMap)this.getFormHM().get("requestPamaHM");   
			String  ids=(String) HM.get("ids");//获取选中的档案号
//			String path = SafeCode.decode((String)this.getFormHM().get("path"))
			this.getFormHM().remove("selectedlist");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(deletelist.size()>0)
        	{
				for(int i=0;i<deletelist.size();i++)
 	            {   	
					LazyDynaBean bean=(LazyDynaBean)deletelist.get(i); 
					String id =(String)bean.get("id");
					String dSQL="delete from template_archive where id= "+id;
					dao.delete(dSQL, new ArrayList());
 	            }  
    			
        	}else if(StringUtils.isNotBlank(ids)){//如果选中档案号不为空，说明是人事异动历史记录删除
        		String[] id=ids.split(",");
        		for(int i=0;i<id.length;i++){
        			String dSQL="delete from template_archive where id= "+id[i];
					dao.delete(dSQL, new ArrayList());
        		}
        	}else
        		return ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

