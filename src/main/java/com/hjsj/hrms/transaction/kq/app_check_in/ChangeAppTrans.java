package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChangeAppTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList infolist=(ArrayList)this.getFormHM().get("infolist");
		String table=(String)this.getFormHM().get("table");
		String ta=table.toLowerCase();
		String or=(String)this.getFormHM().get("radio");
		String result=(String)this.getFormHM().get("result");
		String audit_flag=(String)this.getFormHM().get("audit_flag");
		
		if(infolist==null||infolist.size()==0)
		  return;
		if(or==null|| "".equals(or))
		if(audit_flag==null||audit_flag.length()<=0)
          return;
		if("1".equals(audit_flag))//审批
		{
		    this.upData_audit(table,or,result,infolist);
		    for(int i=0;i<infolist.size();i++)
       	    {
       	       LazyDynaBean rec=(LazyDynaBean)infolist.get(i);   
       	       String nn=(String) rec.get((ta+"03").toString());
       	       String A0100=rec.get("a0100").toString();
       	       String nbase=rec.get("nbase").toString();
       	       String b0110=rec.get("b0110").toString();
       	       String id=rec.get( ta+"01").toString();
       	       String stat=rec.get( ta+"z5").toString();
       	       if(ta.indexOf("q15")!=-1&& "01".equals(or)&&!"03".equals(stat.trim()))
       	       {
       	    	  this.upLeaveManage(nn,A0100,nbase,b0110,id);
       	       }else{
       	    	  this.upData_audit(table,or,result,infolist);
       	       }
			}
		}else if("2".equals(audit_flag))
		{
			this.upData_overrule(table,result,infolist);
		}
		     


	}
	
	private void upLeaveManage(String sels, String a0100,String nbase,String b0110,String t_id) throws GeneralException
	{
		 Date kq_start=null;
		 Date kq_end=null;
		 String a0101="";
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 RecordVo vo=new RecordVo("q15");     
		 try
		 {
			    	  
   	         vo.setString("q1501",t_id);
   	         vo=dao.findByPrimaryKey(vo);
   	         kq_start=vo.getDate("q15z1");
   	         kq_end=vo.getDate("q15z3");
   	         a0101=vo.getString("a0101");
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 
		 if(KqParam.getInstance().isHoliday(this.frameconn, b0110, sels))
		 {
			 float re=0;
			 AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());
		     HashMap kqItem_hash=annualApply.count_Leave(sels);
		     float[] holiday_rules=annualApply.getHoliday_minus_rule();//年假假期规则
		     float leave_tiem=annualApply.getHistoryLeaveTime(kq_start,kq_end,a0100,nbase,b0110,kqItem_hash,holiday_rules);
		    
		    if(leave_tiem<=0)
		    	throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+"可休有效时间为0天","",""));
		    
		    String start=DateUtils.format(kq_start,"yyyy.MM.dd HH:mm:ss");
		    String end=DateUtils.format(kq_end,"yyyy.MM.dd HH:mm:ss");
		    float myTime=annualApply.getMy_Time(sels,a0100,nbase,start,end,b0110,kqItem_hash);
		    re=myTime-leave_tiem;
		    if(re<0)
			{
					
					String message=a0101+","+ResourceFactory.getProperty("error.kq.morelet");
					message=message+"可休时间为"+myTime+"天！";
					throw GeneralExceptionHandler.Handle(new GeneralException("",message,"",""));
			}else
			{
				
				String history=annualApply.upLeaveManage(a0100,nbase,sels,start,end,leave_tiem,"1",b0110,kqItem_hash,holiday_rules);
				try {
					vo.setString("history", history);
					dao.updateValueObject(vo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		 }
	}
	/**
	 * 审批
	 * @param table
	 * @param radio
	 * @param idea
	 * @param infolist
	 * @throws GeneralException
	 */
	private void upData_audit (String table,String radio,String idea,ArrayList infolist ) throws GeneralException
	{
		  String ta=table.toLowerCase();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  String idea_coulmn=getIdeaCoulmn(ta);
		  StringBuffer  upl=null;
          try{
        	
		      for(int i=0;i<infolist.size();i++)
              {
			    LazyDynaBean rec=(LazyDynaBean)infolist.get(i);  
			      upl=new StringBuffer();
			     upl.append(" update ");
			     upl.append(table);
			     upl.append(" set ");
			     if(userView.isOrgLeader())
			     {
			       upl.append(ta+"15='");
			       upl.append(idea);
			       upl.append("',");
			     }else{

			       upl.append(idea_coulmn+"='");
			       upl.append(idea);
			       upl.append("',");
			     }
			     upl.append(ta+"z0='");
			     upl.append(radio);
			     upl.append("',");
			     upl.append(ta+"z5='");
			     upl.append("03");
			     upl.append("' where ");
			     upl.append(ta+"01='");
			     upl.append(rec.get( ta+"01").toString());
			     upl.append("'");
			     upl.append(" and "+ta+"z5<>'03'");
			     			     
			   dao.update(upl.toString());
               }
            }catch(Exception sqle)
            {
	           sqle.printStackTrace();
	         throw GeneralExceptionHandler.Handle(sqle);            
            }
	}
	/**
	 * 驳回
	 * @param table
	 * @param idea
	 * @param infolist
	 * @throws GeneralException
	 */
	private void upData_overrule (String table,String idea,ArrayList infolist ) throws GeneralException
	{
		 String ta=table.toLowerCase();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  String idea_coulmn=getIdeaCoulmn(ta);
		  StringBuffer  upl=null;
          try{
        	
		      for(int i=0;i<infolist.size();i++)
              {
			    LazyDynaBean rec=(LazyDynaBean)infolist.get(i);  
			      upl=new StringBuffer();
			     upl.append(" update ");
			     upl.append(table);
			     upl.append(" set ");
			     if(userView.isOrgLeader())
			     {
			       upl.append(ta+"15='");
			       upl.append(idea);
			       upl.append("',");
			     }else{

			       upl.append(idea_coulmn+"='");
			       upl.append(idea);
			       upl.append("',");
			     }
			     upl.append(ta+"z0='02',");			     
			     upl.append(ta+"z5='");
			     upl.append("07");
			     upl.append("' where ");
			     upl.append(ta+"01='");
			     upl.append(rec.get( ta+"01").toString());
			     upl.append("'");

			     			     
			   dao.update(upl.toString());
               }
            }catch(Exception sqle)
            {
	           sqle.printStackTrace();
	         throw GeneralExceptionHandler.Handle(sqle);            
            }
	}
	public String getIdeaCoulmn(String ta)
	{
		String privCode= RegisterInitInfoData.getKqPrivCode(userView);
        String idea_coulmn=ta+"11";
        if(privCode==null||privCode.length()<=0)
        {
      	  idea_coulmn=ta+"11";
        }else if("UM".equals(privCode.toUpperCase()))
        {
      	  idea_coulmn=ta+"11";
        }else if("UN".equals(privCode.toUpperCase()))
        {
      	  idea_coulmn=ta+"15";
        }
        return idea_coulmn;
	}
}
