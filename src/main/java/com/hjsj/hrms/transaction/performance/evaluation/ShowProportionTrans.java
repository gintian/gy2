package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ShowProportionTrans.java</p>
 * <p>Decsription:绩效评估 计算 权重设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-11-23 10:24:08</p>
 * @author JinChunhai
 * @version 4.0
 */

public class ShowProportionTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String planid=(String)hm.get("planid");
			PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
			Hashtable ht=pe.getPlanParamSet();
			ArrayList weightList=pe.getWeightList(planid);
			String temp=(String)ht.get("UnLeadSingleAvg");
			String unLeadSingleAvg="0";
			String zeroflag = "1";
			String flag = "0";
			String lead = "0";
			
			if("1".equals(temp)){
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
			String zeroByNull = (String)hm.get("zeroByNull");
			if("True".equalsIgnoreCase(temp))
				unLeadSingleAvg="1";
			this.getFormHM().put("unLeadSingleAvg",unLeadSingleAvg);
			this.getFormHM().put("weightList",weightList);
			this.getFormHM().put("zeroByNull",zeroByNull);
			this.getFormHM().put("zeroflag",zeroflag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
