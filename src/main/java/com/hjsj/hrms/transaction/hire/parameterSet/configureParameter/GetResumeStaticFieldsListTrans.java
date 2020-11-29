package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class GetResumeStaticFieldsListTrans extends IBusiness {
	public void execute() throws GeneralException {
		try{ 
			//HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String fieldSetId = (String)this.getFormHM().get("fieldsetid");
			ArrayList resumeStaticFieldsSetList = new ArrayList();
			ArrayList resumeStaticFieldsList = new ArrayList();
			ArrayList selectedStaticFieldsList = new ArrayList();
			ParameterXMLBo xbo = new ParameterXMLBo(this.getFrameconn());
			String selectedFieldIds = "";
			if(xbo.getParaValues("resume_static") != null && xbo.getParaValues("resume_static").trim().length()>0)
				selectedFieldIds = xbo.getParaValues("resume_static").toUpperCase();
			ParameterSetBo sbo = new ParameterSetBo(this.getFrameconn());
			if(selectedFieldIds != null && selectedFieldIds.trim().length()>0)
			      selectedStaticFieldsList = sbo.getParaNameListByParaValue(selectedFieldIds,0);
			String sql = "select * from constant where constant='ZP_FIELD_LIST'";
			String fieldSetId_str = getFieldSetIdStr(sql);
			HashMap map=xbo.getAttributeValues();
			String remenberExamineSet="";
			if(map!=null&&map.get("remenberExamineSet")!=null)
			{
				remenberExamineSet=(String)map.get("remenberExamineSet");
			}
			if(!"".equals(remenberExamineSet))
				fieldSetId_str+=",'"+remenberExamineSet+"'";
			resumeStaticFieldsSetList = getFieldSetList(fieldSetId_str);
			String fieldId_str = "";
			if(fieldSetId == null|| "".equals(fieldSetId)){
				fieldId_str = getFieldIdStrFirst(sql);
			}else{
				fieldId_str = getFieldIdStr(fieldSetId,sql);
			}
			if(fieldSetId!=null&&!"".equals(fieldSetId)&&fieldSetId.equalsIgnoreCase(remenberExamineSet))
			{
				ArrayList alist = DataDictionary.getFieldList(remenberExamineSet, Constant.USED_FIELD_SET);
				for(int i=0;i<alist.size();i++)
				{
					FieldItem item = (FieldItem)alist.get(i);
					if(item.isCode())
					{
						resumeStaticFieldsList.add(new CommonData(item.getItemid().toUpperCase(),item.getItemdesc()));
					}
				}
			}else{
				resumeStaticFieldsList = getFieldList(fieldId_str,fieldSetId);
			}
			this.getFormHM().put("resumeStaticFieldsList",resumeStaticFieldsList);
			this.getFormHM().put("resumeStaticFieldsSetList",resumeStaticFieldsSetList);
			this.getFormHM().put("selectedStaticFieldsList",selectedStaticFieldsList);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
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
		return str.toString();
	}
	/**
	 * 得到指标集列表
	 * @param sql
	 * @return
	 */
	private ArrayList getFieldSetList(String sql){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(sql==null || sql.trim().length()<=0)
				return list;
			String sql_str = sql.substring(1);
			String sel_sql = "select * from fieldset where fieldsetid in ("+sql_str+")";
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
			String temp="";
			if(!(sql==null|| "".equals(sql)))
			{
				temp=sql.substring(1);
			}
			else
			{
				temp="''";
			}
			String sql_str=" select * from fielditem where itemid in ("+temp+") and itemtype ='A' and codesetid <>'0'";
			this.frowset = dao.search(sql_str);
			while(this.frowset.next()){
				CommonData obj = new CommonData(this.frowset.getString("itemid").toUpperCase(),this.frowset.getString("itemdesc"));
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
		try{ 
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String str_value = this.frowset.getString("str_value");
				if(str_value!=null&&!"".equals(str_value))
				{
				String[] strArr = str_value.split(",},");
				for(int i=0;i<strArr.length;i++){
					//String fieldSetIdStr = strArr[0].substring(0,strArr[i].indexOf("{"));
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
		return str.toString();
	}

	
}
