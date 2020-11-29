package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：InitRecruitBatchTrans 
 * 类描述：加载招聘批次
 * 创建人：sunming 
 * 创建时间：2015-10-27
 * 
 * @version
 */
public class InitRecruitBatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			String z0129 = (String) this.getFormHM().get("z0129");
			PositionBo pobo = new PositionBo(this.frameconn,new ContentDAO(this.frameconn),this.getUserView());
			pobo.getCodeItem();
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			boolean existPath = VfsService.existPath();
            if (!existPath) {
                throw new GeneralException("没有配置文件存放目录，请联系管理员！");
            }
			columnsInfo = bo.getColumnList();
			/** 获取sql */
			String sql = bo.getDataSql(z0129);
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("recruitbatch", columnsInfo, "recruitbatch001", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy(" order by z0101 ASC");
			builder.setAutoRender(true);
			builder.setTitle("招聘批次");
			builder.setSetScheme(true);
			builder.setScheme(true);
			builder.setColumnFilter(true);
			builder.setSelectable(true);
			builder.setEditable(true);
			builder.setPageSize(20);
			builder.setLockable(true);
			builder.setConstantName("recruitment/recruitBatch");
			builder.setTableTools(bo.getButtonList());
			builder.setRowdbclick("RecruitbatchGlobal.modifyRecruitBatch");
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			if(userView.hasTheFunction("3110007"))//公有栏目设置权限处理
			{				
				builder.setShowPublicPlan(true);
			}else{
				builder.setShowPublicPlan(false);
			}
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		}catch(Exception ex){
			 ex.printStackTrace();
			 throw GeneralExceptionHandler.Handle(ex);
		}	
	}

}
