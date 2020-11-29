package com.hjsj.hrms.transaction.kq.register.ambiquity;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OrgSearchAmbiquityDataTrans extends IBusiness{
	
	   public void execute()throws GeneralException{		   
		   
		   String code = (String) this.getFormHM().get("code");	
		   String kind = (String) this.getFormHM().get("kind");	
		   ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");		   
		   String duration=(String)this.getFormHM().get("duration");
		   String start_date=(String)this.getFormHM().get("start_date");
		   String end_date=(String)this.getFormHM().get("end_date");
		   if(start_date==null||start_date.length()<=0||end_date==null||end_date.length()<=0)
		   {
			   ArrayList datelist=RegisterDate.getKqDayList(this.getFrameconn());
			   start_date=datelist.get(0).toString();
			   end_date=datelist.get(datelist.size()-1).toString();
		   }		   
		   if(duration==null||duration.length()<=0)
		   {
			   String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());
			   duration=kq_duration.substring(0,4);
		   }
		   
		   if(kind==null||kind.length()<=0)
		   {
			   kind="2";
		   }
		   if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		   {
			   kq_dbase_list=userView.getPrivDbList();  			   
		   }		   	
		   code=code.trim();
		   if(code==null||code.length()<=0)
			{
				 code="";
			}		  
		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		   ArrayList list= OrgRegister.newFieldItemList(fielditemlist);
			
			 String codesetid="UN";
			 if(!userView.isSuper_admin()) 
	        {
			 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
				codesetid="UM";	
	        }
			 list=OrgRegister.newFieldItemListQ09(list,codesetid);
			 String b0110=code;
			 if(b0110==null||b0110.length()<=0)
			 {
				 b0110=RegisterInitInfoData.getKqPrivCodeValue(userView);
			 }
			 
			ArrayList a0100whereIN= new ArrayList();
				
			
			String b0100s="";
			
				 for(int i=0;i<kq_dbase_list.size();i++)
				 {
						String dbase=kq_dbase_list.get(i).toString();
						String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
						a0100whereIN.add(whereA0100In);
				 }
				 String whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"e0122",a0100whereIN,"");
				 ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
				 StringBuffer b0110Str=new StringBuffer();		
				 for(int i=0;i<orgide0122List.size();i++)
				 {
						b0110Str.append("'"+orgide0122List.get(i).toString()+"',");
				 }
				 ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
				 String userOrgId=managePrivCode.getPrivOrgId();  
				 if(userOrgId!=null&&userOrgId.length()>0)
				 {
						b0110Str.append("'"+userOrgId+"',");
				 }			
				 if(b0110Str.toString()!=null&&b0110Str.toString().length()>0)
				 {
					 b0100s= b0110Str.toString().substring(0,b0110Str.length()-1);	
				 }else
				 {
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.date.no.record"),"",""));
				 }
			 
			 ArrayList sqllist=OrgRegister.getSqlstrOrg(list,b0100s ,duration,code,"q09");
			 this.getFormHM().put("sqlstr", sqllist.get(0).toString());	 
			 this.getFormHM().put("strwhere", sqllist.get(1).toString());
			 this.getFormHM().put("orderby",sqllist.get(2).toString());	
			 this.getFormHM().put("columns", sqllist.get(3).toString());		 	 
			 this.getFormHM().put("fielditemlist", list);		 
			 this.getFormHM().put("code",code);			 
		    
	}

}
