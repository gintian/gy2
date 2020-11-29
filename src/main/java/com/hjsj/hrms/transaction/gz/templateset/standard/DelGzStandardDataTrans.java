package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DelGzStandardDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String type=(String)this.getFormHM().get("type");
			if(type==null)
			{
				String current_item=(String)this.getFormHM().get("current_id");
				
				String value=(String)this.getFormHM().get("value");
				String[] temps=value.split(",");
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						String[] temps1=temps[i].split("~");
						String[] temps2=temps1[1].replaceAll("＃", "#").split("#");
						whl.append(" or  (item='"+temps2[0]+"' and item_id='"+temps2[1]+"' )");
					}
				}
				dao.delete("delete from gz_stand_date where "+whl.substring(3),new ArrayList());
				
				String isSub="0";
				this.frowset=dao.search("select * from gz_stand_date where item='"+current_item+"'");
				if(this.frowset.next())
					isSub="1";
				this.getFormHM().put("isSub",isSub);
				
				
			}
			else
			{
				
				String itemid=(String)this.getFormHM().get("itemid");
				if("B0110".equalsIgnoreCase(itemid))
				{
					this.getFormHM().put("itemName","单位");
				}
				else if("E0122".equalsIgnoreCase(itemid))
				{
					this.getFormHM().put("itemName","部门");
				}
				else if("E01A1".equalsIgnoreCase(itemid))
				{
					this.getFormHM().put("itemName","职位");
				}
				else
				{
					this.frowset=dao.search("select itemdesc from fieldItem where itemid='"+itemid+"'");
					if(this.frowset.next())
						this.getFormHM().put("itemName",this.frowset.getString("itemdesc"));
				}
				this.getFormHM().put("type",type);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
