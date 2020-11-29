package com.hjsj.hrms.service.core.http;

import com.google.common.collect.Maps;
import com.hjsj.hrms.module.system.numberrule.outrequest.ResultData;
import com.hjsj.hrms.module.system.numberrule.utils.JsonUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import kafka.admin.AdminUtils;
import kafka.utils.ZkUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.*;

public class HttpProbingService extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(HttpProbingService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("======================= begin out interface called ===============================================");
        long start = System.currentTimeMillis();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "application/json; charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        Pair<Boolean, String> param = getParam(request);
        String jsonParam = param.getRight();
        log.warn("request param: {}", jsonParam);
        String responseResult = "";
        if (param.getLeft()) {
            responseResult = doProbing(param.getRight());
        } else {
            ResultData resultData = new ResultData();
            resultData.setResponseCode(CODE_PARAM_ERROR);
            resultData.setResponseMessage(CODE_SUCCESS_MSG);
            responseResult = JsonUtil.toJSONString(resultData);
            log.error("{}，ErrorMessage: {}", responseResult, param.getRight());
        }
        response.getWriter().write(responseResult);
        log.warn("======================= end out interface called =====================[consume time is {} ms]=================", (System.currentTimeMillis() - start));
    }

    private Pair<Boolean, String> getParam(HttpServletRequest request) throws IOException {
        String result = "";
        boolean cannotValidate = false;
        String contentType = request.getContentType();
        HashMap<String, Object> param = Maps.newHashMap();
        if (contentType.toLowerCase().indexOf("application/json") > -1) {
            cannotValidate = true;
            result = IOUtils.toString(request.getInputStream(), "utf-8");
        } else if (contentType.toLowerCase().indexOf("application/x-www-form-urlencoded") > -1) {
            cannotValidate = true;
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (MapUtils.isNotEmpty(parameterMap)) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    param.put(entry.getKey(), entry.getValue()[0]);
                }
                result = JsonUtil.toJSONString(param);
            }
        } else {
            log.error("无法解析请求参数类型，contentType={}， result:{}", contentType, result);
            return Pair.of(cannotValidate, result);
        }

        return Pair.of(cannotValidate, result);
    }

    /**
     * @param jsonParam
     * @return
     */
    private String doProbing(String jsonParam) {
        ResultData resultData = new ResultData();
        String result = "";
        //探测数据库
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            if (conn == null) {
                resultData.setResponseCode("HRSPFR0001");
                resultData.setResponseData(new JSONObject());
                resultData.setResponseMessage("数据库连接异常");
                result = JsonUtil.toJSONString(resultData);
                return result;
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(conn);
        }
        //探测kafka
        boolean linkKafkaFlag = true;
        String bootstrapServers = SystemConfig.getPropertyValue("bootStrapServers");
        Socket socket = null;
        try {
            socket = new Socket();
            String hostname = bootstrapServers.split(":")[0];
            String port = bootstrapServers.split(":")[1];
            InetSocketAddress address = new InetSocketAddress(hostname, Integer.parseInt(port));
            socket.connect(address, 5000);

        } catch (Exception e) {
            linkKafkaFlag = false;
            log.error("doProbing:连接kafka出错!,desc:{},topic:{}", e);
        }finally {
            PubFunc.closeResource(socket);
        }
        if (!linkKafkaFlag) {
            resultData.setResponseCode("HRSPFR0002");
            resultData.setResponseData(new JSONObject());
            resultData.setResponseMessage("KAFKA连接异常");
            result = JsonUtil.toJSONString(resultData);
            return result;
        }
        if (StringUtils.isBlank(result)) {
            resultData.setResponseCode("HRSAAAAAAA");
            resultData.setResponseMessage("交易成功");
            resultData.setResponseData(new JSONObject());
            result = JsonUtil.toJSONString(resultData);
        }
        return result;
    }
}
