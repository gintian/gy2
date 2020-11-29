package com.hjsj.hrms.module.system.hrcloud.nationalstandard;

import net.sf.json.JSONObject;
/**
 * 国标指标对应名称类
 * @author xus
 *
 */
public class NationalStandardPojo {
	private static JSONObject A01Json = new JSONObject();
	private static JSONObject A04Json = new JSONObject();
	//姓名：itemid 键值对
	private static JSONObject A01ReJson = new JSONObject();
	private static JSONObject A04ReJson = new JSONObject();
	static{
		JSONObject a01Json = new JSONObject();
		a01Json.put("A0101", "姓名");
		a01Json.put("A0102", "外文姓名");
		a01Json.put("A0104", "曾用名");
		a01Json.put("A0107", "性别");
		a01Json.put("A0111", "出生日期");
		a01Json.put("A0112", "国籍");
		a01Json.put("A0114", "籍贯");
		a01Json.put("A0117", "出生地");
		a01Json.put("A0121", "民族");
		a01Json.put("A0124", "健康状况");
		a01Json.put("A0127", "婚姻状况");
		a01Json.put("A0131", "个人身份");
		a01Json.put("A0137", "家庭住址");
		a01Json.put("A0138", "住宅电话");
		a01Json.put("A0141", "参加工作日期");
		a01Json.put("A0142", "工作单位通信地址");
		a01Json.put("A0144", "进入本系统工作日期");
		a01Json.put("A0146", "电子邮箱");
		a01Json.put("A0147", "现身份起始日期");
		a01Json.put("A0148", "移动电话");
		a01Json.put("A0151", "连续工龄");
		a01Json.put("A0155", "组织机构用人类别");
		a01Json.put("A0161", "职业类别");
		a01Json.put("A0164", "从事专业");
		a01Json.put("A0167", "享受待遇级别");
		a01Json.put("A0171", "户籍所在地");
		a01Json.put("A0177", "公民身份号码");
		a01Json.put("A0181", "港澳台侨属标识");
		a01Json.put("A0182", "特殊项标识");
		a01Json.put("A0184", "专长");
		a01Json.put("A0185", "爱好");
		a01Json.put("A0187", "有效证件类别");
		a01Json.put("A0188", "证件号码");
		a01Json.put("A0194", "退出现役军人（武警）标识");
		a01Json.put("A0197", "参照公务员法管理标识");
		a01Json.put("A0198", "离岗退养标识");
		A01Json = a01Json;
		
		JSONObject a04Json = new JSONObject();
		a04Json.put("A0405", "学历");
		a04Json.put("A0440", "学位");
		A04Json = a04Json;
		
		
	}
	public static void setA01ReJson(JSONObject a01Json) {
		JSONObject a01ReJson = new JSONObject();
		for(Object o:a01Json.keySet()){
			a01ReJson.put(a01Json.get(o),o);
		}
		A01ReJson = a01ReJson;
	}
	public static void setA04ReJson(JSONObject a04Json) {
		JSONObject a04ReJson = new JSONObject();
		for(Object o:a04Json.keySet()){
			a04ReJson.put(a04Json.get(o),o);
		}
		A04ReJson = a04ReJson;
	}
	/**
	 * 获取指标对应的国家标准名称
	 * @param itemid
	 * @return
	 */
	public static String getNationalStandardName(String itemid){
		return isA01NationalStandardField(itemid)?A01Json.getString(itemid):null;
	}
	/**
	 * 判断国家标准个人信息中是否存在该指标
	 * @param itemid
	 * @return
	 */
	public static boolean isA01NationalStandardField(String itemid){
		return A01Json.containsKey(itemid);
	}
	/**
	 * 判断国家标准个人信息中是否存在该名称
	 * @param name
	 * @return
	 */
	public static boolean isA01NationalStandardName(String name){
		return A01ReJson.containsKey(name);
	}
	/**
	 * 获取指标对应的国家标准指标
	 * @param name
	 * @return
	 */
	public static String getNationalStandardItemid(String name){
		return isA01NationalStandardName(name)?A01ReJson.getString(name):null;
	}
}
