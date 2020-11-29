package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.jxcell.CellException;
import com.jxcell.CellFormat;
import com.jxcell.ChartShape;
import com.jxcell.View;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 薪资报表导出excel交易类
 * @author zhanghua
 *
 */
public class ExportDataTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try{
		// TODO Auto-generated method stub
		String gz_module = (String) this.getFormHM().get("gz_module");////薪资和保险区分标识  1：保险  否则是薪资
		gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
		String model = (String) this.getFormHM().get("model");//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
		String rsid = (String) this.getFormHM().get("rsid");//表类号
		String rsdtlid = (String) this.getFormHM().get("rsdtlid");//具体表号
		
		String salaryid = (String) this.getFormHM().get("salaryid");//薪资类别
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String bosdate = (String) this.getFormHM().get("bosdate");//业务如期
		bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
		String count = (String) this.getFormHM().get("count");//发放次数
		count = PubFunc.decrypt(SafeCode.decode(count));
		String groupvalues = (String) this.getFormHM().get("groupvalues");//分组值
		if(!StringUtils.isBlank(groupvalues))
			groupvalues = SafeCode.decode(groupvalues);
		String groupField="",UserFlag = "",subModuleId="";

		SalaryReportBo bo=new SalaryReportBo(this.getFrameconn(), salaryid, userView,rsid,rsdtlid,true);
		
		String baseid = "";//统计项目
		String itemid = "";//统计指标
		Boolean bgroup=false;
		boolean isGroupPage=false;
		if("4".equals(rsid)){
			baseid = (String) this.getFormHM().get("baseid");//统计项目
			itemid = (String) this.getFormHM().get("itemid");//统计指标
			subModuleId="salarycollect";
		
		}else{
			bgroup= "0".equals(bo.getReportdetailvo().getString("bgroup"))?false:true;//是否启用分组
			isGroupPage= "1".equalsIgnoreCase(bo.getIsGroup(bo.getReportdetailvo().getString("ctrlparam")))?true:false;//是否启用分组打印
			subModuleId="salaryreport_"+rsdtlid;
		}
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
			ArrayList<ColumnsInfo> displayColumns=new ArrayList<ColumnsInfo>();
			if(tableCache!=null){
				displayColumns=(ArrayList<ColumnsInfo>)tableCache.getDisplayColumns();
			}

		if(("2".equals(rsid)|| "12".equals(rsid))&&bgroup){
			UserFlag=(String) this.getFormHM().get("UserFlag");
			groupField = (String) this.getFormHM().get("groupField");
		}

		String tableTitle=(String)this.getFormHM().get("tableTitle");
		
		tableTitle=tableTitle.substring(tableTitle.lastIndexOf("-->")+3, tableTitle.length());
		
		ArrayList<ArrayList> dataList=new ArrayList<ArrayList>();
		
		
		
		if("1".equals(rsid) ||(   ("2".equals(rsid)|| "12".equals(rsid))&&!bgroup  ) ){////若为 工资单或者没有设置汇总指标的签名表  则获取页面sql 进行数据拼装
			dataList=bo.getRepotrDataList(tableCache);
		}else{
			ArrayList<String> rowList;
			ArrayList<LazyDynaBean> List=bo.getExportReportDataList(model, bosdate, count, UserFlag, groupvalues, baseid, itemid);
			//按页面顺序拼装数据
			if(List!=null&&List.size()!=0){
				for(LazyDynaBean bean : List){
					rowList=new ArrayList<String>();
					for(Object o:tableCache.getDisplayColumns()){
						if(((ColumnsInfo)o).getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
							continue;
						String fieldName=((ColumnsInfo)o).getColumnId();
						if(bean.get(fieldName)!=null)
							rowList.add(bean.get(fieldName).toString());
						else
							rowList.add("");
					}
					dataList.add(rowList);
				}
			}
		}
		ArrayList<Integer>groupPageNum=null;
		if(("3".equals(rsid)|| "13".equals(rsid))&&bgroup)
			groupPageNum=bo.getGroupPageNum();
		else if(("2".equals(rsid)|| "12".equals(rsid))&&isGroupPage&&bgroup)
			groupPageNum=bo.getGroupPageNum();
		if(groupPageNum!=null && groupPageNum.size() != 0)
			groupPageNum.remove(groupPageNum.size()-1);
		String groupName="";
		if("3".equals(rsid)|| "13".equals(rsid)){
			LazyDynaBean groupBean=null;
			groupBean=bo.getGroupBean();
			groupField=(String)groupBean.get("f_groupItem");
			String sgroup=(String)groupBean.get("s_groupDesc");
			if(!StringUtils.isBlank(sgroup))
				groupName=(String)groupBean.get("f_groupDesc")+"&"+sgroup;
		}

		
		String fileName="";
		//保存参数
		//人员结构工资分析表由于没有表号，这里全部置为0，存储在表中0
		ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.getFrameconn(), this.userView, rsid + "", StringUtils.isNotBlank(rsdtlid)?rsdtlid:"0");
		ReportParseVo reportParseVo = rpob.analyse(0);
		
		if("4".equalsIgnoreCase(rsid)|| "14".equalsIgnoreCase(rsid)){//导出图表

			//String baseid=(String)this.getFormHM().get("baseid");//汇总列id
			Double other=Double.parseDouble((String)this.getFormHM().get("other"));//“其他”区域显示数量
			
			ArrayList<Integer> showRowNum=(ArrayList)this.getFormHM().get("showRowNum");//取页面图表显示行的下标，用以取得excel 图表所需显示的数据坐标
			fileName=this.exportExcelAndGraph(reportParseVo, dataList,baseid,showRowNum,other,tableTitle);
			
		}
		else {//不带图表
			int schemeId = bo.getSchemeId("salaryreport_"+rsdtlid);
			// 从数据库中得到可以显示的薪资项目代码


			ArrayList<FieldItem> fieldItems=new ArrayList<FieldItem>();
			if(tableCache!=null){

				FieldItem field;
				for (ColumnsInfo displayColumn : displayColumns) {
					if(displayColumn.getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
						continue;
					field=new FieldItem();
					String codesetid=displayColumn.getCodesetId();
					if(StringUtils.isBlank(codesetid)||"0".equalsIgnoreCase(codesetid)){
						codesetid=DataDictionary.getFieldItem(displayColumn.getColumnId())!=null?DataDictionary.getFieldItem(displayColumn.getColumnId()).getCodesetid():"";
					}
					field.setCodesetid(codesetid);
					field.setItemtype(displayColumn.getColumnType());
					field.setItemdesc(displayColumn.getColumnDesc());
					field.setItemid(displayColumn.getColumnId());
					field.setAlign(displayColumn.getTextAlign());
					field.setDisplaywidth(displayColumn.getColumnWidth());
					field.setDecimalwidth(displayColumn.getDecimalWidth());
					fieldItems.add(field);
				}
			}

			HashMap itemIdMap = null;
			if(schemeId > 0){
				itemIdMap = bo.getItemsToMap(schemeId,"1",0);
			}
			
			fileName=this.exportExcel(reportParseVo, fieldItems, dataList,tableTitle,groupField,groupName,rsid,groupPageNum,itemIdMap);
		}
		
		
		this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 导出带图表的excel
	 * @param itemText 列头id
	 * @param dataList 数据
	 * @param baseid 汇总列id
	 * @param showRowNum 所需显示行的下标，用以取得excel 图表所需显示的数据坐标
	 * @param other “其他”区域显示数量
	 * @param tableTitle 文件名
	 * @return
	 * @author:zhanghua
	 * @throws GeneralException
	 */
	private String exportExcelAndGraph(ReportParseVo pageSet,ArrayList<ArrayList> dataList,String baseid,
			ArrayList showRowNum,Double other,String tableTitle) throws GeneralException{
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String fileName="";
		View view = new View();
		view.getLock();
		try{
			
			//标题
			String formTitle = pageSet.getTitle_fw();//表格标题
			String titleFontType = pageSet.getTitle_fn();//标题字体
			String titleFontBlod = pageSet.getTitle_fb();//标题粗体
			String titleColor = StringUtils.isBlank(pageSet.getTitle_fc())?"#000000":pageSet.getTitle_fc();//颜色默认黑色
			String titleItalic = pageSet.getTitle_fi();//标题斜体
			String titleUnderLine = pageSet.getTitle_fu();//标题下划线
			String titleDelLine = pageSet.getTitle_fs();//标题删除线
			String titleSize = pageSet.getTitle_fz();//标题字体大小
			
			//页头
			String topLeft = pageSet.getHead_flw();//页头上左内容
			String topCenter = pageSet.getHead_fmw();//上中
			String topRight = pageSet.getHead_frw();//上右
			String topFontType = pageSet.getHead_fn();//字体类型
			String topFontBlod = pageSet.getHead_fb();//粗体
			String topFontColor = StringUtils.isBlank(pageSet.getHead_fc())?"#000000":pageSet.getHead_fc();//颜色默认黑色
			String topItalic = pageSet.getHead_fi();//斜体
			String topUnderLine = pageSet.getHead_fu();//下划线
			String topDelLine = pageSet.getHead_fs();//删除线
			String topSize = pageSet.getHead_fz();//字体大小
			
			//页尾
			String footLeft = pageSet.getTile_flw();//页尾上左内容
			String footCenter = pageSet.getTile_fmw();//上中
			String footRight = pageSet.getTile_frw();//上右
			String footFontType = pageSet.getTile_fn();//字体类型
			String footFontBlod = pageSet.getTile_fb();//粗体
			String footFontColor = StringUtils.isBlank(pageSet.getTile_fc())?"#000000":pageSet.getTile_fc();//颜色默认黑色
			String footItalic = pageSet.getTile_fi();//斜体
			String footUnderLine = pageSet.getTile_fu();//下划线
			String footDelLine = pageSet.getTile_fs();//删除线
			String footSize = pageSet.getTile_fz();//字体大小
			
			//正文内容
			String bodyFontType = pageSet.getBody_fn();//字体类型
			String bodyFontBlod = pageSet.getBody_fb();//粗体
			String bodyFontColor = StringUtils.isBlank(pageSet.getBody_fc())?"#000000":pageSet.getBody_fc();//颜色默认黑色
			String bodyItalic = pageSet.getBody_fi();//斜体
			String bodyUnderLine = pageSet.getBody_fu();//下划线
			String bodySize = pageSet.getBody_fz();//字体大小
			
			//正文表头
			String theadFontType = pageSet.getThead_fn();//字体类型
			String theadFontBlod = pageSet.getThead_fb();//粗体
			String theadFontColor = StringUtils.isBlank(pageSet.getThead_fc())?"#000000":pageSet.getThead_fc();//颜色
			String theadItalic = pageSet.getThead_fi();//下划线
			String theadUnderLine = pageSet.getThead_fu();//斜体
			String theadSize = pageSet.getThead_fz();//字体大小
			
			fileName=this.userView.getUserName()+"_"+tableTitle+".xls";
			
			// 设置颜色
			view.setPaletteEntry(1, new Color(230, 230, 230));// 浅灰色
			view.setPaletteEntry(2, new Color(217, 217, 217));// 深灰色
			view.setPaletteEntry(3, new Color(250, 0, 0));// 浅灰色

			CellFormat tTitle = null;
			if(StringUtils.isNotBlank(formTitle)) {
				tTitle = view.getCellFormat();
				//标题的样式
				tTitle.setFontSize(StringUtils.isNotBlank(titleSize)?Double.parseDouble(titleSize):10);// 设置字体大小
				tTitle.setFontBold(StringUtils.isNotBlank(titleFontBlod)?true:false);//粗体
				tTitle.setFontItalic(StringUtils.isNotBlank(titleItalic)?true:false);//斜线
				tTitle.setFontStrikeout(StringUtils.isNotBlank(titleDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(titleUnderLine))//下划线
					tTitle.setFontUnderline((short)1);
				int[] tColor=new int[3]; 
				tColor[0]=Integer.parseInt(titleColor.substring(1, 3), 16); 
				tColor[1]=Integer.parseInt(titleColor.substring(3, 5), 16); 
				tColor[2]=Integer.parseInt(titleColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(titleColor)) {
					tTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					tTitle.setFontColor(new Color(tColor[0], tColor[1], tColor[2]));//颜色
				}
				tTitle.setFontName(titleFontType);//字体
	            
				tTitle.setMergeCells(true);// 合并单元格
				tTitle.setWordWrap(true);
				// 水平对齐方式
				tTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				// 垂直对齐方式
				tTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			CellFormat hTitle = null;
			if(StringUtils.isNotBlank(topLeft) || StringUtils.isNotBlank(topCenter) || StringUtils.isNotBlank(topRight)) {
				hTitle = view.getCellFormat();
				//页头的样式
				hTitle.setFontSize(StringUtils.isNotBlank(topSize)?Double.parseDouble(topSize):10);// 设置字体大小
				hTitle.setFontBold(StringUtils.isNotBlank(topFontBlod)?true:false);//粗体
				hTitle.setFontItalic(StringUtils.isNotBlank(topItalic)?true:false);//斜线
				hTitle.setFontStrikeout(StringUtils.isNotBlank(topDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(topUnderLine))//下划线
					hTitle.setFontUnderline((short)1);
				int[] hColor=new int[3]; 
				hColor[0]=Integer.parseInt(topFontColor.substring(1, 3), 16); 
				hColor[1]=Integer.parseInt(topFontColor.substring(3, 5), 16); 
				hColor[2]=Integer.parseInt(topFontColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(topFontColor)) {
					hTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					hTitle.setFontColor(new Color(hColor[0], hColor[1], hColor[2]));//颜色
				}
				hTitle.setFontName(topFontType);//字体
	            
				hTitle.setWordWrap(false);//是否换行
				// 水平对齐方式
				hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				// 垂直对齐方式
				hTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			CellFormat fTitle = null;
			if(StringUtils.isNotBlank(footLeft) || StringUtils.isNotBlank(footCenter) || StringUtils.isNotBlank(footRight)) {
				fTitle = view.getCellFormat();
				//页尾的样式
				fTitle.setFontSize(StringUtils.isNotBlank(footSize)?Double.parseDouble(footSize):10);// 设置字体大小
				fTitle.setFontBold(StringUtils.isNotBlank(footFontBlod)?true:false);//粗体
				fTitle.setFontItalic(StringUtils.isNotBlank(footItalic)?true:false);//斜线
				fTitle.setFontStrikeout(StringUtils.isNotBlank(footDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(footUnderLine))//下划线
					fTitle.setFontUnderline((short)1);
				int[] fColor=new int[3]; 
				fColor[0]=Integer.parseInt(footFontColor.substring(1, 3), 16); 
				fColor[1]=Integer.parseInt(footFontColor.substring(3, 5), 16); 
				fColor[2]=Integer.parseInt(footFontColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(footFontColor)) {
					fTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					fTitle.setFontColor(new Color(fColor[0], fColor[1], fColor[2]));//颜色
				}
				fTitle.setFontName(footFontType);//字体
	            
				fTitle.setWordWrap(true);
				// 水平对齐方式
				fTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				// 垂直对齐方式
				fTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			// 表头样式
			// view.setDefaultRowHeight(800);
			CellFormat cfTitle = view.getCellFormat();
			cfTitle.setFontSize(StringUtils.isNotBlank(theadSize)?Double.parseDouble(theadSize):10);// 设置字体大小
			cfTitle.setFontBold(StringUtils.isNotBlank(theadFontBlod)?true:false);//粗体
			cfTitle.setFontItalic(StringUtils.isNotBlank(theadItalic)?true:false);//斜线
			if(StringUtils.isNotBlank(theadUnderLine))//下划线
				cfTitle.setFontUnderline((short)1);
			int[] cfColor=new int[3]; 
			cfColor[0]=Integer.parseInt(theadFontColor.substring(1, 3), 16); 
			cfColor[1]=Integer.parseInt(theadFontColor.substring(3, 5), 16); 
			cfColor[2]=Integer.parseInt(theadFontColor.substring(5, 7), 16);
			if("FF0000".equalsIgnoreCase(theadFontColor)) {
				cfTitle.setFontColor(view.getPaletteEntry(3));//颜色
			}else {
				cfTitle.setFontColor(new Color(cfColor[0], cfColor[1], cfColor[2]));//颜色
			}
            cfTitle.setFontName(theadFontType);//字体
            
			cfTitle.setBottomBorder((short) 1);// 设置边框为细实线
			cfTitle.setTopBorder(CellFormat.PatternSolid);
			cfTitle.setLeftBorder((short) 1);
			cfTitle.setRightBorder((short) 1);
			cfTitle.setWordWrap(true);
			// 水平对齐方式
			cfTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
			// 垂直对齐方式
			cfTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);

			// 内容样式
			CellFormat cfBody = view.getCellFormat();
			cfBody.setFontSize(StringUtils.isNotBlank(bodySize)?Double.parseDouble(bodySize):10);// 设置字体大小
			cfBody.setFontBold(StringUtils.isNotBlank(bodyFontBlod)?true:false);//粗体
			cfBody.setFontItalic(StringUtils.isNotBlank(bodyItalic)?true:false);//斜线
			if(StringUtils.isNotBlank(bodyUnderLine))//下划线
				cfBody.setFontUnderline((short)1);
			int[] bodyColor=new int[3]; 
			bodyColor[0]=Integer.parseInt(bodyFontColor.substring(1, 3), 16); 
			bodyColor[1]=Integer.parseInt(bodyFontColor.substring(3, 5), 16); 
			bodyColor[2]=Integer.parseInt(bodyFontColor.substring(5, 7), 16);
			if("FF0000".equalsIgnoreCase(bodyFontColor)) {
				cfBody.setFontColor(view.getPaletteEntry(3));//颜色
			}else {
				cfBody.setFontColor(new Color(bodyColor[0], bodyColor[1], bodyColor[2]));//颜色
			}
			cfBody.setFontName(bodyFontType);//字体
            
			cfBody.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfBody.setTopBorder(CellFormat.PatternSolid);
			cfBody.setLeftBorder(CellFormat.PatternSolid);
			cfBody.setRightBorder(CellFormat.PatternSolid);
			cfBody.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);// 水平居左
			cfBody.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中
			
			
			CellFormat cfBodyNum = view.getCellFormat();
			cfBodyNum.setFontSize(10);
			cfBodyNum.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfBodyNum.setTopBorder(CellFormat.PatternSolid);
			cfBodyNum.setLeftBorder(CellFormat.PatternSolid);
			cfBodyNum.setRightBorder(CellFormat.PatternSolid);
			cfBodyNum.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);// 水平居右
			cfBodyNum.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中
			

			// 表头样式
			CellFormat cfcolum = view.getCellFormat();
			cfcolum.setFontSize(10);
			cfcolum.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfcolum.setTopBorder(CellFormat.PatternSolid);
			cfcolum.setLeftBorder(CellFormat.PatternSolid);
			cfcolum.setRightBorder(CellFormat.PatternSolid);
			cfcolum.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);// 水平居中
			cfcolum.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中

			view.setDefaultColWidth(10 * 256);// 固定列宽
			view.setColWidth(0, 20 * 256);
			cfTitle.setPattern(CellFormat.PatternSolid);
			cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
			
			FieldItem fielditem=(FieldItem) DataDictionary.getFieldItem(baseid).clone();
			
			String [] textList={fielditem.getItemdesc(),"人数","总额","比例（%）","平均值","最低值","最高值"};
			
			//如果设置了标题和页头之类的则所有数据往后移动
			int addColum = 0;
			boolean hasTopHead = false;
			//添加主标题
			if(StringUtils.isNotBlank(formTitle)) {
				formTitle = pageSet.getRealcontent(formTitle, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(formTitle);
				view.setCellFormat(tTitle, 0, 0, 0, textList.length-1);
				addColum++;
			}
			//添加页头上左
			if(StringUtils.isNotBlank(topLeft)) {
				topLeft = pageSet.getRealcontent(topLeft, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(addColum, 0, topLeft);
				view.setCellFormat(hTitle, addColum, 0, addColum, 1);
				hasTopHead = true;
			}
			//添加页头上中
			if(StringUtils.isNotBlank(topCenter)) {
				topCenter = pageSet.getRealcontent(topCenter, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(addColum, (textList.length-1)/2, topCenter);
				view.setCellFormat(hTitle, addColum, (textList.length-1)/2, addColum, (textList.length-1)/2+1);
				hasTopHead = true;
			}
			//添加页头上右
			if(StringUtils.isNotBlank(topRight)) {
				topRight = pageSet.getRealcontent(topRight, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(addColum, textList.length-1, topRight);
				view.setCellFormat(hTitle, addColum, textList.length-1, addColum, textList.length-1);
				hasTopHead = true;
			}
			
			if(hasTopHead)//如果有上左，上中，上右其中的一个，直加一行
				addColum++;
			
			
			for(int i=0;i<textList.length;i++){
				String fieldName=textList[i];

				view.setText(addColum, i, fieldName);
				view.setCellFormat(cfTitle, addColum, i, addColum,i); // 设置标题区域 和样式
			}

			String codesetid=fielditem.getCodesetid();
			String content="";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";	
			ArrayList<String> rowList=null;
			int rowNums = 0;//数据所在的行
			for(int rowNum=0;rowNum<dataList.size();rowNum++,rowNums++){
				rowList=dataList.get(rowNum);
				if(rowNum==0)
					rowNums = rowNum + addColum;
				for(int colNum=0;colNum<rowList.size();colNum++){
					if(colNum==0){

						String value=rowList.get(colNum);
						
							if(!StringUtils.isBlank(codesetid)){
							    if("un".equalsIgnoreCase(codesetid)){
							        content = AdminCode.getCodeName("UN", value);
							        if(StringUtils.isBlank(content))
							            content = AdminCode.getCodeName("UM", value);
							    } 
							    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
								{							
									if("e0122".equalsIgnoreCase(fielditem.getItemid()))
									{
										if(Integer.parseInt(display_e0122)==0)
											content=AdminCode.getCodeName("UM",value);
										else
										{
											CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
							    	    	if(item!=null)
							    	    	{
							    	    		content=item.getCodename();
							        		}
							    	    	else
							    	    	{
							    	    		content=AdminCode.getCodeName("UM",value);
							    	    	}
										}								
									}else
										content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);	
								
							}else
								content=AdminCode.getCodeName(codesetid, value);
							    
							view.setText(rowNums+1, colNum, "".equalsIgnoreCase(content)?"空":content );
						}else
							view.setText(rowNums+1, colNum,value );
							view.setCellFormat(cfBody,rowNums+1 , colNum, rowNums+1 , colNum);
							
					}else{
						if(colNum!=1)
							cfBodyNum.setCustomFormat("0.00;(0.00);0.00");
						else
							cfBodyNum.setCustomFormat("0;(0);0");
						
						view.setNumber(rowNums+1, colNum, Double.parseDouble(rowList.get(colNum)));
						view.setCellFormat(cfBodyNum,rowNums+1 , colNum, rowNums+1 , colNum);
					}

				}
			}
			view.setText(rowNums,0,"总计");
			
			if(other!=0d){//若“其他”不为0，则将“其他”行值写入
				view.setText(2, 9,"其他" );
				view.setNumber(2, 10, other);
			}
			//添加页尾上左
			if(StringUtils.isNotBlank(footLeft)) {
				footLeft = pageSet.getRealcontent(footLeft, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, 0, footLeft);
				view.setCellFormat(fTitle, rowNums+1, 0, rowNums+1, 0);
				hasTopHead = true;
			}
			//添加页尾上中
			if(StringUtils.isNotBlank(footCenter)) {
				footCenter = pageSet.getRealcontent(footCenter, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, (textList.length-1)/2, footCenter);
				view.setCellFormat(fTitle, rowNums+1, (textList.length-1)/2, rowNums+1, (textList.length-1)/2);
				hasTopHead = true;
			}
			//添加页尾上右
			if(StringUtils.isNotBlank(footRight)) {
				footRight = pageSet.getRealcontent(footRight, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, textList.length-1, footRight);
				view.setCellFormat(fTitle, rowNums+1, textList.length-1, rowNums+1, textList.length-1);
				hasTopHead = true;
			}
			
			String sheetname="sheet1";
			StringBuffer valuePlacePie=new StringBuffer();
			StringBuffer textPlace=new StringBuffer();
			StringBuffer valuePlaceBar=new StringBuffer();
			for(int i=0;i<showRowNum.size();i++){
				int n=Integer.parseInt(((ArrayList)showRowNum.get(i)).get(0).toString())+2;//和实际excel差2个纵坐标
				valuePlacePie.append(sheetname+"!$D$"+n+",");//饼图数据为D列
				textPlace.append(sheetname+"!$A$"+n+",");
				valuePlaceBar.append(sheetname+"!$E$"+n+",");//柱状图数据为E列
			}
			valuePlacePie.deleteCharAt(valuePlacePie.length()-1);
			textPlace.deleteCharAt(textPlace.length()-1);
			valuePlaceBar.deleteCharAt(valuePlaceBar.length()-1);
			
			ChartShape chartshapeBar=view.addChart(textList.length,18, 16, 33 );//渲染位置  左上右下
			chartshapeBar.setChartType(ChartShape.TypeColumn);//设置为柱状图
			
			chartshapeBar.addSeries();
			
			chartshapeBar.setSeriesYValueFormula(0, valuePlaceBar.toString());//设置柱状图数据范围
			chartshapeBar.setCategoryFormula(textPlace.toString());//设置柱状图图例范围
			//设置横坐标标题
			chartshapeBar.setAxisTitle(ChartShape.XAxis, 0, textList[0]);
            //设置纵坐标标题
			chartshapeBar.setAxisTitle(ChartShape.YAxis, 0, "平均值");
			//chartshapeBar.initData(new RangeRef(0, 1,  1,dataList.size()-1), false);
			chartshapeBar.setVaryColors(true);
			
			
			ChartShape chartshapePie=view.addChart(textList.length,0, 16, 18 );//渲染位置 左上右下
			chartshapePie.setChartType(ChartShape.TypePie);//设置为饼图
			//chartshapePie.initData(new RangeRef(0, 1,  1,dataList.size()-1), false);
			chartshapePie.addSeries();
			
			
			if(other!=0d){//若“其他”不为0，则将“其他”行坐标加入图表中
				valuePlacePie.append(","+sheetname+"!$K$3");//'其他'
				textPlace.append(","+sheetname+"!$J$3");//值
			}
				
			
			chartshapePie.setSeriesYValueFormula(0, valuePlacePie.toString());//设置饼图数据范围
			chartshapePie.setCategoryFormula(textPlace.toString());//设置饼图图例范围
//			chartshapePie.addSeries();
			//chartshapePie.setSeriesXValueFormula(0,sheetname+"!$D$2:$D$5");
			chartshapePie.setVaryColors(true);//设置为彩色

			view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+fileName);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			view.releaseLock();
		}
		return fileName;
	}

	/**
	 * 返回写入的行号
	 * @param dataRowNum 数据行号
	 * @param pageNum 页码
	 * @param zongjiNum 总计行的数量
	 * @author ZhangHua
	 * @date 15:58 2018/5/18
	 * @return
	 */
	private int getRowNum(int dataRowNum,int pageNum,int zongjiNum,String viewtype,int addColum){
		//换页后的空行 2表示空1行 3空2行
		int blankLine="1".equals(viewtype)?2:3;
		//+1因为第一行是标题
		return dataRowNum+pageNum*blankLine-zongjiNum+1+addColum;
	}
	
	/**
	 * 
	 * @param itemText 页面列名
	 * @param dataList 数据集
	 * @param tableTitle 
	 * @param groupvalues
	 * @param groupName
	 * @param viewtype
	 * @param groupPageNum
	 * @return
	 * @throws GeneralException
	 * @author zhanghua
	 * @date 2017年6月9日 上午10:01:39
	 */
	private String exportExcel(ReportParseVo pageSet, ArrayList<FieldItem> Fielditem,ArrayList<ArrayList> dataList,String tableTitle,String groupvalues,String groupName
			,String viewtype,ArrayList<Integer> groupPageNum,HashMap itemIdMap) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.frameconn);
		String fileName="";
		View view = new View();
		view.getLock();
		try{
			
			//标题
			String formTitle = pageSet.getTitle_fw();//表格标题
			String titleFontType = pageSet.getTitle_fn();//标题字体
			String titleFontBlod = pageSet.getTitle_fb();//标题粗体
			String titleColor = pageSet.getTitle_fc();//标题颜色
			String titleItalic = pageSet.getTitle_fi();//标题斜体
			String titleUnderLine = pageSet.getTitle_fu();//标题下划线
			String titleDelLine = pageSet.getTitle_fs();//标题删除线
			String titleSize = StringUtils.isBlank(pageSet.getTitle_fz())?"14":pageSet.getTitle_fz();//标题字体大小
			
			//页头
			String topLeft = pageSet.getHead_flw();//页头上左内容
			String topCenter = pageSet.getHead_fmw();//上中
			String topRight = pageSet.getHead_frw();//上右
			String topFontType = pageSet.getHead_fn();//字体类型
			String topFontBlod = pageSet.getHead_fb();//粗体
			String topFontColor = pageSet.getHead_fc();//颜色
			String topItalic = pageSet.getHead_fi();//斜体
			String topUnderLine = pageSet.getHead_fu();//下划线
			String topDelLine = pageSet.getHead_fs();//删除线
			String topSize = StringUtils.isBlank(pageSet.getHead_fz())?"10":pageSet.getHead_fz();//字体大小
			
			//页尾
			String footLeft = pageSet.getTile_flw();//页尾上左内容
			String footCenter = pageSet.getTile_fmw();//上中
			String footRight = pageSet.getTile_frw();//上右
			String footFontType = pageSet.getTile_fn();//字体类型
			String footFontBlod = pageSet.getTile_fb();//粗体
			String footFontColor = pageSet.getTile_fc();//颜色
			String footItalic = pageSet.getTile_fi();//斜体
			String footUnderLine = pageSet.getTile_fu();//下划线
			String footDelLine = pageSet.getTile_fs();//删除线
			String footSize = StringUtils.isBlank(pageSet.getTile_fz())?"10":pageSet.getTile_fz();//字体大小
			
			//正文内容
			String bodyFontType = pageSet.getBody_fn();//字体类型
			String bodyFontBlod = pageSet.getBody_fb();//粗体
			//可能第一次什么都没设置，直接点
			String bodyFontColor = StringUtils.isBlank(pageSet.getBody_fc())?"#000000":pageSet.getBody_fc();//颜色默认黑色
			String bodyItalic = pageSet.getBody_fi();//斜体
			String bodyUnderLine = pageSet.getBody_fu();//下划线
			String bodySize = StringUtils.isBlank(pageSet.getBody_fz())?"10":pageSet.getBody_fz();//字体大小
			
			//正文表头
			String theadFontType = pageSet.getThead_fn();//字体类型
			String theadFontBlod = pageSet.getThead_fb();//粗体
			String theadFontColor = StringUtils.isBlank(pageSet.getThead_fc())?"#000000":pageSet.getThead_fc();//颜色
			String theadItalic = pageSet.getThead_fi();//下划线
			String theadUnderLine = pageSet.getThead_fu();//斜体
			String theadSize = StringUtils.isBlank(pageSet.getThead_fz())?"10":pageSet.getThead_fz();//字体大小
			
			fileName=this.userView.getUserName()+"_"+tableTitle+".xls";
			
			
			// 设置颜色
			view.setPaletteEntry(1, new Color(230, 230, 230));// 浅灰色
			view.setPaletteEntry(2, new Color(217, 217, 217));// 深灰色
			view.setPaletteEntry(3, new Color(250, 0, 0));// 浅灰色
			//标题的样式
			CellFormat tTitle = null;
			if(StringUtils.isNotBlank(formTitle)) {
				tTitle = view.getCellFormat();
				tTitle.setFontSize(Double.parseDouble(titleSize));// 设置字体大小
				tTitle.setFontBold(StringUtils.isNotBlank(titleFontBlod)?true:false);//粗体
				tTitle.setFontItalic(StringUtils.isNotBlank(titleItalic)?true:false);//斜线
				tTitle.setFontStrikeout(StringUtils.isNotBlank(titleDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(titleUnderLine))//下划线
					tTitle.setFontUnderline((short)1);
				int[] tColor=new int[3]; 
				tColor[0]=Integer.parseInt(titleColor.substring(1, 3), 16); 
				tColor[1]=Integer.parseInt(titleColor.substring(3, 5), 16); 
				tColor[2]=Integer.parseInt(titleColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(titleColor)) {
					tTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					tTitle.setFontColor(new Color(tColor[0], tColor[1], tColor[2]));//颜色
				}
				tTitle.setFontName(titleFontType);//字体
	            
				tTitle.setMergeCells(true);// 合并单元格
				tTitle.setWordWrap(false);
				// 水平对齐方式
				tTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				// 垂直对齐方式
				tTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			CellFormat hTitle = null;
			if(StringUtils.isNotBlank(topLeft) || StringUtils.isNotBlank(topCenter) || StringUtils.isNotBlank(topRight)) {
				hTitle = view.getCellFormat();
				//页头的样式
				hTitle.setFontSize(Double.parseDouble(topSize));// 设置字体大小
				hTitle.setFontBold(StringUtils.isNotBlank(topFontBlod)?true:false);//粗体
				hTitle.setFontItalic(StringUtils.isNotBlank(topItalic)?true:false);//斜线
				hTitle.setFontStrikeout(StringUtils.isNotBlank(topDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(topUnderLine))//下划线
					hTitle.setFontUnderline((short)1);
				int[] hColor=new int[3]; 
				hColor[0]=Integer.parseInt(topFontColor.substring(1, 3), 16); 
				hColor[1]=Integer.parseInt(topFontColor.substring(3, 5), 16); 
				hColor[2]=Integer.parseInt(topFontColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(topFontColor)) {
					hTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					hTitle.setFontColor(new Color(hColor[0], hColor[1], hColor[2]));//颜色
				}
				hTitle.setFontName(topFontType);//字体
	            
				hTitle.setWordWrap(true);
				// 垂直对齐方式
				hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				hTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			CellFormat fTitle = null;
			if(StringUtils.isNotBlank(footLeft) || StringUtils.isNotBlank(footCenter) || StringUtils.isNotBlank(footRight)) {
				fTitle = view.getCellFormat();
				//页尾的样式
				fTitle.setFontSize(Double.parseDouble(footSize));// 设置字体大小
				fTitle.setFontBold(StringUtils.isNotBlank(footFontBlod)?true:false);//粗体
				fTitle.setFontItalic(StringUtils.isNotBlank(footItalic)?true:false);//斜线
				fTitle.setFontStrikeout(StringUtils.isNotBlank(footDelLine)?true:false);//删除线
				if(StringUtils.isNotBlank(footUnderLine))//下划线
					fTitle.setFontUnderline((short)1);
				int[] fColor=new int[3]; 
				fColor[0]=Integer.parseInt(footFontColor.substring(1, 3), 16); 
				fColor[1]=Integer.parseInt(footFontColor.substring(3, 5), 16); 
				fColor[2]=Integer.parseInt(footFontColor.substring(5, 7), 16);
				if("FF0000".equalsIgnoreCase(footFontColor)) {
					fTitle.setFontColor(view.getPaletteEntry(3));//颜色
				}else {
					fTitle.setFontColor(new Color(fColor[0], fColor[1], fColor[2]));//颜色
				}
				fTitle.setFontName(footFontType);//字体
				fTitle.setWordWrap(true);
				// 水平对齐方式
				fTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				// 垂直对齐方式
				fTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);
			}
			
			// 表头样式
			// view.setDefaultRowHeight(800);
			CellFormat cfTitle = view.getCellFormat();
			cfTitle.setFontSize(Double.parseDouble(theadSize));// 设置字体大小
			cfTitle.setFontBold(StringUtils.isNotBlank(theadFontBlod)?true:false);//粗体
			cfTitle.setFontItalic(StringUtils.isNotBlank(theadItalic)?true:false);//斜线
			if(StringUtils.isNotBlank(theadUnderLine))//下划线
				cfTitle.setFontUnderline((short)1);
			int[] cfColor=new int[3]; 
			cfColor[0]=Integer.parseInt(theadFontColor.substring(1, 3), 16); 
			cfColor[1]=Integer.parseInt(theadFontColor.substring(3, 5), 16); 
			cfColor[2]=Integer.parseInt(theadFontColor.substring(5, 7), 16);
			if("FF0000".equalsIgnoreCase(theadFontColor)) {
				cfTitle.setFontColor(view.getPaletteEntry(3));//颜色
			}else {
				cfTitle.setFontColor(new Color(cfColor[0], cfColor[1], cfColor[2]));//颜色
			}
            cfTitle.setFontName(theadFontType);//字体
            
			cfTitle.setBottomBorder((short) 1);// 设置边框为细实线
			cfTitle.setTopBorder(CellFormat.PatternSolid);
			cfTitle.setLeftBorder((short) 1);
			cfTitle.setRightBorder((short) 1);
			cfTitle.setWordWrap(true);
			// 水平对齐方式
			cfTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
			// 垂直对齐方式
			cfTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);

			// 内容样式
			CellFormat cfBody = view.getCellFormat();
			cfBody.setFontSize(Double.parseDouble(bodySize));// 设置字体大小
			cfBody.setFontBold(StringUtils.isNotBlank(bodyFontBlod)?true:false);//粗体
			cfBody.setFontItalic(StringUtils.isNotBlank(bodyItalic)?true:false);//斜线
			if(StringUtils.isNotBlank(bodyUnderLine))//下划线
				cfBody.setFontUnderline((short)1);
			int[] bodyColor=new int[3]; 
			bodyColor[0]=Integer.parseInt(bodyFontColor.substring(1, 3), 16); 
			bodyColor[1]=Integer.parseInt(bodyFontColor.substring(3, 5), 16); 
			bodyColor[2]=Integer.parseInt(bodyFontColor.substring(5, 7), 16);
			if("FF0000".equalsIgnoreCase(bodyFontColor)) {
				cfBody.setFontColor(view.getPaletteEntry(3));//颜色
			}else {
				cfBody.setFontColor(new Color(bodyColor[0], bodyColor[1], bodyColor[2]));//颜色
			}
			cfBody.setFontName(bodyFontType);//字体
            
			cfBody.setWordWrap(true);
			cfBody.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfBody.setTopBorder(CellFormat.PatternSolid);
			cfBody.setLeftBorder(CellFormat.PatternSolid);
			cfBody.setRightBorder(CellFormat.PatternSolid);
			//cfBody.setHorizontalAlignment(cfBody.HorizontalAlignmentLeft);// 水平居左
			cfBody.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中
			
			
			CellFormat cfBodyNum = view.getCellFormat();
			cfBodyNum.setFontSize(10);
			cfBodyNum.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfBodyNum.setTopBorder(CellFormat.PatternSolid);
			cfBodyNum.setLeftBorder(CellFormat.PatternSolid);
			cfBodyNum.setRightBorder(CellFormat.PatternSolid);
			cfBodyNum.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);// 水平居右
			cfBodyNum.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中
			
			// 表头样式
			CellFormat cfcolum = view.getCellFormat();
			cfcolum.setFontSize(10);
			cfcolum.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfcolum.setTopBorder(CellFormat.PatternSolid);
			cfcolum.setLeftBorder(CellFormat.PatternSolid);
			cfcolum.setRightBorder(CellFormat.PatternSolid);
			cfcolum.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);// 水平居中
			cfcolum.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中

			view.setDefaultColWidth(15 * 256);// 固定列宽
			cfTitle.setPattern(CellFormat.PatternSolid);
			cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
			
//			ArrayList<FieldItem> Fielditem=new ArrayList<FieldItem>();
			FieldItem field;
			
			int groupSize=-1;//汇总列的位置
			for(int i=0;i<Fielditem.size();i++){
				String fieldName=Fielditem.get(i).getItemdesc();
				if(fieldName.equalsIgnoreCase(groupvalues))
					groupSize=i;
			}
			//如果设置了标题和页头之类的则所有数据往后移动
			int addColum = 0;
			boolean hasTopHead = false;
			//添加主标题
			if(StringUtils.isNotBlank(formTitle)) {
				formTitle = pageSet.getRealcontent(formTitle, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(formTitle);
				view.setCellFormat(tTitle, 0, 0, 0, Fielditem.size()-1);
				addColum++;
			}
			//添加页头上左
			if(StringUtils.isNotBlank(topLeft)) {
				topLeft = pageSet.getRealcontent(topLeft, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(addColum, 0, topLeft);
				hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
				view.setCellFormat(hTitle, addColum, 0, addColum, 0);
				hasTopHead = true;
			}
			//添加页头上中
			if(StringUtils.isNotBlank(topCenter)) {
				topCenter = pageSet.getRealcontent(topCenter, userView, dataList.size(), tableTitle, 1, dao);
				hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
				view.setText(addColum, (Fielditem.size()-1)/2, topCenter);
				view.setCellFormat(hTitle, addColum, (Fielditem.size()-1)/2, addColum, (Fielditem.size()-1)/2);
				hasTopHead = true;
			}
			//添加页头上右
			if(StringUtils.isNotBlank(topRight)) {
				topRight = pageSet.getRealcontent(topRight, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(addColum, Fielditem.size()-1, topRight);
				hTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);
				view.setCellFormat(hTitle, addColum, Fielditem.size()-1, addColum, Fielditem.size()-1);
				hasTopHead = true;
			}
			
			if(hasTopHead)//如果有上左，上中，上右其中的一个，直加一行
				addColum++;
			
			this.setHeadTitle(cfTitle, Fielditem, view, addColum);//插入标题
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";		
			ArrayList<String> rowList=null;
			int num=0;
			
			int zongji=0;//当分组指标为部门时 会存在多个总计行 若存在一次总计行 就会存在一个空行 所以就要将整个数据减去一行 
			int rowNums = 0;//数据所在的行
			for(int rowNum=0;rowNum<dataList.size();rowNum++){
				rowList=dataList.get(rowNum);
				if(groupPageNum!=null&&groupSize!=-1&& "总计".equals(rowList.get(groupSize)))//分组分页打印取消总计
				{
					zongji++;
					continue;
				}
				rowNums = this.getRowNum(rowNum,num,zongji,viewtype,addColum);//行号
				for(int colNum=0;colNum<rowList.size();colNum++){
					field=Fielditem.get(colNum);
					//对于数值和日期型的水平居右
					short align = 0; 
					if("N".equalsIgnoreCase(field.getItemtype()) || "D".equalsIgnoreCase(field.getItemtype()))
						align = CellFormat.HorizontalAlignmentRight;
					else 
						align = CellFormat.HorizontalAlignmentLeft;
					cfBody.setHorizontalAlignment(itemIdMap==null?align:itemIdMap.get(field.getItemid().toLowerCase() + "align")==null?align:(Short)itemIdMap.get(field.getItemid().toLowerCase() + "align"));
					if(rowNum == 0) {
						view.setColWidth(colNum, itemIdMap==null?(15 * 256):itemIdMap.get(field.getItemid().toLowerCase() + "displaywidth")==null?(15 * 256):(Integer)itemIdMap.get(field.getItemid().toLowerCase() + "displaywidth"));
					}
					
					if(!StringUtils.isBlank(groupvalues)&&field.getItemid().equalsIgnoreCase(groupvalues)){
						view.setText(rowNums, colNum, rowList.get(colNum));
						view.setCellFormat(cfBody,rowNums , colNum, rowNums , colNum);
					}
					else if("A".equalsIgnoreCase(field.getItemtype())){
						String codesetid=field.getCodesetid();
						String content="";
						String value=rowList.get(colNum);
						
						if(!StringUtils.isBlank(codesetid)&&!"0".equals(codesetid)){
						    if("un".equalsIgnoreCase(codesetid)){
						        content = AdminCode.getCodeName("UN", value);
						        if(StringUtils.isBlank(content))
						            content = AdminCode.getCodeName("UM", value);
						    } 
						    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
							{							
								if("e0122".equalsIgnoreCase(field.getItemid()))
								{
									if(Integer.parseInt(display_e0122)==0)
										content=AdminCode.getCodeName("UM",value);
									else
									{
										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		content=item.getCodename();
						        		}
						    	    	else
						    	    	{
						    	    		content=AdminCode.getCodeName("UM",value);
						    	    	}
									}								
								}else
									content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);	
							
						}else 
							content=AdminCode.getCodeName(codesetid, value);

						
						view.setText(rowNums, colNum, content);	
						view.setCellFormat(cfBody,rowNums , colNum, rowNums , colNum);
					}
					else
						view.setText(rowNums, colNum, rowList.get(colNum));
						view.setCellFormat(cfBody,rowNums, colNum, rowNums , colNum);
					}
					else if("D".equalsIgnoreCase(field.getItemtype())){
						view.setText(rowNums, colNum, rowList.get(colNum));
						view.setCellFormat(cfBody,rowNums , colNum, rowNums , colNum);
					}
					else if("N".equalsIgnoreCase(field.getItemtype())){
						String value=rowList.get(colNum);
						if(value==null|| "".equals(value))
							value="0";
						
						String str="0.";
						for(int i=0;i<field.getDecimalwidth();i++)
							str+="0";
						if(str.length()==2)
							str="0";
						if(!"a00z1".equalsIgnoreCase(field.getItemid())&&!"a00z3".equalsIgnoreCase(field.getItemid()))
							//cfBodyNum.setCustomFormat(str+";("+str+");"+str);//数字格式化
							cfBody.setCustomFormat(str);//数字格式化
						else
							cfBody.setCustomFormat("#;(#);#");
						view.setNumber(rowNums, colNum,Double.parseDouble( value));
						view.setCellFormat(cfBody,rowNums, colNum, rowNums, colNum);
					}
					//备注型没有值
					else{
						view.setText(rowNums, colNum, rowList.get(colNum));
						view.setCellFormat(cfBody,rowNums , colNum, rowNums , colNum);
					}
				}
				if(("1".equals(viewtype)&&rowNum+1<dataList.size())||(groupPageNum!=null&&num<groupPageNum.size()&&rowNum==groupPageNum.get(num))){//如果当前行为需要分页的行，则插入标题 并空一行
					num++;
					this.setHeadTitle(cfTitle, Fielditem, view, this.getRowNum(rowNum,num,zongji,viewtype,addColum));
				}
			}
			//添加页尾下左
			if(StringUtils.isNotBlank(footLeft)) {
				footLeft = pageSet.getRealcontent(footLeft, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, 0, footLeft);
				view.setCellFormat(fTitle, rowNums+1, 0, rowNums+1, 0);
			}
			//添加页尾下中
			if(StringUtils.isNotBlank(footCenter)) {
				footCenter = pageSet.getRealcontent(footCenter, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, (Fielditem.size()-1)/2, footCenter);
				view.setCellFormat(fTitle, rowNums+1, (Fielditem.size()-1)/2, rowNums+1, (Fielditem.size()-1)/2);
				
			}
			//添加页尾下右
			if(StringUtils.isNotBlank(footRight)) {
				footRight = pageSet.getRealcontent(footRight, userView, dataList.size(), tableTitle, 1, dao);
				view.setText(rowNums+1, Fielditem.size()-1, footRight);
				view.setCellFormat(fTitle, rowNums+1, Fielditem.size()-1, rowNums+1, Fielditem.size()-1);
			}
			
			view.write(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+fileName);
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			view.releaseLock();
		}
		return fileName;
	}
	
	/**
	 * 设置标题
	 * @param cfTitle 标题样式
	 * @param Fielditem 标题列表
	 * @param view
	 * @param row 行号
	 */
	private void setHeadTitle(CellFormat cfTitle,ArrayList<FieldItem> Fielditem ,View view,int row){
		
		try {
			FieldItem field=null;
			for(int i =0;i<Fielditem.size();i++){
				field=Fielditem.get(i);
				view.setText(row, i, field.getItemdesc());
				view.setCellFormat(cfTitle, row, i, row,i); // 设置标题区域 和样式
			}
		} catch (CellException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
