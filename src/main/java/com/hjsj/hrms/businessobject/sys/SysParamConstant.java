package com.hjsj.hrms.businessobject.sys;

/**
 * 
 * <p>Title: SysParamConstant </p>
 * <p>Description:参数常量 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-9-28 下午10:21:00</p>
 * @author xuj
 * @version 1.0
 */
public class SysParamConstant {
	
	//----------------模块------------------------------
	/**
	 * 系统管理模块
	 */
	public static String SYS_SYS_PARAM = "SYS_SYS_PARAM"; 
	//-----------------参数------------------------------

	/*1、首次密码强制修改，0|1，默认为0不修改
	#login_first_chang_pwd=0|1
	2、重新设置的新密码不能和前N次密码相同，输入为整数值，0记录全部历史密码，默认或空不记录历史密码
	#login_history_pwd
	3、密码最小长度
	#passwordlength=10
	4、密码复杂度，现对密码复杂度进行0低|1中|2强三种模式划分，1密码长度不得低于参数passwordlength指定长度，必须包字母、数字，2密码长度不得低于参数passwordlength指定长度，必须包含大、小写字母、数字、特殊符号， 且不能重复
	#passwordrule=0|1|2
	5、密码到期提醒天数设置
	#passworddays=10
	密码到期天数未修改，强制锁定账号
	#password_lock_days=90
	7、同帐号某一时间点仅允许登陆一次	false|true(未定义此属性时) 允许登录多次
	#only_logon_one=false
	8、时间间隔在多少分钟内连续登录错误锁定，默认不做登录锁定控制
	#account_logon_interval=30
	9、时间间隔内累计登录错误次数锁定
	#account_logon_failedcount=5*/
	/**
	 * 首次密码强制修改，0|1，默认为0不修改
	 */
	public static String LOGIN_FIRST_CHANG_PWD = "login_first_chang_pwd"; 
	/**
	 * 重新设置的新密码不能和前N次密码相同，输入为整数值，0记录全部历史密码，默认或空不记录历史密码
	 */
	public static String LOGIN_HISTORY_PWD="login_history_pwd";
	/**
	 * 密码最小长度
	 */
	public static String PASSWORDLENGTH="passwordlength";
	/**
	 * 密码复杂度，现对密码复杂度进行0低|1中|2强三种模式划分，
	 * 1密码长度不得低于参数passwordlength指定长度，必须包字母、数字，
	 * 2密码长度不得低于参数passwordlength指定长度，必须包含大、小写字母、数字、特殊符号， 且不能重复
	 */
	public static String PASSWORDRULE = "passwordrule";   
	/**
	 * 密码到期提醒天数设置
	 */
	public static String PASSWORDDAYS="passworddays";
	/**
	 * 密码到期天数未修改，强制锁定账号
	 */
	public static String PASSWORD_LOCK_DAYS="password_lock_days";
	/**
	 * 同帐号某一时间点仅允许登陆一次	false|true(未定义此属性时) 允许登录多次
	 */
	public static String ONLY_LOGON_ONE = "only_logon_one"; 
	/**
	 * 时间间隔在多少分钟内连续登录错误锁定，默认不做登录锁定控制
	 */
	public static String ACCOUNT_LOGON_INTERVAL="account_logon_interval";
	/**
	 * 时间间隔内累计登录错误次数锁定
	 */
	public static String ACCOUNT_LOGON_FAILEDCOUNT="account_logon_failedcount";
	/**
	 * 登录验证码
	 */
	public static String VALIDATECODE="validatecode";
	/**
	 * 登录验证码长度
	 */
	public static String VALIDATECODELEN="validatecodelen";
	/**
	 * 登录验证码包含内容,默认为QAZWSXEDCRFVTGBYHNUJMIKLP123456789
	 */
	public static String VALIDATECODEINFO="validatecodeinfo";
	/**
	 * 登录页面显示忘记密码
	 */
	public static String RETRIEVING_PASSWORD="retrieving_password";
	/**
	 * 登陆页面显示安装插件
	 */
	public static String SHOWHOMEPAGEPLUGINSETUP="ShowHomePagePluginSetup";

	/**
	 * 登陆用户名密码传输过程中是否加密 true加密，默认不加密
	 */
	public static String PASSWORD_TRANS_ENCRYPT="password_trans_encrypt";

}
