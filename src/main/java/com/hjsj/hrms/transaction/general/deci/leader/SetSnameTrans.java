package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 常用统计
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 8, 2007:4:14:15 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetSnameTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		String sql="select id,name  from sname where infokind=1 order by id";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list =new ArrayList();
		try
		{
			this.frowset=dao.search(sql);
			CommonData dataobj =null;
			while(this.frowset.next())
			{
				dataobj=new CommonData();
				dataobj.setDataName(this.frowset.getString("name"));
				dataobj.setDataValue(this.frowset.getString("id"));
				list.add(dataobj);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("snamelist",list);
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());	
		String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
		if(gcond==null||gcond.length()<=0)
			gcond="";
		this.getFormHM().put("selectsname",leaderParam.getSelectSname(gcond));
	}
    
}
