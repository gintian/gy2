/**
 * FileName: RestFulServlet
 * Author:   xuchangshun
 * Date:     2020/2/13 10:47
 * Description: RestFul请求使用的Servlet
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.bankgz.servlet;

import com.hjsj.hrms.bankgz.utils.BankGzUtils;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.hjadmin.api.ResponseCodeEnum;
import com.hrms.hjsj.hjadmin.api.ResponseFactory;
import com.hrms.hjsj.hjadmin.api.RetResult;
import com.hrms.hjsj.hjadmin.util.JwtUtil;
import com.hrms.struts.command.FrameCmd;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.TransInfoView;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈类功能描述〉<br>
 * 〈RestFul请求使用的Servlet〉
 *
 * @Author xuchangshun
 * @Date 2020/2/13
 * @since 1.0.0
 */
public class BgzAjaxTokenServlet extends HttpServlet {
    // 日志文件
    private Logger log = LoggerFactory.getLogger(BgzAjaxTokenServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String authorization = req.getHeader(JwtUtil.DEFAULT_JWT_PARAM);
            BankGzUtils bankGzUtils = new BankGzUtils();
            String onlyLogonFieldValue = req.getHeader("logonFiledValue");
            String bankToken = req.getHeader("bankToken");
            String ip = req.getHeader("ip");
            String mac = req.getHeader("mac");
            log.info("BgzAjaxTokenServlet:authorization:{},onlyLogonFieldValue:{},bankToken:{},ip:{},mac:{}", authorization, onlyLogonFieldValue, bankToken, ip, mac);
            //如果authorization不存在，则需要构建UserView放到缓存中去，否则的话从缓存中获取
            if (StringUtils.isBlank(authorization)) {
                //使用贵银传递过来的登录信息进行认证,此时应该只是要进行认证而已
                Map<String, Object> authorResult = bankGzUtils.authorByBank(onlyLogonFieldValue);
                boolean error = (boolean) authorResult.get("error");
                if (error) {
                    //如果出现错误信息,则向前台抛出错误结果，错误由前端处理展现
                    String msg = (String) authorResult.get("msg");
                    RetResult retResult = new RetResult(msg, ResponseCodeEnum.businessException, null);
                    ResponseFactory.buildResponseSuccess(resp, retResult);
                } else {
                    HashMap<String, String> returnUserInfoMap = (HashMap<String, String>) authorResult.get("userInfo");
                    ResponseFactory.buildResponseSuccess(resp, new RetResult("", ResponseCodeEnum.ok, returnUserInfoMap));
                }
                return;
            }
            //向下进行是为了进行执行交易类
            //缓存中存在userView则使用缓存中的,如果缓存中不存在,则进行判定生成
            UserView userView = bankGzUtils.getUserViewByCache(authorization);
            String isSuccess = "";
            if (userView == null) {
                isSuccess = bankGzUtils.checkBankToken(bankToken, ip, mac, onlyLogonFieldValue);
                log.info("isSuccess:{}", isSuccess);
                if ("true".equals(isSuccess) || "tokenExpire".equals(isSuccess)) {
                    Map<String, Object> authorResult = bankGzUtils.authorByBank(onlyLogonFieldValue);
                    boolean error = (boolean) authorResult.get("error");
                    if (error) {
                        //如果出现错误信息,则向前台抛出错误结果，错误由前端处理展现
                        String msg = (String) authorResult.get("msg");
                        RetResult retResult = new RetResult(msg, ResponseCodeEnum.businessException, null);
                        ResponseFactory.buildResponseSuccess(resp, retResult);
                        return;
                    } else {
                        Map<String, String> returnUserInfoMap = (Map<String, String>) authorResult.get("userInfo");
                        authorization = returnUserInfoMap.get("tkaccount");
                        //刚生成的是肯定有的
                        userView = bankGzUtils.getUserViewByCache(authorization);
                    }
                } else {
                    //银行提供的token失效啦才抛出身份认证失败的错误,其余的都由业务逻辑错误来处理吧
                    ResponseFactory.buildResponseAuthenticationError(resp);
                    return;
                }
            }
            String xml = "";
            String readerStr = IOUtils.toString(req.getInputStream(), "utf-8");
            log.info("request param: {}", readerStr);
            if (StringUtils.isNotEmpty(readerStr)) {//从流里取
                readerStr = URLDecoder.decode(readerStr, "UTF-8");
                JSONObject jsonObject = JSONObject.fromObject(readerStr);
                xml = jsonObject.getJSONObject("requestData").getJSONObject("body").getString("paramData");
            } else {
                String parameter = req.getParameterMap().toString().substring(1);
                JSONObject jsonObject = JSONObject.fromObject(parameter);
                xml = jsonObject.getJSONObject("requestData").getJSONObject("body").getString("paramData");
            }
            //只有需要执行交易类才需要解析参数信息,才会传递参数信息
            xml = SafeCode.keyWord_reback(xml);
            log.info("统一门户请求交易信息xml:{}", xml);
            JSONObject jsonObject = JSONObject.fromObject(xml);
            Iterator iterator = jsonObject.keys();
            HashMap<String, Object> returnData = new HashMap<>();
            HashMap<String, Object> dataMap = new HashMap<String, Object>();
            while (iterator.hasNext()) {
                String paramKey = String.valueOf(iterator.next());
                dataMap.put(paramKey, jsonObject.get(paramKey));
            }
            returnData = this.execute(req, dataMap, userView);
            log.debug("统一门户返回交易信息returnData:{}", returnData);
            if ("tokenExpire".equals(isSuccess)) {
                ResponseFactory.buildResponseSuccess(resp, new RetResult("tokenExpire", ResponseCodeEnum.ok, returnData));//token即将过期
            } else {
                ResponseFactory.buildResponseSuccess(resp, new RetResult("", ResponseCodeEnum.ok, returnData));
            }
        } catch (Exception e) {
            log.error("贵州银行--->请求交易失败,错误信息为ErrorMessage:{}", e);
            try {
                if (e instanceof GeneralException) {
                    GeneralException generalException = (GeneralException) e;
                    RetResult retResult = new RetResult(generalException.getErrorDescription(), ResponseCodeEnum.businessException, null);
                    ResponseFactory.buildResponseSuccess(resp, retResult);
                } else {
                    RetResult retResult = new RetResult(e.toString(), ResponseCodeEnum.runtimeException, null);
                    ResponseFactory.buildResponseSuccess(resp, retResult);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private HashMap execute(HttpServletRequest request, HashMap<String, Object> hm, UserView userView) throws Exception {
        String functionId = (String) hm.get("functionId");
        if (StringUtils.isBlank(functionId)) {
            throw new Exception("no functionId!");
        } else {
            TransInfoView transInfoView = new TransInfoView();
            transInfoView.setFormHM(hm);
            transInfoView.setUserView(userView);
            transInfoView.setRemoteAddr(JwtUtil.getIpAddr(request));
            transInfoView.setRequestId("");
            transInfoView.setWorkFlowId(functionId);
            FrameCmd frameCmd = new FrameCmd();
            frameCmd.setTransInfoView(transInfoView);
            frameCmd.execute();
            hm = frameCmd.getTransInfoView().getFormHM();
            return hm;
        }
    }

}
