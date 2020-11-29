package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Transition;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Define_WF_Node {
    private Connection conn;
    private String tabid;//固定表单id
    private int node_id=0;	
	private String nodename;
	/**节点类型
	 * =1开始，=2人工,=3自动
	 * =4与发散,=5与汇聚,=6或发散
	 * =7或汇聚,=8哑节点,
	 * */
	private int nodetype=0;//无效节点;	
	/**其它选项参数*/
	private String ext_param;
	
	/**对应的实例对象*/
	private RecordVo ins_vo;
	/**审批对象及其签署的意见*/
	//private WF_Actor wf_actor;
	/**对应的任务对象*/
	private RecordVo task_vo;
	/**
	 * 任务号，对人工节点
	 * 用户审批时，需设置当前审批的任务号
	 * 需结束当前审批的任务
	 * */
	private int taskid=-1;
	private TemplateDefineBo tablebo;
	private String url_addr;//业务url地址
	private String params;//业务参数地址
	public Define_WF_Node(Connection conn,String tabid)
	{
		this.conn=conn;
		this.tabid=tabid;
	}
	public Define_WF_Node(TemplateDefineBo tablebo,Connection conn)
	{
		this.conn=conn;
		this.tabid=tablebo.getTabid()+"";
	}
	public Define_WF_Node(int node_id, Connection conn,TemplateDefineBo tablebo) {
		this.tablebo=tablebo;
		this.node_id=node_id;
		this.conn = conn;		
		initdata();

	}
	private void initdata()
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo=new RecordVo("t_wf_node");
		vo.setInt("node_id",this.node_id);
		try
		{
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
			{
				this.ext_param=vo.getString("ext_param");
				this.nodename=vo.getString("nodename");
				this.nodetype=Integer.parseInt(vo.getString("nodetype"));
				this.tabid=vo.getString("tabid");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}	
	public Define_WF_Node(Connection conn)
	{
		this.conn=conn;		
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getExt_param() {
		return ext_param;
	}
	public void setExt_param(String ext_param) {
		this.ext_param = ext_param;
	}
	public RecordVo getIns_vo() {
		return ins_vo;
	}
	public void setIns_vo(RecordVo ins_vo) {
		this.ins_vo = ins_vo;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
	}
	public String getNodename() {
		return nodename;
	}
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
	public int getNodetype() {
		return nodetype;
	}
	public void setNodetype(int nodetype) {
		this.nodetype = nodetype;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public RecordVo getTask_vo() {
		return task_vo;
	}
	public void setTask_vo(RecordVo task_vo) {
		this.task_vo = task_vo;
	}
	/**
	 * 创建下一个任务
	 * @param instance
	 * @return
	 * @throws GeneralException
	 */
	public boolean createTask(RecordVo instancevo,WF_Actor actor)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			this.ins_vo=instancevo;
			ArrayList nextlist=this.getNextNodeList();
			Define_WF_Node next_node=null;
			switch(this.nodetype)
			{
			case NodeType.START_NODE://起始开始
				/**开始节点的输出变迁仅只能有一个*/
				if(nextlist.size()!=1)
					throw new GeneralException(ResourceFactory.getProperty("error.start.outtrans"));
				/**下一个节点*/
				next_node=(Define_WF_Node)nextlist.get(0);
				if(next_node.getNodetype()==0)
					throw new GeneralException(ResourceFactory.getProperty("error.nextnode.not"));
				/**把起始节点设置为结束状态*/
				task_vo=saveTask(this,NodeType.TASK_FINISHED,actor);
				switch(next_node.nodetype)
				{
				case NodeType.HUMAN_NODE://人工节点
				case NodeType.TOOL_NODE://自动节点				
					RecordVo taskvo=saveTask(next_node,NodeType.TASK_WAINTING,actor);
					/**起始节点，流程定义加入参与对象*/
					if(next_node.nodetype==NodeType.HUMAN_NODE)					
						updateTaskActor(taskvo,actor);
					if(taskvo.getString("actorid")==null|| "".equals(taskvo.getString("actorid")))
						throw new GeneralException(ResourceFactory.getProperty("error.nodefine.applyobj"));
					break;				
				case NodeType.END_NODE:
					saveTask(next_node,NodeType.TASK_FINISHED,actor);					
					break;
				default:
					next_node.createTask(instancevo,actor);
					break;
				}
				break;
			case NodeType.HUMAN_NODE://人工节点
			case NodeType.TOOL_NODE: //自动节点			
				/**人工节点的输出变迁仅只能有一个*/
				if(nextlist.size()==0)
					throw new GeneralException(ResourceFactory.getProperty("error.nextnode.not"));
				/**这句很重要，如果当前节点任务已传进来，则不用
				 * 再新一个任务啦
				 * */
				if(this.taskid==-1)
					task_vo=saveTask(this,NodeType.TASK_FINISHED,actor);	


				/**取下一个节点*/
				next_node=(Define_WF_Node)nextlist.get(0);
				switch(next_node.nodetype)
				{
				case NodeType.HUMAN_NODE://人工节点
				case NodeType.TOOL_NODE://自动节点
					RecordVo taskvo=saveTask(next_node,NodeType.TASK_WAINTING,actor);	
					if(next_node.nodetype==NodeType.HUMAN_NODE)
						updateTaskActor(taskvo,actor);
					if(taskvo.getString("actorid")==null|| "".equals(taskvo.getString("actorid")))
						throw new GeneralException(ResourceFactory.getProperty("error.nodefine.applyobj"));
					break;				
				case NodeType.END_NODE:
					saveTask(next_node,NodeType.TASK_FINISHED,actor);					
					break;
				default:
					next_node.createTask(instancevo,actor);
					break;
				}			
				break;
			case NodeType.AND_SPLIT_NODE://与发散
				break;
			case NodeType.ADN_JOIN_NODE://与汇聚和ADN_SPLIT_NODE配对，需要同步
				break;
			case NodeType.OR_SPLIT_NODE://或发散
				break;
			case NodeType.OR_JOIN_NODE://或汇聚
				break;
			case NodeType.END_NODE: //终止结点
				saveTask(this,NodeType.TASK_FINISHED,actor);			
				break;
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
	 * 设置下一步任务处理对象
	 * @param taskvo
	 * @throws GeneralException
	 */
	private void updateTaskActor(RecordVo taskvo,WF_Actor actor)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(actor!=null)
			{
				if(!actor.isBexchange())//不支持同一节点任意参与者的流转
					return;
				taskvo.setString("actorid",actor.getActorid());//当前对象	流程定义的参与者	 
				taskvo.setString("actor_type",actor.getActortype());
				taskvo.setString("task_pri",actor.getEmergency());
				taskvo.setString("actorname",actor.getActorname());
				dao.updateValueObject(taskvo);				
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 取得下一节点列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getNextNodeList()throws GeneralException
	{
		ArrayList nextnodelist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		int node_id=-1;
		try
		{
			ArrayList translist=this.getOutTransitionList();
			
			for(int i=0;i<translist.size();i++)
			{
				WF_Transition wf_trans=(WF_Transition)translist.get(i);
				node_id=wf_trans.getNext_nodeid();
				if(node_id==-1)
					continue;
				RecordVo node_vo=new RecordVo("t_wf_node");
				node_vo.setInt("node_id",node_id);
				node_vo=dao.findByPrimaryKey(node_vo);
				Define_WF_Node define_wf_node=new Define_WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
				define_wf_node.setNode_id(node_vo.getInt("node_id"));
				define_wf_node.setNodename(node_vo.getString("nodename"));
				define_wf_node.setNodetype( Integer.parseInt(node_vo.getString("nodetype")));
				define_wf_node.setTabid(node_vo.getString("tabid"));
				nextnodelist.add(define_wf_node);
			}// for loop end
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return nextnodelist;
	}
	/**
	 * 求进入变迁
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getInTransitionList()throws GeneralException
	{
		return getTransitionList(1);
	}
	/**
	 * 求输出变迁
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getOutTransitionList()throws GeneralException
	{
		return getTransitionList(2);
	}	
	/**
	 * 取得
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getTransitionList(int flag)throws GeneralException
	{
		ArrayList translist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			if(flag==1)
				strsql.append("select * from t_wf_transition where next_nodeid=");
			else
				strsql.append("select * from t_wf_transition where pre_nodeid=");
			strsql.append(this.node_id);
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				WF_Transition trans=new WF_Transition();
				trans.setPre_nodeid(rset.getInt("pre_nodeid"));
				trans.setNext_nodeid(rset.getInt("next_nodeid"));
				trans.setTabid((String)rset.getString("tabid"));
				trans.setCondition(Sql_switcher.readMemo(rset,"condition"));
				trans.setTran_id(rset.getInt("tran_id"));
				translist.add(trans);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally
		{
			if(rset!=null)
				try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return translist;		
	}
	/**
	 * 创建任务状态
	 */
	private RecordVo saveTask(Define_WF_Node node,int task_state,WF_Actor wf_actor)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);

		RecordVo taskvo=null;
		try
		{
			/**多级审批流转时,发送人的数据取不到*/
			if(this.taskid!=-1)
			{
				this.task_vo=new RecordVo("t_wf_task");
				this.task_vo.setInt("task_id", this.taskid);
				this.task_vo=dao.findByPrimaryKey(this.task_vo);
			}
			
			taskvo=new RecordVo("t_wf_task");
            IDGenerator idg=new IDGenerator(2,this.conn);		
            taskvo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
            taskvo.setString("task_topic",this.ins_vo.getString("name"));
            taskvo.setInt("node_id",node.node_id);
            taskvo.setInt("ins_id",this.ins_vo.getInt("ins_id"));
            taskvo.setDate("start_date",DateStyle.getSystemTime());
            taskvo.setString("task_type",String.valueOf(node.nodetype));
            if(wf_actor!=null)
            	taskvo.setString("task_pri",wf_actor.getEmergency());//任务优先级
			switch(node.nodetype)
			{
			case NodeType.START_NODE:
	            taskvo.setString("state",""); //审批状态
	            if(wf_actor!=null)
	            {
	            	taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
	            	taskvo.setString("content",wf_actor.getContent());//审批意见描述
	            }
	            taskvo.setInt("bread",0);//是否已阅读
	            taskvo.setDate("end_date",DateStyle.getSystemTime());	
	            taskvo.setString("task_state",String.valueOf(task_state));
	            /**根据任务分配算法，具体对应到准*/
				taskvo.setString("actorid",this.ins_vo.getString("actorid"));//当前对象	流程定义的参与者
				if(this.ins_vo.getInt("actor_type")==0)
					taskvo.setString("actor_type","4"/*this.ins_vo.getString("actor_type")*/);
				else
					taskvo.setString("actor_type","1"/*this.ins_vo.getString("actor_type")*/);
				taskvo.setString("actorname",this.ins_vo.getString("actorname")/*wf_actor.getActorname()*/);
	            taskvo.setString("a0100",this.ins_vo.getString("actorid"));//人员编号 实际处理人员编码
	            taskvo.setString("a0101",this.ins_vo.getString("actorname"));//人员姓名 实际处理人员姓名
            	taskvo.setString("a0100_1",this.ins_vo.getString("actorid")/*this.wf_actor.getActorid()*/);//发送人
            	taskvo.setString("a0101_1",this.ins_vo.getString("actorname")/*this.wf_actor.getActorname()*/);//发送人姓名
            	taskvo.setString("appuser", this.ins_vo.getString("actorid")+",");//手工指派
	            taskvo.setString("url_addr",this.getUrl_addr());//审批网址
	            taskvo.setString("params",this.getParams());//参数
				break;
			case NodeType.END_NODE:
	            taskvo.setString("state","06"); //审批状态
	            taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
	            taskvo.setString("content",wf_actor.getContent());//审批意见描述
	            taskvo.setInt("bread",0);//是否已阅读
	            taskvo.setDate("end_date",DateStyle.getSystemTime());	
	            taskvo.setString("task_state",String.valueOf(task_state));
	            /**根据任务分配算法，具体对应到准*/
				taskvo.setString("actorid",wf_actor.getActorid());//当前对象	流程定义的参与者	 
				taskvo.setString("actor_type",wf_actor.getActortype());
	            taskvo.setString("a0100",wf_actor.getActorid());//人员编号 实际处理人员编码
	            taskvo.setString("a0101",wf_actor.getActorname());//人员姓名 实际处理人员姓名
            	taskvo.setString("a0100_1",wf_actor.getActorid());//发送人
            	taskvo.setString("a0101_1",wf_actor.getActorname());//发送人姓名	            
	            taskvo.setString("url_addr",this.getUrl_addr());//审批网址
	            taskvo.setString("params",this.getParams());//参数	
	            /**设置流程实例为结束状态*/
	            this.ins_vo.setString("finished",String.valueOf(NodeType.TASK_FINISHED));
	            this.ins_vo.setDate("end_date",DateStyle.getSystemTime());
	            dao.updateValueObject(ins_vo);
				break;	
			case NodeType.HUMAN_NODE://人工结点
	            taskvo.setString("state","08"); //审批状态,报批状态
	            taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
	            taskvo.setString("content",wf_actor.getContent());//审批意见描述
	            taskvo.setInt("bread",0);//是否已阅读
	            //taskvo.setDate("end_date",DateStyle.getSystemTime());//?	
	            taskvo.setString("task_state",String.valueOf(task_state));
	            /**根据任务分配算法，具体对应到准*/
				taskvo.setString("actorid","");//当前对象	流程定义的参与者	            
	            taskvo.setString("a0100","");//人员编号 实际处理人员编码
	            taskvo.setString("a0101","");//人员姓名 实际处理人员姓名		
	            if(this.task_vo!=null)
	            {
	            	taskvo.setString("a0100_1",task_vo.getString("a0100"));//发送人
	            	taskvo.setString("a0101_1",task_vo.getString("a0101"));//发送人姓名
	            }
            	String appuser="";
            	if(this.task_vo==null)
            		appuser=this.ins_vo.getString("actorid")+",";
            	else
            	{
            		appuser=this.task_vo.getString("appuser")==null?"":this.task_vo.getString("appuser");
            		//appuser=appuser+","+this.ins_vo.getString("actorid");
            	}
            	taskvo.setString("appuser", appuser);
            	
	            taskvo.setString("url_addr",this.getUrl_addr());//审批网址
	            taskvo.setString("params",this.getParams());//参数	
	            /**流程节点*/
				ArrayList actorlist=node.getActorList();
				WF_Actor actor=null;
				if(actorlist.size()!=0)
				{
					actor=(WF_Actor)actorlist.get(0);
					taskvo.setString("actorid",actor.getActorid());//当前对象	流程定义的参与者
					taskvo.setString("actor_type",actor.getActortype());
					taskvo.setString("actorname",actor.getActorname());					
				}
				break;
			}
			dao.addValueObject(taskvo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return taskvo;
	}
	/**
	 * 取得本节点参与者列表
	 * @return
	 */
	public ArrayList getActorList() throws GeneralException
	{
		ArrayList actorlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_actor where node_id=");
			strsql.append(this.node_id);
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				WF_Actor actor=new WF_Actor();
				actor.setNode_id(rset.getInt("node_id"));
				actor.setActorid(rset.getString("actorid"));
				actor.setActorname(rset.getString("actorname"));
				actor.setActortype(rset.getString("actor_type"));
				actorlist.add(actor);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally
		{
			if(rset!=null)
				try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return actorlist;
	}
	/**
	 * 取得一下人工节点
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getNextHumanNodeList()throws GeneralException
	{
		ArrayList humanlist=new ArrayList();
		Define_WF_Node d_wf_node=null;
		try
		{
			ArrayList nextlist=this.getNextNodeList();
			switch(this.nodetype)
			{
				case NodeType.START_NODE:
					if(nextlist.size()>1)
						throw new GeneralException(ResourceFactory.getProperty("error.start.outtrans"));
					d_wf_node=(Define_WF_Node)nextlist.get(0);
					if(d_wf_node.nodetype==NodeType.HUMAN_NODE)
						humanlist.add(d_wf_node);
					else
					{
						humanlist.addAll(getNextHumanNodes(d_wf_node));
					}
					break;
				case NodeType.END_NODE:
					break;
				default:
					for(int i=0;i<nextlist.size();i++)
					{
						d_wf_node=(Define_WF_Node)nextlist.get(i);
						switch(d_wf_node.nodetype)
						{
							case NodeType.HUMAN_NODE:
								humanlist.add(nextlist.get(i));
								break;
							case NodeType.END_NODE:
								break;
							default:
								humanlist.addAll(getNextHumanNodes(d_wf_node));
								break;
						}						
					}//for i
					break;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return humanlist;
	}
	private ArrayList getNextHumanNodes(Define_WF_Node wf_node)throws GeneralException
	{
		ArrayList humanlist=new ArrayList();
		try
		{
			ArrayList nextlist=wf_node.getNextNodeList();
			for(int i=0;i<nextlist.size();i++)
			{
				
				switch(wf_node.nodetype)
				{
					case NodeType.HUMAN_NODE:
						humanlist.add(nextlist.get(i));
						break;
					case NodeType.END_NODE:
						break;
					default:
						humanlist.addAll(getNextHumanNodes(wf_node));
						break;
				}
			}//for e
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return humanlist;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getUrl_addr() {
		return url_addr;
	}
	public void setUrl_addr(String url_addr) {
		this.url_addr = url_addr;
	}
}
