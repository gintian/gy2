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

import com.hjsj.hrms.module.system.numberrule.utils.NumberGenTool;

/**
 * function：description
 * datetime：2020-06-01 10:41
 * author：warne
 */
public interface IOutRequest {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String DATE_FORMAT_SQL = "yyyy-MM-dd HH24:MI:ss";
    String TEXT_ALIGN = "center";
    String DEFAULT_SYSTEM_NAME = "人力资源系统";
    String DEFAULT_SYSTEM_CODE = "HRS";
    String DEFAULT_NUMBER_FIRST = "A0000";
    String DEFAULT_ACTION_USERNAME = "outer"; //# 接口调用时，外部用户

    Integer ONE_SYSTEM_MAX_NUMBER_COUNT = 500; //# 单个系统最多编号个数

    //======================================================================

    String CODE_SUCCESS = "HRSAAAAAAA";
    String CODE_SUCCESS_MSG = "SUCCESS";

    String CODE_PARAM_IS_EMPTY = "4001";
    String CODE_PARAM_IS_EMPTY_MSG = "请求参数为空";

    String CODE_PARAM_NOT_FORMAT = "4002";
    String CODE_PARAM_NOT_FORMAT_MSG = "请求参不规范";

    String CODE_PARAM_NOT_REGISTER = "4003";
    String CODE_PARAM_NOT_REGISTER_MSG = "系统未注册";

    String CODE_SYSTEM_CODE_IS_EMPTY = "4004";
    String CODE_SYSTEM_CODE_IS_EMPTY_MSG = "系统简称(英文)不能为空";

    String CODE_SYSTEM_NAME_IS_EMPTY = "4005";
    String CODE_SYSTEM_NAME_IS_EMPTY_MSG = "系统名称(中文)不能为空";

    String CODE_APPLICANT_IS_EMPTY = "4006";
    String CODE_APPLICANT_IS_EMPTY_MSG = "申请者不能为空";

    String CODE_APPLICANT_MOBILE_IS_EMPTY = "4007";
    String CODE_APPLICANT_MOBILE_IS_EMPTY_MSG = "申请者手机号不能为空";

    String CODE_COUNT_IS_ERROR = "4008";
    String CODE_COUNT_IS_ERROR_MSG = "申请编号个数范围[ 1~" + NumberGenTool.MAX_COUNT_PER + " ]";

    String CODE_REMARK_IS_EMPTY = "4009";
    String CODE_REMARK_IS_EMPTY_MSG = "申请说明不能为空";

    String CODE_UNKNOWN_ERROR = "4010"; //# 未知错误
    String CODE_UNKNOWN_ERROR_MSG = "未知错误，请联系管理员"; //#

    String CODE_MAX_COUNT_LIMIT = "4011"; //# 单个系统编号个数达到上限

    String CODE_PARAM_ERROR = "4012"; //#
    String CODE_PARAM_ERROR_MSG = "参数解析错误"; //#

    String CODE_TOKEN_IS_EMPTY = "4013";
    String CODE_TOKEN_CODE_IS_EMPTY_MSG = "调用token为空";

    String CODE_KQCARD_ERROR = "4014";
    String CODE_KQCARD_NO_MSG = "主集中未设置考勤卡号指标";
    String CODE_SUC_MSG = "成功";
}
