package com.hjsj.hrms.module.muster.mustermanage.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 花名册管理交易类
 * @author Zhiyh
 *
 */
public class MusterManageMainTrans extends  IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			MusterManageService musterManageService = new MusterManageServiceImpl(this.frameconn,this.userView);
			String moduleID = (String) this.getFormHM().get("moduleID");//模块号，=0：员工管理；=1：组织机构；
			String musterType = (String) this.getFormHM().get("musterType");//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
			String musterManageStyleid = (String) this.getFormHM().get("musterManageStyleid");
			String currentPage = (String) this.getFormHM().get("currentPage");
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = musterManageService.getColumnList();
			/** 获取操作按钮*/
    		ArrayList buttonList = musterManageService.getButtonList(musterType);
    		//获取sql
    		String sql = musterManageService.getMusterMainSql(musterType,moduleID);
			musterManageService.updateMusterstyletype();
    		if (StringUtils.isNotBlank(musterManageStyleid)) {
    		    sql = sql+" and lname.styleid = '"+musterManageStyleid+"'";
            }
    		TableConfigBuilder builder = new TableConfigBuilder("musterManage", columnList, "musterManage001", userView, this.getFrameconn());
    		builder.setDataSql(sql);  
    		builder.setOrderBy(" order by tabid  ");
			if ("1".equals(musterType)) {
				builder.setTitle(ResourceFactory.getProperty("muster.emp"));
			}else if ("2".equals(musterType)) {
				builder.setTitle(ResourceFactory.getProperty("muster.un"));
			}else if ("3".equals(musterType)){
				builder.setTitle(ResourceFactory.getProperty("muster.post"));
			}else {
				builder.setTitle(ResourceFactory.getProperty("muster.basepost"));
			}
			builder.setCurrentPage(Integer.parseInt(currentPage));
			builder.setAutoRender(true);
			builder.setColumnFilter(true);
			builder.setLockable(true);
			builder.setSelectable(true);//是否有复选框列
			builder.setEditable(false);
			builder.setTableTools(buttonList);
			builder.setPageSize(20);	
			String config = builder.createExtTableConfig();
			String priv = musterManageService.getMusterPriv(moduleID);//当前用户的权限
			this.getFormHM().put("priv",priv);//权限
			this.getFormHM().put("tableConfig",config);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
