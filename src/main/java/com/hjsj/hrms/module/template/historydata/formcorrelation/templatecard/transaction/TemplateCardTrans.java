package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 卡片显示页面
* @Title: TemplateCardTrans
* @Description:
* @author: hej
* @date 2019年11月19日 下午4:41:40
* @version
 */
public class TemplateCardTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			HashMap formMap = this.getFormHM();
			TemplateFrontProperty frontProperty = new TemplateFrontProperty(formMap);
			String returnFlag = frontProperty.getReturnFlag();
			String tabId = frontProperty.getTabId();
			String taskId = frontProperty.getTaskId();
			String cur_object_id = TemplateFuncBo.getValueFromMap(formMap,
					"cur_object_id");//当前定位的人 回传回去
			
			String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
			//linbz 查询条件
			String filterStr = frontProperty.getOtherParam("search_sql");
			String record_id = frontProperty.getOtherParam("record_id");
			String archive_id = frontProperty.getOtherParam("archive_id");
			String archive_year = frontProperty.getOtherParam("archive_year");
			String taskid_validate = PubFunc.decrypt(frontProperty.getOtherParam("taskid_validate"));
			//是否显示左侧人员列表 是否自助  当前模板类型
			TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,this.userView, Integer.parseInt(tabId),archive_id);
			if(taskid_validate!=null&&!"".equals(taskid_validate)&&taskId.equals(taskid_validate.split("_")[0]))
		    	dataBo.getParamBo().setNeedJudgPre("0");
			/**获取左侧人员导航-----------------*/
			// 获取列头 
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			columnsInfo = getGridColumnList(dataBo);
			StringBuffer orderBy = new StringBuffer();
			String sql = dataBo.getArchiveSql(record_id,archive_year);
			
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
			PubFunc.closeDbObj(rowset);
			this.getFormHM().put("hidePersonGrid", hidePersonGrid);
			//  加载表格 
			TableConfigBuilder builder = new TableConfigBuilder("templatecard",columnsInfo, "templatecard", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy(orderBy.toString());
			builder.setPageSize(20);
			builder.setSelectable(true);
			builder.setSetScheme(false);
			builder.setModuleId("templatecard");
			builder.setColumnFilter(false);
			builder.setSortable(false);
            HashMap map = new HashMap();
            map.put("taskId", taskId);
            map.put("search_sql", filterStr);
            builder.setCustomParamHM(map);
            
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config);
			if (cur_object_id.length() > 0) {
				this.getFormHM().put("objectId", cur_object_id);
			}
			/** end 获取左侧人员导航-----------------*/
			dataBo.getParamBo().setReturnFlag(returnFlag);//liuyz 我的申请不需要去获取节点权限
			TemplateCardBo cardBo = new TemplateCardBo(this.frameconn, this.userView, dataBo.getParamBo());

			String selfapply = "0";
			if (frontProperty.isSelfApply()) {
				selfapply = "1";
			}
			cardBo.setSelfApply(selfapply);
			cardBo.setApproveFlag(frontProperty.getApproveFlag());
			cardBo.setTask_id(taskId);
			/**获取页数-----------------*/
			ArrayList outlist = getPageList(Integer.parseInt(tabId), false, noShowPageNo,archive_id);
			ArrayList pageList = new ArrayList();
			String title="";
			for (int i = 0; i < outlist.size(); i++) {
				TemplatePage pagebo = (TemplatePage) outlist.get(i);
				//判断此页的指标无读写权限。无读写权限指标的不显示
				if (!cardBo.isHaveReadFieldPriv(pagebo.getPageId() + "")) {//
					continue;
				}
				//liuyz 27387 解决页签分离给标题套一层div设置最小宽度和js中设置的minTabWidth相同
				if(outlist.size()>7) {//页签超出7个 纵向展现页签 页签title样式修改
					title="<div style=\"width:80px;\"><table style=\"table-layout:fixed;width:80px;\">"
							+ "<tr><td style=\"word-wrap:break-word;text-align:left;"
							+ "white-space: normal;line-height: normal;font-weight: normal !improtant;"
							+ "font-size: 12px;font-style: normal;\" class=\"x-tab-inner-default\"><div>"+pagebo.getTitle()+"</div></td></tr></table></div>";
				}else
					title="<div style='min-width:86px'>"+pagebo.getTitle()+"</div>";
				LazyDynaBean lazyvo = new LazyDynaBean();
				lazyvo.set("pageId", pagebo.getPageId());
				lazyvo.set("title", title);//liuyz 优化 页签加只读和编辑图标
				pageList.add(lazyvo);
			}
			this.getFormHM().put("pageList", pageList);
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
			}
			
			/**签章厂家标识  end    **/
			/** end 获取页数-----------------*/

			//获取所有模板页指标模型
			ArrayList fieldList = cardBo.getFieldList();
			this.getFormHM().put("fieldList", fieldList);
			
            this.getFormHM().put("nodePriv","0");
            String node_id = "";
            this.getFormHM().put("node_id",StringUtils.isBlank(node_id)?"-1":node_id);
            
            /** linbz 卡片增加查询控件，取得复杂查询下拉中的字段* */
            ArrayList fieldsMap = new ArrayList();
            ArrayList fieldsArray =new ArrayList();
            this.getFormHM().put("fieldsArray", fieldsArray);
            this.getFormHM().put("fieldsMap", fieldsMap);
            String opinion_field = dataBo.getParamBo().getOpinion_field();//liuyz bug31563
            this.getFormHM().put("opinion_field", opinion_field==null?"":opinion_field);
            
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
	 * @param archive_id 
	 * @param @return
	 * @param @throws Exception
	 * @return ArrayList
	 */
	public ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo, String archive_id) throws Exception {
		ArrayList outlist = new ArrayList();
		try {
			TemplateUtilBo utilBo = new TemplateUtilBo(frameconn, userView);
			ArrayList list = utilBo.getAllArchiveTemplatePage(tabId,archive_id,-1);
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
	public ArrayList<ColumnsInfo> getGridColumnList(TemplateDataBo dataBo) {
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
			if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
				name = "a0101_2";
			} else {
				name = "a0101_1";
			}
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A", 160);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			columnTmp.add(cname);
		} else if (dataBo.getParamBo().getInfor_type() == 2) {
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A", flag ? 160 : 200);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			columnTmp.add(cname);
		} else if (dataBo.getParamBo().getInfor_type() == 3) {
			ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo(name,
					columnName, "A", 200);
			cname.setRendererFunc("templateCard_me.renderPersonColumn");
			cname.setCodesetId("");
			columnTmp.add(cname);
		}
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
