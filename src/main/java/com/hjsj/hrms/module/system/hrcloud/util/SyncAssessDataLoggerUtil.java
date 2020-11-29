package com.hjsj.hrms.module.system.hrcloud.util;

import com.hjsj.hrms.utils.RollLoggerUtil;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 云考核数据同步日志
 *
 * 19/6/29
 * @author xus
 *
 */
public class SyncAssessDataLoggerUtil {
	private static String path = null;

	private static String no_name_path = null;
	//记录sql执行日志logger
	private static Logger sql_logger = null;

	static{

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


				//默认日志文件名为assessData+date.log
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				String date = sdf.format(new Date());
				filePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep)+1)+"assessData"+date+".log";
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
				if(file.exists() || file.mkdir()){
					no_name_path = pruePath;
					path = filePath;
				}
			}

		}catch(Exception e){

		}


	}

	/**
	 * 开始保存云同步日志
	 * @param sql
	 * @param obj 预处理sql参数
	 */
	public static void start(String interfacename,JSONArray params,String result){

		try {
			//如果没有，不记录
			if(path==null)
				return;
			//记录sql到日志中
			Logger sqlKeeper = getSqlLogger(path);
			/*
			 * interfacename:考核数据撤销| time:2019-05-06 12:20:53 | 	params:{.....} | 	result:success
			 */
			String loginfo = "interfacename:"+interfacename+" | params:"+params.toString()+" | result:"+result;
			sqlKeeper.error(loginfo);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取日志对象
	 * @param path 日志文件路径
	 * @return Logger
	 * @throws IOException
	 */
	private static Logger getSqlLogger(String path) throws IOException{
		//动态创建 Logger
		if(sql_logger==null){
			sql_logger = RollLoggerUtil.createLogger("cloudassesskeeper");
/*			sql_logger = Logger.getLogger("cloudassesskeeper");
			//设置格式：日期 | 线程名称 | message \n
			Layout layout = new PatternLayout(">>%d|%t|%m%n");
			//文件记录方式：当文件到达50兆时备份，最多存在10个备份，超过时删除最早的备份
			RollingFileAppender appender = new RollingFileAppender(layout, path);
//			DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, path,"'.'yyyy-MM-dd");
			appender.setMaxBackupIndex(10);
			//文件大小读取参数配置，如果没有配置，默认为50MB
			String filesize = SystemConfig.getPropertyValue("cloud_log_filesize");
			filesize = filesize==null || filesize.length()<1?"50MB":filesize;
			appender.setMaxFileSize(filesize);
			sql_logger.addAppender(appender);
			sql_logger.setAdditivity(false);
			sql_logger.setLevel(Level.INFO);*/
		}

		return sql_logger;
	}

	/**
	 * 获取数据日志文件集合
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ArrayList getCloudFileList() throws FileNotFoundException, IOException {
		ArrayList fileList = new ArrayList();
		try {
			String filePath = path.substring(0, path.lastIndexOf(System.getProperty("file.separator")));
			File file = new File(filePath);
			if (!file.isDirectory()) {
				throw new FileNotFoundException("未获取到文件路径");
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filePath + System.getProperty("file.separator") + filelist[i]);
					if (readfile.isFile()) {
						if(filelist[i].indexOf("assessData")>-1){
							fileList.add(filelist[i]);
						}
					} else if (readfile.isDirectory()) {
						throw new FileNotFoundException("未获取到文件");
//                             readfile(filepath + "\\" + filelist[i]);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(fileList.size()>0){
			fileList = sortFileByName(fileList);
		}
		return fileList;
	}

	public static String getNo_name_path() {
		return no_name_path;
	}
	/**
	 *名称倒序排列
	 * @param files
	 * @return
	 */
	public static ArrayList sortFileByName(ArrayList files) {
		ArrayList newList = new ArrayList();
		Object[] files1 = files.toArray();
		Arrays.sort(files1);
		for(int i = files1.length-1;i>=0;i--){
			newList.add(files1[i]);
		}
		return newList;
	}

	/**
	 * 清除云考核结果数据日志
	 */
	public static void cleanCloudFileList(){
		String filePath = path.substring(0, path.lastIndexOf(System.getProperty("file.separator")));
		File file = new File(filePath);
		if (!file.isDirectory()) {
			return;
		} else {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filePath + System.getProperty("file.separator") + filelist[i]);
				if (readfile.isFile()) {
					if(filelist[i].indexOf("assessData")>-1){
						readfile.delete();
					}
				}
			}
		}
	}
}
