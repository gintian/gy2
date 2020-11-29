package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPositionListTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100=(String)hm.get("a0100");
		ArrayList list=getPositionList(a0100);

		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("positionList",list);
	}
	
	public ArrayList getPositionList(String a0100)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer isExistPositionID=new StringBuffer("");
			this.frowset=dao.search("select * from zp_pos_tache where a0100='"+a0100+"'");
			while(this.frowset.next())
			{
				isExistPositionID.append(this.frowset.getString("zp_pos_id"));
				isExistPositionID.append(",");
			}
			
			StringBuffer sql=new StringBuffer("select z0301,un.codeitemdesc un,um.codeitemdesc um,po.codeitemdesc pos,z01.z0103  from z03 ");
						 sql.append(" left join z01 on  z03.z0101=z01.z0101 ");
						 sql.append(" left join organization un on z03.z0321=un.codeitemid ");
						 sql.append(" left join organization um on z03.Z0325=um.codeitemid ");
						 sql.append(" left join organization po on z03.Z0311=po.codeitemid ");
						 sql.append(" where z0129='04' order by z03.z0101 ");
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				String z0301=this.frowset.getString("z0301");
				String operate="";
				if(isExistPositionID.indexOf(z0301+",")!=-1)
					operate=" checked ";
				String ids="<input type='radio' name='id' value='"+z0301+"'"+operate+" />";
				abean.set("id",ids);
				abean.set("pos",this.frowset.getString("pos"));
				
				if(this.frowset.getString("um")!=null)
					abean.set("um",this.frowset.getString("um"));
				else 
					abean.set("um"," ");
				if(this.frowset.getString("un")!=null)
					abean.set("un",this.frowset.getString("un"));
				else 
					abean.set("un"," ");
				abean.set("z0103",this.frowset.getString("z0103"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	

}
