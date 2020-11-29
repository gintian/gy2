package com.hjsj.hrms.module.system.regothersys;

import com.hjsj.hrms.utils.RollLoggerUtil;
import com.hrms.struts.constant.SystemConfig;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SysRegLogger {

	private String path = null;

	private String fileName = null;
	//记录sql执行日志logger
	//private Logger sys_logger = null;
	private static Map<String, Logger> map = new HashMap<String, Logger>();
	public SysRegLogger(String name) {
		fileName = name;
		this.getFilePath();
	}

	/**
	 * 获取日志所在路径
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 根据文件名获取文件所在路径
	 * 参考SqlLogger.java
	 * @return
	 */
	private void getFilePath() {
		try{
			//获取system中的日志路径
			String filePath = SystemConfig.getPropertyValue("sql_log_file");
			//当前系统路径分割符号
			String sysFileSep = System.getProperty("file.separator");
			getPath:if(filePath!=null && !"".equals(filePath)){
				filePath = filePath.replace("\\","/");
				filePath = filePath.replace("/", sysFileSep);
			}else{
				//读取 log4j 中配置的日志文件路径
				ResourceBundle bundle = ResourceBundle.getBundle("log4j");
				if(!bundle.containsKey("log4j.appender.file.File"))
					break getPath;
				filePath = bundle.getString("log4j.appender.file.File");
				filePath = filePath.replace("\\","/");
				filePath = filePath.replace("/", sysFileSep);
				//默认日志文件名为sqllog.log
				filePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep)+1)+"RegService_"+ fileName +".log";//将传进来的名字进行拼接
			}
			if(filePath!=null && !"".equals(filePath)){
				//如果使用了变量，解析变量
				if(filePath.startsWith("${catalina.base}")){
					filePath = System.getProperty("catalina.base")+filePath.substring(16);
				}
				if(filePath.startsWith("${catalina.home}")){
					filePath = System.getProperty("catalina.home")+filePath.substring(16);
				}
				//获取文件所在路径，并判断路径是否合法
				String pruePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep)+1);
				File file = new File(pruePath);
				if(file.exists() || file.mkdir())
					path = filePath;
			}
		}catch(Exception e){

		}
	}

	/**
	 * 开始记录日志
	 * @param msg 需要记录的信息
	 */
	public void start(String msg){
		try {
			//如果没有，不记录
			if(path==null)
				return;
			//记录sql到日志中
			Logger logKeeper = getOthSysLogger(path);
			logKeeper.info(msg.trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取日志对象
	 * @param path 日志文件路径
	 * @return
	 * @throws IOException
	 */
	private Logger getOthSysLogger(String path) throws IOException{
		//动态创建 Logger
		Logger sys_logger = map.get("RegService_"+fileName);
		if(sys_logger==null){
			sys_logger = RollLoggerUtil.createLogger("RegService_"+fileName);
/*			//设置格式：日期 | 线程名称 | message \n
			Layout layout = new PatternLayout(">>%d|"+fileName+"系统|%m%n");
			//文件记录方式：当文件到达20兆时备份，最多存在10个备份，超过时删除最早的备份
			RollingFileAppender appender = new SqlRollingFileAppender(layout, path);
			appender.setMaxBackupIndex(10);
			appender.setMaxFileSize("20MB");
			sys_logger.addAppender(appender);
			sys_logger.setAdditivity(false);
			sys_logger.setLevel(Level.INFO);*/
			map.put("RegService_"+fileName, sys_logger);
		}
		return sys_logger;
	}
}
