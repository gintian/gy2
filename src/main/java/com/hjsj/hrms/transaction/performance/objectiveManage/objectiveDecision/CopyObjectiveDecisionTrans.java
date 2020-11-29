package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveDecision;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchObjectiveDecisionTrans.java</p>
 * <p>Description>:目标卡制定</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 05, 2010 09:15:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class CopyObjectiveDecisionTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		RowSet rowSet = null;
		RowSet rs = null;
		RowSet rwSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
//			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt = (String)this.getFormHM().get("opt");	
			String plan_id = "";			
			String object_id = "";
			String object_type = "";
			if("1".equals(opt))
			{				
				plan_id = (String)this.getFormHM().get("plan_id");
				object_id = PubFunc.decryption(SafeCode.decode((String)this.getFormHM().get("object_id")));	
				object_type = (String)this.getFormHM().get("object_type");	
				String info="";
				String sql="";
				if("2".equals(object_type))
				{
					sql=("select count(*) num from p04,per_template_item pti where p04.item_id=pti.item_id and pti.Kind=2 and p04.plan_id="+plan_id+" and lower(p04.nbase)='usr' and p04.a0100='"+object_id+"'");
				}else{
					sql=("select count(*) num from p04,per_template_item pti where p04.item_id=pti.item_id and pti.Kind=2 and p04.plan_id="+plan_id+" and p04.b0110='"+object_id+"'");
				}		
				rowSet=dao.search(sql);									
				if(rowSet.next())
					if(rowSet.getInt("num")==0)
						info="所选考核对象没有设置目标卡信息！";						
									
				this.getFormHM().put("info",info);
				this.getFormHM().put("plan_id",plan_id);
				this.getFormHM().put("object_id",object_id);
				this.getFormHM().put("object_type",object_type);
								
			}else if("2".equals(opt))
			{	
				ArrayList list=new ArrayList();				

				plan_id = (String)this.getFormHM().get("plan_id");
				String yorn = (String)this.getFormHM().get("yorn");
				object_id = (String)this.getFormHM().get("object_id");	
				object_type = (String)this.getFormHM().get("object_type");
				String object_past = (String)this.getFormHM().get("object_past");																				
				
				//  获得绩效参数
				LoadXml parameter_content = null;
	         	if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{						
	         		parameter_content = new LoadXml(this.frameconn,plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();								
				
				if(object_past!=null && object_past.length()>0)
				{
					String[] object_array = object_past.split(",");				
					StringBuffer buf = new StringBuffer();
					for (int i = 1; i < object_array.length; i++)
						buf.append(",'" + object_array[i].toUpperCase()+"'");
										
					// 删除任务和任务附件
					if("y".equalsIgnoreCase(yorn))
						delItemid(plan_id,object_type,params,object_array);					
										
					// 获得被复制考核对象的目标卡信息
					HashMap map = objectTargetMap(plan_id,object_type,buf);					
					
					//获得被复制考核对象的任务附件信息
					HashMap hashMap=new HashMap();
					if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
						hashMap = objectRwFjMap(plan_id,object_type,object_array);
					
					String strSql="";				
					if("2".equals(object_type))
					{
						strSql=("select b0110,e0122,e01a1,a0101,a0100 from usrA01 where a0100 IN ("+ buf.substring(1) + ")");
						rowSet=dao.search(strSql);
					
						String b0110 = "";
						String e0122 = "";
						String e01a1 = "";
						String a0101 = "";
						String sql = "";
						String a0100 = "";
															
						int planid = Integer.parseInt(plan_id);
						while(rowSet.next())
						{						
							b0110 = rowSet.getString("b0110");
							e0122 = rowSet.getString("e0122");
							e01a1 = rowSet.getString("e01a1");
							a0101 = rowSet.getString("a0101");
							a0100 = rowSet.getString("a0100");							
							ArrayList values = (ArrayList) map.get(a0100);														

							sql = ("select * from p04 where plan_id="+plan_id+" and a0100='"+object_id+"' and ((chg_type <> 3) or (chg_type is null))");													
							rs=dao.search(sql);
							while(rs.next())
							{
								String p0400 = rs.getString("p0400");
								String p0401 = rs.getString("p0401");
								int right = 0;
								if(values!=null && values.size()>0)
								{				
									for(int j=0;j<values.size();j++)
									{					
										String pointcode=(String)values.get(j);						       		
										if(pointcode.equalsIgnoreCase(p0401))
						       			{
											String strSqls=("delete from p04 where plan_id="+plan_id+" and a0100 ='"+a0100+"' and p0401='"+p0401+"'");																
											dao.delete(strSqls.toString(),null);
						       			}
						       			else{
//						       				right = 0;
							       			continue;
						       			}		 	
									}
								}
//								if(right == 1)
//								{									
//									String strSqls=("delete from p04 where plan_id="+plan_id+" and a0100 ='"+a0100+"' and p0401='"+p0401+"'");																
//									dao.delete(strSqls.toString(),null);
//								}
								
								RecordVo vo = new RecordVo("p04");	
								IDGenerator idg=new IDGenerator(2,frameconn);
								String id=idg.getId("P04.P0400");
								vo.setInt("p0400", Integer.parseInt(id));
								vo.setInt("plan_id", planid);
								vo.setString("b0110", b0110);
								vo.setString("e0122", e0122);
								vo.setString("e01a1", e01a1);
								vo.setString("nbase", "USR");							
								vo.setString("a0100", a0100);							
								vo.setString("a0101", a0101);
								vo.setInt("seq", rs.getInt("seq"));
								vo.setInt("itemtype", rs.getInt("itemtype"));
								
							    list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
								for (int i = 0; i < list.size(); i++)
							    {
									FieldItem item = (FieldItem) list.get(i);
									String itemtype=item.getItemtype();
									int decimalwidth = item.getDecimalwidth();
									
									String itemid = item.getItemid();
									if("rater".equalsIgnoreCase(itemid))
										continue;
									if("F_p0400".equalsIgnoreCase(itemid))
										continue;
									if("p0415".equalsIgnoreCase(itemid))
										vo.setDouble(itemid, rs.getDouble(itemid));											
									else if ("p0400,plan_id,b0110,e0122,e01a1,nbase,a0100,a0101,seq,itemtype,p0421,p0423".indexOf(itemid.toLowerCase()) == -1)
								    {
										if (rs.getObject(itemid) != null)
										{
	
											if ("N".equalsIgnoreCase(itemtype))
											{
												if (decimalwidth == 0)
												{
													int value = Integer.parseInt(PubFunc.round(rs.getString(itemid), 0));
													vo.setInt(itemid, value);
												} else
													vo.setDouble(itemid, rs.getDouble(itemid));
	
											} else if ("A".equalsIgnoreCase(itemtype) || "M".equalsIgnoreCase(itemtype))
												vo.setString(itemid, rs.getString(itemid));
											else if ("D".equalsIgnoreCase(itemtype))
												vo.setDate(itemid, rs.getDate(itemid));
										}									
								    }								
							    }		
								vo.setInt("chg_type", 0);
								vo.setInt("state", 0);
								dao.addValueObject(vo);
								
								
								
							
								//  复制指标任务附件  JinChunhai 2011.08.25		当考核计划勾选了"任务支持附件上传"参数：才复制任务附件
								if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
					        	{
									ArrayList valueList = (ArrayList) hashMap.get(a0100);		
									String strsql = ("select * from per_article where plan_id="+plan_id+" and a0100='"+object_id+"' and article_type=3 and task_id='"+p0400+"'");													
									rwSet=dao.search(strsql);
									while(rwSet.next())
									{										
										if(valueList!=null && valueList.size()>0)
										{																									
											for(int j=0;j<valueList.size();j++)
											{					
												String pointcode=(String)valueList.get(j);						       		
												if(pointcode.equalsIgnoreCase(p0400))
								       			{
													String strSqls=("delete from per_article where plan_id="+plan_id+" and a0100 ='"+a0100+"' and article_type=3 and task_id='"+p0400+"'");																
													dao.delete(strSqls.toString(),null);
								       			}
								       			else
									       			continue;								       					 	
											}
										}
										
										int article_id=0;
										RecordVo avo=new RecordVo("per_article");
										article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
										avo.setInt("article_id", article_id);
										avo.setInt("plan_id",Integer.parseInt(plan_id));										
										avo.setString("b0110",b0110);
										avo.setString("e0122",e0122);
										avo.setString("e01a1",e01a1);
										avo.setString("nbase","USR");																				
										avo.setString("a0100",a0100);
										avo.setString("a0101",a0101);
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
										
																				
								    	// blob字段保存,数据库中差异
								    	InputStream ism = null;
										PreparedStatement pt = null;
										try{
											String sqlStr = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
											pt = this.frameconn.prepareStatement(sqlStr);
											pt.setString(1, rwSet.getString("ext"));

								    		switch (Sql_switcher.searchDbServer()) 
								    		{
									    		case Constant.ORACEL:
									    			
									    			ism = rwSet.getBinaryStream("affix");
									    			Blob blob = getOracleBlob(ism, "per_article",article_id);
									    			pt.setBlob(2, blob);
									    			pt.setString(3, rwSet.getString("Article_name"));
									    			pt.setInt(4, article_id);
									    			break;
									    		default:
									    			byte[] data = rwSet.getBytes("affix");
									    			// a_vo.setObject("affix",data);
									    			pt.setBytes(2, data);
									    			pt.setString(3, rwSet.getString("Article_name"));
									    			pt.setInt(4, article_id);
									    			break;
								    		}

											// 打开Wallet
											dbS.open( this.frameconn, sqlStr);
											pt.execute();
								    	}finally{
											PubFunc.closeResource(pt);
											PubFunc.closeResource(ism);
								    	}

									}
					        	}
								
							}
						}
					}else
					{
						for (int j = 1; j < object_array.length; j++)
						{
							String b0110=object_array[j].toUpperCase();
							ArrayList values = (ArrayList) map.get(b0110);
							
							int planid = Integer.parseInt(plan_id);
							String sql = ("select * from p04 where plan_id="+plan_id+" and b0110='"+object_id+"' and ((chg_type <> 3) or (chg_type is null))");													
							rs=dao.search(sql);
							while(rs.next())
							{
								String p0400 = rs.getString("p0400");
								String p0401 = rs.getString("p0401");
								int right = 0;
								if(values==null || values.size()<=0)
								{
									
								}else{				
									for(int k=0;k<values.size();k++)
									{					
										String pointcode=(String)values.get(k);						       		
										if(pointcode.equalsIgnoreCase(p0401))
						       			{
											String strSqls=("delete from p04 where plan_id="+plan_id+" and b0110 ='"+b0110+"' and p0401='"+p0401+"'");																
											dao.delete(strSqls.toString(),null);
						       			}
						       			else{
							       			continue;
						       			}			 	
									}
								}
//								if(right == 1)
//								{
//									String strSqls=("delete from p04 where plan_id="+plan_id+" and b0110 ='"+b0110+"' and p0401='"+p0401+"'");																
//									dao.delete(strSqls.toString(),null);
//								}
								
								RecordVo vo = new RecordVo("p04");	
								IDGenerator idg=new IDGenerator(2,frameconn);
								String id=idg.getId("P04.P0400");
								vo.setInt("p0400", Integer.parseInt(id));
								vo.setInt("plan_id", planid);
								vo.setString("b0110", b0110);
//								vo.setString("e0122", "");
//								vo.setString("e01a1", "");
//								vo.setString("nbase", "USR");							
//								vo.setString("a0100", "");							
//								vo.setString("a0101", "");
								vo.setInt("seq", rs.getInt("seq"));
								vo.setInt("itemtype", rs.getInt("itemtype"));																
								
							    list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
								for (int i = 0; i < list.size(); i++)
							    {
									FieldItem item = (FieldItem) list.get(i);
									String itemtype=item.getItemtype();
									int decimalwidth = item.getDecimalwidth();
									
									String itemid = item.getItemid();									
									if("F_p0400".equalsIgnoreCase(itemid))
										continue;
									if("p0415".equalsIgnoreCase(itemid))
										vo.setDouble(itemid, rs.getDouble(itemid));								
									else if ("p0400,plan_id,b0110,e0122,e01a1,nbase,a0100,a0101,seq,itemtype,p0421,p0423".indexOf(itemid.toLowerCase()) == -1)
								    {
										if (rs.getObject(itemid) != null)
										{
	
											if ("N".equalsIgnoreCase(itemtype))
											{
												if (decimalwidth == 0)
												{
													int value = Integer.parseInt(PubFunc.round(rs.getString(itemid), 0));
													vo.setInt(itemid, value);
												} else
													vo.setDouble(itemid, rs.getDouble(itemid));
	
											} else if ("A".equalsIgnoreCase(itemtype) || "M".equalsIgnoreCase(itemtype))
												vo.setString(itemid, rs.getString(itemid));
											else if ("D".equalsIgnoreCase(itemtype))
												vo.setDate(itemid, rs.getDate(itemid));
										}									
								    }								
							    }		
								vo.setInt("chg_type", 0);
								vo.setInt("state", 0);
								dao.addValueObject(vo);
								
								
								
								//  复制指标任务附件  JinChunhai 2011.08.25		当考核计划勾选了"任务支持附件上传"参数：才复制任务附件
								if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
					        	{
									// 取得当前团队计划的负责人员
									LazyDynaBean bean = getMainbodyBean(plan_id,b0110);		
									
									ArrayList valueList = (ArrayList) hashMap.get((String)bean.get("mainbody_id"));		
									String strsql = ("select * from per_article where plan_id="+plan_id+" and article_type=3 and task_id='"+p0400+"'");													
									rwSet=dao.search(strsql);
									while(rwSet.next())
									{										
										if(valueList!=null && valueList.size()>0)
										{																									
											for(int k=0;k<valueList.size();k++)
											{					
												String pointcode=(String)valueList.get(k);						       		
												if(pointcode.equalsIgnoreCase(p0400))
								       			{
													String strSqls=("delete from per_article where plan_id="+plan_id+" and article_type=3 and task_id='"+p0400+"'");																
													dao.delete(strSqls.toString(),null);
								       			}
								       			else
									       			continue;								       					 	
											}
										}
										
										int article_id=0;
										RecordVo avo=new RecordVo("per_article");
										article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
										avo.setInt("article_id", article_id);
										avo.setInt("plan_id",Integer.parseInt(plan_id));										
										avo.setString("b0110",(String)bean.get("b0110"));
										avo.setString("e0122",(String)bean.get("e0122"));
										avo.setString("e01a1",(String)bean.get("e01a1"));
										avo.setString("nbase","USR");																				
										avo.setString("a0100",(String)bean.get("mainbody_id"));
										avo.setString("a0101",(String)bean.get("a0101"));
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
										try(
					        	    		PreparedStatement pt = this.frameconn.prepareStatement(sqlStr);
										) {
											pt.setString(1, rwSet.getString("ext"));
											// blob字段保存,数据库中差异
											switch (Sql_switcher.searchDbServer()) {
												case Constant.ORACEL:
													InputStream in = null;
													try {
														in = rwSet.getBinaryStream("affix");
														Blob blob = getOracleBlob(in, "per_article", article_id);
														pt.setBlob(2, blob);
														pt.setString(3, rwSet.getString("Article_name"));
														pt.setInt(4, article_id);
													} finally {
														PubFunc.closeResource(in);
													}
													break;
												default:
													byte[] data = rwSet.getBytes("affix");
													// a_vo.setObject("affix",data);
													pt.setBytes(2, data);
													pt.setString(3, rwSet.getString("Article_name"));
													pt.setInt(4, article_id);
													break;
											}
											// 打开Wallet
											dbS.open(this.frameconn, sqlStr);
											pt.execute();
										}
									}
					        	}
								
								
								
							}
						}						
					}
				}				
				this.getFormHM().put("flag", "1");
			}											
			if(rowSet!=null)
				rowSet.close();
			if(rs!=null)
				rs.close();
			if(rwSet!=null)
				rwSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.frameconn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**	
	 * 删除任务和任务附件
	 */
	public void delItemid(String plan_id,String object_type,Hashtable params,String[] object_array)
	{		
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < object_array.length; i++)
			buf.append(",'" + object_array[i].toUpperCase()+"'");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			String strSqls="";	
			String str="";	
			if("2".equals(object_type))
			{
				strSqls=("delete from p04 where plan_id="+plan_id+" and a0100 IN ("+ buf.substring(1) + ")");
				if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
					str=("delete from per_article where plan_id="+plan_id+" and a0100 IN ("+ buf.substring(1) + ") and article_type=3 ");
			}else
			{
				StringBuffer buffer = new StringBuffer();
				strSqls=("delete from p04 where plan_id="+plan_id+" and b0110 IN ("+ buf.substring(1) + ")");
				if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
				{
					for (int i = 1; i < object_array.length; i++)
					{
						// 获取团队负责人信息
						LazyDynaBean bean = getMainbodyBean(plan_id,object_array[i].toUpperCase());																			
						buffer.append(",'" + (String)bean.get("mainbody_id") +"'");
					}
					if(buffer!=null && buffer.toString().trim().length()>0)
						str=("delete from per_article where plan_id="+plan_id+" and a0100 IN ("+ buffer.substring(1) + ") and article_type=3 ");
				}
			}							
			dao.delete(strSqls.toString(),null);
			if(params.get("TaskSupportAttach")!=null && "True".equalsIgnoreCase((String)params.get("TaskSupportAttach")))
			{
				if(str!=null && str.trim().length()>0)
					dao.delete(str.toString(),null);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**	
	 * 获得被复制考核对象的目标卡信息
	 */
	public HashMap objectTargetMap(String plan_id,String object_type,StringBuffer buf)
	{	
		HashMap map=new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			
			ArrayList alist=new ArrayList();
			String sqls = "";
			if("2".equals(object_type))
			{
				sqls = ("select a0100,p0401 from p04 where plan_id="+plan_id+" and a0100 IN ("+ buf.substring(1) + ") and ((chg_type <> 3) or (chg_type is null)) order by A0100");		
			}else{
				sqls = ("select b0110,p0401 from p04 where plan_id="+plan_id+" and b0110 IN ("+ buf.substring(1) + ") and ((chg_type <> 3) or (chg_type is null)) order by B0110");
			}
			rowSet=dao.search(sqls);
			while(rowSet.next())
			{
				if("2".equals(object_type))
				{
					String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"";
					String p0401=rowSet.getString("p0401")!=null?rowSet.getString("p0401"):"";
					alist.add(p0401);
					map.put(a0100, alist);				
					if(rowSet.next()!=false)
					{
						if(!a0100.equalsIgnoreCase(rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"")){
							alist=new ArrayList();
						}
					}				
					rowSet.previous();	
				}else{
					String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
					String p0401=rowSet.getString("p0401")!=null?rowSet.getString("p0401"):"";
					alist.add(p0401);
					map.put(b0110, alist);				
					if(rowSet.next()!=false)
					{
						if(!b0110.equalsIgnoreCase(rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"")){
							alist=new ArrayList();
						}
					}				
					rowSet.previous();
				}
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**	
	 * 获得被复制考核对象的任务附件信息
	 */
	public HashMap objectRwFjMap(String plan_id,String object_type,String[] object_array)
	{	
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < object_array.length; i++)
			buf.append(",'" + object_array[i].toUpperCase()+"'");
		
		HashMap map=new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			
			ArrayList alist=new ArrayList();
			String sqls = "";
			if("2".equals(object_type))
			{
				sqls = ("select a0100,task_id from per_article where plan_id="+plan_id+" and a0100 IN ("+ buf.substring(1) + ") and article_type=3 ");		
			}else
			{
				StringBuffer buffer = new StringBuffer();								
				for (int i = 1; i < object_array.length; i++)
				{
					// 获取团队负责人信息
					LazyDynaBean bean = getMainbodyBean(plan_id,object_array[i].toUpperCase());																			
					buffer.append(",'" + (String)bean.get("mainbody_id") +"'");
				}
				if(buffer!=null && buffer.toString().trim().length()>0)
					sqls = ("select a0100,task_id from per_article where plan_id="+plan_id+" and a0100 IN ("+ buffer.substring(1) + ") and article_type=3 ");
			}
			if(sqls!=null && sqls.trim().length()>0)
			{
				rowSet=dao.search(sqls);
				while(rowSet.next())
				{					
					String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"";
					String task_id=rowSet.getString("task_id")!=null?rowSet.getString("task_id"):"";
					alist.add(task_id);
					map.put(a0100, alist);				
					if(rowSet.next()!=false)
					{
						if(!a0100.equalsIgnoreCase(rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"")){
							alist=new ArrayList();
						}
					}				
					rowSet.previous();						
				}
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
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
	
}	


