package com.hjsj.hrms.transaction.general.sys.options;

import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SavePortalItemAdjustTrans extends IBusiness {

	public void execute() throws GeneralException {
	    String[] str_valueList=(String[])this.getFormHM().get("right_fields");	 
	    ArrayList nodeslist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
		ArrayList savenodesList=new ArrayList();
	    try
		{
	    	for(int i=0;i<str_valueList.length;i++)
			{
		    	for(int j=0;nodeslist!=null && j<nodeslist.size();j++)
				{
					ArrayList attributelist=(ArrayList)nodeslist.get(j);
					for(int n=0;n<attributelist.size();n++)
					{
						LabelValueView item=(LabelValueView)attributelist.get(n);
						if("id".equals(item.getLabel()) && str_valueList[i].equals(item.getValue()))
							savenodesList.add(attributelist);							
					}
				}	
			}
	    	new PortalTailorXml().WriteOutParameterXml("SYS_PARAM",this.getFrameconn(),savenodesList,"门户定制",this.userView.getUserName(),String.valueOf(this.userView.getStatus()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
