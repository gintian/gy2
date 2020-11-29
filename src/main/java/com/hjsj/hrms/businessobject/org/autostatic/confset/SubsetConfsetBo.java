package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class SubsetConfsetBo {
	
	public SubsetConfsetBo()
	{
		
	}
	/**
	 * 获得子集List
	 * @return ArrayList
	 */
	public ArrayList getsubsetlist()
	{
		ArrayList retlist = new ArrayList();
		retlist = DataDictionary.getFieldSetList(1, Constant.ALL_FIELD_SET);		
		for(int i=0;i<retlist.size();i++){
			FieldSet fs=(FieldSet)retlist.get(i);
				String fid=fs.getFieldsetid();
				if(/*fid.startsWith("A")*/"a00".equalsIgnoreCase(fid)||"a01".equalsIgnoreCase(fid) || "b01".equalsIgnoreCase(fid)
						|| "k01".equalsIgnoreCase(fid)||"a00".equalsIgnoreCase(fid)
						||"b00".equalsIgnoreCase(fid)|| "k00".equalsIgnoreCase(fid) ){
					retlist.remove(i);
					i--;
				}			
		}
		return retlist;
	}
	/**
	 * 获得子集List
	 * @return ArrayList
	 */		
	public ArrayList getconfsetlist()
	{
		ArrayList retlist = new ArrayList();

		retlist = DataDictionary.getFieldSetList(1, Constant.ALL_FIELD_SET);
		
		for(int i=0;i<retlist.size();i++){
			FieldSet fs=(FieldSet)retlist.get(i);
				String fid=fs.getFieldsetid();
				if(fid.startsWith("A")){
					
					retlist.remove(i);
					
					i--;
				}			
		}
		return retlist;
	}
/**
 * 设置变化子集
 * @param changeflagarray   changeflag 和 子集    eg:  1-A04
 * @param dao    ContentDAO
 * @param uv     UserView
 * @param conn   Connection
 * @throws SQLException
 */
	public void updatesubset(String changeflagarray,Connection conn) throws SQLException
	{
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			String temp ="";
			RowSet rs;
			if(changeflagarray ==null )
			{
				changeflagarray="";
			}
				if(!"".equals(changeflagarray) && changeflagarray.length()>0)
				{
					String[] changeflagstr = changeflagarray.split("/");
					for(int i=0;i<changeflagstr.length;i++)
					{
						temp = changeflagstr[i].toString();
						int index = temp.indexOf(",");
						String fieldsetid = temp.substring(0,index);
						String changeflag = temp.substring(index+1);
						String changeflagrs = "";
						//  查找changeflag的值
						String findchangeflagsql = "select changeflag from fieldset where fieldsetid = '"+fieldsetid+"'";
						rs = dao.search(findchangeflagsql);
						if(rs.next())
						{
							changeflagrs = rs.getString("changeflag");
						}
						if(!changeflag.equals(changeflagrs)) 
						{
							//  修改fieldset表的changeflag的值
							String fieldsetsql = "update fieldset set changeflag ='"+changeflag+"' where fieldsetid= '"+fieldsetid+"'";
							int reulstnum = dao.update(fieldsetsql);
							if("0".equals(changeflag) && !"0".equals(changeflagrs))
							{
								//  修改表结构
								this.updatetable(fieldsetid,changeflag,conn,dao);
							}
							else if("0".equals(changeflagrs) && !"0".equals(changeflag))
							{
								//  修改表结构
								this.updatetable(fieldsetid,changeflag,conn,dao);
							}

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
 * 更新表结构
 * @param fieldsetid 子集
 * @param changeflag 变化子集；  0：一般变化子集     1：按月变化子集    2：按年变化子集
 * @param dao  ContentDAO
 * @param conn 
 * @param uv   UserView
 * @param conn   Connection
 */
	public void updatetable(String fieldsetid,String changeflag, Connection conn,ContentDAO dao)
	{
		try
		{
//			ContentDAO dao = new ContentDAO(conn);
			RowSet rs;
			String fielditemsql = "";
			String tablename = "";
			UpdateTableOper uto=new UpdateTableOper(conn);
			ArrayList columnlist = new  ArrayList();
			com.hrms.frame.dbstruct.Field f0=uto.getField(false,fieldsetid+"Z0",ResourceFactory.getProperty("hmuster.label.nybs"),"D",10,0);
			com.hrms.frame.dbstruct.Field f1=uto.getField(false,fieldsetid+"Z1",ResourceFactory.getProperty("hmuster.label.counts"),"N",3,0);		
			columnlist.add(0,f0);
			columnlist.add(1,f1);
			StringBuffer fielditemsbsql = new StringBuffer();
			
			if("0".equals(changeflag))
			{
				String findz0sql = "select * from fielditem where itemid = '"+fieldsetid+"Z0' " ;
				rs = dao.search(findz0sql);
				if(rs.next())
				{
					//	 修改fielditem表
					fielditemsql = "delete fielditem where itemid = '"+fieldsetid+"Z0'  ";
//					System.out.println(fielditemsql);
					dao.update(fielditemsql);
				}
				String findz1sql = "select * from fielditem where itemid = '"+fieldsetid+"Z1' " ;
				rs = dao.search(findz1sql);
				if(rs.next())
				{
					//	修改fielditem表
					fielditemsql = "delete fielditem where  itemid = '"+fieldsetid+"Z1' ";
					dao.update(fielditemsql);
				}
				   //  修改表结构
				if(fieldsetid.startsWith("A"))
				{
					ArrayList dbnamelist = new ArrayList();
					//  得到数据库前缀
					dbnamelist = dao.searchDynaList(" select pre from dbname");
					for(Iterator it = dbnamelist.iterator();it.hasNext();)
					{
						DynaBean dynabean=(DynaBean)it.next();
						String dbname = dynabean.get("pre").toString();
						tablename = dbname+fieldsetid;										
						uto.create_update_Table(tablename,columnlist,true);

					}
				}
				else
				{
					tablename = fieldsetid;
					
					uto.create_update_Table(tablename,columnlist,true);
				}
			}
			else
			{
				if(fieldsetid.startsWith("A"))
				{		
					tablename = "usr"+fieldsetid;						
				}
				else
				{
					tablename = fieldsetid;					

				}
				
				String findz0sql = "select * from fielditem where itemid = '"+fieldsetid+"Z0' " ;
				rs = dao.search(findz0sql);
				
				if(!rs.next())				
				{
					//	修改fielditem表
					RowSet disrs = dao.search(" select max(displayid) FROM fielditem where fieldsetid like '"+fieldsetid+"'");
					String displayid = "";
					if(disrs.next())
					{
						 displayid = disrs.getString(1);
					}
					fielditemsbsql.append("insert into fielditem (displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,");
					fielditemsbsql.append("itemdesc,itemlength,decimalwidth,codesetid,displaywidth) values ('"+displayid+"'");
					fielditemsbsql.append(",'"+fieldsetid+"','"+fieldsetid+"Z0',1,'1111111111111111111','D',");
					fielditemsbsql.append("'"+ResourceFactory.getProperty("hmuster.label.nybs")+"',10,0,'0',14 ) ");
//					System.out.println(fielditemsbsql.toString());
					dao.update(fielditemsbsql.toString());
					fielditemsbsql.setLength(0);
					
				}
				String findz1sql = "select * from fielditem where itemid = '"+fieldsetid+"Z1' " ;
				rs = dao.search(findz1sql);
				if(!rs.next())
				{
					RowSet disrs = dao.search(" select max(displayid) FROM fielditem where fieldsetid like '"+fieldsetid+"'");
					String displayid = "";
					if(disrs.next())
					{
						 displayid = disrs.getString(1);
					}
					
					fielditemsbsql.append("insert into fielditem (displayid,fieldsetid,itemid,useflag,moduleflag,itemtype,");
					fielditemsbsql.append("itemdesc,itemlength,decimalwidth,codesetid,displaywidth) values ('"+displayid+"'");			
					fielditemsbsql.append(",'"+fieldsetid+"','"+fieldsetid+"Z1',1,'1111111111111111111','N',");
					fielditemsbsql.append("'"+ResourceFactory.getProperty("hmuster.label.counts")+"',3,0,'0',10 ) ");
					dao.update(fielditemsbsql.toString());
					fielditemsbsql.setLength(0);
					
				}
				if(fieldsetid.startsWith("A"))
				{
					ArrayList dbnamelist = new ArrayList();
					dbnamelist = dao.searchDynaList(" select pre from dbname");
					for(Iterator it=dbnamelist.iterator();it.hasNext();)
					{
						DynaBean dynabean=(DynaBean)it.next();
						String dbname = dynabean.get("pre").toString();
						tablename = dbname+fieldsetid;						
						uto.create_update_Table(tablename,columnlist,false);
					}
				}
				else
				{
					tablename = fieldsetid;					
					uto.create_update_Table(tablename,columnlist,false);

				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
		
		


	
}
