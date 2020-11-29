package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class HmusterPdf {
	private Connection conn = null;
    private float  precent=0.24f;
	private float lt = 0; // 表格整体往左靠，需减的边距像素
	private float tp = 0; // 顶边距象素
	private String privConditionStr=" ";
	private String modelFlag="";
	private UserView userView=null;
	private HashMap gridNoMap=null;
	private String isGroupPoint=null;
	private String groupPoint="";
	private String a0100="";
	private String[] fzhj=null;
	private String[] zj=null;
	private boolean isyxj=false;  
	private boolean isylj=false;
	private boolean isfzhj=false;
	private boolean iszj=false;
	private float   paperW=0f;
	private float   paperH=0f;
	private int paperOri=0;
	private float left_space=30;
	private float top_space=20;
	private RecordVo musterVo=null;
	private String isGroupNoPage="0";  //分组不分页
	private String tabID="";
	private String dataarea="";
	private String itemHeArr="";
	private String groupNcode="";
	private String sql="";
	private String column="";
	private String isGroupPoint2="0";
	private String groupPoint2="";
	private boolean isGroupTerm2=false;
	private boolean isGroupV2=false;
	private String groupCount="0";//我的薪酬中的分组合计标志
	private HmusterViewBo hmusterViewBo=null;
	private String platform;
	private String yearmonth="";
	private String showPartJob="false";//是否显示兼职人员
	private HashMap topDateTitleMap = new HashMap();//高级花名册日期型上标题Map集合
	String isGroupedSerials="0";//按组显示序号
	public HashMap getTopDateTitleMap() {
		return topDateTitleMap;
	}
	
	public void setTopDateTitleMap(HashMap topDateTitleMap) {
		this.topDateTitleMap = topDateTitleMap;
	}
	public void setShowPartJob(String showPartJob)
	{
		this.showPartJob=showPartJob;
	}
	public HmusterPdf(Connection conn) {
		this.conn = conn;
	}

	public HmusterPdf() {

	}
	public boolean isHaveGroup2(String tableName)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search("select * from "+tableName);
			ResultSetMetaData data=rs.getMetaData();
			String group2="groupv2";
			for(int i=1;i<=data.getColumnCount();i++)
			{
				String columnName=data.getColumnName(i).toLowerCase();
				if(group2.toLowerCase().equalsIgnoreCase(columnName))
				{
					flag=true;
					break;
				}
				else {
                    continue;
                }
			}
		}
		catch(Exception e)
		{
			flag=false;
		}
		
		return flag;
	}
	
	
//	取得权限控制语句
	public String getPrivCondition(UserView userView,String infor_Flag,String dbpre)
	{
		StringBuffer privConditionStr=new StringBuffer("");
		try
		{
			if (!userView.isSuper_admin()) {
				if ("1".equals(infor_Flag)&&!"ALL".equals(dbpre)) // 人员库
				{
					String conditionSql = " select "+dbpre+"A01.A0100 "+ userView.getPrivSQLExpression(dbpre, true);
					/**加入兼职人员*/
					if("true".equalsIgnoreCase(this.showPartJob))
					{
						HmusterBo bo = new HmusterBo(this.conn);
			    		String parttimerSQL =""; 
			    		 if(userView.getManagePrivCodeValue()!=null) {
                             parttimerSQL=bo.getQueryFromPartLike(userView, dbpre, userView.getManagePrivCodeValue());
                         }
				    	 if(parttimerSQL!=null&&!"".equals(parttimerSQL))
				    	 {
					    	 conditionSql+=" or ("+parttimerSQL+")";
				    	 }
					}
					privConditionStr.append(" where A0100 in (" + conditionSql + " )");
				}
				String codesetid=userView.getManagePrivCode();
				String codeValue=userView.getManagePrivCodeValue();
				if ("2".equals(infor_Flag)) // 2：机构
				{
					String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  codeitemid like '"
						+ codeValue+"%'";
					privConditionStr.append(" where b0110 in (" + conditionSql + " )");
					
				}
				if ("3".equals(infor_Flag)) //  3：职位
				{
					String conditionSql = " select codeitemid from organization  where codesetid='@K' and  codeitemid like '"
						+ codeValue+"%'";
					privConditionStr.append(" where e01a1 in (" + conditionSql + " )");
				
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return privConditionStr.toString();
	}
	
	
	
	public RecordVo getMusterRecordVo(String tabid)
	{
		RecordVo musterVo=new RecordVo("muster_name");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			musterVo.setInt("tabid",Integer.parseInt(tabid));
			musterVo=dao.findByPrimaryKey(musterVo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return musterVo;
	}
	

	public String executePdf(String infor_Flag, String tabid,
			String isGroupPoint, String groupPoint, String tableName,
			String pageRows, String currpage, String isAutoCount,
			String zeroPrint, String emptyRow, String column, String pix,
			String columnLine, String user, String dbpre, String history,
			String year, String month, String count, UserView userView,
			String printGrid,String modelFlag,String platform) throws GeneralException {
		this.platform=platform;
		this.modelFlag=modelFlag;
		this.userView=userView;
		this.isGroupPoint=isGroupPoint;
		this.tabID = tabid;
		if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)) {
            this.privConditionStr=getPrivCondition(userView,infor_Flag,dbpre);
        }
		String url = "";
		this.musterVo=getMusterRecordVo(tabid);
		String musterName=this.musterVo.getString("cname");
		this.left_space=Float.parseFloat(String.valueOf(this.musterVo.getDouble("lmargin")))/precent;
		this.top_space=Float.parseFloat(String.valueOf(this.musterVo.getDouble("tmargin")))/precent;
		HmusterViewBo hmusterViewBo = new HmusterViewBo(this.conn,tabid);
		hmusterViewBo.setInfor_Flag(infor_Flag);
		HmusterBo hmusterBo = new HmusterBo(this.conn);
		HmusterXML hmxml = new HmusterXML(this.conn,tabID);
		
		String groupedSerials = hmxml.getValue(HmusterXML.GROUPEDSERIALS);
		if("1".equals(groupedSerials)) {
            isGroupedSerials="1";
        } else {
            isGroupedSerials="0";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;

		ArrayList list3 = new ArrayList(); // 哪些列有页小计或累计
		int nOperation = 0; // 汇总标示
		float tableBodyWidth = 0; // 表体的宽度
		ArrayList bottomnList = null; // 表头最底端的列
		ArrayList tableHeaderList = null; // 表头所有列的集合

		float[] ltPix = null;
		float tableheaderHeight = 0; // 表头的高
		boolean isPhoto = false; // 表体是否显示照片
		ArrayList photoList = new ArrayList();

		/* 取得表头最底端的列 && 求得表底是否有页小计和累计 && 哪些列有页小计或累计 &&得到表体的宽度（单位：像素） &&表头的所有列集合 */
		ArrayList aList = getBottomnList(hmusterViewBo, tabid);
		bottomnList = (ArrayList) aList.get(0); // 取得表头最底端的列
		nOperation = ((Integer) aList.get(1)).intValue();
		if("0".equals(column)) {
            list3 = (ArrayList) aList.get(2); // 哪些列有页小计或累计
        } else {
            list3 = (ArrayList) aList.get(7); // 哪些列有页小计或累计
        }
		
		tableBodyWidth = ((Float) aList.get(3)).floatValue(); // 得到表体的宽度（单位：像素）
		tableHeaderList = (ArrayList) aList.get(4);
		tableheaderHeight = ((Float) aList.get(5)).floatValue(); // 表头的高度
		ltPix = (float[]) aList.get(6);
		float r_bottomn = ((Float) aList.get(8)).floatValue();
		String[] temp0 = (String[]) tableHeaderList.get(0);
		float[] columnDefinitionSize = new float[bottomnList.size()]; // 表体列宽
		for (int i = 0; i < bottomnList.size(); i++) {
			String[] temp = (String[]) bottomnList.get(i);
			columnDefinitionSize[i] = Float.parseFloat(temp[4]);
		}

		paperOri = Integer.parseInt(temp0[18]); // 纸张方向
		paperW = Float.parseFloat(hmusterViewBo.round(String.valueOf(Float.parseFloat(temp0[19]) / precent), 0)); // 页宽
		paperH = Float.parseFloat(hmusterViewBo.round(String.valueOf(Float.parseFloat(temp0[20]) / precent), 0)); // 页高

		Document document = null;
		if (paperOri == 2) // 纸张横向
		{
			Rectangle pageSize = new Rectangle(paperH, paperW); // 自定义纸张大小
			document = new Document(pageSize);
		} else {
			Rectangle pageSize = new Rectangle(paperW, paperH); // 自定义纸张大小
			document = new Document(pageSize);
		}
		PdfWriter writer = null;
		try {
			//35065	员工管理/高级花名册，导出PDF，建议文件名称改为“花名册的名称+_+用户名.pdf”
			//V77员工管理：涉及到导出的（pdf、word、excel、zip等等）命名，需要和别的模块一样，统一成： 登陆用户_相应信息
			url =this.userView.getUserName()+"_"+PubFunc.hireKeyWord_filter(musterName)+ ".pdf";//musterName + "_" + this.userView.getUserName() + ".pdf";
			writer = PdfWriter.getInstance(document,
					new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+  url));

			float page_width = document.getPageSize().width();
			float page_height = document.getPageSize().height();
			document.open();

			String tableFontStyle = "select FontName,FontEffect,FontSize  from muster_name where tabid="+ tabid;
			String[] fontStyle = new String[3];
			rowSet = dao.search(tableFontStyle);
			if (rowSet.next()) {
				fontStyle[0] = rowSet.getString("FontName");
				fontStyle[1] = rowSet.getString("FontEffect");
				fontStyle[2] = rowSet.getString("FontSize");
			}
            this.setGroupV2(this.isHaveGroup2(tableName));
			if ("0".equals(column)) // 不分栏
			{

				getNoFenceHmuster(document, bottomnList, infor_Flag, hmusterBo,
						tableName, isGroupPoint, emptyRow, fontStyle, writer,
						zeroPrint, user, columnDefinitionSize, dbpre,
						tableBodyWidth, tableheaderHeight, hmusterViewBo,
						tabid, isAutoCount, pageRows, nOperation, list3,
						page_height, history, year, month, count,
						tableHeaderList, r_bottomn, printGrid);

			} else if ("1".equals(column)) // 横分
			{
				if("0".equals(dataarea)){
					emptyRow="0";
				}
				getHorizontalFenceHmuster(tableBodyWidth, columnLine, history,
						year, month, count, ltPix, user, writer, document,
						dbpre, zeroPrint, page_height, pix, tableheaderHeight,
						hmusterViewBo, tabid, tableHeaderList, infor_Flag,
						tableName, isGroupPoint, userView, isAutoCount,
						pageRows, nOperation, list3,emptyRow,r_bottomn, printGrid);
			} else if ("2".equals(column)) // 纵分
			{
				list3 = (ArrayList) aList.get(7); // 哪些列有页小计或累计
				getPortraitFenceHmuster(tableBodyWidth, columnLine, history,
						year, month, count, ltPix, user, writer, document,
						dbpre, zeroPrint, page_height, pix, tableheaderHeight,
						hmusterViewBo, tabid, tableHeaderList, infor_Flag,
						tableName, isGroupPoint, userView, isAutoCount,
						pageRows, nOperation, list3,emptyRow);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(writer!=null){
				writer.flush();
				writer.close();
			}
		}
		return url;
	}
	
	
	/** 取得纵向分栏每页的记录数 */
	public int  getPortraitFenceRows(float tableBodyWidth,int pix)
	{
		int rows=0;
		if (paperOri == 2) // 纸张横向
		{
			String temp=String.valueOf(this.paperH/(tableBodyWidth+pix));
			rows=Integer.parseInt(temp.substring(0,temp.indexOf(".")));
		}
		else
		{
			String temp=String.valueOf(this.paperW/(tableBodyWidth+pix));
			rows=Integer.parseInt(temp.substring(0,temp.indexOf(".")));
			
		}
		return rows;
	}

	/**
	 * 生成（纵向分栏）花名册的pdf
	 * 
	 * @param tableBodyWidth
	 *            表体宽
	 * @param columnLine
	 *            是否有分格线
	 * @param history
	 *            1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param year
	 * @param month
	 * @param count
	 * @param ltPix
	 *            表最左边和最顶端位置
	 * @param user
	 *            用户名
	 * @param writer
	 * @param document
	 * @param dbpre
	 *            人员库前缀
	 * @param zeroPrint
	 *            是否零打印
	 * @param page_height
	 *            页高
	 * @param pix
	 *            间隔的像素
	 * @param tableheaderHeight
	 *            表头高
	 * @param hmusterViewBo
	 * @param tabid
	 *            高级花名册id
	 * @param tableHeaderList
	 *            表头列集合
	 * @param infor_Flag
	 *            信息库
	 * @param tableName
	 *            临时表名
	 * @param isGroupPoint
	 *            是否按组查询
	 * @throws GeneralException
	 */

	public void getPortraitFenceHmuster(float tableBodyWidth,
			String columnLine, String history, String year, String month,
			String count, float[] ltPix, String user, PdfWriter writer,
			Document document, String dbpre, String zeroPrint,
			float page_height, String pix, float tableheaderHeight,
			HmusterViewBo hmusterViewBo, String tabid,
			ArrayList tableHeaderList, String infor_Flag, String tableName,
			String isGroupPoint, UserView userView, String isAutoCount,
			String pageRows, int nOperation, ArrayList list3,String emptyRow)
			throws GeneralException {
		boolean isPhoto = false;
		ArrayList photoList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		ArrayList fieldList = getFieldandFieldsList(list3);
		String[] field = (String[]) fieldList.get(0);
		String[] fields = (String[]) fieldList.get(1);
		ArrayList tempList = new ArrayList();
		tempList = getHorizentalFenceSQL(tableHeaderList, infor_Flag,
				tableName, isGroupPoint, userView, dbpre);
		String h_sql = (String) tempList.get(0);
		isPhoto = ((Boolean) tempList.get(1)).booleanValue();
		boolean isGroupTerm = ((Boolean) tempList.get(2)).booleanValue();

		ArrayList titleInfoList = getTableBodyHeight(tabid, 0, 0, lt, "2",
				hmusterViewBo);
		ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
		int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体宽度（像素）
		if (pix == null || "".equals(pix)) {
            pix = "2";
        }
		
		HmusterXML hmxml = new HmusterXML(this.conn,tabid);//校验纵分 卡片式 列表式
		String tableFlag=hmxml.getValue(HmusterXML.RECORDWAY);
		
		int h_rows=0;
		int columns=0;
		if("1".equals(tableFlag))//纵分 卡片式
        {
            h_rows=getPortraitFenceRows(tableBodyWidth,Integer.parseInt(pix));
        } else{//纵分 列表式 //纵分时 列表 h_rows 可以看做列
			columns=getPortraitFenceRows(tableBodyWidth,Integer.parseInt(pix));  
			h_rows=Integer.parseInt(pageRows);
		}
			
		if ("1".equals(isAutoCount)) {
			if (Integer.parseInt(pageRows) < h_rows) {
                h_rows = Integer.parseInt(pageRows);
            }
		}

		String a_width = String.valueOf(tableBodyWidth + Float.parseFloat(pix));
		a_width = a_width.substring(0, a_width.indexOf("."));
		// 生成表格
		try {
			float hr_pix = 0; // 分隔线的坐标
			int curPage = 1; // 页数
			String groupV = "";
			String groupN="";
			int i = 1; // 序号
			int n = 1; // 每页的实际行数
			int no=0;
			
			/* 页小计初始化 */
			double[] pageCount = null;
			if (field != null && field.length > 0) {
				pageCount = new double[field.length];
				for (int a = 0; a < field.length; a++) {
					pageCount[a] = 0;
				}
			}

			double[] totalCount = null;
			if (fields != null && fields.length > 0) {
				totalCount = new double[fields.length];
				for (int a = 0; a < fields.length; a++) {
					totalCount[a] = 0;
				}
			}

			rowSet = dao.search(h_sql);
			Image image =null;
			
			ArrayList copyList=new ArrayList();
			for (int j = 0; j < tableHeaderList.size(); j++) {
				String[] args=((String[])tableHeaderList.get(j)).clone();
				copyList.add(args);
			}
			
			while (rowSet.next()) {
				no++;
				/* 分组显示 */  //分页写入数据
				if (!isGroupTerm && isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					String tempGroupN = " ";
					if (rowSet.getString("GroupN") != null) {
                        tempGroupN = rowSet.getString("GroupN");
                    }
					if (!groupN.equals(tempGroupN) || (i - 1) % h_rows == 0) {
						if (i != 1) {
							
							//空行打印
							if(n<=h_rows&& "1".equals(emptyRow))
							{
								while(n<=h_rows)
								{
									getEmptyRows(columnLine, Float
											.parseFloat(pix),tableheaderHeight, Float
											.parseFloat(a_width),
											tableHeaderList, "2", n,writer,page_height,tableBodyWidth,ltPix);
									n++;
								}			
							}
							
							
							/* 显示页小计 */
							if (field != null && field.length > 0
									&& (nOperation == 1 || nOperation == 3)) {
								getPageCountRows(columnLine, Float
										.parseFloat(pix), 0f, Float
										.parseFloat(a_width), tableHeaderList,
										"2", n, field, zeroPrint, pageCount,
										"hmuster.label.pageCount", writer,
										page_height, tableBodyWidth, ltPix);
								n++;
							}
							/* 显示累计 */
							if (fields != null && fields.length > 0
									&& (nOperation == 2 || nOperation == 3)) {
								getPageCountRows(columnLine, Float
										.parseFloat(pix), 0f, Float
										.parseFloat(a_width), tableHeaderList,
										"2", n, fields, zeroPrint, totalCount,
										"hmuster.label.toatlCount", writer,
										page_height, tableBodyWidth, ltPix);
								n++;
							}
							if("1".equals(isGroupNoPage)){
								/* 生成标题 */
								getTitle(tabid,isGroupTerm, page_height, writer, 0, 0,
									titelList, user, String.valueOf(curPage),
									h_rows, infor_Flag, tableName,
									isGroupPoint, groupV, ltPix[1],
									tableheaderHeight, history, year, month,
									count, userView, dbpre);
								document.newPage();
							}else{
//								if(no<2){
									/* 生成标题 */
									getTitle(tabid,isGroupTerm, page_height, writer, 0, 0,
										titelList, user, String.valueOf(curPage),
										h_rows, infor_Flag, tableName,
										isGroupPoint, groupV, ltPix[1],
										tableheaderHeight, history, year, month,
										count, userView, dbpre);
									document.newPage();
//								}
							}
							// 小计清空
							if (field != null && field.length > 0) {
								for (int a = 0; a < field.length; a++) {
									pageCount[a] = 0;
								}
							}
							// 累计清空
							if (!groupN.equals(tempGroupN)) {
								if (fields != null && fields.length > 0) {
									for (int a = 0; a < fields.length; a++) {
										totalCount[a] = 0;
									}
								}
								no=1;
							}
							i = 1;
							n = 1;
							curPage++;
						}
						groupN = tempGroupN;
						if(rowSet.getString("GroupV")!=null) {
                            groupV=rowSet.getString("GroupV");
                        } else {
                            groupV=" ";
                        }
					}
				} else {
					if("1".equals(tableFlag)){//卡片式
					if (i != 1 && (i - 1) % h_rows == 0) {
						
//						空行打印
						if(n<=h_rows&& "1".equals(emptyRow))
						{
							while(n<=h_rows)
							{
								getEmptyRows(columnLine, Float
										.parseFloat(pix), tableheaderHeight, Float
										.parseFloat(a_width),
										tableHeaderList, "2", n,writer,page_height,tableBodyWidth,ltPix);
								n++;
							}			
						}
						
						
						/* 显示页小计 */
						if (nOperation == 1 || nOperation == 3) {
							getPageCountRows(columnLine, Float.parseFloat(pix),
									tableheaderHeight, Float
											.parseFloat(a_width),
									tableHeaderList, "2", n, field, zeroPrint,
									pageCount, "hmuster.label.pageCount",
									writer, page_height, tableBodyWidth, ltPix);
							n++;
						}
						/* 显示累计 */
						if (nOperation == 2 || nOperation == 3) {
							getPageCountRows(columnLine, Float.parseFloat(pix),
									tableheaderHeight, Float
											.parseFloat(a_width),
									tableHeaderList, "2", n, fields, zeroPrint,
									totalCount, "hmuster.label.toatlCount",
									writer, page_height, tableBodyWidth, ltPix);
							n++;
						}
																     //行高  行数 
						getTitle(tabid,isGroupTerm, page_height, writer, 0, 0,
								titelList, user, String.valueOf(curPage),
								h_rows, infor_Flag, tableName, isGroupPoint,
								groupV, ltPix[1], tableheaderHeight, history,
								year, month, count, userView, dbpre);
						if((no-1)%(h_rows*2)==0){
							document.newPage();//生成新的页面
						}
						// 小计清空
						if (field != null && field.length > 0) {
							for (int a = 0; a < field.length; a++) {
								pageCount[a] = 0;
							}
						}

						n = 1;
						curPage++;
						}
					}else{
						if(i != 1 && (i - 1) %(h_rows*columns)  == 0){  //3为列数

							
//							空行打印
							if(n<=h_rows&& "1".equals(emptyRow))
							{
								while(n<=h_rows)
								{
									getEmptyRows(columnLine, Float
											.parseFloat(pix), tableheaderHeight, Float
											.parseFloat(a_width),
											tableHeaderList, "2", n,writer,page_height,tableBodyWidth,ltPix);
									n++;
								}			
							}
							
							
							/* 显示页小计 */
							if (nOperation == 1 || nOperation == 3) {
								getPageCountRows(columnLine, Float.parseFloat(pix),
										tableheaderHeight, Float
												.parseFloat(a_width),
										tableHeaderList, "2", n, field, zeroPrint,
										pageCount, "hmuster.label.pageCount",
										writer, page_height, tableBodyWidth, ltPix);
								n++;
							}
							/* 显示累计 */
							if (nOperation == 2 || nOperation == 3) {
								getPageCountRows(columnLine, Float.parseFloat(pix),
										tableheaderHeight, Float
												.parseFloat(a_width),
										tableHeaderList, "2", n, fields, zeroPrint,
										totalCount, "hmuster.label.toatlCount",
										writer, page_height, tableBodyWidth, ltPix);
								n++;
							}
							int aa=(int)(Float.parseFloat(((String[])tableHeaderList.get(0))[5])+Float.parseFloat(((String[])tableHeaderList.get(0))[14]));//+Float.parseFloat(((String[])tableHeaderList.get(0))[14]);
							getTitle(tabid,isGroupTerm, page_height, writer, aa, h_rows,
									titelList, user, String.valueOf(curPage),
									h_rows, infor_Flag, tableName, isGroupPoint,
									groupV, ltPix[1], tableheaderHeight, history,
									year, month, count, userView, dbpre);
							if((no-1)%(h_rows*columns)==0){  //列表式展现时 有多列 存在处理
								document.newPage();//生成新的页面
								
							}
							// 小计清空
							if (field != null && field.length > 0) {
								for (int a = 0; a < field.length; a++) {
									pageCount[a] = 0;
								}
							}

							n = 1;
							curPage++;
						
						} 
					}
					
				}
				

				if(no!=1&&((no-1)-h_rows*columns*(curPage-1))%h_rows==0){  // 当当前行为每页的第二列开始行时 表格的下标为前一列的累加和    changxy
					tableHeaderList=hmusterViewBo.updateList(tableHeaderList,Float.parseFloat(pix));
				}
				
				if(curPage!=1&&(no-1)==h_rows*columns*(curPage-1)){  //当当前行为每页的开始行时 表格下标还原
					tableHeaderList.clear();
					tableHeaderList.addAll(copyList);
					
				}
				for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {//每个单元格的位置
					String[] temp = (String[]) t.next();
					int type = 1;
					String border = "1";
					int align = Integer.parseInt(temp[13]);
					String fontName = temp[10];
					int fontSize = Integer.parseInt(temp[12]);
					String afontStyle = temp[11];
					float topPix =0;
						if(no<=h_rows) {
                            topPix = page_height - Float.parseFloat(temp[3])*(no);//6为每个表格的列数  //每个单元格的位置信息
                        } else{
							topPix = page_height - Float.parseFloat(temp[3])*(no-((no-1)/h_rows)*h_rows);//6为每个表格的列数  //每个单元格的位置信息	
						}
					float left=Float.parseFloat(temp[2]);// left  每个单元格居左的位置  //需要考虑第二列的居左位置
					hr_pix = ltPix[0] + (n - 1)
							* (tableBodyWidth + Float.parseFloat(pix))
							+ tableBodyWidth + (Float.parseFloat(pix) / 2);
					float width = Float.parseFloat(temp[4]);
					float aheight = Float.parseFloat(temp[5]);
					String context = " ";
					if ("S".equals(temp[7])) {
						context = String.valueOf(no);
					} else if (!isGroupTerm && "P".equals(temp[7])) // 生成照片
					{
						/*
						String tempName = createPhotoFile(dbpre + "A00", rowSet
								.getString("a0100"), "P");
						photoList.add(tempName);
						Image image = Image.getInstance(System
								.getProperty("java.io.tmpdir")
								+ "\\" + tempName);  */					
						ArrayList list=createPhotoFile2(getDbpre(rowSet, dbpre) + "A00",
								rowSet.getString("A0100"), "P");
						byte[] buf=(byte[]) list.get(0);
						boolean photo_flag=(Boolean) list.get(1);
						PdfPCell pdfpCell =null;
						if(buf.length>0)
						{
							if(photo_flag) {
								java.awt.Image awtImage=Toolkit.getDefaultToolkit().createImage(buf);
								image= Image.getInstance(awtImage,null);		
							}else {
								image = Image.getInstance(buf);	
							}
							image.scaleAbsolute(width - 5, aheight - 5);
							pdfpCell = new PdfPCell(image, false);
						}
						else
						{
							pdfpCell=new PdfPCell(new Paragraph(""));
						}						
						pdfpCell.setFixedHeight(aheight);
						PdfPTable table = new PdfPTable(1);
						pdfpCell.setHorizontalAlignment(Element.ALIGN_CENTER); // 水平居中
						pdfpCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // 垂直居中
						table.setTotalWidth(width); // 设置表的总体宽度
						table.setLockedWidth(true); // 宽度锁定
						table.addCell(pdfpCell);
						table.writeSelectedRows(0, -1, left+this.left_space, topPix-this.top_space, writer
								.getDirectContent()); // 固定坐标
					} else if (isGroupTerm && "P".equals(temp[7])) {
                        context = " ";
                    } else if (temp[7]==null|| "H".equals(temp[7])|| "".equals(temp[7])) {
						if(temp[7]!=null) {
                            context = temp[1];
                        } else {
                            context="";
                        }
					} else // if(temp[7]!=null&&!temp[7].equals("R")&&!temp[7].equals(""))
					{

						if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                            context = rowSet.getString("GroupV");
                        } else
						{
							if("E".equals(temp[7])&&this.isGroupV2) {
                                context = rowSet.getString("GroupV2");
                            } else if("E".equals(temp[7])) {
                                context="";
                            } else {
                                context = rowSet.getString("C" + temp[0]);
                            }
						}

						// 页小计
						if (field != null && field.length > 0) {
							for (int b = 0; b < field.length; b++) {

								if (field[b].equals("C" + temp[0])) {
									if (context != null) {
                                        pageCount[b] += Double
                                                .parseDouble(context);
                                    }
									break;
								}
							}
						}
						if (fields != null && fields.length > 0) {
							for (int b = 0; b < fields.length; b++) {
								if (fields[b].equals("C" + temp[0])) {
									if (context != null) {
                                        totalCount[b] += Double
                                                .parseDouble(context);
                                    }
									break;
								}
							}
						}

						if (context == null || "".equals(context)) {
							context = " ";
							if ("1".equals(zeroPrint) && "N".equals(temp[9])) {
                                context = "0";
                            }
						}
						if (context != null && !"".equals(context)
								&& !" ".equals(context) && "N".equals(temp[9])) {
							float f = Float.parseFloat(context);
							if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
							{
								if (f == 0) {
                                    context = " ";
                                } else {
                                    context = hmusterViewBo.round(context,
                                            Integer.parseInt(temp[6]));
                                }
							} else {
                                context = hmusterViewBo.round(context, Integer
                                        .parseInt(temp[6]));
                            }
						}
						if (temp[9]!=null && "D".equals(temp[9])) {
							context = strToDate(context);
						}
					}

					if (!"P".equals(temp[7]) || isGroupTerm) {
						if("1".equals(temp[26])) {
                            context=" ";
                        }
						// c.L,c.T,c.R,c.B
						String[] ltrb = new String[4];
						ltrb[0] = temp[21];
						ltrb[1] = temp[22];
						ltrb[2] = temp[23];
						ltrb[3] = temp[24];
//						ltrb[0] = "1";
//						ltrb[1] = "1";
//						ltrb[2] = "1";
//						ltrb[3] = "1";
						excecute(context, left, topPix, width, aheight, align,
								afontStyle, fontSize,fontName, writer, 1, ltrb);
					}
				}
				for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {
					String[] temp = (String[]) t.next();
					float topPix = Float.parseFloat(temp[3]);
					float left = Float.parseFloat(temp[2]) + (n - 1)
							* (tableBodyWidth + Float.parseFloat(pix));
					float width = Float.parseFloat(temp[4]);
					float aheight = Float.parseFloat(temp[5]);
					// 处理虚线 c.L,c.T,c.R,c.B
					String[] ltrb = new String[4];
					ltrb[0] = temp[21];
					ltrb[1] = temp[22];
					ltrb[2] = temp[23];
					ltrb[3] = temp[24];
//					ltrb[0] = "1";
//					ltrb[1] = "1";
//					ltrb[2] = "1";
//					ltrb[3] = "1";
					dealDashed(ltrb[0], ltrb[1], ltrb[2], ltrb[3], left,
							topPix, width, aheight, page_height, writer);
				}

				if (columnLine != null && "1".equals(columnLine)) {
					PdfContentByte cb = writer.getDirectContent();
					cb.setLineWidth(1f);
					cb.setLineDash(3, 3, 0);
					cb.moveTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space);
					cb.lineTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space
							- tableheaderHeight);
					cb.setColorStroke(new Color(0, 0, 0));
					cb.stroke();
					cb.setLineDash(1);
				}
				i++;
				n++;
			}
			
			//	空行打印
			if(n<=h_rows&& "1".equals(emptyRow))
			{
				while(n<=h_rows)
				{
					getEmptyRows(columnLine, Float
							.parseFloat(pix), tableheaderHeight, Float
							.parseFloat(a_width),
							tableHeaderList, "2", n,writer,page_height,tableBodyWidth,ltPix);
					n++;
				}			
			}
			
			/* 显示页小计 */
			if (nOperation == 1 || nOperation == 3) {
				getPageCountRows(columnLine, Float.parseFloat(pix),
						tableheaderHeight, Float
								.parseFloat(a_width),
						tableHeaderList, "2", n, field, zeroPrint,
						pageCount, "hmuster.label.pageCount",
						writer, page_height, tableBodyWidth, ltPix);
				n++;
			}
			/* 显示累计 */
			if (nOperation == 2 || nOperation == 3) {
				getPageCountRows(columnLine, Float.parseFloat(pix),
						tableheaderHeight, Float
								.parseFloat(a_width),
						tableHeaderList, "2", n, fields, zeroPrint,
						totalCount, "hmuster.label.toatlCount",
						writer, page_height, tableBodyWidth, ltPix);
				n++;
			}
			
			
			
			/* 生成标题 最后一页生成标题   0, 0*/
			int aa=0;
			if("0".equals(tableFlag))//纵分列表式 
            {
                aa=(int)(Float.parseFloat(((String[])tableHeaderList.get(0))[5])+Float.parseFloat(((String[])tableHeaderList.get(0))[14]));
            }
			getTitle(tabid,isGroupTerm, page_height, writer, aa, h_rows, titelList, user,
					String.valueOf(curPage), h_rows, infor_Flag, tableName,
					isGroupPoint, groupV, ltPix[1], tableheaderHeight, history,
					year, month, count, userView, dbpre);
			document.close();
			this.deletePicture(photoList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}
	/**
	 * 日期纠正格式
	 * @param str
	 * @return
	 */
	public String strToDate(String str){
		String dateStr="";
		String year="";
		String month="";
		String day = "";
		char arr[] = str.toCharArray();
		int n=0;
		for(int i=0;i<arr.length;i++){
			if(n==0){
				if(arr[i]>47&&arr[i]<58){
					year+=arr[i];
				}else{
					n=1;
				}
			}else if(n==1){
				if(arr[i]>47&&arr[i]<58){
					month+=arr[i];
				}else{
					n=2;
				}
			}else if(n==2){
				if(arr[i]>47&&arr[i]<58){
					day+=arr[i];
				}
			}
		}
		if(year.length()>3){
			dateStr+=year;
			dateStr+="-"+month;
			dateStr+="-"+day;
		}else if(day.length()>3){
			dateStr+=day;
			dateStr+="-"+month;
			dateStr+="-"+year;
		}else if(month.length()>3){
			dateStr+=month;
			dateStr+="-"+day;
			dateStr+="-"+year;
		}else{
			dateStr = str;
		}
		return dateStr;
	}
	private float headerHeight=0;//多表头显示时，表头的高度
	/**
	 *多层表头显示是，记录表格整体高度，为画空行用
	 */
	private float headerHeight_haveTitle=0;
	/**
	 * 生成（横分栏）高级花名册的pdf
	 * 
	 * @param tableBodyWidth
	 *            表体宽
	 * @param columnLine
	 *            是否有分格线
	 * @param history
	 *            1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param year
	 * @param month
	 * @param count
	 * @param ltPix
	 *            表最左边和最顶端位置
	 * @param user
	 *            用户名
	 * @param writer
	 * @param document
	 * @param dbpre
	 *            人员库前缀
	 * @param zeroPrint
	 *            是否零打印
	 * @param page_height
	 *            页高
	 * @param pix
	 *            间隔的像素
	 * @param tableheaderHeight
	 *            表头高
	 * @param hmusterViewBo
	 * @param tabid
	 *            高级花名册id
	 * @param tableHeaderList
	 *            表头列集合
	 * @param infor_Flag
	 *            信息库
	 * @param tableName
	 *            临时表名
	 * @param isGroupPoint
	 *            是否按组查询
	 * @throws GeneralException
	 */

	public void getHorizontalFenceHmuster(float tableBodyWidth,
			String columnLine, String history, String year, String month,
			String count, float[] ltPix, String user, PdfWriter writer,
			Document document, String dbpre, String zeroPrint,
			float page_height, String pix, float tableheaderHeight,
			HmusterViewBo hmusterViewBo, String tabid,
			ArrayList tableHeaderList, String infor_Flag, String tableName,
			String isGroupPoint, UserView userView, String isAutoCount,
			String pageRows, int nOperation, ArrayList list3,String emptyRow,
			float r_bottomn, String printGrid)
			throws GeneralException {
		ArrayList photoList = new ArrayList();
		boolean isPhoto = false;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet subSet = null;
		ArrayList fieldList = getFieldandFieldsList(list3);
		String[] field = (String[]) fieldList.get(0);
		String[] fields = (String[]) fieldList.get(1);
		String[] zj_fields=(String[]) fieldList.get(2);
		ArrayList tempList = new ArrayList();
		tempList = getHorizentalFenceSQL(tableHeaderList, infor_Flag,tableName, isGroupPoint, userView, dbpre);
		String h_sql = (String) tempList.get(0);
		isPhoto = ((Boolean) tempList.get(1)).booleanValue();
		boolean isGroupTerm = ((Boolean) tempList.get(2)).booleanValue();

		try {
			ArrayList titleInfoList = getTableBodyHeight(tabid, 0, 0, lt, "1",hmusterViewBo);
			ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
			int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体高度（像素）
			/* --------------------*************** */
			if (pix == null || "".equals(pix)) {
                pix = "1";
            }
			float headHeight=tableheaderHeight;
			if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
	        {
	            StringBuffer sb = new StringBuffer();
	            Set  keySet = this.getHmusterViewBo().getTextFormatMap().keySet();
	            for(Iterator it = keySet.iterator();it.hasNext();)
	            {
	            	String key = (String)it.next();
	            	sb.append(","+key);
	            }
	            subSet = dao.search("select max(rleft+rwidth)-min(rleft) t_width,max(rtop+rheight)-min(rtop) t_height,min(rleft)-"
						+ lt
						+ " r_left,max(rtop+rheight) r_bottomn   from muster_cell where tabid="
						+ tabid+" and gridno in ("+sb.toString().substring(1)+")");//打印空行时 不应该只查插入指标行，应该累加两行记录
				if (subSet.next()) {
					headHeight=subSet.getFloat("t_height");
				}
				this.headerHeight=headHeight;
				
				 subSet = dao.search("select max(rleft+rwidth)-min(rleft) t_width,max(rtop+rheight)-min(rtop) t_height,min(rleft)-"
							+ lt
							+ " r_left,max(rtop+rheight) r_bottomn   from muster_cell where tabid="
							+ tabid+" ");//打印空行时 不应该只查插入指标行，应该累加两行记录
					if (subSet.next()) {
						this.headerHeight_haveTitle = subSet.getFloat("t_height");
					}
				
	         }
			int h_rows = hmusterViewBo.getHorizontalrows(bodyHeight,headHeight+3, pix, nOperation); // 得到每页的行数；
			if("1".equals(dataarea)) {
                h_rows-=1;
            }
			if ("1".equals(isAutoCount)) {
				if (Integer.parseInt(pageRows) < h_rows) {
                    h_rows = Integer.parseInt(pageRows);
                }
			}

			String a_height = String.valueOf(headHeight+ Float.parseFloat(pix));
			float height=Float.parseFloat(a_height);
			while(height*h_rows>bodyHeight) {
                height--;
            }
			a_height=height+"";
			a_height = a_height.substring(0, a_height.indexOf("."));
			/** ##################### 生成表格 ############################ **** */
			{
				float hr_pix = 0; // 分隔线的坐标
				int curPage = 1; // 页数
				String groupV = "";
				String groupN="";
				int i = 1; // 序号
				int n = 1; // 每页的实际行数
				/* 页小计初始化 */
				double[] pageCount = null;//页小计
				if (field != null && field.length > 0) {
					pageCount = new double[field.length];
					for (int a = 0; a < field.length; a++) {
						pageCount[a] = 0;
					}
				}

				double[] totalCount = null;//页累计
				if (fields != null && fields.length > 0) {
					totalCount = new double[fields.length];
					for (int a = 0; a < fields.length; a++) {
						totalCount[a] = 0;
					}
				}
				double[] totalCount_all=null;//总计
				if (zj_fields != null && zj_fields.length > 0) {
					totalCount_all = new double[zj_fields.length];
					for (int a = 0; a < zj_fields.length; a++) {
						totalCount_all[a] = 0;
					}
				}
				
				rowSet = dao.search(h_sql);
				Image image=null;
				int xx=0;
				//没合格子的
				this.mergeGrid=this.mergeGrid==null?"":this.mergeGrid;
				ArrayList aheaderList=new ArrayList();
				for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {
					String[] temp = (String[]) t.next();
					if((this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&this.getHmusterViewBo().getTextFormatMap().get(temp[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                        continue;
                    }
					aheaderList.add(temp);
				}
				String mergeitemid[] = new String[aheaderList.size()];//gridno
				String mergevalue[] = new String[aheaderList.size()];//数据值
				String ismergemain[] = new String[aheaderList.size()];//是否按机构/人员合并
				float[] toppixvalue = new float[aheaderList.size()];//格子的顶部坐标
				int mergeheight[]=new int[aheaderList.size()];//合并格子的高度
				//row
				String[] rowitemid = new String[aheaderList.size()];//行合并
				int arrayindex=0;
	 			String[] heArr = mergeGrid.split(",");
				for(int h=0;h<heArr.length;h++){
					if(heArr[h]!=null&&heArr[h].length()>1){
						String heirem[] = heArr[h].split(":");
						if((this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&this.getHmusterViewBo().getTextFormatMap().get(heirem[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                            continue;
                        }
						if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
							mergeitemid[arrayindex] = heirem[0];
							mergevalue[arrayindex] = "&nbsp;";
							mergeheight[arrayindex] = 0;
							toppixvalue[arrayindex]=0;
						}
						if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
							ismergemain[arrayindex] = "true";
						}
						if(heirem[3]!=null&& "true".equalsIgnoreCase(heirem[3]))
						{
							rowitemid[arrayindex]=heirem[0];
						}
						arrayindex++;
					}
				}
				String mainidvalue = "";
				String mainArr[] = new String[aheaderList.size()];
				int topvalue_index=0;
				int recordRow=0;
				ArrayList rowMargeList = new ArrayList();
				while (rowSet.next()) {
					if(infor_Flag!=null&& "1".equals(infor_Flag)) {
                        mainidvalue = rowSet.getString("A0100");
                    } else if(infor_Flag!=null&& "2".equals(infor_Flag)) {
                        mainidvalue = rowSet.getString("B0110");
                    } else if(infor_Flag!=null&& "3".equals(infor_Flag)) {
                        mainidvalue = rowSet.getString("E01A1");
                    }
					/* 分组显示 */
					if (/*!isGroupTerm && */isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(this.isGroupNoPage)) {/**分组显示*/
						String tempGroupN = " ";
						if (rowSet.getString("GroupN") != null) {
                            tempGroupN = rowSet.getString("GroupN");
                        }
						if (!groupN.equals(tempGroupN) || (i - 1) % h_rows == 0) {/**满一页*/
							/**当分组显示时，要处理*/
							if (i != 1) {
								if((i - 1) % h_rows != 0&&("1".equals(column)&& "1".equals(this.dataarea)))
								{
									int acolumnNum = 0;
									for (Iterator t = aheaderList.iterator(); t.hasNext();) {
										String[] temp = (String[]) t.next();
										acolumnNum++;
										String[] ltrb = new String[4];
										ltrb[0] = temp[21];
										ltrb[1] = temp[22];
										ltrb[2] = temp[23];
										ltrb[3] = temp[24];
										int align = Integer.parseInt(temp[13]);
										String fontName = temp[10];
										int fontSize = Integer.parseInt(temp[12]);
										String afontStyle = temp[11];
										float left = Float.parseFloat(temp[2]);
										float width = Float.parseFloat(temp[4]);
										float aheight = Float.parseFloat(temp[5]);
										if(mergeitemid[acolumnNum-1]!=null&&mergeitemid[acolumnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(mergeitemid[acolumnNum-1])&&!"P".equals(temp[7]))
										{
											if(rowitemid[acolumnNum-1]!=null&&rowitemid[acolumnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[acolumnNum-1]))// row merge
											{
												if(acolumnNum==1)//第一个格要看下一个是否合并
        								    	{
        								    		if(rowitemid[acolumnNum]!=null&&rowitemid[acolumnNum].trim().length()>0)//下一个合并
        								    		{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", 1+"");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*aheight));
        								    			bean.set("content",mergevalue[acolumnNum-1]);
        								    			bean.set("cloumn",acolumnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        								    		}
        								    		else
        								    		{
        								    			excecute(mergevalue[acolumnNum-1], left,toppixvalue[acolumnNum-1], width, (mergeheight[acolumnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else if(acolumnNum==aheaderList.size())//最后一个格子
        								    	{
        								    		if(rowitemid[acolumnNum-2]!=null&&rowitemid[acolumnNum-2].trim().length()>0)//前一个合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*aheight));
        								    			bean.set("content",mergevalue[acolumnNum-1]);
        								    			bean.set("cloumn",acolumnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
          								    			excecute(mergevalue[acolumnNum-1], left,toppixvalue[acolumnNum-1], width, (mergeheight[acolumnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else
        										{
        								    		if(rowitemid[acolumnNum]!=null&&rowitemid[acolumnNum].trim().length()>0||rowitemid[acolumnNum-2]!=null&&rowitemid[acolumnNum-2].trim().length()>0)//前一个或者后一个有合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();	   
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*aheight));
        								    			bean.set("content",mergevalue[acolumnNum-1]);
        								    			bean.set("cloumn",acolumnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(mergevalue[acolumnNum-1], left,toppixvalue[acolumnNum-1], width, (mergeheight[acolumnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        										}
        								    }
											else
											{
												excecute(mergevalue[acolumnNum-1], left,toppixvalue[acolumnNum-1], width, (mergeheight[acolumnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
											}
										}
								    }
								}
								//--------------------------------------
								if(rowMargeList.size()>0)
								{
									HashMap existMap = new HashMap();
								    for(int out=0;out<rowMargeList.size();out++)
								    {
								    	if(existMap.get(out+"")!=null) {
                                            continue;
                                        }
								    	LazyDynaBean outBean = (LazyDynaBean)rowMargeList.get(out);
									    String top = (String)outBean.get("topvalue");
									    String cloumn = (String)outBean.get("cloumn");
									    String mergeaheight=(String)outBean.get("height");
									    String content=(String)outBean.get("content");
									    String gridno = (String)outBean.get("gridno");
									    String left = (String)outBean.get("left");
										String width=(String)outBean.get("width");
										String fontName=(String)outBean.get("fontName");
										String fontSize=(String)outBean.get("fontSize");
										String fontStyle = (String)outBean.get("fontStyle");
										String  border=(String)outBean.get("border");
										String align=(String)outBean.get("align");
										String[] ltrb = (String[])outBean.get("ltrb");
										int columnNum=1;
										int col = Integer.parseInt(cloumn);
									    for(int in=0;in<rowMargeList.size();in++)
									    {
									     	LazyDynaBean inBean =(LazyDynaBean)rowMargeList.get(in);
									    	String intop = (String)inBean.get("topvalue");
										    String incloumn = (String)inBean.get("cloumn");
										    String inheight=(String)inBean.get("height");
										    String incontent=(String)inBean.get("content");
										    String ingridno = (String)inBean.get("gridno");
										    String inwidth=(String)inBean.get("width");
										    int inclo = Integer.parseInt(incloumn);
										    if((top.equalsIgnoreCase(intop)&&cloumn.equalsIgnoreCase(incloumn))||!(top.equalsIgnoreCase(intop)))//同一个格子
										    {
											    continue;
										    }
										    if(top.equalsIgnoreCase(intop)&&mergeaheight.equalsIgnoreCase(inheight)&&content.equalsIgnoreCase(incontent)&&inclo==(col+columnNum))
										    {
												columnNum++;
												width=String.valueOf(Float.parseFloat(width)+Float.parseFloat(inwidth));
												existMap.put(in+"", "1");
										    }
										    else
										    {
										    	continue;
										    }
									    }
									    excecute(content, Float.parseFloat(left),  Float.parseFloat(top),  Float.parseFloat(width),  Float.parseFloat(mergeaheight),Integer.parseInt(align), fontStyle, Integer.parseInt(fontSize),fontName, writer, 1,ltrb);
									   /* margeBuf.append(executeAbsoluteTable2(Integer.parseInt(type), Integer.parseInt(align),fontName, fontSize, fontStyle, border, top, 
										    	left, width, mergeheight,content, style_name));*/
							    	}
								    rowMargeList = new ArrayList();
								}
								arrayindex=0;
								for(int h=0;h<heArr.length;h++){
									if(heArr[h]!=null&&heArr[h].length()>1){
										String heirem[] = heArr[h].split(":");
										if((this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&this.getHmusterViewBo().getTextFormatMap().get(heirem[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                                            continue;
                                        }
										if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
											mergeitemid[arrayindex] = heirem[0];
											mergevalue[arrayindex] = "&nbsp;";
											mergeheight[arrayindex] = 0;
											toppixvalue[arrayindex]=0;
										}
										if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
											ismergemain[arrayindex] = "true";
										}
										if(heirem[3]!=null&& "true".equalsIgnoreCase(heirem[3]))
										{
											rowitemid[arrayindex]=heirem[0];
										}
										arrayindex++;
									}
								}
								mainidvalue = "";
								mainArr = new String[aheaderList.size()];
								topvalue_index=0;
								recordRow=0;
								//-------------------
								//空行打印
								if(n<=h_rows&& "1".equals(emptyRow))
								{
									while(n<=h_rows)
									{
										getEmptyRows(columnLine, Float.parseFloat(pix), Float.parseFloat(a_height), 0f,aheaderList, "1", n,writer,page_height,tableBodyWidth,ltPix);
										n++;
									}			
								}
								
								/* 显示页小计 */
								if (field != null && field.length > 0&& (nOperation == 1 || nOperation == 3)) {
									getPageCountRows(columnLine, Float
											.parseFloat(pix), Float
											.parseFloat(a_height), 0f,
											aheaderList, "1", n, field,
											zeroPrint, pageCount,
											"hmuster.label.pageCount", writer,
											page_height, tableBodyWidth, ltPix);
									n++;
								}
								/* 显示累计 */
								if (fields != null && fields.length > 0&& (nOperation == 2 || nOperation == 3)) {
									getPageCountRows(columnLine, Float
											.parseFloat(pix), Float
											.parseFloat(a_height), 0f,
											aheaderList, "1", n, fields,
											zeroPrint, totalCount,
											"hmuster.label.toatlCount", writer,
											page_height, tableBodyWidth, ltPix);
									n++;
								}
								/* 生成标题 */
								getTitle(tabid,isGroupTerm, page_height, writer,
										Integer.parseInt(a_height), n - 2,
										titelList, user, String.valueOf(curPage), h_rows,
										infor_Flag, tableName, isGroupPoint,
										groupV, ltPix[1], tableheaderHeight,
										history, year, month, count, userView,
										dbpre);
								document.newPage();
								// 小计清空
								if (field != null && field.length > 0) {
									for (int a = 0; a < field.length; a++) {
										pageCount[a] = 0;
									}
								}

								// 累计清空
								if (!groupN.equals(tempGroupN)) {
									if (fields != null && fields.length > 0) {
										for (int a = 0; a < fields.length; a++) {
											totalCount[a] = 0;
										}
									}
									if("1".equals(isGroupedSerials)) {
										i = 1;
									}
								}

								
								n = 1;
								curPage++;
							}
							groupN = tempGroupN;
							if(rowSet.getString("GroupV")!=null) {
                                groupV=rowSet.getString("GroupV");
                            } else {
                                groupV=" ";
                            }
							if("1".equals(dataarea)) {
                                writeTableHeader(tableHeaderList, page_height,
                                    writer, printGrid, r_bottomn);
                            }
						}
					} else {

						if ((i != 1 && (i - 1) % h_rows == 0)||i==1) {/**满一页了，*/
							if(i!=1){
							if(rowMargeList.size()>0)
							{
								HashMap existMap = new HashMap();
							    for(int out=0;out<rowMargeList.size();out++)
							    {
							    	if(existMap.get(out+"")!=null) {
                                        continue;
                                    }
							    	LazyDynaBean outBean = (LazyDynaBean)rowMargeList.get(out);
								    String top = (String)outBean.get("topvalue");
								    String cloumn = (String)outBean.get("cloumn");
								    String mergeaheight=(String)outBean.get("height");
								    String content=(String)outBean.get("content");
								    String gridno = (String)outBean.get("gridno");
								    String left = (String)outBean.get("left");
									String width=(String)outBean.get("width");
									String fontName=(String)outBean.get("fontName");
									String fontSize=(String)outBean.get("fontSize");
									String fontStyle = (String)outBean.get("fontStyle");
									String  border=(String)outBean.get("border");
									String align=(String)outBean.get("align");
									String[] ltrb = (String[])outBean.get("ltrb");
									int columnNum=1;
									int col = Integer.parseInt(cloumn);
								    for(int in=0;in<rowMargeList.size();in++)
								    {
								     	LazyDynaBean inBean =(LazyDynaBean)rowMargeList.get(in);
								    	String intop = (String)inBean.get("topvalue");
									    String incloumn = (String)inBean.get("cloumn");
									    String inheight=(String)inBean.get("height");
									    String incontent=(String)inBean.get("content");
									    String ingridno = (String)inBean.get("gridno");
									    String inwidth=(String)inBean.get("width");
									    int inclo = Integer.parseInt(incloumn);
									    if((top.equalsIgnoreCase(intop)&&cloumn.equalsIgnoreCase(incloumn))||!(top.equalsIgnoreCase(intop)))//同一个格子
									    {
										    continue;
									    }
									    if(top.equalsIgnoreCase(intop)&&mergeaheight.equalsIgnoreCase(inheight)&&content.equalsIgnoreCase(incontent)&&inclo==(col+columnNum))
									    {
											columnNum++;
											width=String.valueOf(Float.parseFloat(width)+Float.parseFloat(inwidth));
											existMap.put(in+"", "1");
									    }
									    else
									    {
									    	continue;
									    }
								    }
								    excecute(content, Float.parseFloat(left),  Float.parseFloat(top),  Float.parseFloat(width),  Float.parseFloat(mergeaheight),Integer.parseInt(align), fontStyle, Integer.parseInt(fontSize),fontName, writer, 1,ltrb);
								   /* margeBuf.append(executeAbsoluteTable2(Integer.parseInt(type), Integer.parseInt(align),fontName, fontSize, fontStyle, border, top, 
									    	left, width, mergeheight,content, style_name));*/
						    	}
							    rowMargeList = new ArrayList();
							}
							arrayindex=0;
							for(int h=0;h<heArr.length;h++){
								if(heArr[h]!=null&&heArr[h].length()>1){
									String heirem[] = heArr[h].split(":");
									if((this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&&this.getHmusterViewBo().getTextFormatMap().get(heirem[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                                        continue;
                                    }
									if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
										mergeitemid[arrayindex] = heirem[0];
										mergevalue[arrayindex] = "&nbsp;";
										mergeheight[arrayindex] = 0;
										toppixvalue[arrayindex]=0;
									}
									if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
										ismergemain[arrayindex] = "true";
									}
									if(heirem[3]!=null&& "true".equalsIgnoreCase(heirem[3]))
									{
										rowitemid[arrayindex]=heirem[0];
									}
									arrayindex++;
								}
							}
							mainidvalue = "";
							mainArr = new String[aheaderList.size()];
							topvalue_index=0;
							recordRow=0;
//							空行打印
							if(n<=h_rows&& "1".equals(emptyRow))
							{
								while(n<=h_rows)
								{
									getEmptyRows(columnLine, Float.parseFloat(pix), Float.parseFloat(a_height), 0f,aheaderList, "1", n,writer,page_height,tableBodyWidth,ltPix);
									n++;
								}			
							}
							
							
							/* 显示页小计 */
							if (nOperation == 1 || nOperation == 3) {
								getPageCountRows(columnLine, Float
										.parseFloat(pix), Float
										.parseFloat(a_height), 0f,
										aheaderList, "1", n, field,
										zeroPrint, pageCount,
										"hmuster.label.pageCount", writer,
										page_height, tableBodyWidth, ltPix);
								n++;
							}
							/* 显示累计 */
							if (nOperation == 2 || nOperation == 3) {
								getPageCountRows(columnLine, Float.parseFloat(pix), Float.parseFloat(a_height), 0f,
										aheaderList, "1", n, fields,
										zeroPrint, totalCount,
										"hmuster.label.toatlCount", writer,
										page_height, tableBodyWidth, ltPix);
								n++;
							}

							getTitle(tabid,isGroupTerm, page_height, writer, Integer.parseInt(a_height), n - 2, titelList,
									user, String.valueOf(curPage), h_rows,
									infor_Flag, tableName, isGroupPoint,
									groupV, ltPix[1], tableheaderHeight,
									history, year, month, count, userView,
									dbpre);
							document.newPage();
							// 小计清空
							if (field != null && field.length > 0) {
								for (int a = 0; a < field.length; a++) {
									pageCount[a] = 0;
								}
							}

							n = 1;
							curPage++;
						}
					 }
						if("1".equals(dataarea)) {
                            writeTableHeader(tableHeaderList, page_height,
                                writer, printGrid, r_bottomn);
                        }
					}

					boolean isFirst = true; // 表的第一单元格
					recordRow++;
					int columnNum = 0;
					String rowValue="";
					float rowLeft=0;
					float rowWidth=0;
					int rowmergeindex=0;
					for (Iterator t = aheaderList.iterator(); t.hasNext();) {
						String[] temp = (String[]) t.next();
						columnNum++;
						topvalue_index++;
						if(temp[27]!=null&& "1".equals(temp[27])) {
                            continue;
                        }
						String gridno=temp[0];
						if ((i != 1 && (i - 1) % h_rows == 0)||i==1) 
						{
							
						}
						else
						{
				    		if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().get(gridno)==null&&("1".equals(this.column)&& "1".equals(this.dataarea))) {
                                continue;
                            }
						}
						int type = 1;
						String border = "1";
						int align = Integer.parseInt(temp[13]);
						String fontName = temp[10];
						int fontSize = Integer.parseInt(temp[12]);
						String afontStyle = temp[11];
                        
						float topPix = page_height- (Float.parseFloat(temp[3]) + (n - 1)* Float.parseFloat(a_height));
						if("1".equals(dataarea))
						{
							if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
							{
//						    	topPix = page_height- tableheaderHeight-(n)* Float.parseFloat(temp[5])+1;
								//wangcq 2014-12-05 多行表头时数据区的第一行的上边距在数据库中也是对的，不需要单独处理表头高度总和、表区域高度等
								topPix = page_height- (Float.parseFloat(temp[3]) + (n - 1)* Float.parseFloat(a_height));
							}
							else
							{
								topPix = page_height- (Float.parseFloat(temp[3]) + (n)* Float.parseFloat(a_height));
							}
						}
						else {
                            topPix = page_height- (Float.parseFloat(temp[3]) + (n - 1)* Float.parseFloat(a_height));
                        }
						 
						if (isFirst) {
                            hr_pix = topPix- tableheaderHeight- (Float.parseFloat(pix) / 2)-this.top_space;
                        }

						float left = Float.parseFloat(temp[2]);
						float width = Float.parseFloat(temp[4]);
						float aheight = Float.parseFloat(temp[5]);
						String context = " ";
						if ("S".equals(temp[7])) {
							context = String.valueOf(i);
						} else if (!isGroupTerm && "P".equals(temp[7])) // 生成照片
						{
							ArrayList list=createPhotoFile2(getDbpre(rowSet, dbpre) + "A00",rowSet.getString("a0100"), "P");
							byte[] buf=(byte[]) list.get(0);
							boolean photo_flag=(Boolean) list.get(1);
							PdfPCell pdfpCell =null;
							if(buf.length>0)
							{
								if(photo_flag) {
									java.awt.Image awtImage=Toolkit.getDefaultToolkit().createImage(buf);
									image= Image.getInstance(awtImage,null);		
								}else {
									image = Image.getInstance(buf);	
								}
								image.scaleAbsolute(width - 5, aheight - 5);
								pdfpCell = new PdfPCell(image, false);
							}
							else
							{
								pdfpCell=new PdfPCell(new Paragraph(""));
							}
							pdfpCell.setFixedHeight(aheight);
							PdfPTable table = new PdfPTable(1);
							pdfpCell.setHorizontalAlignment(Element.ALIGN_CENTER); // 水平居中
							pdfpCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // 垂直居中
							table.setTotalWidth(width); // 设置表的总体宽度
							table.setLockedWidth(true); // 宽度锁定
							table.addCell(pdfpCell);
							table.writeSelectedRows(0, -1, left+this.left_space, topPix-this.top_space, writer.getDirectContent()); // 固定坐标
						} else if (isGroupTerm && "P".equals(temp[7])) {
							context = " ";
						}else if (temp[7]==null|| "H".equals(temp[7])|| "".equals(temp[7])) {
							if(temp[1]!=null) {
                                context = temp[1].replaceAll("`", "");
                            } else {
                                context="";
                            }
						} else // if(temp[7]!=null&&!temp[7].equals("R")&&!temp[7].equals(""))
						{
							if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                                context = rowSet.getString("GroupV");
                            } else
							{
								if("E".equals(temp[7])&&this.isGroupV2)
								{
									context = rowSet.getString("GroupV2");
								}else if("E".equals(temp[7]))
								{
									context="";
								}
								else {
                                    context = rowSet.getString("C" + temp[0]);
                                }
							}

							// 页小计
							if (field != null && field.length > 0) {
								for (int b = 0; b < field.length; b++) {

									if (field[b].equals("C" + temp[0])) {
										if (context != null) {
                                            pageCount[b] += Double.parseDouble(context);
                                        }
										break;
									}
								}
							}
							if (fields != null && fields.length > 0) {
								for (int b = 0; b < fields.length; b++) {
									if (fields[b].equals("C" + temp[0])) {
										if (context != null) {
                                            totalCount[b] += Double.parseDouble(context);
                                        }
										break;
									}
								}
							}
							//计算总计
							if (zj_fields != null && zj_fields.length > 0) {
								for (int b = 0; b < zj_fields.length; b++) {
									if (zj_fields[b].equals("C" + temp[0])) {
										if (context != null) {
                                            totalCount_all[b] += Double.parseDouble(context);
                                        }
										break;
									}
								}
							}
							
							if (context == null || "".equals(context)) {
								context = " ";
								if ("1".equals(zeroPrint)&& "N".equals(temp[9])) {
									context = "0";
								}
							}

							if (context != null && !"".equals(context)&& !" ".equals(context)&& "N".equals(temp[9])) {
								float f = Float.parseFloat(context);
								if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
								{
									if (f == 0) {
                                        context = " ";
                                    } else {
                                        context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                    }

								} else {
                                    context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                }
							}
						}

						if (!"P".equals(temp[7]) || isGroupTerm) {
							if("1".equals(temp[26])) {
                                context=" ";
                            }
//							如果用户对该指标无权限，则不予显示数据
							if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
							{
								if(!temp[25].toLowerCase().startsWith("yk")&&!"nbase".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25])
									&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
								{
									if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
									{
										
									}
									else
									{
								    	context="";
									}
								}
							}
							// //c.L,c.T,c.R,c.B
							String[] ltrb = new String[4];
							ltrb[0] = temp[21];
							ltrb[1] = temp[22];
							ltrb[2] = temp[23];
							ltrb[3] = temp[24];
                            if(mergeitemid[columnNum-1]!=null&&mergeitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(mergeitemid[columnNum-1])&&context!=null&&!"".equals(context.trim())&&!"&nbsp;".equalsIgnoreCase(context)&&("1".equals(column)&& "1".equals(this.dataarea))&&!"P".equals(temp[7]))
        					{
                            	if(recordRow==1)//第一条记录，先不画出，和以后的记录比较，但是要记下绝对定位的坐标
        						{
                            		if(rowSet.isLast())
        							{
                            			if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
        								{
                            				if(rowmergeindex==0)
        									{
        										rowValue=context;
        										rowLeft=left;
        										rowWidth=width;
        									}
        									if(columnNum==1)
        									{
        										if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)
        										{
        											rowmergeindex++;
        										}
        										else
        										{
        											excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        										}
        									}
        									else if(columnNum==aheaderList.size())
        									{
        										if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
        										{
        											if(context.equalsIgnoreCase(rowValue))
        											{
        												rowWidth+=width;
        												excecute(context, rowLeft, topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        											}
        											else
        											{
        												excecute(rowValue, rowLeft, topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        												excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        											}
        										}
        										else
        										{
        											excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        										}
        									}
        									else
        									{
        										if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
        										{
        											if(context.equalsIgnoreCase(rowValue))
        											{
        												rowmergeindex++;
        												rowWidth+=width;
        											}
        											else
        											{
        												excecute(rowValue, rowLeft, topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        												rowValue=context;
        												rowLeft=left;
        												rowWidth=width;
        												rowmergeindex++;
        											}
        										}
        										else if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//before
        										{
        											if(context.equalsIgnoreCase(rowValue))
        											{
        												rowWidth+=width;
        												excecute(context, rowLeft, topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        											}
        											else
        											{
        												excecute(rowValue, rowLeft, topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        												excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);        			
        											}
        										}
        										else if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//after
        										{
        											rowValue=context;
        											rowLeft=left;
        											rowWidth=width;
        											rowmergeindex++;
        										}
        										else
        										{
        											excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        										}
        									}
        								}
                            			else
                            			{
         						        	if (!isGroupTerm && "P".equals(temp[7])) {
                                                context=context;
                                            } else
         						        	{
         						    	     	//如果用户对该指标无权限，则不予显示数据(对于考勤模块放开权限)
         							    	     if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
         							    	     {
         							    	    	FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
         								        	if(fielditem!=null){
         								        		if(!"nbase".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25])
         								        		&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
         								        		{
         								        			if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
         													{
         														
         													}
         													else
         													{
         												    	context="";
         													}
         								        		}
         							    	    	}
         							    	    }
         						    	    }
         						    	
         						        	excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
                            			}
        							}
                            		else
                            		{
                            			toppixvalue[columnNum-1]=topPix;
        					     		mergevalue[columnNum-1]=context;
        					    		if(ismergemain[columnNum-1]!=null&& "true".equalsIgnoreCase(ismergemain[columnNum-1]))//按人员或机构合并
        						    	{
        							    	mainArr[columnNum-1]=mainidvalue;
        						    	}
                            		}
        						}
                            	else
                            	{
                            		//----------------------------------------------------------
        							if(ismergemain[columnNum-1]!=null&& "true".equalsIgnoreCase(ismergemain[columnNum-1]))//按人员或机构合并
        							{
        								if(mainidvalue!=null&&mainArr[columnNum-1]!=null&&!mainArr[columnNum-1].equalsIgnoreCase(mainidvalue))//人员或机构不同了，要画格子
        								{	
        								    if (!isGroupTerm && "P".equals(temp[7])) {
                                                context=context;
                                            } else
        								    {
        							    			//如果用户对该指标无权限，则不予显示数据(对于考勤模块放开权限)
        								    	if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
        								    	{
        								    		FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
        									    	if(fielditem!=null){
        									    		if(!"nbase".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25])
        									    		&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
        									    		{
        									    			if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
        													{
        														
        													}
        													else
        													{
        												    	context="";
        													}
        									    		}
        								    		}
        								    	}
        								    }
        								    if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
        								    {
        								    	if(columnNum==1)//第一个格要看下一个是否合并
        								    	{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
        								    		{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", 1+"");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        								    		}
        								    		else
        								    		{
        								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else if(columnNum==aheaderList.size())//最后一个格子
        								    	{
        								    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
          								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else
        										{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();	   
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        										}
        								    }
        								    else
        								    {
        								    	excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    }
        									toppixvalue[columnNum-1]=topPix;
        									mergeheight[columnNum-1]=0;
        									mainArr[columnNum-1]=mainidvalue;
        								}
        								else
        								{
        									mergeheight[columnNum-1]++;
        								}
        							}
        							else if(mergevalue[columnNum-1]!=null&&!mergevalue[columnNum-1].equalsIgnoreCase(context))//合并的格子的值与上个值不同了，要画格子了
        							{
        								String va="";
        								if (!isGroupTerm && "P".equals(temp[7])) {
                                            va=mergevalue[columnNum-1];
                                        } else
        								{
        									//如果用户对该指标无权限，则不予显示数据(对于考勤模块放开权限)
        									if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
        									{
        										FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
        										if(fielditem!=null){
        											if(!"nbase".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25])
        											&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
        											{	
        												if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
        												{
        													
        												}
        												else
        												{
        											    	mergevalue[columnNum-1]="";
        												}
        											}
        										}
        									}
        								}
        								va=mergevalue[columnNum-1];
        								 if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
        								 {
        									 if(columnNum==1)//第一个格要看下一个是否合并
        								    	{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
        								    		{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", border);
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",va);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        								    		}
        								    		else
        								    		{
        								    			excecute(va, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else if(columnNum==aheaderList.size())//最后一个格子
        								    	{
        								    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",va);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(va, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else
        										{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",va);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(va, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        										}
        								 }
        								 else
        								 {
        									 excecute(va, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								 }
        								toppixvalue[columnNum-1]=topPix;
        								mergeheight[columnNum-1]=0;
        								mergevalue[columnNum-1]=context;
        							}
        							else
        							{
        								mergeheight[columnNum-1]++;
        							}
        							if((recordRow>0&&recordRow%h_rows==0)||rowSet.isLast())//最后一条记录，或者一页结束了
        							{
        								if (!isGroupTerm && "P".equals(temp[7])) {
                                            context=context;
                                        } else
        								{
        									//如果用户对该指标无权限，则不予显示数据(对于考勤模块放开权限)
        									if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
        									{
        										FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
        										if(fielditem!=null){
        											if(!"nbase".equalsIgnoreCase(temp[25])&&!"nbase".equalsIgnoreCase(temp[25])
        											&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
        											{
        												if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
        												{
        													
        												}
        												else
        												{
        											    	context="";
        												}
        											}
        										}
        									}
        								}
        								if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1])&&context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context))// row merge
        								 {
        									 if(columnNum==1)//第一个格要看下一个是否合并
        								    	{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
        								    		{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", "1");
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        								    		}
        								    		else
        								    		{
        								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else if(columnNum==aheaderList.size())//最后一个格子
        								    	{
        								    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", border);
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        								    	}
        								    	else
        										{
        								    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
        											{
        								    			LazyDynaBean bean = new LazyDynaBean();
        								    			bean.set("align", align+"");
        								    			bean.set("fontName", fontName);
        								    			bean.set("fontSize", fontSize+"");
        								    			bean.set("fontStyle", afontStyle);
        								    			bean.set("border", border);
        								    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
        								    			bean.set("left", left+"");
        								    			bean.set("width", width+"");
        								    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*aheight));
        								    			bean.set("content",context);
        								    			bean.set("cloumn",columnNum+"");
        								    			bean.set("gridno", temp[0]);
        								    			bean.set("ltrb", ltrb);
        								    			rowMargeList.add(bean);
        											}
        								    		else{
        								    			excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								    		}
        										}
        								 }
        								else
        								{
        									excecute(context, left,toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  	
        								}
        							}
                            	}
        					}
                            else
                            {
        						if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1])&&context!=null&&!"".equals(context.trim())&&!"&nbsp;".equalsIgnoreCase(context)&&!"&nbsp;&nbsp;".equalsIgnoreCase(context)&&!"P".equals(temp[7]))// row merge
        						{
        							if(rowmergeindex==0)
        							{
        								rowValue=context;
        								rowLeft=left;
        								rowWidth=width;
        							}
        							if(columnNum==1)
        							{
        								if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)
        								{
        									rowmergeindex++;
        								}
        								else
        								{
        									excecute(context, left,topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								}
        							}
        							else if(columnNum==aheaderList.size())
        							{
        								if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
        								{
        									if(context.equalsIgnoreCase(rowValue))
        									{
        										rowWidth+=width;
        										excecute(context, rowLeft,topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        									}
        									else
        									{
        										excecute(rowValue, rowLeft,topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        										excecute(context, left,topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  		
        									}
        								}
        								else
        								{
        									excecute(context, left,topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								}
        							}
        							else
        							{
        								if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
        								{
        									if(context.equalsIgnoreCase(rowValue))
        									{
        										rowmergeindex++;
        										rowWidth+=width;
        									}
        									else
        									{
        										excecute(rowValue, rowLeft,topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        										rowValue=context;
        										rowLeft=left;
        										rowWidth=width;
        										rowmergeindex++;
        									}
        								}
        								else if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//before
        								{
        									if(context.equalsIgnoreCase(rowValue))
        									{
        										rowWidth+=width;
        										excecute(context, rowLeft,topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  

        									}else
        									{
        										excecute(rowValue, rowLeft,topPix, rowWidth, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        										excecute(context, left,topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        									}
        								}
        								else if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//after
        								{
        									rowValue=context;
        									rowLeft=left;
        									rowWidth=width;
        									rowmergeindex++;
        								}
        								else
        								{
        									excecute(context, left,topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);  
        								}
        							}
        							
        						}
        						else
        						{
        							if(mergeitemid[columnNum-1]!=null&&mergeitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(mergeitemid[columnNum-1])&&("1".equals(column)&& "1".equals(this.dataarea))&&("".equalsIgnoreCase(context.trim())|| "&nbsp;".equalsIgnoreCase(context)|| "&nbsp;&nbp;".equalsIgnoreCase(context))&&mergevalue[columnNum-1]!=null&&!"".equals(mergevalue[columnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[columnNum-1]))
        							{
        									if(!(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1])))// row merge
        									{
        										excecute(mergevalue[columnNum-1], left, toppixvalue[columnNum-1], width, (mergeheight[columnNum-1]+1)*aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        									}
        							}
						        	excecute(context, left, topPix, width, aheight,align, afontStyle, fontSize,fontName, writer, 1,ltrb);
        						}
                            }
							isFirst = false;
						}
					}

					for (Iterator t = aheaderList.iterator(); t.hasNext();) {/**画虚线*/
						String[] temp = (String[]) t.next();
						if(temp[27]!=null&& "1".equals(temp[27])) {
                            continue;
                        }
						float topPix = (Float.parseFloat(temp[3]) + (n)* Float.parseFloat(a_height));
						float left = Float.parseFloat(temp[2]);
						float width = Float.parseFloat(temp[4]);
						float aheight = Float.parseFloat(temp[5]);
						// 处理虚线 c.L,c.T,c.R,c.B
						String[] ltrb = new String[4];
						ltrb[0] = temp[21];
						ltrb[1] = temp[22];
						ltrb[2] = temp[23];
						ltrb[3] = temp[24];
						dealDashed(ltrb[0], ltrb[1], ltrb[2], ltrb[3],left, topPix, width, aheight, page_height,writer);
					}

					// #######################################################画分格线##########################
					if (columnLine != null && "1".equals(columnLine)) {
						PdfContentByte a_cb = writer.getDirectContent();
						a_cb.setLineWidth(1f);
						a_cb.setLineDash(3, 3, 0);
						a_cb.moveTo(ltPix[0]+this.left_space, hr_pix);
						a_cb.lineTo(ltPix[0]+this.left_space + tableBodyWidth, hr_pix);
						a_cb.setColorStroke(new Color(0, 0, 0));
						a_cb.stroke();
						a_cb.setLineDash(1);
					}
					i++;
					n++;
					xx++;
				
				}
				if(rowMargeList.size()>0)
				{
					HashMap existMap = new HashMap();
				    for(int out=0;out<rowMargeList.size();out++)
				    {
				    	if(existMap.get(out+"")!=null) {
                            continue;
                        }
				    	LazyDynaBean outBean = (LazyDynaBean)rowMargeList.get(out);
					    String top = (String)outBean.get("topvalue");
					    String cloumn = (String)outBean.get("cloumn");
					    String mergeaheight=(String)outBean.get("height");
					    String content=(String)outBean.get("content");
					    String gridno = (String)outBean.get("gridno");
					    String left = (String)outBean.get("left");
						String width=(String)outBean.get("width");
						String fontName=(String)outBean.get("fontName");
						String fontSize=(String)outBean.get("fontSize");
						String fontStyle = (String)outBean.get("fontStyle");
						String  border=(String)outBean.get("border");
						String align=(String)outBean.get("align");
						String[] ltrb = (String[])outBean.get("ltrb");
						int columnNum=1;
						int col = Integer.parseInt(cloumn);
					    for(int in=0;in<rowMargeList.size();in++)
					    {
					     	LazyDynaBean inBean =(LazyDynaBean)rowMargeList.get(in);
					    	String intop = (String)inBean.get("topvalue");
						    String incloumn = (String)inBean.get("cloumn");
						    String inheight=(String)inBean.get("height");
						    String incontent=(String)inBean.get("content");
						    String ingridno = (String)inBean.get("gridno");
						    String inwidth=(String)inBean.get("width");
						    int inclo = Integer.parseInt(incloumn);
						    if((top.equalsIgnoreCase(intop)&&cloumn.equalsIgnoreCase(incloumn))||!(top.equalsIgnoreCase(intop)))//同一个格子
						    {
							    continue;
						    }
						    if(top.equalsIgnoreCase(intop)&&mergeaheight.equalsIgnoreCase(inheight)&&content.equalsIgnoreCase(incontent)&&inclo==(col+columnNum))
						    {
								columnNum++;
								width=String.valueOf(Float.parseFloat(width)+Float.parseFloat(inwidth));
								existMap.put(in+"", "1");
						    }
						    else
						    {
						    	continue;
						    }
					    }
					    excecute(content, Float.parseFloat(left),  Float.parseFloat(top),  Float.parseFloat(width),  Float.parseFloat(mergeaheight),Integer.parseInt(align), fontStyle, Integer.parseInt(fontSize),fontName, writer, 1,ltrb);
					   /* margeBuf.append(executeAbsoluteTable2(Integer.parseInt(type), Integer.parseInt(align),fontName, fontSize, fontStyle, border, top, 
						    	left, width, mergeheight,content, style_name));*/
			    	}
				}
				/**
				 * 是否打印空行 打印空行时记录页累计页总计不需要重新生成新页，
				 * 在已打印的空行上记录
				 */
				boolean isprintEmp_row=false;
				//空行打印
				if(n<=h_rows&& "1".equals(emptyRow))
				{
					while(n<=h_rows)
					{
						getEmptyRows(columnLine, Float.parseFloat(pix), Float.parseFloat(a_height), 0f,aheaderList, "1", n,writer,page_height,tableBodyWidth,ltPix);
						n++;
					}			
					isprintEmp_row=true;
				}
				
				if(n>h_rows&&!isprintEmp_row) {//行数超出每页行数 累计 小计写入下一页
					getTitle(tabid,isGroupTerm, page_height, writer, Integer
							.parseInt(a_height), n - 2, titelList, user, String
							.valueOf(curPage), h_rows, infor_Flag, tableName,
							isGroupPoint, groupV, ltPix[1], tableheaderHeight,
							history, year, month, count, userView, dbpre);
					document.newPage();
					n=1;
				}
				boolean flag=false;
				/* 显示页小计 */
				if (nOperation == 1 || nOperation == 3) {
					getPageCountRows(columnLine, Float
							.parseFloat(pix), Float
							.parseFloat(a_height), 0f,
							aheaderList, "1", n, field,
							zeroPrint, pageCount,
							"hmuster.label.pageCount", writer,
							page_height, tableBodyWidth, ltPix);
					flag=true;
					n++;
				}
				/* 显示累计 */
				if (nOperation == 2 || nOperation == 3) {
					getPageCountRows(columnLine, Float
							.parseFloat(pix), Float
							.parseFloat(a_height), 0f,
							aheaderList, "1", n, fields,
							zeroPrint, totalCount,
							"hmuster.label.toatlCount", writer,
							page_height, tableBodyWidth, ltPix);
					flag=true;
					n++;
				}
				// 总计 topPix 
				if (this.iszj) {
					getPageCountRows(columnLine, Float
							.parseFloat(pix), Float
							.parseFloat(a_height), 0f,
							aheaderList, "1", n, zj_fields,
							zeroPrint, totalCount_all,
							"workdiary.message.total", writer,
							page_height, tableBodyWidth, ltPix);
					flag=true;
					
					n++;
				}
				if(flag||(n-1<=h_rows&&n!=1)) {
					// 生成标题
					getTitle(tabid,isGroupTerm, page_height, writer, Integer
							.parseInt(a_height), n - 2, titelList, user, String
							.valueOf(curPage), h_rows, infor_Flag, tableName,
							isGroupPoint, groupV, ltPix[1], tableheaderHeight,
							history, year, month, count, userView, dbpre);
				}
			}
			document.close();
			this.deletePicture(photoList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(rowSet!=null)
				{
					rowSet.close();
				}
				if(subSet!=null)
				{
					subSet.close();
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	
	
	public String isAdd(String noperation,int flag)
	{
		String isAdd="0";
		if(noperation!=null)
		{
			/*String s=Integer.toBinaryString(Integer.parseInt(noperation));
			String d=s;
			for(int i=4;i>0;i--)
			{
				if(i>s.length())
					d="0"+d;
			}
			isAdd=String.valueOf(d.charAt(flag));*/
			if(flag==0)
			{
				if("8".equals(noperation)|| "9".equals(noperation)|| "10".equals(noperation)|| "11".equals(noperation)
						|| "12".equals(noperation)|| "13".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==1)
			{
				if("4".equals(noperation)|| "5".equals(noperation)|| "7".equals(noperation)|| "6".equals(noperation)
						|| "12".equals(noperation)|| "13".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==2)
			{
				if("2".equals(noperation)|| "3".equals(noperation)|| "6".equals(noperation)|| "7".equals(noperation)
						|| "10".equals(noperation)|| "11".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==3)
			{
				if("1".equals(noperation)|| "3".equals(noperation)|| "5".equals(noperation)|| "7".equals(noperation)
						|| "9".equals(noperation)|| "13".equals(noperation)|| "11".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
		}
		return isAdd;
	}

	public ArrayList getFieldandFieldsList(ArrayList list) {
		ArrayList a_list = new ArrayList();
		StringBuffer fieldName = new StringBuffer("");
		StringBuffer fieldNames = new StringBuffer("");
		StringBuffer fzhjNames=new StringBuffer("");
		StringBuffer zjNames=new StringBuffer("");
		
		for (Iterator t = list.iterator(); t.hasNext();) {
			String[] tt = (String[]) t.next();
			if ("1".equals(isAdd(tt[1],3))) {
				this.isyxj=true;
				fieldName.append("#" + tt[0]);
			}

			if ("1".equals(isAdd(tt[1],2))) {
				this.isylj=true;
				fieldNames.append("#" + tt[0]);
			}
			
			if ("1".equals(isAdd(tt[1],1))) {
				this.isfzhj=true;
				fzhjNames.append("#" + tt[0]);
			}
			
			if ("1".equals(isAdd(tt[1],0))) {
				this.iszj=true;
				zjNames.append("#" + tt[0]);
			}

		}
		String[] field = null;
		String[] fields = null;
		String[] zj_field=null;
		float[] pageCount = null;
		if (fieldName.length() > 2) {
			field = fieldName.substring(1).split("#"); // 需小计的列名
		}
		if (fieldNames.length() > 2) {
			fields = fieldNames.substring(1).split("#"); // 需累计的列名
		}
		if(zjNames.length() > 2) {
			zj_field = zjNames.substring(1).split("#");//需要总计的列名
		}
		if(fzhjNames.length()>2)
		{
			this.fzhj=fzhjNames.substring(1).split("#"); //分组合计
		}
		if(zjNames.length()>2)
		{
			this.zj=zjNames.substring(1).split("#"); //总计
		}
		a_list.add(field);
		a_list.add(fields);
		a_list.add(zj_field);
		return a_list;
	}

	
	
	
	
	/**
	 * 生成分栏页小计pdf
	 * 
	 * @param bottomnList
	 *            列集合
	 * @param field
	 *            需小计的列
	 * @param zeroPrint
	 *            是否零打印
	 * @param pageCount
	 *            页小计值
	 * @param rowHeight
	 *            行高
	 * @return String
	 */
	public void getEmptyRows(String columnLine, float pix,
			float h_tableHeight, float h_tableWidth, ArrayList headerList,
			String column, int n, PdfWriter writer,
			float page_height, float tableBodyWidth, float[] ltPix) {
		
		float hr_pix = 0; // 分隔线的坐标
		boolean isFirst = true; // 表的第一单元格
		for (Iterator t = headerList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			int type = 1;
			String border = "1";
			int align = Integer.parseInt(temp[13]);
			String fontName = temp[10];
			int fontSize = Integer.parseInt(temp[12]);
			String afontStyle = temp[11];
			float topPix = Float.parseFloat(temp[3]);
			float left = Float.parseFloat(temp[2]);
			if ("1".equals(column)) {
				if("1".equals(dataarea))
				{
					if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
					{
						if(this.headerHeight_haveTitle>0) {
							topPix = page_height
									- (Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight);//bug号：38374
						}
						else {
							topPix = page_height-(n-1)* Float.parseFloat(temp[5])-13;
						}
					}
					else
					{
						topPix = page_height- (Float.parseFloat(temp[3]) + n* h_tableHeight);
					}
					
				}
				else {
					topPix = page_height- (Float.parseFloat(temp[3]) + (n - 1)* h_tableHeight);
				}
				//topPix = page_height- (Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight);
				if (isFirst) {
                    hr_pix = topPix- h_tableHeight+ (pix / 2)-this.top_space;
                }
//					hr_pix = topPix + (pix / 2);
			} else {
				topPix = page_height - Float.parseFloat(temp[3]);
				left = Float.parseFloat(temp[2]) + (n - 1) * h_tableWidth;
//				hr_pix = ltPix[0] + (n - 1) * h_tableWidth + h_tableWidth - pix
//						/ 2;
				hr_pix = ltPix[0] + (n - 1)
				* (tableBodyWidth + pix)
				+ tableBodyWidth + pix / 2;
			}
			float width = Float.parseFloat(temp[4]);
			float aheight = Float.parseFloat(temp[5]);
			String context = "";
			

			{
				// c.L,c.T,c.R,c.B
				String[] ltrb = new String[4];
				ltrb[0] = temp[21];
				ltrb[1] = temp[22];
				ltrb[2] = temp[23];
				ltrb[3] = temp[24];
				excecute(context, left, topPix, width, aheight, align,
						afontStyle, fontSize,fontName, writer, 1, ltrb);
			}
			isFirst = false;

		}

		for (Iterator t = headerList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			float topPix = Float.parseFloat(temp[3]);
			float left = Float.parseFloat(temp[2]);
			if ("1".equals(column)) {
				topPix = (Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight);
			} else {
				topPix = Float.parseFloat(temp[3]);
				left = Float.parseFloat(temp[2]) + (n - 1) * h_tableWidth;
			}
			float width = Float.parseFloat(temp[4]);
			float aheight = Float.parseFloat(temp[5]);
			// 处理虚线 c.L,c.T,c.R,c.B
			String[] ltrb = new String[4];
			ltrb[0] = temp[21];
			ltrb[1] = temp[22];
			ltrb[2] = temp[23];
			ltrb[3] = temp[24];
//			ltrb[0] = "1";
//			ltrb[1] = "1";
//			ltrb[2] = "1";
//			ltrb[3] = "1";
			dealDashed(ltrb[0], ltrb[1], ltrb[2], ltrb[3], left, topPix,
					width, aheight, page_height, writer);
		}

		// #######################################################画分格线##########################
		if (columnLine != null && "1".equals(columnLine)) {
			PdfContentByte a_cb = writer.getDirectContent();
			a_cb.setLineWidth(1f);
			a_cb.setLineDash(3, 3, 0);
			if ("1".equals(column)) {
//				a_cb.moveTo(ltPix[0], hr_pix);
//				a_cb.lineTo(ltPix[0] + tableBodyWidth, hr_pix);
				a_cb.moveTo(ltPix[0]+this.left_space, hr_pix);
				a_cb.lineTo(ltPix[0]+this.left_space + tableBodyWidth, hr_pix);
			} else {
//				a_cb.moveTo(hr_pix, page_height - ltPix[1]);
//				a_cb.lineTo(hr_pix, page_height - ltPix[1] - h_tableHeight);
				a_cb.moveTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space);
				a_cb.lineTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space-h_tableHeight);
			}
			a_cb.setColorStroke(new Color(0, 0, 0));
			a_cb.stroke();
			a_cb.setLineDash(1);
		}
	}
	
	
	
	
	/**
	 * 生成分栏页小计pdf
	 * 
	 * @param bottomnList
	 *            列集合
	 * @param field
	 *            需小计的列
	 * @param zeroPrint
	 *            是否零打印
	 * @param pageCount
	 *            页小计值
	 * @param rowHeight
	 *            行高
	 * @return String
	 */
	public void getPageCountRows(String columnLine, float pix,
			float h_tableHeight, float h_tableWidth, ArrayList headerList,
			String column, int n, String[] field, String zeroPrint,
			double[] pageCount, String resourceProperty, PdfWriter writer,
			float page_height, float tableBodyWidth, float[] ltPix) {
		float hr_pix = 0; // 分隔线的坐标
		boolean isFirst = true; // 表的第一单元格
		for (Iterator t = headerList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			int type = 1;
			String border = "1";
			int align = Integer.parseInt(temp[13]);
			String fontName = temp[10];
			int fontSize = Integer.parseInt(temp[12]);
			String afontStyle = temp[11];
			float topPix = Float.parseFloat(temp[3]);
			float left = Float.parseFloat(temp[2]);
			if ("1".equals(column)) {
				topPix = page_height
						- (Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight);
				if (isFirst) {
                    hr_pix = topPix- h_tableHeight+(pix / 2)-this.top_space;
                }
//					hr_pix = topPix + (pix / 2);
			} else {
				topPix = page_height - Float.parseFloat(temp[3]);
				left = Float.parseFloat(temp[2]) + (n - 1) * h_tableWidth;
//				hr_pix = ltPix[0] + (n - 1) * h_tableWidth + h_tableWidth - pix
//						/ 2;
				hr_pix = ltPix[0] + (n - 1)
				* (tableBodyWidth + pix)
				+ tableBodyWidth + pix / 2;
			}
			float width = Float.parseFloat(temp[4]);
			float aheight = Float.parseFloat(temp[5]);
			String context = "";
			if ("S".equals(temp[7])) {
				context = ResourceFactory.getProperty(resourceProperty);
			} else if ("P".equals(temp[7])) // 生成照片
			{
				context = "";
			} else if ("H".equals(temp[7])) {
				context = temp[1].replaceAll("`", "");
			} else if (temp[7] != null && !"R".equals(temp[7])
					&& !"".equals(temp[7])) {
				for (int b = 0; b < field.length; b++) {
					if (field[b].equals("C" + temp[0])) {
						if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
						{
							if (pageCount[b] == 0) {
                                context = "";
                            } else {
                                context = round(String.valueOf(pageCount[b]),
                                        Integer.parseInt(temp[6]));
                            }
						} else {
                            context = round(String.valueOf(pageCount[b]),
                                    Integer.parseInt(temp[6]));
                        }
						break;
					}
				}
			}else if("R".equals(temp[7])){
				for (int b = 0; b < field.length; b++) {
					if (field[b].equals("C" + temp[0])) {
						if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
						{
							if (pageCount[b] == 0) {
                                context = "";
                            } else {
                                context = round(String.valueOf(pageCount[b]),
                                        Integer.parseInt(temp[6]));
                            }
						} else {
                            context = round(String.valueOf(pageCount[b]),
                                    Integer.parseInt(temp[6]));
                        }
						break;
					}
				}
			}

			{
//				如果用户对该指标无权限，则不予显示数据
				if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
				{
					if(!"nbase".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25])
						&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
					{
						if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
						{
							
						}
						else
						{
					    	context="";
						}
					}
				}
				// c.L,c.T,c.R,c.B
				String[] ltrb = new String[4];
				ltrb[0] = temp[21];
				ltrb[1] = temp[22];
				ltrb[2] = temp[23];
				ltrb[3] = temp[24];
				excecute(context, left, topPix, width, aheight, align,
						afontStyle, fontSize,fontName, writer, 1, ltrb);
			}
			isFirst = false;

		}

		for (Iterator t = headerList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			float topPix = Float.parseFloat(temp[3]);
			float left = Float.parseFloat(temp[2]);
			if ("1".equals(column)) {
				topPix = (Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight);
			} else {
				topPix = Float.parseFloat(temp[3]);
				left = Float.parseFloat(temp[2]) + (n - 1) * h_tableWidth;
			}
			float width = Float.parseFloat(temp[4]);
			float aheight = Float.parseFloat(temp[5]);
			// 处理虚线 c.L,c.T,c.R,c.B
			String[] ltrb = new String[4];
			ltrb[0] = temp[21];
			ltrb[1] = temp[22];
			ltrb[2] = temp[23];
			ltrb[3] = temp[24];
//			ltrb[0] = "1";
//			ltrb[1] = "1";
//			ltrb[2] = "1";
//			ltrb[3] = "1";
			dealDashed(ltrb[0], ltrb[1],ltrb[2], ltrb[3], left, topPix,
					width, aheight, page_height, writer);
		}

		// #######################################################画分格线##########################
		if (columnLine != null && "1".equals(columnLine)) {
			PdfContentByte a_cb = writer.getDirectContent();
			a_cb.setLineWidth(1f);
			a_cb.setLineDash(3, 3, 0);
			if ("1".equals(column)) {
//				a_cb.moveTo(ltPix[0], hr_pix);
//				a_cb.lineTo(ltPix[0] + tableBodyWidth, hr_pix);
				a_cb.moveTo(ltPix[0]+this.left_space, hr_pix-40);
				a_cb.lineTo(ltPix[0]+this.left_space + tableBodyWidth, hr_pix-40);
			} else {
//				a_cb.moveTo(hr_pix, page_height - ltPix[1]);
//				a_cb.lineTo(hr_pix, page_height - ltPix[1] - h_tableHeight);
				a_cb.moveTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space);
				a_cb.lineTo(hr_pix+this.left_space, page_height - ltPix[1]-this.top_space
						- h_tableHeight);
			}
			
			a_cb.setColorStroke(new Color(0, 0, 0));
			a_cb.stroke();
			a_cb.setLineDash(1);
		}
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * 生成（分栏）高级花名册的SQL && 是否包含照片
	 * 
	 */
	public ArrayList getHorizentalFenceSQL(ArrayList tableHeaderList,
			String infor_Flag, String tableName, String isGroupPoint,
			UserView userView, String dbpre) {
		ArrayList list = new ArrayList();

		try {
			boolean isPhoto = false;
			boolean isGroupTerm = false; // 是表内条件否有分组指标
			/* 查询语句 */
			StringBuffer h_sql = new StringBuffer("select ");
			StringBuffer h_sql_ext = new StringBuffer("");
			for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if (temp[7] != null && ("G".equals(temp[7])|| "R".equals(temp[7]))) {
					isGroupTerm = true;
				}
				if (temp[7] != null && ("E".equals(temp[7]))) {
					isGroupTerm2 = true;
				}
				if (temp[7] != null && !"S".equals(temp[7])&&!"G".equals(temp[7])&&!"E".equals(temp[7])
						&& !"H".equals(temp[7]) && !"P".equals(temp[7])
						&& !"R".equals(temp[7]) && !"".equals(temp[7])) {
					h_sql_ext.append(",C");
					h_sql_ext.append(temp[0]);
				}
				if ("P".equals(temp[7])) {
                    isPhoto = true;
                }
			}

			h_sql_ext.append(",GroupV,GroupN ");
			if(this.isGroupV2)
			{
		    	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupTerm&&isGroupPoint != null && "1".equals(isGroupPoint))
		     	{
		    		h_sql_ext.append(",GroupV2,GroupN2");
		     	}
		    	else if(isGroupTerm) {
                    h_sql_ext.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
                } else {
                    h_sql_ext.append(",GroupV2,GroupN2");
                }
			}
			if ("1".equals(infor_Flag)) {
			    h_sql_ext.append(",NBASE");
				h_sql_ext.append(",a0100");
			}
			else if ("2".equals(infor_Flag)) {
                h_sql_ext.append(",b0110");
            } else if ("3".equals(infor_Flag)) {
                h_sql_ext.append(",e01a1");
            }
			String orderby="";
			if (isGroupPoint != null && "1".equals(isGroupPoint)) {
				HmusterXML hmxml = new HmusterXML(this.conn,tabID);
				String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
				GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
				String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
				GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
				if(GROUPFIELD.trim().length()>4){
					if("B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
						h_sql_ext.append(",(select A0000 from organization where codeitemid=");
						h_sql_ext.append(tableName);
						h_sql_ext.append(".GroupN) AS A0000 ");
						if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
							    orderby=" group by "+Sql_switcher.month("a00z0");	
							}
							else
							{
					    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						    	{
						    		if(GROUPFIELD2.trim().length()>4)
					    			{
					    				FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
					    				if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
					    					h_sql_ext.append(",(select A0000 from organization where codeitemid=");
							 	     		h_sql_ext.append(tableName);
							    			h_sql_ext.append(".GroupN2) AS A00002 ");
						    				orderby=" order by A0000,A00002,recidx";
						    			}else
						    			{
						     				orderby=" order by A0000,GroupN2,recidx";
						    			}
					      			}
						    		else
					    			{
					    				orderby=" order by A0000,GroupN2,recidx";
					    			}
					    		}
					    		else
					    		{
					     			orderby=" order by A0000,recidx";
				    			}
							}
						} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
								}
								else
								{
							    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
								}
							}
							else
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by A0000 ";
								}
								else
								{
						        	orderby=" group by GroupN,GroupV order by A0000 ";
								}
							}
						}
						
					}else{
						if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
							    orderby=" group by "+Sql_switcher.month("a00z0");	
							}
							else
							{
					      		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
					    		{
					    			if(GROUPFIELD2.trim().length()>4)
					    			{
						    			FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
							    		if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
							    			h_sql_ext.append(",(select A0000 from organization where codeitemid=");
							    			h_sql_ext.append(tableName);
							    			h_sql_ext.append(".GroupN2) AS A00002 ");
							    			orderby=" order by A0000,A00002,recidx";
						        		}else
							    		{
							    			orderby=" order by A0000,GroupN2,recidx";
							    		}
						    		}
						    		else
						    		{
						    			orderby=" order by A0000,GroupN2,recidx";
						    		}
						    	}
					    		else
					    		{
					    			orderby=" order by A0000,recidx";
					    		}
							}
						} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
								}else
								{
							    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
								}
							}
							else{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
								}
								else
								{
						        	orderby=" group by GroupN,GroupV order by GroupN ";
								}
							}
						}
					}
				}else{
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else
						{
				    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
				    		{
				    			if(GROUPFIELD2.trim().length()>4)
				    			{
						    		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
				    				if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
							    		h_sql_ext.append(",(select A0000 from organization where codeitemid=");
							    		h_sql_ext.append(tableName);
							        	h_sql_ext.append(".GroupN2) AS A00002 ");
							     		orderby=" order by A0000,A00002,recidx";
					    			}else
						    		{
						    			orderby=" order by A0000,GroupN2,recidx";
					     			}
					    		}
					    		else
					     		{
					    			orderby=" order by A0000,GroupN2,recidx";
					    		}
					    	}
				    		else
				    		{
				    			orderby=" order by A0000,recidx";
					    	}
						}
					} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}
						}
						else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
							}else
							{
					        	orderby=" group by GroupN,GroupV order by GroupN ";
							}
						}
					}
				}
			}else{
				if (!isGroupTerm) {
                    orderby=" order by recidx";
                }
				if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
				{
					orderby=" group by "+Sql_switcher.month("a00z0");
				}
			}
			
			h_sql_ext.append(" from ");
			h_sql_ext.append(tableName);

			if (isGroupTerm||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
				h_sql_ext = new StringBuffer("");
				h_sql_ext.append(getGroupSql(tableHeaderList, infor_Flag,
						tableName, isGroupPoint));
			}
			/* 权限控制 */
			h_sql_ext.append(this.privConditionStr);
			if("5".equals(this.modelFlag))
			{
				if(this.privConditionStr.trim().length()>0)
				{
					if(this.getSql().trim().length()>0) {
                        h_sql_ext.append(" and ("+this.getSql()+")");
                    }
				}
				else
				{
					if(this.getSql().trim().length()>0) {
                        h_sql_ext.append(" where ("+this.getSql()+")");
                    }
				}
			}
			h_sql_ext.append(orderby);

			h_sql.append(h_sql_ext.substring(1));

			list.add(h_sql.toString());
			list.add(new Boolean(isPhoto));
			list.add(Boolean.valueOf(isGroupTerm));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	
	
	
//	数组清零
	public double[] initCount(String[] info)
	{
		double[] count=null;
		if (info != null && info.length > 0) {
			count = new double[info.length];
			for (int a = 0; a < info.length; a++) {
				count[a] = 0;
			}
		}
		return count;
	}
	
	private String getDbpre(ResultSet resultSet, String dbpre) {
	    String s=null;
	    try{
	        s=resultSet.getString("NBASE");
	    }catch(Exception e){}
	    if(s==null) {
            s=dbpre;
        }
	    return s;
	}
	
	
	/**
	 * 生成（不分栏）高级花名册的pdf
	 * 
	 * @param document
	 * @param bottomnList
	 * @param infor_Flag
	 * @param hmusterBo
	 * @param tableName
	 * @param isGroupPoint
	 * @param emptyRow
	 * @param fontStyle
	 * @param writer
	 * @param zeroPrint
	 * @param user
	 * @param columnDefinitionSize
	 * @param dbpre
	 * @param tableBodyWidth
	 * @param tableheaderHeight
	 * @param hmusterViewBo
	 * @param tabid
	 * @param isAutoCount
	 * @param pageRows
	 * @param nOperation
	 * @param list3
	 * @param page_height
	 * @param history
	 * @param year
	 * @param month
	 * @param count
	 * @param tableHeaderList
	 * @throws GeneralException
	 */
	public void getNoFenceHmuster(Document document, ArrayList bottomnList,
			String infor_Flag, HmusterBo hmusterBo, String tableName,
			String isGroupPoint, String emptyRow, String[] fontStyle,
			PdfWriter writer, String zeroPrint, String user,
			float[] columnDefinitionSize, String dbpre, float tableBodyWidth,
			float tableheaderHeight, HmusterViewBo hmusterViewBo, String tabid,
			String isAutoCount, String pageRows, int nOperation,
			ArrayList list3, float page_height, String history, String year,
			String month, String count, ArrayList tableHeaderList,
			float r_bottomn, String printGrid)
			throws GeneralException {
		String existColumn = "";
		infor_Flag=infor_Flag!=null&&infor_Flag.trim().length()>0?infor_Flag:"";
		ArrayList list2 = new ArrayList(); // 与上面相对应得列		
		String isPhoto ="";
		
		Connection a_conn = AdminDb.getConnection();	
		ResultSet resultSet=null;
		Statement stmt=null;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		boolean isGroupTerm = false; // 是表内条件否有分组指标
		
		
		try {
			ArrayList tempList = getSQL(dbpre, bottomnList, infor_Flag,
					hmusterBo, isGroupPoint, tableName, userView);
			ArrayList dashedGridList = getDashedGridList(bottomnList);
			String sql = (String) tempList.get(0);
			String sql2 = (String) tempList.get(1);
			isPhoto = (String) tempList.get(2);
			list2 = (ArrayList) tempList.get(3);
			existColumn = (String) tempList.get(4);
			isGroupTerm = ((Boolean) tempList.get(5)).booleanValue();
			float[] lth = new float[3]; // 表头的最左边\最上边\总高
			String lthSql = "select min(Rleft) a,min(RTop) b  from muster_cell where tabid="
					+ tabid;
			rowSet = dao.search(lthSql);
			if (rowSet.next()) {
				lth[0] = rowSet.getFloat("a") - lt;
				lth[1] = rowSet.getFloat("b");
				lth[2] = tableheaderHeight;
			}
			
			int height = 0;
			if (sql2.length() > 7)
				//height =Integer.parseInt(PubFunc.round(String.valueOf(hmusterViewBo.getMaxColumnHeight(sql2, list2)*0.8),0)); // 得到查询的所有数据中最高列的高度
            {
                height =hmusterViewBo.getMaxColumnHeight(sql2, list2); // 得到查询的所有数据中最高列的高度
            } else {
                height = 25;
            }
			if (isPhoto.trim().length()>0) {
				int a_width=1;
				if(isPhoto.indexOf(".")!=-1) {
                    a_width=(Integer.parseInt(isPhoto.substring(0,isPhoto.indexOf("."))));
                } else {
                    a_width=Integer.parseInt(isPhoto);
                }
				int aheight=a_width+a_width/2;
				if (height < aheight) {
                    height = aheight;
                }
			}
            
			ArrayList titleInfoList = getTableBodyHeight(tabid, lth[1],
					tableheaderHeight, lt, "0", hmusterViewBo);
			ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
			int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体高度（像素）
			int rows = hmusterViewBo.getPageRows(tabid, height, isAutoCount,
					pageRows, lth, nOperation, bodyHeight); // 求出每页的行数


			String[] field = null;
			String[] fields = null;
			ArrayList fieldList = getFieldandFieldsList(list3);
			field = (String[]) fieldList.get(0);
			fields = (String[]) fieldList.get(1);
			/* 如果用户指定行数为可行,则重算行高 */
			if (pageRows.equals(String.valueOf(rows))) {
				int hrows = rows;
				if (field != null && field.length > 0&&this.isyxj) {
					hrows+=1;
				}
				if (fields != null && fields.length > 0&&this.isylj) {
					hrows+=1;
				}
				if (isGroupTerm&&this.fzhj != null && this.fzhj.length > 0&&this.isfzhj) {
					hrows+=1;	
				}
				if(height<bodyHeight / hrows) {
                    height = bodyHeight / hrows;
                }
			}
			while(height*rows>bodyHeight) {
                height--;
            }
			/** ##################### 生成表格 ############################ **** */
			{
				int curPage = 1; // 页数
				String groupV = "n";
				String groupN="n";
				int nn = 0;
				int i = 1; // 序号
				int n = 1; // 每页的实际行数
				int groups=0;//分组不分页 记录合计行行数
				//stmt=a_conn.createStatement();=con.prepareStatement(csql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);  
				//stmt=a_conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
				//resultSet=stmt.executeQuery(sql);
				resultSet=dao.search(sql);
				double[] pageCount=initCount(field);
				double[] totalCount = initCount(fields);
				double[] fzhjCount=initCount(this.fzhj);
				double[] zjCount = initCount(this.zj);
				
				itemHeArr=itemHeArr!=null?itemHeArr:"";
				String heitemid[] = new String[bottomnList.size()];
				String heitemvalue[] = new String[bottomnList.size()];
				String herevalue[] = new String[bottomnList.size()];
				String isHeMain[] = new String[bottomnList.size()];
				int m[]=new int[bottomnList.size()];
				String[] heArr = itemHeArr.split(",");
				for(int h=0;h<heArr.length;h++){
					if(heArr[h]!=null&&heArr[h].length()>1){
						String heirem[] = heArr[h].split(":");
						if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
							heitemid[h] = heirem[0];
							heitemvalue[h] = " ";
							herevalue[h] = " ";
							m[h] = 0;
						}
						if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
							isHeMain[h] = "true";
						}
					}
				}
				int countAll = 0;
				if(isGroupPoint == null||!"1".equals(isGroupPoint)){
					RowSet rs = dao.search("select max(recidx) as recidx from "+tableName);
					if(rs.next()){
						countAll = rs.getInt("recidx");
					}
				}
				String mainidvalue = "";
				String mainArr[] = new String[bottomnList.size()];
				ArrayList tablelist = new ArrayList();
				ArrayList tablelist1 = new ArrayList();
				int begin = 0;
				Image image=null;
				String macth="[0-9]+(.[0-9]+)?";
				BigDecimal zero = new BigDecimal("0");
				ArrayList recidxList=new ArrayList();//分组分页或者分组不分页记录分组指标的值 
				boolean lastRow=false;
				while (resultSet.next()) {
				HashMap recidxMap=new HashMap();//每条记录的分组指标1 分组指标2
	      			//记录每条记录的分组指标 区分上一条记录的分组指标是否等于当前的分组指标 不等则序号从1开始
						if("1".equals(isGroupPoint)) {
                            recidxMap.put("GroupV",resultSet.getString("GroupV")!=null?resultSet.getString("GroupV"):"");
                        }
						if(this.isGroupV2) {
                            recidxMap.put("", resultSet.getString("GroupV2")!=null?resultSet.getString("GroupV2"):"");
                        }
						recidxList.add(recidxMap);		
					nn++;
					int recidx = 0;
					if(infor_Flag!=null&& "1".equals(infor_Flag)) {
                        mainidvalue = resultSet.getString("A0100");
                    } else if(infor_Flag!=null&& "2".equals(infor_Flag)) {
                        mainidvalue = resultSet.getString("B0110");
                    } else if(infor_Flag!=null&& "3".equals(infor_Flag)) {
                        mainidvalue = resultSet.getString("E01A1");
                    }
					/* 分组显示 */
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
						String tempGroupN = " ";
						if (resultSet.getString("GroupN") != null) {
							tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
						}

						if (!groupN.equals(tempGroupN) || (i - 1) % rows == 0) {/**打满一页，将该页中的总计，合计，合并信息画出来*/
							if (i != 1) {
								/*-------   空行打印    --------*/
								if (i < rows) // 如果纪录没有打满一页
								{
									if (emptyRow != null&& "1".equals(emptyRow)) {
										while (i <= rows) {
											writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
													lth[0]+this.left_space,height-4,
													hmusterViewBo,writer, bottomnList, null,
													pageCount, null, zeroPrint,
													printGrid);
											i++;
											n++;
										}
									}
								}
								/* 页小计 */
								if (field != null && field.length > 0&&this.isyxj) {
									writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
											lth[0]+this.left_space,height-4,hmusterViewBo,
											writer,bottomnList,
											ResourceFactory.getProperty("hmuster.label.pageCount"),
											pageCount, field, zeroPrint,printGrid);
									for (int a = 0; a < field.length; a++) // 清空
									{
										pageCount[a] = 0;
									}
									n++;
								}
								/* 页累计 */
								if (fields != null && fields.length > 0&&this.isylj) {
									writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
											lth[0]+this.left_space,height-4,hmusterViewBo,
											writer,bottomnList,
											ResourceFactory.getProperty("hmuster.label.toatlCount"),totalCount, 
											fields, zeroPrint,printGrid);
									n++;
								}
								
								if (!groupN.equals(tempGroupN) ) {
									/* 分组合计 */
									if (this.fzhj != null && this.fzhj.length > 0&&this.isfzhj) {
										writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
												lth[0]+this.left_space,height-4,hmusterViewBo,writer,bottomnList,
												ResourceFactory.getProperty("gz.gz_acounting.total"),
												fzhjCount, this.fzhj, zeroPrint,printGrid);
										n++;
									}
								}

								if (!groupN.equals(tempGroupN)) {
									totalCount = initCount(fields);
									fzhjCount = initCount(this.fzhj);
								}
								this.setGroupNcode(groupN);
								/* 生成标题 */
								getTitle(tabid,isGroupTerm, page_height, writer,
										height - 4, n, titelList, user, String.valueOf(curPage), rows,
										infor_Flag, tableName, isGroupPoint,
										groupV, lth[1], lth[2], history, year,
										month, count, userView, dbpre);
								// 处理虚线
								if ("1".equals(printGrid)) {
                                    dealDashed2(dashedGridList, n - 1, lth,page_height, height - 4, writer);
                                }
								
								if(itemHeArr!=null&&itemHeArr.trim().length()>0&&i>0){
									int num=0;
									for (Iterator t = bottomnList.iterator(); t.hasNext();) {
										String[] temp = (String[]) t.next();	
										if(temp[27]!=null&& "1".equals(temp[27])) {
                                            continue;
                                        }
										String lrtb[] = {temp[21],temp[22],temp[23],temp[24]};
										if(heitemid[num]!=null&&temp[25]!=null&&temp[25].equalsIgnoreCase(heitemid[num])){
											String context = " ";
											if ("S".equals(temp[7])) {
												context = String.valueOf(nn);
											}else {
												if (!isGroupTerm&& existColumn.indexOf("," + temp[0] + ",") != -1) {
                                                    context = resultSet.getString("C" + temp[0]);
                                                } else if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                                                    context = resultSet.getString("GroupV");
                                                } else if (isGroupTerm) {
                                                    if(temp[25].equalsIgnoreCase(groupPoint)&& isGroupPoint != null&& "1".equals(isGroupPoint)){
                                                        context = resultSet.getString("GroupV");
                                                    }else if("E".equals(temp[7])&&this.isGroupV2)
                                                    {
                                                        context = resultSet.getString("GroupV2");
                                                    }
                                                    else if("E".equals(temp[7]))
                                                    {
                                                        context="";
                                                    }
                                                    else
                                                    {
                                                        context = resultSet.getString("C" + temp[0]);
                                                    }
                                                } else
												{
													if("E".equals(temp[7])&&this.isGroupV2)
													{
														context = resultSet.getString("GroupV2");
													}else if("E".equals(temp[7]))
													{
														context="";
													}
													else {
                                                        context = " ";
                                                    }
												}

												if (context == null) {
													context = " ";
													if ("1".equals(zeroPrint)&& "N".equals(temp[9])) {
                                                        context = "0";
                                                    }

												}
												if (context != null && !"".equals(context)&& !" ".equals(context)&& "N".equals(temp[9])) {
													float f = Float.parseFloat(context);
													if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
													{
														if (f == 0) {
                                                            context = " ";
                                                        } else {
                                                            context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                                        }

													} else {
                                                        context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                                    }

												}
											}
											if (!"P".equals(temp[7]) || isGroupTerm) {
												if("1".equals(temp[26])) {
                                                    context=" ";
                                                }

												//如果用户对该指标无权限，则不予显示数据
												if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
												{
													FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
													if(fielditem!=null){
														//临时变量不校验指标权限
														if(!temp[25].toLowerCase().startsWith("yk")&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25]))
															&&!"NBASE".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25]))
														{
															if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
															{
																
															}
															else
															{
														    	context=" ";
															}
														}
													}
												}
											}
											excecute(heitemvalue[num],Float.parseFloat(temp[2]), 
													page_height - (lth[1] + lth[2])-(n-m[num]-2)*(height - 4),
													Float.parseFloat(temp[4]), (m[num]+1)*(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, 1, lrtb);
											heitemvalue[num] = " ";
											herevalue[num] = " ";
											m[num]=0;
										}
										num++;
									}
								}
								document.newPage();
								begin = 0;
								i = 1;
								n = 1;
								groups=0;
								curPage++;

							}
							groupN = tempGroupN;
							if(resultSet.getString("GroupV")!=null) {
                                groupV=resultSet.getString("GroupV");
                            } else {
                                groupV=" ";
                            }
							// 写表头
							writeTableHeader(tableHeaderList, page_height,writer, printGrid, r_bottomn);
						}
					}else if (!isGroupTerm || isGroupPoint != null && "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)) {//分组不分页
						String tempGroupN = "";
						if (resultSet.getString("GroupN") != null) {
							tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
						}

						if (!groupN.equals(tempGroupN)&&i!=1) {
							/* 分组合计 */
							if (this.fzhj != null && this.fzhj.length > 0&&this.isfzhj) { //合计行
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,hmusterViewBo,writer,bottomnList,
										ResourceFactory.getProperty("gz.gz_acounting.total"),fzhjCount, 
										this.fzhj, zeroPrint,printGrid);
								n++;
								groups++;
								lastRow=true;
							}
							fzhjCount = initCount(this.fzhj);
						}
						groupN = tempGroupN;
						if(resultSet.getString("GroupV")!=null) {
                            groupV=resultSet.getString("GroupV");
                        } else {
                            groupV="";
                        }
						if ((i - 1) % rows == 0||i>rows) {
							if (i != 1) {
								/*-------   空行打印    --------*/
								if (i < 10) // 如果纪录没有打满一页
								{
									if (emptyRow != null&& "1".equals(emptyRow)) {
										while (i <= rows) {
											writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
													lth[0]+this.left_space,height-4,
													hmusterViewBo,writer, bottomnList, null,
													pageCount, null, zeroPrint,
													printGrid);
											i++;
											n++;
										}
									}
								}
								this.setGroupNcode(groupN);
								/* 生成标题 */
								getTitle(tabid,isGroupTerm, page_height, writer,
										height - 4, n, titelList, user, String.valueOf(curPage), rows,
										infor_Flag, tableName, isGroupPoint,
										groupV, lth[1], lth[2], history, year,
										month, count, userView, dbpre);
								// 处理虚线
								if ("1".equals(printGrid)) {
                                    dealDashed2(dashedGridList, n - 1, lth,
                                            page_height, height - 4, writer);
                                }
								document.newPage();
								begin = 0;
								i = 1;
								n = 1;
								groups=0;//切换页 分组合计为0；
								curPage++;
							}
							// 写表头
							writeTableHeader(tableHeaderList, page_height,
									writer, printGrid, r_bottomn);
						}
					} else {
						recidx = resultSet.getInt("recidx");
						if ((i - 1) % rows == 0) {
							if (i != 1) {
								if ("1".equals(printGrid)) {
                                    dealDashed2(dashedGridList, n - 1, lth,
                                            page_height, height - 4, writer);
                                }
								document.newPage();
								begin = 0;
								n = 1;

								curPage++;
							}
							// 写表头
							writeTableHeader(tableHeaderList, page_height,
									writer, printGrid, r_bottomn);

						}
					}
					if("stipend".equals(this.modelFlag))
					{
						int zeroCount=0;//我的薪酬花名册，判断值为空或者为零的总列数
						int columnCount=0;//我的薪酬花名册，画出的总列数
						for (Iterator t = bottomnList.iterator(); t.hasNext();) {
							String[] temp = (String[]) t.next();	
							if(temp[27]!=null&& "1".equals(temp[27])) {
                                continue;
                            }
							String context = " ";
							if ("S".equals(temp[7])) {
								context = String.valueOf(nn);
							}else if (!isGroupTerm && "P".equals(temp[7])) // 生成照片
							{
								
								
							} else if (isGroupTerm && "P".equals(temp[7])) {
                                context = " ";
                            } else {
								if (!isGroupTerm&& existColumn.indexOf("," + temp[0] + ",") != -1) {
                                    context = resultSet.getString("C" + temp[0]);
                                } else if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                                    context = resultSet.getString("GroupV");
                                } else if (isGroupTerm) {
                                    if(temp[25].equalsIgnoreCase(groupPoint)&& isGroupPoint != null&& "1".equals(isGroupPoint)){
                                        context = resultSet.getString("GroupV");
                                    }else if("E".equals(temp[7])&&this.isGroupV2)
                                    {
                                        context = resultSet.getString("GroupV2");
                                    }
                                    else if("E".equals(temp[7]))
                                    {
                                        context="";
                                    }
                                    else {
                                        context = resultSet.getString("C" + temp[0]);
                                    }
                                } else
								{
									if("E".equals(temp[7])&&this.isGroupV2)
									{
										context = resultSet.getString("GroupV2");
									}else if("E".equals(temp[7]))
									{
										context="";
									}else {
                                        context = " ";
                                    }
								}
								if (context == null) {
									context = " ";
									if ("1".equals(zeroPrint)&& "N".equals(temp[9])) {
                                        context = "0";
                                    }
								}
								if (context != null && !"".equals(context)&& !" ".equals(context)&& "N".equals(temp[9])) {
									float f = Float.parseFloat(context);
									if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
									{
										if (f == 0) {
                                            context = " ";
                                        } else {
                                            context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                        }

									} else {
                                        context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                    }
								}
							}
							if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
							{
								FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
								if(fielditem!=null){
									if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25]))
										&&!"NBASE".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25]))
									{
										if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
										{
											
										}
										else
										{
									    	context=" ";
										}
									}
								}
							}
							columnCount++;
							if ("S".equals(temp[7])|| "P".equals(temp[7])||(temp[26]!=null&&!"".equals(temp[26])&& "A01".equalsIgnoreCase(temp[26]))||(temp[25]!=null&&(temp[25].toUpperCase().endsWith("Z0")||temp[25].toUpperCase().endsWith("Z1")|| "A0101".equalsIgnoreCase(temp[25])|| "b0110".equalsIgnoreCase(temp[25])|| "e0122".equalsIgnoreCase(temp[25])|| "e01a1".equalsIgnoreCase(temp[25])))) {
				    			zeroCount++;
				    		}else{
	                           if("".equals(context.trim())||"&nbsp;".equalsIgnoreCase(context)){
	                        	   zeroCount++;
	                           }else if(context.matches(macth))
	                           {
	                        	   BigDecimal tempBD = new BigDecimal(context);
	                        	   if(zero.compareTo(tempBD)==0) {
                                       zeroCount++;
                                   }
	                           }
				    		}
						}
						if(zeroCount==columnCount)
						{
							nn--;
							continue;
						}
					}
					begin++;
					int num=0;
					for (Iterator t = bottomnList.iterator(); t.hasNext();) {
						String[] temp = (String[]) t.next();	
						if(temp[27]!=null&& "1".equals(temp[27])) {
                            continue;
                        }
						int flag = 0; // 是否为最低层的表头
						if (Float.parseFloat(temp[3]) + Float.parseFloat(temp[5]) == r_bottomn) {
                            flag = 1;
                        }
						String lrtb[] = {temp[21],temp[22],temp[23],temp[24]};
						if ("0".equals(printGrid)) {
							lrtb[0] = "0";
							lrtb[1] = "0";
							lrtb[2] = "0";
							if (flag == 1) {
                                lrtb[3] = "1";
                            } else {
                                lrtb[3] = "0";
                            }
						}
						String context = " ";
						if ("S".equals(temp[7])) {
							boolean recidxflag=false;
							if(recidxList.size()>1&& "1".equals(isGroupedSerials)){//按组显示序号标识 changxy
								HashMap maps=(HashMap)recidxList.get(recidxList.size()-2);
								if("1".equals(isGroupPoint)||this.isGroupV2){//分组指标1，2 如果上一行的分组指标与当前行的分组指标不同时 序号从1重新开始
									if(!maps.get("GroupV").equals(recidxMap.get("GroupV"))){
										recidxflag=true;
										
									}
								}
							}
							if(!recidxflag) {
                                context = String.valueOf(nn);
                            } else{
								nn=1;
								context = String.valueOf(nn);
							}
								
						}else if (!isGroupTerm && "P".equals(temp[7])) // 生成照片
						{
							
							ArrayList list=createPhotoFile2(getDbpre(resultSet, dbpre) + "A00",
									resultSet.getString("A0100"), "P");
							byte[] buf=(byte[]) list.get(0);
							boolean photo_flag=(Boolean) list.get(1);
							if(buf.length>0)
							{
								if(photo_flag) {
									java.awt.Image awtImage=Toolkit.getDefaultToolkit().createImage(buf);
									image= Image.getInstance(awtImage,null);		
								}else {
									image = Image.getInstance(buf);	
								}

								int a_width=0;
								if(temp[4].indexOf(".")!=-1) {
                                    a_width=(Integer.parseInt(temp[4].substring(0,temp[4].indexOf("."))))-5;
                                } else {
                                    a_width=(Integer.parseInt(temp[4]))-5;
                                }
								image.scaleAbsolute(Float.parseFloat(temp[4]), height-6);
							}
							
							excecute(buf,image,Float.parseFloat(temp[2]), 
									page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
									Float.parseFloat(temp[4]), height - 4, Integer.parseInt(temp[13]),
									temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid),  lrtb);
						} else if (isGroupTerm && "P".equals(temp[7])) {
                            context = " ";
                        } else {
							if (!isGroupTerm&& existColumn.indexOf("," + temp[0] + ",") != -1) {
								context = resultSet.getString("C" + temp[0]);
								if(StringUtils.isNotEmpty(context)&&context.indexOf("`")>-1) {
                                    context=context.replaceAll("`", "\n");
                                }
							}
							else if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                                context = resultSet.getString("GroupV");
                            } else if (isGroupTerm) {
                                if(temp[25].equalsIgnoreCase(groupPoint)&& isGroupPoint != null&& "1".equals(isGroupPoint)){
                                    context = resultSet.getString("GroupV");
                                }else if("E".equals(temp[7])&&this.isGroupV2)
                                {
                                    context = resultSet.getString("GroupV2");
                                }
                                else if("E".equals(temp[7]))
                                {
                                    context="";
                                }
                                else {
                                    context = resultSet.getString("C" + temp[0]);
                                }
                            } else
							{
								if("E".equals(temp[7])&&this.isGroupV2)
								{
									context = resultSet.getString("GroupV2");
								}else if("E".equals(temp[7]))
								{
									context="";
								}else {
                                    context = " ";
                                }
							}

							if ("N".equals(temp[9])|| "R".equals(temp[7])) {
								// 页小计
								count(pageCount,field,context,temp);						
								// 处理页累计
								count(totalCount,fields,context,temp);
								// 分组合计
								count(fzhjCount,this.fzhj,context,temp);	
								// 总计
								count(zjCount,this.zj,context,temp);
							}

							// /

							if (context == null|| "".equals(context)) {//为空的值会存在“”和“ ”，二者归为一类 否则合并会有问题
								context = " ";
								if ("1".equals(zeroPrint)&& "N".equals(temp[9])) {
                                    context = "0";
                                }

							}
							if (context != null && !"".equals(context)&& !" ".equals(context)&& "N".equals(temp[9])) {
								float f = Float.parseFloat(context);
								if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
								{
									if (f == 0) {
                                        context = " ";
                                    } else {
                                        context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                    }

								} else {
                                    context = hmusterViewBo.round(context,Integer.parseInt(temp[6]));
                                }

							}
						}
						if (!"P".equals(temp[7]) || isGroupTerm) {
							if("1".equals(temp[26])) {
                                context=" ";
                            }
							
							//如果用户对该指标无权限，则不予显示数据
							if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
							{
								FieldItem fielditem = DataDictionary.getFieldItem(temp[25]);
								if(fielditem!=null){
									if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25]))
										&&!"NBASE".equalsIgnoreCase(temp[25])&&!"a0100".equalsIgnoreCase(temp[25]))
									{
										
										if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
										{
											
										}
										else
										{
									    	context=" ";
										}
									}
								}
							}
							
							int aFontSize = Integer.parseInt(fontStyle[2]);
							aFontSize=getFitFontSize(aFontSize,columnDefinitionSize[num],height,context);
							
							num++;
							if(heitemid[num-1]!=null&&heitemid[num-1].trim().length()>0&&temp[25]!=null&&temp[25].equalsIgnoreCase(heitemid[num-1])){

								boolean ismain = false;
								if(isHeMain[num-1]!=null&& "true".equalsIgnoreCase(isHeMain[num-1])){
									if(n>1&&mainArr[num-1]!=null&&!mainArr[num-1].equalsIgnoreCase(mainidvalue)){
										ismain = true;
									}
									mainArr[num-1] = mainidvalue;
								}
								if(num==1){
									tablelist = new ArrayList();
									tablelist1 = new ArrayList();
								}
								if(heitemvalue[num-1].equalsIgnoreCase(context)){
									if(n>1) {
                                        m[num-1]++;
                                    }
									if((i>0&&(i%rows ==0||resultSet.isLast()))||((isGroupPoint == null||!"1".equals(isGroupPoint))&&countAll==recidx)||ismain){
										if(ismain){
											excecute(context,Float.parseFloat(temp[2]), 
													page_height - (lth[1] + lth[2])-(n-m[num-1]-1)*(height - 4),
													Float.parseFloat(temp[4]), m[num-1]*(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid),  lrtb);
										}else{
											//每页合并行到最后一行合并处理 添加是否有合计行  n==(m[num-1]+1+groups
											if( n==(m[num-1]+1+groups)){
												excecute(context,Float.parseFloat(temp[2]), 
														page_height - (lth[1] + lth[2])-(n-m[num-1]-1-groups)*(height - 4),
														Float.parseFloat(temp[4]), (m[num-1]+1+groups)*(height - 4), Integer.parseInt(temp[13]),
														temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid),  lrtb);

											}else{
												//lastRow 当前行上方有合计行存在时处理方案
												if(lastRow){
													excecute(context,Float.parseFloat(temp[2]),//当最后一行的值与上一行的值相同时计算高度 
															page_height - (lth[1] + lth[2])-(n-m[num-1]-1-1)*(height - 4),
															Float.parseFloat(temp[4]), (m[num-1]+1+1)*(height - 4), Integer.parseInt(temp[13]),
															temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid),  lrtb);
													
												}else{
													excecute(context,Float.parseFloat(temp[2]),//当最后一行的值与上一行的值相同时计算高度 
															page_height - (lth[1] + lth[2])-(n-m[num-1]-1)*(height - 4),
															Float.parseFloat(temp[4]), (m[num-1]+1)*(height - 4), Integer.parseInt(temp[13]),
															temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid),  lrtb);
													
												}

											}
																							
											
										}
										if((i%rows == 0||countAll==recidx||resultSet.isLast())&&ismain){
											excecute(context,Float.parseFloat(temp[2]), 
													page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
													Float.parseFloat(temp[4]),(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid), lrtb);
										}
										m[num-1]=0;
									}
									if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
										PdfPTable table = null;
										ArrayList listStr = new ArrayList();
										
										if(ismain){
											table = excecute(context,Float.parseFloat(temp[4]),
													m[num-1]*(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid));
											listStr.add(temp[2]);
											listStr.add( new Float(page_height - (lth[1] + lth[2])-(n-m[num-1]-1)*(height - 4)));
										}else{
											table = excecute(context,Float.parseFloat(temp[4]), 
													(m[num-1]+1)*(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid));
											listStr.add(temp[2]);
											listStr.add(new Float(page_height - (lth[1] + lth[2])-(n-m[num-1])*(height - 4)));
										}
										tablelist1.add(listStr);
										tablelist.add(table);
									}
									heitemvalue[num-1] = context;
									if(!ismain) {
                                        herevalue[num-1] = context;
                                    }
									continue;
								}else{
									if((i>0&&(i%rows == 0||resultSet.isLast()))){
										excecute(context,Float.parseFloat(temp[2]), 
												page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
												Float.parseFloat(temp[4]), (height - 4), Integer.parseInt(temp[13]),
												temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid), lrtb);
										if(isGroupNoPage.equals(0))//分组不分页时最后一行的前n行是合计行时 合并会有问题
                                        {
                                            m[num-1]=0;

                                            int a = 10000_4550;

                                        }
									}
									if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
											PdfPTable table = excecute(context,Float.parseFloat(temp[4]), 
													(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, 1);
											ArrayList listStr = new ArrayList();
											listStr.add(temp[2]);
											listStr.add(new Float(page_height - (lth[1] + lth[2])-(n-m[num-1]-1)*(height - 4)));
											tablelist1.add(listStr);
											tablelist.add(table);
									}
									herevalue[num-1] = context;
								}
								if(!heitemvalue[num-1].equalsIgnoreCase(herevalue[num-1])){
									//当前行数-1-合并行-合计行=当前行的上一行对应指标合并top
									int topheight= (n-1-m[num-1]-groups)*(height - 4);
									if(lastRow&&groups>=2){
										groups--; //当前行上方有合计行时 并且合计行上方的指标有合并格存在 //4476
										topheight=(n-1-m[num-1]-groups)*(height - 4);
									}
									//区分如果合并行从第一行开始时 
												//m[num-1]+1 计算合并行数从0开始
									if((n-1)==(m[num-1]+1+groups)) {
                                        topheight= (n-1-m[num-1]-1-groups)*(height - 4);
                                    }
									if(lastRow&&topheight>0&&groups<=1)//
                                    {
                                        topheight-=(height - 4);
                                    }
									if(groups==0&&topheight>0)//
                                    {
                                        topheight-=(height - 4);
                                    }
									/*if(topheight>0)
										topheight-=(height - 4);*/
									
									if(i%rows==1){
										heitemvalue[num-1] = context;
										continue;
									}//分组部分页时 行合并指标计算位置
									excecute(heitemvalue[num-1],Float.parseFloat(temp[2]), 
											page_height - (lth[1] + lth[2])-topheight,
											Float.parseFloat(temp[4]), (m[num-1]+1)*(height - 4), Integer.parseInt(temp[13]),
											temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid), lrtb);
									heitemvalue[num-1] = context;
									if(isGroupPoint == null||!"1".equals(isGroupPoint)){
										if(countAll==recidx||resultSet.isLast()){
											excecute(heitemvalue[num-1],Float.parseFloat(temp[2]), 
													page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
													Float.parseFloat(temp[4]),(height - 4), Integer.parseInt(temp[13]),
													temp[11], Integer.parseInt(temp[12]),temp[10], writer, Integer.parseInt(printGrid), lrtb);
										}
									}
									m[num-1]=0;
								}else{
									continue;
								}
							}else{
								excecute(context,Float.parseFloat(temp[2]), 
										page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										Float.parseFloat(temp[4]), height - 4, Integer.parseInt(temp[13]),
										temp[11], aFontSize,temp[10], writer, Integer.parseInt(printGrid), lrtb);
							}
						}
					}
					lastRow=false;
					if ((isGroupPoint == null || "0".equals(isGroupPoint))&&isGroupTerm) {
						if ((i != 1||rows==1) && i % rows == 0) {
							
							/* 页小计 */
							if (this.isyxj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n)*(height - 4),
										lth[0]+this.left_space,height-4,
										hmusterViewBo,writer,
										bottomnList, "hmuster.label.pageCount",
										pageCount, field, zeroPrint, printGrid);
								// 清空
								for (int a = 0; a < field.length; a++) {
									pageCount[a] = 0;
								}
								n++;
							}

							if (this.isylj) // 显示页累计
							{
								writePageCount(page_height - (lth[1] + lth[2])-(n)*(height - 4),
										lth[0]+this.left_space,height-4,
										hmusterViewBo,writer,
										bottomnList,
										"hmuster.label.toatlCount", totalCount,
										fields, zeroPrint, printGrid);
								n++;
							}
							this.setGroupNcode(groupN);
							/* 生成标题 */
							getTitle(tabid,isGroupTerm, page_height, writer,
									height - 4, n, titelList, user, String
											.valueOf(curPage), rows,
									infor_Flag, tableName, isGroupPoint,
									groupV, lth[1], lth[2], history, year,
									month, count, userView, dbpre);
						}
					}
					i++;
					n++;
				}
				/** 无记录也需要输出表头 */
				if(!resultSet.last()){
                    curPage=0;
					// 写表头
					writeTableHeader(tableHeaderList, page_height,
							writer, printGrid, r_bottomn);
				}
				
				if(!isGroupTerm || isGroupPoint != null&& "1".equals(isGroupPoint)){
						if("0".equals(isGroupNoPage)){
							for(int j=0;j<tablelist.size();j++){
								PdfPTable table = (PdfPTable)tablelist.get(j);
								ArrayList liststr1 = (ArrayList)tablelist1.get(j);
								if(table!=null&&liststr1!=null&&liststr1.size()==2){
									float leftP = Float.parseFloat(liststr1.get(0).toString());
									float top = Float.parseFloat(liststr1.get(1).toString());
									table.writeSelectedRows(0, 1, leftP+this.left_space, top-this.top_space, writer.getDirectContent()); // 固定坐标
								}
							}
							if (n < rows) // 如果纪录没有打满一页
							{
								if (emptyRow != null&& "1".equals(emptyRow)) {
									while (n+1<= rows) {
										writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
												lth[0]+this.left_space,height-4,
												hmusterViewBo,writer, bottomnList, null,
												pageCount, null, zeroPrint,
												printGrid);
										i++;
										n++;
									}
								}
							}
							/* 分组合计 */
							if (this.isfzhj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,hmusterViewBo,writer,bottomnList,
										ResourceFactory.getProperty("gz.gz_acounting.total"),
										fzhjCount, this.fzhj, zeroPrint,printGrid);
								n++;
								i++;
							}
							/* 页小计 */
							if (this.isyxj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,
										hmusterViewBo, writer,
										bottomnList, "hmuster.label.pageCount",
										pageCount, field, zeroPrint, printGrid);
								// 清空
								for (int a = 0; a < field.length; a++) {
									pageCount[a] = 0;
								}
								n++;
							}
							
							if (this.isylj) // 显示页累计
							{
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,
										hmusterViewBo,writer,
										bottomnList,
										"hmuster.label.toatlCount", totalCount,
										fields, zeroPrint, printGrid);
								n++;
							}
							/* 总计 */
							if (this.iszj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,hmusterViewBo,writer,bottomnList,
										ResourceFactory.getProperty("workdiary.message.total"),
										zjCount, this.zj, zeroPrint,printGrid);
								n++;
							}
						}else{
							/* 分组合计 */
							if (this.isfzhj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,hmusterViewBo,writer,bottomnList,
										ResourceFactory.getProperty("gz.gz_acounting.total"),fzhjCount, 
										this.fzhj, zeroPrint,printGrid);
								n++;
								i++;
							}
							/* 总计 */
							if (this.iszj) {
								writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
										lth[0]+this.left_space,height-4,hmusterViewBo, writer,bottomnList,
										ResourceFactory.getProperty("workdiary.message.total"),zjCount,
										this.zj, zeroPrint,printGrid);
								n++;
							}
							if (n < rows) // 如果纪录没有打满一页
							{
								if (emptyRow != null&& "1".equals(emptyRow)) {
									while (n<= rows) {
										writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
												lth[0]+this.left_space,height-4,
												hmusterViewBo,writer, bottomnList, null,
												pageCount, null, zeroPrint,
												printGrid);
										i++;
										n++;
									}
								}
							}
						}
					}else{
						if (n < rows) // 如果纪录没有打满一页
						{
								if (emptyRow != null&& "1".equals(emptyRow)) {
									while (n<= rows) {
										writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
												lth[0]+this.left_space,height-4,
												hmusterViewBo,writer, bottomnList, null,
												pageCount, null, zeroPrint,
												printGrid);
										i++;
										n++;
									}
								}
						}
						/* 页小计 */
						if (this.isyxj) {
							writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
									lth[0]+this.left_space,height-4,
									hmusterViewBo, writer,
									bottomnList, "hmuster.label.pageCount",
									pageCount, field, zeroPrint, printGrid);
							// 清空
							for (int a = 0; a < field.length; a++) {
								pageCount[a] = 0;
							}
							n++;
						}
						
						if (this.isylj) // 显示页累计
						{
							writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
									lth[0]+this.left_space,height-4,
									hmusterViewBo, writer,
									bottomnList,
									"hmuster.label.toatlCount", totalCount,
									fields, zeroPrint, printGrid);
							n++;
						}
						/* 总计 */
						if (this.iszj) {
		                    //if (!infor_Flag.equals("stipend")&&!isGroupTerm &&!isGroupTerm2 &&( isGroupPoint== null|| !isGroupPoint.equals("1")))
		                    //    zjCount=totalCounts(zjCount,this.zj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
		                    //else 
		                    if (isGroupTerm||isGroupTerm2||("stipend".equalsIgnoreCase(infor_Flag)/*&&this.groupCount.equals("1")*/)) {
                                zjCount=hmusterViewBo.totalCountsZj(zjCount,zj,tableName,bottomnList);  // 重新计算总计
                            }
							writePageCount(page_height - (lth[1] + lth[2])-(n-1)*(height - 4),
									lth[0]+this.left_space,height-4 
									,hmusterViewBo, writer,bottomnList,
									ResourceFactory.getProperty("workdiary.message.total"),zjCount, this.zj, zeroPrint,printGrid);
							n++;
						}
						
				}
				this.setGroupNcode(groupN);
				/* 生成标题 */
				getTitle(tabid,isGroupTerm, page_height, writer, height - 4, n-1,
						titelList, user, String.valueOf(curPage), rows,
						infor_Flag, tableName, isGroupPoint, groupV, lth[1],
						lth[2], history, year, month, count, userView, dbpre);
				// 处理虚线
				if ("1".equals(printGrid)) {
                    dealDashed2(dashedGridList, n - 1, lth, page_height,
                            height, writer);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeResource(resultSet);
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(stmt);
			PubFunc.closeResource(a_conn);
			if(document!=null&&document.isOpen()){
				document.close();
			}
		}
		
	}
	

	public void count(double[] count,String[] field,String context,String[] temp)
	{
		//获取指标配置信息
		if (field != null && field.length > 0) {
			String extendattr=temp[temp.length-1].toUpperCase();
			String groupSum="0";  // 0求和/1平均/2最大/3最小
			if(extendattr!=null&&extendattr.indexOf("GROUPSUM")!=-1){  //合计行指标区分计算是求和还是求平均最大或最小
				  boolean RoundBeforeAggregate = true;
		            if(extendattr.indexOf("ROUNDBEFOREAGGREGATE")!=-1) {
                        RoundBeforeAggregate = "1".equals(extendattr.substring(extendattr.indexOf("<ROUNDBEFOREAGGREGATE>")+
                                "<ROUNDBEFOREAGGREGATE>".length(),extendattr.indexOf("</ROUNDBEFOREAGGREGATE>")));
                    }
		            groupSum= extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
			}
			for (int b = 0; b < field.length; b++) {

				if (field[b].equals("C" + temp[0])) {

					//if (context != null&&!context.equals("&nbsp;")&&context.trim().length()!=0){
					context=(context==null|| "&nbsp;".equals(context)||context.trim().length()==0)?"0.0":context;
					    try{
					    	if("0".equals(groupSum)){//求和
					    		 count[b] += Double.parseDouble(context);
					    	}else if("2".equals(groupSum)){// 最大
					    		 if(count[b]<Double.parseDouble(context)) {
                                     count[b]= Double.parseDouble(context);
                                 }
					    	}else if("3".equals(groupSum)){//最小值（数组对应的值默认为0 但是context有为0的情况，无法判断count[b]为0是默认值还是context值）和求平均值（无法计算一共有多少条记录）无法计算 现改为不为0的最小值
					    		if(count[b]!=0){
					    			if(Double.parseDouble(context)<count[b]) {
                                        count[b]=Double.parseDouble(context);
                                    }
					    		}else{
					    			count[b]=Double.parseDouble(context);
					    		}
					    		
					    		 //count[b] += Double.parseDouble(context);
					    		 
					    	}else{
					    		count[b] += Double.parseDouble(context);
					    	}
					       
					    }catch(Exception e){
					    	e.printStackTrace();
					    }
					//}else{
					//	count[b]=0;//计算数据为空时为0；
					//}
					break;
				}
			}
		}
	}
	
	public int  getFitFontSize(int fontSize,float width,float height,String context)
	{
		width-=5;
		height-=5;
		float size=Integer.parseInt(String.valueOf(height/(fontSize+2)).substring(0,String.valueOf(height/(fontSize+2)).indexOf("."))) *Integer.parseInt(String.valueOf(width/(fontSize+2)).substring(0,String.valueOf(width/(fontSize+2)).indexOf(".")));
		while(fontSize>0&&(context.getBytes().length/2)>size)
		{
			fontSize=fontSize-1;
			size=Integer.parseInt(String.valueOf(height/(fontSize+2)).substring(0,String.valueOf(height/(fontSize+2)).indexOf("."))) *Integer.parseInt(String.valueOf(width/(fontSize+2)).substring(0,String.valueOf(width/(fontSize+2)).indexOf(".")));
		}
		return fontSize;
	}
	
	

	/**
	 * 写表头
	 */
	public void writeTableHeader(ArrayList tableHeaderList, float page_height,
			PdfWriter writer, String printGrid, float r_bottomn) {
		for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			String  gridno=temp[0];
			if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().get(gridno)!=null&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                continue;
            }
			String context = temp[1];
			if (context == null) {
                context = " ";
            }
			if (context != null && context.indexOf("`") != -1) {
                context = context.replaceAll("`", "\\\n");
            }
			if("1".equals(temp[26])) {
                context=" ";
            }
			int fontSize = Integer.parseInt(temp[12]);
			int fontAlign = Integer.parseInt(temp[13]);
			String fontEffect = temp[11];
			float leftP = Float.parseFloat(temp[2]);
			float top = page_height - Float.parseFloat(temp[3]);
			float width = Float.parseFloat(temp[4]);
			float aheight = Float.parseFloat(temp[5]);
			String[] ltrb = {temp[21],temp[22],temp[23],temp[24]};
			excecute(context, leftP, top, width, aheight, fontAlign,
					fontEffect, fontSize,temp[10], writer, 2, ltrb);
		}
		// c.GridNo,c.Hz,c.Rleft,c.RTop,c.RWidth,c.RHeight,c.Slope
		for (Iterator t = tableHeaderList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			int flag = 0; // 是否为最低层的表头
			if (Float.parseFloat(temp[3]) + Float.parseFloat(temp[5]) == r_bottomn) {
                flag = 1;
            }
			// 处理虚线 c.L,c.T,c.R,c.B
			String[] ltrb = new String[4];
			ltrb[0] = temp[21];
			ltrb[1] = temp[22];
			ltrb[2] = temp[23];
			ltrb[3] = temp[24];
//			ltrb[0] = "1";
//			ltrb[1] = "1";
//			ltrb[2] = "1";
//			ltrb[3] = "1";

			if ("0".equals(printGrid)) {
				ltrb[0] = "0";
				ltrb[1] = "0";
				ltrb[2] = "0";
				if (flag == 1) {
                    ltrb[3] = "1";
                } else {
                    ltrb[3] = "0";
                }
			}

			dealDashed(ltrb[0], ltrb[2], ltrb[1], ltrb[3], Float
					.parseFloat(temp[2]), Float.parseFloat(temp[3]), Float
					.parseFloat(temp[4]), Float.parseFloat(temp[5]),
					page_height, writer);
		}
	}

	// 得到底层左或右边线有虚线表格列表
	public ArrayList getDashedGridList(ArrayList bottomnList) {
		ArrayList list = new ArrayList();
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			// c.L,c.T,c.R,c.B
			if ("0".equals(temp[21]) || "0".equals(temp[23])) {
                list.add(temp);
            }
		}
		return list;
	}

	// 不打印表格线
	public void dealDashed3(ArrayList bottomnList, int n, float[] lth,
			float page_height, float height, PdfWriter writer) {
		for (int i = 0; i < bottomnList.size(); i++) {
			String[] temp = (String[]) bottomnList.get(i);
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			for (int j = 0; j < n; j++) {
				dealDashed("0", "0", "0", "0", Float.parseFloat(temp[2]),
						lth[1] + lth[2] + (j * height), Float
								.parseFloat(temp[4]), height, page_height,
						writer);
			}
		}
	}

	// 处理虚线2
	public void dealDashed2(ArrayList dashedGridList, int n, float[] lth,
			float page_height, float height, PdfWriter writer) {
		for (int i = 0; i < dashedGridList.size(); i++) {
			String[] temp = (String[]) dashedGridList.get(i);
			for (int j = 0; j < n; j++) {
				dealDashed("1", "1", "1", "1", Float
						.parseFloat(temp[2]), lth[1] + lth[2] + (j * height),
						Float.parseFloat(temp[4]), height, page_height, writer);
			}
		}
	}

	// 处理虚线1
	public void dealDashed(String l, String r, String t, String b, float rleft,
			float rtop, float rwidth, float height, float page_height,
			PdfWriter writer) {
		rleft+=this.left_space;
		rtop+=this.top_space;
		PdfContentByte cb = writer.getDirectContent();
		if ("0".equals(l)) {
			cb.moveTo(rleft, page_height - rtop);
			cb.lineTo(rleft, page_height - (rtop + height));
			cb.setColorStroke(new Color(255, 255, 255));
			cb.stroke();
		}
		if ("0".equals(r)) {
			cb.moveTo(rleft + rwidth, page_height - rtop);
			cb.lineTo(rleft + rwidth, page_height - (rtop + height));
			cb.setColorStroke(new Color(255, 255, 255));
			cb.stroke();
		}
		if ("0".equals(t)) {
			cb.moveTo(rleft, page_height - rtop);
			cb.lineTo(rleft + rwidth, page_height - rtop);
			cb.setColorStroke(new Color(255, 255, 255));
			cb.stroke();
		}
		if ("0".equals(b)) {
			cb.moveTo(rleft, page_height - (rtop + height));
			cb.lineTo(rleft + rwidth, page_height - (rtop + height));
			cb.setColorStroke(new Color(255, 255, 255));
			cb.stroke();
		}
	}

	/**
	 * 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 && 是否包含照片 && list2
	 * 
	 * @param bottomnList
	 * @param infor_Flag
	 * @param hmusterBo
	 * @param isGroupPoint
	 * @param tableName
	 * @return
	 */
	public ArrayList getSQL(String dbpre, ArrayList bottomnList,
			String infor_Flag, HmusterBo hmusterBo, String isGroupPoint,
			String tableName, UserView userView) {
		ArrayList list = new ArrayList();
		infor_Flag=infor_Flag!=null&&infor_Flag.trim().length()>0?infor_Flag:"";
		try {
			StringBuffer sql = new StringBuffer("select ");
			StringBuffer existColumn = new StringBuffer("");
			StringBuffer sql2 = new StringBuffer("select "); // 求得每个字符列内容的最大长度的sql语句
			ArrayList list2 = new ArrayList(); // 与上面相对应得列
			StringBuffer tempSql = new StringBuffer("");
			StringBuffer tempSql2 = new StringBuffer("");
			String isPhoto ="";
			boolean isGroupTerm = false; // 是表内条件否有分组指标

			for (Iterator t = bottomnList.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if(temp[27]!=null&& "1".equals(temp[27])) {
                    continue;
                }
				if (temp[7] != null && ("G".equals(temp[7])|| "R".equals(temp[7]))) {
					isGroupTerm = true;
				}
				if (temp[7] != null && ("E".equals(temp[7]))) {
					isGroupTerm2 = true;
				}
				/**文本型先显示*/
				if (!"S".equals(temp[7]) && !"H".equals(temp[7])&&!"G".equals(temp[7])&& !"P".equals(temp[7])&&!"E".equals(temp[7]) && !"R".equals(temp[7])&& temp[7] != null && !"".equals(temp[7])) {
					tempSql.append(",C");
					tempSql.append(temp[0]);
					existColumn.append(",");
					existColumn.append(temp[0]);
				}
				if ("P".equals(temp[7])) {
                    isPhoto = temp[4];
                }
				if (("A".equals(temp[7]) || "B".equals(temp[7]) || "K".equals(temp[7]))&& ("A".equals(temp[9]) || "M".equals(temp[9]))) {

					
						if ("M".equals(temp[9])||("A0101".equals(temp[25])&&!"1".equals(infor_Flag))) {
                            tempSql2.append(","+Sql_switcher.isnull("max("+ Sql_switcher.datalength("C" + temp[0])+"/3)","0")+" C" + temp[0]);
                        } else {
                            tempSql2.append(",max("+ Sql_switcher.length("C" + temp[0])+ ") C" + temp[0]);
                        }
					
					list2.add(temp);
				}
			}
			existColumn.append(",");
			tempSql.append(",GroupV,GroupN ");
			if(this.isGroupV2)
			{
		       if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupTerm&&isGroupPoint != null && "1".equals(isGroupPoint))
		       {
		            tempSql.append(",GroupV2,GroupN2");
		       }
		       else if(isGroupTerm) {
                   tempSql.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
               } else {
                   tempSql.append(",GroupV2, GroupN2");
               }
			}
			if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
                tempSql.append(",A0100");
            } else if ("2".equals(infor_Flag)) {
                tempSql.append(",B0110");
            } else if ("3".equals(infor_Flag)) {
                tempSql.append(",E01A1");
            } else {
                tempSql.append(",A0100");
            }
			String orderby="";
			if (isGroupPoint != null && "1".equals(isGroupPoint)) {
				HmusterXML hmxml = new HmusterXML(this.conn,tabID);
				String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
				GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
				String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
				GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
				if(GROUPFIELD.trim().length()>4){
					FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
					if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
						tempSql.append(",(select A0000 from organization where codeitemid=");
						tempSql.append(tableName);
						tempSql.append(".GroupN) AS A0000 ");
						if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
							    orderby=" group by "+Sql_switcher.month("a00z0");	
							}
							else
							{
				     			if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						    	{
						    		if(GROUPFIELD2.trim().length()>4)
						    		{
						    			FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
							    		if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
								    		tempSql.append(",(select A0000 from organization where codeitemid=");
								     		tempSql.append(tableName);
								     		tempSql.append(".GroupN2) AS A00002 ");
									     	orderby=" order by A0000,A00002,recidx";
								    	}else
							    		{
							    			orderby=" order by A0000,GroupN2,recidx";
							     		}
						    		}
						     		else
							    	{
						    			orderby=" order by A0000,GroupN2,recidx";
							     	}
						    	}
					    		else
					    		{
						    		orderby=" order by A0000,recidx";
					    		}
							}
						} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
								}
								else
								{
							    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by A0000 ";
								}
							}
							else{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by A0000 ";
								}
								else
								{
						        	orderby=" group by GroupN,GroupV order by A0000 ";
								}
							}
						}
						
					}else{
						if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0");	
							}
							else
							{
			     				if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
		    					{
		     						if(GROUPFIELD2.trim().length()>4)
			     					{
		    							FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
				     					if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
		      	     						tempSql.append(",(select A0000 from organization where codeitemid=");
				     						tempSql.append(tableName);
					    					tempSql.append(".GroupN2) AS A00002 ");
					    					orderby=" order by GroupN,A00002,recidx";
					    				}else
					    				{
					    					orderby=" order by GroupN,GroupN2,recidx";
					    				}
					     			}
					     			else
					     			{
					    				orderby=" order by GroupN,GroupN2,recidx";
					    			}
					    		}
					    		else
					    		{
					    			orderby=" order by GroupN,recidx";
					    		}
							}
						} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
								}else
								{
							    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
								}
							}
							else{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
								}
								else
								{
						        	orderby=" group by GroupN,GroupV order by GroupN ";
								}
							}
						}
					}
				}else{
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else
						{
			    			if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
			    			{
			    				if(GROUPFIELD2.trim().length()>4)
			    				{
					    			FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
				    				if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
					    				tempSql.append(",(select A0000 from organization where codeitemid=");
					    				tempSql.append(tableName);
						    			tempSql.append(".GroupN2) AS A00002 ");
						    			orderby=" order by A0000,A00002,recidx";
					    			}else
					    			{
					    				orderby=" order by A0000,GroupN2,recidx";
				    				}
				    			}
				    			else
				    			{
				     				orderby=" order by A0000,GroupN2,recidx";
				     			}
				    		}
				    		else
				    		{
					    		orderby=" order by A0000,recidx";
				     		}
						}
					} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}
						}
						else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
							}else
							{
					        	orderby=" group by GroupN,GroupV order by GroupN ";
							}
						}
					}
				}
			}else{
				if (!isGroupTerm) {
                    orderby=" order by recidx";
                }
				if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
				{
					orderby=" group by "+Sql_switcher.month("a00z0");
				}
			}
			tempSql.append(",recidx from ");
			tempSql.append(tableName);
			if (tempSql2.length() > 3) {
				tempSql2.append(" from ");
				tempSql2.append(tableName);
			}

			if (isGroupTerm||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
				tempSql = new StringBuffer("");
				tempSql.append(getGroupSql(bottomnList, infor_Flag, tableName,isGroupPoint));
			}
			/* 权限控制 */
			tempSql.append(this.privConditionStr);
			if("5".equals(this.modelFlag))
			{
				if(privConditionStr.trim().length()>0)
				{
				    if(this.getSql().trim().length()>0) {
                        tempSql.append(" and ("+this.getSql()+")");
                    }
				}
				else
				{
					if(this.getSql().trim().length()>0) {
                        tempSql.append(" where ("+this.getSql()+")");
                    }
				}
			}
			if("stipend".equals(infor_Flag)&&a0100!=null&&a0100.trim().length()>0)
			{
				if(tempSql.toString().substring(tempSql.toString().toUpperCase().lastIndexOf("FROM")).toLowerCase().indexOf("where")>0){
					tempSql.append(" and  a0100='"+this.a0100+"' ");
				}
				else {
                    tempSql.append(" where A0100='"+a0100+"'");
                }
			}
			tempSql.append(orderby);
			//sql.append(tempSql.substring(1));
	        // 用HmusterViewBo类得到sql
			HmusterViewBo hmusterViewBo=new HmusterViewBo(conn,tabID);
			hmusterViewBo.setModelFlag(modelFlag);
			hmusterViewBo.setUserView(userView);
			hmusterViewBo.setIsGroupPoint(isGroupPoint);
			hmusterViewBo.setGroupPoint(groupPoint);
			hmusterViewBo.setDataarea(dataarea);
			hmusterViewBo.setIsGroupPoint2(isGroupPoint2);
			hmusterViewBo.setGroupPoint2(groupPoint2);
			hmusterViewBo.setColumn(column);
			hmusterViewBo.setInfor_Flag(infor_Flag);
			//hmusterViewBo.setPrintGrind(printGrid);
			hmusterViewBo.setTextFormatMap(hmusterViewBo.getTextFormat(tabID));
			hmusterViewBo.setGroupV2(hmusterViewBo.isHaveGroup2(tableName));
			hmusterViewBo.setGroupCount(groupCount);
			if(hmusterViewBo.getTextFormatMap().size()>0)
			{
				if("1".equals(column)&& "1".equals(dataarea)) {
                    hmusterViewBo.setTextDataHeight(hmusterViewBo.getTextData(tabID));
                }
			}		
			sql.delete(0, sql.length());
			try{
				sql.append(hmusterViewBo.getMusterSqlAll(tableName));
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if (tempSql2.length() > 3) {
				sql2.append(tempSql2.substring(1));
			}
			/* end */
			
			if("stipend".equals(infor_Flag)&&a0100!=null&&a0100.trim().length()>0)
			{
				if (tempSql2.length() > 3) {
					sql2.append(" where A0100='"+a0100+"'");
				}
			}
//			if((infor_Flag.equals("salary")||infor_Flag.equals("analysis"))
//					&&(sql.indexOf("order")==-1||(!isGroupTerm && isGroupPoint != null
//					&&isGroupPoint.equals("1"))) )
//			if((infor_Flag.equals("stipend")||infor_Flag.equals("salary")||infor_Flag.equals("analysis"))
//					&&(sql.indexOf("order")==-1||(!isGroupTerm && isGroupPoint != null)) )
//			{
//				String atempSql=getInserSql(sql.toString(),tableName);
//				sql.setLength(0);
//				sql.append(atempSql);
//			}
			
			list.add(sql.toString());
			list.add(sql2.toString());
			list.add(isPhoto);
			list.add(list2);
			list.add(existColumn.toString());
			list.add(Boolean.valueOf(isGroupTerm));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}
	
	/**
	 * 根据薪资花名册排序要求 修改sql
	 * @param sql
	 * @return
	 */
	public String getInserSql(String sql,String tableName)
	{
		String temp=sql.substring(0,sql.indexOf(tableName)+tableName.length());
		temp+=",dbname";
		sql=temp+sql.substring(sql.indexOf(tableName)+tableName.length());
		String order="";
		
		if(sql.indexOf("order by")!=-1)
		{
			order=sql.substring(sql.indexOf("order by"));
			sql=sql.substring(0,sql.indexOf("order by"));
		}
		
		if(sql.indexOf("where")!=-1)
		{
			temp=sql.substring(0,sql.indexOf("where")+5);
			temp+=" upper("+tableName+".nbase)=upper(dbname.pre) ";
			
			sql=temp+" and "+sql.substring(sql.indexOf("where")+5);
		}
		else {
            sql+=" where upper("+tableName+".nbase)=upper(dbname.pre) ";
        }
		
		if(order.length()>0) {
            sql+=" "+order+",dbname.dbid,a0000,A00Z0,A00Z1";
        } else {
            sql+=" order by dbname.dbid,a0000, A00Z0, A00Z1";
        }
		return sql;
	}
	
	
	

	// 取得包含分组指标的sql语句
	public String getGroupSql(ArrayList bottomnList, String infor_Flag,
			String tableName, String isGroupPoint) {
		StringBuffer tempSql = new StringBuffer("");

		/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
		// GridNo,Hz,Rleft,RTop,RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,
		String a_temp = "";
		if ("1".equals(infor_Flag)|| "81".equals(this.modelFlag)|| "5".equals(this.modelFlag)
				|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
		    if("1".equals(infor_Flag)) {
                a_temp = "NBASE,A0100";
            } else {
                a_temp = "A0100";
            }
		}
		else if ("2".equals(infor_Flag)) {
            a_temp = "B0110";
        } else if ("3".equals(infor_Flag)) {
            a_temp = "E01A1";
        }
		if("1".equals(infor_Flag)|| "2".equals(infor_Flag)|| "3".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)){
		    if("1".equals(infor_Flag)) {
                tempSql.append(",max(NBASE) as NBASE,max(A0100) as A0100");
            } else {
                tempSql.append(",max("+a_temp+") as "+a_temp);
            }
		}
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			String extendattr=temp[28]==null?"":temp[28].toUpperCase();
			if(extendattr.indexOf("GROUPSUM")!=-1&&("N".equalsIgnoreCase(temp[9])))
			{
				String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
				/**求和*/
				if("0".equals(groupSum))
				{/*
					if(infor_Flag.equalsIgnoreCase("salary"))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				        	tempSql.append(",Sum(C"+temp[0]+") C" + temp[0]);
				    	else
			    			tempSql.append(",SUM(C"+temp[0]+") C" + temp[0]);
			    	}
			    	else
		    		    tempSql.append(",SUM(*) C" + temp[0]);*/
					tempSql.append(" ,sum(C"+temp[0]+") C"+temp[0]);
				}else if("1".equals(groupSum))
				{
					tempSql.append(" ,avg(C"+temp[0]+") C"+temp[0]);
				}
				else if("2".equals(groupSum))
				{
					tempSql.append(" ,max(C"+temp[0]+") C"+temp[0]);
				}
				else if("3".equals(groupSum))
				{
					tempSql.append(" ,min(C"+temp[0]+") C"+temp[0]);
				}
			}
			else
			{
		     	if (temp[9] != null && "N".equals(temp[9])) {
		     		if("stipend".equals(infor_Flag)&&temp[25]!=null&&temp[25].toUpperCase().endsWith("Z1"))
		     		{
		     			tempSql.append(",max(C");
		    	    	tempSql.append(temp[0] + ") C" + temp[0]);
		     		}
		     		else
		     		{
		    	    	tempSql.append(",sum(C");
		    	    	tempSql.append(temp[0] + ") C" + temp[0]);
		     		}
		    	} else if (temp[7] != null && "R".equals(temp[7])) {
		    		if("salary".equalsIgnoreCase(infor_Flag))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            tempSql.append(",count(distinct nbase||a0100");
                        } else {
                            tempSql.append(",count(distinct nbase+a0100");
                        }
			    	}
			    	else {
                        tempSql.append(",count(*");
                    }
			    	tempSql.append(") as C" + temp[0]);
		    	}else if(temp[7]!=null&&!"S".equals(temp[7])&& !"G".equals(temp[7])&& !"E".equals(temp[7]) && !"H".equals(temp[7])&& !"P".equals(temp[7]) && !"R".equals(temp[7])&& !"".equals(temp[7]))
	    		{
 		    		tempSql.append(",max( C" + temp[0]+") as C"+temp[0]);
    			}
		    	else {
                    tempSql.append(",' ' C" + temp[0]);
                }
			}
		}
		if(this.isGroupV2)
		{
	    	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint != null && "1".equals(isGroupPoint))
	     	{
	            tempSql.append(",GroupV2,GroupN2");
	     	}
	    	else {
                tempSql.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
            }
		}
		if (isGroupPoint != null && "1".equals(isGroupPoint)){
			tempSql.append(",GroupV,GroupN");
			HmusterXML hmxml = new HmusterXML(this.conn,this.tabID);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){

					tempSql.append(",(select A0000 from organization where codeitemid=");
					tempSql.append(tableName);
					tempSql.append(".GroupN) AS A0000 ");
				}
			}
		}
		tempSql.append(",max(recidx) as recidx from ");
		tempSql.append(tableName);

		return tempSql.toString();
	}

	/**
	 * 构建页小计和页累计
	 * 
	 * @param aheight
	 * @param fontStyle
	 * @param hmusterViewBo
	 * @param table
	 * @param writer
	 * @param bottomList
	 * @param propertyKey
	 * @param fieldValue
	 * @param field
	 * @param zeroPrint
	 */
	public void writePageCount(int aheight, String[] fontStyle,
			HmusterViewBo hmusterViewBo, PdfPTable table, PdfWriter writer,
			ArrayList bottomList, String propertyKey, double[] fieldValue,
			String[] field, String zeroPrint, String printGrid) {
		
		int column_num=0;
		for (Iterator t = bottomList.iterator(); t.hasNext();) {
			
			String[] temp = (String[]) t.next();
			String context = " ";
			if (column_num==0) {
				if (propertyKey != null) {
                    if(propertyKey.indexOf(".")!=-1) {
                        context = ResourceFactory.getProperty(propertyKey);
                    } else {
                        context = propertyKey;
                    }
                }
			} else {
				if (field != null) {
					for (int b = 0; b < field.length; b++) {
						if (field[b].equals("C" + temp[0])) {
							if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
							{
								if (fieldValue[b] == 0) {
                                    context = " ";
                                } else {
                                    context = hmusterViewBo.round(String
                                            .valueOf(fieldValue[b]), Integer
                                            .parseInt(temp[6]));
                                }
							} else {
                                context = hmusterViewBo.round(String
                                        .valueOf(fieldValue[b]), Integer
                                        .parseInt(temp[6]));
                            }
							break;
						}
					}
				}
			}
			int aFontSize = Integer.parseInt(fontStyle[2]);
			Font font =FontFamilyType.getFont(fontStyle[0],fontStyle[1], aFontSize,this.platform);
			if("1".equals(temp[26])) {
                context=" ";
            }
//			如果用户对该指标无权限，则不予显示数据
			if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
			{
				if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
				{
					if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
					{
						
					}
					else
					{
				    	context=" ";
					}
				}
			}
			Paragraph paragraph = new Paragraph(context, font);
			PdfPCell cell = new PdfPCell(paragraph);
			cell.setMinimumHeight(aheight - 4); // 设置单元格的最小高度
			setAlignment(cell,Integer.parseInt(temp[13]));
			if ("0".equals(printGrid)) {
                cell.setBorder(0);
            }
			if(table!=null) {
                table.addCell(cell);
            }
			column_num++;
		}
	}
	/**
	 * 构建页小计和页累计
	 * 
	 * @param aheight
	 * @param fontStyle
	 * @param hmusterViewBo
	 * @param table
	 * @param writer
	 * @param bottomList
	 * @param propertyKey
	 * @param fieldValue
	 * @param field
	 * @param zeroPrint
	 */
	public void writePageCount(float top,float left,int aheight,
			HmusterViewBo hmusterViewBo, PdfWriter writer,
			ArrayList bottomList, String propertyKey, double[] fieldValue,
			String[] field, String zeroPrint, String printGrid) {
	    boolean titleAdded = false;
		for (Iterator t = bottomList.iterator(); t.hasNext();) {
			
			String[] temp = (String[]) t.next();
			if(temp[27]!=null&& "1".equals(temp[27])) {
                continue;
            }
			String context = " ";
			if(this.resourceCloumn==Integer.parseInt(temp[0])&&
					!"N".equalsIgnoreCase(temp[9])&&
					!"R".equalsIgnoreCase(temp[7]))  // 记录数格
			{
		    	if(propertyKey != null) {
                    if(propertyKey.indexOf(".")!=-1) {
                        context = ResourceFactory.getProperty(propertyKey);
                    } else {
                        context = propertyKey;
                    }
                }
		    	titleAdded = true;
			}
			else if (!titleAdded && !"N".equalsIgnoreCase(temp[9])) {
				if (propertyKey != null) {
                    if(propertyKey.indexOf(".")!=-1) {
                        context = ResourceFactory.getProperty(propertyKey);
                    } else {
                        context = propertyKey;
                    }
                }
				titleAdded = true;
			} else {
				if (field != null) {
					for (int b = 0; b < field.length; b++) {
						if (field[b].equals("C" + temp[0])) {
							if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
							{
								if (fieldValue[b] == 0) {
                                    context = " ";
                                } else {
                                    context = hmusterViewBo.round(String
                                            .valueOf(fieldValue[b]), Integer
                                            .parseInt(temp[6]));
                                }
							} else {
                                context = hmusterViewBo.round(String
                                        .valueOf(fieldValue[b]), Integer
                                        .parseInt(temp[6]));
                            }
							break;
						}
					}
				}
			}
			if("1".equals(temp[26])) {
                context=" ";
            }
//			如果用户对该指标无权限，则不予显示数据
			if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[25]!=null&&temp[25].trim().length()>0)
			{
				if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(("5".equals(this.modelFlag)&&!temp[26].toUpperCase().startsWith("V_EMP_"))?temp[25].replaceAll("_1", "").replaceAll("_2",""):temp[25])))
				{
					if(temp[26]!=null&&temp[26].startsWith("V_EMP_"))
					{
						
					}
					else
					{
				    	context=" ";
					}
				}
			}
			int fontSize = Integer.parseInt(temp[12]);
			int fontAlign = Integer.parseInt(temp[13]);
			String fontEffect = temp[11];
			float width = Float.parseFloat(temp[4]);
			String[] ltrb = {temp[21],temp[22],temp[23],temp[24]};
			excecute(context, Float.parseFloat(temp[2]), top, width, aheight, fontAlign,
					fontEffect, fontSize,temp[10], writer, Integer.parseInt(printGrid), ltrb);
		}
	}
	/**
	 * 生成字体样式,解决中文问题
	 * 
	 */
	/*
	public Font getFont(String fontEffect, int fontSize) {
		Font font = null;
		try {
			// 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
			BaseFont bfComic = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED); // 解决中文问题
			if (fontEffect.equals("2")) {
				font = new Font(bfComic, fontSize + 3, Font.BOLD);
			}
			if (fontEffect.equals("3")) {
				font = new Font(bfComic, fontSize + 3, Font.ITALIC);
			}
			if (fontEffect.equals("4")) {
				font = new Font(bfComic, fontSize + 3, Font.BOLD | Font.ITALIC);
			} else {
				font = new Font(bfComic, fontSize + 3, Font.NORMAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return font;

	}
*/

	/**
	 * 生成标题
	 * 
	 * @param titelTopList
	 *            表头上部的标题信息列表
	 * @param titleBottomnList
	 *            表头下部的标题信息列表
	 * @param tableBodyHeight
	 *            表体高度 （像素）
	 * @param paperRows
	 *            每页行数
	 * @param height
	 *            每行的高度
	 * @author dengc
	 * @return created : 2006/03/31
	 */

	private void getTitle(String tabid,boolean isGroupTerm, float page_height,
			PdfWriter writer, int height, int factRows, ArrayList titelList,
			String userName, String currentPage, int paperRows,
			String infor_Flag, String tempTable, String isGroupPoint,
			String groupV, float tableHeaderTop, float tableHeaderHeight,
			String history, String year, String month, String counts,
			UserView userView, String dbpre) throws GeneralException {
		int count = -1; // 总行数
		int pages = 0; // 总页数
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			/* 生成表头上部的标题信息 */
			if (titelList.size() >= 1) {

				//for (Iterator t = titelList.iterator(); t.hasNext();) {
				for (int i=0;i< titelList.size(); i++) {
					String[] temp = (String[]) titelList.get(i);
					byte[] buf = null;
					Image image  = null;
					String context = "";
					String[] ltrb = new String[4];

					switch (Integer.parseInt(temp[16])) {
					case 0:
						context = temp[8]!=null?temp[8].toString():""; // 文本描述
						break;
					case 1:
						/*GregorianCalendar d = new GregorianCalendar();
						context = ResourceFactory
								.getProperty("hmuster.label.createTableDate")
								+ ":"
								+ d.get(Calendar.YEAR)
								+ "."
								+(d.get(Calendar.MONTH)+1)
								+ "."
								+ d.get(Calendar.DATE);*/
						context=this.getCreateTableDate(temp[18]);
						break;
					case 2:
						Date dd = new Date(); // 制表时间
						context = ResourceFactory
								.getProperty("hmuster.label.createTableTime")
								+ DateFormat.getTimeInstance(DateFormat.MEDIUM,
										Locale.CHINA).format(dd);
						break;
					case 3:
						/*context = ResourceFactory
								.getProperty("hmuster.label.createTableMen")
								+ ":" + userName; // 制表人*/		
						context=this.getCreateTablePerson(userName, temp[18]);
					break;
					case 4: // 总页数 调用hmusterBo 与页面显示保持一致
						pages=hmusterViewBo.getHmusterTotalPages(isGroupTerm, infor_Flag, isGroupPoint, tempTable, paperRows, dbpre);
						context = ResourceFactory
								.getProperty("hmuster.label.total")
								+ pages
								+ ResourceFactory
										.getProperty("hmuster.label.paper");
						break;
					case 5:
						context = ResourceFactory
								.getProperty("hmuster.label.d")
								+ currentPage
								+ ResourceFactory
										.getProperty("hmuster.label.paper"); // 页码
						break;
					case 12: //-#-
						context = "-"
						+ currentPage
						+"-"; 
						break;	
					case 6:
						if (count == -1) // 总行数
						{
							String tempCount2 = "";
							String where2 = this.privConditionStr;
							if ("1".equals(infor_Flag)) {
								tempCount2 = " count(a0100) ";								
							} else if ("2".equals(infor_Flag)) {
                                tempCount2 = " count(b0110) ";
                            } else if ("3".equals(infor_Flag)) {
                                tempCount2 = " count(e01a1) ";
                            }
							String sql2 ="";
							if(tempCount2.length()>1) {
                                sql2 = "select " + tempCount2 + " from "
                                    + tempTable + where2;
                            } else {
                                sql2 = "select count(*) from "
                                + tempTable + where2;
                            }
							rowSet = dao.search(sql2);
							if (rowSet.next()) {
								count = rowSet.getInt(1);
							} else {
								count = 0;
							}
						
						}
						context = count+"";
						break;
					case 7: // 分组指标
						if (isGroupPoint == null || "0".equals(isGroupPoint)) {
							context = " ";
						} else {
							context = groupV;

						}
						break;
					case 8:						
						context=this.hmusterViewBo.getYearMonth(infor_Flag, history, tempTable, year, month, 1, count+"", this.hmusterViewBo.getSetChangeFlag(tabid));
						break;
					case 9:
						if(topDateTitleMap.get(i+"")!=null) {
                            context = (String)topDateTitleMap.get(i+"");
                        } else {
                            context=this.hmusterViewBo.getYearMonth(infor_Flag, history, tempTable, year, month, 2, count+"", this.hmusterViewBo.getSetChangeFlag(tabid));
                        }
						break;
					case 10:   //标题变量
						if(temp[18]!=null&&temp[18].trim().length()>1)
						{
							context=getTitleVarValue(temp[18],infor_Flag,tempTable,year,month);
						}
						else {
                            context=" ";
                        }
						break;
					case 14:    //组内记录
						if (!isGroupTerm&&isGroupPoint!= null&& "1".equals(isGroupPoint)&&isGroupNoPage!=null&& "0".equals(isGroupNoPage)) {
							if(temp[18]!=null&&temp[18].trim().length()>1){
								context=getTitleCount(groupV,tempTable);
							}else {
                                context=" ";
                            }
						}else{
							context=" ";
						}
						break;	
					case 13://照片
						buf=this.getImageByte(tabid, temp[7]);
						ltrb = new String[]{"1","1","1","1"};
						if(buf.length>0)
						{
							image = Image.getInstance(buf);	

							int a_width=0;
							if(temp[10].indexOf(".")!=-1) {
                                a_width=(Integer.parseInt(temp[10].substring(0,temp[10].indexOf("."))))-5;
                            } else {
                                a_width=(Integer.parseInt(temp[10]))-5;
                            }
							image.scaleAbsolute(Float.parseFloat(temp[10]), Float.parseFloat(temp[12])-4);
						}
						break;
		            case 16:// 审批意见
		                if(hmusterViewBo!=null) {
                            context = hmusterViewBo.getGzTaoSpProcess();
                        }
		                break;
					}
					
					if (Float.parseFloat(temp[11]) < tableHeaderTop) // 表头上部标题
					{

						if(image!=null){
							excecute(buf,image, Float.parseFloat(temp[9]),
									page_height - Float.parseFloat(temp[11]), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}else{
							if(Float.parseFloat(temp[10])<Float.parseFloat((context.getBytes().length*10+""))) {
                                temp[10]=context.getBytes().length*10+"";
                            }
							excecute(context, Float.parseFloat(temp[9]),
									page_height - Float.parseFloat(temp[11]), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}

					}else if (Float.parseFloat(temp[11]) >tableHeaderTop
							&&Float.parseFloat(temp[11])< (tableHeaderTop + tableHeaderHeight)) // 表头下部标题
					{
						if(image!=null){
							excecute(buf,image, Float.parseFloat(temp[9]),
									page_height - Float.parseFloat(temp[11]), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}else{
							if(Float.parseFloat(temp[10])<Float.parseFloat((context.getBytes().length*10+""))) {
                                temp[10]=context.getBytes().length*10+"";
                            }
							excecute(context, Float.parseFloat(temp[9]),
									page_height - Float.parseFloat(temp[11]), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}

					}else if (Float.parseFloat(temp[11]) > (tableHeaderTop + tableHeaderHeight)) // 表头下部标题
					{
						if(image!=null){
							excecute(buf,image, Float.parseFloat(temp[9]),
									page_height- (Float.parseFloat(temp[11]) + height* factRows), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}else{							
							if(Float.parseFloat(temp[10])<Float.parseFloat((context.getBytes().length*10+""))) {
                                temp[10]=context.getBytes().length*10+"";
                            }
							excecute(context, Float.parseFloat(temp[9]),
									page_height- (Float.parseFloat(temp[11]) + height* factRows), Float.parseFloat(temp[10]), Float.parseFloat(temp[12]), 6, temp[15],
									Integer.parseInt(temp[13]),temp[14], writer, 0, ltrb);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);
		} 
	}

	public String getTitleCount(String groupV,String tempTable){
		String context="";
		ResultSet resultSet=null;
		Statement statement=null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select count(*) as counts from ");
			buf.append(tempTable);
			if(groupV!=null&&groupV.trim().length()>0) {
                buf.append(" where groupV='"+groupV+"'");
            }
			ContentDAO dao = new ContentDAO(conn);
			resultSet=dao.search(buf.toString());
			if(resultSet.next()){
				context = resultSet.getInt("counts")+"";
			}else{
				context = "0";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return context;
	}
	public String getYearMonth(String infor_Flag,String history,String tempTable,String year,String month,int type,String count)
	{
        DbWizard dbw = new DbWizard(conn);
		String context="";
		try
		{
			Calendar c=Calendar.getInstance();
			if("salary".equals(infor_Flag)|| "stipend".equals(infor_Flag))
			{
				
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z0,a00z1 from "+tempTable);
				if(rowSet.next())
				{
					Date d=rowSet.getDate(1);
					if(d!=null)
					{
						c.setTime(d);
						context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+(c.get(Calendar.MONTH)+1)+ResourceFactory.getProperty("kq.wizard.month");
					    String a00z1=rowSet.getString("a00z1");
					    if(a00z1!=null&&type==2) {
                            context+=a00z1+ResourceFactory.getProperty("hmuster.label.count");
                        }
					}
				}
			}
			else if("81".equals(modelFlag)&&dbw.isExistField(tempTable, "Q03Z0", false)) {
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rowSet=dao.search("select Max(Q03Z0) AS Q03Z0 from "+tempTable);
                if(rowSet.next())
                {
                    String s=rowSet.getString("Q03Z0");
                    if(s != null) {
                        s += "-01";
                    }
                    if(s == null) {
                        s = ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
                    }
                    if(s != null) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = df.parse(s);
                        if(d!=null)
                        {
                            c.setTime(d);
                            context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+(c.get(Calendar.MONTH)+1)+ResourceFactory.getProperty("kq.wizard.month");
                        }
                    }
                }
			}
			else
			{
                if(type==2)
                {
                	boolean ischangeflag=this.getSetChangeFlag();
                	if(ischangeflag&& "3".equals(history))
                	{
                		context = year
						+ ResourceFactory.getProperty("hmuster.label.year")
						+ month
						+ ResourceFactory.getProperty("hmuster.label.month");
				    	context+=count+ResourceFactory.getProperty("hmuster.label.count");
                	}
                }else{
		    		if ("3".equals(history)) {
		    			context = year
			    				+ ResourceFactory.getProperty("hmuster.label.year")
			    				+ month
			    				+ ResourceFactory.getProperty("hmuster.label.month");
				 
	    			} else if("2".equals(history)) {//取部分历史
	    				if(this.yearmonth!=null&&this.yearmonth.length()>0)
	    				{
	    					context=this.yearmonth;
	    				}else{
				    	
	    				 
				    		String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
				    		String ayear="";
				    		String amonth="";
				    		String aday="";
				    		if(value!=null&&!"".equals(value))
				    		{
				    			ayear=value.substring(0,4);
				    			amonth=value.substring(5,7);
				    			aday=value.substring(8);
				    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
				    			ayear=this.userView.getAppdate().substring(0,4);
				    			amonth=this.userView.getAppdate().substring(5,7);
				     			aday=this.userView.getAppdate().substring(8);
				    		}else
				    		{
				    			ayear=c.get(Calendar.YEAR)+"";
				    			amonth=c.get(Calendar.MONTH)+1+"";
				    		}
				    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    				}
	    			}else{
	    				String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
			    		String ayear="";
			    		String amonth="";
			    		String aday="";
			    		if(value!=null&&!"".equals(value))
			    		{
			    			ayear=value.substring(0,4);
			    			amonth=value.substring(5,7);
			    			aday=value.substring(8);
			    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
			    			ayear=this.userView.getAppdate().substring(0,4);
			    			amonth=this.userView.getAppdate().substring(5,7);
			     			aday=this.userView.getAppdate().substring(8);
			    		}else
			    		{
			    			ayear=c.get(Calendar.YEAR)+"";
			    			amonth=c.get(Calendar.MONTH)+1+"";
			    		}
			    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    			}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return context;
	}
	
	public boolean getSetChangeFlag()
	{
		boolean flag= false;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select setname,Field_Name from Muster_Cell where Tabid="+this.tabID);
			while(rs.next())
			{
				String setname=rs.getString("setname");
				if(setname==null|| "".equals(setname)) {
                    continue;
                }
				FieldSet vo = DataDictionary.getFieldSetVo(setname.toLowerCase());
				if(vo==null) {
                    continue;
                }
				if(!"0".equals(vo.getChangeflag()))//按年或按月变化的子集
				{
					flag=true;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	/**
	 * 取得记录的总行数
	 * @param infor_Flag
	 * @param tempTable
	 * @return
	 */
	public int getCount(String infor_Flag,String tempTable)
	{
		int count=0;
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String tempCount2 = "";
			String where = this.privConditionStr;
			if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)) {
				tempCount2 = " count(a0100) ";							
			} else if ("2".equals(infor_Flag)) {
                tempCount2 = " count(b0110) ";
            } else if ("3".equals(infor_Flag)) {
                tempCount2 = " count(e01a1) ";
            } else {
                tempCount2 = " count(a0100) ";
            }
			String sql2 = "select " + tempCount2 + " from "
					+ tempTable + where;
			rowSet = dao.search(sql2);
			if (rowSet.next()) {
				count = rowSet.getInt(1);
			} else {
				count = 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	
	
	
	
	/**
	 * 取得标题变量
	 * @param extendAtrr  变量的描述信息
	 * @return
	 */
	public String getTitleVarValue(String extendAtrr,String infor_Flag,String tempTable)
	{
		String context="";
		HashMap map=analyseExtendAttr(extendAtrr);   //分析字符串描述
		int mode=Integer.parseInt((String)map.get("mode"));
		String type=(String)map.get("type");
		String expr=(String)map.get("expr");
		if(mode==1)      //求个数
		{
				context=String.valueOf(getCount(infor_Flag,tempTable));
		}
		else
		{
			// 2 首记录  3末记录   4平均值   5求总合   6求最大   7求最小
			String sql=getTitleVarSql(map,tempTable);
			ResultSet resultSet=null;
			Statement statement=null;
			if(sql==null||sql.trim().length()<5) {
                return "";
            }
			try
			{
				ContentDAO dao = new ContentDAO(conn);
				resultSet=dao.search(sql);
				String value="";
				if(mode==3){
					while(resultSet.next())
					{
						if(!"M".equals(type)) {
                            value=resultSet.getString(expr);
                        } else {
                            value=Sql_switcher.readMemo(resultSet,expr);
                        }
					}
				}else{
					if(resultSet.next())
					{
						if(!"M".equals(type)) {
                            value=resultSet.getString(expr);
                        } else {
                            value=Sql_switcher.readMemo(resultSet,expr);
                        }
					}
				}
			
				if("N".equals(type))
				{
					String dec=(String)map.get("dec");
					if(value!=null&&value.trim().length()>0){
						value=round(value,Integer.parseInt(dec));
						value = "          "+value;
					}else {
                        value="";
                    }
				}
				context=value;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return context;
	}
	public String extendAttrXML(String ext,String par){
		String value="";
		String starStr = "<"+par+">";
		String endStr = "</"+par+">";
		if(ext.indexOf(starStr)!=-1){
			value = ext.substring(ext.indexOf(starStr)
					+starStr.length(),
					ext.indexOf(endStr));
		}
		return value;
	}
	/**
	 * 取得标题变量
	 * @param extendAtrr  变量的描述信息
	 * @return
	 */
	public String getTitleVarValue(String extendAtrr,String infor_Flag,String tempTable,
			String year,String month)
	{
		String context="";
		HashMap map=analyseExtendAttr(extendAtrr);   //分析字符串描述
		int mode=Integer.parseInt((String)map.get("mode"));
		String type=(String)map.get("type");
		String expr=(String)map.get("expr");
		String BIGNUM = extendAttrXML(extendAtrr,"BIGNUM");
		if(mode==1)      //求个数
		{
				context=String.valueOf(getCount(infor_Flag,tempTable));
				if("true".equalsIgnoreCase(BIGNUM)){
					context = NumToRMBBo.NumToRMBStr(Double.parseDouble(context));
				}
		}
		else
		{
			// 2 首记录  3末记录   4平均值   5求总合   6求最大   7求最小
			String sql= "";
			if("salary".equalsIgnoreCase(infor_Flag)) {
                sql=getTitleVarSql(map,tempTable,year,month);
            } else {
                sql=getTitleVarSql1(map,tempTable);
            }
			ResultSet resultSet=null;
			Statement statement=null;
			if(sql==null||sql.trim().length()<5) {
                return "";
            }
			try
			{
				ContentDAO dao = new ContentDAO(conn);
				resultSet=dao.search(sql);
				String value="";
				if(mode==3){
					while(resultSet.next())
					{
						if(!"M".equals(type)) {
                            value=resultSet.getString(expr);
                        } else {
                            value=Sql_switcher.readMemo(resultSet,expr);
                        }
					}
				}else{
					if(resultSet.next())
					{
						if(!"M".equals(type)) {
                            value=resultSet.getString(expr);
                        } else {
                            value=Sql_switcher.readMemo(resultSet,expr);
                        }
					}
				}
			
				if("N".equals(type))
				{
					String dec=(String)map.get("dec");
					if(value!=null&&value.trim().length()>0){
						value=round(value,Integer.parseInt(dec));
						if("true".equalsIgnoreCase(BIGNUM)){
							value = NumToRMBBo.NumToRMBStr(Double.parseDouble(value));
						}
						value = "          "+value;
					}else{
						if("true".equalsIgnoreCase(BIGNUM)){
							value = "          零圆整";
						}else {
                            value="         0";
                        }
					}
				}
				context=value;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return context;
	}
	
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql(HashMap map,String tempTable)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if("stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            }
		
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)==null||this.gridNoMap.get(expr).toString().trim().length()<1) {
            return "";
        }
		sql.append("C"+(String)this.gridNoMap.get(expr));
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			sql.append(")");
		}
		sql.append(" "+expr+" from "+tempTable);	
		if("2".equals(mode))
		{
			if (this.isGroupPoint != null
					&& "1".equals(isGroupPoint)) {
				sql.append(" order by GroupN ");
			} else if (this.isGroupPoint != null
					&& "1".equals(isGroupPoint)) {
				sql.append(" group by GroupN,GroupV  ");
			}
			else {
                sql.append(" where recidx=1");
            }
		}
		if("3".equals(mode)) {
            sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
        }
		return sql.toString();
	}
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql1(HashMap map,String tempTable)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if("stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            }
		
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null&&this.gridNoMap.get(expr).toString().trim().length()>0){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+tempTable);	
			if("2".equals(mode))
			{
				if (this.isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN ");
				} else if (this.isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				FieldItem fielditem1 = DataDictionary.getFieldItem(groupPoint);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())
						|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					sql.append(" where B0110='");
					sql.append(this.getGroupNcode());
					sql.append("'");
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql(HashMap map,String tempTable,String year,String month)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if("stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            }
		
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null&&this.gridNoMap.get(expr).toString().trim().length()>0){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+tempTable);	
			if("2".equals(mode))
			{
				if (this.isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN ");
				} else if (this.isGroupPoint != null
						&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				FieldItem fielditem1 = DataDictionary.getFieldItem(groupPoint);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())
						|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					if(year!=null&&year.trim().length()==4){
						sql.append(" where ");
						sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
						if(month!=null&&month.length()>0){
							sql.append(" and ");
							sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
						}
						sql.append(" and B0110='");
						sql.append(this.getGroupNcode());
						sql.append("'");
					}else{
						ContentDAO dao = new ContentDAO(this.conn);
						RowSet rowSet;
						try {
							rowSet = dao.search("select a00z0 from "+tempTable);
							Calendar c=Calendar.getInstance();
							while(rowSet.next()){
								Date d=rowSet.getDate(1);
								if(d!=null){
									c.setTime(d);
									year = c.get(Calendar.YEAR)+"";
									month = (c.get(Calendar.MONTH)+1)+"";
									break;
								}
							}
							if(year!=null&&year.trim().length()==4){
								sql.append(" where ");
								sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
								if(month!=null&&month.length()>0){
									sql.append(" and ");
									sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
								}
							}
							sql.append(" and B0110='");
							sql.append(this.getGroupNcode());
							sql.append("'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	
	public String  isNotNull(String fieldName)
	{
		String str=fieldName+" is not null ";
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
            str+=" and "+fieldName+"<>''";
        }
		return str;
	}

	
	
	public HashMap getNoMap(String tabid)
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search("select GridNo,field_Name from muster_cell where "+isNotNull("field_Name")+" and  tabid="+tabid);
			while(rowSet.next())
			{
				map.put(rowSet.getString("field_Name").toUpperCase(),rowSet.getString("gridno"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	private HashMap  analyseExtendAttr(String extendAttr)
	{
		HashMap map=new HashMap();
		String s=extendAttr;
		String setid=s.substring(s.indexOf("<SETID>")+7,s.indexOf("</SETID>"));
		String expr=s.substring(s.indexOf("<EXPR>")+6,s.indexOf("</EXPR>"));
		String mode=s.substring(s.indexOf("<MODE>")+6,s.indexOf("</MODE>"));
		String type=s.substring(s.indexOf("<TYPE>")+6,s.indexOf("</TYPE>"));
		String format=s.substring(s.indexOf("<FORMAT>")+8,s.indexOf("</FORMAT>"));
		String len=s.substring(s.indexOf("<LEN>")+5,s.indexOf("</LEN>"));
		String dec=s.substring(s.indexOf("<DEC>")+5,s.indexOf("</DEC>"));
		
		map.put("setid",setid);
		map.put("expr",expr);
		map.put("mode",mode);
		map.put("type",type);
		map.put("format",format);
		map.put("len",len);
		map.put("dec",dec);
		
		return map;
	}
	
	
	
	
	
	
	
	
	
	
	public int resetSize(float width,float height,int fontsize,String context)
	{
		boolean flag=true;
		int a_size=fontsize;
		while(flag&&a_size>0)
		{
			if(width/(a_size+4)>2&&context.getBytes().length/2>(height/(a_size+4))*(width/(a_size+4)))
			{ 
				a_size=(int)(a_size*(((height/(a_size+4))*(width/(a_size+4)-2))/(context.getBytes().length/2))); 
				a_size-=2;
			}
			else {
                break;
            }
		}
		return a_size;
		
	}

	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param context
	 *            内容
	 * @param leftP
	 *            左边坐标（像素）
	 * @param top
	 *            顶端坐标（像素：从下往上数）
	 * @param width
	 *            表体宽度
	 * @param height
	 *            单元格高度
	 * @param align
	 *            单元格内容的排列方式
	 * @param fontEffect
	 *            字体效果
	 * @param fontSize
	 *            字体大小
	 * @param ltrb[]
	 *            c.L,c.T,c.R,c.B
	 * @param writer
	 */
	public void excecute(String context, float leftP, float top, float width,
			float height, int align, String fontEffect, int fontSize,String a_fontName,
			PdfWriter writer, int border, String[] ltrb) {

		Font font =null;
		if (border == 1) {
			//改为调用新字体计算，较为精准，表头默认不缩小字体，当border=2时不缩小字体
			ResetFontSizeUtil res=new ResetFontSizeUtil();
			fontSize=res.ResetFontSize(width, height, context, fontSize, a_fontName, Integer.parseInt(fontEffect));
		}
		font=FontFamilyType.getFont(a_fontName,fontEffect, fontSize,this.platform); // 生成字体样式
		font.setColor(0,0,0);
		
		
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(width); // 设置表的总体宽度
		if (border == 1||border == 2) {
			table.setLockedWidth(true); // 宽度锁定
		}
		Paragraph paragraph = null;

		try {
			paragraph = new Paragraph(context, font);
			PdfPCell cell = new PdfPCell(paragraph);
			if (border == 0) {
				cell.setBorder(0);
				//cell.setNoWrap(true);
			}else{
				Color color = new Color(255,255,255);
				cell.setBackgroundColor(color);
			}
			cell.setBorderColor(new Color(0,0,0));
			/*if(ltrb[0]!=null&&ltrb[0].equals("0"))
				cell.setBorderWidthLeft(0);*/
		    if(ltrb[1]!=null&& "0".equals(ltrb[1])) {
		        cell.setBorderWidthTop(0);
		    }
		  /*  if(ltrb[2]!=null&&ltrb[2].equals("0"))
				cell.setBorderWidthRight(0);*/
			if(ltrb[3]!=null&& "0".equals(ltrb[3])) {
			    cell.setBorderWidthBottom(0);
			}
			if(ltrb[0]!=null&& "0".equals(ltrb[0])) {
                cell.setBorderWidthLeft(0);
            }
			if(ltrb[2]!=null&& "0".equals(ltrb[0])) {
                cell.setBorderWidthRight(0);
            }
			
			cell.setMinimumHeight(height); // 设置单元格的最小高度
			
			//cell.setFixedHeight(height);
			setAlignment(cell,align);
			table.addCell(cell);
			table.writeSelectedRows(0, 1, leftP+this.left_space, (top-this.top_space), writer.getDirectContent()); // 固定坐标
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param context
	 *            内容
	 * @param leftP
	 *            左边坐标（像素）
	 * @param top
	 *            顶端坐标（像素：从下往上数）
	 * @param width
	 *            表体宽度
	 * @param height
	 *            单元格高度
	 * @param align
	 *            单元格内容的排列方式
	 * @param fontEffect
	 *            字体效果
	 * @param fontSize
	 *            字体大小
	 * @param ltrb[]
	 *            c.L,c.T,c.R,c.B
	 * @param writer
	 */
	public void excecuteL(String context, float leftP, float top, float width,
			float height, int align, String fontEffect, int fontSize,String a_fontName,
			PdfWriter writer, int border, String[] ltrb) {

		Font font =null;
		if (border == 1) {
            fontSize=resetSize(width,height,fontSize,context);
        }
		font=FontFamilyType.getFont(a_fontName,fontEffect, fontSize,this.platform); // 生成字体样式
		font.setColor(0,0,0);
		
		
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(width); // 设置表的总体宽度
		if (border == 1) {
            table.setLockedWidth(true); // 宽度锁定
        }
		Paragraph paragraph = null;

		try {
			paragraph = new Paragraph(context, font);
			PdfPCell cell = new PdfPCell(paragraph);
			if (border == 0) {
				cell.setBorder(0);
				cell.setNoWrap(true);
			}else{
				Color color = new Color(255,255,255);
				cell.setBackgroundColor(color);
			}
			if(ltrb[0]!=null&& "0".equals(ltrb[0])) {
                cell.setBorderWidthLeft(0);
            } else if(ltrb[1]!=null&& "0".equals(ltrb[1])) {
                cell.setBorderWidthTop(0);
            } else if(ltrb[2]!=null&& "0".equals(ltrb[2])) {
                cell.setBorderWidthRight(0);
            } else if(ltrb[3]!=null&& "0".equals(ltrb[3])) {
                cell.setBorderWidthBottom(0);
            }
			
			cell.setMinimumHeight(height); // 设置单元格的最小高度
			
			//cell.setFixedHeight(height);
			setAlignment(cell,align);
			table.addCell(cell);
			table.writeSelectedRows(0, 1, leftP+this.left_space, top-this.top_space, writer
							.getDirectContent()); // 固定坐标
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param context
	 *            内容
	 * @param leftP
	 *            左边坐标（像素）
	 * @param top
	 *            顶端坐标（像素：从下往上数）
	 * @param width
	 *            表体宽度
	 * @param height
	 *            单元格高度
	 * @param align
	 *            单元格内容的排列方式
	 * @param fontEffect
	 *            字体效果
	 * @param fontSize
	 *            字体大小
	 * @param ltrb[]
	 *            c.L,c.T,c.R,c.B
	 * @param writer
	 */
	public PdfPTable excecute(String context, float width,
			float height, int align, String fontEffect, int fontSize,String a_fontName,
			PdfWriter writer, int border) {

		Font font =null;
		if (border == 1) {
            fontSize=resetSize(width,height,fontSize,context);
        }
		font=FontFamilyType.getFont(a_fontName,fontEffect, fontSize,this.platform); // 生成字体样式
		font.setColor(0,0,0);
		
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(width); // 设置表的总体宽度
		if (border == 1) {
            table.setLockedWidth(true); // 宽度锁定
        }
		Paragraph paragraph = null;
		try {
			paragraph = new Paragraph(context, font);

			PdfPCell cell = new PdfPCell(paragraph);
			if (border == 0) {
				cell.setBorder(0);
				cell.setNoWrap(true);
			}
			cell.setMinimumHeight(height); // 设置单元格的最小高度
			//cell.setFixedHeight(height);
			setAlignment(cell,align);
			table.addCell(cell);
//			table.writeSelectedRows(0, 1, leftP+this.left_space, top-this.top_space, writer
//							.getDirectContent()); // 固定坐标
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param context
	 *            内容
	 * @param leftP
	 *            左边坐标（像素）
	 * @param top
	 *            顶端坐标（像素：从下往上数）
	 * @param width
	 *            表体宽度
	 * @param height
	 *            单元格高度
	 * @param align
	 *            单元格内容的排列方式
	 * @param fontEffect
	 *            字体效果
	 * @param fontSize
	 *            字体大小
	 * @param ltrb[]
	 *            c.L,c.T,c.R,c.B
	 * @param writer
	 */
	public void excecute(byte[] buf,Image image,
			float leftP, float top, float width,
			float height, int align, String fontEffect, int fontSize,String a_fontName,
			PdfWriter writer, int border, String[] ltrb) {
		PdfPTable table = new PdfPTable(1);

		table.setTotalWidth(width); // 设置表的总体宽度
		if (border == 1) {
            table.setLockedWidth(true); // 宽度锁定
        }
		try {
			PdfPCell pdfCell =null;
			//liuy 2014-12-2 5225：员工名册，本月新进员工一览表，取出来的人员都无照片，输出PDF后，照片格线错位了 start
			/*错误原因：当有照片时，得到照片后会给照片设置高度，然后将照片变量放到对象里；如果没有照片，原来会new一个空对象，这里面没有高度撑起单元格
			 * 解决方案：判断是否存在照片，如果没有，这按照空文本处理
			 * */
			if(buf.length>0)
			{
				
				if(image==null) {
					pdfCell = new PdfPCell();
				}else {
					pdfCell = new PdfPCell(image, false);
				}
				if(ltrb[0]!=null&& "0".equals(ltrb[0])) {
                    pdfCell.setBorderWidthLeft(0);
                } else if(ltrb[1]!=null&& "0".equals(ltrb[1])) {
                    pdfCell.setBorderWidthTop(0);
                } else if(ltrb[2]!=null&& "0".equals(ltrb[2])) {
                    pdfCell.setBorderWidthRight(0);
                } else if(ltrb[3]!=null&& "0".equals(ltrb[3])) {
                    pdfCell.setBorderWidthBottom(0);
                }
				pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(pdfCell);
				table.writeSelectedRows(0, 1, leftP+this.left_space+1/*有照片左右边线显不出来，所以加1*/, top-this.top_space-2/*有照片上下边线显不出来，所以加2*/, writer.getDirectContent()); // 固定坐标
			}else{
				//pdfCell=new PdfPCell(new Paragraph(""));
				String context="　";
				Font font =null;
				if (border == 1) {
                    fontSize=resetSize(width,height,fontSize,context);
                }
				font=FontFamilyType.getFont(a_fontName,fontEffect, fontSize,this.platform); // 生成字体样式
				font.setColor(0,0,0);
				table.setTotalWidth(width); // 设置表的总体宽度
				if (border == 1) {
                    table.setLockedWidth(true); // 宽度锁定
                }
				Paragraph paragraph = null;
				paragraph = new Paragraph(context, font);
				PdfPCell cell = new PdfPCell(paragraph);
				if (border == 0) {
					cell.setBorder(0);
				}else{
					Color color = new Color(255,255,255);
					cell.setBackgroundColor(color);
				}
				if(ltrb[0]!=null&& "0".equals(ltrb[0])) {
                    cell.setBorderWidthLeft(0);
                } else if(ltrb[1]!=null&& "0".equals(ltrb[1])) {
                    cell.setBorderWidthTop(0);
                } else if(ltrb[2]!=null&& "0".equals(ltrb[2])) {
                    cell.setBorderWidthRight(0);
                } else if(ltrb[3]!=null&& "0".equals(ltrb[3])) {
                    cell.setBorderWidthBottom(0);
                }
				cell.setMinimumHeight(height); // 设置单元格的最小高度
				
				setAlignment(cell,align);
				table.addCell(cell);
				table.writeSelectedRows(0, 1, leftP+this.left_space, (top-this.top_space), writer.getDirectContent()); // 固定坐标
			}
			//liuy end
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * 单元格内容的排列方式 =0上左 =1上中 =2上右 =3下左 =4下中 =5下右 =6中左 =7中中 =8中右
	 */
	public void setAlignment(PdfPCell cell,int align)
	{
		if (align == 0) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED); // 基于最合适的
		} else if (align == 1) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else if (align == 2) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		} else if (align == 3) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 4) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 5) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 6) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 7) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 居中
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 8) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT); // 居右
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
	}
	/**
	 * 根据人员库前缀和人员编码生成其对应的文件
	 * 
	 * @param userTable
	 *            应用库 usra01
	 * @param userNumber
	 *            0000001 ,a0100
	 * @param flag
	 *            'P'照片
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Object>  createPhotoFile2(String userTable, String userNumber,
			String flag) throws Exception {
		ArrayList list=new ArrayList();
		boolean photo_type=false;//判断图片是否是png JPEG jpg gif 格式 特殊处理这些格式图片
		byte [] buf=null; 
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		InputStream in=null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ext,Ole,fileid from ");
			strsql.append(userTable);
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {	
				String fileid=rowSet.getString("fileid");
				if(StringUtils.isNotEmpty(fileid)) {
					in = VfsService.getFile(fileid);
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			        byte[] buff = new byte[100];
			        int rc = 0;
			        while ((rc = in.read(buff, 0, 100)) > 0) {
			            byteArrayOutputStream.write(buff, 0, rc);
			        }
			        buf = byteArrayOutputStream.toByteArray();
				}else {
					buf=rowSet.getBytes("Ole");		
				}
						
				String ext=rowSet.getString("ext");
				if(".png".equalsIgnoreCase(ext)|| ".jpeg".equalsIgnoreCase(ext)|| ".gif".equalsIgnoreCase(ext)) {
					photo_type=true;
				}
			}
		
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(in);
		}
		
		if(buf==null) {
            buf=new byte[0];
        }
		list.add(buf);
		list.add(photo_type);
		return list;
	}
	
	
	
	

	/**
	 * 根据人员库前缀和人员编码生成其对应的文件
	 * 
	 * @param userTable
	 *            应用库 usra01
	 * @param userNumber
	 *            0000001 ,a0100
	 * @param flag
	 *            'P'照片
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String createPhotoFile(String userTable, String userNumber,
			String flag) throws Exception {
		File tempFile = null;
		String filename = "";
		ServletUtilities.createTempDir();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		InputStream in = null;
		java.io.FileOutputStream fout = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ext,Ole,fileid from ");
			strsql.append(userTable);
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");

			
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				try {
					if(StringUtils.isNotEmpty(rowSet.getString("fileid"))) {
						return rowSet.getString("fileid");
					}else {
						tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,
								rowSet.getString("ext"), new File(System
										.getProperty("java.io.tmpdir")));
						in = rowSet.getBinaryStream("Ole");
						fout = new java.io.FileOutputStream(tempFile);
						int len;
						byte buf[] = new byte[1024];
						
						while ((len = in.read(buf, 0, 1024)) != -1) {
							fout.write(buf, 0, len);
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
				filename = PubFunc.encrypt(tempFile.getName());
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeIoResource(fout);
			PubFunc.closeIoResource(in);
			PubFunc.closeResource(rowSet);
		}
		return filename;
	}
	/**
	 * 根据人员库前缀和人员编码生成其对应的文件
	 * 
	 * @param userTable
	 *            应用库 usra01
	 * @param userNumber
	 *            0000001 ,a0100
	 * @param flag
	 *            'P'照片
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String createTitlePhotoFile(String temptable, String GridNo,
			String[] temp) throws Exception {
		File tempFile = null;
		String filename = "";
		ServletUtilities.createTempDir();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		java.io.FileOutputStream fout = null;
		InputStream in = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select Content from ");
			strsql.append(temptable);
			strsql.append(" where GridNo='");
			strsql.append(GridNo);
			strsql.append("'");
			String ext = "";
			if(temp!=null&&temp.length>16&&temp[18].indexOf("<ext>")!=-1){
				ext = temp[18].substring(temp[18].indexOf("<ext>")+5,temp[18].indexOf("</ext>"));
			}
			
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, ext, new File(System.getProperty("java.io.tmpdir")));
				in = rowSet.getBinaryStream("Content");
				fout = new java.io.FileOutputStream(tempFile);
				int len;
				byte buf[] = new byte[1024];

				while ((len = in.read(buf, 0, 1024)) != -1) {
					fout.write(buf, 0, len);

				}
				fout.close();

				filename = tempFile.getName();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			PubFunc.closeResource(fout);			
			PubFunc.closeResource(in);
			PubFunc.closeResource(rowSet);
		}
		return filename;
	}

	private String mergeGrid="";
	/**
	 * 取得表头最底端的列 && 求得表底是否有页小计和累计 && 哪些列有页小计或累计 &&得到表体的宽度（单位：像素） &&表头的所有列集合
	 * &&表头的高度 &&
	 * 
	 * @param tabid
	 *            高级花名册的id
	 * @return ArrayList
	 */
	public ArrayList getBottomnList(HmusterViewBo hmusterViewBo, String tabid)
			throws GeneralException {

		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql = new StringBuffer("select c.GridNo,c.Hz,c.Rleft,c.RTop,");
		sql.append("c.RWidth,c.RHeight,c.Slope,c.Flag,c.noperation,c.Field_Type,c.fontName,");
		sql.append("c.fontEffect,c.fontSize,c.align ");
		sql.append(" ,n.TMargin,n.BMargin,n.RMargin,n.LMargin,n.PaperOri,n.PaperW,n.PaperH,");
		sql.append("c.L,c.T,c.R,c.B,c.Field_name,c.SetName,c.nhide ");
		sql.append(",ExtendAttr from muster_cell c,muster_name n where n.tabid=c.tabid and c.tabid=");
		sql.append(tabid);
		sql.append(" order by c.RTop,c.Rleft ");
		
		//wangcq 2014-11-20 begin 查询临时表字段类型
		StringBuffer sql_muster = new StringBuffer("");
		sql_muster.append("select * from ");
		sql_muster.append(this.userView.getUserName());
		sql_muster.append("_Muster_");
		sql_muster.append(tabid);
		sql_muster.append(" where 1=2");
		//wangcq 2014-11-20 end
		ArrayList columnList = new ArrayList();
		ArrayList tempList = new ArrayList();
		float tableBodyWidth = 0; // 表体的宽度
		float bodyHeight = 0; // 表总高
		float[] ltPix = { 0, 0 }; // 表的左坐标及顶坐标
		ArrayList colColumnsList = new ArrayList();
		String yxj = "0"; // 页小计
		String ylj = "0"; // 页累计
		float lMargin = 0;
		boolean isMargin=true;
		float baseLeft=0f;

		int nOperation = 0;
		try {
			rowSet = dao.search(sql_muster.toString());
			ResultSetMetaData data=rowSet.getMetaData();
			/* 将标头放入list中 */
			rowSet = dao.search(sql.toString());
			int aa = 0;
			float aRleft2 = 0;
			float aRleft = 0;
			while (rowSet.next()) {
				String[] temp = new String[29];			
				/* #### 所有花名册都往左靠 ##### */
				if (aa == 0) {
					float tt = rowSet.getFloat(3);
					lMargin =Float.parseFloat(PubFunc.round(String.valueOf(rowSet.getFloat(18) / precent),0));
					lt = tt - lMargin;
					if(lt<0)
					{
						lt=0;
						isMargin=false;
						baseLeft=tt;
					}
					
				}
				aa++;

				/* 表的左坐标及顶坐标 */
				if (ltPix[0] == 0) {
					ltPix[0] = rowSet.getFloat(3) - lt;
					ltPix[1] = rowSet.getFloat(4);
				}
				/* ######### end ####### */

				String fieldName = rowSet.getString("Field_name");
				String setName = rowSet.getString("SetName");
				String extendAttr = rowSet.getString("ExtendAttr");
				extendAttr=extendAttr!=null?extendAttr:"";
				String ColMerge = "false"; //是否合并
				String ColMergeByMain = "false"; //是否按人员,单位,职位合并
				if(extendAttr.indexOf("<ColMerge>")!=-1){
					ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),
							extendAttr.indexOf("</ColMerge>"));
				}
				if(extendAttr.indexOf("<ColMergeByMain>")!=-1){
					ColMergeByMain = extendAttr.substring(extendAttr.indexOf("<ColMergeByMain>")
							+"<ColMergeByMain>".length(),
							extendAttr.indexOf("</ColMergeByMain>"));
				}
				String RowMerge="false";
				if(extendAttr.indexOf("<RowMerge>")!=-1){
					RowMerge = extendAttr.substring(extendAttr.indexOf("<RowMerge>")+"<RowMerge>".length(),extendAttr.indexOf("</RowMerge>"));
				}
				itemHeArr +=fieldName+":"+ColMerge+":"+ColMergeByMain+",";
				mergeGrid += rowSet.getString("GridNo")+":"+ColMerge+":"+ColMergeByMain+":"+RowMerge+",";
				if(fieldName==null) {
                    fieldName="";
                }
				FieldItem item =null;
				if(!"5".equals(this.modelFlag))			//如果不是工资变动模块
                {
                    item=DataDictionary.getFieldItem(fieldName.toLowerCase());
                } else{
					if(fieldName.indexOf("_")>-1){
						item=DataDictionary.getFieldItem(fieldName.substring(0,fieldName.indexOf("_")).toLowerCase());
					}else {
						item=DataDictionary.getFieldItem(fieldName);
					}
				}
				boolean print = false;
				for (int i = 0; i < 27; i++) {
					if (i == 2) {
						temp[i] = String.valueOf(rowSet.getFloat(i + 1) - lt);
					} else if (i == 7) {
						if(rowSet.getString(i + 1)==null)
						{
							temp[i] = "H";
						}
						else
						{
							if ("A".equals(rowSet.getString(i + 1))|| "B".equals(rowSet.getString(i + 1))|| "K".equals(rowSet.getString(i + 1))) {
								if("salary".equalsIgnoreCase(this.modelFlag)){
									temp[i] = rowSet.getString(i + 1);
								}else{ 
									if (item == null) {
										if(fieldName.toLowerCase().startsWith("yk")) {
											temp[i] = "A";
										}else {
											temp[i] = "H";
										}
									}else if ("0".equals(item.getUseflag())) {
                                        temp[i] = "H";
                                    } else {
										temp[i] = rowSet.getString(i + 1);
									}
								}
							}else {
                                temp[i] = rowSet.getString(i + 1);
                            }
						}

					} else if (i == 9) {
						if(rowSet.getString(i + 1)!=null&&!"".equals(rowSet.getString(i + 1))) {
                            temp[i] = rowSet.getString(i + 1);
                        } else if(item != null && !"0".equals(item.getUseflag())) {
                            temp[i] = item.getItemtype();
                        } else {
                            temp[i] = "A";
                        }
					}else if (i == 6) {
						if (item!=null&&item.getItemid().equalsIgnoreCase(setName+"z1")&&!"81".equals(this.modelFlag)) {
                            temp[i] = "0";
                        } else {
                            temp[i] = rowSet.getString(i + 1);
                        }
					} else {
						temp[i] = rowSet.getString(i + 1);
					}
					if(temp[i]==null) {
                        temp[i]="";
                    }
				}
				
				if(!"M".equals(temp[9]) && HmusterViewBo.isTextType("C"+temp[0], data)) //wangcq 2014-11-20 花名册定位查询多条数据时，相应数据类型改变，此时temp[9]的类型也需改变
                {
                    temp[9] = "M";
                }
				
				temp[27] = rowSet.getString(28);
				
				if(temp[27]!=null&& "1".equals(temp[27])){
					if(columnList.size()>0){
						String[] arr = (String[])columnList.get(columnList.size()-1);
						if(arr!=null&&arr.length==28){
							arr[23]="1";
							columnList.remove(columnList.size()-1);
							columnList.add(arr);
						}
					}else {
                        print = false;
                    }
				}
				temp[28]=extendAttr;
				columnList.add(temp);

				if ("N".equals(temp[9])|| "R".equals(temp[7])) {
					if (!"0".equals(temp[8])) {
					/*	if (temp[8].equals("1"))
							yxj = "1";
						else if (temp[8].equals("2"))
							ylj = "1";
						else if (temp[8].equals("3")) {
							yxj = "1";
							ylj = "1";
						}*/

						String[] a = new String[2];
						a[0] = "C" + temp[0];
						a[1] = temp[8];
						colColumnsList.add(a);
					}
				}

			}

			tempList = getBottomColumnList(columnList, tabid, lMargin,isMargin,baseLeft);
			ArrayList bottomList=(ArrayList)tempList.get(0);
			for(int i=0;i<bottomList.size();i++)
			{
				String[] temp=(String[])bottomList.get(i);
				if ("N".equals(temp[9])|| "R".equals(temp[7])) {
					if (!"0".equals(temp[8])) {
						if ("1".equals(temp[8])) {
							yxj = "1";
						}else if ("2".equals(temp[8])) {
							ylj = "1";
						}else if ("3".equals(temp[8])) {
							yxj = "1";
							ylj = "1";
						}else if("11".equals(temp[8])) {
							yxj = "1";
							ylj = "1";
						}else if("15".equals(temp[8])) {
							yxj = "1";
							ylj = "1";
						}
						
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		if ("1".equals(yxj) && "1".equals(ylj)) {
            nOperation = 3;
        } else if ("1".equals(yxj) && "0".equals(ylj)) {
            nOperation = 1;
        } else if ("0".equals(yxj) && "1".equals(ylj)) {
            nOperation = 2;
        }

		list.add((ArrayList) tempList.get(0));
		list.add(new Integer(nOperation));
		list.add((ArrayList) tempList.get(1));
		list.add((Float) tempList.get(3));
		list.add(columnList);
		list.add((Float) tempList.get(2));
		list.add(ltPix);
		list.add(colColumnsList);
		list.add((Float) tempList.get(4));
		return list;

	}

	/**
	 * 获得表头的最底端列集合 && 装有页小计或页累计的列(String[] 0:列名 1:汇总标示 )
	 * 
	 * @param columnList
	 *            表头列集合
	 * @return
	 */

	public ArrayList getBottomColumnList(ArrayList columnList, String tabid,
			float lMargin,boolean isMargin,float baseLeft) {
		ArrayList list = new ArrayList();
		ArrayList bottomColumnNo = new ArrayList();
		ArrayList columnsList = new ArrayList(); // 装有页小计或页累计的列(String[] 0:列名
		// 1:汇总标示 )
		float t_width = 0;
		float t_height = 0; 
		float r_left = 0;
		float r_bottomn = 0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select max(rleft+rwidth)-min(rleft) t_width,max(rtop+rheight)-min(rtop) t_height,min(rleft)-"
							+ lt
							+ " r_left,max(rtop+rheight) r_bottomn   from muster_cell where tabid="
							+ tabid);
			if (rowSet.next()) {
				t_width = rowSet.getFloat("t_width");
				t_height = rowSet.getFloat("t_height");
				r_left = rowSet.getFloat("r_left");
				r_bottomn = rowSet.getFloat("r_bottomn");
			}
            if(this.getHmusterViewBo()!=null&&this.getHmusterViewBo().getTextFormatMap().size()>0)
            {
            	
            	t_height=(float)(t_height-this.getHmusterViewBo().getTextDataHeight());
            }
			float temp_width = 0;
			float temp_left =0f;
			if(isMargin) {
                temp_left=lMargin;
            } else {
                temp_left=baseLeft;
            }
			
			while (temp_width < t_width) {

				for (int i = 0; i < columnList.size(); i++) {
					String[] a_temp = (String[]) columnList.get(i);
					if (temp_left == Float.parseFloat(a_temp[2])&& (Float.parseFloat(a_temp[3]) + Float.parseFloat(a_temp[5])) == r_bottomn) {
						bottomColumnNo.add(a_temp);
						if ("N".equals(a_temp[9])|| "R".equals(a_temp[7])) {
							if (!"0".equals(a_temp[8])) {
								String[] a = new String[2];
								a[0] = "C" + a_temp[0];
								a[1] = a_temp[8];
								columnsList.add(a);
							}
						}
						temp_width += Float.parseFloat(a_temp[4]);
						temp_left += Float.parseFloat(a_temp[4]);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		list.add(bottomColumnNo);
		list.add(columnsList);
		list.add(new Float(t_height));
		list.add(new Float(t_width));
		list.add(new Float(r_bottomn));
		return list;
	}

	/**
	 * 获得表头的最底端列集合 && 装有页小计或页累计的列(String[] 0:列名 1:汇总标示 )
	 * @param columnList
	 *            表头列集合
	 * @return
	 */

	public ArrayList getBottomColumnList2(ArrayList columnList) {
		ArrayList list = new ArrayList();
		ArrayList bottomColumnNo = new ArrayList();
		ArrayList columnsList = new ArrayList(); // 装有页小计或页累计的列(String[] 0:列名
		// 1:汇总标示 )

		for (int i = 0; i < columnList.size(); i++) {
			String[] temp = (String[]) columnList.get(i);
			int isRow = 0;
			for (int a = i + 1; a < columnList.size(); a++) {
				String[] temp_next = (String[]) columnList.get(a);
				if (temp_next[2].equals(temp[2])) {
					isRow = a;
				} else {
                    break;
                }
			}

			if (isRow == 0) {
				bottomColumnNo.add(temp); // 如果该列没有分行，则直接写入
				float aWidth = Float.parseFloat(temp[4]);

				if ("N".equals(temp[9])) {
					if (!"0".equals(temp[8])) {
						String[] a = new String[2];
						a[0] = "C" + temp[0];
						a[1] = temp[8];
						columnsList.add(a);

					}
				}
			} else {
				String[] temp_next = (String[]) columnList.get(isRow);
				bottomColumnNo.add(temp_next); // 如果该列有分行，则取最底行的第一列

				if (isRow == columnList.size() - 1) {
                    i = isRow;
                }

				float aWidth = Float.parseFloat(temp_next[4]);

				if ("N".equals(temp_next[9])) {
					if (!"0".equals(temp_next[8])) {
						String[] a = new String[2];
						a[0] = "C" + temp_next[0];
						a[1] = temp_next[8];
						columnsList.add(a);

					}
				}

				for (int a = isRow; a < columnList.size() - 1; a++) {
					String[] columns = (String[]) columnList.get(a);
					String[] columns2 = (String[]) columnList.get(a + 1);

					float rleft = Float.parseFloat(columns[2]);
					float rwidth = Float.parseFloat(columns[4]);
					float rtop = Float.parseFloat(columns[3]);

					float rleft2 = Float.parseFloat(columns2[2]);
					float rwidth2 = Float.parseFloat(columns2[4]);
					float rtop2 = Float.parseFloat(columns2[3]);

					if ((rleft + rwidth == rleft2) && rtop == rtop2) {

						bottomColumnNo.add(columns2);
						float width = Float.parseFloat(columns2[4]);

						if ("N".equals(columns2[9])) {
							if (!"0".equals(columns2[8])) {
								String[] b = new String[2];
								b[0] = "C" + columns2[0];
								b[1] = columns2[8];
								columnsList.add(b);

							}
						}
						i = a + 1;
					} else {
						i = a;
						break;
					}
				}
			}
		}
		list.add(bottomColumnNo);
		list.add(columnsList);
		return list;
	}

	/**
	 * 删除临时文件夹中的图片文件
	 * 
	 * @param list
	 *            文件名-列表
	 * 
	 */
	public void deletePicture(ArrayList list) {

		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String filename = (String) iter.next();
			File file = null;
			file = new File(System.getProperty("java.io.tmpdir"), filename);
			if (file.exists()) {
                file.delete();
            }
		}
		return;
	}
	
	
	
	
	
	/**
	 * 获得表体的高度(单位:像素)
	 * 
	 * @param tabid
	 *            花名册id
	 * @param tableHeaderTop
	 *            表头最顶端的位置
	 * @param tableHeaderHeight
	 *            表头的高
	 * @author dengc
	 * @return ArrayList
	 */

	public ArrayList getTableBodyHeight(String tabid, float tableHeaderTop,
			float tableHeaderHeight, float alt, String column, HmusterViewBo vb)
			throws GeneralException {

		StringBuffer sql = new StringBuffer();
		sql.append("select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,GridNo,Hz,RLeft");
		sql.append(",rWidth,RTop,rHeight,muster_title.FontSize,muster_title.FontName");
		sql.append(",muster_title.FontEffect,muster_title.Flag,muster_name.paperOri");
		sql.append(",muster_title.extendattr  from muster_title ,muster_name");
		sql.append(" where muster_name.tabid=muster_title.tabid and muster_name.tabid=");
		sql.append(tabid);
		ArrayList list = new ArrayList(); // 所有的标题信息列表
		double pageTotalPix = 0; // 页面总象素
		double tableBodyPix = 0; // 表体象素
		ArrayList titleList = new ArrayList();	
		ContentDAO dao = new ContentDAO(this.conn);
		int titleTop = 0; // 标题最上端的位置
		RowSet rowSet = null;
		try {
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				String[] temp = new String[19];
				temp[0] = rowSet.getString("TMargin")!=null?rowSet.getString("TMargin"):"";
				temp[1] = rowSet.getString("BMargin")!=null?rowSet.getString("BMargin"):"";
				temp[2] = rowSet.getString("RMargin")!=null?rowSet.getString("RMargin"):"";
				temp[3] = rowSet.getString("LMargin")!=null?rowSet.getString("LMargin"):"";
				temp[4] = rowSet.getString("Paper")!=null?rowSet.getString("Paper"):"";
				temp[5] = rowSet.getString("PaperW")!=null?rowSet.getString("PaperW"):"";
				temp[6] = rowSet.getString("PaperH")!=null?rowSet.getString("PaperH"):"";
				temp[7] = rowSet.getString("GridNo")!=null?rowSet.getString("GridNo"):"";
				temp[8] = rowSet.getString("Hz")!=null?rowSet.getString("Hz"):"";
				temp[9] = String.valueOf(rowSet.getFloat("RLeft") - alt);
				temp[10] = rowSet.getString("rWidth")!=null?rowSet.getString("rWidth"):"";
				String Rtop = rowSet.getString("RTop")!=null?rowSet.getString("RTop"):"";
				Rtop=Rtop!=null&&Rtop.trim().length()>0?Rtop:"0";
				float tops = Float.parseFloat(Rtop);
				if(tops>1000){
					if(tops>10000) {
                        tops=tops/10000;
                    } else {
                        tops=tops/1000;
                    }
					Rtop = String.valueOf(tops);
					Rtop = Rtop.substring(0,Rtop.indexOf("."));
				}
				temp[11] = Rtop;
				temp[12] = rowSet.getString("rHeight")!=null?rowSet.getString("rHeight"):"";
				temp[13] = rowSet.getString("FontSize")!=null?rowSet.getString("FontSize"):"";
				temp[14] = rowSet.getString("FontName")!=null?rowSet.getString("FontName"):"";
				temp[15] = rowSet.getString("FontEffect")!=null?rowSet.getString("FontEffect"):"";
				temp[16] = rowSet.getString("Flag")!=null?rowSet.getString("Flag"):"";
				temp[17] = rowSet.getString("paperOri")!=null?rowSet.getString("paperOri"):"";
				temp[18] = rowSet.getString("extendattr")!=null?rowSet.getString("extendattr"):"";
				if(!("0".equals(temp[10])&&"0".equals(temp[16])))//25283  插入标题为文字类型并且宽度为0的为脏数据不加入标题  changxy 20170223
                {
                    titleList.add(temp);
                }
				
				if (titleTop == 0) {
                    titleTop = Integer.parseInt(temp[11]);
                } else {
					if (Integer.parseInt(temp[11]) > titleTop) {
                        titleTop = Integer.parseInt(temp[11]);
                    }
				}
				
			}

			if (titleList.size() > 0) {
				String[] temp = (String[]) titleList.get(0);
				if ("0".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[5])
                        / precent- (Double.parseDouble(temp[0])+Double.parseDouble(temp[1]))
                        / precent
                        -(tableHeaderTop + tableHeaderHeight);
                    } else
					{					
						tableBodyPix = Double.parseDouble(temp[6])
						/ precent
						- (Double.parseDouble(temp[0])+Double.parseDouble(temp[1]))/ precent
						-(tableHeaderTop + tableHeaderHeight);
					
					
					}
				} else if ("1".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent-  Double.parseDouble(temp[1]) / precent-tableHeaderTop;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent- Double.parseDouble(temp[1])/ precent-tableHeaderTop;
                    }
					
				} else if ("2".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) /precent;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) / precent;
                    }
				}

			} else // 如果没有标题
			{
				String sql2 = "select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,paperOri  from muster_name  where tabid="
						+ tabid;
				RowSet rowSet2 = dao.search(sql2);
				if (rowSet2.next()) {
					if ("2".equals(column)) {
						if (rowSet2.getInt("paperOri") == 2) {
                            tableBodyPix = rowSet2.getFloat("PaperW")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent-40;
                        } else {
                            tableBodyPix = rowSet2.getFloat("PaperH")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent-40;
                        }
					} else {
						if (rowSet2.getInt("paperOri") == 2) // 横向
						{
							tableBodyPix = rowSet2.getDouble("PaperW")
									/ precent
									- (rowSet2.getFloat("RMargin") + rowSet2
											.getFloat("LMargin")) / precent-(tableHeaderTop + tableHeaderHeight)-40;
						} else {
							tableBodyPix = rowSet2.getDouble("PaperH")
									/ precent
									- (rowSet2.getFloat("RMargin") + rowSet2
											.getFloat("LMargin")) / precent- (tableHeaderTop + tableHeaderHeight)-40;
						}
					}
				}
			}
			list.add(titleList);
			list.add(vb.round(String.valueOf(tableBodyPix), 0));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 获得表体的高度(单位:像素)
	 * 
	 * @param tabid
	 *            花名册id
	 * @param tableHeaderTop
	 *            表头最顶端的位置
	 * @param tableHeaderHeight
	 *            表头的高
	 * @author dengc
	 * @return ArrayList
	 */

	public ArrayList getTableBodyHeight2(String tabid, float tableHeaderTop,
			float tableHeaderHeight, float alt, String column, HmusterViewBo vb)
			throws GeneralException {

		String sql = "select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,GridNo,Hz,RLeft,rWidth,RTop,rHeight,muster_title.FontSize,muster_title.FontName,muster_title.FontEffect,muster_title.Flag,muster_name.paperOri  from muster_title ,muster_name where muster_name.tabid=muster_title.tabid and muster_name.tabid="
				+ tabid;
		ArrayList list = new ArrayList(); // 所有的标题信息列表
		double pageTotalPix = 0; // 页面总象素
		double tableBodyPix = 0; // 表体象素
		ArrayList titleList = new ArrayList();
		int titleTop = 0; // 标题最上端的位置
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				String[] temp = new String[18];
				temp[0] = rowSet.getString("TMargin");
				temp[1] = rowSet.getString("BMargin");
				temp[2] = rowSet.getString("RMargin");
				temp[3] = rowSet.getString("LMargin");
				temp[4] = rowSet.getString("Paper");
				temp[5] = rowSet.getString("PaperW");
				temp[6] = rowSet.getString("PaperH");
				temp[7] = rowSet.getString("GridNo");
				temp[8] = rowSet.getString("Hz");
				temp[9] = String.valueOf(rowSet.getFloat("RLeft") - alt);
				temp[10] = rowSet.getString("rWidth");
				temp[11] = rowSet.getString("RTop");
				temp[12] = rowSet.getString("rHeight");
				temp[13] = rowSet.getString("FontSize");
				temp[14] = rowSet.getString("FontName");
				temp[15] = rowSet.getString("FontEffect");
				temp[16] = rowSet.getString("Flag");
				temp[17] = rowSet.getString("paperOri");

				titleList.add(temp);
				if (titleTop == 0) {
                    titleTop = Integer.parseInt(temp[11]);
                } else {
					if (Integer.parseInt(temp[11]) < titleTop) {
                        titleTop = Integer.parseInt(temp[11]);
                    }
				}
			}

			if (titleList.size() > 0) {
				String[] temp = (String[]) titleList.get(0);
				if ("0".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[5])
                        / precent- Double.parseDouble(temp[1])
                        / precent
                        -(tableHeaderTop + tableHeaderHeight);
                    } else
					{					
						tableBodyPix = Double.parseDouble(temp[6])
						/ precent
						- Double.parseDouble(temp[1])/ precent
						-(tableHeaderTop + tableHeaderHeight);
					
					
					}
				} else if ("1".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent
                                - (Double.parseDouble(temp[0]) + Double
                                        .parseDouble(temp[1])) / precent;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent
                                - (Double.parseDouble(temp[0]) + Double
                                        .parseDouble(temp[1])) / precent;
                    }
				} else if ("2".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) /precent;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) / precent;
                    }
				}

			} else // 如果没有标题
			{
				String sql2 = "select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,paperOri  from muster_name  where tabid="
						+ tabid;
				RowSet rowSet2 = dao.search(sql2);
				if (rowSet2.next()) {
					if ("2".equals(column)) {
						if (rowSet2.getInt("paperOri") == 2) {
                            tableBodyPix = rowSet2.getFloat("PaperH")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent;
                        } else {
                            tableBodyPix = rowSet2.getFloat("PaperW")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent;
                        }
					} else {
						if (rowSet2.getInt("paperOri") == 2) // 横向
						{
							tableBodyPix = rowSet2.getDouble("PaperW")
									/ precent
									- (rowSet2.getDouble("TMargin") + rowSet2
											.getDouble("BMargin")) / precent
									- tableHeaderHeight;
						} else {
							tableBodyPix = rowSet2.getDouble("PaperH")
									/ precent
									- (rowSet2.getDouble("TMargin") + rowSet2
											.getDouble("BMargin")) / precent
									- tableHeaderHeight;
						}
					}
				}
			}
			list.add(titleList);
			list.add(vb.round(String.valueOf(tableBodyPix), 0));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	public static void main(String[] args) {
		Document document = new Document();
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream("c:\\"+"HelloWorld.pdf");
			PdfWriter.getInstance(document,fs);
			document.open();
			BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
			Font font=new Font(bfComic,5, Font.BOLD);
			Paragraph paragraph =  new Paragraph("gffjgf付发丝fgsgfgs",font);			
			document.add(paragraph);
			
			
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}finally {
        	PubFunc.closeIoResource(fs);
		}
		document.close();
	}
	public String getCreateTablePerson(String userName,String xml)
	{
		String str="";
		try
		{
			String ss=userName;
			String prefix="";
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			str=prefix+ss;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getCreateTableDate(String xml)
	{
		String str="";
		try
		{
			GregorianCalendar d = new GregorianCalendar();//制表日期
			String dateStr= d.get(Calendar.YEAR)+ "."+ (d.get(Calendar.MONTH)+1)+ "."+ d.get(Calendar.DATE);
			String prefix="";
			
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			if(xml.indexOf("<format>")!=-1)
			{
				String format=xml.substring(xml.indexOf("<format>")+8, xml.indexOf("</format>"));
				/* 0: 1991.12.3
			      1: 1990.01.01
			      2: 1990年2月10日
			      3: 1990年01月01日
			      4: 1991-12-3
			      5: 1990-01-01*/
                int year=d.get(Calendar.YEAR);
                int month=d.get(Calendar.MONTH)+1;
                int day=d.get(Calendar.DATE);
				if(format.length()>0)
				{
					if("0".equals(format))
					{
						dateStr=year+"."+month+"."+day;
					}
					else if("1".equals(format))
					{
						dateStr=year+"."+((month>10)?month+"":"0"+month)+"."+((day>=10)?day+"":"0"+day);
					}
					else if("2".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+month+ResourceFactory.getProperty("hmuster.label.month")+day+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("3".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+((month>10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month")+((day>=10)?day+"":"0"+day)+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("4".equals(format))
					{
						dateStr=year+"-"+month+"-"+day;
					}
					else if("5".equals(format))
					{
						dateStr=year+"-"+((month>10)?month+"":"0"+month)+"-"+((day>=10)?day+"":"0"+day);
					}
                    else if("6".equals(format))
                    {
                        dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+((month>=10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month");
                    }
				}
			}
			str=prefix+dateStr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	private int resourceCloumn=-1;

	/**
	 * 判断总计，小计，累计等在哪个格显示
	 * resourceCloumn=-1默认，没有位置显示
	 * 先在序号位置显示
	 * 如果没有序号有分组指标，在分组指标显示
	 * 如果没有分组指标，如果为人员花名册时，在姓名位置显示，如果为机构花名册，在机构名称位置显示，如果是职位花名册，在职位名称位置显示
	 * 如果以上都没有，人员的找‘姓名’，机构的找‘部门名称’或者‘单位名称’，职位的，找岗位名称
	 * 以上都没有找第一个，并且这个要不是数值型的
	 * @param tabid
	 * @return
	 */
	public void getResourceCloumn(String tabid,String info_flag)
	{
		HmusterViewBo hmusterViewBo=new HmusterViewBo(conn,tabid);
		resourceCloumn=hmusterViewBo.getResourceCloumn(tabid, info_flag);
/*		try
		{
			boolean flag=true;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =null;
			String sql = "select * from muster_cell where tabid="+tabid+" and UPPER(flag)='S'";
			rs=dao.search(sql);
			//是否有序号指标
			while(rs.next())
			{
				this.resourceCloumn=rs.getInt("GridNo");
				flag=false;
				break;
			}
			//是否有分组指标
			if(flag)
			{
				sql="select * from muster_cell where tabid="+tabid+" and UPPER(flag)='G'";
				rs=dao.search(sql);
				while(rs.next())
				{
					this.resourceCloumn=rs.getInt("GridNo");
					flag=false;
					break;
				}
			}
			if(flag)
			{
				if(info_flag.equals("1"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='A0101'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if(info_flag.equals("2"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_name)='B0110' or UPPER(field_name)='E0122')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if(info_flag.equals("3"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='E01A1'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
			}
			if(flag)
			{
				if(info_flag.equals("1"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_hz)='姓名'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if(info_flag.equals("2"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_hz)='部门名称' or UPPER(field_hz)='单位名称')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if(info_flag.equals("3"))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='岗位名称'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
			}
			if(flag)
			{
				this.resourceCloumn=-2;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
		
	}
	public String parseSelectPoint(String tocope,String fromCope)
	  {
		  String temp="";
		  try
		  {
			  StringBuffer ss=new StringBuffer("");
			  if(fromCope!=null&&fromCope.length()>0&&tocope!=null&&tocope.length()>0)
			  {
				  ss.append(fromCope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(fromCope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
				  ss.append("~");
				  ss.append(tocope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(tocope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }else if(fromCope!=null&&fromCope.length()>0)
			  {
				  ss.append(fromCope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(fromCope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }else if(tocope!=null&&tocope.length()>0)
			  {
				  ss.append(tocope.substring(0,4));
				  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
				  ss.append(tocope.substring(5,7));
				  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  }
			  temp=ss.toString();
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return temp;
	  }
	public byte[] getImageByte(String tabid,String gridno) throws Exception {
		byte[] buf = null;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select extendattr,content from muster_title where tabid="+tabid+" and gridno="+gridno);
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				buf=rowSet.getBytes("content");				
		
		
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(buf==null) {
            buf=new byte[0];
        }
		return buf;
	}
	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getIsGroupNoPage() {
		return isGroupNoPage;
	}

	public void setIsGroupNoPage(String isGroupNoPage) {
		this.isGroupNoPage = isGroupNoPage;
	}

	public String getGroupPoint() {
		return groupPoint;
	}

	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}

	public String getDataarea() {
		return dataarea;
	}

	public void setDataarea(String dataarea) {
		this.dataarea = dataarea;
	}

	public String getGroupNcode() {
		return groupNcode;
	}

	public void setGroupNcode(String groupNcode) {
		this.groupNcode = groupNcode;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public HmusterViewBo getHmusterViewBo() {
		return hmusterViewBo;
	}

	public void setHmusterViewBo(HmusterViewBo hmusterViewBo) {
		this.hmusterViewBo = hmusterViewBo;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}

	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}

	public String getGroupPoint2() {
		return groupPoint2;
	}

	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}

	public boolean isGroupV2() {
		return isGroupV2;
	}

	public void setGroupV2(boolean isGroupV2) {
		this.isGroupV2 = isGroupV2;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public String getYearmonth() {
		return yearmonth;
	}

	public void setYearmonth(String yearmonth) {
		this.yearmonth = yearmonth;
	}

}
