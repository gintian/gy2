package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveTrainTotalPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String selectIDs=(String)this.getFormHM().get("selectIDs");
		ArrayList planFieldList=(ArrayList)this.getFormHM().get("planFieldList");
		ContentDAO dao=null;
		try
		{
			String r2501="";
		    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            dao=new ContentDAO(this.getFrameconn());
            RecordVo vo=new RecordVo("r25");
            r2501=idg.getId("R25.R2501");
            vo.setString("r2501",r2501);
            vo.setDate("r2508",new Date());
            vo.setString("r2509","03");
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
            dao.addValueObject(vo);
            
            if(selectIDs!=null&&selectIDs.trim().length()>1)
            {
	            selectIDs =selectIDs.substring(1);
	            String[] ids=selectIDs.split("\\^");
	            StringBuffer idstr=new StringBuffer("");
	            for(int i=0;i<ids.length;i++)
	            {
	            	idstr.append(",'"+ids[i]+"'");
	            }
	            String ss="update r31 set r3125='"+r2501+"' where r3101 in ("+idstr.substring(1)+")";
	            dao.update("update r31 set r3125='"+r2501+"' where r3101 in ("+idstr.substring(1)+")");
            }
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
