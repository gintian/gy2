package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 上会材料  主界面初始化
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */	
@SuppressWarnings("serial")
public class ReviewFileTrans extends IBusiness {

	// 排除指标 : 
	public static String exceptFields = ",w0575,";
	// 不可编辑指标
	public static String notEditFields = ",meetingname,checkproficient,professiongroup,agreeproportion,collegeagree,w0511,w0507,w0509,w0535,w0537,w0521,w0555,w0571,committeeName,w0573,";
	// 锁列
	public static String islock = ",w0507,w0509,w0511,";
	//导入数据-下载时需要锁住的列--会议名称、姓名、部门、现聘职务、申报职务
	public static String exportIslock = ",meetingname,w0509,w0507,w0511,w0559,w0557,w0533,w0513,w0515,group_id,w0555,checkproficient,w0521,w0517,w0571,committeename,collegeagree,w0569,w0573,";
	//导入数据-下载时需要隐藏的列--申报人主键序号ID 会议ID proficientagree
    public static String exportHidden = ",w0301,w0321,w0501,proficientagree,subjectsagree,committeeagree,collegeagree,";
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
			if(StringUtils.isEmpty(subModuleId)){// 初次进入页面 
				
				String w0301 = PubFunc.decrypt((String)formHM.get("w0301"));
				ArrayList schemeType = (ArrayList)this.getFormHM().get("schemeType");
				ArrayList<Object> columnsList = reviewFileBo.getColumnList(ReviewFileTrans.exceptFields, ReviewFileTrans.notEditFields, ReviewFileTrans.islock,w0301);
				TableConfigBuilder builder = new TableConfigBuilder("reviewFile_"+w0301, columnsList, "reviewFile", userView, this.getFrameconn());
				StringBuilder querySql = new StringBuilder();
				for(int i=0; schemeType!=null && i<schemeType.size(); i++){
					String scheme = (String)schemeType.get(i);
					if("0".equals(scheme) || "all".equals(scheme) || StringUtils.isEmpty(scheme)){//all：全部、0：没有选
						continue;
					}
					querySql.append(" and ");
					if ("in".equals(scheme)) {// 进行中
						querySql.append("w0321='05' ");
					} else if ("stop".equals(scheme)) {// 暂停
						querySql.append("w0321='09' ");
					} else if ("finish".equals(scheme)) {// 已结束
						querySql.append("w0321='06' ");
					}
				}
				
				builder.setDataSql(reviewFileBo.getSql(w0301)+" and w03.w0301="+w0301+querySql);
				builder.setOrderBy(" order by w0507,w0509,w0511 DESC");
				builder.setColumnFilter(true);//统计过滤
				builder.setLockable(true);
				builder.setSelectable(true);
				builder.setEditable(true);
				if(this.userView.hasTheFunction("380050617")){//栏目设置权限
					builder.setScheme(true);//栏目设置
					builder.setSetScheme(true);
					builder.setShowPublicPlan(this.userView.hasTheFunction("38005061701"));//公有
					builder.setSchemePosition("ReviewFile_schemeSetting");
					builder.setSchemeSaveCallback("reviewfile_me.schemeSetting_callBack");
				}
				boolean returnButton = (Boolean) this.getFormHM().get("returnButton");// 为空：初次进入页面 ；不为空：快速查询
				builder.setTableTools(reviewFileBo.getButtonList(returnButton));
				builder.setPageSize(500);
				String config = builder.createExtTableConfig();
				this.getFormHM().put("tableConfig", config.toString());
				
				// 学科专业组
				ArrayList<HashMap> group_id = new ArrayList<HashMap>();
				group_id = reviewFileBo.getProfessionGroup();
				this.getFormHM().put("group_id", group_id);
				
				// 问卷调查计划
				ArrayList<HashMap> qnPlan = new ArrayList<HashMap>();
				qnPlan = reviewFileBo.getQnPlan();
				this.getFormHM().put("qnPlan", qnPlan);
				
				// 评审环节
				ArrayList<HashMap> reviewStepList = new ArrayList<HashMap>();
				reviewStepList = reviewFileBo.getReviewStepList();
				this.getFormHM().put("reviewsteplist", reviewStepList);
				
				
				JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(),this.getUserView());
				// 公示、投票环节显示申报材料表单上传的word模板内容
				boolean support_word = jobtitleConfigBo.getParamConfig("support_word");	
				this.getFormHM().put("support_word", support_word);
				
			} else if(subModuleId.startsWith("reviewFile")){// 快速查询
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
				String type = (String)this.getFormHM().get("type");
				if("1".equals(type)) {// 1:输入查询
					StringBuilder querySql = new StringBuilder();
					ArrayList<String> valuesList = new ArrayList<String>();
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
					// 快速查询
					if (valuesList != null && valuesList.size() > 0) {
						querySql.append(" and ( ");
					}
					for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
						String queryVal = valuesList.get(i);
						queryVal = SafeCode.decode(queryVal);// 解码
						if (i != 0) {
							querySql.append("or ");
						}
						querySql.append("(W0511 like '%" + queryVal+"%'");
						//haosl 2017-08-03 模糊查询单位和部门
						List<String> itemids = reviewFileBo.getCodeByLikeDesc(queryVal);
						if(itemids.size()>0) {
							StringBuffer itemBuf = new StringBuffer();
							for(String itemid : itemids) {
								itemBuf.append("'"+itemid+"',");
							}
							itemBuf.setLength(itemBuf.length()-1);
							querySql.append(" or W0507 in ("+itemBuf+") or W0509 in ("+itemBuf+")");
						}
						querySql.append(")");
					}
					
					String tempSql =catche.getQuerySql()==null?"":catche.getQuerySql();
					if (valuesList != null && valuesList.size() > 0) {
						querySql.append(" ) ");
						if(tempSql.indexOf("2=2")>-1)
							tempSql=tempSql.substring(0,tempSql.indexOf(" and 2=2"));
						tempSql+=" and 2=2"+querySql.toString();
					}else {
						if(tempSql.indexOf("2=2")>-1)
							tempSql=tempSql.substring(0,tempSql.indexOf(" and 2=2"));
					}
					catche.setQuerySql(tempSql);
				}else if("2".equals(type)){//方案查询
					StringBuilder querySql = new StringBuilder();
					querySql.append(catche.getQuerySql()==null?"":catche.getQuerySql());
					if(querySql.indexOf("2=2")==-1) {
						querySql.append(" and 2=2");
					} else {
						querySql.setLength(querySql.indexOf("2=2")+3);
					}
					HashMap queryFields = catche.getQueryFields();//haosl 20161014方案查询可以查询自定义指标
					String exp = (String) this.getFormHM().get("exp");
					exp = SafeCode.decode(exp);
					exp=PubFunc.keyWord_reback(exp);
					String cond = (String) this.getFormHM().get("cond");
					cond = SafeCode.decode(cond);
					cond = cond.replaceAll("＜", "<");
					cond = cond.replaceAll("＞", ">");
					if(cond.length()<1 || exp.length()<1){
						// 方案查询中选择“全部”的时候，要恢复原来的检索条件 chent 20180203 add
						catche.setQuerySql(querySql.toString());
						return;
					}
					querySql.append(" and ");
					FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);//haosl 20161014方案查询可以查询自定义指标
					querySql.append(parser.getSingleTableSqlExpression("myGridData"));
					catche.setQuerySql(querySql.toString());
				}else {//检索条件
					/*String w0301 ="";
					StringBuilder querySql = new StringBuilder();
					ArrayList schemeTypeArray = new ArrayList();// 查询方案
					ArrayList schemeType = (ArrayList)this.getFormHM().get("schemeType");
					if(schemeType != null){
						schemeTypeArray = schemeType;
					}
					for(int i=0; i<schemeTypeArray.size(); i++){
						String scheme = (String)schemeTypeArray.get(i);
						if("0".equals(scheme) || "all".equals(scheme) || StringUtils.isEmpty(scheme)){//all：全部、0：没有选
							continue;
						}
						querySql.append("and ");
						if ("in".equals(scheme)) {// 进行中
							querySql.append("w0321='05' ");
						} else if ("stop".equals(scheme)) {// 暂停
							querySql.append("w0321='09' ");
						} else if ("finish".equals(scheme)) {// 已结束
							querySql.append("w0321='06' ");
						} else if ("w0555_1".equals(scheme)) {// 论文送审（外部鉴定专家）
							querySql.append("w0555='1'");
						} else if ("w0555_2".equals(scheme)) {// 材料初审（学科组）
							querySql.append("w0555='2'");
						} else if ("w0555_3".equals(scheme)) {// 会议评审（聘委会）
							querySql.append("w0555='3'");
						}  else if ("w0555_4".equals(scheme)) {// 会议评审（学校聘任组）
							querySql.append("w0555='4'");
						}else {
							String[] s = scheme.split("_");
							if("w0301".equalsIgnoreCase(s[0]) && !StringUtils.isEmpty(s[1])){
								w0301 = PubFunc.decrypt(s[1]);
								querySql.append("w0301='"+w0301+"'");
							}
						}
					}
					
					catche.setTableSql(reviewFileBo.getSql());
					catche.setQuerySql(querySql.toString());*/
				}
				this.getFormHM().put("errorcode", "0");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
