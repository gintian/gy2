package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 保存薪酬表设置
 * <p>Title:SaveConstantSetTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 27, 2007 2:57:53 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveConstantSetTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	
	public void execute() throws GeneralException {
		ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");			
		String fashion_flag=(String)this.getFormHM().get("fashion_flag");
		// String codesetname=(String)this.getFormHM().get("codesetname");
		String codename=(String)this.getFormHM().get("codename");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		boolean mobapp = Boolean.parseBoolean((String)this.getFormHM().get("mobapp"));
		String types="ok";
		if(fashion_flag==null||fashion_flag.length()<=0)
			fashion_flag="";
		
		StringBuffer cardids=new StringBuffer();
		if(code_fields==null||code_fields.size()<=0)
			cardids.append("");
		else
		{
			for(int i=0;i<code_fields.size();i++)
			{
				cardids.append(code_fields.get(i)+"`");
			}
			cardids.setLength(cardids.length()-1);
		}
		String org="";
		if(!this.userView.isSuper_admin())
			org=userView.getUserOrgId();
		if("0".equals(fashion_flag)) {
			XmlParameter xml=new XmlParameter("UN",org,"00");
			xml.WriteOutParameterXml("SS_SETCARD",true,cardids.toString(),false,"","",fashion_flag,"",this.getFrameconn(), mobapp);	
		} else if("1".equals(fashion_flag)) {
			XmlParameter xml=new XmlParameter(this.getFrameconn(),org,fashion_flag);
			xml.removeContent(codename);
			ArrayList codesetlist= getChilds(codename);
			xml.initCodeChile(codename,codesetlist);
			xml.setCodeChild(codeitemid,codename,cardids.toString(),mobapp);
			xml.setCodeFlag();
			xml.saveParameter();
		}else
		{
			types="false";
		}
		CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
		String mess=cardConstantSet.getCardMesslist(cardids.toString());
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
