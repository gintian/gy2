package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.businessobject.sys.cmpp.MsgUtils;
import com.hjsj.hrms.service.syncdata.FieldRefBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * function：核心系统调用获取组织信息的接口
 * datetime：2020-06-20
 * author：wangcy
 */
public class HttpOrgService extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(HttpOrgService.class);

    public HttpOrgService() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonStr = this.returnJsonStr("0", "请使用Post请求");
        this.returnResponse(response, jsonStr, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("======================= begin out interface called ===============================================");
        long start = System.currentTimeMillis();
        String readerStr = "";//请求报文体
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            readerStr = readerStr.concat(line);
        }
        log.info("request param: {}", readerStr);
        String sw6 = request.getHeader("sw6");//调用链聚合信息
        JSONObject jsonObject = JSONObject.fromObject(readerStr);
        JSONObject body = jsonObject.getJSONObject("requestData").getJSONObject("body");
        String corcode = (String) body.get("corcode");//机构编码
        String sysEtoken = (String) body.get("sysEtoken");//认证码
        String systemCode = (String) body.get("systemCode");//请求系统编码

        String result = this.getOrgInfo(corcode, sysEtoken, systemCode);

        this.returnResponse(response, result, sw6);
        log.warn("======================= end out interface called =====================[consume time is {} ms]=================", (System.currentTimeMillis() - start));
    }

    /**
     * 获取机构信息数据
     *
     * @param corcode    机构编码
     * @param sysEtoken  认证码
     * @param systemCode 系统编码
     * @return
     */
    private String getOrgInfo(String corcode, String sysEtoken, String systemCode) {
        String result = "";
        Connection conn = null;
        try {
            if (StringUtils.isBlank(corcode)) {
                log.error("必填字段机构编码corcode未填写!");
                return this.returnJsonStr("0", "必填字段机构编码corcode未填写!");
            }
            /*if (StringUtils.isBlank(sysEtoken)) {
                log.error("必填字段认证码sysEtoken未填写!");
                return this.returnJsonStr("0", "必填字段认证码sysEtoken未填写!");
            }*/
            if (StringUtils.isBlank(systemCode)) {
                log.error("必填字段系统标识systemCode未填写!");
                return this.returnJsonStr("0", "必填字段系统标识systemCode未填写!");
            }
            String errorMsg = ValidateSysEtoken(systemCode, sysEtoken);
            if (!"".equals(errorMsg)) {
                return this.returnJsonStr("0", errorMsg);
            }
            conn = AdminDb.getConnection();

            ArrayList<FieldRefBean> fieldRefBeans = parserXml("KAFKA.xml");

            result = getOrgSyncData(fieldRefBeans, corcode);

        } catch (Exception e) {
            log.error("getOrgInfo:获取组织数据出错!,desc:{}", e);
            e.printStackTrace();
            return this.returnJsonStr("0", "获取机构信息出错");
        } finally {
            PubFunc.closeDbObj(conn);
        }
        return this.returnJsonStr("1", result);
    }

    /**
     * 校验ETOKEN是否正确
     *
     * @param systemCode
     * @param etoken
     */
    private String ValidateSysEtoken(String systemCode, String etoken) {
        String retStr = "";
        String valid = "";
        String sysetoken = "";
        String sql = "SELECT sysetoken,valid from t_sys_reg_services where sysCode = ?";
        Connection conn = null;
        RowSet rs = null;
        List<String> valList = new ArrayList<String>();
        valList.add(systemCode);
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql, valList);
            if (rs.next()) {
                valid = rs.getString("valid") != null ? rs.getString("valid") : "";
                sysetoken = rs.getString("sysetoken") != null ? rs.getString("sysetoken") : "";
                if ("2".equals(valid)) {
                    log.error("HRS系统未启用获取组织信息接口!");
                    retStr = "HRS系统未启用获取组织信息接口!";
                    return retStr;
                }
                /*if (!sysetoken.equalsIgnoreCase(etoken)) {
                    log.error("认证码错误!");
                    retStr = "认证码错误!";
                    return retStr;
                }*/
            } else {
                log.error("HRS系统未注册调用方系统，请联系管理员进行配置!");
                retStr = "HRS系统未注册调用方系统，请联系管理员进行配置!";
                return retStr;
            }
        } catch (Exception e) {
            log.error("ValidateSysEtoken:执行验证etoken出错!,param:{},desc:{}", systemCode, e);
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(conn);
        }
        return retStr;
    }

    /**
     * 解析xml 转为java对象
     *
     * @param fileName
     */
    public ArrayList<FieldRefBean> parserXml(String fileName) throws Exception {
        ArrayList<FieldRefBean> empFieldRefList = new ArrayList<>();
        File file = getFile(fileName);
        if (file == null) {
            log.error("未找到 {} 文件", fileName);
            throw GeneralExceptionHandler.Handle(new Exception("未找到" + fileName));
        }
        SAXBuilder saxbuilder = new SAXBuilder();
        Document document = saxbuilder.build(file);

        String path = "/sync/fields_ref/orgfield/field_ref";
        XPath xpath = XPath.newInstance(path);
        List paramslist = xpath.selectNodes(document);
        Iterator it = paramslist.iterator();
        while (it.hasNext()) {
            Element element = (Element) it.next();
            FieldRefBean fieldrefbean = new FieldRefBean();
            fieldrefbean.setHrField(element.getAttributeValue("hrfield"));
            fieldrefbean.setDestField(element.getAttributeValue("destfield"));
            fieldrefbean.setFlddesc(element.getAttributeValue("desc"));
            empFieldRefList.add(fieldrefbean);
        }

        return empFieldRefList;
    }

    /**
     * 获得待同步的机构数据
     *
     * @return orgDataList
     * @Title: getOrgSyncData
     * @Description:
     */
    public String getOrgSyncData(ArrayList<FieldRefBean> orgFieldRefList, String corcode) {
        //系统指标
        StringBuilder columns = new StringBuilder();
        for (int i = 0; i < orgFieldRefList.size(); i++) {
            FieldRefBean fildreBean = orgFieldRefList.get(i);
            String destfield = fildreBean.getDestField();
            String hrfield = fildreBean.getHrField();
            columns.append(hrfield + " " + destfield + ",");
        }
        if (columns.toString().endsWith(",")) {
            columns.setLength(columns.length() - 1);
        }
        String sql = "select " + columns + " from t_org_view where corcode = '" + corcode + "' and KAFKA in (0)";
        log.info("获取组织信息 sql: {}", sql);
        List rs = ExecuteSQL.executeMyQuery(sql);
        if (rs.size() == 0) {
            JSONObject obj = new JSONObject();
            obj.put("existFlag", "0");
            obj.put("Msg", corcode + " 组织不存在!");
            return obj.toString();
        } else {
            JSONObject obj = JSONObject.fromObject(rs.get(0));
            obj.put("existFlag", "1");
            return obj.toString();
        }

    }

    /**
     * 返回json格式的字符串
     *
     * @param opt 选项值 0:失败,返回错误信息,1:成功,返回数据信息,2:成功(请求成功,但没有数据信息返回)
     * @param msg 返回的信息
     */
    private String returnJsonStr(String opt, Object msg) {
        JSONObject json = new JSONObject();
        if ("0".equals(opt)) {
            json.put("responseCode", "HRSDDR0001");
            json.put("responseMessage", msg);
            json.put("responseDetail", msg);

        } else if ("1".equals(opt)) {
            json.put("responseCode", "000000");
            json.put("responseMessage", "交易成功");
            json.put("responseDetail", "交易成功");

            JSONObject responsedata = new JSONObject();

            JSONObject systemheader = new JSONObject();
            systemheader.put("sourceSystemCode", "hrs");
            systemheader.put("sinkSystemCode", "cbs");
            systemheader.put("actionVersion", "v1");
            String timestamp = MsgUtils.getTimestamp();
            systemheader.put("sinkJnlNo", timestamp);
            DateFormat format = new SimpleDateFormat("MMddhhmmss");
            String currentTimestamp = format.format(new Date());
            systemheader.put("timestamp", currentTimestamp);
            systemheader.put("ip", "172.0.0.1");

            responsedata.put("systemHeader", systemheader);
            responsedata.put("body", msg);
            json.put("responseData", responsedata);
        }
        return json.toString();
    }

    /**
     * 返回json格式的数据
     *
     * @param response 响应对象
     * @param msg
     */
    private void returnResponse(HttpServletResponse response, String msg, String sw6) {
        //设置编码格式
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("sw6", sw6);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 根据文件名获取文件，没有则返回null
     *
     * @param filename 文件名
     * @return File
     * @Title: getFile
     * @Description:
     */
    public File getFile(String filename) {

        String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        File file = new File(path, filename);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }
}
