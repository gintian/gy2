package com.hjsj.hrms.businessobject.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class HistoryDataBo {
	Connection con;
	String salaryid;
	UserView userview;
	public HistoryDataBo(Connection con)
	{
		this.con=con;
	}
	public HistoryDataBo(Connection con,String salaryid)
	{
		this.con=con;
		this.salaryid=salaryid;
	}
	public HistoryDataBo()
	{
		
	}
	public HistoryDataBo(Connection con,UserView u)
	{
		this.con=con;
		this.userview=u;
	}
	public HistoryDataBo(Connection con,String salaryid,UserView u)
	{
		this.con=con;
		this.salaryid=salaryid;
		this.userview=u;
	}
	/**
	 * 取得个税明细表的固定字段列表
	 * @return
	 */
	private ArrayList<Field> searchCommonItemList(){
		ArrayList<Field> al=new ArrayList<Field>();
		
		Field field=new Field("NBASE","NBASE");
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		al.add(field);
		
		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(8);
		al.add(field);				
		
		field=new Field("tax_date","tax_date");
		field.setDatatype(DataType.DATE);
		al.add(field);				
		
		field=new Field("A00Z0","A00Z0");
		field.setDatatype(DataType.DATE);
		al.add(field);				

		field=new Field("A00Z1","A00Z1");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);					
		
		field=new Field("tax_max_id","tax_max_id");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		field.setKeyable(true);
		field.setNullable(false);
		al.add(field);					

		field=new Field("salaryid","salaryid");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);
		
		field=new Field("A0000","A0000");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);				
		
		field=new Field("B0110","B0110");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);
		
		field=new Field("E0122","E0122");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);	

		field=new Field("A0101","A0101");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);	
		
		field=new Field("declare_tax","declare_tax");
		field.setDatatype(DataType.DATE);
		al.add(field);	
		
		field=new Field("taxitem","taxitem");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);	
		
		field=new Field("sskcs","sskcs");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);			
		
		field=new Field("ynse","ynse");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);		
		
		field=new Field("basedata","basedata");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);	
		
		field=new Field("sl","sl");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);		
		
		field=new Field("sds","sds");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);	
		
		field=new Field("taxmode","taxmode");
		field.setDatatype(DataType.STRING);
		field.setLength(10);
		al.add(field);		
		
		field=new Field("description","description");
		field.setDatatype(DataType.STRING);
		field.setLength(200);
		al.add(field);
		
		
		field=new Field("UserFlag","UserFlag");
		field.setDatatype(DataType.STRING);
		field.setLength(50);
		al.add(field);	
		
		
		field=new Field("flag","flag");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		return al;
	}
	
	//同步个税归档表 
	public void syncSalaryTaxArchiveStrut()
	{
		
		try
		{
			DbWizard dbw=new DbWizard(this.con);
			String _str="/a0100/tax_date/a00z0/a00z1/tax_max_id/salaryid/a0000/b0110/e0122/a0101/declare_tax/taxitem/sskcs/ynse/basedata/sl/sds/taxmode/description/userflag/flag/a00z2/a00z3/ynse_field/deptid/";
			ArrayList<Field> al=searchCommonItemList();
			if(!dbw.isExistTable("taxarchive", false))
			{
				Table table=new Table("taxarchive");
				for(Field field:al)
					table.addField(field);
				dbw.createTable(table);
			}
			ContentDAO dao=new ContentDAO(this.con);  
			ArrayList chgList=searchDynaItemList();
			 
			HashMap amap=new HashMap();
			RowSet rowSet=dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			ArrayList addList = new ArrayList();
			for(int i=1;i<=data.getColumnCount();i++)
			{
					String columnName=data.getColumnName(i).toLowerCase();
					amap.put(columnName, "1");
				//	if(DataDictionary.getFieldItem(columnName)!=null)
				//		addList.add(DataDictionary.getFieldItem(columnName));
			}
			
			
			HashMap map=new HashMap();
			for(int i=0;i<chgList.size();i++)
			{
				Field field=(Field)chgList.get(i);
				FieldItem tempItem=DataDictionary.getFieldItem(field.getName().toLowerCase());
		    	if(tempItem!=null)
		    	{
		    		if(amap.get(field.getName().toLowerCase())==null)
		    			addList.add((FieldItem)tempItem.clone());
		    		map.put(tempItem.getItemid(),tempItem); 
		    	}
			}
			if(addList.size()>0)
			{
				Table table=new Table("gz_tax_mx"); 
				for(int i=0;i<addList.size();i++)
					table.addField(((FieldItem)addList.get(i)).cloneField());
				 dbw.addColumns(table);
			}
			
			
			
			
			//将固定字段加入待同步列表
			FieldItem item ;
			for(Field field:al){
				item=new FieldItem();
				item.setItemid(field.getName());
				item.setItemdesc(field.getLabel());
				if(field.getDataType()==DataType.INT||field.getDataType()==DataType.FLOAT){
					item.setItemtype("N");
					item.setDecimalwidth(field.getDecimalDigits());
				}
				else if(field.getDataType()==DataType.DATE)
					item.setItemtype("D");
				else if(field.getDataType()==DataType.STRING)
					item.setItemtype("A");
				item.setItemlength(field.getLength());
				map.put(item.getItemid(),item); 
			}
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 ArrayList delList=new ArrayList();
			 
			
			 
			  
			 rowSet=dao.search("select * from taxarchive where 1=2");
			 data=rowSet.getMetaData();
			 HashMap existMap=new HashMap();
			 
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					
					if(_str.indexOf("/"+columnName+"/")==-1)
					{
						if(amap.get(columnName)==null&&DataDictionary.getFieldItem(columnName)!=null)
							delList.add(DataDictionary.getFieldItem(columnName));
					}
					
					existMap.put(columnName, "1");
					if(map.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)map.get(columnName); 
						int columnType=data.getColumnType(i);	
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if(size<tempItem.getItemlength()&&tempItem.getItemlength()<=10) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else		
										resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
										alterList.add(tempItem.cloneField());
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else		
										resetList.add(tempItem.cloneField());
								}
								
								
								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else		
										resetList.add(tempItem.cloneField());
								}
								break;	
							case java.sql.Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}
				rowSet.close();
				
			    Table table=new Table("taxarchive");
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			    else
			    {
			    	SalaryTemplateBo bo = new SalaryTemplateBo(this.con);
			    	bo.syncGzOracleField(data,map,"taxarchive");
			    }
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }
				 
				 table.clear();
				 int n=0;
				 for(int i=0;i<addList.size();i++)
				 {
					FieldItem field=(FieldItem)addList.get(i);
					if(existMap.get(field.getItemid().toLowerCase())==null)
					{
						table.addField(field.cloneField());
						n++;
					}
				 }
				 if(n>0)
					 dbw.addColumns(table);
				 
				 table.clear();
				 n=0;
				 for(int i=0;i<delList.size();i++)
				 {
						FieldItem field=(FieldItem)delList.get(i); 
						table.addField(field.cloneField());
						n++; 
				 }
				 if(n>0)
					 dbw.dropColumns(table);
					
				if(existMap.get("a00z2")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("A00Z2","A00Z2");
					field.setDatatype(DataType.DATE);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("a00z3")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("A00Z3","A00Z3");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					tbl.addField(field);	
					dbw.addColumns(tbl);
				}
				if(existMap.get("ynse_field")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("ynse_field","ynse_field");
					field.setDatatype(DataType.STRING);
					field.setLength(5);
					tbl.addField(field);	
					dbw.addColumns(tbl);
				}
				if(existMap.get("deptid")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("deptid","deptid");
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					tbl.addField(field);	
					dbw.addColumns(tbl);
				}
				if(existMap.get("ynse")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("ynse","ynse");
					field.setDatatype(DataType.FLOAT);
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("userflag")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("UserFlag","UserFlag");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("declare_tax")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("declare_date","declare_date");
					field.setDatatype(DataType.DATE);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}			
				if(existMap.get("salaryid")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("salaryid","salaryid");
					field.setDatatype(DataType.INT);
					field.setLength(10);				
					tbl.addField(field);
					dbw.addColumns(tbl);
				}			
				if(existMap.get("taxmode")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("taxmode","taxmode");
					field.setDatatype(DataType.STRING);
					field.setLength(10);				
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("description")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("description","description");
					field.setDatatype(DataType.STRING);
					field.setLength(200);				
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("flag")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("flag","flag");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					tbl.addField(field);	
					dbw.addColumns(tbl);
				}
				for(int i=0;i<chgList.size();i++){//插入动态维护的字段 2016-10-13 zhanghua
					Field field=(Field)chgList.get(i);
					if(existMap.get(field.getName().toLowerCase())==null){
						Table tbl=new Table("taxarchive");
						tbl.addField(field);
						dbw.addColumns(tbl);
					}
				}
				 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			 
		}
		
	}
	
	
	/**
	 * @return 返回个税明细表动态维护的指标
	 */
	public ArrayList searchDynaItemList() {
		ArrayList chglist=new ArrayList();
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", con);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				Document doc=PubFunc.generateDom(ctrlvo.getString("str_value"));
				
				String str_path="/param/items";
				XPath xpath=XPath.newInstance(str_path);	
				List childlist=xpath.selectNodes(doc);
				if(childlist.size()>0)
				{
					Element element=(Element)childlist.get(0);
					String columns=element.getText();
					String[] arr=StringUtils.split(columns, ",");
					SalaryPkgBo pkgbo=new SalaryPkgBo(this.con,null,0);
					for(int i=0;i<arr.length;i++)
					{
						Field field=pkgbo.searchItemById(arr[i]);
						if(field!=null)
						{ 
							chglist.add(field);
						}
					}//for loop end.
				}//if list end.
			}//if ctrlvo end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return chglist;
	}
	
	
	
	
	
	
	
	public void syncSalaryarchiveStrut()
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rowSet =dao.search("select * from salaryarchive where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			HashMap map = new HashMap();
			//System.out.println("archive="+data.getColumnCount());
			for(int i=1;i<=data.getColumnCount();i++)
			{
				String columnName=data.getColumnName(i).toLowerCase();
				/**系统项不进行更改*/
				if("nbase".equalsIgnoreCase(columnName)|| "a0100".equalsIgnoreCase(columnName)|| "a00z0".equalsIgnoreCase(columnName)
						|| "a00z1".equalsIgnoreCase(columnName)|| "salaryid".equalsIgnoreCase(columnName)||
						"a00z2".equalsIgnoreCase(columnName)|| "a00z3".equalsIgnoreCase(columnName)||
						"a01z0".equalsIgnoreCase(columnName)|| "a0000".equalsIgnoreCase(columnName)||
						"b0110".equalsIgnoreCase(columnName)|| "e0122".equalsIgnoreCase(columnName)||
						"a0101".equalsIgnoreCase(columnName)|| "add_flag".equalsIgnoreCase(columnName)||
						"userflag".equalsIgnoreCase(columnName)|| "sp_flag".equalsIgnoreCase(columnName)||
						"curr_user".equalsIgnoreCase(columnName)|| "appuser".equalsIgnoreCase(columnName))
				{
					continue;
				}
				int columnType=data.getColumnType(i);	
				int size=data.getColumnDisplaySize(i);
				int scale=data.getScale(i);
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("columnType", columnType+"");
				bean.set("name", columnName);
				bean.set("size", size+"");
				bean.set("scale", scale+"");
				//System.out.print(columnName+",");
				map.put(columnName.toUpperCase(), bean);
			}
			//System.out.println();
			RowSet rs = dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData meta=rs.getMetaData();
			ArrayList addList = new ArrayList();
			ArrayList modifyList = new ArrayList();
			ArrayList alertList = new ArrayList();
			HashMap alertMap = new HashMap();
			//System.out.println("history="+meta.getColumnCount());
			for(int j=1;j<=meta.getColumnCount();j++)
			{
				String name=meta.getColumnName(j);
				if("nbase".equalsIgnoreCase(name)|| "a0100".equalsIgnoreCase(name)|| "a00z0".equalsIgnoreCase(name)
						|| "a00z1".equalsIgnoreCase(name)|| "salaryid".equalsIgnoreCase(name)||
						"a00z2".equalsIgnoreCase(name)|| "a00z3".equalsIgnoreCase(name)||
						"a01z0".equalsIgnoreCase(name)|| "a0000".equalsIgnoreCase(name)||
						"b0110".equalsIgnoreCase(name)|| "e0122".equalsIgnoreCase(name)||
						"a0101".equalsIgnoreCase(name)|| "add_flag".equalsIgnoreCase(name)||
						"userflag".equalsIgnoreCase(name)|| "sp_flag".equalsIgnoreCase(name)||
						"curr_user".equalsIgnoreCase(name)|| "appuser".equalsIgnoreCase(name))
				{
					continue;
				}
				//System.out.print(name+",");
				/**新的字段属性*/
				int stype=meta.getColumnType(j);
				/* 薪资发放-结构同步-tomcat7上报错问题 xiaoyun 2014-10-30 start */
				int ssize=meta.getColumnDisplaySize(j);
//				int ssize=meta.getPrecision(j);
				/* 薪资发放-结构同步-tomcat7上报错问题 xiaoyun 2014-10-30 end */
				int sscale=meta.getScale(j); 
				/**归档表里有这个字段，判断是否类型改变,如果改变，同步成与工资历史表相同*/
				if(map.get(name.toUpperCase())!=null)
				{
					/**以前的字段属性*/
					LazyDynaBean bean = (LazyDynaBean)map.get(name.toUpperCase());
					int type = Integer.parseInt((String)bean.get("columnType"));
					int size = Integer.parseInt((String)bean.get("size"));
					int scale = Integer.parseInt((String)bean.get("scale"));
					if(type==stype&&size>=ssize&&scale>=sscale)
						continue;
					Field field = new Field(name,name);
					if("appprocess".equalsIgnoreCase(name))
					{
						field.setDatatype(DataType.CLOB);
					}
					else
					{
			    		field.setDatatype(DataType.sqlTypeToType(stype));
				    	field.setLength(ssize-sscale);
				    	field.setDecimalDigits(sscale);
					}
					if(DataType.sqlTypeToType(type)==DataType.sqlTypeToType(stype)){
						alertList.add(field);
						FieldItem tempItem=DataDictionary.getFieldItem(name);
						alertMap.put(name, tempItem);
					}else
			    		modifyList.add(field);
				}
				/**归档表里没有，则加入*/
				else
				{
					Field field = new Field(name,name);
					FieldItem item=DataDictionary.getFieldItem(name.trim().toLowerCase());
					if(item!=null)
					{
						if("M".equalsIgnoreCase(item.getItemtype()))
						{
							field.setDatatype(DataType.CLOB);
						}
						else
						{
							field.setDatatype(DataType.sqlTypeToType(stype));
				    		field.setLength(ssize);
					    	field.setDecimalDigits(sscale);
						}
							
					}
					else if("appprocess".equalsIgnoreCase(name))
					{
						field.setDatatype(DataType.CLOB);
					}
					else
					{
				    	field.setDatatype(DataType.sqlTypeToType(stype));
			    		field.setLength(ssize-sscale);
				    	field.setDecimalDigits(sscale);
					}
					addList.add(field);
				}
			}
			DbWizard dbw=new DbWizard(this.con);
			Table table=new Table("salaryarchive");
		    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
		    {
			    for(int i=0;i<alertList.size();i++)
						table.addField((Field)alertList.get(i));
				if(alertList.size()>0)
						dbw.alterColumns(table);
				 table.clear();
		    }
		    else{
		    	SalaryTemplateBo bo=new SalaryTemplateBo(this.con);
		    	bo.syncGzOracleField(data,alertMap,"salaryarchive");
		    }	    	
//			for(int i=0;i<alertList.size();i++)
//				table.addField((Field)alertList.get(i));
//			if(alertList.size()>0)
//				dbw.alterColumns(table);
			table.clear();
			for(int i=0;i<modifyList.size();i++)
			{
				table.addField((Field)modifyList.get(i));
			}
			if(modifyList.size()>0)
			{
				dbw.dropColumns(table);
				dbw.addColumns(table);
			}
			table.clear();
			for(int i=0;i<addList.size();i++)
				table.addField((Field)addList.get(i));
			if(addList.size()>0)
	    		dbw.addColumns(table);
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateDbidValue()
	{
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select count(*) from salaryarchive where dbid is null");
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs = dao.search(buf.toString());
			boolean flag=false;
			if(rs.next())
			{
				if(rs.getInt(1)>0) {
					flag=true;
				}
			}
			if(flag)
			{
				buf.setLength(0);
				buf.append(" update salaryarchive set dbid=(select dbid from dbname T where UPPER(T.pre)=UPPER(salaryarchive.nbase))");
				buf.append(" where exists (select null from dbname T where UPPER(salaryarchive.nbase)=UPPER(T.pre)) and dbid is null");
				dao.update(buf.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean isHaveAdd_flag()
	{
		boolean flag = false;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rs = dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData meta=rs.getMetaData();
			for(int j=1;j<=meta.getColumnCount();j++)
			{
				String name=meta.getColumnName(j);
				if("add_flag".equalsIgnoreCase(name))
				{
					flag=true;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public void pigeonholeHistoryData(String type,String salary,String startDate,String endDate,UserView userView)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			String gditem = "NBASE,A0100,A00Z0,A00Z1,SALARYID,A00Z2,A00Z3,A01Z0,A0000,B0110,E0122,A0101,USERFLAG,SP_FLAG,CURR_USER,APPUSER";
			StringBuffer columns = new StringBuffer();
			String salaryItem = this.getSalaryItem(dao,gditem);
			columns.append(gditem);
			DbWizard dbw=new DbWizard(this.con);
			if(dbw.isExistField("salaryarchive","appprocess",false))
			{
				columns.append(",appprocess");
			}
			if(this.isHaveAdd_flag())
				columns.append(",ADD_FLAG");
			if(salaryItem.length()>0)
				columns.append(","+salaryItem);
			StringBuffer sql = new StringBuffer("");
			String where = this.getWhereSQL(type,startDate, endDate, userView, dao,1);
			sql.append("delete from salaryarchive where exists (select null from ");
			sql.append(" (select * from salaryhistory ");
			sql.append(" where ");
			sql.append(where);
			sql.append(" and sp_flag='06') ");
		    sql.append(" salaryhistory where salaryhistory.a0100=salaryarchive.a0100 ");
		    sql.append(" and UPPER(salaryhistory.nbase)=UPPER(salaryarchive.nbase) and salaryhistory.a00z0=salaryarchive.a00z0 ");
		    sql.append(" and salaryhistory.a00z1=salaryarchive.a00z1 and salaryhistory.salaryid=salaryarchive.salaryid) and "+where+"");
            dao.delete(sql.toString(), new ArrayList());
            sql.setLength(0);
			sql.append(" insert into salaryarchive ("+columns.toString()+") ");
			sql.append(" select "+columns+" from salaryhistory ");
			sql.append(" where ");
			sql.append(where);
			sql.append(" and sp_flag='06' ");
			dao.update(sql.toString());
			String dSQL="delete from salaryhistory where "+where+" and sp_flag='06'"
			+" and exists (select null from salaryarchive sa where "
			+"  sa.a00z0=salaryhistory.a00z0 and sa.a00z1=salaryhistory.a00z1 and sa.a0100=salaryhistory.a0100 and sa.nbase=salaryhistory.nbase and sa.salaryid=salaryhistory.salaryid  )";
			dao.delete(dSQL, new ArrayList());
			
			
			pigeonholeTaxData(type,salary,startDate,endDate,userView,1);
			
			
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 归档|还原个税明细表
	 * @opt  1:归档  2：还原 
	 */
	public void pigeonholeTaxData(String type,String salary,String startDate,String endDate,UserView userView,int opt)
	{
		String tableName="t#"+userView.getUserName()+"_gz_1";
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			if(opt==3) //删除个税归档表
			{
				String where = this.getWhereSQL(type,startDate, endDate, userView, dao,2);
				String dSQL="delete from taxarchive where "+where;
				dao.delete(dSQL, new ArrayList());
			}
			else
			{
			 
				DbWizard dbw=new DbWizard(this.con);
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
				
				Table table=new Table(tableName);
					
				Field field=new Field("NBASE","NBASE");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				table.addField(field);		
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATE);
				table.addField(field);				
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				field=new Field("salaryid","salaryid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				field=new Field("tax_max_id","tax_max_id");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				if(Sql_switcher.searchDbServer()!=1)
				{
					field=new Field("id","id");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					table.addField(field);	
				}
				dbw.createTable(table);
				
				if(opt==1)
				{
					StringBuffer sql = new StringBuffer("");
					sql.append(" insert into "+tableName+" (nbase,a0100,a00z0,a00z1,salaryid,tax_max_id) ");
					sql.append(" select nbase,a0100,a00z0,a00z1,salaryid,tax_max_id from gz_tax_mx ");
					sql.append(" where ");
					String where = this.getWhereSQL(type,startDate, endDate, userView, dao,2);
					sql.append(where);
					dao.update(sql.toString()); 
					setTmp2PrimaryKey(tableName);
					
					StringBuffer in_columns = new StringBuffer("");
					StringBuffer select_columns=new StringBuffer("");
					
					int num=0;
					RowSet rowSet=dao.search("select max(tax_max_id) from taxarchive");
					if(rowSet.next())
					{
							num=rowSet.getInt(1);
					}
					
					
					String asql = "select * from taxarchive where 1=2";
					rowSet = dao.search(asql);
					ResultSetMetaData data=rowSet.getMetaData();
					for(int i=1;i<=data.getColumnCount();i++)
					{
					    String columnName=data.getColumnName(i).toLowerCase();
					     
					    in_columns.append(","+columnName);
					    if("tax_max_id".equalsIgnoreCase(columnName))
					    	select_columns.append(","+tableName+".id+"+num);
					    else
					    	select_columns.append(",gz_tax_mx."+columnName);
					    
					}
					 
					sql.setLength(0);
					sql.append(" insert into taxarchive ("+in_columns.substring(1)+") ");
					sql.append(" select "+select_columns.substring(1)+" from (select * from gz_tax_mx where "+where+" and flag=1) gz_tax_mx,"+tableName+" where  ");
					sql.append(" gz_tax_mx.tax_max_id="+tableName+".tax_max_id and gz_tax_mx.salaryid="+tableName+".salaryid   and gz_tax_mx.A0100="+tableName+".A0100  and gz_tax_mx.A00Z1="+tableName+".A00Z1   and gz_tax_mx.A00Z0="+tableName+".A00Z0   and gz_tax_mx.NBASE="+tableName+".NBASE "); 
					dao.update(sql.toString());
					String dSQL="delete from gz_tax_mx where "+where+" and flag=1"
					+" and exists (select null from taxarchive ta where "
					+"  ta.a00z0=gz_tax_mx.a00z0 and ta.a00z1=gz_tax_mx.a00z1 and ta.a0100=gz_tax_mx.a0100 and ta.nbase=gz_tax_mx.nbase and ta.salaryid=gz_tax_mx.salaryid  )";
					dao.delete(dSQL, new ArrayList());
				}
				else  // 还原 
				{
					
					StringBuffer sql = new StringBuffer("");
					sql.append(" insert into "+tableName+" (nbase,a0100,a00z0,a00z1,salaryid,tax_max_id) ");
					sql.append(" select nbase,a0100,a00z0,a00z1,salaryid,tax_max_id from taxarchive ");
					sql.append(" where ");
					String where = this.getWhereSQL(type,startDate, endDate, userView, dao,2);
					sql.append(where);
					dao.update(sql.toString()); 
					setTmp2PrimaryKey(tableName);
					
					
					
					HashMap archive_map=new HashMap();
					String asql = "select * from taxarchive where 1=2";
					RowSet rowSet = dao.search(asql);
					ResultSetMetaData data=rowSet.getMetaData();
					for(int i=1;i<=data.getColumnCount();i++)
					{
					    String columnName=data.getColumnName(i).toLowerCase();
					    archive_map.put(columnName,"1");
					}
					
					
					StringBuffer in_columns = new StringBuffer("");
					StringBuffer select_columns=new StringBuffer("");
					int num=0;
					rowSet=dao.search("select max(tax_max_id) from gz_tax_mx");
					if(rowSet.next())
					{
							num=rowSet.getInt(1);
					}
					
					 
					asql = "select * from gz_tax_mx where 1=2";
					rowSet = dao.search(asql);
					data=rowSet.getMetaData();
					for(int i=1;i<=data.getColumnCount();i++)
					{
					    String columnName=data.getColumnName(i).toLowerCase();
					    if(archive_map.get(columnName)==null)
					    	continue;
					    
					    in_columns.append(","+columnName);
					    if("tax_max_id".equalsIgnoreCase(columnName))
					    	select_columns.append(","+tableName+".id+"+num);
					    else
					    	select_columns.append(",taxarchive."+columnName);
					    
					}
					 
					sql.setLength(0);
					
					String dSQL="delete from gz_tax_mx where  exists (select null from taxarchive where "+where+"  and gz_tax_mx.salaryid=taxarchive.salaryid ";
					dSQL+=" and gz_tax_mx.a0100=taxarchive.a0100  and gz_tax_mx.nbase=taxarchive.nbase  and gz_tax_mx.a00z0=taxarchive.a00z0   and gz_tax_mx.a00z1=taxarchive.a00z1 )";
					dao.delete(dSQL, new ArrayList());
					
					
					sql.append(" insert into gz_tax_mx ("+in_columns.substring(1)+") ");
					sql.append(" select "+select_columns.substring(1)+" from (select * from taxarchive where "+where+") taxarchive,"+tableName+" where  ");
					sql.append(" taxarchive.tax_max_id="+tableName+".tax_max_id and  taxarchive.salaryid="+tableName+".salaryid   and taxarchive.A0100="+tableName+".A0100  and taxarchive.A00Z1="+tableName+".A00Z1   and taxarchive.A00Z0="+tableName+".A00Z0   and taxarchive.NBASE="+tableName+".NBASE "); 
					dao.update(sql.toString());
					dSQL="delete from taxarchive where "+where
					+" and exists (select null from gz_tax_mx gtm where "
					+"  gtm.a00z0=taxarchive.a00z0 and gtm.a00z1=taxarchive.a00z1 and gtm.a0100=taxarchive.a0100 and gtm.nbase=taxarchive.nbase and gtm.salaryid=taxarchive.salaryid  )";
					dao.delete(dSQL, new ArrayList());
				}
				dao.update("delete from "+tableName); 
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 设置临时表的主键字段，自动增长类型
	 */
	private void setTmp2PrimaryKey(String tablename)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		DbWizard dbw=new DbWizard(this.con);			
		try
		{
			 
				switch(Sql_switcher.searchDbServer())
				{
				case 1://MSSQL
					buf.append("alter table ");
					buf.append(tablename);
					buf.append(" add id int identity(1,1)");
					dbw.execute(buf.toString());
					break;
				case 2://ORACLE
				case 3://DB2
					

					if(isSequence(Sql_switcher.searchDbServer(),tablename+"_seqid"))
					{
						 dbw.execute("drop sequence "+tablename+"_seqid");	
					}
					buf.append("create sequence "+tablename+"_seqid increment by 1 start with 1");
					dbw.execute(buf.toString());
					
					buf.setLength(0);
					buf.append("update ");
					buf.append(tablename);
					buf.append(" set id=");
					buf.append(Sql_switcher.sql_NextVal(tablename+"_seqid"));
					dbw.execute(buf.toString());
					buf.setLength(0);				
					buf.append("drop sequence "+tablename+"_seqid");
					dbw.execute(buf.toString());
					break;
				}//switch end.
		 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}		
	}
	
	public boolean isSequence(int dbflag,String name)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			if(dbflag==Constant.ORACEL){
				RowSet rowSet=dao.search("select sequence_name from user_sequences where lower(sequence_name)='"+name+"'");
				if(rowSet.next())
					flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 删除归档数据
	 * @param type
	 * @param salary
	 * @param startDate
	 * @param endDate
	 * @param userView
	 */
	public void deleteArchiveData(String type,String salary,String startDate,String endDate,UserView userView)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			String where = this.getWhereSQL(type,startDate, endDate, userView, dao,1); 
			String dSQL="delete from salaryarchive where "+where;
			dao.delete(dSQL, new ArrayList());

			//删除个税归档数据
		 	pigeonholeTaxData(type,salary,startDate,endDate,userView,3);
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void pigeonholeArchiveData(String type,String salary,String startDate,String endDate,UserView userView)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			String gditem = "NBASE,A0100,A00Z0,A00Z1,SALARYID,A00Z2,A00Z3,A01Z0,A0000,B0110,E0122,A0101,USERFLAG,SP_FLAG,CURR_USER,APPUSER";
			StringBuffer columns = new StringBuffer();
			columns.append(gditem);
			DbWizard dbw=new DbWizard(this.con);
			if(dbw.isExistField("salaryhistory","appprocess",false))
			{
				columns.append(",appprocess");
			}
			String salaryItem = this.getSalaryItem(dao,gditem).toUpperCase();
			String asql = "select * from salaryarchive where 1=2";
			RowSet rowSet = dao.search(asql);
			ResultSetMetaData data=rowSet.getMetaData();
			for(int i=1;i<=data.getColumnCount();i++)
			{
			    String columnName=data.getColumnName(i).toUpperCase();
			    if(salaryItem.indexOf(columnName)!=-1)
			    {
			    	columns.append(","+columnName);
			    }
			}
			if(this.isHaveAdd_flag())
				columns.append(",ADD_FLAG");
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into salaryhistory ("+columns.toString()+") ");
			sql.append(" select "+columns+" from salaryarchive ");
			sql.append(" where ");
			String where = this.getWhereSQL(type,startDate, endDate, userView, dao,1);
			sql.append(where);
			dao.update(sql.toString());
			String dSQL="delete from salaryarchive where "+where
			+" and exists (select null from salaryhistory sh where "
			+"  sh.a00z0=salaryarchive.a00z0 and sh.a00z1=salaryarchive.a00z1 and sh.a0100=salaryarchive.a0100 and sh.nbase=salaryarchive.nbase and sh.salaryid=salaryarchive.salaryid  )";
			dao.delete(dSQL, new ArrayList());

			//还原个税明细数据
			pigeonholeTaxData(type,salary,startDate,endDate,userView,2);
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @param userView
	 * @param dao
	 * @param flag 1:历史数据归档  2：个税明细归档
	 * @return
	 */
	public String getWhereSQL(String type,String startDate,String endDate,UserView userView,ContentDAO dao,int flag)
	{
		StringBuffer where = new StringBuffer();
		try
		{
	    	PositionStatBo psb = new PositionStatBo(this.con);
	    	if("1".equals(type))
	    	{
	    		String item="a00z2";
	    		if(flag==2)
	    		{
	    			item="(case when a00z2 is not null then a00z2 else a00z0 end )";
	    			
	    		}
	    	    where.append(" ("+psb.getDateSql(">=", item, startDate)+")");
	    	    where.append(" and ");
	     	    where.append(" ("+psb.getDateSql("<=", item, endDate)+")");
	     	    where.append(" and ");
	    	}

	    	if(flag==1){//1:历史数据归档  2：个税明细归档

		    		GzAnalyseBo bo = new GzAnalyseBo(this.con,this.userview);
		    		String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
		    		String sql = bo.getPrivSQL("", "", this.salaryid.replaceAll("`", ","),b_units);
		    		where.append(sql);
	    	}else{
				
				String[] temp = salaryid.split("`");
		     	HashMap map = new HashMap();
				for (int j= 0; j < temp.length; j++){
					SalaryPropertyBo bo=new SalaryPropertyBo(this.con,temp[j],1,this.userview);
			        TaxMxBo tmb = new TaxMxBo(this.con);
			        String ls_dept=tmb.getDeptID();
			        String lsDept="e0122";
			        if("true".equalsIgnoreCase(ls_dept))
			        {
			        	lsDept=bo.getCtrlparam().getValue(SalaryCtrlParamBo.LS_DEPT);
			        	lsDept=lsDept==null||lsDept.length()==0?"e0122":"deptid";
			        }					
					String item = (String) map.get(lsDept);
			    	if(item!=null&&item.length()>0){
			    		map.put(lsDept, item+",'"+temp[j]+"'");
			    	}else{
			    		map.put(lsDept, "'"+temp[j]+"'");
			    	}	
				}			
				String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");				
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						where.append("((");
						for(int i=0;i<unitarr.length;i++)
						{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				String privCode = codeid.substring(0,2);
			    				String privCodeValue = codeid.substring(2);							  
								if(privCode!=null&&!"".equals(privCode))
								{		
									where.append(" ( case");
									where.append("  when nullif("+key+",'') is not null then "+key+" ");
									where.append("  when (nullif("+key+",'') is null) and nullif(e0122,'') is not null then e0122 ");
									where.append(" else b0110 end ");
									where.append(" like '"+privCodeValue+"%' ");
									where.append(") or");
								}
		    				}
						}
						if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId())){
							where.append(" 1=1 ");
							where.append(" ) and salaryid in ("+val.toString()+")) or");
						}else{
							String _str = where.toString();
							where.setLength(0);
							where.append(_str.substring(0, _str.length()-3));
							where.append(") and salaryid in ("+val.toString()+")) or");
						}	
					}
					String str = where.toString();
					where.setLength(0);
					where.append("("+str.substring(0, str.length()-3)+")");
				}else if("UN`".equalsIgnoreCase(b_units)){
					where.append( "  1=1 ");
					where.append(" and (");
					for (int i = 0; i < temp.length; i++){
						if(i==0){
							where.append("  (salaryid = ");
						}else{
							where.append(" or (salaryid = ");
						}
						
						where.append(temp[i]);
						where.append(")");
					}
					where.append(")");
				}
				else
				{
					if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))  //201602219 dengcan
						where.append(" 1=1 ");
					else
						where.append( "  1=2 ");
				}			
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return where.toString();
	}
	public String getSalaryItem(ContentDAO dao,String gditem)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			String id=this.salaryid.replaceAll("`","','");
			String sql = "select distinct itemid from salaryset where salaryid in ('"+id+"')";
			sql+=" and UPPER(itemid) not in ('"+gditem.replaceAll(",", "','")+"')";
			RowSet rs = dao.search(sql);
			int i=0;
			while(rs.next())
			{
				if(i!=0)
					buf.append(",");
				buf.append(rs.getString("itemid"));
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
   public static int getColumnType(int type)
   {
	   int columnType=DataType.STRING;
	   switch(type)
	   {
	   case java.sql.Types.INTEGER:
		   columnType=DataType.INT;
		   break;
	   case java.sql.Types.TIMESTAMP:
		   columnType=DataType.DATE;
		   break;
	   case java.sql.Types.VARCHAR:
		   columnType=DataType.STRING;
		   break;
	   case java.sql.Types.DOUBLE:
		   columnType=DataType.DOUBLE;
		   break;
	   case java.sql.Types.NUMERIC:
		   columnType=DataType.INT;
		   break;
	   case java.sql.Types.LONGVARCHAR:
		   columnType=DataType.CLOB;
		   break;
	   }
	   return columnType;
   }
   
	public ArrayList getOperationDateList(String[] salaryids)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryarchive where ");
			StringBuffer salaryid = new StringBuffer();
			for(int i=0;i<salaryids.length;i++){
				salaryid.append(salaryids[i]+",");
			}
			salaryid.setLength(salaryid.length()-1);
			
			GzAnalyseBo bo = new GzAnalyseBo(this.con,this.userview);
    		String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
    		String sql = bo.getPrivSQL("", "", salaryid.toString(),b_units);
    		buf.append(sql);
    		buf.append(" and (");
    		for (int i = 0; i < salaryids.length; i++){
    			if(i==0){
    				buf.append("  (salaryid = ");
    			}else{
    				buf.append(" or (salaryid = ");
    			}
    			
    		    buf.append(salaryids[i]);
    		    buf.append(")");
    		}
    		buf.append(")");
			buf.append(" order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 求当前薪资类别在薪资历史数据表中已发放业务次数
	 * @param date
	 * @return
	 */
	public ArrayList getOperationCountList(String[] salaryids,String date)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z3 from salaryarchive where salaryid in (");
			for(int i=0;i<salaryids.length;i++)
			    buf.append(salaryids[i]+",");
			buf.setLength(buf.length()-1);
			buf.append(")");
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(date));
			ContentDAO dao=new ContentDAO(this.con);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=rset.getString("A00Z3");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	
	}
   
  /***
   * 取得系统项
   */
   public ArrayList getSystemItems(String[] salaryids)
   {
       ArrayList list=new ArrayList();
	try
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select distinct itemid from salaryset where salaryid in (");
		for(int i=0;i<salaryids.length;i++)
		    buf.append(salaryids[i]+",");
		buf.setLength(buf.length()-1);
		buf.append(")");
		buf.append(" and initflag=3 order by sortid");
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rset=dao.search(buf.toString());
		CommonData temp=null;
		while(rset.next())
		{			
			list.add(rset.getString(1));
		}
		rset.close();
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
	return list;
   }
   
   /***
    * 取得所有项目
    */
    public ArrayList getSalaryItems(String[] salaryids)
    {
        ArrayList list=new ArrayList();
 	try
 	{
 		StringBuffer buf=new StringBuffer();
 		buf.append("select distinct itemid,sortid from salaryset where salaryid in (");
 		for(int i=0;i<salaryids.length;i++)
 		    buf.append(salaryids[i]+",");
 		buf.setLength(buf.length()-1);
 		buf.append(")");
 		buf.append(" order by sortid");
 		ContentDAO dao=new ContentDAO(this.con);
 		RowSet rset=dao.search(buf.toString());
 		HashMap map=new HashMap();
 		while(rset.next())
 		{	
 		    String itemid = rset.getString(1);
 		    if(map.get(itemid)==null)
 		    {
 		    	list.add(itemid);
 		    	map.put(itemid, "");
 		    	if("A0101".equalsIgnoreCase(itemid))//姓名后添加审批意见
 		    		list.add("appprocess");
 		    } 			
 		}
 		rset.close();
 	}
 	catch(Exception ex)
 	{
 		ex.printStackTrace();
 	}
 	return list;
    }
   
   /**
    * 取得归档表中的所有字段
    */
   public HashMap getAllFlds()
   {
       HashMap map = new HashMap();
	ContentDAO dao = new ContentDAO(this.con);
	try
	{
	    RowSet rowSet =dao.search("select * from salaryarchive where 1=2");
	    ResultSetMetaData data=rowSet.getMetaData();
	    for(int i=1;i<=data.getColumnCount();i++)
	    {
	    	String columnName=data.getColumnName(i).toUpperCase();
	    	map.put(columnName,columnName);
	    }
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
       return map;
   }
   public boolean strutIsChange()
   {
	   boolean flag=false;
	   try
	   {
		   HashMap archiveMap = new HashMap();
		   ContentDAO dao = new ContentDAO(this.con);
		   String sql = "select * from salaryarchive where 1=2";
		   RowSet rowSet = dao.search(sql);
		   ResultSetMetaData data=rowSet.getMetaData();
		    for(int i=1;i<=data.getColumnCount();i++)
		    {
		    	String columnName=data.getColumnName(i).toUpperCase();
		    	archiveMap.put(columnName,columnName);
		    }
		    sql = "select * from salaryhistory where 1=2";
		    rowSet=dao.search(sql);
		    for(int i=1;i<=data.getColumnCount();i++)
		    {
		    	String columnName=data.getColumnName(i).toUpperCase();
		    	if(archiveMap.get(columnName)==null)
		    	{
		    		flag=true;
		    		break;
		    	}
		    }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return flag;
   }
   
   /**
	 * 取得薪资归档表权限过滤语句
	 * @param  
	 * @return
	 */
	public String getPrivPre_His(String[] salaryids)
	{
		StringBuffer pre=new StringBuffer("");
		try
		{
			ArrayList list = this.userview.getPrivDbList();		
			if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))
			{
				pre.append(" 1=1" );
			}
			else if(list==null||list.size()<=0)
			{
				pre.append(" 1=2");
			}
			else
			{
				
				String b0110_item="b0110";
				String e0122_item="e0122";
				

				for (int j = 0; j < salaryids.length; j++){//选择多个薪资帐套的时候，要考虑用户的业务范围、归属单位、归属部门控制查看人员范围  zhaoxg 2013-9-5 add
					SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.con,Integer.parseInt(salaryids[j])); 
					b0110_item="b0110";
					e0122_item="e0122";
					String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
					String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
					
					if(orgid!=null&&orgid.trim().length()>0)
					{ 
						 b0110_item=orgid;
						if(deptid!=null&&deptid.trim().length()>0)
							e0122_item=deptid;
						else
							e0122_item="";  
					}
					else if(deptid!=null&&deptid.trim().length()>0)
					{ 
						e0122_item=deptid;
						b0110_item="";
					}
				
				
				String b_units=this.userview.getUnitIdByBusiOutofPriv("1"); 
				String clientName = SystemConfig.getPropertyValue("clientName");
				if(clientName!=null&& "weichai".equalsIgnoreCase(clientName))
					b_units=this.userview.getUnit_id();
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units))
				{

					String unitarr[] =b_units.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
						pre.append(" or (salaryid = ");
						pre.append(salaryids[j]);
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
		    				   if(b0110_item.length()>0)		
		    					   pre.append(" and "+b0110_item+" like '"+codeid.substring(2)+"%' ");
		    				   else 
		    					   pre.append(" and "+e0122_item+" like '"+codeid.substring(2)+"%' ");
		    				}
		    				else if("UM".equalsIgnoreCase(codeid.substring(0,2))&&e0122_item.length()>0)
		    				{
		    					pre.append(" and "+e0122_item+" like '"+codeid.substring(2)+"%' ");
		    				}
	                 	}
		    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
		    			{
		    				pre.append(" and 1=1 ");
	                 	}
		    			pre.append(")");
		    		}
					
					
				}				 				
			}
	    		if(pre.toString().length()>0)
    			{
     				String str=pre.toString().substring(3);
    				pre.setLength(0);
    				pre.append(str);
    			}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  pre.toString() ;
	} 
 

	/**
	 * 取得权限过滤语句
	 * @param busiUnit  1:工资发放  2:工资总额  3:所得税
	 * @return
	 */
	public String getPrivPre(String busiUnit)
	{
		StringBuffer pre=new StringBuffer("");
		try
		{
			ArrayList list = this.userview.getPrivDbList();		
			if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))
			{
				pre.append(" 1=1" );
			}
			else if(list==null||list.size()<=0)
			{
				pre.append(" 1=2");
			}
			else
			{
				String b_units=this.userview.getUnitIdByBusiOutofPriv(busiUnit);
				String units=this.userview.getUnit_id();
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)&&!"1".equals(busiUnit)) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
   				for(int i=0;i<unitarr.length;i++)
   				{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
	                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
		    				}
		    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
		    				{
		    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
		    				}
	                 	}
		    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
		    			{
		    				pre.append(" or 1=1 ");
	                 	}	
		    		}
		    		if(pre.toString().length()>0)
	    			{
	     				String str=pre.toString().substring(3);
	    				pre.setLength(0);
	    				pre.append(str);
	    			}
					
				}
				else if(units!=null&&units.length()>0&&!"UN".equalsIgnoreCase(units)) //操作单位
				{
					String unitarr[] =units.split("`");
	   				for(int i=0;i<unitarr.length;i++)
	   				{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
		                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
			    				}
			    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
			    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
			    				}
		                 	}
			    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
			    			{
			    				pre.append(" or 1=1 ");
		                 	}	
			    		}
			    		if(pre.toString().length()>0)
		    			{
		     				String str=pre.toString().substring(3);
		    				pre.setLength(0);
		    				pre.append(str);
		    			}
					
				}
				else  //管理权限
				{
					for(int i=0;i<list.size();i++)
	    	    	{
	    	    		String nbase=(String)list.get(i);
		        		if (i == 0) {
		        			pre.append("(");
		        		}
		        		/**加入高级授权*/
	            		StringBuffer sql = new StringBuffer("");
			        	String priStrSql = InfoUtils.getWhereINSql(this.userview, nbase);
		     	    	sql.append("select "+nbase+"a01.A0100 ");
			        	if (priStrSql.length() > 0)
			        		sql.append(priStrSql);
			        	else
			        		sql.append(" from "+nbase+"a01");
			  	
			        	pre.append("(upper(nbase)='");
		        		pre.append(nbase.toUpperCase()+"'");
			        	pre.append(" and a0100 in ("+sql.toString()+"))");
			        	if (i != list.size() - 1) {
   		        		pre.append(" OR ");
			        	} else
			        		pre.append(")");
	    	    	}
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre.toString();
	}
	
   
	/**
	 * 取得权限过滤语句
	 * @param busiUnit  1:工资发放  2:工资总额  3:所得税
	 * @return
	 */
	public String getPrivPre(String busiUnit,String salaryid)
	{
		StringBuffer pre=new StringBuffer("");
		try
		{
			ArrayList list = this.userview.getPrivDbList();		
			if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))
			{
				pre.append(" 1=1" );
			}
			else if(list==null||list.size()<=0)
			{
				pre.append(" 1=2");
			}
			else
			{
				
				
				String b0110_item="b0110";
				String e0122_item="e0122";
				
				 
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.con,Integer.parseInt(salaryid)); 
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); 
				String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
					
				if(orgid!=null&&orgid.trim().length()>0)
				{ 
						 b0110_item=orgid;
						if(deptid!=null&&deptid.trim().length()>0)
							e0122_item=deptid;
						else
							e0122_item="";  
				}
				else if(deptid!=null&&deptid.trim().length()>0)
				{ 
						e0122_item=deptid;
						b0110_item="";
				}
				  
				
				String b_units=this.userview.getUnitIdByBusiOutofPriv(busiUnit);
				String units=this.userview.getUnit_id();
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //&&!busiUnit.equals("1")) //模块操作单位
				{
					String unitarr[] =b_units.split("`");
	   				for(int i=0;i<unitarr.length;i++)
	   				{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				 
			    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
			    				   if(b0110_item.length()>0)		
			    					   pre.append(" or "+b0110_item+" like '"+codeid.substring(2)+"%' ");
			    				   else 
			    					   pre.append(" or "+e0122_item+" like '"+codeid.substring(2)+"%' ");
			    				}
			    				else if("UM".equalsIgnoreCase(codeid.substring(0,2))&&e0122_item.length()>0)
			    				{
			    					pre.append(" or "+e0122_item+" like '"+codeid.substring(2)+"%'");
			    				}
			    				
		                 	}
			    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
			    			{
			    				pre.append(" or 1=1 ");
		                 	}	
			    		}
			    		if(pre.toString().length()>0)
		    			{
		     				String str=pre.toString().substring(3);
		    				pre.setLength(0);
		    				pre.append(str);
		    			}
						
					}
				else if(units!=null&&units.length()>0&&!"UN".equalsIgnoreCase(units)) //操作单位
				{
					String unitarr[] =units.split("`");
	   				for(int i=0;i<unitarr.length;i++)
	   				{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				 
			    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
			    				   if(b0110_item.length()>0)		
			    					   pre.append(" or "+b0110_item+" like '"+codeid.substring(2)+"%' ");
			    				   else 
			    					   pre.append(" or "+e0122_item+" like '"+codeid.substring(2)+"%' ");
			    				}
			    				else if("UM".equalsIgnoreCase(codeid.substring(0,2))&&e0122_item.length()>0)
			    				{
			    					pre.append(" or "+e0122_item+" like '"+codeid.substring(2)+"%'");
			    				}
		                 	}
			    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
			    			{
			    				pre.append(" or 1=1 ");
		                 	}	
			    		}
			    		if(pre.toString().length()>0)
		    			{
		     				String str=pre.toString().substring(3);
		    				pre.setLength(0);
		    				pre.append(str);
		    			}
					
				}
				else  //管理权限
				{
					for(int i=0;i<list.size();i++)
	    	    	{
	    	    		String nbase=(String)list.get(i);
		        		if (i == 0) {
		        			pre.append("(");
		        		}
		        		/**加入高级授权*/
	            		StringBuffer sql = new StringBuffer("");
			        	String priStrSql = InfoUtils.getWhereINSql(this.userview, nbase);
		     	    	sql.append("select "+nbase+"a01.A0100 ");
			        	if (priStrSql.length() > 0)
			        		sql.append(priStrSql);
			        	else
			        		sql.append(" from "+nbase+"a01");
			  	
			        	pre.append("(upper(nbase)='");
		        		pre.append(nbase.toUpperCase()+"'");
			        	pre.append(" and a0100 in ("+sql.toString()+"))");
			        	if (i != list.size() - 1) {
   		        		pre.append(" OR ");
			        	} else
			        		pre.append(")");
	    	    	}
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre.toString();
	}

	/**
	 * 初始化薪资历史数据 项目过滤xml
	 * @param constantXml
	 * @author ZhangHua
	 * @date 16:12 2018/6/28
	 * @throws GeneralException
	 */
	private void initFilterXmlFromHistory(ConstantXml constantXml) throws GeneralException {
		try{
			Element el=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history"));
			if(el==null){
				ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("name", "filters");
				list.add(bean);
				constantXml.addElement3(this.getParentPath(constantXml,"/Gz_history"),list);
			}
			el=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history/Serive"));
			if(el==null){
				ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
//				LazyDynaBean bean = new LazyDynaBean();
//				bean.set("name", "Serive");
//				list.add(bean);
				constantXml.addElement3(this.getParentPath(constantXml,"/Gz_history/Serive"),list);
			}
			constantXml.saveStrValue();

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 保存薪资历史数据项目过滤id
	 * @param filterId
	 * @author ZhangHua
	 * @date 16:12 2018/6/28
	 * @throws GeneralException
	 */
	public void saveFilterXmlFromHistory(String filterId) throws GeneralException {
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);
			Element el=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history"));
			String value=constantXml.getTextValue(this.getParentPath(constantXml,"/Gz_history/filters"));
			constantXml.setTextValue(this.getParentPath(constantXml,"/Gz_history/filters"),value+filterId+",");
			constantXml.saveStrValue();


		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 获取薪资历史数据项目过滤id
	 * @return
	 * @author ZhangHua
	 * @date 16:13 2018/6/28
	 * @throws GeneralException
	 */
	public String getFilterIdFromHistory() throws GeneralException {
		String value="";
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);
			Element el=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history"));
			value=constantXml.getTextValue(this.getParentPath(constantXml,"/Gz_history/filters"));
			if(value.endsWith(",")){
				value=value.substring(0,value.length()-1);
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return value;
	}

	/**
	 * 删除薪资历史数据项目过滤id
	 * @param delFilter
	 * @author ZhangHua
	 * @date 16:13 2018/6/28
	 * @throws GeneralException
	 */
	public void delFilterIdFromHistory(ArrayList<String> delFilter) throws GeneralException {
		try{
			String value="";
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);
			Element el=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history"));
			value=constantXml.getTextValue(this.getParentPath(constantXml,"/Gz_history/filters"));
			if(value.endsWith(",")){
				value=value.substring(0,value.length()-1);
			}
			String [] valueList=value.split(",");
			StringBuffer strValue=new StringBuffer();
			for (String v : valueList) {
				boolean isDel=false;
				for (String del : delFilter) {
					if(del.equalsIgnoreCase(v)){
						isDel=true;
						break;
					}
				}
				if(!isDel){
					strValue.append(v).append(",");
				}
			}
			constantXml.setTextValue(this.getParentPath(constantXml,"/Gz_history/filters"),strValue.toString());
			constantXml.saveStrValue();
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}





	/**
	 * 查询人员过滤条件
	 * @return
	 */
	public ArrayList searchManFilterFromHistory()
	{
		ArrayList list=new ArrayList();
		try
		{
			CommonData temp=new CommonData("all", ResourceFactory.getProperty("label.gz.allman"));
			list.add(temp);
			list.addAll(this.getServiceItemListFromHistory());
			temp=new CommonData("new",ResourceFactory.getProperty("label.gz.new"));
			list.add(temp);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;

	}

	/**
	 * 求过滤条件列表 读取项特殊处理 超级用户才能读取旧的数据 (人员刷选)
	 * @return
	 */
	public ArrayList getServiceItemListFromHistory()
	{
		ArrayList list=new ArrayList();
		try
		{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);

			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					//判断user_name是否存在
					if(element.getAttribute("user_name")!=null&&this.userview!=null){
						if(element.getAttributeValue("user_name")!=null&&element.getAttributeValue("user_name").equalsIgnoreCase(this.userview.getUserName())){
							CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
							list.add(comm);
						}else{	//超级用户组能看到别人的项目
							if(this.userview!=null&&this.userview.isSuper_admin()){
								CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
								list.add(comm);
							}
						}
					}else{
						if(this.userview!=null&&this.userview.isSuper_admin()){
							CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name").replaceAll("\"", "”"));
							list.add(comm);
						}
					}
//					CommonData comm=new CommonData(element.getAttributeValue("ID"),element.getAttributeValue("Name"));
//					list.add(comm);
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	/**
	 * 求每个条件过滤号对应的表达式
	 * 例   1        1+2|AXXXX=2`AYYYY=32`
	 * @return
	 */
	public HashMap getServiceItemMapFromHistory()
	{
		HashMap map=new HashMap();
		try
		{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);
			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					map.put(element.getAttributeValue("ID"), element.getAttributeValue("Expr")+"|"+element.getAttributeValue("Factor"));
				}//for end.
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}

	/**
	 * 得到做为人员筛选条件的指标的属性
	 */
	public LazyDynaBean getFiledItemProperty(String itemid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			if("SP_FLAG".equalsIgnoreCase(itemid))
			{
				bean.set("itemid","SP_FLAG");
				bean.set("itemdesc", com.hrms.hjsj.sys.ResourceFactory.getProperty("label.gz.sp"));
				bean.set("itemlength","50");
				bean.set("decwidth","0");
				bean.set("codesetid","23");
				bean.set("itemtype","A");
			}else if("SP_FLAG2".equalsIgnoreCase(itemid))
			{
				bean.set("itemid","SP_FLAG2");
				bean.set("itemdesc","报审状态");
				bean.set("itemlength","50");
				bean.set("decwidth","0");
				bean.set("codesetid","23");
				bean.set("itemtype","A");
			}
			else if(DataDictionary.getFieldItem(itemid)!=null)
			{
				FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
				bean.set("itemid", fieldItem.getItemid());
				bean.set("itemdesc", "a0000".equalsIgnoreCase(fieldItem.getItemdesc()) ? "序号" : fieldItem.getItemdesc());
				bean.set("itemlength", fieldItem.getItemlength());
				bean.set("decwidth", fieldItem.getDecimalwidth());
				bean.set("codesetid", fieldItem.getCodesetid());
				bean.set("itemtype", fieldItem.getItemtype());

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}

	public String savePersionFilter(String condid,String expr,HashMap propertymp) throws GeneralException {
		int id=0;
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);

			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			Element element=null;
			HashMap<String,Element> map=new HashMap<String, Element>();
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("ID")==null)
						continue;
					map.put(element.getAttributeValue("ID"),element);
				}//for end.
			}
			String user_name="";
			if(StringUtils.isBlank(condid)){
				user_name=this.userview.getUserName();
				Iterator iterator=map.entrySet().iterator();
				while (iterator.hasNext()){
					Map.Entry entry=(Map.Entry) iterator.next();
					int i=Integer.parseInt((String)entry.getKey());
					if(i>id){
						id=i;
					}
				}
				id++;

				Element childElement=new Element("SeiveItem");
				childElement.setAttribute("ID", String.valueOf(id));
				childElement.setAttribute("user_name", this.userview.getUserName() ); //xieguiquan 20100828
				childElement.setAttribute("Name",(String)propertymp.get("Name") );
				childElement.setAttribute("Expr", expr );
				childElement.setAttribute("Factor", (String)propertymp.get("Factor") );

				element=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history/Serive"));
				element.addContent(childElement);
				constantXml.saveStrValue();

			}else{
				id=Integer.parseInt(condid);
				element=map.get(condid);
				element.setAttribute("Name",(String)propertymp.get("Name"));
				element.setAttribute("Expr", expr );
				element.setAttribute("Factor", (String)propertymp.get("Factor") );
				constantXml.saveStrValue();

			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return String.valueOf(id);
	}

	public void  deletePersionFilter(String condid) throws GeneralException {
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			HashMap<String,String> map=new HashMap<String, String>();
			String[] temp = condid.split(",");
			for (int i = 0; i < temp.length; i++) {
				if(StringUtils.isNotBlank(temp[i])) {
					map.put(temp[i], "");
				}
			}

			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			ArrayList newList= new ArrayList();
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					Element element=(Element)childlist.get(i);
					if(!map.containsKey(element.getAttributeValue("ID")))
					{
						newList.add(element);
					}
				}
				constantXml.getElement(this.getParentPath(constantXml,"/Gz_history/Serive")).removeChildren("SeiveItem");
			}
			if(newList.size()!=0)
			{
				Element element=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history/Serive"));
				for(int i=0;i<newList.size();i++)
				{
					Element node=(Element)newList.get(i);
					element.addContent(node);
				}
			}
			constantXml.saveStrValue();

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

	public void sortPersionFilter(String sortStr) throws GeneralException {
		try{

			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			this.initFilterXmlFromHistory(constantXml);
			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			LinkedHashMap<String, LazyDynaBean> eleMap=new LinkedHashMap<String, LazyDynaBean>();
			Element element;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("user_name",element.getAttributeValue("user_name"));
					bean.set("ID",element.getAttributeValue("ID"));
					bean.set("Expr",element.getAttributeValue("Expr"));
					bean.set("Factor",element.getAttributeValue("Factor"));
					bean.set("Name",element.getAttributeValue("Name"));
					eleMap.put(element.getAttributeValue("ID"),bean);
				}//for end.
			}
			LinkedHashMap<String, LazyDynaBean> c_eleMap=(LinkedHashMap<String, LazyDynaBean>)eleMap.clone();

			String[] arr = sortStr.split("/");
			element=constantXml.getElement(this.getParentPath(constantXml,"/Gz_history/Serive"));
			element.removeChildren("SeiveItem");


			int j = 0;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null || "".equals(arr[i]))
					continue;
				LazyDynaBean bean = eleMap.get(arr[i]);

				if (bean != null) {
					j++;
					c_eleMap.remove(arr[i]);
					String user_name = this.userview.getUserName();
					if (bean.get("user_name") != null)
						user_name = (String) bean.get("user_name");
					Element childElement=new Element("SeiveItem");
					childElement.setAttribute("ID", (String) bean.get("ID"));
					childElement.setAttribute("user_name", user_name ); //xieguiquan 20100828
					childElement.setAttribute("Name",(String) bean.get("Name"));
					childElement.setAttribute("Expr", (String) bean.get("Expr") );
					childElement.setAttribute("Factor", (String) bean.get("Factor") );
					element.addContent(childElement);
				}

			}
			Iterator iter = c_eleMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				LazyDynaBean bean = (LazyDynaBean) entry.getValue();
				if (bean != null) {
					String user_name = this.userview.getUserName();
					if (bean.get("user_name") != null)
						user_name = (String) bean.get("user_name");
					Element childElement=new Element("SeiveItem");
					childElement.setAttribute("ID",String.valueOf(j));
					childElement.setAttribute("user_name", user_name ); //xieguiquan 20100828
					childElement.setAttribute("Name",(String) bean.get("Name"));
					childElement.setAttribute("Expr", (String) bean.get("Expr") );
					childElement.setAttribute("Factor", (String) bean.get("Factor") );
					element.addContent(childElement);
					j++;
				}
			}
			constantXml.saveStrValue();

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

	public void reNamePersionFilter(String name,String condid) throws GeneralException {
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("ID").equalsIgnoreCase(condid)){
						element.removeAttribute("Name");
						element.setAttribute("Name", name);
						break;
					}
				}//for end.
			}
			constantXml.saveStrValue();
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public ArrayList<String> getPersionFilterField(String condid) throws GeneralException {
		ArrayList<String>  buf=new ArrayList<String>();
		try{
			ConstantXml constantXml=new ConstantXml(this.con,"GZ_PARAM");
			List childlist=constantXml.getAllChildren(this.getParentPath(constantXml,"/Gz_history/Serive"));
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					if(element.getAttributeValue("ID").equalsIgnoreCase(condid)){
						break;
					}
				}//for end.
			}
			if(element!=null) {
				String factor = element.getAttributeValue("Factor");
				String[] temp = factor.split("`");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i] == null || "".equals(temp[i]))
						continue;
					buf.add(temp[i].substring(0, 5));
				}
			}

		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return  buf;
	}


	public ArrayList<Field> searchAllGzItem()
	{
		ArrayList<Field> list=new ArrayList<Field>();
		StringBuffer strread=new StringBuffer();
		/**只读字段*/
		strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,E01A1");
		StringBuffer format=new StringBuffer();
		format.append("###################");
		StringBuffer buf=new StringBuffer();
		buf.append("select itemid,max(itemtype) as itemtype,Max(codesetid) as codesetid,MAX(itemdesc) as itemdesc,MAX(itemlength) as itemlength" +
				" ,MAX(decwidth) as decwidth from salaryset group by itemid ");
		Field field=null;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.con);

			rset=dao.search(buf.toString());

			boolean isOk=false;
//			加上报审标识
				field=new Field("sp_flag2","报审状态");
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);




			/**加上审批标识*/
			field=new Field("sp_flag", com.hrms.hjsj.sys.ResourceFactory.getProperty("label.gz.sp"));
			field.setLength(50);
			field.setCodesetid("23");
			field.setDatatype(DataType.STRING);
			field.setReadonly(true);
			list.add(field);
			/**加上审批意见*/
			field=new Field("appprocess","审批意见");
			field.setDatatype(DataType.CLOB);
			field.setAlign("left");
			field.setReadonly(true);
			list.add(field);



			//追加标记
			field=new Field("add_flag","追加标记");
			field.setDatatype(DataType.INT);
			field.setAlign("left");
			field.setVisible(false);
			list.add(field);
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
			while(rset.next())
			{
				String itemid=rset.getString("itemid");
				if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
				{
					FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(_tempItem==null)
						continue;

				}

				field=new Field(itemid,rset.getString("itemdesc"));

				String type=rset.getString("itemtype");
				String codesetid=rset.getString("codesetid");
				field.setCodesetid(codesetid);
				/**字段为代码型,长度定为50*/
				if("A".equals(type))
				{
					field.setDatatype(DataType.STRING);

					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						field.setLength(rset.getInt("itemlength"));
					else
						field.setLength(50);
					field.setAlign("left");
				}
				else if("M".equals(type))
				{
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");
				}
				else if("N".equals(type))
				{

					field.setLength(rset.getInt("itemlength"));
					int ndec=rset.getInt("decwidth");
					field.setDecimalDigits(ndec);
					if(ndec>0)
					{
						field.setDatatype(DataType.FLOAT);
						//format.setLength(ndec);
						field.setFormat("####."+format.toString().substring(0,ndec));
					}
					else
					{
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}
					field.setAlign("right");
				}
				else if("D".equals(type))
				{
					field.setLength(20);
					//field.setDatatype(DataType.STRING);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");
				}
				else
				{
					field.setDatatype(DataType.STRING);
					field.setLength(rset.getInt("itemlength"));
					field.setAlign("left");
				}
				/**对人员库标识，采用“@@”作为相关代码类*/
				if("nbase".equalsIgnoreCase(itemid))
				{
					field.setCodesetid("@@");
					field.setReadonly(true);
				}


				list.add(field);
			}//loop end.

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return list;
	}

	private String rootNodePath="";
	/**
	 * 为了兼容Params大小写问题 根据库里的大小写动态拼接
	 * @param constantXml
	 * @param path
	 * @return
	 * @author ZhangHua
	 * @date 16:16 2018/8/2
	 */
	private String getParentPath(ConstantXml constantXml,String path){
		String rootName="/Params";
		if(StringUtils.isNotBlank(this.rootNodePath)){
			rootName=this.rootNodePath;
		}else {
			if (constantXml.getElementList(rootName).size() == 0) {
				if (constantXml.getElementList(rootName.toLowerCase()).size() != 0) {
					rootName = rootName.toLowerCase();
				}
			}
			this.rootNodePath=rootName;
		}
		return rootName+path;
	}
   
}
