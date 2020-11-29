package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 月末处理封存期间---审批标志 仅五种“起草”、“已报批”、“已批”、“驳回”、“报审”。
 * <p>Title:SealDurationTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 28, 2007 3:42:31 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SealDurationTrans extends IBusiness{
	public void execute() throws GeneralException {
		 
		 String kqduration = RegisterDate.getKqDuration(this.frameconn);
		 ArrayList date_list=RegisterDate.getKqDayList(this.frameconn);
		 String start_date=date_list.get(0).toString();
		 String end_date=date_list.get(date_list.size()-1).toString();	
		 ArrayList kq_dbase_list=userView.getPrivDbList(); 		 
		 String ky_year=kqduration.substring(0,4);
		 String kq_duration=kqduration.substring(5,7);
		 sealDuration(kqduration,ky_year,kq_duration,kq_dbase_list,start_date,end_date);
		 kqduration=RegisterDate.getKqDuration(this.frameconn);	  
	     this.getFormHM().put("kq_duration",kqduration); 
	     this.getFormHM().put("pigeonhole_flag","xxx");
	}
	/**
	 * 
	 * 
	 * */
	public void sealDuration(String kqduration,String ky_year,String kq_duration,ArrayList kq_dbase_list,String start_date,String end_date)
	{
		 StringBuffer sb=new StringBuffer();
		 StringBuffer sbs=new StringBuffer();
		 StringBuffer ssql=new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());		 
		 if(kqduration!=null&&kqduration.length()>0){
		    sb.append("select min(kq_year) as yy from kq_duration where finished =0 ");
		    try{
	          this.frowset = dao.search(sb.toString());  
	          this.frowset.first();
	          String yy=this.frowset.getString("yy");
	          if(yy.equals(ky_year.toString()))
	          {
	    	    sbs.append("select min(kq_duration) as mm from kq_duration where finished =0 and kq_year='");
	    	    sbs.append(ky_year.toString());
	    	    sbs.append("'");
  	            this.frowset = dao.search(sbs.toString());  
  	            this.frowset.first();
  	            String mm=this.frowset.getString("mm");
  	            if(mm.equals(kq_duration.toString()))
  	            {
  	    	       
  	               	    /***修改员工日考勤***/  
  	     		   /**********状态改为发布状态************/
  	       	       /*String kq_emp_sql=updateSql("Q03");
  	       	       ArrayList updateO03list=new ArrayList();
  	       	       updateO03list.add("04");   
  	               updateO03list.add(start_date);
  	               updateO03list.add(end_date);
  	                      	       
	       	       dao.update(kq_emp_sql,updateO03list);
	       	         *//***修改员工日考勤***//* 
	       	       String kq_emp_sum_sql=updateQ05Sql("Q05");   
  	               ArrayList updateQ05list= new ArrayList();
  	               updateQ05list.add("04");   
  	               updateQ05list.add(kqduration);          
  	       	       dao.update(kq_emp_sum_sql,updateQ05list);*/
   	 	           //修改部门月统计
   	 	       
   	 	           /**得到部门的编号**/
  	       	      //update_Org(start_date,end_date,"UM");
  				  
  			       /**得到单位的编号**/
  			       //update_OrgSum(kqduration,"UN");
  				
  	               ssql.append("update kq_duration set finished=");
  	               ssql.append(1);
  	               ssql.append(" where kq_duration='");
  	               ssql.append(kq_duration.toString());
  	               ssql.append("' and kq_year = '");
  	               ssql.append(ky_year.toString());
  	               ssql.append("'");
  	               dao.update(ssql.toString());
  	               
  	            }else{
  	    	       throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty(GeneralConstant.KQERROA),"",""));
  	           }

	         }else{
	    	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty(GeneralConstant.KQERROA),"",""));
  	    	 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	  }
	}
	
	/**
     * 修改审核标志
     * @param start_date, end_date 考勤期间
     * @param org_id 部门代码
     * @param codesetid UM:部门，UN:单位
     *
     * */
	public void  update_Org(String start_date,String end_date,String codesetid)throws GeneralException
	{
    	
	    StringBuffer updateSql=new StringBuffer();
    	updateSql.append("update Q07 set ");
    	updateSql.append(" Q03Z5='04' ");
    	updateSql.append(" where");	
    	updateSql.append(" setid ='"+codesetid+"'");
    	updateSql.append(" and Q03Z0 >='"+start_date+"'");
    	updateSql.append(" and Q03Z0 <='"+end_date+"'");    	
    	ContentDAO dao = new ContentDAO(this.getFrameconn()); 	    
 	    try{ 	    	
 	       dao.update(updateSql.toString()); 	      
 	    }catch(Exception e){
 	    	e.printStackTrace();
 	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
 	    }
    }
	 /**
	    * 修改审核标志
	    * @param coursedate 考勤期间
	    * @param org_value 部门代码
	    * @param codesetid UM:部门，UN:单位
	    * 
	    * */
	public void  update_OrgSum(String coursedate,String codesetid)throws GeneralException
	{
		
		 
	   	 StringBuffer updateSql=new StringBuffer();
	     updateSql.append("update Q09 set ");
	     updateSql.append(" Q03Z5='04' ");
	     updateSql.append(" where");	
	     updateSql.append(" setid = '"+codesetid+"'");  
	     updateSql.append(" and Q03Z0 = '"+coursedate+"'");	
	      	
	   	 ContentDAO dao = new ContentDAO(this.getFrameconn()); 	    
		    try{ 	    	
		       dao.update(updateSql.toString());		      
		    }catch(Exception e){
		    	e.printStackTrace();
		    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.refer.lost"),"",""));
		    }
	   }
	
	 /****************
     * @param whereIN select in子句 
     * @param tablename 表名
     * @return 返回？号的update的SQL语句
     * 
     * ***/
    public String updateSql(String tablename){
    	
    	StringBuffer updatesql=new StringBuffer();
    	updatesql.append("update "+tablename+" set ");
    	updatesql.append(" Q03Z5=? where 1=1");    	
    	updatesql.append(" and Q03Z0 >=? ");
    	updatesql.append(" and Q03Z0 <=? ");    	
    	return updatesql.toString();
    	
    }
    /****************
     * @param whereIN select in子句 
     * @param tablename 表名
     * @return 返回？号的update的SQL语句
     * 
     * ***/
    public String updateQ05Sql(String tablename){    	
    	StringBuffer updatesql=new StringBuffer();
    	updatesql.append("update "+tablename+" set ");
    	updatesql.append(" Q03Z5=? where 1=1");    	
    	updatesql.append(" and Q03Z0=?");     	
    	return updatesql.toString();
    	
    }
}
