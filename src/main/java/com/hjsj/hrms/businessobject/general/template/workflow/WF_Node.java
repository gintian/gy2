/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:WF_Node</p>
 * <p>Description:流程节点</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 20063:19:04 PM
 * @author chenmengqing
 * @version 4.0
 */
public class WF_Node {
    private String url_s="";
    private String cd_whl="";//拆单条件
    
    private int node_id=0;
    /**流程定义对象*/
    private TemplateTableBo tablebo;    
    private WorkflowBo workflowBo;
    private String nodename;
    /**节点类型
     * =1开始，=2人工,=3自动
     * =4与发散,=5与汇聚,=6或发散
     * =7或汇聚,=8哑节点,=9结束
     * */
    private int nodetype=0;//无效节点;
    /**模板编码*/
    private String tabid;
    /**其它选项参数*/
    private String ext_param;

    private Connection conn;
    /**对应的实例对象*/
    private RecordVo ins_vo;
    /**审批对象及其签署的意见*/
    //private WF_Actor wf_actor;
    /**对应的任务对象*/
    private RecordVo task_vo;
    
    public boolean isbSelfApply() {
		return bSelfApply;
	}

	public void setbSelfApply(boolean bSelfApply) {
		this.bSelfApply = bSelfApply;
	}
	private boolean bSelfApply=false;

    /**
     * 任务号，对人工节点
     * 用户审批时，需设置当前审批的任务号
     * 需结束当前审批的任务
     * */
    private int taskid=-1;
    private String tabname="";
    
    /**下一个任务节点*/
    private RecordVo nexTTask_vo;
    /**下一个任务节点(含多个），用于自动流转*/
    private ArrayList nextTaskVoList=new ArrayList();
    public ArrayList getNextTaskVoList() {
		return nextTaskVoList;
	}
    private boolean  isWaiting=false; //当下一节点是与或汇聚时，需判断是否满足汇聚要求，可以继续流转还是等待
    
    /** 邮件抄送 */
    String isSendMessage="0";
    String user_h_s="";
    String email_staff_value="";
    String objs_sql="";
    
    HashMap otherParaMap=new HashMap();
    String  opt="";  // 1:报批  2：驳回  3：批准  4:重新分配
    ArrayList seqnumList=new ArrayList(); //节点下关联的数据
    String actorname="";   //如果节点是按权限范围 控制的角色 ，则 赋予相应角色名字
    String actorid="";
    String actor_type="";
    
    boolean isStartWorkflow=false;  //流程从开始节点报批
    public boolean isStartWorkflow() {
		return isStartWorkflow;
	}

	public void setStartWorkflow(boolean isStartWorkflow) {
		this.isStartWorkflow = isStartWorkflow;
	}
	//  public void setWf_actor(WF_Actor wf_actor) {
//      this.wf_actor = wf_actor;
//  }
    private String sendresource="";//1为触发器
    public String getActorname() {
        return actorname;
    }

    public void setActorname(String actorname) {
        this.actorname = actorname;
    }

    public WF_Node(TemplateTableBo tablebo, Connection conn) {
        this.tablebo=tablebo;
        this.tabid=tablebo.getTable_vo().getString("tabid");
        this.conn = conn;
    }
    
    public WF_Node(int node_id, Connection conn,TemplateTableBo tablebo) {
        this.tablebo=tablebo;
        this.node_id=node_id;
        this.conn = conn;       
        initdata();
    }   
    
    public WF_Node(int node_id, Connection conn ) {
         
        this.node_id=node_id;
        this.conn = conn;       
        initdata();

    }   
    
    
    public WF_Node(Connection conn)
    {
        this.conn=conn;
    }
    
    
    
    /**
     * 判断当前节点是否是考核关系节点
     * @param node_id
     * @return
     */
    public boolean isKhRelationNode(int node_id)
    {
        boolean flag=false;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select role_property from t_sys_role where role_id=(select actorid from t_wf_actor where node_id="+node_id+" and actor_type='2')");
            if(rowSet.next())
            {
                int role_property=rowSet.getInt("role_property");
                if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14) {
                    flag=true;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
    
    
    private ArrayList getNextHumanNodes(WF_Node wf_node)throws GeneralException
    {
        ArrayList humanlist=new ArrayList();
        try
        {
            ArrayList nextlist=wf_node.getNextNodeList(null);
            for(int i=0;i<nextlist.size();i++)
            {
                
                switch(wf_node.nodetype)
                {
                    case NodeType.HUMAN_NODE:
                        humanlist.add(nextlist.get(i));
                        break;
                    case NodeType.END_NODE:
                        break;
               /*   default:
                        humanlist.addAll(getNextHumanNodes(wf_node)); //与发散 产生死循环
                        break;  */
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
    
    private ArrayList getPreHumanNodes(WF_Node wf_node)throws GeneralException
    {
        ArrayList humanlist=new ArrayList();
        try
        {
            ArrayList prelist=wf_node.getPreNodeList();
            for(int i=0;i<prelist.size();i++)
            {
                switch(wf_node.nodetype)
                {
                    case NodeType.HUMAN_NODE:
                        humanlist.add(prelist.get(i));
                        break;
                    default:
                        humanlist.addAll(getPreHumanNodes(wf_node));
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
    
    
    /**
     * 获得开始节点
     * @param tabid
     * @return
     */
    public RecordVo getBeginNode(String tabid)throws GeneralException
    {
        ContentDAO dao=new ContentDAO(this.conn); 
        RecordVo node_vo=new RecordVo("t_wf_node");
        int flag =0;
        try
        {
            
            int node_id=0;
            RowSet rowSet=dao.search("select node_id from t_wf_node where tabid="+tabid+" and nodetype='1'");
            if(rowSet.next()) {
                node_id=rowSet.getInt("node_id");
            } else{
                flag =1;
                
            }
            node_vo.setInt("node_id",node_id);
            node_vo=dao.findByPrimaryKey(node_vo); 
        }
        catch(Exception e)
        {
            if(flag==1){
                throw new GeneralException("开始节点没有输出变迁！");
            }else{
            e.printStackTrace();
            }
        }
        return node_vo;
    }
    
    
    /**
     * 求上节点列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getPreNodeList()throws GeneralException
    {
        ArrayList nextnodelist=new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        int node_id=-1;
        try
        {
            ArrayList translist=this.getInTransitionList();
            
            for(int i=0;i<translist.size();i++)
            {
                WF_Transition wf_trans=(WF_Transition)translist.get(i);
                node_id=wf_trans.getPre_nodeid();
                if(node_id==-1) {
                    continue;
                }
          //      RecordVo node_vo=new RecordVo("t_wf_node");
          //      node_vo.setInt("node_id",node_id);
         //       node_vo=dao.findByPrimaryKey(node_vo);
                RecordVo node_vo=TemplateStaticDataBo.getWfNodeVo(node_id,this.conn);  //20171111 邓灿，采用缓存解决并发下压力过大问题 
                WF_Node wf_node=new WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
                wf_node.setNode_id(node_vo.getInt("node_id"));
                wf_node.setNodename(node_vo.getString("nodename"));
                wf_node.setNodetype( Integer.parseInt(node_vo.getString("nodetype")));
                wf_node.setTabid(node_vo.getString("tabid"));
                nextnodelist.add(wf_node);
                if (this.tablebo!=null){
                    wf_node.setTablebo(this.tablebo);
                }
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
     * 求当前节点一步所有人工节点列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getPreHumanNodeList()throws GeneralException
    {
        ArrayList humanlist=new ArrayList();
        WF_Node wf_node=null;
        try
        {
            ArrayList prelist=this.getPreNodeList();
            if(this.nodetype!=NodeType.START_NODE)
            {
                    for(int i=0;i<prelist.size();i++)
                    {
                        wf_node=(WF_Node)prelist.get(i);
                        switch(wf_node.nodetype)
                        {
                            case NodeType.HUMAN_NODE:
                            case NodeType.START_NODE:
                                humanlist.add(prelist.get(i));
                                break;
                            default:
                                humanlist.addAll(getPreHumanNodes(wf_node));
                                break;
                        }                       
                    }//for i
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);           
        }
        return humanlist;
        
    }
    /**
     * 取得一下人工节点
     * @return
     * @throws GeneralException
     */
    public ArrayList getNextHumanNodeList()throws GeneralException
    {
        ArrayList humanlist=new ArrayList();
        WF_Node wf_node=null;
        try
        {
            ArrayList nextlist=this.getNextNodeList(null);
            switch(this.nodetype)
            {
                case NodeType.START_NODE:
                    if(nextlist.size()>1) {
                        throw new GeneralException(ResourceFactory.getProperty("error.start.outtrans"));
                    }
                    if(nextlist.size()==0) {
                        throw new GeneralException(ResourceFactory.getProperty("error.nodefinenode"));
                    }
                    wf_node=(WF_Node)nextlist.get(0);
                    if(wf_node.nodetype==NodeType.HUMAN_NODE) {
                        humanlist.add(wf_node);
                    } else
                    {
                        humanlist.addAll(getNextHumanNodes(wf_node));
                    }
                    break;
                case NodeType.END_NODE:
                    break;
                default:
                    for(int i=0;i<nextlist.size();i++)
                    {
                        wf_node=(WF_Node)nextlist.get(i);
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
    
    
    public void setNodeParam(String _tabid,int _taskid,String _tabname,TemplateTableBo _tablebo)
    {
        this.tabid=_tabid;
        this.taskid=_taskid;
        this.tablebo=_tablebo;
        this.tabname=_tabname;
    }
    
    
    /**
     * 判断下一节点是否是结束节点
     * @return
     */
    public boolean getNextNodeIsEnd(int nodeid)throws GeneralException
    {
        boolean isEnd=false;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            
            StringBuffer strsql=new StringBuffer("select  twt.*,twd.nodetype from t_wf_transition twt,t_wf_node twd  where  twt.next_nodeid=twd.node_id and pre_nodeid=");
            strsql.append(nodeid);
            RowSet rset=dao.search(strsql.toString());
            
            while(rset.next())
            { 
                int _node_id=rset.getInt("next_nodeid"); 
              //  RecordVo node_vo=new RecordVo("t_wf_node");
              //  node_vo.setInt("node_id",_node_id);
              //  node_vo=dao.findByPrimaryKey(node_vo);
                RecordVo node_vo=TemplateStaticDataBo.getWfNodeVo(_node_id,this.conn);  //20171111 邓灿，采用缓存解决并发下压力过大问题
                int nodetype= Integer.parseInt(node_vo.getString("nodetype")); 
                if(nodetype==4||nodetype==6) //与|或发散节点
                {
                    break;
                }
                else if(nodetype==5||nodetype==7) //与|或汇聚节点
                {
                    isEnd=getNextNodeIsEnd(_node_id);
                }
                else if(nodetype==9) {
                    isEnd=true;
                }
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return isEnd;
    }
    
    
    
    
    /**
     * 取得下一节点列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getNextNodeList(ArrayList seqnumList)throws GeneralException
    {
        ArrayList nextnodelist=new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        int node_id=-1;
        try
        {
            ArrayList translist=this.getOutTransitionList();
            
             
            String sql="";
            if(this.tabname.length()>0)
            {
                sql="select seqnum from "+tabname+" where 1=1 ";
                if(tabname.equalsIgnoreCase("templet_"+this.tabid))
                {
                    sql="select seqnum from templet_"+tabid+" where    exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
                    sql+=" and submitflag=1  and   td.tab_id="+this.tabid+" and td.task_id="+this.taskid ; // +" and ( td.state=0 or td.state is null ) )";
                    
                    sql+=" and ( ("+Sql_switcher.isnull("special_node","0")+"=0  and  "+Sql_switcher.isnull("state","0")+"=0  ) ";
                    sql+=" or ("+Sql_switcher.isnull("special_node","0")+"=1 and ( "+Sql_switcher.isnull("state","0")+"=0 or "+Sql_switcher.isnull("state","0")+"=1 ) )) )";
                }
                else if(tabname.equalsIgnoreCase(this.tablebo.getUserview().getUserName()+"templet_"+this.tabid))
                {
                    sql+=" and submitflag=1 ";
                    if(this.cd_whl!=null) {
                        sql+=this.cd_whl;
                    }
                    
                }
                else if(tabname.equalsIgnoreCase("g_templet_"+this.tabid))
                {
                    sql+=" and lower(basepre)='"+this.tablebo.getUserview().getDbname().toLowerCase()+"' and a0100='"+this.tablebo.getUserview().getA0100()+"'";
                }
            }
            
            RowSet rowSet=null; 
            if((seqnumList==null||seqnumList.size()==0)&&sql.length()>0)
            {
                if(seqnumList==null) {
                    seqnumList=new ArrayList();
                }
                rowSet=dao.search(sql);
                ArrayList list=new ArrayList();
                while(rowSet.next())
                {
                //  list.add(rowSet.getString("seqnum").trim());
                    seqnumList.add(rowSet.getString("seqnum").trim());
                }
                //seqnumList=list;
            }
             
            for(int i=0;i<translist.size();i++)
            {
                WF_Transition wf_trans=(WF_Transition)translist.get(i);
                node_id=wf_trans.getNext_nodeid();
                if(node_id==-1) {
                    continue;
                }
            //    RecordVo node_vo=new RecordVo("t_wf_node");
            //    node_vo.setInt("node_id",node_id);
           //     node_vo=dao.findByPrimaryKey(node_vo);
                
                RecordVo node_vo=TemplateStaticDataBo.getWfNodeVo(node_id,this.conn);  //20171111 邓灿，采用缓存解决并发下压力过大问题
                
                
                WF_Node wf_node=new WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
                wf_node.setNode_id(node_vo.getInt("node_id"));
                wf_node.setNodename(node_vo.getString("nodename"));
                wf_node.setNodetype( Integer.parseInt(node_vo.getString("nodetype")));
                wf_node.setTabid(node_vo.getString("tabid"));
                wf_node.setExt_param(node_vo.getString("ext_param"));
                
            /*
                if((seqnumList==null||seqnumList.size()==0)&&sql.length()>0)
                {
                    rowSet=dao.search(sql);
                    ArrayList list=new ArrayList();
                    while(rowSet.next())
                        list.add(rowSet.getString("seqnum").trim());
                    wf_node.setSeqnumList(list);
                }
                else 
                */
                if(seqnumList!=null&&seqnumList.size()>0) {
                    wf_node.setSeqnumList(seqnumList);
                }
                if (this.tablebo!=null){
                    wf_node.setTablebo(this.tablebo);
                }
                nextnodelist.add(wf_node);
            }// for loop end
            
            if(rowSet!=null) {
                rowSet.close();
            }
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return nextnodelist;
    }
    
    /**
     * 从系统邮件服务器设置中得到发送邮件的地址
     * @return
     */
    public String getFromAddr() throws GeneralException 
    {
        String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
            Document doc = PubFunc.generateDom(param);;
            Element root = doc.getRootElement();
            Element stmp=root.getChild("stmp"); 
            str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
    }
    
    
    
    String fromOri="0"; //0：不处理 1: 人员还没写入templet_xx表中，发送消息时得关联xxxtemplet_xx 或  g_templet_xxx表中的数据
    /**
     * 给当前审批人发送邮件及短信
     * @throws GeneralException
     */
    public void sendMessage(WF_Actor actor,int task_id,int node_id)throws GeneralException
    {
    	this.sendMessage(actor, task_id, node_id,"");
    }
    public void sendMessage(WF_Actor actor,int task_id,int node_id,String ydNodeWarnJob)throws GeneralException
    {
        try
        {
            
            String strc=""; 
            String name="";
            ContentDAO dao=new ContentDAO(this.conn);
            if("2".equals(opt)) //驳回
            {
                
                strc=actor.getContent();
                if(strc==null) {
                    strc="";
                }
                name=this.tablebo.getTable_vo().getString("name")+"("+ResourceFactory.getProperty("info.appleal.state2")+")";
                
            }
            else
            {
                name=this.tablebo.getTable_vo().getString("name");
            } 
            SendMessageBo sendMessageBo=new SendMessageBo(this.conn,tablebo.getUserview());
            sendMessageBo.setbSelfApply(this.bSelfApply);
            sendMessageBo.setSendresource(this.sendresource);
            if(task_id!=0)
            {
                sendMessageBo.setTask_id(String.valueOf(task_id));
                sendMessageBo.setIns_id(String.valueOf(this.ins_vo.getInt("ins_id")));
                sendMessageBo.setSp_flag("1");
            } 
            String title=this.tablebo.getTable_vo().getString("name");
            //邮件标题取自任务名称 wangrd 20151111
            if (this.task_vo!=null){
            	title=this.task_vo.getString("task_topic");
            }
            String context=name+strc;
          
            if(actor==null)
            { 
                return;
            }  
            
            String dbase="";//库前缀
            String a0100="";//人员编号
            String toAddr="";
            String toPhone="";
            /**人员编码Usr00000001*/
            String strid=actor.getActorid();
            if(strid==null|| "".equals(strid)) {
                return;
            }
            
            //zxj 20141023  邮件模板附件
            ArrayList attachList = null; 
            String template_sp="";
            SendMessageBo sendBo=null;
            if(this.tablebo.isBemail()||this.tablebo.isBsms() )
            {
                
                template_sp=(String)tablebo.getTemplate_sp();
                sendBo=new SendMessageBo(this.conn,this.tablebo.getUserview());
                sendBo.setbSelfApply(this.bSelfApply);
                if(task_id!=0) {
                    sendBo.setTask_id(String.valueOf(task_id));
                }
                sendBo.setIns_id(String.valueOf(this.ins_vo.getInt("ins_id")));
                sendBo.setSp_flag("1");
                if(!"2".equals(opt)&&template_sp!=null&&template_sp.trim().length()>0) //获得审批模板
                {
                    LazyDynaBean mailInfo=sendBo.getTemplateMailInfo(template_sp);
                    attachList = (ArrayList)mailInfo.get("attach"); //zxj 20141023 邮件模板附件
                    context=(String)mailInfo.get("content")==null?"":(String)mailInfo.get("content");
                    String template_emailAddress=(String)mailInfo.get("address");
                    String template__set=(String)mailInfo.get("set");
                    String subject =  mailInfo.get("subject")==null?"":(String)mailInfo.get("subject");
                    
                    if(subject.trim().length()>0){
                      //  title = subject;
                    }
                    
                    sendMessageBo.setTemplate__set(template__set);
                    sendMessageBo.setTemplate_emailAddress(template_emailAddress); 
                }
            }
            
            
            
            ArrayList objList=new ArrayList();
            if("1".equals(actor.getActortype()))
            {
                if(strid.length()>3)
                {
                    dbase=strid.substring(0,3);
                    a0100=strid.substring(3);
                    
                    LazyDynaBean a_bean=new LazyDynaBean();
                    a_bean.set("a0100",strid);
                    a_bean.set("email","");
                    a_bean.set("phone","");
                    a_bean.set("status","1");
                    objList.add(a_bean);
                }
                
            }
            /**operuser中的用户*/
            if("4".equals(actor.getActortype()))
            {
                RecordVo vo=new RecordVo("operuser");

                vo.setString("username",strid);
                vo=dao.findByPrimaryKey(vo);
                dbase=vo.getString("nbase");
                a0100=vo.getString("a0100");
                
                LazyDynaBean a_bean=new LazyDynaBean();
                a_bean.set("email","");
                a_bean.set("phone","");
            //  if(a0100==null||a0100.equalsIgnoreCase(""))
                {
 
                    toAddr=vo.getString("email");
                    toPhone=vo.getString("phone");
                    a_bean.set("email",vo.getString("email"));
                    a_bean.set("phone",vo.getString("phone"));
                    a_bean.set("status","0");
                    a_bean.set("a0100",strid);
                    if(a0100!=null&&a0100.trim().length()>0)
                    {
                        a_bean.set("relation_a0100",a0100);
                        a_bean.set("relation_dbase",dbase);
                    }
                } 
                objList.add(a_bean);
            }
            if("2".equals(actor.getActortype()))//角色
            {
 
                ArrayList alist=sendMessageBo.findUserListByRoleId(strid,sendMessageBo.getTemplate_emailAddress(),sendMessageBo.getTemplate__set()); 
                //角色特征为部门领导、单位领导时再过滤一次 wangrd 2015-07-14
                alist=filterRoleMemberList(dao,strid,node_id,task_id,alist); 
                LazyDynaBean abean=null;
                HashMap map = new HashMap();
                ArrayList userlist =getUserflag(""+node_id,""+task_id,map);
                ArrayList emaillist = new ArrayList();
                for(int i=0;i<alist.size();i++)//需要考虑接受范围
                {
                    LazyDynaBean a_bean=new LazyDynaBean();
                    abean=(LazyDynaBean)alist.get(i); 
                    boolean flag =true;
                    if(map.isEmpty()&&userlist.size()>0){
                         flag = getUserflag2(""+node_id,""+task_id,abean,userlist,strid);
                    }
                    if(!flag) {
                        continue;
                    }
                    //接收范围内的用户   
                    a_bean.set("a0100",(String)abean.get("a0100"));
                    if(abean.get("email")!=null) {
                        a_bean.set("email",(String)abean.get("email"));
                    } else {
                        a_bean.set("email","");
                    }
                    if(abean.get("phone")!=null) {
                        a_bean.set("phone",(String)abean.get("phone"));
                    } else {
                        a_bean.set("phone","");
                    }
                    String status=(String)abean.get("status");
                    
                    a_bean.set("status",status);
                    if(emaillist.contains(abean.get("email"))) {
                    	continue;
                    }else {
                    	emaillist.add(abean.get("email"));
                    }	
                    objList.add(a_bean);
                }
            }else if("5".equals(actor.getActortype())) //本人//待改
            {
            	/**syl 52539 招商蛇口：在流程定义-设置拆单模式，按单据中人员数据的姓名来设置（详情见图“拆单设置”），设置完成后，在人事异动发送面试通知业务，选择多个人来进行发送面试通知，一个人可收到包括自己和其他人的邮件（详情见图“邮件1”、“邮件2”，此此处用的是测试数据）*/
                /**拆单模式下，首次发起，会拆单成一一个个单据，然后一个个进入到中间表，且发送信息，会有如上情况*/
            	ArrayList alist= sendMessageBo.findUserListBySeqNum(this.tabid,""+task_id,"5",this.cd_whl);
                LazyDynaBean abean=null;
                for(int i=0;i<alist.size();i++)
                {
                    LazyDynaBean a_bean=new LazyDynaBean();
                    abean=(LazyDynaBean)alist.get(i);
                    
                    a_bean.set("a0100",(String)abean.get("a0100"));                    
                    if(abean.get("email")!=null) {
                        a_bean.set("email",(String)abean.get("email"));
                    } else {
                        a_bean.set("email","");
                    }
                    if(abean.get("phone")!=null) {
                        a_bean.set("phone",(String)abean.get("phone"));
                    } else {
                        a_bean.set("phone","");
                    }
                    String status=(String)abean.get("status");
                    
                    a_bean.set("status",status);
                    objList.add(a_bean);
                }
            }
            else  if("3".equals(actor.getActortype()))
            {
                String codesetid=actor.getActorid().substring(0,2);
                String codeitemid=actor.getActorid().substring(2);
                ArrayList alist=sendMessageBo.findUserListByOrgId(codeitemid,codesetid,sendMessageBo.getTemplate_emailAddress(),sendMessageBo.getTemplate__set());
                LazyDynaBean abean=null;
                for(int i=0;i<alist.size();i++)
                {
                    abean=(LazyDynaBean)alist.get(i);
                    LazyDynaBean a_bean=new LazyDynaBean();
                    a_bean.set("a0100",(String)abean.get("a0100"));
                    a_bean.set("email",(String)abean.get("email"));
                    a_bean.set("phone",(String)abean.get("phone"));
                    a_bean.set("status","1");
                    objList.add(a_bean);
                }
            }
            
            if(objList.size()==0) {
                return;
            }
            
            /** 中建接口 */
            
            boolean flag=false;
            /**人事异动安全改造,将发送邮件的taskid加密处理**/
            String pendingCode="HRMS-"+PubFunc.encrypt(String.valueOf(task_id)); 
            
            //在待办任务中判断模板设置的展现方式，以默认显示 20150923 liuzy
            int tab_id=Integer.parseInt(this.tabid);
            TemplateUtilBo tb=new TemplateUtilBo(this.conn,this.tablebo.getUserview());
            String view = tb.getTemplateView(tab_id);
            String url="";
        	if(view!=null&& "list".equalsIgnoreCase(view)){
         		url="/general/template/templatelist.do?b_init=init&isInitData=1&sp_flag=1&pre_pendingID="+pendingCode+"&ins_id="+this.ins_vo.getInt("ins_id")+"&returnflag=3&task_id="+(String.valueOf(task_id))+"&tabid="+this.tabid+"&index_template=1&appfwd=1";
        	}else {
        		url="/general/template/edit_form.do?b_query=link&businessModel=0&tabid="+this.tabid+"&pre_pendingID="+pendingCode+"&ins_id="+this.ins_vo.getInt("ins_id")+"&taskid="+PubFunc.encrypt((String.valueOf(task_id)))+"&sp_flag=1&returnflag=3&appfwd=1";
        	}
            if (PubFunc.isUseNewPrograme(this.tablebo.getUserview())){
                String approve_flag="1";
                String newUrl ="/module/template/templatemain/templatemain.html?b_query=link&task_id="
                    +PubFunc.encrypt(String.valueOf(task_id))+"&tab_id="+tabid+"&return_flag=13"
                    +"&approve_flag="+approve_flag
                    +"&pre_pendingID="+pendingCode;
                url="/module/utils/jsp.do?br_query=link&param="
                    +SafeCode.encode(newUrl)  ;
            }
            //String url="/general/template/edit_form.do?b_query=link&businessModel=0&tabid="+this.tabid+"&pre_pendingID="+pendingCode+"&ins_id="+this.ins_vo.getInt("ins_id")+"&taskid="+PubFunc.encrypt((String.valueOf(task_id)))+"&sp_flag=1&returnflag=3&appfwd=1";
            ArrayList manList=new ArrayList();
            if(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")))
            {   
                flag=true;
                
            }
            
            
            
            //普天代办
            //  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
            {
                if(StringUtils.isBlank(ydNodeWarnJob)){//催办不在往第三方接口发信息。
	                if("2".equals(actor.getActortype())||"3".equals(actor.getActortype())||"5".equalsIgnoreCase(actor.getActortype()))//角色   20150811增加本人  20200604 增加组织单元
	                {
	                    MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
	                    for(int i=0;i<objList.size();i++)
	                    {
	                        LazyDynaBean a_bean=(LazyDynaBean)objList.get(i);
	                        String status=(String)a_bean.get("status");
	                        String _a0100=(String)a_bean.get("a0100");
	                        if("1".equals(status)|| "5".equals(status))
	                        {
	                            String pre_pendingID="";
	                            if(this.otherParaMap.get("pre_pendingID")!=null) {
                                    pre_pendingID=(String)this.otherParaMap.get("pre_pendingID");
                                }
	                            int dealFlag=1;
	                            if ("2".equals(this.opt)) {
                                    dealFlag =2;//驳回的单据，待办标题需体现驳回信息。wangrd 20150829
                                }
	                            sysBo.sendDealWithInfo(pre_pendingID, dealFlag,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(task_id)), _a0100.substring(0,3),_a0100.substring(3));
	                        }
	                    }
	                    
	                }
	                else
	                {
	                    if(a0100!=null&&a0100.trim().length()>0)
	                    {
	                        MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
	                        String pre_pendingID="";
	                        if(this.otherParaMap.get("pre_pendingID")!=null) {
                                pre_pendingID=(String)this.otherParaMap.get("pre_pendingID");
                            }
	                        int dealFlag=1;
	                        if ("2".equals(this.opt)) {
                                dealFlag =2;
                            }
	                        sysBo.sendDealWithInfo(pre_pendingID, dealFlag,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(task_id)), dbase, a0100);
	                        
	                    }
	                }
                }
            }
                
            
            
            
            
            
            
            if(this.tablebo.isBemail())
            {
                //中建oa接口
                if(flag&&a0100!=null&&a0100.length()>0)
                {
                    try
                    {
                        LazyDynaBean a_bean=new LazyDynaBean();
                        a_bean.set("nbase",dbase);
                        a_bean.set("a0100",a0100); 
                        String username=""; //登陆用户名
                        String password=""; //登陆密码
                        RowSet rowSet=null;
                        if("4".equals(actor.getActortype()))
                        {
                            rowSet=dao.search("select * from operuser where username='"+actor.getActorid()+"'");
                            if(rowSet.next())
                            {
                                username=rowSet.getString("username");
                                password=rowSet.getString("password");
                            }
                        }
                        else
                        {
                            AttestationUtils utils=new AttestationUtils();
                            LazyDynaBean fieldbean=utils.getUserNamePassField();
                            String username_field=(String)fieldbean.get("name");
                            String password_field=(String)fieldbean.get("pass");
                            StringBuffer sql=new StringBuffer("");
                            sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+dbase+"A01");
                            sql.append(" where a0100='"+a0100+"'");
                            rowSet=dao.search(sql.toString());
                            if(rowSet.next())
                            {
                                username=rowSet.getString("username");
                                password=rowSet.getString("password");
                            }
                        }
                        if(rowSet!=null) {
                            rowSet.close();
                        }
                        
                        if(password==null) {
                            password="";
                        }
                        String etoken=PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
                        url+="&etoken="+etoken+"&validatepwd=false";
                        
                        //url="http://cscec-oa.cscec.com.cn/oahr.asp?tabid="+this.tabid+"&etoken="+etoken+"&ins_id="+this.ins_vo.getInt("ins_id")+"&taskid="+task_id+"&type=2";
                        
                        a_bean.set("url", url);
                        manList.add(a_bean);
                        
                        
                        LazyDynaBean sendBean=null;
                        if(this.tablebo!=null&&this.tablebo.getUserview()!=null&&this.tablebo.getUserview().getA0100().length()>0)
                        {
                            sendBean=new LazyDynaBean();
                            sendBean.set("nbase",this.tablebo.getUserview().getDbname());
                            sendBean.set("a0100",this.tablebo.getUserview().getA0100());
                        }
                        
                        SendEmailFormOA oa=new SendEmailFormOA(this.conn);
                        oa.sendEmail(manList,sendBean,name, name+strc);
                    }
                    catch(Exception e)
                    {
                        
                    }
                }
                
                
                
                if(!flag)
                {
                    //EMailBo bo =null;
                    AsyncEmailBo bo = null;
                    try
                    {
                        //bo=new EMailBo(this.conn,true,"");
                        bo = new AsyncEmailBo(this.conn, this.tablebo.getUserview());
                    }
                    catch(Exception e)
                    {
                        return;
                    }
                    String fromaddr=this.getFromAddr();
                    ArrayList emailBeanList = new ArrayList();
                    for(int i=0;i<objList.size();i++)
                    {
                        
                        LazyDynaBean abean=(LazyDynaBean)objList.get(i);
                        String status=(String)abean.get("status");
                        String email=(String)abean.get("email");
                        String _context=context.replace("\r\n","<br>").replace("\n","<br>").replace("\r","<br>");
                        LazyDynaBean _bean =null;
                        
                        if(!"2".equals(opt)&&template_sp!=null&&template_sp.trim().length()>0) //获得审批模板
                        {
                            String _sql=objs_sql;
                            if("0".equals(this.fromOri)&&isStartWorkflow) //流程发起且人员信息已提交到临时表中 2014-7-16  邓灿
                            { 
                                StringBuffer strsql=new StringBuffer("");
                                strsql.append("select * from templet_"+tabid); 
                                strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
                                strsql.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=0 ) ");
                                _sql=strsql.toString();
                            }else if("0".equals(this.fromOri)){    //流程发起后是空节点的情况，sql语句需要重新赋值 20150728 liuzy
                            	RowSet rowSet=null;
                            	if (_sql!=null && _sql.length()>0) {
                                    rowSet=dao.search(_sql);
                                }
                              if((_sql==null)|| _sql.length()<1 ||!rowSet.next()){
                            		StringBuffer strsql=new StringBuffer("");
	                                strsql.append("select templet_"+tabid+".* from templet_"+tabid); 
	                                strsql.append(",t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum and templet_"+tabid+".ins_id=td.ins_id ");
	                                strsql.append("and td.task_id="+task_id);
	                                _sql=strsql.toString();
                            	}
                            } 
                            
                            _bean = sendBo.getEmailBean("2",null,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql);
                            _context = (String)_bean.get("bodyText");
                        }
                        
                        if("2".equals(opt)||template_sp==null||template_sp.trim().length()==0){//要么是驳回的单据,要么是报批的单据没有定义邮件模板
                            String _sql=objs_sql;
                            if("0".equals(this.fromOri)&&isStartWorkflow) //流程发起且人员信息已提交到临时表中 2014-7-16  邓灿
                            { 
                                StringBuffer strsql=new StringBuffer("");
                                strsql.append("select * from templet_"+tabid); 
                                strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
                                strsql.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=0 ) ");
                                _sql=strsql.toString();
                            } 
                            if(StringUtils.isNotBlank(ydNodeWarnJob)){//ydNodeWarnJob为空或者null说明可能是老程序掉的，访问老接口
                            	_bean = sendBo.getTileAndContent(opt,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql,this.getSeqnumList(),this.tablebo.getInfor_type(),ydNodeWarnJob);
                            }else{
                            	_bean = sendBo.getTileAndContent(opt,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql,this.getSeqnumList(),this.tablebo.getInfor_type());
                            }
                        }
                        if("1".equals(status)||"5".equals(status))
                        {
                            String str=(String)abean.get("a0100");
                            String toaddr="";
                            String objectId ="";
                            String _dbase=str.substring(0,3);
                            String _a0100=str.substring(3);
                            if(sendMessageBo.getTemplate_emailAddress()!=null&&sendMessageBo.getTemplate_emailAddress().trim().length()>0)
                            {
                                
                                toaddr=sendMessageBo.getEmailAddress(_dbase,_a0100);
                            }
                            objectId =_dbase+_a0100;
                            _bean.set("objectId", objectId);
                            if(toaddr.trim().length()>0){
                                _bean.set("toAddr", toaddr);
                            }else{
                                _bean.set("objectId", objectId);
                            }
                            
                            
                        //  toaddr=bo.getEmailAddrByA0100(str);
//                            if(toaddr!=null&&toaddr.trim().length()>0&&toaddr.indexOf("@")!=-1)
//                                bo.sendEmail(title, _context, attachList, fromaddr, toaddr);//zxj 20141023  带附件
                        }
                        else
                        {
                            if(abean.get("relation_a0100")!=null&&abean.get("relation_dbase")!=null)
                            {
                                String str=(String)abean.get("relation_dbase")+(String)abean.get("relation_a0100"); 
                                String objectId ="";
                                String toaddr="";
                                String _dbase=str.substring(0,3);
                                String _a0100=str.substring(3);
                                if(sendMessageBo.getTemplate_emailAddress()!=null&&sendMessageBo.getTemplate_emailAddress().trim().length()>0)
                                {
                                    
                                    toaddr=sendMessageBo.getEmailAddress(_dbase,_a0100);
                                }
                                objectId =_dbase+_a0100;
                                _bean.set("objectId", objectId);
                                if(toaddr.trim().length()>0){
                                    _bean.set("toAddr", toaddr);
                                }else{
                                    _bean.set("objectId", objectId);
                                }
                            }else{                //当业务用户没有关联自助用户时，取业务用户的邮箱地址，20150807 liuzy
                            	if(email!=null&&!"".equals(email)) {
                                    _bean.set("toAddr", email);
                                }
                            }
                        }
                        /**处理如果有附件的话那么就添加附件**/
                        if(attachList!=null&&attachList.size()>0){
                            _bean.set("attachList", attachList);
                        }
                        emailBeanList.add(_bean);
                    }
                  bo.send(emailBeanList);
                  //发送微信信息
                  new TemplateUtilBo(this.conn,this.tablebo.getUserview()).sendWeixinMessageFromEmail(emailBeanList);
                  
                }
            }
            if(this.tablebo.isBsms())
            {
                 

                SmsBo smsbo=new SmsBo(this.conn);
                for(int i=0;i<objList.size();i++)
                {
                    
                    LazyDynaBean abean=(LazyDynaBean)objList.get(i);
                    String phone=(String)abean.get("phone");
                    String status=(String)abean.get("status");
                    
                    String _context=context;
                    if(!"2".equals(opt)&&template_sp!=null&&template_sp.trim().length()>0) //获得审批模板
                    {
                        sendBo.setSmsContext(true);
                        String _sql=objs_sql;
                        if("0".equals(this.fromOri)&&isStartWorkflow)  //流程发起且人员信息已提交到临时表中 2014-7-16  邓灿
                        { 
                            StringBuffer strsql=new StringBuffer("");
                            strsql.append("select * from templet_"+tabid); 
                            strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
                            strsql.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=0 ) ");
                            _sql=strsql.toString();
                        }else if("0".equals(this.fromOri)){    //流程发起后是空节点的情况，sql语句需要重新赋值 20150728 liuzy
                        	RowSet rowSet=null;
                        	if (_sql!=null && _sql.length()>0) {
                                rowSet=dao.search(_sql);
                            }
                          if((_sql==null)|| _sql.length()<1 ||!rowSet.next()){
                        		StringBuffer strsql=new StringBuffer("");
                                strsql.append("select templet_"+tabid+".* from templet_"+tabid); 
                                strsql.append(",t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum and templet_"+tabid+".ins_id=td.ins_id ");
                                strsql.append("and td.task_id="+task_id);
                                _sql=strsql.toString();
                        	}
                        } 
                        LazyDynaBean _bean=sendBo.getEmailBean("2",null,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql);
                        _context=(String)_bean.get("bodyText");
                        sendBo.setSmsContext(false);
                    }else{//驳回或者说是没有定义模版
                        String _sql=objs_sql;
                        if("0".equals(this.fromOri)&&isStartWorkflow) //流程发起且人员信息已提交到临时表中 2014-7-16  邓灿
                        { 
                            StringBuffer strsql=new StringBuffer("");
                            strsql.append("select * from templet_"+tabid); 
                            strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
                            strsql.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=0 ) ");
                            _sql=strsql.toString();
                        } 
                        LazyDynaBean  _bean =new LazyDynaBean();
                        
                        if(StringUtils.isNotBlank(ydNodeWarnJob)){//ydNodeWarnJob为空或者null说明可能是老程序掉的，访问老接口
                        	_bean = sendBo.getTileAndContent(opt,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql,this.getSeqnumList(),this.tablebo.getInfor_type(),ydNodeWarnJob);
                        }else{
                        	_bean = sendBo.getTileAndContent(opt,title,_context,(String)abean.get("a0100"),this.tablebo.getUserview(),tabid,_sql,this.getSeqnumList(),this.tablebo.getInfor_type());
                        }
                        _context=(String)_bean.get("bodyText");
                    }
                    
                    _context=_context.replaceAll("<br>","\n");
                    _context=_context.replaceAll("<br/>","\n");
                    _context=_context.replaceAll("&nbsp;"," ");
                    
                    if("1".equals(status))
                    {
                        String str=(String)abean.get("a0100");
                        String _dbase=str.substring(0,3);
                        String _a0100=str.substring(3);
                        smsbo.sendMessage(this.tablebo.getUserview(),_dbase+_a0100,_context);
                    }
                    else
                    {
                        smsbo.setBflag(true);
                        smsbo.sendMessage(this.tablebo.getUserview(),phone,_context);
                    }
                }
            
                
            }
            
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            //throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    /**
     * 根据节点id取得流程节点参与者的信息
     * @param node_id
     * @return
     */
    public WF_Actor getWf_Actor(int node_id)
    {
        WF_Actor wf_actor=null;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select * from  t_wf_actor where node_id="+node_id);
            if(rowSet.next())
            {
                wf_actor=new WF_Actor(rowSet.getString("actorid"),rowSet.getString("actor_type")) ;
                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return wf_actor;
    }
    
    
    /**
     * 给当前审批人发送邮件及短信
     * @throws GeneralException
     */
    private void sendMessage(WF_Actor actor)throws GeneralException
    {
        String dbase="";//库前缀
        String a0100="";//人员编号
        /**人员编码Usr00000001*/
        String strid=actor.getActorid();
        if(strid==null|| "".equals(strid)) {
            return;
        }
        
        try
        {
            if("1".equals(actor.getActortype())&&strid.length()>3)
            {
                dbase=strid.substring(0,3);
                a0100=strid.substring(3);
            }
            ContentDAO dao=new ContentDAO(this.conn);
            String toAddr="";
            String toPhone="";
            /**operuser中的用户*/
            if("4".equals(actor.getActortype()))
            {
                RecordVo vo=new RecordVo("operuser");

                vo.setString("username",strid);
                vo=dao.findByPrimaryKey(vo);
                dbase=vo.getString("nbase");
                a0100=vo.getString("a0100");
                if(a0100==null|| "".equalsIgnoreCase(a0100))
                {
//                  return;
                    toAddr=vo.getString("email");
                    toPhone=vo.getString("phone");
                }
            }
            String strc=this.tablebo.getRecordBusiTopic("");
            String name=this.tablebo.getTable_vo().getString("name");
            
            if("2".equals(actor.getActortype())|| "3".equals(actor.getActortype())) {
                return;
            }
            if(this.tablebo.isBemail())
            {
                EMailBo bo = new EMailBo(this.conn,true,"");
                String fromaddr=this.getFromAddr();
                if((a0100==null|| "".equalsIgnoreCase(a0100))&&toAddr!=null&&toAddr.length()>0&&toAddr.indexOf("@")!=-1)
                {
                    //EMailBo emailbo=new EMailBo(this.conn,true,dbase);
                    bo.sendEmail(name,name+strc,"",fromaddr,toAddr);
                    //emailbo.sendEmail2(name,name+strc,null,this.tablebo.getUserview(),toAddr);
                }
                else if(dbase.trim().length()>0&&a0100.trim().length()>0)
                {
                    String toaddr=bo.getEmailAddrByA0100(dbase+a0100);
                    if(toaddr!=null&&toaddr.trim().length()>0&&toaddr.indexOf("@")!=-1) {
                        bo.sendEmail(name,name+strc,"",fromaddr,toaddr);
                    }
                    //EMailBo emailbo=new EMailBo(this.conn,false,dbase);
                    //emailbo.sendEmail2(name,name+strc,null,this.tablebo.getUserview(),a0100);
                }
            }
            if(this.tablebo.isBsms())
            {
                 

                SmsBo smsbo=new SmsBo(this.conn);
                if((a0100==null|| "".equalsIgnoreCase(a0100))&&toPhone!=null&&toPhone.length()>0)
                {
                    smsbo.setBflag(true);
                    smsbo.sendMessage(this.tablebo.getUserview(),toPhone,name+strc);
                }
                else if(a0100.trim().length()>0&&dbase.trim().length()>0) {
                    smsbo.sendMessage(this.tablebo.getUserview(),dbase+a0100,name+strc);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            //throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    
    /**
     * 设置下一步任务处理对象(不发邮件)
     * @param taskvo
     * @throws GeneralException
     */
    private void updateTaskActor_nomail(RecordVo taskvo,WF_Actor actor)throws GeneralException
    {
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            if(actor!=null)
            {
                if(!actor.isBexchange())//不支持同一节点任意参与者的流转
                {
                    return;
                }
                taskvo.setString("actorid",actor.getActorid());//当前对象   流程定义的参与者     
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
                {
                    return;
                }
                taskvo.setString("actorid",actor.getActorid());//当前对象   流程定义的参与者     
                taskvo.setString("actor_type",actor.getActortype());
                taskvo.setString("task_pri",actor.getEmergency());
                taskvo.setString("actorname",actor.getActorname());
                dao.updateValueObject(taskvo);
                //sendMessage(actor);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
     
    /**
     * 处理空节点
     * @param taskvo_temp
     * @param instancevo
     * @param actor
     * @throws GeneralException
     */
    private void handleNullNode(RecordVo  taskvo_temp,RecordVo instancevo,WF_Actor actor)throws GeneralException
    {
        try
        {
            WF_Node nextNode=getNextNode(taskvo_temp.getInt("node_id"),taskvo_temp.getInt("task_id")); //获得下一节点类型
            if(nextNode.getNodetype()==4||nextNode.getNodetype()==6)//=4与发散 =6或发散
            {
                nextNode.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                this.taskid=this.task_vo.getInt("task_id");
                nextNode.setWorkflowBo(this.workflowBo);
                nextNode.setObjs_sql(this.objs_sql);
                nextNode.setTask_vo(this.task_vo);
                nextNode.setStartWorkflow(this.isStartWorkflow);
                nextNode.createTask(instancevo,actor,null);  
                this.setNexTTask_vo(nextNode.getNexTTask_vo());
            }
            else
            {
                nextNode.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                nextNode.setWorkflowBo(this.workflowBo);
                nextNode.setTask_vo(this.task_vo);
                nextNode.setObjs_sql(this.objs_sql);
                nextNode.setStartWorkflow(this.isStartWorkflow);
                nextNode.createTask(instancevo,actor,nextNode.getSeqnumList()); 
                this.setNexTTask_vo(nextNode.getNexTTask_vo());
                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    
    /**
     *  按自定义条件发送报备信息
     * @param nextNode
     * @param task_vo
     */
    public boolean  sendFilingTasks_manual(RecordVo task_vo) throws GeneralException
    {
        boolean flag=false;
        try
        {
            
            /*if(this.tablebo.getInfor_type()==2 || this.tablebo.getInfor_type()==3){//单位和岗位暂时忽略报备   郭峰
                return false;
            }*/
            int _task_id=task_vo.getInt("task_id");
            ContentDAO dao=new ContentDAO(this.conn); 
            String objs=getFilingObjs(_task_id,this.tablebo.getTabid());
            String tabname="templet_"+this.tabid;
            
            String sql="select * from "+tabname+" where 1=1 ";
            sql="select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
            sql+=" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+_task_id+" and td.state<>3 )";
            
            ArrayList seqnumList=new ArrayList();
            ArrayList realnumList = new ArrayList();
            RowSet rowSet=dao.search(sql);
            
            String a0101 = "";
            if(this.tablebo.getInfor_type()==1) {
                a0101="a0101_1";
            } else {
                a0101="codeitemdesc_1";
            }
            
            /*String nbase="";
            if(!this.tablebo.getDestBase().equals(""))
            {
            	nbase=this.tablebo.getDestBase();
            }*/
            if(this.tablebo!=null&&this.tablebo.getOperationtype()==0)//调入
            {
                a0101="a0101_2";
            }
            while(rowSet.next())
            {
                seqnumList.add(rowSet.getString("seqnum")+"`"+rowSet.getString(a0101)); 
                realnumList.add(rowSet.getString("seqnum"));
            } 
            
         
            LazyDynaBean bean=null;
            RecordVo _task_vo=new RecordVo("t_wf_task"); 
            _task_vo.setString("bs_flag", "3"); 
            _task_vo.setInt("bread",0); 
            _task_vo.setString("task_state","3");
            _task_vo.setString("state","08");
            _task_vo.setString("task_type","2"); 
            _task_vo.setInt("node_id",this.node_id);
            _task_vo.setInt("ins_id",this.ins_vo.getInt("ins_id"));
            _task_vo.setDate("start_date",new Date()); 
            _task_vo.setString("task_pri","1");
            _task_vo.setString("params","1"); 
            _task_vo.setString("pri_task_id",String.valueOf(_task_id));
            
            boolean email_staff=this.tablebo.isEmail_staff();
            SendMessageBo sendMessageBo=new SendMessageBo(this.conn,tablebo.getUserview());
            sendMessageBo.setBusinessModel("61");
            sendMessageBo.setSendresource(this.sendresource);
            String template_bos=tablebo.getTemplate_bos();    ////业务办理人员的邮件模板
            sendMessageBo.setIns_id(String.valueOf(this.ins_vo.getInt("ins_id")));
            sendMessageBo.setSp_flag("3");
            
            MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
            
            IDGenerator idg=new IDGenerator(2,this.conn);       
            //EMailBo emailBo=null;
            AsyncEmailBo newEmailBo = null;
            LazyDynaBean emialInfo_bos=null;
            LazyDynaBean emailInfo_staff=null;
            HashMap columnMap=new HashMap();
            if(!"0".equals(isSendMessage)||(this.email_staff_value!=null&& "1".equals(this.email_staff_value.trim())))
            {
                try
                {
                    //emailBo=new EMailBo(this.conn,true,"");
                    newEmailBo = new AsyncEmailBo(this.conn, this.tablebo.getUserview());
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
                
                if(this.email_staff_value!=null&& "1".equals(this.email_staff_value.trim()))
                {
                    rowSet=dao.search(sql);
                    ResultSetMetaData md=rowSet.getMetaData();
                    for(int i=0;i<md.getColumnCount();i++)
                    {
                        int columnType=md.getColumnType(i+1);   
                        String columnName=md.getColumnName(i+1).toLowerCase();
                        if(columnType==java.sql.Types.TIMESTAMP||columnType==java.sql.Types.DATE||columnType==java.sql.Types.TIME) {
                            columnMap.put(columnName, "D");
                        } else {
                            columnMap.put(columnName, "A");
                        }
                    }
                }
            }
            
            String  fillUsers=this.user_h_s;//wangrd  2015-03-04 防止有缓存的问题 
            if(objs!=null&&objs.trim().length()>0) {
                fillUsers+=objs;
            }
            if(fillUsers!=null&&fillUsers.trim().length()>0)
            {
                String[] users=fillUsers.split(",");
                HashSet set=new HashSet();
                for(int i=0;i<users.length;i++)
                {
                    if(users[i]!=null&&users[i].trim().length()>0) {
                        set.add(users[i]);
                    }
                }
                
                String topic="";
                for(int j=0;j<seqnumList.size();j++)
                {
                    if(j<3) {
                        topic+=((String)seqnumList.get(j)).split("`")[1]+",";
                    }
                }
             
                if(this.tablebo.getInfor_type()==1) {
                    _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)");
                } else if(this.tablebo.getInfor_type()==2) {
                    _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)");
                } else {
                    _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)");
                }
                
                flag=true;
                
                String a0100=this.tablebo.getUserview().getA0100();
                if(a0100==null||a0100.length()==0){
                    a0100=this.tablebo.getUserview().getUserName();
                    _task_vo.setString("a0100_1",a0100);//发送人
                }else{
                	_task_vo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//发送人
                }
                if(this.actorname.length()>0) {
                    _task_vo.setString("a0101_1",this.actorname);
                } else {
                    _task_vo.setString("a0101_1",this.tablebo.getUserview().getUserFullName());//发送人姓名
                }
                
                for(Iterator t=set.iterator();t.hasNext();)
                {
            
                    String temp=(String)t.next();
                    if(temp.trim().length()==0) {
                        continue;
                    }
                    String[] temps=temp.split(":");  
                    if (temps.length<2) {
                        continue;
                    }
                    int taskId=Integer.parseInt(idg.getId("wf_task.task_id"));
                    _task_vo.setInt("task_id",taskId);
                    _task_vo.setString("task_id_pro",","+taskId+task_vo.getString("task_id_pro")); 
                    _task_vo.setString("actor_type", temps[0]);
                    _task_vo.setString("actorid", temps[1]);
                    dao.addValueObject(_task_vo);
                    
                    String recivenbase="";
                    String recivea0100="";  //这里只处理了自助用户的接收人，业务用户未处理
                    if(temps[1].length()>3&&"1".equals(temps[0])){//自助
                    	recivenbase=temps[1].substring(0,3);
                    	recivea0100=temps[1].substring(3,temps[1].length());
                    	//给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
                        sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                    }else if("4".equals(temps[0])){//业务
                    	RecordVo vo=new RecordVo("operuser");
                        vo.setString("username",temps[1]);
                        vo=dao.findByPrimaryKey(vo);
                        if(vo.getString("a0100")!=null&&!"".equals(vo.getString("a0100"))){
	                         recivenbase=vo.getString("nbase");
	                         recivea0100=vo.getString("a0100");
	                         //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
	                         sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                        }
                    }/*else if("2".equals(temps[0])){//角色  此处先不放开,考虑到角色下人员可能比较多 发送到oa的代办会造成很多而不会被及时处理,就会一直存在.
	                	rowSet=dao.search("select * from t_sys_role where role_id='"+temps[1]+"'");
						int role_property=0;
						if(rowSet.next())
							role_property=rowSet.getInt("role_property");
						if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14){
							continue;
						}
						ArrayList alist=sendMessageBo.findUserListByRoleId(temps[1],sendMessageBo.getTemplate_emailAddress(),sendMessageBo.getTemplate__set());
						LazyDynaBean abean=null;
						for(int i=0;i<alist.size();i++)
						{
							abean=(LazyDynaBean)alist.get(i);
							String status = (String) abean.get("status");
							String nbase_ = "";
							String a0100_ = "";
							if (status.equals("1")) {
		                        String str = (String) abean.get("a0100");
		                        nbase_ = str.substring(0, 3);
		                        a0100_ = str.substring(3);
			                }
							//给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818 
	                        sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),nbase_, a0100_,"2");
						}
	                }*/

                    StringBuffer whl_str=new StringBuffer("");
                    ArrayList recordList=new ArrayList();
                    for(int j=0;j<seqnumList.size();j++)
                    {
                        RecordVo vo=new RecordVo("t_wf_task_objlink"); 
                        String[] _str=((String)seqnumList.get(j)).split("`");
                        whl_str.append(",'"+_str[0]+"'");
                        vo.setString("seqnum",_str[0]);
                        vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                        vo.setInt("task_id",_task_vo.getInt("task_id"));
                        vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
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
                        sendMessageBo.sendFilingMessage(_task_vo.getString("actorid"),_task_vo.getString("actor_type"),newEmailBo,this.tablebo,isSendMessage,a_context,a_title,
                                String.valueOf(this.ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ("+whl_str.substring(1)+")","0",columnMap,realnumList,"3",this.tablebo.getInfor_type());
                    }
                }
            }
             
            if(email_staff_value!=null&& "1".equals(this.email_staff_value.trim())&&newEmailBo!=null)//抄送本人
            {
                rowSet=dao.search(sql); 
                _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name"));
                while(rowSet.next())
                { 
                    flag=true;
                    _task_vo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
                    _task_vo.setString("actor_type","1");
                    _task_vo.setString("actorid",rowSet.getString("basepre")+rowSet.getString("a0100"));
                    dao.addValueObject(_task_vo);
                    
                    //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
                    sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),rowSet.getString("basepre"), rowSet.getString("a0100"),"2");
                    
                    RecordVo vo=new RecordVo("t_wf_task_objlink");  
                    vo.setString("seqnum",rowSet.getString("seqnum"));
                    vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                    vo.setInt("task_id",_task_vo.getInt("task_id"));
                    vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
                    vo.setInt("node_id",_task_vo.getInt("node_id"));
                    vo.setString("task_type","3"); 
                    vo.setInt("submitflag",1);
                    vo.setInt("count",1);
                    vo.setInt("state",1);
                    dao.addValueObject(vo);
                    
                    String a_title=_task_vo.getString("task_topic"); 
                    String a_context=a_title;  
                    sendMessageBo.setTask_id(String.valueOf(_task_vo.getInt("task_id")));
                    sendMessageBo.sendFilingMessage("","",newEmailBo,this.tablebo,isSendMessage,a_context,a_title,
                            String.valueOf(this.ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ('"+vo.getString("seqnum")+"')","1",columnMap,realnumList,"3",this.tablebo.getInfor_type());
                }
            } 
        }
        catch(Exception ex)
        { 
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return flag;
    }
    
    
    //bug 35083 只有结束节点才给本人发送邮件。此方法是供其他调用2个参数的类调用。
    public void sendFilingTasks(int _task_id,String _ext_param) throws GeneralException
    {
    	this.sendFilingTasks(_task_id,_ext_param,null);
    }
    /**
     *  按设置的高级条件发送报备信息
     * @param nextNode
     * @param task_vo
     */
    public void sendFilingTasks(int _task_id,String _ext_param,WF_Node wf_node) throws GeneralException//bug 35083 只有结束节点才给本人发送邮件。
    {
    	RowSet rowSet = null;
        try
        {
            
            //if(this.tablebo.getInfor_type()==2 || this.tablebo.getInfor_type()==3){//单位和岗位暂时忽略报备   郭峰
              //  return;
           // }

            
        //  int _task_id=task_vo.getInt("task_id");
            ContentDAO dao=new ContentDAO(this.conn);
            LazyDynaBean filingBean= getFilingInfo(_ext_param);
             
            ArrayList condList=new ArrayList();
            if(filingBean.get("condlist")!=null) {
                condList=(ArrayList)filingBean.get("condlist");
            }
            String objs=(String)filingBean.get("objs");
            
            MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
             
            String selfapply="0";
            if(this.tablebo!=null&&this.tablebo.isBEmploy()) {
                selfapply="1";
            }
            String tabname="templet_"+this.tabid;
            if(_task_id==0||_task_id==-1)
            {
                if("0".equals(selfapply)) {
                    tabname=this.tablebo.getUserview().getUserName()+tabname;
                } else {
                    tabname="g_"+tabname;
                }
            } 
            String a0101="";
            if(this.tablebo.getInfor_type()==1) {
                a0101="a0101_1";
            }else {
                a0101="codeitemdesc_1";
            }
            
            if(this.tablebo!=null&&this.tablebo.getOperationtype()==0)//调入
            {
                a0101="a0101_2";
            }
            String sql="select * from "+tabname+" where 1=1 ";
            if(tabname.equalsIgnoreCase("templet_"+this.tabid))
            {
                sql="select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ";
                sql+=" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+_task_id+" and td.state<>3 )";
            }
            else if(tabname.equalsIgnoreCase(this.tablebo.getUserview().getUserName()+"templet_"+this.tabid))
            {
                sql+=" and submitflag=1";
                if(this.cd_whl!=null&&this.cd_whl.trim().length()>0) {
                    sql+=this.cd_whl;
                }
            }
            else
            {
                sql+=" and lower(basepre)='"+this.tablebo.getUserview().getDbname().toLowerCase()+"' and a0100='"+this.tablebo.getUserview().getA0100()+"'";
            }
            ArrayList seqnumList=new ArrayList();
            ArrayList realSeqnumList = new ArrayList();
            rowSet=dao.search(sql);
            
            while(rowSet.next())
            {
                seqnumList.add(rowSet.getString("seqnum")+"`"+rowSet.getString(a0101)); 
                realSeqnumList.add(rowSet.getString("seqnum"));
            }
             
            
            ArrayList fldvarlist=this.tablebo.getAllFieldItem(); 
            ArrayList fieldlist=this.tablebo.getMidVariableList();
            fldvarlist.addAll(fieldlist);
            for(int i=0;i<fldvarlist.size();i++)
            {
                FieldItem fielditem=(FieldItem)fldvarlist.get(i);
                if(fielditem.getVarible()!=1)
                {
                    if(fielditem.isChangeAfter()) {
                        fielditem.setItemid(fielditem.getItemid()+"_2");
                    }
                    if(fielditem.isChangeBefore()){
                            if(this.tablebo.getSub_domain_map()!=null&&this.tablebo.getSub_domain_map().get(""+i)!=null&&this.tablebo.getSub_domain_map().get(""+i).toString().trim().length()>0){
                                fielditem.setItemid(fielditem.getItemid()+"_"+this.tablebo.getSub_domain_map().get(""+i)+"_1");
                                fielditem.setItemdesc(""+this.tablebo.getSub_domain_map().get(""+i+"hz"));
                                }else{
                                    fielditem.setItemid(fielditem.getItemid()+"_1");    
                                } 
                    }
                }
            }
            if(workflowBo==null) {
                workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.tablebo.getUserview());
            }
            
            workflowBo.computeVarByCondition(tabname,condList,fldvarlist,fieldlist,sql);
            int infoGroupFlag=YksjParser.forPerson;
            if(this.tablebo.getInfor_type()==2) {
                infoGroupFlag=YksjParser.forUnit;
            }
            if(this.tablebo.getInfor_type()==3) {
                infoGroupFlag=YksjParser.forPosition;
            }
            YksjParser yp = new YksjParser(this.tablebo.getUserview(),fldvarlist,
                    YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", "");  
            LazyDynaBean bean=null;
            RecordVo _task_vo=new RecordVo("t_wf_task"); 
            _task_vo.setString("bs_flag", "3"); 
            _task_vo.setInt("bread",0); 
            _task_vo.setString("task_state","3");
            _task_vo.setString("state","08");
            _task_vo.setString("task_type","2"); 
            _task_vo.setInt("node_id",this.node_id);
            _task_vo.setInt("ins_id",this.ins_vo.getInt("ins_id"));
            _task_vo.setDate("start_date",new Date()); 
            _task_vo.setString("task_pri","1");
            
            _task_vo.setString("a0100",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
            _task_vo.setString("a0101",this.tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
            _task_vo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()/*this.wf_actor.getActorid()*/);//发送人
            if(this.actorname.length()>0) {
                _task_vo.setString("a0101_1",this.actorname);
            } else {
                _task_vo.setString("a0101_1",this.tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名
            }
            
            boolean email_staff=this.tablebo.isEmail_staff();
            SendMessageBo sendMessageBo=new SendMessageBo(this.conn,tablebo.getUserview());
            sendMessageBo.setBusinessModel("61");
            sendMessageBo.setSendresource(this.sendresource);
            String template_bos=tablebo.getTemplate_bos();    ////业务办理人员的邮件模板
            sendMessageBo.setIns_id(String.valueOf(this.ins_vo.getInt("ins_id")));
            sendMessageBo.setSp_flag("3");
            
            IDGenerator idg=new IDGenerator(2,this.conn);       
            AsyncEmailBo newEmailBo=null;
            LazyDynaBean emialInfo_bos=null;
            LazyDynaBean emailInfo_staff=null;
            HashMap columnMap=new HashMap();
            if(!"0".equals(isSendMessage)||(this.email_staff_value!=null&& "1".equals(this.email_staff_value.trim())))
            {
                try
                {
                    newEmailBo=new AsyncEmailBo(this.conn, this.tablebo.getUserview());
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
                
                if(this.email_staff_value!=null&& "1".equals(this.email_staff_value.trim()))
                {
                    rowSet=dao.search(sql);
                    ResultSetMetaData md=rowSet.getMetaData();
                    for(int i=0;i<md.getColumnCount();i++)
                    {
                        int columnType=md.getColumnType(i+1);   
                        String columnName=md.getColumnName(i+1).toLowerCase();
                        if(columnType==java.sql.Types.TIMESTAMP||columnType==java.sql.Types.DATE||columnType==java.sql.Types.TIME) {
                            columnMap.put(columnName, "D");
                        } else {
                            columnMap.put(columnName, "A");
                        }
                    }
                }
            }
            
            if(!"2".equals(this.opt))//如果不是驳回
            {
                for(int i=0;i<condList.size();i++)
                {
                    bean=(LazyDynaBean)condList.get(i);
                    String cond=(String)bean.get("cond");
                    ArrayList _objList=(ArrayList)bean.get("objList");
                    ArrayList seqNumList=workflowBo.getFilingObjs(sql,yp,cond,_task_id,ins_vo.getInt("ins_id"),selfapply,_objList,this.cd_whl);  //获得单据中的报备人员
                    if(seqNumList.size()==0) {
                        continue;
                    }
                    ArrayList actorList=getFilingActorList(_objList,_task_id,ins_vo.getInt("ins_id"),selfapply,this.cd_whl);//获得报备对象
                    
                    
                    
                    String topic="";
                    for(int j=0;j<seqNumList.size();j++)
                    {
                        if(j<3) {
                            topic+=((String)seqNumList.get(j)).split("`")[1]+",";
                        }
                    }
                    
                    if("2".equals(opt)){ //驳回
                        if(this.tablebo.getInfor_type()==1) {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)_驳回");
                        } else if(this.tablebo.getInfor_type()==2) {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)_驳回");
                        } else {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)_驳回");
                        }
                    }else{
                        if(this.tablebo.getInfor_type()==1) {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)");
                        } else if(this.tablebo.getInfor_type()==2) {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)");
                        } else {
                            _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)");
                        }
                    }
                    String a0100=this.tablebo.getUserview().getA0100();
                    if(this.actorname.length()>0) {
                        _task_vo.setString("a0101_1",this.actorname);
                    } else {
                        _task_vo.setString("a0101_1",this.tablebo.getUserview().getUserFullName());//发送人姓名
                    }
                    for(Iterator t=actorList.iterator();t.hasNext();)
                    {
                        bean=(LazyDynaBean)t.next();
                            
                        _task_vo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
                        _task_vo.setString("actor_type",(String)bean.get("actor_type"));
                        _task_vo.setString("actorid",(String)bean.get("actorid"));
                        _task_vo.setString("actorname",(String)bean.get("actorname"));
                        dao.addValueObject(_task_vo); 
                        
                        String actorid=(String)bean.get("actorid");
                        String actor_type=(String)bean.get("actor_type");
                        String recivenbase="";
                        String recivea0100="";  //这里只处理了自助用户的接收人，业务用户未处理
                        if(actorid.length()>3&&"1".equals(actor_type)){
	                    	recivenbase=actorid.substring(0,3);
	                    	recivea0100=actorid.substring(3,actorid.length());
	                    }else{
	                    	RecordVo vo=new RecordVo("operuser");
	                         vo.setString("username",actorid);
	                         vo=dao.findByPrimaryKey(vo);
	                         if(vo.getString("a0100")!=null&&!"".equals(vo.getString("a0100"))){
		                         recivenbase=vo.getString("nbase");
		                         recivea0100=vo.getString("a0100");
	                         }
	                    }
                        
                        //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
                        sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                        
                        StringBuffer whl_str=new StringBuffer("");
                        ArrayList recordList=new ArrayList();
                        for(int j=0;j<seqNumList.size();j++)
                        {
                            
                            RecordVo vo=new RecordVo("t_wf_task_objlink"); 
                            String[] _str=((String)seqNumList.get(j)).split("`");
                            whl_str.append(",'"+_str[0]+"'");
                            vo.setString("seqnum",_str[0]);
                            vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                            vo.setInt("task_id",_task_vo.getInt("task_id"));
                            vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
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
                            //String a_title=_task_vo.getString("task_topic"); 
                            //String a_context=a_title; 
                            //报备时,邮件的主题和内容,均取值为模板的名称,如果报备设置了邮件模板,后面的方法重新取值
                            //String a_title=this.tablebo.getTable_vo().getString("name"); 
                            //邮件标题取自任务名称 wangrd 20151111
                          String a_title=_task_vo.getString("task_topic");
                          String a_context=a_title; 
                          sendMessageBo.sendFilingMessage((String)bean.get("actorid"),(String)bean.get("actor_type"),newEmailBo,this.tablebo,isSendMessage,a_context,a_title,
                                    String.valueOf(this.ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ("+whl_str.substring(1)+")","0",columnMap,realSeqnumList,opt,this.tablebo.getInfor_type());
                        }
                        
                    } 
                }
            }
            else//如果是驳回
            {
                objs="";
            }
            String  fillUsers=this.user_h_s;//wangrd  2015-03-04 防止有缓存的问题
            if(objs!=null&&objs.trim().length()>0) {
                fillUsers+=objs;
            }
            if(fillUsers!=null&&fillUsers.trim().length()>0)
            {
                String[] users=fillUsers.split(",");
                HashSet set=new HashSet();
                for(int i=0;i<users.length;i++)
                {
                    if(users[i]!=null&&users[i].trim().length()>0) {
                        set.add(users[i]);
                    }
                }
                
                String topic="";
                for(int j=0;j<seqnumList.size();j++)
                {
                    if(j<5) {
                        topic+=((String)seqnumList.get(j)).split("`")[1]+",";
                    }
                }
                if("2".equals(opt)){ //驳回
                    if(this.tablebo.getInfor_type()==1) {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)_驳回");
                    } else if(this.tablebo.getInfor_type()==2) {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)_驳回");
                    } else {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)_驳回");
                    }
                }else{
                    if(this.tablebo.getInfor_type()==1) {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"人)");
                    } else if(this.tablebo.getInfor_type()==2) {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个单位或部门)");
                    } else {
                        _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"("+topic+"共"+seqnumList.size()+"个岗位)");
                    }
                }
                String a0100=this.tablebo.getUserview().getA0100();
                for(Iterator t=set.iterator();t.hasNext();)
                {
                    String temp=(String)t.next();
                    if(temp.trim().length()==0) {
                        continue;
                    }
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
                        sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                    }else if("4".equals(temps[0])){//业务
                    	RecordVo vo=new RecordVo("operuser");
                        vo.setString("username",temps[1]);
                        vo=dao.findByPrimaryKey(vo);
                        if(vo.getString("a0100")!=null&&!"".equals(vo.getString("a0100"))){
	                         recivenbase=vo.getString("nbase");
	                         recivea0100=vo.getString("a0100");
	                       //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818 
	                         sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),recivenbase, recivea0100,"2");
                        }
                    }/*else if("2".equals(temps[0])){//角色  此处先不放开,考虑到角色下人员可能比较多 发送到oa的代办会造成很多而不会被及时处理,就会一直存在.
	                	rowSet=dao.search("select * from t_sys_role where role_id='"+temps[1]+"'");
						int role_property=0;
						if(rowSet.next())
							role_property=rowSet.getInt("role_property");
						if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14){
							continue;
						}
						ArrayList alist=sendMessageBo.findUserListByRoleId(temps[1],sendMessageBo.getTemplate_emailAddress(),sendMessageBo.getTemplate__set());
						LazyDynaBean abean=null;
						for(int i=0;i<alist.size();i++)
						{
							abean=(LazyDynaBean)alist.get(i);
							String status = (String) abean.get("status");
							String nbase_ = "";
							String a0100_ = "";
							if (status.equals("1")) {
		                        String str = (String) abean.get("a0100");
		                        nbase_ = str.substring(0, 3);
		                        a0100_ = str.substring(3);
			                }
							//给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818 
	                        sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),nbase_, a0100_,"2");
						}
	                }*/

                    StringBuffer whl_str=new StringBuffer("");
                    ArrayList recordList=new ArrayList();
                    for(int j=0;j<seqnumList.size();j++)
                    {
                        RecordVo vo=new RecordVo("t_wf_task_objlink"); 
                        String[] _str=((String)seqnumList.get(j)).split("`");
                        whl_str.append(",'"+_str[0]+"'");
                        vo.setString("seqnum",_str[0]);
                        vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                        vo.setInt("task_id",_task_vo.getInt("task_id"));
                        vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
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
                    if(!"0".equals(isSendMessage)&&newEmailBo!=null&&(wf_node==null||wf_node.nodetype!=NodeType.END_NODE))//bug 35083 只有结束节点才给本人发送邮件。
                    { 
                      //String a_title=_task_vo.getString("task_topic"); 
                        //String a_context=a_title; 
                        //报备时,邮件的主题和内容,均取值为模板的名称,如果报备设置了邮件模板,后面的方法重新取值
                        //String a_title=this.tablebo.getTable_vo().getString("name"); 
                        //邮件标题取自任务名称 wangrd 20151111
                      String a_title=_task_vo.getString("task_topic");
                      String a_context=a_title; 
                      String whl_str_sub="";
                      if(whl_str.length()>0){//排除抄送时必选邮件模版的情况
                    	  whl_str_sub=whl_str.substring(1);
                      }
                        sendMessageBo.sendFilingMessage(_task_vo.getString("actorid"),_task_vo.getString("actor_type"),newEmailBo,this.tablebo,isSendMessage,a_context,a_title,
                                String.valueOf(this.ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ("+whl_str_sub+")","0",columnMap,realSeqnumList,opt,this.tablebo.getInfor_type());
                    }
                }
            }
             
            if((!"4".equalsIgnoreCase(this.bs_flag))&&email_staff_value!=null&& "1".equals(this.email_staff_value.trim())&&newEmailBo!=null&&wf_node!=null&&wf_node.nodetype==NodeType.END_NODE)//抄送本人//bug 35083 只有结束节点才给本人发送邮件。
            {
                rowSet=dao.search(sql); 
                if("2".equals(opt)) //驳回
                {
                    _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name")+"_驳回");
                } else {
                    _task_vo.setString("task_topic",this.tablebo.getTable_vo().getString("name"));
                }
                while(rowSet.next())
                { 
                    _task_vo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
                    _task_vo.setString("actor_type","1");
                    _task_vo.setString("actorid",rowSet.getString("basepre")+rowSet.getString("a0100"));
                    dao.addValueObject(_task_vo);
                    
                    //给报备信息提交接口，使其可以直接通过url地址进行查看 liuzy 20150818
                    sysBo.sendDealWithInfo("", 1,tabid, this.ins_vo.getInt("ins_id"),PubFunc.encrypt(String.valueOf(_task_vo.getInt("task_id"))),rowSet.getString("basepre"), rowSet.getString("a0100"),"2");

                    
                    RecordVo vo=new RecordVo("t_wf_task_objlink");  
                    vo.setString("seqnum",rowSet.getString("seqnum"));
                    vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                    vo.setInt("task_id",_task_vo.getInt("task_id"));
                    vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
                    vo.setInt("node_id",_task_vo.getInt("node_id"));
                    vo.setString("task_type","3"); 
                    vo.setInt("submitflag",1);
                    vo.setInt("count",1);
                    vo.setInt("state",1);
                    dao.addValueObject(vo);
                    
                  //String a_title=_task_vo.getString("task_topic"); 
                    //String a_context=a_title; 
                    //报备时,邮件的主题和内容,均取值为模板的名称,如果报备设置了邮件模板,后面的方法重新取值
                  String a_title=this.tablebo.getTable_vo().getString("name"); 
                  String a_context=a_title;
                    sendMessageBo.setTask_id(String.valueOf(_task_vo.getInt("task_id")));
                    sendMessageBo.sendFilingMessage("","",newEmailBo,this.tablebo,isSendMessage,a_context,a_title,
                            String.valueOf(this.ins_vo.getInt("tabid")),emailInfo_staff,emialInfo_bos,sql+" and seqnum in ('"+vo.getString("seqnum")+"')","1",columnMap,realSeqnumList,opt,this.tablebo.getInfor_type());
                }
            }
            
            
            
            
            
        }
        catch(Exception ex)
        { 
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally{
        	PubFunc.closeDbObj(rowSet);
        }
    }
    
    /**
     * 获得报备对象
     * @param _objList
     * @param taskid
     * @return
     */
    public ArrayList getFilingActorList(ArrayList _objList,int taskid,int ins_id,String selfapply,String whl)throws GeneralException
    {
        ArrayList list=new ArrayList();
        try
        {
            LazyDynaBean bean=new LazyDynaBean();
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=null;
            for(int i=0;i<_objList.size();i++)
            {
                bean=(LazyDynaBean)_objList.get(i);
                LazyDynaBean _bean=new LazyDynaBean();
                if(bean.get("special_role")==null)  //自助或业务用户
                {
                    String username=(String)bean.get("value");
                    rowSet=dao.search("select * from operuser where username='"+username+"'");
                    if(rowSet.next())
                    {
                        _bean.set("actor_type","4");
                        _bean.set("actorid",username);
                        _bean.set("actorname",rowSet.getString("fullname")!=null?rowSet.getString("fullname"):username);
                    }
                    else if(username.length()>10)
                    {
                        String nbase=username.substring(0,3);
                        String a0100=username.substring(3);
                        DbWizard dbw = new DbWizard(this.conn);
                        if(dbw.isExistTable(nbase+"A01",false)){   //判断表是否存在，处理业务用户在表中查不到的情况 20150729 liuzy
                             rowSet=dao.search("select * from "+nbase+"A01 where a0100='"+a0100+"'");
                             if(rowSet.next())
                             {
                                 _bean.set("actor_type","1");
                                 _bean.set("actorid",username);
                                 _bean.set("actorname",rowSet.getString("a0101"));
                             }
                        }   
                       
                    }
                    
                }
                else
                {
                    String special_role=(String)bean.get("special_role");
                    String from_nodeid=(String)bean.get("from_nodeid");
                    String item_id=(String)bean.get("item_id");
                    
                    LazyDynaBean roleBean=workflowBo.getFromNodeid_role(bean,ins_id,dao,taskid,selfapply,whl);
                    if("gwgx".equalsIgnoreCase(this.tablebo.getRelation_id())) //标准岗位关系
                    {
                        String userStr=getSuperPos_userStr(roleBean);
                        if(userStr!=null&&userStr.length()>0)
                        {
                            String[] temps=userStr.split("`");
                            _bean.set("actor_type","1");
                            _bean.set("actorid",temps[0]);
                            _bean.set("actorname",temps[1]); 
                        }
                    }
                    else
                    {
                        String sql=""; 
                        HashMap a_map=workflowBo.getSuperSql(Integer.parseInt(special_role),this.tablebo.getRelation_id(),roleBean);
                        if(a_map.size()==0) {
                            throw new GeneralException("报备指定的特殊角色在审批关系中没有定义!");
                        }
                        sql=(String)a_map.get("sql");  
                        rowSet=dao.search(sql);
                        while(rowSet.next())
                        {
                            LazyDynaBean _bean1=new LazyDynaBean();
                            String actor_type=rowSet.getString("actor_type");
                            _bean1.set("actor_type",actor_type);
                            _bean1.set("actorid",rowSet.getString("mainbody_id"));
                            _bean1.set("actorname",rowSet.getString("a0101"));  
                            list.add(_bean1);
                        } 
                    }  
                }
                if (_bean.get("actor_type")!=null) {
                    list.add(_bean);
                }
            }
            if(rowSet!=null) {
                rowSet.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    
    
    
    
    
    /**
     * 获得节点设置的高级报备条件
     * @param extParam
     * @return
     */
    public LazyDynaBean getFilingInfo(String extParam)throws GeneralException
    {
        LazyDynaBean filingData=new LazyDynaBean();
        ArrayList list=new ArrayList();
        String objs="";
        try
        {
            if(extParam==null||extParam.length()==0) {
                return filingData;
            }
            ContentDAO dao=new ContentDAO(this.conn);
            
            Document doc=null;
            Element element=null; 
            Element element1=null; 
            doc=PubFunc.generateDom(extParam);; 
            
            String xpath="/params/filing_objs";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element=(Element)findPath.selectSingleNode(doc);
            if(element!=null&&element.getAttributeValue("obj")!=null)
            {
                String temp=element.getAttributeValue("obj");
                String[] temps=temp.split(",");
                RowSet rowSet=null;
                for(int j=0;j<temps.length;j++)
                {
                    if(temps[j]==null||temps[j].trim().length()==0) {
                        continue;
                    }
                    String username=temps[j].trim();
                    rowSet=dao.search("select * from operuser where username='"+username+"'");
                    if(rowSet.next())
                    {
                        objs+=",4:"+username;
                     
                    }
                    else if(username.length()>10)
                    {
                        String nbase=username.substring(0,3);
                        String a0100=username.substring(3);
                    	DbWizard dbw = new DbWizard(this.conn);
            			if(dbw.isExistTable(nbase+"A01",false)){   //判断表是否存在，处理业务用户在表中查不到的情况 20150729 liuzy
            				rowSet=dao.search("select * from "+nbase+"A01 where a0100='"+a0100+"'");
            				if(rowSet.next())
            				{
            					objs+=",1:"+username;
            					
            				}
            			}   
                    }
                }
            }
            
            
            xpath="/params/filing_objs/filing_obj";
            findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc);
            if(childlist!=null&&childlist.size()>0)
            {
                for(int i=0;i<childlist.size();i++)
                {
                    element=(Element)childlist.get(i);
                    LazyDynaBean bean=new LazyDynaBean();
                    String cond="";
                    ArrayList objList=new ArrayList();
                    List elements=element.getChildren();
                    for(int j=0;j<elements.size();j++)
                    {
                        element1=(Element)elements.get(j);
                        if("cond".equalsIgnoreCase(element1.getName())) {
                            cond=element1.getValue();
                        } else if("object".equalsIgnoreCase(element1.getName()))
                        {
                            LazyDynaBean obj=new LazyDynaBean();
                            if(element1.getAttributeValue("special_role")==null)
                            {
                                obj.set("value", element1.getValue());
                            }
                            else //special_role=”9” from_nodeid=”form” item_id=”E0122_1”
                            {
                                //reportNode ,startNode ,form,xxxx
                                obj.set("special_role",element1.getAttributeValue("special_role"));
                                if(element1.getAttributeValue("from_nodeid")==null) {
                                    throw new GeneralException(this.nodename+"节点报备设置错误!");
                                } else
                                {
                                    obj.set("from_nodeid",element1.getAttributeValue("from_nodeid"));
                                    if("form".equalsIgnoreCase(element1.getAttributeValue("from_nodeid")))
                                    {
                                        if(element1.getAttributeValue("item_id")==null||element1.getAttributeValue("item_id").trim().length()==0) {
                                            throw new GeneralException(this.nodename+"节点报备设置错误!");
                                        }
                                        obj.set("item_id",element1.getAttributeValue("item_id"));
                                    } 
                                }
                            } 
                            objList.add(obj);
                        } 
                    }
                    bean.set("cond",cond);
                    bean.set("objList",objList);
                    list.add(bean);
                }
            }
        }
        catch(Exception ex)
        { 
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        
        filingData.set("condlist",list);
        filingData.set("objs",objs);
        return filingData;
    }
    /**
     * 读取节点设置 控制是否报批后才允许导出及导出指定页签 
     *  <!—- pages:输出的页签号   applied_out:报批后允许输出  -- >
     < out  pages=’1,2,3,4’   applied_out=’true’  />
     * @param _ext_param
     * @return
     */
    public LazyDynaBean getOutSetting(String _ext_param,int node_type)throws GeneralException {
    	LazyDynaBean bean=null;
    	try {
    		if(StringUtils.isEmpty(_ext_param)) {
    			return null;
    		}
			bean=new LazyDynaBean();
			Document doc=null;
            Element element=null; 
            doc=PubFunc.generateDom(_ext_param);; 
            String xpath="/params/out";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element=(Element)findPath.selectSingleNode(doc);
            if(element!=null) {
            	String pages=element.getAttributeValue("pages");//输出页签号
            	String applied_out=element.getAttributeValue("applied_out");//报批后允许输出
            	bean.set("pages", pages);
            	bean.set("applied_out", applied_out);
            }
            xpath="/params/function";
            findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element=(Element)findPath.selectSingleNode(doc);
            if(element!=null) {
            	String pri=element.getAttributeValue("pri");
            	pri=pri.toUpperCase()+",";
            	if(pri.indexOf("WORD,")>-1) {
            		bean.set("word", true);
            	}else {
            		bean.set("word", false);
            	}
            	if(pri.indexOf("PRINT,")>-1) {
            		bean.set("print", true);
            	}else {
            		bean.set("print", false);
            	}
            	if(pri.indexOf("PDF,")>-1) {
            		bean.set("pdf", true);
            	}else {
            		bean.set("pdf", false);
            	}
            	if(pri.indexOf("DELETE,")>-1) {
            		bean.set("delete", true);
            	}else {
            		bean.set("delete", false);
            	}
            	if(pri.indexOf("CHANGE_VIEW,")>-1) {
            		bean.set("change_view", true);
            	}else {
            		bean.set("change_view", false);
            	}
            	if(pri.indexOf("BATCHUPDATE,")>-1) {
            		bean.set("batchupdate", true);
            	}else {
            		bean.set("batchupdate", false);
            	}
        		if(pri.indexOf("SETBUSIDATE,")>-1) {
            		bean.set("setbusidate", true);
            	}else {
            		bean.set("setbusidate", false);
            	}
        		
        		if(pri.indexOf("CARD,")>-1) {
            		bean.set("card", true);
            	}else {
            		bean.set("card", false);
            	}
        		if(pri.indexOf("WORDTEMPLATE,")>-1) {
            		bean.set("wordtemplate", true);
            	}else {
            		bean.set("wordtemplate", false);
            	}
        		if(pri.indexOf("MUSTER,")>-1) {
            		bean.set("muster", true);
            	}else {
            		bean.set("muster", false);
            	}
        		
            	if(node_type==1) {
            		if(pri.indexOf("IMPORT,")>-1) {
                		bean.set("import", true);
                	}else {
                		bean.set("import", false);
                	}
            		bean.set("compute", true);
            		bean.set("reject", true);
            		bean.set("process", true);
            		bean.set("down", true);
            	}else {
            		bean.set("import", true);
            		if(pri.indexOf("COMPUTE,")>-1) {
                		bean.set("compute", true);
                	}else {
                		bean.set("compute", false);
                	}
                	if(pri.indexOf("REJECT,")>-1) {
                		bean.set("reject", true);
                	}else {
                		bean.set("reject", false);
                	}
                	if(pri.indexOf("PROCESS,")>-1) {
                		bean.set("process", true);
                	}else {
                		bean.set("process", false);
                	}
                	if(pri.indexOf("DOWN,")>-1) {
                		bean.set("down", true);
                	}else {
                		bean.set("down", false);
                	}
            	}
            	
            }
            return bean;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
    }
    
    /**
     * 发起结点遇到与|或发散时，先将模版信息导入到临时表
     * @Title: impTempletTable 
     * @Description: TODO
     * @param selfapply
     * @param whl
     * @param wf_actor
     * @throws GeneralException void   
     * @throws
     */
    public void impTempletTable(String selfapply,String whl,WF_Actor wf_actor)throws GeneralException
    {
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            String sql="select count(*) as nrec from ";
            if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
            {
                sql+=" g_templet_"+tabid+" where a0100='"+this.tablebo.getUserview().getA0100()+"' and lower(basepre)='"+this.tablebo.getUserview().getDbname().toLowerCase()+"'";
            } else
            {
                sql+=this.tablebo.getUserview().getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
            
            }
            RowSet frowset=dao.search(sql);
            if(frowset.next())
            {
                if(frowset.getInt(1)>0)
                {
                     
                    String approve_opinion = tablebo.getApproveOpinion(ins_vo,String.valueOf(this.taskid), wf_actor,"");
                    tablebo.setApprove_opinion(approve_opinion);
                    if("1".equalsIgnoreCase(selfapply)) {
                        tablebo.saveSubmitTemplateData(ins_vo.getInt("ins_id"));
                    } else//将数据插入到template_tabid中
                    {
                        tablebo.saveSubmitTemplateData(this.tablebo.getUserview().getUserName(),ins_vo.getInt("ins_id"),whl);
                    }
                    //附件数据也需要改为流程中 bug51109
					String sqlfile=" select * from t_wf_file where ins_id=0 and tabid="
	                        +tabid+" and create_user='"+this.tablebo.getUserview().getUserName()+"' ";
	                        if(this.tablebo.getInfor_type()==1){//人员
	                        	String sqlStr="";
	                        	//liuyz bug27262 选人较多时，in后面超过1000,报ORA-01795
	                        	switch (Sql_switcher.searchDbServer()) {
	        					case Constant.MSSQL:
	        					    sqlStr+=" lower(basepre+objectid) " ;
	        						 break;
	        					default:
	        					    sqlStr+=" lower(concat(basepre,objectid)) " ;
	        						 break;
	                        	}
	                        	/*for(int i=0;i<personlist.size();i++){
	                        		ArrayList list = (ArrayList)personlist.get(i);
	                        		String basepre = (String)list.get(0);
	                        		String a0100 = (String)list.get(1);
	                        		if(i==0)
	                        			personarr += " in('"+basepre+a0100+"'";
	                        		else if(i%990==0&&personlist.size()%990>0)
	                        		{
	                        			personarr+=") or "+sqlStr+" in('"+basepre+a0100+"'";
	                        		}
	                        		else{
	                        			personarr += ",'"+basepre+a0100+"'";
	                        		}
	                        		if(i==personlist.size()-1)
	                        		{
	                        			personarr +=")";
	                        		}
	                        	}*/
	                        	/*if("".equals(personarr))
	                        		personarr = "in ('')";*/
	                        	sqlfile+="and ("+sqlStr+" in ('"+this.tablebo.getUserview().getDbname().toLowerCase()+this.tablebo.getUserview().getA0100()+"'))";
	                        }/*else{//b0110,e01a1
	                        	String personarr = "";
	                        	for(int i=0;i<personlist.size();i++){
	                        		ArrayList list = (ArrayList)personlist.get(i);
	                        		String a0100 = (String)list.get(0);
	                        		if(i==0)
	                        			personarr += "'"+a0100+"'";
	                        		else
	                        			personarr += ",'"+a0100+"'";
	                        	}
	                        	if("".equals(personarr))
	                        		personarr = "''";
	        					sql+=" and objectid in ("+personarr+")" ;
	                        }*/
	                        sqlfile+= " order by file_id";
	                        frowset = dao.search(sqlfile);
	                    String sqlstrs = "";
	                    while(frowset.next()){
	                        String file_id = frowset.getString("file_id");
	                        IDGenerator idg = new IDGenerator(2, this.conn);
	                        String file_id2 = idg.getId("t_wf_file.file_id");
	                        //liuyz bug25774 兼容6.3上传的附件，须将content字段内容也保留
	                        sqlstrs=" insert into t_wf_file(file_id,filepath,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,"
	                            +"create_user,create_time,fullname,content,i9999,state) "//bug 51078
	                            +"select "+file_id2+",filepath,filetype,objectid,basepre,attachmenttype,"+ins_vo.getInt("ins_id")
	                            +",tabid,ext,name,create_user,create_time,fullname,content,i9999,state from t_wf_file where file_id="+file_id+"  ";
	                        dao.update(sqlstrs);
	                    } 
                }
            }
        }
        catch(Exception ex)
        { 
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    
    
    
    /**
     * 创建下一个任务
     * @param instance
     * @return
     * @throws GeneralException
     */
    public boolean createTask(RecordVo instancevo,WF_Actor actor,ArrayList seqnumList)throws GeneralException
    {
        boolean bflag=true;
        try
        {
            
            this.ins_vo=instancevo;
            if(seqnumList==null)
            { 
                seqnumList=new ArrayList();
            }
            ArrayList nextlist=this.getNextNodeList(seqnumList);
            WF_Node next_node=null;
            ContentDAO dao=new ContentDAO(this.conn);
            
            String selfapply="0";
            if(this.tablebo!=null&&this.tablebo.isBEmploy()) {
                selfapply="1";
            }
            int count=getCountByTaskID(this.taskid);  //获得任务当前节点处理次数
            
            if(this.nodetype==NodeType.START_NODE&&this.taskid!=-1) {
                this.nodetype=NodeType.TOOL_NODE;
            }
            
            if(workflowBo==null){
                if (this.tablebo!=null) {
                    workflowBo=new WorkflowBo(this.conn,this.tablebo,this.tablebo.getUserview());
                } else {
                    workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.tablebo.getUserview());
                }
            }
            
            switch(this.nodetype)
            {
            case NodeType.START_NODE://起始开始
                
                isStartWorkflow=true;  //记录流程发起操作  2014-7-16  邓灿
                
                /**开始节点的输出变迁仅只能有一个*/
                if(nextlist.size()!=1) {
                    throw new GeneralException(ResourceFactory.getProperty("error.start.outtrans"));
                }
                /**下一个节点*/
                next_node=(WF_Node)nextlist.get(0);
                if(next_node.getNodetype()==0) {
                    throw new GeneralException(ResourceFactory.getProperty("error.nextnode.not"));
                }
                /**把起始节点设置为结束状态*/
                task_vo=saveTask(this,NodeType.TASK_FINISHED,actor,1);
                
                //按设置的高级条件发送报备信息
                sendFilingTasks(0,this.ext_param,this); 
                //=1开始，=2人工,=3自动 =4与发散,=5与汇聚,=6或发散 =7或汇聚,=8哑节点,=9结束
                switch(next_node.nodetype)
                {
                    case NodeType.HUMAN_NODE://人工节点
                    case NodeType.TOOL_NODE://自动节点      
                        this.fromOri="1";
                        /* 自定义了审批流 */
                        if(isDefFlowSelf(this.tablebo,this.tablebo.getTabid()))
                        {
                            ArrayList nextnodelist=getNextNodeList_manual(seqnumList,task_vo.getInt("node_id"),instancevo.getInt("ins_id"),task_vo.getInt("task_id"),this.tablebo,true);
                            this.fromOri="1"; 
                            this.params="1";  //自定义流程的任务param=1
                            for(int i=0;i<nextnodelist.size();i++)
                            {
                                WF_Node wf_node=(WF_Node)nextnodelist.get(i); 
                                actor.setActorid(wf_node.getActorid());
                                actor.setActorname(wf_node.getActorname());
                                actor.setActortype("1");
                                RecordVo _taskvo=saveTask(wf_node,NodeType.TASK_WAINTING,actor,1);
                                _taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id"));
                                if(this.task_vo.getString("task_id_pro")!=null&&this.task_vo.getString("task_id_pro").trim().length()>0)
                                {
                                    _taskvo.setString("task_id_pro",","+_taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
                                }
                                else {
                                    _taskvo.setString("task_id_pro",","+_taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id"));
                                }
                                dao.updateValueObject(_taskvo); 
                            }
                            params="";
                            this.fromOri="0"; 
                        }
                        else
                        {
                            RecordVo taskvo=saveTask(next_node,NodeType.TASK_WAINTING,actor,1);
                            /**起始节点，流程定义加入参与对象*/
                            if(next_node.nodetype==NodeType.HUMAN_NODE) {
                                updateTaskActor_nomail(taskvo,actor);
                            }
                            if(taskvo.getString("actorid")==null|| "".equals(taskvo.getString("actorid"))) {
                                throw new GeneralException(ResourceFactory.getProperty("error.nodefine.applyobj"));
                            }
                        
                            taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id"));
                            
                            if(this.task_vo.getString("task_id_pro")!=null&&this.task_vo.getString("task_id_pro").trim().length()>0)
                            {
                                taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
                            }
                            else {
                                taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id"));
                            }
                            dao.updateValueObject(taskvo);
                            int defaultSelected=0;
                            if("1".equals(this.tablebo.getDefaultselected())) {
                                defaultSelected=1;
                            }
                            dao.update("update t_wf_task_objlink set submitflag="+defaultSelected+" where task_id="+taskvo.getInt("task_id")); //报批后的数据默认为选中 
                            this.fromOri="0";
                            this.setNexTTask_vo(taskvo);
                        }
                        break;              
                    case NodeType.END_NODE:
                        task_vo=saveTask(next_node,NodeType.TASK_FINISHED,actor,1);                 
                        break;
                    case NodeType.AND_SPLIT_NODE:    //与发散 
                        ArrayList nodelist=workflowBo.getNextNode(0,0,selfapply,cd_whl);
                        RecordVo _taskvo=saveTask(next_node,NodeType.TASK_FINISHED,actor,1); 
                        
                        //发起结点遇到与|或发散时，先将模版信息导入到临时表  2014-05-22 dengcan
                        impTempletTable(selfapply,this.cd_whl,actor);
                        
                        task_vo=_taskvo;
                        //往各个节点创建任务 
                        this.fromOri="0"; // 2014-05-22 dengcan
                        ArrayList taskList=saveTasks(nodelist,actor,workflowBo,1); 
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            int defaultSelected=0;
                            if("1".equals(this.tablebo.getDefaultselected())|| "4".equals(taskvo_temp.getString("bs_flag"))) {
                                defaultSelected=1;
                            }
                            dao.update("update t_wf_task_objlink set submitflag="+defaultSelected+" where task_id="+taskvo_temp.getInt("task_id")); //报批后的数据默认为选中
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp; 
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        }
                        this.fromOri="0";
                        break;
                    case NodeType.OR_SPLIT_NODE:     //或发散
                        ArrayList nodelist1=workflowBo.getNextNode(0,0,selfapply,cd_whl);
                        RecordVo _taskvo1=saveTask(next_node,NodeType.TASK_FINISHED,actor,1); 
                        task_vo=_taskvo1;
                        
                          //发起结点遇到与|或发散时，先将模版信息导入到临时表 2014-05-22 dengcan
                        impTempletTable(selfapply,this.cd_whl,actor);
                        
                        this.fromOri="0"; // 2014-05-22 dengcan
                        taskList=saveTasks(nodelist1,actor,workflowBo,1);
                         
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            int defaultSelected=0;
                            if("1".equals(this.tablebo.getDefaultselected())|| "4".equals(taskvo_temp.getString("bs_flag"))) {
                                defaultSelected=1;
                            }
                            dao.update("update t_wf_task_objlink set submitflag="+defaultSelected+" where task_id="+taskvo_temp.getInt("task_id")); //报批后的数据默认为选中
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp; 
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        } 
                        this.fromOri="0";
                        break;  
                    default:
                        next_node.createTask(instancevo,actor,seqnumList);
                        break;
                }
                break;
            case NodeType.HUMAN_NODE://人工节点
            case NodeType.TOOL_NODE: //自动节点 
                int selfTaskid=this.taskid;
                /**人工节点的输出变迁仅只能有一个*/
                if(nextlist.size()==0) {
                    throw new GeneralException(ResourceFactory.getProperty("error.nextnode.not"));
                }
                /**这句很重要，如果当前节点任务已传进来，则不用
                 * 再新一个任务啦
                 * */
                if(this.taskid==-1) {
                    task_vo=saveTask(this,NodeType.TASK_FINISHED,actor,0);
                }

                
                /**取下一个节点*/
                next_node=(WF_Node)nextlist.get(0);
                switch(next_node.nodetype)
                {
                    case NodeType.HUMAN_NODE://人工节点
                    case NodeType.TOOL_NODE://自动节点
                        RecordVo taskvo=saveTask(next_node,NodeType.TASK_WAINTING,actor,count); 
                        if(next_node.nodetype==NodeType.HUMAN_NODE) {
                            updateTaskActor_nomail(taskvo,actor);
                        }
                        if(taskvo.getString("actorid")==null|| "".equals(taskvo.getString("actorid"))) {
                            throw new GeneralException(ResourceFactory.getProperty("error.nodefine.applyobj"));
                        }
                        taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id"));
                        
                        if(this.task_vo.getString("task_id_pro")!=null&&this.task_vo.getString("task_id_pro").trim().length()>0)
                        {
                            taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
                        }
                        else {
                            taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id"));
                        }
                        dao.updateValueObject(taskvo);
                        this.setNexTTask_vo(taskvo); 
                        break;              
                    case NodeType.END_NODE:
                        task_vo=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);//20080418
                        this.setNexTTask_vo(task_vo);
                        break;
                    case NodeType.OR_SPLIT_NODE: //或发散
                        RecordVo _taskvo1=new RecordVo("t_wf_task");
                        _taskvo1.setInt("task_id",this.taskid);
                        _taskvo1=dao.findByPrimaryKey(_taskvo1);
                        ArrayList nodelist1=workflowBo.getNextNode(_taskvo1.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                        count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                        _taskvo1=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                        this.task_vo=_taskvo1;
                        //往各个节点创建任务 
                        ArrayList taskList=saveTasks(nodelist1,actor,workflowBo,count); 
                         
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        }
                         
                        break;
                    case NodeType.OR_JOIN_NODE://或汇聚和OR_SPLIT_NODE配对，需要同步
                        ArrayList objNumList_or=workflowBo.getContinueObj_AND(this.taskid,this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),next_node,2);
                        if(objNumList_or.size()>0)
                        {
                            //将暂停状态的任务改成结束状态
                            workflowBo.taskState_stopToEnd(this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),next_node.getNode_id(),1);
                            
                            next_node.setSeqnumList(objNumList_or);
                            RecordVo _taskvo=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);  
                            task_vo=_taskvo;
                            ArrayList _nextlist=next_node.getNextNodeList(objNumList_or);
                            next_node=(WF_Node)_nextlist.get(0);   
                            next_node.setSeqnumList(objNumList_or);
                            if(!"0".equals(isSendMessage)) {
                            	next_node.setEmail_staff_value(email_staff_value);
                            	next_node.setIsSendMessage(isSendMessage);
                            	next_node.setUser_h_s(user_h_s);
                            	next_node.setObjs_sql(this.objs_sql);
                            }
                            
                            if(next_node.getNodetype()==2&&isNullNode(next_node.getNode_id())) //20141110 dengcan 或汇聚后为空节点，需自动跳转
                            {
                                this.bs_flag="4"; //空任务
                                RecordVo taskvo_temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);
                                this.bs_flag="1"; //待批任务
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                            }
                            else if(next_node.getNodetype()==NodeType.HUMAN_NODE||next_node.getNodetype()==NodeType.TOOL_NODE)
                            {
                                RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_WAINTING,actor,count);
                                next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),_taskvo2.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                                this.setNexTTask_vo(_taskvo2); 
                            } 
                            else if(next_node.getNodetype()==NodeType.AND_SPLIT_NODE)
                            {
                                 
                                ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                                count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                                RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                                _taskvo2.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                             
                                task_vo=_taskvo2; //_taskvo;
                                //往各个节点创建任务 
                                taskList=saveTasks(nodelist,actor,workflowBo,count); 
                                 
                                for(int i=0;i<taskList.size();i++)
                                {  
                                    RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                    if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                    {
                                        this.task_vo=taskvo_temp;
                                        handleNullNode(taskvo_temp,instancevo,actor);
                                         
                                    }
                                    else
                                    {
                                    	this.setNexTTask_vo(taskvo_temp);
                                    	this.nextTaskVoList.add(taskvo_temp);
                                    }
                                }
                             
                            }
                            else if(next_node.getNodetype()==NodeType.OR_SPLIT_NODE)
                            { 
                                    ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                                    count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                                    RecordVo  temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);  
                                    this.task_vo=temp;
                                    //往各个节点创建任务 
                                    taskList=saveTasks(nodelist,actor,workflowBo,count); 
                                     
                                    for(int i=0;i<taskList.size();i++)
                                    {  
                                        RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                        if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                        {
                                            this.task_vo=taskvo_temp;
                                            handleNullNode(taskvo_temp,instancevo,actor);
                                         
                                        }
                                        else
                                        {
                                        	this.setNexTTask_vo(taskvo_temp);
                                        	this.nextTaskVoList.add(taskvo_temp);
                                        }
                                    }
                                
                            }
                            else
                            {
                                next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                                if(next_node.nodetype==NodeType.ADN_JOIN_NODE||NodeType.ADN_JOIN_NODE==NodeType.OR_JOIN_NODE)
                                {
                                    next_node.createTask(instancevo,actor,new ArrayList());
                                }
                                else {
                                    next_node.createTask(instancevo,actor,objNumList_or);
                                }
                                this.setNexTTask_vo(next_node.getNexTTask_vo());
                            }
                        }   
                        else {
                            isWaiting=true;
                        }
                        break;       
                    case NodeType.AND_SPLIT_NODE://与发散
                        RecordVo _taskvo0=new RecordVo("t_wf_task");
                        _taskvo0.setInt("task_id",this.taskid);
                        _taskvo0=dao.findByPrimaryKey(_taskvo0);
                        count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                        ArrayList nodelist0=workflowBo.getNextNode(_taskvo0.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                        _taskvo0=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                        task_vo=_taskvo0;
                        //往各个节点创建任务 
                        taskList=saveTasks(nodelist0,actor,workflowBo,count); 
                         
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        }
                    
                        break;
                    case NodeType.ADN_JOIN_NODE://与汇聚和ADN_SPLIT_NODE配对，需要同步
                        ArrayList objNumList=workflowBo.getContinueObj_AND(this.taskid,this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),next_node,1);
                        if(objNumList.size()>0)
                        {
                            //将暂停状态的任务改成结束状态
                            workflowBo.taskState_stopToEnd(this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),next_node.getNode_id(),1);
                            
                            next_node.setSeqnumList(objNumList);
                            RecordVo _taskvo=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);  
                            task_vo=_taskvo;
                            ArrayList _nextlist=next_node.getNextNodeList(objNumList);
                            next_node=(WF_Node)_nextlist.get(0);   
                            next_node.setSeqnumList(objNumList);
                            
                            if(next_node.getNodetype()==2&&isNullNode(next_node.getNode_id())) //与发散后跟空节点 20160627 参考或发散
                            {
                                this.bs_flag="4"; //空任务
                                RecordVo taskvo_temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);
                                this.bs_flag="1"; //待批任务
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                            }
                            else if(next_node.getNodetype()==NodeType.HUMAN_NODE||next_node.getNodetype()==NodeType.TOOL_NODE)
                            {
                                RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_WAINTING,actor,count);
                                next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),_taskvo2.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                                this.setNexTTask_vo(_taskvo2); 
                            } 
                            else if(next_node.getNodetype()==NodeType.AND_SPLIT_NODE)
                            {
                                 
                                ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                                count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                                RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                                _taskvo2.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                                 
                                this.task_vo=_taskvo2; //_taskvo;
                                //往各个节点创建任务 
                                taskList=saveTasks(nodelist,actor,workflowBo,count); 
                                for(int i=0;i<taskList.size();i++)
                                {  
                                    RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                    if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                    {
                                        this.task_vo=taskvo_temp;
                                        handleNullNode(taskvo_temp,instancevo,actor);
                                         
                                    }
                                    else
                                    {
                                    	this.setNexTTask_vo(taskvo_temp);
                                    	this.nextTaskVoList.add(taskvo_temp);
                                    }
                                }
                                 
                            }
                            else if(next_node.getNodetype()==NodeType.OR_SPLIT_NODE)
                            { 
                                    ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                                    count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                                    RecordVo  temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);  
                                    this.task_vo=temp;
                                    //往各个节点创建任务 
                                    taskList=saveTasks(nodelist,actor,workflowBo,count); 
                                     
                                    for(int i=0;i<taskList.size();i++)
                                    {  
                                        RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                        if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                        {
                                            this.task_vo=taskvo_temp;
                                            handleNullNode(taskvo_temp,instancevo,actor);
                                             
                                        }
                                        else
                                        {
                                        	this.setNexTTask_vo(taskvo_temp);
                                        	this.nextTaskVoList.add(taskvo_temp);
                                        }
                                    }
                                
                            }
                            else
                            {
                                next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                                if(next_node.nodetype==NodeType.ADN_JOIN_NODE||NodeType.ADN_JOIN_NODE==NodeType.OR_JOIN_NODE)
                                {
                                    next_node.createTask(instancevo,actor,new ArrayList());
                                }
                                else {
                                    next_node.createTask(instancevo,actor,objNumList);
                                }
                                this.setNexTTask_vo(next_node.getNexTTask_vo());
                            }
                        } 
                        else {
                            isWaiting=true;
                        }
                        break;  
                    default:
                        next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                        next_node.createTask(instancevo,actor,seqnumList);
                        break;
                }
                dao.update("update t_wf_task_objlink set state=1 where tab_id="+this.tabid+" and state<>3 and task_id="+selfTaskid+" and submitflag=1");
                break;
            case NodeType.AND_SPLIT_NODE://与发散
                {
                    ArrayList nodelist=workflowBo.getNextNode(this.task_vo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                    
                    RecordVo _taskvo2=saveTask(this,NodeType.TASK_FINISHED,actor,count); 
                    count=workflowBo.getSplitMaxCount(_taskvo2.getInt("node_id"),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                     
                    _taskvo2.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                    task_vo=_taskvo2;
                    //往各个节点创建任务 
                    ArrayList taskList=saveTasks(nodelist,actor,workflowBo,count); 
                     
                    for(int i=0;i<taskList.size();i++)
                    {  
                        RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                        this.task_vo=taskvo_temp;
                        if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                        { 
                            handleNullNode(taskvo_temp,instancevo,actor);
                         
                        }
                        else
                        {
                        	this.setNexTTask_vo(taskvo_temp);
                        	this.nextTaskVoList.add(taskvo_temp);
                        }
                    }
                
                }
                
            break;
            case NodeType.ADN_JOIN_NODE://与汇聚和ADN_SPLIT_NODE配对，需要同步 
                ArrayList objNumList=workflowBo.getContinueObj_AND(this.taskid,this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),this,1);
                if(objNumList.size()>0)//||(objNumList.size()==0&&seqnumList.size()>0))
                {
                     
                    this.setSeqnumList(objNumList);
                    if(objNumList.size()==0&&seqnumList.size()>0) {
                        this.setSeqnumList(seqnumList);
                    }
                    RecordVo _taskvo=saveTask(this,NodeType.TASK_FINISHED,actor,count);  
                    task_vo=_taskvo;
                    ArrayList _nextlist=this.getNextNodeList(objNumList);
                    next_node=(WF_Node)_nextlist.get(0);   
                    next_node.setSeqnumList(objNumList);
                    if(objNumList.size()==0&&seqnumList.size()>0) {
                        next_node.setSeqnumList(seqnumList);
                    }
                    
                    if(next_node.getNodetype()==2&&isNullNode(next_node.getNode_id())) //或发散后是空节点的情况 20160627
                    {
                        this.bs_flag="4"; //空任务
                        RecordVo taskvo_temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);
                        this.bs_flag="1"; //待批任务
                        this.task_vo=taskvo_temp;
                        handleNullNode(taskvo_temp,instancevo,actor);
                    }
                    else if(next_node.getNodetype()==NodeType.HUMAN_NODE||next_node.getNodetype()==NodeType.TOOL_NODE)
                    {
                        RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_WAINTING,actor,count);
                        next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),_taskvo2.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                        this.setNexTTask_vo(_taskvo2); 
                    } 
                    else if(next_node.getNodetype()==NodeType.AND_SPLIT_NODE)
                    {
                         
                        ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                        count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                        RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                        _taskvo2.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                     
                        task_vo=_taskvo2; //_taskvo;
                        //往各个节点创建任务 
                        ArrayList taskList=saveTasks(nodelist,actor,workflowBo,count); 
                         
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        }
                    }
                    else if(next_node.getNodetype()==NodeType.OR_SPLIT_NODE)
                    { 
                            ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                            count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                            RecordVo  temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count);  
                            this.task_vo=temp;
                            //往各个节点创建任务 
                            ArrayList taskList=saveTasks(nodelist,actor,workflowBo,count); 
                             
                            for(int i=0;i<taskList.size();i++)
                            {  
                                RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                {
                                    this.task_vo=taskvo_temp;
                                    handleNullNode(taskvo_temp,instancevo,actor);
                               
                                }
                                else
                                {
                                	this.setNexTTask_vo(taskvo_temp);
                                	this.nextTaskVoList.add(taskvo_temp);
                                }
                            }
                            
                    }
                    else
                    {
                        next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                        next_node.createTask(instancevo,actor,objNumList);
                        this.setNexTTask_vo(next_node.getNexTTask_vo());
                    }
                } 
                
                
                break;
            case NodeType.OR_SPLIT_NODE://或发散
                ArrayList _nodelist=workflowBo.getNextNode(this.task_vo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                //bug 50957 空节点后跟或发散节点 或发散节点计算count有问题，应当是先查或发散节点下以及子节点最大count 然后当前节点与子节点写入计算后的count
                count=workflowBo.getSplitMaxCount(this.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                RecordVo  temp=saveTask(this,NodeType.TASK_FINISHED,actor,count);   
                this.task_vo=temp;
                //往各个节点创建任务 
                ArrayList _taskList=saveTasks(_nodelist,actor,workflowBo,count); 
                 
                for(int i=0;i<_taskList.size();i++)
                {  
                    RecordVo  taskvo_temp=(RecordVo)_taskList.get(i);
                    this.task_vo=taskvo_temp;
                    if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                    {
                        handleNullNode(taskvo_temp,instancevo,actor);
                         
                    }
                    else
                    {
                    	this.setNexTTask_vo(taskvo_temp);
                    	this.nextTaskVoList.add(taskvo_temp);
                    }
                }
                
                break;
            case NodeType.OR_JOIN_NODE://或汇聚 
                
                ArrayList objNumList_or=workflowBo.getContinueObj_AND(this.taskid,this.ins_vo.getInt("ins_id"),Integer.parseInt(this.tabid),this,2);
                if(objNumList_or.size()>0)//||(objNumList_or.size()==0&&seqnumList.size()>0))
                {
                     
                    this.setSeqnumList(objNumList_or);
                    if(objNumList_or.size()==0&&seqnumList.size()>0) {
                        this.setSeqnumList(seqnumList);
                    }
                    RecordVo _taskvo=saveTask(this,NodeType.TASK_FINISHED,actor,count);  
                    task_vo=_taskvo;
                    ArrayList _nextlist=this.getNextNodeList(objNumList_or);
                    next_node=(WF_Node)_nextlist.get(0);   
                    next_node.setSeqnumList(objNumList_or);
                    if(objNumList_or.size()==0&&seqnumList.size()>0) {
                        next_node.setSeqnumList(seqnumList);
                    }
                    
                    if(next_node.getNodetype()==NodeType.HUMAN_NODE||next_node.getNodetype()==NodeType.TOOL_NODE)
                    {
                        RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_WAINTING,actor,count);
                        next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),_taskvo2.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                        this.setNexTTask_vo(_taskvo2); 
                    } 
                    else if(next_node.getNodetype()==NodeType.AND_SPLIT_NODE)
                    {
                         
                        ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                        count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                        RecordVo _taskvo2=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                        _taskvo2.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                        
                         
                        this.task_vo=_taskvo2; //_taskvo;
                        //往各个节点创建任务 
                        ArrayList taskList=saveTasks(nodelist,actor,workflowBo,count); 
                         
                        for(int i=0;i<taskList.size();i++)
                        {  
                            RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                            if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                            {
                                this.task_vo=taskvo_temp;
                                handleNullNode(taskvo_temp,instancevo,actor);
                                 
                            }
                            else
                            {
                            	this.setNexTTask_vo(taskvo_temp);
                            	this.nextTaskVoList.add(taskvo_temp);
                            }
                        } 
                    }
                    else if(next_node.getNodetype()==NodeType.OR_SPLIT_NODE)
                    { 
                            ArrayList nodelist=workflowBo.getNextNode(_taskvo.getInt("task_id"),this.ins_vo.getInt("ins_id"),"");
                            count=workflowBo.getSplitMaxCount(next_node.getNode_id(),Integer.parseInt(tabid),this.ins_vo.getInt("ins_id"));
                            temp=saveTask(next_node,NodeType.TASK_FINISHED,actor,count); 
                            this.task_vo=temp;
                            //往各个节点创建任务 
                            ArrayList taskList=saveTasks(nodelist,actor,workflowBo,count); 
                             
                            for(int i=0;i<taskList.size();i++)
                            {  
                                RecordVo  taskvo_temp=(RecordVo)taskList.get(i);
                                if("4".equals(taskvo_temp.getString("bs_flag"))) //空任务
                                {
                                    this.task_vo=taskvo_temp;
                                    handleNullNode(taskvo_temp,instancevo,actor);
                                     
                                }
                                else
                                {
                                	this.setNexTTask_vo(taskvo_temp);
                                	this.nextTaskVoList.add(taskvo_temp);
                                }
                            } 
                    }
                    else
                    {
                        next_node.setNodeParam(String.valueOf(this.tablebo.getTabid()),task_vo.getInt("task_id"),"templet_"+this.tablebo.getTabid(),this.tablebo); 
                        next_node.createTask(instancevo,actor,objNumList_or);
                        this.setNexTTask_vo(next_node.getNexTTask_vo());
                    }
                } 
                
                 
                break;   
            case NodeType.END_NODE: //终止结点
                task_vo=saveTask(this,NodeType.TASK_FINISHED,actor,count);  
                this.setNexTTask_vo(task_vo);
                break;
            }
            
            
            
            
            
            if(task_vo!=null&&task_vo.getDate("end_date")!=null) {
                instancevo.setDate("end_date",task_vo.getDate("end_date"));
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
    
    
    
    public RecordVo saveWarningTask(WF_Node next_node,WF_Actor actor,int count,ContentDAO dao)
    {
        RecordVo taskvo=null;
        try
        {
            taskvo=saveTask(next_node,NodeType.TASK_WAINTING,actor,count);  
            if(next_node.nodetype==NodeType.HUMAN_NODE) {
                updateTaskActor_nomail(taskvo,actor);
            }
            if(taskvo.getString("actorid")==null|| "".equals(taskvo.getString("actorid"))) {
                throw new GeneralException(ResourceFactory.getProperty("error.nodefine.applyobj"));
            }
            taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id"));
            
            if(this.task_vo.getString("task_id_pro")!=null&&this.task_vo.getString("task_id_pro").trim().length()>0)
            {
                taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
            }
            else {
                taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id"));
            }
            dao.updateValueObject(taskvo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return taskvo;
    }
    
    
    
    
    /**
     * 获得任务节点当前处理的次数
     */
    public int getCountByTaskID(int task_id)throws GeneralException
    {
        int count=0;
        RowSet rowSet=null;
        try
        { 
            ContentDAO dao=new ContentDAO(this.conn);
            rowSet=dao.search("select count from t_wf_task_objlink where task_id="+task_id);
            if(rowSet.next()) {
                count=rowSet.getInt(1);
            }
            if("2".equalsIgnoreCase(this.tablebo.getReject_type())&&task_id>0){
            	RecordVo vo = new RecordVo("t_wf_task");
				vo.setInt("task_id", task_id);
				vo = dao.findByPrimaryKey(vo);
				String ins_id=vo.getString("ins_id");
            	int beginCount=TemplateStaticDataBo.getBeginCount(ins_id, this.conn, String.valueOf(this.tablebo.getTabid()));
            	if(beginCount>count){
            		count=beginCount;
            	}
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
     * 获得任务节点当前处理的次数（适用于驳回到发起人）
     */
    public int getCountByTaskIDToSponsor(int ins_id)throws GeneralException
    {
        int count=0;
        RowSet rowSet=null;
        try
        { 
            ContentDAO dao=new ContentDAO(this.conn);
            rowSet=dao.search("select max(count) from t_wf_task_objlink where ins_id="+ins_id);
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
    
    
    
    public void rejectTask_manul(ArrayList seqList,RecordVo _task_vo,RecordVo instancevo,WF_Actor actor)
    {
        try
        {
            this.ins_vo=instancevo;
            ContentDAO dao=new ContentDAO(this.conn);
            ArrayList nodeList=getPreNodeList_manual(seqList,_task_vo,instancevo);
            this.params="1";  //自定义流程的任务param=1
            if(nodeList.size()==0) //驳回到主流程或发起人节点 
            {
                int tabid=instancevo.getInt("tabid");
                String sql="select * from t_wf_task where task_id=(select task_id from t_wf_node_manual where tabid="+tabid+" and ins_id="+instancevo.getInt("ins_id")+" and id="+_task_vo.getInt("node_id")+")";
                RowSet rset=dao.search(sql);
                if(rset.next())
                {
                    WF_Node wf_node=new WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
                    wf_node.setNode_id(rset.getInt("node_id"));
                    wf_node.setNodename(rset.getString("actorname"));
                    wf_node.setActorid(rset.getString("actorid"));
                    wf_node.setActorname(rset.getString("actorname"));
                    wf_node.setActor_type(rset.getString("actor_type"));
                    wf_node.setNodetype(NodeType.HUMAN_NODE); //人工节点
                    wf_node.setTabid(String.valueOf(tabid)); 
                    if(seqList!=null&&seqList.size()>0) {
                        wf_node.setSeqnumList(seqList);
                    }
                    if (this.tablebo!=null){
                        wf_node.setTablebo(this.tablebo);
                    }
                    nodeList.add(wf_node);
                    this.params="";
                }
        		String _sql ="select * from templet_"+this.tablebo.getTabid()+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
		   	    String tablename="templet_"+this.tablebo.getTabid();
		   	    WF_Instance wf_ins=new WF_Instance(tablebo, conn);
		   	    wf_ins.insertKqApplyTable(_sql,String.valueOf(this.tablebo.getTabid()),"","07",tablename); //往考勤申请单中写入报批记录
            }
            else{
				//驳回给非发起人也调用考勤接口修改审批人信息
        		String _sql ="select * from templet_"+this.tablebo.getTabid()+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
            	KqAppInterface kqapp=new KqAppInterface(this.conn,tablebo.getUserview());
            	kqapp.synApproverRejectKqApp(this.tablebo.getTabid()+"", _sql);
            }
            for(int i=0;i<nodeList.size();i++)
            {
                
                WF_Node wf_node=(WF_Node)nodeList.get(i); 
                actor.setActorid(wf_node.getActorid());
                actor.setActorname(wf_node.getActorname());
                actor.setActortype(wf_node.getActor_type());
                RecordVo rej_taskvo=saveTask(wf_node,NodeType.TASK_WAINTING,actor,1);  
                rej_taskvo.setString("state","07"); 
                if(actor!=null) {
                    rej_taskvo.setString("task_pri",actor.getEmergency());
                }
                rej_taskvo.setInt("pri_task_id", _task_vo.getInt("task_id"));
                if(_task_vo.getString("task_id_pro")!=null&&_task_vo.getString("task_id_pro").trim().length()>0)
                {
                    rej_taskvo.setString("task_id_pro",","+rej_taskvo.getInt("task_id")+","+_task_vo.getInt("task_id")+_task_vo.getString("task_id_pro"));
                }
                else {
                    rej_taskvo.setString("task_id_pro",","+rej_taskvo.getInt("task_id")+","+_task_vo.getInt("task_id"));
                }
                dao.updateValueObject(rej_taskvo);   
                
            }
            this.params="";
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 重新创建下一审批层级的新任务(自定义审批流程)
     * @param instancevo
     * @return
     * @throws GeneralException
     */
    public void reCreateTask_manual(RecordVo instancevo,WF_Actor actor,RecordVo task_vo,TemplateTableBo tablebo)throws GeneralException
    {
         
        try
        {
            this.ins_vo=instancevo;
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select seqnum from  t_wf_task_objlink where task_id="+task_vo.getInt("task_id")+" and (  state<>3 or state is null )   "); //重新分派不应该限制死，人员就丢失了and submitflag=1 
            ArrayList seqnumList=new ArrayList();
            while(rowSet.next()) {
                seqnumList.add(rowSet.getString("seqnum"));
            }
            ArrayList nextnodelist=getNextNodeList_manual(seqnumList,task_vo.getInt("node_id"),instancevo.getInt("ins_id"),task_vo.getInt("task_id"),tablebo,false);
            this.params="1";  //自定义流程的任务param=1
            for(int i=0;i<nextnodelist.size();i++)
            {
                WF_Node wf_node=(WF_Node)nextnodelist.get(i); 
                actor.setActorid(wf_node.getActorid());
                actor.setActorname(wf_node.getActorname());
                actor.setActortype("1");
                RecordVo _taskvo=saveTask(wf_node,NodeType.TASK_WAINTING,actor,1); 
                _taskvo.setInt("pri_task_id", task_vo.getInt("task_id"));
                if(task_vo.getString("task_id_pro")!=null&&task_vo.getString("task_id_pro").trim().length()>0)
                {
                    _taskvo.setString("task_id_pro",","+_taskvo.getInt("task_id")+","+task_vo.getInt("task_id")+task_vo.getString("task_id_pro"));
                }
                else {
                    _taskvo.setString("task_id_pro",","+_taskvo.getInt("task_id")+","+task_vo.getInt("task_id"));
                }
                dao.updateValueObject(_taskvo); 
            }
            params=""; 
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
         
    }
    
    
    
    /**
     * 重新创建一个新的任务
     * @param instancevo
     * @return
     * @throws GeneralException
     */
    public RecordVo reCreateTask(RecordVo instancevo,WF_Actor actor,boolean isFirstReject)throws GeneralException
    {
        RecordVo taskvo=null;
        try
        {
            this.ins_vo=instancevo;
            
            int count=this.getCountByTaskID(this.taskid);
            if(isFirstReject) //如果是驳回，并且是报批后的首次驳回
            {
                count++;
            }
            taskvo=saveTask(this,NodeType.TASK_WAINTING,actor,count);
            
            if(this.ext_param!=null&&this.ext_param.length()>0) // 20140912  dengcan  如果驳回的节点是取自表单  发起--》表单直接领导 --》报批人直接领导--》BS---》驳回报错
            {
                Document doc=null;
                Element element=null; 
                doc=PubFunc.generateDom(ext_param);; 
                String xpath="/params/special_role";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                element=(Element)findPath.selectSingleNode(doc);
                String from_nodeid="";
                String item_id="";
                RowSet rowSet=null;
                if(element!=null)
                {
                    if(element.getAttributeValue("from_nodeid")!=null&&element.getAttributeValue("from_nodeid").trim().length()>0)  
                    {
                        from_nodeid=element.getAttributeValue("from_nodeid");
                    } 
                    if(element.getAttributeValue("item_id")!=null&&element.getAttributeValue("item_id").trim().length()>0)  
                    {
                        item_id=element.getAttributeValue("item_id");
                    } 
                } 
                if("reportNode".equalsIgnoreCase(from_nodeid))  //取自报批人
                {
                    updateTaskActor(taskvo,actor); 
                }
                doc=null;
            } 
            
    //      updateTaskActor(taskvo,actor); 
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return taskvo;
    }
    /**
     * 重新创建一个新的任务(适用于驳回到发起人)
     * @param instancevo
     * @return
     * @throws GeneralException
     */
    public RecordVo reCreateTaskToSponsor(RecordVo instancevo,WF_Actor actor)throws GeneralException
    {
        RecordVo taskvo=null;
        try
        {
            this.ins_vo=instancevo;
            
            int count=this.getCountByTaskIDToSponsor(instancevo.getInt("ins_id"));//t_wf_task_objlink中count最大值
            count++;
            taskvo=saveTask(this,NodeType.TASK_WAINTING,actor,count);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return taskvo;
    }
    
    /**
     * 与发散创建多个任务
     * @param nodelist
     * @param actor
     * @return
     * @throws GeneralException
     */
    private ArrayList saveTasks(ArrayList nodelist,WF_Actor actor,WorkflowBo workflowBo,int count )throws GeneralException
    {
        ArrayList list=new ArrayList();
        ArrayList specialRoleUserList=actor.getSpecialRoleUserList();
        HashMap roleUserMap=new HashMap();
        for(int i=0;i<specialRoleUserList.size();i++)
        {
            String temp=(String)specialRoleUserList.get(i);
            String[] temps=temp.split(":");
            roleUserMap.put(temps[0].trim(),temps[1]);
        }
         
        RowSet rowSet=null;
        try
        { 
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo taskvo=null;
            for(int i=0;i<nodelist.size();i++)
            {
                WF_Node wf_node=(WF_Node)nodelist.get(i);
                int nodeid=wf_node.getNode_id();
                WF_Actor _wf_actor=wf_node.getWf_Actor(nodeid);
                if(_wf_actor!=null&& "2".equalsIgnoreCase(_wf_actor.getActortype()))
                {
                    rowSet=dao.search("select * from t_sys_role where role_id='"+_wf_actor.getActorid()+"'");
                    if(rowSet.next())
                    {
                        int role_property=rowSet.getInt("role_property");
                        if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
                        {
                            String userStr="";
                            if(roleUserMap.get(String.valueOf(nodeid))==null)
                            { 
                                String ext_param=wf_node.getExt_param();
                                String selfapply="0";
                                if(this.tablebo!=null&&this.tablebo.isBEmploy()) {
                                    selfapply="1";
                                }
                                LazyDynaBean abean=null;
                                if("0".equals(this.fromOri)&&isStartWorkflow)  //流程发起且人员信息已提交到临时表中 2014-7-17  邓灿
                                {
                                    abean=this.workflowBo.getFromNodeid_role(ext_param,this.ins_vo.getInt("ins_id"),dao,this.task_vo.getInt("task_id"),"0","");
                                } else {
                                    abean=this.workflowBo.getFromNodeid_role(ext_param,this.ins_vo.getInt("ins_id"),dao,this.task_vo.getInt("task_id"),selfapply,this.cd_whl);
                                }
                                
                                if("gwgx".equalsIgnoreCase(this.tablebo.getRelation_id())) //标准岗位关系
                                {
                                    userStr=getSuperPos_userStr(abean,role_property);
                                    if(userStr!=null&&userStr.trim().length()==0)
                                    {
                                    	throw new GeneralException("按职位汇报关系定义的上级领导职位没有关联审批人员!");
                                    }
                                }
                                else
                                {
                                    String sql="";
                            //      if(this.tablebo.getUserview().getStatus()==4)  //自助用户
                                    {
                                        HashMap a_map=workflowBo.getSuperSql(role_property,this.tablebo.getRelation_id(),abean);
                                        if(a_map.size()==0) {
                                            throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
                                        }
                                        sql=(String)a_map.get("sql"); 
                                     
                                    }
                                /*  else  //业务用户
                                    {
                                        sql="select *  from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
                                        sql+=" and SP_GRADE="+role_property+" " ; 
                                    } 
                                    */
                                    rowSet=dao.search(sql);
                                    if(rowSet.next()) {
                                        userStr=rowSet.getString("mainbody_id")+"`"+rowSet.getString("a0101");
                                    }
                                } 
                            }
                            else
                            {
                                userStr=(String)roleUserMap.get(String.valueOf(nodeid));
                            }
                            taskvo=saveTask2(wf_node,actor,workflowBo,userStr,dao,count);
                        }
                        else
                        {
                            taskvo=saveTask(wf_node,NodeType.TASK_WAINTING,actor,count);
                        }
                    }else{
                        String nodename="";
                        rowSet=dao.search("select nodename from t_wf_node where node_id="+nodeid);//查询出角色的名称
                        if(rowSet.next()){
                            nodename=rowSet.getString("nodename");
                        }
                        throw new GeneralException(nodename+ResourceFactory.getProperty("general.template.wf_node.role.notexist"));
                    }
                }
                else
                {
                    /*
                    if(wf_node.getNodetype()==NodeType.OR_JOIN_NODE||wf_node.getNodetype()==NodeType.ADN_JOIN_NODE)
                    {
                        list.add(wf_node);
                        return list;
                    }
                    else*/
                    if(wf_node.getNodetype()==2&&isNullNode(wf_node.getNode_id()))
                    {
                        this.bs_flag="4"; //空任务
                        taskvo=saveTask(wf_node,NodeType.TASK_FINISHED,actor,count);
                        this.bs_flag="1"; //待批任务
                    }
                    else {
                        taskvo=saveTask(wf_node,NodeType.TASK_WAINTING,actor,count);
                    }
                } 
                list.add(taskvo);   
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
        return list;
    }
    
    
    /**
     * 是否空节点
     * @param node_id
     * @return
     */
    private boolean isNullNode(int node_id)
    {
        boolean flag=false;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select count(node_id) from t_wf_actor where node_id="+node_id);
            if(rowSet.next())
            {
                if(rowSet.getInt(1)==0) {
                    flag=true;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 获得标准职位汇报关系中的上级领导
     * @param userview
     * @return
     */
    private String getSuperPos_userStr(LazyDynaBean abean)
    {
        return getSuperPos_userStr(abean,9);
    }
    
    
    /**
     * 获得标准职位汇报关系中的上级领导 扩展此方法 可获取直接上级、主管等领导
     * @param userview
     * @return
     */
    private String getSuperPos_userStr(LazyDynaBean abean,int role_property)
    {
        String userStr="";
        try
        {
            String superpos_item="";
            RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR"); 
            superpos_item=vo.getString("str_value"); 
            String e0122_value=(String)abean.get("value"); 
             
            //取得上级汇报岗位
            String post="";
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=null;
            /*
            RowSet rowSet=dao.search("select "+superpos_item+" from k01 where e01a1='"+e0122_value+"'");
            if(rowSet.next())
            {
                if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
                    post=rowSet.getString(1);
            }
            */
            if(workflowBo==null) {
                workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.tablebo.getUserview());
            }
            post=workflowBo.getSuperPosition(e0122_value, superpos_item, role_property);
             /**登录参数表*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String nbase_str="";
            if(login_vo!=null) {
                nbase_str = login_vo.getString("str_value");//.toLowerCase();
            }
            if(nbase_str==null||nbase_str.trim().length()==0) {
                throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nodefineloginbase")+"!");
            }
            String[] temps=nbase_str.split(",");
            int num=0;
            for(int j=0;j<temps.length;j++)
            {
                if(temps[j].trim().length()>0)
                {
                    rowSet=dao.search("select a0101,a0100 from "+temps[j].trim()+"A01 where e01a1='"+post+"'");
                    if(rowSet.next())
                    {
                        userStr=temps[j].trim()+rowSet.getString("a0100")+"`"+rowSet.getString("a0101");
                    }
                } 
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return userStr;
    }
    
    
    private RecordVo saveTask2(WF_Node wf_node,WF_Actor actor,WorkflowBo workflowBo,String userStr,ContentDAO dao,int count) throws GeneralException
    {
        RecordVo taskvo=null;
        try
        {
            
            taskvo=new RecordVo("t_wf_task");
            IDGenerator idg=new IDGenerator(2,this.conn);       
            taskvo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
            taskvo.setString("task_topic",this.ins_vo.getString("name"));
            taskvo.setInt("node_id",wf_node.getNode_id());
            taskvo.setInt("ins_id",this.ins_vo.getInt("ins_id"));
            taskvo.setDate("start_date",new Date()); //DateStyle.getSystemTime());
            taskvo.setString("task_type","2"); 
            taskvo.setString("task_pri",actor.getEmergency());//任务优先级
            taskvo.setString("sp_yj",actor.getSp_yj());//审批意见
            taskvo.setString("content",actor.getContent());//审批意见描述 
            taskvo.setString("state","08"); //审批状态,报批状态
            taskvo.setInt("bread",0);//是否已阅读
            taskvo.setString("task_state",String.valueOf(NodeType.TASK_WAINTING));
            /**根据任务分配算法，具体对应到准*/
            taskvo.setString("actorid","");//当前对象   流程定义的参与者                
            taskvo.setString("a0100","");//人员编号 实际处理人员编码
            taskvo.setString("a0101","");//人员姓名 实际处理人员姓名        
            taskvo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//发送人
            taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName());//发送人姓名
            taskvo.setString("bs_flag",bs_flag);  //=1 or =null，报批任务（任务参与者可以审批，提交）  =2，加签任务(任务参与者，可以审批，提交)  =3 报备任务 =4 空任务，自动流转
            taskvo.setString("appuser", this.ins_vo.getString("actorid")+",");
            
            taskvo.setString("url_addr","");//审批网址
            taskvo.setString("params","");//参数    
            taskvo.setString("actorid",userStr.split("`",-1)[0]);//当前对象    流程定义的参与者
            taskvo.setString("actor_type",workflowBo.getT_wf_relationVo().getString("actor_type"));
            taskvo.setString("actorname",userStr.split("`",-1)[1]);
            
     
            taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
            taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
             
            
            WF_Actor actor_t=new WF_Actor();
            actor_t.setActorid(userStr.split("`",-1)[0]);
            actor_t.setActorname(userStr.split("`",-1)[1]);
            RecordVo t_wf_relationVo=new RecordVo("t_wf_relation"); 
            if("gwgx".equalsIgnoreCase(this.tablebo.getRelation_id()))
            {
                actor_t.setActortype("1");
                taskvo.setString("actor_type","1");
            }
            else
            {
                t_wf_relationVo.setInt("relation_id", Integer.parseInt(this.tablebo.getRelation_id()));
                t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
                actor_t.setActortype(t_wf_relationVo.getString("actor_type"));
            } 
            
            if(wf_node.getSeqnumList()!=null&&wf_node.getSeqnumList().size()>0)
            {
                    StringBuffer seqnum_str=new StringBuffer("");
                    int n=0;
                    ArrayList recordList=new ArrayList();
                    for(int i=0;i<wf_node.getSeqnumList().size();i++)
                    {
                        String seqnum=(String)wf_node.getSeqnumList().get(i);
                        
                        RecordVo vo=new RecordVo("t_wf_task_objlink"); 
                        vo.setString("seqnum",seqnum);
                        vo.setInt("ins_id", this.ins_vo.getInt("ins_id"));
                        vo.setInt("task_id",taskvo.getInt("task_id"));
                        vo.setInt("tab_id", this.ins_vo.getInt("tabid"));
                        vo.setInt("node_id",wf_node.getNode_id());
                        vo.setInt("submitflag",1);
                        vo.setInt("count",count);
                        vo.setString("task_type","1"); 
                        vo.setInt("state",0);
                        recordList.add(vo); 
                        
                        if(n<5)//统一成5个
                        {
                            seqnum_str.append(" or  seqnum='"+seqnum+"' ");
                        }  
                        n++;
                    }
                    if(seqnum_str.length()>0)
                    { 
                         String _tablename=this.tabname;
                            if("0".equals(this.fromOri)&&isStartWorkflow)  //流程发起且人员信息已提交到临时表中 2014-7-17  邓灿
                            {
                                _tablename="templet_"+this.tabid;
                            }
                        taskvo.setString("task_topic",this.tablebo.getRecordBusiTopic(_tablename,seqnum_str.toString(),recordList.size()));
                        
                    }
                    dao.addValueObject(recordList);
            }  
            dao.addValueObject(taskvo); 
            sendMessage(actor_t,taskvo.getInt("task_id"),taskvo.getInt("node_id"));//发短信及邮件  下一审批节点已产生记录后再发送邮件   2014-7-16  邓灿
        }
        catch(Exception e)
        {
            Category.getInstance(this.getClass()).error("或发散新增流程失败，任务号"+taskvo.getString("task_id")+":"
            		+e.getMessage());
			throw GeneralExceptionHandler.Handle(e);
        }
        return taskvo;
    }
    

    private String bs_flag="1"; //=1 or =null，报批任务（任务参与者可以审批，提交）  =2，加签任务(任务参与者，可以审批，提交)  =3 报备任务 =4 空任务，自动流转
    private String params="";   //=1:自定义流程
    /**
     * 创建任务状态    (驳回时wf_actor目前只有四个属性有值。分别是:actorid,actorname,actortype,specialRoleUserList)
     */
    private RecordVo saveTask(WF_Node node,int task_state,WF_Actor wf_actor,int count)throws GeneralException
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
            taskvo.setDate("start_date",new Date()); //DateStyle.getSystemTime());
            taskvo.setString("task_type",String.valueOf(node.nodetype));
            taskvo.setString("bs_flag",bs_flag);  //=1 or =null，报批任务（任务参与者可以审批，提交）  =2，加签任务(任务参与者，可以审批，提交)  =3 报备任务 =4 空任务，自动流转

            if(wf_actor!=null) {
                taskvo.setString("task_pri",wf_actor.getEmergency());//任务优先级
            }
            
            boolean isFromBeginNode=false;//是否被驳回到起始节点
            if(("2".equals(this.opt)|| "4".equals(this.opt))&&node.nodetype==NodeType.START_NODE)
            {//如果当前节点是开始节点，并且是因为驳回才变为开始节点的，那么节点类型应设为人工节点
                 taskvo.setString("task_type","2");
                node.setNodetype(NodeType.HUMAN_NODE);
                isFromBeginNode=true;
            }
          
            //
            if(task_state==NodeType.TASK_WAINTING&&this.task_vo!=null)
            {
                taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
            }
            
            WF_Actor new_wf_actor=null; //下一审批节点已产生记录后再发送邮件   2014-7-16  邓灿
            
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
                taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());   
                taskvo.setString("task_state",String.valueOf(task_state));
                /**根据任务分配算法，具体对应到准*/
                taskvo.setString("actorid",this.ins_vo.getString("actorid"));//当前对象 流程定义的参与者
                if(this.ins_vo.getInt("actor_type")==0) {
                    taskvo.setString("actor_type","4"/*this.ins_vo.getString("actor_type")*/);
                } else {
                    taskvo.setString("actor_type",this.ins_vo.getString("actor_type")); //20131223 dengc 发起节点的任务参与者类型应该取自t_wf_instance
                }
                //  taskvo.setString("actor_type","1"/*this.ins_vo.getString("actor_type")*/);
                taskvo.setString("actorname",this.ins_vo.getString("actorname")/*wf_actor.getActorname()*/);
                taskvo.setString("a0100",this.ins_vo.getString("actorid"));//人员编号 实际处理人员编码
                taskvo.setString("a0101",this.ins_vo.getString("actorname"));//人员姓名 实际处理人员姓名
                taskvo.setString("a0100_1",this.ins_vo.getString("actorid")/*this.wf_actor.getActorid()*/);//发送人
                taskvo.setString("a0101_1",this.ins_vo.getString("actorname")/*this.wf_actor.getActorname()*/);//发送人姓名
                taskvo.setString("appuser", this.ins_vo.getString("actorid")+",");//手工指派
                taskvo.setString("url_addr","");//审批网址
                taskvo.setString("params","");//参数
                break;
            case NodeType.END_NODE:
                taskvo.setString("state","06"); //审批状态
                taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                taskvo.setString("content",wf_actor.getContent());//审批意见描述
                taskvo.setInt("bread",0);//是否已阅读
                taskvo.setDate("end_date",new Date()); //DateStyle.getSystemTime());    
                taskvo.setString("task_state",String.valueOf(task_state));
                /**根据任务分配算法，具体对应到准*/
                taskvo.setString("actorid",wf_actor.getActorid());//当前对象    流程定义的参与者     
                taskvo.setString("actor_type",wf_actor.getActortype());
                taskvo.setString("a0100",wf_actor.getActorid());//人员编号 实际处理人员编码
                taskvo.setString("a0101",wf_actor.getActorname());//人员姓名 实际处理人员姓名
                taskvo.setString("a0100_1",wf_actor.getActorid());//发送人
                taskvo.setString("a0101_1",wf_actor.getActorname());//发送人姓名             
                taskvo.setString("url_addr","");//审批网址
                taskvo.setString("params","");//参数  
                if(this.task_vo!=null) {
                    taskvo.setString("task_id_pro", this.task_vo.getString("task_id_pro"));
                }
                if(!"0".equals(isSendMessage))
                {
                    sendMessage(null,0,0);
                    sendFilingTasks(this.taskid,node.getExt_param(),node);
                }
//             // 普天代办
        //      if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
                {
                        MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,tablebo.getUserview());
                        String pre_pendingID="";
                        if(this.otherParaMap.get("pre_pendingID")!=null) {
                            pre_pendingID=(String)this.otherParaMap.get("pre_pendingID");
                        }
                        sysBo.sendDealWithInfo(pre_pendingID, 0,tabid, this.ins_vo.getInt("ins_id"),String.valueOf(taskid), "","");
                    
                }
                
                
                
                /**设置流程实例为结束状态*/
                this.ins_vo.setString("finished",String.valueOf(NodeType.TASK_FINISHED));
                this.ins_vo.setDate("end_date",new Date());  //DateStyle.getSystemTime());
                dao.updateValueObject(ins_vo);
                break;  
            case NodeType.HUMAN_NODE://人工结点
                taskvo.setString("state","08"); //审批状态,报批状态
                taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                taskvo.setString("content",wf_actor.getContent());//审批意见描述
                taskvo.setInt("bread",0);//是否已阅读
                //taskvo.setDate("end_date",DateStyle.getSystemTime());//?  
                taskvo.setString("task_state",String.valueOf(task_state));
                if("4".equals(this.bs_flag)) //空任务
                {
                    taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());    
                }
                /**根据任务分配算法，具体对应到准*/
                taskvo.setString("actorid","");//当前对象   流程定义的参与者                
                taskvo.setString("a0100","");//人员编号 实际处理人员编码
                taskvo.setString("a0101","");//人员姓名 实际处理人员姓名         
                String a0100=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100();
                if(a0100==null||a0100.length()==0) {
                    a0100=this.tablebo.getUserview().getUserName();
                }
                taskvo.setString("a0100_1",a0100/*this.wf_actor.getActorid()*/);//发送人
                if(this.actorname.length()>0) {
                    taskvo.setString("a0101_1",this.actorname);
                } else {
                    taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名
                }
                String appuser="";
                if(this.task_vo==null) {
                    appuser=this.ins_vo.getString("actorid")+",";
                } else
                {
                    appuser=this.task_vo.getString("appuser")==null?"":this.task_vo.getString("appuser"); 
                }
                taskvo.setString("appuser", appuser);
                
                taskvo.setString("url_addr","");//审批网址
                taskvo.setString("params",params);//参数  
                /**流程节点*/  //2009-3-6  WF_Actor没有地方写入新节点数据，读取的数据不对
                //得到当前节点是不是重新分派过来的
                boolean reAssgin = false;
                if("2".equals(this.opt)&&this.tablebo.getSp_mode()==0) {
                    reAssgin= this.reAssgin();
                }
                ArrayList actorlist=node.getActorList();//本节点所有参与者的列表。对于开始节点，肯定没有参与者
                if(wf_actor!=null) {
                    taskvo.setString("task_pri",wf_actor.getEmergency());
                }
                
                WF_Actor actor=null;
                
                
                if((task_state==NodeType.TASK_WAINTING|| "4".equals(this.bs_flag))&&node.getSeqnumList()!=null&&node.getSeqnumList().size()>0)
                {
                     
                        StringBuffer seqnum_str=new StringBuffer("");
                        int n=0;
                        ArrayList recordList=new ArrayList();
                        StringBuilder insertSql = new StringBuilder();
            			insertSql.append("insert into t_wf_task_objlink ");
            			insertSql.append("(task_type,seqnum,username,ins_id,task_id,tab_id,node_id,submitflag,state,count");
            			if("2".equals(this.opt)&&"2".equals(wf_actor.getActortype())) {//驳回的普通角色
            				insertSql.append(",locked_time");
            			}
            			insertSql.append(") values ");
            			insertSql.append("(?,?,?,?,?,?,?,?,?,?");
            			if("2".equals(this.opt)&&"2".equals(wf_actor.getActortype())) {//驳回的普通角色
            				insertSql.append(",?");
            			}
            			insertSql.append(")");
            			for(int i=0;i<node.getSeqnumList().size();i++)
                        {
            				ArrayList list = new ArrayList();
                            String seqnum=(String)node.getSeqnumList().get(i);
                            String username="";
                            if(seqnum.indexOf(":")!=-1) //20141220 dengcan 执行退回操作，如果上一流程节点是角色，需直接退回到报批人
                            {
                            	username=seqnum.split(":")[1];
                            	seqnum=seqnum.split(":")[0];
                            }
                            list.add("1");//task_type
                            list.add(seqnum);
                            list.add(username);
                            list.add(this.ins_vo.getInt("ins_id"));
                            list.add(taskvo.getInt("task_id"));
                            list.add(this.ins_vo.getInt("tabid"));
                            list.add(node.getNode_id());
                            
                            int defaultSelected=0;
                            if("1".equals(this.tablebo.getDefaultselected())|| "4".equals(this.bs_flag)) {
                                defaultSelected=1;
                            }
                            list.add(defaultSelected);//submitflag
                            /*if(node.nodetype==NodeType.AND_SPLIT_NODE||node.nodetype==NodeType.OR_SPLIT_NODE||node.nodetype==NodeType.END_NODE||node.nodetype==NodeType.OR_JOIN_NODE||node.nodetype==NodeType.ADN_JOIN_NODE)
                            	list.add(1);*/
                            if(node.nodetype==NodeType.END_NODE&&task_state==NodeType.TASK_FINISHED) {
                                list.add(1);//state
                            } else if(("4".equals(this.bs_flag)&&!"2".equals(this.opt))||node.nodetype==NodeType.AND_SPLIT_NODE||node.nodetype==NodeType.OR_SPLIT_NODE||node.nodetype==NodeType.OR_JOIN_NODE||node.nodetype==NodeType.ADN_JOIN_NODE) {
                                list.add(1);//state
                            } else {
                                list.add(0);//state
                            }
                            list.add(count);
                            if("2".equals(this.opt)&&"2".equals(wf_actor.getActortype())) {//驳回的普通角色
                            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            	Timestamp dateTime = new Timestamp(sdf.parse("2100-12-31").getTime());
                            	list.add(dateTime);
                            }
                            if(n<5)//统一显示5个 20151111
                            {
                                seqnum_str.append(" or  seqnum='"+seqnum+"' ");
                            } 
                            recordList.add(list);
                            n++;
                        }
            			if(recordList.size()>0) {
                            dao.batchInsert(insertSql.toString(),recordList);
                        }
                        if(seqnum_str.length()>0)
                        { 
                            String _tablename=this.tabname;
                            if("0".equals(this.fromOri)&&isStartWorkflow)  //流程发起且人员信息已提交到临时表中 2014-7-17  邓灿
                            {
                                _tablename="templet_"+this.tabid;
                            }
                            taskvo.setString("task_topic",this.tablebo.getRecordBusiTopic(_tablename,seqnum_str.toString(),recordList.size()));
                            
                        }
                     
                }
                 //如果不是空任务
                if(!"4".equals(this.bs_flag))
                {
                 
                    //审批模式=0自动流转，=1手工指派
                    if(this.tablebo.getSp_mode()==1)
                    {
                        actor=wf_actor;
                        
                        String actorStr="";
                        if("2".equalsIgnoreCase(actor.getActortype()))
                        {
                            if(this.taskid==-1)
                            {
                                //actorStr=getActoridStr(actor,"human",taskvo.getInt("task_id"));
                                actorStr=getActoridStr(actor,String.valueOf(node.node_id),0);
                            }
                            else {
                                actorStr=getActoridStr(actor,"human",this.taskid);//taskvo.getInt("task_id"));
                            }
                        }
                        if(actorStr.trim().length()>0)
                        {
                            actor.setActorid(actorStr.split("`")[0]);
                            actor.setActorname(actorStr.split("`")[1]);
                            if("gwgx".equals(this.tablebo.getRelation_id()))
                            {
                                actor.setActortype("1");
                            }
                            else
                            {
                                RecordVo t_wf_relationVo=new RecordVo("t_wf_relation"); 
                                t_wf_relationVo.setInt("relation_id", Integer.parseInt(this.tablebo.getRelation_id()));
                                t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
                                actor.setActortype(t_wf_relationVo.getString("actor_type"));
                            }
                        }
                        
                        taskvo.setString("actorid",actor.getActorid());//当前对象   流程定义的参与者
                        taskvo.setString("actor_type",actor.getActortype());
                        taskvo.setString("actorname",actor.getActorname());
                        new_wf_actor=actor;
                        //sendMessage(actor,taskvo.getInt("task_id"),taskvo.getInt("node_id"));//发短信及邮件
                    }
                    else//自动流转
                    {
                        if(actorlist.size()!=0||isFromBeginNode)
                        {
                            if(isFromBeginNode) {
                                actor=new WF_Actor();
                            } else {
                            	if(reAssgin) {
                                    actor  = wf_actor;
                                } else {
                                    actor=(WF_Actor)actorlist.get(0);
                                }
                            }
                            
                            if("2".equals(opt)) { //驳回
                                actor.setContent(wf_actor.getContent());
                                if("2".equalsIgnoreCase(actor.getActortype()))//驳回时判断 20171225 添加用于驳回到上一个选择的特殊角色
                                {
                                    actor.setSpecialRoleUserList(wf_actor.getSpecialRoleUserList());
                                }
                            }else if("4".equals(opt)) //重新分配 xieguiquan
                            {
                                actor=wf_actor;
                            } else if("2".equalsIgnoreCase(actor.getActortype())) {
                                actor.setSpecialRoleUserList(wf_actor.getSpecialRoleUserList());
                            }
                            if(isFromBeginNode)//如果被驳回到开始节点
                            {
                                actor.setActorid(wf_actor.getActorid());
                                actor.setActorname(wf_actor.getActorname());
                                actor.setActortype(wf_actor.getActortype());
                            }
                            else
                            {
                                String actorStr="";
                                if("2".equalsIgnoreCase(actor.getActortype()))
                                {
                                    if(this.taskid==-1)
                                    {
                                      //actorStr=getActoridStr(actor,"human",taskvo.getInt("task_id"));
                                        actorStr=getActoridStr(actor,String.valueOf(node.node_id),0);
                                    }
                                    else {
                                        actorStr=getActoridStr(actor,String.valueOf(node.node_id),this.taskid);//taskvo.getInt("task_id"));
                                    }
                                } 
                                else if("6".equals(actor.getActortype())) //发起人
                                {
                                    actor.setActorid(this.ins_vo.getString("actorid"));
                                    actor.setActorname(this.ins_vo.getString("actorname"));
                                    actor.setActortype(String.valueOf(this.ins_vo.getInt("actor_type")));
                                }
                                else if("5".equals(actor.getActortype())) //本人
                                {
                                    actor.setActorid("本人");
                                    actor.setActorname("本人");
                                    actor.setActortype("5");
                                }
                                
                                if(actorStr.trim().length()>0)
                                {
                                    actor.setActorid(actorStr.split("`")[0]);
                                    actor.setActorname(actorStr.split("`")[1]);
                                    if("gwgx".equals(this.tablebo.getRelation_id()))
                                    {
                                        actor.setActortype("1");
                                    }
                                    else
                                    {
                                        RecordVo t_wf_relationVo=new RecordVo("t_wf_relation"); 
                                        t_wf_relationVo.setInt("relation_id", Integer.parseInt(this.tablebo.getRelation_id()));
                                        t_wf_relationVo=dao.findByPrimaryKey(t_wf_relationVo);
                                        actor.setActortype(t_wf_relationVo.getString("actor_type"));
                                    }
                                } 
                            }
                            taskvo.setString("actorid",actor.getActorid());//当前对象   流程定义的参与者
                            taskvo.setString("actor_type",actor.getActortype());
                            taskvo.setString("actorname",actor.getActorname());
                            
                            new_wf_actor=actor;
                            //sendMessage(actor,taskvo.getInt("task_id"),taskvo.getInt("node_id"));//发短信及邮件
                        }
                        else
                        {
                        	if(actorlist.size()>0|| "4".equals(opt)){
                        		new_wf_actor=wf_actor; 
                        	}
                            if("4".equals(opt)){//重新分配 当前记录无审批节点的情况     不然分配不上
                            	taskvo.setString("actorid",new_wf_actor.getActorid());//当前对象   流程定义的参与者
                            	taskvo.setString("actor_type",new_wf_actor.getActortype());
                            	taskvo.setString("actorname",new_wf_actor.getActorname());
                            }
                            //sendMessage(wf_actor,taskvo.getInt("task_id"),taskvo.getInt("node_id"));//发短信及邮件
                        }
                    }
                }
            
                break;
            case NodeType.OR_SPLIT_NODE:
                 taskvo.setString("state","08"); //审批状态
                   if(wf_actor!=null)
                   {
                        taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                        taskvo.setString("content",wf_actor.getContent());//审批意见描述
                   }
                   taskvo.setInt("bread",1);//是否已阅读
                   taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());    
                   taskvo.setString("task_state",String.valueOf(task_state));
                    /**根据任务分配算法，具体对应到准*/
                   if(this.tablebo.getUserview().getStatus()==4)
                   {
                        taskvo.setString("actorid",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()); 
                        taskvo.setString("actor_type","1");
                        String _appuser=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()+","; 
                        
                   }
                   else
                   {
                       taskvo.setString("actorid",this.tablebo.getUserview().getUserName()); 
                       taskvo.setString("actor_type","4");
                       String _appuser=this.tablebo.getUserview().getUserName()+","; 
                      
                   } 
                    taskvo.setString("appuser",this.ins_vo.getString("actorid")+",");
                    taskvo.setString("actorname",this.tablebo.getUserview().getUserFullName()); 
                    taskvo.setString("a0100",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
                    taskvo.setString("a0101",this.tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
                    taskvo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//发送人
                    if(this.actorname.length()>0) {
                        taskvo.setString("a0101_1",this.actorname);
                    } else {
                        taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName());//发送人姓名
                    }
                    
                    taskvo.setString("url_addr","");//审批网址
                    taskvo.setString("params","");//参数
                break;
            case NodeType.OR_JOIN_NODE:
                taskvo.setString("state","08"); //审批状态
                   if(wf_actor!=null)
                   {
                        taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                        taskvo.setString("content",wf_actor.getContent());//审批意见描述
                   }
                   taskvo.setInt("bread",1);//是否已阅读
                   taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());    
                   taskvo.setString("task_state",String.valueOf(task_state));
                    /**根据任务分配算法，具体对应到准*/
                   if(this.tablebo.getUserview().getStatus()==4)
                   {
                        taskvo.setString("actorid",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()); 
                        taskvo.setString("actor_type","1");
                        String _appuser=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()+","; 
                        
                   }
                   else
                   {
                       taskvo.setString("actorid",this.tablebo.getUserview().getUserName()); 
                       taskvo.setString("actor_type","4");
                       String _appuser=this.tablebo.getUserview().getUserName()+","; 
                      
                   } 
                    taskvo.setString("appuser",this.ins_vo.getString("actorid")+",");
                    taskvo.setString("actorname",this.tablebo.getUserview().getUserFullName()/*wf_actor.getActorname()*/);
                    taskvo.setString("a0100",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
                    taskvo.setString("a0101",this.tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
                    taskvo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()/*this.wf_actor.getActorid()*/);//发送人
                    
                    if(this.actorname.length()>0) {
                        taskvo.setString("a0101_1",this.actorname);
                    } else {
                        taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名
                    }
  
                    taskvo.setString("url_addr","");//审批网址
                    taskvo.setString("params","");//参数
                break;
            case NodeType.AND_SPLIT_NODE:// 与发散
                   taskvo.setString("state","08"); //审批状态
                   if(wf_actor!=null)
                   {
                        taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                        taskvo.setString("content",wf_actor.getContent());//审批意见描述
                   }
                   taskvo.setInt("bread",1);//是否已阅读
                   taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());    
                   taskvo.setString("task_state",String.valueOf(task_state));
                    /**根据任务分配算法，具体对应到准*/
                   if(this.tablebo.getUserview().getStatus()==4)
                   {
                        taskvo.setString("actorid",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()); 
                        taskvo.setString("actor_type","1");
                        String _appuser=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()+","; 
                        
                   }
                   else
                   {
                       taskvo.setString("actorid",this.tablebo.getUserview().getUserName()); 
                       taskvo.setString("actor_type","4");
                       String _appuser=this.tablebo.getUserview().getUserName()+","; 
                      
                   }
                   taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
                   
                   taskvo.setString("appuser",this.ins_vo.getString("actorid")+",");
                   taskvo.setString("actorname",this.tablebo.getUserview().getUserFullName()/*wf_actor.getActorname()*/);
                   taskvo.setString("a0100",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
                   taskvo.setString("a0101",this.tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
                   taskvo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()/*this.wf_actor.getActorid()*/);//发送人
                   if(this.actorname.length()>0) {
                       taskvo.setString("a0101_1",this.actorname);
                   } else {
                       taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名
                   }
                    
                    taskvo.setString("url_addr","");//审批网址
                    taskvo.setString("params","");//参数
                break;
            case NodeType.ADN_JOIN_NODE:// 与汇聚
                   taskvo.setString("state","08"); //审批状态
                   if(wf_actor!=null)
                   {
                        taskvo.setString("sp_yj",wf_actor.getSp_yj());//审批意见
                        taskvo.setString("content",wf_actor.getContent());//审批意见描述
                   }
                   taskvo.setInt("bread",0);//是否已阅读
                   taskvo.setDate("end_date",new Date());//  DateStyle.getSystemTime());    
                   taskvo.setString("task_state",String.valueOf(task_state));
                    /**根据任务分配算法，具体对应到准*/
                   if(this.tablebo.getUserview().getStatus()==4)
                   {
                        taskvo.setString("actorid",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()); 
                        taskvo.setString("actor_type","1");
                        String _appuser=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()+","; 
                        
                   }
                   else
                   {
                       taskvo.setString("actorid",this.tablebo.getUserview().getUserName()); 
                       taskvo.setString("actor_type","4");
                       String _appuser=this.tablebo.getUserview().getUserName()+","; 
                      
                   }
                   
               
                    taskvo.setInt("pri_task_id", this.task_vo.getInt("task_id")); 
//                  if(this.task_vo.getString("task_id_pro")!=null&&this.task_vo.getString("task_id_pro").trim().length()>0)
//                  {
//                          taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id")+this.task_vo.getString("task_id_pro"));
//                  }
//                  else
//                          taskvo.setString("task_id_pro",","+taskvo.getInt("task_id")+","+this.task_vo.getInt("task_id"));   
                    taskvo.setString("appuser",this.ins_vo.getString("actorid")+",");
                    taskvo.setString("actorname",this.tablebo.getUserview().getUserFullName()/*wf_actor.getActorname()*/);
                    taskvo.setString("a0100",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100());//人员编号 实际处理人员编码
                    taskvo.setString("a0101",this.tablebo.getUserview().getUserFullName());//人员姓名 实际处理人员姓名
                    taskvo.setString("a0100_1",this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100()/*this.wf_actor.getActorid()*/);//发送人
                    if(this.actorname.length()>0) {
                        taskvo.setString("a0101_1",this.actorname);
                    } else {
                        taskvo.setString("a0101_1",this.tablebo.getUserview().getUserFullName()/*this.wf_actor.getActorname()*/);//发送人姓名
                    }
                    
                    taskvo.setString("url_addr","");//审批网址
                    taskvo.setString("params","");//参数
                break;
                
            }
            
            
            if((node.nodetype==NodeType.AND_SPLIT_NODE||node.nodetype==NodeType.OR_SPLIT_NODE||node.nodetype==NodeType.END_NODE||node.nodetype==NodeType.OR_JOIN_NODE||node.nodetype==NodeType.ADN_JOIN_NODE)&&task_state==NodeType.TASK_FINISHED)
            {
                if(node.getSeqnumList()!=null&&node.getSeqnumList().size()>0)
                {
                	StringBuffer seqnum_str=new StringBuffer("");
                    int n=0;
                    int m=1;
                    ArrayList recordList=new ArrayList(); 
                    StringBuilder insertSql = new StringBuilder();
        			insertSql.append("insert into t_wf_task_objlink ");
        			insertSql.append("(task_type,seqnum,username,ins_id,task_id,tab_id,node_id,submitflag,state,count");
        			insertSql.append(") values ");
        			insertSql.append("(?,?,?,?,?,?,?,?,?,?");
        			insertSql.append(")");
        			
        			for(int i=0;i<node.getSeqnumList().size();i++)
                    {
        				ArrayList list = new ArrayList();
                        String seqnum=(String)node.getSeqnumList().get(i);
                        String username="";
                        if(seqnum.indexOf(":")!=-1) //20141220 dengcan 执行退回操作，如果上一流程节点是角色，需直接退回到报批人
                        {
                        	username=seqnum.split(":")[1];
                        	seqnum=seqnum.split(":")[0];
                        }
                        list.add("1");//task_type
                        list.add(seqnum);
                        list.add(username);
                        list.add(this.ins_vo.getInt("ins_id"));
                        list.add(taskvo.getInt("task_id"));
                        list.add(this.ins_vo.getInt("tabid"));
                        list.add(node.getNode_id());
                        list.add(1);//submitflag
                        /*if(node.nodetype==NodeType.AND_SPLIT_NODE||node.nodetype==NodeType.OR_SPLIT_NODE||node.nodetype==NodeType.END_NODE||node.nodetype==NodeType.OR_JOIN_NODE||node.nodetype==NodeType.ADN_JOIN_NODE)
                        	list.add(1);*/
                        if(node.nodetype==NodeType.END_NODE&&task_state==NodeType.TASK_FINISHED) {
                            list.add(1);//state
                        } else if(node.nodetype==NodeType.AND_SPLIT_NODE||node.nodetype==NodeType.OR_SPLIT_NODE||node.nodetype==NodeType.OR_JOIN_NODE||node.nodetype==NodeType.ADN_JOIN_NODE) {
                            list.add(1);//state
                        } else {
                            list.add(0);//state
                        }
                        list.add(count);
                         
                        if(n<5)//统一显示5个 20151111
                        {
                            seqnum_str.append(" or  seqnum='"+seqnum+"' ");
                        } 
                        recordList.add(list);
                        n++;
                    }
        			if(recordList.size()>0) {
                        dao.batchInsert(insertSql.toString(),recordList);
                    }
                    if(seqnum_str.length()>0)
                    { 
                        String _tablename=this.tabname; 
                        taskvo.setString("task_topic",this.tablebo.getRecordBusiTopic(_tablename,seqnum_str.toString(),recordList.size()));
                        
                    }
                }
            }
             
            if(task_state==NodeType.TASK_WAINTING&&this.task_vo!=null) 
            {
                taskvo.setString("sp_yj","");//审批意见
                taskvo.setString("content","");//审批意见描述 
            }
            dao.addValueObject(taskvo);//向t_wf_task增加数据
            if("2".equals(this.opt)&&this.tablebo.getSp_mode()!=0){
            	RowSet rs=null;
            	try{
            	String _sql="select actorid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+taskvo.getString("task_id")+") ";
            	rs=dao.search(_sql);
            	if(rs.next()){
            		String actorid=rs.getString("actorid");
            		if(actorid.equalsIgnoreCase(taskvo.getString("actorid"))){
            			_sql="select * from templet_"+this.tablebo.getTabid()+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
         		   	    String tablename="templet_"+this.tablebo.getTabid();
         		   	    WF_Instance wf_ins=new WF_Instance(tablebo, conn);
         		   	    wf_ins.insertKqApplyTable(_sql,String.valueOf(this.tablebo.getTabid()),"","07",tablename); //往考勤申请单中写入报批记录
            		}else{
						//驳回给非发起人也调用考勤接口修改审批人信息
            			_sql="select * from templet_"+this.tablebo.getTabid()+" t where seqnum not in (select seqnum from t_wf_task_objlink where ins_id="+ins_vo.getInt("ins_id")+" and state=3  ) and ins_id="+ins_vo.getInt("ins_id");
            			KqAppInterface kqapp=new KqAppInterface(this.conn,tablebo.getUserview());
    	            	kqapp.synApproverRejectKqApp(this.tablebo.getTabid()+"", _sql);
            		}
            	}
            	}catch(Exception ex){
            		ex.printStackTrace();
            	}finally {
					PubFunc.closeDbObj(rs);
				}
            	
            }
            if(new_wf_actor!=null) {
                sendMessage(new_wf_actor,taskvo.getInt("task_id"),taskvo.getInt("node_id"));//发短信及邮件
            }
            
            
            //按设置的高级条件发送报备信息   bs_flag=4是空任务，自动流转
            if(bs_flag!=null&& "4".equals(bs_flag))
            {
                this.user_h_s="";//2015-03-04 wangrd 空节点清除手工选择的报备信息
                sendFilingTasks(taskvo.getInt("task_id"),node.getExt_param(),node);
            }
              
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return taskvo;
    }
    /**
     * 得到当前节点是不是重新分派过来的
     * @return
     */
    private boolean reAssgin() {
    	boolean reAssgin = false;
    	RowSet rowSet = null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	try {
    		ArrayList param = new ArrayList();
        	param.add(this.ins_vo.getInt("ins_id"));
        	param.add(this.node_id);
        	param.add(this.ins_vo.getInt("tabid"));
        	param.add("重新分派");
        	param.add(this.node_id);
        	param.add(this.ins_vo.getInt("ins_id"));
        	String sql="select 1 from t_wf_task where task_id = (select pri_task_id from t_wf_task where task_id = (select max(task_id) from t_wf_task_objlink where "+
        	Sql_switcher.isnull("task_type","'1'")+"='1' and  ins_id=? and node_id=? and  tab_id=?)) and "+Sql_switcher.sqlToChar("content")+"=? and node_id=? and  ins_id=?";
			rowSet = dao.search(sql,param);
			if(rowSet.next()) {
				reAssgin = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return reAssgin;
	}
    
    /**
     * 获得节点的报备对象信息
     * @param nodeid
     * @return
     */
    public LazyDynaBean getUsersByfiling(int nodeid)
    {
        LazyDynaBean abean=new LazyDynaBean();
        String user_="";
        String user_h="";
        try
        {
                ContentDAO dao=new ContentDAO(this.conn);
            
                String sql="select  node_id,ext_param  from t_wf_node where node_id="+nodeid;
                RowSet rowSet=dao.search(sql);
                if(rowSet.next())
                { 
                    Document doc=null;
                    Element element=null; 
                    String  ext_param=Sql_switcher.readMemo(rowSet,"ext_param"); 
                    if(ext_param!=null&&ext_param.trim().length()>0)
                    {
                        doc=PubFunc.generateDom(ext_param);; 
                        String xpath="/params/filing_obj";
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        element=(Element)findPath.selectSingleNode(doc);
                        if(element!=null)
                        {
                            if(element.getValue()!=null&&element.getValue().length()>0)
                            {
                                String str=element.getValue();
                                String[] temps=str.split(",");
                                for(int i=0;i<temps.length;i++)
                                {
                                    if(temps[i].length()>0)
                                    {
                                        rowSet=dao.search("select username,fullname from operuser where username='"+temps[i]+"'");
                                        if(rowSet.next())
                                        {
                                            user_h+=",4:"+rowSet.getString("username");
                                            String fullname=rowSet.getString("fullname");
                                            if(fullname==null||fullname.trim().length()==0) {
                                                fullname=rowSet.getString("username");
                                            }
                                            user_+=","+fullname;
                                            
                                        }
                                        else if(temps[i].length()>3)
                                        {
                                            String dbname=temps[i].substring(0,3);
                                         
                                            rowSet=dao.search("select a0100,a0101 from "+dbname+"A01 where a0100='"+temps[i].substring(3)+"'");
                                            if(rowSet.next())
                                            {
                                                user_h+=",1:"+dbname+rowSet.getString("a0100"); 
                                                user_+=","+rowSet.getString("a0101");
                                            } 
                                        } 
                                    }
                                }
                                
                            
                            }
                        }
                         
                    } 
                }
                if(rowSet!=null) {
                    rowSet.close();
                }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        if(user_.length()>0) {
            user_=user_.substring(1);
        }
        if(user_h.length()>0) {
            user_h=user_h.substring(1);
        }
        abean.set("user_",user_);
        abean.set("user_h",user_h);
        return abean;
    }
    
    
    
    
    
    
    private String getActoridStr(WF_Actor _wf_actor,String nodeid,int task_id)
    {
        String userStr="";
        try
        {
            
            ArrayList specialRoleUserList=_wf_actor.getSpecialRoleUserList();
            HashMap roleUserMap=new HashMap();
            for(int i=0;i<specialRoleUserList.size();i++)
            {
                String temp=(String)specialRoleUserList.get(i);
                String[] temps=temp.split(":");
                roleUserMap.put(temps[0].trim(),temps[1]);
            }
            
            ContentDAO dao=new ContentDAO(this.conn);
            if("2".equalsIgnoreCase(_wf_actor.getActortype()))
            {
                RowSet rowSet=dao.search("select * from t_sys_role where role_id='"+_wf_actor.getActorid()+"'");
                if(rowSet.next())
                {
                    int role_property=rowSet.getInt("role_property");
                    if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
                    {
                        
                        if(roleUserMap.get(String.valueOf(nodeid))==null)
                        {
                            String ext_param="";
                            rowSet=dao.search("select * from t_wf_node where node_id="+nodeid);
                            if(rowSet.next()) {
                                ext_param=Sql_switcher.readMemo(rowSet,"ext_param");
                            }
                            
                            if(this.workflowBo==null) {
                                this.workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.tablebo.getUserview());
                            }
                         
                            LazyDynaBean bean=this.workflowBo.getFromNodeid_role(ext_param,this.ins_vo.getInt("ins_id"),dao,task_id,this.tablebo!=null&&this.tablebo.isBEmploy()?"1":"0",this.cd_whl);
                            if("gwgx".equalsIgnoreCase(this.tablebo.getRelation_id())) //标准岗位关系
                            {
                                userStr=getSuperPos_userStr(bean,role_property);
                            }
                            else
                            { 
                                String sql="";
                            //  if(this.tablebo.getUserview().getStatus()==4)  //自助用户
                                {
                                    
                                    HashMap a_map=workflowBo.getSuperSql(role_property,this.tablebo.getRelation_id(),bean);
                                    if(a_map.size()==0) {
                                        throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
                                    }
                                    sql=(String)a_map.get("sql"); 
                                 
                                }
                            /*  else  //业务用户
                                {
                                    sql="select *  from t_wf_mainbody where Relation_id="+this.tablebo.getRelation_id()+"  and lower(Object_id)='"+_userViewFromNode.getUserName()+"'";
                                    if(role_property!=13)
                                        sql+=" and SP_GRADE="+role_property+"  " ;
                                    else
                                        sql+=" and SP_GRADE in (9,10,11,12) " ;
                                } */
                                rowSet=dao.search(sql);
                                if(rowSet.next()){
                                    if("1".equals(rowSet.getString("actor_type")))
                                    {
                                        userStr=rowSet.getString("mainbody_id")+"`"+rowSet.getString("a0101");
                                    }else{
                                        userStr=rowSet.getString("mainbody_id")+"`"+rowSet.getString("mainbody_id");
                                    }
                                    
                                }
                            }
                        }
                        else
                        {
                            userStr=(String)roleUserMap.get(String.valueOf(nodeid));
                        }
                         
                    }
                     
                }
                if(rowSet!=null) {
                    rowSet.close();
                }
            }else{
                if(roleUserMap.get(String.valueOf(nodeid))!=null) {
                    userStr=(String)roleUserMap.get(String.valueOf(nodeid));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return userStr;
    }
    
    
    public static void main(String[] args)
    {
            ByteArrayOutputStream b = new ByteArrayOutputStream();     
            b.write(3);          b.write(4);          b.write(5);     
            byte[] data = b.toByteArray();        int row=data.length;    
            for(int i=0;i<row;i++){          
                System.out.println(data[i]);        }   
    }
    
    private void initdata()
    {
    //    ContentDAO dao=new ContentDAO(this.conn);
   //     RecordVo vo=new RecordVo("t_wf_node");
   //     vo.setInt("node_id",this.node_id);
        try
        {
   //     	RecordVo  vo=dao.findByPrimaryKey(vo);
        	RecordVo vo=TemplateStaticDataBo.getWfNodeVo(this.node_id,this.conn);  //20171111 邓灿，采用缓存解决并发下压力过大问题
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
     * 获得当前节点的下一节点类型
     * @param nodeid
     * @return
     */
    private WF_Node getNextNode(int  nodeid,int task_id)
    {
        WF_Node wf_node=null;
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rset=null; 
        try
        {
            
            StringBuffer strsql=new StringBuffer();
            strsql.append("select  twd.node_id from t_wf_transition twt,t_wf_node twd  where  twt.next_nodeid=twd.node_id and pre_nodeid=");
            strsql.append(nodeid);
            rset=dao.search(strsql.toString());
            if(rset.next())
            {
                wf_node=new WF_Node(rset.getInt("node_id"),this.conn);
            }
            
            strsql.setLength(0);
            strsql.append("select * from templet_"+tabid+" where   exists (select null from  t_wf_task_objlink td where templet_"+tabid+".seqnum=td.seqnum ");
            strsql.append(" and td.ins_id=templet_"+tabid+".ins_id   and submitflag=1 and td.tab_id="+this.tabid+" and td.task_id="+task_id+" and td.state<>3 )");
            
            rset=dao.search(strsql.toString());
            ArrayList list=new ArrayList();
            while(rset.next()) {
                list.add(rset.getString("seqnum").trim());
            }
            wf_node.setSeqnumList(list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
             
        }
        return wf_node;
    }
    
    
    /**
     * 取得
     * @return
     * @throws GeneralException
     */
    private ArrayList getTransitionList(int flag)throws GeneralException
    {
        ArrayList translist=new ArrayList();
        
        /*
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rset=null;
        RowSet rset2=null;
        try
        {
            StringBuffer strsql=new StringBuffer();
            if(flag==1)
                strsql.append("select twt.*,twd.nodetype from t_wf_transition twt,t_wf_node twd  where twt.pre_nodeid=twd.node_id and  next_nodeid=");
            else
                strsql.append("select  twt.*,twd.nodetype from t_wf_transition twt,t_wf_node twd  where  twt.next_nodeid=twd.node_id and pre_nodeid=");
            strsql.append(this.node_id);
            if(this.tabid!=null&&this.tabid.trim().length()>0) //2013-12-18 邓灿 数据库中有脏数据时，会得到非发散节点的下级节点有多个
                strsql.append(" and twt.tabid='"+this.tabid+"'");
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
        }*/
        
        
        if(TemplateStaticDataBo.getTransitionList(this.tabid,this.node_id,flag, conn)!=null) {
            translist=(ArrayList)TemplateStaticDataBo.getTransitionList(this.tabid,this.node_id,flag, conn);
        }
        return translist;       
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
        }
        return actorlist;
    }
    
    /**
     * 获得审批节点标识
     * @param ext_param
     * @return
     */
    public String getSpFlag(String ext_param)
    {
        String sp_flag="";
        try
        {
            if(ext_param==null||ext_param.trim().length()==0) {
                return "";
            }
            Document doc=null;
            Element element=null;
            if(StringUtils.isEmpty(ext_param)) {
                return "";
            }
            doc=PubFunc.generateDom(ext_param);; 
            String xpath="/params/opt_flag";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            element=(Element)findPath.selectSingleNode(doc);
            if(element!=null&&element.getValue()!=null&&element.getValue().trim().length()>0) {
                sp_flag=element.getValue();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return sp_flag;
    }
    
    /**   
     * @Title: getNodeScopeField   
     * @Description:取得当前节点的接受范围。审批节点为角色时使用    
     * @return ArrayList  
     * @throws   
    */    
    public LazyDynaBean  getNodeScopeField(String node_id){     
    	LazyDynaBean bean= new LazyDynaBean();
        String scope_field = "";
		String containUnderOrg = "0"; // 包含下属机构
		Document doc = null;
		Element element = null;
		RowSet rowSet2 = null;
		ContentDAO dao = new ContentDAO(this.conn);
    	try {
			rowSet2 = dao.search("select * from t_wf_node where tabid=" + tabid
					+ " and node_id=" + node_id);
			String ext_param = "";
			if (rowSet2.next()) {
                ext_param = Sql_switcher.readMemo(rowSet2, "ext_param");
            }
			if (ext_param != null && ext_param.trim().length() > 0) {
				doc = PubFunc.generateDom(ext_param);;
				String xpath = "/params/scope_field";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist = findPath.selectNodes(doc);
				if (childlist.size() == 0) {
					xpath = "/param/scope_field";
					findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					childlist = findPath.selectNodes(doc);
				}
				if (childlist != null && childlist.size() > 0) {
					for (int i = 0; i < childlist.size(); i++) {
						element = (Element) childlist.get(i);
						if (element != null && element.getText() != null
								&& element.getText().trim().length() > 0) {
							scope_field = element.getText().trim();
							if (element.getAttribute("flag") != null
									&& "1"
											.equals(element.getAttributeValue("flag").trim())) {
                                containUnderOrg = "1";
                            }
						}
					}
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			bean.set("scope_field",scope_field);
			bean.set("containUnderOrg",containUnderOrg);
			
		}
		return bean;
        
        
    }
    
        /**   
     * @Title: getNodeScopeFliedValues   
     * @Description:取得当前审批节点任务列表里所有成员设置的接受范围指标值，比如设置了拟单位名称，则把所有人员的拟单位名名称。   
     * @return HashMap  key:单位或部门编号 value :1;
     * @throws   
    */    
   public HashMap  getNodeScopeFliedValues(String node_id,String task_id){   	   
	    LazyDynaBean  bean = getNodeScopeField(node_id);
        String scope_field = (String)bean.get("scope_field");
		String containUnderOrg = (String)bean.get("containUnderOrg"); // 包含下属机构
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		HashMap returnMap= new HashMap();
		String orgValues =",";//拟单位名称/部门名称的所有值
		try {	
			if (scope_field.length() > 0) {
				String stateSql = "";
				String state = "0";
				if (state != null && state.length() > 0) {// 按状态控制
					stateSql = " and " + Sql_switcher.isnull("twt.state", "0")
							+ "= " + state;
				}
				String sql0 = "";
				if (scope_field.toUpperCase().indexOf("SUBMIT") != -1) {// 报批人的单位或部门
					String value = "";
					if (scope_field.toUpperCase().indexOf("E0122") != -1) {
						value = getSubmitTaskInfo(task_id, "UM");

					} else if (scope_field.toUpperCase().indexOf("B0110") != -1) {
						value = getSubmitTaskInfo(task_id, "UN");

					}
					orgValues=orgValues+value+",";
				} else {
					String tablename = "templet_" + tabid;
					if (this.tablebo != null && "1".equals(this.fromOri)) {
						if (this.tablebo.isBEmploy()) {
                            tablename = "g_templet_" + tabid;
                        } else {
                            tablename = this.tablebo.getUserview()
                                    .getUserName()
                                    + "templet_" + tabid;
                        }
						sql0 = "select twt.*,tt."
								+ scope_field
								+ " scope_fieldvalue from  t_wf_task_objlink twt,"
								+ tablename
								+ " tt where twt.seqnum=tt.seqnum   ";
						sql0 += " and twt.task_id=" + task_id + stateSql;
					} else {
						sql0 = "select twt.*,tt."
								+ scope_field
								+ " scope_fieldvalue from  t_wf_task_objlink twt,"
								+ tablename
								+ " tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
						sql0 += " and twt.task_id=" + task_id + stateSql;
					}
				}
				if (sql0.trim().length() > 0) {
					RowSet rowSet2 = dao.search(sql0);
					while (rowSet2.next()) {
						String scope_fieldvalue=rowSet2.getString("scope_fieldvalue") == null ? ""
								: rowSet2.getString("scope_fieldvalue");
						if (orgValues.indexOf(","+scope_fieldvalue+",")<0 ){
							orgValues=orgValues+scope_fieldvalue+",";
						}						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("orgValues", orgValues);
		returnMap.put("containUnderOrg", containUnderOrg);
		returnMap.put("scope_field", scope_field);
		return returnMap;
    }


    /**   
     * @Title: filterRoleMemberList   
     * @Description:如果角色特征为部门领导、单位领导时，再按照是否在同一部门或者单位过滤一次角色成员。
     * @param roleId  角色编号
     * @param node_id 审批节点编号
     * @param task_id 任务号
     * @param roleMemberList 角色成员
     * @return ArrayList  
     * @throws   
    */    
   public ArrayList filterRoleMemberList(ContentDAO dao,String roleId,int node_id,
           int task_id,ArrayList roleMemberList){          
        ArrayList newRoleMemberList = null;
        try {   
            //获取角色特征
            String role_property="";
            StringBuffer sql = new StringBuffer("select role_property from t_sys_role where role_id= '"+roleId+"'");
            RowSet rset = dao.search(sql.toString());
            if (rset.next()){       
                role_property= rset.getString("role_property");
            }
            //如果角色特征为单位领导或部门领导，则根据角色特征过滤一下 1：部门领导 6：单位领导 
            String filterField="";
            if ("1".equals(role_property)){
                filterField="e0122";
            }
            else if ("6".equals(role_property)){
                filterField="b0110";
            }
            //考虑兼职情况 20160706 wangrd            
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
            String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
            String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept"); 
            String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
            String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
            String org_field= unit_field;
            if (setid==null) {
                setid="";
            }
            if (dept_field==null) {
                dept_field="";
            }
            if (unit_field==null) {
                unit_field="";
            }
            if (appoint_field==null) {
                appoint_field="";
            }
            
            if (filterField.length()>0){
                HashMap map =getNodeScopeFliedValues(String.valueOf(node_id),String.valueOf(task_id));
                String scope_field = (String)map.get("scope_field");//接收范围
                String containUnderOrg = (String)map.get("containUnderOrg"); // 包含下属机构
                if (scope_field!=null && scope_field.length() >0){//有接收范围才过滤
                    newRoleMemberList= new ArrayList();
                    String orgValues = (String)map.get("orgValues");//拟单位或拟部门值
                    for (int i=0;i<roleMemberList.size();i++){
                        LazyDynaBean a_bean=(LazyDynaBean)roleMemberList.get(i); 
                        String type =(String) a_bean.get("type");
                        if ("2".equals(type)){//自助用户
                            String e0122Value =(String) a_bean.get(filterField);
                            if("true".equals(flag) && setid.length()>0  &&appoint_field.length()>0){//兼职
                                String a0100 =(String) a_bean.get("a0100");
                                String nbase =a0100.substring(0,3);
                                a0100 =a0100.substring(3);
                                String curPartTab=nbase+setid;
                                String strSql="select *  from "+curPartTab+" where a0100='"+a0100+"'"
                                    +" and "+appoint_field+"='0'";
                                rset = dao.search(strSql.toString());
                                while (rset.next()){        
                                    if (unit_field.length()>0){//兼职单位
                                        String _value= rset.getString(unit_field);
                                        if (_value!=null && _value.length()>0 ) {
                                            e0122Value=e0122Value+","+_value;
                                        }
                                    }
                                    if ("e0122".equals(filterField)){//兼职部门，需取兼职单位、兼职部门的值
                                        if (dept_field.length()>0){
                                            String _value= rset.getString(dept_field);
                                            if (_value!=null && _value.length()>0 ) {
                                                e0122Value=e0122Value+","+_value;
                                            }
                                        }
                                    }
                                }
                                a_bean.set(filterField, e0122Value) ;
                            }
                            String [] arrValue = e0122Value.split(",");
                            for (int j=0;j<arrValue.length;j++){
                                String value = arrValue[j];
                                if (value!=null && value.length()>0){
                                    if ("1".equals(containUnderOrg)){//包含下级机构
                                        if (orgValues.indexOf(","+value)>-1){
                                            newRoleMemberList.add(a_bean); 
                                            break;
                                        }                                   
                                    }
                                    else {
                                        if (orgValues.indexOf(","+value+",")>-1){
                                            newRoleMemberList.add(a_bean);                              
                                            break;
                                        }                                   
                                    }
                                } 
                            }
                        }
                        else {
                            newRoleMemberList.add(a_bean);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newRoleMemberList!=null){
            return newRoleMemberList;
        }
        else {
            return roleMemberList;
        }
    }
    

    public ArrayList  getUserflag(String node_id,String task_id,HashMap map){
        
        return getUserflag(node_id,task_id,map,"0");
    }
    
    /**   
     * @Title: getUserflag   
     * @Description:    
     * @param @param node_id
     * @param @param task_id
     * @param @param map
     * @param @param state //按状态查看 0:等待处理 1：已处理  空值：不按状态查询。wangrd 2015-01-20
     * @param @return 
     * @return ArrayList  
     * @throws   
    */
    public ArrayList  getUserflag(String node_id,String task_id,HashMap map,String state){
        
        String scope_field="";
        String containUnderOrg="0"; //包含下属机构
        RowSet rowSet2=null;
        ContentDAO dao=new ContentDAO(this.conn);
        Document doc=null;
        Element element=null;
        String field_value="";
        ArrayList list = new ArrayList();
        try {
            rowSet2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
        
        String ext_param="";
        if(rowSet2.next()) {
            ext_param=Sql_switcher.readMemo(rowSet2,"ext_param");
        }
        if(ext_param!=null&&ext_param.trim().length()>0)
        {
            doc=PubFunc.generateDom(ext_param);; 
            String xpath="/params/scope_field";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            List childlist=findPath.selectNodes(doc);
            if(childlist.size()==0){
                xpath="/param/scope_field";
                 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                 childlist=findPath.selectNodes(doc);
            }
            if(childlist!=null&&childlist.size()>0)
            {
                for(int i=0;i<childlist.size();i++)
                {
                    element=(Element)childlist.get(i);
                    if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
                    {
                        scope_field=element.getText().trim();
                        if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim())) {
                            containUnderOrg="1";
                        }
                        
                    }
                }
            }
        }
        
        if(scope_field.length()>0)
        {
            String stateSql="";
            if(state!=null && state.length()>0){//按状态控制
                stateSql=" and "+Sql_switcher.isnull("twt.state","0")+"= "+state;;    
            }
                 String sql0="";
                    
            
            
                    if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
                    {
                        String value="";
                        if(scope_field.toUpperCase().indexOf("E0122")!=-1)
                        {
                            value=getSubmitTaskInfo(task_id,"UM");
                            field_value = value;
                            
                            
                        }
                        else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
                        {
                            value=getSubmitTaskInfo(task_id,"UN");
                            field_value = value;
                             
                        } 
                        if(value.length()>0)
                        {
                            String tablename="templet_"+tabid;
                            if(this.tablebo!=null&& "1".equals(this.fromOri))
                            {
                                if(this.tablebo.isBEmploy()) {
                                    tablename="g_templet_"+tabid;
                                } else {
                                    tablename=this.tablebo.getUserview().getUserName()+"templet_"+tabid;
                                }
                                sql0="select twt.*,'"+value+"' scope_fieldvalue from  t_wf_task_objlink twt,"+tablename+" tt where twt.seqnum=tt.seqnum   ";
                                sql0+=" and twt.task_id="+task_id+stateSql;
                            }
                            else
                            {
                                sql0="select twt.*,'"+value+"' scope_fieldvalue from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
                                sql0+=" and twt.task_id="+task_id+stateSql;
                            } 
                        } 
                    }
                    else
                    {
                        String tablename="templet_"+tabid;
                        if(this.tablebo!=null&& "1".equals(this.fromOri))
                        {
                            if(this.tablebo.isBEmploy()) {
                                tablename="g_templet_"+tabid;
                            } else {
                                tablename=this.tablebo.getUserview().getUserName()+"templet_"+tabid;
                            }
                            sql0="select twt.*,tt."+scope_field+" scope_fieldvalue from  t_wf_task_objlink twt,"+tablename+" tt where twt.seqnum=tt.seqnum   ";
                            sql0+=" and twt.task_id="+task_id+stateSql;
                        }
                        else
                        {
                            sql0="select twt.*,tt."+scope_field+" scope_fieldvalue from  t_wf_task_objlink twt,"+tablename+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
                            sql0+=" and twt.task_id="+task_id+stateSql;
                        }
                    }
                    if(sql0.trim().length()>0){
                        rowSet2 = dao.search(sql0);
                        while(rowSet2.next())
                        {
                            LazyDynaBean bean =  new LazyDynaBean();
                            bean.set("scope_fieldvalue", rowSet2.getString("scope_fieldvalue")==null?"":rowSet2.getString("scope_fieldvalue"));
                            bean.set("username", rowSet2.getString("username")==null?"":rowSet2.getString("username"));
                            bean.set("scope_field", scope_field);
                            bean.set("containUnderOrg",containUnderOrg);
                            bean.set("field_value", field_value);
                            list.add(bean);
                        
                            
                        }
                    }else{
                        if(map==null){//wangrd 2015-07-06由不等于改为等于 不等于对于里面代码无意义。
                            map = new HashMap();
                        }
                        else {
                        	map.put("no", "no");
                        }
                    }
            
            
        }else{
            if(map==null){//wangrd 2015-07-06由不等于改为等于 不等于对于里面代码无意义。
                map = new HashMap();
            }
            else {
            	map.put("no", "no");
            }
        }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    public boolean getUserflag2(String node_id,String task_id,LazyDynaBean a_bean,ArrayList list,String actor_id){
        boolean flag = false;
        String scope_field="";
        String containUnderOrg="0";
        RowSet rowSet2=null;
        ContentDAO dao=new ContentDAO(this.conn);
        try {
            LazyDynaBean userbean = (LazyDynaBean)list.get(0);
            scope_field =(String)userbean.get("scope_field");
            containUnderOrg=(String)userbean.get("containUnderOrg");
            if(scope_field.length()>0)
            {
                    String username =""+a_bean.get("username");
                    String password =""+a_bean.get("password");
                    
                    String operOrg =(String)a_bean.get("templateMangerPirv");
                    String userOrgId ="";//单位
                    String userDeptId ="";//部门
                    String A0100="";
                    if (operOrg==null||"".equals(operOrg)){
                        UserView userView = new UserView(username,password, this.conn);
                        userView.canLogin(false);
                        operOrg = userView.getUnitIdByBusi("8");
                        userDeptId= userView.getUserDeptId();
                        userOrgId= userView.getUserOrgId();
                        A0100= userView.getA0100();
                    }
                    else {  
                        userOrgId= (String)a_bean.get("b0110");
                        if (userOrgId==null) {
                            userOrgId="";
                        }
                        userDeptId= (String)a_bean.get("e0122");
                        if (userDeptId==null) {
                            userDeptId="";
                        }
                        A0100= (String)a_bean.get("a0100");
                        
                    }
                    
                    //UserView userView = null;

                     for(int j=0;j<list.size();j++){
                             userbean = (LazyDynaBean)list.get(j);
                             String user = ""+userbean.get("username");
                            String value=""+userbean.get("scope_fieldvalue");
                    /*      if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
                            {
                            
                                String field_value=""+userbean.get("field_value");
                                if(userView.getManagePrivCode().equalsIgnoreCase("UN")&&userView.getManagePrivCodeValue().trim().length()==0) //组织机构
                                {
                                    if(user.length()==0){
                                        return true;
                                    }else{
                                        if(user.toLowerCase().equals(userView.getUserName().toLowerCase())){
                                            return true;
                                        }
                                    }
                                }
                                else if(scope_field.toUpperCase().indexOf("E0122")!=-1)
                                {
                                    if(field_value.length()>0)
                                    {
                                        if(userView.getManagePrivCode().equalsIgnoreCase("UM"))
                                        {
                                            if(value.toLowerCase().startsWith(userView.getManagePrivCodeValue().toLowerCase())){
                                                if(user.length()==0){
                                                    return true;
                                                }else{
                                                    if(user.toLowerCase().equals(userView.getUserName().toLowerCase())){
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            return false;
                                        }
                                            
                                    }
                                    else
                                    {
                                        return false;
                                    }
                                }
                                else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
                                {
                                    if(field_value.length()>0)
                                    {
                                        if(userView.getManagePrivCode().equalsIgnoreCase("UN"))
                                        {
                                            if(value.toLowerCase().equals(userView.getManagePrivCodeValue().toLowerCase())){
                                                if(user.length()==0){
                                                    return true;
                                                }else{
                                                    if(user.toLowerCase().equals(userView.getUserName().toLowerCase())){
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            if(value.toLowerCase().equals(userView.getUserOrgId().toLowerCase())){
                                                if(user.length()==0){
                                                    return true;
                                                }else{
                                                    if(user.toLowerCase().equals(userView.getUserName().toLowerCase())){
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                            
                                    }
                                    else
                                    {
                                        return false;
                                    }
                                }
                            }
                            else */
                            { 

                                //  String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
                                    //如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
                                    if (actor_id!=null && actor_id.length()>0){
                                        String role_property="";//角色特征
                                        StringBuffer sql = new StringBuffer("select role_property from t_sys_role where role_id= '"+actor_id+"'");
                                        RowSet rset = dao.search(sql.toString());
                                        if (rset.next()){       
                                            role_property= rset.getString("role_property");
                                        }
                                        
                                        String filterField="";
                                        if ("1".equals(role_property)){//部门领导
                                            String[] arrdeptId=userDeptId.split(",");
                                            for (int k=0;k<arrdeptId.length;k++){
                                                String e0122=arrdeptId[k];
                                                if (e0122!=null &&e0122.length()>0){
                                                    if (operOrg.length()>0){
                                                    	if(operOrg.lastIndexOf("`")!=-1) {
                                                            operOrg=operOrg+"UN"+e0122;
                                                        } else {
                                                            operOrg=operOrg+"`"+"UN"+e0122;
                                                        }
                                                    }else {
                                                        operOrg="UN"+e0122;
                                                    }
                                                }
                                            }
                                        }
                                        else if ("6".equals(role_property)){//单位领导
                                            String[] arrOrgId=userOrgId.split(",");
                                            for (int k=0;k<arrOrgId.length;k++){
                                                String b0110=arrOrgId[k];
                                                if (b0110!=null &&b0110.length()>0){
                                                    if (operOrg.length()>0){
                                                    	if(operOrg.lastIndexOf("`")!=-1) {
                                                            operOrg=operOrg+"UN"+b0110;
                                                        } else {
                                                            operOrg=operOrg+"`"+"UN"+b0110;
                                                        }
                                                    }else {
                                                        operOrg="UN"+b0110;
                                                    }
                                                        
                                                }
                                            }
                                        }
                                    }                                   
                                    if("UN`".equalsIgnoreCase(operOrg)|| "UN".equalsIgnoreCase(operOrg))//兼容UN的情况 bug 57585
                                    {
                                        return true;
                                    }
                                    String codesetid="";
                                    if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
                                    {
                                        String field_value=""+userbean.get("field_value");
                                        if(field_value.length()>0)
                                        {
                                            if(scope_field.toUpperCase().indexOf("E0122")!=-1) {
                                                codesetid="UM";
                                            } else if(scope_field.toUpperCase().indexOf("B0110")!=-1) {
                                                codesetid="UN";
                                            }
                                        }
                                        else {
                                            return false;
                                        }
                                    }
                                    else
                                    {
                                        String[] temps=scope_field.split("_");
                                        String itemid=temps[0].toLowerCase();  
                                        FieldItem _item=DataDictionary.getFieldItem(itemid);
                                        if(_item!=null) {
                                            codesetid=_item.getCodesetid();
                                        }
                                    }
                                    
                                    if(operOrg!=null && operOrg.length() > 3)
                                    {
                                        boolean tempflag = false;
                                        String[] temp = operOrg.split("`");
                                        for (int i = 0; i < temp.length; i++) {
                                        	if("".equals(temp[i])) {
                                                continue;
                                            }
                                            if("1".equals(containUnderOrg)) //包含下级机构
                                            {
                                                tempflag= true;
                                                if(value.toLowerCase().startsWith(temp[i].substring(2).toLowerCase())){ 
                                                    if(user.length()==0){
                                                            return true;
                                                        }else{
                                                            if(user.toLowerCase().equals(username.toLowerCase())){
                                                                return true;
                                                            }
                                                        }
                                                }
                                                
                                            }
                                            else
                                            {
                                                if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))){
                                                    tempflag= true;
                                                    if(value.toLowerCase().equals(temp[i].substring(2).toLowerCase())){
                                                        if(user.length()==0){
                                                            return true;
                                                        }else{
                                                            if(user.toLowerCase().equals(username.toLowerCase())){
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                                    
                                                else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
                                                    tempflag= true;
                                                    if(value.toLowerCase().startsWith(temp[i].substring(2).toLowerCase())){ 
                                                    if(user.length()==0){
                                                            return true;
                                                        }else{
                                                            if(user.toLowerCase().equals(username.toLowerCase())){
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                        }
                                        
                                        if(!tempflag)
                                        {
                                            if("UN".equalsIgnoreCase(codesetid))
                                            {
                                                if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                                {
                                                    String orgid=userOrgId;
                                                    if(value.toLowerCase().equals(orgid)||("1".equals(containUnderOrg)&&value.toLowerCase().startsWith(orgid))){
                                                        if(user.length()==0){
                                                            return true;
                                                        }else{
                                                            if(user.toLowerCase().equals(username.toLowerCase())){
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else if ("UM".equalsIgnoreCase(codesetid))
                                            {
                                                if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                                {
                                                    String orgid=userDeptId.toLowerCase();
                                                    if(value.toLowerCase().startsWith(orgid)){  
                                                        if(user.length()==0){
                                                            return true;
                                                        }else{
                                                            if(user.toLowerCase().equals(username.toLowerCase())){
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                        
                                    }
                                }
                                else
                                {
                                        if("UN".equalsIgnoreCase(codesetid))
                                        {
                                            if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                            {
                                                String orgid=userOrgId.toLowerCase();
                                                if(value.toLowerCase().equals(orgid)||("1".equals(containUnderOrg)&&value.toLowerCase().startsWith(orgid))){
                                                    if(user.length()==0){
                                                        return true;
                                                    }else{
                                                        if(user.toLowerCase().equals(username.toLowerCase())){
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if ("UM".equalsIgnoreCase(codesetid))
                                        {
                                            if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                            {
                                                if(value.toLowerCase().startsWith(userDeptId.toLowerCase())){   
                                                    if(user.length()==0){
                                                        return true;
                                                    }else{
                                                        if(user.toLowerCase().equals(username.toLowerCase())){
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                
                                } 
                                
                            }   
                        }
            }else{
                return true;
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
//  public boolean getUserflag3(String node_id,String task_id,LazyDynaBean a_bean){
//      boolean flag = true;
//      String scope_field="";
//      RowSet rowSet2=null;
//      ContentDAO dao=new ContentDAO(this.conn);
//      Document doc=null;
//      Element element=null;
//      try {
//          rowSet2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
//      
//      String ext_param="";
//      if(rowSet2.next())
//          ext_param=Sql_switcher.readMemo(rowSet2,"ext_param"); 
//      if(ext_param!=null&&ext_param.trim().length()>0)
//      {
//         
//          StringReader reader=new StringReader(ext_param);
//          doc=saxbuilder.build(reader); 
//          String xpath="/params/scope_field";
//          XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
//          List childlist=findPath.selectNodes(doc);
//          if(childlist.size()==0){
//              xpath="/param/scope_field";
//               findPath = XPath.newInstance(xpath);// 取得符合条件的节点
//               childlist=findPath.selectNodes(doc);
//          }
//          if(childlist!=null&&childlist.size()>0)
//          {
//              for(int i=0;i<childlist.size();i++)
//              {
//                  element=(Element)childlist.get(i);
//                  if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
//                  {
//                      scope_field=element.getText().trim();
//                  }
//              }
//          }
//      }
//      
//      if(scope_field.length()>0)
//      {
//          String username =""+a_bean.get("username");
//          String password =""+a_bean.get("password");
//          UserView userView = null;
//          
//               userView = new UserView(username,password, this.conn);
//               userView.canLogin(false);
//               String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
//                  sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0 and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' )  ";
//                  
//          
//          
//                  if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
//                  {
//                      if(scope_field.toUpperCase().indexOf("E0122")!=-1)
//                      {
//                          String value=getSubmitTaskInfo(task_id,"UM");
//                          if(value.length()>0)
//                          {
//                              if(userView.getManagePrivCode().equalsIgnoreCase("UM"))
//                              {
//                                  sql0+=" and '"+value+"' like '"+userView.getManagePrivCodeValue()+"%'";
//                              }
//                              else
//                                  sql0+=" and 1=2 ";
//                          }
//                          else
//                              sql0+=" and 1=2 ";
//                      }
//                      else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
//                      {
//                          String value=getSubmitTaskInfo(task_id,"UN");
//                          if(value.length()>0)
//                          {
//                              if(userView.getManagePrivCode().equalsIgnoreCase("UN"))
//                              {
//                                  sql0+=" and '"+value+"'='"+userView.getManagePrivCodeValue()+"'";
//                              }
//                              else 
//                                  sql0+=" and '"+value+"'='"+userView.getUserOrgId()+"'";
//                          }
//                          else
//                              sql0+=" and 1=2 ";
//                      }
//                  }
//                  else
//                  {
//                      String[] temps=scope_field.split("_");
//                      String itemid=temps[0].toLowerCase(); 
//                      String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
//                      FieldItem _item=DataDictionary.getFieldItem(itemid);
//                      String codesetid=_item.getCodesetid();
//                      if(operOrg!=null && operOrg.length() > 3)
//                      {
//                          StringBuffer tempSql = new StringBuffer(""); 
//                          String[] temp = operOrg.split("`");
//                          for (int i = 0; i < temp.length; i++) {
//              
//                              if (codesetid.equalsIgnoreCase("UN")&&temp[i].substring(0, 2).equalsIgnoreCase("UN"))
//                                  tempSql.append(" or "+scope_field+"='" + temp[i].substring(2)+ "'");
//                              else if (codesetid.equalsIgnoreCase("UM")&&temp[i].substring(0, 2).equalsIgnoreCase("UM"))
//                                  tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");              
//                          }
//                          
//                          if(tempSql.length()==0)
//                          {
//                              if(codesetid.equalsIgnoreCase("UN"))
//                              {
//                                  tempSql.append(" or "+scope_field+"='"+userView.getUserOrgId()+"'");
//                              }
//                              else if (codesetid.equalsIgnoreCase("UM"))
//                              {
//                                  tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
//                              }
//                          }
//                          
//                          sql0+=" and ( " + tempSql.substring(3) + " ) ";
//                      }
//                      else
//                      {
//                          if(codesetid.equalsIgnoreCase("UN"))
//                          {
//                              sql0+=" and "+scope_field+"='"+userView.getUserOrgId()+"'";
//                          }
//                          else if (codesetid.equalsIgnoreCase("UM"))
//                          {
//                              sql0+=" and "+scope_field+" like '"+userView.getUserDeptId()+"%'";
//                          }
//                      }
//                  
//                  }
//                  rowSet2 = dao.search(sql0);
//                  if(rowSet2.next())
//                  {
//                      
//                      flag =true;
//                  }else{
//                      flag = false;
//                  }
//          
//          
//      }else{
//          flag =true;
//      }
//      } catch (Exception e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
//      return flag;
//  }
    /**
     * 获得报批人所在的单位  或 部门
     * @param task_id
     * @param orgFlag UN:单位  UM：部门
     * @return
     */
    private String getSubmitTaskInfo(String task_id,String orgFlag)
    {
        String info="";
        ContentDAO dao=new ContentDAO(this.conn);
        String fielditem="e0122";
        if("UN".equalsIgnoreCase(orgFlag)) {
            fielditem="b0110";
        }
        RowSet rset=null;
        try
        {
            String a0100="";
            rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
            if(rset.next()) {
                a0100=rset.getString(1);
            }
            if(a0100==null||a0100.trim().length()==0)
            {
                a0100=this.tablebo.getUserview().getDbname()+this.tablebo.getUserview().getA0100();
                if(a0100==null||a0100.length()==0) {
                    a0100=this.tablebo.getUserview().getUserName();
                }
            }
            
            if(a0100!=null&&a0100.trim().length()>0)
            {
                if(a0100.length()>3)
                {
                    String dbpre=a0100.substring(0,3);
                    boolean flag=false;
                    ArrayList dblist=DataDictionary.getDbpreList();
                    for(int i=0;i<dblist.size();i++)
                    {
                        if(((String)dblist.get(i)).equalsIgnoreCase(dbpre)) {
                            flag=true;
                        }
                    }
                    if(flag)
                    {
                        rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
                        if(rset.next())
                        {
                            info=rset.getString(1);
                        }
                    } 
                }
                
                if(info.length()==0)
                {
                    rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
                    if(rset.next())
                    {
                        String _a0100=rset.getString("a0100");
                        String _nbase=rset.getString("nbase");
                        if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
                        {
                        	a0100 = _nbase+_a0100;
                            rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
                            if(rset.next())
                            {
                                info=rset.getString(1);
                            }
                        }
                        
                    }
                    
                }
                if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
					String dbpre=a0100.substring(0,3);
					fielditem="b0110";
					rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
					if(rset.next())
					{
						info=rset.getString(1);
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
                if(rset!=null) {
                    rset.close();
                }
            }
            catch(Exception e)
            {
                
            }
        }
        return info;
    }
    
    
    
    private LazyDynaBean getBean(String itemid,String type,String decimal,String value)
    {
        LazyDynaBean abean=new LazyDynaBean();
        abean.set("itemid",itemid);
        abean.set("type",type);
        abean.set("decimal",decimal);
        abean.set("value",value);
        return abean;
    }
    
    
    
    
    
    
    /**
     * 判断任务是否是自定义流程中当前审批层级最后一个处理的节点
     * @param task_id
     * @param bo
     * @return
     */
    public boolean isEndNode_level(int task_id,TemplateTableBo bo)
    {
        boolean flag=true;
        try
        {
            String sql="";
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo _task_vo=new RecordVo("t_wf_task");
            _task_vo.setInt("task_id",task_id);
            _task_vo=dao.findByPrimaryKey(_task_vo);
            if("1".equals(_task_vo.getString("params"))) //自定义流程
            {
                int _nodeid=_task_vo.getInt("node_id");
                int _ins_id=_task_vo.getInt("ins_id");
                sql="select count(task_id) from t_wf_task where node_id in (select id from t_wf_node_manual where sp_level=(select sp_level from  t_wf_node_manual where bs_flag='1' and ins_id="+_ins_id+" and tabid="+bo.getTabid()+" and id="+_nodeid+" ) ";
                sql+=" and ins_id="+_ins_id+" ) and task_id<>"+task_id+"  and ins_id="+_ins_id+" and Task_state=3";
                RowSet rowSet=dao.search(sql);
                if(rowSet.next())
                {
                    if(rowSet.getInt(1)>0) {
                        flag=false;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return flag;
    }
    
    
    
    
    /**
     * 获得自定义流程的报备对象
     * @param task_id
     * @param tabid
     * @return
     */
    private String  getFilingObjs(int task_id,int tabid)
    {
        String objs="";
        try
        {
            String sql="select node_id,Params,ins_id from t_wf_task where   task_id="+task_id;
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(sql);
            String params="";
            int node_id=0;
            int ins_id=0;
            if(rset.next())
            {
                node_id=rset.getInt("node_id");
                params=rset.getString("params");
                ins_id=rset.getInt("ins_id");
            }
            if(params!=null&& "1".equals(params.trim())) //自定义审批流程
            {
                sql="select * from t_wf_node_manual where bs_flag='3'   and tabid="+tabid+" and ins_id="+ins_id;
                sql+="  order by seq ";
                rset=dao.search(sql);
                while(rset.next())
                {
                    objs+=","+rset.getString("actor_type")+":"+rset.getString("actorid"); 
                }
            } 
            if(rset!=null) {
                rset.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return objs;
    }
    
    
    
    /**
     * 获得下一审批层级，0为没有(自定义审批流)
     */
    public int getNextSpLevel(int task_id,TemplateTableBo bo)
    {
        int level=0;
        try
        {
            String sql="select node_id,Params,ins_id from t_wf_task where   task_id="+task_id;
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(sql);
            String params="";
            int node_id=0;
            int ins_id=0;
            if(rset.next())
            {
                node_id=rset.getInt("node_id");
                params=rset.getString("params");
                ins_id=rset.getInt("ins_id");
            }
            //查询当前结点的类型，校验是否是begin节点  start
            boolean isStart = false;
        	String node_type = "";
        	sql = "select nodetype from t_wf_node where node_id="+node_id+" and tabid="+bo.getTabid();//bug 38049 判断是否是自定义审批节点的起始节点时查t_wf_node表中node_id是根据t_wf_node_manual 中id的值，导致t_wf_node中有相同值但是表单号不同且是起始节点导致程序判断错误
        	rset=dao.search(sql);
        	if(rset.next()){
        		node_type = rset.getString("nodetype");
        	}
        	if("1".equals(node_type)) {
                isStart = true;
            }
        	//查询当前结点的类型，校验是否是begin节点  end
            if(params!=null&& "1".equals(params.trim())&&!isStart) //自定义审批流程
            {
                sql="select * from t_wf_node_manual where bs_flag='1'   and tabid="+bo.getTabid()+" and ins_id="+ins_id;
                sql+=" and sp_level>(select sp_level from t_wf_node_manual where id="+node_id+" ) and actorid is not null order by sp_level ";
                rset=dao.search(sql);
                if(rset.next()) {
                    level=rset.getInt("sp_level");
                } else
                {
                    rset=dao.search("select nodename from t_wf_node where   tabid="+bo.getTabid()+" and node_id="+node_id); 
                    if(rset.next())
                    {
                        if("begin".equalsIgnoreCase(rset.getString("nodename"))) {
                            level=1;
                        }
                    }
                }
            }
            else {
                level=1;
            }
            
            if(rset!=null) {
                rset.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return level;
    }
    
    
    /**
     * 获得自定义流程的下一节点
     * @param seqnumList
     * @param task_id
     * @return
     * @throws GeneralException
     */
    public ArrayList getNextNodeList_manual(ArrayList seqnumList,int node_id,int ins_id,int task_id,TemplateTableBo tablebo,boolean isStart)throws GeneralException
    {
        ArrayList nextnodelist=new ArrayList();
        try
        { 
            String sql="select * from t_wf_node_manual where create_user='"+tablebo.getUserview().getUserName()+"' and tabid="+tabid+" and ins_id=-1";
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=null;
            if(isStart) //自定义流程发起人
            {
                rset=dao.search(sql);   
                while(rset.next())
                {
                        ArrayList infoList=new ArrayList();
                        infoList.add(getBean("tabid","N","0",String.valueOf(tablebo.getTabid())));
                        infoList.add(getBean("node_id","N","0",String.valueOf(node_id))); 
                        infoList.add(getBean("task_id","N","0",String.valueOf(task_id)));
                        infoList.add(getBean("ins_id","N","0",String.valueOf(ins_id)));
                        infoList.add(getBean("create_user","A","0",tablebo.getUserview().getUserName()));
                        infoList.add(getBean("bs_flag","A","0",rset.getString("bs_flag")));
                        infoList.add(getBean("sp_level","N","0",rset.getString("sp_level")));
                        infoList.add(getBean("actor_type","A","0",rset.getString("actor_type")));
                        infoList.add(getBean("actorid","A","0",rset.getString("actorid")));
                        infoList.add(getBean("actorname","A","0",rset.getString("actorname")));
                        infoList.add(getBean("seq","N","0",rset.getString("seq"))); 
                        DbNameBo.insertNewRecord("t_wf_node_manual","id",this.conn,infoList);
                }
            }
            
            int level=getNextSpLevel(task_id,tablebo);
            sql="select * from t_wf_node_manual where bs_flag='1' and sp_level="+level+" and tabid="+tablebo.getTabid()+" and ins_id="+ins_id+" order by seq";
            rset=dao.search(sql);
            while(rset.next())
            {
                
                WF_Node wf_node=new WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
                wf_node.setNode_id(rset.getInt("id"));
                wf_node.setNodename(rset.getString("actorname"));
                wf_node.setActorid(rset.getString("actorid"));
                wf_node.setActorname(rset.getString("actorname"));
                wf_node.setNodetype(NodeType.HUMAN_NODE); //人工节点
                wf_node.setTabid(String.valueOf(tablebo.getTabid())); 
                if(seqnumList!=null&&seqnumList.size()>0) {
                    wf_node.setSeqnumList(seqnumList);
                }
                nextnodelist.add(wf_node);
                
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return nextnodelist;
    }
    
    
    
    /**
     * 获得自定义流程的上一节点
     * @param seqnumList
     * @param task_id
     * @return
     * @throws GeneralException
     */
    public ArrayList getPreNodeList_manual(ArrayList seqnumList,RecordVo taskVo,RecordVo instancevo)throws GeneralException
    {
        ArrayList prenodelist=new ArrayList();
        try
        { 
            int tabid=instancevo.getInt("tabid");
            int node_id=taskVo.getInt("node_id");
            int ins_id=taskVo.getInt("ins_id");
            int task_id=taskVo.getInt("task_id");
            ContentDAO dao=new ContentDAO(this.conn); 
            String sql="select * from t_wf_node_manual where  bs_flag='1' and  tabid="+tabid+" and ins_id="+ins_id+"  and sp_level< ";
            sql+="(select sp_level from t_wf_node_manual where tabid="+tabid+" and ins_id="+ins_id+"  and id="+node_id+" )  order by sp_level desc";
            RowSet rset=dao.search(sql);
            int _level=0;
            while(rset.next())
            {
                if(_level==0) {
                    _level=rset.getInt("sp_level");
                }
                if(_level!=rset.getInt("sp_level")) {
                    break;
                }
                WF_Node wf_node=new WF_Node(this.conn);     //new WF_Node(node_vo.getInt("node_id"),this.conn);
                wf_node.setNode_id(rset.getInt("id"));
                wf_node.setNodename(rset.getString("actorname"));
                wf_node.setActorid(rset.getString("actorid"));
                wf_node.setActorname(rset.getString("actorname"));
                wf_node.setActor_type("1"); 
                wf_node.setNodetype(NodeType.HUMAN_NODE); //人工节点
                wf_node.setTabid(String.valueOf(tabid)); 
                if(seqnumList!=null&&seqnumList.size()>0) {
                    wf_node.setSeqnumList(seqnumList);
                }
                prenodelist.add(wf_node);
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return prenodelist;
    }
    
    
    
    
    
    
    
    
    /**
     * 判断当前节点是否自定义了审批流程
     * @param userView
     * @param node_id
     * @param task_id
     * @param tabid
     * @param ins_id
     * @return
     */
    private boolean isDefFlowSelf(TemplateTableBo tablebo,int tabid)throws GeneralException
    {
        boolean flag=false;
        try
        {
            if(!this.tablebo.isBsp_flag()||this.tablebo.getSp_mode()==0||!this.tablebo.isAllow_defFlowSelf()) {
                return flag;
            }
            ContentDAO dao=new ContentDAO(this.conn);
            String sql="select count(id) from t_wf_node_manual where create_user='"+tablebo.getUserview().getUserName()+"' and tabid="+tabid+" and ins_id=-1";
            RowSet rset=dao.search(sql);
            if(rset.next())
            {
                if(rset.getInt(1)>0) {
                    flag=true;
                }
            }
            if(rset!=null) {
                rset.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }
    
    
    
    public String getExt_param() {
        return ext_param;
    }
    public void setExt_param(String ext_param) {
        this.ext_param = ext_param;
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

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public RecordVo getNexTTask_vo() {
        return nexTTask_vo;
    }

    public void setNexTTask_vo(RecordVo nexTTask_vo) {
        this.nexTTask_vo = nexTTask_vo;
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

    public HashMap getOtherParaMap() {
        return otherParaMap;
    }

    public void setOtherParaMap(HashMap otherParaMap) {
        this.otherParaMap = otherParaMap;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public ArrayList getSeqnumList() {
        return seqnumList;
    }

    public void setSeqnumList(ArrayList seqnumList) {
        this.seqnumList = seqnumList;
    }

    public String getTabname() {
        return tabname;
    }

    public void setTabname(String tabname) {
        this.tabname = tabname;
    }
    public void setIns_vo(RecordVo ins_vo) {
        this.ins_vo = ins_vo;
    }

    public String getSendresource() {
        return sendresource;
    }

    public void setSendresource(String sendresource) {
        this.sendresource = sendresource;
    }

    public String getFromOri() {
        return fromOri;
    }

    public void setFromOri(String fromOri) {
        this.fromOri = fromOri;
    }

    public String getBs_flag() {
        return bs_flag;
    }

    public void setBs_flag(String bs_flag) {
        this.bs_flag = bs_flag;
    }

    public WorkflowBo getWorkflowBo() {
        return workflowBo;
    }

    public void setWorkflowBo(WorkflowBo workflowBo) {
        this.workflowBo = workflowBo;
    }

    public String getCd_whl() {
        return cd_whl;
    }

    public void setCd_whl(String cd_whl) {
        this.cd_whl = cd_whl;
    }

    public String getActorid() {
        return actorid;
    }

    public void setActorid(String actorid) {
        this.actorid = actorid;
    }

    public void setTablebo(TemplateTableBo tablebo) {
        this.tablebo = tablebo;
    }

    public String getActor_type() {
        return actor_type;
    }

    public void setActor_type(String actor_type) {
        this.actor_type = actor_type;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

     
}
