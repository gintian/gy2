package com.hjsj.hrms.businessobject.workplan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("all")
public class WorkPlanOperationLogBo {
	private Connection conn;
	private UserView userView;
	
	public WorkPlanOperationLogBo(Connection conn, UserView userView){
		this.conn = conn;
		this.userView = userView;
	}
	
	
	public ArrayList queryAllLog(Integer type,Integer objectId){
//		if (true)
//			return null;
		ArrayList logList = new ArrayList();
		StringBuffer sqlsbf = new StringBuffer();
		sqlsbf.append("select id, type, object_id, content,  nbase, a0100,  create_time, create_user, create_fullname ");
		sqlsbf.append("from per_opt_history ");
		sqlsbf.append("where type = " + type);
		sqlsbf.append(" and object_id = " + objectId);
		sqlsbf.append(" order by create_time desc");//修改为倒叙排序  haosl 20160907
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			WorkPlanBo wpBo = new WorkPlanBo(conn, userView);
			rs = dao.search(sqlsbf.toString());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			while(rs.next()){
				ArrayList list = new ArrayList();
				list.add(rs.getString("create_fullname"));
				list.add(rs.getString("content"));
				Date createTime = rs.getDate("create_time");
				//解决oracle库不能去到时分的问题
				if(Sql_switcher.searchDbServer() == 2){
					Timestamp ta = rs.getTimestamp("create_time");
					if(ta != null){
						createTime = new Date(ta.getTime());
					}
				}
				
				list.add(df.format(createTime));
				list.add(WorkPlanUtil.encryption(rs.getInt("id") + ""));
				String photoUrl = wpBo.getPhotoPath(rs.getString("nbase"), rs.getString("a0100"));
				list.add(photoUrl);
				list.add(rs.getInt("type") + "");
				logList.add(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			WorkPlanUtil.closeDBResource(rs);
		}
		
		
		return logList;
		
	}
	
	
	
	/**
	 * 日志操作
	 * @param type
	 * @param object_id		计划/任务id
	 * @param content		操作内容
	 * @param nbase			人员库
	 * @param a0100			人员编号
	 */
	public void addLog(Integer object_id, String content){
		if (content == null || content.trim().length()==0) {
            return;
        }
		RecordVo logVo = new RecordVo("per_opt_history");
		ContentDAO dao = new ContentDAO(this.conn);
		DateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			IDGenerator idg = new IDGenerator(2, this.conn);
			String id = idg.getId("per_opt_history.id");
			
			Date newTime = time.parse(time.format(new Date()));
			logVo.setInt("id", Integer.parseInt(id));
			logVo.setInt("type", 2);
			logVo.setInt("object_id", object_id);
			logVo.setString("content", content);
			logVo.setString("nbase", this.userView.getDbname());
			logVo.setString("a0100", this.userView.getA0100());
			logVo.setDate("create_time", newTime);
			logVo.setString("create_user", this.userView.getUserName());
			logVo.setString("create_fullname", this.userView.getUserFullName());
			dao.addValueObject(logVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
