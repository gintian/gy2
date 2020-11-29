package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteCandidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
		StringBuffer a0100=new StringBuffer("");
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)list.get(i);
			String  id=(String)abean.get("id");
			String[]  ids=id.split("/");
		//	a0100.append(",'"+ids[0]+"'");
			a0100.append(" or (a0100='"+ids[0]+"' and zp_pos_id='"+ids[1]+"')");
		}
		
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			/*String dbname="";
			ArrayList setList=new ArrayList();
			this.frowset=dao.search("select * from constant where constant='ZP_SUBSET_LIST' or  constant='ZP_DBNAME' ");
			while(this.frowset.next())
			{
				if(this.frowset.getString("constant").equalsIgnoreCase("ZP_DBNAME"))
					dbname=Sql_switcher.readMemo(this.frowset,"str_value");
				if(this.frowset.getString("constant").equalsIgnoreCase("ZP_SUBSET_LIST"))
				{
					
					String set_value=Sql_switcher.readMemo(this.frowset,"str_value");
					if(set_value.indexOf(",")==-1)
					{
						setList.add(set_value);
					}
					else
					{
						String[] fielsSetID=set_value.split(",");
						for(int i=0;i<fielsSetID.length;i++)
						{
							setList.add(fielsSetID[i]);
						}
					}
					
				}
			}*/
			dao.delete("delete from zp_pos_tache where "+a0100.substring(3),new ArrayList());
		//	dao.delete("delete from zp_pos_tache where a0100 in ("+a0100.substring(1)+")",new ArrayList());
			/*for(Iterator t=setList.iterator();t.hasNext();)
			{
				String setName=(String)t.next();
				dao.delete("delete from "+dbname+setName+" where a0100 in ("+a0100.substring(1)+")",new ArrayList());
			}*/
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		

	}

}
