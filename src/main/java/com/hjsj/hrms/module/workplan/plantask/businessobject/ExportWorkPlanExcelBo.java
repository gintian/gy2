package com.hjsj.hrms.module.workplan.plantask.businessobject;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.module.workplan.plantask.transaction.DownloadWorkPlanTaskTrans;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: ExportWorkPlanExcelBo </p>
 * OKR-工作计划-导入导出
 * <p>create time  2017-9-28 上午10:46:31</p>
 * @author linbz
 */
public class ExportWorkPlanExcelBo {
	private Connection conn = null;
	private UserView userView = null;
	private RecordVo p07_vo = null; 
	private int P0723; //查看的计划类型 1 人员 2 部门
    private int P0727;//年
    private int P0729;//月 根据期间类型不同代码月份、季度、上半年
    private int P0731;//周
    private int P0700;//
    private int typeDown;//=0下载计划模板；（其他待扩展）
    private String objectId; // 团队id 人员：库+人员编号  
    private String planType; //查看的计划类型
    private String periodType;//期间类型
    private String periodYear;//年
    private String periodMonth;//月 根据期间类型不同代码月份、季度、上半年
    private String periodWeek;//周
    public ArrayList sortItem = new ArrayList();
    
    public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserView() { 
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public RecordVo getP07_vo() {
		return p07_vo;
	}

	public void setP07_vo(RecordVo p07_vo) {
		this.p07_vo = p07_vo;
	}

	public int getP0723() {
		return P0723;
	}

	public void setP0723(int p0723) {
		P0723 = p0723;
	}

	public int getP0727() {
		return P0727;
	}

	public void setP0727(int p0727) {
		P0727 = p0727;
	}

	public int getP0729() {
		return P0729;
	}

	public void setP0729(int p0729) {
		P0729 = p0729;
	}

	public int getP0731() {
		return P0731;
	}

	public void setP0731(int p0731) {
		P0731 = p0731;
	}

	public int getP0700() {
		return P0700;
	}

	public void setP0700(int p0700) {
		P0700 = p0700;
	}

	public void setTypeDown(int typeDown) {
		this.typeDown = typeDown;
	}

	public int getTypeDown() {
		return typeDown;
	}

	public ExportWorkPlanExcelBo(Connection conn,int P0700, UserView userView) {
		this.conn= conn;
		this.P0700= P0700;
		this.userView = userView;
		if(P0700!=0)
			this.p07_vo=getP07Vo(P0700); 
	}
	
	public RecordVo getP07Vo(int p0700) {
		RecordVo vo = new RecordVo("p07");
		try {
			vo.setInt("p0700",p0700);
			ContentDAO dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);
			this.P0723=vo.getInt("p0723");
			this.P0727=vo.getInt("p0727");
			this.P0729=vo.getInt("p0729");
			this.P0731=vo.getInt("p0731");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 获得列头
	 * @param periodType
	 * 			计划周期（1、年度2、半年3、季度4、月份5、周）
	 * @author haosl
	 * @return
	 */
	public ArrayList getColumnList(String periodType){
		ArrayList columnsList = new ArrayList();
		ColumnsInfo columnsInfo;
		
		//p0800
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("p0800");
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnId("p0800");
		columnsList.add(columnsInfo);
		//p0831 父任务号
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("p0831");
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnId("p0831");
		columnsList.add(columnsInfo);
		//序号
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnDesc("序号");// 列头名称
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnId("seq");
		columnsList.add(columnsInfo);
		//任务名称
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnDesc("任务名称");// 列头名称
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnId("p0801");
		columnsList.add(columnsInfo);
		//开始时间
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnDesc("开始时间");// 列头名称
//		columnsInfo.setCodesetId("0");
		columnsInfo.setColumnId("p0813");// 列头代码
		columnsInfo.setColumnType("D");
		columnsList.add(columnsInfo);
		//结束时间
		columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnDesc("结束时间");// 列头名称
		columnsInfo.setColumnId("p0815");// 列头代码
		columnsInfo.setColumnType("D");
		columnsList.add(columnsInfo);
				
		PlanTaskTreeTableBo planTaskTreeBo = new PlanTaskTreeTableBo(this.conn,P0700,this.userView);
		ArrayList<FieldItem> fieldList = planTaskTreeBo.getHeadList(1, P0723);
		this.sortItem = planTaskTreeBo.sortItem;
		for(FieldItem fi : fieldList){
			String itemid = fi.getItemid();
			if("taskprogresscolor".equalsIgnoreCase(fi.getItemid()))
				continue;//不导出
			//linbz 下载导出模板（负责人、任务成员、时间安排、甘特图）不显示不导出
			if(DownloadWorkPlanTaskTrans.exportNo.contains(","+itemid.toLowerCase()+","))
				continue;
			//必填项已单独添加
			if(",seq,p0801,p0813,p0815,".contains(","+itemid.toLowerCase()+","))
				continue;
			
			columnsInfo = new ColumnsInfo();
			columnsInfo.setColumnDesc(fi.getItemdesc());// 列头名称
			columnsInfo.setCodesetId(fi.getCodesetid());// 列头代码类型
			columnsInfo.setColumnId(itemid);// 列头代码
			if(",rank,p0835,".indexOf(","+itemid+",") != -1 ){
				columnsInfo.setColumnType("A");
			}else{
				columnsInfo.setColumnType(fi.getItemtype());// 列头代码
			}
			columnsInfo.setDecimalWidth(fi.getDecimalwidth());// 小数位
//				columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
//				columnsInfo.setReadOnly(true);// 是否只读
//				columnsInfo.setLocked(false);// 是否锁列
			columnsList.add(columnsInfo);
			
		}
		return columnsList;
	}

	/**
	 * 甘特图列
	 * haosl
	 */
	 public ArrayList<ColumnsInfo> getGanttColumn(String periodType,ArrayList columnsInfos){
		 int start = 0;
		 int end = 0;
		 ColumnsInfo columnsInfo;
		 if("1".equals(periodType)){//年度
			 start = 1;
			 end = 12;
			 ArrayList list = new ArrayList();
			 for(int i=start;i<=end;i++){
				 
				columnsInfo = new ColumnsInfo();
				columnsInfo.setColumnId("a_"+i);//columnId
				columnsInfo.setColumnDesc(i+"");//列头名称
				columnsInfo.setColumnWidth(400);
				list.add(columnsInfo);
				if(i%3==0){
					columnsInfo = new ColumnsInfo();
					columnsInfo.setColumnDesc(i/3+"季度");
					columnsInfo.setColumnId("gantt"+(i/3));//组合列头的父列头
					ArrayList list_copy =(ArrayList)list.clone();
					columnsInfo.setChildColumns(list_copy);
					list.clear();
					columnsInfos.add(columnsInfo);
				}
					
			 }
		 }else if("2".equals(periodType)){//半年
			 if(p07_vo.getInt("p0729")==1){//上半年
				 start = 1;
				 end = 6;
			 }else if(p07_vo.getInt("p0729")==2){
				 start = 7;
				 end = 12;
			 }
			 ArrayList list = new ArrayList();
			 for(int i=start;i<=end;i++){
				 
				columnsInfo = new ColumnsInfo();
				columnsInfo.setColumnId("a_"+i);//columnId
				columnsInfo.setColumnDesc(i+"");
				columnsInfo.setColumnWidth(400);
				list.add(columnsInfo);
				if(i%3==0){
					columnsInfo = new ColumnsInfo();
					columnsInfo.setColumnDesc(i/3+"季度");//组合列头的父列头
					columnsInfo.setColumnId("gantt"+(i/3));//columnId
					ArrayList list_copy =(ArrayList)list.clone();
					columnsInfo.setChildColumns(list_copy);
					columnsInfos.add(columnsInfo);
					list.clear();
				}
					
			 }
		 }else if("3".equals(periodType)){//季度
			 if (p07_vo.getInt("p0729")==1){
	             start=1;
	             end=3;
	         }
	         else if (p07_vo.getInt("p0729")==2){
	        	 start=4;
	             end=6;                 
	         }
	         else if (p07_vo.getInt("p0729")==3){
	        	 start=7;
	             end=9;                 
	         }
	         else if (p07_vo.getInt("p0729")==4){
	        	 start=10;
	             end=12;                 
	         }
			 ArrayList list = new ArrayList();
			 for(int i=start;i<=end;i++){
				 
				columnsInfo = new ColumnsInfo();
				columnsInfo.setColumnDesc(i+"");
				columnsInfo.setColumnId("a_"+i);//columnId
				columnsInfo.setColumnWidth(400);
				list.add(columnsInfo);
				if(i%3==0){
					columnsInfo = new ColumnsInfo();
					columnsInfo.setColumnDesc(i/3+"季度");//组合列头的父列头
					columnsInfo.setColumnId("gantt"+(i/3));
					ArrayList list_copy =(ArrayList)list.clone();
					columnsInfo.setChildColumns(list_copy);
					list.clear();
					columnsInfos.add(columnsInfo);
				}
					
			 }
		 }else if("4".equals(periodType)){//月
			 start = 1;
			 end = 4;
			 for(int i=start;i<=end;i++){
				 columnsInfo = new ColumnsInfo();
				 columnsInfo.setColumnId("a_"+i);//columnId
				 columnsInfo.setColumnWidth(400);
				 columnsInfo.setColumnDesc("第"+i+"周");
				 columnsInfos.add(columnsInfo);
			 }
		 }else if("5".equals(periodType)){//周
			  String[] summaryDates = new WorkPlanUtil(this.conn,null)
	          .getBeginEndDates(WorkPlanConstant.Cycle.WEEK,
	          String.valueOf(this.P0727), 
	          String.valueOf(this.P0729), this.P0731);
			  String firstday = summaryDates[0];
			  Date date=DateUtils.getDate(firstday, "yyyy-MM-dd");
			  Calendar calendar =DateUtils.getCalendar(date);
			  for (int i=1;i<=7;i++){                
			      int month=DateUtils.getMonth(calendar.getTime()); 
			      int day=calendar.get(Calendar.DATE);
			      String colDesc=String.valueOf(month)+"."+String.valueOf(day);
			      columnsInfo = new ColumnsInfo();
			      columnsInfo.setColumnId("a_"+i);//columnId
			      columnsInfo.setColumnWidth(40);
			      columnsInfo.setColumnDesc(colDesc);
			      columnsInfos.add(columnsInfo);
			      calendar.add(Calendar.DATE, 1);
			      
			  }
		 }
		 return columnsInfos;
	 }
	/**
	 * 获得导出Excel列头
	 * @param columList
	 * @param isForImport 是否是导入数据下载模板
	 * @return
	 */
	public ArrayList<LazyDynaBean> getSheetHeadList(ArrayList<ColumnsInfo> columList, boolean isForImport) {
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		int colNum = 0;
		HashMap colStyleMap = new HashMap();//列样式设置
		colStyleMap.put("fontSize", 11);
//		colStyleMap.put("border",HSSFCellStyle.BORDER_NONE);
	    for (int i = 0; i < columList.size(); i++) {//后1列为隐藏列
	    	 ColumnsInfo columnInfo = (ColumnsInfo)columList.get(i);
	    	 ArrayList<ColumnsInfo> childColumnList = columnInfo.getChildColumns();
	    	  if(childColumnList.size()>0){//判断childColumnList是否大于0，大于0为复合列
	    		  for (int j = 0; j < childColumnList.size(); j++) {
	    			  HashMap headStyleMap = new HashMap();//表头样式设置
	                  ColumnsInfo columnInfo_j = (ColumnsInfo)childColumnList.get(j);
	                  LazyDynaBean ldbean = new LazyDynaBean();
	                  headStyleMap.put("columnWidth",columnInfo_j.getColumnWidth()*3);//表头宽度设置 
	                  headStyleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
	                  ldbean.set("content",columnInfo_j.getColumnDesc());//列头名称
	                  ldbean.set("itemid", columnInfo_j.getColumnId());
	                  ldbean.set("fromRowNum", 1);//单元格开始行
	                  ldbean.set("toRowNum", 1);//单元格结束行
	                  ldbean.set("fromColNum", colNum);//单元格开始行列
	                  ldbean.set("toColNum", colNum);//单元格结束行列
	                  ldbean.set("headStyleMap", headStyleMap);//表头样式
	                  headList.add(ldbean);
	                  colNum++;//下一列开始位置
	    		  }
	    	  }else{
		    	  LazyDynaBean ldbean = new LazyDynaBean();
		    	  HashMap headStyleMap = new HashMap();//表头样式设置
		    	  headStyleMap.put("fontSize", 12);
		    	  headStyleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
		    	  headStyleMap.put("columnWidth",columnInfo.getColumnWidth()*60);//默认表头宽度设置 
		    	  String columnId = columnInfo.getColumnId();
		    	  String columnType = columnInfo.getColumnType();
		    	  String columnCodesetId = columnInfo.getCodesetId();
		    	  String headIds = ",a_1,a_2,a_3,a_4,a_5,a_6,a_7,a_8,a_9,a_10,a_11,a_12,";
	    		  if(DownloadWorkPlanTaskTrans.exportHidden.contains(","+columnId.toLowerCase()+",")){  
		    		  ldbean.set("columnHidden", true);
		    	  }
		    	  if(headIds.contains(","+columnId+",")){//单列甘特图列需要重新设置列宽（如周计划下的甘特图）
		    		  headStyleMap.put("columnWidth",1500);//表头宽度设置  
		    	  }
//		    	  headStyleMap.put("border",HSSFCellStyle.BORDER_NONE);//表头宽度设置0  BORDER_THIN  1
		    	  ldbean.set("itemid", columnId);//列头代码
		    	  ldbean.set("content", columnInfo.getColumnDesc());//列头名称
		    	  
		    	  if(isForImport){//导入数据--下载模板文件时，相关设置
                      ldbean.set("comment", columnId);//列头注释
                  }
		    	  if("rank".equalsIgnoreCase(columnId)
		    			 ||"p0835".equalsIgnoreCase(columnId)
		    			 ||"seq".equalsIgnoreCase(columnId)) {//对权重和完成进度数据加% 加上序号
		    		  columnType = "A";//列数据类型
		    		  headStyleMap.put("columnWidth",2800);//表头宽度设置  
		    	  }
		    	  ldbean.set("colType", columnType);//列数据类型
		    	  
		    	  if("D".equalsIgnoreCase(columnType)){
		    		  headStyleMap.put("columnWidth",4000);//表头宽度设置  
		    	  }else if("N".equalsIgnoreCase(columnType)){
		    		  headStyleMap.put("columnWidth",2800);//表头宽度设置  
		    	  }else if("p0801".equalsIgnoreCase(columnId) || "p0803".equalsIgnoreCase(columnId)){
		    		  headStyleMap.put("columnWidth",8000);//表头宽度设置  
		    	  }else if("A".equalsIgnoreCase(columnType) 
		    			  && !"0".equalsIgnoreCase(columnCodesetId) && StringUtils.isNotEmpty(columnCodesetId)){
		    		  headStyleMap.put("columnWidth",4000);//表头宽度设置  
		    	  }
//		    	  if("seq".equalsIgnoreCase(columnId))
//		    		  ldbean.set("columnLocked", true);//单元格不可编辑
		          ldbean.set("codesetid", columnCodesetId.toUpperCase());//列头代码
		          ldbean.set("decwidth",  columnInfo.getDecimalWidth()+"");//列小数位数
		          ldbean.set("fromRowNum", 0);//单元格开始行
		          ldbean.set("toRowNum", 0);//单元格结束行
		          ldbean.set("fromColNum", colNum);//单元格开始行列
		          ldbean.set("toColNum", colNum);//单元格结束行列
		          ldbean.set("headStyleMap", headStyleMap);//表头样式
		          ldbean.set("colStyleMap", colStyleMap);//每列样式
		          headList.add(ldbean);
		          colNum++;
	    	  }
	    }
		return headList;
	}
	/**
	 * 得到工作计划导出Excel复合列集合
	 * @author haosl
	 * @return
	 */
	public ArrayList<LazyDynaBean> getMergedCellList(ArrayList columnsInfo){
		ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
		int colNum = 0;
		for (int i = 0; i < columnsInfo.size(); i++) {
			ColumnsInfo columnInfo = (ColumnsInfo)columnsInfo.get(i);
			ArrayList childColumnList = (ArrayList)columnInfo.getChildColumns();
			if(childColumnList.size() > 0){//子列头大于0，则是复合列头
				LazyDynaBean ldbean = new LazyDynaBean();
				HashMap<String, Object> styleMap = new HashMap<String, Object>();// 样式
			    styleMap.put("fontSize", 10);// 字号
			    styleMap.put("columnWidth",columnInfo.getColumnWidth()*40);// 字号
			    styleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
				ldbean.set("mergedCellStyleMap", styleMap);
				ldbean.set("content", columnInfo.getColumnDesc());// 列头名称
				ldbean.set("fromRowNum", 0);// 合并单元格从那行开始
				ldbean.set("toRowNum", 0);// 合并单元格到哪行结束
				ldbean.set("fromColNum", colNum);// 合并单元格从哪列开始
				ldbean.set("toColNum", colNum + childColumnList.size()-1);// 合并单元格从哪列结束
				mergedCellList.add(ldbean);
				colNum += childColumnList.size();//定位下次初始列
			}else{
				colNum++;
			}
		}
		
		return mergedCellList;
	}
	/**
	 * 导出工作计划Excel
	 * 
	 * @fileName 导出Excel文件名
	 * @sheetName
	 * @p0723
	 * @p0700
	 * @periodType 计划类型
	 */
	public void createExcel(String fileName,String sheetName,int p0723,int p0700,String periodType)throws Exception{
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
		ArrayList<ColumnsInfo> columList = this.getColumnList(periodType);
		//暂不需要复合列
//		ArrayList<LazyDynaBean> mergedCellList = this.getMergedCellList(columList);
		ArrayList<LazyDynaBean> headList = this.getSheetHeadList(columList, true);
		
		//重新组装sql
		StringBuffer sql = new StringBuffer();
		sql.append("select ptm.seq seq, '' participant, '' as timeArrange,'' principal, p08.*,ptm.rank as rank ");//ptm.id as ptm_id,
		//若是下载模板则不需加甘特图d
//		sql.append(",'' a_1,'' a_2,'' a_3,'' a_4,'' a_5,'' a_6,'' a_7,'' a_8,'' a_9,'' a_10,'' a_11,'' a_12 ");//用于甘特图组合列
//		sql.append(",'' gantt1,'' gantt2,'' gantt3,'' gantt4,'' gantt5,'' gantt6,'' gantt7 ");//用于甘特图组合列
		sql.append(",p08.p0813 p0813_e,p08.p0815 p0815_e ");
		sql.append(" from p08,per_task_map ptm,p07 ");
		sql.append(" where" + this.getSqlWhere(""));
		sql.append(" order by ptm.seq");
		
		//根据sql得到数据集
		ArrayList<LazyDynaBean> dataList = excelUtil.getExportData(headList, sql.toString());
		// 穿透任务
		ArrayList otherTaskList = null;
		String notinstr = "";
		
		/*
		 *	此处增加p0800Buf，是为了解决寰球租赁某个任务，页面显示是顶级，
		 *  但在数据库中的数据显示该任务有父任务，但是这个父任务号不存在，然后整个计划数据无法导出的问题
		 *  原理就是，如果父任务在数据库已经不存在，则该任务当成顶级任务处理
		 *  haosl  2017-8-23
		 */
		StringBuffer p0800Buf = new StringBuffer(","); 
		for(int i=0; i<dataList.size(); i++){//穿透任务
			LazyDynaBean rowBean = (LazyDynaBean)dataList.get(i);
			String p0800_ = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");
			if(!p0800Buf.toString().contains(p0800_))
				p0800Buf.append(p0800_+",");
			notinstr += ("'"+p0800_+"',");
		}
		
		if(StringUtils.isNotEmpty(notinstr))
			notinstr = notinstr.substring(0, notinstr.length()-1);
		
		ArrayList<LazyDynaBean> dataList_copy =(ArrayList<LazyDynaBean>)dataList.clone();
		for(int i=0; i<dataList_copy.size(); i++){//穿透任务
			LazyDynaBean rowBean = (LazyDynaBean)dataList_copy.get(i);
			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");
			otherTaskList = this.getOtherTaskList(p0800,headList,notinstr);
			if(otherTaskList.size()>0)
				dataList.addAll(otherTaskList);
			for(int j = 0 ; j < otherTaskList.size();j++) {
				String othP0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");
				if(!p0800Buf.toString().contains(othP0800))
					p0800Buf.append(othP0800+",");
			}
		}
		
		// 重新整理
		HashMap<String, ArrayList<LazyDynaBean>> dataMap = this.sortByGroup(dataList);
		dataList.clear();
		Iterator iter = dataMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			ArrayList<LazyDynaBean> valueList = (ArrayList<LazyDynaBean>)entry.getValue();
			
			for(LazyDynaBean bean : valueList){
				dataList.add(bean);
			}
		}
		
		// 排序
		ArrayList<LazyDynaBean> newDataList = this.sortData(dataList,p0800Buf.toString());
		dataList = newDataList;

		LazyDynaBean dataBean;
		// 序号重排===========
		int num = 0;
		for(int i=0; i<dataList.size(); i++){
			String text = "";
			
			LazyDynaBean rowBean = (LazyDynaBean)dataList.get(i);
			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");	//任务ID
			String P0831 = (String)((LazyDynaBean)rowBean.get("p0831")).get("content");	//父任务ID
			if(p0800.equals(P0831) || ! p0800Buf.toString().contains(","+P0831+",")){//顶级任务
				text = String.valueOf(++num);
			} else {
				LazyDynaBean rowBean_pre = (LazyDynaBean)dataList.get(i-1);
				String p0800_pre = (String)((LazyDynaBean)rowBean_pre.get("p0800")).get("content");	//任务ID
				String P0831_pre = (String)((LazyDynaBean)rowBean_pre.get("p0831")).get("content");	//父任务ID
				String seq_pre = (String)((LazyDynaBean)rowBean_pre.get("seq")).get("content");	//父任务ID
				
				if(p0800_pre.equals(P0831)){//上一个是父任务
					text = seq_pre+".1";
				}
				else if(!p0800_pre.equals(P0831_pre) && P0831_pre.equals(P0831)){//上一个是同级任务
					if(!StringUtils.isEmpty(seq_pre)){
						String[] s = seq_pre.split("\\.");
						int last = Integer.parseInt(s[s.length-1]); 
						s[s.length-1] = String.valueOf(++last);
						for(String t : s){
							text += (t+".");
						}
					} else {
						text = "0.";
					}
					
					text = text.substring(0, text.length()-1);
				} else {//上一个是同级的下级任务
					for(int j=i-1; j>=0; j--){
						LazyDynaBean rowBean_pre_pre = (LazyDynaBean)dataList.get(j);
						String p0800_pre_pre = (String)((LazyDynaBean)rowBean_pre_pre.get("p0800")).get("content");	//任务ID
						String P0831_pre_pre = (String)((LazyDynaBean)rowBean_pre_pre.get("p0831")).get("content");	//父任务ID
						String seq_pre_pre = (String)((LazyDynaBean)rowBean_pre_pre.get("seq")).get("content");	//父任务ID
						if(!p0800_pre_pre.equals(P0831_pre_pre) && P0831_pre_pre.equals(P0831)){
							if(!StringUtils.isEmpty(seq_pre_pre)){
								String[] s = seq_pre_pre.split("\\.");
								int last = Integer.parseInt(s[s.length-1]); 
								s[s.length-1] = String.valueOf(++last);
								for(String t : s){
									text += (t+".");
								}
							} else {
								text = "0.";
							}
							
							text = text.substring(0, text.length()-1);
							break;
						}
					}
				}
			}
			
			dataBean = new LazyDynaBean();
			dataBean.set("content", text);
			rowBean.set("seq", dataBean);
		}
		for(int i=0;i<dataList.size();i++){
			LazyDynaBean rowBean = (LazyDynaBean)dataList.get(i);
			//权重==============================
			//由于栏目设置可能隐藏指标 故增加校验
			if(null != rowBean.get("rank")){
				dataBean = new LazyDynaBean();
				String rankStr = (String)((LazyDynaBean)rowBean.get("rank")).get("content");
				if(!StringUtils.isEmpty(rankStr)){
					if(rankStr.contains("%")){
						dataBean.set("content",rankStr);
					}else{
						double rank = Double.valueOf(rankStr);
						if(rank != 0)
							dataBean.set("content",PubFunc.round(String.valueOf(rank*100), 0)+"%");//(int)(rank*100)
						else//=0时置空
							dataBean.set("content","");
					}
					rowBean.set("rank",dataBean);
				}
			}
			//完成进度
		    dataBean = new LazyDynaBean();
		    String p0835Str = "";
		    if(rowBean.get("p0835")!=null)
		    	p0835Str = String.valueOf(((LazyDynaBean)rowBean.get("p0835")).get("content"));
			if(StringUtils.isNotBlank(p0835Str)){
				int p0835 = Integer.valueOf(p0835Str);
				if(p0835!=0)
					dataBean.set("content",p0835+"%");
				else
					dataBean.set("content","");
				
				rowBean.set("p0835",dataBean);
			}
		}
		//若没有数据，则导出参照模板数据
		if(dataList.size() < 1){
			dataList = getTemplateData(columList);
		}
		//添加空数据，兼容下拉数据模板
		LazyDynaBean nullBean = new LazyDynaBean();
		for(int i=0;i<50;i++){
			dataList.add(getNullDataBean("1", columList, nullBean));
		}
		
		HashMap dropDownMap = new HashMap();
		//取代码型下拉列表
	    for (int i = 0; i < headList.size(); i++) {
	         LazyDynaBean codebean = headList.get(i);
	         String codesetid = (String) codebean.get("codesetid");
	         if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){
	              String itemid = (String) codebean.get("itemid");
	              ArrayList<String> desclist = getCodeByDesc(codesetid);
	              dropDownMap.put(itemid, desclist);
	         }
	    }
	    excelUtil.setConvertToZero(false);//数值型指标为空时默认为空串
	    excelUtil.setHeadRowHeight((short)820);
	    excelUtil.setRowHeight((short)800);
	    // 导出Excel
		excelUtil.exportExcel(sheetName, null, headList, dataList, dropDownMap, 0);
		excelUtil.exportExcel(fileName);
	}
	
	/**
	 * 若没有数据，则导出参照模板数据
	 * @return
	 */
	public ArrayList getTemplateData(ArrayList columList){
		ArrayList datalist = new ArrayList();
		String p0813 = DateUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		String p0815 = DateUtils.format(DateUtils.addDays(new Date(), +1), "yyyy-MM-dd");
				
		LazyDynaBean oneBean = new LazyDynaBean();
		LazyDynaBean dateBean = new LazyDynaBean();
		dateBean.set("content", "1");
		oneBean.set("seq", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "任务名称1");
		oneBean.set("p0801", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", p0813);
		oneBean.set("p0813", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", p0815);
		oneBean.set("p0815", dateBean);
		getNullDataBean("0", columList, oneBean);
		datalist.add(oneBean);
		
		oneBean = new LazyDynaBean();
		dateBean = new LazyDynaBean();
		dateBean.set("content", "1.1");
		oneBean.set("seq", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "任务名称1.1");
		oneBean.set("p0801", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "");
		oneBean.set("p0813", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "");
		oneBean.set("p0815", dateBean);
		getNullDataBean("0", columList, oneBean);
		datalist.add(oneBean);
		
		oneBean = new LazyDynaBean();
		dateBean = new LazyDynaBean();
		dateBean.set("content", "2");
		oneBean.set("seq", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "任务名称2");
		oneBean.set("p0801", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "");
		oneBean.set("p0813", dateBean);
		dateBean = new LazyDynaBean();
		dateBean.set("content", "");
		oneBean.set("p0815", dateBean);
		getNullDataBean("0", columList, oneBean);
		datalist.add(oneBean);
		
		return datalist;
	}
	
	/**
	 * 获取空的数据，处理数值等类型的默认值，都为""
	 * @param flag
	 * @param columList
	 * @param oneBean
	 * @return
	 */
	public LazyDynaBean getNullDataBean(String flag, ArrayList columList, LazyDynaBean oneBean) {
		ArrayList datalist = new ArrayList();
		String str = ",p0831,p0800,seq,p0801,p0813,p0815,";
		ColumnsInfo columnsInfo;
		LazyDynaBean dateBean = new LazyDynaBean();
		dateBean.set("content", "");
		for(int i=0;i<columList.size();i++){
			columnsInfo = (ColumnsInfo) columList.get(i);
			String columnId = columnsInfo.getColumnId();
			if(str.contains(","+columnId+",") && "0".equals(flag))
				continue;
			
			oneBean.set(columnId, dateBean);
		}
		
		return oneBean;
	}
	
	/**
     * 获取代码型 数据  下拉列表数据集合
     * 
     * @param fieldCodeSetId 
     * @return desclist 下拉列表数据集合
     */
    public ArrayList<String> getCodeByDesc(String fieldCodeSetId){
    	String tableName = "";
    	if("UN".equalsIgnoreCase(fieldCodeSetId) 
    			|| "UM".equalsIgnoreCase(fieldCodeSetId)
    			||"@K".equalsIgnoreCase(fieldCodeSetId))
    		tableName = "organization";
    	else
    		tableName = "codeitem";
    	
    	StringBuffer sql = new StringBuffer("");
    	sql.append("select codeitemdesc ");
    	sql.append(" from ").append(tableName);
    	sql.append(" where codesetid='").append(fieldCodeSetId).append("' ");
    	sql.append(" and ").append(Sql_switcher.isnull("invalid", "1")).append("='1'");
    	
        RowSet rs = null;
        ArrayList<String> desclist = new ArrayList<String>();
        try{
            ContentDAO dao = new ContentDAO(conn);
            rs=dao.search(sql.toString());
            while(rs.next()){
                String codeitemdesc=rs.getString("codeitemdesc");
                desclist.add(codeitemdesc);
            }
            return desclist;
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return null;
    }
	
    /**
     * 导出任务排序
     * 1
     * 1.1
     * 2
     * ...
     */
	private ArrayList<LazyDynaBean> sortData(ArrayList<LazyDynaBean> dataList,String p0800Str){
			
		// 排序 
		ArrayList<LazyDynaBean> newDataList = new ArrayList<LazyDynaBean>();
		for(LazyDynaBean rowBean : dataList){
			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");	//任务ID
			String P0831 = (String)((LazyDynaBean)rowBean.get("p0831")).get("content");	//父任务ID
			if(!(p0800.equals(P0831) || !p0800Str.contains(","+P0831+",")))
				continue;
				//顶级任务
			newDataList.add(rowBean);
			for(LazyDynaBean rowBean1 : dataList){
				String p0800_1 = (String)((LazyDynaBean)rowBean1.get("p0800")).get("content");	//任务ID
				String P0831_1 = (String)((LazyDynaBean)rowBean1.get("p0831")).get("content");	//父任务ID
				if(!(!p0800_1.equals(P0831_1) && p0800.equals(P0831_1)))
					continue;
				newDataList.add(rowBean1);
				for(LazyDynaBean rowBean2 : dataList){
					String p0800_2 = (String)((LazyDynaBean)rowBean2.get("p0800")).get("content");	//任务ID
					String P0831_2 = (String)((LazyDynaBean)rowBean2.get("p0831")).get("content");	//父任务ID
					if(!(!p0800_2.equals(P0831_2) && p0800_1.equals(P0831_2)))
						continue;
					newDataList.add(rowBean2);
					for(LazyDynaBean rowBean3 : dataList){
						String p0800_3 = (String)((LazyDynaBean)rowBean3.get("p0800")).get("content");	//任务ID
						String P0831_3 = (String)((LazyDynaBean)rowBean3.get("p0831")).get("content");	//父任务ID
						if(!(!p0800_3.equals(P0831_3) && p0800_2.equals(P0831_3)))
							continue;
						newDataList.add(rowBean3);
						for(LazyDynaBean rowBean4 : dataList){
							String p0800_4 = (String)((LazyDynaBean)rowBean4.get("p0800")).get("content");	//任务ID
							String P0831_4 = (String)((LazyDynaBean)rowBean4.get("p0831")).get("content");	//父任务ID
							if(!(!p0800_4.equals(P0831_4) && p0800_3.equals(P0831_4)))
								continue;
							newDataList.add(rowBean4);
							for(LazyDynaBean rowBean5 : dataList){
								String p0800_5 = (String)((LazyDynaBean)rowBean5.get("p0800")).get("content");	//任务ID
								String P0831_5 = (String)((LazyDynaBean)rowBean5.get("p0831")).get("content");	//父任务ID
								if(!(!p0800_5.equals(P0831_5) && p0800_4.equals(P0831_5)))
									continue;
								newDataList.add(rowBean5);
							}
						}
					}
				}
			}
		}
		
		return newDataList;
	}
	
	private void getTopAndChildTaskList(ArrayList<LazyDynaBean> dataList, ArrayList<LazyDynaBean> topList, ArrayList<LazyDynaBean> childList){
		
		for(LazyDynaBean rowBean : dataList){
			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");	//任务ID
			String P0831 = (String)((LazyDynaBean)rowBean.get("p0831")).get("content");	//父任务ID
			if(p0800.equals(P0831)){//顶级任务
				topList.add(rowBean);
			} else {
				childList.add(rowBean);
			}
		}
	}
	
	public String getTableDatasqlForExcel(String p0800)
	{
		StringBuffer sql = new StringBuffer();
		try {
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}
	
	private String getSqlWhere(String p0800) throws GeneralException{
		 StringBuffer sql_where=new StringBuffer();
		try {
			sql_where.append(" ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
           if (p07_vo==null){
           	sql_where.append(" and  p08.p0800=0");
           }
           else {
               if(p07_vo.getInt("p0723")==2)  {//查看部门任务
               	sql_where.append(" and ptm.org_id='"+this.p07_vo.getString("p0707")+"'"); 
                   //查看部门计划时 ,加上指派给部门的任务    wusy          
               	sql_where.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1 and  p08.p0811 in ('02','03')))" );
                   
               } else {//查看个人任务：我创建、负责、参与的个人计划任务，我负责、参与的部门计划任务      
               	sql_where.append(" and ptm.nbase='"+this.p07_vo.getString("nbase")+"' and ptm.a0100='"+this.p07_vo.getString("a0100")+"' ");    
               	sql_where.append(" and (( p07.p0723=1 and (( ptm.flag<>5 and p08.p0811 in ('02','03')) or ptm.flag=5)");
               	sql_where.append(" ) or ( ");
               	sql_where.append("p07.p0723=2 and ( ptm.flag<>5 and  p08.p0811 in ('02','03'))"); 
               	sql_where.append("))");
               	
               	// 协办任务：不是协办任务直接显示;是协办任务：如本人是发起人，则显示；如是协办人，则协办状态应是已批状态 chent add 20160608 start
               	WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
               	if(util.isOpenCooperationTask()){//启用协作任务
               		sql_where.append(" and (");
               		sql_where.append(" "+Sql_switcher.isnull("p0845", "0")+"<>1 ");//非协办任务
               		sql_where.append(" or (p0845=1 and ptm.flag=1 and p08.p0800 in (select p10.p0800 from p10 where P1019='02')) ");//协办人、已批状态
               		sql_where.append(" or (p0845=1 and ptm.flag=5) ");//发起人
               		sql_where.append(")");
               	}
               	// 协办任务：不是协办任务直接显示;是协办任务：如本人是发起人，则显示；如是协办人，则协办状态应是已批状态 chent add 20160608 end
               }
               /* 同期计划 */
               sql_where.append(" and p07.p0725="+this.p07_vo.getInt("p0725")+" and p07.p0727="+this.p07_vo.getInt("p0727"));  
               if(this.p07_vo.getInt("p0725")==2||this.p07_vo.getInt("p0725")==3||this.p07_vo.getInt("p0725")==4||this.p07_vo.getInt("p0725")==5)
               	sql_where.append(" and p07.p0729="+this.p07_vo.getInt("p0729"));
               if(this.p07_vo.getInt("p0725")==5)
               	sql_where.append(" and p07.p0731="+this.p07_vo.getInt("p0731"));
               if (p0800!=null && p0800.length()>0){//查看某一条任务记录的子任务
               	sql_where.append(" and p08.p0800<>p08.p0831 and  p08.p0831="+p0800);
               }
           }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql_where.toString();
	}
	
	
	/**
	 * 获得穿透任务
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getOtherTaskList(String p0800,ArrayList<LazyDynaBean> headList,String notinstr) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
		ArrayList otherTaskDataList = new ArrayList();
		try {
			if(StringUtils.isNotBlank(p0800)){
			 	StringBuffer sql_str=new StringBuffer();
		        StringBuffer sql = new StringBuffer();
	        	sql.append(" from p08,p09,per_task_map ptm ");
	        	sql.append(" where p09.P0903=p08.p0800 and p09.Nbase=ptm.Nbase and p09.a0100=ptm.a0100 ");
	        	sql.append(" and p09.p0905=1 and ptm.p0800=p08.p0800  and p08.p0811 in ('02','03')");
	        	sql.append(" and p08.p0800<>p08.p0831 and  p08.p0831="+p0800);
	        	sql.append(" and p08.p0800 not in("+notinstr+") ");
	        	
	        	sql_str.append("select 1 as othertask");
	        	sql_str.append(",'' seq, '' participant, '' as timeArrange,'' principal,ptm.rank as rank,p08.*");
	        	sql_str.append(",'' a_1,'' a_2,'' a_3,'' a_4,'' a_5,'' a_6,'' a_7,'' a_8,'' a_9,'' a_10,'' a_11,'' a_12 ");//用于甘特图组合列
	        	sql_str.append(",'' gantt1,'' gantt2,'' gantt3,'' gantt4,'' gantt5,'' gantt6,'' gantt7 ");//用于甘特图组合列
	        	sql_str.append(","+Sql_switcher.isnull("p08.p0813", " ''")+" as p0813_e");
	        	sql_str.append(","+Sql_switcher.isnull("p08.p0815", " ''")+" as p0815_e");
	        	sql_str.append(sql);
	        	sql_str.append(" order by p08.p0813");
	        	otherTaskDataList = excelUtil.getExportData(headList, sql_str.toString());
			}
			return otherTaskDataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获取任务各个角色的人
	 * @param p0800:任务号
	 * @param flag：成员标识：1、负责人 2、参与人 5、发起人
	 * @return
	 * chent
	 */
	public ArrayList<String> getTaskRolePersionList(int p0800, int flag){
		ArrayList<String> list = new ArrayList<String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "";
			ArrayList sqlList = new ArrayList();
			if(flag == 5){// 发起人
				sql = "SELECT org_id,nbase,a0100 FROM per_task_map WHERE p0800=? AND flag=?";
				sqlList.add(p0800);
				sqlList.add(flag);
			} else{//1、负责人 2、参与人
				sql = "SELECT org_id,nbase,a0100 FROM P09 WHERE P0901=2 AND P0903=? AND P0905=?";
				sqlList.add(p0800);
				sqlList.add(flag);
			}
			rs = dao.search(sql, sqlList);
			while (rs.next()) {
				String nbase = "";
				String a0100 = "";
				if(this.isPersonTask(p0800)){// 个人计划直接取nbse，a0100
					nbase = rs.getString("nbase");
					a0100 = rs.getString("a0100");
				} else if(!this.isPersonTask(p0800)){// 团队计划
					if(flag == 1 || flag == 2 ){//负责人、参与人
						nbase = rs.getString("nbase");
						a0100 = rs.getString("a0100");
					} else if(flag == 5){//发起人
						String org_id = rs.getString("org_id");
						if(!StringUtils.isEmpty(org_id)){
							WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
							String leader = util.getFirstDeptLeaders(org_id);
							if(!StringUtils.isEmpty(leader)){
								nbase = leader.substring(0, 3);
								a0100 = leader.substring(3);
							}
						}
					}
				}
				list.add(nbase+a0100);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 通过任务号，判断任务是否为个人任务
	 * @param p0800
	 * @return
	 */
	public boolean isPersonTask(int p0800){
		boolean isPersonTask = true;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select p0723 from p07 where p0700 in (select p0700 from p08 where p0800=?)";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			rs = dao.search(sql, list);
			while(rs.next()){
				int p0723 = rs.getInt("p0723");
				if(p0723 == 2){//团队任务
					isPersonTask = false;
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return isPersonTask;
	}
	/**
	 * 通过nbase、a0100获取用户信息
	 * @param nbase
	 * @param a0100
	 * @return
	 * chent
	 */
	public HashMap<String, String> getEmpInfo(String nbase, String a0100) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String tableName = nbase + "A01";
			String sql = "select e0122,a0101,GUIDKEY from "+tableName+" where A0100=?";
			rs = dao.search(sql, Arrays.asList(new Object[] {a0100}));
			if (rs.next()) {
				String e0122 = rs.getString("e0122");
				String a0101 = rs.getString("a0101");
				String guidKey = rs.getString("GUIDKEY");
				map.put("e0122", e0122);
				map.put("a0101", a0101);
				map.put("guidkey", guidKey);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	 /**
	  * 交换数组位置
	 * @param x
	 * @param a
	 * @param b
	 */
	public void swap(Object[] x, int a, int b) {
	        Object t = x[a];
	        x[a] = x[b];
	        x[b] = t;
	    }
	
	 /**
	  * 自定义排序规则
	 * @param _o1 对象1
	 * @param _o2 对象2
	 * @return 0/-1/1
	 */
	public int compare(Object _o1, Object _o2) {
			
		LazyDynaBean o1 = (LazyDynaBean)_o1;
		LazyDynaBean o2 = (LazyDynaBean)_o2;
			
		if("合计".equals((String)((LazyDynaBean)o1.get("p0801")).get("content"))){
			return 1;
		}
		if("合计".equals((String)((LazyDynaBean)o2.get("p0801")).get("content"))){
			return -1;
		}
		
		int flag = 0;
		for(int i=0; i<this.sortItem.size(); i++){
			if(flag != 0){
				return flag;
			}
			String[] array = this.sortItem.get(i).toString().split(":");
			String itemid = array[0];
			String type = array[1];
			boolean isAsc = Integer.parseInt(array[2])==1?true:false;
			if(o1.get(itemid) == null || o2.get(itemid) == null){
				continue ;
			}
			String v1 = (String)((LazyDynaBean)o1.get(itemid)).get("content");
			String v2 = (String)((LazyDynaBean)o2.get(itemid)).get("content");
			Collator instance = Collator.getInstance(Locale.CHINA);
			if("A".equalsIgnoreCase(type) || "M".equalsIgnoreCase(type) || "D".equalsIgnoreCase(type)){
				//flag = isAsc ? (v1.compareTo(v2)) : (v2.compareTo(v1));
				if("M".equalsIgnoreCase(type)){
					v1 = SafeCode.decode(v1);
					v2 = SafeCode.decode(v2);
				}
				if(StringUtils.isEmpty(v1)) v1="";
				if(StringUtils.isEmpty(v2)) v2="";
				flag =  isAsc ? (instance.compare(v1, v2)) : (instance.compare(v2, v1));
				
			} else {
				if(StringUtils.isEmpty(v1)){
					v1 = "0";
				}
				if(StringUtils.isEmpty(v2)){
					v2 = "0";
				}
				if (Double.parseDouble(v1) > Double.parseDouble(v2)) {
					flag = isAsc ? 1 : -1;
				}else if(Double.parseDouble(v1) < Double.parseDouble(v2)) {
					flag = isAsc ? -1 : 1;
				}else {
					flag = 0;
				}
			}
			
		}
		return flag;
	
	}
	
	/**
	 * 分组重排。同层级任务按照栏目设置的顺序进行排序
	 * @param list
	 * @return
	 */
	public HashMap<String, ArrayList<LazyDynaBean>> sortByGroup(ArrayList<LazyDynaBean> list){
		HashMap<String, ArrayList<LazyDynaBean>> map = new HashMap<String, ArrayList<LazyDynaBean>>();
        for(LazyDynaBean bean : list) {
        	
        	String p0800 = (String)((LazyDynaBean)bean.get("p0800")).get("content");	//任务ID
        	String p0831 = (String)((LazyDynaBean)bean.get("p0831")).get("content");	//父任务ID
			
        	String key = "";
        	if(p0800.equals(p0831)){//顶级任务
        		key = "0";
			} else {
				key = p0831;
			}
			ArrayList<LazyDynaBean> staList = map.get(key);
			if(staList == null){
				staList = new ArrayList<LazyDynaBean>();
			}
			staList.add(bean);
			
			// 基于栏目设置重新排序，因为数据是拼出来的，没办法用sql直接排序，只能自己排了。 chent 20170113 start
			Object[] dest = staList.toArray();
			for (int i=0; i<dest.length; i++){
	            for (int j=i; j>0 && this.compare(dest[j-1], dest[j])>0; j--)
	            	this.swap(dest, j, j-1);
			}
			
			staList = new ArrayList();
			for(int i=0; i<dest.length; i++){
				staList.add((LazyDynaBean)dest[i]);
			}
			// 基于栏目设置重新排序，因为数据是拼出来的，没办法用sql直接排序，只能自己排了。 chent 20170113 end
            
            map.put(key, staList);
        }
        return map;
    }
	
	/**
     * 工作计划--导入数据，浏览上传
     * 
     * @param wb  获取的excel
     * @return importMsgList 存放返回信息记录
     */
	public ArrayList<String> importTemplate(Workbook wb) throws GeneralException{
	    ArrayList<String> importMsgList = new ArrayList<String>();//存放返回信息记录
	    String importMsg = "";
	    Sheet sheet = null;
    	try{
    	    sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);//得到第一行
    //        int cols = row.getPhysicalNumberOfCells();//总列数
    //        int rows = sheet.getPhysicalNumberOfRows();//总行数
            //从第1行取记录的所有列数
            int cellNum = sheet.getRow(0).getPhysicalNumberOfCells();
            //取得所有指标名称
            HashMap indexOfFieldMap = new HashMap();
            //先判断记录是否包含主键p0800,来判断Excel模板是否正确
            String colNameStr = ",";
            for (int j = 0; j < cellNum; j++) {
                String itemid = "";
                Cell cell = row.getCell(j);
                if(cell != null&&cell.getCellComment()==null){
                    Row rowTwo = sheet.getRow(1);//得到第2行,其中有合并行
                    cell = rowTwo.getCell(j);
                }else if(cell == null)
                	continue;
                /**
                 * 因为row.getCell(j)和rowTwo.getCell(j);的返回对象引用了同一个变量cell,
                 * 上面只判断了第一个cell不为空，没考虑被重新赋值后的cell是否为空。bug 号35602 
                 */
                if(cell==null)
                	continue;
                else if(cell.getCellComment()==null){
                    continue;
                }
                String cellComment = cell.getCellComment().getString().toString();// 得到comment
                String[] commentValueArr = cellComment.split("`");
                itemid = commentValueArr[0];
                indexOfFieldMap.put("" + j, itemid);
                colNameStr += itemid+",";
            }
            
            if(!colNameStr.contains(",p0800,")){
            	importMsg = "请使用该页面下载的Excel模板来导入数据！";
                importMsgList.add(importMsg);
                return importMsgList;
            }
            
            /** 开始处理每一个记录begin **/
            /** 序号、p0800、p0831的map集合 **/
            ArrayList p0800slist = new ArrayList();
            /** 需要更新的记录 **/
            ArrayList updatelist = new ArrayList();
            /** 需要新增的集合 **/
            ArrayList insertlist = new ArrayList();
            Pattern pattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])", Pattern.DOTALL);
            //存序号键值对，校验序号是否重复
            HashMap seqallMap = new HashMap();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {// 页中的每一行(除了表头行1行)
                //每次循环一行记录，新建一个集合
                row = sheet.getRow(i);
                
                ArrayList cellslist = new ArrayList();
                
                HashMap p0800sMap = new HashMap();
                p0800sMap.put("seq", "");
                p0800sMap.put("p0800", "");
                p0800sMap.put("p0831", "");
                String seqOne = "";//每条记录序号
                String p0801NameOne = "";//每条记录任务名称
                boolean rowbool = true;
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {// 列循环
                    importMsg = "";
                    Cell colCell = row.getCell(j);
                    if(null == colCell)
                    	continue;
                    //获取的每列 指标名称
                    String colName = (String) indexOfFieldMap.get("" + j);
                    if(StringUtils.isEmpty(colName)){
                    	continue;
                    }
                    //父带任务号
                    if("p0831".equalsIgnoreCase(colName)){
                    	p0800sMap.put("p0831", colCell.toString());
                    	continue;
                    }
                    FieldItem item = DataDictionary.getFieldItem(colName);
                    if(item == null)
                        continue;
                    
                    //数据类型
                    String itemtype = item.getItemtype();
                    String itemdesc = item.getItemdesc();
                    String codesetid = item.getCodesetid();
                    //存放的map
                    HashMap map = new HashMap();
                    map.put("itemid", colName);
                    map.put("itemtype", itemtype);
                    
                    if("p0800".equalsIgnoreCase(colName)){
                    	map.put("value", colCell.toString());
                        cellslist.add(map);
                        p0800sMap.put("p0800", colCell.toString());
                        continue;
                    }
                    if("p0801".equalsIgnoreCase(colName)){
                    	//若在String类型的字段输入纯数值的数据，则要改变单元格格式为普通文本类型Cell.CELL_TYPE_STRING
                    	if(colCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    		colCell.setCellType(Cell.CELL_TYPE_STRING);
                    	}
                    	map.put("value", colCell.getStringCellValue());
                        cellslist.add(map);
                        p0801NameOne = colCell.getStringCellValue();
                        continue;
                    }
                    //序号  权重  完成情况   单独处理 
                    if (",seq,rank,p0835,".indexOf("," + colName + ",") != -1 ) {
                    	String value1 = "";
                    	int celltype = colCell.getCellType();
                        if(celltype == 0){
                        	value1 = colCell.toString();
                        }else{
                        	value1 = colCell.getStringCellValue();
                        }
                       
                        if("seq".equalsIgnoreCase(colName)){
                        	if(value1.indexOf(".0") != -1){//防止出现2.0、3.0等数值
                        		value1 = value1.replaceAll(".0", "");
            				}
            				map.put("itemtype", "A");
            				map.put("value", value1);
                            cellslist.add(map);
                            p0800sMap.put("seq", value1);
                            seqOne = value1;
                            continue;
            			}
            			if(",rank,p0835,".indexOf(","+colName+",") != -1 ){//0.33   22%  22.0
            				if(value1.indexOf("%")!=-1){
            					value1 = value1.replaceAll("%", "");
            					if(StringUtils.isEmpty(value1))
            						value1 = "0";
            				}else if(value1.startsWith("0.")){
            					String val = String.valueOf(Double.parseDouble(value1)*100);
            					value1 = StringUtils.split(val, "\\.")[0];
            				}else if(value1.indexOf(".")!=-1){
            					value1 = StringUtils.split(value1, "\\.")[0];
            				}
            			}
            			
            			Matcher matcher = pattern.matcher(value1);
        				if(!matcher.find() && StringUtils.isNotEmpty(value1)){
        					importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，数据格式错误，请重新下载模板导入！";
        					importMsgList.add(importMsg);
        					rowbool = false;
        					continue;
        				}
        				if(StringUtils.isEmpty(value1)){
        					map.put("value", null);
            			}else{
            				map.put("value", Integer.parseInt(value1));
            			}
                        cellslist.add(map);
                        continue;
                    }
                    //日期校验
                    if(",p0813,p0815,".indexOf(","+colName+",") != -1 ){
                    	boolean bool = true;
                    	if ("N".equals(itemtype)) {
                    		int value = (int) colCell.getNumericCellValue();
                			if (item.getDecimalwidth() == 0) {
                				String val = String.valueOf(value);
                				if(val.matches("^[+-]?[\\d]+$") && val.length()==8){//如：20170201
                					map.put("value", DateUtils.format(DateUtils.getDate(val, "yyyyMMdd"), "yyyy.MM.dd"));
                                    cellslist.add(map);
                				}else{
                					bool = false;
                				}
                			} else{
                				String val = String.valueOf(value);
                				if(val.matches("^[+-]?[\\d]*[.]?[\\d]*[.]?[\\d]+")){//如：2017.02.01;
                					map.put("value", val);
                                    cellslist.add(map);
                				}else{
                					bool = false;
                				}
                			}
                		}else{
            				String value = "";
            				if("D".equals(itemtype)&& 0==colCell.getCellType()){
            					Date date= colCell.getDateCellValue();
            					value = OperateDate.dateToStr(date, "yyyy.MM.dd");
            				}else{
            					value = colCell.toString();
            				}
            				value=value.replaceAll("/", ".");
            				value=value.replace("-", ".");
            				if(!value.matches("^[+-]?[\\d]*[.]?[\\d]*[.]?[\\d]+") && StringUtils.isNotEmpty(value)){
            					importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，日期格式错误，请重新下载模板导入！";
            					importMsgList.add(importMsg);
            					rowbool = false;
            					continue;
            				}
            				if(StringUtils.isNotEmpty(value)){
	            				if(value.length()<10 && value.length()>0){
	            					String[] values = StringUtils.split(value, "\\.");
	            					String month = "";
	            					String day = "";
	        						if(values[1].length()==1)
	        							month = "0"+values[1];
	        						else
	        							month = values[1];
	        						
	        						if(values[2].length()==1)
	        							day = "0"+values[2];
	        						else
	        							day = values[2];
	        						value = values[0] +"."+ month +"."+ day;
		            				map.put("value", value);
		                            cellslist.add(map);
	                            }else if (value.length() == 10){
	                            	map.put("value", value);
		                            cellslist.add(map);
	                            }else
	                            	bool = false;
            				}else{
            					map.put("value", value);
	                            cellslist.add(map);
            				}
            			} 
                    	
                    	if(!bool){
                    		importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，日期格式错误，请重新下载模板导入！";
        					importMsgList.add(importMsg);
        					rowbool = false;
                    	}
                    	continue;
                    }
                    //
                    if ("A".equalsIgnoreCase(itemtype)) {
                        if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){//如果关联代码类
                            String value = colCell.getStringCellValue();
                            String codeitemid = getCodeByDesc(value, codesetid);//取得对应codeitemid
                            map.put("value", codeitemid);
                            cellslist.add(map);
                        }else{
                        	if(colCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        		colCell.setCellType(Cell.CELL_TYPE_STRING);
                        	}
                            String value = colCell.getStringCellValue();
                            map.put("value", value);
                            cellslist.add(map);
                        }
                    } else if ("D".equalsIgnoreCase(itemtype)) {
                        String value = colCell.getStringCellValue();
                    	try{
                    		map.put("value", value);
                            cellslist.add(map);
                    	}catch (Exception e) {
                    		importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，日期格式错误，请重新下载模板导入！";
						}
                    } else if ("N".equalsIgnoreCase(itemtype)) {
                        int decimalwidth = item.getDecimalwidth();
                        int itemlength = item.getItemlength();//整数长度
                        //校验数值为空时
                        if(StringUtils.isEmpty(colCell.toString())) {
                        	map.put("value", null);
                        	continue;
                        }
                        if (decimalwidth == 0) {
                            try {
                                int value = (int) colCell.getNumericCellValue();//直接取得就是整数
                                if(String.valueOf(value).length() > itemlength){
                                    importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，最大长度为"+itemlength+"位，请重新下载模板导入！";
                                }else{
                                	map.put("value", value);
                                    cellslist.add(map);
                                }
                            } catch (Exception e) {
                                importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，应输入数值型，请重新下载模板导入！";
                            }
                        } else {
                            try {
                                double value = colCell.getNumericCellValue();
                                map.put("value", value);
                                cellslist.add(map);
                            } catch (Exception e) {
                                importMsg = "第"+(i)+"条记录的【"+itemdesc+"】列，应输入数值型，请重新下载模板导入！";
                            }
                        }
                    } else {
                    	String value = "";
                    	if(colCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							colCell.setCellType(Cell.CELL_TYPE_STRING);
							value = colCell.getStringCellValue();
                    	}else {
                    		if(colCell != null)
                    			value = colCell.toString();
                    	}
                        map.put("value", value);
                        cellslist.add(map);
                    }
                    
                    if(!"".equalsIgnoreCase(importMsg)){
                        importMsgList.add(importMsg);
                        rowbool = false;
                    }
                    
                }
                //校验p0800与p0700是否对应，否则模板错误
                if(i == 1){
                	String firstP0800 = (String)p0800sMap.get("p0800");
                	if(StringUtils.isNotEmpty(firstP0800)){
                		if(!checkTaskPlan(firstP0800)){
                			importMsg = "导入模板文件错误，请使用该页面下载的最新Excel模板来导入数据！";
                			importMsgList.add(importMsg);
                			return importMsgList;
                		}
                	}
                }
                
                //校验整行数据是否为空
                boolean nullb = true;
                for(int p=0;p<cellslist.size();p++){
                	HashMap nullmap = (HashMap)cellslist.get(p);
                	String isNullstr = nullmap.get("value")==null?"":nullmap.get("value").toString();
                	if(StringUtils.isNotEmpty(isNullstr)){
                		nullb = false;
                		break;
                	}
                }
                if(nullb)
                	break;
                //序号 为数值  不能为空
            	Matcher seqmatcher = pattern.matcher(seqOne);
            	if(!seqmatcher.find() || StringUtils.isEmpty(seqOne)){
					importMsg = "第"+(i)+"条记录的【序号】列，数据格式错误，请重新下载模板导入！";
					importMsgList.add(importMsg);
					rowbool = false;
					continue;
				}
            	//32873 序号重复时增加校验
            	if(seqallMap.get(seqOne) !=null) {
            		String irow = (String)seqallMap.get(seqOne);
            		importMsg = "第"+(i)+"条记录的【序号】列，与第"+irow+"条记录的【序号】重复，请重新下载模板导入！";
					importMsgList.add(importMsg);
					rowbool = false;
					continue;
            	}else {
            		seqallMap.put(seqOne, String.valueOf(i));
				}
                //任务名称  不能为空
                if(StringUtils.isEmpty(p0801NameOne)){
                	importMsg = "第"+(i)+"条记录的【任务名称】列为空，请重新下载模板导入！";
                	importMsgList.add(importMsg);
                	rowbool = false;
                	continue;
                }
                //校验一行记录中若有错误数据则整行不导入
                if(!rowbool)
                	continue;
              
                p0800slist.add(p0800sMap);
                //更新0，新增1
                String flaginfo = "0";
                if(StringUtils.isEmpty((String)p0800sMap.get("p0800")))
                	flaginfo = "1";
                
                if("0".equals(flaginfo))
                	updatelist.add(cellslist);
                else if("1".equals(flaginfo))
                	insertlist.add(cellslist);
                
            }
            
            //处理序号和p0800
            handleSeqP0800(p0800slist);

            //update
            int updateNum = updateTask(updatelist);
            
            //insert  新增
            ArrayList addMsgList = insertTask(insertlist, p0800slist);
            int addNum = (Integer)addMsgList.get(addMsgList.size()-1);
            addMsgList.remove(addMsgList.size()-1);
            
            if(importMsgList.size()<1 && addMsgList.size()<1){

            }else{
            	importMsgList.addAll(addMsgList);
            }
            if(updateNum==0 && addNum==0){
				importMsg = "导入成功！任务未变更。";
			}else{
				importMsg = "导入成功！共";
				if(updateNum>0){
					importMsg+="更新"+updateNum+"条记录";
					if(addNum==0){
						importMsg+="。";
					}else{
						importMsg+=",";
					}
				}
				if(addNum>0){
					importMsg+="新增"+addNum+"条记录。";
				}

			}
			importMsgList.add(importMsg);
    	} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
    	    return importMsgList;
	}

	/**
	 * 校验模板p0800是否与p0700对应，否则模板错误
	 * @param p0800
	 * @return
	 * @throws GeneralException
	 */
	public boolean checkTaskPlan(String p0800) throws GeneralException{
		
		boolean bool = false;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqltest = new StringBuffer("");
    		sqltest.append(" select * from P08 ");
    		sqltest.append(" where P0800=? ");
    		sqltest.append(" and P0700=? ");
    		
    		ArrayList list = new ArrayList();
    		list.add(p0800);
    		list.add(P0700);
    		rs = dao.search(sqltest.toString(), list);
    		if(rs.next())
                bool = true;
			
		}catch (Exception e) {
			e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
		}finally{
        	PubFunc.closeDbObj(rs);
        }
		return bool;
	}
	
	/**
	 * 处理序号和p0800
	 * @param p0800slist
	 * @throws GeneralException
	 */
	public void handleSeqP0800(ArrayList p0800slist) throws GeneralException{

		//若没有p0800,则先生成
		IDGenerator idg = new IDGenerator(2, conn); // id生成器
        for(int i=0;i<p0800slist.size();i++){
    		HashMap map = new HashMap();
    		HashMap p0800sMap = (HashMap)p0800slist.get(i);
    		String p0800 = p0800sMap.get("p0800")==null ?"":(String)p0800sMap.get("p0800");
    		if(StringUtils.isEmpty(p0800)){
    			p0800 = idg.getId("P08.P0800");
    			p0800sMap.put("p0800", p0800);
    		}
        }
        //根据seq序号确认父任务id
        for(int i=0;i<p0800slist.size();i++){
    		HashMap p0800sMap = (HashMap)p0800slist.get(i);
    		String seq = (String)p0800sMap.get("seq");
    		String p0800 = (String)p0800sMap.get("p0800");
    		String p0831 = (String)p0800sMap.get("p0831");
    		
    		if(StringUtils.isEmpty(p0831)){
    			if(seq.indexOf(".0") != -1){
					seq = seq.replaceAll(".0", "");
					p0800sMap.put("seq", seq);
				}
    			String[] seqlist = StringUtils.split(seq, "\\.");
    			int len = seqlist.length;
    			if(len == 1){
    				p0831 = p0800;
    			}else{
    				//seq拼起来前面部分
    				String parentSeq = "";
    				for(int j=0;j<len-1;j++){
    					parentSeq += seqlist[j];
    					//32635 寻找父id拼接之前的序号需加点 .
    					if(j != len-2)
    						parentSeq += ".";
    					
    				}
    				for(int k=0;k<p0800slist.size();k++){
    					HashMap map = (HashMap)p0800slist.get(k);
    	        		String seqone = (String)map.get("seq");
    	        		if(parentSeq.equalsIgnoreCase(seqone)){
    	        			p0831 = (String)map.get("p0800");
    	        			break;
    	        		}
    				}
    			}
    			p0800sMap.put("p0831", p0831);
    		}
        }
	}
	/**
	 * 导入新增任务
	 * @param insertlist
	 * @param p0800slist
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList insertTask(ArrayList insertlist, ArrayList p0800slist) throws GeneralException{
		ArrayList importMsgList = new ArrayList();
		try{
			int num = 0;
			PlanTaskBo ptbo = new PlanTaskBo(this.conn, userView);
            for(int i=0;i<insertlist.size();i++){
            	ArrayList inlist = (ArrayList) insertlist.get(i);
            	
            	HashMap params= new HashMap();
                params.put("objectid", this.objectId);      
                params.put("p0700", String.valueOf(this.P0700));
                params.put("p0723", String.valueOf(this.P0723));
                params.put("othertask", "");
                params.put("director", "");
                
                ArrayList permaplist = new ArrayList();
            	for(int j=0;j<inlist.size();j++){
            		HashMap upmap = (HashMap) inlist.get(j);
            		String itemid = (String)upmap.get("itemid");
            		String itemtype = (String)upmap.get("itemtype");
//            		String value = (String)upmap.get("value");
            		//序号
            		if("seq".equalsIgnoreCase(itemid)){
            			String seqvalue = (String)upmap.get("value");
            			params.put("seq", seqvalue);
            			for(int k=0;k<p0800slist.size();k++){
                    		HashMap p0800sMap = (HashMap)p0800slist.get(k);
                    		String seq = (String)p0800sMap.get("seq");
                    		if(seqvalue.equalsIgnoreCase(seq)){
                    			params.put("p0800", (String)p0800sMap.get("p0831"));//父任务id
                    			params.put("newP0800", (String)p0800sMap.get("p0800"));//新id
                    			break;
                    		}
            			}
            			continue;
            		}
            		//权重
            		if("rank".equalsIgnoreCase(itemid)){
            			params.put("rank", String.valueOf(upmap.get("value")==null?0:(Integer)upmap.get("value")));
            			continue;
            		}
            		//任务名称
            		if("p0801".equalsIgnoreCase(itemid)){
            			params.put("taskName", (String)upmap.get("value"));
            			continue;
            		}
            		//开始结束日期
            		if("p0813".equalsIgnoreCase(itemid) || "p0815".equalsIgnoreCase(itemid)){
            			params.put(itemid, (String)upmap.get("value"));//2017.02.02
            			continue;
            		}
            		//其他数据存入list
            		permaplist.add(upmap);
            		
            	}
            	String msg = addSubtask(ptbo, params, permaplist);
            	if(StringUtils.isEmpty(msg))
            		num++;
            	else
            		importMsgList.add(msg);
            }
            importMsgList.add(num);
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
		return importMsgList;
	}
	
	/**
	 * 更新已有计划任务
	 * @param updatelist
	 * @return
	 * @throws GeneralException
	 */
	public int updateTask(ArrayList updatelist) throws GeneralException{
//		ArrayList importMsgList = new ArrayList();
		int num = 0;
		try{
			//update
	        for(int i=0;i<updatelist.size();i++){
	        	ArrayList uplist = (ArrayList) updatelist.get(i);
	        	
	        	StringBuffer sql = new StringBuffer();
	            ArrayList valuelist = new ArrayList();
	            sql.append("update p08 set ");
	            String p0800 = "";
	            String seqvalue = "";
	            String rankvalue = "";
	            ArrayList permaplist = new ArrayList();
	        	for(int j=0;j<uplist.size();j++){
	        		HashMap upmap = (HashMap) uplist.get(j);
	        		String itemid = (String)upmap.get("itemid");
	        		String itemtype = (String)upmap.get("itemtype");
	//        		String value = (String)upmap.get("value");
	        		if("p0800".equalsIgnoreCase(itemid)){
	        			p0800 = (String)upmap.get("value");
	        			continue;
	        		}
	        		//序号
	        		if("seq".equalsIgnoreCase(itemid)){
	        			String value = (String)upmap.get("value");
	        			String[] seqlist = StringUtils.split(value, "\\.");
	        			if(seqlist.length > 0)
	        				seqvalue = seqlist[seqlist.length-1];
	        			continue;
	        		}
	        		//权重
	        		if("rank".equalsIgnoreCase(itemid)){
	        			rankvalue = String.valueOf(upmap.get("value")==null?0:(Integer)upmap.get("value"));
	        			continue;
	        		}
	        		sql.append(" ");
	                sql.append(itemid + "=?,");
	        		
	                if("A".equalsIgnoreCase(itemtype)
	                		||"M".equals(itemtype)){
	                    valuelist.add((String)upmap.get("value"));
	                }else if("N".equalsIgnoreCase(itemtype)){
	                	
	                	int len = DataDictionary.getFieldItem(itemid).getDecimalwidth();
	                	if(len > 0){
	                		double dvalue = upmap.get("value")==null?0:(Double)upmap.get("value");//Double.parseDouble(value);
	                		valuelist.add(dvalue);
	                	}else {
	                		int intvalue = upmap.get("value")==null?0:(Integer)upmap.get("value");//Integer.parseInt(value);
	                		valuelist.add(intvalue);
	                	}
	                }else if("D".equalsIgnoreCase(itemtype)){
	                	String dateValue = (String)upmap.get("value");
	                	if(StringUtils.isNotEmpty(dateValue))
	                		valuelist.add(DateUtils.getTimestamp(dateValue, "yyyy.MM.dd"));
	                	else
	                		valuelist.add(null);
	                }
	        		
	        	}
	        	if(StringUtils.isEmpty(p0800))
	        		continue;
	        	//更新权重和序号
	        	updateRank(this.P0700, Integer.valueOf(p0800), rankvalue, seqvalue, 1);
	        	sql.setLength(sql.length() -1);
	        	sql.append(" where p0800=?");//根据主键p0800 update
	        	valuelist.add(p0800);
	        	if(StringUtils.isNotEmpty(p0800)){
	            	ContentDAO dao = new ContentDAO(conn);
	            	int resnum = dao.update(sql.toString(), valuelist);
	            	if(1 == resnum)
	            		num++;
	        	}
	        }
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
		return num;
	}
	public String addSubtask(PlanTaskBo ptbo, Map params, ArrayList p08Maplist) throws Exception {
		String importMsg = "";
		
		ContentDAO dao = new ContentDAO(conn);
		IDGenerator idg = new IDGenerator(2, conn); // id生成器
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		WorkPlanBo bo = new WorkPlanBo(conn, userView);
		String p0700 = (String) params.get("p0700"); // 计划id
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		String p0800 = (String) params.get("p0800"); // 任务id
		String newP0800 = params.get("newP0800")!=null ? (String) params.get("newP0800") : "" ; // 若有任务id则不需新建
		String seq = params.get("seq")!=null ? (String) params.get("seq") : "" ; // 序号
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String taskName = params.get("taskName")!= null ? (String) params.get("taskName") : "";  // 子任务名称
		String p0813 = params.get("p0813") != null  ? (String) params.get("p0813") : ""; // 开始时间
		String p0815 = params.get("p0815") != null  ? (String) params.get("p0815") : ""; // 结束时间
		String director = params.get("director")!= null ? (String) params.get("director") : "";  // 负责人id
		String othertask = params.get("othertask")!= null ? (String) params.get("othertask") : "";//父级是否是穿透任务
		
		//区分部门和个人登录情况
		String usrId = "";
		String deptName = "";
		if("1".equals(p0723)){
			usrId = objectid;
		}
		if("2".equals(p0723)){
			usrId = wpUtil.getFirstDeptLeaders(objectid);
			if(StringUtils.isBlank(usrId)){
//				throw new Exception("该部门没有负责人,不可添加任务！");
				importMsg = "任务"+seq+"、"+taskName+"，该部门没有负责人,不可添加任务！";
				return importMsg;
			}
			deptName = wpUtil.getOrgDesc(objectid)+"的";
		}
		
		director = director == null || "".equals(director) ? usrId : director;
		Float rank = params.get("rank") == null || "".equals(params.get("rank")) ? null : Float.valueOf((String) params.get("rank")); // 权重
		String createUserFullName = this.userView.getUserFullName();
		String createUser = this.userView.getUserName();
		boolean isEditSub = false;
		
		//如果usrId和登陆人id不相符,上级在查看下级计划
		if(!usrId.equals(this.userView.getDbname()+this.userView.getA0100())){
			createUser = (String) wpUtil.getUserNamePassword(usrId.substring(0, 3), usrId.substring(3)).get("username");
			createUserFullName = wpUtil.getUsrA0101(usrId.substring(0, 3), usrId.substring(3));
			isEditSub = true;
		}
		
		int superiorEdit = ptbo.isSuperiorEdit(params);
		//superiorEdit: 1:是上级且下属本人创建的任务  2:是上级且上级分派的任务（上级本人计划中创建的）3:是上级且上级分派的任务（上级在其他下属计划中创建的） 4:是上级且上级创建的任务（在下属计划中创建）
		if(superiorEdit >=1 && superiorEdit <=4){
			//上级在下级计划中创建任务,org_id,nbase,a0100要保存成下级的,create_user还是保存当前登录用户
			if("2".equals(p0723)){
				director = wpUtil.getFirstDeptLeaders(objectid);//负责人  lis 21060628
			}else{
				director = director;//负责人  lis 21060628
			}
		}
		
		RecordVo plan = ptbo.getPlan(Integer.parseInt(p0700));
		if (plan == null) {
			return "";
		}
		/**由于导入新增的时候p0800还未存入库中，故这里暂不校验**/
		/*//superiorEdit: 1:是上级且下属本人创建的任务  2:是上级且上级分派的任务（上级本人计划中创建的）3:是上级且上级分派的任务（上级在其他下属计划中创建的） 4:是上级且上级创建的任务（在下属计划中创建）
		//我是上级
		if(((superiorEdit >= 1 && superiorEdit <= 4) && ptbo.isSubCanEdit(params)) || isEditSub){
			//是上级,获得下级在此任务中的权限,如果下级能动,上级就能动
		}else{
			if (!ptbo.isMyTask(params)) {//othertask,是穿透任务时不判断权限
//				throw new Exception("您没有添加子任务的权限！");
				importMsgList.add("您没有添加子任务的权限！");
			}
		}*/
		
        //检查任务是否重名 
		PlanTaskTreeTableBo treeBo = new PlanTaskTreeTableBo(conn, Integer.parseInt(p0700));
        if (treeBo.taskNameIsRepeated(p0800, "",taskName)){            
//            throw new GeneralException("已存在同名任务,不能保存！");
        	importMsg = "任务"+seq+"、"+taskName+"，已存在同名任务,不能保存！";
        	return importMsg;
        }
		
		/** ##################################### p08 ####################################### */
		RecordVo subtask = new RecordVo("p08");
		
		// 验证任务起止时间是否合逻辑
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date dP0813 = null; // 开始
		Date dP0815 = null; // 结束
		if (p0813 != null && p0813.length() > 0) {
		    try{
		        dP0813 = format.parse(p0813);
		    }
		    catch (Exception e){
//		        throw new Exception("开始日期格式不正确！"); 
		    	importMsg = "任务"+seq+"、"+taskName+"，开始日期格式不正确！";
		    	return importMsg;
		    }
			subtask.setDate("p0813", dP0813);
		}
		if (p0815 != null && p0815.length() > 0) {
		    try{
		        dP0815 = format.parse(p0815);
            }
            catch (Exception e){
//                throw new Exception("结束日期格式不正确！");  
            	importMsg = "任务"+seq+"、"+taskName+"，结束日期格式不正确！";
            	return importMsg;
            }
			
			subtask.setDate("p0815", dP0815);
		}
		if (dP0813 != null && dP0815 != null && dP0813.after(dP0815)) { // 开始结束日期都不为空且结束日期早于开始日期，抛异常
//			throw new Exception("开始日期大于结束日期, 保存失败！");
			importMsg = "任务"+seq+"、"+taskName+"，开始日期大于结束日期, 保存失败！";
			return importMsg;
		}
		// 若有任务id则不需新建
		String id_p08 = StringUtils.isEmpty(newP0800)?idg.getId("P08.P0800"):newP0800;
		subtask.setInt("p0800", Integer.parseInt(id_p08));
		subtask.setInt("p0700", Integer.parseInt(p0700));
		subtask.setString("p0801", taskName);
		subtask.setString("p0809", "1");
		
		// 计划处于报批状态，则新增的子任务默认是报批状态
		if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.HandIn) {
			subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVE);
		} else {
			subtask.setString("p0811", WorkPlanConstant.TaskStatus.DRAFT);
		}
//		if(isEditSub){
//			subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
//		}
		if((superiorEdit >=1 && superiorEdit <= 4) || isEditSub){
			RecordVo p07V0 = ptbo.getPlan(Integer.parseInt(p0700));
			if(p07V0.getInt("p0719") == 2){
				subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			}
		}else{
			// 起草中的计划，新增子任务的变更状态是未变更
			if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Draft) {
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			} else {
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.add);
			}
		}
		
		
		
		// 如果当前用户是公司最高领导(没有上级)，则新增的任务默认为已报批,未变更
		if (ptbo.isTopLeader(objectid, p0723)) {
			// 计划处于已批准的状态
			if (plan.getInt("p0719") == WorkPlanConstant.PlanApproveStatus.Pass) {
				subtask.setString("p0811", WorkPlanConstant.TaskStatus.APPROVED);
				subtask.setInt("p0833", WorkPlanConstant.TaskChangedStatus.Normal);
			}
		}
		
		String p0831 = "".equals(p0800) ? id_p08 : p0800; // 父任务号
		subtask.setInt("p0831", Integer.parseInt(p0831));
		subtask.setInt("p0823", 1);
		subtask.setInt("p0835", 0);
		subtask.setDate("create_time", new Date());
		subtask.setString("create_fullname", userView.getUserFullName());
		subtask.setString("create_user", userView.getUserName());
		subtask.setInt("p0845", 0);//非协作任务

		for(int i=0;i<p08Maplist.size();i++){
			HashMap upmap = (HashMap) p08Maplist.get(i);
    		String itemid = (String)upmap.get("itemid");
    		if("p0800".equalsIgnoreCase(itemid))
    			continue;
    		String itemtype = (String)upmap.get("itemtype");
    		
    		if("A".equalsIgnoreCase(itemtype)
            		||"M".equals(itemtype)){
    			subtask.setString(itemid, (String)upmap.get("value"));
            }else if("N".equalsIgnoreCase(itemtype)){
            	
            	int len = DataDictionary.getFieldItem(itemid).getDecimalwidth();
            	if(len > 0){
            		double dvalue = upmap.get("value")==null?0:(Double)upmap.get("value");//Double.parseDouble(value);
            		subtask.setDouble(itemid, dvalue);
            	}else {
            		int intvalue = upmap.get("value")==null?0:(Integer)upmap.get("value");//Integer.parseInt(value);
            		subtask.setInt(itemid, intvalue);
            	}
            }
		}
		
		dao.addValueObject(subtask);
		//**************p08 end********************************************//
		
		//新建任务后,更新操作日志wusy
		String content = "创建了任务";
		new WorkPlanOperationLogBo(conn, userView).addLog(Integer.parseInt(id_p08), content);
		//上级修改下级任务,给下级发送提醒邮件
		if(isEditSub || (superiorEdit >=1 && superiorEdit <=4)){
			ArrayList list= new ArrayList();
			String subNbase = usrId.substring(0, 3);
			String subA0100 = usrId.substring(3);
			String plan_title = wpUtil.getPlanPeriodDesc(plan.getInt("p0725")+"", plan.getInt("p0727")+"", plan.getInt("p0729")+"", plan.getInt("p0731")+"");
			String subject= "    " + this.userView.getUserFullName()+"在您的"+deptName+plan_title+"工作计划新增了任务,请查看";
            String bodyText=bo.getRemindSubEmail_BodyText(wpUtil.getUsrA0101(subNbase, subA0100),deptName,plan_title, taskName);  
            String href = bo.getRemindSubEmail_PlanHref(subNbase,subA0100, objectid, "", p0723, plan.getInt("p0725")+"", plan.getInt("p0727")+"", plan.getInt("p0729")+"", plan.getInt("p0731")+"", true);  
            LazyDynaBean emailBean = bo.getEmailBean(subNbase+subA0100,subject, bodyText, href,"去查看计划");                   
            emailBean.set("bodySubject", "新增任务提醒");
            list.add(emailBean);
            AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userView);         
            emailBo.send(list);
            //发送微信
            wpUtil.sendWeixinMessageFromEmail(list);
		}
		// 清除当前任务一条线上的所有权重
		params.put("p0800", subtask.getString("p0800"));
		if (rank!= null && rank.floatValue() != 0) {
			ptbo.clearBranchRank(Integer.parseInt(p0700), subtask.getInt("p0800"));
			// 导入任务时不需要查询  清除的权重id
//			String clearIDs = ptbo.getClearRankTaskIds(Integer.parseInt(p0700),  subtask.getInt("p0800"));
//			params.put("clearIDs", clearIDs);
		}
		
		/* ################################ per_task_map表: 创建人 ################################ */
		//分配任务到部门的部门id				wusy
		
		RecordVo builder_map = new RecordVo("per_task_map");
		String id_builder_map = idg.getId("per_task_map.id");
		builder_map.setInt("id", Integer.parseInt(id_builder_map));
		builder_map.setInt("p0800", Integer.parseInt(id_p08));
		builder_map.setInt("flag", 5);
		builder_map.setObject("rank", rank == null ? rank : Float.valueOf(rank.floatValue() / 100));
		builder_map.setInt("p0700", Integer.parseInt(p0700));
		builder_map.setDate("create_time", new Date());
		builder_map.setString("create_user", createUser);
		builder_map.setString("create_fullname", createUserFullName);
		if ("2".equals(p0723)) { // 团队计划
			builder_map.setInt("seq", treeBo.getSeq(objectid, Integer.parseInt(p0831),2));
			builder_map.setString("org_id", objectid);
			// 任务分解到部门,增加字段wusy
			builder_map.setInt("belongflag", Integer.parseInt(p0723));
		} else {
			builder_map.setInt("seq", treeBo.getSeq(objectid, Integer.parseInt(p0831),1));
			builder_map.setString("nbase", objectid.substring(0, 3));
			builder_map.setString("a0100", objectid.substring(3));
			
			// 任务分解到部门,增加字段wusy
			builder_map.setInt("belongflag", Integer.parseInt(p0723));
		}
		dao.addValueObject(builder_map);
		//部门计划：需要判断当前部门负责是否有个人计划
		WorkPlanBo planBo= new WorkPlanBo(this.conn,this.userView);
		planBo.initPlan(Integer.parseInt(p0700));
		if("2".equals(p0723)){
		    planBo.addPlan(planBo.getP07_vo(),usrId.substring(0, 3)+usrId.substring(3));
        }
		
		/* ################################ per_task_map表: 负责人 ################################ */
		// 团队的任务(需要在该人员个人计划下再创建一条)或者个人任务且负责人不是本人(指定了别的负责人)，则新建一条负责人记录
		String b0110 = "";
		if ("2".equals(p0723) || ("1".equals(p0723) && !director.equalsIgnoreCase(usrId))) {
			RecordVo director_map = new RecordVo("per_task_map");
//			String id_director_map = idg.getId("per_task_map.id");
//			director_map.setInt("id", Integer.parseInt(id_director_map));
			director_map.setInt("p0800", Integer.parseInt(id_p08));
			director_map.setInt("p0700", Integer.parseInt(p0700));
			director_map.setInt("seq", treeBo.getSeq(director, Integer.parseInt(p0831), 1));
			director_map.setDate("create_time", new Date());
			director_map.setString("create_user", createUser);
			director_map.setString("create_fullname", createUserFullName);
			director_map.setInt("flag", 1);
			director_map.setString("nbase", director.substring(0, 3));
			director_map.setString("a0100", director.substring(3));
			director_map.setDouble("rank", 0.0);
			String nbase = director.substring(0,3);
			String a0100 = director.substring(3);
			if(!director.equalsIgnoreCase(usrId)){
				b0110 = new WorkPlanUtil(conn, userView).getFristMainDept(nbase, a0100);
				director_map.setInt("belongflag", Integer.parseInt(p0723));//部门计划
				if(!"".equals(b0110)){
					director_map.setString("org_id", b0110);
				}
			}
			director_map.setInt("belongflag", Integer.parseInt(p0723));
			String id_director_map = idg.getId("per_task_map.id");
			director_map.setInt("id", Integer.parseInt(id_director_map));
			director_map.setInt("dispatchflag", 0);
			dao.addValueObject(director_map);
			//分派任务到部门
			if(!"".equals(b0110)){
				ptbo.addDeptTask(b0110, p0700, id_p08,p0723);
			}
			planBo.addPlan(planBo.getP07_vo(),director);
		}
		
		/* ################################ P09表: 负责人 ################################ */
		RecordVo director_p09 = new RecordVo("p09");
		String id_p09 = idg.getId("P09.P0900");
		director_p09.setInt("p0900", Integer.parseInt(id_p09));
		//如果是任务分配到部门,p09中需要插入org_id
		if(!"".equals(b0110)){
			director_p09.setString("org_id", b0110);
		}
		director_p09.setInt("p0901", 2);
		director_p09.setInt("p0903", Integer.parseInt(id_p08));
//		if(isEditSub){
//			director = usrId;
//		}
		director_p09.setString("nbase", director.substring(0, 3));
		director_p09.setString("a0100", director.substring(3));
		
		director_p09.setInt("p0905", 1);
	
		RecordVo a01 = new RecordVo(director.substring(0, 3) + "A01");
		a01.setString("a0100", director.substring(3));
		a01 = dao.findByPrimaryKey(a01);
		director_p09.setString("p0907", a01.getString("b0110"));
		director_p09.setString("p0909", a01.getString("e0122"));
		director_p09.setString("p0911", a01.getString("e01a1"));
		director_p09.setString("p0913", a01.getString("a0101"));

		dao.addValueObject(director_p09);
		
		if(isEditSub){//上级查看下级 创建协作任务时，发送协办申请
			WorkPlanBo workPlanBo = new WorkPlanBo(this.conn, this.userView);
			workPlanBo.SuperiorOperation(Integer.parseInt(id_p08));
		}
		
		return importMsg;
	}
	
	/** 更新权重的值
	 * @param p0700 当前用户的计划id
	 * @param p0800 当前修改或新增的任务id
	 * @param rank 修改后权重的值,页面传递过来未经修饰的值
	 * @param seq  修改后序号的值,
	 * @param flag 	标识,1记录日志,0不记录
	 * @return 更新后新旧权重是否相等
	 * @throws GeneralException
	 */
	public boolean updateRank(int p0700, int p0800, String rank, String seq, int flag) throws GeneralException {
		int count = 0; // 更新的行数
		PlanTaskBo bo = new PlanTaskBo(conn, userView);
		Float fValue = Float.parseFloat((rank == null || "".equals(rank) ? 0.0 : rank).toString());
		fValue =  Float.valueOf(fValue.floatValue() / 100);
		fValue = fValue == 0.0 ? null : fValue;
		try {
			if (!bo.isRankModified(p0700, p0800, fValue)) { // 新旧权重值相等
				return false;
			}
			// 32966 导入更新权重时，没有清除当前任务一条线上的父任务权重
			if (StringUtils.isNotEmpty(rank) && !"0".equals(rank)) {
				bo.clearBranchRank(p0700, p0800);
				// 导入任务时不需要查询  清除的权重id
				//bo.getClearRankTaskIds(p0700,  p0800);
			}
			
			RecordVo plan = bo.getPlan(p0700);

			int p0723 = P0723;//plan.getInt("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
			String objectid = objectId;//getObjectId(plan);
			
			String _objId = null;
			StringBuffer subStr = new StringBuffer();
			if (1 == p0723) {
				subStr.append(" nbase ").append(Sql_switcher.concat()).append(" a0100 = ? AND "+Sql_switcher.isnull("dispatchFlag", "0")+" =0");
				_objId = objectid.substring(0, 3) + objectid.substring(3);
			} else if (2 == p0723) {
				subStr.append(" org_id=? AND nbase is null and A0100 is null");
				_objId = objectid;
			}
			
			// 更新
			float oldRank = bo.GetRankValue(p0700, p0800);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE per_task_map SET rank=?, seq=? WHERE p0800=? AND ").append(subStr);
			count = new ContentDAO(conn).update(sql.toString(), Arrays.asList(new Object[] {
				fValue, seq, new Integer(p0800), _objId
			}));
			//记录日志 调整任务权重 wusy
			String logcontent = "";
			if(flag == 1 && count > 0 && fValue != null){
				if(oldRank != 0 ){
					logcontent = "将任务权重从" + String.valueOf(oldRank*100).split("\\.")[0] +"%调整为了" + (fValue==0?0:(String.valueOf(fValue*100).split("\\.")[0]) + "%");
				}else{
					logcontent = "将任务权重从" + 0 +"调整为了" + String.valueOf(fValue*100).split("\\.")[0] + "%";
				}
			}
			if(flag == 1 && count > 0 && fValue == null){
				logcontent = "将任务权重从" + String.valueOf(oldRank*100).split("\\.")[0] +"%调整为了0";
			}
			RecordVo p08Vo = new RecordVo("p08");
	    	p08Vo.setInt("p0800", p0800);
	    	try {
				p08Vo = new ContentDAO(conn).findByPrimaryKey(p08Vo);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
				new WorkPlanOperationLogBo(conn, userView).addLog(p0800, logcontent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return count != 0;
	}
	
	private String getObjectId(RecordVo plan) {
		int p0723 = plan.getInt("p0723");
		
		if (p0723 == 1) {
			return plan.getString("nbase") + plan.getString("a0100");
		} else if (p0723 == 2) {
			return plan.getString("p0707");
		} else {
			return "";
		}
	}
	/**
     * 获取代码型 数据  codeitemid
     * 
     * @param codeDesc 代码型描述 
     * @param fieldCodeSetId 代码型codesetid
     * @return codeitemid 数据  codeitemid
     */
    private String getCodeByDesc(String codeDesc, String fieldCodeSetId){
    	String tableName = "";
    	//组织机构类型的代码应该查organization表 haosl 2017-08-02 add
    	if("UN".equalsIgnoreCase(fieldCodeSetId) 
    			|| "UM".equalsIgnoreCase(fieldCodeSetId)
    			||"@K".equalsIgnoreCase(fieldCodeSetId))
    		tableName = "organization";
    	else
    		tableName = "codeitem";
    	
    	StringBuffer sql = new StringBuffer("");
    	sql.append("select codeitemid from ");
    	sql.append(tableName);
    	sql.append(" where  codeitemdesc='").append(codeDesc).append("' ");
    	sql.append(" and codesetid='").append(fieldCodeSetId).append("'");
        RowSet rs = null;
        String msg = "";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            rs=dao.search(sql.toString());
            if(rs.next()){
                String codeitemid=rs.getString("codeitemid");
                return codeitemid;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return msg;
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return null;
    }
    
    /**
     * 获得一条记录，导入数据库
     * 
     * @param recordList 一条记录的集合
     * @param wb  获取的excel
     * @param indexList  需要更新的指标名称集合
     * @param indexitemtypeList （指标名称，指标类型）的集合
     * @return realRecordNumber 录入库中的条数
     */
    private int resloveData(ArrayList recordList, Workbook wb, ArrayList<String> indexList, ArrayList<HashMap> indexitemtypeList) {
        ContentDAO dao = new ContentDAO(this.conn);
        int realRecordNumber = 0;
        Sheet sheet = null;
        try{
            for (int i = 0; i < recordList.size(); i++) {
                // sheet名字，即指标集的名字
                String sheetName = wb.getSheetName(0);
                // 挨着得到每一个sheet
                sheet = wb.getSheet(sheetName);
                
                HashMap recordMap = (HashMap) recordList.get(i);
                
                ArrayList w05list  = (ArrayList) recordMap.get(sheetName);
                RecordVo p08Vo = (RecordVo) w05list.get(0);
                
                String p0800 = p08Vo.getString("p0800");
                if(StringUtils.isEmpty(p0800)){
                    return 2;
                }
                StringBuffer sql = new StringBuffer();
                ArrayList valuelist = new ArrayList();
                sql.append("update w05 set ");
                
                for(int j = 0; j < indexList.size(); j++){
                    String index = indexList.get(j);
                    if("w0501".equalsIgnoreCase(index.toLowerCase()))
                        continue;
                    String value =  p08Vo.getString(index);
                    sql.append(" ");
                    sql.append(index + "=?,");
                    HashMap indexitemtype = indexitemtypeList.get(0);
                    String itemtype = (String) indexitemtype.get(index);
                    if("A".equalsIgnoreCase(itemtype)
                    		||"M".equals(itemtype)
                    		||"D".equals(itemtype)){
                        valuelist.add(value);
                    }else if("N".equalsIgnoreCase(itemtype)){
                    	//len=0位整形，len>0位小数类型  haosl 2017-07-14
                    	int len = DataDictionary.getFieldItem(index).getDecimalwidth();
                    	if(len > 0){
                    		double dvalue = Double.parseDouble(value);
                    		valuelist.add(dvalue);
                    	}else {
                    		int intvalue = Integer.parseInt(value);
                    		valuelist.add(intvalue);
                    	}
                    }
                }
                sql.setLength(sql.length() -1);
                sql.append(" where w0501=?");//根据主键w0501 update
                valuelist.add(p0800);
                
                realRecordNumber  = dao.update(sql.toString(), valuelist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realRecordNumber;
    }

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodYear(String periodYear) {
		this.periodYear = periodYear;
	}

	public String getPeriodYear() {
		return periodYear;
	}

	public void setPeriodMonth(String periodMonth) {
		this.periodMonth = periodMonth;
	}

	public String getPeriodMonth() {
		return periodMonth;
	}

	public void setPeriodWeek(String periodWeek) {
		this.periodWeek = periodWeek;
	}

	public String getPeriodWeek() {
		return periodWeek;
	}
}
