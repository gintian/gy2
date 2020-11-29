/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>Title:SubmitTempletTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 6, 20066:19:49 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SubmitTempletTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		try
		{
			String tabid=(String)hm.get("tabid");
			String selectAll = (String)hm.get("selectAll");//=1 全选
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			ArrayList fieldlist=tablebo.getAllFieldItem();
			String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
			String msgs="";
			/**员工自助申请标识
			 *＝1员工
			 *＝0业务员 
			 */
			String selfapply=(String)hm.get("selfapply");
			if(selfapply==null||selfapply.length()==0)
				selfapply="0";
			if("1".equals(selfapply))
			{	
				srcTab="g_templet_"+Integer.parseInt(tabid);
				tablebo.setBEmploy(true);
			}
			
			
			//自动计算
			if(tablebo.getAutoCaculate().length()==0)
			{
				if(SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))&&!"1".equalsIgnoreCase(selfapply))
				{
					tablebo.batchCompute("0");
				}
			}else if("1".equals(tablebo.getAutoCaculate()))
			{
				tablebo.batchCompute("0");
			}
			

			//编制控制
			ArrayList addlist = new ArrayList();
			ArrayList movelist = new ArrayList();
			if(tablebo.getInfor_type()==1&&tablebo.isHeadCountControl(0)&&false){//如果是人员
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
								}else{
									throw GeneralExceptionHandler.Handle(new Exception(mess));
								}
							}
						} //当前模板的指标能引起编制检查 结束
					} //allFieldsList.size()>0 结束
				} //需要编制控制 结束 
				
			}
			
			ArrayList personlist = new ArrayList();//用于删除个人附件 郭峰  personlist里面存储着许多小List。如果是人员模板，小List存放basepre,a0100.如果是单位模板，小list存放b0110
			WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
			/**必填项校验*/
			tablebo.checkMustFillItem(srcTab,fieldlist,/*ins_id*/0);
			tablebo.checkLogicExpress(srcTab, 0, fieldlist);
			tablebo.setValidateM_L(true);
            if(tablebo.getInfor_type()==1)
                ins.resetDbpre(srcTab,tablebo,"0");
            
            String _sql="select * from ";
			String tablename=this.userView.getUserName()+"templet_"+tabid; 
			if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
			{
				_sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
				tablename="g_templet_"+tabid;
			}
			else
			{
				_sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"; 
			}
			ins.insertKqApplyTable(_sql,tabid,selfapply,"03",tablename); //往考勤申请单中写入申请记录
			tablebo.expDataIntoArchive(0);//将数据插入到相应的库中
			
			
			personlist = tablebo.getPersonlist(tablebo.getInfor_type(),this.userView.getUserName()+"templet_"+tabid);
			
			
			
            /**不用审批的单据也创建一个实例号，把数据导入templet_xxx中去*/ 
			String ins_id=createInstance(this.getFrameconn(),tablebo,selfapply,srcTab); 
			tablebo.saveSubmitTemplateData(this.userView.getUserName(),Integer.parseInt(ins_id),"",1);
			
			//原始单据归档 && 走审批流程的单据
			if("1".equals(tablebo.getArchflag()))
				tablebo.subDataToArchive("select * from templet_"+tabid+" where ins_id="+ins_id,"templet_"+tabid,"2");
			
			this.getFormHM().put("ins_id",ins_id);//目标管理/面谈纪录中用到
			if(!"yes".equals(msgs)&&msgs.trim().length()>0)
			{ 
					Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
					String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
					if(mode!=null&& "warn".equalsIgnoreCase(mode)){
						this.getFormHM().put("msgs", msgs);
					}
			}
			this.getFormHM().put("msgs","");
			
			//更新联动兼职数据
			DbNameBo db=new DbNameBo(this.getFrameconn());
			for(int i=0;i<addlist.size();i++){
				String pos_value = (String)addlist.get(i);
				if(pos_value!=null&&pos_value.trim().length()>0){
				db.dateLinkage(pos_value, 1, "+");
				}
			}
			for(int i=0;i<movelist.size();i++){
				String pos_value = (String)movelist.get(i);
				if(pos_value!=null&&pos_value.trim().length()>0){
				db.dateLinkage(pos_value, 1, "-");
				}
			}
			
			//把附件增加到流程中
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
			if(!this.frowset.next())
			{
				StringBuffer insertSQL=new StringBuffer();
				insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
				ArrayList list=new ArrayList();
				dao.insert(insertSQL.toString(),list);				
			}
			
			this.frowset = dao.search(" select * from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"'");
			String sqlstrs = "";
			while(this.frowset.next()){
				String file_id = this.frowset.getString("file_id");
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
	    		String file_id2 = idg.getId("t_wf_file.file_id");
				sqlstrs=" insert into t_wf_file(file_id,content,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,create_user,create_time) select "+file_id2+",content,filetype,objectid,basepre,attachmenttype,"+ins_id+",tabid,ext,name,create_user,create_time from t_wf_file where file_id="+file_id+"  ";
				dao.update(sqlstrs);
			}
			
////////////////个人附件归档 开始//////////////////
			StringBuffer sb = new StringBuffer("");
			if(tablebo.hasFunction()){
				sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and attachmenttype=1");//个人附件
				if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !tablebo.getIsIgnorePriv()){
					sb.append(" and filetype in (select id from mediasort where flag in "+tablebo.getMediaPriv()+")");
				}
				this.frowset = dao.search(sb.toString());
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
			    HashMap map = tablebo.getDestination_a0100();
			    
				while(this.frowset.next()){
					sb.setLength(0);
					int flagid = this.frowset.getInt("filetype");//和mediasort表的id相关联
					int file_id = this.frowset.getInt("file_id");
					if(tablebo.getInfor_type()==1){
						String basepre = "";//要归档到的人员库
						String a0100 = "";//最终的人员编号
						if(tablebo.getOperationtype()==0||tablebo.getOperationtype()==1 || tablebo.getOperationtype()==2){
							basepre = tablebo.getDestBase();
							String sourcea0100 = a0100 = this.frowset.getString("objectid");
							a0100 = (String)tablebo.getDestination_a0100().get(sourcea0100);
						}else{
							basepre = this.frowset.getString("basepre");
							a0100 = this.frowset.getString("objectid");
						}
						sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						//state 状态设置为3，liuyz 个人附件归档 状态应为批准。
					}else if(tablebo.getInfor_type()==2){     //xyy20141203  给合并成新的代码的部门或单位的多媒体子集存入真实的b0110
						String b0110 = this.frowset.getString("objectid");
						
						if(map!=null){
						    String objectid = (String) map.get(b0110);
						    if(objectid!=null&&!objectid.equals(b0110)){
						        b0110 = objectid;
						    }
						}
						sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						//state 状态设置为3，liuyz 个人附件归档 状态应为批准。
					}else if(tablebo.getInfor_type()==3){  //xyy 20141203 应该是from K00.. 给合并成新的代码的岗位的多媒体子集存入真实的e01a1
						String e01a1 = this.frowset.getString("objectid");
						
                        if(map!=null){
                            String objectid = (String) map.get(e01a1);
                            if(objectid!=null&&!objectid.equals(e01a1)){
                                e01a1 = objectid;
                            }
                        }
						sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from K00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						//state 状态设置为3，liuyz 个人附件归档 状态应为批准。
					}
					dao.update(sb.toString());
				}
			}
			
////////////////个人附件归档结束//////////////////
			//把原公共附件清空
			if("1".equals(selectAll)){//如果全选
				dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
			}
			//再清空个人附件
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
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private String createInstance(Connection conn,TemplateTableBo tablebo,String selfapply,String srcTab)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(conn);
		RecordVo vo=new RecordVo("t_wf_instance");
		try
		{
	        //取得当前用户的人员范围 wangrd 2013-12-11
	        String operOrg = this.userView.getUnitIdByBusi("8");
            if(operOrg!=null&&operOrg.trim().length()>0){
                if(this.userView.getStatus()==4&& "UM`".equalsIgnoreCase(operOrg)){  //当自助用户没有部门时，将单位存储进去 T_WF_INSTANCE的B0110字段 20150730 liuzy。
                       operOrg=this.userView.getUserOrgId();
                }else {
                    String[] temps=operOrg.split("`");
                    for(int i=0;i<temps.length;i++)
                    {
                        if(temps[i].trim().length()>0)
                        {
                            if("UN".equalsIgnoreCase(temps[i].trim()))
                                operOrg="UN";
                            else
                                operOrg=temps[i].trim().substring(2);
                            break;
                        }
                    } 
                }
            }
            else {
                operOrg="UN";   
            }
            IDGenerator idg=new IDGenerator(2,conn);		
            vo.setInt("ins_id",Integer.parseInt(idg.getId("wf_instance.ins_id")));

            vo.setString("name",tablebo.getName()+tablebo.getRecordBusiTopic(""));
            vo.setInt("tabid",tablebo.getTable_vo().getInt("tabid"));
            vo.setDate("start_date",DateStyle.getSystemTime());
            /**过程状态
             * 1 初始化
             * 2 运行中
             * 3 暂停
             * 4 终止
             * 5 结束
             * */
            vo.setString("finished","5"); //结束
            /**模板类型
             * =1业务模板
             * =2固定网页
             * */
            vo.setInt("template_type",1);
            /**平台用户 =0
             * 还是自助用户=4
             * */
            vo.setInt("actor_type",tablebo.getUserview().getStatus());
            if(tablebo.getUserview().getStatus()==0)
            	vo.setString("actorid",tablebo.getUserview().getUserName());
            else
            	vo.setString("actorid",tablebo.getUserview().getDbname()+tablebo.getUserview().getUserId());            	
            vo.setString("actorname",tablebo.getUserview().getUserFullName());
            /**是否有附件
             * =0 无附件
             * =1 有附件
             * */
            vo.setInt("bfile",0);
            //new_ins_vo=vo;
            if (!"".equals(operOrg)){
                vo.setString("b0110", operOrg);                
            }            
            dao.addValueObject(vo);
            /**加上任务处理*/
            RecordVo taskvo=new RecordVo("t_wf_task");
	
            taskvo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
            taskvo.setString("task_topic",vo.getString("name"));
            taskvo.setInt("node_id",-1);
            taskvo.setInt("ins_id",vo.getInt("ins_id"));
            taskvo.setDate("start_date",DateStyle.getSystemTime());
            taskvo.setString("task_type","9");//结束   //""1");

            taskvo.setString("state","06"); //审批状态
            taskvo.setString("sp_yj","");//审批意见
            taskvo.setString("content","");//审批意见描述
            taskvo.setInt("bread",0);//是否已阅读
            taskvo.setDate("end_date",DateStyle.getSystemTime());	
            taskvo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
            /**根据任务分配算法，具体对应到准*/
			taskvo.setString("actorid",vo.getString("actorid"));//当前对象	流程定义的参与者	 
			taskvo.setString("actor_type",vo.getString("actor_type"));
            taskvo.setString("a0100",vo.getString("actorid"));//人员编号 实际处理人员编码
            taskvo.setString("a0101",vo.getString("actorname"));//人员姓名 实际处理人员姓名
        	taskvo.setString("a0100_1",vo.getString("actorid"));//发送人
        	taskvo.setString("a0101_1",vo.getString("actorname"));//发送人姓名	            
            taskvo.setString("url_addr","");//审批网址
            taskvo.setString("params","");//参数	
            taskvo.setInt("flag",1);
            dao.addValueObject(taskvo); 
            //t_wf_task_objlink里写记录
        	StringBuffer strsql = new StringBuffer();
        	strsql.append("select * from ");
			strsql.append(srcTab);
			if("1".equals(selfapply))//员工通过自助平台发动申请
				strsql.append(" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			else
				strsql.append(" where submitflag=1");//对选中的人提交审批
			this.frowset=dao.search(strsql.toString());
			ArrayList recordList = new ArrayList();
            while(this.frowset.next())
			{
				String seqnum=(String)this.frowset.getString("seqnum");
				
				RecordVo objlinkvo=new RecordVo("t_wf_task_objlink"); 
				objlinkvo.setString("seqnum",seqnum);
				objlinkvo.setInt("ins_id", Integer.parseInt(vo.getString("ins_id")));
				objlinkvo.setInt("task_id",taskvo.getInt("task_id"));
				objlinkvo.setInt("tab_id", vo.getInt("tabid"));
				objlinkvo.setInt("node_id",-1);
				objlinkvo.setInt("count",0);
				objlinkvo.setInt("submitflag", 1);
				objlinkvo.setInt("state",1);
				recordList.add(objlinkvo);
			} 
			dao.addValueObject(recordList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return vo.getString("ins_id"); 
	}
	
}
