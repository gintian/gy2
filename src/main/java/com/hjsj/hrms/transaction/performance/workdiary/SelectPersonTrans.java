package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;

public class SelectPersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String personstr = (String) this.getFormHM().get("personstr");
		
		boolean bencrypt = false;
		Object _bencrypt = (String) this.getFormHM().get("bencrypt");
		if(_bencrypt != null && "1".equals((String)_bencrypt)) {
			bencrypt = true;
		}
		
		personstr = personstr != null && personstr.trim().length() > 0 ? personstr
				: "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String result = "";
		String personname = "";
		try {
			String[] personarr = personstr.split("`");
			for (int i = 0; i < personarr.length; i++) {
				String person = personarr[i];
				if(bencrypt) {
					person = PubFunc.decrypt(person);
				}
				if (person != null && person.length() > 3) {
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("select B0110,E0122,E01A1,A0101 from ");
					sqlstr.append(person.substring(0,3));
					sqlstr.append("A01 where A0100='");
					sqlstr.append(person.substring(3));
					sqlstr.append("'");
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next()) {
						personname+=this.frowset.getString("A0101")+",";
					}
				}
			}
			result="success";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("result", result);
		this.getFormHM().put("personname", personname);
		//没有加密时，统一加密后传输。
		if(!bencrypt){
            String[] personarr = personstr.split("`");
            for (int i = 0; i < personarr.length; i++) {
                personarr[i] = PubFunc.encrypt(personarr[i]);
            }
            this.getFormHM().put("personid", StringUtils.join(personarr,"`"));
        }else{
            this.getFormHM().put("personid", personstr);
        }

	}

}
