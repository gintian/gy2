package com.hjsj.hrms.transaction.kq.kqself.apply;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title:</p>
 * <p>Description:添加员工个人休假申请</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 15, 2006:9:40:37 AM</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class SaveAnnualApplyTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		ArrayList flist=(ArrayList)this.getFormHM().get("flist");
        String plan_id=(String)this.getFormHM().get("plan_id");
        boolean isCorrect=getIfSave(userView.getDbname(),userView.getA0100(), plan_id,flist);
	    if(isCorrect)
	    {
	    	this.AddValue("q31",flist,plan_id);
	    }
	}
	/**
	 * 添加人员休假计划
	 * @param table
	 * @param viewlist
	 * @throws GeneralException
	 */
	private void AddValue(String table,ArrayList viewlist,String plan_id) throws GeneralException {
		   
		   
			try{
	      	  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	    	  RecordVo vo=new RecordVo(table); 
	    	  String apply_id=idg.getId("Q31.Q3101");
	    	  vo.setString("q3101",apply_id);
	    	  vo.setString("q2901",plan_id);	      	  
	          vo.setString("nbase",userView.getDbname());
	          vo.setString("a0100",userView.getA0100());
	          vo.setString("b0110",userView.getUserOrgId());
	          vo.setString("e0122",userView.getUserDeptId()!=null?userView.getUserDeptId():"");
	          vo.setString("a0101",userView.getUserFullName());
	          vo.setString("e01a1",userView.getUserPosId());
	          vo.setString("q31z5","01");
	          vo.setString("q31z0","03");
	       	  for(int i=0;i<viewlist.size();i++)
	       	    {
	       	       FieldItem field=(FieldItem)viewlist.get(i);
	       	       if("q3101".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "q2901".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "e01a1".equals(field.getItemid())|| "q31z0".equals(field.getItemid())|| "q31z5".equals(field.getItemid()))
	    	  		  continue;
	               if("N".equals(field.getItemtype()))
	                  vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
	   	   		   if("D".equals(field.getItemtype()))
	   	   		   {
	   	   			  Date date=DateUtils.getDate(field.getValue().replaceAll("\\.","-"),"yyyy-MM-dd");
	   	   		      vo.setDate(field.getItemid().toLowerCase(),date);	   	   		      
	   	   		   }else{
	   	   			  // System.out.println(field.getItemid());
	   	   			vo.setString(field.getItemid().toLowerCase(),field.getValue());
	   	   		   }
	       	    }
	       	  ContentDAO dao=new ContentDAO(this.getFrameconn());
	       	  dao.addValueObject(vo);
	         }catch(Exception e){
	           e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e); 
	          }
	}
	
	 public static int getI9999(String tablename,String pid){
	    	int i9999=1;
	    	String sql="select max(i9999) as i9999 from "+tablename+" where  q3101='"+pid;
	    	List rs=null;
	    	rs = ExecuteSQL.executeMyQuery(sql);
		       if(!rs.isEmpty())
		       {
		    	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);	    	  
		    	  String str_i9999=rec.get("i9999")!=null?rec.get("i9999").toString():"1";	  
		    	  if(str_i9999!=null&&str_i9999.length()>0)
		    	  {
		    		  i9999=Integer.parseInt(str_i9999); 
		    		  i9999=i9999+1;
		    	  }
		       }
	    	return i9999;
	    }
	 public boolean getIfSave(String nbase,String a0100,String plan_id,ArrayList viewlist)throws GeneralException
	 {
		boolean isCorrect=true;
		Date F_time=null;
		Date T_time=null;
		for(int i=0;i<viewlist.size();i++)
    	{
    	     FieldItem field=(FieldItem)viewlist.get(i);    	     
            
	   		   if("D".equals(field.getItemtype()))
	   		   {
	   			  Date date=DateUtils.getDate(field.getValue().replaceAll("\\.","-"),"yyyy-MM-dd");
	   		      if("q31z1".equalsIgnoreCase(field.getItemid()))
	   		    	F_time =date;
	   		      else if("q31z3".equalsIgnoreCase(field.getItemid()))
	   		    	T_time=date;
	   		   }else{
	   			 continue;
	   		   }
    	}
		String z1=DateUtils.format(F_time,"yyyy-MM-dd HH:mm");
		String z3=DateUtils.format(T_time,"yyyy-MM-dd HH:mm");
		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());  
		if(annualApply.isRepeatedApp(nbase,a0100,z1,z3,"q31",this.getFrameconn(),"",""))
	    {
	    	isCorrect=false;
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",this.userView.getUserFullName()+",在这个申请的时间段已经申请了休假计划","",""));
	    }	
		isCorrect=isSave2(F_time,T_time,nbase,a0100,this.userView.getUserFullName());
		return isCorrect;
	 }
	 public boolean isSave2(Date kq_start,Date kq_end,String nbase,String a0100,String b0110)throws GeneralException
	 {
		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());
		float re=0;		    	
	    HashMap kqItem_hash=annualApply.count_Leave("06");
	    boolean isCorrect=true;	
//	    float leave_tiem=annualApply.planDays(kq_start,kq_end,kqItem_hash);
//	    if(leave_tiem<=0)
//		{
//		    	isCorrect=false;
//		    	throw GeneralExceptionHandler.Handle(new GeneralException("",this.userView.getUserFullName()+"可休有效时间为0天","",""));
//		}
	    String start=DateUtils.format(kq_start,"yyyy.MM.dd HH:mm");
	    String end=DateUtils.format(kq_end,"yyyy.MM.dd HH:mm");
	    float myTime=annualApply.getMy_Time("06",a0100,nbase,start,end,b0110,kqItem_hash);
	    if(b0110!=null && b0110.equals(this.userView.getUserFullName())) b0110="";
	    float other_time=annualApply.othenPlanTime(kq_start,kq_end,a0100,nbase,b0110,"",kqItem_hash,"");
	    re=myTime;//-leave_tiem-other_time;
	    if(re<=0)
		{
				isCorrect=false;
				String message=this.userView.getUserFullName()+"，"+ResourceFactory.getProperty("error.kq.morelet");
				KqUtilsClass kqUtilsClass=new KqUtilsClass();
				myTime=kqUtilsClass.round(myTime+"",1);
				message=message+"可休时间为"+myTime+"天！";
				if(other_time>0)
				 message=message+"在这之前您已经计划了"+other_time+"天!<br>"+annualApply.getAppLeavedMess();
				throw GeneralExceptionHandler.Handle(new GeneralException("",message,"",""));
		}
	    return isCorrect;
   }
}
