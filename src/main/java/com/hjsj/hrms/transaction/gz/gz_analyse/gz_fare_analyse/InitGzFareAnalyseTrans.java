package com.hjsj.hrms.transaction.gz.gz_analyse.gz_fare_analyse;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFareAnalyseBo;
import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class InitGzFareAnalyseTrans extends IBusiness{

	public void execute() throws GeneralException {
		String _info="";
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			GzFareAnalyseBo gbo= new GzFareAnalyseBo(this.getFrameconn());
			String chartkind=(String)this.getFormHM().get("chartkind");
			if(chartkind==null|| "".equals(chartkind)){//用于页签定位，即：切换组织机构页签不改变，首次进入为第一个页签  为3  zhaoxg 2013-4-28
				chartkind = "3";
			}
			String opt=(String)hm.get("opt");
			String planItemDesc=(String)this.getFormHM().get("planItemDesc");
			String type="1";
			if(hm.get("type")!=null)
			{
			   type=(String)hm.get("type");
			   hm.remove("type");
			}
			String code ="";
			if("init".equals(opt))
					code=(String)hm.get("a_code");
			else
				code=(String)this.getFormHM().get("code");
			String ctrl_type=(String)this.getFormHM().get("ctrl_type");
			GzAmountXMLBo bo =  new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			String setid=(String)map.get("setid");
			if(map==null||setid==null)
			{
				_info="薪资总额参数未定义";
				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
				
			}
			String charttype="1";
			if(this.getFormHM().get("charttype")!=null)
			{
				charttype=(String)this.getFormHM().get("charttype");
			}
			if(hm.get("charttype")!=null)
			{
				charttype=(String)hm.get("charttype");
				hm.remove("charttype");
			}
    			ArrayList itemlist = (ArrayList)map.get("ctrl_item");
    			if(itemlist==null||itemlist.size()==0)
    			{
    				_info="薪资总额参数未定义";
    				throw GeneralExceptionHandler.Handle(new Exception("薪资总额参数未定义"));
    			}
    			ArrayList planitemlist =getPlanitemList(itemlist);
    			LazyDynaBean bean = (LazyDynaBean)itemlist.get(0);
    			String planitemid="";
    			String realitem="";
    			String balanceitem="";
    			if(planitemlist==null||planitemlist.size()==0)
    			{
    				_info="您没有权限查看薪资总额参数中定义的参数指标";
    				throw GeneralExceptionHandler.Handle(new Exception("您没有权限查看薪资总额参数中定义的参数指标"));
    			}
	    		planitemid = (String)this.getFormHM().get("planitemid");
		    	String yearf=(String)this.getFormHM().get("yearf");
		    	/*if(yearf==null||yearf.equals(""))
	        		yearf=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));*/
		    	String option=(String)hm.get("option");
		    	if("0".equals(type))
		    	{
		    		yearf=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		    	}
		    	if("3".equals(chartkind)|| "5".equals(chartkind))
		    	{
		    		planitemlist=gbo.getItemListByClass(itemlist, userView);
		    	}
		    	if("one".equalsIgnoreCase(option)&&planitemlist.size()>0)
		    	{
	    			planitemid = ((CommonData)planitemlist.get(0)).getDataValue();
	    			planItemDesc=((CommonData)planitemlist.get(0)).getDataName();
	    		}
		    	if("3".equals(chartkind)|| "5".equals(chartkind))
    			{
    				String[] arr=planitemid.split("`");
    				chartkind=arr[0];
    				planitemid=arr[1];
    			}
	    		HashMap p=bo.getCtrl_itemMap(planitemid);
	    		realitem = (String)p.get("realitem");
	    		balanceitem=(String)p.get("balanceitem");
	    		/**累计实发与全年剩余总额分析,chartkind=1*/
	    		if("1".equals(chartkind))
	    		{
	        		ArrayList btlist = gbo.getBtlist(setid, planitemid,realitem,balanceitem, yearf, code);
	        		this.getFormHM().put("btlist",btlist);
	    		}
	    		/**计划总额,实发总额,剩余额对比分析 chartkind=2*/
	    		if("2".equals(chartkind))
	    		{
	        		ArrayList ztlist = gbo.getZtlist(setid,planitemid,realitem,balanceitem, yearf, code);
	        		this.getFormHM().put("ztlist",ztlist);
	    		}
	    		/**计划完成情况分析，chartkind=4，*/
	    		if("4".equals(chartkind))
	    		{
	    			if("2".equals(charttype))
	    	   		{
	    	    		HashMap mp = gbo.getXtlist(setid, planitemid,realitem,balanceitem, yearf, code,1);
	    	    		this.getFormHM().put("xtmap",mp);
	    	   		}
	    			else
	    			{
	    				HashMap mp = gbo.getXtlist(setid, planitemid,realitem,balanceitem, yearf, code,3);
	    				ArrayList xtlist = (ArrayList)mp.get("list");
			        	this.getFormHM().put("xtlist",xtlist);
	    			}
	    		
	    		}
	    		if("3".equals(chartkind))
	    		{
	        		HashMap lmp = gbo.getXtlist(setid, planitemid,realitem,balanceitem, yearf, code,2);
	        		/**工资计划完成情况分析 chartkind=3*/
		            ArrayList ltlist = (ArrayList)lmp.get("list");
		        	this.getFormHM().put("ltlist",ltlist);
	    		}
	    		if("5".equals(chartkind))
	    		{
	    			ArrayList alist = new ArrayList();
	    			for(int j=0;j<itemlist.size();j++)
	    			{
	    				LazyDynaBean abean = (LazyDynaBean)itemlist.get(j);
	    				String planitem=(String)abean.get("planitem");
	    				String className=(String)abean.get("classname");
	    				if("0".equals(this.userView.analyseFieldPriv(planitem)))
	    					continue;
	    				if(className==null|| "".equals(className)||!className.equalsIgnoreCase(planitemid))
	    					continue;
	    				alist.add(abean);
	    			}
	    			if(alist.size()<=0)
	    			{
	    				_info="您没有权限查看薪资总额参数中定义的参数指标";
	    				throw GeneralExceptionHandler.Handle(new Exception("您没有权限查看薪资总额参数中定义的参数指标"));
	    			}
	    			HashMap mpp =gbo.getYdList(setid, yearf, code, alist);
	    			ArrayList dataList=(ArrayList)mpp.get("list");
	    			String totalValue=(String)mpp.get("total");
	    			this.getFormHM().put("dataList", dataList);
	    			this.getFormHM().put("alist", alist);
	    			String str=planitemid;
	    			if(planitemid.endsWith("总额"))
	    			{
	    				str=planitemid+":";
	    			}
	    			else
	    			{
	    				str=planitemid+"总额:";
	    			}
	    			this.getFormHM().put("totalValue", str+totalValue);
	    		}
	    		if("3".equals(chartkind)|| "5".equals(chartkind))
	    		{
	    			String amountAdjustSet="-1";
	     
	    			if(map!=null&&map.get("amountAdjustSet")!=null&&((String)map.get("amountAdjustSet")).trim().length()>0)
	    				amountAdjustSet=(String)map.get("amountAdjustSet");
	    			String amountPlanitemDescField="-1";
	    			if(map!=null&&map.get("amountPlanitemDescField")!=null&&((String)map.get("amountPlanitemDescField")).length()>0)
	    				amountPlanitemDescField=(String)map.get("amountPlanitemDescField");
	    			if(!"-1".equals(amountAdjustSet)&&amountAdjustSet.trim().length()>0)
	    			{
	    	    		ArrayList adjustDataList=gbo.getAdjustList(amountAdjustSet, code, planItemDesc, yearf, amountPlanitemDescField);
	    	    		ArrayList tableHeaderList =gbo.getTableHeaderList();
	    			    this.getFormHM().put("tableHeaderList", tableHeaderList);
	    			    this.getFormHM().put("adjustDataList", adjustDataList);
	    			}
	    			this.getFormHM().put("isHasAdjustSet", amountAdjustSet);
	    		}
		    	ChartParameter chartParameter = new ChartParameter(); //图形参数对象
		    	chartParameter.setLineNodeIsMarked(1);
		    	chartParameter.setItemLabelsVisible(false);
		    	this.getFormHM().put("chartParameter", chartParameter);
		    	this.getFormHM().put("planitemlist",planitemlist);
		    	if("3".equals(chartkind)|| "5".equals(chartkind))
		    	{
		    		this.getFormHM().put("planitemid",chartkind+"`"+planitemid);		
		    	}
		    	else
		        	this.getFormHM().put("planitemid",planitemid);		
		    	this.getFormHM().put("yearf",yearf);
	    		this.getFormHM().put("code",code);
	    		this.getFormHM().put("chartkind", chartkind);
	    	    this.getFormHM().put("charttype", charttype);
	    	    this.getFormHM().put("planItemDesc", planItemDesc);
	    	    this.getFormHM().put("totalAmount", gbo.getTotalAmount());
	    	    this.getFormHM().put("info","");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("info",_info+"!");
		//	throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public ArrayList getPlanitemList(ArrayList itemlist)
	{
		ArrayList list = new ArrayList();
		try
		{
			for(int i=0;i<itemlist.size();i++)
			{
				LazyDynaBean bean =(LazyDynaBean)itemlist.get(i);
				if("0".equals(this.userView.analyseFieldPriv((String)bean.get("planitem"))))
					continue;
				list.add(new CommonData((String)bean.get("planitem"),(String)bean.get("planitemdesc")));
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
