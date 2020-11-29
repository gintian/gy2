package com.hjsj.hrms.transaction.performance.per_initData;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *<p>Title:InitPerDataTrans.java</p> 
 *<p>Description:初始化绩效/能力素质数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 12, 2009</p> 
 *@author JinChunhai
 *@version 4.2
 */

public class InitPerDataTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			ArrayList tabList = (ArrayList)this.getFormHM().get("tabName");
			String timeScope = (String)this.getFormHM().get("timeScope"); //0：全部  1：时间范围
			String startDate = (String)this.getFormHM().get("startDate");
			String endDate = (String)this.getFormHM().get("endDate");
			String busitype = (String)this.getFormHM().get("busitype");
			String plan_ids = getPlanIDs(timeScope,startDate,endDate,busitype);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			DbWizard dbWizard = new DbWizard(this.getFrameconn()); 
			String target_ids="";
			if("1".equals(timeScope))
			{
			//	target_ids = getTargetIDs(startDate,endDate);
				target_ids = getTargetIDs2(startDate,endDate);
			}
			String subsys_id = "33";
			if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			{
				subsys_id = "35";
				
			}
			for(int i=0;i<tabList.size();i++)
			{
				String tabName=(String)tabList.get(i);

				if("0".equals(timeScope))
				{
					if(tabName.indexOf("_xxx")==-1)
					{
						if(dbWizard.isExistTable(tabName.toLowerCase(),false))
						{
							if("per_pointset".equalsIgnoreCase(tabName)|| "per_template_set".equalsIgnoreCase(tabName))
								dao.delete("delete from "+tabName+" where subsys_id = "+subsys_id+" ",new ArrayList());
							else if("per_point".equalsIgnoreCase(tabName)|| "per_grade".equalsIgnoreCase(tabName))
								dao.delete("delete from "+tabName+" where point_id in (select po.point_id from per_point po,per_pointset pe where po.pointsetid = pe.pointsetid and pe.subsys_id = "+subsys_id+") ",new ArrayList());
							else if("per_template".equalsIgnoreCase(tabName)|| "per_template_item".equalsIgnoreCase(tabName))
								dao.delete("delete from "+tabName+" where template_id in (select pt.template_id from per_template pt,per_template_set pe where pt.template_setid = pe.template_setid and pe.subsys_id = "+subsys_id+") ",new ArrayList());
							else if("per_template_point".equalsIgnoreCase(tabName))
								dao.delete("delete from "+tabName+" where item_id in (select item_id from per_template_item where template_id in (select pt.template_id from per_template pt,per_template_set pe where pt.template_setid = pe.template_setid and pe.subsys_id = "+subsys_id+")) ",new ArrayList());
							else if("OrgPointTable".equalsIgnoreCase(tabName)){
								dao.delete("delete from "+tabName,new ArrayList());
								AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
								appb.init();
								AnalysePlanParameterBo.setReturnHt(null);
								Hashtable ht=appb.analyseParameterXml();
								String pointset_menu=(String)ht.get("pointset_menu");
								dao.delete("delete from "+pointset_menu,new ArrayList());
							}
							else
								dao.delete("delete from "+tabName,new ArrayList());
						}
					}
					else
					{
						if(plan_ids.length()>0)
						{
							String[] temps=plan_ids.split(",");
							for(int j=0;j<temps.length;j++)
							{
								if(temps[j]!=null&&temps[j].length()>0)
								{
									tabName=tabName.replaceAll("_xxx","_"+temps[j]);
									if(dbWizard.isExistTable(tabName.toLowerCase(),false))
										dao.delete("delete from "+tabName,new ArrayList());
									tabName=tabName.replaceAll("_"+temps[j],"_xxx");
								}
							}
						}
					}
				}
				else
				{
					if(tabName.indexOf("_xxx")!=-1)
					{
						if(plan_ids.length()>0)
						{
							String[] temps=plan_ids.split(",");
							for(int j=0;j<temps.length;j++)
							{
								if(temps[j]!=null&&temps[j].length()>0)
								{
									tabName=tabName.replaceAll("_xxx","_"+temps[j]);
									if(dbWizard.isExistTable(tabName.toLowerCase(),false))
										dao.delete("delete from "+tabName,new ArrayList());
									tabName=tabName.replaceAll("_"+temps[j],"_xxx");
								}
							}
						}
					}
					else
					{
						if("per_pointset".equalsIgnoreCase(tabName)|| "per_point".equalsIgnoreCase(tabName)
								|| "per_grade".equalsIgnoreCase(tabName)|| "per_template_set".equalsIgnoreCase(tabName)
								|| "per_template".equalsIgnoreCase(tabName)|| "per_template_item".equalsIgnoreCase(tabName)
								|| "per_template_point".equalsIgnoreCase(tabName)|| "per_grade_competence".equalsIgnoreCase(tabName)
								|| "per_grade_template".equalsIgnoreCase(tabName))
						{
							
							//找出 计划关联的模板
							String templateids = this.getTemplates(plan_ids);
							if(templateids.length()>0) {
								if("per_pointset".equalsIgnoreCase(tabName))
									dao.delete("delete from per_pointset where subsys_id="+subsys_id+" and pointsetid in (select pointsetid from per_point where point_id in"
										 +"(select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id in"
										 +"("+templateids+"))))",new ArrayList());
								else if("per_template_set".equalsIgnoreCase(tabName))
									dao.delete("delete from per_template_set where subsys_id="+subsys_id+" and template_setid in (select template_setid from per_template where template_id in"
											 +"("+templateids+"))",new ArrayList());
								else if("per_point".equalsIgnoreCase(tabName) || "per_grade".equalsIgnoreCase(tabName))
									dao.delete("delete from "+tabName+" where  point_id in"
											 +"(select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id in"
											 +"("+templateids+")))",new ArrayList());
								else if("per_template".equalsIgnoreCase(tabName))
									dao.delete("delete from "+tabName+" where template_id in"
											 +"("+templateids+")",new ArrayList());
								else if("per_template_item".equalsIgnoreCase(tabName)|| "per_template_point".equalsIgnoreCase(tabName))
									dao.delete("delete from "+tabName+" where  item_id in (select item_id from per_template_item where template_id in"
											 +"("+templateids+"))",new ArrayList());
								else
									dao.delete("delete from "+tabName,new ArrayList());
							}
						}
						else if("per_gather".equalsIgnoreCase(tabName))
						{
							if(plan_ids.length()>0)
							{
								String[] temps=plan_ids.split(",");
								for(int j=0;j<temps.length;j++)
								{
									if(temps[j]!=null&&temps[j].length()>0)
									{
										tabName="per_gather_"+temps[j];
										if(dbWizard.isExistTable(tabName.toLowerCase(),false))
											dao.delete("delete from per_gather where Gather_id in (select Gather_id from "+tabName+")", new ArrayList());
									}
								}
							}
						}
						else if("per_target_list".equalsIgnoreCase(tabName))
						{
							if(target_ids.length()>0)
								dao.delete("delete from per_target_list where target_id in ("+target_ids+")", new ArrayList());
						}
						else if("per_target_mx".equalsIgnoreCase(tabName))
						{
							if(target_ids.length()>0)
								dao.delete("delete from per_target_mx where target_id in ("+target_ids+")", new ArrayList());
						}
						else if("per_target_point".equalsIgnoreCase(tabName))
						{
							if(target_ids.length()>0)
								dao.delete("delete from per_target_point where target_id in ("+target_ids+")", new ArrayList());
						}
						else if("per_target_evaluation".equalsIgnoreCase(tabName))
						{
							if(plan_ids.length()>0)
								dao.delete("delete from per_target_evaluation where p0400 in (select p0400 from p04 where plan_id in ("+plan_ids+"))", new ArrayList());
							
						}
						else if("per_param".equalsIgnoreCase(tabName)|| "per_key_event".equalsIgnoreCase(tabName))
						{
							dao.delete("delete from "+tabName,new ArrayList());
						}
						else if(plan_ids.length()>0)
							dao.delete("delete from "+tabName+" where plan_id in ("+plan_ids+")",new ArrayList());
						
					}
				}				
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
	public String getTemplates(String plan_ids) {
		String templates = "";
		StringBuffer str=new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(plan_ids.length()>0) {
			String sql="select distinct template_id from per_plan where plan_id in("+plan_ids+") ";
			try {
				this.frowset=dao.search(sql);
				while(frowset.next()){
					String template = frowset.getString("template_id");
					String sql1 = "select plan_id from per_plan where template_id='"+template+"' and plan_id not in("+plan_ids+") ";
					this.frecset = dao.search(sql1);
					boolean b = false;
					while(frecset.next()){
						b=true;
					}
					if(b){
						str.append(",'"+template+"'");
					}
				}
				if(str.length()>0)
					templates=str.substring(1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return templates;
	}
	public String getTargetIDs(String startDate,String endDate)
	{
		String ids="";
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="select target_id from per_target_list where 1=1 ";
			if(startDate.length()>0)
			{
				StringBuffer sub_str=new StringBuffer("");
				String[] temps=startDate.split("-");
				sql+=" and theyear>='"+temps[0]+"' ";
			}
			if(endDate.length()>0)
			{
				StringBuffer sub_str=new StringBuffer("");
				String[] temps=endDate.split("-");
				sql+=" and theyear<='"+temps[0]+"' ";
			}
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				str.append(","+this.frowset.getString(1));
			}
			
			if(str.length()>0)
				ids=str.substring(1);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ids;
	}
	
	public String getTargetIDs2(String startDate,String endDate)
	{
		String ids="";
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="select target_id from per_target_list where 1=1 ";
			/*
			if(startDate.length()>0)
			{
				StringBuffer sub_str=new StringBuffer("");
				String[] temps=startDate.split("-");
				sub_str.append(" ( (cycle=0 and  theyear>='"+temps[0]+"' )");
				//半年
				String thequarter="1";
				if(Integer.parseInt(temps[1])>6)
					thequarter="2";
				sub_str.append(" or (cycle=1 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter>='"+thequarter+"')  ) )");
				//季度
				thequarter="01";
				if(Integer.parseInt(temps[1])>3)
					thequarter="02";
				if(Integer.parseInt(temps[1])>6)
					thequarter="03";
				if(Integer.parseInt(temps[1])>9)
					thequarter="04";
				sub_str.append(" or (cycle=2 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter>='"+thequarter+"')  ) )");
				//月份
				sub_str.append(" or (cycle=3 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and themonth>='"+temps[1]+"')  ) ))");
			
				
				sql+=" and "+sub_str.toString();
			}
			
			if(endDate.length()>0)
			{
				StringBuffer sub_str=new StringBuffer("");
				String[] temps=endDate.split("-");
				sub_str.append(" ( (cycle=0 and  theyear<='"+temps[0]+"' )");
				//半年
				String thequarter="1";
				if(Integer.parseInt(temps[1])>6)
					thequarter="2";
				sub_str.append(" or (cycle=1 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter<='"+thequarter+"')  ) )");
				//季度
				thequarter="01";
				if(Integer.parseInt(temps[1])>3)
					thequarter="02";
				if(Integer.parseInt(temps[1])>6)
					thequarter="03";
				if(Integer.parseInt(temps[1])>9)
					thequarter="04";
				sub_str.append(" or (cycle=2 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter<='"+thequarter+"')  ) )");
				//月份
				sub_str.append(" or (cycle=3 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and themonth<='"+temps[1]+"')  ) ))");
				
				sql+=" and "+sub_str.toString();
			}
			*/
			if (startDate.length() > 0)
			{
			    StringBuffer buf = new StringBuffer();
			    buf.append(Sql_switcher.year("create_date") + ">" + getDatePart(startDate, "y") + " or ");
			    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(startDate, "y") + " and ");
			    buf.append(Sql_switcher.month("create_date") + ">" + getDatePart(startDate, "m") + ") or ");
			    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(startDate, "y") + " and ");
			    buf.append(Sql_switcher.month("create_date") + "=" + getDatePart(startDate, "m") + " and ");
			    buf.append(Sql_switcher.day("create_date") + ">=" + getDatePart(startDate, "d") + ")");
			    sql+=" and (" + buf.toString() + ")";
			}
			if (endDate.length() > 0)
			{
			    StringBuffer buf = new StringBuffer();
			    buf.append(Sql_switcher.year("create_date") + "<" + getDatePart(endDate, "y") + " or ");
			    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(endDate, "y") + " and ");
			    buf.append(Sql_switcher.month("create_date") + "<" + getDatePart(endDate, "m") + ") or ");
			    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(endDate, "y") + " and ");
			    buf.append(Sql_switcher.month("create_date") + "=" + getDatePart(endDate, "m") + " and ");
			    buf.append(Sql_switcher.day("create_date") + "<=" + getDatePart(endDate, "d") + ")");
			    sql+=" and (" + buf.toString() + ")";
			}
			
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				str.append(","+this.frowset.getString(1));
			}
			
			if(str.length()>0)
				ids=str.substring(1);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ids;
	}	
	
	/**按时间段初始化时，按计划的创建时间来判断*/
	public String getPlanIDs(String timeScope,String startDate,String endDate,String busitype)
	{
		String ids="";
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql = "select plan_id from per_plan where 1=1 ";
			
			if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
				sql+=" and busitype=1 ";
			else
				sql+=" and ("+ Sql_switcher.isnull("busitype", "0") +"=0 or busitype=0) ";
			
			if("1".equals(timeScope))
			{/*
				if(startDate.length()>0)
				{
					StringBuffer sub_str=new StringBuffer("");
					String[] temps=startDate.split("-");
					sub_str.append(" ( (cycle=0 and  theyear>='"+temps[0]+"' )");
					//半年
					String thequarter="1";
					if(Integer.parseInt(temps[1])>6)
						thequarter="2";
					sub_str.append(" or (cycle=1 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter>='"+thequarter+"')  ) )");
					//季度
					thequarter="01";
					if(Integer.parseInt(temps[1])>3)
						thequarter="02";
					if(Integer.parseInt(temps[1])>6)
						thequarter="03";
					if(Integer.parseInt(temps[1])>9)
						thequarter="04";
					sub_str.append(" or (cycle=2 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter>='"+thequarter+"')  ) )");
					//月份
					sub_str.append(" or (cycle=3 and  ( theyear>'"+temps[0]+"' or (theyear='"+temps[0]+"' and themonth>='"+temps[1]+"')  ) )");
//					不定期
					sub_str.append(" or (cycle=7 and ( "+Sql_switcher.year("Start_date")+">"+temps[0]+" or ("+Sql_switcher.year("Start_date")+"="+temps[0]+" and "+Sql_switcher.month("Start_date")+">"+temps[1]+"  ) or ( "+Sql_switcher.year("Start_date")+"="+temps[0]+" and "+Sql_switcher.month("Start_date")+"="+temps[1]+"  and "+Sql_switcher.day("Start_date")+">"+temps[2]+" )  ) )  )");
					
					sql+=" and "+sub_str.toString();
				}
				
				if(endDate.length()>0)
				{
					StringBuffer sub_str=new StringBuffer("");
					String[] temps=endDate.split("-");
					sub_str.append(" ( (cycle=0 and  theyear<='"+temps[0]+"' )");
					//半年
					String thequarter="1";
					if(Integer.parseInt(temps[1])>6)
						thequarter="2";
					sub_str.append(" or (cycle=1 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter<='"+thequarter+"')  ) )");
					//季度
					thequarter="01";
					if(Integer.parseInt(temps[1])>3)
						thequarter="02";
					if(Integer.parseInt(temps[1])>6)
						thequarter="03";
					if(Integer.parseInt(temps[1])>9)
						thequarter="04";
					sub_str.append(" or (cycle=2 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and thequarter<='"+thequarter+"')  ) )");
					//月份
					sub_str.append(" or (cycle=3 and  ( theyear<'"+temps[0]+"' or (theyear='"+temps[0]+"' and themonth<='"+temps[1]+"')  ) )");
					//不定期
					sub_str.append(" or (cycle=7 and ( "+Sql_switcher.year("end_date")+"<"+temps[0]+" or ("+Sql_switcher.year("end_date")+"="+temps[0]+" and "+Sql_switcher.month("end_date")+"<"+temps[1]+"  ) or ( "+Sql_switcher.year("end_date")+"="+temps[0]+" and "+Sql_switcher.month("end_date")+"="+temps[1]+"  and "+Sql_switcher.day("end_date")+"<"+temps[2]+" )  ) )  )");
					sql+=" and "+sub_str.toString();
				}
				*/
				if (startDate.length() > 0)
				{
				    StringBuffer buf = new StringBuffer();
				    buf.append(Sql_switcher.year("create_date") + ">" + getDatePart(startDate, "y") + " or ");
				    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(startDate, "y") + " and ");
				    buf.append(Sql_switcher.month("create_date") + ">" + getDatePart(startDate, "m") + ") or ");
				    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(startDate, "y") + " and ");
				    buf.append(Sql_switcher.month("create_date") + "=" + getDatePart(startDate, "m") + " and ");
				    buf.append(Sql_switcher.day("create_date") + ">=" + getDatePart(startDate, "d") + ")");
				    sql+=" and (" + buf.toString() + ")";
				}
				if (endDate.length() > 0)
				{
				    StringBuffer buf = new StringBuffer();
				    buf.append(Sql_switcher.year("create_date") + "<" + getDatePart(endDate, "y") + " or ");
				    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(endDate, "y") + " and ");
				    buf.append(Sql_switcher.month("create_date") + "<" + getDatePart(endDate, "m") + ") or ");
				    buf.append("(" + Sql_switcher.year("create_date") + "=" + getDatePart(endDate, "y") + " and ");
				    buf.append(Sql_switcher.month("create_date") + "=" + getDatePart(endDate, "m") + " and ");
				    buf.append(Sql_switcher.day("create_date") + "<=" + getDatePart(endDate, "d") + ")");
				    sql+=" and (" + buf.toString() + ")";
				}
			}
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				str.append(","+this.frowset.getString(1));
			}
			
			if(str.length()>0)
				ids=str.substring(1);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ids;
	}
	
    public String getDatePart(String mydate, String datepart)
    {

		String str = "";
		if ("y".equalsIgnoreCase(datepart))
		    str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
		    if ("0".equals(mydate.substring(5, 6)))
			str = mydate.substring(6, 7);
		    else
			str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
		    if ("0".equals(mydate.substring(8, 9)))
			str = mydate.substring(9, 10);
		    else
			str = mydate.substring(8, 10);
		}
		return str;
    }	

}
