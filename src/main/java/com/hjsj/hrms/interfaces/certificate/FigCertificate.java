package com.hjsj.hrms.interfaces.certificate;

import com.bjca.sso.bean.UserTicket;
import com.bjca.sso.processor.TicketManager;
import org.apache.log4j.Category;

import java.util.Hashtable;
public class FigCertificate {
	
	private String username;
	private String password;
	private String departid;
	private String userid;       //用户32位唯一标识码;
	private String departname;   //部门名称
    private Hashtable roles;     //角色
	
	 /**
	   * 日志跟踪器
	   */
	  protected Category cat = Category.getInstance(this.getClass());
	 /**beijing东城区人事局认证用户解析
	  * @param BJCA_SERVER_CERT   服务器证书
	  * @param BJCA_TICKET        票据
	  * @param BJCA_TICKET_TYPE   票据类型
	  * */
    public void parseUserinfo(String BJCA_SERVER_CERT,String BJCA_TICKET,String BJCA_TICKET_TYPE)
    {
    	
    	cat.debug("BJCA_SERVER_CERT==========" + BJCA_SERVER_CERT);
    	cat.debug("BJCA_TICKET==========" + BJCA_TICKET );
    	cat.debug("BJCA_TICKET_TYPE==========" + BJCA_TICKET_TYPE );
    	TicketManager ticketmag = new TicketManager();
        //验证签名及解密
    	UserTicket userticket = ticketmag.getTicket(BJCA_TICKET, BJCA_TICKET_TYPE, BJCA_SERVER_CERT);
        //处理票据信息
    	if(userticket != null) {
		    //用户姓名 
		    username = userticket.getUserName();   //这个是由 bjca 配置的，也许该值为空。
			//用户32位唯一标识码
			userid = userticket.getUserUniqueID();
			//用户所在部门的编码
			departid = userticket.getUserDepartCode();
			//用户所在部门的名称
			departname = userticket.getUserDepartName();
			//用户所拥有的角色信息
			roles = userticket.getUserRoles();
			/*String s_role = "";
			if(roles != null && roles.size() > 0) {
				int index = 1;
				Enumeration e = roles.keys();
				Enumeration e2 = roles.elements();
				for(;e.hasMoreElements();){
					String rolecode = (String)e.nextElement();
					String rolename = (String)e2.nextElement();
					if(rolename.indexOf("?") != -1) {
						rolename = new String(rolename.getBytes("GBK"),"ISO-8859-1");
					}
					if(index == 1){
						s_role = rolecode;
					}else{
						s_role = s_role + "," + rolecode;
					}
					index++;
				}
			}*/
			//打印信息：
			cat.debug("username======="+username);
			cat.debug("userid======="+userid);
			cat.debug("departid======="+departid);		
		}else{	
			//为空处理
		}
    }
	public String getDepartid() {
		return departid;
	}
	public void setDepartid(String departid) {
		this.departid = departid;
	}
	public String getDepartname() {
		return departname;
	}
	public void setDepartname(String departname) {
		this.departname = departname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Hashtable getRoles() {
		return roles;
	}
	public void setRoles(Hashtable roles) {
		this.roles = roles;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
