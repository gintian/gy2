package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:</p>
 * <p>Description:部门休假信息状态处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-21:14:12:36</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class TransactAnnualPlanTrans  extends IBusiness {
    private String sp_result="";
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 if(selectedinfolist==null||selectedinfolist.size()==0)
		 {
			 selectedinfolist=(ArrayList)this.getFormHM().get("appeoveinfolist");
			 if(selectedinfolist==null||selectedinfolist.size()==0)
			 {
				 return;
			 }
		 }	            
		
        String  status=(String)hm.get("status");       
        if(status==null||status.length()<=0)
        {
        	return;
        }
        if("04".equals(status.trim()))
        {
        	/******发布****/
        	boolean isCorrect =status04(status,selectedinfolist);
        	if(isCorrect)
        		this.getFormHM().put("sp_result","计划发布成功！");
        	else
        		if(this.sp_result!=null&&this.sp_result.length()>0)
        		{
        			this.getFormHM().put("sp_result",this.sp_result);
        		}else
        		{
        			this.getFormHM().put("sp_result","计划发布失败！");
        		}        		
        }else if("02".equals(status.trim()))
        {
        	/******报批****/
        	boolean isCorrect =status02(status,selectedinfolist);
        	if(isCorrect)
        		this.getFormHM().put("sp_result","计划审核成功！");
        	else
        		if(this.sp_result!=null&&this.sp_result.length()>0)
        		{
        			this.getFormHM().put("sp_result",this.sp_result);
        		}else
        		{
        			this.getFormHM().put("sp_result","计划审核失败！");
        		}
        }else if("08".equals(status.trim()))
        {
        	/******报批****/
        	boolean isCorrect =status08(status,selectedinfolist);
        	if(isCorrect)
        		this.getFormHM().put("sp_result","计划审核成功！");
        	else
        		if(this.sp_result!=null&&this.sp_result.length()>0)
        		{
        			this.getFormHM().put("sp_result",this.sp_result);
        		}else
        		{
        			this.getFormHM().put("sp_result","计划审核失败！");
        		}
        }else if("03".equals(status.trim()))
        {
        	/******比准****/
        	ArrayList approvelist=(ArrayList)this.getFormHM().get("approvelist");        	
        	String approve_date="";
        	String approve_result="";
        	String q29z7="";
        	for(int i=0;i<approvelist.size();i++)
        	{
        		FieldItem field=(FieldItem)approvelist.get(i); 
        		if("q2913".equals(field.getItemid().trim()))
        		{
        			approve_date=field.getValue();
        		}else if("q29z0".equals(field.getItemid().trim()))
        		{
        			approve_result=field.getValue();
        		}else if("q29z7".equals(field.getItemid().trim()))
        		{
        			q29z7=field.getValue();
        		}
        		
        	}
        	boolean isCorrect =status03(status,selectedinfolist,approve_date,approve_result,q29z7);
        	if(isCorrect)
        		this.getFormHM().put("sp_result","计划批准成功！");
        	else
        		if(this.sp_result!=null&&this.sp_result.length()>0)
        		{
        			this.getFormHM().put("sp_result",this.sp_result);
        		}else
        		{
        			this.getFormHM().put("sp_result","计划发布失败！");
        		}
        }else if("05".equals(status.trim()))
        {
        	String approve_date = SafeCode.decode((String)hm.get("date"));
        	String approve_result = SafeCode.decode((String)hm.get("result"));
        	String q29z7 = SafeCode.decode((String)hm.get("memo"));
  
        	boolean isCorrect = status05("03",selectedinfolist,approve_date,approve_result,q29z7);
			if (isCorrect)
				this.getFormHM().put("sp_result", "计划批准成功！");
			else if (this.sp_result != null && this.sp_result.length() > 0) {
				this.getFormHM().put("sp_result", this.sp_result);
			} else {
				this.getFormHM().put("sp_result", "计划发布失败！");
			}
        }else if("00".equals(status.trim()))
        {
        	this.getFormHM().put("appeoveinfolist",selectedinfolist);
        }
        
	}
	/**
	 * 发布选择信息
	 * @param status
	 * @param selectedinfolist
	 */
   private boolean  status04(String status,ArrayList selectedinfolist)throws GeneralException
   {
	   ArrayList list=new ArrayList();
	   boolean isCorrect =true;
		for(int i=0;i<selectedinfolist.size();i++)
       {
			 ArrayList one_value= new ArrayList();
	         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
	         String flag=rec.get("q29z5").toString();
	         if("02".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result=ResourceFactory.getProperty("kq.plan.issue.error.replyed");
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.issue.error.replyed"),"",""));
	         }else if("03".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result=ResourceFactory.getProperty("kq.plan.issue.error.allow");
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.issue.error.allow"),"",""));
	         }else if("08".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result="选项中有报审数据，不予发布，请检查";
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
	         }
	         one_value.add(status);
	         one_value.add(rec.get("q2901").toString());
	         one_value.add("01");
	         list.add(one_value);
       }
	  String sql="update q29 set q29z5=? where q2901=? and q29z5=?";
	  ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.batchUpdate(sql,list);
			 
		 }catch(Exception e)
		 {
			 isCorrect =false;
			 e.printStackTrace();
		 }	
		 return isCorrect;
   }
   /**
    * 报批数据
    * @param status
    * @param selectedinfolist
    */
   private boolean status02(String status,ArrayList selectedinfolist)throws GeneralException
   {
	   ArrayList list=new ArrayList();
	   ArrayList list_emp=new ArrayList();
	   boolean isCorrect =true;
		for(int i=0;i<selectedinfolist.size();i++)
      {
			 ArrayList one_value= new ArrayList();
	         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 	        
	         /***部门计划***/
	         String flag=rec.get("q29z5").toString();
	         if("03".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result=ResourceFactory.getProperty("kq.plan.replyed.error.allow");
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.replyed.error.allow"),"",""));
	         }else if("01".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result=ResourceFactory.getProperty("kq.plan.replyed.error.draftout");
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.replyed.error.draftout"),"",""));
	         }/*else if(flag.equals("04"))
	         {
	        	 isCorrect =false;
	        	 this.sp_result="选项中有发布数据，不予审核，请检查";
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
	         }*/
	         String q2901=rec.get("q2901").toString();
	         one_value.add(status);
	         one_value.add(q2901);
	         one_value.add("04");
	         /****个人计划*****/
	         ArrayList one_emp_value= new ArrayList();
	         one_emp_value.add(status);
	         one_emp_value.add(rec.get("q2901").toString());
	         //szk 验证是否有为报批人员在CheckperAnnualPlanTrans中
//	         if(checkoutEmp(q2901,rec.get("q2903").toString()))
//	         {
//	        	isCorrect =false;
//	        	this.sp_result=rec.get("q2905").toString()+",有员工没有填写计划，该计划不能报批！";
//	        	throw GeneralExceptionHandler.Handle(new GeneralException("",rec.get("q2905").toString()+"，有员工没有填写计划，该计划不能报批！","",""));
//	         }
	         list.add(one_value);
	         list_emp.add(one_emp_value);	         
      }
	  String sql="update q29 set q29z5=? where q2901=? and q29z5=?";
//	  String emp_sql="update q31 set q31z5=? where q2901=?";   //审核只需要更改外面的状态
	  ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.batchUpdate(sql,list);
//			 dao.batchUpdate(emp_sql,list_emp);  //审核只需要更改外面的状态
			 
		 }catch(Exception e)
		 {
			 isCorrect =false;
			 e.printStackTrace();
		 }	
		 return isCorrect;
   }
   /**
    * 批准休假
    * @param status
    * @param selectedinfolist
    * @param approve_date
    * @param approve_result
    * @param q29z7 审批意见
    */
   private boolean  status03(String status,ArrayList selectedinfolist,String approve_date,String approve_result,String q29z7)throws GeneralException
   {
	   boolean isCorrect =true;
	   try
		 {
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		    ArrayList list=new ArrayList();
		    ArrayList list_emp=new ArrayList();	  
		    String q29z7_value=Sql_switcher.numberToChar(Sql_switcher.isnull("q29z7","''"));
		    String q31z7_value=Sql_switcher.numberToChar(Sql_switcher.isnull("q31z7","''"));

			for(int i=0;i<selectedinfolist.size();i++)
	        {
				 ArrayList one_value= new ArrayList();
		         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 	        
		         /***部门计划***/
		         String flag=rec.get("q29z5").toString();
		         if("04".equals(flag))
		         {
		        	 isCorrect =false;
		        	 this.sp_result=ResourceFactory.getProperty("kq.plan.allow.error.issue");
		        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.allow.error.issue"),"",""));
		         }else if("01".equals(flag))
		         {
		        	 isCorrect =false;
		        	 this.sp_result=ResourceFactory.getProperty("kq.plan.allow.error.draftout");
		        	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.plan.allow.error.draftout"),"",""));
		         }
		         else if("08".equals(flag))
		         {
		        	 isCorrect =false;
		        	 this.sp_result="选项中有报审数据，不予批准，请检查";
		        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
		         }
		         String char_to_date=Sql_switcher.dateValue(approve_date);
		         /*one_value.add(status);
		         
		         one_value.add(char_to_date);
		         one_value.add(approve_result);
		         one_value.add(rec.get("q2901").toString());
		         one_value.add("02");*/
		         StringBuffer sql=new StringBuffer();
		         sql.append("update q29 set q29z5='"+status+"',q2913="+char_to_date+",");
		         sql.append(" q29Z0='"+approve_result+"',q29z7='"+q29z7+"' ");
		         sql.append(" where q2901='"+rec.get("q2901").toString()+"' and q29z5='02'");
		         /****个人计划*****/
		         /*ArrayList one_emp_value= new ArrayList();
		         one_emp_value.add(status);
		         one_emp_value.add(approve_result);
		         one_emp_value.add(rec.get("q2901").toString());	 
		         one_emp_value.add("02");
		         one_emp_value.add("03");
		         list.add(one_value);		         
		         list_emp.add(one_emp_value);*/
		         StringBuffer emp_sql=new StringBuffer();
		         emp_sql.append("update q31 set q31z5='"+status+"',q31z0='"+approve_result+"',");   
		         emp_sql.append(" q31z7="+q31z7_value+""+ Sql_switcher.concat() +"'"+q29z7+"'");
		         emp_sql.append(" where q2901='"+rec.get("q2901").toString()+"' and q31z5='02'  and q31z0='03'");
		         dao.update(sql.toString());
		         dao.update(emp_sql.toString());
	        }
		 }  catch(Exception e)
		 {
			 isCorrect =false;
			 e.printStackTrace();
		 }	
		 return isCorrect;
   }
   /**
    * 详细个人批准休假
    * @param status
    * @param selectedinfolist
    * @param approve_date
    * @param approve_result
    * @param q29z7 审批意见
    */
   private boolean status05(String status,ArrayList selectedinfolist,String approve_date,String approve_result,String q29z7)throws GeneralException
   {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		boolean isCorrect = true;

		String q31z7_value = Sql_switcher.numberToChar(Sql_switcher.isnull("q31z7", "''"));
		StringBuffer emp_sql = new StringBuffer();
		try {
			for (int i = 0; i < selectedinfolist.size(); i++) {
				emp_sql = new StringBuffer();
				LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
				/**** 个人计划 *****/
				
				String flag = rec.get("q31z5").toString();
				if ("04".equals(flag)) {
					isCorrect = false;
					this.sp_result = ResourceFactory.getProperty("kq.plan.allow.error.issue");
				} else if ("08".equals(flag)) {
					isCorrect = false;
					this.sp_result = "选项中有报审数据，不予批准，请检查";
				} else if ("01".equals(flag)) {
					isCorrect = false;
					this.sp_result = "选项中有起草数据，不予批准，请检查";
				}
				
				if (!isCorrect) {
					throw new GeneralException(this.sp_result);
				}

				emp_sql.append("update q31");
				emp_sql.append(" set q31z5=?,");
				emp_sql.append("q31z0=?,");
				emp_sql.append("q31z7=" + q31z7_value + Sql_switcher.concat() + "?");
				emp_sql.append(" where q2901=?");
				emp_sql.append(" and q3101=?");
				emp_sql.append(" and q31z5='02'");

				ArrayList sqlParams = new ArrayList();
				sqlParams.add(status);
				sqlParams.add(approve_result);
				sqlParams.add(q29z7);
				sqlParams.add(rec.get("q2901").toString());
				sqlParams.add(rec.get("q3101").toString());

				dao.update(emp_sql.toString(), sqlParams);
			}
		} catch (Exception e) {
			isCorrect = false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return isCorrect;
   }
   
   private boolean checkoutEmp(String q2901,String q2903)
   {
	   boolean isCorrect=false;
	   ArrayList kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn());
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
	   try
	   {
		   for(int i=0;i<kq_dbase_list.size();i++)
		   {
			   String nbase=kq_dbase_list.get(i).toString();
			   String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
			   StringBuffer sql=new StringBuffer();
			   sql.append("select a0101 from "+nbase+"A01 A");
			   sql.append(" where not EXISTS(");
			   sql.append("select a0101 from q31 where q2901='"+q2901+"' and nbase='"+nbase+"'");
			   sql.append(" and q31.a0100=A.a0100");
			   sql.append(" and q31.a0100 in(select a0100 "+whereIN+") )"); 
			   sql.append(" and A.a0100 in(select a0100 "+whereIN+") "); 	
			   sql.append(" and EXISTS(");
			   sql.append("select 1 from q17 where A.a0100=q17.a0100 and  nbase='"+nbase+"' and q1701='"+q2903+"'");
			   sql.append(" and q17.a0100 in(select a0100 "+whereIN+") "); 
			   sql.append(" and q17.q1703>0 "); 
			   sql.append(")");
			   this.frowset=dao.search(sql.toString());			  
			   if(this.frowset.next())
			   {
				   isCorrect=true;
			   }
		   }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return isCorrect;
   }
   /**
    * 报批数据
    * @param status
    * @param selectedinfolist
    */
   private boolean status08(String status,ArrayList selectedinfolist)throws GeneralException
   {
	   ArrayList list=new ArrayList();
	   ArrayList list_emp=new ArrayList();
	   boolean isCorrect =true;
		for(int i=0;i<selectedinfolist.size();i++)
      {
			 ArrayList one_value= new ArrayList();
	         LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 	        
	         /***部门计划***/
	         String flag=rec.get("q29z5").toString();
	         if("03".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result="选项中有批准数据，不予报审，请检查";
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
	         }else if("01".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result="选项中有起草数据，不予报审，请检查";
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
	         }
	         else if("02".equals(flag))
	         {
	        	 isCorrect =false;
	        	 this.sp_result="选项中有报批数据，不予报审，请检查";
	        	 throw GeneralExceptionHandler.Handle(new GeneralException("",this.sp_result,"",""));
	         }
	         String q2901=rec.get("q2901").toString();
	         one_value.add(status);
	         one_value.add(rec.get("q2901").toString());
	         one_value.add("04");
	         /****个人计划*****/
	         ArrayList one_emp_value= new ArrayList();
	         one_emp_value.add(status);
	         one_emp_value.add(rec.get("q2901").toString());	  
	         /*if(!checkoutEmp(q2901))
	         {
	        	isCorrect =false;
	        	this.sp_result=rec.get("q2905").toString()+",有员工没有填写计划，该计划不能报审！";
	        	throw GeneralExceptionHandler.Handle(new GeneralException("",rec.get("q2905").toString()+",有员工没有填写计划，该计划不能报审！","",""));
	         }*/
	         list.add(one_value);
	         list_emp.add(one_emp_value);	         
      }
	  String sql="update q29 set q29z5=? where q2901=? and q29z5=?";
	  String emp_sql="update q31 set q31z5=? where q2901=?";
	  ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 dao.batchUpdate(sql,list);
			 dao.batchUpdate(emp_sql,list_emp);
			 
		 }catch(Exception e)
		 {
			 isCorrect =false;
			 e.printStackTrace();
		 }	
		 return isCorrect;
   }
}
