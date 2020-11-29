package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * 
 *<p>SyncPersonSortJob.java</p> 
 *<p>Description:这个类是人员排序字段数据同步到人员视图表A0000字段排序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:January 8, 2018</p> 
 *@author WangBo
 *@version 1.0
 */
public class SyncPersonSortJob implements Job{

	private Category cat = Category.getInstance(SyncPersonSortJob.class);

	/**
	 * 规则：同步人员A0000字段
	 * 1、查询出A0000发生改变的人员
	 * 2、修改为正确的A0000值
	 * 3、外部系统状态为 2 //修改状态
	 */
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//获取数据视图配置xml
		RecordVo vo=ConstantParamter.getRealConstantVo("SYS_EXPORT_VIEW");
		if(vo ==null) {
			return;
		}
		if(vo.getString("str_value") == null || vo.getString("str_value").trim().length()==0 || vo.getString("str_value").toLowerCase().indexOf("xml") == -1) {
			return;
		}
		Document doc=null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			doc = PubFunc.generateDom(vo.getString("str_value").toLowerCase());//解析XML 
			Element root = doc.getRootElement(); // 取得根节点 
			Element childnode = root.getChild("base"); //获取人员库
			if(childnode == null || "".equals(childnode.getText()))//不存在人员库参数配置
			{
				return;
			}
			String base = childnode.getText();// Usr,Oth,Ret 
			String[] bases = base.split(",");
			conn = AdminDb.getConnection();
			//获取外部同步系统字段
			ArrayList outlist= getOutSysSyncColumns();
			for(int i = 0 ; i < bases.length ; i++){
				StringBuffer sql = new StringBuffer();
				sql.append("update t_hr_view set A0000=(");
				sql.append("select A0000 from " +bases[i]+ "A01 A where A.GUIDKEY=unique_id");
				sql.append(")");
				for(int j = 0 ; j < outlist.size() ; j++){
					String sys_id = (String) outlist.get(j);
					sql.append(","+ sys_id +"=case when "+ sys_id +"=0 and sys_flag<>3 then 2 else "+ sys_id +" end");
				}
				if(Sql_switcher.searchDbServer() == 2) //oracle 库
				{
					sql.append(" where nvl(A0000,'-1')<>(select A0000 from " +bases[i]+ "A01 A where A.GUIDKEY=unique_id)");
				} else // sql server
				{
					sql.append(" where isnull(A0000,'-1')<>(select A0000 from " +bases[i]+ "A01 A where A.GUIDKEY=unique_id)");
				}
				try {
					ps = conn.prepareStatement(sql.toString());
					ps.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					PubFunc.closeDbObj(ps);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(ps);
			PubFunc.closeDbObj(conn);
		}
	}
	/**
	 * 获取同步外部系统字段
	 * @return 外部系统字段集合
	 */
	private ArrayList getOutSysSyncColumns(){
		ArrayList list = new ArrayList();
		String sql = "SELECT sys_id FROM t_sys_outsync WHERE state=1";
		Connection conn =null;
		PreparedStatement ps =null;
		ResultSet rs =null;
		try {
			conn = AdminDb.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString("sys_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(ps);
			PubFunc.closeDbObj(conn);
		}
		return list;
	}

}
