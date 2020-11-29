package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.WebServiceBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 上报方式及导出方案交易类
 * @author zhiyh
 * @date 2019-05-28 15:11:44
 */
public class CreateInputPlanTrans extends IBusiness {


	@Override
	public void execute() throws GeneralException {
		try {
			//数据库配置
			String operationtype = (String) this.getFormHM().get("type");// 测试数据库连接还是保存方案
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			if("testdbconnection".equals(operationtype)) {//测试数据库连接
				this.getFormHM().put("issucceed", bo.testdbconnection(formHM));
			}else if ("savescheme".equals(operationtype)) {//保存上报方式
			    boolean result =  bo.savescheme(formHM);
			    TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("setupscheme");
                ArrayList<LazyDynaBean> dataList = bo.getSchemeData();
                tableCache.setTableData(dataList);
				this.getFormHM().put("issucceed",result);
			}else if ("testftpconnection".equals(operationtype)) {//测试ftp连接
				this.getFormHM().put("issucceed", bo.testftpconnection(formHM));
			}else if ("testwsdlconnection".equals(operationtype)) {//测试wsdl连接
				this.getFormHM().put("issucceed", WebServiceBo.testWsdlConnection((String) this.getFormHM().get("serviceUrl")));
			}else if ("exportScheme".equals(operationtype)) {//导出方案
				//1、先检验上报方式是否已定义
				boolean flag = bo.checkSchemeparam(formHM);
				//2、检测是否有方案导出文件存储路径
				String filePath = FileUtil.getSaveFilePath();
				//未定义上报方式
				if (!flag) {
					this.getFormHM().put("flag", false);
				} else if (StringUtils.isBlank(filePath)) {
					//没有文件存储路径
					this.getFormHM().put("flag", true);
					this.getFormHM().put("filePath", filePath);
				} else {
					//导出方案
					String filename = bo.exportScheme(formHM);
					this.getFormHM().put("filename", filename);
					this.getFormHM().put("filePath", filePath);
					this.getFormHM().put("flag", true);
				}
			}else if ("getschemeparam".equals(operationtype)) {//回显上报方式
				boolean haveSchemeFlag = bo.existenceStandard();
				if (haveSchemeFlag) {//已定义数据标准
					String id = (String) this.getFormHM().get("schemeid"); // 方案编号
					HashMap<String, String> dbConfigMap = bo.getSchemeparam(id);
					this.getFormHM().put("dbconfig", dbConfigMap);
					this.getFormHM().put("sflag", true);
				}else {//未定义数据标准
					this.getFormHM().put("sflag", false);
				}
			}
		} catch (Exception e) {
			this.getFormHM().put("issucceed", false);
			e.printStackTrace();
		} 
	}
}
