package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 薪酬高级花名册
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 1, 2007:10:16:26 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SearchYkcardMustTrans extends IBusiness {


	public void execute() throws GeneralException {
		String org="";
		if(!this.userView.isSuper_admin())
			org=userView.getUserOrgId();
		
		XmlParameter xml=new XmlParameter("UN",org,"00");
		//检查一下代码是否同步
		xml.syncYkCardConfigCode("SS_SETCARD",this.frameconn);
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
