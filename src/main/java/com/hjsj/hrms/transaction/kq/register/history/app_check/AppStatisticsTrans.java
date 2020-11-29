package com.hjsj.hrms.transaction.kq.register.history.app_check;

import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 统计所有请假天数
 * @author Owner
 *
 */
public class AppStatisticsTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String a0100=(String)hm.get("a0100");
        a0100 = PubFunc.decrypt(a0100);
		String nbase=(String)hm.get("nbase");
		nbase = PubFunc.decrypt(nbase);
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");	
		String select_time_type=(String)this.getFormHM().get("select_time_type");
		if(select_time_type==null||select_time_type.length()<=0)
			select_time_type="0";
		SearchAllApp searchAllApp=new SearchAllApp(this.getFrameconn(),this.userView);
		ArrayList list =searchAllApp.getShowType("q15",this.getFrameconn());
		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList applist=new ArrayList();
		String isLeave="false";
        try
        {
        	for(int i=0;i<list.size();i++)
        	{
        		CommonData da=(CommonData)list.get(i);
        		String kq_item=da.getDataValue();
        		if(kq_item!=null&& "all".equals(kq_item))
        			continue;
        		HashMap kqItem_hash=annualApply.count_Leave(kq_item);
        		StringBuffer sql=new StringBuffer();
        		sql.append("select q1501,q15z1,q15z3,b0110 from q15_arc where ");
        		sql.append(" a0100='"+a0100+"' and nbase='"+nbase+"'");
        		sql.append(" and "+Sql_switcher.isnull("q1517","0")+"=0");
        		String cond0= getWhere2("q15", start_date, end_date, kq_item, "03", "",select_time_type);
        		sql.append(" and ");
        		sql.append(cond0);
        		sql.append(" and q15z0='01'");
        		sql.append(" and "+Sql_switcher.isnull("q1517","0")+"=0");
        		//System.out.println(sql.toString());
        		rs=dao.search(sql.toString());
        		float leaveLen=0;
        		while(rs.next())
        		{
        			Date kq_start=rs.getTimestamp("q15z1");
        			Date kq_end=rs.getTimestamp("q15z3");  
        			String q1501=rs.getString("q1501");
        			String b0110=rs.getString("b0110");
        			float leave_tiem=annualApply.getLeaveTime(kq_start,kq_end,a0100,nbase,b0110,kqItem_hash);        			
        			leaveLen=leave_tiem+leaveLen;
        			StringBuffer buf=new StringBuffer();
        			buf.append("select q1501,q15z1,q15z3,b0110 from q15_arc where ");
        			buf.append(" q1517='1' and q1519='"+q1501+"' and q15z0='01' and q15z5='03'");
        			//System.out.println(buf.toString());
        			RowSet xjrs=dao.search(buf.toString());
        			float xjtime=0;
        			while(xjrs.next())
        			{
        				kq_start=xjrs.getTimestamp("q15z1");
            			kq_end=xjrs.getTimestamp("q15z3");
        				leave_tiem=annualApply.getLeaveTime(kq_start,kq_end,a0100,nbase,b0110,kqItem_hash);
        				xjtime=xjtime+leave_tiem;
        			}        			
        			leaveLen=leaveLen-xjtime;
        			
        		}
        		if(leaveLen>0)
        		{
        			String valuef=PubFunc.round(leaveLen+"",2);
        			da.setDataValue(valuef);
            		applist.add(da);
            		isLeave="true";
        		}
        		
        	}        	
        }catch(Exception e)
        {
        	e.printStackTrace();
        }finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        this.getFormHM().put("leaveTimeList", applist);
        this.getFormHM().put("isLeave", isLeave);
	}	
	/**
	 * chenmengqing added at 20070112
	 * 根据前台定义参数，生成过滤条件
	 * @param table
	 * @param kq_start
	 * @param kq_end
	 * @param kqitem
	 * @param sp_flag
	 * @param query_type
	 * @return
	 */
	public String getWhere2(String table,String kq_start,String kq_end,String kqitem,String sp_flag,String query_type,String select_time_type)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		String fieldname=null;
		buf.append(" 1=1 ");
		String column_z1=table+"z1";
		String column_z3=table+"z3";
		String column_05=table+"05";
		if("9".equals(query_type))
		{
			String z1="";
			if(kq_start!=null&&kq_start.length()>0)
				z1=kq_start+" 00:00:00";
			String z3="";
			if(kq_end!=null&&kq_end.length()>0)
				z3=kq_end+" 23:59:59";
			if(select_time_type!=null&& "1".equals(select_time_type))//按申请日期
			{
				buf.append(" and (");
				if(z1!=null&&z1.length()>0)
					buf.append(column_05+">="+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_05+"<="+Sql_switcher.dateValue(z3));
				buf.append(")");
				if(buf==null||buf.length()<15)
					buf.setLength(0);
			}else if(select_time_type!=null&& "0".equals(select_time_type))//按起止时间
			{
				buf.append(" and ((");
				if(z1!=null&&z1.length()>0)
					buf.append(column_z1+">="+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_z1+"<="+Sql_switcher.dateValue(z3));	
				buf.append(") or (");
				if(z1!=null&&z1.length()>0)
					buf.append(column_z3+">"+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_z3+"<"+Sql_switcher.dateValue(z3));	
				buf.append(")");
				if(z1!=null&&z1.length()>0&&z3!=null&&z3.length()>0){
					buf.append(" or ("+column_z1+"<="+Sql_switcher.dateValue(z1));
					buf.append(" and "+column_z3+">="+Sql_switcher.dateValue(z3)+")");
				}
				buf.append(")");
			}
			
			return buf.toString();
		}
		if(kq_start!=null&&kq_start.length()>0&&kq_end!=null&&kq_end.length()>0)
		{
			String z1=kq_start+" 00:00:00";
			String z3=kq_end+" 23:59:59";
			if(select_time_type!=null&& "1".equals(select_time_type))//按申请日期
			{
				buf.append(" and ("+column_05+">="+Sql_switcher.dateValue(z1));
				buf.append(" and "+column_05+"<="+Sql_switcher.dateValue(z3)+")");
			}else if(select_time_type!=null&& "0".equals(select_time_type))//按起止时间
			{
				buf.append(" and (("+column_z1+">"+Sql_switcher.dateValue(z1));
				buf.append(" and "+column_z1+"<"+Sql_switcher.dateValue(z3)+")");	
				buf.append(" or ("+column_z3+">"+Sql_switcher.dateValue(z1));
				buf.append(" and "+column_z3+"<"+Sql_switcher.dateValue(z3)+")");	
				buf.append(" or ("+column_z1+"<="+Sql_switcher.dateValue(z1));
				buf.append(" and "+column_z3+">="+Sql_switcher.dateValue(z3)+")");
				buf.append(")");
			}
		}else
		{
			String z1="";
			if(kq_start!=null&&kq_start.length()>0)
				z1=kq_start+" 00:00:00";
			String z3="";
			if(kq_end!=null&&kq_end.length()>0)
				z3=kq_end+" 23:59:59";
			if(select_time_type!=null&& "1".equals(select_time_type))//按申请日期
			{
				buf.append(" and (");
				if(z1!=null&&z1.length()>0)
					buf.append(column_05+">="+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_05+"<="+Sql_switcher.dateValue(z3));
				buf.append(")");
				if(buf==null||buf.length()<15)
					buf.setLength(0);
			}else if(select_time_type!=null&& "0".equals(select_time_type))//按起止时间
			{
				buf.append(" and ((");
				if(z1!=null&&z1.length()>0)
					buf.append(column_z1+">="+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_z1+"<="+Sql_switcher.dateValue(z3));	
				buf.append(") or (");
				if(z1!=null&&z1.length()>0)
					buf.append(column_z3+">"+Sql_switcher.dateValue(z1));
				else
					buf.append("1=1");
				if(z3!=null&&z3.length()>0)
					buf.append(" and "+column_z3+"<"+Sql_switcher.dateValue(z3));	
				buf.append(")");
				if(z1!=null&&z1.length()>0&&z3!=null&&z3.length()>0){
					buf.append(" or ("+column_z1+"<="+Sql_switcher.dateValue(z1));
					buf.append(" and "+column_z3+">="+Sql_switcher.dateValue(z3)+")");
				}
				buf.append(")");
			}
		}
		if(!"all".equalsIgnoreCase(sp_flag))
		{
				fieldname=table+"z5";
				buf.append(" and "+fieldname);
				buf.append("='");
				buf.append(sp_flag);
				buf.append("'");
		}
//		else
//		{
//			//部门考勤申请页面里不展现起草状态
//			fieldname=table+"z5";
//			buf.append(" and "+fieldname);
//			buf.append("<>'01'");
//		}
		
		if(!"all".equalsIgnoreCase(kqitem))
		{
				fieldname=table+"03";
				buf.append(" and "+fieldname);
				buf.append("='");
				buf.append(kqitem);
				buf.append("'");
		}	
		return buf.toString();
	}
}
