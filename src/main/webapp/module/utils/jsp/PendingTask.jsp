  	<%@page import="java.util.*"%>
  	<%@page import="com.hjsj.hrms.utils.PubFunc,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
  	<script language="JavaScript" src="../../../../ajax/basic.js"></script>

	<%
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		if (userView!=null){
			String path = request.getSession().getServletContext().getRealPath("/js");
			if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
				path = session.getServletContext().getResource("/js").getPath();//.substring(0);
				if (path.indexOf(':') != -1) {
					path = path.substring(1);
				} else {
					path = path.substring(0);
				}
				int nlen = path.length();
				StringBuffer buf = new StringBuffer();
				buf.append(path);
				buf.setLength(nlen - 1);
				path = buf.toString();
			}
			userView.getHm().put("js_path", path);
		}
		HashMap map = null;
	    try{
	    	map = new HashMap(request.getParameterMap());
	    }catch(IllegalStateException e){
		    e.printStackTrace();
		}
	%>
	<script type="text/javascript">
			var html = "";
		<%
			Iterator iter = map.entrySet().iterator();
			String html = "";
			String _html = "";
			String noStr = ",etoken,appfwd,";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String[] val = (String[])map.get(key);
				if(key.toString().equalsIgnoreCase("param")){
					String value = val[0];
					html = value;
				}else if(noStr.indexOf(","+key.toString()+",")==-1&&!key.toString().startsWith("br_")){
					String value = val[0];
					_html += "&"+key.toString()+"="+value;
				}
			}
		%>
			html = getDecodeStr("<%=html+_html%>");
			window.location.href=html;

 	</script>