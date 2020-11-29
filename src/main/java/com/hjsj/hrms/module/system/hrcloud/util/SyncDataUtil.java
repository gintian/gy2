package com.hjsj.hrms.module.system.hrcloud.util;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.module.system.hrcloud.SyncDataThread;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 云同步工具类
 * @author xus
 */
public class SyncDataUtil {
	/**一次同步条数*/
	private static int PAGE_COUNT = 300; 
	private static Category log = Category.getInstance(SyncDataUtil.class.getName());
	/**正在执行同步状态*/
	private static boolean IS_SYNCING_FLAG = false;
	/**t_org_view表中parentGUIDKEY是否都为空*/
	private static boolean ORG_PARENTGUID_NOT_NULL = false;
	/**t_postorg_view表中parentGUIDKEY是否都为空*/
	private static boolean POST_PARENTGUID_NOT_NULL = false;
	/**云同步到hcm离职库参数，空为未配置，否则配置了离职库*/
	private static String SYNC_RET_DBNAME = "";
	/**组织机构新增的字母排序*/
	public static final String[] ADD_STR_ARRAY = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	/**数据视图中单位视图表名常量*/
	private static final String TABLE_T_ORG_VIEW = "t_org_view";
	/**数据视图中岗位表名常量*/
	private static final String TABLE_T_POST_VIEW = "t_post_view";
	/**数据视图中人员视图表名常量*/
	private static final String TABLE_T_HR_VIEW = "t_hr_view";

	public static void startSync(){
		SyncDataThread sdt = new SyncDataThread();
		sdt.start();
	}
	
	public static String getSYNC_RET_DBNAME() {
		return SYNC_RET_DBNAME;
	}

	public static void setSYNC_RET_DBNAME(String sYNC_RET_DBNAME) {
		SYNC_RET_DBNAME = sYNC_RET_DBNAME;
	}
	
	public static boolean getIS_SYNCING_FLAG() {
		return IS_SYNCING_FLAG;
	}
	
	public static void setIS_SYNCING_FLAG(boolean iS_SYNCING_FLAG) {
		IS_SYNCING_FLAG = iS_SYNCING_FLAG;
	}
	
	/**
	 * 判断t_org_view表中的parentGUIDKEY是否不全为空 
	 * @return true:不全为空;false:全为空
	 */
	public static boolean getORG_PARENTGUID_NOT_NULL() {
		if(ORG_PARENTGUID_NOT_NULL) {
			return ORG_PARENTGUID_NOT_NULL;
		}else {
			Connection conn = null;
			RowSet rs = null;
			try {
				conn = AdminDb.getConnection();
				String sql = "select count(1) parcount from t_org_view where parentGUIDKEY is not null ";
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql);
				if(rs.next()) {
					if(rs.getInt("parcount")>0) {
						ORG_PARENTGUID_NOT_NULL = true;
					}
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rs);
				PubFunc.closeDbObj(conn);
			}
		}
		return ORG_PARENTGUID_NOT_NULL;
	}
	/**
	 * 判断t_post_view表中的parentGUIDKEY是否不全为空 
	 * @return true:不全为空;false:全为空
	 */
	public static boolean getPOST_PARENTGUID_NOT_NULL() {
		if(POST_PARENTGUID_NOT_NULL) {
			return POST_PARENTGUID_NOT_NULL;
		}else {
			Connection conn = null;
			RowSet rs = null;
			try {
				conn = AdminDb.getConnection();
				String sql = "select count(1) parcount from t_post_view where parentGUIDKEY is not null ";
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql);
				if(rs.next()) {
					if(rs.getInt("parcount")>0) {
						POST_PARENTGUID_NOT_NULL = true;
					}
				}
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rs);
				PubFunc.closeDbObj(conn);
			}
		}
		return POST_PARENTGUID_NOT_NULL;
	}
	/**
	 * 同步人员到云平台
	 * @param conn
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @param tables 
	 */
	public static void syncEmptoCloud(Connection conn, String appId, String tenantId, String appSecret, JSONArray tables) {
		int topFailCount = 0;
		
		//1、获取同步人员的数量
		int count = getHrEmpCount(conn);
		
		//2、获取库中代码对应关系
		JSONObject codeRelationObj = getCloudCodeRelation(conn);
		if(count == 0){
			return ;
		}
		//初始化将hrcloud_id改为null
		resetHrcloudId(conn,TABLE_T_HR_VIEW);
		int page = count/PAGE_COUNT + 1;
		//回填数据视图状态的集合
		JSONArray totalArray = new JSONArray();
		for(int i = 0;i<page;i++){
			//2、获取hr人员数据1000条
			JSONArray jsonArray = getHrEmpSyncInfo(conn,i+1,codeRelationObj,tables,topFailCount);
			if(jsonArray.size()==0){
				return;
			}
			SyncCloudDataLoggerUtil.start("", jsonArray);
			//3、批量同步到云平台
			String result = batchSyncEmp(conn,jsonArray,appId,tenantId,appSecret,tables);
			if(result.indexOf("success")<0){
				return;
			}
			
			//4、记录到hr系统中
			syncRecordToHrSys(jsonArray,conn,result,3,null);
			
			totalArray .addAll(jsonArray);
		}	
		//5、将hrcloud_id改为null
		resetHrcloudId(conn,TABLE_T_HR_VIEW);
	}

	/**
	 * 记录系统同步日志
	 * @param jsonArray 
	 * @param conn 
	 * @param result
	 * @param state 1:新增机构；2:删除机构  3:同步人员；
	 * @param table 
	 * @return 
	 */
	private static void syncRecordToHrSys(JSONArray jsonArray, Connection conn, String result, int state, String table) {
		JSONArray exptidArray = new JSONArray();
		try {
			if(result.indexOf("success")<0){
				result = "{\"success\":false,\"errcode\":\"0001\",\"errmsg\":\"调用接口返回值格式错误\"}";
			}
			JSONObject resultObj = JSONObject.fromObject(result);
			boolean success = resultObj.getBoolean("success");
			String errcode = resultObj.getString("errcode");
			String errormsg = "";
			int status = 0;
			if(success){
				status = 1;
			}else{
				if("0002".equals(errcode)){
					status = 2;
				}
				if(resultObj.get("errmsg")!=null){
					errormsg = resultObj.getString("errmsg");
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date());
			IDFactoryBean id =new IDFactoryBean();
			String logid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
			int logtype = 1;
			String loginfo = "";
			if(state == 1){
				if(TABLE_T_ORG_VIEW.equals(table)){
					loginfo = "新增、更新机构同步";
				}else if(TABLE_T_POST_VIEW.equals(table)){
					loginfo = "新增、更新岗位同步";
				}
			}else if(state == 2){
				if(TABLE_T_ORG_VIEW.equals(table)){
					loginfo = "删除机构同步";
				}else if(TABLE_T_POST_VIEW.equals(table)){
					loginfo = "删除岗位同步";
				}
			}else if(state == 3){
				loginfo = "新增、更新、删除人员同步";
			}
			RecordVo vo = new RecordVo("t_sys_hrcloud_synclog");
			vo.setString("logid", logid);
			vo.setInt("logtype", logtype);
			vo.setString("loginfo", loginfo);
			vo.setInt("status", status);
			vo.setString("errormsg", errormsg);
			vo.setDate("syncdate", new Date());
			ContentDAO dao = new ContentDAO(conn);
			dao.addValueObject(vo);
			
			if(status == 1){
				syncDataToEhr(conn,jsonArray,state,null,table);
			}else if(status == 2){
				JSONArray failReport = resultObj.getJSONArray("failReport");
				logtype = state==1?2:state;
//				insertSql = " insert into t_sys_hrcloud_synclog (logid,logtype,loginfo,main_logid,status,errormsg,syncdate) values (?,?,?,?,?,?,"+Sql_switcher.sqlNow()+")";
				for(int i = 0;i<failReport.size();i++){
					JSONObject resultjson = failReport.getJSONObject(i);
					loginfo = resultjson.getString("id");
					String childlogid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
					errormsg = resultjson.getString("msg");
//					values = new ArrayList();
//					values.add(childlogid);
//					values.add(logtype);
//					values.add(loginfo);
//					values.add(logid);
////					values.add(time);
//					values.add(status);
//					values.add(errormsg);
//					dao.update(insertSql, values);
					
					
					RecordVo vo2 = new RecordVo("t_sys_hrcloud_synclog");
					vo2.setString("logid", childlogid);
					vo2.setInt("logtype", logtype);
					vo2.setString("loginfo", loginfo);
					vo2.setString("main_logid", logid);
					vo2.setInt("status", status);
					vo2.setString("errormsg", errormsg);
					vo2.setDate("syncdate", new Date());
					dao.addValueObject(vo2);
					exptidArray.add(loginfo);
				}
				syncDataToEhr(conn, jsonArray,state,exptidArray,table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回填hr系统视图同步状态
	 * @param conn
	 * @param jsonArray
	 * @param state
	 * @param object
	 * @param table2 
	 * @throws SQLException
	 */
	private static void syncDataToEhr(Connection conn, JSONArray jsonArray, int state, JSONArray object, String table2) throws SQLException {
		String table = "";
		String id = "id";
		if(state == 3){
			table = " t_hr_view ";
		}else{
			table = table2;
			id = "orgId";
		}
		String sql = "update "+table+" set hrcloud = '0' where ";
		String ids = "";
		for(Object o:jsonArray){
			JSONObject json = (JSONObject) o;
			//id是否存在
			boolean idexist = object == null||(json.containsKey(id)&&!object.contains(json.getString(id)));
			if(idexist){
				ids += "'"+json.getString(id)+"',";
			}
		}
		if(ids.length()>0){
			ids = ids.substring(0, ids.length()-1);
			sql += " unique_id in ("+ids+")";
		}else{
			sql += " 1=2 ";
		}
		log.debug("syncDataToEhr sql:"+sql);
		ContentDAO dao = new ContentDAO(conn);
		dao.update(sql);
	}

	/**
	 * 获取需要同步人员的信息
	 * @param conn
	 * @param page
	 * @param codeRelationObj 
	 * @param tables 
	 * @param topFailCount 
	 * @return
	 */
	private static JSONArray getHrEmpSyncInfo(Connection conn, int page, JSONObject codeRelationObj, JSONArray tables, int topFailCount) {
		JSONArray staffList = new JSONArray();
		RowSet rs = null;
		try{
			HashMap feldHrCloudMap = new HashMap();
			HashMap hrFieldItemToCloudCodesetMap = new HashMap();
			if(tables != null&&tables.size()>0){
				for(int z = 0;z<tables.size();z++){
					JSONObject fieldset = tables.getJSONObject(z);
					JSONArray fields = fieldset.getJSONArray("fields");
					for(int y = 0;y<fields.size();y++){
						JSONObject fielditem = fields.getJSONObject(y);
						JSONObject connectField = fielditem.getJSONObject("connectField");
//							if(fieldlist.contains(connectField.getString("itemid"))){
							feldHrCloudMap.put(connectField.getString("itemid"), fielditem.getString("id"));
							hrFieldItemToCloudCodesetMap.put(connectField.getString("itemid"), fielditem.getString("codesetid"));
//							}
					}
				}
			}
				
//		}
			
			//3、查询语句
//			String subsql = "SELECT unique_id FROM t_hr_view where hrcloud in (1,2,3) ";
//			subsql = Sql_switcher.sqlTop(subsql, PAGE_COUNT*page);
			
			String subsql = " SELECT unique_id FROM t_hr_view where hrcloud in (1,2,3) ";
			subsql = Sql_switcher.sqlTop(subsql, topFailCount);
			
			String sql = "select unique_id,o1.GUIDKEY unitId,o2.GUIDKEY deptId,o3.GUIDKEY posId,A0101 staffName,"
					+ "hrcloud operation ";
			DbWizard db = new DbWizard(conn);
			String hr_set = TABLE_T_HR_VIEW;
			for(Object obj:feldHrCloudMap.keySet()){
				if(obj == null ){
					continue;
				}
				String hr_field = (String)obj;
				FieldItem item = DataDictionary.getFieldItem(hr_field);
				if(!db.isExistField(hr_set, hr_field,false)){
					continue;
				}
				if("D".equals(item.getItemtype())){
					sql += ","+Sql_switcher.dateToChar(hr_field)+" "+hr_field;
//					sql += ","+Sql_switcher.dateToChar(hr_field,"YYYY-MM-DD HH24:MI:SS")+" "+hr_field;
				}else{
					sql += ","+hr_field+" "+hr_field;
				}
			}
			sql +=" from t_hr_view t LEFT JOIN organization o1 on t.B0110_0 = o1.codeitemid LEFT JOIN organization o2 on t.E0122_0 = o2.codeitemid LEFT JOIN organization o3 on t.E01A1_0 = o3.codeitemid where hrcloud in (1,2,3) and (hrcloud_id is null or hrcloud_id <> '1') ";
			sql +=" and t.unique_id not in ("+subsql+")";
//			sql += " and unique_id not in ("+subsql+") ";
//			sql = Sql_switcher.sqlTop(sql, PAGE_COUNT);
			
			sql = Sql_switcher.sqlTop(sql, PAGE_COUNT);
			log.debug("PAGE_COUNT:"+PAGE_COUNT+"  page:"+page);
			log.debug("t_hr_view sql:"+sql);
			ContentDAO dao = new ContentDAO(conn);
//			rs = dao.search(sql,PAGE_COUNT,1);
			rs = dao.search(sql);
			String updUnique = "";
			while(rs.next()){
				JSONObject json = new JSONObject();
				json.put("id", rs.getString("unique_id"));
				json.put("staffNum", rs.getString("unique_id"));//云中集成标识，单点登录用
				json.put("unitId", rs.getString("unitId"));
				json.put("deptId", rs.getString("deptId"));
				json.put("posId", rs.getString("posId"));
				json.put("staffName", rs.getString("staffName"));
				json.put("operation", rs.getInt("operation")-1+"");
				json.put("base", "0");
				ArrayList<String> codeJsonList = new ArrayList<String>();
				for(Object obj:feldHrCloudMap.keySet()){
					if(obj == null ){
						continue;
					}
					String hr_field = (String)obj;
					String cloud_field = (String) feldHrCloudMap.get(obj);
					FieldItem item = DataDictionary.getFieldItem(hr_field);
					if(!db.isExistField(hr_set, hr_field,false)){
						continue;
					}
					//性别单独处理
					if("gender".equals(feldHrCloudMap.get(hr_field))){
						String sex = rs.getString(hr_field);
						if("男".equals(sex)){
							sex = "1";
						}else if("女".equals(sex)){
							sex = "2";
						}
						json.put(cloud_field, sex);
						continue;
					}
					if("A".equals(item.getItemtype().toUpperCase())){
						if(!"0".equals(item.getCodesetid())){
							String codeset = item.getCodesetid();
							String codeitem =  rs.getString(hr_field);
							JSONObject datajson =new JSONObject();
							String cloudCodeSet = (String) hrFieldItemToCloudCodesetMap.get(hr_field);
							//xus 19/10/16 同步人员信息时 若对应了父代码，没对应子代码，则同步父代码。
							if(codeRelationObj.containsKey(cloudCodeSet)) {
								JSONObject codesetJson = codeRelationObj.getJSONObject(cloudCodeSet);
								if(codesetJson.containsKey(codeitem)) {
									//有云的代码
									JSONObject codeinfo = codesetJson.getJSONObject(codeitem);
									datajson.put("code", codeinfo.getString("codeitemid"));
									datajson.put("name", codeinfo.getString("codeitemdesc"));
									json.put(cloud_field, datajson);
									codeJsonList.add(cloud_field);
								}else {
									//没有云的代码，循环遍历获取父节点代码
									json.put(cloud_field, null);
								}
							}else {
								json.put(cloud_field, null);
							}
//							String relationkey = cloudCodeSet+"`"+codeset+"`"+codeitem;
//							if(!codeRelationObj.containsKey(relationkey)){
//								for(Object key : codeRelationObj.keySet()){
//									String codeRelationObjKey = (String)key;
//									if(relationkey.indexOf(codeRelationObjKey) == -1){
//										relationkey = codeRelationObjKey;
//									}
//								}
//							}
//							if(codeRelationObj.containsKey(cloudCodeSet+"`"+codeset+"`"+codeitem)){
//								JSONObject jSONObject = codeRelationObj.getJSONObject(cloudCodeSet+"`"+codeset+"`"+codeitem);
//								if(codeset.equals(jSONObject.getString("hr_codesetid"))){
//									if(jSONObject.containsKey("codeitemid")&&!"".equals(jSONObject.getString("codeitemid"))
//											&&jSONObject.containsKey("codeitemdesc")&&!"".equals(jSONObject.getString("codeitemdesc"))){
//										datajson.put("code", jSONObject.getString("codeitemid"));
//										datajson.put("name", jSONObject.getString("codeitemdesc"));
//										json.put(cloud_field, datajson);
//										codeJsonList.add(cloud_field);
//									}
//								}else{
//									json.put(cloud_field, null);
//								}
//							}else{
//								json.put(cloud_field, null);
//							}
						}else{
							json.put(cloud_field, rs.getString(hr_field));
						}
					}
					else if("D".equals(item.getItemtype().toUpperCase())){
						//xus 19/12/19 日期型为空时传null
						if(rs.getObject(hr_field) == null || StringUtils.isBlank(rs.getString(hr_field))) {
							json.put(cloud_field, null);
						}else {
							String value = "";
							int length = item.getItemlength();
							if(length==19) {
								value = rs.getString(hr_field);
							}else if(length==16) {
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
								Date date = format1.parse(rs.getString(hr_field)); 
								value = PubFunc.FormatDate(date,"yyyy-MM-dd HH:mm:ss");
							}else if(length==10) {
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
								Date date = format1.parse(rs.getString(hr_field)); 
								value = PubFunc.FormatDate(date,"yyyy-MM-dd HH:mm:ss");
							}else if(length==7) {
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");  
								Date date = format1.parse(rs.getString(hr_field)); 
								value = PubFunc.FormatDate(date,"yyyy-MM-dd HH:mm:ss");
							}else if(length==4) {
								SimpleDateFormat format1 = new SimpleDateFormat("yyyy");  
								Date date = format1.parse(rs.getString(hr_field)); 
								value = PubFunc.FormatDate(date,"yyyy-MM-dd HH:mm:ss");
							}if(length>19) {
								value = rs.getString(hr_field).substring(0, 19);
							}
							json.put(cloud_field, value);
						}
					}
					else{
						json.put(cloud_field, rs.getString(hr_field));
					}
				}
				String returnJsonStr =json.toString();
				if(codeJsonList.size()>0){
					for(String str:codeJsonList){
						JSONObject datajson = json.getJSONObject(str);
						String code =datajson.getString("code");
						String name =datajson.getString("name");
						
						String prevstr = returnJsonStr.substring(0,returnJsonStr.indexOf(str)+str.length()+2);
						String newStr = "\"{code:'"+code+"',name:'"+name+"'}\"";
						String nextstr = returnJsonStr.substring(returnJsonStr.indexOf("}", returnJsonStr.indexOf(str)+str.length()+1)+1);
						String newretstr = prevstr+newStr+nextstr;
						returnJsonStr = newretstr;
					}
				}
				staffList.add(returnJsonStr);
				if(rs.getObject("unique_id") != null) {
					updUnique += "'"+rs.getString("unique_id")+"',";
				}
			}
			//将查询后的数据状态置为'99'
			if(StringUtils.isNotBlank(updUnique)) {
				updUnique = updUnique.substring(0,updUnique.length()-1);
				String totalChangeSql = "update t_hr_view set hrcloud_id = '1' where unique_id in (";
				totalChangeSql = totalChangeSql +updUnique + ")";
				dao.update(totalChangeSql);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return staffList;
	}

	/**
	 * 获取hr与云端代码对应关系
	 * @param conn
	 * @return {codeitemid:code}
	 */
	private static JSONObject  getCloudCodeRelation(Connection conn) {
		String sql=" select t.codesetid codesetid,t.codeitemid codeitemid,t.codeitemdesc codeitemdesc,t.hr_codesetid hr_codesetid,c.codeitemid hr_codeitemid from codeitem c left join t_sys_hrcloud_codematch t on (c.codeitemid = t.hr_codeitemid or c.parentid = t.hr_codeitemid ) and t.hr_codesetid = c.codesetid  where c.codesetid = t.hr_codesetid";
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		JSONObject returnJson = new JSONObject();
		try {
			rs = dao.search(sql);
			while(rs.next()){
				String codesetid = rs.getString("codesetid");
				String codeitemid = rs.getString("codeitemid");
				String codeitemdesc = rs.getString("codeitemdesc");
				String hr_codesetid = rs.getString("hr_codesetid");
				String hr_codeitemid = rs.getString("hr_codeitemid");
				
				JSONObject codesetJSON = new JSONObject();
				if(returnJson.containsKey(codesetid)) {
					codesetJSON = returnJson.getJSONObject(codesetid);
				}
				
				JSONObject infoJSON = new JSONObject();
				infoJSON.put("codeitemid", codeitemid);
				infoJSON.put("codeitemdesc", codeitemdesc);
				infoJSON.put("hr_codesetid", hr_codesetid);
				infoJSON.put("hr_codeitemid", hr_codeitemid);
				codesetJSON.put(hr_codeitemid, infoJSON);
				
				returnJson.put(codesetid, codesetJSON);
//				JSONObject json = new JSONObject();
//				json.put("codeitemid", rs.getString("codeitemid"));
//				json.put("codeitemdesc", rs.getString("codeitemdesc"));
//				json.put("hr_codesetid", rs.getString("hr_codesetid"));
//				json.put("hr_codeitemid", rs.getString("hr_codeitemid"));
//				returnJson.put( rs.getString("codesetid")+"`"+rs.getString("hr_codesetid")+"`"+rs.getString("hr_codeitemid"), json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return returnJson;
	}

	/**
	 * 批量同步人员到云
	 * @param conn
	 * @param staffList
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @param tables
	 * @return
	 */
	public static String batchSyncEmp(Connection conn, JSONArray staffList, String appId, String tenantId,
			String appSecret, JSONArray tables) {
		String result = ""; 
		String url = SyncConfigUtil.IP_PORT+"/open/staff/update";
		
		JSONObject json = new JSONObject();
		json.put("appId", appId);
		JSONObject data = new JSONObject();
		data.put("tenantId", tenantId);
		data.put("staffList", staffList);
		String dataEncode = SyncConfigUtil.AESEncrypt(data.toString(), appSecret);
		json.put("data", dataEncode);
		ArrayList datalist = new ArrayList();
		datalist.add(appId);
		datalist.add(dataEncode);
		String signEncode = SyncConfigUtil.digest(datalist);
		json.put("sign", signEncode);
		result = SyncConfigUtil.postHttp(url, json.toString());
		return result;
	}

	/**
	 * 获取需要同步人员的数量
	 * @param conn
	 * @return
	 */
	public static int getHrEmpCount(Connection conn) {
		int count = 0; 
		String sql = "select COUNT(*) count from t_hr_view where hrcloud in (1,2,3)";
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return count;
	}

	/**
	 * 同步机构到云平台
	 * @param conn
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @param state 1:新增；2：删除
	 * @param table t_org_view或t_post_view
	 */
	public static void syncOrgtoCloud(Connection conn, String appId, String tenantId, String appSecret, int state,String table) {
		
		//1、获取同步机构的数量
		int count = getHrOrgCount(conn,state,table);
		if(count == 0){
			return;
		}
		int page = count/PAGE_COUNT +1;
		//回填数据视图状态的集合
		JSONArray totalArray = new JSONArray();
		//初始化将hrcloud_id改为null
		resetHrcloudId(conn,table);
		//循环执行同步
		for(int i = 0; i<page;i++){
			//2、获取hr机构数据1000条
			JSONArray jsonArray = getHrOrgSyncInfo(conn,i+1,state,table);
			if(jsonArray.size()==0){
				return;
			}
			SyncCloudDataLoggerUtil.start("", jsonArray);
			//3、批量同步到云平台
			String result = batchSyncOrg(conn,jsonArray,appId,tenantId,appSecret);
			//4、记录到hr系统中
			syncRecordToHrSys(jsonArray, conn,result,state,table);
			totalArray.addAll(jsonArray);
		}
		//5、将hrcloud_id改为null
		resetHrcloudId(conn,table);
	}
	
	/**
	 * 将hrcloud_id改为null
	 * @param conn
	 * @param table
	 */
	private static void resetHrcloudId(Connection conn, String table) {
		String updsql = "update "+table+" set hrcloud_id = null";
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.update(updsql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量同步到云平台
	 * @param conn
	 * @param jsonArray
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 */
	public static String batchSyncOrg(Connection conn, JSONArray structureList, String appId, String tenantId,
			String appSecret) {
		String result = ""; 
//		String url = "http://www.hjhrcloud.com/open/structure/update";
		String url = SyncConfigUtil.IP_PORT+"/open/structure/update";
		
		JSONObject json = new JSONObject();
		json.put("appId", appId);
		JSONObject data = new JSONObject();
		data.put("tenantId", tenantId);
		data.put("structureList", structureList);
		String dataEncode = SyncConfigUtil.AESEncrypt(data.toString(), appSecret);
		json.put("data", dataEncode);
		ArrayList datalist = new ArrayList();
		datalist.add(appId);
		datalist.add(dataEncode);
		String signEncode = SyncConfigUtil.digest(datalist);
		json.put("sign", signEncode);
		result = SyncConfigUtil.postHttp(url, json.toString());
		return result;
	}

/**
 * 获取hr同步机构的数量
 * @param conn
 * @param state 
 * @param table 
 * @return
 */
	public static int getHrOrgCount(Connection conn, int state, String table) {
		int count = 0;
		//查询同步机构总数 加上限制条件
		String sql = "select count(*) count from "+table;
		if(state == 1){
			sql += " where (hrcloud = 1 or hrcloud = 2) ";
		}else if(state == 2){
			sql += " where (hrcloud = 3) ";
		}
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return count;
	}

	/**
	 * 获取单页机构数据
	 * @param conn
	 * @param page
	 * @param state 状态：1：新增、修改 ；2：删除
	 * @param table 
	 * @param topFailCount 
	 * @return
	 */
	public static JSONArray getHrOrgSyncInfo(Connection conn,int page,int state, String table) {
		JSONArray jsonArray = new JSONArray();
		//xus 【56555】v77发版：云集成，HCM往云同步机构人员，同步到云平台，父级机构和子级机构显示为同级，日志显示系统存在多个直接父级
		//原因：数据视图没有初始化时parentGUIDKEY参数为null，改为到organization表中获取GUIDKEY
		String sql = "SELECT "
				+ "t.hrcloud AS operation , "
				+ "t.codesetid AS orgType, "
				+ "t.unique_id AS orgId, t.codeitemdesc AS orgName,t.parentGUIDKEY AS parentId, o.start_date AS startDate , o.end_date AS endDate, o.A0000 AS showOrder FROM "+table+" t ";
		if(TABLE_T_ORG_VIEW.equals(table)){	
			sql	+= " LEFT JOIN organization o ON t.b0110_0 = o.codeitemid  ";
		}else{
			sql	+= " LEFT JOIN organization o ON t.e01a1_0 = o.codeitemid  ";
		}
		sql += "WHERE (hrcloud_id is null or hrcloud_id <> '1') ";
		if(state == 1){
			sql += " and (hrcloud = 1 or hrcloud = 2) ";
		}else if(state == 2){
			sql += " and (hrcloud = 3) ";
		}
		if(TABLE_T_ORG_VIEW.equals(table)){	
			sql += " order by t.b0110_0 asc ";
		}else{
			sql	+= " order by t.e01a1_0 asc ";
		}
		
		log.debug("t_org_view t_post_view sql:"+sql);
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toUpperCase(),PAGE_COUNT,1);
//			rs = dao.search(sql.toUpperCase());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			JSONObject jsonObject = new JSONObject();
			String updUnique = "";
			while (rs.next()) {
				jsonObject = new JSONObject();
				if(StringUtils.isBlank(rs.getString("operation")) || StringUtils.isBlank(rs.getString("orgType"))) {
					continue;
				}
				String operation =  rs.getString("operation");
				if("1".equalsIgnoreCase(operation)) {
					operation = "0";
				}else if("2".equalsIgnoreCase(operation)) {
					operation = "1";
				}else if("3".equalsIgnoreCase(operation)) {
					operation = "2";
				}
				jsonObject.put("operation",operation);
				String orgType =  rs.getString("orgType");
				if("UN".equalsIgnoreCase(orgType)) {
					orgType = "1";
				}else if("UM".equalsIgnoreCase(orgType)) {
					orgType = "2";
				}else if("@K".equalsIgnoreCase(orgType)) {
					orgType = "3";
				}
				
				Date startDate = rs.getDate("startDate");
				Date endDate = rs.getDate("endDate");
				
				String sdate = "";
				String edate = "";
				if(startDate != null) {
					sdate = sdf.format(startDate);
				}
				if(endDate != null) {
					edate = sdf.format(endDate);
				}	
				
				jsonObject.put("orgType",orgType);
				jsonObject.put("orgId", rs.getObject("orgId")==null?"":rs.getString("orgId"));
				jsonObject.put("orgName", rs.getObject("orgName")==null?"":rs.getString("orgName"));
				jsonObject.put("parentId",rs.getObject("parentId")==null?"": rs.getString("parentId"));
				jsonObject.put("startDate", sdate);
				jsonObject.put("endDate", edate);
				jsonObject.put("showOrder", rs.getInt("showOrder"));
				jsonArray.add(jsonObject);
				if(rs.getObject("orgId")!=null) {
					updUnique += "'"+rs.getString("orgId")+"',";
				}
			}
			
			//将查询后的数据状态置为'99'
			if(StringUtils.isNotBlank(updUnique)) {
				updUnique = updUnique.substring(0,updUnique.length()-1);
				String totalChangeSql = "update "+table+" set hrcloud_id = '1' where unique_id in (";
				totalChangeSql = totalChangeSql +updUnique + ")";
				dao.update(totalChangeSql);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return jsonArray;
	}
	
	/**
	 * 新增单条数据日志
	 * @param conn
	 * @param data :
	 * {
	 * 		logid:string主键，可不填，
	 * 		logtype:int, 1：同步操作日志（HR->HJCLOUD）
						 2：机构数据日志（HR->HJCLOUD）
						 3、人员数据日志（HR->HJCLOUD）
						 4 、云调HR接口日志（HJCLOUD->HR ）
						 5、云调HR接口异常数据明细
			loginfo:日志信息 String,	logtype=1时，值为操作说明，如机构新增、机构删除等；
									logtype=2时，值为具体机构unique_id
									logtype=3时，值为具体人员unique_id
									logtype=4时，值为操作说明，如机构新增、机构删除等；
									logtype=5时，值为具体人员、机构unique_id
			main_logid:父日志id String,		当logtype=2|3|5时此列有值，记录所属父级操作日志id
			status:同步状态  int,	0：失败，1：成功，2：异常
			errormsg:错误详情 String,
	 * }
	 * @return
	 */
	public static boolean insertDataIntoOptLogTabel(Connection conn,ContentDAO dao,JSONObject data){
		boolean flag = false;
		try {
			String logid = "";
			if(data.containsKey("logid")){
				logid = data.getString("logid");
			}
			if(StringUtils.isEmpty(logid)){
				IDFactoryBean id =new IDFactoryBean();
				logid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
			}
			int logtype = data.getInt("logtype");
			String loginfo = data.getString("loginfo");
			String syncdate = Sql_switcher.sqlNow();
			int status = data.getInt("status");
			String errormsg = data.getString("errormsg");
			RecordVo vo = new RecordVo("t_sys_hrcloud_synclog");
			vo.setString("logid", logid);
			vo.setInt("logtype", logtype);
			vo.setString("loginfo", loginfo);
			if(data.containsKey("main_logid")){
				String main_logid = data.getString("main_logid");
				vo.setString("main_logid", main_logid);
			}
			vo.setDate("syncdate", new Date());
			vo.setInt("status", status);
			vo.setString("errormsg", errormsg);
			int i = dao.addValueObject(vo);
			if(i>0){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 获取guidkeyTOuserName的map    作废
	 */
	public static JSONObject getGUIDKEYToNbaseA0100Map(Connection conn,RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
			
	        HrSyncBo hsb = new HrSyncBo(conn);
		    String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
		    String[] dbnames = dbnamestr.split(",");
		    
			String sql = "";
			for(int i = 0 ; i < dbnames.length ; i++){
				if(i>0){
					sql += " union ";
				}
				sql += " select '"+dbnames[i]+"'"+Sql_switcher.concat()+"A0100 username,GUIDKEY GUIDKEY from "+dbnames[i]+"A01 ";
			}
			rs = dao.search(sql);
			while(rs.next()){
				if(StringUtils.isNotBlank(rs.getString("GUIDKEY"))){
					returnMap.put(rs.getString("GUIDKEY").toUpperCase(), rs.getString("username"));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 获取guidkeyTOuserName的map
	 */
	public static HashMap getNbaseA0100ByGUIDKEYMap(Connection conn,RowSet rs,HashSet GUIDKEYSet) {
		HashMap returnMap=new HashMap();
		try{
			ContentDAO dao = new ContentDAO(conn);
			
	        HrSyncBo hsb = new HrSyncBo(conn);
		    String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
		    String[] dbnames = dbnamestr.split(",");
		    
			String sql = "";
			ArrayList values = new ArrayList();
			
			StringBuffer wenhaos=new StringBuffer(""); 
			ArrayList valueList=new ArrayList();
			
			ArrayList<LazyDynaBean> list_fiveHundrer = new ArrayList<LazyDynaBean>();
			int count = 0;
			
			for(Iterator  t=GUIDKEYSet.iterator();t.hasNext();)
			{
				wenhaos.append(",?");
				valueList.add((String)t.next());
				count++;
				// 每500次循环查询一次
				if(count % 500 == 0) {
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("wenhaos", wenhaos.toString());
					bean.set("valueList", valueList);
					list_fiveHundrer.add(bean);
					wenhaos = new StringBuffer();
					valueList = new ArrayList();
					count = 0;
				}
			}
			if(count > 0) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("wenhaos", wenhaos.toString());
				bean.set("valueList", valueList);
				list_fiveHundrer.add(bean);
			}
			
			for(int j = 0; j < list_fiveHundrer.size(); j++) {
				String wenhaos_ = (String) list_fiveHundrer.get(j).get("wenhaos");
				ArrayList valueList_ = (ArrayList) list_fiveHundrer.get(j).get("valueList");
				sql = "";
				for(int i = 0 ; i < dbnames.length ; i++){
					if(i>0){
						sql += " union ";
					}
					sql += " select '"+dbnames[i]+"'"+Sql_switcher.concat()+"A0100 username,GUIDKEY GUIDKEY from "+dbnames[i]+"A01 where GUIDKEY in ("+wenhaos_.substring(1)+")";
					values.addAll(valueList_);
				}
				rs = dao.search(sql,values);
				while(rs.next()){
					if(StringUtils.isNotBlank(rs.getString("GUIDKEY"))){
						returnMap.put(rs.getString("GUIDKEY").toUpperCase(), rs.getString("username"));
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 获取guidkeyTOCodeitemId的map (单位部门岗位)
	 * @param rs 
	 * @param conn 
	 * @param codesetid
	 */
	public static JSONObject getGUIDKEYToObjectMap(Connection conn, RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
		    //不考虑虚拟组织机构
			String sql = " select GUIDKEY,codesetid,codeitemid,parentid,childid,grade,layer from organization  order by a0000 ";
			rs = dao.search(sql);
			while(rs.next()){
				if(StringUtils.isNotBlank(rs.getString("GUIDKEY"))){
					JSONObject data = new JSONObject();
					data.put("GUIDKEY", rs.getString("GUIDKEY"));
					data.put("codesetid", rs.getString("codesetid"));
					data.put("codeitemid", rs.getString("codeitemid"));
					data.put("parentid", rs.getString("parentid"));
					data.put("childid", rs.getString("childid"));
					data.put("grade", rs.getInt("grade"));
					data.put("layer", rs.getInt("layer"));
					returnMap.put(rs.getString("GUIDKEY").toUpperCase(),data);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 获取guidkeyTOCodeitemId的map (单位部门岗位)
	 * @param codesetid
	 */
	public static JSONObject getGUIDKEYToCodeitemIdMap(String codesetid,Connection conn,RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
		    //不考虑虚拟组织机构
			String sql = "";
			ArrayList values = new ArrayList();
			if(StringUtils.isBlank(codesetid)){
				sql = " select GUIDKEY,codeitemid from organization order by a0000 ";
				rs = dao.search(sql);
			}else{
				sql = " select GUIDKEY,codeitemid from organization where codesetid = ? order by a0000 ";
				values.add(codesetid);
				rs = dao.search(sql,values);
			}
			while(rs.next()){
				if(StringUtils.isNotBlank(rs.getString("GUIDKEY"))){
					returnMap.put(rs.getString("GUIDKEY").toUpperCase(), rs.getString("codeitemid"));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 通过Reciver、ExtFlag获取待办标识
	 * @param dao 
	 * @param conn
	 * @param rs 
	 * @param username
	 * @param creator
	 * @return 若为空则没有，否则有
	 * @throws SQLException 
	 */
	public static String getPeddIngIdByReciverAndExtFlag(ContentDAO dao, Connection conn, RowSet rs, String username, String creator) throws SQLException {
		String id = "";
		
		String sql = " select pending_id from t_hr_pendingtask where pending_type = 99 and receiver = ? and ext_flag = ? ";
		ArrayList values = new ArrayList();
		values.add(username);
		values.add(creator);
		rs = dao.search(sql,values);
		if(rs.next()){
			id = rs.getString("pending_id");
		}
		return id;
	}

	/**
	 * 获取组织机构下最大的a0000
	 * @param rs2 
	 * @param conn2 
	 * @return
	 */
	public static int getOrgMaxA0000(Connection conn, RowSet rs) {
		int A0000 = 1;
		String sql = " select MAX(a0000) maxA0000 from organization ";
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()){
				A0000 = rs.getInt("maxA0000");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return A0000;
	}

	/**
	 * 获取parentid与levelA0000的对应关系
	 * 根节点前边加 root`
	 * @param rs 
	 * @param conn 
	 * @return
	 */
	public static JSONObject getParentCodeitemToLevelA0000(Connection conn, RowSet rs) {
		JSONObject returnJson = new JSONObject();
		String sql = " select parentid,MAX(levelA0000) maxLevelA0000 from organization where codeitemid != parentid group by parentid ";
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				returnJson.put(rs.getString("parentid"), rs.getInt("maxLevelA0000"));
			}
			sql = " select parentid,MAX(levelA0000) maxLevelA0000 from organization where codeitemid = parentid group by parentid ";
			rs = dao.search(sql);
			while(rs.next()){
				returnJson.put("root`"+rs.getString("parentid"), rs.getInt("maxLevelA0000"));
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnJson;
	}

	
	public static String getOrgMaxRootCode(Connection conn, RowSet rs) {
		String returnStr = "01";
		String sql = " select MAX(codeitemid) codeitemid from organization where codeitemid = parentid ";
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()){
				returnStr = rs.getString("codeitemid");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public static JSONObject getOrgMaxCodeJson(Connection conn, RowSet rs) {
		JSONObject returnJson = new JSONObject();
		String sql = " select parentid,MAX(codeitemid) codeitemid from organization where codeitemid != parentid group by parentid ";
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				returnJson.put(rs.getString("parentid"), rs.getString("codeitemid"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnJson;
	}

	/**
	 * 获取组织机构新增的codeitemid
	 * @param codesetid
	 * @param maxCode
	 * @param parentid
	 * @param conn
	 * @return
	 * @throws GeneralException
	 */
	public static String getNewCodeItem(String codesetid , String maxCode, String parentid,Connection conn) throws GeneralException {
		String returnStr = "01";
		if("00".equals(maxCode) ){
			returnStr = parentid+"01";
		}else {
			if( maxCode.length() == 1){
				//根节点是一位时
				returnStr = getNextStr(maxCode);
			}else{
				String substr = maxCode;
				//最后一个字符
				String lastStr = substr.substring(substr.length()-1);
				String firstStr = substr.substring(substr.length()-2,substr.length()-1);
				String newLaststr = "";
				
				//最后一个字符加1
				newLaststr = getNextStr(lastStr);
				
				//加之前最后一个字符若果是9，则进位
				if(lastStr.equalsIgnoreCase(SyncDataUtil.ADD_STR_ARRAY[SyncDataUtil.ADD_STR_ARRAY.length-1])){
					returnStr = getNextStr(firstStr) +newLaststr;
				}else{
					returnStr = firstStr+newLaststr;
				}
			}
			returnStr = parentid+returnStr;
		}
		
		//新增组织机构记录
		RecordVo rec = new RecordVo("organization");
		rec.setString("codesetid", codesetid);
		rec.setString("codeitemid", returnStr);
		ContentDAO dao = new ContentDAO(conn);
		dao.addValueObject(rec);
		
		return returnStr;
	}

	private static String getNextStr(String substr) {
		String newStr = "0";
		for(int i = 0 ; i<SyncDataUtil.ADD_STR_ARRAY.length-1;i++){
			if(substr.equalsIgnoreCase(SyncDataUtil.ADD_STR_ARRAY[i])){
				newStr = SyncDataUtil.ADD_STR_ARRAY[i+1];
				break;
			}
		}
		return newStr;
	}

	/**
	 * 获取待办表中
	 * @param dao
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject getExtFlagToPeddIngId(ContentDAO dao, Connection conn, RowSet rs) throws SQLException {
		JSONObject retObj = new JSONObject();
		
		String sql = " select pending_id,ext_flag,pending_url from t_hr_pendingtask where pending_type = 99 ";
		rs = dao.search(sql);
		while(rs.next()){
			retObj.put(rs.getString("ext_flag").toUpperCase(), rs.getString("pending_id"));
//			retObj.put(rs.getString("pending_url"), rs.getString("pending_id"));
		}
		return retObj;
	}
	
	/**
	 * 获取待办表中PeddIngId
	 * @param dao
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static HashMap getPeddIngTaskIdMap(ContentDAO dao, Connection conn, RowSet rs,HashSet ext_flag_set) throws SQLException {
	 
		HashMap maps=new HashMap();
		
		try {
			StringBuffer flags=new StringBuffer(""); 
			ArrayList<String> valueList=new ArrayList<String>();
			ArrayList<LazyDynaBean> list_fiveHundrer = new ArrayList<LazyDynaBean>();
			int count = 0;
			for(Iterator  t=ext_flag_set.iterator();t.hasNext();)
			{
				flags.append(",?");
				valueList.add((String)t.next());
				count++;
				// 每500次循环查询一次
				if(count % 500 == 0) {
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("flags", flags.toString());
					bean.set("valueList", valueList);
					list_fiveHundrer.add(bean);
					flags = new StringBuffer();
					valueList = new ArrayList();
					count = 0;
				}
			} 
			if(count > 0) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("flags", flags.toString());
				bean.set("valueList", valueList);
				list_fiveHundrer.add(bean);
			}
			
			for(int i = 0; i < list_fiveHundrer.size(); i++) {
				String flags_ = (String) list_fiveHundrer.get(i).get("flags");
				ArrayList valueList_ = (ArrayList) list_fiveHundrer.get(i).get("valueList");
				String sql = " select pending_id,ext_flag,pending_url from t_hr_pendingtask where pending_type = 99 and ext_flag in ("+flags_.substring(1)+") ";
				rs = dao.search(sql,valueList_);
				while(rs.next()){
					maps.put(rs.getString("ext_flag").trim().toUpperCase(), rs.getString("pending_id"));
				}
			}
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		return maps;
	}

	/**
	 * 获取人员库中预新增的a0100、a0000
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject getMaxA0000AndA0100Map(Connection conn, RowSet rs) throws SQLException {
		JSONObject returnObj = new JSONObject();
		StringBuffer buf=new StringBuffer();
        int a0100 = 1;
        int a0000 = 1;
        String stra0100 = "00000001";
        ContentDAO dao = new ContentDAO(conn);
        
		buf.append("select max(a0100) as a0100,max(a0000) as a0000 from USRA01");
        rs = dao.search(buf.toString());
        if(rs.next())
        {
            String a0100value = rs.getString("a0100");
            if(a0100value!=null && a0100value.trim().length()>0) {
            	a0100 = Integer.parseInt(a0100value) + 1;
            }

            a0000 = rs.getInt("a0000")+1;
        }

        stra0100 = StringUtils.leftPad(String.valueOf(a0100), 8,"0");
        
        returnObj.put("a0100", stra0100);
        returnObj.put("a0000", a0000);
		return returnObj;
	}

	/**
	 * 获取人员信息表中GUIDKEY与A0100的对应关系
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public static JSONObject getGUIDKEYToA0100Map(Connection conn, RowSet rs) throws SQLException {
		JSONObject returnObj = new JSONObject();
		StringBuffer buf=new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        
		buf.append("select a0100,GUIDKEY from USRA01");
        rs = dao.search(buf.toString());
        while(rs.next())
        {
        	returnObj.put(rs.getString("GUIDKEY"), rs.getString("a0100"));
        }
		return returnObj;
	}
	
	/**
	 * 通过codeset获取代码表中存在的codeitemid，childid对应关系
	 * @param codesetid
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject codeitemToChildid(String codesetid, Connection conn, RowSet rs) throws SQLException {
		JSONObject returnJson = new JSONObject();
		String sql = "select codeitemid,childid from codeitem where codesetid = ? and flag = 9 and end_date > "+Sql_switcher.sqlNow();
		ArrayList values = new ArrayList();
		values.add(codesetid);
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql, values);
		while(rs.next()){
			returnJson.put(rs.getString("codeitemid"), rs.getString("childid")); 
		}
		return returnJson;
	}
	
	/**
	 * 获取子集中GUIDKEY与key,i9999的对应关系
	 * @param table
	 * @param key
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public static JSONObject subsetGuidkeyToKeyI9999(String table,String key, Connection conn, RowSet rs) throws SQLException{
		JSONObject returnJson = new JSONObject();
		String sql = " select "+key+",i9999,guidkey from "+table+" where guidkey is not null";
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql);
		while(rs.next()){
			JSONObject json = new JSONObject();
			json.put("key", rs.getString(key));
			json.put("i9999", rs.getString("i9999"));
			String guidkey = rs.getString("guidkey").toUpperCase();
			if(StringUtils.isNotBlank(guidkey)){
				returnJson.put(guidkey, json);
			}
		}
		return returnJson;
	}

	/**
	 * 获取子集中id与最大i9999的关系
	 * @param table
	 * @param primkey
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public static JSONObject subsetKeyToMaxI9999(String table, String key, Connection conn, RowSet rs) throws SQLException {
		JSONObject returnJson = new JSONObject();
		String sql = "select "+key+",MAX(I9999) maxi9999 from "+table+" group by "+key;
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql);
		while(rs.next()){
			returnJson.put(rs.getString(key), rs.getString("maxi9999"));
		}
		return returnJson;
	}
	
	/**
	 * 删除人员主集、组织架构时，同时删除子集、子节点
	 * @param type “0”机构；“1”：人员
	 * @param key 
	 * @param conn
	 * @param rs 
	 */
	public static void delSubRelationData(String type, String key, Connection conn, RowSet rs) {
		try {
			String delsql = "";
			ContentDAO dao = new ContentDAO(conn);
			if(StringUtils.isBlank(key)){
				return;
			}
			if("0".equals(type)){
				ArrayList sqllist = new ArrayList();
				String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where codeitemid like '"+key+"%'";
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					CodeItem item=new CodeItem();
					item.setCodeid(rs.getString("codesetid"));
					item.setCodename(rs.getString("codeitemdesc"));
				   	item.setPcodeitem(rs.getString("parentid"));
					item.setCcodeitem(rs.getString("childid"));
					item.setCodeitem(rs.getString("codeitemid"));
					item.setCodelevel(String.valueOf(rs.getInt("grade")));
		    		AdminCode.removeCodeItem(item);  
				}    
				//1、删除子节点
				delsql = "delete organization where codeitemid like '"+key+"%'";
				sqllist.add(delsql);
				ArrayList setlist1 = DataDictionary.getFieldSetList(1, 2);
				ArrayList setlist2 = DataDictionary.getFieldSetList(1, 3);
				for(Object o : setlist1){
					FieldSet set = (FieldSet)o;
					String table = set.getFieldsetid();
					sql = "delete "+table+" where b0110 like '"+key+"%'";
					sqllist.add(delsql);
				}
				for(Object o : setlist2){
					FieldSet set = (FieldSet)o;
					String table = set.getFieldsetid();
					sql = "delete "+table+" where e01a1 like '"+key+"%'";
					sqllist.add(sql);
				}
				dao.batchUpdate(sqllist);
			}
			/*不存在云同步人员信息到hcm
			 * else if("1".equals(type)){ ArrayList setlist =
			 * DataDictionary.getFieldSetList(1, 1); for(Object o : setlist){ FieldSet set =
			 * (FieldSet)o; String table = set.getFieldsetid(); delsql =
			 * "delete "+table+" where a0100 = '"+key+"'"; setlist.add(delsql); }
			 * dao.batchUpdate(setlist); }
			 */
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getParamJson(){
		JSONObject paramJson = new JSONObject();
		RecordVo recordvo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
		if (recordvo != null) {
			String str = recordvo.getString("str_value");
			if(!"".equals(str)){
				paramJson = JSONObject.fromObject(str);
				if(paramJson.get("appId") == null){
					paramJson.put("appId", "");
				}
				if(paramJson.get("tenantId") == null){
					paramJson.put("tenantId", "");
				}
				if(paramJson.get("appSecret") == null){
					paramJson.put("appSecret", "");
				}
				if(paramJson.get("tables") == null){
					paramJson.put("tables", null);
				}
				if(paramJson.get("cloudTOhr") == null){
					paramJson.put("cloudTOhr", null);
				}
				if(paramJson.get("frequency") == null){
					paramJson.put("frequency", null);
				}
				if(paramJson.get("used") == null){
					paramJson.put("used", "0");
				}
				if(paramJson.get("retdbname") == null){
					paramJson.put("retdbname", "");
				}
			}
		}
		if(paramJson.isEmpty()){
			paramJson.put("appId", "");
			paramJson.put("tenantId", "");
			paramJson.put("appSecret", "");
			paramJson.put("tables", null);
			paramJson.put("cloudTOhr", null);
			paramJson.put("frequency", null);
			paramJson.put("used", "0");
			paramJson.put("retdbname", "");
		}
		return paramJson;
	}

	/**
	 * 获取组织机构中codeitemid与childid的对应关系
	 * @param conn
	 * @param rs
	 * @return
	 */
	public static JSONObject getCodeitemidToObjectMap(Connection conn, RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
		    //不考虑虚拟组织机构
			String sql = " select codesetid,codeitemid,childid from organization  order by a0000 ";
			rs = dao.search(sql);
			while(rs.next()){
				JSONObject data = new JSONObject();
				data.put("codesetid", rs.getString("codesetid"));
				data.put("codeitemid", rs.getString("codeitemid"));
				String childid = rs.getString("codeitemid");
				if(rs.getString("childid") != null && !"null".equalsIgnoreCase(rs.getString("childid")) && !"".equalsIgnoreCase(rs.getString("childid"))) {
					childid = rs.getString("childid");
				}
				data.put("childid", childid);
				returnMap.put(rs.getString("codeitemid"),data);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	/**
	 * 删除子集数据
	 * @param conn
	 * @param rs
	 * @param table
	 * @param primkey 主集id名称：'a0100','b0110','e01a1'
	 * @param primaKeyValue 主集id值：'00000009'...
	 * @param i9999
	 * @return
	 */
	public static int delSubSetData(Connection conn, RowSet rs, String table, String primkey, String primaKeyValue ,String i9999) {
		int i = 0;
		//不考虑虚拟组织机构
		String sql = " delete  from "+table+" where "+primkey+" = '"+primaKeyValue+"'";
		if(StringUtils.isNotEmpty(i9999)){
			sql += " and i9999 = "+i9999;
		}
		try{
			ContentDAO dao = new ContentDAO(conn);
			i = dao.update(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 获取codeitem表中 codeitemid与corCode的对应关系
	 * @param codesetid
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject codeitemTocorCode(String codesetid, Connection conn, RowSet rs) throws SQLException {
		JSONObject returnJson = new JSONObject();
		String sql = "select codeitemid,corCode from codeitem where codesetid = ? and flag = 9 and end_date > "+Sql_switcher.sqlNow();
		ArrayList values = new ArrayList();
		values.add(codesetid);
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql, values);
		while(rs.next()){
			returnJson.put(rs.getString("codeitemid"), rs.getString("corCode")); 
		}
		return returnJson;
	}

	/**
	 * parentid中 子节点的数量
	 * @param codesetid
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public static JSONObject parentidToChildArray(String codesetid, Connection conn, RowSet rs) throws SQLException {
		JSONObject returnJson = new JSONObject();
		String sql = "select codeitemid,parentid from codeitem where codesetid = ? and flag = 9 and end_date > "+Sql_switcher.sqlNow()+" and parentid <> codeitemid order by parentid,codeitemid";
//		String sql = "select parentid,COUNT(parentid) count from codeitem where codesetid = ? and flag = 9 and end_date > "+Sql_switcher.sqlNow()+" and parentid <> codeitemid group by parentid ";
		ArrayList values = new ArrayList();
		values.add(codesetid);
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql, values);
		while(rs.next()){
			String parentid = rs.getString("parentid");
			JSONArray arr = new JSONArray();
			if(returnJson.containsKey(parentid)){
				arr = returnJson.getJSONArray(parentid);
			}
			arr.add(rs.getString("codeitemid"));
			returnJson.put(parentid,arr); 
		}
		return returnJson;
	}

	/**
	 * 删除codeitem中的子节点
	 * @param codesetid
	 * @param codeitemid 
	 * @param conn
	 * @param rs
	 * @throws SQLException 
	 */
	public static void delChildCode(String codesetid, String codeitemid, Connection conn, RowSet rs) throws SQLException {
		String sql = "delete from codeitem where codesetid = '"+codesetid+"' and flag = 9 and end_date > "+Sql_switcher.sqlNow()+" and codeitemid like '"+codeitemid+"%' ";
		ContentDAO dao = new ContentDAO(conn);
		dao.update(sql);
	}

	public static String getMinChildId(Connection conn, RowSet rs, String parentid,String codesetid) throws SQLException {
		String childId = parentid;
		String sql = " select MIN(codeitemid) mincode from organization where parentid = ? and parentid <> codeitemid and codeitemid <> ? ";
		ContentDAO dao = new ContentDAO(conn);
		ArrayList values = new ArrayList();
		values.add(parentid);
		values.add(codesetid);
		rs = dao.search(sql,values);
		if(rs.next()){
			childId = rs.getString("mincode");
		}
		return childId;
	}

	/**
	 * 获取存在的 主键集合
	 * @param table
	 * @param conn
	 * @param rs
	 * @param primkey
	 * @return
	 * @throws SQLException 
	 */
	public static JSONArray getExistedPrimaryKeys(String table, Connection conn, RowSet rs, String primkey) throws SQLException {
		JSONArray returnArray = new JSONArray();
		String sql = "select "+primkey+" from "+table;
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql);
		while(rs.next()) {
			returnArray.add(rs.getString(primkey));
		}
		return returnArray;
	}

	/**
	 * 批量刷新AdminCode
	 * @param conn
	 * @param batchJson  {'add':[{'codesetid':'XX','codeitemid':'XX',...},{...},...],'upd':[{'codesetid':'XX','codeitemid':'XX',...},{...},...],'del':[{'codesetid':'XX','codeitemid':'XX',...},{...},...]} 
	 */
	public static void batchRefAdminCodes(JSONObject batchJson) {
		if(batchJson == null) {
			return;
		}
		if(batchJson.containsKey("add") && batchJson.getJSONArray("add") != null && !batchJson.getJSONArray("add").isEmpty()) {
			JSONArray array = batchJson.getJSONArray("add");
			for(int i = 0 ; i < array.size() ; i++) {
				JSONObject json = array.getJSONObject(i);
				if(!json.containsKey("codeitemdesclen") ||
						!json.containsKey("codesetid") ||
						!json.containsKey("codeitemid") ||
						!json.containsKey("codeitemdesc") ||
						!json.containsKey("parentid") ||
						!json.containsKey("grade") ) {
					continue;
				}
				
				int codeitemdesclen = json.getInt("codeitemdesclen");
				String codesetid = json.getString("codesetid");
				String codeitemid = json.getString("codeitemid");
				String codeitemdesc = json.getString("codeitemdesc");
				String parentid = json.getString("parentid");
				String grade = String.valueOf(json.getInt("grade"));
				
				CodeItem item=new CodeItem();
				 item.setCodeid(codesetid);
				 item.setCodeitem(codeitemid.toUpperCase());
				 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
				 item.setPcodeitem(parentid.toUpperCase());
				 item.setCcodeitem(codeitemid.toUpperCase());
				 item.setCodelevel(grade);
				 AdminCode.addCodeItem(item);
				 AdminCode.updateCodeItemDesc(codesetid,codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
			}
		}
		if(batchJson.containsKey("upd") && batchJson.getJSONArray("upd") != null && !batchJson.getJSONArray("upd").isEmpty()) {
			JSONArray array = batchJson.getJSONArray("upd");
			for(int i = 0 ; i < array.size() ; i++) {
				JSONObject json = array.getJSONObject(i);
				if(!json.containsKey("codeitemdesclen") ||
						!json.containsKey("codesetid") ||
						!json.containsKey("codeitemid") ||
						!json.containsKey("codeitemdesc") ) {
					continue;
				}
				int codeitemdesclen = json.getInt("codeitemdesclen");
				String codesetid = json.getString("codesetid");
				String codeitemid = json.getString("codeitemid");
				String codeitemdesc = json.getString("codeitemdesc");
				
				String parentid = json.getString("parentid");
				String grade = String.valueOf(json.getInt("grade"));
				
				CodeItem item=new CodeItem();
				 item.setCodeid(codesetid);
				 item.setCodeitem(codeitemid.toUpperCase());
				 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
				 item.setPcodeitem(parentid.toUpperCase());
				 item.setCcodeitem(codeitemid.toUpperCase());
				 item.setCodelevel(grade);
				 AdminCode.addCodeItem(item);
				 
				AdminCode.updateCodeItemDesc(codesetid,codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
			}
		}
		if(batchJson.containsKey("del") && batchJson.getJSONArray("del") != null && !batchJson.getJSONArray("del").isEmpty()) {
			JSONArray array = batchJson.getJSONArray("del");
			for(int i = 0 ; i < array.size() ; i++) {
				JSONObject json = array.getJSONObject(i);
				if(!json.containsKey("codeitemdesclen") ||
						!json.containsKey("codesetid") ||
						!json.containsKey("codeitemid") ||
						!json.containsKey("codeitemdesc") ||
						!json.containsKey("parentid") ||
						!json.containsKey("grade") ) {
					continue;
				}
				int codeitemdesclen = json.getInt("codeitemdesclen");
				String codesetid = json.getString("codesetid");
				String codeitemid = json.getString("codeitemid");
				String codeitemdesc = json.getString("codeitemdesc");
				String parentid = json.getString("parentid");
				String grade = String.valueOf(json.getInt("grade"));
				
				CodeItem item=new CodeItem();
				item.setCodeid(codesetid);
				item.setCodeitem(codeitemid.toUpperCase());
				item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
				item.setPcodeitem(parentid.toUpperCase());
				item.setCcodeitem(codeitemid.toUpperCase());
				item.setCodelevel(grade);
	    		AdminCode.removeCodeItem(item);  
			}
		}
	}

	/**
	 * 批量删除组织机构
	 * @param conn
	 * @param rs
	 * @param delCodeitemids
	 * @return
	 */
	public static ArrayList batchDelOrg(Connection conn, RowSet rs, ArrayList<String> delCodeitemids) {
//		JSONObject returnJson = new JSONObject();
		ArrayList voList = new ArrayList();
		if(delCodeitemids.isEmpty()) {
			return voList;
		}
		try {
			String delsql = "";
			String whereSql = "";
			for(int i = 0;i<delCodeitemids.size() ; i++) {
				String itemid = delCodeitemids.get(i);
				if(i > 0 ) {
					whereSql += " or ";
				}
				whereSql += " codeitemid like '"+itemid+"%' ";
			}
			ContentDAO dao = new ContentDAO(conn);
			ArrayList sqllist = new ArrayList();
			String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where "+whereSql;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				RecordVo vo = new RecordVo("organization");
				vo.setString("codesetid", rs.getString("codesetid"));
				vo.setString("codeitemdesc", rs.getString("codeitemdesc"));
				vo.setString("parentid", rs.getString("parentid"));
				vo.setString("childid", rs.getString("childid"));
				vo.setString("codeitemid", rs.getString("codeitemid"));
				vo.setInt("grade", rs.getInt("grade"));
				voList.add(vo);
			}    
			if(voList.isEmpty()) {
				return voList;
			}
			//1、删除子节点
			delsql = "delete organization where "+whereSql;
			sqllist.add(delsql);
			
			//2、删除单位、岗位表中对应数据
			String b0110Where = "";
			String e01a1Where = "";
			for(int i = 0;i<delCodeitemids.size() ; i++) {
				String itemid = (String) delCodeitemids.get(i);
				if(i > 0 ) {
					b0110Where += " or ";
					e01a1Where += " or ";
				}
				b0110Where += " b0110 like '"+itemid+"%' ";
				e01a1Where += " e01a1 like '"+itemid+"%' ";
			}
			ArrayList setlist1 = DataDictionary.getFieldSetList(1, 2);
			ArrayList setlist2 = DataDictionary.getFieldSetList(1, 3);
			for(Object o : setlist1){
				FieldSet set = (FieldSet)o;
				String table = set.getFieldsetid();
				sql = "delete "+table+" where "+b0110Where;
				sqllist.add(delsql);
			}
			for(Object o : setlist2){
				FieldSet set = (FieldSet)o;
				String table = set.getFieldsetid();
				sql = "delete "+table+" where "+e01a1Where;
				sqllist.add(sql);
			}
			dao.batchUpdate(sqllist);
			
//			returnJson.put("delList",voList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return voList;
	}

	/**
	 * 获取organization中corCode与codeitemid的对应关系
	 * @param conn
	 * @param rs
	 * @return
	 */
	public static JSONObject getCorCodeGUIDKEYToCodeitemIdMap(Connection conn, RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
		    //不考虑虚拟组织机构
			String sql = "";
			ArrayList values = new ArrayList();
			sql = " select corCode,codeitemid from organization where corCode is not null and corCode <> '' order by a0000 ";
			rs = dao.search(sql);
			while(rs.next()){
				if(StringUtils.isNotBlank(rs.getString("corCode"))){
					returnMap.put(rs.getString("corCode").toUpperCase(), rs.getString("codeitemid"));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	public static JSONObject getCorCodeGUIDKEYToObjectMap(Connection conn, RowSet rs) {
		JSONObject returnMap = new JSONObject();
		try{
			ContentDAO dao = new ContentDAO(conn);
		    //不考虑虚拟组织机构
			String sql = "";
			ArrayList values = new ArrayList();
			sql = " select corCode,codesetid,codeitemid,parentid,childid,grade,layer from organization where corCode is not null and corCode <> '' order by a0000 ";
			rs = dao.search(sql);
			while(rs.next()){
				if(StringUtils.isNotBlank(rs.getString("corCode"))){
					JSONObject data = new JSONObject();
					data.put("corCode", rs.getString("corCode"));
					data.put("codesetid", rs.getString("codesetid"));
					data.put("codeitemid", rs.getString("codeitemid"));
					data.put("parentid", rs.getString("parentid"));
					data.put("childid", rs.getString("childid"));
					data.put("grade", rs.getInt("grade"));
					data.put("layer", rs.getInt("layer"));
					returnMap.put(rs.getString("corCode").toUpperCase(),data);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 获取子集guidkey为null的i9999
	 * @param table
	 * @param primkey
	 * @param conn
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject subsetKeyNullGuidkeyToI9999(String table, String primkey, Connection conn, RowSet rs) throws SQLException {
		JSONObject returnJson = new JSONObject();
		String sql = "select "+primkey+",i9999 from "+table+" where guidkey is null order by I9999 desc ";
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql);
		while(rs.next()){
			if(!returnJson.containsKey(rs.getString(primkey))) {
				returnJson.put(rs.getString(primkey), rs.getString("i9999"));
			}
		}
		return returnJson;
	}

	/**
	 * 删除掉子集中不在传入数据中的其他记录
	 * @param conn
	 * @param rs
	 * @param subExistIdI9999Array
	 */
	public static void delNotInSubSetData(Connection conn, JSONArray subExistIdI9999Array) {
		ContentDAO dao = new ContentDAO(conn);
		try {
		for(Object o : subExistIdI9999Array) {
			JSONObject existSubSetJson = (JSONObject)o;
			String table = existSubSetJson.getString("table");
			String primkey = existSubSetJson.getString("primkey");
			String primaKeyValue = existSubSetJson.getString("primaKeyValue");
			JSONArray existI9999Array = existSubSetJson.getJSONArray("i9999");
			if(!existI9999Array.isEmpty()) {
				String subinSql = "and i9999 not in (";
				for(Object obj : existI9999Array) {
					int i9999 = Integer.parseInt(obj.toString());
					subinSql+=i9999+",";
				}
				if(!subinSql.isEmpty()) {
					subinSql = subinSql.substring(0, subinSql.length()-1);
					subinSql += ")";
				}
				String sql = " delete from "+table+" where "+primkey+" = '"+primaKeyValue+"' "+subinSql;
				dao.update(sql);
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	
}
