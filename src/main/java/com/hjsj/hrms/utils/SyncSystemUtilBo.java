package com.hjsj.hrms.utils;

import com.hjsj.hrms.businessobject.sys.LoadDynamicParametersBo;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.module.system.hrcloud.util.SyncDataUtil;
import com.hjsj.hrms.transaction.sys.warn.ScanTrans;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 集群环境刷新数据字典、加载动态参数功能实现
 * <p>Title: SyncSystemUtilBo </p>
 * <p>Description: 集群环境刷新数据字典同步、加载动态参数工具类</p>
 * <p>create time: 2017-6-26 下午03:48:26</p>
 * <p>Company: hjsj</p>
 * @author wangbo
 * @version 1.0
 */
public class SyncSystemUtilBo {

	private Connection conn;
	/**
	 * 刷新数据字典
	 */
	public static final int SYNC_TYPE_RELOAD_DATADICTIONARY=1;

	/**
	 * 加载动态参数
	 */
	public static final int SYNC_TYPE_RELOAD_PARAM=2;
	
	/**
	 * 刷新预警参数
	 */
	public static final int SYNC_TYPE_RELOAD_WARN=3;
	
	/**
	 * 刷新用户登录参数
	 */
	public static final int SYNC_TYPE_RELOAD_LOGIN_USERINFO=4;
	
	
	public static final int SYNC_TYPE_UPD_ADMINCODE=5;

	public SyncSystemUtilBo(Connection conn){
		this.conn=conn;
	}
	/**
	 * 集群环境操作处理
	 * @param type  1 刷新数据字典  2 加载动态参数
	 * @param param 参数集合
	 * @return 
	 */
	public static String sendSyncCmd(int type,HashMap param){
		//读取system参数
		String clusterEnvironment = SystemConfig.getPropertyValue("cluster_environment");
		if("".equals(clusterEnvironment) || clusterEnvironment == null) 
			return ""; 
		String[] clusterEnvironments=clusterEnvironment.split(",");
		String webPort=SystemConfig.getWebPort();//当前服务器 端口号
		String webHost=SystemConfig.getWebHost();//当前服务器ip地址
		String localAddress = webHost +":"+ webPort;// 服务器ip : 端口号
		for(int i=0;i<clusterEnvironments.length;i++){
			if("".equals(clusterEnvironments[i]) || clusterEnvironments[i] == null) 
				continue;
			//xus 18/2/28 判断如果微信程序与hr程序在同一服务器上也可用
			String sendPath=clusterEnvironments[i].endsWith("/")?clusterEnvironments[i].substring(0, clusterEnvironments[i].length()-1):clusterEnvironments[i];
			//w_selfservice 微信；recruitservice 微招聘
			if(localAddress != null && sendPath.indexOf(localAddress) > -1 && sendPath.indexOf("w_selfservice") == -1 && sendPath.indexOf("recruitservice") == -1)
				continue;
			//发送信息
			sendCmd(sendPath,type,param);
		}
		return "0";
	}
	
	/**
	 * 集群环境操作处理
	 * @param type  1 刷新数据字典  2 加载动态参数
	 * @return 
	 */
	public static String sendSyncCmd(int type){
		return sendSyncCmd(type,new HashMap());
	}

	private static void sendCmd(String address , int type, HashMap paramMap){
		//采用加密时间戳方式保证调用合法性，
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String time = dateFormat.format( now );
		String timestamp = PubFunc.encrypt("type=" +type +",timestamp=" + time);//加密方式 type=操作类型,timestamp=当前时间
		String params="param="+timestamp;//请求的参数
		if(paramMap!=null){
			Iterator ite =  paramMap.keySet().iterator();
			while(ite.hasNext()){
				String key = (String)ite.next();
				String value = (String)paramMap.get(key);
				params+="&"+key+"="+value;
			}
			
		}
		String path = address + "/sys/syncsysdataservlet";//集群环境同步请求地址
		//异步执行 线程
		SyncSystemThread sst=new SyncSystemThread(params,path);
		sst.start();
	}

	/**
	 * 执行加载动态参数
	 * @throws GeneralException
	 */
	public void reloadDynaParam() throws GeneralException{
		LoadDynamicParametersBo ldpb = new LoadDynamicParametersBo(this.conn);
		ldpb.reloadAllParam();
	}

	/**
	 * 加载刷新数据字典
	 * @param path js路径
	 * @throws GeneralException
	 */
	public void  refreshDataDirectory(String path) throws GeneralException{
		PubFunc.syncRefreshDataDirectory(path, this.conn);
	}


	/**
	 * 执行同步操作
	 * @param type 1 刷新数据字典 2 加载动态参数
	 */
	public void doSync(int type,HttpServletRequest req)throws GeneralException{
		switch(type){
		case 1://刷新数据字典
			String jsPath = getJsPath(req);//js 本地绝对路径
			this.refreshDataDirectory(jsPath);
			break;
		case 2://加载动态参数
			this.reloadDynaParam();
			break;
		case 3:
			this.refreshWarnConfig();
			break;
		case 4:
			this.refreshLoginUserInfo(req);
			break;
		case 5:
			this.batchUpdAdminCode(req);
			break;
		}
	}
	
	/**
	 * 批量更新AdminCode属性
	 * @param req
	 */
	private void batchUpdAdminCode(HttpServletRequest req) {
		String batch = req.getParameter("batchJson");
		batch = PubFunc.decrypt(batch);
		if(batch.length() == 0 || batch.indexOf("isref") == -1) {
			return ;
		}
		JSONObject batchJson = JSONObject.fromObject(batch);
		boolean isref = batchJson.getBoolean("isref");
		if(isref) {
			AdminCode.refreshCodeTable();
		}else {
			SyncDataUtil.batchRefAdminCodes(batchJson);
		}
	}
	/**
	 * 获取js文件夹绝对路径
	 * @return js路径
	 */
	private String getJsPath(HttpServletRequest req){
		String path = "";
		try {
			path = req.getSession().getServletContext().getRealPath("/js");
			if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		    {
			  path=req.getSession().getServletContext().getResource("/js").getPath();
			  if(path.indexOf(':')!=-1)
		  	  {
				 path=path.substring(1);   
		   	  }
		  	  else
		   	  {
				 path=path.substring(0);      
		   	  }
		      int nlen=path.length();
		  	  StringBuffer buf=new StringBuffer();
		   	  buf.append(path);
		  	  buf.setLength(nlen-1);
		   	  path=buf.toString();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	private void refreshLoginUserInfo(HttpServletRequest req){
		String username = req.getParameter("username");
		ConstantParamter.setUserAttribute(username, "first_login", "0");
	}
	
	/**
	 * 集群环境刷新从属节点预警参数
	 * guodd 2018-06-14
	 */
	private void refreshWarnConfig(){
		try {
			ScanTrans st = ScanTrans.getInstance();
			Method queryAllConfig =  st.getClass().getDeclaredMethod("queryAllConfig");
			queryAllConfig.setAccessible(true);
			ArrayList configList = (ArrayList)queryAllConfig.invoke(st);
			
			HashMap warnMap = ContextTools.getWarnConfigCache();
			warnMap.clear();
			for(int i=0;i<configList.size();i++){
				DynaBean dbean = (DynaBean) configList.get(i);//获得一个预警记录
				ConfigCtrlInfoVO ctrlVo = new ConfigCtrlInfoVO((String) dbean.get(IConstant.Key_HrpWarn_FieldName_CtrlInf));
				dbean.set(IConstant.Key_HrpWarn_Ctrl_VO, ctrlVo);
				String strWid = (String) dbean.get(IConstant.Key_HrpWarn_FieldName_ID);//wid字段		
				warnMap.put(strWid, dbean);
				
			}
		} catch (Exception e) {
		}
		
	}
}

class SyncSystemThread extends Thread{
	/**请求参数*/
	private String params;
	/**请求路径*/
	private String path;

	public SyncSystemThread(String params , String path){
		this.params=params;
		this.path=path;
	}

	@Override
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			//根据地址调用SyncSysDataServlet并以post方式传入type和timestamp参数，实现同步刷新
			//请求的参数type和timestamp
			URL connURL = new URL(path);//创建请求URL对象
			HttpURLConnection httpConn = (HttpURLConnection) connURL.openConnection();//打开URL连接
			//设置postf方式
			httpConn.setDoInput(true);  
			httpConn.setDoOutput(true); 
			// 获取HttpURLConnection对象对应的输出流  
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数  
			out.write(params); 
			// flush输出流的缓冲  
			out.flush();
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "GBK"));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getParam() {
		return params;
	}
	public void setParam(String params) {
		this.params = params;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}