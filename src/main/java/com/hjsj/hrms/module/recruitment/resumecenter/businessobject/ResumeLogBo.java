package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:ResumeBo</p>
 * <p>Description:操作历史类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-02-04</p>
 * @author zx
 * @version 1.0
 * 
 */
public class ResumeLogBo {

	private Connection conn;
	private UserView userview;
	private ContentDAO dao;
	
	public ResumeLogBo(){}
	
	public ResumeLogBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview = userview;
		this.dao = new ContentDAO(this.conn);
	}
	/**
	 * 获取指定人员所有招聘日志记录
	 * @param positionid
	 * @param a0100
	 * @param nbase
	 * @return json字符串
	 * @throws GeneralException
	 */
	public String searchLogs(String positionid,String a0100,String nbase) throws GeneralException{
		ArrayList tem = new ArrayList();
		
		String sql = "select * from zp_opt_history where a0100=? and nbase=? and Position_id=? order by create_time asc";
		ArrayList values = new ArrayList();
		values.add(a0100);
		values.add(nbase);
		values.add(positionid);
		RowSet rs = null;
		try{
			rs = dao.search(sql,values);
			while(rs.next())
				tem.add(this.logInfo(rs.getInt("id"), a0100, nbase, rs.getString("a0101"), rs.getString("log_info"), 
										rs.getString("create_user"), new Date(rs.getDate("create_time").getTime())));
		}catch(Exception e){
			e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return JSONArray.fromObject(tem).toString();
	}
	/**
	 * 将每条日志记录封装到map中
	 * @param id
	 * @param a0100
	 * @param nbase
	 * @param a0101
	 * @param loginfo
	 * @param createuser
	 * @param createtime
	 * @return
	 */
	public HashMap logInfo(int id,String a0100,String nbase,String a0101,String loginfo,String createuser,Date createtime){
		HashMap res = new HashMap();
		res.put("a0100", a0100);
		res.put("a0101", a0101);
		res.put("nbase", nbase);
		res.put("id",id);
		res.put("loginfo", loginfo);
		res.put("createuser", createuser);
		res.put("createtime", DateUtils.format(createtime, "yyyy-MM-dd HH:mm"));
		res.put("html", this.generateHtml(res));
		return res;
	}
	/**
	 * 生成页面html
	 * @param map  包括日志的所有信息
	 * @return
	 */
	private String generateHtml(HashMap map){
		StringBuffer html = new StringBuffer();
		html.append("<div class='hj-wzm-six-bottom-er1' style='margin: 10px 10px 0px; width: 99%; padding-bottom: 10px; border-bottom-width: 1px; border-bottom-color: rgb(213, 213, 213);border-bottom-style: dashed; overflow: hidden;'>");
		html.append("<div style='clear: both;'></div>");
		html.append("<div style='float: left; width: 32px;'>");
		
		PositionBo bo = new PositionBo(this.conn,this.dao, this.userview);
		html.append("<a><img class='img-circle' width='32px' height='32px' style='padding-top:10px;' src='"+bo.getPhotoPath((String)map.get("nbase"), (String)map.get("a0100"))+";bencrypt=true'/></a></div>");
		html.append("<div style='text-align: left; margin-left: 40px;'>");
		
		html.append("<div><p><span class='hj-wzm-six-dd2'>"+ (String)map.get("a0101"));
		html.append("</span>&nbsp;&nbsp;<span class='hj-wzm-six-dd1' style='color:#c3c8c4;'>"+(String)map.get("createtime")+"</span></p>");
		html.append("<textarea name='div_bottom_content' readonly='readonly' style='resize: none; font-family: 微软雅黑; font-size: 12px; width: 95%; height: 36px; overflow: hidden; border: 0px;background: rgb(250, 250, 250);'>");
		html.append((String)map.get("loginfo")+"</textarea>");
		
		return html.toString();
	}
	/**
	 * 新增日志记录
	 * @param log
	 * @throws GeneralException
	 */
	public void addLog(HashMap log) throws GeneralException{
		String sql = "insert into zp_opt_history(id,nbase,a0100,a0101,position_id,log_info,create_time,create_user) values(?,?,?,?,?,?,?,?)";
		IDGenerator idg = new IDGenerator(this.conn);
		try {
			String id = idg.getId("zp_opt_history.id");
			ArrayList values = new ArrayList();
			values.add(id);
			values.add((String)log.get("nbase"));
			values.add((String)log.get("a0100"));
			values.add((String)log.get("a0101"));
			values.add((String)log.get("position_id"));
			values.add((String)log.get("log_info"));
			values.add(java.sql.Date.valueOf((String)log.get("create_time")));
			values.add((String)log.get("create_user"));
			
			dao.insert(sql, values);
		} catch (Exception e) {
			e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
		}
	}
}
