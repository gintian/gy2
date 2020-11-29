/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           6/2/20, 8:48 AM
 *  *
 *
 */

package com.hjsj.hrms.servlet.numberrule;

import com.google.common.collect.Maps;
import com.hjsj.hrms.module.system.numberrule.outrequest.OutInterfaceRequest;
import com.hjsj.hrms.module.system.numberrule.outrequest.ResultData;
import com.hjsj.hrms.module.system.numberrule.utils.JsonUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.CODE_PARAM_ERROR;
import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.CODE_PARAM_ERROR_MSG;

/**
 * function：外部接口调用生成编号的servlet接口
 * datetime：2020-06-02 08:48
 * author：warne
 */
public class NumberRuleOfOutInterfaceServlet extends HttpServlet {
    static Logger log = LoggerFactory.getLogger(NumberRuleOfOutInterfaceServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("======================= begin out interface called ===============================================");
        long start = System.currentTimeMillis();
        String sw6 = request.getHeader("sw6");//调用链聚合信息
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "application/json; charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("sw6", sw6);
        Pair<Boolean, String> param = getParam(request);

        String jsonParam = param.getRight();
        log.warn("request param: {}", jsonParam);

        String responseResult = "";
        if (param.getLeft()) {
            OutInterfaceRequest outer = new OutInterfaceRequest();
            responseResult = outer.doRequest(param.getRight());

        } else {
            ResultData resultData = new ResultData();
            resultData.setResponseCode(CODE_PARAM_ERROR);
            resultData.setResponseMessage(CODE_PARAM_ERROR_MSG);
            responseResult = JsonUtil.toJSONString(resultData);
            log.error("{}，desc: {}", responseResult, param.getRight());
        }

        response.getWriter().write(responseResult);

        log.warn("======================= end out interface called =====================[consume time is {} ms]=================", (System.currentTimeMillis() - start));
    }

    /**
     * 获取参数
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Pair<Boolean, String> getParam(HttpServletRequest request) throws IOException {
        String result = "";
        boolean cannotValidate = true;
        String contentType = request.getContentType();
        Map<String, Object> param = Maps.newHashMap();

        if (contentType.toLowerCase().indexOf("application/json") > -1) {
            //# json格式
            cannotValidate = false;
            result = IOUtils.toString(request.getInputStream(), "utf-8");
        } else if (contentType.toLowerCase().indexOf("application/x-www-form-urlencoded") > -1) {
            //# 表单形式
            cannotValidate = false;
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (MapUtils.isNotEmpty(parameterMap)) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    param.put(entry.getKey(), entry.getValue()[0]);
                }
                result = JsonUtil.toJSONString(param);
            }
        }

        //# 类型无法匹配
        if (cannotValidate) {
            log.error("无法解析请求参数类型，contentType={}， result:{}", contentType, result);
            return Pair.of(false, result);
        }


        return Pair.of(true, result);
    }
}
