/**
 * 
 */
package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title: ResumeEvaluationBo </p>
 * <p>Description: 简历评价类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-27 上午10:12:54</p>
 * @author zhaoxj
 * @version 1.0
 */
public class ResumeEvaluationBo {
    
    private Connection conn;
    private UserView userView;
    private ContentDAO dao;
    
    public ResumeEvaluationBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.dao = new ContentDAO(conn);
    }
    
    /**
     * 新增简历评价信息
     * @Title: addEvaluation   
     * @Description: 新增简历评价信息   
     * @param evaluation 评价数据（评价人、评价对象、评价分数、评价语等）
     * @return
     */
    public boolean addEvaluation(HashMap evaluation) {
        boolean isOK = false;
        
        //新增简历评价信息
        IDGenerator idg = new IDGenerator(2, this.conn);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String now = sdf.format(new Date());// 获取当前系统时间
		String flg = evaluation.get("flg")==null?"":evaluation.get("flg").toString();//0或其他：直接新增评价   1：添加评价人记录
        try {
        	String id = idg.getId("zp_evaluation.id");//参数从系统管理-应用管理-参数设置-序号维护中获取			
        	StringBuffer sql = new StringBuffer("insert into zp_evaluation(id,nbase_object,a0100_object,nbase,a0100,score,content,");
        	if(!"1".equals(flg))
        	{        		
        		sql.append("eval_time,");
        	}
        	sql.append("create_user,create_fullname,create_time) values(?,?,?,?,?,?,?,");
        	if(!"1".equals(flg))
        	{        		
        		sql.append("?,");
        	}
        	sql.append("?,?,?)");
        	ArrayList value= new ArrayList();
        	value.add(id);
        	value.add(evaluation.get("nbase_object"));
        	value.add(evaluation.get("a0100_object"));
        	value.add(evaluation.get("nbase"));
        	value.add(evaluation.get("a0100"));
        	value.add(evaluation.get("score"));
        	value.add(evaluation.get("content"));
        	if(!"1".equals(flg))
        	{        		
        		value.add(java.sql.Timestamp.valueOf(now));
        	}
        	value.add(this.userView.getUserName());
        	value.add(this.userView.getUserFullName());
        	value.add(java.sql.Timestamp.valueOf(now));
        	int num = dao.insert(sql.toString(), value);
        	if(num>0)
        	{
        		isOK = true;
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return isOK;
    }
    
    /**
     * 更新简历评价信息
     * @Title: updateEvaluation   
     * @Description: 更新简历评价信息   
     * @param evaluation 评价数据（评价人、评价对象、评价分数、评价语等）
     * @return
     */
    public boolean updateEvaluation(HashMap evaluation) {
        boolean isOK = false;
        
        // 更新简历评估数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String now = sdf.format(new Date());// 获取当前系统时间
        try {
			StringBuffer sql = new StringBuffer();
			sql.append("update zp_evaluation set content=?,score=?,eval_time=? where  nbase_object=? and a0100_object=? and nbase=? and a0100=?");
			ArrayList value = new ArrayList();
			value.add(evaluation.get("content"));
			value.add(evaluation.get("score"));
        	value.add(java.sql.Timestamp.valueOf(now));
			value.add(evaluation.get("nbase_object"));
			value.add(evaluation.get("a0100_object"));
        	value.add(evaluation.get("nbase"));
        	value.add(evaluation.get("a0100"));
			
			int num = dao.update(sql.toString(), value);
			if(num>0)
			{
				isOK = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return isOK;        
    }
    
    
    /***
     * 查询评价列表
    * @Title:getEvaluationList
    * @Description：
    * @author xiexd
    * @param nbase_o被评价人人员库
    * @param a0100_o被评价人人员编号
    * @param nbase评价人人员库
    * @param a0100评价人人员编号
    * @return
     */
    public ArrayList getEvaluationList(String nbase_o,String a0100_o,String nbase,String a0100,int flg)
    {
    	ArrayList evaluationList = new ArrayList();
    	try {
			StringBuffer sql = new StringBuffer();
			ArrayList value = new ArrayList();
			sql.append("select id,nbase_object,a0100_object,nbase,a0100,score,content,eval_time,create_user,create_fullname,create_time from zp_evaluation where ");
			sql.append(" nbase_object=? and a0100_object=? ");
			value.add(nbase_o);
			value.add(a0100_o);
			if(nbase!=null&&!"".equals(nbase)&&a0100!=null&&!"".equals(a0100))
			{
				if(flg==0)
				{
					sql.append("  and nbase=? and a0100=?");
					value.add(nbase);
				}else{
					sql.append("  and a0100!=?");
				}
				value.add(a0100);
			}
			ContentDAO dao = new ContentDAO(conn); 
			RowSet rs = dao.search(sql.toString(),value);
			LazyDynaBean bean = new LazyDynaBean();
			while(rs.next())
			{
				//如果评价人已经不存在评价信息不显示
				LazyDynaBean userInfo = this.getUserInfo(rs.getString("nbase"), rs.getString("a0100"));
				if(userInfo == null)
					continue;
				bean = new LazyDynaBean();
				bean.set("id", rs.getString("id"));
				bean.set("nbase_object", rs.getString("nbase_object"));
				bean.set("a0100_object", rs.getString("a0100_object"));
				bean.set("nbase", rs.getString("nbase"));
				bean.set("a0100", rs.getString("a0100"));
				bean.set("score", rs.getString("score")==null?"0":rs.getString("score"));
				bean.set("content", rs.getString("content")==null|| "".equals(rs.getString("content"))?"未填写评语":rs.getString("content").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").replace("\n", "<BR/>").replace(" ", "&nbsp;"));
				bean.set("eval_time", rs.getDate("eval_time"));
				bean.set("create_user", rs.getString("create_user"));
				bean.set("create_fullname", rs.getString("create_fullname"));
				bean.set("create_time", rs.getDate("create_time"));
				bean.set("username", userInfo.get("a0101"));
				bean.set("ta", userInfo.get("ta"));
				evaluationList.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return evaluationList;
    }
    
    /***
     * 获取当前人员姓名
    * @Title:getUserInfo
    * @Description：
    * @author xiexd
    * @param nbase
    * @param a0100
    * @return
     */
    public LazyDynaBean getUserInfo(String nbase,String a0100)
    {
    	LazyDynaBean bean = null;
    	try {
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select a0101,a0107 from  "+nbase+"A01  where  a0100='"+a0100+"'";
			RowSet rs = dao.search(sql);
			String sex = "";
			if(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("a0101", rs.getString("a0101"));
				sex = rs.getString("a0107");
				if(sex!=null&&"1".equalsIgnoreCase(sex)){
					sex = "他";
				}else if(sex!=null&&"2".equalsIgnoreCase(sex)){
					sex = "她";
				}else{
					sex = "";
				}
				bean.set("ta", sex);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return bean;
    }
}
