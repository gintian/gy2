package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;

public class EditPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList planFieldList=(ArrayList)this.getFormHM().get("planFieldList");
		ContentDAO dao=null;
		try
		{
			
            dao=new ContentDAO(this.getFrameconn());
            RecordVo vo=new RecordVo("r25");         
			for(int i=0;i<planFieldList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)planFieldList.get(i);
				if(abean.get("value")!=null&&((String)abean.get("value")).trim().length()>=1)
				{
					String itemtype=(String)abean.get("itemtype");
					String itemid=((String)abean.get("itemid")).toLowerCase();
					String value=((String)abean.get("value")).trim();
					String decimalwidth=(String)abean.get("decimalwidth");
					if("A".equals(itemtype)|| "M".equals(itemtype))
						vo.setString(itemid,value);
					else if("N".equals(itemtype))
					{
						if("0".equals(decimalwidth))
						{
							if(value.indexOf(".")!=-1)
								value=value.substring(0,value.indexOf("."));
							vo.setInt(itemid,Integer.parseInt(value));
						}
						else
							vo.setDouble(itemid,Double.parseDouble(value));
					}
					else if("D".equals(itemtype))
					{
						String[] temp=value.split("-");
						Calendar a=Calendar.getInstance();
						a.set(Calendar.YEAR,Integer.parseInt(temp[0]));
						a.set(Calendar.MONTH,Integer.parseInt(temp[1]));
						a.set(Calendar.DATE,Integer.parseInt(temp[2]));
						vo.setDate(itemid,a.getTime());
					}
				}
			}
			
			//培训预算
			TrainBudgetBo tbb = new TrainBudgetBo(this.getFrameconn());
			if(tbb.getBudget()!=null&&tbb.getBudget().length()>0){
				if("03".equals(vo.getString("r2509"))||"09".equals(vo.getString("r2509")))
					tbb.updateTrainPlanBudget("1", vo.getString("r2501"), vo.getDouble("r2506"), false);
		    }
			
            dao.updateValueObject(vo);
            TrainPlanBo trainPlanBo=new TrainPlanBo(this.getFrameconn());
            ArrayList trainPlanList=trainPlanBo.getTrainPlanList(this.userView);
            this.getFormHM().put("trainPlanList",trainPlanList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}

}
