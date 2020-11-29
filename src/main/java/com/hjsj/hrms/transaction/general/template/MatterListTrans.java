package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.businessobject.infor.PersonMatterTask;
import com.hjsj.hrms.businessobject.kq.interfaces.KqMatterTask;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class MatterListTrans extends IBusiness {


	public void execute() throws GeneralException {
		try{
		MatterTaskList matterTaskList=new MatterTaskList(this.getFrameconn(),this.userView);
		
		matterTaskList.setReturnflag("10");
		matterTaskList.setReturnURL("/general/template/matterList.do?b_query=link");
		matterTaskList.setTarget("_self");
		ArrayList matterList=matterTaskList.getPendingTask();  //new ArrayList();		20160513	dengc
		LazyDynaBean abean=new LazyDynaBean();
		CommonData cData=null;

		//招聘需求审批待办任务
		ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, this.userView);
		ArrayList zpdatalist = zpbo.getZpapprDta();
		for(int m = 0; m < zpdatalist.size();m++){
			CommonData zpdata = new CommonData();
			zpdata=(CommonData) zpdatalist.get(m);
			if (zpdata.getDataName() != null && zpdata.getDataName().length() > 0) {
				CommonData ZpapprDta = (CommonData) zpdatalist.get(m);
				ZpapprDta.setDataValue(ZpapprDta.getDataValue().replace("returnflag=8", "returnflag=10"));
				matterList.add(ZpapprDta);
			}
		}
		
		
		if("gw".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){//国家电网的代办，只要绩效
			matterList=matterTaskList.getScoreList(matterList); 
		}else{
		    //考勤刷卡数据待办、加班申请待办
		    KqMatterTask kqMatter = new KqMatterTask(this.frameconn, this.userView);
			matterList = kqMatter.getKqCardTaskMore(matterList);
			matterList = kqMatter.getKqOvertimeTask(matterList);
	    	ArrayList okrList =matterTaskList.getOKRPending(); 
	    	if(okrList!=null)
	    	{
	    		int okrCooperationTaskNum = 0;//okr协办任务计数
				CommonData okrCooperationTaskData = new CommonData();
	        	for(int i=0;i<okrList.size();i++)
	        	{        
	        		abean=(LazyDynaBean)okrList.get(i);
	        		String name = (String)abean.get("name");
	        		if("部门协作任务申请".equals(name)){//okr协作任务待办合并 chent 20160623
	        			okrCooperationTaskNum += 1;
	        			okrCooperationTaskData.setDataName(name+"("+okrCooperationTaskNum+")");
	        			okrCooperationTaskData.setDataValue((String)abean.get("url"));
	        			continue;
	        		}
	        		cData=new CommonData();
	        		cData.setDataName(name);
	        		cData.setDataValue((String)abean.get("url"));
	        		matterList.add(cData);
	        	}
	        	if(okrCooperationTaskNum > 0){
	        		matterList.add(okrCooperationTaskData);
	        	}
			}
			matterList = matterTaskList.getWorkPlanList(matterList);
	    	matterList=matterTaskList.getWaitTaskList(matterList);
	  //  	matterList=matterTaskList.getInstanceList(matterList); 
	    	matterList=matterTaskList.getTmessageList(matterList); 
	    	matterList=matterTaskList.getPerformancePending(matterList); 
	    	matterList=matterTaskList.getApproveList(matterList); //报表上报是否支持审批  zhaoxg 2012-2-18
	    	matterList=matterTaskList.getReturnList(matterList); //报表上报是否支持审批  zhaoxg 2013-3-7
	    	WorkdiarySelStr WorkdiarySelStr=new WorkdiarySelStr(); 
	    	WorkdiarySelStr.setReturnURL("/general/template/matterList.do?b_query=link");
			WorkdiarySelStr.setTarget("_self");
	    	matterList=WorkdiarySelStr.getLogWaittask(this.getFrameconn(), this.userView, matterList);
	    	SalaryPkgBo salaryPkgBo=new SalaryPkgBo(this.getFrameconn(),this.userView); 
//			ArrayList salarylist=salaryPkgBo.getEndorseRecords(); //审批薪资
			ArrayList salarylist=salaryPkgBo.getGzPending("list"); //审批薪资  读取待办表中数据   zhaoxg add 2014-7-25

	    	if(salarylist!=null)
	    	{
	        	for(int i=0;i<salarylist.size();i++)
	        	{        	
	        		
	        		abean=(LazyDynaBean)salarylist.get(i);
	        		cData=new CommonData();
	        		cData.setDataName((String)abean.get("name"));
	        		cData.setDataValue((String)abean.get("url"));
	        		matterList.add(cData);
	        	}
			}
	    	
	    	//人员信息变动 审核 代办
	    	ArrayList personChange = new PersonMatterTask(frameconn, userView).getPersonInfoChange();
	    	if(personChange!=null)
	    	{
	        	for(int i=0;i<personChange.size();i++)
	        	{        
	                CommonData commData= (CommonData)personChange.get(i); 
	                
	                CommonData cData1=new CommonData(); 
	                cData1.setDataName(commData.getDataName());
	                cData1.setDataValue(commData.getDataValue().replaceAll("portal", "tasklist") + "&returnflag=tasklist");	  
	        		matterList.add(cData1);
	        	}
			}

		}
    	this.getFormHM().put("matterList", matterList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
