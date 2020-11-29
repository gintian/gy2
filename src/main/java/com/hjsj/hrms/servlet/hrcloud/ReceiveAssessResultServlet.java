package com.hjsj.hrms.servlet.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReceiveAssessResultServlet extends HttpServlet{
	private String appSecret = null;
	private String appId = null;
	
	public String getAppSecret() {
		if(this.appSecret == null){
			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			if (recordVo != null) {
				String str = recordVo.getString("str_value");
				if(!"".equals(str)){
					JSONObject json = JSONObject.fromObject(str);
					if(json.get("appSecret") != null){
						this.appSecret = json.getString("appSecret");
					}
				}
			}
		}
		return appSecret;
	}
	public String getAppId() {
		if(this.appId == null){
			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			if (recordVo != null) {
				String str = recordVo.getString("str_value");
				if(!"".equals(str)){
					JSONObject json = JSONObject.fromObject(str);
					if(json.get("appId") != null){
						this.appId = json.getString("appId");
					}
				}
			}
		}
		return appId;
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject respJson = new JSONObject();
		JSONObject dataJson = new JSONObject();
		
		InputStream is= null;
		try{
			is = request.getInputStream();
			String bodyInfo = IOUtils.toString(is, "utf-8");
			if(bodyInfo != null && !"".equals(bodyInfo)){
				JSONObject reqJson = JSONObject.fromObject(bodyInfo);
				if(reqJson.containsKey("data")){
//					String appId = reqJson.getString("appId");
					String data = reqJson.getString("data");
					String sign = reqJson.getString("sign");
					
					String resData = SyncConfigUtil.AESDecrypt(data,getAppSecret());
					JSONObject resDataJson = JSONObject.fromObject(resData);
					
					JSONArray list = resDataJson.getJSONArray("list");
					int type = resDataJson.getInt("type");
					if(type == 3){
						respJson = syncDataToSys(list);
						SyncAssessDataLoggerUtil.start("考核数据同步", list, "success="+String.valueOf(respJson.getBoolean("success")));
					}else if(type == 4){
						respJson = CancelAssessResult(list);
						SyncAssessDataLoggerUtil.start("考核数据撤销", list, "success="+String.valueOf(respJson.getBoolean("success")));
					}else{
						respJson.put("success", false);
						respJson.put("code", "1001");
						respJson.put("msg", "未找到有效业务类型");
						respJson.put("data","");
						SyncAssessDataLoggerUtil.start("考核数据同步", new JSONArray(), "success=false");
					}
					respJson.put("data",data);
					respJson.put("sign",sign);
				}
			}
			if(respJson.size()==0){
				respJson.put("success", false);
				respJson.put("code", "1001");
				respJson.put("msg", "未获取到数据");
				respJson.put("data","");
				respJson.put("sign","");
				SyncAssessDataLoggerUtil.start("考核数据同步", new JSONArray(), "success=false");
			}
			respJson.put("appId",getAppId());
			JSONObject dataJson1 = new JSONObject();
			dataJson1.put("success", respJson.getBoolean("success"));
			String dataEncode = SyncConfigUtil.AESEncrypt(dataJson1.toString(), getAppSecret());
			respJson.put("data", dataEncode);
			ArrayList datalist = new ArrayList();
			datalist.add(getAppId());
			datalist.add(dataEncode);
			datalist.add(respJson.getString("success"));
			datalist.add(respJson.getString("code"));
			datalist.add(respJson.getString("msg"));
//			String signEncode = SyncConfigUtil.digest(json);
			String signEncode = SyncConfigUtil.digest(datalist);
//			String sign = 
			respJson.put("sign",signEncode);
			response.getWriter().write(respJson.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
	}
	/**
	 * 将数据同步到系统中
	 * @param list
	 * @return 
	 */
	public JSONObject syncDataToSys (JSONArray list){
		/*
		 * code:
		 * 错误码（success为false时有值）
		 * 1001:全部数据未同步成功
		 * 1002:部分数据未同步成功
		 * 1003:系统异常
		 * 1004:未获取到数据
		 * errmsg:
		 * 错误信息（success为false时有值）
		 * data:（json数组）
		 * code为1002时有值。返回导入失败的考核数据错误信息列表，格式：[{id: '1', msg: '唯一标识缺失'},…]
		 */
		Connection conn = null;
		boolean success = true;
		String code = "";
		JSONArray failReport = new JSONArray();
		int id = 0;
		
		JSONObject returnJson = new JSONObject();
		HashMap returnMap = SyncConfigUtil.getHrAssessConnectConfig();
		JSONObject orgAssessSet = (JSONObject) returnMap.get("orgAssessSet");
		JSONObject empAssessSet = (JSONObject) returnMap.get("empAssessSet");
		
//		HashMap orgReturnMap = getInsertSql(orgAssessSet);
//		String insertOrgSql = (String) orgReturnMap.get("insertSql");
//		HashMap orgConnectedFields = (HashMap) orgReturnMap.get("connectedFields");
//		
//		HashMap empReturnMap = getInsertSql(empAssessSet);
//		String insertEmpSql = (String) orgReturnMap.get("insertSql");
//		HashMap empConnectedFields = (HashMap) orgReturnMap.get("connectedFields");

		//判断机构、人员子集是否配置
		String isOrgEmp = "";
		if(!"".equals(orgAssessSet.getString("setid"))){
			isOrgEmp += "0";
		}
		if(!"".equals(empAssessSet.getString("setid"))){
			isOrgEmp += ",1";
		}
		if(isOrgEmp.length() == 0){
			returnJson.put("success", false);
			returnJson.put("code", "1001");
			returnJson.put("msg", "未配置考核结果集");
			returnJson.put("data","" );
			return returnJson;
		}
		//机构、人员自家是否判断过对应
		boolean orgJudged = false;
		boolean empJudged = false;
		
		boolean isorgfieldconnected = isNoConnected(orgAssessSet);
		boolean isempfieldconnected = isNoConnected(empAssessSet);
		if(!isorgfieldconnected){
			success = false;
			code = "1002";
			JSONObject failReportJson = new JSONObject();
			id++;
			failReportJson.put("id", String.valueOf(id));
			failReportJson.put("msg", "未配置机构考核指标");
			failReport.add(failReportJson);
		}
		if(!isempfieldconnected){
			success = false;
			code = "1002";
			JSONObject failReportJson = new JSONObject();
			id++;
			failReportJson.put("id", String.valueOf(id));
			failReportJson.put("msg", "未配置人员考核指标");
			failReport.add(failReportJson);
		}
		
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
						/**
		 	setid:’BAA’ //考核结果子集
			businessId:’BAA01’, //考核项目id
			name:’’,//考核项目名称
			type:’’,//项目类型
			planDate:’’//计划日期
			planYear:’’,//考核年度
			
			sheetName:’’,//考核表名称
			score:’’,//综合分数
			degreeName:’’//结果等级

						 */
			for(Object obj :list){
				JSONObject jsonmap = (JSONObject)obj;
				String businessId = stringIsNull(jsonmap.getString("businessId"))?"":jsonmap.getString("businessId");
				String tenantId = jsonmap.getString("tenantId");
				String name = stringIsNull(jsonmap.getString("name"))?"":jsonmap.getString("name");
				int type = jsonmap.getInt("type");
				//日期型 TODO 与云 对接日期格式
				String planDate = jsonmap.getString("planDate");
				int planYear = jsonmap.getInt("planYear");
				JSONArray objects = jsonmap.getJSONArray("objects");
				for(Object o :objects){
					JSONObject object = (JSONObject)o;
					String objectId = object.getString("objectId");
					String thirdSysId = object.getString("thirdSysId");
					int objectType = object.getInt("objectType");
					if(isOrgEmp.indexOf(String.valueOf(objectType))<-1){
						if(!orgJudged&&objectType == 0){
							success = false;
							code = "1002";
							JSONObject failReportJson = new JSONObject();
							id++;
							failReportJson.put("id", String.valueOf(id));
							failReportJson.put("msg", "未配置机构考核结果集");
							failReport.add(failReportJson);
							orgJudged = true;
						}
						if(!empJudged&&objectType == 1){
							success = false;
							code = "1002";
							id++;
							JSONObject failReportJson = new JSONObject();
							failReportJson.put("id", String.valueOf(id));
							failReportJson.put("msg", "未配置人员考核结果集");
							failReport.add(failReportJson);
							empJudged = true;
						}
						continue;
					}
					if((!isorgfieldconnected&&objectType == 0)||(!isempfieldconnected&&objectType == 1)){
						continue;
					}
					String objectName = object.getString("objectName");
					String unitName = object.getString("unitName");
					String deptName = object.getString("deptName");
					String posName = object.getString("posName");
					String sheetName = stringIsNull(object.getString("sheetName"))?"":object.getString("sheetName");
					JSONArray objectScores = object.getJSONArray("objectScores");
					JSONArray dimScores = object.getJSONArray("dimScores");
					JSONArray compScores = object.getJSONArray("compScores");
					//只记录综合得分数据
					for(Object scoreinfo : objectScores){
						JSONObject scoreinfoJson = (JSONObject)scoreinfo;
						String bodyGroupName = scoreinfoJson.getString("bodyGroupName");
						if(!"0".equals(bodyGroupName)){
							continue;
						}
						double score = scoreinfoJson.getDouble("score");
						String degreeName = stringIsNull(scoreinfoJson.getString("degreeName"))?"":scoreinfoJson.getString("degreeName");
						
						//获取人员/机构主键及子集序号
						
						JSONObject assessSet = null;
						String table = "";
						//TODO 指标未配置全是否要记录到系统中？
						if(objectType == 0){
							assessSet =  orgAssessSet;
						}else{
							//TODO 人员库设置？
							assessSet =  empAssessSet;
						}
						table = assessSet.getString("setid");
						//TODO 人员库
						HashMap infomap = SyncConfigUtil.getSetInfo(conn,"USR",table,objectType,thirdSysId);
						if("".equals(infomap.get("id"))){
							success = false;
							code = "1002";
							JSONObject failReportJson = new JSONObject();
							id++;
							failReportJson.put("id", String.valueOf(id));
							failReportJson.put("msg", "\""+thirdSysId+"\"在系统中不存在对应");
							failReport.add(failReportJson);
							continue;
						}
						
						String fieldid = (String) infomap.get("id");
						String dbname = (String) infomap.get("dbname");
						table = dbname+table;
						int index = (Integer) infomap.get("index");
						
						String insertSql = " insert into "+table+" (";
						/**
				 	setid:’BAA’ //考核结果子集
					businessId:’BAA01’, //考核项目id
					name:’’,//考核项目名称
					type:’’,//项目类型
					planDate:’’//计划日期
					planYear:’’,//考核年度
					
					sheetName:’’,//考核表名称
					score:’’,//综合分数
					degreeName:’’//结果等级

					 */
						String columns = "";
						String values = "";
						ArrayList valueList = new ArrayList();
						if(!"".equals(assessSet.get("businessId"))){
							columns += assessSet.get("businessId")+",";
							values += "?,";
							valueList.add(businessId);
						}
						if(!"".equals(assessSet.get("name"))){
							columns += assessSet.get("name")+",";
							values += "?,";
							valueList.add(name);
						}
						if(!"".equals(assessSet.get("type"))){
							columns += assessSet.get("type")+",";
							values += "?,";
							valueList.add(type);
						}
						if(assessSet.get("planDate")!=null&&!stringIsNull(planDate)){
							String datetype="yyyy-MM-dd H:m:s";
							/*if(value.length()==4){
								datetype="yyyy";
							}else if(value.length()==7){
								datetype="yyyy-MM";
							}else if(value.length()==10){
								datetype="yyyy-MM-dd";
							}else if(value.length()==13){
								datetype="yyyy-MM-dd H";
							}else if(value.length()==16){
								datetype="yyyy-MM-dd H:m";
							}else if(value.length()==19){
								datetype="yyyy-MM-dd H:m:s";
							}*/
							SimpleDateFormat sdf = new SimpleDateFormat(datetype);
							Date objvalue =DateUtils.getSqlDate(sdf.parse(planDate));
							
							columns += assessSet.get("planDate")+",";
							values += "?,";
							valueList.add(objvalue);
						}
						if(!"".equals(assessSet.get("planYear"))){
							columns += assessSet.get("planYear")+",";
							values += "?,";
							valueList.add(planYear);
						}
						if(!"".equals(assessSet.get("sheetName"))){
							columns += assessSet.get("sheetName")+",";
							values += "?,";
							valueList.add(sheetName);
						}
						if(!"".equals(assessSet.get("score"))){
							columns += assessSet.get("score")+",";
							values += "?,";
							valueList.add(score);
						}
						if(!"".equals(assessSet.get("degreeName"))){
							columns += assessSet.get("degreeName")+",";
							values += "?,";
							valueList.add(degreeName);
						}
						if(columns.length()==0){
							continue;
						}
						
						if(objectType == 0){
							columns += "B0110,";
						}else{
							columns += "A0100,";
						}
						values += "?,";
						valueList.add(fieldid);
						
						columns += "I9999,";
						values += "?,";
						valueList.add(index+1);
						
						columns = columns.substring(0,columns.length()-1);
						values = values.substring(0,values.length()-1);
						
						insertSql = insertSql + columns + " ) values ("+values+")";
					    
						dao.update(insertSql, valueList);
					
				}
			}
		}
			returnJson.put("success", success);
			returnJson.put("code", code);
			returnJson.put("msg", "");
			returnJson.put("data",failReport.toString());
		} catch (Exception e) {
			returnJson.put("success", false);
			returnJson.put("code", "1003");
			returnJson.put("msg", "系统异常"+e.getMessage());
			returnJson.put("data","" );
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		return returnJson;
	}
	
	private JSONObject CancelAssessResult(JSONArray list) {
		JSONObject returnJson = new JSONObject();
		
		Connection conn = null;
		
		boolean success = true;
		String code = "";
		String msg = "";
		JSONArray failReport = new JSONArray();
		int id = 0;
		
		
		HashMap returnMap = SyncConfigUtil.getHrAssessConnectConfig();
		JSONObject orgAssessSet = (JSONObject) returnMap.get("orgAssessSet");
		JSONObject empAssessSet = (JSONObject) returnMap.get("empAssessSet");
		String orgtable = orgAssessSet.getString("setid");
		//TODO 人员库？
		String emptable = "USR"+empAssessSet.getString("setid");
		String orgbusinessIdField = orgAssessSet.getString("businessId");
		String empbusinessIdField = empAssessSet.getString("businessId");
		
		if("".equals(orgtable)&&"".equals(emptable)){
			returnJson.put("success", false);
			returnJson.put("code", "1001");
			returnJson.put("msg", "未配置考核结果集");
			returnJson.put("data","" );
			return returnJson;
		}
		if("".equals(orgbusinessIdField)&&"".equals(empbusinessIdField)){
			returnJson.put("success", false);
			returnJson.put("code", "1001");
			returnJson.put("msg", "未配置考核项目标识");
			returnJson.put("data","" );
			return returnJson;
		}
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql = "";
			String businessIds = "";
			for(Object o :list){
				JSONObject data = (JSONObject)o;
				String businessId = data.getString("businessId");
				if(!"".equals(businessIds)){
					businessIds += ",";
				}
				businessIds += "'"+businessId+"'";
			}
			
			if(!"".equals(orgtable)&&!"".equals(orgbusinessIdField)){
				sql = "delete from "+orgtable+" where "+orgbusinessIdField+" in ("+businessIds+")";
				dao.update(sql);
			}else{
				success = false;
				code = "1001";
				msg = "未配置机构考核结果集";
			}
			if(!"".equals(emptable)&&!"".equals(empbusinessIdField)){
				sql = "delete from "+emptable+" where "+empbusinessIdField+" in ("+businessIds+")";
				dao.update(sql);
			}else{
				success = false;
				code = "1001";
				msg = "未配置人员考核结果集";
			}
			returnJson.put("success", success);
			returnJson.put("code", code);
			returnJson.put("msg", msg);
			returnJson.put("data",failReport.toString() );
		}catch (Exception e) {
			returnJson.put("success", false);
			returnJson.put("code", "1003");
			returnJson.put("msg", "系统异常"+e.getMessage());
			returnJson.put("data","" );
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		return returnJson;
	}

/**
 * 判断子集指标是否关联
 * @param empAssessSet 
 * @param i
 * @return
 */
	private boolean isNoConnected(JSONObject empAssessSet) {
		for(Object o:empAssessSet.keySet()){
			String key = (String) o;
			if("setid".equals(key)){
				continue;
			}
			if(!"".equals(empAssessSet.getString(key))){
				return true;
			}
		}
		return false;
	}

	private boolean stringIsNull(Object object){
		if(object == null || "null".equals(object)||"".equals(object))
			return true;
		else
			return false;
	}
	
}
