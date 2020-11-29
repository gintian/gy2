package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.utils.components.fielditemmultiselector.businessobject.GetFieldItemBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 职位候选人栏目设置添加指标
 * node "root" 获取指标集，其他获取子集指标
 * @author wangjl
 *	20170213
 */
public class RecruitprocessFieldTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String fieldset = (String) this.getFormHM().get("node");
			GetFieldItemBo bo = new GetFieldItemBo(this.getFrameconn());
			ArrayList list = new ArrayList();
			String setList = "A";
			if("root".equals(fieldset)){
				list = this.getFieldSetList(setList, "ZP");
			}else{
				list = this.getFieldItemList(fieldset, "ZP");
			}
			this.formHM.put("children", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 加载指标集A`B`K`Y:***
	 * @param fieldset 指标集
	 * @param module 模块：=ZP：招聘模块
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldSetList(String fieldset, String module) throws GeneralException {
		ArrayList list = new ArrayList();
    	StringBuffer sql = new StringBuffer();
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	try {
    		if(fieldset==null||fieldset.length()==0){
    			return list;
    		}
    		
    		String[] strs = fieldset.split("`");
            HashMap<String, String> fieldHm = null;
            if ("ZP".equalsIgnoreCase(module))
                fieldHm = getZpFieldSetId();

    		sql.append(" select fieldsetid,fieldsetdesc from ");
    		sql.append(" fieldset where UseFlag=1 and (");
    		StringBuffer sql2 = new StringBuffer("select fieldsetid,fieldsetdesc from ");
    		boolean hasY = false;
    		for(int i=0;i<strs.length;i++){
    			if(strs[i].contains("Y")){
    				hasY = true;
					String[] split = strs[i].split(":");
					sql2.append(" t_hr_busitable where UseFlag=1 and ");
					sql2.append(" fieldsetid in( ");
					for (int j=1;j<split.length;j++) {
						sql2.append("'");
						sql2.append(split[j]);
						sql2.append("',");
					}
					sql2.setLength(sql2.length()-1);
					sql2.append(")");
				}else{
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
    		}
    		sql.append(")");
    		sql.append(" order by fieldSetId, Displayorder ");
			RowSet rs = dao.search(sql.toString());
			HashMap map = new HashMap();
			String fieldsetid="";
			String fieldsetdesc = "";
			while(rs.next()){
                map = new HashMap();
                fieldsetid = rs.getString("fieldsetid");
                fieldsetdesc = rs.getString("fieldsetdesc");
                if (fieldHm != null && !fieldHm.containsKey(fieldsetid))
                    continue;

                map.put("id", fieldsetid);
                map.put("text", fieldsetdesc);

                list.add(map);
			}
			if(hasY){
				rs = dao.search(sql2.toString());
				while(rs.next()){
	                map = new HashMap();
	                fieldsetid = rs.getString("fieldsetid");
	                fieldsetdesc = rs.getString("fieldsetdesc");
	
	                map.put("id", "Y:"+fieldsetid);
	                map.put("text", fieldsetdesc);
	
	                list.add(map);
				}
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
        ParameterSetBo paramBo = new ParameterSetBo(this.frameconn);
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
			if("ZP".equalsIgnoreCase(module)&&!value.contains("Y:")){
		        HashMap fieldsetMap= getZpFieldList(value.toLowerCase());
		        if(fieldsetMap != null)
		            fieldItemMap = (HashMap) fieldsetMap.get(value.toLowerCase());
		        
			}
			if("my_custom".equals(value)){
				HashMap<String,String> hm = new HashMap<String,String>();
				hm = new HashMap<String,String>();
	 	        hm.put("fieldsetid", "Z03");
	 	        hm.put("itemid", "z0321");
	 	        hm.put("itemdesc", "需求单位");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "UN");
	 	        list.add(hm);
	 	        hm = new HashMap<String,String>();
	 	        hm.put("fieldsetid", "Z03");
	 	        hm.put("itemid", "z0325");
	 	        hm.put("itemdesc", "需求部门");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "UM");
	 	        list.add(hm);
	 	        hm = new HashMap<String,String>();
	 	        hm.put("itemid", "suitable");
	 	        hm.put("itemdesc", "简历筛选");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "0");
	 	        list.add(hm);
	 	        hm = new HashMap<String,String>();
	 	        hm.put("itemid", "custom_name");
	 	        hm.put("itemdesc", "流程状态");
	 	        hm.put("itemtype", "A");
	 	        hm.put("codesetid", "0");
	 	        list.add(hm);
	 	        hm = new HashMap<String,String>();
	 	        hm.put("itemid", "recdate");
	 	        hm.put("itemdesc", "应聘时间");
	 	        hm.put("itemtype", "D");
	 	        hm.put("codesetid", "0");
	 	        list.add(hm);
	 	        return list;
			}
			buf.append(" select itemid,itemdesc,itemtype,fieldsetid,codesetid from ");
			if(!value.contains("Y:")){
				buf.append(" fielditem ");
			}else{
				buf.append(" t_hr_busifield ");
				value = value.split(":")[1];
			}
			buf.append(" where useflag=1 ");
			if(StringUtils.isNotEmpty(value)&&value.length()>0){
				buf.append("and fieldsetid=?");
				buf.append(" order by displayid");
				list.add(value);
			}
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(buf.toString(),list);
			list.clear();
			HashMap map = new HashMap();
			while(rs.next()){
                map = new HashMap();
                if (fieldItemMap != null && !fieldItemMap.containsKey(rs.getString("itemid").toLowerCase()))
                    continue;

                map.put("id", rs.getString("itemid"));
                map.put("fieldItemId", rs.getString("itemid"));
                map.put("text", rs.getString("itemdesc"));
                map.put("fieldItemType", rs.getString("itemtype"));
                map.put("fieldSetId", rs.getString("fieldsetid"));
                map.put("checked", false);
                map.put("leaf",Boolean.TRUE);
                list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
		
	}
	
    
    /**
     * 将招聘的指标权限参数转换为map的形式
     * 
     * @return
     * @throws GeneralException
     */
    public HashMap getZpFieldList(String fieldKey) throws GeneralException {
        RecordVo vo2= ConstantParamter.getConstantVo("ZP_FIELD_LIST");
        String strValue="";
        if(vo2!=null)
            strValue=vo2.getString("str_value");
        
        HashMap fieldSetMap = new HashMap();
        RowSet search = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        int idx = -1;
        try {
        	search = dao.search("select str_value from constant where constant='ZP_FIELD_LIST_JSON'");
        	String field = "";
            if(search.next()) {
                field = search.getString("str_value");
                field = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(field);
                        if (StringUtils.isNotEmpty(field)) {//例如：field = "A01{A0101[0#1],A0111#出生日期[0#0],A01AX#最高学历毕业日期[0#0],},"
                            JSONObject json = JSONObject.fromObject(field);
                            Iterator<String> it = json.keys(); 
                            HashMap fieldItemMap = new HashMap();
                            while(it.hasNext()){
                                // 获得key
                                String key = it.next(); 
                                JSONObject array = (JSONObject) json.get(key);
                                JSONArray fieldArray = (JSONArray)array.get(fieldKey.toUpperCase());
                                if(fieldArray == null)
                                	continue;
                                
                                for (int i = 0; i < fieldArray.size(); i++) {
                                    JSONObject fieldJson =  (JSONObject) fieldArray.get(i);
                                    String  id = (String) fieldJson.get("id");
                                    String in_list =  (String) fieldJson.get("in_list");
                                    String must =   (String) fieldJson.get("must");
                                    String name =   (String) fieldJson.get("name");
                                    fieldItemMap.put(id, in_list+"#"+must);
                                }
                                
                            }
                            fieldSetMap.put(fieldKey.toLowerCase(), fieldItemMap);
                            
                       }
            }else{
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
        	
        	
        	
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fieldSetMap;
    }

}
