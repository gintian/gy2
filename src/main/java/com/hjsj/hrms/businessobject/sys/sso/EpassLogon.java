package com.hjsj.hrms.businessobject.sys.sso;



import com.baosight.epass2.ws.generated.AuthenticationManagerBean;
import com.baosight.epass2.ws.generated.AuthenticationManagerBeanServiceLocator;
import com.hrms.struts.admin.VerifyUser;
import com.hrms.struts.constant.SystemConfig;


public class EpassLogon implements VerifyUser {
	String hh="kkk";
	public EpassLogon(){
	}
	/**
	 * 第三方
	 */
	@Override
    public boolean isExist(String arg0, String arg1) {
		return getAuthorization(arg0,arg1,SystemConfig.getPropertyValue("EpassUrl"),SystemConfig.getPropertyValue("EpassQname"));
	}


	@Override
    public String getUserId() {
		return "";
	}
	public boolean getAuthorization(String username,String password){
		boolean flag=false;
		try {
			AuthenticationManagerBeanServiceLocator ambsl=new AuthenticationManagerBeanServiceLocator();		
			AuthenticationManagerBean amb=ambsl.getAuthenticationManagerBean();
			String[] retvalue=amb.createAuthenToken(username,password);
			if(retvalue!=null&& "true".equalsIgnoreCase(retvalue[0])){
				flag=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	}
	public boolean getAuthorization(String username,String password,String url,String qname) {
		boolean flag=false;
		AuthenticationManagerBeanServiceLocator ambsl=new AuthenticationManagerBeanServiceLocator();
		try {
			
			ambsl.setAuthenticationManagerBeanWSDDServiceName(qname.trim());
			AuthenticationManagerBean amb=ambsl.getAuthenticationManagerBean(new java.net.URL(url.trim()));
			String[] retvalue=amb.createAuthenToken(username,password);
			if(retvalue!=null&& "true".equalsIgnoreCase(retvalue[0])){
				flag=true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return flag;
		}
		return flag;
	}
	public  static void main(String[] args){
		EpassLogon el=new EpassLogon();
		try {
			System.out.println(el.getAuthorization("hello","world"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
