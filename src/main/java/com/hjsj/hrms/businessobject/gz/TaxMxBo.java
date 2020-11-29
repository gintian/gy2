/**
 * 
 */
package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.org.autostatic.confset.UpdateTable;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.TimeScope;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:TaxMxBo</p> 
 *<p>Description:管理个税明细表结构类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-7-20:上午11:25:26</p> 
 *@author cmq
 *@version 4.0
 */
public class TaxMxBo {
	private Connection conn=null;
	/**个税明细表所有字段列表以，存放Field类型对象*/
	private ArrayList fieldlist=new ArrayList();
	private UserView view;
	public TaxMxBo(Connection conn,UserView view)
	{
		this.conn=conn;
		this.view=view;
	}
	/**
	 * 从工资发放进入税率表时，将税率表中存在但是在工资表中不存在的数据
	 * @param salaryid
	 */
	public void syncTaxData(String salaryid)
	{
		try
		{
			SalaryCtrlParamBo scpb = new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
			String manager=scpb.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String gz_tablename="";
			String userflag="";
			if(manager.length()==0)
			{
				gz_tablename=this.view.getUserName()+"_salary_"+salaryid;
				userflag=this.view.getUserName();
			}
			else
			{
				gz_tablename=manager+"_salary_"+salaryid;
				userflag=manager;
			}
			ContentDAO dao = new ContentDAO(this.conn);
			String a00z2="";
			int a00z3=0;
			StringBuffer tempsql=new StringBuffer("");
			tempsql.append("select a00z2,a00z3 from "+gz_tablename);
			RowSet rs = dao.search(tempsql.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next())
			{
				Date d = rs.getDate("a00z2");
				a00z2=format.format(d);
				a00z3=rs.getInt("a00z3");
				break;
			}
			if("".equals(a00z2)||a00z3==0)
				return;
			StringBuffer del_sql = new StringBuffer("");
			del_sql.append(" delete from gz_tax_mx where not exists (select null from ");
			del_sql.append(gz_tablename+" where gz_tax_mx.a00z1="+gz_tablename+".a00z1 and gz_tax_mx.a00z0="+gz_tablename+".a00z0 ");
			del_sql.append(" and gz_tax_mx.a0100="+gz_tablename+".a0100 ");
			del_sql.append(" and UPPER(gz_tax_mx.nbase)=UPPER("+gz_tablename+".nbase)) ");
			del_sql.append(" and salaryid="+salaryid);
			del_sql.append(" and UPPER(userflag)='"+userflag.toUpperCase()+"'");
			del_sql.append(" and a00z3="+a00z3);
			del_sql.append(" and "+Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd")+"='"+a00z2+"'");
			dao.delete(del_sql.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void initdata()
	{
		/**个税明细表固定字段列表*/
		ArrayList commlist=searchCommonItemList();
		fieldlist.addAll(commlist);
		ArrayList chglist=searchDynaItemList();
		fieldlist.addAll(chglist);
	}
	/**
	 * 查找对应的指标
	 * @param itemid
	 * @return
	 */
	private Field searchFieldById(String itemid)
	{
		Field field=null;
		boolean flag=false;
		for(int i=0;i<this.fieldlist.size();i++)
		{
			field=(Field)fieldlist.get(i);

			if(itemid.equalsIgnoreCase(field.getName()))
			{
				flag=true;
				break;
			}
		}
		if(!flag)
			field=null;
		return field;
	}
	/**
	 * 按指标排序输出
	 * @return
	 */
	private ArrayList sortFieldList(ArrayList fieldlist)
	{
		ArrayList sortlist=new ArrayList();
		boolean isynssde=false;
		boolean isljsde=false;
		boolean isljse=false;
		boolean islj_basedata=false;
		boolean is_allowance=false;
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				Document doc=null;
				doc=PubFunc.generateDom(ctrlvo.getString("str_value"));
				//System.out.println(ctrlvo.getString("str_value"));
				String str_path="/param/fields/field";
				XPath xpath=XPath.newInstance(str_path);	
				List childlist=xpath.selectNodes(doc);
				Iterator i = childlist.iterator();
				Element element=null;			
				while(i.hasNext())
				{
					element=(Element)i.next();				
					String id=element.getAttributeValue("id");
					String title=element.getAttributeValue("title");
					String width=element.getAttributeValue("width");
					if("b0110".equalsIgnoreCase(id)){
						title="单位名称";
					}
					if("ynse".equalsIgnoreCase(id)){//汉口银行    应用所得额强行改成收入额   zhaoxg add 2013-11-11  光棍节~~
						title="收入额";
					}
					String visible=element.getAttributeValue("visible");
					Field field=searchFieldById(id);
					if(field!=null)
					{
						if("ynssde".equalsIgnoreCase(field.getName()))
							isynssde=true;
						if("ljsde".equalsIgnoreCase(field.getName()))
							isljsde=true;
						if("ljse".equalsIgnoreCase(field.getName()))
							isljse=true;
						if("lj_basedata".equalsIgnoreCase(field.getName()))
							islj_basedata=true;
						if("allowance".equalsIgnoreCase(field.getName()))
							is_allowance=true;
						field.setLabel(title);
						if("true".equalsIgnoreCase(visible))
							field.setVisible(true);
						else
							field.setVisible(false);
						sortlist.add(field);
					}
				}//while loop end.
			}//if ctrlvo end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		if(sortlist.size()==0)
		{
			StringBuffer format=new StringBuffer();	
			format.append("############");	
			sortlist.addAll(fieldlist);
			if(!isynssde)
			{
				Field field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
		}
		else
		{
			StringBuffer format=new StringBuffer();	
			format.append("############");	
			
			if(!isynssde)
			{
				Field field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
			if(!isljsde)
			{
				Field field=new Field("ljsde","累计应纳税所得额");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
			if(!isljse)
			{
				Field field=new Field("ljse","累计预扣税额");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
			if(!islj_basedata)
			{ 
				Field field=new Field("lj_basedata","累计基本减除费用");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
			
			if(!is_allowance)
			{
				Field field=new Field("allowance","减免费用");
				field.setDatatype(DataType.FLOAT);						
				format.setLength(2);
				field.setFormat("####."+format.toString());		
				field.setVisible(true);			
				field.setDecimalDigits(2);
				field.setLength(12);
				sortlist.add(field);
			}
			Field field=new Field("Tax_max_id","tax_id");
			field.setDatatype(DataType.INT);
			field.setLength(12);
			field.setFormat("####");
			field.setVisible(false);
			sortlist.add(field);
		}
		if("true".equalsIgnoreCase(this.getDeptID()))
		{
			Field field=new Field("deptid",ResourceFactory.getProperty("gz.columns.lse0122"));
	    	field.setDatatype(DataType.STRING);
	    	field.setLength(30);
	    	field.setCodesetid("UM");
	    	field.setReadonly(true);		
	    	field.setVisible(true);
	    	sortlist.add(field);
		}
		return sortlist;
	}
	
	


	/**
	 * 同步个税归档表
	 * @author ZhangHua
	 * @date 10:45 2018/6/8
	 */
	public void syncSalaryTaxArchiveStrut() {
		try
		{
			DbWizard dbw = new DbWizard(this.conn);
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap amap = new HashMap();
			RowSet rowSet = dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData data = rowSet.getMetaData();
			ArrayList addList = new ArrayList();
			for (int i = 1; i <= data.getColumnCount(); i++) {
				String columnName = data.getColumnName(i).toLowerCase();
				amap.put(columnName, "1");
			}
			if(amap.get("ljsde")==null)
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("ynssde","应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljsde","累计应纳税所得额");
				field.setDatatype(DataType.FLOAT);	 
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("ljse","累计预扣税额");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field); 
				dbw.addColumns(table); 
			}//
			
			if(amap.get("znjy")==null) //20181221
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("lj_basedata","累计基本减除费用");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("znjy","子女教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("sylr","赡养老人");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				
				field=new Field("zfdklx","住房贷款利息");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("zfzj","住房租金");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				
				field=new Field("jxjy","继续教育");
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field); 
				
				dbw.addColumns(table); 
				
			}//
			 
			if(amap.get("allowance")==null) //20181221
			{
				Table table=new Table("gz_tax_mx");
				Field field=new Field("allowance","税收减免"); //残疾人个税减免
				field.setDatatype(DataType.FLOAT);	
				field.setLength(12);
				field.setDecimalDigits(4);
				table.addField(field);
				dbw.addColumns(table); 
			}
			
			
			
			if(dbw.isExistTable("taxarchive", false))
			{
				rowSet = dao.search("select * from taxarchive where 1=2");
				data = rowSet.getMetaData();
				HashMap existMap = new HashMap(); 
				for (int i = 1; i <= data.getColumnCount(); i++) {
					String columnName = data.getColumnName(i).toLowerCase(); 
					existMap.put(columnName, "1");
				} 
				if(existMap.get("ljsde")==null)
				{
					Table tbl = new Table("taxarchive");
					Field field=new Field("ynssde","应纳税所得额");
					field.setDatatype(DataType.FLOAT);	 
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("ljsde","累计应纳税所得额");
					field.setDatatype(DataType.FLOAT);	 
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("ljse","累计预扣税额");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field); 
					dbw.addColumns(tbl); 
				}//
				
				if(existMap.get("znjy")==null) //20181221
				{
					Table tbl = new Table("taxarchive");
					Field field=new Field("lj_basedata","累计基本减除费用");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("znjy","子女教育");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("sylr","赡养老人");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					
					field=new Field("zfdklx","住房贷款利息");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("zfzj","住房租金");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					
					field=new Field("jxjy","继续教育");
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field); 
					
					dbw.addColumns(tbl); 
					
				}//
				
				
				if(existMap.get("allowance")==null) //20181221
				{
					Table table=new Table("taxarchive");
					Field field=new Field("allowance","税收减免"); //残疾人个税减免
					field.setDatatype(DataType.FLOAT);	
					field.setLength(12);
					field.setDecimalDigits(4);
					table.addField(field);
					dbw.addColumns(table); 
				}
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	/**
	 * @return 返回个税明细表动态维护的指标
	 */
	public ArrayList searchDynaItemList() {
		ArrayList chglist=new ArrayList();
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
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
					SalaryPkgBo pkgbo=new SalaryPkgBo(this.conn,null,0);
					for(int i=0;i<arr.length;i++)
					{
						Field field=pkgbo.searchItemById(arr[i]);
						if(field!=null)
						{
							if(field.getDataType()==DataType.FLOAT)
							{
								int num = field.getDecimalDigits();
								field.setFormat("####."+this.setFormat(num));
							}else if(field.getDataType()==DataType.INT)
							{
								field.setFormat("####");
							}
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
	
	/**
	 * 取得个税明细表的固定字段列表
	 * @return
	 */
	public ArrayList searchCommonItemList() {
		ArrayList templist=new ArrayList();
		
		syncSalaryTaxArchiveStrut();
		
		StringBuffer format=new StringBuffer();	
		format.append("############");	
		
		Field field=new Field("Tax_max_id","tax_id");
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(false);
		templist.add(field);
		
		field=new Field("nbase",ResourceFactory.getProperty("gz.columns.nbase"));
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		field.setCodesetid("@@");
		field.setVisible(true);
		field.setReadonly(true);
		templist.add(field);

		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(10);
		field.setVisible(false);
		templist.add(field);			
		
		field=new Field("A00Z0",ResourceFactory.getProperty("gz.columns.a00z0"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);	
		templist.add(field);

		
		field=new Field("A00Z1",ResourceFactory.getProperty("gz.columns.a00z1"));
		field.setDatatype(DataType.INT);
		field.setLength(12);
		field.setFormat("####");
		field.setVisible(true);
		templist.add(field);			
		
		field=new Field("B0110",ResourceFactory.getProperty("gz.columns.b0110"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UN");
		field.setReadonly(true);		
		field.setVisible(true);
		templist.add(field);			

		field=new Field("E0122",ResourceFactory.getProperty("gz.columns.e0122"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setCodesetid("UM");
		field.setReadonly(true);		
		field.setVisible(true);
		templist.add(field);			

		field=new Field("A0101",ResourceFactory.getProperty("gz.columns.a0101"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);		
		field.setVisible(true);
		templist.add(field);			
		
		field=new Field("Tax_date",ResourceFactory.getProperty("gz.columns.taxdate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);			
		templist.add(field);

		field=new Field("Declare_tax",ResourceFactory.getProperty("gz.columns.declaredate"));
		field.setDatatype(DataType.DATE);
		field.setFormat("yyyy.MM.dd");
		field.setAlign("right");
		field.setVisible(true);			
		templist.add(field);			
		
		field=new Field("TaxMode",ResourceFactory.getProperty("gz.columns.taxmode"));
		field.setLength(2);			
		field.setDatatype(DataType.STRING);
		field.setCodesetid("46");
		field.setReadonly(false);
		field.setVisible(true);			
		templist.add(field);

		field=new Field("ynse",ResourceFactory.getProperty("gz.columns.ynse"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		


		
		field=new Field("ynssde","应纳税所得额");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		field=new Field("ljsde","累计应纳税所得额");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		

		field=new Field("Sl",ResourceFactory.getProperty("gz.columns.sl"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		field=new Field("basedata",ResourceFactory.getProperty("gz.columns.basedata"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);

		field=new Field("Sds",ResourceFactory.getProperty("gz.columns.sds"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);		
		field.setLength(12);
		templist.add(field);	
		


		field=new Field("allowance","减免费用");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);		
		field.setLength(12);
		templist.add(field);	
		
		field=new Field("ljse","累计预扣税额 ");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		
		field=new Field("Description",ResourceFactory.getProperty("gz.columns.desc"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setVisible(true);
		templist.add(field);


		field=new Field("lj_basedata","累计基本减除费用");
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		field.setLength(12);
		templist.add(field);
		
		
		field=new Field("Flag",ResourceFactory.getProperty("gz.columns.flag"));
		field.setDatatype(DataType.STRING);
		field.setLength(12);
		field.setVisible(true);
		field.setCodesetid("34");
		templist.add(field);
		return templist;
	}
	
	public TaxMxBo(Connection conn) {
		this.conn=conn;
	}
	/**
	 * 取得个税明细表中所有不同的报税时间列表
	 * @param flag=0(不加全部),flag=1加上“全部”选项
	 * @return
	 */
	public ArrayList searchDeclareDateList(int flag,String tablename)
	{
		ArrayList datelist=new ArrayList();
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
//			buf.append("select distinct ");
//			buf.append(Sql_switcher.year("Declare_tax"));
//			buf.append(" as theyear,");
//			buf.append(Sql_switcher.month("Declare_tax"));
//			buf.append(" as themonth ");
//			buf.append(" from gz_tax_mx order by Declare_tax");
			buf.append("select distinct Declare_tax  from "+tablename+" order by Declare_tax desc");
			RowSet rset=dao.search(buf.toString());
//			if(flag==1)
//			{
//				CommonData data=new CommonData("all",ResourceFactory.getProperty("label.all"));	
//				datelist.add(data);
//			}
			HashMap map=new HashMap();
			while(rset.next())
			{
				if(rset.getDate("Declare_tax")==null)
					continue;
				String temp=PubFunc.FormatDate(rset.getDate("Declare_tax"), "yyyy.MM");

				if(!map.containsKey(temp))
				{
					CommonData data=new CommonData(temp,temp);	
					datelist.add(data);
					map.put(temp,temp);
				}
			}//while loop end.
			if(flag==1)//全部放到下拉框的最下面了，解决第一次进入默认选择的是最近一个月，解决全部数据过多的问题  zhaoxg 2013-4-15
			{
				CommonData data=new CommonData("all",ResourceFactory.getProperty("label.all"));	
				datelist.add(data);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return datelist;
	}
	/**
	 * 取得个税明细表结构中所有指标列表
	 * @return
	 */
	public ArrayList getFieldlist() {
		initdata();
		return sortFieldList(fieldlist);
	}
	/**
	 * 生成个税明细表
	 * @return,返回的为文件名,包括目录结构
	 */
	public String exportMxExcel(String fromtable,String strwhere,String exporttype,UserView view,String filterByMdule)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String outname="ExportTaxMx_"+PubFunc.getStrg()+".xls";
		String filename=outname;
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT); 
		
		    ArrayList itemlist = (ArrayList)this.getFieldlist();
			StringBuffer selectsb = new StringBuffer();
			if("1".equalsIgnoreCase(exporttype))
			{
    			selectsb.append(" max(A0000) as a0000 ");
			}
			else{
				selectsb.append(" a0000 ");
			}
			StringBuffer groupbysb = new StringBuffer();
			String itemstr = "";
			//  形成SQL语句
			String str="";
			int decimal=0;
			boolean taxmodeflag=true;
			boolean nbaseflag=true;
			boolean tax_dateflag=true;
			for(int i=0;i<itemlist.size();i++){
				Field fi = (Field)itemlist.get(i);
				String itemid =  (String)fi.getName();
				if("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid)||!fi.isVisible())
				{
					continue;
				}	
				if("taxmode".equalsIgnoreCase(itemid))
					taxmodeflag=false;
				if("NBASE".equalsIgnoreCase(itemid))
					nbaseflag=false;
				if("tax_date".equalsIgnoreCase(itemid))
					tax_dateflag=false;
				/*if(exporttype.equalsIgnoreCase("1"))
				{
					if(itemid.equalsIgnoreCase("a00z1")||itemid.equalsIgnoreCase("a00z0"))
						continue;
				}*/
				String itemdesc = (String)fi.getLabel();
				if(str.toUpperCase().indexOf((","+itemid.toUpperCase()+","))!=-1)
				{
					continue;
				}
				str+=","+itemid+",";
				int datatype = fi.getDatatype();
				if("1".equalsIgnoreCase(exporttype))//合并
				{
					if(!"deptid".equalsIgnoreCase(itemid))
					    itemid= getitemstr(itemid,datatype);
					else
		    			itemid="max("+itemid+") as "+itemid;
				}
				selectsb.append(","+itemid);
			}
			/* dengcan 2014-10-10 start */
			if("1".equalsIgnoreCase(exporttype))//合并
			{ 
				if(taxmodeflag)
					selectsb.append(",max(taxmode) as taxmode");
				if(nbaseflag)
					selectsb.append(",max(nbase) as nbase");
				if(tax_dateflag)
					selectsb.append(",max(tax_date) as tax_date");
			}
			else
			{
				if(taxmodeflag)
					selectsb.append(",taxmode");
				if(nbaseflag)
					selectsb.append(",nbase");
				if(tax_dateflag)
					selectsb.append(",tax_date");
			}
			/* dengcan 2014-10-10 end */
			String sql = this.getoutpartsql(fromtable,selectsb.substring(1),strwhere,exporttype,filterByMdule);
			RowSet rset=dao.search(sql);
			int n=1;
			/**由于excel每sheet里最多65535条记录，默认每页显示65535条，多余则从新建一个sheet*/

			String macth="(-)?+[0-9]+(.[0-9]+)?";
			int sheetPerRows=1;
			int sheetPage=1;
			HSSFSheet sheet = workbook.createSheet(sheetPage+"");
			while(rset.next()){
				//System.out.println("sheetPerRows="+sheetPerRows);
				//System.out.println("n="+n);
				if(sheetPerRows>=((25000*sheetPage)))
				{
					sheetPage++;
					sheet = workbook.createSheet(sheetPage+"");
					/**建表头*/
					int t=0;
					String strr="";
					for(int i=0;i<itemlist.size();i++){
						Field fi = (Field)itemlist.get(i);
						String itemid =  (String)fi.getName();
						if("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid)||!fi.isVisible())
						{
							continue;
						}	
						if(str.toUpperCase().indexOf((","+itemid.toUpperCase()+","))!=-1)
						{
							continue;
						}
						strr+=","+itemid+",";
						String itemdesc = (String)fi.getLabel();
						int datatype = fi.getDatatype();
						row = sheet.getRow((short)0);
						if(row==null)
							row = sheet.createRow((short)0);
						csCell =row.createCell((short)(t));
											
						csCell.setCellValue(itemdesc);
						csCell.setCellStyle(style);
						t++;
					}
					if("1".equals(exporttype)&& "1".equals(filterByMdule))
					{
						csCell =row.createCell((short)(t));
						
						csCell.setCellValue("已纳税额");
						csCell.setCellStyle(style);
						t++;
					}
					n=1;
				}
				if(sheetPerRows==1)
				{
					/**建表头*/
					int t=0;
					for(int i=0;i<itemlist.size();i++){
						Field fi = (Field)itemlist.get(i);
						String itemid =  (String)fi.getName();
						if("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid)||!fi.isVisible())
						{
							continue;
						}		
						String itemdesc = (String)fi.getLabel();
						int datatype = fi.getDatatype();
						row = sheet.getRow((short)0);
						if(row==null)
							row = sheet.createRow((short)0);
						csCell =row.createCell((short)(t));
											
						csCell.setCellValue(itemdesc);
						csCell.setCellStyle(style);
						t++;
					}
					if("1".equals(exporttype)&& "1".equals(filterByMdule))
					{
						csCell =row.createCell((short)(t));
						
						csCell.setCellValue("已纳税额");
						csCell.setCellStyle(style);
						t++;
					}
					
				}
				int m = 0;
				row = sheet.createRow((short)n);
				for(int i=0;i<itemlist.size();i++){
					Field fielditem = (Field)itemlist.get(i);
					if("Tax_max_id".equalsIgnoreCase(fielditem.getName())|| "A0100".equalsIgnoreCase(fielditem.getName())||!fielditem.isVisible())
					{
						continue;
					}	
					/*if(exporttype.equalsIgnoreCase("1")&&(fielditem.getName().equalsIgnoreCase("A00z1")||fielditem.getName().equalsIgnoreCase("A00z0")))
						continue;*/
						
					if("sds".equalsIgnoreCase(fielditem.getName()))//所得税  用它的小数位来绝对已纳税额的小数位   zhaoxg add 2014-7-24
						decimal=fielditem.getDecimalDigits();
					ResultSetMetaData rsetmd=rset.getMetaData();
					String fieldesc = getColumStr(rset,rsetmd,fielditem.getName());
					fieldesc = fieldesc!=null?fieldesc:"";
					String desc = "";
					int type = fielditem.getDatatype();			
					if(fielditem.isCode()|| "flag".equalsIgnoreCase(fielditem.getName())){
						/*desc = AdminCode.getCodeName("UN", fieldesc);
						if(desc.length()<1){
							desc = AdminCode.getCodeName("UM", fieldesc);
							if(desc.length()<1){
								desc = AdminCode.getCodeName("@K", fieldesc);
								if(desc.length()<1){*/
									desc = AdminCode.getCodeName(fielditem.getCodesetid(),rset.getString(fielditem.getName()));
									if("UM".equalsIgnoreCase(fielditem.getCodesetid())&&(desc==null||desc.trim().length()==0))
										desc = AdminCode.getCodeName("UN",rset.getString(fielditem.getName()));
						/*		}
							}
						}*/
					}else{
									
						if(type==DataType.FLOAT||type==DataType.DOUBLE||type==DataType.LONG){
							if(fieldesc!=null&&fieldesc.trim().length()>0){
								desc = PubFunc.round(fieldesc,fielditem.getDecimalDigits());
							}else{
								desc = "";
							}
						}else if(type==4){						
							if(fieldesc!=null&&fieldesc.trim().length()>0){
								//desc = Math.round(Float.parseFloat(fieldesc))+"";
								desc=PubFunc.round(fieldesc, fielditem.getDecimalDigits());
								if("0".equals(desc)){
									desc = "";
								}
							}else{
								desc = "";
							}
						}else{
							desc = fieldesc;
						}
					}
					csCell =row.createCell((short)(m));
					
					if(type==DataType.FLOAT||type==DataType.DOUBLE||type==DataType.INT||type==DataType.LONG)
					{
			    		if(desc!=null&&!"".equals(desc))
				    	{
					    	csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				    		csCell.setCellValue(Double.parseDouble(desc));
				    	}
				    	else
				        	csCell.setCellValue(desc);
					}
					else
						csCell.setCellValue(desc);
					csCell.setCellStyle(style);
					m++;
				}
				if("1".equals(exporttype)&& "1".equals(filterByMdule))
				{
					csCell =row.createCell((short)(m));
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		    		csCell.setCellValue(Double.parseDouble(PubFunc.round(String.valueOf(rset.getDouble("mynse")),decimal)));
					csCell.setCellStyle(style);
					m++;
				}
				n++;
				sheetPerRows++;
			}
		
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
		}catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return filename;
	}
	/**
	 * 生成个税汇总表
	 * @return ,返回的为文件名,包括目录结构
	 */
	public String exportDefaultExcel(String fromtable,String strwhere,String declaredate,UserView view,String filterByMdule)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String outname="ExportTaxCount_"+PubFunc.getStrg()+".xls";
		String filename=outname;
		Calendar cal=Calendar.getInstance(); 
//		int y,m,d; 
//		y=cal.get(Calendar.YEAR); 
//		m=cal.get(Calendar.MONTH); 
//		d=cal.get(Calendar.DATE); 
//		java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("yyyy/MM/dd");
//		String receivedTime = date.format(new Date(System.currentTimeMillis()));
		try{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(0,"个税申报总表");

			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT);  
			// 设置列宽,参数一，第几列
			sheet.setColumnWidth((short)0,(short)5000);
			sheet.setColumnWidth((short)1,(short)2000);
			sheet.setColumnWidth((short)2,(short)2000);
			sheet.setColumnWidth((short)3,(short)4000);
			sheet.setColumnWidth((short)4,(short)4000);
			// 第一行
			row = sheet.createRow((short)0);
			// 设置行高
			row.setHeightInPoints(26); 
			// 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,(short)4); 
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("个税申报总表");
			csCell.setCellStyle(this.setTitleStyle(workbook));
			// 第二行
			row = sheet.createRow((short)1);
			row.setHeightInPoints(17); 
			ExportExcelUtil.mergeCell(sheet, 1,(short)0,1,(short)4);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("时间： "+this.getDeclareDate(declaredate));
			csCell.setCellStyle(this.setDateStyle(workbook));
			// 第三行
			row = sheet.createRow((short)2);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("所得项目");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(1));
			
			csCell.setCellValue("税率");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(2));
			
			csCell.setCellValue("人数");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(3));
			
			csCell.setCellValue("计税金额");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(4));
			
			csCell.setCellValue("纳税额");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			String sql = this.getoutsumtsql(fromtable,strwhere,filterByMdule);			
//			System.out.println(sql);
			RowSet rset=dao.search(sql);
			int n=3;
			while(rset.next()){
				if(rset.getDouble("ynse")>=0)
				{
					row = sheet.createRow((short)n);
					row.setHeightInPoints(16); 
					DecimalFormat format = new DecimalFormat("#0.00");
					csCell =row.createCell((short)(0));
					
					csCell.setCellValue(rset.getString("codeitemdesc"));
					csCell.setCellStyle(style);
					
					csCell =row.createCell((short)(1));
					
					String values =rset.getString("sl");
					if(values==null){
						csCell.setCellValue("小计");
					}else{
						csCell.setCellValue(Math.round(rset.getFloat("sl"))+"%");
					}	
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					csCell =row.createCell((short)(2));
					
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(rset.getInt("rs"));
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					csCell =row.createCell((short)(3));
					
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble("ynse"))));
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					csCell =row.createCell((short)(4));
					
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble("Sds"))));
					csCell.setCellStyle(this.setDataStyle(workbook));
					n++;
				}
				
			}
			if(rset.last()){
				n--;
				row = sheet.createRow((short)n);
				row.setHeightInPoints(16); 
				DecimalFormat format = new DecimalFormat("#0.00");
				csCell =row.createCell((short)(0));
				
				csCell.setCellValue("合计");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(1));
				
				csCell.setCellValue("");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(2));
				
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(rset.getInt("rs"));
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(3));
				
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble("ynse"))));
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(4));
				
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble("Sds"))));
				csCell.setCellStyle(this.setDataStyle(workbook));
			}
			row = sheet.createRow((short)n+2);
			csCell =row.createCell((short)(3));
			
			csCell.setCellValue("制表时间：" );
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			csCell =row.createCell((short)(4));
			
			int month = cal.get(Calendar.MONTH)+1;
			csCell.setCellValue(cal.get(Calendar.YEAR)+"."+month+"."+cal.get(Calendar.DATE));
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;

		}catch(Exception e){
			e.printStackTrace();
		}
		return filename;
	}
	/**
	 * 生成个税汇总表
	 * @return ,返回的为文件名,包括目录结构
	 */
	public String exportMinExcel(String fromtable,String strwhere,String declaredate,UserView view,String filterByMdule)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String outname="ExportTaxCount_"+PubFunc.getStrg()+".xls";
		String filename=outname;
		Calendar cal=Calendar.getInstance();
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		RowSet rset = null;
		try{
			HSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(0,"个税申报总表");

			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT);  
			// 设置列宽,参数一，第几列
			sheet.setColumnWidth((short)0,(short)5000);
			sheet.setColumnWidth((short)1,(short)2000);
			sheet.setColumnWidth((short)2,(short)2000);

			// 第一行
			row = sheet.createRow((short)0);
			// 设置行高
			row.setHeightInPoints(26); 
			// 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,(short)2); 
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("个税申报总表");
			csCell.setCellStyle(this.setTitleStyle(workbook));
			// 第二行
			row = sheet.createRow((short)1);
			row.setHeightInPoints(17); 
			ExportExcelUtil.mergeCell(sheet, 1,(short)0,1,(short)2);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("时间： "+this.getDeclareDate(declaredate));
			csCell.setCellStyle(this.setDateStyle(workbook));
			// 第三行
			row = sheet.createRow((short)2);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("所得项目");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(1));
			
			csCell.setCellValue("税率");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(2));
			
			csCell.setCellValue("人数");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));			
			
			String sql = this.getoutsumtsql(fromtable,strwhere,filterByMdule);			
//			System.out.println(sql);
			rset=dao.search(sql);
			int n=3;
			while(rset.next()){
				if(rset.getDouble("ynse")>0)
				{
					row = sheet.createRow((short)n);
					row.setHeightInPoints(16); 
					DecimalFormat format = new DecimalFormat("#0.00");
					csCell =row.createCell((short)(0));
					
					csCell.setCellValue(rset.getString("codeitemdesc"));
					csCell.setCellStyle(style);
					
					csCell =row.createCell((short)(1));
					
					int values = Math.round(rset.getFloat("sl"));
					if(values==0){
						csCell.setCellValue("小计");
					}else{
						csCell.setCellValue(Math.round(rset.getFloat("sl"))+"%");
					}	
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					csCell =row.createCell((short)(2));
					
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(rset.getInt("rs"));
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					n++;	
				}
				
			}
			if(rset.last()){
				n--;
				row = sheet.createRow((short)n);
				row.setHeightInPoints(16); 
				DecimalFormat format = new DecimalFormat("#0.00");
				csCell =row.createCell((short)(0));
				
				csCell.setCellValue("合计");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(1));
				
				csCell.setCellValue("");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(2));
				
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(rset.getInt("rs"));
				csCell.setCellStyle(this.setDataStyle(workbook));
				
			}
			row = sheet.createRow((short)n+2);
			csCell =row.createCell((short)(1));
			
			csCell.setCellValue("制表时间：" );
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			csCell =row.createCell((short)(2));
			
			int month = cal.get(Calendar.MONTH)+1;
			csCell.setCellValue(cal.get(Calendar.YEAR)+"."+month+"."+cal.get(Calendar.DATE));
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rset);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return filename;
	
		
	}
	/**
	 * 生成个税汇总表
	 * @return ,返回的为文件名,包括目录结构
	 */
	public String exportMxSumExcel(String fromtable,String strwhere,String declaredate,ArrayList list,HashMap hm,String title,UserView view,String filterByMdule)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String outname="ExportTaxCount_"+PubFunc.getStrg()+".xls";
		String filename=outname;
		Calendar cal=Calendar.getInstance(); 
		String where =this.getPrivPre(filterByMdule);
		strwhere=where+strwhere;
		HSSFWorkbook workbook = new HSSFWorkbook();
		RowSet rset = null;
		FileOutputStream fileOut = null;
		try{
			HSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(0,(title==null|| "".equals(title)?"个税申报总表":title));

			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT);  
			// 设置列宽,参数一，第几列
			sheet.setColumnWidth((short)0,(short)5000);
			sheet.setColumnWidth((short)1,(short)2000);
			sheet.setColumnWidth((short)2,(short)2000);

			// 第一行
			row = sheet.createRow((short)0);
			// 设置行高
			row.setHeightInPoints(26); 
			// 合并单元格，参数，从第几行，该行的第几个单元格，到第几行，第几个单元格
			int c=2+list.size();
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,(short)c); 
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue((title==null|| "".equals(title)?"个税申报总表":title));
			csCell.setCellStyle(this.setTitleStyle(workbook));
			// 第二行
			row = sheet.createRow((short)1);
			row.setHeightInPoints(17); 
			ExportExcelUtil.mergeCell(sheet, 1,(short)0,1,(short)c);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("时间： "+this.getDeclareDate(declaredate));
			csCell.setCellStyle(this.setDateStyle(workbook));
			// 第三行
			row = sheet.createRow((short)2);
			csCell =row.createCell((short)(0));
			
			csCell.setCellValue("所得项目");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(1));
			
			csCell.setCellValue("税率");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));
			
			csCell =row.createCell((short)(2));
			
			csCell.setCellValue("人数");
			csCell.setCellStyle(this.setFirstRowStyle(workbook));	
			
			int k=3;
			for(int i=0;i<list.size();i++)
			{
				String itemid = (String)list.get(i);
				if(!(itemid==null || "".equals(itemid)))
				{
					ArrayList itemlist = (ArrayList)this.getFieldlist();			
					if("jtynse".equalsIgnoreCase(itemid))
					{
						csCell =row.createCell((short)(k));
						
						csCell.setCellValue("计税金额");
						csCell.setCellStyle(this.setFirstRowStyle(workbook));
					}
					else if("jtSds".equalsIgnoreCase(itemid))
					{
						csCell =row.createCell((short)(k));
						
						csCell.setCellValue("纳税额");
						csCell.setCellStyle(this.setFirstRowStyle(workbook));
					}
					else
					{
						for(int f=0;f<itemlist.size();f++)
						{
							Field field = (Field)itemlist.get(f);
							if(field.getName().equalsIgnoreCase(itemid))
							{
								String itemdesc = field.getLabel();
								csCell =row.createCell((short)(k));
								
								csCell.setCellValue(itemdesc);
								csCell.setCellStyle(this.setFirstRowStyle(workbook));
								break;
							}
						}
						
					}
					k++;	
				}
				
			}
			
			
			String sql = this.getoutsumtsql(fromtable,strwhere,hm);			
//			System.out.println(sql);
			rset=dao.search(sql);
			int n=3;
			while(rset.next()){
				if(rset.getDouble("jtynse")>0)
				{
					row = sheet.getRow((short)n);
					if(row==null)
						row = sheet.createRow((short)n);
					row.setHeightInPoints(16); 
					DecimalFormat format = new DecimalFormat("#0.00");
					csCell =row.createCell((short)(0));
					
					csCell.setCellValue(rset.getString("codeitemdesc"));
					csCell.setCellStyle(style);
					
					csCell =row.createCell((short)(1));
					
					int values = Math.round(rset.getFloat("sl"));
					if(values==0){
						csCell.setCellValue("小计");
					}else{
						csCell.setCellValue(Math.round(rset.getFloat("sl"))+"%");
					}	
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					csCell =row.createCell((short)(2));
					
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(rset.getInt("rs"));
					csCell.setCellStyle(this.setDataStyle(workbook));
					
					int t=3;
					for(int i=0;i<list.size();i++)
					{
						String itemid = (String)list.get(i);
						if(!(itemid==null || "".equals(itemid)))
						{
							csCell =row.createCell((short)(t));
							
							csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble(itemid))));
							csCell.setCellStyle(this.setDataStyle(workbook));
							t++;	
						}
					}
					n++;	
				}
				
			}
			if(rset.last()){
				n--;
				row = sheet.getRow((short)n);
				if(row==null)
					row = sheet.createRow((short)n);
				
				row.setHeightInPoints(16); 
				DecimalFormat format = new DecimalFormat("#0.00");
				csCell =row.createCell((short)(0));
				
				csCell.setCellValue("合计");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(1));
				
				csCell.setCellValue("");
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				csCell =row.createCell((short)(2));
				
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(rset.getInt("rs"));
				csCell.setCellStyle(this.setDataStyle(workbook));
				
				int t=3;
				for(int i=0;i<list.size();i++)
				{
					String itemid = (String)list.get(i);
					if(!(itemid==null || "".equals(itemid)))
					{
						if("ynse".equalsIgnoreCase(itemid))
							itemid="jtynse";
						csCell =row.createCell((short)(t));
						
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						csCell.setCellValue(Double.parseDouble(format.format(rset.getDouble(itemid))));
						csCell.setCellStyle(this.setDataStyle(workbook));
						t++;	
					}
				}
					
				
			}
			row = sheet.getRow((short)n+2);
			if(row==null)
				row = sheet.createRow((short)n+2);
			csCell =row.createCell((short)(c-1));
			
			csCell.setCellValue("制表时间：" );
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			csCell =row.createCell((short)(c));
			
			int month = cal.get(Calendar.MONTH)+1;
			csCell.setCellValue(cal.get(Calendar.YEAR)+"."+month+"."+cal.get(Calendar.DATE));
			csCell.setCellStyle(this.setDateStyle(workbook));
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rset);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return filename;
	
		
	}

	public String getColumStr(RowSet rset,ResultSetMetaData rsetmd,String str) throws SQLException{
		int j=rset.findColumn(str);
		String temp=null;
		switch(rsetmd.getColumnType(j)){
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
			    if(temp.indexOf("12:00:00")!=-1)
			        temp=PubFunc.FormatDate(rset.getDate(j));
				break;
		case Types.CLOB:
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	                    	
				break;
		case Types.BLOB:
				temp="二进制文件";	                    	
				break;		
		case Types.NUMERIC:
			  temp=String.valueOf(rset.getDouble(j));			  
			  break;
		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	/**
	 * 删除个税明细表记录
	 * @param 需要删除的记录
	 */
	public void deleteMxRecord(String str)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String selectstr = "";
			String delsql = "";
			if(str.length()>1)
			{
				selectstr = str.substring(1,str.length());
			}
			delsql = "delete gz_tax_mx where Tax_max_id in ("+selectstr+")";
			dao.update(delsql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 获得薪资类别 
	 * @return
	 */
	public ArrayList getGzMxType(UserView userView)
	{
		ArrayList gztypelist = new ArrayList();
		RowSet rs ;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String gztypeid = "";
			String gztypename = "";
			String sql = "select salaryid,Cname from salarytemplate where (CSTATE IS NULL OR CSTATE='') ";
			rs = dao.search(sql);
			while(rs.next())
			{
				if (!userView.isHaveResource(/*IResourceConstant.LAWRULE*/IResourceConstant.GZ_SET, rs.getString("salaryid")))
				{
					continue;
				}				
				gztypeid = rs.getString("salaryid");
				gztypename = rs.getString("Cname");
				CommonData gztypecd = new CommonData(gztypeid,gztypename);
				gztypelist.add(gztypecd);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return gztypelist;
	}
	
	
	/**
	 * 常量表的维护指标 
	 * @return
	 */
	public ArrayList getRightField()
	{
		ArrayList retlist = new ArrayList();
		Document doc = this.getDoc();
		if(doc!=null)
		{
			try
			{
				String path ="/param/items";
				XPath xpath = XPath.newInstance(path);
				Element items = (Element)xpath.selectSingleNode(doc);
				String itemstr = items.getText();
				if(!("".equals(itemstr)))
				{
					int itemsnum = itemstr.split(",").length;
					if(itemsnum>0)
					{
						String[] itmes  = itemstr.split(",");
						for(int i=0;i<itmes.length;i++)
						{
							String field = itmes[i].toString();
							FieldItem fi = DataDictionary.getFieldItem(field);
							if(fi==null)
								continue;
							String itemid = fi.getItemid();
							String itemdesc = fi.getItemdesc();
							if(!"a0100".equalsIgnoreCase(itemid))
							{
								CommonData itemcd = new CommonData(itemid,itemdesc);
								retlist.add(itemcd);
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
		return retlist;
	}
	/***
	 * 取得是否支持按隶属部门进行所得税管理
	 * @return
	 */
	public String getDeptID()
	{
		String deptid="false";
		Document doc = this.getDoc();
		try
		{
			if(doc!=null)
			{
				String path ="/param";
				XPath xpath = XPath.newInstance(path);
				Element items = (Element)xpath.selectSingleNode(doc);
				if(items.getAttributeValue("deptid")!=null&&items.getAttributeValue("deptid").trim().length()>0)
				{
					deptid=items.getAttributeValue("deptid");
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return deptid;
	}
	/**查询常量表中的GZ_TAX_MX
	 * @return  RecordVo
	 */
	public RecordVo getRecordVo()
	{
		RecordVo ctrlvo = null;
		try
		{
			ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ctrlvo;
	}
	/**获得Document对象
	 * @return  Document
	 */
	public Document getDoc()
	{
		Document doc=null;
		try
		{
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				doc=PubFunc.generateDom(ctrlvo.getString("str_value"));	
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return doc;
		
	}
	/**
	 * 更新个税明细表字段
	 * @param field
	 * @throws JDOMException
	 */
	public void updateTaxMxField(String[] field,String deptid) throws JDOMException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String retstr = "";
		RecordVo ctrlvo = this.getRecordVo();
		if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
		{
			Document doc = this.getDoc();
			String parentpath = "/param/fields";
			Element fields = this.getSingleNode(doc,parentpath);
			String itemspath = "/param/items";
			Element items = this.getSingleNode(doc,itemspath); 
			StringBuffer itemssb =new StringBuffer();
			UpdateTable uto=new UpdateTable();
			ArrayList columnlist = new  ArrayList();
			StringBuffer formats=new StringBuffer();	
			formats.append("############");	
			if(field!=null && field.length>0)
			{
				for(int t=0;t<field.length;t++)
				{
					String fieldtimeid = field[t].toString();	
					if(!("".equals(fieldtimeid)))
					{
						FieldItem fi=DataDictionary.getFieldItem(fieldtimeid);
						boolean flag = this.checkfield(fieldtimeid);
						//  如果是常量表中没有的指标
						//  添加指标
						if(!flag)
						{
							Element fieldelement  = new Element("field");
							fieldelement.setAttribute("id",fieldtimeid);
							fieldelement.setAttribute("visible","true");
							if(fi!=null)
							{
								fieldelement.setAttribute("width","80");
								fieldelement.setAttribute("title",fi.getItemdesc());
								Field ft=uto.getField(false,fieldtimeid,fi.getItemdesc(),fi.getItemtype(),fi.getItemlength(),fi.getDecimalwidth());
								columnlist.add(ft);
							}
							else  // 如果是固定指标
							{
								ArrayList fieldlist = new ArrayList();
								fieldlist = this.getFieldlist();
								for(int x=0;x<fieldlist.size();x++)
								{
									Field addfi = (Field)fieldlist.get(x);
									if(addfi.getName().equalsIgnoreCase(fieldtimeid))
									{
										fieldelement.setAttribute("width","80");
										fieldelement.setAttribute("title",addfi.getLabel());
										Field ft=uto.getField(false,fieldtimeid,addfi.getLabel(),this.getvarType(addfi.getDataType()),addfi.getLength(),addfi.getDecimalDigits());
										columnlist.add(ft);
										break;
									}
								}								
							}
							fields.addContent(fieldelement);
//							fields.a
							itemssb.append(","+fieldtimeid);
						}
					}					
				}
				if("".equals(items.getText()))
				{
					if(itemssb==null || "".equals(itemssb.toString()))
					{
						items.setText("");
					}else{
						items.setText(itemssb.substring(1).toString());
					}
				}else{
					items.setText(items.getText()+itemssb.toString());
				}
				String paramspath="/param";
				Element params = this.getSingleNode(doc,paramspath);
				if(params!=null)
				{
					params.setAttribute("deptid", deptid);
				}
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				retstr = outputter.outputString(doc);
//				System.out.println(retstr);
				ArrayList alist = new ArrayList();
				alist.add(retstr);
				String sql = " update constant set str_value = ? where constant = 'GZ_TAX_MX'";	
				try
				{
					dao.update(sql,alist);
					uto.create_update_Table("GZ_TAX_MX",columnlist,false,this.conn);
					if(!(itemssb==null || "".equals(itemssb.toString())))
					{
						this.updateGzTaxMx(itemssb.substring(1).toString(),dao);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			this.deleteTaxMxField(field);
		}
		else
		{
			this.createXml();
			this.updateTaxMxField(field,deptid);
		}
	}
	/**
	 * 当工资项目的长度，小数位发生变化时，要同步过来
	 * @param fields
	 */
	public void syncTaxTable(String[] fields)
	{
		try{
 			 DbWizard dbw=new DbWizard(this.conn);
			 if(!dbw.isExistTable("gz_tax_mx",false))
				 return;
 			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 ContentDAO dao=new ContentDAO(this.conn);
			 HashMap map=new HashMap();
			 for(int i=0;i<fields.length;i++)
			 {
				 String itemid = fields[i];
				 if(itemid!=null&&!"".equals(itemid))
				 {
					 itemid = itemid.toLowerCase();
			    	 FieldItem tempItem=DataDictionary.getFieldItem(itemid);
			    	 if(tempItem!=null)
			        	 map.put(itemid, tempItem);
				 }
			 }
			 RowSet rowSet=dao.search("select * from GZ_TAX_MX where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
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
							case Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if(size<tempItem.getItemlength()&&tempItem.getItemlength()<=10) //2017-10-11  如果指标长度改大了，需同步结构 zhanghua
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
							case Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
										alterList.add(tempItem.cloneField());
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2017-10-11  如果指标长度改大了，需同步结构 zhanghua
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
							case Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2017-10-11  如果指标长度改大了，需同步结构 zhanghua
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
							case Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}
				rowSet.close();
				
			    Table table=new Table("GZ_TAX_MX");
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
			    	SalaryTemplateBo bo = new SalaryTemplateBo(this.conn);
			    	bo.syncGzOracleField(data,map,"GZ_TAX_MX");
			    }
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
	 * 获得Element
	 * @param fieldtimeid
	 * @return
	 */
	public boolean checkfield (String fieldtimeid)
	{ 
		Document doc = null;
		boolean flag = false;
		try
		{
			doc = this.getDoc();
			String findpath = "/param/fields/field";
			XPath xPath = XPath.newInstance(findpath);
			List list = xPath.selectNodes(doc);
			for(Iterator it=list.iterator();it.hasNext();)
			{
				Element field = (Element)it.next();
				String temp = field.getAttributeValue("id");
				if(temp.equalsIgnoreCase(fieldtimeid))
				{
					flag = true;
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
	 * 删除个税明细表字段
	 * @param addfield
	 * @throws JDOMException
	 */
	public void deleteTaxMxField(String[] field) throws JDOMException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String retstr = "";
		RecordVo ctrlvo = this.getRecordVo();
		if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
		{
			Document doc = this.getDoc();
			String itemspath = "/param/items";
			XPath ixpath = XPath.newInstance(itemspath);
			Element items =  (Element)ixpath.selectSingleNode(doc);
			String fieldspath = "/param/fields/field";
			XPath fxpath = XPath.newInstance(fieldspath);
			List list =  fxpath.selectNodes(doc);
			String timestr = "";
			StringBuffer fieldsb = new StringBuffer();			
			UpdateTable uto=new UpdateTable();
			ArrayList columnlist = new  ArrayList();
			if(items!=null )
			{
				timestr = items.getText();
				
					String[] xmlitems = timestr.split(",");
					for(int i=0;i<xmlitems.length;i++)
					{
						String xmlitemstr = xmlitems[i].toString();// items里面的指标
						if(field==null || "".equalsIgnoreCase(field[0]))  // 如果传回的数组为空，就表示删除全部非固定指标
						{
							for(Iterator it=list.iterator();it.hasNext();)
							{
								Element removetemp =(Element)it.next();// field里的指标
								String temp = removetemp.getAttributeValue("id");
								// items里面有，field里也有的
								if(temp.equalsIgnoreCase(xmlitemstr) && !("a0100".equalsIgnoreCase(temp))
										&& !("a0000".equalsIgnoreCase(temp))){
									removetemp.detach();
									columnlist.add(this.getField(xmlitemstr));	
									break;
								}
							}
						}
						else
						{
							boolean flag = this.checkfields(xmlitemstr,field);
							if(flag==false && !("a0100".equalsIgnoreCase(xmlitemstr))
									&& !("a0000".equalsIgnoreCase(xmlitemstr)))// 删除
							{
								for(Iterator it=list.iterator();it.hasNext();)
								{
									Element removetemp =(Element)it.next();
									String temp = removetemp.getAttributeValue("id");
									
									if(temp.equalsIgnoreCase(xmlitemstr))
									{
										removetemp.detach();
										columnlist.add(this.getField(xmlitemstr));	
										break;
									}
								}
							}
							else
							{
								fieldsb.append(","+xmlitemstr);
							}
						}						
					}
												
			}			
			if(fieldsb==null || "".equals(fieldsb.toString()))
			{
				items.setText("");
			}
			else
			{
				items.setText(fieldsb.substring(1).toString());
			}			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			retstr = outputter.outputString(doc);
//			System.out.println(retstr);
			ArrayList alist = new ArrayList();
			alist.add(retstr);
			String sql = " update constant set str_value = ? where constant = 'GZ_TAX_MX'";			
			try
			{
				dao.update(sql,alist);
//				System.out.println(sql);
				uto.create_update_Table("GZ_TAX_MX",columnlist,true,this.conn);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 * 判断删除的指标,为true的不要删除 
	 * @param xmlitems
	 * @param fields
	 * @return
	 */
	public boolean checkfields(String xmlitems,String[] fields)
	{
		boolean flag = false;
		if(fields!=null && fields.length>0)
		{
			for(int i=0;i<fields.length;i++)
			{
				String field = fields[i].toString();
				if(field.equalsIgnoreCase(xmlitems))
				{
					flag = true;  // 如果传回的数组里有的，item里面也有的，就不要删除，falg为true
				}			
			}
		}
		return flag;
	}
	/**
	 * 获得需要隐藏列的数组
	 * @param hides
	 * @return
	 */
	public String[] gethidestr (String hides)
	{
		String[] hide = null;
		int tempnum = hides.split("\\/").length;
		if(tempnum>0)
		{
			hide = hides.split("\\/");
		}
		return hide;
	}
	/**
	 * 隐藏列
	 * @param hides
	 * @return
	 */
	public void hideTaxField(String[] hide) 
	{
		Document doc = this.getDoc();
		String retstr = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if(hide!=null && hide.length>0)
			{
				for(int i=0;i<hide.length;i++)
				{
					String hidetemp = hide[i].toString();
					String[] hidename = hidetemp.split(",");
					String fieldpath = "/param/fields/field";
					XPath xpath = XPath.newInstance(fieldpath);
					List fieldlist = xpath.selectNodes(doc);
					Element viselement = null;
					for(Iterator it =fieldlist.iterator();it.hasNext();)
					{
						viselement = (Element)it.next();
						if(viselement.getAttributeValue("id").equalsIgnoreCase(hidename[0]))
						{
							if("0".equalsIgnoreCase(hidename[1]))
							{
								viselement.setAttribute("visible","true");
							}else{
								viselement.setAttribute("visible","false");
							}
							if("Tax_max_id".equalsIgnoreCase(viselement.getAttributeValue("id")))
							{
								viselement.setAttribute("visible","false");
							}						
							break;
						}
					}
				}
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				retstr = outputter.outputString(doc);
//				System.out.println(retstr);
				String sql = "update constant set str_value = '"+retstr+"' where UPPER(constant) = 'GZ_TAX_MX' ";
				dao.update(sql);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 获得原来常量表中的指标
	 * @return
	 */
	public Map sortbefore()
	{
		Map map = new HashMap();
		try
		{
			Document doc = this.getDoc();
			String path = "/param/fields/field";
			XPath xpath = XPath.newInstance(path);
			List fields = xpath.selectNodes(doc);
			Element element = null;
			for(Iterator it = fields.iterator();it.hasNext();)
			{

				element = (Element)it.next();
				String itemid = element.getAttributeValue("id");
				map.put(itemid,element);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}	
	/**
	 * 排序
	 * @return
	 */
	public void sort(String[] sort)
	{
		Map map = this.sortbefore();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList fieldlist = this.getFieldlist();	
			Document doc = this.getDoc();
			String path = "/param/fields";
			XPath xpath = XPath.newInstance(path);
			Element fields = (Element)xpath.selectSingleNode(doc);
			List list = fields.removeContent();
			for(int i=0;i<sort.length;i++)
			{
				String field = sort[i];
				for(int t=0;t<fieldlist.size();t++)
				{
					Field fi = (Field)fieldlist.get(t);
					if(fi.getName().equalsIgnoreCase(field))
					{
						Element fieldnote = new Element("field");
						fieldnote.setAttribute("id",fi.getName());
						fieldnote.setAttribute("width","80");
						fieldnote.setAttribute("visible",fi.isVisible()+"");
						fieldnote.setAttribute("title",fi.getLabel());
						fields.addContent(fieldnote);
						break;
					}
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String retstr = outputter.outputString(doc);
//			System.out.println(retstr);
			String sql = "update constant set str_value = '"+retstr+"' where UPPER(constant) = 'GZ_TAX_MX' ";
			dao.update(sql);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public String getTimeSql(Factor factor)
	{
		TimeScope ts = new TimeScope();
		String retstr = ts.getTimeConditon(factor.getFieldname(),factor.getOper(),factor.getValue());
		return retstr;
	}
	
	public String getconstr(Factor factor,boolean like)
	{
		String log = "";
		String fieldncon = "";
		StringBuffer partsql = new StringBuffer();
		if("*".equalsIgnoreCase(factor.getLog()))
			log = " and ";
		else 
			log = " or ";
		if(!(factor.getValue()==null || "".equals(factor.getValue())))
		{
			if(like)
			{
			
			}
			else
			{

			}
		}		
		return partsql.toString();
	}
	/**
	 * 获得查询条件部分SQL
	 * @param factorlist
	 * @param like 模糊查询
	 * @return
	 */
	public String getSql(ArrayList factorlist,boolean like)
	{
		String sql = "";
		String log = "";
		String fieldncon = "";
		StringBuffer partsql = new StringBuffer();
		for(int i=0;i<factorlist.size();i++)
		{
			Factor factor = (Factor)factorlist.get(i);
			if(!(factor.getLog()==null || "".equals(factor.getLog())))
			{
				
				if(this.getconstr(factor,like).length()>1)
				{
					partsql.append(this.getconstr(factor,like));					
				}				
			}			
		}
		sql = " where "+partsql.substring(4).toString();
//		System.out.println(sql);
		return sql;
	}
	/**
	 * 得到常量表中的指标
	 * @return
	 */
	public ArrayList getitemid()
	{
		ArrayList itemidlist = new ArrayList();
		try
		{
			Document doc = this.getDoc();
			String path = "/param/fields/field";
			XPath xpath = XPath.newInstance(path);
			List fields = xpath.selectNodes(doc);
			Element element = null;
			for(Iterator it = fields.iterator();it.hasNext();)
			{
				element = (Element)it.next();
				String itemid = element.getAttributeValue("id");
				String itemdesc = element.getAttributeValue("title");
				CommonData itemcd = new CommonData(itemid,itemdesc);
				itemidlist.add(itemcd);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return itemidlist;
	}
	/**
	 * 为前台list处理指标
	 * @param fieldlist
	 * @return
	 */
	public ArrayList getitemlist(ArrayList fieldlist)
	{
		ArrayList retlist = new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			Field field = (Field)fieldlist.get(i);
			String itemid = field.getName();
			String itendesc = field.getLabel();
			if(!("Tax_max_id".equalsIgnoreCase(itemid) || "A0100".equalsIgnoreCase(itemid) || "flag".equalsIgnoreCase(itemid) ) )
			{
				retlist.add(field);
			}
		}
		Field field = new Field("salaryid","工资类别号");
		field.setDatatype(DataType.INT);
		field.setLength(12);
		retlist.add(field);
		return retlist;
	}
	/**
	 * 获得要修改的字段信息
	 * @param xmlitem
	 * @return
	 */
	public Field getField(String xmlitem)
	{
		UpdateTable uto=new UpdateTable();
		FieldItem fi=DataDictionary.getFieldItem(xmlitem);
		Field ft = null;
		if(fi!=null)
		{									
			 ft =uto.getField(false,xmlitem,fi.getItemdesc(),fi.getItemtype(),fi.getItemlength(),fi.getDecimalwidth());			
		}
		else
		{
			ArrayList fieldlist = this.getFieldlist();
			for(int x=0;x<fieldlist.size();x++)
			{
				Field addfi = (Field)fieldlist.get(x);
				if(addfi.getName().equalsIgnoreCase(xmlitem))
				{
					ft=uto.getField(false,xmlitem,addfi.getLabel(),this.getvarType(addfi.getDatatype()),addfi.getLength(),addfi.getDecimalDigits());
				}
			}
		}
		return ft;
	}
	/**
	 * 获得单个结点
	 * @param doc
	 * @param path
	 * @return
	 */
	public Element getSingleNode(Document doc,String path)
	{
		Element fields= null;
		try
		{
			XPath xpath = XPath.newInstance(path);
			fields =  (Element)xpath.selectSingleNode(doc);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields;
	}
	/**
	 * 创建新XML
	 */
	public void createXml()
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String xmlstr = "";
		Element param = new Element("param");
		Element items = new Element("items");
		Element fields = new Element("fields");
		ArrayList fieldlist = this.searchCommonItemList();
		for(Iterator it=fieldlist.iterator();it.hasNext();)
		{
			Element field = new Element("field");
			Field fi = (Field)it.next();
			String id = fi.getName();
			String visible = fi.isVisible()+"";
			String width = "80";
			String title = fi.getLabel();
			field.setAttribute("id",id);
			field.setAttribute("width",width);
			field.setAttribute("visible",visible);
			field.setAttribute("title",title);
			fields.addContent(field);
		}
		param.setAttribute("deptid","false");
		param.addContent(items);
		param.addContent(fields);
		Document doc = new Document(param);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		xmlstr = outputter.outputString(doc);
		String sql = "update constant set str_value = '"+xmlstr+"' where constant like 'gz_tax_mx' ";
		try
		{
			dao.update(sql);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 从薪资历史表中导入数据，更新个税明细表
	 * @param fields
	 */
	public void updateGzTaxMx(String fieldstr,ContentDAO dao)
	{
		String[] fields = null;	
		try
		{
			int tempnum = fieldstr.split(",").length;
			if(tempnum>0)
			{
				fields = fieldstr.split(",");
				if(fields!=null && fields.length>0)
				{
					for(int t=0;t<fields.length;t++)
					{
						StringBuffer sqlsb = new StringBuffer();
						String field = fields[t].toString();
						sqlsb.append("update gz_tax_mx set "+field+"=( ");
						sqlsb.append("select "+field+" from salaryhistory t");
						sqlsb.append(" where gz_tax_mx.NBASE =t.NBASE ");
						sqlsb.append(" and gz_tax_mx.A0100 = t.A0100 ");
						sqlsb.append(" and gz_tax_mx.SalaryId = t.SalaryId ");
						sqlsb.append(" and  gz_tax_mx.A00Z0 = t.A00Z0");
						sqlsb.append(" and gz_tax_mx.A00Z1 = t.A00Z1 )");
//						System.out.println(sqlsb.toString());
						dao.update(sqlsb.toString());
					}
				}
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	public void synData(String fieldstr)
	{
		try
		{
			if(fieldstr==null||fieldstr.trim().length()==0)
				return;
			String[] arr = fieldstr.split(",");
			ContentDAO dao = new ContentDAO(this.conn);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				StringBuffer buf = new StringBuffer();
				StringBuffer temp=new StringBuffer();
	    		for(int i=0;i<arr.length;i++)
    			{
	    			if(arr[i]==null|| "".equals(arr[i]))
	    				continue;
	    			buf.append(" g."+arr[i]+" as "+arr[i]+"_1,s."+arr[i]+" as "+arr[i]+"_2,");
	    			temp.append(arr[i]+"_1="+arr[i]+"_2,");
    			}
	    		if(buf.toString().length()>0)
	    		{
	     			buf.setLength(buf.length()-1);
	     			temp.setLength(temp.length()-1);
	    		}
	    		StringBuffer sql = new StringBuffer();
	    		sql.append(" update ( select ");
	    		sql.append(buf.toString());
	    		sql.append(" from gz_tax_mx g,salaryhistory s where g.NBASE =s.NBASE and g.A0100 = s.A0100 ");
		    	sql.append(" and g.SalaryId = s.SalaryId and  g.A00Z0 = s.A00Z0");
		    	sql.append(" and g.A00Z1 = s.A00Z1 )");
		    	sql.append(" set ");
		    	sql.append(temp.toString());
		    	dao.update(sql.toString());
			}
			else
			{
	    		StringBuffer buf = new StringBuffer();
	    		for(int i=0;i<arr.length;i++)
    			{
	    			if(arr[i]==null|| "".equals(arr[i]))
	    				continue;
	    			buf.append(" gz_tax_mx."+arr[i]+"=salaryhistory."+arr[i]+",");
    			}
	    		if(buf.toString().length()>0)
	    		{
	     			buf.setLength(buf.length()-1);
	    		}
	    		StringBuffer sql = new StringBuffer();
	    		sql.append(" update gz_tax_mx set ");
	    		sql.append(buf.toString());
		    	sql.append(" from gz_tax_mx,salaryhistory where gz_tax_mx.NBASE =salaryhistory.NBASE and gz_tax_mx.A0100 = salaryhistory.A0100 ");
		    	sql.append(" and gz_tax_mx.SalaryId = salaryhistory.SalaryId and  gz_tax_mx.A00Z0 = salaryhistory.A00Z0");
		    	sql.append(" and gz_tax_mx.A00Z1 = salaryhistory.A00Z1 ");
		    	dao.update(sql.toString());
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	/**
	 * 个税明细导出的SQL
	 * @param feildstr
	 * @param timewhere
	 * @return
	 */
	public String getoutpartsql(String fromtable,String feildstr,String timewhere,String exporttype,String filterByMdule)
	{
		StringBuffer sqlsb = new StringBuffer();
		if("1".equals(filterByMdule)&& "1".equals(exporttype))
		{
			
			String pre=this.getPrivPre(filterByMdule);
			StringBuffer temp=new StringBuffer("");
			temp.append(" from  "+fromtable);
			temp.append(" where ("+pre+")");
	    	if(timewhere!=null&&!"".equals(timewhere))
	    	{
	    		temp.append(timewhere);
	    	}
	    	String sumSql=this.getSumSql();
	    	sqlsb.append("select "+feildstr+",a0100,"+sumSql+" from "+fromtable+" where a0100 in (select a0100 "+temp.toString()+")");
	    	if(timewhere!=null&&!"".equals(timewhere))
	    	{
	    		sqlsb.append(timewhere);
	    	}
	    	
	    	//20141018 dengcan  
	    	sqlsb.append(" and ("+pre+")");
	    	
	    	ArrayList list = view.getPrivDbList();
	        StringBuffer nbaseBuf = new StringBuffer(" ");
	        if(list==null||list.size()<=0)
	        {
	        	nbaseBuf.append(" and 1=2 ");
	        }
	        else
	        {
	        	for(int j=0;j<list.size();j++)
	        	{
	        		if(j==0)
	        			nbaseBuf.append(" and UPPER(NBASE) IN(");
	        		if(j!=0)
	        			nbaseBuf.append(",");
	        		nbaseBuf.append("'"+((String)list.get(j)).toUpperCase()+"'");
	        		if(j==list.size()-1)
	        			nbaseBuf.append(")");
	        			
	        	}
	        }
	        sqlsb.append(nbaseBuf.toString());
	        sqlsb.append(" group by nbase,a0100,"+Sql_switcher.dateToChar("tax_date","yyyy-MM")+",taxmode,deptid) T left join (select dbid,pre from DBName) DBName on upper(t.nbase)=upper(DBName.Pre) ");
	    	StringBuffer buf=new StringBuffer("select "+feildstr.toUpperCase()+" ,a0100,sum(mynse) as mynse from (");
	    	buf.append(sqlsb+" group by DBName.DbId,a0100,"+Sql_switcher.dateToChar("tax_date","yyyy-MM")+",taxmode order by dbname.dbid,a0000,a00z0,a00z1,b0110,e0122 ");
	    	sqlsb.setLength(0);
	    	sqlsb.append(buf.toString());
		}
		else
		{
			String pre=this.getPrivPre(filterByMdule);
	    	sqlsb.append("select "+feildstr+" from "+fromtable+" left join (select dbid,pre from dbname) dbname on "+fromtable+".nbase=dbname.pre ");
	    	sqlsb.append(" where ("+pre+")");
	    	if(timewhere!=null&&!"".equals(timewhere))
	    	{
	        	sqlsb.append(timewhere);
	    	}
	    	/**税率为0的也要导出*/
	    	//sqlsb.append(" and "+Sql_switcher.sqlNull("SDS",0)+">0 ");
		   if("1".equalsIgnoreCase(exporttype))
	    	{
			   //合并计算按月合并 2014-7-2  dengcan
    			sqlsb.append(" group by nbase,a0100,"+Sql_switcher.dateToChar("tax_date","yyyy-MM")+",taxmode");
    			 
    		}
	    	else
	    	{
	    		sqlsb.append(" order by dbid,a0000,a00z0,a00z1");
	    	}
    		if("1".equals(exporttype))
	    	{
		    	StringBuffer ss= new StringBuffer();
    			ss.append(" select T.* from (");
	    		ss.append(sqlsb.toString());
	    		ss.append(") T left join (select dbid,pre from DBName) DBName on upper(t.nbase)=upper(DBName.Pre) order by dbname.dbid,a0000,a00z0,a00z1,b0110,e0122 ");
	    		sqlsb.setLength(0);
	    		sqlsb.append(ss.toString());
	    	}
			
		}
		
		/*sqlsb.append("select "+feildstr+" from GZ_TAX_MX m ");
		sqlsb.append(" right join (select nbase,b0110,e0122, A0101,");
		sqlsb.append(" Tax_date,sum("+Sql_switcher.sqlNull("ynse",(float) 0.0)+") as ynse,sum("+Sql_switcher.sqlNull("SDS",(float) 0.0)+") as Sds,");
		sqlsb.append(" TaxMode from GZ_TAX_MX where TaxMode <> 2 and TaxMode <> 4 ");
		sqlsb.append(timewhere+" group by");
		sqlsb.append(" Tax_date,nbase,b0110,e0122,A0101,TaxMode ");
		sqlsb.append(" having sum("+Sql_switcher.sqlNull("SDS",(float) 0.0)+")>0 union  all ");
		sqlsb.append(" select nbase,b0110,e0122,A0101,Tax_date,");
		sqlsb.append(" "+Sql_switcher.sqlNull("ynse",(float) 0.0)+","+Sql_switcher.sqlNull("SDS",(float) 0.0)+",TaxMode from GZ_TAX_MX ");
		sqlsb.append(" where TaxMode = 4 and "+Sql_switcher.sqlNull("SDS",(float) 0.0)+">0 ");
		sqlsb.append(timewhere);
		sqlsb.append(" union  all select nbase,b0110,e0122,A0101,");
		sqlsb.append(" Tax_date, sum("+Sql_switcher.sqlNull("ynse",(float) 0.0)+") as ynse,");
		sqlsb.append(" sum("+Sql_switcher.sqlNull("SDS",0)+") as Sds,TaxMode from GZ_TAX_MX ");
		sqlsb.append(" where TaxMode = 2 and "+Sql_switcher.sqlNull("SDS",0)+">0 ");
		sqlsb.append(timewhere);
		sqlsb.append("  group by  nbase,b0110,e0122,");
		sqlsb.append(" A0101,Tax_date,TaxMode ");
		sqlsb.append(" having sum("+Sql_switcher.sqlNull("SDS",0)+")>0 )t ");
		sqlsb.append(" on t.nbase = m.nbase and t.b0110 = m.b0110 ");
		sqlsb.append(" and t.e0122 = m.e0122 and t.A0101 = m.A0101 ");
		sqlsb.append(" and t.Tax_date = m.Tax_date ");
		sqlsb.append(" and t.ynse =m.ynse and t.Sds = m.sds ");
		sqlsb.append(" and t.TaxMode = m.TaxMode order by t.B0110,t.E0122,A0000");		*/
		return sqlsb.toString();
	}
	/**
	 * 获得要查询的字段
	 * @param itemid
	 * @return
	 */
	public String getitemstr(String itemid,int datatype)
	{
		String itemstr= "";
		if("a00z1".equalsIgnoreCase(itemid)|| "sl".equalsIgnoreCase(itemid)|| "basedata".equalsIgnoreCase(itemid)|| "sskcs".equalsIgnoreCase(itemid))
		{
			itemstr="max("+itemid+") as "+itemid;
		}
		else if("ynse".equalsIgnoreCase(itemid)||datatype==DataType.FLOAT||datatype==DataType.INT||datatype==DataType.DOUBLE)
		{
			itemstr="sum("+itemid+") as "+itemid;
		}			
		else
		{
			String itemTemp=itemid;
			FieldItem item=DataDictionary.getFieldItem(itemid);
			if(item!=null&& "M".equals(item.getItemtype()))
				itemTemp="cast("+itemid+" as VarChar(2000))";
			itemstr="max("+itemTemp+") as "+itemid;
			 
			
		}
		return itemstr;
	}
	
	public String getoutsumtsql(String fromtable,String timewhere,HashMap hm)
	{
		StringBuffer sqlsb = new StringBuffer();
		try
		{
			ArrayList list = view.getPrivDbList();
	        StringBuffer nbaseBuf = new StringBuffer(" ");
	        if(list==null||list.size()<=0)
	        {
	        	nbaseBuf.append(" and 1=2 ");
	        }
	        else
	        {
	        	for(int j=0;j<list.size();j++)
	        	{
	        		if(j==0)
	        			nbaseBuf.append(" and UPPER(NBASE) IN(");
	        		if(j!=0)
	        			nbaseBuf.append(",");
	        		nbaseBuf.append("'"+((String)list.get(j)).toUpperCase()+"'");
	        		if(j==list.size()-1)
	        			nbaseBuf.append(")");
	        			
	        	}
	        }
	        //-------------------------------------------这块重写了下，zhaoxg add 2015-2-13---------------------------
		       sqlsb.append("select T.*,f.codeitemdesc from ( ");           		       
		       sqlsb.append("(select sl,count(*) as rs,taxmode,sum(case when ynse>basedata then ynse-basedata else 0 end) as jtynse,sum(sds) as jtsds"+hm.get("f")+" from");
		       sqlsb.append("(");
		       sqlsb.append("select "+Sql_switcher.sqlNull("TAXMODE", "0")+" as TAXMODE,A0100,"+Sql_switcher.dateToChar("declare_tax", "yyyy-mm"));
		       sqlsb.append(" as declare_tax,max("+Sql_switcher.isnull("SL","0")+" * 100) as SL,");		   
		       sqlsb.append("SUM(ABS("+Sql_switcher.sqlNull("ynse", "0")+")) as ynse,max("+Sql_switcher.isnull("basedata", "0")+") AS basedata,");
		       sqlsb.append("SUM(ABS("+Sql_switcher.isnull("sds", "0")+")) AS sds,"+Sql_switcher.dateToChar("tax_date", "yyyy-mm")+" as tax_date");
		       sqlsb.append(hm.get("f"));
		       sqlsb.append(" from ").append(fromtable).append(" where ").append(timewhere+" "+nbaseBuf);
		       sqlsb.append("group by taxmode,nbase,a0100,declare_tax,tax_date) A");
				if(Sql_switcher.searchDbServer()== Constant.ORACEL)
				{
					sqlsb.append(" group by rollup (taxmode, sl)");
				}
				else
				{			
					sqlsb.append(" group by  taxmode, sl with rollup");
				}
				sqlsb.append(") T left join (select codeitemdesc,codeitemid from codeitem where codesetid='46') f on f.codeitemid= T.taxmode");
				sqlsb.append(")");		
				sqlsb.append(" order by case when T.taxmode is null then 'z' else T.taxmode end,");
				sqlsb.append("case when T.sl is null then 99999 else T.sl end");
				//-------------------------------------------end----------------------------------------------------------
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sqlsb.toString();
	}
	public String getoutsumtsql(String fromtable,String timewhere,String filterByMdule)
	{
		StringBuffer sqlsb = new StringBuffer();
		try
		{
			String pre=this.getPrivPre(filterByMdule);
			/* 所得税管理，给业务用户分配业务范围，所得税管理（指定部门后），导出汇总表非所见即所得问题处理。 xiaoyun 2014-10-21 start */
			if(pre != null && pre.trim().length() > 0) {
				pre = "("+pre+")";
			}
			/* 所得税管理，给业务用户分配业务范围，所得税管理（指定部门后），导出汇总表非所见即所得问题处理。 xiaoyun 2014-10-21 end */
			
			ArrayList list = view.getPrivDbList();
	        StringBuffer nbaseBuf = new StringBuffer(" ");
	        if(list==null||list.size()<=0)
	        {
	        	nbaseBuf.append(" and 1=2 ");
	        }
	        else
	        {
	        	for(int j=0;j<list.size();j++)
	        	{
	        		if(j==0)
	        			nbaseBuf.append(" and UPPER(NBASE) IN(");
	        		if(j!=0)
	        			nbaseBuf.append(",");
	        		nbaseBuf.append("'"+((String)list.get(j)).toUpperCase()+"'");
	        		if(j==list.size()-1)
	        			nbaseBuf.append(")");
	        			
	        	}
	        }
//			System.out.println(dbserver);
/*			sqlsb.append(" select t.codeitemdesc ,t.sl as sl,sum(rs) as rs,");
			sqlsb.append("sum(ynse) as ynse,sum(Sds) as Sds   from ( ");
			// 数据集 t
			sqlsb.append("select f.codeitemdesc as codeitemdesc,e.sl,e.rs,e.ynse,e.Sds ");
			sqlsb.append(" from (");
			// e 
			sqlsb.append("select d.TAXMODE,d.sl,("+Sql_switcher.sqlNull("d.rs",0));
			sqlsb.append("+"+Sql_switcher.sqlNull("c.rs",0)+") as rs,d.ynse,d.Sds");
			// 数据集 d
			sqlsb.append(" from (select a.TAXMODE,a.sl,b.rs,a.ynse,");
			sqlsb.append(" a.Sds from (");
			// a
			sqlsb.append(" SELECT TAXMODE,SL as sl,");
			sqlsb.append(" SUM(case when ynse>basedata then ynse-basedata else 0 end) as ynse,sum(Sds) as Sds from (");
			sqlsb.append("  select "+Sql_switcher.sqlNull("TAXMODE",1)+" as TAXMODE,a0100,TAX_DATE,");
			sqlsb.append(" max("+Sql_switcher.sqlNull("SL","0")+"*100) as SL,SUM(ynse) as ynse,");
			sqlsb.append(" max(basedata) AS basedata,SUM(sds) AS Sds from gz_tax_mx ");
			sqlsb.append(" where ");
			sqlsb.append(" ("+pre+")");//("+Sql_switcher.sqlNull("SDS",0)+">0)
			sqlsb.append(" "+timewhere+" "+nbaseBuf+" group by TAXMODE,a0100,TAX_DATE ");
			sqlsb.append(" ) x group by x.TAXMODE,x.SL  ) a ");
			//end a
			sqlsb.append(" left join (");
			sqlsb.append(" SELECT x.TAXMODE, x.sl, COUNT(*) AS rs ");
			sqlsb.append(" FROM (SELECT a0100, TAX_DATE, "+Sql_switcher.sqlNull("TAXMODE",1)+" AS TAXMODE,");
			sqlsb.append(" max(ABS("+Sql_switcher.sqlNull("SL","0")+" * 100)) AS sl  FROM gz_tax_mx WHERE ");
			sqlsb.append("("+pre+")");//("+Sql_switcher.sqlNull("SDS",0)+">0)
			sqlsb.append(" "+timewhere+" "+nbaseBuf+" AND ");
			sqlsb.append(" ("+Sql_switcher.sqlNull("TaxMode",0)+"<>'4') GROUP BY "+Sql_switcher.sqlNull("TAXMODE",1)+",a0100,");
			sqlsb.append(" TAX_DATE) x GROUP BY x.TAXMODE, x.sl) b on a.TAXMODE=b.TAXMODE");
			sqlsb.append(" and a.sl  =b.sl ) d left join ");
			//d end
			sqlsb.append("( SELECT "+Sql_switcher.sqlNull("TAXMODE",1)+"as TAXMODE,ABS("+Sql_switcher.sqlNull("SL","0")+"*100) AS sl,");
			sqlsb.append(" count(*) AS rs    FROM gz_tax_mx where ");
			sqlsb.append(" ("+pre+")"); //("+Sql_switcher.sqlNull("SDS",0)+">0)
			sqlsb.append(" "+timewhere+" "+nbaseBuf+"  AND ( TaxMode='4')");
			sqlsb.append("  group by "+Sql_switcher.sqlNull("TAXMODE",1)+",ABS("+Sql_switcher.sqlNull("SL","0")+" *100)  ) c");
			sqlsb.append(" on d.TAXMODE=c.TAXMODE and d.sl  =c.sl  ) e ");	
			// end e
			sqlsb.append(" left join (select codeitemid,codeitemdesc FROM Codeitem");
			sqlsb.append(" where  codesetid='46') f on e.TAXMODE=f.codeitemid ) t ");*/
	       sqlsb.append("select T.*,f.codeitemdesc from ( ");           
	       /* 所得税管理-个税申报汇总模板导出业务逻辑统计错误 xiaoyun 2014-10-14 start */
	       /*
	       sqlsb.append("select sl,sum(rs) as rs, taxmode,sum(ynse) as ynse,sum(sds) as sds from(");                        
	       sqlsb.append("select ABS("+Sql_switcher.sqlNull("SL", "0")+"*100) as sl,count(*) as rs,taxmode,SUM(case when ynse>basedata then ynse-basedata else 0 end) as ynse");
	       sqlsb.append(",sum(sds) as sds from "+fromtable);
	       sqlsb.append(" where ("+pre+") "+timewhere+" "+nbaseBuf+" group by taxmode,sl) A ");
	       */
	       
	       sqlsb.append("(select sl,count(*) as rs,taxmode,sum(case when ynse>basedata then ynse-basedata else 0 end) as ynse,sum(sds) as sds from");
	       sqlsb.append("(");
	       sqlsb.append("select "+Sql_switcher.sqlNull("TAXMODE", "0")+" as TAXMODE,A0100,"+Sql_switcher.dateToChar("declare_tax", "yyyy-mm"));
	       sqlsb.append(" as declare_tax,max("+Sql_switcher.isnull("SL","0")+" * 100) as SL,");		   
	       sqlsb.append("SUM(ABS("+Sql_switcher.sqlNull("ynse", "0")+")) as ynse,max("+Sql_switcher.isnull("basedata", "0")+") AS basedata,");
	       sqlsb.append("SUM(ABS("+Sql_switcher.isnull("sds", "0")+")) AS sds,"+Sql_switcher.dateToChar("tax_date", "yyyy-mm")+" as tax_date");
	       sqlsb.append(" from ").append(fromtable).append(" where ").append(pre+" ").append(timewhere+" "+nbaseBuf);
	       sqlsb.append("group by taxmode,nbase,a0100,declare_tax,tax_date) A");
	       /* 所得税管理-个税申报汇总模板导出业务逻辑统计错误 xiaoyun 2014-10-14 end */
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)
			{

				sqlsb.append(" group by rollup (taxmode, sl)");
			}
			else
			{			
				sqlsb.append(" group by  taxmode, sl with rollup");
			}
			sqlsb.append(") T left join (select codeitemdesc,codeitemid from codeitem where codesetid='46') f on f.codeitemid= T.taxmode");
			/* 所得税管理-个税申报汇总模板导出业务逻辑统计错误 xiaoyun 2014-10-14 start */
			sqlsb.append(")");		
			sqlsb.append(" order by case when T.taxmode is null then 'z' else T.taxmode end,");
			sqlsb.append("case when T.sl is null then 99999 else T.sl end");
			/* 所得税管理-个税申报汇总模板导出业务逻辑统计错误 xiaoyun 2014-10-14 end */
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sqlsb.toString();
	}
	
	public String getFloat(String desc,int decimalwidth){
		String fielddesc = "";
		StringBuffer temp= new StringBuffer("#0.");
		for(int i=0;i<decimalwidth;i++){
			temp.append("0");
		}
		
		DecimalFormat format = new DecimalFormat(temp.toString());
		double a=0;
		if(desc!=null&&desc.trim().length()>0){
			a = Double.parseDouble(desc);
			fielddesc = format.format(a);
		}
		
		return fielddesc;
	}
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	public String getvarType(int varType)
	{
		String type="";
		if(varType==DataType.DATE)
			type="D";
		else if(varType==DataType.STRING)
			type="A";
		else if(varType==DataType.INT||varType==DataType.FLOAT)
			type="N";
		else if(varType==DataType.CLOB)
			type="M";
		else
			type="A";
		return type;
	}
	
	public String setFormat(int decimal)
	{
		String retformat = "";
		if(decimal==1)
		{
			retformat="#";
		}else if(decimal==2){
			retformat="##";
		}
		else if(decimal==3){
			retformat="###";
		}
		else if(decimal==4){
			retformat="####";
		}
		else if(decimal==5){
			retformat="#####";
		}
		else if(decimal==6){
			retformat="######";
		}
		else if(decimal==7){
			retformat="#######";
		}
		else if(decimal==8){
			retformat="########";
		}
		else if(decimal==9){
			retformat="#########";
		}
		else if(decimal==10){
			retformat="##########";
		}
		else if(decimal==11){
			retformat="###########";
		}else if(decimal==12){
			retformat="############";
		}
		return retformat;
	}
	/**
	 * 设置单元格格式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle setTitleStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 20); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        return style;

	}
	
	public HSSFCellStyle setFirstRowStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 12); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;

	}
	public HSSFCellStyle setDataStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 13); // 字体大小
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        return style;

	}
	public String getDeclareDate(String declaredate)
	{
		String ret = "";
		if(!(declaredate==null || "".equals(declaredate)))
		{
			if("all".equalsIgnoreCase(declaredate))
			{
				ret ="   年     月";
			}else{
				String year = "";
				String month = "";
				String arr[] = declaredate.split("\\.");
				year = arr[0]+"  年 ";
				month = arr[1]+" 月";
				ret = year+month;
			}
		}
		return ret;
	}
	/**
	 * 取得导入文件中列指标列表
	 * @param form_file
	 * @author dengcan
	 * @return
	 */
    public ArrayList getExcelDataFiledList(FormFile form_file) throws GeneralException {
        ArrayList list = new ArrayList();
        InputStream in = null;
        try {
            TaxMxExcelBo tmeb = new TaxMxExcelBo(this.conn);
            in = form_file.getInputStream();
            tmeb.getSelfAttribute(in);
            list = tmeb.getRowAllInfo(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (in != null)
                PubFunc.closeResource(in);
        }
        return list;
    }
	/**
	 * 取得导入文件中列指标列表
	 * @param form_file
	 * @author dengcan
	 * @return
	 */
	public ArrayList getExcelFiledList(FormFile form_file)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			TaxMxExcelBo tmeb=new TaxMxExcelBo(this.conn);
			tmeb.getSelfAttribute(form_file.getInputStream());
			list=tmeb.getRowFirstInfo(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 将流写入文件
	 * @param stream
	 */
	public void writeFile(InputStream stream)
	{
		  
	        File newFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
	    			+"importGzData.xls");
	        FileOutputStream outPutStream = null;
	        try {
	          
	            outPutStream = new FileOutputStream(newFile);
	            byte[] byteArr = new byte[512];
	            while (stream.read(byteArr) > 0) {
	                outPutStream.write(byteArr);
	                outPutStream.flush();
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            PubFunc.closeIoResource(outPutStream);
	        }
	}
	/**
	 * 取得 薪资类别中的薪资项目列表
	 * @return
	 */
	public ArrayList getTaxMxItemList()
	{
		ArrayList list=new ArrayList();
		/**取排序后的指标列表*/
		ArrayList TaxMxItemList=getFieldlist()/*this.getTaxMxField()*/;
		for(int i=0;i<TaxMxItemList.size();i++)
		{
			Field fi = (Field)TaxMxItemList.get(i);
			String itemid=(String)fi.getName();
			String itemdesc=(String)fi.getLabel();
			if(!("a0100".equalsIgnoreCase(itemid) || "flag".equalsIgnoreCase(itemid)|| "Tax_max_id".equalsIgnoreCase(itemid)))
			{
				String typeDesc="字符";
				if(fi.getDatatype()==DataType.DATE)
				{
					typeDesc="日期";
				}
				else if(fi.getDatatype()==DataType.INT||fi.getDatatype()==DataType.FLOAT)
				{
					typeDesc="数值";
				}
				list.add(new CommonData(itemid,itemid+"       "+itemdesc+"       ( "+typeDesc+" )"));
			}		
		}
		return list;
	}
	public ArrayList getTaxMxField()
	{
		ArrayList retlist = new ArrayList();
		ArrayList CommonItemList = this.searchCommonItemList();
		ArrayList DynaItemList = this.searchDynaItemList();
		for(int i=0;i<CommonItemList.size();i++)
		{
			Field fi = (Field)CommonItemList.get(i);
			if(!("Tax_max_id".equalsIgnoreCase(fi.getName()))|| "a0100".equalsIgnoreCase(fi.getName()))
			{
				retlist.add(fi);
			}
		}
		for(int t=0;t<DynaItemList.size();t++)
		{
			Field fi = (Field)DynaItemList.get(t);
			retlist.add(fi);
		}
		return retlist;
	}
	
	/**
	 * 取得薪资类别项目的数据类型 map
	 * @return
	 */
	public HashMap getTaxMxItemMap()
	{
		HashMap map=new HashMap();
		try
		{
			ArrayList list=this.getTaxMxField();
			for(int i=0;i<list.size();i++)
			{
				Field fi=(Field)list.get(i);
				map.put((String)fi.getName(),fi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得薪资类别项目的数据类型 map
	 * @return
	 */
	public HashMap getCompareTaxMxMap()
	{
		HashMap map=new HashMap();
		try
		{
			ArrayList list=this.getTaxMxField();
			for(int i=0;i<list.size();i++)
			{
				Field fi=(Field)list.get(i);
				map.put((String)fi.getLabel(),fi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获得要导出的字段
	 * @param oppositeItem
	 */
	public String[] getImportField(String[] oppositeItem)
	{
		String[] importfields = new String[oppositeItem.length];
		for(int i=0;i<oppositeItem.length;i++)
		{
			String[] temps = oppositeItem[i].split("=");
			importfields[i] = temps[0];
		}
		return importfields;
	}
	/**
	 * 获取ItemId
	 * @param itemdesc
	 * @param dao
	 * @return
	 */
	public String getItemId(String itemdesc,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sql = new StringBuffer();
		sql.append(" select itemid,fieldsetid from fielditem");
		sql.append(" where itemdesc like '"+itemdesc+"'");
		String retstr = "";
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				retstr = rs.getString("fieldsetid");
				if("a01".equalsIgnoreCase(retstr))
				{
					retstr = rs.getString("itemid");
				}else{
					retstr = "";
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	/**
	 * 取工资套对应的库前缀
	 * @param salaryid
	 * @param dao
	 * @return
	 */
	public String[] getDbPre(String salaryid,ContentDAO dao)
	{
		RowSet rs;
		String dbPreStr = "";
		String[] dbPre = null; 
		String sql = " select * from salarytemplate where salaryid="+salaryid;
		try
		{			
			rs =dao.search(sql);
			if(rs.next())
			{
				dbPreStr = rs.getString("cbase");
				dbPre = dbPreStr.split(",");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return dbPre;
	}
	/**
	 * 判断 值类型是否与 要求的类型一致
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	public boolean isDataType(String decwidth,String itemtype,String value)
	{
		boolean flag=true;
		if("N".equals(itemtype))
		{
			/*if(decwidth.equals("0"))
			{
				flag=value.matches("^[+-]?[\\d]+$");
			}
			else
			{*/
			    //=/^(-|\+)?\d+(\.\d+)?$/  
				flag=value.matches("/^-?\\d+(\\.\\d)?/");
			/*}*/
			
		}
		else if("D".equals(itemtype))
		{
			flag=value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	/**
	 * 获得维护字段的Id
	 * @return
	 */
	public String getDynaItemId()
	{
		String dynaItemid = "";
		Document doc = this.getDoc();
		Element el = this.getSingleNode(doc,"/param/items");
		if(el!=null)
		{
			dynaItemid = el.getText();
			if(!(dynaItemid==null || "".equals(dynaItemid)))
			{
				dynaItemid = ","+dynaItemid;
			}
		}
		return dynaItemid;
	}
	private  HashMap preMap;
	private  HashMap codeMap; 
	private  HashMap fixedFieldMap;
	public void initFixedFieldMap()
	{
		try
		{
			fixedFieldMap = new HashMap();
			ArrayList  getfieldlist = this.searchCommonItemList();
			for(int t=0;t<getfieldlist.size();t++)
			{
				Field field = (Field)getfieldlist.get(t);
				fixedFieldMap.put(field.getName().toUpperCase(), field);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**初始化所有代码型指标，以便导入代码值，而不是汉字*/
	public void initPreMap(String[] oppositeItem)
	{
		try
    	{
		   initFixedFieldMap();
	       String sql="select pre,dbname from dbname";
    	   ContentDAO dao = new ContentDAO(this.conn);
    	   RowSet rs = dao.search(sql);
    	   preMap = new HashMap();
    	   codeMap = new HashMap();
    	   while(rs.next())
    	   {
		      preMap.put(rs.getString("dbname"),rs.getString("pre")+rs.getString("dbname"));
    	   }
    	   String[] excelItem = new String[oppositeItem.length];
			String[] importItem =  new String[oppositeItem.length];
			for(int i=0;i<oppositeItem.length;i++)
			{
				String[] temp =  oppositeItem[i].split("=");
				excelItem[i] = temp[0];               
				importItem[i] = temp[1].toLowerCase();
			}
			// 取得要更新字段的数据类型
			StringBuffer sql_buf=new StringBuffer("");
			for(int i=0;i<oppositeItem.length;i++)
			{
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				if(fi!=null)
				{
					if("a".equalsIgnoreCase(fi.getItemtype())&&!"0".equalsIgnoreCase(fi.getCodesetid()))
					{
						sql_buf.append(",'"+fi.getCodesetid()+"'");
					}
				}else
				{ 
				    if(fixedFieldMap.get(importItem[i].toUpperCase())!=null)
				    {
				    	Field field = (Field)fixedFieldMap.get(importItem[i].toUpperCase());
				    	if(field.getDataType()==DataType.STRING&&!"0".equals(field.getCodesetid()))
				    	{
				    		sql_buf.append(",'"+field.getCodesetid()+"'");
				    	}
				    }
				}
			}
			if(sql_buf.toString().length()>0)
			{
	    		sql="select codeitemid,codeitemdesc from codeitem where codesetid in("+sql_buf.toString().substring(1)+")";
	    		rs=dao.search(sql);
	    		while(rs.next())
	    		{
	    			codeMap.put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
	    		}
			}
			
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void initOrg()
	{
		try
		{
			if(codeMap==null)
				codeMap=new HashMap();
			String sql = "select * from organization";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= dao.search(sql);
			while(rs.next())
			{
				codeMap.put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
			}
			codeMap.put("ISORG", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得人员数据
	 * @param itemid
	 * @param oppositeItem
	 * @param allImportDataMap
	 * @param dbnamestr
	 * @param where
	 * @param dao
	 * @return
	 */
	public int setPersonRecordVo(int tax_max_id,ArrayList addlist,String itemid[],String[] oppositeItem,LazyDynaBean dataBean,String dbnamestr,StringBuffer where,ContentDAO dao,String fromTable)
	{
		int max_id=tax_max_id;
		try
		{
			RowSet rs;			
			String fieldstr = "";
			String[] dbPre = dbnamestr.split(",");	
			int waipin = 0;
			int id=0;
			String temp = this.getDynaItemId();
			StringBuffer dynasb = new StringBuffer();
			if(!(temp==null || "".equals(temp)))
			{
				String[] dynatemp = temp.split(",");
				for(int i=0;i<dynatemp.length;i++)
				{
					FieldItem fi= DataDictionary.getFieldItem(dynatemp[i]);
					if(fi!=null && "a01".equalsIgnoreCase(fi.getFieldsetid()))
						dynasb.append(","+dynatemp[i]);
				}
			}		
			HashMap allreadyMap = new HashMap();
			if(dbPre!=null && dbPre.length>0)
			{
				// 循环各个人员库
				for(int j=0;j<dbPre.length;j++)
				{					
					StringBuffer sb = new StringBuffer();				
					fieldstr = "a0100,a0101,e0122,b0110"+dynasb.toString();				
					sb.append(" select "+fieldstr+","+itemid[0]);
					sb.append(" from "+dbPre[j]+"a01");
					sb.append(" where 1=1 "+where.toString());
					rs = dao.search(sb.toString());
					while(rs.next()){
						waipin++;
						RecordVo vo = new RecordVo(fromTable);
						String field[] = fieldstr.split(",");
						vo.setInt("tax_max_id",max_id);
						// 设置要各个人员字段
						for(int i=0;i<field.length;i++)
						{						
							FieldItem fi = DataDictionary.getFieldItem(field[i]);
							String itemtype = fi.getItemtype();
							if("A".equals(fi.getItemtype())){
								vo.setString(fi.getItemid(),rs.getString(field[i]));
							}else if("D".equals(fi.getItemtype())){
								if(rs.getDate(field[i])!=null)
					    			vo.setDate(fi.getItemid(),rs.getDate(field[i]));
							}else if("N".equals(fi.getItemtype())){
								if(fi.getDecimalwidth()==0){
									vo.setInt(fi.getItemid(),rs.getInt(field[i]));
								}else{
									vo.setDouble(fi.getItemid(),rs.getFloat(field[i]));
								}
							}
							allreadyMap.put(fi.getItemid().toUpperCase(), "1");
							
							
						}
						// 设置人员库标识字段
						vo.setString("nbase",dbPre[j]);
						allreadyMap.put("nbase".toUpperCase(), "1");
						this.setExcelDataRecordVo(oppositeItem, vo, dataBean,allreadyMap);
						/**使用标识默认为已使用*/
						vo.setString("flag", "1");
						addlist.add(vo);
						max_id++;
					}
				}// for loop end pre
				if(waipin == 0)
				{
					   
						RecordVo vo = new RecordVo(fromTable);
						vo.setInt("tax_max_id",max_id); 
						this.setOutOfA01RecordVo(itemid,oppositeItem,vo,dataBean);
						vo.setString("flag","1");
						addlist.add(vo);
						max_id++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return max_id;

	}
	/**
	 * 取得要导入的个税数据
	 * @param oppositeItem
	 * @param vo
	 * @param allImportDataMap
	 * @return
	 */
	public RecordVo setOutOfA01RecordVo(String[] itemid,String[] oppositeItem,RecordVo vo,LazyDynaBean dataBean)
	{

		try
		{
			String[] excelItem = new String[oppositeItem.length];
			String[] importItem =  new String[oppositeItem.length];
			for(int i=0;i<oppositeItem.length;i++)
			{
				String[] temp =  oppositeItem[i].split("=");
				excelItem[i] = temp[0];               
				importItem[i] = temp[1].toLowerCase();
			}
			// 取得要更新字段的数据类型
			for(int i=0;i<oppositeItem.length;i++)
			{
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
					if("nbase".equalsIgnoreCase(fi.getItemid()))
						codesetid="@@";
				}else
				{ 
					if(this.fixedFieldMap.get(importItem[i].toUpperCase())!=null)
					{
						Field field=(Field)this.fixedFieldMap.get(importItem[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				// 在Map中得到该记录对应的 动态Bean
				String tempValue = (String)dataBean.get(excelItem[i]); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeMap.get("ISORG")==null)
						    	this.initOrg();
							if(codeMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)codeMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							if(this.preMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)preMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if(codeMap.get(tempValue)!=null)
						{
							vo.setString(importItem[i],(String)codeMap.get(tempValue));
						}
						else
						{
							vo.setString(importItem[i],tempValue);
						}
					}
					else
		    			vo.setString(importItem[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
						/*if(isDataType(decwidth,itemtype,tempValue)||tempValue.equals("0"))
						{*/
							if("0".equals(decwidth))
							{  
								if(tempValue.indexOf(".")!=-1)
						   		   vo.setInt(importItem[i],Integer.parseInt(tempValue.substring(0,tempValue.indexOf("."))));
								else
								   vo.setInt(importItem[i],Integer.parseInt(tempValue));
							}
							else
								vo.setDouble(importItem[i],Double.parseDouble(tempValue));
						/*}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));*/
					}else
					{
						if("0".equals(decwidth))
							vo.setInt(importItem[i],0);
						else
							vo.setDouble(importItem[i],0);
					}
					
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(importItem[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));
					}else
					{
						//vo.setDate(importItem[i],"");
					}
					
				}
				
			}	
			
			for(int i=0;i<itemid.length;i++)
			{
				itemid[i]=itemid[i].toLowerCase();
				FieldItem fi = DataDictionary.getFieldItem(itemid[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
				}else
				{ 
					if(this.fixedFieldMap.get(itemid[i].toUpperCase())!=null)
					{
						Field field=(Field)this.fixedFieldMap.get(itemid[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				// 在Map中得到该记录对应的 动态Bean
				String itemvalue = DataDictionary.getFieldItem(itemid[i]).getItemdesc();
				String tempValue = (String)dataBean.get(itemvalue); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeMap.get("ISORG")==null)
						    	this.initOrg();
							if(codeMap.get(tempValue)!=null)
							{
								vo.setString(itemid[i],(String)codeMap.get(tempValue));
							}
							else
							{
								vo.setString(itemid[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							if(this.preMap.get(tempValue)!=null)
							{
								vo.setString(itemid[i],(String)preMap.get(tempValue));
							}
							else
							{
								vo.setString(itemid[i],tempValue);
							}
						}
						else if(codeMap.get(tempValue)!=null)
						{
							vo.setString(itemid[i],(String)codeMap.get(tempValue));
						}
						else
						{
							vo.setString(itemid[i],tempValue);
						}
					}
					else
		    			vo.setString(itemid[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
						/*if(isDataType(decwidth,itemtype,tempValue)||tempValue.equals("0"))
						{*/
							if("0".equals(decwidth))
							{
								if(tempValue.indexOf(".")!=-1)
							   		   vo.setInt(importItem[i],Integer.parseInt(tempValue.substring(0,tempValue.indexOf("."))));
									else
									   vo.setInt(importItem[i],Integer.parseInt(tempValue));
							}
							else
								vo.setDouble(itemid[i],Double.parseDouble(tempValue));
						//}
						/*else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+itemid[i]+")中数据:"+tempValue+" 不符合格式!"));
*/					}else
					{
						if("0".equals(decwidth))
							vo.setInt(itemid[i],0);
						else
							vo.setDouble(itemid[i],0);
					}
					
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(itemid[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+itemid[i]+")中数据:"+tempValue+" 不符合格式!"));
					}else
					{
						//vo.setDate(itemid[i],"");
					}
					
				}
				
			
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;
	
	}
	/**
	 * 取得要导入的个税数据
	 * @param oppositeItem
	 * @param vo
	 * @param allImportDataMap
	 * @return
	 */
	public RecordVo setExcelDataRecordVo(String[] oppositeItem,RecordVo vo,LazyDynaBean dataBean,HashMap allreadyMap)
	{
		try
		{
			String[] excelItem = new String[oppositeItem.length];
			String[] importItem =  new String[oppositeItem.length];
			for(int i=0;i<oppositeItem.length;i++)
			{
				String[] temp =  oppositeItem[i].split("=");
				excelItem[i] = temp[0];
				importItem[i] = temp[1].toLowerCase();
			}
			// 取得要更新字段的数据类型
			for(int i=0;i<oppositeItem.length;i++)
			{
				/**已经赋值的指标，不要重新在赋值*/
				if(allreadyMap.get(importItem[i].toUpperCase())!=null)
					continue;
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
				}else
				{ 
					if(this.fixedFieldMap.get(importItem[i].toUpperCase())!=null)
					{
						Field field=(Field)this.fixedFieldMap.get(importItem[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				String tempValue = (String)dataBean.get(excelItem[i]); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeMap.get("ISORG")==null)
						    	this.initOrg();
							if(codeMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)codeMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							if(this.preMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)preMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if(codeMap.get(tempValue)!=null)
						{
							vo.setString(importItem[i],(String)codeMap.get(tempValue));
						}
						else
						{
							vo.setString(importItem[i],tempValue);
						}
					}
					else
		    			vo.setString(importItem[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
						/*if(isDataType(decwidth,itemtype,tempValue)||tempValue.equals("0"))
						{*/
							if("0".equals(decwidth))
							{
								if(tempValue.indexOf(".")!=-1)
							   		   vo.setInt(importItem[i],Integer.parseInt(tempValue.substring(0,tempValue.indexOf("."))));
									else
									   vo.setInt(importItem[i],Integer.parseInt(tempValue));
							}
							else
								vo.setDouble(importItem[i],Double.parseDouble(tempValue));
						/*}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));
*/					}else
					{
						if("0".equals(decwidth))
							vo.setInt(importItem[i],0);
						else
							vo.setDouble(importItem[i],0);
					}
					
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(importItem[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));
					}else
					{
						//vo.setDate(importItem[i],"");
					}
					
				}
			
			}	

		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 取得最大的主键值
	 * @param dao
	 * @return
	 */
	public int getTaxMaxId(ContentDAO dao)
	{
		int tax_max_id=1;
		try
		{
			RowSet rs;
			String sql = "select max(tax_max_id) as tax_max_id from gz_tax_mx ";
			rs = dao.search(sql);
			if(rs.next())
			{
				tax_max_id = rs.getInt("tax_max_id");
				if(tax_max_id==0)
					tax_max_id=1;
				else
					tax_max_id=tax_max_id+1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tax_max_id;
	}
	
	
	/**
	 * 取得最大的主键值
	 * @param dao
	 * @return
	 */
	public int getTaxMaxId2(ContentDAO dao,String fromTable)
	{
		int tax_max_id=1;
		try
		{
			RowSet rs;
			String sql = "select max(tax_max_id) as tax_max_id from "+fromTable+" ";
			rs = dao.search(sql);
			if(rs.next())
			{
				tax_max_id = rs.getInt("tax_max_id");
				if(tax_max_id==0)
					tax_max_id=1;
				else
					tax_max_id=tax_max_id+1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tax_max_id;
	}
	
	
	/**
	 * 取得所有要导入的数据
	 * @param oppositeItem 指标对应（姓名=a0101）
	 * @param allImportDataMap
	 * @param allImportDataList bean（汉字，值）
	 * @param nbaseItem 人员标识
	 * @param dbnamestr 人员库前缀串
	 * @param dao
	 */
	public void iteratExcelData(String[] oppositeItem,HashMap allImportDataMap,ArrayList allImportDataList,String[] nbaseItem,String dbnamestr,ContentDAO dao,String fromTable)
	{
		try
		{		
			this.initPreMap(oppositeItem);
			String[] itemid = new String[nbaseItem.length];
			String[] itemtype = new String[nbaseItem.length];
			String[] decwidth = new String[nbaseItem.length];	
			String[] codeset=new String[nbaseItem.length];
			int tax_max_id=this.getTaxMaxId2(dao,fromTable);
			/**人员标识字段*/
			for(int i=0;i<nbaseItem.length;i++){
				String itemdesc = nbaseItem[i];
				String getitemid = this.getItemId(itemdesc,dao);
				FieldItem fi = DataDictionary.getFieldItem(getitemid);				
				itemid[i]=getitemid;
				itemtype[i]=fi.getItemtype();
				decwidth[i] = fi.getDecimalwidth()+"";
				codeset[i]=fi.getCodesetid();
			}
			ArrayList addlist = new ArrayList();
			ArrayList deleteList = new ArrayList();
			int allnums = allImportDataList.size();
			int iteratNum = allnums/30+1;	
				StringBuffer where = new StringBuffer();
				int initNum = 0;
				/**所有记录bean*/
				for(int t=0;t</*=30&&(i*30+t)<*/allImportDataList.size();t++)
				{
					StringBuffer whereTemp = new StringBuffer();
					LazyDynaBean abean = (LazyDynaBean)allImportDataList.get(t);
					for(int x=0;x<nbaseItem.length;x++)
					{
						// 条件对应的值，如A0177=430651196277324468
						if("A".equals(itemtype[x])){
							if("0".equals(codeset[x]))
						    	whereTemp.append(" and "+itemid[x]+"='"+((String)abean.get(nbaseItem[x].trim())).trim()+"'");
							else
							{
								if(codeSetMap==null||codeSetMap.get(codeset[x].toUpperCase())==null)
								{
									this.getCodeitemIdByCodeSetID(codeset[x]);
								}
								whereTemp.append(" and "+itemid[x]+"='"+(codeItemidMap.get(((String)abean.get(nbaseItem[x].trim())))==null?"":codeItemidMap.get(((String)abean.get(nbaseItem[x].trim()))))+"'");
							}
						}
						if("N".equals(itemtype[x])){
							String tempValue=((String)abean.get(nbaseItem[x].trim())).trim();
							//if(isDataType(decwidth[x],itemtype[x],tempValue)){
								whereTemp.append(" and "+itemid[x]+"="+tempValue);
							//}
							//else
							//	throw GeneralExceptionHandler.Handle(new Exception("源数据("+itemid[x]+")中数据:"+tempValue+" 不符合格式!"));
							
						}

					}
					tax_max_id=this.setPersonRecordVo(tax_max_id,addlist,itemid,oppositeItem,abean, dbnamestr, whereTemp, dao,fromTable);
				}
			dao.addValueObject(addlist);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	private HashMap codeItemidMap=null;
	private HashMap codeSetMap=null;
	public void getCodeitemIdByCodeSetID(String codesetid)
	{
		try
		{
			if(codeItemidMap==null)
			{
				codeItemidMap=new HashMap();
				codeSetMap = new HashMap();
			}
			codeSetMap.put(codesetid.toUpperCase(), "1");
			StringBuffer sql = new StringBuffer("");
			if("UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
			{
				sql.append("select codeitemid,codeitemdesc from organization where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
			}
			else
			{
				sql.append("select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				String codeitemid=rs.getString("codeitemid");
				String codeitemdesc=rs.getString("codeitemdesc");
				codeItemidMap.put(codeitemdesc.toUpperCase(),codeitemid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param nbaseItem 人员标识
	 * @param oppositeItem 对应指标(姓名=a0101)
	 * @param form_file
	 * @param uv
	 * @param dao
	 * @return
	 * @throws GeneralException
	 */
	public int importFileDataToTaxMx(String[] nbaseItem,String[] oppositeItem,FormFile form_file,UserView uv,ContentDAO dao ,String fromTable)throws GeneralException
	{
		int ret=0;
		InputStream _in = null;
		try
		{
			RowSet rs;
			String usrname = uv.getUserName();
			// 循环各个人员库
			StringBuffer dbname= new StringBuffer();
			String sql = "select pre from dbname";
			rs = dao.search(sql);
			while(rs.next())
			{
				dbname.append(","+rs.getString("pre"));
			}		
			TaxMxExcelBo tmbo=new TaxMxExcelBo(this.conn);
			/**得到sheet(标题行)和workbook*/
			_in = form_file.getInputStream();
			tmbo.getSelfAttribute(_in);
//			int rowNums=tmbo.getTotalDataRows();   //数据总行数
			int rowNums=tmbo.getTotalDataRows2();   //数据总行数
			ret=rowNums;
			int pageNum=rowNums/100+1;
			/** 要导出的字段，汉字列标题(姓名，税率)*/
			String[] importfields = this.getImportField(oppositeItem);
			// 要导入的所有Excel数据
			ArrayList allImportDataList = new ArrayList();
			/**excel文件所有列commondata(汉字列名，汉字列名)*/
			ArrayList excelFiledList = this.getExcelFiledList(form_file);
			/**allImportDataList中封装的bean(汉字列名，对应值)*/
			/**allImportDataMap （人员标识列值，对应bean）*/
			HashMap allImportDataMap = tmbo.getImportData(rowNums,excelFiledList,nbaseItem,allImportDataList);
			
			this.iteratExcelData(oppositeItem, allImportDataMap, allImportDataList,nbaseItem, dbname.substring(1).toString(), dao,fromTable);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(_in);
		}
		return ret;
		
	}
	public HashMap getFieldMap (ArrayList list)
	{
		HashMap ret = new HashMap();
		for(int i=0;i<this.fieldlist.size();i++)
		{
			Field fi = (Field)this.fieldlist.get(i);			
			ret.put(fi.getLabel(),fi);
		}
		
		return ret;
	}
	public String getDbSQL(ArrayList pre,UserView view) {
		StringBuffer dbSql = new StringBuffer();
		try 
		{
			if(pre==null||pre.size()==0)
			{
				dbSql.append(" 1=2 ");
				return dbSql.toString();
			}
    		for (int i = 0; i < pre.size(); i++) {
    	    	String temp=(String)pre.get(i);
    			if (i == 0) {
    				dbSql.append("(");
	    		}
    			/**加入高级授权*/
    		    StringBuffer sql = new StringBuffer("");
    			String priStrSql = InfoUtils.getWhereINSql(view, temp);
    			sql.append("select "+temp+"a01.A0100 ");
    			if (priStrSql.length() > 0)
    				sql.append(priStrSql);
    			else
    				sql.append(" from "+temp+"a01");
    					
	    		dbSql.append("(upper(nbase)='");
	    		dbSql.append(temp.toUpperCase());
	    		dbSql.append("' and a0100 in ("+sql.toString()+"))");
	    		if (i != pre.size() - 1) {
		    		dbSql.append(" OR ");
	    		} else
	     			dbSql.append(")");
    		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbSql.toString();
	}
	public String getSumSql()
	{
		String sum="";
		try
		{
			String deptid=this.getDeptID();
			if("true".equalsIgnoreCase(deptid)&&!(view.isSuper_admin()|| "1".equals(view.getGroupId()))&&view.getUnitIdByBusi("3")!=null&&!"".equals(view.getUnitIdByBusi("3"))&&!"UN".equalsIgnoreCase(view.getUnitIdByBusi("3")))
			{
				String nunit=view.getUnitIdByBusi("3");
				String unitarr[] =nunit.split("`");
				StringBuffer pre=new StringBuffer("");
				for(int i=0;i<unitarr.length;i++)
				{
    				String codeid=unitarr[i];
    				if(codeid==null|| "".equals(codeid))
    					continue;
	    			if(codeid!=null&&codeid.trim().length()>2)
    				{
	    				/* 所得税管理/文件/导出申报明细表，导出的excel表中“已纳税额”数据不对 xiaoyun 2014-10-11 start */
                 		//pre.append(" or deptid = '"+codeid.substring(2)+"' ");
	    				pre.append(" or (deptid = '"+codeid.substring(2)+"' or e0122='"+codeid.substring(2)+"')");
                 		/* 所得税管理/文件/导出申报明细表，导出的excel表中“已纳税额”数据不对 xiaoyun 2014-10-11 end */
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
				sum=" sum(case when("+pre+") then sds else 0 end ) as Mynse";
				
			}
			else
			{
				sum=" sum(sds) as Mynse";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sum;
	}
	/**
	 * 取得权限过滤语句
	 * @param filterByMdule 是否按模块权限来，如果按模块权限来的话，则采用不级联的方式
	 * @return
	 * @see #hasModulePriv()
	 */
	public String getPrivPre(String filterByMdule)
	{
		StringBuffer pre=new StringBuffer("");
		try
		{
			ArrayList list = view.getPrivDbList();
			/**是否按隶属部门来控制*/
			String deptid=this.getDeptID();
			if(list==null||list.size()<=0)
			{
				pre.append("1=2");
			}
			else
			{
				if("false".equalsIgnoreCase(deptid))
				{
					/**不按模块权限*/
					if("0".equals(filterByMdule))
					{
			        	for(int i=0;i<list.size();i++)
		    	    	{
		    	    		String nbase=(String)list.get(i);
			        		if (i == 0) {
			        			pre.append("(");
    		        		}
			        		/**加入高级授权*/
		            		StringBuffer sql = new StringBuffer("");
				        	String priStrSql = InfoUtils.getWhereINSql(view, nbase);
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
					/**按模块权限*/
					else
					{
						/*if(view.isSuper_admin()||view.getGroupId().equals("1"))
						{
							pre.append(" 1=1" );
						}
						else*/
						{
							String nunit=view.getUnitIdByBusi("3");
			    			if(nunit==null|| "".equals(nunit))
		    				{
		    					pre.append(" 1=2 ");
			    			}
			    			else
			    			{
			    				String unitarr[] =nunit.split("`");
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
						}
					}
				}
				else
				{
					/*if(view.isSuper_admin()||view.getGroupId().equals("1"))
					{
						pre.append(" 1=1 ");
					}
					else */if("0".equals(filterByMdule))
					{
						String code=view.getManagePrivCode();
						String value=view.getManagePrivCodeValue();
						if(code==null|| "".equals(code))
							pre.append(" 1=2 ");
						else 
						{
			            	for(int i=0;i<list.size();i++)
		    	        	{
		    	        		String nbase=(String)list.get(i);
		    	        		if(i == 0) {
				        			pre.append("(");
	    		        		}
		    	        		
		    	        		if("UN".equalsIgnoreCase(code))
		    	        		{
		    	        			pre.append(" (case when deptid is not null then deptid else  b0110  end like '"+value+"%'");
		    	        		}
		    	        		else
		    	        		{
		    	        			pre.append(" (case when deptid is not null then deptid else  e0122  end like '"+value+"%'");
		    	        		}
		    	        		
		    	        	//	pre.append(" (deptid like '"+value+"%'");
		    	        		if(value==null|| "".equals(value))
		    	        		{
		    	        			pre.append(" or deptid is null");
		    	        		}
		    	        		pre.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"')");
		    	        		if (i != list.size() - 1) {
		    		        		pre.append(" OR ");
	    			        	} else
	     			        		pre.append(")");
		    	        	}
		    	    	}
			    	}
					else
					{
			    		String nunit=view.getUnitIdByBusi("3");
		    			if(nunit==null|| "".equals(nunit))
	    				{
	    					pre.append(" 1=2 ");
		    			}
		    			else
		    			{
		    				String unitarr[] =nunit.split("`");
		    				for(int i=0;i<unitarr.length;i++)
		    				{
			    				String codeid=unitarr[i];
			    				if(codeid==null|| "".equals(codeid))
			    					continue;
				    			if(codeid!=null&&codeid.trim().length()>2)
			    				{
				    				
				    				String code=codeid.substring(0,2);
				    				if("UN".equalsIgnoreCase(code))
			    	        		{
			    	        			pre.append(" or  case when deptid is not null then deptid else  b0110  end = '"+codeid.substring(2)+"'");
			    	        		}
			    	        		else
			    	        		{
			    	        			pre.append(" or  case when deptid is not null then deptid else  e0122  end = '"+codeid.substring(2)+"'");
			    	        		}
				    				
				    				
			                 	//	pre.append(" or deptid = '"+codeid.substring(2)+"' ");
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
	 * 
	 * @return true 按业务范围 false 按人员范围
	 */
	public boolean hasModulePriv()
	{
		boolean flag=false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet =null;
		try
		{
			if(this.view.getStatus()==4)
			{
				rowSet =dao.search("select busi_org_dept from t_sys_function_priv where UPPER(id)='"+this.view.getDbname().toUpperCase()+this.view.getA0100().toUpperCase()+"'");
			}else{
				rowSet =dao.search("select busi_org_dept,org_dept from operuser where UPPER(username)='"+this.view.getUserName().toUpperCase()+"'");
			}
			while(rowSet.next())
			{
				String str=rowSet.getString(1);
				if(str!=null&&str.trim().length()>0)
				{
					flag=true;
					break;
				}else if(this.view.getStatus()!=4)
				{
					str=rowSet.getString(2);
					if(str!=null&&str.trim().length()>0)
					{
						flag=true;
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rowSet!=null)
				PubFunc.closeResource(rowSet);
		}
		return flag;
	}
}
