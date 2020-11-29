package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

public class OverruleRegisterTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		 		  
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");	
		 if(selectedinfolist==null)
			 selectedinfolist=(ArrayList)this.getFormHM().get("overrulelist");	
		 if(selectedinfolist==null)
			 selectedinfolist=new ArrayList();
		 String kq_duration=(String)this.getFormHM().get("kq_duration");
		 String overrule=(String)this.getFormHM().get("overrule");
		 /*if(overrule==null||overrule.length()<=0)
			 overrule="";		
		 else
			 overrule=this.userView.getUserFullName()+":"+overrule;*/
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
		 String overrule_value=Sql_switcher.numberToChar(Sql_switcher.isnull("overrule","''"));
		 String result=RegisterInitInfoData.getResult();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 boolean isCorrect=true;
		 try
		 {
			 for(int i=0;i<selectedinfolist.size();i++)
	         {
				ArrayList Q05_one_value= new ArrayList();
				ArrayList Q03_one_value= new ArrayList();
	         	LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);   
	         	String userbase=rec.get("nbase").toString();
	         	String a0100=rec.get("a0100").toString();
	         	String q03z5 = rec.get("q03z5").toString();
	         	//***员工考勤月表***//*
	         	/*Q05_one_value.add("07");//驳回
	         	Q05_one_value.add(userbase);
	         	Q05_one_value.add(a0100);
	         	Q05_one_value.add(work_date);
	         	*//***员工日考勤表***//*
	         	Q03_one_value.add("07");//驳回
	         	Q03_one_value.add(userbase);
	         	Q03_one_value.add(a0100);
	         	Q03_one_value.add(start_date);
	         	Q03_one_value.add(end_date);
	         	Q05_list.add(Q05_one_value);
	         	Q03_list.add(Q03_one_value);*/
	         	//32614  linbz完善驳回失败提示信息  并抛出
	         	/**起草状态不能驳回**/
                if("01".equals(q03z5))
                    throw GeneralExceptionHandler.Handle(new GeneralException("","操作失败：<br>1、只能驳回非起草的数据！<br>2、注意检查该记录日明细与月汇总的审批状态是否保持一致！<br>如不一致可能由于月汇总报批后重新生成日明细导致的，请试着重新进行月汇总。","",""));
                 
                 StringBuffer update_Q05=new StringBuffer();
                 update_Q05.append("update Q05 set state='1'");
                 //update_Q05.append(" ,overrule="+overrule_value+""+result+"'."+overrule+"' ");
                 update_Q05.append("where nbase='"+userbase+"' and a0100='"+a0100+"' and q03z0='"+kq_duration+"'");
                 StringBuffer update_Q03=new StringBuffer();
                 update_Q03.append("update Q03 set state='1' ");
                 update_Q03.append("where nbase='"+userbase+"' and a0100='"+a0100+"' ");
                 update_Q03.append("and q03z0>='"+start_date+"' and q03z0<='"+end_date+"' ");   
                 dao.update(update_Q05.toString());
                 dao.update(update_Q03.toString());     
                 this.getFormHM().put("overrule_status","2");
	         }	
			 ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
			 String code = (String) this.getFormHM().get("code");
			 String kind = (String) this.getFormHM().get("kind"); 
			 String code_kind="";
			 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			 kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);
			 // 考勤部门
			 String field = KqParam.getInstance().getKqDepartment();
			 //考勤管理范围机构编码
	         String kqDeptCode = RegisterInitInfoData.getKqPrivCodeValue(userView);
			 for(int i=0;i<kq_dbase_list.size();i++)
		   	 {
				 String userbase=kq_dbase_list.get(i).toString();
				 String where_In=RegisterInitInfoData.getWhereINSql(userView,userbase);
				 if (where_In.indexOf("WHERE") != -1) {
					 where_In = where_In.replace("WHERE", "WHERE (");
                     if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
                    	 where_In += " OR " + userbase + "A01." + field + " like '"
                                 + kqDeptCode + "%'";
                     where_In += ")";
                 } 
//				 else {
////                	 where_In = where_In.replace("WHERE", "WHERE (");
//                     if (field != null && field.length() > 0 && !"".equals(kqDeptCode))
//                    	 where_In += userbase + "A01." + field + " like '" + kqDeptCode
//                                 + "%'";
////                     where_In += ")";
//                 }
				 if(where_In.indexOf("WHERE")==-1&&where_In.indexOf("where")==-1)
					 where_In=where_In+" where 1=1 ";
				 String up1="update q03 set Q03Z5='07' where nbase='"+userbase+"' and  state='1' and q03z0>='"+start_date+"' and q03z0<='"+end_date+"' and  EXISTS(select a0100 "+where_In+" and q03.a0100="+userbase+"A01.a0100)";
				 //String up2="update q05 set Q03Z5='07',overrule="+overrule_value+""+result+"'."+overrule+"' where nbase='"+userbase+"' and state='1' and q03z0='"+kq_duration+"' and  a0100 in(select a0100 "+where_In+")";
				 updateSumSql(where_In,userbase,kq_duration,overrule);
				 dao.update(up1);
				 //dao.update(up2);
				 up1="update q03 set state='0' where nbase='"+userbase+"' and state='1' and q03z0>='"+start_date+"' and q03z0<='"+end_date+"' and  EXISTS(select a0100 "+where_In+" and q03.a0100="+userbase+"A01.a0100)";
				 String up2="update q05 set state='0' where nbase='"+userbase+"' and state='1' and q03z0='"+kq_duration+"' and EXISTS(select a0100 "+where_In+" and q05.a0100="+userbase+"A01.a0100)";
				 dao.update(up1);
				 dao.update(up2);
		   	 }
			
		 }catch(GeneralException e) {
			 throw e;
		 }
		 catch(Exception e)
		 {
			 isCorrect=false;
			 e.printStackTrace();
		 }	
		
		 if(isCorrect)
	    {
	    		this.getFormHM().put("sp_result","数据驳回成功！");
	    }else
	    {
	    		this.getFormHM().put("sp_result","数据驳回失败！");
	    }
	}
	public boolean  updateSumSql(String whereIN,String dbase,String kq_duration,String overrule)throws GeneralException
    {
    	boolean isCorrect = false;
        RegisterInitInfoData registerInitInfoData=new RegisterInitInfoData();
        overrule=registerInitInfoData.getOverruleFormat(overrule,"07",this.userView.getUserFullName());
    	String up2="select a0100,overrule from q05 where nbase='"+dbase+"' and state='1' and q03z0='"+kq_duration+"' and  a0100 in(select a0100 "+whereIN+")";
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	try {
    		ArrayList list= new ArrayList();
    		StringBuffer updatesql=new StringBuffer();
        	updatesql.append("update Q05 set ");
        	updatesql.append(" Q03Z5=?,overrule=? where ");
        	updatesql.append(" nbase=? ");
        	updatesql.append(" and Q03Z0 =? ");    
        	updatesql.append(" and a0100=? ");
			this.frowset=dao.search(up2);
			while(this.frowset.next())
			{
				String oldover=Sql_switcher.readMemo(this.frowset, "overrule");
				ArrayList u_list=new ArrayList();
		  	    u_list.add("07");
		  	    u_list.add(overrule + oldover);
		  	    u_list.add(dbase);
		  	    u_list.add(kq_duration);  	    
		  	    u_list.add(this.frowset.getString("a0100"));
		  	    list.add(u_list);
			}
			
			dao.batchUpdate(updatesql.toString(),list);
			isCorrect = true;
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			 e1.printStackTrace();
	 	     return false;  	
		}
	    return isCorrect;
    }
}
