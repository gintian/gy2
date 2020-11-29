package com.hjsj.hrms.businessobject.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class EditReport {

	public ArrayList getU02FieldList(ArrayList fieldlist,String flag)
	{
		ArrayList list =new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem)fieldlist.get(i);
			if("1".equals(flag))
			{
				
			}else if("2".equals(flag))
			{
				
			}else if("3".equals(flag))
			{
				
			}else if("4".equals(flag))
			{
				
			}
			list.add(field.clone());
		}
		return list;
	}
	/**
	 * 得到U02不同人员状态返回的fieldlist
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU02FieldList(Connection conn,String Report_id,boolean isChangeLine) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
    	TargetsortBo targetsortBo =new TargetsortBo(conn);
		HashMap map=targetsortBo.getTargetsortMap(conn);
    	String fields="";    	
    	if("U02_1".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("1");
			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_1")+"没有定义指标","",""));
            }
		}else if("U02_2".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("2");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_2")+"没有定义指标","",""));
            }
		}else if("U02_3".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("3");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_3")+"没有定义指标","",""));
            }
		}else if("U02_4".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("4");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_4")+"没有定义指标","",""));
            }
		}	
    	ArrayList list=new ArrayList();	
    	fields=fields+",";
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();
            if(isChangeLine)
            {
            	itemfield.setItemdesc(reChangeLine(itemfield.getItemdesc()));
            }
			String itemid=itemfield.getItemid();
			if("U0200".equalsIgnoreCase(itemid)) {
                continue;
            }
			if(fields.indexOf(itemid+",")!=-1)
			{
				list.add(itemfield);
			}
		}
		FieldItem itemfield=DataDictionary.getFieldItem("U0200");
		list.add(0,itemfield);
		return list;
	}
	/**
	 * 导出得到U02不同人员状态返回的fieldlist过滤掉隐藏指标
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU02FieldList(Connection conn,String Report_id,boolean isChangeLine,int flag) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
    	TargetsortBo targetsortBo =new TargetsortBo(conn);
		HashMap map=targetsortBo.getTargetsortMap(conn);
    	String fields="";    	
    	if("U02_1".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("1");
			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_1")+"没有定义指标","",""));
            }
		}else if("U02_2".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("2");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_2")+"没有定义指标","",""));
            }
		}else if("U02_3".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("3");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_3")+"没有定义指标","",""));
            }
		}else if("U02_4".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("4");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_4")+"没有定义指标","",""));
            }
		}	
    	ArrayList list=new ArrayList();	
    	fields=fields+",";
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();
            if(isChangeLine)
            {
            	itemfield.setItemdesc(reChangeLine(itemfield.getItemdesc()));
            }
           
			String itemid=itemfield.getItemid();
			if("U0200".equalsIgnoreCase(itemid)) {
                continue;
            }
			 if(flag==1&&!itemfield.isVisible()){
				 continue;
	            }
			if(fields.indexOf(itemid+",")!=-1)
			{
				list.add(itemfield);
			}
		}
		FieldItem itemfield=DataDictionary.getFieldItem("U0200");
		list.add(0,itemfield);
		return list;
	}
	/**
	 * 得到U02不同人员状态返回的fieldlist
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU02FieldList(Connection conn,String Report_id,boolean isChangeLine,boolean cycleparm) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
    	TargetsortBo targetsortBo =new TargetsortBo(conn);
		HashMap map=targetsortBo.getTargetsortMap(conn);
    	String fields="";    	
    	if("U02_1".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("1");
			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_1")+"没有定义指标","",""));
            }
		}else if("U02_2".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("2");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_2")+"没有定义指标","",""));
            }
		}else if("U02_3".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("3");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_3")+"没有定义指标","",""));
            }
		}else if("U02_4".equalsIgnoreCase(Report_id))
		{
			fields=(String)map.get("4");			
			if(fields==null||fields.length()<=0) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_4")+"没有定义指标","",""));
            }
		}	
    	ArrayList list=new ArrayList();	
    	fields=fields+",";
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();
            if(isChangeLine)
            {
            	itemfield.setItemdesc(reChangeLine(itemfield.getItemdesc()));
            }
			String itemid=itemfield.getItemid();
			if("U0200".equalsIgnoreCase(itemid)) {
                continue;
            }
			if(fields.indexOf(itemid+",")!=-1)
			{
				list.add(itemfield);
			}
		}
		FieldItem itemfield=DataDictionary.getFieldItem("U0200");
		list.add(0,itemfield);
		return list;
	}
	/**
	 * 得到上下线规则
	 * @param conn
	 * @param fieldlist
	 * @param Report_id 人员范围
	 * @param emflag 人员分类
	 * @return
	 */
	public LazyDynaBean getUpdownRuleBean(Connection conn,ArrayList fieldlist,String Report_id,String emflag)
	{
		LazyDynaBean bean=new LazyDynaBean();
		String Emtype="";
		if("U02_1".equalsIgnoreCase(Report_id))
		{
			Emtype="1";
		}else if("U02_2".equalsIgnoreCase(Report_id))
		{
			Emtype="2";
		}else if("U02_3".equalsIgnoreCase(Report_id))
		{
			Emtype="3";
		}else if("U02_4".equalsIgnoreCase(Report_id))
		{
			Emtype="4";
		}
		if("1".equals(emflag)) {
            emflag="0";
        } else {
            emflag="1";
        }
		String sql="select * from tt_updown_rule where Emtype='"+Emtype+"' and Emflag='"+emflag+"'";
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			 rowSet=dao.search(sql);
			ResultSetMetaData meta = rowSet.getMetaData();
			
			if(rowSet.next())
			{
				for(int j=1;j<=meta.getColumnCount();j++)
				{
					String columnName=meta.getColumnName(j);
					if(meta.getColumnType(j)==Types.DATE||meta.getColumnType(j)==Types.TIMESTAMP)
					{
						if(rowSet.getDate(columnName)!=null)
						{
							if(meta.getColumnType(j)==Types.DATE) {
                                bean.set(columnName.toLowerCase(),PubFunc.FormatDate(rowSet.getDate(columnName)));
                            }
							if(meta.getColumnType(j)==Types.TIMESTAMP)
							{
								String temp=PubFunc.FormatDate(rowSet.getDate(columnName),"yyyy-MM-dd hh:mm:ss");
								if(temp.indexOf("12:00:00")!=-1) {
                                    temp=PubFunc.FormatDate(rowSet.getDate(columnName));
                                }
								bean.set(columnName.toLowerCase(),temp);
							}
						}
						else {
                            bean.set(columnName.toLowerCase(),"");
                        }
					}
					else
					{
						if(rowSet.getString(columnName)==null) {
                            bean.set(columnName.toLowerCase(),"");
                        } else {
                            bean.set(columnName.toLowerCase(),rowSet.getString(columnName));
                        }
							
					}
				}
			}
			
			/*
			List rslist=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			if(!rslist.isEmpty())
			{
				 bean=(LazyDynaBean)rslist.get(0);
				
			}
			*/
		}catch(Exception e)
		{
			
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return bean;
	}
	
	/**
	 * 得到上下线规则
	 * @param conn
	 * @param fieldlist
	 * @param Report_id 人员范围	 
	 * @return
	 */
	public LazyDynaBean getUpdownRuleBeans(Connection conn,ArrayList fieldlist,String Report_id)
	{
		LazyDynaBean bean=new LazyDynaBean();
		String Emtype="";
		if("U02_1".equalsIgnoreCase(Report_id))
		{
			Emtype="1";
		}else if("U02_2".equalsIgnoreCase(Report_id))
		{
			Emtype="2";
		}else if("U02_3".equalsIgnoreCase(Report_id))
		{
			Emtype="3";
		}else if("U02_4".equalsIgnoreCase(Report_id))
		{
			Emtype="4";
		}
		String sql="select * from tt_updown_rule where Emtype='"+Emtype+"'";		
		try
		{
			List rslist=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			for(int i=0;i<rslist.size();i++)
			 {
				 LazyDynaBean rulebean=(LazyDynaBean)rslist.get(i);
				 String emflag=(String)rulebean.get("emflag");
				 bean.set(emflag, rulebean);
			}
		}catch(Exception e)
		{
			
		}
		return bean;
	}
	/**
	 * 校验上下限
	 * @param updownRuleBean
	 * @param value
	 * @return
	 */
	public String  estimateRule(LazyDynaBean updownRuleBeans,LazyDynaBean bean,String U0207)
	{
		StringBuffer errorStr=new StringBuffer();
		//String U0207=(String)bean.get("u0207");
		String name=(String)bean.get("u0203");
		String remark = bean.get("u0239")==null?"":bean.get("u0239").toString().trim();
		if(U0207==null||U0207.length()<=0) {
            errorStr.append(name+":人员分类为空数据，导入数据数据失败！");
        }
		String ruleu0207 = "1".equals(U0207)?"0":"1";
		LazyDynaBean beanRule=(LazyDynaBean)updownRuleBeans.get(ruleu0207);
		if(beanRule==null) {
            return "";
        }
		for(Iterator it = bean.getMap().entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry e = (Map.Entry)it.next();   
			String fieldname=(String)e.getKey();
			String value=(String)e.getValue();
			FieldItem field= DataDictionary.getFieldItem(fieldname);
			String itemid=field.getItemid();
			String itemtype = field.getItemtype();
			String codesetid = field.getCodesetid();
			int decwidth = field.getDecimalwidth();
			if(value!=null&&value.trim().length()>0)
			{
				if("N".equalsIgnoreCase(itemtype))
			 	{
			 		String rule_u=(String)beanRule.get(itemid+"_u");
			 		if(rule_u!=null&&rule_u.length()>0)
			 		{
			 			float rule_f=Float.parseFloat(rule_u);
			 			if(rule_f<=0) {
                            continue;
                        }
			 			float value_f=Float.parseFloat(value);
			 			if(rule_f<value_f){
			 				if("".equals(remark)||remark.length()<2) {
                                errorStr.append(name+":"+field.getItemdesc()+"输入数据违反上下限规则,规则为不大于"+rule_u+"，数据偏高,数据导入失败,若福利水平核实无误，请在备注栏 加以说明，方可保存并上报数据！");
                            }
			 			}
			 			
			 		}
			 	}else if("D".equalsIgnoreCase(field.getItemtype()))//大于等于下限，小于等于上限
			 	{
			 		String rule_u=(String)beanRule.get(itemid+"_u");
			 		String rule_d=(String)beanRule.get(itemid+"_d");
			 		value=value.replace(".", "-");
			 		KqUtilsClass utils=new KqUtilsClass();
			 		if(rule_u!=null&&rule_u.length()>0)
			 		{
			 			rule_u=rule_u.replace(".", "-");
			 			Date rule_u_D=DateUtils.getDate(rule_u,"yyyy-MM-dd");
			 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
			 			if(utils.getPartMinute(vlue_D, rule_u_D)<0)
			 			{
			 				if("".equals(remark)||remark.length()<2) {
                                errorStr.append(name+":"+field.getItemdesc()+"输入数据违反上限规则,规则为不大于"+rule_u+"，日期偏高,数据导入失败,请核实信息的正确性或添加相关备注信息！");
                            }
			 			}
			 		}
			 		if(rule_d!=null&&rule_d.length()>0)
			 		{
			 			rule_d=rule_d.replace(".", "-");
			 			Date rule_d_D=DateUtils.getDate(rule_d,"yyyy-MM-dd");
			 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
			 			if(utils.getPartMinute(vlue_D, rule_d_D)>0)
			 			{
			 				if("".equals(remark)||remark.length()<2) {
                                errorStr.append(name+":"+field.getItemdesc()+"输入数据违反下限规则,规则为不小于"+rule_d+",日期偏低,数据导入失败,请核实信息的正确性或添加相关备注信息！");
                            }
			 			}
			 		}
			 	}
			}
		}		
		return errorStr.toString();
	}
	public String reChangeLine(String str)
	{
		StringBuffer buf=new StringBuffer();
		if(str==null||str.length()<=0) {
            return "";
        }
		int halfLen=6;		
		if(str.length()>7&&str.length()<=12)
		{
			halfLen=6;
			buf.append(str.substring(0,halfLen));
			buf.append("<br>");
			buf.append(str.substring(halfLen));
		}else if(str.length()>12)
		{
			int len=str.length();
			halfLen=len/2+1;
			buf.append(str.substring(0,halfLen));
			buf.append("<br>");
			buf.append(str.substring(halfLen));
		}else {
            return str;
        }
	    return buf.toString();	
	}
	/**
	 * 引入数据
	 * @param id 周期主键序号
	 * @param report_id 人员范围
	 * @param conn
	 * @param userView
	 * @throws GeneralException
	 */
	public static synchronized  void introduceData(String unitcode,String id,String report_id,Connection conn,UserView userView)throws GeneralException
	{
		String sqlstr="select * from tt_cycle where id='"+id+"'";
        ContentDAO dao=new ContentDAO(conn);
        String kmethod="";
        String cycle_id="";
        String theyear="";
        Date bos_date=new Date();
        RowSet rs=null;
        try {
			rs=dao.search(sqlstr);
			if(rs.next())
			{
				kmethod=rs.getString("Kmethod");				
				theyear=rs.getString("Theyear");
				bos_date=rs.getDate("bos_date");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bos_date==null) {
            return;
        }
		if(kmethod!=null&& "0".equals(kmethod))
		{
			/*int year=DateUtils.getYear(bos_date);
			year--;*/
			String date_str=DateUtils.format(bos_date, "yyyy-MM-dd");
			sqlstr="select id from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and  kmethod=0 order by bos_date desc";
			//sqlstr="select id from tt_cycle where Theyear='"+year+"' and kmethod=0";
			boolean isIntroduce=false;
			try {
				rs=dao.search(sqlstr);			  
				if(rs.next())
				{
					cycle_id=rs.getString("id");
					isIntroduce=true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String escope="";	
			escope=report_id.split("_")[1];
			try {
				rs=dao.search("select 1 from U02 where id='"+id+"' and unitcode='"+unitcode+"' and escope='"+escope+"'");
				if(rs.next())
				{
					isIntroduce=false;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try
				 {
					 if(rs!=null) {
                         rs.close();
                     }
			 
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
			}
			if(isIntroduce)
			{
				introduceData(unitcode,escope,id,cycle_id,conn,userView);
			}
		}
	}
	/**
	 * 判断上期是否有数据
	 * @param conn
	 * @param id
	 * @return
	 */
	public boolean isBeforeCycle(Connection conn,String id)
	{
		String sqlstr="select * from tt_cycle where id='"+id+"'";
        ContentDAO dao=new ContentDAO(conn);       
        Date bos_date=new Date();
        String kmethod="";
        RowSet rs=null;
        try {
			rs=dao.search(sqlstr);
			if(rs.next())
			{
				kmethod=rs.getString("Kmethod");
				bos_date=rs.getDate("bos_date");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bos_date==null) {
            return false;
        }
		boolean isIntroduce=false;
		if(kmethod!=null&& "0".equals(kmethod))
		{
			/*int year=DateUtils.getYear(bos_date);
			year--;
			sqlstr="select * from tt_cycle where Theyear='"+year+"' and kmethod=0";			*/
			String date_str=DateUtils.format(bos_date, "yyyy-MM-dd");
			sqlstr="select id from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and kmethod=0 order by bos_date desc";
			try {
				rs=dao.search(sqlstr);			  
				if(rs.next()) {
                    isIntroduce=true;
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try
				 {
					 if(rs!=null) {
                         rs.close();
                     }
			 
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
			}
		}
		
		return isIntroduce;
	}
	/**
	 * 
	 * @param escope   人员范围
	 * @param id  本期周期主键序号id
	 * @param cycle_id 上一周期主键序号id
	 * @param conn
	 * @param userView
	 * @throws GeneralException
	 */
	public static synchronized void introduceData(String unitcode,String escope,String id,String cycle_id,Connection conn,UserView userView)throws GeneralException
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select count(*) a from U02");
		sql.append(" where id='"+cycle_id+"' and escope='"+escope+"' and unitcode='"+unitcode+"'");		
		ContentDAO dao=new ContentDAO(conn);
		int count=0;
		try {
			
			RowSet rs=dao.search(sql.toString());			  
			if(rs.next())
			{
				count=rs.getInt("a");
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(count>0)
		{
			ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
			TargetsortBo targetsortBo =new TargetsortBo(conn);
			HashMap map=targetsortBo.getTargetsortMap(conn);
			String fields=(String)map.get(escope);
			fields=fields+",";
			StringBuffer cloums=new StringBuffer();					
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem itemfield=(FieldItem)fieldlist.get(i);			
				String itemid=itemfield.getItemid();						
				if(fields.indexOf(itemid+",")!=-1&&!"U0207".equalsIgnoreCase(itemid))
				{
					if(!"U0200".equalsIgnoreCase(itemid)) {
                        cloums.append(itemid+",");
                    }
				}
			}
			sql.delete(0,sql.length());
			sql.append("insert into U02 ("+cloums+"escope,id,U0207,u0200,editflag,unitcode)");
			sql.append("select "+cloums+"'"+escope+"',"+id+",U0207,u0200,1,'"+unitcode+"'");
			sql.append(" from U02 where id='"+cycle_id+"' and escope='"+escope+"' and unitcode='"+unitcode+"' and u0209<>'2' and u0209<>'3'");
			try {
				dao.insert(sql.toString(),new ArrayList());	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("update u02 set U0207='2' where id='"+id+"' and escope='"+escope+"' and U0207='3' and unitcode='"+unitcode+"'");
			try {
				dao.update(sql.toString());
				sql.delete(0,sql.length());
				sql.append("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id='"+id+"' and report_id='U02_"+escope+"'");
				dao.delete(sql.toString(), new ArrayList());
				sql.delete(0,sql.length());
				sql.append("insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values('"+unitcode+"','"+id+"','U02_"+escope+"','0')");
		        dao.insert(sql.toString(), new ArrayList());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	
	
	/**
	 * 引入上期向前滚动内退人员数据
	 * @param cycle_id
	 */
	public void importPreRetirementDate(String cycle_id,String pre_cycle_id,Connection conn,Date bos_date,Date pre_bos_date)
	{
		try
		{
	        ContentDAO dao=new ContentDAO(conn);
	        int yy=0;
			int mm=0;
			int dd=0;
			Calendar d=Calendar.getInstance();
			d.setTime(bos_date);  //本周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			
			
	        ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
			StringBuffer cloums=new StringBuffer();					
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem itemfield=(FieldItem)fieldlist.get(i);			
				String itemid=itemfield.getItemid();						
				if(!"U0207".equalsIgnoreCase(itemid)&&!"U0200".equalsIgnoreCase(itemid)&&!"M".equals(itemfield.getItemtype())){
					  cloums.append(itemid+",");
				}
			}
			StringBuffer sql=new StringBuffer("");
			 
			sql.append("insert into U02 ("+cloums+"escope,id,U0207,u0200,editflag,unitcode)");
			sql.append(" select "+cloums+"escope,"+cycle_id+",U0207,u0200,1,unitcode");
			sql.append(" from U02 where id="+pre_cycle_id+" and unitcode in(select unitcode from tt_organization ");
			
			sql.append(" where ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 				
			
			sql.append(" )  and u0209<>'2' and u0209<>'3'and u0209<>'6'  and u0209<>'5'  and u0209<>'9'  and u0209<>'8' ");
			dao.insert(sql.toString(),new ArrayList());	
			 
			sql.setLength(0);
			sql.append("select * from tt_organization where d_unitcode is not null and len(d_unitcode)>0 ");
			sql.append(" and ( "+Sql_switcher.year("end_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+"<="+dd+" ) ) ");	 			
			d.setTime(pre_bos_date);  //前一周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				String unitcode=rowSet.getString("unitcode");
				String d_unitcode=rowSet.getString("d_unitcode");
				
				sql.setLength(0);
				sql.append("insert into U02 ("+cloums+"escope,id,U0207,u0200,editflag,unitcode)");
				sql.append("select "+cloums+"escope,"+cycle_id+",U0207,u0200,1,'"+d_unitcode+"'");
				sql.append(" from U02 where id="+pre_cycle_id+" and unitcode='"+unitcode+"'  and u0209<>'2' and u0209<>'3' and u0209<>'6' and u0209<>'5'  and u0209<>'9'  and u0209<>'8' ");
				dao.insert(sql.toString(),new ArrayList());	
				sql.setLength(0);
				sql.append("update u02 set U0209='1' where id="+cycle_id+" and (U0209='4' or U0209='7' )");
				dao.update(sql.toString());
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 引入上期数据
	 * @param cycle_id
	 */
	public void importPreDate(String cycle_id,String pre_cycle_id,Connection conn,Date pre_bos_date,Date bos_date)
	{
		try
		{
	        ContentDAO dao=new ContentDAO(conn);
	        int yy=0;
			int mm=0;
			int dd=0;
			Calendar d=Calendar.getInstance();
	        
	        ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
			StringBuffer cloums=new StringBuffer();					
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem itemfield=(FieldItem)fieldlist.get(i);			
				String itemid=itemfield.getItemid();						
				if(!"U0207".equalsIgnoreCase(itemid)&&!"U0200".equalsIgnoreCase(itemid)&&!"M".equals(itemfield.getItemtype())){
					  cloums.append(itemid+",");
				}
			}
			StringBuffer sql=new StringBuffer("");
			dao.update("delete from u02 where id="+cycle_id);
			
			sql.append("insert into U02 ("+cloums+"escope,id,U0207,u0200,editflag,unitcode)");
			sql.append("select "+cloums+"escope,"+cycle_id+",U0207,u0200,1,unitcode");
			sql.append(" from U02 where id="+pre_cycle_id+" and unitcode in ( select unitcode from tt_organization ");
		
			d.setTime(bos_date);  //本周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			sql.append(" where ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 				
			sql.append(" )  and u0209<>'2' and u0209<>'3'and u0209<>'6'  and u0209<>'5'  and u0209<>'9'  and u0209<>'8' ");
			dao.insert(sql.toString(),new ArrayList());	
	//		sql.setLength(0);
	//		sql.append("update u02 set U0207='2' where id="+cycle_id+" and U0207='3'");
	//		dao.update(sql.toString());
			sql.setLength(0);
			sql.append("delete from tt_calculation_ctrl where   id="+cycle_id);
			dao.delete(sql.toString(), new ArrayList());
			RowSet rowSet=dao.search("select distinct unitcode,escope from u02 where id="+cycle_id+"  order by unitcode");
			while(rowSet.next())
			{
				String unitcode=rowSet.getString("unitcode");
				String escope=rowSet.getString("escope");
				
				sql.delete(0,sql.length());
				sql.append("insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values('"+unitcode+"',"+cycle_id+",'U02_"+escope+"',0)");
		        dao.insert(sql.toString(), new ArrayList());
			}
			
			sql.setLength(0);
			sql.append("select * from tt_organization where d_unitcode is not null and len(d_unitcode)>0 ");
			sql.append(" and ( "+Sql_switcher.year("end_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+"<="+dd+" ) ) ");	 			
			d.setTime(pre_bos_date);  //前一周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				String unitcode=rowSet.getString("unitcode");
				String d_unitcode=rowSet.getString("d_unitcode");
				
				sql.setLength(0);
				sql.append("insert into U02 ("+cloums+"escope,id,U0207,u0200,editflag,unitcode)");
				sql.append("select "+cloums+"escope,"+cycle_id+",U0207,u0200,1,'"+d_unitcode+"'");
				sql.append(" from U02 where id="+pre_cycle_id+" and unitcode='"+unitcode+"'  and u0209<>'2' and u0209<>'3'and u0209<>'6'  and u0209<>'5'  and u0209<>'9'  and u0209<>'8' ");
				dao.insert(sql.toString(),new ArrayList());		
			}
			sql.setLength(0);
			sql.append("update u02 set U0207='2' where id="+cycle_id+" and U0207='3'");
			dao.update(sql.toString());
			sql.setLength(0);
			sql.append("update u02 set U0209='1' where id="+cycle_id+" and (U0209='4' or U0209='7' )");
			dao.update(sql.toString());
			rowSet.close();
	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 修改idfactory
	 * @param count
	 * @param dao
	 * @throws GeneralException
	 */
	private static synchronized void updateIdFactory(int count,ContentDAO dao)throws GeneralException
	{
	   String sql="update id_factory set currentid=currentid+"+count+",curr_year=? where sequence_name=? and currentid<maxvalue";
	   int year=0;
	   year=DateUtils.getYear(new Date());
	   ArrayList list=new ArrayList();
	   list.add(year+"");
	   list.add("U02.U0200");
	   int nupdata =0;
	   try {
		   nupdata =dao.update(sql,list);
		   if (nupdata == 0)
	       {
	            throw new GeneralException("err.utility.noCorrentOfID_FACTORY");
	       }
	   } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	}
	private static String getCurrentId(String min_id,ContentDAO dao)
	{
		String sql="select prefix,suffix from id_factory  where sequence_name='U02.U0200'";
		String currentid="";
		RowSet rs =null;
		try {
			 rs=dao.search(sql);
			if(rs.next())
			{
				String prefix=rs.getString("prefix");
			    String suffix=rs.getString("suffix");
			    if (prefix == null)
			    {
			        prefix = "";
			    }
			    if (suffix == null)
			    {
			        suffix = "";
			    }
			    if(prefix.length()>0) {
                    currentid=min_id.substring(prefix.length());
                }
			    if(suffix.length()>0) {
                    currentid=currentid.substring(0,currentid.length()-suffix.length());
                }
			    int id=Integer.parseInt(currentid);
			    currentid=Integer.toString(id);			    
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return currentid;
	}
	/**
	 * 判断单位是否存在
	 * @param unitName
	 * @param conn
	 * @return
	 */
	public String getUnitcode(String unitName, Connection conn)
	{
		String unitcode="";
		ContentDAO dao=new ContentDAO(conn);
		RowSet recset=null;
		try
		{
			//
			recset=dao.search("select unitcode from tt_organization where unitname='"+unitName.trim()+"'");
			if(recset.next()) {
                unitcode=recset.getString(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		finally{
			try
			 {
				 if(recset!=null) {
                     recset.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return unitcode;
	}
	/**
	 * 判断子单位是否存在
	 * @param unitName
	 * @param conn
	 * @return
	 */
	public String getUnitcode(String unitName, Connection conn,String selfunitcode)
	{
		String unitcode="";
		ContentDAO dao=new ContentDAO(conn);
		RowSet recset=null;
		try
		{
			//
			recset=dao.search("select unitcode from tt_organization where unitname='"+unitName.trim()+"' and unitcode like '"+selfunitcode+"%' and unitcode!='"+selfunitcode+"%'");
			if(recset.next()) {
                unitcode=recset.getString(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		finally{
			try
			 {
				 if(recset!=null) {
                     recset.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return unitcode;
	}
	/**
	 * 单位层级
	 * @param conn
	 * @param grad
	 * @return
	 */
	public HashMap getUnitCodeGrad(Connection conn,String grad)
	{
		String sql="select unitcode,unitname,parentid,grade from tt_organization order by unitcode";
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		if(grad==null||grad.length()<=0|| "0".equals(grad)) {
            grad="3";//xgq修改grad由1为3默认为三级
        }
		HashMap map=new HashMap();
		try {
			rs=dao.search(sql);
			ArrayList list=new ArrayList();
			while(rs.next())
			{
				LazyDynaBean bean=new LazyDynaBean();
				String unitcode=rs.getString("unitcode")!=null&&rs.getString("unitcode").length()>0?rs.getString("unitcode"):"";
				String parentid=rs.getString("parentid")!=null&&rs.getString("parentid").length()>0?rs.getString("parentid"):"";
				bean.set("unitcode", unitcode);
				bean.set("unitname", rs.getString("unitname"));
				bean.set("parentid", parentid);
				bean.set("grademess", rs.getString("unitname"));
				bean.set("grade",String.valueOf(rs.getInt("grade")));
				if(unitcode.equals(parentid)) {
                    bean.set("state","1");
                } else {
                    bean.set("state","0");
                }
				map.put(rs.getString("unitcode"), bean);
				list.add(rs.getString("unitcode"));
			}
			
			int g=Integer.parseInt(grad);
			for(int l=0;l<list.size();l++)
			{
				String unitcode=(String)list.get(l);					
				LazyDynaBean bean=(LazyDynaBean)map.get(unitcode);
				String state=(String)bean.get("state");
				if("1".equals(state)) {
                    continue;
                }
				//二级单位与三级单位分开g==0或者1默认为1 当前bean是2级就把三级单位名为空,如果是三级把2级部门名求出来
			
				if(bean.get("grade")!=null&& "2".equals(bean.get("grade"))) {
                    bean.set("grademessx","");
                }
				if(bean.get("grade")!=null&& "3".equals(bean.get("grade"))){
					String parentid = (String)bean.get("parentid");
					LazyDynaBean bean2=(LazyDynaBean)map.get(parentid);
					String grademessx = (String)bean2.get("unitname");
					bean.set("grademessx", grademessx);
				}
			//	String grademess=getUnitMess(map,g,unitcode);
				//bean.set("grademess", grademess);
				map.put(unitcode, bean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return map;
	}
	private String getUnitMess(HashMap map,int g,String unitcode)
	{
		LazyDynaBean bean=(LazyDynaBean)map.get(unitcode);
		String parentid=(String)bean.get("parentid");
		String gradmess=(String)bean.get("gradmess");
		for(int i=1;i<g;i++)
		{
			LazyDynaBean parentbean=(LazyDynaBean)map.get(parentid);
			String parentcode=(String)parentbean.get("unitcode");
			String parent_parentid=(String)parentbean.get("parentid");
			String unitname=(String)parentbean.get("unitname");			
			gradmess=unitname+"/"+gradmess;
			if(parentcode.equals(parentid)&&parent_parentid.equals(parentcode))
			{
				bean.set("state","1");
				break;
			}else
			{
				parentid=parent_parentid;
				bean.set("state","0");
			}
		}
        return gradmess;
	}
	/**
	 * 
	 * @param conn
	 * @param id
	 * @param age
	 * @param sex 男1女2
	 * @return
	 */
	public boolean getCheckAgeLegal(Connection conn,String id,String report_id,Date age,String sex)
	{
		boolean isCorrect = true;
		if(!"U02_3".equalsIgnoreCase(report_id)) {
            return true;
        }
		String sql="select Bos_date  from tt_cycle where id='"+id+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try {
			Date bos_date=null;
			 rs=dao.search(sql);
			if(rs.next()) {
                bos_date=rs.getDate("Bos_date");
            }
			double diff=0;
			if(age!=null&&bos_date!=null)
			{
				diff=DateUtils.yearDiff(age, bos_date);
				if("1".equals(sex))
				{
					if(diff<55) {
                        isCorrect=false;
                    }
				}
				else if("2".equals(sex))
				{
					if(diff<45) {
                        isCorrect=false;
                    }
				}	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return isCorrect;
	}
	/**
	 * 获得单位父亲级
	 * @param conn
	 * @param grad
	 * @return
	 */
	public String getUnitCodeParentid(String liststr ,Connection conn,String unitcodeid)
	{
		ArrayList alist = new ArrayList();
		String sql="select unitcode,parentid from tt_organization order by unitcode";
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		HashMap map=new HashMap();
		try {
			rs=dao.search(sql);
			ArrayList list=new ArrayList();
			while(rs.next())
			{
				LazyDynaBean bean=new LazyDynaBean();
				String unitcode=rs.getString("unitcode")!=null&&rs.getString("unitcode").length()>0?rs.getString("unitcode"):"";
				String parentid=rs.getString("parentid")!=null&&rs.getString("parentid").length()>0?rs.getString("parentid"):"";
				bean.set("unitcode", unitcode);
				bean.set("parentid", parentid);
				//bean.set("grademess", rs.getString("unitname"));
				if(unitcode.equals(parentid)) {
                    bean.set("state","1");
                } else {
                    bean.set("state","0");
                }
				map.put(rs.getString("unitcode"), bean);
				list.add(rs.getString("unitcode"));
			}
		
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean=(LazyDynaBean)map.get(unitcodeid);
				if(bean==null||bean.get("parentid")==null) {
                    continue;
                }
				String parentcode =(String) bean.get("parentid");
				unitcodeid = parentcode;
				LazyDynaBean parentbean=(LazyDynaBean)map.get(parentcode);
				if("".equals(liststr)) {
                    liststr+=parentcode+",";
                }
				String liststrs [] =liststr.split(",");
				boolean flag = false;
				if("1".equals(parentbean.get("state"))){
					for(int j =0;j<liststrs.length;j++){
					if(liststrs[j].equals(parentcode)){//消除重复
						flag =true;
					}
					}
					if(!flag){
						liststr+=parentcode+",";
					}
					flag =false;
					break;
				}
				for(int j =0;j<liststrs.length;j++){
					if(liststrs[j].equals(parentcode)){
						flag =true;
						
					}
					}
					if(!flag){
						liststr+=parentcode+",";
					}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return liststr;
	}
	public boolean isExistData(Connection conn,String u0200id,String unitcode,String id,String escope){
		boolean error =false;
		RowSet rs=null;
//		int length =u0200id.length();
//		if(length<8){
//			int i = 8-length;
//			for(int j=0;j<i;j++){
//				u0200id ="0"+u0200id;
//			}
//		}
		ContentDAO dao=new ContentDAO(conn);
		String sql = " select * from U02 where u0200='"+u0200id+"'  and id="+Integer.parseInt(id)+" and escope='"+escope+"'";
		try {
			rs = dao.search(sql);
			if(rs.next()) {
                error=true;
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return error;
	}
	public String isExistData2(Connection conn,String oldid,String id,String oldstr,String escope,RecordVo cycle_vo,HashMap u0207Map){
		String U0207 ="-1";
		if("u0200".equalsIgnoreCase(oldstr)){
		boolean flag =	isExistDataOther( conn, oldid, id, oldstr, escope);
		if(flag){
			U0207 = getPersonState( conn, oldid, id, oldstr, escope);
			return U0207;
		}
		
		}
		try
		{
			if(u0207Map==null) {
                U0207="-3";
            }
	/*	ContentDAO dao = new ContentDAO(conn);
		RowSet rs=null;	
		int cycle_id=0;
		String kmethod = cycle_vo.getString("kmethod");
		Date bos_date=new Date();
		bos_date = cycle_vo.getDate("bos_date");
		if(bos_date!=null){	
			
		if(kmethod!=null&&kmethod.equals("0")){
			String date_str=DateUtils.format(bos_date, "yyyy-MM-dd");
			String sqlstr="select id from tt_cycle  where bos_date<"+Sql_switcher.dateValue(date_str)+" and  kmethod=0 order by bos_date asc";
			rs=dao.search(sqlstr);			  
				if(rs.next())
				{
					cycle_id=rs.getInt("id");
				}else{
					U0207 ="-3";
					return U0207;
				}
				*/
			if(oldid!=null&&oldid.trim().length()>0&&u0207Map!=null&&u0207Map.get(oldid)!=null)
			{
				if("3".equals((String)u0207Map.get(oldid))) {
                    U0207="2";
                } else {
                    U0207=(String)u0207Map.get(oldid);
                }
			}
			/*
		if(cycle_id!=0){
			String sql = "select U0207,"+oldstr+" from u02 where id="+cycle_id+" and "+oldstr+"='"+oldid+"' and escope='"+escope+"'";	
			rs = dao.search(sql);
			if(rs.next()){
				U0207 = rs.getString("U0207");
				if(U0207.equals("3"))
					U0207="2";
			}else{
				//U0207 ="-2";
			}
			}
		*/
		
		
		
		//	}
		//	}
		} catch (Exception  e) {
			e.printStackTrace();
		}
		
		return U0207;
	}
	public boolean isExistDataOther(Connection conn,String oldid,String id,String oldstr,String escope){
		RowSet rs=null;
		boolean flag=false;
		ContentDAO dao = new ContentDAO(conn);
			String sql = "select U0207,"+oldstr+" from u02 where id="+id+" and "+oldstr+"='"+oldid+"' and escope='"+escope+"'";	
			try{
			rs = dao.search(sql);
			if(rs.next()){
				flag = true;
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	public String getPersonState(Connection conn,String oldid,String id,String oldstr,String escope){
		RowSet rs=null;
		String u0207="-1";
		ContentDAO dao = new ContentDAO(conn);
			String sql = "select U0207,"+oldstr+" from u02 where id="+id+" and "+oldstr+"='"+oldid+"' and escope='"+escope+"'";	
			try{
			rs = dao.search(sql);
			if(rs.next()){
				u0207 = rs.getString("U0207");
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return u0207;
	}
	public boolean isExistData3(Connection conn,String oldid,String id,String oldstr,String escope){
		boolean flag =false;
		RowSet rs=null;
	
		ContentDAO dao = new ContentDAO(conn);
	
				try {
					
					String sql = "select u0200,U0207,"+oldstr+" from u02 where id="+id+" and "+oldstr+"='"+oldid+"' and escope='"+escope+"'";	
					rs = dao.search(sql);
					if(rs.next()){
						flag =true;
					}
		
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	/**
	 * 判断id自动增加1
	 */
	public String getId(Connection conn,String escope,String oldstr,String idstr){
		//String idstr ="";
		RowSet rs=null;
		String tag="";
		if("1".equals(escope)) {
            tag="L";
        } else if("2".equals(escope)) {
            tag="T";
        } else if("3".equals(escope)) {
            tag="N";
        } else if("4".equals(escope)) {
            tag="Y";
        }
		String sql = " select "+oldstr+"  from u02 where escope='"+escope+"'  order by "+oldstr+" desc";
		ContentDAO dao = new ContentDAO(conn);
		try {
			rs = dao.search(sql);
			if(rs.next()){
				String newidstr = rs.getString(oldstr);
				if("".equals(idstr)){
					idstr = newidstr;
				}else{
					String copyidstr=idstr.substring(1);
					String copyidstr2=newidstr.substring(1);
					if(Integer.parseInt(copyidstr)<Integer.parseInt(copyidstr2)) {
                        idstr=	newidstr;
                    }
				}
				String copyidstr=idstr.substring(1);
				String id = Integer.parseInt(copyidstr)+1+"";
				
				while(id.length()<5){
					id="0"+id;
				}
				idstr = tag+id;
			}else{
				if("".equals(idstr)) {
                    idstr=tag+"0000"+1;
                }
				String copyidstr=idstr.substring(1);
				String id = Integer.parseInt(copyidstr)+1+"";
				while(id.length()<5){
					id="0"+id;
				}
				idstr = tag+id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rs!=null) {
                     rs.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return idstr;
	}
	/**
	 * 得到U01动态显示的列返回的fieldlist
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU01FieldList(Connection conn) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U01",Constant.USED_FIELD_SET);
    	ArrayList list = new ArrayList();
    	String fields =",unitcode,id,u0101,u0103,t3_desc,t5_desc,";
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();
            
			String itemid=itemfield.getItemid();
			if(fields.indexOf(","+itemid+",")==-1)
			{
				list.add(itemfield);
			}
		}
		return list;
	}
	public ArrayList getU02QueryList(ArrayList fieldlist ){
		ArrayList list=new ArrayList();
		for(int i=0;i<fieldlist.size();i++) 
		  {
		 		FieldItem field=(FieldItem)fieldlist.get(i); 
		 		if("1".equals(field.getState()))
				{
							
		 			field.setVisible(true);
				}else
				{
					field.setVisible(false);
				}
//		 		if(field.getItemtype().equalsIgnoreCase("N")){
//		 			continue;
//		 		}
//		 		if(field.getItemid().equalsIgnoreCase("u0207")||field.getItemid().equalsIgnoreCase("u0209")||field.getItemid().equalsIgnoreCase("u0239"))
//		 		continue;
		 		if("u0239".equalsIgnoreCase(field.getItemid())|| "u0243".equalsIgnoreCase(field.getItemid())) {
                    continue;
                }
		 		
		 		if("U0200".equalsIgnoreCase(field.getItemid())) {
                    continue;
                }
		 		list.add(field.clone());
		  }
		return list;
	}
	public int getSelfUnitFlag(Connection conn,String id,String unitcode,String report_id){
		int flag=0;
		RecordVo vo = new RecordVo("tt_calculation_ctrl");
		vo.setString("unitcode",unitcode);
		vo.setString("report_id",report_id);
		vo.setInt("id",Integer.parseInt(id));
		ContentDAO dao=new ContentDAO(conn);
		try {
			dao.findByPrimaryKey(vo);
			flag = vo.getInt("flag");
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flag;
	}
 
}
