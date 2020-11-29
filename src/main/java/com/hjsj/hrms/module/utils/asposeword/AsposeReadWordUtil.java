package com.hjsj.hrms.module.utils.asposeword;

import com.aspose.words.*;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 备份String pr="(\\{\\[([^\"]+)\\]\\})|(\\{\\[([^\"]+)\\]\\} | \\{\\[([^\"]+)\\]\\}\\;)|(\\{\\*([^\"]+)\\*\\})|(\\{\\&([^\"]+)\\&\\})|(\\{\\@([^\"]+)\\@\\})|(\\{\\#([^\"]+)\\#\\})";//
 * Aspose导入word模板 
 * 正则表达式解析标签	
 * 
 * (\\{\\&([^\"]+)\\&\\}) 解析{&XXXX&}
 * |(\\{\\@([^\"]+)\\@\\})解析{@XXX@}
 * |(\\{\\#([^\"]+)\\#\\})解析{#XXX#}
 * |(\\{\\[([^\"]+)\\]\\})解析{[XXXX]} {[求列表(指标代号_1)v(n)]}
 * |(\\{\\[([^\"]+)\\]\\} | \\{\\[([^\"]+)\\]\\}\\;) {[分段(关于{[求列表(B0110_1)]}，{[求列表(A0100_1)]}同志)]};
 * |(?=\\{\\*)(.+?)(?<=\\*\\})"; //特殊模板 解析{*指标名称*} {*列标题(指标代号_1)*}
 * |(\\{\\$([^\"]+)\\$\\})  标题
 * 
 * **/




/**
 * @author Administrator
 *(?=\\{\\*)(.+?)(?<=\\*\\})"; //特殊模板 解析{*指标名称*} {*列标题(指标代号_1)*}
 *(?=\\{\\&)(.+?)\\[(.+?)\\]\\:(.+?)(?<=\\&\\})
 *PARAGRAPH = 8
 *TABLE = 5
 *
 */
public class AsposeReadWordUtil {
	private String wordUrl=null;
	private Document doc=null;
	private DocumentBuilder builder=null;
	private static String textMatch="";
	public String isPdf;
	
	public String getIsPdf() {
		return isPdf;
	}
	public void setIsPdf(String isPdf) {
		this.isPdf = isPdf;
	}
	/**
	 * 
	 * wordUrl word文档路径
	 * **/
	public AsposeReadWordUtil(String wordUrl) {
		this.wordUrl = wordUrl;
		init();
	}
	/***
	 * 
	 * @param doc
	 */
	public AsposeReadWordUtil(Document doc){
		this.doc=doc;
		init();
	}
	/**
	 * 初始化doc 与 builder
	 * **/
	private void init(){
		try {
			DocumentBuilder builder = new AsposeLicenseUtil();//文件授权
			if(StringUtils.isNotEmpty(this.wordUrl))
				this.doc=new Document(this.wordUrl);
			 this.builder=new DocumentBuilder(doc);
			 textMatch="(?=\\{\\[段)(.+?)(?<=\\]\\})" +
			  "|((?=\\{\\[分段\\()(.+?)(?<=\\)\\]\\}\\;))" +
			  "|((?=\\{\\[求(.+?)\\()(.+?)(?<=\\)\\]\\}))" +
			  "|(?=\\{\\[求列表\\()(.+?)\\)(.+?)(?<=\\]\\})"+ //{[求列表(指标代号_1)v(n)]} 或{[求列表(指标代号_2)v(n)]}
			  "|(\\{\\$([^\"\\{]+)\\$\\})" +                  //bug 40364 拆分指标公式不完全
			  "|(\\{\\&(.+?)[^\"\\{]\\&\\})" +
			  "|((?=\\{\\&)(.+?)\\[(.+?)\\][^\"\\{]\\:(.+?)(?<=\\&\\}))" +
			  "|(\\{\\@([^\"\\{]+)\\@\\})" +
			  "|((?=\\{\\#)(.+?)[^\"\\{](?<=\\#\\}))" +
			  "|(?=\\{\\*)(.+?)[^\"\\{](?<=\\*\\})";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/***
	 * 解析word 文档 返回需要查询的对象
	 * **/
	public ArrayList getWordBean() {
		Node[] node=doc.getChildNodes().toArray();
		ArrayList beanList=new ArrayList();
		try {
			
			for (Node pageNode : node) {
				Section pageSec=(Section)pageNode;//获取每页word
				int secIndex=doc.getChildNodes().indexOf(pageNode);
				Body body=pageSec.getBody();
				NodeCollection bodyList=body.getChildNodes();
				for (Node bodyNode :(Iterable<Node>) bodyList) {
					int bodyIndex=bodyList.indexOf(bodyNode);
					if(bodyNode.getNodeType()==NodeType.PARAGRAPH){//获取段落
						Paragraph para=(Paragraph)bodyNode;
						getParaMatchText(para.getRange().getText().trim(), bodyIndex,0,0, beanList,8,secIndex);
					}
					if(bodyNode.getNodeType()==NodeType.TABLE){//获取表格
							ansyTable(bodyNode, beanList,secIndex,bodyIndex,0);
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*if(doc!=null){throws Exception
			
		}else{
			throw GeneralExceptionHandler.Handle(new Exception(""));
		}*/
		return beanList;
	}
	
	public void ansyTable(Node bodyNode, ArrayList beanList, int secIndex, int bodyIndex,int type) {
		Table table = (Table) bodyNode;
		try {
			RowCollection rowlist = table.getRows();
			for (Row row : rowlist) {
				ArrayList childBeanList = null;
				int emptRowNum = -1;
				int rowindex = rowlist.indexOf(row);// 表格行
				CellCollection cellist = row.getCells();
				HashMap map = new HashMap();
				String exper = "";
				String cellIndex = "";
				HashMap<Integer, Double> fontsizeMap = new HashMap<Integer, Double>();
				boolean flag = true;
				LazyDynaBean subBean = new LazyDynaBean();
				for (Cell cell : cellist) {
					int cellindex = cellist.indexOf(cell);// 单元格坐标
					if (cell.getFirstChild().getNodeType() == NodeType.TABLE) {
						childBeanList = new ArrayList();
						cellIndex += cellindex + ",";
						ansyTable(cell.getFirstChild(), childBeanList, secIndex, bodyIndex,1);
					} else {
						String text=cell.getRange().getText().trim().replace("\r", "").replace("\n", "");
						if (getSubFlag(text)) {// 判断单元格内是否是子集
							// 根据当前位置向下查找单元格为空的，直至表格结束或者表格内容不为空 获取记录数
							exper += text + "`";
							cellIndex += cellindex + ",";
							double fontsize = 0;
							if (cell.getRange() != null) {
								fontsize = cell.getFirstParagraph().getRuns().toArray()[0].getFont().getSize();
							}

							fontsizeMap.put(cellindex, fontsize);
							Row[] parentRow = cell.getParentRow().getParentTable().getRows().toArray();
							if (emptRowNum == -1 && flag) {
								emptRowNum = 1;//进出口银行子集画了三行，实际输出两行数据
								for (int i = rowindex + 1; i < parentRow.length; i++) {
									// parentRow[i].getCells().get(i).getParagraphs();
									// 1、判断当前子集单元格是否是合并单元格 （是：下一行单元格是否是合并单元格
									// 是否是合并到上一个单元格）行数不加
									// 如果下一单元格是合并单元格 并且是合并的第一个单元格 行数加一
									Cell nextCell = parentRow[i].getCells().get(cellindex);
									if (nextCell != null) {
										if (nextCell.getCellFormat().getVerticalMerge() != CellMerge.NONE
												&& nextCell.getCellFormat().getVerticalMerge() == CellMerge.PREVIOUS) {
											// 当前单元格是合并单元格而且是合并到上一个单元格
											// 认为与子集行是同一行 行数不加
											continue;
										}
										if (nextCell.getCellFormat().getVerticalMerge() != CellMerge.NONE
												&& nextCell.getCellFormat().getVerticalMerge() == CellMerge.FIRST) {
											// 是合并单元格并且是合并格的第一个单元格
											emptRowNum++;
											continue;
										}
									}

									if (nextCell == null) {
										emptRowNum++;
										continue;
									}
									String childText = nextCell.getRange().getText().trim().replace("\r", "").replace("\n", "");
									if (StringUtils.isEmpty(childText)) {
										emptRowNum++;
									} else
										break;

								}
								flag = false;// 子集标签 查询表格内需要计算子集行数 计算结束标记
												// 列标题只需计算一个单元格
											// 其余单元格不需计算
							}
						} else {
							getParaMatchText(text, bodyIndex, rowindex, cellindex, beanList,
									5, secIndex);
						}

					}
				}
					if (StringUtils.isNotEmpty(exper)) {
						if (emptRowNum != -1 && !flag) {
							subBean.set("hz", "subSet");
							exper = exper.substring(0, exper.lastIndexOf("`"));
							cellIndex = cellIndex.substring(0, cellIndex.lastIndexOf(","));
							subBean.set("hzValue", exper);
							subBean.set("cellIndex", cellIndex);
							subBean.set("subSetList", new ArrayList());// 子集存储对象集合bean.set({&子集名称:指标名称1&},value),bean.set({&子集名称:指标名称2&},value)....
																		// {{&子集名称:指标名称&}:value,.....},{},{}......
							subBean.set("rowNum", emptRowNum);// 需要子集行数
							subBean.set("bodyIndex", bodyIndex);
							subBean.set("rowIndex", rowindex);
							subBean.set("fontMap", fontsizeMap);
							subBean.set("secIndex", secIndex);
							subBean.set("type", "5");
							subBean.set("TableType", "0");
							subBean.set("isChildTable", false);
							beanList.add(subBean);
						}
					} else if (childBeanList != null) {
						subBean.set("hz", "subSet");
						// exper = exper.substring(0, exper.lastIndexOf("`"));
						cellIndex = cellIndex.substring(0, cellIndex.lastIndexOf(","));
						subBean.set("hzValueList", childBeanList);
						subBean.set("cellIndex", cellIndex);
						subBean.set("subSetList", new ArrayList());// 子集存储对象集合bean.set({&子集名称:指标名称1&},value),bean.set({&子集名称:指标名称2&},value)....
																	// {{&子集名称:指标名称&}:value,.....},{},{}......
						subBean.set("rowNum", ++emptRowNum);// 需要子集行数
						subBean.set("bodyIndex", bodyIndex);
						subBean.set("rowIndex", rowindex);
						subBean.set("fontMap", fontsizeMap);
						subBean.set("secIndex", secIndex);
						subBean.set("type", "5");
						subBean.set("TableType", "1");
						subBean.set("isChildTable", true);
						beanList.add(subBean);
					}
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public  Document writeDoc(ArrayList beanList){
		try {
			FindReplaceOptions option=new FindReplaceOptions();//设置单元格格式
//			option.getApplyParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
			for (int i = 0; i < beanList.size(); i++) {
			LazyDynaBean bean=(LazyDynaBean)beanList.get(i);
			int bodyindex=Integer.parseInt(bean.get("bodyIndex")+"");
			String hz=bean.get("hz").toString();
			Boolean isPhoto="photo".equalsIgnoreCase(String.valueOf(bean.get("photo")));
			String hzValue="";			
			HashMap<Integer,Double> fontMap=(HashMap<Integer,Double>)bean.get("fontMap");//记录的子集行字体大小
				int type=Integer.parseInt(bean.get("type")+"");
				Body body=(Body)this.doc.getChildNodes(NodeType.BODY, true).toArray()[Integer.parseInt(bean.get("secIndex").toString())];
				if(type==8){//标签在普通段落文本中
					hzValue=bean.get("hzValue").toString();
					//导出word大文本段首空格优化，大文本换行需要将\r\n替换为aspose提供的。
					hzValue=hzValue.replace(" ", "&nbsp;");
					hzValue=hzValue.replace("\r\n", ControlChar.LINE_BREAK).replace("\n", ControlChar.LINE_BREAK).replace("\r", ControlChar.LINE_BREAK);
					if("1".equalsIgnoreCase(isPdf)){
						hzValue= hzValue.replace("&nbsp;&nbsp;", "&nbsp;");
					}
					hzValue=hzValue.replace("&nbsp;", " ");
					Paragraph para=(Paragraph)body.getChildNodes().toArray()[bodyindex];//(Paragraph)body.getChild(NodeType.PARAGRAPH, bodyindex, true);
					ParagraphFormat format=para.getParagraphFormat();
					option.getApplyParagraphFormat().setAlignment(format.getAlignment());//取出段落格式并设置替换文本格式
					para.getRange().replace(hz, hzValue, option);
					
				}else{//标签在表格中
					Table table=(Table)body.getChildNodes().toArray()[bodyindex];
					//Table table=(Table)this.doc.getChild(NodeType.TABLE, bodyindex, true);
					int rowIndex=Integer.parseInt(bean.get("rowIndex")+"");
					int cellIndex=0;
					if(!"subSet".equals(hz))
						cellIndex=Integer.parseInt(bean.get("cellIndex")+"");	
					if("subSet".equals(hz)){//子集标签
						if((Boolean) bean.get("isChildTable")){
							ArrayList childList=(ArrayList) bean.get("hzValueList");
							String[] subIndex=bean.get("cellIndex").toString().split(",");
							RowCollection rowList=table.getRows();
							for (int j = rowIndex; j < rowList.toArray().length; j++) {
								Row row=rowList.toArray()[j];
								Cell cell=row.getCells().get(Integer.parseInt(subIndex[0]));//插入子集第一列 行合并列表
								if(cell==null)
									continue;
								if(cell.getFirstChild().getNodeType()==NodeType.TABLE){
									for(int childNum=0;childNum<childList.size();childNum++){
										LazyDynaBean childBean=(LazyDynaBean)childList.get(childNum);
										Table childTable=(Table) cell.getFirstChild();
										int childRowIndex=Integer.parseInt( childBean.get("rowIndex")+"");
										writeSubInfo(childBean,childTable,childRowIndex,1);
									}
								}
							}
						}else{
							writeSubInfo(bean,table,rowIndex,1);
						}
					}else{//非子集标签
						hzValue=bean.get("hzValue").toString();
						if(!isPhoto){
                            //导出word大文本段首空格优化，大文本换行需要将\r\n替换为aspose提供的。
							hzValue=hzValue.replace(" ", "&nbsp;");
							hzValue=hzValue.replace("\r\n", ControlChar.LINE_BREAK).replace("\n", ControlChar.LINE_BREAK).replace("\r", ControlChar.LINE_BREAK);
							if("1".equalsIgnoreCase(isPdf)){
								hzValue=hzValue.replace("&nbsp;&nbsp;", "&nbsp;");
							}
							hzValue=hzValue.replace("&nbsp;", " ");
						}
						Row row=table.getRows().get(rowIndex);
						Cell cell=row.getCells().get(cellIndex);
						if(isPhoto){
							if(NodeType.RUN==cell.getFirstParagraph().getFirstChild().getNodeType()){
								Run textRun=(Run)cell.getFirstParagraph().getFirstChild();
								textRun.setText("");
								cell.getFirstParagraph().removeAllChildren();
							}
							if(StringUtils.isNotBlank(hzValue)){
								Shape shape=new Shape(doc,ShapeType.IMAGE);
								shape.setWidth(cell.getCellFormat().getWidth());
								shape.setHeight(getMergeRowHei(cell));
								shape.getImageData().setImage(hzValue);
								shape.setWrapType(WrapType.INLINE);//图片在文本下
								shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
								shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
								shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
								shape.setVerticalAlignment(VerticalAlignment.CENTER);
								cell.getFirstParagraph().appendChild(shape);
							}else{
								String projectName = "hrms";
								String strc=System.getProperty("user.dir").replace("bin", "webapps");  //把bin 文件夹变到 webapps文件里面 
								strc+=System.getProperty("file.separator")+projectName+System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"photo.jpg";
								Shape shape=new Shape(doc,ShapeType.IMAGE);
								shape.setWidth(cell.getCellFormat().getWidth());
								shape.setHeight(getMergeRowHei(cell));
								shape.getImageData().setImage(strc);
								shape.setWrapType(WrapType.INLINE);//图片在文本下
								shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
								shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
								shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
								shape.setVerticalAlignment(VerticalAlignment.CENTER);
								cell.getFirstParagraph().appendChild(shape);
							}
							
						}else{
							//表格中文字换行。
							ParagraphCollection paragraphs = cell.getParagraphs();
						    for(int m=0;m<paragraphs.getCount();m++){
						    	 NodeCollection childNodes = paragraphs.get(m).getChildNodes();
						    	 String text="";
						    	 Boolean isfind=false;
						    	 for(int childCount=0;childCount<childNodes.getCount();childCount++){
						    		Node node = childNodes.get(childCount);
						    		 if(node!=null){
						    			 text+=node.getText();
						    			 if(text.indexOf(hz)==-1){
						    				 if(text.indexOf("{")==-1){
						    					 text="";
						    				 }else{
						    					 Run run=(Run)node;
								    			 run.setText("");
						    				 }
						    				 continue;
						    			 }else
						    			 {
						    				 isfind=true;
						    			 }
						    			 text = text.replace(hz, hzValue);
						    			 Run run=(Run)node;
						    			 run.setText(text);
						    			 text="";
						    			 if(isfind){
						    				 break;
						    			 }
						    		 }
						    	 }
						    	 if(isfind){
				    				 break;
				    			 }
						    	 
						    	 
						    	
						    	
						    }
							/*cell.getRange().replace(hz, hzValue, option);
							String[] textArr=cell.getRange().getText().trim().split("\r\n");
							cell.getRange().replace(hzValue, "", option);
							cell.getParagraphs().clear();
							for (int k = 0; k < textArr.length; k++) {
								Paragraph pra=new Paragraph(this.doc);
								pra.appendChild(new Run(this.doc,textArr[k]));
								cell.appendChild(pra);
							}*/
							
							
						}
						//Range range=cell.getRange();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.doc;
	}
	public void writeSubInfo(LazyDynaBean bean, Table table, int rowIndex,int type){
		try{
			String hzValue=bean.get("hzValue").toString();
			hzValue=bean.get("hzValue").toString();
			Font font_bak=null;
			CellFormat cellFormat_bak=null;
			Cell cell_bak=null;
			Row row_bak=null;
			ArrayList subList=(ArrayList)bean.get("subSetList");
			String[] subArry=hzValue.split("`");
			String[] subIndex=bean.get("cellIndex").toString().split(",");
			RowCollection rowList=table.getRows();
			int realRowNum=0;
			int rowCount=rowList.getCount()-rowIndex;//模板子集如果有标题的话需要减掉标题行，没有标题会传rowIndex=0
			int rowRowNum=Integer.parseInt(bean.get("rowNum")+"");
			HashMap map=new HashMap();
			for (int j = rowIndex; j < rowIndex+subList.size(); j++) {
				if(realRowNum<rowRowNum){
					if(realRowNum<rowCount){
						Row row=rowList.toArray()[j];
						if(row_bak==null){
							row_bak=row;
						}
						Cell cell=row.getCells().get(Integer.parseInt(subIndex[0]));//插入子集第一列 行合并列表
						if(cell==null)
							continue;
						if(cell_bak==null){
							cell_bak=cell;
						}
						int cellType=cell.getCellFormat().getVerticalMerge();
						if(cellType!=CellMerge.NONE){
							if(cellType==CellMerge.FIRST){
								map.put(realRowNum+"", j+"");
								realRowNum++;
							}
						}else{
							realRowNum++;
						}
					}else{
						if(type==1){
							Node newRow=row_bak.deepClone(true);
							table.appendChild(newRow);
							realRowNum++;
						}else{
							break;
						}
					}
				}else{
					break;
				}
			}
			rowList=table.getRows();
			for (int j = 0; j < subList.size()&&j<realRowNum; j++) {
				LazyDynaBean subBean=(LazyDynaBean)subList.get(j);
				Row row=null;
				if(map!=null&&StringUtils.isNotEmpty((String)map.get(j+""))){
					row=rowList.get(Integer.parseInt((String)map.get(j+"")));
				}else{
					row=rowList.get(rowIndex+j);
				}
				for(int k=0;k<subArry.length;k++){
					int index=Integer.parseInt(subIndex[k]);
					Cell cell=row.getCells().get(index);
					//if(cell.getCellFormat().getVerticalMerge()!=CellMerge)
					if(cell==null)
						continue;
					if(cellFormat_bak==null){
						cellFormat_bak=cell.getCellFormat();
					}
					ParagraphCollection paragraphs = cell.getParagraphs();
				    for(int m=0;m<paragraphs.getCount();m++){
				    	NodeCollection node = paragraphs.get(m).getChildNodes();
				    	Run firstChild =null;
				    	Boolean isReplace=false;
				    	for(int i=0;i<node.getCount();i++){
				    		Node node2= node.get(i);
				    		if("Run".equalsIgnoreCase(Node.nodeTypeToString(node2.getNodeType()))){
				    			firstChild= (Run) node2;
					    	 if(!isReplace){
					    		 isReplace=true;
						    	 String text=subBean.get(subArry[k])==null?"":subBean.get(subArry[k])+"";
								 //导出word大文本段首空格优化，大文本换行需要将\r\n替换为aspose提供的。
						    	 text=text.replace(" ", "&nbsp;");
						    	 text=text.replace("\r\n", ControlChar.LINE_BREAK).replace("\n", ControlChar.LINE_BREAK).replace("\r", ControlChar.LINE_BREAK);
								 if("1".equalsIgnoreCase(isPdf)){
									text= text.replace("&nbsp;&nbsp;", "&nbsp;");
								 }
								 text= text.replace("&nbsp;", " ");
						    	 if(firstChild!=null){
						    		 firstChild.setText(text);
						    		 if(font_bak==null)
						    			 font_bak=firstChild.getFont();
						    	 }
					    	 }else{
					    		 if(firstChild!=null){
						    		 firstChild.setText("");
						    		 if(font_bak==null)
						    			 font_bak=firstChild.getFont();
						    	 }
					    	 }
				    		}
				    	}
				    	if(firstChild==null){
				    		 String text=subBean.get(subArry[k])==null?"":subBean.get(subArry[k])+"";
							 //导出word大文本段首空格优化，大文本换行需要将\r\n替换为aspose提供的。
				    		 text=text.replace(" ", "&nbsp;");
					    	 text=text.replace("\r\n", ControlChar.LINE_BREAK).replace("\n", ControlChar.LINE_BREAK).replace("\r", ControlChar.LINE_BREAK);
							 if("1".equalsIgnoreCase(isPdf)){
								text= text.replace("&nbsp;&nbsp;", "&nbsp;");
							 }
							 text= text.replace("&nbsp;", " ");
				    		 Run newRun=new Run(this.doc,text+"");
				    		 newRun.getFont().setSize(font_bak.getSize());
				    		 newRun.getFont().setName(font_bak.getName());
				    		 newRun.getFont().setColor(font_bak.getColor());
				    		 newRun.getFont().setBold(font_bak.getBold());
				    		 newRun.getFont().setItalic(font_bak.getItalic());
				    		 newRun.getFont().setBold(font_bak.getBold());
				    		 paragraphs.get(m).appendChild(newRun);
				    		 if(cellFormat_bak!=null){
					    		 cell.getCellFormat().setVerticalAlignment(cellFormat_bak.getVerticalAlignment());
					    		 cell.getCellFormat().setHorizontalMerge(cellFormat_bak.getHorizontalMerge());
				    		 }
				    	}
				    }
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/***
	 * 获取合并行的每行行高
	 * */
	public double getMergeRowHei(Cell cell){
		double rowHeight=0;
		try {
			boolean isHorizontallyMerged = cell.getCellFormat().getHorizontalMerge() != CellMerge.NONE;
			boolean isVerticallyMerged = cell.getCellFormat().getVerticalMerge() != CellMerge.NONE;
			/*int startCell=cell.getParentRow().indexOf(cell) + 1;//开始列
			int startRow=cell.getParentRow().getParentTable().indexOf(cell.getParentRow()) + 1;//开始行
*/			int C=cell.getParentRow().indexOf(cell);//当前单元格 列下标
			int R=cell.getParentRow().getParentTable().indexOf(cell.getParentRow());//当前单元行下标
			Row[] rows=cell.getParentRow().getParentTable().getRows().toArray();
			double width=0;//开始位置到图片格的宽度
			int col=0;
			//rowHeight=cell.getParentRow().getRowFormat().getHeight();
			colOk:for (int i = R; i < rows.length; i++) {//行
				Cell[] childCelArr=rows[i].getCells().toArray();//指定行的单元格数组
				double tem=0;
				for (int j = 0; j < childCelArr.length; j++) {//列
					if(i==R){//图片起始行 
						if(j<C){//图片当前列的位置
							width+=childCelArr[j].getCellFormat().getWidth();//计算插入图片的单元格第一行从开始位置到图片格的宽度 
							}
						if(j==C)
							rowHeight+=cell.getParentRow().getRowFormat().getHeight();
					}else if(i>R){
						tem+=childCelArr[j].getCellFormat().getWidth();
						if(((int)width-5<=(int)tem)&&(int)tem<=(int)width+5){
							if((childCelArr[j+1].getCellFormat().getVerticalMerge()!=CellMerge.FIRST)&&
									(childCelArr[j+1].getCellFormat().getVerticalMerge()==CellMerge.PREVIOUS)){
								rows[i].getRowFormat().setHeightRule(HeightRule.EXACTLY);//涉及到插入图片的合并单元格 取消行高自适应
								rowHeight+=rows[i].getRowFormat().getHeight(); 
								col++;
								break;
							}else{
								break colOk;
							}
						}else if(tem>width)
							break;
					}
				}
			}
			
			if(isHorizontallyMerged && isVerticallyMerged){//横向纵向合并
				
			}else if(isHorizontallyMerged){//横向合并
				
			}else if(isVerticallyMerged){//纵向合并
				
			}else{
				rowHeight=cell.getParentRow().getRowFormat().getHeight();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowHeight==0?16:rowHeight;//bug 36391 画的模版没有设置行高此处导致获取的行高为0，图片显示不出来。现给默认值16
	}
	
	/**
	 * 
	 * 
	 * @param beanList
	 * @return
	 */
	public String getWordUrl(ArrayList beanList){
		try {
			Document doc=this.writeDoc(beanList);
			doc.save(this.wordUrl);
			if("1".equals(this.isPdf)){
				int lastindex=this.wordUrl.lastIndexOf(".");
				String url=this.wordUrl.substring(0,lastindex)+".pdf";
				doc.save(url);
				return url;
			}else
				return this.wordUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/***
	 * 
	 * @param beanList
	 * @return
	 */
	public Document getWordDoc(ArrayList beanList){
		return this.writeDoc(beanList);
	}
	/**
	 * 判断是否是子集标识
	 * 单人 {&子集名称:指标名称&} 多人"{*列标题*}"   {*指标名称*} {*列标题(指标代号_1)*}    (?=\\{\\*)(.+?)(?<=\\*\\})
	 * {&拟[个人简历子集]:起始时间&}  ((?=\\{\\&)(.+?)\\[(.+?)\\]\\:(.+?)(?<=\\&\\}))|
	 * {&指标名称&}  (\\{\\&(.+?)\\&\\})
	 * 分析表格内的文本是否是子集
	 * @param text
	 * @return
	 */
	public boolean getSubFlag(String text){
		/**
		 * {&子集名称:指标名称&}
		   {&拟[个人简历子集]:起始时间&}
		   {*指标名称*}
		   {*列标题(指标代号_1)*}
		   {#常量#}
		 * **/
		String textMatch="(\\{\\&(.+?):(.+?)\\&\\})" +
						"|(?=\\{\\*)(.+?)(?<=\\*\\})" +
						"|((?=\\{\\&)(.+?)\\[(.+?)\\]\\:(.+?)(?<=\\&\\}))"+
						"|((?=\\{\\#)(.+?)(?<=\\#\\}))";
		Pattern patt=Pattern.compile(textMatch);
		Matcher matcher=patt.matcher(text);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 判断文档中读取内容是否包含标签
	 * @param text 需要校验的内容
	 * @param bodyIndex 读取的文档位置
	 * @param rowIndex 表格行
	 * @param cellIndex 表格列
	 * @param beanList
	 * @param type (8 PARAGRAPH) (5 table)
	 * @return
	 */
	public ArrayList getParaMatchText(String text,int bodyIndex,int rowIndex,int cellIndex,ArrayList beanList,int type,int secIndex){
//		String textMatch="(\\{\\$([^\"]+)\\$\\})|(\\{\\&([^\"]+)\\&\\})|(\\{\\@([^\"]+)\\@\\})|(\\{\\#([^\"]+)\\#\\})|(\\{\\[([^\"]+)\\]\\})|(\\{\\[([^\"]+)\\]\\} | \\{\\[([^\"]+)\\]\\}\\;)|(?=\\{\\*)(.+?)(?<=\\*\\})";
//		String textMatch="(\\{\\$([^\"]+)\\$\\})|((?=\\{\\&)(.+?)\\[(.+?)\\]\\:(.+?)(?<=\\&\\}))|(\\{\\&(.+?)\\&\\})|(\\{\\@([^\"]+)\\@\\})|(\\{\\#([^\"]+)\\#\\})|(\\{\\[([^\"]+)\\]\\})|(\\{\\[([^\"]+)\\]\\} | \\{\\[([^\"]+)\\]\\}\\;)|(?=\\{\\*)(.+?)(?<=\\*\\})";
		
		Pattern partt=Pattern.compile(textMatch);
		Matcher matcher=partt.matcher(text);
		while(matcher.find()){
			LazyDynaBean bean=new LazyDynaBean();
			if(type==8){
				bean.set("hz", matcher.group().trim());
				bean.set("bodyIndex", bodyIndex);
				bean.set("type", "8");
				bean.set("secIndex", secIndex);
				beanList.add(bean);
			}else if(type==5){
				bean.set("hz", matcher.group().trim());
				bean.set("bodyIndex", bodyIndex);
				bean.set("rowIndex", rowIndex);
				bean.set("cellIndex", cellIndex);
				bean.set("type", "5");
				bean.set("secIndex", secIndex);
				if(matcher.group().trim().toLowerCase().indexOf("photo")!=-1)
				{
					bean.set("photo","photo");
				}
				beanList.add(bean);
			}
			
		}
		return beanList;
	}
	
}
