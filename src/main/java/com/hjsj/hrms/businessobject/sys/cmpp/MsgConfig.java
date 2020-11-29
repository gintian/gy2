package com.hjsj.hrms.businessobject.sys.cmpp;

/**
 * 用于获取短信接口配置参数
 * 
 * @author
 * 
 */
public class MsgConfig{
	
	// 获取互联网短信网关IP
	private static String ismgIp;
	
	
	// 互联网短信网关端口号
	private static int ismgPort;
	
	// 企业代码
	private static String spId;
	
	// 号段
	private static String spCode;
	
	//业务代码（serivceId）
	private static String serviceId;
	
	// 密码
	private static String spSharedSecret;
	
	private static int connectCount = 3;
	
	private static int timeOut = 100;
	
	
	public static void setIsmgIp(String ismgIp) {
		MsgConfig.ismgIp = ismgIp;
	}
	public static void setIsmgPort(int ismgPort) {
		MsgConfig.ismgPort = ismgPort;
	}
	public static void setSpId(String spId) {
		MsgConfig.spId = spId;
	}
	public static void setSpCode(String spCode) {
		MsgConfig.spCode = spCode;
	}
	public static String getServiceId() {
		return serviceId;
	}
	public static void setServiceId(String serviceId) {
		MsgConfig.serviceId = serviceId;
	}
	public static void setSpSharedSecret(String spSharedSecret) {
		MsgConfig.spSharedSecret = spSharedSecret;
	}
	public static void setConnectCount(int connectCount) {
		MsgConfig.connectCount = connectCount;
	}
	public static void setTimeOut(int timeOut) {
		MsgConfig.timeOut = timeOut;
	}
	/**
	 * 获取互联网短信网关IP
	 * @return
	 */
	public static String getIsmgIp(){
		return MsgConfig.ismgIp ;
	}
	/**
	 * 获取互联网短信网关端口号
	 * @return
	 */
	public static int getIsmgPort(){
		return MsgConfig.ismgPort;
	}
	/**
	 * 获取sp企业代码
	 * @return
	 */
	public static String getSpId(){
		return MsgConfig.spId;
	}
	/**
	 * 获取sp下发短信号码
	 * @return
	 */
	public static String getSpCode(){
		return MsgConfig.spCode;
	}
	/**
	 * 获取sp sharedSecret
	 * @return
	 */
	public static String getSpSharedSecret(){
		return MsgConfig.spSharedSecret;
	}
	/**
	 * 获取链接的次数
	 * @return
	 */
	public static int getConnectCount(){
		return MsgConfig.connectCount;
	}
	/**
	 * 获取链接的超时时间
	 * @return
	 */
	public static int getTimeOut(){
		return MsgConfig.timeOut;
	}
}