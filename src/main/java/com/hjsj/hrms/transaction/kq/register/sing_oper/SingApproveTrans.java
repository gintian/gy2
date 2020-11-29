package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SingApproveTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private  String error_return="/kq/register/browse_registerdata.do?b_search=link";	
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 String flag=(String)hm.get("flag");
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");	
		 if(selectedinfolist==null||selectedinfolist.size()==0)
			{
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));	
			  String error_message=ResourceFactory.getProperty("kq.register.noselect.manager");	
	 		  this.getFormHM().put("error_message",error_message);
	 	      this.getFormHM().put("error_return",this.error_return);  
	 	      this.getFormHM().put("error_flag","2");
	 	      this.getFormHM().put("error_stuts","1");
	 	      return;
			}
		 String kq_duration=(String)this.getFormHM().get("kq_duration");
		 if(selectedinfolist==null||selectedinfolist.size()==0)
	            return;
		 String q03z5="";
		 String q03z5_1="";		 
		if(flag==null||flag.length()<=0)
		{
				
				return;
	    }else if("2".equals(flag))
		{
				q03z5="02";
				q03z5_1="08";
				
		}else if("3".equals(flag))
		{
				q03z5="03";
				q03z5_1="02";
				
		}else
		{
			return;
		}
		  ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);  	
	   	    /*********************/
	   	    ArrayList fieldlist=RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);
		    FieldItem fielditem=new FieldItem();
		    fielditem.setFieldsetid("Q05");
		    fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
		    fielditem.setItemid("scope");
		    fielditem.setItemtype("A");
		    fielditem.setCodesetid("0");
		    fielditem.setVisible(true);
		    fieldlist.add(fielditem);		    
		    SingOpintion singOpintion=new SingOpintion();
		    StringBuffer column=new StringBuffer();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem field=(FieldItem)fieldlist.get(i);			
				column.append(field.getItemid()+",");
			}
			int l=column.toString().length()-1;
			String columnstr=column.toString().substring(0,l);
			/********************/
		String kq_year="";
		String duration="";
		ArrayList kqDate_list=new ArrayList();
		if(kq_duration!=null&&kq_duration.length()>0)
		 {
			 String[] kq_d=kq_duration.split("-");
			 kq_year=kq_d[0];
			 duration=kq_d[1];
			 kqDate_list=RegisterDate.getKqDayList(this.getFrameconn(),kq_year,duration);
		 }else
		 {
			 kqDate_list=RegisterDate.getKqDayList(this.getFrameconn());
		 }			
		 String start_date=kqDate_list.get(0).toString();
		 String end_date=kqDate_list.get(1).toString();
		 StringBuffer update_Q05=new StringBuffer();
		 update_Q05.append("update Q05 set Q03Z5=? ");		
		 update_Q05.append("where nbase=? and a0100=? and q03z0=?");
		 update_Q05.append(" and q03z5 in(?)");
		 StringBuffer update_Q03=new StringBuffer();
		 update_Q03.append("update Q03 set Q03Z5=? ");
		 update_Q03.append("where nbase=? and a0100=? ");
		 update_Q03.append("and q03z0>=? and q03z0<=? ");
		 update_Q03.append(" and q03z5 in(?)");
		 ArrayList Q05_list= new ArrayList();
		 ArrayList Q03_list= new ArrayList();		
		 StringBuffer selectSQL=new StringBuffer();
		 try
		 {
			 for(int i=0;i<selectedinfolist.size();i++)
	         {
				ArrayList Q05_one_value= new ArrayList();
				ArrayList Q03_one_value= new ArrayList();
	         	LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);   
	         	String userbase=rec.get("nbase").toString();
	         	String a0100=rec.get("a0100").toString();
	         	String work_date=rec.get("q03z0").toString(); 	         	
	         	/***员工考勤月表***/
	         	Q05_one_value.add(q03z5);
	         	Q05_one_value.add(userbase);
	         	Q05_one_value.add(a0100);
	         	Q05_one_value.add(work_date);
	         	Q05_one_value.add(q03z5_1);	         	
	         	/***员工日考勤表***/
	         	Q03_one_value.add(q03z5);
	         	Q03_one_value.add(userbase);
	         	Q03_one_value.add(a0100);
	         	Q03_one_value.add(start_date);
	         	Q03_one_value.add(end_date);
	         	Q03_one_value.add(q03z5_1);	         	
	         	Q05_list.add(Q05_one_value);
	         	Q03_list.add(Q03_one_value);
	         	String strsql=singOpintion.getSqlstr(columnstr,userbase,kq_duration,a0100);
	           	selectSQL.append(strsql);
	        	selectSQL.append(" ");
	        	selectSQL.append(" UNION ");
	         }
			 selectSQL.setLength(selectSQL.length()-7); 
			
		 	 this.getFormHM().put("s_strsql",selectSQL.toString());
			 this.getFormHM().put("s_columns",columnstr);
			 this.getFormHM().put("fieldlist",fieldlist);	
			 this.getFormHM().put("singlist",selectedinfolist);
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 
				 dao.batchUpdate(update_Q05.toString(),Q05_list);
				 dao.batchUpdate(update_Q03.toString(),Q03_list); 
			this.getFormHM().put("flag",flag);
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }	
		 
		 /*****************************/
		
		 this.getFormHM().put("error_flag","0");
	      this.getFormHM().put("error_stuts","0"); 
	}
	

}
