package com.hjsj.hrms.servlet.lawbase;

import com.hjsj.hrms.interfaces.lawbase.GetLawbaseDirectoryByXml;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * <p>Title:GetLawbaseDir</p>
 * <p>Description:查询规章制度目录结构</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 1, 2005:10:29:28 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class GetLawbaseDirServlet extends HttpServlet {

    /* 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException {
        doPost(arg0, arg1);
    }
    /* 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String params=req.getParameter("params");
        String target=req.getParameter("target");
        /**
         * 取第一级目录
         */
        try
        {
           GetLawbaseDirectoryByXml lawxml=new GetLawbaseDirectoryByXml(target,params);
           String xml_c=lawxml.outPutDirectoryXml();
           resp.setContentType("text/xml;charset=gb2312");
           resp.getWriter().println(xml_c);
        }
        catch(GeneralException ge)
        {
            ge.printStackTrace();
        }
    }
}
