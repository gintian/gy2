/**
 * FileName: BankGzUtils
 * Author:   xuchangshun
 * Date:     2020/4/21 16:00
 * Description: 贵州银行通用功能类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.bankgz.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.hjadmin.cache.CacheUtil;
import com.hrms.hjsj.hjadmin.cache.FrameworkCacheKeysEnum;
import com.hrms.hjsj.hjadmin.util.JwtUtil;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 〈类功能描述〉<br>
 * 〈贵州银行通用功能类〉
 *
 * @Author xuchangshun
 * @Date 2020/4/21
 * @since 1.0.0
 */
public class BankGzUtils {
    private Logger log = LoggerFactory.getLogger(BankGzUtils.class);

    public Map<String, Object> authorByBank(String logonFiledValue) throws Exception {
        HashMap<String, Object> returnData = new HashMap<>();
        Connection conn = null;
        try {
            String logonprefix = SystemConfig.getPropertyValue("logonprefix");
            logonFiledValue = logonprefix + logonFiledValue;//纯数字登录前缀加U
            //根据传递过来的贵银信息获取，用户相关信息构建UserView
            Map<String, String> userInfoMap = getUserNameAndPassWordByOnlyField(logonFiledValue);
            log.info("统一门户认证用户userInfoMap：{}", userInfoMap);
            if (MapUtils.isEmpty(userInfoMap)) {
                //构建错误集成信息，让检查认证用户指标、认证用户库
                returnData.put("error", true);
                returnData.put("msg", "认证失败,用户不存在！");
            } else {
                String username = userInfoMap.get("username");
                String password = userInfoMap.get("password");
                //形成UserView,登录放入缓存中
                String accToken = JwtUtil.applyToken(username, System.currentTimeMillis(), JwtUtil.TOKENTYPE_ACCESS_TOKEN);
                conn = AdminDb.getConnection();
                log.info("统一门户认证用户conn：{},accToken{}", conn, accToken);
                UserView userView = new UserView(username, password, conn);
                log.info("统一门户认证用户userView：{}", userView);
                if (!userView.canLogin()) {
                    //构建错误集成信息，让检查认证用户指标、认证用户库
                    returnData.put("error", true);
                    returnData.put("msg", "认证失败,请检查登录指标与登录人员库！");
                } else {
                    CacheUtil.set(FrameworkCacheKeysEnum.userViewCache, username, userView);
                    Map<String, String> returnUserInfoMap = new HashMap<>();
                    returnUserInfoMap.put("tkaccount", accToken);
                    returnUserInfoMap.put("userName", userView.getUserFullName());
                    returnData.put("userInfo", returnUserInfoMap);
                    returnData.put("error", false);
                }
            }
        } catch (Exception e) {
            log.error("贵州银行-->自助服务认证用户失败！异常信息{}", e);
            throw e;
        } finally {
            PubFunc.closeDbObj(conn);
        }
        return returnData;
    }

    /**
     * 获取登录用户的信息值
     *
     * @param userNamevalue :传递过来的唯一性指标值
     * @return Map<String, String> 存放用户信息值
     * @Author xuchangshun
     * @Date 2020/4/20 11:26
     */
    private Map<String, String> getUserNameAndPassWordByOnlyField(String userNamevalue) throws SQLException, GeneralException {
        //empInfo登录用户的信息
        Map<String, String> empInfo = null;
        Connection connection = null;
        RowSet rs = null;
        try {
            connection = AdminDb.getConnection();
            //获取密码字段名
            String pwdField = getLogonPassWordField();
            //获取用户名字段名
            String loginField = getLogonUserNameField();
            //登陆认证人员库
            List<RecordVo> nbaselist = getAllLoginDbNameList(connection);
            if (CollectionUtils.isEmpty(nbaselist)) {
                //没有认证用户库返回null
                return null;
            }
            List<String> userList = new ArrayList<String>();
            ContentDAO dao = new ContentDAO(connection);
            //查询人员用户名和密码
            StringBuilder sqls = new StringBuilder();
            for (int i = 0; i < nbaselist.size(); i++) {
                RecordVo dbvo = nbaselist.get(i);
                sqls.append("select ");
                sqls.append(loginField);
                sqls.append(" username, ");
                sqls.append(pwdField);
                sqls.append(" password ");
                sqls.append(" from ");
                sqls.append(dbvo.getString("pre"));
                sqls.append("a01 where ");
                sqls.append(loginField);
                sqls.append(" =? ");
                userList.add(userNamevalue);
                if (i < nbaselist.size() - 1) {
                    sqls.append(" union all ");
                }
            }
            rs = dao.search(sqls.toString(), userList);

            String username = "";
            String pwd = "";
            if (!rs.next()) {
                return null;
            }
            username = rs.getString("username");
            pwd = rs.getString("password");
            if (StringUtils.isBlank(pwd)) {
                pwd = StringUtils.EMPTY;
            }
            empInfo = new HashMap<String, String>();
            empInfo.put("username", username);
            // 密码如果加密需要脱密
/*            if (ConstantParamter.isEncPwd()) {
                Des des = new Des();
                pwd = des.DecryPwdStr(pwd);
            }*/
            empInfo.put("password", pwd);
        } catch (Exception e) {
            log.error("贵州银行-->获取用户登录信息失败！异常信息{}", e);
            throw e;
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(connection);
        }
        return empInfo;
    }

    /**
     * 登记口令字段
     *
     * @return 登记口令字段
     */
    private String getLogonPassWordField() {
        String defaultUserPwdFld = "userpassword";

        RecordVo param_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if (param_vo == null || param_vo.getString("str_value").equals(""))
            return defaultUserPwdFld;

        String user_pwd = param_vo.getString("str_value");
        int idx = user_pwd.indexOf(",");
        if (idx == -1)
            return defaultUserPwdFld;

        String pwd = user_pwd.substring(idx + 1).trim();
        if (pwd.equals("#") || pwd.equals("")) {
            return defaultUserPwdFld;
        }
        //zxj 20150921 进一步判断密码指标是否存在
        FieldItem userpwdItem = DataDictionary.getFieldItem(pwd, "A01");
        if (null == userpwdItem || !"1".equals(userpwdItem.getUseflag()))
            return defaultUserPwdFld;

        return pwd;
    }

    /**
     * 取得全部登记用户库前缀列表，
     *
     * @return 全部等级用户库前缀列表
     */
    private List<RecordVo> getAllLoginDbNameList(Connection connection) {
        StringBuffer sql = new StringBuffer();
        RecordVo param_vo = ConstantParamter.getConstantVo("SS_LOGIN");
        if (param_vo == null || param_vo.getString("str_value").equals(""))
            return null;
        String str_dbpre = param_vo.getString("str_value").toUpperCase();
        sql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao = new ContentDAO(connection);
        RowSet recset = null;
        List<RecordVo> list = new ArrayList<RecordVo>();
        try {
            recset = dao.search(sql.toString());
            while (recset.next()) {
                String pre = recset.getString("pre").toUpperCase();
                if (!str_dbpre.contains(pre)) {
                    continue;
                }
                RecordVo vo = new RecordVo("dbname");
                vo.setInt("dbid", recset.getInt("dbid"));
                vo.setString("dbname", recset.getString("dbname"));
                vo.setString("pre", recset.getString("pre"));
                list.add(vo);
            }
        } catch (Exception ex) {
            log.error("贵州银行-->获取用户库前缀失败！异常信息{}", ex);
        } finally {
            PubFunc.closeDbObj(recset);
        }
        return list;
    }

    /**
     * 求登录用户名字段
     *
     * @return 登录用户名指标
     */
    public String getLogonUserNameField() {
        String defaultUserNameFld = "username";
        RecordVo param_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if (param_vo == null || param_vo.getString("str_value").equals(""))
            return defaultUserNameFld;

        String user_pwd = param_vo.getString("str_value");
        int idx = user_pwd.indexOf(",");
        if (idx == -1)
            return defaultUserNameFld;

        String username = user_pwd.substring(0, idx).trim();
        if (username.equals("#") || username.equals("")) {
            return defaultUserNameFld;
        }
        FieldItem usernameItem = DataDictionary.getFieldItem(username, "A01");
        if (null == usernameItem || !"1".equals(usernameItem.getUseflag())) {
            return defaultUserNameFld;
        }
        return username;
    }

    /**
     * 获取缓存中的userView
     * 如果不存在则返回空
     *
     * @param authorization :认证字符串
     * @return UserView
     * @Author xuchangshun
     * @Date 2020/4/22 9:17
     */
    public UserView getUserViewByCache(String authorization) {
        UserView userView = null;
        try {
            userView = (UserView) CacheUtil.get(FrameworkCacheKeysEnum.userViewCache, JwtUtil.parseJWT(authorization, JwtUtil.TOKENTYPE_ACCESS_TOKEN).getSubject());
        } catch (Exception e) {
            //异常一般是通过认证字符串获取Userview时,JWtUtil抛出异常token过期,贵银的处理方案为不处理异常，返回null的userview
        }
        return userView;
    }

    /**
     * 进行贵州银行token验证
     *
     * @param bankToken           :贵州银行的token
     * @param ip                  请求的ip地址
     * @param onlyLogonFieldValue 登录用户
     * @return boolean 验证是否成功
     * @Author duxl
     * @Date 2020/4/20 13:05
     */
    public String checkBankToken(String bankToken, String ip, String mac, String onlyLogonFieldValue) throws Exception {
        String result = "false";
        try {
            Date date = new Date();
            String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
            String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
            String sourceJnlNo = datetime.substring(0, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17);
            Map<String, String> systemHeaderMap = new HashMap<String, String>();
            systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
            systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
            systemHeaderMap.put("actionId", "ACP2007112");//交易码
            systemHeaderMap.put("actionVersion", "v1");
            systemHeaderMap.put("sourceJnNo", sourceJnlNo);
            systemHeaderMap.put("timestamp", timestamp);
            systemHeaderMap.put("ip", ip);
//            String mac = Tools.getMACAddress();
            systemHeaderMap.put("mac", mac);
            Map<String, String> bodyMap = new HashMap<String, String>();
            bodyMap.put("appId", "HRS");
            bodyMap.put("account", onlyLogonFieldValue);
            bodyMap.put("token", bankToken);
            Map<String, Map> requestDataMap = new HashMap<String, Map>();
            requestDataMap.put("systemHeader", systemHeaderMap);
            requestDataMap.put("body", bodyMap);
            Map<String, Map> dataMap = new HashMap<String, Map>();
            dataMap.put("requestData", requestDataMap);
            String uap_checktoken_url = SystemConfig.getPropertyValue("uap_checktoken_url");//请求接口地址
            String urlParams = JSONObject.fromObject(dataMap).toString();
            String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.ACPA_node.zak");
            log.info("统一认证校验token请求报文：" + urlParams + " 请求地址:" + uap_checktoken_url + "  macVal:" + macVal);
            String responseString = GzBankHttpUtil.sendParamToUrl(uap_checktoken_url, urlParams, macVal, "ACP2007112");
            log.info("统一认证校验token响应报文：" + responseString);
            JSONObject responseObject = JSONObject.fromObject(responseString);
            String responseMessage = responseObject.getString("responseMessage");
            String body = JSONObject.fromObject(responseObject.getString("responseData")).getString("body");
            String isTokenExpire = JSONObject.fromObject(body).getString("isTokenExpire");//token即将过期标志 当为true需重新调用统一认证刷新接口
            //验证统一认证token分三种情况:1、认证通过 2、认证失败 3、认证token即将过期
            if ("success".equals(responseMessage)) {
                result = "true";
                if ("true".equalsIgnoreCase(isTokenExpire)) {
                    result = "tokenExpire";
                }
            } else {
                result = "false";
                log.info("统一认证校验token失败：" + responseMessage);
            }
        } catch (Exception e) {
            log.error("贵州银行-->自助服务认证用户失败！异常信息{}", e);
            throw e;
        }
        return result;
    }
}
