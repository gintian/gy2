package com.hjsj.hrms.businessobject.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 口令加密/解密操作类
 * @author Owner
 *
 */
public class PassWordEncodeOrDecode {

	private Connection conn;
	private String item;
	private String flag;
	private String name;
	
	/**
	 * 口令加密/解密操作类
	 * @param item  口令指标
	 * @param flag  加密/解密标识(1,2)
	 */
	public PassWordEncodeOrDecode(Connection conn ,String item ,String flag ,String name) {
		this.conn = conn;
		this.item = item;
		this.flag = flag;
		this.name = name;
	}
	
	
	public String exectue() throws GeneralException{
		String info="ok";
		ArrayList dbpreList = this.getDbPreList();
		
		if("1".equals(flag)){//首先验证口令指标是否能加密
			int max = this.getMaxColumnLength(dbpreList);
			boolean b = this.checkEncode(dbpreList,max);
			//System.out.println("max=" + max + "  b=" + b);
			if(b){}else{
				Exception e = new Exception("口令指标加密后超出数据库定义长度,不予加密！");
				throw GeneralExceptionHandler.Handle(e);
			}
			
			
		}
		
		for(int i=0; i<dbpreList.size(); i++){
			String pre = (String) dbpreList.get(i);
			StringBuffer sql = new StringBuffer();
			sql.append(" select a0100 , ");
			sql.append(item);
			sql.append(" from ");
			sql.append(pre);
			sql.append("a01 ");
			
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				RowSet rss = dao.search(sql.toString());
				while(rss.next()){
					String a0100 = rss.getString("a0100");
					String temp = rss.getString(item);
					if(temp == null || "".equals(temp)){
					}else{
						if("1".equals(flag)){//加密
							Des des=new Des();
							temp=des.EncryPwdStr(temp);
						}else{//解密
							Des des=new Des();
							temp=des.DecryPwdStr(temp);
						}
						this.executeUpdateSql(pre,a0100,temp);
					}
				}
			} catch (SQLException e) {
				info="error";
				e.printStackTrace();
			}
			
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select username , ");
		sql.append("password");
		sql.append(" from operuser");
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rss = dao.search(sql.toString());
			while(rss.next()){
				String a0100 = rss.getString("username");
				String temp = rss.getString("password");
				if(temp == null || "".equals(temp)){
				}else{
					if("1".equals(flag)){//加密
						Des des=new Des();
						temp=des.EncryPwdStr(temp);
					}else{//解密
						Des des=new Des();
						temp=des.DecryPwdStr(temp);
					}
					this.executeBUpdateSql(a0100,temp);
				}
			}
		} catch (SQLException e) {
			info="error";
			e.printStackTrace();
		}
	
		return info;
	}
	
	/**
	 * 获得加密口令指标数据库存储长度
	 * @param dbpreList
	 * @return
	 */
	public int getMaxColumnLength(ArrayList dbpreList){
		int max = 0;
		for(int i=0; i<dbpreList.size(); i++){
			String pre = (String) dbpreList.get(i);
			StringBuffer sql = new StringBuffer();
			sql.append(" select a0100 , ");
			sql.append(item);
			sql.append(" from ");
			sql.append(pre);
			sql.append("a01 ");
			try {
				ContentDAO dao = new ContentDAO(this.conn);
				ResultSet rs = dao.search(sql.toString());
				ResultSetMetaData rsmd = rs.getMetaData();
				max = rsmd.getColumnDisplaySize(2);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return max;
	}
	
	/**
	 * 效验口令指标加密后是否超出数据库定义长度
	 * @param dbpreList
	 * @param max
	 * @return
	 */
	public boolean checkEncode(ArrayList dbpreList,int max){
		boolean b = true;
		for(int i=0; i<dbpreList.size(); i++){
			String pre = (String) dbpreList.get(i);
			StringBuffer sql = new StringBuffer();
			sql.append(" select a0100 , ");
			sql.append(item);
			sql.append(" from ");
			sql.append(pre);
			sql.append("a01 ");
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				RowSet rss = dao.search(sql.toString());
				
				while(rss.next()){
					String temp = rss.getString(item);
					if(temp == null || "".equals(temp)){
						temp="";
					}else{
						Des des=new Des();
						temp=des.EncryPwdStr(temp);
					}
					int flag = (temp.getBytes()).length;
					if(flag > max){
						//System.out.println("flag=" + flag);
						b = false;
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append("password");
		sql.append(" from operuser");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rss = dao.search(sql.toString());
			
			while(rss.next()){
				String temp = rss.getString("password");
				if(temp == null || "".equals(temp)){
					temp="";
				}else{
					Des des=new Des();
					temp=des.EncryPwdStr(temp);
				}
				int flag = (temp.getBytes()).length;
				if(flag > max){
					//System.out.println("flag=" + flag);
					b = false;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return b;
	}
	/**
	 * 执行修改操作
	 * @param dbPre
	 * @param a0100
	 * @param value
	 */
	public void executeUpdateSql(String dbPre , String a0100 , String value){
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append(" update ");
		sql.append(dbPre);
		sql.append("A01 set ");
		sql.append(item);
		sql.append("= '");
		sql.append(value);
		sql.append("' where a0100='");
		sql.append(a0100);
		sql.append("'");
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void executeBUpdateSql(String a0100 , String value){
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		sql.append(" update ");
		sql.append("operuser set ");
		sql.append("password");
		sql.append("= '");
		sql.append(value);
		sql.append("' where username='");
		sql.append(a0100);
		sql.append("'");
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 获得系统人员库前缀集合
	 * @return
	 */
	public ArrayList getDbPreList(){
		ArrayList list = new ArrayList();
		String sql="select pre from dbname";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				String pre = rs.getString("pre");
				list.add(pre);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	} 

	
	public void saveUserNamePassword() throws GeneralException{
        String username=this.name;
        String password=this.item;
        if(username==null||"username".equalsIgnoreCase(username)) {
            username="";
        }
        if(password==null||"userpassword".equalsIgnoreCase(password)) {
            password="";
        }
        try{        
		    String login_name=username+","+password;
		    RecordVo vo=new RecordVo("constant");
		    vo.setString("constant","SS_LOGIN_USER_PWD");        
		    vo.setString("str_value",login_name);
		    vo.setString("describe","login_user_password");
		    ContentDAO dao=new ContentDAO(this.conn);
		    ifNoParameterInsert("SS_LOGIN_USER_PWD");
            dao.updateValueObject(vo);
        	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
        	
        }catch(Exception sqle){
  	      throw GeneralExceptionHandler.Handle(sqle);            
        }
	}
	
	public void ifNoParameterInsert(String param_name){
		  String sql="select * from constant where Constant='"+param_name+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try{
			rs=dao.search(sql);		  
			  if(!rs.next()){
				  insertNewParameter(param_name);
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }		 
	}
	
	public void insertNewParameter(String param_name){
		String insert="insert into constant(Constant) values (?)";
		ArrayList list=new ArrayList();
		list.add(param_name);			
		ContentDAO dao = new ContentDAO(conn);
		try{
			dao.insert(insert,list);		    
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
