package com.hjsj.hrms.businessobject.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ReportPigeonholeBo {
	private Connection conn=null;
	private TgridBo tgridBo=null;
	public ReportPigeonholeBo(Connection con)
	{
		this.conn=con;
		this.tgridBo=new TgridBo(conn);
	}
	
	
	/**
	 * 
	 * @param selectUnitType  1:全部  2：部分
	 * @param userView
	 * @param unitIDs  
	 * @return
	 */
	public ArrayList getUnitIds(String  selectUnitType,UserView userView,String unitIDs)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			if("1".equals(selectUnitType))
			{
				TTorganization ttorganization=new TTorganization(this.conn);
				RecordVo selfVo=ttorganization.getSelfUnit(userView.getUserName());
				//recset=dao.search("select * from tt_organization where parentid like '"+selfVo.getString("unitcode")+"%' or unitcode='"+selfVo.getString("unitcode")+"'");
				//liuy 2015-4-10 8092：报表归档模块批量按月归档无效  begin
				//recset=dao.search("select * from tt_organization where parentid like '"+selfVo.getString("unitcode")+"%' "+ext_sql+" ");
				recset=dao.search("select * from tt_organization where parentid like '"+selfVo.getString("unitcode")+"%' or unitcode='"+selfVo.getString("unitcode")+"'"+ext_sql+" ");
				//liuy 2015-4-10 end
			}
			else
			{
				StringBuffer whl=new StringBuffer("");
				String[] temps=unitIDs.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0) {
                        whl.append(",'"+temps[i]+"'");
                    }
				}
				recset=dao.search("select * from tt_organization where unitcode in ( "+whl.substring(1)+" ) "+ext_sql+"");
			}
			LazyDynaBean abean=null;
			while(recset.next())
			{
				abean=new LazyDynaBean();
				abean.set("unitcode",recset.getString("unitcode"));
				abean.set("unitname",recset.getString("unitname"));
				list.add(abean);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return list;
	}
	
	
	
	/**
	 * 设置报表归档状态
	 * @param sortIDs
	 * @param operate
	 * @param narch
	 * @return
	 */
	public boolean setReportNarch(String sortIDs,String operate,String narch)
	{
		boolean flag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			ArrayList list=getUpdateReportTypeList(sortIDs,operate,narch);
			DbWizard dbWizard=new DbWizard(this.conn);
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				String tabid=(String)abean.get("tabid");
				if(dbWizard.isExistTable("ta_"+tabid,false))
				{
					dao.delete("delete from ta_"+tabid,new ArrayList());	
				}
			}
			if("1".equals(operate)) {
                dao.update("update tname set narch="+narch+" where tsortid in ("+sortIDs+")");
            } else if("2".equals(operate)) {
                dao.update("update tname set narch="+narch+" where tabid in ("+sortIDs+")");
            }
		}
		catch(Exception e)
		{
			flag=false;
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 得到 归档的报表类型
	 * @param selectids
	 * @param operate
	 * @return
	 */
	public ArrayList getReportPigonholeType(String selectids,String operate)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		TnameBo tnamebo  = new TnameBo(this.conn);
		HashMap scopeMap = tnamebo.getScopeMap();
		java.util.Iterator it = scopeMap.entrySet().iterator();
		String tabids = "";
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String keys = (String) entry.getKey();
			tabids+= keys+",";
			
		}
		if(tabids.length()>0) {
            tabids=tabids.substring(0,tabids.length()-1);
        }
		String sqlwhere = "";
		if(tabids.length()>0) {
            sqlwhere=" and tabid not in("+tabids+")";
        }
		try
		{
			String sql="";
			if("1".equals(operate)) {
                sql="select  distinct narch from  tname where  tsortid in ("+selectids+")"+sqlwhere;
            } else if("2".equals(operate)) {
                sql="select  distinct narch from  tname where    tabid in ("+selectids+")"+sqlwhere;
            }
			recset=dao.search(sql);
		    while(recset.next())
		    {
		    	if(recset.getString(1)==null) {
                    list.add("0");
                } else {
                    list.add(recset.getString(1));
                }
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return list;
	}
	

	/**
	 * 得到哪些表更改了归档类型
	 * @param sortIDs
	 * @param operate  1:表类  2：单表
	 * @param narch    归档类型
	 * @return
	 */
	public ArrayList getUpdateReportTypeList(String sortIDs,String operate,String narch)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			String sql="";
			if("1".equals(operate)) {
                sql="select tabid,name,narch from tname where  narch<>"+narch+" and narch is not null and tsortid in ("+sortIDs+")";
            } else if("2".equals(operate)) {
                sql="select tabid,name,narch from tname where  narch<>"+narch+" and narch is not null and  tabid in ("+sortIDs+")";
            }
			
			recset=dao.search(sql);
			LazyDynaBean abean=null;
			while(recset.next())
			{
				abean=new LazyDynaBean();
				abean.set("tabid",recset.getString("tabid"));
				abean.set("name",recset.getString("name"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return list;
	}
	
	
	
	/**
	 * 得到表类下所有的报表集合
	 * @param sortIDs
	 * @param operate  1:表类  2：单表
	 * @return
	 */
	public ArrayList getReportList(String sortIDs,String operate)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			StringBuffer sql = new StringBuffer();
			if("1".equals(operate)){
				sql.append("select tabid,name,narch from tname where tsortid in ("+sortIDs+")");
				if(tabids.length()>0){
					sql.append(" and tabid not in("+tabids+") ");
				}
				recset=dao.search(sql.toString());
			}
			else if("2".equals(operate)){
				sql.append("select tabid,name from tname where tabid in ("+sortIDs+")");
				if(tabids.length()>0) {
                    sql.append(" and tabid not in("+tabids+") ");
                }
				recset=dao.search(sql.toString());
			
			}
			
			LazyDynaBean abean=null;
			while(recset.next())
			{
				abean=new LazyDynaBean();
				abean.set("tabid",recset.getString("tabid"));
				abean.set("name",recset.getString("name"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return list;
	}
	
	public HashMap getCount(String tabid,String unitIDs_whl,String year)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select max(countid),unitcode from ta_"+tabid+"  where unitcode in ("+unitIDs_whl+")  and  yearid="+year+" group by unitcode");
			while(recset.next())
			{
				int countid=recset.getInt(1)+1;
				map.put(recset.getString("unitcode"),String.valueOf(countid));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return map;
	}
	
	
	/**
	 * 报表归档
	 * @param resultList
	 * @param paramValue
	 * @param tabid
	 * @param rows
	 * @param cols
	 * @param userid
	 * @param userName
	 * @param unitcode
	 * @param year  年份
	 * @param count 
	 * @param selfType 本身的报表类型
	 * @param reportTYpe 保存的报表类型
	 * @return  1:归档成功 2：归档不成功
	 */
	public String reportPigeonholeTrans(UserView userView,String operate,String selectedIDs,String selectUnitType,String unitIDs,String year,String count,String reportType,String week)
	{
		String info="1";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		String tabName="";
		try
		{
			ArrayList unitList=getUnitIds(selectUnitType,userView,unitIDs);
			ArrayList tabList=getReportList(selectedIDs,operate);
			StringBuffer unitids=new StringBuffer("");
			for(int i=0;i<unitList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)unitList.get(i);
				unitids.append(",'"+(String)abean.get("unitcode")+"'");
			}
			String unitIDs_whl=unitids.substring(1);
			DbWizard dbWizard=new DbWizard(this.conn);
			String reports = getOtherTableid(userView);
			for(int i=0;i<tabList.size();i++)
			{
				LazyDynaBean tabBean=(LazyDynaBean)tabList.get(i);
				String tabid=(String)tabBean.get("tabid");
				if(reports.indexOf(","+tabid+",")!=-1) {
                    continue;
                }
				
				recset=dao.search("select name from tname where tabid="+tabid);
				if(recset.next()) {
                    tabName=recset.getString(1);
                }
				
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid)){
					info = "2";
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
				}
					
				if(dbWizard.isExistTable("tt_"+tabid,false))
				{
					TnameBo tnameBo=new TnameBo(this.conn,tabid);
					execute_Ta_table(tabid,tnameBo);	//判断是否存在统计结果归档表，如果没有则产生一个
					compareTaTable(tnameBo,tabid);		//比较统计结果表与当前的报表格式是否一致,如果不一致，则动态添加
					PubFunc.closeResource(recset);
					recset=dao.search("select * from tt_"+tabid+" where 1=2");
					ResultSetMetaData data=recset.getMetaData();
					int columncount=data.getColumnCount()-2;
					//产生次数
					dealwithData(reportType,year,count,unitIDs_whl,tabid,week);
					//插入数据
					if(!"1".equals(reportType))
					{
						
						StringBuffer sql_1=new StringBuffer("unitcode,secid,yearid,countid,row_item");
						StringBuffer sql_2=new StringBuffer("unitcode,secid,"+year+",");		
						
						if("2".equals(reportType)) {
                            sql_2.append(1);
                        } else {
                            sql_2.append(count);
                        }
						sql_2.append(",secid");
						
						
						for(int j=0;j<columncount;j++)
						{
							RecordVo a_vo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
							String fieldname="C"+(j+1);
							if(a_vo.getString("archive_item")!=null&&!"".equals(a_vo.getString("archive_item"))&&!" ".equals(a_vo.getString("archive_item"))) {
                                fieldname=a_vo.getString("archive_item");
                            }
							sql_1.append(","+fieldname);
							sql_2.append(",C"+(j+1));
						}	
						
						if("6".equals(reportType))
						{
							sql_1.append(",weekid");
							sql_2.append(","+week);
						}
						
						
						String sql="insert into ta_"+tabid+" ( "+sql_1.toString()+" )  select "+sql_2.toString()+" from tt_"+tabid+" where unitcode in ("+unitIDs_whl+")";
						dao.insert(sql,new ArrayList());
						ArrayList colList=tnameBo.getColInfoBGrid();
						for(int n=0;n<colList.size();n++)
						{
							RecordVo vo=(RecordVo)colList.get(n);
							if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item")))
							{
								dao.update("update ta_"+tabid+" set row_item='"+vo.getString("archive_item")+"' where row_item='"+(n+1)+"'");
							}
						}
						
					}
					else
					{
						HashMap unitCountMap=getCount(tabid,unitIDs_whl,year);
						for(int n=0;n<unitList.size();n++)
						{
							LazyDynaBean abean=(LazyDynaBean)unitList.get(n);
							String unitcode=(String)abean.get("unitcode");
							
							StringBuffer sql_1=new StringBuffer("unitcode,secid,yearid,countid,row_item");
							StringBuffer sql_2=new StringBuffer("unitcode,secid,"+year+",");		
							
							if(unitCountMap.get(unitcode)!=null) {
                                sql_2.append((String)unitCountMap.get(unitcode));
                            } else {
                                sql_2.append("1");
                            }
							
							sql_2.append(",secid");
							for(int j=0;j<columncount;j++)
							{
								RecordVo a_vo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
								String fieldname="C"+(j+1);
								if(a_vo.getString("archive_item")!=null&&!"".equals(a_vo.getString("archive_item"))&&!" ".equals(a_vo.getString("archive_item"))) {
                                    fieldname=a_vo.getString("archive_item");
                                }
								sql_1.append(","+fieldname);
								sql_2.append(",C"+(j+1));
							}	
							
							if("6".equals(reportType))
							{
								sql_1.append(",weekid");
								sql_2.append(","+week);
							}
							String sql="insert into ta_"+tabid+" ( "+sql_1.toString()+" )  select "+sql_2.toString()+" from tt_"+tabid+" where unitcode='"+unitcode+"'";
							dao.insert(sql,new ArrayList());
						}
						ArrayList colList=tnameBo.getColInfoBGrid();
						for(int n=0;n<colList.size();n++)
						{
							RecordVo vo=(RecordVo)colList.get(n);
							if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item")))
							{
								dao.update("update ta_"+tabid+" set row_item='"+vo.getString("archive_item")+"' where row_item='"+(n+1)+"'");
							}
						}
						
					}
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if("2".equals(info)){
				info="2~@"+SafeCode.encode(tabName)+ResourceFactory.getProperty("report.noResource.info")+"!";
			}else{			
				info="2~@"+SafeCode.encode(tabName);
			}
		}finally {
			PubFunc.closeResource(recset);
		}
	
		return info;
	}
	public ArrayList getAuto_units(String tabid){
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			
			String whl=" reporttypes is not null and report not like '%,"+tabid+",%'";
			recset=dao.search("select * from tt_organization where  "+whl.substring(1)+ext_sql+"");
			LazyDynaBean abean=null;
			while(recset.next())
			{
				abean=new LazyDynaBean();
				abean.set("unitcode",recset.getString("unitcode"));
				abean.set("unitname",recset.getString("unitname"));
				list.add(abean);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return list;
		
	}
	public String auto_archive(String tabid,String reportType,String week,String month,String year){
		String info="1";
		String count="";
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			
			String tabName="";
			try
			{
				if("3".equalsIgnoreCase(reportType)){
					if(Integer.parseInt(month)<=6){
						count="1";
						
					}else{
						count="2";
					}
				}
				if("4".equalsIgnoreCase(reportType)){
					if(Integer.parseInt(month)<=3){
						count="1";
						
					}else{
						if(Integer.parseInt(month)<=6){
							count="2";
						}else{
							if(Integer.parseInt(month)<=9){
								count="3";
							}else{
								count="4";
							}
						}
					}
				}
				if("5".equalsIgnoreCase(reportType)|| "6".equalsIgnoreCase(reportType)){
					count=month;
				}
				ArrayList unitList=getAuto_units(tabid);
				StringBuffer unitids=new StringBuffer("");
				for(int i=0;i<unitList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)unitList.get(i);
					unitids.append(",'"+(String)abean.get("unitcode")+"'");
				}
				String unitIDs_whl=unitids.substring(1);
				DbWizard dbWizard=new DbWizard(this.conn);
				recset=dao.search("select name from tname where tabid="+tabid);
					if(recset.next()) {
                        tabName=recset.getString(1);
                    }
					if(dbWizard.isExistTable("tt_"+tabid,false))
					{
						TnameBo tnameBo=new TnameBo(this.conn,tabid);
						execute_Ta_table(tabid,tnameBo);	//判断是否存在统计结果归档表，如果没有则产生一个
						compareTaTable(tnameBo,tabid);		//比较统计结果表与当前的报表格式是否一致,如果不一致，则动态添加
						
						recset=dao.search("select * from tt_"+tabid+" where 1=2");
						ResultSetMetaData data=recset.getMetaData();
						int columncount=data.getColumnCount()-2;
						//产生次数
						dealwithData(reportType,year,count,unitIDs_whl,tabid,week);
						//插入数据
						if(!"1".equals(reportType))
						{
							
							StringBuffer sql_1=new StringBuffer("unitcode,secid,yearid,countid,row_item");
							StringBuffer sql_2=new StringBuffer("unitcode,secid,"+year+",");		
							
							if("2".equals(reportType)) {
                                sql_2.append(1);
                            } else {
                                sql_2.append(count);
                            }
							sql_2.append(",secid");
							
							
							for(int j=0;j<columncount;j++)
							{
								RecordVo a_vo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
								String fieldname="C"+(j+1);
								if(a_vo.getString("archive_item")!=null&&!"".equals(a_vo.getString("archive_item"))&&!" ".equals(a_vo.getString("archive_item"))) {
                                    fieldname=a_vo.getString("archive_item");
                                }
								sql_1.append(","+fieldname);
								sql_2.append(",C"+(j+1));
							}	
							
							if("6".equals(reportType))
							{
								sql_1.append(",weekid");
								sql_2.append(","+week);
							}
							
							
							String sql="insert into ta_"+tabid+" ( "+sql_1.toString()+" )  select "+sql_2.toString()+" from tt_"+tabid+" where unitcode in ("+unitIDs_whl+")";
							dao.insert(sql,new ArrayList());
							ArrayList colList=tnameBo.getColInfoBGrid();
							for(int n=0;n<colList.size();n++)
							{
								RecordVo vo=(RecordVo)colList.get(n);
								if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item")))
								{
									dao.update("update ta_"+tabid+" set row_item='"+vo.getString("archive_item")+"' where row_item='"+(n+1)+"'");
								}
							}
							
						}
						else
						{
							HashMap unitCountMap=getCount(tabid,unitIDs_whl,year);
							for(int n=0;n<unitList.size();n++)
							{
								LazyDynaBean abean=(LazyDynaBean)unitList.get(n);
								String unitcode=(String)abean.get("unitcode");
								
								StringBuffer sql_1=new StringBuffer("unitcode,secid,yearid,countid,row_item");
								StringBuffer sql_2=new StringBuffer("unitcode,secid,"+year+",");		
								
								if(unitCountMap.get(unitcode)!=null) {
                                    sql_2.append((String)unitCountMap.get(unitcode));
                                } else {
                                    sql_2.append("1");
                                }
								
								sql_2.append(",secid");
								for(int j=0;j<columncount;j++)
								{
									RecordVo a_vo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
									String fieldname="C"+(j+1);
									if(a_vo.getString("archive_item")!=null&&!"".equals(a_vo.getString("archive_item"))&&!" ".equals(a_vo.getString("archive_item"))) {
                                        fieldname=a_vo.getString("archive_item");
                                    }
									sql_1.append(","+fieldname);
									sql_2.append(",C"+(j+1));
								}	
								
								if("6".equals(reportType))
								{
									sql_1.append(",weekid");
									sql_2.append(","+week);
								}
								String sql="insert into ta_"+tabid+" ( "+sql_1.toString()+" )  select "+sql_2.toString()+" from tt_"+tabid+" where unitcode='"+unitcode+"'";
								dao.insert(sql,new ArrayList());
							}
							ArrayList colList=tnameBo.getColInfoBGrid();
							for(int n=0;n<colList.size();n++)
							{
								RecordVo vo=(RecordVo)colList.get(n);
								if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item")))
								{
									dao.update("update ta_"+tabid+" set row_item='"+vo.getString("archive_item")+"' where row_item='"+(n+1)+"'");
								}
							}	
						}
					}		
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				info="2~@"+SafeCode.encode(tabName);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
		return info;
		
	}
	
	
//	如果需保存的数据已存在，则删除
	public void dealwithData(String reportType,String year,String count,String unitIDs_whl,String tabid,String week)
	{
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			if("2".equals(reportType)) {
                dao.delete("delete from ta_"+tabid+" where unitcode in("+unitIDs_whl+") and yearid="+year,new ArrayList());
            }
			if("3".equals(reportType)|| "4".equals(reportType)|| "5".equals(reportType)) {
                dao.delete("delete from ta_"+tabid+" where unitcode in("+unitIDs_whl+") and yearid="+year+" and countid="+count,new ArrayList());
            }
			if("6".equals(reportType)) {
                dao.delete("delete from ta_"+tabid+" where unitcode in("+unitIDs_whl+") and yearid="+year+" and countid="+count+" and weekid="+week,new ArrayList());
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
	}
	
	
	
	
//	比较统计结果表与当前的报表格式是否一致,如果不一致，则动态添加,删除
	public void compareTaTable(TnameBo tnameBo,String tabid)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from ta_"+tabid+" where 1=2");
			ResultSetMetaData data=recset.getMetaData();
			DbWizard dbWizard=new DbWizard(this.conn);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			Table table=new Table("ta_"+tabid);
			//动态添加
			int isAdd=0;
			for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		    {
		    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
		    	String fieldname="C"+(i+1);
		    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    fieldname=vo.getString("archive_item");
                }
				boolean isExistColumn=false;
		    	for(int a=0;a<data.getColumnCount();a++)
				{
					String columnName=((String)data.getColumnName(a+1)).toLowerCase();
					if(columnName.equals(fieldname.toLowerCase())|| "scopeid".equals(columnName)) {
                        isExistColumn=true;
                    }
					if(isExistColumn) {
                        break;
                    }
				}
				if(!isExistColumn)
				{
					Field obj=tgridBo.getField2(fieldname,fieldname,"N");
					table.addField(obj);
					isAdd++;
				}
			}
			if(isAdd>0)
			{
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel(table.getName());
			}
			
			RecordVo vo2=new RecordVo(table.getName());
			if(!vo2.hasAttribute("weekid"))
			{
				
				table=new Table("ta_"+tabid);
				Field obj=new Field("weekid","weekid");
				obj.setDatatype(DataType.INT);
				obj.setAlign("left");				
				table.addField(obj);
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel("ta_"+tabid);
			}
			
			
			//动态删除列
			Table table2=new Table("ta_"+tabid);
			int isdelete=0;
		    for(int a=0;a<data.getColumnCount();a++)
			{
					String columnName=((String)data.getColumnName(a+1)).toLowerCase();
					if(!"unitcode".equals(columnName)&&!"secid".equals(columnName)&&!"weekid".equals(columnName)&&!"yearid".equals(columnName)&&!"countid".equals(columnName)&&!"row_item".equals(columnName)&&!"scopeid".equals(columnName))
					{
						boolean isExistColumn=false;
						for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
					    {
					    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
					    	String fieldname="C"+(i+1);
					    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                                fieldname=vo.getString("archive_item").toLowerCase();
                            }
					    	if(columnName.equals(fieldname.toLowerCase())) {
                                isExistColumn=true;
                            }
					    	if(isExistColumn) {
                                break;
                            }
					    }
						if(!isExistColumn)
						{
							Field obj=tgridBo.getField2(columnName,columnName,"N");
							table2.addField(obj);
							isdelete++;
						}
					}
					
			}
		    if(data!=null) {
                data=null;
            }
			if(isdelete>0)	
			{
				dbWizard.dropColumns(table2);
				dbmodel.reloadTableModel(table2.getName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(recset);
		}
	}

	
//	 判断是否存在统计结果归档表，如果没有则产生一个
		public boolean execute_Ta_table(String tabid,TnameBo tnameBo)throws GeneralException
		{
			boolean flag=true;
			try
			{
				Table table=new Table("ta_"+tabid);
				DbWizard dbWizard=new DbWizard(this.conn);
				if(!dbWizard.isExistTable(table.getName(),false))
				{
					flag=false;
					ArrayList fieldList=getTa_TableFields(tnameBo);
					for(Iterator t=fieldList.iterator();t.hasNext();)
					{
						Field temp=(Field)t.next();
						table.addField(temp);
					}
					table.setCreatekey(false);	
					dbWizard.createTable(table);	
					DBMetaModel dbmodel=new DBMetaModel(this.conn);
					dbmodel.reloadTableModel(table.getName());
				
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return flag;
		}
		
		

		/**
		 * 得到统计结果归档表中列的集合
		 * 
		 * @param
		 * @param
		 * @param
		 * @return
		 */
		public ArrayList getTa_TableFields(TnameBo tnameBo)
		{
			ArrayList fieldsList=new ArrayList();	
			fieldsList.add(tgridBo.getField1("unitcode",ResourceFactory.getProperty("ttOrganization.unit.unitcode"),"DataType.STRING",30));
			Field temp21=new Field("secid",ResourceFactory.getProperty("ttOrganization.record.secid"));
			temp21.setDatatype(DataType.INT);
			temp21.setKeyable(true);			
			temp21.setVisible(false);			
			fieldsList.add(temp21);
			Field temp22=new Field("yearid",ResourceFactory.getProperty("edit_report.year"));
			temp22.setDatatype(DataType.INT);
			temp22.setKeyable(true);			
			temp22.setVisible(false);			
			fieldsList.add(temp22);
			Field temp23=new Field("countid",ResourceFactory.getProperty("hmuster.label.counts"));
			temp23.setDatatype(DataType.INT);
			temp23.setKeyable(true);			
			temp23.setVisible(false);			
			fieldsList.add(temp23);	
			
			Field temp33=new Field("weekid","weekid");
			temp33.setDatatype(DataType.INT);
			temp33.setKeyable(true);			
			temp33.setVisible(false);			
			fieldsList.add(temp33);	
			
			fieldsList.add(tgridBo.getField1("row_item",ResourceFactory.getProperty("reportspacecheck.rowOtherName"),"DataType.STRING",8));
			
		    for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		    {
		    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
		    	String fieldname="C"+(i+1);
		    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    fieldname=vo.getString("archive_item");
                }
		    	
				Field obj=tgridBo.getField2(fieldname,fieldname,"N");
				fieldsList.add(obj);
			}
			
		    return fieldsList;
		}
		public String  getOtherTableid(UserView userView){
			
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			RowSet recset=null;
			ContentDAO dao=new ContentDAO(this.conn);
			String reports =",";
		try {
			recset=dao.search("select t.* from tt_organization t,operuser o where o.unitcode=t.unitcode  and o.username='"+userView.getUserName()+"' "+ext_sql+"");
			while(recset.next()){
			reports+=Sql_switcher.readMemo(recset,"report");
			}
			reports+=",";
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}
		
		return reports;
		}
}
