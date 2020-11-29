package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 3020071030
 * <p>Title:ModBZCtrlTrans.java</p>
 * <p>Description>:ModBZCtrlTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 17, 2009 8:57:32 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ModBZCtrlTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String message="0";
			String z0301=(String)this.getFormHM().get("z0301");
			String z0313=(String)this.getFormHM().get("z0313");
			String z0315=(String)this.getFormHM().get("z0315");
			String z0316=(String)this.getFormHM().get("z0316");
			String entertype=(String)this.getFormHM().get("entertype");
			if(("1".equals(z0316)|| "01".equals(z0316))&& "2".equals(entertype))
			{
				 RecordVo vo = new RecordVo("z03");
				 vo.setString("z0301",z0301);
				 ContentDAO dao = new ContentDAO(this.getFrameconn());
				 vo = dao.findByPrimaryKey(vo);
				if("-1".equals(z0315))
				{
				 
				  int dd=vo.getInt("z0315");
				  if(dd==0)
				  {
					  dd=vo.getInt("z0313");
				  }
				  z0315=dd+"";
				}
				DemandCtrlParamXmlBo bo = new DemandCtrlParamXmlBo(this.getFrameconn());
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
				if("1".equals(isCtrlReportBZ))
				{
		    		PosparameXML pos = new PosparameXML(this.getFrameconn());  
			    	String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
			    	ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
			        /**规则未定义*/
		    		if(setid==null|| "".equals(setid))
			    	{
			    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
			    		this.getFormHM().put("message", message);
			    		return;
		    		}
			    	if(planitemlist==null||planitemlist.size()<=0)
			    	{
			    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
		    			this.getFormHM().put("message", message);
		    			return;
		    		}
		    		HashMap posInfoMap=bo.getPoisitionInfo(z0301);
		    		message = bo.isModToSurpassBZ(z0301, pos, posInfoMap, z0313, z0315);
				}
				String schoolPosition ="";
				if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)
					schoolPosition=(String)map.get("schoolPosition");
				boolean isCtrl=true;
				if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					String z0336=vo.getString("z0336");
					if(z0336!=null&& "01".equals(z0336))
					{
						isCtrl=false;
					}
				}
				if("0".equals(message)&& "1".equals(isCtrlReportGZ)&&isCtrl)
				{
					GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
					HashMap hm = XMLbo.getValuesMap();
					if(hm==null||hm.get("setid")==null|| "".equals((String)hm.get("setid")))
					{
						message=ResourceFactory.getProperty("hire.gzamount.nodefine");
		    			this.getFormHM().put("message", message);
		    			return;
					}
					HashMap posInfoMap=bo.getPoisitionInfo(z0301);
	    			String positionSalaryStandardItem="";
	        		if(map!=null&&map.get("positionSalaryStandardItem")!=null)
	        		{
	        			positionSalaryStandardItem=(String)map.get("positionSalaryStandardItem");
	        		}
	        		if(positionSalaryStandardItem==null|| "".equals(positionSalaryStandardItem))
	        		{
	        			message=ResourceFactory.getProperty("hire.salarystandard.nodefine");
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
		    			this.getFormHM().put("message", message);
		    			return;
	        	    }
	        	   message=bo.isModToSurpassGZ(z0301, hm, posInfoMap, positionSalaryStandardItem, z0315, z0313);
				}
			}
			this.getFormHM().put("message", message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
