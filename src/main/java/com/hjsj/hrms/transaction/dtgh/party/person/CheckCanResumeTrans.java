package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * @author xujian
 *Mar 1, 2010
 */
public class CheckCanResumeTrans extends IBusiness {


	public void execute() throws GeneralException {
		String param = (String)this.getFormHM().get("param");
		String a0100s = (String)this.getFormHM().get("a0100s");
		String userbase = (String)this.getFormHM().get("userbase");
		userbase = userbase!=null&&userbase.length()>0?userbase:"usr";
		try{
			String[] as = a0100s.split(",");
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			String polity = xml.getNodeAttributeValue("/param/polity", "column");
			polity = polity!=null&&polity.length()>0?polity:"";
			String party = xml.getNodeAttributeValue("/param/polity/party","value");
			String member=xml.getNodeAttributeValue("/param/polity/member","value");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("Y".equals(param)){
				String p[]=polity.split("\\.");
				if(p.length==2){
					if("A01".equalsIgnoreCase(p[0])){
						for(int i=0;i<as.length;i++){
							if(as[i].length()>0)
								this.getFormHM().put(as[0], "none");
						}
					}else{
						for(int i=0;i<as.length;i++){
							String sql="select * from "+userbase+p[0]+" where a0100='"+as[i]+"' and "+p[1]+"='"+party+"'";
							this.frecset = dao.search(sql);
							if(this.frecset.next()){
								this.getFormHM().put(as[i], "block");
							}else{
								this.getFormHM().put(as[i], "none");
							}
						}
					}
				}else{
					for(int i=0;i<as.length;i++){
						if(as[0].length()>0)
							this.getFormHM().put(as[0], "none");
					}
				}
			}else if("V".equals(param)){
				String p[]=polity.split("\\.");
				if(p.length==2){
					if("A01".equalsIgnoreCase(p[0])){
						for(int i=0;i<as.length;i++){
							if(as[i].length()>0)
								this.getFormHM().put(as[0], "none");
						}
					}else{
						for(int i=0;i<as.length;i++){
							String sql="select * from "+userbase+p[0]+" where a0100='"+as[i]+"' and "+p[1]+"='"+member+"'";
							this.frecset = dao.search(sql);
							if(this.frecset.next()){
								this.getFormHM().put(as[i], "block");
							}else{
								this.getFormHM().put(as[i], "none");
							}
						}
					}
				}else{
					for(int i=0;i<as.length;i++){
						if(as[0].length()>0)
							this.getFormHM().put(as[0], "none");
					}
				}
			}
		}catch(Exception e){
			
		}finally{
			
		}
	}

}
