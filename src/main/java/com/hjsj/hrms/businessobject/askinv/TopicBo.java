package com.hjsj.hrms.businessobject.askinv;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class TopicBo {
	private Connection conn = null;
	
	public TopicBo(Connection conn,UserView userView){
		this.conn = conn;
	}
	
	public boolean savePriv(String priv,String tabid){
		boolean flag = true;
		String privarr[] = priv.split(",");
		StringBuffer str_value = new StringBuffer();
		for(int i=0;i<privarr.length;i++){
			if(privarr[i]==null||privarr[i].trim().length()<1) {
                continue;
            }
			String[] prarr = privarr[i].split(":");
			SysPrivBo privbo=new SysPrivBo(prarr[1],prarr[0],this.conn,"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,IResourceConstant.INVEST);
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
			SysPrivBo privbo=new SysPrivBo(prarr[1],prarr[0],this.conn,"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,IResourceConstant.INVEST);
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
		return flag;
	}
	
	public String[] getPriUser(String tabid){
		String[] priArr = new String[2];
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String pesonid = "";
			String pesonname = "";
			ArrayList a01list = new ArrayList();
			ArrayList jlist = new ArrayList();
			
			RowSet rs = dao.search("select id,status from t_sys_function_priv where status in('1','4')");
			HashSet hs = new HashSet();
			while(rs.next()){
				String id = rs.getString(1);
				String status = rs.getString(2);
				SysPrivBo privbo=new SysPrivBo(id,status,this.conn,"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.INVEST);
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
            priArr[0] = pesonid;
            priArr[1] = pesonname;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				ResourceParser parser=new ResourceParser(res_str,IResourceConstant.INVEST);
				String content=parser.getContent();
				content=content+",";
				if(content!=null&&content.trim().length()>0&&content.indexOf(tabid+",")!=-1){
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
