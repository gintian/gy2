package com.hjsj.hrms.module.serviceclient.servlet;

import com.hjsj.hrms.module.serviceclient.serviceSetting.GetPasswordTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Servlet implementation class ServiceClientGetPasswordServlet
 */
public class ServiceClientGetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceClientGetPasswordServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 用户输入校验码
            String userValidate = SafeCode.decode(request.getParameter("validatecode"));
            // 系统生成校验码
            String sysValidate = (String) request.getSession().getAttribute("validatecode");
            // 获取完成后销毁校验码
            request.getSession().removeAttribute("validatecode");
            String msg = null;
            if (userValidate == null || userValidate.length() == 0) {
                return;
            } else if (!sysValidate.equalsIgnoreCase(userValidate)) {
                // 请输入正确的校验码！
                msg = "10";
                this.write(msg, response);
                return;
            }
            this.getPasswordLogin(request, response, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @Title: getPasswordLogin   
     * @Description: 自助终端找回密码
     * @param request
     * @param response
     * @param msg
     * @throws ServletException
     * @throws IOException 
     * @return void
     */
    private void getPasswordLogin(HttpServletRequest request, HttpServletResponse response,
            String msg) throws ServletException, IOException {
        String logintype = SafeCode.decode(request.getParameter("logintype"));
        String type = SafeCode.decode(request.getParameter("type"));
        Des des= new Des();
        String ZE = SafeCode.decode(request.getParameter("ZE"));
        ZE=des.DecryPwdStr(ZE);
        try {
                msg = this.getPasswordLoginMsg(logintype, type, ZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.write(msg, response);
        // 发起刷新请求
//        response.sendRedirect(url.toString());
    }
    /**
     * 
     * @Title: getPasswordLoginMsg   
     * @Description:    
     * @param logintype 用户登录平台=1业务=2自助
     * @param type 找回密码方式=1根据电话=2根据邮箱
     * @param ZE 电话或邮箱
     * @return String
     * @throws GeneralException 
     */
    private String getPasswordLoginMsg(String logintype, String type, String ZE) throws GeneralException{
        RowSet frowset = null;
        Connection connection = null;
        // 利用原生GetPasswordTrans执行相应请求
        GetPasswordTrans getPasswordTrans = new GetPasswordTrans(); 
        try {   
            // 初始化核心参数
            getPasswordTrans.setFormHM(new HashMap());
            connection = AdminDb.getConnection();
            getPasswordTrans.setFrameconn(connection);
            getPasswordTrans.setFrowset(frowset);
            // 填充参数
            getPasswordTrans.getFormHM().put("logintype", logintype);
            getPasswordTrans.getFormHM().put("type", type);
            getPasswordTrans.getFormHM().put("ZE", ZE);
            // 发起请求
            getPasswordTrans.execute();
        } catch (Exception e) {     
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(frowset);
            PubFunc.closeResource(connection);
        }
        return (String) getPasswordTrans.getFormHM().get("msg");
    }
    private void write (String msg,HttpServletResponse response) {
        // 执行完毕刷新页面
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",msg);
//        response.setContentType("text/html,charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(jsonObject.toString()); 
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(out);
        }  
        
    }
}
