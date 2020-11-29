package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.team.ArrayEmpOrganizationXml;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ArrayLoadEmpTree extends HttpServlet {

    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String params = (String) req.getParameter("params");
        String action = (String) req.getSession().getAttribute("SYS_LOAD_ORG_ACTION");
        String target = (String) req.getParameter("target");
        String flag = (String) req.getParameter("flag");
        String dbtype = (String) req.getParameter("dbtype");
        String id = (String) req.getParameter("id");
        String priv = (String) req.getParameter("priv");
        String emp_flag = (String) req.getParameter("emp_flag");
        String kqtype = (String) req.getParameter("kqtype");
        String first = (String) req.getParameter("first");

        KqParameter para = new KqParameter();
        boolean isPost = true;
        if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
            isPost = false;
        } else {
            isPost = true;
        }

        if (emp_flag == null || emp_flag.length() <= 0) {
            emp_flag = "0";
        }
        
        if (priv == null || "".equals(priv))
            priv = "1";
        
        ArrayEmpOrganizationXml orgxml = new ArrayEmpOrganizationXml(params, action, target, flag, dbtype, priv, emp_flag,
                kqtype, isPost);
        
        if (first == null || "".equalsIgnoreCase(first))
            orgxml.setBfirst(false);
        else
            orgxml.setBfirst(true);
        
        try {
            UserView userview = (UserView) req.getSession().getAttribute(WebConstant.userView);
            String xmlc = orgxml.outOrgEmployTree(userview, id);
            resp.setContentType("text/xml;charset=UTF-8");
            resp.getWriter().println(xmlc);
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }
}
