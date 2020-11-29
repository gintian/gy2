package com.hjsj.hrms.module.template.templatemain.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.templatetoolbar.businessobject.TemplateToolBarBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VFSUtil;
import com.hrms.virtualfilesystem.VfsParam;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplateMainTrans.java</p>
 * <p>Description>:初始进入异动模板界面，（1）获取功能按钮</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-7-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
public class TemplateMainTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			this.userView.getHm().put("useNewTemplateUrl","true");
			HashMap formMap= this.getFormHM();
			TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
	        String sysType = TemplateFuncBo.getValueFromMap(formMap,"sys_type");
	        String moduleId = TemplateFuncBo.getValueFromMap(formMap,"module_id");
	        String returnFlag = TemplateFuncBo.getValueFromMap(formMap,"return_flag");
			String tabId = TemplateFuncBo.getValueFromMap(formMap,"tab_id");
			String fillInfo = (String) this.userView.getHm().get("fillInfo");
			String approveFlag = frontProperty.getApproveFlag();
			if(!PubFunc.isUseNewPrograme(this.userView))
		    	throw GeneralExceptionHandler.Handle(new Exception("新异动程序仅支持70版本以上的加密锁!"));
			if(tabId==null || "".equals(tabId)){
			    tabId="1";
				formMap.put("tab_id", tabId);
			}
			String taskId = TemplateFuncBo.getDecValueFromMap(formMap,"task_id");
			boolean isCombine = Boolean.parseBoolean(frontProperty.getOtherParam("iscombine"));
		    int task_count = Integer.parseInt((frontProperty.getOtherParam("task_count") == null) || ("".equals(frontProperty.getOtherParam("task_count"))) ? "0" : frontProperty.getOtherParam("task_count"));
		    String combine_num = frontProperty.getOtherParam("combine_num");
		    String combine_nodeid = frontProperty.getOtherParam("combine_nodeid");
		    TemplateDataBo dataBo = new TemplateDataBo(getFrameconn(), this.userView, Integer.parseInt(tabId));
		    if (isCombine){
		    	String approveFlag_ = "3".equals(approveFlag)?"11":approveFlag;//首页报备进来的
		    	String task_state = combine_num.split("_")[1];
		    	int combine_num_ = Integer.parseInt(combine_num.split("_")[0]);
		        ArrayList taskIdArr = dataBo.getDbTaskForUser(tabId, approveFlag_, task_count, combine_num_, task_state ,combine_nodeid);
		        taskId = taskIdArr.get(0).toString();
		        this.getFormHM().put("task_id", taskIdArr.get(1).toString());
		    }
		    if(/*("13".equals(returnFlag)||"14".equals(returnFlag))&&*/!"0".equals(taskId)) {
				String[] taskidarr = taskId.split(",");
				String taskids_ = "";
				for(int j=0;j<taskidarr.length;j++) {
					String taskid = taskidarr[j];
					taskids_ += this.getSplitTaskId(taskid);
				}
				taskids_ = taskids_.substring(0,taskids_.length()-1);
				String[] taskids = taskids_.split(",");
				String taskids_en = "";
				for(int i=0;i<taskids.length;i++) {
					if(i==0) {
						taskids_en = PubFunc.encrypt(taskids[i]);
					}else {
						if(taskids_en.indexOf(PubFunc.encrypt(taskids[i]))==-1)
							taskids_en+= ","+PubFunc.encrypt(taskids[i]);
					}
				}
				this.getFormHM().put("task_id", taskids_en);
			}
		    //如果用户配置连接错误，模版id不存在提示用户
			String view_type = TemplateFuncBo.getValueFromMap(formMap,"view_type");
			String prefix = TemplateFuncBo.getValueFromMap(formMap,"prefix");
			
			TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tabId));
			templateBo.setModuleId(moduleId);
	        templateBo.setTaskId(taskId);
			TemplateParam tableParamBo=templateBo.getParamBo();
			//进入时人员库设置
			String initbase = tableParamBo.getInit_base();
			ArrayList privDbList = this.userView.getPrivDbList();
			if(StringUtils.isNotBlank(initbase)){
				Boolean isHaveInitBase=false;
				for(int i=0;i<privDbList.size();i++){
					String dbname = (String) privDbList.get(i);
					if(initbase.equalsIgnoreCase(dbname)){
						isHaveInitBase=true;
						break;
					}
				}
				if(!isHaveInitBase){
					initbase="-1";
				}
			}
			if(tableParamBo.getTable_vo().getValues().size()==0)//liuyz bug32523  如果根据模版id不存在提示用户
			{
				throw new GeneralException("此模板不存在！");
			}
			if("0".equals(taskId)){//起草状态判断是否有模板的资源权限
				this.getFormHM().put("ins_id", "0");
				boolean isCorrect = getCorrect(tabId,moduleId);
				if(!isCorrect){
					throw new GeneralException("当前用户不具有此模板的资源权限！");
				}
			}else{
				boolean ExistsTaskId = this.checkExistsTaskId(taskId);
			    if(!ExistsTaskId)
			    	throw new GeneralException("该单据已被撤回！");
				this.getFormHM().put("ins_id", "");
			}

			
			if (!"card".equals(view_type)&&!"list".equals(view_type)){//如果没设置，则取默认定义方式
				view_type = tableParamBo.getView();
				this.getFormHM().put("view_type", view_type);		
			}
			//校验此单据是否处理过及被其他人锁定
			if ("1".equals(frontProperty.getApproveFlag())&& !"0".equals(taskId)/*&& !"1".equals(templateBo.isStartNode(taskId))*/){ // && !frontProperty.isBatchApprove()){
				String def_flow_self =tableParamBo.getDef_flow_self();
				if ("1".equals(def_flow_self)){
					String[] tasklist=StringUtils.split(taskId,",");
					for(int i=0;i<tasklist.length;i++){
						if (tableParamBo.isDef_flow_self(Integer.parseInt(tasklist[i]))){
	                        def_flow_self="2";
	                        break;
	                    }
					}
				}
				String errorInfo= templateBo.checkDealTaskInformation(taskId, def_flow_self);
				if (errorInfo.length()>0){//已处理过，置为不能审批
				    throw GeneralExceptionHandler.Handle(new Exception(errorInfo));   
				}
			}
			this.getFormHM().put("approve_flag",approveFlag); 			
			/**  往templateMain.js传递的一些参数，模板号固定后这些值一般都不会发生变化 start  */
			//业务类型
			this.getFormHM().put("operation_type", tableParamBo.getOperationType()+""); 			
			//获取表名
			TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn,this.userView);
			String tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);
			this.getFormHM().put("table_name",PubFunc.encryption(tableName));
			this.getFormHM().put("infor_type",tableParamBo.getInfor_type()+"");
			this.getFormHM().put("nbases", initbase);
			if("11".equals(moduleId)){//如果是职称评审，则判断是否有业务范围，如果有则走业务范围 lis 20160525
				String orgId = this.userView.getUnitIdByBusiOutofPriv("9");
				StringBuffer orgIdBuf = new StringBuffer();
				if(StringUtils.isNotBlank(orgId)){
					String[] orgIds = orgId.split("`");
					for(String orgid: orgIds){
						if(StringUtils.isNotBlank(orgid) && orgid.length() > 2){
							orgIdBuf.append(",");
							orgIdBuf.append(orgid.substring(2));
						}
					}
				}
				if(StringUtils.isNotBlank(orgIdBuf.toString()))
					this.getFormHM().put("orgId",orgIdBuf.toString().substring(1));
			}
			else if(!this.userView.isSuper_admin()&& !"1".equals(this.userView.getGroupId())){ 
				String orgid = this.getOrgId();
				this.getFormHM().put("orgId", orgid);
			}

			if("0".equalsIgnoreCase(taskId))
			{
				
			    if (frontProperty.isSelfApply()){//是否业务申请
			        if  ("".equals(this.userView.getA0100())){
			            throw GeneralExceptionHandler.Handle(new Exception("没有关联自助用户!"));   
			        }
			        //创建临时表
			        templateBo.createTempTemplateTable("");
			        if("1".equals(fillInfo)){
			        	//删除当前日期之前的记录
			        	this.deleteFillInfo(tableName);
			        	//自动生成一条记录。
				        templateBo.autoAddRecord(tableName);
			        }else{
			        	// 从档案库中导入当前人的数据
				        ArrayList a0100list=new ArrayList();
				        a0100list.add(this.userView.getA0100());
			            templateBo.impDataFromArchive(a0100list,this.userView.getDbname());
			            
			            String initValueOther = frontProperty.getOtherParam("iniValue");
			            // 考勤日历模板传参
		            	HashMap map = frontProperty.getInitValueParam(initValueOther, "i_");
		            	// 更改模板默认显示数据
		            	templateBo.updateInitValue(a0100list, tableName, map);
			        }
			    }
			    else {
			        templateBo.createTempTemplateTable(this.userView.getUserName());
			        // 自动生成一条记录。
			        templateBo.autoAddRecord(tableName);					
			        // 同步档案库数据
			        if("1".equals(tableParamBo.getAutosync_beforechg_item()))
			        	templateBo.syncDataFromArchive();	
			    }
			}
			else 
			{	
				utilBo.getAllTemplateItem(Integer.valueOf(tabId));//在流程中校验指标是否存在或数据类型改变 lis 20160802
				// 同步审批表结构
			    templateBo.changeSpTableStrut();
			    
			    if ("1".equals(tableParamBo.getAutosync_beforechg_item()) &&(!"0".equals(approveFlag)&&!"3".equals(approveFlag))){//需要同步档案库数据时才同步 //approveFlag=3代表是报备任务。
			        templateBo.syncDataFromArchive();
			    }
			}
		
			
			/*加载标题栏、工具栏*/
			TemplateToolBarBo toolBarBo= new TemplateToolBarBo(this.frameconn,this.userView);			
			//返回功能按钮及标题栏json
			ArrayList buttonList = new ArrayList();
			String visible_toolbar=frontProperty.getOtherParam("visible_toolbar");
			HashMap downmap = new HashMap();
			if (!"0".equals(visible_toolbar)){
			    buttonList = toolBarBo.getAllToolButtonList(tableParamBo,formMap);
			    if(formMap.containsKey("out_pages")) {
			    	this.getFormHM().put("out_pages", formMap.get("out_pages"));
			    }
			    if(formMap.containsKey("change_view")) {
			    	this.getFormHM().put("change_view", (Boolean)formMap.get("change_view"));
			    }
			    ////暂时添加 下载模板与上传数据 权限查询  start////
			    String functions = "";
				boolean bCanEdit=  "1".equals(approveFlag) || "0".equals(taskId);
				boolean bDraft=bCanEdit && ("0".equals(taskId));
				boolean bSelfApply = frontProperty.isSelfApply() && bDraft;
				boolean bDownload=false;
				functions="400040124,32024,32122,37022,37122,37222,37322,33001024,33101024,2701524,0C34824,324010121,2306721,23110221,3800724,325010121";
				if (bDraft && (!bSelfApply) && toolBarBo.haveFunctionIds(functions)){
					bDownload=true;
				}
				//上传模板
				boolean bUploadload=false;
				functions="400040125,33001025,33101025,2701525,0C34825,32025,32123,37023,37123,37223,37323,324010122,325010122,2306722,23110222,3800725";
				if (bDraft && (!bSelfApply) && toolBarBo.haveFunctionIds(functions)){
					bUploadload=true;
				}
				if(formMap.containsKey("import_btn")) {
					bUploadload=(Boolean)formMap.get("import_btn");//节点控制是否显示导入数据
				}
				downmap.put("bDownload", bDownload);
				downmap.put("bUploadload", bUploadload);
				////暂时添加 下载模板与上传数据 权限查询 end////
			}			
			String visible_title=frontProperty.getOtherParam("visible_title");
			this.getFormHM().put("visible_toolbar",visible_toolbar);
			this.getFormHM().put("visible_title",visible_title);
			
			ArrayList columnList = new ArrayList();
			String subModuleId=prefix;
			TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt(subModuleId)), columnList, 
					subModuleId, userView,this.getFrameconn());
			builder.setTableTools(buttonList);
			String title =tableParamBo.getName();
			builder.setTitle(title);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config);		
			
			String onlyname = "";
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			//得到系统设置的唯一性指标代码
			onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			//得到系统设置的唯一性指标是否可用
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");

			//系统未设置唯一性指标
			if ("0".equals(uniquenessvalid)){
				this.getFormHM().put("isValidOnlyname", "false");
				this.getFormHM().put("onlyname", ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName"));//请输入姓名
			}else{
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				if (item!=null){
				    //请输入姓名或唯一性标示
				    this.getFormHM().put("onlyname", ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName")+ResourceFactory.getProperty("label.logic.or")+item.getItemdesc());
				    this.getFormHM().put("isValidOnlyname", "true");
				}
				else {
				    this.getFormHM().put("onlyname", ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName"));//请输入姓名
				    this.getFormHM().put("isValidOnlyname", "false");
				}
			}
			//判断是否需要自动检索
			String needImportMen="false";
			String hasRecord="false";
			if("0".equalsIgnoreCase(taskId)&&!"9".equals(moduleId)){ //不为业务申请且为发启节点 dengcan 20160905
				//没有设定检索条件，也去获取是否按检索条件过滤和是否按管理范围控制
				boolean isPrivExpression=true;
				if("1".equals(tableParamBo.getNo_priv_ctrl()))
				{
					isPrivExpression=false;
				}
				this.getFormHM().put("isPrivExpression", isPrivExpression); 
		        this.getFormHM().put("filter_by_factor", tableParamBo.getFilter_by_factor());
			    if (tableParamBo.getFactor().length()>0){
			        needImportMen="true";
			        //手工选人、通用查询是否按管理范围管理
					
			        this.getFormHM().put("filter_factor", tableParamBo.getFactor());
			        if (isHasRecord(tableName)){
			            hasRecord="true"; 
			        }
			        this.getFormHM().put("factor_update_type", tableParamBo.getFactor_update_type());
			        //非人员模板获得设置的检索条件对应的sql
			        if(tableParamBo.getInfor_type()!=1) {
			        	String sqlwhere_factor = templateBo.getFactor2Sql();
			        	this.getFormHM().put("sqlwhere_factor", PubFunc.encrypt(sqlwhere_factor));
			        }
			    };
            }
			if(!"0".equalsIgnoreCase(taskId)&&!"0".equals(approveFlag)){
            	this.setReadFlag(taskId);//设置是否阅读
            	this.setPendingTask(taskId, approveFlag);
            }
			this.getFormHM().put("needImportMen", needImportMen); 
			this.getFormHM().put("hasRecord", hasRecord); 
			//版本标识
			this.getFormHM().put("bos_flag", this.userView.getBosflag()); 
			//判断是否是（起草或者驳回到起草）或者是审批状态
			Boolean tasktype=false;		
			if("0".equals(taskId)|| "1".equals(templateBo.isStartNode(taskId))){
				tasktype=true;
				if("1".equals(fillInfo)){//如果有此参数，且其等于1， 则不显示刷新按钮
					tasktype=false;
				}
			}else {
				tasktype=false;
			}
			/*
			if(info.get("isRole")!=null)
				this.getFormHM().put("isRole","1");
			else
				this.getFormHM().put("isRole","0");
			*/
			this.getFormHM().put("tasktype", tasktype); 
			AttachmentBo attachmentBo = new AttachmentBo(userView, frameconn, tabId);
			attachmentBo.initParam(true);
			String multimedia_maxsize = attachmentBo.getMaxFileSize()/1024/1024 + "MB";
			String rootDir = attachmentBo.getRootDir();
			//多媒体文件上传限制大小
			this.getFormHM().put("multimedia_maxsize", multimedia_maxsize); 
			//是否设置了文件存放根目录
			this.getFormHM().put("rootDir", rootDir); 
			if (!"0".equals(visible_toolbar))
				this.getFormHM().put("downmap", downmap); 
			//是否自动计算
			Boolean bCalc=false;	
            if("0".equals(taskId)|| "1".equals(templateBo.isStartNode(taskId))){
    			if(tableParamBo.getAutoCaculate().length()==0){
    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
    					bCalc=true;
    				}
    			}
    			else if("1".equals(tableParamBo.getAutoCaculate())){
    				bCalc=true;
    			}	
    		}else {
    			if(tableParamBo.getSpAutoCaculate().length()==0){
    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
    					bCalc=true;
    				}
    			}
    			else if("1".equals(tableParamBo.getSpAutoCaculate())){
    				bCalc=true;
    			}
    		}
            this.getFormHM().put("autoCompute", bCalc); 
			boolean isInterval = this.getRoleType(taskId);
			this.getFormHM().put("isInterval", isInterval); 
			//获得第一个设置页码的模板页
			int firstPageNo = this.getFirstPageNo(Integer.parseInt(tabId));
			this.getFormHM().put("firstPageNo", firstPageNo);
			String autoLogColor = tableParamBo.getAutoLogColor();
			Boolean isAotuLog = tableParamBo.getIsAotuLog();
			Boolean isRejectAotuLog = tableParamBo.getIsRejectAotuLog();
			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(taskId)){
				Boolean haveReject= utilBo.isHaveRejectTaskByTaskId(taskId);
				if(haveReject){
					isAotuLog=true;
				}
			}
			this.getFormHM().put("isAutoLog", isAotuLog);
			this.getFormHM().put("autoLogColor", autoLogColor);
			FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
			if(tableParamBo.isArchiveAttachToMainSet()||"A01".equalsIgnoreCase(tableParamBo.getArchive_attach_to())){
				if(!"1".equals(a01Set.getMultimedia_file_flag())){
					throw GeneralExceptionHandler.Handle(new Exception("当前模板设置了“个人附件归档至主集附件”，但是主集未设置支持附件，请联系管理员！"));  
				}
			
			}
			
		    this.getFormHM().put("@eventlog","表单号:"+tabId+",任务号:"+taskId);
			String allowExt = SystemConfig.getAllowExt();
			String extArr [] = allowExt.split(",");
			String extControl = "";
			for(int i=0;i<extArr.length;i++) {
				String ext = extArr[i];
				if(StringUtils.isNotEmpty(ext)) {
					extControl+="*."+ext+";";
				}
			}
			this.getFormHM().put("extControl",extControl);
			VfsParam vfsParam = VFSUtil.getParam();
			int vfstype = vfsParam.getType();
			this.getFormHM().put("vfstype", vfstype);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获得用户对应的操作单位=》人员范围
	 * @return 
	 */
	private String getOrgId() {
		String orgid = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rset = null;
		try {
			String unit_id = this.userView.getUnit_id();
			String privcode = this.userView.getManagePrivCode();
			String privcodeValue = this.userView.getManagePrivCodeValue();
			if("UN`".equalsIgnoreCase(unit_id)){//UN`=全部 需要查到最顶层的UN节点
				String sql = "select codeitemid from organization where grade=1 and codesetid='UN'";
				rset = dao.search(sql);
				while(rset.next()) {
					String codeitemid = rset.getString("codeitemid");
					orgid += codeitemid+",";
				}
				if(StringUtils.isNotBlank(orgid)) {
					orgid = orgid.substring(0,orgid.length()-1);
				}
			} else if (StringUtils.isNotEmpty(unit_id) && unit_id.length()>2 || StringUtils.isNotEmpty(privcode)) {
				if(StringUtils.isNotEmpty(unit_id) && unit_id.length() > 2) {
					String[] unitArr = unit_id.split("`");
					String unitStr = "";
					for(int j=0;j<unitArr.length;j++) {
						String unit = unitArr[j].substring(2);
						unitStr += unit+",";
					}
					orgid=unitStr.substring(0, unitStr.length()-1);
				}else {
				    //此处都为空才是没有权限，如果 privcodeValue为空，privcode不为空有可能是顶级机构“组织机构”
					if (StringUtils.isBlank(privcode) && StringUtils.isBlank(privcodeValue)) {
						orgid = "asdf";
					} else {
						orgid = privcodeValue;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return orgid;
	}
	
	private String getSplitTaskId(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String taskids = taskId+",";
		try {
			String sql  = "select task_id from t_wf_task where originate_id="+taskId+" order by task_id";
			rowSet = dao.search(sql);
			while(rowSet.next()) {
				String task_id = rowSet.getString("task_id");
				//判断此task_id是否结束了
				RecordVo taskvo = new RecordVo("t_wf_task");
				taskvo.setInt("task_id", Integer.parseInt(task_id));
				taskvo = dao.findByPrimaryKey(taskvo);
				String task_state = taskvo.getString("task_state");
				if(!"4".equals(task_state)&&!"5".equals(task_state)&&!"6".equals(task_state)) {//非结束得
					taskids+=getSplitTaskId(task_id);
				}else {
					continue;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return taskids;
	}
	/**
	 * 获得第一个插入页码的模板页
	 * @param tabid
	 * @return
	 */
	private int getFirstPageNo(int tabid) {
		int pageid = -1;
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select min(pageid) as pageid from template_title where tabid=");
			strsql.append(tabid);
			strsql.append(" and flag=5");
			rowSet = dao.search(strsql.toString());
			if(rowSet.next()) {
				pageid = rowSet.getInt("pageid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return pageid;
	}
	/**
	 * 判断taskid的任务是否存在
	 * @param taskId
	 */
	private boolean checkExistsTaskId(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		boolean ExistsTaskId = false;
		try{
			String[] tasklist=StringUtils.split(taskId,",");
			if(tasklist.length==1){
				rowSet = dao.search("select 1 from t_wf_instance t,t_wf_task t1 where t.ins_id=t1.ins_id and t1.task_id='"+tasklist[0]+"'");
				if(rowSet.next())
					ExistsTaskId = true;
			}else
				ExistsTaskId = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return ExistsTaskId;
	}
	/**
	 * 得到当前任务节点是不是角色(排除特殊角色)
	 * @param taskId
	 * @return
	 */
	private boolean getRoleType(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		boolean isInterval = false;
		try {
			String [] list = StringUtils.split(taskId,",");
			String taskid = "";
			for(int i=0;i<list.length;i++){
				if(StringUtils.isBlank(list[i]))
					continue;
				if(i==0)
					taskid+=list[i];
				else
					taskid+=","+list[i];
			}
			if(StringUtils.isNotBlank(taskid)) {
				rowSet = dao.search("select DISTINCT t2.task_id from t_sys_role t4,t_wf_node t1,t_wf_task t3,t_wf_task_objlink t2 where "
						+ "t1.node_id=t2.node_id and t2.task_id=t3.task_id and t3.actorid=t4.role_id and "
						+ Sql_switcher.isnull("t2.state","0")+"=0 and t2.task_id in("+taskid+") and "
						+ "t1.nodetype=2 and t4.role_property not in (9,10,11,12,13,14)");
				if(rowSet.next()){
					isInterval = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return isInterval;
	}
	/**
	 * 删除之前记录
	 * @param tableName 
	 */
	private void deleteFillInfo(String tableName) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			ArrayList values = new ArrayList();
			String fieldname = "create_time";
			String sql="delete from "+tableName+" where ("+Sql_switcher.diffDays(Sql_switcher.sqlNow() ,fieldname)+")>0.5";
			dao.delete(sql, values);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private boolean getCorrect(String tabid,String moduleId) {
		
		UserView userView = this.getUserView();
		if("9".equalsIgnoreCase(moduleId))//业务申请
		{ 
			//业务用户关联自助用户 按自助用户走
			if(userView.getS_userName()!=null&&userView.getS_userName().length()>0&&userView.getStatus()==0&&userView.getBosflag()!=null){
				userView=new UserView(userView.getS_userName(), userView.getS_pwd(), this.getFrameconn());
				try {
					userView.canLogin();
				} catch (Exception e) {
					e.printStackTrace(); 
					return false;
				}
			}
		}
		
		/**判断用户是否拥有该模版资源的权限**/
        boolean isCorrect=false;
        if(userView.isHaveResource(IResourceConstant.RSBD,tabid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.ORG_BD,tabid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.POS_BD,tabid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.GZBD,tabid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.INS_BD,tabid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.PSORGANS,tabid))
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.PSORGANS_FG,tabid))
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.PSORGANS_GX,tabid))
                isCorrect=true;
        if(!isCorrect)
            if(userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid))
                isCorrect=true;
		return isCorrect;
	}

	/**
	 * 对人员调入模板业务，需要升级，前台人员列表姓名才不为空
	 * @throws GeneralException
	 */
	private void updateSeqNum(int infor_type,int tabid)throws GeneralException
	{
		String strDesT=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			strDesT=this.userView.getUserName()+"templet_"+tabid; 
			String sql="select * from "+strDesT ;
			RowSet rowSet=dao.search(sql);
			while(rowSet.next()){
				String	seqnum = rowSet.getString("seqnum");
				if(seqnum==null||seqnum.trim().length()==0){
					seqnum=CreateSequence.getUUID();
					if(infor_type==1) 
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+rowSet.getString("basepre").toLowerCase()+"'");
					}
					else if(infor_type==2)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where b0110='"+rowSet.getString("b0110")+"'");
					}
					else if(infor_type==3)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where E01A1='"+rowSet.getString("E01A1")+"'");
					}
				}
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	

    /**
     * 是否有记录
     */
    public boolean isHasRecord(String tablename)throws GeneralException
    {
    	boolean b=false;
        RowSet rset = null;
        try
        {
            ContentDAO dao=new ContentDAO(this.frameconn);
            String sql ="select count(*) as nrec from "+tablename;
            rset=dao.search(sql);
            if(rset.next()){
                if (rset.getInt("nrec")>0){
                    b=true;
                };
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally {
        	PubFunc.closeDbObj(rset);
        }
       return b;
    }
    
    /**
     * @author lis
     * @Description: 设置任务阅读
     * @date 2016-5-13
     * @param taskid
     */
    private void setReadFlag(String taskid)
	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			int j=1;
			StringBuffer updateSql = new StringBuffer("update t_wf_task set bread=1 where task_id in (-1");
			StringBuffer middleSql = new StringBuffer("");
			String[] taskidStr = taskid.split(",");
			for(int i=0;i<taskidStr.length;i++){
				String taskId = taskidStr[i];
				middleSql.append(",");
				middleSql.append(taskId);
				if(i==500*j){//每500条执行一次
					j++;
					middleSql.append(")");
					dao.update(updateSql.toString()+middleSql.toString());
					middleSql.setLength(0);
				}
			}
			if(middleSql.length()>0)
				dao.update(updateSql.toString()+middleSql.toString()+" )");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    private void setPendingTask(String taskid,String approve_flag) {
		PendingTask imip=new PendingTask();
		String pendingType="业务模板";
		int pendingStatus = 2;//已阅
		if("3".equals(approve_flag))//报备
			pendingStatus = 1;//已办
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			String[] taskidStr = taskid.split(",");
			for(String taskId:taskidStr){
				taskId = PubFunc.encrypt(taskId);
				imip.updatePending("T","HRMS-"+taskId,pendingStatus,pendingType,this.userView);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
