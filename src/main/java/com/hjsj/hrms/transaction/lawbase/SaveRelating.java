package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveRelating.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 15, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveRelating extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fileid = (String)this.getFormHM().get("a_id");
		ArrayList filename = (ArrayList)this.getFormHM().get("code_fields");
		if(filename==null)
			filename = new ArrayList();
		DocumentParamXML documentParamXML = new DocumentParamXML(this.getFrameconn());
		String codesetid=documentParamXML.getValue(DocumentParamXML.FILESET,"setid");
		String codeitemid=documentParamXML.getValue(DocumentParamXML.FILESET,"fielditem");
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList employelist = new ArrayList();
		ArrayList dblist = new ArrayList();
		String dbsql = "select Pre from DBName";
		try {
			this.frowset = dao.search(dbsql);
			while(this.frowset.next()){
				dblist.add(this.frowset.getString("Pre"));
			}
			for(int i=0;i<dblist.size();i++){
				String sql  = "select a0100,max(i9999) i9999 from "+dblist.get(i)+codesetid+" where "+codeitemid+" = '"+fileid+"' group by a0100";
				RecordVo vo = new RecordVo(dblist.get(i)+codesetid);
				this.frowset  = dao.search(sql);
				while(this.frowset.next()){
					//employelist.add(dblist.get(i)+this.frowset.getString("a0100"));
					vo.setString("a0100",this.frowset.getString("a0100"));
					vo.setString("i9999",this.frowset.getString("i9999"));
					dao.deleteValueObject(vo);
				}
			}
		} catch (SQLException e) {e.printStackTrace();}
		for(int i=0;i<filename.size();i++){
			String dbname = filename.get(i).toString().substring(0,3);
			String a0100 = filename.get(i).toString().substring(3);
			StringBuffer sql = new StringBuffer();
			sql.append("select max(I9999) I9999 from "+dbname+codesetid+" where A0100 = '"+a0100+"'");
			try {
				this.frowset = dao.search(sql.toString());
				int i9999 = 0;
				while(this.frowset.next()){
					i9999 = this.frowset.getInt("I9999");
				}
				RecordVo vo = new RecordVo(dbname+codesetid);
				vo.setString("a0100",a0100);
				vo.setInt("i9999",i9999+1);
				vo.setString(codeitemid,fileid);
				dao.addValueObject(vo);
			} catch (SQLException e) {e.printStackTrace();}
		}
	}

}
