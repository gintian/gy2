package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.util.ArrayList;

public class SaveAnnualPlanTrans extends IBusiness {
	private void AddValue(String table,ArrayList viewlist, Date dat,String yes,String id) throws GeneralException {
		   
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
			try{
	      	  IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	    	  RecordVo vo=new RecordVo(table);        	  
	      	  
	    	  String insertid=idg.getId("Q29.Q2901");
	    	  String insertname="q2901";
	      	  vo.setString("q2901",insertid);
	      	  if(this.userView.isSuper_admin())
	      	  {
	      		 vo.setString("b0110",userView.getUserOrgId()!=null&&userView.getUserOrgId().length()>0?userView.getUserDeptId():"UN");
		         vo.setString("e0122",userView.getUserDeptId()!=null&&userView.getUserOrgId().length()>0?userView.getUserDeptId():"UM");
	      	  }else
	      	  {
	      		 vo.setString("b0110",userView.getUserOrgId());
		         vo.setString("e0122",userView.getUserDeptId()!=null?userView.getUserDeptId():"");
	      	  }
	          vo.setString("q29z0","03");
	          vo.setString("q29z5","01");	          
	          vo.setDate("q2909",dat);
	          vo.setString("q2903",yes);
	       	  for(int i=0;i<viewlist.size();i++)
	       	    {
	       	       FieldItem field=(FieldItem)viewlist.get(i);
	       	       if(field.getItemid().equals(insertname)|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "q29z5".equals(field.getItemid())|| "q29z0".equals(field.getItemid()))
	    	  		  continue;
	               if("N".equals(field.getItemtype()))
	                  vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
	   	   		   else if("D".equals(field.getItemtype()))
	                  vo.setDate(field.getItemid().toLowerCase(),field.getValue().replaceAll("\\.","-"));
	   	   	       else	
	   	   		      vo.setString(field.getItemid().toLowerCase(),field.getValue());
	       	    }
	       	if(!isYear(vo.getString("q2903")))
				throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("q2903")+",该考勤年度没有定义，请到参数设置-考勤期间中定义！","",""));
	       	  dao.addValueObject(vo);
	         }catch(Exception e){
	           e.printStackTrace();
	           throw GeneralExceptionHandler.Handle(e); 
	          }
	}

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
       ArrayList flist=(ArrayList)this.getFormHM().get("flist");
       String plan_id = (String) this.getFormHM().get("plan_id");
	 	String year = (String)this.getFormHM().get("year");
		Date start_date=null;
		if(year==null||year.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","考勤年度不能为空！","",""));		
		for(int i=0;i<flist.size();i++)
	    {
	      FieldItem field=(FieldItem)flist.get(i);
	      if("q2909".equals(field.getItemid())&& "".equals(field.getValue()))
	      	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.notapplydate"),"",""));
	      if("q2909".equals(field.getItemid()))
	      	start_date=Date.valueOf(field.getValue().replaceAll("\\.","-"));

	    }
		   this.AddValue("q29",flist,start_date,year,plan_id);
	}
    private boolean isYear(String year)
    {
       boolean isCorrect=false;
       String sql="select * from kq_duration where kq_year='"+year+"'";
       ContentDAO dao=new ContentDAO(this.getFrameconn());
       try
       {
    	   this.frowset=dao.search(sql);
    	   if(this.frowset.next())
    		   isCorrect=true;
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
       return isCorrect;
    }
}
