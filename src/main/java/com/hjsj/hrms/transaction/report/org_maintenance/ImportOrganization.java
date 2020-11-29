
package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;


public class ImportOrganization extends IBusiness 
{
	public void execute() throws GeneralException 
	{		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		try
		{
			//规则：不删除动态表，tt_organization 里把原有单位的截止日期设为当前日期的前一天。组织机构的单位进来后单位id前加上自动生成的两位。xgq20110114
			//删除动态表 ta_xxxx tt_sxxx tt_xxxx tt_txxx tt_pxxx 
//			RowSet recset=dao.search(" select tabid from tname ");
//			while(recset.next())
//			{
//				String tabid=recset.getString("tabid");
//				if(dbWizard.isExistTable("ta_"+tabid,false))
//				{
//					dao.delete(" delete from "+"ta_"+tabid, new ArrayList());
//				} 
//				if(dbWizard.isExistTable("tt_s"+tabid,false))
//				{
//					dao.delete(" delete from "+"tt_s"+tabid, new ArrayList());
//				} 
//				if(dbWizard.isExistTable("tt_"+tabid,false))
//				{
//					dao.delete(" delete from "+"tt_"+tabid, new ArrayList());
//				} 
//				if(dbWizard.isExistTable("tt_t"+tabid,false))
//				{
//					dao.delete(" delete from "+"tt_t"+tabid, new ArrayList());
//				} 
//				if(dbWizard.isExistTable("tt_p"+tabid,false))
//				{
//					dao.delete(" delete from "+"tt_p"+tabid, new ArrayList());
//				} 
//			}
			
			//删除实体表treport_ctrl  tinteg_report tt_organization
		//	dao.delete(" delete from treport_ctrl ", new ArrayList());
		//	dao.delete(" delete from tinteg_report ", new ArrayList());
		//	dao.delete(" delete from tt_organization ", new ArrayList());
			
			//把organization的信息全部导入到tt_organization
			String bosdate=DateStyle.dateformat(new Date(System.currentTimeMillis()-24*60*60*1000),"yyyy-MM-dd");
			switch(Sql_switcher.searchDbServer())
		    {
				case Constant.ORACEL:
			    {
			    	bosdate="to_date('"+bosdate+"','yyyy-mm-dd')";
			    	break;
			    }
			}
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql= new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
	        //String  where=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
//			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
//				dao.update(" update tt_organization set end_date="+bosdate+" where 1=1 "+ext_sql.toString()+"");
//			else
//				dao.update(" update tt_organization set end_date='"+bosdate+"' where 1=1 "+ext_sql.toString()+"");	        
	        dao.update(" update operuser set unitcode=null ");
			this.frowset=dao.search(" select MAX(unitcode) unitcode  from tt_organization where unitcode=parentid ");
			String pre="01";
//			String order="0123456789abcdefghigklmnopqrstuvwsyz";
			String order="0123456789abcdefghijklmnopqrstuvwxyz";
			if(this.frowset.next())
			{
				pre =this.frowset.getString("unitcode");								
				if(pre!=null && pre.length()>1)
				{
					if(pre.charAt(1)=='z'||pre.charAt(1)=='Z'){
						if(pre.charAt(0)=='z'||pre.charAt(0)=='Z')
						    pre="00";
						else
							pre=""+order.charAt(order.indexOf(pre.charAt(0))+1)+"0";
					}
					else					
						pre=""+pre.charAt(0)+order.charAt(order.indexOf(pre.charAt(1))+1);					
				}
				else if(pre!=null && pre.trim().length()>0 && pre.length()<=1)
					pre="0";
				else
					pre="";								
			}
			int precount = 0;  //wangcq 2015-1-30 记录pre赋值次数，如果使用完了就直接跳出循环
			if("00".equals(pre)){   //循环完了再继续找没有使用的前缀
				this.frowset=dao.search(" select unitcode  from tt_organization where unitcode=parentid ");
				while(this.frowset.next()){
					if(precount>36*36){
						throw GeneralExceptionHandler.Handle(new GeneralException("","数据库中前缀数据已满，请清理数据。", "", ""));
					}
					if(this.frowset.getString("unitcode").startsWith(pre)){
						precount++;
						pre =this.frowset.getString("unitcode");
						this.frowset.beforeFirst();
						if(pre!=null && pre.length()>1)
						{
							if(pre.charAt(1)=='z'||pre.charAt(1)=='Z'){
								if(pre.charAt(0)=='z'||pre.charAt(0)=='Z')
								    pre="00";
								else
									pre=""+order.charAt(order.indexOf(pre.charAt(0))+1)+"0";
							}
							else					
								pre=""+pre.charAt(0)+order.charAt(order.indexOf(pre.charAt(1))+1);					
						}
						else if(pre!=null && pre.trim().length()>0 && pre.length()<=1)
							pre="0";
						else
							pre="";	
					}
				}
			}
			RowSet recsetimport=dao.search(" select * from organization where codesetid='UN' "+ext_sql.toString()+" ORDER BY CODEITEMID ");
//			RowSet recsetucode=dao.search(" select unitcode from tt_organization");
//			int count = 0; //wangcq 2015-1-19 对修改了的数据条数进行记录
			while(recsetimport.next())
			{
				StringBuffer sql = new StringBuffer();
				sql.append(" insert into tt_organization ");
				sql.append(" (unitcode , unitid , unitname, parentid ,grade,a0000,end_date,start_date) ");
				sql.append(" values (?,?,?,?,?,?,?,?) ");
				ArrayList sqlvalue=new ArrayList();
//				boolean importdata = true;   //wangcq 2015-1-17 判断tt_organization表中是否存在相应字段，有就不添加
				String unitcode = pre+recsetimport.getString("codeitemid");
//				recsetucode.beforeFirst();
//				while(recsetucode.next()){
//					if(recsetucode.getString("unitcode").equals(unitcode)){
//						importdata = false;
//						break;
//					}
//				}
//				if(importdata){
					sqlvalue.add(unitcode);
					sqlvalue.add(new Integer(this.getUnitID()));
					sqlvalue.add(recsetimport.getString("codeitemdesc"));
					sqlvalue.add(pre+recsetimport.getString("parentid"));
					sqlvalue.add(recsetimport.getString("grade"));
					sqlvalue.add(new Integer(this.getUnitID()));
					sqlvalue.add( recsetimport.getDate("end_date"));
					sqlvalue.add(recsetimport.getDate("start_date"));
					dao.insert(sql.toString(),sqlvalue);
//					count++;
//				}
				
			}
			
//			if(count>0){
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					dao.update(" update tt_organization set end_date="+bosdate+" where 1=1 "+ext_sql.toString()+" and unitcode not like '"+pre+"%'");
				else
					dao.update(" update tt_organization set end_date='"+bosdate+"' where 1=1 "+ext_sql.toString()+" and unitcode not like '"+pre+"%'");	     
//			}
			
			this.getFormHM().put("addFlag","b_delete_save");
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}          		
	}
	
	public synchronized int getUnitID() throws GeneralException
	{
		int num = 0;  //序号默认为0
		String sql="select max(unitid) as num  from tt_organization";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
				num = this.frowset.getInt("num");
			
		}catch(Exception e)
		{
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num+1;		
	}	
	
}
     