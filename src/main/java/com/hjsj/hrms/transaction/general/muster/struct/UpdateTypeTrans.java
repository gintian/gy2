package com.hjsj.hrms.transaction.general.muster.struct;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UpdateTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String enteryType=(String)this.getFormHM().get("enteryType");//=muster花名册，=hmuster是高级花名册
		String fromid = (String)this.getFormHM().get("fromid");
		String toid = (String)this.getFormHM().get("toid");
		String table =(String)this.getFormHM().get("table");
		if("muster".equalsIgnoreCase(enteryType))
		{
	    	String primarykey_column_name =(String)this.getFormHM().get("primarykey_column_name");
	    	String father_column_name =(String)this.getFormHM().get("father_column_name");
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	StringBuffer sql = new StringBuffer();
		
	    	if(toid.indexOf("X")!=-1|| "root".equalsIgnoreCase(toid)){
	    		//"select ModuleFlag from lname where Tabid='"+tabid+"'"
	    		if(toid.indexOf("X")!=-1){
		    		toid=toid.trim().substring(1);
			    	String moduleflag = modeflagValue(dao,fromid,"lname","moduleflag");
			    	toid = replaceModule(moduleflag,toid);
		    	}else{
		    		String moduleflag = modeflagValue(dao,fromid,"lname","moduleflag");
		    		toid = replaceModule(moduleflag,"00");
	    		}
	    	}else{
	    		sortMuster(dao,fromid,toid,"lname");
		    	String moduleflag = modeflagValue(dao,toid,"lname","moduleflag");
		    	toid = moduleflag;
	    	}
	    	sql.append("update ");
	    	sql.append(table);
	    	sql.append(" set ");
    		sql.append(father_column_name);
	    	sql.append(" = '");
	    	sql.append(toid);
    		sql.append("' where ");
	    	sql.append(primarykey_column_name);
	    	sql.append(" = '");
	    	sql.append(fromid);
	      	sql.append("'");
	    	try {
	     		dao.update(sql.toString());
	    	} catch (SQLException e)
	    	{
	    		e.printStackTrace();
	    	}
		}
		else{
			String primarykey_column_name ="tabid";
	    	String father_column_name ="sortid";
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	StringBuffer sql = new StringBuffer();
		
	    	if(toid.indexOf("X")!=-1|| "root".equalsIgnoreCase(toid)){
	    		if(toid.indexOf("X")!=-1){
		    		toid=toid.trim().substring(1);
			    	//String moduleflag = modeflagValue(dao,fromid,"muster_name","sortid");
			    	//toid = moduleflag;
		    	}else{
		    		String moduleflag = modeflagValue(dao,fromid,"muster_name","sortid");
		    		toid = moduleflag;
	    		}
	    	}else{
	    		sortMuster(dao,fromid,toid,"muster_name");
		    	String moduleflag = modeflagValue(dao,toid,"muster_name","sortid");
		    	toid = moduleflag;
	    	}
	    	sql.append("update ");
	    	sql.append(table);
	    	sql.append(" set ");
    		sql.append(father_column_name);
	    	sql.append(" = '");
	    	sql.append(toid);
    		sql.append("' where ");
	    	sql.append(primarykey_column_name);
	    	sql.append(" = '");
	    	sql.append(fromid);
	      	sql.append("'");
	    	try {
	     		dao.update(sql.toString());
	    	} catch (SQLException e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	}
	private String modeflagValue(ContentDAO dao,String tabid,String tableName,String column){
		String moduleflag = "";
		try {
			RowSet rs = dao.search("select "+column+" from "+tableName+" where Tabid='"+tabid+"'");
			if(rs.next())
				moduleflag=rs.getString(column);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return moduleflag;
	}
	private String replaceModule(String moduleflag,String toid){
		String moduleid = "";
		moduleflag=moduleflag!=null&&moduleflag.trim().length()>3?moduleflag:"";
		if(moduleflag.length()<1)
			return toid;
		moduleid+=moduleflag.substring(0,1)+toid+moduleflag.substring(3);
		return moduleid;
	}
	private void sortMuster(ContentDAO dao,String fromid,String toid,String tableName){
		StringBuffer buf = new StringBuffer();
		buf.append("select tabid from "+tableName+" order by norder");
		try {
			ArrayList tabidlist = new ArrayList();
			RowSet rs = dao.search(buf.toString());
			int i=0;
			while(rs.next()){
				String tabid = rs.getString("tabid");
				if(tabid!=null&&tabid.trim().length()>0){
					if(tabid.equalsIgnoreCase(fromid)){
						continue;
					}
					if(tabid.equalsIgnoreCase(toid)){
						i++;
						ArrayList list = new ArrayList();
						list.add(i+"");
						list.add(fromid);
						tabidlist.add(list);
					}
					i++;
					ArrayList list = new ArrayList();
					list.add(i+"");
					list.add(tabid);
					tabidlist.add(list);
				}
			}
			dao.batchUpdate("update "+tableName+" set norder=? where tabid=?", tabidlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
