package com.hjsj.hrms.module.workplan.weeklysummary.transaction;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.module.workplan.weeklysummary.businessobject.WeeklySummaryBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title: ExportSummaryTrans </p>
 * <p>Company: hjsj</p>
 * <p>create time: 上午09:38:24 </p>
 * @author linbz
 * @version 1.0
 */

@SuppressWarnings("serial")
public class ExportSummaryTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			
			WeeklySummaryBo weeklySummaryBo = new WeeklySummaryBo(this.getFrameconn(), this.userView);// 工具类
			
			String cyclenow = (String)this.getFormHM().get("cyclenow");// 当前的类型类型，1周报、2月报、3季报、5半年报、4年报
			String zhouzj = (String)this.getFormHM().get("zhouzj");//是否启用周总结
			
			String userName = (String)this.getFormHM().get("userName");//
			String summarydesc = (String)this.getFormHM().get("summarydesc");//

			String thisWorkPlan = (String)this.getFormHM().get("thisWorkPlan");//本期工作总计划
			String thisWorkSummary = (String)this.getFormHM().get("thisWorkSummary");//大文本周总结
			String nextWorkSummary = (String)this.getFormHM().get("nextWorkSummary");//大文本下周计划
			
			String _p0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("p0100"));

			if(StringUtils.isEmpty(_p0100)){
				throw new Exception(ResourceFactory.getProperty("info.workplan.error.noexport"));
			}
			int p0100 =  Integer.parseInt(_p0100);// 总结号
			String nbase = WorkPlanUtil.decryption((String)this.getFormHM().get("nbase"));
			String a0100 = WorkPlanUtil.decryption((String)this.getFormHM().get("a0100"));
			
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());
			if(userName.indexOf("我") != -1){
				userName = userView.getUserFullName();
			}
			String fileName = userView.getUserName()+"_"+summarydesc+"工作总结("+userName+").xls";//31446根据计划的导出规则生成Excel名称
			
			StringBuffer sql = new StringBuffer();
			//31332  取消行高让其自适应
//			excelUtil.setRowHeight((short)1900);
			if("true".equalsIgnoreCase(zhouzj) && "1".equalsIgnoreCase(cyclenow)){
				boolean isopentasktime = weeklySummaryBo.getIsOpentasktime(nbase, a0100);
				//周总结
				ArrayList zhouHeadList = weeklySummaryBo.getHeadList("1", isopentasktime, "");
				sql.append("select p1900,p0100,p1901,p1903,p1905,p1907,p1919 from p19 where p0100=").append(p0100).append(" and p1917='2'");
				ArrayList dataList = excelUtil.getExportData(zhouHeadList, sql.toString());//得到数据列getDataList(data);
				excelUtil.exportExcel("本期工作总结",null, zhouHeadList, dataList, null, 1);
				
				//日志
				ArrayList logHeadList = weeklySummaryBo.getHeadList("2", isopentasktime, "");;
				ArrayList<Integer> p0100_diary_List = weeklySummaryBo.getDiaryP0100ByWeekly(p0100);
				StringBuilder sqlIn = new StringBuilder();
				for (int i = 0; i < p0100_diary_List.size(); i++) {
					sqlIn.append("'").append(p0100_diary_List.get(i)).append("',");
				}
				if(sqlIn.length() > 0)
					sqlIn.deleteCharAt(sqlIn.length()-1);
				else
					sqlIn.append("-1");
				
				sql.setLength(0);
				sql.append("select content, finish_desc, ");
				sql.append(Sql_switcher.dateToChar("start_time", "yyyy-MM-dd HH:mm")).append(" start_time,");
				sql.append(Sql_switcher.dateToChar("end_time", "yyyy-MM-dd HH:mm")).append(" end_time,");
				sql.append(" work_time, other_desc, work_type from per_diary_content where P0100  in (").append(sqlIn).append(")");
				ArrayList dataLogList = excelUtil.getExportData(logHeadList, sql.toString());
				excelUtil.exportExcel("本期工作日志",null, logHeadList, dataLogList, null, 1);
				
				//下期计划
				ArrayList nextPlanHeadList = weeklySummaryBo.getHeadList("3", isopentasktime, "");
				sql.setLength(0);
				sql.append("select p1901,p1903 from p19 where p0100=").append(p0100).append(" and p1917='1'");
				ArrayList nextPlanList = excelUtil.getExportData(nextPlanHeadList, sql.toString());
				excelUtil.exportExcel("下期工作计划",null, nextPlanHeadList, nextPlanList, null, 1);
				
				//培训等大文本
				String zhouzjpx = (String)this.getFormHM().get("zhouzjpx");
				String[] zhoulist = StringUtils.split(zhouzjpx, ",");
				for(int i=0;i<zhoulist.length;i++){
					//p0121:培训需求,p0122:1231313
					String itemStr = zhoulist[i];
					String itemId = StringUtils.split(itemStr, ":")[0];
					String nameStr = StringUtils.split(itemStr, ":")[1];
					
					ArrayList headList = weeklySummaryBo.getHeadList("4", isopentasktime, itemId);
					sql.setLength(0);
					sql.append("select ").append(itemId).append(" from p01 where p0100=").append(p0100);
					ArrayList nextList = excelUtil.getExportData(headList, sql.toString());
					excelUtil.exportExcel(nameStr, null, headList, nextList, null, 1);
				}
			}else{
				/**
				 * 导出大文本时，不需查询，前台传回数据即可
				 */
				HashMap cellStyle = new HashMap();
				cellStyle.put("columnWidth", 9999);
				cellStyle.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
				
				//本期工作计划
				ArrayList headList = new ArrayList();
				LazyDynaBean headBean = new LazyDynaBean();
				headBean.set("itemid","p0109");//列标题代码
				headBean.set("colType","A");//该列的类型，D：日期，N：数字，A：字符
				headBean.set("content","本期工作计划");//表头
				headBean.set("codesetid", "");//列头代码
				headBean.set("fromRowNum", 0);//单元格开始行
				headBean.set("toRowNum", 1);//单元格结束行
				headBean.set("fromColNum", 0);//单元格开始行列
				headBean.set("toColNum", 0);//单元格结束行列
		        headBean.set("headStyleMap", cellStyle);//表头样式
		        headList.add(headBean);
				
		        ArrayList nextList = new ArrayList();
		        LazyDynaBean dataBean = new LazyDynaBean();
		        LazyDynaBean rowDataBean = new LazyDynaBean();
		        dataBean.set("content", thisWorkPlan);
		        dataBean.set("itemid","p0109");
				rowDataBean.set("p0109", dataBean);
				nextList.add(rowDataBean);
				
				excelUtil.exportExcel("本期工作计划", null, headList, nextList, null, 1);
				
				//本期工作总结
				headList = new ArrayList();
				headBean = new LazyDynaBean();
				headBean.set("itemid","p0109");//列标题代码
				headBean.set("colType","A");//该列的类型，D：日期，N：数字，A：字符
				headBean.set("content","本期工作总结");//表头
				headBean.set("codesetid", "");//列头代码
				headBean.set("fromRowNum", 0);//单元格开始行
				headBean.set("toRowNum", 1);//单元格结束行
				headBean.set("fromColNum", 0);//单元格开始行列
				headBean.set("toColNum", 0);//单元格结束行列
		        headBean.set("headStyleMap", cellStyle);//表头样式
		        headList.add(headBean);
				
		        nextList = new ArrayList();
		        dataBean = new LazyDynaBean();
		        rowDataBean = new LazyDynaBean();
		        dataBean.set("content", thisWorkSummary);
				rowDataBean.set("p0109", dataBean);
				nextList.add(rowDataBean);
				
				excelUtil.exportExcel("本期工作总结", null, headList, nextList, null, 1);
				//下期工作计划
				headList = new ArrayList();
				headBean = new LazyDynaBean();
				headBean.set("itemid","p0109");//列标题代码
				headBean.set("colType","A");//该列的类型，D：日期，N：数字，A：字符
				headBean.set("content","下期工作计划");//表头
				headBean.set("codesetid", "");//列头代码
				headBean.set("fromRowNum", 0);//单元格开始行
				headBean.set("toRowNum", 1);//单元格结束行
				headBean.set("fromColNum", 0);//单元格开始行列
				headBean.set("toColNum", 0);//单元格结束行列
		        headBean.set("headStyleMap", cellStyle);//表头样式
		        headList.add(headBean);
				
		        nextList = new ArrayList();
		        dataBean = new LazyDynaBean();
		        rowDataBean = new LazyDynaBean();
		        dataBean.set("content", nextWorkSummary);
		        dataBean.set("itemid","p0109");
				rowDataBean.set("p0109", dataBean);
				nextList.add(rowDataBean);
				
				excelUtil.exportExcel("下期工作计划", null, headList, nextList, null, 1);
			}
			
			excelUtil.exportExcel(fileName);// 导出表格
			this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));//表格名传进前台
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
