package com.hjsj.hrms.transaction.mobileapp.kq.util;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * <p>Title: Parameter </p>
 * <p>Description: 读取人员主集配置的显示主集信息和考勤参数（卡号，工号，方式）</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-10-31 上午09:11:59</p>
 * @author tiany
 * @version 1.0
 */
public class Parameter {
    private Connection conn =null;
    public Parameter(Connection conn){
        this.conn =conn;
    }
    /**
     * 检索常量表中纪录为KQ_PARAMETER的Constant得字段
     * @throws SQLException 
     * **/
    public String search_KQ_PARAMETER() throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("select Str_Value from constant where UPPER(Constant)='KQ_PARAMETER'");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        String xmlConstant = "";
        try {
            rowSet = dao.search(sb.toString());
            if (rowSet.next()) {
                xmlConstant = rowSet.getString("Str_Value");
            } else {
                xmlConstant = init_XMLData(conn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rowSet!=null){
                rowSet.close();
            }
        }
        return xmlConstant;
    }
    /**
     * 当常量表中没有KQ_PARAMETER记录，或KQ_PARAMETER中Constant为空的时候处理
     * */
    public String init_XMLData(Connection conn) {
        StringBuffer xmlstr = new StringBuffer();
        xmlstr.append("<?xml version='1.0' encoding='GBK'?>");
        xmlstr.append("<kq cardno='' g_no='' kq_type=''>");
        xmlstr.append("<parameter B0110='UN'>");
        xmlstr.append("<nbase value=''/>");
        xmlstr.append("</parameter></kq>");
        ArrayList deletelist = new ArrayList();
        deletelist.add("KQ_PARAMETER");
        String deleteSQL = "delete from constant where Constant=?";
        ContentDAO dao = new ContentDAO(conn);
        try {
            dao.delete(deleteSQL, deletelist);
            StringBuffer insertSQL = new StringBuffer();
            insertSQL.append("insert into constant (Constant,Type,Describe,Str_Value)");
            insertSQL.append(" values (?,?,?,?)");
            ArrayList insertlist = new ArrayList();
            insertlist.add("KQ_PARAMETER");
            insertlist.add("A");
            insertlist.add("考勤参数");
            insertlist.add(xmlstr.toString());
            dao.insert(insertSQL.toString(), insertlist);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlstr.toString();
    }
    public HashMap ReadParameterXml() throws SQLException {
       String xmlContent = search_KQ_PARAMETER();
        String xpath = "/kq";
        HashMap map = new HashMap();
        if (xmlContent != null && xmlContent.length() > 0) {
            try {
                Document doc = PubFunc.generateDom(xmlContent);//读入xml
                XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
                List childlist = reportPath.selectNodes(doc);
                Iterator i = childlist.iterator();
                if (i.hasNext()) {
                    /**报表基本参数**/
                    Element childR = (Element) i.next();
                    map.put("cardno",childR.getAttributeValue("cardno"));//考勤卡号                    
                    map.put("g_no",childR.getAttributeValue("g_no")); //考勤工号                     
                    map.put("kq_type",childR.getAttributeValue("kq_type")); //考勤方式   
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    public HashMap ReadOneParameterXml(String xmlContent, String b0110) {
        String xpath = "/kq/parameter[@B0110='" + b0110 + "']";
        HashMap hashmap = new HashMap();
        if (xmlContent != null && xmlContent.length() > 0) {
            try {
                Document doc = PubFunc.generateDom(xmlContent);//读入xml
                XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点

                List childlist = reportPath.selectNodes(doc);

                Iterator i = childlist.iterator();
                if (i.hasNext()) {
                    /**报表基本参数**/
                    Element childR = (Element) i.next();
                    hashmap.put("b0110", childR.getAttributeValue("B0110"));
                    Element nbase = childR.getChild("nbase");
                    String dd = nbase.getAttributeValue("value");
                    hashmap.put("nbase", dd);//考勤人员库    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashmap;
    } 
}
