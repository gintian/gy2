package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.RollLoggerUtil;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import org.apache.log4j.Category;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DrLogger {
	private String path ;//文件路径
	private String unitcode ;//单位编码
	private String fileName ;//日志文件名

	private Category log = Category.getInstance(this.getClass().getName());
	//记录sql执行日志logger
	private static Map<String, Logger> map = new HashMap<String, Logger>();
	/**
	 * 分布式上报日志工具类
	 * @param unitcode 单位编码
	 * @param flag 生成数据 0;接收数据1;
	 */
	public DrLogger(String unitcode,int flag,Date date) {
		Connection conn = null;
		try {
			this.unitcode = unitcode;
			conn = AdminDb.getConnection();
			ConstantXml constantXml = new ConstantXml(conn, "FBTB_FILEPATH");
			path = constantXml.getNodeAttributeValue("/filepath", "rootpath")+File.separator+"asyn"+File.separator;
			if (0==flag) {
				path+="asynreport"+File.separator+"logs"+File.separator+PubFunc.FormatDate(date, "yyyy-MM");
				fileName = "bp"+unitcode+"_"+PubFunc.FormatDate(date, "yyyyMMddHHmmss")+".log";
			}else {
				path+="asynrecive"+File.separator+"logs"+File.separator+PubFunc.FormatDate(date, "yyyy-MM");
				fileName = "js"+unitcode+"_"+PubFunc.FormatDate(date, "yyyyMMddHHmmss")+".log";
			}
			File file = new File(path+File.separator+fileName);
			if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
				file.getParentFile().mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(conn);
		}
	}
	/**
	 * 将信息记录进入日志文件
	 * @param msg 要记录的信息
	 */
	public void write(String msg){
		try {
			//如果没有，不记录
			if(path==null)
				return;
			//记录sql到日志中
			Logger logKeeper = getOthSysLogger();
			logKeeper.info(msg.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取日志对象
	 * @param path 日志文件路径
	 * @return
	 * @throws IOException
	 */
	private Logger getOthSysLogger() throws IOException{
		//动态创建 Logger
		Logger sys_logger = null;
		try {
			sys_logger = map.get(fileName);
			if(sys_logger==null){
				sys_logger =  RollLoggerUtil .createLogger(path+File.separator+fileName);//Logger.getLogger(path+File.separator+fileName);
				CodeItem codeItem =AdminCode.getCode("UN", unitcode);

				String unitname = "";
				if (null!=codeItem) {
					unitname = codeItem.getCodename();
				}else {
					log.error(unitcode+"未找到对应单位");
				}
	     /*       //设置格式：日期 | 单位名称 | message \n
	            Layout layout = new PatternLayout(">>%d|"+unitname+"|%m%n");
	            //文件记录方式：当文件到达20兆时备份，最多存在10个备份，超过时删除最早的备份
	            appender = new SqlRollingFileAppender(layout, path+File.separator+fileName);
	            appender.setMaxBackupIndex(10);
	            appender.setMaxFileSize("50MB");
	            sys_logger.addAppender(appender);
	            sys_logger.setAdditivity(false);
	            sys_logger.setLevel(Level.INFO);*/
				map.put(fileName, sys_logger);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sys_logger;
	}
}
