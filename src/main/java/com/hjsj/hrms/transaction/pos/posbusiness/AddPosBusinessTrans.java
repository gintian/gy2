/*
 * Created on 2005-12-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddPosBusinessTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String a_code=(String)this.getFormHM().get("a_code");
		//String codesetid=(String)this.getFormHM().get("codesetid");
		/**代码维护*/
		String codesetid=a_code.substring(0,2);
		String labelmessage="";
		String first="1";
		int len=30;
		StringBuffer strsql=new StringBuffer();
		if(a_code!=null && a_code.length()==2)
		{
			strsql.append("select codeitemid from codeitem where codeitemid=parentid and codesetid='");
			strsql.append(codesetid);
			strsql.append("'");
		}
		else if(a_code!=null && a_code.length()>2)
		{
			strsql.append("select codeitemid from codeitem where parentid='");
			strsql.append(a_code!=null && a_code.length()>=2?a_code.substring(2):"");
			//strsql.append(a_code);
			strsql.append("' and codeitemid<>parentid and codesetid='");
			strsql.append(codesetid);
			strsql.append("'");
		}else
		{
			strsql.append("select codeitemid from codeitem where codeitemid=parentid and codesetid='");
			strsql.append(codesetid);
			strsql.append("'");
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{			
			cat.debug("------strsql----->" + strsql);
			if(strsql!=null && strsql.toString().length()>0)
			   this.frowset=dao.search(strsql.toString());
			if(this.frowset !=null && this.frowset.next())
			{		
				first="0";
			    String chilecode=this.frowset.getString("codeitemid");
			    if(chilecode!=null)
			    {
			    	if(a_code!=null)
			    	{
			    		len=chilecode.trim().length()-a_code.trim().length()+2;
			    	}
			    	else
			    	{
			    		len=chilecode.trim().length();
			    	}
			    }
			    this.getFormHM().put("first",first);
			    labelmessage=ResourceFactory.getProperty("label.org.childmessage") + len;
		    }else
		    {
		    	first="1";		    	
		    	this.getFormHM().put("first",first);
		    	RecordVo rvo = new RecordVo("codeitem");
				  Map lenmap = rvo.getAttrLens();
				  int codeitemidlen = Integer.parseInt((String)lenmap.get("codeitemid"));
		      if(a_code!=null)
		        len=codeitemidlen-a_code.trim().length()+2;
		      labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
		    }
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("len",String.valueOf(len));
		this.getFormHM().put("labelmessage",labelmessage);
		this.getFormHM().put("codeitemid","");
		this.getFormHM().put("codeitemdesc","");
		this.getFormHM().put("corcode","");
		this.getFormHM().put("isrefresh","no");
		if(a_code!=null && a_code.length()>2)
			this.getFormHM().put("code",a_code.substring(2));
		else
			this.getFormHM().put("code",codesetid);
	}

}
