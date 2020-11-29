package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 做全选操作_检测操作表有没有state字段，没有则添加上，全选后该字段值为1
 *<p>Title:SelectAllOperateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 8, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SelectAllOperateTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");	
		String query_type=(String)this.getFormHM().get("query_type");
		if(query_type==null|| "".equals(query_type))
			query_type="1";
		String sp_flag=(String)this.getFormHM().get("sp_flag");
		if(sp_flag==null|| "".equals(sp_flag))
			sp_flag="all";
		String kq_item=(String)this.getFormHM().get("showtype");
		if(kq_item==null|| "".equals(kq_item))
			kq_item="all";
		String table = (String) this.getFormHM().get("table");	
		String ta=table.toLowerCase();
		String frist = (String) this.getFormHM().get("wo");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		String select_pre=(String)this.getFormHM().get("select_pre");	
		String select_time_type=(String)this.getFormHM().get("select_time_type");
		String code=(String)this.getFormHM().get("code");		
		String kind=(String)this.getFormHM().get("kind");	
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");	
		if(start_date!=null&&start_date.length()>0)
			start_date=start_date.replaceAll("\\.","-");
		if(end_date!=null&&end_date.length()>0)
			end_date=end_date.replaceAll("\\.","-");
		if(!(validateDate(start_date)&&validateDate(end_date)))
		{
			ArrayList datelist =RegisterDate.getKqDayList(this.getFrameconn()); 
			if(datelist!=null&&datelist.size()>0)
			{
				start_date=datelist.get(0).toString();
				end_date=datelist.get(datelist.size()-1).toString();
				if(start_date!=null&&start_date.length()>0)
					start_date=start_date.replaceAll("\\.","-");
				if(end_date!=null&&end_date.length()>0)
					end_date=end_date.replaceAll("\\.","-");
			}else
			{
				start_date=DateStyle.dateformat(new java.util.Date(),"yyyy-MM-dd");
				end_date=start_date; 
			}
			 //当天假单
		}
		StringBuffer whereINStr=new StringBuffer();
		StringBuffer cond_str = new StringBuffer();
		cond_str.append(" from ");
		cond_str.append(table);
		cond_str.append(" where ");
		whereINStr.append(" 1=1 ");//过滤条件
		/**左边树节点代码*/
		if(!(code==null|| "".equalsIgnoreCase(code)))
		{
			if("1".equals(kind))
			{
				whereINStr.append(" and e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				whereINStr.append(" and e01a1 like '"+code+"%'");	
			}else
			{
				whereINStr.append(" and b0110 like '"+code+"%'");	
			}			
		}else
		{
			String privcode=RegisterInitInfoData.getKqPrivCode(userView);
			String codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
			if("UM".equalsIgnoreCase(privcode))
				whereINStr.append(" and e0122 like '"+codevalue+"%'");
			else if("@K".equalsIgnoreCase(privcode))
				whereINStr.append(" and e01a1 like '"+codevalue+"%'");
			else if("UN".equalsIgnoreCase(privcode))
				whereINStr.append(" and b0110 like '"+codevalue+"%'");
		}
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		if(where_c!=null&&where_c.length()>0)
			whereINStr.append(" "+where_c+"");
		SearchAllApp searchAllApp=new SearchAllApp(this.getFrameconn(),this.userView);
		String cond0=searchAllApp.getWhere2(table, start_date, end_date, kq_item, sp_flag, query_type,select_time_type);
		if(cond0.length()>0)
		{
			whereINStr.append(" and ");
			whereINStr.append(cond0);
		}
		
		if("Q15".equalsIgnoreCase(table))
		{
			whereINStr.append(" and "+Sql_switcher.isnull("q1517","0")+"=0");
		}	
		String where_is=whereINStr.toString();	   
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
				sql_db_list.add(select_pre);
		}else
		{
				sql_db_list=kq_dbase_list;
	    }
		SelectAllOperate selectAllOperate=new SelectAllOperate(this.getFrameconn(),this.userView);
		String state_flag=(String)this.getFormHM().get("state_flag");
		if(state_flag==null||state_flag.length()<=0)
			state_flag="0";
		if(!"1".equals(state_flag))
		{
			selectAllOperate.allSelectApp(kind,code,table,where_is,sql_db_list,"1");
			this.getFormHM().put("state_flag", "1");
		}
		else
		{
			selectAllOperate.allSelectApp(kind,code,table,where_is,sql_db_list,"0");
			this.getFormHM().put("state_flag", "0");
		}
			
	}
	
	/**
	 * 校验日期是否正确
	 * @return
	 */
	private boolean validateDate(String datestr)
	{
		boolean bflag=true;
		if(datestr==null|| "".equals(datestr))
			return false;
		try
		{
			java.util.Date date=DateStyle.parseDate(datestr);
			if(date==null)
				bflag=false;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
}
