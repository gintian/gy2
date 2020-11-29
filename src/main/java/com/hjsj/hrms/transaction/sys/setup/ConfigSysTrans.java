package com.hjsj.hrms.transaction.sys.setup;

import com.hjsj.hrms.businessobject.sys.setup.TomcatCommand;
import com.hjsj.hrms.businessobject.sys.setup.findfile.parse.ParseServerxml;
import com.hjsj.hrms.businessobject.sys.setup.findfile.parse.ParseSystemConf;
import com.hjsj.hrms.businessobject.sys.setup.findfile.parse.ParseWebxml;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ConfigSysTrans extends IBusiness {

    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        HashMap hm = this.getFormHM();
        String flag = (String) hm.get("flag");
        if (flag == null) {
            String validateflag = "false";
            String scrollwelcome = "false";
            try {
                validateflag = SystemConfig.getProperty("validatecode");
                scrollwelcome = SystemConfig.getProperty("scrollwelcome");
                if (validateflag == null || "false".equalsIgnoreCase(validateflag)) {
                    validateflag = "false";
                }
                if (scrollwelcome == null || "false".equalsIgnoreCase(scrollwelcome)) {
                    scrollwelcome = "false";
                }
            } catch (Exception e) {
                validateflag = "false";
            }
            String hjserverurl = SystemConfig.getProperty("hrpserver");
            String hjserverport = SystemConfig.getProperty("port");
            String dbtype = SystemConfig.getProperty("dbserver");
            String dburl = SystemConfig.getProperty("dbserver_addr");
            String dbport = SystemConfig.getProperty("dbserver_port");
            String dbuser = SystemConfig.getProperty("db_user");
            String dbpassword = SystemConfig.getProperty("db_user_pwd");
            String dbname = SystemConfig.getProperty("dbname");
            hm.put("hjserverurl", hjserverurl);
            hm.put("hjserverport", hjserverport);
            hm.put("validateflag", validateflag);
            hm.put("scrollwelcome", scrollwelcome);
            hm.put("dbtype", dbtype);
            hm.put("dburl", dburl);
            hm.put("dbport", dbport);
            hm.put("dbuser", dbuser);
            hm.put("dbpassword", dbpassword);
            hm.put("selstr", this.getSelstr(dbtype));
            hm.put("dbname", dbname);
        } else {
//			修改相关文件，并重新启动tomcat
            try {
                String validateflag = (String) hm.get("validateflag");
                if ("on".equals(validateflag)) {
                    validateflag = "true";
                } else {
                    validateflag = "false";
                }
                String scrollwelcome = (String) hm.get("scrollwelcome");
                if ("on".equals(scrollwelcome)) {
                    scrollwelcome = "true";
                } else {
                    scrollwelcome = "false";
                }
                String hjserverurl = (String) hm.get("hjserverurl");
                String hjserverport = (String) hm.get("hjserverport");
                String dbtype = (String) hm.get("dbtype");
                String dburl = (String) hm.get("dburl");
                String dbport = (String) hm.get("dbport");
                String dbuser = (String) hm.get("dbuser");
                String dbpassword = (String) hm.get("password");
                String dbname = (String) hm.get("dbname");
                HashMap prohm = new HashMap();
                prohm.put("hrpserver", hjserverurl);
                prohm.put("port", hjserverport);
                prohm.put("dbserver", dbtype);
                prohm.put("dbserver_addr", dburl);
                prohm.put("dbserver_port", dbport);
                prohm.put("db_user", dbuser);
                prohm.put("db_user_pwd", dbpassword);
                prohm.put("validatecode", validateflag);
                prohm.put("scrollwelcome", scrollwelcome);
                prohm.put("dbname", dbname);
                ParseSystemConf.savaOrupdateProperty(prohm);
                ParseWebxml.updateSessionTime((String) hm.get("sessiontime"));
                String url = "";
                if ("mssql".equalsIgnoreCase(dbtype)) {
                    url = getUrlprefix(dbtype) + dburl + ":" + dbport + ";databaseName=" + dbname;
                }
                if ("oracle".equalsIgnoreCase(dbtype)) {
                    url = getUrlprefix(dbtype) + dburl + ":" + dbport + ":" + dbname;
                }
                if ("db2".equalsIgnoreCase(dbtype)) {
                    url = getUrlprefix(dbtype) + dburl + ":" + dbport + "/" + dbname;
                }
                ParseServerxml.updateDBpool(dbuser, dbpassword, null, url, this.getdriver(dbtype));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("error");
            }
            TomcatCommand.Command("restart");
        }
    }

    private String getUrlprefix(String dbtype) {
        if ("mssql".equalsIgnoreCase(dbtype)) {
            return "jdbc:sqlserver://";
        }
        if ("db2".equalsIgnoreCase(dbtype)) {
            return "jdbc:db2://";
        }
        if ("oracle".equalsIgnoreCase(dbtype)) {
            return "jdbc:oracle:thin:@";
        }
        return null;
    }

    private String getdriver(String dbtype) {
        if ("mssql".equalsIgnoreCase(dbtype)) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        if ("db2".equalsIgnoreCase(dbtype)) {
            return "com.ibm.db2.jcc.DB2Drive";
        }
        if ("oracle".equalsIgnoreCase(dbtype)) {
            return "oracle.jdbc.driver.OracleDriver";
        }
        return null;
    }

    private String getSelstr(String dbtype) {
        StringBuffer sbselstr = new StringBuffer();
        sbselstr.append("<select name=\"dbtype\">");
        if ("mssql".equalsIgnoreCase(dbtype)) {
            sbselstr.append("<option value=\"mssql\" selected=\"selected\">SQL SERVER");
        } else {
            sbselstr.append("<option value=\"mssql\">SQL SERVER");
        }
        sbselstr.append("</option>");
        if ("oracle".equalsIgnoreCase(dbtype)) {
            sbselstr.append("<option value=\"oracle\" selected=\"selected\">ORACLE");
        } else {
            sbselstr.append("<option value=\"oracle\" >ORACLE");
        }
        sbselstr.append("</option>");
        if ("db2".equalsIgnoreCase(dbtype)) {
            sbselstr.append("<option value=\"db2\" selected=\"selected\">DB2");
        } else {
            sbselstr.append("<option value=\"db2\">DB2");
        }

        sbselstr.append("</option>");

        return sbselstr.toString();
    }

}
