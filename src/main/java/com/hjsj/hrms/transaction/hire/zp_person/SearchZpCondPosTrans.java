/*
 * Created on 2005-10-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_person;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchZpCondPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {    	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String domain_value=(String)hm.get("domain_value");	
    	try{
    	   domain_value = PubFunc.ToGbCode(domain_value);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		String valid_date_value=(String)hm.get("valid_date");
		String pos_id=(String)hm.get("pos_id");
		StringBuffer strsql=new StringBuffer();
		switch(Sql_switcher.searchDbServer())
	 	{
	 	   case Constant.ORACEL:
			  if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and valid_date like sysdate");
	          }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and valid_date like sysdate-"+Integer.parseInt(valid_date_value));
	          }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"'");
	          }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and valid_date like sysdate ");
	          }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and valid_date like sysdate-"+Integer.parseInt(valid_date_value));
	          }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and valid_date like sysdate ");
	          }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and valid_date like sysdate-"+Integer.parseInt(valid_date_value));
	          }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%'");
	          }else if(pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"'");
	          }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where valid_date like sysdate ");
	          }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where valid_date like sysdate-"+Integer.parseInt(valid_date_value));
	          }else{
	    	      strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position");
	          }
	 		  break;
	 	  case Constant.DB2:
	 	  	if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) = 0");
            }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) <= "+Integer.parseInt(valid_date_value)+" and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) >= 0");
            }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"'");
            }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) = 0 ");
            }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) <= "+Integer.parseInt(valid_date_value)+" and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) >= 0");
            }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) = 0 ");
            }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) <= "+Integer.parseInt(valid_date_value)+" and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) >= 0");
            }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%'");
            }else if(pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"'");
            }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) = 0 ");
            }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) <= "+Integer.parseInt(valid_date_value)+" and TIMESTAMPDIFF(16,CHAR(Current Timestamp-valid_date)) >= 0");
            }else{
   	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position");
            }
	 		break;
	 	   default:
	 	         if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and datediff(day,valid_date,getdate()) = 0");
	             }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"' and datediff(day,valid_date,getdate()) <= "+Integer.parseInt(valid_date_value)+" and datediff(day,valid_date,getdate()) >= 0");
	             }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and pos_id = '"+pos_id+"'");
	             }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and datediff(day,valid_date,getdate()) = 0 ");
	             }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)&& valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%' and datediff(day,valid_date,getdate()) <= "+Integer.parseInt(valid_date_value)+" and datediff(day,valid_date,getdate()) >= 0");
	             }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id) && "0".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and datediff(day,valid_date,getdate()) = 0 ");
	             }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"' and datediff(day,valid_date,getdate()) <= "+Integer.parseInt(valid_date_value)+" and datediff(day,valid_date,getdate()) >= 0");
	             }else if(domain_value != null && !"".equals(domain_value) && !"0".equals(domain_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where domain like '%"+domain_value+"%'");
	             }else if(pos_id!=null && !"".equals(pos_id)&&!"0".equals(pos_id)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+pos_id+"'");
	             }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value) && "0".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where datediff(day,valid_date,getdate()) = 0 ");
	             }else if(valid_date_value!= null && !"".equals(valid_date_value)&&!"a".equals(valid_date_value)){
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where datediff(day,valid_date,getdate()) <= "+Integer.parseInt(valid_date_value)+" and datediff(day,valid_date,getdate()) >= 0");
	             }else{
	    	         strsql.append("select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position");
	             }
	 			 break;
	 	}
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("zp_position",1);
	          vo.setString("zp_pos_id",this.getFrowset().getString("zp_pos_id"));
	          vo.setString("amount",this.getFrowset().getString("amount"));
	          String sql = "select parentid from organization where codeitemid = '"+this.getFrowset().getString("dept_id")+"'";
	          ArrayList namelist = new ArrayList();
	          ResultSet rs = dao.search(sql,namelist);
	          while(rs.next()){
	          	 vo.setString("dept_id",rs.getString("parentid"));
	          }
	          vo.setString("pos_id",this.getFrowset().getString("pos_id"));
	          vo.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
	          vo.setString("domain",PubFunc.toHtml(this.getFrowset().getString("domain")));
	          String ssql = "select count(a0100) as count from zp_pos_tache where zp_pos_id = '"+this.getFrowset().getString("zp_pos_id")+"'";
	          ResultSet rst = dao.search(ssql,namelist);
	          while(rst.next()){
	          	 vo.setString("plan_id",rst.getString("count"));
	          }
	          list.add(vo);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("zppositionlist",list);
	    }

	}

}
