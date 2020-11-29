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

import net.sf.json.JSONObject;

import java.io.Serializable;

/**
 * function：交互数据模型
 * datetime：2020-06-01 10:42
 * author：warne
 */
public class ResultData implements Serializable {
    private String responseCode = IOutRequest.CODE_SUCCESS;
    private String responseMessage = IOutRequest.CODE_SUCCESS_MSG;
    private JSONObject responseData = null;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseDetail() {
        return responseMessage;
    }

    public void setResponseDetail(String responseDetail) {
        this.responseMessage = responseDetail;
    }

    public JSONObject getResponseData() {
        return responseData;
    }

    public void setResponseData(JSONObject responseData) {
        this.responseData = responseData;
    }
}
