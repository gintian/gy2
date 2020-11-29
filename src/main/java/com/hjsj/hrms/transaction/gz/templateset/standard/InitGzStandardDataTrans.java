package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:初始化工资指标数据</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 23, 2007:2:42:41 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitGzStandardDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String operate=(String)hm.get("operate");
			hm.remove("operate");
			String  uid=(String)hm.get("uid");
			
			SalaryStandardBo salaryStandardBo=new SalaryStandardBo(this.getFrameconn());
			
			String item=uid;
			String item_id="";
			String description="";
			String type="N";  // N,D
			String lowerValue="";
			String lowerOperate="";
			ArrayList lowerOperateList=salaryStandardBo.getLowerOperateList();
			
			String heightValue="";
			String heightOperate="";
			ArrayList heightOperateList=new ArrayList();
			
			String middleValue="";
			ArrayList middleValueList=new ArrayList();
			String isAccuratelyDay="0";  //是否精确到天
			
			FieldItem itemvo=DataDictionary.getFieldItem(uid);
			type=itemvo.getItemtype();
			if("D".equals(type))
			{
				heightOperateList=salaryStandardBo.getHeightOperateList();
				middleValueList=salaryStandardBo.getMiddleValueList(itemvo.getItemid().toUpperCase(),itemvo.getItemdesc());
			}
			else 
			{
				heightOperateList=salaryStandardBo.getLowerOperateList();
				middleValue=itemvo.getItemdesc();
			}
			if("1".equals(operate))
			{
				item_id=(String)hm.get("item_id");
				RecordVo vo=salaryStandardBo.getStandardDataVo(item,item_id);
				description=vo.getString("description");
				lowerValue=salaryStandardBo.getFactorValue("0",vo.getString("factor"),type,item);
				lowerOperate=salaryStandardBo.getFactorValue("2",vo.getString("factor"),type,item);
				heightOperate=salaryStandardBo.getFactorValue("3",vo.getString("factor"),type,item);
				String[] temp=vo.getString("factor").split("\\|");
				if(temp.length>3){//后面写“无”了，那么这个没必要读，否则可能报错
					heightValue=salaryStandardBo.getFactorValue("1",vo.getString("factor"),type,item);

				}
				
				
				if("D".equals(type))
				{
					middleValue=salaryStandardBo.getFactorValue("4",vo.getString("factor"),type,item);
					isAccuratelyDay=salaryStandardBo.getFactorValue("5",vo.getString("factor"),type,item);
				}
				
			}
			
			
			
			
			
			this.getFormHM().put("item",item);
			this.getFormHM().put("lowerOperateList",lowerOperateList);
			this.getFormHM().put("heightOperateList",heightOperateList);
			this.getFormHM().put("lowerValue",lowerValue);
			this.getFormHM().put("lowerOperate",lowerOperate);
			this.getFormHM().put("heightValue",heightValue);
			this.getFormHM().put("heightOperate",heightOperate);
			this.getFormHM().put("item_id",item_id);
			this.getFormHM().put("description",description);
			this.getFormHM().put("type",type);
			this.getFormHM().put("middleValue",middleValue);
			this.getFormHM().put("middleValueList",middleValueList);
			this.getFormHM().put("isAccuratelyDay",isAccuratelyDay);
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	
	
}
