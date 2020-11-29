package com.hjsj.hrms.module.vuesupport.user.transaction;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.hjadmin.cache.CacheUtil;
import com.hrms.hjsj.hjadmin.cache.FrameworkCacheKeysEnum;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

public class HjadminSaveNewPasswordTrans extends IBusiness {

    final static String apikey="hjadminuifkgeqeqkkey";
    @Override
    public void execute() throws GeneralException {
        try {
            String oldpwd = (String) this.getFormHM().get("nowPassWord");
            String newpwd = (String) this.getFormHM().get("newPassWord");
            String newokpwd = (String) this.getFormHM().get("repeatPassWord");

            UserObjectBo userObjectBo = new UserObjectBo(this.getFrameconn());

            /*为了密码传输安全，前端对密码进行了加密，此处解密。 guodd 2018-09-28*/
            oldpwd = PubFunc.keyWord_reback(oldpwd);
            oldpwd = oldpwd == null ? "" : SafeCode.decrypt(apikey, oldpwd).trim();
            newpwd = PubFunc.keyWord_reback(newpwd);
            newpwd = newpwd == null ? "" : SafeCode.decrypt(apikey, newpwd).trim();
            newokpwd = PubFunc.keyWord_reback(newokpwd);
            newokpwd = newokpwd == null ? "" : SafeCode.decrypt(apikey, newokpwd).trim();

            if (oldpwd == null || newpwd == null || newokpwd == null)
                throw GeneralExceptionHandler.Handle(new Exception("数据不合法，操作终止。"));

            //检查userview
            UserView userView = this.getUserView();

            /**分析用户名和密码是否存在特殊字符*/

            validateUserNamePwdComma(newokpwd);
            validateUserNamePwdComma(newpwd);
            validateUserNamePwdComma(oldpwd);
            //现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
            userObjectBo.validatePasswordNew(newokpwd);


            /**口令加密*/
            Des des = new Des();
            String _newpwd = "";

            String userP = userView.getPassWord();

            if (ConstantParamter.isEncPwd(this.getFrameconn())) {
                _newpwd = des.DecryPwdStr(userP.replaceAll("''", "'"));
            } else {
                _newpwd = userP.replaceAll("''", "'");
            }
            if (Sql_switcher.dbflag == 1) {
                if (_newpwd.equalsIgnoreCase(newpwd)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("error.password.last.same"), "", ""));
                }
            } else {
                if (_newpwd.equals(newpwd)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("error.password.last.same"), "", ""));
                }
            }

            if (Sql_switcher.dbflag == 1) {
                if (!newpwd.equalsIgnoreCase(newokpwd)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("errors.sys.newpassword"), "", ""));
                }
            } else {
                if (!newpwd.equals(newokpwd)) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("errors.sys.newpassword"), "", ""));
                }
            }
            if (userObjectBo.checkHistoryPwd(newpwd, userView.getUserName())) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", com.hjsj.hrms.utils.ResourceFactory.getProperty("error.password.history.same").replace("{0}", SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD)), "",""));
            }

            /**口令加密*/
            if (ConstantParamter.isEncPwd(this.getFrameconn())) {
                Des des0 = new Des();
                newpwd = des0.EncryPwdStr(newpwd);
            }
            userView.setPassWord(newpwd.replaceAll("'", "''"));
            String tablename = null;

            /**平台用户*/
            if (userView.getStatus() == 0) {
                tablename = "operuser";
                RecordVo vo = new RecordVo(tablename);
                vo.setString("password", newpwd);
                vo.setString("username", userView.getUserId());
                vo.setDate("modtime", DateStyle.getSystemTime());
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                dao.updateValueObject(vo);
            }
            /**自助用户*/
            if (userView.getStatus() == 4) {
                /**登录参数表,登录用户指定不是username or userpassword*/
                String username = null;
                String password = null;
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
                        if ("#".equals(username) || "".equals(username))
                            username = "username";
                        password = login_name.substring(idx + 1);
                        if ("#".equals(password) || "".equals(password))
                            password = "userpassword";
                    }
                }
                String dbpre = userView.getDbname();
                tablename = dbpre + "A01";
                RecordVo vo = new RecordVo(tablename);
                vo.setString(password, newpwd);
                vo.setString("a0100", userView.getUserId());
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                dao.updateValueObject(vo);
            }

            //处理历史密码、首次密码修改 xuj add 2013-10-9

            if (ConstantParamter.isEncPwd(this.getFrameconn())) {
                Des des0 = new Des();
                newpwd = des0.DecryPwdStr(newpwd);
            }
            userObjectBo.doHistoryPwd(newpwd, userView.getUserName());

            CacheUtil.set(FrameworkCacheKeysEnum.userViewCache, userView.getUserName(), userView);
        }catch (Exception e){
            e.printStackTrace();
            throw new GeneralException(e.getMessage());
        }

    }

//    /**
//     * 检查userview
//     * 当设置了登陆验证类时（system.properties中设置了logonclass），自助用户登录时会自动转成关联的业务用户身份登录
//     * 修改密码时，程序需要判断一下用户到底想要修改的是自助的密码，还是业务用户的密码。
//     * 此方法用户获取业务用户关联的自助用户的userview
//     *
//     * @return
//     */
//    private UserView checkUserView(String oldPassword) {
//        UserView uv = this.getUserView();
//        try {
//            String logonClassFunc = SystemConfig.getPropertyValue("logonclass_func");
//            /*如果配置了logonclass_func参数，并且是业务用户且关联了自助用户*/
//            if (logonClassFunc != null && logonClassFunc.length() > 0 && this.getUserView().getStatus() == 0 && this.getUserView().getA0100() != null) {
//                Class funcClass = Class.forName(logonClassFunc);
//                Object func = funcClass.newInstance();
//                //获取自助用户的用户名
//                Method getChangePwdUsername = funcClass.getMethod("getChangePwdUsername", String.class);
//                String username = (String) getChangePwdUsername.invoke(func, uv.getUserName());
//                if (username.equals(uv.getUserName())) {
//                    return uv;
//                }
//                //自助用户的UserView
//                UserView newuv = new UserView(username, this.getFrameconn());
//                if (newuv.canLogin()) {
//                    Des des = new Des();
//                    String realPassword = newuv.getPassWord();
//                    if (ConstantParamter.isEncPwd(this.getFrameconn())) {
//                        realPassword = des.DecryPwdStr(realPassword.replaceAll("''", "'"));
//                    } else {
//                        realPassword = realPassword.replaceAll("''", "'");
//                    }
//
//                    //如果密码匹配上，说明是想改自助用户的密码。
//                    if (oldPassword.equals(realPassword)) {
//                        uv = newuv;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return uv;
//    }


    /**
     * 校验密码中是否含有逗号
     * @param name
     * @return
     * @throws GeneralException
     */
    public void validateUserNamePwdComma(String name) throws Exception
    {
        if(name==null)
            return;
        boolean bflag=false;
        String ctrlvalue="`";
        for(int i=0;i<name.length();i++)
        {
            char c=name.charAt(i);
            if(ctrlvalue.indexOf(c)!=-1)
            {
                bflag=true;
                break;
            }
        }
        if(bflag){
            throw new GeneralException(ResourceFactory.getProperty("error.password.validate.pwdcomma"));
        }
    }
}
