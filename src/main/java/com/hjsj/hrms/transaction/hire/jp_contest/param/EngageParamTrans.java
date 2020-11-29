package com.hjsj.hrms.transaction.hire.jp_contest.param;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
 *<p>Title:EngageParamTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class EngageParamTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		EngageParamXML epXML = new EngageParamXML(this.getFrameconn());
		EngageParam ep=new EngageParam(this.getFrameconn());
		String attent_view_field = epXML.getTextValue(EngageParamXML.ATTENT_VIEW);
		if(attent_view_field==null||attent_view_field.length()<=0)
			attent_view_field="";
		ArrayList list = ep.getFields(attent_view_field);
		String attent_view_mess = this.getMess(list);
		this.getFormHM().put("attent_view_mess",attent_view_mess);
		
		String employ_card=epXML.getTextValue(EngageParamXML.CARD);	
		if(employ_card==null||employ_card.length()<=0)
			employ_card="";
		list=ep.getSelectRname(employ_card);
		String card_mess=getMess(list);
		this.getFormHM().put("card_mess",card_mess);
		
		String maxpos_mess = epXML.getTextValue(EngageParamXML.APP_COUNT);
		if(maxpos_mess==null||maxpos_mess.length()<=0)
			maxpos_mess="";
		this.getFormHM().put("maxpos",maxpos_mess);
		
		String app_view_mess = epXML.getTextValue(EngageParamXML.APP_VIEW);
		if(app_view_mess==null||app_view_mess.length()<=0)
			app_view_mess="";
		this.getFormHM().put("app_view_mess",ep.getAppViewMess(app_view_mess));
		String template = epXML.getTextValue(EngageParamXML.TEMPLATE);
		if(template==null||template.length()<=0)
			template="";
		this.getFormHM().put("strTemplate",template);
		this.getFormHM().put("template",getTenplateList(template));
		
	}
	private String getMess(ArrayList list)
	{
		StringBuffer mess=new StringBuffer();
		if(list==null||list.size()<=0)
			return "";
		int r=1;
		for(int i=0;i<list.size();i++)
		{
			 CommonData dataobj =(CommonData)list.get(i);
			 mess.append(dataobj.getDataName());
			 if(r%5==0)
    			   mess.append("<br>");
    		   else
    			 mess.append(",");  
    	    r++;
		}
		return mess.toString();
	}
	private String getTenplateList(String selectedId)
    {
		String value="";
    	ArrayList list=new ArrayList();
    	StringBuffer strsql=new StringBuffer();
    	if(selectedId==null||selectedId.length()<=0)
    	{
    		
            this.formHM.put("tenplatelist", null);
    		return value;
    	}	
    	strsql.append("select tabid,name from template_table where ");
    	strsql.append("tabid in (");
    	String[] tIds=selectedId.split(",");
    	for(int i=0;i<tIds.length;i++)
    	{
    		strsql.append("'"+tIds[i]+"',");
    	}
    	strsql.setLength(strsql.length()-1);
    	strsql.append(")");
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{			
				if (!userView.isHaveResource(IResourceConstant.RSBD, rset.getString("tabid")))
					continue;	
				CommonData vo=new CommonData();   		
				vo.setDataValue(rset.getString("tabid"));
	            vo.setDataName(rset.getString("tabid")+":"+rset.getString("name"));
	            list.add(vo);
	            value+=rset.getString("tabid")+":"+rset.getString("name")+" \r\n";
			}
		}
		catch(Exception ex)
		{
			
		}
		return value;
    }
}
