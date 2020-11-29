package com.hjsj.hrms.module.system.hrcloud.businessobject.impl;

import com.hjsj.hrms.businessobject.dtgh.CodeUtilBo;
import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.module.system.hrcloud.businessobject.ReceiveHjCloudInfo;
import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncDataUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hjsj.weixin.message.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.*;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 接收云数据接口实现
 * @author xus
 * 19/9/27
 */
public class ReceiveHjCloudInfoImpl implements ReceiveHjCloudInfo{
	private static Category log = Category.getInstance(ReceiveHjCloudInfoImpl.class.getName());
	/**
	 * 云访问数据
	 */
	private JSONObject resDataJson;
	
	/**
	 * 单位、部门、岗位主键的集合{table:['01','0101'],table:['010101','01010101']}
	 */
	private JSONObject primaryKeysMap;
	/**
	 * @return the resDataJson
	 */
	public JSONObject getResDataJson() {
		return resDataJson;
	}


	/**
	 * @param resDataJson the resDataJson to set
	 */
	public void setResDataJson(JSONObject resDataJson) {
		this.resDataJson = resDataJson;
	}

	/**
	 * 构造
	 * @param resDataJson
	 */
	public ReceiveHjCloudInfoImpl(JSONObject resDataJson) {
		this.resDataJson = resDataJson;
	}
	
	/**
	 * 执行
	 */
	@Override
    public JSONObject doExcute(){
		Connection conn = null;
		RowSet rs = null;
		JSONObject retJson = new JSONObject();
		HashMap metaDataJson = new HashMap();
		try{
			conn = AdminDb.getConnection();
			JSONArray list = resDataJson.getJSONArray("list");
			int type = resDataJson.getInt("type");
			
			//存到数据日志
			saveDataLog();
			//1人员,2单位,3部门,4岗位,5职位,6岗位序列
			String syncType = "";
			if(type == 1){
				//1待办
				retJson = addSchedule(conn,rs);
			}else if(type == 2){
				//2待办处理结果
				retJson = updSchedule(conn,rs);
			}else if(type == 3){
				//3考核结果发布
				retJson = assessmentResultRelease(conn,rs);
	//			SyncAssessDataLoggerUtil.start("考核数据同步", list, "success="+String.valueOf(retJson.getBoolean("success")));
			}else if(type == 4){
				//4考核结果取消发布
				retJson = cancelAssessResult(list,conn,rs);
	//			SyncAssessDataLoggerUtil.start("考核数据撤销", list, "success="+String.valueOf(retJson.getBoolean("success")));
			}else {
//				if(type == 5){
//					//5人员变动
//	//				retJson = cloudEmpChangeToHr(list);
//					syncType = "1";
//				}else
					if(type == 6){
					//6组织架构变动
				}else if(type == 7){
					//7待办提醒   已无此项
				}else if(type == 8){
					//8单位变动
					syncType = "2";
				}else if(type == 9){
					//9部门变动
					syncType = "3";
				}else if(type == 10){
					//10岗位变动
					syncType = "4";
				}else if(type == 11){
					//11职位变动  已无此项
				}else if(type == 12){
					//12岗位序列变动
		//			syncType = "6";
				}else{
					
				}
				
				//1、获取云与hr指标对应
				JSONObject cloudTOHrfieldsMap = new JSONObject();
				if(type != 6){
					cloudTOHrfieldsMap = getCloudEmpConnected(syncType,conn,rs);
				}
				
				//2、解析数据为元数据  type=1\2\3\4 单独处理
				metaDataJson = analysisDataToMetadata(cloudTOHrfieldsMap,conn,rs);
				
				//3、批量写入到数据库
				retJson = batchWriteIntoDataBase(metaDataJson,conn,rs);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		return retJson;
	}
	
	/**
	 * 批量记录操作日志
	 * @param logErrJson  操作数据库失败数据
	 * @param datas 校验失败数据
	 * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 * @param conn 
	 * @param dao 
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	private void batchLogErrOpration(JSONObject logErrJson, JSONArray datas, Connection conn, ContentDAO dao) throws GeneralException, SQLException {
		String addinfo = logErrJson.getString("addinfo");
		JSONArray logList = logErrJson.getJSONArray("logList");
		if(logList.isEmpty() && datas.isEmpty() && StringUtils.isEmpty(addinfo)){
			JSONObject data = new JSONObject();
			IDFactoryBean id =new IDFactoryBean();
			String parentlogid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
			data.put("logid", parentlogid);
			data.put("logtype", 4);
			data.put("status", 1);
			data.put("loginfo", ResourceFactory.getProperty("hrcloud.recieve.error.cloudtohrsuccess")+ResourceFactory.getProperty("hrcloud.recieve.error.success"));
			data.put("errormsg", "");
			SyncDataUtil.insertDataIntoOptLogTabel(conn, dao, data);
			return;
		}
		
		//记录操作数据库失败日志
		StringBuffer errormsg = new StringBuffer();
		if(addinfo.indexOf("a")>-1){
			errormsg.append( ResourceFactory.getProperty("hrcloud.recieve.error.insert")+",");
		}
		if(addinfo.indexOf("u")>-1){
			errormsg.append( ResourceFactory.getProperty("hrcloud.recieve.error.edit")+",");
		}
		if(addinfo.indexOf("d")>-1){
			errormsg.append( ResourceFactory.getProperty("hrcloud.recieve.error.delete")+",");
		}
		if(errormsg.length() >0 ){
			errormsg.deleteCharAt(errormsg.lastIndexOf(","));
			errormsg.append( ResourceFactory.getProperty("hrcloud.recieve.error.fail"));
		}
		
		ArrayList addlist = new ArrayList();
		//新增父节点
		JSONObject data = new JSONObject();
		IDFactoryBean id =new IDFactoryBean();
		String parentlogid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
		data.put("logid", parentlogid);
		data.put("loginfo", "");
		data.put("logtype", 4);
		data.put("status", 2);
		data.put("errormsg", errormsg);
		
		SyncDataUtil.insertDataIntoOptLogTabel(conn, dao, data);
		
		//记录校验失败日志子节点
		for(Object o:datas){
			JSONObject dataJson = (JSONObject)o;
			RecordVo vo = new RecordVo("t_sys_hrcloud_synclog");
			vo.setString("logid", id.getId("t_sys_hrcloud_synclog.Id","", conn));
			vo.setInt("logtype", 5);
			vo.setInt("status", 2);
			vo.setString("loginfo", dataJson.getString("id"));
			vo.setString("main_logid", parentlogid);
			data.put("errormsg", dataJson.getString("msg"));
			addlist.add(vo);
		}
		//新增操作数据库失败子节点
		for(Object o : logList){
			data = (JSONObject)o;
			RecordVo vo = new RecordVo("t_sys_hrcloud_synclog");
			vo.setString("logid", id.getId("t_sys_hrcloud_synclog.Id","", conn));
			vo.setInt("logtype", 5);
			vo.setInt("status", 2);
			vo.setString("loginfo", data.getString("loginfo"));
			vo.setString("main_logid", parentlogid);
			data.put("errormsg", ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail"));
			addlist.add(vo);
		}
		dao.addValueObject(addlist);
	}

	
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
	/**
	 * 批量同步到数据库中
	 * @param metaDataJson
	 * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 * @param rs 
	 * @param conn2 
	 * @return
	 */
	private JSONObject batchWriteIntoDataBase(HashMap metaDataJson, Connection conn, RowSet rs) {
		int type = resDataJson.getInt("type");
		//操作日志只记录30天的数据
		String delLogSql = "delete from t_sys_hrcloud_synclog where "+Sql_switcher.diffDays(Sql_switcher.sqlNow(),"syncdate")+"> 30 ";
		JSONObject retJson = new JSONObject();
		JSONArray logList = new JSONArray();
		JSONArray datas = new JSONArray();
		JSONArray returnData = new JSONArray();
		String addinfo = "";//"a,u,d"
		JSONObject data = new JSONObject();
		JSONObject logErrJson = new JSONObject();
		HashMap adminsJson = new HashMap();//需要更新的AdminCode的json ：{'add':[//RecordVo {'codesetid':'XX','codeitemid':'XX',...},{...},...],'upd':[{'codesetid':'XX','codeitemid':'XX',...},{...},...],'del':[{'codesetid':'XX','codeitemid':'XX',...},{...},...]}
		//数据视图t_org_view回填的集合
		ArrayList orgViewList = new ArrayList();
		//数据视图t_org_view物理删除回填的集合
		ArrayList delOrgViewList = new ArrayList();
		
		String code = "";
		StringBuffer errmsg = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(conn);
			dao.update(delLogSql);
			
			if(metaDataJson.containsKey("success") && !(Boolean)metaDataJson.get("success")){
				if(metaDataJson.containsKey("code") && "1001".equals((String)metaDataJson.get("code"))){
					//未配置云与hr系统的指标对应
					retJson.put("success",false);
					retJson.put("code","1001");
					retJson.put("data",returnData);
					retJson.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.noconnectedfield"));
					IDFactoryBean id =new IDFactoryBean();
					String parentlogid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
					data.put("logid", parentlogid);
					data.put("logtype", 4);
					data.put("status", 0);
					data.put("loginfo", ResourceFactory.getProperty("hrcloud.recieve.error.cloudtohrsuccess"));
					data.put("errormsg", ResourceFactory.getProperty("hrcloud.recieve.error.noconnectedfield"));
					SyncDataUtil.insertDataIntoOptLogTabel(conn,new ContentDAO(conn), data);
					return retJson;
				}
				if(metaDataJson.containsKey("datas") && metaDataJson.get("datas") != null ){
					datas = (JSONArray)metaDataJson.get("datas");
				}
			}
			JSONArray addArray = (JSONArray)metaDataJson.get("addArray");
			JSONArray updArray = (JSONArray)metaDataJson.get("updArray");
			JSONArray delArray = (JSONArray)metaDataJson.get("delArray");
			HashMap changeDatas =  (HashMap)metaDataJson.get("changeDatas");
			JSONObject idGUIDMap =  (JSONObject)metaDataJson.get("idGUIDMap");
			//数据视图t_org_view逻辑删除回填的集合
			ArrayList orgDelList = new ArrayList();
			if(metaDataJson.containsKey("orgDelList")) {
				orgDelList = (ArrayList) metaDataJson.get("orgDelList");
			}
			JSONObject childInfoJson = new JSONObject();
			if(metaDataJson.containsKey("childInfoJson")){
				childInfoJson = (JSONObject)metaDataJson.get("childInfoJson");
			}
			//子集中云同步的数据
			JSONArray subExistIdI9999Array = new JSONArray();
			if(metaDataJson.containsKey("subExistIdI9999Array")){
				subExistIdI9999Array = (JSONArray)metaDataJson.get("subExistIdI9999Array");
			}
			
			//xus 20/1/6  【55671】潍柴云集成：查看同步详情，云同步到HCM，同步情况只显示了{}
			if(addArray.size()==0 && updArray.size()==0 && delArray.size()==0) {
				String logerrormsg = "";
				for(int i = 0;i<datas.size();i++) {
					JSONObject dataobj = (JSONObject) datas.get(i);
					if(dataobj.containsKey("msg") && StringUtils.isNotBlank( dataobj.getString("msg"))) {
						if(logerrormsg.length() > 0) {
							logerrormsg += ";";
						}
						logerrormsg += dataobj.getString("msg");
					}
				}
				retJson.put("success",true);
				retJson.put("code",code);
				retJson.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.cloudtohrsuccess")+ResourceFactory.getProperty("hrcloud.recieve.error.fail"));
				retJson.put("data",datas);
				IDFactoryBean id =new IDFactoryBean();
				String parentlogid = id.getId("t_sys_hrcloud_synclog.Id","", conn);
				data.put("logid", parentlogid);
				data.put("logtype", 4);
				data.put("status", 0);
				data.put("loginfo", ResourceFactory.getProperty("hrcloud.recieve.error.cloudtohrsuccess"));
				data.put("errormsg", logerrormsg);
				SyncDataUtil.insertDataIntoOptLogTabel(conn,new ContentDAO(conn), data);
				return retJson;
			}
			//批量新增
			for(Object o :addArray){
				String uuid = (String)o;
				String guidkey = "";
				if(idGUIDMap.containsKey(uuid)){
					guidkey = idGUIDMap.getString(uuid);
				}
				RecordVo vo = (RecordVo)changeDatas.get(uuid);
				int i = dao.addValueObject(vo);
				if(i<1){
					code = "1002";
					if(errmsg.length()==0){
						errmsg.append(ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail")+"|ids:|");
					}else{
						errmsg.append(",");
					}
					errmsg.append(guidkey);
					data.put("logtype", 5);
					data.put("loginfo", guidkey);
					data.put("status", 2);
					data.put("errormsg", ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail"));
					logList.add(data);
					if(!addinfo.contains("a")){
						addinfo += ",a";
					}
					JSONObject obj = new JSONObject();
					obj.put("uuid", uuid);
					obj.put("orgNum", guidkey);
					obj.put("success", false);
					if(childInfoJson.containsKey(uuid)){
						obj.put("parentid", childInfoJson.getString(uuid));
					}
					returnData.add(obj);
				}else{
					JSONObject obj = new JSONObject();
					obj.put("uuid", uuid);
					obj.put("orgNum", guidkey);
					obj.put("success", true);
					if(childInfoJson.containsKey(uuid)){
						obj.put("parentid", childInfoJson.getString(uuid));
					}
					returnData.add(obj);
					
					if(!adminsJson.containsKey("add")) {
						adminsJson.put("add", new ArrayList());
					}
					((ArrayList)adminsJson.get("add")).add(vo);
//					if(type == 5) {
//						empViewList.add(vo);
//					}else {
//						orgViewList.add(vo);
//					}
					orgViewList.add(vo);
				}
			}
			//批量修改
			for(Object o :updArray){
				String uuid = (String)o;
				String guidkey = "";
				if(idGUIDMap.containsKey(uuid)){
					guidkey = idGUIDMap.getString(uuid);
				}
				RecordVo vo = (RecordVo)changeDatas.get(uuid);
				int i = dao.updateValueObject(vo);
				if(i<1){
					code = "1002";
					if(errmsg.length()==0){
						errmsg.append(ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail")+"|ids:|");
					}else{
						errmsg.append(",");
					}
					errmsg.append(guidkey);
					data.put("logtype", 5);
					data.put("loginfo", guidkey);
					data.put("status", 2);
					data.put("errormsg", ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail"));
					logList.add(data);
					if(!addinfo.contains("u")){
						addinfo += ",u";
					}
					JSONObject obj = new JSONObject();
					obj.put("uuid", uuid);
					obj.put("orgNum", guidkey);
					obj.put("success", false);
					if(childInfoJson.containsKey(uuid)){
						obj.put("parentid", childInfoJson.getString(uuid));
					}
					returnData.add(obj);
				}else{
					JSONObject obj = new JSONObject();
					obj.put("uuid", uuid);
					obj.put("orgNum", guidkey);
					obj.put("success", true);
					if(childInfoJson.containsKey(uuid)){
						obj.put("parentid", childInfoJson.getString(uuid));
					}
					returnData.add(obj);
					
					if(!adminsJson.containsKey("upd")) {
						adminsJson.put("upd", new ArrayList());
					}
					((ArrayList)adminsJson.get("upd")).add(vo);
//					if(type == 5) {
//						empViewList.add(vo);
//					}else {
//						orgViewList.add(vo);
//					}
					orgViewList.add(vo);
				}
			}
			//删除子集中云未同步的记录
			if(!subExistIdI9999Array.isEmpty()) {
				SyncDataUtil.delNotInSubSetData(conn,subExistIdI9999Array);
			}
			
			//批量删除
			JSONObject tablesJson = new JSONObject();
			if(metaDataJson.containsKey("tablesJson") && metaDataJson.get("tablesJson") != null ){
				tablesJson = (JSONObject)metaDataJson.get("tablesJson");
			}
			ArrayList<String> delCodeitemids = new ArrayList<String>(); 
			for(Object o :delArray){
				String uuid = (String)o;
				String guidkey = "";
				if(idGUIDMap.containsKey(uuid)){
					guidkey = idGUIDMap.getString(uuid);
				}
				RecordVo vo = (RecordVo)changeDatas.get(uuid);
//				if(type == 5){
//					int i = dao.deleteValueObject(vo);
//					if(i<1){
//						code = "1002";
//						if(errmsg.length()==0){
//							errmsg.append(ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail")+"|ids:|");
//						}else{
//							errmsg.append(",");
//						}
//						errmsg.append(guidkey);
//						data.put("logtype", 5);
//						data.put("loginfo", guidkey);
//						data.put("status", 2);
//						data.put("errormsg", ResourceFactory.getProperty("hrcloud.recieve.error.partdatafail"));
//						logList.add(data);
//						if(!addinfo.contains("d")){
//							addinfo += ",d";
//						}
//						JSONObject obj = new JSONObject();
//						obj.put("uuid", uuid);
//						obj.put("orgNum", guidkey);
//						obj.put("success", false);
//						if(childInfoJson.containsKey(uuid)){
//							obj.put("parentid", childInfoJson.getString(uuid));
//						}
//						returnData.add(obj);
//					}else{
//						JSONObject obj = new JSONObject();
//						obj.put("uuid", uuid);
//						obj.put("orgNum", guidkey);
//						obj.put("success", true);
//						if(childInfoJson.containsKey(uuid)){
//							obj.put("parentid", childInfoJson.getString(uuid));
//						}
//						returnData.add(obj);
//						//删除人员主集
//						String table = tablesJson.getString(uuid);
//						if("UsrA01".equalsIgnoreCase(table)){
//							//删除人员子集
//							SyncDataUtil.delSubRelationData("1",vo.getString("a0100"),conn,rs);
//						}
////						empViewList.add(vo);
////						delEmpViewList.add(vo);
//					}
//				}else 
				if(type == 6) {
					//删除机构
					//获取所有待删除机构的codeitemid
					delCodeitemids.add(vo.getString("codeitemid"));
				}

			}
			logErrJson.put("addinfo", addinfo);
			logErrJson.put("logList", logList);
			if(type == 6) {
				if(!delCodeitemids.isEmpty()) {
					//组织机构删除
					ArrayList delList = SyncDataUtil.batchDelOrg(conn,rs,delCodeitemids);
//					ArrayList delList = (ArrayList) obj.get("delList");
					//TODO 是否还需要记录日志（还有子节点可能会很多）
					//组织机构，修改AdminCode
					if(!adminsJson.containsKey("del")) {
						adminsJson.put("del", delList);
					}
					delOrgViewList.addAll(delList);
				}
				batchUpdateAdminCode(conn,rs,adminsJson);
			}
			batchLogErrOpration(logErrJson,datas,conn,dao);
			
			
			/*if(logList.isEmpty() && datas.isEmpty()){
				retJson.put("success",true);
				retJson.put("code","");
				retJson.put("errmsg","");
			}*/
			retJson.put("success",true);
			retJson.put("code",code);
			retJson.put("msg","");
			retJson.put("data",returnData);
			
			syncDataToEhr(conn,orgViewList,orgDelList,delOrgViewList);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return retJson;
	}
	/**
	 * 回填hr系统视图同步状态
	 * @param conn
	 * @param orgViewList  数据
	 * @param orgDelList  物理删除
	 * @param delOrgViewList 逻辑删除
	 * @throws SQLException
	 */
	public void syncDataToEhr(Connection conn, ArrayList orgViewList, ArrayList orgDelList, ArrayList delOrgViewList) throws SQLException {
		
		String orgsql = " update t_org_view set hrcloud = 0 where b0110_0 in (";
		String postsql = " update t_post_view set hrcloud = 0 where e01a1_0 in (";
		String delorgsql = " update t_org_view set hrcloud = 0 , sys_flag = 3 where b0110_0 in (";
		String delpostsql = " update t_post_view set hrcloud = 0 , sys_flag = 3 where e01a1_0 in (";
		String orgwheresql = "";
		String postwheresql = "";
		String delorgwheresql = "";
		String delpostwheresql = "";
		ArrayList orgvalues = new ArrayList();
		ArrayList postvalues = new ArrayList();
		ArrayList delorgvalues = new ArrayList();
		ArrayList delpostvalues = new ArrayList();
		
		//逻辑删除中包含的id
		ArrayList delidlist = new ArrayList();
		//删除状态回填为“删除已同步”
		for(Object o : delOrgViewList) {
			RecordVo vo = (RecordVo)o;
			if(vo.hasAttribute("codesetid")) {
				String codesetid = vo.getString("codesetid");
				if(vo.hasAttribute("codeitemid")) {
					delidlist.add(vo.getString("codeitemid"));
					if(!"@K".equalsIgnoreCase(codesetid)) {
						String b0110 = vo.getString("codeitemid");
						delorgwheresql += ("?,");
						delorgvalues.add(b0110);
					}else {
						String e01a1 = vo.getString("codeitemid");
						delpostwheresql += ("?,");
						delpostvalues.add(e01a1);
					}
				}
			}else {
				if(vo.hasAttribute("b0110")) {
					String b0110 = vo.getString("b0110");
					delorgwheresql += ("?,");
					delorgvalues.add(b0110);
					delidlist.add(b0110);
				}
				if(vo.hasAttribute("e01a1")) {
					String e01a1 = vo.getString("e01a1");
					delpostwheresql += ("?,");
					delpostvalues.add(e01a1);
					delidlist.add(e01a1);
				}
			}
		}
				
		//新增修改状态回填为“已同步”
		for(Object o : orgViewList) {
			RecordVo vo = (RecordVo)o;
			if(vo.hasAttribute("codesetid")) {
				String codesetid = vo.getString("codesetid");
				if(vo.hasAttribute("codeitemid")) {
					//逻辑删除中包含修改数据的id
					String id = vo.getString("codeitemid");
					if(delidlist.contains(vo.getString("codeitemid"))) {
						continue;
					}
					if(!"@K".equalsIgnoreCase(codesetid)) {
						if(orgDelList.contains(id)) {
							delorgwheresql += ("?,");
							delorgvalues.add(id);
						}else {
							orgwheresql += ("?,");
							orgvalues.add(id);
						}
					}else {
						String e01a1 = vo.getString("codeitemid");
						if(orgDelList.contains(e01a1)) {
							delpostwheresql += ("?,");
							delpostvalues.add(e01a1);
						}else {
							postwheresql += ("?,");
							postvalues.add(e01a1);
						}
					}
					
				}
			}else {
				if(vo.hasAttribute("b0110")) {
					String b0110 = vo.getString("b0110");
					//逻辑删除中包含修改数据的id
					if(delidlist.contains(b0110)) {
						continue;
					}
					if(vo.hasAttribute("state")) {
						String state = vo.getString("state");
						if("1".equalsIgnoreCase(state)) {
							delorgwheresql += ("?,");
							delorgvalues.add(b0110);
						}else {
							orgwheresql += ("?,");
							orgvalues.add(b0110);
						}
					}else {
						orgwheresql += ("?,");
						orgvalues.add(b0110);
					}
				}
				if(vo.hasAttribute("e01a1")) {
					String e01a1 = vo.getString("e01a1");
					//逻辑删除中包含修改数据的id
					if(delidlist.contains(e01a1)) {
						continue;
					}
					if(vo.hasAttribute("state")) {
						String state = vo.getString("state");
						if("1".equalsIgnoreCase(state)) {
							delpostwheresql += ("?,");
							delpostvalues.add(e01a1);
						}else {
							postwheresql += ("?,");
							postvalues.add(e01a1);
						}
					}else {
						postwheresql += ("?,");
						postvalues.add(e01a1);
					}
				}
			}
		}
		//t_org_view 状态回填为“已同步”
		if(StringUtils.isNotBlank(orgwheresql)) {
			orgwheresql = orgwheresql.substring(0,orgwheresql.length()-1);
			orgsql += orgwheresql;
			orgsql += ")" ;
			ContentDAO dao = new ContentDAO(conn);
			dao.update(orgsql, orgvalues);
		}
		//t_post_view 状态回填为“已同步”
		if(StringUtils.isNotBlank(postwheresql)) {
			postwheresql = postwheresql.substring(0,postwheresql.length()-1);
			postsql += postwheresql;
			postsql += ")" ;
			ContentDAO dao = new ContentDAO(conn);
			dao.update(postsql, postvalues);
		}
		//t_org_view 状态回填为“删除已同步”
		if(StringUtils.isNotBlank(delorgwheresql)) {
			delorgwheresql = delorgwheresql.substring(0,delorgwheresql.length()-1);
			delorgsql += delorgwheresql;
			delorgsql += ")" ;
			ContentDAO dao = new ContentDAO(conn);
			dao.update(delorgsql, delorgvalues);
		}
		//t_post_view 状态回填为“删除已同步”
		if(StringUtils.isNotBlank(delpostwheresql)) {
			delpostwheresql = delpostwheresql.substring(0,delpostwheresql.length()-1);
			delpostsql += delpostwheresql;
			delpostsql += ")" ;
			ContentDAO dao = new ContentDAO(conn);
			dao.update(delpostsql, delpostvalues);
		}
		
	}
	
	
	/**
	 * 批量修改AdminCode中的参数
	 * @param rs 
	 * @param conn 
	 * @param adminsJson {'add':[{..},..],'upd':[{..},..],'del':[{..},..]}
	 */
	private void batchUpdateAdminCode(Connection conn, RowSet rs, HashMap adminsJson) {
		JSONObject batchJson = new JSONObject();
		ArrayList addList = new ArrayList();
		ArrayList updList = new ArrayList();
		ArrayList delList = new ArrayList();
		if(adminsJson.containsKey("add")) {
			addList = (ArrayList)adminsJson.get("add");
		}
		if(adminsJson.containsKey("upd")) {
			updList = (ArrayList)adminsJson.get("upd");
		}
		if(adminsJson.containsKey("del")) {
			delList = (ArrayList)adminsJson.get("del");
		}
		if(!addList.isEmpty()) {
			JSONArray array = new JSONArray();
			for(int i = 0 ; i < addList.size() ; i++) {
				RecordVo vo = (RecordVo)addList.get(i);
				JSONObject json = new JSONObject();
				Map lenmap = vo.getAttrLens();
				int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
				json.put("codeitemdesclen", codeitemdesclen);
				json.put("codesetid", vo.getString("codesetid"));
				json.put("codeitemid", vo.getString("codeitemid"));
				json.put("codeitemdesc", vo.getString("codeitemdesc"));
				json.put("parentid", vo.getString("parentid"));
				json.put("grade", vo.getInt("grade"));
				array.add(json);
			}
			batchJson.put("add", array);
		}
		if(!updList.isEmpty()) {
			JSONArray array = new JSONArray();
			for(int i = 0 ; i < updList.size() ; i++) {
				RecordVo vo = (RecordVo)updList.get(i);
				JSONObject json = new JSONObject();
				Map lenmap = vo.getAttrLens();
				int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
				json.put("codeitemdesclen", codeitemdesclen);
				json.put("codesetid", vo.getString("codesetid"));
				json.put("codeitemid", vo.getString("codeitemid"));
				json.put("codeitemdesc", vo.getString("codeitemdesc"));
				json.put("parentid", vo.getString("parentid"));
				json.put("grade", vo.getInt("grade"));
				array.add(json);
			}
			batchJson.put("upd", array);
		}
		if(!delList.isEmpty()) {
			JSONArray array = new JSONArray();
			for(int i = 0 ; i < delList.size() ; i++) {
				RecordVo vo = (RecordVo)delList.get(i);
				JSONObject json = new JSONObject();
				json.put("codesetid", vo.getString("codesetid"));
				json.put("codeitemid", vo.getString("codeitemid"));
				json.put("parentid", vo.getString("parentid"));
				json.put("grade", vo.getInt("grade"));
				array.add(json);
			}
			batchJson.put("del", array);
		}
		
		SyncDataUtil.batchRefAdminCodes(batchJson);
		
		//超过10条，直接刷新Admincode
		if(addList.size()+updList.size()+delList.size() > 10) {
			batchJson.put("isref", true);
		}else {
			batchJson.put("isref", false);
		}
		
		HashMap paramMap = new HashMap();
		paramMap.put("batchJson", PubFunc.encrypt(batchJson.toString()));
		//集群环境批量修改AdminCode中的参数
		SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_UPD_ADMINCODE,paramMap);
	}


	/**
	 * 解析数据为元数据  type=1\2\3\4 单独处理
	 * 数据类型:
	 * {
	 * 	 tablename:data{
	 * 						GUIDKEY:"DFJIGHIJGWEOIFGJOFMKOSDMJKO",
	 * 						childGUIDKEY:"JFIOGWJHIUFNGVIEJFIOJEGIO",(子集才有)
	 * 						operation:0新增，1修改，2删除
	 * 						itemid:{
	 * 									type:"A"|"D"|"M","N",
	 * 									value:"",
	 * 									cloudcodeset:"AX"(代码型才有)
	 * 								}
	 * 					}
	 * }
	 * @param cloudTOHrfieldsMap 
	 * @param rs 
	 * @param conn 
	 * @return JSONObject 
	 * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 */
	private HashMap analysisDataToMetadata(JSONObject cloudTOHrfieldsMap, Connection conn, RowSet rs) {
		HashMap retJson = new HashMap();
		JSONArray list = resDataJson.getJSONArray("list");
		int type = resDataJson.getInt("type");
		
		if(type == 6){
			//组织架构变动 单独处理
			retJson = getOrgMetaDataJson(conn, rs);
//		}else if(type == 5 ||type == 8 ||type == 9 ||type == 10){
		}else if(type == 8 ||type == 9 ||type == 10){
			if(cloudTOHrfieldsMap.isEmpty()){
				//未配置云与hr系统的指标对应
				retJson.put("success",false);
				retJson.put("code","1001");
				retJson.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.noconnectedfield"));
				retJson.put("addArray", new JSONArray());
				retJson.put("updArray", new JSONArray());
				retJson.put("delArray", new JSONArray());
				return retJson;
			}
			retJson = getMetaDataJson(list,cloudTOHrfieldsMap,type,conn, rs);
		}else if(type == 12){
			//岗位序列变动 无需进行指标对应。获取到同步数据，在库中找到岗位序列的代码。把原有的item全部置为结束状态，并在itemid前添加“dd”进行区分后 直接添加到代码表中
			retJson = getCloudUnitOrderChange(conn, rs);
		}
		return retJson;
	}

	/**
	 * 获取岗位序列同步数据
	 * @param rs 
	 * @param conn 
	 * @return
	 * * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 */
	private HashMap getCloudUnitOrderChange(Connection conn, RowSet rs) {
		HashMap retJson = new HashMap();
		JSONArray list = resDataJson.getJSONArray("list");
		HashMap changeDatas = new HashMap();
		JSONObject idGUIDMap = new JSONObject();
		JSONArray datas = new JSONArray();
		
		try{
			JSONArray addArray = new JSONArray();
			JSONArray updArray = new JSONArray();
			JSONArray delArray = new JSONArray();
			
			//1、获取岗位管理中的岗位序列代码类
			RecordVo ps_c_code_constant_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
			String  codesetid = "";
			if(ps_c_code_constant_vo!=null)
			{ 
				codesetid = ps_c_code_constant_vo.getString("str_value");
			}
			//未配置岗位序列指标
			if(StringUtils.isEmpty(codesetid) || "#".equals(codesetid)){
				retJson.put("success", false);
				retJson.put("code", "1001");
				retJson.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noordercode"));
				return retJson;
			}
			//2、flag=9 云 若存在flag!=9且非失效代码，则置为失效状态并且添加“DD”
			initCCodeItemTable(codesetid,conn,rs);
			
			//3、拼接数据 {codeitem:child} 
			JSONObject parentObject = SyncDataUtil.codeitemToChildid(codesetid,conn,rs);
			
			//4、获取数据库中存在的codeitemid,用于判断是新增还是更新
			JSONObject codeItemIdsMap = SyncDataUtil.codeitemToChildid(codesetid,conn,rs);
			
			//codeitemid 与 云uuid 对应关系
			JSONObject corCodeObj = SyncDataUtil.codeitemTocorCode(codesetid,conn,rs);
			
			//parentid中 子节点的数量
			JSONObject parentidToChildArray = SyncDataUtil.parentidToChildArray(codesetid,conn,rs);
			
			//获取元数据
			for(Object o : list){
				JSONObject post = (JSONObject)o;
				if(post.getInt("type") == -1){
					//没意义
					continue;
				}
				String uuid = post.getString("uuid").toUpperCase();
				int operation = post.getInt("operation");
				String codeitemid = post.getString("code");
				String codeitemdesc = post.getString("name");
				String parentid = post.getString("parentId");
				//parentId 校验
				if(parentid == null || "null".equals(parentid) || parentid.equalsIgnoreCase(codeitemid)){
					parentid = codeitemid;
				}else{
					if(!parentObject.containsKey(parentid)){
						//没找到父节点 不新增
						JSONObject returndata = new JSONObject();
						returndata.put("success", false);
						returndata.put("id", "");
						returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noparentid"));
						returndata.put("data", post);
						datas.add(returndata);
						continue;
					}
				}
				String childid = codeitemid;
				int layer = post.getInt("type");
				int A0000 = 1;
				if(post.containsKey("showOrder")){
					A0000 = post.getInt("showOrder");
				}
				RecordVo vo = new RecordVo("codeitem");
				vo.setString("codesetid", codesetid);
				vo.setString("codeitemid", codeitemid);
				vo.setString("codeitemdesc", codeitemdesc);
				vo.setString("parentid", parentid);
				vo.setInt("layer", layer);
				vo.setString("corcode", uuid);
				vo.setDate("start_date", "1949-10-01 00:00:00.000");
				vo.setDate("end_date", "9999-12-31 00:00:00.000");
				vo.setInt("flag", 9);
				vo.setInt("invalid", 1);
				vo.setInt("a0000", A0000);
				parentObject.put(codeitemid, childid);
				corCodeObj.put(codeitemid, uuid);
				//xus 19/12/2 判断父级代码中的childid是否是自己点，如果不是 则修改成子节点；根节点不需要判断
				if(!parentid.equalsIgnoreCase( codeitemid )){
					//父节点的childid
					String parChildid = parentObject.getString(parentid);
					if(parChildid.equalsIgnoreCase(parentid)){
						//父节点的codeitemid==childid
//						if(operation == 0){
							//修改父节点
							String paruuid = codesetid+parentid;
							if(corCodeObj.containsKey(parentid)){
								paruuid = corCodeObj.getString(parentid);
								
								RecordVo parvo = null;
								if(changeDatas.containsKey(paruuid)){
									parvo = (RecordVo)changeDatas.get(paruuid);
								}else{
									parvo = new RecordVo("codeitem");
									parvo.setString("codesetid", codesetid);
									parvo.setString("codeitemid", parentid);
									updArray.add(paruuid);
								}
								parvo.setString("childid", codeitemid);
								changeDatas.put(paruuid, parvo);
								idGUIDMap.put(paruuid, paruuid);
								parentObject.put(parentid, codeitemid);
//							}
						}
					}else{
						//删除
						if(operation == 2){
							//父节点的子节点 是当前节点，则修改父节点的子节点
							if(parChildid.equalsIgnoreCase(codeitemid)){
								//若父节点下没子节点，恢复父节点为父节点的codeitemid
								String paruuid = codesetid+parentid;
								if(corCodeObj.containsKey(parentid)){
									paruuid = corCodeObj.getString(parentid);
									if(parentidToChildArray.containsKey(parentid)){
										//父节点的子节点
										String parentChildId = parentid;
										JSONArray childArray = parentidToChildArray.getJSONArray(parentid);
										//当前节点在父节点中，并且不是最后一个节点
										if(childArray.indexOf(codeitemid) > -1 && childArray.size() - childArray.indexOf(codeitemid) > 1){
											parentChildId = childArray.getString(childArray.indexOf(codeitemid)+1);
										}
										RecordVo parvo = null;
										if(changeDatas.containsKey(paruuid)){
											parvo = (RecordVo)changeDatas.get(uuid);
										}else{
											parvo = new RecordVo("codeitem");
											parvo.setString("codesetid", codesetid);
											parvo.setString("codeitemid", parentid);
											updArray.add(paruuid);
										}
										parvo.setString("childid", parentChildId);
										changeDatas.put(paruuid, parvo);
										idGUIDMap.put(paruuid, paruuid);
									}
								}
							}
						}
					}
				}
				
				//xus 19/12/17 数据库中不存在则新增，存在则修改
				if(operation == 0 || operation == 1){
					if(codeItemIdsMap.containsKey(codeitemid)) {
						updArray.add(uuid);
						AdminCode.updateCodeItemDesc(codesetid.toUpperCase(), codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,50));
					} else {
						addArray.add(uuid);
						//新增时加上子节点参数（修改的时候不需要进行修改）
						vo.setString("childid", childid);
						CodeItem item=new CodeItem();
						 item.setCodeid(codesetid.toUpperCase());
						 item.setCodeitem(codeitemid.toUpperCase());
						 item.setCodename(PubFunc.splitString(codeitemdesc,50));
						 item.setPcodeitem(parentid.toUpperCase());				 
						 item.setCcodeitem(childid.toUpperCase());
						 item.setCodelevel(Integer.toString(layer));
						 AdminCode.addCodeItem(item);
						 AdminCode.updateCodeItemDesc(codesetid.toUpperCase(), codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,50));
					}
				}else if(operation == 2){
					delArray.add(uuid);
					CodeUtilBo.delCodeitem(conn, codesetid, parentid, codeitemid);
					//删除子节点
//					SyncDataUtil.delChildCode(codesetid,codeitemid,conn,rs);
				}
				changeDatas.put(uuid, vo);
				idGUIDMap.put(uuid, uuid);
				
			}
			retJson.put("addArray", addArray);
			retJson.put("updArray", updArray);
			retJson.put("delArray", delArray);
			retJson.put("changeDatas", changeDatas);
			retJson.put("idGUIDMap", idGUIDMap);
			
			retJson.put("success", true);
			retJson.put("errCode", "");
			retJson.put("datas", datas);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return retJson;
	}

	/**
	 * 初始化岗位序列代码
	 * @param codesetid
	 * @param rs 
	 * @param conn 
	 * @throws SQLException 
	 */
	private void initCCodeItemTable(String codesetid, Connection conn, RowSet rs) throws SQLException {
		//flag=9 云 若存在flag!=9且非失效代码，则置为失效状态并且添加“DD”
		String sql = "select flag from codeitem where codesetid = ? and flag <> 9 and end_date > "+Sql_switcher.sqlNow();
		ArrayList values = new ArrayList();
		values.add(codesetid);
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql, values);
		//是否备份原有系统中的代码（方便恢复）
		boolean recordExc = false;
		if(rs.next()){
			recordExc = true; 
		}
		
		if(recordExc){
			//备份原有系统中的代码（方便恢复）
			sql = "update codeitem set codeitemid = 'DD'"+Sql_switcher.concat()+"codeitemid,end_date="+Sql_switcher.sqlNow()+"  where codesetid = ? and end_date > "+Sql_switcher.sqlNow();
			dao.update(sql,values);
		}
	}


	/**
	 * 获取元数据
	 * @param list 
	 * @param cloudTOHrfieldsMap  constant表中指标对应关系
	 * @param type 
	 * @param rs 
	 * @param conn 
	 * @return
	 * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 */
	private HashMap getMetaDataJson(JSONArray list, JSONObject cloudTOHrfieldsMap, int type, Connection conn, RowSet rs) {
		HashMap returnJson = new HashMap();
		try {
			JSONObject hrCodeRelationJson = getHrCodeRelationJson(conn,rs);
			//主集
			JSONArray addArray = new JSONArray();
			JSONArray updArray = new JSONArray();
			JSONArray delArray = new JSONArray();
			//uuid对应的表（只有人员有值）
			JSONObject tablesJson = new JSONObject();
			//子集
			JSONArray addSubArray = new JSONArray();
			JSONArray updSubArray = new JSONArray();
			JSONArray delSubArray = new JSONArray();
			JSONArray subExistIdI9999Array = new JSONArray();
			
			//子集信息json （用于返回参数）childInfoJson
			JSONObject childInfoJson = new JSONObject();
			
			//子集GUIDKEY，key，i9999的集合{'usrA04':{'XXXXXXXXX'(GUIDKEY):{'key':'xxxxxxxxxxx','i9999':1}}}
			JSONObject subsetsInfoJson = new JSONObject(); 
			
			HashMap changeDatas = new HashMap();
			JSONObject idGUIDMap = new JSONObject();
			
			JSONArray retDatas = new JSONArray();
			
			boolean success = true;
			
			JSONObject GUIDKEYToCodeitemJson = new JSONObject();
			//organization中corcode与codeitemid对应关系
			JSONObject corCodeToCodeitemJson = new JSONObject();
			JSONObject  maxA01Map =  new JSONObject();
			JSONObject  GUIDKEYToA0100 =  new JSONObject();
			
			//获取GUIDKEY与codesetid对应的关系
			String codesetid = "";
			//信息集主键"a0100"、"b0110"、"e01a1"
			String primkey = "";
			GUIDKEYToCodeitemJson = SyncDataUtil.getGUIDKEYToCodeitemIdMap("", conn, rs);
			corCodeToCodeitemJson = SyncDataUtil.getCorCodeGUIDKEYToCodeitemIdMap(conn, rs);
			JSONArray existedPrimaryKeys = new JSONArray();
//			if(type == 8 || type == 9 || type == 10 ){
				if(type == 8){
					codesetid = "UN";
					primkey = "b0110";
				}else if(type == 9){
					codesetid = "UM";
					primkey = "b0110";
				}else if(type == 10){
					codesetid = "@K";
					primkey = "e01a1";
				}
				//获取组织架构中所有的codesetid
//				GUIDKEYToCodeitemJson = SyncDataUtil.getGUIDKEYToCodeitemIdMap(codesetid, conn, rs);
//			}else if(type == 5){
//				primkey = "a0100";
//				maxA01Map = SyncDataUtil.getMaxA0000AndA0100Map(conn,rs);
//				GUIDKEYToA0100 = SyncDataUtil.getGUIDKEYToA0100Map(conn,rs);
//			}
			
			//hr系统中 congstant参数存放的主集对应关系
			JSONObject main = cloudTOHrfieldsMap.getJSONObject("main");
			
			for(int i = 0 ; i<list.size();i++){

				//主集指标对应关系
				JSONObject fieldsMap = main.getJSONObject("fields");
				//每条数据
				JSONObject staff = list.getJSONObject(i);
//				String thirdId = "";
				String uuid = "";
				try {
					//信息集主键a0100、b0110、e01a1的值
					String primaKeyValue = "";
					
					//人员单位部门岗位通用属性
					int operation = staff.getInt("operation");
					String GUIDKEY = "";
					//表名
					String table = main.getString("hr_set");
					
					
					RecordVo vo = null;
					
//					if(type == 8 || type == 9 || type == 10 ){
						//---------------------------单位部门岗位------------------------
						//1、hr系统唯一标识GUIDKEY 
						String codeitemid = "";
//						String thirdId = "";
						//新增
						if(!staff.containsKey("uuid") || StringUtils.isBlank(staff.getString("uuid")) || "null".equalsIgnoreCase(staff.getString("uuid"))){
							JSONObject returndata = new JSONObject();
							returndata.put("success", false);
							returndata.put("id", "");
							returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noorgid"));
							returndata.put("data", staff);
							retDatas.add(returndata);
							continue;
						}
						uuid = staff.getString("uuid").toUpperCase();
//						thirdId = staff.getString("uuid").toUpperCase();
						String orgNum = "";
						//orgNum为hcm系统的GUIDKEY orgNum为云系统单位部门岗位的主键。获取时优先获取orgNum，为空时再取uuid
						if(staff.containsKey("orgNum") && StringUtils.isNotBlank(staff.getString("orgNum")) && !"null".equalsIgnoreCase(staff.getString("orgNum"))){
							orgNum = staff.getString("orgNum").toUpperCase();
						}
						//云传来的数据新增、更新判断
						//判断hcm组织架构中是否有：云传过来的第三方主键，有则更新
						if(StringUtils.isNotBlank(orgNum) && !"null".equalsIgnoreCase(orgNum) ) {
							if(corCodeToCodeitemJson.containsKey(orgNum)) {
								codeitemid = corCodeToCodeitemJson.getString(orgNum);
								GUIDKEY = orgNum;
							}else if(GUIDKEYToCodeitemJson.containsKey(orgNum)) {
								codeitemid = GUIDKEYToCodeitemJson.getString(orgNum);
								GUIDKEY = orgNum;
							}
						}
						if(StringUtils.isBlank(codeitemid)) {
							if(corCodeToCodeitemJson.containsKey(uuid)) {
								codeitemid = corCodeToCodeitemJson.getString(uuid);
								GUIDKEY = uuid;
							}else if(GUIDKEYToCodeitemJson.containsKey(uuid)) {
								codeitemid = GUIDKEYToCodeitemJson.getString(uuid);
								GUIDKEY = uuid;
							}
						}
						//如果组织机构中没有对应的
						if(StringUtils.isBlank(codeitemid)){
							JSONObject returndata = new JSONObject();
							returndata.put("success", false);
							returndata.put("id", uuid);
							returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noorgid"));
							returndata.put("data", staff);
							retDatas.add(returndata);
							continue;
						}
						
						//判断主集表中是否已存在此条数据
						existedPrimaryKeys = getExistedPrimaryKeys(table,conn, rs ,primkey);
						if(existedPrimaryKeys.contains(codeitemid) ) {
							if(operation == 0) {
								operation = 1;
							}
						}else {
							if(operation == 1) {
								operation = 0;
							}
						}
						
						if(operation == 0) {
							addExistedPrimaryKeys(table,primkey,codeitemid);
						}
						vo = getConnectedDataJson(staff,fieldsMap,hrCodeRelationJson,table);
						vo.setString(primkey, codeitemid);
						if(operation == 1){
							//修改 deleteflag == 1 时 逻辑删除
							if(staff.containsKey("deleteFlag")){
								if("1".equals(staff.getString("deleteFlag"))){
									vo.setString("state", "1");
								}
							}
						}
						primaKeyValue = codeitemid ; 
						
						
						//1、组织机构数据
						//2、单位部门岗位数据
						if(type == 10){
							/*
							 * //xus 19/12/21 若k01中存在，则修改 否则新增。 if(operation == 0 || operation == 1) {
							 * if(K01ExistedCodeitems.contains(codeitemid)) { operation = 1; }else {
							 * operation = 0; } }
							 */
							//所属部门
							if("K01".equalsIgnoreCase(table)){
								if(staff.containsKey("deptId") && StringUtils.isNotBlank(staff.getString("deptId")) && GUIDKEYToCodeitemJson.containsKey(staff.getString("deptId").toUpperCase())){
									String E0122Guid = staff.getString("deptId").toUpperCase();
									String E0122 = GUIDKEYToCodeitemJson.getString(E0122Guid);
									vo.setString("e0122", E0122);
								}else if(staff.containsKey("unitId") && StringUtils.isNotBlank(staff.getString("unitId")) && GUIDKEYToCodeitemJson.containsKey(staff.getString("unitId").toUpperCase())){
									//【56843】v77发版：云集成，在云平台单位下直接新增岗位，在hr系统组织机构/岗位管理/信息浏览中不显示
									String E0122Guid = staff.getString("unitId").toUpperCase();
									String E0122 = GUIDKEYToCodeitemJson.getString(E0122Guid);
									vo.setString("e0122", E0122);
								}
							}
							//岗位序列：
							String thirdPosLine = staff.getString("posList");
							RecordVo PS_C_JOB = ConstantParamter.getRealConstantVo("PS_C_JOB");
							if(PS_C_JOB != null && StringUtils.isNotBlank(PS_C_JOB.getString("str_value")) && !"#".equals(PS_C_JOB.getString("str_value"))){
								vo.setString(PS_C_JOB.getString("str_value").toLowerCase(), thirdPosLine);
							}
						}
//					}else{
//						//----------------------------人员----------------------
//						table = "Usr"+table;
//						String a0100 = "00000001";
//						int a0000 = 1;
//						String B0110 = "";
//						String E0122 = "";
//						String E01A1 = "";
//						if(!staff.containsKey("id") || StringUtils.isBlank(staff.getString("id"))){
//							JSONObject returndata = new JSONObject();
//							returndata.put("success", false);
//							returndata.put("id", "");
//							returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noorgid"));
//							returndata.put("data", staff);
//							retDatas.add(returndata);
//							continue;
//						}
//						uuid = staff.getString("id").toUpperCase();
//						//人员所属单位unitId
//						if(staff.containsKey("unitId") && StringUtils.isNotBlank(staff.getString("unitId")) && GUIDKEYToCodeitemJson.containsKey(staff.getString("unitId").toUpperCase())){
//							B0110 = GUIDKEYToCodeitemJson.getString(staff.getString("unitId").toUpperCase());
//						}
//						//人员所属部门deptId
//						if(staff.containsKey("deptId") && StringUtils.isNotBlank(staff.getString("deptId")) && GUIDKEYToCodeitemJson.containsKey(staff.getString("deptId").toUpperCase())){
//							E0122 = GUIDKEYToCodeitemJson.getString(staff.getString("deptId").toUpperCase());
//						}
//						//人员所属岗位posId
//						if(staff.containsKey("posId") && StringUtils.isNotBlank(staff.getString("posId")) && GUIDKEYToCodeitemJson.containsKey(staff.getString("posId").toUpperCase())){
//							E01A1 = GUIDKEYToCodeitemJson.getString(staff.getString("posId").toUpperCase());
//						}
//						if(operation == 0){
//							//新增
//							GUIDKEY =  staff.getString("id").toUpperCase();
//							a0100 = maxA01Map.getString("a0100");
//							a0000 = maxA01Map.getInt("a0000");
//							
//							vo = getConnectedDataJson(staff,fieldsMap,hrCodeRelationJson,table);
//							vo.setString("a0100", a0100);
//							vo.setInt("a0000", a0000);
//							vo.setString("guidkey", GUIDKEY);
//						}else{
//							//修改、删除
//							if(staff.containsKey("staffNum") && StringUtils.isNotBlank(staff.getString("staffNum"))){
//								GUIDKEY = staff.getString("staffNum");
//							}
//							if(StringUtils.isBlank(GUIDKEY) && staff.containsKey("thirdId") && StringUtils.isNotBlank(staff.getString("thirdId"))){
//								GUIDKEY = staff.getString("thirdId");
//							}
//							if(StringUtils.isBlank(GUIDKEY) || !GUIDKEYToA0100.containsKey(GUIDKEY)){
//								JSONObject returndata = new JSONObject();
//								returndata.put("success", false);
//								returndata.put("id", uuid);
//								returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.nohrrelation"));
//								returndata.put("data", staff);
//								retDatas.add(returndata);
//								continue;
//							}
//							a0100 = GUIDKEYToA0100.getString(GUIDKEY);
//							
//							vo = getConnectedDataJson(staff,fieldsMap,hrCodeRelationJson,table);
//							vo.setString("a0100", a0100);
//							//修改 deleteflag == 1 时 逻辑删除
//							if(staff.containsKey("deleteFlag")){
//								if("1".equals(staff.getString("deleteFlag"))){
//									vo.setString("state", "1");
//								}
//							}
//						}
//						vo.setString("b0110", B0110);
//						vo.setString("e0122", E0122);
//						vo.setString("e01a1", E01A1);
//						primaKeyValue = a0100;
//						tablesJson.put(GUIDKEY, table);
//					}
					
					if(operation == 0){
						addArray.add(GUIDKEY);
					}else if(operation == 1){
						updArray.add(GUIDKEY);
					}else if(operation == 2){
						delArray.add(GUIDKEY);
					}
					changeDatas.put(GUIDKEY, vo);
					idGUIDMap.put(uuid, GUIDKEY);
					//子集
					if(!staff.containsKey("subsets") || staff.get("subsets") == null || StringUtils.isBlank(staff.getString("subsets")) || "null".equals(staff.getString("subsets"))){
						continue;
					}
					JSONArray subsets = staff.getJSONArray("subsets");
					
					//xus 19/11/28 接口变动：同步子集之前，清空子集中该条数据的记录
//					for(Object o : subsets){
//						JSONObject subset = (JSONObject)o;
//						//云子集id名
//						String id = subset.getString("id");
//						//hr系统中 congstant参数存放的子集对应关系
//						if(cloudTOHrfieldsMap.containsKey(id)){
//							//子集指标对应关系
//							JSONObject conssubset = cloudTOHrfieldsMap.getJSONObject(id);
//							//子集表名
//							table = conssubset.getString("hr_set");
//							SyncDataUtil.delSubSetData(conn, rs, table,primkey,primaKeyValue, "");
//						}
//					}
					//同步
					for(Object o : subsets){
						JSONObject subset = (JSONObject)o;
						//云子集id名
						String id = subset.getString("id");
						//hr系统中 congstant参数存放的子集对应关系
						if(!cloudTOHrfieldsMap.containsKey(id)){
							//TODO 没有子集指标对应
							JSONObject returndata = new JSONObject();
							returndata.put("success", false);
							returndata.put("id", id);
							returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.nosubfield"));
							returndata.put("data", subset);
							retDatas.add(returndata);
							continue;
						}
						//子集指标对应关系
						JSONObject conssubset = cloudTOHrfieldsMap.getJSONObject(id);
						JSONArray datas = subset.getJSONArray("data");
						//子集表名
						table = conssubset.getString("hr_set");
						//xus 19/11/28子集数据为空时、状态为删除时,删除子集数据
						if(datas.size()==0){
							SyncDataUtil.delSubSetData(conn, rs, table,primkey,primaKeyValue, "");
							continue;
						}
						if("1".equals(conssubset.getString("set_type"))){
							table = "Usr"+table;
						}
						
						//判断GUIDKEY是否存在，并改为50
						judgeGUIDKEYLength(conn,rs,table);
						
						
						//子集GUIDKEY指标与key，i9999对应关系
						JSONObject subsetGuidkeyToKeyI9999 = SyncDataUtil.subsetGuidkeyToKeyI9999(table, primkey, conn, rs);
						//子集key，最大i9999对应关系
						JSONObject subsetKeyToMaxI9999 = SyncDataUtil.subsetKeyToMaxI9999(table, primkey, conn, rs);
						//子集key，guidkey为空的i9999对应关系
						JSONObject subsetKeyNullGuidkeyToI9999 = SyncDataUtil.subsetKeyNullGuidkeyToI9999(table, primkey, conn, rs);
						
						//子集中存在的i9999(需要清除掉除此些i9999之外的数据)
						JSONArray existI9999Array = new JSONArray();
						//子集
						for(Object object : datas){
							JSONObject data = (JSONObject)object;
							fieldsMap = conssubset.getJSONObject("fields");
							vo = getConnectedDataJson(data,fieldsMap,hrCodeRelationJson,table);
							
							String key = data.getString("uuid").toUpperCase();
							operation = data.getInt("operation");
							//主集ID A0100 B0110...
							vo.setString(primkey, primaKeyValue);
							
							vo.setString("guidkey", key);
							//删除子集
							if(operation != 2 && data.containsKey("deleteFlag")){
								if("1".equals(data.getString("deleteFlag"))){
									operation = 2;
								}
							}
							//新增修改子集
							if(operation != 2){
								//若果没有找到对应GUIDKEY参数则新增，否则更新
								if(subsetGuidkeyToKeyI9999.containsKey(key)){
									JSONObject sunsetJson = subsetGuidkeyToKeyI9999.getJSONObject(key);
									int i9999 = Integer.parseInt(sunsetJson.getString("i9999"));
									vo.setInt("i9999", i9999);
									updSubArray.add(key);
								}else{
									//若新增子集中存在guidkey为null的数据，则基于此条数据进行修改
									if(subsetKeyNullGuidkeyToI9999.containsKey(primaKeyValue)) {
										int i9999 = Integer.parseInt(subsetKeyNullGuidkeyToI9999.getString(primaKeyValue));
										vo.setInt("i9999", i9999);
										updSubArray.add(key);
									}else {
										//新增
										int i9999 = 1;
										if(subsetKeyToMaxI9999.containsKey(primaKeyValue)){
											i9999 = subsetKeyToMaxI9999.getInt(primaKeyValue);
											i9999++;
										}
										vo.setInt("i9999", i9999);
										subsetKeyToMaxI9999.put(primaKeyValue, i9999);
										addSubArray.add(key);
									}
								}
								existI9999Array.add(vo.getInt("i9999"));
							}else{
								//删除子集
								String subsetI9999 = "";
								if(subsetGuidkeyToKeyI9999.containsKey(key)){
									//删除指定子集
									JSONObject sunsetJson = subsetGuidkeyToKeyI9999.getJSONObject(key);
									subsetI9999 = sunsetJson.getString("i9999");
								}
								SyncDataUtil.delSubSetData(conn, rs, table,primkey,primaKeyValue, subsetI9999);
							}
								
							changeDatas.put(key, vo);
							idGUIDMap.put(id, key);
							childInfoJson.put(key, GUIDKEY);
						}
						JSONObject existSubSetJson = new JSONObject();
						existSubSetJson.put("table", table);
						existSubSetJson.put("primkey", primkey);
						existSubSetJson.put("primaKeyValue", primaKeyValue);
						existSubSetJson.put("i9999", existI9999Array);
						subExistIdI9999Array.add(existSubSetJson);
					}
				} catch (Exception e) {
					JSONObject returndata = new JSONObject();
					returndata.put("success", false);
					returndata.put("id", uuid);
					returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.exception"));
					returndata.put("data", staff);
					retDatas.add(returndata);
					continue;
				}
			}
			if(!addSubArray.isEmpty()){
				for(Object ooj : addSubArray){
					addArray.add((String)ooj);
				}
			}
			if(!updSubArray.isEmpty()){
				for(Object ooj : updSubArray){
					updArray.add((String)ooj);
				}
			}
			if(!delSubArray.isEmpty()){
				for(Object ooj : delSubArray){
					delArray.add((String)ooj);
				}
			}
			returnJson.put("addArray", addArray);
			returnJson.put("updArray", updArray);
			returnJson.put("delArray", delArray);
			returnJson.put("changeDatas", changeDatas);
			returnJson.put("idGUIDMap", idGUIDMap);
			returnJson.put("tablesJson", tablesJson);
			returnJson.put("childInfoJson", childInfoJson);
			returnJson.put("subExistIdI9999Array", subExistIdI9999Array);
			
			returnJson.put("success", success);
			returnJson.put("datas", retDatas);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJson;
	}


	/**
	 * 判断子集GUIDKEY的长度，并改为50
	 * @param conn
	 * @param rs
	 * @param table
	 */
	private void judgeGUIDKEYLength(Connection conn, RowSet rs, String table) {
		try {
			//表中没有GUIDKEY字段  增加GUIDKEY标识字段
			DbWizard db = new DbWizard(conn);
			if(!db.isExistField(table, "GUIDKEY",false)){
				Table t = new Table(table);
				Field f = new Field("GUIDKEY",DataType.STRING);
				f.setNullable(true);
				f.setLength(50);
				t.addField(f);
				db.addColumns(t);
			}else {
				ContentDAO dao = new ContentDAO(conn);
				String sql = "select GUIDKEY from "+table;
				rs = dao.search(sql);
				ResultSetMetaData meta = rs.getMetaData();
				int len = meta.getColumnDisplaySize(1);
				//GUIDKEY 长度不为50 则改为50
				if(len < 50 ) {
					Table t = new Table(table);
					Field f = new Field("GUIDKEY",DataType.STRING);
					f.setNullable(true);
					f.setLength(50);
					t.addField(f);
					db.alterColumns(t);
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 将主键存到已存在的主键集合
	 * @param table
	 * @param primkey
	 * @param codeitemid
	 */
	private void addExistedPrimaryKeys(String table, String primkey, String codeitemid) {
		if(this.primaryKeysMap == null) {
			this.primaryKeysMap = new JSONObject();
		}
		if(!this.primaryKeysMap.containsKey(table)) {
			this.primaryKeysMap.put(table, new JSONArray());
		}
		if(!this.primaryKeysMap.getJSONArray(table).contains(codeitemid)) {
			this.primaryKeysMap.getJSONArray(table).add(codeitemid);
		}
	}


	/**
	 * 获取存在的主键集合
	 * @param table 
	 * @param primkey 
	 * @param rs 
	 * @param conn 
	 * @return
	 * @throws SQLException 
	 */
	private JSONArray getExistedPrimaryKeys(String table, Connection conn, RowSet rs, String primkey) throws SQLException {
		JSONArray returnArray = new JSONArray();
		if(this.primaryKeysMap == null ) {
			this.primaryKeysMap = new JSONObject();
		}
		if(this.primaryKeysMap.containsKey(table)) {
			returnArray = this.primaryKeysMap.getJSONArray(table);
		}else {
			returnArray = SyncDataUtil.getExistedPrimaryKeys(table, conn, rs ,primkey);
			this.primaryKeysMap.put(table, returnArray);
		}
		return returnArray;
	}


	/**
	 * 获取hr组织机构单条数据
	 * @param data
	 * @param codesetid 
	 * @param GUIDKEYToCodeitemJson 
	 * @param corCodeToCodeitemJson 
	 * @param maxCode 根节点下最大的codeitemid
	 * @param maxCodeJson 非根节点下最大的codesetid关系{parentid：codeitemid}
	 * @return
	 */
	private JSONObject getHrORGSingelData(Connection conn,JSONObject data, JSONObject GUIDKEYToCodeitemJson, JSONObject corCodeToCodeitemJson, String maxCode, JSONObject maxCodeJson) {
		String maxcode = maxCode;
		JSONObject returndata = new JSONObject();
		//云ID
		String cloudId = "";
		if(data.containsKey("uuid") && StringUtils.isNotBlank(data.getString("uuid"))){
			cloudId = data.getString("uuid");
		}
		returndata.put("uuid", cloudId);
		//operation校验
		if(!data.containsKey("operation") || (data.getInt("operation") != 0 && data.getInt("operation") != 1 && data.getInt("operation") != 2)){
			returndata.put("success", false);
			returndata.put("id", cloudId);
			returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.nooperation"));
			returndata.put("data", data);
			SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.nooperation"));
			return returndata;
		}
		int operation = data.getInt("operation");
		String parentid = "";
		String GUIDKEY = "";
		
		//orgType校验
		if(!data.containsKey("orgType") || (data.getInt("orgType") != 1 && data.getInt("orgType") != 2 && data.getInt("orgType") != 3)){
			returndata.put("success", false);
			returndata.put("id", cloudId);
			returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noorgtype"));
			returndata.put("data", data);
			SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noorgtype"));
			return returndata;
		}
		
		int orgType = data.getInt("orgType");
		String codesetid = "UN";
		if(orgType == 1){
			codesetid = "UN";
		}else if(orgType == 2){
			codesetid = "UM";
		}else if(orgType == 3){
			codesetid = "@K";
		}
		
		//codesetid非空判断
		if(StringUtils.isBlank(codesetid)){
			returndata.put("success", false);
			returndata.put("id", cloudId);
			returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"codesetid");
			returndata.put("data", data);
			SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"codesetid");
			return returndata;
		}
		//parentid非空判断
		if(!data.containsKey("parentId")){
			returndata.put("success", false);
			returndata.put("id", cloudId);
			returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"parentid");
			returndata.put("data", data);
			SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"parentid");
			return returndata;
		}
		//云中的父节点id
		String cloudparentid = data.getString("parentId").toUpperCase();
		
		
		
		JSONObject orgObj = new JSONObject();
		//与云中的第三方系统父节点id进行匹配
		if(data.containsKey("parentNum") && StringUtils.isNotBlank(data.getString("parentNum")) && !"null".equalsIgnoreCase(data.getString("parentNum")) ) {
			//云中的第三方系统父节点id
			String cloudparentNum = data.getString("parentNum").toUpperCase();
			if(corCodeToCodeitemJson.containsKey(cloudparentNum)) {
				JSONObject dataObj = corCodeToCodeitemJson.getJSONObject(cloudparentNum);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					parentid = dataObj.getString("codeitemid");
				}
			}else if(GUIDKEYToCodeitemJson.containsKey(cloudparentNum)) {
				JSONObject dataObj = GUIDKEYToCodeitemJson.getJSONObject(cloudparentNum);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					parentid = dataObj.getString("codeitemid");
				}
			}
		}
		//与云中的父节点id进行匹配
		if(StringUtils.isBlank(parentid)) {
			if(GUIDKEYToCodeitemJson.containsKey(cloudparentid)) {
				JSONObject dataObj = GUIDKEYToCodeitemJson.getJSONObject(cloudparentid);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					parentid = dataObj.getString("codeitemid");
				}
			}else if(corCodeToCodeitemJson.containsKey(cloudparentid)) {
				JSONObject dataObj = corCodeToCodeitemJson.getJSONObject(cloudparentid);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					parentid = dataObj.getString("codeitemid");
				}
			}
		}
		
		if(parentid == null || StringUtils.isBlank(parentid) || "null".equalsIgnoreCase(parentid)) {
			//没有对应到父级节点，并且不是根节点时，退出
			if(cloudparentid != null && StringUtils.isNotBlank(cloudparentid) && !"null".equalsIgnoreCase(cloudparentid)) {
				returndata.put("success", false);
				returndata.put("id", cloudId);
				returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.parentidconnectfail"));
				returndata.put("data", data);
				SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.parentidconnectfail"));
				return returndata;
			}
			//根节点
			parentid = "";
			//岗位不允许创建再根节点上
			if("@K".equalsIgnoreCase(codesetid)){
				returndata.put("success", false);
				returndata.put("id", cloudId);
				returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.rootnok"));
				returndata.put("data", data);
				SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.rootnok"));
				return returndata;
			}
		}else {
			if(maxCodeJson.containsKey(parentid)){
				maxcode = maxCodeJson.getString(parentid);
			}else{
				maxcode = "00";
			}
		}
		
		//若系统中已存在架构，则修改
		//xus 19/12/25 【56826】v77发版：云集成，在云平台部门下新增岗位，同步到hr系统，岗位管理/信息浏览中，显示了4个同样的岗位
		//orgId
		if(!data.containsKey("orgId") || StringUtils.isBlank(data.getString("orgId")) || "null".equalsIgnoreCase(data.getString("orgId"))){
			//失败，未获取到组织机构对应的单位部门岗位id
			returndata.put("success", false);
			returndata.put("id", cloudId);
			returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+ResourceFactory.getProperty("hrcloud.recieve.error.orgname"));
			returndata.put("data", data);
			SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noget")+ResourceFactory.getProperty("hrcloud.recieve.error.orgname"));
			return returndata;
		}
		
		//存储规则：优先找orgNum，如果hcm中有对应值，则修改，否则找orgId，hcm中有对应值,则修改,否则新增
		//云系统组织架构id(对应hcm中corcode)
		String orgId = data.getString("orgId").toUpperCase();
		//云系统第三方系统id(对应hcm中GUIDKEY)
		String orgNum = "";
		if(data.containsKey("orgNum") && StringUtils.isNotBlank(data.getString("orgNum")) && !"null".equalsIgnoreCase(data.getString("orgNum"))){
			orgNum = data.getString("orgNum").toUpperCase();
		}
		String codeitemid = "";
		if(StringUtils.isNotBlank(orgNum) && !"null".equalsIgnoreCase(orgNum) ) {
			if(corCodeToCodeitemJson.containsKey(orgNum)) {
				JSONObject dataObj = corCodeToCodeitemJson.getJSONObject(orgNum);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					codeitemid = dataObj.getString("codeitemid");
				}
			}else if(GUIDKEYToCodeitemJson.containsKey(orgNum)) {
				JSONObject dataObj = GUIDKEYToCodeitemJson.getJSONObject(orgNum);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					codeitemid = dataObj.getString("codeitemid");
				}
			}
		}else {
			//若orgNum为空，则赋值为云的组织架构id
			orgNum = orgId;
		}
		if(StringUtils.isBlank(codeitemid)) {
			if(GUIDKEYToCodeitemJson.containsKey(orgId)) {
				JSONObject dataObj = GUIDKEYToCodeitemJson.getJSONObject(orgId);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					codeitemid = dataObj.getString("codeitemid");
				}
			}else if(corCodeToCodeitemJson.containsKey(orgId)) {
				JSONObject dataObj = corCodeToCodeitemJson.getJSONObject(orgId);
				if(dataObj != null && !"null".equalsIgnoreCase(dataObj.toString()) && dataObj.containsKey("codeitemid")){
					codeitemid = dataObj.getString("codeitemid");
				}
			}
		}
		if(StringUtils.isNotBlank(codeitemid) && operation == 0){
			operation = 1;
		}
		if(StringUtils.isBlank(codeitemid) && operation == 1) {
			operation = 0;
		}
		
			if(!data.containsKey("uuid") || StringUtils.isBlank(data.getString("uuid"))){
				returndata.put("success", false);
				returndata.put("id", cloudId);
				returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"uuid");
				returndata.put("data", data);
				SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"uuid");
				return returndata;
			}
			//start_date、end_date
			String start_date = "1949-10-01 00:00:00.000";
			String end_date = "9999-12-31 00:00:00.000";
			//codeitemdesc:接收组织机构数据获取orgName参数
			String codeitemdesc = "";
			if(data.containsKey("orgName") && StringUtils.isNotBlank(data.getString("orgName")) ){
				codeitemdesc = data.getString("orgName");
			}else{
				//失败，未获取到组织机构名称
				returndata.put("success", false);
				returndata.put("id", cloudId);
				returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+ResourceFactory.getProperty("hrcloud.recieve.error.orgname"));
				returndata.put("data", data);
				SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(),ResourceFactory.getProperty("hrcloud.recieve.error.noget")+ResourceFactory.getProperty("hrcloud.recieve.error.orgname"));
				return returndata;
			}
			//根节点
			if(StringUtils.isBlank(parentid)){
				//根节点数超出范围
				if((maxcode.length()==1 && "9".equalsIgnoreCase(maxcode)) || (maxcode.length()==2 && "99".equalsIgnoreCase(maxcode))){
					returndata.put("success", false);
					returndata.put("id", cloudId);
					returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.codeoutlength"));
					returndata.put("data", data);
					SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.codeoutlength"));
					return returndata;
				}
			}

			//codeitemid
			if(StringUtils.isBlank(codeitemid)) {
				try {
					codeitemid = SyncDataUtil.getNewCodeItem(codesetid,maxcode,parentid,conn);
				} catch (GeneralException e) {
					returndata.put("success", false);
					returndata.put("id", cloudId);
					returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"codeitemid");
					returndata.put("data", data);
					SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noget")+"codeitemid");
					return returndata;
				}
			}
			//parentid
			if(StringUtils.isBlank(parentid)){
				parentid = codeitemid;
			}
			
			//grade
			int grade = 1;
			if(!orgObj.isEmpty()){
				grade = orgObj.getInt("grade")+1;
			}
			//A0000
			
			//levelA0000
			
			//layer
			int layer = 1;
			if(!orgObj.isEmpty() && codesetid.equalsIgnoreCase(orgObj.getString("codesetid"))){
				layer = orgObj.getInt("layer")+1;
			}
			//childid
			String childid = codeitemid;
			
			returndata.put("codesetid", codesetid);
			returndata.put("codeitemid", codeitemid);
			returndata.put("codeitemdesc", codeitemdesc);
			returndata.put("parentid", parentid);
			returndata.put("childid", childid);
			returndata.put("grade", grade);
			returndata.put("layer", layer);
			returndata.put("start_date", start_date);
			returndata.put("end_date", end_date);
			returndata.put("success", true);
			
			//给最大子节点重新赋值
			if(parentid.equalsIgnoreCase(codeitemid)){
				maxCode = codeitemid;
			}else {
				maxCodeJson.put(parentid, codeitemid);
			}
			returndata.put("GUIDKEY", orgNum);
			returndata.put("corCode", orgId);
			returndata.put("codesetid", codesetid);
			
			if(StringUtils.isBlank(codeitemid)){
				returndata.put("success", false);
				returndata.put("id", "");
				returndata.put("msg",  ResourceFactory.getProperty("hrcloud.recieve.error.noorgid"));
				SyncAssessDataLoggerUtil.start("---- organization change error ---- ", new JSONArray(), ResourceFactory.getProperty("hrcloud.recieve.error.noorgid"));
				return returndata;
			}
			returndata.put("codeitemid", codeitemid);
			if(data.containsKey("orgName") && StringUtils.isNotBlank(data.getString("orgName"))){
				returndata.put("codeitemdesc", data.getString("orgName"));
			}
			if(StringUtils.isNotBlank(parentid)){
				returndata.put("parentid", parentid);
			}
			//逻辑删除
			if(operation == 1){
				if(data.containsKey("deleteFlag")){
					returndata.put("deleteFlag", data.getString("deleteFlag"));
				}
//				if(data.containsKey("startDate")){
//					returndata.put("start_date", data.getString("startDate"));
//				}
				if(data.containsKey("endDate")){
					returndata.put("end_date", data.getString("endDate"));
				}
			}
//			if(operation == 0 || operation == 1) {
//				operation = 1;
//			}
			returndata.put("success", true);
		returndata.put("operation", operation);
		return returndata;
	}


	/**
	 * 获取组织架构元数据
	 * 数据类型:
	 * {
	 * 	 tablename:data{
	 * 						GUIDKEY:"DFJIGHIJGWEOIFGJOFMKOSDMJKO",
	 * 						childGUIDKEY:"JFIOGWJHIUFNGVIEJFIOJEGIO",(子集才有)
	 * 						operation:0新增，1修改，2删除
	 * 						itemid:{
	 * 									type:"A"|"D"|"M","N",
	 * 									value:"",
	 * 									cloudcodeset:"AX"(代码型才有)
	 * 								}
	 * 					}
	 * }
	 * @param rs 
	 * @param conn 
	 * @return
	 * { 
	 * 		addArray:[],
	 * 		updArray:[],
	 * 		delArray:[],
	 * 		success:true/false,
	 * 		errCode:"1002",
	 * 		datas:[{id:'XXXXXX',msg:'...',data:{}}]
	 * }
	 * 
	 */
	private HashMap getOrgMetaDataJson(Connection conn, RowSet rs) {
		HashMap returnJson = new HashMap();
		JSONArray addArray = new JSONArray();
		JSONArray updArray = new JSONArray();
		JSONArray delArray = new JSONArray();
		HashMap changeDatas = new HashMap();
		JSONObject idGUIDMap = new JSONObject();
//		JSONArray logList = new JSONArray();
		JSONArray datas = new JSONArray();
		JSONArray list = resDataJson.getJSONArray("list");
		
		//获取到机构
		try {
			boolean flag = true;
			String errorMsg = "";
			
			//获取组织架构中所有的codesetid
			JSONObject GUIDKEYToCodeitemId = SyncDataUtil.getGUIDKEYToObjectMap(conn,rs);
			//获取组织机构下corCode与codeitemid的对应关系
			JSONObject corCodeToCodeitemJson = SyncDataUtil.getCorCodeGUIDKEYToObjectMap(conn, rs);
			//获取组织架构中{codesetid：childId}
			JSONObject codesetidTochildid = SyncDataUtil.getCodeitemidToObjectMap(conn,rs);
			//获取组织机构下最大的A0000
			int maxA0000 = SyncDataUtil.getOrgMaxA0000(conn,rs);
			//获取parent下的最大levelA0000
			JSONObject parentidToLevelA0000 = SyncDataUtil.getParentCodeitemToLevelA0000(conn,rs);
			//根节点下最大的代码codeitemid
			String maxCode = SyncDataUtil.getOrgMaxRootCode(conn,rs);
			//非根节点下最大的代码parentid和codeitemid关系
			JSONObject maxCodeJson = SyncDataUtil.getOrgMaxCodeJson(conn,rs);
			
			//回填数据视图删除的机构集合
			ArrayList orgDelList = new ArrayList();
			
			for(Object o:list){
				JSONObject dataObj = new JSONObject();
				JSONObject structure = (JSONObject)o;
				String uuid = "";
				try {
					
					JSONObject returndata = getHrORGSingelData(conn,structure,GUIDKEYToCodeitemId,corCodeToCodeitemJson,maxCode,maxCodeJson);
					
					if(returndata.getBoolean("success")){
						RecordVo vo = new RecordVo("organization");
						if(returndata.containsKey("codesetid")){
							vo.setString("codesetid", returndata.getString("codesetid"));
						}
						if(returndata.containsKey("codeitemid")){
							vo.setString("codeitemid", returndata.getString("codeitemid"));
						}
						if(returndata.containsKey("codeitemdesc")){
							vo.setString("codeitemdesc", returndata.getString("codeitemdesc"));
						}
						if(returndata.containsKey("parentid")){
							vo.setString("parentid", returndata.getString("parentid"));
						}
						if(returndata.containsKey("childid")){
							vo.setString("childid", returndata.getString("childid"));
						}
						if(returndata.containsKey("grade")){
							vo.setInt("grade", returndata.getInt("grade"));
						}
						if(returndata.containsKey("layer")){
							vo.setInt("layer", returndata.getInt("layer"));
						}
						if(returndata.containsKey("start_date")){
							vo.setDate("start_date", returndata.getString("start_date"));
						}
						if(returndata.containsKey("end_date")){
							vo.setDate("end_date", returndata.getString("end_date"));
						}
						if(returndata.containsKey("GUIDKEY")){
							vo.setString("guidkey", returndata.getString("GUIDKEY"));
						}
						if(returndata.containsKey("corCode")){
							vo.setString("corcode", returndata.getString("corCode"));
						}
						
						uuid = returndata.getString("uuid");
						int operation = returndata.getInt("operation");
						
						if(operation == 0){
							String codeitemid = returndata.getString("codeitemid");
							String parentid = returndata.getString("parentid");
							//A0000
							maxA0000++;
							vo.setInt("a0000", maxA0000);
							//levelA0000
							int levela0000 = 0;
							/*if(!parentid.equals(codeitemid) && GUIDKEYToCodeitemId.containsKey(parentidGUID) 
									&& codesetid.equalsIgnoreCase(GUIDKEYToCodeitemId.getJSONObject(parentidGUID).getString("codesetid"))
									&& parentidToLevelA0000.containsKey(parentid)){*/
							if(!parentid.equals(codeitemid) && parentidToLevelA0000.containsKey(parentid)){
								levela0000 = parentidToLevelA0000.getInt(parentid);
							}
							levela0000++;
							parentidToLevelA0000.put(parentid,levela0000);
							vo.setInt("levela0000",levela0000);
							//父节点的childid
							if(!codeitemid.equalsIgnoreCase(parentid) && codesetidTochildid.containsKey(parentid)){
								JSONObject parObj = codesetidTochildid.getJSONObject(parentid);
								if(parObj.getString("codeitemid").equalsIgnoreCase(parObj.getString("childid"))){
									//修改父节点的childid
									if(changeDatas.containsKey(uuid)){
										//预处理数据中存在父部门
										RecordVo parVo = (RecordVo) changeDatas.get(uuid);
										parVo.setString("childid", codeitemid);
										changeDatas.put(uuid, parVo);
									}else{
										//修改数据库中的数据
										RecordVo parVo = new RecordVo("organization");
										parVo.setString("codesetid", parObj.getString("codesetid"));
										parVo.setString("codeitemid", parObj.getString("codeitemid"));
										parVo.setString("childid", codeitemid);
										updArray.add(parentid);
										changeDatas.put(parentid, parVo);
									}
								}
							}
							
							//corCodeToCodeitemJson中新增对应关系
							if(returndata.containsKey("corCode") && StringUtils.isNotBlank(returndata.getString("corCode"))){
								JSONObject data = new JSONObject();
								data.put("corCode", returndata.getString("corCode"));
								data.put("codesetid", returndata.getString("codesetid"));
								data.put("codeitemid", returndata.getString("codeitemid"));
								data.put("parentid", returndata.getString("parentid"));
								data.put("childid", returndata.getString("childid"));
								data.put("grade", returndata.getInt("grade"));
								data.put("layer", returndata.getInt("layer"));
								corCodeToCodeitemJson.put(returndata.getString("corCode").toUpperCase(),data);
							}
							//GUIDKEYToCodeitemId中新增对应关系
							JSONObject data = new JSONObject();
							data.put("GUIDKEY", returndata.getString("GUIDKEY"));
							data.put("codesetid", returndata.getString("codesetid"));
							data.put("codeitemid", returndata.getString("codeitemid"));
							data.put("parentid", returndata.getString("parentid"));
							data.put("childid", returndata.getString("childid"));
							data.put("grade", returndata.getInt("grade"));
							data.put("layer", returndata.getInt("layer"));
							GUIDKEYToCodeitemId.put(returndata.getString("GUIDKEY").toUpperCase(),data);
								
//							addArray.add(uuid);
							updArray.add(uuid);
						}else if(operation == 1){
							updArray.add(uuid);
							if(returndata.containsKey("deleteFlag")){
								if("1".equals(returndata.getString("deleteFlag"))){
									Date date = new Date();
									Calendar calendar = Calendar.getInstance();
						            //将当期日期设置进去
						            calendar.setTime(date);
						            //对天进行减1天
						            calendar.add(Calendar.DAY_OF_MONTH, -1);
						            //获取昨天的Date对象
						            Date yesterdayDate = calendar.getTime();
									vo.setDate("end_date", yesterdayDate);
									orgDelList.add(returndata.getString("codeitemid"));
								}
							}
						}else if(operation == 2){
							delArray.add(uuid);
							//更新父节点的childid
							String codeitemid = returndata.getString("codeitemid");
							if(returndata.containsKey("parentid") && !codeitemid.equalsIgnoreCase( returndata.getString("parentid"))){
								String parentid = returndata.getString("parentid");
								//查询父节点下最小的codeitemid
								String minChildid = SyncDataUtil.getMinChildId(conn,rs,parentid,codeitemid);
								//修改数据库中的数据
								JSONObject parObj = codesetidTochildid.getJSONObject(parentid);
								RecordVo parVo = new RecordVo("organization");
								parVo.setString("codesetid", parObj.getString("codesetid"));
								parVo.setString("codeitemid", parObj.getString("codeitemid"));
								parVo.setString("childid", minChildid);
								updArray.add(parentid);
								changeDatas.put(parentid, parVo);
							}
						}
						changeDatas.put(uuid, vo);
						idGUIDMap.put(uuid, returndata.getString("GUIDKEY"));
					}else{
						flag = false;
						datas.add(returndata);
					}
				}catch (Exception e) {
					JSONObject returndata = new JSONObject();
					returndata.put("success", false);
					returndata.put("id", uuid);
					returndata.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.exception"));
					returndata.put("data", structure);
					continue;
				}
			}
			returnJson.put("addArray", addArray);
			returnJson.put("updArray", updArray);
			returnJson.put("delArray", delArray);
			returnJson.put("changeDatas", changeDatas);
			returnJson.put("idGUIDMap", idGUIDMap);
			returnJson.put("orgDelList", orgDelList);
			
			returnJson.put("success", flag);
			if(datas.isEmpty()){
				returnJson.put("errCode", "");
			}else{
				returnJson.put("errCode", "1002");
			}
//			returnJson.put("logList", logList);
			returnJson.put("datas", datas);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJson;
	}



	/**
	 * 将请求数据保存到数据日志
	 */
	private void saveDataLog(){
		JSONObject retJson = new JSONObject();
		JSONArray list = resDataJson.getJSONArray("list");
		int type = resDataJson.getInt("type");
		
		//存到数据日志
		String dataDesc = "";
		
		if(type == 1){
			//1待办
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.dbtask");
		}else if(type == 2){
			//2待办处理结果
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.dbdealresult");
		}else if(type == 3){
			//3考核结果发布
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.khresultpublish");
		}else if(type == 4){
			//4考核结果取消发布
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.khresultcancel");
		}else if(type == 5){
			//5人员变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.personchange");
		}else if(type == 6){
			//6组织架构变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.orgchange");
		}else if(type == 7){
			//7待办提醒   已无此项
		}else if(type == 8){
			//8单位变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.unitchange");
		}else if(type == 9){
			//9部门变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.depchange");
		}else if(type == 10){
			//10岗位变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.postchange");
		}else if(type == 11){
			//11职位变动  已无此项
		}else if(type == 12){
			//12岗位序列变动
			dataDesc = ResourceFactory.getProperty("hrcloud.recieve.postorderchange");
		}else{
			
		}
		
		SyncAssessDataLoggerUtil.start(dataDesc+" | type="+type, list, "");
	}

	/**
	 * 添加待办事项
	 * @param rs2 
	 * @param conn2 
	 * @return
	 */
	private JSONObject addSchedule(Connection conn, RowSet rs) {
		JSONObject retObj = new JSONObject();
		String success = "true";
		String code = "";
		JSONArray failReport = new JSONArray();
		JSONObject data = new JSONObject(); 
		try{
			ContentDAO dao = new ContentDAO(conn);
			ArrayList vo_list = new ArrayList();
			ArrayList upd_list = new ArrayList();
			JSONArray list = resDataJson.getJSONArray("list");
	//		JSONObject usernamesMap = SyncDataUtil.getGUIDKEYToNbaseA0100Map(conn, rs);
	//		JSONObject extFlagToPeddIngId = SyncDataUtil.getExtFlagToPeddIngId(dao,conn,rs);
			
			HashSet creatorSet=new HashSet();
			HashSet businessIdSet=new HashSet();
			for(Object o : list){
				JSONObject json = (JSONObject)o;
				String creator = json.getString("creator").trim().toUpperCase();
				if(!StringUtils.isEmpty(creator)) {
					creatorSet.add(creator);
				}
				String businessId = json.getString("businessId").trim().toUpperCase();
				if(!StringUtils.isEmpty(businessId)) {
					businessIdSet.add(businessId);  
				}
			}
			if(creatorSet.size()==0)
			{
				success = "false";
				data.put("success", success);
				data.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.nohrrelation")); 
				retObj.put("data",data);
				return retObj;
			}
			
			HashMap usernamesMap=SyncDataUtil.getNbaseA0100ByGUIDKEYMap(conn, rs,creatorSet);
			HashMap extFlagToPeddIngId=SyncDataUtil.getPeddIngTaskIdMap(dao,conn,rs,businessIdSet);
			ArrayList weixinDataList=new ArrayList(); 
			
			for(Object o : list){
				JSONObject json = (JSONObject)o;
				String creator = json.getString("creator").toUpperCase();
				//未找到对应的人员ID
				if(!usernamesMap.containsKey(creator)){
					success = "false";
					data.put("success", success);
					data.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.nohrrelation"));
					data.put("creator",creator);
					retObj.put("data",data);
					return retObj;
				}
				//云的访问URL
//				String url = SyncConfigUtil.getCloudLogonUrl(creator,token,1);
				if(!json.containsKey("url") || "null".equals(json.getString("url")) || StringUtils.isBlank(json.getString("url"))){
					success = "false";
					data.put("success", success);
					data.put("msg",ResourceFactory.getProperty("hrcloud.recieve.error.nourl"));
					data.put("creator",creator);
					retObj.put("data",data);
					return retObj;
				}
				
				String businessId = json.getString("businessId").toUpperCase(); 
				String username =(String) usernamesMap.get(creator); 
				//判断待办表中是否存在相同标识的待办数据
				String pendingid = ""; 
				if(extFlagToPeddIngId.containsKey(businessId)){
					pendingid =(String) extFlagToPeddIngId.get(businessId);
				}

				RecordVo vo = new RecordVo("t_hr_pendingtask");
				vo.setString("pending_title", json.getString("title"));
				String url = json.getString("url");
				vo.setString("pending_url", url);
				vo.setString("pending_status", "0");
				//待办级别 0：一般，1：紧急 2：特急
				vo.setString("pending_level", "0");
				//云 所属模块固定为"99"
				vo.setString("pending_type", "99");
				vo.setString("receiver", username);
				vo.setString("sender", username);
				String time = json.getString("time");
				SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
				Date date = sdf.parse(time);
 
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("nbase",username.substring(0,3));
				bean.set("a0100",username.substring(3));
				bean.set("cloudpendmsg",ResourceFactory.getProperty("hrcloud.recieve.error.cloudpendmsg"));
				bean.set("title",json.getString("title"));
				bean.set("url",url); 
				weixinDataList.add(bean);
				 
				if(StringUtils.isBlank(pendingid)){
					IDGenerator idg = new IDGenerator(2, conn);
					pendingid = idg.getId("pengdingTask.pengding_id");
					extFlagToPeddIngId.put(businessId, pendingid);
					vo.setInt("pending_id", Integer.parseInt(pendingid));
					vo.setString("ext_flag", businessId);
					vo.setDate("create_time", DateStyle.dateformat(date,"yyyy-MM-dd HH:mm:ss"));
					vo_list.add(vo);
				}else{
					vo.setInt("pending_id", Integer.parseInt(pendingid));
					vo.setDate("create_time", DateStyle.dateformat(date,"yyyy-MM-dd HH:mm:ss"));
					vo.setDate("lasttime", DateStyle.dateformat(date,"yyyy-MM-dd HH:mm:ss"));
					upd_list.add(vo);
				}
			} 
			if(!vo_list.isEmpty()){
			    dao.addValueObject(vo_list);
			}
			if(!upd_list.isEmpty()){
				dao.updateValueObject(upd_list);
			}
			LazyDynaBean abean=null;
			try {
				String sendBy = SystemConfig.getPropertyValue("weitalk_sendBy");
				if(StringUtils.isNotBlank(sendBy)){
					for(Iterator t=weixinDataList.iterator();t.hasNext();)
					{ 
						abean=(LazyDynaBean)t.next();
						String nbase=(String)abean.get("nbase");
						String a0100=(String)abean.get("a0100");
						String cloudpendmsg=(String)abean.get("cloudpendmsg");
						String title=(String)abean.get("title");
						String url=(String)abean.get("url"); 
					    WeiXinBo.sendMsgToPerson(nbase,a0100,cloudpendmsg,title, "", url); 
					}
				}
			}catch (Exception e) {
				log.error("send weitalk message error");
				data.put("msg","send weitalk message error");
			}
			
			
		 
		}catch (Exception e) {
			success = "false";
			data.put("msg","sync Exception");
			e.printStackTrace();
		}finally{ 
			data.put("success", success);
			retObj.put("data",data);
		}
		return retObj;
	}

	
	/**
	 * 更新代办项
	 * @param rs 
	 * @param conn2 
	 * @return
	 */
	private JSONObject updSchedule(Connection conn, RowSet rs) {
		JSONObject retObj = new JSONObject();
		String success = "false";
		try{
			ContentDAO dao = new ContentDAO(conn);
			ArrayList vo_list = new ArrayList();
			
			JSONArray list = resDataJson.getJSONArray("list");
			for(Object o : list){
				JSONObject json = (JSONObject)o;
				String businessId = json.getString("businessId").toUpperCase();
				String sql = " update t_hr_pendingtask set pending_status = '1' where UPPER(ext_flag) = '"+businessId.toUpperCase()+"' ";
				vo_list.add(sql);
			}
			dao.batchUpdate(vo_list);
			success = "true";
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			JSONObject data = new JSONObject();
			data.put("success", success);
			retObj.put("data",data);
		}
		return retObj;
	}





	/**
	 * 考核结果发布
	 * @param rs 
	 * @param conn 
	 * @return
	 */
	public JSONObject assessmentResultRelease(Connection conn, RowSet rs){
		JSONObject retJson = new JSONObject();
		JSONArray list = resDataJson.getJSONArray("list");
		
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
		boolean success = true;
		String code = "";
		JSONArray failReport = new JSONArray();
		int id = 0;
		
		JSONObject returnJson = new JSONObject();
		HashMap returnMap = SyncConfigUtil.getHrAssessConnectConfig();
		JSONObject orgAssessSet = (JSONObject) returnMap.get("orgAssessSet");
		JSONObject empAssessSet = (JSONObject) returnMap.get("empAssessSet");
		
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
				String businessId = StringUtils.isBlank(jsonmap.getString("businessId"))?"":jsonmap.getString("businessId");
				String tenantId = jsonmap.getString("tenantId");
				String name = StringUtils.isBlank(jsonmap.getString("name"))?"":jsonmap.getString("name");
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
					String sheetName = StringUtils.isBlank(object.getString("sheetName"))?"":object.getString("sheetName");
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
						//xus 19/12/24 【56814 】v77发版：云集成，云平台发布360考核结果，在hr系统员工管理中结果等级显示为“null”
						String degreeName = (scoreinfoJson.get("degreeName")==null || "null".equalsIgnoreCase(scoreinfoJson.getString("degreeName"))|| StringUtils.isBlank(scoreinfoJson.getString("degreeName")))?"":scoreinfoJson.getString("degreeName");
						
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
						if(assessSet.get("planDate")!=null&&!StringUtils.isBlank(planDate)){
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
		}
		return retJson;
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
		
	/**
	 * 通过guidkey获取A0100
	 * @param guidkey
	 */
/*	private String getGUIDKEYToUserNameMap() {
		Connection conn= null;
		RowSet rs = null;
		String username = "";
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
			AttestationUtils utils=new AttestationUtils();
	        LazyDynaBean fieldbean=utils.getUserNamePassField();
	        String username_field=(String)fieldbean.get("name");
	        
			ArrayList<String> dbnames = new ArrayList<String>();
			String sql = " select pre from DBName ";
			rs = dao.search(sql);
			while(rs.next()){
				dbnames.add(rs.getString("pre"));
			}
			sql = "";
			for(int i = 0 ; i < dbnames.size() ; i++){
				if(i>0){
					sql += " union ";
				}
				sql += " select "+username_field+" username from "+dbnames.get(i)+"A01 ";
			}
			rs = dao.search(sql);
			while(rs.next()){
				username = rs.getString("username");
			}
		}catch (Exception e) {
			
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		return username;
	}*/
	
	/**
	 * 撤销考核结果
	 * @param list
	 * @param rs 
	 * @param conn2 
	 * @return
	 */
	private JSONObject cancelAssessResult(JSONArray list, Connection conn, RowSet rs) {
		JSONObject returnJson = new JSONObject();
		
		
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
		}
		return returnJson;
	}
	
	/**
	 * 人员变动
	 * @param list
	 * @return
	 */
/*	private JSONObject cloudEmpChangeToHr(JSONArray list) {
		JSONObject returnJson = new JSONObject();
		
		//1、获取云与hr指标对应
		JSONObject cloudTOHrfieldsMap = getCloudEmpConnected("1");
		//2、处理数据、分类增删改
		JSONObject dataMap = getEmpDataListMap(list,cloudTOHrfieldsMap);
		//3、批量处理
		returnJson = batchRecordEmpDataToHr(dataMap);
		
		return returnJson;
	}*/
	
	/**
	 * 将云中的数据 批量保存到hr系统
	 * @param dataMap
	 * @return
	 */
	/*private JSONObject batchRecordEmpDataToHr(JSONObject dataMap) {
		Connection conn = null;
		RowSet rs = null;
		try{
			
			JSONArray addArray = dataMap.getJSONArray("addArray");
			JSONArray updArray = dataMap.getJSONArray("updArray");
			JSONArray delArray = dataMap.getJSONArray("delArray");
			
			addArray.addAll(updArray);
			HrSyncBo hsb = new HrSyncBo(conn);
			String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
			String[] dbnames = dbnamestr.split(",");
			
			JSONObject json = CloudConstantParams.getParamJson();
			json.getString("cloudTOhr");
			
			JSONObject GUIDKEYToNbase = getAllA01GUIDKEYToNbaseJson(conn,rs);
			
			JSONArray array = insertAndUpdateToHr(addArray,GUIDKEYToNbase);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		return null;
	}*/

	/**
	 * 新增或修改到hr系统
	 * @param addArray
	 * @param GUIDKEYToNbase 
	 */
	private JSONArray insertAndUpdateToHr(JSONArray addArray, JSONObject GUIDKEYToNbase) {
		JSONArray returnArray = new JSONArray();
		RecordVo vo = null;
		
//		JSONObject json = CloudConstantParams.getParamJson();
//		json.getString("cloudTOhr");
//		
//		JSONObject GUIDKEYToNbase = getAllA01GUIDKEYToNbaseJson();
		
		for(Object o : addArray){
			JSONObject data = (JSONObject)o;
			if(!data.containsKey("childGUIDKEY")){
				//主集
				String GUIDKEY = data.getString("GUIDKEY");
				String table = "A01";
				if(GUIDKEYToNbase.containsKey(GUIDKEY)){
					table = GUIDKEYToNbase.getString(GUIDKEY)+table;
				}else{
					table = "Usr"+table;
				}
				vo = new RecordVo(table);
				vo.setString("GUIDKEY", GUIDKEY);
				for(Object obj :data.keySet()){
					String itemid = (String)obj;
					JSONObject json = data.getJSONObject(itemid);
					String itemType = json.getString("type");
					String value = json.getString("value");
					if("A".equalsIgnoreCase(itemType)){
						if(json.containsKey("cloudcodeset")){
							vo.setInt(itemid, json.getInt("value"));
						}else{
							vo.setString(itemid, value);
						}
					}else if("D".equalsIgnoreCase(itemType)){
						vo.setDate(itemid, value);
					}else if("M".equalsIgnoreCase(itemType)){
						vo.setString(itemid, value);
					}else if("N".equalsIgnoreCase(itemType)){
						vo.setDouble(itemid, Integer.parseInt(value));
					}
				}
			}else{
				//子集
			}
		}
		return addArray;
	}


	/**
	 * 获取所有GUIDKEY与Nbase对应的jsonmap
	 * @param conn 
	 * @param rs 
	 * @return
	 */
	private JSONObject getAllA01GUIDKEYToNbaseJson(Connection conn, RowSet rs) {
		JSONObject returnJson = new JSONObject();
	    try {
	    	String sql = "";
	    	HrSyncBo hsb = new HrSyncBo(conn);
	    	String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
	    	String[] dbnames = dbnamestr.split(",");
	    	for(int i = 0 ; i < dbnames.length ; i++ ){
	    		String dbname = dbnames[i];
	    		if(i>0){
	    			sql += " union ";
	    		}
	    		sql += " select GUIDKEY,'"+dbname+"' nbase from "+dbname+"A01 ";
	    	}
	    	ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				returnJson.put(rs.getString("GUIDKEY"), rs.getString("nbase"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnJson;
	}


	/**
	 * 获取人员数据list
	 * @param list
	 * @return
	 */
	/*private JSONObject getEmpDataListMap(JSONArray list,JSONObject cloudTOHrfieldsMap) {
		JSONObject returnJson = new JSONObject();
		try {
			
			JSONObject hrCodeRelationJson = getHrCodeRelationJson();
			
			JSONArray addArray = new JSONArray();
			JSONArray updArray = new JSONArray();
			JSONArray delArray = new JSONArray();
			for(int i = 0 ; i<list.size();i++){
				JSONObject data = new JSONObject();
				JSONObject staff = list.getJSONObject(i);
	//			String id = staff.getString("id"); 云id 暂时没有用到
				String thirdId = staff.getString("thirdId");
				int operation = staff.getInt("operation");
				
				//主集
				data = getConnectedDataJson(staff,cloudTOHrfieldsMap,hrCodeRelationJson);
				data.put("GUIDKEY", thirdId);
				data.put("operation", operation);
				
				if(operation == 0){
					addArray.add(data);
				}else if(operation == 1){
					updArray.add(data);
				}else if(operation == 2){
					delArray.add(data);
				}
				
				//子集
				JSONArray subsets = staff.getJSONArray("subsets");
				for(Object o : subsets){
					JSONObject subset = (JSONObject)o;
					String subsetId = subset.getString("id");
					operation = subset.getInt("operation");
					data = new JSONObject();
					data = getConnectedDataJson(subset,cloudTOHrfieldsMap,hrCodeRelationJson);
					data.put("childGUIDKEY", subsetId);
					data.put("GUIDKEY", thirdId);
					data.put("operation", operation);
					if(operation == 0){
						addArray.add(data);
					}else if(operation == 1){
						updArray.add(data);
					}else if(operation == 2){
						delArray.add(data);
					}
				}
			}
			returnJson.put("addArray", addArray);
			returnJson.put("updArray", updArray);
			returnJson.put("delArray", delArray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJson;
	}*/

	/**
	 * 获取云中代码对应关系，格式：
	 * {
	 * 		codesetid`codeitemid:hr_codeitemid
	 * }
	 * @return
	 */
	private JSONObject getHrCodeRelationJson(Connection conn,RowSet rs) {
		JSONObject returnObject = new JSONObject();
		
		try {
			String sql = " select codesetid,codeitemid,hr_codesetid,hr_codeitemid from t_sys_hrcloud_codematch ";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				String codesetid = rs.getString("codesetid");
				String codeitemid = rs.getString("codeitemid");
//				String hr_codesetid = rs.getString("hr_codesetid");
				String hr_codeitemid = rs.getString("hr_codeitemid");
				
				returnObject.put(codesetid+"`"+codeitemid, hr_codeitemid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObject;
	}
	


	/**
	 * 获取指标对应后的数据
	 * @param json
	 * @param cloudTOHrfieldsMap
	 * @param table 
	 * @return
	 * @throws SQLException 
	 */
	private RecordVo getConnectedDataJson(JSONObject json,JSONObject cloudTOHrfieldsMap,JSONObject hrCodeRelationJson, String table) throws SQLException{
		RecordVo vo = new RecordVo(table);
		for(Object o : json.keySet()){
			String key = (String)o;
			//不包含subsets 循环Staff中的指标项
			if(cloudTOHrfieldsMap.containsKey(key) && !"id".equalsIgnoreCase(key) && !"operation".equalsIgnoreCase(key) && !"posList".equalsIgnoreCase(key)){
				try{
					
					JSONObject field = cloudTOHrfieldsMap.getJSONObject(key);
					if(field.getJSONObject("connectField") == null || field.getJSONObject("connectField").getString("itemid") == null){
						continue;
					}
					JSONObject item = new JSONObject();
					String hrItemId = field.getJSONObject("connectField").getString("itemid");
					String value = "";
					//A:字符型  D:日期型  N:数值型  M:备注型
					if("A".equalsIgnoreCase(field.getString("type"))){
						//指标为空时
						if(json.getString(key) == null || "null".equals(json.getString(key)) || StringUtils.isEmpty(json.getString(key))){
							vo.setString(hrItemId.toLowerCase(), null);
							continue;
						}
						if(!field.containsKey("codesetid") || field.getString("codesetid") == null || "0".equalsIgnoreCase(field.getString("codesetid")) || "null".equalsIgnoreCase(field.getString("codesetid"))){
							//字符型
							value = json.getString(key);
						}else{
							//代码型
							//单位
							String code = json.getString(key);
							String codesetid = field.getString("codesetid");
							if(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(code) && hrCodeRelationJson.containsKey(codesetid+"`"+code)){
								value = hrCodeRelationJson.getString(codesetid+"`"+code);
								item.put("cloudcodeset", codesetid);
							}
						}
						vo.setString(hrItemId.toLowerCase(), value);
					}else if("D".equalsIgnoreCase(field.getString("type"))){
						//指标为空时
						if(json.getString(key) == null || "null".equals(json.getString(key)) || StringUtils.isEmpty(json.getString(key))){
							continue;
						}
						//日期型
						value = json.getString(key);
						vo.setDate(hrItemId.toLowerCase(), value);
					}else if("M".equalsIgnoreCase(field.getString("type"))){
						//指标为空时
						if(json.getString(key) == null || "null".equals(json.getString(key)) || StringUtils.isEmpty(json.getString(key))){
							vo.setString(hrItemId.toLowerCase(), null);
							continue;
						}
						value = json.getString(key);
						vo.setString(hrItemId.toLowerCase(), value);
					}else if("N".equalsIgnoreCase(field.getString("type"))){
						//指标为空时
						if(json.getString(key) == null || "null".equals(json.getString(key)) || StringUtils.isEmpty(json.getString(key))){
							vo.setDouble(hrItemId.toLowerCase(), 0);
							continue;
						}
						vo.setDouble(hrItemId.toLowerCase(), json.getDouble(key));
					}
				}catch (Exception e) {
					//TODO 指标项获取失败
					continue;
				}
			}
		}
		return vo;
	}
	/**
	 * 获取云中的
	 * @param code
	 * @param codeset
	 * @param conn 
	 * @return
	 * @throws SQLException 
	 */
	private String getHrCodeValue(JSONObject code, String codeset, Connection conn,RowSet rs) throws SQLException {
		String codevalue = null;
		String codeitemid = code.getString("id");
		String sql = " select codesetid,codeitemid,hr_codesetid,hr_codeitemid from t_sys_hrcloud_codematch where codesetid = ? and codeitemid = ? ";
		ArrayList values = new ArrayList();
		values.add(codeset);
		values.add(codeitemid);
		ContentDAO dao = new ContentDAO(conn);
		rs = dao.search(sql,values);
		if(rs.next()){
			codevalue = rs.getString("hr_codeitemid");
		}
		return codevalue;
	}




	/**
	 * 获取人员同步关联的map
	 * @param rs 
	 * @param conn 
	 * @return returnJson:
{
    //主集
	"XX": {
		cloudset_id: "XX",
		hr_set: "A01",
		fields: {
			id(云指标编码): field(Constant表中配置 格式如下： {
				id: ’name’,
				name: ’姓名’,
				type: ’A’,
				length: 10,
				dlength: 0,
				codesetid: ’0’,
				required: true,
				//关联子集的指标
				connectField: {
					itemid: ’A0101’,
					itemdesc: ’姓名’
				}
			})
		}
	},
	"XXX": { ...
	}
}
	 */
	private JSONObject getCloudEmpConnected(String type, Connection conn, RowSet rs) {
		JSONObject returnJson = new JSONObject();
		
		JSONObject json = SyncDataUtil.getParamJson();
		//人员
//		if("1".equalsIgnoreCase(type) && json.containsKey("tables") && json.getJSONArray("tables") != null){
//			JSONArray tables = json.getJSONArray("tables");
//			JSONObject table = tables.getJSONObject(0);
//			String cloudset_id = "main";
//			String hr_set = "A01"; 
//			String set_type = "1";
//			JSONArray cloud_fields = table.getJSONArray("fields");
//			JSONObject fields = new JSONObject();
//			for(Object obj : cloud_fields){
//				JSONObject field = (JSONObject)obj;
//				fields.put(field.getString("id"), field);
//			}
//			JSONObject newset = new JSONObject();
//			newset.put("cloudset_id", cloudset_id);
//			newset.put("hr_set", hr_set);
//			newset.put("set_type", set_type);
//			newset.put("fields", fields);
//			returnJson.put(cloudset_id, newset);
//			return returnJson;
//		}
		//单位部门岗位
		if( json.getJSONObject("cloudTOhr") != null && json.getJSONObject("cloudTOhr").containsKey("setMapping")){
			JSONArray setMapping = json.getJSONObject("cloudTOhr").getJSONArray("setMapping");
			for(Object o : setMapping){
				JSONObject set = (JSONObject)o;
				if(set.containsKey("set_type") && type.equalsIgnoreCase(set.getString("set_type"))){
					String cloudset_id = set.getString("cloudset_id");
					if(!set.containsKey("isChildSet") || "0".equals(set.getString("isChildSet"))){
						cloudset_id = "main";
					}
					String hr_set = set.getString("hr_set");
					JSONObject newset = new JSONObject();
					newset.put("cloudset_id", cloudset_id);
					newset.put("hr_set", hr_set);
					newset.put("set_type", set.getString("set_type"));
					JSONObject fields = new JSONObject();
					JSONArray cloud_fields = set.getJSONArray("cloud_fields");
					for(Object obj : cloud_fields){
						JSONObject field = (JSONObject)obj;
						fields.put(field.getString("id"), field);
					}
					newset.put("fields", fields);
					returnJson.put(cloudset_id, newset);
//					break;
				}
			}
		}
		return returnJson;
	}
}