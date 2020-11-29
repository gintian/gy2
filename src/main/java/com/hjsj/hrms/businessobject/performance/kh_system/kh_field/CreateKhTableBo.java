package com.hjsj.hrms.businessobject.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateKhTableBo implements  Serializable{
	private Connection conn=null;
	private String tablename;
	private UserView userView;
	private int database;
	public CreateKhTableBo(Connection conn, String tablename,UserView userview){
		super();
		this.conn = conn;
		this.tablename=tablename;
		this.userView=userview;
	}
	public void createTable() throws SQLException {
		String temName="OrgPointTable";
		
		DbWizard dbwizard=new DbWizard(this.conn);
		String sql="";
		if(!dbwizard.isExistTable(temName,false)){
			Sql_switcher sw=new Sql_switcher(); 
			this.database=sw.searchDbServer(); 
			try {
				String sql2="select * from "+this.tablename+" where 1=2";

				Table table=new Table(temName);
				this.addtable(sql2, table);
				
				dbwizard.createTable(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(temName);		
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}else{
			updatetable(temName);
		}
	}
	public void updatetable(String temName){
		ArrayList alist=getallfield();
		DbWizard dbwizard=new DbWizard(this.conn);
		HashMap att_map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet = null;
		Table table=new Table(temName);
		try {
			rowSet = dao.search("select * from "+temName+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				att_map.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}
			for(int i=0;i<alist.size();i++){
				FieldItem item=(FieldItem)alist.get(i);
				if(item!=null){
					if(att_map.get(item.getItemid().toLowerCase())==null)
					{
						table.addField(item);	
					}
				}
			}

			if(table.size()>0) {
                dbwizard.addColumns(table);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public ArrayList getallfield(){
		ArrayList list=new ArrayList();
		String sql="select * from "+this.tablename+" where 1=2";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try {
			rowSet=dao.search(sql);
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{	
				FieldItem item=DataDictionary.getFieldItem(mt.getColumnName(i+1).toLowerCase());
				if(item!=null) {
                    list.add(item);
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	public void copyData(String b0110,String unitcode){
		String sql="select * from OrgPointTable where B0110='"+b0110+"' and username='"+this.userView.getUserName()+"'";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet = null;
		ArrayList list=new ArrayList();
		StringBuffer str=new StringBuffer();
		StringBuffer str2=new StringBuffer();
		ArrayList lsit=new ArrayList();
		FieldItem item;
		try {
			rowSet = dao.search(sql);
			if(rowSet.next()){
				sql="delete from OrgPointTable where B0110='"+b0110+ "' and username='"+this.userView.getUserName()+"'";
				dao.delete(sql, list);
			}
			sql="select * from OrgPointTable where 1=2";
			rowSet=dao.search(sql);
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{	
				if("username".equalsIgnoreCase(mt.getColumnName(i+1))) {
                    continue;
                }
				if("b0110".equalsIgnoreCase(mt.getColumnName(i+1))) {
                    str2.append("('"+b0110+"') as b0110,");
                } else {
                    str2.append(mt.getColumnName(i+1)+",");
                }
				str.append(mt.getColumnName(i+1)+",");
				
			}
			str.append("username");
			str2.append("'"+this.userView.getUserName()+"' as username");
/*			
			sql="select * from "+this.tablename+" where 1=2";
			rowSet=dao.search(sql);
			ResultSetMetaData mt1=rowSet.getMetaData();
			for(int i=0;i<mt1.getColumnCount();i++)
			{	
				item=DataDictionary.getFieldItem(mt1.getColumnName(i+1).toLowerCase());
				if(item!=null)
				{
					if(mt1.getColumnName(i+1).equalsIgnoreCase("b0110"))
					{
						str2.append("('"+b0110+"') as b0110,");
					}else
					{
						str2.append(mt1.getColumnName(i+1)+",");
					}
				}
			}
			str2.append("'"+this.userView.getUserName()+"' as username");
*/			
			
			sql="insert into OrgPointTable("+str.toString()+") select "+str2.toString()+ " from "+this.tablename+" where b0110='" +unitcode+"'";
			dao.insert(sql, list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void addtable(String sql2,Table table){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rst = null;
		FieldItem item;
		Field field;
		try {
			rst = dao.search(sql2);
			ResultSetMetaData mt=rst.getMetaData();
			for(int k=0;k<mt.getColumnCount();k++){
				
				item=DataDictionary.getFieldItem(mt.getColumnName(k+1).toLowerCase());
				
				if(item!=null){
					if("b0110".equalsIgnoreCase(mt.getColumnName(k+1).toLowerCase())){
						field=new Field("B0110",item.getItemdesc());
						field.setKeyable(true);
						field.setNullable(false);
						field.setLength(50);
						field.setDatatype(DataType.STRING);
						table.addField(field);
						continue;
					}
					if("i9999".equalsIgnoreCase(mt.getColumnName(k+1).toLowerCase())){
						field=new Field("i9999",item.getItemdesc());
						field.setKeyable(true);
						field.setNullable(false);
						field.setLength(30);//int型 长度不能超过38 zhaoxg add 2014-10-15
						field.setDatatype(DataType.INT);
						table.addField(field);
						continue;
					}
					table.addField(item);
				}
			}
			field=new Field("username","username");
			field.setKeyable(true);
			field.setNullable(false);
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(rst!=null) {
                    rst.close();
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
}
