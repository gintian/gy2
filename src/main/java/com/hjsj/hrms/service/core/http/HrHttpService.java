package com.hjsj.hrms.service.core.http;

import com.hjsj.hrms.utils.PubFunc;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HrHttpService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public HrHttpService() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取请求URI
		String requestURL = request.getRequestURI();
		//针对该servlet截取请求的方法名
		String method = requestURL.substring(15);
		
		if("updInfoState".equalsIgnoreCase(method)) {//如果方法名为更新同步状态返回错误提示
			String jsonStr = this.returnJsonStr("0","请使用Post请求");
			this.returnResponse(response, jsonStr);
		}else if ("syncProcess".equalsIgnoreCase(method)) {//如果方法名为更新子集返回错误提示
			String jsonStr = this.returnJsonStr("0","该方法不支持Get方式，请使用Post方式");
			this.returnResponse(response, jsonStr);
		}else {
			this.doPost(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取请求URI
		String requestURL = request.getRequestURI();
		//针对该servlet截取请求的方法名
		String method = requestURL.substring(15);
		String sysEtoken = request.getParameter("sysEtoken");
		String sqlWhere = request.getParameter("sqlWhere");
		if (StringUtils.isNotBlank(sqlWhere)){
			sqlWhere =PubFunc.keyWord_reback(sqlWhere);
		}
		if("getSysEtoken".equalsIgnoreCase(method)) {
			String sysCode = request.getParameter("sysCode");
			String result = this.getSysEtoken(sysCode);
			this.returnResponse(response,result);
		}else if ("getOrgInfo".equalsIgnoreCase(method)) {
			String result = this.getOrgInfo(sysEtoken, sqlWhere);
			this.returnResponse(response,result);
		}else if ("getUserInfo".equalsIgnoreCase(method)) {
			String result = this.getUserInfo(sysEtoken, sqlWhere);
			this.returnResponse(response,result);
		}else if ("getPostInfo".equalsIgnoreCase(method)) {
			String result = this.getPostInfo(sysEtoken,sqlWhere);
			this.returnResponse(response,result);
		}else if ("updInfoState".equalsIgnoreCase(method)) {
			String jsonStr = PubFunc.keyWord_reback(request.getParameter("jsonStr"));//需要处理的数据,将特殊字符还原
			String onlyFiled = request.getParameter("onlyFiled");//唯一标识(默认为unique_id)
			String sysFlag = request.getParameter("sysFlag");//系统标识，由eHR系统提供如AD,OA(默认是flag)
			String type = request.getParameter("type");//"ORG" 代表机构，"HR"代表人员，"POST"代表岗位
			String result = this.updInfoState(sysEtoken, jsonStr, onlyFiled, sysFlag, type);
			this.returnResponse(response,result);
		}else if ("getBoardJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getBoardJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getMatterJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getMatterJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getStaticsJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getStaticsJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getReportJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getReportJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getWarnJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getWarnJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getKqInfoJson".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getKqInfoJson(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getUserEtoken".equalsIgnoreCase(method)) {
			String userName  = request.getParameter("userName");
			String result = this.getUserEtoken(sysEtoken,userName,request);
			this.returnResponse(response,result);
		}else if ("getHolidayMsg".equalsIgnoreCase(method)) {
			//String jsonStr = new String(request.getParameter("jsonStr").getBytes("iso8859-1"),"GBK");
			String jsonStr  = PubFunc.keyWord_reback(request.getParameter("jsonStr"));
			String result = this.getHolidayMsg(sysEtoken, jsonStr);
			this.returnResponse(response,result);
		}else if ("updateHolidays".equalsIgnoreCase(method)) {
			String jsonStr  = PubFunc.keyWord_reback(request.getParameter("jsonStr"));
			String result = this.updateHolidays(sysEtoken, jsonStr);
			this.returnResponse(response,result);
		}else if ("syncProcess".equalsIgnoreCase(method)) {
			String xml  = PubFunc.keyWord_reback(request.getParameter("xmlStr"));
			String type  = request.getParameter("type");
			String result = this.syncProcess(sysEtoken, type, xml);
			this.returnResponse(response,result);
		}else {
			String jsonStr = this.returnJsonStr("0","请求的方法不存在!");
			this.returnResponse(response,jsonStr);
		}
	}

	/**
	 * 获取动态认证码
	 * @param sysCode
	 * @return
	 */
	private String getSysEtoken(String sysCode) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("1", sysCode);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取动态认证码:");
		}
		result = bo.getSysEtoken(sysCode);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取动态认证码结束。");
		}
		return result;
	}
	

	/**
	 * 获取有变动（新增、删除、更新）的机构信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */
	private String getOrgInfo(String sysEtoken, String sqlWhere) {
		String result = "";
		if (StringUtils.isBlank(sqlWhere)) {
			sqlWhere="";
		}
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息:");
		}
		result = bo.getOrgInfo(sysEtoken, sqlWhere);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取机构变动信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取有变动（新增、删除、更新）的岗位信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */
	private String getPostInfo(String sysEtoken, String sqlWhere) {
		String result = "";
		if (StringUtils.isBlank(sqlWhere)) {
			sqlWhere="";
		}
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位变动信息:");
		}
		result = bo.getPostInfo(sysEtoken, sqlWhere);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取岗位变动信息结束。");
		}
		return result;
	}
	
	
	/**
	 * 获取有变动（新增、删除、更新）的人员信息数据
	 * @param sysEtoken 认证标识/动态码
	 * @param sqlWhere sql条件
	 * @return
	 */
	private String getUserInfo(String sysEtoken, String sqlWhere) {
		String result = "";
		if (StringUtils.isBlank(sqlWhere)) {
			sqlWhere="";
		}
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息:");
		}
		result = bo.getUserInfo(sysEtoken, sqlWhere);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取人员变动信息结束。");
		}
		return result;
	}

	
	/**
	 * 更新数据同步状态
	 * @param sysEtoken 认证标识
	 * @param jsonStr 需要处理的数据
	 * @param onlyFiled 唯一标识(默认为unique_id)
	 * @param sysFlag 系统标识，由eHR系统提供如AD,OA(默认是flag)
	 * @param type "ORG" 代表机构，"HR"代表人员，"POST"代表岗位
	 * @return XML格式的处理状态
	 */
	private String updInfoState(String sysEtoken,String jsonStr,String onlyFiled,String sysFlag,String type) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新数据同步状态:");
		}
		result = bo.updInfoState(sysEtoken, jsonStr, onlyFiled, sysFlag, type);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新数据同步状态结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的公告信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getBoardJson(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取公告信息:");
		}
		result = bo.getBoardJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取公告信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的待办信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getMatterJson(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取待办信息:");
		}
		result = bo.getMatterJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取待办信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的常用统计信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getStaticsJson(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取常用统计信息:");
		}
		result = bo.getStaticsJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取常用统计信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的报表信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getReportJson(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取报表信息:");
		}
		result = bo.getReportJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取报表信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取当前人员的预警信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getWarnJson(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息:");
		}
		result = bo.getWarnJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息结束。");
		}
		return result;
	}
	
	/**
	 * 获取考勤（请假、加班、公出）报批信息
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getKqInfoJson(String sysEtoken,String userName,HttpServletRequest request){
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息:");
		}
		result = bo.getKqInfoJson(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取预警信息结束。");
		}
		return result;
		
	}
	
	/**
	 * 获取用户标识
	 * @param sysEtoken 认证码
	 * @param userName 用户名
	 * @return
	 */
	private String getUserEtoken(String sysEtoken,String userName,HttpServletRequest request) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取用户标识:");
		}
		result = bo.getUserEtoken(sysEtoken, userName, request);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取用户标识结束。");
		}
		return result;
	}

	/**
	 * 更新信息集
	 * @param sysEtoken 认证码
	 * @param type 流程标志
	 * @param xml XML格式的数据,详情查看白皮书
	 * @return
	 */
	private String syncProcess(String sysEtoken,String type,String xmlStr) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新子集信息:");
		}
		result = bo.syncProcess(sysEtoken, type, xmlStr);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新子集信息结束。");
		}
		return result;
	}
	
	
	/**
	 * 获取年假已休、可休天数
	 * @param sysEtoken 认证码
	 * @param jsonStr json格式的数据
	 * @return Xml格式的数据其中包含年假假期（可休、已休天数）
	 */
	private String getHolidayMsg(String sysEtoken,String jsonStr) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取年假已休、可休天数:");
		}
		result = bo.getHolidayMsg(sysEtoken, jsonStr);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("获取年假已休、可休天数结束。");
		}
		return result;
	}
	
	/**
	 * 更新年假天数
	 * @param sysEtoken 认证码
	 * @param jsonStr json格式的数据
	 * @return 
	 */
	private String updateHolidays(String sysEtoken,String jsonStr) {
		String result = "";
		HttpServiceBo bo = new HttpServiceBo("", sysEtoken);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新年假天数:");
		}
		result = bo.updateHolidays(sysEtoken, jsonStr);
		if (bo.getRegLogger() != null) {
			bo.getRegLogger().start("更新年假天数结束。");
		}
		return result;
	}
	
	
	/**
	 * 返回json格式的字符串
	 * @param opt 选项值 0:错误信息,1:数据信息
	 * @param msg 
	 */
	private String returnJsonStr(String opt,Object msg) {
	    JSONObject json= new JSONObject();
	    json.put("flag",opt);
	    if("0".equals(opt)) {
	    	json.put("msg",msg);
	    }else if("1".equals(opt)) {
	    	json.put("data",msg);
	    }
	    return json.toString();
	}
	
	/**
	 * 返回json格式的数据
	 * @param response 响应对象
	 * @param opt 选项值 0:错误信息,1:数据信息
	 * @param msg
	 */
	private void returnResponse(HttpServletResponse response,String msg) {
		//设置编码格式 
	    response.setContentType("text/plain;charset=UTF-8"); 
	    response.setCharacterEncoding("GBK");
	    PrintWriter out = null; 
	    try{ 
	      out = response.getWriter(); 
	      out.write(msg); 
	      out.flush(); 
	    }catch(IOException e){
	      e.printStackTrace(); 
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    } 
	}
}
