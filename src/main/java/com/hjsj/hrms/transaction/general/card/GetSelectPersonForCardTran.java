package com.hjsj.hrms.transaction.general.card;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class GetSelectPersonForCardTran  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ArrayList persList=(ArrayList)this.getFormHM().get("pers");
		if(persList==null||persList.size()<=0)
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		try
		{
			String pers="";
			String[] mess;
			String a0100="";
			String nbase="";
			String sql="";
			String tag="";
		    for(int i=0;i<persList.size();i++)
		    {
		    	pers=(String)persList.get(i);
		    	if(pers==null||pers.length()<=0)
		    		continue;
		    	mess=pers.split("`");
		    	if(mess==null||mess.length!=2)
		    		continue;
		    	nbase=mess[0];
		    	a0100=mess[1];
		    	sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
		    	this.frowset=dao.search(sql);
		    	if(this.frowset.next())
		    	{
		    		tag="<NBASE>"+nbase+"</NBASE><ID>"+a0100+"</ID><NAME>"+this.frowset.getString("a0101")+"</NAME>";
		    		CommonData dataobj = new CommonData(tag,a0100);
				    list.add(dataobj);
		    	}
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
		}
		this.getFormHM().put("personlist",list);
	}

}
