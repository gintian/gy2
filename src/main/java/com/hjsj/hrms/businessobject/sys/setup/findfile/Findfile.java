package com.hjsj.hrms.businessobject.sys.setup.findfile;

import com.hjsj.hrms.businessobject.sys.setup.TomcatCommand;

public class Findfile {
	public static String findPath(String filename){
		String filepath=null;
		if(System.getenv("OS").startsWith("Windows")){
			if("web.xml".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"\\webapps\\hrms\\WEB-INF\\"+filename;
			}
			if("server.xml".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"\\conf\\"+filename;
			}
			if("system.properties".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"\\config\\"+filename;
			}
		}else{
			if("web.xml".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"/webapps/hrms/WEB-INF/"+filename;
			}
			if("server.xml".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"/conf/"+filename;
			}
			if("system.properties".equalsIgnoreCase(filename)){
				filepath=System.getenv("CATALINA_HOME")+"/config/"+filename;
			}
		}
			return filepath;
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(findPath("web.xml"));
		System.out.println(findPath("server.xml"));
		System.out.println(findPath("system.properties"));
		TomcatCommand.Command("restart");
	}

}
