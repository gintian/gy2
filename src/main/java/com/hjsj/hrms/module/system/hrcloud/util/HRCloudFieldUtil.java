package com.hjsj.hrms.module.system.hrcloud.util;

import com.hjsj.hrms.module.system.hrcloud.nationalstandard.NationalStandardPojo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.HashMap;

public class HRCloudFieldUtil {
	/**云中的国标指标**/
	private static JSONObject cloud_national_standard_json = new JSONObject();
	static{
		JSONObject nationalJson = new JSONObject();
		nationalJson.put("staffName", "姓名");
		nationalJson.put("gender", "性别");
		nationalJson.put("birthday", "出生日期");
		nationalJson.put("cvYear", "参加工作日期");
		nationalJson.put("reductionTime", "减员时间");
		nationalJson.put("education", "学历");
		nationalJson.put("degree", "学位");
		nationalJson.put("currentJob", "现任职务");
		nationalJson.put("inOfficeTime", "任职时间");
		nationalJson.put("sameRankTime", "任同职级时间");
		cloud_national_standard_json = nationalJson;
	}
	/**
	 * 判断是否为国标指标
	 * @param itemid
	 * @return
	 */
	public static boolean isNationalField(String itemid){
		return cloud_national_standard_json.containsKey(itemid);
	}
	/**
	 * 通过国标名称找到 hr系统中的指标
	 * @param itemid
	 * @param name
	 * @return
	 */
	public static String getHrNationalFieldbyCloudFieldId(Connection conn, String itemid,String name){
		if(itemid == null){
			return null;
		}
		if("staffName".equals(itemid)){
			itemid = "A0101";
		}else if("gender".equals(itemid)){
			itemid = "A0107";
		}else if("birthday".equals(itemid)){
			itemid = "A0111";
		}else if("cvYear".equals(itemid)){
			itemid = "A0141";
		}else if("mobile".equals(itemid)){
			//手机号
			RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", conn);
			String phone = null;
			if(vo!=null){
				phone=vo.getString("str_value");
			}
			itemid = phone;
			return itemid;
		}else if("email".equals(itemid)){
			//邮箱
			RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL", conn);
			String email = null;
			if(vo!=null){
				email=vo.getString("str_value");
			}
			itemid = email;
			return itemid;
		}else if("education".equals(itemid)){
			itemid = "A0405";
		}else if("degree".equals(itemid)){
			itemid = "A0440";
		}else if("inOfficeTime".equals(itemid)){
			itemid = "A0707";
		}else{
			return null;
		}
		/*
		else if("currentJob".equals(itemid)){
			return "A0101";
		}else if("reductionTime".equals(itemid)){
			return "A0101";
		}else if("currentJob".equals(itemid)){
			return "A0101";
		}
		hrItemId = getHrFielditem(itemid, name);
		 */
		if(isNationalField(itemid)){
			name=cloud_national_standard_json.getString(itemid);
		}
		if(itemid != null){
			FieldItem item = DataDictionary.getFieldItem(itemid);
			if(item == null){
				return null;
			}
			//判断系统指标与国标是否一致
			if(item.getItemdesc().equals(name)){
				return itemid;
			}else{
				return null;
			}
			
		}
		return itemid;
	}
	
	public static HashMap getMatchMap(Connection conn){
		HashMap returnMap = new HashMap();
		returnMap.put("staffName", getHrNationalFieldbyCloudFieldId(conn,"staffName", "姓名"));
		returnMap.put("gender", getHrNationalFieldbyCloudFieldId(conn,"gender", "性别"));
		returnMap.put("birthday", getHrNationalFieldbyCloudFieldId(conn,"birthday", "出生日期"));
		returnMap.put("cvYear", getHrNationalFieldbyCloudFieldId(conn,"cvYear", "参加工作日期"));
		returnMap.put("reductionTime", getHrNationalFieldbyCloudFieldId(conn,"reductionTime", "减员时间"));
		returnMap.put("mobile", getHrNationalFieldbyCloudFieldId(conn,"mobile", "手机号"));
		returnMap.put("email", getHrNationalFieldbyCloudFieldId(conn,"email", "邮箱"));
		returnMap.put("education", getHrNationalFieldbyCloudFieldId(conn,"education", "学历"));
		returnMap.put("degree", getHrNationalFieldbyCloudFieldId(conn,"degree", "学位"));
		returnMap.put("currentJob", getHrNationalFieldbyCloudFieldId(conn,"currentJob", "现任职务"));
		returnMap.put("inOfficeTime", getHrNationalFieldbyCloudFieldId(conn,"inOfficeTime", "任职时间"));
		returnMap.put("sameRankTime", getHrNationalFieldbyCloudFieldId(conn,"sameRankTime", "任同职级时间"));
		return returnMap;
	}
	/**
	 * 获取hr系统中的指标
	 * @param itemid
	 * @param name
	 * @return
	 */
	private static String getHrFielditem(String itemid,String name) {
		if(!isNationalField(itemid)){
			return null;
		}
		if(name == null || "".equals(name)){
			//TODO name为空处理
			return null;
		}
		String fielditemid = NationalStandardPojo.getNationalStandardItemid(name);
		
		if(fielditemid != null){
			FieldItem item = DataDictionary.getFieldItem(fielditemid);
			//判断系统指标与国标是否一致
			if(item.getItemdesc().equals(name)){
				return fielditemid;
			}else{
				return null;
			}
			
		}
		return fielditemid;
	}
}
