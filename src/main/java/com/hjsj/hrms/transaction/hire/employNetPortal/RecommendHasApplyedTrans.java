/**   
* @Title: RecommendHasApplyedTrans.java 
* @Package com.hjsj.hrms.transaction.hire.employNetPortal 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xucs   
* @date 2015年2月7日 下午1:25:49 
* @version V1.0   
*/ 
package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: RecommendHasApplyedTrans 
 * @Description: TODO(校验被推荐人员是否应聘了当前职位) 
 * @author xucs
 * @date 2015年2月7日 下午1:25:49 
 *  
 */
public class RecommendHasApplyedTrans extends IBusiness {

	public void execute() throws GeneralException {
		String infor ="";
		try{
			String reCommendoption = (String) this.getFormHM().get("reCommendoption");
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("one".equals(reCommendoption)){
				String a0100 = (String) this.getFormHM().get("a0100s");
				a0100 = PubFunc.decrypt(a0100);
				String userName = (String) this.getFormHM().get("userNames");
				String z0301 = (String) this.getFormHM().get("z0301");
				z0301=SafeCode.decode(z0301);
				String posName = (String) this.getFormHM().get("posName");
				posName = SafeCode.decode(posName);
				StringBuffer queryBuffer = new StringBuffer();
				queryBuffer.append("select * from zp_pos_tache where a0100=? and Zp_pos_id=?");
				ArrayList values = new ArrayList();
				values.add(a0100);
				values.add(z0301);
				this.frowset=dao.search(queryBuffer.toString(), values);
				if(this.frowset.next()){
					if(userName.indexOf(",") != -1){
						userName = userName.substring(0, userName.length()-1);
					}
					infor=userName+ResourceFactory.getProperty("hire.out.zp_person.alreadyapply")+posName+ResourceFactory.getProperty("hire.out.can.not.continue.recommend");
				}
				this.getFormHM().put("infor", infor);
				this.getFormHM().put("a0100s", PubFunc.encrypt(a0100));
				this.getFormHM().put("userNames", userName);
				this.getFormHM().put("z0301", z0301);
				this.getFormHM().put("from", "one");//来自多人推荐
			}else{
				StringBuffer queryBuffer = new StringBuffer();
				ArrayList values = new ArrayList();
				HashMap userNameMap = new HashMap();
				String a0100s = (String) this.getFormHM().get("a0100s");
				String a0100Array[] = a0100s.split(",");
				String userNames = (String) this.getFormHM().get("userNames");
				String userNameArray[] = userNames.split(",");
				String z0301 = (String) this.getFormHM().get("z0301");
				z0301=SafeCode.decode(z0301);
				String posName = (String) this.getFormHM().get("posName");
				posName = SafeCode.decode(posName);
				queryBuffer.append("select * from zp_pos_tache where a0100 in(");
				for(int i=0;i<a0100Array.length;i++){
					String a0100 = a0100Array[i];
					a0100 = PubFunc.decrypt(a0100);
					if(i==a0100Array.length-1){
						queryBuffer.append("?");
					}else{
						queryBuffer.append("?,");
					}
					values.add(a0100);
					String userName =userNameArray[i];
					userNameMap.put(a0100, userName);
				}
				queryBuffer.append(") and Zp_pos_id=?");
				values.add(z0301);
				this.frowset=dao.search(queryBuffer.toString(), values);
				while(this.frowset.next()){
					String a0100 = this.frowset.getString("a0100");
					String userName = (String) userNameMap.get(a0100);
					infor=infor+userName+",";
				}
				if(infor.trim().length()>1){
					infor = infor.substring(0, infor.length()-1);
					infor = infor+ResourceFactory.getProperty("hire.out.zp_person.alreadyapply")+posName+ResourceFactory.getProperty("hire.out.can.not.continue.recommend");
				}
				this.getFormHM().put("infor", infor);
				this.getFormHM().put("a0100s", a0100s);
				this.getFormHM().put("userNames", userNames);
				this.getFormHM().put("z0301", z0301);
				this.getFormHM().put("from", "more");//来自多人推荐
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
