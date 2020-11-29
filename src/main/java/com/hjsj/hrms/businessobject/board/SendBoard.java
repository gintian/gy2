package com.hjsj.hrms.businessobject.board;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SendBoard {

	private Connection conn = null;
	private UserView userView = null;

	private BoardBo boardBo = null;
	
	private PendingTask pt = null;

	public SendBoard(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
		this.boardBo = new BoardBo(conn, userView);
		this.pt = new PendingTask();
	}
	public boolean addBoard(String id) {
		//System.out.println("审批调用接口进入1");
		String pArr[] = boardBo.getPriUser(id);
		//System.out.println("审批调用接口进入2");
		ArrayList list = getUserList(pArr[0]);
		//System.out.println("审批调用接口进入3");
		try {
			for (Iterator it = list.iterator(); it.hasNext();) {
				//System.out.println("审批调用接口进入4");
				String uid[] = ((String) it.next()).split("'");
				String nbase = uid[0];
				String a0100 = uid[1];
				ContentDAO dao = new ContentDAO(this.conn);
				if (!isExists(id, nbase, a0100)) {
					//System.out.println("审批调用接口进入5");
					String task_id = this.getPendingCode(a0100);
					RecordVo vo = new RecordVo("per_task_pt");
					vo.setInt("id", this.getTaskID());
					vo.setString("plan_id", id);
					vo.setString("nbase", nbase);
					vo.setString("task_id", task_id);
					vo.setString("object_id", "");
					vo.setString("mainbody_id", a0100);
					vo.setString("flag", "100");
					if(sendPending(vo,1)){
						dao.addValueObject(vo);
					}
				}
			}
			//System.out.println("审批调用接口进入6");
		} catch (GeneralException e) {
			e.printStackTrace();
			
		}
		//System.out.println("审批调用接口进入7");
		return false;
	}

	public boolean delBoard(String id) {
		
		RowSet rs=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search("select * from per_task_pt WHERE plan_id='" + id + "' AND flag=100");
			while (rs.next()) {
				RecordVo vo = new RecordVo("per_task_pt");
				vo.setString("plan_id", id);
				vo.setString("nbase", rs.getString("nbase"));
				vo.setString("mainbody_id", rs.getString("mainbody_id"));
				vo.setString("flag", "100");
				vo.setString("task_id", rs.getString("task_id"));
				String sql = "DELETE FROM per_task_pt WHERE plan_id='" + id + "' AND nbase='" + rs.getString("nbase") + "' AND mainbody_id='" + rs.getString("mainbody_id") + "' AND flag=100";
				if(sendPending(vo,4)){
					dao.update(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return false;
	}

	public boolean delBoard(String id, String nbase, String a0100 ,String task_id) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			if (this.isExists(id, nbase, a0100)) {
				RecordVo vo = new RecordVo("per_task_pt");
				vo.setString("plan_id", id);
				vo.setString("nbase", nbase);
				vo.setString("mainbody_id", a0100);
				vo.setString("flag", "100");
				vo.setString("task_id", task_id);
				String sql = "DELETE FROM per_task_pt WHERE plan_id='" + id + "' AND nbase='" + nbase + "' AND mainbody_id='" + a0100 + "' AND flag=100";
				if(sendPending(vo,3)){
					dao.update(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getPendingCode(String a0100) {
		Date d = new Date();
		return "IMIS-003-" + d.getTime() + a0100
				+ Math.round(Math.ceil(Math.random() * 10));
	}

	private ArrayList getUserList(String UserStr) {
		ArrayList list = new ArrayList();
		if(UserStr==null||UserStr.length()<=0) {
            return list;
        }
		String UserArray[] = UserStr.split(",");
		for (int i = 0; i < UserArray.length; i++) {
			if(UserArray[i].split(":").length==2){
				String uid = UserArray[i].split(":")[1];
				String a0100 = uid.substring(uid.length() - 8, uid.length());
				String nbase = uid.substring(0, uid.length() - 8);
				if(nbase.length() < 1){
					String sql = "select staff_id,role_id from t_sys_staff_in_role where role_id = '" + a0100 + "' AND status='1'";
					ContentDAO dao = new ContentDAO(this.conn);
					try {
						RowSet rs = dao.search(sql);
						while(rs.next()){
							String staff_id = rs.getString("staff_id");
							if(staff_id.length() > 8){
								a0100 = staff_id.substring(staff_id.length() - 8, staff_id.length());
								nbase = staff_id.substring(0, staff_id.length() - 8);
								list.add(nbase + "'" + a0100);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else{
					list.add(nbase + "'" + a0100);
				}
			}
		}
		return list;
	}

	private boolean isExists(String id, String nbase, String a0100) {
		String sql = "SELECT 1 FROM per_task_pt WHERE FLAG=100 AND plan_id='" + id
				+ "' AND UPPER(nbase) = '" + nbase.toUpperCase() + "' AND mainbody_id='" + a0100
				+ "'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param vo
	 * @param flag 1:新增，2：修改，3：删除
	 * @return
	 */
	private boolean sendPending(RecordVo vo,int flag){
		String id = vo.getString("plan_id");
		ContentDAO dao = new ContentDAO(this.conn);		
		RowSet rs = null;
		try {
			
			if(flag == 1){
				String sql = "SELECT topic,content FROM announce WHERE id='" + id + "'";
				rs = dao.search(sql);
				if(rs.next()){
					String mainbody_id = vo.getString("mainbody_id");
					String task_id = vo.getString("task_id");
					String receiverNbase = vo.getString("nbase");
					String topic =  rs.getString("topic");
					String sendA0100 = this.userView.getA0100();
					String sendNbase = this.userView.getDbname();
					String senderMessage = "";
					if(sendA0100 == null || sendA0100.length() < 1){
						senderMessage = this.userView.getUserFullName();
					}else{
						senderMessage =  sendNbase + sendA0100;
					}
					String receiverMessage = receiverNbase + mainbody_id;
					return pt.insertPending(task_id, "B", topic, senderMessage, receiverMessage, "/selfservice/welcome/welcome.do?b_view=link&a_id=" + id, 2, 1, "公告", this.userView);
				}
				
			
			}else if(flag == 4){
				String mainbody_id = vo.getString("mainbody_id");
				String receiverNbase = vo.getString("nbase");
				String task_id = vo.getString("task_id");
				String receiverMessage=receiverNbase+mainbody_id;
				LazyDynaBean receiverbean=getUserNamePassword(receiverMessage);
				if(receiverbean==null){
					return false;
				}

				String receiveruser="";//待办系统接收人
				String receiveruser_field=SystemConfig.getPropertyValue("receiveruser_field");
				if(receiveruser_field!=null&&receiveruser_field.length()>0)
				{
					receiveruser=(String)receiverbean.get("receiveruser");
				}else {
                    receiveruser=(String)receiverbean.get("username");
                }
				UserView userViewSelf=new UserView(receiveruser,this.conn);
				return pt.updatePending("B", task_id, 100, "公告", userViewSelf);
			}else{
				String task_id = vo.getString("task_id");
				return pt.updatePending("B", task_id, 100, "公告", this.userView);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	private int getTaskID(){
		String sql = "SELECT MAX(id) id FROM per_task_pt";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int maxid = 1;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				maxid = rs.getInt("id") + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return maxid;
	}
	private LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String receiveruser_field=SystemConfig.getPropertyValue("receiveruser_field");
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    StringBuffer sql=new StringBuffer();
	    
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password ");
	    if(receiveruser_field!=null&&receiveruser_field.length()>0)
	    {
	       sql.append(","+receiveruser_field+" receiveruser");	
	    }
	    sql.append(" from "+nbase+"A01 where a0100='"+a0100+"'");
	    //Category.getInstance("com.hrms.frame.dao.ContentDAO").error("代办调用新增SQL=="+sql);
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}
}
