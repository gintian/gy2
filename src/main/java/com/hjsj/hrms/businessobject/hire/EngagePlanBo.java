package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.FormatValue;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EngagePlanBo {
private Connection con=null;
	
	public EngagePlanBo(Connection conn)
	{
		this.con=conn;
	}
	
	
	
	/**
	 * 取得某表下的详细信息
	 * @param fieldList
	 * @param userView
	 * @param origin  a 已发布状态  b:起草状态
	 * @return
	 */
	public ArrayList getPlanFieldList(ArrayList fieldList,UserView userView,String planid,String origin) throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet=null;
	    FormatValue formatvalue=new FormatValue();  
		try
		{
			if(!"0".equals(planid))
			{
				rowSet=dao.search("select * from z01 where z0101='"+planid+"'");
				rowSet.next();
			}
			for(int i=0;i<fieldList.size();i++)
			{
				String operator="1";   // 1: 可操作，0：只读
				FieldItem item=(FieldItem)fieldList.get(i);		
				String state=item.getState();//0隐藏  1显示
				LazyDynaBean abean=new LazyDynaBean();
				if("z0115".equalsIgnoreCase(item.getItemid())|| "z0117".equalsIgnoreCase(item.getItemid()))//计划招聘人数 实际招聘人数
                {
                    continue;
                }
				abean.set("itemid",item.getItemid());
				abean.set("itemdesc",item.getItemdesc());
				String value="";
				String viewValue="";
				if(rowSet!=null&&!"D".equalsIgnoreCase(item.getItemtype())&&rowSet.getString(item.getItemid())!=null)
				{
					if("N".equals(item.getItemtype())&&item.getDecimalwidth()!=0)
					{
						double avalue=rowSet.getDouble(item.getItemid());
						value=String.valueOf(avalue);
						value=formatvalue.format(item, value);//格式化字符串
					}
					else {
                        value=rowSet.getString(item.getItemid());
                    }
				}
				else if(rowSet!=null&& "D".equalsIgnoreCase(item.getItemtype()))
				{
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					Date d=rowSet.getDate(item.getItemid());
					if(d!=null)
					{
						Calendar calendar=Calendar.getInstance();
						calendar.setTime(d);
					//	calendar.add(Calendar.MONTH,-1);
						value=format.format(calendar.getTime());
					}
				}
				if(!"0".equals(item.getCodesetid()))
				{
					if(value!=null&&value.trim().length()>0) {
                        viewValue=AdminCode.getCodeName(item.getCodesetid(),value);
                    }
				}
				if("z0123".equalsIgnoreCase(item.getItemid()))//审批通过时间
				{					
					operator="0";
				}
				if("z0129".equalsIgnoreCase(item.getItemid())) //计划审批状态
				{
					operator="0";
					if(value==null||value.trim().length()==0)
					{
						if("b".equals(origin)) {
                            value="01";
                        } else {
                            value="04";
                        }
					}
						viewValue=AdminCode.getCodeName(item.getCodesetid(),value);
				}
				if("z0101".equalsIgnoreCase(item.getItemid())) //计划号
                {
                    operator="0";
                }
				if("z0119".equalsIgnoreCase(item.getItemid())) //负责部门
				{					
					if(value==null||value.trim().length()==0)
						//value=userView.getUserDeptId();  //为了使负责部门可以为空
                    {
                        if(value.trim().length()>0) {
                            viewValue = AdminCode.getCodeName(item.getCodesetid(), value);
                        }
                    }
				}
				if("z0105".equalsIgnoreCase(item.getItemid())) //计划所属单位
				{					
					/*if(value==null||value.trim().length()==0)
						value=userView.getUserOrgId();
					if(value.trim().length()>0)
						viewValue=AdminCode.getCodeName(item.getCodesetid(),value);*/
					if(userView.isSuper_admin()|| "1".equals(userView.getGroupId()))
					{
						if(value==null||value.trim().length()==0)
						{
//			    			LazyDynaBean  a_bean= this.getUnitInformation("", 1);
//			    			value=(String)(a_bean.get("id")==null?"":a_bean.get("id"));
//			    			viewValue=(String)(a_bean.get("desc")==null?"":a_bean.get("desc"));
						}
						
					}
					else
					{
						String unitID=userView.getUnitIdByBusi("7");
						/**没有操作单位*/
						if(unitID==null|| "".equals(unitID))
						{
						}
						/**操作单位为全部*/
						if(unitID.trim().length()==3)
						{
							if(value==null||value.trim().length()==0)
							{
//				    			LazyDynaBean  a_bean= this.getUnitInformation("", 1);
//				    			value=(String)(a_bean.get("id")==null?"":a_bean.get("id"));
//				    			viewValue=(String)(a_bean.get("desc")==null?"":a_bean.get("desc"));
							}
						}
						else
						{
							String[] temps=unitID.split("`");
							String id="";
							boolean bl=false;
							for(int j=0;j<temps.length;j++)
							{
								if(temps[j]==null|| "".equals(temps[j])) {
                                    continue;
                                }
								if("UN".equalsIgnoreCase(temps[j].substring(0,2)))
								{
									id=temps[j].substring(2);
									bl=true;
									break;
								}
							}
							if(!bl)
							{
								for(int j=0;j<temps.length;j++)
								{
									if(temps[j]==null|| "".equals(temps[j])) {
                                        continue;
                                    }
									 RowSet recset=null;
					    			 boolean isOk=true;
					    			 String codeitemid=temps[j].substring(2);
					    			 while(isOk)
						    		{
						    			recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')");
						    			if(recset.next())
						    			{
							    			id=recset.getString("codeitemid");								
							    			if("UN".equalsIgnoreCase(recset.getString("codesetid")))
							     			{
							    				isOk=false;
												
						 	    			}			
						    			}	
						    		}
					    			 break;
								}
							}
							LazyDynaBean  a_bean= this.getUnitInformation(id, 2);
			    			value=(String)(a_bean.get("id")==null?"":a_bean.get("id"));
			    			viewValue=(String)(a_bean.get("desc")==null?"":a_bean.get("desc"));
						}
					}
				}
				if("z0121".equalsIgnoreCase(item.getItemid())) //负责人
				{					
					if(value==null||value.trim().length()==0) {
                        value=userView.getUserFullName();
                    }
				}
				
				
				abean.set("operator",operator);
				abean.set("value",value);
				abean.set("viewvalue",viewValue);
				abean.set("decimalwidth",String.valueOf(item.getDecimalwidth()));
				abean.set("itemtype",item.getItemtype());
				abean.set("itemlength",String.valueOf(item.getItemlength()));
				abean.set("codesetid",item.getCodesetid());
				if("1".equals(state)
						|| "z0101".equalsIgnoreCase(item.getItemid())//计划号
						|| "z0129".equalsIgnoreCase(item.getItemid())//计划审批状态
						|| "z0103".equalsIgnoreCase(item.getItemid())//计划名称
						|| "z0107".equalsIgnoreCase(item.getItemid())//开始时间
						|| "z0109".equalsIgnoreCase(item.getItemid())//结束时间
						|| "z0127".equalsIgnoreCase(item.getItemid())//计划招聘对象
						|| "z0105".equalsIgnoreCase(item.getItemid())//所属单位
				  ){//0隐藏  1显示
					list.add(abean);
				}
				
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
	 * 取负责单位
	 */
	public LazyDynaBean getUnitInformation(String codeitemid,int type)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			StringBuffer buf = new StringBuffer();
			/**当管理范围和操作单位范围是所有的时候，默认取编码最大的单位*/
			if(type==1)
			{
				buf.append("select codeitemid,codeitemdesc from organization where codesetid='UN' and codeitemid=" +
						"(select min(codeitemid) from organization where codesetid='UN')");
			}
			else if(type==2)
			{
				buf.append("select codeitemid,codeitemdesc from organization where codesetid='UN' and codeitemid=" +
						"'"+codeitemid+"'");
			}
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs = dao.search(buf.toString());
			while(rs.next())
			{
				bean.set("id",rs.getString("codeitemid"));
				bean.set("desc",rs.getString("codeitemdesc"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
}
