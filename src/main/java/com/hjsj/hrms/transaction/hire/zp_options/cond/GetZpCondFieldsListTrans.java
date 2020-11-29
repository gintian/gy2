package com.hjsj.hrms.transaction.hire.zp_options.cond;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class GetZpCondFieldsListTrans extends IBusiness {
	public void execute () throws GeneralException{
		try{ 
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String templateid="-1";
			if(hm!=null)
				templateid=(String)hm.get("templateid");
			String fieldSetId = (String)this.getFormHM().get("fieldsetid");
			ArrayList fieldSetList = new ArrayList();
			ArrayList fieldList = new ArrayList();
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			/**面试过程记录子集*/
			 String remenberExamineSet="";
			if(map!=null&&map.get("remenberExamineSet")!=null)
			{
				remenberExamineSet=(String)map.get("remenberExamineSet");
			}
			ArrayList selectedFieldsList = new ArrayList();
			String selectedIds = "";
			int type=0;
			if(hm != null){
			   if(hm.get("type") != null && ((String)hm.get("type")).trim().length()>0)
			       type=Integer.parseInt((String)hm.get("type"));
			}
			else
				type=Integer.parseInt((String)this.getFormHM().get("type"));
			if(type == 0)
				selectedIds=bo.getSelectedFieldsIds("simple");
			if(type == 1)
			{
				if(templateid!=null&&!"-1".equals(templateid))
				{
				   HashMap hmp=bo.getFactorExpr(templateid);
				   selectedIds=(String)hmp.get("str");
				}
			}
			if(selectedIds!= null&&selectedIds.trim().length()>0)
				selectedFieldsList = this.getSelectedList(selectedIds);
			String sql = "select * from constant where constant='ZP_FIELD_LIST'";
			String fieldSetId_str = getFieldSetIdStr(sql);
			if(fieldSetId_str != null && fieldSetId_str.trim().length()>0)
			    fieldSetList = getFieldSetList(fieldSetId_str,remenberExamineSet);
			String fieldId_str = "";
			String firstId="";
			if(fieldSetId == null|| "".equals(fieldSetId))
			{
				fieldId_str = getFieldIdStrFirst(sql);
				String[] arr= fieldId_str.split("#");
				if(arr.length>=2)
				{
			    	fieldId_str=arr[0];
			    	firstId=arr[1];
				}
			}else{
				fieldId_str = getFieldIdStr(fieldSetId,sql);
			}
			if(fieldId_str != null && fieldId_str.trim().length()>0 )
			    fieldList = getFieldList(fieldId_str,fieldSetId);
			if(fieldSetId!=null&&fieldSetId.equalsIgnoreCase(remenberExamineSet))
				fieldList = getFieldList2(remenberExamineSet);
			if(fieldSetId!=null&& "A01".equalsIgnoreCase(fieldSetId))
			{
				if(type!=1)
				   fieldList.add(fieldList.size(),new CommonData("createtime","简历入库时间"));
			}
			if(firstId!=null&& "A01".equalsIgnoreCase(firstId))
			{
				if(type!=1)
				fieldList.add(fieldList.size(),new CommonData("createtime","简历入库时间"));
			}
			this.getFormHM().put("zpFieldList",fieldList);
			this.getFormHM().put("zpFieldSetList",fieldSetList);
			this.getFormHM().put("selectedFieldsList",selectedFieldsList);
			this.getFormHM().put("zp_cond_template_type",String.valueOf(type));
			//this.getFormHM().put("right_fields", new String[0]);
			//this.getFormHM().put("zp_cond_template_type",type);
	       // hm.remove("type");
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	
	}
	
	/**
	 * 查找指标集的方法
	 * @param sql
	 * @return
	 */
	private String getFieldSetIdStr(String sql){
		StringBuffer str = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String str_value = this.frowset.getString("str_value");
				if(str_value!=null&&!"".equals(str_value))
				{
				String[] strArr = str_value.split(",},");
				for(int i=0;i<strArr.length;i++){
					String fieldSetId = strArr[i].substring(0,strArr[i].indexOf("{"));
					str.append(",'"+fieldSetId);
					str.append("'");
				}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();
	}
	/**
	 * 查找指标集对应的指标项
	 * @param fieldSetId
	 * @param sql
	 * @return
	 */
	private String getFieldIdStr(String fieldSetId,String sql){
		StringBuffer str = new StringBuffer();
		String fieldId_str = "";
		String tem_sql = "";
		try{ 
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String str_value = this.frowset.getString("str_value");
				if(str_value!=null&&!"".equals(str_value))
				{
				String[] strArr = str_value.split(",},");
				for(int i=0;i<strArr.length;i++){
					String fieldSetIdStr = strArr[i].substring(0,strArr[i].indexOf("{"));
					if(fieldSetIdStr.equals(fieldSetId)){
						fieldId_str=strArr[i].substring(strArr[i].indexOf("{")+1);
						String[] str_a = fieldId_str.split(",");
						for(int j=0;j<str_a.length;j++){
							if(str_a[j].indexOf("[") != -1){
								str.append(",");
								str.append("'");
								str.append(str_a[j].substring(0,str_a[j].indexOf("[")));
								str.append("'");
								
							}else{
							str.append(",");
							str.append("'");
							str.append(str_a[j]);
							str.append("'");
							}
						}
						break;
					}
				}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(str.toString().trim().length()<=0 || str ==null)
			return "";
		else
		   return str.toString().substring(1);
	}
	/**
	 * 得到指标集列表
	 * @param sql
	 * @return
	 */
	private ArrayList getFieldSetList(String sql, String remenberExamineSet){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql_str = "";
			if(sql!=null&&!"".equals(sql))
			{
				sql_str = sql.substring(1);
			}
			else
			{
				sql_str="''";
			}
			String sel_sql = "select * from fieldset where fieldsetid in ("+sql_str+",'"+remenberExamineSet+"')";
            this.frowset = dao.search(sel_sql);
			while(this.frowset.next()){
				CommonData obj =  new CommonData(this.frowset.getString("fieldsetid"),this.frowset.getString("customdesc"));
				list.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到指标列表
	 * @param sql
	 * @return
	 */
	private ArrayList getFieldList(String sql,String str_fieldSetId){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql_str=" select * from fielditem where itemid in ("+((sql==null|| "".equals(sql)|| "#".equals(sql))?"''":sql)+") and itemtype <> 'M'";
			this.frowset = dao.search(sql_str);
			while(this.frowset.next()){
				CommonData obj = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
				list.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	
	
    private ArrayList getFieldList2(String remenberExamineSet) {
		
    	ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql_str="select * from fielditem where fieldsetid ='"+remenberExamineSet+"'";
			this.frowset = dao.search(sql_str);
			while(this.frowset.next()){
				CommonData obj = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
				list.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 首次进入
	 * @param fieldSetId
	 * @param sql
	 * @return
	 */
	private String getFieldIdStrFirst(String sql){
		StringBuffer str = new StringBuffer();
		String fieldId_str = "";
		String fieldSetIdStr="";
		try{ 
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String str_value = this.frowset.getString("str_value");
				if(str_value!=null&&!"".equals(str_value))
				{
				String[] strArr = str_value.split(",},");
				for(int i=0;i<strArr.length;i++){
					fieldSetIdStr = strArr[0].substring(0,strArr[i].indexOf("{"));
					str.append(strArr[0].substring(strArr[0].indexOf("{")+1));
					break;
				}
				}
			}
			fieldId_str = str.toString();
			if(fieldId_str!=null&&!"".equals(fieldId_str))
			{
			String[] str_a = fieldId_str.split(",");
			str.setLength(0);
			for(int j=0;j<str_a.length;j++){
				if(str_a[j].indexOf("[") !=-1){
					str.append(",");
					str.append("'");
					str.append(str_a[j].substring(0,str_a[j].indexOf("[")));
					str.append("'");
				}else{
				   str.append(",");
				   str.append("'");
				   str.append(str_a[j]);
				   str.append("'");
			}
			}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(str.toString().trim().length()<=0 || str == null)
			return ""+"#"+fieldSetIdStr;
		else
		 return str.toString().substring(1)+"#"+fieldSetIdStr;
	}
	private ArrayList getSelectedList(String selectedIds){
		ArrayList list = new ArrayList();
		String[] str_Arr = null;
		HashMap hm = new HashMap();
		boolean flag=false;
		StringBuffer ids=new StringBuffer();
		if(selectedIds != null && selectedIds.trim().length()>0){
			selectedIds=selectedIds.replaceAll("'","");
			str_Arr= selectedIds.split(",");
		}
		for(int j=0;j<str_Arr.length;j++)
		{
			if("createtime".equalsIgnoreCase(str_Arr[j]))
			{
				flag=true;
				continue;
			}
			ids.append(",'");
			ids.append(str_Arr[j]);
			ids.append("'");
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuilder sql= new StringBuilder();	
	    sql.append("select itemid,itemdesc from fielditem");
	    sql.append(" where itemid in ("+((ids!=null&&ids.toString().length()>0)?ids.toString().substring(1):"''")+")");
	    sql.append(" and useflag='1'");
		try{
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				hm.put(this.frowset.getString("itemid").toLowerCase(),new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc")));
			}
			if(flag)
			{
				hm.put("createtime",new CommonData("createtime","简历入库时间"));
			}
			
			if(str_Arr != null){
				for(int i=0;i<str_Arr.length;i++){
					if(str_Arr[i]==null|| "".equals(str_Arr[i])||hm.get(str_Arr[i].toLowerCase())==null)
						continue;
					list.add((CommonData)hm.get(str_Arr[i].toLowerCase()));
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	

}
