package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveYkcardMustTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		String org="";
		if(!this.userView.isSuper_admin())
			org=userView.getUserOrgId();
		 XmlParameter xml=new XmlParameter("UN",org,"00");
		try
        {
		   String mustflag=(String)this.getFormHM().get("mustflag");
		   String type=(String)this.getFormHM().get("type");
		   if(type==null)
		   	  type="";		  
		  
		   xml.WriteOutParameterXml("SS_SETCARD",false,"",false,"","","",mustflag,this.getFrameconn());		 		   
        }catch(Exception e){
           e.printStackTrace();
        }   
        xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),"all");	
		String mustflag=xml.getMustflag();
		String musterid=xml.getMusterid();
		ArrayList musteredlist=xml.getMusteredlist();
		CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());		
		ArrayList  mustmesslist=cardConstantSet.getMessMessList(musteredlist);
		String mustidmess=cardConstantSet.getMustmess(musterid,"1");
	    ArrayList codenamelist=xml.getCodenamelist();
	    String codename="";
		if(codenamelist!=null&&codenamelist.size()>0)
		   codename=(String)codenamelist.get(0);
		if(mustflag==null||mustflag.length()<=0)
			mustflag="0";	
		ArrayList codesetlist=xml.getCodesetlist();
		this.getFormHM().put("codenamelist",codenamelist);
		this.getFormHM().put("mustmesslist",mustmesslist);
		this.getFormHM().put("mustidmess",mustidmess);
		this.getFormHM().put("codename",codename.toUpperCase());
		this.getFormHM().put("codesetlist",codesetlist);
		this.getFormHM().put("musteredlist",musteredlist);
		this.getFormHM().put("mustflag",mustflag);
	}
}
