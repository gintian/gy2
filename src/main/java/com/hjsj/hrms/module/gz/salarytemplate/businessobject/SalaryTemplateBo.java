package com.hjsj.hrms.module.gz.salarytemplate.businessobject;

import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 薪资发放、薪资审批、薪资上报 工具类
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 */
public class SalaryTemplateBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public SalaryTemplateBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	
	/**
	 * 获取列头、表格渲染
	 * @param viewtype 页面区分 0:薪资发放  1:审批  2:上报
	 * @param imodule 0:薪资  1:保险
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumnList(String viewtype, String imodule){
		
		/** 获取类型名称 */
		String str = ResourceFactory.getProperty("gz.report.salary");//薪资
		if ("1".equals(imodule)){
			str = ResourceFactory.getProperty("gz.report.welfare");//保险
		}
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		
		/** 薪资发放 */
		if ("0".equals(viewtype)) {

			/**
			 * [26010]
			 * 修改时间：2017-04-05 15:16
			 * 修改人:sunjian
			 * 修改内容:noComparisonColumn=True:薪资发放&薪资审批列表页面，不显示“业务日期”、“发放次数”列，其他列都显示，若为false或者空时，显示全部列
			 */
			// 编号
			columnTmp.add(getColumnsInfo("salaryid", ResourceFactory.getProperty("report.number"), 50));
			// 薪资类别(有点击事件)
			ColumnsInfo cname = getColumnsInfo("cname", ResourceFactory.getProperty("gz_new.gz_accounting.type"), 350);//类别
			cname.setRendererFunc("salarypay_me.cnameForPay");
			columnTmp.add(cname);
			if (StringUtils.isBlank(SystemConfig.getPropertyValue("noComparisonColumn")) || "false".equalsIgnoreCase(SystemConfig.getPropertyValue("noComparisonColumn"))) {
				// 业务日期
				columnTmp.add(getColumnsInfo("appdate", ResourceFactory.getProperty("label.gz.appdate"), 80));//业务日期
				// 发放次数
				ColumnsInfo count = getColumnsInfo("count", ResourceFactory.getProperty("label.gz.count"), 70);
				count.setTextAlign("center");
				columnTmp.add(count);
			}
			// 变动比对
			if (("0".equals(imodule) && "0".equals(viewtype) && this.userview.hasTheFunction("3240201")) || ("1".equals(imodule) && "0".equals(viewtype) && this.userview.hasTheFunction("3250201"))) {
				ColumnsInfo compare = getColumnsInfo("compare", ResourceFactory.getProperty("label.gz.changeinfo"), 100);
				compare.setRendererFunc("salarypay_me.compareForPay");
				compare.setTextAlign("center");
				columnTmp.add(compare);
			}
			// 业务处理
			ColumnsInfo dealto = getColumnsInfo("dealto", ResourceFactory.getProperty("label.gz.operation"), 70);
			dealto.setRendererFunc("salarypay_me.dealtoForPay");
			dealto.setTextAlign("center");
			columnTmp.add(dealto);

			//历史数据
			ColumnsInfo historyView = getColumnsInfo("historyView", "历史数据", 70);
			historyView.setRendererFunc("salarypay_me.historyViewForApprov");
			historyView.setTextAlign("center");
			columnTmp.add(historyView);

			/** 隐藏 */
			// 提成工资是否启用
			ColumnsInfo royalty_valid = getColumnsInfo("royalty_valid", "is_royalty_valid", 100);
			royalty_valid.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(royalty_valid);
			// 编号
			ColumnsInfo salaryid_safe = getColumnsInfo("salaryid_safe", ResourceFactory.getProperty("report.number") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			salaryid_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(salaryid_safe);
			// 业务日期
			ColumnsInfo appdate_safe = getColumnsInfo("appdate_safe", ResourceFactory.getProperty("label.gz.appdate") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);//业务日期
			appdate_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(appdate_safe);
			// 发放次数
			ColumnsInfo count_safe = getColumnsInfo("count_safe", ResourceFactory.getProperty("label.gz.count") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			count_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(count_safe);
			// 页面区分
			ColumnsInfo viewtype_safe = getColumnsInfo("viewtype_safe", "viewtype" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			viewtype_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(viewtype_safe);
			// 薪资保险区分
			ColumnsInfo imodule_safe = getColumnsInfo("imodule_safe", "imodule" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			imodule_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(imodule_safe);
			// 应用机构的填报时间
			ColumnsInfo tip = getColumnsInfo("tip", "tip" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			tip.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(tip);
			//不可以进入的根据这个标识识别
			ColumnsInfo canComing = getColumnsInfo("can_coming", "canComing" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			canComing.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(canComing);

			// 是否共享用户   0：否或者管理员 1：是
			ColumnsInfo manager = getColumnsInfo("manager", "是否共享用户", 0);
			manager.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(manager);
		}
		/** 薪资审批 */
		else if ("1".equals(viewtype)){
			/** 显示 */
			// 编号
			columnTmp.add(getColumnsInfo("salaryid", ResourceFactory.getProperty("report.number"), 50));
			// 薪资类别(有点击事件)
			ColumnsInfo cname = getColumnsInfo("cname", ResourceFactory.getProperty("gz_new.gz_accounting.type"), 350);//类别
			cname.setRendererFunc("salarypay_me.cnameForApprov");
			columnTmp.add(cname);
			// 操作
			ColumnsInfo operation = getColumnsInfo("operation", ResourceFactory.getProperty("reportcyclelist.option"), 150);
			operation.setRendererFunc("salarypay_me.operationForApprov");
			operation.setTextAlign("center");
			columnTmp.add(operation);
			
			/** 隐藏 */
			// 薪资类别的链接,是否汇总审批 搜房网需求
			ColumnsInfo collectPoint = getColumnsInfo("collectpoint", "is" + ResourceFactory.getProperty("report_collect.collect"), 100);
			collectPoint.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(collectPoint);
			// 操作中显示的内容 :审批/查看
			ColumnsInfo isCurr_user = getColumnsInfo("iscurr_user", ResourceFactory.getProperty("gz_new.gz_accounting.approveoperation") + "/" + ResourceFactory.getProperty("label.view"), 100);
			isCurr_user.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(isCurr_user);
			// 编号
			ColumnsInfo salaryid_safe = getColumnsInfo("salaryid_safe", ResourceFactory.getProperty("report.number") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			salaryid_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(salaryid_safe);
			// 页面区分
			ColumnsInfo viewtype_safe = getColumnsInfo("viewtype_safe", "viewtype" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			viewtype_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(viewtype_safe);
			// 薪资保险区分
			ColumnsInfo imodule_safe = getColumnsInfo("imodule_safe", "imodule" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			imodule_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(imodule_safe);
		}
		/** 薪资上报 */
		else if ("2".equals(viewtype)){
			/** 显示 */
			// 编号
			columnTmp.add(getColumnsInfo("salaryid", ResourceFactory.getProperty("report.number"), 50));
			// 薪资类别(有点击事件)
			ColumnsInfo cname = getColumnsInfo("cname", ResourceFactory.getProperty("gz_new.gz_accounting.type"), 350);//类别

			cname.setRendererFunc("salarypay_me.cnameForUp");
			columnTmp.add(cname);
			// 业务日期
			columnTmp.add(getColumnsInfo("appdate", ResourceFactory.getProperty("label.gz.appdate"), 80));
			// 发放次数
			ColumnsInfo count = getColumnsInfo("count", ResourceFactory.getProperty("label.gz.count"), 70);
			count.setTextAlign("center");
			columnTmp.add(count);
			// 业务处理
			ColumnsInfo dealto = getColumnsInfo("dealto", ResourceFactory.getProperty("label.gz.operation"), 70);
			dealto.setRendererFunc("salarypay_me.dealtoForUp");
			dealto.setTextAlign("center");
			columnTmp.add(dealto);

			//历史数据
			ColumnsInfo historyView = getColumnsInfo("historyView", ResourceFactory.getProperty("gz_new.gz_accounting.historyData"), 70);
			historyView.setRendererFunc("salarypay_me.historyViewForApprov");
			historyView.setTextAlign("center");
			columnTmp.add(historyView);
			
			/** 隐藏 */
			// 编号
			ColumnsInfo salaryid_safe = getColumnsInfo("salaryid_safe", ResourceFactory.getProperty("report.number") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			salaryid_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(salaryid_safe);
			// 页面区分
			ColumnsInfo viewtype_safe = getColumnsInfo("viewtype_safe", "viewtype" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			viewtype_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(viewtype_safe);
			// 业务日期
			ColumnsInfo appdate_safe = getColumnsInfo("appdate_safe", ResourceFactory.getProperty("label.gz.appdate") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);//业务日期
			appdate_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(appdate_safe);
			// 发放次数
			ColumnsInfo count_safe = getColumnsInfo("count_safe", ResourceFactory.getProperty("label.gz.count") + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			count_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(count_safe);
			// 薪资保险区分
			ColumnsInfo imodule_safe = getColumnsInfo("imodule_safe", "imodule" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			imodule_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(imodule_safe);
			// 是否共享用户   0：否或者管理员 1：是
			ColumnsInfo manager = getColumnsInfo("manager", "是否共享用户", 0);
			manager.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(manager);
			// 应用机构的填报时间
			ColumnsInfo tip = getColumnsInfo("tip", "imodule" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			tip.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(tip);
			//不可以进入的根据这个标识识别
			ColumnsInfo canComing = getColumnsInfo("can_coming", "canComing" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
			canComing.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(canComing);
		}
		
		return columnTmp;
	}
	/**
	 * 取得权限范围的薪资列表 列表中存放是的LazyBean
	 * @param viewtype 页面区分 0:薪资发放  1:审批  2:上报
	 * @param imodule  0:薪资  1:保险 
	 * @param valuesList 快速查询检索条件
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<LazyDynaBean> getDataList(String viewtype, String imodule, ArrayList<String> valuesList) throws GeneralException{ 
		
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet salarytemplateRs = null;
		RowSet salaryhistoryRs = null;
		try {
			StringBuffer buf = new StringBuffer();
			ArrayList<String> sqlList = new ArrayList<String>();
		 	buf.append("select salaryid,cname,cbase,seq,cond from salarytemplate "); 
		 	if("2".equals(viewtype)){//数据上报
		 		buf.append(" where 1=1 ");
		 	}else if ("0".equals(imodule)){// 薪资类别
				buf.append(" where (cstate is null or cstate='')");
			}else {
				buf.append(" where cstate='1'");// 险种类别
			}
			// 快速查询
			for(int i = 0; i < valuesList.size(); i++){
				String queryVal = valuesList.get(i);
				queryVal = SafeCode.decode(queryVal);
				if(this.isInteger(queryVal)){
					if(i == 0){
						buf.append(" and (");
					}else{
						buf.append(" or ");
					}
					buf.append("(salaryid=? or cname like ?)");
					sqlList.add(queryVal);
					sqlList.add("%"+queryVal+"%");
				}else{
					if(i == 0){
						buf.append(" and (");
					}else{
						buf.append(" or ");
					}
					buf.append("cname like ?");
					sqlList.add("%"+queryVal+"%");
				}
				
				if(i == valuesList.size()-1) {
					buf.append(")");
				}
			}
			buf.append(" order by seq");
			salarytemplateRs = dao.search(buf.toString(), sqlList);
			
			SalaryCtrlParamBo salaryCtrlParamBo=new SalaryCtrlParamBo();
			salaryCtrlParamBo.setConn(conn);
			if(SalaryCtrlParamBo.docMap.size()==0){//若没有加载过，则直接加载全部数据，
				StringBuilder strSalary=new StringBuilder();
				while(salarytemplateRs.next()){
					// 加上权限过滤 
					if("2".equals(viewtype)){
						if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid"))&&!this.userview.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}
					else if ("0".equals(imodule)){
						if (!this.userview.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}else {
						if (!this.userview.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
							continue;
					}
					
					strSalary.append(","+salarytemplateRs.getString("salaryid"));
				}
				salarytemplateRs.beforeFirst();
				
				if(StringUtils.isNotBlank(strSalary.toString()))
					salaryCtrlParamBo.initAllSalaryCtrlParam(strSalary.deleteCharAt(0).toString());
			}
			HashMap newSalarySetMap=getNewStructSalaryset("2".equals(viewtype)?"":imodule);
			HashMap<String,HashMap> salaryAppdateMap=new HashMap<String, HashMap>();
			HashMap<String,String> dateMap=new HashMap<String, String>();
			while(salarytemplateRs.next()){
				// 加上权限过滤 
				if("2".equals(viewtype)){
					if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid"))&&!this.userview.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
						continue;
				}
				else if ("0".equals(imodule)){
					if (!this.userview.isHaveResource(IResourceConstant.GZ_SET, salarytemplateRs.getString("salaryid")))
						continue;
				}else {
					if (!this.userview.isHaveResource(IResourceConstant.INS_SET, salarytemplateRs.getString("salaryid")))
						continue;
				}
				LazyDynaBean lazyvo = new LazyDynaBean();
		//		com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo salaryTemplateBo = new com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo(this.conn,Integer.parseInt(salarytemplateRs.getString("salaryid")), this.userview);
				Document doc=null;
				if(SalaryCtrlParamBo.docMap.get(salarytemplateRs.getString("salaryid"))==null)//当薪资账套有6-700个时，需从内存中取属性来提高效率
				{
					com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo salaryTemplateBo = new com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo(this.conn,Integer.parseInt(salarytemplateRs.getString("salaryid")), this.userview);
					doc=salaryTemplateBo.getCtrlparam().getDoc1();
				}
				else
					doc=(Document)SalaryCtrlParamBo.docMap.get(salarytemplateRs.getString("salaryid"));
				String  royalty_valid = getValue(SalaryCtrlParamBo.ROYALTIES,"valid",doc); // 提成工资是否启用 1:启用 0:没有启用
				if (royalty_valid == null)
					royalty_valid = "0";
				lazyvo.set("royalty_valid", royalty_valid);
				String salaryid = salarytemplateRs.getString("salaryid");
				lazyvo.set("salaryid", salaryid);
				lazyvo.set("salaryid_safe", SafeCode.encode(PubFunc.encrypt(salaryid)));
				lazyvo.set("seq", salarytemplateRs.getString("seq") != null ? salarytemplateRs.getString("seq") : "0");//<TODO>
				String cname = salarytemplateRs.getString("cname"); 
				String cond = Sql_switcher.readMemo(salarytemplateRs, "cond");//<TODO>
				String cbase = salarytemplateRs.getString("cbase");
				// 对条件进行转换,转成用户可阅读的格式 
				lazyvo.set("domain", "["+cbase+"]:["+cond+"]");
			//	SalaryCtrlParamBo ctrlparam = null;
			//	ctrlparam = salaryTemplateBo.getCtrlparam(); // 参数查询
				String manager = getValue(SalaryCtrlParamBo.SHARE_SET, "user",doc); // 工资管理员，对共享类别有效
				String flowCtrlFlag = getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag",doc); //审批流程控制
				// 薪资审批时,操作栏显示"审批"/"查看"
				if ("1".equals(viewtype)){
					
					if (!"1".equalsIgnoreCase(flowCtrlFlag)){
						continue;
					}
					String collectPoint = getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field",doc);
					if (collectPoint != null && collectPoint.length() > 0){//薪资汇总审批 搜房网需求 
//						collectPoint = "/gz/gz_sp_collect/gz_sp_collect.do";
						collectPoint = "1";
					}else {
//						collectPoint = "/gz/gz_accounting/gz_sp_orgtree.do";
						collectPoint = "0";
					}
					String sql = "select curr_user from salaryhistory where salaryid=? and curr_user=?";
					sqlList.clear();
					sqlList.add(salaryid);
					sqlList.add(this.userview.getUserName());
					salaryhistoryRs = dao.search(sql, sqlList);
					if (salaryhistoryRs.next()){
						lazyvo.set("iscurr_user", ResourceFactory.getProperty("gz_new.gz_accounting.approveoperation"));
						lazyvo.set("collectpoint", collectPoint);
					}else {
						lazyvo.set("iscurr_user", ResourceFactory.getProperty("label.view"));
						lazyvo.set("collectpoint", collectPoint);
					}
				}

				if ("0".equals(viewtype)){// 薪资发放显示驳回状态
					if ("1".equalsIgnoreCase(flowCtrlFlag)){ // 审批流程控制
						String state_str = searchIsBackState(manager, salarytemplateRs.getString("salaryid"));
						if (state_str != null&&state_str.trim().length() > 0)
							cname += " (" + state_str + ")";
					}
					
				}
				
				if("0".equals(viewtype) || "2".equals(viewtype)) {
					String start_date = getValue(SalaryCtrlParamBo.FILLING_AGENCYS, "start_date",doc); // 工资管理员，对共享类别有效
					String end_date = getValue(SalaryCtrlParamBo.FILLING_AGENCYS, "end_date",doc); // 工资管理员，对共享类别有效
					lazyvo.set("start_date", start_date);
					lazyvo.set("end_date", end_date);
				}
				lazyvo.set("cname", cname);
				
				/** 自动升级薪资类别 */
				syncSalaryStruct(salarytemplateRs.getInt("salaryid"),newSalarySetMap);
				if (!"1".equals(viewtype)){ // 薪资审批时不显示发放日期、发放次数
					LazyDynaBean abean = null;
					if (manager.length() == 0){ // 不共享
						if ("2".equals(viewtype)){ // 数据上报
							continue;
						}else {
							dateMap=new HashMap<String, String>();
							dateMap.put("salaryid", salarytemplateRs.getString("salaryid"));
							dateMap.put("username", this.userview.getUserName());
							dateMap.put("a00z2","");
							dateMap.put("a00z3","");
							salaryAppdateMap.put(salarytemplateRs.getString("salaryid"), dateMap);
							//abean = searchBusinessDate(salarytemplateRs.getString("salaryid"),this.userview.getUserName());
						}
						lazyvo.set("manager", "0");
					}else {// 共享
							dateMap=new HashMap<String, String>();
							dateMap.put("salaryid", salarytemplateRs.getString("salaryid"));
							dateMap.put("username", manager);
							dateMap.put("a00z2","");
							dateMap.put("a00z3","");
							salaryAppdateMap.put(salarytemplateRs.getString("salaryid"), dateMap);
						// abean = searchBusinessDate(salarytemplateRs.getString("salaryid"),manager);
						 if(manager.equalsIgnoreCase(this.userview.getUserName())){
							 lazyvo.set("manager", "0");
						 }else{
							 lazyvo.set("manager", "1");
						 }
					}
				}
				lazyvo.set("viewtype_safe", SafeCode.encode(PubFunc.encrypt(viewtype)));
				lazyvo.set("imodule_safe", SafeCode.encode(PubFunc.encrypt(imodule)));
				list.add(lazyvo);
			}
			if(salaryAppdateMap.size()>0){
				this.getAppdate(salaryAppdateMap);
				
				for(LazyDynaBean bean :list){
					String salaryid=bean.get("salaryid").toString();
					
					HashMap map=salaryAppdateMap.get(salaryid);
					String date=map.get("a00z2").toString();
					if(date.length()>7)
						date=date.substring(0, 7);
					bean.set("appdate", date);
					bean.set("appdate_safe", SafeCode.encode(PubFunc.encrypt(map.get("a00z2").toString())));
					bean.set("count", map.get("a00z3").toString());
					bean.set("count_safe", SafeCode.encode(PubFunc.encrypt(map.get("a00z3").toString())));
					
					String a00z2 = map.get("a00z2").toString();
					String tip = "";
					ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.conn,salaryid,this.userview);
					ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn,this.userview,imodule,salaryid);
					boolean canComing = true;
					String start_date = "";
					String end_date = "";
					if("0".equals(viewtype)) {//薪资发放
						String sql = "select min(start_date) as start_date,max(end_date) as end_date,max(enable) as enable  from gz_reporting_log where salaryid=? and username=? and a00z2 =" + Sql_switcher.dateValue(a00z2) + " group by username";
						sqlList.clear();
						sqlList.add(salaryid);
						sqlList.add(this.userview.getUserName());
						salaryhistoryRs = dao.search(sql, sqlList);
						if(salaryhistoryRs.next()) {
							start_date = salaryhistoryRs.getString("start_date");
							end_date = salaryhistoryRs.getString("end_date");
							String enable = salaryhistoryRs.getString("enable");
							String infoMessage = aorgbo.getErrorMess(Integer.parseInt(start_date), Integer.parseInt(end_date), enable);
							if(StringUtils.isNotBlank(infoMessage)) {
								canComing = false;
							}
							tip = " 【" + ResourceFactory.getProperty("reportcyclelist.adddate") +"：" + start_date + ResourceFactory.getProperty("datestyle.day") + 
									"-" + end_date + ResourceFactory.getProperty("datestyle.day") + "】";
						}
						
						ArrayList<String> gzReportingDataList = processMonitorBo.getGzReportingData("3");
						start_date = (String) bean.get("start_date"); // 工资管理员，对共享类别有效
						end_date = (String) bean.get("end_date"); // 工资管理员，对共享类别有效
						if(StringUtils.isBlank(tip) && gzReportingDataList.size() > 0) {
				    		for(int i = 0; i < gzReportingDataList.size(); i++) {
				    			String[] reportArray = gzReportingDataList.get(i).split("\\|");
				    			if(reportArray.length > 1) {
					    			String enable = gzReportingDataList.get(i).split("\\|")[0];
					    			String username = gzReportingDataList.get(i).split("\\|")[1];
					    			if(this.userview.getUserName().equalsIgnoreCase(username)) {
					    				String info = aorgbo.getErrorMess(Integer.parseInt(start_date), Integer.parseInt(end_date),enable);
					    				if(StringUtils.isNotBlank(info)) {
					    					canComing = false;
					    				}else {
					    					canComing = true;
					    					break;
					    				}
					    			}
				    			}
				    		}
				    	}
						//只针对应用机构有机构的显示提示，如果没有机构的，只有填报日期的也不显示了，没什么意义
						if(StringUtils.isBlank(tip) && StringUtils.isNotBlank(start_date) && gzReportingDataList.size() > 0) {//填报日期：从XXXX到XXX
							tip = " 【" + ResourceFactory.getProperty("reportcyclelist.adddate") +"："+ start_date + ResourceFactory.getProperty("datestyle.day") + 
									"-" + end_date + ResourceFactory.getProperty("datestyle.day") + "】";
						}
					}else if("2".equals(viewtype)){//数据上报
						
						String username = this.userview.getUserName();
						if(this.userview.getStatus() == 0) {//如果是数据上报，而且是业务用户，存入对应的自助自助用户名
							ArrayList list_self = PubFunc.SearchOperUserOrSelfUserName(userview);
							if(list_self.size() > 1) {
								this.userview.getHm().put("selfUsername", PubFunc.SearchOperUserOrSelfUserName(userview).get(1));
								username = (String)PubFunc.SearchOperUserOrSelfUserName(userview).get(1);
							}
						}
						
						String sql = "select min(start_date) as start_date,max(end_date) as end_date,max(enable) as enable from gz_reporting_log where salaryid=? and username=? and a00z2 =" + Sql_switcher.dateValue(a00z2) + " group by username";
						sqlList.clear();
						sqlList.add(salaryid);
						sqlList.add(username);
						salaryhistoryRs = dao.search(sql, sqlList);
						if(salaryhistoryRs.next()) {
							start_date = salaryhistoryRs.getString("start_date");
							end_date = salaryhistoryRs.getString("end_date");
							String enable = salaryhistoryRs.getString("enable");
							String infoMessage = aorgbo.getErrorMess(Integer.parseInt(start_date), Integer.parseInt(end_date), enable);
							if(StringUtils.isNotBlank(infoMessage)) {
								canComing = false;
							}
							tip = " 【" + ResourceFactory.getProperty("reportcyclelist.adddate") +"：" + start_date + ResourceFactory.getProperty("datestyle.day") + 
									"-" + end_date + ResourceFactory.getProperty("datestyle.day") + "】";
						}
						//如果tip为空，但是设置了start_date，并且有机构的情况下，这样就是管理员还没有下发数据
						salaryCtrlParamBo.setDoc((Document)SalaryCtrlParamBo.docMap.get(salaryid));
						List elementList = salaryCtrlParamBo.getValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS);
						if(StringUtils.isBlank(tip) && elementList.size() > 0) {
							String start_dates = (String) bean.get("start_date"); // 工资管理员，对共享类别有效
							if(StringUtils.isNotBlank(start_dates)) {//填报日期：从XXXX到XXX
								canComing = false;
							}
						}
					}
					bean.set("can_coming", canComing);
					bean.set("tip", tip);
				}
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(salarytemplateRs);
			PubFunc.closeDbObj(salaryhistoryRs);
		}
		
		return list;
	}
	
	/**
	 * 批量查询薪资账套业务日期
	 * 
	 * 1、发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 2、若发放记录表中没有未结束状态的记录，则判断薪资发放临时表中是否有记录。
	 * 3、若以上都没有，则根据发放记录表中最大日期记录的业务日期确定
	 * @param salaryAppdateMap
	 * @throws GeneralException
	 * @author zhanghua
	 * @date 2017年7月17日 下午1:24:05
	 */
	private void getAppdate(HashMap<String,HashMap> salaryAppdateMap) throws GeneralException{
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			Iterator iter = salaryAppdateMap.entrySet().iterator();
			RowSet rs=null;
			StringBuilder strWhere=new StringBuilder();
			//查询发放记录表中是否有未结束的记录。
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				HashMap map = (HashMap) entry.getValue();
				strWhere.append("or (salaryid="+map.get("salaryid").toString());
				strWhere.append(" and upper(username)='"+map.get("username").toString().toUpperCase()+"'");
				strWhere.append(") ");
			}
			
			String strSql="select salaryid,a00z2,a00z3 from gz_extend_log where sp_flag<>'06' and (";
			strSql+=strWhere.delete(0, 2);
			strSql+=")";
			rs=dao.search(strSql);
			
			while(rs.next()){
				String salaryid=rs.getString("salaryid");
				HashMap map=salaryAppdateMap.get(salaryid);
				map.put("a00z2", PubFunc.FormatDate(rs.getDate("A00z2"),"yyyy-MM-dd"));
				map.put("a00z3", rs.getString("a00z3"));
				
			}
			
			
			//查询发放临时表中是否有未结束的记录。
			iter=salaryAppdateMap.entrySet().iterator();
			StringBuilder strTable=new StringBuilder();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				HashMap map = (HashMap) entry.getValue();
				
				if(StringUtils.isNotBlank(map.get("a00z2").toString()))//若已经写入过业务日期，则不更新。
					continue;
				String tableName=map.get("username")+"_salary_"+map.get("salaryid");
				
				strTable.append(",'"+tableName.toUpperCase()+"'");
			}
			if(StringUtils.isBlank(strTable.toString()))
				return;
			if(Sql_switcher.searchDbServer()==2)//去掉判断表空间，表空间是建立的时候可以指定的而this.conn.getMetaData().getUserName()是获取数据库用户名，两个不一定相等
				strSql="select table_name as name from user_tables where upper(table_name) in ("+strTable.delete(0, 1)+")";
			else
				strSql=" select name from sysobjects where upper(name) in ("+strTable.delete(0, 1)+")";
			rs=dao.search(strSql);
			while(rs.next()){
				String tableName=rs.getString("name");
				String salaryid=tableName.toLowerCase().split("_salary_")[1];//因为可能出现XX_YY这种用户名的，如果仅仅截取split("_")[2]是不对的，所有截取临时表的地方都得注意
				if(Sql_switcher.searchDbServer()==2)
					strSql="select  a00z2,a00z3 from "+tableName+" where rownum < 2 "; 
				else 
					strSql="select  top 1 a00z2,a00z3 from "+tableName;
				RowSet rs1 = dao.search(strSql, new ArrayList());
				if (rs1.next()) {
					if (rs1.getDate("A00z2") != null) {
						HashMap map=salaryAppdateMap.get(salaryid);
						map.put("a00z2",PubFunc.FormatDate(rs1.getDate("A00z2"),"yyyy-MM-dd"));
						map.put("a00z3", rs1.getString("A00Z3")); 
	
					}
				}
			}
			
			//查询发放记录表中最大业务日期的记录。
			strWhere.setLength(0);
			iter=salaryAppdateMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				HashMap map = (HashMap) entry.getValue();
				if(StringUtils.isNotBlank(map.get("a00z2").toString()))
					continue;
				strWhere.append("or ( salaryid="+map.get("salaryid").toString());
				strWhere.append(" and upper(username)='"+map.get("username").toString().toUpperCase()+"'");
				strWhere.append(") ");
			}
			if(strWhere.length()==0)//若已经写入过业务日期，则不更新。
				return;
			
			strSql=" select b.salaryid, max(b.a00z2) as a00z2 ,max(b.username) as username,max(b.a00z3) as a00z3 from ";
			strSql+= "(select salaryid, max(a00z2) as a00z2 ,max(username) as username from gz_extend_log where (";
			strSql+=strWhere.delete(0, 2);
			strSql+=" ) group by salaryid ) a ";
			strSql+=" inner join gz_extend_log b on a.SalaryID=b.SalaryID and a.a00z2=b.A00Z2 and a.username=b.username group by b.salaryid ";
			rs=dao.search(strSql);
			
			while(rs.next()){
				String salaryid=rs.getString("salaryid");
				HashMap map=salaryAppdateMap.get(salaryid);
				map.put("a00z2", PubFunc.FormatDate(rs.getDate("A00z2"),"yyyy-MM-dd"));
				map.put("a00z3", rs.getString("a00z3"));
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	 /**
	 * 设置对应节点的属性值
	 * @param param_type
	 * @param property
	 * @return
	 */
	private String getValue(int param_type,String property,Document doc)
	{
		SalaryCtrlParamBo paramBo=new SalaryCtrlParamBo();
		String value="";
		String name=paramBo.getElementName(param_type);
		if(!"".equals(name))
		{
		  try
		  {
			String str_path="/param/"+name;
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				element=(Element)childlist.get(0);
				value=element.getAttributeValue(property);	
			}			
			//人员范围权限过滤 默认为过滤
			if("priv_mode".equalsIgnoreCase(name)&& "flag".equalsIgnoreCase(property)&&value.length()==0)
				value="1";
			
			if("a01z0".equalsIgnoreCase(name)&& "flag".equalsIgnoreCase(property)&&value.length()==0)
				value="0";				
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return value==null?"":value;		
	}
	
	
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setCodesetId("");// 指标集
		columnsInfo.setColumnType("M");// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setColumnLength(100);// 显示长度 
		columnsInfo.setDecimalWidth(0);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		
		return columnsInfo;
	}
	/**
	 * 查询是否是退回状态
	 * @param manager
	 * @param salaryid
	 * @return
	 */
	private String searchIsBackState(String manager, String salaryid) {
		String str = "";
		RowSet rowSet = null;
		try {
			String tableName = this.userview.getUserName() + "_salary_" + salaryid;
			if (manager != null && manager.trim().length() > 0) {
				tableName = manager + "_salary_" + salaryid;
			}
			DbWizard dbWizard = new DbWizard(this.conn);
			if (!dbWizard.isExistTable(tableName, false)) {
				return str;
			}
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select count(sp_flag) from " + tableName + " where sp_flag = '07'";
			rowSet = dao.search(sql);
			if (rowSet.next()) {
				if (rowSet.getInt(1) > 0)
					str = ResourceFactory.getProperty("gz.budget.budget_allocation.reject");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}

		return str;
	}
	/**
	 * 同步薪资类别，自助升级
	 * @param salaryid
	 */
	private void syncSalaryStruct(int salaryid,HashMap newSalarySetMap){
		
		if (newSalarySetMap.get(salaryid+"")==null){ 
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			try {
				/** 未升过级,增加字段 */
				int fieldid = 0; int sortid = 0;
				String sql = "select max(fieldid),max(sortid) from salaryset where salaryid=?";
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(salaryid);
				rowSet = dao.search(sql, list);
				if (rowSet.next()){
					fieldid = rowSet.getInt(1);
					sortid = rowSet.getInt(2);
				}	
				
				ArrayList<RecordVo> recordVolist = new ArrayList<RecordVo>();
				recordVolist.add(getSalarySetRecordvo(salaryid, fieldid+1, "A00", "A00Z2", ResourceFactory.getProperty("gz_new.gz_accounting.send_time"), 20, 0, "0", sortid + 1, 10, "", 3, 1, "D"));
				recordVolist.add(getSalarySetRecordvo(salaryid, fieldid+2, "A00", "A00Z3", ResourceFactory.getProperty("label.gz.count"), 15, 0, "0", sortid + 2, 10, "", 3, 1, "N"));
				dao.addValueObject(recordVolist);
				/** 更新名称A00Z0归属日期,A00Z1归属次数 */
				recordVolist.clear();
				
				RecordVo vo = new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", ResourceFactory.getProperty("gz.columns.a00z0"));
				vo.setString("itemid", "A00Z0");
				recordVolist.add(vo);
				vo = new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", ResourceFactory.getProperty("gz.columns.a00z1"));
				vo.setString("itemid","A00Z1");
				recordVolist.add(vo);
				dao.updateValueObject(recordVolist);
			}catch (Exception ex){
				ex.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rowSet);
			}
		}
	}
	/**
	 * 薪资类别是否为最近结构
	 * @param salaryid
	 * @return
	 */
	private boolean isNewStruct(int salaryid) {

		boolean bflag = false;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select salaryid from salaryset where itemid = ? and salaryid = ?");
			ArrayList<Object> paralist = new ArrayList<Object>();
			paralist.add("A00Z2");
			paralist.add(Integer.valueOf(salaryid));
			rset = dao.search(buf.toString(), paralist);
			if (rset.next()) {
				bflag = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
		}finally{
			PubFunc.closeDbObj(rset);
		}

		return bflag;
	}
	
	
	/**
	 * 拥有最新结构的薪资类别
	 * @param salaryid
	 * @return
	 */
	private HashMap getNewStructSalaryset(String imodule) {

		HashMap salarysetMap=new HashMap();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select sp.salaryid from salaryset st inner join salarytemplate sp on st.salaryid=sp.salaryid where upper(st.itemid)= ? ");//zhanghua 20161125 24549
			if ("0".equals(imodule)){// 薪资类别
				buf.append(" and (sp.cstate is null or sp.cstate='')");
			}else if("1".equals(imodule)){
				buf.append(" and sp.cstate='1'");// 险种类别
			}
			ArrayList<Object> paralist = new ArrayList<Object>();
			paralist.add("A00Z2");

			rset = dao.search(buf.toString(), paralist);
			while (rset.next()) {
				salarysetMap.put(rset.getString(1),"1");
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}finally{
			PubFunc.closeDbObj(rset);
		}

		return salarysetMap;
	}
	
	
	/**
	 * 根据当前用户，查找处理的业务日期和次数 
	 * 1、发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 2、发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * @param salaryid 薪资类别编号
	 * @param username 用户名
	 * @return chent
	 */
	public LazyDynaBean searchBusinessDate(String salaryid, String username) {
		LazyDynaBean businessDateBean = new LazyDynaBean();
		String strYm = "";
		String strC = "";
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			// <<薪资发放记录表>>中发放日期、发放次数
			// 发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
			String sql = "select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid=? and upper(username)=?";
			ArrayList list = new ArrayList();
			list.add(salaryid);
			list.add(username.toUpperCase());
			rowSet = dao.search(sql, list);
			if (rowSet.next()) {
				strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),
						"yyyy-MM-dd");
				strC = rowSet.getString("A00z3");
			} else {
				
				DbWizard dbWizard = new DbWizard(this.conn);
				if (dbWizard.isExistTable(username+"_salary_"+salaryid, false)) { 
					
					if(Sql_switcher.searchDbServer()==2)
						sql="select  a00z2,a00z3 from "+username+"_salary_"+salaryid+" where rownum < 2 "; 
					else 
						sql="select  top 1 * from "+username+"_salary_"+salaryid;
					rowSet = dao.search(sql, new ArrayList());
					if (rowSet.next()) {
						if (rowSet.getDate("A00z2") != null) {
							strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),"yyyy-MM-dd");
							strC = rowSet.getString("A00Z3"); 
							businessDateBean.set("strYm", strYm);
							businessDateBean.set("strC", strC);
							return businessDateBean;
						}
					}
				}
				
				// 发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
				sql = "select max(A00z2) A00z2 from gz_extend_log where  salaryid=? and upper(username)=?";
				list.clear();
				list.add(salaryid);
				list.add(username.toUpperCase());
				rowSet = dao.search(sql, list);
				if (rowSet.next()) {
					if (rowSet.getDate("A00z2") != null) {
						strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),
								"yyyy-MM-dd");
					} else {
						strYm = "";
					}
				}
				if ("".equalsIgnoreCase(strYm)) {
					strC = "";
				} else {
					sql = "select max(A00Z3) A00Z3 from gz_extend_log  where salaryid=? and  upper(username)=? and A00Z2=?";
					list.clear();
					list.add(salaryid);
					list.add(username.toUpperCase());
					
					Date date = null;
					if(StringUtils.isNotBlank(strYm)){
						String dateStr = strYm;
						if(dateStr.indexOf("-")<0)
							date = DateUtils.getSqlDate(strYm,"yyyy.MM.dd");
						else 
							date = DateUtils.getSqlDate(strYm,"yyyy-MM-dd");
						list.add(date);
					}else{
						list.add(date);
					}
					
					rowSet = dao.search(sql, list);
					if (rowSet.next()) {
						strC = rowSet.getString("A00Z3");
					}
				}
			}
			businessDateBean.set("strYm", strYm);
			businessDateBean.set("strC", strC);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return businessDateBean;
	}

	/**
	 * 生成RecordVo类型对象
	 * @return
	 */
	private RecordVo getSalarySetRecordvo(int salaryid, int fieldid, String fieldsetid, String itemid, String itemdesc, int itemlength,
			int decwidth, String codesetid, int sortid, int nwidth, String formula, int initflag, int nlock, String itemtype) {

		RecordVo vo = new RecordVo("salaryset");
		vo.setInt("salaryid", salaryid);
		vo.setInt("fieldid", fieldid);
		vo.setString("fieldsetid", fieldsetid);
		vo.setString("itemid", itemid.toUpperCase());
		vo.setString("itemdesc", itemdesc);
		vo.setInt("itemlength", itemlength);
		vo.setInt("decwidth", decwidth);
		vo.setString("codesetid", codesetid);
		vo.setInt("sortid", sortid);
		vo.setInt("nwidth", nwidth);
		vo.setString("formula", formula);
		vo.setInt("initflag", initflag);
		vo.setInt("nlock", nlock);
		vo.setString("itemtype", itemtype);

		return vo;
	}
	/**
	 * 获取页面title
	* @param viewtype 页面区分 0:薪资发放  1:审批  2:上报
	 * @param imodule  0:薪资  1:保险
	 * @return
	 */
	public String getTitle(String viewtype, String imodule) {

		StringBuilder imoduleName = new StringBuilder();
		if ("0".equals(imodule)) {
			imoduleName.append(ResourceFactory.getProperty("gz.report.salary"));//薪资
		} else if ("1".equals(imodule)) {
			imoduleName.append(ResourceFactory.getProperty("gz.report.welfare"));//保险
		}

		StringBuilder viewName = new StringBuilder();
		if ("0".equals(viewtype)) {
			viewName.append(ResourceFactory.getProperty("gz_new.gz_accounting.pay"));
		} else if ("1".equals(viewtype)) {
			viewName.append(ResourceFactory.getProperty("gz_new.gz_accounting.approveoperation"));
		} else if ("2".equals(viewtype)) {
			imoduleName = new StringBuilder();
			viewName.append("数据上报");
		}

		return (imoduleName.append(viewName)).toString();
	}

	/**
	 * 通过url查找的值
	 * @param url 源url加密字符串
	 * @param str 需要筛选的字符串
	 * @return
	 */
	public String getValByStr(String url, String str) {

		String val = "";
		try {
			url = url.substring(1);
			if(url.indexOf("encryptParam")!=-1){
				url = url.replaceAll("b_query=link&encryptParam=", "");
				url = PubFunc.decrypt(url);
			}
			int index = url.indexOf(str + "=");
			if(index==-1){
				return "1";
			}
			int startIndex = index + str.length() + 1;
			int endIndex = index + str.length() + 2;
			val = url.substring(startIndex, endIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return val;
	}
    /**
     * 整型判断
     * @param str
     * @return
     */
    public boolean isInteger(String str){
     if(str==null )
      return false;
     Pattern pattern = Pattern.compile("[0-9]+");
     return pattern.matcher(str).matches();
    }
    
	/**
	 * 获得 salarytemplate对象
	 * 
	 * @param salaryid
	 * @param conn
	 * @return
	 */
	public RecordVo getRealConstantVo(Connection conn, int salaryid) {

		RecordVo vo = null;

		vo = new RecordVo("salarytemplate");
		vo.setInt("salaryid", salaryid);
		ContentDAO dao = new ContentDAO(conn);
		try {
			try {
				vo = dao.findByPrimaryKey(vo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}

		return vo;
	}


	/**
	 * 获取薪资常用报表
	 * @param salaryid
	 * @param model 0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 15:22 2019/1/25
	 */
	public ArrayList<LazyDynaBean> listCommon_report(int salaryid,String model) throws GeneralException {
		ArrayList<LazyDynaBean> reportList=new ArrayList<LazyDynaBean>();
		try{
			Document doc=this.initLProgram(salaryid);
			XPath xpath=XPath.newInstance("/Params/common_report");
			List childlist=xpath.selectNodes(doc);
			Element element = null;
			if(childlist.size()>0){
				Element pelement=(Element)childlist.get(0);
				List list=pelement.getChildren();
				for (int i = 0; i < list.size(); i++) {
					{
						element = (Element) list.get(i);
						String value=element.getValue();
						String rsModel=element.getAttributeValue("model");
						if(!model.equals(rsModel)){
							continue;
						}
						String rsid = StringUtils.isBlank(element.getAttributeValue("rsid")) ? "" : element.getAttributeValue("rsid");
						if(StringUtils.isNotBlank(value)){

							if("4".equals(rsid)) {
								String[] userIds = value.trim().split(",");
								for (String userName : userIds) {
									if (this.userview.getUserName().equalsIgnoreCase(userName)) {
										LazyDynaBean bean = new LazyDynaBean();
										bean.set("rsid", "4");
										bean.set("tabid", "4");
										reportList.add(bean);
									}

								}
							}else{
								String[] ids = value.trim().split(",");
								for (String id : ids) {
									if (StringUtils.isNotBlank(id)) {
										LazyDynaBean bean = new LazyDynaBean();
										bean.set("rsid", rsid);
										bean.set("tabid", id);
										reportList.add(bean);
									}

								}
							}
						}
					}
				}
			}

			Collections.sort(reportList, new Comparator<LazyDynaBean>() {
				//@Override
				@Override
                public int compare(LazyDynaBean o1, LazyDynaBean o2) {
					String rsid1=(String) o1.get("rsid");
					String tabid1=(String) o1.get("tabid");
					String rsid2=(String) o2.get("rsid");
					String tabid2=(String) o2.get("tabid");
					if(rsid1.equals(rsid2)){
						if(tabid1.startsWith("t")){
							tabid1=tabid1.replaceAll("t_","10");
						}
						if(tabid2.startsWith("t")){
							tabid2=tabid2.replaceAll("t_","10");
						}
						return Integer.parseInt(tabid2)-Integer.parseInt(tabid1);
					}else{
						if("0".equals(rsid1)){
							return 1;
						}else if("0".equals(rsid2)){
							return -1;
						}else {
							return Integer.parseInt(rsid2)-Integer.parseInt(rsid1);
						}
					}
				}
			});
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return reportList;
	}

	/**
	 * 添加薪资常用报表
	 * @param salaryid
	 * @param bean
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 15:23 2019/1/25
	 */
	public void addCommon_report(int salaryid ,LazyDynaBean bean) throws GeneralException {

		try {
			int rsid = (Integer) bean.get("rsid");
			String tabid = (String) bean.get("tabid");
			String model = (String) bean.get("model");
			Document doc = this.initLProgram(salaryid);

			Element element = null;
			String str_path = "/Params/common_report/report_item[@rsid=" + rsid + "][@model="+model+"]";
			XPath xpath = XPath.newInstance(str_path);
			List childlist = xpath.selectNodes(doc);

			if (childlist.size() == 0) {

				str_path = "/Params/common_report";
				xpath = XPath.newInstance(str_path);
				childlist = xpath.selectNodes(doc);
				Element pelement = null;

				if (childlist.size() == 0) {
					pelement = new Element("common_report");
					doc.getRootElement().addContent(pelement);
				} else {
					pelement = (Element) childlist.get(0);
				}

				element = new Element("report_item");
				element.setAttribute("rsid", String.valueOf(rsid));
				element.setAttribute("model", model);
				element.setText(","+tabid + ",");
				pelement.addContent(element);
			} else {
				element = (Element) childlist.get(0);
				String ids = element.getValue();
				ids += tabid + ",";
				element.setText(ids);
			}
			this.saveParameterLProgram(doc, salaryid);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 删除薪资常用报表
	 * @param salaryid
	 * @param bean
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 15:23 2019/1/25
	 */
	public void delCommon_report(int salaryid ,LazyDynaBean bean) throws GeneralException {
		try{
			int rsid=(Integer) bean.get("rsid");
			String tabid=(String) bean.get("tabid");
			String model = (String) bean.get("model");

			Document doc=this.initLProgram(salaryid);

			String str_path = "/Params/common_report/report_item[@rsid=" + rsid + "][@model="+model+"]";
			XPath xpath = XPath.newInstance(str_path);
			List childlist = xpath.selectNodes(doc);
			Element element = null;
			if (childlist.size() >0) {
				element = (Element) childlist.get(0);
				String ids=element.getValue();
				if(ids.indexOf(","+tabid+",")>-1) {
					ids = ids.replaceAll(","+tabid + ",", ",");
					element.setText(ids);
					this.saveParameterLProgram(doc,salaryid);
				}
			}


		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 薪资常用报表参数保存
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 15:24 2019/1/25
	 */
	public void saveParameterLProgram(Document doc,int salaryid)throws GeneralException
	{
		try
		{
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setInt("salaryid", salaryid);
			dao.findByPrimaryKey(vo);
			vo.setString("lprogram", buf.toString());
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 初始化薪资常用报表
	 * @param salaryid
	 * @return
	 * @author ZhangHua
	 * @date 15:24 2019/1/25
	 */
	private Document initLProgram(int salaryid) {
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<Params>");
		strxml.append("</Params>");
		RowSet rset = null;
		Document document=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbw = new DbWizard(this.conn);
			String xmlcontent="";
			StringBuffer buf = new StringBuffer();
			buf.append("select lprogram from salarytemplate where salaryid=?");
			ArrayList paramlist = new ArrayList();
			paramlist.add(salaryid);
			rset = dao.search(buf.toString(), paramlist);
			if (rset.next()) {
				xmlcontent = Sql_switcher.readMemo(rset, "lprogram");
			}
			if (xmlcontent == null || "".equalsIgnoreCase(xmlcontent))
				xmlcontent = strxml.toString();
			document = PubFunc.generateDom(xmlcontent.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);

		}
		return document;
	}
}
