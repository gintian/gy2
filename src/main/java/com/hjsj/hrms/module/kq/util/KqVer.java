package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 考勤版本类：判断当前系统正在使用的是新还是老考勤
 * 
 * @author zhaoxj
 *
 */
public class KqVer {
    Category cat = Category.getInstance(getClass());

    /**
     * 考勤版本：0：老考勤；1：新考勤
     */
    private static int version = -1;

    /**
     * 得到当前使用的考勤版本
     * @return 
     */
    public int getVersion() {
        return version;
    }

    public KqVer() {
        getKqVer();
    }

    private void getKqVer() {
        if (version > -1) {
            return;
        }

        // 优先从system.properties中判断版本
        String kqVer = SystemConfig.getPropertyValue("kq_ver");
        if (StringUtils.isNotBlank(kqVer)) {
            if (cat.isDebugEnabled()) {
                cat.debug("从system.properties中获得考勤版本信息kq_ver=" + kqVer);
            }

            if (kqVer.equalsIgnoreCase(String.valueOf(KqConstant.Version.STANDARD))) {
                version = KqConstant.Version.STANDARD;
            } else if (kqVer.equalsIgnoreCase(String.valueOf(KqConstant.Version.UNIVERSITY_HOSPITAL))) {
                version = KqConstant.Version.UNIVERSITY_HOSPITAL;
            }
        }

        if (version > -1) {
            return;
        }

        version = getVerFromMenuXml();
    }

    /**
     * 从menu.xml中判断考勤版本
     * @return 如果启用了考勤管理模块（mod_id=22),那么就认为是新考勤，否则，老考勤
     */
    private int getVerFromMenuXml() {
        int ver = KqConstant.Version.STANDARD;

        // 从mneu.xml中判断版本
        String file = this.getPath();
        if (cat.isDebugEnabled()) {
            cat.debug("判断考勤版获取menu.xml路径为：" + file);
        }

        Document doc = null;
        String entryName = "com/hjsj/hrms/constant/menu.xml";
        InputStream in = null;
        JarFile jf = null;
        try {
            String webserver = SystemConfig.getPropertyValue("webserver");
            if ("jboss".equalsIgnoreCase(webserver) || "inforsuite".equalsIgnoreCase(webserver)) {
                in = SystemConfig.class.getResourceAsStream("/com/hjsj/hrms/constant/menu.xml");
            } else {
                if (file.indexOf("hrpweb3.jar") != -1) {
                    jf = new JarFile(file);
                    Enumeration es = jf.entries();
                    while (es.hasMoreElements()) {
                        JarEntry je = (JarEntry) es.nextElement();
                        if (je.getName().equals(entryName)) {
                            in = jf.getInputStream(je);
                            break;
                        }
                    }
                }
            }

            if (in == null) {
                in = new FileInputStream(file);
            }

            doc = PubFunc.generateDom(in);
            Element rootnode = doc.getRootElement();
            List childNodes = rootnode.getChildren();
            Element node = null;
            for (int i = 0; i < childNodes.size() - 1; i++) {
                node = (Element) childNodes.get(i);
                String modId = (String) node.getAttributeValue("mod_id");
                String menuHideString = (String) node.getAttributeValue("menuhide");
                if ("22".equals(modId) && "true".equalsIgnoreCase(menuHideString)) {
                    ver = KqConstant.Version.UNIVERSITY_HOSPITAL;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(jf);

            if (cat.isDebugEnabled()) {
                cat.debug("从menu.xml中判断考勤版本为：" + ver);
            }
        }

        return ver;
    }

    /**
     * 获取menu.xml文件所在路径
     * @return
     */
    private String getPath() {
    	String classPath = "";
        try {
            classPath = this.getClass().getResource("").toString();
            classPath = java.net.URLDecoder.decode(classPath, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        classPath = classPath.replaceAll("hjsj-kq.jar", "hrpweb3.jar");

        Properties props = System.getProperties();
        String sysname = props.getProperty("os.name");

        if (classPath.indexOf("hrpweb3.jar") != -1) {
            int beginIndex = -1, endIndex = -1;
            /** weblogic,环境布署时 */
            if (classPath.startsWith("zip:")) {
                beginIndex = classPath.indexOf("zip:") + 4;
                endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                classPath = classPath.substring(beginIndex, endIndex);
            } else {
                if (sysname.startsWith("Win")) {
                    beginIndex = classPath.indexOf("/") + 1;
                    endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                    classPath = classPath.substring(beginIndex, endIndex);
                } else {
                    beginIndex = classPath.indexOf("/");
                    endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                    classPath = classPath.substring(beginIndex, endIndex);
                }
            }
        } else {
            // zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
            int beginIndex = classPath.indexOf("/");
            if (sysname.startsWith("Win")) {
                beginIndex++;
            }

            if (classPath.indexOf("utils") != -1) {
                int endIndex = classPath.lastIndexOf("utils") - 1;
                String mixpath = "/constant/menu.xml/";
                classPath = classPath.substring(beginIndex, endIndex) + mixpath;
            }
        }

        return classPath;
    }
}
