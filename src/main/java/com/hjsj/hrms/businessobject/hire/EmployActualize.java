package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployActualize {
	private Connection conn=null;
	private String flag="0";
	
	public EmployActualize(Connection conn) {
		this.conn=conn;
	}
	
	
	/**
	 * 得到候选人应聘职位对应表中的数据
	 * @param tableColumnsList  表列名集合
	 * @param code				组织范围id
	 * @return
	 */
	/*public ArrayList getFilterPersonnelList(ArrayList tableColumnsList,String code)
	{
		ArrayList filterPersonnelList=new ArrayList();
		String dbName=getZP_DB_NAME();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			
			if(dbName.trim().length()==0)
				return filterPersonnelList;
			String sql=getQuerySQL(dbName,tableColumnsList,code);
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				LazyDynaBean lazyDynaBean=new LazyDynaBean();
				for(int i=0;i<tableColumnsList.size();i++)
				{
					LazyDynaBean aBean=(LazyDynaBean)tableColumnsList.get(i);
					String codesetid=(String)aBean.get("codesetid");
					String itemid=(String)aBean.get("itemid");
					if(codesetid.equals("0"))
					{
						lazyDynaBean.set(itemid,rowSet.getString(itemid));
					}
					else
					{
						lazyDynaBean.set(itemid,AdminCode.getCode(codesetid,rowSet.getString(itemid)));
					}
				}
				filterPersonnelList.add(lazyDynaBean);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return filterPersonnelList;
	}
	*/
	
	
	
	
	
	/**
	 * 取得 某组织id下 招聘的职位信息
	 * @param codeid 组织id
	 */
	public ArrayList getPositionList(String codeid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset=null;
		CommonData dataobj0 = new CommonData("0"," ");
		list.add(dataobj0);
		try
		{
			String sql="select z03.z0301,org.codeitemdesc,org2.codeitemdesc depart,z01.z0103 from z03,z01,organization org,(select * from organization where codesetid='UM') org2 "
					  +" where z03.z0101=z01.z0101 and z03.z0311=org.codeitemid and org.parentid=org2.codeitemid  and z01.z0129='04' ";
			if(!"0".equals(codeid)) {
                sql+=" and z0311 like '"+codeid+"%' ";
            }
			frowset=dao.search(sql);
			while(frowset.next())
			{
				CommonData dataobj = new CommonData(frowset.getString("z0301"), frowset.getString("codeitemdesc")+"(部门："+frowset.getString("depart")+"   计划："+frowset.getString("z0103")+" )");
				list.add(dataobj);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 按条件查询人 花名册的列信息
	 * @param column_str
	 * @return
	 */
	public ArrayList getColumnsList(String column_str)
	{
		ArrayList list=new ArrayList();
		String[] columns=column_str.split(",");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset=null;
		try
		{
			HashMap map=new HashMap();
			column_str="'"+column_str.replaceAll(",","','")+"'";
			frowset=dao.search("select * from fielditem where itemid in ("+column_str+")");
			while(frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("itemid",frowset.getString("itemid").toLowerCase());
				abean.set("itemtype",frowset.getString("itemtype"));
				if("a0101".equalsIgnoreCase(frowset.getString("itemid"))) {
                    abean.set("itemdesc",frowset.getString("itemdesc")+"(应聘状态)");
                } else {
                    abean.set("itemdesc",frowset.getString("itemdesc"));
                }
				abean.set("codesetid",frowset.getString("codesetid"));
				
				map.put(frowset.getString("itemid").toLowerCase(),abean);
			}
			
			LazyDynaBean abean=new LazyDynaBean();
			abean.set("itemid","a0100");
			abean.set("itemtype","A");
			abean.set("itemdesc",ResourceFactory.getProperty("hire.parameterSet.menNo"));
			abean.set("codesetid","0");
			map.put("a0100",abean);
			
			for(int i=0;i<columns.length;i++)
			{
				list.add((LazyDynaBean)map.get(columns[i]));
			}
			
			/*LazyDynaBean aabean=new LazyDynaBean();
			aabean.set("itemid","codeitemdesc");
			aabean.set("itemtype","A");
			aabean.set("itemdesc","应聘状态");
			aabean.set("codesetid","0");
			list.add(aabean);*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 修改外聘人才库人员状态 或 面试状态
	 * @param idList  人员id
	 * @param state   人员状态
	 * @param dnName  库前缀
	 * @param flag    1:XXXA01  2:Z05
	 * @param userName 用户名
	 * @return 0:  1:（针对人员筛选  删除zp_pos_tache 里，某人申请的其他职位的信息）
	 */
	public String setState(ArrayList idList,String state,String dbName,String flag,String userid,String isMailField)throws GeneralException
	{
		String info="0";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		if("0".equals(state)) {
            state="";
        }
		try
		{
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<idList.size();i++)
			{
				String temp=(String)idList.get(i);
				/**
				 * 当同一个人申请了多份职位时，保留选定的这条记录，删除其他的申请记录。
				 */
				if("1".equals(flag))
				{
					String[] arr=temp.split("/");
					rowSet=dao.search("select count(a0100) num from zp_pos_tache where a0100='"+arr[0]+"'");
					if(rowSet.next())
					{
						int num=rowSet.getInt("num");
						if(num>1)
						{
							info="1";
							dao.delete("delete from zp_pos_tache where a0100='"+arr[0]+"' and zp_pos_id<>'"+arr[1]+"'",new ArrayList());
						}
					}
					temp=arr[0];
				}
				whl.append(",'"+temp+"'");
			}
			if(idList.size()>0)
			{
				if("1".equals(flag)) {
                    dao.update("update "+dbName+"A01 set state='"+state+"' where a0100 in ("+whl.substring(1)+") ");
                } else if("2".equals(flag))
				{
					dao.update("update Z05 set state='"+state
					        +"',z0511='"+userid+"'"
					        +",State_date="+Sql_switcher.sqlNow()//简历状态更新时间 2014-05-28
					        +" where Z0501 in ("+whl.substring(1)+") ");
					/*if(state.equals("22")&&!isMailField.equals("#"))  //自动发送面试通知邮件
					{
						SendEmail sendEmail=new SendEmail();
						boolean aflag=sendEmail.setInfo();
						if(!aflag)
							throw GeneralExceptionHandler.Handle(new Exception("没有设置邮件服务器！"));
						rowSet=dao.search("select * from fielditem where itemid='"+isMailField+"'");
						String fieldSet="";
						if(rowSet.next())
						{
							fieldSet=rowSet.getString("fieldsetid");
						}
						
						String sql="select "+dbName+"a01.a0101,z05.a0100,organization.codeitemdesc,z05.z0509,z05.z0503,"+dbName+"A01."+isMailField+" from z05 left join "+dbName+"a01 on z05.a0100="+dbName+"a01.a0100 left join zp_pos_tache on z05.a0100=zp_pos_tache.a0100 "        
								+" left join z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join organization  on z03.z0311=organization.codeitemid where z05.z0501 in ("+whl.substring(1)+") ";
						rowSet=dao.search(sql);
						
						ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
						HashMap map=parameterXMLBo.getAttributeValues();
						String hire_emailContext=(String)map.get("email_template");
						SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
						while(rowSet.next())
						{
							String context=hire_emailContext;
							context=context.replaceAll("\\(~姓名~\\)",rowSet.getString("a0101"));
							if(rowSet.getString("codeitemdesc")!=null)
								context=context.replaceAll("\\(~应聘职位~\\)",rowSet.getString("codeitemdesc"));
							Date date=rowSet.getDate("z0509");		
							if(date!=null)
								context=context.replaceAll("\\(~面试时间~\\)",bartDateFormat.format(date));
							else
								context=context.replaceAll("\\(~面试时间~\\)"," ");
							if(rowSet.getString("z0503")!=null)
								context=context.replaceAll("\\(~面试地点~\\)",rowSet.getString("z0503"));
							context=context.replaceAll("\\(~系统时间~\\)",bartDateFormat2.format(new Date()));								
							if(rowSet.getString(isMailField)!=null&&rowSet.getString(isMailField).trim().length()>1)
							{
								sendEmail.send(rowSet.getString(isMailField).trim(), ResourceFactory.getProperty("hire.employActualize.interviewNotice"),context);
							}
						}
						sendEmail=null;
					}*/
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return info;
	}
	
	
	/**
	 * 保存描述（评语）信息
	 * @param ids
	 * @param dbName
	 */
	public void saveDescription(ArrayList ids,String dbName,String summary)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<ids.size();i++)
			{
				String a_id=(String)ids.get(i);
				String[] id=a_id.split("/");
				whl.append(" or ");
				whl.append(" (a0100='"+id[0]+"' and zp_pos_id='"+id[1]+"' )");
			}
			dao.update("update zp_pos_tache set description='"+summary+"' where "+whl.substring(3));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * get候选人员应聘职位对应表的描述
	 * @param a0100
	 * @param zp_pos_id
	 * @return
	 */
	public String getDescription(String a0100,String zp_pos_id)
	{
		String description="";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			rowSet=dao.search("select description from zp_pos_tache where a0100='"+a0100+"' and zp_pos_id='"+zp_pos_id+"'");
			if(rowSet.next())
			{
				if(rowSet.getString("description")!=null) {
                    description=rowSet.getString("description");
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return description;
	}
	
	
	
	
	/**
	 * 
	 * @param dbname 库前缀
	 * @param tableColumnsList 表列名
	 * @param code   组织id
	 * @return
	 */
	public String getQuerySQL(String dbname,ArrayList tableColumnsList,String code,String extendSql)
	{
		StringBuffer sql=new StringBuffer("");
		StringBuffer select_str=new StringBuffer("select "+dbname+"A01.A0100"+Sql_switcher.concat()+"'/'"+Sql_switcher.concat()+"Z03.Z0301 id,A0101,org2.codeitemdesc departname,org.codeitemdesc,"+dbname+"A01.state");
		StringBuffer from_str=new StringBuffer(" from zp_pos_tache zp  left join  Z03 on zp.ZP_POS_ID=Z03.Z0301  ");
		 			 from_str.append(" left join  Z01 on Z03.Z0101=Z01.Z0101 ");
					 from_str.append(" left join  organization org on Z03.Z0311=org.codeitemid ");
					 from_str.append(" left join  (select * from organization where codesetid='UM' ) org2 on org.parentid=org2.codeitemid ");
					 from_str.append(" left join "+dbname+"A01 on zp.a0100="+dbname+"A01.a0100 ");					
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		fieldSetMap.put("zp_pos_tache","1");
		for(int i=4;i<tableColumnsList.size();i++)
		{
			LazyDynaBean aBean=(LazyDynaBean)tableColumnsList.get(i);
			String itemid=(String)aBean.get("itemid");
//			System.out.println(itemid);
			String itemtype=(String)aBean.get("itemtype");
			String fieldsetid=(String)aBean.get("fieldsetid");
			
			String tempName="";
			if(!"zp_pos_tache".equalsIgnoreCase(fieldsetid)) {
                tempName=dbname+fieldsetid;
            } else {
                tempName="zp";
            }
			if("a0101".equalsIgnoreCase(itemid)|| "M".equals(itemtype)) {
                continue;
            }
			if(fieldSetMap.get(fieldsetid)==null)
			{
				
				StringBuffer viewSql=new StringBuffer("");
				viewSql.append("(SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);

				from_str.append(" left join "+viewSql.toString()+" on zp.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid,"1");
			}
			
			String column=tempName+"."+itemid;
			if("A".equals(itemtype)|| "N".equals(itemtype)) {
                select_str.append(","+column);
            } else if("D".equals(itemtype))
			{
				select_str.append(","+Sql_switcher.numberToChar(Sql_switcher.year(column))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month(column))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day(column))+"  "+itemid);
			}
		}
		select_str.append(","+Sql_switcher.numberToChar(Sql_switcher.year("zp.APPLY_DATE"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.month("zp.APPLY_DATE"))+Sql_switcher.concat()+"'-'"+Sql_switcher.concat()+Sql_switcher.numberToChar(Sql_switcher.day("zp.APPLY_DATE"))+"  APPLY_DATE");
       
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		sql.append(" where ");
		if(!"0".equals(code)) {
            sql.append("  Z03.Z0311 like '"+code+"%' ");
        } else {
            sql.append(" 1=1 ");
        }
		sql.append(" and ("+dbname+"A01.state is null or "+dbname+"A01.state='10' or "+dbname+"A01.state='11' or "+dbname+"A01.state='12' or "+dbname+"A01.state='') and Z01.Z0129='04' ");
		if(extendSql!=null&&extendSql.trim().length()>0)
		{
			if("ord".equalsIgnoreCase(extendSql.trim().substring(0,3))) {
                sql.append(" "+extendSql);
            } else {
                sql.append(" and "+extendSql);
            }
		}
		return sql.toString();
	}
	
	
	
	public ArrayList getFieldList()
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			
			
			
			FieldItem fieldItem2=new FieldItem();
			fieldItem2.setItemid("a0101");
			fieldItem2.setItemtype("A");
			fieldItem2.setCodesetid("0");
			fieldItem2.setItemdesc(ResourceFactory.getProperty("hire.employActualize.name"));
			list.add(fieldItem2);
			
			FieldItem fieldItem0=new FieldItem();
			fieldItem0.setItemid("departname");
			fieldItem0.setItemtype("A");
			fieldItem0.setCodesetid("0");
			fieldItem0.setItemdesc(ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment"));
			list.add(fieldItem0);
			
			
			FieldItem fieldItem3=new FieldItem();
			fieldItem3.setItemid("codeitemdesc");
			fieldItem3.setItemtype("A");
			fieldItem3.setCodesetid("0");
			fieldItem3.setItemdesc(ResourceFactory.getProperty("hire.employActualize.interviewPosition"));
			list.add(fieldItem3);
			
			
			
			
			
			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("fields")!=null&&((String)map.get("fields")).trim().length()>0)
			{
				StringBuffer whl=new StringBuffer("");
				String musterFieldIDs=(String)map.get("fields");
				if(musterFieldIDs.indexOf("`")==-1)
				{
					whl.append(",'"+musterFieldIDs+"'");
				}
				else
				{
					String[] fields=musterFieldIDs.split("`");
					for(int i=0;i<fields.length;i++)
					{
						whl.append(",'"+fields[i]+"'");
					}
				}
				rowSet=dao.search("select * from fielditem where itemid in ("+whl.substring(1)+")");
				while(rowSet.next())
				{
					
					FieldItem a_fieldItem=new FieldItem();
					a_fieldItem.setItemid(rowSet.getString("itemid").toLowerCase());
					a_fieldItem.setItemtype(rowSet.getString("itemtype"));
					a_fieldItem.setCodesetid(rowSet.getString("codesetid"));
					a_fieldItem.setItemdesc(rowSet.getString("itemdesc"));
					list.add(a_fieldItem);
					
				}
			}
			FieldItem fieldItem4=new FieldItem();
			fieldItem4.setItemid("apply_date");
			fieldItem4.setItemtype("D");
			fieldItem4.setCodesetid("0");
			fieldItem4.setItemdesc(ResourceFactory.getProperty("hire.employActualize.interviewTime"));
			list.add(fieldItem4);
			
			FieldItem fieldItem1=new FieldItem();
			fieldItem1.setItemid("state");
			fieldItem1.setItemtype("A");
			fieldItem1.setCodesetid("36");
			fieldItem1.setItemdesc(ResourceFactory.getProperty("hire.employActualize.resumeState"));
			list.add(fieldItem1);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得显示的表头名称和对应的列
	 * @return
	 */
	public ArrayList getTableColumn_headNameList()
	{
		ArrayList list=new ArrayList();
		ArrayList tableHeadNameList=new ArrayList();				  //表头列名；
		ArrayList tableColumnsList=new ArrayList();	
		StringBuffer columns=new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			{
				LazyDynaBean lazyDynaBean1=new LazyDynaBean();
				lazyDynaBean1.set("itemid","id");
				lazyDynaBean1.set("itemtype","A");
				lazyDynaBean1.set("codesetid","0");
				lazyDynaBean1.set("fieldsetid","zp_pos_tache");
				lazyDynaBean1.set("itemdesc","id");
				tableColumnsList.add(lazyDynaBean1);	   //人员编号
				columns.append(",id");
				/*
				LazyDynaBean lazyDynaBean1=new LazyDynaBean();
				lazyDynaBean1.set("itemid","A0100");
				lazyDynaBean1.set("itemtype","A");
				lazyDynaBean1.set("codesetid","0");
				lazyDynaBean1.set("fieldsetid","zp_pos_tache");
				tableColumnsList.add(lazyDynaBean1);	   //人员编号
				
				LazyDynaBean lazyDynaBean2=new LazyDynaBean();
				lazyDynaBean2.set("itemid","ZP_POS_ID");
				lazyDynaBean2.set("itemtype","A");
				lazyDynaBean2.set("codesetid","0");
				lazyDynaBean2.set("fieldsetid","zp_pos_tache");
				tableColumnsList.add(lazyDynaBean2); //应聘职位id
				*/
				
			
				
				LazyDynaBean lazyDynaBean4=new LazyDynaBean();
				lazyDynaBean4.set("itemid","a0101");
				lazyDynaBean4.set("itemtype","A");
				lazyDynaBean4.set("codesetid","0");
				lazyDynaBean4.set("fieldsetid","A01");
				lazyDynaBean4.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.name"));
				tableColumnsList.add(lazyDynaBean4);	  	    //姓名
				columns.append(",a0101");
				
				
				LazyDynaBean lazyDynaBean44=new LazyDynaBean();
				lazyDynaBean44.set("itemid","departname");
				lazyDynaBean44.set("itemtype","A");
				lazyDynaBean44.set("codesetid","0");				
				lazyDynaBean44.set("fieldsetid","org2");
				lazyDynaBean44.set("itemdesc",ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment"));
				tableColumnsList.add(lazyDynaBean44);	  	    //应聘部门
				columns.append(",departname");
				
				
				
				
				
				LazyDynaBean lazyDynaBean5=new LazyDynaBean();
				lazyDynaBean5.set("itemid","codeitemdesc");
				lazyDynaBean5.set("itemtype","A");
				lazyDynaBean5.set("codesetid","0");				
				lazyDynaBean5.set("fieldsetid","org");
				lazyDynaBean5.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.interviewPosition"));
				tableColumnsList.add(lazyDynaBean5);	  	    // //应聘职位名称
				columns.append(",codeitemdesc");
				
				tableHeadNameList.add(" ");
				tableHeadNameList.add(ResourceFactory.getProperty("hire.employActualize.name"));
				tableHeadNameList.add(ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment"));
				tableHeadNameList.add(ResourceFactory.getProperty("hire.employActualize.interviewPosition"));
				
			}
			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("fields")!=null&&((String)map.get("fields")).trim().length()>0)
			{
				StringBuffer whl=new StringBuffer("");
				String musterFieldIDs=(String)map.get("fields");
				if(musterFieldIDs.indexOf("`")==-1)
				{
					whl.append(",'"+musterFieldIDs+"'");
				}
				else
				{
					String[] fields=musterFieldIDs.split("`");
					for(int i=0;i<fields.length;i++)
					{
						whl.append(",'"+fields[i]+"'");
					}
				}
				rowSet=dao.search("select * from fielditem where itemid in ("+whl.substring(1)+")");
				while(rowSet.next())
				{
					LazyDynaBean lazyDynaBean=new LazyDynaBean();
					lazyDynaBean.set("itemid",rowSet.getString("itemid").toLowerCase());
					lazyDynaBean.set("itemtype",rowSet.getString("itemtype"));
					lazyDynaBean.set("codesetid",rowSet.getString("codesetid"));
					lazyDynaBean.set("fieldsetid",rowSet.getString("fieldsetid"));
					lazyDynaBean.set("itemdesc",rowSet.getString("itemdesc"));
					tableColumnsList.add(lazyDynaBean);	  
					columns.append(","+rowSet.getString("itemid").toLowerCase());
					
					tableHeadNameList.add(rowSet.getString("itemdesc"));
				}
			}
			
			LazyDynaBean lazyDynaBean6=new LazyDynaBean();
			lazyDynaBean6.set("itemid","apply_date");
			lazyDynaBean6.set("itemtype","D");
			lazyDynaBean6.set("codesetid","0");
			lazyDynaBean6.set("fieldsetid","zp_pos_tache");
			lazyDynaBean6.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.interviewTime"));
			tableColumnsList.add(lazyDynaBean6);	  	   //应聘时间
			columns.append(",apply_date");
			tableHeadNameList.add(ResourceFactory.getProperty("hire.employActualize.interviewTime"));
			
			
			LazyDynaBean lazyDynaBean3=new LazyDynaBean();
			lazyDynaBean3.set("itemid","state");
			lazyDynaBean3.set("itemtype","A");
			lazyDynaBean3.set("codesetid","36");
			lazyDynaBean3.set("fieldsetid","A01");
			lazyDynaBean3.set("itemdesc",ResourceFactory.getProperty("hire.employActualize.resumeState"));
			tableColumnsList.add(lazyDynaBean3);     		//简历状态
			columns.append(",state");
			tableHeadNameList.add(ResourceFactory.getProperty("label.zp_resource.status"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		list.add(tableHeadNameList);
		list.add(tableColumnsList);
		list.add(columns.toString());
		return list;
	}
	
	
	
	
	/**
	 * 取得应聘人才库标识
	 * @return
	 */
	public String getZP_DB_NAME()
	{
		String dbName="";
	
		try
		{
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			dbName=zpDbNameVo.getString("str_value");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  dbName;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	
	
}
