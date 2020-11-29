package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableHtmlBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

/**
 *<p>Title:ReportExcelBo.java</p> 
 *<p>Description:生成excel报表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 28, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
/**
 * @author Administrator
 *
 */
public class ReportExcelBo {
	private HSSFWorkbook wb = null;
	private HSSFSheet sheet = null;
	private HSSFCellStyle style = null;
	private HSSFCellStyle style_l = null;
	private HSSFCellStyle style_r = null;
	private HSSFCellStyle style_cc = null;

	private HSSFCellStyle styleN = null;
	private HSSFCellStyle styleF1 = null;
	private HSSFCellStyle styleF2 = null;
	private HSSFCellStyle styleF3 = null;
	private HSSFCellStyle styleF4 = null;
	private HSSFCellStyle styleF5 = null;

	private HSSFRow row = null;
	private HSSFCell cell = null;

	private Connection conn;// DB连接
	private String operateObject = "1"; // 表对象 1：编辑没上报表 2：编辑上报后的表 3:归档表 4:综合表 5:反查表

	/** 反查涉及到的数据 */
	private String reverseSql = ""; // 反查sql
	private String setMap_str = "";
	private String fieldItem_str = "";
	private String scanMode = "";

	private String tabid = "";
	private String itemName = ""; // 项目名称
	private String itemFontName = ""; // 项目字体
	private String itemSize = ""; // 项目大小
	private String itemAlign = ""; // 项目位置
	private int itemEffect = 0; // 项目样式
	private String unitcode = ""; // 填报单位
	private UserView userView = null;
	private int rowLayNum = 1;
	private int colLayNum = 1;
	private String username = null;

	private String yearid = ""; // 归档年份
	private String countid = ""; // 归档次数
	private String narch = ""; // 归档类型
	private String weekid = "";

	private HashMap row_l_map = new HashMap();
	private HashMap row_t_map = new HashMap();
	private HashMap col_l_map = new HashMap();
	private HashMap col_t_map = new HashMap();
	private int tab_width = 0; // 表宽度 （像素）
	private int tab_height = 0; // 表高度 （像素）
	private int index_x = 0;
	private int index_y = 0;

	private RecordVo tnameVo = null;
	private double pageWidth = 0; // 页宽度（像素）
	private double pageHeight = 0; // 页高度（像素）
	public static float scale = 0.27f;

	private ArrayList resultList = new ArrayList(); // 表结果数据
	private ArrayList gridList = new ArrayList();
	private ArrayList rowInfoList = new ArrayList();
	private ArrayList colInfoList = new ArrayList();
	private ArrayList rowInfoBGrid = new ArrayList(); // 横表栏底层单元格列表（按顺序排列）
	private ArrayList colInfoBGrid = new ArrayList(); // 纵表栏底层单元格列表（按顺序排列）
	private int[] itemGridArea = new int[4]; // 表项目区域 l,t,w,h
	private HashMap rowMap = new HashMap();
	private HashMap colMap = new HashMap();
	private ArrayList pageList = null;
	private HashMap paramMap = null;
	private ArrayList topPageList = null; // 上标题
	private ArrayList bottomPageList = null; // 下标题
	private ArrayList allPageList = null; // 所有标题
	private HashMap allParamMap = new HashMap(); // 所有参数值
	private int top_pix = 0;
	private int bottom_pix = 0;
	private int topParamLayNum = 0; // 表头 标题层数
	private HashMap topLayMap = new HashMap();
	private int bottomParamLayNum = 0; // 表尾 标题层数
	private HashMap botLayMap = new HashMap();

	private ArrayList rowHeightList = new ArrayList();
	private ArrayList columnWidthList = new ArrayList();

	// 综合表参数
	private String[] condition = null;
	private String nums = "";
	private int constantNum = 0;
	private ContentDAO dao;
	
	public ReportExcelBo(UserView a_userview, String unitcode, String operate, Connection con) {
		this.conn = con;
		this.operateObject = operate;
		this.unitcode = unitcode;
		this.userView = a_userview;
		this.dao = new ContentDAO(con);
	}

	// 生成综合表excel的构造方法
	public ReportExcelBo(UserView a_userview, String tabid, String unitcode, String operate, Connection con,
			String nums, ArrayList resultList, String[] condition) {
		this(a_userview, unitcode, operate, con);
		this.tabid = tabid;
		this.nums = nums;
		this.condition = condition;
		this.resultList = resultList;
		init();
	}

	public ReportExcelBo(UserView a_userview, String tabid, String unitcode, String operate, Connection con) {
		this(a_userview, unitcode, operate, con);
		this.tabid = tabid;
		init();
	}

	public ReportExcelBo(UserView a_userview, String username, String tabid, String unitcode, String operate,
			Connection con) {
		this(a_userview, unitcode, operate, con);
		this.tabid = tabid;
		this.username = username;
		init();
	}

	public ReportExcelBo(UserView a_userview, String tabid, String unitcode, String operate, Connection con,
			String yearid, String countid, String weekid) {
		this(a_userview, unitcode, operate, con);
		this.tabid = tabid;
		this.yearid = yearid;
		this.countid = countid;
		this.weekid = weekid;
		init();
	}

	public ReportExcelBo(String username, UserView a_userview, String unitcode, String operate, Connection con) {
		this(a_userview, unitcode, operate, con);
		this.username = username;
	}

	public void init() {
		try {
			TgridBo tgridBo = new TgridBo(this.conn);
			IntegrateTableHtmlBo htmlBo = null;
			ArrayList a_rowInfoBGrid = new ArrayList();
			ArrayList b_colInfoBGrid = new ArrayList();
			constantNum = 0;
			if ("4".equals(this.operateObject)) {
				TnameBo tnameBo = new TnameBo(this.conn, tabid, this.userView.getUserId(), this.userView.getUserName(),
						" ");
				htmlBo = new IntegrateTableHtmlBo();
				htmlBo.setExceflag("1");
				ArrayList list = htmlBo.getNewGridList(tnameBo, nums, condition);
				a_rowInfoBGrid = (ArrayList) list.get(0);
				b_colInfoBGrid = (ArrayList) list.get(1);
				/* 根据选中的列行条件 替换单元格对象 */
				this.gridList = (ArrayList) list.get(2);
				this.itemGridArea = tgridBo.getItemGridID(this.tabid);
				this.top_pix = itemGridArea[1];
				// int[] temp=htmlBo.getItemGridID(tabid,this.conn,this.nums,this.condition);
				// this.bottom_pix=temp[1]+temp[3];
				this.bottom_pix = getbottomPix();// xiegh update on date:20170801 bug:28438
			} else {
				this.gridList = tgridBo.getGridInfoList(tabid);
				// 分析表格,得到报表横表栏和纵表栏的相关信息集合
				this.itemGridArea = tgridBo.getItemGridID(this.tabid);
				this.top_pix = itemGridArea[1];
				this.bottom_pix = getbottomPix();
			}
			// 标题数据初始化
			this.tnameVo = getTnameVoById(this.tabid);
			if (this.tnameVo.getInt("paperori") == 1) { // 横向
				this.pageHeight = this.tnameVo.getDouble("paperw") / scale;
				this.pageWidth = this.tnameVo.getDouble("paperh") / scale;
			} else // 纵向
			{
				this.pageHeight = this.tnameVo.getDouble("paperh") / scale;
				this.pageWidth = this.tnameVo.getDouble("paperw") / scale;
			}

			TpageBo tpageBo = new TpageBo(this.conn);
			ArrayList pageAndParamList = null;
			if ("2".equals(operateObject) || "3".equals(operateObject) || "4".equals(operateObject)) {
                pageAndParamList = tpageBo.getPageListAndTparam2(tabid, this.tnameVo.getInt("tsortid"), unitcode);
            }
			if ("1".equals(operateObject)) {
                pageAndParamList = tpageBo.getPageListAndTparam(this.tabid, this.tnameVo.getInt("tsortid"),
                        this.userView.getUserName());
            }
			if (pageAndParamList != null) {
				this.pageList = (ArrayList) pageAndParamList.get(0);
				this.paramMap = (HashMap) pageAndParamList.get(1);
			}
			if (this.pageList != null) {
				getTabPageList();
			}
			if ("4".equals(this.operateObject)) {
				this.rowInfoList = recordToBean(getIntegrateRow_ColList(1, this.gridList));
				this.colInfoList = recordToBean(getIntegrateRow_ColList(2, this.gridList));
				// 1:横懒 2:纵
				if (nums.indexOf(",a") != -1) // 列
				{
					this.rowInfoBGrid = getIntegrateBottomGridInfoList(a_rowInfoBGrid);
					this.colInfoBGrid = getBottomGridInfoList(2);
				} else {
					this.rowInfoBGrid = getBottomGridInfoList(1);
					this.colInfoBGrid = getIntegrateBottomGridInfoList(b_colInfoBGrid);
				}
				if (nums.indexOf(",a") != -1) // 列
				{
					int r_left = this.itemGridArea[0] + this.itemGridArea[2];
					String atemp = nums.substring(3);
					String[] temps = atemp.split(",");
					int num = 1;
					// int rwidth2=60;
					int rwidth3 = 60;
					// int r_left2 = r_left;
					this.row_l_map.put(String.valueOf(r_left), String.valueOf(num));
					for (int i = 0; i < this.condition.length; i++) {
						for (int j = 0; j < temps.length; j++) {
							String a_condition = (String) condition[i];
							String[] condition_arr = a_condition.split(":");
							String tempname = condition_arr[1];
							String numstemp[] = nums.split(",");
							r_left += rwidth3;
							if (numstemp.length == 2) {// 1列
								if (tempname.length() < 13) {
                                    rwidth3 = 240;
                                } else if (tempname.length() < 20) {
                                    rwidth3 = 300;
                                } else {
                                    rwidth3 = 396;
                                }
							} else if (numstemp.length == 3) {
								if (tempname.length() < 13) {
                                    rwidth3 = 120;
                                } else if (tempname.length() < 20) {
                                    rwidth3 = 150;
                                } else {
                                    rwidth3 = 183;
                                }

							} else if (numstemp.length == 4) {
								if (tempname.length() < 13) {
                                    rwidth3 = 80;
                                } else if (tempname.length() < 20) {
                                    rwidth3 = 100;
                                } else {
                                    rwidth3 = 132;
                                }
							} else if (numstemp.length == 5) {// 4列
								if (tempname.length() < 13) {
                                    rwidth3 = 60;
                                } else if (tempname.length() < 20) {
                                    rwidth3 = 75;
                                } else {
                                    rwidth3 = 99;
                                }
							} else {
								rwidth3 = 60;
							}
							num++;

							String key = String.valueOf(r_left);
							this.row_l_map.put(key, String.valueOf(num));
						}
					}
					int r_top = this.itemGridArea[1];
					this.row_t_map.put(String.valueOf(r_top), "1");
					if (temps.length > 0)// xgq 2010 01 15 修改1为0
					{
						if (this.itemGridArea[3] < 60)// 高度太小，生成的过度表求合并，最大等显示不出来xieguiquan
						{
							this.row_t_map.put(String.valueOf(r_top + 15), "2");
						} else {
							this.row_t_map.put(String.valueOf(r_top + this.itemGridArea[3] / 2 - 15), "2");
						}
					}
					this.col_l_map = getTabNumMap(2, "rleft");
					this.col_t_map = getTabNumMap(2, "rtop");
					this.tab_width = r_left + rwidth3;
				} else {
					this.row_l_map = getTabNumMap(1, "rleft");
					this.row_t_map = getTabNumMap(1, "rtop");
					constantNum = htmlBo.getConstatnNum(condition);
					int r_top = this.itemGridArea[1] + this.itemGridArea[3];
					String atemp = nums.substring(3);
					String[] temps = atemp.split(",");
					int num = 1;
					int constantNum3 = constantNum;
					this.col_t_map.put(String.valueOf(r_top), String.valueOf(num));
					for (int i = 0; i < this.condition.length; i++) {
						for (int j = 0; j < temps.length; j++) {
							r_top += constantNum3;
							String a_condition = (String) condition[i];
							String[] condition_arr = a_condition.split(":");
							if (temps.length == 1) {
								if (condition_arr[1].getBytes().length <= 18) {
									constantNum3 = 60;
								} else if (condition_arr[1].getBytes().length <= 30) {
									constantNum3 = 100;
								} else if (condition_arr[1].getBytes().length <= 42) {
									constantNum3 = 120;
								} else {
									constantNum3 = 120;
								}

							} else if (temps.length == 2) {
								// if(condition_arr[1].getBytes().length<=42&&condition_arr[1].getBytes().length>30){
								// constantNum3=70;
								// }else if(condition_arr[1].getBytes().length>42) {
								// constantNum3=90;
								// }else{
								// constantNum3=60;
								// }
								constantNum3 = 60;
							} else {
								constantNum3 = 60;
							}
							num++;

							String key = String.valueOf(r_top);
							this.col_t_map.put(key, String.valueOf(num));
						}
					}
					int r_left = this.itemGridArea[0];
					this.col_l_map.put(String.valueOf(r_left), "1");
					if (temps.length > 0)// xgq 2010 01 15 修改1为0
                    {
                        this.col_l_map.put(String.valueOf(r_left + this.itemGridArea[2] / 2 - 15), "2");
                    }
					this.tab_height = r_top + constantNum3;

				}
				int[] temp = htmlBo.getItemGridID(tabid, this.conn, this.nums, this.condition);
				if (nums.indexOf(",a") != -1) // 列
				{
					if (this.tab_width == 0) {
                        this.tab_width = temp[0] + temp[2];
                    }
					this.tab_height = getTabArea(2);
				} else {
					this.tab_width = getTabArea(1);
					if (this.tab_height == 0) {
                        this.tab_height = temp[1] + temp[3];
                    }
				}
			} else {
				this.rowInfoList = recordToBean(getRow_ColList(1, this.gridList));
				this.colInfoList = recordToBean(getRow_ColList(2, this.gridList));
				this.rowInfoBGrid = getBottomGridInfoList(1);
				this.colInfoBGrid = getBottomGridInfoList(2);
				this.row_l_map = getTabNumMap(1, "rleft");
				this.row_t_map = getTabNumMap(1, "rtop");
				this.col_l_map = getTabNumMap(2, "rleft");
				this.col_t_map = getTabNumMap(2, "rtop");
				this.tab_width = getTabArea(1);
				this.tab_height = getTabArea(2);
			}
			this.rowLayNum = row_t_map.size();
			this.colLayNum = col_l_map.size();
			resetGridListInfo(this.rowInfoList, 1);
			resetGridListInfo(this.colInfoList, 2);

			ReportResultBo resultBo = new ReportResultBo(this.conn);
			resultBo.setColinfolist(this.colInfoList);
			if ("1".equals(operateObject)) {
                this.resultList = resultBo.getTBxxResultList(tabid, username);
            } else if ("2".equals(operateObject)) {
                this.resultList = resultBo.getTTxxResultList(tabid, unitcode);
            } else if ("3".equals(operateObject)) {
				String sql = "select narch from tname where tabid = " + tabid;
				RowSet rs = dao.search(sql.toString());
				if (rs.next()) {
					narch = rs.getString("narch");
				}
				TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
				TnameBo tnameBo = new TnameBo(this.conn, this.tabid);
				if ("6".equals(narch)) {
                    tnameExtendBo.setWeekid(this.weekid);
                }
				resultList = tnameExtendBo.getReportAnalyseResult(unitcode, yearid, countid, tabid, tnameBo, narch);

			}

			if ("4".equals(this.operateObject)) {
				this.rowHeightList = getIntegrateExcelGridSizeList(1, constantNum);
				this.columnWidthList = getIntegrateExcelGridSizeList(2, constantNum);
			} else {
				this.rowHeightList = getExcelGridSizeList(1);
				this.columnWidthList = getExcelGridSizeList(2);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调整尺寸
	 *
	 */
	public void resetSize(int temp, int temp1) {

		for (int i = 0; i < this.rowHeightList.size(); i++) {

			HSSFRow row = sheet.getRow(i + temp);
			if (row == null) {
                row = sheet.createRow(i + temp);
            }
			int d = 0;
			d = ((Integer) rowHeightList.get(i)).intValue();
			// xiegh 2017/3/22 处理d为负值的情况
			if (d <= 0) {
				d = 300;
			} else {
				d = d * 15;
			}
			row.setHeight(Short.parseShort(String.valueOf(d)));
		}
		for (int i = 0; i < this.columnWidthList.size(); i++) {

			int d = ((Integer) this.columnWidthList.get(i)).intValue();
			// 调整列宽 xiegh 2017/3/22
			d = d * 45;
			this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i + temp1)), Short.parseShort(String.valueOf(d)));

		}
	}

	/**
	 * 取得各单元格 高度 or 宽度
	 * 
	 * @param flag
	 *            1:高度 2：宽度
	 * @return
	 */
	private ArrayList getIntegrateExcelGridSizeList(int flag, int constantNum) {
		ArrayList list = new ArrayList();
		try {
			String sql = "";
			String sql2 = "";
			if (flag == 1) {
				if (nums.indexOf(",a") != -1) // 列
                {
                    sql = "select rtop  from tgrid2 where tabid=" + this.tabid
                            + "  and  flag=2   group  by rtop order by rtop";
                } else {
                    sql = "select rtop  from tgrid2 where tabid=" + this.tabid
                            + "  and  flag=1   group  by rtop order by rtop";
                }
				if (nums.indexOf(",a") != -1) // 列
                {
                    sql2 = "select max(rtop+rheight)  from tgrid2 where tabid=" + this.tabid;
                }
			} else if (flag == 2) {
				if (nums.indexOf(",a") != -1) // 列
                {
                    sql = "select rleft  from tgrid2 where tabid=" + this.tabid
                            + "  and  flag=2    group  by rleft order by rleft";
                } else {
                    sql = "select rleft  from tgrid2 where tabid=" + this.tabid
                            + "  and  flag=1    group  by rleft order by rleft";
                }
				if (nums.indexOf(",b") != -1) // 列
                {
                    sql2 = "select max(rleft+rwidth) from  tgrid2 where tabid=" + this.tabid;
                }
			}
			String value1 = "0";
			String value2 = "0";

			String atemp = nums.substring(3);
			String[] temps = atemp.split(",");
			if (flag == 1) {
				if (nums.indexOf(",a") != -1) // 列
				{
					if (temps.length > 0)// xgq 2010 01 15 修改1为0
					{
						list.add(new Integer(this.itemGridArea[3] / 2 - 15));
						list.add(new Integer(this.itemGridArea[3] / 2 + 15));
					} else {
                        list.add(new Integer(this.itemGridArea[3]));
                    }
					RowSet rowSet = dao.search(sql);
					if (rowSet.next()) {
                        value1 = rowSet.getString(1);
                    }
					while (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
						value1 = value2;
					}
					rowSet = dao.search(sql2);
					if (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
					}
				} else {
					RowSet rowSet = dao.search(sql);
					if (rowSet.next()) {
                        value1 = rowSet.getString(1);
                    }
					while (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
						value1 = value2;
					}
					list.add(new Integer(
							PubFunc.subtract(String.valueOf(this.itemGridArea[1] + this.itemGridArea[3]), value1, 0)));
					list.add(new Integer(constantNum));
					for (int i = 0; i < this.condition.length; i++) {
						for (int j = 0; j < temps.length; j++) {
							list.add(new Integer(constantNum));
						}
					}

				}
			} else if (flag == 2) {
				if (nums.indexOf(",a") != -1) // 列
				{
					RowSet rowSet = dao.search(sql);
					if (rowSet.next()) {
                        value1 = rowSet.getString(1);
                    }
					while (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
						value1 = value2;
					}
					list.add(new Integer(
							PubFunc.subtract(String.valueOf(this.itemGridArea[0] + this.itemGridArea[2]), value1, 0)));
					list.add(new Integer(60));
					for (int i = 0; i < this.condition.length; i++) {
						for (int j = 0; j < temps.length; j++) {
							list.add(new Integer(60));
						}
					}

				} else {
					if (temps.length > 0)// xgq 2010 01 15 修改1为0
					{
						list.add(new Integer(this.itemGridArea[2] / 2 - 15));
						list.add(new Integer(this.itemGridArea[2] / 2 + 15));
					} else {
                        list.add(new Integer(this.itemGridArea[2]));
                    }
					RowSet rowSet = dao.search(sql);
					if (rowSet.next()) {
                        value1 = rowSet.getString(1);
                    }
					while (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
						value1 = value2;
					}
					rowSet = dao.search(sql2);
					if (rowSet.next()) {
						value2 = rowSet.getString(1);
						list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
					}
				}
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 取得各单元格 高度 or 宽度
	 * 
	 * @param flag
	 *            1:高度 2：宽度
	 * @return
	 */
	private ArrayList getExcelGridSizeList(int flag) {
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			String sql = "";
			String sql2 = "";
			boolean isTitle = true;
			// 判断是否只有标题,模板上有四个内容为空的单元格
			rowSet = dao.search("select hz from tgrid2 where tabid=" + this.tabid);
			int index = 0;
			while (rowSet.next()) {
				index++;
				if (index > 4) {
					isTitle = false;
					break;
				}
				if (StringUtils.isNotBlank(rowSet.getString("hz"))) {
                    isTitle = false;
                }
			}
			String sqlUnion = "select rleft,rwidth,tabid  from tgrid2 where tabid=" + this.tabid
					+ "  and ( flag=1 or flag=2 ) ";
			if (isTitle) {
                sqlUnion = "select rleft,rwidth,tabid from tgrid2 where tabid=" + this.tabid
                        + " and ( flag=1 or flag=2 )" + " union all select rleft,rwidth,tabid from tpage where tabid="
                        + this.tabid;
            }
			if (flag == 1) {
				sql = "select rtop  from tgrid2 where tabid=" + this.tabid
						+ "  and ( flag=1 or flag=2 )  group  by rtop order by rtop";
				sql2 = "select max(rtop+rheight) from tgrid2 where tabid=" + this.tabid;
			} else if (flag == 2) {
				sql = "select rleft from (" + sqlUnion + ")t group  by rleft order by rleft";

				sql2 = "select max(rleft+rwidth) from (" + sqlUnion + ")t where tabid=" + this.tabid;
			}
			String value1 = "0";
			String value2 = "0";
			rowSet = dao.search(sql);
			if (rowSet.next()) {
                value1 = rowSet.getString(1);
            }
			while (rowSet.next()) {
				value2 = rowSet.getString(1);
				list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
				value1 = value2;
			}
			PubFunc.closeResource(rowSet);
			rowSet = dao.search(sql2);
			if (rowSet.next()) {
				value2 = rowSet.getString(1);
				list.add(new Integer(PubFunc.subtract(value2, value1, 0)));
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}

		return list;
	}

	/**
	 * 得到表对象
	 * 
	 * @param tabid
	 */
	public RecordVo getTnameVoById(String tabid) {
		RecordVo vo = new RecordVo("tname");
		RowSet recset = null;
		try {
			recset = dao.search("select * from tname where tabid=" + tabid);
			if (recset.next()) {
				vo.setInt("tabid", recset.getInt("tabid"));
				vo.setInt("tsortid", recset.getInt("tsortid"));
				vo.setString("name", recset.getString("name"));
				vo.setDouble("tmargin", recset.getDouble("tmargin"));
				vo.setDouble("bmargin", recset.getDouble("bmargin"));
				vo.setDouble("lmargin", recset.getDouble("lmargin"));
				vo.setDouble("rmargin", recset.getDouble("rmargin"));
				vo.setInt("paper", recset.getInt("paper"));
				vo.setInt("paperori", recset.getInt("paperori"));
				vo.setDouble("paperw", recset.getDouble("paperw"));
				vo.setDouble("paperh", recset.getDouble("paperh"));
				vo.setString("moduleflag", recset.getString("moduleflag"));
				vo.setString("flag1", recset.getString("flag1"));
				vo.setString("flag2", recset.getString("flag2"));
				vo.setString("fontname", recset.getString("fontname"));
				vo.setInt("fonteffect", recset.getInt("fonteffect"));
				vo.setInt("fontsize", recset.getInt("fontsize"));
				vo.setInt("narch", recset.getInt("narch"));
				vo.setString("cbase", recset.getString("cbase"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}
		return vo;
	}

	public int getbottomPix() {
		int temp = 0;
		RowSet rowSet = null;
		try {
			rowSet = dao.search("select rtop+rheight from tgrid3 where tabid=" + this.tabid + " and flag=3 ");
			if (rowSet.next()) {
                temp = rowSet.getInt(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return temp;
	}

	public ArrayList recordToBean(ArrayList list) {
		ArrayList alist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			RecordVo vo = (RecordVo) list.get(i);
			LazyDynaBean abean = new LazyDynaBean();

			/* 标识：2723 增加tabid属性 xiaoyun 2014-6-23 start */
			abean.set("tabid", String.valueOf(vo.getInt("tabid")));
			/* 标识：2723 增加tabid属性 xiaoyun 2014-6-23 end */
			abean.set("gridno", String.valueOf(vo.getInt("gridno")));
			abean.set("hz", vo.getString("hz"));
			// System.out.println("-------------" + recset.getString("hz"));
			abean.set("rleft", String.valueOf(vo.getInt("rleft")));
			abean.set("rtop", String.valueOf(vo.getInt("rtop")));
			abean.set("rwidth", String.valueOf(vo.getInt("rwidth")));
			abean.set("rheight", String.valueOf(vo.getInt("rheight")));
			abean.set("l", String.valueOf(vo.getInt("l")));
			abean.set("t", String.valueOf(vo.getInt("t")));
			abean.set("r", String.valueOf(vo.getInt("r")));
			abean.set("b", String.valueOf(vo.getInt("b")));
			abean.set("sl", String.valueOf(vo.getInt("sl")));
			abean.set("cfactor", vo.getString("cfactor"));
			abean.set("flag2", String.valueOf(vo.getInt("flag2")));
			abean.set("flag1", String.valueOf(vo.getInt("flag1")));
			abean.set("cexpr2", vo.getString("cexpr2"));
			abean.set("cexpr1", vo.getString("cexpr1"));
			abean.set("scanmode", String.valueOf(vo.getInt("scanmode")));
			abean.set("fontsize", String.valueOf(vo.getInt("fontsize")));
			abean.set("fontname", vo.getString("fontname"));
			abean.set("fonteffect", String.valueOf(vo.getInt("fonteffect")));
			abean.set("flag", String.valueOf(vo.getInt("flag")));
			abean.set("align", String.valueOf(vo.getInt("align")));
			abean.set("lsize", String.valueOf(vo.getInt("lsize")));
			abean.set("rsize", String.valueOf(vo.getInt("rsize")));
			abean.set("tsize", String.valueOf(vo.getInt("tsize")));
			abean.set("bsize", String.valueOf(vo.getInt("bsize")));
			abean.set("npercent", String.valueOf(vo.getInt("npercent")));
			abean.set("archive_item", vo.getString("archive_item"));

			alist.add(abean);
		}

		return alist;
	}

	public ArrayList getRow_ColList(int flag, ArrayList list) {
		ArrayList alist = new ArrayList();
		RowSet recset = null;
		try {
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo = (RecordVo) list.get(i);
				if (vo.getInt("flag") == 0) {
					int align = vo.getInt("align");
					itemAlign = getItemAlign(String.valueOf(align));
					itemSize = String.valueOf(vo.getInt("fontsize"));
					itemFontName = vo.getString("fontname");
					itemName = vo.getString("hz");
					itemEffect = vo.getInt("fonteffect");
					break;
				}
				// if(flag==vo.getInt("flag"))
				// alist.add(vo);
			}
			String sql = "select *  from tgrid2 where tabid=" + tabid + " and flag=" + flag;
			if (flag == 1) {
                sql += " order by rleft";
            } else if (flag == 2) {
                sql += " order by rtop";
            }
			recset = dao.search(sql);
			while (recset.next()) {
				RecordVo vo = new RecordVo("tgrid2");
				/* 标识：2723 增加tabid属性 xiaoyun 2014-6-23 start */
				vo.setInt("tabid", recset.getInt("TabId"));
				/* 标识：2723 增加tabid属性 xiaoyun 2014-6-23 end */
				vo.setInt("gridno", recset.getInt("gridno"));
				vo.setString("hz", recset.getString("hz"));
				// System.out.println("-------------" + recset.getString("hz"));
				vo.setInt("rleft", recset.getInt("rleft"));
				vo.setInt("rtop", recset.getInt("rtop"));
				vo.setInt("rwidth", recset.getInt("rwidth"));
				vo.setInt("rheight", recset.getInt("rheight"));
				vo.setInt("l", recset.getInt("l"));
				vo.setInt("t", recset.getInt("t"));
				vo.setInt("r", recset.getInt("r"));
				vo.setInt("b", recset.getInt("b"));
				vo.setInt("sl", recset.getInt("sl"));
				vo.setString("cfactor", Sql_switcher.readMemo(recset, "cfactor"));
				vo.setInt("flag2", recset.getInt("flag2"));
				vo.setInt("flag1", recset.getInt("flag1"));
				vo.setString("cexpr2", Sql_switcher.readMemo(recset, "cexpr2"));
				vo.setString("cexpr1", Sql_switcher.readMemo(recset, "cexpr1"));
				vo.setInt("scanmode", recset.getInt("scanmode"));
				vo.setInt("fontsize", recset.getInt("fontsize"));
				vo.setString("fontname", recset.getString("fontname"));
				vo.setInt("fonteffect", recset.getInt("fonteffect"));
				vo.setInt("flag", recset.getInt("flag"));
				vo.setInt("align", recset.getInt("align"));
				vo.setInt("lsize", recset.getInt("lsize"));
				vo.setInt("rsize", recset.getInt("rsize"));
				vo.setInt("tsize", recset.getInt("tsize"));
				vo.setInt("bsize", recset.getInt("bsize"));
				vo.setInt("npercent", recset.getInt("npercent"));
				vo.setString("archive_item", recset.getString("archive_item"));
				alist.add(vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}

		return alist;
	}

	public ArrayList getIntegrateRow_ColList(int flag, ArrayList list) {
		ArrayList alist = new ArrayList();
		RowSet recset = null;
		try {
			String sql = "select *  from tgrid2 where tabid=" + tabid + " and flag=0";
			recset = dao.search(sql);
			if (recset.next()) {
				int align = recset.getInt("align");
				itemAlign = getItemAlign(String.valueOf(align));
				itemSize = String.valueOf(recset.getInt("fontsize"));
				itemFontName = recset.getString("fontname");
				itemName = recset.getString("hz");
				itemEffect = recset.getInt("fonteffect");
			}

			if ((nums.indexOf(",a") != -1 && flag == 2) || (nums.indexOf(",b") != -1 && flag == 1)) // 列
			{
				sql = "select *  from tgrid2 where tabid=" + tabid + " and flag=" + flag;
				if (flag == 1) {
                    sql += " order by rleft";
                } else if (flag == 2) {
                    sql += " order by rtop";
                }
				PubFunc.closeResource(recset);
				recset = dao.search(sql);
				while (recset.next()) {
					RecordVo vo = new RecordVo("tgrid2");
					vo.setInt("gridno", recset.getInt("gridno"));
					vo.setString("hz", recset.getString("hz"));
					// System.out.println("-------------" + recset.getString("hz"));
					vo.setInt("rleft", recset.getInt("rleft"));
					vo.setInt("rtop", recset.getInt("rtop"));
					vo.setInt("rwidth", recset.getInt("rwidth"));
					vo.setInt("rheight", recset.getInt("rheight"));
					vo.setInt("l", recset.getInt("l"));
					vo.setInt("t", recset.getInt("t"));
					vo.setInt("r", recset.getInt("r"));
					vo.setInt("b", recset.getInt("b"));
					vo.setInt("sl", recset.getInt("sl"));
					vo.setString("cfactor", Sql_switcher.readMemo(recset, "cfactor"));
					vo.setInt("flag2", recset.getInt("flag2"));
					vo.setInt("flag1", recset.getInt("flag1"));
					vo.setString("cexpr2", Sql_switcher.readMemo(recset, "cexpr2"));
					vo.setString("cexpr1", Sql_switcher.readMemo(recset, "cexpr1"));
					vo.setInt("scanmode", recset.getInt("scanmode"));
					vo.setInt("fontsize", recset.getInt("fontsize"));
					vo.setString("fontname", recset.getString("fontname"));
					vo.setInt("fonteffect", recset.getInt("fonteffect"));
					vo.setInt("flag", recset.getInt("flag"));
					vo.setInt("align", recset.getInt("align"));
					vo.setInt("lsize", recset.getInt("lsize"));
					vo.setInt("rsize", recset.getInt("rsize"));
					vo.setInt("tsize", recset.getInt("tsize"));
					vo.setInt("bsize", recset.getInt("bsize"));
					vo.setInt("npercent", recset.getInt("npercent"));
					vo.setString("archive_item", recset.getString("archive_item"));
					alist.add(vo);
				}
			} else {
				for (int i = 0; i < list.size(); i++) {
					RecordVo vo = (RecordVo) list.get(i);
					int aflag = vo.getInt("flag");
					if (flag == aflag) {
                        alist.add(vo);
                    }
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return alist;
	}

	/**
	 * 为横/纵 栏单元格 加入excel坐标
	 * 
	 * @param infoList
	 * @param flag
	 *            1: 横栏 2：纵栏
	 */
	public void resetGridListInfo(ArrayList infoList, int flag) {
		LazyDynaBean abean = null;
		for (int i = 0; i < infoList.size(); i++) {
			abean = (LazyDynaBean) infoList.get(i);
			int rleft = Integer.parseInt((String) abean.get("rleft"));
			int rtop = Integer.parseInt((String) abean.get("rtop"));
			int rwidth = Integer.parseInt((String) abean.get("rwidth"));
			int rheight = Integer.parseInt((String) abean.get("rheight"));

			if (flag == 1) {

				this.index_x = 0 + this.topParamLayNum;
				this.index_y = this.colLayNum;

				int row_x = Integer.parseInt((String) this.row_t_map.get(String.valueOf(rtop)));
				int row_y = Integer.parseInt((String) this.row_l_map.get(String.valueOf(rleft)));
				abean.set("from_x", String.valueOf(index_x + row_x - 1));
				abean.set("from_y", String.valueOf(index_y + row_y - 1));
				if (rtop + rheight == itemGridArea[1] + itemGridArea[3]) {
					abean.set("to_x", String.valueOf(index_x + rowLayNum - 1));
					abean.set("to_y", String.valueOf(index_y + row_y - 1));
				} else {
					int childNum = getChildNum(abean, flag);
					if (childNum != 0 && this.row_t_map.get(String.valueOf(rtop + rheight)) != null) {
						int row_t_x = Integer.parseInt((String) this.row_t_map.get(String.valueOf(rtop + rheight)));
						if (rleft + rwidth == this.tab_width) {
							abean.set("to_x", String.valueOf(index_x + row_t_x - 2));
							abean.set("to_y", String.valueOf(index_y + this.row_l_map.size() - 1));
						} else {
							/* 标识：2723 报表管理：市政集团，用四处离退休专员登录系统，37号表，输出excel出现空白界面 xiaoyun 2014-6-23 start */
							// 获取下一层横栏在第几层
							String temp = null;
							int row_t_y = 0;
							if (this.row_l_map.containsKey(String.valueOf(rleft + rwidth))) {
								temp = (String) this.row_l_map.get(String.valueOf(rleft + rwidth));
								row_t_y = Integer.parseInt(temp);
							} else {
								// 根据rtop获取下一列横坐标位置
								int t_rleft = getNextCoordinate(abean, flag);
								row_t_y = Integer.parseInt((String) this.row_l_map.get(String.valueOf(t_rleft)));
							}
							// int
							// row_t_y=Integer.parseInt((String)this.row_l_map.get(String.valueOf(rleft+rwidth)));
							/* 标识：2723 报表管理：市政集团，用四处离退休专员登录系统，37号表，输出excel出现空白界面 xiaoyun 2014-6-23 end */
							abean.set("to_x", String.valueOf(index_x + row_t_x - 2));
							abean.set("to_y", String.valueOf(index_y + row_t_y - 2));
						}
					} else {
						abean.set("to_x", String.valueOf(index_x + rowLayNum - 1));
						abean.set("to_y", String.valueOf(index_y + row_y - 1));
					}
				}
			} else {
				this.index_x = this.rowLayNum + this.topParamLayNum;
				this.index_y = 0;

				int row_x = Integer.parseInt((String) this.col_t_map.get(String.valueOf(rtop)));
				int row_y = Integer.parseInt((String) this.col_l_map.get(String.valueOf(rleft)));

				abean.set("from_x", String.valueOf(index_x + row_x - 1));
				abean.set("from_y", String.valueOf(index_y + row_y - 1));
				if (rleft + rwidth == itemGridArea[0] + itemGridArea[2]) {
					abean.set("to_x", String.valueOf(index_x + row_x - 1));
					abean.set("to_y", String.valueOf(index_y + this.colLayNum - 1));
				} else {
					int childNum = getChildNum(abean, flag);
					if (childNum != 0) {

						int col_t_y = 0;
						if (this.col_l_map.get(String.valueOf(rleft + rwidth)) == null) {
                            col_t_y = this.colLayNum;
                        } else {
                            col_t_y = Integer.parseInt((String) this.col_l_map.get(String.valueOf(rleft + rwidth)));
                        }

						if (rtop + rheight == this.tab_height) {
							abean.set("to_x", String.valueOf(index_x + this.col_t_map.size() - 1));
							abean.set("to_y", String.valueOf(index_y + col_t_y - 2));
						} else {
							int col_t_x = Integer.parseInt((String) this.col_t_map.get(String.valueOf(rtop + rheight)));
							abean.set("to_x", String.valueOf(index_x + col_t_x - 2));
							abean.set("to_y", String.valueOf(index_y + col_t_y - 2));
						}

					} else {
						abean.set("to_x", String.valueOf(index_x + row_x - 1));
						abean.set("to_y", String.valueOf(index_y + this.colLayNum - 1));
					}
				}
			}
		}
	}

	/**
	 * 获取该单元格的横坐标右侧位置
	 * 
	 * @param abean
	 * @param flag
	 *            1-横栏 2-纵栏
	 * @return
	 * @author xiaoyun 2014-6-23 标识：2723
	 */
	private int getNextCoordinate(LazyDynaBean abean, int flag) {
		int rleft = Integer.parseInt((String) abean.get("rleft"));
		int rtop = Integer.parseInt((String) abean.get("rtop"));
		int rwidth = Integer.parseInt((String) abean.get("rwidth"));
		int tabid = Integer.parseInt((String) abean.get("tabid"));
		int tleft = 0;
		RowSet rowSet = null;
		try {
			String sql = "select rleft,rtop,rwidth from tgrid2 where TabId=" + tabid + " and flag=" + flag
					+ " and rtop=" + rtop + " order by rleft";
			rowSet = dao.search(sql);
			boolean isOk = false;
			while (rowSet.next()) {
				if (isOk) {
					tleft = rowSet.getInt(1);
					break;
				}
				if (rowSet.getInt(1) == rleft && rowSet.getInt(2) == rtop && rowSet.getInt(3) == rwidth) {
					isOk = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return tleft;
	}

	/**
	 * 1取得横纵栏的层数
	 * 
	 * @param flag
	 *            1: 横栏 2：纵栏
	 * @return
	 */
	public HashMap getTabNumMap(int flag, String columnName) {
		HashMap map = new HashMap();
		RowSet rowSet = null;
		try {
			int num = 0;
			String sql = "";
			if (flag == 1) {
				sql = "select " + columnName + " from tgrid2  where tabid=" + this.tabid + " and flag=1  group by "
						+ columnName + " order by " + columnName;
			} else {
                sql = "select " + columnName + " from tgrid2  where tabid=" + this.tabid + " and flag=2  group by "
                        + columnName + " order by " + columnName;
            }
			rowSet = dao.search(sql);

			while (rowSet.next()) {
				num++;
				map.put(rowSet.getString(1), String.valueOf(num));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}

		return map;
	}

	/**
	 * 取得 表头底层单元格数据集和
	 * 
	 * @param flag
	 *            1: 横栏 2：纵栏
	 * @return
	 */
	public ArrayList getIntegrateBottomGridInfoList(ArrayList alist) {
		ArrayList list = new ArrayList();
		try {
			LazyDynaBean abean = new LazyDynaBean();
			for (int i = 0; i < alist.size(); i++) {
				RecordVo vo = (RecordVo) alist.get(i);
				abean = new LazyDynaBean();

				abean.set("gridno", vo.getString("gridno"));
				abean.set("hz", vo.getString("hz"));
				abean.set("flag2", vo.getString("flag2"));
				abean.set("flag1", vo.getString("flag1"));
				abean.set("flag", vo.getString("flag"));
				abean.set("align", vo.getString("align"));
				abean.set("npercent", vo.getString("npercent"));
				abean.set("cfactor", vo.getString("cfactor"));
				list.add(abean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 取得 表头底层单元格数据集和
	 * 
	 * @param flag
	 *            1: 横栏 2：纵栏
	 * @return
	 */
	public ArrayList getBottomGridInfoList(int flag) {
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			String sql = "";
			if (flag == 1) {
				sql = "select * from tgrid2 where tabid=" + this.tabid + "  and flag=1 and rtop+rheight="
						+ (this.itemGridArea[1] + this.itemGridArea[3]) + " order by rleft";
			} else {
                sql = "select * from tgrid2 where  tabid=" + this.tabid + " and flag=2 and rleft+rwidth="
                        + (this.itemGridArea[0] + this.itemGridArea[2]) + " order by rtop";
            }
			rowSet = dao.search(sql);
			LazyDynaBean abean = null;
			while (rowSet.next()) {
				abean = new LazyDynaBean();

				abean.set("gridno", rowSet.getString("gridno"));
				abean.set("hz", rowSet.getString("hz"));
				abean.set("flag2", rowSet.getString("flag2"));
				abean.set("flag1", rowSet.getString("flag1"));
				abean.set("flag", rowSet.getString("flag"));
				abean.set("align", rowSet.getString("align"));
				abean.set("npercent", rowSet.getString("npercent"));
				abean.set("cfactor", Sql_switcher.readMemo(rowSet, "cfactor"));
				list.add(abean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return list;
	}

	/**
	 * 取得 某单元格下包括的子格个数
	 * 
	 * @param vo
	 * @param flag
	 *            1: 横栏 2：纵栏
	 * @return
	 */
	public int getChildNum(LazyDynaBean vo, int flag) {
		int size = 0;
		RowSet rowSet = null;
		try {
			String sql = "";
			if ("4".equals(this.operateObject)) {
				String atemp = this.nums.substring(3);
				String[] temps = atemp.split(",");
				if (flag == 1) {

					if (nums.indexOf(",a") != -1 && "1"
							.equals((String) vo.get("flag"))/* &&Integer.parseInt((String)vo.get("rwidth"))==temps.length*60 */)// 过渡表宽度规则变，不应判断60
					{
						size = temps.length;
					} else {
						sql = "select count(*) from tgrid2 where  tabid=" + this.tabid + " and flag=1 and rtop+rheight="
								+ (itemGridArea[1] + itemGridArea[3]) + " and rleft>=" + (String) vo.get("rleft")
								+ " and (rleft+rwidth)<="
								+ (Integer.parseInt((String) vo.get("rleft"))
										+ Integer.parseInt((String) vo.get("rwidth")))
								+ " and gridno<>" + (String) vo.get("gridno");
						rowSet = dao.search(sql);
						if (rowSet.next()) {
                            size = rowSet.getInt(1);
                        }
					}
				} else {

					if (nums.indexOf(
							",b") != -1/*
										 * &&Integer.parseInt((String)vo.get("rheight"))==temps.length*this.constantNum
										 */)// 过渡表宽度规则变
					{
						size = temps.length;
					} else {
						sql = "select count(*) from tgrid2 where  tabid=" + this.tabid + " and flag=2 and rleft+rwidth="
								+ (itemGridArea[0] + itemGridArea[2]) + " and rtop>=" + (String) vo.get("rtop")
								+ "  and (rtop+rheight)<="
								+ (Integer.parseInt((String) vo.get("rtop"))
										+ Integer.parseInt((String) vo.get("rheight")))
								+ " and gridno<>" + (String) vo.get("gridno");
						rowSet = dao.search(sql);
						if (rowSet.next()) {
                            size = rowSet.getInt(1);
                        }
					}

				}
			} else {

				if (flag == 1) {
					sql = "select count(*) from tgrid2 where  tabid=" + this.tabid + " and flag=1 and rtop+rheight="
							+ (itemGridArea[1] + itemGridArea[3]) + " and rleft>=" + (String) vo.get("rleft")
							+ " and (rleft+rwidth)<="
							+ (Integer.parseInt((String) vo.get("rleft")) + Integer.parseInt((String) vo.get("rwidth")))
							+ " and gridno<>" + (String) vo.get("gridno");
				} else {
                    sql = "select count(*) from tgrid2 where  tabid=" + this.tabid + " and flag=2 and rleft+rwidth="
                            + (itemGridArea[0] + itemGridArea[2]) + " and rtop>=" + (String) vo.get("rtop")
                            + "  and (rtop+rheight)<="
                            + (Integer.parseInt((String) vo.get("rtop")) + Integer.parseInt((String) vo.get("rheight")))
                            + " and gridno<>" + (String) vo.get("gridno");
                }
				rowSet = dao.search(sql);
				if (rowSet.next()) {
                    size = rowSet.getInt(1);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return size;
	}

	/** 取得报表得最大宽度 或 高度 */
	public int getTabArea(int flag) {
		int value = 0;
		RowSet rowSet = null;
		try {
			String sql = "";
			if (flag == 1) {
				sql = "select max(rleft+rwidth) from tgrid2  where tabid=" + this.tabid + " and flag=" + flag;
			} else {
                sql = "select max(rtop+rheight) from tgrid2  where tabid=" + this.tabid + " and flag=" + flag;
            }
			rowSet = dao.search(sql);
			if (rowSet.next()) {
                value = rowSet.getInt(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return value;
	}

	/** --------------------------- 表参数 ----------------------------- */

	/**
	 * 取得修饰后的 表参数 数据集和
	 * 
	 * @return
	 */
	public void getTabPageList() {
		ArrayList list = new ArrayList();
		try {
			isExistTpageTable();
			ArrayList tempList = new ArrayList();
			int rtop = 0;
			RecordVo vo = null;
			RecordVo a_vo = null;
			for (int i = 0; i < this.pageList.size(); i++) {
				a_vo = new RecordVo("t_tpage");
				vo = (RecordVo) this.pageList.get(i);
				if (rtop == 0) {
                    rtop = vo.getInt("rtop");
                }

				if (vo.getInt("rtop") <= rtop + 10 && vo.getInt("rtop") >= rtop - 10) {
                    vo.setInt("rtop", rtop);
                } else {
                    rtop = vo.getInt("rtop");
                }

				a_vo.setInt("tabid", vo.getInt("tabid"));
				a_vo.setInt("gridno", vo.getInt("gridno"));
				a_vo.setString("hz", vo.getString("hz"));
				a_vo.setInt("rleft", vo.getInt("rleft"));
				a_vo.setInt("rtop", vo.getInt("rtop"));
				a_vo.setInt("rwidth", vo.getInt("rwidth"));
				a_vo.setInt("rheight", vo.getInt("rheight"));
				a_vo.setInt("fontsize", vo.getInt("fontsize"));
				a_vo.setString("fontname", vo.getString("fontname"));
				a_vo.setInt("fonteffect", vo.getInt("fonteffect"));
				a_vo.setInt("flag", vo.getInt("flag"));
				a_vo.setString("extendattr", vo.getString("extendattr"));
				tempList.add(a_vo);
			}
			dao.addValueObject(tempList);
			this.topPageList = getPageList("t");
			this.bottomPageList = getPageList("b");
			this.allPageList = getPageList("all");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param position
	 *            t:表头上部参数 b:表尾 下部参数 all:全部（封面）
	 * @return
	 */
	public ArrayList getPageList(String position) {
		ArrayList list = new ArrayList();
		RowSet recset = null;
		try {
			String sql = "";
			if ("t".equals(position)) {
                sql = "select * from t_tpage where rtop<" + this.top_pix + " order by rtop,rleft";
            } else if ("b".equals(position)) {
                sql = "select * from t_tpage where rtop>" + this.bottom_pix + " order by rtop,rleft";
            } else if ("all".equals(position)) {
                sql = "select * from t_tpage  order by rtop,rleft";
            }
			recset = dao.search(sql);
			int rtop = 0;
			int layNum = 0;
			while (recset.next()) {
				if (layNum == 0 || recset.getInt("rtop") != rtop) // 第一行一定要加进来，后面的行才要进行判断是否为一行 wangcq 2014-11-13
				{
					layNum++;
					if ("t".equals(position)) {
                        this.topLayMap.put(recset.getString("rtop"), String.valueOf(layNum));
                    } else if ("b".equals(position)) {
                        this.botLayMap.put(recset.getString("rtop"), String.valueOf(layNum));
                    } else if ("all".equals(position)) {
                        this.allParamMap.put(recset.getString("rtop"), String.valueOf(layNum));
                    }

				}
				rtop = recset.getInt("rtop");
				RecordVo vo = new RecordVo("t_tpage");
				vo.setInt("tabid", recset.getInt("tabid"));
				vo.setInt("gridno", recset.getInt("gridno"));
				String hz = recset.getString("hz");
				vo.setString("hz", hz);
				vo.setInt("rleft", recset.getInt("rleft"));
				vo.setInt("rtop", recset.getInt("rtop"));
				vo.setInt("rwidth", recset.getInt("rwidth"));
				vo.setInt("rheight", recset.getInt("rheight"));
				vo.setInt("fontsize", recset.getInt("fontsize"));
				vo.setString("fontname", recset.getString("fontname"));
				vo.setInt("fonteffect", recset.getInt("fonteffect"));
				vo.setInt("flag", recset.getInt("flag"));
				vo.setString("extendattr", Sql_switcher.readMemo(recset, "extendattr"));
				list.add(vo);
			}
			if ("t".equals(position)) {
                this.topParamLayNum = layNum;
            } else {
                this.bottomParamLayNum = layNum;
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
		}
		return list;
	}

	/**
	 * 是否存在 tpage 的临时表
	 */
	public void isExistTpageTable() {
		boolean is = true;
		DbWizard dbWizard = new DbWizard(this.conn);
		String sql = "";
		RowSet recset = null;
		RowSet recset1 = null;
		try {
			if (Sql_switcher.searchDbServer() == 2) {
                sql = "create table t_tpage as select * from tpage where 1=2 ";
            } else {
                sql = "select *  into t_tpage  from tpage where 1=2 ";
            }
			if (!dbWizard.isExistTable("t_tpage", false)) {
				dao.update(sql);
				DBMetaModel dbmodel = new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("t_tpage");
			} else {
				recset = dao.search("select * from t_tpage where 1=2");
				ResultSetMetaData data = recset.getMetaData();
				int i = data.getColumnCount();
				recset1 = dao.search("select * from tpage where 1=2");
				ResultSetMetaData data1 = recset1.getMetaData();
				int j = data1.getColumnCount();
				if (i != j) {
					dao.update("drop table t_tpage");
					dao.update(sql);
					DBMetaModel dbmodel = new DBMetaModel(this.conn);
					dbmodel.reloadTableModel("t_tpage");
				}
			}
			dao.delete("delete from t_tpage", new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(recset);
			PubFunc.closeResource(recset1);
		}
	}

	/**
	 * -------------------------------- end --------------------------------------
	 */

	public String executReportExcel() {
		String fileName = this.userView.getUserName()+"_report_" + PubFunc.getStrg() + ".xls";
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
			this.wb = new HSSFWorkbook();
			this.sheet = wb.createSheet();
			initStyleParams();

			if ("5".equals(this.operateObject)) // 反查结果
			{

				executeReverseResult();

			} else {
				if (this.itemGridArea[0] >= this.pageWidth || this.itemGridArea[1] >= this.pageHeight) {
					executeFrontPage();
				} else if ("4".equals(this.operateObject) && this.rowInfoList.size() > 250
						&& nums.indexOf(",a") != -1) {
					String atemp = nums.substring(3);
					String[] temps = atemp.split(",");
					int rownum = 250;
					int resultnum = 0;
					int off = rownum % (temps.length + 1);
					if (off != 0) {
						rownum += temps.length + 1 - off;
						resultnum = (rownum - 1) * temps.length / (temps.length + 1) + 1;
					} else {
						resultnum = rownum * temps.length / (temps.length + 1);
					}

					ArrayList rowInfoListtemp = (ArrayList) this.rowInfoList.clone();
					ArrayList resultListtemp = (ArrayList) this.resultList.clone();
					ArrayList rowInfoBGridtemp = (ArrayList) this.rowInfoBGrid.clone();
					LazyDynaBean abean = (LazyDynaBean) rowInfoListtemp.get(0);
					short to_y2 = Short.parseShort((String) abean.get("to_y"));
					int n = rowInfoListtemp.size() / rownum;
					if (rowInfoListtemp.size() % rownum != 0) {
                        n = n + 1;
                    }

					// 初始化一些数据
					rownum = rownum + 1;// 编号占一列
					resultnum = resultnum + 1;
					this.rowInfoList.clear();
					this.resultList.clear();
					this.rowInfoBGrid.clear();
					for (int j = 0; j < rownum; j++) {
						if (j >= rowInfoListtemp.size()) {
                            break;
                        }
						abean = (LazyDynaBean) rowInfoListtemp.get(j);
						this.rowInfoList.add(abean);
					}
					for (int j = 0; j < resultnum; j++) {
						if (j >= rowInfoBGridtemp.size()) {
                            break;
                        }
						this.rowInfoBGrid.add(rowInfoBGridtemp.get(j));
					}
					String resulettemp[] = (String[]) resultListtemp.get(0);
					for (int i = 0; i < resultListtemp.size(); i++) {
						resulettemp = (String[]) resultListtemp.get(i);
						String resulettemp2[] = new String[resultnum];
						for (int j = 0; j < resultnum; j++) {
							resulettemp2[j] = resulettemp[j];
						}
						this.resultList.add(resulettemp2.clone());
					}
					executeTabHeader();
					executeTabDataArea();
					executeTopParam();
					executeBottomParam();
					resetSize(this.topParamLayNum, 0);
					rownum = rownum - 1;// 编号占一列
					resultnum = resultnum - 1;
					LazyDynaBean abean2 = null;
					for (int i = 1; i < n; i++) {
						// 初始化一些数据
						this.rowInfoList.clear();
						this.resultList.clear();
						this.rowInfoBGrid.clear();
						// if(i==1)
						// this.rowInfoBGrid.remove(0);

						// 定义一个比较的bean
						if (rownum * i >= rowInfoListtemp.size()) {
                            break;
                        }
						// if(i==1){
						abean2 = (LazyDynaBean) rowInfoListtemp.get(rownum * i);
						// }else{
						// abean2 =(LazyDynaBean)rowInfoListtemp.get(rownum*i-1);
						// }

						short to_y = Short.parseShort((String) abean2.get("to_y"));
						short from_y = Short.parseShort((String) abean2.get("from_y"));
						for (int j = 1; j <= rownum; j++) {
							if (rownum * i + j >= rowInfoListtemp.size()) {
                                break;
                            }
							LazyDynaBean abean3 = new LazyDynaBean();
							abean = (LazyDynaBean) rowInfoListtemp.get(rownum * i + j);
							abean3.set("from_y", Short.parseShort("" + abean.get("from_y")) - from_y + to_y2 - 1 + "");
							abean3.set("to_y", Short.parseShort("" + abean.get("to_y")) - to_y + to_y2 - 1 + "");
							abean3.set("from_x", abean.get("from_x"));
							abean3.set("to_x", abean.get("to_x"));
							abean3.set("gridno", abean.get("gridno"));
							abean3.set("hz", abean.get("hz"));
							abean3.set("rleft", abean.get("rleft"));
							abean3.set("rtop", abean.get("rtop"));
							abean3.set("rwidth", abean.get("rwidth"));
							abean3.set("rheight", abean.get("rheight"));
							abean3.set("l", abean.get("l"));
							abean3.set("t", abean.get("t"));
							abean3.set("r", abean.get("r"));
							abean3.set("b", abean.get("b"));
							abean3.set("sl", abean.get("sl"));
							abean3.set("cfactor", abean.get("cfactor"));
							abean3.set("flag2", abean.get("flag2"));
							abean3.set("flag1", abean.get("flag1"));
							abean3.set("cexpr2", abean.get("cexpr2"));
							abean3.set("cexpr1", abean.get("cexpr1"));
							abean3.set("scanmode", abean.get("scanmode"));
							abean3.set("fontsize", abean.get("fontsize"));
							abean3.set("fontname", abean.get("fontname"));
							abean3.set("fonteffect", abean.get("fonteffect"));
							abean3.set("flag", abean.get("flag"));
							abean3.set("align", abean.get("align"));
							abean3.set("lsize", abean.get("lsize"));
							abean3.set("rsize", abean.get("rsize"));
							abean3.set("tsize", abean.get("tsize"));
							abean3.set("bsize", abean.get("bsize"));
							abean3.set("npercent", abean.get("npercent"));
							abean3.set("archive_item", abean.get("archive_item"));
							this.rowInfoList.add(abean3);
							// x起，y起，x结，y结
						}

						resulettemp = (String[]) resultListtemp.get(0);
						for (int m = 0; m < resultListtemp.size(); m++) {
							resulettemp = (String[]) resultListtemp.get(m);

							if (i == n - 1) {
								String resulettemp2[] = new String[resulettemp.length - resultnum * i - 1];
								for (int j = 1; j < resultnum; j++) {
									if (resultnum * i + j >= resulettemp.length) {
                                        break;
                                    }
									resulettemp2[j - 1] = resulettemp[resultnum * i + j];
								}
								this.resultList.add(resulettemp2.clone());
							} else {
								String resulettemp2[] = new String[resultnum];
								for (int j = 1; j < resultnum; j++) {
									if (resultnum * i + j >= resulettemp.length) {
                                        break;
                                    }
									resulettemp2[j - 1] = resulettemp[resultnum * i + j];
								}
								this.resultList.add(resulettemp2.clone());
							}

						}
						for (int j = 1; j <= resultnum; j++) {
							if (resultnum * i + j >= resulettemp.length) {
                                break;
                            }
							this.rowInfoBGrid.add(rowInfoBGridtemp.get(resultnum * i + j));

						}
						this.sheet = wb.createSheet("sheet" + i);
						// init();
						// this.wb.setSheetName(i,"tab_"+this.tabid);

						if (this.itemGridArea[0] >= this.pageWidth || this.itemGridArea[1] >= this.pageHeight) {
							executeFrontPage();
						} else {
							executeTabHeader();
							executeTabDataArea();
							// executeTopParam();
							// executeBottomParam();

						}

						resetSize(this.topParamLayNum, 0);

					}
				} else {
					executeTabHeader();
					executeTabDataArea();

					if (!"3".equals(this.operateObject)) {
                        executeTopParam();
                    }
					if (!"3".equals(this.operateObject)) {
                        executeBottomParam();
                    }
					if ("3".equals(this.operateObject)) {
                        executeFileTitle();
                    }

				}
				resetSize(this.topParamLayNum, 0);
			}
			FileOutputStream fileOut = new FileOutputStream(url);
			this.wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private ArrayList getDataAreaFontStyle() {
		RowSet rs = null;
		String fontName = "";
		String fontsize = "11";
		String fontffect = "1";// 默认1：常规
		ArrayList fontStyleList = new ArrayList();
		try {
			String sql = " select FontName,Fontsize,FontEffect from tname where tabid =?";
			ArrayList valuelist = new ArrayList();
			valuelist.add(this.tabid);
			rs = dao.search(sql, valuelist);
			if (rs.next()) {
                fontName = rs.getString("FontName");
            }
			fontsize = String.valueOf(rs.getInt("Fontsize"));
			fontffect = String.valueOf(rs.getInt("FontEffect"));
			fontStyleList.add(fontName);
			fontStyleList.add(fontsize);
			fontStyleList.add(fontffect);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return fontStyleList;
	}

	public String batchExecutReportExcel(ArrayList tabList) {
		String fileName = this.userView.getUserName()+"_report_" + PubFunc.getStrg() + ".xls";
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;

			this.wb = new HSSFWorkbook();

			for (int i = 0; i < tabList.size(); i++) {
				this.tabid = (String) tabList.get(i);
				initStyleParams();
				this.sheet = wb.createSheet("tab_" + this.tabid);
				init();
				this.wb.setSheetName(i, "tab_" + this.tabid);

				if (this.itemGridArea[0] >= this.pageWidth || this.itemGridArea[1] >= this.pageHeight) {
					executeFrontPage();
				} else {
					executeTabHeader();
					executeTabDataArea();
					executeTopParam();
					executeBottomParam();

				}

				resetSize(this.topParamLayNum, 0);

			}

			FileOutputStream fileOut = new FileOutputStream(url);
			this.wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/*
	 * add by xiegh bug:34232 批量导出和 导出都有该段代码 基本一样，故将该段代码封装起来公用 批量导出excel是
	 * 方法style需要tabid，移到循环中
	 */
	private void initStyleParams() {
		this.style = getStyle("c", wb);
		this.style_l = getStyle("l", wb);
		this.style_r = getStyle("r", wb);
		this.style_cc = getStyle("cc", wb);

		this.styleN = style(wb, 1);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		this.styleF1 = style(wb, 1);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		this.styleF2 = style(wb, 1);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		this.styleF3 = style(wb, 1);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		this.styleF4 = style(wb, 1);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		this.styleF5 = style(wb, 1);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
	}

	/**
	 * 输出报表反查结果
	 */
	public void executeReverseResult() {
		RowSet rowSet = null;
		try {
			HashMap setMap = new HashMap();
			ArrayList fieldItemList = new ArrayList();
			String[] temps = this.setMap_str.split("/");
			HashMap midMap = new HashMap();
			for (int i = 0; i < temps.length; i++) {
				if (temps[i] == null || temps[i].trim().length() == 0) {
                    continue;
                }
				setMap.put(temps[i].split(":")[0], temps[i].split(":")[1]);
			}
			temps = this.fieldItem_str.split("/");
			for (int i = 0; i < temps.length; i++) {
				if (temps[i] == null || temps[i].trim().length() == 0) {
                    continue;
                }
				fieldItemList.add(temps[i]);
			}

			for (int a = 0; a < fieldItemList.size(); a++) {
				String itemid = (String) fieldItemList.get(a);
				if ("1".equals(scanMode) && ("B0110".equals(itemid) || "A0101".equals(itemid))) {
                    continue;
                }
				if ("6".equals(scanMode) && ("B0110".equals(itemid) || "A0101".equals(itemid))) {
                    continue;
                }
				if (DataDictionary.getFieldItem(itemid.toLowerCase()) == null) { // 临时变量的处理
					rowSet = dao.search(" select * from midvariable where templetid=" + this.tabid + " and cname='"
							+ itemid + "' and nflag=2");
					RecordVo vo = new RecordVo("MidVariable");
					if (rowSet.next()) {
						vo.setInt("nid", rowSet.getInt("nid"));
						vo.setString("cname", rowSet.getString("cname"));
						vo.setString("chz", rowSet.getString("chz"));
						vo.setInt("ntype", rowSet.getInt("ntype"));
						vo.setString("cvalue", Sql_switcher.readMemo(rowSet, "cvalue"));
						vo.setInt("nflag", rowSet.getInt("nflag"));
						vo.setString("cstate", rowSet.getString("cstate"));
						vo.setInt("fldlen", rowSet.getInt("fldlen"));
						vo.setInt("flddec", rowSet.getInt("flddec"));
						vo.setInt("templetid", rowSet.getInt("templetid"));
						vo.setString("codesetid", rowSet.getString("codesetid"));
						midMap.put(rowSet.getString("cname"), vo);
					}
				}
			}

			int rowNum = 0;
			// head
			short colIndex = 0;
			if ("1".equals(scanMode) || "6".equals(scanMode)) {
				executeCell2((short) 0, rowNum, "序号", "c");
				executeCell2((short) 1, rowNum, "人员库", "c");
				executeCell2((short) 2, rowNum, "姓名", "c");
				executeCell2((short) 3, rowNum, "单位名称", "c");
				colIndex = 3;
			} else {
				executeCell2((short) 0, rowNum, "序号", "c");
				executeCell2((short) 1, rowNum, " 组织名称", "c");
				colIndex = 1;
			}
			for (int i = 0; i < fieldItemList.size(); i++) {
				String itemid = (String) fieldItemList.get(i);
				if ("1".equals(scanMode) && ("B0110".equals(itemid) || "A0101".equals(itemid))) {
					continue;
				}
				if ("6".equals(scanMode) && ("B0110".equals(itemid) || "A0101".equals(itemid))) {
					continue;
				}
				if (!"1".equals(scanMode)) {
					if ("B0110".equalsIgnoreCase(itemid) || "E01A1".equalsIgnoreCase(itemid)) {
                        continue;
                    }
				}

				if ("6".equals(scanMode) && "create_date".equalsIgnoreCase(itemid)) {
					colIndex++;
					executeCell2(colIndex, rowNum, "归档日期", "c");
					continue;
				} else {
					if (DataDictionary.getFieldItem(itemid.toLowerCase()) == null) {
						colIndex++;
						if (midMap != null && midMap.get(itemid) != null) {
							RecordVo vo = (RecordVo) midMap.get(itemid);
							executeCell2(colIndex, rowNum, vo.getString("chz"), "c");
						} else {
							executeCell2(colIndex, rowNum, itemid.toLowerCase(), "c");
						}
						continue;
					}
				}
				colIndex++;
				executeCell2(colIndex, rowNum, DataDictionary.getFieldItem(itemid.toLowerCase()).getItemdesc(), "c");
			}

			HashMap db_map = new HashMap();
			PubFunc.closeResource(rowSet);
			rowSet = dao.search("select * from dbname");
			while (rowSet.next()) {
				db_map.put(rowSet.getString("Pre").toLowerCase(), rowSet.getString("DBName"));
			}

			FieldItem item = null;
			PubFunc.closeResource(rowSet);
			rowSet = dao.search(this.reverseSql);
			int i = 0;
			while (rowSet.next()) {
				i++;
				rowNum++;
				short colIndex_ = 0;
				executeCell2(colIndex_, rowNum, String.valueOf(i), "c");
				if ("1".equals(scanMode) || "6".equals(scanMode)) {
					colIndex_++;
					executeCell2(colIndex_, rowNum, (String) db_map.get(rowSet.getString("dbpre").toLowerCase()), "c");
					colIndex_++;
					executeCell2(colIndex_, rowNum, rowSet.getString("a0101"), "c");
					colIndex_++;
					executeCell2(colIndex_, rowNum, AdminCode.getCodeName("UN", rowSet.getString("b0110")), "c");
				} else if ("2".equals(scanMode)|| "5".equals(scanMode)||"3".equals(scanMode)||"4".equals(scanMode)) {//2单位 zhaoxg 2013-4-20 3单位和部门 4部门 5职位
					colIndex_++;
					executeCell2(colIndex_, rowNum, rowSet.getString("a_name"), "c");
				} 

				for (int a = 0; a < fieldItemList.size(); a++) {
					String itemid = (String) fieldItemList.get(a);
					String value_str = "";

					String set = (String) setMap.get(itemid);
					if (set == null || "0".equals(set)) // 不是代码型
					{
						String context = "";
						if ("create_date".equalsIgnoreCase(itemid)) {
							context = "" + rowSet.getDate(itemid);
						} else {
							context = rowSet.getString(itemid);
						}
						if (context != null) {
							if (DataDictionary.getFieldItem(itemid.toLowerCase()) == null) {
								if (midMap != null && midMap.get(itemid) != null) {
									RecordVo vo = (RecordVo) midMap.get(itemid);
									int ntype = vo.getInt("ntype");
									int flddec = vo.getInt("flddec");
									String codesetid = vo.getString("codesetid");
									switch (ntype) {
									case 1: // 数值型
										value_str = PubFunc.round(rowSet.getString(itemid), flddec);
										break;
									case 2: // 字符型
									case 3: // 日期型
										value_str = rowSet.getString(itemid);
										break;
									case 4: // 代码型
										value_str = AdminCode.getCodeName(codesetid, rowSet.getString(itemid));
										break;
									}
								} else {

									if ("create_date".equalsIgnoreCase(itemid)) {
										if (context.length() > 9) {
                                            value_str = context.substring(0, 10);
                                        } else {
                                            value_str = "";
                                        }
									}

								}
							} else {
								item = DataDictionary.getFieldItem(itemid.toLowerCase());
								if ("N".equalsIgnoreCase(item.getItemtype())) {
									int decimalWidth = item.getDecimalwidth();
									String value = rowSet.getString(itemid);
									value_str = PubFunc.round(value, decimalWidth);

								} else {
                                    value_str = rowSet.getString(itemid);
                                }
							}
						}
					} else {

						if (rowSet.getString(itemid) != null && !"".equals(rowSet.getString(itemid))) {
							if ("UM".equals(set)) {
								if (rowSet.getString(itemid) != null) {
                                    value_str = AdminCode.getCodeName("UM", rowSet.getString(itemid));
                                }
							} else if ("UN".equals(set)) {
								if (AdminCode.getCodeName("UN", rowSet.getString(itemid)) != null
										&& AdminCode.getCodeName("UN", rowSet.getString(itemid)).trim().length() > 0) {
                                    value_str = AdminCode.getCodeName("UN", rowSet.getString(itemid));
                                } else {
                                    value_str = AdminCode.getCodeName("UM", rowSet.getString(itemid));
                                }
							} else if ("@K".equals(set)) {
								value_str = AdminCode.getCodeName("@K", rowSet.getString(itemid));
							} else {
								value_str = AdminCode.getCodeName(set, rowSet.getString(itemid));
							}
						}
					}
					colIndex_++;
					executeCell2(colIndex_, rowNum, value_str, "c");

				}

			}

			for (short j = 0; j <= colIndex; j++) {
				if (j == 0) {
                    this.sheet.setColumnWidth(j, (short) 3000);
                } else {
                    this.sheet.setColumnWidth(j, (short) 5000);
                }

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}

	}

	public void executeCell2(short columnIndex, int rowNum, String value, String style) {

		row = this.sheet.getRow(rowNum);
		if (row == null) {
            row = sheet.createRow(rowNum);
        }
		cell = row.createCell(columnIndex);
		if ("c".equalsIgnoreCase(style)) {
            cell.setCellStyle(this.style);
        } else if ("l".equalsIgnoreCase(style)) {
            cell.setCellStyle(this.style_l);
        } else if ("R".equalsIgnoreCase(style)) {
            cell.setCellStyle(this.style_r);
        }
		if (value == null) {
            value = "";
        }
		HSSFRichTextString richTextString = new HSSFRichTextString(value);
		cell.setCellValue(richTextString);

	}

	/** 封面 */
	public void executeFrontPage() {
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		for (int i = 0; i < this.allPageList.size(); i++) {
			RecordVo vo = (RecordVo) this.allPageList.get(i);
			String fontname = vo.getString("fontname");
			String fontsize = String.valueOf(vo.getInt("fontsize"));
			int fonteffect = vo.getInt("fonteffect");
			StringBuffer values = new StringBuffer("");
			values.append(getTitleValue(vo));
			if (vo.getInt("flag") == 10) {
				try {
					int from_x = Integer.parseInt((String) this.allParamMap.get(String.valueOf(vo.getInt("rtop")))) - 1;

					short from_y = (short) (15 * vo.getInt("rleft") / tab_width);
					short from2_y = (short) (15 * (vo.getInt("rleft") + vo.getInt("rwidth")) / tab_width);

					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
					// executeCellImage(from_x,from_y,from_x,from2_y,vo.getInt("rwidth"),vo.getInt("rheight"),patriarch,vo);
					executeCellImage(from_x, from_y, from_x, from2_y, vo.getInt("rwidth"), vo.getInt("rheight"),
							patriarch, vo, false);
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 end */
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}
			for (int j = i + 1; j < this.allPageList.size(); j++) {
				RecordVo temp_vo = (RecordVo) this.allPageList.get(j);

				if (temp_vo.getInt("rtop") == vo.getInt("rtop")) {
					values.append("    " + getTitleValue(temp_vo));
				} else {
					int from_x = Integer.parseInt((String) this.allParamMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					executeCell(from_x, (short) 0, from_x, (short) 15, values.toString(), "no_c", fontname, fontsize,
							fonteffect);
					i = j - 1;
					break;
				}

				if (j == this.allPageList.size() - 1) {
					int from_x = Integer.parseInt((String) this.allParamMap.get(String.valueOf(vo.getInt("rtop")))) - 1;

					executeCell(from_x, (short) 0, from_x, (short) 15, values.toString(), "no_c", fontname, fontsize,
							fonteffect);
					i = j;
					break;
				}

			}

		}

	}

	/* 产生归档标题 */
	public void executeFileTitle() {
		RowSet rowSet = null;
		try {
			if (topParamLayNum >= 1) {
				rowSet = dao.search("select name from tname where tabid=" + this.tabid);
				if (rowSet.next()) {
					String title = rowSet.getString(1);
					if ("8".equals(this.narch)) {
                        title += "  " + this.yearid + "年度统计报表";
                    }
					executeCell(0, (short) 0, 0,
							Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)), title,
							"no_c", "宋体", "18", 0);
				}
			}
			if (topParamLayNum >= 2) {
				String unitname = "";
				String fileDate = "";
				if ("1".equals(narch)) {
					fileDate = this.yearid + ResourceFactory.getProperty("report_collect.nd") + this.countid
							+ ResourceFactory.getProperty("report_collect.info12");
				} else if ("2".equals(narch)) {
					fileDate = this.yearid + ResourceFactory.getProperty("report_collect.info13");
				} else if ("3".equals(narch)) {
					if ("1".equals(this.countid)) {
                        fileDate = this.yearid + ResourceFactory.getProperty("report_collect.info14");
                    } else {
                        fileDate = this.yearid + ResourceFactory.getProperty("report_collect.info15");
                    }
				} else if ("4".equals(narch)) {
					fileDate = this.yearid + ResourceFactory.getProperty("report_collect.nd") + this.countid
							+ ResourceFactory.getProperty("report_collect.info16");
				} else if ("5".equals(narch)) {
					fileDate = this.yearid + ResourceFactory.getProperty("report_collect.nd") + this.countid
							+ ResourceFactory.getProperty("report_collect.info17");
				} else if ("8".equals(narch)) {
                    fileDate = this.yearid + "年度统计报表";
                }
				rowSet = dao.search("select unitname from tt_organization where unitcode='" + this.unitcode + "'");
				if (rowSet.next()) {
                    unitname = rowSet.getString("unitname");
                }
				String str = ResourceFactory.getProperty("report.appealUnit") + "：" + unitname + " |  " + fileDate;
				executeCell(1, (short) 0, 1,
						Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)), str, "no_r",
						"宋体", "13", 0);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}

	/** 产生表头标题 */
	public void executeTopParam() {
		HSSFCellStyle a_style = null;
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		for (int i = 0; i < this.topPageList.size(); i++) {
			RecordVo vo = (RecordVo) this.topPageList.get(i);
			String fontname = vo.getString("fontname");
			String fontsize = String.valueOf(vo.getInt("fontsize"));
			int fonteffect = vo.getInt("fonteffect");

			if (vo.getInt("flag") == 10) {
				try {
					int from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					// int
					// from_y=Integer.parseInt((String)this.allParamcolumnMap.get(String.valueOf(vo.getInt("rleft"))))-1;
					short num = Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1));

					short from_y = (short) (num * vo.getInt("rleft") / tab_width);
					short from2_y = (short) (num * (vo.getInt("rleft") + vo.getInt("rwidth")) / tab_width);
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
					// executeCellImage(from_x,(short)from_y,from_x,from2_y,vo.getInt("rwidth"),vo.getInt("rheight"),patriarch,vo);
					executeCellImage(from_x, (short) from_y, from_x, from2_y, vo.getInt("rwidth"), vo.getInt("rheight"),
							patriarch, vo, true);
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}
			if (i == 0 && this.topPageList.size() > 1
					&& ((RecordVo) this.topPageList.get(1)).getInt("rtop") != vo.getInt("rtop")) {
				int from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
				short from_y = 0;
				String hz = vo.getString("hz");
				if (hz != null) {
                    hz = hz.replaceAll("&nbsp;", " ");
                }
				executeCell(from_x, from_y, from_x,
						Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)), hz, "no_c",
						fontname, fontsize, fonteffect);

			} else {

				if (i + 1 < this.topPageList.size()) {
					RecordVo vo_2 = (RecordVo) this.topPageList.get(i + 1);

					int from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					short from_y = getColumn_y(vo.getInt("rleft"));
					int from2_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo_2.getInt("rtop"))))
							- 1;
					short from2_y = getColumn_y(vo_2.getInt("rleft"));
					if (from_x == from2_x && from_y == from2_y) {
						String value1 = getTitleValue(vo);
						String value2 = getTitleValue(vo_2);
						String value = (value1 + value2).replaceAll("&nbsp;", " ");
						if (from_y == 0) {
							if (this.colLayNum == 1) {
                                this.sheet.setColumnWidth((short) 0, (short) 8000);
                            }
							executeCell(from_x, from_y, from_x, Short.parseShort(String.valueOf(this.colLayNum - 1)),
									value, "no_l", fontname, fontsize, fonteffect);
						} else {
							executeCell(from_x, from_y, from_x, Short.parseShort(String.valueOf(from_y + 2)), value,
									"no_l", fontname, fontsize, fonteffect);
						}
						i++;
					} else {
						String value1 = getTitleValue(vo).replaceAll("&nbsp;", " ");
						if (from_x == from2_x) {
							executeCell(from_x, from_y, from_x,
									Short.parseShort(String.valueOf(from_y + (from2_y - from_y - 1))), value1, "no_l",
									fontname, fontsize, fonteffect);
						} else {
							executeCell(from_x, from_y, from_x,
									Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)),
									value1, "no_l", fontname, fontsize, fonteffect);
						}
					}
				} else {
					int from_x = Integer.parseInt((String) this.topLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					short from_y = getColumn_y(vo.getInt("rleft"));
					String value1 = getTitleValue(vo).replaceAll("&nbsp;", " ");
					String temp = "";
					if (from_y == 0 && fonteffect == 2) {// 只有一行标题且从左面零列开始合并且为粗体的 那么居中 zhaoxg 2013-11-14
						temp = "no_c";
					} else {
						temp = "no_l";
					}
					executeCell(from_x, from_y, from_x,
							Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)), value1,
							temp, fontname, fontsize, fonteffect);
				}
			}
		}
	}

	/** 产生表尾标题 */
	public void executeBottomParam() {
		HSSFCellStyle a_style = null;
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		int assist_x = this.topParamLayNum + this.rowLayNum + this.colInfoBGrid.size();
		String oldValue = "";
		int old_fromx = -1;
		int old_fromy = -1;
		for (int i = 0; i < this.bottomPageList.size(); i++) {

			RecordVo vo = (RecordVo) this.bottomPageList.get(i);
			String fontname = vo.getString("fontname");
			String fontsize = String.valueOf(vo.getInt("fontsize"));
			int fonteffect = vo.getInt("fonteffect");
			// wangcq 2014-12-24 begin 表格区域底下标题生成图片
			if (vo.getInt("flag") == 10) {
				try {
					int from_x = Integer.parseInt((String) this.botLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
					// int
					// from_y=Integer.parseInt((String)this.botLayMap.get(String.valueOf(vo.getInt("rleft"))))-1;
					short num = Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1));

					short from_y = (short) (num * vo.getInt("rleft") / tab_width);
					short from2_y = (short) (num * (vo.getInt("rleft") + vo.getInt("rwidth")) / tab_width);
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
					// executeCellImage(from_x,(short)from_y,from_x,from2_y,vo.getInt("rwidth"),vo.getInt("rheight"),patriarch,vo);
					executeCellImage(from_x + assist_x, (short) from_y, from_x + assist_x, from2_y, vo.getInt("rwidth"),
							vo.getInt("rheight"), patriarch, vo, true);
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}
			// wangcq 2014-12-24 end
			if (i + 1 < this.bottomPageList.size()) {
				RecordVo vo_2 = (RecordVo) this.bottomPageList.get(i + 1);

				int from_x = Integer.parseInt((String) this.botLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
				short from_y = getColumn_y(vo.getInt("rleft"));
				int from2_x = Integer.parseInt((String) this.botLayMap.get(String.valueOf(vo_2.getInt("rtop")))) - 1;
				short from2_y = getColumn_y(vo_2.getInt("rleft"));
				if (from_x == from2_x && from_y == from2_y) {
					String value1 = getTitleValue(vo);
					String value2 = getTitleValue(vo_2);
					String value = (value1 + "          " + value2).replaceAll("&nbsp;", " ");// xiegh 20170327 26519
																								// 加空格隔开底表题之间的距离
					if (from_x == old_fromx && from_y == old_fromy) {
                        value = oldValue + value;
                    }
					oldValue = value;
					old_fromx = from_x;
					old_fromy = from_y;

					if (from_y == 0) {
						executeCell(from_x + assist_x, from_y, from_x + assist_x,
								Short.parseShort(String.valueOf(this.colLayNum - 1)), value, "no_l", fontname, fontsize,
								fonteffect);
					} else {
						executeCell(from_x + assist_x, from_y, from_x + assist_x,
								Short.parseShort(String.valueOf(from_y + 2)), value, "no_l", fontname, fontsize,
								fonteffect);
					}
					i++;
				} else {
					String value1 = getTitleValue(vo).replaceAll("&nbsp;", " ");
					if (from_x == old_fromx && from_y == old_fromy) {
                        value1 = oldValue + "        " + value1;// xiegh 20170327 26519 加空格隔开底表题之间的距离
                    }
					oldValue = value1;
					old_fromx = from_x;
					old_fromy = from_y;
					if (from_x == from2_x) {
						executeCell(from_x + assist_x, from_y, from_x + assist_x,
								Short.parseShort(String.valueOf(from_y + (from2_y - from_y - 1))), value1, "no_l",
								fontname, fontsize, fonteffect);
					} else {
						executeCell(from_x + assist_x, from_y, from_x + assist_x,
								Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1)), value1,
								"no_l", fontname, fontsize, fonteffect);
					}
				}
			} else {
				int from_x = Integer.parseInt((String) this.botLayMap.get(String.valueOf(vo.getInt("rtop")))) - 1;
				short from_y = getColumn_y(vo.getInt("rleft"));
				String value1 = getTitleValue(vo).replaceAll("&nbsp;", " ");
				// update by xiegh 20170408 bug号26797
				short to_y = Short.parseShort(String.valueOf(this.colLayNum + this.rowInfoBGrid.size() - 1));
				// 48752 底部标题位置不为负 且有多个标题存在时修改
				executeCell(from_x + assist_x, (short) (from_y) >= 0 ? (short) (from_y) : 0, from_x + assist_x,
						(short) (to_y), value1, "no_l", fontname, fontsize, fonteffect);// b 起始 y坐标非负
			}

		}
	}

	/**
	 * 取得标题值（包括参数）
	 * 
	 * @param vo
	 * @return
	 */
	public String getTitleValue(RecordVo vo) {
		StringBuffer content = new StringBuffer("");
		RowSet rowSet = null;
		try {
			Date dd = new Date(); // 制表时间
			String extendattr = vo.getString("extendattr");
			String temp = "";
			String formattemp = "";
			int format = 0;
			if (extendattr.indexOf("<prefix>") != -1) {
				int fromIndex = extendattr.indexOf("<prefix>");
				int toIndex = extendattr.indexOf("</prefix>");
				temp = extendattr.substring(fromIndex + 8, toIndex).trim();
			}
			if (extendattr.indexOf("<format>") != -1) {
				int fromIndex = extendattr.indexOf("<format>");
				int toIndex = extendattr.indexOf("</format>");
				formattemp = extendattr.substring(fromIndex + 8, toIndex).trim();
				if (formattemp.length() > 0) {
                    format = Integer.parseInt(formattemp);
                }
			}
			switch (vo.getInt("flag")) {
			case 0:
				content.append(vo.getString("hz").replaceAll("&nbsp;", " "));
				break;
			case 1:
				GregorianCalendar d = new GregorianCalendar();
				// d.add(d.MONTH,d.get(Calendar.MONTH)+1);

				if ("".equals(temp))
					// content.append(ResourceFactory.getProperty("hmuster.label.createTableDate")+":"+formatDateFiledsetValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE),format));
					// dml 2012年1月17日17:14:06 lisuju提 制表日期去掉前缀符不应该再出现前缀福
                {
                    content.append(formatDateFiledsetValue(
                            d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE),
                            format));
                } else {
					if (!(temp.lastIndexOf(":") != -1 || temp.lastIndexOf("：") != -1)) {
                        temp += ":";
                    }
					content.append(temp + formatDateFiledsetValue(
							d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE),
							format));
				}
				break;
			case 2:
				content.append(ResourceFactory.getProperty("hmuster.label.createTableTime")
						+ DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA).format(dd));
				break;
			case 3:
				rowSet = dao
						.search("select fullname from operuser where username='" + this.userView.getUserName() + "'");
				String fullname = this.userView.getUserName();
				if (rowSet.next()) {
					if (rowSet.getString(1) != null && rowSet.getString(1).length() > 0) {
                        fullname = rowSet.getString(1);
                    }
				}
				content.append(ResourceFactory.getProperty("hmuster.label.createTableMen") + "：" + fullname);
				break;
			case 4: // 总页数
				break;
			case 5: // 页码
				break;
			case 9: // 参数定义
				String hz = vo.getString("hz");
				HashMap param_map = (HashMap) paramMap.get(hz);
				if (param_map != null) {
					if (((String) param_map.get("paramtype"))
							.equals(ResourceFactory.getProperty("kq.formula.character"))) {
						if (param_map.get("a_value") != null) {
                            content.append((String) param_map.get("a_value"));
                        }
					} else if (((String) param_map.get("paramtype"))
							.equals(ResourceFactory.getProperty("orglist.reportunitlist.code"))) {
						String[] values = new String[2];
						if (((String) param_map.get("a_value")).indexOf("/") != -1) {
                            values = ((String) param_map.get("a_value")).split("/");
                        } else {
							values[0] = "";
							values[1] = "";
						}
						content.append(values[1]);
					} else if (((String) param_map.get("paramtype"))
							.equals(ResourceFactory.getProperty("report.parse.d"))) {
						content.append((String) param_map.get("a_value"));
					} else if (((String) param_map.get("paramtype"))
							.equals(ResourceFactory.getProperty("kq.formula.counts"))) {
						if (param_map.get("a_value") != null) {
                            content.append((String) param_map.get("a_value"));
                        }
					} else if (((String) param_map.get("paramtype"))
							.equals(ResourceFactory.getProperty("report.parse.text"))) {
						if (param_map.get("a_value") != null) {
                            content.append((String) param_map.get("a_value"));
                        }
					}
				}
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	/**
	 * 取得标题在excel的纵坐标位置
	 * 
	 * @param rleft
	 * @return
	 */
	public short getColumn_y(int rleft) {
		short y = 0;
		LazyDynaBean abean = null;
		short max_y = 0;
		int num = 0;

		for (int i = 0; i < this.rowInfoList.size(); i++) {
			abean = (LazyDynaBean) this.rowInfoList.get(i);
			int a_rleft = Integer.parseInt((String) abean.get("rleft"));
			int a_width = Integer.parseInt((String) abean.get("rwidth"));
			int a_rtop = Integer.parseInt((String) abean.get("rtop"));
			int a_rheight = Integer.parseInt((String) abean.get("rheight"));

			short temp_y = Short.parseShort((String) abean.get("from_y"));
			if (temp_y > max_y) {
                max_y = temp_y;
            }

			if (rleft >= a_rleft && rleft <= (a_rleft + a_width)
					&& a_rtop + a_rheight == itemGridArea[1] + itemGridArea[3]) {
				num++;
				y = Short.parseShort((String) abean.get("from_y"));
				break;
			}
		}
		if (num == 0 && rleft > 10 && rleft > itemGridArea[0] + itemGridArea[2]) {
            y = ++max_y;
        }
		return y;
	}

	/**
	 * 设置excel字体效果
	 * 
	 * @param fonts
	 *            设置不同的字体
	 * @param size
	 *            设置字体的大小
	 * @param workbook
	 *            新建的表格
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size) {
		return fonts(workbook, fonts, size, "");
	}

	/**
	 * 设置excel字体效果
	 * 
	 * @param fonts
	 *            设置不同的字体
	 * @param size
	 *            设置字体的大小
	 * @param workbook
	 *            新建的表格
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size, String fontffect) {
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);

		if ("2".equals(fontffect))// 字形：1:常规 2：粗体 3: 斜体 4：粗体加斜体 不设置 则为正常
        {
            font.setBold(true);
        } else if ("3".equals(fontffect)) {
            font.setItalic(true);
        } else if ("4".equals(fontffect)) {
			font.setItalic(true);
			font.setBold(true);
		}
		return font;
	}

	/**
	 * 设置excel表格效果
	 * 
	 * @param styles
	 *            设置不同的效果
	 * @param workbook
	 *            新建的表格
	 */
	public HSSFCellStyle style(HSSFWorkbook workbook, int styles) {
		HSSFCellStyle style = workbook.createCellStyle();

		// add by xiegh on date 20180104 bug:33670
		ArrayList dataAreaFontList = getDataAreaFontStyle();
		String dataAreaFontName = (String) dataAreaFontList.get(0);// 数据区域字体样式
		dataAreaFontName = dataAreaFontName.length() > 0 ? dataAreaFontName
				: ResourceFactory.getProperty("gz.gz_acounting.black.font");
		Integer fontSize = Integer.valueOf((String) dataAreaFontList.get(1)); // 数据区域字体大小
		String fontffect = (String) dataAreaFontList.get(2); // 字形：1:常规 2：粗体 3: 斜体 4：粗体加斜体

		switch (styles) {

		case 0:
			HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 15);
			fonttitle.setBold(true);// 加粗
			style.setFont(fonttitle);
			style.setAlignment(HorizontalAlignment.LEFT);
			break;
		case 1:
			style.setFont(fonts(workbook, dataAreaFontName, fontSize, fontffect));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER);
			break;
		case 2:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			break;
		case 3:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			break;
		case 4:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			break;
		default:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
			style.setAlignment(HorizontalAlignment.LEFT);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			break;
		}
		return style;
	}

	public String decimalwidth(int len) {
		StringBuffer decimal = new StringBuffer("0");
		if (len > 0) {
            decimal.append(".");
        }
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	/**
	 * 数据区
	 */
	public void executeTabDataArea() {
		int colNum = 0;
		int rowNum = 0;
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setFontName("宋体");
		this.style_r.setFont(font);
		this.style.setFont(font);

		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				rowNum = 0;
				String[] rowInfo = (String[]) resultList.get(i);
				if (i >= this.colInfoBGrid.size()) {
                    continue;
                }
				LazyDynaBean colVo = (LazyDynaBean) this.colInfoBGrid.get(i);
				if (!"4".equals((String) colVo.get("flag1"))) {
                    colNum++;
                }
				for (int j = 0; j < rowInfo.length; j++) {
					String context = "";
					LazyDynaBean rowVo = (LazyDynaBean) this.rowInfoBGrid.get(j);
					int npercent = 0;
					int row_percent = Integer.parseInt((String) rowVo.get("npercent"));
					int col_percent = Integer.parseInt((String) colVo.get("npercent"));

					// if(((String)rowVo.get("flag1")).equals("2")||((String)colVo.get("flag1")).equals("2"))
					// //统计个数
					// npercent=0;
					// else
					/*
					 * if(((String)rowVo.get("flag1")).equals("3")) npercent=row_percent; else
					 * if(((String)colVo.get("flag1")).equals("3")) npercent=col_percent; else
					 */
					// 上面这个需要注释了 跟界面展示那一块保持一致 当初xgq改的 没有改全 add by xiegh ondate 20180122
					npercent = row_percent >= col_percent ? row_percent : col_percent;

					boolean isFlag = false;

					if (!"4".equals((String) rowVo.get("flag1"))) {
                        rowNum++;
                    }
					if ("4".equals((String) colVo.get("flag1")) && "4".equals((String) rowVo.get("flag1"))) {
						context = "";
					} else if ("4".equals((String) colVo.get("flag1")))// flag1:1:取值, 2:统计个数, 3:表达式, 4:求序号
					{
						context = String.valueOf(rowNum);
						isFlag = true;
					} else if ("4".equals((String) rowVo.get("flag1"))) {
						context = String.valueOf(colNum);
						isFlag = true;
					} else {
						if (rowInfo[j] == null || Float.parseFloat(rowInfo[j]) == 0) {
                            context = "";
                        } else {
                            context = PubFunc.round(rowInfo[j], npercent);
                        }
					}

					this.row = sheet.getRow(this.rowLayNum + this.topParamLayNum + i);
					if (row == null) {
                        row = sheet.createRow(this.rowLayNum + this.topParamLayNum + i);
                    }
					this.cell = row.createCell(Short.parseShort(String.valueOf(this.colLayNum + j)));
					switch (npercent)// npercent:小数位数
					{
					case 0:
						this.cell.setCellStyle(styleN);
						break;
					case 1:
						this.cell.setCellStyle(styleF1);
						break;
					case 2:
						this.cell.setCellStyle(styleF2);
						break;
					case 3:
						this.cell.setCellStyle(styleF3);
						break;
					case 4:
						this.cell.setCellStyle(styleF4);
						break;
					default:
						this.cell.setCellStyle(styleF5);
						break;
					}
					// if(isFlag) isFlag：true 表示为序号 这段代码 没啥意义 同样是数据区域的数据 格式应该保持一致
					// this.cell.setCellStyle(this.style);

					if (context.trim().length() > 0) {
						if (isFlag) {
                            this.cell.setCellValue(new HSSFRichTextString(context));
                        } else {
                            this.cell.setCellValue(Double.parseDouble(context));
                        }
					}
				}
			}
		} else {
			for (int i = 0; i < this.colInfoBGrid.size(); i++) {
				rowNum = 0;
				LazyDynaBean colVo = (LazyDynaBean) this.colInfoBGrid.get(i);
				if (!"4".equals((String) colVo.get("flag1"))) {
                    colNum++;
                }
				for (int j = 0; j < this.rowInfoBGrid.size(); j++) {
					String context = "";
					LazyDynaBean rowVo = (LazyDynaBean) this.rowInfoBGrid.get(j);
					/*
					 * int row_percent=Integer.parseInt((String)rowVo.get("npercent")); int
					 * col_percent=Integer.parseInt((String)colVo.get("npercent")); int
					 * npercent=row_percent>=col_percent?row_percent:col_percent;
					 */
					int npercent = 0;
					int row_percent = Integer.parseInt((String) rowVo.get("npercent"));
					int col_percent = Integer.parseInt((String) colVo.get("npercent"));
					boolean isFlag = false;
					if ("2".equals((String) rowVo.get("flag1")) && "2".equals((String) colVo.get("flag1"))) // 统计个数
                    {
                        npercent = 0;
                    } else if ("3".equals((String) rowVo.get("flag1"))) {
                        npercent = row_percent;
                    } else if ("3".equals((String) colVo.get("flag1"))) {
                        npercent = col_percent;
                    } else {
                        npercent = row_percent >= col_percent ? row_percent : col_percent;
                    }

					if (!"4".equals((String) rowVo.get("flag1"))) {
                        rowNum++;
                    }
					if ("4".equals((String) colVo.get("flag1")) && "4".equals((String) rowVo.get("flag1"))) {
						context = "";
					} else if ("4".equals((String) colVo.get("flag1"))) {
						context = String.valueOf(rowNum);
						isFlag = true;
					} else if ("4".equals((String) rowVo.get("flag1"))) {
						context = String.valueOf(colNum);
						isFlag = true;
					}

					this.row = sheet.getRow(this.rowLayNum + this.topParamLayNum + i);
					if (row == null) {
                        row = sheet.createRow(this.rowLayNum + this.topParamLayNum + i);
                    }
					this.cell = row.createCell(Short.parseShort(String.valueOf(this.colLayNum + j)));

					switch (npercent) {
					case 0:
						this.cell.setCellStyle(styleN);
						break;
					case 1:
						this.cell.setCellStyle(styleF1);
						break;
					case 2:
						this.cell.setCellStyle(styleF2);
						break;
					case 3:
						this.cell.setCellStyle(styleF3);
						break;
					case 4:
						this.cell.setCellStyle(styleF4);
						break;
					default:
						this.cell.setCellStyle(styleF5);
						break;
					}
					/*
					 * if(isFlag)//isFlag：true 表示为序号 这段代码 没啥意义 同样是数据区域的数据 格式应该保持一致
					 * this.cell.setCellStyle(this.style);
					 */
					if (context.trim().length() > 0) {
						if (isFlag) {
                            this.cell.setCellValue(new HSSFRichTextString(context));
                        } else {
                            this.cell.setCellValue(Double.parseDouble(context));
                        }
					}
				}
			}
		}
	}

	/**
	 * 表头
	 */
	public void executeTabHeader() {
		LazyDynaBean abean = null;
		executeCell(this.topParamLayNum, (short) 0, this.topParamLayNum + this.rowLayNum - 1,
				Short.parseShort(String.valueOf(this.colLayNum - 1)), this.itemName, this.itemAlign, this.itemFontName,
				this.itemSize, this.itemEffect);

		for (int i = 0; i < this.rowInfoList.size(); i++) {
			abean = (LazyDynaBean) this.rowInfoList.get(i);
			String fontname = (String) abean.get("fontname");
			String fontsize = (String) abean.get("fontsize");
			int fonteffect = Integer.parseInt((String) abean.get("fonteffect"));
			int from_x = Integer.parseInt((String) abean.get("from_x"));
			short from_y = Short.parseShort((String) abean.get("from_y"));

			if (abean.get("to_x") != null) {
				int to_x = Integer.parseInt((String) abean.get("to_x"));
				short to_y = Short.parseShort((String) abean.get("to_y"));
				String hz = (String) abean.get("hz");
				String align = (String) abean.get("align");
				String a_lign = getItemAlign(align);
				// String a_lign="l";
				// if(align.equals("1")||align.equals("4")||align.equals("7"))
				// a_lign="c";
				// if(align.equals("2")||align.equals("5")||align.equals("8"))
				// a_lign="r";
				executeCell(from_x, from_y, to_x, to_y, hz, a_lign, fontname, fontsize, fonteffect);
			}
		}
		for (int i = 0; i < this.colInfoList.size(); i++) {
			abean = (LazyDynaBean) this.colInfoList.get(i);
			String fontname = (String) abean.get("fontname");
			String fontsize = (String) abean.get("fontsize");
			int fonteffect = Integer.parseInt((String) abean.get("fonteffect"));
			int from_x = Integer.parseInt((String) abean.get("from_x"));
			short from_y = Short.parseShort((String) abean.get("from_y"));
			if (abean.get("to_x") != null) {
				int to_x = Integer.parseInt((String) abean.get("to_x"));
				short to_y = Short.parseShort((String) abean.get("to_y"));
				String hz = (String) abean.get("hz");
				String align = (String) abean.get("align");
				String a_lign = getItemAlign(align);
				executeCell(from_x, from_y, to_x, to_y, hz, a_lign, fontname, fontsize, fonteffect);
			}
		}
	}

	/**
	 * 根据排列方式值给出相应的排列编码 wangcq 2014-11-29
	 */
	public String getItemAlign(String align) {
		String a_lign = "cc";
		if ("0".equals(align)) {
            a_lign = "ul";
        }
		if ("1".equals(align)) {
            a_lign = "uc";
        }
		if ("2".equals(align)) {
            a_lign = "ur";
        }
		if ("3".equals(align)) {
            a_lign = "dl";
        }
		if ("4".equals(align)) {
            a_lign = "dc";
        }
		if ("5".equals(align)) {
            a_lign = "dr";
        }
		if ("6".equals(align)) {
            a_lign = "cl";
        }
		if ("7".equals(align)) {
            a_lign = "cc";
        }
		if ("8".equals(align)) {
            a_lign = "cr";
        }
		return a_lign;
	}

	/**
	 * 
	 * @param a
	 *            起始 x坐标
	 * @param b
	 *            起始 y坐标
	 * @param c
	 *            终止 x坐标
	 * @param d
	 *            终止 y坐标
	 * @param content
	 *            内容
	 * @param style
	 *            表格样式
	 * @param fontEffect
	 *            字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
	public void executeCell(int a, short b, int c, short d, String content, String style, String fontName,
			String fontSize, int fontEffect) {
		try {
			HSSFRow row = sheet.getRow(a);
			if (row == null) {
                row = sheet.createRow(a);
            }
			HSSFCell cell = row.createCell(b);

			HSSFFont font = wb.createFont();
			if (fontName == null || fontName.trim().length() == 0) {
				font.setFontHeightInPoints((short) 10);
				font.setFontName("宋体");
			} else {
				// wangcq 2014-11-27 begin 输出字体完整样式
				fontEffect = fontEffect - 1;
				boolean bold, t, s, u; // 粗体、斜体、删除线、下划线
				bold = (fontEffect & 0x00000001) == 0x00000001;
				t = (fontEffect & 0x00000002) == 0x00000002;
				s = (fontEffect & 0x00000004) == 0x00000004;
				u = (fontEffect & 0x00000008) == 0x00000008;
				font.setFontHeightInPoints(Short.parseShort(fontSize));
				font.setFontName(fontName);
				if (fontEffect > 0) {
					if (bold) {
                        font.setBold(true);
                    }
					if (t) {
                        font.setItalic(true);
                    }
					if (s) {
                        font.setStrikeout(true);
                    }
					if (u) {
                        font.setUnderline((byte) 1);
                    }
				}
				// wangcq 2014-11-27 end
				// if(fontEffect==2)
				// {
				// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				// }
				// else if(fontEffect==3)
				// {
				// font.setItalic(true);
				// }
				// else if(fontEffect==4)
				// {
				// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				// font.setItalic(true);
				// }
			}
			// update by wangcq 2014-11-28 begin单元格样式需要重新创建，否则会被覆盖
			if ("uc".equalsIgnoreCase(style) || "dc".equalsIgnoreCase(style) || "cc".equalsIgnoreCase(style)) {
				// this.style.setFont(font);
				// cell.setCellStyle(this.style);
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.cloneStyleFrom(this.style);
				if (style.charAt(0) == 'u') {
                    a_style.setVerticalAlignment(VerticalAlignment.TOP);
                }
				if (style.charAt(0) == 'd') {
                    a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
                }
				if (style.charAt(0) == 'c') {
                    a_style.setVerticalAlignment(VerticalAlignment.CENTER);
                }
				a_style.setFont(font);
				a_style.setWrapText(true);// xiegh
				cell.setCellStyle(a_style);
			} else if ("ul".equalsIgnoreCase(style) || "dl".equalsIgnoreCase(style) || "cl".equalsIgnoreCase(style)) {
				// this.style_l.setFont(font);
				// cell.setCellStyle(this.style_l);
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.cloneStyleFrom(this.style_l);
				if (style.charAt(0) == 'u') {
                    a_style.setVerticalAlignment(VerticalAlignment.TOP);
                }
				if (style.charAt(0) == 'd') {
                    a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
                }
				if (style.charAt(0) == 'c') {
                    a_style.setVerticalAlignment(VerticalAlignment.CENTER);
                }
				a_style.setFont(font);
				cell.setCellStyle(a_style);
			} else if ("ur".equalsIgnoreCase(style) || "dr".equalsIgnoreCase(style) || "cr".equalsIgnoreCase(style)) {
				// this.style_r.setFont(font);
				// cell.setCellStyle(this.style_r);
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.cloneStyleFrom(this.style_r);
				if (style.charAt(0) == 'u') {
                    a_style.setVerticalAlignment(VerticalAlignment.TOP);
                }
				if (style.charAt(0) == 'd') {
                    a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
                }
				if (style.charAt(0) == 'c') {
                    a_style.setVerticalAlignment(VerticalAlignment.CENTER);
                }
				a_style.setFont(font);
				cell.setCellStyle(a_style);
			}
			// else if(style.equalsIgnoreCase("cc"))
			// {
			// this.style_cc.setFont(font);
			// cell.setCellStyle(this.style_cc);
			// HSSFCellStyle a_style = wb.createCellStyle();
			// a_style.cloneStyleFrom(this.style_cc);
			// a_style.setFont(font);
			// cell.setCellStyle(a_style);
			// }
			// wangcq 2014-11-28 end
			else if ("no_c".equalsIgnoreCase(style)) {
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.setAlignment(HorizontalAlignment.CENTER);
				a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				a_style.setFont(font);
				cell.setCellStyle(a_style);
			} else if ("no_l".equalsIgnoreCase(style)) {
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.setAlignment(HorizontalAlignment.LEFT);
				a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				a_style.setFont(font);
				cell.setCellStyle(a_style);
			} else if ("no_r".equalsIgnoreCase(style)) {
				HSSFCellStyle a_style = wb.createCellStyle();
				a_style.setAlignment(HorizontalAlignment.RIGHT);
				a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				a_style.setFont(font);
				cell.setCellStyle(a_style);
			}

			if ("no_c".equalsIgnoreCase(style)) {
                row.setHeight((short) 800);
            } else if ("no_p".equalsIgnoreCase(style)) {
                row.setHeight((short) 1600);
            } else
				/* 标识：1518 报表表头含有图片导出样式优化 xiaoyun 2014-6-8 start */
				// row.setHeight((short)400);
            {
                row.setHeight((short) 600);
            }
			/* 标识：1518 报表表头含有图片导出样式优化 xiaoyun 2014-6-8 end */

			if (content.endsWith("`")) // wangcq 2015-1-12 除去内容中最后一个`，以免导出的excel内容显示不全。
            {
                content = content.substring(0, content.length() - 1);
            }
			content = content.replaceAll("`", "\r\n");

			cell.setCellValue(new HSSFRichTextString(content));
			short b1 = b;
			while (++b1 <= d) {
				cell = row.createCell(b1);
				if (!"no_c".equals(style) && !"no_l".equals(style) && !"no_r".equals(style)) {
                    cell.setCellStyle(this.style);
                }
			}
			for (int a1 = a + 1; a1 <= c; a1++) {

				row = sheet.getRow(a1);
				if (row == null) {
                    row = sheet.createRow(a1);
                }
				b1 = b;
				while (b1 <= d) {
					cell = row.createCell(b1);
					if (!"".equals(style)) {
                        cell.setCellStyle(this.style);
                    }
					b1++;
				}
			}

			if (b <= d) {
				ExportExcelUtil.mergeCell(sheet, a, b, c, d);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 设置表格样式 */
	public HSSFCellStyle getStyle(String align, HSSFWorkbook wb) {
		HSSFCellStyle a_style = wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
		a_style.setWrapText(true);
		if ("c".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
        } else if ("l".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.LEFT);
        } else if ("r".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.RIGHT);
        } else if ("cc".equals(align)) {
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}
		return a_style;
	}

	public ArrayList createPhotoFile(String userTable, int tabid, int gridno, String flag, Connection conn)
			throws Exception {
		ArrayList list = new ArrayList();
		byte[] bytes = null;
		RowSet rowSet = null;
		InputStream in = null;
		String ext = "";
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select extendattr,content from ");
			strsql.append("" + userTable);
			strsql.append(" where tabid=");
			strsql.append(tabid);
			strsql.append(" and gridno=");
			strsql.append(gridno);
			strsql.append("");
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {
				try {
					in = rowSet.getBinaryStream("content");
					if (in == null) {
                        return list;
                    }
					String extendattr = Sql_switcher.readMemo(rowSet, "extendattr");
					ext = getExtendAttrContext(1, extendattr);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					int len;
					byte buf[] = new byte[1024];
					while ((len = in.read(buf, 0, 1024)) != -1) {
						outStream.write(buf, 0, len);
					}
					bytes = outStream.toByteArray();
					outStream.close();
				} finally {
					PubFunc.closeIoResource(in);
					PubFunc.closeResource(rowSet);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

		list.add(bytes);
		list.add(ext);

		return list;
	}

	/* 标识：1515 增加是否为表头的标识 xiaoyun 2014-6-18 start */
	// public void executeCellImage(int a,short b,int c,short d,int rwidth,int
	// rheight,HSSFPatriarch patriarch,RecordVo vo)
	public void executeCellImage(int a, short b, int c, short d, int rwidth, int rheight, HSSFPatriarch patriarch,
			RecordVo vo, boolean isTop)
	/* 标识：1515 增加是否为表头的标识 xiaoyun 2014-6-18 end */
	{

		// System.out.println(sheet.getColumnWidth(4));
		try {
			HSSFRow row = sheet.getRow(a);
			if (row == null) {
                row = sheet.createRow(a);
            }
			HSSFCell cell = row.createCell(b);
			HSSFCellStyle a_style = wb.createCellStyle();
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
			cell.setCellStyle(a_style);
			row.setHeightInPoints(rheight);
			short b1 = b;
			while (++b1 <= d) {
				cell = row.createCell(b1);

				if (!"no_c".equals(style) && !"no_l".equals(style) && !"no_r".equals(style)) {
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 start */
					if (isTop) {
						cell.setCellStyle(a_style);
					} else {
						cell.setCellStyle(this.style);
					}
					/* 标识：1518 增加是否为表头的标识 xiaoyun 2014-6-18 end */

				}
			}
			for (int a1 = a + 1; a1 <= c; a1++) {

				row = sheet.getRow(a1);
				if (row == null) {
                    row = sheet.createRow(a1);
                }
				b1 = b;
				while (b1 <= d) {
					cell = row.createCell(b1);
					if (!"".equals(style)) {
                        cell.setCellStyle(this.style);
                    }
					b1++;
				}
			}

			if (b <= d) {
				ExportExcelUtil.mergeCell(sheet, a, b, c, d);
			}

			String r_W = String.valueOf(vo.getInt("rwidth"));
			if (r_W.indexOf(".") != -1) {
                r_W = r_W.substring(0, r_W.indexOf("."));
            }
			String r_H = String.valueOf(vo.getInt("rheight"));
			if (r_H.indexOf(".") != -1) {
                r_H = r_H.substring(0, r_H.indexOf("."));
            }
			// int
			// from_x=Integer.parseInt((String)this.allParamMap.get(String.valueOf(vo.getInt("rtop"))))-1;
			ArrayList filelist;
			filelist = createPhotoFile("tpage", vo.getInt("tabid"), vo.getInt("gridno"), "P", conn);

			if (filelist.size() > 0) {
				// row.setHeight((short)2000);
				HSSFClientAnchor anchor = new HSSFClientAnchor(0, 5, 1000, 255, b, a, d, c);
				anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
				byte[] byt = (byte[]) filelist.get(0);
				String ext = (String) filelist.get(1);
				if (byt != null && (".JPG".equalsIgnoreCase(ext) || ".BMP".equalsIgnoreCase(ext))) {
                    patriarch.createPicture(anchor, this.wb.addPicture(byt, HSSFWorkbook.PICTURE_TYPE_JPEG));
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* */
		// wb.addPicture()

	}

	/**
	 * 取得tpage下的extendAttr字段的内容<image>
	 * <ext>.JPG|.BMP</ext><stretch>拉伸True|False</stretch>
	 * <transparent>透明True|False</transparent>
	 * <proportional>保持比例True|False</proportional>
	 * <background>置底True(默认值)|置顶False</background> 1表示图片，2表示拉伸，3透明，4保持比列，5置底
	 * </image>
	 *
	 * @return
	 */
	public String getExtendAttrContext(int flag, String extendAttr) {
		String temp = "";

		if (extendAttr != null && extendAttr.length() > 0) {
			if (extendAttr.indexOf("<image>") != -1) {
				if (flag == 1) {
					if (extendAttr.indexOf("<ext>") == -1) {
                        temp = ".jpg";
                    } else {
						int fromIndex = extendAttr.indexOf("<ext>");
						int toIndex = extendAttr.indexOf("</ext>");
						temp = extendAttr.substring(fromIndex + 5, toIndex).trim();

					}
				}
				if (flag == 2) {
					if (extendAttr.indexOf("<stretch>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<stretch>");
						int toIndex = extendAttr.indexOf("</stretch>");
						temp = extendAttr.substring(fromIndex + 9, toIndex).trim();

					}
				}
				if (flag == 3) {
					if (extendAttr.indexOf("<transparent>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<transparent>");
						int toIndex = extendAttr.indexOf("</transparent>");
						temp = extendAttr.substring(fromIndex + 13, toIndex).trim();

					}
				}
				if (flag == 4) {
					if (extendAttr.indexOf("<proportional>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<proportional>");
						int toIndex = extendAttr.indexOf("</proportional>");
						temp = extendAttr.substring(fromIndex + 14, toIndex).trim();

					}
				}
				if (flag == 5) {
					if (extendAttr.indexOf("<background>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<background>");
						int toIndex = extendAttr.indexOf("</background>");
						temp = extendAttr.substring(fromIndex + 12, toIndex).trim();

					}
				}
			}
		}

		return temp;
	}

	/**
	 * 子集中格式化日期字符串
	 * 
	 * @param value
	 *            日期字段值 yyyy-mm-dd
	 * @return
	 */
	private String formatDateFiledsetValue(String value, int disformat) {
		StringBuffer buf = new StringBuffer();

		String prefix = "", strext = "";

		if ("".equals(value)) {
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		} else {
			buf.append(prefix);
		}
		Date date = DateUtils.getDate(value, "yyyy-MM-dd");
		int year = DateUtils.getYear(date);
		int month = DateUtils.getMonth(date);
		int day = DateUtils.getDay(date);
		// String strv[]=exchangNumToCn(year,month,day);
		value = value.replaceAll("-", ".");
		switch (disformat) {
		case 0: // 1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 1:// 1992.02.01
			buf.append(year);
			buf.append(".");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			break;
		case 2:// 1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 3:// 1999年02月03日
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			buf.append("日");
			break;
		case 4: // 1991-12-3
			buf.append(year);
			buf.append("-");
			buf.append(month);
			buf.append("-");
			buf.append(day);
			break;
		case 5:// 1992-02-01
			buf.append(year);
			buf.append("-");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("-");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			break;
		case 6:// 1999年02月
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		}
		return buf.toString();
	}

	public String getCountid() {
		return countid;
	}

	public void setCountid(String countid) {
		this.countid = countid;
	}

	public String getYearid() {
		return yearid;
	}

	public void setYearid(String yearid) {
		this.yearid = yearid;
	}

	public String getWeekid() {
		return weekid;
	}

	public void setWeekid(String weekid) {
		this.weekid = weekid;
	}

	public ArrayList getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList resultList) {
		this.resultList = resultList;
	}

	public String getNarch() {
		return narch;
	}

	public void setNarch(String narch) {
		this.narch = narch;
	}

	public String getReverseSql() {
		return reverseSql;
	}

	public void setReverseSql(String reverseSql) {
		this.reverseSql = reverseSql;
	}

	public String getSetMap_str() {
		return setMap_str;
	}

	public void setSetMap_str(String setMap_str) {
		this.setMap_str = setMap_str;
	}

	public String getFieldItem_str() {
		return fieldItem_str;
	}

	public void setFieldItem_str(String fieldItem_str) {
		this.fieldItem_str = fieldItem_str;
	}

	public String getScanMode() {
		return scanMode;
	}

	public void setScanMode(String scanMode) {
		this.scanMode = scanMode;
	}

}
