package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.module.system.hrcloud.businessobject.SyncConfigService;
import com.hjsj.hrms.module.system.hrcloud.businessobject.impl.SyncConfigServiceImpl;
import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncCloudDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncConfigTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
//		Map map = new HashMap(); 
		JSONObject constantParamJson= new JSONObject();
		try{
			String transType = (String)this.getFormHM().get("transType");
//			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			SyncConfigService scs = new SyncConfigServiceImpl(this.userView, this.frameconn);
			constantParamJson = scs.getConstantParamJson();
			if("loadAPIConfig".equals(transType)){
				//操作类型，加载云平台参数
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", scs.getCloudAPIConfigData());
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("saveFirstPage".equals(transType)){
				//保存第一页数据
				MorphDynaBean configDataBean = (MorphDynaBean)this.getFormHM().get("json_str");
				scs.saveCloudAPIConfigData(configDataBean);
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", scs.getCloudAPIConfigData());
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("loadFieldMap".equals(transType)){
				//操作类型，加载指标对应关系
				String appId = (String)this.getFormHM().get("appId");
				String tenantId = (String)this.getFormHM().get("tenantId");
				String appSecret = (String)this.getFormHM().get("appSecret");
				JSONObject return_data = new JSONObject();
				
				//获取云平台指标
				JSONObject cloudReturnJson = getCloudFieldItem(appId,tenantId,appSecret,scs);
				scs.setCloudFieldDataJson(cloudReturnJson);
				SyncConfigUtil.setCloudFieldDataJson(cloudReturnJson);
				
				//hrTOcloud
				JSONObject hrTOcloud = scs.getCloudDataJson();
				//cloudset、currentSet
				JSONArray cloudTOhrSet = scs.getCloudTOhrSet();
//				JSONObject cloudTOhrCurrentSet = scs.getCloudTOhrCurrentSet("");
				
				JSONObject cloudTOhr = new JSONObject();
				cloudTOhr.put("cloudset", cloudTOhrSet);
//				cloudTOhr.put("currentSet", cloudTOhrCurrentSet);
				
				return_data.put("hrTOcloud", hrTOcloud);
				return_data.put("cloudTOhr", cloudTOhr);
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", return_data);
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
				
			}else if("loadCurrentSet".equals(transType)){
				scs.setCloudFieldDataJson(SyncConfigUtil.getCloudFieldDataJson());
				
				String id = (String)this.getFormHM().get("id");
				JSONObject cloudTOhrCurrentSet = scs.getCloudTOhrCurrentSet(id);
				JSONObject cloudTOhr = new JSONObject();
				cloudTOhr.put("currentSet", cloudTOhrCurrentSet);
				JSONObject return_data = new JSONObject();
				return_data.put("cloudTOhr", cloudTOhr);
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", return_data);
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("loadSyncConfig".equals(transType)){
				JSONObject return_data = new JSONObject();
				return_data.put("used", constantParamJson.get("used"));
				return_data.put("frequency", constantParamJson.get("frequency"));
				
				ArrayList dblist = getDbList();
				return_data.put("dblist", dblist);
				
				boolean isRemind = isRemind();
				return_data.put("isRemind", isRemind);
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", return_data);
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("save".equals(transType)){
				//操作类型，保存数据
				MorphDynaBean configDataBean = (MorphDynaBean)this.getFormHM().get("json_str");
				JSONObject strValue = scs.getStrValue(configDataBean);
				scs.saveAllCloudConfigData(configDataBean,strValue);
				//保存并且启用状态为暂停的时候 不刷新后台作业
				boolean isSave = (Boolean) this.getFormHM().get("isSave");
				if(!isSave||!"0".equals(strValue.getString("used"))){
					scs.refreshWarnScanJob();
				}
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", "");
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("autoMatch".equals(transType)){
				String json_str = (String)this.getFormHM().get("json_str");
			    //hr同步到云指标自动关联
			    JSONObject configDataBean = JSONObject.fromObject(json_str);
			    
//				MorphDynaBean configDataBean = (MorphDynaBean)this.getFormHM().get("json_str");
				//hr同步到云指标自动关联
				JSONArray  tabelsList = configDataBean.getJSONArray("fields");
				JSONObject matchMap = scs.getMatchConfig(tabelsList);
				
				//云同步到hr指标自动关联
				JSONObject cloudTOhr = (JSONObject) configDataBean.get("cloudTOhr");
				JSONArray setMapping = cloudTOhr.getJSONArray("setMapping");
				JSONObject cloudmatchMap = scs.getCloudMatchJson(setMapping);
				
				JSONObject return_data = new JSONObject();
				return_data.put("fields", matchMap);
				return_data.put("cloudTOhr", cloudmatchMap);
				
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", return_data);
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("cleanInterfaceData".equals(transType)){
				//清空接口数据
				scs.cleanInterfaceData();
				
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_data", "");
				returnStr.put("return_msg", "");
				returnStr.put("return_code", "success");
				this.getFormHM().put("returnStr", returnStr);
			}
		}catch (Exception e) {
			e.printStackTrace();
			JSONObject returnStr = new JSONObject();
			returnStr.put("flag", false);
//			this.getFormHM().put("return_msg", e.);
			returnStr.put("return_code", "fail");
			this.getFormHM().put("returnStr", returnStr);
		}finally {
		}
	}
	
	
	/**
	 * 清空接口数据
	 * @param recordVo
	 * @return
	 */
	public boolean cleanInterfaceData(RecordVo recordVo){
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(frameconn);
			//1、清除constant表中数据
			if (recordVo != null) {
				recordVo.setString("str_value", "");
				dao.updateValueObject(recordVo);
			}
			//2、清除日志表"t_sys_hrcloud_synclog"中数据
			String sql = "delete from t_sys_hrcloud_synclog";
			dao.update(sql);
			//3、清除tomcat下的日志文件
			//清除数据同步日志
			SyncCloudDataLoggerUtil.cleanCloudFileList();
			//清除考核结果日志
			SyncAssessDataLoggerUtil.cleanCloudFileList();
			flag = true;
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
/**
 * 保存第一页参数
 * @param configDataBean
 * @param recordVo
 * @throws SQLException 
 * @throws GeneralException 
 */
	public void saveConfigData(MorphDynaBean configDataBean, RecordVo recordVo) throws GeneralException, SQLException {
		String appId = configDataBean.get("appId") != null?(String)configDataBean.get("appId"):"";
		String tenantId = configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"";
		String appSecret = configDataBean.get("appSecret") != null?(String)configDataBean.get("appSecret"):"";
		JSONObject json = new JSONObject();
		if (recordVo != null) {
			String str = recordVo.getString("str_value");
			Map jsonMap = new HashMap();
			if(StringUtils.isNotEmpty(str)){
				json = JSONObject.fromObject(str);
			}
		}
		json.put("appId", appId);
		json.put("tenantId", tenantId);
		json.put("appSecret", appSecret);
		saveJsonToConstant(json,recordVo);
	}
	
	public void saveAcceptAssessData(MorphDynaBean configDataBean, RecordVo recordVo) throws GeneralException, SQLException {
		//考核数据
		JSONObject json = new JSONObject();
		MorphDynaBean orgAssessSet = (MorphDynaBean) configDataBean.get("orgAssessSet");
		JSONObject orgAssessSetJson = getAssessJson(orgAssessSet);
		
		MorphDynaBean empAssessSet = (MorphDynaBean) configDataBean.get("empAssessSet");
		JSONObject empAssessSetJson = getAssessJson(empAssessSet);
		if (recordVo != null) {
			String str = recordVo.getString("str_value");
			Map jsonMap = new HashMap();
			if(StringUtils.isNotEmpty(str)){
				json = JSONObject.fromObject(str);
			}
		}
		json.put("orgAssessSet", orgAssessSetJson);
		json.put("empAssessSet", empAssessSetJson);
		saveJsonToConstant(json,recordVo);
	}

	/**
	 * 将HRCLOUD_CONFIG参数保存到constan中
	 * @param json
	 * @param recordVo
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private void saveJsonToConstant(JSONObject json, RecordVo recordVo) throws GeneralException, SQLException {
		RecordVo para_vo = new RecordVo("constant");
		para_vo.setString("constant", "HRCLOUD_CONFIG");
		para_vo.setString("describe", "云接口参数");
		para_vo.setString("str_value", json.toString());
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		// 不存在则增加，存在则更新
		if (recordVo == null){
			dao.addValueObject(para_vo);
		}else{
			dao.updateValueObject(para_vo);
		}
		ConstantParamter.putConstantVo(para_vo, "HRCLOUD_CONFIG");
	}


	/**
	 * 操作类型，加载云平台参数
	 * @param recordVo
	 * @return
	 */
	public HashMap getConfigData(RecordVo recordVo){
		HashMap map = new HashMap();
		if (recordVo != null) {
			String str = recordVo.getString("str_value");
			Map jsonMap = new HashMap();
			if(!"".equals(str)){
				JSONObject json = JSONObject.fromObject(str);
				jsonMap = (Map)json;
				if(jsonMap.get("appId") == null){
					map.put("appId", "");
				}else{
					map.put("appId", jsonMap.get("appId"));
				}
				if(jsonMap.get("tenantId") == null){
					map.put("tenantId", "");
				}else{
					map.put("tenantId", jsonMap.get("tenantId"));
				}
				if(jsonMap.get("appSecret") == null){
					map.put("appSecret", "");
				}else{
					map.put("appSecret", jsonMap.get("appSecret"));
				}
			}
		}
		if(map.isEmpty()){
			map.put("appId", "");
			map.put("tenantId", "");
			map.put("appSecret", "");
		}
		return map;
	}
		
	/**
	 * xus 19/8/28 判断如果t_sys_outsync表中没有"hrcloud"记录，则添加上此条记录
	 */
	private void doJudgeAndAdd() {
		try {
			boolean isExist = false;
			String sql = "select * from t_sys_outsync where state = 1 and sys_id = 'hrcloud'";
			ContentDAO dao = new ContentDAO(frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				isExist = true;
			}
			if(!isExist){
				ArrayList vo_list = new ArrayList();
				//如果不存在则新增记录
				RecordVo param_vo = new RecordVo("t_sys_outsync");
				param_vo.setString("sys_id", "hrcloud");
				param_vo.setString("sys_name", "云");
				param_vo.setString("url", "http://?wsdl");
				param_vo.setString("sync_method", "sendSyncMsg");
				param_vo.setInt("state", 1);
				param_vo.setString("targetnamespace", "http://WebXml.com.cn/");
				param_vo.setInt("send", 1);
				vo_list.add(param_vo);
				dao.addValueObject(vo_list);
			}
			//xus 19/9/4  如果同步参数设置中勾选了同步照片，则加上hrcloudp字段
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			String photo = hsb.getAttributeValue(HrSyncBo.photo);
			photo=photo!=null&&photo.trim().length()>0?photo:"0";
			DbWizard dbw = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
			Table hr_view_table = new Table("t_hr_view");
			if(!"0".equals(photo)){
				if(!dbw.isExistField("t_hr_view", "hrcloudp",false)) {
					Field item = new Field("hrcloudp", "hrcloudp");
					item.setDatatype(DataType.INT);
					item.setLength(2);
					hr_view_table.addField(item);
					dbw.addColumns(hr_view_table);
					dbmodel.reloadTableModel("t_hr_view");
				}
			}else{
				if(dbw.isExistField("t_hr_view", "hrcloudp" + "p",false)) {
					Field item = new Field("hrcloudp", "hrcloudp");
					hr_view_table.addField(item);
					dbw.dropColumns(hr_view_table);
					dbmodel.reloadTableModel("t_hr_view");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject setDescValue(JSONObject orgAssessSet) {
		String setid = orgAssessSet.getString("setid");
		HashMap reMap = new HashMap();
		for(Object o:orgAssessSet.keySet()){
			reMap.put(orgAssessSet.get(o), o);
		}
		if(!"".equals(setid)){
			ArrayList fieldList = this.userView.getPrivFieldList(setid);
			for(int i = 0;i<fieldList.size();i++){
				FieldItem item = (FieldItem) fieldList.get(i);
				if(orgAssessSet.containsValue(item.getItemid())){
					orgAssessSet.put(reMap.get(item.getItemid()), item.getItemid()+"`"+item.getItemdesc());
				}
			}
		}
		return orgAssessSet;
	}
	
	/**
	 * 获取考核结果子集	
	 * @return
	 */
	private ArrayList getAssessSetList(int type) {
		ArrayList privList = this.getUserView().getPrivFieldSetList(type);
		ArrayList returnList = new ArrayList();
		for(Object obj:privList){
			FieldSet set = (FieldSet)obj;
			if("1".equals(set.getUseflag())){
				JSONObject json = new JSONObject();
				json.put("fieldsetid", set.getFieldsetid());
				json.put("fieldsetdesc", set.getFieldsetdesc());
				returnList.add(json);
			}
		}
		return returnList;
	}
	
	/**
	 * 判断修改人员库时是否需要提示	
	 * @return
	 */
	private boolean isRemind() {
		boolean flag = false;
		String sql = "select sys_id,control from t_sys_outsync where state != 0";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String str = this.frowset.getString("sys_id");
				if(!"hrcloud".equals(str)&&this.frowset.getString("control") != null&&!"null".equals(this.frowset.getString("control"))&&!"".equals(this.frowset.getString("control"))){
					if(this.frowset.getString("control").toUpperCase().contains("A")){
						flag = true;
						break;
					}
				}
	//			if(!"hrcloud".equals(str)&&this.frowset.getObject("control") != null&&!"null".equals(this.frowset.getString("control"))&&(this.frowset.getString("control").toUpperCase().contains("A")||"".equals(this.frowset.getString("control")))){
	//				flag = true;
	//				break;
	//			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/**
	 * 获取数据视图人员库配置
	 * @return
	 */
	private ArrayList getDbList() {
		ArrayList dbprelist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from dbname ");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    HrSyncBo hsb = new HrSyncBo(this.frameconn);
	    String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
	    String[] dbnames = dbnamestr.split(",");
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	    	  LazyDynaBean bean = new LazyDynaBean();
	    	  bean.set("id",this.getFrowset().getInt("dbid")+"");
	    	  bean.set("boxLabel",this.getFrowset().getString("dbname")+"&nbsp;&nbsp;");
	    	  bean.set("flag",this.getFrowset().getString("flag"));
	    	  bean.set("pre",this.getFrowset().getString("pre"));
	    	  for(int i=0;i<dbnames.length;i++){
	    		  if(this.getFrowset().getString("pre").equalsIgnoreCase(dbnames[i])){
	    			  bean.set("checked",true);
	    			  break;
	    		  }
	    		  else
	    			  bean.set("checked",false);
	    	  }
	    	  dbprelist.add(bean);
	      }
	    } catch(SQLException sqle)
	    {
		      sqle.printStackTrace();
	    }
	    finally
		{
		}
		
		return dbprelist;
	}
	
	private ArrayList<String> getTableFieldids(RecordVo recordVo) {
		ArrayList<String> returnList = new ArrayList<String>();
		JSONArray consTabelsJsonArray = null;
		if (recordVo != null) {
			String str = recordVo.getString("str_value");
			if(str!=null&&!"".equals(str)){
				Map jsonMap = new HashMap();
				JSONObject json = JSONObject.fromObject(str);
				if(json.get("tables") != null){
					consTabelsJsonArray =  json.getJSONArray("tables");
				}
			}
		}
		if(consTabelsJsonArray != null &&consTabelsJsonArray.size() != 0){
			//将constant表中的记录转换成{id:{itemid:XX,itemdesc:XX}//connectField。。。}的格式
			for(int i = 0;i<consTabelsJsonArray.size();i++){
				JSONObject consTabelJson = consTabelsJsonArray.getJSONObject(i);
					JSONArray fields = consTabelJson.getJSONArray("fields");
					for(int j = 0;j<fields.size();j++){
						JSONObject field = fields.getJSONObject(j);
						//在constant表中数据的格式为{connectField:{itemid:XX,itemdesc:XX}}
						JSONObject connectField = field.getJSONObject("connectField");
						returnList.add(connectField.getString("itemid").toLowerCase());
					}
			}
		}
		return returnList;
	}
	
	
	 
	private ArrayList getViewFields(ArrayList<String> oldTableFieldids, ArrayList tabelsList) {
	//	ArrayList<String> hrViewFielditemids = new ArrayList<String>();
		ArrayList<String> existedfieldidlist=new ArrayList<String>();
		ArrayList<String> tosavefieldidlist = new ArrayList<String>();
			try {
				MorphDynaBean table = (MorphDynaBean) tabelsList.get(0);
				ArrayList fields = (ArrayList)table.get("fields");
				//获取数据视图中的指标
				HrSyncBo hsb = new HrSyncBo(this.frameconn);
				
				ArrayList fieldlist = hsb.getAppFieldList(HrSyncBo.A);
				existedfieldidlist = getFieldidlist(fieldlist);
				ContentDAO dao = new ContentDAO(frameconn);
				
				for(int i = 0;i<fields.size();i++){
					MorphDynaBean field = (MorphDynaBean)fields.get(i);
					MorphDynaBean connectField = (MorphDynaBean)field.get("connectField");
					String itemid = (String)connectField.get("itemid");
					tosavefieldidlist.add(itemid.toLowerCase());
				}
				
				ArrayList<String> delfs = getDelViewFields(oldTableFieldids,tosavefieldidlist);
				//1. 创建数据视图同时类型 为 触发器
				hsb.setAttributeValue(HrSyncBo.SYNC_MODE,"trigger");
				boolean fieldAndCode = false;
				 String fieldAndCodeSeq = ":";
				if("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
				   fieldAndCode = true;
				   fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
				}
				CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				    this.userView, fieldAndCode, fieldAndCodeSeq);
				if(!hsb.isSync_a01()){
					hsb.setAttributeValue(HrSyncBo.SYNC_A01, "1");
					hsb.dropTable("t_hr_view");
					hsb.importHrDataTriggerMode();
					hsb.creatFieldChangeTable("t_hr_view_log");
				}
				if(!hsb.isSync_b01()){
					hsb.setAttributeValue(HrSyncBo.SYNC_B01, "1");
					csf.delFrigger(CreateSyncFrigger.ORG_FLAG);
					hsb.dropTable("t_org_view");
					hsb.importOrgDataTriggerMode();
					hsb.creatFieldChangeTable("t_org_view_log");
					csf.createFrigger(CreateSyncFrigger.ORG_FLAG);
				}
				if(!hsb.isSync_k01()){
					hsb.setAttributeValue(HrSyncBo.SYNC_K01, "1");
					csf.delFrigger(CreateSyncFrigger.POST_FLAG);
					hsb.dropTable("t_post_view");
					hsb.importPostDataTriggerMode();
					csf.createFrigger(CreateSyncFrigger.POST_FLAG);
				}
				
				//2. 人员视图配置指标更新
				String fieldsStr =hsb.getTextValue(HrSyncBo.FIELDS);
				for(String addfieldid : tosavefieldidlist){
					if(!existedfieldidlist.contains(addfieldid)){
						fieldsStr += ","+addfieldid;
						hsb.setAppAttributeValue(HrSyncBo.A,addfieldid,addfieldid);
					}
				}
				if(fieldsStr.startsWith(","))
					fieldsStr = fieldsStr.substring(1);
				hsb.setTextValue(HrSyncBo.FIELDS,fieldsStr);
				
				//3. 视图视图配置参数保存
				hsb.saveParameter(dao);
				
				//4. 更新数据视图  并创建触发器
				hsb.creatHrTable("t_hr_view");
				csf.delFrigger(CreateSyncFrigger.HR_FLAG);
			    csf.createFrigger(CreateSyncFrigger.HR_FLAG);
	
				//			if(delfs!=null&&delfs.size()>0){
	//				//删除视图中的指标
	//				for(String delfieldid : delfs){
	//					if(existedfieldidlist.contains(delfieldid)){
	//						String codefieldstr = hsb.getTextValue(HrSyncBo.CODE_FIELDS).toLowerCase();
	//						String onlyfield = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
	//						
	//						if(onlyfield!=null)
	//							onlyfield=onlyfield.toLowerCase();
	//						if(codefieldstr.indexOf(delfieldid.toLowerCase()) != -1){
	//							throw new GeneralException("该字段已经被设置为翻译型指标，现在无法删除！");
	//						}
	//						if(delfieldid.equalsIgnoreCase(onlyfield)){
	//							throw new GeneralException("该字段已经被设置为唯一性字段指标，现在无法删除！");
	//						}
	//						hsb.delAppAttributeValue(HrSyncBo.A,delfieldid);
	//						
	//						
	////						hsb.deleteColumn("t_hr_view", delfieldid);
	//						String fieldsStr = hsb.getTextValue(HrSyncBo.FIELDS);
	//						String[] fieldsStrs = fieldsStr.split(",");
	//						ArrayList<String> newfieldidlist = new ArrayList<String>();
	//						for(String str:fieldsStrs){
	//							if(!str.trim().toLowerCase().equals(delfieldid)){
	//								newfieldidlist.add(str.trim());
	//							}
	//						}
	//						String newfieldsStr = newfieldidlist.toString();
	//						newfieldsStr = newfieldsStr.substring(1,newfieldsStr.length()-1);
	//						hsb.setTextValue(HrSyncBo.FIELDS,fieldsStr);
	//						hsb.saveParameter(dao);
	//					}
	//				}
	//			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tosavefieldidlist;
	}
	
	
	
	private ArrayList<String> getDelViewFields(ArrayList<String> oldTableFieldids, ArrayList<String> tosavefieldidlist) {
		ArrayList<String> delFields = new ArrayList<String>();
		if(oldTableFieldids == null){
			return null;
		}
		for(String str:oldTableFieldids){
			if(!tosavefieldidlist.contains(str)){
				delFields.add(str);
			}
		}
		return delFields;
	}
	
	
	
	private ArrayList<String> getFieldidlist(ArrayList fieldlist) {
		ArrayList<String> fieldidlist = new ArrayList<String>();
		for(Object o:fieldlist){
			LazyDynaBean bean = (LazyDynaBean)o;
			fieldidlist.add(bean.get("name").toString().toLowerCase());
		}
		return fieldidlist;
	}
	
	
	
	private JSONObject getStrValue(MorphDynaBean configDataBean) throws GeneralException {
			JSONObject json = new JSONObject();
			StringBuilder strValue = new StringBuilder();
			json.put("appId", configDataBean.get("appId") != null?(String)configDataBean.get("appId"):"");
			json.put("tenantId", configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"");
			json.put("appSecret", configDataBean.get("appSecret") != null?(String)configDataBean.get("appSecret"):"");
			json.put("used", configDataBean.get("used") != null?(String)configDataBean.get("used"):"");
			json.put("tenantId", configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"");
			MorphDynaBean frequency = (MorphDynaBean) configDataBean.get("frequency");
			JSONObject frequencyJson = new JSONObject();
			frequencyJson.put("type", (String)frequency.get("type"));
			if(frequency.get("week")!=null&& !"".equals(frequency.get("week"))){
				frequencyJson.put("week", (String)frequency.get("week"));
			}
			frequencyJson.put("time", (String)frequency.get("time"));
			json.put("frequency", frequencyJson);
			//考核数据
			MorphDynaBean orgAssessSet = (MorphDynaBean) configDataBean.get("orgAssessSet");
			JSONObject orgAssessSetJson = getAssessJson(orgAssessSet);
			json.put("orgAssessSet", orgAssessSetJson);
			
			MorphDynaBean empAssessSet = (MorphDynaBean) configDataBean.get("empAssessSet");
			JSONObject empAssessSetJson = getAssessJson(empAssessSet);
			json.put("empAssessSet", empAssessSetJson);
			//tables 判断null
			if(configDataBean.get("tables") != null){
				ArrayList tabelsList = (ArrayList)configDataBean.get("tables");
				json.put("tables", tabelsList);
	//			addViewFields(tabelsList);
			}else{
				json.put("tables", null);
			}
			
			
			return json;
		}
	
	/**
	 * 将考核指标转换为JSON格式
	 * @param assessSet
	 * @return
	 */
	private JSONObject getAssessJson (MorphDynaBean AssessSet){
		JSONObject AssessSetJson = new JSONObject();
		AssessSetJson.put("setid", (String)AssessSet.get("setid"));
		AssessSetJson.put("businessId", (String)AssessSet.get("businessId"));
		AssessSetJson.put("name", (String)AssessSet.get("name"));
		AssessSetJson.put("type", (String)AssessSet.get("type"));
		AssessSetJson.put("planDate", (String)AssessSet.get("planDate"));
		AssessSetJson.put("planYear", (String)AssessSet.get("planYear"));
		AssessSetJson.put("sheetName", (String)AssessSet.get("sheetName"));
		AssessSetJson.put("score", (String)AssessSet.get("score"));
		AssessSetJson.put("degreeName", (String)AssessSet.get("degreeName"));
		return AssessSetJson;
	}
	/**
	 * 数据视图中 添加指标
	 * @param tabelsList
	 * @return 
	 */
	
	 private String[] addViewFields(ArrayList tabelsList){
		 String[] arr=null;
			try {
				MorphDynaBean table = (MorphDynaBean) tabelsList.get(0);
				ArrayList fields = (ArrayList)table.get("fields");
				//获取数据视图中的指标
				ArrayList hrViewFielditemids = new ArrayList();
				HrSyncBo hsb = new HrSyncBo(this.frameconn);
				ArrayList fieldlist=new ArrayList();
				fieldlist = hsb.getAppFieldList(HrSyncBo.A);
				if(fieldlist!=null&&fieldlist.size()>0)
				{
					for(int i=0;i<fieldlist.size();i++)
					{
						LazyDynaBean bean_1=(LazyDynaBean)fieldlist.get(i);
						String key=(String)bean_1.get("name");
						hrViewFielditemids.add(key);
					}
				}
				arr = new String[fields.size()];
				for(int i = 0;i<fields.size();i++){
					MorphDynaBean field = (MorphDynaBean)fields.get(i);
					MorphDynaBean connectField = (MorphDynaBean)field.get("connectField");
					String itemid = (String)connectField.get("itemid");
					if(hrViewFielditemids.contains(itemid)){
						continue;
					}
					arr[i] = itemid;
				}
	//			addFields(arr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return arr;
	 }
	 
	 public void addFields(ArrayList<String> arr)throws GeneralException
	 {
	 	for(int i=0;i<arr.size();i++)
	 	{
	 		if(!"".equals(arr.get(i)))
	 		{
	 			if(!("a0100".equalsIgnoreCase(arr.get(i)) || "b0110".equalsIgnoreCase(arr.get(i))
	 					|| "e0122".equalsIgnoreCase(arr.get(i)) || "a0101".equalsIgnoreCase(arr.get(i))
	 					|| "e01A1".equalsIgnoreCase(arr.get(i)) || "username".equalsIgnoreCase(arr.get(i))
	 					|| "userpassword".equalsIgnoreCase(arr.get(i)) || "flag".equalsIgnoreCase(arr.get(i))))
	 			{
					this.addField(arr.get(i));
	 			}
	 		}
	 	}
	 }
	 public void addField(String fieldstr)throws GeneralException
	 {	
			DbWizard dbw=new DbWizard(this.getFrameconn());	
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			if(!dbw.isExistField("t_hr_view", fieldstr))
			{
				Table table=new Table("t_hr_view");
				FieldItem fi = DataDictionary.getFieldItem(fieldstr);
				if(fi!=null)
				{
					String codesetid = fi.getCodesetid();
					Field field = fi.cloneField();
					if(!("0".equals(codesetid)))
					{
						field.setLength(50);
					}
					table.addField(field);
					dbw.addColumns(table);
					hsb.setTextValue(HrSyncBo.FIELDS, fieldstr);
					hsb.setTextValue(HrSyncBo.A, fieldstr);
				}			
			}			
	 }
	/**
	 * 获取云平台指标
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @return 
	 */
	private JSONObject getCloudFieldItem(String appId, String tenantId, String appSecret,SyncConfigService scs) {
		// String url = SyncConfigUtil.IP_PORT+"/open/staff/fields";
		// 新程序改为此链接
		String url = SyncConfigUtil.IP_PORT+"/open/fields";
		String param = getCloudJsonParam(appId, tenantId, appSecret);
		String str = SyncConfigUtil.postHttp(url, param);
		if(str.indexOf("success")<0){
			return null;
		}
		JSONObject resJson = JSONObject.fromObject(str);
		String resSign = resJson.getString("sign");
		String resData = resJson.getString("data");
		resData = SyncConfigUtil.AESDecrypt(resData,appSecret);
		JSONObject resDataJson = JSONObject.fromObject(resData);
		if(!resDataJson.containsKey("subsets")){
			return null;
		}
		return resDataJson;
		/*JSONArray subsetsArray = resDataJson.getJSONArray("subsets");
		// 记录hr系统 constant表中已存数据
		ArrayList subsetsList = new ArrayList();
		JSONObject tabelsReturnJSONObject = getTablesJson(subsetsArray,consTabelsJsonArray);
		return tabelsReturnJSONObject;*/
	}
	
	
	
	/**
	 * 拼接云访问参数
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @return
	 */
	private String getCloudJsonParam(String appId, String tenantId, String appSecret){
		JSONObject json = new JSONObject();
		json.put("appId", appId);
		JSONObject data = new JSONObject();
		data.put("tenantId", tenantId);
		String dataEncode = SyncConfigUtil.AESEncrypt(data.toString(), appSecret);
		json.put("data", dataEncode);
		ArrayList datalist = new ArrayList();
		datalist.add(appId);
		datalist.add(dataEncode);
		String signEncode = SyncConfigUtil.digest(datalist);
		json.put("sign", signEncode);
		return json.toString();
	}
	
}
