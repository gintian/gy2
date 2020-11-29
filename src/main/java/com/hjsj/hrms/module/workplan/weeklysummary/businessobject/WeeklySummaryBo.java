package com.hjsj.hrms.module.workplan.weeklysummary.businessobject;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.worklog.businessobject.WorkLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.util.HSSFColor;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作总结-周总结
 * 
 * @createtime Dec 07, 2016 9:07:55 PM
 * @author chent
 */
public class WeeklySummaryBo {

	private Connection conn = null;
	public Connection getConn() {
		return conn;
	}

	public UserView getUserview() {
		return userview;
	}

	private UserView userview;

	public WeeklySummaryBo(Connection conn, UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}

	/**
	 * 获取本期工作总结
	 * 
	 * @return objectivedata 数据源
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getData_1(int p0100) throws GeneralException {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select p1900,p0100,p1901,p1903,p1905,p1907,p1919 from p19 where p0100=? and p1917='2'";

			rs = dao.search(sql, Arrays.asList(p0100));
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("p1900", rs.getString("p1900"));
				map.put("p0100", rs.getString("p0100"));
				map.put("p1901", rs.getString("p1901"));
				map.put("p1903", rs.getString("p1903"));
				map.put("p1905", rs.getString("p1905"));
				map.put("p1907", Sql_switcher.readMemo(rs, "P1907"));
				map.put("p1919", rs.getString("p1919"));

				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;

	}

	/**
	 * 本周工作日志
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getData_2(int p0100) throws GeneralException {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			
			ArrayList<Integer> p0100_diary_List = this.getDiaryP0100ByWeekly(p0100);
			if(p0100_diary_List.size() == 0){
				return list;
			}
			StringBuilder sqlIn = new StringBuilder();
			for (int i = 0; i < p0100_diary_List.size(); i++) {
				sqlIn.append("'").append(p0100_diary_List.get(i)).append("',");
			}
			sqlIn.deleteCharAt(sqlIn.length()-1);
			//linbz  取时间类型参数改为getTimestamp
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String sql = "select content, finish_desc, start_time, end_time, work_time, other_desc, work_type from per_diary_content where P0100 in (" + sqlIn + ")";
			rs = dao.search(sql);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("content", Sql_switcher.readMemo(rs, "content"));
				map.put("work_type", rs.getString("work_type"));
				map.put("finish_desc", Sql_switcher.readMemo(rs, "finish_desc"));
				map.put("start_time", df.format(rs.getTimestamp("start_time")));
				map.put("end_time", df.format(rs.getTimestamp("end_time")));
				map.put("work_time", String.valueOf(rs.getInt("work_time")));
				map.put("other_desc", Sql_switcher.readMemo(rs, "other_desc"));

				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;

	}

	/**
	 * 下期工作计划
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<HashMap<String, String>> getData_3(int p0100) throws GeneralException {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select p1900,p0100,p1901,p1903,p1905,p1907 from p19 where p0100=? and p1917='1'";

			rs = dao.search(sql, Arrays.asList(p0100));
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("p1900", rs.getString("p1900"));
				map.put("p0100", rs.getString("p0100"));
				map.put("p1901", rs.getString("p1901"));
				map.put("p1903", rs.getString("p1903"));
				map.put("p1905", rs.getString("p1905"));
				map.put("p1907", Sql_switcher.readMemo(rs, "P1907"));

				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;

	}
	/**
	 * 培训需求
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public String getData_4(int p0100, String field) throws GeneralException {
		String value = "";
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select "+field+" from p01 where p0100=?";
			rs = dao.search(sql, Arrays.asList(p0100));
			
			if(rs.next()) {
				value = Sql_switcher.readMemo(rs, field);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return value;
		
	}

	/**
	 * 汇总
	 * @param p0100_current_week:本周总结号
	 * @param p0100_pre_week:上周总结号
	 * @throws GeneralException
	 */
	public void collect(int p0100_current_week, int p0100_pre_week) throws GeneralException {

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs1 = null;
		RowSet rs2 = null;
		try {

			StringBuilder batchInsertSql = new StringBuilder();
			ArrayList batchInsertList = new ArrayList();
			batchInsertSql.append("insert into p19 (P1900, P0100, P1901, P1903, P1905, P1907, P1909, P1911, P1913, P1915, P1917, P1919) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			/** 上周工作计划汇总到本周工作总结，总结来源记为1 */ 
			rs = dao.search(" select * from p19 where P0100=? and P1917='1'", Arrays.asList(p0100_pre_week));
			while (rs.next()) {

				String p1903 = rs.getString("p1903");
				// 总结中是否有相同名称的任务？如果有说明汇总过，更新即可；没有则新增。
				rs1 = dao.search("select p1900 from p19 where p1903=? and p0100=? and p1917='2'", Arrays.asList(p1903, p0100_current_week));
				if(rs1.next()){//存在则更新，只更新来源
					int p1900 = rs1.getInt("p1900");
					RecordVo vo = new RecordVo("p19");
					vo.setInt("p1900", p1900);
					vo = dao.findByPrimaryKey(vo);
					vo.setString("p1919", "1");
					dao.updateValueObject(vo);
					
				} else {
					IDFactoryBean idf = new IDFactoryBean();
					ArrayList dataList = new ArrayList();
					dataList.add(Integer.parseInt(idf.getId("P19.P1900", "", this.conn)));
					dataList.add(p0100_current_week);
					dataList.add(rs.getString("P1901"));
					dataList.add(p1903);
					dataList.add(rs.getInt("P1905"));
					dataList.add(Sql_switcher.readMemo(rs, "P1907"));
					dataList.add(rs.getString("P1909"));
					dataList.add(rs.getDate("P1911"));
					dataList.add(rs.getString("P1913"));
					dataList.add(rs.getDate("P1915"));
					dataList.add("2");// 记录类型 1：计划；2：总结
					dataList.add("1");// 总结来源 1：来自于计划；2：来自于工作日志中非计划部分；3：手工添加
					batchInsertList.add(dataList);
				}
			}
			dao.batchInsert(batchInsertSql.toString(), batchInsertList);

			/** 首钢周报只引入上周计划而不引入日报方案 先行注释 **/
			/** 本周工作日志汇总到本周工作总结，总结来源记为2 */ 
			/**	
			batchInsertList = new ArrayList();
			
			ArrayList<Integer> p0100_diary_List = this.getDiaryP0100ByWeekly(p0100_current_week);
			if(p0100_diary_List.size() == 0){
				return ;
			}
			StringBuilder sqlIn = new StringBuilder();
			for (int i = 0; i < p0100_diary_List.size(); i++) {
				sqlIn.append("'").append(p0100_diary_List.get(i)).append("',");
			}
			sqlIn.deleteCharAt(sqlIn.length()-1);
			String sql = "select * from per_diary_content where P0100 in (" + sqlIn + ")";
			rs = dao.search(sql);
			while (rs.next()) {
				String content = rs.getString("content");
				// 本周周总结中的计划是否存在与日志名相同的？如果有则更新成来自于日志；没有则继续判断。
				rs1 = dao.search("select p1900 from p19 where p1903='"+content+"' and p0100="+p0100_current_week+" and p1917='1'");
				if(rs1.next()){//存在则更新
					int p1900 = rs1.getInt("p1900");
					RecordVo vo = new RecordVo("p19");
					vo.setInt("p1900", p1900);
					vo = dao.findByPrimaryKey(vo);
					
					vo.setString("p1901", rs1.getString("work_type"));
					vo.setInt("p1905", rs1.getInt("work_time"));
					vo.setString("p1919", "2");
					dao.updateValueObject(vo);
					
				} else {
					// 本周周总结中的总结并且来自于日志的记录中，是否存在与日志名相同的？有的话说明汇总过，则更新成来自于日志；没有则新增。
					rs2 = dao.search("select p1900,p1901,p1905 from p19 where p1903='"+content+"' and p0100="+p0100_current_week+" and p1917='2' and p1919='2'");
					if(rs2.next()){//存在则更新
						int p1900 = rs2.getInt("p1900");
						RecordVo vo = new RecordVo("p19");
						vo.setInt("p1900", p1900);
						vo = dao.findByPrimaryKey(vo);
						
						vo.setString("p1901", rs2.getString("p1901"));
						vo.setInt("p1905", rs2.getInt("p1905"));
						vo.setString("p1919", "2");
						dao.updateValueObject(vo);
						
					} else {
						IDFactoryBean idf = new IDFactoryBean();
						ArrayList dataList = new ArrayList();
						dataList.add(Integer.parseInt(idf.getId("P19.P1900", "", this.conn)));
						dataList.add(p0100_current_week);
						dataList.add(rs.getString("work_type"));// 任务类别 // 01例行工作/02重点工作/03其它工作
						dataList.add(content); // 任务名称
						dataList.add(rs.getInt("work_time"));// 耗时（分钟）
						dataList.add("");// 任务总结
						dataList.add("");// 创建人
						dataList.add(new java.sql.Date(new java.util.Date().getTime()));// 创建日期
						dataList.add("");// 修改人
						dataList.add(new java.sql.Date(new java.util.Date().getTime()));// 修改日期
						dataList.add("2");// 记录类型 1：计划；2：总结
						dataList.add("2");// 总结来源 1：来自于计划；2：来自于工作日志中非计划部分；3：手工添加
						batchInsertList.add(dataList);
					}
				}

			}
			dao.batchInsert(batchInsertSql.toString(), batchInsertList);
			**/
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

	}

	/**
	 * 通过周总结号获取日志的总结号
	 * @param p0100_week：周总结号
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<Integer> getDiaryP0100ByWeekly(int p0100_week) throws GeneralException {
		ArrayList<Integer> list = new ArrayList<Integer>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select a.p0100,a.p0115 from P01 a,P01 b where a.nbase=b.nbase and a.a0100=b.a0100 and a.P0104 between b.P0104 and b.P0106 and a.state=0 and b.P0100=? ", Arrays.asList(p0100_week));
			while (rs.next()) {
				String p0115 = rs.getString("p0115");
				if(!("02".equals(p0115) || "03".equals(p0115))){
					continue ;
				}
				int p0100 = rs.getInt("p0100");
				list.add(p0100);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 添加记录
	 * @param type：1、本期工作总结 3、下期工作计划
	 * @param p0100：总结号
	 * @param p1901：任务类别
	 * @param p1903：任务名称
	 * @return
	 * @throws GeneralException
	 */
	public HashMap addRecord(String type, int p0100, String p1901, String p1903) throws GeneralException {
		HashMap map = new HashMap();

		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("P19");
			IDFactoryBean idf = new IDFactoryBean();
			vo.setInt("p1900", Integer.parseInt(idf.getId("P19.P1900", "", this.conn)));
			vo.setInt("p0100", p0100);
			vo.setString("p1901", p1901);// 任务类别 01例行工作/02重点工作/03其它工作
			vo.setString("p1903", p1903);// 任务名称
			String p1917 = "";
			if ("1".equals(type)) {
				p1917 = "2";
			} else if ("3".equals(type)) {
				p1917 = "1";
			}
			vo.setString("p1917", p1917);// 记录类型 1：计划；2：总结
			vo.setString("p1919", "3");// 总结来源 1：来自于计划；2：来自于工作日志中非计划部分；3：手工添加

			int result = dao.addValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");
			map.put("p1900", vo.getInt("p1900"));

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return map;
	}

	/**
	 * 更新记录
	 * @param p1900：总结号
	 * @param field：目标字段
	 * @param value：目标值
	 * @return
	 * @throws GeneralException
	 */
	public HashMap updateRecord(int p1900, String field, String value) throws GeneralException {
		HashMap map = new HashMap();

		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("p19");
			vo.setInt("p1900", p1900);
			vo = dao.findByPrimaryKey(vo);
			vo.setString(field, value);
			int result = dao.updateValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return map;
	}
	/**
	 * 更新培训需求
	 * @param p0100：总结号
	 * @param field：目标字段
	 * @param value：目标值
	 * @return
	 * @throws GeneralException
	 */
	public HashMap updateContentValue(int p0100, String field, String value) throws GeneralException {
		HashMap map = new HashMap();
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100", p0100);
			vo = dao.findByPrimaryKey(vo);
			vo.setString(field, value);
			int result = dao.updateValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return map;
	}

	/**
	 * 删除记录
	 * @param p1900：周总结号
	 * @return
	 * @throws GeneralException
	 */
	public HashMap deleteRecord(int p1900) throws GeneralException {
		HashMap map = new HashMap();

		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo vo = new RecordVo("p19");
			vo.setInt("p1900", p1900);
			vo = dao.findByPrimaryKey(vo);
			int result = dao.deleteValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return map;
	}
	/**
	 * 查询岗位职责任务
	 * @param nbase：库前缀
	 * @param a0100：员工号
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getE01a1PlanTask(String nbase, String a0100) throws GeneralException {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String e01a1 = this.getE01a1(nbase, a0100);
			
			WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.conn);
			Map configMap = workPlanConfigBo.getXmlData();
			String taskSet = (String)configMap.get("taskSet");//岗位子集
			String taskItem = (String)configMap.get("taskItem");//子集下指标
			if(StringUtils.isEmpty(taskSet) && StringUtils.isEmpty(taskItem)){
				return list;
			}
			StringBuilder sql = new StringBuilder();
			sql.append("select i9999,");
			sql.append(taskItem);
			sql.append(" from ");
			sql.append(taskSet);
			sql.append(" where e01a1 ='");
			sql.append(e01a1);
			sql.append("'");
			sql.append(" order by i9999");
			
			rs = dao.search(sql.toString());
			while(rs.next()){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("itemid", rs.getString("i9999"));
				map.put("itemdesc", rs.getString(taskItem));
				list.add(map);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 获取岗位
	 * @param nbase：库前缀
	 * @param a0100：员工号
	 * @return
	 * @throws GeneralException
	 */
	public String getE01a1(String nbase, String a0100) throws GeneralException {
		String e01a1 = "";
		try {
			WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userview);
			RecordVo vo = util.getPersonVo(nbase, a0100);
			e01a1 = vo.getString("e01a1");// 岗位
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return e01a1;
	}
	
	/**
	 * 是否显示耗时相关列
	 * @param nbase：库前缀
	 * @param a0100：员工号
	 * @return
	 * @throws GeneralException
	 */
	public boolean getIsOpentasktime(String nbase, String a0100) throws GeneralException {
		//是否显示耗时相关列，默认为false 不显示
		boolean opentasktime = false;
		WorkLogBo worklogBo = new WorkLogBo(this.conn, this.userview, a0100, nbase);
		String taskTime = worklogBo.getTaskTimeSign();
		if("1".equals(taskTime)){
			opentasktime = true;
		}else if("2".equals(taskTime)){
			opentasktime = false;
		}
		return opentasktime;
	}
	
	/**
	 * 获取导出的列头
	 * @param flag  周报=1，日志=2，下期计划=3，其他=4（单个P01指标）
	 * @param isTaskTime 是否显示耗时列
	 * @param itemId	指标id
	 * @return 
	 */
	public ArrayList getHeadList(String flag, boolean isTaskTime, String itemId) {
		//需要显示的字段
		String str = "";
		ArrayList list = new ArrayList();
		if("1".equals(flag)){
			str = ",p1901,p1903,p1905,p1907,";
			list = DataDictionary.getFieldList("P19", Constant.USED_FIELD_SET);
		}else if("2".equals(flag)){
			str = ",content,finish_desc,start_time,end_time,work_time,other_desc,work_type,";
			list = DataDictionary.getFieldList("PER_DIARY_CONTENT", Constant.USED_FIELD_SET);
		}else if("3".equals(flag)){
			str = ",p1901,p1903,";
			list = DataDictionary.getFieldList("P19", Constant.USED_FIELD_SET);
		}else if("4".equals(flag)){
			FieldItem item = DataDictionary.getFieldItem(itemId, "P01");
			list.add(item);
		}
		
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		LazyDynaBean headBean = new LazyDynaBean();
		HashMap headStyleMap = new HashMap();//表头样式设置
		int colNum = 0;
		
		for(int i=0;i<list.size();i++){
			headBean = new LazyDynaBean();
			FieldItem item = (FieldItem)list.get(i);
			String itemid = item.getItemid();//字段id
			
			if(!"4".equals(flag) && (StringUtils.isEmpty(itemid)
					|| !(str.indexOf(","+itemid+",")>-1 ))){// 不排除字段
				continue ;
			}
			
			String itemtype = item.getItemtype();//字段类型
			if(!isTaskTime && ("start_time".equalsIgnoreCase(itemid) 
								|| "end_time".equalsIgnoreCase(itemid)
								|| "work_time".equalsIgnoreCase(itemid))){//不启用耗时，则不导出耗时列，所见即所得 haosl 2018-3-20
				continue;
			}
			if("start_time".equalsIgnoreCase(itemid) || "end_time".equalsIgnoreCase(itemid)){
				itemtype="A";
			}
			String codesetid = item.getCodesetid();//关联的代码			
			String columndesc = item.getItemdesc();//字段描述
			//31338  周总结，p1907对照总结单独处理
			if("p1907".equalsIgnoreCase(itemid)){
				columndesc = "对照总结";
			}
			int itemlength = item.getItemlength();//字段长度
			String fieldsetid = item.getFieldsetid();
			
			headStyleMap = new HashMap();
			headStyleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
			headStyleMap.put("columnWidth",itemlength*40<5500?5500:itemlength*40);//表头宽度设置 
			
			headBean.set("itemid",itemid);//列标题代码
			headBean.set("colType",itemtype);//该列的类型，D：日期，N：数字，A：字符
			headBean.set("content",columndesc);//表头
//			headBean.set("columnLocked", column.isLocked());
			headBean.set("codesetid", codesetid);//列头代码
//			headBean.set("decwidth",  column.getDecimalWidth()+"");//列小数位数
			headBean.set("fromRowNum", 0);//单元格开始行
			headBean.set("toRowNum", 1);//单元格结束行
			headBean.set("fromColNum", colNum);//单元格开始行列
			headBean.set("toColNum", colNum);//单元格结束行列
	        headBean.set("headStyleMap", headStyleMap);//表头样式
	        headList.add(headBean);
	        colNum++;
		}
		return headList;
	}
	
}
