/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.service.SynOaService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

/**
 * <p>Title:WF_Instance</p>
 * <p>Description:工作流实例</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 200612:57:41 PM
 * @author chenmengqing
 * @version 4.0
 */
public class WF_Instance  {
	private String url_s="";
	
	/**流程定义对象*/
	private TemplateTableBo tablebo;
	private Connection conn;
	/**创建下一个任务对象*/
	private RecordVo task_vo;
	 /**下一个任务节点(含多个），用于自动流转*/
    private ArrayList nextTaskVoList=new ArrayList();
	
	public ArrayList getNextTaskVoList() {
		return nextTaskVoList;
	}
	private int ins_id;
	/** 当前任务是否是单一节点下多任务中的一员 */
	private boolean isParallel=false;
	
	/** 邮件抄送 */
	String isSendMessage="0";
	String user_h_s="";
	String email_staff_value="";
	String objs_sql="";
	String specialOperate="0"; //业务模板中人员需要报送给各自领导进行审批处理
	
	HashMap otherParaMap=new HashMap();  //pre_pendingID:普天代办id
	
	TemplateTableParamBo tableParamBo=null;
	private boolean bSelfApply=false;
	private String moduleId = ""; 
	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public boolean isbSelfApply() {
		return bSelfApply;
	}

	public void setbSelfApply(boolean bSelfApply) {
		this.bSelfApply = bSelfApply;
	}

	public int getIns_id() {
		return ins_id;
	}

	public void setIns_id(int ins_id) {
		this.ins_id = ins_id;
	}

	public WF_Instance(TemplateTableBo tablebo, Connection conn) {
		this.tablebo=tablebo;
		this.conn = conn;
	}
	
	public WF_Instance(int tabid, Connection conn,UserView userview)throws GeneralException{
		tablebo=new TemplateTableBo(conn,tabid,userview);
		this.conn=conn;
	}

	/**
	 * 取得流程开始结点
	 * @return
	 * @throws GeneralException
	 */
	public WF_Node getStartNode()throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		WF_Node start_node=null;
		try
		{
			start_node=new WF_Node(this.tablebo,this.conn);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_node where nodetype='");
			strsql.append(NodeType.START_NODE);
			strsql.append("' and tabid=");
			strsql.append(tablebo.getTable_vo().getString("tabid"));
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
			{
				start_node.setNode_id(rset.getInt("node_id"));
				start_node.setNodename(rset.getString("nodename"));
				start_node.setNodetype(Integer.parseInt(rset.getString("nodetype")));
				start_node.setExt_param(Sql_switcher.readMemo(rset,"ext_param"));		
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return start_node;
	}


	/**
	 * 取得流程开始结点
	 * @return
	 * @throws GeneralException
	 */
	private WF_Node getEndNode()throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		WF_Node end_node=null;
		try
		{
			end_node=new WF_Node(this.tablebo,this.conn);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_node where nodetype='");
			strsql.append(NodeType.END_NODE);
			strsql.append("' and tabid=");
			strsql.append(tablebo.getTable_vo().getString("tabid"));
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
			{
				end_node.setNode_id(rset.getInt("node_id"));
				end_node.setNodename(rset.getString("nodename"));
				end_node.setNodetype(Integer.parseInt(rset.getString("nodetype")));
				end_node.setExt_param(Sql_switcher.readMemo(rset,"ext_param"));		
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return end_node;
	}	
	
	
	/**
	 * 结束(并行非最后审批人)任务
	 * @param taskid
	 * @param ins_id
	 * @param objs
	 */
	public void finishedTask(int taskid,int ins_id,WF_Actor actor,UserView userview)
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="templet_"+this.tablebo.getTable_vo().getString("tabid");		
			RecordVo task_vo=new RecordVo("t_wf_task");
			task_vo.setInt("task_id",taskid);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
            }

			/**结束前面的任务，创建下一个新的任务*/
			task_vo.setString("sp_yj",actor.getSp_yj());
			task_vo.setString("content",actor.getContent());
			
			String sender=null;
			if(userview.getStatus()!=0) {
                sender=userview.getDbname()+userview.getA0100();
            } else {
                sender=userview.getUserId();
            }
			//if(sender==null||sender.equals(""))
			String fullsender=userview.getUserFullName();
			if(fullsender==null|| "".equalsIgnoreCase(fullsender)) {
                fullsender=userview.getUserId();
            }
			task_vo.setString("a0100",sender/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
			task_vo.setString("a0101",fullsender/*userview.getUserFullName()*/);//人员姓名 实际处理人员姓名
			String appuser=task_vo.getString("appuser")+userview.getUserName()+",";
			task_vo.setString("appuser", appuser);
			
			task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
			task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
				
			task_vo.setString("state","08"); //审批状态
			task_vo.setInt("bread",0);//是否已阅读
			   
			     /**根据任务分配算法，具体对应到准*/
			task_vo.setString("actorid",actor.getActorid());//当前对象	流程定义的参与者	 
			task_vo.setString("actor_type",actor.getActortype());
			task_vo.setString("a0100",actor.getActorid());//人员编号 实际处理人员编码
			task_vo.setString("a0101",actor.getActorname());//人员姓名 实际处理人员姓名
			task_vo.setString("a0100_1",actor.getActorid());//发送人
			task_vo.setString("a0101_1",actor.getActorname());//发送人姓名	            
			task_vo.setString("url_addr","");//审批网址
			task_vo.setString("params","");//参数	
			task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"(,共0人)");
			dao.update("update t_wf_task_datalink set state=1 where ins_id="+ins_id+" and  task_id="+taskid);
			dao.updateValueObject(task_vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 当前任务是否有未选中借宿的象（t_wf_task_datalink）
	 * @param taskid
	 * @return
	 */
	
	/*
	public boolean isHaveObjTheTask(int taskid,int ins_id)
	{
		boolean bflag=false;
		String tablename;
		int nm=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			tablename="templet_"+this.tablebo.getTable_vo().getString("tabid");		
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nm from t_wf_task_datalink td,"+tablename+" where td.seqnum="+tablename+".seqnum ");
			buf.append(" and td.ins_id="+ins_id+" and td.task_id="+taskid+" and ("+tablename+".submitflag=0 or "+tablename+".submitflag is null) ");
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				nm=rset.getInt("nm");
			if(nm>0)
				bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
		}
		return bflag;
	}	
	*/
	
	
	
	/**
	 * 当前任务是否有未选中报批的象
	 * @param taskid
	 * @return
	 */
	public boolean isHaveObjTheTask(int taskid)
	{
		boolean bflag=false;
		String tablename;
		int nm=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			tablename="templet_"+this.tablebo.getTable_vo().getString("tabid");			
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nm from ");
			buf.append(tablename+" tb,t_wf_task_objlink two");
			buf.append(" where tb.ins_id=");
			buf.append(this.ins_id+" and two.ins_id="+this.ins_id+" and two.tab_id="+this.tablebo.getTable_vo().getString("tabid")+"  and two.task_id="+taskid+" and two.seqnum=tb.seqnum ");
			buf.append(" and (two.submitflag=0 or two.submitflag is null) and (two.state=0 or two.state is null) ");
			RowSet rset=dao.search(buf.toString());
			if(rset.next()) {
                nm=rset.getInt("nm");
            }
			if(nm>0) {
                bflag=true;
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
		}
		return bflag;
	}	
	/**
	 * 把当前实例下审批人员与对应的任务号挂接上
	 * @return
	 * @throws GeneralException
	 */
	public boolean setTempletTaskLink()throws GeneralException
	{
		boolean bflag=true;
		int task_id=-1;
		String tablename;
		try
		{
			/*
			ContentDAO dao=new ContentDAO(this.conn);
			tablename="templet_"+this.tablebo.getTable_vo().getString("tabid");
			task_id=this.getTask_vo().getInt("task_id");
			StringBuffer buf=new StringBuffer();
			buf.append("update ");
			buf.append(tablename);
			buf.append(" set task_id=");
			buf.append(task_id);
			buf.append(" where ins_id=");
			buf.append(this.ins_id);
			
			dao.update(buf.toString());*/
			
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;
	}
	
	private boolean setTempletNextTaskLink(int curr_task_id)throws GeneralException
	{
		boolean bflag=true;
		int task_id=-1;
		String tablename;
		try
		{
			if(this.task_vo==null) {
                return true;
            }
			ContentDAO dao=new ContentDAO(this.conn);
			tablename="templet_"+this.tablebo.getTable_vo().getString("tabid");
			task_id=this.getTask_vo().getInt("task_id");
			StringBuffer buf=new StringBuffer();
			
			if(isParallel)  //如果为单节点下多任务中的操作
			{
				buf.append("update ");
				buf.append(tablename);
				buf.append(" set task_id=");
				buf.append(task_id);
				buf.append(" where ins_id=");
				buf.append(this.ins_id);
				if(!isReAssign)//如果不是任务监控里的重新分配
                {
                    buf.append(" and submitflag=1 ");
                }
				buf.append(" and exists (select null from t_wf_task_datalink where t_wf_task_datalink.seqnum="+tablename+".seqnum ");
				buf.append(" and t_wf_task_datalink.ins_id="+this.ins_id+" and task_id="+curr_task_id+" )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update t_wf_task_datalink");
				buf.append(" set state=1");
				buf.append(" where  ins_id="+this.ins_id+" and   task_id="+curr_task_id);
				buf.append(" and exists (select null from "+tablename+" where t_wf_task_datalink.seqnum="+tablename+".seqnum ");
				buf.append(" and  submitflag=1 and "+tablename+".ins_id="+this.ins_id+"   )");
				dao.update(buf.toString());
				
			}
			else
			{
				buf.append("update ");
				buf.append(tablename);
				buf.append(" set task_id=");
				buf.append(task_id);
				buf.append(" where ins_id=");
				buf.append(this.ins_id);
				if(!isReAssign)//如果不是任务监控里的重新分配
                {
                    buf.append(" and submitflag=1 ");
                }
				buf.append(" and task_id=");
				buf.append(curr_task_id);
				dao.update(buf.toString());
			}
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;
	}	
	
	
	/**
	 * 
	 * @param ins_id
	 * @param task_id
	 * @param flag  1:自助申请  2：业务申请   3.审批过程
	 * @param tabid
	 * @param userView
	 * @return
	 */
	public String getObjsSql(int ins_id,int task_id,int flag,String tabid,UserView userView,String whl)
	{
		StringBuffer sql=new StringBuffer("select * from ");
		if(flag==1) {
            sql.append("g_templet_"+tabid);
        } else if(flag==2) {
            sql.append(userView.getUserName()+"templet_"+tabid);
        } else if(flag==3) {
            sql.append("templet_"+tabid);
        }
		
		if(flag==1)
		{
			sql.append(" where lower(basepre)='");
			sql.append(userView.getDbname().toLowerCase());
			sql.append("' and a0100='");
			sql.append(userView.getA0100());
			sql.append("'");
		}
		if(flag==2)
		{
			sql.append(" where submitflag=1"+whl);
		}
		if(flag==3)
		{
		//	if(isParallel(task_id,ins_id))
			{
				sql.setLength(0);
				sql.append("select templet_"+tabid+".* from t_wf_task_objlink td,templet_"+tabid+" where templet_"+tabid+".seqnum=td.seqnum ");
				sql.append(" and td.ins_id="+ins_id+" and td.task_id="+task_id);
			}
		/*	else
			{	sql.append(" where  ins_id="+ins_id+" and task_id="+task_id);
				if(!isReAssign)	
					sql.append(" and submitflag=1");
			}*/
		}
		
		
		
		return sql.toString();
	}
	
	
	/**
	 * 初始起始任务
	 * 发启任务
	 * 关键算法参见设计文档
	 * @return
	 */
	private boolean InitStartTask(RecordVo instancevo,WF_Actor actor,String whl)throws GeneralException
	{
		boolean bflag=true;
		WF_Node start_node=getStartNode();
		try
		{
			start_node.setbSelfApply(this.bSelfApply);
			start_node.setUrl_s(this.url_s);
			if(start_node==null||start_node.getNodetype()==0) {
                throw new GeneralException(ResourceFactory.getProperty("error.start.notdefine"));
            }
			/**创建任务*/
			//start_node.setWf_actor(actor); //设置审批对象及其内容,支持任意流转,在一个节点上做任意流转
			
			start_node.setIsSendMessage(isSendMessage);
			start_node.setUser_h_s(user_h_s);
			start_node.setEmail_staff_value(email_staff_value);
			start_node.setObjs_sql(this.objs_sql);
			start_node.setOtherParaMap(this.otherParaMap);
			start_node.setOpt("1"); //报批
			
			
			String srcTab=this.tablebo.getUserview().getUserName()+"templet_"+this.tablebo.getTabid();
			if(this.tablebo.isBEmploy()) {
                srcTab="g_templet_"+this.tablebo.getTabid();
            }
			start_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),-1,srcTab,this.tablebo);
			
			if(whl==null||whl.trim().length()==0||this.tablebo.isBEmploy()) {
                start_node.createTask(instancevo,actor,null);
            } else  //拆单
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select *   from  "+srcTab+"  where 1=1 and submitflag=1 "+whl);
				ArrayList list=new ArrayList();
				while(rowSet.next()) {
                    list.add(rowSet.getString("seqnum").trim());
                }
				start_node.setCd_whl(whl);
				start_node.createTask(instancevo,actor,list);
				if(rowSet!=null) {
                    PubFunc.closeDbObj(rowSet);
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		/**创建的下一个任务*/
		this.setTask_vo(start_node.getNexTTask_vo());
		
		
		
		return bflag;
	}
	
	
	/**
	 * 求实际的业务数 (t_wf_task_datalink)
	 * @return
	 */
	public String getRecordBusiTopic(int task_id,int ins_id)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append(this.tablebo.getTable_vo().getString("name"));
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname="templet_"+this.tablebo.getTable_vo().getInt("tabid");
			String a0101="a0101_1";
			if(this.tablebo.getInfor_type()==2||this.tablebo.getInfor_type()==3) {
                a0101="codeitemdesc_1";
            }
			RecordVo vo=new RecordVo(tabname);
			int operationtype=this.tablebo.getOperationtype();
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			else if(operationtype==5&&vo.hasAttribute("codeitemdesc_2"))//调入
			{
					a0101="codeitemdesc_2";
			}
			String strWhere=" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and ins_id="+ins_id+" and task_id="+task_id;
			strWhere+=" )";
			
			strsql.append("select  "+tabname+".");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());			
			int i=0;
			while(rset.next())
			{
				if(i!=0) {
                    stopic.append(",");
                }
				if(i<=4) {
                    stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
                }
				i++;
			}
			nmax=i;
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(this.tablebo.getInfor_type()==2||this.tablebo.getInfor_type()==3) {
                stopic.append("条记录)");
            } else {
                stopic.append("人)");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}	
	
	

	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopic(int task_id,boolean flag)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append(this.tablebo.getTable_vo().getString("name"));
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname="templet_"+this.tablebo.getTable_vo().getInt("tabid");
			String a0101="a0101_1";
			if(this.tablebo.getInfor_type()==2||this.tablebo.getInfor_type()==3) {
                a0101="codeitemdesc_1";
            }
			RecordVo vo=new RecordVo(tabname);
			int operationtype=this.tablebo.getOperationtype();
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			else if(operationtype==5&&vo.hasAttribute("codeitemdesc_2"))//调入
			{
					a0101="codeitemdesc_2";
			}	
			String strWhere=" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id and task_id="+task_id;
			if(flag) {
                strWhere+=" and (state is null or  state=0) ";
            }
			strWhere+=" )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());			 
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
                }
				if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());
			rset=dao.search(strsql.toString());
			if(rset.next()) {
                nmax=rset.getInt("nmax");
            }
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(this.tablebo.getInfor_type()==2||this.tablebo.getInfor_type()==3) {
                stopic.append("条记录)");
            } else {
                stopic.append("人)");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}	
	/**
	 * 更新任务标题
	 * @param task_id
	 * @return
	 */
	public boolean updateTaskTopic(int task_id,boolean flag)
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RecordVo vo=new RecordVo("t_wf_task");
			vo.setInt("task_id",task_id);
			vo.setString("task_topic", getRecordBusiTopic(task_id,flag));
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
	
	
	
	/**
	 * 判断其他审批人是否已处理完。
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public boolean isFinishTask(String task_id,String ins_id,UserView userView)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select count(*) from t_wf_task_objlink where ins_id="+ins_id+" and task_id="+task_id+" and state=0";
			sql+=" and "+Sql_switcher.isnull("special_node","0")+"=1 and lower("+Sql_switcher.isnull("username","' '")+")<>'"+userView.getUserName().toLowerCase()+"' and lower("+Sql_switcher.isnull("username","' '")+")<>'"+userView.getDbname().toLowerCase()+userView.getA0100()+"'   "; 
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    flag=false;
                }
			}
			if(rowSet!=null) {
                PubFunc.closeDbObj(rowSet);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	/**
	 * 判断当前所处理的任务是否是 需按 权限控制的角色(特殊角色)
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public boolean isRoleByPriv(String task_id,String ins_id)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select count(*) from t_wf_task_objlink where ins_id="+ins_id+" and task_id="+task_id;
			sql+=" and "+Sql_switcher.isnull("special_node","0")+"=1 ";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    flag=true;
                }
			}
			if(rowSet!=null) {
                PubFunc.closeDbObj(rowSet);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	
	/**
	 * 创建下一节点任务
	 * @param ins_vo
	 * @param actor
	 * @param taskid
	 * @param userview
	 * @return
	 * @throws GeneralException
	 */
	public boolean createNextTask(RecordVo ins_vo,WF_Actor actor,int taskid,UserView userview)throws GeneralException
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			ins_vo=dao.findByPrimaryKey(ins_vo);
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			
			
			
			//20140728  dengcan 解决报批时写入考勤数据报错，结果流程处理结束问题。
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select * from templet_"+this.tablebo.getTabid()); 
			strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tablebo.getTabid()+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tablebo.getTabid()+".ins_id=t_wf_task_objlink.ins_id ");
			strsql.append("  and task_id="+taskid+" and tab_id="+this.tablebo.getTabid()+" and ( state=1 or  state=0 ) ) "); //20140917 dengcan 
			
			insertKqApplyTable(strsql.toString(),String.valueOf(this.tablebo.getTabid()),"0","02","templet_"+this.tablebo.getTabid()); //往考勤申请单中写入报批记录
            TemplateInterceptorAdapter.preHandle("templet_"+this.tablebo.getTabid(),this.tablebo.getTabid(), taskid,null, "appeal",this.tablebo.getUserview(),"");
			 
			
			
			
			
			RecordVo task_vo=new RecordVo("t_wf_task");
			task_vo.setInt("task_id",taskid);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
            }
			String fullsender=userview.getUserFullName();
			if(fullsender==null|| "".equalsIgnoreCase(fullsender))
			{
				//fullsender=userview.getUserId();
				fullsender=userview.getUserName();
			}
			boolean roleByPriv=isRoleByPriv(String.valueOf(taskid),String.valueOf(this.ins_id));
			if(roleByPriv&&!isFinishTask(String.valueOf(taskid),String.valueOf(this.ins_id),userview))
			{
				task_vo.setString("a0101",task_vo.getString("actorname"));
				String content=task_vo.getString("content");
				if(content!=null&&content.length()>0)//liuyz bug31611 如果content有内容则加换行，没内容直接输出审批意见否则查审批意见会多出空行
				{
					content=content+"\n\n"+fullsender+": "+actor.getContent();
				}
				else
				{
					content=fullsender+": "+actor.getContent();
				}
				task_vo.setString("content",content);//
				dao.updateValueObject(task_vo);
				String sql=" update t_wf_task_objlink set state=1 where ins_id="+ins_id+" and task_id="+taskid;
				sql+=" and "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+userview.getUserName().toLowerCase()+"' or lower(username)='"+userview.getDbname().toLowerCase()+userview.getA0100()+"' )    "; 
				dao.update(sql);
				return bflag;
			}
			
			/**结束前面的任务，创建下一个新的任务*/
			task_vo.setString("sp_yj",actor.getSp_yj());
			String sender=null;
			if(userview.getStatus()!=0) {
                sender=userview.getDbname()+userview.getA0100();
            } else {
                sender=userview.getUserId();
            }
			//if(sender==null||sender.equals(""))
			
			task_vo.setString("a0100",sender/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
			if(roleByPriv)
			{
				task_vo.setString("a0101",task_vo.getString("actorname"));
				String content=task_vo.getString("content"); 
				if(content!=null&&content.length()>0)//liuyz bug31611 如果content有内容则加换行，没内容直接输出审批意见否则查审批意见会多出空行
				{
					content=content+"\n\n"+fullsender+": "+actor.getContent();
				}
				else
				{
					content=fullsender+": "+actor.getContent();
				}
				task_vo.setString("content",content);//
			}
			else
			{
				task_vo.setString("a0101",fullsender/*userview.getUserFullName()*/);//人员姓名 实际处理人员姓名 
				task_vo.setString("content",actor.getContent());
			}
			/**结束前面的任务，创建新的任务*/
			boolean bend=false;
			 
			
			int node_id=task_vo.getInt("node_id");
			WF_Node wf_node=new WF_Node(node_id,this.conn,this.tablebo);
			
			if(tablebo.getOperationtype()==0&&wf_node.getNextNodeIsEnd(node_id))  //下一节点是否为结束节点
			{
				ArrayList fieldlist=tablebo.getAllFieldItem();
				HashMap subhm=tablebo.readUpdatesSetField(fieldlist);
				if(subhm.get("A01")==null)
				{
					throw new GeneralException(ResourceFactory.getProperty("error.input.a01read"));
				}
			}
			
			if(roleByPriv) {
                wf_node.setActorname(task_vo.getString("actorname"));
            }
			
	//		wf_node.setTaskid(taskid);//这句很重要，主要为了不再创建当前节点对应的任务 
			wf_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),taskid,"templet_"+this.tablebo.getTabid(),this.tablebo); 
			wf_node.setUrl_s(this.url_s);
			wf_node.setIsSendMessage(isSendMessage);
	//由于抄送的信息和通知到本人都是由报备发送的所以  要把代码提前
	         wf_node.setEmail_staff_value(email_staff_value);
	            
	            wf_node.setUser_h_s(user_h_s);
	            wf_node.setObjs_sql(this.objs_sql);
	            wf_node.setOtherParaMap(this.otherParaMap); 
			//按设置的高级条件发送报备信息
			wf_node.setIns_vo(ins_vo);
			wf_node.sendFilingTasks(taskid,wf_node.getExt_param(),wf_node); //bug 35083 只有结束节点才给本人发送邮件。
			
//			邮件抄送 是由报备来发送的所以  将代码提前了 xcs 2014-06-4
//			wf_node.setEmail_staff_value(email_staff_value);
//			
//			wf_node.setUser_h_s(user_h_s);
//			wf_node.setObjs_sql(this.objs_sql);
//			wf_node.setOtherParaMap(this.otherParaMap); 
			wf_node.createTask(ins_vo,actor,null);
	 		
		
			task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
			task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
			 
			if(!wf_node.isWaiting()) //按照同进同出原则，无需等待可以流转至下一节点时
			{
				String topic=this.tablebo.getRecordBusiTopic(taskid,ins_vo.getInt("ins_id"));
				task_vo.setString("task_topic",topic); 
			}
			else {
                task_vo.setString("task_state",String.valueOf(NodeType.TASK_STOP));
            }
			dao.updateValueObject(task_vo);
			
			/**创建的下一个任务*/
			this.setTask_vo(wf_node.getNexTTask_vo());		
			this.nextTaskVoList=wf_node.getNextTaskVoList();
			
			PendingTask imip=new PendingTask();
			String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(taskid)); 
			String pendingType="业务模板";  
			imip.updatePending("T",pendingCode,1,pendingType,this.tablebo.getUserview());  
			
			if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))) {
                this.updateApproveOpinion(ins_vo,actor,userview,taskid);
            }
		    //////信息归档/////	liuzy 20150821
		    this.filingInformation(ins_vo, actor, userview);
		
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
		
	}
	
	 /**
	 * 流程完成后，将信息归档 liuzy 20150821
	 * @throws GeneralException 
	 * 
	 */
	public void filingInformation(RecordVo ins_vo,WF_Actor actor,UserView userview) throws GeneralException
	{
		try
		{
			if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED)))
			{
				if(actor.getSp_yj()!=null&&actor.getSp_yj().trim().length()>0) {
                    this.tablebo.setSp_yj(actor.getSp_yj());
                }
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer strsql=new StringBuffer("");
				if(this.getTask_vo().getInt("task_id")!=0) ////往考勤申请单中写入记录
				{
					strsql.setLength(0);
					strsql.append("select * from templet_"+this.tablebo.getTabid()); 
					strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tablebo.getTabid()+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tablebo.getTabid()+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+this.getTask_vo().getInt("task_id")+" and tab_id="+this.tablebo.getTabid()+" and state=1 ) ");
					String operState="03";
					if(!"01".equals(this.tablebo.getSp_yj())) {
                        operState="07";
                    }

					insertKqApplyTable(strsql.toString(),String.valueOf(this.tablebo.getTabid()),"0",operState,"templet_"+this.tablebo.getTabid()); //往考勤申请单中写入报批记录
		            TemplateInterceptorAdapter.preHandle("templet_"+this.tablebo.getTabid(),this.tablebo.getTabid(), this.getTask_vo().getInt("task_id"), null, "submit",this.tablebo.getUserview(),"");
	
				}
				this.tablebo.setIns_id(ins_vo.getInt("ins_id"));
				this.tablebo.expDataIntoArchive(this.getTask_vo().getInt("task_id"));
				
				
				//判断是否 调用中建接口  bo.sendMessageToOa(String.valueOf(this.tablebo.getTabid())) ;
				
				
				//如果当前流程实例中存在正在运行中的任务，重新把实例置为运行状态 
				StringBuffer buf=new StringBuffer();
				if(isHaveRuningTask(ins_vo.getInt("ins_id")))
				{
					//流程实例启动 
					buf.append("update t_wf_instance set end_date=null,finished='2' where ins_id=");
					buf.append(ins_vo.getInt("ins_id"));
					dao.update(buf.toString());
					//把END任务节点设置成人工节点 
				
					
					buf.setLength(0);
					buf.append("update t_wf_task set actorname='");
					buf.append(task_vo.getString("actorname"));
					buf.append("',actorid='");
					buf.append(task_vo.getString("actorid"));
					buf.append("',actor_type='");
					buf.append(task_vo.getString("actor_type"));
					buf.append("',A0100='");
					buf.append(userview.getUserId());
					buf.append("',A0101='");
					buf.append(userview.getUserFullName());
					buf.append("',A0100_1='");
					buf.append(task_vo.getString("a0100_1"));
					buf.append("',A0101_1='");
					buf.append(task_vo.getString("a0101_1"));
					buf.append("',bread=1,flag=1,task_type=2 where task_id=");
					buf.append(this.getTask_vo().getInt("task_id"));
					dao.update(buf.toString());
					
				}
			}
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 分析当前流程实例是否存在正在运行中的任务
	 * @param ins_id 流程实例号
	 * @return
	 */
	public boolean isHaveRuningTask(int ins_id)
	{
		boolean bflag=false;
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			buf.append("select task_id from t_wf_task where "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and ins_id=");
			buf.append(ins_id);
			buf.append(" and task_state='3'");
			RowSet rset=dao.search(buf.toString());
			if(rset.next()) {
                bflag=true;
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
	/**
	 * 求当前节点实际处理人的上一个人工节点。。。
	 * 目前仅考虑串行处理的方式
	 * @param nodelist
	 * @return
	 */
	private int getPreHumanNodeId(ArrayList nodelist)
	{
		int node_id=-1;
		if(nodelist==null||nodelist.size()==0) {
            return node_id;
        }
		/**目前串行路由仅只有一个节点，这样处理没问题啦。。。*/
		for(int i=0;i<nodelist.size();i++)
		{
			WF_Node wf_node=(WF_Node)nodelist.get(i);
			node_id=wf_node.getNode_id();
		}
		return node_id;
	}
	
	
	/**
	 * 查上次任务处理者
	 * @param task_vo
	 * @return
	 * @throws GeneralException
	 */
	private WF_Actor findPreTaskActorByPriID(RecordVo task_vo )throws GeneralException
	{
		WF_Actor tempactor=null;
		try
		{
			
			int pri_task_id=task_vo.getInt("pri_task_id");
			if(pri_task_id!=0)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select actorid,actor_type,actorname from t_wf_task where ins_id="+task_vo.getInt("ins_id")+" and task_id="+pri_task_id);
				if(rowSet.next())
				{
					tempactor=new WF_Actor();
					tempactor.setActorid(rowSet.getString("actorid"));
					tempactor.setActortype(rowSet.getString("actor_type"));
					tempactor.setActorname(rowSet.getString("actorname"));
				}
				/*
				RowSet rowSet=dao.search("select role_id from t_wf_task_datalink where ins_id="+task_vo.getInt("ins_id")+" and task_id="+pri_task_id);
				if(rowSet.next())
				{
					tempactor=new WF_Actor(rowSet.getString("role_id"),"2");
				}
				*/
				PubFunc.closeDbObj(rowSet);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return tempactor;
	}
	
	
	/**
	 * 手工指派方式，查上次任务处理者
	 * @param task_vo
	 * @return
	 * @throws GeneralException
	 */
	private WF_Actor findPreTaskActor(RecordVo task_vo )throws GeneralException
	{
		WF_Actor tempactor=null;
		try
		{
			String appuser=task_vo.getString("appuser");
			String[] userlist=StringUtils.split(appuser,",");
			if(userlist.length!=0)
			{
				tempactor=new WF_Actor(userlist[userlist.length-1],"1");
				tempactor.setActorname(userlist[userlist.length-1]/*rset.getString("a0101")*/);	//考虑用姓名
				if(userlist[userlist.length-1].indexOf(":")!=-1)
				{
					String[] temps=userlist[userlist.length-1].split(":");
					tempactor.setActortype(temps[0]);
					tempactor.setActorid(temps[1]);
					
				}
				else
				{
					if(userlist[userlist.length-1].length()<11)
					{	
						tempactor.setActortype("4");
						tempactor.setActorname(task_vo.getString("a0101_1"));
					}
					else
					{	
						tempactor.setActortype("1");  //?有点问题，应再在operuser表中查一下有没有此用户
						tempactor.setActorname(task_vo.getString("a0101_1"));
					}
				}
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return tempactor;
	}
	/**
	 * 去掉最后一个用户，层层驳回操作
	 * @param task_vo
	 * @return
	 */
	private String getOutOfReject(RecordVo task_vo )throws GeneralException
	{
		String appuser="";
		try
		{
			StringBuffer buf=new StringBuffer();
			appuser=task_vo.getString("appuser");
			String[] userlist=StringUtils.split(appuser,",");
			for(int i=0;i<userlist.length-1;i++)
			{
				buf.append(userlist[i]);
				buf.append(",");
			}//for i loop end.
			appuser=buf.toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
		return appuser;
	}

	/**
	 * 驳回任务，具体为：结束当前任务，创建新的任务
	 * @param ins_vo
	 * @param taskid
	 * @param userview
	 * @return
	 * @throws GeneralException
	 */
	public boolean rejectTask(RecordVo ins_vo,WF_Actor actor,int taskid,UserView userview)throws GeneralException
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			 
			ins_vo=dao.findByPrimaryKey(ins_vo);//当前流程
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			/**结束当前任务*/
			RecordVo task_vo=new RecordVo("t_wf_task");//当前任务的信息。后面要更新它里面的某几个字段
			task_vo.setInt("task_id",taskid);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
            }
			task_vo.setString("sp_yj",actor.getSp_yj()); //当前审批人的意见及相关描述
			
			//20190731
			TemplateInterceptorAdapter.preHandle("templet_"+this.tablebo.getTabid(),this.tablebo.getTabid(),taskid, null, "reject", userview,"");
			 
			
			boolean roleByPriv=isRoleByPriv(String.valueOf(taskid),String.valueOf(this.ins_id));
			if(roleByPriv)
			{
				String content=task_vo.getString("content");
				if(content!=null&&content.length()>0)//liuyz bug31611 如果content有内容则加换行，没内容直接输出审批意见否则查审批意见会多出空行
				{
					content=content+"\n\n"+userview.getUserFullName()+": "+actor.getContent();
				}
				else
				{
					content=userview.getUserFullName()+": "+actor.getContent();
				}
				task_vo.setString("content",content);//
			}
			else
			{
				task_vo.setString("content",actor.getContent());
			}
			String sender=null;//实际处理人员编号
			if(userview.getStatus()!=0)//自助用户
            {
                sender=userview.getDbname()+userview.getA0100();
            } else {
                sender=userview.getUserId();
            }
			//if(sender==null||sender.equals(""))
			String fullsender=userview.getUserFullName();
			if(fullsender==null|| "".equalsIgnoreCase(fullsender)) {
                fullsender=userview.getUserId();
            }
			task_vo.setString("a0100",sender/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
			task_vo.setString("a0101",fullsender/*userview.getUserFullName()*/);//人员姓名 实际处理人员姓名
		//	if(!isHaveObjTheTask(taskid))//如果当前任务有正在待报对象，则不能结束
			{
				task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
				task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
			} 
			
			dao.updateValueObject(task_vo);//更新前面的任务数据
			
			ArrayList seqlist=new ArrayList();//存储未处理的任务（？节点）的序列号
			
			boolean isFirstReject=true;//本节点不是驳回（只有不是驳回的时候，当从202节点驳回到201节点的时候，新增t_wf_task_objlink数据的时候count的值才增1）
			if("07".equals(task_vo.getString("state"))) {
                isFirstReject=false;
            }
			String sql="select * from  t_wf_task_objlink where task_id="+taskid+" and ( ("+Sql_switcher.isnull("special_node","0")+"=0  and submitflag=1   and  "+Sql_switcher.isnull("state","0")+"=0  ) ";
			sql+=" or ("+Sql_switcher.isnull("special_node","0")+"=1 and ( "+Sql_switcher.isnull("state","0")+"=0 or "+Sql_switcher.isnull("state","0")+"=1 ) ))";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				seqlist.add(rowSet.getString("seqnum"));
			}
			sql="update t_wf_task_objlink set state=2 where task_id="+taskid+"   and ( ("+Sql_switcher.isnull("special_node","0")+"=0 and  submitflag=1 and  "+Sql_switcher.isnull("state","0")+"=0  ) ";
			sql+=" or ("+Sql_switcher.isnull("special_node","0")+"=1 and ( "+Sql_switcher.isnull("state","0")+"=0 or "+Sql_switcher.isnull("state","0")+"=1 ) ))";
			dao.update(sql);//更新t_wf_task_objlink表中的state。2代表驳回
			dao.update("update t_wf_task_objlink set flag=1,submitflag=1 where  tab_id="+this.tablebo.getTabid()+" and ins_id="+ins_vo.getInt("ins_id")+" and node_id="+task_vo.getInt("node_id"));
			 
			
			if(this.tablebo.isAllow_defFlowSelf()&& "1".equals(task_vo.getString("params"))) //自定义流程里的逐级驳回
			{
				rejectTask_manual(seqlist,ins_vo,actor,task_vo,this.tablebo.getUserview());
			}
			else
			{
				//驳回调用接口更新第三方oa代办状态  更新当前任务
		 		PendingTask imip=new PendingTask();
				String pendingType="业务模板";
				String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(taskid)); 
				//将旧的代办信息置为已阅状态  
				imip.updatePending("T",pendingCode,1,pendingType,userview);
				/**求当前节点实际处理的上一个人工节点，对人工指定方式，上一个节点是其本身*/
				int pri_task_id=0;
				int node_id=task_vo.getInt("node_id");
				WF_Node wf_node=new WF_Node(node_id,this.conn,this.tablebo);
				//按设置的高级条件发送报备信息
				wf_node.setIns_vo(ins_vo);
				wf_node.setOpt("2");  //驳回
		 		wf_node.sendFilingTasks(taskid,wf_node.getExt_param(),wf_node);  //驳回不报备//bug 35083 只有结束节点才给本人发送邮件。
				
		 		//开始创建新任务
				if(this.tablebo.getSp_mode()==0)//自动分派
				{	 
					WorkflowBo workflowBo=new WorkflowBo(this.conn,ins_vo.getInt("tabid"),userview);  
					//向t_wf_task和t_wf_task_objlink表中增加数据
					rejectTaskVo( workflowBo,taskid,ins_vo,dao,isFirstReject,actor,task_vo);
					
					
				}
				else // 人工分派
				{
					/**对应的节点类型,重新创建一个新的任务*/
					
					WF_Actor tempActor=findPreTaskActorByPriID(task_vo);
					if(tempActor!=null)
					{
						actor.setActorid(tempActor.getActorid());
						actor.setActortype(tempActor.getActortype());
						actor.setActorname(tempActor.getActorname());
					}
					else
					{
						WF_Actor tempactor=findPreTaskActor(task_vo); 
						if(tempactor!=null)
						{
							actor.setActorid(tempactor.getActorid());
							actor.setActortype(tempactor.getActortype());
							actor.setActorname(tempactor.getActorname());
						}
					}
					pri_task_id=getPri_task_id(task_vo.getInt("task_id"),dao); 
					wf_node.setTask_vo(task_vo);
					wf_node.setOtherParaMap(this.otherParaMap);
					
					wf_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),taskid,"templet_"+this.tablebo.getTabid(),this.tablebo); 
					wf_node.setSeqnumList(seqlist);
					/**设置任务为驳回状态*/
					RecordVo rej_taskvo=wf_node.reCreateTask(ins_vo,actor,isFirstReject);	
					/**用户列表*/
					//liuyz 驳回修改t_wf_task_objlink中抢单人
					String sql_obj="Select username from t_wf_task_objlink where task_id="+task_vo.getInt("pri_task_id");
					RowSet rowSet_obj = dao.search(sql_obj);
					if(rowSet_obj.next())
					{
						String userName=rowSet_obj.getString("username");
						if(userName!=null&&userName.trim().length()>0)
						{
							String update_sql="update t_wf_task_objlink set locked_time="+Sql_switcher.sqlNow()+",username='"+userName+"' where task_id="+rej_taskvo.getInt("task_id");
							dao.update(update_sql);
						}
					}
					if(actor!=null) {
                        rej_taskvo.setString("task_pri",actor.getEmergency());
                    }
					rej_taskvo.setString("appuser", getOutOfReject(task_vo));
					rej_taskvo.setString("state","07");
				 	rej_taskvo.setInt("pri_task_id", pri_task_id);
					rej_taskvo.setString("task_id_pro",","+rej_taskvo.getInt("task_id")+task_vo.getString("task_id_pro"));
					dao.updateValueObject(rej_taskvo);	
					/**设置审批对象与任务关联及任务标题*/
					this.setTask_vo(rej_taskvo); 
					if(this.getTask_vo()!=null) {
                        updateTaskTopic(this.getTask_vo().getInt("task_id"),true);//更新最新任务（最后一个任务）的标题
                    }
				}
				
			}
			
			if(!task_vo.getString("task_state").equalsIgnoreCase(String.valueOf(NodeType.TASK_STOP))) {
                updateTaskTopic(taskid,true);//更新上一个任务的标题
            }
			PubFunc.closeDbObj(rowSet); 
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**
	 * 逐级驳回任务（自定义流程)，具体为：结束当前任务，创建新的任务
	 * @param ins_vo
	 * @param taskid
	 * @param userview
	 * @return
	 * @throws GeneralException
	 */
	public boolean rejectTask_manual(ArrayList seqlist,RecordVo ins_vo,WF_Actor actor,RecordVo _task_vo,UserView userview)throws GeneralException
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			PendingTask imip=new PendingTask();
			int temptask_id=_task_vo.getInt("task_id");
			ins_vo=dao.findByPrimaryKey(ins_vo);//当前流程
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			/**结束当前任务*/
			
			//首先查出所有待审批任务(必须是审批任务，不能是报备任务。)
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from t_wf_task where ins_id="+ins_vo.getInt("ins_id")+" and task_state=3 and bs_flag='1'");
			rs = dao.search(sb.toString());
			while(rs.next()){
				int taskid = rs.getInt("task_id");
				RecordVo task_vo=new RecordVo("t_wf_task");//当前任务的信息。后面要更新它里面的某几个字段
				task_vo.setInt("task_id",taskid);
				task_vo=dao.findByPrimaryKey(task_vo);
				if(temptask_id==taskid){//当前驳回人
					task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
					task_vo.setString("sp_yj",actor.getSp_yj()); //当前审批人的意见及相关描述
					boolean roleByPriv=isRoleByPriv(String.valueOf(taskid),String.valueOf(this.ins_id));
					String tempcontent = actor.getContent();
					if(roleByPriv)
					{
						String content=task_vo.getString("content");
						if(content!=null&&content.length()>0)//liuyz bug31611 如果content有内容则加换行，没内容直接输出审批意见否则查审批意见会多出空行
						{
							content=content+"\n\n"+userview.getUserFullName()+": "+tempcontent;
						}
						else
						{
							content=userview.getUserFullName()+": "+tempcontent;
						}
						task_vo.setString("content",content);//
					}
					else
					{
						task_vo.setString("content",tempcontent);
					}
					//为a0100,a0101赋值。a01001的值和actorid的值是一样的。
					String sender=rs.getString("actorid");//实际处理人员编号
					String fullsender=rs.getString("actorname");
					task_vo.setString("a0100",sender/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
					task_vo.setString("a0101",fullsender/*userview.getUserFullName()*/);//人员姓名 实际处理人员姓名
				}else{//系统驳回人
					task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
					task_vo.setString("state", "07");//驳回状态
				}
				task_vo.setDate("end_date",new Date());
				dao.updateValueObject(task_vo);//更新前面的任务数据
				//更新t_wf_task_objlink表
				String sql="update t_wf_task_objlink set state=2,flag=1,submitflag=1 where task_id="+taskid;
				dao.update(sql);//state=2  驳回  
				updateTaskTopic(taskid,true);//更新上一个任务的标题
				 //驳回调用接口更新第三方oa代办状态
				String pendingType="业务模板";
				String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(taskid)); 
				//将旧的代办信息置为已阅状态  
				imip.updatePending("T",pendingCode,1,pendingType,userview);
				
			} //遍历所有的待审任务 结束   结束任务 结束
			
			//开始创建新任务
			 
			WF_Node wf_node=new WF_Node(this.conn);
			wf_node.setOpt("2");  //驳回
			wf_node.setTablebo(this.tablebo);
			wf_node.setTabname("templet_"+this.tablebo.getTabid());
			wf_node.setTabid(String.valueOf(this.tablebo.getTabid()));
			wf_node.setObjs_sql(this.objs_sql);
			if(seqlist!=null&&seqlist.size()>0) {
                wf_node.setSeqnumList(seqlist);
            }
			wf_node.rejectTask_manul(seqlist,_task_vo,ins_vo,actor);
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return bflag;
	}
	
	
	
	
	
	/**
	 * 驳回任务（驳回到发起人），具体为：结束当前任务，创建新的任务  郭峰
	 * @param ins_vo
	 * @param taskid
	 * @param userview
	 * @return
	 * @throws GeneralException
	 */
	public boolean rejectTaskToSponsor(RecordVo ins_vo,WF_Actor actor,int temptask_id,UserView userview)throws GeneralException
	{
		//驳回到发起人修改考勤状态。
		String tabid=String.valueOf(tablebo.getTable_vo().getInt("tabid"));
		String strsql ="select * from templet_"+tabid+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
   	    String tablename="templet_"+tabid;
        
   	    insertKqApplyTable(strsql.toString(),tabid,"","07",tablename); //往考勤申请单中写入报批记录
        TemplateInterceptorAdapter.preHandle("templet_"+this.tablebo.getTabid(),this.tablebo.getTabid(),temptask_id, null, "reject", userview,"");
		 
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			ins_vo=dao.findByPrimaryKey(ins_vo);//当前流程
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			/**结束当前任务*/
			
			//首先查出所有待审批任务(必须是审批任务，不能是报备任务。)
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from t_wf_task where ins_id="+ins_vo.getInt("ins_id")+" and task_state=3 and bs_flag='1'");
			rs = dao.search(sb.toString());
			while(rs.next()){
				int taskid = rs.getInt("task_id");
				RecordVo task_vo=new RecordVo("t_wf_task");//当前任务的信息。后面要更新它里面的某几个字段
				task_vo.setInt("task_id",taskid);
				task_vo=dao.findByPrimaryKey(task_vo);
				if(temptask_id==taskid){//当前驳回人
					task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
					task_vo.setString("sp_yj",actor.getSp_yj()); //当前审批人的意见及相关描述
					boolean roleByPriv=isRoleByPriv(String.valueOf(taskid),String.valueOf(this.ins_id));
					String tempcontent = actor.getContent();
					if(roleByPriv)
					{
						String content=task_vo.getString("content");
						if(content!=null&&content.length()>0)//liuyz bug31611 如果content有内容则加换行，没内容直接输出审批意见否则查审批意见会多出空行
						{
							content=content+"\n\n"+userview.getUserFullName()+": "+tempcontent;
						}
						else
						{
							content=userview.getUserFullName()+": "+tempcontent;
						}
						task_vo.setString("content",content);//
					}
					else
					{
						task_vo.setString("content",tempcontent);
					}
					//为a0100,a0101赋值。a01001的值和actorid的值是一样的。
					String sender=rs.getString("actorid");//实际处理人员编号
					String fullsender=rs.getString("actorname");
					task_vo.setString("a0100",sender/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
					task_vo.setString("a0101",fullsender/*userview.getUserFullName()*/);//人员姓名 实际处理人员姓名
				}else{//系统驳回人
					task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
					task_vo.setString("state", "07");//驳回状态 
				}
				task_vo.setDate("end_date",new Date());
				dao.updateValueObject(task_vo);//更新前面的任务数据
				//更新t_wf_task_objlink表
				String sql="update t_wf_task_objlink set state=2,flag=1,submitflag=1 where task_id="+taskid+" and (  state<>3 or state is null )";
				dao.update(sql);//state=2  驳回
		 		
				/**求当前节点实际处理的上一个人工节点，对人工指定方式，上一个节点是其本身*/
//				int node_id=task_vo.getInt("node_id");
//				WF_Node wf_node=new WF_Node(node_id,this.conn,this.tablebo);
//				//按设置的高级条件发送报备信息
//				wf_node.setIns_vo(ins_vo);
//				wf_node.setOpt("2");  //驳回
//		 		wf_node.sendFilingTasks(taskid,wf_node.getExt_param());  //驳回不报备
				
				updateTaskTopic(taskid,true);//更新上一个任务的标题
				//驳回调用接口更新第三方oa代办状态  更新当前任务
				PendingTask imip=new PendingTask();
				String pendingType="业务模板";
				String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(taskid)); 
				//将旧的代办信息置为已阅状态  
				imip.updatePending("T",pendingCode,1,pendingType,userview);
			} //遍历所有的待审任务 结束   结束任务 结束
			
			//开始创建新任务
			WorkflowBo workflowBo=new WorkflowBo(this.conn,ins_vo.getInt("tabid"),userview);
			RecordVo tempvo = new RecordVo("t_wf_task");
			tempvo.setInt("task_id", temptask_id);
			tempvo=dao.findByPrimaryKey(tempvo);
			rejectTaskVoToSponsor(workflowBo,temptask_id,ins_vo,dao,actor,tempvo);//向t_wf_task和t_wf_task_objlink表中增加数据
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return bflag;
	}
	
	/**
	 * 获取上一节点单据数据的处理人信息
	 * @param dao
	 * @param seqList
	 * @param nodeid
	 * @param tabid
	 * @param task_id
	 * @author dengcan
	 * @return
	 */
	private ArrayList getPreSeqUserList(ContentDAO dao,ArrayList seqList,String nodeid,int tabid,int task_id)
	{ 
		try
		{
			String sql="select seqnum,username from  t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and node_id=? and tab_id=? and task_id=?";
			ArrayList paramList=new ArrayList();
			paramList.add(new Integer(nodeid));
			paramList.add(new Integer(tabid));
			paramList.add(new Integer(task_id));
			RowSet rowSet=dao.search(sql,paramList);
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				if(rowSet.getString("username")!=null&&rowSet.getString("username").trim().length()>0) {
                    map.put(rowSet.getString("seqnum"),rowSet.getString("username"));
                }
			}
			for(int i=0;i<seqList.size();i++)
			{
				String s=(String)seqList.get(i);
				if(map.get(s)!=null) {
                    seqList.set(i,s+":"+(String)map.get(s));
                }
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return seqList;
	}
	
	/**向t_wf_task和t_wf_task_objlink表中增加数据*/
	private void rejectTaskVo(WorkflowBo workflowBo,int taskid,RecordVo ins_vo,ContentDAO dao,boolean isFirstReject,WF_Actor wf_actor,RecordVo _task_vo)
	{
		try
		{
			String sql="";
			WF_Node pre_wf_node=workflowBo.getPreWF_Node(taskid);
			HashMap preNodesMap=workflowBo.getPreNode(taskid,ins_vo.getInt("ins_id"),pre_wf_node);//当前节点的上一个节点。因为要驳回到上一个节点
			Set keySet=preNodesMap.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{ 
				//将暂停状态的任务改成结束状态
				workflowBo.taskState_stopToEnd(ins_vo.getInt("ins_id"),this.tablebo.getTabid(),pre_wf_node.getNode_id(),2);
				
				String nodeid=(String)t.next();
				ArrayList _seqlist=(ArrayList)preNodesMap.get(nodeid); 
				WF_Node _wfnode=new WF_Node(Integer.parseInt(nodeid),this.conn);
				
				 
				
				if(_wfnode.getNodetype()==1){//如果是开始节点（begin节点）
					sql="select * from t_wf_instance where ins_id="+ins_vo.getInt("ins_id");
					//逐级驳回 驳回到发起人修改考勤状态。
					String tabid=String.valueOf(tablebo.getTable_vo().getInt("tabid"));
					String strsql ="select * from templet_"+tabid+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
					String tablename="templet_"+tabid;
					insertKqApplyTable(strsql,tabid,"","07",tablename); //往考勤申请单中写入报批记录
				}else{
					sql="select * from t_wf_task where task_id=(select max(task_id) from t_wf_task_objlink where "+Sql_switcher.isnull("task_type","'1'")+"='1' and  ins_id='"+ins_vo.getInt("ins_id")+"'  and node_id="+nodeid+" and  tab_id="+ins_vo.getInt("tabid")+")";
					//驳回给非发起人也调用考勤接口修改审批人信息
					String tabid=String.valueOf(tablebo.getTable_vo().getInt("tabid"));
					String strsql ="select * from templet_"+tabid+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
	            	KqAppInterface kqapp=new KqAppInterface(this.conn,tablebo.getUserview());
	            	kqapp.synApproverRejectKqApp(this.tablebo.getTabid()+"", strsql);
				}
				RowSet rowSet=dao.search(sql);
				//得到的是上一个节点的一些信息
				if(rowSet.next())
				{
					
					if(_wfnode.getNodetype()!=1&& "2".equals(rowSet.getString("Actor_type"))) //20141220 dengcan 执行退回操作，如果上一流程节点是角色，需直接退回到报批人
                    {
                        _seqlist=getPreSeqUserList(dao,_seqlist,nodeid,ins_vo.getInt("tabid"),rowSet.getInt("task_id"));
                    }
					
					String actorid=rowSet.getString("actorid");
					String actor_type=rowSet.getString("actor_type");
					String bs_flag="1";//报送方式。=1：报批任务  =2：抄送任务
					if(_wfnode.getNodetype()!=1) {
                        bs_flag=rowSet.getString("bs_flag")!=null?rowSet.getString("bs_flag"):"1";
                    }
					 
					
					String actorname=rowSet.getString("actorname");
					WF_Actor _actor=new WF_Actor();//生成新的流程参与者的对象。流程参与者的actorid是上一个任务的actorid。
					_actor.setActorid(actorid);
					_actor.setActorname(actorname);
					_actor.setActortype(actor_type);
					_actor.setContent(wf_actor.getContent()); //将驳回意见添加到发送邮件中 liuzy 20150126
					_actor.setSpecialRoleUserList(nodeid+":"+actorid+"`"+actorname);
						
					WF_Node wf_node=new WF_Node(Integer.parseInt(nodeid),this.conn,this.tablebo);//初始化里面的一些全局变量，然后调用该类的reCreateTask()方法
						 
				//	int pri_task_id=getPri_task_id(_task_vo.getInt("task_id"),dao); 
					wf_node.setTask_vo(_task_vo);//_task_vo是当前节点的，不是上一个节点的
					wf_node.setOtherParaMap(this.otherParaMap);
					wf_node.setOpt("2");  //驳回
					wf_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),taskid,"templet_"+this.tablebo.getTabid(),this.tablebo); 
					wf_node.setSeqnumList(_seqlist);
						/**设置任务为驳回状态*/
					if("4".equals(bs_flag))//空节点
                    {
                        wf_node.setBs_flag("4");
                    }
					RecordVo rej_taskvo=wf_node.reCreateTask(ins_vo,_actor,isFirstReject);	
					wf_node.setBs_flag("1");	
					/**用户列表*/
					if(wf_actor!=null) {
                        rej_taskvo.setString("task_pri",wf_actor.getEmergency());
                    }
					rej_taskvo.setString("appuser", getOutOfReject(_task_vo));
					rej_taskvo.setString("state","07");//新数据task_vo
					/*
					rej_taskvo.setInt("pri_task_id", pri_task_id);
					rej_taskvo.setString("task_id_pro",","+rej_taskvo.getInt("task_id")+_task_vo.getString("task_id_pro"));
					*/
					dao.updateValueObject(rej_taskvo);//修改		
						/**设置审批对象与任务关联及任务标题*/
					this.setTask_vo(rej_taskvo); 
					if(this.getTask_vo()!=null)//更新最新任务的主题
                    {
                        updateTaskTopic(this.getTask_vo().getInt("task_id"),true);
                    }
					
					if("4".equals(bs_flag))//空节点
					{	
						dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+rej_taskvo.getInt("task_id")+" and tab_id="+this.tablebo.getTabid());
						rejectTask(ins_vo,wf_actor,rej_taskvo.getInt("task_id"),this.tablebo.getUserview()); //,rejectObj))
					}
				    
				}
				
				
				if(rowSet!=null) {
                    PubFunc.closeDbObj(rowSet);
                }
			}
			
			if(keySet.size()==0&&(pre_wf_node.getNodetype()==4||pre_wf_node.getNodetype()==6)) //驳回时不能满足统一驳回条件，将任务状态置为 暂停
			{ 
				_task_vo.setString("task_state",String.valueOf(NodeType.TASK_STOP));
				dao.updateValueObject(_task_vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**向t_wf_task和t_wf_task_objlink表中增加数据   驳回到发起人  郭峰*/
	private void rejectTaskVoToSponsor(WorkflowBo workflowBo,int task_id,RecordVo ins_vo,ContentDAO dao,WF_Actor wf_actor,RecordVo _task_vo)
	{
		try{
			RowSet rs = null;
			//首先获取开始节点的节点号
			StringBuffer sb = new StringBuffer("");
			sb.append("select node_id,nodename from t_wf_node where tabid="+ins_vo.getDouble("tabid")+" and nodetype='1'");//得到开始节点的节点号。插入数据时就在t_wf_task的node_id字段中插入
			int nodeid=0;//得到开始节点的节点号
			rs = dao.search(sb.toString());
			if(rs.next()){
				nodeid = rs.getInt("node_id");
			}
			sb.setLength(0);
			ArrayList _seqlist = new ArrayList();
			sb.append("select t.seqnum from templet_" + ins_vo.getInt("tabid") + " t,t_wf_task_objlink twt where t.seqnum=twt.seqnum and twt.ins_id="
					+ ins_vo.getInt("ins_id") + " and twt.task_id=" + task_id + " and twt.state=2 and twt.submitflag=1");
			rs = dao.search(sb.toString());
			while(rs.next()){
				_seqlist.add(rs.getString("seqnum"));
			}
			sb.setLength(0);
			sb.append("select * from t_wf_instance where ins_id="+ins_vo.getInt("ins_id"));//得到begin节点的信息
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				String actorid=rs.getString("actorid");
				String actor_type=rs.getString("actor_type");
				String actorname=rs.getString("actorname");
				WF_Actor _actor=new WF_Actor();//生成新的流程参与者的对象。流程参与者的actorid是上一个任务的actorid。这里所有的信息都是上一个节点的信息
				_actor.setActorid(actorid);
				_actor.setActorname(actorname);
				_actor.setActortype(actor_type);
				_actor.setSpecialRoleUserList(nodeid+":"+actorid+"`"+actorname);
				_actor.setContent(wf_actor.getContent()); //bug 38466 驳回邮件，驳回原因不显示。
					
				WF_Node wf_node=new WF_Node(nodeid,this.conn,this.tablebo);//初始化里面的一些全局变量，然后调用该类的reCreateTask()方法
				wf_node.setTask_vo(_task_vo);//_task_vo是当前节点的，不是上一个节点的
				wf_node.setOtherParaMap(this.otherParaMap);
				wf_node.setOpt("2");  //驳回
				wf_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_id,"templet_"+this.tablebo.getTabid(),this.tablebo); 
				wf_node.setSeqnumList(_seqlist);//t_wf_task_objlink表中的seqnum
				RecordVo rej_taskvo=wf_node.reCreateTaskToSponsor(ins_vo,_actor);//
				wf_node.setBs_flag("1");	
				if(wf_actor!=null) {
                    rej_taskvo.setString("task_pri",wf_actor.getEmergency());//任务优先级
                }
				/**用户列表*/
				
				if("1".equals(_task_vo.getString("params"))) //自定义审批流
                {
                    rej_taskvo.setString("params","1");
                }
				rej_taskvo.setString("appuser", getOutOfReject(_task_vo));
				rej_taskvo.setString("state","07");//新数据task_vo
				dao.updateValueObject(rej_taskvo);//修改
				/**设置审批对象与任务关联及任务标题*/
				this.setTask_vo(rej_taskvo);
				if(this.getTask_vo()!=null)//更新最新任务的主题
                {
                    updateTaskTopic(this.getTask_vo().getInt("task_id"),true);
                }
				
			}
			if(rs!=null) {
                PubFunc.closeDbObj(rs);
            }
			
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 取得当前任务前一任务的 pri_task_id值
	 * @param task_id
	 * @return
	 */
	public  int getPri_task_id(int task_id,ContentDAO dao)
	{
		int pri_task_id=0;
		try
		{
			RowSet rowSet=dao.search("select pri_task_id from t_wf_task where task_id=(select pri_task_id from t_wf_task where task_id="+task_id+")");
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null) {
                    pri_task_id=rowSet.getInt(1);
                }
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pri_task_id;
	}
	
	/**
	 * 当前审批人是否是单一节点下多审批人中的一个
	 * @param taskid
	 * @param ins_id
	 * @return
	 */
	public boolean isParallel(int taskid,int ins_id)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nm from t_wf_task_datalink where ");
			buf.append("  ins_id="+ins_id+" and task_id="+taskid);
			RowSet rset=dao.search(buf.toString());
			if(rset.next()) {
                if(rset.getInt(1)>0) {
                    flag = true;
                }
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	/** 是否是任务监控里的重新分配 */
	boolean isReAssign=false;
	
	/**
	 * 重新分发任务，根据当前实例、当前任务号
	 * 在当前对应的节点下面重新创建一个新任务节点
	 * @param ins_vo
	 * @param actor
	 * @param taskid
	 * @return
	 * @throws GeneralException
	 */
	public boolean reAssignTask(RecordVo ins_vo,WF_Actor actor,int taskid,UserView userview)throws GeneralException
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer buf=new StringBuffer();
		try
		{
			//当前审批人是否是单一节点下多审批人中的一员
//			if(this.ins_id!=0)
//				isParallel=isParallel(taskid,this.ins_id);
			
			ins_vo=dao.findByPrimaryKey(ins_vo);
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			
			/**结束前面的任务，创建新的任务*/
			RecordVo task_vo=new RecordVo("t_wf_task");
			task_vo.setInt("task_id",taskid);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
            }
			task_vo.setString("sp_yj",actor.getSp_yj());
			task_vo.setString("content",actor.getContent());

			String a0100=userview.getDbname()+userview.getA0100();
			if(a0100==null||a0100.length()==0) {
                a0100=userview.getUserName();
            }
			task_vo.setString("a0100",a0100/*userview.getDbname()+userview.getA0100()*/);//人员编号 实际处理人员编码
			task_vo.setString("a0101",userview.getUserFullName());//人员姓名 实际处理人员姓名
			String appuser=task_vo.getString("appuser")+userview.getUserName()+",";
			 
			task_vo.setString("appuser", appuser);
	//		if(!isHaveObjTheTask(taskid))//如果当前任务有正在待报对象，则不能结束	
			{
				task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
				task_vo.setDate("end_date",new Date());//DateStyle.getSystemTime());					
			}
			
			
            
			/*
			 * 2008-05-08
			buf.append("update ");
			buf.append("templet_"+this.tablebo.getTable_vo().getString("tabid"));
			buf.append(" set submitflag=1 where task_id=");
			buf.append(taskid);
			dao.update(buf.toString());
			*/
			/**对应的节点类型,重新创建一个新的任务*/
			int node_id=task_vo.getInt("node_id");
			WF_Node wf_node=new WF_Node(this.conn);
			if(taskid!=-1&&this.tablebo.getSp_mode()==1&&this.tablebo.isDef_flow_self(taskid)) 
			{
			    if (isReAssign){
			      //重新复制当前的任务，只是把任务处理人 更改为重新分配的人 
	                copyDef_flow_selfTask(taskid,actor); 
	                dao.updateValueObject(task_vo);  
			    }
			    else {
			        wf_node.setTablebo(this.tablebo);
			        wf_node.setTabname("templet_"+this.tablebo.getTabid());
			        wf_node.setTabid(String.valueOf(this.tablebo.getTabid()));
			        wf_node.setObjs_sql(this.objs_sql);
			        wf_node.setOtherParaMap(this.otherParaMap);
			        
			        if(wf_node.isEndNode_level(taskid,this.tablebo))//如果任务是自定义流程里当前层级最后一个处理人，则可创建下一流程节点
			        {
			            wf_node.reCreateTask_manual(ins_vo,actor,task_vo,this.tablebo); 
			        }
			    }
			}
			else
			{ 
				//报批后更新考勤状态为待批  47336  先校验考勤，考勤不通过不创建新任务
	            String _sql="select * from templet_"+this.tablebo.getTabid() +" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
		   	    String tablename="templet_"+this.tablebo.getTabid();
		   	     
		   	    insertKqApplyTable(_sql,String.valueOf(this.tablebo.getTabid()),"","02",tablename); //往考勤申请单中写入报批记录
		   	    TemplateInterceptorAdapter.preHandle("templet_"+this.tablebo.getTabid(),this.tablebo.getTabid(),taskid, null, "appeal",userview,"");
		   	     
		   	    dao.updateValueObject(task_vo); 
				
				wf_node=new WF_Node(node_id,this.conn,this.tablebo);
				if(isReAssign&&this.tablebo.getSp_mode()==0&&wf_node.getNodetype()!=1)	//如果是重新分配,并且是自动流转模板，则节点类型必是活动的
                {
                    wf_node.setNodetype(2);
                }
				//邮件抄送
				wf_node.setEmail_staff_value(email_staff_value);
				wf_node.setIsSendMessage(isSendMessage);
				wf_node.setUser_h_s(user_h_s);
				wf_node.setObjs_sql(this.objs_sql);
				if(this.objs_sql==null||this.objs_sql.trim().length()==0) {
                    wf_node.setObjs_sql(getObjsSql(ins_id,taskid,3,String.valueOf(this.tablebo.getTabid()),this.tablebo.getUserview(),""));
                }
				wf_node.setOtherParaMap(this.otherParaMap);
				wf_node.setOpt("4");  //重新分配 
				wf_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),taskid,"templet_"+this.tablebo.getTabid(),this.tablebo);  
				//wf_node.setWf_actor(actor);
	            wf_node.setIns_vo(ins_vo);
	            //手动流转的发送抄送的报备信息 xcs add @2014-12-25
	            /**bug:52320 中国核工业建设股份有限公司：人事异动流程设置中设置报备--高级条件（如下图），在流程流转中进行了重新分派，导致报备人收到了两条一样的待办*/
	            /**问题原因是重新分配时会向报备人发送一条报备待办，注释掉，重新分配不再走报备信息*/
	            //wf_node.sendFilingTasks(taskid,wf_node.getExt_param(),wf_node); //bug 35083 只有结束节点才给本人发送邮件。
	            
				RowSet rowSet=dao.search("select seqnum from  t_wf_task_objlink where task_id="+taskid+" and (  state<>3 or state is null )   "); //重新分派不应该限制死，人员就丢失了and submitflag=1 
				ArrayList seqnumList=new ArrayList();
				while(rowSet.next()) {
                    seqnumList.add(rowSet.getString("seqnum"));
                }
				wf_node.setSeqnumList(seqnumList);  
				
				RecordVo re_taskvo=wf_node.reCreateTask(ins_vo,actor,false);
				/**用于驳回*/
				re_taskvo.setString("appuser", appuser);
				/**发送人*/
				re_taskvo.setString("a0100_1",task_vo.getString("a0100"));//发送人
				re_taskvo.setString("a0101_1",task_vo.getString("a0101"));//发送人姓名
				re_taskvo.setInt("pri_task_id", task_vo.getInt("task_id"));
				re_taskvo.setString("task_id_pro",","+re_taskvo.getInt("task_id")+task_vo.getString("task_id_pro"));
				dao.updateValueObject(re_taskvo);
				/**设置审批对象与任务关联及任务标题*/
				this.setTask_vo(re_taskvo); 
			//	setTempletNextTaskLink(taskid);
				if(this.getTask_vo()!=null)
				{
					updateTaskTopic(this.getTask_vo().getInt("task_id"),true);				
				} 
				PubFunc.closeDbObj(rowSet); 
				TemplateInterceptorAdapter.afterHandle(taskid,0,tablebo.getTabid(),null,"appeal",userview);
			}
			
			dao.update("update t_wf_task_objlink set state=1 where tab_id="+this.tablebo.getTabid()+" and task_id="+taskid+" and submitflag=1"); 
			String topic=this.tablebo.getRecordBusiTopic(taskid,ins_vo.getInt("ins_id"));
			task_vo.setString("task_topic",topic);
			dao.updateValueObject(task_vo);
			//重新分配后 ，将OA系统原单据更改为已办状态 wangrd 2015-08-31
			if (this.isReAssign){
				PendingTask imip=new PendingTask();
				String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(taskid)); 
				String pendingType="业务模板"; 
				imip.updatePending("T",pendingCode,1,pendingType,userview);
			}

		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
    /**   
     * @Title: copyDef_flow_selfTask   
     * @Description: 重新分配自定义任务时，复制一份新任务。  
     * @param @param srcTaskvo
     * @param @param wf_actor
     * @param @return
     * @param @throws GeneralException 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    private boolean copyDef_flow_selfTask(int srcTask_id,WF_Actor wf_actor)throws GeneralException
    {
        boolean b=false;
        try {
             
                ContentDAO dao = new ContentDAO(this.conn);                
                RecordVo srcTaskvo=new RecordVo("t_wf_task");
                srcTaskvo.setInt("task_id",srcTask_id);
                srcTaskvo=dao.findByPrimaryKey(srcTaskvo);
                
                //复制任务
                 RecordVo taskvo = new RecordVo("t_wf_task");
                IDGenerator idg = new IDGenerator(2, this.conn);
                int task_id=Integer.parseInt(idg.getId("wf_task.task_id"));
                taskvo.setInt("task_id", task_id);
                taskvo.setString("task_topic", srcTaskvo.getString("task_topic"));
                taskvo.setInt("node_id", srcTaskvo.getInt("node_id"));
                taskvo.setInt("ins_id", srcTaskvo.getInt("ins_id"));
                taskvo.setDate("start_date", new Date()); //DateStyle.getSystemTime());
                taskvo.setString("task_type", srcTaskvo.getString("task_type"));
                taskvo.setString("bs_flag", srcTaskvo.getString("bs_flag")); //=1 or =null，报批任务（任务参与者可以审批，提交）  =2，加签任务(任务参与者，可以审批，提交)  =3 报备任务 =4 空任务，自动流转
                taskvo.setString("task_pri", srcTaskvo.getString("task_pri"));//任务优先级
                taskvo.setString("task_type", srcTaskvo.getString("task_type"));
                 //上一任务复制源任务      
                taskvo.setInt("pri_task_id", srcTask_id); 
                String task_id_pro=srcTaskvo.getString("task_id_pro");
               // task_id_pro=task_id_pro.replace(","+srcTaskvo.getString("task_id")+",", ","+taskvo.getString("task_id")+",");
               // taskvo.setString("task_id_pro", task_id_pro);
                taskvo.setString("task_id_pro",","+task_id+ task_id_pro);
                
                taskvo.setString("a0100", srcTaskvo.getString("a0100"));
                taskvo.setString("a0101",  srcTaskvo.getString("a0101"));
                taskvo.setString("a0100_1",  srcTaskvo.getString("a0100_1"));
                taskvo.setString("a0101_1",  srcTaskvo.getString("a0101_1"));
                taskvo.setString("appuser",  srcTaskvo.getString("appuser"));
                taskvo.setString("state",  srcTaskvo.getString("state"));
                taskvo.setString("params",  srcTaskvo.getString("params"));
                
                    //添加当前审批人信息
                taskvo.setString("actorid", wf_actor.getActorid());
                taskvo.setString("actor_type", wf_actor.getActortype());
                taskvo.setString("actorname", wf_actor.getActorname());
                taskvo.setString("task_state",String.valueOf(NodeType.TASK_WAINTING));
                dao.addValueObject(taskvo);
                //复制taskobjlink
                String sql="insert into t_wf_task_objlink (seqnum,ins_id,task_id,node_id,tab_id,task_type,submitflag) "
                    +"select seqnum,ins_id,"+taskvo.getString("task_id")
                    +",node_id,tab_id,task_type,submitflag from t_wf_task_objlink "
                    +"where task_id="+srcTaskvo.getString("task_id");
                dao.update(sql);
                b=true;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }
	
	/**
	 * 确认，结束当前任务以及实例,把实例设置完成标志，把当前任务
	 * 设置完成标志，创建结束类型的节点
	 * @param ins_vo
	 * @param actor
	 * @param taskid
	 * @param userview
	 * @param state 任务结束方式，正常结束=4，还是非正常结=5
	 * @return
	 * @throws GeneralException
	 */
	public boolean finishTask(RecordVo ins_vo,WF_Actor actor,int taskid,UserView userview,String state)throws GeneralException
	{
		boolean bflag=true;
		ContentDAO dao=new ContentDAO(this.conn);		
		try
		{
			ins_vo=dao.findByPrimaryKey(ins_vo);
			if(ins_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }
			
			/**结束前面的任务，创建新的任务*/
			RecordVo task_vo=new RecordVo("t_wf_task");
			task_vo.setInt("task_id",taskid);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo==null) {
                throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
            }
			
//			当前审批人是否是单一节点下多审批人中的一员
//			if(this.ins_id!=0)
//				isParallel=isParallel(taskid,this.ins_id);
			
			WF_Node wf_node=new WF_Node(this.tablebo,this.conn);
			if(!"0".equals(isSendMessage))
			{ 
				//邮件抄送
				wf_node.setEmail_staff_value(email_staff_value);
				wf_node.setIsSendMessage(isSendMessage);
				wf_node.setUser_h_s(user_h_s);
				wf_node.setObjs_sql(this.objs_sql);
				wf_node.sendMessage(null,0,0);
			}
			
			String pre_pendingID="";
			if(this.otherParaMap.get("pre_pendingID")!=null) {
                pre_pendingID=(String)this.otherParaMap.get("pre_pendingID");
            }
			if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
			{
				MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
				sysBo.copeYpTask(pre_pendingID);
			}
		
			
			
			task_vo.setString("sp_yj",actor.getSp_yj());
			task_vo.setString("content",actor.getContent());
			task_vo.setString("a0100",userview.getDbname()+userview.getA0100());//人员编号 实际处理人员编码
			task_vo.setString("a0101",userview.getUserFullName());//人员姓名 实际处理人员姓名
			/**结束前面的任务，创建新的任务*/
			boolean bend=false;
	//		if(!isHaveObjTheTask(taskid))//如果当前任务有正在待报对象，则不能结束
			{
				task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));            
				task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());	
				bend=true;
			}
			
			wf_node.setIns_vo(ins_vo);
			wf_node.setNode_id(task_vo.getInt("node_id"));
			wf_node.sendFilingTasks_manual(task_vo); //自定义流程报备操作
			dao.updateValueObject(task_vo);
			
			
			WF_Node end_node=getEndNode();
			if(end_node==null||end_node.getNodetype()==0) {
                throw new GeneralException(ResourceFactory.getProperty("error.end.notdefine"));
            }
			RowSet rowSet=dao.search("select seqnum from  t_wf_task_objlink where task_id="+taskid+"  and submitflag=1  and (  state=0 or  state is null )  "); 
			ArrayList seqnumList=new ArrayList();
			while(rowSet.next()) {
                seqnumList.add(rowSet.getString("seqnum"));
            }
			end_node.setSeqnumList(seqnumList);  
			dao.update("update t_wf_task_objlink set state=1 where tab_id="+this.tablebo.getTabid()+" and task_id="+taskid+" and submitflag=1 and state<>3");
			
			end_node.setOtherParaMap(this.otherParaMap);
			end_node.setOpt("3");  //批准
			/**创建结束任务节点*/
			actor.setActorid(task_vo.getString("a0100"));
			actor.setActorname(task_vo.getString("a0101"));
			actor.setActortype("1");
			//end_node.setWf_actor(actor); //设置审批对象及其内容,支持任意流转
			end_node.setTask_vo(task_vo);
			end_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),taskid,"templet_"+this.tablebo.getTabid(),this.tablebo);  
			end_node.createTask(ins_vo, actor,null);
			StringBuffer buf=new StringBuffer();
			if(!bend)
			{
				buf.setLength(0);
				buf.append("update t_wf_task set actorname='");
				buf.append(task_vo.getString("actorname"));
				buf.append("',actorid='");
				buf.append(task_vo.getString("actorid"));
				buf.append("',actor_type='");
				buf.append(task_vo.getString("actor_type"));
				buf.append("',A0100='");
				buf.append(userview.getUserId());
				buf.append("',A0101='");
				buf.append(userview.getUserFullName());
				buf.append("',A0100_1='");
				buf.append(task_vo.getString("a0100_1"));
				buf.append("',A0101_1='");
				buf.append(task_vo.getString("a0101_1"));
				buf.append("',bread=1,flag=1,task_type=2 where task_id=");
				buf.append(end_node.getTask_vo().getInt("task_id"));
				dao.update(buf.toString());
				
				
				
		  
			}		
			
			//插入每个任务处理的人员记录
//			String sql=getObjsSql(ins_id,taskid,3,String.valueOf(this.tablebo.getTabid()),this.tablebo.getUserview());
//			tablebo.insertTaskRecords2(sql,end_node.getTask_vo().getInt("task_id"));
			
			
			this.setTask_vo(end_node.getTask_vo());	
//			setTempletNextTaskLink(taskid);
			if(this.getTask_vo()!=null) {
                updateTaskTopic(this.getTask_vo().getInt("task_id"),false);
            }
		//	if(!bend)
				updateTaskTopic(taskid,true);
			/**结束流程实例*/
			ins_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());
			ins_vo.setString("finished",state); //结束...	
			dao.updateValueObject(ins_vo);
			
			
			PubFunc.closeDbObj(rowSet);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**
	 * 设置考勤申请记录的默认值 A0100，nbase 等
	 * @param bean
	 * @param rowSet 
	 */
	private LazyDynaBean defaultSetInfo(LazyDynaBean bean,String a0100,String nbase )
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			RecordVo vo=new RecordVo(nbase.toLowerCase()+"A01");
			vo.setString("a0100",a0100);
			vo=dao.findByPrimaryKey(vo);
			bean.set("a0100",a0100);
			bean.set("nbase",nbase);
			bean.set("e0122",vo.getString("e0122"));
			bean.set("e01a1",vo.getString("e01a1"));
			bean.set("b0110",vo.getString("b0110"));
			bean.set("a0101",vo.getString("a0101"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * 根据模板ID获得考勤表的对应信息
	 * @param tabid
	 * @return
	 */
	public String getKqMappingInfo(String tabid)throws GeneralException
	{
        if (tableParamBo==null){
            tableParamBo=new TemplateTableParamBo(Integer.parseInt(tabid),this.conn);
        }
        String mapping=tableParamBo.getKq_field_mapping(); 
        String kqTab=tableParamBo.getKq_setid();
		if(mapping==null) {
            mapping="";
        }
		if(kqTab==null) {
            kqTab="";
        }
		return kqTab+"~"+mapping;
	}
	
	
	/**
	 * 判断审批人是否是发起人 
	 * @param task_id
	 * @return
	 */
	public boolean isStartNode(String task_id)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		String sql="";
		RowSet rowSet=null;
		boolean flag=false;
		try
		{
			if(this.tablebo!=null)
			{
				if(this.tablebo.isBsp_flag())
				{
					if(this.tablebo.getSp_mode()==0) //审批模式=0自动流转，=1手工指派
					{
						sql="select nodetype from t_wf_node where node_id=(select node_id  from t_wf_task where  task_id="+task_id+" )";
						rowSet=dao.search(sql);
						if(rowSet.next())
						{
							if("1".equals(rowSet.getString(1))) {
                                flag=true;
                            }
						}
					}
					else if(this.tablebo.getSp_mode()==1)
					{
						sql="select actor_type,actorid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";
						rowSet=dao.search(sql);
						if(rowSet.next())
						{
							String actor_type=rowSet.getString("actor_type");
							String actorid=rowSet.getString("actorid");
							if("4".equals(actor_type)&&actorid.trim().equalsIgnoreCase(this.tablebo.getUserview().getUserName())) //业务用户
							{
								flag=true;
							}
							else if("1".equals(actor_type)&&actorid.trim().equalsIgnoreCase(this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100())) //自助用户
							{
								flag=true;
							}
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception ee)
			{
				
			}
		}
		return flag;
	}
	
	/**
	 * 通过指标id取销假（撤销考勤申请）指标定义
	 * 撤销模板为通用模板，可撤请假、加班、公出，因此指标是虚拟指标，非业务字典中指标
	 * @param itemId 指标id
	 * @return FieldItem
	 */
    private FieldItem getCancelKqApplyFieldItem(String itemId) {
    	if(null == itemId || "".equals(itemId.trim())) {
            return null;
        }
    	
    	FieldItem item = new FieldItem();
    	item.setItemid(itemId);
    	
    	if("QXJ01".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("销假单号");
    		item.setItemlength(10);
    		item.setItemtype("A");    		
    	} else if("QXJ05".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("申请日期");
    		item.setItemlength(18);
    		item.setItemtype("D");    		
    	} else if("QXJZ1".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("起始时间");
    		item.setItemlength(16);
    		item.setItemtype("D");    		
    	} else if("QXJZ3".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("终止时间");
    		item.setItemlength(16);
    		item.setItemtype("D");    		
    	} else if("QXJ07".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("销假事由");
    		item.setItemlength(250);
    		item.setItemtype("A");    		
    	} else if("QXJ01_O".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("被销假单据序号");
    		item.setItemlength(10);
    		item.setItemtype("A");    		
    	} else if("QXJ03_O".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("被销假单据类型");
    		item.setItemlength(30);
    		item.setItemtype("A");    		
    	} else if("QXJZ1_O".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("被销假单据起始时间");
    		item.setItemlength(16);
    		item.setItemtype("D");    		
    	} else if("QXJZ3_O".equalsIgnoreCase(itemId)) {
    		item.setItemdesc("被销假单据终止时间");
    		item.setItemlength(16);
    		item.setItemtype("D");    		
    	} else {
    		return null;
    	}
    	
    	return item;
    }
	
	/**
	 * 模板在报批时通过配置的申请单与模板映射关系在考勤对应的申请表中新建一条申请单记录，状态为“已报批”，同时需将新建的申请单记录的单据序号写回模板记录对应的指标中。
	 * @param sql
	 * @param tabid
	 * @param operState 申请记录操作状态（02：报批，03：批准 07：驳回）
	 */
	public void insertKqApplyTable(String sql,String tabid,String selfapply,String operState,String tablename) throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String seqnum_id="";
			String seqnum_value=""; 
			String info=getKqMappingInfo(tabid);
			if(info.length()<=1) {
                return;
            }
			seqnum_id=info.split("~")[0]+"01";
			String mapping=info.split("~")[1];
			 
			if(mapping!=null&&mapping.length()>0)
			{
				RecordVo vo=new RecordVo(tablename.toLowerCase());
				KqAppInterface kqAppInterface=new KqAppInterface(this.conn,this.tablebo.getUserview());
				mapping=mapping.toLowerCase();
				String[] temps=mapping.split(","); 
				rowSet=dao.search(sql);
				LazyDynaBean abean=new LazyDynaBean();
				FieldItem item=null;
				ArrayList<LazyDynaBean> kqBeanList  = new ArrayList<LazyDynaBean>();
				ArrayList seqnumList = new ArrayList();
				while(rowSet.next())
				{
						abean=new LazyDynaBean();
						String a0100=rowSet.getString("a0100");
						String basepre=rowSet.getString("basepre");
						abean=defaultSetInfo(abean,a0100,basepre);
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String[] temp=temps[i].toLowerCase().split(":");
								item=DataDictionary.getFieldItem(temp[0]);
								
								//如果不是字典中的指标，那么可能是销假业务特殊指标
								if(null == item) {
									item = getCancelKqApplyFieldItem(temp[0]);
								}
								
								if(!vo.hasAttribute(temp[1])) {
                                    throw new GeneralException("考勤业务单与模板指标对应关系定义错误!");
                                }
								if(item!=null)
								{
									if("D".equalsIgnoreCase(item.getItemtype()))
									{
										if(rowSet.getDate(temp[1])!=null)
										{
											if(Sql_switcher.searchDbServer()==2)
											{
												Timestamp ta=rowSet.getTimestamp(temp[1]);
												Date d=new Date(ta.getTime());
												abean.set(temp[0],d);
											}
											else {
                                                abean.set(temp[0],rowSet.getDate(temp[1]));
                                            }
										}
									//	else
									//		abean.set(temp[0],null);
									}
									else if("A".equalsIgnoreCase(item.getItemtype()))
									{
										if(rowSet.getString(temp[1])!=null)
										{
											abean.set(temp[0],rowSet.getString(temp[1]));
											if(temp[0].equalsIgnoreCase(seqnum_id)) {
                                                seqnum_value=rowSet.getString(temp[1]);
                                            }
										}
									//	else
									//		abean.set(temp[0],null);
									}
									else if("N".equalsIgnoreCase(item.getItemtype()))
									{
										if(item.getDecimalwidth()==0)
										{
											if(rowSet.getString(temp[1])!=null&&rowSet.getString(temp[1]).length()>0)// 20140728  dengcan，解决""情况
											{ 
												String _itemid=temp[1].substring(0,temp[1].length()-2);
												// 20140814  dengcan，解决考勤是数值型，人事异动是字符型问题
												if(DataDictionary.getFieldItem(_itemid.toLowerCase())!=null&& "A".equalsIgnoreCase(DataDictionary.getFieldItem(_itemid.toLowerCase()).getItemtype())) {
                                                    abean.set(temp[0],new Integer(rowSet.getString(temp[1]).trim()));
                                                } else {
                                                    abean.set(temp[0],new Integer(rowSet.getInt(temp[1])));
                                                }
											}
										//	else
										//		abean.set(temp[0],null);
										}
										else
										{
											if(rowSet.getString(temp[1])!=null&&rowSet.getString(temp[1]).length()>0)// 20140728  dengcan，解决""情况
											{
												String _itemid=temp[1].substring(0,temp[1].length()-2);
												// 20140814  dengcan，解决考勤是数值型，人事异动是字符型问题
												if(DataDictionary.getFieldItem(_itemid.toLowerCase())!=null&& "A".equalsIgnoreCase(DataDictionary.getFieldItem(_itemid.toLowerCase()).getItemtype())) {
                                                    abean.set(temp[0],new Float(rowSet.getString(temp[1]).trim()));
                                                } else {
                                                    abean.set(temp[0],new Float(rowSet.getFloat(temp[1])));
                                                }
											}else if(rowSet.getString(temp[1])!=null&&rowSet.getString(temp[1]).length()==0){//bug 37464 清空参考班次报批，入库仍显示。
												if("Q1104".equalsIgnoreCase(temp[0])) {
                                                    abean.set(temp[0],"0");
                                                }
											}
										//	else
										//		abean.set(temp[0],null);
										}
									}
									else if("M".equalsIgnoreCase(item.getItemtype()))
									{
										abean.set(temp[0],Sql_switcher.readMemo(rowSet,temp[1])); 
									}
									
								}
							}
						}
						
						abean.set("seqnum_value", seqnum_value);
						kqBeanList.add(abean);
					}
					try
					{
						ArrayList errorMsgList = kqAppInterface.syncAppInfoToKqTab(tabid,kqBeanList,operState);
						if(errorMsgList.size()>0) {
							StringBuffer errorMsg = new StringBuffer("");
							String between = "\n";
							if(PubFunc.isUseNewPrograme(this.tablebo.getUserview())) {
                                between = "<br />";
                            }
							for(int i=0;i<errorMsgList.size();i++) {
								if(i==0) {
                                    errorMsg.append(errorMsgList.get(i));
                                } else {
                                    errorMsg.append(between+errorMsgList.get(i));
                                }
							}
							if(errorMsg.length()>0) {
                                throw new GeneralException(errorMsg.toString());
                            }
						}else {
							// 36369 若是在考勤处理过程中生成的单号，需单独处理到人事异动流程中
							for(int j=0;j<kqBeanList.size();j++){
								abean = kqBeanList.get(j);
								seqnum_value=(String) abean.get("seqnum_value");
								if((seqnum_value==null||seqnum_value.trim().length()==0)&&!"07".equals(operState))
								{
									
									if(abean.get(seqnum_id)!=null&&((String)abean.get(seqnum_id)).trim().length()>0)
									{
										seqnum_value=(String)abean.get(seqnum_id);
										String a0100=(String) abean.get("a0100");
										String basepre=(String) abean.get("nbase");
										if(seqnum_value!=null&&seqnum_value.trim().length()>0)
										{
											for(int i=0;i<temps.length;i++)
											{
												if(temps[i].trim().length()>0)
												{
													String[] temp=temps[i].toLowerCase().split(":");
													if(temp[0].equalsIgnoreCase(seqnum_id))
													{
														dao.update("update "+tablename+" set "+temp[1]+"='"+seqnum_value+"' where a0100='"+a0100+"' and lower(basepre)='"+basepre.toLowerCase()+"'");
														break;
													}
												}
											}
										}
									}
								}
							}
						}
					}
					catch(Exception ex)
					{
						throw GeneralExceptionHandler.Handle(ex); 
					}
				}
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}finally {
				PubFunc.closeDbObj(rowSet);
			}
	}
	
	/**
	 * 将考勤选中人员的加班信息导入加班模板中并自动执行报批操作。
	 * @param tabid
	 * @param 
	 */
	public void syncAppInfoToTemplateTab(String tabid,ArrayList infoList) throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		CallableStatement cstmt=null;
		RowSet rowSet = null;
		try
		{
			if(infoList==null||infoList.size()==0) {
                return;
            }
			String seqnum_id="";
			String seqnum_value=""; 
			String info=getKqMappingInfo(tabid);
			if(info.length()<=1) {
                throw new GeneralException("加班业务模板参数设置不完整!");
            }
			String tableName=this.tablebo.getUserview().getUserName()+"templet_"+this.tablebo.getTabid();
			DbWizard dbwizard=new DbWizard(this.conn);
			if(!dbwizard.isExistTable(tableName,false)) {
                this.tablebo.createTempTemplateTable(this.tablebo.getUserview().getUserName());
            }
			rowSet=dao.search("select count(*) from "+tableName);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    throw new GeneralException("请先处理完加班申请模板中的数据再执行当前操作!");
                }
			}
			if(this.tablebo.isBsp_flag()&&this.tablebo.getSp_mode()==1) {
                throw new GeneralException("请将加班申请关联的业务模板设置为自动审批!");
            }
			
			
			
			seqnum_id=info.split("~")[0]+"01";
			String mapping=info.split("~")[1];
			HashMap nbaseMap=analyseInfoList(infoList);
			Set keySet=nbaseMap.keySet();
			LazyDynaBean abean=new LazyDynaBean();
			String[] temps=mapping.split(","); 
			FieldItem item=null;
			RecordVo vo=null;
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				ArrayList _infoList=(ArrayList)nbaseMap.get(key);
				ArrayList a0100List=getA0100List(_infoList);
				this.tablebo.impDataFromArchive(a0100List,key);
				 
				for(int j=0;j<_infoList.size();j++)
				{
					abean=(LazyDynaBean)_infoList.get(j);
					String a0100=(String)abean.get("a0100");
					String nbase=(String)abean.get("nbase");
					vo=new RecordVo(tableName); 
					vo.setString("basepre",nbase);
					vo.setString("a0100",a0100);
					vo=dao.findByPrimaryKey(vo);
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
						{
							String[] temp=temps[i].toLowerCase().split(":");
							item=DataDictionary.getFieldItem(temp[0]);
							if(item!=null&&abean.get(temp[0])!=null)
							{
								if("D".equalsIgnoreCase(item.getItemtype()))
								{ 
									vo.setDate(temp[1],(Date)abean.get(temp[0]));
								}
								else if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
								{
									vo.setString(temp[1],(String)abean.get(temp[0])); 
								}
								else if("N".equalsIgnoreCase(item.getItemtype()))
								{
									if(item.getDecimalwidth()==0)
									{
										 vo.setInt(temp[1],((Integer)abean.get(temp[0])).intValue());
									}
									else
									{
										vo.setDouble(temp[1],((Double)abean.get(temp[0])).doubleValue());
									}
								}  
							}
						}
					}
					vo.setInt("submitflag",1);
					dao.updateValueObject(vo); 
				} 
				//执行存储过程，需将时间指标里的值分别写入  日期指标、小时指标、分钟指标中
				if(isProfunc("RESOLVETIMETOOTHER"))
				{
					StringBuffer sqlCall = new StringBuffer("{call  RESOLVETIMETOOTHER(?)}");
					cstmt= this.conn.prepareCall(sqlCall.toString()); 
					cstmt.setString(1,tableName);
					cstmt.execute();
					if (cstmt != null) {
                        cstmt.close();
                    }
				}
				autoAppealTask(tableName);
			}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeResource(cstmt);
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	private WF_Actor getSelfActor(UserView userView)
	{
		
		    String actorid="";
			String actor_type=""; 
			actorid=userView.getUserName();
			actor_type="4";
			WF_Actor wf_actor=new WF_Actor(actorid,actor_type);
			wf_actor.setContent("");//当前提交人的审批意见
			wf_actor.setEmergency("1");
			wf_actor.setSp_yj("01"); 
			wf_actor.setActorname(userView.getUserFullName());	
			wf_actor.setBexchange(false);
			return wf_actor;
	}
	
	/**
	 * 自动执行报批操作
	 * @param tablename
	 * @return
	 * @throws GeneralException
	 */
	private boolean autoAppealTask(String tablename)throws GeneralException
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 String tabid=String.valueOf(this.tablebo.getTabid());
			 UserView userView=this.tablebo.getUserview();
			 this.tablebo.batchCompute("0");
			 ArrayList  whlList=tablebo.getSplitInstanceWhl();
			 WF_Actor wf_actor=getSelfActor(userView);
			 RowSet rowSet=null;
			 for(int i=0;i<whlList.size();i++)
			 {	
				RecordVo ins_vo=new RecordVo("t_wf_instance");	 
				String whl=(String)whlList.get(i); 
				setObjs_sql(getObjsSql(0,0,2,tabid,userView,whl)); 
				if(createInstance(ins_vo,wf_actor,whl))
				{
					String sql="select count(*) as nrec from "+tablename+" where submitflag=1"+whl;
					rowSet=dao.search(sql);
					if(rowSet.next())
					{
						if(rowSet.getInt(1)>0)
						{
							String _sql="select * from "+tablename+" where submitflag=1"+whl;
							this.insertKqApplyTable(_sql,tabid,"0","02",tablename); //往考勤申请单中写入报批记录
							String approve_opinion = tablebo.getApproveOpinion(ins_vo,this.getTask_vo().getString("task_id"), wf_actor,"");
							tablebo.setApprove_opinion(approve_opinion);
							tablebo.saveSubmitTemplateData(userView.getUserName(),ins_vo.getInt("ins_id"),whl);
						}
					} 
					
				}
				 
				//把附件增加到流程中
				int ins_id = ins_vo.getInt("ins_id");
				rowSet=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
				if(!rowSet.next())
				{
					StringBuffer insertSQL=new StringBuffer();
					insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue,auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
					insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
					ArrayList list=new ArrayList();
					dao.insert(insertSQL.toString(),list);				
				}
				rowSet = dao.search(" select * from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userView.getUserName()+"'");
				String sqlstrs = ""; 
				while(rowSet.next()){
					String file_id =rowSet.getString("file_id");
					IDGenerator idg = new IDGenerator(2, this.conn);
		    		String file_id2 = idg.getId("t_wf_file.file_id");
					sqlstrs=" insert into t_wf_file(file_id,content,filetype,ins_id,tabid,ext,name,create_user,create_time) select "+file_id2+",content,filetype,"+ins_id+",tabid,ext,name,create_user,create_time from t_wf_file where file_id="+file_id+"  ";
					dao.update(sqlstrs);
				} 
				
				
			 
				SynOaService sos=new SynOaService();
				String tab_ids=sos.getTabids();
				if(tab_ids.indexOf(","+tabid+",")!=-1)
				{
						if("1".equals((String)sos.getTabOptMap().get(tabid.trim())))
						{
							String _info=sos.synOaService(String.valueOf(this.getTask_vo().getInt("task_id")),tabid,userView);  //创建成功返回1，否则返回详细错误信息
						//	if(!_info.equals("1"))
						//		throw GeneralExceptionHandler.Handle(new Exception(_info));	
						}
				}
			 }
			 PubFunc.closeDbObj(rowSet);
		}
		catch(Exception ex)
		{
				flag=false;
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return flag;
	}
	
	
	
	/**
	 * 判断数据库中有无此存储过程
	 * @param proFuncName
	 * @return
	 */
	private boolean isProfunc(String proFuncName)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String validateSql1=""; 
			switch (Sql_switcher.searchDbServer()) {
			case 1: // MSSQL 
				validateSql1="select   count(*)  from   sysobjects   where   ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and "; 
				validateSql1+=" id = object_id(N'[dbo].[" + proFuncName + "]'))"; 
				break;
			case 2:// oracle
				validateSql1="SELECT count(*) FROM all_objects WHERE object_type='PROCEDURE' AND object_name='"+proFuncName+"'"; 
				break;
			}
			
			//判断有无存储过程名
			int nn=0;
			RowSet rowSet=dao.search(validateSql1);
			if(rowSet.next()) {
                nn=rowSet.getInt(1);
            }
			if(nn==0)
			{
				flag=false;
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	private ArrayList getA0100List(ArrayList _infoList)
	{
		ArrayList list=new ArrayList();
		LazyDynaBean abean=new LazyDynaBean();
		for(int i=0;i<_infoList.size();i++)
		{
			abean=(LazyDynaBean)_infoList.get(i);
			list.add((String)abean.get("a0100"));
		}
		return list;
	}
	
	
	/**
	 * 将申请登记的人员分库
	 * @param infoList
	 * @return
	 */
	private HashMap analyseInfoList(ArrayList infoList)
	{
		HashMap map=new HashMap();
		LazyDynaBean abean=new LazyDynaBean();
		for(int i=0;i<infoList.size();i++)
		{
			abean=(LazyDynaBean)infoList.get(i);
			String a0100=(String)abean.get("a0100");
			String nbase=(String)abean.get("nbase");
			if(map.get(nbase.toLowerCase())!=null)
			{
				ArrayList list=(ArrayList)map.get(nbase.toLowerCase());
				list.add(abean);
			}
			else
			{
				ArrayList list=new ArrayList();
				list.add(abean);
				map.put(nbase.toLowerCase(),list);
			
			}
		}
		return map;
	}
	
	
	/***
	 * 创建一个流程实例
	 * @return
	 * @param ins_vo 实例对象
	 * @throws GeneralException
	 */
	public boolean createInstance(RecordVo vo,WF_Actor actor,String whl) throws GeneralException
	{
		boolean bflag=true;
		//取得当前用户的人员范围 wangrd 2013-12-11
		String operOrg = this.tablebo.getUserview().getUnitIdByBusi("8"); 
		if(operOrg!=null&&operOrg.trim().length()>0){
			if(this.tablebo.getUserview().getStatus()==4&& "UM`".equalsIgnoreCase(operOrg)){  //当自助用户没有部门时，将单位存储进去 T_WF_INSTANCE的B0110字段 20150730 liuzy。
			       operOrg=tablebo.getUserview().getUserOrgId();
			}else {
				String[] temps=operOrg.split("`");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equalsIgnoreCase(temps[i].trim())) {
                            operOrg="UN";
                        } else {
                            operOrg=temps[i].trim().substring(2);
                        }
						break;
					}
				}
			}
			/*
		    int k = operOrg.indexOf("`");
		    if (k>=0){
		        String s=operOrg.substring(0, k);
		        if (!"UN".equals(s))
		          s = s.substring(2);
		        operOrg=s;
		    }*/		    
		}
		else {
		    operOrg="UN";   
		}
		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			//20140920 dengcan 
			 String tabid=String.valueOf(tablebo.getTable_vo().getInt("tabid"));
			 String _sql="select * from ";
	    	 String tablename=tablebo.getUserview().getUserName()+"templet_"+tabid; 
	         if(this.tablebo.isBEmploy())//员工通过自助平台发动申请
	         {
	                    _sql+=" g_templet_"+tabid+" where a0100='"+tablebo.getUserview().getA0100()+"' and lower(basepre)='"+tablebo.getUserview().getDbname().toLowerCase()+"'";
	                    tablename="g_templet_"+tabid;
	         }
	         else
	         {
	                    _sql+=tablebo.getUserview().getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
	         }  

	         insertKqApplyTable(_sql,tabid,"","02",tablename); //往考勤申请单中写入报批记录
           //20190731
 			TemplateInterceptorAdapter.preHandle(tablename,Integer.parseInt(tabid),0 , null, "apply",tablebo.getUserview(),whl); 
			
			 
			//RecordVo vo=new RecordVo("t_wf_instance");
            IDGenerator idg=new IDGenerator(2,this.conn);		
            vo.setInt("ins_id",Integer.parseInt(idg.getId("wf_instance.ins_id")));
            this.ins_id=vo.getInt("ins_id");
            int _ins_id=vo.getInt("ins_id");
            vo.setString("name",tablebo.getName()+tablebo.getRecordBusiTopic(whl));
            vo.setInt("tabid",tablebo.getTable_vo().getInt("tabid"));
            vo.setDate("start_date",new Date()); //DateStyle.getSystemTime());
            /**过程状态
             * 1 初始化
             * 2 运行中
             * 3 暂停
             * 4 终止
             * 5 结束
             * */
            vo.setString("finished","2"); //运行中...
            /**模板类型
             * =1业务模板
             * =2固定网页
             * */
            vo.setInt("template_type",1);
            /**平台用户 =0
             * 还是自助用户=4
             * */
            if(tablebo.getUserview().getStatus()==0)//业务用户
            { 
                if(this.tablebo!=null&&this.tablebo.isBEmploy())
            	{
            		vo.setString("actorid", tablebo.getUserview().getDbname()+tablebo.getUserview().getA0100()); //tablebo.getUserview().getUserName()
                    vo.setInt("actor_type",1);
            	}
            	else {
					vo.setString("actorid",tablebo.getUserview().getUserName());
					vo.setInt("actor_type",4);
				}
            }
            else//自助用户
            {
            	vo.setString("actorid", tablebo.getUserview().getDbname()+tablebo.getUserview().getA0100()); //tablebo.getUserview().getUserName()
                vo.setInt("actor_type",1);
            }
            vo.setString("actorname",tablebo.getUserview().getUserFullName());
            /**是否有附件
             * =0 无附件
             * =1 有附件
             * =2 代表从业务模块人事异动进入
             * =3 代表从自助服务业务申请进入
             * */
            int bfile = 0;
            if("9".equals(this.moduleId)) {
                bfile = 3;
            } else {
                bfile = 2;
            }
            vo.setInt("bfile",bfile);
            if (!"".equals(operOrg)){
                vo.setString("b0110", operOrg);                
            }
            //new_ins_vo=vo;
            dao.addValueObject(vo);
            /**初始化任务*/
            try
            {
            	String srcTab=this.tablebo.getUserview().getUserName()+"templet_"+this.tablebo.getTabid();
    			if(this.tablebo.isBEmploy()) {
                    srcTab="g_templet_"+this.tablebo.getTabid();
                }
    			//2017-07-15 7x包程序 招聘模块已经没有“员工录用业务模板”此项配置不需要执行此方法。
            	//updateZpResumeState(srcTab,dao,tablebo.getTable_vo().getInt("tabid")); //更新招聘库中待录用人员的简历状态，置为“待审批” 
            	InitStartTask(vo,actor,whl);//将数据插入到t_wf_task_objlink和t_wf_task中
            	
            	//信息归档liuzy 20150820
            	this.filingInformation(vo, actor, this.tablebo.getUserview());
            }
            catch(Exception e)
            {
            	dao.delete("delete from t_wf_instance where ins_id="+_ins_id,new ArrayList());
				dao.delete("delete from t_wf_task where ins_id="+_ins_id,new ArrayList());
				dao.delete("delete from t_wf_task_objlink where ins_id="+_ins_id,new ArrayList());
				try
                {   //删除考勤数据 wangrd 2015-03-31
                    RowSet rowSet=dao.search(_sql);
                    String _delStr="";
                    if (!rowSet.next()){//用户临时表没有数据，则去template_*取。
                        _sql="select * from templet_"+tabid+" where ins_id="+_ins_id;
                        _delStr="delete from templet_"+tabid+" where ins_id="+_ins_id;
                        //回写到临时表数据里
                        tablebo.saveRecallTemplatedata(tablebo.getUserview().getUserName(), _ins_id, whl, "");
                    }                   
                    insertKqApplyTable(_sql,tabid,"","10",tablename); 
                    if (_delStr.length()>0){
                        dao.delete( _delStr,new ArrayList());
                    }
                    PubFunc.closeDbObj(rowSet);
                }
                catch(Exception sqle)
                {
                    sqle.printStackTrace();
                }
            	throw GeneralExceptionHandler.Handle(e);
            }
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**
	 * 更新招聘库中待录用人员的简历状态，置为“待审批”
	 * @param tabname
	 * @param dao
	 * @param tabid
	 */
	public void updateZpResumeState(String tabname,ContentDAO dao,int  tabid) throws GeneralException
	{
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			String businessTemplateIds="";
			String resumeStateFieldIds="";
			if(map!=null&&map.get("business_template")!=null&&map.get("resume_state") !=null) {
                ;
            }
			{
				businessTemplateIds=(String)map.get("business_template");
				resumeStateFieldIds=(String)map.get("resume_state"); 
			}  
			//2014-05-08 dengcan  当resumeStateFieldIds、businessTemplateIds为空时，报错
			if(map!=null&&resumeStateFieldIds!=null&&businessTemplateIds!=null&&resumeStateFieldIds.trim().length()>0&&businessTemplateIds.trim().length()>0&&businessTemplateIds.equals(String.valueOf(tabid)))
			{ 
					RowSet rowSet=dao.search("select  *   from  "+tabname+"  where 1=1 and submitflag=1 ");
					String srcbase="";
					String srca0100="";
					while(rowSet.next())
					{
						srcbase=rowSet.getString("basepre");
						srca0100=rowSet.getString("a0100");
						String tablename=srcbase+"A01";
						DbWizard dbwizard=new DbWizard(this.conn);
						if(!dbwizard.isExistField(tablename,resumeStateFieldIds,false)){ //加入这个判断，使得没有招聘模块的用户也可以正常使用，liuzy 20150716
							return;
						}
						dao.update("update "+srcbase+"A01 set "+resumeStateFieldIds+"='44' where a0100='"+srca0100+"'  ");
					}
					if(rowSet!=null) {
                        PubFunc.closeDbObj(rowSet);
                    }
			} 
        	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.zp_employ.resumeappealerror")+"!"));
		}
	}
	
	
	
	
	/**
	 * 判断任务是否是自定义流程中最后审批层级的最后一个待处理任务
	 * @param task_id
	 * @param bo
	 * @return
	 */
	public boolean isEndNode(int task_id,TemplateTableBo bo)
	{
		boolean flag=false;
		try
		{
			String sql="";
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo _task_vo=new RecordVo("t_wf_task");
			_task_vo.setInt("task_id",task_id);
			_task_vo=dao.findByPrimaryKey(_task_vo);
			if("1".equals(_task_vo.getString("params"))) //自定义流程
			{
				int _ins_id=_task_vo.getInt("ins_id");
				sql="select task_id from t_wf_task where node_id in (select id from t_wf_node_manual where sp_level=(select max(sp_level) from  t_wf_node_manual where bs_flag='1' and ins_id="+_ins_id+" and tabid="+bo.getTabid()+" ) ";
				sql+=" and ins_id="+_ins_id+" )  and ins_id="+_ins_id+" and Task_state=3";
				RowSet rowSet=dao.search(sql);
				String _task_id="";
				while(rowSet.next())
				{
					_task_id+=rowSet.getString("task_id")+",";
				}
				if(_task_id.equals(task_id+",")) {
                    flag=true;
                }
				if(rowSet!=null) {
                    PubFunc.closeDbObj(rowSet);
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return flag;
	}
	

	public RecordVo getTask_vo() {
		return task_vo;
	}

	public void setTask_vo(RecordVo task_vo) {
		this.task_vo = task_vo;
	}

	public String getUrl_s() {
		return url_s;
	}

	public void setUrl_s(String url_s) {
		this.url_s = url_s;
	}

	public String getEmail_staff_value() {
		return email_staff_value;
	}

	public void setEmail_staff_value(String email_staff_value) {
		this.email_staff_value = email_staff_value;
	}

	public String getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(String isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	

	public String getUser_h_s() {
		return user_h_s;
	}

	public void setUser_h_s(String user_h_s) {
		this.user_h_s = user_h_s;
	}

	public String getObjs_sql() {
		return objs_sql;
	}

	public void setObjs_sql(String objs_sql) {
		this.objs_sql = objs_sql;
	}

	public String getSpecialOperate() {
		return specialOperate;
	}

	public void setSpecialOperate(String specialOperate) {
		this.specialOperate = specialOperate;
	}

	public boolean isReAssign() {
		return isReAssign;
	}

	public void setReAssign(boolean isReAssign) {
		this.isReAssign = isReAssign;
	}
	
	public HashMap getOtherParaMap() {
		return otherParaMap;
	}

	public void setOtherParaMap(HashMap otherParaMap) {
		this.otherParaMap = otherParaMap;
	}
	/**
	 * 
	 * @Title: resetDbpre 
	 * @Description: 兼容老程序产生的USR这种数据(现有程序都改成Usr这种的了)
	 * @param srcTab  从那些表中得到数据并更改
	 * @param tablebo 
	 * @param taskid    
	 * @throws
	 */
	public void resetDbpre(String srcTab, TemplateTableBo tablebo,String taskid)throws GeneralException{
	    ContentDAO dao=new ContentDAO(this.conn);
        RowSet rset=null;
        try {
            ArrayList dbList=DataDictionary.getDbpreList();
            ArrayList oldPreList=new ArrayList();  
            StringBuffer querysql = new StringBuffer();
            querysql.append("select distinct lower(basepre) from ");
            querysql.append(srcTab);
            if("0".equalsIgnoreCase(taskid)){
            {
                if(srcTab.trim().equalsIgnoreCase("g_templet_"+tablebo.getTabid()))
                {
                    querysql.append(" where a0100='"+tablebo.getUserview().getA0100()+"' and lower(basepre)='"+tablebo.getUserview().getDbname().toLowerCase()+"'");
                }
            }
            }else{ 
                querysql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum  and "+srcTab+".ins_id=t_wf_task_objlink.ins_id");
                querysql.append("  and task_id="+taskid+" and tab_id="+tablebo.getTabid()+" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");  
            }
            rset=dao.search(querysql.toString());
            while(rset.next()){
                oldPreList.add(rset.getString(1));  
            } 
            for(int i=0;i<oldPreList.size();i++){
                String dbpre=(String) oldPreList.get(i); 
                for(int j=0;j<dbList.size();j++)
                {
                	
                    String pre=(String)dbList.get(j);
                    if(pre.equalsIgnoreCase(dbpre))
                    {
                        dbpre=pre;
                        break;
                    }
                }
                StringBuffer sqlbuffer = new StringBuffer();
                sqlbuffer.append("update ");
                sqlbuffer.append( srcTab );
                sqlbuffer.append(" set Basepre='"+dbpre+"' where ");
                if("0".equalsIgnoreCase(taskid)){
                    if(srcTab.trim().equalsIgnoreCase("g_templet_"+tablebo.getTabid()))
                    {
                        sqlbuffer.append("  a0100='"+tablebo.getUserview().getA0100()+"' and lower(basepre)='"+tablebo.getUserview().getDbname().toLowerCase()+"'");
                    }else{
                        sqlbuffer.append("  lower(Basepre)='"+dbpre.toLowerCase()+"'");
                    }
                        
                }else{
                    sqlbuffer.append(" lower(Basepre)='"+dbpre.toLowerCase()+"' and  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum  and "+srcTab+".ins_id=t_wf_task_objlink.ins_id");
                    sqlbuffer.append("  and task_id="+taskid+" and tab_id="+tablebo.getTabid()+" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");  
                }
                dao.update(sqlbuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            if(rset!=null){
                try {
                    rset.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
/** 流程终止   start */
	
	/**
	 * @author lis
	 * @Description: TODO
	 * @date 2016-4-15
	 * @param task_id
	 * @param tabid
	 * @param userView
	 * @param flag 0:撤销，1:流程终止按钮
	 * @throws GeneralException 
	 */
    public void processEnd(int task_id,int tabid,UserView userView,int flag) throws GeneralException{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
    		
			//判断审批人是否是发起人 
			String fullName = null;
			int ins_id = 0;
			String actorid="";
			String actor_type="";
			
			fullName=userView.getUserFullName();
			if(StringUtils.isBlank(fullName)) {
                fullName=userView.getUserName();
            }
			
			if(userView.getStatus()==0)//业务用户
			{
				actorid=userView.getUserName();
				actor_type="4";
			}
			else
			{
				actorid=userView.getDbname()+userView.getA0100();
				actor_type="1";
			}
			RecordVo task_vo=new RecordVo("t_wf_task");
			RecordVo ins_vo =null;
			task_vo.setInt("task_id",task_id);
			task_vo=dao.findByPrimaryKey(task_vo);
			if(task_vo!=null)
			{
				ins_id = task_vo.getInt("ins_id");
				
				ins_vo= new RecordVo("t_wf_instance");
				ins_vo.setInt("ins_id",ins_id);
				ins_vo = dao.findByPrimaryKey(ins_vo);
				if(ins_vo == null) {
                    throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
                }
				
				if(ins_vo.getString("finished").equals(String.valueOf(NodeType.TASK_STOP)))//如果流程已终止，则不再执行终止操作
                {
                    return;
                }
				
				String strsql="select * from templet_"+tabid+" where ins_id ="+ins_id;    
				insertKqApplyTable(strsql,tabid+"","0","10","templet_"+tabid); //往考勤申请单中写入报批记录
				
				if(flag == 0){
					updateTaskVo(task_vo, fullName, actorid, userView, dao);
					 /** 删除其它系统的待办任务 */
					updatePending(task_id, userView);
				}else if(flag == 1){
					//查询其当前实例的所有运行流程
					RowSet rowSet = dao.search(this.getRunningTaskSql(), Arrays.asList(ins_id));
					while(rowSet.next()){
						RecordVo task_vo_temp = new RecordVo("t_wf_task");
						int taskId = rowSet.getInt("task_id");
						task_vo_temp.setInt("task_id",taskId);
						task_vo_temp = dao.findByPrimaryKey(task_vo_temp);
						updateTaskVo(task_vo_temp, fullName, actorid, userView, dao);
						
						 /** 删除其它系统的待办任务 */
						updatePending(task_id, userView);
					}
				}
			}
			else {
                return;
            }
			/** 新增一条任务 */
			int node_id=task_vo.getInt("node_id");
			RecordVo taskvo = addTaskVo(ins_vo, node_id, actorid, actor_type, fullName, dao);
			
            /**设置流程实例为结束状态*/
            ins_vo.setString("finished",String.valueOf(NodeType.TASK_STOP));
            ins_vo.setDate("end_date",new Date());  //DateStyle.getSystemTime());
            dao.updateValueObject(ins_vo);
            
            ////当模板中有审批意见指标，向其中添加内容
            String srcTab="templet_" + tabid;
    		StringBuffer opinionstr = new StringBuffer(" select * from templet_"+tabid+" ");
    		opinionstr.append(" where  exists (select null from t_wf_task_objlink ");
    		opinionstr.append("where "+srcTab+".seqnum=t_wf_task_objlink.seqnum ");
    		opinionstr.append("and task_id="+task_id+"   and submitflag=1 ");
    		opinionstr.append(" and ins_id=" + ins_id);
    		opinionstr.append(" and tab_id=" + tabid);
    		opinionstr.append(" and node_id=" + node_id);
    		//opinionstr.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0");
    		//opinionstr.append(" or ( "+Sql_switcher.isnull("special_node","0")+"=1 and lower(username)='"+userView.getUserName().toLowerCase()+"' ) )");
    		//opinionstr.append(" and state=3 ");
			opinionstr.append(") ");
			
            String opinion_field = tablebo.getOpinion_field();//审批意见指标
            ArrayList fieldlist=tablebo.getAllFieldItem();
			
    		WF_Actor wf_actor=new WF_Actor(actorid,actor_type);//流程参与者的相关信息。对话框中的数据全部会存进去
			wf_actor.setBexchange(false);//不让替换成选择的报送对象
			wf_actor.setActorname(userView.getUserFullName());			
			wf_actor.setContent(fullName+"：终止流程");//.replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
			wf_actor.setSp_yj("02");//审批意见
			
    		for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(j);
				String field_name=fielditem.getItemid();
				if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
				{
					String approveopinion ="";
					
					approveopinion = tablebo.getApproveOpinion(ins_vo, wf_actor,"");
					//更新审批意见指标
					tablebo.updateApproveOpinion( "templet_"+tabid,ins_id+"",opinion_field+"_2",approveopinion,opinionstr.toString());
					break;
				}
					
			}
    		
    		 /** 新增审批任务对应记录信息表A，t_wf_task_objlink */
            this.createTaskObjLink(task_id,tabid, ins_id, node_id, taskvo, dao);
            
            TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),tablebo.getTabid(),null,"stop",userView);
            
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
    /**
     * @author lis
     * @Description: 获得流程审批信息表的序号
     * @date 2016-4-18
     * @param tabid 模板号
     * @param ins_id 实例id
     * @param dao
     * @return ArrayList
     * @throws GeneralException
     */
    public ArrayList getSeqnumList(int tabid,int ins_id,ContentDAO dao) throws GeneralException{
    	StringBuffer sql = new StringBuffer();
    	ArrayList seqnumList = new ArrayList();
    	RowSet rowSet = null;
    	try {
    		  sql.append("select * from templet_"+tabid+" where ins_id=? ");
    		  rowSet = dao.search(sql.toString(),Arrays.asList(ins_id));
    		  while(rowSet.next())
              {
                  seqnumList.add(rowSet.getString("seqnum").trim());
              }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return seqnumList;
    }
    
    /**
     * @author lis
     * @Description:  新增审批任务对应记录信息表A，t_wf_task_objlink
     * @date 2016-4-18
     * @param task_id 任务id
     * @param tabid 模板号
     * @param ins_id 实例id
     * @param node_id 节点id
     * @param taskvo 任务
     * @param dao
     * @throws GeneralException
     */
    public void createTaskObjLink(int task_id,int tabid,int ins_id,int node_id,RecordVo taskvo,ContentDAO dao) throws GeneralException{
    	try {
    		 ArrayList seqnumList = this.getSeqnumList(tabid, ins_id, dao);
             if(seqnumList!=null&&seqnumList.size()>0)
             {	
            	 int count = this.getCountByTaskID(Integer.valueOf(task_id));
            	 String _tablename="templet_"+this.tablebo.getTabid(); 
                 StringBuffer seqnum_str=new StringBuffer("");
                 int n=0;
                 ArrayList recordList=new ArrayList();
                 for(int i=0;i<seqnumList.size();i++)
                 {
                     String seqnum=(String)seqnumList.get(i);
                     String username="";
                     if(seqnum.indexOf(":")!=-1) //20141220 dengcan 执行退回操作，如果上一流程节点是角色，需直接退回到报批人
                     {
                     	username=seqnum.split(":")[1];
                     	seqnum=seqnum.split(":")[0];
                     }
                     
                     RecordVo objlink_vo=new RecordVo("t_wf_task_objlink"); 
                     objlink_vo.setString("task_type","1"); 
                     objlink_vo.setString("seqnum",seqnum);
                     objlink_vo.setString("username",username);
                     objlink_vo.setInt("ins_id", ins_id);
                     objlink_vo.setInt("task_id",taskvo.getInt("task_id"));
                     objlink_vo.setInt("tab_id", tabid);
                     objlink_vo.setInt("node_id",node_id);
                     objlink_vo.setString("task_type","1"); //任务类型
                     objlink_vo.setInt("submitflag",1);
                     objlink_vo.setInt("count",count);
                     objlink_vo.setInt("state",1);//记录处理状态,=1 ，批准
                      
                     if(n<5)
                     {
                         seqnum_str.append(" or  seqnum='"+seqnum+"' ");
                     } 
                     recordList.add(objlink_vo);
                     n++;
                 } 
                 dao.addValueObject(recordList);
                 if(seqnum_str.length()>0)
                 { 
                     taskvo.setString("task_topic",tablebo.getRecordBusiTopic(_tablename,seqnum_str.toString(),recordList.size()));
                 }
             }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    /**
     * 
     * 获得任务节点当前处理的次数
     *
     * @param valueOf
     */
    private int getCountByTaskID(int taskid) throws GeneralException{
    	int count=0;
        RowSet rowSet=null;
        try
        { 
            ContentDAO dao=new ContentDAO(this.conn);
            rowSet=dao.search("select count from t_wf_task_objlink where task_id="+taskid);
            if(rowSet.next()) {
                count=rowSet.getInt(1);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
            try
            {
                if(rowSet!=null) {
                    rowSet.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return count;
	}

	/**
     * @author lis
     * @Description: 新增一条任务
     * @date 2016-4-18
     * @param ins_vo 
     * @param node_id 节点id
     * @param actorid 任务参与者
     * @param actor_type  参与者类型
     * @param fullName 任务参与者名称
     * @param dao
     * @return RecordVo
     * @throws GeneralException
     */
    public RecordVo addTaskVo(RecordVo ins_vo,int node_id,String actorid,String actor_type,String fullName,ContentDAO dao) throws GeneralException{
    	RecordVo taskvo=new RecordVo("t_wf_task");
    	try {
            IDGenerator idg=new IDGenerator(2,this.conn);       
            taskvo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
            taskvo.setString("task_topic",ins_vo.getString("name"));
            taskvo.setInt("node_id",node_id);
            taskvo.setInt("ins_id",ins_vo.getInt("ins_id"));
            taskvo.setDate("start_date",new Date()); //DateStyle.getSystemTime());
            taskvo.setString("task_type",String.valueOf(NodeType.END_NODE));
            taskvo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));//终止
            taskvo.setString("bs_flag","1");  //=1 or =null，报批任务（任务参与者可以审批，
            
            taskvo.setString("state","06"); //审批状态
            taskvo.setString("sp_yj","02");//审批意见
            taskvo.setString("content","不同意");//审批意见描述
            taskvo.setInt("bread",0);//是否已阅读
            taskvo.setDate("end_date",new Date()); //DateStyle.getSystemTime());    
            /**根据任务分配算法，具体对应到准*/
            taskvo.setString("actorid",actorid);//当前对象    流程定义的参与者     
            taskvo.setString("actor_type",actor_type);
            taskvo.setString("a0100",actorid);
            taskvo.setString("a0101",fullName);
            taskvo.setString("a0100_1",actorid);//发送人
            taskvo.setString("a0101_1",fullName);//发送人姓名             
            taskvo.setString("url_addr","");//审批网址
            taskvo.setString("params","");//参数  
            if(task_vo!=null) {
                taskvo.setString("task_id_pro", task_vo.getString("task_id_pro"));
            }
            dao.addValueObject(taskvo);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return taskvo;
    }
    
	/**
	 * @author lis
	 * @Description: 删除其它系统的待办任务
	 * @date 2016-4-18
	 * @param task_id 任务id
	 * @param userView
	 * @throws GeneralException 
	 */
    public void updatePending(int task_id,UserView userView) throws GeneralException{
    	try {
    		 /** 删除其它系统的待办任务 */
			PendingTask imip=new PendingTask();
			String pendingType="业务模板";  
			String pendingCode="HRMS-"+PubFunc.encrypt(task_id+""); 
			imip.updatePending("T",pendingCode,100,pendingType,userView);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    
    /**
     * @author lis
     * @Description: 更新任务
     * @date 2016-4-18
     * @param task_vo 
     * @param fullName 实际处理人名字
     * @param actorid  实际处理任务人id
     * @param userView
     * @param dao
     * @throws GeneralException 
     */
    public void updateTaskVo(RecordVo task_vo,String fullName,String actorid,UserView userView,ContentDAO dao) throws GeneralException{
    	try {
			task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());				
			task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
			
			String appuser=task_vo.getString("appuser")+userView.getUserName()+",";
			task_vo.setString("appuser", appuser);
			task_vo.setString("a0100",actorid);
			task_vo.setString("a0101",fullName);
			task_vo.setString("content",fullName+"：终止流程");
			task_vo.setString("sp_yj","02");//审批意见
			task_vo.setString("task_state", String.valueOf(NodeType.TASK_FINISHED));
			dao.updateValueObject(task_vo);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    
    /**
     * @author lis
     * @Description: 得到所有运行中的任务
     * @date 2016-4-15
     * @return String
     * @throws GeneralException 
     */
    public String getRunningTaskSql() throws GeneralException{
    	StringBuffer sql = null;
    	try {
    		ContentDAO dao = new ContentDAO(this.conn);
    		sql = new StringBuffer("select * from t_wf_task T,t_wf_instance U where  T.ins_id=U.ins_id and task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");
			sql.append(" and T.ins_id=?");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
    }
    
    /** 流程终止   end */
    /***
     * 更新审批意见
     * @param ins_vo
     * @param actor
     * @param userview
     * @param taskid 
     */
    public void updateApproveOpinion(RecordVo ins_vo, WF_Actor actor, UserView userview, int taskid) {
		try {
			ArrayList fieldlist = tablebo.getAllFieldItem();
			String opinionstr = " select  *  from templet_" + this.tablebo.getTabid() + " ";
	        opinionstr += "  where  seqnum in  (select  seqnum  from t_wf_task_objlink where  ";
	        opinionstr += "   task_id=" + taskid + "   and submitflag=1  and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+userview.getUserName().toLowerCase()+"' or lower(username)='"+userview.getDbname().toLowerCase()+userview.getA0100()+"' ) ) )) ";
	        String opinion_field = tablebo.getOpinion_field();// 审批意见指标
	        if ( opinion_field != null && opinion_field.length() > 0){
	            for (int j = 0; j < fieldlist.size(); j++) {
	                FieldItem fielditem = (FieldItem) fieldlist.get(j);
	                String field_name = fielditem.getItemid();
	                int changestate = fielditem.getNChgstate();
	                if ((opinion_field+"_2").equalsIgnoreCase(field_name+"_"+changestate)) {
	                    String approveopinion = "";
	                    
	                    if ("02".equals(actor.getSp_yj())) {
	                        approveopinion = tablebo.getApproveOpinion(ins_vo, taskid+"",actor, "02");
	                    } else {
	                        approveopinion = tablebo.getApproveOpinion(ins_vo, taskid+"",actor, "");
	                    }
	                    tablebo.updateApproveOpinion("templet_" + tablebo.getTabid()+"", ins_id+"", opinion_field + "_2", approveopinion, opinionstr);
	                    break;
	                }
	            }
	        }
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    }
}
