package com.hjsj.hrms.businessobject.report.report_isApprove;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class Report_isApproveBo {

	private Connection conn=null;
	/** 登录用户 */
	private UserView userview=null;
	public Report_isApproveBo(Connection conn,UserView userview) {
		this.conn=conn;
		this.userview=userview;
	}
	
	public ArrayList getApprovelist(ArrayList list){
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from treport_ctrl where currappuser = '"+this.userview.getUserName()+"' and status = '4' and unitcode in (select unitcode from operuser where UserName in (select username from treport_ctrl where currappuser = '"+this.userview.getUserName()+"'))";
			RowSet rs = dao.search(sql);
			LazyDynaBean abean = null;
			String str = "审核";
			while(rs.next()){
				abean=new LazyDynaBean();
					String tabid = rs.getString("tabid");
					String name = getTname(tabid);
					String username = rs.getString("username");
					boolean isUpapprove = isUpapprove(username);
					abean.set("name",name+"("+str+")");
					if(isCollectUnit(username)){
						abean.set("url","/report/report_collect/reportOrgCollecttree.do?b_query=link&operateObject=2&status=4&isUpapprove="+isUpapprove+"&username="+SafeCode.encode(username)+"&tabid="+tabid+"&obj1=1");
					}else{
						abean.set("url","/report/edit_report/reportSettree.do?b_query=link&operateObject=1&status=4&isUpapprove="+isUpapprove+"&username="+SafeCode.encode(username)+"&code="+tabid+"&obj1=1");
					}
					//abean.set("url","/report/edit_report/reportSettree.do?b_query3=link&operateObject=1&status=4&isUpapprove="+isUpapprove+"&username="+username+"&tabid="+tabid+"&obj1=1");
					abean.set("target","i_body");
					list.add(abean);
			}
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return list;
	}
	/**
	 * 判断当前用户是基层单位还是汇总单位  zhaoxg 2014-2-13
	 * @param username
	 * @return
	 */
	public boolean isCollectUnit(String username){
		boolean flag=false;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from tt_organization where parentid =(select unitcode from tt_organization where unitcode=(select unitcode from operuser where username ='"+username+"'))";
			RowSet rs=dao.search(sql);
			if(rs.next()){
				flag=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	public ArrayList getReturnList(ArrayList list){
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select * from treport_ctrl where currappuser = '"+this.userview.getUserName()+"' and status = '2' and unitcode=(select unitcode from operuser where UserName = '"+this.userview.getUserName()+"')";
			RowSet rs = dao.search(sql);
			LazyDynaBean abean = null;
			String str = "驳回";
			while(rs.next()){
				abean=new LazyDynaBean();
					String tabid = rs.getString("tabid");
					String name = getTname(tabid);
					String username = rs.getString("username");
					boolean isUpapprove = isUpapprove(username);
					abean.set("name",name+"("+str+")");
					if(isCollectUnit(username)){
						abean.set("url","/report/report_collect/reportOrgCollecttree.do?b_query=link&operateObject=2&status=2&isUpapprove="+isUpapprove+"&username="+SafeCode.encode(username)+"&tabid="+tabid+"&obj1=1");
					}else{
						abean.set("url","/report/edit_report/reportSettree.do?b_query=link&operateObject=1&status=2&isUpapprove="+isUpapprove+"&username="+SafeCode.encode(username)+"&code="+tabid+"&obj1=1");
					}
//					abean.set("url","/report/edit_report/reportSettree.do?b_query=link&operateObject=1&status=2&isUpapprove="+isUpapprove+"&username="+username+"&code="+tabid+"&obj1=1");
					//abean.set("url","/report/edit_report/reportSettree.do?b_query3=link&operateObject=1&status=4&isUpapprove="+isUpapprove+"&username="+username+"&tabid="+tabid+"&obj1=1");
					abean.set("target","i_body");
					list.add(abean);
			}
//			//---------------------过滤重复数据------------------
//			int len = list.size();
//	        for(int i=0; i<len-1; i++){
//	        	LazyDynaBean temp = (LazyDynaBean) list.get(i);
//	            for(int j=i+1; j<len; j++){
//	            	LazyDynaBean temp1 = (LazyDynaBean) list.get(j);
//	                if(temp.get("name").equals(temp1.get("name"))){
//	                    list.remove(j);
//	                    j-- ;
//	                    len-- ;
//	                }
//	            }
//	        }
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return list;
	}
	/**
	 * 获得报表名称
	 * @param tabid
	 * @return
	 */
	public String getTname(String tabid){
		String tname="";
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			String sql = "select name from tname where tabid = "+tabid+"";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				tname = rs.getString("name");
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }		
		return tname;
	}
	/**
	 * 判断当前用户是不是顶级审批人
	 */
	public boolean isUpapprove(String username){
		boolean isUpapprove = false;
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			String sp_grade = "";
			String max = "";
			String sql = "select sp_grade from t_wf_mainbody where relation_id = '1' and object_id = '"+username+"' and mainbody_id = '"+this.userview.getUserName()+"'";
			String sqll = "select max(sp_grade) as max from t_wf_mainbody where relation_id = '1' and object_id = '"+username+"'";
			RowSet rs = dao.search(sql);
			RowSet rowset = dao.search(sqll);
			if(rs.next()){
				sp_grade = rs.getString("sp_grade");
			}
			if(rowset.next()){
				max = rowset.getString("max");
			}
			if(max!=null&&max.equals(sp_grade)){
				isUpapprove = true;
			}			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return isUpapprove;
	}
}
