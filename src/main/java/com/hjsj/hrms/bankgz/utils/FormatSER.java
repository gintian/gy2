package com.hjsj.hrms.bankgz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FormatSER {
    /***
     *
     * @param glbSrvNo  全局流水号
     * @param state     交易状态 （0-成功；1-失败；2-异常；3-已冲正；9-处理中）
     * @return
     */
    public static String createFormat(String glbSrvNo, String state, String sv_cod) {
        JSONObject body = new JSONObject(true);
        JSONArray data = new JSONArray();
        HashMap<String, String> map = new HashMap<>();
        data.add(map);
        body.put("data", data);
        body.put("operate", "create");
        body.put("busiType", "ACP_GLBSVRDTL");
        map.put("BANK_NUM", "001");
        map.put("GLB_SER", glbSrvNo);
        map.put("GLBSER_SEQ", "");
        map.put("SV_SER", ""); //发起方请求流水
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        map.put("SYSA_DATE", date);
        map.put("RSPN_DATE", date);//会计日期
//        map.put("RECNCLTN_DATE", "");//对账日期；非必填
        map.put("PRCS_SYS_COD", "HRS");
        map.put("INTA_SYST_COD", "ACP");
        map.put("SV_TYP", "2");
        map.put("SV_COD", sv_cod);
//        map.put("SV_NAM", "");
//        map.put("CUST_NO", "");
//        map.put("ID_TYP", "");
//        map.put("ID_NO", "");
//        map.put("ID_NAM", "");
//        map.put("DR_ACCT", "");
//        map.put("CR_ACCT", "");
//        map.put("CR_ACCT_NAM", "");
//        map.put("TR_AMT", "");
//        map.put("CHRG_AMT1", "");
//        map.put("CHRG_AMT1_NAM", "");
//        map.put("CHRG_AMT2", "");
//        map.put("CHRG_AMT2_NAM", "");
//        map.put("ORG_CODE", "");
//        map.put("USER_CODE", "");
        map.put("TRAN_STS", state);
//        map.put("REC_SER", "");
//        map.put("REC_COD", "");
//        map.put("REC_MSG", "");
        map.put("STRT_TIME", DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()));
//        map.put("UPDATE_TIME", "");
        map.put("REVE_FLAG", "N");
//        map.put("RCRS_SER", "");
        return JSONObject.toJSONString(body);
    }

    /***
     *
     * @param glbSrvNo 全局流水号
     * @param state    交易状态 （0-成功；1-失败；2-异常；3-已冲正；9-处理中）
     * @return
     */
    public static String updateFormat(String glbSrvNo, String state, String recCode, String recMessage) {
        JSONObject body = new JSONObject(true);
        JSONObject data = new JSONObject();
        JSONObject whele = new JSONObject();
        whele.put("BANK_NUM", "001");
        whele.put("GLB_SER", glbSrvNo);
        whele.put("SV_SER", "");
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        whele.put("RSPN_DATE", date);//会计日期

//        data.put("CUST_NO", "");
//        data.put("ID_TYP", "");
//        data.put("ID_NO", "");
//        data.put("ID_NAM", "");
//        data.put("DR_ACCT", "");
//        data.put("DR_ACCT_NAM", "");
//        data.put("CR_ACCT", "");
//        data.put("CR_ACCT_NAM", "");
//        data.put("TR_AMT", "");
//        data.put("CHRG_AMT1", "");
//        data.put("CHRG_AMT1_NAM", "");
//        data.put("CHRG_AMT2", "");
//        data.put("CHRG_AMT2_NAM", "");
//        data.put("ORG_CODE", "");
//        data.put("USER_CODE", "");
        data.put("TRAN_STS", state);
//        data.put("REC_SER", "");
        data.put("REC_COD", recCode);
        data.put("REC_MSG", recMessage);
        data.put("STRT_TIME", DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()));
        // data.put("UPDATE_TIME", "");
        body.put("data", data);
        body.put("where", whele);
        return body.toJSONString();
    }


}
