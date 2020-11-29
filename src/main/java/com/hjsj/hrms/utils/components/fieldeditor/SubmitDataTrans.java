package com.hjsj.hrms.utils.components.fieldeditor;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class SubmitDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tableName = (String)this.getFormHM().get("tableName");
		String dbName = (String)this.getFormHM().get("dbName");
		String indexKey = (String)this.getFormHM().get("indexKey");
		String subType = (String)this.getFormHM().get("subType");
		DynaBean db = (DynaBean)this.getFormHM().get("commitData");
		ArrayList fieldInfos = (ArrayList)this.getFormHM().get("fieldInfos");
		HashMap dataMap = PubFunc.DynaBean2Map(db);
		
		//ArrayList fieldList = DataDictionary.getFieldList(tableName, Constant.USED_FIELD_SET);
		//if(fieldList==null || fieldList.size()<1)
		//	return ;
		
		RecordVo vo = null;
		try{
			  if(tableName.toLowerCase().indexOf("a")==0)//当是人员子集时加上人员库前缀
				  tableName+=dbName;
		      vo = getRecordVoFilledIndexValue(indexKey,dataMap,tableName);
		
		      DynaBean fi = null;
			for(int i=0;i<fieldInfos.size();i++){
				fi = (DynaBean)fieldInfos.get(i);
				String fieldId = fi.get("fieldId").toString();
				//如果是主键，跳过
				if(indexKey.indexOf(fieldId)!=-1 || dataMap.get(fieldId)==null || dataMap.get(fieldId).toString().length()<1)
					continue;
				String codesetid = fi.get("codesetid").toString();
				String fieldType = fi.get("fieldType").toString();
				if(codesetid.length()>0 && !"0".equals(codesetid) && dataMap.containsKey(fieldId)){
					String value = (String)dataMap.get(fieldId);
					if(value.split("`").length!=2 || "root".equals(value.split("`")[0]))
						vo.setObject(fieldId,"");
					else
						vo.setObject(fieldId,value.split("`")[0]);
					continue;
				}else if("D".equals(fieldType)){
					int length = ((Integer)fi.get("fieldLength")).intValue();
					String dateFormat="";
			    	if(length == 4){
						dateFormat="yyyy";
					}else if(length == 7){
						dateFormat="yyyy-MM";
					}else if(length == 10){
						dateFormat="yyyy-MM-dd";
					}else if(length == 16){
						dateFormat="yyyy-MM-dd HH:mm";
					}else{
						dateFormat="yyyy-MM-dd HH:mm:ss";
					}
			    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			    	Date d = new Date(sdf.parse((String)dataMap.get(fieldId)).getTime());
			    	vo.setObject(fieldId,d);
				}else if("A".equals(fieldType)){//保存前对字符串过滤，特殊字符转为全角 changxy 20160624
					vo.setString(fieldId, PubFunc.hireKeyWord_filter(dataMap.get(fieldId).toString()));
				}else
					vo.setObject(fieldId, PubFunc.hireKeyWord_filter(dataMap.get(fieldId).toString()));
			}
			if("update".equals(subType) || "view".equals(subType))
				new ContentDAO(frameconn).updateValueObject(vo);
			else
				new ContentDAO(frameconn).addValueObject(vo);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private RecordVo getRecordVoFilledIndexValue(String keystr,HashMap dataMap,String tableName) throws GeneralException{
		RecordVo vo = new RecordVo(tableName);
		String[] keys = keystr.split(",");
		for(int b=0;b<keys.length;b++)
		{
			if(dataMap.get(keys[b])!=null && dataMap.get(keys[b]).toString().length()>1){
				vo.setObject(keys[b], dataMap.get(keys[b]));
			    continue;
			}
			
			FieldItem fi = DataDictionary.getFieldItem(keys[b]);
		    if(!fi.isSequenceable())
		    	 throw new GeneralException("主键id生成错误》》》table:"+tableName+";field:"+keys[b]);
		    
		     String id = new IDGenerator(2,frameconn).getId(fi.getSequencename());
		     
		     vo.setObject(keys[b],id);
			
		}
		return vo;
	}
}
