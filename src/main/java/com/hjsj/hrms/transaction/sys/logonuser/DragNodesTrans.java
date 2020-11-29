package com.hjsj.hrms.transaction.sys.logonuser;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 用户管理用户组调整
 * @author xujian
 *Apr 7, 2010
 */
public class DragNodesTrans extends IBusiness {
	public void execute() throws GeneralException {
		String fromid = (String)this.getFormHM().get("fromid");
		String toid = (String)this.getFormHM().get("toid");
		String table =(String)this.getFormHM().get("table");
		//是否为同组间移动 1 是 0 不是
		String isSameGroup =this.getFormHM().containsKey("isSameGroup")?(String) this.getFormHM().get("isSameGroup"):"0";
		String primarykey_column_name =(String)this.getFormHM().get("primarykey_column_name");
		String father_column_name =(String)this.getFormHM().get("father_column_name");
		try {
			if("1".equals(isSameGroup)){
				this.removeUserOrder(fromid,toid);
			}else{

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="";

			if("root".equalsIgnoreCase(toid)){
				sql = "update "+table+" set "+father_column_name +" =1  where "+primarykey_column_name+" = '"+fromid+"'";
				dao.update(sql);
			}else {
				sql = "select groupid from usergroup where groupname='" + toid + "'";
				this.frecset = dao.search(sql);
				int toGroupid = 0;
				if (this.frecset.next()) {
					toGroupid = this.frecset.getInt("groupid");
				}


				/**
				 * 不用下面recordVo保存，因为groupname 有空格时会过滤掉，导致找不到。guodd 17-03-09
				 */
				sql = "update " + table.toLowerCase() + " set groupid=? where username=?";
				ArrayList values = new ArrayList();
				values.add(toGroupid);
				values.add(fromid);
				dao.update(sql, values);
			}

				
				/*
				
				RecordVo vo = new RecordVo(table.toLowerCase());
				/*vo.setString("username", toid);//xuj 改变被拖动用户权限
				vo = dao.findByPrimaryKey(vo);*/
				/*vo.setString("username", fromid);
				vo = dao.findByPrimaryKey(vo);//xuj 不改变被拖动用户权限
				vo.setInt("groupid", toGroupid);
				sql = "select groupid from usergroup where groupname='"+fromid+"'";
				this.frecset = dao.search(sql);
				if(!this.frecset.next()){
					vo.setInt("roleid", 0);
				}else{
					vo.setInt("roleid", 1);
					updateChild(this.frecset.getInt("groupid"),toid,dao);
				}
			/*	sql="select password,fullname,photoid,ingrporder,a0100,nbase,unitcode,email,phone,state,userflag from operuser where username='"+fromid+"'";
				this.frecset = dao.search(sql);
				if(this.frecset.next()){
					vo.setString("password", this.frecset.getString("password"));
					vo.setString("fullname", this.frecset.getString("fullname"));
					vo.setInt("photoid", this.frecset.getInt("photoid"));
					vo.setInt("ingrporder", this.frecset.getInt("ingrporder"));
					vo.setInt("userflag", this.frecset.getInt("userflag"));
					vo.setString("a0100", this.frecset.getString("a0100"));
					vo.setString("nbase", this.frecset.getString("nbase"));
					vo.setString("unitcode", this.frecset.getString("unitcode"));
					vo.setString("email", this.frecset.getString("email"));
					vo.setString("phone", this.frecset.getString("phone"));
					vo.setInt("state", this.frecset.getInt("state"));
					vo.setDate("modtime", PubFunc.getStringDate("yyyy-MM-dd"));
				}*///xuj 改变被拖动用户权限
				/*if(groupid==1){//超级用户组
					//vo.setInt("userflag", 10);
				}else{
					//vo.setInt("userflag", 12);
				}
				dao.updateValueObject(vo);*/
			}
			
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 递归修改子节点对应的用户或用户组权限
	 * @param groupid
	 * @param toid
	 * @param dao
	 * @throws Exception
	 */
	private void updateChild(int groupid,String toid,ContentDAO dao) throws Exception{
		String sql = "select username,roleid from operuser where groupid="+groupid;
		RecordVo vo = new RecordVo("operuser");
		/*vo.setString("username", toid);//xuj 改变被拖动用户权限
		vo = dao.findByPrimaryKey(vo);*/
		ResultSet rs=null;
		ResultSet rstemp=null;
		try{
			rs = dao.search(sql);
			while(rs!=null&&rs.next()){
				vo.setString("username", rs.getString("username"));
				vo = dao.findByPrimaryKey(vo);//xuj 不改变被拖动用户权限
				vo.setInt("groupid", groupid);
				int roleid=rs.getInt("roleid");
				if(roleid==0){
					vo.setInt("roleid",0);
				}else{
					vo.setInt("roleid", 1);
					sql = "select groupid from usergroup where groupname='"+rs.getString("username")+"'";
					rstemp = dao.search(sql);
					if(rstemp.next()){
						updateChild(rstemp.getInt("groupid"),toid,dao);
					}
				}
				/*sql = "select password,fullname,photoid,ingrporder,a0100,nbase,unitcode,email,phone,state,userflag from operuser where username='"+rs.getString("username")+"'";
				rstemp = dao.search(sql);
				if(rstemp.next()){
					vo.setString("password", rstemp.getString("password"));
					vo.setString("fullname", rstemp.getString("fullname"));
					vo.setInt("photoid", rstemp.getInt("photoid"));
					vo.setInt("ingrporder", rstemp.getInt("ingrporder"));
					vo.setInt("userflag", rstemp.getInt("userflag"));
					vo.setString("a0100", rstemp.getString("a0100"));
					vo.setString("nbase", rstemp.getString("nbase"));
					vo.setString("unitcode", rstemp.getString("unitcode"));
					vo.setString("email", rstemp.getString("email"));
					vo.setString("phone", rstemp.getString("phone"));
					vo.setInt("state", rstemp.getInt("state"));
					vo.setDate("modtime", PubFunc.getStringDate("yyyy-MM-dd"));
				}*///xuj 改变被拖动用户权限
				dao.updateValueObject(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null)
				rs.close();
			if(rstemp!=null)
				rstemp.close();
		}
	}


	/**
	 * 同组移动人员
	 * @param fromid
	 * @param toId
	 * @return
	 * @throws SQLException
	 * @author ZhangHua
	 * @date 18:28 2019/12/19
	 */
	private boolean removeUserOrder(String fromid,String toId) throws SQLException {

		String sql="select groupid ,InGrpOrder,username from operuser where username =? or username =? ";
		ArrayList<String > list=new ArrayList<>(2);
		list.add(fromid);
		list.add(toId);
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs=null;
		try{
			rs=dao.search(sql,list);
			int fromGroupId=-1,fromOrder=-1,toGroupId=-1,toOrder=-1;
			while (rs.next()){
				if(fromid.equalsIgnoreCase(rs.getString("username"))){
					fromGroupId=rs.getInt("groupid");
					fromOrder=rs.getInt("InGrpOrder");
				}else{
					toGroupId=rs.getInt("groupid");
					toOrder=rs.getInt("InGrpOrder");
				}
			}
			if(fromGroupId==-1||toGroupId==-1){
				return false;
			}
			if(fromGroupId==toGroupId){
				return this.inGroupDoMove(fromOrder,toOrder,fromGroupId,fromid);
			}

		}catch (SQLException e){
			throw e;
		}

		return false;
	}

	/**
	 * 实际移动方法
	 * @param fromId
	 * @param toId
	 * @param groupId
	 * @param fromUsername
	 * @return
	 * @author ZhangHua
	 * @date 18:28 2019/12/19
	 */
	private boolean inGroupDoMove(int fromId,int toId,int groupId,String fromUsername){
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			ArrayList list=new ArrayList();
			list.add(toId);
			list.add(fromId);
			list.add(groupId);
			if(fromId>toId){
				dao.update("update operuser set InGrpOrder=InGrpOrder+1 where InGrpOrder >= ? and InGrpOrder <= ? and groupId=?",list);
			}else{
				dao.update("update operuser set InGrpOrder=InGrpOrder-1 where InGrpOrder <= ? and InGrpOrder >= ? and groupId=?",list);
			}
			list.clear();
			list.add(toId);
			list.add(fromUsername);
			dao.update("update operuser set InGrpOrder=? where username=?",list);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return true;
	}
}
