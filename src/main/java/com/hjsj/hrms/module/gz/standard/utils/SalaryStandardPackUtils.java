package com.hjsj.hrms.module.gz.standard.utils;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 导出薪资历史沿革数据使用工具类
 * @Titile: SalaryStandardPackUtils
 * @Description:
 * @Company:hjsj
 * @Create time: 2019年12月26日下午2:48:49
 * @author: qinxx
 * @version 1.0
 *
 */
public class SalaryStandardPackUtils {
	private Connection conn=null;
	private UserView userView;
	public SalaryStandardPackUtils(Connection con)
	{
		this.conn=con;
	}
	public SalaryStandardPackUtils(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
	}
	
	/**
	 * 获得需导出的数据
	 * @param ids  
	 * @param type  1：gz_stand_pkg  2:gz_stand 3:gz_item 
	 * @return
	 */
	public LazyDynaBean getOutPutTableInfo(String ids,String type, String pkg_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		String[] a_ids=ids.split("#");
		String columns="";
		String keycolumns="";
		if("1".equals(type))
		{
			columns="NAME,START_DATE,END_DATE,STATUS";
			keycolumns="PKG_ID";
		}
		else if("2".equals(type))
		{
			columns="NAME,DB_TYPE,UNIT_TYPE,FLAG,HFACTOR,HCONTENT,VFACTOR,VCONTENT,ITEM,LEXPR,FACTOR,S_VFACTOR,S_HFACTOR,b0110,createorg";
			keycolumns="ID";
		}
		else if("3".equals(type))
		{
			columns="ID,HVALUE,VVALUE,S_HVALUE,S_VVALUE,STANDARD";
			keycolumns="";
		}
		abean.set("columns",columns);
		abean.set("keycolumns",keycolumns);		
		ArrayList list=getTableContextInfoList(a_ids,type,columns,keycolumns,pkg_id);
		abean.set("records",list);
		abean.set("rowcount",String.valueOf(list.size()));	
		return abean;
	}
	
	
	
	public ArrayList getTableContextInfoList(String[] ids,String type,String columns,String keycolumns,String pkg_id)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tableName=getTableName(type);
			HashMap columnTypeMap=getColumnTypeMap(tableName);
			int num=ids.length/200;
			if(ids.length%200!=0)
				num++;
			for(int n=0;n<num;n++)
			{
				String sql=getSql(ids,type,n,pkg_id);
				RowSet rowSet=dao.search(sql);
				LazyDynaBean abean=null;
				SimpleDateFormat  df=new SimpleDateFormat("yyyy-MM-dd");
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					
					for(int j=0;j<2;j++)
					{
						String temp="";
						if(j==0)
							temp=columns;
						else
							temp=keycolumns;
						if(temp.trim().length()>0)
						{
							String[] temps=temp.split(",");
							for(int i=0;i<temps.length;i++)
							{
								if(columnTypeMap.get(temps[i].toLowerCase())!=null)
								{
									if("D".equals((String)columnTypeMap.get(temps[i].toLowerCase())))
									{
										if(rowSet.getString(temps[i])==null)
											abean.set(temps[i],"");
										else
											abean.set(temps[i],df.format(rowSet.getDate(temps[i])));
									}
									else if("M".equals((String)columnTypeMap.get(temps[i].toLowerCase())))
									{
										abean.set(temps[i],Sql_switcher.readMemo(rowSet,temps[i]));
									}	
									else
									{
										abean.set(temps[i],rowSet.getString(temps[i])==null?"":rowSet.getString(temps[i]));
									}
								}
								else
									abean.set(temps[i],rowSet.getString(temps[i])==null?"":rowSet.getString(temps[i]));
							}
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
	    
	
	public String getSql(String[] ids,String type,int num,String pkg_id)
	{
		String sql="";
		StringBuffer whl=new StringBuffer("");
		
		for(int i=num*200;i<(num+1)*200;i++)
		{
			if(ids.length>i)
			{
				if(ids[i].length()>0)
					whl.append(","+ids[i]);
			}
			else
				break;
		}
		
		if("1".equals(type)){
			sql="select * from gz_stand_pkg where pkg_id in ("+whl.substring(1)+")";
	    }else if("2".equals(type)){
			sql="select gz_stand.*,b0110,createorg from gz_stand left join (select * from gz_stand_history where pkg_id="+pkg_id+") gzh on gz_stand.id=gzh.id where gz_stand.id in ("+whl.substring(1)+") order by gz_stand.id";
		}else if("3".equals(type)){
			sql="select * from gz_item where id in ("+whl.substring(1)+") order by gz_item.id";
		}
		return sql;
	}
	
	/**
	 * 取得表列类型
	 * @param tableName
	 * @return
	 */
	public HashMap getColumnTypeMap(String tableName)
	{
		HashMap columnTypeMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			ResultSet resultSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData metaData=resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String name=metaData.getColumnName(i).toLowerCase();
				String type="A";
				int  columnType=metaData.getColumnType(i);
				switch (columnType) {
				case Types.LONGVARCHAR:
				{
					type="M";
					break;
				}
				case Types.TINYINT:
				{
					type="N";
					break;
				}
				case Types.SMALLINT:
				{
					type="N";
					break;
				}
				case Types.INTEGER:
				{
					type="N";
					break;
				}
				case Types.BIGINT:
				{
					type="N";
					break;
				}
				case Types.FLOAT:
				{
					type="N";
					break;
				}
				case Types.DOUBLE:
				{
					type="N";
					break;
				}
				case Types.DECIMAL:
				{
					type="N";
					break;
				}
				case Types.NUMERIC:
				{
					type="N";
					break;
				}
				case Types.REAL: 
				{
					type="N";
					break;
				}
				case Types.DATE:
				{
					type="D";
					break;
				}
				case Types.TIME:
				{
					type="D";
					break;
				}
				case Types.TIMESTAMP: {
					type="D";
					break;
				}
				default:
					type="A";
					break;
				}
				columnTypeMap.put(name,type);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return columnTypeMap;
	}
	/**
	 * 根据导出的类型获取表名称
	 * @param type
	 * @return
	 */
	public String getTableName(String type)
	{
		String tableName="";
		if("1".equals(type))
			tableName="gz_stand_pkg";
		else if("2".equals(type))
			tableName="gz_stand_history";
		else if("3".equals(type))
			tableName="gz_item_history";
		else if("4".equals(type))
			tableName="";
		return tableName;
	}
	
	
	

}
