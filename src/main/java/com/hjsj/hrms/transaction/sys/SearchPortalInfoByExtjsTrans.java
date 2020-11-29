package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @Title: SearchPortalInfoByExtjsTrans.java
 * @Description: 页面展现交易类
 * @Company: hjsj
 * @Create time: 2015-9-24 下午03:02:02
 * @author chenxg
 * @version 1.0
 */
public class SearchPortalInfoByExtjsTrans extends IBusiness {

    public void execute() throws GeneralException {

        // 页面编号
        String pageid = (String) this.getFormHM().get("pageid");
        String parms = "";
        if (pageid == null || pageid.length() < 1) {
            String url = (String) this.getFormHM().get("urlpath");
            String parameter = url.substring(url.indexOf("?") + 1);
            String[] parameters = parameter.split("&");
            for (int i = 0; i < parameters.length; i++) {
                String param = parameters[i];
                if (param != null && param.startsWith("pageid=")) {
                    String[] pageparam = param.split("=");
                    if (pageparam != null && pageparam.length == 2)
                        pageid = pageparam[1];
                } else if (param != null && !param.startsWith("br_") && !param.startsWith("b_")) {
                    parms += "&" + param;
                }
            }

            if (pageid == null || pageid.length() < 1)
                pageid = "1";
        }

        Object pid = (Object) this.formHM.get("parentid");
        String parentid = "";

        if (pid != null)
            parentid = pid + "";

        Object lay = this.formHM.get("layout");
        String layout = "";
        if (lay != null)
            layout = lay + "";

        ArrayList<String> panellist = new ArrayList<String>();

        ContentDAO dao = new ContentDAO(this.frameconn);
        StringBuffer sql = new StringBuffer();
        sql.append("select pageId,regionId,name,layout,width,height,contentType,content,jumpUrl,parentRegionId");
        sql.append(" from t_sys_page_region  where pageId=" + pageid);
        if (parentid != null && parentid.trim().length() > 0) {
            sql.append(" and parentRegionId =" + parentid);
            sql.append(" and parentRegionId <> regionId");
        } else
            sql.append(" and parentRegionId = regionId");

        sql.append(" order by pageid,regionid");

        try {
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                StringBuffer panel = new StringBuffer("{");
                // 区域id
                String regionId = this.frowset.getString("regionId");
                panel.append("'regionid':" + regionId);
                // 父区域id
                String parentRegionId = this.frowset.getString("parentRegionId");
                panel.append(",'parentRegionid':" + parentRegionId);
                // 区域名称
                String name = this.frowset.getString("name");
                name = PubFunc.nullToStr(name).trim();
                panel.append(",'name':'" + name + "'");
                // 区域的上外边距
                String layOut = this.frowset.getString("layout");
                panel.append(",'layout':" + layOut);
                // 区域的宽度
                String width = this.frowset.getString("width");
                panel.append(",'width':" + width);
                // 区域的高度
                String height = this.frowset.getString("height");
                panel.append(",'height':" + height);
                // 区域内容的类型 =0：文字；=1链接
                String contentType = this.frowset.getString("contentType");
                panel.append(",'contentType':" + contentType);
                // 区域的内容
                String content = this.frowset.getString("content");
                content = PubFunc.nullToStr(content).trim();
                if (content != null && content.length() > 1)
                    content = content.replaceAll("`", "&");

                panel.append(",'content':'" + content + "'");
                // 区域的穿透要跳转的链接
                String jumpUrl = this.frowset.getString("jumpUrl");
                jumpUrl = PubFunc.nullToStr(jumpUrl).trim();
                if (jumpUrl != null && jumpUrl.length() > 1)
                    jumpUrl = jumpUrl.replaceAll("`", "&");

                panel.append(",'jumpUrl':'" + jumpUrl + "'}");
                panellist.add(panel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        this.formHM.put("panellist", panellist);
        this.formHM.put("parms", parms);
        this.formHM.put("layout", layout);
        this.formHM.put("pageid", pageid);
    }

}
