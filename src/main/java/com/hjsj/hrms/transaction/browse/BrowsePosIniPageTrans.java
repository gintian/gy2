/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Owner
 *
 */
public class BrowsePosIniPageTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		String username = (String)hm.get("userbase");
		username = username != null ? username : "";
		hm.remove("userbase");
		
		String infokind = (String)hm.get("infokind");
		if(infokind != null && !"".equals(infokind)){
		    this.getFormHM().put("inforkind", infokind);
		}
		else{
		    infokind = (String)this.getFormHM().get("inforkind");
		}
		
		String flag = (String)this.getFormHM().get("flag");
		
		String a0100 = (String)hm.get("a0100");
		a0100 = a0100 != null ? a0100 : "";
        if(a0100.trim().length()>0&& "~".equalsIgnoreCase(a0100.substring(0,1)))
        { 
            String _temp=a0100.substring(1); 
            a0100=PubFunc.convert64BaseToString(SafeCode.decode(_temp));
        }
		hm.remove("a0100");
		
		if("infoself".equals(flag)){
			if("4".equals(infokind)){
				a0100 = this.userView.getUserPosId();
			}
		}else{
			if("4".equals(infokind)){
				CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
				a0100 = checkPrivSafeBo.checkOrg(a0100, "");
			}
		}
		
		String selfinfo = (String)hm.get("selfinfo");
		selfinfo = selfinfo != null ? selfinfo:"noinfo";
		hm.remove("selfinfo");
		
		if("infoself".equals(flag) && "noinfo".equals(selfinfo)){
		    selfinfo = "selfinfo";
		}
		
		if(!"".equals(a0100))
		{
		    if("1".equals(selfinfo) || "selfinfo".equalsIgnoreCase(selfinfo))
		        a0100 = this.userView.getUserPosId();
		    else
		    {
		        CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
		        a0100 = cps.checkOrg(a0100, "");
		    }
		}
		
		String a0101="";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select codeitemdesc ");
			buf.append(" from ");
			buf.append("organization where codeitemid='");
			buf.append(a0100+"'");
			this.frowset = dao.search(buf.toString());
			while(this.frowset.next()){
				a0101 = this.frowset.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("currentpage","0");
		this.getFormHM().put("a0101",a0101);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("userpriv", selfinfo);
		
		if("selfinfo".equals(selfinfo)){
		    this.getFormHM().put("cardtype", "myposcard");
		    this.getFormHM().put("havepriv", "1");
		}
		else{
		    this.getFormHM().put("cardtype", "ZP_POS_TEMPLATE");
		    this.getFormHM().put("havepriv", "0");
		}
	}

}
