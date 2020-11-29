package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.*;
/**
 * 培训课程推送
 */
public class CoursePushTrans extends IBusiness {

	public void execute() throws GeneralException {
		String personstr = (String)this.getFormHM().get("personstr");
		String sel = (String)this.getFormHM().get("sel");
		String basePath = (String)this.getFormHM().get("basePath");
		if(personstr==null||personstr.length()<10||sel==null||sel.length()<1)
			return;
		
		Set idset = new HashSet();
		HashMap urlmap = new HashMap();
		String username = "";
		String password = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
			password = "userpassword";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
				password = "userpassword";
			} else {
				username = login_name.substring(0, idx);
				password = login_name.substring(idx+1);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
					password = "userpassword";
				}
				
				if ("#".equals(password) || "".equals(password)) {
                    password = "userpassword";
                }
			}
		}
		
		boolean isencry = ConstantParamter.isEncPwd(this.getFrameconn());
		Des des=new Des();
		
		ArrayList emaillist = new ArrayList();
		TrainCourseBo bo = new TrainCourseBo(this.getFrameconn());
		String[] person = personstr.split("`");
		for (int i = 0; i < person.length; i++) {
			if(person[i]==null||person[i].length()<10)
				continue;
			
			String[] strs = person[i].split("::");
			if(strs.length==5){
				String nbase = PubFunc.decrypt(SafeCode.decode(strs[4]));
				String a0100 = PubFunc.decrypt(SafeCode.decode(strs[0]));
				String a0101 = strs[1];
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select b0110,e0122,e01a1,"+username+","+password+" from "+nbase+"A01 where a0100='"+a0100+"'";
				try {
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						String u = this.frowset.getString(username);
						u = u==null?"":u;
						String p = this.frowset.getString(password);
						p = p==null?"":p;
						if(isencry){
							p = des.DecryPwdStr(p);
						}
						idset.add(u);
						String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(u+","+p));
						//urlmap.put(u, basePath+"train/resource/course/mobile/mylession.do?b_search=link&etoken="+etoken+"&appfwd=1");
						urlmap.put(u, basePath+"elearning/mylession/mobile/list.jsp?etoken="+etoken);
						String[] sels = sel.split(",");
						for (int j = 0; j < sels.length; j++) {
							bo.pushCourse(PubFunc.decrypt(SafeCode.decode(sels[j])), nbase, a0100, this.frowset.getString("b0110"), this.frowset.getString("e0122"), this.frowset.getString("e01a1"), a0101, "5","");
						}
						
						LazyDynaBean emailbean = bo.sendEMail(sel,nbase, a0100, basePath);
						if(emailbean != null)
						    emaillist.add(emailbean);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.fillInStackTrace();
				}
			}
			
		}
		
		if(idset.size()>0){
			String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png";
			String[] sels = sel.split(",");
			for (int i = 0; i < sels.length; i++) {
				try {
					HashMap url = new HashMap();
					for(Iterator it = idset.iterator();it.hasNext();){
						String u = (String)it.next();
						String ul = urlmap.get(u).toString();
						ul +="&encryptParam="+PubFunc.encrypt("r5000="+PubFunc.decrypt(SafeCode.decode(sels[i])));
						url.put(u, ul);
					}
					bo.sendCourseToWX(PubFunc.decrypt(SafeCode.decode(sels[i])), "5", picUrl, url, new ArrayList(idset));
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		
		if(emaillist != null && emaillist.size() > 0) {
		    //发送邮件
		    AsyncEmailBo emailbo = new AsyncEmailBo(this.frameconn, this.userView);
		    emailbo.send(emaillist);
		}
		
		this.getFormHM().put("flag", "ok");
	}
	
}
