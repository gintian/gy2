package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * <p>Title:KQ_Parameter</p>
 * <p>Description:得到考勤参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-5:11:04:29</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class KQ_Parameter {

    public HashMap getParameter(String codeitemid, UserView userView, Connection conn) {
        if (codeitemid.indexOf("UN") != -1) {
            int i = codeitemid.indexOf("UN");
            codeitemid = codeitemid.substring(i + 2);
        }        
        else if (codeitemid.indexOf("UM") != -1) {
            int i = codeitemid.indexOf("UM");
            codeitemid = codeitemid.substring(i + 2);
        }        
        else if (codeitemid.indexOf("@K") != -1) {
            int i = codeitemid.indexOf("@K");
            codeitemid = codeitemid.substring(i + 2);
        }
        
        String xmlContent = search_KQ_PARAMETER(conn);
        ArrayList kq_conlist = ReadParameterXml(xmlContent);
        
        HashMap hashmap = new HashMap();
        try {
            hashmap = getKqBaseParameter(codeitemid, conn, xmlContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (kq_conlist != null && kq_conlist.size() > 0) {
            hashmap.put("cardno", kq_conlist.get(0).toString());
            hashmap.put("g_no", kq_conlist.get(1).toString());
            hashmap.put("kq_type", kq_conlist.get(2).toString());
        } else {
            hashmap.put("cardno", "");
            hashmap.put("g_no", "");
            hashmap.put("kq_type", "");
        }

        hashmap.put("userid", userView.getUserId());
        return hashmap;
    }

    private HashMap getKqBaseParameter(String codeitemid, Connection conn, String xmlContent) throws GeneralException {
        HashMap hashmap = new HashMap();
        
        //zxj changed 2014.02.03 人员库参数不再分单位，直接取“UN”
        hashmap = ReadOneParameterXml(xmlContent, "UN");
        if (hashmap.isEmpty()) {
            hashmap.put("b0110", "UN");
            hashmap.put("nbase", "");//考勤人员库                                                    
        }
        return hashmap;
        
        /*        
        if (codeitemid == null || codeitemid.length() <= 0) {
            hashmap = ReadOneParameterXml(xmlContent, "UN");
            if (hashmap.isEmpty()) {
                hashmap.put("b0110", "UN");
                hashmap.put("nbase", "");//考勤人员库			  				 					    
            }
            return hashmap;
        }
        
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        try {
            String orgSql = "SELECT parentid,codeitemid from organization" 
                          + " where '" + codeitemid + "' like codeitemid" + Sql_switcher.concat() + "'%'" 
                          + " and codesetid='UN'";
            rowSet = dao.search(orgSql);
            while (rowSet.next()) {
                codeitemid = rowSet.getString("codeitemid");
                String b0100 = "UN" + codeitemid;
                hashmap = ReadOneParameterXml(xmlContent, b0100);
                if (!hashmap.isEmpty())
                    return hashmap;
            }
            
            if (hashmap.isEmpty()) {
                hashmap = ReadOneParameterXml(xmlContent, "UN");
                if (hashmap.isEmpty()) {
                    hashmap.put("b0110", "UN");
                    hashmap.put("nbase", "");//考勤人员库			  				 					    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        
        return hashmap;
        */
    }

    private HashMap ReadOneParameterXml(String xmlContent, String b0110) {
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

    public ArrayList ReadParameterXml(String xmlContent) {
        String xpath = "/kq";
        ArrayList list = new ArrayList();
        if (xmlContent != null && xmlContent.length() > 0) {
            try {
                Document doc = PubFunc.generateDom(xmlContent);//读入xml
                XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点

                List childlist = reportPath.selectNodes(doc);

                Iterator i = childlist.iterator();
                if (i.hasNext()) {
                    /**报表基本参数**/
                    Element childR = (Element) i.next();
                    list.add(childR.getAttributeValue("cardno"));//考勤卡号					   
                    list.add(childR.getAttributeValue("g_no")); //考勤工号					   
                    list.add(childR.getAttributeValue("kq_type")); //考勤方式	
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 修改考勤参数
     * */
    public String WriteOutParameterXml(HashMap hashMap, UserView userView, Connection conn) {

        String xmlContent = search_KQ_PARAMETER(conn);
        //ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);

        if (xmlContent != null && xmlContent.length() > 0) {
            try {
                Document doc = PubFunc.generateDom(xmlContent);//读入xml
                String xpath = "/kq";
                XPath reportPath = XPath.newInstance(xpath);// 取得根节点
                List childlist = reportPath.selectNodes(doc);
                Iterator t = childlist.iterator();
                if (t.hasNext()) {
                    String cardno = (String) hashMap.get("cardno") != null ? (String) hashMap.get("cardno") : "";
                    String g_no = (String) hashMap.get("g_no") != null ? (String) hashMap.get("g_no") : "";
                    String kq_type = (String) hashMap.get("kq_type") != null ? (String) hashMap.get("kq_type") : "";
                    
                    Element childR = (Element) t.next();
                    childR.setAttribute("cardno", cardno);
                    childR.setAttribute("g_no", g_no);
                    childR.setAttribute("kq_type", kq_type);
                    
                    //zxj changed 2014.02.13 人员库不再分单位设置，固定设为UN
                    //String b0110 = managePrivCode.getUNB0110();
                    String b0110 = "UN";
                    xpath = "/kq/parameter[@B0110='" + b0110 + "']";
                    reportPath = XPath.newInstance(xpath);// 取得根节点
                    childlist = reportPath.selectNodes(doc);
                    Iterator i = childlist.iterator();
                    if (i.hasNext()) {
                        /**节点,考勤人员库**/
                        Element childE = (Element) i.next();
                        Element nbase = childE.getChild("nbase");

                        String strNbase = (String) hashMap.get("nbase");
                        strNbase = strNbase != null && strNbase.length() > 0 ? strNbase : "";
                        nbase.setAttribute("value", strNbase);

                        XMLOutputter outputter = new XMLOutputter();
                        Format format = Format.getPrettyFormat();
                        format.setEncoding("UTF-8");
                        outputter.setFormat(format);
                        xmlContent = outputter.outputString(doc);
                    } else {
                        /**添加新增**/
                        xpath = "/kq";
                        reportPath = XPath.newInstance(xpath);// 取得根节点
                        List list = reportPath.selectNodes(doc);
                        Iterator r = list.iterator();
                        if (r.hasNext()) {
                            Element param_item = new Element("parameter");
                            param_item.setAttribute("B0110", b0110);
                            Element nbase_item = new Element("nbase");
                            
                            String strNbase = (String) hashMap.get("nbase");
                            strNbase = strNbase != null && strNbase.length() > 0 ? strNbase : "";
                            nbase_item.setAttribute("value", strNbase);
                            
                            childR.addContent(param_item);
                            param_item.addContent(nbase_item);
                            
                            XMLOutputter outputter = new XMLOutputter();
                            Format format = Format.getPrettyFormat();
                            format.setEncoding("UTF-8");
                            outputter.setFormat(format);
                            xmlContent = outputter.outputString(doc);
                        }
                    }
                }
                insert_XMLData(conn, xmlContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xmlContent;
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

    /**
     * 检索常量表中纪录为KQ_PARAMETER的Constant
     * **/
    public String search_KQ_PARAMETER(Connection conn) {
        StringBuffer sb = new StringBuffer();
        String constant = "constant";
        if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
            constant = "\"constant\"";
        }
        sb.append("select Str_Value from "+constant+" where UPPER("+constant+")='KQ_PARAMETER'");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        String xmlConstant = "";
        try {
            rowSet = dao.search(sb.toString());
            if (rowSet.next()) {
                xmlConstant = Sql_switcher.readMemo(rowSet, "Str_Value");
            } 
            
            //防止strValue为空的情况
            if (xmlConstant == null || "".equals(xmlConstant.trim())) {
                xmlConstant = init_XMLData(conn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return xmlConstant;
    }

    /**
     * 添加纪录
     * */
    public void insert_XMLData(Connection conn, String xmlContent) {
        ArrayList deletelist = new ArrayList();
        deletelist.add("KQ_PARAMETER");
        String deleteSQL = "delete from constant where Constant=?";
        ContentDAO dao = new ContentDAO(conn);
        try {
            dao.delete(deleteSQL, deletelist);
            RecordVo vo = new RecordVo("constant");
            vo.setString("constant", "KQ_PARAMETER");
            vo.setString("type", "A");
            vo.setString("describe", "考勤参数");
            vo.setString("str_value", xmlContent);
            dao.addValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getUNParameter(Connection conn) throws GeneralException {
        String xmlContent = search_KQ_PARAMETER(conn);
        HashMap hashmap = ReadOneParameterXml(xmlContent, "UN");
        return hashmap;
    }
}
