package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
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
public class UpdateTableOper {
	private Connection conn=null;
	private DbWizard dbWizard=null;
	private DBMetaModel dbmodel=null;
	public UpdateTableOper(Connection con)
	{
		this.conn=con;
		this.dbWizard=new DbWizard(this.conn);
    	this.dbmodel=new DBMetaModel(this.conn);
	}


	/**
	 * 创建 或 更新 表
	 * @param tableName 表名
	 * @param fieldList 表列信息
	 * @param flag      true :可以删除不需要的列（目前只针对 数字类型 的列）   false:不删
	 */
	public void create_update_Table(String tableName,ArrayList fieldList,boolean flag)
	{
		
		try
		{
			Table table=new Table(tableName);
			if(!dbWizard.isExistTable(tableName,false))
			{
				
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field field=(Field)t.next();
					table.addField(field);
				}
				dbWizard.createTable(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tableName);
				flag=false;
			}
			else
			{
				analyseTableStructure(fieldList,tableName,flag);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
		
	
	/**
	 * 分析表结构，进行动态更新
	 * @param new_fieldList  新表的结构
	 * @param tableName      表名
	 * @param flag           true :可以删除不需要的列（目前只针对 数字类型 的列）   false:不删
	 */
	public void analyseTableStructure(ArrayList new_fieldList,String tableName,boolean flag)
	{
		Table table=new Table(tableName);
		try
		{
			String exist_tableNames=getExistColumnNameStr(tableName);
			exist_tableNames=exist_tableNames.toLowerCase();
			StringBuffer new_tableNames=new StringBuffer();
			int i=0;
			for(Iterator t=new_fieldList.iterator();t.hasNext();)
			{
				Field aField=(Field)t.next();
				String columnName=aField.getName();
				new_tableNames.append(columnName.toLowerCase()+" , ");
				if(exist_tableNames.indexOf(columnName.toLowerCase()+",")==-1)
				{
					i++;
					table.addField(aField);
				}
			}
			if(i>0)
			{
				this.dbWizard.addColumns(table);
			}
			table=new Table(tableName);
			if(flag)
			{
				i=0;
				String[] str_arr=exist_tableNames.split(",");
				for(int a=0;a<str_arr.length;a++)
				{
					if(new_tableNames.indexOf(str_arr[a].toLowerCase())!=-1)
					{
						i++;
						Field obj=getField(false,str_arr[a],str_arr[a],"N",15,4);
						table.addField(obj);
						
					}
				}
				if(i>0)
				{
					this.dbWizard.dropColumns(table);
				}
			}
			this.dbmodel.reloadTableModel(tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	//得到表的列名字符串
	public String getExistColumnNameStr(String tableName)
	{
		
		String sql="select * from "+tableName+" where 1=2";
		StringBuffer names=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=dao.search(sql);
			ResultSetMetaData metaData=recset.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{
				names.append(metaData.getColumnName(i)+",");
			}
			if(metaData!=null) {
                metaData=null;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return names.toString();
	}
	

	

	/**
	 * 
	 * @param primaryKey	是否是主键
	 * @param fieldname     列名
	 * @param fieldDesc     列描述
	 * @param type          数据类型
	 * @param length        长度
	 * @param decimalLength 小数点位数
	 * @return
	 */
	public Field getField(boolean primaryKey,String fieldname,String fieldDesc,String type,int length,int decimalLength)
	{
		Field obj=new Field(fieldname,fieldDesc);
		if("A".equals(type))
		{	
			obj.setDatatype(DataType.STRING);
			obj.setKeyable(primaryKey);	
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);
			obj.setLength(length);
			
		}
		else if("M".equals(type))
		{
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);			
			obj.setVisible(true);
			obj.setAlign("left");					
		}
		else if("D".equals(type))
		{
			
			obj.setDatatype(DataType.DATE);
			obj.setKeyable(false);			
			obj.setVisible(true);												
		}	
		else if("N".equals(type))
		{
			obj.setDatatype(DataType.FLOAT);
			obj.setDecimalDigits(decimalLength);
			obj.setLength(length);							
			obj.setKeyable(primaryKey);		
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);								
		}	
		else if("I".equals(type))
		{		
			obj.setDatatype(DataType.INT);
			obj.setKeyable(primaryKey);		
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);	
		}		
		return obj;
	}
	
}
