package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:工资总额</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SalaryTotalBo {
	private Connection conn=null;
	private GzAmountXMLBo gzAmountXMLBo=null;
	private HashMap gzXmlMap=null;
	private ArrayList controlList=new ArrayList();   //工资总额 参数控制项列表(有效的)
	private UserView userView=null;
	private String privCodeValue="";   //管理权限
//	private String isPrivControl="1";  //受权限过滤
	private String salaryid="";
	private String totalTable="";      //工资总额关联表
	private String ctrl_type="";       //(0|1)是否进行部门总额控制
	private String contrlLevelId="";   //部门 控制层级
	private String belongUN="";        //归属单位
	private String belongUM="";        //归属部门
	private String ctrl_peroid="";     //控制种类 1按年| 0按月 |2季度
	private String ctrl_by_level="1";  //1;按层级控制  0：不按层级控制
	
	private String isControl="0";      //是否进行总额计算
	private int opt=0;                 // 0:校验  1:塞值 
	/**薪资项目和临时变量列表*/
	private  ArrayList fldvarlist=new ArrayList();
	private  HashMap fldMap=new HashMap();
	private String desc="不予提交工资";
	private String ctrlType="1";//控制方式，=1强制控制，=0仅提示。
	private SalaryCtrlParamBo ctrlparam=null;
	private boolean isComputeTotalTable=false; //是否计算总额表中的剩余值和实际值
	
	
	public SalaryTotalBo(Connection con,UserView a_userView,String a_salaryid)throws GeneralException
	{
		this.conn=con;
		this.salaryid=a_salaryid;
		try
		{
			this.gzAmountXMLBo=new GzAmountXMLBo(this.conn,1);
			this.gzXmlMap=this.gzAmountXMLBo.getValuesMap();
			ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(a_salaryid));
			if(this.gzXmlMap!=null)
			{	//throw GeneralExceptionHandler.Handle(new Exception("没有设置工资总额参数!"));			
				this.totalTable=(String)this.gzXmlMap.get("setid");
				this.ctrl_type=(String)this.gzXmlMap.get("ctrl_type");
				this.ctrl_peroid=(String)this.gzXmlMap.get("ctrl_peroid");
				this.ctrl_by_level=(String)this.gzXmlMap.get("ctrl_by_level");
				HashMap um_un=(HashMap)this.gzXmlMap.get("hs");
				if(um_un!=null)
				{
					this.belongUN=(String)um_un.get("orgid");
					this.belongUM=(String)um_un.get("deptid");
					this.contrlLevelId=(String)um_un.get("contrlLevelId");  //部门控制层级
				}
				this.controlList=getControlList();
				this.userView=a_userView;
				this.privCodeValue=this.userView.getManagePrivCodeValue();
				fldvarlist.clear();
				if(salaryid!=null&&salaryid.trim().length()>0)
					getGzFieldList(salaryid);
				fldvarlist.addAll(this.getMidVariableList());
				fldvarlist.addAll(this.getGzFieldList());
				this.isControl="1";
			}
			ctrlType = ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type");
			if(ctrlType==null||ctrlType.trim().length()==0)
				ctrlType="1";
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);			
		}
	}
	
	public String getCtrlType()
	{
		return this.ctrlType;
	}
	
	
	
	/**
	 * 汇总薪资总额
	 * @param year
	 * @throws GeneralException 
	 */
	public void collectData(String year) throws GeneralException
	{
		try
		{
			
			if("1".equals(ctrl_by_level))
			{
			
				String isSetTotalCondition = isSetTotalCondition();
				if(StringUtils.isNotBlank(isSetTotalCondition)) {
					throw GeneralExceptionHandler.Handle(new Exception(isSetTotalCondition));
				}
				ContentDAO dao=new ContentDAO(this.conn);
				ArrayList layerOrgList=getLayerOrgList();
				
				HashMap   orgMap=getCollectOrg(year);
				for(int i=layerOrgList.size()-2;i>=0;i--)
				{
					ArrayList nodeList=(ArrayList)layerOrgList.get(i);
					for(int j=0;j<nodeList.size();j++)
					{
						LazyDynaBean abean=(LazyDynaBean)nodeList.get(j);
						String codeitemid=(String)abean.get("codeitemid");
						if(orgMap.get(codeitemid)!=null)
						{
							String sub_whl=getSubWhl(abean,(ArrayList)layerOrgList.get(i+1),orgMap);
							if(sub_whl.length()>0)
								collectGzTotal(codeitemid,sub_whl,year);
							
						}
						
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 汇总工资总额
	 * @param codeitemid
	 * @param sub_whl
	 * @param year
	 * @param collectFieldList
	 */
	public void collectGzTotal(String codeitemid,String sub_whl,String year)
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");

			String fcWhere=" and 1=1 ";//sql是否要按封存状态更新 wangrd 2015-02-05
	        String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标   
	            if(fc_flag!=null&&fc_flag.trim().length()>0)
	                fcWhere=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";//未封存
		
			for(int i=0;i<this.controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String balanceitem=(String)abean.get("balanceitem"); //剩余项目	
				String Planitem=(String)abean.get("planitem");  //计划项目
			
				String itemid=Realitem;
				sql.setLength(0);
				sql.append("update "+setid+" set "+itemid+"=(select  "+itemid+" from ");
				sql.append(" (select sum("+itemid+") "+itemid+","+Sql_switcher.month(setid+"z0")+" amonth from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+"  and b0110 in ("+sub_whl+") "
				        +fcWhere
				        +" group by "+Sql_switcher.month(setid+"z0")+" "); 
				sql.append(" )a where "+Sql_switcher.month(setid+"."+setid+"z0")+"=a.amonth ) where "+Sql_switcher.year(setid+"z0")+"="+year+" and b0110='"+codeitemid
				        +"'"+fcWhere+" and exists ( select null from ");
				sql.append(" (select sum("+itemid+") "+itemid+","+Sql_switcher.month(setid+"z0")+" amonth from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+"  and b0110 in ("+sub_whl+") "
				        +fcWhere
				        +" group by "+Sql_switcher.month(setid+"z0")+" ");
				sql.append(" )a where "+Sql_switcher.month(setid+"."+setid+"z0")+"=a.amonth ) ");
				dao.update(sql.toString());
				sql.setLength(0);
				sql.append("update "+setid+" set "+balanceitem+"=("+Planitem+"-"+Realitem+" ) where "+Sql_switcher.year(setid+"z0")+"="+year+" and b0110='"+codeitemid+"' and "+Realitem+" is not null");
				dao.update(sql.toString());
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	
	
	public String getSubWhl(LazyDynaBean abean,ArrayList subNodeList,HashMap orgMap)
	{
		String codeitemid=(String)abean.get("codeitemid");
		StringBuffer whl=new StringBuffer("");
		for(int i=0;i<subNodeList.size();i++)
		{
			LazyDynaBean aabean=(LazyDynaBean)subNodeList.get(i);
			String acodeitemid=(String)aabean.get("codeitemid");
			String aparentid=(String)aabean.get("parentid");
			if(aparentid.equals(codeitemid)&&orgMap.get(acodeitemid)!=null)
				whl.append(",'"+acodeitemid+"'");
		}
		if(whl.length()>0)
			return whl.substring(1);
		return whl.toString();
	}
	
	
	
	
	/**
	 * 取得工资总额表中存在的组织
	 * @param year
	 * @return
	 */
	public HashMap getCollectOrg(String year)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String setid=(String)this.gzXmlMap.get("setid");
			if(setid.length()>0)
			{
				RowSet rowSet=dao.search("select distinct b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
				while(rowSet.next())
				{
					map.put(rowSet.getString("b0110"),"1");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得每层的节点
	 * @return
	 */
	public ArrayList getLayerOrgList()
	{
		ArrayList layerList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
		//	if(this.userView.isSuper_admin())
				sql.append("select * from organization where codesetid<>'@K'");
		/*	else if(this.userView.getManagePrivCodeValue()!=null&&this.userView.getManagePrivCodeValue().length()>0)
			{
				sql.append("select * from organization where codesetid<>'@K'");
				sql.append(" and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
			}*/
			if(sql.length()>0)
			{
				if("1".equals(this.ctrl_type))
					sql.append(" and codesetid<>'UM' ");
				sql.append(" order by grade");
				RowSet rowSet=dao.search(sql.toString());
				ArrayList tempList=new ArrayList();
				String temp="";
				LazyDynaBean abean=new LazyDynaBean();
				while(rowSet.next())
				{
					String grade=rowSet.getString("grade");
					if(temp.length()==0)
						temp=grade;
					if(!temp.equals(grade))
					{
						layerList.add(tempList);
						tempList=new ArrayList();
						temp=grade;
					}
					abean=new LazyDynaBean();
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					String parentid=rowSet.getString("parentid");
					String childid=rowSet.getString("childid");
					String codeitemdesc=rowSet.getString("codeitemdesc");
					abean.set("grade",grade);
					abean.set("codesetid",codesetid);
					abean.set("codeitemid",codeitemid);
					abean.set("parentid",parentid);
					abean.set("childid",childid);
					abean.set("codeitemdesc",codeitemdesc);
					
					tempList.add(abean);
				}
				if(tempList.size()>0)
					layerList.add(tempList);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return layerList;
	}
	
	
	
	/**
	 * 计算总计 (工资审批)
	 * @param belongTime
	 */
	public String  calculateTotal(LazyDynaBean belongTime,String whl)
	{
		String info="";
		
		try
		{
			
			if(isCalculateTotal(belongTime)) //判断是否需要计算总计
			{
				/**薪资表数据->薪资历史表中去*/
				this.opt=0;
				info=validateOverPlanValue(belongTime,whl);
				if(info.length()==0)
				{
					this.opt=1;
					caculateTotal(belongTime,whl);
					info="success";
				}
				
		
			}
			else
			{
				if(calculate_info.length()>0)
					info=calculate_info;
				else
					info="success";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
	
	
	/**
	 * 计算总计
	 * @param belongTime
	 */
	public String  calculateSpTotal(String whl)
	{
		
		String info="";
		try
		{ 
			if(isFlag())
			{
				
				/**薪资表数据->薪资历史表中去*/
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView); 
		        ArrayList dataList=getBelongTimeList(1,whl);
				if(dataList.size()==0)
				{
					info="success";
				}
				else
				{ 
					for(int i=0;i<dataList.size();i++)
					{
						LazyDynaBean dataBean=(LazyDynaBean)dataList.get(i);
						if(isCalculateTotal2(dataBean,whl)) //判断是否需要计算总计
						{
							this.opt=0;
							info=validateOverPlanValue(dataBean,whl);
							if(info.length()==0||info.indexOf(desc)==-1|| "0".equals(this.ctrlType))
							{
								this.opt=1;
								caculateTotal(dataBean,whl);
								if(info.length()==0)  //2011-06-21
									info="success";
							}
							else
								break;
						}
						else
						{
							if(calculate_info.length()>0)
								info=calculate_info;
							else
								info="success";
						}
					}
					
					
				}
				 
			}
			else
				info="success";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			info=e.getMessage();
		}
		return info;
	}
	
	
	/**
	 * 取得数据中的归属日期列表
	 * @param flag
	 * @param whl
	 * @return
	 */
	public ArrayList getBelongTimeList(int flag,String whl)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if(flag==0) //薪资发放
			{
				sql="select distinct a00z0 from "+this.userView.getUserName()+"_salary_"+this.salaryid;
			}
			else        //薪资审批
			{
				sql="select distinct a00z0 from salaryhistory where 1=1 "+whl;
			}
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				Calendar c=Calendar.getInstance();
				Date d=rowSet.getDate(1);
				c.setTime(d);
				abean=new LazyDynaBean();
				abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
				abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
				abean.set("day",String.valueOf(c.get(Calendar.DATE)));
				list.add(abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public boolean isFlag()
	{
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		return flag;
	}
	
	
	/**
	 * 计算总计
	 * @param belongTime
	 */
	public String  calculateTotal()
	{
		String info="";
		try
		{
			if(isFlag())  //判断是否需要计算总计
			{
				/**薪资表数据->薪资历史表中去*/
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
				boolean isSp=gzbo.isApprove();
				String tableName=this.userView.getUserName()+"_salary_"+this.salaryid;
				DbNameBo.autoAddZ1_total(this.conn,this.userView,tableName,salaryid,gzbo.getManager(),gzbo,"",isSp);
				
				ArrayList dataList=getBelongTimeList(0,"");
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean dataBean=(LazyDynaBean)dataList.get(i);
					if(isCalculateTotal(dataBean))
					{
						this.opt=0;
						info=validateOverPlanValue(dataBean,null);
						if(info.length()==0||info.indexOf(desc)==-1|| "0".equals(this.ctrlType))
						{
							this.opt=1;
							caculateTotal(dataBean,null);
							if(info.length()==0)   //2011-06-21
								info="success";
						}
						else
							break;
					}
					else 
					{
						if(calculate_info.length()>0)
							info=calculate_info;
						else
							info="success";
					}
				}
				ContentDAO dao=new ContentDAO(this.conn);
				String temp_name="t#"+this.userView.getUserName()+"_gz"; //"salaryhistorybak_"+this.userView.getUserName();
				StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+temp_name);
				del.append(" where a0100=salaryhistory.a0100 ");
				del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  ) ");
				del.append(" and salaryid="+salaryid);
				del.append(" and lower(userflag)='");  //20100323
				del.append(this.userView.getUserName().toLowerCase());
				del.append("'");
				dao.delete(del.toString(),new ArrayList());
				
				if(!isSp)
				{
					StringBuffer s1=new StringBuffer("");
					StringBuffer s2=new StringBuffer("");
					RowSet rowSet=dao.search("select * from  "+temp_name+" where 1=2");
					ResultSetMetaData metaData=rowSet.getMetaData();
					for(int i=1;i<=metaData.getColumnCount();i++)
					{				 
							 
							s1.append(","+metaData.getColumnName(i));
							s2.append(","+metaData.getColumnName(i));
					}
					String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+temp_name+" where  ( sp_flag='06')";
					dao.update(sql0);
				}
				dao.update("delete from "+temp_name);
			}
			else
				info="success";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return info;
	}
	
	
	
	public void deleteHistory(String whl)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("delete from salaryhistory where 1=1 "+whl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 将已归挡的历史数据备份
	 */
	public void copyInfoToTemp2(String whl)
	{
		
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			DbWizard dbWizard =new DbWizard(this.conn);
			String temp_name="t#"+this.userView.getUserName()+"_gz"; //"salaryhistorybak_"+this.userView.getUserName();
			if(dbWizard.isExistTable(temp_name,false))
				dbWizard.dropTable(temp_name);
			
			if(Sql_switcher.searchDbServer()==2)
				sql.append("create table  "+temp_name+"  as select *");
			else
				sql.append("select * into "+temp_name+" ");
			sql.append(" from salaryhistory where 1=1 "+whl);
	/*		if(!this.userView.isSuper_admin())
			{
				sql.append(" and lower(userflag)='");
				sql.append(this.userView.getUserName().toLowerCase());
				sql.append("'");
			}*/
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	/**
	 * 将已归挡的历史数据备份
	 */
	public void copyInfoToTemp()
	{
		
		try
		{
			String tableName=this.userView.getUserName()+"_salary_"+this.salaryid;
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			DbWizard dbWizard =new DbWizard(this.conn);
			String temp_name="t#"+this.userView.getUserName()+"_gz"; //"salaryhistorybak_"+this.userView.getUserName();
			if(dbWizard.isExistTable(temp_name,false))
				dbWizard.dropTable(temp_name);
			
			if(Sql_switcher.searchDbServer()==2)
				sql.append("create table  "+temp_name+"  as select *");
			else
				sql.append("select * into "+temp_name+" ");
			sql.append(" from salaryhistory where exists (select null from "+tableName);
			sql.append(" where "+tableName+".nbase=salaryhistory.nbase and "+tableName+".a0100=salaryhistory.a0100 ");
			sql.append(" and  "+tableName+".a00z0=salaryhistory.a00z0 and "+tableName+".a00z1=salaryhistory.a00z1 ) ");
			sql.append(" and salaryid="+this.salaryid);
			sql.append(" and lower(userflag)='");
			sql.append(this.userView.getUserName().toLowerCase());
			sql.append("'");
	/*		if(!this.userView.isSuper_admin())
			{
				sql.append(" and lower(userflag)='");
				sql.append(this.userView.getUserName().toLowerCase());
				sql.append("'");
			}*/
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void copyTempToHistory()
	{
		try
		{
			String temp_name="t#"+this.userView.getUserName()+"_gz"; //"salaryhistorybak_"+this.userView.getUserName();
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			StringBuffer field_str=new StringBuffer("");
			for(int i=0;i<metaData.getColumnCount();i++)
				field_str.append(","+metaData.getColumnName(i+1));
			String sql="insert into salaryhistory ("+field_str.substring(1)+") select "+field_str.substring(1)+" from "+temp_name;
			dao.update(sql);
			
			DbWizard dbWizard =new DbWizard(this.conn);
			dbWizard.dropTable(temp_name);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 计算工资总额
	 * @param belongTime
	 */
	public void  caculateTotal(LazyDynaBean belongTime,String whl)
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		//	String table=this.userView.getUserName()+"_salary_"+this.salaryid;
			String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl2=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");
			whl2.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
			whl2.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
			
			String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 	
			if(fc_flag!=null&&fc_flag.trim().length()>0)
				whl2.append(" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ");
			
			
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Planitem=(String)abean.get("planitem");  //计划项目
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String balanceitem=(String)abean.get("balanceitem");
				
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+","; 
				boolean flag=false;
				if(this.salaryid!=null&&this.salaryid.trim().length()>0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true;
					
				} 
				if(!flag)
					continue;  
				this.isComputeTotalTable=true;
				createTotalTempTable(belongTime,abean,whl);
		//		System.out.println("update "+this.totalTable+" set "+Realitem+"=(select realitem from tempTotlalSalary where "+this.totalTable+".b0110=tempTotlalSalary.b0110 ) where 1=1 "+whl2.toString());
		//		System.out.println("update "+this.totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" )");
				dao.update("update "+this.totalTable+" set "+Realitem+"=(select realitem from "+tempTableName+" where "+this.totalTable+".b0110="+tempTableName+".b0110 ) where 1=1 "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" ) ");
				dao.update("update "+this.totalTable+" set "+balanceitem+"=("+Sql_switcher.isnull(Planitem,"0")+"-"+Sql_switcher.isnull(Realitem,"0")+") where 1=1  "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" )");
				this.isComputeTotalTable=false;
			}
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(Math.pow(10,3));
	}
	

	
	/**
	 * 生成实发总额临时表 (已考虑 同月多次发放 和 汇总单位的实发总额)
	 */
	public String createTotalTempTable(LazyDynaBean belongTime,LazyDynaBean abean,String whl)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
		try
		{
			DbWizard dbWizard =new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			Table table=new Table(tempTableName,tempTableName);
			if(dbWizard.isExistTable(tempTableName,false))
			{
				dbWizard.dropTable("t#"+userView.getUserName()+"_gz_2");
			}
				
				
			Field obj=new Field("b0110","b0110");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setLength(255);
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("realitem","realitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("planitem","planitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("remainitem","remainitem");
				obj.setDatatype(DataType.FLOAT);
				obj.setDecimalDigits(4);
				obj.setLength(15);							
				obj.setKeyable(false);			
				obj.setVisible(false);							
				obj.setAlign("left");
				table.addField(obj);
				
				obj=new Field("sp_flag","sp_flag");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setLength(50);
				obj.setAlign("left");
				table.addField(obj);
				
				dbWizard.createTable(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("t#"+userView.getUserName()+"_gz_2");
			
			importTotalTableData(belongTime,abean,whl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tempTableName;
	}
	
	
	/**
	 * 
	 * @param whl
	 * @param set
	 * @param flag
	 * @return
	 */
	public ArrayList getDateList(String whl,HashSet set,boolean flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			
			String tableName="salaryhistory";
			if(!flag)
				tableName=this.userView.getUserName()+"_salary_"+this.salaryid;
			
			
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String str=(String)t.next();
				String[] strs=str.split("-");				
				StringBuffer subwhl=new StringBuffer("");
				
				subwhl.append(" and "+Sql_switcher.year("a00z0")+"="+strs[0]);
				subwhl.append(" and "+Sql_switcher.month("a00z0")+"="+strs[1]+" and a00z1="+strs[2]);	
				
				StringBuffer uns=new StringBuffer("");
				RowSet rowSet=dao.search("select distinct "+field+" from "+tableName+"  where 1=1 "+subwhl.toString()+whl);
				while(rowSet.next())
				{
					uns.append(",'"+rowSet.getString(1)+"'");
				}
				
				if(uns.length()>0)
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("year",strs[0] );
					abean.set("month", strs[1]);
					abean.set("whl",uns.substring(1));
					list.add(abean);
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 判断当前薪资发放数据是否全结束
	 * @return
	 */
	public boolean isAllEnd(String tableName){
		boolean flag=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn); 
			String sql="select count(a0100) from "+tableName;
			RowSet rowSet=dao.search(sql);
			int num=0;
			if(rowSet.next()){
				num=rowSet.getInt(1);
			}
			rowSet=dao.search("select count(a0100) from "+tableName+" where sp_flag='06' ");
			if(rowSet.next()){
				if(num!=0&&num==rowSet.getInt(1))
					flag=true;
			} 
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	//取得当前日期下 薪资历史表中无记录的组织机构
	public String getEmptyRecordsUns(String whl,LazyDynaBean belongTime)
	{
		StringBuffer s=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl_=new StringBuffer("");
			whl_.append(" and "+Sql_switcher.year("a00z0")+"="+year);
			whl_.append(" and "+Sql_switcher.month("a00z0")+"="+month);	
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			RowSet rowSet=dao.search("select distinct "+field+" from salaryhistory where 1=1 "+whl_.toString()+" and "+field+" in ("+whl+")");
			StringBuffer str=new StringBuffer("");
			while(rowSet.next())
			{
				String value=rowSet.getString(1);
				str.append(",'"+value+"'");
			}
			String sql="select codeitemid from organization where codeitemid in ("+whl+")   ";
			if(str.length()>0)
				sql+=" and codeitemid not in ("+str.substring(1)+")";
			rowSet=dao.search(sql);
			while(rowSet.next())
				s.append(",'"+rowSet.getString(1)+"'");
			if(s.length()>0)
				return s.substring(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return s.toString();
	}
	
	/**
	 * 工资总额重新计算
	 * @param dateList   
	 */
	public void calculateTotalSum(ArrayList dateList)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			for(int i=0;i<dateList.size();i++)
			{
				LazyDynaBean dataBean=(LazyDynaBean)dateList.get(i);
				String whl=(String)dataBean.get("whl");
				
				String ss=getEmptyRecordsUns(whl,dataBean);
				if(isCalculateTotal3(dataBean,whl))
				{
					importTotalTempTable(dataBean,whl,ss);
					
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 *判断是否超过 工资总额计划值 
	 * @return
	 */
	public void importTotalTempTable(LazyDynaBean belongTime,String whl,String ss)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String Planitem=(String)abean.get("planitem");  //计划项目
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+",";
				boolean flag=false;
				if(this.salaryid!=null&&this.salaryid.trim().length()>0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true;
					
				}
				if(!flag)
					continue;
				
				this.whereIsUnit=true;
				this.isComputeTotalTable=true; //20141129 dengcan 
				createTotalTempTable(belongTime,abean,whl);
				this.isComputeTotalTable=false; //20141129 dengcan
				String year=(String)belongTime.get("year");
				String month=(String)belongTime.get("month");
				StringBuffer whl2=new StringBuffer("");
				String setid=(String)this.gzXmlMap.get("setid");
				whl2.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				whl2.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
					
				
				String balanceitem=(String)abean.get("balanceitem");
				dao.update("update "+this.totalTable+" set "+Realitem+"=(select realitem from "+tempTableName+" where "+this.totalTable+".b0110="+tempTableName+".b0110 ) where 1=1 "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" ) ");
				dao.update("update "+this.totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in (select b0110 from "+tempTableName+" )");
				
				if(ss.length()>0)
				{
							dao.update("update "+this.totalTable+" set "+Realitem+"=0 where 1=1 "+whl2.toString()+" and b0110 in ("+ss+" ) ");
							dao.update("update "+this.totalTable+" set "+balanceitem+"=("+Planitem+"-"+Realitem+") where 1=1  "+whl2.toString()+" and b0110 in ("+ss+"  )");
					
				}
				
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	
	
	
	
	
	/**
	 * 判断是否需要计算总计(salaryhistory)
	 * @param belongTime 归属时间
	 * @return
	 */
	public boolean isCalculateTotal3(LazyDynaBean belongTime,String whl)
	{
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 

		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			sql.append(" where  b0110 in ("+whl+")");
			if("2".equals(ctrl_peroid))
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			}
			sql.append(" and ("+sp_flag+"='04' or "+sp_flag+"='03' )");
			
			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
				sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			}
			
		    RowSet rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
		    }
		    
		    if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private boolean  whereIsUnit=false;  //sql条件是否写的是 单位
	
	/**
	 *导入实发总额临时表数据
	 */
	public void importTotalTableData(LazyDynaBean belongTime,LazyDynaBean abean,String whl_str)
	{
		try
		{
			String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
			
			String dateField="z0";
			
			
			
			ContentDAO dao=new ContentDAO(this.conn);
			String Planitem=Sql_switcher.isnull((String)abean.get("planitem"),"0");  //计划项目
			String Realitem=(String)abean.get("realitem");  //实发项目
			String formular=(String)abean.get("formular");  //计算公式
			String balanceitem=(String)abean.get("balanceitem"); //剩余项目
			
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			StringBuffer whl=new StringBuffer("");
			String setid=(String)this.gzXmlMap.get("setid");
			String sp_flag=(String)this.gzXmlMap.get("sp_flag");
			
			whl.append(" and "+Sql_switcher.year("a00"+dateField)+"="+year);
			whl.append(" and "+Sql_switcher.month("a00"+dateField)+"="+month);	//20161203 dengcan
 
			/* 
				if(ctrl_peroid .equals("0")) //按月控制   20141106 dengcan
					whl.append(" and "+Sql_switcher.month("a00"+dateField)+"="+month);		
				else if(ctrl_peroid .equals("2")) //按季度 
				{
					whl.setLength(0);
					whl.append(" and "+getQuarterWhl(year,Integer.parseInt(month)));
				}
			 */
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			//导入各单位或部门当前月份实发值（已考虑 同月多次发放 和 汇总单位的实发总额）
			String sub_sql="";
			if(balanceitem!=null&&balanceitem.length()>0)
				sub_sql=calcFormula_str(formular,"",Realitem,"");
			else
				sub_sql=Sql_switcher.isnull(Realitem,"0");
			
			String sub_str="";
			if(whereIsUnit)
			{
				sub_str=" and "+field+" in ( "+whl_str+" )";
			}
			else
			{
				sub_str=" and "+field+" in (select distinct "+field+" from "+this.userView.getUserName()+"_salary_"+this.salaryid+") ";
				if(whl_str!=null&&whl_str.trim().length()>0)
				{
					StringBuffer a_sql=new StringBuffer("");
					a_sql.append(" and "+field+" in (select distinct "+field+" from salaryhistory where 1=1 "+whl_str+" )");	
					sub_str=a_sql.toString();
				}
			}
			if(sub_sql!=null&&sub_sql.trim().length()>0)
			{ 
				StringBuffer sql=new StringBuffer("insert into "+tempTableName+" (realitem,b0110) select  (select sum("+sub_sql+") from salaryhistory a where a."+field+" like b.a_"+field+" "/*+sub_str*/+whl.toString()+"  ) acount,b."+field); //2011-06-21
				sql.append(" from (select "+field+","+field+Sql_switcher.concat()+"'%' a_"+field+" from salaryhistory where 1=1 "+sub_str+whl.toString()+"  group by "+field+" ) b ");
			//	sql.append(" where b."+field+" like '"+this.privCodeValue+"%' ");
				sql.append(" order by "+field);
				
				
				String clientName = SystemConfig.getPropertyValue("clientName");
				if(clientName!=null&& "weichai".equalsIgnoreCase(clientName))  //潍柴放开不按层级汇总   2014-7-3
				{
					if("0".equals(ctrl_by_level)) // 不按层级控制
					{
						sql.setLength(0);
						sql.append("insert into "+tempTableName+" (realitem,b0110) select  sum("+sub_sql+"),"+field+"  from salaryhistory where 1=1 "+sub_str+whl.toString()+"  group by "+field); 
						sql.append(" order by "+field);
					}
				}
				//导入实发项目数据
				dao.update(sql.toString()); 
				
				//导入计划项目数据
				sql.setLength(0);
				
				String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 	
				String subStr="";
				if(fc_flag!=null&&fc_flag.trim().length()>0)
					subStr=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";
				if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append("update "+tempTableName+" set planitem=(select plan_data from ");
					sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
				//	sql.append(" from ( select case when "+Planitem+"-"+Realitem+"="+balanceitem+"  and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  then "+balanceitem+" else "+Planitem+" end as planData ");
					sql.append(" from ( select  "+Planitem+"   as planData ");
					
					sql.append(",b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
					sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
				//	sql.append(" from ( select case when "+Planitem+"-"+Realitem+"="+balanceitem+"  and "+Sql_switcher.month(setid+"z0")+"<>"+month+" then "+balanceitem+" else "+Planitem+" end as planData ");
					sql.append(" from ( select  "+Planitem+"  as planData "); 
					sql.append(",b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  )");
					dao.update(sql.toString());
					
				}
				else if("0".equals(ctrl_peroid))
				{
					sql.append("update "+tempTableName+" set planitem=(select "+Planitem+" from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
					sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ) where exists ( select null from ");
					sql.append(setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
					sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ");
					sql.append(" ) ");
					dao.update(sql.toString());
				}
				else if("2".equals(ctrl_peroid))
				{
					String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
					sql.append("update "+tempTableName+" set planitem=(select plan_data from ");
					sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
				//	sql.append(" from ( select case when "+Planitem+"-"+Realitem+"="+balanceitem+"  and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  then "+balanceitem+" else "+Planitem+" end as planData ");
					sql.append(" from ( select "+Planitem+"   as planData ");
					sql.append(",b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
					sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
				//	sql.append(" from ( select case when "+Planitem+"-"+Realitem+"="+balanceitem+"  and "+Sql_switcher.month(setid+"z0")+"<>"+month+" then "+balanceitem+" else "+Planitem+" end as planData ");
					sql.append(" from ( select  "+Planitem+"  as planData "); 
					sql.append(",b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  )");
					dao.update(sql.toString());
				}
				
				sql.setLength(0);
				String str=" and "+Sql_switcher.year(setid+"Z0")+"="+year+" and "+Sql_switcher.month(setid+"Z0")+"="+month;
				sql.append("update "+tempTableName+" set sp_flag=(select "+sp_flag+" from "+setid+"  where "+setid+".b0110="+tempTableName+".b0110 "+str+" "+subStr+" )");
				sql.append(" where exists (select null from "+setid+"  where "+setid+".b0110="+tempTableName+".b0110 "+str+" "+subStr+" )");
				dao.update(sql.toString());
				dao.delete("delete from "+tempTableName+" where sp_flag is null or ( sp_flag<>'04' and sp_flag<>'03' )  ",new ArrayList());
				sql.setLength(0);
				
				String surplus_compute=(String)this.gzXmlMap.get("surplus_compute"); //封存结余参与计算
				subStr="";
				if(fc_flag!=null&&fc_flag.trim().length()>0)
				{		
					subStr=" and "+Sql_switcher.isnull(fc_flag,"''")+"='1'  "; 
					//实发额-已封存的实发额
					String temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month;
					sql.append("update "+tempTableName+" set realitem=(select  "+tempTableName+".realitem-"+Sql_switcher.isnull("real_data","0")+" from ");
					sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
					sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
					sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
					sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
					sql.append(" where "+tempTableName+".b0110=b.b0110  )");
					dao.update(sql.toString());
					
					/*
					if(ctrl_peroid .equals("1"))    //控制种类 1按年| 0按月 |2季度
					{
						sql.append("update "+tempTableName+" set realitem=(select "+tempTableName+".realitem-"+Sql_switcher.isnull("real_data","0")+" from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+" ,b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  )");
						dao.update(sql.toString());
						
					}
					else if(ctrl_peroid.equals("0"))
					{
						String temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month;
						sql.append("update "+tempTableName+" set realitem=(select  "+tempTableName+".realitem-"+Sql_switcher.isnull("real_data","0")+" from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  )");
						dao.update(sql.toString());
						
						 
					}
					else if(ctrl_peroid.equals("2"))
					{
						String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
						sql.append("update "+tempTableName+" set realitem=(select  "+tempTableName+".realitem-"+Sql_switcher.isnull("real_data","0")+" from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
						sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
						sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
						sql.append(" where "+tempTableName+".b0110=b.b0110  )");
						dao.update(sql.toString());
					}*/
				}	
				
				if(!isComputeTotalTable)  //20141201 dengcan
				{
						sql.setLength(0);	
						String subStr2="";
						if(fc_flag!=null&&fc_flag.trim().length()>0)
							subStr2=" and "+Sql_switcher.isnull(fc_flag,"''")+"<>'1'  ";
						if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
						{
							sql.append("update "+tempTableName+" set realitem=(select "+tempTableName+".realitem+"+Sql_switcher.isnull("real_data","0")+" from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+" ,b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"    "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
							
						}
						else if("2".equals(ctrl_peroid))
						{
							String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
							sql.append("update "+tempTableName+" set realitem=(select  "+tempTableName+".realitem+"+Sql_switcher.isnull("real_data","0")+" from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+"  where "+temp_whl+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"   "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull(Realitem,"0")+") real_data ,b0110");
							sql.append(" from ( select "+Realitem+"  ,b0110 from "+setid+" where "+temp_whl+" and "+Sql_switcher.month(setid+"z0")+"<>"+month+"  "+subStr2+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
						}	
				}
					
				if(fc_flag!=null&&fc_flag.trim().length()>0)
				{	
					sql.setLength(0);	
					if(surplus_compute!=null&& "1".equals(surplus_compute)&&!isComputeTotalTable)
					{
						if("1".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
						{
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
							
						}
						else if("0".equals(ctrl_peroid))
						{
							/*
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull(balanceitem,"0")+"+"+tempTableName+".planitem from "+setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
							sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ) where exists ( select null from ");
							sql.append(setid+" where "+Sql_switcher.year(setid+"z0")+"="+year);
							sql.append(" "+subStr+" and "+Sql_switcher.month(setid+"z0")+"="+month+" and  "+tempTableName+".b0110="+setid+".b0110 ");
							sql.append(" ) ");
							dao.update(sql.toString());
							*/
							
							
							String temp_whl=" "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month;
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
							
							
							
						}
						else if("2".equals(ctrl_peroid))
						{
							String temp_whl=getQuarterWhl(year,Integer.parseInt(month),setid);
							sql.append("update "+tempTableName+" set planitem=(select "+Sql_switcher.isnull("plan_data","0")+"+"+tempTableName+".planitem from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+"  where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  ) where exists (select null from ");
							sql.append("(select sum("+Sql_switcher.isnull("planData","0")+") plan_data ,b0110");
							sql.append(" from ( select "+balanceitem+" as planData,b0110 from "+setid+" where "+temp_whl+" "+subStr+" ) a group by a.b0110) b");
							sql.append(" where "+tempTableName+".b0110=b.b0110  )");
							dao.update(sql.toString());
						}
					}
				}
				
			//	sql.append("delete from "+tempTableName+" where planitem=0 or planitem is null");
			//	dao.delete(sql.toString(),new ArrayList());
				//计划剩余项目
				dao.update("update "+tempTableName+" set remainitem="+Sql_switcher.isnull("planitem","0")+"-"+Sql_switcher.isnull("realitem","0"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void insertOtherInfo(String tempTableName,String field,String sub_sql,String whl)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer b0110_s=new StringBuffer(",");
			RowSet rowSet=dao.search("select distinct b0110 from "+tempTableName);
			while(rowSet.next())
			{
				b0110_s.append(rowSet.getString(1)+",");
			}
			
			String temp_name="t#"+this.userView.getUserName()+"_gz"; //"salaryhistorybak_"+this.userView.getUserName();
			rowSet=dao.search("select distinct "+field+" from "+temp_name);
			StringBuffer un=new StringBuffer("");
			while(rowSet.next())
			{
				String temp=rowSet.getString(1);
				if(b0110_s.indexOf(","+temp+",")==-1)
					un.append(",'"+temp+"'");
			}
			if(un.length()>0)
			{
				String sub_str=" and "+field+" in ("+un.substring(1)+") ";
				StringBuffer sql=new StringBuffer("insert into "+tempTableName+" (realitem,b0110) select  (select sum("+sub_sql+") from salaryhistory a where a."+field+" like b.a_"+field+" "+sub_str+whl.toString()+"  ) acount,b."+field);
				sql.append(" from (select "+field+","+field+Sql_switcher.concat()+"'%' a_"+field+" from salaryhistory where 1=1 "+sub_str+whl.toString()+"  group by "+field+" ) b ");
			//	sql.append(" where b."+field+" like '"+this.privCodeValue+"%' ");
				sql.append(" order by "+field);
				//导入实发项目数据
				dao.update(sql.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String getQuarterWhl(String year,int month)
	{
		
		StringBuffer sql=new StringBuffer("");
		sql.append(Sql_switcher.year("a00z0")+"="+year);
		int one=1;int two=2;int three=3;
		if(month>3&&month<=6)
		{
			one=4;two=5;three=6;
		}
		else if(month>=7&&month<=9)
		{
			one=7;two=8;three=9;
		}
		else if(month>=10&&month<=12)
		{
			one=10;two=11;three=12;
		}
		
		sql.append(" and  ( "+Sql_switcher.month("a00z0")+"="+one);
		sql.append(" or "+Sql_switcher.month("a00z0")+"="+two);
		sql.append(" or "+Sql_switcher.month("a00z0")+"="+three);
		sql.append(" )");
		
		
		
		return sql.toString();
	}
	
	public String getQuarterWhl(String year,int month,String setid)
	{
		
		StringBuffer sql=new StringBuffer("");
		sql.append(Sql_switcher.year(setid+"z0")+"="+year);
		int one=1;int two=2;int three=3;
		if(month>3&&month<=6)
		{
			one=4;two=5;three=6;
		}
		else if(month>=7&&month<=9)
		{
			one=7;two=8;three=9;
		}
		else if(month>=10&&month<=12)
		{
			one=10;two=11;three=12;
		}
		
		sql.append(" and  ("+Sql_switcher.month(setid+"z0")+"="+one);
		sql.append(" or "+Sql_switcher.month(setid+"z0")+"="+two);
		sql.append(" or "+Sql_switcher.month(setid+"z0")+"="+three);
		sql.append(")");
		
		
		
		return sql.toString();
	}
	 
	
	//取得组织机构的部门层级
	public HashMap getUMLayer()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList orgList=getOrgList();
			RowSet rowSet=dao.search("select * from organization where codeitemid=parentid");
			while(rowSet.next())
			{
				String codeitemid=rowSet.getString("codeitemid");
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("codesetid", rowSet.getString("codesetid"));
				abean.set("codeitemid",  rowSet.getString("codeitemid"));
				abean.set("codeitemdesc",  rowSet.getString("codeitemdesc"));
				abean.set("parentid",  rowSet.getString("parentid"));
				
				setUMlayer(abean,map,0,orgList);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	public void setUMlayer(LazyDynaBean _abean,HashMap map,int lay,ArrayList orgList)
	{
		LazyDynaBean abean=null;
		LazyDynaBean abean2=null;
		ArrayList tempList=new ArrayList();

		abean=_abean;
		String codesetid=(String)abean.get("codesetid");
		String codeitemid=(String)abean.get("codeitemid");
		boolean isUm=false;
		int _lay=0;
		if("UM".equalsIgnoreCase(codesetid))
		{
				isUm=true;
				_lay=lay+1;
				map.put(codeitemid.toLowerCase(),String.valueOf(lay+1));
		}
			
		for(int j=0;j<orgList.size();j++)
		{
				abean2=(LazyDynaBean)orgList.get(j);
				String a_parentid=(String)abean2.get("parentid");
				String a_codeitemid=(String)abean2.get("codeitemid");
				if(!a_parentid.equals(a_codeitemid)&&a_parentid.equalsIgnoreCase(codeitemid))
				{
					tempList.add(abean2);
				}
		}

		for(int i=0;i<tempList.size();i++)
		{
			abean2=(LazyDynaBean)tempList.get(i);
			if(isUm)
			{
				setUMlayer(abean2,map,_lay,orgList);
			}
			else
				setUMlayer(abean2,map,0,orgList);
		}
		
		
	}
	
	
	
	//取得组织机构信息
	public ArrayList getOrgList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization  where codesetid<>'@K' order by codeitemid ");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("codesetid", rowSet.getString("codesetid"));
				abean.set("codeitemid",  rowSet.getString("codeitemid"));
				abean.set("codeitemdesc",  rowSet.getString("codeitemdesc"));
				abean.set("parentid",  rowSet.getString("parentid"));
				list.add(abean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	 
	
	/**
	 *判断是否超过 工资总额计划值 
	 * @return
	 */
	public String validateOverPlanValue(LazyDynaBean belongTime,String whl)
	{
		String tempTableName="t#"+userView.getUserName()+"_gz_2"; //this.userView.getUserName()+"_tempTotlalSalary";
		StringBuffer info=new StringBuffer("");
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			
			
			ContentDAO dao=new ContentDAO(this.conn);
			HashMap umLayer=null;
			if(!"1".equals(this.ctrl_type))
				umLayer=getUMLayer();
			boolean isOver=false;
			for(int i=0;i<controlList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)controlList.get(i);
				String Realitem=(String)abean.get("realitem");  //实发项目
				String formular=(String)abean.get("formular");  //计算公式
				String planitemdesc = (String) abean.get("planitemdesc");//计划项目名称
				String _salaryid=","+((String)abean.get("salaryid")).toLowerCase()+",";
				
				
				
				boolean flag=false;
				if(this.salaryid!=null&&this.salaryid.trim().length()>0)
				{
					if(_salaryid.indexOf(","+this.salaryid+",")!=-1)
					  flag=true;
					
				}
				
				
				if(!flag)
					continue;
				
				
				createTotalTempTable(belongTime,abean,whl);
				RowSet rowSet=dao.search("select b0110 from "+tempTableName+" where remainitem<0");
				int num=0;
				while(rowSet.next())
				{
					String org_name="";
					String b0110=rowSet.getString("b0110");
					if("1".equals(this.ctrl_type))
					{
						org_name=AdminCode.getCodeName("UN",b0110);
						isOver=true;
					}
					else
					{	
						org_name=AdminCode.getCodeName("UM",b0110);
						if(org_name==null||org_name.length()==0)
						{
							org_name=AdminCode.getCodeName("UN",b0110);
							isOver=true;
						}
						else
						{
							if(this.contrlLevelId!=null&&this.contrlLevelId.length()>0)
							{
								int level=Integer.parseInt(this.contrlLevelId);
								int selfLevel=Integer.parseInt((String)umLayer.get(b0110.toLowerCase()));
								if(selfLevel<=level)
									isOver=true;
							}
							
							
							if(Integer.parseInt(display_e0122)>0)
							{
								CodeItem item=AdminCode.getCode("UM",b0110,Integer.parseInt(display_e0122));
								if(item!=null)
								{
									org_name=item.getCodename();
									
								}
							}
							String _org_name=getUnByUm(b0110);
							if(_org_name.length()>0)
								org_name=_org_name+" "+org_name;
						}
						
						
					}
					
					info.append("\r\n"+org_name+" 薪资总额控制中实发项目大于计划项目  '"+planitemdesc+"'");
					num++;
				}
				if(num>0)
				{
					if(this.contrlLevelId==null||this.contrlLevelId.length()==0)					
						info.append("\r\n "+desc+"！");
					else if(isOver)
						info.append("\r\n "+desc+"！");
				}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			info.append("算法分析器运算错误！");
		}
		return info.toString();
	}
	
	
	
	public String getUnByUm(String e0122)
	{
		String name="";
		RowSet rowSet=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			int n=0;
			while(true)
			{
				n++;
				rowSet=dao.search("select codesetid,codeitemid,codeitemdesc,parentid from organization where codeitemid=(select parentid from organization where codeitemid='"+e0122+"')");
				if(rowSet.next())
				{
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					String codeitemdesc=rowSet.getString("codeitemdesc");
					String parentid=rowSet.getString("parentid");
					
					if("UN".equalsIgnoreCase(codesetid))
					{
						name=codeitemdesc;
						break;
					}
					
					e0122=codeitemid;
				}
				
				if(n>10)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return name;
	}
	
	
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				
				fldMap.put(rset.getString("cname").toLowerCase(),"1");
				
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	
	
	public void  getGzFieldList(String salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid,formula  from salaryset ");
			buf.append(" where salaryid="+salaryid);
			buf.append(" order by sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			HashMap fieldItemMap=new HashMap();
			while(rset.next())
			{
				
					fldMap.put(rset.getString("itemid").toLowerCase(),"1");
					
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	
	/**
	 * 查询薪资类别中的指标列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getGzFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			//20141031 dengcan  客户薪资类别500多个，表中薪资项19000多个，严重影响效率
	//		buf.append("select fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid,formula  from salaryset ");
	//		buf.append(" order by sortid");
			buf.append("select distinct fieldsetid,itemid,itemdesc,itemtype,itemlength,nwidth,decwidth,codesetid   from salaryset ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			HashMap fieldItemMap=new HashMap();
			while(rset.next())
			{
				if(fieldItemMap.get(rset.getString("itemid"))==null)
				{
					FieldItem item=new FieldItem();
					item.setFieldsetid(rset.getString("fieldsetid"));
					item.setItemid(rset.getString("itemid"));
					item.setItemdesc(rset.getString("itemdesc"));
					item.setItemtype(rset.getString("itemtype"));
					item.setItemlength(rset.getInt("itemlength"));
					item.setDisplaywidth(rset.getInt("nwidth"));
					item.setDecimalwidth(rset.getInt("decwidth"));
					item.setCodesetid(rset.getString("codesetid"));
			//		item.setFormula(Sql_switcher.readMemo(rset,"formula"));
					item.setVarible(0);
					fieldlist.add(item);
					fieldItemMap.put(rset.getString("itemid"), "1");
				}
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return fieldlist;
	}
	
	/**
	 * 
	 * @param formula    计算公式
	 * @param cond       计算条件
	 * @param fieldname  计算项目
	 * @param strWhere   整个人员过滤条件
	 */
	private String calcFormula_str(String formula,String cond,String fieldname,String strWhere)throws GeneralException
	{
		String sql="";
		try
		{
			String table=this.userView.getUserName()+"_salary_"+this.salaryid;
			String strfilter="";
			YksjParser yp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			/**先对计算公式的条件进行分析*/
			if(!(cond==null|| "".equalsIgnoreCase(cond)))
			{
				yp = new YksjParser( this.userView ,fldvarlist,
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(cond);
				strfilter=yp.getSQL();
			}
			StringBuffer strcond=new StringBuffer();
			if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				strcond.append(strWhere);
			if(!("".equalsIgnoreCase(strfilter)))
			{
				if(strcond.length()>0)
					strcond.append(" and ");
				strcond.append(strfilter);
			}
			/**进行公式计算*/
			FieldItem item=DataDictionary.getFieldItem(fieldname);

			yp=new YksjParser( this.userView ,fldvarlist,
					YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
		//	System.out.println(formula);
			yp.run(formula,this.conn,strcond.toString(),table);
			/**单表计算*/
			sql=yp.getSQL();
			//System.out.println(sql.toCharArray());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
      	    throw GeneralExceptionHandler.Handle(ex);
		}
		return sql;
	}
	
	
	
	
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	
	
	
	/**
	 * 判断是否需要计算总计(salaryhistory)
	 * @param belongTime 归属时间
	 * @return
	 */
	public boolean isCalculateTotal2(LazyDynaBean belongTime,String whl)
	{
		calculate_info="";
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 

		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			sql.append(" where  b0110 in (select distinct "+field+" from salaryhistory where 1=1 "+whl+" )");
			
			if("2".equals(ctrl_peroid))
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			}
			

			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
							sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			}
			

			String sql_str=sql.toString().replaceAll("count\\(b0110\\)"," distinct b0110 ")+" and "+sp_flag+"<>'04' and "+sp_flag+"<>'03'  ";
			RowSet rowSet=dao.search(sql_str);
			StringBuffer desc=new StringBuffer("");
			while(rowSet.next())
			{
			   String b0110_value=rowSet.getString("b0110");
			   if(b0110_value!=null&&b0110_value.length()>0)
			   {
				   if("0".equals(this.ctrl_type)) //部门
				   {
					   desc.append(","+AdminCode.getCodeName("UM",b0110_value));
				   }
				   else
				   {
					   desc.append(","+AdminCode.getCodeName("UN",b0110_value));
				   }
			   }
			}
			if(desc.length()>0)
			{
				if(rowSet!=null)
					rowSet.close();
				calculate_info=desc.substring(1)+" 薪资总额没有批复 ,";
				return false;
			}
			
			
			
			
			sql.append(" and ( "+sp_flag+"='04' or "+sp_flag+"='03' )");
		    rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
		    }
		    
		    if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	String calculate_info="";
	/**
	 * 判断是否需要计算总计
	 * @param belongTime 归属时间
	 * @return
	 */
	public boolean isCalculateTotal(LazyDynaBean belongTime)
	{
		calculate_info="";
		boolean flag=true;
		if(this.controlList.size()==0)
			return false;
		String setid=(String)this.gzXmlMap.get("setid");
		if(setid==null||setid.length()==0)  //如果没有设置工资总额子集
			return false;
		String sp_flag=(String)this.gzXmlMap.get("sp_flag");
		if(sp_flag==null||sp_flag.length()==0)  //如果没有设置审批状态标识
			return false;
		
		String fc_flag=(String)this.gzXmlMap.get("fc_flag"); //封存状态指标 
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=(String)belongTime.get("year");
			String month=(String)belongTime.get("month");
			
			
			StringBuffer sql=new StringBuffer("select count(b0110) from "+setid);
			
			String field="b0110";
			if(this.belongUN!=null&&this.belongUN.length()>0)
				field=this.belongUN;
			if("0".equals(this.ctrl_type))
			{
				field="e0122";
				if(this.belongUM!=null&&this.belongUM.length()>0)
					field=this.belongUM;
			}
			sql.append(" where  b0110 in (select distinct "+field+" from "+this.userView.getUserName()+"_salary_"+this.salaryid+" )");
			
			if("2".equals(ctrl_peroid))
			{
				sql.append(" and "+getQuarterWhl(year,Integer.parseInt(month),(String)this.gzXmlMap.get("setid")));
			}
			else
			{
				sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year);
				if("0".equals(ctrl_peroid))    //控制种类 1按年| 0按月 |2季度
				{
					sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+month);
				}
			}
			
			if(fc_flag!=null&&fc_flag.trim().length()>0) //设置了封存指标
			{
				sql.append(" and  "+Sql_switcher.isnull(fc_flag,"''")+"<>'1' ");
			}
			
			String sql_str=sql.toString().replaceAll("count\\(b0110\\)"," distinct b0110 ")+" and "+sp_flag+"<>'04'"+" and "+sp_flag+"<>'03'";
			RowSet rowSet=dao.search(sql_str);
			StringBuffer desc=new StringBuffer("");
			while(rowSet.next())
			{
			   String b0110_value=rowSet.getString("b0110");
			   if(b0110_value!=null&&b0110_value.length()>0)
			   {
				   if("0".equals(this.ctrl_type)) //部门
				   {
					   desc.append(","+AdminCode.getCodeName("UM",b0110_value));
				   }
				   else
				   {
					   desc.append(","+AdminCode.getCodeName("UN",b0110_value));
				   }
			   }
			}
			if(desc.length()>0)
			{
				if(rowSet!=null)
					rowSet.close();
				calculate_info=desc.substring(1)+" 薪资总额没有批复 ,";
				return false;
			}
			
			
			sql.append(" and ( "+sp_flag+"='04' or "+sp_flag+"='03' )");
		    rowSet=dao.search(sql.toString());
		    if(rowSet.next())
		    {
		    	if(rowSet.getInt(1)==0)
		    		return false;
		    }
		    if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	
	/**
	 * 工资总额 参数控制项列表(有效的)
	 * @return
	 */
	private ArrayList getControlList()
	{
		ArrayList list=new ArrayList();
		ArrayList ctrl_item_list=(ArrayList)this.gzXmlMap.get("ctrl_item");
		if(ctrl_item_list!=null)
		{
			for(int i=0;i<ctrl_item_list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)ctrl_item_list.get(i);
				String flag=(String)abean.get("flag");
				if("1".equals(flag))
					list.add(abean);
			}
		}
		return list;
	}

	/**
	 * 判断是否设置了总额参数设置条件
	 * @return
	 */
	public String isSetTotalCondition() {
		String tipsData = "";
		try {
			if(this.gzXmlMap == null) {
				tipsData = "薪资总额参数未定义";
			}else {	
				if(StringUtils.isBlank(this.totalTable.trim())) {
					tipsData = "薪资总额参数未定义";
					return tipsData;
				}
				ArrayList dataList=(ArrayList) this.gzXmlMap.get("ctrl_item");
				if(dataList == null || dataList.size() == 0) {
					tipsData = "薪资总额参数未定义计划项目，实发项目和剩余项目参数!";
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tipsData;
	}

	public String getIsControl() {
		return isControl;
	}



	public void setIsControl(String isControl) {
		this.isControl = isControl;
	}

 
	public String getDesc() {
		return desc;
	}





	public void setDesc(String desc) {
		this.desc = desc;
	}





	public String getBelongUN() {
		return belongUN;
	}





	public void setBelongUN(String belongUN) {
		this.belongUN = belongUN;
	}





	public String getBelongUM() {
		return belongUM;
	}





	public void setBelongUM(String belongUM) {
		this.belongUM = belongUM;
	}





	public String getCtrl_type() {
		return ctrl_type;
	}





	public void setCtrl_type(String ctrl_type) {
		this.ctrl_type = ctrl_type;
	}
	
	
	
}
