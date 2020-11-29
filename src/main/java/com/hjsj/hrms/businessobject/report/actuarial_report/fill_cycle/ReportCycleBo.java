package com.hjsj.hrms.businessobject.report.actuarial_report.fill_cycle;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
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
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReportCycleBo {

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
	 * 得到U04不同人员状态返回的fieldlist
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU04FieldList(String Report_id,boolean isChangeLine) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U04",Constant.USED_FIELD_SET);

    	String fields="";    	

    	ArrayList list=new ArrayList();	
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();

			String itemid=itemfield.getItemid();
			if("U0401".equalsIgnoreCase(itemid)) {
                continue;
            }
			if("U0403".equalsIgnoreCase(itemid))//年度
            {
                continue;
            }
			
				list.add(itemfield);
			
		}
		FieldItem itemfield=DataDictionary.getFieldItem("U0401");
		list.add(0,itemfield);
		return list;
	}
	/**
	 * 得到U03不同人员状态返回的fieldlist
	 * @param conn
	 * @param Report_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getU03FieldList(String Report_id,boolean isChangeLine) throws GeneralException
	{
		ArrayList fieldlist = DataDictionary.getFieldList("U03",Constant.USED_FIELD_SET);

    	String fields="";    	

    	ArrayList list=new ArrayList();	
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem itemfieldF=(FieldItem)fieldlist.get(i);	
			FieldItem itemfield=(FieldItem)itemfieldF.clone();

			String itemid=itemfield.getItemid();
			if("U0301".equalsIgnoreCase(itemid)) {
                continue;
            }
			if("U0303".equalsIgnoreCase(itemid))//年度
            {
                continue;
            }
			
				list.add(itemfield);
			
		}
		FieldItem itemfield=DataDictionary.getFieldItem("U0301");
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
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			RowSet rowSet=dao.search(sql);
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
			int year=DateUtils.getYear(bos_date);
			year--;
			sqlstr="select id from tt_cycle where Theyear='"+year+"' and kmethod=0";
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
			int year=DateUtils.getYear(bos_date);
			year--;
			sqlstr="select * from tt_cycle where Theyear='"+year+"' and kmethod=0";			
			try {
				rs=dao.search(sqlstr);			  
				if(rs.next()) {
                    isIntroduce=true;
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			sql.append(" from U02 where id='"+cycle_id+"' and escope='"+escope+"' and unitcode='"+unitcode+"'");
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
		try {
			RowSet rs=dao.search(sql);
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
			recset=dao.search("select unitcode from tt_organization where unitname='"+unitName.trim()+"'");
			if(recset.next()) {
                unitcode=recset.getString(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return unitcode;
	}
	//符合条件自动更新U05表中的内容
	public boolean isupdateU05(String id,String method,Connection conn,UserView view){
		boolean flag = true;
		String sql = "select distinct(report_id) from tt_calculation_ctrl where id= "+id+"and flag=1";
		ContentDAO dao=new ContentDAO(conn);
		String reportidstr="U02_1,U02_2,U02_3,U02_4";
		String reportidstr2[]= reportidstr.split(",");
		RowSet recset=null;
		RecordVo vo=new RecordVo("tt_cycle");
		String year="";
		String unitcode="";
    	vo.setInt("id",Integer.parseInt(id));
    	String resultstr="";
		try {
			recset=dao.search(sql);
			while(recset.next()){
				resultstr+=recset.getString(1)+",";
		
			}
			for(int i=0;i<reportidstr2.length;i++){
				if(resultstr.indexOf(reportidstr2[i])==-1){
					flag=false;
				break;
				}
			}
			if(flag){
			try {
				vo=dao.findByPrimaryKey(vo);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
			 year = vo.getString("theyear");
			
			 
					ActuarialReportBo ab=new ActuarialReportBo(conn,view);
					 sql = "select distinct(unitcode) from tt_calculation_ctrl where id="+id+" and ( report_id='U02_1' or report_id='U02_2'or report_id='U02_3' or report_id='U02_4') ";
					 recset=dao.search(sql);
					 while(recset.next()){
						 unitcode =recset.getString(1);
						 ab.saveU05Values(unitcode,id,year);
							}
					
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flag;
	}
}
