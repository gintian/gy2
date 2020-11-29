package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HeadCountControlTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			 
			
			String msgs="";
			String flag="ok";
			/**员工自助申请标识
			 *＝1员工
			 *＝0业务员 
			 */
			String selfapply=(String)this.getFormHM().get("selfapply");
			if(selfapply==null||selfapply.length()==0)
				selfapply="0";
			else
				this.getFormHM().remove("selfapply");
			String tabid=(String)this.getFormHM().get("tabid");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if(tablebo.getInfor_type()==1)
			{
				String task_id=(String)this.getFormHM().get("task_id");
				if("0".equals(task_id)&&tablebo.isHeadCountControl(0))
				{ 
						String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
						if("1".equalsIgnoreCase(selfapply))
						{
							srcTab="g_templet_"+tabid;
							tablebo.setBEmploy(true);
						}
						
						//自动流程在判断变迁条件时，已在GetNextNodeStrTrans.java中判断 
				 		if(!(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0))
						{
							//自动计算
							if(tablebo.getAutoCaculate().length()==0)
							{
								if(SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute")))//&&!selfapply.equalsIgnoreCase("1"))
								{
									tablebo.batchCompute("0");
								}
							}else if("1".equals(tablebo.getAutoCaculate()))
							{
								tablebo.batchCompute("0");
							}
						}
					
						/**编制控制*/
						StringBuffer sbsql = new StringBuffer("");
						sbsql.append("select * from "+srcTab+" where");
					    if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
					    	sbsql.append(" a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
						else
							sbsql.append(" submitflag=1");
					    
						///源库与目标库
						String srcDb = "";//源库
						String destinationDb = "";//目标库
						srcDb = tablebo.getDestinationDb(sbsql.toString());
						destinationDb = tablebo.getDestBase(); //移库模板的目标库
						if("".equals(destinationDb)){//说明不是移库模板
							destinationDb = srcDb;
						}
						///对那些库进行编制控制
						PosparameXML pos = new PosparameXML(this.getFrameconn());//得到constant表中UNIT_WORKOUT对应的参数
						String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
						dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
						if(dbs.length()>0){
							if(dbs.charAt(0)!=','){
								dbs=","+dbs;
							}
							if(dbs.charAt(dbs.length()-1)!=','){
								dbs=dbs+",";
							}
						}
						int currentOperation = tablebo.getOperationtype();//模板类型
						String addFlag = tablebo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
						
						ScanFormationBo scanFormationBo=new ScanFormationBo(this.getFrameconn(),this.userView);
							
						if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
							
							///得到所有变化后的指标（包含子集中的指标）
							StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
							ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
							allFieldsList = tablebo.getAllFields(tabid);
							if(allFieldsList.size()>0){
								ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
								ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
								temp_list.addAll(afterFields);
								HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
								
								Set key = temp_map.keySet();
								for (Iterator it = key.iterator(); it.hasNext();) {
									String s = (String) it.next();
									ArrayList innerlist = (ArrayList)temp_map.get(s);
									temp_list.addAll(innerlist);
								}
								
								//将所有变化后的指标（包括子集中的指标）变为字符串的形式
								for(int i=0;i<temp_list.size();i++){
									String str = (String)temp_list.get(i);
									allFields.append(str+",");
								}
								if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
									allFields.setLength(allFields.length()-1);
								}
								String itemids = "all";
								if(currentOperation!=1 && currentOperation!=2){//不是移库型
									itemids = allFields.toString();
								}
								if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
									ArrayList beanlist = new ArrayList();
									//开始传list。list中存放着bean.
									String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
									partType = tablebo.getPartType(temp_map);
									if("0".equals(partType)){
										beanlist = tablebo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,tabid);
									}else{
										beanlist = tablebo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
									}
									scanFormationBo.execDate2TmpTable(beanlist);
									String mess=scanFormationBo.isOverstaffs();
									if(!"ok".equals(mess)){
										if("warn".equals(scanFormationBo.getMode())){//提示，继续。
											msgs += mess;
											flag="warn";
										}else{
											msgs += mess;
											flag="error";
										//	throw GeneralExceptionHandler.Handle(new Exception(mess));
										}
									}
								} //当前模板的指标能引起编制检查 结束
							} //allFieldsList.size()>0 结束
						} //需要编制控制 结束 
					 
				}
				else if(!"0".equals(task_id))
				{
					/**批量审批*/
					String sp_batch=(String)this.getFormHM().get("sp_batch");
					String ins_id=(String)this.getFormHM().get("ins_id");
					ArrayList tasklist=null;//所有要处理的任务
					String srcTab="templet_"+Integer.parseInt(tabid);
					if(sp_batch==null|| "".equals(sp_batch))
						sp_batch="0";//单个任务审批
					if("1".equals(sp_batch))
					{
						String batch_task=(String)this.getFormHM().get("batch_task");
						tasklist=getTaskList(batch_task);
					}
					else//单任务审批
					{
						tasklist=new ArrayList();
						RecordVo taskvo=new RecordVo("t_wf_task");
						taskvo.setInt("task_id",Integer.parseInt(task_id));
						taskvo.setInt("ins_id",Integer.parseInt(ins_id));	
						tasklist.add(taskvo);
					}
					
					
					for(int i=0;i<tasklist.size();i++)
					{
						ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
						task_id=((RecordVo)tasklist.get(i)).getString("task_id");
						//自动计算
						
						//自动流程在判断变迁条件时，已在GetNextNodeStrTrans.java中判断 
						if(!(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0))
						{
				 			Boolean bCalc=false;	
				 			if("0".equals(task_id)||"".equals(task_id) || "1".equals(tablebo.isStartNode(task_id))){
				 				if(tablebo.getAutoCaculate().length()==0){
				 					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
				 						bCalc=true;
				 					}
				 				}
				 				else if("1".equals(tablebo.getAutoCaculate())){
				 					bCalc=true;
				 				}	
				 			}else {
				 				if(tablebo.getSpAutoCaculate().length()==0){
				 					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
				 						bCalc=true;
				 					}
				 				}
				 				else if("1".equals(tablebo.getSpAutoCaculate())){
				 					bCalc=true;
				 				}
				 			}
							if(bCalc)
							{
								tablebo.batchCompute(ins_id);
							}
						}
						  
							/**编制控制*/
							if(tablebo.isHeadCountControl(Integer.parseInt(task_id))){//报批和提交时验证超编
								StringBuffer sbsql = new StringBuffer("");//查询的sql语句
								sbsql.append("select * from ");
								sbsql.append(srcTab);
								sbsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
								sbsql.append("  and task_id="+task_id+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
								///源库与目标库
								String srcDb = "";//源库
								String destinationDb = "";//目标库
								srcDb = tablebo.getDestinationDb(sbsql.toString());
								destinationDb = tablebo.getDestBase(); //移库模板的目标库
								if("".equals(destinationDb)){//说明不是移库模板
									destinationDb = srcDb;
								}
								///对那些库进行编制控制
								PosparameXML pos = new PosparameXML(this.getFrameconn());//得到constant表中UNIT_WORKOUT对应的参数
								String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
								dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
								if(dbs.length()>0){
									if(dbs.charAt(0)!=','){
										dbs=","+dbs;
									}
									if(dbs.charAt(dbs.length()-1)!=','){
										dbs=dbs+",";
									}
								}
								int currentOperation = tablebo.getOperationtype();//模板类型
								String addFlag = tablebo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
								
								ScanFormationBo scanFormationBo=new ScanFormationBo(this.getFrameconn(),this.userView);
									
								if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
									
									///得到所有变化后的指标（包含子集中的指标）
									StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
									ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
									allFieldsList = tablebo.getAllFields(tabid);
									if(allFieldsList.size()>0){
										ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
										ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
										temp_list.addAll(afterFields);
										HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
										
										Set key = temp_map.keySet();
										for (Iterator it = key.iterator(); it.hasNext();) {
											String s = (String) it.next();
											ArrayList innerlist = (ArrayList)temp_map.get(s);
											temp_list.addAll(innerlist);
										}
										
										//将所有变化后的指标（包括子集中的指标）变为字符串的形式
										for(int m=0;m<temp_list.size();m++){
											String str = (String)temp_list.get(m);
											allFields.append(str+",");
										}
										if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
											allFields.setLength(allFields.length()-1);
										}
										String itemids = "all";
										if(currentOperation!=1 && currentOperation!=2){//不是移库型
											itemids = allFields.toString();
										}
										if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
											ArrayList beanlist = new ArrayList();
											//开始传list。list中存放着bean.
											String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
											partType = tablebo.getPartType(temp_map);
											if("0".equals(partType)){
												beanlist = tablebo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,tabid);
											}else{
												beanlist = tablebo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
											}
											scanFormationBo.execDate2TmpTable(beanlist);
											String mess=scanFormationBo.isOverstaffs();
											if(!"ok".equals(mess)){
												if("warn".equals(scanFormationBo.getMode())){//提示，继续。
													msgs += mess;
													flag="warn";
												}else{
													msgs += mess;
													flag="error";
													//throw GeneralExceptionHandler.Handle(new Exception(mess));
												}
											}
										} //当前模板的指标能引起编制检查 结束
									} //allFieldsList.size()>0 结束
								} //需要编制控制 结束 
							} 
						
					} //tasklit end loop 
					
				}
					
			}
			if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
			{
				String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
				String task_id=(String)this.getFormHM().get("task_id");
				if("0".equals(task_id)){
					StringBuffer strsql=new StringBuffer(""); 
					strsql.append("select * from "+srcTab+" where");
					strsql.append(" submitflag=1");
					if(tablebo.getOperationtype()==7) {//撤销机构验证机构下是否还有人员
	                	String havePerson = tablebo.checkIsHavePerson(strsql.toString(),"");
	                	if(havePerson.length()>1) {
							msgs += havePerson+"组织下还有人员";
							flag="warn";
	                	}
	                }
				}
			}
			if("warn".equals(flag))
				msgs+=" 是否继续?";
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("msgs",msgs);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

	
	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		ArrayList tasklist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from t_wf_task where task_id in (");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				RecordVo taskvo=new RecordVo("t_wf_task");
				taskvo.setInt("task_id",rset.getInt("task_id"));
				taskvo.setInt("ins_id",rset.getInt("ins_id"));
				tasklist.add(taskvo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return tasklist;
	}
	
	
	
}
