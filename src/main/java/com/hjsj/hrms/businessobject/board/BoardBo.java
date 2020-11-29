package com.hjsj.hrms.businessobject.board;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class BoardBo {
	private Connection conn = null;
	private UserView userView = null;
	public BoardBo(Connection conn,UserView userView){
		this.conn = conn;
		this.userView = userView;
	}
	public boolean savePriv(String priv,String tabid){
		boolean flag = true;
		String privarr[] = priv.split(",");
		StringBuffer str_value = new StringBuffer();
		StringBuffer tmpstr = new StringBuffer();
		for(int i=0;i<privarr.length;i++){
			if(privarr[i]==null||privarr[i].trim().length()<1) {
                continue;
            }
			String[] prarr = privarr[i].split(":");
			if(prarr.length!=2){//机构通知对象类型
				String noticeunit = privarr[i];
				noticeunit = noticeunit.replaceAll("`", ",");
				tmpstr.append(noticeunit);
			}else{
				SysPrivBo privbo=new SysPrivBo(prarr[1],prarr[0],this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.ANNOUNCE);
				String content=parser.getContent();
				String ct="";
				if(content==null||content.length()<=0){
					content="";
				}else{
					str_value.append(content+",");
				}
				ct=","+content+",";
				if(ct.toLowerCase().indexOf(","+tabid.toLowerCase()+",")==-1) {
                    str_value.append(tabid+",");
                } else {
					str_value.delete(0,str_value.length());
					continue;
				}
				if(str_value.length()!=0) {
                    str_value.setLength(str_value.length()-1);
                }
				parser.reSetContent(str_value.toString());
				res_str=parser.outResourceContent();
				privbo.saveResourceString(prarr[1],prarr[0],res_str);
				str_value.delete(0,str_value.length());
			}
		}
		if(tmpstr.length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			try{
				dao.update("update announce set noticeunit='"+tmpstr.toString()+"' where id="+tabid);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return flag;
	}
	public boolean saveTrainPriv(String priv,String trainid){
		boolean flag = true;
		String privarr[] = priv.split(",");
		StringBuffer str_value = new StringBuffer();
		for(int i=0;i<privarr.length;i++){
			if(privarr[i]==null||privarr[i].trim().length()<1) {
                continue;
            }
			String[] prarr = privarr[i].split(":");
			if(prarr.length==2){
				SysPrivBo privbo=new SysPrivBo(prarr[1],prarr[0],this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.TRAINJOB);
				String content=parser.getContent();
				String ct="";
				if(content==null||content.length()<=0){
					content="";
				}else{
					 str_value.append(content+",");
				}
				ct=","+content+",";
				if(ct.toLowerCase().indexOf(","+trainid.toLowerCase()+",")==-1) {
                    str_value.append(trainid+",");
                } else {
					str_value.delete(0,str_value.length());
					continue;
				}
				if(str_value.length()!=0) {
                    str_value.setLength(str_value.length()-1);
                }
				parser.reSetContent(str_value.toString());
				res_str=parser.outResourceContent();
				privbo.saveResourceString(prarr[1],prarr[0],res_str);
				str_value.delete(0,str_value.length());
			}
		}
		return flag;
	}
	public String[] getPriUser(String tabid){
		String[] priArr = new String[2];
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			String pesonid = "";
			String pesonname = "";
			ArrayList a01list = new ArrayList();
			ArrayList jlist = new ArrayList();
			
			rs= dao.search("select id,status,warnpriv from t_sys_function_priv where status in('1','4')");
			HashSet hs = new HashSet();
			while(rs.next()){
				String id = rs.getString(1);
				String status = rs.getString(2);
				//guodd 2016-01-19 公告修改进入太慢，多次查询数据库
				//SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				//String res_str=privbo.getWarn_str();
				String res_str = rs.getString("warnpriv");
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.ANNOUNCE);
				String content=parser.getContent();
				content=","+content+",";
				if(content!=null&&content.trim().length()>0&&content.indexOf(","+tabid+",")!=-1){
					pesonid+=status+":"+id+",";
					if("1".equals(status)){
						jlist.add(id);
					}else{
						a01list.add(id);
						hs.add(id.substring(0, 3).toUpperCase());
					}
				}
			}
			
            Iterator dblist = hs.iterator();
            while(dblist.hasNext()){
            	String dbname = (String)dblist.next();
            	String a0100arr = "";
            	for(int i=0;i<a01list.size();i++){
            		String a0100 = (String)a01list.get(i);
            		if(a0100.toLowerCase().indexOf(dbname.toLowerCase())!=-1) {
                        a0100arr+="'"+a0100.substring(3)+"',";
                    }
            	}
            	a0100arr = a0100arr.substring(0,a0100arr.length()-1);
            	StringBuffer a01sql = new StringBuffer();
            	a01sql.append("select A0101 from ");
            	a01sql.append(dbname);
            	a01sql.append("A01 where A0100 in(");
            	a01sql.append(a0100arr);
            	a01sql.append(")");
            	rs = dao.search(a01sql.toString());
            	while(rs.next()){
            		pesonname+=rs.getString(1)+",";
            	}
            }
            if(jlist.size()>0){
            	String rolearr = "";
            	for(int i=0;i<jlist.size();i++){
            		String role = (String)jlist.get(i);
            		if(role!=null&&role.trim().length()>0) {
                        rolearr+="'"+jlist.get(i)+"',";
                    }
            	}
            	rolearr = rolearr.substring(0,rolearr.trim().length()-1);
            	rs = dao.search("select role_name from t_sys_role where role_id in("+rolearr+")");
            	while(rs.next()){
            		pesonname+=rs.getString(1)+",";
            	}
            }
            String sql = "select noticeunit from announce where id="+tabid+" and noticeunit is not null ";
            rs=dao.search(sql);
            if(rs.next()){
            	StringBuffer sbids = new StringBuffer();
            	StringBuffer sbnames = new StringBuffer();
            	String noticeunit = rs.getString("noticeunit");
            	String[] orgs = noticeunit.split(",");
            	for(int i=0,n=orgs.length;i<n;i++){
            		String org = orgs[i];
            		if(org.length()>2){
            			sbids.append(org+"`");
            			sbnames.append(AdminCode.getCodeName(org.substring(0,2), org.substring(2))+",");
            		}
            	}
            	if(sbids.length()>0){
            		pesonid +=sbids.toString()+",";
            		pesonname+=sbnames.toString();
            	}
            }
            priArr[0] = pesonid;
            priArr[1] = pesonname;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		
		return priArr;
	}
	
	public void deletePriv(String priv,String tabid){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String pesonid = "";
			RowSet rs = dao.search("select id,status from t_sys_function_priv where status in('1','4')");
			while(rs.next()){
				String id = rs.getString(1);
				String status = rs.getString(2);
				SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.ANNOUNCE);
				String content=parser.getContent();
				content=content+",";
				if(content!=null&&content.trim().length()>0&&content.indexOf(tabid+",")!=-1){
					parser.reSetContent(content.replace(tabid+",", ""));
					res_str=parser.outResourceContent();
					privbo.saveResourceString(id,status,res_str);
				}
			}
			//机构通知对象类型
			dao.update("update announce set noticeunit=null where id="+tabid );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean savePriv2(String priv,String tabid){
		boolean flag = true;
		String privarr[] = priv.split(",");
		StringBuffer str_value = new StringBuffer();
		for(int i=0;i<privarr.length;i++){
			if(privarr[i]==null||privarr[i].trim().length()<1) {
                continue;
            }
			String[] prarr = privarr[i].split(":");
			if(prarr.length==2){
				SysPrivBo privbo=new SysPrivBo(prarr[1],prarr[0],this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.CUSTOM_REPORT);
				String content=parser.getContent();
				String ct="";
				if(content==null||content.length()<=0){
					content="";
				}else{
					 str_value.append(content+",");
				}
				ct=","+content+",";
				if(ct.toLowerCase().indexOf(","+tabid.toLowerCase()+",")==-1) {
                    str_value.append(tabid+",");
                } else {
					str_value.delete(0,str_value.length());
					continue;
				}
				if(str_value.length()!=0) {
                    str_value.setLength(str_value.length()-1);
                }
				parser.reSetContent(str_value.toString());
				res_str=parser.outResourceContent();
				privbo.saveResourceString(prarr[1],prarr[0],res_str);
				str_value.delete(0,str_value.length());
			}
		}
		return flag;
	}
	
	public String getPriUser(String tabid, int constant,String status){
		String priArr = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String pesonid = "";
			String pesonname = "";
			ArrayList a01list = new ArrayList();
			ArrayList jlist = new ArrayList();
			
			RowSet rs = dao.search("select id from t_sys_function_priv where status='"+status+"'");
			HashSet hs = new HashSet();
			while(rs.next()){
				String id = rs.getString(1);
				SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,constant);
				String content=parser.getContent();
				content=","+content+",";
				if(content!=null&&content.trim().length()>0&&content.indexOf(","+tabid+",")!=-1){
					priArr+=id+",";
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return priArr;
	}
	
	/**
	 * 根据用户获得用户的权限
	 * @param IResourceConstant int 
	 * @param status String 状态 0为业务用户
	 * @return 权限 逗号分开
	 */
	public String getPrivByUser( int IResourceConstant,String status){
		String content = "";
		ContentDAO dao = new ContentDAO(this.conn);
		String []st = status.split(",");
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < st.length; i++) {
			buf.append(",'");
			buf.append(st[i]);
			buf.append("'");
		}
		RowSet rs = null;
		try {
			// 按人员
			StringBuffer sql = new StringBuffer();
			sql.append("select id from t_sys_function_priv ");
			sql.append("where status in (");
			sql.append(buf.substring(1));
			sql.append(") and id='");
			sql.append(this.userView.getUserName());
			sql.append("'");
			
			rs = dao.search(sql.toString());
			if (rs.next()){
				String id = rs.getString(1);
				SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant);
				content=parser.getContent();
			}
			
			// 按角色
			ArrayList list = this.userView.getRolelist();
			if (this.userView.isSuper_admin()) {
				list = getAllRoleList();
			}
			if (list == null) {
				list = new ArrayList();
			}
			
			for (int i = 0; i < list.size(); i++) {
				String role_id = (String) list.get(i);
				SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.conn,"warnpriv");
				 String res_str=privbo.getWarn_str();
				 ResourceParser parser=new ResourceParser(res_str,IResourceConstant);
				 String str_content=parser.getContent();
				 if(str_content==null||str_content.length()<=0) {
					 str_content="";
				 }
				 content = content + "," + str_content;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		content = content == null ? "''" : content;
		return content;
	}
	private ArrayList getAllRoleList() {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id from t_sys_function_priv where ");
		strsql.append(" status=");
		strsql.append(GeneralConstant.ROLE);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(strsql.toString());
			while (rs.next()) {
				String id = rs.getString("id");
				id = id == null ? "" : id;
				list.add(id);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 删除记录的权限
	 * @param contant 常量
	 * @param tabid 记录id
	 * @param status 类型
	 */
	public void deleteUserPriv(int contant, String tabid,String status){
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String pesonid = "";
			RowSet rs = dao.search("select id,status from t_sys_function_priv where status='"+status+"'");
			while(rs.next()){
				String id = rs.getString(1);
				SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,contant);
				String content=parser.getContent();
				content=","+content+",";
				if(content!=null&&content.trim().length()>0&&content.indexOf(","+tabid+",")!=-1){
					parser.reSetContent(content.replace(tabid+",", ""));
					res_str=parser.outResourceContent();
					privbo.saveResourceString(id,status,res_str);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
