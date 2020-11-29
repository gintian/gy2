package com.hjsj.hrms.businessobject.report;

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

public class TableAnalyse {
	private Connection conn=null;
	private int flag=0;            //1: tb_    2:tt_
	private int tabid=0;
	private DbWizard dbWizard=null;
	private TnameBo tnameBo=null;
	private String  sortid="";
	private DBMetaModel dbmodel=null;
	
	
	
	public TableAnalyse(Connection conn,int flag,int tabid,TnameBo tnameBo)
    {
    	this.conn=conn;
    	this.flag=flag;
    	this.tabid=tabid;
    	this.tnameBo=tnameBo;
    	dbWizard=new DbWizard(this.conn);
    	this.dbmodel=new DBMetaModel(this.conn);
    }
    
	
	public TableAnalyse(Connection conn,int flag,String sortid)
    {
    	this.conn=conn;
    	this.flag=flag;
    	this.sortid=sortid;
    	dbWizard=new DbWizard(this.conn);
    }
	
	public void analyseTable(String sortid)
	{		
		this.sortid=sortid;
		isExistTable();
		if(!"-1".equals(sortid)) {
            updateParamTable1();
        }
		if(this.flag!=1) {
            insertValue();
        }
	}
	
	
	
	
	
	
	
	
//	判断结果表(tbXXX)是否存在
	public void isExistTable()
	{
		
		try
		{
			Table table=null;
			String name="";
			if(flag==1)
			{			
				name="tb"+this.tabid;
			}
			else 
			{			
				name="tt_"+this.tabid;
			}
			table=new Table(name);
			if(!this.dbWizard.isExistTable(table.getName(),false))
			{				
				//如果不存在该统计结果表，则新建一个
				ArrayList fieldList=new ArrayList();
				if(flag==1) {
                    fieldList=this.tnameBo.getTgridBo().getTB_TableFields(this.tnameBo.getRowInfoBGrid().size());
                } else {
                    fieldList=this.tnameBo.getTgridBo().getTT_TableFields(this.tnameBo.getRowInfoBGrid().size());
                }
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				table.setCreatekey(false);					
				dbWizard.createTable(table);
				this.dbmodel.reloadTableModel(name);
				
			}
			else
			{
				updateResultTableColumns();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	
	
	public void insertValue()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			int gonnaNum=this.tnameBo.getColInfoBGrid().size();
			recset=dao.search("select unitcode,max(secid) from tt_"+this.tabid+" group by unitcode having max(secid)<"+gonnaNum);
			while(recset.next())
			{
				String unitcode=recset.getString("unitcode");
				int actNum=recset.getInt(2);
				for(int i=(actNum+1);i<=gonnaNum;i++)
				{		
					dao.insert("insert into tt_"+this.tabid+" (unitcode,secid) values('"+unitcode+"',"+i+")",new ArrayList());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	//判断表结构是否有变动，动态更新
	public void updateResultTableColumns()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		Table table=null;
		StringBuffer sql=new StringBuffer("select * from ");
		if(flag==1)
		{
			sql.append("tb"+this.tabid+"  where 1=2");
			table=new Table("tb"+this.tabid);
		}
		else 
		{
			sql.append("tt_"+this.tabid+"  where 1=2");
			table=new Table("tt_"+this.tabid);
		}
		try
		{
			
			recset=dao.search(sql.toString());
			ResultSetMetaData metaData=recset.getMetaData();
			int factNum=metaData.getColumnCount()-2;
			if(flag==1&&dbWizard.isExistField("tb"+this.tabid, "scopeid",false)){//判断字段是否存在
				factNum =factNum-1;
			}
			int gonnaNum=this.tnameBo.getRowInfoBGrid().size();
			if(factNum!=gonnaNum)
			{
					ArrayList fieldList=null;		
					if(factNum<gonnaNum)
					{
						for(int i=factNum+1;i<gonnaNum+1;i++)
						{
							Field obj=new Field("C"+i,"C"+i);
							obj.setDatatype(DataType.FLOAT);
							obj.setDecimalDigits(6);
							obj.setLength(15);							
							obj.setKeyable(true);			
							obj.setVisible(false);							
							obj.setAlign("left");
							obj.setKeyable(true);
							table.addField(obj);
						}
						this.dbWizard.addColumns(table);
					}
					else
					{
						for(int i=factNum;i>gonnaNum;i--)
						{
							Field obj=new Field("C"+i,"C"+i);
							obj.setDatatype(DataType.FLOAT);
							obj.setDecimalDigits(6);
							obj.setLength(15);							
							obj.setKeyable(true);			
							obj.setVisible(false);							
							obj.setAlign("left");
							obj.setKeyable(true);
							table.addField(obj);
						}
						this.dbWizard.dropColumns(table);
					}
			}
			if(metaData!=null) {
                metaData=null;
            }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	public void updateParamTable1()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select distinct tparam.paramscope from tpage,tparam where tparam.paramname=tpage.hz and tabid="+tabid+" and flag=9");
			while(recset.next())
			{
				int paramscope=recset.getInt("paramscope");
				updateParamTable2(paramscope);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public boolean updateParamTable1(int flag)
	{
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		boolean isValue=false;
		try
		{
			recset=dao.search("select distinct tparam.paramscope from tpage,tparam where tparam.paramname=tpage.hz and tabid="+tabid+" and flag=9");
			while(recset.next())
			{
				int paramscope=recset.getInt("paramscope");
				if(flag==paramscope)
				{
					isValue=true;
					updateParamTable2(paramscope);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isValue;
	}
	
	
	
	
	
	public void  updateParamTable2(int paramscope)
	{
		try
		{
			Table table=new Table(getTableName(paramscope));
			table.setCreatekey(false);	
			String sql="select * from tparam where paramscope="+paramscope;		
			if(!this.dbWizard.isExistTable(table.getName(),false))
			{	
				ArrayList fieldList=getParamFieldList(sql);
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				table.setCreatekey(true);					
				dbWizard.createTable(table);					
				this.dbmodel.reloadTableModel(getTableName(paramscope));
				
				//dbWizard.addPrimaryKey(table);
			}
			else
			{
				String existColumnName=getExistColumnNameStr(paramscope);			
				ArrayList fieldList=getParamFieldList(sql,existColumnName.toLowerCase());
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				if(fieldList.size()>0) {
                    this.dbWizard.addColumns(table);
                }
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//得到参数结果表的列名字符串
	public String getExistColumnNameStr(int paramscope)
	{
		StringBuffer names=new StringBuffer("");
		String sql="";
		
		if(this.dbmodel==null) {
            this.dbmodel=new DBMetaModel(this.conn);
        }
		if(!this.dbmodel.isHaveTheTable("tp_p")) {
            this.dbmodel.reloadTableModel("tp_p");
        }
		if(!this.dbmodel.isHaveTheTable("tp_s"+this.sortid)) {
            this.dbmodel.reloadTableModel("tp_s"+this.sortid);
        }
		if(!this.dbmodel.isHaveTheTable("tp_t"+tabid)) {
            this.dbmodel.reloadTableModel("tp_t"+tabid);
        }
		
		if(paramscope==0)  //全局
		{
			if(flag==1) {
                sql="select * from tp_p where 1=2";
            } else {
                sql="select * from tt_p where 1=2";
            }
		}
		else if(paramscope==1) //表类
		{
			if(flag==1) {
                sql="select * from tp_s"+this.sortid+" where 1=2";
            } else {
                sql="select * from tt_s"+this.sortid+" where 1=2";
            }
		}
		else if(paramscope==2) //表
		{
			if(flag==1) {
                sql="select * from tp_t"+tabid+" where 1=2";
            } else {
                sql="select * from tt_t"+tabid+" where 1=2";
            }
		}
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
	
	
	
	
	public ArrayList getParamFieldList(String sql,String existColumnName)
	{
		ArrayList fieldList=new ArrayList();

		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search(sql);
			while(recset.next())
			{
				if(existColumnName.indexOf(recset.getString("paramename").toLowerCase()+",")==-1)
				{
					Field a_temp=new Field(recset.getString("paramename"),recset.getString("paramename"));
					a_temp.setNullable(true);	
					String paramType=recset.getString("paramtype");
					if("数值".equals(paramType))
					{
						if(recset.getInt("paramfmt")==0)
						{
							a_temp.setDatatype(DataType.INT);
							a_temp.setDecimalDigits(recset.getInt("paramfmt"));
							a_temp.setLength(recset.getInt("paramlen"));													
							a_temp.setAlign("left");
						}
						else
						{
							a_temp.setDatatype(DataType.FLOAT);
							a_temp.setDecimalDigits(recset.getInt("paramfmt"));
							a_temp.setLength(recset.getInt("paramlen"));													
							a_temp.setAlign("left");
						}
					}
					else if("字符".equals(paramType)|| "代码".equals(paramType))
					{
						a_temp.setDatatype(DataType.STRING);
						a_temp.setSortable(true);
						a_temp.setLength(recset.getInt("paramlen"));	
					}
					else if("日期".equals(paramType))
					{						
						a_temp.setDatatype(DataType.DATE);
						a_temp.setFormat("yyyy.MM.dd");
						a_temp.setAlign("right");			
					}else if("备注".equals(paramType))
					{						
						a_temp.setDatatype(DataType.CLOB);
						a_temp.setAlign("right");			
					}
					fieldList.add(a_temp);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return fieldList;
	}
	
	
	public ArrayList getParamFieldList(String sql)
	{
		ArrayList fieldList=new ArrayList();
		
		Field temp=new Field("unitcode","unitcode");
		temp.setNullable(false);
		temp.setKeyable(true);
		temp.setDatatype(DataType.STRING);
		temp.setSortable(true);
		temp.setLength(30);	
		fieldList.add(temp);
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search(sql);
			while(recset.next())
			{
				
					Field a_temp=new Field(recset.getString("paramename"),recset.getString("paramename"));
					a_temp.setNullable(true);	
					String paramType=recset.getString("paramtype");
					if("数值".equals(paramType))
					{
						if(recset.getInt("paramfmt")==0)
						{
							a_temp.setDatatype(DataType.INT);
							a_temp.setDecimalDigits(recset.getInt("paramfmt"));
							a_temp.setLength(recset.getInt("paramlen"));													
							a_temp.setAlign("left");
						}
						else
						{
							a_temp.setDatatype(DataType.FLOAT);
							a_temp.setDecimalDigits(recset.getInt("paramfmt"));
							a_temp.setLength(recset.getInt("paramlen"));													
							a_temp.setAlign("left");
						}
					}
					else if("字符".equals(paramType)|| "代码".equals(paramType))
					{
						a_temp.setDatatype(DataType.STRING);
						a_temp.setSortable(true);
						a_temp.setLength(recset.getInt("paramlen"));	
					}
					else if("日期".equals(paramType))
					{						
						a_temp.setDatatype(DataType.DATE);
						a_temp.setFormat("yyyy.MM.dd");
						a_temp.setAlign("right");			
					}else if("备注".equals(paramType))
					{						
						a_temp.setDatatype(DataType.CLOB);
						a_temp.setAlign("right");			
					}
					fieldList.add(a_temp);
				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return fieldList;
	}
	
	
	
	
	
	
	
	//根据安装程序得到表名
	public String getTableName(int paramscope)
	{
		String tableName="";
		if(flag==1)
		{
			if(paramscope==0)   //全局
			{
				tableName="tp_p";
			}
			else if(paramscope==1)  //表类
			{
				tableName="tp_s"+this.sortid;
			}
			else if(paramscope==2)  //表
			{
				tableName="tp_t"+this.tabid;
			}
		}
		else
		{
			if(paramscope==0)   //全局
			{
				tableName="tt_p";
			}
			else if(paramscope==1)  //表类
			{
				tableName="tt_s"+this.sortid;
			}
			else if(paramscope==2)  //表
			{
				tableName="tt_t"+this.tabid;
			}
		}
		
		if(this.dbmodel==null) {
            this.dbmodel=new DBMetaModel(this.conn);
        }
		if(!this.dbmodel.isHaveTheTable(tableName)) {
            this.dbmodel.reloadTableModel(tableName);
        }
		
		
		
		return tableName;		
	}
	
	
	
}
