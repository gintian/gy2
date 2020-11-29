package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 
 * 
 *<p>Title:SalaryReportBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 22, 2007</p> ResumeFilterBo
 *@author dengcan
 *@version 4.0
 */
public class SalaryReportBo {
	private Connection conn=null;
	private String salaryid="";
	/**薪资类别数据对象*/
	private RecordVo templatevo=null; 
	private String gz_tablename="";
	private RecordVo reportdetailvo=null;
	/**人员过滤条件列表*/
	private ArrayList condlist;
	private HashMap   condmap;
	private UserView userView;
	private String unit_sql="";
	private String mode="";
	private String   controlByUnitcode="0";  //=1共享类别非管理员按操作单位控制权限
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	private String manager="";
	private String orgid="";//归属单位指标
	private String deptid="";//归属部门指标
	public SalaryReportBo(Connection cn,String salaryid)
	{
		this.conn=cn;
		this.salaryid=salaryid;
		
	}	
	public SalaryReportBo(Connection cn,String salaryid,UserView userView,int t)
	{
		this.conn=cn;
		this.salaryid=salaryid;
		this.userView=userView;
	}	
	public SalaryReportBo(Connection cn,String salaryid,UserView userView)
	{
		try{
	    	this.conn=cn;
	    	this.salaryid=salaryid;
	    	this.userView=userView;
	    	ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(this.salaryid));
	    	this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
	    	this.controlByUnitcode=this.controlByUnitcode();
	    	orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
		    deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
	    	init();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	/**
	 * 如果薪资类别是共享类别，操作用户是非管理员，并且属性设置了归属单位或部门，则为：1   否则为：0
	 * @return
	 */
	public String controlByUnitcode()
	{
		String flag="0";
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		if(this.manager!=null&&this.manager.trim().length()>0)
		{
			if(!this.userView.getUserName().equalsIgnoreCase(this.manager))
			{
				if(orgid.length()>0||deptid.length()>0)
					flag="1";
			}
		}
		return flag;
	}
	public SalaryReportBo(Connection cn,String salaryid,String applet)
	{
		this.conn=cn;
		this.salaryid=salaryid;
		init();
		
	}
	
	public void init()
	{
		try
		{
			templatevo=new RecordVo("salarytemplate");
			templatevo.setInt("salaryid",Integer.parseInt(this.salaryid));
			ContentDAO dao=new ContentDAO(this.conn);
			templatevo=dao.findByPrimaryKey(templatevo);
			this.condlist=searchManFilter();	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 更新工资报表项目记录
	 * @param rsdtlid
	 * @param itemid
	 * @param columnName
	 * @param value
	 */
	public void updateReportItemVo(String rsdtlid,String itemid,String columnName,String value)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo reportitemvo=new RecordVo("reportitem");
			reportitemvo.setInt("rsdtlid",Integer.parseInt(rsdtlid));
			reportitemvo.setString("itemid", itemid.toUpperCase());
			reportitemvo=dao.findByPrimaryKey(reportitemvo);
			if("align".equalsIgnoreCase(columnName)|| "nwidth".equalsIgnoreCase(columnName))
				reportitemvo.setInt(columnName.toLowerCase(),Integer.parseInt(value));
			else if("itemfmt".equalsIgnoreCase(columnName)|| "itemdesc".equalsIgnoreCase(columnName))
				reportitemvo.setString(columnName.toLowerCase(), value);
			dao.updateValueObject(reportitemvo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public RecordVo getReportdetailVo(String rsdtlid,String rsid)
	{
		RecordVo reportdetail=new RecordVo("reportdetail");
		try
		{
			
			reportdetail.setInt("rsdtlid",Integer.parseInt(rsdtlid));
			reportdetail.setInt("rsid",Integer.parseInt(rsid));
			ContentDAO dao=new ContentDAO(this.conn);
			reportdetail=dao.findByPrimaryKey(reportdetail);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return reportdetail;
	}
	
	
	/**
	 * 重新设置工资报表项顺序
	 * @param currentcolList
	 * @param tableHeadList
	 */
	public void resetTableColumnSort(ArrayList currentcolList,ArrayList tableHeadList,String rsdtlid)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			for(int i=0;i<currentcolList.size();i++)
			{
				String aitemdesc=(String)currentcolList.get(i);
				for(int j=0;j<tableHeadList.size();j++)
				{
					LazyDynaBean abean=(LazyDynaBean)tableHeadList.get(j);
					String itemid=(String)abean.get("itemid");
					String itemdesc=(String)abean.get("itemdesc");
					if(itemdesc.equals(aitemdesc))
					{
						
						dao.update("update reportitem set sortid="+i+" where rsdtlid="+rsdtlid+" and UPPER(itemid)='"+itemid.toUpperCase()+"'");
						
						break;
					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 重新设置工资报表项顺序
	 * @param currentcolList
	 * @param tableHeadList
	 */
	public void resetTableColumnWidth(ArrayList currentColWidthList,ArrayList currentcolList,ArrayList tableHeadList,String rsdtlid)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			for(int i=0;i<currentcolList.size();i++)
			{
				String aitemdesc=(String)currentcolList.get(i);
				String nwidth=(String)currentColWidthList.get(i);
				for(int j=0;j<tableHeadList.size();j++)
				{
					LazyDynaBean abean=(LazyDynaBean)tableHeadList.get(j);
					String itemid=(String)abean.get("itemid");
					String itemdesc=(String)abean.get("itemdesc");
					if(itemdesc.equals(aitemdesc))
					{
						dao.update("update reportitem set nwidth="+nwidth+" where rsdtlid="+rsdtlid+" and UPPER(itemid)='"+itemid.toUpperCase()+"'");						
						break;
					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 *  取得表头描述
	 * @param rsid    报表种类编号
	 * @param rsdtlid 报表编号
	 * @return
	 */
	public ArrayList getTableHeadDescList(String rsid,String rsdtlid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{	
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select a.itemid,a.itemdesc,a.itemfmt,b.itemtype,b.codesetid,a.nwidth,a.align,c.initflag from reportitem a left join fielditem b on UPPER(a.itemid)=UPPER(b.itemid) left join salaryset c on UPPER(a.itemid)=UPPER(c.itemid) and a.stid=c.salaryid where  a.stid="+this.salaryid+"  and a.Rsdtlid="+rsdtlid+"  order by a.sortid";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				String itemid=rowSet.getString("itemid").toLowerCase();
				int initFlag=rowSet.getInt("initflag");
				if(initFlag!=3)
				{
			    	if(!(userView.isSuper_admin()|| "1".equals(userView.getGroupId())))
			    	{
			    		if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				    		continue;
			    	}
				}
				abean=new LazyDynaBean();
				abean.set("itemid",itemid);
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				String itemtype="A";
				String codesetid="0";
				String itemfmt="";
				if(rowSet.getString("itemfmt")!=null)
					itemfmt=rowSet.getString("itemfmt");
				
				if("a00z0".equals(itemid)|| "a00z2".equals(itemid))
				{
					itemtype="D";
				}
				else if("a00z1".equals(itemid)|| "a00z3".equals(itemid))
				{
					itemtype="N";
				}
				else if(rowSet.getString("itemtype")!=null)
				{
					itemtype=rowSet.getString("itemtype");
					codesetid=rowSet.getString("codesetid");
				}
				
				if("E01A1".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="@K";
				}
				if("E0122".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="UM";
				}
				abean.set("itemtype",itemtype);
				abean.set("codesetid",codesetid);
				abean.set("itemfmt",itemfmt);
				abean.set("nwidth", rowSet.getString("nwidth"));
				abean.set("align",rowSet.getString("align"));
				list.add(abean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return list;
	}
	/**
	 * 原来的列顺序
	 * @param rsdtlid
	 * @return
	 */
	public HashMap getOldSort(String rsdtlid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql="select * from reportitem where rsdtlid="+rsdtlid+" order by sortid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("itemid"),rs.getString("sortid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	/**
	 * 取得SQL过滤条件
	 * @param condid
	 * @param tablename
	 * @return
	 */
	public String getFilterWhere(String condid,String tablename)
	{
	  String strwhere="";
	  try
	  {
		/**
		 * 表达式|因子
		 */
		String value=(String)condmap.get(condid);
		if(value==null|| "".equalsIgnoreCase(value))
			return "";
		int idx=0;
		idx=value.indexOf("|");
		String expr=value.substring(0, idx);
		String factor=value.substring(idx+1);
		FactorList factorlist=new FactorList(expr,factor,"");
		strwhere=factorlist.getSingleTableSqlExpression(tablename);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  strwhere=" 1=2 ";
	  }
	  return strwhere;
	}
	
	
	/**
	 * 取得报表数据总记录数
	 * @param codeitemid
	 * @param codesetid
	 * @param userName
	 * @param condid 过滤条件
	 * @return
	 */
	public int getRecordsCount(String codeitemid,String codesetid,String userName,String condid,String empfiltersql,String priv_mode,String role,String privCode,String privCodeValue,String manager)
	{
		int recordCount=0;
		if(manager.length()==0||manager.equalsIgnoreCase(userName))
	    	this.gz_tablename=userName+"_salary_"+this.salaryid;
		else
			this.gz_tablename=manager+"_salary_"+this.salaryid;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer(" from "+this.gz_tablename+" where 1=1 "+(empfiltersql==null?"":empfiltersql));
			buf.append(getwhl1(codeitemid,codesetid,condid,privCodeValue));
			buf.append(this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName));
			RowSet rowSet=dao.search("select count(*) "+buf.toString());
			if(rowSet.next())
				recordCount=rowSet.getInt(1); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return recordCount;
	}
	/***
	 * 机构树几点限制
	 * @param codeitemid
	 * @param codesetid
	 * @param condid
	 * @param privCodeValue
	 * @return
	 */
	private String getwhl1(String codeitemid,String codesetid,String condid,String privCodeValue)
	{
		StringBuffer buf=new StringBuffer("");
		if(!"-1".equals(codeitemid))
	    {
			if(this.orgid.length()>0||this.deptid.length()>0)//工资发放中(只发放中这样)，当设置了归属单位指标或者设置了归属部门指标，按这个规则走
			{
				 buf.append(" and ");
				if("UN".equalsIgnoreCase(codesetid))
				{
					if(this.orgid.length()>0&&this.deptid.length()>0)
					{
						buf.append("(");
						buf.append(this.orgid+" like '"+codeitemid+"%'");
						buf.append(" or "+this.deptid+" like '"+codeitemid+"%')");
					}
					else if(this.orgid.length()>0)
					{
						buf.append("(");
						buf.append(this.orgid+" like '"+codeitemid+"%')");
					}
					else
					{
						buf.append("(");
						buf.append(this.deptid+" like '"+codeitemid+"%')");
					}	
				}
				else if("UM".equalsIgnoreCase(codesetid))
				{
					if(this.orgid.length()>0&&this.deptid.length()>0)
					{
						buf.append("(");
						buf.append(this.orgid+" like '"+codeitemid+"%'");
						buf.append(" or "+this.deptid+" like '"+codeitemid+"%')");
					}
					else if(this.orgid.length()>0)
					{
						buf.append("(");
						buf.append(this.orgid+" like '"+codeitemid+"%')");
					}
					else
					{
						buf.append("(");
						buf.append(this.deptid+" like '"+codeitemid+"%')");
					}	
				}
			}
			 else
			 {
	    		 if("UN".equalsIgnoreCase(codesetid))
             	  {
    	    			buf.append(" and (b0110 like '");
	        			buf.append(codeitemid);
	    	    		buf.append("%'");
	    	    		if("".equalsIgnoreCase(codeitemid))
	    	    		{
		    	    		buf.append(" or b0110 is null");
		    	    	}
	    		    	buf.append(")");
	        		}
	         		else if("UM".equalsIgnoreCase(codesetid))
	    	    	{
	 	        		buf.append(" and e0122 like '");
		        		buf.append(codeitemid);
		        		buf.append("%'");
	    	    	}
	     	      	else if("@K".equalsIgnoreCase(codesetid))
                 	{
                 		String value=this.getUnByPosition(codeitemid);
        	    		if(value!=null&&value.length()>=2)
        	    		{
        	    			String code=value.substring(0,2);
        	    			String codevalue=value.substring(2);
        	    			if("UN".equalsIgnoreCase(code))
		            		{
		            			buf.append(" and (b0110 like '"+codevalue+"%' ");
		            			if(codevalue==null)
		            				buf.append(" or b0110 is null");
		            			buf.append(")");
		            		}else if("UM".equalsIgnoreCase(code))
		            		{
		            			buf.append(" and (e0122 like '"+codevalue+"%' ");
		        	     		if(codevalue==null)
		            				buf.append(" or e0122 is null");
		             			buf.append(")");
		            		}
        	    		}
        	    		else
          	    		{
             				buf.append(" and 1=2 ");
             			}
                 	}
	        		else
	    	    		buf.append(" and 1=2 ");
	        	}
	    }else{
	    	if("-1".equals(codeitemid)){//首次打开或者选中组织机构节点，则放开机构树控制   zhaoxg 2014-3-1
	    		buf.append(" and 1=1 ");
	    	}    	
	    }
//		获得过滤条件
		if(!"all".equalsIgnoreCase(condid))
		{
			String temp=getFilterWhere(condid,this.gz_tablename);
			if(temp.trim().length()>0)
				buf.append(" and ("+temp+")");
		}
		//获得过滤条件
		return buf.toString();
	}
	/**
	 * 兼容共享工资
	 * @param privCode
	 * @param privCodeValue
	 * @param manager
	 * @param priv_mode
	 * @param role
	 * @param userName
	 * @return
	 */
	private String getwhl2(String privCode,String privCodeValue,String manager,String priv_mode,String role,String userName)
	{
		StringBuffer buf=new StringBuffer("");
		/**当该工资类别启用权限过滤时，才加权限过滤*/
		if("1".equals(role))//超级用户，
		{
				
		}
		else if(manager.length()==0||manager.equalsIgnoreCase(userName))//非共享类别，或者管理员
	    {
	    }
		else{
			if("3".equals(this.mode))
			{
				if((manager.trim().length()>0&&!manager.equalsIgnoreCase(userName)))
				{
		    		if(this.unit_sql!=null&&this.unit_sql.trim().length()>0)
		    		{
			    		buf.append(" and ("+this.unit_sql+")");
			    	}
				}
			}
			else if("0".equals(this.mode))
			{
				String privSql = this.getWhlByUnits();
				buf.append(privSql);
			}
//			else
//			{
//				
//		    	if((manager.trim().length()>0&&!manager.equalsIgnoreCase(userName)))
//		    	{
//			
//		    		if(privCode!=null&&!privCode.equals(""))
//			    	{
//		    			if(privCode.equalsIgnoreCase("CZDW"))
//		    			{
//		    				/**不为全部时加限制*/
//		    				if(privCodeValue.length()!=3)
//		    				{
//		    		    		String[] arr = privCodeValue.split("`");
//		    		    		buf.append(" and (");
//		    		    		StringBuffer t_buf = new StringBuffer();
//		    			    	for(int i=0;i<arr.length;i++)
//		    		    		{
//		    			    		String temp = arr[i];
//		    				    	if(arr[i]==null||arr[i].equals(""))
//		    				    		continue;
//		    				    	if(temp.substring(0, 2).equalsIgnoreCase("UN"))
//		    				    		t_buf.append(" or b0110 ");
//		    			    		else
//		    					    	t_buf.append(" or e0122 ");
//		    				    	t_buf.append(" like '"+arr[i].substring(2)+"%'");
//		    		    		}
//		    			    	buf.append(t_buf.toString().substring(3)+")");
//		    				}
//		    			}
//		    			else if(privCode.equalsIgnoreCase("UN"))
//             	    	{
//            	    		buf.append(" and  (b0110 like '");
//	            	    	buf.append(privCodeValue);
//	        	        	buf.append("%'");
//	             	    	if(privCodeValue.equalsIgnoreCase(""))
//	    	            	{
//		            	    	buf.append(" or b0110 is null");
//		                	}
//	            	    	buf.append(")");
//	                	}
//	                 	else if(privCode.equalsIgnoreCase("UM"))
//	                	{
//	     	            	buf.append(" and (e0122 like '");
//		                	buf.append(privCodeValue);
//		                	buf.append("%'");
//		                	if(privCodeValue.equalsIgnoreCase(""))
//	    	            	{
//		                		buf.append(" or e0122 is null");
//		                	}
//		                	buf.append(")");
//	                	}
//	                 	else if(privCode.equalsIgnoreCase("@K"))
//	                 	{
//	                 		String value=this.getUnByPosition(privCodeValue);
//	        	    		if(value!=null&&value.length()>=2)
//	        		    	{
//	        		    		String code=value.substring(0,2);
//	        		    		String codevalue=value.substring(2);
//	        			    	if(code.equalsIgnoreCase("UN"))
//			            		{
//			        	    		buf.append(" and (b0110 like '"+codevalue+"%' ");
//			        	     		if(codevalue==null)
//			        	    			buf.append(" or b0110 is null");
//			        	    	 	buf.append(")");
//			            		}else if(code.equalsIgnoreCase("UM"))
//			            		{
//			            			buf.append(" and (e0122 like '"+codevalue+"%' ");
//			            			if(codevalue==null)
//			        	    			buf.append(" or e0122 is null");
//			        	    		buf.append(")");
//			        	    	}
//	        	    		}
//	            			else
//	             			{
//	            				buf.append(" and 1=2 ");
//	            			}
//	                 	}
//	                	else
//	                		buf.append(" and 1=2 ");
//	                 }
//	    			else
//		    		{
//	    				buf.append(" and 1=2 ");
//	 	    		}
//	    		}
//	    	}
		}
		
		return buf.toString();
	}
	
	
	/**
	 * 取得报表查询的sql语句
	 * @param rsid
	 * @param rsdtlid
	 * @param codeitemid
	 * @param codesetid
	 * @param condid
	 * @param groupValues
	 * @param tabHeadList
	 * @return
	 */
	public String getReportSql(String rsid,String rsdtlid,String codeitemid,String codesetid,String condid,String groupValues,ArrayList tabHeadList,LazyDynaBean groupBean,String noManagerFilterSql,String empfiltersql,String priv_mode,String role,String privCode,String privCodeValue,String userName,String manager,String model,String spSQL,String buf_b)
	{
		String sql="";
		try
		{
			if("1".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid))
			{
				StringBuffer buf=new StringBuffer(" from "+this.gz_tablename+" where 1=1 "+(noManagerFilterSql==null?"":noManagerFilterSql)+(empfiltersql==null?"":empfiltersql));
				buf.append(getwhl1(codeitemid,codesetid,condid,privCodeValue));
				buf.append(this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName));
				if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
					buf.append(" and "+spSQL);
				buf.append(buf_b);
				sql="select "+this.gz_tablename+".* "+buf.toString();
			}
			else if("3".equals(rsid)|| "13".equals(rsid))
			{
				
				String f_groupItem=(String)groupBean.get("f_groupItem");
				String s_groupItem=(String)groupBean.get("s_groupItem");
				boolean isHaveE0122=false;
				boolean isHaveB0110=false;
				StringBuffer buf=new StringBuffer("");
				LazyDynaBean headBean=null;
				for(int i=0;i<tabHeadList.size();i++)
				{
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					if("a00z1".equalsIgnoreCase(aitemid)|| "a00z3".equalsIgnoreCase(aitemid))
						buf.append(",max("+aitemid+") "+aitemid);
					else
				    	buf.append(",sum("+aitemid+") "+aitemid);
					
				}
				StringBuffer temp_sql=new StringBuffer("");
				String tp="";
				if(buf.toString().length()>0)
					tp=buf.substring(1)+",";
				temp_sql.append("select "+tp);
				if(f_groupItem!=null&&f_groupItem.length()>0)
				{
					if("e0122".equalsIgnoreCase(f_groupItem))
						isHaveE0122=true;
					if("b0110".equalsIgnoreCase(f_groupItem))
						isHaveB0110=true;
					temp_sql.append(f_groupItem+",");
				}
				if(s_groupItem!=null&&s_groupItem.length()>0)
				{
					if("e0122".equalsIgnoreCase(s_groupItem))
						isHaveE0122=true;
					if("B0110".equalsIgnoreCase(s_groupItem))
						isHaveB0110=true;
					if(f_groupItem!=null&&f_groupItem.length()>0) //2014-06-28 dengcan 【汉口银行 2880】薪资管理--薪资发放-薪资报表-工资汇总表，工资汇总表按照一级单位和二级单位分组汇总显示时，当二级单位为空一级单位有值时，一列一级单位&二级单位不显示。
					{
						temp_sql.append(" case when "+s_groupItem+" is null then "+f_groupItem+" else "+s_groupItem+" end as "+s_groupItem+",");
					}
					else
						temp_sql.append(s_groupItem+",");
				}
				temp_sql.append("count(a0100) num ,");
				if(!isHaveE0122)
					temp_sql.append(" max(e0122) as e0122,");
				if(!isHaveB0110)
					temp_sql.append(" max(b0110) as b0110, ");
				temp_sql.append("max(a0000) as a0000,max(dbid) as dbid ");
				temp_sql.append(" from "+this.gz_tablename+" where 1=1 "+(noManagerFilterSql==null?"":noManagerFilterSql)+(empfiltersql==null?"":empfiltersql)+getwhl1(codeitemid,codesetid,condid,privCodeValue)+this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName));
				if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
					temp_sql.append(" and "+spSQL);
				temp_sql.append(buf_b);
				temp_sql.append("  group by  ");
				String temp="";
				if(s_groupItem!=null&&s_groupItem.length()>0)
					temp+=","+s_groupItem;
				if(f_groupItem!=null&&f_groupItem.length()>0)
					temp+=","+f_groupItem;
				temp_sql.append(temp.substring(1));
				if(groupValues!=null&&groupValues.length()>0)
				{
						String[] temps=groupValues.split(",");
						StringBuffer buffer2=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								/* 薪资发放/薪资报表-工资汇总表，打开时，分组范围 空指针异常 xiaoyun 2014-9-29 start */
								//String[] temps_2=temps[i].split("/");
								String[] temps_2 = PubFunc.keyWord_reback(temps[i]).split("/");
								/* 薪资发放/薪资报表-工资汇总表，打开时，分组范围 空指针异常 xiaoyun 2014-9-29 end */
								if("E0122".equalsIgnoreCase(f_groupItem))
								{
									if("UN".equalsIgnoreCase(temps_2[0]))
										continue;
								}
								buffer2.append(",'"+temps_2[1]+"'");
							}
						}
						if(buffer2.toString().trim().length()>0)
				    		temp_sql.append(" having "+f_groupItem+" in ("+buffer2.substring(1)+")");
				}
				sql=temp_sql.toString();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return sql;
	}
	
	/**
	 * 取得分组汇总记录
	 * @param rsid
	 * @param rsdtlid
	 * @param codeitemid
	 * @param codesetid
	 * @param condid
	 * @param groupValues
	 * @param groupBean
	 * @param tabHeadList
	 * @return
	 */
	public HashMap getGroupSumRecord(String rsid,String rsdtlid,String codeitemid,String codesetid,String condid,String groupValues,LazyDynaBean groupBean,ArrayList tabHeadList,String noManagerFilterSql,String empfiltersql,String priv_mode,String role,String privCode,String privCodeValue,String manager,String userName,String model,String spSQL)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String f_groupItem=(String)groupBean.get("f_groupItem");
			String f_codesetid=(String)groupBean.get("f_codesetid");
			StringBuffer buf=new StringBuffer("");
			LazyDynaBean headBean=null;
			
			for(int i=0;i<tabHeadList.size();i++)
			{
				headBean=(LazyDynaBean)tabHeadList.get(i);
				String aitemid=(String)headBean.get("itemid");
				if("a00z1".equalsIgnoreCase(aitemid)|| "a00z3".equalsIgnoreCase(aitemid))
					buf.append(",max("+aitemid+") "+aitemid);
				else
				    buf.append(",sum("+aitemid+") "+aitemid);
				
			}
			
			String whl="";
			if(groupValues!=null&&groupValues.length()>0)
			{
				SalaryTemplateBo gzbo=new SalaryTemplateBo(conn, Integer.parseInt(salaryid), this.userView);
				String deptid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
				deptid = deptid != null ? deptid : ""; 
				String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
				orgid = orgid != null ? orgid : "";
				
					String[] temps=groupValues.split(",");
					StringBuffer buffer2=new StringBuffer("");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
						{
							/* 薪资发放/薪资报表-工资汇总表，打开时，分组范围 空指针异常 xiaoyun 2014-9-29 start */
							//String[] temps_2=temps[i].split("/");
							String[] temps_2 = PubFunc.keyWord_reback(temps[i]).split("/");
							/* 薪资发放/薪资报表-工资汇总表，打开时，分组范围 空指针异常 xiaoyun 2014-9-29 end */
							if("E0122".equalsIgnoreCase(f_groupItem))
							{
								if("UN".equalsIgnoreCase(temps_2[0]))
									continue;
							}
							buffer2.append(",'"+temps_2[1]+"'");
						}
					}
					if(buffer2.toString().trim().length()>0){
		    			
		    			if("E0122".equalsIgnoreCase(f_groupItem)&&StringUtils.isNotBlank(deptid))
		    				whl=" and ("+f_groupItem+" in ("+buffer2.substring(1)+")  or "+deptid+" in ("+buffer2.substring(1)+")) ";
		    			else if("B0110".equalsIgnoreCase(f_groupItem)&&StringUtils.isNotBlank(orgid))
		    				whl=" and ("+f_groupItem+" in ("+buffer2.substring(1)+")  or "+orgid+" in ("+buffer2.substring(1)+")) ";
		    			else
		    				whl=" and "+f_groupItem+" in ("+buffer2.substring(1)+") ";
					}
				//	temp_sql.append(" having "+f_groupItem+" in ("+buffer2.substring(1)+")  or "+f_groupItem+" is null ");
			}
			
			StringBuffer buf_b = new StringBuffer();
			if("1".equals(role))//超级用户，不用加权限
			{
				
			}else if("3".equals(model))
			{
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				String _sql = this.getPrivSQL(role, this.gz_tablename+".", salaryid, b_units);
				buf_b.append(" and ");
				buf_b.append(_sql);
			}
			StringBuffer temp_sql=new StringBuffer("");
			String tp="";
			if(buf.toString().length()>0)
				tp=buf.substring(1)+",";
			temp_sql.append("select "+tp);
			temp_sql.append("count(a0100) num,"+f_groupItem);
			temp_sql.append(" from "+this.gz_tablename+" where 1=1 "+buf_b+" "+(noManagerFilterSql==null?"":noManagerFilterSql)+(empfiltersql==null?"":empfiltersql)+this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName)+getwhl1(codeitemid,codesetid,condid,privCodeValue)+whl);
			if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
				temp_sql.append(" and "+spSQL+" and salaryid="+this.salaryid);
			temp_sql.append(" group by ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				temp_sql.append(" rollup ("+f_groupItem+")");
			}
			else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
			{			
				temp_sql.append(f_groupItem+" with rollup");
			}
	
			
			
			RowSet rowSet=dao.search(temp_sql.toString());
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String itemtype=(String)headBean.get("itemtype");
					String itemfmt=(String)headBean.get("itemfmt");
					
					if(rowSet.getString(aitemid)!=null)
					{
						if("N".equals(itemtype))
						{
							if(Float.parseFloat( rowSet.getString(aitemid))==0)
								abean.set(aitemid, "");
							else
							{
								if(itemfmt==null||itemfmt.length()==0)
									abean.set(aitemid,rowSet.getString(aitemid));
								else
								{
									DecimalFormat myformat =null;
									if(itemfmt.indexOf(".")==-1)
									{
										abean.set(aitemid,PubFunc.round(rowSet.getString(aitemid), 0));
									}
									else
									{
										if(itemfmt.substring(2).length()==1)
											myformat = new DecimalFormat("0.0");
										else if(itemfmt.substring(2).length()==2)
											myformat = new DecimalFormat("0.00");
										else if(itemfmt.substring(2).length()==3)
											myformat = new DecimalFormat("0.000");
										else if(itemfmt.substring(2).length()==4)
											myformat = new DecimalFormat("0.0000");
										abean.set(aitemid,myformat.format(rowSet.getDouble(aitemid)));
									}
								}
								//abean.set(aitemid, rowSet.getString(aitemid));
							}
						}
						else
							abean.set(aitemid, rowSet.getString(aitemid));
					}
					else
						abean.set(aitemid,"");
				}
				if(rowSet.getString("num")!=null&&Float.parseFloat(rowSet.getString("num"))!=0)
					abean.set("num",rowSet.getString("num")); //人数
				else
					abean.set("num",""); //人数
				String mapKey="-1";
				if(rowSet.getString(f_groupItem)!=null)
					mapKey=rowSet.getString(f_groupItem);
				if(rowSet.getString(f_groupItem)!=null)
				{
					
					if("UN".equalsIgnoreCase(f_codesetid)|| "UM".equalsIgnoreCase(f_codesetid))
					{
						String desc=AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem));
						if((desc==null|| "".equals(desc))&& "UN".equalsIgnoreCase(f_codesetid))
							desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
						if((desc==null|| "".equals(desc))&& "UM".equalsIgnoreCase(f_codesetid))
							desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
						abean.set(f_groupItem,desc);
					}
					else	
				    	abean.set(f_groupItem,AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem)));
				}
				else
					abean.set(f_groupItem,"总计");
				abean.set("isCollect","1");
				map.put(mapKey,abean);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		
		
		return map;
	}
	
	
	/**
	 * 取得工资汇总表的分组信息
	 * @param rsid
	 * @param rsdtlid
	 * @return
	 */
	public LazyDynaBean getGroupBean(String rsid,String rsdtlid)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
		    String f_groupItem="";
		    String s_groupItem="";
		    String f_groupDesc="";
		    String s_groupDesc="";
		    String f_codesetid="";
		    String s_codesetid="";

		    LazyDynaBean aabean=getGzDetailBydID(rsdtlid);
			f_groupItem=(String)aabean.get("fgroup");
			s_groupItem=(String)aabean.get("sgroup");
			if(f_groupItem!=null&& "b0110".equalsIgnoreCase(f_groupItem))
					f_codesetid="UN";
			else if(f_groupItem!=null&& "e0122".equalsIgnoreCase(f_groupItem))
					f_codesetid="UM";
				
			if(s_groupItem!=null&& "b0110".equalsIgnoreCase(s_groupItem))
					s_codesetid="UN";
			else if(s_groupItem!=null&& "e0122".equalsIgnoreCase(s_groupItem))
					s_codesetid="UM";
				
			if(f_groupItem!=null&&f_groupItem.length()>0&&!"UN".equalsIgnoreCase(f_groupItem)&&!"UM".equalsIgnoreCase(f_groupItem))
		    {
		    		FieldItem item=DataDictionary.getFieldItem(f_groupItem.toLowerCase());
		    		f_codesetid=item.getCodesetid();
		    		f_groupDesc=item.getItemdesc();
		    }
		    if(s_groupItem!=null&&s_groupItem.length()>0&&!"UN".equalsIgnoreCase(s_groupItem)&&!"UM".equalsIgnoreCase(s_groupItem))
		    {
		    		FieldItem item=DataDictionary.getFieldItem(s_groupItem.toLowerCase());
		    		s_codesetid=item.getCodesetid();
		    		s_groupDesc=item.getItemdesc();
		    }
		
		    abean.set("f_groupItem", f_groupItem);
		    abean.set("s_groupItem", s_groupItem);
		    abean.set("f_codesetid", f_codesetid);
		    abean.set("s_codesetid", s_codesetid);
		    abean.set("f_groupDesc",f_groupDesc);
		    abean.set("s_groupDesc", s_groupDesc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return abean;
	}
	
	
	
	
	public void resetGroupName(LazyDynaBean abean,RowSet rowSet,LazyDynaBean groupBean)
	{
		try
		{
			String f_groupItem=(String)groupBean.get("f_groupItem");
		    String s_groupItem=(String)groupBean.get("s_groupItem");
		    String f_codesetid=(String)groupBean.get("f_codesetid");
		    String s_codesetid=(String)groupBean.get("s_codesetid");
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
		    if(s_groupItem.length()>0)
		    {
		    	if("UM".equalsIgnoreCase(s_codesetid)|| "UN".equalsIgnoreCase(s_codesetid))
		    	{
		    		String desc="";
		    		if("UN".equalsIgnoreCase(s_codesetid))
		    		{
		    			desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    			if(desc==null|| "".equals(desc))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    		}
		    		else
		    		{
		    			CodeItem ci = AdminCode.getCode("UM", rowSet.getString(s_groupItem), Integer.parseInt(display_e0122));
		    			if(ci!=null)
		    			{
		    				desc=ci.getCodename();
		    			}
		    			else
		    			{
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    				if(desc==null|| "".equals(desc))
		    					desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    			}
		    		}
		    	/*	AdminCode.getCodeName(s_codesetid, rowSet.getString(s_groupItem));
		    		if(desc==null||desc.equals(""))
		    		{
		    			if(s_codesetid.equalsIgnoreCase("UN"))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    			else
		    				desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    		}*/
		    		abean.set(f_groupItem,desc);
		    	}
		    	else
		    	{
		        	abean.set(f_groupItem,AdminCode.getCodeName(s_codesetid, rowSet.getString(s_groupItem)));
		    	}
		    }
		    else
		    {
				if("UM".equalsIgnoreCase(f_codesetid)|| "UN".equalsIgnoreCase(f_codesetid))
		    	{
		    		String desc="";
		    		if("UN".equalsIgnoreCase(f_codesetid))
		    		{
		    			desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
		    			if(desc==null|| "".equals(desc))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
		    		}
		    		else
		    		{
		    			CodeItem ci = AdminCode.getCode("UM", rowSet.getString(f_groupItem), Integer.parseInt(display_e0122));
		    			if(ci!=null)
		    			{
		    				desc=ci.getCodename();
		    			}
		    			else
		    			{
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
		    				if(desc==null|| "".equals(desc))
		    					desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
		    			}
		    		}
		    	/*	AdminCode.getCodeName(s_codesetid, rowSet.getString(s_groupItem));
		    		if(desc==null||desc.equals(""))
		    		{
		    			if(s_codesetid.equalsIgnoreCase("UN"))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    			else
		    				desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    		}*/
		    		abean.set(f_groupItem,desc);
		    	}
				else
		    	{
		        	abean.set(f_groupItem,AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem)));
		    	}
		    }
		    
		    abean.set("isCollect","0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 取得表类(数字类 or 代码)类型报表项目(人员结构工资分析表)
	 * @param  N / A
	 * @return
	 */
	public Vector getReportItemVector(String flag)
	{
		Vector reportItemVector=new Vector();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if("N".equals(flag))
				sql="select itemid,itemdesc from salaryset where salaryid="+this.salaryid+" and itemtype='N' and UPPER(itemid)<>'A0000' and UPPER(itemid)<>'A00Z1'  order by sortid";
			if("A".equals(flag))
				sql="select itemid,itemdesc from salaryset where salaryid="+this.salaryid+" and itemtype='A' and codesetid<>'0'  order by sortid";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				if("0".equals(this.userView.analyseFieldPriv(rowSet.getString("itemid"))))
					continue;
				abean=new LazyDynaBean();
				abean.set("value",rowSet.getString("itemid"));
				abean.set("name",rowSet.getString("itemdesc"));
				reportItemVector.addElement(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return reportItemVector;
	}
	
	/**
	 *  取得人员结构分析表表头描述
	 * @return
	 */
	public ArrayList getGzAnalyseHeadDescList(String groupItemid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{	
			LazyDynaBean abean=new LazyDynaBean();
			abean.set("itemid",groupItemid);
			String itemdesc="";
			String codesetid="";
			if("e0122".equalsIgnoreCase(groupItemid))
			{
				itemdesc="部门";
				codesetid="UM";
			}
			else if("b0110".equalsIgnoreCase(groupItemid))
			{
				itemdesc="单位";
				codesetid="UN";
			}
			else 
			{
				FieldItem item=DataDictionary.getFieldItem(groupItemid.toLowerCase());
				itemdesc=item.getItemdesc();
				codesetid=item.getCodesetid();
			}
			abean.set("itemtype", "A");
			abean.set("itemdesc",itemdesc);
			abean.set("itemfmt","");
			abean.set("codesetid",codesetid);
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","countMen");
			abean.set("itemdesc","人数");
			abean.set("itemtype", "N");
			abean.set("itemfmt","0");
			abean.set("codesetid","0");
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","sumNumber");
			abean.set("itemtype", "N");
			abean.set("itemdesc","总额");
			abean.set("itemfmt","0.00");
			abean.set("codesetid","0");
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","percent");
			abean.set("itemtype", "A");
			abean.set("itemdesc","比例（%）");
			abean.set("itemfmt","");
			abean.set("codesetid","0");
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","average");
			abean.set("itemtype", "N");
			abean.set("itemdesc","平均值");
			abean.set("itemfmt","0.00");
			abean.set("codesetid","0");
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","minNumber");
			abean.set("itemtype", "N");
			abean.set("itemdesc","最低值");
			abean.set("itemfmt","0.00");
			abean.set("codesetid","0");
			list.add(abean);
			abean=new LazyDynaBean();
			abean.set("itemid","maxNumber");
			abean.set("itemdesc","最高值");
			abean.set("itemfmt","0.00");
			abean.set("itemtype", "N");
			abean.set("codesetid","0");
			list.add(abean);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return list;
	}
	
	
	/**
	 * 取得人员结构工资分析表数据
	 * @param rsid		 4：人员结构工资分析表
	 * @param codeitemid
	 * @param codesetid
	 * @param userName
	 * @param condid   过滤条件
	 * @param itemid   分析指标(人员结构工资分析表)
	 * @param baseid   分组指标（人员结构工资分析表）
	 * @return
	 */
	public ArrayList getGzAnalyseList(String rsid,String codeitemid,String codesetid,String userName,String condid,String itemid,String baseid,String noManagerFilterSql,String empfiltersql,String role,String privCode,String privCodeValue,String privDb,String priv_mode,String manager,String model,String spSQL)
	{
		ArrayList list=new ArrayList();
		this.mode=model;
		if("0".equals(model))
		{
    		if(manager.length()==0||manager.equalsIgnoreCase(userName))
    	    	this.gz_tablename=userName+"_salary_"+this.salaryid;
    		else
	    		this.gz_tablename=manager+"_salary_"+this.salaryid;
		}
		else if("1".equals(model))
		{
			this.gz_tablename="salaryhistory";
		}
		else if("3".equals(model))
			this.gz_tablename="salaryarchive";
		try
		{
			StringBuffer buf_b = new StringBuffer();
			if("1".equals(role))//超级用户，不用加权限
			{
				
			}else if("3".equals(model))
			{
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				String _sql = this.getPrivSQL(role, "", salaryid, b_units);
				buf_b.append(" and ");
				buf_b.append(_sql);
			}
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList headList=getGzAnalyseHeadDescList(baseid);
			double sumNumber=0d;
			String a_sql="select count(a0100)  from "+this.gz_tablename+" where 1=1 "+buf_b+" "+(noManagerFilterSql==null?"":SafeCode.decode(noManagerFilterSql))+(empfiltersql==null?"":SafeCode.decode(empfiltersql))+getwhl1(codeitemid,codesetid,condid,privCodeValue)+this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName);
			if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
				a_sql+=" and "+spSQL;
			RowSet rowSet=dao.search(a_sql);
			if(rowSet.next())
				sumNumber=rowSet.getDouble(1);
			
			StringBuffer sql=new StringBuffer("select "+baseid+",count(a0100),sum("+itemid+"),");
			if(sumNumber!=0)
				sql.append("count(a0100)/"+sumNumber);
			else 
				sql.append("0");
			sql.append(",sum("+itemid+")/count(a0100),min("+itemid+"),max("+itemid+")");
			sql.append(" from "+this.gz_tablename+" where 1=1 "+buf_b+" "+(noManagerFilterSql==null?"":SafeCode.decode(noManagerFilterSql))+(empfiltersql==null?"":SafeCode.decode(empfiltersql)));
			sql.append(getwhl1(codeitemid,codesetid,condid,privCodeValue));
			sql.append(this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName));
			if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
				sql.append(" and "+spSQL);
			if("1".equals(model)|| "3".equals(model)|| "2".equals(model))
				sql.append(" and salaryid="+this.salaryid);
			if (Sql_switcher.searchDbServer() == Constant.ORACEL
					|| Sql_switcher.searchDbServer() == Constant.DB2) {
				sql.append(" group by rollup(" + baseid+ ")");
			} else {
				sql.append(" group by " + baseid+ " with rollup ");
			}
			rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
			LazyDynaBean headbean=null;
			DecimalFormat myformat = new DecimalFormat("0.00");
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			 String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			 if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				 display_e0122="0";
			 int num=0;
			while(rowSet.next())
			{
				num++;
				abean=new LazyDynaBean();
				for(int i=0;i<headList.size();i++)
				{
					headbean=(LazyDynaBean)headList.get(i);
					String a_itemid=(String)headbean.get("itemid");
					String a_itemdesc=(String)headbean.get("itemdesc");
					String a_codesetid=(String)headbean.get("codesetid");
					if(i==0)
					{
						if(rowSet.getString(i+1)!=null)
						{
							if("UM".equalsIgnoreCase(a_codesetid))
							{
								CodeItem item=AdminCode.getCode("UM",rowSet.getString(i+1),Integer.parseInt(display_e0122));
			    				if(item!=null)
			    				{
			    					abean.set(a_itemid,item.getCodename());
			     				}else
				    			{
			     					abean.set(a_itemid,AdminCode.getCodeName("UM",rowSet.getString(i+1)));
				    			}
							}
							else
							{
				    			abean.set(a_itemid, AdminCode.getCodeName(a_codesetid,rowSet.getString(i+1)));
							}
						}
						else if(rowSet.isLast())
							abean.set(a_itemid,"合计");
						else
						{
							abean.set(a_itemid,"");
						}
					}
					else if(i==3)
					{
						if(rowSet.getString(i+1)!=null&&rowSet.getDouble(i+1)!=0)
						{
							BigDecimal b=new BigDecimal(rowSet.getString(i+1));
							BigDecimal hundred = new BigDecimal("100");
							BigDecimal one = new BigDecimal("1");
							abean.set(a_itemid,b.multiply(hundred).divide(one,2,BigDecimal.ROUND_HALF_UP).toString()+"%");
							
							
						}
						else
							abean.set(a_itemid,"");
					}
					else
					{
						if(rowSet.getString(i+1)!=null)
						{
							//abean.set(a_itemid,new Double(rowSet.getString(i+1)));
							abean.set(a_itemid,rowSet.getString(i+1));
						}
						else
							abean.set(a_itemid,"");
					}
				}
				list.add(abean);
			}
			this.recordRows=num-1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public LazyDynaBean getGroupBean2(String itemid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			/*abean.set("itemid","maxNumber");
			abean.set("itemdesc","最高值");
			abean.set("itemfmt","0.00");
			abean.set("itemtype", "N");
			abean.set("codesetid","0");*/
			String sql = "select s.* from salaryset s where UPPER(s.itemid) = '"+itemid.toUpperCase()+"'  and s.salaryid="+this.salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("itemid",itemid);
				bean.set("itemdesc",rs.getString("itemdesc"));
				bean.set("itemfmt","");
				bean.set("itemtype", rs.getString("itemtype"));
				bean.set("codesetid",rs.getString("codesetid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	private int recordRows=0;
	public int getRecordRows()
	{
		return this.recordRows;
	}
	/**
	 *  取得工资报表数据记录 
	 * @param rsid  1:工资条 2：签名表  3：汇总表  4：人员结构工资分析表
	 * @param rsdtlid
	 * @param codeitemid 机构树代码值
	 * @param codesetid  机构树代码
	 * @param userName 登录用户名
	 * @param condid 过滤条件
	 * @param groupValues  分组值
	 * @param orderSql 排序语句
	 * @param empfiltersql 过滤语句
	 * @param role =1超级用户，
	 * @param privCode 管理范围代码
	 * @param privCodeValue 管理范围值
	 * @param privDb 权限内的应用库
	 * @param priv_mode =1薪资类别需要权限过滤，=0不需要
	 * @return
	 */
	public ArrayList getRecordList(String rsid,String rsdtlid,String codeitemid,String codesetid,String userName,String condid,String groupValues,String orderSql,String noManagerFilterSql,String empfiltersql,String role,String privCode,String privCodeValue,String privDb,String priv_mode,String manager,String model,String spSQL,SalaryTemplateBo gzbo)
	{
		ArrayList aList=new ArrayList();
		
		String default_order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
		ArrayList list=new ArrayList();
		this.mode=model;
		if("0".equals(model))
		{
	    	if(manager.length()==0||userName.equalsIgnoreCase(manager))
	    	{
	    		this.gz_tablename=userName+"_salary_"+this.salaryid;
    		}
    		else
	    	{
	    		this.gz_tablename=manager+"_salary_"+this.salaryid;
    		}
		}
		else if("1".equals(model))
		{
			this.gz_tablename="salaryhistory";
		}
		else if("3".equals(model))
		{
			this.gz_tablename="salaryarchive";
		}
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			this.reportdetailvo=new RecordVo("reportdetail");
			this.reportdetailvo.setInt("rsid",Integer.parseInt(rsid));
			this.reportdetailvo.setInt("rsdtlid",Integer.parseInt(rsdtlid));
			this.reportdetailvo=dao.findByPrimaryKey(this.reportdetailvo);
		
		    LazyDynaBean groupBean=null;
		    if("3".equals(rsid)|| "13".equals(rsid))
			    	groupBean=getGroupBean(rsid,rsdtlid);
			ArrayList tabHeadList=getTableHeadDescList(rsid,rsdtlid);
			HashMap  groupSumRecords=new HashMap();    //分组汇总数据
			if("3".equals(rsid)|| "13".equals(rsid))
				groupSumRecords=getGroupSumRecord(rsid,rsdtlid,codeitemid,codesetid,condid,groupValues,groupBean,tabHeadList,SafeCode.decode(noManagerFilterSql),SafeCode.decode(empfiltersql),priv_mode,role,privCode,privCodeValue,manager,userName,model,spSQL);
			RowSet rowSet=null;
			StringBuffer buf_b = new StringBuffer("");
			if("1".equals(role))//超级用户，不用加权限
			{
				
			}else if("3".equals(model))
			{
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				String _sql = this.getPrivSQL(role, "", salaryid, b_units);
				buf_b.append(" and ");
				buf_b.append(_sql);
			}
			
//			{
//				if(manager.length()==0||manager.equalsIgnoreCase(userName))//非共享类别，或者当前用户为管理员
//				{
//					
//				}
//				else
//				{
//					if(this.mode.equals("3"))
//					{
//						if((manager.trim().length()>0&&!manager.equalsIgnoreCase(userName)));
//						{
//							if(this.unit_sql!=null&&this.unit_sql.trim().length()>0)
//							{
//								buf_b.append(" and ("+this.unit_sql+")");
//							}
//						}
//					}
//					else if(this.mode.equals("0")&&(this.orgid.length()>0||this.deptid.length()>0)&&this.controlByUnitcode.equals("1"))//工资发放中(只发放中这样)，当设置了归属单位指标或者设置了归属部门指标，按这个规则走
//					{
//						String privSql = this.getWhlByUnits();
//						buf_b.append(privSql);
//					}
//					else
//					{
//	        			/**需要权限过滤*/
//	        			if((manager.trim().length()>0&&!manager.equalsIgnoreCase(userName)))
//	        			{
//	             			if(privCode!=null&&!privCode.equals(""))
//	            			{
//	             				if(privCode.equalsIgnoreCase("CZDW"))
//	    		    			{
//	    		    				/**不为全部时加限制*/
//	    		    				if(privCodeValue.length()!=3)
//	    		    				{
//	    		    		    		String[] arr = privCodeValue.split("`");
//	    		    		    		buf_b.append(" and (");
//	    		    		    		StringBuffer t_buf = new StringBuffer();
//	    		    			    	for(int i=0;i<arr.length;i++)
//	    		    		    		{
//	    		    			    		String temp = arr[i];
//	    		    				    	if(arr[i]==null||arr[i].equals(""))
//	    		    				    		continue;
//	    		    				    	if(temp.substring(0, 2).equalsIgnoreCase("UN"))
//	    		    				    		t_buf.append(" or b0110 ");
//	    		    			    		else
//	    		    					    	t_buf.append(" or e0122 ");
//	    		    				    	t_buf.append(" like '"+arr[i].substring(2)+"%'");
//	    		    		    		}
//	    		    			    	buf_b.append(t_buf.toString().substring(3)+")");
//	    		    				}
//	    		    			}
//	             				else if(privCode.equalsIgnoreCase("UN"))
//	             				{
//	    		        			buf_b.append(" and (b0110 like '"+(privCodeValue==null?"":privCodeValue)+"%'");
//	    			        		if(privCodeValue==null)
//	    		        				buf_b.append(" or b0110 is null");
//	    			        		buf_b.append(")");
//		            			}
//		            			if(privCode.equalsIgnoreCase("UM"))
//		    	         		{
//			            			buf_b.append(" and (e0122 like '"+(privCodeValue==null?"":privCodeValue)+"%'");
//		    		        		if(privCodeValue==null)
//		    		          			buf_b.append(" or e0122 is null");
//		    			        	buf_b.append(")");
//		    	        		}
//		            			if(privCode.equalsIgnoreCase("@K"))
//		    	        		{
//		    				
//		    	        		}
//		             			if(privDb!=null&&!privDb.equals(""))
//		            			{
//		             				String[] tt=privDb.split("#");
//		    	        			for(int j=0;j<tt.length;j++)
//		    	        			{
//			    		        		if(j==0)
//			    	        				buf_b.append(" and (");
//			    		        		else
//			    	        				buf_b.append(" OR ");
//			    		        		buf_b.append(" UPPER(nbase)='");
//			    	        			buf_b.append(tt[j].toUpperCase());
//			    	        			buf_b.append("'");
//                                        if(j==tt.length-1)
//                                        {
//                                        	buf_b.append(")");
//                                        }
//	    			        		}
//		    		        	}
//	     		         		else
//	    		        		{
//	     		        			buf_b.append(" and 1=2 ");
//	    		        		}
//		            		}
//		            		else
//		    	        	{
//	    		         		buf_b.append( " and 1=2 ");
//	    	        		}
//		        		}
//		       		}
//				}
//			}
			if("1".equals(model)|| "3".equals(model)|| "2".equals(model))
				buf_b.append(" and salaryid="+this.salaryid);
			String sql=getReportSql(rsid,rsdtlid,codeitemid,codesetid,condid,groupValues,tabHeadList,groupBean,SafeCode.decode(noManagerFilterSql),SafeCode.decode(empfiltersql),priv_mode,role,privCode,privCodeValue,userName,manager,model,spSQL,buf_b.toString());
			String order_str="";
			String groupField="";
			if(("2".equals(rsid)|| "12".equals(rsid))&&this.reportdetailvo.getInt("bgroup")==1)
			{
				groupField=this.reportdetailvo.getString("fgroup").toLowerCase();
			}
			if(orderSql!=null&&!"".equals(orderSql))
			{
				order_str=orderSql;
				if("3".equals(rsid)|| "13".equals(rsid))
				{
					
			    	String f_groupItem=(String)groupBean.get("f_groupItem");
		   	    	String s_groupItem=(String)groupBean.get("s_groupItem");
				    if(s_groupItem!=null&&s_groupItem.trim().length()>0)
				    {
					   order_str=s_groupItem+","+order_str.toUpperCase().replaceAll((","+s_groupItem.toUpperCase()), "").replaceAll(s_groupItem.toUpperCase(), "");
				    }
				    if(f_groupItem!=null&&f_groupItem.trim().length()>0)
				    {
					   order_str=f_groupItem+","+order_str.toUpperCase().replaceAll((","+f_groupItem.toUpperCase()), "").replaceAll(f_groupItem.toUpperCase(), "");
				    }
				}
				
			}
			else if("3".equals(rsid)|| "13".equals(rsid))
			{
				 String f_groupItem=(String)groupBean.get("f_groupItem");
				 String s_groupItem=(String)groupBean.get("s_groupItem");
				 
				 if(f_groupItem!=null&&f_groupItem.trim().length()>0)
				 {
					 order_str+=f_groupItem+",";
				 }
				 if(s_groupItem!=null&&s_groupItem.trim().length()>0&&order_str.toUpperCase().indexOf(s_groupItem.toUpperCase())==-1)
				 {
					 order_str+=s_groupItem+",";
				 }
				 if(order_str.endsWith(","))
					 order_str = order_str.substring(0,order_str.length()-1);
				
			}
			else 
			{
				if("1".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid))
				{
		    		if(default_order_str!=null&&!"".equals(default_order_str))
		        	{
		        		order_str=default_order_str;
	    	    	}
	    	     	else
	    	    	{
	    	     		order_str="b0110,e0122,dbid,a0000";
		        	}
				}
				else
				{
					order_str="b0110,e0122,dbid,a0000";
				}
			}
			if(order_str.toUpperCase().indexOf(groupField.toUpperCase())!=-1&&groupField!=null&&!"".equals(groupField))
			{
		      
			}
			else if(groupField!=null&&!"".equals(groupField))
			{
				order_str=" "+groupField+","+order_str;
			}
			rowSet=dao.search(sql+" order by "+order_str);		    
			boolean flag=true;
		    int num=0;
		    LazyDynaBean abean=null;
		    SimpleDateFormat df=null;
		    StringBuffer sumStr=new StringBuffer("");
		    boolean bool=false;
		    String group="";
		    //-----------------------------------------------------------------
		    if(("2".equals(rsid)|| "12".equals(rsid))&&this.reportdetailvo.getInt("bgroup")==1)
		    {
		        group=this.reportdetailvo.getString("fgroup").toLowerCase();
		        boolean isB0110=false;
		    	LazyDynaBean t_bean=null;
		    	for(int i=0;i<tabHeadList.size();i++)
		    	{
		    		t_bean = (LazyDynaBean)tabHeadList.get(i);
		    		String itemid=(String)t_bean.get("itemid");
		    		if(itemid.equalsIgnoreCase(group))
		    		{
		    			bool = true;
		    		}else if("b0110".equalsIgnoreCase(itemid))
		    		{
		    			isB0110=true;
		    		}
		    		else
		    		{
		    			continue;
		    		}
		    	}
		    	if(!bool)
		    	{
		    		tabHeadList.add(this.getGroupBean2(group));
		    	}
		    	if("e0122".equalsIgnoreCase(group)&&!isB0110)
		    		tabHeadList.add(this.getGroupBean2("b0110"));
		    }
           //-------------------------------------------------------------------
		    String f_groupValue="key";
		    int a=0;
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			while(rowSet.next()){
				num++;
				LazyDynaBean headBean=new LazyDynaBean();
				abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String aitemtype=(String)headBean.get("itemtype");
					String acodesetid=(String)headBean.get("codesetid");
					String itemfmt=(String)headBean.get("itemfmt");
					String value="";
					if("D".equalsIgnoreCase(aitemtype))
					{
						value=rowSet.getDate(aitemid)==null?"":rowSet.getDate(aitemid).toString();
					}
					else{
						value=rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid);
					}
					if(value==null&& "".equals(value))
					{
						abean.set(aitemid,"");
					}
					else
					{
						if("b0110".equalsIgnoreCase(aitemid))
						{
							String str= rowSet.getString(aitemid)==null?"": rowSet.getString(aitemid);
							if(!"".equals(str)&&(AdminCode.getCodeName("UN", str)==null|| "".equals(AdminCode.getCodeName("UN", str))))
							{
								CodeItem item=AdminCode.getCode("UM",str,Integer.parseInt(display_e0122));
			    				if(item!=null)
			    				{
			    					abean.set(aitemid,item.getCodename());
			     				}else
				    			{
			     					abean.set(aitemid,AdminCode.getCodeName("UM",str));
				    			}
							}
							else
							{
					    		abean.set(aitemid,AdminCode.getCodeName("UN",str));
							}
							abean.set("b0110_value",rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
						}
						else if("e0122".equalsIgnoreCase(aitemid))
						{
							String str= rowSet.getString(aitemid)==null?"": rowSet.getString(aitemid);
							if(!"".equals(str)&&(AdminCode.getCodeName("UM", str)==null|| "".equals(AdminCode.getCodeName("UM", str))))
							{
								abean.set(aitemid,AdminCode.getCodeName("UN",str));
							}
							else
							{
					    		CodeItem item=AdminCode.getCode("UM",str,Integer.parseInt(display_e0122));
		    		    		if(item!=null)
		    		    		{
		    			    		abean.set(aitemid,item.getCodename());
		     		    		}else
			    		    	{
		     			     		abean.set(aitemid,AdminCode.getCodeName("UM",str));
			    	    		}
							}
							abean.set("e0122_value",rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
						}
						else
						{
							if("M".equals(aitemtype))
								abean.set(aitemid,Sql_switcher.readMemo(rowSet,aitemid));
							/*else if(rowSet.getString(aitemid)==null)
								abean.set(aitemid,"");*/
							else
							{
								if("D".equals(aitemtype))
								{
									itemfmt=itemfmt.replaceAll("m","M");
									df=new SimpleDateFormat(itemfmt);
									abean.set(aitemid,rowSet.getDate(aitemid)==null?"":df.format(rowSet.getDate(aitemid)));
								}
								else if("A".equals(aitemtype)&&!"0".equals(acodesetid))
								{
									if("UN".equalsIgnoreCase(acodesetid)|| "UM".equalsIgnoreCase(acodesetid))
									{
								    	String str= rowSet.getString(aitemid)==null?"": rowSet.getString(aitemid);
								    	if("UN".equalsIgnoreCase(acodesetid))
								    	{
								    		String codeitemdesc=AdminCode.getCodeName("UN", str);
								    		if(codeitemdesc==null|| "".equals(codeitemdesc))
								    		{
								    			CodeItem item=AdminCode.getCode("UM",str,Integer.parseInt(display_e0122));
						    		    		if(item!=null)
						    		    		{
						    			    		codeitemdesc=item.getCodename();
						     		    		}else
							    		    	{
						     		    			codeitemdesc=AdminCode.getCodeName("UM", str);
							    	    		}
								    			
								    		}
								    		abean.set(aitemid,codeitemdesc);
								    	}
								    	else
								    	{
								    		String codeitemdesc=AdminCode.getCodeName("UM", str);
								    		if(codeitemdesc==null|| "".equals(codeitemdesc))
								    			codeitemdesc=AdminCode.getCodeName("UN", str);
								    		else
								    		{
								    			CodeItem item=AdminCode.getCode("UM",str,Integer.parseInt(display_e0122));
						    		    		if(item!=null)
						    		    		{
						    			    		codeitemdesc=item.getCodename();
						     		    		}else
							    		    	{
						     		    			codeitemdesc=AdminCode.getCodeName("UM", str);
							    	    		}
								    		}
								    		abean.set(aitemid,codeitemdesc);
								    	}
								 	}
									else
						    			abean.set(aitemid,AdminCode.getCodeName(acodesetid, rowSet.getString(aitemid)));
									//abean.set(aitemid.toLowerCase()+"_value", rowSet.getString(aitemid));
								}
								else if("N".equals(aitemtype))
								{
									if(rowSet.getString(aitemid)==null||Float.parseFloat(rowSet.getString(aitemid))==0)
										abean.set(aitemid,"");
									else
									{
										if(itemfmt==null||itemfmt.length()==0)
											abean.set(aitemid,rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
										else
										{
											DecimalFormat myformat =null;
											if(itemfmt.indexOf(".")==-1)
											{
												abean.set(aitemid,rowSet.getString(aitemid)==null?"":PubFunc.round(rowSet.getString(aitemid), 0));
											}
											else
											{
												if(itemfmt.substring(2).length()==1)
													myformat = new DecimalFormat("0.0");
												else if(itemfmt.substring(2).length()==2)
													myformat = new DecimalFormat("0.00");
												else if(itemfmt.substring(2).length()==3)
													myformat = new DecimalFormat("0.000");
												else if(itemfmt.substring(2).length()==4)
													myformat = new DecimalFormat("0.0000");
												abean.set(aitemid,myformat.format(rowSet.getDouble(aitemid)));
											}
										}
									}
								}
								else 
									abean.set(aitemid,rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
							}
							
							
						}
					}
					if(a==0&& "N".equals(aitemtype))
						sumStr.append(",sum("+aitemid+") "+aitemid);
				}
				a++;
				
				 if("3".equals(rsid)|| "13".equals(rsid))
				 {
					 String f_groupItem=(String)groupBean.get("f_groupItem");
					 String s_groupItem=(String)groupBean.get("s_groupItem");
					 abean.set("num",rowSet.getString("num"));
					 
					 String groupValue=rowSet.getString(f_groupItem);
					 resetGroupName(abean,rowSet,groupBean);
					 if(s_groupItem.length()>0)
					 {
						 if("key".equals(f_groupValue))
							 f_groupValue=groupValue;
						 if(f_groupValue!=null&&groupValue!=null&&!f_groupValue.equals(groupValue))
						 {
							 list.add((LazyDynaBean)groupSumRecords.get(f_groupValue));
							 f_groupValue=groupValue;
						 }
					 }
				 }	
			    list.add(abean);
			}
			this.recordRows=num;
			
			if("3".equals(rsid)|| "13".equals(rsid))  //总计
			{
				if(!"key".equals(f_groupValue)){
					list.add((LazyDynaBean)groupSumRecords.get(f_groupValue));
					list.add((LazyDynaBean)groupSumRecords.get("-1"));
				}
				else if("key".equals(f_groupValue)&&groupSumRecords.get("-1")!=null)
					list.add((LazyDynaBean)groupSumRecords.get("-1"));
			}
			else if(("2".equals(rsid)|| "12".equals(rsid))/*&&sumStr.length()>0*/)  //总计
			{
				StringBuffer p=new StringBuffer("");
				if("1".equals(model)|| "2".equals(model)|| "3".equals(model))
					p.append(" and "+spSQL+" and salaryid="+this.salaryid);
				p.append(buf_b.toString());
				if(("2".equals(rsid)|| "12".equals(rsid))&&this.reportdetailvo.getInt("bgroup")==1)
				{
					list=resetList(sumStr.toString(),list,this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName)+getwhl1(codeitemid,codesetid,condid,privCodeValue)+p.toString(),tabHeadList,SafeCode.decode(empfiltersql));
				}
				else if(sumStr.length()>0)
				{
					StringBuffer buf=new StringBuffer(" from "+this.gz_tablename+" where 1=1 "+p.toString()+(noManagerFilterSql==null?"":SafeCode.decode(noManagerFilterSql))+(empfiltersql==null?"":SafeCode.decode(empfiltersql)));
					buf.append(getwhl1(codeitemid,codesetid,condid,privCodeValue));
					buf.append(this.getwhl2(privCode, privCodeValue, manager, priv_mode, role, userName));
					rowSet=dao.search("select "+sumStr.substring(1)+buf.toString()+p.toString());
					LazyDynaBean headBean=new LazyDynaBean();
					if(rowSet.next())
					{
						abean=new LazyDynaBean();
						for(int i=0;i<tabHeadList.size();i++){
							headBean=(LazyDynaBean)tabHeadList.get(i);
							String aitemid=(String)headBean.get("itemid");
							String aitemtype=(String)headBean.get("itemtype");
							String value="";
							if("N".equals(aitemtype))
							{
								if(rowSet.getString(aitemid)!=null)
									value=rowSet.getString(aitemid);
							}
							abean.set(aitemid,value);
						}
					}
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
	 * 权限限制sql语句
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @param tablename
	 * @return
	 */
	public String getPrivSQL(String role,String tablename,String salaryid,String b_units)
	{
		StringBuffer buf = new StringBuffer("");
		String[] temp = salaryid.split(",");
		if("1".equals(role))//如果是树节点传进来的，那么此处role可传空  role=1 代表超级用户 if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
		{
			buf.append( "  1=1 ");
		}else
		{			
			
	     	HashMap map = new HashMap();
			for (int j= 0; j < temp.length; j++){
				String b0110_item="b0110";
				String e0122_item="e0122";
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(deptid!=null&&deptid.trim().length()>0)//设置了归属部门
				{ 
					e0122_item=deptid;
					if(orgid!=null&&orgid.length()>0)
						b0110_item=orgid;
				}else if(orgid!=null&&orgid.trim().length()>0)//没设置归属部门，只设置了归属单位，走归属单位
				{ 
					b0110_item=orgid;
				}
				String item = (String) map.get(e0122_item+"/"+b0110_item);
		    	if(item!=null&&item.length()>0){
		    		map.put(e0122_item+"/"+b0110_item, item+",'"+temp[j]+"'");
		    	}else{
		    		map.put(e0122_item+"/"+b0110_item, "'"+temp[j]+"'");
		    	}	

			}			
			if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
			{
				String unitarr[] =b_units.split("`");				
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					String[] str = key.toString().split("/");
					buf.append("((");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);							  
							if(privCode!=null&&!"".equals(privCode))
							{		
								buf.append(" ( case");
								if(!"e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//归属单位和部门均设置了
									buf.append("  when  nullif("+tablename+str[0]+",'') is not null  then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(!"e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//设置了归属部门，没设置归属单位
									buf.append("  when nullif("+tablename+str[0]+",'') is not null then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//没设置归属部门，设置了归属单位
									buf.append("  when nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//啥都没设置
									buf.append("  when nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}
							}
	    				}
					}

					if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())){
						String _str = buf.toString();
						buf.setLength(0);
						buf.append(_str.substring(0, _str.length()-3));
						buf.append(" ) ) or");
					}else{
						String _str = buf.toString();
						buf.setLength(0);
						buf.append(_str.substring(0, _str.length()-3));
						buf.append(") ) or");
					}									
				}
				String str = buf.toString();
				buf.setLength(0);
				buf.append("("+str.substring(0, str.length()-3)+")");
			}else if("UN`".equalsIgnoreCase(b_units)){
				buf.append( "  1=1 ");
			}
			else
			{
				buf.append( "  1=2 ");
			}
		}
		return buf.toString();
	}
	
	public static void main(String[] arg)
	{
		DecimalFormat myformat = new DecimalFormat("0.00");
		System.out.println(myformat.format(0.245));
		
	}
	
	
	/**
	 * 当工资发放签名表分组显示时,需分组计算合计行
	 * @param sumStr
	 * @return
	 */
	public ArrayList resetList(String sumStr,ArrayList recordList,String whl,ArrayList tabHeadList,String empfiltersql )
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String group=this.reportdetailvo.getString("fgroup").toLowerCase();
			
			StringBuffer buf=new StringBuffer(" from "+this.gz_tablename+" where 1=1 "+(empfiltersql==null?"":empfiltersql)+" "+whl);
			buf.append(whl);
			boolean isE=false;
			if("e0122".equalsIgnoreCase(group))
			{
				isE=true;
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			//String str="select "+sumStr+","+(isE?"b0110,":"")+group+"  "+buf.toString()+" group by "+(isE?"b0110,":"")+group;
			String atemp="";
			if(sumStr!=null&&!"".equals(sumStr))
				atemp=sumStr.substring(1)+",";
			RowSet rowSet=dao.search("select "+atemp+(isE?"b0110,":"")+group+"  "+buf.toString()+" group by "+(isE?"b0110,":"")+group);
			LazyDynaBean headBean=new LazyDynaBean();
			LazyDynaBean abean=null;
			HashMap map=new HashMap();
			String codesetid="";
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String aitemtype=(String)headBean.get("itemtype");
					String acodesetid=(String)headBean.get("codesetid");
					
					if("b0110".equalsIgnoreCase(aitemid))
						acodesetid="UN";
					if("e0122".equalsIgnoreCase(aitemid))
						acodesetid="UM";
					
					if(codesetid.length()==0&&aitemid.toLowerCase().equals(group.toLowerCase()))
						codesetid=acodesetid;
					
					
					
					String value="";
					if("N".equals(aitemtype))
					{
						if(rowSet.getString(aitemid)!=null)
							value=rowSet.getString(aitemid);
					}
					if(aitemid.equalsIgnoreCase(group))
					{
						if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)){
				    		String codeitemdesc=AdminCode.getCodeName(codesetid, rowSet.getString(aitemid));
				    		if("UN".equalsIgnoreCase(codesetid)){
				    			if(codeitemdesc==null|| "".equals(codeitemdesc))
				    			{
				    				CodeItem item=AdminCode.getCode(codesetid,rowSet.getString(aitemid),Integer.parseInt(display_e0122));
				    				if(item!=null)
				    				{
				    					codeitemdesc=item.getCodename();
				    				}else
				    				{
				    					codeitemdesc=AdminCode.getCodeName(codesetid, rowSet.getString(aitemid));
				    				}
				    			}
				    		}else if("UM".equalsIgnoreCase(codesetid)){
				    			if(codeitemdesc==null|| "".equals(codeitemdesc))
				    				codeitemdesc=AdminCode.getCodeName("UN", rowSet.getString(aitemid));
				    			else
				    			{
				    				CodeItem item=AdminCode.getCode("UM",rowSet.getString(aitemid),Integer.parseInt(display_e0122));
				    				if(item!=null)
				    				{
				    					codeitemdesc=item.getCodename();
				    				}else
				    				{
				    					codeitemdesc=AdminCode.getCodeName("UM", rowSet.getString(aitemid));
				    				}
				    			}
				    		}
				    		value = codeitemdesc;
						}else
							value=AdminCode.getCodeName(codesetid, rowSet.getString(aitemid));
					}
					abean.set(aitemid,value);
				}
				if(isE)
				{
					CodeItem item=AdminCode.getCode("UM",rowSet.getString(group),Integer.parseInt(display_e0122));
    				if(item!=null)
    				{
    					map.put(rowSet.getString("b0110")+"/"+item.getCodename(), abean);
     				}else
	    			{
     					map.put(rowSet.getString("b0110")+"/"+AdminCode.getCodeName(codesetid,rowSet.getString(group)), abean);
	    			}
					
				}
				else{
					String codeitemdesc = "";
					if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)){
						codeitemdesc=AdminCode.getCodeName(codesetid, rowSet.getString(group));
			    		if("UN".equalsIgnoreCase(codesetid)){
			    			if(codeitemdesc==null|| "".equals(codeitemdesc))
			    			{
			    				CodeItem item=AdminCode.getCode(codesetid,rowSet.getString(group),Integer.parseInt(display_e0122));
			    				if(item!=null)
			    				{
			    					codeitemdesc=item.getCodename();
			    				}else
			    				{
			    					codeitemdesc=AdminCode.getCodeName(codesetid, rowSet.getString(group));
			    				}
			    			}
			    		}else if("UM".equalsIgnoreCase(codesetid)){
			    			if(codeitemdesc==null|| "".equals(codeitemdesc))
			    				codeitemdesc=AdminCode.getCodeName("UN", rowSet.getString(group));
			    			else
			    			{
			    				CodeItem item=AdminCode.getCode("UM",rowSet.getString(group),Integer.parseInt(display_e0122));
			    				if(item!=null)
			    				{
			    					codeitemdesc=item.getCodename();
			    				}else
			    				{
			    					codeitemdesc=AdminCode.getCodeName("UM", rowSet.getString(group));
			    				}
			    			}
			    		}
			    		map.put(codeitemdesc, abean);
					}else{
						map.put(AdminCode.getCodeName(codesetid,rowSet.getString(group)), abean);
					}
				}
			}//while loop end
			
			/////////////////////////////////////////////////////////////////////////
			
			HashMap map0=new HashMap();
			if("e0122".equalsIgnoreCase(group))
			{
				StringBuffer buf0=new StringBuffer(" from "+this.gz_tablename+" where 1=1 " +(empfiltersql==null?"":empfiltersql));
				buf0.append(whl);
				rowSet=dao.search("select "+atemp+" b0110 "+buf0.toString()+" group by b0110");
				//System.out.println("sql===  select "+sumStr+",b0110  "+buf0.toString()+" group by b0110");
				abean=null;
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					for(int i=0;i<tabHeadList.size();i++){
						
						headBean=(LazyDynaBean)tabHeadList.get(i);
						String aitemid=(String)headBean.get("itemid");
						String aitemtype=(String)headBean.get("itemtype");
						String acodesetid=(String)headBean.get("codesetid");
						String value="";
						if("N".equals(aitemtype))
						{
							if(rowSet.getString(aitemid)!=null)
								value=rowSet.getString(aitemid);
						}
						if("b0110".equalsIgnoreCase(aitemid))
						{
							value=AdminCode.getCodeName(codesetid, rowSet.getString(aitemid));
						}
						abean.set(aitemid,value);
					}
					map0.put(rowSet.getString("b0110"), abean);
				}	
			}
			
			////////////////////////////////////////////////////////////////////////
			String temp="-1";
			LazyDynaBean a_bean=null;
			LazyDynaBean old_bean=null;
			String old_b0110="";
			String e0122_value="";
			int serial=1;
			for(int i=0;i<recordList.size();i++)
			{
				a_bean=(LazyDynaBean)recordList.get(i);
				String group_value=(String)a_bean.get(group);
				String b0110_value=(String)a_bean.get("b0110_value");
				if("e0122".equalsIgnoreCase(group))
			        e0122_value=(String)a_bean.get("e0122_value");
				if("".equals(old_b0110)&&b0110_value!=null)
					old_b0110=b0110_value;

				if("-1".equals(temp))
				{
					temp=group_value;
					//serial=1;
				}
				if(isE&&temp.equalsIgnoreCase(group_value)&&old_b0110.equalsIgnoreCase(b0110_value))
				{
					a_bean.set("serial",String.valueOf(serial++));
					list.add(a_bean);
				}
				else if(temp.equals(group_value))
				{
					a_bean.set("serial",String.valueOf(serial++));
					list.add(a_bean);
				}
				else
				{
					LazyDynaBean temp_bean=null;
					if(isE)
						temp_bean=(LazyDynaBean)map.get(old_b0110+"/"+temp);
					else
						temp_bean=(LazyDynaBean)map.get(temp);
					if(temp_bean!=null)
					{
						/*
						if(old_bean.get("b0110")!=null)
							temp_bean.set("b0110",(String)old_bean.get("b0110"));
						if(old_bean.get("e0122")!=null)
							temp_bean.set("e0122",(String)old_bean.get("e0122"));
						*/
						
						if("e0122".equalsIgnoreCase(group))
						{
							if(old_bean.get("b0110")!=null)
								temp_bean.set("b0110",(String)old_bean.get("b0110"));
						}
						
						
						
						temp_bean.set("serial","合计");
						list.add(temp_bean);
						
						if("e0122".equalsIgnoreCase(group))
						{
							if(b0110_value!=null&&!b0110_value.equals(old_b0110))
							{
					      		if(map0.get(old_b0110)!=null)
					    		{
					    			LazyDynaBean atemp_bean=(LazyDynaBean)map0.get(old_b0110);
					    			atemp_bean.set("serial","总计");
					    			list.add(atemp_bean);
					    		}	
					    		old_b0110=b0110_value;
							}
						}
						a_bean.set("serial",String.valueOf(serial++));
						list.add(a_bean);
					}else{
						a_bean.set("serial",String.valueOf(serial++));
						list.add(a_bean);
					}
					temp=group_value;
				}
				old_bean=a_bean;
			}
			LazyDynaBean temp_bean=(LazyDynaBean)map.get(temp);
			if(isE)
				temp_bean=(LazyDynaBean)map.get(old_b0110+"/"+temp);
			if(temp_bean!=null)
			{
				if("e0122".equalsIgnoreCase(group))
				{
					if(old_bean.get("b0110")!=null)
						temp_bean.set("b0110",(String)old_bean.get("b0110"));
				}
				
				temp_bean.set("serial","合计");	
				list.add(temp_bean);
			}
			
			
			if("e0122".equalsIgnoreCase(group)&&map0.get(old_b0110)!=null)
			{
				temp_bean=(LazyDynaBean)map0.get(old_b0110);
				temp_bean.set("serial","总计");
				list.add(temp_bean);
			}	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		return list;
	}
	
	
	
	
	/**
	 * 将 List 转换为 byte[]
	 * @param dataList
	 * @param tabHeadLis
	 * @return
	 */
	public byte[] getListToString(ArrayList dataList,ArrayList tabHeadList )
	{
		StringBuffer str=new StringBuffer("");
		LazyDynaBean dataBean=null;
		for(int i=0;i<dataList.size();i++)
		{
			dataBean=(LazyDynaBean)dataList.get(i);
			str.append("`");
			for(int e=0;e<tabHeadList.size();e++){
				LazyDynaBean ffBean=(LazyDynaBean)tabHeadList.get(e);
				String aitemid=(String)ffBean.get("itemid");
				String temp=((String)dataBean.get(aitemid)).trim();
				if(temp.length()==0)
					str.append("^");
				else
					str.append(temp);
				str.append("~");
			}
		}
		String temp="";
		if(str.length()>0)
			temp=str.substring(1);
		return temp.getBytes();
	}
	
	
	

    
    
    
	
	
	
	
	/**
	 * 查询人员过滤条件
	 * @return
	 */
	public ArrayList searchManFilter()
	{
		ArrayList list=new ArrayList();
		try
		{
			LazyDynaBean abean=new LazyDynaBean();
			abean.set("value","all");
			abean.set("name", ResourceFactory.getProperty("label.gz.allman"));
			list.add(abean);			
			String lpro=this.templatevo.getString("lprogram");
			if(!(lpro==null|| "".equalsIgnoreCase(lpro)))
			{
				SalaryLProgramBo lprgbo=new SalaryLProgramBo(lpro,this.userView); //xieguiquan add this.userView 20100828
				ArrayList templist=lprgbo.getServiceItemList();
				for(int i=0;i<templist.size();i++)
				{
					CommonData data=(CommonData)templist.get(i);
					abean=new LazyDynaBean();
					abean.set("value", data.getDataValue());
					abean.set("name", data.getDataName());
					list.add(abean);
				}
				this.condmap=lprgbo.getServiceItemMap();
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 根据报表编号得到工资报表信息
	 * @param rsdtlid
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getGzDetailBydID(String rsdtlid)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from reportdetail where rsdtlid="+rsdtlid;
			RowSet rowSet=dao.search(sql);
			ResultSetMetaData metaData=rowSet.getMetaData();
			int columnCount=metaData.getColumnCount();
			if(rowSet.next())
			{
				
				for(int i=1;i<=columnCount;i++)
				{
					String columnName=metaData.getColumnName(i).toLowerCase();
					if(rowSet.getString(columnName)!=null)
						abean.set(columnName,rowSet.getString(columnName));
					else
						abean.set(columnName,"");
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return abean;
	}
	
	  public String analyseXML(String xml)
	    {
	    	String ownertype="0";
	    	try
	    	{
	    		if(xml==null|| "".equals(xml))
	    		{
	    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
	    		}
	    		Document doc;
	    		 
				doc = PubFunc.generateDom(xml);
				Element root=doc.getRootElement();
		    	XPath xpath=XPath.newInstance("/"+root.getName()+"/owner");
	    		Element element=(Element)xpath.selectSingleNode(doc);
	    		if(element==null)
	    			return ownertype;
	    		else{
	    			ownertype = element.getAttributeValue("type");
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return ownertype;
	    }
	/**
	 * 分组指标列表
	 * @param isEmpty
	 * @return
	 */
	public ArrayList getGroupItemList(String isEmpty,String salaryid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from salaryset where salaryid="+salaryid+" and codesetid<>'0'";
			RowSet rowSet=dao.search(sql);
			if("1".equals(isEmpty))
				list.add(new CommonData("",""));
			while(rowSet.next())
			{
				CommonData data=new CommonData(rowSet.getString("itemid"),rowSet.getString("itemdesc"));
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	/**
	 * 取得薪资报表 项目定义列表
	 * @param salarySetList
	 * @return
	 */
	public ArrayList getReportSalarySet(String queryValue,String stid,String rsdtlid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			StringBuffer exsist=new StringBuffer("");
			ContentDAO dao=new ContentDAO(this.conn);
			if(rsdtlid!=null&&rsdtlid.trim().length()>0)
			{
				String sql="select itemid,itemdesc from reportitem where stid="+this.salaryid+" and rsdtlid="+rsdtlid+" order by sortid";
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
					LazyDynaBean a_bean=new LazyDynaBean();
					a_bean.set("itemid",rowSet.getString("itemid"));
					a_bean.set("itemdesc",rowSet.getString("itemdesc"));
					a_bean.set("isSelected","1");
					list.add(a_bean);
					exsist.append(",'"+rowSet.getString("itemid")+"'");
				}
			}
			LazyDynaBean abean=null;
			String asql = "select * from salaryset where salaryid="+this.salaryid;
			if(queryValue!=null&&!"".equals(queryValue))
				asql+=" and ("+queryValue+")";
		    if(exsist.toString().length()>0)
		    	asql+=" and UPPER(itemid) not in("+exsist.toString().substring(1).toUpperCase()+") ";
		    asql+=" order by sortid";
			rowSet = dao.search(asql); 
			while(rowSet.next()){
				String itemid=(String)rowSet.getString("itemid");
				String itemdesc=(String)rowSet.getString("itemdesc");
				String itemtype=(String)rowSet.getString("itemtype");
				String initflag=(String)rowSet.getString("initflag");
				String isSelected="0";
				if("NBASE".equalsIgnoreCase(itemid)|| "A0100".equalsIgnoreCase(itemid)|| "A0000".equalsIgnoreCase(itemid))
					continue;
				if("2".equals(stid))
				{
					if("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))
							continue;
					
				}
				if(("3".equals(stid)|| "13".equals(stid))&&(!"N".equals(itemtype)|| "A00Z1".equalsIgnoreCase(itemid)))
					continue;
				
				LazyDynaBean a_bean=new LazyDynaBean();
				if(!"3".equals(initflag))
				{
					if(!(userView.isSuper_admin()|| "1".equals(userView.getGroupId())))
			    	{
			    		if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				    		continue;
			    	}
				}
				a_bean.set("itemid",itemid);
				a_bean.set("itemdesc",itemdesc);
				a_bean.set("isSelected",isSelected);
				list.add(a_bean);
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		
		}finally
		{
			if(rowSet!=null)
			{
				try
				{
					rowSet.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	
	
	public String getIsGroup(String str)
	{
		String value="0";
		
		try {
			if(str!=null&&str.trim().length()>0)
			{
				Document doc =PubFunc.generateDom(str);
				XPath xPath = XPath.newInstance("/param/group");
				Element out_fields = (Element) xPath.selectSingleNode(doc);
				if(out_fields!=null)
				{
			    	value=out_fields.getAttributeValue("pagebreak");
			    	if("true".equalsIgnoreCase(value))
			    		value="1";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
	
	/**
	 * 保存或修改 报表定义
	 * @param reportStyleID  表类id
	 * @param reportDetailID 表id
	 * @param f_groupItem    一级分组指标
	 * @param s_groupItem    二级分组指标
	 * @param isPrintWithGroup 是否按分组指标
	 * @param salaryReportName 报表名称
	 * @param right_fields     报表项目
	 * @param isGroup          是否分组打印
	 * @author dengcan
	 * @return
	 */
	public String saveOrUpdateRecord(String reportStyleID,String reportDetailID,String f_groupItem,String s_groupItem,String isPrintWithGroup,String salaryReportName,String[] right_fields,HashMap itemOrderMap,String isGroup,String ownerType,UserView userView)
	{
		String a_reportDetailID="";
		RowSet rowSet=null;
		try
		{
			String opt="edit";
			ContentDAO dao=new ContentDAO(this.conn);
			if("".equals(reportDetailID))
			{
				opt="new";
				reportDetailID=String.valueOf(DbNameBo.getPrimaryKey("reportdetail","rsdtlid",this.conn));  //取得主键值
			}
			RecordVo detailVo=new RecordVo("reportdetail");
			detailVo.setInt("rsid",Integer.parseInt(reportStyleID));
			detailVo.setInt("rsdtlid",Integer.parseInt(reportDetailID));
			detailVo.setInt("stid",Integer.parseInt(this.salaryid));
			detailVo.setString("rsdtlname",salaryReportName);
			detailVo.setInt("leftmargin",20);
			detailVo.setInt("rightmargin",20);
			detailVo.setInt("topmargin",20);
			detailVo.setInt("bottommargin",20);
			 ReportPageOptionsBo rpob=null;
			if("edit".equals(opt))
			{
				detailVo=dao.findByPrimaryKey(detailVo);
				detailVo.setString("rsdtlname",salaryReportName);//修改名称  zhaoxg 2013-6-27
				rpob=new ReportPageOptionsBo(this.conn,userView,reportStyleID,reportDetailID);
				rpob.init();
			}
			
			if(!"1".equals(reportStyleID))
			{
				detailVo.setInt("bgroup",Integer.parseInt(isPrintWithGroup));
				detailVo.setString("fgroup",f_groupItem);
				if("3".equals(reportStyleID)|| "13".equals(reportStyleID))
					detailVo.setString("sgroup",s_groupItem);
				if("0".equals(isPrintWithGroup))
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
                        Element param = new Element("param");
				    	Element onwerT=new Element("owner");
				    	onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	param.addContent(onwerT);
				    	Document myDocument = new Document(param);
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(myDocument);
				    	detailVo.setString("ctrlparam", temp);
					}
				}
				else if(("2".equals(reportStyleID)|| "12".equals(reportStyleID))&& "1".equals(isPrintWithGroup))
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				        group=new Element("group");
			    		if("0".equals(isGroup))
			    			group.setAttribute("pagebreak","False");
			    		else
			    			group.setAttribute("pagebreak","True");
			    		root.addContent(group);
			    		
			    		xpath = XPath.newInstance("/param/leftfooter");
			    		Element leftfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(leftfooter!=null)
			    			root.removeChild("leftfooter");
			    		leftfooter=new Element("leftfooter");
				    	leftfooter.setAttribute("lastpageonly","False");
				    	
				    	xpath = XPath.newInstance("/param/centerfooter");
			    		Element centerfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(centerfooter!=null)
			    			root.removeChild("centerfooter");
		    			centerfooter=new Element("centerfooter");
			    		centerfooter.setAttribute("lastpageonly","False");
			    		
			    		xpath = XPath.newInstance("/param/rightfooter");
			    		Element rightfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(rightfooter!=null)
			    			root.removeChild("rightfooter");
			    		rightfooter=new Element("rightfooter");
			    		rightfooter.setAttribute("lastpageonly","False");
			    		root.addContent(leftfooter);
			    		root.addContent(centerfooter);
			    		root.addContent(rightfooter);
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
				    	Element param = new Element("param");
			    		Element group=new Element("group");
			    		if("0".equals(isGroup))
			    			group.setAttribute("pagebreak","False");
			    		else
			    			group.setAttribute("pagebreak","True");
			    		Element onwerT=new Element("owner");
				    	onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	param.addContent(onwerT);
				    	Element leftfooter=new Element("leftfooter");
				    	leftfooter.setAttribute("lastpageonly","False");
		    			Element centerfooter=new Element("centerfooter");
			    		centerfooter.setAttribute("lastpageonly","False");
			    		Element rightfooter=new Element("rightfooter");
			    		rightfooter.setAttribute("lastpageonly","False");
			    		param.addContent(group);
			    		param.addContent(leftfooter);
			    		param.addContent(centerfooter);
			    		param.addContent(rightfooter);
			    		Document myDocument = new Document(param);
			    		XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
			    		outputter.setFormat(format);
				    	String temp= outputter.outputString(myDocument);
			    		detailVo.setString("ctrlparam", temp);
					}
				}else
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
			    		Element param = new Element("param");
			    		Element onwerT=new Element("owner");
			    		onwerT.setAttribute("type", ownerType);
			    		onwerT.setText(userView.getUserName());
			    		param.addContent(onwerT);
			    		Document myDocument = new Document(param);
			    		XMLOutputter outputter = new XMLOutputter();
			    		Format format = Format.getPrettyFormat();
			    		format.setEncoding("UTF-8");
			    		outputter.setFormat(format);
			    		String temp= outputter.outputString(myDocument);
			    		detailVo.setString("ctrlparam", temp);
					}
				}
			}
			else
			{
				if(rpob!=null)
				{
					Element root = rpob.doc.getRootElement();
					XPath xpath = XPath.newInstance("/param/owner");
					Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
					if(onwerT!=null)
						root.removeChild("owner");
					onwerT=new Element("owner");
					onwerT.setAttribute("type", ownerType);
			    	onwerT.setText(userView.getUserName());
			    	root.addContent(onwerT);
			    	xpath = XPath.newInstance("/param/group");
			    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
			    	if(group!=null)
			    		root.removeChild("group");
			    	XMLOutputter outputter = new XMLOutputter();
			    	Format format = Format.getPrettyFormat();
			    	format.setEncoding("UTF-8");
			     	outputter.setFormat(format);
			    	String temp= outputter.outputString(rpob.doc);
			    	detailVo.setString("ctrlparam", temp);
				}
				else
				{
				    Element param = new Element("param");
					Element onwerT=new Element("owner");
					onwerT.setAttribute("type", ownerType);
					onwerT.setText(userView.getUserName());
					param.addContent(onwerT);
					Document myDocument = new Document(param);
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					String temp= outputter.outputString(myDocument);
					detailVo.setString("ctrlparam", temp);
				    detailVo.setInt("bgroup",0);
				}
			}
			if("new".equals(opt))
				dao.addValueObject(detailVo);
			else
				dao.updateValueObject(detailVo);
			StringBuffer where_str=new StringBuffer("");
			for(int i=0;i<right_fields.length;i++)
				where_str.append(",'"+right_fields[i]+"'");
			HashMap beanMap=new HashMap();
			rowSet = dao.search("select itemid,itemdesc,nwidth,itemfmt,align,sortid from reportitem where  rsdtlid="+reportDetailID+" and stid="+this.salaryid);
			while(rowSet.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemdesc",rowSet.getString("itemdesc"));
				bean.set("nwidth", rowSet.getString("nwidth"));
				bean.set("itemfmt", rowSet.getString("itemfmt")==null?"":rowSet.getString("itemfmt"));
				bean.set("align", rowSet.getString("align"));
				beanMap.put(rowSet.getString("itemid").toUpperCase(), bean);
			}
			dao.delete("delete from reportitem where rsdtlid="+reportDetailID+" and stid="+this.salaryid,new ArrayList());
			rowSet=dao.search("select * from salaryset where salaryid="+this.salaryid+" and UPPER(itemid) in ("+where_str.substring(1).toUpperCase()+") order by sortid");
			ArrayList recordList=new ArrayList();
			int order=1;
			HashMap map = new HashMap();
			while(rowSet.next())
			{
				String itemid=rowSet.getString("itemid").toUpperCase();
				RecordVo vo=new RecordVo("reportitem");
				vo.setInt("rsdtlid",Integer.parseInt(reportDetailID));
				vo.setInt("stid",Integer.parseInt(this.salaryid));
				vo.setString("itemid",rowSet.getString("itemid").toUpperCase());
				int nwidth=0;
				int align=0;
				String itemfmt="";
				String itemdesc="";
				if(beanMap.get(itemid)!=null)
				{
				    LazyDynaBean bean = (LazyDynaBean)beanMap.get(itemid);
				    itemdesc=(String)bean.get("itemdesc");
				    nwidth=Integer.parseInt(((String)bean.get("nwidth")));
				    itemfmt=(String)bean.get("itemfmt");
				    align=Integer.parseInt(((String)bean.get("align")));
				}
				else
				{
			    	String itemtype=rowSet.getString("itemtype");
			    	int    decwidth=rowSet.getInt("decwidth");
			        itemdesc=rowSet.getString("itemdesc");
				    nwidth=rowSet.getInt("nwidth");
			    	if("D".equals(itemtype))
			    	{
			    		align=2;
				    	itemfmt="yyyy.mm.dd";
			    	}
			    	if("N".equals(itemtype))
			    	{
				    	align=2;
				    	itemfmt="0";
				    	if(decwidth>0)
				    	{
				    		itemfmt+=".";
					    	for(int i=0;i<decwidth;i++)
					    		itemfmt+="0";
				    	}
					
		    		}
				}
				vo.setString("itemdesc",itemdesc);
		    	vo.setInt("nwidth",nwidth);
				vo.setString("itemfmt",itemfmt);
				vo.setInt("align",align);
				map.put(rowSet.getString("itemid").toUpperCase(), vo);
			}
			for(int i=0;i<right_fields.length;i++)
			{
				String itid=right_fields[i];
				if(itid==null|| "".equals(itid))
					continue;
				RecordVo vo = (RecordVo)map.get(itid.toUpperCase());
				vo.setInt("sortid",order);
				order++;
				recordList.add(vo);
			}
			dao.addValueObject(recordList);
			a_reportDetailID=reportDetailID;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_reportDetailID;
	}
	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public HashMap getCondmap() {
		return condmap;
	}

	public void setCondmap(HashMap condmap) {
		this.condmap = condmap;
	}

	public RecordVo getReportdetailvo() {
		return reportdetailvo;
	}

	public void setReportdetailvo(RecordVo reportdetailvo) {
		this.reportdetailvo = reportdetailvo;
	}
	/**
	 * 根据职位找部门或者单位
	 * @param codeid
	 * @return
	 */
	public String getUnByPosition(String codeid)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
			if(rowSet.next())
			{
				str=rowSet.getString("codesetid")+rowSet.getString("codeitemid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getSpSQL(UserView view,String boscount,String bosdate,String model)
	{
		StringBuffer sql = new StringBuffer("");
		sql.append(" salaryid="+this.salaryid);
		sql.append(" and A00Z3=");
		sql.append(boscount);
		sql.append(" and A00Z2=");
		sql.append(Sql_switcher.dateValue(bosdate));
		if("1".equals(model))
		{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),view);
			sql.append(" and (( curr_user='"+view.getUserId()+"' ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+view.getUserName()+";%' ) ) ) ");
		}
		//buf.append(" and (( curr_user='"+view.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( AppUser is null or AppUser Like '%;"+view.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) ) ");
		return sql.toString();
	}
	public String getUnit_sql() {
		return unit_sql;
	}
	public void setUnit_sql(String unit_sql) {
		this.unit_sql = unit_sql;
	}
	/**
	 * 按归属单位和归属部门走权限
	 * @return
	 */
	public String getWhlByUnits()
	{
		StringBuffer whl=new StringBuffer(""); 
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		
		String unitcodes=this.userView.getUnitIdByBusiOutofPriv("1");
		if(unitcodes!=null&&("UN".equalsIgnoreCase(unitcodes)||unitcodes.length()==0))
		{
			
		}
		else
		{
//			if(unitcodes==null||unitcodes.length()==0||unitcodes.trim().equalsIgnoreCase("UN"))
//			{
//				String a_code=""; 
//				if(!this.userView.isSuper_admin())
//				{
//					if(this.userView.getManagePrivCode().length()==0)
//						a_code="1=2";
//					else if(this.userView.getManagePrivCode().equals("@K"))
//						a_code=getUnByPosition(this.userView.getManagePrivCodeValue());
//					else
//						a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
//					
//				}
//				
//				if(a_code.equals(""))
//					return "";
//				else if(a_code.equals("1=2"))
//					return " and 1=2 ";
//				else
//					unitcodes=a_code+"`";
//			}
			
			
			String[] temps=unitcodes.split("`");
			if(orgid.trim().length()>0&&deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or "+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(orgid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or "+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
			}
			
		}
		String whl_str="";
		if(whl.length()>0)
			whl_str=" and ( "+whl.substring(3)+" ) ";
		return whl_str;
	}
	
}
