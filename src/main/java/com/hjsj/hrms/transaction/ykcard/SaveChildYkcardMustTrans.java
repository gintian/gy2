package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SaveChildYkcardMustTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");			
		String mustflag=(String)this.getFormHM().get("mustflag");
		String codesetname=(String)this.getFormHM().get("codesetname");
		String codename=(String)this.getFormHM().get("codename");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String types="ok";
		if(mustflag==null||mustflag.length()<=0)
			mustflag="";
		
		StringBuffer mustids=new StringBuffer();
		if(code_fields==null||code_fields.size()<=0)
			mustids.append("");
		else
		{
			for(int i=0;i<code_fields.size();i++)
			{
				mustids.append(code_fields.get(i)+"`");
			}
			mustids.setLength(mustids.length()-1);
		}
		String org="";
		if(!this.userView.isSuper_admin())
			org=userView.getUserOrgId();
		if("0".equals(mustflag))
		{
			XmlParameter xml=new XmlParameter("UN",org,"00");
			xml.WriteOutParameterXml("SS_SETCARD",false,"",true,mustids.toString(),"","",mustflag,this.getFrameconn());	
		}else if("1".equals(mustflag))
		{
			XmlParameter xml=new XmlParameter(this.getFrameconn(),org,mustflag);
			xml.removeContent(codename);
			ArrayList codesetlist= getChilds(codename);
			xml.initCodeChile(codename,codesetlist);
			xml.setMustChild(codeitemid,codename,mustids.toString());
			xml.setMustFlag();
			xml.saveParameter();
		}else
		{
			types="false";
		}
		CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
		String mess=cardConstantSet.getMustmess(mustids.toString(),"1");
		this.getFormHM().put("types",types);
		this.getFormHM().put("mess",mess);
	}
	private ArrayList  getChilds(String codename)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer sql=new StringBuffer();
    	sql.append("select codesetid from  fielditem");
    	sql.append(" where itemid='"+codename+"'");
    	ArrayList codesetlist=new ArrayList();
    	try
    	{
    		String codesetid="";
    		this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    			codesetid=this.frowset.getString("codesetid");
    		if(codesetid==null||codesetid.length()<=0)
    			return codesetlist;
    		sql=new StringBuffer();
        	sql.append("SELECT codeitemid,codesetid FROM codeitem");
        	sql.append(" where codesetid='"+codesetid+"'");
        	this.frowset=dao.search(sql.toString());
    		CommonData dataobj=null;
    		while(this.frowset.next())
    		{
    			dataobj=new CommonData();
        		dataobj.setDataName(this.frowset.getString("codesetid"));
        		dataobj.setDataValue(this.frowset.getString("codeitemid"));
        		codesetlist.add(dataobj);        		
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return codesetlist;
    	
    }	

}
