package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class MatchPersonnelTrans extends IBusiness {

	/*
	 * 1、“综合测试”合格 2、未申请其它岗位或被其它岗位淘汰的 3、简历符合本岗位要求的（满足岗位筛选规则条件）
	 * 如果没有符合条件的人员，则提示“未找到符合要求人员！”。
	 */
	@Override
	public void execute() throws GeneralException {
		try {
			// 岗位id
			String positionid = (String) this.getFormHM().get("positionid");
			positionid = PubFunc.decrypt(positionid);
			// 岗位名称
			String position = (String) this.getFormHM().get("position");

			// 读取system.properties中招聘职位编号 格式： 招聘流程编号:招聘职位编号
			String[] zp_pre_pos = SystemConfig.getPropertyValue("zp_pre_pos").split(":");
			// 岗位前置ID
			String prePositionId = (zp_pre_pos.length == 2) ? zp_pre_pos[1] : "";

			ResumeFilterBo resumeFilterBo = new ResumeFilterBo(this.getFrameconn(), this.userView);
			// 获取人员库前缀
			String dbname = resumeFilterBo.getDBPre();
			// 招聘根据应聘职位校验应聘人员是否符合筛选条件
			ArrayList a0100List = resumeFilterBo.ruleFilter(positionid, "", dbname, "recommend");
			String a0100Condition = (String) a0100List.get(0);

			// 组装查询SQL,满足条件：1、“综合测试”合格2、未申请其它岗位或被其它岗位淘汰的3、简历符合本岗位要求的（满足岗位筛选规则条件）
			String filterA0100 = resumeFilterBo.getValidataSql(a0100Condition, resumeFilterBo, dbname, prePositionId);
			// 二次推荐   检查多个多个a0100
			String resultA0100s = resumeFilterBo.checkApplyQualifyForA0100s(dbname, filterA0100, positionid);
																												
			ArrayList<ColumnsInfo> column = resumeFilterBo.listRecommendedPosition("recommendedpositionid");// 传入表格控件的列集合
			String sql = resumeFilterBo.getQueryDataSql(column,dbname, resultA0100s);// 传送表格控件的SQL
			int countNumber = resumeFilterBo.countNumber(sql);
			ArrayList btnlist = resumeFilterBo.getButtonList();// 前台的功能按钮
			TableConfigBuilder builder = new TableConfigBuilder("recommendedpositionid", column, "recommend", userView,
					this.getFrameconn());
			builder.setDataSql(sql);
			builder.setSetScheme(true);
			//builder.setConstantName("recruitment/recommend");// 推荐按钮
			builder.setPageSize(20);
			builder.setTitle(position + "岗位-推荐候选人");
			builder.setTableTools(btnlist);
			builder.setSelectable(true);
			//builder.setScheme(true);
			builder.setColumnFilter(true);// 启用过滤
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TOOLBAR);
			builder.setFieldAnalyse(true);// 代码型指标增加统计功能
			builder.setItemKeyFunctionId("ZP0000002092");
			builder.setSchemeSaveCallback("Global.saveCallBack");
			builder.setSearchConfig("ZP0000002090", "请输入姓名、邮箱、学校...",true);

			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("countNumber", countNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
