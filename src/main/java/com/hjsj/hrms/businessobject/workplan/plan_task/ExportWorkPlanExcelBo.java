package com.hjsj.hrms.businessobject.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.Collator;
import java.util.*;

/**
 * 工作计划导出ExcelBo类
 *@author haosl 
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
    
	public ExportWorkPlanExcelBo(Connection conn,int P0700, UserView userView) {
		this.conn= conn;
		this.P0700= P0700;
		this.userView = userView;
		if(P0700!=0) {
            this.p07_vo=getP07Vo(P0700);
        }
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
		PlanTaskTreeTableBo planTaskTreeBo = new PlanTaskTreeTableBo(this.conn,P0700,this.userView);
		ArrayList<FieldItem> fieldList = planTaskTreeBo.getHeadList(1, P0723);
		this.sortItem = planTaskTreeBo.sortItem;
		for(FieldItem fi : fieldList){
			
			if("taskprogresscolor".equalsIgnoreCase(fi.getItemid())) {
                continue;//不导出
            }
			if("gantt".equalsIgnoreCase(fi.getItemid())){
				getGanttColumn(periodType,columnsList);
			}else{
				columnsInfo = new ColumnsInfo();
				columnsInfo.setColumnDesc(fi.getItemdesc());// 列头名称
				columnsInfo.setCodesetId(fi.getCodesetid());// 列头代码
				columnsInfo.setColumnId(fi.getItemid());// 列头代码
				columnsInfo.setColumnType(fi.getItemtype());// 列头代码
				columnsInfo.setDecimalWidth(fi.getDecimalwidth());// 小数位
				columnsInfo.setColumnWidth(Integer.parseInt(fi.getFormula()));// 栏目设置定义宽度
				columnsList.add(columnsInfo);
			}
		}
		//p0800
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("p0800");
		columnsInfo.setColumnId("p0800");
		columnsList.add(columnsInfo);
		
		//p0813_e 开始时间
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("p0813_e");
		columnsInfo.setColumnType("D");
		columnsInfo.setColumnId("p0813_e");
		columnsList.add(columnsInfo);
		//p0815_e 结束时间
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("p0815_e");
		columnsInfo.setColumnType("D");
		columnsInfo.setColumnId("p0815_e");
		columnsList.add(columnsInfo);
		
		columnsInfo = new ColumnsInfo();
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
		columnsInfo.setColumnDesc("");
		columnsInfo.setColumnType("A");
		columnsInfo.setColumnId("p0831");
		columnsList.add(columnsInfo);
		
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
	 * @return
	 */
	public ArrayList<LazyDynaBean> getSheetHeadList(ArrayList<ColumnsInfo> columList) {
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		int colNum = 0;
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
		    	  headStyleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
		    	  headStyleMap.put("columnWidth",columnInfo.getColumnWidth()*40);//表头宽度设置 
		    	  String columnId = columnInfo.getColumnId();
		    	  String headIds = ",a_1,a_2,a_3,a_4,a_5,a_6,a_7,a_8,a_9,a_10,a_11,a_12,";
		    	  if("p0800".equals(columnId)
		    			||"p0813_e".equals(columnId)
		    			||"p0815_e".equals(columnId)
		    			||"p0831".equals(columnId)){
		    		  ldbean.set("columnHidden", true);
		    	  }
		    	  if(headIds.contains(","+columnId+",")){//单列甘特图列需要重新设置列宽（如周计划下的甘特图）
		    		  headStyleMap.put("columnWidth",1500);//表头宽度设置  
		    	  }
		    	  ldbean.set("headStyleMap", headStyleMap);//表头样式
		    	  ldbean.set("itemid", columnId);//列头代码
		    	  ldbean.set("content", columnInfo.getColumnDesc());//列头名称
		    	  
		    	  if("rank".equalsIgnoreCase(columnId)
		    			 ||"p0835".equalsIgnoreCase(columnId))//对权重和完成进度数据加%
                  {
                      ldbean.set("colType", "A");//列数据类型
                  } else {
                      ldbean.set("colType", columnInfo.getColumnType());//列数据类型
                  }
		    	  
		          ldbean.set("codesetid", columnInfo.getCodesetId().toUpperCase());//列头代码
		          ldbean.set("decwidth",  columnInfo.getDecimalWidth()+"");//列小数位数
		          ldbean.set("fromRowNum", 0);//单元格开始行
		          ldbean.set("toRowNum", 1);//单元格结束行
		          ldbean.set("fromColNum", colNum);//单元格开始行列
		          ldbean.set("toColNum", colNum);//单元格结束行列
		          ldbean.set("headStyleMap", headStyleMap);//表头样式
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
	public void createExcel(String fileName,String sheetName,int p0723,int p0700,String periodType,String exportSubTask)throws Exception{
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
		ArrayList<ColumnsInfo> columList = this.getColumnList(periodType);
		ArrayList<LazyDynaBean> mergedCellList = this.getMergedCellList(columList);
		ArrayList<LazyDynaBean> headList = this.getSheetHeadList(columList);
		
		//重新组装sql
		StringBuffer sql = new StringBuffer();
		sql.append("select p0831,ptm.seq seq, '' participant, '' as timeArrange,'' principal, p08.*,ptm.rank as rank ");
		sql.append(",'' a_1,'' a_2,'' a_3,'' a_4,'' a_5,'' a_6,'' a_7,'' a_8,'' a_9,'' a_10,'' a_11,'' a_12 ");//用于甘特图组合列
		sql.append(",'' gantt1,'' gantt2,'' gantt3,'' gantt4,'' gantt5,'' gantt6,'' gantt7 ");//用于甘特图组合列
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
			if(!p0800Buf.toString().contains(p0800_)) {
                p0800Buf.append(p0800_+",");
            }
			notinstr += ("'"+p0800_+"',");
		}
		//32663 若计划任务为空时，截取SQL空串报错
		if(StringUtils.isNotEmpty(notinstr)) {
            notinstr = notinstr.substring(0, notinstr.length()-1);
        }
		//显示下属计划的时候才加入穿透任务  haosl 2017-12-06
		if("1".equals(exportSubTask)) {
			ArrayList<LazyDynaBean> dataList_copy =(ArrayList<LazyDynaBean>)dataList.clone();
			for(int i=0; i<dataList_copy.size(); i++){//穿透任务
				LazyDynaBean rowBean = (LazyDynaBean)dataList_copy.get(i);
				String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");
				otherTaskList = this.getOtherTaskList(p0800,headList,notinstr);
				if(otherTaskList.size()>0) {
                    dataList.addAll(otherTaskList);
                }
				for(int j = 0 ; j < otherTaskList.size();j++) {
					String othP0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");
					if(!p0800Buf.toString().contains(othP0800)) {
                        p0800Buf.append(othP0800+",");
                    }
				}
			}
		}
		
		// 重新整理
        //改为有序map，否则计划顺序无法控制
        LinkedHashMap<String, ArrayList<LazyDynaBean>> dataMap = this.sortByGroup(dataList);
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
						String s[] = seq_pre.split("\\.");
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
								String s[] = seq_pre_pre.split("\\.");
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
		//为自定义列设置值
		for(int i=0;i<dataList.size();i++){
			LazyDynaBean rowBean = (LazyDynaBean)dataList.get(i);

			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");	//任务ID
			//任务成员=======================
			String participantStr = getPrincipal(p0800, 2);
			dataBean = new LazyDynaBean();
			dataBean.set("content", participantStr);
			rowBean.set("participant", dataBean);
				
			//负责人=============================
			dataBean = new LazyDynaBean();
			String principal = getPrincipal(p0800, 1);
			dataBean.set("content", principal);
			rowBean.set("principal", dataBean);
			
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
						if(rank != 0) {
                            dataBean.set("content",(int)(rank*100)+"%");
                        } else//=0时置空
                        {
                            dataBean.set("content","");
                        }
					}
					rowBean.set("rank",dataBean);
				}
			}
			//计划甘特图========start=====================
			int from = 1;
			int end = 0;
			String p0813_e = (null!=rowBean.get("p0813_e"))?(String)((LazyDynaBean)rowBean.get("p0813_e")).get("content"):"";	//任务ID
			String p0815_e = (null!=rowBean.get("p0815_e"))?(String)((LazyDynaBean)rowBean.get("p0815_e")).get("content"):"";	//任务ID
			RecordVo p07_vo = this.getP07Vo(p0700);
			int p0727=p07_vo.getInt("p0727");  //计划的年份
			int p0729=p07_vo.getInt("p0729");  //计划的月份
			if("1".equals(periodType) || "2".equals(periodType) ||"3".equals(periodType)){//年||半年 计算甘特图列的指针
				if(StringUtils.isNotBlank(p0813_e)){
					String[] p0813Arr = p0813_e.split("-");
					from = Integer.valueOf(p0813Arr[1]);
					if(StringUtils.isNotBlank(p0815_e)){
						String[] p0815Arr = p0815_e.split("-");
						end = Integer.valueOf(p0815Arr[1]);
					}else{
						end = 12;
					}
				}else{
					if(StringUtils.isNotBlank(p0815_e)){
						String[] p0815Arr = p0815_e.split("-");
						end = Integer.valueOf(p0815Arr[1]);
					}
				}
				
			}else if("4".equals(periodType)){//月 计算甘特图列的指针
	    		   
			        WorkPlanUtil planUtil=new WorkPlanUtil(this.conn,null);
	    		    if(p0813_e.length()>0)
	    		    {
	    		        Date date1= DateUtils.getDate(p0813_e, "yyyy-MM-dd");
	    		        int[] weeks=planUtil.getWhichWeekInMonth(date1);
	    		        int inYear=weeks[0];
	    		        int inMonth=weeks[1];
	    		        int inWeek=weeks[2];
	    		        if ((inYear*100+inMonth)==(p0727*100+p0729)){
	    		            from =inWeek;   		            
	    		        }
	    		        else if ((inYear*100+inMonth)>(p0727*100+p0729)){
	    		            from=6;
	    		        }
	    		    }
	    		    if(p0815_e.length()>0)
	    		    {
	                    Date date1= DateUtils.getDate(p0815_e, "yyyy-MM-dd");
	                    int[] weeks=planUtil.getWhichWeekInMonth(date1);
	                    int inYear=weeks[0];
	                    int inMonth=weeks[1];
	                    int inWeek=weeks[2];
	                    if ((inYear*100+inMonth)==(p0727*100+p0729)){
	                        end =inWeek;                       
	                    }              
	                    else if ((inYear*100+inMonth)<(p0727*100+p0729)){
	                        end=0;
	                    }
	    		    }  
			}else if("5".equals(periodType)){//周 计算甘特图列的指针
				WorkPlanUtil planUtil=new WorkPlanUtil(this.conn,null);
				String[] summaryDates = planUtil.getBeginEndDates(WorkPlanConstant.Cycle.WEEK,String.valueOf(p0727), String.valueOf(p0729), p07_vo.getInt("p0731"));
	            String firstday = summaryDates[0];
				Date firstDay1=DateUtils.getDate(firstday, "yyyy-MM-dd");
				if(p0813_e.length()>0)
                {
                    String[] temps1=p0813_e.split("-");
                    Date startDate1= DateUtils.getDate(p0813_e, "yyyy-MM-dd");
                    if (startDate1.before(firstDay1)){//在第一天之前
                        from=1; 
                    }
                    else {
                        from=8;
                        Calendar calendar =DateUtils.getCalendar(firstDay1);
                        for (int j=1;j<=7;j++){                
                            int year=DateUtils.getYear(calendar.getTime()); 
                            int month=DateUtils.getMonth(calendar.getTime()); 
                            int day=calendar.get(Calendar.DATE);
                            if ((Integer.parseInt(temps1[0])==year)
                                    &&(Integer.parseInt(temps1[1])==month)
                                    &&(Integer.parseInt(temps1[2])==day)){
                                from=j;    
                                break;
                            }
                            calendar.add(Calendar.DATE, 1);
                        }  
                    }
                }
                if(p0815_e.length()>0)
                {
                    String[] temps1=p0815_e.split("-");
                    Date endDate1= DateUtils.getDate(p0815_e, "yyyy-MM-dd");
                    if (endDate1.before(firstDay1)){//在第一天之前
                        end=0; 
                    }
                    else {
                        end=7;
                        Calendar calendar =DateUtils.getCalendar(firstDay1);
                        for (int j=1;j<=7;j++){                
                            int year=DateUtils.getYear(calendar.getTime()); 
                            int month=DateUtils.getMonth(calendar.getTime()); 
                            int day=calendar.get(Calendar.DATE);
                            if ((Integer.parseInt(temps1[0])==year)
                                    &&(Integer.parseInt(temps1[1])==month)
                                    &&(Integer.parseInt(temps1[2])==day)){
                                end=j;    
                                break;
                            }
                            calendar.add(Calendar.DATE, 1);
                        }  
                    }
                }  
                
			}
			//设置甘特图列的值
		    for(int x=from;x<=end;x++){
				dataBean = new LazyDynaBean();
				//32820  陈总建议甘列图之前 对勾 改成 线条  //√ //▄ //▃  //━
				dataBean.set("content","▄");
				rowBean.set("a_"+x, dataBean);
			}
		  //计划甘特图===========end===============
		  //完成进度
		    dataBean = new LazyDynaBean();
		    String p0835Str = "";
		    if(rowBean.get("p0835")!=null) {
                p0835Str = String.valueOf(((LazyDynaBean)rowBean.get("p0835")).get("content"));
            }
			if(StringUtils.isNotBlank(p0835Str)){
				int p0835 = Integer.valueOf(p0835Str);
				if(p0835!=0) {
                    dataBean.set("content",p0835+"%");
                } else {
                    dataBean.set("content","");
                }
				
				rowBean.set("p0835",dataBean);
			}
		  //时间安排列==========start==============
			if(StringUtils.isNotBlank(p0813_e)){
				String[] p0813Arr = p0813_e.split("-");
				if(StringUtils.isNotBlank(p0815_e)){
					String[] p0815Arr = p0815_e.split("-");
					dataBean = new LazyDynaBean();
					dataBean.set("content",p0813Arr[1]+"月"+p0813Arr[2]+"日"+" 至 "+p0815Arr[1]+"月"+p0815Arr[2]+"日");
					rowBean.set("timearrange", dataBean);
				}else{
					dataBean = new LazyDynaBean();
					dataBean.set("content","自 "+p0813Arr[1]+"月"+p0813Arr[2]+"日 开始");
					rowBean.set("timearrange", dataBean);
				}
			}else{
				if(StringUtils.isNotBlank(p0815_e)){
					String[] p0815Arr = p0815_e.split("-");
					dataBean = new LazyDynaBean();
					dataBean.set("content","截止 "+p0815Arr[1]+"月"+p0815Arr[2]+"日");
					rowBean.set("timearrange", dataBean);
				}
			}
			
			
			String seq = (null!=rowBean.get("seq"))?(String)((LazyDynaBean)rowBean.get("seq")).get("content"):"";	
			String p0801 = (null!=rowBean.get("p0801"))?(String)((LazyDynaBean)rowBean.get("p0801")).get("content"):"";	
			int len = seq.split("\\.").length;
			dataBean = new LazyDynaBean();
			String p0801text = "";
			for(int j=0; j<len; j++){
				p0801text += " ";
			}
			p0801text += p0801;
			dataBean.set("content", p0801text);
			rowBean.set("p0801", dataBean);
		}
		// 导出Excel
//		excelUtil.setRowHeight((short)600);//设置行高 31332 注释掉，自适应即可
		excelUtil.exportExcel(sheetName, mergedCellList, headList, dataList, null, 1);
		excelUtil.exportExcel(fileName);
	}
	private ArrayList<LazyDynaBean> sortData(ArrayList<LazyDynaBean> dataList,String p0800Str){
			
		// 排序 
		ArrayList<LazyDynaBean> newDataList = new ArrayList<LazyDynaBean>();
		for(LazyDynaBean rowBean : dataList){
			String p0800 = (String)((LazyDynaBean)rowBean.get("p0800")).get("content");	//任务ID
			String P0831 = (String)((LazyDynaBean)rowBean.get("p0831")).get("content");	//父任务ID
			if(p0800.equals(P0831) || !p0800Str.contains(","+P0831+",")){//顶级任务
				newDataList.add(rowBean);
				for(LazyDynaBean rowBean1 : dataList){
					String p0800_1 = (String)((LazyDynaBean)rowBean1.get("p0800")).get("content");	//任务ID
					String P0831_1 = (String)((LazyDynaBean)rowBean1.get("p0831")).get("content");	//父任务ID
					if(!p0800_1.equals(P0831_1) && p0800.equals(P0831_1)){
						newDataList.add(rowBean1);
						for(LazyDynaBean rowBean2 : dataList){
							String p0800_2 = (String)((LazyDynaBean)rowBean2.get("p0800")).get("content");	//任务ID
							String P0831_2 = (String)((LazyDynaBean)rowBean2.get("p0831")).get("content");	//父任务ID
							if(!p0800_2.equals(P0831_2) && p0800_1.equals(P0831_2)){
								newDataList.add(rowBean2);
								for(LazyDynaBean rowBean3 : dataList){
									String p0800_3 = (String)((LazyDynaBean)rowBean3.get("p0800")).get("content");	//任务ID
									String P0831_3 = (String)((LazyDynaBean)rowBean3.get("p0831")).get("content");	//父任务ID
									if(!p0800_3.equals(P0831_3) && p0800_2.equals(P0831_3)){
										newDataList.add(rowBean3);
										for(LazyDynaBean rowBean4 : dataList){
											String p0800_4 = (String)((LazyDynaBean)rowBean4.get("p0800")).get("content");	//任务ID
											String P0831_4 = (String)((LazyDynaBean)rowBean4.get("p0831")).get("content");	//父任务ID
											if(!p0800_4.equals(P0831_4) && p0800_3.equals(P0831_4)){
												newDataList.add(rowBean4);
												for(LazyDynaBean rowBean5 : dataList){
													String p0800_5 = (String)((LazyDynaBean)rowBean5.get("p0800")).get("content");	//任务ID
													String P0831_5 = (String)((LazyDynaBean)rowBean5.get("p0831")).get("content");	//父任务ID
													if(!p0800_5.equals(P0831_5) && p0800_4.equals(P0831_5)){
														newDataList.add(rowBean5);
													}
												}
											}
										}
									}
								}
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
               if(this.p07_vo.getInt("p0725")==2||this.p07_vo.getInt("p0725")==3||this.p07_vo.getInt("p0725")==4||this.p07_vo.getInt("p0725")==5) {
                   sql_where.append(" and p07.p0729="+this.p07_vo.getInt("p0729"));
               }
               if(this.p07_vo.getInt("p0725")==5) {
                   sql_where.append(" and p07.p0731="+this.p07_vo.getInt("p0731"));
               }
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
	 * 获得负责人
	 * @param p0800
	 * @param role  =1 负责人  =2 参与人 =3 关注人
	 * @return
	 */
	public String getPrincipal(String p0800,int role)throws GeneralException{
		StringBuffer names = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer p0800_str = new StringBuffer("");
			p0800_str.append(p0800);
			RowSet rowSet = dao
					.search("select * from p09 where P0905="+role+" and p0901=2 and p0903 in ("
							+ p0800_str.toString()
							+ ") order by p0900");
			while (rowSet.next()) {
				names.append(rowSet.getString("P0913")+"、"); // 姓名
			}
			if(names.length()>0) {
                names.setLength(names.length()-1);
            }
			return names.toString();
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
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
	public HashMap<String, String> getInfoByNbsA0100(String nbase, String a0100) {
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
				if(StringUtils.isEmpty(v1)) {
                    v1="";
                }
				if(StringUtils.isEmpty(v2)) {
                    v2="";
                }
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
	public LinkedHashMap<String, ArrayList<LazyDynaBean>> sortByGroup(ArrayList<LazyDynaBean> list){
	    //使用HashMap存储时，没法保证顶级任务的顺序，故此采用LinkedHashMap
        LinkedHashMap<String, ArrayList<LazyDynaBean>> map = new LinkedHashMap<String, ArrayList<LazyDynaBean>>();
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
	            for (int j=i; j>0 && this.compare(dest[j-1], dest[j])>0; j--) {
                    this.swap(dest, j, j-1);
                }
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
}
