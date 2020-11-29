package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
public class SaveProportionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String planid=(String)this.getFormHM().get("planid");
			PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
			ArrayList weightList=(ArrayList)this.getFormHM().get("weightList");
			String UnLeadSingleAvg=(String)this.getFormHM().get("unLeadSingleAvg");
			String zeroflag = "1";
			String flag = "0";
			String lead = "0";
			
			if("1".equals(UnLeadSingleAvg)){
				zeroflag="0";
			} else {
				for(int i=0;i<weightList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)weightList.get(i);
					if(bean.get("flag") !=null && ("1".equals(bean.get("flag")) || "0".equals(bean.get("flag"))) )
						flag = (String) bean.get("flag");
					if(bean.get("lead") !=null && ("1".equals(bean.get("lead")) || "0".equals(bean.get("lead"))) )
						lead=(String) bean.get("lead");
					if("1".equals(flag) || "1".equals(lead)){
						zeroflag="0";
						break;
					}
				}
			}
			if("1".equals(UnLeadSingleAvg))
				UnLeadSingleAvg="True";
			else
				UnLeadSingleAvg="False";
			pe.saveProportionValue(weightList,UnLeadSingleAvg);
			this.getFormHM().put("zeroflag",zeroflag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
