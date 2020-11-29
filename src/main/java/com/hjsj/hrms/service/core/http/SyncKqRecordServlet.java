package com.hjsj.hrms.service.core.http;
/**
 * function：外部接口调用同步考勤打卡数据的servlet接口
 * datetime：2020-10-16 13:09
 * author：wangchunyu
 */

import com.google.common.collect.Maps;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.module.system.numberrule.outrequest.ResultData;
import com.hjsj.hrms.module.system.numberrule.utils.JsonUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.*;

public class SyncKqRecordServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(SyncKqRecordServlet.class);

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
        Pair<Boolean, String> param = getParam(request);
        String jsonParam = param.getRight();
        log.warn("request param: {}", jsonParam);

        String responseResult = "";
        if (param.getLeft()) {
            responseResult = doRequest(param.getRight());
        } else {
            ResultData resultData = new ResultData();
            resultData.setResponseCode(CODE_PARAM_ERROR);
            resultData.setResponseMessage(CODE_PARAM_ERROR_MSG);
            responseResult = JsonUtil.toJSONString(resultData);
            log.error("{}，ErrorMessage: {}", responseResult, param.getRight());
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
    public String doRequest(String jsonParam) {
        Triple<Boolean, String, List<KqRecordBean>> checkParam = preCheckParam(jsonParam);
        if (!checkParam.getLeft()) {//# 参数校验没有通过
            return checkParam.getMiddle();
        }
        ResultData resultData = new ResultData();
        List<KqRecordBean> kqList = checkParam.getRight();
        Pair<Boolean, String> addResult = saveRequestKqRecord(kqList);
        if (addResult.getLeft()) {
            JSONObject responseData = new JSONObject();
            JSONObject jsonObject = JSONObject.fromObject(jsonParam);
            JSONObject systemHeader = jsonObject.getJSONObject("requestData").getJSONObject("systemHeader");
            responseData.put("systemHeader", systemHeader);
            resultData.setResponseCode(CODE_SUCCESS);
            resultData.setResponseMessage(CODE_SUC_MSG);
            resultData.setResponseData(responseData);
        } else {
            return addResult.getRight();
        }
        return JsonUtil.toJSONString(resultData);
    }

    /**
     * 校验请求参数
     *
     * @param jsonParam
     * @return
     */
    private Triple<Boolean, String, List<KqRecordBean>> preCheckParam(String jsonParam) {
        ResultData resultData = new ResultData();
        String result = "";
        JSONObject jsonObject = JSONObject.fromObject(jsonParam);
        JSONArray body = jsonObject.getJSONObject("requestData").getJSONArray("body");
        if (body.isEmpty()) {
            resultData.setResponseCode(CODE_PARAM_IS_EMPTY);
            resultData.setResponseMessage(CODE_PARAM_IS_EMPTY_MSG);
            result = JsonUtil.toJSONString(resultData);
            log.error("接口调用考勤数据同步时: {}", result);
            return Triple.of(false, result, null);
        }
        List<KqRecordBean> kqDataList = new ArrayList();
        for (int i = 0; i < body.size(); i++) {
            JSONObject obj = (JSONObject) body.get(i);
            KqRecordBean info = JsonUtil.parseObject(obj.toString(), KqRecordBean.class);
            String empe_id = info.getEmpe_id();
            String work_date = info.getWork_date();
            String work_time = info.getWork_time();
            if (StringUtils.isNotBlank(empe_id) && StringUtils.isNotBlank(work_date) && StringUtils.isNotBlank(work_time)) {
                kqDataList.add(info);
            }
        }
        return Triple.of(true, result, kqDataList);
    }

    private Pair<Boolean, String> saveRequestKqRecord(List<KqRecordBean> kqRecordList) {
        String saveResult = "true";
        ResultData resultData = new ResultData();
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            UserView userView = new UserView("su", conn);
            userView.canLogin(false);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, userView);
            //库集合
            ArrayList preList = kqUtilsClass.getKqPreList();
            if (preList.size() <= 0) {
                log.error("系统中未设置考勤人员库，默认同步在职人员库！");
                preList.add("Usr");
            }
            //获取考勤卡号指标方法：
            KqParameter para = new KqParameter(userView, conn);
            HashMap hashmap = para.getKqParamterMap();
            //卡号指标
            String cardnoField = (String) hashmap.get("cardno");
            if (StringUtils.isEmpty(cardnoField)) {
                resultData.setResponseCode(CODE_KQCARD_ERROR);
                resultData.setResponseMessage(CODE_KQCARD_NO_MSG);
                saveResult = JsonUtil.toJSONString(resultData);
                return Pair.of(false, saveResult);
            }
            StringBuffer kqsql = new StringBuffer();
            kqsql.append(" select A0100 from kq_originality_data ");
            kqsql.append(" where nbase=? ");
            kqsql.append(" and A0100=? ");
            kqsql.append(" and work_date=? ");
            kqsql.append(" and work_time=? ");
            /*刷卡表添加记录sql*/
            StringBuffer kqsqlInto = new StringBuffer();
            kqsqlInto.append(" insert into kq_originality_data  ");
            kqsqlInto.append(" (nbase,A0100,work_date,work_time,card_no,A0101,B0110,E0122,E01A1,location,sp_flag,inout_flag,datafrom,iscommon) ");
            kqsqlInto.append(" values ");
            kqsqlInto.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            /*循环kqRecordList集合并加入刷卡表*/
            int num = 0;
            int kqnum = 0;
            for (int i = 0; i < kqRecordList.size(); i++) {
                KqRecordBean kqRecordBean = kqRecordList.get(i);
                ArrayList kqInfolist = getKqInfolist(dao, preList, kqRecordBean, cardnoField, "A0144");
                if (kqInfolist.size() == 0) {
                    continue;
                }
                num += kqInfolist.size();
                kqnum += insertDataMain(dao, kqInfolist, kqsql.toString(), kqsqlInto.toString());
            }
            log.info("获取有效打卡数据{}条记录，未找到对应系统人员信息的刷卡数据有{}条记录，系统已存在的刷卡数据有{}条记录。", num, (kqRecordList.size() - num), (num - kqnum));
            log.info("成功同步有效打卡数据{}条记录。", kqnum);
        } catch (Exception e) {
            log.error("saveRequestKqRecord:同步考勤打卡数据出错!,ErrorMessage:{}", e.getMessage());
        } finally {
            PubFunc.closeResource(conn);
        }
        return Pair.of(true, saveResult);
    }

    /**
     * 获取人员基本信息 A0100,A0101,B0110,E0122,E01A1...
     *
     * @param dao
     * @param preList      考勤库集合
     * @param kqrecordbean
     * @param userIdindex  userID对应指标
     * @return
     * @paramcardnoField 考勤卡号对应指标
     */
    private ArrayList getKqInfolist(ContentDAO dao, ArrayList preList, KqRecordBean kqrecordbean, String cardnoField, String userIdindex) throws JobExecutionException {
        StringBuffer sql = new StringBuffer();
        ArrayList kqInfolist = new ArrayList();
        for (int j = 0; j < preList.size(); j++) {
            String pre = (String) preList.get(j);
            if (j > 0) {
                sql.append(" union all ");
            }
            sql.append("SELECT A0100,A0101,B0110,E0122,E01A1,").append(cardnoField).append(" as cardno,").append(userIdindex).append(" as userId,'" + pre + "' as nbase");
            sql.append(" FROM ").append(pre).append("A01");
            sql.append(" where ").append(userIdindex).append(" in (").append(kqrecordbean.getEmpe_id()).append(") ");

        }
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {

                String A0100 = rs.getString("A0100");
                String nbase = rs.getString("nbase");
                String userId = rs.getString("userId");
                String A0101 = rs.getString("A0101");
                A0101 = StringUtils.isEmpty(A0101) ? "" : A0101;
                String B0110 = rs.getString("B0110");
                B0110 = StringUtils.isEmpty(B0110) ? "" : B0110;
                String E0122 = rs.getString("E0122");
                E0122 = StringUtils.isEmpty(E0122) ? "" : E0122;
                String E01A1 = rs.getString("E01A1");
                E01A1 = StringUtils.isEmpty(E01A1) ? "" : E01A1;
                String cardno = rs.getString("cardno");
                cardno = StringUtils.isEmpty(cardno) ? " " : cardno;

                LazyDynaBean ldb = new LazyDynaBean();
                ldb.set("A0100", A0100);
                ldb.set("A0101", A0101);
                ldb.set("B0110", B0110);
                ldb.set("E0122", E0122);
                ldb.set("E01A1", E01A1);
                ldb.set("userId", userId);
                ldb.set("nbase", nbase);
                ldb.set("cardno", cardno);
                ldb.set("work_date", kqrecordbean.getWork_date());
                ldb.set("work_time", kqrecordbean.getWork_time());
                ldb.set("location", kqrecordbean.getLocation());
                ldb.set("exceptionType", kqrecordbean.getIscommon());
                ldb.set("inout_flag", kqrecordbean.getInout_flag());
                kqInfolist.add(ldb);
            }
        } catch (Exception e) {
            log.error("getKqInfolist:同步考勤打卡数据出错!,ErrorMessage:{},sql:{}", e.getMessage(), sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return kqInfolist;
    }

    /**
     * 拼装考勤数据，并往考勤表增加记录
     *
     * @param dao
     * @param kqInfolist
     * @param kqsql
     * @param kqsqlInto
     */
    private int insertDataMain(ContentDAO dao, ArrayList kqInfolist, String kqsql, String kqsqlInto) throws JobExecutionException {

        RowSet kqrs = null;
        int num = 0;
        try {
            for (int i = kqInfolist.size() - 1; i >= 0; i--) {
                LazyDynaBean ldb = (LazyDynaBean) kqInfolist.get(i);
                String nbase = (String) ldb.get("nbase");
                String A0100 = (String) ldb.get("A0100");
                String work_date = (String) ldb.get("work_date");
                String work_time = (String) ldb.get("work_time");
                String iscommon = (String) ldb.get("exceptionType");
                String inout_flag = (String) ldb.get("inout_flag");
                if (StringUtils.isEmpty(nbase) || StringUtils.isEmpty(A0100)) {
                    kqInfolist.remove(i);
                    continue;
                }
                ArrayList kqWherelist = new ArrayList();
                kqWherelist.add(nbase);
                kqWherelist.add(A0100);
                kqWherelist.add(work_date);
                kqWherelist.add(work_time);

                kqrs = dao.search(kqsql, kqWherelist);
                if (kqrs.next()) {
                    continue;
                }

                String A0101 = (String) ldb.get("A0101");
                A0101 = "".equalsIgnoreCase(A0101) ? null : A0101;
                String B0110 = (String) ldb.get("B0110");
                B0110 = "".equalsIgnoreCase(B0110) ? null : B0110;
                String E0122 = (String) ldb.get("E0122");
                E0122 = "".equalsIgnoreCase(E0122) ? null : E0122;
                String E01A1 = (String) ldb.get("E01A1");
                E01A1 = "".equalsIgnoreCase(E01A1) ? null : E01A1;

                ArrayList kqIntolist = new ArrayList();
                kqIntolist.add(nbase);
                kqIntolist.add(A0100);
                kqIntolist.add(work_date);
                kqIntolist.add(work_time);
                kqIntolist.add(ldb.get("cardno"));
                kqIntolist.add(A0101);
                kqIntolist.add(B0110);
                kqIntolist.add(E0122);
                kqIntolist.add(E01A1);
                kqIntolist.add(ldb.get("location"));
                kqIntolist.add("03");
                kqIntolist.add(inout_flag);
                kqIntolist.add(0);
                kqIntolist.add(iscommon);
                num += dao.insert(kqsqlInto, kqIntolist);
            }
        } catch (Exception e) {
            log.error("insertDataMain:同步考勤打卡数据出错!,ErrorMessage:{}", e.getMessage());
        } finally {
            PubFunc.closeDbObj(kqrs);
        }
        return num;
    }
}
