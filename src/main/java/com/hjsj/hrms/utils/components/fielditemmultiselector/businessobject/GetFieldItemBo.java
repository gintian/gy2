package com.hjsj.hrms.utils.components.fielditemmultiselector.businessobject;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class GetFieldItemBo {
	private Connection conn = null;
	
	public GetFieldItemBo() {
		
	}
	public GetFieldItemBo(Connection conn) {
		super();
		this.conn = conn;
	}
	/**
	 * 获取指标集中的所有指标
	 * @param fieldsetid
	 * @param module 模块：=ZP：招聘模块
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFieldItemList (String value, String module) throws GeneralException{
		StringBuffer buf = new StringBuffer();
		ArrayList list = new ArrayList();
		try {
			if(value==null || value.length()==0){
				return list;
			}
			
			HashMap fieldItemMap = null;
			if("ZP".equalsIgnoreCase(module)){
		        HashMap fieldsetMap= getZpFieldList();
		        if(fieldsetMap != null)
		            fieldItemMap = (HashMap) fieldsetMap.get(value.toLowerCase());
		        
			}
			if("my_custom".equals(value)){
				HashMap<String,String> hm = new HashMap<String,String>();
				FieldItem fieldItem = DataDictionary.getFieldItem("Z0321", "Z03");
				if(fieldItem!=null){
					hm = new HashMap<String,String>();
		 	        hm.put("fieldsetid", "Z03");
		 	        hm.put("itemid", "Z0321");
		 	        hm.put("itemdesc", fieldItem.getItemdesc());
		 	        hm.put("itemtype", fieldItem.getItemtype());
		 	        hm.put("codesetid", fieldItem.getCodesetid());
		 	        list.add(hm);
				}
				fieldItem = DataDictionary.getFieldItem("Z0325", "Z03");
				if(fieldItem!=null){
		 	        hm = new HashMap<String,String>();
		 	        hm.put("fieldsetid", "Z03");
		 	        hm.put("itemid", "Z0325");
		 	        hm.put("itemdesc", fieldItem.getItemdesc());
		 	        hm.put("itemtype", fieldItem.getItemtype());
		 	        hm.put("codesetid", fieldItem.getCodesetid());
		 	        list.add(hm);
				}
				fieldItem = DataDictionary.getFieldItem("Z0351", "Z03");
				if(fieldItem!=null){
		 	        hm = new HashMap<String,String>();
		        	hm.put("fieldsetid", "Z03");
		        	hm.put("itemid", "Z0351");
		        	hm.put("itemdesc", fieldItem.getItemdesc());
		 	        hm.put("itemtype", fieldItem.getItemtype());
		 	        hm.put("codesetid", fieldItem.getCodesetid());
		 	        list.add(hm);
				}
	 	        hm = new HashMap<String,String>();
	 	        hm.put("itemid", "suitable");
	 	        hm.put("itemdesc", "简历筛选");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "0");
	 	        list.add(hm);
	 	        //流程状态选项已包含了流程环节
	 	        /*hm = new HashMap<String,String>();
	 	        hm.put("itemid", "node_id");
	 	        hm.put("itemdesc", "流程环节");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "36");
	 	        list.add(hm);*/
	 	        hm = new HashMap<String,String>();
	 	        hm.put("itemid", "status");
	 	        hm.put("itemdesc", "流程状态");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "36");
	 	        list.add(hm);
	 	        HashMap recdateHm = new HashMap();
	 	        recdateHm.put("itemid", "recdate");
	 	        recdateHm.put("itemdesc", "应聘时间");
	 	        recdateHm.put("itemtype", "D");
	 	        recdateHm.put("codesetid", "0");
	 	        recdateHm.put("formatlength", 18);
	 	        list.add(recdateHm);
	 	        hm = new HashMap<String,String>();
	 			hm.put("itemid", "Z0103");
	 			hm.put("itemdesc", "招聘批次");
	 			hm.put("itemtype", "A");
	 			hm.put("codesetid", "0");
	 			list.add(hm);
	 	        return list;
//				if (fieldItemMap != null && !fieldItemMap.containsKey(rs.getString("itemid").toLowerCase()))
//	                continue;
			}
			buf.append(" select itemid,itemdesc,itemtype,fieldsetid,codesetid from fielditem ");
			buf.append(" where useflag=1 ");
			if(value!=null&&value.length()>0&&!"".equals(value)){
				buf.append("and fieldsetid=?");
				buf.append(" order by displayid");
				list.add(value);
			}
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString(),list);
			list.clear();
			HashMap map = new HashMap();
			while(rs.next()){
                map = new HashMap();
                if (fieldItemMap != null && !fieldItemMap.containsKey(rs.getString("itemid").toLowerCase()))
                    continue;

                map.put("itemid", rs.getString("itemid"));
                map.put("itemdesc", rs.getString("itemdesc"));
                map.put("itemtype", rs.getString("itemtype"));
                map.put("fieldsetid", rs.getString("fieldsetid"));
                map.put("codesetid", rs.getString("codesetid"));
                list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
		
	}
	/**
	 * 加载指标集A`B`K`Y
	 * @param fieldset 指标集
	 * @param module 模块：=ZP：招聘模块
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldSetList(String fieldset, String module) throws GeneralException {
		ArrayList list = new ArrayList();
    	StringBuffer sql = new StringBuffer();
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		if(fieldset==null||fieldset.length()==0){
    			return list;
    		}
	        
	        HashMap hashMap = null;
    		
    		String[] strs = fieldset.split("`");
            HashMap<String, String> fieldHm = null;
            if ("ZP".equalsIgnoreCase(module)){
                fieldHm = getZpFieldSetId();
                hashMap = getZpFieldList();
            }

    		sql.append(" select fieldsetid,customdesc from fieldset ");
    		sql.append(" where UseFlag=1 and (");
    		for(int i=0;i<strs.length;i++){
    			if(i==0){
    				sql.append(" fieldsetid like '");
    				sql.append(strs[i]);
    				sql.append("%'");
    			}else{
    				sql.append(" or fieldsetid like '");
    				sql.append(strs[i]);
    				sql.append("%'");
    			}
    		}
    		sql.append(")");
    		sql.append(" order by  Displayorder ");
			RowSet rs = dao.search(sql.toString());
			HashMap map = new HashMap();
			String fieldsetid="";
			String fieldsetdesc = "";
			while(rs.next()){
                map = new HashMap();
                fieldsetid = rs.getString("fieldsetid");
                fieldsetdesc = rs.getString("customdesc");
                if (fieldHm != null && !fieldHm.containsKey(fieldsetid))
                    continue;
                if(hashMap!=null&&hashMap.get(fieldsetid.toLowerCase())==null)
                	continue;
                map.put("fieldsetid", fieldsetid);
                map.put("fieldsetdesc", fieldsetdesc);

                list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return list;
	}
	/**
	 * 获取招聘中子集权限信息
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap<String, String> getZpFieldSetId() throws GeneralException {
        HashMap<String, String> hm = new HashMap<String, String>();
        RecordVo vo= ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
        String tableName = vo.getString("str_value");
        ParameterSetBo paramBo = new ParameterSetBo(this.conn);
        if(!tableName.startsWith("{"))
        	tableName =  paramBo.convertStringFormat(tableName);
	
		Map map = (Map<String, Map<String, String>>) JSON.parse(tableName);
		ArrayList fieldList = paramBo.getFieldList();
		Map<String, Map<String, String>> sortConstantMap = paramBo.sortConstantMap(fieldList, map);
		for (String key : sortConstantMap.keySet()) {
			Map<String, String> tempMap = sortConstantMap.get(key);
			for (String hireChannelId : tempMap.keySet()) {
				if(!"displayname".equalsIgnoreCase(hireChannelId)&&!"-1".equals(tempMap.get(hireChannelId)))
					hm.put(key, "1");
			}
		}
        
        return hm;
    }

    /**
     * 将招聘的指标权限参数转换为map的形式
     * 
     * @return
     * @throws GeneralException
     */
    public HashMap getZpFieldList() throws GeneralException {
    	HashMap fieldSetMap = new HashMap();
    	RowSet rowSet=null;
    	ContentDAO dao = new ContentDAO(conn);
    	try {
    		String strValue="";
			rowSet = dao.search("select str_value from constant where constant='ZP_FIELD_LIST_JSON'");
 			if(rowSet.next()) {
            	strValue=rowSet.getString("str_value");
            	if(StringUtils.isBlank(strValue))
            		return null;
            	HashMap<String,String> map = new HashMap<String, String>();
            	if (StringUtils.isNotEmpty(strValue)) {
                    JSONObject json = JSONObject.fromObject(strValue);
                    Iterator<String> it = json.keys(); 
                    HashMap fieldItemMap = new HashMap();
                    while(it.hasNext()){
                        // 获得key
                        String key = it.next(); 
                        JSONObject array = (JSONObject) json.get(key);
                        Iterator<String> keys = array.keys(); 
                    	while(keys.hasNext()){
                    		String next = keys.next();
                    		JSONArray fieldArray = (JSONArray)array.get(next);
                    		if(fieldArray==null || fieldArray.size()==0)
                    			continue;
                    		for (int i = 0; i < fieldArray.size(); i++) {
                    			JSONObject json3 =  (JSONObject) fieldArray.get(i);
                    			String  id = (String) json3.get("id");
                    			String in_list =  (String) json3.get("in_list");
                    			String must = (String) json3.get("must");
                    			fieldItemMap.put(id, in_list+"#"+must);
                    		}
                    		fieldSetMap.put(next.toLowerCase(), fieldItemMap);
                    	}
                    }
                    
               }
            
			}else {
		        RecordVo vo2= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
		        if(vo2!=null)
		            strValue=vo2.getString("str_value");
		        
		        int idx = -1;
	            if (strValue == null || strValue.trim().length() < 1)
	                return null;

	            String[] temps = strValue.split(",},");
	            for (int i = 0; i < temps.length; i++) {
	                String setid = temps[i].substring(0, temps[i].indexOf("{"));
	                String fieldstr = temps[i].substring((temps[i].indexOf("{") + 1));
	                HashMap fieldItemMap = new HashMap();
	                String[] fields = fieldstr.split(",");
	                for (int n = 0; n < fields.length; n++) {
	                    /** 考虑兼容性，以后定义过参数 */
	                    idx = fields[n].indexOf("[");
	                    if (idx != -1) {
	                        String a = fields[n].substring(0, fields[n].indexOf("[")).toLowerCase();
	                        if(StringUtils.isNotEmpty(a) && a.indexOf("#") > -1)
	                            a = a.substring(0, a.indexOf("#"));
	                            
	                        String b = fields[n].substring((fields[n].indexOf("[") + 1), fields[n].indexOf("]"));
	                        fieldItemMap.put(a, b);
	                    } else {
	                        fieldItemMap.put(fields[n], "0");
	                    }
	                }
	                fieldSetMap.put(setid.toLowerCase(), fieldItemMap);
	            }
	        
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return fieldSetMap;
    }
}
