/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template.workflow;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:WF_Actor</p>
 * <p>Description:流程参与者</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 20, 20063:40:21 PM
 * @author chenmengqing
 * @version 4.0
 */
public class WF_Actor {
	/**对应的节点编号*/
	private int node_id;
	/**
	 * 参与者类型
	 * =1 人员
	 * =2 角色
	 * =3 组织单元
	 * =5 本人
	 * =6 发起人
	 */
	private String actortype;
	/**活动参与者名称*/
	private String actorname;
	/**活动参与者编码*/
	private String actorid;
	/**审批意见*/
	private String sp_yj;
	/**审批描述*/
	private String content;
	/**紧急程度*/
	private String emergency;
	/**是否支持替换
	 * 也就是考虑在同一个节点中任意流转
	 * */
	private boolean bexchange=true;
	
	/** 角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，属性值各自为“9，10，11，12，13”。 */
	private boolean isKhRelation=false;
	
	/** 特殊角色对应的用户列表  */
	private ArrayList specialRoleUserList=new ArrayList();
	
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSp_yj() {
		return sp_yj;
	}
	public void setSp_yj(String sp_yj) {
		this.sp_yj = sp_yj;
	}
	public WF_Actor(String actorid,String actortype) {
		this.actorid=actorid;
		this.actortype=actortype;
	}
	
	/**
	 * 角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”、“本人”，属性值各自为“9，10，11，12，13,14”。
	 * @param a_actorid
	 * @param a_actortype
	 * @param con
	 * @return
	 */
	public int decideIsKhRelation(String a_actorid,String a_actortype,Connection con)
	{
		int flag=0;
		if(!"2".equals(a_actortype)) {
            return flag;
        }
		try
		{
			ContentDAO dao=new ContentDAO(con);
			RowSet rowSet=dao.search("select * from t_sys_role where role_id='"+a_actorid+"'");
			if(rowSet.next())
			{	
				int role_property=rowSet.getInt("role_property");
				if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13||role_property==14) {
                    flag=role_property;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	public WF_Actor() {
	}	
	public String getActorid() {
		return actorid;
	}
	public void setActorid(String actorid) {
		this.actorid = actorid;
	}
	public String getActorname() {
		return actorname;
	}
	public void setActorname(String actorname) {
		this.actorname = actorname;
	}
	public String getActortype() {
		return actortype;
	}
	public void setActortype(String actortype) {
		this.actortype = actortype;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
		 
	}
	
	
	
	
	public String getEmergency() {
		return emergency;
	}
	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}
	public boolean isBexchange() {
		return bexchange;
	}
	public void setBexchange(boolean bexchange) {
		this.bexchange = bexchange;
	}
	public boolean isKhRelation() {
		return isKhRelation;
	}
	public void setKhRelation(boolean isKhRelation) {
		this.isKhRelation = isKhRelation;
	}
	public ArrayList getSpecialRoleUserList() {
		return specialRoleUserList;
	}
	public void setSpecialRoleUserList(ArrayList specialRoleUserList) {
		this.specialRoleUserList = specialRoleUserList;
	}
	
	public void setSpecialRoleUserList(String specialRoleUserStr)
	{
		String[] temps=specialRoleUserStr.split(",");
		ArrayList list=new ArrayList();
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i]!=null&&temps[i].trim().length()>0) {
                list.add(temps[i]);
            }
		}
		this.specialRoleUserList=list;
	}

}
