package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.*;

public class ScoreDetailBo {
	// 反查
	private Connection conn = null;
	private UserView userView = null;
	private int columnWidth = 100;
	private String plan_id = "";
	private String method = "";
	private String template_id = "";
	private double lastScore = 0.0;
	private String object_type = "";// 考核对象类型 1:部门 2:人员 3:单位 4.部门
	String templateStatus = "0";// 0:分值模板 1：权重模板
	private String accuracy = "";// 小数的位数
	private String gather_type = "";// 采集数据的方式 0:网上 1:机读 2:网上+机读
	private String plan_type = "";// 0:不记名 1:记名
	ArrayList totalitemList = new ArrayList();
	ArrayList dtdzlist = new ArrayList();// 存储定量统一打分指标

	// 画EXCEL
	private HSSFWorkbook workbook = new HSSFWorkbook();
	private HSSFSheet sheet = null;
	private HSSFCellStyle centerstyle = null;
	private HSSFCellStyle style = null;
	private HSSFCellStyle style_l = null;
	private HSSFCellStyle style_r = null;
	private HSSFCellStyle style_title = null;
	private int rowNum = 0; // 行坐标
	private short colIndex = 0; // 纵坐标
	private HSSFRow row = null;
	private HSSFCell csCell = null;
	private int totalColNum = 0;// 所有字段总共有几列

	public ScoreDetailBo(Connection conn, UserView userView, String plan_id, String method, String template_id,
			String object_type) {
		this.conn = conn;
		this.userView = userView;
		this.plan_id = plan_id;
		this.method = method;
		this.template_id = template_id;
		this.accuracy = getAccuracy();
		this.gather_type = getGatherType();
		this.plan_type = getPlanType();
		this.templateStatus = this.getTemplateStatus();
		this.object_type = object_type;
		this.totalitemList = this.getTotalitemList(template_id);
		this.dtdzlist = this.getDtdzlist();
	}

	// 通过计划号获取计划名称
	public String getPlanName() {
		String plan_name = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select name from per_plan where plan_id='" + plan_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				plan_name = rowSet.getString("name");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plan_name;
	}

	// 通过考核对象编号获得考核对象名字
	public String getObjectName(String object_id) {
		String object_name = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select a0101 from per_object where object_id='" + object_id + "' and plan_id='" + plan_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				object_name = rowSet.getString("a0101");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object_name;
	}

	//////////////////////////////// 得到画表头所需要的全部数据///////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 模版项目列表 && 最底层项目的指标个数集合 && 表头的层数 && HashMap各项目包含的指标个数&&最底层的项目 itemsCountMap
	 * lays map bottomItemList
	 */
	public ArrayList getPerformanceStencilList(String templateID, String object_id) throws GeneralException {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		int lays = 0; // 表头的层数
		HashMap map = new HashMap();
		try {
			String item_id = "0";
			String sql = "";
			ArrayList bottomItemList = new ArrayList();
			/* 按循序得到模版项目列表 */
			ArrayList items = getItems(templateID);
			/* 取得表头的层数 */
			getLays(items);
			lays = this.a_lays;
			lays++;
			lays++;
			list.add(items);

			/* 得到各最底层项目的指标个数集合 */
			HashMap itemsCountMap = new HashMap();
			if ("1".equals(method)) {// 如果是360管理
				StringBuffer sql_ = new StringBuffer(
						"select pp.item_id,count(pp.item_id) count  from  per_template_item pi,per_template_point pp,per_point  where pi.item_id=pp.item_id");
				sql_.append(" and  pp.point_id=per_point.point_id ");
				sql_.append(" and pi.template_id='" + templateID + "' group by pp.item_id  ");
				sql = sql_.toString();
			} else {
				StringBuffer sql_ = new StringBuffer("select  item_id,count(item_id) count from p04 ");
				if ("2".equals(object_type)) {
                    sql_.append("where plan_id='" + plan_id + "' and a0100='" + object_id
                            + "' and (chg_type<>3 or chg_type is null)  group by item_id");
                } else {
                    sql_.append("where plan_id='" + plan_id + "' and b0110='" + object_id
                            + "' and (chg_type<>3 or chg_type is null)  group by item_id");
                }
				sql = sql_.toString();
			}
			rowSet = dao.search(sql);
			while (rowSet.next()) {
				itemsCountMap.put(rowSet.getString("item_id"), rowSet.getString("count"));
			}

			/* 求得map值 */
			for (Iterator t = items.iterator(); t.hasNext();) {
				int count = 0;
				String[] temp = (String[]) t.next();
				this.leafNodes = "";
				getleafCounts(temp, items, itemsCountMap);
				if (this.leafNodes.substring(1).equals(temp[0])) {
                    bottomItemList.add(temp);
                }
				this.leafNodes += "/";

				String[] a = this.leafNodes.substring(1).split("/");

				for (int i = 0; i < a.length; i++) {
					if (itemsCountMap.get(a[i]) != null) {
                        count += Integer.parseInt((String) itemsCountMap.get(a[i]));
                    } else {
						// if(this.planVo!=null&&planVo.getInt("method")==2)//计划方法是可以随意调的 在此这样限定没有意义
						// {
						for (int j = 0; j < items.size(); j++) {
							String[] atemp = (String[]) items.get(j);
							if (atemp[0].equalsIgnoreCase(a[i]) && "2".equals(atemp[5])) {
                                count++;
                            }

						}
						// }
					}
				}
				if (!a[0].equals(temp[0]) && itemsCountMap.get(temp[0]) != null) {
					count += Integer.parseInt((String) itemsCountMap.get(temp[0]));
				}
				map.put(temp[0], String.valueOf(count));
			}

			list.add(itemsCountMap);
			list.add(new Integer(lays));
			list.add(map);
			list.add(bottomItemList);

			for (int i = 0; i < bottomItemList.size(); i++) {
				String[] tt = (String[]) bottomItemList.get(i);
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 返回 得到所有画模板时用到的指标，包括空指标。
	 */
	public ArrayList getPerPointList(String templateID, String plan_id, String object_id) throws GeneralException {
		ArrayList list = new ArrayList();
		ArrayList pointList = new ArrayList();
		HashMap map2 = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			StringBuffer sb = new StringBuffer();
			if ("1".equals(method)) {// 如果是360管理
				sb.append("select pp.point_id,pp.pointname,pti.item_id ");
				sb.append("from per_template_item pti,per_template_point ptp,per_point pp ");
				sb.append("where ptp.item_id=pti.item_id and ptp.point_id=pp.point_id and template_id='" + template_id
						+ "' ");
			//	sb.append("order by pp.seq");
				sb.append("order by ptp.seq");

			} else {// 如果是目标管理
				sb.append("select p0401 point_id,p0407 pointname,item_id ");
				sb.append("from p04 ");
				if ("2".equals(object_type))// 如果考核对象是人员
                {
                    sb.append("where plan_id='" + plan_id + "' and a0100='" + object_id
                            + "' and (chg_type<>3 or chg_type is null) ");
                } else {
                    sb.append("where plan_id='" + plan_id + "' and b0110='" + object_id
                            + "' and (chg_type<>3 or chg_type is null) ");
                }
				sb.append("order by seq");
			}
			HashMap map = new HashMap();
			rowSet = dao.search(sb.toString());

			// 解决排列顺序问题
			ArrayList seqList = new ArrayList();
			ArrayList tempPointList = new ArrayList();
			while (rowSet.next()) {
				String[] temp = new String[3];
				temp[0] = rowSet.getString(1);
				temp[1] = rowSet.getString(2);
				temp[2] = rowSet.getString(3);
				tempPointList.add(temp);
				map2.put(temp[0].toLowerCase(), temp);
			}
			// 这段代码不能去掉啦，否则会影响打分界面的显示问题－－－－dengcan-2009-6-8------
			get_LeafItemList(templateID, tempPointList, seqList);
			// seqList得到指标的顺序
			for (Iterator t = seqList.iterator(); t.hasNext();) {
				String temp = (String) t.next();
				String[] atemp = (String[]) map2.get(temp);
				if (atemp == null) {
					String[] temp3 = new String[3];
					temp3[0] = "empty";
					temp3[1] = "&nbsp;";
					temp3[2] = "&nbsp;";
					pointList.add(temp3);
				} else {
					pointList.add(atemp);
				}
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		list.add(pointList);
		int pointNum = 0;
		if (!"1".equals(this.gather_type)) {// 如果是网上打分
			if ("0".equals(this.plan_type)) {// 如果是不记名
				pointNum = 2;
			} else {
				pointNum = 6;
			}
		} else {// 如果是机读
			pointNum = 1;
		}
		this.totalColNum = pointList.size() + pointNum;// 总共有几个指标
		return list;
	}

	//////////////////////////////// 画表头///////////////////////////////////////////////////////////////////////////////////////////////////

	public String getTableHeadHtml(ArrayList list, ArrayList pointList) {

		StringBuffer a_tableHtml = new StringBuffer("");

		ArrayList items = (ArrayList) list.get(0); // 模版项目列表
		HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
		int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
		HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数
		ArrayList bottomItemList = (ArrayList) list.get(4); // 模版最底层的项目
		ArrayList tempColumnList = new ArrayList();

		a_tableHtml.append("<table id='tbl' class='ListTable'>");
		/* 画第一层表头 */
		a_tableHtml.append("<thead><tr> ");
		a_tableHtml.append(getTh2("&nbsp;", lays, 1, "a", "cell_locked2"));
		if (!"1".equals(this.gather_type)) {// 当打分方式是网上打分
			if ("1".equals(this.plan_type)) {
				a_tableHtml.append(getTh2(ResourceFactory.getProperty("police.un.name"), lays, 1, "b", "cell_locked2"));
				a_tableHtml.append(getTh2(ResourceFactory.getProperty("police.um.name"), lays, 1, "c", "cell_locked2"));
				a_tableHtml.append(
						getTh2(ResourceFactory.getProperty("label.codeitemid.kk"), lays, 1, "d", "cell_locked2"));
				a_tableHtml.append(
						getTh2(ResourceFactory.getProperty("hire.employActualize.name"), lays, 1, "e", "cell_locked2"));
			}
			a_tableHtml.append(getTh2(ResourceFactory.getProperty("lable.performance.perMainBodySort"), lays, 1, "f",
					"cell_locked2"));
		}

		// 画出指标的最顶层项目
		for (Iterator t = items.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if (temp[1] == null) {
				if ("2".equals(temp[5]))// 如果是个性项目
                {
                    a_tableHtml.append(getTh(temp[3] + "*", Integer.parseInt((String) map.get(temp[0])), 2, null,
                            "header_locked"));
                } else {
                    a_tableHtml.append(
                            getTh(temp[3], Integer.parseInt((String) map.get(temp[0])), 2, null, "header_locked"));
                }
				tempColumnList.add(temp);
			}
		}
		a_tableHtml.append(getTh2(ResourceFactory.getProperty("jx.lastScore"), lays, 1, "g", "header_locked2"));
		a_tableHtml.append("</tr> \n ");

		// 画表头的中间层
		ArrayList perPointList = (ArrayList) pointList.get(0);
		HashMap pointItemMap = this.getPointItemList((ArrayList) pointList.get(0), items);
		a_tableHtml.append(getMidHeadHtml(list, tempColumnList, perPointList, pointItemMap));
		// 画指标列
		StringBuffer sequence = new StringBuffer("");
		a_tableHtml.append("<tr>");

		int character_num = 0;
		for (Iterator t = perPointList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if (temp[1].length() > character_num) {
                character_num = temp[1].length();
            }
		}
		// int hs=1;
		// if(character_num>4)
		// {
		// hs=character_num/4;
		// if(hs%4!=0)
		// hs++;
		// }
		for (Iterator t = perPointList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			a_tableHtml.append("<td valign='top' class='header_locked' align='center' width='" + character_num * 15
					+ "' id='" + temp[0] + "' nowrap>");
			// a_tableHtml.append(" width='" + character_num + "'>");
			a_tableHtml.append(temp[1]);
			a_tableHtml.append("</td>");
		}

		a_tableHtml.append("</tr>\n");
		a_tableHtml.append("</thead>\n");
		return a_tableHtml.toString();
	}

	public String getTh(String name, int lays, int opt, String idname, String className) {
		StringBuffer sb = new StringBuffer("");
		sb.append("<td class='" + className + "' valign='middle' align='center' nowrap ");
		if (idname != null && idname.length() > 0) {
            sb.append(" id='" + idname + "' ");
        }
		if (lays > 0) {
			if (opt == 1) {
                sb.append("rowspan='" + lays + "' width='" + columnWidth + "'");
            } else {
                sb.append("colspan='" + lays + "' height='35'");
            }
		}
		sb.append(" > ");
		sb.append(name);
		sb.append("</td>");
		return sb.toString();
	}

	public String getTh2(String name, int lays, int opt, String idname, String className)// 目的是让“单位名称”，“部门”这几列的显示不换行。
	{
		StringBuffer sb = new StringBuffer("");
		sb.append("<td class='" + className + "' valign='middle' align='center' nowrap");
		if (idname != null && idname.length() > 0) {
            sb.append(" id='" + idname + "' ");
        }
		if (lays > 0) {
			if (opt == 1) {
                sb.append("rowspan='" + lays + "' width='" + columnWidth + "'");
            } else {
                sb.append("colspan='" + lays + "' height='35'");
            }
		}
		sb.append(" > ");
		sb.append(name);
		sb.append("</td>");
		return sb.toString();
	}
	// public String getNumberTh(String name, int lays, int opt, String
	// idname,String className)
	// {
	// StringBuffer sb = new StringBuffer("");
	// sb.append("<td class='"+className+"' valign='middle' align='center' nowrap");
	// if (idname != null && idname.length() > 0)
	// sb.append(" id='" + idname + "' ");
	// if (lays > 0)
	// {
	// if (opt == 1)
	// sb.append("rowspan='" + lays + "' width='70'");
	// else
	// sb.append("colspan='" + lays + "' height='35'");
	// }
	// sb.append(" > ");
	// sb.append(name);
	// sb.append("</td>");
	// return sb.toString();
	// }

	// 生成表头中间层html
	public String getMidHeadHtml(ArrayList list, ArrayList tempColumnList, ArrayList perPointList,
			HashMap pointItemMap) {
		ArrayList items = (ArrayList) list.get(0); // 模版项目列表
		HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
		int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
		HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数

		StringBuffer tableHtml = new StringBuffer("");
		for (int b = 2; b < lays; b++) {
			ArrayList tempList = new ArrayList();
			tableHtml.append("<tr>");
			int d = 0;
			for (int i = 0; i < tempColumnList.size(); i++) {
				String[] temp1 = (String[]) tempColumnList.get(i);
				if (temp1[0] == null) // 如果项目为空
				{
					tableHtml.append(getTh("&nbsp;", 1, 2, null, "header_locked"));
					tempList.add(temp1);
					d++;
				} else // 如果项目不是空
				{
					int isNullItem = 0;
					if (itemsCountMap.get(temp1[0]) != null)// 第一层项目下有指标时候在子项目行上空出单元格来（找准位置）
					{
						int pointCount = Integer.parseInt((String) itemsCountMap.get(temp1[0]));
						while (pointCount-- > 0) {
							tableHtml.append(getTh("&nbsp;", 1, 2, null, "header_locked"));
							isNullItem++;
							d++;
						}
					}

					int pointNum = Integer.parseInt((String) map.get(temp1[0]));

					for (Iterator t1 = items.iterator(); t1.hasNext();) {
						String[] temp2 = (String[]) t1.next();
						if (temp2[1] != null && temp2[1].equals(temp1[0])) // 当父亲不为空，并且是该该项目的孩子
						{
							if ("2".equals(temp2[5]))// 如果是个性项目
							{
								tableHtml.append(getTh(temp2[3] + "*", Integer.parseInt((String) map.get(temp2[0])), 2,
										null, "header_locked"));
							} else {
								int pointNum2 = Integer.parseInt((String) map.get(temp2[0]));
								int selfnum = 0;
								isNullItem++;
								while (d < perPointList.size()) {
									String[] point = (String[]) perPointList.get(d);
									ArrayList pointItemList = (ArrayList) pointItemMap.get(point[0]);
									int flag = 0;
									for (Iterator t2 = pointItemList.iterator(); t2.hasNext();) {
										String[] tempItem = (String[]) t2.next();
										if (tempItem[0].equals(temp2[0])) {
                                            flag++;
                                        }
									}

									if (flag == 0) {
										tableHtml.append(getTh("&nbsp;", 1, 2, null, "header_locked"));
										String[] ttt = new String[5];
										tempList.add(ttt);
										d++;
										selfnum++;
									} else {
										tableHtml.append(getTh(temp2[3], Integer.parseInt((String) map.get(temp2[0])),
												2, null, "header_locked"));
										d += pointNum2;
										selfnum += pointNum2;
										tempList.add(temp2);
										break;
									}
								}
							}
						}
					}
					// if (isNullItem == 0&&temp1[5].equals("1")&&temp1[2]==null) {
					if (isNullItem == 0 && temp1[2] == null) {
						for (int a = 0; a < pointNum; a++) {
							tableHtml.append(getTh("&nbsp;", 1, 2, null, "header_locked"));
							String[] ttt = new String[5];
							tempList.add(ttt);
							d++;
						}
					}
				}
			}

			tableHtml.append("</tr>");
			tempColumnList = tempList;
		}
		return tableHtml.toString();
	}

	///////////////////////////// 画表头结束/////////////////////////////////////////////////////////////////////

	////////////////////////////// 得到画表体的所有数据///////////////////////////////////////////////////////////////////////////
	// 得到总的map
	public LinkedHashMap getTotalMap(String object_id) {
		RowSet rowSet = null;
		ContentDAO dao = new ContentDAO(this.conn);
		LinkedHashMap map = new LinkedHashMap();
		try {
			// 再查各个指标的数据
			StringBuffer sb = new StringBuffer();
			if (!"1".equals(this.gather_type)) {// 如果是网上打分
				sb.append(
						"select pm.b0110 unit,pm.e0122 depart,pm.e01a1 job,pm.a0101 name,pms.name body_type,pm.score totalScore,pm.mainbody_id mainbody ");
				sb.append("from per_mainbody pm,per_mainbodyset pms ");
				sb.append("where pm.body_id=pms.body_id and pm.plan_id='" + plan_id
						+ "' and pm.status=2 and pm.object_id='" + object_id + "'");
				sb.append(" order by pm.body_id,pm.b0110,pm.e0122");
			} else {
				sb.append(
						"select pm.b0110 unit,pm.e0122 depart,pm.e01a1 job,pm.a0101 name,pms.name body_type,pm.score totalScore,pm.mainbody_id mainbody ");
				sb.append("from per_mainbody pm,per_mainbodyset pms ");
				sb.append("where pm.body_id=pms.body_id and pm.plan_id='" + plan_id + "' and pm.object_id='" + object_id
						+ "'");
				sb.append(" order by pm.body_id,pm.b0110,pm.e0122");
			}

			rowSet = dao.search(sb.toString());
			// 以主体为单位把所有数据装入map中
			while (rowSet.next()) {
				ArrayList totalList = new ArrayList();// 用于存放主体数据
				HashMap pointerMap = new HashMap();// 用于存放指标数据
				String mainbody = rowSet.getString("mainbody");// 主体
				String unit = rowSet.getString("unit");
				String depart = rowSet.getString("depart");
				String job = rowSet.getString("job");
				String name = rowSet.getString("name");
				String body_type = rowSet.getString("body_type");
				// String totalScore = rowSet.getString("totalScore");
				pointerMap = this.getItemPointMap(object_id, mainbody, templateStatus);// 得到指标list

				totalList.add(unit);
				totalList.add(depart);
				totalList.add(job);
				totalList.add(name);
				totalList.add(body_type);
				totalList.add(pointerMap);
				totalList.add(String.valueOf(lastScore));// 总分
				lastScore = 0.0;// 重置
				map.put(mainbody, totalList);
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	// 得到项目信息的集合，用于显示指标数据用的。(该指标是所有打了分的指标)
	public HashMap getItemPointMap(String object_id, String mainbody_id, String templateStatus) {
		String decimal = this.getDecimal();// 格式化数据的规则
		java.text.DecimalFormat df = new java.text.DecimalFormat(decimal);
		RowSet rowSet = null;
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap map = new HashMap();
		try {
			HashMap pointRankMap = new HashMap();
			StringBuffer sb = new StringBuffer();

			StringBuffer sbDyna = new StringBuffer();
			// 求出动态权重
			if ("1".equals(method)) {// 如果是360考核
				// 先找出动态权重(只有360考核才有动态权重)

				if ("2".equals(object_type)) {// 如果是考核对象是人员
					// 要先判断动态权重类型是人员
					sbDyna.append("select point_id,rank from per_dyna_rank where plan_id='" + plan_id
							+ "' and dyna_obj='" + object_id + "' and Dyna_obj_type=4");
					rowSet = dao.search(sbDyna.toString());
					if (rowSet.next()) {// 如果有数据
						rowSet.previous();
						while (rowSet.next()) {
							pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
						}
					} else {// 如果动态权重类型不是人员
							// 按对象类别来匹配
						String duixiangleibie = this.getDuixiangLeibie(object_id);
						sbDyna.delete(0, sbDyna.length());// 清空 StringBuffer
						sbDyna.append("select point_id,rank from per_dyna_rank where plan_id='" + plan_id
								+ "' and dyna_obj='" + duixiangleibie + "' and Dyna_obj_type=5");
						rowSet = dao.search(sbDyna.toString());
						if (rowSet.next()) {// 如果有数据
							rowSet.previous();
							while (rowSet.next()) {
								pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
							}
						} else {// 如果动态权重类型不是考核对象类别
								// 从职位、部门、单位依次向上找
							String initId = this.getJobId(object_id);// 先把岗位查出来
							ArrayList objectidList = this.getObjectList(initId);
							int m = objectidList.size();
							boolean isHaveDyna = false;// 是否有动态权重 初始没有
							for (int i = 0; i < m; i++) {
								sbDyna.delete(0, sbDyna.length());// 清空 StringBuffer
								sbDyna.append("select point_id,rank from per_dyna_rank where plan_id='" + plan_id
										+ "' and dyna_obj='" + objectidList.get(i) + "'");
								rowSet = dao.search(sbDyna.toString());
								if (rowSet.next()) {// 如果终于匹配到了(早晚会匹配到的)
									rowSet.previous();
									while (rowSet.next()) {
										pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
									}
									isHaveDyna = true;
									break;// 跳出循环
								}
							}
							if (!isHaveDyna) {// 如果最后没有找到动态权重
								for (int i = 0; i < totalitemList.size(); i++) {
									sbDyna.delete(0, sbDyna.length());
									sbDyna.append("select point_id,rank from per_template_point where item_id='"
											+ totalitemList.get(i) + "'");
									rowSet = dao.search(sbDyna.toString());
									while (rowSet.next()) {
										pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
									}
								}

							}

						}
					}
				} else {// 如果考核对象是部门或单位
						// 判断动态权重类型是否为考核对象类别
						// 如果动态权重类型不是人员
						// 按对象类别来匹配
					String duixiangleibie = this.getDuixiangLeibie(object_id);
					sbDyna.delete(0, sbDyna.length());// 清空 StringBuffer
					sbDyna.append("select point_id,rank from per_dyna_rank where plan_id='" + plan_id
							+ "' and dyna_obj='" + duixiangleibie + "' and Dyna_obj_type=5");
					rowSet = dao.search(sbDyna.toString());
					if (rowSet.next()) {// 如果有数据
						rowSet.previous();
						while (rowSet.next()) {
							pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
						}
					} else {// 如果没有数据
							// 部门、单位依次向上找
						ArrayList objectidList = this.getObjectList(object_id);
						int m = objectidList.size();
						boolean isHaveDyna = false;// 是否有动态权重 初始没有
						for (int i = 0; i < m; i++) {
							sbDyna.delete(0, sbDyna.length());// 清空 StringBuffer
							sbDyna.append("select point_id,rank from per_dyna_rank where plan_id='" + plan_id
									+ "' and dyna_obj='" + objectidList.get(i) + "'");
							rowSet = dao.search(sbDyna.toString());
							if (rowSet.next()) {// 如果终于匹配到了(早晚会匹配到的)
								rowSet.previous();
								while (rowSet.next()) {
									pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
								}
								isHaveDyna = true;
								break;// 跳出循环
							}
						}
						if (!isHaveDyna) {// 如果最后没有找到动态权重
							for (int i = 0; i < totalitemList.size(); i++) {
								sbDyna.delete(0, sbDyna.length());
								sbDyna.append("select point_id,rank from per_template_point where item_id='"
										+ totalitemList.get(i) + "'");
								rowSet = dao.search(sbDyna.toString());
								while (rowSet.next()) {
									pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
								}
							}

						}
					}

				}

			} else {// 如果是目标管理，则找静态权重
				sbDyna.delete(0, sbDyna.length());// 清空 StringBuffer
				sbDyna.append("select p0401 point_id,p0415 rank from p04 where plan_id='" + plan_id + "' and ");
				if ("2".equals(object_type)) {
					sbDyna.append("a0100='" + object_id + "'");
				} else {
					sbDyna.append("b0110='" + object_id + "'");
				}
				rowSet = dao.search(sbDyna.toString());
				while (rowSet.next()) {
					pointRankMap.put(rowSet.getString("point_id"), rowSet.getString("rank"));
				}
			}

			// 求出指标的标准分
			HashMap standardMap = new HashMap();// 标准分值的map
			if ("1".equals(method)) {// 如果是360管理
				StringBuffer sbStandard360 = new StringBuffer();
				for (int i = 0; i < totalitemList.size(); i++) {
					sbStandard360.delete(0, sbStandard360.length());
					sbStandard360.append("select point_id,score from per_template_point where item_id='"
							+ totalitemList.get(i) + "'");
					rowSet = dao.search(sbStandard360.toString());
					while (rowSet.next()) {
						standardMap.put(rowSet.getString("point_id"), rowSet.getString("score"));
					}
				}
			} else {
				StringBuffer sbStandard = new StringBuffer();
				sbStandard.append(
						"select p0401 point_id,P0413 standardScore from p04 where plan_id='" + plan_id + "' and ");
				if ("2".equals(object_type)) {
					sbStandard.append("a0100='" + object_id + "'");
				} else {
					sbStandard.append("b0110='" + object_id + "'");
				}
				rowSet = dao.search(sbStandard.toString());
				while (rowSet.next()) {
					standardMap.put(rowSet.getString("point_id"), rowSet.getString("standardScore"));
				}
			}

			// 求出指标的分数
			if ("1".equals(method)) {// 如果是360管理
				sb.append("select ptx.point_id pointer,ptx.score singleScore,pg.gradedesc degree ");
				sb.append("from per_table_" + plan_id + " ptx,per_grade pg ");
				sb.append("where ptx.degree_id=pg.gradecode and ptx.point_id=pg.point_id and object_id='" + object_id
						+ "' and mainbody_id='" + mainbody_id + "'");
			} else {// 如果是目标管理
				sb.append("(select p.p0401 pointer,pte.score singleScore,pg.gradedesc degree ");
				sb.append("from per_target_evaluation pte,p04 p,per_grade pg ");
				sb.append(
						"where pte.p0400=p.p0400 and pg.point_id=p.p0401 and pg.gradecode=pte.degree_id and fromflag=2 and pte.plan_id='"
								+ plan_id + "' and pte.object_id='" + object_id + "' and pte.mainbody_id='"
								+ mainbody_id + "')");
				sb.append(" union all ");
				sb.append("(select p.p0401 pointer,pte.score singleScore,pgt.gradedesc degree ");
				sb.append("from per_target_evaluation pte,p04 p,per_grade_template pgt ");
				sb.append(
						"where pte.p0400=p.p0400 and pgt.grade_template_id=pte.degree_id and fromflag<>2 and pte.plan_id='"
								+ plan_id + "' and pte.object_id='" + object_id + "' and pte.mainbody_id='"
								+ mainbody_id + "')");
			}
			rowSet = dao.search(sb.toString());
			while (rowSet.next()) {
				String pointer = rowSet.getString("pointer");
				String singleScore = rowSet.getString("singleScore");
				String degree = rowSet.getString("degree");
				String standardScore = (String) standardMap.get(pointer);

				double singleScoreInt = Double.parseDouble(singleScore);
				double standardInt = Double.parseDouble(standardScore);
				double baifenbi = Double.parseDouble(decimal);
				if (standardInt > 1e-6)// 如果分母不是0
                {
                    baifenbi = singleScoreInt / standardInt * 100;
                }
				map.put(pointer, df.format(singleScoreInt) + "`" + df.format(baifenbi) + "`" + degree);

				// 计算总分
				if ("1".equals(templateStatus)) {// 权重模板
					double amountInt = Double.parseDouble((String) pointRankMap.get(pointer));
					lastScore += singleScoreInt * amountInt;
				} else {
                    lastScore += singleScoreInt;// 分值模板
                }
			}

			// 再加上定量统一打分指标
			for (int i = 0; i < dtdzlist.size(); i++) {
				String pointer = (String) dtdzlist.get(i);
				String singleScore = "";// 实际分值
				singleScore = getSingleScore(object_id, pointer);
				if ("isempty".equals(singleScore)) {// 如果该对象没有用到这个定量统一打分指标，跳出循环
					continue;
				}
				String degree = "";// 标度
				degree = getDegree(pointer, singleScore, (String) standardMap.get(pointer));
				String standardScore = (String) standardMap.get(pointer);
				double singleScoreInt = Double.parseDouble(singleScore);
				double standardInt = Double.parseDouble(standardScore);
				double baifenbi = Double.parseDouble(decimal);
				if (standardInt > 1e-6)// 如果分母不是0
                {
                    baifenbi = singleScoreInt / standardInt * 100;
                }
				map.put(pointer, df.format(singleScoreInt) + "`" + df.format(baifenbi) + "`" + degree);

				// 计算总分
				if ("1".equals(templateStatus)) {// 权重模板
					double amountInt = Double.parseDouble((String) pointRankMap.get(pointer));
					lastScore += singleScoreInt * amountInt;
				} else {
                    lastScore += singleScoreInt;// 分值模板
                }

			}

			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	////////////////////////////// 画表体///////////////////////////////////////////////////////////////////////////
	public String getDataHtml(HashMap totalMap, ArrayList pointTotalList, int showWays) {
		String decimal = this.getDecimal();// 格式化数据的规则
		java.text.DecimalFormat df = new java.text.DecimalFormat(decimal);
		int index = 1;// 当序号用
		StringBuffer dataHtml = new StringBuffer();
		ArrayList pointList = (ArrayList) pointTotalList.get(0);
		// 有几个主体就有几行
		Set mapSet = totalMap.keySet();
		int n = mapSet.size();
		for (Iterator t = mapSet.iterator(); t.hasNext();) {

			dataHtml.append("<tr>");

			// 先把指标列除外的列的数据显示(前5列)
			ArrayList rowList = (ArrayList) totalMap.get(t.next());
			if (index != n) {
                dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
            } else {
                dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
            }
			dataHtml.append(index);
			dataHtml.append("</td>");
			if (!"1".equals(this.gather_type)) {// 如果打分方式是网上打分
				if (index != n) {// 如果不是最后一条数据
					if ("1".equals(this.plan_type)) {// 如果是记名
						dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
						dataHtml.append(AdminCode.getCodeName("UN", (String) rowList.get(0)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
						dataHtml.append(AdminCode.getCodeName("UM", (String) rowList.get(1)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
						dataHtml.append(AdminCode.getCodeName("@K", (String) rowList.get(2)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
						dataHtml.append((String) rowList.get(3));
						dataHtml.append("</td>");
					}
					dataHtml.append("<td align='center' class='cell_locked3' nowrap>");
					dataHtml.append((String) rowList.get(4));
					dataHtml.append("</td>");
				} else {
					if ("1".equals(this.plan_type)) {// 如果是记名
						dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
						dataHtml.append(AdminCode.getCodeName("UN", (String) rowList.get(0)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
						dataHtml.append(AdminCode.getCodeName("UM", (String) rowList.get(1)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
						dataHtml.append(AdminCode.getCodeName("@K", (String) rowList.get(2)));
						dataHtml.append("</td>");
						dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
						dataHtml.append((String) rowList.get(3));
						dataHtml.append("</td>");
					}
					dataHtml.append("<td align='center' class='cell_locked4' nowrap>");
					dataHtml.append((String) rowList.get(4));
					dataHtml.append("</td>");
				}

			}

			/*
			 * 显示指标列数据 pointList中存储着所有的指标（打分的，没打分的，空指标） pointMap中存储着打过分的指标的 分数`标准分`标度
			 */
			HashMap pointMap = (HashMap) rowList.get(5);
			String alignType = "right";
			if (showWays == 2) {
                alignType = "center";
            }
			for (int i = 0; i < pointList.size(); i++) {
				String[] temp = (String[]) pointList.get(i);
				String point_id = temp[0];
				dataHtml.append("<td align='" + alignType + "' class='RecordRow2'>");
				String pointset = (String) pointMap.get(point_id);
				String to_show = "&nbsp;";// 如果没给指标打分，就什么页不显示。或者，将分数，百分制，标度分别考虑
				if (pointset != null) {// 如果(没有给该指标打分，或者指标是空的)的反面，即 如果给指标打分了
					to_show = pointset.split("`")[showWays];
				}
				// 优： 等含有冒号内容的标度，期望只截取 冒号前内容
				if (showWays == 2) {
					to_show = to_show.replaceAll("：", ":");
					int loc = to_show.indexOf(":");
					if (loc != -1) {
                        to_show = to_show.substring(0, loc);
                    }
				}
				dataHtml.append(to_show);
				dataHtml.append("</td>");
			}

			// 显示总得分数据
			dataHtml.append("<td align='right' style='border-top-width:0px;' class='RecordRow2' nowrap>");
			dataHtml.append(df.format(Double.parseDouble((String) rowList.get(6))));
			dataHtml.append("</td>");
			dataHtml.append("</tr>\n");
			index++;
		}
		dataHtml.append("</table>");
		return dataHtml.toString();
	}

	/////////////////////////////////////////////// 画出Excel//////////////////////////////////////////////////////////////////////
	public String getEvaluationTableExcel(String plan_name, String objectName, ArrayList itemTotalList,
			ArrayList pointTotalList, HashMap totalMap, int showWays) {
		String outputFile = this.userView.getUserName()+"_perEvaluation" + PubFunc.getStrg() + ".xls";
		try {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("Sheet0");
			centerstyle = style(workbook, 1);
			this.style = getStyle("c", workbook, 1);
			this.style_l = getStyle("l", workbook, 1);
			this.style_r = getStyle("r", workbook, 1);
			this.style_title = getStyle("title", workbook, 1);
			this.rowNum = 1;// 第0行显示表格主题
			getTableHeadExcel(itemTotalList, pointTotalList);// 画表头
			getDataExcel(totalMap, pointTotalList, showWays);// 画表体
			HSSFCellStyle centerstyle_no = style(workbook, 0);
			String tableTopic = plan_name + "：" + objectName + "--"
					+ ResourceFactory.getProperty("jx.show.scoreDetail");
			executeCell(0, (short) 0, 0, Short.parseShort(String.valueOf(this.totalColNum)), tableTopic,
					centerstyle_no);// 把主题显示出来

			// resetSize(1, "1");

			FileOutputStream fileOut = new FileOutputStream(
					System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outputFile);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
			workbook = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputFile;

	}

	// 画EXCEL的表头
	public void getTableHeadExcel(ArrayList list, ArrayList pointList) {
		ArrayList items = (ArrayList) list.get(0); // 模版项目列表
		HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
		int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
		HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数
		ArrayList bottomItemList = (ArrayList) list.get(4); // 模版最底层的项目
		ArrayList tempColumnList = new ArrayList();

		/* 画第一层表头 */
		int alay = lays;

		executeCell(this.rowNum, colIndex, alay, colIndex, ResourceFactory.getProperty("conlumn.mediainfo.info_id"),
				this.centerstyle);
		colIndex++;
		if (!"1".equals(this.gather_type)) {// 如果打分方式是网上打分
			if ("1".equals(this.plan_type)) {// 如果是记名
				executeCell(this.rowNum, colIndex, alay, colIndex, ResourceFactory.getProperty("police.un.name"),
						this.centerstyle);
				colIndex++;
				executeCell(this.rowNum, colIndex, alay, colIndex, ResourceFactory.getProperty("police.um.name"),
						this.centerstyle);
				colIndex++;
				executeCell(this.rowNum, colIndex, alay, colIndex, ResourceFactory.getProperty("label.codeitemid.kk"),
						this.centerstyle);
				colIndex++;
				executeCell(this.rowNum, colIndex, alay, colIndex,
						ResourceFactory.getProperty("hire.employActualize.name"), this.centerstyle);
				colIndex++;
			}
			executeCell(this.rowNum, colIndex, alay, colIndex,
					ResourceFactory.getProperty("lable.performance.perMainBodySort"), this.centerstyle);
			colIndex++;
		}
		// 画出指标的最顶层项目
		for (Iterator t = items.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if (temp[1] == null) {
				short to_col = Short
						.parseShort(String.valueOf(colIndex + Short.parseShort((String) map.get(temp[0])) - 1));
				executeCell(this.rowNum, colIndex, this.rowNum, to_col, temp[3], this.centerstyle);
				// a_tableHtml.append(getTh(temp[3], Integer.parseInt((String)
				// map.get(temp[0])), 2, null,"header_locked"));
				colIndex = to_col;
				colIndex++;
				tempColumnList.add(temp);
			}
		}
		executeCell(this.rowNum, colIndex, alay, colIndex, ResourceFactory.getProperty("jx.lastScore"),
				this.centerstyle);

		// 画表头的中间层
		ArrayList perPointList = (ArrayList) pointList.get(0);
		HashMap pointItemMap = this.getPointItemList((ArrayList) pointList.get(0), items);
		getMidHeadExcel(list, tempColumnList, perPointList, pointItemMap);

		// 画指标列
		StringBuffer sequence = new StringBuffer("");
		HSSFCellStyle pointStyle = style(this.workbook, 2);
		if (!"1".equals(this.gather_type)) {// 如果是网上打分
			if ("1".equals(this.plan_type)) {// 如果是记名
				colIndex = 6;
			} else {
				colIndex = 2;
			}
		} else {
            colIndex = 1;
        }
		for (Iterator t = perPointList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			executeCell(this.rowNum, colIndex, this.rowNum, colIndex, handleEmpty(temp[1]), pointStyle);
			colIndex++;
		}
	}

	public void getMidHeadExcel(ArrayList list, ArrayList tempColumnList, ArrayList perPointList,
			HashMap pointItemMap) {
		this.rowNum++;
		ArrayList items = (ArrayList) list.get(0); // 模版项目列表
		HashMap itemsCountMap = (HashMap) list.get(1); // 最底层项目的指标个数集合
		int lays = ((Integer) list.get(2)).intValue(); // 表头的总层数
		HashMap map = (HashMap) list.get(3); // 各项目的子项目或指标个数
		StringBuffer tableHtml = new StringBuffer("");
		for (int b = 2; b < lays; b++) {
			ArrayList tempList = new ArrayList();
			if (!"1".equals(this.gather_type)) {// 如果是网上打分
				if ("1".equals(this.plan_type)) {// 如果是记名
					colIndex = 6;
				} else {
					colIndex = 2;
				}
			} else {
                colIndex = 1;
            }
			int d = 0;
			for (int i = 0; i < tempColumnList.size(); i++) {
				String[] temp1 = (String[]) tempColumnList.get(i);
				if (temp1[0] == null) // 如果项目为空
				{
					executeCell(this.rowNum, colIndex, this.rowNum, colIndex, "", this.centerstyle);
					colIndex++;
					tempList.add(temp1);
					d++;
				} else // 如果项目不是空
				{
					int isNullItem = 0;
					int pointNum = Integer.parseInt((String) map.get(temp1[0]));

					for (Iterator t1 = items.iterator(); t1.hasNext();) {
						String[] temp2 = (String[]) t1.next();
						if (temp2[1] != null && temp2[1].equals(temp1[0])) // 当父亲不为空，并且是该该项目的孩子
						{
							if ("2".equals(temp2[5]))// 如果是个性项目
							{
								short to_col = Short.parseShort(
										String.valueOf(colIndex + Integer.parseInt((String) map.get(temp2[0])) - 1));
								executeCell(this.rowNum, colIndex, this.rowNum, to_col, temp2[3], this.centerstyle);
								colIndex = to_col;
								colIndex++;
							} else {
								int pointNum2 = Integer.parseInt((String) map.get(temp2[0]));
								int selfnum = 0;
								isNullItem++;
								while (d < perPointList.size()) {
									String[] point = (String[]) perPointList.get(d);
									ArrayList pointItemList = (ArrayList) pointItemMap.get(point[0]);
									int flag = 0;
									for (Iterator t2 = pointItemList.iterator(); t2.hasNext();) {
										String[] tempItem = (String[]) t2.next();
										if (tempItem[0].equals(temp2[0])) {
                                            flag++;
                                        }
									}

									if (flag == 0) {
										executeCell(this.rowNum, colIndex, this.rowNum, colIndex, "", this.centerstyle);
										colIndex++;
										String[] ttt = new String[5];
										tempList.add(ttt);
										d++;
										selfnum++;
									} else {
										short to_col = Short.parseShort(String
												.valueOf(colIndex + Integer.parseInt((String) map.get(temp2[0])) - 1));
										executeCell(this.rowNum, colIndex, this.rowNum, to_col, temp2[3],
												this.centerstyle);
										colIndex = to_col;
										colIndex++;
										d += pointNum2;
										selfnum += pointNum2;
										tempList.add(temp2);
										break;
									}
								}
							}
						}
					}
					if (isNullItem == 0 && temp1[2] == null) {
						for (int a = 0; a < pointNum; a++) {
							executeCell(this.rowNum, colIndex, this.rowNum, colIndex, "", this.centerstyle);
							colIndex++;
							String[] ttt = new String[5];
							tempList.add(ttt);
							d++;
						}
					}
				}
			}
			tempColumnList = tempList;
			this.rowNum++;
		}
	}

	// 画表体
	public void getDataExcel(HashMap totalMap, ArrayList pointTotalList, int showWays) {
		String decimal = this.getDecimal();// 格式化数据的规则
		java.text.DecimalFormat df = new java.text.DecimalFormat(decimal);
		int index = 1;// 当序号用
		ArrayList pointList = (ArrayList) pointTotalList.get(0);
		// 有几个主体就有几行
		Set mapSet = totalMap.keySet();
		for (Iterator t = mapSet.iterator(); t.hasNext();) {
			this.colIndex = 0;
			this.rowNum++;
			// 先把指标列除外的列的数据显示(前5列)
			ArrayList rowList = (ArrayList) totalMap.get(t.next());

			this.writeCell2(colIndex, String.valueOf(index), "l", "", this.rowNum);
			colIndex++;
			if (!"1".equals(this.gather_type)) {// 如果打分方式是网上打分
				if ("1".equals(this.plan_type)) {// 如果是记名
					this.writeCell2(colIndex, AdminCode.getCodeName("UN", (String) rowList.get(0)), "l", "",
							this.rowNum);
					colIndex++;
					this.writeCell2(colIndex, AdminCode.getCodeName("UM", (String) rowList.get(1)), "l", "",
							this.rowNum);
					colIndex++;
					this.writeCell2(colIndex, AdminCode.getCodeName("@K", (String) rowList.get(2)), "l", "",
							this.rowNum);
					colIndex++;
					this.writeCell2(colIndex, (String) rowList.get(3), "l", "", this.rowNum);
					colIndex++;
				}
				this.writeCell2(colIndex, (String) rowList.get(4), "l", "", this.rowNum);
				colIndex++;
			}
			/*
			 * 显示指标列数据 pointList中存储着所有的指标（打分的，没打分的，空指标） pointMap中存储着打过分的指标的 分数`标准分`标度
			 */
			HashMap pointMap = (HashMap) rowList.get(5);
			String alignType = "r";
			if (showWays == 2) {
				alignType = "c";
			}
			for (int i = 0; i < pointList.size(); i++) {
				String[] temp = (String[]) pointList.get(i);
				String point_id = temp[0];
				String pointset = (String) pointMap.get(point_id);
				String to_show = "";// 如果没给指标打分，就什么也不显示。或者，将分数，百分制，标度分别考虑
				if (pointset != null) {// 如果(没有给该指标打分，或者指标是空的)的反面，即 如果给指标打分了
					to_show = pointset.split("`")[showWays];
				}
				// 优： 等含有冒号内容的标度，期望只截取 冒号前内容
				if (showWays == 2) {
					to_show = to_show.replaceAll("：", ":");
					int loc = to_show.indexOf(":");
					if (loc != -1) {
                        to_show = to_show.substring(0, loc);
                    }
				}
				this.writeCell2(colIndex, to_show, alignType, "", this.rowNum);
				colIndex++;
			}

			// 显示总得分数据
			this.writeCell2(colIndex, df.format(Double.parseDouble((String) rowList.get(6))), "r", "", this.rowNum);
			index++;// 序号
		}
	}

	/* a:x坐标起始 b:y起始 c:x终止 d:y终止 **/
	public void executeCell(int a, short b, int c, short d, String content, HSSFCellStyle aStyle) {
		try {
			HSSFRow row = sheet.getRow(a);
			if (row == null) {
                row = sheet.createRow(a);
            }
			
			HSSFCell cell = row.getCell(b);
			if (cell == null) {
                cell = row.createCell(b);
            }
			
			cell.setCellValue(new HSSFRichTextString(content));
			cell.setCellStyle(aStyle);
			short b1 = b;
			while (++b1 <= d) {
				cell = row.getCell(b1);
				if (cell == null) {
                    cell = row.createCell(b1);
                }
				
				cell.setCellStyle(aStyle);
			}
			
			for (int a1 = a + 1; a1 <= c; a1++) {
				row = sheet.getRow(a1);
				if (row == null) {
                    row = sheet.createRow(a1);
                }
				
				b1 = b;
				while (b1 <= d) {
					cell = row.getCell(b1);
					if (cell == null) {
                        cell = row.createCell(b1);
                    }
					
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeCell2(short colIndex, String value, String style, String type, int a) {
		HSSFRow row = sheet.getRow(a);
		if (row == null) {
            row = sheet.createRow(a);
        }
		csCell = row.getCell(colIndex);
		if (csCell == null) {
            csCell = row.createCell(colIndex);
        }

		if ("c".equalsIgnoreCase(style)) {
            csCell.setCellStyle(this.style);
        } else if ("l".equalsIgnoreCase(style)) {
            csCell.setCellStyle(this.style_l);
        } else if ("R".equalsIgnoreCase(style)) {
            csCell.setCellStyle(this.style_r);
        }

		if ("N".equalsIgnoreCase(type)) {
			if (value == null || value.length() == 0) {
                value = "0.0";
            }
			csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			csCell.setCellValue(Double.parseDouble(value));
		} else {
			if (value == null) {
                value = "";
            }
			HSSFRichTextString richTextString = new HSSFRichTextString(value);
			csCell.setCellValue(richTextString);
		}
	}

	/////////////////////////////////////////////// 辅助函数//////////////////////////////////////////////////////////////////////
	String leafNodes = "";

	public void getleafCounts(String[] node, ArrayList items, HashMap itemsCountMap) {
		int i = 0;
		for (Iterator t = items.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			if (node[0].equals(temp[1])) {
				i++;
			}
		}
		if (i == 0) {
            leafNodes += "/" + node[0];
        } else {

			for (Iterator t = items.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if (node[0].equals(temp[1])) {
					if (itemsCountMap.get(node[0]) != null && leafNodes.indexOf("/" + node[0]) == -1) {
                        leafNodes += "/" + node[0];
                    } else if (itemsCountMap.get(temp[0]) != null && leafNodes.indexOf("/" + node[0]) == -1) {
                        leafNodes += "/" + node[0];
                    }

					getleafCounts(temp, items, itemsCountMap); // 递归

				}
			}
		}
	}

	/**
	 * 按顺序显示项目
	 */
	public ArrayList getItems(String template_id) {

		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			String sql = "select * from per_template_item where template_id='" + template_id + "' order by seq";
			rowSet = dao.search(sql);
			ArrayList items = new ArrayList();
			ArrayList bottomItemList = new ArrayList();
			ArrayList parentList = new ArrayList();
			String item_id = "0";
			while (rowSet.next()) {
				String[] temp = new String[6];
				temp[0] = rowSet.getString("item_id");
				temp[1] = rowSet.getString("parent_id");
				temp[2] = rowSet.getString("child_id");
				temp[3] = rowSet.getString("itemdesc");
				temp[4] = rowSet.getString("seq");
				temp[5] = rowSet.getString("kind") != null ? rowSet.getString("kind") : "1";
				items.add(temp);
				if (temp[1] == null) {
                    parentList.add(temp);
                }
			}
			String node = null;
			for (int i = 0; i < parentList.size(); i++) {
				String[] temp = (String[]) parentList.get(i);
				list.add(temp);
				searchIterms(items, list, temp[0]);
			}
			if (rowSet != null) {
				rowSet.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void searchIterms(ArrayList items, ArrayList list, String node) {

		for (Iterator t = items.iterator(); t.hasNext();) {
			String[] temp1 = (String[]) t.next();
			if (temp1[1] != null && temp1[1].equals(node)) {
				list.add(temp1);
				searchIterms(items, list, temp1[0]);
			}
		}
	}

	int a_lays = 0;

	public void getLays(ArrayList items) {
		for (Iterator t = items.iterator(); t.hasNext();) {
			String[] item = (String[]) t.next();
			if (item[1] == null) {
				int lay = CountLevel(item, items);
				if (a_lays < lay) {
					a_lays = lay;
				}
			}
		}
	}

	int CountLevel(String[] MyNode, ArrayList list) {
		if (MyNode == null) {
            return -1;
        }
		int iLevel = 1;
		int iMaxLevel = 0;
		ArrayList subNodeList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			String[] temp = (String[]) list.get(i);
			if (temp[1] != null && temp[1].equals(MyNode[0])) {
                subNodeList.add(temp);
            }
		}

		for (int i = 0; i < subNodeList.size(); i++) {
			iLevel = CountLevel((String[]) subNodeList.get(i), list) + 1;
			if (iMaxLevel < iLevel) {
                iMaxLevel = iLevel;
            }
		}
		return iMaxLevel;
	}

	public HashMap getPointItemList(ArrayList pointList, ArrayList items) {
		HashMap pointItemMap = new HashMap();
		for (Iterator t = pointList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();

			String item_str = temp[2];
			ArrayList pointItemList = new ArrayList();
			getPointItemList(item_str, pointItemList, items);
			pointItemMap.put(temp[0], pointItemList);
		}
		return pointItemMap;
	}

	public void getPointItemList(String item_str, ArrayList pointItemList, ArrayList items) {

		for (Iterator t1 = items.iterator(); t1.hasNext();) {
			String[] item = (String[]) t1.next();
			if (item[0].equals(item_str)) {
				pointItemList.add(item);
				if (item[1] != null) {
                    getPointItemList(item[1], pointItemList, items);
                }
			}
		}

	}

	/**
	 * 叶子项目列表
	 *
	 */
	public void get_LeafItemList(String templateID, ArrayList pointList, ArrayList seqList) {
		try {
			ArrayList itemList = getTemplateItemList(templateID);
			LazyDynaBean abean = null;
			for (int i = 0; i < itemList.size(); i++) {
				abean = (LazyDynaBean) itemList.get(i);
				String parent_id = (String) abean.get("parent_id");
				if (parent_id.length() == 0)// 从最顶层项目开始找
				{
					setLeafItemFunc(abean, pointList, itemList, seqList);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean, ArrayList pointList, ArrayList itemList, ArrayList seqList) {
		String item_id = (String) abean.get("item_id");
		String child_id = (String) abean.get("child_id");
		// 判断项目下是否有指标

		if (child_id.length() == 0)// 如果没有孩子，那么它肯定存储着指标（指标可以为空）
		{
			// this.leafItemList.add(abean);
			String itemid = (String) abean.get("item_id");
			boolean flag = false;
			for (int i = 0; i < pointList.size(); i++) {
				String[] temp = (String[]) pointList.get(i);
				String a_itemid = temp[2];
				if (itemid.equals(a_itemid)) {
					seqList.add(temp[0].toLowerCase());
					flag = true;
				}
			}
			if (!flag) {
				seqList.add("empty");
			}
			flag = false;
			return;
		}
		LazyDynaBean a_bean = null;
		for (int i = 0; i < pointList.size(); i++) {
			String[] temp = (String[]) pointList.get(i);
			String a_itemid = temp[2];
			if (item_id.equals(a_itemid)) {
				seqList.add(temp[0].toLowerCase());
			}
		}

		for (int j = 0; j < itemList.size(); j++) {
			a_bean = (LazyDynaBean) itemList.get(j);
			String parent_id = (String) a_bean.get("parent_id");
			if (parent_id.equals(item_id)) {
                setLeafItemFunc(a_bean, pointList, itemList, seqList);
            }
		}
	}

	/**
	 * 取得 模板项目记录
	 * 
	 * @return
	 */
	public ArrayList getTemplateItemList(String templateID) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = dao
					.search("select * from  per_template_item where template_id='" + templateID + "'  order by seq");
			LazyDynaBean abean = null;
			while (rowSet.next()) {
				abean = new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				abean.set("parent_id", rowSet.getString("parent_id") != null ? rowSet.getString("parent_id") : "");
				abean.set("child_id", rowSet.getString("child_id") != null ? rowSet.getString("child_id") : "");
				abean.set("template_id", rowSet.getString("template_id"));
				abean.set("itemdesc", rowSet.getString("itemdesc"));
				abean.set("seq", rowSet.getString("seq"));
				abean.set("kind", rowSet.getString("kind") != null ? rowSet.getString("kind") : "1");
				list.add(abean);
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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

		switch (styles) {

		case 0:
			HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 15);
			fonttitle.setBold(true);// 加粗
			style.setFont(fonttitle);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER);
			break;
		case 1:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER);
			break;
		case 2:
			style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setVerticalAlignment(VerticalAlignment.TOP);
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
		style.setWrapText(true);
		return style;
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
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		return font;
	}

	public HSSFCellStyle getStyle(String align, HSSFWorkbook wb, int computeFashion) {
		HSSFCellStyle a_style = wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);

		if ("c".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.CENTER);
        } else if ("l".equals(align)) {
            a_style.setAlignment(HorizontalAlignment.LEFT);
        } else if ("r".equals(align)) {
			LoadXml parameter_content = new LoadXml(this.conn, this.plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String voteScoreDecimal = (String) params.get("KeepDecimal");// 得到计算结果保留的小数位数
			voteScoreDecimal = voteScoreDecimal.trim().length() == 0 ? "0" : voteScoreDecimal;
			if (computeFashion != 1) {
                voteScoreDecimal = "0";
            }
			HSSFDataFormat df = wb.createDataFormat();
			a_style.setDataFormat(df.getFormat(decimalwidth(Integer.parseInt(voteScoreDecimal))));
			a_style.setAlignment(HorizontalAlignment.RIGHT);
		}

		else if ("title".equals(align)) {
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			a_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		}
		return a_style;
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

	public String handleEmpty(String str) {
		if ("&nbsp;".equals(str)) {
            return "";
        } else {
            return str;
        }
	}

	public String getTemplateStatus() {
		String templateStatus = "0";// 默认是分值模板
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select status from per_template where template_id='" + this.template_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				templateStatus = rowSet.getString("status");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateStatus;
	}

	// 得到小数位数
	public String getAccuracy() {
		String accuracy = "";
		Hashtable htxml = new Hashtable();
		LoadXml _loadxml = null;
		if (BatchGradeBo.planLoadXmlMap.get(plan_id) == null) {
			_loadxml = new LoadXml(this.conn, plan_id);
			BatchGradeBo.planLoadXmlMap.put(plan_id, _loadxml);
		} else {
            _loadxml = (LoadXml) BatchGradeBo.planLoadXmlMap.get(plan_id);
        }
		htxml = _loadxml.getDegreeWhole();
		accuracy = (String) htxml.get("KeepDecimal"); // 小数位

		return accuracy;
	}

	// 格式化数据的规则
	public String getDecimal() {
		String str = "0.";
		int decimalInt = Integer.parseInt(this.accuracy);
		for (int i = 0; i < decimalInt; i++) {
			str += "0";
		}
		return str;
	}

	// 得到数据采集类型
	public String getGatherType() {
		PerformanceImplementBo pb = new PerformanceImplementBo(conn);
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String gather_type = vo.getString("gather_type");
		return gather_type;
	}

	// 得到该计划是否记名
	public String getPlanType() {
		PerformanceImplementBo pb = new PerformanceImplementBo(conn);
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String plan_type = vo.getString("plan_type");
		return plan_type;
	}

	// 通过人员编号查出岗位
	public String getJobId(String object_id) {
		String job_name = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select e01a1 from per_object where object_id='" + object_id + "' and plan_id='" + plan_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				job_name = rowSet.getString("e01a1") == null ? "" : rowSet.getString("e01a1");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return job_name;
	}

	// 把组织机构编号拆分
	public ArrayList getObjectList(String initId) {
		ArrayList list = new ArrayList();
		int n = initId.length();
		for (int i = n; i >= 0; i--) {
			list.add(initId.substring(0, i));
		}
		return list;
	}

	// 得到考核对象类别
	public String getDuixiangLeibie(String object_id) {
		String str = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append(
					"select body_id from per_object where plan_id='" + plan_id + "' and object_id='" + object_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				str = rowSet.getString("body_id");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// 得到所有item
	public ArrayList getTotalitemList(String template_id) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select item_id from per_template_item where template_id='" + template_id + "'");
			rowSet = dao.search(sb.toString());
			while (rowSet.next()) {
				list.add(rowSet.getString("item_id"));
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 获取定量统一打分指标
	public ArrayList getDtdzlist() {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select pp.point_id,pp.pointkind,pp.status ");
			sb.append("from per_template_item pti,per_template_point ptp,per_point pp ");
			sb.append("where ptp.item_id=pti.item_id and ptp.point_id=pp.point_id and template_id='" + template_id
					+ "' ");
			sb.append("order by pp.seq");
			rowSet = dao.search(sb.toString());
			while (rowSet.next()) {
				int pointkind = rowSet.getInt("pointkind");
				int status = rowSet.getInt("status");
				if (pointkind == 1 && status == 1) {// 如果是定量统一打分指标
					list.add((String) rowSet.getString("point_id"));
				}
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getSingleScore(String object_id, String point_id) {
		String str = "isempty";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select C_" + point_id + " score from per_result_" + plan_id + " where object_id='" + object_id
					+ "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				if (rowSet.getString("score") != null && !"".equals(rowSet.getString("score"))) {
                    str = rowSet.getString("score");
                }
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public String getDegree(String point_id, String singleScore, String standard) {
		String str = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			StringBuffer sb = new StringBuffer();
			double singleScoreD = Double.parseDouble(singleScore);
			double standardD = Double.parseDouble(singleScore);
			double percent = 0.0;
			if (standardD > 1e-6) {
                percent = singleScoreD / standardD;
            }
			sb.append("select gradedesc from per_grade where bottom_value<= " + percent + " and top_value >=" + percent
					+ " and point_id='" + point_id + "'");
			rowSet = dao.search(sb.toString());
			if (rowSet.next()) {
				str = rowSet.getString("gradedesc");
			}
			if (rowSet != null) {
                rowSet.close();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}
