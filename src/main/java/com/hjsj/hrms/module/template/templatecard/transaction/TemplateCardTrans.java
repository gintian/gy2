package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplateCardTrans.java</p>
 * <p>Description>:模板卡片类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-7-29 下午01:36:00</p>
 * <p>@version: 7.0</p>
 */
public class TemplateCardTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			HashMap formMap = this.getFormHM();
			TemplateFrontProperty frontProperty = new TemplateFrontProperty(
					formMap);
			String sysType = frontProperty.getSysType();
			String moduleId = frontProperty.getModuleId();
			String approveFlag = frontProperty.getApproveFlag();
			String returnFlag = frontProperty.getReturnFlag();
			String tabId = frontProperty.getTabId();
			String taskId = frontProperty.getTaskId();
			String object_id = frontProperty.getOtherParam("object_id");
			String isDelete = frontProperty.getOtherParam("isDelete");
			String cur_object_id = TemplateFuncBo.getValueFromMap(formMap,
					"cur_object_id");//当前定位的人 回传回去
			
			String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
			//linbz 查询条件
			String filterStr = frontProperty.getOtherParam("search_sql");
			String taskid_validate = PubFunc.decrypt(frontProperty.getOtherParam("taskid_validate"));
			//card增加查询控件所需参数
			String subModuleId = "templet_"+tabId;
			this.getFormHM().put("subModuleId", subModuleId);
			//是否显示左侧人员列表 是否自助  当前模板类型
			TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,
					this.userView, Integer.parseInt(tabId));
			if(taskid_validate!=null&&!"".equals(taskid_validate)&&taskId.equals(taskid_validate.split("_")[0]))
		    	dataBo.getParamBo().setNeedJudgPre("0");
			/**获取左侧人员导航-----------------*/
			// 获取列头 
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			String dataTabName = dataBo.getUtilBo().getTableName(moduleId,
					Integer.valueOf(tabId), taskId);
			columnsInfo = getGridColumnList(dataBo, dataTabName);
			StringBuffer orderBy = new StringBuffer();
			if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo
					.getParamBo().getInfor_type() == 3)
					&& (dataBo.getParamBo().getOperationType() == 8 || dataBo
							.getParamBo().getOperationType() == 9)) {
				String key = "b0110";
				if (dataBo.getParamBo().getInfor_type() == 3)
					key = "e01a1";
				orderBy.append("  order by "
						+ Sql_switcher.isnull("to_id", "100000000")
						+ ",case when " + key
						+ "=to_id then 100000000 else a0000 end asc ");
			} else
				orderBy.append(" order by a0000");
			ArrayList displayColumns =null;
			// 方案查询//liuyz bug32425 列表栏目设置更改排序规则，卡片跟着变化
			 TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get(subModuleId);//liuyz bug32425 修正卡片人员显示的顺序没有和栏目设置的顺序一致
	            if(tableCacheList!=null)
	            {
	            	String sortSql = tableCacheList.getSortSql();
	            	HashMap customParamHM = tableCacheList.getCustomParamHM()==null?new HashMap():tableCacheList.getCustomParamHM();
					String property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
					String direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
					if(StringUtils.isNotBlank(property)&&StringUtils.isNotBlank(direction)) {// 卡片人员顺序也要和列表下每列的排序结果相同  update 20180105
						orderBy.setLength(0);
	            		orderBy.append(" order by "+property+ " "+direction);
					}
					else if(sortSql!=null&&sortSql.trim().length()>0)
	            	{
	            		orderBy.setLength(0);
	            		orderBy.append(sortSql);
	            	}
					String filterStr_catch=(String)customParamHM.get("filterStr_catch");//获取列表过滤条件的sql
	            	if(StringUtils.isNotBlank(filterStr_catch))
	            		filterStr+=filterStr_catch;
	            	displayColumns= tableCacheList.getDisplayColumns();
	            }
			//获取数据 
			//ArrayList<LazyDynaBean> dataList =dataBo.getDataList(moduleId,returnFlag,approveFlag,taskId, object_id,"");
			String sql = dataBo.getSql(moduleId, returnFlag, approveFlag,
					dataTabName, taskId, object_id, filterStr, isDelete );
			//将sql中T.*替换成具体得字段
			String replaceSql = "";
			for(int i=0;i<columnsInfo.size();i++) {
				ColumnsInfo columnsinfo = columnsInfo.get(i);
				String columnid = columnsinfo.getColumnId();
				if("".equals(columnid)||columnid==null||"objectid".equals(columnid)||"objectid_noencrypt".equals(columnid)||"submitflag2".equals(columnid)||"realtask_id".equals(columnid))
					continue;
				if("ins_id".equals(columnid)) {
					if (!"0".equals(taskId)) {
						replaceSql+="T."+columnid+",";
			        }
					continue;
				}
				replaceSql+="T."+columnid+",";
			}
			if(dataBo.getParamBo().getInfor_type() == 2)
				replaceSql+="T.b0110,";
			else if(dataBo.getParamBo().getInfor_type() == 3)
				replaceSql+="T.e01a1,";
			else if(dataBo.getParamBo().getInfor_type() == 1)
				replaceSql+="T.basepre,T.a0100,";	
			replaceSql+="T.a0000";
			if(displayColumns!=null){
				for(int num=0;num<displayColumns.size();num++)
				{
					ColumnsInfo column = (ColumnsInfo)displayColumns.get(num);
	     			String columnId = column.getColumnId();
	     			if(StringUtils.isBlank(columnId))
	     				continue;
	     			if(orderBy.toString().toLowerCase().indexOf(columnId.toLowerCase())!=-1&&replaceSql.toLowerCase().indexOf(("T."+columnId).toLowerCase())==-1){
	     				replaceSql+=",T."+columnId;
	     			}
				}
			}
			if(replaceSql.length()>0) {
				sql = sql.replace("T.*", replaceSql);
			}
			
			/*if("11".equals(returnFlag)||"1".equals(returnFlag))//首页待办或我的待办进入将人员全部自动选中
			{
				String updateSql="update t_wf_task_objlink set submitflag=1 where exists("+sql+" and O.seqnum=t_wf_task_objlink.seqnum  )";
				ContentDAO dao=new ContentDAO(this.frameconn);
				dao.update(updateSql);
			}*/
			
			//29235 linbz 增加选人控件不显示的人员参数
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rowset=dao.search(sql);
			//29235 linbz 增加选人控件不显示的人员参数
			if(dataBo.getParamBo().getInfor_type() == 1){
				ArrayList objectslist = new ArrayList();
				while(rowset.next()){
					String nbase = rowset.getString("basepre");
					String a0100 = rowset.getString("a0100");
					objectslist.add(PubFunc.encrypt(nbase+a0100));
				}
				this.getFormHM().put("deprecate", objectslist);
				String deprecateFlag = frontProperty.getOtherParam("deprecate_flag");
	            if("1".equals(deprecateFlag)){
	            	return;
	            }
			}
			//农大一个人就不显示右侧人员列表不显示复杂查询框
			//RowSet rowset = dao.search(sql);
			Boolean hidePersonGrid=false;
			int count=0;
			rowset.beforeFirst();
			while(rowset.next())
			{
				count++;
			}
			if(count==1)
			{
				hidePersonGrid=true;
				//bug 33094 任务上只有一个人的时候，出现这个人员记录没有选中的情况，报批报错：没有选中记录！
				if(StringUtils.isEmpty(filterStr))
				{
					filterStr=" and 1=1 ";
				}
			}
			//syl 拼接列明 卡片模式左侧人员列表 显示 共几条 初始化 TemplateToolBar.js 该js文件 实时刷新
			String name = "";
			if (dataBo.getParamBo().getInfor_type() == 2
					|| dataBo.getParamBo().getInfor_type() == 3) {//单位名称
				if (dataBo.getParamBo().getOperationType() == 5) {
					name = "codeitemdesc_2";
				} else {
					name = "codeitemdesc_1";
				}
			}
			if (dataBo.getParamBo().getInfor_type() == 1) {
				DbWizard dbWizard = new DbWizard(this.frameconn);

				if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
					if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
						name = "a0101_2";
					}
				} else {
					name = "a0101_1";
				}
			}
			for(int i=0;i<columnsInfo.size();i++) {
				ColumnsInfo columnsinfo = columnsInfo.get(i);
				String columndesc = columnsinfo.getColumnDesc();
				String columnid = columnsinfo.getColumnId();
				if(name.equals(columnid)){
					rowset=dao.search("select count(*) num1 from ("+sql+") msql");
					if(rowset.next())
					{
						int c=rowset.getInt(1);
						//显示列名称
						columnsinfo.setHintText(columndesc);
						columndesc="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+columndesc;
						columndesc+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size='1px' color='#9e9e9e'>&nbsp;共"+c+"条</font>";
						columnsinfo.setColumnDesc(columndesc);
						break;
					}
					
				}
			}
			PubFunc.closeDbObj(rowset);
			this.getFormHM().put("hidePersonGrid", hidePersonGrid);
			//  加载表格 
			TableConfigBuilder builder = new TableConfigBuilder("templatecard",
					columnsInfo, "templatecard", userView, this.getFrameconn());
			//builder.setDataList(dataList);
			builder.setDataSql(sql);
			builder.setOrderBy(orderBy.toString());
			builder.setPageSize(20);
			builder.setSelectable(true);
			builder.setSetScheme(false);
			builder.setModuleId("templatecard");
			builder.setColumnFilter(false);
			builder.setSortable(false);
			//linbz 优化过滤条件后默认设置选中
            builder.setBeforeLoadFunctionId("MB00003009");
            HashMap map = new HashMap();
            map.put("taskId", taskId);
            map.put("tableName", dataTabName);
            map.put("search_sql", filterStr);
            builder.setCustomParamHM(map);
            
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config);
			if (cur_object_id.length() > 0) {
				this.getFormHM().put("objectId", cur_object_id);
			}
			/*else if (dataList.size()>0){
				LazyDynaBean lazyvo = (LazyDynaBean)dataList.get(0);
				String objectid =(String)lazyvo.get("objectid");        
				this.getFormHM().put("objectId", objectid);
			}*/
			/** end 获取左侧人员导航-----------------*/
			dataBo.getParamBo().setReturnFlag(returnFlag);//liuyz 我的申请不需要去获取节点权限
			TemplateCardBo cardBo = new TemplateCardBo(this.frameconn,
					this.userView, dataBo.getParamBo());

			String selfapply = "0";
			if (frontProperty.isSelfApply()) {
				selfapply = "1";
			}
			cardBo.setSelfApply(selfapply);
			cardBo.setApproveFlag(frontProperty.getApproveFlag());
			cardBo.setTask_id(taskId);
			/**获取页数-----------------*/
			ArrayList outlist = getPageList(Integer.parseInt(tabId), false, noShowPageNo);
			ArrayList pageList = new ArrayList();
			String title="";
			for (int i = 0; i < outlist.size(); i++) {
				TemplatePage pagebo = (TemplatePage) outlist.get(i);
				//判断此页的指标无读写权限。无读写权限指标的不显示
				if (!cardBo.isHaveReadFieldPriv(pagebo.getPageId() + "")) {//
					continue;
				}
				//liuyz 优化 页签加只读和编辑图标
				if(cardBo.isReadOnly(pagebo.getPageId() + ""))
				{
					//liuyz 27387 解决页签分离给标题套一层div设置最小宽度和js中设置的minTabWidth相同
					if(outlist.size()>7) {//页签超出7个 纵向展现页签 页签title样式修改
						title="<div style=\"width:110px;\"><table style=\"table-layout:fixed;width:110px;\">"
								+ "<tr><td style=\"word-wrap:break-word;text-align:left;"
								+ "white-space: normal;line-height: normal;font-weight: normal !improtant;"
								+ "font-size: 12px;font-style: normal;\" class=\"x-tab-inner-default\"><div>"+pagebo.getTitle()+"</div></td></tr></table></div>";
					}else
						title="<div style='min-width:86px'>"+pagebo.getTitle()+"</div>";
				}
				else
				{
					//liuyz 27387 解决页签分离给标题套一层div设置最小宽度和js中设置的minTabWidth相同
					if(outlist.size()>7) {
						title="<div style=\"width:110px;\"><table style=\"table-layout:fixed;width:110px;\">"
								+ "<tr><td style=\"word-wrap:break-word;text-align:left;"
								+ "white-space: normal;line-height: normal;font-weight: normal !improtant;"
								+ "font-size: 12px;font-style: normal;\" class=\"x-tab-inner-default\"><div>"+pagebo.getTitle()+"</div></td></tr></table></div>";
					}
					else
						title="<div style='min-width:86px'>"+pagebo.getTitle()+"</div>";
				}
				LazyDynaBean lazyvo = new LazyDynaBean();
				lazyvo.set("pageId", pagebo.getPageId());
				lazyvo.set("title", title);//liuyz 优化 页签加只读和编辑图标
				pageList.add(lazyvo);
			}
			this.getFormHM().put("pageList", pageList);
			this.getFormHM().put("operationType", dataBo.getParamBo().getOperationType());
		/** end 获取页数-----------------*/
			/**签章厂家标识  start    **/
			int signatureType = dataBo.getParamBo().getTemplateModuleParam().getSignatureType();
			String mServerUrl=userView.getServerurl()+"/iSignatureHTML/Service.jsp";
			this.getFormHM().put("signatureType",signatureType);
			this.getFormHM().put("mServerUrl",SafeCode.encode(mServerUrl));
			if(signatureType==2) {
				//查询当前登陆用户对应得签章
				SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
				ArrayList imgUrlList = bo.getSignatureUrl();
				this.getFormHM().put("imgUrlList",imgUrlList);
				String username = this.userView.getUserName();
				if(this.userView.getStatus()==4) {
					username = this.userView.getDbname()+this.userView.getA0100();
				}
				this.getFormHM().put("signature_usb",dataBo.getParamBo().getTemplateModuleParam().getSignature_usb());
				this.getFormHM().put("currentUser",username);
			}else if(signatureType==3) {
				String serverUrlForHtml5 = dataBo.getParamBo().getTemplateModuleParam().getServer_url();
				this.getFormHM().put("serverUrlForHtml5",serverUrlForHtml5);
				//获取用户对应的签章key
				SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
				String keysn = bo.getKeysn();
				this.getFormHM().put("keysn",keysn);
				this.getFormHM().put("currentUsername",this.userView.getUserName());
			}
			
			/**签章厂家标识  end    **/
			/** end 获取页数-----------------*/

			//获取所有模板页指标模型
			ArrayList fieldList = cardBo.getFieldList();
			this.getFormHM().put("fieldList", fieldList);
			
			HashMap nodePrivMap = new HashMap(); 
            if (!"3".equals(returnFlag)&&dataBo.getParamBo().getSp_mode()==0) {//liuyz 我的申请不需要去获取节点权限
                nodePrivMap = dataBo.getUtilBo().getFieldPrivByNode(taskId,Integer.parseInt(tabId));
            }
            if(nodePrivMap.size()>0)
            	this.getFormHM().put("nodePriv","1"); //是否受节点权限控制
            else
            	this.getFormHM().put("nodePriv","0");
            String node_id = dataBo.getUtilBo().getNodeIdByTask_ids(taskId,tabId);//根据task_id查询节点id，用于判断签章
            this.getFormHM().put("node_id",StringUtils.isBlank(node_id)?"-1":node_id);
            
            /** linbz 卡片增加查询控件，取得复杂查询下拉中的字段* */
            ArrayList fieldsMap = new ArrayList();
            ArrayList fieldJsonList = getDownDataList(fieldList);
            JSONArray tablecolumnsList = (JSONArray)(JSONArray.fromObject(fieldJsonList));
            ArrayList fieldsArray =cardBo.getFieldsArray(tablecolumnsList, fieldsMap);
            this.getFormHM().put("fieldsArray", fieldsArray);
            this.getFormHM().put("fieldsMap", fieldsMap);
            String opinion_field = dataBo.getParamBo().getOpinion_field();//liuyz bug31563
            this.getFormHM().put("opinion_field", opinion_field==null?"":opinion_field);
            // 方案查询
            TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("templatecard");
            if (StringUtils.isNotBlank(filterStr)) {
                tableCache.setTableSql(sql+filterStr.toString());
            }
            if(tableCache!=null)
            {
            	String sortSql = tableCache.getSortSql();
            	if(sortSql!=null&&sortSql.trim().length()>0)
            	{
            		String muster_sql=(String)this.userView.getHm().get("template_sql");
					//liuyz bug32425 列表栏目设置更改排序规则，卡片跟着变化
            		if(tableCacheList!=null&&tableCacheList.getSortSql()!=null&&tableCacheList.getSortSql().trim().length()>0){
            			//bug 33668 人事异动按单位排序后和组织机构模块显示一致
            			ArrayList columns = tableCacheList.getDisplayColumns();
            			rebuildSearchSql(columns,dataBo.getParamBo(),sortSql,tableCache,sql);
            			this.userView.getHm().put("template_sql",muster_sql+orderBy);
            		}
            		else{
	            		this.userView.getHm().put("template_sql",muster_sql+sortSql);
            		}
            	}
            }
            
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 取得复杂查询下拉中的字段集合
	 * @param fieldList 
	 * @return
	 */
	public ArrayList getDownDataList(ArrayList fieldList){
		ArrayList fieldJsonList = new ArrayList();
        LazyDynaBean bean = new LazyDynaBean();
        for(int i=0;i<fieldList.size();i++){
        	bean = (LazyDynaBean) fieldList.get(i);
        	String fldName = (String) bean.get("fldName");
        	String fldType = (String) bean.get("fldType");
        	String flag = (String) bean.get("flag");
        	//30228 增加指标权限控制
        	String rwPriv = (String) bean.get("rwPriv");
        	if("clob".equalsIgnoreCase(fldType) || "M".equalsIgnoreCase(fldType) 
        			|| "blob".equalsIgnoreCase(fldType) || "photo".equalsIgnoreCase(fldName) || "ext".equalsIgnoreCase(fldName) 
        			|| "V".equalsIgnoreCase(flag) || "F".equalsIgnoreCase(flag) || "S".equalsIgnoreCase(flag)
        			|| "0".equals(rwPriv)){
        		
        		continue;
        	}
        	
        	fieldJsonList.add(bean);
        }
        return fieldJsonList;
	}
	/** 
	 * @Title: getPageList 
	 * @Description:  获取模板显示的页签
	 * @param @param isMobile 是否显示异动标签
	 * @param noShowPageNo  不显示那些页签
	 * @param @return
	 * @param @throws Exception
	 * @return ArrayList
	 */
	public ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo) throws Exception {
		ArrayList outlist = new ArrayList();
		try {
			TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn,
					this.userView);
			ArrayList list = utilBo.getAllTemplatePage(tabId);
			for (int i = 0; i < list.size(); i++) {
				TemplatePage pagebo = (TemplatePage) list.get(i);
				if(!"".equals(noShowPageNo)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(pagebo.getPageId());
					String[] pagearr = noShowPageNo.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint)
						continue;
				}else if(!pagebo.isShow()) {
					continue;
				}

				if (isMobile != pagebo.isMobile()) {
					continue;
				}
				outlist.add(pagebo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return outlist;
	}

	/** 
	 * @Title: getPersonListGridColumnList 
	 * @Description: 人事异动主界面-卡片-左侧人员列表列头。
	 * @param TemplateParam paramBo
	 * @param @return
	 * @return ArrayList<ColumnsInfo>
	 */
	public ArrayList<ColumnsInfo> getGridColumnList(TemplateDataBo dataBo,
			String dataTabName) {
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		// 是否选择
		ColumnsInfo submitflag = TemplateLayoutBo.getColumnsInfo("submitflag2",
				"submitflag", "A", 100);
		submitflag.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnTmp.add(submitflag);
		// 流程号
		ColumnsInfo insId = TemplateLayoutBo.getColumnsInfo("ins_id", "ins_id",
				"A", 100);
		insId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnTmp.add(insId);
		// 任务号
		ColumnsInfo taskId = TemplateLayoutBo.getColumnsInfo("realtask_id",
				"realtask_id", "A", 100);
		taskId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		taskId.setEncrypted(true);
		columnTmp.add(taskId);
		// 人员编号
		ColumnsInfo objectId = TemplateLayoutBo.getColumnsInfo("objectid",
				"人员编号", "A", 100);
		objectId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		objectId.setEncrypted(true);
		objectId.setKey(true);
		columnTmp.add(objectId);
		
		// 人员编号
		ColumnsInfo objectId_noencrypt = TemplateLayoutBo.getColumnsInfo("objectid_noencrypt",
				"人员编号", "A", 100);
		objectId_noencrypt.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnTmp.add(objectId_noencrypt);

		// 通知但
		ColumnsInfo state = TemplateLayoutBo.getColumnsInfo("state", "state",
				"A", 100);
		state.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnTmp.add(state);

		String name = "";
		boolean flag = false;//单位合并或划转
		String columnName = "姓名";
		if (dataBo.getParamBo().getInfor_type() == 2
				|| dataBo.getParamBo().getInfor_type() == 3) {//单位名称
			columnName = "名称";
			if (dataBo.getParamBo().getOperationType() == 8
					|| dataBo.getParamBo().getOperationType() == 9) {
				flag = true;
				ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo("to_id",
						"组号", "A", 40);
				cname.setRendererFunc("templateCard_me.renderGroupColumn");
				columnTmp.add(cname);
			}
			if (dataBo.getParamBo().getOperationType() == 5) {
				name = "codeitemdesc_2";
			} else {
				name = "codeitemdesc_1";
			}
		}
		if (dataBo.getParamBo().getInfor_type() == 1) {
			DbWizard dbWizard = new DbWizard(this.frameconn);

			if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
				if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
					name = "a0101_2";
				}
			} else {
				name = "a0101_1";
			}
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A", 160);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			columnTmp.add(cname);
		} else if (dataBo.getParamBo().getInfor_type() == 2) {
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A",  160);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			columnTmp.add(cname);
		} else if (dataBo.getParamBo().getInfor_type() == 3) {
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A", 160);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			cname.setCodesetId("");
			columnTmp.add(cname);
		}

		/*
		if((dataBo.getParamBo().getInfor_type() == 2||dataBo.getParamBo().getInfor_type() == 3)&&(dataBo.getParamBo().getOperationType()==8||dataBo.getParamBo().getOperationType()==9))
		{
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo("to_id",
					"to_id", "A", 200); 
			cname.setCodesetId("");
			cname.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
			columnTmp.add(cname);
		}*/
		
		return columnTmp;
	}
	/**
	 * 栏目设置设置按单位部门岗位排序与机构表顺序一致，重组查询sql和排序语句。
	 * columns:栏目设置中的全部指标
	 * sortSql:原排序语句
	 * sql:原查询sql
	 * **/
	private void rebuildSearchSql(ArrayList columns,TemplateParam paramBo,String sortSql,TableDataConfigCache tableCache,String sql){
    	ArrayList list=new ArrayList();//记录与机构表关联查询排序的指标
	 		for(int num=0;num<columns.size();num++)
	 		{
	 			String orderByColumnid=null;//记录按照哪个字段与机构表关联查询排序
	 			ColumnsInfo column = (ColumnsInfo)columns.get(num);
	 			String columnId = column.getColumnId();
	 			if(StringUtils.isBlank(columnId))
     				continue;
	 			if(paramBo.getInfor_type() == 1&&(columnId.toLowerCase().indexOf("b0110")!=-1||columnId.toLowerCase().indexOf("e0122")!=-1||columnId.toLowerCase().indexOf("e01a1")!=-1)&& sortSql.indexOf(columnId) != -1)
	 			{
	 				if(columnId.toLowerCase().indexOf("b0110")!=-1){
	 					orderByColumnid=columnId;
	 				}else if(columnId.toLowerCase().toLowerCase().indexOf("e0122")!=-1){
	 					orderByColumnid=columnId;
	 				}else if(columnId.toLowerCase().toLowerCase().indexOf("e01a1")!=-1){
	 					orderByColumnid=columnId;
	 				}
	 			}else if((paramBo.getInfor_type() == 2||paramBo.getInfor_type() == 3)&&columnId.toLowerCase().indexOf("codeitemdesc")!=-1&& sortSql.indexOf(columnId) != -1){
	 				orderByColumnid = "b0110";
	                if (paramBo.getInfor_type() == 3)
	                	orderByColumnid = "e01a1";
	 			}
	 			if(orderByColumnid!=null)
	 				list.add(orderByColumnid);
	 		}
	 		//重组查询sql
	 		if(list.size()>0){
				String tableSql=" select temp.* from ("+sql+") temp ";
					String fieldStr="";
					String joinStr="";
	     		for(int num=0;num<list.size();num++){
	     			fieldStr+=",org"+num+".a0000 org"+num+"a0000";
	     			joinStr+=" left join organization org"+num+" on temp."+list.get(num)+"=org"+num+".codeitemid ";
	     		}
	     		tableSql=tableSql.replace("temp.*","temp.*"+fieldStr)+joinStr;
	     		tableCache.setTableSql(tableSql);
			}
    	
    }
}
