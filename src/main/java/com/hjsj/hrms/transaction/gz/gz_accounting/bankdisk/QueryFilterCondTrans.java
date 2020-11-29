package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class QueryFilterCondTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String model=this.getFormHM().get("model")!=null?(String)this.getFormHM().get("model"):"";//history 表示为薪资历史数据分析进入
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			ArrayList condlist=new ArrayList();
			if("history".equalsIgnoreCase(model)){

				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				ArrayList temp=hbo.getServiceItemListFromHistory();
				for(int i=0;i<temp.size();i++)
				{
					CommonData cd=(CommonData)temp.get(i);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("condid",cd.getDataValue());
					bean.set("name",cd.getDataName());
					//特殊处理：这里是读取的信息，超级用户能读旧信息
					condlist.add(bean);
				}
			}else{
				condlist=bo.getFilterCondBeanList(salaryid,this.userView);
			}

			this.getFormHM().put("condbeanlist",condlist);
			this.getFormHM().put("condsize",condlist.size()+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
