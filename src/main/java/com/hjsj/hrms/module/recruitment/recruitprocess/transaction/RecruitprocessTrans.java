package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class RecruitprocessTrans extends IBusiness {

	/**流程查询
	 * @param args
	 */
@Override
public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		this.getFormHM().remove("requestPamaHM");
		UserView userView= this.getUserView();
		String pageNum = "";//返回页数
		String searchStr = "";//类别
		String pagesize = "";//页面数值
		String page = "";//招聘流程表格信息
		//得到当前职位序号
		String z0301 = "";// 职位id
		String z0381 = "";//招聘流程ID
		int flag = 2;//环节范围
		String projectId = SafeCode.decode((String)this.getFormHM().get("projectId"));//获取查询方案
		String status = SafeCode.decode((String)this.getFormHM().get("status"));//获取查询状态
		String queryStr = SafeCode.decode((String)this.getFormHM().get("queryStr"));//获取查询条件
		LazyDynaBean infoBean = new LazyDynaBean();
		String node_id = "";
		String link_id = "";
		if(hm!=null)
		{	
			z0301 = SafeCode.decode(PubFunc.decrypt((String)hm.get("z0301")));//职位id
			z0381 = SafeCode.decode(PubFunc.decrypt((String)hm.get("z0381")));//招聘流程ID
			pageNum = SafeCode.decode((String)hm.get("pageNum"));//返回页数
			searchStr = SafeCode.decode((String)hm.get("searchStr"));//类别
			pagesize = SafeCode.decode((String)hm.get("pagesize"));//页面数值
			page = SafeCode.decode((String)hm.get("page"));//面试安排返回信息
			hm.remove("page");
			if(page==null|| "".equals(page))
			{
				this.getFormHM().put("pageSize", "20");
				this.getFormHM().put("pageNum", "1");
			}else{				
				this.getFormHM().put("pageSize", page.split("`")[0]);
				this.getFormHM().put("pageNum", page.split("`")[1]);
			}
			node_id=(String) hm.get("node_id");
			link_id=(String) hm.get("link_id");
			projectId = link_id;
			status = node_id;
		}else
		{
			link_id=projectId;
			node_id = status;
			z0381 = PubFunc.decrypt((String)this.getFormHM().get("z0381"));
			z0301 = PubFunc.decrypt((String)this.getFormHM().get("z0301"));
		}
		String tablekey = SafeCode.decode((String)this.getFormHM().get("tablekey"));
		if(tablekey!=null)
		{
			this.getFormHM().remove("tablekey");
		}
		
		RecruitProcessBo bo = new RecruitProcessBo(this.frameconn,this.userView);
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//获取文件配置路径
			TemplateBo tbo = new TemplateBo(this.frameconn, dao, this.userView);
			//是否具有环节权限
//			RecruitPrivBo privBo = new RecruitPrivBo();
//			boolean hasFlowLinkPriv = privBo.hasFlowLinkPriv(this.frameconn, this.userView, z0301, z0381, link_id);
			boolean hasFlowLinkPriv = true; //201224 客户反映没有操作权限的，还是应该可以看到数据
			//获取流程阶段信息
			ArrayList<LazyDynaBean> stageList = bo.getStageInfo(z0301,z0381,flag);
			//是否必须按流程环节进行
			String skipFlag = bo.getSkipFlag(z0381);
			if(!hasFlowLinkPriv){
				for (LazyDynaBean bean : stageList) {
					if("true".equals(bean.get("linkPriv"))){
						link_id = (String) bean.get("link_id");
						node_id = (String)bean.get("status");
						projectId = link_id;
						status = node_id;
						hasFlowLinkPriv =true;
						break;
					}
				}
			}
			//获取查询方案列表
			ArrayList projectList = bo.getProjectList(link_id,z0301,flag);
			//获取操作集合
			ArrayList buttons = bo.getOperationList(z0301, z0381, link_id, pageNum ,searchStr,pagesize);
			//获取表列
			ArrayList columnList = bo.getColumnList(link_id);
			//获取sql
			String listSql = bo.getListSql(columnList, link_id,z0301);
			//获取查询条件
			String sql_where = bo.get_where(projectId,status,queryStr);
			String querySql = "";
			String filterSql = "";
			String back = (String) userView.getHm().get("isback");
			if("true".equals(back))//职位候选人进入简历详情，返回加入查询条件使用
			{
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_recruit_00001");
				querySql = tableCache.getQuerySql()==null?"":tableCache.getQuerySql();
				filterSql = tableCache.getFilterSql()==null?"":tableCache.getFilterSql();
				querySql = querySql.replaceAll("myGridData.", "");
				filterSql = filterSql.replaceAll("myGridData.", "");
			}
			if(sql_where.length()>0)
			{
				listSql = listSql+" "+sql_where;
			}
			//获得招聘流程下一阶段的link_id,node_id
			String next_linkId=bo.getNextLinkId(link_id,stageList);
			String next_nodeId=bo.getNextNodeId(link_id, stageList);
			String lastLinkId = bo.getLastLinkId(link_id, stageList);
			ArrayList<String> skiplist = new ArrayList<String>();
			skiplist.add(lastLinkId);
//			skiplist.add(link_id);//当前环节
			skiplist.add(next_linkId);
			infoBean.set("z0381", PubFunc.encrypt(z0381));
			
			if(!hasFlowLinkPriv){
				listSql = listSql+" and 1=2 ";
			}
			
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("zp_recruit_00001", columnList, "tablelist", userView, this.getFrameconn());
			builder.setDataSql("true".equals(back)?listSql+querySql+filterSql:listSql);
			builder.setSetScheme(true);
			builder.setConstantName("recruitment/recruitprocess");
			builder.setPageSize(20);
			builder.setTableTools(buttons);
			builder.setSelectable(true);
			builder.setScheme(true);
			builder.setItemKeyFunctionId("ZP0000002086");
			builder.setColumnFilter(true);//启用过滤
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TOOLBAR);
			builder.setFieldAnalyse(true);//代码型指标增加统计功能
			builder.setSchemeSaveCallback("Global.pageLode");
			if(userView.isSuper_admin()||userView.hasTheFunction("311011708")){
				builder.setShowPublicPlan(true);
			}
			
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("skipFlag", skipFlag);
			this.getFormHM().put("skiplist", skiplist);
			this.getFormHM().put("hasFlowLinkPriv", hasFlowLinkPriv);
			this.getFormHM().put("infoBean", infoBean);
			this.getFormHM().put("linkId", link_id);
			this.getFormHM().put("nodeId", node_id);
			this.getFormHM().put("z0301", PubFunc.encrypt(z0301));
			this.getFormHM().put("stageList", stageList);
			this.userView.getHm().put("stageList", stageList);
			this.getFormHM().put("next_linkId",next_linkId);
			this.getFormHM().put("next_nodeId",next_nodeId);
			this.getFormHM().put("sqlcolumn", columnList);
			this.getFormHM().put("sqlstr",listSql );
			this.getFormHM().put("projectList", projectList);
			this.getFormHM().put("operationList", buttons);
			this.getFormHM().put("emailItemId", bo.getEmailItemId().toLowerCase());//获取邮件地址指标
			this.userView.getHm().put("export_sql", listSql);//查询语句放入userview，导出简历时使用
			if(tablekey!=null&&!"".equals(tablekey))
			{
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);
				tableCache.setQuerySql("");
				tableCache.setTableSql(listSql);
				userView.getHm().put(tablekey, tableCache);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
