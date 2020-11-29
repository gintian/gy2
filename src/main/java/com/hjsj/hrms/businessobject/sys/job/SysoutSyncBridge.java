package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.sysout.SyncBo;
import com.hjsj.hrms.businessobject.sys.sysout.SyncParamPojo;
import com.hjsj.hrms.service.syncdata.SyncDataService;
import com.hjsj.hrms.transaction.sys.options.message.IMessage;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SysoutSyncBridge {
    private LazyDynaBean bean;
    private String hr_only_field;//人员的唯一指标
    private String outinfo = "";
    boolean isSend = false;
    private String sendValue = "0";
    private Logger log = LoggerFactory.getLogger(SysoutSyncBridge.class);
    public SysoutSyncBridge(LazyDynaBean bean, String hr_only_field) {
        this.bean = bean;
        if (hr_only_field == null || hr_only_field.length() < 1) {
            this.hr_only_field = "unique_id";
        } else {
            this.hr_only_field = hr_only_field;
        }
        String send = (String) bean.get("send");
//		isSend = send!=null&&"1".equals(send)?true:false;
        // 增加了两种同步模式，需要将发送的条件重新判断一下
        isSend = send != null && "0".equals(send) ? false : true;
        sendValue = send;
    }

    public String run() {
        String errorMsg = "";
        Connection conn = null;
        RowSet rs = null;
        try {
            String isMorrow = SystemConfig.getPropertyValue("is_morrow");
            conn = (Connection) AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String sql = "select count(*) a from t_hr_view where " + bean.get("sys_id") + "<>0 ";
            if ("true".equalsIgnoreCase(isMorrow)) {
                sql = sql + " and username is not null and username <>'' and unique_id is not null";
            } else if (this.hr_only_field != null && this.hr_only_field.length() > 0) {
                sql = sql + " and " + this.hr_only_field + " is not null";
            }
            String tempWhere = getSyncWhere("A");
            if (isSend && tempWhere != null && tempWhere.length() > 0) {//加上过滤条件
                sql = sql + " and " + tempWhere;
            }
            boolean ishr = false;
            boolean isOrg = false;
            boolean isPost = false;
            String sync_A01 = (String) bean.get("sync_A01");
            String sync_B01 = (String) bean.get("sync_B01");
            String sync_K01 = (String) bean.get("sync_K01");
            if (sync_A01 != null && "1".equals(sync_A01)) {
                rs = dao.search(sql);
                if (rs.next()) {
                    int count = rs.getInt("a");
                    if (count > 0) {
                        ishr = true;
                    }
                }
            }

            if (sync_B01 != null && "1".equals(sync_B01)) {
                sql = "select count(*) a from t_org_view where " + bean.get("sys_id") + "<>0";
                tempWhere = getSyncWhere("B");
                if (isSend && tempWhere != null && tempWhere.length() > 0) {//加上过滤条件
                    sql = sql + " and " + tempWhere;
                }
                rs = dao.search(sql);
                if (rs.next()) {
                    int count = rs.getInt("a");
                    if (count > 0) {
                        isOrg = true;
                    }
                }
            }
            /**new */
            if (isSend && sync_K01 != null && "1".equals(sync_K01)) {
                sql = "select count(*) a from t_post_view where " + bean.get("sys_id") + "<>0";
                tempWhere = getSyncWhere("K");
                if (tempWhere != null && tempWhere.length() > 0) {
                    sql = sql + " and " + tempWhere;
                }
                rs = dao.search(sql);
                if (rs.next()) {
                    int count = rs.getInt("a");
                    if (count > 0) {
                        isPost = true;
                    }
                }
            }

            /**new control=A,B,K, 对那些类型需要发送*/
            String control = (String) this.bean.get("control");
            if (isSend && control != null && control.length() > 0) {
                ishr = control.indexOf("A") != -1 ? ishr : false;
                isOrg = control.indexOf("B") != -1 ? isOrg : false;
                isPost = control.indexOf("K") != -1 ? isPost : false;
            }

            if (ishr || isOrg || isPost) {
                if ("1".equals(sendValue)) {// 发送变动通知
                    String mess = sendSyncMessage(ishr, isOrg, isPost);//调用webservice发送消息后，得到返回消息
                    this.outinfo = this.outinfo != null && this.outinfo.length() > 0 ? this.outinfo : mess;
                    if (mess != null && mess.length() > 0) {
                        Category.getInstance(this.getClass().getName()).error(bean.get("sys_name") + "接口返回信息内容：" + mess);
                        errorMsg = mess;
                    }
                } else if ("2".equals(sendValue) || "3".equals(sendValue)) {// 发送变动内容

                    SyncBo bo = new SyncBo(conn);
                    // 获取系统代号
                    String sysId = (String) bean.get("sys_id");
                    // 获取 xml配置文件
//					String webserver = SystemConfig.getPropertyValue("webserver");
//
//					File file = null;
////					System.out.println("---webserver:" + webserver);
//					if ("jboss".equalsIgnoreCase(webserver)) {
//						String pathStr = SystemConfig.getPropertyValue("jbossxmlpath");
////						System.out.println("---pathStr:" + pathStr);
//						file = new File(pathStr, sysId + ".xml");
////						System.out.println("---file:" + sysId + ".xml,,,," + file.toString());
//					} else {
//						file = bo.getFilePath (sysId + ".xml");
//					}

                    String fileContent = null;
                    StringBuffer strBuff = new StringBuffer();
                    BufferedReader buff = null;
                    try {
                        InputStream input = this.getClass().getClassLoader().getResourceAsStream(sysId + ".xml");
                        InputStreamReader reader = new InputStreamReader(input);
                        buff = new BufferedReader(reader);
                        String str = null;
                        while ((str = buff.readLine()) != null) {
                            strBuff.append(str);
                        }
                        fileContent = strBuff.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (buff != null) {
                                buff.close();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (fileContent != null) {// 存在配置文件
                        SyncParamPojo pojo = new SyncParamPojo();
                        // 设置参数
                        bo.setParam(fileContent, pojo);

                        // 条件过滤
                        String whereA = getSyncWhere("A");
                        String whereB = getSyncWhere("B");
                        String whereK = getSyncWhere("K");
                        String emporg = (String) bean.get("emporg");
                        String hruniqueids = (String) bean.get("hruniqueids");
                        String orguniqueids = (String) bean.get("orguniqueids");
                        String postuniqueids = (String) bean.get("postuniqueids");

                        if ("1".equals(emporg)) {// 只发送人员
                            whereA = addWhere(whereA, hruniqueids);

                            // 如果没有选人，则按照原来的逻辑发送（即发送全部）
                            if (hruniqueids != null && hruniqueids.trim().length() > 0) {
                                whereB = "1=2";
                                whereK = "1=2";
                            }
                        }

                        if ("2".equals(emporg)) {// 只发送机构
                            whereB = addWhere(whereB, orguniqueids);

                            if (orguniqueids != null && orguniqueids.trim().length() > 0) {
                                whereA = "1=2";
                                whereK = "1=2";
                            }
                        }

                        if ("3".equals(emporg)) {// 只发送岗位
                            if (postuniqueids != null && postuniqueids.trim().length() > 0) {
                                whereA = "1=2";
                                whereB = "1=2";
                            }
                            whereK = addWhere(whereK, postuniqueids);
                        }


                        // 发送
                        String mess = bo.sendSyncMessage(pojo, sysId, whereA, whereB, whereK);
                        this.outinfo = mess;
                        if (bo.erroCount > 0) {
                            this.updateFailtime(conn);
                        }
                        System.out.println("返回值---" + mess);

                    } else {// 不存在配置文件

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        return errorMsg;
    }


    //发送消息更新同步消息
    private String sendSyncMessage(boolean ishr, boolean isOrg, boolean isPost) {
        String ip_addr = (String) bean.get("sync_data_addr");
        String port = (String) bean.get("sync_data_post");
        String username = (String) bean.get("sync_user");
        String pass = (String) bean.get("sync_pass");
        String sync_base = (String) bean.get("sync_base");
        String sync_baseType = (String) bean.get("sync_baseType");
        String org_table = (String) bean.get("org_table");
        String emp_table = (String) bean.get("emp_table");
        String post_table = (String) bean.get("post_table");

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        buf.append("<hr>");
        buf.append("<recs>");
        if (ishr) {
            buf.append("<rec>emp</rec>");
        }
        if (isOrg) {
            buf.append("<rec>org</rec>");
        }
        if (isPost) {
            buf.append("<rec>post</rec>");
        }
        buf.append("</recs>");
        buf.append("<jdbc>");
        //信息xml中添加外部系统代号，读取xml配置时根据 外部系统代号.xml 读取配置文件。以前是读取固定文件，不能实现配置多个相同类型的外部系统
        buf.append("<sysid>" + bean.get("sys_id") + "</sysid>");
        buf.append("<ip_addr>" + ip_addr + "</ip_addr>");
        buf.append("<port>" + port + "</port>");
        buf.append("<username>" + username + "</username>");
        buf.append("<pass>" + pass + "</pass>");
        buf.append("<database>" + sync_base + "</database>");
        buf.append("<datatype>" + sync_baseType + "</datatype>");
        buf.append("<emp_table>" + emp_table + "</emp_table>");
        buf.append("<org_table>" + org_table + "</org_table>");
        if (isSend) {
            buf.append("<post_table>" + post_table + "</post_table>");

            String whereA = getSyncWhere("A");
            String whereB = getSyncWhere("B");
            String whereK = getSyncWhere("K");

            String emporg = (String) bean.get("emporg");
            String hruniqueids = (String) bean.get("hruniqueids");
            String orguniqueids = (String) bean.get("orguniqueids");
            String postuniqueids = (String) bean.get("postuniqueids");

            if ("1".equals(emporg)) {// 只发送人员
                whereA = addWhere(whereA, hruniqueids);
                if (hruniqueids != null && hruniqueids.trim().length() > 0) {
                    whereB = "1=2";
                    whereK = "1=2";
                }
            }

            if ("2".equals(emporg)) {// 只发送机构
                whereB = addWhere(whereB, orguniqueids);
                if (orguniqueids != null && orguniqueids.trim().length() > 0) {
                    whereA = "1=2";
                    whereK = "1=2";
                }
            }

            if ("3".equals(emporg)) {// 只发送岗位
                if (postuniqueids != null && postuniqueids.trim().length() > 0) {
                    whereA = "1=2";
                    whereB = "1=2";
                }
                whereK = addWhere(whereK, postuniqueids);
            }

            buf.append("<emp_where><![CDATA[" + whereA + "]]></emp_where>");
            buf.append("<org_where><![CDATA[" + whereB + "]]></org_where>");
            buf.append("<post_where><![CDATA[" + whereK + "]]></post_where>");
        }
        buf.append("</jdbc>");
        buf.append("</hr>");
        String url = (String) bean.get("url");
        //SysoutSyncInterf stub=null;
        //boolean isCorrect =false;
        String mess = "";
        //int i=0;
        //String isMorrow = SystemConfig.getPropertyValue("is_morrow");
//		SyncDataSoap s = null;
        try {

//				mess=stub.sendSyncMsg(buf.toString());
            String targetNamespace = (String) bean.get("targetNamespace");
            targetNamespace = targetNamespace == null ? "" : targetNamespace;
            mess = getMessage(buf.toString());

        } catch (Exception e) {
            Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_id") + "接口调用sendSyncMsg方法时失败");
            System.out.println(bean.get("sys_id") + "接口初始化失败");
            this.outinfo = this.outinfo != null && this.outinfo.length() > 0 ? this.outinfo : "接口初始化失败=" + e.toString();
            e.printStackTrace();
        }
        return mess;
    }

    private String addWhere(String oldWhere, String uniqueids) {
        if (uniqueids != null && uniqueids.trim().length() > 0) {
            String[] strs = uniqueids.split(",");
            StringBuffer buff = new StringBuffer();
            buff.append(" unique_id in (");
            for (int i = 0; i < strs.length; i++) {
                if (i != 0) {
                    buff.append(",");
                }

                buff.append("'");
                buff.append(strs[i]);
                buff.append("'");
            }
            buff.append(")");

            if (oldWhere != null && oldWhere.trim().length() > 0) {
                return "(" + oldWhere + ") and " + buff.toString();
            } else {
                return buff.toString();
            }
        } else {
            return oldWhere;
        }
    }

    /**
     * 用xfire动态代理调用webservice，.net的有问题
     * @param url
     * @return private SysoutSyncInterf getSysoutSyncInterf(String url) {
    SysoutSyncInterf stub = null;
    try {
    org.codehaus.xfire.service.Service serviceModel = new ObjectServiceFactory().create(SysoutSyncInterf.class);
    serviceModel.setProperty(CommonsHttpMessageSender.DISABLE_EXPECT_CONTINUE, "true");
    XFire xfire = XFireFactory.newInstance().getXFire();
    XFireProxyFactory factory = new XFireProxyFactory(xfire);
    stub = (SysoutSyncInterf) factory.create(serviceModel, url);//得到webservice连接
    } catch (Exception e)  {
    e.printStackTrace();
    }
    return stub;
    }*/

    /**
     * 根据空间名和参数名获得webservice接口返回的数据（解决调用.net的webservice时出错的问题）
     *
     * @return 调用webservice返回的结果
     */
    private String getMessage(String paramValue) {
        String mess = "";

        try {
            SyncDataService SyncDataService = new SyncDataService();
            mess = SyncDataService.sendSyncJobMsg(paramValue);
        } catch (Exception e) {
            mess = e.getMessage();
            e.printStackTrace();
            Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_id") + "接口调用失败，信息为" + mess);
            System.out.println(bean.get("sys_id") + "接口初始化失败");
            this.outinfo = this.outinfo != null && this.outinfo.length() > 0 ? this.outinfo : bean.get("sys_id") + "接口调用失败=" + e.toString();
        }
        return mess;

    }

    /**
     * 通过返回信息，修改标记
     *
     * @param mess
     */
    private void updateSyncSysFlag(Connection conn, String mess) {
        Document doc = null;
        try {
            doc = PubFunc.generateDom(mess);
            boolean isHinfo = failinfo(conn, doc);//info异常信息的处理
            if (!isHinfo)//有异常信息，记失败一次
            {
                updateFailtime(conn);
            }
            boolean isCorrect = updateIdFlag(conn, doc);//处理以同步的数据
            if (!isCorrect && isHinfo)//没有失败信息，但是更新时错误，记失败一次
            {
                updateFailtime(conn);
            }
            boolean isPass = othInfoMess(conn, doc);//其他返回信息中，有描述没有更新的记录
            if (isCorrect && isHinfo && !isPass) {
                updateFailtime(conn);
            }

            String isMorrow = SystemConfig.getPropertyValue("is_morrow");
            if ("true".equalsIgnoreCase(isMorrow)) {
                if (!isHinfo || !isCorrect || !isPass) {
                    String morrow = SystemConfig.getPropertyValue("is_othermessage_tosend");
                    if ("true".equalsIgnoreCase(morrow)) {
                        String classpath = SystemConfig.getPropertyValue("morrow_send_jishitong_messageclass");
                        String textConstant = mess;
                        try {
                            IMessage message = (IMessage) Class.forName(classpath).newInstance();
                            message.sendMessage(textConstant, "3");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(bean.get("sys_name") + "接口返回信息后，修改状态报错");
            System.out.println(mess);
            this.outinfo = bean.get("sys_name") + "接口返回信息后，修改状态报错:" + mess;
            Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "接口返回信息后，修改状态报错");
            e.printStackTrace();
            updateFailtime(conn);
        }
    }

    /**
     * 更细一同步标识
     *
     * @param conn
     * @param doc
     * @return
     * @throws GeneralException
     */
    private boolean updateIdFlag(Connection conn, Document doc) throws GeneralException {
        String str_path = "/msg/recs";
        XPath xpath;
        boolean isCorrect = true;
        try {
            xpath = XPath.newInstance(str_path);
            List list = xpath.selectNodes(doc);
            Element element = null;
            //String info="";
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    element = (Element) list.get(i);
                    String type = element.getAttributeValue("type");
                    if (type != null && "emp".equalsIgnoreCase(type)) {
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            //人员
                            Iterator iter = childlist.iterator();
                            isCorrect = updateFlag(conn, "t_hr_view", iter);
                        }

                    } else if (type != null && "org".equalsIgnoreCase(type)) {
                        //机构
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            Iterator iter = childlist.iterator();
                            isCorrect = updateFlag(conn, "t_org_view", iter);
                        }
                    } else if (isSend && type != null && "post".equalsIgnoreCase(type)) {
                        //机构
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            Iterator iter = childlist.iterator();
                            isCorrect = updateFlag(conn, "t_post_view", iter);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
            System.out.println(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
            e.printStackTrace();
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }

    /**
     * 通过返回的替他异常信息
     *
     * @param conn
     * @param doc
     * @return
     * @throws GeneralException
     */
    private boolean othInfoMess(Connection conn, Document doc) throws GeneralException {
        String str_path = "/msg/oths/elems";
        XPath xpath;
        boolean isCorrect = true;
        try {
            xpath = XPath.newInstance(str_path);
            List list = xpath.selectNodes(doc);
            Element element = null;
            //String info="";
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    element = (Element) list.get(i);
                    String type = element.getAttributeValue("type");
                    if (type != null && "emp".equalsIgnoreCase(type)) {
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            //人员
                            Iterator iter = childlist.iterator();
                            while (iter.hasNext()) {
                                Element elementC = (Element) iter.next();
                                String pass = elementC.getAttributeValue("pass");
                                if (pass != null && "0".equals(pass)) {
                                    isCorrect = false;
                                    break;
                                }
                            }
                        }

                    } else if (type != null && "org".equalsIgnoreCase(type)) {
                        //机构
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            Iterator iter = childlist.iterator();
                            while (iter.hasNext()) {
                                Element elementC = (Element) iter.next();
                                String pass = elementC.getAttributeValue("pass");
                                if (pass != null && "0".equals(pass)) {
                                    isCorrect = false;
                                    break;
                                }
                            }
                        }
                    } else if (isSend && type != null && "post".equalsIgnoreCase(type)) {
                        //机构
                        List childlist = element.getChildren();
                        if (childlist != null && childlist.size() > 0) {
                            Iterator iter = childlist.iterator();
                            while (iter.hasNext()) {
                                Element elementC = (Element) iter.next();
                                String pass = elementC.getAttributeValue("pass");
                                if (pass != null && "0".equals(pass)) {
                                    isCorrect = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isCorrect) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
            System.out.println(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
            e.printStackTrace();
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }

    /**
     *
     * @param conn
     * @param destTab
     * @param i
     * @return
     */
    private boolean updateFlag(Connection conn, String destTab, Iterator i) {

        Element element = null;
        ContentDAO dao = new ContentDAO(conn);
        boolean isCorrect = true;
        String isMorrow = SystemConfig.getPropertyValue("is_morrow");
        while (i.hasNext()) {
            element = (Element) i.next();
            String flag = element.getAttributeValue("flag");
            if (flag == null || flag.length() <= 0) {
                continue;
            }
            String texts = element.getText();
            if (texts == null || texts.length() <= 0) {
                continue;
            }
            System.out.println("$$$$$$$--" + flag + "----" + texts);
            String iDs[] = texts.split(",");
            StringBuffer sql = new StringBuffer();
            if ("1".equals(flag)) {
                sql.append("update " + destTab + " set " + this.bean.get("sys_id") + "=0 where (" + this.bean.get("sys_id") + "=1 or " + this.bean.get("sys_id") + "=2)");
            } else {
                sql.append("update " + destTab + " set " + this.bean.get("sys_id") + "=0 where " + this.bean.get("sys_id") + "=" + flag + " ");
            }
            if ("t_hr_view".equals(destTab)) {
                String hrOnly = SystemConfig.getPropertyValue("sync_hr_only_field");
                if ("true".equalsIgnoreCase(isMorrow)) {
                    sql.append(" and unique_id=?");
                } else if (hrOnly != null && hrOnly.length() > 0) {
                    sql.append(" and " + hrOnly + "=?");
                } else {
                    sql.append(" and " + this.hr_only_field + "=?");
                }
            } else if ("t_org_view".equals(destTab)) {
                String only = SystemConfig.getPropertyValue("sync_orgpost_only_field");

                if ("true".equalsIgnoreCase(isMorrow)) {
                    sql.append(" and unique_id=?");
                } else if (only != null && only.trim().length() > 0) {
                    sql.append(" and " + only + "=?");
                } else {
                    sql.append(" and corcode=?");
                }
            } else if ("t_post_view".equals(destTab)) {
                String only = SystemConfig.getPropertyValue("sync_orgpost_only_field");
                if ("true".equalsIgnoreCase(isMorrow)) {
                    sql.append(" and unique_id=?");
                } else if (only != null && only.trim().length() > 0) {
                    sql.append(" and " + only + "=?");
                } else {
                    sql.append(" and corcode=?");
                }
            } else {
                break;
            }
            ArrayList alllist = new ArrayList();
            for (int r = 0; r < iDs.length; r++) {
                if (iDs[r] != null && iDs.length > 0) {
                    ArrayList id_list = new ArrayList();
                    id_list.add(iDs[r]);
                    alllist.add(id_list);
                }
            }
            System.out.println(sql.toString());
            System.out.println(alllist);
            try {
                dao.batchUpdate(sql.toString(), alllist);
            } catch (SQLException e) {
                isCorrect = false;
                Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
                System.out.println(bean.get("sys_name") + "系统接口返回信息后同步标记时出错");
            }
        }
        return isCorrect;
    }

    /**
     * 错误日志
     *
     * @param doc
     */
    private boolean failinfo(Connection conn, Document doc) throws GeneralException {
        String str_path = "/msg/info";
        XPath xpath;
        boolean isCorrect = true;
        try {
            xpath = XPath.newInstance(str_path);
            List childlist = xpath.selectNodes(doc);
            Element element = null;
            String info = "";
            if (childlist != null && childlist.size() > 0) {
                element = (Element) childlist.get(0);
                info = element.getText();
                if (info != null && info.length() > 0) {
                    Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "系统接口返回错误信息：" + info);
                    System.out.println(bean.get("sys_name") + "系统接口返回错误信息：" + info);
                    isCorrect = false;
//					updateFailtime(conn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }

    private void failinfo(Connection conn, String info) throws GeneralException {
        Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_name") + "系统接口错误信息：" + info);
        System.out.println(bean.get("sys_name") + "系统接口返回错误信息：" + info);
        updateFailtime(conn);

    }

    /**
     * 返回日志错误信息，失败次数加一
     */
    private void updateFailtime(Connection conn) {
        ContentDAO dao = new ContentDAO(conn);
        String sql = "update t_sys_outsync set fail_time=fail_time+1 where sys_id='" + this.bean.get("sys_id") + "'";
        try {
            dao.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步查询条件
     *
     * @return
     */
    private String getSyncWhere(String type) {
        String tempStr = "";
        String tempValue = "";
        if (this.bean.get("other_param") != null) {
            String other_param = (String) this.bean.get("other_param");
            if (other_param != null && other_param.length() > 20) {
                try {
                    org.dom4j.Document doc = DocumentHelper.parseText(other_param);
                    org.dom4j.Element root = doc.getRootElement();
                    for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                        org.dom4j.Element element = (org.dom4j.Element) it.next();
                        String tempName = element.attributeValue("name");
                        tempName = tempName == null ? "" : tempName;
                        if (type.equalsIgnoreCase(tempName)) {
                            tempValue = element.getText();
                        }
                    }
                    tempStr = getSearchWhere(type.toLowerCase(), tempValue);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Category.getInstance("com.hjsj.hrms.businessobject.sys.job.SysoutSyncThread").error(bean.get("sys_name") + "系统接口错误信息：" + e.getMessage());
                    return "";
                }
            }
        }
        return tempStr;
    }

    /**
     * 条件查询解析 LiWeichao
     *
     * @param search
     * @return
     */
    private String getSearchWhere(String type, String search) throws Exception {
        StringBuffer wherestr = new StringBuffer();
        search = PubFunc.keyWord_reback(search);
        if (search != null && search.trim().length() > 0) {
            String searcharr[] = search.split(",");
            if (searcharr.length == 3) {
                String sexpr = searcharr[1];
                String sfactor = searcharr[2];
                boolean blike = false;
                blike = searcharr[0] != null && "1".equals(searcharr[0]) ? true : false;
                String strSFACTOR = sfactor;
                sfactor = "";
                String strItem[] = strSFACTOR.split("`");
                String xpr = "";
                for (int i = 0; i < strItem.length; i++) {
                    String item = strItem[i] + " ";
                    String code = "";
                    if (item.indexOf("<>") != -1) {
                        code = "<>";
                    } else if (item.indexOf(">=") != -1) {
                        code = ">=";
                    } else if (item.indexOf("<=") != -1) {
                        code = "<=";
                    } else if (item.indexOf(">") != -1) {
                        code = ">";
                    } else if (item.indexOf("<") != -1) {
                        code = "<";
                    } else {
                        code = "=";
                    }

                    String emp[] = item.split(code);
                    FieldItem field = DataDictionary.getFieldItem(emp[0]);
                    //设置中字段别名
                    if ("A".equalsIgnoreCase(field.getItemtype())) {
                        //xus 19/4/26 数据视图人员过滤范围可以选择代码型指标
                        if ("UM".equals(field.getCodesetid()) || "UN".equals(field.getCodesetid()) || "@K".equals(field.getCodesetid())) {
                            emp[0] = emp[0] + "_0";
                        }
                    } else {
                        emp[0] = getAppAttribute(type, emp[0]);
                    }

                    if (emp[0] == null || emp[0].length() < 1) {
                        continue;
                    }
                    if (blike) {
                        if ("D".equalsIgnoreCase(field.getItemtype())) {
                            wherestr.append(xpr + emp[0] + code + Sql_switcher.dateValue(emp[1].trim()));
                        } else if ("N".equalsIgnoreCase(field.getItemtype())) {
                            emp[1] = emp[1].trim() == null || emp[1].trim().length() < 1 ? "0" : emp[1].trim();
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "0") + code + emp[1]);
                        } else if ("A".equalsIgnoreCase(field.getItemtype()) && "0".equals(field.getCodesetid())) {
                            if ("<>".equalsIgnoreCase(code)) {
                                wherestr.append(xpr + Sql_switcher.isnull(emp[0], "''") + " not like '%" + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + emp[0] + " like '%" + emp[1].trim() + "%'");
                            }
                        } else {
                            if ("<>".equalsIgnoreCase(code)) {
                                wherestr.append(xpr + emp[0] + " not like '" + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + emp[0] + " like '" + emp[1].trim() + "%'");
                            }
                        }
                    } else {
                        if ("N".equalsIgnoreCase(field.getItemtype())) {
                            emp[1] = emp[1].trim() == null || emp[1].trim().length() < 1 ? "0" : emp[1].trim();
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "0") + code + emp[1]);
                        } else if ("D".equalsIgnoreCase(field.getItemtype())) {
                            wherestr.append(xpr + emp[0] + code + Sql_switcher.dateValue(emp[1].trim()));
                        } else {
                            //xus 17/11/30 oracle 语句中<> '' 无法查询出数据
                            String whereRiSql = "".equals(emp[1].trim()) ? " " : emp[1].trim();
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "''") + code + "'" + whereRiSql + "'");
                        }
                    }
                    int temp = sexpr.indexOf((i + 1) + "") + String.valueOf(i + 1).length();//下一个的位数
                    if (sexpr.substring(sexpr.indexOf((i + 1) + "")) != null
                            && sexpr.substring(temp).length() > 0) {
                        xpr = sexpr.substring(temp, temp + 1);
                        if ("+".equals(xpr)) {
                            xpr = " OR ";
                        } else if ("*".equals(xpr)) {
                            xpr = " AND ";
                        }
                    } else {
                        xpr = "";
                    }
                }
            }
        }
        return wherestr.toString();
    }

    private String getAppAttribute(String name, String attribute) throws Exception {
        String ret = "";
        if (!"".equals(name)) {

            RecordVo vo = ConstantParamter.getConstantVo("SYS_EXPORT_VIEW");
            String str_path = "/root/" + name;
            XPath xpath = XPath.newInstance(str_path);
            List childlist = xpath.selectNodes(PubFunc.generateDom(vo.getString("str_value")));
            Element element = null;
            for (int i = 0; i < childlist.size(); i++) {
                element = (Element) childlist.get(i);
                if (attribute.equalsIgnoreCase(element.getAttributeValue("name"))) {
                    ret = element.getText();
                }
            }
        }
        return ret;
    }

    public String getOutinfo() {
        return outinfo;
    }
}
