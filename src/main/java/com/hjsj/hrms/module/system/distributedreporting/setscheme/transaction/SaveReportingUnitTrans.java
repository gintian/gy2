package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * @version: 1.0
 * @Description: 保存选中的上报单位，如果上一次已经定义的上报单位本次未选中则删除
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:55:41
 */
public class SaveReportingUnitTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(userView,this.frameconn);
			String unitguid=(String) this.getFormHM().get("orgList");//存单位
			String unitcodeList = (String) this.getFormHM().get("unitcodeList");//存单位
			String removeList=(String) this.getFormHM().get("removeList");//存要删除的单位
			String removeCodeList=(String) this.getFormHM().get("removeCodeList");//存要删除的单位
			String savePath = (String) this.getFormHM().get("savePath");//文件存储路径

			if (StringUtils.isNotBlank(savePath)) {
				String saveFilePathMsg = FileUtil.saveFilePath(savePath, this.frameconn);
				//路径不合规范
				if (!StringUtils.equalsIgnoreCase("success", saveFilePathMsg)) {
					this.formHM.put("saveFilePathMsg", saveFilePathMsg);
					return;
				}
			}
			if (!"".equals(removeList)) {
				//删除上报单位对应的导入方案表t_sys_asyn_scheme，过滤记录方案表t_sys_asyn_filtercondition ，代码映射表t_sys_asyn_code 中的记录
				bo.delReportingUnit(removeCodeList,removeList);
			}
			if (StringUtils.isNotEmpty(unitguid)) {
			  //1、将单位保存到数据库
	            String[] guidArray =unitguid.split(",");
	            String[] unitcodeArray = unitcodeList.split(",");
	            for(int i=0;i<guidArray.length;i++) {
	                String guid=  guidArray[i];
	                String unitcode=  unitcodeArray[i].split(":")[1];
	                
	                //2、查询单位在表中是否存在
	                if (!bo.getOrg(guid)&&!"".equals(guid)&&null!=guid) {
	                    int Schemeid=bo.getMaxSchemeid();
	                    bo.addReportingUnit(Schemeid,guid,unitcode);
	                }
	            }
            }
			 TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("setupscheme");
             ArrayList<LazyDynaBean> dataList = bo.getSchemeData();
             tableCache.setTableData(dataList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
