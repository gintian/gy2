package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 项目名称 ：ehr7.x
 * 类名称：SearchExamHallTrans
 * 类描述：考场设置
 * 创建人： lis
 * 创建时间：2015-11-2
 */
public class SearchExamHallTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	try{
    		ExamHallBo bo = new ExamHallBo(this.frameconn,this.userView);
    		
			String flag = (String)this.getFormHM().get("flag");   //操作标识   1：招聘批次
			String batchId = (String)this.getFormHM().get("batchId");//批次id
			String isInit = (String)this.getFormHM().get("isInit");//是否是第一次进入
			if(StringUtils.isBlank(batchId)){
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");//快速查询参数
				try {
				    if(bean != null)
				        batchId = (String)bean.get("batchId");
                } catch (Exception e) {
                }
				
			}

			StringBuffer where = new StringBuffer("");
			RecruitBatchBo batchBo = new RecruitBatchBo(this.getFrameconn(), this.userView);
			
			if("1".equals(flag)){//得到招聘批次下拉框数据
				ArrayList batchInfos = new ArrayList();
				CommonData commonData = new CommonData();
    			commonData.setDataName("--- 查询全部  ---");
    			commonData.setDataValue("all");
				batchInfos.add(commonData);
				batchInfos.addAll(batchBo.getAllBatchInfos("5"));
				this.getFormHM().put("allBatch",batchInfos);
				return;
			}
			
			if("1".equals(isInit)){//第一次进入考场设置，默认显示第一个批次
				for(CommonData commondata:(ArrayList<CommonData>)batchBo.getAllBatchInfos("1")){
					batchId = commondata.getDataValue();
					where.append(" and hall.batch_id='" +batchId + "' ");
					break;
				}
			}else if(StringUtils.isNotBlank(batchId) && !"all".equals(batchId)){//招聘批次下拉框选择
				where.append(" and hall.batch_id='" +batchId + "' ");
			}
			
			//查询控件的查询条件
			String sqlCondition = (String) this.getFormHM().get("sqlCondition");
			
			// 查询类型，1为输入查询，2为方案查询
			String type = (String) this.getFormHM().get("type");
			 
			if("1".equals(type)) {
				//快速查询
				ArrayList<String> valuesList = new ArrayList<String>();
				// 输入的内容
				
				TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get("zp_exam_hall_id_001");
				String batchIds = cache.getCustomParamHM().get("batchId").toString();
				valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
				if(valuesList != null){
					StringBuffer querySql = new StringBuffer();
					sqlCondition = bo.getConditon(valuesList);
					querySql.append(sqlCondition);
					batchIds = batchIds.toLowerCase();
					if(!"all".equals(batchIds)){
						querySql.append("and myGridData.batch_id='"+batchIds+"'");
					}
					cache.setQuerySql(querySql.toString());
					return;
				}
			} 
//			if(StringUtils.isNotBlank(batchId) && !"all".equals(batchId)){//招聘批次下拉框选择
//				sqlCondition+=" and hall.batch_id='" +batchId + "' ";
//			}
			
			if(StringUtils.isNotBlank(sqlCondition))
				where.append(sqlCondition);
			
			/** 获取列头 */
			ArrayList columnList = bo.getColumnList();
            
			/** 获取当前用户单位操作权限 */
			RecruitPrivBo privBo = new RecruitPrivBo();
    		String priv = privBo.getPrivB0110Whr(this.userView, "b0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
    		
			/** 拼接sql*/
			StringBuffer sql = new StringBuffer("select * from (select batch_id, id as idx,hall_id,hall_name,hall_address,b0110,exam_date,exam_time,seat_num,people_num,seat_num-people_num as surplus_num,z0103 as batch_name from zp_exam_hall left join z01 on Z0101=batch_id) hall ");
			sql.append(" where " + priv);
			String orderBy = " order by hall_id ASC";
			
    		/** 获取操作按钮*/
    		ArrayList buttonList = bo.getButtonList();
    		
    		/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("zp_exam_hall_id_001", columnList, "zp_exam_hall", userView, this.getFrameconn());
			builder.setLockable(true);
			builder.setDataSql(sql + where.toString());
			builder.setOrderBy(orderBy);
			builder.setAutoRender(true);
			builder.setTitle("考场安排");
			builder.setSetScheme(true);
			builder.setEditable(false);
			builder.setConstantName("recruitment/examHallList");
			builder.setPageSize(20);
			builder.setTableTools(buttonList);
			builder.setSelectable(true);
			builder.setColumnFilter(true);
			builder.setScheme(true);
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			HashMap para = new HashMap();
			para.put("batchId", batchId);
			builder.setCustomParamHM(para);
			if(userView.isSuper_admin()||userView.hasTheFunction("311080707")){
				builder.setShowPublicPlan(true);
			}
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("batchId", batchId);
			this.getFormHM().put("sqlCondition", sqlCondition);
    	}catch(Exception e){
    		e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
