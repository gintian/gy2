package com.hjsj.hrms.transaction.stat;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.util.Calendar;
import java.util.List;
/**
 * @Title:        SformulaListTrans.java
 * @Description:  统计图联动页面-下拉列表
 * @Company:      hjsj     
 * @Create time:  2015-11-5 下午06:43:09
 * @author        chenxg
 * @version       1.0
 */
public class SformulaListTrans extends IBusiness {

    public void execute() throws GeneralException {
        String url = (String) this.getFormHM().get("urlpath");
        String statid = (String) this.getFormHM().get("statid");
        String pageid = "";
        String parms = "";
        String page = "";

        String parameter = url.substring(url.indexOf("?") + 1);
        String[] parameters = parameter.split("&");
        for (int i = 0; i < parameters.length; i++) {
            String param = parameters[i];
            if (param != null && param.startsWith("pageid=")) {
                String[] pageparam = param.split("=");
                if (pageparam != null && pageparam.length == 2)
                    pageid = pageparam[1];
            } else if (param != null && param.startsWith("statid=")) {
                if (statid == null || statid.length() < 1) {
                    String[] statparam = param.split("=");
                    if (statparam != null && statparam.length == 2)
                        statid = statparam[1];
                }
            } else if (param != null && !param.startsWith("br_") && !param.startsWith("b_")) {
                if (param != null && param.startsWith("page=")) {
                    String[] pageparam = param.split("=");
                    if (pageparam != null && pageparam.length == 2)
                        page = pageparam[1];
                }

                parms += "&" + param;
            }
        }

        parms += "&" + "parentid=" + statid;

        if (pageid == null || pageid.length() < 1)
            pageid = "1";

        SformulaXml xml = new SformulaXml(this.frameconn, statid);
        List list = xml.getAllChildren();
        String showsformula = "0";
        StringBuffer number = new StringBuffer();
        StringBuffer sformula = new StringBuffer("[");
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element element = (Element) list.get(i);
                if (sformula.length() > 1)
                    sformula.append(",");
                String del = element.getAttributeValue("del");
                if("1".equalsIgnoreCase(del)) {
                    number.append("[{'value':'" + element.getAttributeValue("id") + "','name':'" + element.getAttributeValue("title") + "'}]");
                    continue;
                }
                
                if(sformula.indexOf(",") == -1)
                    showsformula = element.getAttributeValue("id");
                
                sformula.append("{'value':'" + element.getAttributeValue("id") + "','name':'" + element.getAttributeValue("title") + "'}");
            }
            sformula.append("]");
            
            if(sformula == null || sformula.length() < 3) {
                sformula = number;
                showsformula = "0";
            }

            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
            StringBuffer yearBuff = new StringBuffer("[");
            yearBuff.append("{'value':'" + year + "','name':'" + year + "'},");
            yearBuff.append("{'value':'" + (year - 1) + "','name':'" + (year - 1) + "'},");
            yearBuff.append("{'value':'" + (year - 2) + "','name':'" + (year - 2) + "'},");
            yearBuff.append("{'value':'" + (year - 3) + "','name':'" + (year - 3) + "'},");
            yearBuff.append("{'value':'" + (year - 4) + "','name':'" + (year - 4) + "'}");
            yearBuff.append("]");
            this.getFormHM().put("sformula", sformula.toString());
            this.getFormHM().put("yearlist", yearBuff.toString());
            this.getFormHM().put("year", year + "");
            this.getFormHM().put("showsformula", showsformula);
        } else {
            this.getFormHM().put("showsformula", "0");
        }

        this.getFormHM().put("pageid", pageid);
        this.formHM.put("parms", parms);
        this.formHM.put("page", page);

    }

}
