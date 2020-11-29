package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.CertificateManageBo;
import com.hjsj.hrms.module.certificate.manage.businessobject.ExportCertificateTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ExportCertificateTemplateTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String subModuleId = "certificateManage_001";
		String fileName = this.userView.getUserName() + "_证书信息模板.xls";
		try {
			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
			ArrayList<ColumnsInfo> columnsList = (ArrayList<ColumnsInfo>) catche.getDisplayColumns().clone();
			HashMap<String, ArrayList<String>> codeitemMap = new HashMap<String, ArrayList<String>>();
			CertificateManageBo bo = new CertificateManageBo(this.userView, this.frameconn);
			if (columnsList == null || columnsList.size() < 1)
				columnsList = bo.getColumns(subModuleId);

			CertificateConfigBo ccbo = new CertificateConfigBo(this.frameconn, this.userView);
			String certOrg = ccbo.getCertOrganization();
			for (int i = 0; i < columnsList.size(); i++) {
				ColumnsInfo column = (ColumnsInfo) columnsList.get(i);
				if (StringUtils.isEmpty(column.getColumnId()))
					continue;

				String codesetid = column.getCodesetId();
				if (!"0".equalsIgnoreCase(codesetid) && StringUtils.isNotEmpty(codesetid)
						&& ColumnsInfo.LOADTYPE_ONLYLOAD != column.getLoadtype()
						&& !certOrg.equalsIgnoreCase(column.getColumnId())) {
					String columnid = column.getColumnId();
					ArrayList<String> descList = bo.getItemDescList(codesetid);
					// 48382 筛选出空的下拉集合，防止导出excel报错
					if(null==descList || descList.size()<1) 
						continue;
					codeitemMap.put(columnid, descList);
				}

			}

			ArrayList<LazyDynaBean> itemList = bo.getTemplateHeadList(columnsList, certOrg);
//			ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
//			excelUtil.exportExcelBySql(fileName, null, null, itemList, "", codeitemMap, 0);
			ExportCertificateTemplateBo expotBo = new ExportCertificateTemplateBo();
			expotBo.creatSheet(fileName, itemList, codeitemMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
	}

}
