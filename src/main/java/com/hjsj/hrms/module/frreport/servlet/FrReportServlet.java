package com.hjsj.hrms.module.frreport.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class FrReportServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public FrReportServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
    public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		response.setContentType("text/html");
		response.setCharacterEncoding("GBK");
		String reportName = request.getParameter("reportName");
		reportName = PubFunc.hireKeyWord_filter_reback(reportName);
		String showType = request.getParameter("showType");
		String privType = request.getParameter("privType");
		privType = StringUtils.isNotEmpty(privType)&&!"null".equals(privType)  ? privType : "U";
		String op = request.getParameter("op"); 
		String urlroot = SystemConfig.getPropertyValue("reportserver");
		//判断是否部署了帆软报表
		boolean isExistFr = true;
		if(urlroot=="") {
			String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/WebReport";
			urlroot = basePath;
		} 
		
		HttpSession session = request.getSession();
		
		JSONObject object=null;
		UserView userView =null;
		userView = (UserView) session.getAttribute("userView");
		
		
		LazyDynaBean bean = new LazyDynaBean();
		if(userView!=null){
			bean.set("username",userView.getUserName());
			bean.set("pawssword",userView.getPassWord());
			object = JSONObject.fromObject(bean);
		}
		HttpClient client = new HttpClient();
		PrintWriter out = response.getWriter();
		
		
		PostMethod post= new PostMethod(urlroot+"/servlet/SetUvSession");
		if(null!=object){
			post.addParameter("userView",PubFunc.encrypt(object.toString()));
		}else{
			out.println("未登录成功！");
			out.flush();
			out.close();
			return;
		}
		client.executeMethod(post);
		post.getResponseBodyAsString();
		//单点登陆，实现报表权限控制
		post = new PostMethod(urlroot+"/ReportServer?op=fs_load&cmd=sso");
		//syl 优先配置参数用户名， 如没有配置 则使用 当前登录用户名 密码
		String fr_username=SystemConfig.getPropertyValue("fr_username");
		String fr_password=SystemConfig.getPropertyValue("fr_password");
		if(StringUtils.isEmpty(fr_username)){
			fr_username=userView.getUserName();
		}
		if(StringUtils.isEmpty(fr_password)){
			fr_password=userView.getPassWord();
		}
		NameValuePair[] data = {
				new NameValuePair("fr_username", fr_username),
				new NameValuePair("fr_password", fr_password)
		};
		post.setRequestBody(data);
		client.executeMethod(post);
		String res = post.getResponseBodyAsString();

		JSONObject json = null;
		try {
			json = JSONObject.fromObject(res.substring(res.indexOf("{"), res.indexOf("}") + 1));
		}catch (Exception e){
			//没有部署帆软报表时，返回的是报错页面的html，解析json会失败
			isExistFr = false;
			e.printStackTrace();
			json = new JSONObject();
			json.put("status","fail");
		}
		String status = "fail";
		if(json.containsKey("status")){
			json.getString("status");
		}

		String sqlstr="",operaManger = "";
		if("U".equalsIgnoreCase(privType))
			
			operaManger =userView.getManagePrivCode() + userView.getManagePrivCodeValue() + "`";
		
		else if("O".equalsIgnoreCase(privType))
			
			operaManger = userView.getUnit_id();//操作单位
		
		else if(StringUtils.isNotEmpty(privType)){
			
			operaManger = userView.getUnitIdByBusi(privType);
			//operaManger = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(operaManger);
		}
		String code="";
		if(operaManger.indexOf("UN`")!=-1 || userView.isSuper_admin()){
			sqlstr="select  min(codeitemid) as codeitemid  from organization where  ";
			if(Sql_switcher.searchDbServer() == 2)//oracle
				sqlstr+= " sysdate ";
			else if(Sql_switcher.searchDbServer() == 1)//sqlserver
				sqlstr+= "  getDate()  ";
			sqlstr+=" between start_date and end_date ";
			RowSet frowset = null;
			Connection conn=null;
			try {
				conn=AdminDb.getConnection();
				ContentDAO dao=new ContentDAO(conn);
				frowset=dao.search(sqlstr);
				if(frowset.next())
					code="UN"+frowset.getString("codeitemid");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(frowset);
				PubFunc.closeDbObj(conn);
			}
		}else
			code = getTopCode(operaManger);
		if(!isExistFr){
			//没有部署帆软报表时，给出提示信息
			out.println("<html><div style=\"color:#157efb;font-size:1.2em;position: absolute;top:50%;left:50%;margin-left:-150px;margin-top:-180px; \">未部署BI工具或分析模型，请与系统管理员联系！</div></html>");
		}else{
			if("success".equals(status)||userView!=null){
				javax.servlet.http.Cookie coo = new javax.servlet.http.Cookie("JSESSIONID", "");
				Cookie[] cookies =  client.getState().getCookies();
				for(int i=0;i<cookies.length;i++){
					Cookie co = cookies[i];
					if("jsessionid".equalsIgnoreCase(co.getName())){
						coo.setComment(co.getComment());
						coo.setDomain(co.getDomain());
						coo.setPath(co.getPath());
						coo.setSecure(co.getSecure());
						coo.setVersion(co.getVersion());
						coo.setValue(co.getValue());
						break;
					}

				}
				response.addCookie(coo);

				String reportType = "reportlet";
				if(reportName.endsWith(".frm"))
					reportType = "formlet";
				String action=urlroot+"/ReportServer?"+reportType+"="+reportName+"&amp;op="+op;
				//添加两个## 是因为解密会过滤前两位
				String othwhere="&amp;logonUser="+PubFunc.encrypt(userView==null?"##":("##"+userView.getUserName()))+"&amp;logonA0100="+PubFunc.encrypt(userView==null?"##":("##"+userView.getDbname()+userView.getA0100()));
				action+=othwhere;
				/**拼接所有的参数*/
				Enumeration enu=request.getParameterNames();
				while(enu.hasMoreElements()){
					String paraName=(String)enu.nextElement();
					String paraVal=request.getParameter(paraName);
					paraVal=URLDecoder.decode(paraVal, "UTF-8");
					/**如果参数是帆软报表名 则过滤  logonUser logonA0100 不考虑*/
					if("reportName".equals(paraName)||"op".equals(paraName)){
						continue;
					}
					action+="&amp;"+paraName+"="+paraVal;
				 } 
				
				String encryptAction = PubFunc.encrypt(action);

				if(userView!=null&&showType!=null&&("UN".equalsIgnoreCase(showType)|| "UM".equalsIgnoreCase(showType)|| "@K".equalsIgnoreCase(showType))){
					out.println("  <!DOCTYPE HTML>");
					out.println("  <HTML>");
					out.println("  <HEAD>");
					out.println("  </HEAD>");
					out.println("  <frameset cols=\"200,*\">");
					out.println("  <frame src=\"/report/ReportTreeServlet?reportAction="+encryptAction+"&amp;showType="+showType+"&amp;privType="+privType+"\" noresize=\"noresize\"/><frame id=\"frreport\" name=\"mil_body\" src=\""+action+"&amp;orgCode="+PubFunc.encrypt(code)+"\"/>");
					out.println("  </frameset>");
					out.println("  </HTML>");
				}else if(userView!=null&&showType!=null&&!"".equals(showType)&&!"".equals(showType)){
					out.println("  <!DOCTYPE HTML>");
					out.println("  <HTML>");
					out.println("  <HEAD>");
					out.println("  </HEAD>");
					out.println("  <frameset cols=\"200,*\">");
					out.println("  <frame src=\"/servlet/FrCodeTreeServlet?reportAction="+encryptAction+"&amp;privType="+privType+"&amp;showType="+showType+"\" noresize=\"noresize\"/><frame id=\"frreport\" name=\"mil_body\" src=\""+action+"&amp;itemCode= \"/>");
					out.println("  </frameset>");
					out.println("  </HTML>");
				}else{
					out.println("  <!DOCTYPE HTML>");
					out.println("  <HTML>");
					out.println("  <HEAD>");
					out.println("  </HEAD>");
					out.println("  <frameset cols=\"*\">");
					out.println("  <frame name=\"mil_body\" src=\""+action+"&amp;op="+op+"&amp;orgCode="+PubFunc.encrypt(code)+"\"/>");
					out.println("  </frameset>");
					out.println("  </HTML>");
				}
			}
		}
		out.flush();
		out.close();
	}

	private String getTopCode(String operaManger) {
		String codeStr = "";
		try {
			HashMap<Integer,ArrayList> map = new HashMap<Integer,ArrayList>();
			String[] operaMangers = operaManger.split("`");
			for(int i = 0;i < operaMangers.length;i++){
				String code = operaMangers[i];
				int length = code.length();
				if(length==0)continue;
				if(map.containsKey(length)){
					ArrayList list = map.get(length);
					list.add(code);
				}else{
					ArrayList list = new ArrayList();
					list.add(code);
					map.put(length, list);
				}
			}
			Integer min = 9999;
			for(Integer key : map.keySet()){
				if(min > key)
					min = key;
			}
			ArrayList codelist = map.get(min);
			codeStr = (String)codelist.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeStr;
	}

	@Override
    public void init() throws ServletException {
		// Put your code here
	}

}
