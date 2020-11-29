package com.hjsj.hrms.transaction.report.report_collect;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 15, 2006:2:17:54 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SetComplexConditionCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String[] right_fields=(String[])this.getFormHM().get("right_fields");
		ArrayList   selectedParamList=new ArrayList();
		ArrayList   rightFieldsList=new ArrayList();
		
		for(int i=0;i<right_fields.length;i++)
		{
			String temp=right_fields[i];
			String[] array=temp.split("§§");
			DynaBean bean = new LazyDynaBean();
			bean.set("paramename",array[0]);
			bean.set("paramname",array[1]);
			bean.set("paramCode",array[2]);
			bean.set("paramscope",array[3]);
			bean.set("sortid",array[4]);
			
			rightFieldsList.add(bean);
			selectedParamList.add(bean);
		}
		
		this.getFormHM().put("rightFieldsList",rightFieldsList);
		this.getFormHM().put("selectedParamList",selectedParamList);

	}

}
