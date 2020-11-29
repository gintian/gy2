package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class CashListBo {
	private Connection conn;
	private UserView userview;
	private String order_by;
	private String mode="";//=0薪资发放
	private String   controlByUnitcode="0";  //=1共享类别非管理员按操作单位控制权限
	private String salaryid="";
	private String orgid="";
	private String deptid="";
	public CashListBo(Connection conn)
	{
		this.conn=conn;
	}
	public CashListBo(Connection conn,String mode,String salaryid)
	{
		this.conn=conn;
		this.mode=mode;
		this.salaryid = salaryid;
		
	}
	public CashListBo(Connection conn,UserView userview)
	{
		this.conn=conn;
		this.userview=userview;
	}
	public CashListBo()
	{
		
	}
	/**
	 * 实发工资项目列表(当前薪资类别中的数值型指标)
	 * @param salaryid
	 * @return
	 */
	public ArrayList getGzProjectList(String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid,itemdesc from salaryset where salaryid=");
			sql.append(salaryid);
			sql.append(" and itemtype='N' and UPPER(itemid) not in('A0000','A00Z1','A00Z3') order by sortid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 主体数据的方法
	 * @param code根据单位或部门进行过滤
	 * @param tableName薪资表名
	 * @param itemid 选择的薪资表中的一列,要展现的那列
	 * @param moneyitemlist货币面值等信息
	 * @return
	 */
	public ArrayList getCashList(String code,String tableName,String itemid,ArrayList moneyitemlist,String beforeSql,String filterSql,String privSql,String priv_mode)
	{
		
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if(!(code==null|| "".equalsIgnoreCase(code)))
			{
				String codesetid=code.substring(0,2);
				String codevalue=code.substring(2);
				if("UN".equalsIgnoreCase(codesetid))//单位节点
				{
					    HashMap unitmap =this.getAllUnits(tableName,codevalue,beforeSql,filterSql,priv_mode,privSql);
					    ArrayList personList=null;
					    int q=unitmap.size();
					    for(int p=0;p<unitmap.size();p++)
					    {
					    	int count=0;
					    	int[] unCount= new int[moneyitemlist.size()];
							float unTotal=0.00f;
					    	int[] personCount= new int[moneyitemlist.size()];
					    	float personTotal=0.00f;
					    	String value=(String)unitmap.get(String.valueOf(p));
					    	personList=this.getUnitPerson(tableName,value,itemid,beforeSql,filterSql,privSql);
					    	/**单位下的人员*/
					    	int u=personList.size();
					    	for(int j=0;j<personList.size();j++)
					    	{
					    		LazyDynaBean newpersonBean = new LazyDynaBean();
					    		LazyDynaBean personBean =(LazyDynaBean)personList.get(j);
					    		newpersonBean.set("name",(String)personBean.get("a0101")==null?"":(String)personBean.get("a0101"));
					    		newpersonBean.set("value",GzAnalyseBo.add((String)personBean.get(itemid),"0",2));
					    		HashMap itemMap = getMoneyitem(Float.parseFloat(GzAnalyseBo.add((String)personBean.get(itemid),"0",2)),moneyitemlist);
					    		for(int k = 0;k<itemMap.size();k++)
					    		{
					    			personCount[k]+=Integer.parseInt((String)itemMap.get(String.valueOf(k)));
					    			newpersonBean.set(String.valueOf(k), "0".equalsIgnoreCase((String)itemMap.get(String.valueOf(k)))?"":(String)itemMap.get(String.valueOf(k)));
					    		}
					    		personTotal += Float.parseFloat((String)personBean.get(itemid));
					    		//personTotal = GzAnalyseBo.add(personTotal,(String)personBean.get(itemid),2);
					    		list.add(newpersonBean);
					    		count++;
					    	}
					    	LazyDynaBean unitBean = new LazyDynaBean();
							unitBean.set("name",AdminCode.getCodeName("UN",value)+"(合计)");
							HashMap umMap=getUMMap(value,tableName,dao,beforeSql,filterSql);
							ArrayList personInfoList=null;
							for(int i=0;i<umMap.size();i++)
							{
								String e0122=(String)umMap.get(String.valueOf(i));
								LazyDynaBean umBean = new LazyDynaBean();
								umBean.set("name",AdminCode.getCodeName("UM",e0122)+"(小计)");
								int[] umCount=new int[moneyitemlist.size()];
								float umTotal=0.00f;
								personInfoList=getPersonInfo(e0122,tableName,itemid,dao,beforeSql,filterSql,priv_mode,privSql);
								for(int j=0;j<personInfoList.size();j++)
								{
									LazyDynaBean bean = (LazyDynaBean)personInfoList.get(j);
									LazyDynaBean personBean = new LazyDynaBean();
									personBean.set("name",(String)(bean.get("a0101")==null?"":bean.get("a0101")));
									personBean.set("value",GzAnalyseBo.add((String)bean.get(itemid),"0",2));
									HashMap itemMap=getMoneyitem(Float.parseFloat(GzAnalyseBo.add((String)bean.get(itemid),"0",2)),moneyitemlist);
									for(int k=0;k<itemMap.size();k++)
									{   
										umCount[k]+=Integer.parseInt((String)itemMap.get(String.valueOf(k)));
										personBean.set(String.valueOf(k), "0".equalsIgnoreCase((String)itemMap.get(String.valueOf(k)))?"":(String)itemMap.get(String.valueOf(k)));
									}
									list.add(personBean);
									count++;
									umTotal+=Float.parseFloat((String)bean.get(itemid));
									//umTotal = GzAnalyseBo.add(umTotal, (String)bean.get(itemid), 2);
									
								}
								for(int h=0;h<umCount.length;h++)
								{
									unCount[h]+=umCount[h];
									umBean.set(String.valueOf(h), "0".equalsIgnoreCase(String.valueOf(umCount[h]))?"":String.valueOf(umCount[h]));
								}
								//------------------------------------------
								umBean.set("value",this.getXS(String.valueOf(umTotal),2));
								list.add(list.size()-personInfoList.size(),umBean);
								count++;
								unTotal+=umTotal;
								//unTotal = GzAnalyseBo.add(unTotal, umTotal, 2);
							}
							for(int r=0;r<personCount.length;r++)
							{
								unCount[r]+=personCount[r];
							}
							unTotal+=personTotal;
							//unTotal = GzAnalyseBo.add(unTotal, personTotal, 2);
							for(int s=0;s<unCount.length;s++)
							{
			                    unitBean.set(String.valueOf(s), "0".equalsIgnoreCase(String.valueOf(unCount[s]))?"":String.valueOf(unCount[s]));
							}
							unitBean.set("value",this.getXS(String.valueOf(unTotal),2));
							list.add(p==0?0:list.size()-count,unitBean);
					    	
					    }
				}
				else if("UM".equalsIgnoreCase(codesetid))//部门节点
				{
					ArrayList personInfoList=null;
					personInfoList=getPersonInfo(codevalue,tableName,itemid,dao,beforeSql,filterSql,priv_mode,privSql);
					int[] umCount = new int[moneyitemlist.size()];
					float umTotal=0f;
					for(int j=0;j<personInfoList.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)personInfoList.get(j);
						LazyDynaBean personBean = new LazyDynaBean();
						personBean.set("name",(String)bean.get("a0101"));
						personBean.set("value",GzAnalyseBo.add((String)bean.get(itemid),"0",2));
						HashMap itemMap=getMoneyitem(Float.parseFloat(GzAnalyseBo.add((String)bean.get(itemid),"0",2)),moneyitemlist);
						for(int k=0;k<itemMap.size();k++)
						{   
							umCount[k]+=Integer.parseInt((String)itemMap.get(String.valueOf(k)));
							personBean.set(String.valueOf(k), "0".equalsIgnoreCase((String)itemMap.get(String.valueOf(k)))?"":(String)itemMap.get(String.valueOf(k)));
						}
						list.add(personBean);
						umTotal+=Float.parseFloat((String)bean.get(itemid));
						//umTotal = GzAnalyseBo.add(umTotal,(String)bean.get(itemid), 2);
					}
					LazyDynaBean umBean = new LazyDynaBean();
					umBean.set("name",AdminCode.getCodeName("UM",codevalue)+"(小计)");
					for(int h=0;h<umCount.length;h++)
					{
						umBean.set(String.valueOf(h), "0".equalsIgnoreCase(String.valueOf(umCount[h]))?"":String.valueOf(umCount[h]));
					}
					umBean.set("value",this.getXS(String.valueOf(umTotal),2));
					list.add(0,umBean);
				}
			}
			if(list.size()==1)
			{
				list.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 单位下的所有部门
	 * @param unitid
	 * @param tableName
	 * @return
	 */
	public HashMap getUMMap(String unitid,String tableName,ContentDAO dao,String beforeSql,String filterSql)
	{
		HashMap map= new HashMap();
		try
		{
			//unitid=""  的情况没考虑
			StringBuffer sql = new StringBuffer();
			sql.append("select distinct(e0122) from "+tableName+" where ");
			if(unitid==null|| "null".equalsIgnoreCase(unitid))
				sql.append(" b0110 is null ");
			else
		    	sql.append(" b0110 = '"+unitid+"' ");
			if(filterSql!=null&&!"".equals(filterSql))
    		{
    			sql.append(" and  ");
    			sql.append(filterSql);
    		}
			else if(beforeSql!=null&&!"".equals(beforeSql))
				sql.append(beforeSql);
			sql.append(" order by e0122");
			RowSet rs = null;
			rs=dao.search(sql.toString());
			int i=0;
			while(rs.next())
			{
				if(rs.getString("e0122")!=null&&!"".equals(rs.getString("e0122")))
				{
    				map.put(String.valueOf(i),rs.getString("e0122"));
		   		i++;
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
	 * 得到已选的货币项目
	 * @param nstyleid
	 * @return
	 */
	public ArrayList getSelectedMoneyItemList(String nstyleid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneyitem where nstyleid=");
			sql.append(nstyleid);
			sql.append(" and nflag=1 order by nitemid desc");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			int i=0;
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set(String.valueOf(i),rs.getString("cname"));
				bean.set("nitemid",String.valueOf(rs.getFloat("nitemid")));
				list.add(bean);
				i++;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 改变货币项目的有效性
	 * @param nstyleid
	 * @param nitemids
	 * @param type
	 */
	public void modifyMoneyitemInfo(String nstyleid,String nitemids,int type)
	{
		try
		{
			String[] nitemid_arr=nitemids.split("-");
			StringBuffer nitemid_buf=new StringBuffer();
			if(nitemids==null||nitemids.trim().length()<=0)
				return;
			for(int i=0;i<nitemid_arr.length;i++)
			{
				nitemid_buf.append(",");
				nitemid_buf.append(nitemid_arr[i]);
			}
			StringBuffer sql = new StringBuffer();
			sql.append("update moneyitem set nflag=");
			sql.append(type);
			sql.append(" where nstyleid=");
			sql.append(nstyleid);
			sql.append(" and nitemid ");
			if(type==0)
			{
				sql.append(" not ");
			}
			sql.append("in (");
			sql.append(nitemid_buf.toString().substring(1));
			sql.append(")");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 得到薪资类别对应的货币种类
	 * @param salaryid
	 * @return
	 */
	public String getNstyleidBySalaryid(String salaryid) throws GeneralException
	{
		String n="";
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select nmoneyid from salarytemplate where salaryid=");
			sql.append(salaryid);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				n=rs.getString("nmoneyid");
			}
			if(n==null|| "".equals(n))
				throw GeneralExceptionHandler.Handle(new Exception("该工资类别没有设置货币种类"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 得到某部门下的所有人和要显示的薪资项目那列的数据
	 * @param e0122
	 * @param tableName
	 * @param itemid
	 * @param dao
	 * @return
	 */
	public ArrayList getPersonInfo(String e0122,String tableName,String itemid,ContentDAO dao,String beforeSql,String filterSql,String priv_mode,String privSql)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			String orgid="";
			String deptid="";
			if(this.salaryid!=null&&this.salaryid.length()>0)
			{
			    SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userview);
				orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
				orgid = orgid != null ? orgid : "";
				deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
				deptid = deptid != null ? deptid : "";
			}
			sql.append("select a0101,");
			sql.append(itemid);
			sql.append(" from ");
			sql.append(tableName); 
			sql.append(" where ");
			if(deptid.length()>0)
			{
				
				if(e0122==null|| "null".equalsIgnoreCase(e0122)){
					sql.append("  ("+deptid+" is null ");
				    sql.append(")");
				}else{
				    sql.append("  ("+deptid+" ='");
		        	sql.append(e0122);
			        sql.append("' ");
			        sql.append(")");
				}
			}else{
	    		if(e0122==null|| "null".equalsIgnoreCase(e0122))
	    			sql.append("e0122 is null ");
	    		else
	    		{
		        	sql.append("e0122 ='"+e0122);
	         		sql.append("'");
	    	 	}
			}
			if(filterSql!=null&&!"".equals(filterSql))
    		{
    			sql.append(" and ");
    			sql.append(filterSql);
    		}
			if("1".equals(priv_mode))
			{
				if(privSql!=null&&privSql.trim().length()>0)
				{
					sql.append(" and ");
	    			sql.append(privSql);
				}
			}
			if(beforeSql!=null&&!"".equals(beforeSql))
				sql.append(beforeSql);
			//sql.append(" and UPPER(dbname.pre)=UPPER("+tableName+".nbase) ");
			if(this.order_by!=null&&this.order_by.trim().length()>0)
				sql.append(" order by "+this.order_by);
			else
		    	sql.append(" order by dbid,a0000");
			RowSet rs= null;
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("a0101",rs.getString("a0101")==null?"":rs.getString("a0101"));
				bean.set(itemid,getXS(String.valueOf(rs.getFloat(itemid)),2));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 返回每个面值的货币项目有多少张
	 * @param value
	 * @param moneyItemList
	 * @return
	 */
	public HashMap getMoneyitem(float value,ArrayList moneyItemList)
	{
		HashMap map = new HashMap();
		float n=value;
		BigDecimal b1 = new BigDecimal(n+"");  //BigDecimal 中务必传入string  否则小数精度会出错，导致取余数不准确  zhaoxg add 2014-12-19
		try
		{
			for(int i=0;i<moneyItemList.size();i++)
			{
				LazyDynaBean bean=(LazyDynaBean)moneyItemList.get(i);
				float f=Float.parseFloat((String)bean.get("nitemid"));
				if(f==0)
					return map;
		        BigDecimal b2 = new BigDecimal(f+"");  
				int count=(int)(n/f);				
				String yu = b1.remainder(b2).toString(); //取余数
				n=Float.parseFloat(this.getXS(yu,2));
				map.put(String.valueOf(i),String.valueOf(count));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 某一货币下所有面值的货币项目
	 * @param nstyleid
	 * @return
	 */
	public ArrayList getAllMoneyItemList(String nstyleid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from moneyitem where nstyleid=");
			sql.append(nstyleid);
			sql.append(" order by nitemid desc");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				if((int)(rs.getFloat("nflag"))==1)
					bean.set("isSelect","1");
				else
					bean.set("isSelect","0");
				bean.set("itemid",String.valueOf(rs.getFloat("nitemid")));
				bean.set("itemdesc",rs.getString("cname"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 初次进入显示的薪资项目列
	 * @param salaryid
	 * @return
	 */
	public String getMaxItemid(String salaryid)
	{
		String str="";
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid from salaryset where salaryid=");
			sql.append(salaryid);
			sql.append(" and itemtype='N' ");
			sql.append("and sortid=(select max(sortid) from ");
			sql.append("salaryset where salaryid=");
			sql.append(salaryid);
			sql.append(" and itemtype='N'");
			sql.append(") order by sortid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				str=rs.getString("itemid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getCodeSql(String oth_name,String codevalue)
	{
		StringBuffer sql = new StringBuffer("");
		try
		{
			if(!(codevalue==null|| "".equals(codevalue)))
			{
				String code=codevalue.substring(0,2);
	    		String value=codevalue.substring(2);
				if(orgid.length()>0||deptid.length()>0)
				{
					if(orgid.length()>0&&deptid.length()>0)
					{
						sql.append("(");
						if(oth_name.length()>0)
							sql.append(oth_name+".");
	         			sql.append(orgid+" like'");
	    	    		sql.append(value);
	    	    		sql.append("%'");
	    	    		if(value==null|| "".equals(value))
	    	    		{
	    	    			sql.append(" or ");
	    	    			if(oth_name.length()>0)
								sql.append(oth_name+".");
	    	    			sql.append(orgid+" is null");
	    	    		}
	    	    		sql.append(" or ");
	    	    		if(oth_name.length()>0)
							sql.append(oth_name+".");
	         			sql.append(deptid+" like'");
	    	    		sql.append(value);
	    	    		sql.append("%'");
	    	    		if(value==null|| "".equals(value))
	    	    		{
	    	    			sql.append(" or ");
	    	    			if(oth_name.length()>0)
								sql.append(oth_name+".");
	    	    			sql.append(deptid+" is null");
	    	    		}
	    	    		
	    	    		sql.append(")");
					}else if(orgid.length()>0)
					{
						sql.append("(");
						if(oth_name.length()>0)
							sql.append(oth_name+".");
	         			sql.append(orgid+" like'");
	    	    		sql.append(value);
	    	    		sql.append("%'");
	    	    		if(value==null|| "".equals(value))
	    	    		{
	    	    			sql.append(" or ");
	    	    			if(oth_name.length()>0)
								sql.append(oth_name+".");
	    	    			sql.append(orgid+" is null");
	    	    		}
	    	    		sql.append(")");
					}else
					{
						sql.append("(");
						if(oth_name.length()>0)
							sql.append(oth_name+".");
	         			sql.append(deptid+" like'");
	    	    		sql.append(value);
	    	    		sql.append("%'");
	    	    		if(value==null|| "".equals(value))
	    	    		{
	    	    			sql.append(" or ");
	    	    			if(oth_name.length()>0)
								sql.append(oth_name+".");
	    	    			sql.append(deptid+" is null");
	    	    		}
	    	    		sql.append(")");
					}
				}
				else
				{
	         		if("UN".equalsIgnoreCase(code))
	        		{
	        			sql.append("(");
	        			if(oth_name.length()>0)
							sql.append(oth_name+".");
	         			sql.append("b0110 like'");
	    	    		sql.append(value);
	    	    		sql.append("%'");
	    	    		if(value==null|| "".equals(value))
	    	    		{
	    	    			sql.append(" or ");
	    	    			if(oth_name.length()>0)
								sql.append(oth_name+".");
	    	    			sql.append("b0110 is null");
	    	    		}
	    	    		sql.append(")");
	    	    	}
	        		if("UM".equalsIgnoreCase(code))
    	    		{
    		     		sql.append("(");
    		     		if(oth_name.length()>0)
							sql.append(oth_name+".");
    	     			sql.append("e0122 like '");
    		    		sql.append(value);
    		    		sql.append("%'");
    		    		if(value==null|| "".equals(value))
	    	    		{
	    			    	sql.append(" or ");
	    			    	if(oth_name.length()>0)
								sql.append(oth_name+".");
	    		     		sql.append("e0122 is null");
	    		    	}
	    	    		sql.append(")");
    	     		}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	/**
	 * 人员筛选，根据sql语句查人
	 * @param filterSql
	 * @param tableName
	 * @param itemid
	 * @param moneyitemlist
	 * @return
	 */
	public ArrayList getPersonListBySql(String filterSql,String tableName,String itemid,ArrayList moneyitemlist,String codeSql,String beforeSql,String priv_mode,String privSql)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			/*select T.* from (
			  select a0101, coalesce(e0122,'um') e0122,coalesce(b0110,'un') b0110,C58G2 ,max(nbase) nbase,max(a0000)  a0000 
			  from su_salary_1 where (su_salary_1.E0122='010101') group by b0110,e0122,a0101,C58G2,a0100) T, dbname 
			  where T.nbase=dbname.pre order by dbname.dbid,a0000 */
			sql.append("select a0101,");
			/*if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
				sql.append(Sql_switcher.isnull("b0110", "un")+",");
				//sql.append(" case b0110 when null then 'un' else b0110 end as b0110,");
				//sql.append(" case e0122 when null then 'um' else e0122 end as e0122");
			} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				sql.append(" coalesce(e0122,'um') e0122,coalesce(b0110,'un') b0110");
			}*/
			sql.append(Sql_switcher.isnull("b0110", "'un'")+" as b0110,");
			sql.append(Sql_switcher.isnull("e0122", "'um'")+" as e0122");
			sql.append(",");
			sql.append(itemid);
			sql.append(",max(nbase) nbase,max(a0000) a0000,max(a00z0) a00z0,max(a00z1) a00z1,a0100 ");
			sql.append(" from ");
			sql.append(tableName); 
			
		    sql.append(" where 1=1 ");
			if(filterSql!=null&&!"".equals(filterSql))
    		{
				sql.append(" and ");
    			sql.append(filterSql);
    		}
			 if(beforeSql!=null&&!"".equals(beforeSql))
			{
				sql.append(" and ");
			    sql.append(beforeSql.trim().substring(3));
			}
			 if(privSql!=null&&privSql.length()>0)
			 {
				 sql.append(" and ");
			     sql.append(privSql);
			 }
				 
			/*if(codeSql != null&&!codeSql.equals("")&&(filterSql==null||filterSql.equals("")))
				sql.append(" and "+codeSql);*/
			sql.append(" group by b0110,e0122,a0101,"+itemid+",a0100");
			StringBuffer buf = new StringBuffer();
			if(this.order_by!=null&&this.order_by.trim().length()>0)
			{
				buf.append(" select T.* from ("+sql.toString()+") T,"+tableName+" S where UPPER(T.nbase)=UPPER(S.nbase) and T.a0100=S.a0100 and T.a00z1=S.a00z1 and T.a00z0=S.a00z0");
				buf.append(" order by S.");
				String oby = order_by.replaceAll(",", ",S.");
				buf.append(oby);
			
			}else{
		    	buf.append(" select T.* from ("+sql.toString()+") T,dbname where UPPER(T.nbase)=UPPER(dbname.pre) order by b0110,e0122,dbname.dbid,a0000");
			}
		    RowSet rs= null;
			ContentDAO dao = new ContentDAO (this.conn);
			//System.out.println(sql);
			rs = dao.search(buf.toString());
			String b0110 = "";
			String e0122 = "";
			HashMap map=null;
			int init = 0 ;
			//int umsize = 0;
			//int unsize=0;
			int[] un = new int[moneyitemlist.size()];
			int[] um = new int[moneyitemlist.size()];
			for(int i=0;i<moneyitemlist.size();i++)
			{
				un[i] = 0;
				um[i] = 0;
			}
			String umtotal = "0";
			String untotal = "0";
			LazyDynaBean bean = null;
			LazyDynaBean umbean = null;
			LazyDynaBean unbean = null;
			ArrayList alist = new ArrayList();
			ArrayList umList = new ArrayList();
			while(rs.next())
			{
				if(init == 0)
				{
					b0110 = rs.getString("b0110");
					e0122 = rs.getString("e0122");
				}
				if(b0110.equalsIgnoreCase(rs.getString("b0110")))
				{
					if(e0122.equalsIgnoreCase(rs.getString("e0122")))
					{
					    bean = new LazyDynaBean();
						bean.set("name",rs.getString("a0101")==null?"":rs.getString("a0101"));
						bean.set("value",getXS(String.valueOf(rs.getFloat(itemid)),2));
						umtotal = CashListBo.add(umtotal,rs.getString(itemid), 2);
						untotal = CashListBo.add(untotal,rs.getString(itemid), 2);
						map=getMoneyitem(Float.parseFloat(CashListBo.add("0",rs.getString(itemid), 2)),moneyitemlist);
						for(int i=0;i<map.size();i++)
						{
							un[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
							um[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
							bean.set(String.valueOf(i), "0".equalsIgnoreCase((String)map.get(String.valueOf(i)))?"":(String)map.get(String.valueOf(i)));
						}
						alist.add(bean);
					}
					else//部门变化了
					{
						bean = new LazyDynaBean();
						umbean = new LazyDynaBean();
						umbean.set("name",AdminCode.getCodeName("UM",e0122)+"(小计)");
						umbean.set("value",this.getXS(umtotal,2));
						umtotal = "0";
						//
						bean.set("name",rs.getString("a0101")==null?"":rs.getString("a0101"));
						bean.set("value",getXS(String.valueOf(rs.getFloat(itemid)),2));
						umtotal = CashListBo.add(umtotal,rs.getString(itemid), 2);
						untotal = CashListBo.add(untotal,rs.getString(itemid), 2);
						map=getMoneyitem(Float.parseFloat(CashListBo.add("0",rs.getString(itemid), 2)),moneyitemlist);
						for(int i=0;i<map.size();i++)
						{
							umbean.set(String.valueOf(i),um[i]==0?"":(um[i]+""));
							um[i] = 0;
							un[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
							um[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
							bean.set(String.valueOf(i), "0".equalsIgnoreCase((String)map.get(String.valueOf(i)))?"":(String)map.get(String.valueOf(i)));
						}
						umList.add(umbean);
                        for(int i=0;i<alist.size();i++)
                        {
                        	umList.add(((LazyDynaBean)alist.get(i)));
                        }
                        alist.clear();
					
						alist.add(bean);
						
						e0122 = rs.getString("e0122");
					}
				}
				else//单位变了
				{
					bean = new LazyDynaBean();
					umbean = new LazyDynaBean();
					unbean = new LazyDynaBean();
					unbean.set("name",AdminCode.getCodeName("UN",b0110)+"(合计)");
					unbean.set("value",this.getXS(untotal,2));
					untotal = "0";
					//
					umbean.set("name",AdminCode.getCodeName("UM",e0122)+"(小计)");
					umbean.set("value",umtotal);
					umtotal = "0";
					//
					bean.set("name",rs.getString("a0101")==null?"":rs.getString("a0101"));
					bean.set("value",getXS(String.valueOf(rs.getFloat(itemid)),2));
					umtotal = CashListBo.add(umtotal,rs.getString(itemid), 2);
					untotal = CashListBo.add(untotal,rs.getString(itemid), 2);
					map=getMoneyitem(Float.parseFloat(CashListBo.add("0",rs.getString(itemid), 2)),moneyitemlist);
					for(int i=0;i<map.size();i++)
					{
						unbean.set(String.valueOf(i),un[i]==0?"":(un[i]+""));
						un[i]=0;
						umbean.set(String.valueOf(i),um[i]==0?"":(um[i]+""));
						um[i] = 0;
						un[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
						um[i]+=Integer.parseInt(((String)map.get(String.valueOf(i))));
						bean.set(String.valueOf(i), "0".equalsIgnoreCase((String)map.get(String.valueOf(i)))?"":(String)map.get(String.valueOf(i)));
					}
					list.add(unbean);
					umList.add(umbean);
					 for(int i=0;i<alist.size();i++)
                     {
                     	umList.add(((LazyDynaBean)alist.get(i)));
                     }
                     alist.clear();
					for(int i=0;i<umList.size();i++)
					{
						list.add(((LazyDynaBean)umList.get(i)));
					}
					umList.clear();
					alist.add(bean);
					
				//	list.add(list.size()-(umsize-1),umbean);
					
				///	list.add(bean);
				    e0122=rs.getString("e0122");
				    b0110 = rs.getString("b0110");
				}
				init++;
			}
			if(init!=0)
			{
				
	    		umbean = new LazyDynaBean();
	    		unbean = new LazyDynaBean();
	    		unbean.set("name",AdminCode.getCodeName("UN",b0110)+"(合计)");
	    		unbean.set("value",this.getXS(untotal,2));
	    		umbean.set("name",AdminCode.getCodeName("UM",e0122)+"(小计)");
	    		umbean.set("value",this.getXS(umtotal,2));
		    	for(int i=0;i<map.size();i++)
		    	{
		    		unbean.set(String.valueOf(i),un[i]==0?"":(un[i]+""));
		    		umbean.set(String.valueOf(i),um[i]==0?"":(um[i]+""));
	    		}
		    	list.add(unbean);
	    		umList.add(umbean);
				 for(int i=0;i<alist.size();i++)
                 {
                 	umList.add(((LazyDynaBean)alist.get(i)));
                 }
                 alist.clear();
				for(int i=0;i<umList.size();i++)
				{
					list.add(((LazyDynaBean)umList.get(i)));
				}
				umList.clear();
			}
			if(list.size()==1)
			{
				list.clear();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 从tablename中查出所有的单位
	 * @param tableName
	 * @return
	 */
	public HashMap getAllUnits(String tableName,String code,String beforeSql,String filterSql,String priv_mode,String privSql)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			String orgid="";
			String deptid="";
			if(this.salaryid!=null&&this.salaryid.length()>0)
			{
			    SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userview);
				orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
				orgid = orgid != null ? orgid : "";
				deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
				deptid = deptid != null ? deptid : "";
			}
			if(code==null|| "".equals(code))
			{
     			sql.append("select distinct(b0110) from ");
	    		sql.append(tableName);
	    		sql.append(" where 1=1 ");
	    		if(filterSql!=null&&!"".equals(filterSql))
	    		{
	    			sql.append(" and ");
	    			sql.append(filterSql);
	    		}
	    		else if(beforeSql!=null&&!"".equals(beforeSql))
	    		{
	    			sql.append(" and ");
	    			sql.append(beforeSql.trim().substring(3));
	    		}
	    		if(privSql!=null&&!"".equals(privSql.trim()))
	    		{
	        		sql.append(" and ");
	        		sql.append(privSql);
	    		}
    			sql.append(" order by b0110");
			}
			else
			{
				sql.append("select distinct(b0110) from ");
				sql.append(tableName);
				if(orgid.length()>0||deptid.length()>0)
				{
					if(orgid.length()>0&&deptid.length()>0)
					{
						sql.append(" where ("+orgid+" like '");
			    		sql.append(code);
				    	sql.append("%' ");
				    	if(code==null|| "".equals(code))
				    		sql.append(" or "+orgid+" is null ");
				    	sql.append(" or "+deptid+" like '");
			    		sql.append(code);
				    	sql.append("%' ");
				    	if(code==null|| "".equals(code))
				    		sql.append(" or "+deptid+" is null ");
				    	sql.append(")");
					}
					else if(orgid.length()>0)
					{
						sql.append(" where ("+orgid+" like '");
			    		sql.append(code);
				    	sql.append("%' ");
				    	if(code==null|| "".equals(code))
				    		sql.append(" or "+orgid+" is null ");
				    	sql.append(")");
					}else{
						sql.append(" where ("+deptid+" like '");
			    		sql.append(code);
				    	sql.append("%' ");
				    	if(code==null|| "".equals(code))
				    		sql.append(" or "+deptid+" is null ");
				    	sql.append(")");
					}
				}
				else
				{
			     	sql.append(" where (b0110 like '");
		    		sql.append(code);
			    	sql.append("%' ");
			    	if(code==null|| "".equals(code))
			    		sql.append(" or b0110 is null ");
			    	sql.append(")");
				}
				if(filterSql!=null&&!"".equals(filterSql))
	    		{
	    			sql.append(" and ");
	    			sql.append(filterSql);
	    		}
				else if(beforeSql!=null&&!"".equals(beforeSql))
					sql.append(beforeSql);
				if(privSql!=null&&!"".equals(privSql))
				{
		    		sql.append(" and ");
		    		sql.append(privSql);
				}
				sql.append(" order by b0110");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			int i=0;
			while(rs.next())
			{
				map.put(String.valueOf(i),rs.getString("b0110"));
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	public ArrayList getUnitPerson(String tableName,String unitValue,String itemid,String beforeSql,String filterSql,String privSql)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			String orgid="";
			String deptid="";
			if(this.salaryid!=null&&this.salaryid.length()>0)
			{
			    SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userview);
				orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
				orgid = orgid != null ? orgid : "";
				deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
				deptid = deptid != null ? deptid : "";
			}
			sql.append("select a0101,");
			sql.append(itemid);
			sql.append(" from ");
			sql.append(tableName);
			sql.append("  where ");
			if(orgid.length()>0)
			{	
				if(unitValue==null|| "null".equalsIgnoreCase(unitValue)){
				    sql.append("  ("+orgid+" is null ");
				    if(deptid.length()>0)
				    {
				    	sql.append(" and "+deptid+" is not null");
				    }else{
				    	sql.append(" and e0122 is not null ");
				    }
			        sql.append(")");
				}else{
					sql.append("  ("+orgid+" ='"+unitValue+"' ");
					 if(deptid.length()>0)
					 {
					    sql.append(" and "+deptid+" is null");
					 }else{
					    sql.append(" and e0122 is null ");
					 }
			        sql.append(")");
				}
			}else{
	    		if(unitValue==null|| "null".equalsIgnoreCase(unitValue))
	    		{
		    		sql.append(" b0110 is null ");
		    		 if(deptid.length()>0)
					   {
					    	sql.append(" and "+deptid+" is not null");
					    }else{
					    	sql.append(" and e0122 is not null ");
					    }
	    		}
	    		else
	    		{
	     	    	sql.append(" b0110='"+unitValue+"'");
	     	    	if(deptid.length()>0)
					{
					    sql.append(" and "+deptid+" is null");
					}else{
					    sql.append(" and e0122 is null ");
					}
    		    	sql.append(" and e0122 is null");
	    		}
			}
	 		if(filterSql!=null&&!"".equals(filterSql))
    		{
    			sql.append(" and ");
    			sql.append(filterSql);
    		}
			else if(beforeSql!=null&&!"".equals(beforeSql))
				sql.append(beforeSql);
			if(privSql!=null&&!"".equals(privSql))
			{
				sql.append(" and "+privSql);
			}
			//sql.append(" and UPPER("+tableName+".nbase)=UPPER(dbname.pre) ");
			if(this.order_by!=null&&this.order_by.trim().length()>0)
				sql.append(" order by "+this.order_by);
			else
	    		sql.append(" order by dbid,a0000");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("a0101",rs.getString("a0101")==null?"":rs.getString("a0101"));
				bean.set(itemid,getXS(String.valueOf(rs.getFloat(itemid)),2));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	  public String getXS(String str,int scale){
	    	if(str==null)
	    		str="0.00";
	    	BigDecimal m=new BigDecimal(str);
	    	BigDecimal one = new BigDecimal("1");
	    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	    }
	  public static String add(String v1, String v2, int scale) {

			if (scale < 0) {
				throw new IllegalArgumentException(
						"The scale must be a positive integer or zero");
			}
			if(v1==null|| "".equals(v1))
				v1="0";
			if(v2==null|| "".equals(v2))
				v2="0";
			BigDecimal a = new BigDecimal(v1);
			BigDecimal b = new BigDecimal(v2);
			BigDecimal s = a.add(b);
			BigDecimal one = new BigDecimal("1");
			return s.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();

		}
	  public ArrayList getFilterCondList(String salaryid)
	    {
	    	ArrayList list= new ArrayList();
	    	String cond_str="";
	    	try
	    	{
	    		String str="select lprogram from salarytemplate where salaryid="+salaryid;
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs=null;
	    		rs= dao.search(str);
	    		while(rs.next())
	    		{
	    			cond_str=rs.getString("lprogram");
	    		}
	    		CommonData temp=new CommonData("all",ResourceFactory.getProperty("label.gz.allman"));
	    		list.add(temp);
	    		if(!(cond_str==null||cond_str.trim().length()<=0))
	    		{
	    			
	    		    SalaryLProgramBo lbo = new SalaryLProgramBo(cond_str,this.userview); //xieguiquan add this.userview 20100828
	    		    list.addAll(lbo.getServiceItemList());
	    		}
	    		temp=new CommonData("new",ResourceFactory.getProperty("label.gz.new"));
	    		list.add(temp);	
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	    }
	  /**
	   * 是否按归属单位和归属部门来控制
	   * @param gzbo
	   * @param userView
	   * @return
	   */
	  public String controlByUnitcode(SalaryTemplateBo gzbo,UserView userView)
		{
			String flag="0";
			String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(manager!=null&&manager.trim().length()>0)
			{
				if(!userView.getUserName().equalsIgnoreCase(manager))
				{
					if(orgid.length()>0||deptid.length()>0)
						flag="1";
				}
			}
			return flag;
		}
	  /**
	   * 兼容共享工资类别，加入权限控制
	   * @param salaryid
	   * @param view
	   * @return
	   */
	  public String getPrivSql(UserView view,SalaryTemplateBo gzbo)
	  {
		  StringBuffer buf = new StringBuffer("");
		  try
		  {
			  String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		      String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
		      String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			  orgid = orgid != null ? orgid : "";
			  String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			  deptid = deptid != null ? deptid : "";
			  this.orgid=orgid;
			  this.deptid=deptid;
			  this.controlByUnitcode=this.controlByUnitcode(gzbo, view);
			  /**超级用户，没有权限控制*/
			  if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
		      {	 	        	 
		      }
		        /**非共享类别或者是管理员没有权限控制*/
		      else if(manager.trim().length()==0||view.getUserName().equalsIgnoreCase(manager))
		      {
		      }
		         else/**共享的类别，但是不是管理员，加入权限控制*/
		         {
		        	 /**共享的薪资类别，并且为非管理员，一定按照管理范围控制*/
		        	 if(manager.trim().length()>0&&!manager.equalsIgnoreCase(view.getUserName()))
		        	 {
		        		 if("0".equals(this.mode)&& "1".equals(this.controlByUnitcode))
		        		 {
		        			 SalaryReportBo srb=new SalaryReportBo(this.conn,salaryid,this.userview);
		        			 String ss=srb.getWhlByUnits();
		        			 buf.append(ss.substring(4));
		        		 }
		        		 else
		        		 {
		                	String code=view.getManagePrivCode();
		                	String codevalue=view.getManagePrivCodeValue();
		                 	if(code!=null&&!"".equals(code))
		                	{
		                		if("UN".equalsIgnoreCase(code))
		                		{
		            	    		buf.append(" (b0110 like'"+codevalue+"%' ");
		            	    		if(codevalue==null)
		                				buf.append(" or b0110 is null");
		            	    		buf.append(")");
		                		}else if("UM".equalsIgnoreCase(code))
		                 		{
		                 			buf.append(" (e0122 like'"+codevalue+"%' ");
		                			if(codevalue==null)
		                 				buf.append(" or e0122 is null");
		                			buf.append(")");
		                		}
		                		else if("@K".equalsIgnoreCase(code))
		                		{
		        	         		String value=gzbo.getUnByPosition(codevalue);
		        	        		if(value!=null&&value.length()>=2)
		        	        		{
		        		         		code=value.substring(0,2);
		        		         		codevalue=value.substring(2);
		        	        			if("UN".equalsIgnoreCase(code))
				                		{
				        	        		buf.append(" (b0110 like'"+codevalue+"%' ");
				        	         		if(codevalue==null)
				        	        			buf.append(" or b0110 is null");
				                			buf.append(")");
				                		}else if("UM".equalsIgnoreCase(code))
				                		{
				                			buf.append(" (e0122 like'"+codevalue+"%' ");
				            	    		if(codevalue==null)
				                				buf.append(" or e0122 is null");
				            	    		buf.append(")");
				                		}
		            	    		}
		            	    		else
		            	    		{
		            	     			buf.append(" 1=2 ");
		            		    	}
		                		}
		                 	}
		                	else
		                	{
		                		buf.append(" 1=2 ");
    		            	}
		        		 }
		         	 }
		         }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return buf.toString();
	  }
	public String getOrder_by() {
		return order_by;
	}
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getSalaryid() {
		return salaryid;
	}
	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
	public UserView getUserview() {
		return userview;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
}
