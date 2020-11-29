package com.hjsj.hrms.module.talentmarkets.competition.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

public interface CompetitionApplyService {

	/**
	 * 获取竞聘岗位&我的竞聘&历史竞聘表格组件数据
	 * @param state 状态  competitive_jobs 竞聘岗位    executing 进行中   end 结束
	 * @param orgId 机构id号
	 * @return
	 * @throws GeneralException
	 */
	String getTableConfig(String state , String orgId) throws GeneralException;
	
	/**
	 * 获取竞聘岗位&我的竞聘 当前岗位数
	 * @param state 状态  competitive_jobs 竞聘岗位    executing 进行中
	 * @param orgId 机构id号
	 * @return
	 * @throws GeneralException
	 */
	int getCompetitionApplyPostNum(String state , String orgId) throws GeneralException;
	
	/**
	 * 获取竞聘报名所需的其他参数
	 * @throws GeneralException
	 * @return 
	 * {
	 *   laveJobNum:1,//剩余竞聘岗位数
	 *   postDetailRnameId:1,// 获取岗位详情登记表id号
	 *   templateType:'applyTemplate',//与人事异动表单集成类型   竞聘报名模板
	 *   recordList:[{ //与人事异动表单集成提供自助用户人员关键信息
 	 *   	nbase:xxx
	 *   	guidkey:xxx
	 *      a0100:xxx
	 *   }]
	 * }
	 */
	Map getCompetitionApplyOtherParam() throws GeneralException;
	
	/**
	 * 获取当前报名岗位模板情况
	 * @param state 状态    executing 进行中 end 结束
	 * @param z8101 竞聘岗位id号
	 * @return 
	 * @throws GeneralException
	 */
	Map getCompetitionApplyTemplateData(String state, String z8101) throws GeneralException;
	
}
