package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title:SyncSysDataServlet</p>
 * <p>Description:集群环境同步刷新数据字典、加载动态参数接口类</p>
 * <p>Company:hjsj</p>
 * <p>create time: 2017-6-26 下午4:28:29</p>
 * @author wangbo
 * @version 1.0
 *
 */
public class SyncSysDataServlet extends HttpServlet{
	
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Connection conn=null;
		try {
			String param=req.getParameter("param");
			param=PubFunc.decrypt(param);//解密
			String[] params=param.split(",");
			//获取发送请求当前时间
			String timestamp = params[1].split("=")[1];
			if(timestamp == null || timestamp.length()<1)
				return;
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date reqTime = dateFormat.parse(timestamp);
			//如果时间相差超过300秒，不予执行
			if(now.getTime() - reqTime.getTime()>300000)
				return;
			//获取请求发起服务器地址
			String url = req.getRemoteAddr();
			String clusterEnvironment = SystemConfig.getPropertyValue("cluster_environment");
			if(clusterEnvironment.length()<1)
				return;
			if(clusterEnvironment.indexOf(url)==-1)
				return;
			//获取同步操作类型
			String type = params[0].split("=")[1];
			conn = AdminDb.getConnection();
			SyncSystemUtilBo SSUtilBo = new SyncSystemUtilBo(conn);
			//执行同步操作
			SSUtilBo.doSync(Integer.parseInt(type),req);
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (GeneralException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
	}

}
