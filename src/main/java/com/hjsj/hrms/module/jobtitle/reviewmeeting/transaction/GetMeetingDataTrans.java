package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:GetMeetingDataTrans </p>
 * <p>Description: 评审会议展示</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GetMeetingDataTrans extends IBusiness {
	
	private static final String REVIEW_MEETING_SUBMODULEID = "zc_reviewmeeting_00001";

	@Override
    public void execute() throws GeneralException {
		try {
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
			if(StringUtils.isEmpty(subModuleId)){// 初次进入页面 
				
				JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.userView);
				boolean college_eval = jobtitleConfigBo.getParamConfig("college_eval");	//是否在配置中勾选二级单位评议组评审环节

				ReviewMeetingBo bo = new ReviewMeetingBo(this.frameconn,this.userView);
				ArrayList<ColumnsInfo> columnList = bo.getColumnList(college_eval);//得到列头
				StringBuffer datasql =  new StringBuffer();//查询数据源sql
				datasql.append("select * from ");
				String selectsql = bo.getSelectSql();//得到查询列
				datasql.append(selectsql+" c");
				
				TableConfigBuilder builder = new TableConfigBuilder(REVIEW_MEETING_SUBMODULEID, columnList, "meeting", userView, this.getFrameconn());
				builder.setDataSql(datasql.toString());//数据查询sql语句
				builder.setOrderBy("order by create_time desc");//排序语句
				builder.setAutoRender(true);//是否自动渲染表格到页面
				builder.setTitle("评审会议");// 标题
				builder.setColumnFilter(true);//统计过滤
				if(this.userView.hasTheFunction("380050509")){//栏目设置权限
					builder.setScheme(true);//栏目设置
					builder.setSetScheme(true);
					builder.setShowPublicPlan(this.userView.hasTheFunction("38005050901"));//公有
				}
				builder.setSelectable(true);//选框
				builder.setEditable(true);//表格编辑
				builder.setPageSize(20);//每页条数
				builder.setLockable(true);
				builder.setConstantName("jobtitle/reviewmeeting");
				ArrayList buttonList = bo.getButtonList();//得到操作按钮
				builder.setTableTools(buttonList);//表格工具栏功能
				String config = builder.createExtTableConfig();
				Map<String,String> map = bo.getCommiteeInfo();
				this.getFormHM().put("tableConfig", config.toString());
				this.getFormHM().put("commiteeInfo", map);
				
			} else if(REVIEW_MEETING_SUBMODULEID.equals(subModuleId)){// 快速查询
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(REVIEW_MEETING_SUBMODULEID);
				String type = (String)this.getFormHM().get("type");
				StringBuilder querySql = new StringBuilder();
				if("1".equals(type)) {// 1:输入查询
					ArrayList<String> valuesList = new ArrayList<String>();
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
					// 快速查询
					if (valuesList != null && valuesList.size() > 0) {// 输入检索条件
						querySql.append(" and ( ");
					}
					for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
						String queryVal = valuesList.get(i);
						queryVal = SafeCode.decode(queryVal);// 解码
						if (i != 0) {
							querySql.append("or ");
						}
						querySql.append(" w0303 like '%" + queryVal + "%'");
					}
					
					String tempSql =catche.getQuerySql();
					if (valuesList != null && valuesList.size() > 0) {
						querySql.append(" ) ");
						if(tempSql!=null && tempSql.indexOf("2=2")>-1)
							tempSql=tempSql.substring(0,tempSql.indexOf(" and 2=2"));
						tempSql+=" and 2=2"+querySql.toString();
					}else {
						if(tempSql!=null && tempSql.indexOf("2=2")>-1)
							tempSql=tempSql.substring(0,tempSql.indexOf(" and 2=2"));
					}
				}else if("2".equals(type)){//方案查询
					if(StringUtils.isNotBlank(catche.getQuerySql()))
						querySql.append(catche.getQuerySql());
					if(querySql.indexOf("2=2")==-1)
						querySql.append(" and 2=2");
					else
						querySql.setLength(querySql.indexOf("2=2")+3);
					HashMap queryFields = catche.getQueryFields();//haosl 20161014方案查询可以查询自定义指标
					String exp = (String) this.getFormHM().get("exp");
					exp = SafeCode.decode(exp);
					exp=PubFunc.keyWord_reback(exp);
					String cond = (String) this.getFormHM().get("cond");
					cond = SafeCode.decode(cond);
					cond = cond.replaceAll("＜", "<");
					cond = cond.replaceAll("＞", ">");
					if(cond.length()<1 || exp.length()<1){
						catche.setQuerySql(querySql.toString());
						this.getFormHM().put("errorcode", "0");
						return;
					}
					querySql.append(" and ");
					FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);//haosl 20161014方案查询可以查询自定义指标
					querySql.append(parser.getSingleTableSqlExpression("myGridData"));
				}else {//检索条件
					ArrayList schemeTypeArray = (ArrayList)this.getFormHM().get("schemeTypeArray");
					for(int i=0; schemeTypeArray != null && i<schemeTypeArray.size(); i++){
						String scheme = (String)schemeTypeArray.get(i);
						if("0".equals(scheme) || "all".equals(scheme)){//all：全部、0：没有选
							continue;
						}
						querySql.append("and ");
						if ("nowyear".equals(scheme)) {// 本年度
							querySql.append(Sql_switcher.diffYears(Sql_switcher.today(), "W0309")+"=0");
							
						} else if ("preyear".equals(scheme)) {// 上年度
							querySql.append(Sql_switcher.diffYears(Sql_switcher.today(), "W0309")+"=1");
							
						} else if ("init".equals(scheme)) {// 起草
							querySql.append(" w0321='01' ");
							
						} else if ("in".equals(scheme)) {// 执行中
							querySql.append(" w0321='05' ");
							
						} else if ("finish".equals(scheme)) {// 结束
							querySql.append(" w0321='06' ");
							
						} else if ("stop".equals(scheme)) {// 暂停
							querySql.append(" w0321='09' ");
						}
					}
				}
				catche.setQuerySql(querySql.toString());
				this.getFormHM().put("errorcode", "0");
			}
			
		} catch (Exception e) {
			this.getFormHM().put("errorcode", "1");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
