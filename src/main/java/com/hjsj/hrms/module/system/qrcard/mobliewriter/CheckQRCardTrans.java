/*
 * @(#)QRCardUtils.java 2018年8月6日上午10:57:25 ehr Copyright 2018 HJSOFT, Inc. All
 * rights reserved. HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @Titile: QRCardUtils
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年8月6日上午10:57:25
 * @author: wangz
 * @version 1.0
 *
 */
public class CheckQRCardTrans extends IBusiness {

    /** (non-Javadoc)
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    @Override
    public void execute() throws GeneralException {
        String tabid = (String) this.getFormHM().get("tabid");
        String autoCirculation = "1";//自动流转  默认不是自动流转
        String sp = "1";//默认需要审批
        ContentDAO dao = new ContentDAO(frameconn);
        String sql = "select ctrl_para,sp_flag from Template_table where Tabid = ?";
        Document doc = null;
        Element element = null;
        RowSet rs = null;
        String personnelTransfer = "0";//默认不是人员调入型模版
        try {
            rs = dao.search(sql, Arrays.asList(Integer.parseInt(tabid)));
            String ctrl_para = null;
            if (rs.next()) {
                ctrl_para = rs.getString("ctrl_para");
                sp = rs.getString("sp_flag");
                doc = PubFunc.generateDom(ctrl_para);
                String xpath = "/params/sp_flag";
                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist = findPath.selectNodes(doc); //获取当前节点下的元素           
                if (childlist != null && childlist.size() > 0) {
                    element = (Element) childlist.get(0);
                    autoCirculation = (String) element.getAttributeValue("mode");//遍历获取mode的值
                }
            }
            sql = "select TabId from Template_table where TabId = ? and OperationCode in (select  OperationCode from Operation where OperationType = 0);";
            rs = dao.search(sql, Arrays.asList(Integer.parseInt(tabid)));
            if(rs.next()) {
                personnelTransfer = "1";
            }
            
            this.getFormHM().put("autoCirculation", autoCirculation);
            this.getFormHM().put("personnelTransfer", personnelTransfer);
            this.getFormHM().put("sp", sp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

    }

}
