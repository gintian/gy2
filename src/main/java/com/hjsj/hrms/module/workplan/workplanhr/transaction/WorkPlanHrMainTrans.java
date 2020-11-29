package com.hjsj.hrms.module.workplan.workplanhr.transaction;

import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.module.workplan.workplanhr.businessobject.WorkPlanHrBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工作计划监控页面
 * 
 * @createtime Oct 24, 2017 9:07:55 PM
 * @author Administrator
 *
 */
public class WorkPlanHrMainTrans extends IBusiness {

	private static final long serialVersionUID = -5385927771279110043L;
	
	public static final String WORKPLAN_HR_SUBMODULEID = "workplan_hr"; // 工作计划监控唯一标识

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {

		try {
			String type = (String) this.getFormHM().get("type");
			/**
			 * 说明：
			 *  1：快速查询  
			 *  2：快速查询方案查询
			 *  3：计划区间查询、应报已报查询
			 *  4：提醒写/批准计划
			 *  5：关联考核计划 
			 *  6：更新目标卡 
			 *  7：更换审批人
			 *  8: 查询应报已报人数
			 *  9: 页面初始化
			 *  10:页面初始化前加载一些参数
			 *  11:检查当前选中的计划是否可以关联
			 *  12:检查是所选人员是否已关联过
			 *  13:校验考核计划的模板
			 * 	14:复杂查询
             * 	15:更新目标卡前，检查所选人员是否正在打分
			 */
			WorkPlanHrBo bo = new WorkPlanHrBo(this.getFrameconn(), this.getUserView());
			if ("1".equals(type)) {
				int errorcode = bo.fastSearchByInput(this.getFormHM());
				this.getFormHM().put("errorcode", errorcode);

			} else if ("2".equals(type)) {
				// 暂不启用

			} else if ("3".equals(type)) {
				int errorcode = 1;
				bo.fastSearchByCondition(this.getFormHM());
				HashMap<String, HashMap<String, String>> mappings = bo.getMappings(getFormHM());
				//关注人信息
				this.getFormHM().put("followerMap", mappings.get("followerMap"));
				//权重信息
				this.getFormHM().put("rankMap", mappings.get("rankMap"));
				//部门计划下的单位信息
				this.getFormHM().put("b0110Map", mappings.get("b0110Map"));
				errorcode = 0;
				this.getFormHM().put("errorcode", errorcode);

			} else if ("4".equals(type)) {
				int errorcode = bo.remind(this.getFormHM());
				this.getFormHM().put("errorcode", errorcode);

			} else if ("5".equals(type)) {
				int errorcode = bo.relate(this.getFormHM());
				this.getFormHM().put("errorcode", errorcode);

			} else if ("6".equals(type)) {
				int errorcode = bo.modifyP04(this.getFormHM());
                String planType = String.valueOf(this.getFormHM().get("plantype"));// 计划类型1：人员 2：团队
                //清除打分
                if(this.getFormHM().get("clearObjMap")!=null){
                    MorphDynaBean morphDynaBean = (MorphDynaBean)this.getFormHM().get("clearObjMap");
                    HashMap<String,List<String>> map = PubFunc.DynaBean2Map(morphDynaBean);
                    bo.clearMainBodyScore(map,planType);
                }
				this.getFormHM().put("errorcode", errorcode);
				
			} else if ("7".equals(type)) {
				int errorcode = bo.modifyP0733(this.getFormHM());
				this.getFormHM().put("errorcode", errorcode);

			} else if ("8".equals(type)) {
				HashMap<String, String> map = bo.getQueryNum(this.getFormHM());
				this.getFormHM().put("querynum", map);
			
			} else if ("9".equals(type)) {
				String plantype = (String)this.formHM.get("plantype");//个人 or 部门
				ArrayList<ColumnsInfo> columnsList = bo.getColumnsList(plantype);
				TableConfigBuilder builder = new TableConfigBuilder(WORKPLAN_HR_SUBMODULEID, columnsList, WORKPLAN_HR_SUBMODULEID, userView, this.getFrameconn());
				builder.setDataSql("select p0700 from p07 where 1=2");
				
				String orderby = " order by a0100";
				if("2".equals(plantype)) {
					orderby = " order by e0122";
				}
				builder.setOrderBy(orderby);
				builder.setTitle("1".equals(plantype)?"个人工作计划":"部门工作计划");
				builder.setAutoRender(true);
				builder.setColumnFilter(true);//统计过滤
				builder.setLockable(true);
				builder.setSelectable(true);
				builder.setEditable(false);
				builder.setTableTools(bo.getButtonList(plantype));
				builder.setPageSize(20);
				
				String config = builder.createExtTableConfig();
				this.getFormHM().put("tableConfig", config.toString());
				this.getFormHM().put("defaultQuery", bo.getDefaultQuery());
				boolean hasTheFunction = this.userView.hasTheFunction("0KR02020101");
				boolean canCreatePerson = this.userView.hasTheFunction("0KR02020102");//个人计划制定权限
				boolean canCreateOrg = this.userView.hasTheFunction("0KR02010201");//部门计划制定权限
				this.getFormHM().put("saveQuery", hasTheFunction); 
				this.getFormHM().put("canCreatePerson", canCreatePerson); 
				this.getFormHM().put("canCreateOrg", canCreateOrg); 
				
				// 设置tablesql模板
				String tableSql = bo.getTableSql(plantype);
				this.getUserView().getHm().put("workplanhr_tablsql", tableSql);
				TableDataConfigCache catche = (TableDataConfigCache) this.getUserView().getHm().get(WORKPLAN_HR_SUBMODULEID);
				catche.setQuerySql("");
				//获得填报期间范围
				WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(frameconn,userView);
	            List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
	            //启用的期间计划列表json格式
	            this.getFormHM().put("plantypejson",JSONArray.fromObject(configList).toString());
	            this.getFormHM().put("curUsername", userView.getUserName());
	            
			}else if("10".equals(type)) {
				bo.getPeriodParams(this.getFormHM());
				
			} else if("11".equals(type)) {
				HashMap resMap = bo.checkSuperBodyType(this.getFormHM());
				
				this.getFormHM().put("errorcode", resMap.get("errorcode"));
				this.getFormHM().put("info", resMap.get("info"));// errorcode=1时info为提示信息
				
			} else if("12".equals(type)) {
				HashMap resMap = bo.checkIsRelated(this.getFormHM());
				this.getFormHM().put("errorcode", resMap.get("errorcode"));
				this.getFormHM().put("objectName", resMap.get("objectName"));// errorcode=1时A0101s为已经关联过计划的人员名称
				this.getFormHM().put("objectCount", resMap.get("objectCount"));// errorcode=1时count为已经关联过计划的人数

			} else if ("13".equals(type)) {
				HashMap resMap = bo.checkTemplate(this.getFormHM());
				this.getFormHM().put("errorcode", resMap.get("errorcode"));
				this.getFormHM().put("taskDesc", resMap.get("taskDesc"));// errorcode=1时taskDesc为关联计划的模板名
			} else if("14".equals(type)) {//复杂查询
				ArrayList<MorphDynaBean> items = (ArrayList<MorphDynaBean>)this.getFormHM().get("items");
				int errorcode = bo.complexQuery(items);
				this.getFormHM().put("errorcode", errorcode);
			} else if("15".equals(type)) {//更新目标卡前，检查所选人员是否正在打分
                HashMap returnMap = bo.checkIsScoring(this.getFormHM());
                this.getFormHM().put("returnMap", returnMap);
            }

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
