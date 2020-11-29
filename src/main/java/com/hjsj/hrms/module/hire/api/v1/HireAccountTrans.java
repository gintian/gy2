package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.GetZpAccountsBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HireAccountTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String operate = (String) this.formHM.get("operate");
			String type = (String) this.formHM.get("type");
			ResumeBo resumeBo = new ResumeBo(this.frameconn);
			GetZpAccountsBo bo = new GetZpAccountsBo(this.frameconn);
			if ("editPassword".equals(operate)) {
				if(this.userView==null||StringUtils.isEmpty(this.userView.getA0100())) {
					this.formHM.put("return_code", "fail");
					this.formHM.put("return_msg", "请您先登录系统再修改密码！");
					return;
				}
				String return_code="fail";
				String return_msg = "数据处理异常,请联系系统管理员！";
				String serverRandom = (String) this.formHM.get("serverRandom");
				String pw0 = ((String) this.getFormHM().get("pw0")).trim();
				String encryptedClientOldRandom = (String) this.formHM.get("encryptedClientOldRandom");
				String pw1 = ((String) this.getFormHM().get("pw1")).trim();
				String encryptedClientNewRandom = (String) this.formHM.get("encryptedClientNewRandom");
				pw1 = resumeBo.decodeCFCA(encryptedClientNewRandom,pw1,serverRandom);
				//校验新密码是否符合规则
				return_code = resumeBo.validateRules(pw1,false);
				String userName = this.userView.getUserName();
				if("success".equals(return_code)) {
					pw0 = resumeBo.decodeCFCA(encryptedClientOldRandom,pw0,serverRandom);
					//校验原密码是否正确
					ArrayList<String> list = resumeBo.pwValidate(userName, pw0);
					return_code = list.get(0);
					//return_code:not_active账号还未被激活，请到注册邮箱中激活帐号！  error_pw原密码错误
					if("success".equals(return_code)) {
						pw1 = resumeBo.handlerPassword(pw1,"encrypt");
						return_code = bo.setPassWord(pw1, this.userView.getA0100(), resumeBo.getDbName());
						if("success".equals(return_code)) {
						    this.userView.setPassWord(pw1);
						    return_msg = "密码修改成功！";
						}
					}
				}else {
				    if("fail".equals(return_code)) {
				    }else {
				        return_msg = return_code;
				        return_code="fail";
				    }
				}
				this.formHM.put("return_code", return_code);
				this.formHM.put("return_msg", return_msg);
			} else if ("retrievePassword".equals(operate)) {
				if ("verifyId".equals(type)) {// 验证身份
					String info = "ok";
					long between = 0;

					// 需要修改密码的账号
					String username = "";
					if (this.getFormHM().get("emailName") != null) {
						username = (String) this.getFormHM().get("emailName");
					}
					username = PubFunc.decrypt(username);
					// 验证是否发起过修改密码请求
					String active = "";
					if (this.getFormHM().get("active") != null) {
						active = (String) this.getFormHM().get("active");
					}

					String guidkey = isnull(username);
					if ("".equalsIgnoreCase(guidkey)) {
						info = "用户名不存在";
						this.getFormHM().put("info", info);
						return;
					}
					HashMap map = getActive(guidkey);
					String isnull = (String) map.get("isnull");
					if (isnull != null && !"".equalsIgnoreCase(isnull) && isnull.length() > 0) {
						info = "链接失效，请重新找回密码";
						this.getFormHM().put("info", info);
						return;
					}
					// 从库中获取--发起重新设置密码的时间
					String activetime = (String) map.get("activetime");
					// 从库中获取--验证是否发起过修改密码请求
					String active_s = (String) map.get("active");
					// 获取当前时间
					Date date = new Date();
					SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = dfs.format(date);
					Date end = dfs.parse(time);

					Date begin = dfs.parse(activetime);
					between = (end.getTime() - begin.getTime());
					long day = between / (24 * 60 * 60 * 1000);
					// 相差小时数，不满一小时为0
					int hour = (int) (between / (60 * 60 * 1000) - day * 24);
					if (day == 0 && hour >= 2) {
						info = "重置密码链接失效，请重新找回密码";
						this.getFormHM().put("info", info);
						return;
					}

					if (!active.equalsIgnoreCase(active_s)) {
						info = "链接无效，请重新找回密码";
						this.getFormHM().put("info", info);
						return;
					}
					this.getFormHM().put("guidkey", guidkey);
					this.getFormHM().put("info", info);

				} else if ("setPassword".equals(type)) {// 设置新密码
					String return_code = "fail";
					String serverRandom = (String) this.formHM.get("serverRandom");

					String pw0 = ((String) this.getFormHM().get("pw0")).trim();
					String encryptedClientNewLogonRandom = (String) this.formHM.get("encryptedClientNewLogonRandom");

					String pw1 = ((String) this.getFormHM().get("pw1")).trim();
					String encryptedClientConfirmNewLogonRandom = (String) this.formHM.get("encryptedClientConfirmNewLogonRandom");

					this.getFormHM().remove("pw0");
					this.getFormHM().remove("pw1");

					String email = (String) this.getFormHM().get("email");
					email = PubFunc.decrypt(email);
					String guidkey = (String) this.getFormHM().get("guidkey");

					pw1 = resumeBo.decodeCFCA(encryptedClientConfirmNewLogonRandom,pw1,serverRandom);
					pw0 = resumeBo.decodeCFCA(encryptedClientNewLogonRandom,pw0,serverRandom);
					//校验新密码是否符合规则
					return_code = resumeBo.validateRules(pw1,false);
					if("success".equals(return_code)) {
						pw1 = resumeBo.handlerPassword(pw1,"encrypt");
						return_code = bo.getPasswordInfo(pw0, pw1, email, guidkey);
					}
					this.getFormHM().put("return_code", return_code);
				}
			} else if ("retrieveAccount".equals(operate)) {
				//是否显示一个注册一个的功能
				//注册截止时间
				boolean register_flag  = true;//默认是可以注册的
				String regEndTime = RecruitUtilsBo.getRegisterEndTime();
				//判断注册是否已截止
				if(StringUtils.isNotEmpty(regEndTime)) {
					String format = "yyyy-MM-dd HH:mm";
					Date endtime = DateUtils.getDate(regEndTime, format);
					Date now  =  new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(format);
					now = DateUtils.getDate(sdf.format(now), format);
					if(now.after(endtime)) {//如果过了截止时间
						register_flag = false;
					}
				}
				this.getFormHM().put("register_flag", register_flag);
				if ("inputMessage".equals(type)) {// 输入账号信息
					this.getFormHM().put("nameDesc", bo.getA0101Desc());
					this.getFormHM().put("onlynFieldDesc", bo.getOnlynFieldDesc());
					this.getFormHM().put("phoneFieldDesc", bo.getPhoneFieldDesc());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获得active,Activetime
	public HashMap getActive(String guidkey) {
		HashMap map = new HashMap();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select active,");
			sql.append(Sql_switcher.dateToChar("Activetime", "yyyy-mm-dd hh24:mi:ss"));
			sql.append(" Activetime from t_sys_resetpassword");
			sql.append(" where Activetime=(");
			sql.append(" select max(Activetime) from t_sys_resetpassword");
			sql.append("  where guidkey='" + guidkey + "'");
			sql.append(")");
			rs = dao.search(sql.toString());
			if (rs.next()) {
				String active = rs.getString("active");// 发起的标示
				String activetime = rs.getString("Activetime");// 发起的时间
				map.put("active", active);
				map.put("activetime", activetime);
			} else {
				map.put("isnull", "isnull");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	// 获得guidkey
	public String isnull(String username) throws GeneralException {
		String guidkey = "";
		try {
			RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname = ""; // 应聘人员库
			if (vo != null)
				dbname = vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey");
			sql.append(" from " + dbname + "A01");
			sql.append(" where UserName=?");
			ArrayList<String> list = new ArrayList<String>();
			list.add(username);
			rs = dao.search(sql.toString(), list);
			if (rs.next()) {
				guidkey = rs.getString("guidkey");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return guidkey;

	}

}
