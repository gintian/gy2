package com.hjsj.hrms.module.card.businessobject;

import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 登记表html元素拼写
 * */
public class CreateCardHtmlBo {
	
	private String inforkind="";//模块类别
	private String nid="";//id
	private String nbase="";//人员库
	private int tabid=0;
	private int queryflag;          /*0代表安条件查询1代表安月时间查询2代表安时间段查询3.安时间季度查询*/
	private String fieldpurv="";
	private String bizDate="";
	private String plan_id="";
	//---日期参数start
	private int year;//年
	private int month;//月
	private int ctimes;//次数
	private int season;//季度
	private String startDate="";//开始日期
	private String endDate="";//结束日期
	//---日期参数 end
	private String isMobile="";
	private String cardtype="";
	private String browser="MSIE";
	/** 浏览器内核主版本号 */
	private int browserMajorVer=-1;
	private int pageid=0;//页签
	
	public int getPageid() {
		return pageid;
	}
	public void setPageid(int pageid) {
		this.pageid = pageid;
	}
	private Connection conn=null;
	private UserView userview=null;
	
	private int tmargin=0;
	private int bmargin=0;
	private int lmargin=0;
	private int rmargin=0;	
	private String ykcard_auto="";
	private String display_zero="";
	//card的主要函数显示整个的Grid表格
	private String fenlei_type="";
	private String color="#15428b";
	
	public CreateCardHtmlBo(Connection conn,UserView userview) {
		this.conn=conn;
		this.userview=userview;
		
	}
	
	public String getYkcard_auto() {
		return ykcard_auto;
	}
	public void setYkcard_auto(String ykcard_auto) {
		this.ykcard_auto = ykcard_auto;
	}
	/**
	 * 根据tabid创建登记表元素
	 * */
	public StringBuffer getCardHtml() throws GeneralException {
		StringBuffer sbf=new StringBuffer();
		try {
			getCardPageSet();
			ArrayList<Integer> pageWHlist=getTable_height(conn, tabid);
			int height=pageWHlist.get(0);
			int width=pageWHlist.get(1);
			getMargin(conn);
			height=height+this.tmargin+this.bmargin;
		   	width=width+this.lmargin+this.rmargin;
			if(this.inforkind!=null&& "5".equals(this.inforkind))
				nbase="Usr";//绩效的人员库默认Usr
		   	if(this.inforkind!=null&& "2".equals(this.inforkind)&&nbase==null)
		   		nbase="Usr";
		  // 	sbf.append("<link rel=\"stylesheet\" href=\"/css/css1_brokenline.css\" type=\"text/css\">");
		   	//sbf.append("<style>.element::-webkit-scrollbar { width: 0 !important }</style>");
		   	sbf.append("<div  id='pageID'   style=\"position:relative;width:"+(width)+"px;height:"+(height)+"px;background-color:#FFFFFF;\">");
		   	sbf.append(printTitleImage(conn,tabid,pageid));
		   	sbf.append(printCard(this.nbase, conn));
		   	sbf.append("</div>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbf;
	}
	/***
	 * 获取页面设置参数
	 * ykcard_auto 页面自适应
	 * display_zero 打印0
	 * */
	private void getCardPageSet() {
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		ykcard_auto=sysbo.getValue(Sys_Oth_Parameter.YKCARD_AUTO);
		CardConstantSet cardConstantSet=new CardConstantSet();
		LazyDynaBean rnameExtendAttrBean=cardConstantSet.getRnameExtendAttrBean(conn,tabid+"");
		if(rnameExtendAttrBean!=null)
		{
			if(ykcard_auto==null||ykcard_auto.length()<=0|| "0".equals(ykcard_auto))
			{
				ykcard_auto=(String)rnameExtendAttrBean.get("auto_size");
			}
			this.display_zero=(String)rnameExtendAttrBean.get("display_zero");
		}
	}
	/**
	 * 拼接登记表内容
	 * */
	private StringBuffer printCard(String userbase,Connection conn) throws Exception{
		StringBuffer out=new StringBuffer();
		DataEncapsulation encap=new DataEncapsulation();                             //创建封装Grid数据的对象
		 encap.setUserview(userview);
	     List rgrids=encap.getRgrid(tabid,pageid,conn);                               //获得Grid各个cell的List的对象
	     float[] gridArea=YkcardStaticBo.getRGridArea(conn, tabid+"", pageid+"");
	     HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridMapList=encap.getGridMapList();//记录每个单元格 rleft+rwidth 等的位置信息
	     HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopMapList=encap.getGridTopIndexList();//初始化记录单元格 rtop+rheight位置信息
	     HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopLastList=encap.getLastGridTopList();//初始化记录单元格 rtop+rheight位置信息
	     int heightn=encap.getPagesize(tabid,pageid,conn);     
	     MadeFontsizeToCell mc=new MadeFontsizeToCell(browser);                              //创建的字体适应cell大小的对象
	     mc.setAuto(this.ykcard_auto);
	     int fontsize;
	     String fontweight="";
	     ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);     
	     if(this.inforkind!=null&& "5".equals(this.inforkind)&&this.plan_id!=null&&this.plan_id.length()>0)
	     {
	    	 StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
	    	 alUsedFields=statisticPlan.khResultField(alUsedFields,this.plan_id);
	     }
	     GetCardCellValue card=new GetCardCellValue(); 
	     this.fenlei_type=card.getOneFenleiYype(userview,userbase, this.nid, conn);
	     card.setDisplay_zero(this.display_zero);//创建获得单元个cell值的对象
	     card.setBizDate(bizDate);
	     List setList=encap.GetSets(tabid,pageid,conn);     
	     //获得到整个Grid所有的子集名称
	     //显示页面标头等信息的结束
	     //显示各个单元个得开始
	     RGridView rgrid;
	     int topn;                                                  //单元格的上边位置
	     int leftn;                                                 //单元格的左边位置
	     int heights;                                               //单元格的高
	     int widthn;                                                //单元格的宽
	     String hz="";                                              //单元格的内容是说明信息
	     String fontStr="";                                         //字体类型比如"宋体"
	     try{
	        if(rgrids!=null&&!rgrids.isEmpty())
			{
				for (int i = 0; i < rgrids.size(); i++) {
					rgrid = (RGridView) rgrids.get(i);
					// if(!rgrid.getFlag().equals("C")){
					leftn = (int) Float.parseFloat(rgrid.getRleft()) + this.lmargin;
					topn = (int) Float.parseFloat(rgrid.getRtop()) + this.tmargin;
					/*
					 * if("Safari".equals(browser)&&browserMajorVer<537)
					 * widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1; else
					 */
					widthn = (int) Float.parseFloat(rgrid.getRwidth()) - 1;
					if ("Firefox".equalsIgnoreCase(browser)) {// 火狐浏览器
						if (Float.parseFloat(rgrid.getRwidth()) == gridArea[6] - gridArea[0]) {// 插入内容宽度为模板表格宽度处理
							widthn -= 1;
						}
					}

					heights = (int) Float.parseFloat(rgrid.getRheight());

					fontweight = rgrid.getFonteffect();
					if(StringUtils.isEmpty(fontweight)) {
						fontweight="1";
					}
					if("2".equals(fontweight)) {
	          			fontweight="bold";
	          		}else if("3".equals(fontweight)){//倾斜
	          			fontweight="normal;font-style:italic;";
	          		}else if("4".equals(fontweight)){//加粗倾斜
	          			fontweight="bold;font-style:italic;";
	          		}else {
	          			fontweight="normal";
	          		}
					hz = rgrid.getCHz();
					String[] align = mc.getAlign(rgrid.getAlign());
					if ("1".equals(rgrid.getSubflag())) {
						align[1] = "top";
						align[0] = "left";
					}
					// Safari7 有单元格撑大问题
					out.append("<table id=grid_table_" + rgrid.getGridno()
							+ " border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\"  style=\"table-layout:fixed;border-collapse:collapse;BACKGROUND-COLOR:transparent;position:absolute;top:"
							+ topn + "px;left:" + leftn + "px;width:" + widthn + "px;height:" + heights + "px\">");
					out.append("<tr>");
					String cssLineHeight = getCssLineHeight(rgrid);
					// liuy 2015-12-17 优化登记表不显示格线的情况下，数据显示垂直居中 begin
					boolean tempflag = false;
					if (("A".equals(rgrid.getFlag()) || "B".equals(rgrid.getFlag()) || "K".equals(rgrid.getFlag()))
							&& "1".equals(rgrid.getSubflag())) {
						XmlSubdomain xmlSubdomain = new XmlSubdomain(rgrid.getSub_domain());
						xmlSubdomain.getParaAttribute();
						if (StringUtils.isNotEmpty(xmlSubdomain.getFields())) {
							if (("7".equals(rgrid.getAlign()) || "6".equals(rgrid.getAlign())
									|| "8".equals(rgrid.getAlign())) && "false".equals(xmlSubdomain.getVl())
									&& "false".equals(xmlSubdomain.getHl()))
								tempflag = true;
						}
					}
					if (i > 0) {
						HashMap<Double, ArrayList<RGridView>> gridMap = gridMapList.get(pageid + "");
						if (gridMap != null) {
							ArrayList<RGridView> list = gridMap.get(Double.parseDouble(rgrid.getRleft()));
							if (list != null && list.size() > 0) {
								for (int j = 0; j < list.size(); j++) {
									RGridView gridView = list.get(j);
									// 存储rleft+rwidth=当前rgrid 的rleft
									if (!gridView.getGridno().equals(rgrid.getGridno())
											&& Double.parseDouble(gridView.getRtop()) <= Double
													.parseDouble(rgrid.getRtop())
											&& Double.parseDouble(gridView.getRtop())
													+ Double.parseDouble(gridView.getRheight()) > Double
															.parseDouble(rgrid.getRtop())) {
										if ("1".equals(gridView.getR()) && "1".equals(rgrid.getL())) {
											rgrid.setL("0");
										}
										break;
									}
								}
							}

						}

					}

					HashMap<Double, ArrayList<RGridView>> gridTopMap = gridTopMapList.get(pageid + "");
					if (gridTopMap != null) {
						ArrayList<RGridView> list = gridTopMap
								.get(Double.parseDouble(rgrid.getRtop()) + Double.parseDouble(rgrid.getRheight()));
						if (list != null && list.size() > 0) {
							for (int j = 0; j < list.size(); j++) {
								RGridView gridView = list.get(j);
								// 存储rleft+rwidth=当前rgrid 的rleft
								if (!gridView.getGridno().equals(rgrid.getGridno())
										&& Double.parseDouble(gridView.getRleft()) <= Double
												.parseDouble(rgrid.getRleft())
										&& Double.parseDouble(gridView.getRleft())
												+ Double.parseDouble(gridView.getRwidth()) > Double
														.parseDouble(rgrid.getRleft())) {
									if ("1".equals(rgrid.getB()) && "1".equals(gridView.getT())) {
										rgrid.setB("0");
									}
									break;
								}
							}
						}
					}
					//当前单元格上方的单元格 如果当前单元格上方单元格有下边线，则当前单元格隐藏上边线
					HashMap<Double, ArrayList<RGridView>> gridListMap = gridTopLastList.get(pageid + "");
					if(gridListMap!=null&&!"0".equals(rgrid.getT())) {
						ArrayList<RGridView> list = gridListMap
								.get(Double.parseDouble(rgrid.getRtop()));
						if(list!=null&&list.size()>0) {
							for (RGridView gridView : list) {
								if (Double.parseDouble(gridView.getRleft()) <= Double.parseDouble(rgrid.getRleft())
										&& (Double.parseDouble(gridView.getRleft())
												+ Double.parseDouble(gridView.getRwidth())) < Double
														.parseDouble(rgrid.getRleft())) {
									if("1".equals(gridView.getB())) {
										rgrid.setT("0");
										break;
									}
								}
							}
						}
					}
					
					if (tempflag) {
						out.append("<td id=grid_" + rgrid.getGridno()
								+ "  style=\"word-wrap:break-word;overflow:hidden;" + cssLineHeight + "\" class=\""
								+ new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(), rgrid.getR(),
										rgrid.getT(), rgrid.getB())
								+ "\" align=\"" + align[0] + "\" width=\"" + widthn + "\" height=\"" + heights + "\">");
					} else {
						out.append("<td id=grid_" + rgrid.getGridno()
								+ "  style=\"word-wrap:break-word;overflow:hidden;" + cssLineHeight + "\" class=\""
								+ new MadeCardCellLine().GetCardCellLineShowcss(rgrid.getL(), rgrid.getR(),
										rgrid.getT(), rgrid.getB())
								+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\" width=\"" + widthn
								+ "\" height=\"" + heights + "\">");
					}
					if ("1".equals(rgrid.getSubflag())) {
						if (!"1".equalsIgnoreCase(ykcard_auto)) {// 设置单元格字体自适应时子集不添加隐藏滚动设置
							out.append("<div   class=\"outer-container\" style=\"height:" + (heights - 1) + "px;width:"
									+ (widthn - 1) + "px;\">");
							out.append("<div title=\"滑动显示全部\"  class=\"inner-container\" style=\"height:" + (heights)
									+ "px;width:" + (widthn + 16) + "px;\">");
						} else {
							out.append("<div id=grid_sub_" + rgrid.getGridno() + " style=\"position:relative;height:"
									+ (heights - 1) + "px;width:" + (widthn - 1) + "px;text-align: " + align[0]
									+ ";;vertical-align: " + align[1] + ";\">");
						}
					}
					if (!"1".equals(rgrid.getSubflag()) && "1".equalsIgnoreCase(ykcard_auto))
						out.append("<div id=grid_div_" + rgrid.getGridno() + " style=\"position:relative;max-height:"
								+ (heights - 1) + "px;width:" + (widthn - 1) + "px;text-align: " + align[0]
								+ ";;vertical-align: " + align[1] + ";\">");
					// liuy 2015-12-17 end
					if ("A".equals(rgrid.getFlag()) && !"1".equals(rgrid.getSubflag())) { // A人员库
						byte nFlag = 0; // 0表示人员库
						ArrayList valueList = null;

						if ("1".equalsIgnoreCase(rgrid.getIsView())) {
							valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
						} else {
							if ("P01".equalsIgnoreCase(rgrid.getCSetName())) {
								valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
							} else {
								if (!setList.isEmpty())
									for (int j = 0; j < setList.size(); j++) {
										DynaBean fieldset = (DynaBean) setList.get(j);
										if (fieldset.get("fieldsetid").equals(rgrid.getCSetName())) {

											valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag,
													valueList, fieldset);
											break;
										}
									}
							}
						}

						if (valueList != null && !valueList.isEmpty()) {
							if (valueList.size() == 1) {
								fontsize = Integer.parseInt(rgrid.getFontsize());
								// fontsize=mc.ReDrawLitterRect(widthn, heights, valueList,
								// Integer.parseInt(rgrid.getFontsize()));
								out.append("<font  id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
										+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
										+ ";font-size:" + fontsize + "pt\">");
								out.append(valueList.get(0) != null && valueList.get(0).toString().trim().length() > 0
										? valueList.get(0).toString()
										: "&nbsp;");
								out.append("</font>");
							} else {
								// int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
								int heigh = Integer.parseInt(rgrid.getFontsize())
										+ Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(), "0.72", 0));
								;
								for (int j = 0; j < valueList.size(); j++) {

									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										fontsize = Integer.parseInt(rgrid.getFontsize());// mc.ReOneRowDrawLitterRect(widthn,
																							// heigh,
																							// valueList.get(j).toString(),
																							// Integer.parseInt(rgrid.getFontsize()));
										out.append("<font id=grid_value_" + rgrid.getGridno() + "_" + j + " color=\""
												+ this.color + "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										if (j > 0)
											out.append("<br>");
										out.append(valueList.get(j) != null
												&& valueList.get(j).toString().trim().length() > 0
														? valueList.get(j).toString()
														: "&nbsp;");
										out.append("</font>");
									} else {
										out.append("<br>");
									}

								}
							}
						} else {
							out.append("&nbsp;");
						}
					} else if ("A".equals(rgrid.getFlag()) && "1".equals(rgrid.getSubflag())) {
						// 人员子集
						byte nFlag = 0;
						out.append(viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
								widthn, heights));
					} else if ("B".equals(rgrid.getFlag()) && !"1".equals(rgrid.getSubflag())) { // B单位库
						byte nFlag = 2; // 2表示单位库
						ArrayList valueList = null;
						if (!setList.isEmpty())
							for (int j = 0; j < setList.size(); j++) {
								DynaBean fieldset = (DynaBean) setList.get(j);
								if (fieldset.get("fieldsetid").equals(rgrid.getCSetName())) {
									valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList,
											fieldset);
									break;
								}
							}
						if (valueList != null && !valueList.isEmpty()) {
							if (valueList.size() == 1) {
								fontsize = mc.ReDrawLitterRect(widthn, heights, valueList,
										Integer.parseInt(rgrid.getFontsize()));
								out.append("<font id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
										+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
										+ ";font-size:" + fontsize + "pt\">");
								out.append(valueList.get(0) != null && valueList.get(0).toString().trim().length() > 0
										? valueList.get(0).toString()
										: "&nbsp;");
								out.append("</font>");
							} else {
								// int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
								int heigh = Integer.parseInt(rgrid.getFontsize())
										+ Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(), "0.72", 0));
								;
								for (int j = 0; j < valueList.size(); j++) {

									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										fontsize = mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(),
												Integer.parseInt(rgrid.getFontsize()));
										out.append("<font id=grid_value_" + rgrid.getGridno() + "_" + j + "  color=\""
												+ this.color + "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										if (j > 0)
											out.append("<br>");
										out.append(valueList.get(j) != null
												&& valueList.get(j).toString().trim().length() > 0
														? valueList.get(j).toString()
														: "&nbsp;");
										out.append("</font>");
									} else {
										out.append("<br>");
									}
								}

							}
						} else {
							out.append("&nbsp;");
						}
					} else if ("B".equals(rgrid.getFlag()) && "1".equals(rgrid.getSubflag())) {
						// 单位子集
						byte nFlag = 2;
						out.append(viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
								widthn, heights));
					} else if ("K".equals(rgrid.getFlag()) && !"1".equals(rgrid.getSubflag())) { // K岗位库
						byte nFlag = 4; // 4表示岗位库
						ArrayList valueList = null;
						if (!setList.isEmpty())
							for (int j = 0; j < setList.size(); j++) {
								DynaBean fieldset = (DynaBean) setList.get(j);
								if (fieldset.get("fieldsetid").equals(rgrid.getCSetName())) {
									valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList,
											fieldset);
									break;
								}
							}
						if (valueList != null && !valueList.isEmpty()) {
							if (valueList.size() == 1) {
								fontsize = mc.ReDrawLitterRect(widthn, heights, valueList,
										Integer.parseInt(rgrid.getFontsize()));
								out.append("<font id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
										+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
										+ ";font-size:" + fontsize + "pt\">");
								out.append(valueList.get(0) != null && valueList.get(0).toString().trim().length() > 0
										? valueList.get(0).toString()
										: "&nbsp;");
								out.append("</font>");
							} else {
								// int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
								int heigh = Integer.parseInt(rgrid.getFontsize())
										+ Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(), "0.72", 0));
								;
								for (int j = 0; j < valueList.size(); j++) {

									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										fontsize = mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(),
												Integer.parseInt(rgrid.getFontsize()));
										out.append("<font id=grid_value_" + rgrid.getGridno() + "_" + j + "  color=\""
												+ this.color + "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										if (j > 0)
											out.append("<br>");
										out.append(valueList.get(j) != null
												&& valueList.get(j).toString().trim().length() > 0
														? valueList.get(j).toString()
														: "&nbsp;");
										out.append("</font>");
									} else {
										out.append("<br>");
									}
								}

							}
						} else {
							out.append("&nbsp;");
						}
					} else if ("K".equals(rgrid.getFlag()) && "1".equals(rgrid.getSubflag())) {
						// 职位子集
						byte nFlag = 4;
						out.append(viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
								widthn, heights));
					} else if ("P".equals(rgrid.getFlag())) { // p表示照片
						String url = "/servlet/vfsservlet?fileid=";
//						if ("zp_noticetemplate_flag".equals(cardtype))
//							url = "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filename=";
//						else
//							url = "/servlet/DisplayOleContent?filename=";
						String filename = "";
						if (StringUtils.isNotEmpty(nid)) {
							filename = ServletUtilities.createPhotoFile(userbase + "A00", nid, "P", null);
						}
						if (filename != null && filename.length() > 0) {
							//filename = PubFunc.encrypt(filename);
							out.append("<img src=\"" + url + filename + "&fromjavafolder=true\" height=" + String.valueOf(heights - 5)
									+ " width=" + String.valueOf(widthn - 3) + ">");
						} else {
							out.append("<img src=\"/images/photo.jpg\" height=" + String.valueOf(heights - 5)
									+ " width=" + String.valueOf(widthn - 3) + ">");
						}
					} else if ("H".equals(rgrid.getFlag())) { // H表示文字说明
						hz = "`".equals(hz) ? "" : hz;// liuy 2015-6-9 9970
						if (hz != null && hz.trim().length() > 0) {
							// fontsize=mc.ReDrawLitterRect(widthn,heights,rgrid.getCHz(),Integer.parseInt(rgrid.getFontsize()),hz,disting_pt,rgrid.getField_type(),rgrid.getSlope());
							ArrayList varlist = new ArrayList();
							varlist.add(rgrid.getCHz());
							// fontsize=mc.ReDrawLitterRect(widthn, heights, varlist,
							// Integer.parseInt(rgrid.getFontsize()));
							// StringTokenizer Stok=new
							// StringTokenizer(hz,"`");//类获取输入流并将其分析为“标记”，允许一次读取一个标记
							fontsize = Integer.parseInt(rgrid.getFontsize());// 界面展现目前采用jquery技术计算字体 后台不处理字体大小
							int last_s = hz.lastIndexOf("`");
							if (last_s == (hz.length() - 1))
								hz = hz.substring(0, hz.length() - 1);
							String[] a_stok = hz.split("`");
							out.append("<font id=grid_value_" + rgrid.getGridno() + "  style=\"font-weight:"
									+ fontweight + ";font-family:" + rgrid.getFontName() + ";font-size:" + fontsize
									+ "pt\">");

							if (a_stok != null && a_stok.length > 0) {
								for (int s = 0; s < a_stok.length; s++) {
									if (s > 0)
										out.append("<br>");

									out.append(a_stok[s].replaceAll("　", "&nbsp;&nbsp;"));
									// out.println(a_stok[s].replaceAll(" ", "&nbsp;"));

								}
							} else {
								out.append("&nbsp;");
							}
							out.append("</font>");
						} else {
							out.append("<br>");
						}
					} else if ("J".equals(rgrid.getFlag())) { // J计划库
						byte nFlag = 5; // 5表示计划库
						rgrid.setPlan_id(this.plan_id);
						if ("0".equals(rgrid.getSubflag())) {
							ArrayList valueList = null;
							valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
							if (valueList != null && !valueList.isEmpty()) {
								for (int j = 0; j < valueList.size(); j++) {
									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										// 获得显示字体的大小
										String value = valueList.get(j).toString();
										value = value.replaceAll("@#@", "<br>");
										value = value.replaceAll("#@#", "<br>");
										value = value.replaceAll(" ", "&nbsp;&nbsp;");
										// fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()),
										// widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"");
										// fontsize=mc.ReDrawLitterRect(widthn,heights,valueList.get(j)!=null?valueList.get(j).toString():"",Integer.parseInt(rgrid.getFontsize()),valueList.get(j)!=null?valueList.get(j).toString():"",disting_pt,rgrid.getField_type(),rgrid.getSlope());
										fontsize = mc.ReDrawLitterRect(widthn, heights, valueList,
												Integer.parseInt(rgrid.getFontsize()));
										out.append("<font id=grid_value_" + rgrid.getGridno() + " color=\"" + this.color
												+ "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										out.append(value != null ? value : "");
										out.append("</font>");
										out.append("<br>");
									} else {
										out.append("<br>");
									}
								}
							} else {
								out.append("&nbsp;");
							}
						} else if ("1".equals(rgrid.getSubflag()))// 子集
						{
							rgrid.setPlan_id(this.plan_id);
							String tmp = viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
									widthn, heights);
							// System.out.println(tmp);
							out.append(tmp);

						}

					} else if ("Z".equals(rgrid.getFlag()) && !"1".equals(rgrid.getSubflag())) { // A人员库
						byte nFlag = 6; // 0表示人员库
						ArrayList valueList = null;
						valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
						if (valueList != null && !valueList.isEmpty()) {
							if (valueList.size() == 1) {
								fontsize = mc.ReDrawLitterRect(widthn, heights, valueList,
										Integer.parseInt(rgrid.getFontsize()));
								out.append("<font id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
										+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
										+ ";font-size:" + fontsize + "pt\">");
								out.append(valueList.get(0) != null && valueList.get(0).toString().trim().length() > 0
										? valueList.get(0).toString()
										: "&nbsp;");
								out.append("</font>");
							} else {
								// int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
								int heigh = Integer.parseInt(rgrid.getFontsize())
										+ Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(), "0.72", 0));
								;
								for (int j = 0; j < valueList.size(); j++) {

									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										fontsize = mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(),
												Integer.parseInt(rgrid.getFontsize()));
										out.append("<font id=grid_value_" + rgrid.getGridno() + "_" + j + "  color=\""
												+ this.color + "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										if (j > 0)
											out.append("<br>");
										out.append(valueList.get(j) != null
												&& valueList.get(j).toString().trim().length() > 0
														? valueList.get(j).toString()
														: "&nbsp;");
										out.append("</font>");
									} else {
										out.append("<br>");
									}

								}
							}
						} else {
							out.append("&nbsp;");
						}
					} else if ("Z".equals(rgrid.getFlag()) && "1".equals(rgrid.getSubflag())) {
						byte nFlag = 6;
						out.append(viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
								widthn, heights));
					} else if ("D".equals(rgrid.getFlag())) { // 指标公式
						ArrayList valueList = card.getTextValueForCexpress(userbase, conn, card, rgrid, userview,
								alUsedFields, inforkind, this.nid, this.plan_id);
						if (valueList != null && !valueList.isEmpty()) {
							if (valueList.size() == 1) {
								fontsize = mc.ReDrawLitterRect(widthn, heights, valueList,
										Integer.parseInt(rgrid.getFontsize()));
								out.append("<font id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
										+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
										+ ";font-size:" + fontsize + "pt\">");
								out.append(valueList.get(0) != null && valueList.get(0).toString().trim().length() > 0
										? valueList.get(0).toString()
										: "&nbsp;");
								out.append("</font>");
							} else {
								// int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
								int heigh = Integer.parseInt(rgrid.getFontsize())
										+ Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(), "0.72", 0));
								;
								for (int j = 0; j < valueList.size(); j++) {

									if (valueList.get(j) != null && valueList.get(j).toString() != null) {
										fontsize = mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(),
												Integer.parseInt(rgrid.getFontsize()));
										out.append("<font  id=grid_value_" + rgrid.getGridno() + "_" + j + " color=\""
												+ this.color + "\" style=\"font-weight:" + fontweight + ";font-family:"
												+ rgrid.getFontName() + ";font-size:" + fontsize + "pt\">");
										if (j > 0)
											out.append("<br>");
										out.append(valueList.get(j) != null
												&& valueList.get(j).toString().trim().length() > 0
														? valueList.get(j).toString()
														: "&nbsp;");
										out.append("</font>");
									} else {
										out.append("<br>");
									}

								}
							}
						} else {
							out.append("&nbsp;");
						}

					} else if ("E".equals(rgrid.getFlag())) { // 基准岗位
						byte nFlag = 7;
						if ("1".equals(rgrid.getSubflag())) {
							out.append(viewSubclass(rgrid, conn, userview, year, month, ctimes, userbase, nid, nFlag,
									widthn, heights));
						} else {
							ArrayList valueList = null;
							valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
							out.append(outputGridContent(valueList, rgrid, mc, widthn, heights, fontweight));
						}
					} else if ("C".equals(rgrid.getFlag())) {
						out.append("<font id=grid_value_" + rgrid.getGridno() + "  color=\"" + this.color
								+ "\" style=\"font-weight:" + fontweight + ";font-family:" + rgrid.getFontName()
								+ ";font-size:" + rgrid.getFontsize() + "pt\">");
						out.append(card.getFormulaValue(rgrid));
						out.append("</font>");
					} else {
						out.append("&nbsp;");
					}
					if ("1".equalsIgnoreCase(ykcard_auto))
						out.append("</div>");
					if (!"1".equalsIgnoreCase(ykcard_auto) && "1".equals(rgrid.getSubflag())) {
						out.append("</div>");
						out.append("</div>");

					}
					out.append("</td>");
					out.append("</tr>");
					out.append("</table>");

					/*
					 * } else if(rgrid.getFlag().equals("C")){
					 * 
					 * }
					 */

				}
			}
	     }catch(Exception e){
	     	e.printStackTrace();
	     }
	   
/*	去除单独处理计算公式代码    //显示各个单元个得结束
	    //显示各个单元个要格式化数据的数据的开始
	     RGridView rgridc;
	    try
		 {
	     	if(rgrids!=null&&!rgrids.isEmpty())
	     	{
	     		for(int i=0;i<rgrids.size();i++)
	     		{
	     			rgridc=(RGridView)rgrids.get(i);  
	     			 if(rgridc.getFlag().equals("C")){//计算结果(单元格公式)
				         leftn=(int)Float.parseFloat(rgridc.getRleft())+this.lmargin;
				         topn=(int)Float.parseFloat(rgridc.getRtop())+this.tmargin;
				         if("MSIE".equals(browser)||
	   		                     "Safari".equals(browser)&&browserMajorVer<537)//IE 与 Safari 浏览器 跟谷歌浏览器显示宽度不一致  【26626	首开：手机app登陆报错】
				        	 widthn=(int)Float.parseFloat(rgridc.getRwidth()) + 1;
				         else
				        	 widthn=(int)Float.parseFloat(rgridc.getRwidth()) - 1;
				         heights=(int)Float.parseFloat(rgridc.getRheight()) + 1;
	                   fontweight=rgridc.getFonteffect();                  
	                   if(fontweight !=null && fontweight.equals("2"))
	 	                 fontweight="bold";
	 	               else
	 	                 fontweight="normal";
	                   //获得适应单元格大小的字体大小
	                   fontsize=mc.ReDrawLitterRect(widthn,heights,rgridc.getCHz(),Integer.parseInt(rgridc.getFontsize()),hz,"1024",rgridc.getField_type(),rgridc.getSlope());
	                   //fontsize=mc.getFitFontSize(Integer.parseInt(rgridc.getFontsize()), widthn,heights,rgridc.getCHz());
	                   hz=rgridc.getCHz();                   
	                   String[] align=mc.getAlign(rgridc.getAlign());
	                  out.append("<table  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"\" style=\"table-layout:fixed;BACKGROUND-COLOR:transparent;position:absolute;top:" + topn+ "px;left:"+ leftn 
	   				       + "px;width:" + widthn + "px;height:" + heights + "px;\">");
	    	           out.append("<tr>");
	    	           out.append("<td style=\"word-wrap : break-word; overflow:hidden; \" class=\"" + new MadeCardCellLine().GetCardCellLineShowcss(rgridc.getL(),rgridc.getR(),rgridc.getT(),rgridc.getB())+ "\" valign=\"" + align[1] + "\" align=\"" + align[0] + "\"  width=\"" + widthn + "\" height=\"" + heights + "\">");
	    	           out.append("<font  color=\""+this.color+"\" style=\"font-weight:" +
	    	           		fontweight + ";font-family:"+rgridc.getFontName()+";font-size:" + fontsize + "pt\">"); 
	    	           //getFormulaValue()函数是格式化显示数据的函数  
	    	           out.append(card.getFormulaValue(rgridc));  
	    	           out.append("</font>");    
	    	           out.append("</td>");    
	    	           out.append("</tr>"); 
	    	           out.append("</table>");              
	     			 }
	     		}     		
	     	}
	     }catch(Exception e)
		 {
	     	e.printStackTrace();
	     }*/
	     
	     
	     
	     
	     List rpageList=encap.getRpage(tabid,pageid,conn);                            //获得页面title的List的对象
	     //显示各个单元个要格式化数据的数据的结束
	     try{
	      	//显示页面标头等信息的开始
	        if(rpageList!=null&&!rpageList.isEmpty()){
	          for(int i=0;i<rpageList.size();i++){
	          	RPageView rpage=(RPageView)rpageList.get(i);
	          	fontsize=Integer.parseInt(rpage.getFontsize());
	          	fontweight=rpage.getFonteffect();    
	          	if(StringUtils.isEmpty(fontweight)) {
	          		fontweight="1";
	          	}
	          	if(rpage.getFlag()!=6)
	          	{
	          		if("2".equals(fontweight)) {
	          			fontweight="bold";
	          		}else if("3".equals(fontweight)){//倾斜
	          			fontweight="normal;font-style:italic;";
	          		}else if("4".equals(fontweight)){//加粗倾斜
	          			fontweight="bold;font-style:italic;";
	          		}else {
	          			fontweight="normal";
	          		}
	          		
	                   int left_n=Integer.parseInt(rpage.getRleft())+this.lmargin;
	                   int top_n=Integer.parseInt(rpage.getRtop())+this.tmargin;
	                   out.append("<table style=\"position:absolute;top:" + top_n + "px;left:" + left_n + "px\">");

	                /*if(disting_pt.equals("800"))
	      	        {  
	      	           fontsize=Math.round(((float)(fontsize * 800))/1024);
	      	           int left_n=Integer.parseInt(rpage.getRleft("1"))+this.lmargin;
	      	           out.println("<table style=\"position:absolute;top:" + ( Integer.parseInt(rpage.getRtop("1"))+this.tmargin)+ "px;left:" + left_n + "px\">");
	      	        }
	                else
	                {
	                }*/               
	                out.append("<tr>");
	   		        out.append("<td valign=\"middle\" align=\"left\" nowrap>");
	   		        out.append("<font  style=\"font-weight:" + fontweight + ";font-family:"+rpage.getFontname()+";font-size:" + fontsize + "pt\";>");   
	   		        String title = encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,userbase,tabid,this.inforkind,rpage.getExtendAttr());
	   		        // 处理特殊字符
	   		        title = title.replaceAll(" ", "&nbsp;");
	   		        title = title.replaceAll("\r\n", "<br>");
	   		        out.append(title);
	   		        out.append("</font>");
	   		        out.append("</td>");
	   		        out.append("</tr>");
	   		        out.append("</table>");
	          	}	 
	    	 }
	       } 
	      }catch(Exception e)
	 	 {
	         e.printStackTrace();	
	      } 
	     return out;
		}
	
	/*****对子集的显示*****/
	public String viewSubclass(RGridView rgrid,Connection conn,UserView userview,int statYear,           //年
			int statMonth,          //月
			int ctimes,             //次数
			String userbase,
			String nId,byte nFlag,int fact_width,int fact_height)
	{
		StringBuffer html=new StringBuffer();
		String sub_domain=rgrid.getSub_domain();
		if(sub_domain==null||sub_domain.length()<=0)
			return "";
		YkcardViewSubclass ykcardViewSubclass=new YkcardViewSubclass(conn,year,month,ctimes,userbase,nid,userview,true);
		ykcardViewSubclass.setFenlei_type(this.fenlei_type);
		ykcardViewSubclass.setFact_width(fact_width);
		ykcardViewSubclass.setFact_height(fact_height);
        ykcardViewSubclass.setFieldpurv(fieldpurv);
		ykcardViewSubclass.setNFlag(nFlag);
		ykcardViewSubclass.getXmlSubdomain(rgrid.getSub_domain(), rgrid);
		ykcardViewSubclass.setDisplay_zero(this.display_zero);
		ykcardViewSubclass.setBizDate(this.bizDate);
		ykcardViewSubclass.setYkcard_auto("1".equals(ykcard_auto)?true:false);
		ArrayList fieldlist=ykcardViewSubclass.getFieldList();//fieldList 在执行getXmlSubdomain（）此方法时已经将list的指标查出 可以直接使用 changxy
		ykcardViewSubclass.setSearchDateSql(getSearchDatasql(fieldlist));//拼接sql按日期查询
		html.append(ykcardViewSubclass.viewSubClassHtml(inforkind,userbase,conn,userview,rgrid,"1024",nFlag));
		if(html.length()<=0)
            html.append("<br>");
		return html.toString();
	}
	
	/***
	 * 子集查询日期条件  由于使用的参数偏多故在此类中添加此方法 需要的指标集合从YkcardViewSubclass中先取出 
	 * changxy 
	 * 20160928
	 */
	private String getSearchDatasql(ArrayList fieldlist){
		boolean flag=false;
		String str=null;
		for (int i = 0; i < fieldlist.size(); i++) {
			if(fieldlist.get(i).toString()!=null&&fieldlist.get(i).toString().length()>2)
			if(fieldlist.get(i).toString()!=null&& "z0".equalsIgnoreCase(fieldlist.get(i).toString().substring(fieldlist.get(i).toString().length()-2, fieldlist.get(i).toString().length())))//指标中有没有日期标识，如果有则按照查询类型拼sql
			{
				flag=true;
				str=fieldlist.get(i).toString();
				break;
			}	
		}
		String sql=null;
		StringBuffer sbf=new StringBuffer();
		if(flag&&str!=null){//不同查询使用的年月字段不一样，
			
			switch (this.queryflag) {
			case 1://年月
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.year);//月份使用年月
				if(this.month!=13)
					sbf.append("and "+Sql_switcher.month(str)+"="+this.month);
				break;
			case 2://时间段
				sbf.append(" and "+Sql_switcher.dateToChar(str)+">= '"+this.startDate+"' and "+Sql_switcher.dateToChar(str)+"<='"+this.endDate+"'");
				break;
			case 3://季度
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.year);//季度使用年份
				switch (this.season) {
				case 1:
					sbf.append(" and "+Sql_switcher.month(str)+">=1 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=3 ");					
					break;
				case 2:
					sbf.append(" and "+Sql_switcher.month(str)+">=4 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=6 ");
					break;
				case 3:
					sbf.append(" and "+Sql_switcher.month(str)+">=7 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=9 ");
					break;
				case 4:
					sbf.append(" and "+Sql_switcher.month(str)+">=10 ");					
					sbf.append(" and "+Sql_switcher.month(str)+"<=12 ");
					break;
				}
				break;
			case 4://年
				sbf.append(" and "+Sql_switcher.year(str)+"="+this.year);
				break;
			}
		}
		if("0".equals(queryflag))//按条件查询不需拼接日期sql 20161011 changxy
			return "";
		return sbf.toString();
	}
	
	private StringBuffer outputGridContent(ArrayList valueList, RGridView rgrid, MadeFontsizeToCell mc, 
			int widthn, int heights, String fontweight){
		StringBuffer out=new StringBuffer();
		int fontsize;
		try{
			if(valueList !=null &&!valueList.isEmpty())
	        {
	              if(valueList.size()==1)
	              {
	            	  fontsize=mc.ReDrawLitterRect(widthn, heights, valueList, Integer.parseInt(rgrid.getFontsize()));
	                  out.append("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
	                  out.append(valueList.get(0)!=null&&valueList.get(0).toString().trim().length()>0?valueList.get(0).toString():"&nbsp;");	 	                 
	                  out.append("</font>");
	              }else
	              {
	            	  //int fH=(int)Math.round(Integer.parseInt(rgrid.getFontsize())*0.8);
	            	  int heigh=Integer.parseInt(rgrid.getFontsize())+Integer.parseInt(PubFunc.multiple(rgrid.getFontsize(),"0.72", 0));;
	            	  for(int j=0;j<valueList.size();j++)
	                  {
	                	  
	                      if(valueList.get(j)!=null && valueList.get(j).toString() !=null)
	                      {
	                    	  fontsize=mc.ReOneRowDrawLitterRect(widthn, heigh, valueList.get(j).toString(), Integer.parseInt(rgrid.getFontsize()));
	                          out.append("<font  color=\""+this.color+"\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");  
	                        if(j>0)
	                        	 out.append("<br>");	                                    
	                    	 out.append(valueList.get(j)!=null&&valueList.get(j).toString().trim().length()>0?valueList.get(j).toString():"&nbsp;");	 	                                	
	                    	 out.append("</font>");
	                      }else{
	                        out.append("<br>");      
	                       }
	                      
	                  }	
	              }
	         }else{
	        	out.append("&nbsp;");    
	         }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @param userbase
	 * @param conn
	 * @param card
	 * @param rgrid
	 * @param userview
	 * @param nFlag
	 * @param valueList
	 * @param fieldset
	 * @return
	 * @throws Exception
	 */
	private ArrayList getTextValue(String userbase, Connection conn, GetCardCellValue card,RGridView rgrid, UserView userview, byte nFlag, ArrayList valueList, DynaBean fieldset) {
		//获得单元格的内容值
		String changeflag="0";
		String field_priv=this.fieldpurv;
		if(fieldset!=null)
			changeflag=fieldset.get("changeflag").toString();
		else{
			if("1".equalsIgnoreCase(rgrid.getIsView())){
				//定义视图是否是年月变化
				changeflag=card.viewIsChangeflag(rgrid, conn);
				field_priv="1";//视图指标默认指标权限
			}
		}
		
		try{
		  if(0==queryflag) {
			  if(StringUtils.isNotEmpty(nid))
				  valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,Integer.parseInt(changeflag),year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
			  else
				  valueList=new ArrayList();
		  }else if(queryflag==1)
		  {
			  if(inforkind!=null&& "5".equals(inforkind))
			  {
				   StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
				   String table_name=statisticPlan.getPER_RESULT_TableName(this.plan_id);
				   rgrid.setCSetName(table_name);
				   rgrid.setPlan_id(this.plan_id);
				   if(StringUtils.isNotEmpty(nid))
					   valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,0,year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
				   else
					   valueList=new ArrayList();
			  }else
			  {
				  if(StringUtils.isNotEmpty(nid))
					  valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,Integer.parseInt(changeflag),year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
				  else
					  valueList=new ArrayList();
			  }
		  }else if(queryflag==2) {
			  if(StringUtils.isNotEmpty(nid))
				  valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,Integer.parseInt(changeflag),year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
			  else
				  valueList=new ArrayList();
		  }else if(queryflag==3) {
			  if(StringUtils.isNotEmpty(nid))
				  valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,Integer.parseInt(changeflag),year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
			  else
				  valueList=new ArrayList();
		  }else if(queryflag==4) {
			  if(StringUtils.isNotEmpty(nid))
				  valueList=card.GetFldValue(inforkind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflag,Integer.parseInt(changeflag),year,month,ctimes,nid,userview,startDate,endDate,season,conn,field_priv);
			  else
				  valueList=new ArrayList();
		   }
		  }catch(Exception e){
			   //liuy 2014-10-20 在这里去掉打印异常方法，如果有指标未构库或被删除，valueList会为null，后台会一直打印错误信息
		   }
		 return valueList;
	}
	/***
	 * 
	 * */
	private String getCssLineHeight(RGridView rgrid) {
	    String s = "";
	    if("H".equals(rgrid.getFlag())){
	        int fsize = Integer.parseInt(rgrid.getFontsize());
	        int feffect = Integer.parseInt(rgrid.getFonteffect());  // FIXME 转为Font.PLAIN, BOLD, ITALIC
            Font font = new Font(rgrid.getFontName(), feffect, fsize);
            BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = gg.createGraphics(); // 获得画布
            g.setFont(font);
            int aheight = g.getFontMetrics().getHeight(); // 每一行字的高度
            int awidth = g.getFontMetrics().charWidth('汉');//fontSize;  // 汉字宽
/*
    浏览器计算和分配行间距的方法
　　间距 = "line-height" – "font-size";
　　文本上下分配大小 = 间距/2;
　　字号 = 12px; line-height:3;
　　间距 = 3*12 – 12 = 24(px);
　　文本上下分配大小 = 24/2 = 12(px)
    在大多数浏览器中默认行高百分比大约是 110% 到 120%；数值大约为1, IE为1.14, IE0.92相当于没边距
*/
            //liuy 2014-11-17 819:任免管理：任免管理审批审批表在后台画好的表预演等都正确但是在前台显示任免理由就不完整 start
            double arowspace = (aheight * 0.14);
    	    String v = rgrid.getCHz();
    	    if(v == null)
    	        v = "";
    	    String[] lst = v.split("`");
    	    double vheight = lst.length * aheight + (lst.length) * arowspace*2;
    	    double difference= vheight - Float.parseFloat(rgrid.getRheight());
    	    if(difference>-1&&difference<=3) {
    	        // 任`免`理`由, 字体：宋体 16号, 单元格高 80, line-height: normal会显示不全, 170%或0.95 正合适
    	        s = "line-height: 1.2;";  // 去掉行间距
    	    }else if(difference>3&&difference<=10){
    	    	s = "line-height: 1;";
    	    }else if(difference>10){
    	    	s = "line-height: 0.93;";
    	    }
    	    //liuy end
	    }
	    return s;
	}
	/**
	 * 标题插入图片
	 * */
	private String printTitleImage(Connection conn,int tabid,int pageid)
	{
		int leftn=0;
		int topn=0;
		int widthn=0;
		int heights=0;
		DataEncapsulation encap=new DataEncapsulation();   
		List rpageList=encap.getRpage(tabid,pageid,conn);                            //获得页面title的List的对象
		/**
		 * <image>
           <ext>.jpg</ext>
           <stretch>拉伸True</stretch>
           <transparent>透明False</transparent>
           <proportional>保持比例False</proportional>
           </image>
         */
	     //显示各个单元个要格式化数据的数据的结束
		StringBuffer out=new StringBuffer();
	    try{
	      	//显示页面标头等信息的开始
	          if(rpageList!=null&&!rpageList.isEmpty()){
	             for(int i=0;i<rpageList.size();i++)
	             {
	          	    RPageView rpage=(RPageView)rpageList.get(i);
	          	    if(rpage.getFlag()==6)
	          	    {

	   		             leftn=(int)Float.parseFloat(rpage.getRleft())+this.lmargin;
	   		             topn=(int)Float.parseFloat(rpage.getRtop())+this.tmargin;
	   		             widthn=(int)Float.parseFloat(rpage.getRwidth()) + 1;
	   		             heights=(int)Float.parseFloat(rpage.getRheight()) +1;
   	        
	              		String extendattr=rpage.getExtendAttr();
	        			if(extendattr!=null&&extendattr.length()>0)
	        			{
	        				String ext="";
	        				String stretch="";
	        				String transparent="";
	        				String proportional="";
	        				if(extendattr.indexOf("<format>")!=-1&&extendattr.indexOf("</format>")!=-1)
	        				{
	        					ext=extendattr.substring(extendattr.indexOf("<ext>")+5,extendattr.indexOf("</ext>"));
	        				}
	        				if(extendattr.indexOf("<stretch>")!=-1&&extendattr.indexOf("</stretch>")!=-1)
	        				{
	        					stretch=extendattr.substring(extendattr.indexOf("<stretch>")+9,extendattr.indexOf("</stretch>"));
	        				}
	        				if(extendattr.indexOf("<transparent>")!=-1&&extendattr.indexOf("</transparent>")!=-1)
	        				{
	        					transparent=extendattr.substring(extendattr.indexOf("<transparent>")+13,extendattr.indexOf("</transparent>"));
	        				}
	        				if(extendattr.indexOf("<proportional>")!=-1&&extendattr.indexOf("</proportional>")!=-1)
	        				{
	        					proportional=extendattr.substring(extendattr.indexOf("<proportional>")+14,extendattr.indexOf("</proportional>"));
	        				}
	        				String url="/servlet/vfsservlet?fileid=";
//	        				if("zp_noticetemplate_flag".equals(cardtype))
//	   	                		url= "/servlet/DisplayOleContent?mobile=zp_noticetemplate_flag&filename=";
//	   	                	else
//	   	                		url="/servlet/DisplayOleContent?filename=";
	        				//DisplayOleContent 现有程序退出后自动销毁文件
	                        String filename=ServletUtilities.createTitlePhotoFile(tabid,pageid,rpage.getGridno(),ext,null);
	                        if(filename!=null && filename.length()>0){
	                        	filename = PubFunc.encrypt(filename);
	                             String imageurl="file:///"+System.getProperty("java.io.tmpdir")+"\\" + filename;
	                             out.append("<div style=\"position:absolute;top:" + topn + "px;left:" + leftn+"px;");
	                        	 out.append("width:" + widthn + "px;height:" + heights + "px;");
	                        	 out.append("\">");
	                        	 out.append("<img src=\"" + url + filename + "&fromjavafolder=true\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + "  >");//style=\"filter:alpha(opacity=50);\"
	                        	 out.append("</div>");
	                             //out.println("<img src=\"" + url + filename + "\" height=" + String.valueOf(heights-5) + " width=" + String.valueOf(widthn-3) + ">");
	                        }
	        			}
	          	    }
	          }
	        }
	     }catch(Exception e)
	     {
	        	e.printStackTrace();
	     }
	     return out.toString();
      	
	}
	
	/**
	 * 页边据
	 * @param conn
	 */
	private void getMargin(Connection conn)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select Tmargin,bmargin,lmargin,rmargin  from rname where tabid='"+this.tabid+"'");		
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		double tm_f=0;
		double bm_f=0;
		double lm_f=0;
		double rm_f=0;
		try
		{
			
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				tm_f=rs.getDouble("Tmargin");
				bm_f=rs.getDouble("bmargin");
				lm_f=rs.getDouble("lmargin");
				rm_f=rs.getDouble("rmargin");
				tm_f=tm_f/0.24;
				bm_f=bm_f/0.24;
				lm_f=lm_f/0.24;
				rm_f=rm_f/0.24;
				this.tmargin=(int)Math.round(tm_f);
				this.bmargin=(int)Math.round(bm_f);
				this.lmargin=(int)Math.round(lm_f);
				this.rmargin=(int)Math.round(rm_f);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * pagelist: 0 height 1 width
	 * @param conn
	 * @param tabid
	 * @return
	 */
	private ArrayList<Integer> getTable_height(Connection conn,int  tabid) {
		ArrayList<Integer> pagelist=new ArrayList<Integer>();
    	ContentDAO dao=new ContentDAO(conn);
    	int height=0;
    	int width=0;
    	ArrayList<Integer> list=new ArrayList<Integer>();
    	list.add(tabid);
    	String sql="select paperH,paperori,paperW from rname where tabid=?";
     	try
     	{
     		RowSet rs=dao.search(sql,list);
     		float h=0;
     		float w=0;
     		if(rs.next())
     		{
     			String ori=rs.getString("paperori");
     			if(ori==null||ori.length()<=0)// 0 横分 1 纵分
     				ori="1";
     			if("1".equals(ori)) {
     				h=rs.getFloat("paperH");
     				w=rs.getFloat("paperW");
     			}else {
     				w=rs.getFloat("paperH");
     				h=rs.getFloat("paperW");
     			}
     		}
     			
     		h=h*0.0393701f;
     		h=h*96f;
     		w=w*0.0393701f;
     		w=w*96f;
     		height=(int)h;
     		width=(int)w;
     		
     	}catch(Exception e)
     	{
     		e.printStackTrace();
     	}
     	pagelist.add(height);
 		pagelist.add(width);
		return pagelist;
	}
	
	 private int getTable_width(Connection conn,String  tabid) {
			
		   int  width=0;
		   ContentDAO dao=new ContentDAO(conn);
		   String sql="select paperH,paperori,paperW from rname where tabid='"+tabid+"'";
	     	try
	     	{
	     		RowSet rs=dao.search(sql);
	     		float w=0;
	     		if(rs.next())
	     		{
	     			String ori=rs.getString("paperori");
	     			if(ori==null||ori.length()<=0)
	     				ori="1";
	     			if("2".equals(ori))
	     				w=rs.getFloat("paperH");
	     			else
	     				w=rs.getFloat("paperW");
	     		}
	     		w=w*0.0393701f;
	     		w=w*96f;
	     		width=(int)w;
	     	}catch(Exception e)
	     	{
	     		e.printStackTrace();
	     	}
			return width;
		}
	
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public int getBrowserMajorVer() {
		return browserMajorVer;
	}
	public void setBrowserMajorVer(int browserMajorVer) {
		this.browserMajorVer = browserMajorVer;
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
	
	
	
	public String getInforkind() {
		return inforkind;
	}
	public void setInforkind(String inforkind) {
		this.inforkind = inforkind;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public int getTabid() {
		return tabid;
	}
	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	public int getQueryflag() {
		return queryflag;
	}
	public void setQueryflag(int queryflag) {
		this.queryflag = queryflag;
	}
	public String getFieldpurv() {
		return fieldpurv;
	}
	public void setFieldpurv(String fieldpurv) {
		this.fieldpurv = fieldpurv;
	}
	public String getBizDate() {
		return bizDate;
	}
	public void setBizDate(String bizDate) {
		this.bizDate = bizDate;
	}
	public String getPlan_id() {
		return plan_id;
	}
	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getCtimes() {
		return ctimes;
	}
	public void setCtimes(int ctimes) {
		this.ctimes = ctimes;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(String isMobile) {
		this.isMobile = isMobile;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	} 
	
	
}
