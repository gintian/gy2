package com.hjsj.hrms.transaction.train.request;

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

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class TrainCoursePushTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = "ok";
		String classid = (String)this.getFormHM().get("classid");
		String r5000 = (String)this.getFormHM().get("r5000");
		String basePath = (String)this.getFormHM().get("basepath");
		String sql = "select NBase,r4001 from R40 where R4013='03' and R4005='" + classid + "'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		TrainCourseBo bo = new TrainCourseBo(this.getFrameconn());
		RecordVo vo = null;
		try {
		    ArrayList emaillist = new ArrayList();
		    String personNames = "";
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String nbase = this.frowset.getString("nbase");
				String a0100 = this.frowset.getString("r4001");
				vo = new RecordVo(nbase+"A01");
				vo.setString("a0100", a0100);
				vo = dao.findByPrimaryKey(vo);
				if(vo==null)
					continue;
				SendWeiXin(r5000,"5",basePath,a0100,nbase,bo);
				bo.pushCourse(r5000, nbase, a0100, vo.getString("b0110"), vo.getString("e0122"), vo.getString("e01a1"), vo.getString("a0101"), "3",classid);
				
				LazyDynaBean emailbean = bo.sendEMail(SafeCode.encode(PubFunc.encrypt(r5000)), nbase, a0100, basePath);
                if(emailbean != null)
                    emaillist.add(emailbean);
			}
			
			//发送邮件
	        AsyncEmailBo emailbo = new AsyncEmailBo(this.frameconn, this.userView);
	        emailbo.send(emaillist);
			
		} catch (Exception e) {
			//e.printStackTrace();
			flag = "error";
		}
		
		this.getFormHM().put("flag", flag);
	}
	
	private void SendWeiXin(String r5000,String lesson_from,String basePath,String a0100,String nbase,TrainCourseBo bo){
		 //用户登陆指标，主要用于取得微信人员id
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
		String sql = "select "+username+","+password+" from "+nbase+"a01 where a0100 = '"+a0100+"'";
		ContentDAO dao = null;
		HashMap urlmap = new HashMap();
		ArrayList idlist = new ArrayList();
		RowSet rs = null;
		try{
			dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql);
			while(rs.next()){
				username = rs.getString(username)==null?"":rs.getString(username);
				password = rs.getString(password)==null?"":rs.getString(password);
			}
			boolean isencry = ConstantParamter.isEncPwd(this.getFrameconn());
    		Des des=new Des();
    		if(isencry){
    			password = des.DecryPwdStr(password);
    		}
    		String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
			urlmap.put(username, basePath+"elearning/mylession/mobile/list.jsp?etoken="+etoken+"&encryptParam="+PubFunc.encrypt("r5000="+r5000));
			idlist.add(username);
			String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png";
			bo.sendCourseToWX(r5000, lesson_from, picUrl, urlmap, idlist);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
}
