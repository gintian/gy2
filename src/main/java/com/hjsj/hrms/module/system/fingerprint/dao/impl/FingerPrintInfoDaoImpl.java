package com.hjsj.hrms.module.system.fingerprint.dao.impl;

import com.hjsj.hrms.module.system.fingerprint.dao.FingerPrintInfoDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FingerPrintInfoDaoImpl implements FingerPrintInfoDao {
    private Connection connection;

    public FingerPrintInfoDaoImpl(Connection connection) {
        this.connection = connection;
    }

    /**
     * 保存人脸数据
     *
     * @return
     */
    @Override
    public boolean saveFaceData(String faceData, String tabName) throws SQLException {
        int num = 0;
        String faceDataField = SystemConfig.getPropertyValue("faceDataField");
        ContentDAO dao = new ContentDAO(this.connection);
        ArrayList valueList = new ArrayList();
        valueList.add(faceData);
        StringBuffer strsql = new StringBuffer();
        strsql.append("update ");
        strsql.append(tabName);
        strsql.append(" set ");
        strsql.append(faceDataField + "_2");
        strsql.append(" = ?");
        strsql.append(" where submitflag = 1");
        num = dao.update(strsql.toString(), valueList);

        return num == 1 ? true : false;

    }

    /**
     * 保存指纹数据
     *
     * @param fingerDataMap
     * @param tabName
     * @return
     */
    @Override
    public boolean saveFingerData(Map fingerDataMap, String tabName) throws SQLException {
        int num = 0;
        String fingerField = SystemConfig.getPropertyValue("fingerField");
        String[] fingerDataArry = fingerField.split(",");
        Map fieldMap = new HashMap();
        for (int i = 0; i < fingerDataArry.length; i++) {
            String correctField = fingerDataArry[i];
            String key = correctField.split("=")[0];
            String value = correctField.split("=")[1];
            fieldMap.put(key, value);
        }

        ContentDAO dao = new ContentDAO(this.connection);
        ArrayList valueList = new ArrayList();
        StringBuffer strsql = new StringBuffer();
        strsql.append("update ");
        strsql.append(tabName);
        strsql.append(" set ");
        Iterator it = fingerDataMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            String field = (String) fieldMap.get(key);//指标
            strsql.append(field + "_2");
            strsql.append(" = ?,");
            valueList.add(value);
        }
        strsql.setLength(strsql.length() - 1);
        strsql.append(" where submitflag = 1");
        num = dao.update(strsql.toString(), valueList);

        return num == 1 ? true : false;
    }

    @Override
    public void searchInitData(String tabName, Map dataMap) throws SQLException {
        ContentDAO dao = new ContentDAO(this.connection);

        StringBuffer strsql = new StringBuffer();
        strsql.append("select a0101_1 name,A0144_");
        if ("103".equals(tabName.split("_")[1]) || "102".equals(tabName.split("_")[1])) {//变化前
            strsql.append("1");
        } else {
            strsql.append("2");
        }
        strsql.append(" jobNumber from " + tabName);
        strsql.append(" where submitflag = 1");
        RowSet rs = null;
        try {
            rs = dao.search(strsql.toString());
            if (rs.next()) {
                String jobNumber = rs.getString("jobNumber");
                String name = rs.getString("name");
                dataMap.put("jobNumber", jobNumber);
                dataMap.put("name", name);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

}
