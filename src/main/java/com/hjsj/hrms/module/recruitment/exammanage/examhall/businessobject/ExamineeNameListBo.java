package com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：ExamineeNameListBo 
 * 类描述：考生名单Bo类
 * 创建人：sunming 
 * 创建时间：2015-11-3
 * 
 * @version
 */
public class ExamineeNameListBo {
	private Connection conn = null;
	/** 登录用户 */
	private UserView userview;
	public ExamineeNameListBo(Connection conn, UserView userview) {
		this.conn=conn;
		this.userview=userview;
	}

	/**
	 * 拼接column的方法
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<ColumnsInfo> getColumnList() throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		RowSet rs = null;
		try {
			String sql = "select e.nbase,e.a0100,e.A0101,z.z6301,e.Z0321,z.z0351";
			FieldItem A0410 = DataDictionary.getFieldItem("A0410");
			FieldItem A0405 = DataDictionary.getFieldItem("A0405");
			if(A0410!=null&&"1".equals(A0410.getUseflag()))
				sql=sql+",o.A0410";
			if(A0405!=null&&"1".equals(A0405.getUseflag()))
				sql=sql+",o.A0405";
			sql = sql+", e.exam_hall_id,e.hall_id,e.seat_id from zp_exam_assign e,Z63 z ";
			if(!(A0410==null&&A0405==null))
				sql = sql+",OthA04 o ";
			sql = sql +"where e.nbase=z.nbase and e.A0100=z.A0100 ";
			if(!(A0410==null&&A0405==null))
				sql = sql +"and o.A0100=e.A0100  and o.i9999 in (select MAX(i9999) from OthA04 ,z63 where otha04.A0100=z63.A0100 group by OthA04.a0100) ";
			sql = sql +" and 1=2";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			String itemid = "";
			String itemdesc = "";
			String itemtype = "";
			int decimalwidth = 0;
			int itemlength=0;
			String codesetid = "";
			String state = "";
			FieldItem item = new FieldItem();
			item.setItemid(itemid);
			ColumnsInfo info = new ColumnsInfo();
			for(int i = 1;i<=metadata.getColumnCount();i++){
				itemid = metadata.getColumnName(i).toLowerCase();
				if("nbase".equalsIgnoreCase(itemid)){
					itemdesc = "nbase";
					itemtype = "A";
					itemlength = 3;
					codesetid = "0";
				}
				if("a0100".equalsIgnoreCase(itemid)){
					itemdesc = "人员编号";
					itemtype = "A";
					itemlength = 8;
					codesetid = "0";
				}
				if("a0101".equalsIgnoreCase(itemid)){
					itemdesc = "姓名";
					itemtype = "A";
					itemlength = 50;
					codesetid = "0";
				}
				if("z6301".equalsIgnoreCase(itemid)){
					itemdesc = "准考证号";
					itemtype = "A";
					itemlength = 100;
					codesetid = "0";
				}
				if("z0321".equalsIgnoreCase(itemid)){
					itemdesc = "需求单位";
					itemtype = "A";
					itemlength = 30;
					codesetid = "UN";
				}
				if("z0351".equalsIgnoreCase(itemid)){
					itemdesc = "申请职位";
					itemtype = "A";
					itemlength = 50;
					codesetid = "0";
				}
				if("a0410".equalsIgnoreCase(itemid)){
					itemdesc = "专业";
					itemtype = "A";
					itemlength = 6;
					codesetid = "AI";
				}
				if("a0405".equalsIgnoreCase(itemid)){
					itemdesc = "学历";
					itemtype = "A";
					itemlength = 2;
					codesetid = "AM";
				}
				if("exam_hall_id".equalsIgnoreCase(itemid)){
					itemdesc = "exam_hall_id";
					itemtype = "A";
					itemlength = 50;
					codesetid = "0";
				}
				if("hall_id".equalsIgnoreCase(itemid)){
					itemdesc = "hall_id";
					itemtype = "A";
					itemlength = 50;
					codesetid = "0";
				}
				if("seat_id".equalsIgnoreCase(itemid)){
					itemdesc = "seat_id";
					itemtype = "A";
					itemlength = 50;
					codesetid = "0";
				}
				
				item.setItemid(itemid);
				item.setItemdesc(itemdesc);
				item.setItemtype(itemtype);
				item.setDecimalwidth(decimalwidth);
				item.setItemlength(itemlength);
				item.setCodesetid(codesetid);
				info = new ColumnsInfo(item);
				info.setColumnWidth(150);
				if("nbase".equalsIgnoreCase(itemid)||"seat_id".equalsIgnoreCase(itemid)||
						"exam_hall_id".equalsIgnoreCase(itemid)||"hall_id".equalsIgnoreCase(itemid)
						||"a0100".equalsIgnoreCase(itemid)){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				list.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}

	/**
	 * 获取sql的方法
	 * @param hallId 考场id
	 * @return
	 * @throws GeneralException
	 */
	public String getDataSql(String hallId) throws GeneralException {
		EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn,"0");
		StringBuffer sql=new StringBuffer();
		try {
			String pre = employNetPortalBo.getZpkdbName();
			sql = new StringBuffer("select e.nbase,e.a0100,e.A0101,z.z6301,e.Z0321,z.z0351");
			FieldItem A0410 = DataDictionary.getFieldItem("A0410");
			FieldItem A0405 = DataDictionary.getFieldItem("A0405");
			if(A0410!=null&&"1".equals(A0410.getUseflag()))
				sql.append(",o.A0410");
			if(A0405!=null&&"1".equals(A0405.getUseflag()))
				sql.append(",o.A0405");
			sql.append(", e.exam_hall_id,e.hall_id,e.seat_id " +
					"from zp_exam_assign e left join Z63 z on e.nbase=z.nbase and e.A0100=z.A0100 and e.z0301=z.z0301 ");
			if(!(A0410==null&&A0405==null))
				sql.append("left join (select a1.* from "+pre+"A04 a1,(select max(i9999) i9999, a0100 from "+pre+"A04 group by a0100) a2 where a1.a0100 = a2.a0100 and a1.i9999 = a2.i9999) o on o.A0100=e.A0100 ");
			sql.append(" where 1=1 ");
			if(hallId!=null && hallId.length()>0){
				sql.append(" and e.exam_hall_id='").append(hallId).append("'");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
	/**
	 * 拼接buttonList的方法
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getButtonList() throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			buttonList.add("-");
			buttonList.add(new ButtonInfo("移出考场","examineenamelist.removeHall"));//移出考场
			buttonList.add("-");
			buttonList.add(new ButtonInfo("返回","examineenamelist.returnBack"));//移除考场

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	/**
	 * 移除考场
	 * @param list 考生id
	 * @param hallId 考场id
	 * @throws GeneralException 
	 */
	public void removeExamineeNameList(ArrayList list, String hallId) throws GeneralException {
		try {
			ArrayList sqlList = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append("update zp_exam_assign set exam_hall_id=null,hall_id=null,hall_name=null,seat_id=null");
			buf.append(" where exam_hall_id=? and a0100 in ");
			
			StringBuffer tem = new StringBuffer(); 
			tem.append("(");
			sqlList.add(hallId);
			for(int i=0;i<list.size();i++){
				String a0100 = (String) list.get(i);
				tem.append(a0100);
				if(i<list.size()-1){
					tem.append(",");
				}
			}
			tem.append(")");
			
			buf.append(tem.toString());
			String hallSql = "update zp_exam_hall set people_num=people_num-? where id=?";
			String z63Sql = "update z63 set z6301=null where z0301 in " +
					"(select ass.z0301 from zp_exam_assign ass,zp_exam_hall hall where  ass.exam_hall_id=hall.id" +
					" and hall.id="+hallId+" and ass.a0100 in "+tem.toString()+") and a0100 in "+tem.toString();
			
			//删除移除考生的准考证号
			dao.update(z63Sql);
			//清除考场分派记录
			dao.update(buf.toString(),sqlList);
			sqlList.clear();
			//去除list中重复元素(更新考场中已安排考场人数)
			HashSet h = new HashSet(list);
			list.clear();
			list.addAll(h);
			sqlList.add(list.size());
			sqlList.add(hallId);
			dao.update(hallSql, sqlList);
			
			/**
			 * 清除考生所有科目的考试时间
			 */
			ExamineeBo bo = new ExamineeBo(this.conn,this.userview);
			ArrayList timeFields = bo.getExamTime();
			StringBuffer timeSql = new StringBuffer("update zp_exam_assign set ");
			String temp = "";
    		for (int i = 0; i < timeFields.size(); i++) {
    			temp = (String) timeFields.get(i);
    			timeSql.append(temp+"=null,");
			}
    		timeSql.setLength(timeSql.length()-1);
    		timeSql.append(" where a0100 in "+ tem.toString());
    		dao.update(timeSql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}


}
