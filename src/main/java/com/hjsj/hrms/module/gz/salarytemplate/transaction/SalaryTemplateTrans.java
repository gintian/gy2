package com.hjsj.hrms.module.gz.salarytemplate.transaction;

import com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 薪资发放、薪资审批、薪资上报  主界面初始化
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 *
 */
public class SalaryTemplateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try {
			SalaryTemplateBo salaryPayListBo = new SalaryTemplateBo(this.getFrameconn(), this.userView);// 工具类
			
			String url = "";
			if(!StringUtils.isEmpty((String)this.getFormHM().get("url"))){
				url = (String)this.getFormHM().get("url");
			}else{//快速查询
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
				url = (String)bean.get("url");
			}
			
			String viewtype = salaryPayListBo.getValByStr(url, "viewtype");// 页面区分 0:薪资发放  1:审批  2:上报
			String imodule = salaryPayListBo.getValByStr(url, "imodule");// 0:薪资  1:保险
			String currentPage = salaryPayListBo.getValByStr(url, "currentPage");//定位页码
			// 模块id
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = (String) this.getFormHM().get("subModuleId");
			if("gz_salaryTemplate_00000001".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
				}
			}
			
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
			columnsInfo = salaryPayListBo.getColumnList(viewtype, imodule);
			
			/** 获取数据 */
			ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
			dataList = salaryPayListBo.getDataList(viewtype, imodule, valuesList);
			
			/** 加载表格 */
			TableConfigBuilder builder = new TableConfigBuilder("salarytemplate", columnsInfo, "salarypay", userView, this.getFrameconn());
			builder.setCurrentPage(Integer.parseInt(currentPage));
			builder.setDataList(dataList);
			builder.setAutoRender(true);
			builder.setPageSize(15);
			builder.setRowdbclick("salarypay_me.rowdbclick");
			String title = salaryPayListBo.getTitle(viewtype, imodule);
			builder.setTitle(title);
			builder.setSetScheme(false);
//			builder.setPageSize(20);
			builder.setTableTools(new ArrayList());
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
