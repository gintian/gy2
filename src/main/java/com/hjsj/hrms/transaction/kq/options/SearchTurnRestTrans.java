package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.KqRestTurn;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTurnRestTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    HashMap hashMap = (HashMap)this.getFormHM().get("requestPamaHM");
	    String gw_flag = (String)hashMap.get("flag");
	    hashMap.remove("flag");
	    this.getFormHM().put("gw_flag", gw_flag);
	    
		String mess=(String)this.getFormHM().get("mess");	
		String mess2=(String)this.getFormHM().get("mess2");
		String init=(String)this.getFormHM().get("init");	
		if(mess==null||mess.length()<=0)
			mess="";

		if(mess2==null||mess2.length()<=0)
			mess2="";

		if(init==null||init.length()<=0)
			init="";

		if("2".equals(mess)&& "0".equals(init)&& "1".equals(mess2))
		{
			mess="";
			this.getFormHM().put("init","1");
		}
		if("3".equals(mess)&& "0".equals(init)&& "1".equals(mess2))
		{
			mess="";
			this.getFormHM().put("init","1");
		}
		if("4".equals(mess)&& "0".equals(init)&& "1".equals(mess2))
		{
			mess="";
			this.getFormHM().put("init","1");
		}
	      StringBuffer sb = new StringBuffer();
	      ContentDAO cdao =new ContentDAO(this.getFrameconn());
	      ArrayList feastList=new ArrayList();
	      try{
	    	   ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
//	    	   String b0110=managePrivCode.getPrivOrgId();
	    	   String b0110=managePrivCode.getUNB0110();    
	    	   
	    	   ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
	    	   String b0110_return=restList.get(1).toString();
	    	   String b0110s="";
	    	   if(b0110_return.equals(b0110)||b0110_return.length()>=(b0110).length())
	    	   {
	    		   b0110s="'"+b0110_return+"'";
	    	   }else
	    	   {
	    		   KqRestTurn kqRestTurn= new KqRestTurn();
		    	   b0110s=kqRestTurn.getb0110s(b0110,b0110_return,"UN",this.getFrameconn());
	    	   }
	    	   
	    	   sb.append("select * from kq_turn_rest ");
	    	   if(!userView.isSuper_admin())
	   		   {
	    	      sb.append("where b0110 in ("+b0110s+")");
	   		   }	  
	    	   sb.append(" order by turn_id,b0110 ");
	             this.frowset = cdao.search(sb.toString());
	             while(this.frowset.next())
	             {
	            	 RecordVo vo=new RecordVo("kq_turn_rest"); 
	            	 String b0110UN=this.getFrowset().getString("b0110");
	            	 String codeset="";
	            	 String codeid="";
	            	 String description="";
	            	 if(b0110UN!=null&&b0110UN.length()>2)
	            	 {
	            		 codeset=b0110UN.substring(0,2);
	            		 codeid=b0110UN.substring(2);
	            		 description=AdminCode.getCodeName(codeset,codeid);
	            	 } 
	            	 vo.setString("description",description);
	            	 vo.setString("turn_id",this.getFrowset().getString("turn_id"));
	            	 vo.setString("b0110",this.getFrowset().getString("b0110"));
	            	 vo.setDate("week_date",this.getFrowset().getDate("week_date"));
	            	 vo.setDate("turn_date",this.getFrowset().getDate("turn_date"));
	            	 feastList.add(vo);
	             }
	        	
	    
	        }catch(Exception se){
	    	  se.printStackTrace();
	 	      throw GeneralExceptionHandler.Handle(se);
	      }
	        this.getFormHM().put("selist",feastList);
	        this.getFormHM().put("tid","");
	        this.getFormHM().put("rdate","");
	        this.getFormHM().put("tdate","");
	        
	        if(mess==null|| "0".equals(mess))
        	  this.getFormHM().put("mess","0");
	          else if("3".equals(mess))
	          {
	        	  this.getFormHM().put("init","0");
	        	  this.getFormHM().put("mess","3");
	        	  this.getFormHM().put("mess2","1");
	            }else if("2".equals(mess))
	             {
	        		this.getFormHM().put("init","0");
		        	this.getFormHM().put("mess","2");
		        	this.getFormHM().put("mess2","1");
		        	
	            }else if("4".equals(mess))
	             {
	        		this.getFormHM().put("init","0");
		        	this.getFormHM().put("mess","4");
		        	this.getFormHM().put("mess2","1");
	            }
	           else
	        	 this.getFormHM().put("mess","0");
	        this.getFormHM().put("turnRest_flag","0");
	}

}
