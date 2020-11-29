package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:上报跟踪指标</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 12, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class AppealTrancePointTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");  //opt : 02报批  03:批准   07:驳回
			String object_id=(String)this.getFormHM().get("object_id");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String body_id=(String)this.getFormHM().get("body_id");
			String model=(String)this.getFormHM().get("model");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.getUserView(),model,body_id,"2"); 
			String info="";
			if(!"07".equals(opt))
				info=bo.validateFollowPointMustFill(1);
			if(info.length()>0){
			    String temp = info.replaceAll("\r\n", "<br>");
			    this.getFormHM().put("info", temp);
				throw  new Exception(info);
			}
			else {
			    info = "办理成功！";
			    this.getFormHM().put("info", info);
			}
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
	/*		this.frowset=dao.search("select id from per_object where object_id='"+object_id+"' and plan_id="+plan_id);
			int id=0;
			if(this.frowset.next())
					id=this.frowset.getInt(1);*/
			if("07".equals(opt))
			{
				String reject_cause=SafeCode.decode((String)this.getFormHM().get("reject_cause"));
				
				String sql="update per_object set  trace_sp_flag='"+opt+"' where plan_id="+plan_id+" and object_id='"+object_id+"'";
				dao.update(sql);
				
				sql="update per_mainbody set  reasons=null where plan_id="+plan_id+" and object_id='"+object_id+"'";
				dao.update(sql);
				
				this.frowset=dao.search("select id from per_mainbody where object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' and plan_id="+plan_id);
				int id=0;
				if(this.frowset.next())
						id=this.frowset.getInt(1);
				RecordVo vo=new RecordVo("per_mainbody");
				vo.setInt("id",id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("reasons",reject_cause);
				dao.updateValueObject(vo);
				
				//推送待办 chent 20151031 start
				bo.postPendingTask(object_id, "1");
				//推送待办 chent 20151031 end
				//--------------------------------------华丽的分割线   铁血网  赵旭光2013-4-1---------------------------------
				String istargetTasktracking = "";//是否发邮件
				String targetTasktracking = "";  //邮件模版
			    this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			    if ( this.frowset.next())
			    {
				String str_value = this.frowset.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value)))
				{
		
				} else
				{
				    Document doc = PubFunc.generateDom(str_value);
				    String xpath = "//Per_Parameters";
				    XPath xpath_ = XPath.newInstance(xpath);
				    Element ele = (Element) xpath_.selectSingleNode(doc);
				    Element child;
				    if (ele != null)
				    {
						child = ele.getChild("TraceItem");
						if (child != null)
						{
							istargetTasktracking = child.getAttributeValue("email");
							targetTasktracking = child.getAttributeValue("template");
							String sp = "";
							String level = "";
							if("true".equalsIgnoreCase(istargetTasktracking))
							{
							    this.frowset = dao.search("select parameter_content  from per_plan where plan_id = "+plan_id+"");
							    if ( this.frowset.next())
							    {
									String parameter_content = this.frowset.getString("parameter_content");
									if (parameter_content == null || (parameter_content != null && "".equals(parameter_content)))
									{
							
									} else
									{
									    Document doc1 = PubFunc.generateDom(parameter_content);
									    String xpath1 = "//PerPlan_Parameter";
									    XPath xpath1_ = XPath.newInstance(xpath1);
									    Element ele1 = (Element) xpath1_.selectSingleNode(doc1);
									    String spByBodySeq ;
									    if (ele1 != null)
									    {
									    	spByBodySeq = ele1.getAttributeValue("SpByBodySeq");
											if (spByBodySeq != null)
											{
												sp = spByBodySeq;
											}
									    }
									}
							    }
								RowSet rowSet=dao.search("select id,a0101 from per_object where object_id='"+object_id+"' and plan_id="+plan_id);
								int id1=0;
								String a0101="";
								if(rowSet.next())
								{	id1=rowSet.getInt("id");
									a0101=rowSet.getString("a0101");
								}
								LazyDynaBean templateBo=bo.getTemplateMailInfo(targetTasktracking);
								if("true".equalsIgnoreCase(sp)){
									this.frowset = dao.search("select mainbody_id from per_mainbody where object_id ='"+object_id+"'and  plan_id="+plan_id+" and sp_flag is not null and sp_seq =(select max(sp_seq) from per_mainbody where object_id ='"+object_id+"'and  plan_id="+plan_id+" and sp_seq <= '1')");
									
									if(this.frowset.next()){
										sp = this.frowset.getString("mainbody_id");
									}
								}else if("false".equalsIgnoreCase(sp)){
									String sql1="select * from per_mainbody pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id and  pmb.object_id="+object_id+" and pmb.sp_flag is not null and plan_id ="+plan_id+" and ";
									if(Sql_switcher.searchDbServer()==Constant.ORACEL)
										sql1+=" level_o";
									else
										sql1+=" level ";
									sql1+=" in (-2,-1,0,1)";
									this.frowset = dao.search(sql1);
									if(this.frowset.next()){
										sp = this.frowset.getString("mainbody_id");
										if(Sql_switcher.searchDbServer()==Constant.ORACEL)
											level = this.frowset.getString("level_o");
										else
											level = this.frowset.getString("level");
									}
								}else{
									sp="0";
								}
								bo.appealSpObject(object_id, this.userView.getA0100(), plan_id, a0101, templateBo,opt,sp,level);
							}
							
						}
				    }
				}
			    }				
			
				/*
				RecordVo vo=new RecordVo("per_object");
				vo.setInt("id",id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("trace_sp_flag","07");
				ParseXmlBo _bo=new ParseXmlBo(this.getFrameconn());
				String reasons=_bo.produceRecord(object_id,plan_id,this.userView.getA0100(),"Usr",reject_cause,"07","0","","",""); 
				vo.setString("reasons",reasons);
				dao.updateValueObject(vo);*/
			}
			else
			{
				if("02".equals(opt)|| "03".equals(opt)){
					String istargetTasktracking = "";//是否发邮件
					String targetTasktracking = "";  //邮件模版
				    this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				    if ( this.frowset.next())
				    {
					String str_value = this.frowset.getString("str_value");
					if (str_value == null || (str_value != null && "".equals(str_value)))
					{
			
					} else
					{
					    Document doc = PubFunc.generateDom(str_value);
					    String xpath = "//Per_Parameters";
					    XPath xpath_ = XPath.newInstance(xpath);
					    Element ele = (Element) xpath_.selectSingleNode(doc);
					    Element child;
					    if (ele != null)
					    {
							child = ele.getChild("TraceItem");
							if (child != null)
							{
								istargetTasktracking = child.getAttributeValue("email");
								targetTasktracking = child.getAttributeValue("template");
								String sp = "";
								String level = "";
								if("true".equalsIgnoreCase(istargetTasktracking))
								{
								    this.frowset = dao.search("select parameter_content  from per_plan where plan_id = "+plan_id+"");
								    if ( this.frowset.next())
								    {
										String parameter_content = this.frowset.getString("parameter_content");
										if (parameter_content == null || (parameter_content != null && "".equals(parameter_content)))
										{
								
										} else
										{
										    Document doc1 = PubFunc.generateDom(parameter_content);
										    String xpath1 = "//PerPlan_Parameter";
										    XPath xpath1_ = XPath.newInstance(xpath1);
										    Element ele1 = (Element) xpath1_.selectSingleNode(doc1);
										    String spByBodySeq ;
										    if (ele1 != null)
										    {
										    	spByBodySeq = ele1.getAttributeValue("SpByBodySeq");
												if (spByBodySeq != null)
												{
													sp = spByBodySeq;
												}
										    }
										}
								    }
									RowSet rowSet=dao.search("select id,a0101 from per_object where object_id='"+object_id+"' and plan_id="+plan_id);
									int id=0;
									String a0101="";
									if(rowSet.next())
									{	id=rowSet.getInt("id");
										a0101=rowSet.getString("a0101");
									}
									LazyDynaBean templateBo=bo.getTemplateMailInfo(targetTasktracking);
									if("true".equalsIgnoreCase(sp)){
										this.frowset = dao.search("select mainbody_id from per_mainbody where object_id ='"+object_id+"'and  plan_id="+plan_id+" and sp_flag is not null and sp_seq =(select max(sp_seq) from per_mainbody where object_id ='"+object_id+"'and  plan_id="+plan_id+" and sp_seq <= '1')");
										if(this.frowset.next()){
											sp = this.frowset.getString("mainbody_id");
										}
									}else if("false".equalsIgnoreCase(sp)){
										String sql="select * from per_mainbody pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id and  pmb.object_id="+object_id+" and pmb.sp_flag is not null and plan_id ="+plan_id+" and ";
										if(Sql_switcher.searchDbServer()==Constant.ORACEL)
											sql+=" level_o";
										else
											sql+=" level ";
										sql+=" in (-2,-1,0,1)";
										this.frowset = dao.search(sql);
										if(this.frowset.next()){
											sp = this.frowset.getString("mainbody_id");
											if(Sql_switcher.searchDbServer()==Constant.ORACEL)
												level = this.frowset.getString("level_o");
											else
												level = this.frowset.getString("level");
										}
									}else{
										sp="0";
									}
									bo.appealSpObject(object_id, this.userView.getA0100(), plan_id, a0101, templateBo,opt,sp,level);
								}
								
							}
					    }
					}
				    }				
				}
				
				String sql="update per_object set  trace_sp_flag='"+opt+"' where plan_id="+plan_id+" and object_id='"+object_id+"'";
				dao.update(sql);
				//推送待办 chent 20151031 start
				bo.postPendingTask(object_id, "2");
				//推送待办 chent 20151031 end
			
				
			}
			if(info.length()==0){
                info = "交办成功！";
                this.getFormHM().put("info", info);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
