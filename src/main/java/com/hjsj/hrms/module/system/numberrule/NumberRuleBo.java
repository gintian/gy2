/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/5/20 下午3:07
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule;

import com.google.common.collect.Lists;
import com.hjsj.hrms.module.system.numberrule.outrequest.ResultData;
import com.hjsj.hrms.module.system.numberrule.utils.JsonUtil;
import com.hjsj.hrms.module.system.numberrule.utils.NoOverException;
import com.hjsj.hrms.module.system.numberrule.utils.NumberGenTool;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hjsj.hrms.module.system.numberrule.outrequest.IOutRequest.*;
import static com.hjsj.hrms.module.system.numberrule.utils.NumberGenTool.MAX_COUNT_PER;


public class NumberRuleBo {

    private Logger log = LoggerFactory.getLogger(NumberRuleBo.class);

    public String loadNumberRuleList(UserView userView, Connection frameconn) {
        return loadNumberRuleList(userView, frameconn, Lists.newArrayList());
    }

    /**
     * 加载数据列表
     *
     * @return
     */
    public String loadNumberRuleList(UserView userView, Connection frameconn, ArrayList<String> inputValues) {
        ArrayList<ColumnsInfo> columns = Lists.newArrayList();
        // 创建列对象
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("ID");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnWidth(120);
        //columnsInfo.setColumnDesc("#ID");
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("applicant");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("申请人");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("mobile");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("手机号");
        columnsInfo.setColumnWidth(100);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("count");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("申请个数");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("SEGMENT");
        columnsInfo.setColumnType("A");
        columnsInfo.setSortable(false);
        columnsInfo.setColumnDesc("编号区间");
        columnsInfo.setColumnWidth(100);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("CREATETIME");
        columnsInfo.setColumnType("D");
        columnsInfo.setColumnDesc("申请时间");
        columnsInfo.setColumnWidth(150);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("SYSTEMCODE");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("系统简称");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("SYSTEMNAME");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("系统名称");
        columnsInfo.setColumnWidth(150);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("remark");
        columnsInfo.setColumnType("A");
        columnsInfo.setSortable(false);
        columnsInfo.setColumnDesc("申请说明");
        columnsInfo.setColumnWidth(200);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("LASTNO");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("最新编号");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("PATCHINDEX");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("批次");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("CREATEUSERNAME");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("操作者");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("NUMBERLIST");
        columnsInfo.setColumnType("A");
        columnsInfo.setColumnDesc("编号详情");
        columnsInfo.setColumnWidth(80);
        columnsInfo.setSortable(false);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columnsInfo.setRendererFunc("numberRule.numberDetailBtn");
        columns.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId("ID");
        columnsInfo.setColumnType("A");
        columnsInfo.setSortable(false);
        columnsInfo.setColumnDesc("下载");
        columnsInfo.setColumnWidth(120);
        columnsInfo.setTextAlign(TEXT_ALIGN);
        columnsInfo.setRendererFunc("numberRule.downNumberDetailBtn");
        columns.add(columnsInfo);

        //指定sql语句及排序
        String sql = null;
        if (CollectionUtils.isNotEmpty(inputValues)) {
            String str = inputValues.get(0).toUpperCase();

            sql = "SELECT " +
                    "ID, APPLICANT, MOBILE, COUNT, REMARK, TO_CHAR(CREATETIME,'yyyy-MM-dd HH24:mi:ss') AS CREATETIME, MODTIME, CREATEUSERNAME, MODUSERNAME," +
                    "(REGEXP_SUBSTR(NUMBERLIST,'[^,]+')||'~'||LASTNO) AS SEGMENT, NUMBERLIST, LASTNO, SYSTEMNAME, SYSTEMCODE, PATCHINDEX " +
                    "FROM NUMBER_RULE_INFO WHERE DELETEFLAG='Y' " +
                    "AND (APPLICANT LIKE '%" + str + "%' OR MOBILE LIKE '%" + str + "%' OR Upper(SYSTEMNAME) LIKE '%" + str + "%' OR Upper(SYSTEMCODE) LIKE '%" + str + "%') " +
                    "ORDER BY PATCHINDEX DESC";
        } else {
            //# 没有条件时直接查询即可
            sql = "SELECT " +
                    "ID, APPLICANT, MOBILE, COUNT, REMARK, TO_CHAR(CREATETIME,'yyyy-MM-dd HH24:mi:ss') AS CREATETIME, MODTIME, CREATEUSERNAME, MODUSERNAME," +
                    "(REGEXP_SUBSTR(NUMBERLIST,'[^,]+')||'~'||LASTNO) AS SEGMENT, NUMBERLIST, LASTNO, SYSTEMNAME, SYSTEMCODE, PATCHINDEX " +
                    "FROM NUMBER_RULE_INFO WHERE DELETEFLAG='Y' ORDER BY PATCHINDEX DESC";
        }

        log.info("查询 sql: {}", sql);

        //创建按钮集合
        ArrayList buttonList = new ArrayList();
        if (userView.isSuper_admin() || userView.hasTheFunction("300501")) {
            buttonList.add(new ButtonInfo("登记系统", "numberRule.toRegister"));
        }

        if (userView.isSuper_admin() || userView.hasTheFunction("300502")) {
            buttonList.add(new ButtonInfo("申请编号", "numberRule.toApply"));
        }

        if (userView.isSuper_admin() || userView.hasTheFunction("300503")) {
            buttonList.add(new ButtonInfo("删除记录", "numberRule.deleteNumberRule"));
        }

        TableConfigBuilder builder = new TableConfigBuilder("gz_numberRule_query", columns, "numberRuleTable", userView, frameconn);
        builder.setTitle("编号规则");
        builder.setDataSql(sql);
        builder.setSetScheme(true);
        builder.setShowPublicPlan(true);
        builder.setSelectable(true);
        builder.setAutoRender(true);
        builder.setTableTools(buttonList);
        String tableConfig = builder.createExtTableConfig();

        return tableConfig;
    }

    /**
     * 删除
     * 将删除标志置为N
     *
     * @param ids
     * @param username
     */
    public void deleteNumberRule(ArrayList ids, String username) {
        if (ids == null || ids.size() == 0)
            return;

        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            String tempIds = String.join(",", ids);
            String modTime = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String sql = "UPDATE NUMBER_RULE_INFO " +
                    "SET DELETEFLAG='N', " +
                    "MODTIME=to_date('" + modTime + "','" + DATE_FORMAT_SQL + "')," +
                    "MODUSERNAME='" + username +
                    "' WHERE ID in (" + tempIds + ")";
            log.info("删除 sql: {}", sql);
            dao.update(sql);
        } catch (GeneralException e1) {
            log.error("deleteNumberRule:创建conn链接出错!,desc:{}", e1);
        } catch (SQLException e) {
            log.error("deleteNumberRule:执行删除sql出错!,desc:{}", e);
        } finally {
            PubFunc.closeDbObj(conn);
        }
    }

    /**
     * 申请编号
     *
     * @param info
     * @param username
     */
    public String addNumberRule(NumberRuleBean info, String username) {
        String updateResult = "true";
        String now = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        Connection conn = null;

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String systemCode = info.getSystemCode();
            if (StringUtils.isBlank(systemCode)) {
                log.error("系统简称不能为空");
                return "系统简称不能为空";
            }
            Integer count = info.getCount();
            if (count == null || count < 1) {
                log.error("申请编号时，申请个数为{}", count);
                return "编号个数需要大于0";
            }

            if (count > MAX_COUNT_PER) {
                log.error("申请编号时，申请个数为{}", count);
                return "编号个数最大为" + MAX_COUNT_PER + "个";
            }

            String checkMaxCountSql = "SELECT NVL(SUM(COUNT),0) AS TOTAL FROM NUMBER_RULE_INFO WHERE SYSTEMCODE='" + systemCode + "' AND DELETEFLAG='Y'";
            RowSet search = dao.search(checkMaxCountSql);
            if (search.next()) {
                int total = search.getInt("TOTAL");
                int sysTotal = total + count;
                if (sysTotal > ONE_SYSTEM_MAX_NUMBER_COUNT) {
                    log.error("申请编号时，本次申请：{}, 累计已申请：{}, 已经超过了最大值：{}", count, total, ONE_SYSTEM_MAX_NUMBER_COUNT);
                    return "系统：" + systemCode + "累计编号个数超过最大限制[" + ONE_SYSTEM_MAX_NUMBER_COUNT + "], 最多还可以申请" + (ONE_SYSTEM_MAX_NUMBER_COUNT - total) + "个";
                }
            }

            String systemName = info.getSystemName();
            if (StringUtils.isBlank(systemName)) {
                log.error("系统名称不能为空");
                return "系统名称不能为空";
            }
            String id = info.getId();
            String remark = info.getRemark();

            String applicant = info.getApplicant();
            if (StringUtils.isBlank(applicant)) {
                log.error("申请编号时，申请者为{}", applicant);
                return "申请者不能为空";
            }
            String mobile = info.getMobile();
            if (StringUtils.isBlank(mobile)) {
                log.error("申请编号时，手机号为{}", mobile);
                return "手机号不能为空";
            }

            Pair<String, Integer> result = getLastNoAndPatchIndex();
            String oldLastNo = result.getLeft();
            Integer patchIndex = result.getRight() + 1; //# 每次增加为批次号
            Pair<String, String> res = NumberGenTool.getNextPatchNo(oldLastNo, count);
            String lastNo = res.getLeft();
            String numberList = res.getRight();

            String sql = "INSERT INTO NUMBER_RULE_INFO (ID,APPLICANT,MOBILE,COUNT,REMARK,CREATETIME,MODTIME,CREATEUSERNAME,MODUSERNAME,NUMBERLIST,LASTNO,PATCHINDEX,SYSTEMNAME,SYSTEMCODE,DELETEFLAG) " +
                    "VALUES('" + id + "','" + applicant + "','" + mobile + "'," + count + ",'" + remark + "',to_date('" + now + "','" + DATE_FORMAT_SQL + "')," +
                    "to_date('" + now + "','" + DATE_FORMAT_SQL + "'),'" + username + "','" + username + "'," +
                    "'" + numberList + "','" + lastNo + "'," + patchIndex + ",'" + systemName + "' ,'" + systemCode + "' ,'Y' )";

            log.info("添加 sql: {}", sql);

            dao.update(sql);
        } catch (Exception e) {
            if (e instanceof NoOverException) {
                log.error("addNumberRule:执行添加sql出错!,param:{}, desc:{}", info.toString(), e);
                updateResult = "申请编号出错了:" + e.getMessage();
            } else {
                log.error("addNumberRule:执行添加sql出错!,param:{}, desc:{}", info.toString(), e);
                updateResult = "申请编号出错了，请联系管理员";
            }

        } finally {
            PubFunc.closeDbObj(conn);
        }

        return updateResult;
    }

    /**
     * 获取最新编号和批次号
     *
     * @return
     */
    private Pair<String, Integer> getLastNoAndPatchIndex() {
        String lastNo = DEFAULT_NUMBER_FIRST;
        Integer patchIndex = 0;
        String sql = "SELECT LASTNO,PATCHINDEX FROM (SELECT * FROM NUMBER_RULE_INFO ORDER BY PATCHINDEX DESC) WHERE ROWNUM=1 ORDER BY PATCHINDEX DESC";
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            log.info("获取最新编号 sql: {}", sql);
            RowSet rowSet = dao.search(sql);
            if (rowSet.next()) {
                lastNo = rowSet.getString("LASTNO");
                if (StringUtils.isBlank(lastNo)) {
                    lastNo = DEFAULT_NUMBER_FIRST; //# 第一次为A0000
                }

                patchIndex = rowSet.getInt("PATCHINDEX");
                if (patchIndex == null) {
                    patchIndex = 0; //# 第一批次为0
                }
            }

        } catch (GeneralException e1) {
            log.error("getLastNoAndPatchIndex:创建conn链接出错!, desc:{}", e1);
        } catch (SQLException e) {
            log.error("getLastNoAndPatchIndex:执行获取最新编号和批次号sql出错!,desc:{}", e);
        } finally {
            PubFunc.closeDbObj(conn);
        }

        return Pair.of(lastNo, patchIndex);
    }

    /**
     * 下载编号详情
     *
     * @param id
     * @return
     */
    public String downloadNumberList(String id) {
        String sql = "SELECT APPLICANT, COUNT, NUMBERLIST FROM NUMBER_RULE_INFO WHERE ID = '" + id + "'";
        Connection conn = null;
        BufferedWriter out = null;

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            log.info("下载编号详情 sql: {}", sql);
            RowSet rowSet = dao.search(sql);
            String applicant = "";
            String numberList = "";
            StringBuilder content = new StringBuilder();

            if (rowSet.next()) {
                Integer count = rowSet.getInt("COUNT");
                content.append("==================================\n");
                String title = "*** 编号列表(共 " + count + " 个) ***";
                content.append(title).append("\n");
                content.append("==================================\n");

                applicant = rowSet.getString("APPLICANT");
                numberList = rowSet.getString("NUMBERLIST");
                if (StringUtils.isNotBlank(numberList)) {
                    String[] split = numberList.split(NumberGenTool.SEP);
                    for (int i = 0; i < split.length; i++) {
                        //content.append((i + 1) + "\t\t").append(split[i]).append("\n");
                        content.append(split[i]).append("\n");
                    }
                }
            }

            String fileName = applicant + "_" + new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date()) + ".txt";

            //新生成的文件放在临时目录里
            String path = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            log.info("file where={}", path);
            File downloadFile = new File(path);
            if (!downloadFile.exists())
                downloadFile.createNewFile();

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), NumberGenTool.ENCODING_CODE));
            out.write(content.toString());
            out.flush();

            //# 并非加密，只是做base编码转化
            return PubFunc.encrypt(fileName);
        } catch (Exception e) {
            log.error("downloadNumberList: 下载编号详情出错了!, desc:{}", e);
        } finally {
            PubFunc.closeDbObj(conn);
            PubFunc.closeIoResource(out);
        }

        //# 返回文件名即可
        return "";
    }

    /**
     * 注册系统（接口使用）
     *
     * @param info
     * @param username
     * @return
     */
    public String registerSystem(NumberRuleBean info, String username) {
        String result = "true";
        String now = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String systemCode = info.getSystemCode();
            if (StringUtils.isBlank(systemCode)) {
                log.error("系统简称不能为空");
                return "系统简称不能为空";
            }
            String systemName = info.getSystemName();
            if (StringUtils.isBlank(systemName)) {
                log.error("系统名称不能为空");
                return "系统名称不能为空";
            }

            String applicant = info.getApplicant();
            String mobile = info.getMobile();

            systemCode = systemCode.toUpperCase();
            systemName = systemName.toUpperCase();

            //# 先判断当前系统编码是否已经注册过了
            RowSet rowSet = dao.search("SELECT COUNT(ID) SYSTEMCODECOUNT FROM NUMBER_RULE_INFO WHERE SYSTEMCODE = '" + systemCode + "'");
            if (rowSet.next()) {
                int count = rowSet.getInt("SYSTEMCODECOUNT");
                if (count > 0) {
                    return systemName + "-" + systemCode + " 已经注册了！";
                }
            }

            String id = info.getId();
            Integer count = 0;
            String remark = "登记系统";

            Pair<String, Integer> res = getLastNoAndPatchIndex();
            String oldLastNo = res.getLeft();
            Integer patchIndex = res.getRight() + 1; //# 每次增加为批次号

            String sql = "INSERT INTO NUMBER_RULE_INFO (ID,APPLICANT,MOBILE,COUNT,REMARK,CREATETIME,MODTIME,CREATEUSERNAME,MODUSERNAME,NUMBERLIST,LASTNO,PATCHINDEX,SYSTEMNAME,SYSTEMCODE,DELETEFLAG) " +
                    "VALUES('" + id + "','" + applicant + "','" + mobile + "'," + count + ",'" + remark + "',to_date('" + now + "','" + DATE_FORMAT_SQL + "')," +
                    "to_date('" + now + "','" + DATE_FORMAT_SQL + "'),'" + username + "','" + username + "'," +
                    "'','" + oldLastNo + "'," + patchIndex + ",'" + systemName + "' ,'" + systemCode + "' ,'Y' )";

            log.info("注册系统 sql: {}", sql);
            dao.update(sql);

        } catch (Exception e) {
            log.error("registerSystem:执行注册系统sql出错!,param:{},desc:{}", info.toString(), e);
            result = "出错了，请联系管理员~";
        } finally {
            PubFunc.closeDbObj(conn);
        }

        return result;
    }

    /**
     * 接口调用申请编号
     *
     * @param reqInfo
     * @return
     */
    public Pair<Boolean, String> saveRequestNumberRule(NumberRuleBean reqInfo, ContentDAO dao) throws SQLException {
        String saveResult = "true";
        ResultData resultData = new ResultData();

        String checkMaxCountSql = "SELECT NVL(SUM(COUNT),0) AS TOTAL FROM NUMBER_RULE_INFO WHERE SYSTEMCODE='" + reqInfo.getSystemCode() + "' AND DELETEFLAG='Y'";
        RowSet search = dao.search(checkMaxCountSql);
        if (search.next()) {
            int total = search.getInt("TOTAL");
            int sysTotal = total + reqInfo.getCount();
            if (sysTotal > ONE_SYSTEM_MAX_NUMBER_COUNT) {
                String tip = "系统：" + reqInfo.getSystemCode() + "累计编号个数超过最大限制[" + ONE_SYSTEM_MAX_NUMBER_COUNT + "], 最多还可以申请" + (ONE_SYSTEM_MAX_NUMBER_COUNT - total) + "个";

                resultData.setResponseCode(CODE_MAX_COUNT_LIMIT);
                resultData.setResponseMessage(tip);
                saveResult = JsonUtil.toJSONString(resultData);
                log.error("接口调用申请编号时: {}, param:{}, desc:{} ", saveResult, JsonUtil.toJSONString(reqInfo), tip);

                return Pair.of(false, saveResult);
            }
        }

        Integer count = reqInfo.getCount();
        String systemCode = reqInfo.getSystemCode();
        String systemName = reqInfo.getSystemName();
        String applicant = reqInfo.getApplicant();
        String mobile = reqInfo.getMobile();
        String remark = reqInfo.getRemark();

        //# 保存数据
        String now = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        Pair<String, Integer> result = getLastNoAndPatchIndex();
        String oldLastNo = result.getLeft();
        Integer patchIndex = result.getRight() + 1; //# 每次增加为批次号
        Pair<String, String> res = NumberGenTool.getNextPatchNo(oldLastNo, count);
        String lastNo = res.getLeft();
        String numberList = res.getRight();
        String id = NumberGenTool.getId();

        String sql = "INSERT INTO NUMBER_RULE_INFO (ID,APPLICANT,MOBILE,COUNT,REMARK,CREATETIME,MODTIME,CREATEUSERNAME,MODUSERNAME,NUMBERLIST,LASTNO,PATCHINDEX,SYSTEMNAME,SYSTEMCODE,DELETEFLAG) " +
                "VALUES('" + id + "','" + applicant + "','" + mobile + "'," + count + ",'" + remark + "',to_date('" + now + "','" + DATE_FORMAT_SQL + "')," +
                "to_date('" + now + "','" + DATE_FORMAT_SQL + "'),'" + DEFAULT_ACTION_USERNAME + "','" + DEFAULT_ACTION_USERNAME + "'," +
                "'" + numberList + "','" + lastNo + "'," + patchIndex + ",'" + systemName + "' ,'" + systemCode + "' ,'Y')";

        log.info("添加 sql: {}", sql);

        dao.update(sql);

        return Pair.of(true, numberList);
    }

    /**
     * 获取已经注册的系统信息
     *
     * @return
     */
    public List<NumberRuleVo> getSystemList() {
        Connection conn = null;
        List<NumberRuleVo> systemList = Lists.newArrayList();
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            String sql = "SELECT SYSTEMCODE,SYSTEMNAME FROM NUMBER_RULE_INFO WHERE DELETEFLAG='Y' GROUP BY SYSTEMCODE,SYSTEMNAME";
            RowSet search = dao.search(sql);
            NumberRuleVo bean = null;
            while (search.next()) {
                bean = new NumberRuleVo();
                bean.setSystemCode(search.getString("SYSTEMCODE"));
                bean.setSystemName(search.getString("SYSTEMNAME"));
                systemList.add(bean);
            }

        } catch (Exception e) {
            log.error("getSystemList: 获取已注册系统信息出错，desc:{}", e);
        }
        return systemList;
    }
}
