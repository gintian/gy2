package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchSelfResultTrans.java</p>
 * <p>Description>:SearchSelfResultTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-10 上午11:06:28</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchSelfResultTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String model=(String)map.get("model");
			String distinctionFlag=(String)map.get("distinctionFlag");
			String opt=(String)map.get("opt");
			String isSelf=(String)map.get("isSelf");
			//添加绩效、能力素质区分  chent 20151210 start
			String busitype=(String)map.get("busitype");//0：绩效 1：能力素质
			//添加绩效、能力素质区分  chent 20151210 start
			ResultBo bo = new ResultBo(this.getFrameconn());
			 if((this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))&& "0".equals(model))
			        throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			 String object_id="";
			if("1".equals(opt))
			{
    			if("0".equals(model)){
	    			object_id=this.userView.getA0100();
    			}else {
    				object_id=PubFunc.decryption((String)map.get("a0100"));
    			}
			}
			else
			{
				object_id=(String)map.get("a0100");
			}
			String year="-1";
			if(map.get("year")!=null)
			{
				year = (String)map.get("year");
				map.remove("year");
			}
			ArrayList planList = bo.getPlanList(object_id, distinctionFlag,year,this.userView, busitype);
			ArrayList yearList = bo.getYearList(object_id, distinctionFlag);
			this.getFormHM().put("planList", planList);
			this.getFormHM().put("model",model);
			this.getFormHM().put("distinctionFlag", distinctionFlag);
			this.getFormHM().put("object_id",object_id);
			this.getFormHM().put("performanceYear", year);
			this.getFormHM().put("performanceYearList", yearList);
			this.getFormHM().put("busitype", busitype);
		}
		
		catch(Exception e)
		{
		     e.printStackTrace();
		     throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
