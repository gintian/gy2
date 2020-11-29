package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ChangeAddOrgKindTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList codesetlist=new ArrayList();
		String code=(String)this.getFormHM().get("code");
		//String kind=(String)this.getFormHM().get("kind");
		String codesetid=(String)this.getFormHM().get("codesetid");	
		if(codesetid==null || codesetid.trim().length()==0)
			codesetid="UN";
		String labelmessage="";
		String first="1";
		int len=30;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag from organization where parentid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid ");
		//strsql.append(" and codesetid='"+codesetid+"'");
		strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag from vorganization where parentid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid ");
		strsql.append(" order by codeitemid desc");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(strsql.toString());
			boolean b = false;
			while(this.frowset.next())
			{		
				b = true;
				first="0";
				this.getFormHM().put("first",first);
			    String chilecode=this.frowset.getString("codesetid");
			    String codeitemid=this.frowset.getString("codeitemid");
			    int grade=this.frowset.getInt("grade");
			    this.getFormHM().put("grade",String.valueOf(grade));
			    if(chilecode!=null)
			    {
			    	//if(codesetid.equalsIgnoreCase(chilecode)){
				    	if(code!=null)
				    	{
				    		len=codeitemid.trim().length()-code.trim().length();
				    	}
				    	else
				    	{
				    		len=codeitemid.trim().length();
				    	}
				    	labelmessage=ResourceFactory.getProperty("label.org.childmessage") + len;
				    	AddOrgInfo addOrgInfo=new AddOrgInfo();
					    //codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
				    	codeitemid=addOrgInfo.GetNext(codeitemid,code);
					    this.getFormHM().put("codeitemid",codeitemid);
					    break;
			    	/*}
			    	else{
			    		if(code!=null)
					      {
					        len=30-code.trim().length();
					      }
			    		labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
			    		AddOrgInfo addOrgInfo=new AddOrgInfo();
					    //codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
			    		codeitemid=addOrgInfo.GetNext(codeitemid,code);
					    this.getFormHM().put("codeitemid",codeitemid);
					    this.getFormHM().put("first","1");
			    	}*/
			    }
			    //labelmessage=ResourceFactory.getProperty("label.org.childmessage") + len;
		    }
			if(b){
				
			}else
		    {
		    	/*first="1";
		    	strsql.delete(0,strsql.length());
				strsql.append("select grade from organization where codeitemid='");
		    	strsql.append(code);
		    	strsql.append("' and codesetid='");
		        strsql.append(codesetid);
		        strsql.append("'");
		    	this.frowset=dao.search(strsql.toString());
		    	int grade=1;
		    	if(this.frowset.next())
		    	{
		    	  grade=this.frowset.getInt("grade");
		    	  grade=grade + 1;

		    	}
		    	this.getFormHM().put("grade",String.valueOf(grade));
		    	
		      if(code!=null && code.trim().length()>0)
		      {
		        len=30-code.trim().length();
		        this.getFormHM().put("first","1");
		      }
		      else
		      {
		    	  strsql.delete(0,strsql.length());
				  strsql.append("select ");
				  strsql.append(Sql_switcher.length("codeitemid"));
				  strsql.append(" as codeitemidlen from organization where parentid=codeitemid and codesetid='");
		          strsql.append(codesetid);
		          strsql.append("'");			
			      this.frowset=dao.search(strsql.toString()); 
			      if(this.frowset.next())
			      {
			    	  len=this.frowset.getInt("codeitemidlen");
			    	  this.getFormHM().put("first","0");
			      }
			      else
			      {
			    	  this.getFormHM().put("first","1");
			      }
		      }*/
		      String codeitemid="";
		      if(code!=null && code.trim().length()>0)
		      {
		    	 strsql.delete(0,strsql.length());
		    	 strsql.append("select grade from organization where codeitemid='");
		    	 strsql.append(code);
		    	 strsql.append("'");
		    	 this.frowset=dao.search(strsql.toString());
		    	 int grade=1;
	    		 if(this.frowset.next())
		    	 {
		    	   grade=this.frowset.getInt("grade");
		    	   grade=grade + 1;
		    	 }
	    		 len=30-code.trim().length();
		         labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
		         this.getFormHM().put("grade",String.valueOf(grade));
				 this.getFormHM().put("first","1");
				 AddOrgInfo addOrgInfo=new AddOrgInfo();
			     codeitemid=addOrgInfo.getChildCodeitemid("",code,len);
			     this.getFormHM().put("codeitemid",codeitemid);
		      }
		      else
		      {
		    	  len=30;
		    	  strsql.delete(0,strsql.length());
		    	  //strsql.append("select codeitemid from organization where codeitemid=parentid and codesetid='");
		    	  //strsql.append(codesetid);
		    	  //strsql.append("'");
		    	  strsql.append("select codeitemid from organization where codeitemid=parentid ");
		    	  strsql.append(" order by codeitemid desc");
			      this.frowset=dao.search(strsql.toString());
			      if(this.frowset.next())
			      {
			    	  len=this.frowset.getString("codeitemid").trim().length();
			    	  //String sql="select * from organization where parentid=codeitemid and codesetid='"+codesetid+"'";
			    	  ///sql=sql+" order by codeitemid desc";
			    	  //this.frowset=dao.search(sql);
			    	  //if(this.frowset.next())
			    	  //{
			    		  codeitemid=this.frowset.getString("codeitemid");
			    	  //}
			      }
		    	  labelmessage=ResourceFactory.getProperty("label.org.firstchildmessage") + len;
		    	  AddOrgInfo addOrgInfo=new AddOrgInfo();
			      //codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
		    	  codeitemid=addOrgInfo.GetNext(codeitemid,code);
			      this.getFormHM().put("grade","1");
				  this.getFormHM().put("first","1");  
				  this.getFormHM().put("codeitemid",codeitemid);
		      }
		    }
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		this.getFormHM().put("codesetlist",codesetlist);
		this.getFormHM().put("len",String.valueOf(len));
		this.getFormHM().put("labelmessage",labelmessage);		
		this.getFormHM().put("codeitemdesc","");
		this.getFormHM().put("isrefresh","no");
	}

}
