package com.hjsj.hrms.utils.components.complexcondition.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DefineConditionBo 
 * 类描述：条件定义Bo类
 * 创建人：sunming
 * 创建时间：2015-7-21
 * @version
 */
public class ComplexConditionBo {
	private Connection conn = null;
	private UserView userview;
	
	public ComplexConditionBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}
	
	/**
	 * @author lis
	 * @Description: 得到子集数据
	 * @date 2015-11-10
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<CommonData> getfieldSetList(String inforKindFlag) throws GeneralException
	{
		ArrayList<CommonData> list=new ArrayList<CommonData>();
		RowSet frowset = null;

		String [] kindList=new String[]{};
		if(StringUtils.isNotBlank(inforKindFlag))
			kindList=inforKindFlag.toUpperCase().split(",");
		if(kindList.length==0){
			kindList=new String[]{"A%","B%","K%"};
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strSql=new StringBuffer();
			strSql.append("select fieldsetid,customdesc from fieldset where useflag='1' ");

			strSql.append(" and (");
			for (String str : kindList) {
				strSql.append(" upper(fieldsetid) like ? or");
			}
			strSql.delete(strSql.length()-3,strSql.length());
			strSql.append(" ) order by fieldSetId,displayorder");

			frowset=dao.search(strSql.toString(), Arrays.asList(kindList));
			while(frowset.next())
			{
				list.add(new CommonData(frowset.getString(1),frowset.getString(1)+":"+frowset.getString(2)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 得到指标数据
	 * @date 2015-11-10
	 * @param itemSet 子集id
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<CommonData> getfieldItemList(String itemSet) throws GeneralException{
		ArrayList<CommonData> list = new ArrayList<CommonData>();
		try {
			//直接查表没有单位名称等内置字段，此处改为用DataDictionary获取子集指标列表 wangbs 2019年9月25日
			List fieldList = DataDictionary.getFieldList(itemSet, Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem fieldItem = (FieldItem) fieldList.get(i);
				String itemId = fieldItem.getItemid().toUpperCase();
				String itemDesc = fieldItem.getItemdesc();
				list.add(new CommonData(itemId,itemId + ":" + itemDesc));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * @author lis
	 * @Description: 得到代码
	 * @date 2015-11-10
	 * @param itemid 指标id
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<CommonData> getcodeItemList(String itemid) throws GeneralException
	{
		ArrayList<CommonData> list=new ArrayList<CommonData>();
		RowSet frowset = null;
		try
		{
			//list.add(new CommonData("", ""));
			ContentDAO dao=new ContentDAO(this.conn);
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			String codesetid=item.getCodesetid();
			if(!"0".equals(codesetid))
			{
				String sql="";
				if("UN".equals(codesetid)|| "UM".equals(codesetid)|| "@K".equals(codesetid))
				{
					sql="select codeitemid,codeitemdesc from organization where (codesetid='"+codesetid+"'";
				}
				else
				{
					sql="select codeitemid,codeitemdesc from codeitem where (codesetid='"+codesetid+"'";
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				if("UM".equals(codesetid)){//支持关联部门的指标也可以选择单位
					sql+= " or codesetid ='UN'";
				}
				sql+=") and " + Sql_switcher.dateValue(backdate)
     			+ " between start_date and end_date";
				if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY a0000,codeitemid ");
				}else if(!"@@".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY codeitemid ");
				}
				frowset=dao.search(sql);
				while(frowset.next())
				{
					list.add(new CommonData(frowset.getString(1),frowset.getString(1)+":"+frowset.getString(2)));
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return list;
	}
	/**
	 * 设置过滤条件时获取人员视图指标list
	 * @author wangbs
	 * @param itemSet
	 * @return ArrayList
	 * @throws
	 */
	private ArrayList getPsnViewFieldList(String itemSet){
		ArrayList psnFieldList = new ArrayList();
		List fieldList = DataDictionary.getFieldList(itemSet, Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem fieldItem = (FieldItem) fieldList.get(i);
			String itemId = fieldItem.getItemid().toUpperCase();
			String itemDesc = fieldItem.getItemdesc();
			psnFieldList.add(new CommonData(itemId,itemId + ":" + itemDesc));
		}
		return psnFieldList;
	}
}
