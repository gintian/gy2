/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template.workflow;

/**
 * <p>Title:审批环节的节点类型</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 20064:33:04 PM
 * @author chenmengqing
 * @version 4.0
 */
public interface NodeType {
	/**节点类型 参考t_wf_node表中的nodetype字段*/
	public int START_NODE=1;//开始
	public int HUMAN_NODE=2;//人工活动
	public int TOOL_NODE=3;//自然处理活动
	public int AND_SPLIT_NODE=4;//与发散
	public int ADN_JOIN_NODE=5;//与汇聚
	public int OR_SPLIT_NODE=6;//或发散
	public int OR_JOIN_NODE=7;//或汇聚
	public int NULL_NODE=8;//空节点（不做任何处理）
	public int END_NODE=9;//结束
	/**任务状态  参考t_wf_task表中的task_state字段*/
	public int TASK_INIT=1;//初始化
	public int TASK_RUNING=2;//运行中
	public int TASK_WAINTING=3;//等待
	public int TASK_TERMINATE=4;//终止
	public int TASK_FINISHED=5;	//结束
	public int TASK_STOP=6;//暂停
	/**活动参与者类型  参考t_wf_task表中的actor_type字段*/
	public int ACTOR_SYS=0;//系统，比如开始结点，终止结点、以及自动节点
	public int ACTOR_HUMAN=1; //人工
	public int ACTOR_ROLE=2;//角色
	public int ACTOR_ORG=3;//组织
	
}
