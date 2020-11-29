package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

public class InnerHireBo {
private Connection con=null;
DbSecurityImpl dbS = new DbSecurityImpl();
	
	public InnerHireBo(Connection conn)
	{
		this.con=conn;
	}
	
	
	/**
	 * 取得已申请职位数
	 * @param a0100
	 * @return
	 * @throws GeneralException
	 */
	public int getApplyPosCount(String a0100)throws GeneralException
	{
		int size=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet frowset=dao.search("select count(*) from zp_pos_tache where a0100='"+a0100+"'");
			if(frowset.next())
			{
				size=frowset.getInt(1);
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
		
		return size;
	}
	
	/**
	 * 判断是否已申请过该职位
	 * @param a0100
	 * @param z0301
	 * @return
	 * @throws GeneralException
	 */
	public boolean isApplyed(String a0100,String z0301)throws GeneralException
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			StringBuffer buf=new StringBuffer();
			buf.append("select zp_pos_id from zp_pos_tache where a0100='");
			buf.append(a0100);
			buf.append("' and zp_pos_id='");
			buf.append(z0301);
			buf.append("'");
			RowSet frowset=dao.search(buf.toString());
			if(frowset.next())
			{
				flag=true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
		
		return flag;
	}
	
	
	/**
	 * 根据邮件地址 得到 相对应招聘库 人员id
	 * @param emailAddress
	 * @param emailField
	 * @return
	 * @throws GeneralException
	 */
	public String getZpkA0100(String emailAddress,String emailField)throws GeneralException
	{
		String a0100="";
		try
		{
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			if(vo==null) {
                throw GeneralExceptionHandler.Handle(new Exception("没有设置外聘库!"));
            }
			String dbname=vo.getString("str_value");
			ContentDAO dao=new ContentDAO(this.con);
			RowSet frowset=dao.search("select a0100 from "+dbname+"A01 where "+emailField+"='"+emailAddress+"'");
			if(frowset.next()) {
                a0100=frowset.getString(1);
            }
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
		
		return a0100;
	}
	
	
	public int getSameEmailCount(String field,String email,UserView userView)throws GeneralException
	{
		int count=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			RowSet frowset=dao.search("select count("+field+") from "+userView.getDbname()+"A01 where "+field+"='"+email+"'");
			if(frowset.next()) {
                count=frowset.getInt(1);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
			throw GeneralExceptionHandler.Handle(ex);
		}
		return count;
	}
	
	
	
	/**
	 * 取得用户邮件地址
	 * @param field
	 * @param userView
	 * @return
	 * @throws GeneralException
	 */
	public String getEmailAddress(String field,UserView userView)throws GeneralException
	{
		String email="";
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			//String dd="select "+field+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'";
			RowSet frowset=dao.search("select "+field+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'");
			if(frowset.next()) {
                email=frowset.getString(1);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
			throw GeneralExceptionHandler.Handle(ex);
		}
		return email;
	}
	
	
	
	/**
	 * 判断当前用户是否填写了必填项
	 * @param a0100
	 * @param dbname
	 * @return
	 */
	public void getMustItemInfo(String a0100,String dbname)throws GeneralException
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.con);
			
			ArrayList zpFieldlist=employNetPortalBo.getZpFieldList();
			ArrayList fieldSetList=(ArrayList)zpFieldlist.get(0);
			HashMap fieldMap=(HashMap)zpFieldlist.get(1);
			HashMap fieldSetMap=(HashMap)zpFieldlist.get(2);
			RowSet rowSet=null;
			for(int i=0;i<fieldSetList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
				String fieldSetId=(String)abean.get("fieldSetId");
				
				
				HashMap fieldExtendMap=(HashMap)fieldSetMap.get(fieldSetId.toLowerCase());
				ArrayList fieldList=(ArrayList)fieldMap.get(fieldSetId.toUpperCase());
				if(fieldList==null) {
                    fieldList=(ArrayList)fieldMap.get(fieldSetId.toLowerCase());
                }
				
				StringBuffer whl=new StringBuffer("");
				FieldItem item=null;
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					String itemid=(String)t.next();
					String temp=(String)fieldExtendMap.get(itemid.toLowerCase());
					String[] temps=temp.split("#");
					item=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(item==null) {
                        throw GeneralExceptionHandler.Handle(new Exception("应聘库中涉及到的指标("+itemid.toLowerCase()+")没有构库,不能申请职位!"));
                    }
					if("1".equals(temps[1])||item.isFillable()) {
                        whl.append(" or "+itemid+" is null ");
                    }
				}
				if(whl.length()>0)
				{
					FieldSet setVo=DataDictionary.getFieldSetVo(fieldSetId.toLowerCase());
					
					rowSet=dao.search("select a0100 from "+dbname+fieldSetId+" where a0100='"+a0100+"'");
					if(rowSet.next())
					{
						rowSet=dao.search("select a0100 from "+dbname+fieldSetId+" where a0100='"+a0100+"' and ( "+whl.substring(3)+" )");
						if(rowSet.next()) {
                            throw GeneralExceptionHandler.Handle(new Exception("您的"+setVo.getCustomdesc()+" 还有信息没填完整，不能申请职位！"));
                        }
					}
					else {
                        throw GeneralExceptionHandler.Handle(new Exception("您的"+setVo.getCustomdesc()+" 没有填写信息，不能申请职位！"));
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
	
	
	
	public void synchronizeResume(HashSet a0100Set,String email_field,String to_nbase)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			StringBuffer whl=new StringBuffer("");
			RowSet rowSet=null;
			RowSet rowSet2=null;
			StringBuffer sql=new StringBuffer("");
			
			for(Iterator t=a0100Set.iterator();t.hasNext();)
			{
				String to_a0100=(String)t.next();
				sql.setLength(0);
				sql.append("select distinct "+to_nbase+"a01.a0100, "+to_nbase+"a01."+email_field+",zpt.nbase  from zp_pos_tache zpt,"+to_nbase+"a01");
				/**原先为wwxa01???测试也对??*/
				sql.append(" where zpt.a0100="+to_nbase+"a01.a0100 and "+to_nbase+"a01.a0100='"+to_a0100+"'");
				rowSet=dao.search(sql.toString());
				String from_nbase="";
				String from_a0100="";
				if(rowSet.next())
				{
					from_nbase=rowSet.getString("nbase");
					String email=rowSet.getString(email_field);
					rowSet2=dao.search("select a0100 from "+from_nbase+"a01 where "+email_field+"='"+email+"'");
					if(rowSet2.next())
					{
						from_a0100=rowSet2.getString(1);
						updateZpInfo(from_nbase,from_a0100,to_a0100,to_nbase);
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
	 * 信息同步
	 * @param from_nbase
	 * @param from_a0100
	 * @param to_a0100
	 * @param to_nbase
	 */
	public void updateZpInfo(String from_nbase,String from_a0100,String to_a0100,String to_nbase)throws GeneralException
	{
		ArrayList sqls = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			EmployNetPortalBo bo=new EmployNetPortalBo(this.con);
			ArrayList list2 =bo.getSetByWorkExprience("02");
			
			
		
			HashMap   fieldMap;
			ArrayList fieldSetList=(ArrayList)list2.get(0);
			//dml修改为了实现校园和社会招聘的分开和原来程序的兼容
			
				fieldMap=(HashMap)list2.get(1);
			
			//Statement smt=this.con.createStatement();
			String a01_set="";
			for(int i=0;i<fieldSetList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
				String fieldSetId=(String)abean.get("fieldSetId");
				ArrayList fieldList=(ArrayList)fieldMap.get(fieldSetId);
				
				StringBuffer insert_str=new StringBuffer("");
				StringBuffer select_str=new StringBuffer("");
				
				if(!"a01".equalsIgnoreCase(fieldSetId)){
					insert_str.append(",a0100");
					select_str.append(",'"+to_a0100+"'");
				}
				
				if(!"a01".equalsIgnoreCase(fieldSetId))
				{
					insert_str.append(",i9999");
					select_str.append(",i9999");
				}
				else
				{
					a01_set=fieldSetId;
					insert_str.append(",a0000");
					select_str.append(","+Integer.parseInt(to_a0100));
				}
					
				for(int j=0;j<fieldList.size();j++)
				{
						String temp=(String)fieldList.get(j);
						insert_str.append(","+temp);					
						select_str.append(","+temp);
				}
				
				String sql="";
				
			    if(!"a01".equalsIgnoreCase(fieldSetId))
			    {
			    	//smt.addBatch("delete from "+to_nbase+fieldSetId+" where a0100='"+to_a0100+"'");
			    	sql="insert into "+to_nbase+fieldSetId+" ("+insert_str.substring(1)+" ) select "+select_str.substring(1)+" from "+from_nbase+fieldSetId+" where a0100='"+from_a0100+"'";
			    	sqls.add("delete from "+to_nbase+fieldSetId+" where a0100='"+to_a0100+"'");
			    	sqls.add(sql);
			    	//smt.addBatch(sql);
			    }
			}
		
			RowSet rowset=dao.search("select * from "+to_nbase+"A00 where 1=2");
			ResultSetMetaData data=rowset.getMetaData();
			StringBuffer insert_str2=new StringBuffer("");
			StringBuffer select_str2=new StringBuffer("");
			for(int j=0;j<data.getColumnCount();j++)
			{
				String temp=(String)data.getColumnName(j+1);
				insert_str2.append(","+temp);
				if("a0100".equalsIgnoreCase(temp)) {
                    select_str2.append(",'"+to_a0100+"'");
                } else {
                    select_str2.append(","+temp);
                }
						
			}
			sqls.add("delete from "+to_nbase+"a00 where a0100='"+to_a0100+"'");
			//smt.addBatch("delete from "+to_nbase+"a00 where a0100='"+to_a0100+"'");
			String sql2="insert into "+to_nbase+"a00 ("+insert_str2.substring(1)+" ) select "+select_str2.substring(1)+" from "+from_nbase+"a00 where a0100='"+from_a0100+"'";
			sqls.add(sql2);
			//smt.addBatch(sql2);
			dao.batchUpdate(sqls);                 //批量执行
			
			{
					
				RecordVo selfVo=new RecordVo(from_nbase+"a01");
				selfVo.setString("a0100",from_a0100);
				selfVo=dao.findByPrimaryKey(selfVo);
					
					
				RecordVo vo=new RecordVo(to_nbase+"A01");
				vo.setString("a0100",to_a0100);
				RecordVo vo2=dao.findByPrimaryKey(vo);
					
				ArrayList fieldList=(ArrayList)fieldMap.get(a01_set);
				for(int j=0;j<fieldList.size();j++)
				{
							String temp=((String)fieldList.get(j)).toLowerCase();
							FieldItem item=DataDictionary.getFieldItem(temp);
							if(item!=null&&((!"D".equals(item.getItemtype())&&selfVo.getString(temp)!=null)||("D".equals(item.getItemtype())&&selfVo.getDate(temp)!=null)))
							{
								if("A".equals(item.getItemtype())) {
                                    vo2.setString(temp, selfVo.getString(temp));
                                } else if("D".equals(item.getItemtype())) {
                                    vo2.setDate(temp,selfVo.getDate(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()==0) {
                                    vo2.setInt(temp,selfVo.getInt(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()!=0) {
                                    vo2.setDouble(temp,selfVo.getDouble(temp));
                                } else {
                                    vo2.setString(temp,selfVo.getString(temp));
                                }
							}
				}
				vo2.setInt("a0000",Integer.parseInt(to_a0100));
				dao.updateValueObject(vo2);
			}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();	
				throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/**
	 * 将应用库中的人员信息 copy 到招聘库中
	 * @param a0100
	 * @param dbname
	 * @param toDbName
	 * @param email
	 * @throws GeneralException
	 */
	public String  copyInfoToZp(String a0100,String dbname,String toDbName,String email,String email_field)throws GeneralException
	{
		String new_a0100="";
		ArrayList sqls = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			new_a0100=DbNameBo.insertMainSetA0100(toDbName+"A01",this.con);
			
			EmployNetPortalBo bo=new EmployNetPortalBo(this.con);
			ArrayList list=bo.getZpFieldList();
			ArrayList fieldSetList=(ArrayList)list.get(0);
			HashMap   fieldMap=(HashMap)list.get(1);
			//Statement smt=this.con.createStatement();
			String a01_set="";
			for(int i=0;i<fieldSetList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
				String fieldSetId=(String)abean.get("fieldSetId");
				ArrayList fieldList=(ArrayList)fieldMap.get(fieldSetId);
				
				StringBuffer insert_str=new StringBuffer("");
				StringBuffer select_str=new StringBuffer("");
				
				if(!"a01".equalsIgnoreCase(fieldSetId)){
					insert_str.append(",a0100");
					select_str.append(",'"+new_a0100+"'");
				}
				
				if(!"a01".equalsIgnoreCase(fieldSetId))
				{
					insert_str.append(",i9999");
					select_str.append(",i9999");
				}
				else
				{
					a01_set=fieldSetId;
					insert_str.append(",a0000");
					select_str.append(","+Integer.parseInt(new_a0100));
				}
					
				for(int j=0;j<fieldList.size();j++)
				{
						String temp=(String)fieldList.get(j);
						insert_str.append(","+temp);					
						select_str.append(","+temp);
				}
				
				String sql="";
			    if(!"a01".equalsIgnoreCase(fieldSetId))
			    {
			    	sql="insert into "+toDbName+fieldSetId+" ("+insert_str.substring(1)+" ) select "+select_str.substring(1)+" from "+dbname+fieldSetId+" where a0100='"+a0100+"'";
			    	//smt.addBatch(sql);
			    	sqls.add(sql);
			    }
			 // dao.insert(sql,new ArrayList());
			}
			
			RowSet rowset=dao.search("select * from "+toDbName+"A00 where 1=2");
			ResultSetMetaData data=rowset.getMetaData();
			StringBuffer insert_str2=new StringBuffer("");
			StringBuffer select_str2=new StringBuffer("");
			for(int j=0;j<data.getColumnCount();j++)
			{
				String temp=(String)data.getColumnName(j+1);
				insert_str2.append(","+temp);
				if("a0100".equalsIgnoreCase(temp)) {
                    select_str2.append(",'"+new_a0100+"'");
                } else {
                    select_str2.append(","+temp);
                }
				
						
			}
			String sql2="insert into "+toDbName+"a00 ("+insert_str2.substring(1)+" ) select "+select_str2.substring(1)+" from "+dbname+"a00 where a0100='"+a0100+"'";
			//smt.addBatch(sql2);
			sqls.add(sql2);
			dao.batchUpdate(sqls);
			//smt.executeBatch();
			//dao.insert(sql2, new ArrayList());                //批量执行
			////smt.close(); 
			// // if(fieldSetId.equalsIgnoreCase("a01"))
				{
					
					RecordVo selfVo=new RecordVo(dbname+"a01");
					selfVo.setString("a0100",a0100);
					selfVo=dao.findByPrimaryKey(selfVo);
					
					
					RecordVo vo=new RecordVo(toDbName+"A01");
					vo.setString("a0100",new_a0100);
					RecordVo vo2=dao.findByPrimaryKey(vo);
					
					ArrayList fieldList=(ArrayList)fieldMap.get(a01_set);
					for(int j=0;j<fieldList.size();j++)
					{
							String temp=((String)fieldList.get(j)).toLowerCase();
							FieldItem item=DataDictionary.getFieldItem(temp);
							if(item!=null&&selfVo.getString(temp)!=null)
							{
								if("A".equals(item.getItemtype())) {
                                    vo2.setString(temp, selfVo.getString(temp));
                                } else if("D".equals(item.getItemtype())) {
                                    vo2.setDate(temp,selfVo.getDate(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()==0) {
                                    vo2.setInt(temp,selfVo.getInt(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()!=0) {
                                    vo2.setDouble(temp,selfVo.getDouble(temp));
                                } else {
                                    vo2.setString(temp,selfVo.getString(temp));
                                }
							}
					}
					vo2.setInt("a0000",Integer.parseInt(new_a0100));
					vo2.setString(email_field.toLowerCase(), email);
					vo2.setString("username",email);
					vo2.setString("userpassword","111111");				
					vo2.setDate("createtime",Calendar.getInstance().getTime());				
					//设置人员状态
					ParameterXMLBo bo2=new ParameterXMLBo(this.con,"1");
					HashMap map=bo2.getAttributeValues();
					if(map!=null&&map.get("resume_state")!=null) {
                        vo2.setString(((String)map.get("resume_state")).toLowerCase(),"10");
                    }
					if(map!=null&&map.get("person_type")!=null) {
                        vo2.setString(((String)map.get("person_type")).toLowerCase(),"0");
                    }
						//person_type
					dao.updateValueObject(vo2);
				}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();	
				//throw GeneralExceptionHandler.Handle(ex);
		}
		return new_a0100;
	}
	/***
	 * 主要针对内部招聘，导入单位和部门等指标的信息(参数中未设置，系统自动加上)
	 * @param a0100
	 * @param dbname
	 * @param toDbName
	 * @param email
	 * @param email_field
	 * @return
	 * @throws GeneralException
	 */
	public String  copyInfoToZpInner(String a0100,String dbname,String toDbName,String email,String email_field,boolean flag)throws GeneralException
	{
		String new_a0100="";
		ArrayList sqls = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.con);
			new_a0100=DbNameBo.insertMainSetA0100(toDbName+"A01",this.con);
			
			EmployNetPortalBo bo=new EmployNetPortalBo(this.con);
			ArrayList list=bo.getZpFieldList();
			ArrayList fieldSetList=(ArrayList)list.get(0);
			HashMap   fieldMap=(HashMap)list.get(1);
			//Statement smt=this.con.createStatement();
			String a01_set="";
			for(int i=0;i<fieldSetList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
				String fieldSetId=(String)abean.get("fieldSetId");
				ArrayList fieldList=(ArrayList)fieldMap.get(fieldSetId);
				
				StringBuffer insert_str=new StringBuffer("");
				StringBuffer select_str=new StringBuffer("");
				
				if(!"a01".equalsIgnoreCase(fieldSetId)){
					insert_str.append(",a0100");
					select_str.append(",'"+new_a0100+"'");
				}
				
				if(!"a01".equalsIgnoreCase(fieldSetId))
				{
					insert_str.append(",i9999");
					select_str.append(",i9999");
				}
				else
				{
					a01_set=fieldSetId;
					insert_str.append(",a0000");
					select_str.append(","+Integer.parseInt(new_a0100));
				}
					
				for(int j=0;j<fieldList.size();j++)
				{
					String temp = (String)fieldList.get(j);
					FieldItem item = DataDictionary.getFieldItem(temp, fieldSetId);
					if(item == null || !"1".equals(item.getUseflag())) {
                        continue;
                    }
					
					insert_str.append(","+temp);					
					select_str.append(","+temp);
				}
				
				
				String sql="";
			    if(!"a01".equalsIgnoreCase(fieldSetId))
			    {
			    	sql="insert into "+toDbName+fieldSetId+" ("+insert_str.substring(1)+" ) select "+select_str.substring(1)+" from "+dbname+fieldSetId+" where a0100='"+a0100+"'";
			    	//smt.addBatch(sql);
			    	sqls.add(sql);
			    }
			 // dao.insert(sql,new ArrayList());
			}
			
			RowSet rowset=dao.search("select * from "+toDbName+"A00 where 1=2");
			ResultSetMetaData data=rowset.getMetaData();
			StringBuffer insert_str2=new StringBuffer("");
			StringBuffer select_str2=new StringBuffer("");
			for(int j=0;j<data.getColumnCount();j++)
			{
				String temp=(String)data.getColumnName(j+1);
				insert_str2.append(","+temp);
				if("a0100".equalsIgnoreCase(temp)) {
                    select_str2.append(",'"+new_a0100+"'");
                } else {
                    select_str2.append(","+temp);
                }
				
						
			}
			String sql2="insert into "+toDbName+"a00 ("+insert_str2.substring(1)+" ) select "+select_str2.substring(1)+" from "+dbname+"a00 where a0100='"+a0100+"'";
			/*smt.addBatch(sql2);
			dbS.open(this.con, sql2);
			smt.executeBatch();*/
			sqls.add(sql2);
			dao.batchUpdate(sqls);
			//dao.insert(sql2, new ArrayList());                //批量执行
			////smt.close(); 
			// // if(fieldSetId.equalsIgnoreCase("a01"))
			
				{
					boolean eflag=false;
					boolean bflag=false;
					RecordVo selfVo=new RecordVo(dbname+"a01");
					selfVo.setString("a0100",a0100);
					selfVo=dao.findByPrimaryKey(selfVo);
					
					
					RecordVo vo=new RecordVo(toDbName+"A01");
					vo.setString("a0100",new_a0100);
					RecordVo vo2=dao.findByPrimaryKey(vo);
					
					ArrayList fieldList=(ArrayList)fieldMap.get(a01_set);
					for(int j=0;j<fieldList.size();j++)
					{
							String temp=((String)fieldList.get(j)).toLowerCase();
							FieldItem item = DataDictionary.getFieldItem(temp, a01_set);
							if(item == null || !"1".equals(item.getUseflag())) {
                                continue;
                            }
							
							if(flag)
							{
	    						if("a01".equalsIgnoreCase(a01_set))
	    						{
	    			    			if("b0110".equalsIgnoreCase(temp))
		    		    			{
		    						  bflag=true;
		         					}
		    		    			if("e0122".equalsIgnoreCase(temp))
		    		    			{
			    	    				eflag=true;
			    	    			}
	    						}
							}
							
							if(item!=null&&selfVo.getString(temp)!=null)
							{
								if("A".equals(item.getItemtype())) {
                                    vo2.setString(temp, selfVo.getString(temp));
                                } else if("D".equals(item.getItemtype())) {
                                    vo2.setDate(temp,selfVo.getDate(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()==0) {
                                    vo2.setInt(temp,selfVo.getInt(temp));
                                } else if("N".equals(item.getItemtype())&&item.getDecimalwidth()!=0) {
                                    vo2.setDouble(temp,selfVo.getDouble(temp));
                                } else {
                                    vo2.setString(temp,selfVo.getString(temp));
                                }
							}
					}
					if("a01".equalsIgnoreCase(a01_set))
					{
						if(flag)
						{
							if(!eflag)
							{
								vo2.setString("e0122",selfVo.getString("e0122"));
							}
							if(!bflag)
							{
								vo2.setString("b0110",selfVo.getString("b0110"));
							}
						}
					}
					vo2.setInt("a0000",Integer.parseInt(new_a0100));
					//2014.12.19 xxd 键值写反了
					//zhaoxj 20150309 键值写反应该去找调用的地方，而不是改这里。
					vo2.setString(email_field.toLowerCase(), email);
					vo2.setString("username",email);
					vo2.setString("userpassword","111111");				
					vo2.setDate("createtime",Calendar.getInstance().getTime());				
					//设置人员状态
					ParameterXMLBo bo2=new ParameterXMLBo(this.con,"1");
					HashMap map=bo2.getAttributeValues();
					if(map!=null&&map.get("resume_state")!=null) {
                        vo2.setString(((String)map.get("resume_state")).toLowerCase(),"10");
                    }
					if(map!=null&&map.get("person_type")!=null) {
                        vo2.setString(((String)map.get("person_type")).toLowerCase(),"0");
                    }
						//person_type
					dao.updateValueObject(vo2);
					
					

				}
			    
				
		}
		catch(Exception ex)
		{
				ex.printStackTrace();	
				throw GeneralExceptionHandler.Handle(ex);
		}
		return new_a0100;
	}
	
}
