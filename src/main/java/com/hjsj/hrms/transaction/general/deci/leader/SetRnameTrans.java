package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 登记表
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 8, 2007:5:12:12 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetRnameTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		String sql="select tabid,name  from rname where FlagA = 'B' order by tabid";
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
		LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());	
		String unit_card=leadarParamXML.getTextValue(LeadarParamXML.UNIT_CARD);	
		if(unit_card==null||unit_card.length()<=0)
			unit_card="";
		this.getFormHM().put("selectrname",leaderParam.getSelectRname(unit_card));
	}
   
}
