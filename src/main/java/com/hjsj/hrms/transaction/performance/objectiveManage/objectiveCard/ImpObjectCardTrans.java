package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *<p>Title:</p> 
 *<p>Description:引入上期目标卡</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2009</p> 
 *@author dengcan
 *@version 4.2
 */

public class ImpObjectCardTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		InputStream in = null;
		try
		{
			String object_id=(String)this.getFormHM().get("object_id");
			String plan_id=(String)this.getFormHM().get("plan_id");
			String opt=(String)this.getFormHM().get("opt");   //0:校验  1：引入
			
			LoadXml loadxml = new LoadXml(this.frameconn, plan_id);
			Hashtable parameter = loadxml.getDegreeWhole();
			String targetUsePrevious = (String)parameter.get("TargetUsePrevious"); // 引入上期目标卡指标			
			boolean signFlag = false;
			if(targetUsePrevious!=null && targetUsePrevious.trim().length()>0)
			{
				String[] items = targetUsePrevious.split(",");						
				for (int i = 0; i < items.length; i++)
				{
					if("attachment".equalsIgnoreCase(items[i]))
						signFlag=true;
				}			
			}
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.userView,plan_id);
			RecordVo planVo=bo.getPlanVo(plan_id);
			int object_type= planVo.getInt("object_type");
			String template_id=planVo.getString("template_id");
			StringBuffer sql=new StringBuffer("select pp.plan_Id from per_plan pp,per_object po where pp.plan_id=po.plan_id  ");
			sql.append(" and  pp.method=2 and pp.cycle="+planVo.getInt("cycle")+" and pp.template_id='"+template_id+"' ");
			if(planVo.getInt("cycle")==3) //月度
			{	
				sql.append(" and ( pp.theyear<'"+planVo.getString("theyear")+"'");
				sql.append(" or ( pp.theyear='"+planVo.getString("theyear")+"' and pp.themonth<'"+planVo.getString("themonth")+"' ) )");
			}
			else if(planVo.getInt("cycle")==2||planVo.getInt("cycle")==1) //季度 || 半年
			{
				sql.append(" and ( pp.theyear<'"+planVo.getString("theyear")+"'");
				sql.append(" or ( pp.theyear='"+planVo.getString("theyear")+"' and pp.thequarter<'"+planVo.getString("thequarter")+"' ) )");
			}
			else if(planVo.getInt("cycle")==0) //年度
			{
				sql.append(" and  pp.theyear<'"+planVo.getString("theyear")+"'");
			}
			else //不定期
			{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String date=format.format(planVo.getDate("start_date"));
				sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+date.substring(0, 4));
				sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"<"+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" ) ");
				sql.append(" or ( "+Sql_switcher.year("start_date")+"="+date.substring(0, 4)+" and "+Sql_switcher.month("start_date")+"="+date.substring(date.indexOf("-")+1, date.lastIndexOf("-"))+" and "+Sql_switcher.day("start_date")+"<"+date.substring(date.lastIndexOf("-")+1)+" ) ) ");
			}
			 //目标卡未审批也允许打分 True, False, 默认为 False
			String noApproveTargetCanScore=(String)parameter.get("NoApproveTargetCanScore");
	    	 if("false".equalsIgnoreCase(noApproveTargetCanScore)){//上期目标卡所在计划启动时选择了“允许对未批准的目标卡进行评分”，就无需判断目标卡是否已批了 zhaoxg add 2016-8-16
	    		 sql.append(" and po.object_id='"+object_id+"' and (sp_flag='03' or sp_flag='06') "); // 引入上期已经批准的目标卡
	    	 }
			if(planVo.getInt("cycle")==3) //月度
				sql.append(" order by pp.theyear desc ,pp.themonth desc ");
			else if(planVo.getInt("cycle")==2||planVo.getInt("cycle")==1) //季度 || 半年
				sql.append(" order by pp.theyear desc ,pp.thequarter desc ");
			else if(planVo.getInt("cycle")==0) //年度
				sql.append(" order by pp.theyear desc ");
			else
				sql.append(" order by pp.start_date desc ");
				
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search(sql.toString());
			RowSet rowSet2=null;
			RowSet rwSet=null;
			String info="0";
			while(rowSet.next())
			{
				String a_plan_id=rowSet.getString("plan_id");
				
				//  获得绩效参数
				LoadXml parameter_content = null;
	         	if(BatchGradeBo.getPlanLoadXmlMap().get(a_plan_id+"")==null)
				{						
	         		parameter_content = new LoadXml(this.frameconn,a_plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(a_plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(a_plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();										
				
				String sql0="select count(p0400) from P04 where plan_id="+a_plan_id+"  and ";
				if(object_type==1 || object_type==3 || object_type==4) //团队
				{
					sql0+=" b0110='"+object_id+"'";
				}
				else
				{
					sql0+=" a0100='"+object_id+"'";
				}
				rowSet2=dao.search(sql0);
				if(rowSet2.next() && rowSet2.getInt(1)>0)
				{
					sql0="select * from P04 where plan_id="+a_plan_id+" and a0100='"+object_id+"' and ( chg_type<>3 or chg_type is null) order by seq";
					if(object_type==1 || object_type==3 || object_type==4) //团队
						sql0="select * from P04 where plan_id="+a_plan_id+" and b0110='"+object_id+"' and ( chg_type<>3 or chg_type is null) order by seq";
					rowSet2=dao.search(sql0);
										
					StringBuffer bufSql = new StringBuffer();
					StringBuffer buf = new StringBuffer();
					if(targetUsePrevious!=null && targetUsePrevious.trim().length()>0)
					{
						String[] items = targetUsePrevious.split(",");						
						for (int i = 0; i < items.length; i++)
						{
							String temp=items[i].trim();
							if(temp==null || temp.length()<=0)
								continue;
							if("p0400,b0110,e0122,e01a1,nbase,a0100,a0101,p0401,p0407,plan_id,item_id,fromflag,state,seq,p0424,itemtype,attachment".indexOf(temp.toLowerCase()) == -1)
							{
								bufSql.append("," + temp.toUpperCase());
								buf.append(",?");
							}								
						}
					}
					sql0="insert into p04 (p0400,b0110,";
					if(object_type==2)
						sql0+="e0122,e01a1,nbase,a0100,a0101,";
					sql0+="p0401,p0407,plan_id,item_id,fromflag,state,seq,p0424,itemtype";
					if(bufSql!=null && bufSql.toString().trim().length()>0)
						sql0+=bufSql.toString();
					else
						sql0+=",p0413,p0415";
					
					KhTemplateBo khTemplateBo = new KhTemplateBo(this.getFrameconn(), plan_id);
					
					String targetPoints = "";
					ArrayList targetPointsList = khTemplateBo.getTargetPointsList();
					for (int i = 0; i < targetPointsList.size(); i++)
					{
						FieldItem fieldItem = (FieldItem) targetPointsList.get(i);
						String itemtype = fieldItem.getItemtype();
						String fieldid = fieldItem.getItemid().toLowerCase();
						if(sql0.toLowerCase().indexOf(fieldid.toLowerCase()) > -1) {
							continue ;
						}
						targetPoints+=","+fieldid;
					}
					sql0 += targetPoints;
					sql0+=")values(?,?,";
					if(object_type==2)
						sql0+="?,?,?,?,?,";
					sql0+="?,?,?,?,?,?,?,?,?";
					if(buf!=null && buf.toString().trim().length()>0)
						sql0+=buf.toString();
					else
						sql0+=",?,?";
					
					for (int i = 0; i < targetPointsList.size(); i++)
					{
						FieldItem fieldItem = (FieldItem) targetPointsList.get(i);
						String fieldid = fieldItem.getItemid().toLowerCase();
						if(targetPoints.indexOf(fieldid) > -1) {
							sql0+=",?";
						}
					}
					sql0+=")";
					
					ArrayList list=new ArrayList();
					ArrayList p04List=new ArrayList();
					while(rowSet2.next())
					{
						ArrayList tempList=new ArrayList();
						IDGenerator idg=new IDGenerator(2,this.frameconn);
						String id=idg.getId("P04.P0400");
						
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("id",id);
						abean.set("p0400",isNull(rowSet2.getString("p0400")));
						abean.set("b0110",isNull(rowSet2.getString("b0110")));
						abean.set("e0122",isNull(rowSet2.getString("e0122")));
						abean.set("e01a1",isNull(rowSet2.getString("e01a1")));
						abean.set("a0101",isNull(rowSet2.getString("a0101")));
						p04List.add(abean);						
						
						tempList.add(new Integer(id));
						tempList.add(rowSet2.getString("b0110"));
						if(object_type==2)
						{
							tempList.add(rowSet2.getString("e0122"));
							tempList.add(rowSet2.getString("e01a1"));
							tempList.add(rowSet2.getString("nbase"));
							tempList.add(rowSet2.getString("a0100"));
							tempList.add(rowSet2.getString("a0101"));
						}
						tempList.add(rowSet2.getString("p0401"));
						tempList.add(rowSet2.getString("p0407"));												
						tempList.add(new Integer(plan_id));
						tempList.add(new Integer(rowSet2.getInt("item_id")));
						tempList.add(new Integer(rowSet2.getInt("fromflag")));
						tempList.add(new Integer(0));
						tempList.add(new Integer(rowSet2.getInt("seq")));
						tempList.add(rowSet2.getString("p0424"));
						tempList.add(new Integer(rowSet2.getInt("itemtype")));						
						
						if(targetUsePrevious!=null && targetUsePrevious.trim().length()>0)
						{
							String[] items = targetUsePrevious.split(",");						
							for (int i = 0; i < items.length; i++)
							{
								String temp=items[i].trim();
								if(temp==null || temp.length()<=0)
									continue;							    
								if ("p0400,b0110,e0122,e01a1,nbase,a0100,a0101,p0401,p0407,plan_id,item_id,fromflag,state,seq,p0424,itemtype,attachment".indexOf(temp.toLowerCase()) == -1)
								{
									FieldItem fielditem = DataDictionary.getFieldItem(temp);
									String itemtype = fielditem.getItemtype();
									int decimalwidth = fielditem.getDecimalwidth();
									if("N".equalsIgnoreCase(itemtype))
									{
										if (decimalwidth == 0)										
											tempList.add(new Integer(rowSet2.getInt(temp)));
										else
											tempList.add(new Double(rowSet2.getDouble(temp)));
									}else
										tempList.add(rowSet2.getString(temp));																											
								}								
							}
						}else
						{
							tempList.add(new Double(rowSet2.getDouble("p0413")));
							tempList.add(new Double(rowSet2.getDouble("p0415")));
						}
						for (int i = 0; i < targetPointsList.size(); i++)
						{
							FieldItem fieldItem = (FieldItem) targetPointsList.get(i);
							String itemtype = fieldItem.getItemtype();
							String fieldid = fieldItem.getItemid().toLowerCase();
							
							if(targetPoints.indexOf(fieldid) > -1) {
								int decwidth = fieldItem.getDecimalwidth();
								
								if ("N".equals(itemtype) && decwidth == 0)
									tempList.add(rowSet2.getInt(fieldid));
								else if ("N".equals(itemtype) && decwidth > 0)
									tempList.add(rowSet2.getDouble(fieldid));
								else if ("D".equals(itemtype))
									tempList.add(rowSet2.getDate(fieldid));
								else
									tempList.add(rowSet2.getString(fieldid));
							}
							
						}
						
						list.add(tempList);
					}
					if(list.size()>0)
					{
						info="1";
						if(opt!=null && "1".equalsIgnoreCase(opt))
						{
							//  删除本期目标卡和目标卡中任务对应的附件   JinChunhai 2011.08.25							
							String str="";												
							if(parameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)parameter.get("TaskSupportAttach")))
								str=("delete from per_article where plan_id="+plan_id+" and a0100='" + object_id + "' and article_type=3 ");
							
							String sql_1="delete from p04 where plan_id="+plan_id+" and a0100='"+object_id+"' ";
							if(object_type==1 || object_type==3 || object_type==4) //团队
							{
								sql_1="delete from p04 where plan_id="+plan_id+" and b0110='"+object_id+"' ";
								
								if(parameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)parameter.get("TaskSupportAttach")))
								{
									// 获取团队负责人信息
									LazyDynaBean bean = getMainbodyBean(plan_id,object_id);	
									String mainbody_id = (String)bean.get("mainbody_id");
									if(mainbody_id!=null && mainbody_id.trim().length()>0)
										str=("delete from per_article where plan_id="+plan_id+" and a0100='" + mainbody_id + "' and article_type=3 ");																												
								}
							}
							dao.delete(sql_1,new ArrayList());
							if(parameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)parameter.get("TaskSupportAttach")))
							{
								if(str!=null && str.trim().length()>0)
									dao.delete(str.toString(),null);
							}
							
							
							//  引入上期的目标卡 
							dao.batchInsert(sql0,list);
							
							//  复制指标任务附件  JinChunhai 2011.08.25		当考核计划勾选了"任务支持附件上传"参数：才复制任务附件
							if(signFlag && (parameter.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)parameter.get("TaskSupportAttach"))) && (params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach"))))
				        	{	
								for (int i = 0; i < p04List.size(); i++)
								{
									LazyDynaBean abean = (LazyDynaBean) p04List.get(i);
									String p0400 = (String) abean.get("p0400");
									String b0110 = (String) abean.get("b0110");
									String e0122 = (String) abean.get("e0122");
									String e01a1 = (String) abean.get("e01a1");
									String a0101 = (String) abean.get("a0101");
									String id = (String) abean.get("id");									
								
									LazyDynaBean bean = new LazyDynaBean();
									String strsql = "";
									if (object_type == 2)					
										strsql = ("select * from per_article where plan_id="+a_plan_id+" and a0100='"+object_id+"' and article_type=3 and task_id='"+p0400+"'");
									else
									{
										// 取得当前团队计划的负责人员
										bean = getMainbodyBean(plan_id,object_id);						
										strsql = ("select * from per_article where plan_id="+a_plan_id+" and article_type=3 and task_id='"+p0400+"'");					
									}													
									rwSet=dao.search(strsql);
									while(rwSet.next())
									{																						
										int article_id=0;
										RecordVo avo=new RecordVo("per_article");
										article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
										avo.setInt("article_id", article_id);
										avo.setInt("plan_id",Integer.parseInt(plan_id));							
										
										if(object_type == 2)
										{							
											avo.setString("b0110",b0110);
											avo.setString("e0122",e0122);
											avo.setString("e01a1",e01a1);
											avo.setString("nbase","USR");																				
											avo.setString("a0100",object_id);
											avo.setString("a0101",a0101);
										}else
										{		
											avo.setString("b0110",(String)bean.get("b0110"));
											avo.setString("e0122",(String)bean.get("e0122"));
											avo.setString("e01a1",(String)bean.get("e01a1"));
											avo.setString("nbase","USR");																				
											avo.setString("a0100",(String)bean.get("mainbody_id"));
											avo.setString("a0101",(String)bean.get("a0101"));							
										}						
								//		avo.setString("article_name",rwSet.getString("article_name"));
										avo.setString("content",rwSet.getString("content"));
								//		avo.setString("affix",rwSet.getString("affix"));
								//		avo.setString("ext",rwSet.getString("ext"));
										avo.setInt("article_type", rwSet.getInt("article_type"));
										avo.setInt("fileflag",rwSet.getInt("fileflag"));
										avo.setInt("state",rwSet.getInt("state"));
										avo.setString("description",rwSet.getString("description"));										
										avo.setInt("task_id", Integer.parseInt(id));										
										dao.addValueObject(avo);
																										
					        	    	String sqlStr = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
					        	    	ArrayList paramList = new ArrayList();
					        	    	paramList.add(rwSet.getString("ext"));
								    	// blob字段保存,数据库中差异
									    switch (Sql_switcher.searchDbServer()) 
									    {
									    	case Constant.ORACEL:
									    		in = rwSet.getBinaryStream("affix");
									    		Blob blob = getOracleBlob(in, "per_article",article_id);
									    		paramList.add(blob);
									    		paramList.add(rwSet.getString("Article_name"));
									    		paramList.add(article_id);
									    		break;
								    		default:
								    			byte[] data = rwSet.getBytes("affix");
								    			// a_vo.setObject("affix",data);
								    			paramList.add(data);
								    			paramList.add(rwSet.getString("Article_name"));
								    			paramList.add(article_id);
									    		break;
								    	}
								    	dao.update(sqlStr,paramList);												
									}
					        	}
				        	}
							
						}
						break;
					}					
				}
			}
			this.getFormHM().put("info",info);
			this.getFormHM().put("object_id",object_id);
			this.getFormHM().put("plan_id",plan_id);
			
			if (rowSet != null)
				rowSet.close();
			if (rowSet2 != null)
				rowSet2.close();
			if (rwSet != null)
				rwSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
            PubFunc.closeIoResource(in);
        }
	}
	
	/**
	 * 取得当前团队计划的负责人员
	 * @param plan_id
	 * @param object_id
	 * @return
	 */
	public LazyDynaBean getMainbodyBean(String plan_id,String object_id)
	{
		LazyDynaBean abean=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1");
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("body_id",rowSet.getString("body_id"));				
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("e0122",rowSet.getString("e0122"));
				abean.set("e01a1",rowSet.getString("e01a1"));				
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"");
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
				abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}

	private Blob getOracleBlob(InputStream file,String tablename,int article_id) throws FileNotFoundException, IOException 
	{
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix from ");
		strSearch.append(tablename);
		strSearch.append(" where article_id=");
		strSearch.append(article_id);		
		strSearch.append(" FOR UPDATE");
			
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
		strInsert.append(article_id);
		OracleBlobUtils blobutils=new OracleBlobUtils(this.frameconn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	
	public String isNull(String str)
	{
		if (str == null || str.trim().length()<=0)
			str = "";
		return str;
	}
	
}
