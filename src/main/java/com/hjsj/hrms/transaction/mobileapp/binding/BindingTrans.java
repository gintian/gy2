package com.hjsj.hrms.transaction.mobileapp.binding;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p> Title: ContactsTrans </p>
 * <p> Description: 登录绑定认证 </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2015-7-6 下午4:49:33 </p>
 * @author xuj
 * @version 1.0
 */
public class BindingTrans  extends IBusiness {

	private static final long serialVersionUID = 1L;
	
	private enum TransType {
		/**人工激活*/
		handAouth,
		/**发送短信验证码*/
		sendAuthCode,
		/**短信激活*/
		smsAouth
	}
	@SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String succeed="false";
		try{
			String transType = (String)this.getFormHM().get("transType");
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(TransType.handAouth.name().equals(transType)){
				String idfv = (String)this.getFormHM().get("idfv");
				String model = (String)this.getFormHM().get("model");
				String username = this.userView.getUserName();
				RecordVo vo = new RecordVo("t_sys_login_user_info");
				vo.setString("username", username);
				if(dao.isExistRecordVo(vo)){
					vo.setString("mobile_model", model);
					vo.setString("mobile_bind_id", idfv);
					vo.setDate("mobile_bind_time", new Date());
					vo.setInt("mobile_is_oauth", 0);
					dao.updateValueObject(vo);
				}else{
					vo.setString("mobile_model", model);
					vo.setString("mobile_bind_id", idfv);
					vo.setDate("mobile_bind_time", new Date());
					vo.setInt("mobile_is_oauth", 0);
					dao.addValueObject(vo);
				}
				BindingConstant.applyAouth(username, idfv);
			}else if(TransType.sendAuthCode.name().equals(transType)){
				boolean flag = true;
				String random = RandomStringUtils.random(6,false,true);
				String sys_name = SystemConfig.getPropertyValue("sys_name");
			    if(sys_name.length()==0){
			    	sys_name = ResourceFactory.getProperty("frame.logon.title");
			    }
				String msg = "【"+sys_name+"】"+random+","+ResourceFactory.getProperty("identification.info");
				String mobile = "";
				String sql = "";
				if (this.userView.getStatus() == 4) {
					String loginname = getUserName();
					RecordVo mobilevo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
					mobile = mobilevo.getString("str_value");
					if(mobile==null||mobile.length()<=0){
						this.getFormHM().put("msg", "0");
						flag = false;
					}
					sql = "select " + mobile + " phone from "
							+ this.userView.getDbname() + "A01 where "
							+ loginname + " = '" + this.userView.getUserName() + "'";
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						mobile = this.frowset.getString("phone")==null?"":this.frowset.getString("phone");
					}
				} else {
					sql = "select phone from OperUser where username = '"+this.userView.getUserName()+"'";
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						mobile = this.frowset.getString("phone")==null?"":this.frowset.getString("phone");
					}
				}
				if(mobile.length()<1){
					this.getFormHM().put("msg", "0");
					flag = false;
				}
				Pattern pattern = Pattern.compile("^[1][3,4,5,8][0-9]{9}$");
				Matcher matcher = pattern.matcher(mobile);
				if(!matcher.matches()){
					this.getFormHM().put("msg", "0");
					flag = false;
				}
				if(flag){
					SmsBo bo = new SmsBo(this.getFrameconn(),this.userView);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("sender", "ehr");
					bean.set("receiver", this.userView.getUserFullName());
					bean.set("phone_num", mobile);
					bean.set("msg",msg);
					ArrayList<LazyDynaBean> msglist = new ArrayList<LazyDynaBean>();
					msglist.add(bean);
					try{
						bo.batchSendMessage(msglist);
						String returnmobile = mobile.substring(0, 3)+"*****"+mobile.subSequence(mobile.length()-3, mobile.length());
						this.getFormHM().put("smscode", random);
						this.getFormHM().put("mobile", returnmobile);
						this.getFormHM().put("msg", "1");
					}catch(Exception e){
						this.getFormHM().put("msg", "0");
					}
				}
			}else if(TransType.smsAouth.name().equals(transType)){
				//DynaBean deviceInfo = (DynaBean)this.getFormHM().get("deviceInfo");
				String idfv = (String)this.getFormHM().get("idfv")/*deviceInfo.get("idfv")*/;
				String model = (String)this.getFormHM().get("model")/*deviceInfo.get("deviceModel")*/;
				String username = this.userView.getUserName();
				RecordVo vo = new RecordVo("t_sys_login_user_info");
				vo.setString("username", username);
				if(dao.isExistRecordVo(vo)){
					vo.setString("mobile_model", model);
					vo.setString("mobile_bind_id", idfv);
					vo.setDate("mobile_bind_time", new Date());
					vo.setDate("mobile_oauth_time", new Date());
					vo.setInt("mobile_is_oauth", 1);
					dao.updateValueObject(vo);
				}else{
					vo.setString("mobile_model", model);
					vo.setString("mobile_bind_id", idfv);
					vo.setDate("mobile_bind_time", new Date());
					vo.setDate("mobile_oauth_time", new Date());
					vo.setInt("mobile_is_oauth", 1);
					dao.addValueObject(vo);
				}
				BindingConstant.smsAouth(username, idfv);
			}
			succeed="true";
		}catch(Exception e){
			this.getFormHM().put("message", e.getMessage());
			e.printStackTrace();
		}
		//System.out.println(transType);
		this.getFormHM().put("succeed",succeed);
	}
	
	/**
	 * 获取用户登录指标
	 * @Title: getUserName   
	 * @return String
	 */
	public String getUserName() {
		String username = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
			} else {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
				}
			}
		}
		return username;
	}

}
