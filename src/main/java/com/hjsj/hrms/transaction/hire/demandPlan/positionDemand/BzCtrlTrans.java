package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 3000000228
 * <p>Title:BzCtrlTrans.java</p>
 * <p>Description>:BzCtrlTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 13, 2009 4:07:49 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class BzCtrlTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			/**新程序，校园招聘按专业走，关联一个过期的岗位，所以不应该进行编制控制*/
			String z0301=(String)this.getFormHM().get("z0301");
			//z0301 = com.hjsj.hrms.utils.PubFunc.decrypt(z0301); 需求报批时后台出错,这里先注释掉，看看具体原因
			/**=1需求报批，=2需求审核=3审核查询*/
			String model=(String)this.getFormHM().get("model");
			String sp_flag=(String)this.getFormHM().get("sp_flag");
			if(sp_flag==null)
				sp_flag="";
			String opt=(String)this.getFormHM().get("opt");
			if(opt==null)
				opt="";
			DemandCtrlParamXmlBo bo = new DemandCtrlParamXmlBo(this.getFrameconn());
			/**编制内进行判断*/
			String z03inner=bo.getBZInner(z0301,"01");
			/**编制外*/
			String z03outer=bo.getBZInner(z0301, "02");
			String isHaveOuter="0";
			/**需求外的不用控制，直接报批*/
			/*if(z03outer.length()>0)
			{
				isHaveOuter="1";
				String sql = "update z03 set z0319='02' where z0301 in('"+z03outer.replaceAll(",","','")+"')";
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    dao.update(sql);
			}*/
			String message="0";
			String alertmessage="0";
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			/**招聘需求上报进行工资总额控制*/
			String isCtrlReportGZ="0";
			/**招聘需求上报进行编制控制*/
			String isCtrlReportBZ="0";
			if(map!=null&&map.get("isCtrlReportGZ")!=null)
			{
				isCtrlReportGZ=(String)map.get("isCtrlReportGZ");
			}
			if(map!=null&&map.get("isCtrlReportBZ")!=null)
			{
				isCtrlReportBZ=(String)map.get("isCtrlReportBZ");
			}
		
			if("1".equals(isCtrlReportBZ)&&z03inner.length()>0)
			{
	    		PosparameXML pos = new PosparameXML(this.getFrameconn());  
		    	String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
		    	ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
		        /**规则未定义*/
	    		if(setid==null|| "".equals(setid))
		    	{
		    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
		    		this.getFormHM().put("isHaveOuter", isHaveOuter);
		    		this.getFormHM().put("message", message);
		    		return;
	    		}
		    	if(planitemlist==null||planitemlist.size()<=0)
		    	{
		    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
		    		this.getFormHM().put("isHaveOuter", isHaveOuter);
	    			this.getFormHM().put("message", message);
	    			return;
	    		}
	    		HashMap posInfoMap=bo.getPoisitionInfo(z03inner);
	    		alertmessage = bo.isToSurpassBZ(z03inner, pos, posInfoMap);
	    		if("0".equals(alertmessage))
		    		message="0";
	    		else
	    			message="1";
			}
			/**工资总额控制，当编制控制通过时在进行工资总额的控制*/
			if("0".equals(message)&& "1".equals(isCtrlReportGZ))
			{
				GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap hm = XMLbo.getValuesMap();
				if(hm==null||hm.get("setid")==null|| "".equals((String)hm.get("setid")))
				{
					message=ResourceFactory.getProperty("hire.gzamount.nodefine");
					this.getFormHM().put("isHaveOuter", isHaveOuter);
	    			this.getFormHM().put("message", message);
	    			return;
				}
				/**编制内进行判断*/
				z03inner=bo.getGZInner(z0301,"01");
				/**编制外*/
				z03outer=bo.getGZInner(z0301, "02");
				/**只对编制内的招聘需求控制*/
				if(z03inner.length()>0)
				{
	        		HashMap posInfoMap=bo.getPoisitionInfo(z03inner);
	    			String positionSalaryStandardItem="";
	        		if(map!=null&&map.get("positionSalaryStandardItem")!=null)
	        		{
	        			positionSalaryStandardItem=(String)map.get("positionSalaryStandardItem");
	        		}
	        		if(positionSalaryStandardItem==null|| "".equals(positionSalaryStandardItem))
	        		{
	        			message=ResourceFactory.getProperty("hire.salarystandard.nodefine");
	        			this.getFormHM().put("isHaveOuter", isHaveOuter);
		    			this.getFormHM().put("message", message);
		    			return;
	        		}
	        		ArrayList planitemList=null;
	        	    if(hm!=null&&hm.get("ctrl_item")!=null)
	        	    {
	        	    	planitemList=(ArrayList)hm.get("ctrl_item");
	        	    }
	        	    if(planitemList==null||planitemList.size()<=0)
	        	    {
	        	    	message=ResourceFactory.getProperty("hire.planrealitem.nodefine");
	        	    	this.getFormHM().put("isHaveOuter", isHaveOuter);
		    			this.getFormHM().put("message", message);
		    			return;
	        	    }
	        	    alertmessage=bo.isToSurpassGZ(z03inner, hm, posInfoMap, positionSalaryStandardItem);
	        	    if("0".equals(alertmessage))
			    		message="0";
		    		else
		    			message="1";
				}
			}
			String moreLevelSP="0";
			if(map!=null&&map.get("moreLevelSP")!=null)
				moreLevelSP=(String)map.get("moreLevelSP");
			this.getFormHM().put("moreLevelSP", moreLevelSP);
			this.getFormHM().put("z0301",z0301);
			this.getFormHM().put("message",SafeCode.encode(message));
			this.getFormHM().put("alertmessage",SafeCode.encode(alertmessage));
			this.getFormHM().put("model",model);
			this.getFormHM().put("isHaveOuter", isHaveOuter);
			this.getFormHM().put("z03outer", z03outer.replaceAll(",","`"));
			this.getFormHM().put("sp_flag",sp_flag);
			this.getFormHM().put("opt", opt);
		}
		catch(Exception e)
		{
			String errorMsg = e.toString();
			int index_i = errorMsg.indexOf("description:");
			String message = errorMsg.substring(index_i + 12);
			this.getFormHM().put("message", SafeCode.encode(message));
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
	}

}
