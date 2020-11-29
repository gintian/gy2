package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
/**
 *<p>Title:薪资数据</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2009-9-10:上午09:07:24</p> 
 *@author fanzhiguo
 *@version 4.0
 */
public class SalaryDataBo
{
	private Connection conn=null;
	/**登录用户*/
	private UserView userview=null;
	
	public SalaryDataBo(Connection conn,UserView userview) {
		this.conn=conn;
		this.userview=userview;

	}
	/**
	 * 取得权限范围的薪资列表
	 * 列表中存放是的LazyBean
	 * @return
	 */
	public ArrayList searchGzSetList()throws GeneralException
	{
		
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		SalaryCtrlParamBo ctrlparam=null;
		try
		{
			buf.append("select salaryid,cname,cbase,cond,seq from salarytemplate ");
			buf.append(" where (cstate is null or cstate='' or cstate='1')");//薪资类别 + 保险类别 this.gz_type==0
	
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString()+" order by seq");
			while(rset.next())
			{
				/**加上权限过滤*/
				if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid"))&&!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
				    continue;
				
				LazyDynaBean lazyvo=new LazyDynaBean();
				lazyvo.set("salaryid", rset.getString("salaryid"));
				lazyvo.set("seq",rset.getString("seq")!=null?rset.getString("seq"):"0");
				lazyvo.set("cname", rset.getString("cname"));
				String cond=Sql_switcher.readMemo(rset, "cond");
				String cbase=rset.getString("cbase");
				/**对条件进行转换,转成用户可阅读的格式*/
				lazyvo.set("domain", "["+cbase+"]:["+cond+"]");
				ctrlparam=new SalaryCtrlParamBo(this.conn,rset.getInt("salaryid"));
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user"); //工资管理员，对共享类别有效
		
				/**升级薪资表*/
				LazyDynaBean abean=null;
				if(manager.length()==0)//不共享
					continue;
				else//共享
					abean=searchCurrentDate2(rset.getString("salaryid"),manager);
				String strYm=abean.get("strYm")!=null?(String)abean.get("strYm"):"";
				String strC=abean.get("strC")!=null?(String)abean.get("strC"):"";
				if(strYm.length()>0)
				{
					lazyvo.set("appdate", strYm.substring(0, 7));
					lazyvo.set("count", strC);	
				}
				else  
				{
					lazyvo.set("appdate", "");
					lazyvo.set("count", "");	
				}
				list.add(lazyvo);
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
	 * 薪资类别是否为最近结构
	 * @param salaryid
	 * @return
	 */
	private boolean isNewStruct(int salaryid)
	{
		boolean bflag=false;
		StringBuffer buf=new StringBuffer();
		buf.append("select salaryid from salaryset where itemid=? and salaryid=?");
		ArrayList paralist=new ArrayList();
		paralist.add("A00Z2");
		paralist.add(Integer.valueOf(salaryid));
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 根据当前用户，查找处理的业务日期和次数
	 * 	1.当临时表中有数据时根据数据的发放日期确定
	 *  2。当临时表中无数据，发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 *  3。当临时表中无数据，发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * @param lazyvo
	 * @author dengcan
	 */
	public LazyDynaBean searchCurrentDate2(String salaryid,String username) 
	{
		//String salaryid=(String)(String)lazyvo.get("salaryid");
	//	String username=this.userview.getUserName();
		LazyDynaBean abean=new LazyDynaBean();
		String strYm="";
		String strC="";
		boolean isNull=false;
		RowSet rowSet=null;		
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn); 
			if(dbWizard.isExistTable(username.toLowerCase()+"_salary_"+salaryid, false))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				LazyDynaBean tableBean=getTableInfo(username.toLowerCase()+"_salary_"+salaryid);
			//	RecordVo vo=new RecordVo(username.toLowerCase()+"_salary_"+salaryid);
				

				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null)
					rowSet=dao.search("select A00z2,A00z3 from "+username+"_salary_"+salaryid);
				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null&&rowSet.next())
				{
					strYm=rowSet.getDate("A00Z2")!=null?PubFunc.FormatDate(rowSet.getDate("A00Z2"), "yyyy-MM-dd"):"";
					strC=rowSet.getString("A00Z3")!=null?rowSet.getString("A00Z3"):"";
				}
				else
				{
					if(tableBean.get("a00z2")==null||tableBean.get("a00z3")==null)
					{
						
						DbWizard dbw=new DbWizard(this.conn);
						Table table=new Table(username.toLowerCase()+"_salary_"+salaryid);
						if(tableBean.get("a00z2")==null)
						{
							Field field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
							field.setDatatype(DataType.DATE);
							table.addField(field);
						}
						if(tableBean.get("a00z3")==null)
						{
							Field field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
							field.setDatatype(DataType.INT);
							table.addField(field);
						}
						dbw.addColumns(table);
					}
					
					
					rowSet=dao.search("select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
					if(rowSet.next())
					{
						strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
						strC=rowSet.getString("A00z3");
					}
					else
					{
						rowSet=dao.search("select max(A00z2) A00z2 from gz_extend_log where  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
						if(rowSet.next())
						{
							if(rowSet.getDate("A00z2")!=null)
								strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
							else
								strYm="";
						}
						if("".equalsIgnoreCase(strYm))
						{
							strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
							String[] tmp=StringUtils.split(strYm, "-");
							strYm="";//tmp[0]+"-"+tmp[1]+"-01";
							strC="";//"1";
							//appendExtendLog(strYm,strC,salaryid);
							//lazyvo.set("appdate", "");
							//lazyvo.set("count", "");		
							isNull=true;
						}
						else
						{
							StringBuffer buf=new StringBuffer("select max(A00Z3) A00Z3 from gz_extend_log");
							buf.append(" where salaryid=");
							buf.append(salaryid);
							buf.append(" and ");
							buf.append(" upper(username)='");
							buf.append(username.toUpperCase());
							buf.append("'");
							buf.append(" and A00Z2=");
							buf.append(Sql_switcher.dateValue(strYm));
						    rowSet=dao.search(buf.toString());
							if(rowSet.next())
								strC=rowSet.getString("A00Z3");
						}
						
						
					}
				}
				/*if(!isNull){
					lazyvo.set("appdate", strYm.substring(0, 7));
					lazyvo.set("count", strC);	
				}*/
			}
			abean.set("strYm", strYm);
			abean.set("strC", strC);
			
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
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return abean;
	}
	/**
	 * 取得表结构信息
	 * @param tableName
	 * @return
	 */
	public LazyDynaBean getTableInfo(String tableName)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				abean.set(mt.getColumnName(i+1).toLowerCase(),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
}