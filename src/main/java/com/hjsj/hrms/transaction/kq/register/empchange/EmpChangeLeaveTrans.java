package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hjsj.hrms.businessobject.kq.register.Employ_Change;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EmpChangeLeaveTrans extends IBusiness{
	public void execute()throws GeneralException
	   {
		 
		    ArrayList kq_dbase_list=userView.getPrivDbList();	
		    //String start_date=(String)this.getFormHM().get("start_date");
		    //String end_date=(String)this.getFormHM().get("end_date");	  
		    String change_date=(String)this.getFormHM().get("change_date");
			String	code=RegisterInitInfoData.getKqPrivCodeValue(userView);	
	        ContentDAO dao = new ContentDAO(this.getFrameconn());
	        
	        
	        ArrayList addlist= new ArrayList();
	        try {
		        for(int i=0;i<kq_dbase_list.size();i++)
		        {
		           String userbase = kq_dbase_list.get(i).toString();
		           String whereIN= RegisterInitInfoData.getWhereINSql(userView,userbase);
		           change_date=change_date.replaceAll("\\.","-");
		           String sql= Employ_Change.getDateSql(userbase,change_date,change_date,whereIN,0);          
	               this.frowset=dao.search(sql);
	               while(this.frowset.next()){
	            	  RecordVo vo = new RecordVo("kq_employ_change");
	            	  vo.setString("nbase",this.frowset.getString("nbase"));
	            	  vo.setString("a0100",this.frowset.getString("A0100"));
	            	  vo.setString("b0110",this.frowset.getString("B0110"));
	            	  vo.setString("e0122",this.frowset.getString("E0122"));
	            	  vo.setString("a0101",this.frowset.getString("A0101"));
	            	  vo.setString("flag",this.frowset.getString("flag"));
	            	  vo.setString("status", this.frowset.getString("status"));
	            	  Date change_D=this.frowset.getDate("change_date");
	        	      SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	        	      String date_str=format1.format(change_D);     	
	            	  vo.setString("change_date",date_str);
	            	  addlist.add(vo);
//	            	  LazyDynaBean abean=new LazyDynaBean();
//	            	  abean.set("nbase",this.frowset.getString("nbase"));
//	            	  abean.set("a0100",this.frowset.getString("A0100"));
//	            	  abean.set("b0110",this.frowset.getString("B0110"));
//	            	  abean.set("e0122",this.frowset.getString("E0122"));
//	            	  abean.set("a0101",this.frowset.getString("A0101"));
//	            	  abean.set("flag",this.frowset.getString("flag"));
//	            	  Date change_D=this.frowset.getDate("change_date");
//	        	      SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
//	        	      String date_str=format1.format(change_D);     	
//	        	      abean.set("change_date",date_str);
//	        	      addlist.add(abean);
	               }
		        } 
	        }catch(Exception e){
	        	e.printStackTrace();        	
	        	throw GeneralExceptionHandler.Handle(e);
	        } 	       
	        this.getFormHM().put("changelist",addlist);
	        this.getFormHM().put("code",code);
	        if(addlist!=null&&addlist.size()>0)
	        {
	            this.getFormHM().put("changestatus","0");	 
	            this.getFormHM().put("leave_count",addlist.size()+"");
	        }else
	        {
	            //this.getFormHM().put("changestatus","LeaveN");
	        	this.getFormHM().put("changestatus","0");	
	        	this.getFormHM().put("leave_count","0");
	        }
	   }  
	/**********判断是否可以重新计算*********
	    * 
		 * @param userbase  数据库前缀
		 * @param collectdate  操作时间
		 * @param code 部门	
		 * @param userbase  数据库前缀
		 * @return 是否可以起草
	    *
	   * *****/
	   public boolean if_Refer(String userbase,String code,String registerdate,String whereIN,String kind){
	   	     boolean isCorrect=false;
	   	     StringBuffer sql=new StringBuffer();          
	   	     sql.append("select q03z5 from Q05 where ");
	   	     sql.append(" nbase='"+userbase+"'");
	   	     if("1".equals(kind))
			 {
	   		    sql.append(" and e0122 like '"+code+"%'");
			 }else{
				sql.append(" and b0110 like '"+code+"%'");	
			 }
	   	     sql.append(" and Q03Z0='"+registerdate+"'");
	         sql.append(" and a0100 in(select a0100 "+whereIN+")");         
	         ContentDAO dao = new ContentDAO(this.getFrameconn());
	         try{
	           this.frowset = dao.search(sql.toString());
	           if(this.frowset.next())
	           {
	       	      String checkflag= (String)this.frowset.getString("q03z5");
	       	      if("01".equals(checkflag))
	       	      {
	       		     isCorrect=true;
	       	      }
	           }else{
	               isCorrect=true;//第一次汇总	
	           }
	       }catch(Exception e){
	       	  e.printStackTrace();
	       }
	   	   return isCorrect;
	   }
	   
}
