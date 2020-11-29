package com.hjsj.hrms.transaction.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.Pigeonhole;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpinVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SingSavePigeonholeTrans  extends IBusiness {
	private  String error_return="/kq/register/daily_registerdata.do?b_query=link";	
	public void execute() throws GeneralException 
	{
		ArrayList filedlist=(ArrayList)this.getFormHM().get("list");		
		String setlist=(String)this.getFormHM().get("setlist");
		String temp_table_oper=(String)this.getFormHM().get("temp_table");
		String tempCloumn=getTempDate(filedlist,temp_table_oper);
		if(tempCloumn==null||tempCloumn.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.pigeonhole.error.nofield"),"",""));
		}		
	    /***********操作考勤归档临时数据表**********/		
		Pigeonhole pigeonhole =new Pigeonhole(this.getFrameconn(),this.userView);
		String data_table_name=pigeonhole.getTmpTableName(this.userView.getUserId(),"arch");
		String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());	
		/*************得到归档的次数和对应的月**************/
		String month_pigeonhole=RegisterDate.getKqMonth(this.getFrameconn(),kq_duration);
		String num_pigeonhole=RegisterDate.getKqNum(this.getFrameconn(),kq_duration);		
		ArrayList po_userlist=(ArrayList)this.getFormHM().get("po_userlist");
		String pigeonhole_flag="s_true";
		try
		{
			for(int i=0;i<po_userlist.size();i++)
			{
				SingOpinVo rec=(SingOpinVo)po_userlist.get(i);
				String nbase=rec.getNbase();
		     	String a0100=rec.getA0100();
				pigeonhole.insertInitTempData(data_table_name,kq_duration,nbase,tempCloumn,a0100);
				pigeonhole.insertInitDestData(data_table_name,setlist,kq_duration,nbase,month_pigeonhole,this.userView.getUserFullName(),num_pigeonhole);
				pigeonhole.updateI9999(nbase,setlist,month_pigeonhole,num_pigeonhole,true);
				pigeonhole.updateDestData(temp_table_oper,data_table_name,nbase,setlist,month_pigeonhole,this.userView.getUserFullName(),num_pigeonhole);
						 
		    }
		}catch(Exception e)
		{
			pigeonhole_flag="s_false";
			String error_message="";
			if (e.toString().indexOf("`") == -1) {
				error_message = ResourceFactory.getProperty("kq.pigeonhole.save.lost");	
			}else {
				error_message = e.toString().substring(e.toString().indexOf("`")+1);
			}
	 		this.getFormHM().put("error_message",error_message);
	 	    this.getFormHM().put("error_return",this.error_return);  
	 	    this.getFormHM().put("error_flag","2");
	 	    this.getFormHM().put("error_stuts","1");
	 	    return;
		}finally
		{
			KqUtilsClass.dropTable(this.frameconn, data_table_name);
			KqUtilsClass.dropTable(this.frameconn, temp_table_oper);
		}		
		this.getFormHM().put("pigeonhole_flag",pigeonhole_flag);
		this.getFormHM().put("pigeonhole_flag2","true");
		this.getFormHM().put("error_flag","0");
	}
	/**
	 * 修改归档临时表
	 * @param filedlist
	 * @param temp_table
	 */
    public String getTempDate(ArrayList filedlist,String temp_table)
    {
    	 DynaBean dbean=null;
    	 String srcFldId=null;
    	 String destFldId=null;
    	 String destFldName=null;
    	 ArrayList list=new ArrayList();
    	 StringBuffer update=new StringBuffer();
    	 update.append("update "+temp_table+" set");
    	 update.append(" DestFldId=?,DestFldName=?");
    	 update.append(" where SrcFldId=?");
    	 StringBuffer tempCloumn=new StringBuffer();
    	 try
         {
 	        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
 	        tempCloumn.append("a0100,i9999");
 			for(int i=0;i<filedlist.size();i++)
 		    {
 				ArrayList one_list=new ArrayList();
 				dbean=(LazyDynaBean)filedlist.get(i);
 				srcFldId=(String)dbean.get("srcfldid");	
 				destFldId=(String)dbean.get("destfldid");	
 				destFldName=(String)dbean.get("destfldname");	
 				if(destFldId!=null&&destFldId.length()>0&&destFldName!=null&&destFldName.length()>0)
 				{
 					one_list.add(destFldId);
 					one_list.add(destFldName);
 					one_list.add(srcFldId);
 					list.add(one_list);
 					tempCloumn.append(","+srcFldId);
 				}
 		    }
 			dao.batchUpdate(update.toString(),list);
         }catch(Exception e)
         {
        	 e.printStackTrace();
         }
         return tempCloumn.toString();
    }

}
