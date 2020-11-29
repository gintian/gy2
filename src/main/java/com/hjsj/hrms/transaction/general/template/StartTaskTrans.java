/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.service.SynOaService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:StartTaskTrans</p>
 * <p>Description:启动任务</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 24, 20063:13:23 PM
 * @author chenmengqing
 * @version 4.0
 */
public class StartTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		WF_Actor wf_actor=null;
		String tabid=(String)hm.get("tabid");
		try
		{
			String selectAll = (String)hm.get("selectAll");//=1 全选
			String sp_mode=(String)hm.get("sp_mode");
			/**员工自助申请标识
			 *＝1员工
			 *＝0业务员 
			 */
			String selfapply=(String)hm.get("selfapply");
			if(selfapply==null||selfapply.length()==0)
				selfapply="0";
			if(sp_mode==null|| "".equals(sp_mode))
				sp_mode="0";
			
			
			//抄送邮件
			String isSendMessage=(String)hm.get("isSendMessage");//是否
			String user_h_s=(String)hm.get("user_h_s");//抄送的人  格式：   ,1:Usr00000049
			if(user_h_s==null)
				user_h_s="";
			String email_staff_value=(String)hm.get("email_staff_value");//是否通知本人
			String specialOperate=(String)hm.get("specialOperate"); //业务模板中人员需要报送给各自领导进行审批处理
			String specialRoleUserStr=(String)hm.get("specialRoleUserStr"); //特殊角色指定的用户   nodeid:xxxx,nodeid:yyyy
			if(specialRoleUserStr==null)
				specialRoleUserStr="";
			
			String msgs="";
			/**审批模式=0自动流转，=1手工指派*/
			if("1".equals(sp_mode))//手工流转
			{
				LazyDynaBean actor=(LazyDynaBean)hm.get("actor");
				if ("01".equals(((String)actor.get("sp_yj"))) && "".equals(((String)actor.get("content")))) {
                    actor.set("content", ResourceFactory.getProperty("label.agree"));//"默认同意" 
                }
				//String setname=(String)hm.get("setname");
				//String tabid=(String)actor.get("tabid");
				String actorid=(String)actor.get("name");
				String actorname=(String)actor.get("fullname");
				String actor_type=(String)actor.get("objecttype");
				/**审批对象*/
				wf_actor=new WF_Actor(actorid,actor_type);
				wf_actor.setContent(SafeCode.decode((String)actor.get("content")).replace("\r\n", "<p>").replace(" ", "&nbsp;"));//当前提交人的审批意见
				wf_actor.setEmergency((String)actor.get("pri"));
				wf_actor.setSp_yj((String)actor.get("sp_yj")); 
				wf_actor.setActorname(actorname);
			}
			else//自动流转
			{
				LazyDynaBean actor=(LazyDynaBean)hm.get("actor");//对话框中的内容。包括优先级、审批意见、内容这三项
				String actorid="";
				String actor_type="";
				if ("01".equals(((String)actor.get("sp_yj"))) && "".equals(((String)actor.get("content")))) {
                    actor.set("content", ResourceFactory.getProperty("label.agree"));//"默认同意" 
                }
			
			
				if(this.userView.getStatus()==0)
				{
					actorid=this.userView.getUserName();
					actor_type="4";
				}
				else
				{
					actorid=this.userView.getDbname()+this.userView.getA0100();  //this.userView.getUserName();
					actor_type="1";
				}	
			
							
				wf_actor=new WF_Actor(actorid,actor_type);
				wf_actor.setContent(SafeCode.decode((String)actor.get("content")).replace("\r\n", "<p>").replace(" ", "&nbsp;"));//当前提交人的审批意见
				wf_actor.setEmergency((String)actor.get("pri"));
				wf_actor.setSp_yj((String)actor.get("sp_yj")); 
				wf_actor.setActorname(this.userView.getUserFullName());	
				wf_actor.setBexchange(false);
			} 
			if(specialRoleUserStr.length()>0)//特殊角色
				wf_actor.setSpecialRoleUserList(specialRoleUserStr); 
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			//报批时判断调动人员库 是否设置了目标库
			if(tablebo.getOperationtype()==4&&(tablebo.getDestBase()==null|| "".equals(tablebo.getDestBase())))
	            throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desddbase"));
			
			/**员工自助申请*/
			if("1".equalsIgnoreCase(selfapply))
				tablebo.setBEmploy(true);
			
			//单据正在处理，不允许重复申请
			String info=tablebo.validateExistData();
			if(info.length()>0)
				throw new GeneralException(info);
			 
			ArrayList fieldlist=tablebo.getAllFieldItem();
			/**必填项校验*/
			String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
			if("1".equalsIgnoreCase(selfapply))
				srcTab="g_templet_"+tabid;
			ArrayList personlist = new ArrayList();
			personlist = tablebo.getPersonlist(tablebo.getInfor_type(),this.userView.getUserName()+"templet_"+tabid);
		
			
			//自动流程在判断变迁条件时，已在GetNextNodeStrTrans.java中判断 
			boolean isCal=false;
			if(!(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0))
				isCal=true;
			if(tablebo.getSplit_data_model()!=null&&tablebo.getSplit_data_model().trim().length()>0&&("superior".equalsIgnoreCase(tablebo.getSplit_data_model())|| "groupfield".equalsIgnoreCase(tablebo.getSplit_data_model())))
				isCal=true;
			if(isCal)
			{ 
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
					
					tablebo.checkMustFillItem(srcTab,fieldlist,/*ins_id*/0);
					tablebo.checkLogicExpress(srcTab, 0, fieldlist); 
			}
		
			if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)//如果是单位部门或岗位
			{
				StringBuffer strsql=new StringBuffer(""); 
				strsql.append("select * from ");
				strsql.append(srcTab);
				strsql.append(" where submitflag=1");
				this.frowset=dao.search(strsql.toString());
				HashMap tableColumnMap=new HashMap();
				ResultSetMetaData mt=this.frowset.getMetaData();
				for(int i=1;i<=mt.getColumnCount();i++)
				{
					String columnName=mt.getColumnName(i);
					tableColumnMap.put(columnName.toLowerCase(),"1");
				}
				tablebo.validateSysItem(tableColumnMap);
				/**如果为新建组织单元业务  */
				if(tablebo.getOperationtype()==5) 
				{
					tablebo.checkNewOrgFillItem(strsql.toString(),tablebo.getOperationtype());
				}
				/**  如果为合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记 */
				if(tablebo.getOperationtype()==8||tablebo.getOperationtype()==9)
				{
					tablebo.checkSelectedRule(strsql.toString(),srcTab,"");
				}
			}
			else//如果是人员
			{
				
				if(tablebo.isHeadCountControl(0)&&false)
				{
				
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
									}else{
										throw GeneralExceptionHandler.Handle(new Exception(mess));
									}
								}
							} //当前模板的指标能引起编制检查 结束
						} //allFieldsList.size()>0 结束
					} //需要编制控制 结束 
				}
				
			} //如果是人员 结束
          
			String url_s=(String)this.getFormHM().get("url_s");
			 
			 ArrayList whlList=new ArrayList(); 
			 whlList.add("");
			 //拆单
			 if(!"1".equalsIgnoreCase(selfapply))
			 {
				 if(SystemConfig.getPropertyValue("clientName")!=null&& "gdzy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
				 {
					 SynOaService sos=new SynOaService();
					 String tab_ids=sos.getTabids();
					 if(tab_ids.indexOf(","+tabid+",")!=-1)
					 {
						 whlList=sos.getSplitInstanceWhl(tabid,this.userView,this.getFrameconn());
					 }
				 }
				 else
				 {
					 whlList=tablebo.getSplitInstanceWhl();
					 if(whlList.size()==1&&((String)whlList.get(0)).trim().length()==0)//不拆单
					 {
						 
					 }
					 else
						 wf_actor.setSpecialRoleUserList(new ArrayList());
				 }
			 }
			 
			 
			 
			 for(int i=0;i<whlList.size();i++)
			 {
				
				RecordVo ins_vo=new RecordVo("t_wf_instance");	
				WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
				ins.setUrl_s(url_s);
				ins.setSpecialOperate(specialOperate);
				
				String whl=(String)whlList.get(i);
				
				if("1".equalsIgnoreCase(selfapply))
					ins.setObjs_sql(ins.getObjsSql(0,0,1,tabid,this.userView,""));
				else
					ins.setObjs_sql(ins.getObjsSql(0,0,2,tabid,this.userView,whl));
				//邮件抄送
				if(isSendMessage!=null&&!"0".equals(isSendMessage))  // 3:邮件、消息  2：消息  1：邮件
				{
					ins.setEmail_staff_value(email_staff_value); //通知本人 1:通知
					ins.setUser_h_s(user_h_s);                   //抄送人员
					ins.setIsSendMessage(isSendMessage);
				}
				else
					ins.setUser_h_s(user_h_s); 
				
				 
				if(ins.createInstance(ins_vo,wf_actor,whl))//将数据插入到t_wf_instance、t_wf_task_objlink和t_wf_task中
				{
				     
				    
				    boolean isOriData=false; // 表单数据没到临时表
					String sql="select count(*) as nrec from ";
					if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
						sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
					else
					{
						sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
					
					}
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{
						if(this.frowset.getInt(1)>0)
						{
						    isOriData=true;
							
						}
					} 
					if(isOriData)
					{
					   
					    String approve_opinion = tablebo.getApproveOpinion(ins_vo,"0",wf_actor,"");
                        tablebo.setApprove_opinion(approve_opinion);
                        if("1".equalsIgnoreCase(selfapply))
                            tablebo.saveSubmitTemplateData(ins_vo.getInt("ins_id"));
                        else//将数据插入到template_tabid中
                            tablebo.saveSubmitTemplateData(this.userView.getUserName(),ins_vo.getInt("ins_id"),whl);
					}
					else
					{
					   
					}
					//调用sap接口
					if (tablebo.getInfor_type()==1){
					    tablebo.sendDataToSAP(tabid,ins_vo.getString("ins_id"));
					}
					
					
					this.getFormHM().put("ins_id", String.valueOf(ins_vo.getInt("ins_id")));  //目标管理/面谈纪录中用到
					
				}
				this.getFormHM().clear();
				this.getFormHM().put("ins_id", String.valueOf(ins_vo.getInt("ins_id")));  //目标管理/面谈纪录中用到
				if(!"yes".equals(msgs)&&msgs.trim().length()>0)
				{
						Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
						String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
						if(mode!=null&& "warn".equalsIgnoreCase(mode)){
							this.getFormHM().put("msgs", msgs);
						}
				}
				this.getFormHM().put("msgs","");
				//把附件增加到流程中
				int ins_id = ins_vo.getInt("ins_id");
				ArrayList personlist_ = this.getPersonlist(tablebo.getInfor_type(),ins_id,tabid,whl);
				this.frowset=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
				if(!this.frowset.next())
				{//这个语句是向id_factory添加主键自动生成的功能
					StringBuffer insertSQL=new StringBuffer();
					insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue,auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
					insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
					ArrayList list=new ArrayList();
					dao.insert(insertSQL.toString(),list);
				}
				String sql = " select * from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' ";
				if (tablebo.getInfor_type() == 1) {// 人员
					String personarr = "";
					String sqlStr = "";
					switch (Sql_switcher.searchDbServer()) {
					case Constant.MSSQL:
						sqlStr += " (basepre+objectid) ";
						break;
					default:
						sqlStr += " concat(basepre,objectid) ";
						break;
					}
					for (int k = 0; k < personlist_.size(); k++) {
						ArrayList list = (ArrayList) personlist_.get(k);
						String basepre = (String) list.get(0);
						String a0100 = (String) list.get(1);
						if (k == 0)
							personarr += " in('" + basepre + a0100 + "'";
						else if (k % 990 == 0 && personlist_.size() % 990 > 0) {
							personarr += ") or " + sqlStr + " in('" + basepre + a0100 + "'";
						} else {
							personarr += ",'" + basepre + a0100 + "'";
						}
						if (k == personlist_.size() - 1) {
							personarr += ")";
						}
					}
					if ("".equals(personarr))
						personarr = "in ('')";
					sql += "and (" + sqlStr + personarr + ")";
				} else {// b0110,e01a1
					String personarr = "";
					for (int k = 0; k < personlist_.size(); k++) {
						ArrayList list = (ArrayList) personlist_.get(k);
						String a0100 = (String) list.get(0);
						if (k == 0)
							personarr += "'" + a0100 + "'";
						else
							personarr += ",'" + a0100 + "'";
					}
					if ("".equals(personarr))
						personarr = "''";
					sql += " and objectid in (" + personarr + ")";
				}
                sql+= " order by file_id";
                this.frowset = dao.search(sql);
				String sqlstrs = "";
				while(this.frowset.next()){
					String file_id = this.frowset.getString("file_id");
					 IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		    			String file_id2 = idg.getId("t_wf_file.file_id");
					sqlstrs=" insert into t_wf_file(file_id,content,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,create_user,create_time) select "+file_id2+",content,filetype,objectid,basepre,attachmenttype,"+ins_id+",tabid,ext,name,create_user,create_time from t_wf_file where file_id="+file_id+"  ";
					dao.update(sqlstrs);
				} 
				
				//个人附件归档liuzy 20150821
				submitAttachmentFile(ins_vo, tablebo, "1", tabid);
				
				boolean isSend=true;
				if(SystemConfig.getPropertyValue("clientName")!=null&& "gdzy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())&& "1".equalsIgnoreCase(selfapply))
					isSend=false;
				 //将单据信息发送至外部系统
		//		 if(!selfapply.equalsIgnoreCase("1"))
				 if(isSend)
				 {
					 SynOaService sos=new SynOaService();
					 String tab_ids=sos.getTabids();
					 if(tab_ids.indexOf(","+tabid+",")!=-1)
					 {
						if("1".equals((String)sos.getTabOptMap().get(tabid.trim())))
						{
							String _info=sos.synOaService(String.valueOf(ins.getTask_vo().getInt("task_id")),tabid,this.userView);  //创建成功返回1，否则返回详细错误信息
							if(!"1".equals(_info))
								throw GeneralExceptionHandler.Handle(new Exception(_info));	
						}
					 }
				 }
				
			 } //循环whlList 结束
			this.frowset=dao.search("select count(*) from "+srcTab+" where submitflag=0");
			boolean deleteFlag=true;
			if(this.frowset.next()){
			    if(this.frowset.getInt(1)!=0){
			        deleteFlag=false;
			    }
			}
			//把原公共附件清空（不能直接清空。当发起人选了4个人，只报批了三个的时候，就不能清空）
			 //if("1".equals(selectAll)){//如果全选,xcs 2014-05-20 这种做法是不对的 只有全选checkbox打勾时，这个selectAll才为1
			 if(deleteFlag){
				 dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
			 }
			//再清空个人附件
			 StringBuffer sb = new StringBuffer("");
			 if("1".equals(selfapply)){//如果是员工自助申请，那么直接删除
					dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			 }else{
				sb.setLength(0);
				if(tablebo.getInfor_type()==1){
					sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
				}else{
					sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid=?");
				}
				dao.batchUpdate(sb.toString(),personlist);
			}
			 
			//判断临时表里是否有记录，没有记录更新临时表的结构（解决模板变动时保留的多余字段sqlserver可能造成8060的问题）
			 if(Sql_switcher.searchDbServer()==1){//oracle 不判断8086问题 2015-07-25
				 this.frowset = dao.search(" select * from "+srcTab+""); 
				 if(!this.frowset.next()){
					 dao.update(" drop table "+srcTab+"");
					 //创建表结构
					 if("1".equalsIgnoreCase(selfapply)){
						 tablebo.createTempTemplateTable();
					 }else{
						 tablebo.createTempTemplateTable(this.userView.getUserName());
					 }
				 }
			 }
			
		}
		catch(Exception ex)
		{ 
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.getFrameconn(),"templet_"+tabid);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex); 
		}
	}
	 
	private ArrayList getPersonlist(int infor_type, int ins_id, String tabid, String whl) {
		ArrayList list = new ArrayList();
        RowSet rs = null;
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer buf = new StringBuffer("");
            String tableName = "templet_"+tabid;
            ArrayList paramList = new ArrayList();
            String searchcode = "basepre,a0100";
            if(infor_type==2)
            	searchcode = "b0110";
            else if(infor_type==3) 
            	searchcode = "e01a1";
            buf.append("select ");
            buf.append(searchcode);
            buf.append(" from ");
            
            buf.append(tableName);
            buf.append(" where 1=1 ");
            buf.append(" and exists (select null from t_wf_task_objlink where " + tableName + ".seqnum=t_wf_task_objlink.seqnum and " + tableName + ".ins_id=t_wf_task_objlink.ins_id  ");
            buf.append(" and  submitflag=1 and  ins_id = ?");
            buf.append(" and (state is null or state<>3) ) ");
            buf.append(whl);
            paramList.add(ins_id);
            rs = dao.search(buf.toString(),paramList);
            while(rs.next()){
                ArrayList templist = new ArrayList();
                if(infor_type==1){
                    String basepre = rs.getString("basepre");
                    String a0100 = rs.getString("a0100");
                    templist.add(basepre);
                    templist.add(a0100);
                }
                else if(infor_type==2){
                	String b0110 = rs.getString("b0110");
                    templist.add(b0110);
                }
                else if(infor_type==3){
                	String e01a1 = rs.getString("e01a1");
                    templist.add(e01a1);
                }
                list.add(templist);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rs);
        }
        return list;
	}

	/**
	 * 个人附件归档 liuzy 20150821
	 * flag =1报批，=2驳回,=3确认
	 * @throws GeneralException 
	 * 
	 */
	public void submitAttachmentFile(RecordVo ins_vo,TemplateTableBo tablebo,String flag,String tabid) throws GeneralException{

		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
				if("1".equals(flag)&& tablebo.hasFunction()){
					StringBuffer sb = new StringBuffer("");
					int ins_id = ins_vo.getInt("ins_id");
					sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and attachmenttype=1");//个人附件
					if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !tablebo.getIsIgnorePriv()){
						sb.append(" and filetype in (select id from mediasort where flag in "+tablebo.getMediaPriv()+")");
					}
					
					RowSet rowSet = dao.search(sb.toString());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					String time = sdf.format(new Date());//得到当前日期
				    switch(Sql_switcher.searchDbServer())
				    {
						case Constant.ORACEL:
					    {
					    	time="to_date('"+time+"','yyyy-mm-dd')";
					    	break;
					    }
						case Constant.MSSQL:
					    {
					    	time="'"+time+"'";
					    	break;
					    }
					}
					while(rowSet.next()){
						sb.setLength(0);
						int flagid = rowSet.getInt("filetype");//和mediasort表的id相关联
						int file_id = rowSet.getInt("file_id");
						if(tablebo.getInfor_type()==1){
							String basepre = "";//要归档到的人员库
							String a0100 = "";//最终的人员编号
							if(tablebo.getOperationtype()==0 || tablebo.getOperationtype()==1 || tablebo.getOperationtype()==2){
								basepre = tablebo.getDestBase();
								String sourcea0100 = a0100 = rowSet.getString("objectid");
								a0100 = (String)tablebo.getDestination_a0100().get(sourcea0100);
							}else{
								basepre = rowSet.getString("basepre");
								a0100 = rowSet.getString("objectid");
							}
							sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);//liuyz 附件通过表单归档状态应是批准
						}else if(tablebo.getInfor_type()==2){
							String b0110 = rowSet.getString("objectid");
							sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);//liuyz 附件通过表单归档状态应是批准
						}else if(tablebo.getInfor_type()==3){
							String e01a1 = rowSet.getString("objectid");
							sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);//liuyz 附件通过表单归档状态应是批准
						}
						dao.update(sb.toString());
					} //while 遍历结束	
				} //flag=1  结束
			}
		} //try
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	

}
