package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hjsj.hrms.businessobject.kq.Define_WF_Node;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;

public class WF_Define_Instance {
	private Connection conn;
    private UserView userview;
    private ContentDAO dao;    
    private String instance_Name;   
    private String tabid;
    private String url_addr;//业务url地址
	private String params;//业务参数地址
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
	public WF_Define_Instance(){}
	/**
	 * 
	 * @param conn
	 * @param userview
	 * @param app_table
	 * @param vo
	 * @param tabid 固定表单id
	 */
	public WF_Define_Instance(Connection conn,UserView userview,String a0101,String tabid)
	{
		this.dao=new ContentDAO(conn);
		this.conn=conn;
		this.userview=userview;	
		this.tabid=tabid;
		getInstanceName();
	}
	
	
	/**
	 * 建立任务实例
	 * @param ins_vo
	 */
	public boolean createTaskInstance(RecordVo ins_vo,WF_Actor actor,String url_addr,String param)
	{
		boolean isCorrect=false;
		this.url_addr=url_addr;
		this.params=param;
		try
		{
			IDGenerator idg=new IDGenerator(2,this.conn);		
			ins_vo.setInt("ins_id",Integer.parseInt(idg.getId("wf_instance.ins_id")));
			ins_vo.setString("name", this.instance_Name);
			ins_vo.setInt("tabid",Integer.parseInt(this.tabid));
			ins_vo.setDate("start_date",DateStyle.getSystemTime());
			/**过程状态
             * 1 初始化
             * 2 运行中
             * 3 暂停
             * 4 终止
             * 5 结束
             * */
			ins_vo.setString("finished","2"); //运行中...
			/**模板类型
             * =1业务模板
             * =2固定网页
             * */
			ins_vo.setInt("template_type",2);
			/**平台用户 =0
             * 还是自助用户=4
             * */
            if(this.userview.getStatus()==0)
            {
            	ins_vo.setString("actorid",this.userview.getUserName());
            	ins_vo.setInt("actor_type",4);
            }
            else
            {
            	ins_vo.setString("actorid",this.userview.getDbname()+this.userview.getUserId());
            	ins_vo.setInt("actor_type",1);
            } 
            ins_vo.setString("actorname",this.userview.getUserFullName());
            /**是否有附件
             * =0 无附件
             * =1 有附件
             * */
            ins_vo.setInt("bfile",0);
            dao.addValueObject(ins_vo);
            /**初始化任务*/
            InitStartTask(ins_vo,actor);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return isCorrect;
	}
	/**
	 * 任务名称
	 *
	 */
	private void getInstanceName()
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select  name from t_wf_define where tabid='"+this.tabid+"'");
		try
		{
			RowSet rs=this.dao.search(sql.toString());
			String name="申请";
			if(rs.next())
			{
				name=rs.getString("name");
			}
			this.instance_Name=name;
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	/**
	 * 初始化任务
	 * @throws GeneralException
	 */
	private boolean InitStartTask(RecordVo instancevo,WF_Actor actor)throws GeneralException
	{
		boolean bflag=true;
		Define_WF_Node start_node=getStartNode();
		if(start_node==null||start_node.getNodetype()==0) {
            throw new GeneralException(ResourceFactory.getProperty("error.start.notdefine"));
        }
		/**创建任务*/
		start_node.createTask(instancevo, actor);
		return bflag;
	}
	/**
	 * 取得流程开始结点
	 * @return
	 * @throws GeneralException
	 */
	private Define_WF_Node getStartNode()throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		Define_WF_Node start_node=null;
		try
		{
			start_node=new Define_WF_Node(this.conn,this.tabid);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_node where nodetype='");
			strsql.append(NodeType.START_NODE);
			strsql.append("' and tabid=");
			strsql.append(this.tabid);
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
			{
				start_node.setNode_id(rset.getInt("node_id"));
				start_node.setNodename(rset.getString("nodename"));
				start_node.setNodetype(rset.getInt("nodetype"));
				start_node.setExt_param(Sql_switcher.readMemo(rset,"ext_param"));	
				start_node.setUrl_addr(this.url_addr);
				start_node.setParams(this.params);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return start_node;
	}
}
