package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class SavaLeaderOrExportSchemeTrans extends IBusiness{

    private static final long serialVersionUID = 1L;

    @Override
	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type"); // 操作类型
		SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
		if (StringUtils.equals("saveLeader", type)) {//保存上报负责人
			String leader = (String) this.getFormHM().get("leader"); 
			String schemeid = (String) this.getFormHM().get("schemeid"); 
			bo.saveSelectLeader(schemeid, leader);
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("setupscheme");
            ArrayList<LazyDynaBean> dataList = bo.getSchemeData();
            tableCache.setTableData(dataList);
		}else if (StringUtils.equals("delLeader", type)) {
		    String schemeid = (String) this.getFormHM().get("schemeid"); 
		    schemeid = SafeCode.decode(schemeid);
		    bo.delLeader(schemeid);
		    TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("setupscheme");
		    ArrayList<LazyDynaBean> dataList = bo.getSchemeData();
		    tableCache.setTableData(dataList);
        }
	}

}
