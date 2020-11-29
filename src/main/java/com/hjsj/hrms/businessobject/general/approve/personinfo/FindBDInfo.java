package com.hjsj.hrms.businessobject.general.approve.personinfo;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class FindBDInfo {
	
	public static int  getUnitLength() throws SQLException{
		String sql="select codeitemid from organization  where codesetid='UN'";
		int unitlen=0;
		ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql);
		for(int i=0;i<mylist.size();i++){
			LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
			String uintid =(String) dynabean.get("codeitemid");
			if(unitlen<uintid.length()){
				unitlen=uintid.length();
			}
		}
		return unitlen;
	}
	public static int  getUnitLength(ContentDAO dao) throws SQLException{
		String sql="select codeitemid from organization  where codesetid='UN'";
		int unitlen=0;
		RowSet rs =dao.search(sql);
		while(rs.next()){
			String codeitemid = rs.getString("codeitemid");
			if(unitlen<codeitemid.length()) {
                unitlen=codeitemid.length();
            }
		}
		return unitlen;
	}
	
	public static int  getDepLength() throws SQLException{
		String sql="select codeitemid from organization  where codesetid='UM'";
		int deplen=0;
		ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql);
		for(int i=0;i<mylist.size();i++){
			LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
			String uintid =(String) dynabean.get("codeitemid");
			if(deplen<uintid.length()){
				deplen=uintid.length();
			}
		}
		return deplen;
	}
	
	public static int getDepLength(ContentDAO dao) throws SQLException{
		String sql="select codeitemid from organization  where codesetid='UM'";
		int deplen=0;
		RowSet rs =dao.search(sql);
		while(rs.next()){
			String codeitemid = rs.getString("codeitemid");
			if(deplen<codeitemid.length()) {
                deplen=codeitemid.length();
            }
		}
		return deplen;
	}
	public static HashMap getUDinfo(String kid) throws SQLException{
		HashMap udhm = new HashMap();
		int unitlen=getUnitLength();
		int deplen=getDepLength();
		String sql="select codeitemid,codeitemdesc from organization where codeitemid='"+
		kid.substring(0,unitlen)+"' or codeitemid='"+kid.substring(0,deplen)+"'";
		ArrayList mylist = (ArrayList) ExecuteSQL.executeMyQuery(sql);
		for(int i =0 ;i<mylist.size();i++){
			LazyDynaBean dynabean=(LazyDynaBean) mylist.get(i);
			String codeitemid=(String) dynabean.get("codeitemid");
			String codeitemdesc=(String)dynabean.get("codeitemdesc");
			if(codeitemid.length()==unitlen){
				udhm.put("UN",codeitemdesc);
			}else{
				udhm.put("UM",codeitemdesc);
			}
		}
		return udhm;
	}
	public static HashMap getUDinfo(String kid,ContentDAO dao) throws SQLException{
		HashMap udhm = new HashMap();
		int unitlen=getUnitLength(dao);
		int deplen=getDepLength(dao);
		String sql="select codeitemid,codeitemdesc from organization where codeitemid='"+
		kid.substring(0,unitlen)+"' or codeitemid='"+kid.substring(0,deplen)+"'";
		RowSet rs = dao.search(sql);
		while(rs.next()){
			String codeitemid=rs.getString("codeitemid");
			String codeitemdesc=rs.getString("codeitemdesc");
			if(codeitemid.length()==unitlen){
				udhm.put("UN",codeitemdesc);
			}else{
				udhm.put("UM",codeitemdesc);
			}
		}
		return udhm;
	}
}
