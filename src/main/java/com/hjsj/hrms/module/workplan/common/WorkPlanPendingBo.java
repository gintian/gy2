package com.hjsj.hrms.module.workplan.common;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 工作计划代办 公共类
 * @author Administrator
 *
 */
public class WorkPlanPendingBo {
	private Connection conn;
	private UserView userView;
	
	public WorkPlanPendingBo(Connection conn) {
		super();
		this.conn = conn;
	}
	public WorkPlanPendingBo(Connection conn, UserView userView) {
		super();
		this.conn = conn;
		this.userView = userView;
	}
	/** 
    * @Title: insertPending 
    * @Description:新增一条待办信息
    * @param @param sender
    * @param @param receiver
    * @param @param ext_flag
    * @param @param bean
    * @param @return
    * @return String
    */ 
    public String insertPending(String sender,String receiver,String ext_flag,
    		LazyDynaBean bean) {
    	String pending_id="";
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		String pending_url  =(String)bean.get("pending_url");
    		String pending_title  =(String)bean.get("pending_title");
    		if (pending_url==null || pending_url.length()<1
    			|| pending_title==null || pending_title.length()<1){
    			return pending_id;
    		}
    		IDGenerator idg = new IDGenerator(2, conn);
    		pending_id = idg.getId("pengdingTask.pengding_id");
			RecordVo vo = new RecordVo("t_hr_pendingtask");
			vo.setString("pending_id", pending_id);
			vo.setDate("create_time", new java.util.Date());
			vo.setDate("lasttime", new java.util.Date());
			vo.setString("sender", sender);
			vo.setString("pending_type", "58");//OKR模块
			vo.setString("pending_title",pending_title);
			vo.setString("pending_url", pending_url);
			vo.setString("pending_status", "0");
			vo.setString("pending_level", "1");
			vo.setInt("bread", 0);
			vo.setString("receiver", receiver);
			vo.setString("ext_flag", ext_flag);
			dao.addValueObject(vo);
    	
		} catch (Exception e) {
			pending_id="";
			e.printStackTrace();
		}
    	return pending_id;
    }
    /** 
     * @Title: updatePending 
     * @Description: 更新待办信息
     * @param @param flag  1:更新待办状态为已办，已阅   2：只更新已阅   9 置为无效状态
     * @param @param pending_id
     * @param @return
     * @return String
     */ 
     public String updatePending(String flag,String pending_id) {
    	 ContentDAO dao = new ContentDAO(this.conn);
     	try {
     		StringBuffer sql= new StringBuffer();
     		if ("2".equals(flag)){
     			sql.append("update t_hr_pendingtask set bread='1',Lasttime="
     					+Sql_switcher.sqlNow()+" where ");
     			sql.append(" Pending_status='0' and pending_id="+pending_id);
     		}
     		else if ("9".equals(flag)){
     			sql.append("update t_hr_pendingtask set pending_status='9',Lasttime="
     					+Sql_switcher.sqlNow()+" where ");
     			sql.append(" Pending_status='0' and pending_id="+pending_id);
     		}
     		else{
     			sql.append("update t_hr_pendingtask set bread='1',pending_status='1',Lasttime="
     					+Sql_switcher.sqlNow()+" where ");
     			sql.append(" Pending_status='0' and pending_id="+pending_id);
     		}
     		if (sql.length()>0)
     			dao.update(sql.toString());
     	
 		} catch (Exception e) {
 			pending_id="";
 			e.printStackTrace();
 		}
     	return pending_id;
     }   
   /** 
 	* @Title: isHavePendingtask 
 	* @Description: 查看是否有待办
 	* @param @param receiver
 	* @param @param ext_flag
 	* @param @return
 	* @return String
 	*/ 
 	public  String isHavePendingtask(String receiver,String ext_flag){
 		ContentDAO dao = new ContentDAO(this.conn);
 		RowSet rs = null;
 		String pending_id="";
 		try{
 			String withNoLock="";
 			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
 				withNoLock=" WITH(NOLOCK) ";
 			String sql="select pending_id,ext_flag,pending_status,bread,receiver from t_hr_pendingtask "
 				+withNoLock+" where Pending_type='58'";
 			if (receiver!=null && receiver.length()>0){
 				sql=sql+" and receiver='"+receiver+"'";
 			}	
 			sql=sql+" and pending_status='0' and ext_flag='"+ext_flag+"'";
 			rs=dao.search(sql);
 			if (rs.next()){
 				pending_id= rs.getString("pending_id");		
 			}
 		}catch(Exception e){
 			e.printStackTrace();
 		}finally{
 			PubFunc.closeDbObj(rs);
 		}
 		return pending_id;
 	}
 	 /**
     * 删除协办任务待办
     * @param planId
     * chent
     */
    public void deletePending(String ext_flag) {
    	ContentDAO dao = new ContentDAO(this.conn);
     	try {
     		String pending_id= isHavePendingtask("", ext_flag);
     		if(pending_id.length()>0){
    			String sql = "delete t_hr_pendingtask where pending_id=?";
    			ArrayList<String> list = new ArrayList<String>();
    			list.add(pending_id);
    			dao.delete(sql, list);
     		}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
     }
}
