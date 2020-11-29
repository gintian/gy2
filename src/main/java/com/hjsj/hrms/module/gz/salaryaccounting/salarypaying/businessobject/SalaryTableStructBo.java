package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 *<p>Title:薪资表结构操作类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-7-22</p> 
 *@author dengc
 *@version 7.x
 */
public class SalaryTableStructBo {
	private Connection conn=null; 
	/**登录用户*/
	private UserView userview;
	private String onlyField="";  //系统设置的唯一指标
	

	public SalaryTableStructBo(Connection conn,UserView userview) {
		this.conn = conn; 
		this.userview=userview;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		if(uniquenessvalid!=null&&!"0".equals(uniquenessvalid)&&!"".equals(uniquenessvalid)&&onlyname!=null&&onlyname.trim().length()>0)
			onlyField=onlyname;
	}
	
	
	
	/**
	 * 同步薪资表结构
	 *@param paramMap     salaryItemList:薪资项    midVariableList:薪资类别涉及的临时变量   username:用户名（管理员）  salaryid:薪资帐套id  currym:薪资发放日期   currcount:薪资发放次数   ctrlParamBo:SalaryCtrlParamBo
	 */
	public void syncGzTableStruct(HashMap paramMap) throws GeneralException
	{
		String username=(String)paramMap.get("username");
		String salaryid=(String)paramMap.get("salaryid");
		SalaryCtrlParamBo ctrlParamBo=(SalaryCtrlParamBo)paramMap.get("ctrlParamBo");
		String gz_tablename=username+"_salary_"+salaryid;    //临时表名 
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(gz_tablename, false))
			{
				updateGzDataTable(paramMap);
			}
			else
			{ 
				ArrayList setlist=(ArrayList)paramMap.get("salaryItemList");  //salaryTemplateBo.getSalaryItemList("",""+salaryid,1);  薪资项 
				ArrayList midVariableList=(ArrayList)paramMap.get("midVariableList"); //salaryTemplateBo.getMidVariableListByTable(""+salaryid);  :薪资类别涉及的临时变量
				createGzDataTable(gz_tablename,setlist,midVariableList,ctrlParamBo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	/**
	 * 薪资类别中项目定义和已生成薪资表结构进行分析，保持定义和
	 * 薪资表结构一致,包括两边不同指标，或数据类型发生变化的指标
	 * @param paramMap     salaryItemList:薪资项    midVariableList:薪资类别涉及的临时变量   username:用户名（管理员）  salaryid:薪资帐套id  currym:薪资发放日期   currcount:薪资发放次数
	 * @throws GeneralException
	 */
	private   void updateGzDataTable(HashMap paramMap) throws GeneralException
	{
		try
		{  
			ArrayList _salaryItemList=(ArrayList)paramMap.get("salaryItemList");  //salaryTemplateBo.getSalaryItemList("",""+salaryid,1);  薪资项
			ArrayList salaryItemList=convertBeanToFieldItemList(_salaryItemList); 
			ArrayList midVariableList=(ArrayList)paramMap.get("midVariableList"); //salaryTemplateBo.getMidVariableListByTable(""+salaryid);  :薪资类别涉及的临时变量
			String username=(String)paramMap.get("username");
			String salaryid=(String)paramMap.get("salaryid");
			String currym=(String)paramMap.get("currym");  //薪资发放日期
			String currcount=(String)paramMap.get("currcount");   //薪资发放次数
			
			String gz_tablename=username+"_salary_"+salaryid;    //临时表名 
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);	
			/**升级表结构*/
			upgradeGzTableStruct(gz_tablename);		
			/**升级薪资历史数据表*/
			upgradeGzTableStruct("salaryhistory");
			/**重新加载数据模型*/
			 
			dbmodel.reloadTableModel(gz_tablename);	
			dbmodel.reloadTableModel("salaryhistory");	
			upgradGzHisTableStruct2(salaryItemList);
			 
			 
			dbmodel.reloadTableModel("salaryhistory");
			RecordVo vo=new RecordVo(gz_tablename);
			ArrayList list=vo.getModelAttrs();
			StringBuffer buf=new StringBuffer(); 
			for(int i=0;i<list.size();i++)
			{
				String name=(String)list.get(i);
				buf.append(name.toUpperCase());
				buf.append(",");
			} 
			ArrayList addlist=new ArrayList();
			boolean isAddFlag=true;  //临时表是否有追加字段
			/**如果定义中有，而薪资表中没有，则增加此字段*/
			for(int i=0;i<salaryItemList.size();i++)
			{
				FieldItem item=(FieldItem)salaryItemList.get(i);
				String name=item.getItemid().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(name)==-1)
				{
					addlist.add(item.cloneField());
					if("add_flag".equalsIgnoreCase(name))
						isAddFlag=false;
				} 
			}//for i loop end.
			 
			//临时变量 
			for(int i=0;i<midVariableList.size();i++)
			{
				FieldItem item=(FieldItem)midVariableList.get(i);
				String fieldname=item.getItemid(); 
				/**变量如果未加，则构建*/		
				if(buf.indexOf(fieldname.toUpperCase()+",")==-1)
				{
					Field field=item.cloneField();
					addlist.add(field);
				}//if end.
			}//for i loop end. 
			 
			if(addlist.size()>0)
			{
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(gz_tablename);
				for(int i=0;i<addlist.size();i++)
					table.addField((Field)addlist.get(i));
				dbw.addColumns(table);
			}
			/**两边都有的指标，有可能长度或类型发生的变化*/
			 
			syncGzField(gz_tablename,salaryItemList);
			syncGzField("salaryhistory",salaryItemList);
			dbmodel.reloadTableModel("salaryhistory");	
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(gz_tablename);	
			 
			if(!isAddFlag)
			{
				setDefineAddFlagValue(username,salaryid,currym,currcount);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
 
	
	

	
	
	/**
	 * 创建薪资数据表
	 * @param gz_tablename  表名  
	 * @param setlist 薪资项目
	 * @param midVariableList 薪资帐套涉及的临时变量
	 * @param ctrlParamBo 薪资类别参数
	 * 
	 */
	public void createGzDataTable(String gz_tablename,ArrayList setlist,ArrayList midVariableList,SalaryCtrlParamBo ctrlParamBo) throws GeneralException
	{
		try
		{
			Field field=null;
			StringBuffer buf=new StringBuffer();
			buf.append("A0100,NBASE,A00Z0,A00Z1");
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(gz_tablename); 
			ArrayList fieldList=getTableFieldList(setlist,ctrlParamBo); //生成建表所需项目指标
			for(int i=0;i<fieldList.size();i++)
			{
				field=(Field)fieldList.get(i);
				if(buf.indexOf(field.getName())!=-1)
				{
					field.setNullable(false);
					field.setKeyable(true);
				} 
				table.addField(field);
			}//for i loop end.
			 
			//临时变量 
			for(int i=0;i<midVariableList.size();i++)
			{
				FieldItem item=(FieldItem)midVariableList.get(i);
				String fieldname=item.getItemid();
				field=item.cloneField();
				table.addField(field);
				
			}//for i loop end.
			
			
			dbw.createTable(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);	 
			dbw.execute("create index  "+gz_tablename.toUpperCase()+"_id on "+gz_tablename+" (A0100)");  // 用_A0100 对于5个汉字作用户名的太长
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	/**
	 * 创建信息变动临时表
	 * @param tablename  表名
	 * @param f_compare_field 薪资类别参数为信息变动表设置的指标
	 * @param flag  true: 生成xxxx0字段     false:不生成 xxxx0字段  ，标识子集是否有数据
	 * @throws GeneralException
	 */
	public void createChangeInfoTableStruct(String tablename,ArrayList f_compare_field,boolean flag) throws GeneralException {
		DbWizard dbw=new DbWizard(this.conn);
		Table table=new Table(tablename);
		dbw.dropTable(table);
		
		Field field=new Field("DBNAME","DBNAME");
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		field.setNullable(false);
		field.setKeyable(true);
		table.addField(field);
		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(8);
		field.setNullable(false);
		field.setKeyable(true);	
		table.addField(field);
		field=new Field("A0000","A0000");
		field.setDatatype(DataType.INT);
		table.addField(field);
		field=new Field("B0110","B0110");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);	
		field=new Field("B01101","B01101");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);		
		field=new Field("E0122","E0122");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);	
		field=new Field("E01221","E01221");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);			
		field=new Field("A0101","A0101");
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
		table.addField(field);	
		field=new Field("A01011","A01011");
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
		table.addField(field);			
		field=new Field("STATE","STATE");
		field.setDatatype(DataType.STRING);
		field.setLength(10);			
		table.addField(field);
		/**加入自定义的指标*/
		String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
		if(f_compare_field!=null&&f_compare_field.size()>0)
		{
			for(int i=0;i<f_compare_field.size();i++)
			{
				DynaBean dynabean=(DynaBean)f_compare_field.get(i);  
				String itemid=(String)dynabean.get("itemid");
				String itemdesc=(String)dynabean.get("itemdesc");
				
				 String itemtype=(String)dynabean.get("itemtype");
				 int itemlength=Integer.parseInt((String)dynabean.get("itemlength"));
				 int decwidth=Integer.parseInt((String)dynabean.get("decwidth"));
				//代码类长度设置为50
				 String codesetid=(String)dynabean.get("codesetid");
				 if(StringUtils.isNotBlank(codesetid)&&!"0".equalsIgnoreCase(codesetid)&&itemlength<50)
					 itemlength=50;
				
				cloumnStr+=","+(String)dynabean.get("itemid");
				field=new Field(itemid,itemdesc);
				if("N".equalsIgnoreCase(itemtype))
				{
					if(decwidth>0)
					{
						field.setDatatype(DataType.DOUBLE);
						field.setDecimalDigits(decwidth);
					}
					else
					{
						field.setDatatype(DataType.INT);
					}
				}else if("D".equalsIgnoreCase(itemtype))
				{
					field.setDatatype(DataType.DATE);
				}
				else
					field.setDatatype(DataType.STRING);
				field.setLength(itemlength);			
				table.addField(field);	
				/**现在的*/
				field=new Field(itemid+"1",itemdesc);
				if("N".equalsIgnoreCase(itemtype))
				{
					if(decwidth>0)
					{
						field.setDatatype(DataType.DOUBLE);
						field.setDecimalDigits(decwidth);
					}
					else
					{
						field.setDatatype(DataType.INT);
					}
				}else if("D".equalsIgnoreCase(itemtype))
				{
					field.setDatatype(DataType.DATE);
				}
				else
					field.setDatatype(DataType.STRING);
				field.setLength(itemlength);			
				table.addField(field);		
				//标识子集是否有数据（0：没有）
				if(flag)
				{
					field=new Field(itemid+"0",itemdesc);
					field.setDatatype(DataType.INT);
					table.addField(field);	
				}
				
			}
		}
		boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		if(addflag)
		{
			FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
			table.addField(item.cloneField());
		}
		dbw.createTable(table);
		
	}
	
	
	
	/**
	 * 创建新增或减少人员及停发表结构 
	 * @param list   :   新增与减少人员的显示指标，
	 * @param tablename
	 * @throws GeneralException
	 */
	public void createInsDecTableStruct(String tablename,ArrayList list) throws GeneralException {
		try
		{
					DbWizard dbw=new DbWizard(this.conn);
					Table table=new Table(tablename);
					ContentDAO dao=new ContentDAO(this.conn);
					String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A01Z0,A0101,STATE"; 
					StringBuffer column = new StringBuffer();
					if(list!=null&&list.size()>0){
						for(int i=0;i<list.size();i++){
							LazyDynaBean obj = (LazyDynaBean)list.get(i);
							column.append(","+((String)obj.get("itemid")).toLowerCase()+"");
						}
					} 


					if(dbw.isExistTable(tablename, false))
					{	 
							dbw.dropTable(table);
					}
					
					Field field=new Field("DBNAME","DBNAME");
					field.setDatatype(DataType.STRING);
					field.setLength(3);
					field.setNullable(false);
					field.setKeyable(true);
					table.addField(field);
					field=new Field("A0100","A0100");
					field.setDatatype(DataType.STRING);
					field.setLength(8);
					field.setNullable(false);
					field.setKeyable(true);	
					table.addField(field);
					field=new Field("A0000","A0000");
					field.setDatatype(DataType.INT);
					table.addField(field);
					field=new Field("B0110","B0110");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);			
					field=new Field("E0122","E0122");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);	
					field=new Field("A01Z0","A01Z0");
					field.setDatatype(DataType.STRING);
					field.setLength(2);			
					table.addField(field);		
					field=new Field("A0101","A0101");
					field.setDatatype(DataType.STRING); 
					field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
					table.addField(field);	
					field=new Field("STATE","STATE");
					field.setDatatype(DataType.STRING);
					field.setLength(10);			
					table.addField(field);
					/**如果系统设置唯一性指标，则加入*/
					if(this.isAddColumn(onlyField, cloumnStr))
					{
						FieldItem item = DataDictionary.getFieldItem(onlyField);
						if(item!=null)
						{
							field = item.cloneField();
							table.addField(field);
						}
					}
					
					if(column.length()>0){//新增或减少人员设置的字段加进临时表 
						for(int i=0;i<column.substring(1).split(",").length;i++){
							FieldItem item = DataDictionary.getFieldItem(column.substring(1).split(",")[i]);

							if((item!=null&&cloumnStr.indexOf(item.getItemid())==-1)&&!(this.onlyField.equalsIgnoreCase(item.getItemid())))//过滤重复字段
							{
								field = item.cloneField();
								//代码类长度设置为50
								if(StringUtils.isNotBlank(field.getCodesetid())&&!"0".equalsIgnoreCase(field.getCodesetid()))
									field.setLength(50);
								table.addField(field);
							}
						}
					}
					dbw.createTable(table);
				 
			  

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 把标准表中指标加入薪资发放表中，从档案中取得标准表对应的指标值
	 * @param fieldlist
	 * @return
	 */
	public  void addStdFieldIntoGzTable(String strWhere,String tableName,ArrayList stdTableFieldList,String dbpres)throws GeneralException
	{
		try
		{ 
			List setlist=getSetListByStd(stdTableFieldList);			
			RecordVo vo=new RecordVo(tableName);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tableName);
			String midtable="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";			
			/**
			 * 把标准中涉及到的工资表中没有的指标加入至薪资表结构表中,
			 */
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			boolean bflag=false;
			ArrayList notusedlist=new ArrayList();
			for(int i=0;i<stdTableFieldList.size();i++)
			{
				FieldItem item=(FieldItem)stdTableFieldList.get(i);
				String fieldname=item.getItemid();
				notusedlist.add(item);
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}//if end.
			}//for i loop end.
			if(bflag)
			{
				dbw.addColumns(table);
				dbmodel.reloadTableModel(tableName);						
			} 
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			/**从档案表中导入有关标准表涉及到的数据*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))//多媒体子集
						continue;
					char cc=setid.charAt(0);
					switch(cc)
					{
					case 'A': //人员信息
							String strS=dbpre+setid;		
							if("A01".equalsIgnoreCase(setid)) //主集
							{
								String strupdate=getStdUpdateSQL(notusedlist, strS, setid,tableName);
								if(strupdate.length()>0)
									dbw.updateRecord(tableName,strS,tableName+".A0100="+strS+".A0100", strupdate, "upper("+tableName+".NBASE)='"+dbpre.toUpperCase()+"'", "");
							}
							else//子集
							{
								String strupdate=getStdUpdateSQL(notusedlist, midtable, setid,tableName);
								if(strupdate.length()==0)
									continue;
								String strfields=getStdFieldNameList(notusedlist, setid);
								/**子集当前子录生成临时表*/
								String tempt="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"midtable1";
								if(dbw.isExistTable(tempt, false))
									dbw.dropTable(tempt);
								dbw.createTempTable(strS, tempt,"A0100 as A0000,Max(I9999) as midid", "","A0100");
								if(dbw.isExistTable(midtable, false))
									dbw.dropTable(midtable);
								dbw.createTempTable(strS+" Left join "+tempt+" On "+strS+".A0100="+tempt+".A0000",midtable, "A0100,"+strfields,strS+".I9999="+tempt+".midid","");
								dbw.updateRecord(tableName,midtable,tableName+".A0100="+midtable+".A0100",strupdate, "upper("+tableName+".NBASE)='"+dbpre.toUpperCase()+"'", strWhere);
							}
							break;
					case 'B'://单位信息
							break;
					case 'K'://职位信息
							break;
					}
				}//for j 子集数据处理
			}//for i loop end.
			/**对日期型和数值型区域值进行处理*/
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	/**
	 * 当gz_extend_log对应的发放记录审批状态不为结束状态(06) 默认值为0；否则默认值为1
	 */
	private void setDefineAddFlagValue(String username,String salaryid,String currym,String currcount)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			
			StringBuffer buf=new StringBuffer(); 
			
			String sp_flag="01";
			buf.append("select sp_flag from gz_extend_log where salaryid=?  and upper(username)=? ");
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and A00Z3=? ");
			RowSet rset=dao.search(buf.toString(),Arrays.asList(new Object[]{new Integer(salaryid),username.toUpperCase(),new Integer(currcount)}));
			if(rset.next())
				sp_flag=rset.getString(1);
			if("06".equals(sp_flag))
				dao.update("update "+username+"_salary_"+salaryid+" set add_flag=1");
			else
				dao.update("update "+username+"_salary_"+salaryid+" set add_flag=0");
			rset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 *当指标长度或类型发生的变化同步 工资发放临时表 或 工资历史数据表
	 */
	private void  syncGzField(String tableName,ArrayList salaryItemList)
	{
		RowSet rowSet=null;
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 HashMap map=new HashMap();
			 Field field=null;
			 for(int i=0;i<salaryItemList.size();i++)
			 {
				   FieldItem item= (FieldItem)salaryItemList.get(i);
				    field=item.cloneField();
					String name=field.getName().toLowerCase();
					if("nbase".equals(name)|| "a0100".equals(name)|| "a0000".equals(name)|| "a00z0".equals(name)|| "a00z1".equals(name)
							|| "a00z2".equals(name)|| "a00z3".equals(name)|| "b0110".equals(name)|| "e0122".equals(name)|| "a01z0".equals(name))
						continue;
					FieldItem tempItem=DataDictionary.getFieldItem(name,item.getFieldsetid());
					map.put(name, tempItem);
			 }//for i loop end.
			 rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 
			
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
						String columnName=data.getColumnName(i).toLowerCase();
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
								/*		else if((size-scale)!=tempItem.getItemlength())
											alterList.add(tempItem.cloneField());	*/						//2013-11-23 dengc  结构同步时size对应数值型指标获得的值不准确，造成每次都同步结构、宕机		
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
									/*	else if((size-scale)!=tempItem.getItemlength())
											alterList.add(tempItem.cloneField());	*/ //2013-11-23 dengc  结构同步时size对应数值型指标获得的值不准确，造成每次都同步结构、宕机
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
									/*	else if((size-scale)!=tempItem.getItemlength())
											alterList.add(tempItem.cloneField());	*/  //2013-11-23 dengc  结构同步时size对应数值型指标获得的值不准确，造成每次都同步结构、宕机
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
				DbWizard dbw=new DbWizard(this.conn);
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			    else
			    	syncGzOracleField(data,map,tableName);
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 } 
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	private void syncGzOracleField(ResultSetMetaData data,HashMap map,String tableName)
	{
		try
		{
			 DbWizard dbw=new DbWizard(this.conn);
			 ContentDAO dao=new ContentDAO(this.conn);
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				String columnName=data.getColumnName(i).toLowerCase();
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
									 alertColumn(tableName,tempItem,dbw,dao);
								else if(size<tempItem.getItemlength()&&tempItem.getItemlength()<=10) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
								
							}
							break;
						case java.sql.Types.VARCHAR:
							if("A".equals(tempItem.getItemtype()))
							{
								if(tempItem.getItemlength()>size)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case java.sql.Types.DOUBLE:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							
							
							break;
						case java.sql.Types.NUMERIC:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
								
							}
							break;	
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
	 * 升级薪资表结构，gz_tablename<>SALARYHISTORY :临时用户表  ,gz_tablename=SALARYHISTORY:薪资历史表
	 * @throws GeneralException
	 */
	private void upgradeGzTableStruct(String gz_tablename)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		RowSet rowSet=null;
		try
		{ 
			
			DbWizard dbw=new DbWizard(this.conn);
			 
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();			
			HashMap voMap=new HashMap();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				voMap.put(metaData.getColumnName(i).toUpperCase(),"1");			
			}
			metaData=null;  
			Table table=new Table(gz_tablename);
			Field field=null;
			 
			/**A00Z2发放日期*/
			if(voMap.get("A00Z2")==null)
			{
				field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
				field.setDatatype(DataType.DATE);
				table.addField(field);
				/**发放次数*/
				field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbw.addColumns(table);
				/**正常情况下,发放日期=归属日期,发放次数=归属次数*/
				buf.append("update ");
				buf.append(gz_tablename);
				buf.append(" set A00Z2=A00Z0,A00Z3=A00Z1");
				dao.update(buf.toString());					
				/**A00Z0,A00Z1,把可为空改成不能为空*/
				table.clear();
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(false);
				table.addField(field);
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATE);
				field.setNullable(false);
				table.addField(field);
				dbw.alterColumns(table);
				
				/**修改主键*/
				/**先删除主键索引*/
				dbw.dropPrimaryKey(gz_tablename);
				table.clear();
				field=new Field("NBASE","NBASE");
				field.setKeyable(true);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setKeyable(true);
				table.addField(field);				
				field=new Field("A00Z0","A00Z0");
				field.setKeyable(true);
				table.addField(field);					
				field=new Field("A00Z1","A00Z1");
				field.setKeyable(true);
				table.addField(field);			
				if("salaryhistory".equalsIgnoreCase(gz_tablename)) //薪资历史表
				{
					field=new Field("salaryid","salaryid");
					field.setKeyable(true);
					table.addField(field);	
				}
				dbw.addPrimaryKey(table);
			}//
			
			table.clear();
			/**审批状态字段*/
			if(voMap.get("SP_FLAG")==null)
			{
				
				field=new Field("sp_flag","sp_flag");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field); 
			} 
			if(voMap.get("APPPROCESS")==null)
			{ 
				field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field); 		
			} 
			
			if("salaryhistory".equalsIgnoreCase(gz_tablename)) //薪资历史表
			{
				if(voMap.get("APPUSER")==null)
				{ 
					field=new Field("Appuser","Appuser");
					field.setDatatype(DataType.STRING);
					field.setLength(200);
					table.addField(field); 
				} 
				if(voMap.get("CURR_USER")==null)
				{ 
					field=new Field("Curr_user","Curr_user");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field); 
				}
			} 
			if(voMap.get("USERFLAG")==null) 
			{ 
				field=new Field("userflag","userflag");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field); 	
			} 
			if(voMap.get("E0122_O")==null)
			{ 
				field=new Field("E0122_O","E0122_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field); 			
			}  
			if(voMap.get("B0110_O")==null)
			{ 
				field=new Field("B0110_O","B0110_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field); 			
			} 
			if(voMap.get("DBID")==null) 
			{ 
				field=new Field("dbid","dbid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field); 		
			} 
			if(table.size()>0)
				dbw.addColumns(table);				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		
	}
	
	/**
	 * 升级历史薪资表（添加薪资类别中有的而历史表没有的字段）
	 */
	private void upgradGzHisTableStruct2(ArrayList salaryItemList)throws GeneralException 
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			StringBuffer buf=new StringBuffer(); 
			buf.setLength(0); 
			rowSet=dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				buf.append(metaData.getColumnName(i).toUpperCase());
				buf.append(",");		
			} 
			metaData=null;  
			ArrayList addlist=new ArrayList();
			/**如果定义中有，而薪资历史表中没有，则增加此字段*/
			FieldItem item=null;
			for(int i=0;i<salaryItemList.size();i++)
			{
				item=(FieldItem)salaryItemList.get(i);
				String itemid=item.getItemid().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(itemid)==-1)
				{
					addlist.add(item.cloneField());
				} 
			}//for i loop end.
			 
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table("salaryhistory");
			for(int i=0;i<addlist.size();i++)
				table.addField((Field)addlist.get(i));
			if(addlist.size()>0)
				dbw.addColumns(table); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		
	}
	
	
	
	
	/**
	 * 取得标准表字段列表
	 * @param fieldlist
	 * @param setid
	 * @return for examples a0xxx,a2000
	 */
	private String getStdFieldNameList(ArrayList fieldlist,String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if(fielditem.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(buf.indexOf(fielditem.getItemid())!=-1)
					continue;
				buf.append(fielditem.getItemid());
				buf.append(",");
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	
	
	/**
	 * 求标准表中，更新SQL语句
	 * @param fieldlist
	 * @param strS
	 * @param setid
	 * @return
	 */
	private String getStdUpdateSQL(ArrayList fieldlist,String strS,String setid,String tablename)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer fields=new StringBuffer();		
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);

			/**子集名相同*/
			if(item.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(fields.indexOf(item.getItemid())!=-1)
					continue;				
				String fieldname=item.getItemid();
				buf.append(tablename);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=");
				buf.append(strS);
				buf.append(".");
				buf.append(fieldname);
				if(Sql_switcher.searchDbServer()==2)
					buf.append("`");
				else
					buf.append(",");
				/**过滤掉相同的指标项*/
				fields.append(fieldname);
				fields.append(",");
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}

	
	
	/**
	 * 取得字段列表中包括子集名列表
	 * @param fieldlist
	 * @return
	 */
	private List getSetListByStd(ArrayList fieldlist)
	{
		List setlist=null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			String setid=item.getFieldsetid();
			if(buf.indexOf(setid)==-1)
			{
				buf.append(setid);
				buf.append(",");
			}//if end.
		}//for i loop end.
		if(buf.length()>0)
		{
			String[] setarr=StringUtils.split(buf.toString(),",");
			setlist=Arrays.asList(setarr);
		}
		return setlist;
	}
	
	/**
	 * 把临时变量增加到薪资表中去
	 * @param
	 * @param
	 * @param
	 * @param
	 * @param
	 */
	public void addMidVarIntoGzTable(String tableName,ArrayList midVariableList,ArrayList allMidVarList,HashMap paramMap)throws GeneralException
	{
		ArrayList fieldlist=midVariableList;
		ArrayList midList=allMidVarList;
		String strWhere=(String)paramMap.get("where_str");
		String ym=(String)paramMap.get("ym"); //薪资发放月份
		String count=(String)paramMap.get("count"); //薪资发放次数
		RecordVo templatevo=(RecordVo)paramMap.get("templatevo");
		RowSet rowSet=null;
		try
		{
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);
			RecordVo vo=new RecordVo(tableName);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tableName);
			String tablename="t#"+this.userview.getUserName()+"_gz_mid1";  
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			HashMap existMidFieldMap=new HashMap(); 
			FieldItem item=null;
			for(int i=0;i<fieldlist.size();i++)
			{ 
					item=(FieldItem)fieldlist.get(i); 
					String fieldname=item.getItemid().toLowerCase();
					/**变量如果未加，则构建*/
					if(!vo.hasAttribute(fieldname))
					{ 
							bflag=true;
							table.addField(item);
					}
					else
							existMidFieldMap.put(fieldname,item); 
			} 
			
			if(bflag)
			{
				dbw.addColumns(table);
				dbmodel.reloadTableModel(tableName);					
			} 
			if(existMidFieldMap.size()>0) //同步表结构
			{
				syncGzField2(tableName,existMidFieldMap);
			} 
			/**导入计算后的临时变量的值*/
			String dbpres=templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String stry=ym.substring(0, 4);
			String strm=ym.substring(5, 7);
			String strc=count;
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			/**按人员分库进行批量计算*/
			
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			allUsedFields.addAll(midList);  //临时变量调用临时变量  
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				
				StringBuffer strFilter=new StringBuffer(); 
				ArrayList usedlist=initUsedFields();
				/**追加公式中使用的指标*/
				appendUsedFields(fieldlist,usedlist);
				/**创建计算用临时表*/
			//	String tmptable="t#"+this.userview.getUserName()+"_gz_mid1"; //this.userview.getUserName()+"midtable";
				//int dataNum=0;
				/*
				if(createMidTable(usedlist,tmptable,"A0100"))
				{
					//导入人员主集数据A0100,A0000,B0110,E0122,A0101
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
					buf.append(dbpre+"A01");
					buf.append(" where A0100 in (select A0100 from ");
					buf.append(tableName);
					if(strWhere.length()==0)
					{
						buf.append(" where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
						
						//计算临时变量的导入人员范围条件
						strFilter.append(" (select a0100 from ");
						strFilter.append(tableName);
						strFilter.append(" where upper(nbase)='");
						strFilter.append(dbpre.toUpperCase());
						strFilter.append("')");	
					}
					else
					{
						
						buf.append(strWhere);
						buf.append(" and upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
						
						//计算临时变量的导入人员范围条件
						strFilter.append(" (select a0100 from ");
						strFilter.append(tableName);
						strFilter.append(" ");
						strFilter.append(strWhere);
						strFilter.append(" and upper(nbase)='");
						strFilter.append(dbpre.toUpperCase());
						strFilter.append("')");	
					}
					buf.append(")");
					dataNum=dao.update(buf.toString());
	 			    dbw.execute("create index  "+tablename+"_id on "+tablename+" (A0100,A0000)");  // 用_A0100 对于5个汉字作用户名的太长
				}// 创建临时表结束.
			*/
				buf.setLength(0);
				buf.append("select count(A0100) from ");
				buf.append(tableName);
				if(strWhere.length()==0)
				{
					buf.append(" where upper(nbase)='");
					buf.append(dbpre.toUpperCase());
					buf.append("'");
					
					//计算临时变量的导入人员范围条件
					strFilter.append(" (select a0100 from ");
					strFilter.append(tableName);
					strFilter.append(" where upper(nbase)='");
					strFilter.append(dbpre.toUpperCase());
					strFilter.append("')");	
				}
				else
				{
					
					buf.append(strWhere);
					buf.append(" and upper(nbase)='");
					buf.append(dbpre.toUpperCase());
					buf.append("'");
					//计算临时变量的导入人员范围条件
					if(tableName.indexOf("_gzsp")==-1&&tableName.indexOf("t#")==-1)
					{
					
						strFilter.append(" (select a0100 from ");
						strFilter.append(tableName);
						strFilter.append(" ");
						strFilter.append(strWhere);
						strFilter.append(" and upper(nbase)='");
						strFilter.append(dbpre.toUpperCase());
						strFilter.append("')");	
					}
					else
					{
						strFilter.append(" (select a0100 from ");
						strFilter.append(tableName);
						strFilter.append(" where upper(nbase)='");
						strFilter.append(dbpre.toUpperCase());
						strFilter.append("')");	
					}
				}
				rowSet=dao.search(buf.toString());
				if(rowSet.next())
				{
					if(rowSet.getInt(1)==0)
						continue;
				}
				else
						continue;
				
				for(int j=0;j<fieldlist.size();j++)
				{
					
					item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					String formula= item.getFormula();
					if(formula.indexOf(ResourceFactory.getProperty("gz_new.gz_accounting.getfrom"))!=-1)
					{ 
						continue;
					}
					
					
					YksjParser yp = new YksjParser(this.userview, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
					yp.setStdTmpTable(tableName);
					yp.setTargetFieldDecimal(item.getDecimalwidth());
					
					/**增加一个计算公式用的临时字段*/
					/*
					FieldItem fielditem=new FieldItem("A01","AAAAA");
					fielditem.setItemdesc("AAAAA");
					fielditem.setCodesetid(item.getCodesetid());
					fielditem.setItemtype(fldtype);
					fielditem.setItemlength(item.getItemlength());
					fielditem.setDecimalwidth(item.getDecimalwidth());
					usedlist.add(fielditem);	
					*/				
					
					
					
					String dd = item.getFormula();
				//	yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
					if(tableName.indexOf("_gzsp")==-1&&tableName.indexOf("t#")==-1&&strWhere.trim().length()!=0)
						yp.setRenew_term(strWhere.substring(6));
					yp.run(item.getFormula(),ymc,fldname,tableName,dao,strFilter.toString(),this.conn,fldtype,item.getItemlength(),2,item.getCodesetid());
					/*
					buf.setLength(0);
					if(strWhere.length()==0)
					{
						buf.append("where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
					else
					{
						buf.append(strWhere);
						buf.append(" and upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
			
					//
					String strcond=buf.substring(6);
					
					if(yp.isStatMultipleVar())
					{
						StringBuffer set_str=new StringBuffer("");
						StringBuffer set_st2=new StringBuffer("");
						for(int e=0;e<yp.getStatVarList().size();e++)
						{
							String temp=(String)yp.getStatVarList().get(e);
							set_st2.append(","+temp+"=null");
							set_str.append(tableName+"."+temp+"="+tablename+"."+temp);
							if(Sql_switcher.searchDbServer()==2)
								set_str.append("`");
							else
								set_str.append(",");
						}
						if(set_str.length()>0)
							set_str.setLength(set_str.length()-1);
						else
							continue;
						
						dao.update("update "+tableName+" set "+set_st2.substring(1)+"   "+buf.toString());
						dbw.updateRecord(tableName,tablename,tableName+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
					}
					else
					{
						 
						dbw.updateRecord(tableName,tablename,tableName+".A0100="+tablename+".A0100", tableName+"."+fldname+"="+tablename+"."+fldname, strcond, strcond);
					}
					
					*/
				}//for j loop end.
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
	//		if(dbw.isExistTable(tablename, false))
	//			dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
	//		System.out.println("midtablename="+tablename);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	
	 
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	/**
	 * 追加不同的指标
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist,ArrayList dlist)
	{
		boolean bflag=false;
		for(int i=0;i<slist.size();i++)
		{
			FieldItem fielditem=(FieldItem)slist.get(i);
			String itemid=fielditem.getItemid();
			for(int j=0;j<dlist.size();j++)
			{
				bflag=false;
				FieldItem fielditem0=(FieldItem)dlist.get(j);
				String ditemid=fielditem0.getItemid();
				if(itemid.equalsIgnoreCase(ditemid))
				{
					bflag=true;
					break;
				}

			}//for j loop end.
			if(!bflag)
				dlist.add(fielditem);			
		}//for i loop end.
	}
	
	
	/**
	 * 同步表结构(判断临时变量字段)
	 * @param gz_tablename
	 * @param existMidFieldList
	 */
	private void  syncGzField2(String tableName,HashMap existMidFieldMap)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 DbWizard dbw=new DbWizard(this.conn);
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if(existMidFieldMap.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)existMidFieldMap.get(columnName);
						int columnType=data.getColumnType(i);	
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
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
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								
								
								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
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
				
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    } 
			     table.clear();
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 初始设置使用字段列表
	 * @return
	 */
	private ArrayList initUsedFields()
	{
		ArrayList fieldlist=new ArrayList();
		/**人员排序号*/
		FieldItem fielditem=new FieldItem("A01","A0000");
		fielditem.setItemdesc("a0000");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员编号*/
		fielditem=new FieldItem("A01","A0100");
		fielditem.setItemdesc("a0100");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(8);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**单位名称*/
		fielditem=new FieldItem("A01","B0110");
		fielditem.setItemdesc(ResourceFactory.getProperty("b0110.label"));
		fielditem.setCodesetid("UN");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**姓名*/
		fielditem=new FieldItem("A01","A0101");
		FieldItem item=DataDictionary.getFieldItem("a0101");
		fielditem.setItemdesc(ResourceFactory.getProperty("hire.employActualize.name"));
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(item.getItemlength());
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员排序号*/
		fielditem=new FieldItem("A01","I9999");
		fielditem.setItemdesc("I9999");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**部门名称*/
		fielditem=new FieldItem("A01","E0122");
		fielditem.setItemdesc(ResourceFactory.getProperty("label.title.dept"));
		fielditem.setCodesetid("UM");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);		
		return fieldlist;
	}
	
	
	/**
	 * 同步表结构某列（ORACLE）
	 * @param tableName  表名
	 * @param _item   列指标
	 * @param dbw     
	 * @param dao
	 */
	private  void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
	{ 
		RowSet rowSet=null;
		try
		{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
			 String item_id=item.getItemid();
			 item.setItemid(item_id+"_x"); 
			 rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap columnMap=new HashMap(); 
			 for(int i=1;i<=data.getColumnCount();i++)
			 {	 columnMap.put(data.getColumnName(i).toLowerCase().trim(),"1"); 
			 } 
			 if(columnMap.get(item_id.toLowerCase().trim()+"_x")==null)  
			 {
		    	 table.addField(item.cloneField());
		    	 dbw.addColumns(table);
			 }
			 
			 if("N".equalsIgnoreCase(item.getItemtype()))
			 {
				 int dicimal=item.getDecimalwidth();
				 dao.update("update "+tableName+" set "+item_id+"_x=ROUND("+item_id+","+dicimal+")");
			 }
			 if("A".equalsIgnoreCase(item.getItemtype()))
			 {
				 int length=item.getItemlength();
				 dao.update("update "+tableName+" set "+item_id+"_x=substr(to_char("+item_id+"),0,"+length+")");
			 }
			 table.clear();
			 
			 item.setItemid(item_id);
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 dbw.addColumns(table);
			 
			 dao.update("update "+tableName+" set "+item_id+"="+item_id+"_x");
			 table.clear();
			 item.setItemid(item_id+"_x");
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 item.setItemid(item_id);
			 if(rowSet!=null)
				 rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	  /**
     * 判断是否用加唯一性指标字段
     * @param field
     * @param fields
     * @return
     */
    public  boolean isAddColumn(String field,String fields)
    {
    	boolean flag=true;
    	if(field==null|| "".equals(field))
    		flag=false;
    	else
    	{
    		if((","+fields+",").toUpperCase().indexOf(","+field.toUpperCase()+",")!=-1)
    			flag=false;
    	}
    	return flag;
    }
	
	
	
	/**
	 * 生成建表所需项目指标
	 * @param setlist  薪资项目
	 * @param ctrlParam  薪资类别参数
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getTableFieldList(ArrayList setlist,SalaryCtrlParamBo ctrlParam) throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		String format=("###################");		
		String manager=ctrlParam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		Field field=null;
		boolean isOk=false;
		/**加上审批标识*/
		field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
		field.setLength(50);
		field.setCodesetid("23");
		field.setDatatype(DataType.STRING);
		field.setReadonly(true);
		fieldList.add(field); 
		//	加上报审标识
		if(manager!=null&&manager.trim().length()>0)
		{
			field=new Field("sp_flag2",ResourceFactory.getProperty("gz_new.gz_accounting.aprovalstate"));
			field.setLength(50);
			field.setCodesetid("23");
			field.setDatatype(DataType.STRING);
			field.setReadonly(true);
			fieldList.add(field); 
			isOk=true;
		} 
		String flow_flag=ctrlParam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
		if("1".equalsIgnoreCase(flow_flag))  //薪资类别走审批流程
		{ 
			isOk=true;
		}
		if(isOk)
		{
			/**加上审批意见*/
			field=new Field("appprocess",ResourceFactory.getProperty("train.job.student.idea"));
			field.setDatatype(DataType.CLOB);
			field.setAlign("left");		
			field.setReadonly(true);
			fieldList.add(field);
		} 
		//追加标记
		field=new Field("add_flag",ResourceFactory.getProperty("gz_new.gz_accounting.addrecordflag"));
		field.setDatatype(DataType.INT);
		field.setAlign("left");
		field.setVisible(false);
		fieldList.add(field);
		
		String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
		LazyDynaBean abean=null;
		for(int i=0;i<setlist.size();i++)
		{
			abean=(LazyDynaBean)setlist.get(i);
			String itemid=(String)abean.get("itemid");
			String itemdesc=(String)abean.get("itemdesc");
			int itemlength =Integer.parseInt((String)abean.get("itemlength"));
			String _decwidth=(String)abean.get("decwidth");
			int decwidth=_decwidth==null?0:Integer.parseInt(_decwidth);
			if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
			{
				FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
				if(_tempItem==null)
					continue;
				
			}  
			
			field=new Field(itemid,itemdesc); 
			String type=(String)abean.get("itemtype");
			String codesetid=(String)abean.get("codesetid");
			field.setCodesetid(codesetid);
			/**字段为代码型,长度定为50*/
			if("A".equals(type))
			{
				field.setDatatype(DataType.STRING); 
				if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
					field.setLength(itemlength);						
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

				field.setLength(itemlength); 
				field.setDecimalDigits(decwidth);					
				if(decwidth>0)
				{
					field.setDatatype(DataType.FLOAT);		 
					field.setFormat("####."+format.toString().substring(0,decwidth));
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
				field.setDatatype(DataType.DATE);
				field.setFormat("yyyy.MM.dd");
				field.setAlign("right");						
			}	
			else
			{
				field.setDatatype(DataType.STRING);
				field.setLength(itemlength);
				field.setAlign("left");						
			}
			/**对人员库标识，采用“@@”作为相关代码类*/
			if("nbase".equalsIgnoreCase(itemid))
			{
				field.setCodesetid("@@");
				field.setReadonly(true);
			}   
			fieldList.add(field);
		}//loop end. 
		
		
		field=new Field("userflag","userflag");
		field.setDatatype(DataType.STRING);
		field.setLength(50);
		fieldList.add(field); 
		field=new Field("E0122_O","E0122_O");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		fieldList.add(field);
					
		field=new Field("B0110_O","B0110_O");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		fieldList.add(field);
		
		field=new Field("dbid","dbid");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		fieldList.add(field);
		
		return fieldList;
	}
	
	
	/**
	 * 转换方法，将List<LazyDynaBean> to List<FieldItem>
	 * @param beanList
	 * @return
	 */
	private ArrayList convertBeanToFieldItemList(ArrayList beanList)
	{
			ArrayList itemList=new ArrayList();
			LazyDynaBean abean=null;
			FieldItem item=null;
			for(Iterator<LazyDynaBean> t=itemList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
			    item=new FieldItem();
				item.setFieldsetid((String)abean.get("fieldsetid"));
				item.setItemid((String)abean.get("itemid"));
				item.setItemdesc((String)abean.get("itemdesc"));
				item.setItemtype((String)abean.get("itemtype"));
				item.setItemlength(Integer.parseInt((String)abean.get("itemlength")));
				item.setDisplaywidth(Integer.parseInt((String)abean.get("nwidth")));
				item.setDecimalwidth(Integer.parseInt((String)abean.get("decwidth")));
				item.setCodesetid((String)abean.get("codesetid"));
				item.setFormula((String)abean.get("formula"));
				item.setVarible(0);
				itemList.add(item);
			} 
			return itemList;
	}
	/**
	 * 创建审核临时表
	 * @param tableName
	 * @param a00z0
	 * @param a00z1
	 * @param view
	 * @param salaryid
	 * @param reportSQL
	 */
	public void createShTempTable(String tableName,String a00z2,String a00z3,UserView view,int salaryid,String reportSQL)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" salaryid="+salaryid);
			sql.append(" and ((lower(curr_user)='"+view.getUserId().toLowerCase()+"' and (sp_flag='02' or sp_flag='07')) or ( ( AppUser Like '%;"+view.getUserName()+";%' ) and  (sp_flag='06' or  sp_flag='03') ))");
 			if(a00z3!=null&&!"".equals(a00z3))
    		{
    			sql.append(" and A00Z3=");
    			sql.append(a00z3);	
    		}
     		if(a00z2!=null&&!"".equals(a00z2))
    		{
     			a00z2=a00z2.replaceAll("\\.","-");
     			String[] temp=a00z2.split("-");
	    		sql.append(" and "+Sql_switcher.year("a00z2")+"="+temp[0]+" and ");
     			sql.append(Sql_switcher.month("a00z2")+"="+temp[1]);	
	    	}
     		if(reportSQL!=null&&!"".equals(reportSQL))
     		{
     			sql.append(reportSQL);
     		}
     		copyDataToSpTempTable(sql.toString(),tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 薪资审批，先从历史表把相应数据复制到临时表
	 * @param strWhere
	 * @param tableName
	 */
	public void copyDataToSpTempTable(String strWhere,String tableName)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tableName, false))
				dbw.dropTable(tableName);
			String a_sql="";
			strWhere=PubFunc.keyWord_reback(strWhere);
			if(Sql_switcher.searchDbServer()==2)
				a_sql="create table "+tableName+" as select * from salaryhistory where "+strWhere;
			else 
				a_sql="select *  into "+tableName+"  from salaryhistory where "+strWhere;
			dao.update(a_sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getOnlyField() {
		return onlyField;
	} 

	public void setOnlyField(String onlyField) {
		this.onlyField = onlyField;
	} 


}
