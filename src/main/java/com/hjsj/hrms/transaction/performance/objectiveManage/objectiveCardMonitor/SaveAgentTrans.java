package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
/**
 * 9028000306
 * <p>Title:SaveAgentTrans.java</p>
 * <p>Description>:SaveAgentTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 25, 2009 5:30:42 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveAgentTrans extends IBusiness{
  /*
	public void execute() throws GeneralException {
		try
		{
    		String str=SafeCode.decode((String)this.getFormHM().get("str"));///考核主体a0100+","+考核主体a0101+","+操作sp+","+驳回原因bh/
    		String object_id=(String)this.getFormHM().get("object_id");
    		String plan_id=(String)this.getFormHM().get("plan_id");
	    	String khType=(String)this.getFormHM().get("khType");//考核方式0-考核关系 1-汇报关系
	    	String market=(String)this.getFormHM().get("market");//=1自定义=0考核关系或者汇报关系
	    	String level = (String)this.getFormHM().get("level");
	    	String url_p=(String)this.getFormHM().get("url_p");
	    	String isSend=(String)this.getFormHM().get("isSend");//=1发送邮件=0不发邮件
    		ParseXmlBo bo = new ParseXmlBo(this.getFrameconn());
    		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Calendar cl =Calendar.getInstance();
    	    int year=cl.get(Calendar.YEAR);
    	    int month=cl.get(Calendar.MONTH)+1;
    	    int day=cl.get(Calendar.DAY_OF_MONTH);
    	    int hh=cl.get(Calendar.HOUR_OF_DAY);
    	    int mm=cl.get(Calendar.MINUTE);
    	    int ss=cl.get(Calendar.SECOND);
    	    String dddd=year+"-"+month+"-"+day+" "+hh+":"+mm+":"+ss;
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(this.getFrameconn());
    		String agent_name="";
    		if(this.getUserView().getStatus()==4)
    		{
    			agent_name=suob.getAgentInfo(this.getUserView());
    		}
    		else
    		{
    			agent_name=this.getUserView().getUserFullName();
    		}
    		
    		RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			vo = dao.findByPrimaryKey(vo);
			bo.setPlan_vo(vo);
			String object_type=vo.getString("object_type");
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id+"");
  			Hashtable params = parameter_content.getDegreeWhole();
  		 
  			String taskAdjustNeedNew=(String)params.get("taskAdjustNeedNew");
  			if(taskAdjustNeedNew==null)
  				taskAdjustNeedNew="false";
  			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
  			Hashtable ht_table=appb.analyseParameterXml();
  			
  			String creatCard_mail=(String)ht_table.get("creatCard_mail");
  			String creatCard_mail_template=(String)ht_table.get("creatCard_mail_template");
   			LazyDynaBean  templateBo=null;
  			ObjectCardBo ocbo=new ObjectCardBo(plan_id,this.getFrameconn(),object_id);
  			if(creatCard_mail.equalsIgnoreCase("true")&&creatCard_mail_template.length()>0)
				templateBo=ocbo.getTemplateMailInfo(creatCard_mail_template);
  			IMISPendProceed imip=new IMISPendProceed();
  			PendingTask pt = new PendingTask();
  			String pendingType="目标制定";
  			String pendingCode=ocbo.getPendingCode(this.userView.getA0100(),this.userView.getDbname());
			HashMap leaderMap = null;
			ArrayList reasonsList = new ArrayList();
			boolean isORG=false;
			if(!object_type.equals("2"))
			{
				isORG=true;
				leaderMap=suob.getOrgLeader(plan_id,null);
			}
			String a0100="";
			if(isORG)
			{
				LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
				a0100=(String)leaderBean.get("a0100");
			}
    		String[] arr=str.split("/");
    		int length=arr.length;
    		//boolean isTz=this.isTZ(object_id, plan_id,isORG);
    		//00000014,3,黄世杜,02,0,,00000003,1,龚务军
    		if(market.equals("1"))
	     	{
    			ArrayList list = new ArrayList();
	     		for(int i=0;i<arr.length;i++)
	    		{
	    			if(arr[i]==null||arr[i].equals(""))
	    				continue;
	    			ArrayList valueList = new ArrayList();
		     		String[] subarr=arr[i].split("`");
				    StringBuffer buf = new StringBuffer("");
				    buf.append("update per_mainbody set sp_flag='"+subarr[3]+"',sp_date=");
				    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    	buf.append("to_date('"+dddd+"','yyyy-mm-dd hh24:mi:ss')");
				    else
				    	buf.append("'"+dddd+"'");
				    String reasons="";
				    ArrayList values=new ArrayList();
				    StringBuffer bbf=new StringBuffer();
					if(subarr[3].equals("07")&&subarr.length>5)
   					{
   					    bbf.append("update per_mainbody set reasons=? where plan_id="+plan_id);
                       	bbf.append(" and object_id='"+object_id+"' and mainbody_id='"+subarr[0]+"'");
                       	values.add(subarr[5]);
                       	reasons=subarr[5];
   					}
				    buf.append(" where mainbody_id='"+subarr[0]+"' and object_id='"+object_id+"'");
				    buf.append(" and plan_id="+plan_id);
				    String report_to = "";
				    if(subarr[3].equals("02"))
				    	report_to=this.getReportToInfo(subarr[6], object_id, "", plan_id, 1);
				    String newXML=bo.produceRecord(object_id, plan_id, subarr[0], "usr", reasons, subarr[3], "1", this.getUserView().getA0100(), agent_name,report_to);
				    suob.saveNewXML(object_id, plan_id, newXML);
				    if(length==(i+1))
				    {
				    	
				    	this.setObject_sp_flag(object_id, plan_id,subarr[3]);
				    }
				    if(length==(i+1)&&(i+1)!=Integer.parseInt(level)&&subarr.length==9)
				    {
				    	// 是报批操作时才发邮件 
				    	if(subarr[3].equalsIgnoreCase("02"))
				    	{
				    		if(creatCard_mail.equalsIgnoreCase("true")&&creatCard_mail_template.length()>0&&isSend.equals("1"))
				    		{
				            	String objectName=this.getObjectName(plan_id, object_id);
				            	String title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];
  
				           
				            	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
				            	title=ocb.getDBStr("1", vo.getString("name"), objectName, subarr[2]);
				            	if(title==null||title.equals(""))
				            		title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];
				            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+subarr[6]);
				            	String username=(String)_abean.get("username");
								String password=(String)_abean.get("password");	
				            	String content=ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
				            	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
				            	ocbo.sendMessage(title,content, subarr[6], "usr");
				            	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
				            	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
					    		{
				             		
				            		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
					    			imip.insertPending(pendingCode,title,"USR"+subarr[0],"usr"+subarr[6],aurl,0,1,pendingType);
						    	}
				            	pt.insertPending(pendingCode, "P", title, "USR"+subarr[0], "usr"+subarr[6], url, 0, 1, pendingType,this.userView);
				    		}
				    		this.setCurrappuser(object_id, plan_id, subarr[6],1);
				        }
				    	else
				    	{
				    		this.setCurrappuser(object_id, plan_id, subarr[6],2);
				    	}
				    }
				    //清空当前操作人 
				    if(subarr[3].equalsIgnoreCase("03"))
				    {
				    	ocbo.optPersonalComment("2");
				    	this.setCurrappuser(object_id, plan_id,"",2);
				    	if(taskAdjustNeedNew.equalsIgnoreCase("false"))
				    	{
				    		this.changeData(plan_id, object_id, isORG);
				    	}
				    	//批准时，发邮件给考核对象
				    	if(isSend.equals("1"))
				    	{
				    		String objectName=this.getObjectName(plan_id, object_id);
				    		if(isORG)
				    		{
				    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
				    			objectName=(String)leaderBean.get("a0101");
				    		}
				    		String title=objectName+","+vo.getString("name")+"考核指标已审批,请确认考核指标。来自:"+subarr[2];
							StringBuffer content=new StringBuffer(objectName+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>");
							content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]+"已批准您（"+vo.getString("name")+")计划中的目标！");		
							content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]);
							content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date()));						
							ocbo.sendMessage(title,content.toString(),(isORG?a0100:object_id),"USR");
							
				    	}
				    }
				    //驳回给考核对象发送邮件
				    if(subarr[3].equalsIgnoreCase("07"))
				    	ocbo.optPersonalComment("3");
				    if(subarr[3].equalsIgnoreCase("07")&&creatCard_mail.equalsIgnoreCase("true")&&isSend.equals("1"))
			    	{
				    	String objectName=this.getObjectName(plan_id, object_id);
			    		if(isORG)
			    		{
			    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
			    			objectName=(String)leaderBean.get("a0101");
			    		}
				    	String title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
				    	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
				    	title=ocb.getDBStr("2", vo.getString("name"), objectName, subarr[2]);
		            	if(title==null||title.equals(""))
		            		title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
		            
		            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+(isORG?a0100:object_id));
		            	String username=(String)_abean.get("username");
						String password=(String)_abean.get("password");	
			        	
			        	String content=subarr.length>5?subarr[5]:"";//ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
			        	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
			        	content+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date());
			        	if(isORG)
			            	ocbo.sendMessage(title,content, a0100, "usr");
			        	else
			        		ocbo.sendMessage(title,content, object_id, "usr");
			        	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
			        	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
						{
			        		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
			        		if(isORG)
			        		{
					    		imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+a0100,aurl,0,1,pendingType);
			        		}
			        		else
			        		{
			        			imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+object_id,aurl,0,1,pendingType);
			        		}
						}
			        	if(isORG)
			            	pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+a0100, url, 0, 1, pendingType,this.userView);
			        	else
			        		pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+object_id, url, 0, 1, pendingType,this.userView);
			        }
				 
				    if(subarr[3].equals("07")&&subarr.length>5)
                    {
                    	LazyDynaBean bean= new LazyDynaBean();
                    	bean.set("sql",bbf.toString());
                    	bean.set("list", values);
                    	reasonsList.add(bean);
                    }
				    list.add(buf.toString());
				 
  	    		}
	     		if(list.size()>0)
	     		{
	         		dao.batchUpdate(list);
	     		}
    		}
    		else
    		{	
      			if(khType.equals("0"))//考核关系
	    		{
      				ArrayList list = new ArrayList();
	    			for(int i=0;i<arr.length;i++)
	        		{
	    				if(arr[i]==null||arr[i].equals(""))
	    	    			continue;
	    				String[] subarr=arr[i].split("`");
	    				ArrayList values=new ArrayList();
	    				StringBuffer buf = new StringBuffer();
	    				buf.append("");
	    				boolean flag=this.isHaveRecord(object_id, subarr[0], plan_id, dao);
	    				String reasons=" ";
	    				String report_to="";
	    				StringBuffer bbf=new StringBuffer("");
	    				if(flag)
	    				{
	    					buf.append("update per_mainbody set sp_flag='"+subarr[3]+"'");
	    					if(subarr[3].equals("07")&&subarr.length>5)
	    					{
	    						bbf.append("update per_mainbody set reasons=? where plan_id="+plan_id);
                            	bbf.append(" and object_id='"+object_id+"' and mainbody_id='"+subarr[0]+"'");
                            	values.add(subarr[5]);
                            	reasons=subarr[5];
	    					}
	    					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                             	buf.append(" ,sp_date=to_date('"+dddd+"','yyyy-mm-dd hh24:mi:ss') ");
                            else
                            	buf.append(",sp_date='"+dddd+"' ");
	    					buf.append(" where object_id='"+object_id+"' and plan_id="+plan_id);
	    					buf.append(" and mainbody_id='"+subarr[0]+"'");
	    					if(subarr[3].equals("02"))
	    					{
	    						report_to=this.getReportToInfo(subarr[6], object_id, "", plan_id, 1);
	    					}
	    				}
	    				else
	    				{
	        				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
	    		    		int bodyid=Integer.parseInt(idg.getId("per_mainbody.id"));
	    		    		buf.append("insert into per_mainbody(id,b0110,e0122,e01a1,object_id,mainbody_id,");
	    		    		buf.append("a0101,body_id,plan_id,sp_date,sp_flag) select "+bodyid+" as id,");
                            buf.append("b0110,e0122,e01a1,'"+object_id+"' as object_id,mainbody_id,a0101,body_id,");
                            buf.append(plan_id+" as plan_id,");
                            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                             	buf.append(" to_date('"+dddd+"','yyyy-mm-dd hh24:mi:ss') as sp_date");
                            else
                            	buf.append("'"+dddd+"' as sp_date");
                            buf.append(",'"+subarr[3]+"' as sp_flag ");
                            if(subarr[3].equals("07")&&subarr.length>5)
                            {
                            	bbf.append("update per_mainbody set reasons=? where plan_id="+plan_id);
                            	bbf.append(" and object_id='"+object_id+"' and mainbody_id='"+subarr[0]+"'");
                            	values.add(subarr[5]);
                            	reasons=subarr[5];
                            }
                            buf.append(" from per_mainbody_std where object_id='"+(isORG?a0100:object_id)+"' and ");
                            buf.append(" mainbody_id='"+subarr[0]+"'");
                            if(subarr[3].equals("02"))
                            	report_to=this.getReportToInfo(subarr[6], (isORG?a0100:object_id), "", plan_id, 2);
	    				}
	    				
                        String newXML=bo.produceRecord(object_id, plan_id, subarr[0], "usr", reasons, subarr[3], "1", this.getUserView().getA0100(), agent_name,report_to);
                        suob.saveNewXML(object_id, plan_id, newXML);
                        if(length==(i+1))
    				    {
    				    	
    				    	this.setObject_sp_flag(object_id, plan_id,subarr[3]);
    				    }
                        if(length==(i+1)&&(i+1)!=Integer.parseInt(level)&&subarr.length==9)
    				    {
                        	
    				    	if(subarr[3].equalsIgnoreCase("02"))
    				    	{
    				    		if(creatCard_mail.equalsIgnoreCase("true")&&creatCard_mail_template.length()>0&&isSend.equals("1"))
    				    		{
    			         	    	String objectName=this.getObjectName(plan_id, object_id);
    			         	    	String title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];

    				        
    			         	    	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
    			         	    	title=ocb.getDBStr("1", vo.getString("name"), objectName, subarr[2]);
    				            	if(title==null||title.equals(""))
    				            		title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];
    				            
    				            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+subarr[6]);
    				            	String username=(String)_abean.get("username");
    								String password=(String)_abean.get("password");	
    				            	String content=ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
    				            	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
    				             	ocbo.sendMessage(title,content, subarr[6], "usr");
    				             	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
    				            	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    				    			{
    				            		
    				            		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
    					    			imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+subarr[6],aurl,0,1,pendingType);
    				    			}
    				            	pt.insertPending(pendingCode, "P", title,"usr"+subarr[0],"usr"+subarr[6],url,0,1,pendingType,this.userView);
    				    		}
    				    		this.setCurrappuser(object_id, plan_id, subarr[6],1);
    				    	}
    				    	else
    				    	{
    				    		this.setCurrappuser(object_id, plan_id, subarr[6],2);
    				    	}
    				    }
                        //清空当前操作人
    				    if(subarr[3].equalsIgnoreCase("03"))
    				    {
    				    	ocbo.optPersonalComment("2");
    				    	this.setCurrappuser(object_id, plan_id,"",2);
    				    	if(taskAdjustNeedNew.equalsIgnoreCase("false"))
    				    	{
    				    		this.changeData(plan_id, object_id, isORG);
    				    	}
    				    	
    				    	if(isSend.equals("1"))
    				    	{
    				    		String objectName=this.getObjectName(plan_id, object_id);
    				    		if(isORG)
    				    		{
    				    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
    				    			objectName=(String)leaderBean.get("a0101");
    				    		}
    				    		String title=objectName+","+vo.getString("name")+"考核指标已审批,请确认考核指标。来自:"+subarr[2];
    							StringBuffer content=new StringBuffer(objectName+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>");
    							content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]+"已批准您（"+vo.getString("name")+")计划中的目标！");		
    							content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]);
    							content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date()));						
    							ocbo.sendMessage(title,content.toString(),(isORG?a0100:object_id),"USR");
    						
    				    	}
    				    }
    				    if(subarr[3].equalsIgnoreCase("07"))
    				    	ocbo.optPersonalComment("3");
    				    if(subarr[3].equalsIgnoreCase("07")&&creatCard_mail.equalsIgnoreCase("true")&&isSend.equals("1"))
    			    	{
    				    	String objectName=this.getObjectName(plan_id, object_id);
				    		if(isORG)
				    		{
				    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
				    			objectName=(String)leaderBean.get("a0101");
				    		}
    				    	String title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
    			        	String content=subarr.length>0?subarr[5]:"";//ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
    			        	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
    			        	title=ocb.getDBStr("2", vo.getString("name"), objectName, subarr[2]);
			            	if(title==null||title.equals(""))
			            		title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
			            
			            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+(isORG?a0100:object_id));
			            	String username=(String)_abean.get("username");
							String password=(String)_abean.get("password");	
    			        	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
    			        	content+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date());
    			        	if(isORG)
    			            	ocbo.sendMessage(title,content, a0100, "usr");
    			        	else
    			        		ocbo.sendMessage(title,content, object_id, "usr");
    			        	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
    			        	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    						{
    			        		
    			        		
    			        		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
    			        		if(isORG)
    			        		{
    					    		imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+a0100,aurl,0,1,pendingType);
    			        		}
    			        		else
    			        		{
    			        			imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+object_id,aurl,0,1,pendingType);
    			        		}
    						}
    			        	if(isORG)
    			            	pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+a0100, url, 0, 1, pendingType,this.userView);
    			        	else
    			        		pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+object_id, url, 0, 1, pendingType,this.userView);

    			        }
                        if(subarr[3].equals("07")&&subarr.length>5)
                        {
                        	LazyDynaBean bean= new LazyDynaBean();
                        	bean.set("sql",bbf.toString());
                        	bean.set("list", values);
                        	reasonsList.add(bean);
                        }
    				    list.add(buf.toString());
	        		}
	    			if(list.size()>0)
	    			{
	        			dao.batchUpdate(list);
	    			}
	    		}
	    		else
	    		{
	    			HashMap bodyIDMap = this.getBodyIdByLevel(plan_id);
	    			String sql = "select e01a1 from usra01 where a0100='"+(isORG?a0100:object_id)+"'";
	    			String posid="";
	    			this.frowset=dao.search(sql);
	    			while(this.frowset.next())
	    			{
	    				posid=this.frowset.getString("e01a1");
	    			}
	    			RenderRelationBo rrb = new RenderRelationBo(this.getFrameconn());
	    			ArrayList dbnameList = new ArrayList();
	    			dbnameList.add("usr");
	    			ArrayList list2 = new ArrayList();
	    			list2.add(posid);
	    			HashMap mainbodymap = rrb.getReportLeaderMap(list2, Integer.parseInt(level), dbnameList);
	    			ArrayList list = new ArrayList();
	    			for(int i=0;i<arr.length;i++)
	        		{
	    				if(arr[i]==null||arr[i].equals(""))
	    	    			continue;
	    				String[] subarr=arr[i].split("`");
	    				ArrayList values = new ArrayList();
	    				String body_id="";
	    				if(subarr[4].equals("0"))
	    				{
	    					body_id=(String)bodyIDMap.get("1");
	    				}
	    				if(subarr[4].equals("1"))
	    				{
	    					body_id=(String)bodyIDMap.get("0");
	    				}
	    				if(subarr[4].equals("2"))
	    				{
	    					body_id=(String)bodyIDMap.get("-1");
	    				}
	    				if(subarr[4].equals("3"))
	    				{
	    					body_id=(String)bodyIDMap.get("-2");
	    				}
	    				if(body_id==null||body_id.equals(""))
	    					body_id="0";
	    				for(int j=0;j<dbnameList.size();j++)
	    				{
	    					String nbase=(String)dbnameList.get(j);
	    					StringBuffer buf = new StringBuffer("");
	    					StringBuffer bbf=new StringBuffer("");
	    					boolean flag=this.isHaveRecord(object_id, subarr[0], plan_id, dao);
		    				String reasons=" ";
		    				String report_to="";
		    				if(flag)
		    				{
		    					buf.append("update per_mainbody set sp_flag='"+subarr[3]+"'");
		    					if(subarr[3].equals("07")&&subarr.length>5)
		    					{
		    						bbf.append("update per_mainbody set reasons=? where plan_id="+plan_id);
	                            	bbf.append(" and object_id='"+object_id+"' and mainbody_id='"+subarr[0]+"'");
	                            	values.add(subarr[5]);
	                            	reasons=subarr[5];
		    					}
		    					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
	                             	buf.append(" ,sp_date=to_date('"+dddd+"','yyyy-mm-dd hh24:mi:ss') ");
	                            else
	                            	buf.append(",sp_date='"+dddd+"' ");
		    					buf.append(" where object_id='"+object_id+"' and plan_id="+plan_id);
		    					buf.append(" and mainbody_id='"+subarr[0]+"'");
		    					if(subarr[3].equals("02"))
		    						report_to=this.getReportToInfo(subarr[6], object_id, nbase, plan_id, 1);
		    					String newXML=bo.produceRecord(object_id, plan_id, subarr[0], "usr", reasons, subarr[3], "1", this.getUserView().getA0100(), agent_name,report_to);
    				    		suob.saveNewXML(object_id, plan_id, newXML);
    				    		if(length==(i+1))
    				    		{
    						    	
    					    	   this.setObject_sp_flag(object_id, plan_id,subarr[3]);
    				    		}
    					    	 if(length==(i+1)&&(i+1)!=Integer.parseInt(level)&&subarr.length==9)
    					    	    {
    					    		
    							    	if(subarr[3].equalsIgnoreCase("02")&&creatCard_mail.equalsIgnoreCase("true")&&creatCard_mail_template.length()>0&&isSend.equals("1"))
    							    	{
    							    		String objectName=this.getObjectName(plan_id, object_id);
    							        	String title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];

    						        	
    							        	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
    							        	title=ocb.getDBStr("1", vo.getString("name"), objectName, subarr[2]);
    						            	if(title==null||title.equals(""))
    						            		title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];
    						            
    						            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+subarr[6]);
    						            	String username=(String)_abean.get("username");
    										String password=(String)_abean.get("password");
    						            	String content=ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
    						            	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
    						            	ocbo.sendMessage(title,content, subarr[6], "usr");
    						            	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
    						            	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    								      	{
    						        	    	
    						            		String aurl= url+"&pendingCode="+pendingCode+"&appfwd=1";
    								    		imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+subarr[6],aurl,0,1,pendingType);
    							    		}
    						            	pt.insertPending(pendingCode,"P", title,"usr"+subarr[0],"usr"+subarr[6],url,0,1,pendingType,this.userView);
    							    	}
    							    	else
    							    	{
    							    		this.setCurrappuser(object_id, plan_id, subarr[6],2);
    							    	}
    							    	if(subarr[6].equalsIgnoreCase("02"))
    							    	{
    							    		this.setCurrappuser(object_id, plan_id, subarr[6],1);
    							    	}
    							    		
    			    			    }
    					    	
    							    if(subarr[3].equalsIgnoreCase("03"))
    							    {
    							    	ocbo.optPersonalComment("2");
    							    	this.setCurrappuser(object_id, plan_id,"",2);
    							    	if(taskAdjustNeedNew.equalsIgnoreCase("false"))
    							    	{
    							    		this.changeData(plan_id, object_id, isORG);
    							    	}
    							    	
    							    	if(isSend.equals("1"))
    							    	{
    							    		String objectName=this.getObjectName(plan_id, object_id);
    							    		if(isORG)
    							    		{
    							    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
    							    			objectName=(String)leaderBean.get("a0101");
    							    		}
    							    		String title=objectName+","+vo.getString("name")+"考核指标已审批,请确认考核指标。来自:"+subarr[2];
    							    		
    										StringBuffer content=new StringBuffer(objectName+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>");
    										content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]+"已批准您（"+vo.getString("name")+")计划中的目标！");		
    										content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]);
    										content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date()));						
    										ocbo.sendMessage(title,content.toString(),(isORG?a0100:object_id),"USR");
    									
    							    	}
    							    }
    							    if(subarr[3].equalsIgnoreCase("07"))
    							    	ocbo.optPersonalComment("3");
    							    if(subarr[3].equalsIgnoreCase("07")&&creatCard_mail.equalsIgnoreCase("true")&&isSend.equals("1"))
    						    	{
    							    	String objectName=this.getObjectName(plan_id, object_id);
							    		if(isORG)
							    		{
							    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
							    			objectName=(String)leaderBean.get("a0101");
							    		}
    							    	String title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
    						        	
    							    	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
    							    	title=ocb.getDBStr("2", vo.getString("name"), objectName, subarr[2]);
						            	if(title==null||title.equals(""))
						            		title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
						            
						            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+(isORG?a0100:object_id));
						            	String username=(String)_abean.get("username");
										String password=(String)_abean.get("password");
    						        	String content=subarr.length>5?subarr[5]:"";//ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
    						        	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
    						        	content+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date());
    						        	if(isORG)
    						            	ocbo.sendMessage(title,content, a0100, "usr");
    						        	else
    						        		ocbo.sendMessage(title,content, object_id, "usr");
    						        	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
    						        	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    									{
    						        		
    						        		
    						        		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
    						        		if(isORG)
    						        		{
    								    		imip.insertPending(pendingCode,title,content,"usr"+a0100,aurl,0,1,pendingType);
    						        		}
    						        		else
    						        		{
    						        			imip.insertPending(pendingCode,title,content,"usr"+object_id,aurl,0,1,pendingType);
    						        		}
    									}
    						        	if(isORG)
    		    			            	pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+a0100, url, 0, 1, pendingType,this.userView);
    		    			        	else
    		    			        		pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+object_id, url, 0, 1, pendingType,this.userView);


    						        }
    							   
    							    if(subarr[3].equals("07")&&subarr.length>5)
    		                        {
    		                        	LazyDynaBean bean= new LazyDynaBean();
    		                        	bean.set("sql",bbf.toString());
    		                        	bean.set("list", values);
    		                        	reasonsList.add(bean);
    		                        }
    							    list.add(buf.toString());
    							 
		    				}
		    				else
		    				{
	    		    			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
	    		    			//StringBuffer bbf = new StringBuffer("");
	    			    		int bodyid=Integer.parseInt(idg.getId("per_mainbody.id"));
	    		     			LazyDynaBean bean = (LazyDynaBean)mainbodymap.get(nbase.toUpperCase()+subarr[0]);
	    				    	if(bean!=null)
	    				    	{
	    					    	buf.append("insert into per_mainbody(id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,plan_id,sp_date,sp_flag,body_id) values (");
	    					    	buf.append(bodyid+",'"+(String)bean.get("b0110")+"','"+(String)bean.get("e0122")+"','");
	    				    		buf.append((String)bean.get("e01a1")+"','"+object_id+"','"+subarr[0]+"','");
	    				    		buf.append((String)bean.get("a0101")+"',"+plan_id+",");
	    				    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
	    					    		buf.append("to_date('"+dddd+"','yyyy-mm-dd hh24:mi:ss')");
	    				    		else
	    				    			buf.append("'"+dddd+"'");
	    					    	buf.append(",'"+subarr[3]+"'");
	    				    		if(subarr[3].equals("07")&&subarr.length>5)
	    	                         {
	    				    			bbf.append("update per_mainbody set reasons=? where plan_id="+plan_id);
	                                	bbf.append(" and object_id='"+object_id+"' and mainbody_id='"+subarr[0]+"'");
	                                	reasons=subarr[5];
	    	                            values.add(subarr[5]);
	    	                            reasons=subarr[5];
	    	                         }
	    	          
	    					    	 buf.append(","+body_id+")");
	    					    	 if(subarr[3].equals("02"))
	    					    	 {
	    					    		 LazyDynaBean x_bean=(LazyDynaBean)mainbodymap.get(nbase.toUpperCase()+subarr[6]);
	    					    		 report_to=AdminCode.getCodeName("UN",(String)x_bean.get("b0110"))+"/"+AdminCode.getCodeName("UM",(String)x_bean.get("e0122"))+"/"+AdminCode.getCodeName("@K",(String)x_bean.get("e01a1"))+"/"+(String)x_bean.get("a0101");
	    					    	 }

	    				    		String newXML=bo.produceRecord(object_id, plan_id, subarr[0], "usr", reasons, subarr[3], "1", this.getUserView().getA0100(), agent_name,report_to);
	    				    		suob.saveNewXML(object_id, plan_id, newXML);
	    				    		if(length==(i+1))
	    				    		{
	    						    	
	    					    	   this.setObject_sp_flag(object_id, plan_id,subarr[3]);
	    				    		}
	    					    	 if(length==(i+1)&&(i+1)!=Integer.parseInt(level)&&subarr.length==9)
	    					    	    {
	    					    	
	    							    	if(subarr[3].equalsIgnoreCase("02")&&creatCard_mail.equalsIgnoreCase("true")&&creatCard_mail_template.length()>0&&isSend.equals("1"))
	    							    	{
	    							    		String objectName=this.getObjectName(plan_id, object_id);
	    							    		String title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];

	    							        	
	    							    		ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
	    							    		title=ocb.getDBStr("1", vo.getString("name"), objectName, subarr[2]);
	    						            	if(title==null||title.equals(""))
	    						            		title=subarr[8]+","+vo.getString("name")+"考核指标已制定,请审批考核指标。来自:"+subarr[2];
	    						  
	    						            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+subarr[6]);
	    						            	String username=(String)_abean.get("username");
	    										String password=(String)_abean.get("password");
	    							        	String content=ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
	    							        	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
	    							        	ocbo.sendMessage(title,content, subarr[6], "usr");
	    							        	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id="+subarr[7]+"&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
	    							        	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
	    										{
	    							        		
	    							        		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
	    											imip.insertPending(pendingCode,title,"usr"+subarr[0],"usr"+subarr[6],aurl,0,1,pendingType);
	    										}
	    							        	pt.insertPending(pendingCode, "P", title, "usr"+subarr[0],"usr"+subarr[6],url,0,1,pendingType,this.userView);
	    							    	}
	    							    	else
	    							    	{
	    							    		this.setCurrappuser(object_id, plan_id, subarr[6],2);
	    							    	}
	    							    	if(subarr[6].equalsIgnoreCase("02"))
	    							    	{
	    							    		this.setCurrappuser(object_id, plan_id, subarr[6],1);
	    							    	}
	    							    	
	    			    			    } 
	    							    if(subarr[3].equalsIgnoreCase("03"))
	    							    {
	    							    	ocbo.optPersonalComment("2");
	    							    	this.setCurrappuser(object_id, plan_id,"",2);
	    							    	if(taskAdjustNeedNew.equalsIgnoreCase("false"))
	    							    	{
	    							    		this.changeData(plan_id, object_id, isORG);
	    							    	}
	    							    
	    							    	if(isSend.equals("1"))
	    							    	{
	    							    		String objectName=this.getObjectName(plan_id, object_id);
	    							    		if(isORG)
	    							    		{
	    							    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
	    							    			objectName=(String)leaderBean.get("a0101");
	    							    		}
	    							    		String title=objectName+","+vo.getString("name")+"考核指标已审批,请确认考核指标。来自:"+subarr[2];
	    							    		
	    										StringBuffer content=new StringBuffer(objectName+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>");
	    										content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]+"已批准您（"+vo.getString("name")+")计划中的目标！");		
	    										content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+subarr[2]);
	    										content.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date()));						
	    										ocbo.sendMessage(title,content.toString(),(isORG?a0100:object_id),"USR");
	    							 
	    							    	}
	    							    }
	    							    if(subarr[3].equalsIgnoreCase("07"))
	    							    	ocbo.optPersonalComment("3");
	    							    if(subarr[3].equalsIgnoreCase("07")&&creatCard_mail.equalsIgnoreCase("true")&&isSend.equals("1"))
	    						    	{
	    							    	String objectName=this.getObjectName(plan_id, object_id);
    							    		if(isORG)
    							    		{
    							    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(object_id+plan_id);
    							    			objectName=(String)leaderBean.get("a0101");
    							    		}
	    							    	String title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2]; 
	    							    	ObjectCardBo ocb=new ObjectCardBo(this.getFrameconn());
	    							    	title=ocb.getDBStr("1", vo.getString("name"), objectName, subarr[2]);
    						            	if(title==null||title.equals(""))
    						            		title=objectName+","+vo.getString("name")+"考核指标已驳回,请调整考核指标。 来自:"+subarr[2];
    						  
    						            	LazyDynaBean _abean=ocb.getUserNamePassword("Usr"+(isORG?a0100:object_id));
    						            	String username=(String)_abean.get("username");
    										String password=(String)_abean.get("password");
	    						        	String content=subarr.length>5?subarr[5]:"";//ocbo.getEmailContent(subarr[8], subarr[2], objectName,templateBo,1);
	    						        	content+="<br><br><a href='"+url_p+"performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+vo.getString("name")+"</a>";;
	    						        	content+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+df.format(new Date());
	    						        	if(isORG)
	    						            	ocbo.sendMessage(title,content, a0100, "usr");
	    						        	else
	    						        		ocbo.sendMessage(title,content, object_id, "usr");
	    						        	String url="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=3&opt=1&planid="+plan_id+"&object_id="+object_id;
	    						        	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
	    									{
	    						        		
	    						        		
	    						        		String aurl=url+"&pendingCode="+pendingCode+"&appfwd=1";
	    						        		if(isORG)
	    						        		{
	    								    		imip.insertPending(pendingCode,title,content,"usr"+a0100,aurl,0,1,pendingType);
	    						        		}
	    						        		else
	    						        		{
	    						        			imip.insertPending(pendingCode,title,content,"usr"+object_id,aurl,0,1,pendingType);
	    						        		}
	    									}
	    						        	if(isORG)
	    		    			            	pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+a0100, url, 0, 1, pendingType,this.userView);
	    		    			        	else
	    		    			        		pt.insertPending(pendingCode, "P", title, "usr"+subarr[0], "usr"+object_id, url, 0, 1, pendingType,this.userView);


	    						        }
	    							    if(subarr[3].equals("07")&&subarr.length>5)
	    							    {
	    							    	LazyDynaBean abean= new LazyDynaBean();
	    		                        	abean.set("sql",bbf.toString());
	    		                        	abean.set("list", values);
	    		                        	reasonsList.add(bean);
	    							    }
	    		    	         		list.add(buf.toString());
	    	    				}
		    				}
	    				}
	        		}
	    			if(list.size()>0)
	    			{
	    		    	dao.batchUpdate(list);
	    			}
	    		}	
    		}
    		if(reasonsList.size()>0)
    		{
    			for(int i=0;i<reasonsList.size();i++)
    			{
    				LazyDynaBean bean =(LazyDynaBean)reasonsList.get(i);
    				String sql = (String)bean.get("sql");
    				ArrayList list = (ArrayList)bean.get("list");
    				dao.update(sql,list);
    			}
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
    }
	public void setObject_sp_flag(String object_id,String plan_id,String sp_flag)
	{
		try
		{
			String sql = "update per_object set sp_flag='"+sp_flag+"'";
			if(sp_flag.equals("07"))
				sql+=",currappuser=null ";
			sql+=" where object_id='"+object_id+"' and plan_id="+plan_id;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getBodyIdByLevel(String plan_id)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select body_id,");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql.append("level_o");
			else
				sql.append("level");
			sql.append(" as lel from per_mainbodyset where ");
			sql.append(" body_id in (select body_id from per_plan_body where plan_id = "+plan_id+")");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				map.put(this.frowset.getString("lel"),this.frowset.getString("body_id"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getObjectName(String plan_id,String object_id)
	{
		String name="";
		try
		{
			String sql = "select a0101 from per_object where object_id='"+object_id+"' and plan_id="+plan_id;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				name=this.frowset.getString("a0101");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	public void setCurrappuser(String object_id,String plan_id,String appuser,int opttype)
	{
		try
		{
			String sql = "update per_object set currappuser='"+appuser+"' where plan_id="+plan_id+" and object_id='"+object_id+"'";
			if(opttype==2)
				sql="update per_object set currappuser=null where plan_id="+plan_id+" and object_id='"+object_id+"'";
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean isHaveRecord(String object_id,String mainbody_id,String plan_id,ContentDAO dao)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from per_mainbody where object_id='"+object_id+"' and plan_id="+plan_id;
			sql+=" and mainbody_id='"+mainbody_id+"'";
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public String getReportToInfo(String mainbody_id,String object_id,String nbase,String plan_id,int type)
	{
		String report_to = "";
		try
		{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("plan_type")==0)//匿名计划
			{
				 String _sql="select per_mainbody.*,per_mainbodyset.name from per_mainbody ,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id  and plan_id="+plan_id+" and object_id='"+object_id+"'";
	    		 _sql+=" and mainbody_id='"+mainbody_id+"'";
	    		 this.frowset=dao.search(_sql);
		    	 if(this.frowset.next())
		    		 report_to=this.frowset.getString("name");
			}
			else
			{
		    	if(type==1)
		    	{
		    		sql.append(" select b0110,e0122,e01a1,a0101 from per_mainbody where plan_id="+plan_id);
		    		sql.append(" and object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
	    		}
		    	if(type==2)
	    		{
		    		sql.append(" select b0110,e0122,e01a1,a0101 from per_mainbody_std where ");
		    		sql.append(" object_id='"+object_id+"' and mainbody_id='"+mainbody_id+"'");
	    		}
	    		this.frowset=dao.search(sql.toString());
		    	while(this.frowset.next())
	    		{
		    		report_to+=AdminCode.getCodeName("UN",this.frowset.getString("b0110"))+"/";
		    		report_to+=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/";
		    		report_to+=AdminCode.getCodeName("@K",this.frowset.getString("e01a1"))+"/";
		    		report_to+=this.frowset.getString("a0101");
		    	}
	    		if(report_to.length()<=0)
		    	{
		    		sql.setLength(0);
		    		sql.append(" select b0110,e0122,e01a1,a0101 from usra01 where a0100='"+mainbody_id+"'");
		    		this.frowset=dao.search(sql.toString());
			    	while(this.frowset.next())
			    	{
			    		report_to+=AdminCode.getCodeName("UN",this.frowset.getString("b0110"))+"/";
			    		report_to+=AdminCode.getCodeName("UM",this.frowset.getString("e0122"))+"/";
			    		report_to+=AdminCode.getCodeName("@K",this.frowset.getString("e01a1"))+"/";
			    		report_to+=this.frowset.getString("a0101");
		    		}
	  
		    	}
			}
			this.getFormHM().put("msg", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return report_to;
	}
	public boolean isTZ(String object_id,String plan_id,boolean isORG)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from p04 where plan_id="+plan_id+" and ";
			if(isORG)
				sql+=" b0110";
			else
				sql+=" a0100";
			sql+="='"+object_id+"' and state=-1";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	public void changeData(String plan_id,String object_id,boolean isORG)
	{
	//	P0413	标准分值	Float				系统项
	//	P0415	权重	Float				系统项
	//	P0421	调整后标准分值	Float				系统项
	//	P0423	调整后权重	Float				系统项
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			RecordVo templateVo=new RecordVo("per_template");
			templateVo.setString("template_id", vo.getString("template_id"));
			templateVo = dao.findByPrimaryKey(templateVo);
			//=0分值模板
			String status=templateVo.getString("status");
			StringBuffer view=new StringBuffer("");
			view.append("select p0401,p0421,p0423 from p04 where ");
			view.append("plan_id="+plan_id+" and state=-1 and ");
			if(isORG)
				view.append(" b0110 ");
			else
				view.append(" a0100 ");
			view.append("='"+object_id+"'");
			StringBuffer SQL = new StringBuffer();
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				SQL.append(" update p04 P set (");
				if(status.equals("0"))
			    	SQL.append("P.p0413,");
				SQL.append("P.p0415)=");
				SQL.append("(select ");
				if(status.equals("0"))
		    		SQL.append("T.p0421,");
				SQL.append("T.p0423 from ("+view.toString()+") T where T.p0401=P.p0401 ");
				SQL.append(" and P.state=-1 and P.plan_id="+plan_id);
				SQL.append(" and ");
				if(isORG)
					SQL.append(" P.b0110=");
				else
					SQL.append(" P.a0100=");
				SQL.append("'"+object_id+"') where exists(");
				SQL.append(" select null from ("+view.toString()+") T where T.p0401=P.p0401 ");
				SQL.append(" and P.state=-1 and P.plan_id="+plan_id);
				SQL.append(" and ");
				if(isORG)
					SQL.append(" P.b0110=");
				else
					SQL.append(" P.a0100=");
				SQL.append("'"+object_id+"')");
			}
			else
			{
				SQL.append("update p04 set ");
				if(status.equals("0"))
			    	SQL.append("p0413=T.p0421,");
				SQL.append("p0415=T.p0423 from p04 LEFT JOIN ("+view.toString()+") T");
				SQL.append(" on T.p0401=p04.p0401 where ");
				SQL.append("p04.plan_id="+plan_id);
				SQL.append(" and p04.state=-1 and ");
				if(isORG)
					SQL.append(" p04.b0110 ");
				else
					SQL.append(" p04.a0100 ");
				SQL.append("='"+object_id+"'");
			}
			dao.update(SQL.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	*/
	
	public void execute() throws GeneralException {
		try
		{
			String reject_cause=SafeCode.decode((String)this.getFormHM().get("reject_cause"));
			String object_id=(String)this.getFormHM().get("object_id");
    		String plan_id=(String)this.getFormHM().get("plan_id");
    		String obj=(String)this.getFormHM().get("obj");
    		String sp_flag=(String)this.getFormHM().get("sp_flag");
    		String isSend=(String)this.getFormHM().get("isSend");
    		String current_a0100=(String)this.getFormHM().get("current_a0100");
    		String current_level=(String)this.getFormHM().get("current_level");
    		
    		SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(this.getFrameconn());
    		String agent_name="";
    		if(this.getUserView().getStatus()==4)
    		{
    			agent_name=suob.getAgentInfo(this.getUserView());
    		}
    		else
    		{
    			agent_name=this.getUserView().getUserFullName();
    		}
    		UserView _userview=getUserView(current_a0100);
    		_userview.setServerurl(this.userView.getServerurl());
    		ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,_userview,"4",current_level,"1");
    		bo.setIsEmail(isSend);
    		bo.setAgent_id(this.userView.getA0100());
    		bo.setAgent_name(agent_name);
    		
    		
    		if("02".equals(sp_flag))  //上报目标卡
			{
				  bo.appealSpObject(object_id,obj,current_a0100,plan_id,"Usr","");
			     
			}
    		else if("03".equals(sp_flag))  //批准目标卡
			{
				  bo.approveSpObject(object_id,current_a0100,plan_id,"Usr");
				  bo.optPersonalComment("2");	
			}
    		else if("07".equals(sp_flag))
			{
				bo.rejectSpObject(object_id,current_a0100,plan_id,"Usr",reject_cause,obj);
				if(obj.equalsIgnoreCase(object_id))
					bo.optPersonalComment("3");
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
    }
	
	
	//由业务用户得到关联的自助用户
	public UserView getUserView(String a0100)
	{
		UserView userview = null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
	
			 
		String zpFld = "";
		String pwdFld="";
	    RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	    if (login_vo != null)
		{
					    String login_name = login_vo.getString("str_value");
						int idx = login_name.indexOf(",");
						if (idx != -1)
						{
						    zpFld = login_name.substring(0, idx);
						    if(login_name.length()>idx)
						       pwdFld=login_name.substring(idx+1);
						}
		}
		if("".equals(pwdFld)|| "#".equals(pwdFld))
						   pwdFld="userpassword";
		if("".equals(zpFld)|| "#".equals(zpFld))
						zpFld="username";
		try
		{
					this.frowset=dao.search("select "+zpFld+","+pwdFld+" from UsrA01 where a0100='"+a0100+"'");
			        if(this.frowset.next())
			        {
			        		String name=this.frowset.getString(zpFld)==null?"":this.frowset.getString(zpFld);
			        		String pw=this.frowset.getString(pwdFld)==null?"":this.frowset.getString(pwdFld);
			        		userview = new UserView(name,pw,this.frameconn);
			        		try
							{
			        			userview.canLogin();
							} catch (Exception e)
							{
								e.printStackTrace();
							}
							
			        } else{
			            throw GeneralExceptionHandler.Handle(new Exception("人员库中不存在编号为"+a0100+"的人 ！"));
			        }
		} 
		catch (Exception e)
		{
					e.printStackTrace();
		}    
		return userview;
	}
	
			
}
