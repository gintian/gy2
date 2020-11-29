package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveDbTrans extends IBusiness {
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String strsql = "select * from dbname order by dbid";
		ArrayList dblist = new ArrayList();
		ArrayList dbname = new ArrayList();
		try {
			this.frowset = dao.search(strsql.toString());
			while(this.frowset.next()){
				dblist.add(this.frowset.getString("pre").toString());
				dbname.add(this.frowset.getString("dbname").toString());
			}
		} catch (SQLException e) {e.printStackTrace();}
        ArrayList dbture = (ArrayList)this.getFormHM().get("dbtrue");
        String dbname2 = (String)this.getFormHM().get("dbname");
        StringBuffer dbpres = new StringBuffer();
        dbpres.append(" ");
        for(int i=0;i<dbture.size();i++){
        	String bool = (String)dbture.get(i);
        	if("true".equals(bool)){
        		dbpres.append(""+dblist.get(i)+",");
        	}
        }
        dbpres.setLength(dbpres.length()-1);
        LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
        if("bz".equals(dbname2))
        	leadarParamXML.setTextValue(leadarParamXML.BZDBPRE,dbpres.toString());
        else if("hb".equals(dbname2))
        	leadarParamXML.setTextValue(leadarParamXML.HBDBPRE,dbpres.toString());
		leadarParamXML.saveParameter();
		this.getFormHM().put("mess",getSnameMesslist(dbture,dbname));
	}
	 public String getSnameMesslist(ArrayList dbture,ArrayList dbname)
	 {
		 if(dbture==null||dbture.size()<=0||dbname==null||dbname.size()<=0)
	    		return "";	
		 StringBuffer mess = new StringBuffer();
		 for(int i=0;i<dbture.size();i++){
			 if("true".equals(dbture.get(i))){
				 mess.append(dbname.get(i));
				 if((i+1)%2==0)
					 mess.append("<br>");
				 else
					 mess.append(",");
			 }
		 }
		 if(mess.length()!=0)
			 mess.setLength(mess.length()-1);
		 return mess.toString();
	 }
}
