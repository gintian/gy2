/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/6/1 上午10:42
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule.outrequest;

import com.google.common.collect.Lists;
import com.hjsj.hrms.module.system.numberrule.NumberRuleBean;
import com.hjsj.hrms.module.system.numberrule.NumberRuleBo;
import com.hjsj.hrms.module.system.numberrule.utils.JsonUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.*;
import static com.hjsj.hrms.module.system.numberrule.utils.NumberGenTool.MAX_COUNT_PER;

/**
 * function：外部接口请求接口
 * datetime：2020-06-01 10:36
 * author：warne
 */
public class OutInterfaceRequest {

    static Logger log = LoggerFactory.getLogger(OutInterfaceRequest.class);

    /**
     * jsonParam样例：
     * {
     * "systemCode": "JZP",
     * "systemName": "集中作业平台",
     * "mobile": "18024503120",
     * "count": 8,
     * "remark": "我来申请编号了哈，请帮忙处理",
     * "applicant": "张三"
     * }
     *
     * @param jsonParam
     * @return
     */
    public String doRequest(String jsonParam) {
        Triple<Boolean, String, NumberRuleBean> checkParam = preCheckParam(jsonParam);
        if (!checkParam.getLeft()) //# 参数校验没有通过
            return checkParam.getMiddle();

        Connection conn = null;
        ResultData resultData = new ResultData();
        String jsonResult = "";
        try {
            NumberRuleBean reqInfo = checkParam.getRight();
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            String checkExistSql = "SELECT COUNT(1) AS ISEXIST FROM DUAL WHERE EXISTS(SELECT 1 FROM NUMBER_RULE_INFO WHERE Upper(SYSTEMCODE)='" + reqInfo.getSystemCode().toUpperCase() + "' AND DELETEFLAG='Y')";
            log.info("检查系统[{}]是否已注册,checkExistSql: {}", reqInfo.getSystemCode(), checkExistSql);
            RowSet search = dao.search(checkExistSql);
            if (search.next()) {
                int isExist = search.getInt("ISEXIST");
                if (0 == isExist) {
                    //# 系统未注册时
                    resultData.setResponseCode(CODE_PARAM_NOT_REGISTER);
                    resultData.setResponseMessage(reqInfo.getSystemCode() + CODE_PARAM_NOT_REGISTER_MSG);
                    jsonResult = JsonUtil.toJSONString(resultData);
                    log.error("接口调用申请编号时: {}", jsonResult);

                    return jsonResult;
                }
            }

            NumberRuleBo bo = new NumberRuleBo();
            Pair<Boolean, String> addResult = bo.saveRequestNumberRule(reqInfo, dao);
            if (addResult.getLeft()) {
                String resultRight = addResult.getRight();
                String[] split = resultRight.split(",");
                List<String> numberList = Lists.newArrayList(split);
                JSONObject responseData = new JSONObject();
                JSONObject body = new JSONObject();
                body.put("numberList", numberList);
                body.put("applicant", reqInfo.getApplicant());
                body.put("count", reqInfo.getCount());
                body.put("respTime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));

                responseData.put("body", body);

                JSONObject jsonObject = JSONObject.fromObject(jsonParam);
                JSONObject systemHeader = jsonObject.getJSONObject("requestData").getJSONObject("systemHeader");
                responseData.put("systemHeader", systemHeader);
                resultData.setResponseData(responseData);

            } else {
                return addResult.getRight();
            }
        } catch (Exception e) {
            log.error("doRequest: 接口调用申请编号错误! ,desc:{}", e);
            resultData.setResponseCode(CODE_UNKNOWN_ERROR);
            resultData.setResponseMessage(CODE_UNKNOWN_ERROR_MSG + "," + e.getMessage());
        } finally {
            PubFunc.closeDbObj(conn);
        }

        return JsonUtil.toJSONString(resultData);
    }

    /**
     * 校验请求参数
     *
     * @param jsonParam
     * @return
     */
    private Triple<Boolean, String, NumberRuleBean> preCheckParam(String jsonParam) {
        ResultData resultData = new ResultData();
        NumberRuleBean info = null;
        String result = "";

        if (StringUtils.isBlank(jsonParam)) {
            String demo = "{\n" +
                    "  \"requestData\": {\n" +
                    "    \"systemHeader\": {\n" +
                    "      \"sourceSystemCode\": \"COP\",\n" +
                    "      \"sinkSystemCode\": \"HRS\",\n" +
                    "      \"actionVersion\": \"v1\",\n" +
                    "      \"actionId\": \"HRS6022002\",\n" +
                    "      \"sourceJnlNo\": \"COP00901202009141139590006\",\n" +
                    "      \"timestamp\": \"1139938059\",\n" +
                    "      \"ip\": \"172.31.210.158\"\n" +
                    "    },\n" +
                    "    \"body\": {\n" +
                    "      \"systemcode\": \"COP\",\n" +
                    "      \"systemname\": \"集中作业平台\",\n" +
                    "      \"mobile\": \"18024503120\",\n" +
                    "      \"count\": 10,\n" +
                    "      \"remark\": \"集中作业平台账号申请\",\n" +
                    "      \"applicant\": \"张三\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            resultData.setResponseCode(CODE_PARAM_IS_EMPTY);
            resultData.setResponseMessage(CODE_PARAM_IS_EMPTY_MSG + ",样例: " + demo);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}", result);

            return Triple.of(false, result, info);
        }

        JSONObject jsonObject = JSONObject.fromObject(jsonParam);
        JSONObject body = jsonObject.getJSONObject("requestData").getJSONObject("body");

        info = JsonUtil.parseObject(body.toString(), NumberRuleBean.class);
        String targetParam = JsonUtil.toJSONString(info);

        //# 暂不做要求
       /* String sysEtoken = (String) body.get("sysEtoken");//认证码
        if (StringUtils.isBlank(sysEtoken)) {
            resultData.setResponseCode(CODE_TOKEN_IS_EMPTY);
            resultData.setResponseMessage(CODE_TOKEN_CODE_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }*/

        String systemCode = info.getSystemCode();
        if (StringUtils.isBlank(systemCode)) {
            resultData.setResponseCode(CODE_SYSTEM_CODE_IS_EMPTY);
            resultData.setResponseMessage(CODE_SYSTEM_CODE_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        Integer count = info.getCount();
        if (count == null || count < 1 || count > MAX_COUNT_PER) {
            resultData.setResponseCode(CODE_COUNT_IS_ERROR);
            resultData.setResponseMessage(CODE_COUNT_IS_ERROR_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        String systemName = info.getSystemName();
        if (StringUtils.isBlank(systemName)) {
            resultData.setResponseCode(CODE_SYSTEM_NAME_IS_EMPTY);
            resultData.setResponseMessage(CODE_SYSTEM_NAME_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        String remark = info.getRemark();
        if (StringUtils.isBlank(remark)) {
            resultData.setResponseCode(CODE_REMARK_IS_EMPTY);
            resultData.setResponseMessage(CODE_REMARK_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        String applicant = info.getApplicant();
        if (StringUtils.isBlank(applicant)) {
            resultData.setResponseCode(CODE_APPLICANT_IS_EMPTY);
            resultData.setResponseMessage(CODE_APPLICANT_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        String mobile = info.getMobile();
        if (StringUtils.isBlank(mobile)) {
            resultData.setResponseCode(CODE_APPLICANT_MOBILE_IS_EMPTY);
            resultData.setResponseMessage(CODE_APPLICANT_MOBILE_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用申请编号时: {}, param:{}", result, targetParam);

            return Triple.of(false, result, info);
        }

        //# 校验通过
        return Triple.of(true, result, info);
    }
}

