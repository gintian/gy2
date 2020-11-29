package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
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
 * 
 * <p>Title:positionConfirmTrans.java</p>
 * <p>Description:审批</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 25, 2006 1:24:56 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class PositionConfirmTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hmap=this.getFormHM();
		String name=(String)hmap.get("position_set_table");
		ArrayList list=(ArrayList)hmap.get("position_set_record");		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			if(!(list==null||list.size()==0))
			{
				StringBuffer buf = new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					buf.append(vo.getString("z0301")+",");
				}
				buf.setLength(buf.length()-1);
				String z0301=buf.toString();
				
				//------------------------------------------------------------------------------
				DemandCtrlParamXmlBo bo = new DemandCtrlParamXmlBo(this.getFrameconn());
				/**编制内进行判断*/
				String z03inner=bo.getBZInner(z0301,"01");
				/**编制外*/
				String z03outer=bo.getBZInner(z0301, "02");
				String isHaveOuter="0";
				/**需求外的不用控制，直接报批*/
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
			    		
			    		 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.bzparameter.nodefine")));
		    		}
			    	if(planitemlist==null||planitemlist.size()<=0)
			    	{
			    		 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.bzparameter.nodefine")));
		    		}
		    		HashMap posInfoMap=bo.getPoisitionInfo(z03inner);
		    		alertmessage = bo.isToSurpassBZ(z03inner, pos, posInfoMap);
		    		if(!"0".equals(alertmessage))
		    			 throw GeneralExceptionHandler.Handle(new Exception(alertmessage));
			    	
				}
				/**工资总额控制，当编制控制通过时在进行工资总额的控制*/
				if("0".equals(message)&& "1".equals(isCtrlReportGZ))
				{
					GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
					HashMap hm = XMLbo.getValuesMap();
					if(hm==null||hm.get("setid")==null|| "".equals((String)hm.get("setid")))
					{
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.gzamount.nodefine")));
					}
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
		        			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.salarystandard.nodefine")));
		        		}
		        		ArrayList planitemList=null;
		        	    if(hm!=null&&hm.get("ctrl_item")!=null)
		        	    {
		        	    	planitemList=(ArrayList)hm.get("ctrl_item");
		        	    }
		        	    if(planitemList==null||planitemList.size()<=0)
		        	    {
		        	    	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.planrealitem.nodefine")));

		        	    }
		        	    alertmessage=bo.isToSurpassGZ(z03inner, hm, posInfoMap, positionSalaryStandardItem);
		        	    if(!"0".equals(alertmessage))
		        	    	throw GeneralExceptionHandler.Handle(new Exception(alertmessage));
				    	
					}
				}			
				//----------------------------------------------------------------------------
				PositionDemand pd = new PositionDemand(this.getFrameconn());
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					/**刘红梅要求不能审批驳回的需求*/
					if(!"02".equals(vo.getString("z0319"))/*&&!vo.getString("z0319").equals("07")*/)
						throw GeneralExceptionHandler.Handle(new Exception("只能审批已报批的需求!"));
					String sql="update z03 set z0319='03'";
					if(vo.getString("z0315")!=null&&vo.getInt("z0315")!=0)
					{
						sql+=",z0315="+vo.getString("z0315");
					}
					else
					{
						sql+=",z0315="+vo.getString("z0313");
					}
					
					sql+=" where z0301='"+vo.getString("z0301")+"'";
					
					dao.update(sql);
					String xml=pd.createXML(this.getUserView(), "",vo.getString("z0301"), "03");
					pd.saveXML(vo.getString("z0301"), xml);
				}
				//更新待办任务表
				ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, this.userView);
				zpbo.checkZpappr();
			}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}

}
