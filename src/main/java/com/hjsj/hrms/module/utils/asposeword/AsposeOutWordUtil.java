package com.hjsj.hrms.module.utils.asposeword;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.aspose.words.Border;
import com.aspose.words.BorderCollection;
import com.aspose.words.BorderType;
import com.aspose.words.BreakType;
import com.aspose.words.Cell;
import com.aspose.words.CellMerge;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.HeightRule;
import com.aspose.words.HorizontalAlignment;
import com.aspose.words.LineSpacingRule;
import com.aspose.words.LineStyle;
import com.aspose.words.NumberStyle;
import com.aspose.words.PageVerticalAlignment;
import com.aspose.words.PaperSize;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.PreferredWidth;
import com.aspose.words.ProtectionType;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.RelativeVerticalPosition;
import com.aspose.words.Row;
import com.aspose.words.RowAlignment;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.Style;
import com.aspose.words.StyleType;
import com.aspose.words.Table;
import com.aspose.words.VerticalAlignment;
import com.aspose.words.WrapType;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
/**
 * <p>Title: AsposeOutWordUtil </p>
 * <p>Description:应用aspose导出word工具类 </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2017-4-25 下午2:12:58</p>
 * @author hej
 * @version 1.0
 */
public class AsposeOutWordUtil {
	private int page_id = 0;
	private boolean isoutpageno =false;
	private HashMap pageNoStyle = new HashMap();//页码的样式 默认走第一个页码的样式，位置
	private String outtype="";
	private String tab_id="";
	private int signtype = 0;
	private String dirPath="";//设置文件夹路径
	private String out_file_type="1";//导出格式 1 分页导出 2 连续页导出
	//导出officer格式/wps格式 
	private String officerOrWps = "0";
	public String getOfficerOrWps() {
		return officerOrWps;
	}
	public void setOfficerOrWps(String officerOrWps) {
		this.officerOrWps = officerOrWps;
	}
	public String getDirPath() {
		return dirPath;
	}
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}
	
	public String getOut_file_type() {
		return out_file_type;
	}
	public void setOut_file_type(String out_file_type) {
		this.out_file_type = out_file_type;
	}
	public String getTab_id() {
		return tab_id;
	}
	public void setTab_id(String tab_id) {
		this.tab_id = tab_id;
	}
	public String getOuttype() {
		return outtype;
	}
	public void setOuttype(String outtype) {
		this.outtype = outtype;
	}
	public int getSigntype() {
		return signtype;
	}
	public void setSigntype(int signtype) {
		this.signtype = signtype;
	}
	/**
	 * 输出word 调用方法
	 * 
	 * @param filename 文件名  不带后缀名
	 * @param outputList 整个模版页的所有页的数据集合
	 * outputList参数说明 list中存储每页对应map 每页map中包含rleft, bomtitle,toptitle,context,wh四个key值，每页的 context对应的内容为x行x列对应的所有单元格的数据集合
	 * [
		 {//页面每页对应的数据
			 toptitle={//上标题
				 0=[null, 
				 {
					 index=0, //一行的第几个（列）
					 flag=7, //标题类型
					 gridno=2, //编号 可不填
					 height=42//标题的高度
					 rleft=208, //
					 rtop=61,//
					 width=324, //宽度
					 value=, // 值
					 fonteffect=1,// 字体样式 
					 fontname=宋体, // 字体
					 fontsize=11,// 字体大小
					 pageid=0//所在页
				 }
				 ], 
				 1=[null, {index=1, flag=0, gridno=1, rleft=278, width=154, value=员工录用审批表, fonteffect=2, fontname=宋体, fontsize=16}
				 ]
				 },
			 bomtitle={},//下标题（同上标题）
			 wh={1,500,600,1,10,10,10,10},//横纵向与页面高宽
			    wh 组成 1, //横纵向
			           500, //宽  纸张的宽 单位像素
			           600, //高  纸张的高 单位像素
			           1,//纸张大小 1:A4,2:A3,3:A5,4:B5,5:自定义,6:自定义,7:自定义,8:自定义
			           10,//页面上边距  单位像素
			           10,//页面下边距  单位像素
			           10,//页面右边距  单位像素
			           10//页面左边距   单位像素
			 
			 rleft=59, //页面内容最左侧的rleft
			 rtop_t=50,//内容部分最上面的rtop（内容最小的rtop）
			 rtop_b=881,//内容部分最下面的rtop（内容最大的rtop）
			 context={//页面内容
						 0=[//第几行数据
							 { 
								 width=184.68001, //第一行第一个单元格的宽
								 align=7, //主集指标位置或者子集中指标横向位置
								 realheight=38,//真实高度 
								 fontname=宋体, //字体
								 fonteffect=2, //文字样式
								 subflag=false, //是否是子集
								 isHaveLine=0_1_0_0, //边框组成的字符串
								 title=false, //是否是子集表头
								 height=10.260002, //拆分的每行的高度
								 flag=H, //数据类型标识
								 value=    个人基本信息, //每个指标的内容
								 valign=1, //子集中指标纵向位置
								 fontsize=14, //字体大小
								 nhide=0, //指标是否可打印
								 fieldtype:M, //指标类型
								 inputtype:0,//编辑类型
								 subfiledstate={a0415=2, a0440=2, a0430=2, a0435=2},//子集中指标的权限 subfiledstate是一个map  key是指标代码 value是指标的权限
								 recordlist=[],//子集的数据  分析时需要，输出时不需要
								 endRow=true,//是否是结束行
								 setname=,//属于的指标集  例如A04
								 gridno=1,//指标在某一页的排序号
								 signatureValueList=[{left=500,top=200,value='图片的地址'},{}],//签章的集合 里面是n个即{left=500,top=200,value='图片的地址'}
								 sub_domain=,//子集的具体设置  xml格式
								 pageid=0//指标在那一页
								 }, 
							 null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
							]
					  }
		  }
		]
	 * @return
	 * @throws Exception
	 */
	public  String outPutcontext(String filename,ArrayList outputList) throws Exception {
		String filePath = System.getProperty("java.io.tmpdir")+File.separator+filename+".doc";
		if(StringUtils.isNotEmpty(this.dirPath)) {
			File fileDir=new File(this.dirPath);
			if(!(fileDir.exists()&&fileDir.isDirectory())) {
				fileDir.mkdir();
			}
			filePath = this.dirPath+File.separator+filename+".doc";
		}
	    File tempFile = new File(filePath);
		Document document = new Document();
		String docProtectPwd="";
		try{
			docProtectPwd=SystemConfig.getProperty("docLockPwd");
			if(StringUtils.isNotBlank(docProtectPwd)){
				document.protect(ProtectionType.READ_ONLY,docProtectPwd);
			}
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		document.getFirstSection().getPageSetup().setVerticalAlignment(PageVerticalAlignment.TOP);
		int[] papeSizes = new int[]{PaperSize.A4,PaperSize.A3,PaperSize.A5,PaperSize.B5,PaperSize.CUSTOM,PaperSize.CUSTOM,PaperSize.CUSTOM,PaperSize.CUSTOM};
		DocumentBuilder builder = new AsposeLicenseUtil(document);
		for(int i=0;i<outputList.size();i++){
			HashMap map= (HashMap)outputList.get(i);//每一页的
			HashMap TitletopMap = (HashMap)map.get("toptitle");
			HashMap TitlebomMap = (HashMap)map.get("bomtitle");
			ArrayList TitlemidList = (ArrayList)map.get("midtitle_img");
			HashMap pagemap = (HashMap)map.get("context");
			Object[] wh1 = (Object[]) map.get("wh");
			builder.getCurrentSection().getPageSetup().setPaperSize(papeSizes[(Integer)wh1[3]-1]);
			builder.getCurrentSection().getPageSetup().setOrientation((Integer)wh1[0]);
			if(papeSizes[(Integer)wh1[3]-1]==PaperSize.CUSTOM){
				builder.getCurrentSection().getPageSetup().setPageHeight(ConvertUtil.pixelToPoint((Integer)wh1[1]));
				builder.getCurrentSection().getPageSetup().setPageWidth(ConvertUtil.pixelToPoint((Integer)wh1[2]));
			}
			builder.getCurrentSection().getPageSetup().setTopMargin(ConvertUtil.pixelToPoint((Integer)wh1[4]));
			builder.getCurrentSection().getPageSetup().setBottomMargin(ConvertUtil.pixelToPoint((Integer)wh1[5]));
			builder.getCurrentSection().getPageSetup().setRightMargin(ConvertUtil.pixelToPoint((Integer)wh1[6]));
			builder.getCurrentSection().getPageSetup().setLeftMargin(ConvertUtil.pixelToPoint((Integer)wh1[7]));
			int rleft = (Integer)map.get("rleft");
			int rtop_t = (Integer)map.get("rtop_t");
			int rtop_b = (Integer)map.get("rtop_b");
			if(TitletopMap.size()>0)
				this.outWordTitle(TitletopMap,builder,"t",rleft,rtop_t,wh1,i);//输出上标题
			this.outMidTitleImg(TitlemidList,builder,rleft);//输出在内容部分的标题图片
			Table table = builder.startTable();
			boolean ishaveSubOrMFlag=this.outWord(pagemap,builder,document,table);//输出内容
			builder.endTable();
			if(TitlebomMap.size()>0)
				this.outWordTitle(TitlebomMap,builder,"b",rleft,rtop_b,wh1,i);//输出下标题
			
			//判断下一页纸张方向与当前页是否相同 相同则不分页 不同则分页
			boolean isBreak_page=false;
			if(i<outputList.size()-1) {
				HashMap next_map=(HashMap)outputList.get(i+1);//每一页的
				Object[] wh1_next = (Object[]) next_map.get("wh");
				if(!((Integer) wh1[0]).equals((Integer) wh1_next[0])) {
					isBreak_page=true;
				}
			}
			if(!ishaveSubOrMFlag) {//当前页签内没有子集分页显示  或者模板内只有大文本内容时
				isBreak_page=true;
			}
			if(i!=outputList.size()-1){
				if("1".equals(this.out_file_type)) {
					builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);//导出模板内容不连续输出
				}else {
					if(map.containsKey("breakPage")&&"true".equals((String)map.get("breakPage"))||isBreak_page) {
						builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
					}else {
						builder.insertBreak(BreakType.PARAGRAPH_BREAK);//内容连续输出
					}
				}
			}
			/*Boolean isNeedPageBreak=true;
			String noPageBreakTabId = new String(SystemConfig.getPropertyValue("NoPageBreakTabId").getBytes("ISO-8859-1"), "gb2312");
	        if (StringUtils.isNotBlank(noPageBreakTabId)) {
	        	noPageBreakTabId=","+noPageBreakTabId+",";
	        	if(noPageBreakTabId.contains(","+this.tab_id+",")){
	        		isNeedPageBreak=false;
	        	}
	        }
	        if(isNeedPageBreak){
				if(i!=outputList.size()-1){
					builder.insertBreak(BreakType.PARAGRAPH_BREAK);//
					//builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);//导出模板内容连续输出 
				}
				
	        }*/
		}
		if(this.isoutpageno)
			this.OutWordPageNO(builder,document);
		document.save(filePath);
		return filename = tempFile.getName();
	}
	/**
	 * 输出在内容部分的标题图片
	 * @param titlemidList
	 * @param builder
	 * @param rleft 
	 */
	private void outMidTitleImg(ArrayList titlemidList, DocumentBuilder builder, int rleft) {
		try {
			for(int l=0;l<titlemidList.size();l++){
				HashMap titlemidMap = (HashMap)titlemidList.get(l);
				int left = (Integer)titlemidMap.get("rleft");
				int top = (Integer)titlemidMap.get("rtop");
				String value = (String)titlemidMap.get("value");
				int _width = (Integer)titlemidMap.get("width");
				int _height = (Integer)titlemidMap.get("height");
				if(value!=null&&!"".equals(value)){
					BufferedImage image = ImageIO.read(new File(value));
					Shape shape = builder.insertImage(image);
					shape.setWidth(ConvertUtil.pixelToPoint(_width*1.0F));
					shape.setHeight(ConvertUtil.pixelToPoint(_height*1.0F));
					shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
					shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
					//shape.setVerticalAlignment(VerticalAlignment.CENTER);;
					//shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
					shape.setLeft(ConvertUtil.pixelToPoint(left*1.0F));
					shape.setTop(ConvertUtil.pixelToPoint(top*1.0F));
					shape.setWrapType(WrapType.NONE);
					shape.setBehindText(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 生成页码
	 * @param builder 
	 * @param doc 
	 * @throws Exception 
	 * 
	 */
	private void OutWordPageNO(DocumentBuilder builder, Document doc) throws Exception {
		//计算一下标题的位置,默认设置left 然后用空格将其推过来
		//将标题设置的字体也要带过来
		String fontname = (String)this.pageNoStyle.get("fontname");
		double fontsize = (Double)this.pageNoStyle.get("fontsize");
		String fontstyle = (String)this.pageNoStyle.get("fontstyle");
		int fonteffect = (Integer)this.pageNoStyle.get("fonteffect");
		String nbsp_ = (String)this.pageNoStyle.get("nbsp_");
		String position = (String)this.pageNoStyle.get("position");
		Boolean isNeedPageBreak=true;
		String noPageBreakTabId = new String(SystemConfig.getPropertyValue("NoPageBreakTabId").getBytes("ISO-8859-1"), "gb2312");
        if (StringUtils.isNotBlank(noPageBreakTabId)) {
        	noPageBreakTabId=","+noPageBreakTabId+",";
        	if(noPageBreakTabId.contains(","+this.tab_id+",")){
        		isNeedPageBreak=false;
        	}
        }
        if(isNeedPageBreak){
        	builder.moveToSection(this.page_id);//移动到需要生成页码的起始页
        }else{
        	builder.moveToSection(0);//移动到需要生成页码的起始页
        }
		builder.moveToHeaderFooter("b".equals(position)?HeaderFooterType.FOOTER_PRIMARY:HeaderFooterType.HEADER_PRIMARY);//页码生成到页眉还是页脚
		builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);//页码的align
		//页码中数字的样式 start
		Style style = doc.getStyles().add(StyleType.PARAGRAPH, "pageNoStyle");
		style.getFont().setSize(fontsize);
		style.getFont().setName(fontname);
		
		if(fonteffect==2)
			style.getFont().setBold(true);
		else if(fonteffect==3)
			style.getFont().setItalic(true);
		else if(fonteffect==4) {
			style.getFont().setBold(true);
			style.getFont().setItalic(true);
		}	
		builder.getParagraphFormat().setStyle(style);
		//页码中数字的样式 end
		//The page number will look like " 第 10 页 ".
		builder.insertHtml(nbsp_+"<font face='"+fontname+"' style='"+fontstyle+"'>"+ResourceFactory.getProperty("hmuster.label.d")+" </font>");
		builder.insertField("PAGE", "");
		builder.insertHtml("<font face='"+fontname+"' style='"+fontstyle+"'> "+ResourceFactory.getProperty("hmuster.label.paper")+"</font>");

		Section section = doc.getSections().get(this.page_id);
		section.getPageSetup().setPageStartingNumber(1);
		section.getPageSetup().setRestartPageNumbering(true);
		section.getPageSetup().setPageNumberStyle(NumberStyle.ARABIC);
	}
	/**
	 * 输出word标题部分
	 * @param titleList
	 * @param builder
	 * @param position   t 上标题 b 下标题 
	 * @param rtop 内容的最上和最下位置
	 * @param wh1 
	 * @throws Exception 
	 */
	private void outWordTitle(HashMap titlemap, DocumentBuilder builder, String position, int left, int rtop, Object[] wh1, int index) throws Exception {
		//正文的字体 小四 高度4.23mm
		int rtop_=0;
		int height_=0;
		for (int j=0;j<titlemap.size();j++) {
			ArrayList celllist = (ArrayList)titlemap.get(j);
			StringBuffer b = new StringBuffer();
			int titleNum = 0;
			boolean havePageNo = false;
			for(int k=0;k<celllist.size();k++){
				if(celllist.get(k)==null){
					if(k==0){
						int left_t =(Integer)(((HashMap)celllist.get(k+1)).get("rleft"));
						//一个空格按16px算 暂时这么宽 我也不知道应该多宽
						double a = Math.ceil((left_t-(Integer)wh1[7])*1.0F/16);
						for(double i=0;i<a;i++){
							b.append("&ensp;&ensp;");
						}
					}else{
						if(k<celllist.size()-1){
							int left_f =(Integer)(((HashMap)celllist.get(k-1)).get("rleft"));
							int width_f =(Integer)(((HashMap)celllist.get(k-1)).get("width"));
							int left_s =(Integer)(((HashMap)celllist.get(k+1)).get("rleft"));
							int leftlength =left_s-left_f-width_f>0?left_s-left_f-width_f:0;
							double a = Math.ceil(leftlength*1.0F/16);
							for(double i=0;i<a;i++){
								b.append("&ensp;&ensp;");
							}
						}
					}
					continue;
				}
				HashMap map=(HashMap)celllist.get(k);
				String fontname = (String)map.get("fontname");//字体名称
				int fonteffect = (Integer)map.get("fonteffect");//样式
				double fontsize = this.getRealFontSize((Integer)map.get("fontsize"));//大小
				String style = getFontStyle(fontsize,fonteffect);
				String value = (String)map.get("value");
				int flag = (Integer)map.get("flag");//类型
				int height = (Integer)map.get("height");
				int width = (Integer)map.get("width");
				if(k==celllist.size()-1){
					rtop_ = (Integer)map.get("rtop");
					height_ = (Integer)map.get("height");
				}
				if(flag==5&&!this.isoutpageno){
					String nbsp_ = "";
					this.page_id=index;
					this.isoutpageno = true;
					this.pageNoStyle.put("fontname", fontname);
					this.pageNoStyle.put("fonteffect", fonteffect);
					this.pageNoStyle.put("fontsize", fontsize);
					this.pageNoStyle.put("fontstyle", style);
					this.pageNoStyle.put("position", position);
					int left_no =(Integer)map.get("rleft");
					double a = Math.ceil((left_no-(Integer)wh1[7])*1.0F/16);
					for(double i=0;i<a;i++){
						nbsp_+="&ensp;&ensp;";
					}
					this.pageNoStyle.put("nbsp_", nbsp_);//前面需要加多少个空格
				}
				if(flag==7){//图片
					titleNum++;
					int rtop_img = (Integer) map.get("rtop");
					int rleft_img = (Integer) map.get("rleft");
					BufferedImage image = ImageIO.read(new File(value));
					if(image!=null) {
						Shape shape = builder.insertImage(image);
						shape.setBehindText(true);
						shape.setWidth(ConvertUtil.pixelToPoint(width * 1.0F));
						shape.setHeight(ConvertUtil.pixelToPoint(height * 1.0F));
						shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
						shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
						shape.setLeft(ConvertUtil.pixelToPoint(rleft_img * 1.0F));
						shape.setTop(ConvertUtil.pixelToPoint(rtop_img * 1.0F));
						shape.setWrapType(WrapType.NONE);
					}
				}else{
					if(flag==5)
						havePageNo = true;
					else {
						titleNum++;
						b.append("<font face='"+fontname+"' style='"+style+"'>"+value+"</font>");
					}
				}
			}
			if(titleNum==0&&havePageNo) {}
			else {
				if("b".equals(position)&&j!=0){
					builder.insertHtml("<br>");
				}
				builder.insertHtml(b.toString());
				if("b".equals(position)) {//除了最后一个标题其余标题结束都添加换行符
					if(j!=titlemap.size()-1) {
						builder.insertHtml("<br>");
					}
				}else { 
					if(j!=0) {
						builder.insertHtml("<br>");
					} 
				}
			}
		}
		if("t".equals(position)){
			//builder.insertHtml("<br>");
			int height = rtop-(rtop_+height_)-5;
			height = height<0?0:height;
			builder.insertHtml("<div style='height:"+height+"px;overflow:hidden;'></div>");
		}
	}
	/**
	 * 输出word内容部分
	 * @param pagemap
	 * @param builder
	 * @param document  
	 * @param table 
	 * @throws Exception
	 */
	private boolean outWord(HashMap pagemap, DocumentBuilder builder, Document document, Table table) throws Exception {
		/***
		 * 当前页内是否存在子集 或者
		 * 存在大文本内容 如果有大文本则导出页不连续分页,
		 * (存在当只有大文本的情况时，大文本高度已经按照内容自适应，分页按照之前设置逻辑有子集情况才会取消分页设置，导致大文本页分页，页面显示有空白不好看）
		 */
		boolean ishave_sub_MFlag=false;
		builder.getRowFormat().setAlignment(RowAlignment.CENTER);
		//单元格内容自适应  内容自动适应cell得宽度，过长得文本会自动换行
		if ("0".equals(this.getOfficerOrWps())) {
			builder.getCellFormat().setFitText(true);
		} else {
			builder.getCellFormat().setFitText(false);
		}
		//设置边框的粗细
		builder.getCellFormat().getBorders().setColor(Color.BLACK);
		//自动换行
		builder.getCellFormat().setWrapText(false);
		Boolean isHaveM=false;
		for(int i=0;i<pagemap.size();i++)
		{
			ArrayList cellList=(ArrayList) pagemap.get(i);//每页内容行
			int rowNumCount=0;
			for(int cellNum=0;cellNum<cellList.size();cellNum++){
				if(cellList.get(cellNum)!=null){
					rowNumCount++;
				}
			}
			for(int k=0;k<cellList.size();k++){
				
				if(cellList.get(k)==null){//如果元素是空且是一行的最后一个元素则这行结束。
					if(k==cellList.size()-1){
						if(isHaveM){							
							builder.getRowFormat().setAllowBreakAcrossPages(true);
							isHaveM=false;
						}
						else{
							builder.getRowFormat().setAllowBreakAcrossPages(false);
						}
						try{
							builder.endRow();
						}catch(Exception ex){
							
						}
					}
					continue;
				}
				//如果元素没有空则输出
				HashMap map=(HashMap)cellList.get(k);
				String flag = (String)map.get("flag");
				int align =(Integer)map.get("align");
				String isHaveLine = (String) map.get("isHaveLine");
				String lineWidth="";
				if(map.containsKey("lineWidth")) {//边线宽度 默认为0.25
					lineWidth=(String)map.get("lineWidth");
				}
				String[] linewidth_=lineWidth.split("_");
				for(int j=0;j<4;j++) {
				    //有边线的情况下才修改边线
				    if("1".equals(isHaveLine.split("_")[j])&&"1".equals(linewidth_[j])) {
                        linewidth_[j]="0.25";
                    }
				    //边线是0的时候 边线宽度为0
				    if("0".equals(isHaveLine.split("_")[j])) {
				        linewidth_[j]="0";
				    }
				}
				lineWidth=linewidth_[0]+"_"+linewidth_[1]+"_"+linewidth_[2]+"_"+linewidth_[3];
				
				Float width =(Float)map.get("width");
				Float height =(Float)map.get("height");
				Boolean subflag = (Boolean) map.get("subflag");
				String first =(String)map.get("first");
				int fonteffect = (Integer)map.get("fonteffect");//设置字体效果
				String fontname = (String)map.get("fontname");//设置字体名称
				double fontsize = this.getRealFontSize((Integer)map.get("fontsize"));//设置字体大小
				int realheight = (Integer)map.get("realheight");
				int nhide = (Integer)map.get("nhide");//单元格是否能打印导出
				String fieldtype = (String) map.get("fieldtype");
				int inputtype = (Integer)map.get("inputtype");
				ArrayList signatureValueList = (ArrayList)map.get("signatureValueList");
				if(table.getCount()>0){
					table.setPreferredWidth(PreferredWidth.AUTO);//table宽度不走百分比
					table.setAllowAutoFit(true);
				}
				if("M".equalsIgnoreCase(fieldtype)){
					isHaveM=true;
					ishave_sub_MFlag=true;
				}
				if(subflag){//如果是子集
					ishave_sub_MFlag=true;
					int rows=(Integer) (map.get("subRows")==null?0:map.get("subRows"));
						if(rowNumCount==1){//当前行只有子集
							//table.setPreferredWidth(PreferredWidth.AUTO);//table宽度不走百分比
							if(nhide==0&&rows>0){
								Boolean isNeedWarp=(Boolean) map.get("isNeedWarp");//子集外是否需要嵌套table
								if(!"1".equals(this.out_file_type)) {
									isNeedWarp=false;
								}
								if(isNeedWarp){
									Cell cell=builder.insertCell();
									if (first != null && !"".equals(first)) {
										if ("FIRST".equalsIgnoreCase(first)) {
											cell.getCellFormat().setVerticalMerge(1);
										} else {
											cell.getCellFormat().setVerticalMerge(2);
										}
									} else {
										cell.getCellFormat().setVerticalMerge(0);
									}
									this.rebuildSubTable(cell, map, document, builder,width);
									height =(Float)map.get("height");
									isHaveM = Boolean.valueOf(true);
									builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint((double) height.floatValue()));
									if (k == cellList.size() - 1) {
										if (isHaveM.booleanValue()) {
											builder.getRowFormat().setAllowBreakAcrossPages(true);
											isHaveM = Boolean.valueOf(false);
										} else {
											builder.getRowFormat().setAllowBreakAcrossPages(false);
										}

										builder.endRow();
									}
								}else{
									if(table.getCount()>0){
										builder.endTable();
										builder.insertHtml("<div style='display:none;width:100%;height:1px;padding:0px;margin:0px;'>&nbsp;</div>");
										table=builder.startTable();
									}
									rebuildSubTable(table,map,document,builder,width);//根据子集输出子集表格
									if(table.getCount()>0){
										table.setPreferredWidth(PreferredWidth.AUTO);//table宽度不走百分比
									}
									table.setAlignment(1);
									builder.endTable();
									builder.insertHtml("<div style='display:none;width:100%;height:1px;padding:0px;margin:0px;'>&nbsp;</div>");
									table=builder.startTable();
							  }
							}else{
								//插入子集 子集内容为空处理
								Cell cell=builder.insertCell();
								//不输出子集需要输出外边框。
								Border topBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
								Border botBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
								Border lefBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
								Border rigBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
								String[] lines=isHaveLine.split("_");
								topBorder.setLineStyle("1".equalsIgnoreCase(lines[2])?LineStyle.SINGLE:LineStyle.NONE);
								botBorder.setLineStyle("1".equalsIgnoreCase(lines[1])?LineStyle.SINGLE:LineStyle.NONE);
								lefBorder.setLineStyle("1".equalsIgnoreCase(lines[0])?LineStyle.SINGLE:LineStyle.NONE);
								rigBorder.setLineStyle("1".equalsIgnoreCase(lines[3])?LineStyle.SINGLE:LineStyle.NONE);
								
								topBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[2]));
								botBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[1]));
			                    lefBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[0]));
			                    rigBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[3]));
								
								if("2".equals(this.getOut_file_type())) {//插入子集内容为空时 默认设置子集高度
									builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(70));
								}else {
									builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));
								}
								builder.getCellFormat().setWidth(ConvertUtil.pixelToPoint(width));
								if(k==cellList.size()-1){
									if(isHaveM){							
										builder.getRowFormat().setAllowBreakAcrossPages(true);
										isHaveM=false;
									}
									else{
										builder.getRowFormat().setAllowBreakAcrossPages(false);
									}
									builder.endRow();//bug 38535 并排子集没有结束行导致导出显示错乱
								}
							}
						}else{
							Cell cell=builder.insertCell();
							if(first!=null&&!"".equals(first)){
								if("FIRST".equalsIgnoreCase(first)){
									cell.getCellFormat().setVerticalMerge(CellMerge.FIRST);
								}else
									cell.getCellFormat().setVerticalMerge(CellMerge.PREVIOUS);
							}else{
								cell.getCellFormat().setVerticalMerge(CellMerge.NONE);
							}
							if(nhide==0&&rows>0){
								map.put("not_onlySub", false);//当前行子集未横跨一行处理
								rebuildSubTable(cell,map,document,builder,width);//根据子集输出子集表格
								isHaveM=true;
								builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));
								if(k==cellList.size()-1){
									if(isHaveM){							
										builder.getRowFormat().setAllowBreakAcrossPages(true);
										isHaveM=false;
									}
									else{
										builder.getRowFormat().setAllowBreakAcrossPages(false);
									}
									builder.endRow();//bug 38535 并排子集没有结束行导致导出显示错乱
								}
							}
							else{
								//不输出子集需要输出外边框。
								Border topBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
								Border botBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
								Border lefBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
								Border rigBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
								String[] lines=isHaveLine.split("_");
								topBorder.setLineStyle("1".equalsIgnoreCase(lines[2])?LineStyle.SINGLE:LineStyle.NONE);
								botBorder.setLineStyle("1".equalsIgnoreCase(lines[1])?LineStyle.SINGLE:LineStyle.NONE);
								lefBorder.setLineStyle("1".equalsIgnoreCase(lines[0])?LineStyle.SINGLE:LineStyle.NONE);
								rigBorder.setLineStyle("1".equalsIgnoreCase(lines[3])?LineStyle.SINGLE:LineStyle.NONE);
								
								topBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[2]));
								botBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[1]));
								lefBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[0]));
								rigBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[3]));
								
			                    
								builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));
								builder.getCellFormat().setWidth(ConvertUtil.pixelToPoint(width));
								if(k==cellList.size()-1){
									if(isHaveM){							
										builder.getRowFormat().setAllowBreakAcrossPages(true);
										isHaveM=false;
									}
									else{
										builder.getRowFormat().setAllowBreakAcrossPages(false);
									}
									builder.endRow();//bug 38535 并排子集没有结束行导致导出显示错乱
								}
							}
						}
				}else{//处理非子集
					Cell cell=builder.insertCell();
					builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));
					if("P".equalsIgnoreCase(flag)) {//图片所占行高度设置固定值，不根据内容扩展，防止图片没有铺满单元格
						if(map.containsKey("isExpand")&&"false".equalsIgnoreCase((String)map.get("isExpand"))) {
							builder.getRowFormat().setHeightRule(HeightRule.EXACTLY); //设置word完全按照设置高度导出，超出内容会被遮盖
						}
					}
					//不连续分页时，插入的是大文本且独占一行时 默认最小高度不按照设置走,并且大文本高度大于设置的默认高度
					if(!"1".equals(this.out_file_type)&&"M".equalsIgnoreCase(fieldtype)&&rowNumCount==1&&height>200) {
						builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(200));
					}else {
						builder.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));
					}
					builder.getCellFormat().setWidth(ConvertUtil.pixelToPoint(width));
					builder.getCellFormat().setLeftPadding(1);
					builder.getCellFormat().setRightPadding(1);
					builder.getCellFormat().setBottomPadding(0);
					builder.getCellFormat().setTopPadding(0);
					String value =((String)map.get("value")==null||"S".equalsIgnoreCase(flag)||"P".equalsIgnoreCase(flag))?"":(String)map.get("value");
					Border topBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
					Border botBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
					Border lefBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
					Border rigBorder = builder.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
					topBorder.setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
					topBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[2]));
					
					botBorder.setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
					botBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[1]));
					
					lefBorder.setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
					lefBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[0]));
					
					rigBorder.setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
					rigBorder.setLineWidth(Double.parseDouble(lineWidth.split("_")[3]));
					
					if(align!=-1){
						setCellAlign(align,builder);
					}
					if("M".equalsIgnoreCase(fieldtype)&&inputtype==1){
						fontsize+=1;
					}
					String style = getFontStyle(fontsize,fonteffect);
					if(fonteffect==2||fonteffect==4)
						builder.getFont().setBold(true);
					else
						builder.getFont().setBold(false);
					if(fonteffect==3||fonteffect==4)
						builder.getFont().setItalic(true);
					else
						builder.getFont().setItalic(false);
					builder.getFont().setSize(fontsize);
					builder.getFont().setName(fontname);
					
					if(first!=null&&!"".equals(first)){
						if("FIRST".equalsIgnoreCase(first)){
							builder.getCellFormat().setVerticalMerge(CellMerge.FIRST);
							if((!"P".equalsIgnoreCase(flag)||!"S".equalsIgnoreCase(flag))&&nhide==0){
								if("M".equalsIgnoreCase(fieldtype)&&inputtype==1){
									value=value.replace("&mdash;", "－").replace("&ldquo;", "“").replace("&rdquo;", "”").replace("—", "－").replace("-","－");
									  String pattern = "(<[\\s\\S]+?>)";
								      // 创建 Pattern 对象
								      Pattern r = Pattern.compile(pattern);
								      // 现在创建 matcher 对象
								      Matcher m = r.matcher(value);
								      while(m.find()){
								    	  int groupCount = m.groupCount();
									      for(int num=0;num<groupCount;num++){
									    	  value=value.replace(m.group(num), m.group(num).replace("&nbsp;", " "));
									      }
								      }
								      if("1".equals(this.outtype))
								    	  value= value.replace("&nbsp;&nbsp;", "&nbsp;");
									builder.insertHtml("<font face='"+fontname+"' style='"+style+"'>"+value+"</font>");
									builder.getCellFormat().getBorders().getTop().setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getBottom().setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getLeft().setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getRight().setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
								}else if("M".equalsIgnoreCase(fieldtype)&&inputtype==0){
								    //干部任免 简历按备注型特殊处理
								    if(map.containsKey("special_M")&&"true".equals((String)map.get("special_M"))) {
	                                    double fontIndent=getTextFirstLineIndent(fontsize);
	                                    String[] arry=value.split("\r\n");
	                                    Paragraph para=null;
	                                    Run run=null;
	                                    Font font=null;
	                                    for (int j = 0; j < arry.length; j++) {
	                                        if(j==0) {
	                                            cell.removeAllChildren();
	                                        }
	                                        para=new Paragraph(document);
	                                        para.getParagraphFormat().setLeftIndent(fontIndent+9.2);
	                                        para.getParagraphFormat().setRightIndent(5);
	                                        para.getParagraphFormat().setFirstLineIndent(-fontIndent);
	                                        para.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
	                                        para.getParagraphFormat().setKeepTogether(false);
	                                        para.getParagraphFormat().setLineSpacingRule(LineSpacingRule.AT_LEAST);
	                                        para.getParagraphFormat().setLineSpacing(1);
	                                        para.getParagraphFormat().setSuppressAutoHyphens(true);
	                                        para.getParagraphFormat().setSuppressLineNumbers(false);
	                                        para.getParagraphFormat().setPageBreakBefore(false);
	                                        run = new Run(document,arry[j].replace("&nbsp;&nbsp;","　"));
	                                        font = run.getFont();
	                                        font.setSize(fontsize);
	                                        font.setName(fontname);
	                                        para.appendChild(run);
	                                        cell.appendChild(para);
	                                    }
	                                
								    }else {
								        value = value.replace("\n","<br/>").replace(" ", "&nbsp;").replace("　","&nbsp;&nbsp;").replace("—", "－").replace("-", "－");
								        if("1".equals(this.outtype)) {//word
								            value = value.replace("&nbsp;&nbsp;","&nbsp;");
								        }
								        builder.insertHtml("<font face='"+fontname+"' style='"+style+"'>"+value+"</font>");
								    }
									builder.getCellFormat().getBorders().getTop().setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getBottom().setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getLeft().setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
									builder.getCellFormat().getBorders().getRight().setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
								}else if("V".equalsIgnoreCase(flag)) {
									if("0".equals(this.outtype)) {
										String html = "<font face='"+fontname+"' style='"+style+"'>"+value.replace("\n","<br/>").replace(" ", "&nbsp;").replace("　","&nbsp;&nbsp;").replace("—", "－").replace("-", "－")+"</font>";
										builder.insertHtml(html);
									}else
										builder.write(value);
								}else
									builder.write(value);
							}
						}else
							builder.getCellFormat().setVerticalMerge(CellMerge.PREVIOUS);
					}else{
						builder.getCellFormat().setVerticalMerge(CellMerge.NONE);
						if((!"P".equalsIgnoreCase(flag)||!"S".equalsIgnoreCase(flag))&&nhide==0){
							if("M".equalsIgnoreCase(fieldtype)&&inputtype==1){
								  value=value.replace("&mdash;", "－").replace("&ldquo;", "“").replace("&rdquo;", "”").replace("—", "－").replace("-","－");
								  String pattern = "(<[\\s\\S]+?>)";
							      // 创建 Pattern 对象
							      Pattern r = Pattern.compile(pattern);
							      // 现在创建 matcher 对象
							      Matcher m = r.matcher(value);
							      while(m.find()){
							    	  int groupCount = m.groupCount();
								      for(int num=0;num<groupCount;num++){
								    	  value=value.replace(m.group(num), m.group(num).replace("&nbsp;", " ").replace("－", "-"));
								      }
							      }
							      if("1".equals(this.outtype)) {
							    	  value= value.replace("&nbsp;&nbsp;", "&nbsp;");
							      }
								builder.insertHtml("<font face='"+fontname+"' style='"+style+"'>"+value+"</font>");
								builder.getCellFormat().getBorders().getTop().setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getBottom().setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getLeft().setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getRight().setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
								//builder.insertHtml("<font face='"+fontname+"' style='"+style+"'>"+value.replace("&mdash;", "－").replace(" ", "&nbsp;").replace("　","&nbsp;&nbsp;").replace("&ldquo;","\"").replace("—", "－").replace("-","－")+"</font>");
							}else if("M".equalsIgnoreCase(fieldtype)&&inputtype==0){
							  //干部任免 简历按备注型特殊处理
                                if(map.containsKey("special_M")&&"true".equals((String)map.get("special_M"))) {
                                    double fontIndent=getTextFirstLineIndent(fontsize);
                                    String[] arry=value.split("\r\n");
                                    Paragraph para=null;
                                    Run run=null;
                                    Font font=null;
                                    for (int j = 0; j < arry.length; j++) {
                                        if(j==0) {
                                            cell.removeAllChildren();
                                        }
                                        para=new Paragraph(document);
                                        para.getParagraphFormat().setLeftIndent(fontIndent+9.2);
                                        para.getParagraphFormat().setRightIndent(5);
                                        para.getParagraphFormat().setFirstLineIndent(-fontIndent);
                                        para.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
                                        para.getParagraphFormat().setKeepTogether(false);
                                        para.getParagraphFormat().setLineSpacingRule(LineSpacingRule.AT_LEAST);
                                        para.getParagraphFormat().setLineSpacing(1);
                                        para.getParagraphFormat().setSuppressAutoHyphens(true);
                                        para.getParagraphFormat().setSuppressLineNumbers(false);
                                        para.getParagraphFormat().setPageBreakBefore(false);
                                        run = new Run(document,arry[j].replace("&nbsp;&nbsp;","　"));
                                        font = run.getFont();
                                        font.setSize(fontsize);
                                        font.setName(fontname);
                                        para.appendChild(run);
                                        cell.appendChild(para);
                                        
                                    }
                                }else {
                                    value = value.replace("\n","<br/>").replace(" ", "&nbsp;").replace("　","&nbsp;&nbsp;").replace("—", "－").replace("-", "－");
                                    if("1".equals(this.outtype)) {//word
                                        value = value.replace("&nbsp;&nbsp;","&nbsp;");
                                    }
                                    builder.insertHtml("<font face='"+fontname+"' style='"+style+"'>"+value+"</font>");
                                }
								builder.getCellFormat().getBorders().getTop().setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getBottom().setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getLeft().setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
								builder.getCellFormat().getBorders().getRight().setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
							}else if("V".equalsIgnoreCase(flag)) {
								if("0".equals(this.outtype)) {
									String html = "<font face='"+fontname+"' style='"+style+"'>"+value.replace("\n","<br/>").replace(" ", "&nbsp;").replace("　","&nbsp;&nbsp;").replace("—", "－").replace("-", "－")+"</font>";
									builder.insertHtml(html);
								}else
									builder.write(value);
							}
							else
								builder.write(value);
						}
					}
					if(("P".equalsIgnoreCase(flag)||"S".equalsIgnoreCase(flag))&&!"PREVIOUS".equalsIgnoreCase((String)map.get("first"))){
						if("S".equalsIgnoreCase(flag)&&nhide==0){
							if(signatureValueList!=null){
								for(int l=0;l<signatureValueList.size();l++){
									HashMap signerMap = (HashMap)signatureValueList.get(l);
									int left = (Integer)signerMap.get("left");
									int top = (Integer)signerMap.get("top");
									String value_s = (String)signerMap.get("value");
									float _width = (Float)signerMap.get("width");
									float _height = (Float)signerMap.get("height");
									if(value_s!=null&&!"".equals(value_s)){
										//插入浮动图片。
										BufferedImage image = ImageIO.read(new File(value_s));
										Shape shape = builder.insertImage(image);
										shape.setWidth(ConvertUtil.pixelToPoint(_width));
										shape.setHeight(ConvertUtil.pixelToPoint(_height));
										shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
										shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
										shape.setLeft(this.signtype==2?ConvertUtil.pixelToPoint(left*1.0F):left);
										shape.setTop(this.signtype==2?ConvertUtil.pixelToPoint(top*1.0F):top);
										shape.setWrapType(WrapType.NONE);
									}
								}
							}
						}else{
							String isTrans=(String)map.get("isTrans");
							if("true".equalsIgnoreCase(isTrans)){//如果是单元格中插入标题图片，拿图片的高度和宽度
								width=(Float) map.get("photoWidth");
								realheight=(Integer) map.get("photoHeight");
							}
							Shape shape = new Shape(document, ShapeType.IMAGE);
							shape.setWidth(ConvertUtil.pixelToPoint(width));
							shape.setHeight(ConvertUtil.pixelToPoint(realheight));
							shape.getImageData().setImage((String)map.get("value"));
							shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
							shape.setVerticalAlignment(VerticalAlignment.CENTER);
							shape.setWrapType(WrapType.INLINE);//图片嵌入型
							if(nhide==0)
								builder.insertNode(shape);
						}
					}
					if(k==cellList.size()-1){
						if(isHaveM){							
							builder.getRowFormat().setAllowBreakAcrossPages(true);
							isHaveM=false;
						}
						else{
							builder.getRowFormat().setAllowBreakAcrossPages(false);
						}
						builder.endRow();
					}
					if(rowNumCount!=1){
						table.setAlignment(1);
					}
				}
			}
		}
		return ishave_sub_MFlag;
	}
	/**
	 * 重组子集显示的表格
	 * @param cell 放置子集的单元格
	 * @param map  子集数据信息
	 * @param document 
	 * @param builder
	 * @throws Exception
	 */
	private void rebuildSubTable(Cell cell,HashMap map,Document document, DocumentBuilder builder,Float rowWidth) throws Exception {
		int rows=(Integer) (map.get("subRows")==null?0:map.get("subRows"));//获取子集行数
		int cols=(Integer) (map.get("subCols")==null?0:map.get("subCols"));//获取子集列数
		Float mainCellWidth=(Float)map.get("width");//获取子集整体宽度
		ArrayList newRowList=(ArrayList) map.get("newRowList");//子集数据集合
		int real_recordSize=Integer.parseInt((String)map.get("realRecordSize"));//实际子集条数
		if(map.containsKey("recordTitle")) {
			real_recordSize+=1;
		}
		Table table=new Table(document);//表格table
		table.setBorders(LineStyle.NONE, 0, Color.black);//表格设置无边框
		Boolean isHaveVBorder=false;//子集是否显示横线
		Boolean isHaveHBorder=false;//子集是否显示竖线
		String cellIsHaveLine="0_0_0_0";
		float totalRowHeight=0;
		for(int row=0;row<rows;row++){
			if("2".equals(this.getOut_file_type())&&real_recordSize<=2&&!map.containsKey("not_onlySub")) {//模板设置连续页导出 判断子集条数是否
				map.put("height", totalRowHeight);
			}
			Row subRow=new Row(document);//行
			ArrayList rowList=(ArrayList) newRowList.get(row);
			if(cols==0){//子集只有附件，word输出，列数为0导致报错，这里给row里插入空cell
				Cell subCell = builder.insertCell();
				subCell.getCellFormat().setVerticalMerge(0);
				subCell.getCellFormat().setHorizontalMerge(0);
				subCell.getCellFormat().setWidth(rowWidth);
				subCell.getCellFormat().setLeftPadding(1.0D);
				subCell.getCellFormat().setRightPadding(1.0D);
				subCell.getCellFormat().setBottomPadding(0.0D);
				subCell.getCellFormat().setTopPadding(0.0D);
				Border topBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
				Border botBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
				Border lefBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
				Border rigBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
				topBorder.setLineStyle(0);
				botBorder.setLineStyle(0);
				lefBorder.setLineStyle(1);
				rigBorder.setLineStyle(1);
				if (row == 0) {
					topBorder.setLineStyle(1);
				}
				if (row == rows - 1) {
					botBorder.setLineStyle(1);
				}
				subRow.appendChild(subCell);
			}else{
				for(int col=0;col<cols;col++){
					HashMap subMap=(HashMap) rowList.get(col);
					Float height =(Float)subMap.get("height");
					if(col==0) {
						totalRowHeight+=height;
					}
					Float width=(Float) subMap.get("width");
					Integer align=(Integer) subMap.get("align");
					Integer valign=(Integer) subMap.get("valign");//垂直
					Integer nhide=(Integer) subMap.get("nhide");
					cellIsHaveLine=(String) subMap.get("isHaveLine");
					String value=(String) subMap.get("value");
					Boolean istitle = (Boolean) subMap.get("title");
					int fonteffect = (Integer)subMap.get("fonteffect");//设置字体效果
					String fontname = (String)subMap.get("fontname");//设置字体名称
					double fontsize = this.getRealFontSize((Integer)subMap.get("fontsize"));//设置字体大小
					Cell subCell=new Cell(document);//列
					subCell.getCellFormat().getBorders().clearFormatting();
					subCell.getCellFormat().setVerticalMerge(CellMerge.NONE);
					subCell.getCellFormat().setHorizontalMerge(CellMerge.NONE);
					subCell.getCellFormat().setWidth(ConvertUtil.pixelToPoint(width));
					subCell.getCellFormat().setLeftPadding(1);
					subCell.getCellFormat().setRightPadding(1);
					subCell.getCellFormat().setBottomPadding(0);
					subCell.getCellFormat().setTopPadding(0);
					Border topBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
					Border botBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
					Border lefBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
					Border rigBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
					isHaveVBorder="1".equals(cellIsHaveLine.split("_")[1]);
					isHaveHBorder="1".equals(cellIsHaveLine.split("_")[0]);
					if(istitle){//如果是标题行边框全有
						topBorder.setLineStyle(LineStyle.SINGLE);
						botBorder.setLineStyle(LineStyle.SINGLE);
						if(subMap.containsKey("v1")&&(Boolean)subMap.get("v1")) {//显示竖线处理边线
							if(col!=0) {
								lefBorder.setLineStyle(LineStyle.SINGLE);
							}
							if(col!=cols-1) {
								rigBorder.setLineStyle(LineStyle.SINGLE);
							}
						}else {//不显示竖线
							lefBorder.setLineStyle(LineStyle.NONE);
							rigBorder.setLineStyle(LineStyle.NONE);
						}
						
					}else{//如果是数据行边框按照设置显示
						/*topBorder.setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
						botBorder.setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
						lefBorder.setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
						rigBorder.setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);*/
						if(!"1".equals(cellIsHaveLine.split("_")[2])){
							subCell.getCellFormat().setTopPadding(0.25);
						}
						if(!"1".equals(cellIsHaveLine.split("_")[1])){
							subCell.getCellFormat().setBottomPadding(0.25);
						}
					}
					/*if(col==0){//第一列去掉左边框，否则会与子集的单元格重线
						lefBorder.setLineStyle(LineStyle.NONE);
					}
					if(col==cols-1){//最后一列去掉右边框，否则会与子集的单元格重线
						rigBorder.setLineStyle(LineStyle.NONE);
					}
					if(row==0){//第一行去掉上边框，否则会与子集的单元格重线
						topBorder.setLineStyle(LineStyle.NONE);
					}*/
					if(StringUtils.isBlank(value)){
						value="";
						
					}else{
						value=value.replace("—", "－").replace("-","－");
					}
					Run text=new Run(document,value);
					if(fonteffect==2||fonteffect==4)
						text.getFont().setBold(true);
					else
						text.getFont().setBold(false);
					if(fonteffect==3||fonteffect==4)
						text.getFont().setItalic(true);
					else
						text.getFont().setItalic(false);
					text.getFont().setSize(fontsize);
					text.getFont().setName(fontname);
					Paragraph paragraph=new Paragraph(document);
					//paragraph.getParagraphFormat().setAddSpaceBetweenFarEastAndAlpha(true);
					//paragraph.getParagraphFormat().setAddSpaceBetweenFarEastAndDigit(true);
					paragraph.appendChild(text);
					if(align!=-1){
						if(nhide==0){
							setCellAlign(getAlign(align,valign),subCell,paragraph);//设置文本对齐方式
						}
						else
							setCellAlign(align,subCell,paragraph);//设置文本对齐方式
					}
					subCell.appendChild(paragraph);
					subRow.appendChild(subCell);
					subRow.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));//设置行高
					subRow.getRowFormat().setAllowBreakAcrossPages(false);
				}
			}
			table.appendChild(subRow);
			table.setLeftPadding(0);
			table.setRightPadding(0);
			table.setTopPadding(0);
			table.setBottomPadding(0);
		}
		//设置放置子集的单元格的边框、宽度、对齐方式等
		String tableIsHaveLine=(String) map.get("isHaveLine");
		String lineWidth_cell="1_1_1_1";
		if(map.containsKey("lineWidth")) {
			lineWidth_cell=(String)map.get("lineWidth");
		}
		cell.getCellFormat().getBorders().getByBorderType(BorderType.TOP).setLineStyle("1".equals(tableIsHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
		cell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM).setLineStyle("1".equals(tableIsHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
		cell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT).setLineStyle("1".equals(tableIsHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
		cell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT).setLineStyle("1".equals(tableIsHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);
		if("1".equals(tableIsHaveLine.split("_")[2])){
			cell.getCellFormat().getBorders().getByBorderType(BorderType.TOP).setLineWidth(Double.parseDouble(lineWidth_cell.split("_")[2].replace("1", "0.25")));
		}
		if("1".equals(tableIsHaveLine.split("_")[1])) {
			cell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM).setLineWidth(Double.parseDouble(lineWidth_cell.split("_")[1].replace("1", "0.25")));
		}
		if("1".equals(tableIsHaveLine.split("_")[0])) {
			cell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT).setLineWidth(Double.parseDouble(lineWidth_cell.split("_")[0].replace("1", "0.25")));
		}
		if("1".equals(tableIsHaveLine.split("_")[3])) {
			cell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT).setLineWidth(Double.parseDouble(lineWidth_cell.split("_")[3].replace("1", "0.25")));
		}
		
		cell.getCellFormat().setBottomPadding(0);
		cell.getCellFormat().setLeftPadding(0);
		cell.getCellFormat().setRightPadding(0);
		cell.getCellFormat().setTopPadding(0);
		if(!isHaveVBorder&&isHaveHBorder){//如果画了竖线没有设置横线，就让table显示底边线，否则显示不正常。
			BorderCollection boderStyle = cell.getCellFormat().getBorders();
			table.setBorder(BorderType.BOTTOM, LineStyle.SINGLE, boderStyle.getLineWidth(), boderStyle.getColor(), true);
		}
		if((Float)map.get("height")<totalRowHeight&&!"1".equals(out_file_type)) {
			isHaveVBorder=false;
		}
		double lineWidth=cell.getCellFormat().getBorders().getLineWidth();
		table.setBorder(BorderType.LEFT, LineStyle.NONE, 0, Color.BLACK, false);
		table.setBorder(BorderType.RIGHT, LineStyle.NONE, 0, Color.BLACK, false);
		table.setBorder(BorderType.TOP, LineStyle.NONE, 0, Color.BLACK, true);//子集上下底部边框根据设置子集是否显示横线控制是否显示边线
		table.setBorder(BorderType.BOTTOM,isHaveVBorder?LineStyle.SINGLE:LineStyle.NONE,isHaveVBorder?lineWidth:0, Color.BLACK, true);
		table.setBorder(BorderType.VERTICAL,"1".equals(cellIsHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE, "1".equals(cellIsHaveLine.split("_")[0])?lineWidth:0.0, Color.BLACK, false);
		table.setBorder(BorderType.HORIZONTAL,"1".equals(cellIsHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE, "1".equals(cellIsHaveLine.split("_")[2])?lineWidth:0.0, Color.BLACK, false);
		cell.getCellFormat().setWidth(ConvertUtil.pixelToPoint(mainCellWidth));
		cell.prependChild(table);
		cell.getCellFormat().setVerticalAlignment(VerticalAlignment.DEFAULT);
		
	}
	
	/**
	 * 重组子集显示的表格
	 * @param table 
	 * @param map  子集数据信息
	 * @param document 
	 * @param builder
	 * @param width外框宽度
	 * @throws Exception
	 */
	private void rebuildSubTable(Table table,HashMap map,Document document, DocumentBuilder builder,Float rowWidth) throws Exception {
		int rows=(Integer) (map.get("subRows")==null?0:map.get("subRows"));//获取子集行数
		int cols=(Integer) (map.get("subCols")==null?0:map.get("subCols"));//获取子集列数
		Float mainCellWidth=(Float)map.get("width");//获取子集整体宽度
		ArrayList newRowList=(ArrayList) map.get("newRowList");//子集数据集合
		table.setBorders(LineStyle.NONE, 0, Color.black);//表格设置无边框
		
		int real_recordSize=Integer.parseInt((String)map.get("realRecordSize"));//实际子集条数
		//子集无数据或者只有1条数据默认添加空行
		if("2".equals(this.getOut_file_type())&&(real_recordSize==0||real_recordSize==1)) {
			int emp_row=2;//空两行数据
			if(real_recordSize==1) {//有一条数据添加一个空行
				emp_row=1;
			}
			rows+=emp_row;
			
			if(newRowList.size()>0) {
				ArrayList<HashMap> rowList=(ArrayList) newRowList.get(0);
				ArrayList copyList=new ArrayList();
				for(HashMap key_map:rowList) {
					HashMap copy_map=(HashMap)key_map.clone();
					copy_map.put("value", "");
					copyList.add(copy_map);
				}
				for(int i=0;i<emp_row;i++) {
					newRowList.add(copyList);
				}
				
			}
		}
		
		if(map.containsKey("recordTitle")) {
			real_recordSize+=1;
		}
		
		String cellIsHaveLine="0_0_0_0";
		for(int row=0;row<rows;row++){
			if("2".equals(this.getOut_file_type())) {
				//连续不分页时，子集内容少于两行，补空行
				if(real_recordSize<=2&&row==3) {
					break;
				}
			}
			Row subRow=new Row(document);//行
			ArrayList rowList=(ArrayList) newRowList.get(row);
			if(cols==0){//子集只有附件，word输出，列数为0导致报错，这里给row里插入空cell
				Cell cell = builder.insertCell();
				cell.getCellFormat().setVerticalMerge(0);
				cell.getCellFormat().setHorizontalMerge(0);
				cell.getCellFormat().setWidth(rowWidth);
				cell.getCellFormat().setLeftPadding(1.0D);
				cell.getCellFormat().setRightPadding(1.0D);
				cell.getCellFormat().setBottomPadding(0.0D);
				cell.getCellFormat().setTopPadding(0.0D);
				Border topBorder = cell.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
				Border botBorder = cell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
				Border lefBorder = cell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
				Border rigBorder = cell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
				topBorder.setLineStyle(0);
				botBorder.setLineStyle(0);
				lefBorder.setLineStyle(0);
				rigBorder.setLineStyle(0);
				lefBorder.setLineStyle(1);
				rigBorder.setLineStyle(1);
				if (row == 0) {
					topBorder.setLineStyle(1);
				}
				if (row == rows - 1) {
					botBorder.setLineStyle(1);
				}

				subRow.appendChild(cell);
			}else{
				for(int col=0;col<cols;col++){
					HashMap subMap=(HashMap) rowList.get(col);
					Float height =(Float)subMap.get("height");
					Float width=(Float) subMap.get("width");
					Integer align=(Integer) subMap.get("align");
					Integer valign=(Integer) subMap.get("valign");//垂直
					Integer nhide=(Integer) subMap.get("nhide");
					cellIsHaveLine=(String) subMap.get("isHaveLine");
					String value=(String) subMap.get("value");
					Boolean istitle = (Boolean) subMap.get("title");
					int fonteffect = (Integer)subMap.get("fonteffect");//设置字体效果
					String fontname = (String)subMap.get("fontname");//设置字体名称
					double fontsize = this.getRealFontSize((Integer)subMap.get("fontsize"));//设置字体大小
					Cell subCell=builder.insertCell();
					subCell.getCellFormat().getBorders().clearFormatting();
					subCell.getCellFormat().setVerticalMerge(CellMerge.NONE);
					subCell.getCellFormat().setHorizontalMerge(CellMerge.NONE);
					subCell.getCellFormat().setWidth(ConvertUtil.pixelToPoint(width));
					subCell.getCellFormat().setLeftPadding(1);
					subCell.getCellFormat().setRightPadding(1);
					subCell.getCellFormat().setBottomPadding(0);
					subCell.getCellFormat().setTopPadding(0);
					Border topBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.TOP);
					Border botBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.BOTTOM);
					Border lefBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.LEFT);
					Border rigBorder = subCell.getCellFormat().getBorders().getByBorderType(BorderType.RIGHT);
					if(istitle){//如果是标题行边框全有
						boolean v1=(Boolean)subMap.get("v1");
						topBorder.setLineStyle(LineStyle.SINGLE);
						botBorder.setLineStyle(LineStyle.SINGLE);
						if(v1) {//50736 子集设置不显示横线与竖线 显示标题隐藏左右边线
							lefBorder.setLineStyle(LineStyle.SINGLE);
							rigBorder.setLineStyle(LineStyle.SINGLE);
						}
					}else{//如果是数据行边框按照设置显示
						/*topBorder.setLineStyle("1".equals(isHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE);
						botBorder.setLineStyle("1".equals(isHaveLine.split("_")[1])?LineStyle.SINGLE:LineStyle.NONE);
						lefBorder.setLineStyle("1".equals(isHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE);
						rigBorder.setLineStyle("1".equals(isHaveLine.split("_")[3])?LineStyle.SINGLE:LineStyle.NONE);*/
						if(!"1".equals(cellIsHaveLine.split("_")[2])){
							subCell.getCellFormat().setTopPadding(2);
						}
						if(!"1".equals(cellIsHaveLine.split("_")[1])){
							subCell.getCellFormat().setBottomPadding(2);
						}
					}
					/*if(col==0){//第一列加上左边框，否则子集无横线竖线会没有边框
						lefBorder.setLineStyle(LineStyle.SINGLE);
					}
					if(col==cols-1){//最后一列加上右边框，否则子集无横线竖线会没有边框
						rigBorder.setLineStyle(LineStyle.SINGLE);
					}
					if(row==0){//第一行加上上边框，否则子集无横线竖线会没有边框
						topBorder.setLineStyle(LineStyle.SINGLE);
					}
					if(row==rows-1){//最后一行加上底边框，否则子集无横线竖线会没有边框
						botBorder.setLineStyle(LineStyle.SINGLE);
					}*/
					if(StringUtils.isBlank(value)){
						value="";
					}else{
						value=value.replace("—", "－").replace("-","－");
					}
					Run text=new Run(document,value);
					if(fonteffect==2||fonteffect==4)
						text.getFont().setBold(true);
					else
						text.getFont().setBold(false);
					if(fonteffect==3||fonteffect==4)
						text.getFont().setItalic(true);
					else
						text.getFont().setItalic(false);
					text.getFont().setSize(fontsize);
					text.getFont().setName(fontname);
					Paragraph paragraph=subCell.getFirstParagraph();//bug 38535 设置子集垂直居中不起作用
					paragraph.appendChild(text);
					if(align!=-1){
						if(nhide==0){
							setCellAlign(getAlign(align,valign),subCell,paragraph);//设置文本对齐方式
						}
						else
							setCellAlign(align,subCell,paragraph);//设置文本对齐方式
					}
					subRow.appendChild(subCell);
					subRow.getRowFormat().setHeight(ConvertUtil.pixelToPoint(height));//设置行高
					if(istitle){
						subRow.getRowFormat().setAllowBreakAcrossPages(false);//跨页断行
						subRow.getRowFormat().setHeadingFormat(true);//每页显示表头
					}else{
						subRow.getRowFormat().setAllowBreakAcrossPages(false);//跨页断行
						subRow.getRowFormat().setHeadingFormat(false);//每页显示表头
					}
				}			
			}
			table.appendChild(subRow);
			table.setLeftPadding(0);
			table.setRightPadding(0);
			table.setTopPadding(0);
			table.setBottomPadding(0);
			if(row!=rows-1)
				builder.endRow();
		}	
		table.setLeftPadding(0.0D);
		table.setRightPadding(0.0D);
		table.setTopPadding(0.0D);
		table.setBottomPadding(0.0D);
		String isHaveLineOutLine=(String) map.get("isHaveLine");
		String[] lines = isHaveLineOutLine.split("_");
		table.setBorder(BorderType.LEFT, "1".equalsIgnoreCase(lines[0])?LineStyle.SINGLE:LineStyle.NONE,"1".equalsIgnoreCase(lines[0])? 0.25:0, Color.BLACK, true);
		table.setBorder(BorderType.RIGHT, "1".equalsIgnoreCase(lines[3])?LineStyle.SINGLE:LineStyle.NONE,"1".equalsIgnoreCase(lines[3])? 0.25:0, Color.BLACK, true);
		table.setBorder(BorderType.TOP,"1".equalsIgnoreCase(lines[2])?LineStyle.SINGLE:LineStyle.NONE, "1".equalsIgnoreCase(lines[2])?0.25:0, Color.BLACK, true);
		table.setBorder(BorderType.BOTTOM, "1".equalsIgnoreCase(lines[1])?LineStyle.SINGLE:LineStyle.NONE, "1".equalsIgnoreCase(lines[1])?0.25:0, Color.BLACK, true);
		table.setBorder(BorderType.VERTICAL,"1".equals(cellIsHaveLine.split("_")[0])?LineStyle.SINGLE:LineStyle.NONE,  "1".equals(cellIsHaveLine.split("_")[0])?0.25:0.0, Color.BLACK, false);
		table.setBorder(BorderType.HORIZONTAL,"1".equals(cellIsHaveLine.split("_")[2])?LineStyle.SINGLE:LineStyle.NONE,  "1".equals(cellIsHaveLine.split("_")[2])?0.25:0.0, Color.BLACK, false);
	}
	/**
	 * align =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	 * @param align  左 0 中 1 右 2
	 * @param valign 上 0 中 1 下 2
	 * @return
	 */
	private int getAlign(int align, int valign) {
		int [][] erwei = {{0,6,3},{1,7,4},{2,8,5}};
		return erwei[align][valign];
	}
	/**
	 * 求得标题字体信息
	 * @param fontsize 
	 * @param fonteffect 
	 * @return
	 */
	private String getFontStyle(double fontsize, int fonteffect)
	{
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		style.append(fontsize);
		style.append("pt");
		switch(fonteffect)
		{
		case 2:
			style.append(";font-weight:");
			style.append("bold");
			break;
		case 3:
			style.append(";font-style:");
			style.append("italic");			
			break;
		case 4:
			style.append(";font-weight:");
			style.append("bold");
			style.append(";font-style:");
			style.append("italic");				
			break;
		}
		return style.toString();
	}
	/**
	 *单元格内容的排列方式
	 * @param align =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	 * @param builder
	 */
	private void setCellAlign(int align, DocumentBuilder builder) {
		if(align==0)   
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==1)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==2)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
		else if(align==3)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==4)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==5)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
		else if(align==6)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==7)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==8)
		{
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
	}
	/**
	 *单元格内容的排列方式
	 * @param align =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右
	 * @param builder
	 */
	private void setCellAlign(int align, Cell cell,Paragraph paragraph) {
		if(align==0)   
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==1)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==2)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
		else if(align==3)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==4)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==5)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.BOTTOM);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
		else if(align==6)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
		}
		else if(align==7)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
		}
		else if(align==8)
		{
			cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
			paragraph.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
		}
	}
	/**
	 * 得到真实的字体大小
	 * @param fontsize
	 * @return
	 */
	private double getRealFontSize(int fontsize) {
		double fontsize_real = 0.0;
		if(fontsize==11)
			fontsize_real = 10.5;
		else if(fontsize==8)
			fontsize_real = 7.5;
		else if(fontsize==7)
			fontsize_real = 6.5;
		else if(fontsize==6)
			fontsize_real = 5.5;
		else
			fontsize_real = fontsize;
		return fontsize_real;
	}
	/**
	 * 获取悬挂缩进设置
	 */
	private double getTextFirstLineIndent(double fontSize) {
		double lineIndent=0;
		if(fontSize==12) {
			lineIndent=38.6;
		}else if(fontSize==11.5) {
			lineIndent=36.4;
		}else if(fontSize==11) {
			lineIndent=35.4;
		}else if(fontSize==10.5) {
			lineIndent=33.2;
		}else if(fontSize==10) {
			lineIndent=32.2;
		}else if(fontSize==9.5) {
			lineIndent=30;
		}else if(fontSize==9) {
			lineIndent=29;
		}else if(fontSize==8.5) {
			lineIndent=26.8;
		}else if(fontSize==8) {
			lineIndent=25.8;
		}else if(fontSize==7.5) {
			lineIndent=23.6;
		}else if(fontSize==7) {
			lineIndent=22.6;
		}else if(fontSize==6.5) {
			lineIndent=20.4;
		}else if(fontSize==6) {
			lineIndent=19.4;
		}else if(fontSize==5.5) {
			lineIndent=18.2;
		}
		lineIndent = ConvertUtil.millimeterToPoint(lineIndent);
		return lineIndent;
	}
}
