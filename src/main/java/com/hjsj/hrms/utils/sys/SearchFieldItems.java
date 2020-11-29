package com.hjsj.hrms.utils.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.fielditemmultiselector.businessobject.GetFieldItemBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SearchFieldItems {
	//代码类
	String fieldItemId = "";
	//搜索关键字
	String fieldItemName = "";
	Boolean itemflag = false;
	
	String dictionary = "";//业务字典参数
	String dictionarytable = "";//业务字典表参数
	UserView userView = null;
	String isCheckBox = "";
	
	String nodeid = "";
	String entityFn = "";
	
	Connection conn = null;
	RowSet rowset = null;
	ContentDAO dao = null;
	private String filterItems = "";
	public SearchFieldItems(String fieldItemId,String fieldItemName,UserView userView,String nodeid,String entityFn,String isCheckBox)
	{
		this.fieldItemId = fieldItemId;
		this.fieldItemName = fieldItemName;
		this.userView = userView;
		if(nodeid.indexOf("_item")>0)
		{			
			this.nodeid = nodeid.split("_item")[0];
			this.itemflag = true;
		}else{
			this.nodeid = nodeid;
		}
		if(fieldItemId.indexOf(":")>0){
			this.dictionarytable = fieldItemId.split(":")[1];
			this.dictionary = "Y";
		}
		this.entityFn = entityFn;
		this.isCheckBox = isCheckBox;
	}
	
	/**
	 * 执行搜索
	 * @return
	 */
	public ArrayList  executeFieldItemSearch() throws Exception{
		
		ArrayList resultList = new ArrayList();
		
		try{
			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);
			//当传入标志为空时不显示列表
			if(this.fieldItemId == null||this.fieldItemId.trim().length()==0)
			{				
				return resultList;
			}else if(this.fieldItemId.split("`").length==1&&(this.fieldItemName==null||this.fieldItemName.trim().length()==0))
			{
				//传入单个标志时，直接查询当前主集下所有子集表
				if(this.nodeid!=null&&this.nodeid.trim().length()>1&&!"root".equals(this.nodeid))
				{
					//当前节点为根节点时查询指标
					resultList = searchChildrenItems();
				}else{
					resultList = onlyFieldItems();					
				}
			}else if(this.fieldItemId.split("`").length>1&&(this.fieldItemName==null||this.fieldItemName.trim().length()==0))
			{
				//传入多个标志时，进行分类查询
				if(this.nodeid!=null&&this.nodeid.trim().length()>1&&!"root".equals(this.nodeid))
				{
					resultList = searchChildrenItems();
				}else if(this.nodeid!=null&&this.nodeid.trim().length()==1)
				{
					resultList = onlyFieldItems();		
				}else{
					resultList = manyFieldItems();
				}
			}else if(this.fieldItemName!=null&&this.fieldItemName.trim().length()>0)
			{
				//文本框输入进行模糊查询
				resultList = searchNameItems();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rowset);
		}
		return resultList;
	}
	/***
	 * 过滤子集表
	* @Title:notFilterItems
	* @Description：
	* @author xiexd
	* @return
	* @throws Exception
	 */
	private String filterTable() throws Exception
	{
		String notfilter = "";
		ContentDAO dao = new ContentDAO(conn);
		try {
			StringBuffer sql = new StringBuffer("select fieldsetid from fielditem");
			if(!"".equals(this.filterItems))
			{
				sql.append(" where itemid not in(");
				for(int i=0;i<this.filterItems.split(",").length;i++)
				{
					sql.append("'"+this.filterItems.split(",")[i]+"',");
				}
				sql.setLength(sql.length()-1);
				sql.append(") ");
			}
			sql.append(" group by fieldsetid ");
			RowSet rs = dao.search(sql.toString());
			while (rs.next()) {
				notfilter+=rs.getString("fieldsetid")+" ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notfilter;
	}
	/***
	 * 过滤单表中的指标
	* @Title:isFilterItems
	* @Description：
	* @author xiexd
	* @param itemId
	* @return
	 */
	private Boolean isFilterItems(String itemId)
	{
		Boolean flag = false;
		//OKR需要排除的指标项：`create_fullname`create_time`create_user`p0700`p0800`p0807`p0809`p0811`p0813`p0815`p0831`p0833`p0841  20151109  chet
		String filterItem = "z0301`z0381`create_fullname`create_time`create_user`p0700`p0800`p0807`p0809`p0811`p0813`p0815`p0831`p0833`p0841";
		//System.out.println(filterItem.toLowerCase().indexOf(itemId.toLowerCase())+"--------"+itemId);
		itemId = itemId.toUpperCase();
		if(filterItem.toLowerCase().indexOf(itemId.toLowerCase())!=-1)
		{
			flag = true;
		}
		return flag;
	}
	/**
	 * 当传入的查询指标只有一个时查询当前主集下所有子集表
	 * @return
	 * @throws Exception
	 */
	private ArrayList onlyFieldItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		HashMap<String, String> fieldHm = null;
		if("recruit".equals(this.entityFn)) {
            GetFieldItemBo bo = new GetFieldItemBo(this.conn);
            fieldHm = bo.getZpFieldSetId();
        }
		
		if(this.dictionary.trim().length()>0&&"Y".equalsIgnoreCase(this.dictionary)&&!"A".equalsIgnoreCase(this.nodeid)
				&&!"B".equalsIgnoreCase(this.nodeid)&&!"K".equalsIgnoreCase(this.nodeid))
		{
		        
			StringBuffer dictionarySql = new StringBuffer();
			dictionarySql.append("select FieldSetId,itemid,itemdesc,itemtype from t_hr_busifield where (");
			for(int i=0;i<this.dictionarytable.split("&").length;i++)
			{				
				dictionarySql.append(" FieldSetId = '"+this.dictionarytable.split("&")[i]+"' or ");
			}
			dictionarySql.setLength(dictionarySql.length()-3);
			dictionarySql.append(") and useflag=1 order by displayid ");
			this.rowset = dao.search(dictionarySql.toString());
			while(this.rowset.next()) {
				if(this.isFilterItems(rowset.getString("itemid")))
					continue;
				
				HashMap hm = new HashMap();
   				hm.put("id", rowset.getString("itemid"));
   				hm.put("text", rowset.getString("itemdesc"));
   				hm.put("fieldItemId", rowset.getString("itemid"));
   				hm.put("fieldItemType", rowset.getString("itemtype"));
   				hm.put("fieldSetId", rowset.getString("FieldSetId"));
   				hm.put("leaf",Boolean.TRUE);
   				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
   					hm.put("checked", Boolean.FALSE);
    				
				itemList.add(hm);
			}
			this.rowset=null;
		}else{
			String notfilter =(String) this.filterTable();
			StringBuffer sql = new StringBuffer();
			String str = "";
			if(this.nodeid!=null&&this.nodeid.trim().length()==1)
				str = this.nodeid;
			else
				str = this.fieldItemId;
			
			sql.append("select fieldSetId,fieldSetDesc,customdesc from fieldSet where UseFlag=1 and fieldSetId like '"+str+"%' order by Displayorder");
			this.rowset = dao.search(sql.toString());
			while(this.rowset.next()) {
                String itemId = rowset.getString("fieldSetId");
                if (notfilter.indexOf(itemId.toUpperCase()) == -1)
                    continue;

                if (fieldHm != null && !fieldHm.containsKey(itemId))
                    continue;

                HashMap hm = new HashMap();
                hm.put("id", rowset.getString("fieldSetId"));
                hm.put("text", rowset.getString("customdesc"));
                hm.put("fieldItemId", rowset.getString("fieldSetId"));
                // hm.put("icon","/images/pos_l.gif");
                hm.put("leaf", Boolean.FALSE);

                itemList.add(hm);
			}
			this.rowset=null;
			ArrayList list = new ArrayList();
			//定制显示招聘
			if("recruit".equals(this.entityFn)&&"root".equalsIgnoreCase(this.nodeid))
			{	
				list = getRecruitItems();
			}else if("performance".equalsIgnoreCase(this.entityFn)&&"root".equalsIgnoreCase(this.nodeid)) {
				list = getPerformance();
			}
			for(int i=0;i<list.size();i++)
			{
				itemList.add(0,list.get(i));
			}
		}
		return itemList;
	}
	
	/***
	 * 当传入多个指标集代码的时候，首先显示传入的指标集信息
	 * @return
	 * @throws Exception
	 */
	private ArrayList manyFieldItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		for(int i=0;i<this.fieldItemId.split("`").length;i++)
		{
			HashMap hm = new HashMap();
			String itemstr = this.fieldItemId.split("`")[i];
			if("A".equals(itemstr))
			{
				hm.put("id", "A");
				hm.put("text", "人员指标集");
				hm.put("fieldItemId", "A");
				hm.put("leaf",Boolean.FALSE);
				//hm.put("icon","/images/pos_l.gif");
				itemList.add(hm);
			}else if("B".equals(itemstr))
			{
				hm.put("id", "B");
				hm.put("text", "单位指标集");
				hm.put("fieldItemId", "B");
				hm.put("leaf",Boolean.FALSE);
				//hm.put("icon","/images/pos_l.gif");
				itemList.add(hm);
			}else if("K".equals(itemstr))
			{
				hm.put("id", "K");
				hm.put("text", "岗位指标集");
				hm.put("fieldItemId", "K");
				hm.put("leaf",Boolean.FALSE);
				//hm.put("icon","/images/pos_l.gif");
				itemList.add(hm);
			}else if("H".equals(itemstr))
			{
				hm.put("id", "H");
				hm.put("text", "基准岗位指标集");
				hm.put("fieldItemId", "H");
				hm.put("leaf",Boolean.FALSE);
				//hm.put("icon","/images/pos_l.gif");
				itemList.add(hm);
			}else if("Y".equals(itemstr.split(":")[0]))
			{
				ArrayList dictionary = this.getDictionaryItems(itemstr.split(":")[1]);
				for(int j=0;j<dictionary.size();j++)
				{
					itemList.add(dictionary.get(j));
				}
			}
		}
		ArrayList list = new ArrayList();
		//定制显示招聘
		if("recruit".equals(this.entityFn))
		{	
			list = getRecruitItems();
		}else if("performance".equalsIgnoreCase(this.entityFn)&&"root".equalsIgnoreCase(this.nodeid)) {
			list = getPerformance();
		}
		
		for(int i=0;i<list.size();i++)
		{
			itemList.add(list.get(i));
		}
		return itemList;
	}
	/**
	 * 根据查询字符串进行模糊查询
	 * @return
	 * @throws Exception
	 */
	private ArrayList searchNameItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		HashMap<String, String> itemMap = null;
		if("recruit".equals(this.entityFn))
		    itemMap = getItemMap();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select item.itemid,item.itemdesc,itemtype,sets.fieldSetId from fielditem item ");
		sql.append("left join fieldSet sets on item.fieldsetid=sets.fieldSetId where sets.UseFlag=1 and ");
		sql.append("(");
        List values = new ArrayList();
		for(int i=0;i<this.fieldItemId.split("`").length;i++)
		{
			String itemstr = this.fieldItemId.split("`")[i];
			sql.append(" item.fieldSetId like ? or");
			values.add(itemstr+"%");
		}
		sql.setLength(sql.length()-2);
		sql.append(")");
		sql.append("and item.itemdesc like ?  order by item.displayid ");
        values.add("%"+this.fieldItemName+"%");
		this.rowset = dao.search(sql.toString(),values);
		while(this.rowset.next())
		{
			if(this.isFilterItems(rowset.getString("itemid")))
				continue;
			
			if(itemMap != null && !itemMap.isEmpty() && !itemMap.containsKey(rowset.getString("itemid").toLowerCase()))
			    continue;
			
			HashMap hm = new HashMap();
			hm.put("id", rowset.getString("itemid"));
			hm.put("text", rowset.getString("itemdesc"));
			hm.put("fieldItemId", rowset.getString("itemid"));
			hm.put("fieldItemType", rowset.getString("itemtype"));
			hm.put("fieldSetId", rowset.getString("fieldSetId"));
			hm.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm.put("checked", Boolean.FALSE);
			}
			itemList.add(hm);
		}
		this.rowset=null;
		if(this.dictionary.trim().length()>0&&"Y".equalsIgnoreCase(this.dictionary))
		{
		    values.clear();
			StringBuffer dictionarySql = new StringBuffer();
			dictionarySql.append("select FieldSetId,itemid,itemdesc,itemtype from t_hr_busifield where (");
			for(int i=0;i<this.dictionarytable.split("&").length;i++)
			{				
				dictionarySql.append(" FieldSetId = ? or ");
				values.add(this.dictionarytable.split("&")[i]);
			}
			dictionarySql.setLength(dictionarySql.length()-3);
			dictionarySql.append(") and useflag=1 and itemdesc like ? order by displayid ");
			values.add("%"+this.fieldItemName+"%");
			this.rowset = dao.search(dictionarySql.toString(),values);
			while(this.rowset.next())
			{
				if(this.isFilterItems(rowset.getString("itemid")))
				{
					continue;
				}
				HashMap hm = new HashMap();
				hm.put("id", rowset.getString("itemid"));
				hm.put("text", rowset.getString("itemdesc"));
				hm.put("fieldItemId", rowset.getString("itemid"));
				hm.put("fieldItemType", rowset.getString("itemtype"));
				hm.put("fieldSetId", rowset.getString("FieldSetId"));
				hm.put("leaf",Boolean.TRUE);
				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
				{					
					hm.put("checked", Boolean.FALSE);
				}
				itemList.add(hm);
			}
			this.rowset=null;
		}
		ArrayList list = new ArrayList();
		//定制显示招聘
		if("recruit".equals(this.entityFn))
		{	
			list = searchNameRecruitItems();
		}else if("performance".equals(this.entityFn))//定制显示绩效需要的指标
		{
			list = searchPerformanceChildrenItems(true);
		}
		for(int i=0;i<list.size();i++)
		{
			itemList.add(0,list.get(i));
		}
		return itemList;
	}
	
	/***
	 * 查询当前节点下子集指标
	 * @return
	 * @throws Exception
	 */
	private ArrayList searchChildrenItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		HashMap fieldItemMap = null;
		if("recruit".equals(this.entityFn)) { 
		    GetFieldItemBo bo = new GetFieldItemBo(this.conn);
    		HashMap fieldsetMap= bo.getZpFieldList();
            if(fieldsetMap != null)
                fieldItemMap = (HashMap) fieldsetMap.get(this.nodeid.toLowerCase());
		}
		
		StringBuffer sql = new StringBuffer();
		if(this.itemflag)
		{
			//定制显示招聘
			if("recruit".equals(this.entityFn))			
				itemList = searchRecruitChildrenItems();
			else if("performance".equalsIgnoreCase(this.entityFn))
				itemList = searchPerformanceChildrenItems(false);
			
		}else{	
			if("A01".equalsIgnoreCase(this.nodeid)) {
			    if(fieldItemMap == null || fieldItemMap.containsKey("b0110")) {
    				HashMap  B01= new HashMap();
    				B01.put("id", "B0110");
    				B01.put("text", "单位名称");
    				B01.put("fieldItemId", "B0110");
    				B01.put("fieldItemType", "A");
    				B01.put("fieldSetId", "A01");
    				B01.put("leaf",Boolean.TRUE);
    				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
    					B01.put("checked", Boolean.FALSE);
    				
    				itemList.add(B01);
			    }
			    
			    if(fieldItemMap == null || fieldItemMap.containsKey("e0122")) {
    				HashMap E0122 = new HashMap();
    				E0122.put("id", "E0122");
    				E0122.put("text", "部门");
    				E0122.put("fieldItemId", "E0122");
    				E0122.put("fieldItemType", "A");
    				E0122.put("fieldSetId", "A01");
    				E0122.put("leaf",Boolean.TRUE);
    				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
    					E0122.put("checked", Boolean.FALSE);
    				
    				itemList.add(E0122);
			    }
			    
				if(fieldItemMap == null || fieldItemMap.containsKey("e01A1")) {
    				HashMap E01 = new HashMap();
    				E01.put("id", "E01A1");
    				E01.put("text", "岗位");
    				E01.put("fieldItemId", "E01A1");
    				E01.put("fieldItemType", "A");
    				E01.put("fieldSetId", "A01");
    				E01.put("leaf",Boolean.TRUE);
    				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
    					E01.put("checked", Boolean.FALSE);
    				
    				itemList.add(E01);
				}
			}
			//zhangh 2020-5-8 【60123】VFS+UTF-8+达梦：证照管理/档案管理/点击右上角的齿轮(栏目设置)，点击添加，点击主集或子集左侧的"+"展开，指标信息不显示，后台报错
			sql.append(" select item.itemid,item.itemdesc,itemtype,fset.fieldSetId from fielditem item left join fieldSet fset on item.fieldsetid=fset.fieldSetId");
			sql.append(" where item.fieldsetid='"+this.nodeid+"' order by item.displayid");
			this.rowset = dao.search(sql.toString());
			while(this.rowset.next()) {
				if("A01".equalsIgnoreCase(this.nodeid)&&"E0122".equalsIgnoreCase(rowset.getString("itemid")))
					continue;
				
				if(this.isFilterItems(rowset.getString("itemid")))
					continue;
				
				if(fieldItemMap != null && !fieldItemMap.containsKey(rowset.getString("itemid").toLowerCase()))
				    continue;
				
				HashMap hm = new HashMap();
				hm.put("id", rowset.getString("itemid"));
				hm.put("text", rowset.getString("itemdesc"));
				hm.put("fieldItemId", rowset.getString("itemid"));
				hm.put("fieldItemType", rowset.getString("itemtype"));
				hm.put("fieldSetId", rowset.getString("fieldSetId"));
				hm.put("leaf",Boolean.TRUE);
				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
					hm.put("checked", Boolean.FALSE);
				
				itemList.add(hm);
			}
			
			this.rowset=null;
			if(this.dictionary.trim().length()>0&&"Y".equalsIgnoreCase(this.dictionary)) {
				StringBuffer dictionarySql = new StringBuffer();
				dictionarySql.append("select itemid,itemdesc,itemtype,FieldSetId from t_hr_busifield where ");
				dictionarySql.append(" FieldSetId = '"+this.nodeid+"' ");
				dictionarySql.append(" and useflag=1 order by displayid");
				this.rowset = dao.search(dictionarySql.toString());
				while(this.rowset.next()) {
					if(this.isFilterItems(rowset.getString("itemid")))
						continue;
					
					HashMap hm = new HashMap();
					hm.put("id", rowset.getString("itemid"));
					hm.put("text", rowset.getString("itemdesc"));
					hm.put("fieldItemId", rowset.getString("itemid"));
					hm.put("fieldItemType", rowset.getString("itemtype"));
					hm.put("leaf",Boolean.TRUE);
					hm.put("fieldSetId", rowset.getString("FieldSetId"));
					if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))					
						hm.put("checked", Boolean.FALSE);
					
					itemList.add(hm);
				}
			}
		}
		
		return itemList;
	}
	/***
	 * 业务字典指标项
	 * @param dictionaryTable
	 * @return
	 * @throws Exception
	 */
	private ArrayList getDictionaryItems(String dictionaryTable) throws Exception
	{
		ArrayList itemList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select fieldSetId,fieldsetdesc from t_hr_busitable where (");
		for(int i=0;i<dictionaryTable.split("&").length;i++)
		{
			sql.append(" FieldSetId = '"+dictionaryTable.split("&")[i]+"' or ");
		}
		sql.setLength(sql.length()-3);
		sql.append(") and useflag=1  order by displayorder");
		this.rowset = dao.search(sql.toString());
		String notfilter =(String) this.filterTable();
		while(this.rowset.next())
		{
			String itemId = rowset.getString("fieldSetId");
			if(notfilter.indexOf(itemId.toUpperCase())==-1)
			{
				continue;
			}
			HashMap hm = new HashMap();
			hm.put("id", rowset.getString("fieldSetId"));
			hm.put("text", rowset.getString("fieldsetdesc"));
			hm.put("fieldItemId", rowset.getString("fieldSetId"));
			hm.put("leaf",Boolean.FALSE);
			itemList.add(hm);
		}
		this.rowset=null;
		return itemList;
	}
	
	private String filterDictionaryTable() throws Exception
	{
		String notfilter = "";
		ContentDAO dao = new ContentDAO(conn);
		try {
			StringBuffer sql = new StringBuffer("select fieldsetid from t_hr_busifield where itemid not in(");
			for(int i=0;i<this.filterItems.split(",").length;i++)
			{
				sql.append("'"+this.filterItems.split(",")[i]+"',");
			}
			sql.setLength(sql.length()-1);
			sql.append(") group by fieldsetid ");
			RowSet rs = dao.search(sql.toString());
			while (rs.next()) {
				notfilter+=rs.getString("fieldsetid")+" ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notfilter;
	}
	
	/**
	 * 招聘模块
	 * @return
	 * @throws Exception
	 */
	private ArrayList getRecruitItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select fieldSetId,fieldsetdesc from t_hr_busitable where (FieldSetId = 'Z03' or FieldSetId = 'Z05')  order by displayorder");
		this.rowset = dao.search(sql.toString());
		while(this.rowset.next())
		{
			HashMap hm = new HashMap();
			hm.put("id", rowset.getString("fieldSetId")+"_item");
			hm.put("text", rowset.getString("fieldsetdesc"));
			hm.put("fieldItemId", rowset.getString("fieldSetId"));
			//hm.put("icon","/images/pos_l.gif");
			hm.put("leaf",Boolean.FALSE);
			itemList.add(hm);
		}
		HashMap hm = new HashMap();
		hm.put("id", "sysRecruit_item");
		hm.put("text", "系统变量（招聘）");
		hm.put("fieldItemId", "sysRecruit");
		//hm.put("icon","/images/pos_l.gif");
		hm.put("leaf",Boolean.FALSE);
		itemList.add(hm);
		this.rowset=null;
		return itemList;
	}
	
	/***
	 * 查询招聘职位列表下的指标
	 * @return
	 * @throws Exception
	 */
	private ArrayList searchRecruitChildrenItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		if("sysRecruit".equals(this.nodeid))
		{
			HashMap hm4 = new HashMap();
			hm4.put("id", "contactPerson");
			hm4.put("text", "联系人");
			hm4.put("fieldItemId", "sys");
			hm4.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm4.put("checked", Boolean.FALSE);
			}
			itemList.add(hm4);
			HashMap hm3 = new HashMap();
			hm3.put("id", "phoneNo");
			hm3.put("text", "联系人电话");
			hm3.put("fieldItemId", "sys");
			hm3.put("leaf",Boolean.TRUE);

			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm3.put("checked", Boolean.FALSE);
			}
			itemList.add(hm3);
			HashMap hm5 = new HashMap();
			hm5.put("id", "humanDepartment");
			hm5.put("text", "联系人部门");
			hm5.put("fieldItemId", "sys");
			hm5.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm5.put("checked", Boolean.FALSE);
			}
			itemList.add(hm5);
			HashMap hm1 = new HashMap();
			hm1.put("id", "company");
			hm1.put("text", "联系人单位");
			hm1.put("fieldItemId", "sys");
			hm1.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm1.put("checked", Boolean.FALSE);
			}
			itemList.add(hm1);
			HashMap hm2 = new HashMap();
			hm2.put("id", "sendDate");
			hm2.put("text", "发件日期");
			hm2.put("fieldItemId", "sys");
			hm2.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm2.put("checked", Boolean.FALSE);
			}
			itemList.add(hm2);
			HashMap hm6 = new HashMap();
			hm6.put("id", "email_confirm");
			hm6.put("text", "确认参加");
			hm6.put("fieldItemId", "sys");
			hm6.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm6.put("checked", Boolean.FALSE);
			}
			itemList.add(hm6);
		}else{			
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid,itemdesc,itemtype,FieldSetId  from t_hr_busifield where FieldSetId = '"+this.nodeid+"' order by displayid");
			this.rowset = dao.search(sql.toString());
			while(this.rowset.next())
			{
				if(this.isFilterItems(rowset.getString("itemid")))
				{
					continue;
				}
				HashMap hm = new HashMap();
				hm.put("id", rowset.getString("itemid"));
				hm.put("text", rowset.getString("itemdesc"));
				hm.put("fieldItemId", rowset.getString("itemid"));
				//hm.put("icon","/images/pos_l.gif");
				hm.put("fieldItemType", rowset.getString("itemtype"));
				hm.put("fieldSetId", rowset.getString("FieldSetId"));
				hm.put("leaf",Boolean.TRUE);
				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
				{					
					hm.put("checked", Boolean.FALSE);
				}
				itemList.add(hm);
			}
			this.rowset=null;
		}
		return itemList;
	}
	
	/**
	 * 绩效模块
	 * @return
	 * @throws Exception
	 */
	private ArrayList getPerformance() throws Exception
	{
		ArrayList itemList = new ArrayList();
		HashMap hm = new HashMap();
		hm.put("id", "sysRecruit_item");
		hm.put("text", "考核结果表");
		hm.put("fieldItemId", "sysRecruit");
		hm.put("leaf",Boolean.FALSE);
		itemList.add(hm);
		return itemList;
	}
	
	/***
	 * 查询招聘职位列表下的指标
	 * @flag : 是否是查询
	 * @return
	 * @throws Exception
	 */
	private ArrayList searchPerformanceChildrenItems(boolean isSearch) throws Exception
	{
		ArrayList itemList = new ArrayList();
		//if("sysRecruit".equals(this.nodeid))
		//{
		if(!isSearch || (isSearch && "考核计划名称".indexOf(this.fieldItemName)>-1)) {
			HashMap hm0 = new HashMap();
			hm0.put("id", "plan_id");
			hm0.put("text", "考核计划名称");
			hm0.put("fieldItemId", "plan_id");
			hm0.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm0.put("checked", Boolean.FALSE);
			}
			itemList.add(hm0);
		}
		if(!isSearch || (isSearch && "单位名称".indexOf(this.fieldItemName)>-1)) {
			HashMap hm1 = new HashMap();
			hm1.put("id", "B0110");
			hm1.put("text", "单位名称");
			hm1.put("fieldItemId", "B0110");
			hm1.put("fieldSetId","A01");
			hm1.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm1.put("checked", Boolean.FALSE);
			}
			itemList.add(hm1);
		}
		if(!isSearch || (isSearch && "部门名称".indexOf(this.fieldItemName)>-1)) {
			HashMap hm2 = new HashMap();
			hm2.put("id", "E0122");
			hm2.put("text", "部门名称");
			hm2.put("fieldItemId", "E0122");
			hm2.put("fieldSetId","A01");
			hm2.put("leaf",Boolean.TRUE);

			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm2.put("checked", Boolean.FALSE);
			}
			itemList.add(hm2);
		}
		if(!isSearch || (isSearch && "岗位名称".indexOf(this.fieldItemName)>-1)) {
			HashMap hm3 = new HashMap();
			hm3.put("id", "E01A1");
			hm3.put("text", "岗位名称");
			hm3.put("fieldItemId", "E0122");
			hm3.put("fieldSetId","A01");
			hm3.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm3.put("checked", Boolean.FALSE);
			}
			itemList.add(hm3);
		}
		if(!isSearch || (isSearch && "对象类别".indexOf(this.fieldItemName)>-1)) {
			HashMap hm4 = new HashMap();
			hm4.put("id", "body_id");
			hm4.put("text", "对象类别");
			hm4.put("fieldItemId", "body_id");
			hm4.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm4.put("checked", Boolean.FALSE);
			}
			itemList.add(hm4);
		}
		if(!isSearch || (isSearch && "考核对象名称".indexOf(this.fieldItemName)>-1)) {
			HashMap hm5 = new HashMap();
			hm5.put("id", "A0101");
			hm5.put("text", "考核对象名称");
			hm5.put("fieldItemId", "A0101");
			hm5.put("fieldSetId","A01");
			hm5.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm5.put("checked", Boolean.FALSE);
			}
			itemList.add(hm5);
		}
		if(!isSearch || (isSearch && "计算总分".indexOf(this.fieldItemName)>-1)) {
			HashMap hm6 = new HashMap();
			hm6.put("id", "original_score");
			hm6.put("text", "计算总分");
			hm6.put("fieldItemId", "original_score");
			hm6.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm6.put("checked", Boolean.FALSE);
			}
			itemList.add(hm6);
		}
		if(!isSearch || (isSearch && "总分".indexOf(this.fieldItemName)>-1)) {
			HashMap hm7 = new HashMap();
			hm7.put("id", "score");
			hm7.put("text", "总分");
			hm7.put("fieldItemId", "score");
			hm7.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm7.put("checked", Boolean.FALSE);
			}
			itemList.add(hm7);
		}
		if(!isSearch || (isSearch && "部门排名".indexOf(this.fieldItemName)>-1)) {
			HashMap hm8 = new HashMap();
			hm8.put("id", "org_ordering");
			hm8.put("text", "部门排名");
			hm8.put("fieldItemId", "org_ordering");
			hm8.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm8.put("checked", Boolean.FALSE);
			}
			itemList.add(hm8);
		}
		if(!isSearch || (isSearch && "组内排名".indexOf(this.fieldItemName)>-1)) {
			HashMap hm9 = new HashMap();
			hm9.put("id", "ordering");
			hm9.put("text", "组内排名");
			hm9.put("fieldItemId", "ordering");
			hm9.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm9.put("checked", Boolean.FALSE);
			}
			itemList.add(hm9);
		}
		if(!isSearch || (isSearch && "组内平均分".indexOf(this.fieldItemName)>-1)) {
			HashMap hm10 = new HashMap();
			hm10.put("id", "exS_GrpAvg");
			hm10.put("text", "组内平均分");
			hm10.put("fieldItemId", "sys");
			hm10.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm10.put("checked", Boolean.FALSE);
			}
			itemList.add(hm10);
		}
		if(!isSearch || (isSearch && "组内最低分".indexOf(this.fieldItemName)>-1)) {
			HashMap hm11 = new HashMap();
			hm11.put("id", "exS_GrpMin");
			hm11.put("text", "组内最低分");
			hm11.put("fieldItemId", "exS_GrpMin");
			hm11.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm11.put("checked", Boolean.FALSE);
			}
			itemList.add(hm11);
		}
		if(!isSearch || (isSearch && "组内最高分".indexOf(this.fieldItemName)>-1)) {
			HashMap hm12 = new HashMap();
			hm12.put("id", "exS_GrpMax");
			hm12.put("text", "组内最高分");
			hm12.put("fieldItemId", "exS_GrpMax");
			hm12.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm12.put("checked", Boolean.FALSE);
			}
			itemList.add(hm12);
		}
		if(!isSearch || (isSearch && "等级系数".indexOf(this.fieldItemName)>-1)) {
			HashMap hm13 = new HashMap();
			hm13.put("id", "exX_object");
			hm13.put("text", "等级系数");
			hm13.put("fieldItemId", "exX_object");
			hm13.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm13.put("checked", Boolean.FALSE);
			}
			itemList.add(hm13);
		}
		if(!isSearch || (isSearch && "等级".indexOf(this.fieldItemName)>-1)) {
			HashMap hm14 = new HashMap();
			hm14.put("id", "resultdesc");
			hm14.put("text", "等级");
			hm14.put("fieldItemId", "resultdesc");
			hm14.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm14.put("checked", Boolean.FALSE);
			}
			itemList.add(hm14);
		}
		if(!isSearch || (isSearch && "考核结果确认".indexOf(this.fieldItemName)>-1)) {
			HashMap hm15 = new HashMap();
			hm15.put("id", "confirmflag");
			hm15.put("text", "考核结果确认");
			hm15.put("fieldItemId", "confirmflag");
			hm15.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm15.put("checked", Boolean.FALSE);
			}
			itemList.add(hm15);
		}
		if(!isSearch || (isSearch && "绩效考核申述".indexOf(this.fieldItemName)>-1)) {
			HashMap hm16 = new HashMap();
			hm16.put("id", "appeal");
			hm16.put("text", "绩效考核申述");
			hm16.put("fieldItemId", "appeal");
			hm16.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm16.put("checked", Boolean.FALSE);
			}
			itemList.add(hm16);
		}
//		}else{			
//			StringBuffer sql = new StringBuffer();
//			sql.append("select itemid,itemdesc,itemtype,FieldSetId  from t_hr_busifield where FieldSetId = '"+this.nodeid+"' order by displayid");
//			this.rowset = dao.search(sql.toString());
//			while(this.rowset.next())
//			{
//				if(this.isFilterItems(rowset.getString("itemid")))
//				{
//					continue;
//				}
//				HashMap hm = new HashMap();
//				hm.put("id", rowset.getString("itemid"));
//				hm.put("text", rowset.getString("itemdesc"));
//				hm.put("fieldItemId", rowset.getString("itemid"));
//				//hm.put("icon","/images/pos_l.gif");
//				hm.put("fieldItemType", rowset.getString("itemtype"));
//				hm.put("fieldSetId", rowset.getString("FieldSetId"));
//				hm.put("leaf",Boolean.TRUE);
//				if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
//				{					
//					hm.put("checked", Boolean.FALSE);
//				}
//				itemList.add(hm);
//			}
//			this.rowset=null;
//		}
		return itemList;
	}
	
	private ArrayList searchNameRecruitItems() throws Exception
	{
		ArrayList itemList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" select itemid,itemdesc,itemtype,FieldSetId from t_hr_busifield where (FieldSetId = 'Z03' or FieldSetId = 'Z05') and itemdesc like '%"+this.fieldItemName+"%' order by displayid");
		this.rowset = dao.search(sql.toString());
		while(this.rowset.next())
		{
			if(this.isFilterItems(rowset.getString("itemid")))
			{
				continue;
			}
			HashMap hm = new HashMap();
			hm.put("id", rowset.getString("itemid"));
			hm.put("text", rowset.getString("itemdesc"));
			hm.put("fieldItemId", rowset.getString("itemid"));
			hm.put("fieldItemType", rowset.getString("itemtype"));
			hm.put("fieldSetId", rowset.getString("FieldSetId"));
			hm.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm.put("checked", Boolean.FALSE);
			}
			itemList.add(hm);
		}
		if("联系人单位".indexOf(this.fieldItemName)>-1)
		{
			HashMap hm1 = new HashMap();
			hm1.put("id", "company");
			hm1.put("text", "联系人单位");
			hm1.put("fieldItemId", "sys");
			hm1.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm1.put("checked", Boolean.FALSE);
			}
			itemList.add(hm1);
		}
		if("发件日期".indexOf(this.fieldItemName)>-1)
		{
			HashMap hm2 = new HashMap();
			hm2.put("id", "sendDate");
			hm2.put("text", "发件日期");
			hm2.put("fieldItemId", "sys");
			hm2.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm2.put("checked", Boolean.FALSE);
			}
			itemList.add(hm2);
		}
		if("联系人电话".indexOf(this.fieldItemName)>-1)
		{
			HashMap hm3 = new HashMap();
			hm3.put("id", "phoneNo");
			hm3.put("text", "联系人电话");
			hm3.put("fieldItemId", "sys");
			hm3.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm3.put("checked", Boolean.FALSE);
			}
			itemList.add(hm3);
		}
		if("联系人".indexOf(this.fieldItemName)>-1)
		{
			HashMap hm4 = new HashMap();
			hm4.put("id", "sys");
			hm4.put("text", "联系人");
			hm4.put("fieldItemId", "sys");
			hm4.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm4.put("checked", Boolean.FALSE);
			}
			itemList.add(hm4);
		}
		if("联系人部门".indexOf(this.fieldItemName)>-1)
		{
			HashMap hm5 = new HashMap();
			hm5.put("id", "humanDepartment");
			hm5.put("text", "联系人部门");
			hm5.put("fieldItemId", "sys");
			hm5.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm5.put("checked", Boolean.FALSE);
			}
			itemList.add(hm5);
		}
		if("确认参加".indexOf(this.fieldItemName)>-1) {
			HashMap hm6 = new HashMap();
			hm6.put("id", "email_confirm");
			hm6.put("text", "确认参加");
			hm6.put("fieldItemId", "sys");
			hm6.put("leaf",Boolean.TRUE);
			if(this.isCheckBox!=null&&"true".equalsIgnoreCase(this.isCheckBox))
			{					
				hm6.put("checked", Boolean.FALSE);
			}
			itemList.add(hm6);
		}
		this.rowset=null;
		return itemList;
	}

	public void setFilterItems(String filterItems) {
		this.filterItems = filterItems;
	}

	public String getFilterItems() {
		return filterItems;
	}
	/**
	 * 获取招聘中有权限的指标
	 * @return
	 */
	private HashMap<String, String> getItemMap() {
        HashMap<String, String> hm = new HashMap<String, String>();
        try {
            GetFieldItemBo bo = new GetFieldItemBo(this.conn);
            HashMap fieldsetMap= bo.getZpFieldList();
            Iterator it=fieldsetMap.keySet().iterator();    
            while(it.hasNext()){    
                 String key;    
                 HashMap<String, String> value;    
                 key=it.next().toString();    
                 value=(HashMap<String, String>) fieldsetMap.get(key);    
                 if(!value.isEmpty())
                     hm.putAll(value);   
            }   
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        
        return hm;
    }
}
