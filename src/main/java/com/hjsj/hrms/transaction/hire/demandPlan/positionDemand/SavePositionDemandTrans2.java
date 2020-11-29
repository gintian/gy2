package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SavePositionDemandTrans2 extends IBusiness {
	
	
	
	public void execute() throws GeneralException {
		
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)this.getFormHM().get("position_set_record");
		/**数据集字段列表*/
	   //ArrayList fieldlist=(ArrayList)hm.get("position_set_items");
		ContentDAO dao=null;
		try
		{
			    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	            dao=new ContentDAO(this.getFrameconn());
				if(!(list==null||list.size()==0))
				{
					for(int i=0;i<list.size();i++)
					{
						RecordVo vo=(RecordVo)list.get(i);
						String info=getInfo(vo);
						if(info.length()>1)
							throw new GeneralException(info.toString());
					}
					
					
					StringBuffer info=new StringBuffer("");
					StringBuffer z0301_buf = new StringBuffer("");
					for(int i=0;i<list.size();i++)
					{
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						if(state==-1)//Insert record
						{
							throw GeneralExceptionHandler.Handle(new Exception("记录方式下不可以添加招聘需求！"));
						}
						/*
						if(!vo.getString("z0319").equals("02")&&!vo.getString("z0319").equals("09"))
						{	info.append("只能修改报批、暂停状态的需求！");
							
						}
						if(info.length()>1)
							throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
				    	*/	
						String z0316=vo.getString("z0316");
						if(z0316!=null&&("1".equals(z0316)|| "01".equals(z0316)))
						{
			        		String z0301=vo.getString("z0301");
			        		String z0315=vo.getString("z0315");
			        		String z0313=vo.getString("z0313");
			        		DemandCtrlParamXmlBo bo = new DemandCtrlParamXmlBo(this.getFrameconn());
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
			   	
				        	if("1".equals(isCtrlReportBZ))
				        	{
			             		PosparameXML pos = new PosparameXML(this.getFrameconn());  
				            	String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
				            	ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
				                /**规则未定义*/
			        	    	if(setid==null|| "".equals(setid))
				            	{
				            		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
				            		throw GeneralExceptionHandler.Handle(new Exception(message));
			        	    	}
				            	if(planitemlist==null||planitemlist.size()<=0)
				            	{
				            		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
				            		throw GeneralExceptionHandler.Handle(new Exception(message));
			        	    	}
			        	    	HashMap posInfoMap=bo.getPoisitionInfo(z0301);
			        	    	alertmessage = message = bo.isModToSurpassBZ(z0301, pos, posInfoMap, z0313, z0315);
			        	    	if("0".equals(alertmessage))
				            		message="0";
			        	    	else
			        	    		throw GeneralExceptionHandler.Handle(new Exception(alertmessage));
			    	    	}
			        		/**工资总额控制，当编制控制通过时在进行工资总额的控制*/
			     	    	if("0".equals(message)&& "1".equals(isCtrlReportGZ))
			    	    	{
			    	     		GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
			    	    		HashMap hm = XMLbo.getValuesMap();
			    	    		if(hm==null||hm.get("setid")==null|| "".equals((String)hm.get("setid")))
			    	    		{
				        			message=ResourceFactory.getProperty("hire.gzamount.nodefine");
				        			throw GeneralExceptionHandler.Handle(new Exception(message));
				        		}
				        		/**只对编制内的招聘需求控制*/
				        		
			                	HashMap posInfoMap=bo.getPoisitionInfo(z0301);
			            		String positionSalaryStandardItem="";
			                  	if(map!=null&&map.get("positionSalaryStandardItem")!=null)
			                	{
			                		positionSalaryStandardItem=(String)map.get("positionSalaryStandardItem");
			                	}
			                	if(positionSalaryStandardItem==null|| "".equals(positionSalaryStandardItem))
			                	{
			                 		message=ResourceFactory.getProperty("hire.salarystandard.nodefine");
			                		throw GeneralExceptionHandler.Handle(new Exception(message));
			                	}
			                	ArrayList planitemList=null;
			                	if(hm!=null&&hm.get("ctrl_item")!=null)
			                  	{
			                 	    planitemList=(ArrayList)hm.get("ctrl_item");
			                	}
			                    if(planitemList==null||planitemList.size()<=0)
			            	    {
			            	         message=ResourceFactory.getProperty("hire.planrealitem.nodefine");
			            	        throw GeneralExceptionHandler.Handle(new Exception(message));
			            	    }
			                	alertmessage=bo.isModToSurpassGZ(z0301, hm, posInfoMap, positionSalaryStandardItem, z0315, z0313);
			                	if("0".equals(alertmessage))
					             	message="0";
				             	else
				        	    	throw GeneralExceptionHandler.Handle(new Exception(alertmessage));
			    	    	}
						}
					}
					for(int i=0;i<list.size();i++)
					{
						
						RecordVo vo=(RecordVo)list.get(i);
				        dao.updateValueObject(vo);
					}
				}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
		
		
		
	}

	
	
	public String getInfo(RecordVo vo)
	{
		ContentDAO dao=null;
		StringBuffer info=new StringBuffer("");	
		try
		{
			dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=null;
			if(vo.hasAttribute("z0305")&&vo.getString("z0305").trim().length()!=0)
			{
				rowSet=dao.search("select * from organization where codeitemid='"+vo.getString("z0305")+"'");
				if(rowSet.next())
				{
					if(!"UM".equalsIgnoreCase(rowSet.getString("codesetid")))
						info.append("\r\n"+ResourceFactory.getProperty("hire.interviewExam.errorInfo2")+".");	
				}
			}
			
			if(vo.getString("z0311").trim().length()==0)
			{
				info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo1")+".");			//ResourceFactory.getProperty("edit_report.table")					
			}
			else
			{
				rowSet=dao.search("select * from organization where codeitemid='"+vo.getString("z0311")+"'");
				if(rowSet.next())
				{
					if(!"@K".equalsIgnoreCase(rowSet.getString("codesetid")))
						info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo2")+".");	
				}
				else
				{
					info.append("\r\n没有该需求岗位.");
				}
			}	
			
	/*		if(vo.getString("z0321").trim().length()==0)
			{
				info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo3")+".");
			}
			else*/
			{
				ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
				HashMap map=xmlBo.getAttributeValues();
				String schoolPosition="";
				if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)
					schoolPosition=(String)map.get("schoolPosition");
				if(vo.hasAttribute("z0336")&&vo.getString("z0336")!=null&&vo.getString("z0336").trim().length()>0&& "01".equals(vo.getString("z0336"))&&schoolPosition.length()>0)
				{
					
				}
				else
				{
			    	if(vo.getString("z0311").trim().length()!=0)   //自动设置所属单位和部门
			    	{
				    	if(vo.getString("z0311").trim().indexOf(vo.getString("z0321").trim())==-1)
				    		info.append("\r\n"+ResourceFactory.getProperty("hire.interviewExam.errorInfo3")+".");
				    	else
				    	{
				    		vo.setString("z0321",getUnitCode(vo.getString("z0311").trim()));  //所属单位
				    		rowSet=dao.search("select parentid from organization where  organization.codeitemid='"+vo.getString("z0311").trim()+"'");
				    		if(rowSet.next())
				    		{
					    		String temp=rowSet.getString("parentid");
					    		vo.setString("z0325",temp);
							
					     	}
				    		
				    	}
			    	}
					
				}
			}
			
			if(vo.getDouble("z0313")==0)
			{
				info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo4")+".");				
			}
			else
			{
				
			    if(vo.getDouble("z0313")>10000000||vo.getDouble("z0313")<=0)
				{
					info.append("\r\n需求人数范围只能在1~10000000之内");
				}
				if(vo.getDouble("z0315")>10000000||vo.getDouble("z0315")<0)
				{
						info.append("\r\n审核人数范围只能在1~10000000之内");
				}
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info.toString();
	}
	
	
	//根据职位id找直属单位
	public String getUnitCode(String codeid)
	{
		String un_code_id="";
		boolean flag=true;
		ContentDAO dao=null;
		try
		{
			dao=new ContentDAO(this.getFrameconn());
			while(flag)
			{
				this.frowset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
				if(this.frowset.next())
				{
					String codesetid=this.frowset.getString("codesetid");
					String codeitemid=this.frowset.getString("codeitemid");
					codeid=codeitemid;
					if("UN".equalsIgnoreCase(codesetid))
					{
						un_code_id=codeitemid;
						flag=false;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return un_code_id;
	}
	
	
}
