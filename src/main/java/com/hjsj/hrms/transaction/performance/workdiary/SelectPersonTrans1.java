package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPersonTrans1 extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String personstr = (String) this.getFormHM().get("personstr");
		personstr = personstr != null && personstr.trim().length() > 0 ? personstr
				: "";
		String selecteds=(String) this.getFormHM().get("selecteds");
		selecteds = selecteds != null && selecteds.trim().length() > 0 ? selecteds
				: "";
		
		
		boolean bencrypt = false;
		Object _bencrypt = (String) this.getFormHM().get("bencrypt");
		if(_bencrypt != null && "1".equals((String)_bencrypt)) {
			bencrypt = true;
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String result = "success";
		String[] selected=selecteds.split(",");
		for (int j = 0; j < selected.length; j++) {
			String p0100=selected[j];
			try {
				this.insertPerDairy(dao, p0100, personstr, bencrypt);
			} catch (SQLException e) {
				result="error";
				e.printStackTrace();
			}
		
		}
		this.getFormHM().put("result", result);
	}

	private void insertPerDairy(ContentDAO dao,String p0100,String personstr, boolean bencrypt) throws SQLException{
			String sql = "delete from per_diary_actor where P0100='"+p0100+"'";
			dao.update(sql);
			
			String[] personarr = personstr.split("`");
			List personlist = new ArrayList();
			for (int i = 0; i < personarr.length; i++) {
				String person = personarr[i];
				if(bencrypt) {
					person = PubFunc.decrypt(person);
				}
				Map voPerson = new HashMap();
				if (person != null && person.length() > 3) {
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("select B0110,E0122,E01A1,A0101 from ");
					sqlstr.append(person.substring(0,3));
					sqlstr.append("A01 where A0100='");
					sqlstr.append(person.substring(3).replaceAll("\\,", ""));
					sqlstr.append("'");
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next()) {
						voPerson.put("DBNAME", person.substring(0,3));
						voPerson.put("A0100", person.substring(3));
						voPerson.put("B0110", this.frowset
								.getString("B0110"));
						voPerson.put("E0122", this.frowset
								.getString("E0122"));
						voPerson.put("E01A1", this.frowset
								.getString("E01A1"));
						voPerson.put("A0101", this.frowset
								.getString("A0101"));
						personlist.add(voPerson);
						
					}
				}
			}
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("insert into per_diary_actor (NBASE,A0100,P0100,B0110,E0122,E01A1,A0101,state) values(");
			sqlstr.append("?,?,?,?,?,?,?,1)");
			ArrayList perlist = new ArrayList();
			for (int i = 0; i < personlist.size(); i++) {
				Map voPerson = (Map) personlist.get(i);
				ArrayList listvalue = new ArrayList();
				listvalue.add((String)voPerson.get("DBNAME"));
				listvalue.add(((String)voPerson.get("A0100")).replaceAll("\\,", ""));
				listvalue.add(p0100);
				listvalue.add((String)voPerson.get("B0110"));
				listvalue.add((String)voPerson.get("E0122"));
				listvalue.add((String)voPerson.get("E01A1"));
				listvalue.add((String)voPerson.get("A0101"));

				if(((String)voPerson.get("A0100")).replaceAll("\\,", "")!=null && ((String)voPerson.get("A0100")).replaceAll("\\,", "").length()>=0){
					WorkdiarySelStr wss=new WorkdiarySelStr();
					try {wss.sendEMail(this.getFrameconn(), ((String)voPerson.get("A0100")).replaceAll("\\,", ""), p0100,"0");} catch (GeneralException e) {e.printStackTrace();}
				}
				perlist.add(listvalue);
			}
			dao.batchInsert(sqlstr.toString(), perlist);
	}
}
