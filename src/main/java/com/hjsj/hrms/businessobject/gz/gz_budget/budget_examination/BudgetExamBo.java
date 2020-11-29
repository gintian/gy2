package com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BudgetExamBo {
	private Connection conn = null;
	private String tab_id="";
	private UserView userView=null;
	private RecordVo gzBudgetTabVo=null;
	private HashMap sysOptionMap=new HashMap();  //系统项参数
	
	public BudgetExamBo(Connection _con)
	{
		this.conn=_con;
	}
	
	public BudgetExamBo(Connection _con,UserView _userView)
	{
		conn=_con;
		userView=_userView;
		BudgetSysBo bo=new BudgetSysBo(this.conn,this.userView);
		this.sysOptionMap=bo.getSysValueMap();
	}
	
	public BudgetExamBo(Connection _con,String tabid,UserView _userView)
	{
		conn=_con;
		tab_id=tabid;
		userView=_userView;
		init();
		
	}
	
	
	private void init()
	{
		// 获得系统项参数放入Map中 sysOptionMap=xxxxx
		BudgetSysBo bo=new BudgetSysBo(this.conn,this.userView);
		this.sysOptionMap=bo.getSysValueMap();
		if(this.tab_id!=null&&this.tab_id.length()>0)
			gzBudgetTabVo=getRecordVo("gz_budget_tab","tab_id",Integer.parseInt(tab_id)); 
		
		 
	}
	
	private RecordVo getRecordVo(String tabname,String primary_key,int key) 
	{
		RecordVo vo=new RecordVo(tabname); 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setInt(primary_key, key);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 
		}
		return vo;
	}
	
	
	
	/**
	 * 获得sql
	 * @param b0110
	 * @param budget_id
	 * @return
	 * @throws GeneralException
	 */
	public String getSql(String b0110,String budget_id,String tab_id,ArrayList fieldList)throws GeneralException
	{
		String sql="";
		if(gzBudgetTabVo!=null&&(gzBudgetTabVo.getInt("tab_type")==4||gzBudgetTabVo.getInt("tab_type")==3))  //计划表
		{
			boolean isTopUn=isTopUn(b0110); //是不是顶层节点
			if(isTopUn)
			{
				String tableName=createTotalDataTable(budget_id,tab_id,fieldList);
				sql="select * from "+tableName+" where budget_id="+budget_id+" and tab_id="+tab_id+" order by seq "; 
			}
			else
			{	boolean isBudget = isBudget(budget_id,b0110);	
				if(isBudget){
					String tableName = createBudgetData(budget_id,tab_id,b0110);
					sql="select * from "+tableName+" where budget_id="+budget_id+" and tab_id="+tab_id+" and B0110='"+b0110+"' order by seq  ";
				}else{
					String tableName = createTotalSummaryTable(budget_id,tab_id,fieldList,b0110);
					sql="select * from "+tableName+" where budget_id="+budget_id+" and tab_id="+tab_id+" and B0110='"+b0110+"' order by seq  ";
				}
			}
		
		}
			
		return sql;
	}
	/**
	 * 获得汇总sql
	 */
	public String getSummarySql(String b0110,String budget_id,String tab_id,ArrayList fieldList)throws GeneralException
	{
		String sql="";
		if(gzBudgetTabVo!=null&&(gzBudgetTabVo.getInt("tab_type")==4||gzBudgetTabVo.getInt("tab_type")==3))  //计划表
		{
			boolean isTopUn=isTopUn(b0110); //是不是顶层节点
			if(isTopUn)
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.exam.djdwwxhz")));
			}
			else
			{	boolean isBudget = isBudget(budget_id,b0110);	
				if(isBudget){
					String tableName = summary(budget_id,tab_id,fieldList,b0110);
					sql="select * from "+tableName+" where budget_id="+budget_id+" and tab_id="+tab_id+" and B0110='"+b0110+"' order by seq  ";
					
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.exam.feixsdwwxhz")));
				}
			}
		
		}
			
		return sql;
	}
	/**
	 * 判断预算表中是否有数，无则新增
	 * @param budget_id
	 * @param tab_id
	 * @param b0110
	 */
	public String  createBudgetData(String budget_id,String tab_id,String b0110)throws GeneralException	  
	{
		String tabname = getScTabname(tab_id);
		/*
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select count(*) from "+tabname+" where budget_id="+budget_id+" and tab_id="+tab_id+" and B0110='"+b0110+"'");
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0)
				{
					String codesetid=this.gzBudgetTabVo.getString("codesetid");
					ArrayList codeitemList=getCodeItemList(codesetid);
					LazyDynaBean abean=null;
					for(int i=0;i<codeitemList.size();i++)
					{
						abean=(LazyDynaBean)codeitemList.get(i);
						String codeitemid=(String)abean.get("codeitemid");
						String codeitemdesc=(String)abean.get("codeitemdesc");
						dao.update("insert into "+tabname+" (budget_id,tab_id,itemid,itemDesc,b0110,seq) values("+budget_id+","+tab_id+",'"+codeitemid+"','"+codeitemdesc+"','"+b0110+"',"+(i+1)+")");
					} 
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		*/
     return tabname;		
	}
	
	
	
	/**
	 * 创建各单位年度预算数据表
	 * @param budget_id
	 * @param tab_id
	 * @param fieldList
	 * @return
	 */
	public String  createTotalDataTable(String budget_id,String tab_id,ArrayList fieldList)throws GeneralException
	{
		String tablename="t#"+this.userView.getUserName()+"_budgetTotal";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			DbWizard dbw=new DbWizard(this.conn);
			String tname = "";
			if("3".equals(tab_id)){
				tname = "sc02";
			}else{
				tname = "sc03";
			}
			
			String set="";
			String idx="";
			String status="";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
					set=(String)sysOptionMap.get("ysze_set");
					idx=(String)sysOptionMap.get("ysze_idx_menu");
					status=(String)sysOptionMap.get("ysze_status_menu");
			}
			
			
			if(dbw.isExistTable(tablename,false))
			{
				dbw.execute("delete from  "+tablename+" where budget_id="+budget_id+" and tab_id="+tab_id);
				create_alterTable(tablename,dbw,fieldList,2);
			}
			else
			{ 
				create_alterTable(tablename,dbw,fieldList,1);
			}
			
			Field item=null; 
			String insert_b0110="";
			StringBuffer sumSql=new StringBuffer("");
			StringBuffer sn_sumSql=new StringBuffer("");
			for(int i=0;i<fieldList.size();i++)
			{
				item=(Field)fieldList.get(i);
				if(!"itemDesc".equalsIgnoreCase(item.getName())&&!"itemid".equalsIgnoreCase(item.getName())&&!"sn_planNum".equalsIgnoreCase(item.getName())&&!"nc_planNum".equalsIgnoreCase(item.getName()))
				{
					String name=item.getName().substring(2);
					
					if(insert_b0110.length()==0)
					{
						rowSet=dao.search("select "+status+" from "+set+" where b0110='"+name+"' and "+idx+"="+budget_id);
						if(rowSet.next())
						{
							if(rowSet.getString(status)!=null)
							{
								String _status=rowSet.getString(status);
								if("02".equals(_status)|| "03".equals(_status))
								{
									
									if("b_".equals(item.getName().substring(0,2)))
									{
										String sql="insert into "+tablename+" (budget_id,tab_id,itemid,itemDesc,b_"+name+",seq) select "+budget_id+","+tab_id;
										sql+=",itemId,itemDesc,thisYearSum,seq from "+tname+" where budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id;
										dao.update(sql);
									}
									else if("s_".equals(item.getName().substring(0,2)))
									{
										String sql="insert into "+tablename+" (budget_id,tab_id,itemid,itemDesc,s_"+name+",seq) select "+budget_id+","+tab_id;
										sql+=",itemId,itemDesc,lastYearSum,seq from "+tname+" where budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id;
										dao.update(sql);
									}
									insert_b0110=item.getName();
									 
								}
							}
						}
					}
					if("b_".equals(item.getName().substring(0,2)))
						sumSql.append("+"+Sql_switcher.isnull("b_"+name,"0"));
					else if("s_".equals(item.getName().substring(0,2)))
						sn_sumSql.append("+"+Sql_switcher.isnull("s_"+name,"0"));
				}
			}
			
			if(insert_b0110.length()>0)
			{
				for(int i=0;i<fieldList.size();i++)
				{
					item=(Field)fieldList.get(i);
					if(!"itemDesc".equalsIgnoreCase(item.getName())&&!"nc_planNum".equalsIgnoreCase(item.getName())&&!"sn_planNum".equalsIgnoreCase(item.getName()))
					{
						String name=item.getName().substring(2);
						if(insert_b0110.equalsIgnoreCase(item.getName()))
							continue;
						rowSet=dao.search("select "+status+" from "+set+" where b0110='"+name+"' and "+idx+"="+budget_id);
						if(rowSet.next())
						{
							if(rowSet.getString(status)!=null)
							{
								String _status=rowSet.getString(status);
								if("02".equals(_status)|| "03".equals(_status))
								{
									if("b_".equals(item.getName().substring(0,2)))
									{
										String sql="update "+tablename+" set b_"+name+"=(select thisYearSum from "+tname+" S where "+tablename+".itemid=S.itemid and   budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id+" )";
										sql+=" where exists (select null  from "+tname+" S where "+tablename+".itemid=S.itemid and   budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id+" )";		 
										dao.update(sql);
									}
									if("s_".equals(item.getName().substring(0,2)))
									{
										String sql="update "+tablename+" set s_"+name+"=(select lastYearSum from "+tname+" S where "+tablename+".itemid=S.itemid and   budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id+" )";
										sql+=" where exists (select null  from "+tname+" S where "+tablename+".itemid=S.itemid and   budget_id="+budget_id+"  and B0110='"+name+"'  and tab_id="+tab_id+" )";		 
										dao.update(sql);
									}
								}
							}
						}
					}
				}
			}
			else
			{
				String codesetid=this.gzBudgetTabVo.getString("codesetid");
				ArrayList codeitemList=getCodeItemList(codesetid);
				LazyDynaBean abean=null;
				for(int i=0;i<codeitemList.size();i++)
				{
					abean=(LazyDynaBean)codeitemList.get(i);
					String codeitemid=(String)abean.get("codeitemid");
					String codeitemdesc=(String)abean.get("codeitemdesc");
					dao.update("insert into "+tablename+" (budget_id,tab_id,itemid,itemDesc,seq) values("+budget_id+","+tab_id+",'"+codeitemid+"','"+codeitemdesc+"',"+(i+1)+")");
				} 
			}
			
			
			dao.update("update "+tablename+" set nc_planNum=("+sumSql.substring(1)+")  where budget_id="+budget_id+" and tab_id="+tab_id);
			dao.update("update "+tablename+" set sn_planNum=("+sn_sumSql.substring(1)+")  where budget_id="+budget_id+" and tab_id="+tab_id);

			dao.update("update "+tablename+" set bde=nc_planNum-sn_planNum  where budget_id="+budget_id+" and tab_id="+tab_id);
			dao.update("update "+tablename+" set bdl=(nc_planNum-sn_planNum)*100.0/nullif(sn_planNum,0) where budget_id="+budget_id+" and tab_id="+tab_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return tablename;
	}
	/**
	 * 预算审批临时表数据填写
	 */
	public String  createTotalSummaryTable(String budget_id,String tab_id,ArrayList fieldList,String b0110)throws GeneralException
	{
		String tablename="t#"+this.userView.getUserName()+"_summaryTable";
		String tabName = getScTabname(tab_id);
		String temp0110 = "'',";
		String name = "";
		String tempname = "";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			DbWizard dbw=new DbWizard(this.conn);
			
			String set="";
			String idx="";
			String status="";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
					set=(String)sysOptionMap.get("ysze_set");
					idx=(String)sysOptionMap.get("ysze_idx_menu");
					status=(String)sysOptionMap.get("ysze_status_menu");
			}
			
			
			if(dbw.isExistTable(tablename,false)){
				dbw.execute("delete from  "+tablename+" where budget_id="+budget_id+" and tab_id="+tab_id+" and b0110 = '"+b0110+"'");
				create_summaryTable(tablename,dbw,fieldList,2);
			}
			else
			{ 
				create_summaryTable(tablename,dbw,fieldList,1);
			}
			
			Field item=null; 
			rowSet=dao.search("select "+status+", b0110 from "+set+" where b0110 in (select codeitemid from organization where parentid = '"+b0110+"') and "+idx+"="+budget_id);
			while(rowSet.next()){
				if(rowSet.getString(status)!=null)	{				
					String temp = rowSet.getString("b0110");
					temp0110 = temp0110 + "'"+temp + "',";							 
				}
			}


			temp0110 = temp0110.substring(0,temp0110.length()-1);

			for(int i=0;i<fieldList.size();i++)	{
				item=(Field)fieldList.get(i);

				if(!"budget_id".equalsIgnoreCase(item.getName())&&!"itemid".equalsIgnoreCase(item.getName())&&!"b0110".equalsIgnoreCase(item.getName())&&!"seq".equalsIgnoreCase(item.getName())&&!"itemDesc".equalsIgnoreCase(item.getName())&&!"tab_id".equalsIgnoreCase(item.getName())&&!"dcrp".equalsIgnoreCase(item.getName()))
				{			
						name = name + item.getName() + ",";
						tempname = tempname + "sum(" + item.getName() + "),";
				}		
			}			
			name = name.substring(0,name.length()-1);
			tempname = tempname.substring(0,tempname.length()-1);
		//	temp0110 = temp0110.substring(0,temp0110.length()-1);
			String sql="insert into "+tablename+" (b0110,budget_id,tab_id,itemid,itemDesc,"+name+",seq) select '"+b0110+"',budget_id, tab_id,itemid,min(itemdesc), "+tempname+",max(seq)" ;
			sql+=" from "+tabName+" where budget_id="+budget_id+" and tab_id="+tab_id+" and B0110 in ("+temp0110+") "+"group by budget_id,itemid,tab_id ";
			dao.update(sql);
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return tablename;
	}
	/**
	 * 获得表项目
	 * @param codesetid
	 * @return
	 */
	private ArrayList getCodeItemList(String codesetid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList tempList=new ArrayList();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from codeitem where  codesetid='"+codesetid+"'");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("codeitemid", rowSet.getString("codeitemid"));
				abean.set("codeitemdesc", rowSet.getString("codeitemdesc"));
				abean.set("parentid", rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"");
				tempList.add(abean);
			}
			
			 
			for(int i=0;i<tempList.size();i++)
			{
				abean=(LazyDynaBean)tempList.get(i);
				String _itemid=(String)abean.get("codeitemid");
				String _itemdesc=(String)abean.get("codeitemdesc");
				String _parentid=(String)abean.get("parentid");
				int  n=4;
				if(_itemid.equals(_parentid))
				{
					list.add(abean);
					addChildCode(_itemid,tempList,list,n); 
				}
				
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
		}
		return list;
	}
	
	
	private void addChildCode(String itemid,ArrayList tempList,ArrayList list,int n)
	{
		LazyDynaBean abean=null;
		for(int i=0;i<tempList.size();i++)
		{
			abean=(LazyDynaBean)tempList.get(i);
			String _itemid=(String)abean.get("codeitemid");
			String _itemdesc=(String)abean.get("codeitemdesc");
			String _parentid=(String)abean.get("parentid");
			if(itemid.equals(_parentid)&&!_itemid.equals(itemid))
			{
				String _str="";
				for(int j=0;j<n;j++)
					_str+=" ";
				abean.set("codeitemdesc",_str+_itemdesc);
				list.add(abean);
				addChildCode(_itemid,tempList,list,n+4); 
				
			}
			
		} 
	}
	
	
	
	/**
	 * 创建或维护临时表
	 * @param tablename
	 * @param dbw
	 * @param fieldList
	 * @param flag 1：创建  2：维护
	 */
	public void create_alterTable(String tablename,DbWizard dbw,ArrayList fieldList,int flag)
	{
		try
		{
			Table table=new Table(tablename);
		
			if(flag==1)
			{
//				Field field=new Field("b0110","b0110");
//				field.setDatatype(DataType.INT); 
//				field.setLength(8);
//				field.setKeyable(true);
//				field.setNullable(false);
//				table.addField(field);
				
				Field field=new Field("budget_id","budget_id");
				field.setDatatype(DataType.INT); 
				field.setLength(8);
				field.setKeyable(true);
				field.setNullable(false);
				table.addField(field);
				
				field=new Field("tab_id","tab_id");
				field.setDatatype(DataType.INT); 
				field.setKeyable(true);
				field.setLength(8);
				field.setNullable(false);
				table.addField(field);
				
				field=new Field("itemid",ResourceFactory.getProperty("gz.budget.budget_examination.itemNumber"));
				field.setDatatype(DataType.STRING);
				field.setKeyable(true);
				field.setLength(30); 
				field.setNullable(false);
				table.addField(field);
			
				/*
				field=new Field("itemdesc","项目");
				field.setDatatype(DataType.STRING);
				field.setLength(200); 
				table.addField(field);
				
				field=new Field("nc_planNum","年初计划发布数");
				field.setDatatype(DataType.DOUBLE); 
				table.addField(field);
				*/
				
				for(int i=0;i<fieldList.size();i++)
				{
					field=(Field)((Field)fieldList.get(i)).clone();
					if("itemid".equalsIgnoreCase(field.getName()))
						continue;
					table.addField(field);
				}
				
				
				field=new Field("seq","seq");
				field.setDatatype(DataType.INT);  
				field.setLength(8); 
				table.addField(field);
				
				dbw.createTable(table);
			}
			else
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData md=rowSet.getMetaData();
				HashMap existColumnMap=new HashMap();
				for(int i=1;i<=md.getColumnCount();i++)
					existColumnMap.put(md.getColumnName(i).toLowerCase(),"1");
				
				int n=0;
				for(int i=0;i<fieldList.size();i++)
				{
					Field field=(Field)((Field)fieldList.get(i)).clone();
					if(existColumnMap.get(field.getName().toLowerCase())==null)
					{
						n++;
						table.addField(field);
					}
				}
				if(n>0)
					dbw.addColumns(table);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
		}
	}
	/**
	 * 汇总临时表的创建或维护
	 */
	public void create_summaryTable(String tablename,DbWizard dbw,ArrayList fieldList,int flag)throws GeneralException
	{
		try
		{
			Table table=new Table(tablename);
		
			if(flag==1)
			{				
				Field field=new Field("itemid",ResourceFactory.getProperty("gz.budget.budget_examination.itemNumber"));
				field.setDatatype(DataType.STRING);
				
				field.setLength(30); 
				field.setNullable(true);
				table.addField(field);		
				
				for(int i=0;i<fieldList.size();i++)
				{
					field=(Field)((Field)fieldList.get(i)).clone();
					if("itemid".equalsIgnoreCase(field.getName()))
						continue;
					table.addField(field);
				}
				
				
				field=new Field("seq","seq");
				field.setDatatype(DataType.INT);  
				field.setLength(8); 
				table.addField(field);
				
				dbw.createTable(table);
			}
			else
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData md=rowSet.getMetaData();
				HashMap existColumnMap=new HashMap();
				for(int i=1;i<=md.getColumnCount();i++)
					existColumnMap.put(md.getColumnName(i).toLowerCase(),"1");
				
				int n=0;
				for(int i=0;i<fieldList.size();i++)
				{
					Field field=(Field)((Field)fieldList.get(i)).clone();
					if(existColumnMap.get(field.getName().toLowerCase())==null)
					{
						n++;
						table.addField(field);
					}
				}
				if(n>0)
					dbw.addColumns(table);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 判断单位是否是顶层节点
	 * @param b0110
	 * @return
	 * @throws GeneralException
	 */
	public boolean isTopUn(String b0110)throws GeneralException
	{
		boolean isTopUn=false;
		try
		{
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select parentid from organization where codeitemid='"+b0110+"' ");
			if(rowSet.next())
			{
				String parentid=rowSet.getString("parentid");
				if(parentid!=null&&parentid.equals(b0110))
					isTopUn=true; 
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return isTopUn;
	}
	/**
	 * 判断是否为预算单位，赵旭光修改
	 */
	public boolean isBudget(String budget_id,String b0110)throws GeneralException
	{
		boolean bIs=false;
		try
		{
			ArrayList list = new ArrayList();
			String set="";
			String idx="";
			String status="";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
					set=(String)sysOptionMap.get("ysze_set");
					idx=(String)sysOptionMap.get("ysze_idx_menu");
					status=(String)sysOptionMap.get("ysze_status_menu");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select b0110 from "+set+" where "+idx+"="+budget_id+" and b0110='"+b0110+"'");
			if (rowSet.next()){
				bIs=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return bIs;
	}
	/**
	 * 判断是什么类型的表，3为用功计划表，计提和支出都是4
	 */
	public String getScTabname(String tab_id)throws GeneralException{
		String TabName = "sc03";
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select tab_type from gz_budget_tab where tab_id="+tab_id+"");
			if(rowSet.next())
			{
				int tab_type = Integer.parseInt(rowSet.getString("tab_type"));
				if(tab_type==3){
					TabName = "sc02";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return TabName;
	}
	/**
	 * 获得计划表的fieldList
	 * @param b0110
	 * @param budget_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFieldList(String b0110,String budget_id,String tab_id)throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		try
		{
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			if(gzBudgetTabVo!=null&&(gzBudgetTabVo.getInt("tab_type")==4||gzBudgetTabVo.getInt("tab_type")==3))  //计划表
			{
				boolean isTopUn=isTopUn(b0110); //是不是顶层节点
				if(isTopUn)
				{
					fieldList=getChildUnFieldList(budget_id); 
				}
				else
				{	
					String tabname = getScTabname(tab_id);
					ArrayList list=DataDictionary.getFieldList(tabname,Constant.USED_FIELD_SET);
					Field item=null;
					for(int i=0;i<list.size();i++)
					{
						item=(Field)((FieldItem)list.get(i)).cloneField();
						if(",budget_id,b0110,tab_id,itemid,".indexOf(","+item.getName().toLowerCase()+",")!=-1)
						{
							item.setVisible(false);
						}
						item.setLabel("&nbsp;&nbsp;&nbsp;"+item.getLabel()+"&nbsp;&nbsp;&nbsp;");
						item.setReadonly(true);  
						fieldList.add(item);
					}
				}
			}
			
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
		return fieldList;
	}
	
	
	/**
	 * 获得年度汇总表头
	 * @param budget_id
	 * @return
	 */
	private ArrayList getChildUnFieldList(String budget_id)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select extAttr from gz_budget_index where budget_id="+budget_id+"");
			StringBuffer unitIds=new StringBuffer("");
			if(rowSet.next())
			{
				String exAttr=Sql_switcher.readMemo(rowSet,"extAttr");
				String[] temps=exAttr.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].length()>0)
					{
						unitIds.append(",'"+temps[i]+"'");
					}
				}
			} 
		
			Field item=new Field("itemid",ResourceFactory.getProperty("gz.budget.budget_examination.itemNumber"));
			item.setDatatype(DataType.STRING);
			item.setKeyable(true);
			item.setLength(30); 
			item.setNullable(false);
			item.setVisible(false);
			list.add(item);
			 
			item=new Field("itemDesc","&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("gz.formula.project")+"&nbsp;&nbsp;&nbsp;"); 
			item.setLength(100);
			item.setDatatype(DataType.STRING);
			item.setReadonly(true);
			list.add(item);
		 
			//上年计划及预算汇总
			{
				
				item=new Field("sn_planNum","&nbsp;&nbsp;&nbsp;上期计划发布数&nbsp;&nbsp;&nbsp;"); 
				item.setLength(10);
				item.setDatatype(DataType.FLOAT);
				item.setDecimalDigits(2);
				item.setReadonly(true);
				list.add(item);
			 
				 
				rowSet=dao.search("select codeitemid,codeitemdesc from organization where codeitemid in ("+unitIds.substring(1)+") order by a0000 ");
				while(rowSet.next())
				{  
					item=new Field("s_"+rowSet.getString("codeitemid"),"&nbsp;&nbsp;&nbsp;"+rowSet.getString("codeitemdesc")+"&nbsp;&nbsp;&nbsp;"); 
					item.setLength(10);
					item.setDatatype(DataType.FLOAT);
					item.setDecimalDigits(2);
					item.setReadonly(true);
					list.add(item);
					 
				} 
				
				
			}
			
			
			item=new Field("nc_planNum","&nbsp;&nbsp;&nbsp;本期计划申报数&nbsp;&nbsp;&nbsp;"); 
			item.setLength(10);
			item.setDatatype(DataType.FLOAT);
			item.setDecimalDigits(2);
			item.setReadonly(true);
			list.add(item);
		 
			 
		//	rowSet=dao.search("select codeitemid,codeitemdesc from organization where codeitemid in ("+unitIds.substring(1)+") order by a0000 ");
			rowSet.beforeFirst();
			while(rowSet.next())
			{  
				item=new Field("b_"+rowSet.getString("codeitemid"),"&nbsp;&nbsp;&nbsp;"+rowSet.getString("codeitemdesc")+"&nbsp;&nbsp;&nbsp;"); 
				item.setLength(10);
				item.setDatatype(DataType.FLOAT);
				item.setDecimalDigits(2);
				item.setReadonly(true);
				list.add(item);
				 
			} 

			// 变动额，变动率
			item=new Field("bde","&nbsp;&nbsp;&nbsp;变动额&nbsp;&nbsp;&nbsp;"); 
			item.setLength(10);
			item.setDatatype(DataType.FLOAT);
			item.setDecimalDigits(2);
			item.setReadonly(true);
			list.add(item);

			item=new Field("bdl","&nbsp;&nbsp;&nbsp;变动率&nbsp;&nbsp;&nbsp;"); 
			item.setLength(10);
			item.setDatatype(DataType.FLOAT);
			item.setDecimalDigits(2);
			item.setFormat("#####.##%");
			item.setReadonly(true);
			list.add(item);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return list;
	}
	
	
	
	//当前预算描述
	public String getCurrentBudgetDesc(String budget_id)throws GeneralException
	{
		String desc="";
		try
		{
			RecordVo vo=getRecordVo("gz_budget_index","budget_id",Integer.parseInt(budget_id));
			int yearNum=vo.getInt("yearnum");
			int budgetType=vo.getInt("budgettype");  //1 年初预算	  2 年中预算	3 特别调整
			desc=yearNum+"年";
			if(budgetType==1)
				desc+=ResourceFactory.getProperty("gz.budget.budgeting.yearc");
			else if(budgetType==2)
				desc+=ResourceFactory.getProperty("gz.budget.budgeting.yearz");
			else if(budgetType==3)
				desc+=ResourceFactory.getProperty("gz.budget.budgeting.tbtz");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return desc;
	}
	
	
	/**
	 * 获得单位的填报信息
	 * @param b0110
	 * @param budget_id
	 * @return
	 */
	public LazyDynaBean getAppealStatus(String b0110,String budget_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				String set=(String)sysOptionMap.get("ysze_set");
				String idx=(String)sysOptionMap.get("ysze_idx_menu");
				String status=(String)sysOptionMap.get("ysze_status_menu");
				RowSet rowSet=dao.search("select * from "+set+" where "+idx+"="+budget_id+" and b0110='"+b0110+"'");
				if(rowSet.next())
				{
					String  _status=rowSet.getString(status);
					CodeItem item=AdminCode.getCode("23", _status);
					abean.set("status",_status);
					abean.set("statusDesc", item.getCodename());
					if("01".equals(_status)|| "04".equals(_status))
						abean.set("statusDesc",  ResourceFactory.getProperty("performance.workdiary.worknobao"));
				}
			//	else
			//	{
			//		abean.set("status", "01");
			//		abean.set("statusDesc", ResourceFactory.getProperty("performance.workdiary.worknobao"));
		//		}
				
				if(rowSet!=null)
					rowSet.close();
			}
			
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	public String getUnitStatus(String b0110,String budget_id)
	{
		String strresult="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				String set=(String)sysOptionMap.get("ysze_set");
				String idx=(String)sysOptionMap.get("ysze_idx_menu");
				String status=(String)sysOptionMap.get("ysze_status_menu");
				RowSet rowSet=dao.search("select * from "+set+" where "+idx+"="+budget_id+" and b0110='"+b0110+"'");
				if(rowSet.next())
				{
					String  _status=rowSet.getString(status);
					CodeItem item=AdminCode.getCode("23", _status);
					strresult= item.getCodename();		
					if("01".equals(_status)|| "04".equals(_status))
						strresult= ResourceFactory.getProperty("performance.workdiary.worknobao");
					if(rowSet!=null) rowSet.close();
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return strresult;
	}
	/**
	 * 预算汇总
	 * @return
	 */
	public String summary(String budget_id,String tab_id,ArrayList fieldList,String b0110) throws GeneralException
	{
		String tab_name = getScTabname(tab_id);
		try
		{			
			StringBuffer sqll = new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			DbWizard dbw=new DbWizard(this.conn);
			String set="";
			String idx="";
			String status="";
			
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				set=(String)sysOptionMap.get("ysze_set");
				idx=(String)sysOptionMap.get("ysze_idx_menu");
				status=(String)sysOptionMap.get("ysze_status_menu");
			}else{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.exam.zeerror")));
			}

			String approvalB0110 = "'',";//已批准的下级机构
			String Childb0110 = "";//全部下级预算机构
			String _status = "";
			rowSet=dao.search("select "+status+", b0110 from "+set+" where b0110 in (select codeitemid from organization where parentid = '"+b0110+"') and "+idx+"="+budget_id);
			while(rowSet.next()){
				if(rowSet.getString(status)!=null)	{
					_status=rowSet.getString(status);
					String temp_b0110 = rowSet.getString("b0110");
					boolean isBudget = isBudget(budget_id,temp_b0110);
					if(isBudget){						
						String temp = rowSet.getString("b0110");
						Childb0110 = Childb0110 +"'"+ temp +"'"+",";
						if("03".equals(_status)) {	//批准的才汇总
							approvalB0110 = approvalB0110 + "'"+temp + "',";							 
						}
					}
				}
			}
			approvalB0110 = approvalB0110.substring(0,approvalB0110.length()-1);
			if(Childb0110.length()==0&& "".equals(_status)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.exam.wuzdwwxhz")));
			}
			
			tab_id = "";			
			sqll.append("select tab_id from gz_budget_exec where tab_id>2 and budget_id = "+budget_id+"");
			RowSet frowset = dao.search(sqll.toString());
			while(frowset.next()){
				tab_id = tab_id + frowset.getString("tab_id") + ",";
			}
			
			String[] tabid = tab_id.split(",");
			for(int j = 0;j<tabid.length;j++){
				String tabName = getScTabname(tabid[j]);
				String name = "";
				String tempname = "";	
				FieldItem item=null; 
				if(dbw.isExistTable(tabName,false))	{
					dbw.execute("delete from  "+tabName+" where budget_id="+budget_id+" and tab_id="+tabid[j]+" and b0110 = '"+b0110+"'");				
				}	
				fieldList = getFieldList(b0110,budget_id,tabid[j]);
				String tabname = getScTabname(tabid[j]);
				ArrayList list=DataDictionary.getFieldList(tabname,Constant.USED_FIELD_SET);
				for(int i=0;i<fieldList.size();i++)	{
					item=((FieldItem)list.get(i));
					if("N".equalsIgnoreCase(item.getItemtype())&&!"budget_id".equalsIgnoreCase(item.getItemid())&&
							!"itemid".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())&&
							!"seq".equalsIgnoreCase(item.getItemid())&&!"itemDesc".equalsIgnoreCase(item.getItemid())&&
							!"tab_id".equalsIgnoreCase(item.getItemid())&&!"dcrp".equalsIgnoreCase(item.getItemid()))
					{			
						name = name + item.getItemid() + ",";
						tempname = tempname + "sum(" + item.getItemid() + "),";
					}		
				}			
				name = name.substring(0,name.length()-1);
				tempname = tempname.substring(0,tempname.length()-1);

				String sql="insert into "+tabName+" (b0110,budget_id,tab_id,itemid,itemDesc,"+name+",seq) select '"+b0110+"',budget_id, tab_id,itemid,min(itemdesc),"+tempname+",max(seq)" ;
				sql+=" from "+tabName+" where budget_id="+budget_id+" and tab_id="+tabid[j]+" and B0110 in ("+approvalB0110+") group by budget_id,itemid,tab_id";
				dao.update(sql);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return tab_name;
	}
/**
 * 判断执行表里面是否有值
 * @return
 */
	public boolean isExec(String budget_id,String b0110,String tab_id)throws GeneralException
	{
		boolean isExec=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select status from gz_budget_exec where budget_id="+budget_id+" and tab_id="+tab_id+" and b0110 = '"+b0110+"'");
			if(rowSet.next())
			{
				isExec = true;				
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return isExec;
	}
	/**
	 * 上报
	 * @return
	 */
	public void budgetReporting(String budget_id,String b0110)throws GeneralException{
		
		try
		{		
			String codeitemid = "";
			RowSet rowSet=null;
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.conn);
			String set="";
			String idx="";
			String status="";
			String temp_status = "";
			String codeitemdesc = "";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				set=(String)sysOptionMap.get("ysze_set");
				idx=(String)sysOptionMap.get("ysze_idx_menu");
				status=(String)sysOptionMap.get("ysze_status_menu");
			}
			
			
			/*
			StringBuffer sqll = new StringBuffer();
			String id = "";
			sqll
					.append("select tab_id from gz_budget_exec where tab_id>2 and budget_id = "
							+ budget_id + "");
			RowSet frowset = dao.search(sqll.toString());
			while (frowset.next()) {
				id = id + frowset.getShort("tab_id") + ",";
			}
			String[] tabid = id.split(",");
			for (int j = 0; j < tabid.length; j++) {
				boolean isExec = isExec(budget_id, b0110, tabid[j]);
				if (!isExec) {
					throw GeneralExceptionHandler
							.Handle(new Exception(
									ResourceFactory
											.getProperty("gz.budget.exam.zyhzcnsb")));
				}
			}
			*/
		

			//判断下级单位是否批准
			String strSql ="select " + status + ", b0110 from " + set
							+ " where "+ idx + "=" + budget_id
							+" and b0110 in (select codeitemid from organization where parentid = '"+b0110+"')";
			rs = dao.search(strSql);
			while (rs.next()) {
				if (rs.getString(status) != null) {
					String _status = rs.getString(status);

					if  (!"03".equals(_status)) {//非批准
						temp_status = temp_status + "'"
								+ rs.getString("b0110") + "'" + ",";
					}
				}
			}
			if (temp_status.length() > 0) {
				temp_status = temp_status.substring(0, temp_status
						.length() - 1);
				RowSet RS = dao
						.search("select codeitemdesc from organization where codeitemid in ("
								+ temp_status + ")");
				while (RS.next()) {
					codeitemdesc = codeitemdesc
							+ RS.getString("codeitemdesc") + ",";
				}
				codeitemdesc = codeitemdesc.substring(0, codeitemdesc
						.length() - 1);
				throw GeneralExceptionHandler
						.Handle(new Exception(
								""
										+ codeitemdesc
										+ ""
										+ ResourceFactory
												.getProperty("gz.budget.exam.meipizhun")));
			}
	
			//判断是否汇总 判断表里sc02,sc03是否有数据
			StringBuffer sqll = new StringBuffer();
			boolean b= false;
			RowSet frowset=null;
			sqll.append("select * from SC02 where budget_id = "+ budget_id + " and b0110='"+b0110+"'");			
			frowset = dao.search(sqll.toString());
			if  (frowset.next()) {
				b=true;
			}
			if (!b) {		
			    sqll.setLength(0);
				sqll.append("select * from SC03 where budget_id = "+ budget_id + " and b0110='"+b0110+"'");			
				frowset = dao.search(sqll.toString());
				if  (frowset.next()) {
					b=true;
				}
			}
			if (!b) {
				throw GeneralExceptionHandler
						.Handle(new Exception(
								ResourceFactory
										.getProperty("gz.budget.exam.zyhzcnsb")));
			}

	
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
	}
	/**
	 * 判断是否批准
	 * @return
	 */
	public boolean isApproval(String budget_id,String b0110)throws GeneralException
	{
		boolean isApproval=false;
		try
		{
			String set="";
			String idx="";
			String status="";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				set=(String)sysOptionMap.get("ysze_set");
				idx=(String)sysOptionMap.get("ysze_idx_menu");
				status=(String)sysOptionMap.get("ysze_status_menu");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select "+status+" from "+set+" where "+idx+" = "+budget_id+" and b0110 = '"+b0110+"'");
			if(rowSet.next())
			{
				if(rowSet.getString(status)!=null)
				{
					String _status=rowSet.getString(status);
			
					if(/*_status.equals("02")||**/"03".equals(_status))//屏蔽02 wangrd
					{		
						isApproval = true;
					}else{
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.exam.ywpizhundw")));
					}
				}							
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return isApproval;
	}
	/**
	 * 获得预算历史的总额
	 * @return
	 */
	public String getTotal(String budget_id)throws GeneralException{
		String getTotal = "";
		try
		{
			String set="";
			String idx="";
			String ze="";
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				set=(String)sysOptionMap.get("ysze_set");
				idx=(String)sysOptionMap.get("ysze_idx_menu");
				ze=(String)sysOptionMap.get("ysze_ze_menu");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select "+ze+" from "+set+" where "+idx+" = "+budget_id+" and b0110 in (select codeitemid from organization where codeitemid = parentid)");
			if(rowSet.next()){
				getTotal = rowSet.getString(ze);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return getTotal;
	}
	/**
	 * 导出预算历史
	 * @return
	 */
	public String downloadTemplateFactory(String flag,String budget_id,String tab_id,String b0110){
		String fileName="";
//		ArrayList fieldList = new ArrayList();
		try{
					
					if("0".equals(flag)){//如果是普通的导出模板
						ArrayList fieldList  = this.getFieldList(b0110, budget_id, tab_id);
						
						String sql = this.getSql(b0110, budget_id, tab_id, fieldList);
						BudgetingBo budBo=new BudgetingBo(this.conn);
						String tabname=budBo.getTab_Name(tab_id);
						fileName=this.downloadPlanTableTemplate(fieldList,sql,tabname);
					}
					else if("1".equals(flag)){//如果是批量导出模板
						HSSFWorkbook wb = null;
						try{
							wb = new HSSFWorkbook();
							//首先根据当前的tab_id得到预算分类 即计提，支出等
							String budgetGroup = getBudgetGroup(this.gzBudgetTabVo.getInt("tab_id"));
							//根据每个预算分类得到所有的tab_id号。
							ArrayList tabidList = getTabidList(budgetGroup);
							int tabidCount = tabidList.size();
							String randomNum = PubFunc.getStrg();
							//先创建"目录"工作表
							fileName = this.createCatelogue(wb,tabidList,budgetGroup,randomNum);
							//再创建数据工作表
							for(int j=0;j<tabidCount;j++){
								String[] tabTemp = ((String)tabidList.get(j)).split("`");
								ArrayList fieldList  = this.getFieldList(b0110, budget_id, tab_id);
								//String sql = this.getBatchSQL(j,tabTemp[0],budget_id,b0110);
								String sql = this.getSql(b0110, budget_id, tab_id, fieldList);
								fileName=this.batchDownloadPlanTableTemplate(wb,sql,tabTemp[1],budgetGroup,randomNum,fieldList);
							}
						}finally {
							PubFunc.closeResource(wb);
						}

					}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fileName;
	}
	/**
	 * 单个导出模版
	 * @param list
	 * @param sql
	 * @return
	 */
	public String downloadPlanTableTemplate(ArrayList list,String sql,String tabname){
		String fileName="Budget_history_"+this.userView.getUserName()+".xls";
		RowSet rs = null;
		HSSFWorkbook wb = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);


			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet(tabname);
			HSSFCellStyle cellStyle=wb.createCellStyle();
			HSSFFont afont=wb.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			cellStyle.setFont(afont);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(false);
		    cellStyle.setAlignment(HorizontalAlignment.LEFT);
		
			
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFComment comm = null;		
			int rowNum=0;
			HSSFRow row = sheet.createRow(rowNum);
			HSSFCell cell = null;
			int index=0;
			ArrayList headList = new ArrayList();
			row.setHeight((short)500);
			ArrayList codeFieldList = new ArrayList();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
				if("b0110".equalsIgnoreCase(field.getName())|| "budget_id".equalsIgnoreCase(field.getName())|| "tab_id".equalsIgnoreCase(field.getName())|| "itemid".equalsIgnoreCase(field.getName()))
					continue;
				cell=row.createCell(index);
				if(field.getDatatype()==DataType.CLOB)
					sheet.setColumnWidth(index, (short) 8000);
				else{
					if(((field.getLength()+field.getDecimalDigits())*250)>8000)
						sheet.setColumnWidth(index, (short) 8000);
					else
			        	sheet.setColumnWidth(index, (short) ((field.getLength()+field.getDecimalDigits())*250));
				}
				cell.setCellStyle(BudgetingBo.getHSSFCellStyle(wb, -2,DataType.STRING));
				HSSFRichTextString rts=new HSSFRichTextString(field.getLabel().replaceAll("&nbsp;",""));
				cell.setCellValue(rts);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (index + 1), 0, (short) (index + 3), 2));
				comm.setString(new HSSFRichTextString(field.getName()));
				cell.setCellComment(comm);
				headList.add(field);
				if(!"0".equals(field.getCodesetid()))
					codeFieldList.add(field.getCodesetid()+":"+index);
				index++;
			}
			rowNum++;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			rs = dao.search(sql);
			HashMap stylesmap = new HashMap();
			while(rs.next()){
				row = sheet.createRow(rowNum);
				for(int i=0;i<headList.size();i++){
					Field field = (Field)headList.get(i);
					cell=row.createCell(i);
					String key="-1";
					if(field.getDecimalDigits()!=0)
						key=field.getDecimalDigits()+"";
					HSSFCellStyle style=null;
					if(stylesmap.get(key)!=null){
						style=(HSSFCellStyle)stylesmap.get(key);			
					}else{
						style=BudgetingBo.getHSSFCellStyle(wb, Integer.parseInt(key),field.getDatatype());
						stylesmap.put(key, style);		
					}
					if(i==0)
						cell.setCellStyle(cellStyle);
					else
				    	cell.setCellStyle(style);
                    if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT){
                    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    	float value=rs.getFloat(field.getName());
                    	cell.setCellValue(Float.parseFloat(PubFunc.round(value+"", field.getDecimalDigits())));
                    }else if(field.getDatatype()==DataType.DATE){
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(rs.getDate(field.getName())!=null)
						{
							HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(field.getName())));
							cell.setCellValue(textstr);
						}
						else
						{
							HSSFRichTextString textstr = new HSSFRichTextString("");
							cell.setCellValue(textstr);
						}
                    }else {
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(!"0".equals(field.getCodesetid()))
                    	{
                    		String value=rs.getString(field.getName());
                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
                    		cell.setCellValue(textstr);
                    	}else{
                    		String value=rs.getString(field.getName());
                    		HSSFRichTextString textstr = new HSSFRichTextString(value==null?"":value);
                    		cell.setCellValue(textstr);
                    	}
                    }
				}
				rowNum++;
			}
			rowNum--;
			index = 0;
			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			
			int div = 0;
			int mod = 0;			
			for (int n = 0; n < codeFieldList.size(); n++)
			{
				String codeCol = (String) codeFieldList.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
				} else
				{
					if (!"UN".equals(codesetid))
					{
						if("UM".equalsIgnoreCase(codesetid))
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
									+" order by codeitemid");
						else
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
								+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
					}
					else if ("UN".equals(codesetid))
					{
						codeBuf.append("select count(*) from organization where codesetid='UN'");
						rs = dao.search(codeBuf.toString());
						if (rs.next())
							if (rs.getInt(1) == 1)
							{
								codeBuf.setLength(0);
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
							} else if (rs.getInt(1) > 1)
							{
								codeBuf.setLength(0);
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
										+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
								codeBuf.append(" union all ");
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
							}
					}
				}
				rs = dao.search(codeBuf.toString());
				int m = 0;
				while (rs.next())
				{
					row = sheet.getRow(m + 0);
					if (row == null)
						row = sheet.createRow(m + 0);
					cell = row.createCell((short) (208 + index));
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
						cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
					else
						cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
					m++;
				}
				if(m==0)
					m=2;
				sheet.setColumnWidth((short) (208 + index), (short) 0);
				div = index/26;
				mod = index%26;
				String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
			 
				CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum, codeCol1, codeCol1);
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();	
			sheet=null;
			wb=null;
			/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 start */
			//fileName = fileName.replace(".xls", "#");
			/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(wb);
			PubFunc.closeResource(rs);
		}
		return fileName;
	}
	/**
	 * 通过tab_id获得计划表类型（用工，计提，支出）
	 * @param tab_id
	 * @return
	 */
	public String getBudgetGroup(int tab_id){
		String str = "";
		
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select budgetgroup from gz_budget_tab where tab_id="+tab_id+" and tab_type<>3");
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			if(rs.next()){
				String budgetgroup = rs.getString("budgetgroup");
				if(!(budgetgroup==null || "".equals(budgetgroup)))
					str = budgetgroup;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str;
	}
	/**
	 * 获得对应分类的tab_id
	 * @param budgetGroup
	 * @return
	 */
	public ArrayList getTabidList(String budgetGroup){

		RowSet rs = null;
		ArrayList list = new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			if("".equals(budgetGroup))
				buf.append("select tab_id,tab_name from gz_budget_tab where tab_type=3");
			else
				buf.append("select tab_id,tab_name from gz_budget_tab where budgetgroup='"+budgetGroup+"' and tab_type<>3 order by seq");
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String tab_id = rs.getString("tab_id");
				String tab_name = rs.getString("tab_name");
				list.add(tab_id+"`"+tab_name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	
	}
	/**
	 * 创建目录
	 * @param wb
	 * @param tabidList
	 * @param budgetGroup
	 * @param randomNum
	 * @return
	 */
	public String createCatelogue(HSSFWorkbook wb,ArrayList tabidList,String budgetGroup,String randomNum){
		String fileName="";
		if("".equals(budgetGroup) || budgetGroup==null)
			fileName = this.userView.getUserName()+"_基本表_"+randomNum+".xls";
		else
			fileName = this.userView.getUserName()+"_"+budgetGroup+"_"+randomNum+".xls";
		
		HSSFSheet sheet = wb.createSheet("目录");      
		HSSFRow row=null;   //处理行的类
		HSSFCell csCell=null; //处理单元格的类
		short n=0;
		HSSFFont font = wb.createFont();		//处理字体的类	
		font.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle cellStyle= wb.createCellStyle();    //创建单元格对象
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);//水平居中
		sheet.setColumnWidth(0, (short) 8000);
		row=sheet.createRow(n);    //创建第0行，并为它设置一些属性
		csCell=row.createCell(Short.parseShort("0"));    //创建第一个单元格
		HSSFRichTextString ss = new HSSFRichTextString("（本页内容、工作表名称及标题行自动生成，不能修改）");
		csCell.setCellValue(ss);    //向单元格中写入内容
		
		n++;
		row=sheet.createRow(n);   //创建第1行，并为它设置一些属性
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("薪资预算批量导出模板");
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第2行
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("预算表分类");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString(budgetGroup);
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第3行（是个空行）
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString("");
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第4行
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("工作表");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString("tab_id");
		csCell.setCellValue(ss);
		
		n++;
		int tabidCount = tabidList.size();
		for(int k=0;k<tabidCount;k++){
			String[] temp = ((String)tabidList.get(k)).split("`");
			row=sheet.createRow(n);   
			csCell =row.createCell((short)0);
			ss = new HSSFRichTextString(temp[1]);
			csCell.setCellValue(ss);
			csCell =row.createCell((short)1);
			csCell.setCellStyle(cellStyle);
			ss = new HSSFRichTextString(temp[0]);
			csCell.setCellValue(ss);
			n++;
		}
		
		
		//写入excel文件
		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();
			sheet=null;
			wb=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}
	public String getBatchSQL(int j,String tab_id,String budget_id,String b0110){
		
		StringBuffer sql = new StringBuffer();
		try{
		String tabname = this.getScTabname(tab_id);		
		sql.append("select * from ");
		sql.append(tabname);
		sql.append(" where ");
		sql.append(" budget_id="+budget_id);
		sql.append(" and tab_id ="+tab_id);
		sql.append(" and b0110='"+b0110+"' order by seq");
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	/**
	 * 批量导出模版
	 * @param wb
	 * @param sql
	 * @param tab_name
	 * @param budgetGroup
	 * @param randomNum
	 * @param list
	 * @return
	 */
	public String batchDownloadPlanTableTemplate(HSSFWorkbook wb,String sql,String tab_name,String budgetGroup,String randomNum,ArrayList list){

		String fileName="";
		if("".equals(budgetGroup) || budgetGroup==null)
			fileName = this.userView.getUserName()+"_基本表_"+randomNum+".xls";
		else
			fileName = this.userView.getUserName()+"_"+budgetGroup+"_"+randomNum+".xls";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			
			HSSFSheet sheet = wb.createSheet(tab_name);
			HSSFCellStyle cellStyle=wb.createCellStyle();
			HSSFFont afont=wb.createFont();
				afont.setColor(HSSFFont.COLOR_NORMAL);
				afont.setBold(false);
				cellStyle.setFont(afont);
				cellStyle.setBorderBottom(BorderStyle.THIN);
				cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderLeft(BorderStyle.THIN);
				cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderRight(BorderStyle.THIN);
				cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderTop(BorderStyle.THIN);
				cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				cellStyle.setWrapText(false);
			    cellStyle.setAlignment(HorizontalAlignment.LEFT);
			
				
				HSSFPatriarch patr = sheet.createDrawingPatriarch();
				HSSFComment comm = null;		
				int rowNum=0;
				HSSFRow row = sheet.createRow(rowNum);
				HSSFCell cell = null;
				int index=0;
				ArrayList headList = new ArrayList();
				row.setHeight((short)500);
				ArrayList codeFieldList = new ArrayList();
				for(int i=0;i<list.size();i++){
					Field field = (Field)list.get(i);
					if("b0110".equalsIgnoreCase(field.getName())|| "budget_id".equalsIgnoreCase(field.getName())|| "tab_id".equalsIgnoreCase(field.getName())|| "itemid".equalsIgnoreCase(field.getName()))
						continue;
					cell=row.createCell(index);
					if(field.getDatatype()==DataType.CLOB)
						sheet.setColumnWidth(index, (short) 8000);
					else{
						if(((field.getLength()+field.getDecimalDigits())*250)>8000)
							sheet.setColumnWidth(index, (short) 8000);
						else
				        	sheet.setColumnWidth(index, (short) ((field.getLength()+field.getDecimalDigits())*250));
					}
					cell.setCellStyle(BudgetingBo.getHSSFCellStyle(wb, -2,DataType.STRING));
					HSSFRichTextString rts=new HSSFRichTextString(field.getLabel().replaceAll("&nbsp;",""));
					cell.setCellValue(rts);
					comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (index + 1), 0, (short) (index + 3), 2));
					comm.setString(new HSSFRichTextString(field.getName()));
					cell.setCellComment(comm);
					headList.add(field);
					if(!"0".equals(field.getCodesetid()))
						codeFieldList.add(field.getCodesetid()+":"+index);
					index++;
				}
				rowNum++;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				rs = dao.search(sql);
				HashMap stylesmap = new HashMap();
				while(rs.next()){
					row = sheet.createRow(rowNum);
					for(int i=0;i<headList.size();i++){
						Field field = (Field)headList.get(i);
						cell=row.createCell(i);
						String key="-1";
						if(field.getDecimalDigits()!=0)
							key=field.getDecimalDigits()+"";
						HSSFCellStyle style=null;
						if(stylesmap.get(key)!=null){
							style=(HSSFCellStyle)stylesmap.get(key);			
						}else{
							style=BudgetingBo.getHSSFCellStyle(wb, Integer.parseInt(key),field.getDatatype());
							stylesmap.put(key, style);		
						}
						if(i==0)
							cell.setCellStyle(cellStyle);
						else
					    	cell.setCellStyle(style);
	                    if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT){
	                    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	                    	float value=rs.getFloat(field.getName());
	                    	cell.setCellValue(Float.parseFloat(PubFunc.round(value+"", field.getDecimalDigits())));
	                    }else if(field.getDatatype()==DataType.DATE){
	                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                    	if(rs.getDate(field.getName())!=null)
							{
								HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(field.getName())));
								cell.setCellValue(textstr);
							}
							else
							{
								HSSFRichTextString textstr = new HSSFRichTextString("");
								cell.setCellValue(textstr);
							}
	                    }else {
	                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                    	if(!"0".equals(field.getCodesetid()))
	                    	{
	                    		String value=rs.getString(field.getName());
	                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
	                    		cell.setCellValue(textstr);
	                    	}else{
	                    		String value=rs.getString(field.getName());
	                    		HSSFRichTextString textstr = new HSSFRichTextString(value==null?"":value);
	                    		cell.setCellValue(textstr);
	                    	}
	                    }
					}
					rowNum++;
				}
				rowNum--;
				index = 0;
				String[] lettersUpper =
				{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
				
				int div = 0;
				int mod = 0;			
				for (int n = 0; n < codeFieldList.size(); n++)
				{
					String codeCol = (String) codeFieldList.get(n);
					String[] temp = codeCol.split(":");
					String codesetid = temp[0];
					int codeCol1 = Integer.valueOf(temp[1]).intValue();
					StringBuffer codeBuf = new StringBuffer();
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
					{
						codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
					} else
					{
						if (!"UN".equals(codesetid))
						{
							if("UM".equalsIgnoreCase(codesetid))
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
										+" order by codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
									+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
						}
						else if ("UN".equals(codesetid))
						{
							codeBuf.append("select count(*) from organization where codesetid='UN'");
							rs = dao.search(codeBuf.toString());
							if (rs.next())
								if (rs.getInt(1) == 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
								} else if (rs.getInt(1) > 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
											+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
									codeBuf.append(" union all ");
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
								}
						}
					}
					rs = dao.search(codeBuf.toString());
					int m = 0;
					while (rs.next())
					{
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((short) (208 + index));
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
						else
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
						m++;
					}
					if(m==0)
						m=2;
					sheet.setColumnWidth((short) (208 + index), (short) 0);
					div = index/26;
					mod = index%26;
					String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				 
					CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum, codeCol1, codeCol1);
					DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
					HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
					dataValidation.setSuppressDropDownArrow(false);
					sheet.addValidationData(dataValidation);
					index++;
				}
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
				wb.write(fileOut);
				fileOut.close();	
				sheet=null;
				wb=null;
				/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 start */
				//fileName = fileName.replace(".xls", "#");				
				/* 安全问题 文件导出 薪资预算-导出 xiaoyun 2014-9-18 end */
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return fileName;
	
	}
	public boolean isReject(String budget_id,String b0110){
		boolean Reject = true;
		String bb205 = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
			{
				String set=(String)sysOptionMap.get("ysze_set");
				String idx=(String)sysOptionMap.get("ysze_idx_menu");
				String status=(String)sysOptionMap.get("ysze_status_menu");
				
				String sql ="select "+status+" from  "+set+" where "+idx+"="+budget_id+" and b0110="
				   +"(select parentid from organization where codeitemid = '"+b0110+"')";
				RowSet rs = dao.search(sql);
				while(rs.next()){
					bb205 = rs.getString(status);
				}
				if("02".equals(bb205) || "03".equals(bb205)){
					Reject = false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Reject;
	}
	
	public Integer getCurrentBudgetId()
	{
		Integer budgetId = new Integer(0);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select max(budget_id) as budget_id from gz_budget_index");
			if(rowSet.next())
			{
				budgetId = Integer.valueOf(rowSet.getInt("budget_id"));
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return budgetId;
	}
	
	public String getTopUn()
	{
		String result = null;
        if (!userView.isSuper_admin()) // 按管理范围控制
        {
    		String codevalue=userView.getManagePrivCodeValue();
    		if(codevalue.length()>0)
    			result = codevalue;
        }
        return result;
	}
	
	public String genOrgXml(String budget_id,String topUnitCode, String actionName, String target, String flag) 
	{
		if (flag==null) {flag="1";}
		//生成的XML文件
		StringBuffer xmls = new StringBuffer();

		//DB相关
		ResultSet rs = null;		
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		
		//设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "root");

		//与预算表相关
		String set="";
		String idx="";
		if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&sysOptionMap.get("ysze_status_menu")!=null)
		{
			set=(String)sysOptionMap.get("ysze_set");
			idx=(String)sysOptionMap.get("ysze_idx_menu");
			//status=(String)sysOptionMap.get("ysze_status_menu");
		}
		
		String B0110Scope="select B0110 from "+set+" where "+idx+"="+budget_id;
		//创建xml文档自身

		Document myDocument = new Document(root);
		try {
			// 生成SQL语句
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from organization where codesetid = 'UN'");  // 只加载单位
            String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	        strsql.append(" and " +Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	        // 只显示一级预算单位及其上级单位和下级单位
			if (topUnitCode!=null&&topUnitCode.length()>0)  // 加载下级单位
			{
				strsql.append(" and parentid='"+topUnitCode+"' and codeitemid <> parentid and (" +
						"exists(select 1 from organization o "+        // 预算单位及上级
						" where o.codeitemid in ("+B0110Scope+") and "+
				Sql_switcher.substr("o.codeitemid", "1", Sql_switcher.length("organization.codeitemid"))+ "=organization.codeitemid) "+
					")");
			}
	    	else
	    	{   // 加载顶级单位
	    		strsql.append(" and parentid = codeitemid and " +
	    					"exists(select 1 from organization o "+    // 预算单位上级
	    							" where o.codeitemid in ("+B0110Scope+") and "+
	    			Sql_switcher.substr("o.codeitemid", "1", Sql_switcher.length("organization.codeitemid"))+ "=organization.codeitemid)"); 
	    		
	    	}
			strsql.append(" order by A0000");				
			
			// 执行 SQL
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(strsql.toString());
			
			String status = "";
			//设置跳转字符串
			String theaction = null;
			while (rs.next()) {
				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =  rs.getString("codeitemid");
				String unitname =  rs.getString("codeitemdesc");
				
				child.setAttribute("id", rs.getString("codesetid")+ unitcode);
				status="";
				if ("1".equals(flag)){
					LazyDynaBean bean = getAppealStatus(unitcode, budget_id);				
					if (topUnitCode!=null&&topUnitCode.length()>0) 
					  status = (bean.get("statusDesc") != null)?"("+bean.get("statusDesc").toString()+")":"";
				}

				child.setAttribute("text", unitname + status);
				child.setAttribute("title", unitname);

				theaction = actionName + "&a_code="+ rs.getString("codesetid")+ unitcode;								
				child.setAttribute("href", theaction);
				child.setAttribute("target", target);
				child.setAttribute("icon", "/images/unit.gif");
				if ("1".equals(flag)|| "2".equals(flag)){
					child.setAttribute("xml" ,"budget_exam_org_tree_xml.jsp?topunit=" + unitcode);	
				}
				else if ("3".equals(flag)){
					child.setAttribute("xml" ,"exec_orgtree_xml.jsp?topunit=" + unitcode);		
				}
				
				
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();

			//格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			//将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return xmls.toString();		
	}
	
	public RecordVo getGzBudgetTabVo() {
		return gzBudgetTabVo;
	}


	public void setGzBudgetTabVo(RecordVo gzBudgetTabVo) {
		this.gzBudgetTabVo = gzBudgetTabVo;
	}
	
	
	
	
	
}
