package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SavePositionDemandTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		
		HashMap hm=this.getFormHM();
		//String name=(String)hm.get("position_set_table");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		/**数据集字段列表*/
	   //ArrayList fieldlist=(ArrayList)hm.get("position_set_items");
		ContentDAO dao=null;
		try
		{
	            dao=new ContentDAO(this.getFrameconn());
	            boolean isBz=false;
				if(!(list==null||list.size()==0))
				{
					for(int i=0;i<list.size();i++)
					{
						RecordVo vo=(RecordVo)list.get(i);
						String z0301=vo.getString("z0301");
						
						if(!"01".equals(vo.getString("z0319"))&&!"07".equals(vo.getString("z0319")))
                        {   
                            //info.append("只能修改起草、驳回状态的需求！");
                            continue;
                        }
						
						PositionDemand pd = new PositionDemand(this.getFrameconn());
						pd.checkCanOperate(z0301, userView);//查看当前用户是否是选中记录的当前操作人员
						String info=getInfo(vo);
						if(vo.getString("z0316")!=null&&("1".equals(vo.getString("z0316"))|| "01".equals(vo.getString("z0316"))))
						{
							isBz=true;
						}
						if(info.length()>1)
							throw new GeneralException(info.toString());
					}
					
					
					StringBuffer info=new StringBuffer("");
					/*String message="0";
					DemandCtrlParamXmlBo bo = new DemandCtrlParamXmlBo(this.getFrameconn());
					ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
					HashMap map=parameterXMLBo.getAttributeValues();
					PosparameXML pos = new PosparameXML(this.getFrameconn());  
					*//**招聘需求上报进行工资总额控制*//*
					String isCtrlReportGZ="0";
					*//**招聘需求上报进行编制控制*//*
					String isCtrlReportBZ="0";
					GzAmountXMLBo XMLbo = new GzAmountXMLBo(this.getFrameconn(),1);
					HashMap gax = XMLbo.getValuesMap();
					if(map!=null&&map.get("isCtrlReportGZ")!=null)
					{
						isCtrlReportGZ=(String)map.get("isCtrlReportGZ");
					}
					if(map!=null&&map.get("isCtrlReportBZ")!=null)
					{
						isCtrlReportBZ=(String)map.get("isCtrlReportBZ");
					}
					if(isCtrlReportBZ.equals("1")&&isBz)
					{
			    		
				    	String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
				    	ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
				        *//**规则未定义*//*
			    		if(setid==null||setid.equals(""))
				    	{
				    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
			    		}
				    	if(planitemlist==null||planitemlist.size()<=0)
				    	{
				    		message=ResourceFactory.getProperty("hire.bzparameter.nodefine");
			    		}
				    	if(!message.equals("0"))
				    	{
				    		throw GeneralExceptionHandler.Handle(new Exception(message));
				    	}
			    		
					}
					if(isCtrlReportGZ.equals("1")&&isBz)
					{
						
						if(gax==null||gax.get("setid")==null||((String)gax.get("setid")).equals(""))
						{
							message=ResourceFactory.getProperty("hire.gzamount.nodefine");
			    			this.getFormHM().put("message", message);
			    			return;
						}
						//HashMap posInfoMap=bo.getPoisitionInfo(z0301);
		    			String positionSalaryStandardItem="";
		        		if(map!=null&&map.get("positionSalaryStandardItem")!=null)
		        		{
		        			positionSalaryStandardItem=(String)map.get("positionSalaryStandardItem");
		        		}
		        		if(positionSalaryStandardItem==null||positionSalaryStandardItem.equals(""))
		        		{
		        			message=ResourceFactory.getProperty("hire.salarystandard.nodefine");
		        		}
		        		ArrayList planitemList=null;
		        	    if(gax!=null&&gax.get("ctrl_item")!=null)
		        	    {
		        	    	planitemList=(ArrayList)gax.get("ctrl_item");
		        	    }
		        	    if(planitemList==null||planitemList.size()<=0)
		        	    {
		        	    	message=ResourceFactory.getProperty("hire.planrealitem.nodefine");
		        	    }
		        	    if(!message.equals("0"))
				    	{
				    		throw GeneralExceptionHandler.Handle(new Exception(message));
				    	}
		        	    
					}*/
					for(int i=0;i<list.size();i++)
					{
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						if(state==-1)//Insert record
						{
							throw GeneralExceptionHandler.Handle(new Exception("记录方式下不可以添加招聘需求！"));
						}
						
						if(!"01".equals(vo.getString("z0319"))&&!"07".equals(vo.getString("z0319")))
						{	
							//info.append("只能修改起草、驳回状态的需求！");
							continue;
						}


						if(info.length()>1)
							throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
						
						
						if(state==2)
						{
							/**如果需求部门直接选单位，那么修改时会把值置为空了，所以不用检查*/
								if(vo.getString("z0325a")!=null)
								{
									//System.out.println(vo.getString("z0325").toLowerCase());
									//if(AdminCode.getCodeName("UM",vo.getString("z0325")).trim().length()==0)
										vo.setString("z0325",vo.getString("z0325a"));
								}
							
								/**原来的程序，，只有起草的可以报批，，被驳回的记录，只有修改后才能变成起草
								 * 现在的程序，起草和驳回的都可以报批，，*/
								/*if(vo.getString("z0319").equals("07"))
								{
									vo.setString("z0319","01");
									vo.setString("z0327","");
								}	*/						
								dao.updateValueObject(vo);
						
						}
					
						/*if(isCtrlReportBZ.equals("1")&&isBz)
						{
							String z0301=vo.getString("z0301");
							int z0313=vo.getInt("z0313");  
			    			HashMap posInfoMap=bo.getPoisitionInfo(z0301);
			         		message = bo.isModToSurpassBZ(z0301, pos, posInfoMap, z0313+"", "");
			         		if(!message.equals("0"))
					    	{
					    		throw GeneralExceptionHandler.Handle(new Exception(message));
					    	}
						}
						if(isCtrlReportGZ.equals("1")&&isBz)
						{
							String z0301=vo.getString("z0301");
							int z0313=vo.getInt("z0313"); 
							HashMap posInfoMap=bo.getPoisitionInfo(z0301);
							message=bo.isModToSurpassGZ(z0301, gax, posInfoMap, (String)map.get("positionSalaryStandardItem"), "", z0313+"");
							if(!message.equals("0"))
					    	{
					    		throw GeneralExceptionHandler.Handle(new Exception(message));
					    	}
						}	*/
					}					
		/*			for(int i=0;i<list.size();i++)
					{
						
						RecordVo vo=(RecordVo)list.get(i);
						int state=vo.getState();
						
						
						if(state==2)
						{
							*//**如果需求部门直接选单位，那么修改时会把值置为空了，所以不用检查*//*
								if(vo.getString("z0325a")!=null)
								{
									//System.out.println(vo.getString("z0325").toLowerCase());
									//if(AdminCode.getCodeName("UM",vo.getString("z0325")).trim().length()==0)
										vo.setString("z0325",vo.getString("z0325a"));
								}
							
								*//**原来的程序，，只有起草的可以报批，，被驳回的记录，只有修改后才能变成起草
								 * 现在的程序，起草和驳回的都可以报批，，*//*
								if(vo.getString("z0319").equals("07"))
								{
									vo.setString("z0319","01");
									vo.setString("z0327","");
								}							
								dao.updateValueObject(vo);
						
						}
					}*/
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
			FieldItem a_item=DataDictionary.getFieldItem("z0311","Z03");
			if(vo.hasAttribute("z0305")&&vo.getString("z0305").trim().length()!=0)
			{
				rowSet=dao.search("select * from organization where codeitemid='"+vo.getString("z0305")+"'");
				if(rowSet.next())
				{
					if(!"UM".equalsIgnoreCase(rowSet.getString("codesetid")))
						info.append("\r\n"+ResourceFactory.getProperty("hire.interviewExam.errorInfo2")+".");	
				}
			}
			/*if(a_item.isVisible()&&a_item.isFillable())
			{*/
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
			//}
			
	/*		if(vo.getString("z0321").trim().length()==0)
			{
				info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo3")+".");
			}
			else*/
			{
				if(vo.getString("z0311").trim().length()!=0)   //自动设置所属单位和部门
				{
					if(vo.getString("z0311").trim().indexOf(vo.getString("z0321").trim())==-1)
					{
						if(vo.hasAttribute("z0336")&&vo.getString("z0336")!=null&& "01".equals(vo.getString("z0336")))
						{
							ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
							HashMap map=parameterXMLBo.getAttributeValues();
							String hireMajor="";
							if(map.get("schoolPosition")!=null)
								hireMajor=(String)map.get("schoolPosition");
							if(hireMajor!=null&&hireMajor.trim().length()>0)
							{
								
							}
							else
							{
								info.append("\r\n"+ResourceFactory.getProperty("hire.interviewExam.errorInfo3")+".");
							}
						}
						else
						{
					    	info.append("\r\n"+ResourceFactory.getProperty("hire.interviewExam.errorInfo3")+".");
						}
					}
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
			
			if(vo.getDouble("z0313")==0)
			{
				info.append("\r\n"+ResourceFactory.getProperty("hire.demandplan.positiondemand.errorInfo4")+".");				
			}
			else
			{
				
			    if(vo.getDouble("z0313")>10000000||vo.getDouble("z0313")<=0)
				{
					info.append("\r\n需求人数范围只能在1 - 10000000之内");
				}
				if(vo.getDouble("z0315")>10000000||vo.getDouble("z0315")<0)
				{
						info.append("\r\n审核人数范围只能在1 - 10000000之内");
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
				else
				{
					flag=false;
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
