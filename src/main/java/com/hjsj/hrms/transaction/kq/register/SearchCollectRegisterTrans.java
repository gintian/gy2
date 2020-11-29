package com.hjsj.hrms.transaction.kq.register;


import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


public class SearchCollectRegisterTrans  extends IBusiness{
	//u
	   public void execute()throws GeneralException{		   
		   String error_flag="0";
		   String code = (String) this.getFormHM().get("code");	
		   String kind = (String) this.getFormHM().get("kind");	
		   ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
		   String select_flag=(String)this.getFormHM().get("select_flag");
			String select_name=(String)this.getFormHM().get("select_name");
			String select_pre=(String)this.getFormHM().get("select_pre");
		    this.getFormHM().put("select_flag",select_flag);
			this.getFormHM().put("select_name",select_name);	 
		   String showtype = (String) this.getFormHM().get("showtype");
		   String kq_duration=(String)this.getFormHM().get("kq_duration");
		   if(kind==null||kind.length()<=0)
		   {
			   kind="2";
		   }
		   if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		   {
			   kq_dbase_list=userView.getPrivDbList(); 			   
		   }
		   if(showtype==null||showtype.length()<=0)
			{
				showtype="all";
			}	
		   code=code.trim();
		   if(code==null||code.length()<=0)
			{
				 code="";
			}		  
		   
		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
		   if(kq_duration==null||kq_duration.length()<=0)
		   {
			   kq_duration =RegisterDate.getKqDuration(this.getFrameconn());			   
		   }		   
		   try{		  
			   ArrayList fieldlist=RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);;
			   FieldItem fielditem=new FieldItem();
			   fielditem.setFieldsetid("Q05");
			   fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
			   fielditem.setItemid("scope");
			   fielditem.setItemtype("A");
			   fielditem.setCodesetid("0");
			   fielditem.setVisible(true);
			   fieldlist.add(fielditem);   
			   ArrayList kq_daylist=RegisterDate.getKqDayList(this.frameconn);	
			   String start_date=kq_daylist.get(0).toString();
			   String end_date=kq_daylist.get(1).toString();	
			   ArrayList sql_db_list=new ArrayList();
			   if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
			   {
					sql_db_list.add(select_pre);
			   }else
			   {
					sql_db_list=kq_dbase_list;
			   }
			   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			   String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		       ArrayList sqllist = CollectRegister.getSqlstr2(fieldlist,sql_db_list,kq_duration,code,kind,"Q05",this.userView,showtype,where_c);	 
		       this.getFormHM().put("showtype",showtype);
		       this.getFormHM().put("sqlstr", sqllist.get(0).toString());
			   this.getFormHM().put("columns", sqllist.get(3).toString());
			   this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
			   this.getFormHM().put("orderby", sqllist.get(2).toString());
			   this.getFormHM().put("fielditemlist", fieldlist);			  
			   this.getFormHM().put("collectdate", CollectRegister.getMonthRegisterDate(start_date,end_date));			  
			   this.getFormHM().put("code",code);			   
			   this.getFormHM().put("kq_dbase_list",kq_dbase_list); 
		    }catch(Exception e){
			      e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e); 
		   }
		   this.getFormHM().put("error_flag",error_flag);
	}
	   
}
