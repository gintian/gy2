package com.hjsj.hrms.businessobject.gz;

import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.ResourceFactory;

import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class TaxMxFixField {
	
	/**
	 * 取得个税明细表的固定字段列表
	 * @return
	 */
	public ArrayList searchCommonItemList() {
		ArrayList templist=new ArrayList();
		
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
		field.setReadonly(true);		
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
		field.setVisible(true);			
		templist.add(field);

		field=new Field("ynse",ResourceFactory.getProperty("gz.columns.ynse"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		templist.add(field);
		
		field=new Field("Sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);			
		field.setDecimalDigits(2);
		templist.add(field);
		
		field=new Field("Basedata",ResourceFactory.getProperty("gz.columns.basedata"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		templist.add(field);
		
		field=new Field("Sl",ResourceFactory.getProperty("gz.columns.sl"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());		
		field.setVisible(true);	
		field.setDecimalDigits(2);
		templist.add(field);

		field=new Field("Sds",ResourceFactory.getProperty("gz.columns.sds"));
		field.setDatatype(DataType.FLOAT);						
		format.setLength(2);
		field.setFormat("####."+format.toString());
		field.setDecimalDigits(2);
		field.setVisible(true);			
		templist.add(field);			
		
		field=new Field("Description",ResourceFactory.getProperty("gz.columns.desc"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		field.setVisible(true);
		templist.add(field);
		return templist;
	}
	

}
