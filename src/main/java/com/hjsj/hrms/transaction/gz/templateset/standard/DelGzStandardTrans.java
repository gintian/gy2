package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DelGzStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		 
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  operate="";
			String  id="";
			if(hm!=null){
				operate=(String)hm.get("operate");
				id=(String)hm.get("id");
			}
				
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList standardlist=(ArrayList)this.getFormHM().get("selectedList");
			StringBuffer whl_str=new StringBuffer("");
			String pkg_id=(String)this.getFormHM().get("pkg_id"); 
			String pkgIsActive=(String)this.getFormHM().get("pkgIsActive");
			
			if(standardlist!=null)
			{
				for(Iterator t=standardlist.iterator();t.hasNext();)
	        	{
	        		LazyDynaBean a=(LazyDynaBean)t.next();
	        		whl_str.append(" or id="+a.get("id").toString());
	        	}
				if(whl_str.length()>0)
				{
					if("del".equals(operate)){      //删除
					
							dao.delete("delete from gz_stand_history where pkg_id="+pkg_id+" and ("+whl_str.substring(3)+")",new ArrayList());
							dao.delete("delete from gz_item_history where pkg_id="+pkg_id+" and ("+whl_str.substring(3)+")",new ArrayList());
					  	    if("1".equals(pkgIsActive))
					  	    {
					  	    	dao.delete("delete from gz_stand where ("+whl_str.substring(3)+")",new ArrayList());//将gz_stand表里的也删除
					  	    	dao.delete("delete from gz_item where ("+whl_str.substring(3)+")",new ArrayList());
					  	    }
					}
					else if("resetName".equals(operate))  //重命名
					{
						String gzStandardName=(String)this.getFormHM().get("gzStandardName");
						
						dao.update("update gz_stand_history set name='"+gzStandardName+"' where  pkg_id="+pkg_id+" and id="+id+"");
						if("1".equals(pkgIsActive))
							dao.update("update gz_stand  set name='"+gzStandardName+"' where id="+id+"");
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}
