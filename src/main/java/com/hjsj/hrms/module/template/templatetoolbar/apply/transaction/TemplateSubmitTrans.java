/**
 * 
 */
package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.MessageToOtherSys;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * <p>Title:TemplateSubmitTrans.java</p>
 * <p>Description>:不走审批的提交数据</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-14 下午02:28:45</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class TemplateSubmitTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        //区分结束单据重复提交
        String flag=(String)this.getFormHM().get("flag");
        if("repreate".equals(flag)) {
        	try {
        		ArrayList data_list=(ArrayList)this.getFormHM().get("dataList");
				this.repreateSubmit(data_list);
			} catch (Exception e) {
				this.getFormHM().put("msg", e.getMessage());
				e.printStackTrace();
			}
        	return;
        }
        TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
        String moduleId = frontProperty.getModuleId();
        String tabId = frontProperty.getTabId();
        String taskId = frontProperty.getTaskId();
        String infor_type = frontProperty.getInforType();
        RowSet rowSet=null;
        //发送邮件短信 参数start
        String isSendMessage=this.getFormHM().get("isSendMessage")!=null?(String)this.getFormHM().get("isSendMessage"):"0";   //1:email  2:sms  3:email and sms
		String context=this.getFormHM().get("context")!=null?SafeCode.decode((String)this.getFormHM().get("context")):"";
		String user_h=this.getFormHM().get("sendid")!=null?SafeCode.decode((String)this.getFormHM().get("sendid")):"";
		String title=this.getFormHM().get("title")!=null?SafeCode.decode((String)this.getFormHM().get("title")):"0";
		String pt_type="9".equals(frontProperty.getModuleId())?"0":"1";   //0:自助  1：业务
		String email_staff_value=this.getFormHM().get("email_staff_value")!=null?(String)this.getFormHM().get("email_staff_value"):"0";
		//发送邮件短信 参数end
        String selfapply = "0";
        if (frontProperty.isSelfApply()){
            selfapply="1";
        }
        boolean bSelfApply = frontProperty.isSelfApply();        
        TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
        TemplateParam paramBo = templateBo.getParamBo();
        TemplateApplyBo  applyBo = new TemplateApplyBo(this.frameconn,this.userView,paramBo,frontProperty);
        
        TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabId), this.userView);
        tablebo.setValidateM_L(true);
        tablebo.setBEmploy(bSelfApply);    
        tablebo.setPcOrMobile("0");
        WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
        try {
            String srcTab =templateBo.getTableName(moduleId,Integer.parseInt(tabId), taskId); 
            if("1".equals(infor_type))
                ins.resetDbpre(srcTab,tablebo,"0");
            
            //如果是考勤模板，将数据同步考勤
            String _sql="select * from ";
            String tablename=this.userView.getUserName()+"templet_"+tabId; 
            if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
            {
                _sql+=srcTab+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
                tablename=srcTab;
            }
            else
            {
                _sql+=srcTab+" where submitflag=1"; 
            }
            ArrayList personlist =applyBo.getPersonlist(paramBo.getInfor_type(),srcTab);
             
            ins.insertKqApplyTable(_sql,tabId,selfapply,"03",tablename); //往考勤申请单中写入申请记录
            TemplateInterceptorAdapter.preHandle(tablename,Integer.parseInt(tabId), 0, paramBo, "submit", this.userView,"");
             
            
            //提交到人员库
            tablebo.expDataIntoArchive(0);//将数据插入到相应的库中
            
            //不用审批的单据也创建一个实例号，并将数据导入templet_xxx中去*/
            String ins_id=createInstance(this.getFrameconn(),tablebo,selfapply,srcTab);
            //自助申请-调出 离退 要更新流程的actorid
            if(paramBo.getInfor_type()==1&&(paramBo.getOperationType()==1||paramBo.getOperationType()==2)) { 
            	HashMap destination_a0100 = tablebo.getDestination_a0100();
            	String desta0100 = (String) destination_a0100.get(this.userView.getA0100());
            	if("1".equalsIgnoreCase(selfapply))
            		tablebo.updateTempletActorid(desta0100,ins_id);
			}
            tablebo.saveSubmitTemplateData(this.userView.getUserName(),Integer.parseInt(ins_id),"",1);
            
            
            // 把附件增加到流程中 应该放在saveSubmitTemplateData里面放在一起 todo
            applyBo.transAttachmentFile(srcTab,String.valueOf(ins_id), personlist);
            if(paramBo.getIsAotuLog()||paramBo.getIsRejectAotuLog()){
	            TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
	            chgLogBo.updateChangeInfoAddIns_id(personlist, "0", tabId, ins_id,paramBo.getInfor_type());//更新变动日志的ins_id
	            chgLogBo.insertChangeInfoToYearTable(ins_id);//提交时把变动日志更新到年度表中
            }
            //原始单据归档 && 走审批流程的单据
            if("1".equals(paramBo.getArchflag()))
                tablebo.subDataToArchive("select * from templet_"+tabId+" where ins_id="+ins_id,"templet_"+tabId,"2");
            TemplateInterceptorAdapter.afterHandle(0,Integer.parseInt(ins_id),tablebo.getTabid(),paramBo,"submit",this.userView);
            //删除附件
            applyBo.deleteAttachmentFile(srcTab,personlist);
            // 个人附件归档 应该放在归档数据库里面。
            applyBo.submitAttachmentFile(ins_id, tablebo, "1", tabId);
            
            //获得当前ins_id对应的ins_vo
            RecordVo ins_vo = new RecordVo("t_wf_instance");
            ins_vo.setInt("ins_id", Integer.parseInt(ins_id));
            ins_vo=dao.findByPrimaryKey(ins_vo);
            //查出task_id放入ins中
            this.setTaskVoForIns(ins,ins_id,tabId);
            //提交入库时通知发起人
            applyBo.SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabId);
            
            // 判断临时表里是否有记录，没有记录更新临时表的结构（解决模板变动时保留的多余字段sqlserver可能造成8060的问题）
            if (Sql_switcher.searchDbServer() == 1) {// oracle 不判断8086问题 2015-07-25
                this.frowset = dao.search(" select * from " + srcTab + "");
                if (!this.frowset.next()) {
                    dao.update(" drop table " + srcTab + "");
                    // 创建表结构
                    if ("1".equalsIgnoreCase(selfapply)) {
                        templateBo.createTempTemplateTable("");
                    } else {
                        templateBo.createTempTemplateTable(this.userView.getUserName());
                    }
                }
            }
            
            this.getFormHM().put("info", "");
            
            //发送短信邮件
            if(!"0".equals(isSendMessage)){
                IDGenerator idg=new IDGenerator(2,this.frameconn);       
                MessageToOtherSys sysBo=new MessageToOtherSys(this.frameconn,tablebo.getUserview());
                AsyncEmailBo newEmailBo=null;
                LazyDynaBean emialInfo_bos=null;
                LazyDynaBean emailInfo_staff=null;
                String template_bos=tablebo.getTemplate_bos();    ////业务办理人员的邮件模板
                HashMap columnMap=new HashMap();
                SendMessageBo sendMessageBo=new SendMessageBo(this.frameconn,tablebo.getUserview());
                sendMessageBo.setBusinessModel("61");
                sendMessageBo.setSendresource("");
                sendMessageBo.setIns_id(String.valueOf(ins_vo.getInt("ins_id")));
                sendMessageBo.setSp_flag("3");
                
                if(pt_type==null)
    				pt_type="0";
    			String[] users=user_h.split(",");
    			HashSet set=new HashSet();
    			for(int i=0;i<users.length;i++)
    			{
    				if(users[i]!=null&&users[i].trim().length()>0){
	    				String num = users[i].split(":")[0];
	    				String id = users[i].split(":")[1];
	    				set.add(num+":"+PubFunc.decrypt(id));
    				}
    			}
    			String a0101="";
                if(tablebo.getInfor_type()==1) {
                    a0101="a0101_1";
                }else
                    a0101="codeitemdesc_1";
                
                if(tablebo!=null&&tablebo.getOperationtype()==0)//调入
                {
                    a0101="a0101_2";
                }

                String sql="select * from templet_"+tabId+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabId+".seqnum=td.seqnum ";
                sql+=" and td.ins_id=templet_"+tabId+".ins_id   and submitflag=1 and td.tab_id="+tabId+" and td.task_id="+ins.getTask_vo().getString("task_id")+" and td.state<>3 )";
    			ArrayList seqnumList=new ArrayList();
	            ArrayList realSeqnumList = new ArrayList();
	            try
                {
                    newEmailBo=new AsyncEmailBo(this.frameconn, tablebo.getUserview());
                }
                catch(Exception e)
                {
                     System.out.println(e);
                }
                if(template_bos!=null&&template_bos.trim().length()>0)
                {
                    emialInfo_bos=sendMessageBo.getTemplateMailInfo(template_bos);
                }
                if(tablebo.getTemplate_staff()!=null&&tablebo.getTemplate_staff().trim().length()>0)
                {
                    emailInfo_staff=sendMessageBo.getTemplateMailInfo(tablebo.getTemplate_staff());
                }
                
                if(email_staff_value!=null&& "1".equals(email_staff_value.trim()))
                {
                    rowSet=dao.search(sql);
                    ResultSetMetaData md=rowSet.getMetaData();
                    for(int i=0;i<md.getColumnCount();i++)
                    {
                        int columnType=md.getColumnType(i+1);   
                        String columnName=md.getColumnName(i+1).toLowerCase();
                        if(columnType==java.sql.Types.TIMESTAMP||columnType==java.sql.Types.DATE||columnType==java.sql.Types.TIME)
                            columnMap.put(columnName, "D");
                        else
                            columnMap.put(columnName, "A");
                    }
                }
                rowSet=dao.search(sql);
	            while(rowSet.next())
	            {
	                seqnumList.add(rowSet.getString("seqnum")+"`"+rowSet.getString(a0101)); 
	                realSeqnumList.add(rowSet.getString("seqnum"));
	            }
    			RecordVo _task_vo=new RecordVo("t_wf_task"); 
	            _task_vo.setString("bs_flag", "3"); 
	            _task_vo.setInt("bread",0); 
	            _task_vo.setString("task_state","3");
	            _task_vo.setString("state","08");
	            _task_vo.setString("task_type","2"); 
	            _task_vo.setInt("node_id",-1);
	            _task_vo.setInt("ins_id",ins_vo.getInt("ins_id"));
	            _task_vo.setDate("start_date",new Date()); 
	            _task_vo.setString("task_pri","1");
    	            
	            _task_vo.setString("a0100",tablebo.getUserview().getDbname()+tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
	            _task_vo.setString("a0101",tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
	            _task_vo.setString("a0100_1",tablebo.getUserview().getDbname()+tablebo.getUserview().getA0100()/*this.wf_actor.getActorid()*/);//发送人
	            _task_vo.setString("a0101_1",tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名]
	            //抄送通知
				for(Iterator t=set.iterator();t.hasNext();)
                {
    	            String topic="";
                    for(int j=0;j<seqnumList.size();j++)
                    {
                        if(j<5)
                            topic+=((String)seqnumList.get(j)).split("`")[1]+",";
                    }
    	            if(tablebo.getInfor_type()==1)
                        _task_vo.setString("task_topic",tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)");
                    else if(tablebo.getInfor_type()==2)
                        _task_vo.setString("task_topic",tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)");
                    else
                        _task_vo.setString("task_topic",tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)");
    	            String a0100=tablebo.getUserview().getA0100();
                    String temp=(String)t.next();
                    if(temp.trim().length()==0)
                        continue;
                    String[] temps=temp.split(":"); 
                        
                        
                    _task_vo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
                    _task_vo.setString("actor_type", temps[0]);
                    _task_vo.setString("actorid", temps[1]);
                    dao.addValueObject(_task_vo);
                    String recivenbase="";
                    String recivea0100="";  //这里只处理了自助用户的接收人，业务用户未处理
                    if(temps[1].length()>3&&"1".equals(temps[0])){//自助
                    	recivenbase=temps[1].substring(0,3);
                    	recivea0100=temps[1].substring(3,temps[1].length());
                    	//给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818 
                        sysBo.sendDealWithInfo("", 1,tabId, ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                    }else if("4".equals(temps[0])){//业务
                    	RecordVo vo=new RecordVo("operuser");
                        vo.setString("username",temps[1]);
                        vo=dao.findByPrimaryKey(vo);
                        if(vo.getString("a0100")!=null&&!"".equals(vo.getString("a0100"))){
	                         recivenbase=vo.getString("nbase");
	                         recivea0100=vo.getString("a0100");
	                       //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818 
	                         sysBo.sendDealWithInfo("", 1,tabId, ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                        }
                    }
                    StringBuffer whl_str=new StringBuffer("");
                    ArrayList recordList=new ArrayList();
                    for(int j=0;j<seqnumList.size();j++)
                    {
                        RecordVo vo=new RecordVo("t_wf_task_objlink"); 
                        String[] _str=((String)seqnumList.get(j)).split("`");
                        whl_str.append(",'"+_str[0]+"'");
                        vo.setString("seqnum",_str[0]);
                        vo.setInt("ins_id", ins_vo.getInt("ins_id"));
                        vo.setInt("task_id",_task_vo.getInt("task_id"));
                        vo.setInt("tab_id", ins_vo.getInt("tabid"));
                        vo.setInt("node_id",_task_vo.getInt("node_id"));
                        vo.setString("task_type","3"); 
                        vo.setInt("submitflag",1);
                        vo.setInt("count",1);
                        vo.setInt("state",1);
                        recordList.add(vo); 
                    }
                    dao.addValueObject(recordList);  
                        
                    //发送消息
                    sendMessageBo.setTask_id(String.valueOf(_task_vo.getInt("task_id")));
                    if(!"0".equals(isSendMessage)&&newEmailBo!=null)
                    { 
                    	 String a_title=_task_vo.getString("task_topic");
                         String a_context=a_title; 
                         String whl_str_sub="";
                         if(whl_str.length()>0){//排除抄送时必选邮件模版的情况
                       	  whl_str_sub=whl_str.substring(1);
                         }
                         sendMessageBo.sendFilingMessage(_task_vo.getString("actorid"),_task_vo.getString("actor_type"),newEmailBo,tablebo,isSendMessage,a_context,a_title,
                                   String.valueOf(ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ("+whl_str_sub+")","0",columnMap,realSeqnumList,"3",tablebo.getInfor_type());
                    }
                }
    			//抄送通知本人
    			if(email_staff_value!=null&&"1".equalsIgnoreCase(email_staff_value)){
    				rowSet=dao.search(sql); 
	                _task_vo.setString("task_topic",tablebo.getTable_vo().getString("name"));
	                while(rowSet.next())
	                { 
	                    _task_vo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
	                    _task_vo.setString("actor_type","1");
	                    _task_vo.setString("actorid",rowSet.getString("basepre")+rowSet.getString("a0100"));
	                    dao.addValueObject(_task_vo);
    	                    
	                    //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
	                    sysBo.sendDealWithInfo("", 1,tabId, ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),rowSet.getString("basepre"), rowSet.getString("a0100"),"2");

    	                    
	                    RecordVo vo=new RecordVo("t_wf_task_objlink");  
	                    vo.setString("seqnum",rowSet.getString("seqnum"));
	                    vo.setInt("ins_id", ins_vo.getInt("ins_id"));
	                    vo.setInt("task_id",_task_vo.getInt("task_id"));
	                    vo.setInt("tab_id", ins_vo.getInt("tabid"));
	                    vo.setInt("node_id",_task_vo.getInt("node_id"));
	                    vo.setString("task_type","3"); 
	                    vo.setInt("submitflag",1);
	                    vo.setInt("count",1);
	                    vo.setInt("state",1);
	                    dao.addValueObject(vo);
    	                    
	                  //String a_title=_task_vo.getString("task_topic"); 
	                    //String a_context=a_title; 
	                    //报备时,邮件的主题和内容,均取值为模板的名称,如果报备设置了邮件模板,后面的方法重新取值
	                  String a_title=tablebo.getTable_vo().getString("name"); 
	                  String a_context=a_title;
	                    sendMessageBo.setTask_id(String.valueOf(_task_vo.getInt("task_id")));
	                    sendMessageBo.sendFilingMessage("","",newEmailBo,tablebo,isSendMessage,a_context,a_title,
	                            String.valueOf(ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ('"+vo.getString("seqnum")+"')","1",columnMap,realSeqnumList,"3",tablebo.getInfor_type());
	                }
    			}
    			
//    			SendMessageBo bo=new SendMessageBo(this.getFrameconn(),this.userView);
//    			StringBuffer buf=new StringBuffer();
//    			String dest_base = tablebo.getDest_base();//获取目标库前缀
//    			bo.setDestination_a0100(tablebo.getDestination_a0100());
//    			if((tablebo.getOperationtype()==1||tablebo.getOperationtype()==2)&&dest_base!=null&&!"".equals(dest_base))
//    				buf.append("select '"+dest_base+"' dest_base,* from templet_"+tabId+" where ins_id="+ins_id);
//    			else
//    				buf.append("select * from templet_"+tabId+" where ins_id="+ins_id);
//    			bo.setTask_id(ins.getTask_vo().getString("task_id"));
//    			bo.sendMessage(set,isSendMessage,context,title,tabId,buf.toString(),false,email_staff_value,null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            String message = ex.toString();
            if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1 && Sql_switcher.searchDbServer() == 1) {
                PubFunc.resolve8060(this.getFrameconn(), "templet_" + tabId);
                throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
            } else
                throw GeneralExceptionHandler.Handle(ex);
        }
        finally {
			PubFunc.closeDbObj(rowSet);
		}
    }
    /**
     * 结束单据重复提交入库
     * 子集新增判断是否已存在相同数据，存在则不提交数据
     * @param data_list
     * @throws Exception
     */
    private void repreateSubmit(ArrayList  data_list)throws Exception{
    	try {
    		for(Object obj:data_list) {
    			MorphDynaBean map=(MorphDynaBean)obj;
        		String tabid=(String)map.get("tabid");
        		String ins_id=(String)map.get("ins_id");
        		ins_id = StringUtils.isEmpty(ins_id)?"0":ins_id;
        		String task_id=(String)map.get("task_id");
        		task_id=PubFunc.decrypt(task_id);
        		TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid), this.userView);
                tablebo.setValidateM_L(true);
                tablebo.setBEmploy(false);    
                tablebo.setPcOrMobile("0");
                tablebo.setRepreateSubmit(true);
                tablebo.setIns_id(Integer.parseInt(ins_id));
                //提交到人员库
                tablebo.expDataIntoArchive(Integer.parseInt(task_id));//将数据插入到相应的库中
        	}
		} catch (Exception e) {
			throw e;
		}
    }
    
    private void setTaskVoForIns(WF_Instance ins, String ins_id, String tabId) {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	RowSet rset = null;
    	ArrayList paramList = new ArrayList();
		String sql = "select task_id from t_wf_task where ins_id=?";
		paramList.add(Integer.parseInt(ins_id));
		try {
			rset = dao.search(sql,paramList);
			if(rset.next()) {
				int task_id = rset.getInt("task_id");
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",task_id);
				task_vo=dao.findByPrimaryKey(task_vo);
				ins.setTask_vo(task_vo);
				ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),task_id,3,tabId,this.userView,""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
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
            vo.setDate("end_date",DateStyle.getSystemTime());//结束时间
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
            {
            	//vo.setString("actorid",tablebo.getUserview().getUserName());
            	if("1".equals(selfapply))//员工通过自助平台发动申请
             	{
             		vo.setString("actorid", tablebo.getUserview().getDbname()+tablebo.getUserview().getA0100()); //tablebo.getUserview().getUserName()
                     vo.setInt("actor_type",1);
             	}
             	else {
 					vo.setString("actorid",tablebo.getUserview().getUserName());
 					vo.setInt("actor_type",4);
 				}
            }
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
            taskvo.setString("actorid",vo.getString("actorid"));//当前对象  流程定义的参与者     
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
