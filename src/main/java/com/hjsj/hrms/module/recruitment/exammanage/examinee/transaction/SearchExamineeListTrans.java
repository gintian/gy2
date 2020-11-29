package com.hjsj.hrms.module.recruitment.exammanage.examinee.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.json.JSONArray;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>
 * Description:查询考生考场详细信息列表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-10-27 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class SearchExamineeListTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
    		
			String flag = (String)this.getFormHM().get("flag");   //搜索位置标识      2：查询栏    3、招聘批次
			String searchStr=(String)this.getFormHM().get("searchStr"); 
			String batchId=(String)this.getFormHM().get("batchId"); //批次
			String path=(String)this.getFormHM().get("path"); //区分是从左侧菜单查询还是其他的路径(左侧菜单和栏目设置保存进来的时候该值为null，其他的未notMenu)
			String subModuleId=(String)this.getFormHM().get("subModuleId"); //区分是从左侧菜单查询还是其他的路径(左侧菜单和栏目设置保存进来的时候该值为null，其他的未notMenu)
			
			//解决点击栏目设置或导入成绩后页面查询的数据又是最近批次问题
			if(!StringUtils.isEmpty(batchId) || !StringUtils.isEmpty(path))
				this.userView.getHm().put("batchId", batchId);
			else if(StringUtils.isEmpty(path)){
				batchId = (String) this.userView.getHm().get("batchId");
			}
			
			/**
			 * 获取招聘批次
			 */
			RecruitBatchBo batchBo = new  RecruitBatchBo(this.frameconn, this.userView);
			ArrayList batchList = batchBo.getAllBatchInfos("1");

			if(batchList.size()>0){
				//处理首次进入页面时查询最近招聘批次考生问题
				if(StringUtils.isEmpty(batchId)){
					CommonData tem = (CommonData) batchList.get(0);
					batchId = tem.getDataValue();
					this.userView.getHm().remove("batchId");
				}
				
			}
			//招聘批次下拉框数据
			CommonData all = new  CommonData("all", "--查询全部--");
			batchList.add(0, all);
			
			bo.isHasSubjects();
			
			/** 获取列头 */
			ArrayList columnList = bo.getColumnList();
			
			/** 拼接sql*/
			String sql = bo.getExamineeSql(columnList);
			/**条件**/
			String sqlCondition = bo.getWhere(flag, searchStr ,batchId);
			
    		/** 获取操作按钮*/
    		ArrayList buttonList = bo.getButtonList();
    		String querySql = "";
    		String fastQuerySql = "";
    		HashMap<String, String> customParamHM = new HashMap<String, String>();
    		if("zp_exam_assignList".equals(subModuleId)){
    			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_exam_assignList");
    			customParamHM = tableCache.getCustomParamHM();
    			querySql = (String) tableCache.getCustomParamHM().get("pubQuerySql");
    			fastQuerySql = (String) tableCache.getCustomParamHM().get("fastQuerySql");
    		}
    		/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("zp_exam_assignList", columnList, "zp_exam_assign", userView, this.getFrameconn());
			builder.setDataSql(sql+sqlCondition);
			builder.setOrderBy(" order by z6301 ASC,z0321 ASC,z0325 ASC,z0351 ASC");
			builder.setEditable(true);
			builder.setAutoRender(true);
			builder.setTitle("考生管理");
			builder.setSetScheme(true);
			builder.setConstantName("recruitment/examineeList");
			builder.setPageSize(20);
			builder.setTableTools(buttonList);
			builder.setSelectable(true);
			builder.setScheme(true);
			builder.setColumnFilter(true);//启用过滤
			builder.setCustomParamHM(customParamHM);
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			builder.setFieldAnalyse(true);//代码型指标增加统计功能
			if(userView.isSuper_admin()||userView.hasTheFunction("311080710")){
				builder.setShowPublicPlan(true);
			}
			String config = builder.createExtTableConfig();
			
			//招聘批次下拉框数据
			String batchJson = "";
			if(batchList.size()>0)
				batchJson = JSONArray.fromObject(batchList).toString();
			
			ArrayList examSub = bo.getAllExam("Z63");
			String examJson = "";
			if(examSub.size()>0)
				examJson = JSONArray.fromObject(examSub).toString();
			
			this.getFormHM().put("batchList", batchJson);
			this.getFormHM().put("batchId", batchId);
			this.getFormHM().put("examJson", examJson);
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("defaultQuery", bo.getDefaultQuery());
			this.getFormHM().put("optionalQuery", bo.getOptionalQuery());
			this.getFormHM().put("hasTheFunction", this.userView.hasTheFunction("311080710"));
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_exam_assignList");
			querySql = StringUtils.isEmpty(querySql)?"":querySql;
			fastQuerySql = StringUtils.isEmpty(fastQuerySql)?"":fastQuerySql;
			tableCache.setQuerySql(querySql+fastQuerySql);
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
