package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:业务字典构建库表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 27, 2009:5:08:18 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SubSetCreateSaveTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");//子集名称
		ArrayList fielditemlist = (ArrayList)this.getFormHM().get("code_fields");//所选需要构造的指标
		String id = (String)this.getFormHM().get("id");//主id
		ArrayList recordlist=new ArrayList();
		
		try{
			recordlist = updateDictionary(dao,fielditemlist,fieldsetid,recordlist);//把字典表中的useflag字段变为1
			updateSubsetUsefalg(dao,fieldsetid,id);
			updatecreate(dao,recordlist,fieldsetid);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/*
	 * 指标 useflag=1 标示已构库
	 */
	 private ArrayList updateDictionary(ContentDAO dao,ArrayList recordlists,String fieldsetid,ArrayList recordlist) throws GeneralException, SQLException
	 {
//		ArrayList recordlists = new ArrayList();
		for(int i=0;i<recordlists.size();i++)
		{
			String itemid = (String) recordlists.get(i);
			RecordVo busiFieldVo=new RecordVo("t_hr_busiField");
			busiFieldVo.setString("fieldsetid",fieldsetid);
			busiFieldVo.setString("itemid",itemid);
			busiFieldVo=dao.findByPrimaryKey(busiFieldVo); //把这个指标中所有的字段都查询出来放到vo里
			busiFieldVo.setString("useflag","1");
			dao.updateValueObject(busiFieldVo);
			recordlist.add(busiFieldVo);
		}
		return recordlist;
	 }
	 /*
	  *子集 userflag=1  
	  */
	 private void updateSubsetUsefalg(ContentDAO dao,String fieldsetid,String id)
	 {
		try{
			String sql = "UPDATE t_hr_busitable set useflag='1' where id='"+id+"' and fieldsetid='"+fieldsetid+"'";
			dao.update(sql);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	 }
	 /*
	  * 构建库表
	  */
	 private void updatecreate(ContentDAO dao,ArrayList recordlist,String fieldsetid) throws GeneralException
	 {
		 ArrayList fieldlist=new ArrayList();
		 TableOperateBo tob=new TableOperateBo(this.getFrameconn());
		 String tablename="";
		 for(int i=0;i<recordlist.size();i++)
		 {
			 RecordVo busiVo=(RecordVo) recordlist.get(i);
			 tablename=busiVo.getString("fieldsetid");
			 boolean flag=false;
			 if("1".equals(busiVo.getString("keyflag")))
			 {
					flag=true;
			 }
			 Field temf= getField(flag,busiVo.getString("itemid"),busiVo.getString("itemdesc"),busiVo.getString("itemtype"),busiVo.getInt("itemlength"),busiVo.getInt("decimalwidth"));
			 fieldlist.add(temf);
			 
		 }
		 tob.create_sav_Table(tablename,fieldlist,true);
		 DataDictionary.refresh(); //把数据刷新到内存里
	 }
	 /**
		 * 
		 * @param primaryKey	是否是主键
		 * @param fieldname     列名
		 * @param fieldDesc     列描述
		 * @param type          数据类型
		 * @param length        长度
		 * @param decimalLength 小数点位数
		 * setNullable 是否为空，就是能否写入空值 主键为false
		 * @return
		 */
	 public Field getField(boolean primaryKey,String fieldname,String fieldDesc,String type,int length,int decimalLength)
	 {
		 Field obj=new Field(fieldname,fieldDesc);
		 if("A".equals(type))
			{	
				obj.setDatatype(DataType.STRING);  //类型
				obj.setKeyable(primaryKey);	  //主键
				if(primaryKey)
					obj.setNullable(false);  //是否为空，就是能否写入空值
				else 
					obj.setNullable(true);
				obj.setVisible(true);   //是否可见
				obj.setLength(length); //长度
				
			}
			else if("M".equals(type))
			{
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(primaryKey);	  //主键	
				if(primaryKey)
					obj.setNullable(false);  //是否为空，就是能否写入空值
				else 
					obj.setNullable(true);
				obj.setVisible(true);
				obj.setAlign("left");
				obj.setLength(16);
			}
			else if("D".equals(type))
			{
				
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(primaryKey);	  //主键	
				if(primaryKey)
					obj.setNullable(false);  //是否为空，就是能否写入空值
				else 
					obj.setNullable(true);		
				obj.setVisible(true);
				obj.setLength(8);
			}	
			else if("N".equals(type))
			{
				if(decimalLength!=0&&decimalLength>0){
					obj.setDatatype(DataType.FLOAT);
					obj.setDecimalDigits(decimalLength);
					obj.setLength(length);							
					obj.setKeyable(primaryKey);		
					if(primaryKey)
						obj.setNullable(false);
					else 
						obj.setNullable(true);
					obj.setVisible(true);
				}else{
					obj.setDatatype(DataType.INT);
					obj.setDecimalDigits(decimalLength);
					obj.setLength(length);							
					obj.setKeyable(primaryKey);		
					if(primaryKey)
						obj.setNullable(false);
					else 
						obj.setNullable(true);
					obj.setVisible(true);
				}
												
			}	
//			else if(type.equals("I"))
//			{		
//				obj.setDatatype(DataType.INT);
//				obj.setKeyable(primaryKey);		
//				if(primaryKey)
//					obj.setNullable(false);
//				else 
//					obj.setNullable(true);
//				obj.setVisible(true);	
//			}
		 return obj;
	 }
	 
}
