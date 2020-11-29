package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SetRnameTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SetRnameTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		String sql="select tabid,name  from rname where FlagA = 'A' order by tabid";
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
				dataobj.setDataValue(this.frowset.getString("tabid"));
				list.add(dataobj);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("rnamelist",list);
		LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
		EngageParamXML engageParamXML=new EngageParamXML(this.getFrameconn());	
		String employ_card=engageParamXML.getTextValue(EngageParamXML.CARD);	
		if(employ_card==null||employ_card.length()<=0)
			employ_card="";
		this.getFormHM().put("selectrname",leaderParam.getSelectRname(employ_card));
	}
   
}
