package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
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
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 薪资报表导出pdf交易类
 * @author zhanghua
 *
 */
public class SalaryReportPrintTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
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
			
			String groupField="",UserFlag="",subModuleId="salaryreport_"+rsdtlid;
			String baseid = "";//统计项目
			String itemid = "";//统计指标
			if("4".equals(rsid)){
				baseid = (String) this.getFormHM().get("baseid");//统计项目
				itemid = (String) this.getFormHM().get("itemid");//统计指标
				subModuleId="salarycollect";
			}
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
			ArrayList<ColumnsInfo> displayColumns=new ArrayList<ColumnsInfo>();
			if(tableCache!=null){
				displayColumns=(ArrayList<ColumnsInfo>)tableCache.getDisplayColumns();
			}
			SalaryReportBo bo=new SalaryReportBo(this.getFrameconn(), salaryid, userView,rsid,rsdtlid,true);
			
			Boolean bgroup= "0".equals(bo.getReportdetailvo().getString("bgroup"))?false:true;//是否启用分组
			boolean isGroupPage= "1".equalsIgnoreCase(bo.getIsGroup(bo.getReportdetailvo().getString("ctrlparam")))?true:false;//是否启用分组打印
			if(("2".equals(rsid)|| "12".equals(rsid))&&bgroup){
				UserFlag=(String) this.getFormHM().get("UserFlag");
				groupField = (String) this.getFormHM().get("groupField");
			}
			
			ArrayList<LazyDynaBean> List=bo.getExportReportDataList(model, bosdate, count, UserFlag, groupvalues, baseid, itemid);

			
			String tableTitle=(String)this.getFormHM().get("tableTitle");//表名
			
			
			//String groupvalues=(String)this.getFormHM().get("groupvalues");//分组项
	
			ArrayList<ArrayList> dataList=new ArrayList<ArrayList>();
			ArrayList<String> rowList;
			
			
			if("1".equals(rsid) ||(   ("2".equals(rsid)|| "12".equals(rsid))&&!bgroup  ) ){////若为 工资单或者没有设置汇总指标的签名表  则获取页面sql 进行数据拼装
				dataList=bo.getRepotrDataList(tableCache);
			}else{
				if(List!=null&&List.size()!=0){//根据页面列头拼装数据
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

			
			String groupName="";
			if("3".equals(rsid)|| "13".equals(rsid)){
				LazyDynaBean groupBean=null;
				groupBean=bo.getGroupBean();
				
				groupField=(String)groupBean.get("f_groupItem");
				
				String sgroup=(String)groupBean.get("s_groupDesc");
				if(!StringUtils.isBlank(sgroup))
					groupName=(String)groupBean.get("f_groupDesc")+"&"+sgroup;
			}
			
			tableTitle=tableTitle.substring(tableTitle.lastIndexOf("-->")+3, tableTitle.length());
			
			
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.getFrameconn(), this.userView, rsid + "", rsdtlid);
			ReportParseVo rpv = rpob.analyse(0);
			
			int schemeId = bo.getSchemeId("salaryreport_"+rsdtlid);
			// 从数据库中得到可以显示的薪资项目代码
			HashMap itemIdMap = null;
			if(schemeId > 0){
				itemIdMap = bo.getItemsToMap(schemeId,"1",1);
			}

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
			
			String fileName=this.exportPDF(fieldItems, dataList,rpv,tableTitle,groupField,groupName,groupPageNum,rpv,itemIdMap);
			
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
		
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
	}
	
	/**
	 * 导出pdf
	 * @param itemText 列头id
	 * @param dataList 数据
	 * @param pageSet pdf参数  必须包含 paperType:纸张类型 disTop disBottom disLeft disRight： 上下左右边距 normal：纸张方向0为纵向 1为横向
	 * @param tableTitle 表名
	 * @return
	 */
	private String exportPDF(ArrayList<FieldItem> Fielditem,ArrayList<ArrayList> dataList,ReportParseVo pageSet,final String tableTitle,String groupvalues,
			String groupName,ArrayList<Integer> groupPageNum,final ReportParseVo bo,HashMap map) throws GeneralException {
		
		float mm=2.83f;//一毫米 约为2.83像素

		
		final ContentDAO dao = new ContentDAO(this.frameconn);
		PdfWriter writer=null;
		
		String fileName="";
		String titleFontName = "";//得到标题字体的类型
		String pageHeadFontName = "";//得到页头字体的类型
		String pageFootFontName = "";//得到页尾字体的类型
		String theadFontName = "";//得到正文标题的类型
		String bodyFontName = "";//得到正文内容的类型
		String titleEnc = "";//baseFont的encoding
		String pageHeadEnc = "";
		String pageFootEnc = "";
		String theadEnc = "";
		String bodyEnc = "";
		Document document = null;
		FileOutputStream out = null;
		try
		{
			Rectangle pageSize = this.getPageSize(pageSet);//获取纸张大小
			
			float height=Float.parseFloat(pageSet.getHeight());
			float width=Float.parseFloat(pageSet.getWidth());
			
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
			final String topLeft = pageSet.getHead_flw();//页头上左内容
			final String topCenter = pageSet.getHead_fmw();//上中
			final String topRight = pageSet.getHead_frw();//上右
			String topFontType = pageSet.getHead_fn();//字体类型
			String topFontBlod = pageSet.getHead_fb();//粗体
			String topFontColor = pageSet.getHead_fc();//颜色
			String topItalic = pageSet.getHead_fi();//斜体
			String topUnderLine = pageSet.getHead_fu();//下划线
			String topDelLine = pageSet.getHead_fs();//删除线
			final String topSize = StringUtils.isBlank(pageSet.getHead_fz())?"10":pageSet.getHead_fz();//字体大小
			final String topLHomeShow = pageSet.getHead_flw_hs();//上左仅首页显示
			final String topMHomeShow = pageSet.getHead_fmw_hs();//上中仅首页显示
			final String topRHomeShow = pageSet.getHead_frw_hs();//上右仅首页显示
			
			//页尾
			final String footLeft = pageSet.getTile_flw();//页尾上左内容
			final String footCenter = pageSet.getTile_fmw();//上中
			final String footRight = pageSet.getTile_frw();//上右
			String footFontType = pageSet.getTile_fn();//字体类型
			String footFontBlod = pageSet.getTile_fb();//粗体
			String footFontColor = pageSet.getTile_fc();//颜色
			String footItalic = pageSet.getTile_fi();//斜体
			String footUnderLine = pageSet.getTile_fu();//下划线
			String footDelLine = pageSet.getTile_fs();//删除线
			String footSize = StringUtils.isBlank(pageSet.getTile_fz())?"10":pageSet.getTile_fz();//字体大小
			final String footLHomeShow = pageSet.getTile_flw_hs();//下左仅首页显示
			final String footMHomeShow = pageSet.getTile_fmw_hs();//下中仅首页显示
			final String footRHomeShow = pageSet.getTile_frw_hs();//下右仅首页显示
			
			//正文内容
			String bodyFontType = pageSet.getBody_fn();//字体类型
			String bodyFontBlod = pageSet.getBody_fb();//粗体
			String bodyFontColor = pageSet.getBody_fc();//颜色
			String bodyItalic = pageSet.getBody_fi();//斜体
			String bodyUnderLine = pageSet.getBody_fu();//下划线
			String bodySize = StringUtils.isBlank(pageSet.getBody_fz())?"10":pageSet.getBody_fz();//字体大小
			
			//正文表头
			String theadFontType = pageSet.getThead_fn();//字体类型
			String theadFontBlod = pageSet.getThead_fb();//粗体
			String theadFontColor = pageSet.getThead_fc();//颜色
			String theadItalic = pageSet.getThead_fi();//斜体
			String theadUnderLine = pageSet.getThead_fu();//下划线
			String theadSize = StringUtils.isBlank(pageSet.getThead_fz())?"10":pageSet.getThead_fz();//字体大小
			
			final int totalNum = dataList.size();//总人数
			
			float distop = 0;
			float disbottom = 0;//由于这里的设计是在每一页的页头和页尾写入规则是在整个table塞入之后加入的，不包含在table内，这样如果在有页头内容的时候上边距过小会显示错误
			distop = Float.parseFloat(StringUtils.isBlank(pageSet.getTop())?"21":(Integer.parseInt(pageSet.getTop())<15?"15":pageSet.getTop()));//上边距
			disbottom = Float.parseFloat(StringUtils.isBlank(pageSet.getBottom())?"21":(Integer.parseInt(pageSet.getBottom())<15?"15":pageSet.getBottom()));//下边距
			
			float disLeft = Float.parseFloat(StringUtils.isBlank(pageSet.getLeft())?"25":pageSet.getLeft());
			float disRight = Float.parseFloat(StringUtils.isBlank(pageSet.getRight())?"25":pageSet.getRight());
			final float disTop = distop;
			final float disBottom = disbottom;
			
			document = new Document(pageSize,disLeft*mm, disRight*mm, disTop*mm, disBottom*mm);//创建页面(纸张大小，上,下,左,右边距)
			fileName=this.userView.getUserName()+"_"+tableTitle+".pdf";
            String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+fileName;
            File tempFile = new File(filePath);  
            if(!tempFile.exists()) {  
                try {  
                    tempFile.createNewFile();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
            out = new FileOutputStream(tempFile);
            writer=PdfWriter.getInstance(document, out);

			Font tfChinese = getFont(titleFontType, titleSize, titleFontBlod, titleItalic, titleUnderLine, titleDelLine, titleColor);

			//标题的样式

			//设置页头字体//页头
			final Font headFont = getFont(topFontType, topSize, topFontBlod, topItalic, topUnderLine, topDelLine, topFontColor);

			//页尾
			//设置页尾字体
			final Font footFont = getFont(footFontType, footSize, footFontBlod, footItalic, footUnderLine, footDelLine, footFontColor);

			//正文标题设置字体
			final BaseFont bfChinese = getBaseFont(theadFontType);
			Font fontTheadChinese = getFont(theadFontType, theadSize, theadFontBlod, theadItalic, theadUnderLine, "", theadFontColor);

			//正文内容设置字体
			Font fontBodyChinese = getFont(bodyFontType, bodySize, bodyFontBlod, bodyItalic, bodyUnderLine, "", bodyFontColor);

			document.open();

			//设置标题字体

            /***************添加标题************************/
            if(StringUtils.isNotBlank(formTitle))//如果没有手动设置标题，则将原先的添加tableTitle插入
            	formTitle = bo.getRealcontent(formTitle, userView, totalNum, tableTitle,1, dao);
            
            final String strbuffer=formTitle;
            
            Paragraph title = new Paragraph(strbuffer.toString(), tfChinese);// 设置标题  
            title.setAlignment(Element.ALIGN_CENTER);// 设置对齐方式  
            title.setLeading(20f);// 设置行间距  
            document.add(title); //插入标题
            

			FieldItem field=new FieldItem();

            HashMap<Integer,ArrayList<Float>> widthMap = new HashMap<Integer,ArrayList<Float>>();
            int getColNum = this.buildColumnData(Fielditem,map,widthMap,dataList);//一共换了多少次页,页码
		    float[] widthArray=null;
			ArrayList<Float> widths = widthMap.get(0);//列宽集合
			widthArray = new float[widths.size()];
			for(int i = 0; i < widths.size(); i++) {
				widthArray[i] = widths.get(i);
			}
            PdfPTable table = new PdfPTable(widthArray);//设置列宽
            table.setTotalWidth(widthArray);
			table.setHeaderRows(1);
            table.setLockedWidth(true);//锁定列宽
            
            if((StringUtils.isNotBlank(topLeft) || StringUtils.isNotBlank(topCenter) || StringUtils.isNotBlank(topRight)) && StringUtils.isNotBlank(strbuffer))
            	table.setSpacingBefore(40f + Float.parseFloat(topSize));//间距
            else
            	table.setSpacingBefore(20f);//间距
            
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";		
			ArrayList<String> rowList=null;
			
			/**
			 * 插入pdf数据，将全部数据按页分割。先纵向打印数据，剩余列新建一页重新循环数据 纵向打印。直至全部列打印完毕。
			 */
			
			/*int getColNum=(int) Math.ceil(itemText.size()/colsize);//	总列数/每页列数 得到 需截断列并新建一页的次数
			if(itemText.size()%colsize==0)
				getColNum--;*/
			
			int startNum =0,endNum=dataList.size(),groupNum=0;//endNum 每页结束的数据位置，groupNum分组分页的次数
			
			if(groupPageNum!=null&&groupPageNum.size()!=0){
				endNum=groupPageNum.get(groupNum)+groupNum;
			}
			
			//写入分页事件，在每页末尾加上页头页尾
            writer.setPageEvent(new PdfPageEventHelper() {
            	public PdfTemplate tpl;
            	
                @Override
                public void onEndPage(PdfWriter pdfWriter, Document document) {
                	try {
                		tpl = pdfWriter.getDirectContent().createTemplate(100, 100);
                		PdfContentByte headAndFootPdfContent = pdfWriter.getDirectContent();
                		headAndFootPdfContent.saveState();
                		headAndFootPdfContent.beginText();
                		int currentPage = pdfWriter.getPageNumber();
	                    float x1 = document.top(-15);
	                    float y = document.bottom(-20);  
	                    //包括了上左显示的内容，上左仅首页显示内容
	                	if(StringUtils.isNotBlank(topLeft) && ((StringUtils.isNotBlank(topLHomeShow) && currentPage == 1) || StringUtils.isBlank(topLHomeShow))) {//页头页尾，上左
	                		String lHead = bo.getRealcontent(topLeft, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                		if(currentPage == 1 && StringUtils.isNotBlank(strbuffer)) {//第一页如果有标题在标题中间插入页头
	                			x1 = document.top(35+Integer.parseInt(topSize));
	                		}
	                		if(lHead.indexOf("&[总页数]")!=-1) {
	                			headAndFootPdfContent.addTemplate(tpl, document.left()+6+lHead.indexOf("&[总页数]"), x1);//定位“y页” 
	                			lHead=lHead.replaceAll("&\\[总页数\\]","");
	                		}
	                		Phrase phraseLHead = new Phrase(lHead,headFont);
	                		ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_LEFT,phraseLHead,document.left()+6,x1,0);
	                	}
	                    
	                    if(StringUtils.isNotBlank(topCenter) && ((StringUtils.isNotBlank(topMHomeShow) && currentPage == 1) || StringUtils.isBlank(topMHomeShow))) { //页头页尾，上中
	                    	String mHead = bo.getRealcontent(topCenter, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                    	if(currentPage == 1 && StringUtils.isNotBlank(strbuffer))
	                			x1 = document.top(35+Integer.parseInt(topSize));
	                    	if(mHead.indexOf("&[总页数]")!=-1) {
	                    		headAndFootPdfContent.addTemplate(tpl, (document.right() + document.left())/2+mHead.indexOf("&[总页数]"), x1);//定位“y页” 
	                    		mHead=mHead.replaceAll("&\\[总页数\\]","");
	                    	}
	                    	Phrase phraseMHead = new Phrase(mHead,headFont);
	                    	ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_CENTER,phraseMHead,(document.right() + document.left())/2,x1,0);
	                    }
	                    	
	                    if(StringUtils.isNotBlank(topRight) && ((StringUtils.isNotBlank(topRHomeShow) && currentPage == 1) || StringUtils.isBlank(topRHomeShow))) {//页头页尾，上右
	                    	String rHead = bo.getRealcontent(topRight, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                    	if(currentPage == 1 && StringUtils.isNotBlank(strbuffer))
	                			x1 = document.top(35+Integer.parseInt(topSize));
	                    	if(rHead.indexOf("&[总页数]")!=-1) {
	                    		headAndFootPdfContent.addTemplate(tpl, document.right()-6+rHead.indexOf("&[总页数]"), x1);//定位“y页”
	                    		rHead=rHead.replaceAll("&\\[总页数\\]","");
	                    	}
	                    	Phrase phraseRHead = new Phrase(rHead,headFont);
	                    	ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_RIGHT,phraseRHead,document.right()-6,x1,0);
	                    }
	                    //下左及下左仅尾页显示
	                    if(StringUtils.isNotBlank(footLeft) && (StringUtils.isBlank(footLHomeShow) || (StringUtils.isNotBlank(footLHomeShow) && currentPage == lastPage))) {
	                    	String lfoot = bo.getRealcontent(footLeft, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                    	if(lfoot.indexOf("&[总页数]")!=-1) {
	                    		headAndFootPdfContent.addTemplate(tpl, document.left()+6+lfoot.indexOf("&[总页数]"), y);//定位“y页” 
	                    		lfoot=lfoot.replaceAll("&\\[总页数\\]","");
	                    	}
	                    	Phrase phraseLfoot = new Phrase(lfoot,footFont);
	                    	ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_LEFT,phraseLfoot,document.left()+6,y,0);
	                    }
	                    	
	                    if(StringUtils.isNotBlank(footCenter) && (StringUtils.isBlank(footMHomeShow) || (StringUtils.isNotBlank(footMHomeShow) && currentPage == lastPage))) {
	                    	String mfoot = bo.getRealcontent(footCenter, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                    	if(mfoot.indexOf("&[总页数]")!=-1) {
	                    		headAndFootPdfContent.addTemplate(tpl, (document.right() + document.left())/2+mfoot.indexOf("&[总页数]"), y);//定位“y页” 
	                    		mfoot=mfoot.replaceAll("&\\[总页数\\]","");
	                    	}
	                    	Phrase phraseMfoot = new Phrase(mfoot,footFont);
	                    	ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_CENTER,phraseMfoot,(document.right() + document.left())/2,y,0);
	                    }
	                    	
	                    if(StringUtils.isNotBlank(footRight) && (StringUtils.isBlank(footRHomeShow) || (StringUtils.isNotBlank(footRHomeShow) && currentPage == lastPage))) {
	                    	String rfoot = bo.getRealcontent(footRight, userView, totalNum, tableTitle,pdfWriter.getPageNumber(), dao);
	                    	if(rfoot.indexOf("&[总页数]")!=-1) {
	                    		headAndFootPdfContent.addTemplate(tpl, document.right()-6+rfoot.indexOf("&[总页数]"), y);//定位“y页” 
	                    		rfoot=rfoot.replaceAll("&\\[总页数\\]","");
	                    	}
	                    	Phrase phraseRfoot = new Phrase(rfoot,footFont);
	                    	ColumnText.showTextAligned(headAndFootPdfContent,Element.ALIGN_RIGHT,phraseRfoot,document.right()-6,y,0);
	                    }
	                    headAndFootPdfContent.endText();
	                    headAndFootPdfContent.saveState();
	                    headAndFootPdfContent.restoreState();
                	} catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                @Override
                public void onCloseDocument(PdfWriter pdfWriter, Document document) { 
                	tpl.beginText();
                	tpl.setFontAndSize(bfChinese, 22);
                	tpl.showText(Integer.toString(pdfWriter.getPageNumber() - 1));
                	tpl.endText();
                	tpl.closePath();//sanityCheck();
                }  
            });
			do{
				int count=0;//新建一页的次数
				if(groupNum!=0){//若存在汇总 则创建新页
					document.add(table);
					document.newPage();//创建新页
					widths = widthMap.get(0);//列宽集合
					widthArray = new float[widths.size()];
					for(int i = 0; i < widths.size(); i++) {
						widthArray[i] = widths.get(i);
					}

					table = new PdfPTable(widthArray);
					table.setHeaderRows(1);
					table.setTotalWidth(widthArray);
					table.setLockedWidth(true);
					startNum=groupPageNum.get(groupNum-1)+1;
					endNum=groupPageNum.get(groupNum);
					if(groupNum==groupPageNum.size()-1)
						endNum++;
				}
				int oldSize = 0;//记录上面循环了多少次列
				int colsize = 0;//每一页的列数
				while(getColNum>count){//若次数不足，继续循环
					widths = (ArrayList<Float>) widthMap.get(count);
					colsize = widths.size();
					for(int rowNum=startNum-1;rowNum<=endNum&&rowNum<dataList.size();rowNum++){//循环数据行
						if(rowNum==startNum-1){//首行设置标题
							for(int colNum=0;colNum+oldSize<Fielditem.size()&&colNum<colsize;colNum++){
								field=Fielditem.get(colNum+oldSize);
								
								PdfPCell cell = new PdfPCell(new Paragraph(field.getItemdesc(),fontTheadChinese));
							    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

								table.addCell(cell);
							}
							continue;
						}
						rowList=dataList.get(rowNum);
						//不清楚以前为什么跳过总计行，以后出了bug再改回来，否则导出pdf就无法显示总计行了
						/*if(groupSize!=-1&&rowList.get(groupSize).equalsIgnoreCase("总计"))//跳过总计行
							continue;*/
		
						for(int colNum=0;colNum+oldSize<rowList.size()&&colNum<colsize;colNum++){
	
							
							field=Fielditem.get(colNum+oldSize);//列的下标 应该为当前循环次数 加上 已经截断列的次数*每页列数
							if(!StringUtils.isBlank(groupvalues)&&field.getItemid().equalsIgnoreCase(groupvalues)){
								table.addCell(getCell(map,rowList.get(colNum+oldSize),fontTheadChinese,field));
								continue;
							}
							if("A".equalsIgnoreCase(field.getItemtype())){
								String codesetid=field.getCodesetid();
								String content="";
								String value=rowList.get(colNum+oldSize);
								if(value==null|| "".equals(value))
									value=" ";
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
								    
							    if(StringUtils.isBlank(content))
						        	content=value;
								table.addCell(getCell(map,content,fontBodyChinese,field));
							}
							else
								table.addCell(getCell(map,value,fontBodyChinese,field));
							}
							
							else if("N".equalsIgnoreCase(field.getItemtype())){
								
								String value=rowList.get(colNum+oldSize);
								if(("a00z1".equalsIgnoreCase(field.getItemid())|| "a00z3".equalsIgnoreCase(field.getItemid()))&&StringUtils.isBlank(value)){
									table.addCell(new Paragraph("",fontBodyChinese));
									continue;
								}
								if(value==null|| "".equals(value))
									value="0";
								
								String str="0.";
								for(int i=0;i<field.getDecimalwidth();i++)
									str+="0";
								if(str.length()==2)
									str="0";
								
								DecimalFormat df = new DecimalFormat(str);

							    String data=df.format(Double.parseDouble(value));

							    /*PdfPCell cell = new PdfPCell(new Paragraph(data,fontBodyChinese));  
							    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);*/
								table.addCell(getCell(map,data,fontBodyChinese,field));
							}else 
								table.addCell(getCell(map,rowList.get(colNum+oldSize),fontBodyChinese,field));
	
						}
					}
					oldSize += colsize;
					count++;
					if(getColNum>count){//当需要翻页时
						document.add(table);
						document.newPage();//创建新页
						
						widths = widthMap.get(count);
						widthArray = new float[widths.size()];
						for(int i = 0; i < widths.size(); i++) {
							widthArray[i] = widths.get(i);
						}
						table = new PdfPTable(widthArray);
						table.setHeaderRows(1);
						table.setTotalWidth(widthArray);
						table.setLockedWidth(true);
	
					}
					
				}
				
				
				
			}while(groupPageNum!=null&&++groupNum<groupPageNum.size());
			
			document.add(table);
			
			float lead = 20f;
			if(StringUtils.isNotBlank(footLHomeShow)) {
				lastPage = writer.getPageNumber();
				Paragraph footLHome = new Paragraph("", footFont);// 设置标题  
				footLHome.setAlignment(Element.ALIGN_LEFT);// 设置对齐方式  
	            document.add(footLHome); //插入标题
			}
			if(StringUtils.isNotBlank(footMHomeShow)) {
				lastPage = writer.getPageNumber();
				Paragraph footMHome = new Paragraph("", footFont);// 设置标题  
				footMHome.setAlignment(Element.ALIGN_CENTER);// 设置对齐方式  
	            document.add(footMHome); //插入标题
			}
			if(StringUtils.isNotBlank(footRHomeShow)) {
				lastPage = writer.getPageNumber();
				Paragraph footRHome = new Paragraph("", footFont);// 设置标题  
				footRHome.setAlignment(Element.ALIGN_RIGHT);// 设置对齐方式  
	            document.add(footRHome); //插入标题
			}

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			PubFunc.closeIoResource(out);
			if(document!=null&&document.isOpen())
			{
				document.close();
			}
			//PubFunc.closeIoResource(tempFile);
		}
		
		return fileName;
	}
	
	/**
	 * 页面设置
	 * @param pageSet 必须包含 paperType:纸张类型 disTop disBottom disLeft disRight： 上下左右边距 normal：纸张方向0为纵向 1为横向
	 * @return
	 */
	private Rectangle  getPageSize(ReportParseVo pageSet){
		Rectangle pageSize=null;
		String paperType=pageSet.getPagetype();
		float disLeft = Float.parseFloat(StringUtils.isBlank(pageSet.getLeft())?"25":pageSet.getLeft());
		float disRight = Float.parseFloat(StringUtils.isBlank(pageSet.getRight())?"25":pageSet.getRight());
		float width=Float.parseFloat(pageSet.getWidth());//纸张宽
		float height=Float.parseFloat(pageSet.getHeight());//纸张宽
		
		String normal=pageSet.getOrientation();
		
		float mm=2.83f;//一毫米 约为2.83像素
		
		if(("0".equals(normal) && width < height) || ("1".equals(normal) && height < width)){//0为纵向 1为横向
			pageSize=new Rectangle(width*mm, height*mm);
			this.setPageWidth(width*mm-disLeft*mm-disRight*mm);
		}
		else if(("0".equals(normal) && height < width) || ("1".equals(normal) && width < height)){
			pageSize=new Rectangle(height*mm, width*mm);
			this.setPageWidth(height*mm-disLeft*mm-disRight*mm);
		}

		return pageSize;
		
	}
	
	/**
	 * 获取cell的值，因为可能有栏目设置，这里校正对其方式
	 * @return
	 */
	private PdfPCell getCell(HashMap map, String content, Font fontBodyChinese, FieldItem field) {
		PdfPCell cell = new PdfPCell(new Paragraph(content,fontBodyChinese));
		int align = 0;
		if("N".equalsIgnoreCase(field.getItemtype()) || "D".equalsIgnoreCase(field.getItemtype()))
			align = Element.ALIGN_RIGHT;
		else 
			align = Element.ALIGN_LEFT;
		//itext控件修的左中右对应的是0，1，2，栏目设置的是1，2，3，所以减一
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(map==null?align:map.get(field.getItemid().toLowerCase() + "align")==null?align:((Short)map.get(field.getItemid().toLowerCase() + "align")-1));
		return cell;
	}

	/**
	 * 重新组装表格对象
	 * @param Fielditem 列对象
	 * @param map 列宽
	 * @param widthMap 每页宽度
	 * @param dataList 数据
	 * @author ZhangHua
	 * @date 11:38 2018/7/30   
	 * @return
	 */
	private int buildColumnData(ArrayList<FieldItem> Fielditem,HashMap map,HashMap<Integer,ArrayList<Float>> widthMap  ,ArrayList<ArrayList> dataList){
		//一共换了多少次页,页码
		int getColNum = 0;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
		//是否定义唯一性指标 0：没定义
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		//唯一性指标值
		String onlyname = "0".equalsIgnoreCase(uniquenessvalid)?"":sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		//唯一性标识，单位，部门 所在的下标
		int[] columnSeat=new int[]{-1,-1,-1};
		int a0101Seat=-1,otherSeat=-1;
		for(int i = 0,j = 0; i < Fielditem.size(); i++,j++) {
			String itemid = Fielditem.get(i).getItemid();
			if("a0101".equalsIgnoreCase(itemid)){
				a0101Seat=i;
			}else if(StringUtils.isNotBlank(onlyname)&&itemid.equalsIgnoreCase(onlyname)){
				columnSeat[0]=i;
			}else if("E0122".equalsIgnoreCase(itemid)){
				columnSeat[1]=i;
			}else if("B0110".equalsIgnoreCase(itemid)){
				columnSeat[2]=i;
			}
		}
		for (int i : columnSeat) {
			if(i!=-1) {
				otherSeat = i;
				break;
			}
		}
		this.changeTableData(Fielditem,dataList,widthMap,a0101Seat,otherSeat,map);
		getColNum = widthMap.size();
		return getColNum;
	}

	/**
	 * 如果存在需要拆分列头的情况。则在每次横向拆分的前两列添加姓名和单位信息
	 * @param fielditem 列
	 * @param dataList 数据
	 * @param widthMap 列宽(会在此方法中重新拼接)
	 * @param a0101Seat 姓名列下标
	 * @param otherSeat 唯一性指标或单位或部门列下标
	 * @param map
	 * @author ZhangHua
	 * @date 11:40 2018/7/30
	 */
	private void changeTableData(ArrayList<FieldItem> fielditem,ArrayList<ArrayList> dataList,HashMap<Integer,ArrayList<Float>> widthMap,
								 int a0101Seat ,int otherSeat,HashMap map) {

		if (a0101Seat == -1 && otherSeat == -1) {
			return;
		}
		FieldItem otherColumn = new FieldItem();
		FieldItem a0101Column = new FieldItem();
		if (otherSeat != -1) {
			otherColumn = fielditem.get(otherSeat);
		}
		if (a0101Seat != -1) {
			a0101Column = fielditem.get(a0101Seat);
		}
		boolean isHaveA0101 = a0101Seat == -1 ? false : true;
		boolean isHaveOther = otherSeat == -1 ? false : true;
		/***
		 * 找到姓名。单位列的列对象和数据对象 用来插入
		 */
		ArrayList a0101List = new ArrayList();
		ArrayList otherList = new ArrayList();
		for (ArrayList list : dataList) {
			if (isHaveA0101) {
				a0101List.add(list.get(a0101Seat));
			}
			if (isHaveOther) {
				otherList.add(list.get(otherSeat));
			}
		}
		float displayWidth = 0, totalWidth = 0, a0101Width = (map != null && map.containsKey("a0101displaywidth")) ? (Float) map.get("a0101displaywidth") : 80f,
				otherWidth = (map != null && map.containsKey(otherColumn.getItemid().toLowerCase() + "displaywidth")) ? (Float) map.get(otherColumn.getItemid().toLowerCase() + "displaywidth") : 80f;
		//一共换了多少次页,页码
		int getColNum = 0;
		//列宽集合
		ArrayList<Float> widths = new ArrayList<Float>();

		LinkedHashMap<Integer, ArrayList> dataMap = new LinkedHashMap<Integer, ArrayList>();

		int firstSeat = 0;
		boolean isEnd=false;
		boolean a0101NeedAdd = isHaveA0101 ? true : false, otherNeedAdd = isHaveOther ? true : false;
		//循环列头对象 在需要换页的位置插入姓名单位信息
		for (int i = 0; i < fielditem.size(); i++) {
			String itemid = fielditem.get(i).getItemid();
			if (map == null) {
				displayWidth = 80f;
			} else {
				displayWidth = (map == null || map.get(itemid.toLowerCase() + "displaywidth") == null) ? 80f : (Float) map.get(itemid.toLowerCase() + "displaywidth");
			}

			//若该页存在待插入的信息，则不需要额外插入了
			if ("a0101".equalsIgnoreCase(itemid))
				a0101NeedAdd = false;
			if (itemid.equalsIgnoreCase(otherColumn.getItemid()))
				otherNeedAdd = false;

			//防止乱设置，设置的列超长大于纸张宽度
			totalWidth += displayWidth > this.getPageWidth() ? this.getPageWidth() : displayWidth;
			//如果长度超出了，换页
			if (totalWidth > this.getPageWidth()) {
				//判断是否需要插入
				if (a0101NeedAdd && otherNeedAdd) {
					dataMap.put(firstSeat, otherList);
					dataMap.put(firstSeat + 1, a0101List);
					fielditem.add(firstSeat, otherColumn);
					fielditem.add(firstSeat + 1, a0101Column);
					widths.add(0, a0101Width);
					widths.add(1, otherWidth);
				} else if (otherNeedAdd) {
					dataMap.put(firstSeat, otherList);
					fielditem.add(firstSeat, otherColumn);
					widths.add(0, otherWidth);
				} else if (a0101NeedAdd) {
					dataMap.put(firstSeat, a0101List);
					widths.add(0, a0101Width);
					fielditem.add(firstSeat, a0101Column);
				}
				int removeCount = 0;
				if (otherNeedAdd || a0101NeedAdd) {
					float sum = 0f;
					displayWidth=0;
					//若插入之后该页列宽超过了最大宽度，则将i指针向前移动，以在将超过长度的列放在下一页中计算
					for (Float width : widths) {
						sum += width;
					}
					while (sum > this.getPageWidth()) {
						sum -= widths.get(widths.size() - 1);
						widths.remove(widths.size() - 1);
						removeCount++;
					}
					int s=otherNeedAdd && a0101NeedAdd?2:1;
					//i移动的位置
					i=i-(removeCount-s+1);
				}
				a0101NeedAdd = isHaveA0101 ? true : false;
				otherNeedAdd = isHaveOther ? true : false;
				widthMap.put(getColNum, widths);
				//总宽度重新计算
				totalWidth = displayWidth;
				//下次循环重新塞值
				widths = new ArrayList<Float>();
				getColNum++;
			}
			//最后一列塞入
			if (i == (fielditem.size() - 1)) {
				if (a0101NeedAdd && otherNeedAdd) {
					dataMap.put(firstSeat, otherList);
					dataMap.put(firstSeat + 1, a0101List);
					fielditem.add(firstSeat, otherColumn);
					fielditem.add(firstSeat + 1, a0101Column);
					widths.add(0, a0101Width);
					widths.add(1, otherWidth);
				} else if (otherNeedAdd) {
					dataMap.put(firstSeat, otherList);
					fielditem.add(firstSeat, otherColumn);
					widths.add(0, otherWidth);
				} else if (a0101NeedAdd) {
					dataMap.put(firstSeat, a0101List);
					widths.add(0, a0101Width);
					fielditem.add(firstSeat, a0101Column);
				}
				int removeCount = 0;
				if (otherNeedAdd || a0101NeedAdd) {
					a0101NeedAdd = isHaveA0101 ? true : false;
					otherNeedAdd = isHaveOther ? true : false;
					float sum = 0f;
					for (Float width : widths) {
						sum += width;
					}
					while (sum > this.getPageWidth()) {
						sum -= widths.get(widths.size() - 1);
						widths.remove(widths.size() - 1);
						removeCount++;
					}
					int s=otherNeedAdd && a0101NeedAdd?2:1;
					i=i-(removeCount-s+1);
				}
				if(removeCount>0) {
					displayWidth = 0F;
				}else{
					isEnd=true;
				}
				if(displayWidth!=0F&&isEnd) {
					widths.add(displayWidth);
				}
				widthMap.put(getColNum, widths);
				if(i == (fielditem.size() - 1)||isEnd)
					break;
				getColNum++;
				totalWidth=displayWidth;
				widths = new ArrayList<Float>();

				isEnd=true;
			}
			if(displayWidth!=0F) {
				widths.add(displayWidth);
				//获取该页首列位置
				if(widths.size()==1) {
					firstSeat = i;
				}
			}

		}

		/**
		 * 重新拼接数据以适应新列表头
		 */
		Iterator iterator = dataMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, ArrayList> entry = (Map.Entry<Integer, ArrayList>) iterator.next();
			int seat = entry.getKey();
			ArrayList list = entry.getValue();
			for (int i = 0; i < dataList.size(); i++) {
				dataList.get(i).add(seat, list.get(i));
			}
		}
	}
	
	/**
	 * 获取字体
	 * @param fontType 类型
	 * @param size 大小
	 * @param fontBlod 粗体
	 * @param italic 斜体
	 * @param underLine 下划线
	 * @param delLine 删除线
	 * @param color 颜色
	 * @return
	 */
	private Font getFont(String fontType, String size, String fontBlod, String italic, String underLine, String delLine, String color) {
		Font tfChinese = null;
		try {
			//设置标题字体
	        BaseFont basefont = getBaseFont(fontType);
	        tfChinese = new Font(basefont, Integer.parseInt(size), StringUtils.isBlank(fontBlod)?Font.NORMAL:Font.BOLD); 
            if(StringUtils.isNotBlank(italic))//斜体
            	tfChinese.setStyle(Font.ITALIC);
            if(StringUtils.isNotBlank(underLine))//下划线
            	tfChinese.setStyle(Font.UNDERLINE);
            if(StringUtils.isNotBlank(delLine))//删除线
            	tfChinese.setStyle(Font.STRIKETHRU);
            if(StringUtils.isNotBlank(color)) {
	            int[] tColor=new int[3]; 
	            tColor[0]=Integer.parseInt(color.substring(1, 3), 16); 
	            tColor[1]=Integer.parseInt(color.substring(3, 5), 16); 
	            tColor[2]=Integer.parseInt(color.substring(5, 7), 16);
	    		tfChinese.setColor(new Color(tColor[0], tColor[1], tColor[2]));
            }
            
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tfChinese;
	}
	
	private BaseFont getBaseFont(String titleFontType) throws GeneralException {
		BaseFont basefont = null;
		String titleFontName = "STSong-Light";
		String titleEnc = "UniGB-UCS2-H";
		try {
			//设置标题字体
	        if(StringUtils.isNotBlank(titleFontType)) {
	        	titleFontName = FontFamilyType.getFontFamilyTTF(titleFontType);
	        	//titleFontName = titleFontName.replace("/", "\\");
	        	titleEnc = BaseFont.IDENTITY_H;
	        }
	        if(titleFontName==null){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.msg.pleaseCheckPDFFontConfig")));// "请检查联系管理员检查PDF字体文件配置！"
			}
	        basefont = BaseFont.createFont(titleFontName, titleEnc, BaseFont.NOT_EMBEDDED);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}
		return basefont;
	}
	
	private Float pageWidth;
	private int lastPage;//找到最后一页
	public Float getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(Float pageWidth) {
		this.pageWidth = pageWidth;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}
	
}
