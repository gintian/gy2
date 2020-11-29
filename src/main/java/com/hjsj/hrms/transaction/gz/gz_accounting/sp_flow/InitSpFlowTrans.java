package com.hjsj.hrms.transaction.gz.gz_accounting.sp_flow;

import com.hjsj.hrms.businessobject.gz.GzSpFlowBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InitSpFlowTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String gz_module="0";
			if(map.get("gz_module")!=null)
			{
				gz_module=(String)map.get("gz_module");
				map.remove("gz_module");
			}
			else
				gz_module=(String)this.getFormHM().get("gz_module");
			String init=(String)map.get("init");
			String binit=(String)map.get("binit");
			String cinit=(String)map.get("cinit");
			
			GzSpFlowBo bo = new GzSpFlowBo(this.getFrameconn(),this.getUserView(),Integer.parseInt(gz_module));
			ArrayList salaryList = bo.getSpSalarySetList();
			String salaryid="-1";
			String hasData="0";
			if("first".equalsIgnoreCase(init)&&salaryList.size()>0)
			{
				CommonData cd = (CommonData)salaryList.get(0);
				salaryid=cd.getDataValue();	
			}else if(salaryList.size()<=0)
			{
				hasData="1";
			}
			else
			{
				salaryid=(String)this.getFormHM().get("salaryid");
			}
			if("0".equals(hasData))
			{
	    		String busiDate="-1";
		    	ArrayList busiDateList=bo.getBusiDateList(salaryid);
		    	if("first".equalsIgnoreCase(binit)&&busiDateList.size()>0)
		    	{
			    	CommonData cd = (CommonData)busiDateList.get(0);
			    	busiDate=cd.getDataValue();
		    	}
		    	else
		    	{
		    		busiDate=(String)this.getFormHM().get("busiDate");
		    	}


		    	/*String count="-1";
		    	ArrayList countList = bo.getCountList(salaryid, busiDate);
		    	if(cinit.equalsIgnoreCase("first")&&countList.size()>0)
		    	{
		    		CommonData cd = (CommonData)countList.get(0);
		    		count=cd.getDataValue();
		    	}
		    	else
		    	{
		    		count=(String)this.getFormHM().get("count");
		    	}*/
		    	ArrayList spDataList=bo.getSpDataList(salaryid, busiDate);//, count
		    	HashSet nameSet = new HashSet();
		    	HashSet spSet = new HashSet();
		    	HashSet curSet = new HashSet();
		    	for(int i=0;i<spDataList.size();i++){
		    		LazyDynaBean bean = (LazyDynaBean) spDataList.get(i);
		    		nameSet.add(bean.get("admin"));
		    		spSet.add(bean.get("sp_flag"));
		    		curSet.add(bean.get("curr_oper"));
		    	}
		    	String UsrName="-1";
		    	ArrayList UsrNameList=bo.getUsrNameList(nameSet);
		    	UsrName=(String)this.getFormHM().get("usrName");
		    	if(UsrName==null|| "".equals(UsrName)){
		    		UsrName="-1";
		    	}
			    String SpFlag="-1";
			    ArrayList SpFlagList=bo.getSpFlagList(spSet);
			    SpFlag=(String)this.getFormHM().get("spFlag");
		    	if(SpFlag==null|| "".equals(SpFlag)){
		    		SpFlag="-1";
		    	}	
			    String curr="-1";
			    ArrayList currList=bo.getcurrList(curSet);
			    curr=(String)this.getFormHM().get("curr");
		    	if(curr==null|| "".equals(curr)){
		    		curr="-1";
		    	}	
		    	ArrayList _spDataList=new ArrayList();
		    	for(int i=0;i<spDataList.size();i++){
		    		boolean flag1 = false;
		    		boolean flag2 = false;
		    		boolean flag3 = false;
		    		LazyDynaBean bean = (LazyDynaBean) spDataList.get(i);
		    		if(UsrName.equals(bean.get("admin"))|| "-1".equals(UsrName)){
		    			flag1=true;
		    		}
		    		if(SpFlag.equals(bean.get("sp_flag"))|| "-1".equals(SpFlag)){
		    			flag2=true;
		    		}
		    		if(curr.equals(bean.get("curr_oper"))|| "-1".equals(curr)){
		    			flag3=true;
		    		}
		    		if(flag1&&flag2&&flag3){
		    			_spDataList.add(spDataList.get(i));
		    		}else if("-1".equals(UsrName)&& "-1".equals(SpFlag)&& "-1".equals(curr)){
		    			_spDataList.add(spDataList.get(i));
		    		}
		    	}
		    	this.getFormHM().put("spDataList", _spDataList);
		    	this.getFormHM().put("salaryList", salaryList);
		    	this.getFormHM().put("salaryid", salaryid);
		    	this.getFormHM().put("busiDateList", busiDateList);
		    	this.getFormHM().put("busiDate", busiDate);
		    	this.getFormHM().put("usrNameList", UsrNameList);
		    	this.getFormHM().put("usrName", UsrName);
		    	this.getFormHM().put("spFlagList", SpFlagList);
		    	this.getFormHM().put("spFlag", SpFlag);
		    	this.getFormHM().put("currList", currList);
		    	this.getFormHM().put("curr", curr);
		    	this.getFormHM().put("gz_module", gz_module);
		    	/*this.getFormHM().put("count", count);
		    	this.getFormHM().put("countList", countList);*/
			}
			this.getFormHM().put("hasData", hasData);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
