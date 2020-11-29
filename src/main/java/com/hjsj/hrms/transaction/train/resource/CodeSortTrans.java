package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author LiWeiChao
 * 2011-08-17 13:44:00
 */
public class CodeSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		String codeitemid = (String) hm.get("codeitemid");
		if(!"root".equalsIgnoreCase(codeitemid))
		    codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
		hm.remove("codeitemid");
		String codesetid = (String) hm.get("codesetid");
		codesetid = PubFunc.decrypt(SafeCode.decode(codesetid));
		hm.remove("codesetid");

		StringBuffer buf = new StringBuffer();
		if(codeitemid==null||codeitemid.length()<1||"root".equalsIgnoreCase(codeitemid)){
			buf.append("select codeitemid,codeitemdesc,a0000,b0110 from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid");
		}else{
			buf.append("select codeitemid,codeitemdesc,a0000,b0110 from codeitem where codesetid='"+codesetid+"' and codeitemid<>parentid");
			buf.append(" and parentid='");
			buf.append(codeitemid);
			buf.append("'");
		}
		buf.append(" order by a0000");
		ArrayList sortlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			CommonData dataobj = null;
			this.frowset = dao.search(buf.toString());
			while (this.frowset.next()) {
				String tmpb0110=this.frowset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	if(!this.userView.isSuper_admin()&&tbo.isUserParent(tmpb0110)==-1)
            		continue;
            	
				String norder = this.frowset.getString("a0000");
				String courseName = this.frowset.getString("codeitemdesc");
				norder = norder != null && norder.trim().length() > 0 ? norder
						: "0";
				dataobj = new CommonData(SafeCode.encode(PubFunc.encrypt(this.frowset.getString("codeitemid"))) + "::"
						+ norder, courseName);
				sortlist.add(dataobj);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("sortlist", sortlist);
			this.getFormHM().put("codesetid", SafeCode.encode(PubFunc.encrypt(codesetid)));
		}
	}

}
