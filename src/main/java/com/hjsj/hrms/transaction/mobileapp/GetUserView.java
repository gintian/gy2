package com.hjsj.hrms.transaction.mobileapp;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.transaction.mobileapp.binding.BindingConstant;
import com.hjsj.hrms.transaction.mobileapp.utils.Tools;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * <p>Title: GetUserView </p>
 * <p>Description: 移动服务登陆</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2014-7-11 下午3:05:53</p>
 * @author yangj
 * @version 1.0
 */
public class GetUserView extends IBusiness {

	private static final long serialVersionUID = 1L;
	private static Map controlMap;
	private static String defaultFunc = "";
	private Category log = Category.getInstance(GetUserView.class.getName());
	
	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		try {
			String bovertest =  (String) hm.get("bovertest");
			String deviceid = (String)hm.get("deviceid");
			if("true".equals(bovertest)){
				hm.put("message", ResourceFactory.getProperty("error.lock.overtime.mobile"));
	            hm.put("succeed","false");
			}else{
				//String license_module = (String)hm.get("license_module");
				//license_module = license_module==null?"":license_module;
				//hm.put("license_module", license_module.replace(",", ""));
				String transType = (String) hm.get("transType");
				if(transType == null || "".equals(transType) || "1".equals(transType)) {
					LazyDynaBean dynabean=new LazyDynaBean();
					dynabean.set("a0100", this.userView.getA0100());
					dynabean.set("dbname", this.userView.getDbname());
					dynabean.set("fieldpriv", this.userView.getFieldpriv().toString());
					dynabean.set("tablepriv", this.userView.getTablepriv().toString());
					dynabean.set("phone",  String.valueOf(this.userView.getUserTelephone()));
					
					dynabean.set("bsuper", String.valueOf(this.userView.isSuper_admin()));
					
					String fullname = this.userView.getUserFullName();
					dynabean.set("fullname", fullname);
					
					// 权限处理
					String func = this.userView.getFuncpriv().toString();
					func = "," + this.processFunc(func);
					// 0业务用户4自助用户
					int status = this.userView.getStatus();
					dynabean.set("status", String.valueOf(status));
					// 自助用户登录不显示业务协同
					if (status == 4)
						func = func.replace("0K0I,", "");
					//System.out.println(func);
					dynabean.set("funcpriv", func);
					
					dynabean.set("isBThree",String.valueOf(this.userView.isBThreeUser()));
					
					String email = this.userView.getUserEmail();
					//System.out.println(this.userView.getUserId());
					
					email = email == null ? "" : email;
					dynabean.set("email",  email);
				
					// 融云注册
					/*String token = RCTokenConstant.getRCToken(userView,(String)hm.get("url"),false);
					dynabean.set("token", token);
					log.error("RCIM token:"+token);
					dynabean.set("targetid",(String)userView.getHm().get("userid"));*/
					
					
					//this.userView.
					hm.put("userview", dynabean);
					hm.put("bindingStatus",""+this.isBinding(this.userView.getUserName(), deviceid)) ;
					Tools tools = new Tools();
					hm.put("ctr_staffsalary", tools.hasFuncNode("0K0301")+"");
				}
				
				/**
				 * 资源文件可以定义在前台android或ios平台
				 * 成功时可以不用传
				 */
				hm.put("succeed","true");
			}
		} catch(Exception e) {
            String errorMsg=e.toString();
            int index_i=errorMsg.indexOf("description:");
            String message = errorMsg.substring(index_i+12);
            hm.put("message", message);
            hm.put("succeed","false");
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 
	 * @Title: processFunc   
	 * @Description: 用户权限处理   
	 * @param func
	 * @return 
	 * @return String
	 */
	private String processFunc(String func) {
		StringBuffer funcBuf = new StringBuffer();
		// 取交集
		funcBuf.append(this.intersectionFunc(func));
		// 取并集
		funcBuf.append(defaultFunc);
		return funcBuf.toString();
	}
	
	/**
	 * 
	 * @Title: intersectionFunc
	 * @Description: 取交集 
	 * @param func
	 * @return String
	 */
	private String intersectionFunc(String func) {
		StringBuffer funcBuf = new StringBuffer();
		try {
			// 判断是否需要初始化版本控制相关参数
			if (controlMap == null)
				this.versionInit();
			// 判断是否是超级用户。超级用户拥有所有权限，不是超级用户则取交集
			if (this.userView.isSuper_admin()) {
				for(Iterator j = controlMap.entrySet().iterator(); j.hasNext();){
					Entry entry = (Entry)j.next();
					funcBuf.append((String)entry.getKey() + ",");
				}
			} else {
				// 权限分割
				String[] funcList = func.split(",");
				for (int i = 0; i < funcList.length; i++) {
					// 版本控制，和网页授权的权限取交集
					if(controlMap.containsKey(funcList[i]))
						funcBuf.append(funcList[i] + ",");
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return funcBuf.toString();
	}
	
	/**
	 * 
	 * @Title: versionInit   
	 * @Description:初始化版本控制 
	 * @throws GeneralException 
	 * @return void
	 */
	private void versionInit() throws GeneralException {
		StringBuffer buf = new StringBuffer();
        try {
        	controlMap = new HashMap();
        	// 获取服务器版本号
        	String ehrVersion = this.getEhrVersion();
        	// 当为空时，直接返回
        	if (ehrVersion == null || ehrVersion.length() == 0)
        		return;
        	String file = this.getPath("/transaction/mobileapp/mVersion.xml/");
        	Document doc = this.getDocument("/com/hjsj/hrms/transaction/mobileapp/mVersion.xml", file);
        	// 转换为节点
	        Element root = doc.getRootElement();
	        List list = root.getChildren();
	        if(list.size()==0) {
	        	log.debug("mVersion.xml获取权限失败");
	        }
	        log.debug(list);
	        log.debug("解析mVersion.xml开始=============================");
	        // 遍历所有节点
	        for (int i = 0; i < list.size(); i++) {
	        	this.processElement((Element)list.get(i), buf, ehrVersion);
	        }
        } catch(Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	defaultFunc = buf.toString();
        	if(controlMap.size() == 0)
        		System.out.println("版本控制获取权限失败");
        }
    }
	
	/**
	 * 
	 * @Title: processElement
	 * @Description: 节点处理  
	 * @param item       节点
	 * @param buf        可变字符串
	 * @param ehrVersion 服务器版本号
	 * @return void
	 */
	private void processElement(Element item, StringBuffer buf, String ehrVersion) {
		// <func id="功能号" name="说明" control="B/S是否控制，true控制/false不控制" hide="是否隐藏，true隐藏/false显示" 
		// date="功能完成时间">
		String id = item.getAttributeValue("id");
		id = id == null ? "" : id;
		String name = item.getAttributeValue("name");
		name = name == null ? "" : name;
		String hide = item.getAttributeValue("hide");
		hide = hide == null ? "" : hide;
		String control = item.getAttributeValue("control");
		control = control == null ? "" : control;
		String ctrlVer = item.getAttributeValue("ctrl_ver");
		ctrlVer = ctrlVer == null ? "" : ctrlVer;
		log.debug("分析=======id：" + id + " name：" + name + " hide：" + hide + " control：" + control + " ctrlVer：" + ctrlVer + " ehrVersion：" + ehrVersion);
		
		// 判断隐藏
		if("true".equals(hide))
			return;
		// 是否包含该版本号
		if(!ctrlVer.contains(ehrVersion))
			return;
		// 判断是否受网页授权控制
		if("true".equals(control))
			controlMap.put(id, name);
		else
			buf.append(id + ",");
		// 子节点
		List list = item.getChildren();
        for (int i = 0; i < list.size(); i++) {
        	this.processElement((Element)list.get(i), buf, ehrVersion);
        }
	}
	
	/**
	 * 
	 * @Title: getEhrVersion   
	 * @Description:获取服务器版本号
	 * @throws GeneralException 
	 * @return String
	 */
	private String getEhrVersion() throws GeneralException {
		log.debug("运行方法：getEhrVersion()");
		String ehrVersion;
		try {       	
			String file = this.getPath("/constant/version.xml/");
        	Document doc = this.getDocument("/com/hjsj/hrms/constant/version.xml", file);
        	// 转换为节点
	        Element root = doc.getRootElement();
	        ehrVersion = root.getAttributeValue("version");
        } catch(Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
		log.debug("返回ehrVersion：" + ehrVersion);
		return ehrVersion;
	}
	
	/**
	 * 
	 * @Title: getDocument   
	 * @Description: 获取xml转换的Document
	 * @param path
	 * @param file
	 * @throws GeneralException 
	 * @return Document
	 */
	private Document getDocument(String path, String file) throws GeneralException {
		log.debug("运行方法：getDocument(String path, String file)");
		Document doc = null;
		String EntryName = path.substring(1);
		InputStream in = null;
		JarFile jf=null;
		try {
			/** cmq added for jboss eap6 */
			String webserver = SystemConfig.getProperty("webserver");
			if ("jboss".equalsIgnoreCase(webserver) || "inforsuite".equalsIgnoreCase(webserver)) {
				in = this.getClass().getResourceAsStream(path);
			} else {
				if (file.indexOf("hrpweb3.jar") != -1||file.indexOf("mobileapp.jar") != -1) {
					 jf= new JarFile(file);
					Enumeration es = jf.entries();
					while (es.hasMoreElements()) {
						JarEntry je = (JarEntry) es.nextElement();
						log.debug("匹配文件：" + je.getName());
						if (je.getName().equals(EntryName)) {
							in = jf.getInputStream(je);
							log.debug("hrpweb3.jar中找到" + file);
							break;
						}

					}
				}
			}
			if (in == null) {
				in = new FileInputStream(file);
				log.debug("生产环境中找到" + file);
			}
			doc = PubFunc.generateDom(in);
			if (doc == null)
				System.out.println("NOT FOUND version.xml or mVersion.xml FILE");
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			Tools.closeIoResource(in);
			Tools.closeIoResource(jf);
		}
		log.debug("返回doc：" + doc);
		return doc;
	}
	
	/**
	 * 
	 * @Title: getPath   
	 * @Description: 获取xml所在路径    
	 * @param path
	 * @return String
	 */
	private String getPath(String path) {
		log.debug("运行方法：getPath(String path)");
		String classPath = "";
		try {
			if(path.indexOf("constant")!=-1){
				Class cla = Class.forName("com.hjsj.hrms.transaction.lawbase.AddLawBaseFileTrans");//借用AddLawBaseFileTrans class找到hrpweb3.jar资源路径
				classPath = cla.getResource("").toString();
			}else{
				classPath = this.getClass().getResource("").toString();
			}
			classPath = java.net.URLDecoder.decode(classPath, "utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (classPath.indexOf("hrpweb3.jar")!=-1||classPath.indexOf("mobileapp.jar") != -1) {
			int beginIndex = -1, endIndex = -1;
			/** weblogic,环境布署时 */
			if (classPath.startsWith("zip:")) {
				beginIndex = classPath.indexOf("zip:") + 4;
				if(classPath.indexOf("hrpweb3.jar")!=-1)
					endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
				else
					endIndex = classPath.lastIndexOf("mobileapp.jar") + 13;
				classPath = classPath.substring(beginIndex, endIndex);
			} else {
				Properties props = System.getProperties(); // 系统属性
				String sysname = props.getProperty("os.name");
				if (sysname.startsWith("Win")) {
					beginIndex = classPath.indexOf("/") + 1;
					if(classPath.indexOf("hrpweb3.jar")!=-1)
						endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
					else
						endIndex = classPath.lastIndexOf("mobileapp.jar") + 13;
					classPath = classPath.substring(beginIndex, endIndex);
				} else {
					beginIndex = classPath.indexOf("/");
					if(classPath.indexOf("hrpweb3.jar")!=-1)
						endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
					else
						endIndex = classPath.lastIndexOf("mobileapp.jar") + 13;
					classPath = classPath.substring(beginIndex, endIndex);
				}
			}
		} else {
			Properties props=System.getProperties(); //系统属性
			String sysname = props.getProperty("os.name");
			//zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
			int beginIndex = classPath.indexOf("/");
			if(sysname.startsWith("Win")){
				beginIndex++;
			}
			if (classPath.indexOf("transaction") != -1) {
				int endIndex = classPath.lastIndexOf("transaction") - 1;
				classPath = classPath.substring(beginIndex, endIndex) + path;
			}
		}
		log.debug("返回classPath：" + classPath);
		return classPath;
	}
	
	/**
	 * 判断登录设备是否绑定
	 */
	private int isBinding(String username ,String deviceid){
		int bindingStatus = 1;
		try{
			ConstantXml constant = new ConstantXml(this.getFrameconn(), "SYS_LOGIN_SETTING");
			//String computer = constant.getNodeAttributeValue("/params/open_authentication", "computer");
			String mobile = constant.getNodeAttributeValue("/params/open_authentication", "mobile");
			if("1".equals(mobile)){//开启移动服务登录绑定
				//0未绑定，跳转到新手机绑定界面  1已绑定，可以登录  2申请绑定未认证，提示等待认证 3与登录账号与绑定设备不一致，不允许登录
				bindingStatus = BindingConstant.bindingStatus(username, deviceid);
				String manual = constant.getTextValue("/params/authentication_type/manual");
				String sms = constant.getTextValue("/params/authentication_type/sms");
				this.formHM.put("manual", manual);
				this.formHM.put("sms", sms);
				if(!"1".equals(manual)&&bindingStatus==2){//如果先选择人工激活，未成功激活之前,后来只设置短信激活，这种情况下就直接选短信重新去引导激活
					bindingStatus=0;
				}
			}else{
				bindingStatus=1;
			}
		}catch(Exception e){
			bindingStatus = 1;
			e.printStackTrace();
		}
		return bindingStatus;
	}
	
	/**
	 * 
	 */
}
