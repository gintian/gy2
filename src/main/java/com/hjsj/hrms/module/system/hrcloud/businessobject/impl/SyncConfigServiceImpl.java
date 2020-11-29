package com.hjsj.hrms.module.system.hrcloud.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo;
import com.hjsj.hrms.module.system.hrcloud.businessobject.SyncConfigService;
import com.hjsj.hrms.module.system.hrcloud.util.HRCloudFieldUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncCloudDataLoggerUtil;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hjsj.hrms.utils.PubFunc;
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
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncConfigServiceImpl implements SyncConfigService{
	/**
	 * constant中HRCLOUD_CONFIG的recordVo
	 */
	private RecordVo recordVo;
	/**
	 * userview
	 */
	private UserView userview;
	/**
	 * 数据库连接
	 */
	private Connection conn;
	/**
	 * constant中HRCLOUD_CONFIG存放的Json
	 */
	private JSONObject constantParamJson;
	/**
	 * 从云获取的指标
	 * {subsets:[{},{}...]}
	 */
	private JSONObject cloudFieldDataJson;
	
	@Override
    public JSONObject getCloudFieldDataJson() {
		return cloudFieldDataJson;
	}
	@Override
    public void setCloudFieldDataJson(JSONObject cloudFieldDataJson) {
		this.cloudFieldDataJson = cloudFieldDataJson;
	}
	/**
	 * 构造函数
	 * @param recordVo
	 * @param userview
	 * @param conn
	 */
	public SyncConfigServiceImpl(RecordVo recordVo, UserView userview, Connection conn) {
		super();
		this.recordVo = recordVo;
		this.userview = userview;
		this.conn = conn;
		InitConstantParamJson();
	}
	/**
	 * 构造函数
	 * @param userview
	 * @param conn
	 */
	public SyncConfigServiceImpl(UserView userview, Connection conn) {
		super();
		this.userview = userview;
		this.conn = conn;
		this.recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
		InitConstantParamJson();
	}
	/**
	 * 初始化constant中HRCLOUD_CONFIG存放的Json
	 */
	private void InitConstantParamJson(){
		JSONObject jsonobj = new JSONObject();
		if (recordVo != null) {
			String str = recordVo.getString("str_value");
			if(!"".equals(str)){
				JSONObject json = JSONObject.fromObject(str);
				if(json.get("appId") == null){
					json.put("appId", "");
				}
				if(json.get("tenantId") == null){
					json.put("tenantId", "");
				}
				if(json.get("appSecret") == null){
					json.put("appSecret", "");
				}
				if(json.get("tables") == null){
					json.put("tables", null);
				}
				if(json.get("cloudTOhr") == null){
					json.put("cloudTOhr", null);
				}
				if(json.get("frequency") == null){
					json.put("frequency", null);
				}
				if(json.get("used") == null){
					json.put("used", "0");
				}
				jsonobj = json;
			}
		}
		if(jsonobj.isEmpty()){
			jsonobj.put("appId", "");
			jsonobj.put("tenantId", "");
			jsonobj.put("appSecret", "");
			jsonobj.put("tables", null);
			jsonobj.put("cloudTOhr", null);
			jsonobj.put("frequency", null);
			jsonobj.put("used", "0");
		}
		this.constantParamJson = jsonobj;
	}
	
	/**
	 * 获取constant中HRCLOUD_CONFIG存放的Json
	 * @return
	 */
	@Override
    public JSONObject getConstantParamJson() {
		return constantParamJson;
	}
	
	@Override
    public void setConstantParamJson(JSONObject constantParamJson) {
		this.constantParamJson = constantParamJson;
	}
	
	/**
	 * 获取云配置参数
	 * appId、tenantId、appSecret
	 * @return
	 */
	@Override
    public JSONObject getCloudAPIConfigData(){
		JSONObject cloudConfigData = new JSONObject();
		cloudConfigData.put("appId", this.constantParamJson.get("appId"));
		cloudConfigData.put("tenantId", this.constantParamJson.get("tenantId"));
		cloudConfigData.put("appSecret", this.constantParamJson.get("appSecret"));
		return cloudConfigData;
	}
	
	/**
	 * 保存云配置参数
	 * appId、tenantId、appSecret
	 * @param configDataBean
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	@Override
    public void saveCloudAPIConfigData(MorphDynaBean configDataBean) throws Exception{
		String appId = configDataBean.get("appId") != null?(String)configDataBean.get("appId"):"";
		String tenantId = configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"";
		String appSecret = configDataBean.get("appSecret") != null?(String)configDataBean.get("appSecret"):"";
		constantParamJson.put("appId", appId);
		constantParamJson.put("tenantId", tenantId);
		constantParamJson.put("appSecret", appSecret);
		saveJsonToConstant(constantParamJson);
	}
	
	/**
	 * 将HRCLOUD_CONFIG参数保存到constan中
	 * @param json
	 * @param recordVo
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private void saveJsonToConstant(JSONObject json) throws Exception {
		try{
			RecordVo para_vo = new RecordVo("constant");
			para_vo.setString("constant", "HRCLOUD_CONFIG");
			para_vo.setString("describe", "云接口参数");
			para_vo.setString("str_value", json.toString());
			
			ContentDAO dao = new ContentDAO(this.conn);
			// 不存在则增加，存在则更新
			if (recordVo == null){
				dao.addValueObject(para_vo);
			}else{
				dao.updateValueObject(para_vo);
			}
			ConstantParamter.putConstantVo(para_vo, "HRCLOUD_CONFIG");
		}catch(Exception e){
			throw new Exception("云接口参数保存到constant表中失败");
		}
	}
	
	/**
	 * 保存云配置参数
	 * appId、tenantId、appSecret
	 * @param configDataBean
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	@Override
    public void saveCloudConfigData(MorphDynaBean configDataBean) throws Exception{
		String appId = configDataBean.get("appId") != null?(String)configDataBean.get("appId"):"";
		String tenantId = configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"";
		String appSecret = configDataBean.get("appSecret") != null?(String)configDataBean.get("appSecret"):"";
		constantParamJson.put("appId", appId);
		constantParamJson.put("tenantId", tenantId);
		constantParamJson.put("appSecret", appSecret);
		saveJsonToConstant(constantParamJson);
	}
	
	/**
	 * 刷新后台作业
	 * @throws Exception
	 */
	@Override
    public void refreshWarnScanJob() throws Exception{
		try{
			//保存执行后台作业
			String warn_scan=SystemConfig.getPropertyValue("warn_scan");
			/*当设置不执行后台作业，但有某个需要例外的后台作业id，多个逗号隔开*/
			String warn_scan_forcejob = SystemConfig.getPropertyValue("warn_scan_forcejob");
			/*设置不执行整体后台作业，但是有某个又必须要执行时 */
			if(!"false".equalsIgnoreCase(warn_scan)){
				ScheduleJobBo scheduleJobBo = new ScheduleJobBo(this.conn);
				scheduleJobBo.resetJob("HrCloud");
			}
		}catch (Exception e) {
			throw new Exception("刷新后台作业失败");
		}
		
	}
	/**
	 * 保存全部云配置参数
	 * appId、tenantId、appSecret
	 * @param configDataBean
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	@Override
    public void saveAllCloudConfigData(MorphDynaBean configDataBean, JSONObject strValue) throws Exception{
		try{
			//放到ConstantParamter字典里
			saveJsonToConstant(strValue);
			ContentDAO dao = new ContentDAO(this.conn);
			//保存人员库设置
			HrSyncBo hsb = new HrSyncBo(this.conn);
			String dbnamestr = (String) configDataBean.get("dbnames");
			String olddbnamestr = hsb.getTextValue(HrSyncBo.BASE);
			if(!dbnamestr.equalsIgnoreCase(olddbnamestr)){
				hsb.setTextValue(HrSyncBo.BASE,dbnamestr);
				hsb.saveParameter(dao);
			}
			
			
			//数据视图添加指标
			if(strValue.get("tables") != null){
				ArrayList<String> oldTableFieldids = getTableFieldids();
				JSONArray tabelsList = strValue.getJSONArray("tables");
				ArrayList addarr = getViewFields(oldTableFieldids,tabelsList);
			}
			//xus 19/8/28 判断如果t_sys_outsync表中没有"hrcloud"记录，则添加上此条记录
			doJudgeAndAdd();
			//xus 19/11/13 如果保存的对应指标的子集中没有GUIDKEY字段，则添加
			if(strValue.get("cloudTOhr") != null){
				JSONObject cloudTOhr = (JSONObject)strValue.get("cloudTOhr");
				if(cloudTOhr.containsKey("setMapping")){
					JSONArray setMapping = cloudTOhr.getJSONArray("setMapping");
					judgeSubSetGUIDKEY(setMapping);
				}
			}
			
		}catch (Exception e) {
			throw new Exception("云接口参数保存到constant表中失败");
		}
		
	}
	
	/**
	 * 如果保存的对应指标的子集中没有GUIDKEY字段，则添加
	 * @param setMapping
	 * @throws GeneralException 
	 */
	private void judgeSubSetGUIDKEY(JSONArray setMapping) throws GeneralException {
		//xus 18/9/18 如果表中没有guidkey字段 则加上此字段
		DbWizard db = new DbWizard(this.conn);
		for(Object obj : setMapping){
			JSONObject set = (JSONObject)obj;
			String hr_set = set.getString("hr_set");
			if(StringUtils.isBlank(hr_set)){
				continue;
			}
			if(hr_set.toUpperCase().startsWith("A")){
				hr_set = "Usr"+hr_set;
			}
			//表中没有GUIDKEY字段  增加GUIDKEY标识字段
			if(!db.isExistField(hr_set, "GUIDKEY",false)){
				Table t = new Table(hr_set);
				Field f = new Field("GUIDKEY",DataType.STRING);
				f.setNullable(true);
				f.setLength(38);
				t.addField(f);
				db.addColumns(t);
			}
		}
		
	}
	/**
	 * 将前台传入的bean类型参数转为json格式
	 * @param configDataBean
	 * @throws Exception
	 */
	@Override
    public JSONObject getStrValue(MorphDynaBean configDataBean) throws Exception {
		JSONObject json = new JSONObject();
		json.put("appId", configDataBean.get("appId") != null?(String)configDataBean.get("appId"):"");
		json.put("tenantId", configDataBean.get("tenantId") != null?(String)configDataBean.get("tenantId"):"");
		json.put("appSecret", configDataBean.get("appSecret") != null?(String)configDataBean.get("appSecret"):"");
		json.put("used", configDataBean.get("used") != null?(String)configDataBean.get("used"):"");
		MorphDynaBean frequency = (MorphDynaBean) configDataBean.get("frequency");
		JSONObject frequencyJson = new JSONObject();
		frequencyJson.put("type", (String)frequency.get("type"));
		if(frequency.get("week")!=null&& !"".equals(frequency.get("week"))){
			frequencyJson.put("week", (String)frequency.get("week"));
		}
		frequencyJson.put("time", (String)frequency.get("time"));
		json.put("frequency", frequencyJson);
		
		//云同步到ehr指标参数
		if(configDataBean.toString().indexOf("cloudTOhr")>-1 && configDataBean.get("cloudTOhr") != null){
			json.put("cloudTOhr", configDataBean.get("cloudTOhr"));
		}else{
			json.put("cloudTOhr", new JSONObject());
		}
//		JSONObject CloudFieldDataJson = SyncConfigUtil.getCloudFieldDataJson();
//		if(!CloudFieldDataJson.isEmpty()){
//			if(CloudFieldDataJson)
//		}
		//19/12/30 xus【56992】V77云集成：未配置HCM同步到云指标，保存时失败，后台报错，详见附件！
		//tables 判断null
		if(configDataBean.toString().indexOf("tables")>-1 && configDataBean.get("tables") != null){
			ArrayList tabelsList = (ArrayList)configDataBean.get("tables");
			json.put("tables", tabelsList);
//			addViewFields(tabelsList);
		}else{
			json.put("tables", null);
		}
		
		
		return json;
	}
	
	/**
	 * 清空接口数据
	 * @return
	 */
	@Override
    public boolean cleanInterfaceData(){
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(conn);
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
	
	private ArrayList<String> getTableFieldids() {
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
	
 
	private ArrayList getViewFields(ArrayList<String> oldTableFieldids, JSONArray tabelsList) {
	//	ArrayList<String> hrViewFielditemids = new ArrayList<String>();
		ArrayList<String> existedfieldidlist=new ArrayList<String>();
		ArrayList<String> tosavefieldidlist = new ArrayList<String>();
			try {
				JSONObject table = (JSONObject) tabelsList.get(0);
				JSONArray fields = table.getJSONArray("fields");
				//获取数据视图中的指标
				HrSyncBo hsb = new HrSyncBo(conn);
				
				ArrayList fieldlist = hsb.getAppFieldList(HrSyncBo.A);
				existedfieldidlist = getFieldidlist(fieldlist);
				ContentDAO dao = new ContentDAO(conn);
				
				for(int i = 0;i<fields.size();i++){
					JSONObject field = fields.getJSONObject(i);
					JSONObject connectField = field.getJSONObject("connectField");
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
				CreateSyncFrigger csf = new CreateSyncFrigger(conn,
				    userview, fieldAndCode, fieldAndCodeSeq);
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
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tosavefieldidlist;
	}
	
	private ArrayList<String> getFieldidlist(ArrayList fieldlist) {
		ArrayList<String> fieldidlist = new ArrayList<String>();
		for(Object o:fieldlist){
			LazyDynaBean bean = (LazyDynaBean)o;
			fieldidlist.add(bean.get("name").toString().toLowerCase());
		}
		return fieldidlist;
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
	
	/**
	 * xus 19/8/28 判断如果t_sys_outsync表中没有"hrcloud"记录，则添加上此条记录
	 */
	private void doJudgeAndAdd() {
		RowSet rs = null;
		try {
			boolean isExist = false;
			String sql = "select * from t_sys_outsync where state = 1 and sys_id = 'hrcloud'";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()){
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
				
				ArrayList idlist = new ArrayList();
				idlist.add("hrcloud");
				new HrSyncBo(this.conn).addSysOutsyncFlag("t_org_view",
						idlist);
				new HrSyncBo(this.conn).addSysOutsyncFlag("t_hr_view",
						idlist);
				new HrSyncBo(this.conn).addSysOutsyncFlag("t_post_view",
						idlist);
			}
			//xus 19/9/4  如果同步参数设置中勾选了同步照片，则加上hrcloudp字段
			HrSyncBo hsb = new HrSyncBo(this.conn);
			String photo = hsb.getAttributeValue(HrSyncBo.photo);
			photo=photo!=null&&photo.trim().length()>0?photo:"0";
			DbWizard dbw = new DbWizard(this.conn);
			DBMetaModel dbmodel = new DBMetaModel(this.conn);
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
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * 获取hr同步到云 的hrTOcloud json
	 * @param resDataJson
	 * @return
	 */
	@Override
    public JSONObject getCloudDataJson(){
		JSONArray consTabelsJsonArray = null;
        if(getConstantParamJson().containsKey("tables")){
            consTabelsJsonArray = getConstantParamJson().getJSONArray("tables");
        }
		JSONArray subsetsArray = cloudFieldDataJson.getJSONArray("subsets");
		// 记录hr系统 constant表中已存数据
		JSONObject tabelsReturnJSONObject = getTablesJson(subsetsArray,consTabelsJsonArray);
		return tabelsReturnJSONObject;
	}
	
	/**
	 * 获取的指标 与 constant表中数据进行对比
	 * @param subsetsArray subset的集合
	 * @param consTabelsJsonArray
	 * @return tabelsReturnJSONObject
	 */
	private JSONObject getTablesJson(JSONArray subsetsArray, JSONArray consTabelsJsonArray) {
		JSONObject tabelsReturnJSONObject = new JSONObject();
		// 获取的指标 与 constant表中数据进行对比
		if(subsetsArray.size()==0){
			return null;
		}
		for(Object o : subsetsArray){
			JSONObject subset = (JSONObject)o;
//			人员主集id固定为bd_staff
//			if("bd_staff".equalsIgnoreCase(subset.getString("id")) || subset.getInt("type")==1 && subset.getInt("isChildSet")==0){
			if("bd_staff".equalsIgnoreCase(subset.getString("id"))){
				tabelsReturnJSONObject = subset;
				break;
			}
		}
		if(tabelsReturnJSONObject.isEmpty()){
			tabelsReturnJSONObject = subsetsArray.getJSONObject(0);
		}
		tabelsReturnJSONObject = getTableJson(tabelsReturnJSONObject,consTabelsJsonArray);
		return tabelsReturnJSONObject;
	}
	
	/**
	 * 获取hrTOcloud参数的JSONObject
	 * {
			cloud_fields:[{
			  id:’name’,
			  name:’姓名’,
			  type:’A’,
			    length:10,
			  dlength:0,
			    codesetid:’0’,
			    required:true,
			  //关联的数据视图中的指标
			  connectField:{
			       itemid:’A0101’,
			       itemdesc:’姓名’
			    }
			},.........],
			//人员视图备选指标数据
			hrFields:[{
				itemid:’A0101’,
				desc:’姓名’,
				type:’A’,
				length:10,
				dlength:0,
				codesetid:’0’
			},........]
	 * @param tableJson
	 * @param consTabelsJsonArray
	 * @return
	 */
	private JSONObject getTableJson(JSONObject tableJson, JSONArray consTabelsJsonArray) {
		
		JSONObject jsonMap = new JSONObject();
		
		if(consTabelsJsonArray != null &&consTabelsJsonArray.size() != 0){
			//将constant表中的记录转换成{id:{itemid:XX,itemdesc:XX}//connectField。。。}的格式
			for(int i = 0;i<consTabelsJsonArray.size();i++){
				JSONObject consTabelJson = consTabelsJsonArray.getJSONObject(i);
				JSONArray fields = consTabelJson.getJSONArray("fields");
				for(int j = 0;j<fields.size();j++){
					JSONObject field = fields.getJSONObject(j);
					//在constant表中数据的格式为{connectField:{itemid:XX,itemdesc:XX}}
					jsonMap.put(field.getString("id"), field.getJSONObject("connectField"));
				}
			}
		}else{
			JSONObject object = getMatchConfig(new JSONArray());
			for(Object o:object.keySet()){
				String key = (String)o;
				JSONObject jso = object.getJSONObject(key);
				jsonMap.put(jso.getString("id"), jso);
			}
		}
		for(int i = 0;i<tableJson.size();i++){
			JSONArray fields = tableJson.getJSONArray("fields");
			
			JSONArray cloud_fields = new JSONArray();
			for(int j = 0;j<fields.size();j++){
				JSONObject field = fields.getJSONObject(j);
				JSONObject newField = new JSONObject();
				String fieldid = field.getString("id");
				//过滤不需要显示的指标
				if("id".equalsIgnoreCase(fieldid) || "staffNum".equalsIgnoreCase(fieldid) || "thirdId".equalsIgnoreCase(fieldid)
						|| "unitId".equalsIgnoreCase(fieldid) || "unitName".equalsIgnoreCase(fieldid) || "unitCode".equalsIgnoreCase(fieldid)
						|| "deptId".equalsIgnoreCase(fieldid) || "deptName".equalsIgnoreCase(fieldid) || "deptCode".equalsIgnoreCase(fieldid) 
						|| "posId".equalsIgnoreCase(fieldid) || "posName".equalsIgnoreCase(fieldid) || "posCode".equalsIgnoreCase(fieldid) ){
					continue;
				}
				String fieldname = field.getString("name");
				newField.put("id", fieldid);
				newField.put("name", fieldname);
				newField.put("codesetid", field.get("codesetid"));
				newField.put("decimalwidth", field.getInt("decimalwidth"));
				newField.put("fieldlength", field.getInt("fieldlength"));
				newField.put("type", field.getString("type"));
				newField.put("allowempty", field.getInt("allowempty"));
				if(jsonMap.containsKey(fieldid)){
					newField.put("connectField", jsonMap.getJSONObject(fieldid));
				}
				cloud_fields.add(newField);
			}
			tableJson.put("cloud_fields",cloud_fields);
			
		}
		tableJson.put("hrFields", getHrFieldsList());
		return tableJson;
	}
	
	@Override
    public JSONObject getMatchConfig(JSONArray tabelsList) {
		JSONObject obj = new JSONObject();
		HashMap map = HRCloudFieldUtil.getMatchMap(this.conn);
		for(Object o : map.keySet()){
			String key = (String)o;
			if(!tabelsList.contains(key)&&map.containsKey(key)&&map.get(key)!=null){
				String itemid = (String) map.get(key);
				FieldItem item = DataDictionary.getFieldItem(itemid);
				if( item == null ){
					continue;
				}
				JSONObject json = new JSONObject();
				json.put("id", key);
				json.put("itemid", itemid);
				json.put("itemdesc", item.getItemdesc());
				obj.put(key, json);
			}
		}
		return obj;
	}
	
	/**
	 * 获取人员视图备选指标数据
	 * @return
	 */
	private ArrayList getHrFieldsList(){
		//hr系统指标
		ArrayList empFields = null;
		HrSyncBo hsb = new HrSyncBo(this.conn);
		ArrayList fieldlist=new ArrayList();
		fieldlist = hsb.getAppFieldList(HrSyncBo.A);
		if(fieldlist!=null&&fieldlist.size()>0)
		{
			empFields = new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
			{
				LazyDynaBean bean_1=(LazyDynaBean)fieldlist.get(i);
				String key=(String)bean_1.get("name");
				HashMap fieldmap = new HashMap();
				fieldmap.put("itemid",key);
				FieldItem item=DataDictionary.getFieldItem(key);
				if(item!=null){
					fieldmap.put("desc",item.getItemdesc());
					fieldmap.put("type",item.getItemtype());
					fieldmap.put("length",item.getItemlength());
					fieldmap.put("dlength",item.getDecimalwidth());
					fieldmap.put("codesetid",item.getCodesetid());
				}else{
					fieldmap.put("desc","");
					fieldmap.put("type","");
					fieldmap.put("length","");
					fieldmap.put("dlength","");
					fieldmap.put("codesetid","");
				}
				empFields.add(fieldmap);
			}
		}
		return empFields;
	}
	
	/**
	 * 获取云子集
	 * @cloudReturnJson
	 * @return
	 */
	@Override
    public JSONArray getCloudTOhrSet(){
		JSONArray subsetsArray = cloudFieldDataJson.getJSONArray("subsets");
		JSONArray setMapping = null;
		setMapping = (constantParamJson.containsKey("cloudTOhr") &&
						constantParamJson.getJSONObject("cloudTOhr")!=null &&
						constantParamJson.getJSONObject("cloudTOhr").containsKey("setMapping") &&
						constantParamJson.getJSONObject("cloudTOhr").getJSONArray("setMapping") !=null)?
								constantParamJson.getJSONObject("cloudTOhr").getJSONArray("setMapping"):
									null;
		ArrayList<String> ids = new ArrayList<String>();
		if(setMapping != null ){
			for(Object o : setMapping){
				JSONObject json = (JSONObject)o;
				ids.add(json.getString("cloudset_id"));
			}
		}
		
		JSONArray cloudset = new JSONArray();
		//机构考核结果选项
		JSONObject orgCloudSet = new JSONObject();
		orgCloudSet.put("id", "org_result");
		orgCloudSet.put("name", "机构考核结果");
		orgCloudSet.put("type", "org_result");
		orgCloudSet.put("selected", "true");
		cloudset.add(orgCloudSet);
		//人员考核结果选项
		JSONObject empCloudSet = new JSONObject();
		empCloudSet.put("id", "emp_result");
		empCloudSet.put("name", "人员考核结果");
		empCloudSet.put("type", "emp_result");
		empCloudSet.put("selected", "true");
		cloudset.add(empCloudSet);
		
		for(Object obj : subsetsArray){
			JSONObject arr = (JSONObject)obj;
			//过滤掉人员信息集 及 岗位序列集  及  兼职子集
//			if("bd_staff".equalsIgnoreCase(arr.getString("id")) || arr.getInt("type") == 6 || arr.getInt("type") == 0 ){
			//放开人员信息集指标
			if( arr.getInt("type") == 6 ){
				continue;
			}
			JSONObject newjson = new JSONObject();
			newjson.put("id", arr.get("id"));
			newjson.put("name", arr.get("name"));
			newjson.put("type", arr.get("type"));
			if(ids.size()>0 && ids.contains(arr.getString("id"))){
				newjson.put("selected", "true");
			}else{
				newjson.put("selected", "false");
			}
			cloudset.add(newjson);
		}
		return cloudset;
	}
	
	/**
	 * 获取云子集
	 * @cloudReturnJson
	 * @return
	 */
	@Override
    public JSONObject getCloudTOhrCurrentSet(String id){
		
		JSONObject currentSet = new JSONObject();
		
		if(StringUtils.isBlank(id)){
			//id为空时，默认选机构
			id = "org_result";
		}
		
		//获取云指标集合
//		cloudFieldDataJson
		JSONObject cloudFields = getCloudSubsetJson(id);
		
		//获取hr子集集合
		String hrset = "";
		if(cloudFields.containsKey("hr_set")){
			hrset = cloudFields.getString("hr_set");
		}
		JSONArray hr_set = getAssessSetList(cloudFields.getString("type"),hrset);
		
		currentSet.put("id", id);
		currentSet.put("name", cloudFields.getString("name"));
		currentSet.put("hr_set", hr_set);
		currentSet.put("isChildSet", cloudFields.get("isChildSet"));
		currentSet.put("cloud_fields", cloudFields.getJSONArray("fields"));
		
		return currentSet;
	}
	
	/**
	 * 获取云子集下的所有指标(不包含bd_staff人员主集)
	 * @param id 云subset的id
	 */
	private JSONObject getCloudSubsetJson(String id) {
		JSONObject returnObj = new JSONObject();
		JSONArray returnArr = new JSONArray();
		String hr_set = "";
		
		//constant表中数据放到field中
		JSONObject fieldIdTOconnectField = new JSONObject();
		JSONArray setMapping = (constantParamJson.containsKey("cloudTOhr") && constantParamJson.getJSONObject("cloudTOhr")!=null && constantParamJson.getJSONObject("cloudTOhr").containsKey("setMapping") &&constantParamJson.getJSONObject("cloudTOhr").getJSONArray("setMapping") !=null )?constantParamJson.getJSONObject("cloudTOhr").getJSONArray("setMapping"):null;
		if(setMapping != null){
			for(Object o : setMapping){
				JSONObject json = (JSONObject)o;
				if(id.equalsIgnoreCase(json.getString("cloudset_id")) && json.containsKey("cloud_fields")){
					JSONArray cloud_fields =json.getJSONArray("cloud_fields");
					hr_set = json.getString("hr_set");
					for(Object obj : cloud_fields){
						JSONObject field = (JSONObject)obj;
						fieldIdTOconnectField.put(field.getString("id"), field.getJSONObject("connectField"));
					}
					break;
				}
			}
		}
		//云中的指标数据
		if("org_result".equalsIgnoreCase(id)){
			//机构考核结果
			JSONObject json = new JSONObject();
			json.put("id", "businessId");
			json.put("name", "考核项目标识");
			json.put("type", "A");
			json.put("length", 32);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "name");
			json.put("name", "考核项目名称");
			json.put("type", "A");
			json.put("length", 50);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "type");
			json.put("name", "项目类型");
			json.put("type", "N");
			json.put("length", 2);
			json.put("dlength", 0);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "planDate");
			json.put("name", "计划日期");
			json.put("type", "D");
			json.put("length", 19);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "planYear");
			json.put("name", "考核年度");
			json.put("type", "N");
			json.put("length", 4);
			json.put("dlength", 0);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "sheetName");
			json.put("name", "考核表名称");
			json.put("type", "A");
			json.put("length", 50);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "score");
			json.put("name", "综合分数");
			json.put("type", "N");
			json.put("length", 10);
			json.put("dlength", 3);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "degreeName");
			json.put("name", "结果等级");
			json.put("type", "A");
			json.put("length", 50);
			json.put("codesetid", "0");
			returnArr.add(json);
			returnObj.put("id", id);
			returnObj.put("name", "机构考核结果子集");
			returnObj.put("type", 2);
			returnObj.put("fields", returnArr);
		}else if("emp_result".equalsIgnoreCase(id)){
			//人员考核结果
			JSONObject json = new JSONObject();
			json.put("id", "businessId");
			json.put("name", "考核项目标识");
			json.put("type", "A");
			json.put("length", 32);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "name");
			json.put("name", "考核项目名称");
			json.put("type", "A");
			json.put("length", 32);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "type");
			json.put("name", "项目类型");
			json.put("type", "N");
			json.put("length", 2);
			json.put("dlength", 0);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "planDate");
			json.put("name", "计划日期");
			json.put("type", "D");
			json.put("length", 19);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "planYear");
			json.put("name", "考核年度");
			json.put("type", "N");
			json.put("length", 4);
			json.put("dlength", 0);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "sheetName");
			json.put("name", "考核表名称");
			json.put("type", "A");
			json.put("length", 50);
			json.put("codesetid", "0");
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "score");
			json.put("name", "综合分数");
			json.put("type", "N");
			json.put("length", 10);
			json.put("dlength", 3);
			returnArr.add(json);
			json = new JSONObject();
			json.put("id", "degreeName");
			json.put("name", "结果等级");
			json.put("type", "A");
			json.put("length", 50);
			json.put("codesetid", "0");
			returnArr.add(json);
			returnObj.put("id", id);
			returnObj.put("name", "人员考核结果子集");
			returnObj.put("type", 1);
			returnObj.put("fields", returnArr);
		}else{
			JSONArray subsets = cloudFieldDataJson.getJSONArray("subsets");
			for(Object o : subsets){
				JSONObject subset = (JSONObject)o;
				if(id.equalsIgnoreCase(subset.getString("id"))){
					returnObj = subset;
					break;
				}
			}
		}
		
		if(StringUtils.isNotEmpty(hr_set)){
			returnObj.put("hr_set", hr_set);
		}
		if(!returnObj.containsKey("isChildSet")){
			returnObj.put("isChildSet", "0");
		}
		//constant表中数据放到field中
		returnArr = returnObj.getJSONArray("fields");
		JSONArray cloudFieldsArray = new JSONArray();
		for(int i = 0 ; i < returnArr.size() ; i++){
			JSONObject field = (JSONObject)returnArr.getJSONObject(i);
			//过滤掉内置指标
			if(
					"id".equalsIgnoreCase(field.getString("id")) || "staffNum".equalsIgnoreCase(field.getString("id")) || "thirdId".equalsIgnoreCase(field.getString("id")) ||
					"tenantId".equalsIgnoreCase(field.getString("id")) || "uuid".equalsIgnoreCase(field.getString("id")) || "showOrder".equalsIgnoreCase(field.getString("id")) || "status".equalsIgnoreCase(field.getString("id")) 
					|| "deleteFlag".equalsIgnoreCase(field.getString("id")) || "orgNum".equalsIgnoreCase(field.getString("id")) ||"thirdPosLine".equalsIgnoreCase(field.getString("id")) ||"posList".equalsIgnoreCase(field.getString("id"))){
				continue;
			}
			if(fieldIdTOconnectField.containsKey(field.getString("id"))){
//				returnObj.getJSONArray("fields").getJSONObject(i).put("connectField", fieldIdTOconnectField.get(field.getString("id")));
				field.put("connectField",fieldIdTOconnectField.get(field.getString("id")));
			}
			cloudFieldsArray.add(field);
		}
		returnObj.put("fields", cloudFieldsArray);
		return returnObj;
	}
	/**
	 * 获取考核结果子集	
	 * @param hr_set 
	 * @return
	 */
	private JSONArray getAssessSetList(String type, String hr_set) {
		int settype = 2;
		//1人员,2单位,3部门,4岗位,5职位,6岗位序列
		if("1".equalsIgnoreCase(type)){
			//人员考核结果
			settype = 1;
		}else if("4".equalsIgnoreCase(type)){
			//岗位指标
			settype = 3;
		}else if("0".equalsIgnoreCase(type)){
			//兼职信息表
			settype = 1;
		}
		
		
		ArrayList privList = this.userview.getPrivFieldSetList(settype);
		JSONArray returnList = new JSONArray();
		for(Object obj:privList){
			FieldSet set = (FieldSet)obj;
			if("1".equals(set.getUseflag())){
				JSONObject json = new JSONObject();
				json.put("set_id", set.getFieldsetid());
			    json.put("set_name", set.getFieldsetdesc());
			    if(!StringUtils.isEmpty(hr_set) && hr_set.equalsIgnoreCase(set.getFieldsetid())){
			    	json.put("selected", "true");
			    }
				returnList.add(json);
			}
		}
		return returnList;
	}
	
	/**
	 * 获取云指标自动关联JSONObject
	 * @param setMapping
	 * @return 
	 * {
		'A01':{
				cloudid:{
					itemid:’XX’,
					itemdesc:’XX’
				}
			  },
		'B04': {cloudid:{…}}
	   }
	 */
	@Override
    public JSONObject getCloudMatchJson(JSONArray setMapping) {
		JSONObject returnObj = new JSONObject();
		
		for(Object o : setMapping){
			JSONObject returnFields = new JSONObject();
			JSONObject set = (JSONObject)o;
			String hr_set = set.getString("hr_set");
			ArrayList fieldsetList = DataDictionary.getFieldList(hr_set, 1);
			JSONObject fielditemJson = new JSONObject();
			for(Object f :fieldsetList){
				FieldItem field = (FieldItem)f;
				fielditemJson.put(field.getItemdesc(), field.getItemid());
			}
			JSONArray cloud_fields = set.getJSONArray("cloud_fields");
			for(Object obj : cloud_fields){
				JSONObject returnField = new JSONObject();
				JSONObject field = (JSONObject)obj;
				String id = field.getString("id");
				String name = field.getString("name");
				if(fielditemJson.containsKey(name)){
					String itemid = fielditemJson.getString(name);
					returnField.put("id", id);
					returnField.put("itemid", itemid);
					returnField.put("itemdesc", name);
					returnFields.put(id, returnField);
				}
			}
			returnObj.put(hr_set, returnFields);
		}
		
		return returnObj;
	}
}
