<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.struts.admin.VerifyUser,java.util.*" %>
<%@ page import="com.hjsj.hrms.utils.sso.SsoTool,com.hjsj.hrms.businessobject.sys.SmsBo,com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo,com.hrms.frame.utility.AdminDb" %>
<% 

	/*发送规则：
	     一、自助用户登陆，获取自助用户信息发送验证码
	     二、业务用户登陆，先看是否关联自助用户
	         1、如果关联了自助，给自助用户发验证码
	         2、如果没有关联，直接给业务用户发送验证码 因为业务用户不可能有微信号，即使选择微信，也发送手机
	           如果业务用户没有设置手机号，直接将验证码返回到登陆页面*/

	//获取参数
	String username=request.getParameter("username");
	String password=request.getParameter("pwd");
	String type=request.getParameter("type");
	//如果没有输入用户名
	if(username==null || username.length()<1){
		response.getWriter().write("{\"send\":\"false\",\"msg\":\"no_user\"}");
		return;
	}
	
	//是否业务用户登陆
	boolean isBusiUser = false;
	
	//获取用户信息，如果返回值为null，说明用户不存在
	SsoTool ssoTool =new SsoTool(request,response);
	
	//按业务用户查询
	Map userInfo = ssoTool.getBusiUserInfo(username,password,null);
	//不为null说明是业务用户
	if(userInfo!=null){
		//业务用户是否关联自助
		String a0100 = (String)userInfo.get("a0100");
		if(a0100!=null && a0100.length()>0){//如果关联自助用户，查询自助用户信息
			String nbase = (String)userInfo.get("nbase");
		    //查出出自助用户账号密码
			HashMap userLogonInfo = ssoTool.getEmployeeLogonInfo(a0100, "a0100", false,nbase);
			String empUser = (String)userLogonInfo.get("username");
			String empPwd = (String)userLogonInfo.get("password");
			//根据账号密码查询人员信息
			Map empInfo = ssoTool.getUserInfo(empUser,empPwd,null);
			empInfo.put("username", empUser);
			String phonenum = (String)empInfo.get("phone");
			if((phonenum==null || phonenum.length()<1) && !type.equals("2"))
				isBusiUser = true;
			else
				userInfo = empInfo;
		}else{
			//是业务用户登陆
			isBusiUser = true;
			userInfo.put("username", username);
		}
	}else{
		//自助用户登录
		userInfo = ssoTool.getUserInfo(username, password,null);
		if(userInfo!=null)
			userInfo.put("username", username);
	}
	
	//用户不存在
	if(userInfo ==null){
		response.getWriter().write("{\"send\":\"false\",\"msg\":\"no_user\"}");
		return;
	}
	
	//获取要发送的手机号
	String phone = (String)userInfo.get("phone");
	phone = phone==null?"":phone;
	
	/*如果是业务用户，并且手机号存在，将type置成1。因为业务用户没有微信号。
	  如果没有手机号,将type置为3，直接将验证码传回登陆页*/
	if(isBusiUser){
		if(phone.length()>0){
			type="1";
		}else{
			type = "3";
		}
	}
	
	//如果发短息模式，并且手机号不存在
	if(type.equals("1") && phone.length()<1){
		response.getWriter().write("{\"send\":\"false\",\"msg\":\"no_phone\"}");
		return;
	}
		 
	
	//生成验证码 默认长度为6位 
	Random random=new Random(System.currentTimeMillis());  
	StringBuffer code=new StringBuffer();
	String strSrc = "QAZWSXEDCRFVTGBYHNUJMKLP123456789";
	int range = strSrc.length();
	int codeLength = 6,index=0;
	for(int i=0;i<codeLength;i++) {
		index=random.nextInt(range);
		code.append(strSrc.charAt(index));
	}
	String msg="人力资源系统登录验证码为： "+code.toString();
	
	//根据发送类别（type）判断应发送短信还是微信
	boolean flag=true;
	try{
		if(type.equals("1")){
			//发送短信
			SmsBo smsBo =new SmsBo(AdminDb.getConnection());
			flag = smsBo.sendMessage("SYSTEM", username,userInfo.get("phone").toString(), msg);
		}else if(type.equals("2")){
			//发送微信
			flag = WeiXinBo.sendMsgToPerson(userInfo.get("username").toString(), "登录验证码", msg, "", "");
		}
	}catch(Exception e){
		flag = false;
	}
	
	//发送不成功，返回信息
	if(!flag){
		response.getWriter().write("{\"send\":\"false\",\"msg\":\"fail\"}");
		return;
	}
	
	//发送完毕后将验证码和时间保存到session中，并返回发送成功标识
	session.setAttribute("smsvcode", code.toString());
	session.setAttribute("smsvuser", username);
	//放入当前时间
	long start = System.currentTimeMillis()/1000;
	session.setAttribute("smsvcode_time", String.valueOf(start));
	
	//为3的情况是 业务用户登陆，没有关联自助用户，并且没有设置手机号，直接将验证码回传
	if(type.equals("3")){
		response.getWriter().write("{\"send\":\"true\",\"code\":\""+code+"\"}");
		return;
	}
	
	//返回发送成功
	response.getWriter().write("{\"send\":\"true\"}");
%>
