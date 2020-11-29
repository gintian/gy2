package com.hjsj.hrms.businessobject.sys.org;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:${date}:${time}
 * </p>
 * 
 * @author lilinbing
 * @version 4.0
 */
public class ProjectSet {
	/**
	 * 查询按年或按月变化的子集
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return fieldList
	 * @throws GeneralException
	 */
	public ArrayList fieldList(ContentDAO dao, String filedName,String param,Connection conn) {
		ArrayList retlist = new ArrayList();
		String sql ="";
		if("orgpre".equals(param)){//从编制管理过来
			sql = "select fieldsetid,customdesc,changeflag from fieldset where useflag=1 and (fieldsetid like 'K%' or fieldsetid like 'B%') order by fieldsetid desc";
		}else{
			sql= "select fieldsetid,customdesc,changeflag from fieldset where useflag=1  and changeflag<>0 and (fieldsetid like 'K%' or fieldsetid like 'B%') order by fieldsetid desc";
		}
		
		String[] types = { ResourceFactory.getProperty("org.maip.general"),
				ResourceFactory.getProperty("datestyle.month"),
				ResourceFactory.getProperty("datestyle.year") };
		ArrayList dylist = null;
		String pos_set="";
		String unit_set="";
		if("orgpre".equals(param)){
			RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);
			
			if(ps_workout_vo!=null)
			{
			  String  ps_workout=ps_workout_vo.getString("str_value");
			  ps_workout=ps_workout!=null?ps_workout:"";
			  if(ps_workout.length()>0){
				  String strs[]=ps_workout.split("\\|");//K01|K0114,K0111
				  pos_set=strs[0];
			  }
			}
			PosparameXML pos = new PosparameXML(conn);
			unit_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
			
		}
			
			try {
				dylist = dao.searchDynaList(sql);
				for (Iterator it = dylist.iterator(); it.hasNext();) {
					DynaBean dynabean = (DynaBean) it.next();
					if (dynabean.get("fieldsetid").toString().equals(filedName)
							&& !"A".equals(dynabean.get("fieldsetid").toString().substring(0,
									1))) {
						int type = Integer.parseInt(dynabean.get("changeflag")
								.toString());
						CommonData obj = new CommonData(dynabean.get("fieldsetid")
								.toString(), "[" + types[type] + "] "
								+ dynabean.get("customdesc").toString());
						if("orgpre".equals(param)){
							if(dynabean.get("fieldsetid").toString().equalsIgnoreCase(unit_set)||dynabean.get("fieldsetid").toString().equalsIgnoreCase(pos_set)){
								retlist.add(obj);
							}
						}else{
							retlist.add(obj);
						}
					}
				}
				for (Iterator it = dylist.iterator(); it.hasNext();) {
					DynaBean dynabean = (DynaBean) it.next();
					if("K01".equalsIgnoreCase(dynabean.get("fieldsetid").toString())|| "B01".equalsIgnoreCase(dynabean.get("fieldsetid").toString())){
						continue;
					}
					if (!dynabean.get("fieldsetid").toString().equals(filedName)
							&& !"A".equals(dynabean.get("fieldsetid").toString().substring(0,
									1))) {
						int type = Integer.parseInt(dynabean.get("changeflag")
								.toString());
						CommonData obj = new CommonData(dynabean.get("fieldsetid")
								.toString(), "[" + types[type] + "] "
								+ dynabean.get("customdesc").toString());
						if("orgpre".equals(param)){
							if(dynabean.get("fieldsetid").toString().equalsIgnoreCase(unit_set)||dynabean.get("fieldsetid").toString().equalsIgnoreCase(pos_set)){
								retlist.add(obj);
							}
						}else{
							retlist.add(obj);
						}
					}
				}
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return retlist;
	}

	/**
	 * 查询按年或按月变化的子集
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return fieldList
	 * @throws GeneralException
	 */
	public ArrayList fieldSetList(ContentDAO dao, String filedName,String param,Connection conn) {
		ArrayList retlist = new ArrayList();
		String sql="";
		if("orgpre".equals(param)){//从编制管理过来
			sql = "select fieldsetid,customdesc,changeflag from fieldset where useflag=1 and (fieldsetid like 'B%') order by fieldsetid desc";//fieldsetid like 'K%' or 
		}else{
			sql = "select fieldsetid,customdesc,changeflag from fieldset where useflag=1  and changeflag<>0 and (fieldsetid like 'B%') order by fieldsetid desc";//fieldsetid like 'K%' or 
		}
		String[] types = { ResourceFactory.getProperty("org.maip.general"),
				ResourceFactory.getProperty("datestyle.month"),
				ResourceFactory.getProperty("datestyle.year") };
		ArrayList dylist = null;
		String pos_set="";
		String unit_set="";
		if("orgpre".equals(param)){
			RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);
			
			if(ps_workout_vo!=null)
			{
			  String  ps_workout=ps_workout_vo.getString("str_value");
			  ps_workout=ps_workout!=null?ps_workout:"";
			  if(ps_workout.length()>0){
				  String strs[]=ps_workout.split("\\|");//K01|K0114,K0111
				  pos_set=strs[0];
			  }
			}
			PosparameXML pos = new PosparameXML(conn);
			unit_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
			
		}
		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("fieldsetid").toString().equals(filedName)
						&& !"A".equals(dynabean.get("fieldsetid").toString().substring(0,
								1))) {
					int type = Integer.parseInt(dynabean.get("changeflag")
							.toString());
					CommonData obj = new CommonData(dynabean.get("fieldsetid")
							.toString(), dynabean.get("customdesc").toString());
					if("orgpre".equals(param)){
						if(dynabean.get("fieldsetid").toString().equalsIgnoreCase(unit_set)||dynabean.get("fieldsetid").toString().equalsIgnoreCase(pos_set)){
							retlist.add(obj);
						}
					}else{
						retlist.add(obj);
					}
				}
			}
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (!dynabean.get("fieldsetid").toString().equals(filedName)
						&& !"A".equals(dynabean.get("fieldsetid").toString().substring(0,
								1))) {
					int type = Integer.parseInt(dynabean.get("changeflag")
							.toString());
					CommonData obj = new CommonData(dynabean.get("fieldsetid")
							.toString(), dynabean.get("customdesc").toString());
					if("orgpre".equals(param)){
						if(dynabean.get("fieldsetid").toString().equalsIgnoreCase(unit_set)||dynabean.get("fieldsetid").toString().equalsIgnoreCase(pos_set)){
							retlist.add(obj);
						}
					}else{
						retlist.add(obj);
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 查询按年或按月变化子集的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return usedList
	 * @throws GeneralException
	 */
	public ArrayList usedList(ContentDAO dao, String filedName, String type) {
		ArrayList retlist = new ArrayList();
		String sql = "select itemid,itemdesc,expression from fielditem where useflag=1 and fieldsetid='"
				+ filedName + "' and expression is not null order by displayid";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("expression") != null
						&& dynabean.get("expression").toString().length() > 0) {
					if (dynabean.get("expression").toString().substring(0, 1)
							.equals(type)) {
						CommonData obj = new CommonData(dynabean.get("itemid")
								.toString(), dynabean.get("itemdesc")
								.toString());
						retlist.add(obj);
					}
					if ("2".equals(type)) {
						if ("4".equals(dynabean.get("expression").toString().substring(0,
								1))) {
							CommonData obj = new CommonData(dynabean.get(
									"itemid").toString(), dynabean.get(
									"itemdesc").toString());
							retlist.add(obj);
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 查询按年或按月变化子集的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return usedList
	 * @throws GeneralException
	 */
	public ArrayList usedList(ContentDAO dao,UserView uv,String filedName, String type) {
		ArrayList retlist = new ArrayList();
		String sql = "select itemid,itemdesc,expression from fielditem where useflag=1 and fieldsetid='"
				+ filedName + "' and expression is not null order by displayid";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("expression") != null
						&& dynabean.get("expression").toString().length() > 0) {
					if (dynabean.get("expression").toString().substring(0, 1)
							.equals(type)) {
						if("2".equals(uv.analyseFieldPriv((String)dynabean.get("itemid")))){
							CommonData obj = new CommonData(dynabean.get("itemid")
									.toString(), dynabean.get("itemdesc")
									.toString());
							retlist.add(obj);
						}
					}
					if ("2".equals(type)) {
						if ("4".equals(dynabean.get("expression").toString().substring(0,
								1))) {
							if("2".equals(uv.analyseFieldPriv((String)dynabean.get("itemid")))){
								CommonData obj = new CommonData(dynabean.get(
								"itemid").toString(), dynabean.get(
								"itemdesc").toString());
								retlist.add(obj);
							}
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}


	/**
	 * 查询按年或按月变化子集的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return usedList
	 * @throws GeneralException
	 */
	public ArrayList usedSummayList(ContentDAO dao, String filedsetid) {
		ArrayList retlist = new ArrayList();
		String sql = "select itemid,itemdesc,expression from fielditem where useflag=1 and fieldsetid='"
				+ filedsetid + "' and expression is not null order by displayid";
		ArrayList dylist = null;

		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("expression") != null
						&& dynabean.get("expression").toString().length() > 0) {
					String exp = (String) dynabean.get("expression");
					String [] exprArr=exprDecom(exp);
					if(exprArr!=null&&exprArr.length>0){
						if(exprArr[0]!=null&&!"2".equals(exprArr[0])&&!"1".equals(exprArr[0])){
							exp = exp.substring(exp.indexOf("-") + 1);
							String itemArr[] = exp.split("/");
							if (itemArr.length != 3) {
                                continue;
                            }
							String arr[] = itemArr[1].split("=");
							CommonData obj = new CommonData(dynabean.get("itemid")
									.toString(), dynabean.get("itemdesc").toString()
									+ "  [目标子集:" + itemArr[0] + "    目标指标:" + arr[0]
									+ "]");
							retlist.add(obj);
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	private String[] exprDecom(String expr){
		expr=expr!=null?expr:"";
		String[] exprArr = expr.split("::");
		return exprArr;
	}
	/**
	 * 查询按年或按月变化子集的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return usedList
	 * @throws GeneralException
	 */
	public ArrayList usedSummayList(ContentDAO dao,UserView uv,String filedsetid) {
		ArrayList retlist = new ArrayList();
		String sql = "select itemid,itemdesc,expression from fielditem where useflag=1 and fieldsetid='"
				+ filedsetid + "' and expression is not null order by displayid";
		ArrayList dylist = null;

		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("expression") != null
						&& dynabean.get("expression").toString().length() > 0) {
					if("2".equals(uv.analyseFieldPriv((String)dynabean.get("itemid")))){
						String exp = (String) dynabean.get("expression");
						String [] exprArr=exprDecom(exp);
						if(exprArr!=null&&exprArr.length>0){
							if(exprArr[0]!=null&&!"2".equals(exprArr[0])&&!"1".equals(exprArr[0])){
								
						exp = exp.substring(exp.indexOf("-") + 1);
						String itemArr[] = exp.split("/");
						if (itemArr.length != 3) {
                            continue;
                        }
						String arr[] = itemArr[1].split("=");
						CommonData obj = new CommonData(dynabean.get("itemid")
								.toString(), dynabean.get("itemdesc").toString()
								+ "  [目标子集:" + itemArr[0] + "    目标指标:" + arr[0]		                                              + "]");
						retlist.add(obj);
							}
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 查询按年或按月变化并且没有被子集添加的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList addusedList(ContentDAO dao, String filedName) {
		ArrayList retlist = new ArrayList();
		String sql = "select * from fielditem where useflag=1 and fieldsetid = '"
				+ filedName + "' and itemtype='N'";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if (dynabean.get("expression") == null
						|| dynabean.get("expression").toString().length() < 2) {
					if (!dynabean
							.get("itemdesc")
							.toString()
							.equals(
									ResourceFactory
											.getProperty("hmuster.label.counts"))) {
						CommonData obj = new CommonData(dynabean.get("itemid")
								.toString(), dynabean.get("itemdesc")
								.toString());
						retlist.add(obj);
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 查询按年或按月变化并且没有被子集添加的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList addusedList(ContentDAO dao, String filedName, String type) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql
				.append("select * from fielditem where useflag=1 and fieldsetid = '");
		sql.append(filedName);
		if (!"1".equals(type)) {
            sql.append("' and itemtype='N'");
        } else {
            sql.append("'");
        }
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid = (String)dynabean.get("itemid");
				if(itemid.equalsIgnoreCase(filedName+"Z0")){
					continue;
				}
				if (dynabean.get("expression") == null
						|| dynabean.get("expression").toString().length() < 2) {
					if (!dynabean
							.get("itemdesc")
							.toString()
							.equals(
									ResourceFactory
											.getProperty("hmuster.label.counts"))) {
						CommonData obj = new CommonData(dynabean.get("itemid")
								.toString(), dynabean.get("itemdesc")
								.toString());
						retlist.add(obj);
					}
				} else {
					if ("K".equalsIgnoreCase(filedName.substring(0, 1))) {
						String expre = (String) dynabean.get("expression");
						if ("3".equals(type)) {
							if ("2".equals(expre.substring(0, 1))
									|| "3::-".equals(expre)) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						} else {
							if ("3::-".equals(expre)) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						}
					} else {
						if ("3".equals(type)) {
							String expre = (String) dynabean.get("expression");
							if ("2".equals(expre.substring(0, 1))) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}
	
	/**
	 * 查询按年或按月变化并且没有被子集添加的项目
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList addusedList(ContentDAO dao, String filedName, String type,Connection conn) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql
				.append("select * from fielditem where useflag=1 and fieldsetid = '");
		sql.append(filedName);
		if (!"1".equals(type)) {
            sql.append("' and itemtype='N'");
        } else {
            sql.append("'");
        }
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);
			String zw_set ="";
			if(ps_workout_vo!=null)
			{
			  String  ps_workout=ps_workout_vo.getString("str_value");
			  StringTokenizer str=new StringTokenizer(ps_workout,"|");//K01|K0114,K0111
			  if(str.hasMoreTokens())
			  {
				  zw_set = str.nextToken().toUpperCase();
			  }
			}
			String ps_parttime="0";
			if(zw_set.equalsIgnoreCase(filedName)){
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
					/**兼职参数*/
					String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
					//兼职岗位占编 1：占编	
					String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
					
					if("true".equals(flag)&&"1".equals(takeup_quota)){
						ps_parttime="1";
					}
			}
			String zw_ps_workparttime="";
			if("1".equals(ps_parttime)){
				 RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
					if(ps_workparttime_vo!=null){
						zw_ps_workparttime = ps_workparttime_vo.getString("str_value").toUpperCase();
					}
			 }
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid = (String)dynabean.get("itemid");
				if(itemid.equalsIgnoreCase(filedName+"Z0")){
					continue;
				}
				if (dynabean.get("expression") == null
						|| dynabean.get("expression").toString().length() < 2) {
					if (!dynabean
							.get("itemdesc")
							.toString()
							.equals(
									ResourceFactory
											.getProperty("hmuster.label.counts"))/*&&!zw_ps_workparttime.equalsIgnoreCase(dynabean.get("itemid").toString())*/) {
						CommonData obj = new CommonData(dynabean.get("itemid")
								.toString(), dynabean.get("itemdesc")
								.toString());
						retlist.add(obj);
					}
				} else {
					if ("K".equalsIgnoreCase(filedName.substring(0, 1))) {
						String expre = (String) dynabean.get("expression");
						if ("3".equals(type)) {
							if ("2".equals(expre.substring(0, 1))
									|| "3::-".equals(expre)) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						} else {
							if ("3::-".equals(expre)) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						}
					} else {
						if ("3".equals(type)) {
							String expre = (String) dynabean.get("expression");
							if ("2".equals(expre.substring(0, 1))) {
								CommonData obj = new CommonData(dynabean.get(
										"itemid").toString(), dynabean.get(
										"itemdesc").toString());
								retlist.add(obj);
							}
						}
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 定义计算子集中的参考项目list
	 * 
	 * @param dao
	 * @param filedName
	 *            子集id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList fielditemList(ContentDAO dao, String filedName) {
		ArrayList retlist = new ArrayList();
		// StringBuffer sql= new StringBuffer();
		ArrayList unitFieldList = new ArrayList();
		if ("B".equalsIgnoreCase(filedName.substring(0, 1))) {
			unitFieldList = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		} else if ("K".equalsIgnoreCase(filedName.substring(0, 1))) {
			unitFieldList = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
		} else {
			unitFieldList = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		}
		try {
			CommonData obj1 = new CommonData("", "");
			retlist.add(obj1);
			for (int a = 0; a < unitFieldList.size(); a++) {
				FieldItem afielditem = (FieldItem) unitFieldList.get(a);
				if ("N".equalsIgnoreCase(afielditem.getItemtype())
						&& !filedName.equalsIgnoreCase(afielditem
								.getFieldsetid())) {
                    continue;
                }
				if ("M".equalsIgnoreCase(afielditem.getItemtype())) {
                    continue;
                }
				CommonData obj = new CommonData(afielditem.getItemdesc(),
						afielditem.getItemdesc());
				retlist.add(obj);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 查询按年或按月变化并且没有被子集添加的项目
	 * 
	 * @param dao
	 * @param fielditemid
	 *            指标id
	 * @param type
	 *            项目类型(1计算项目,2统计项目,3汇总项目)
	 * @throws GeneralException
	 */
	public void addProject(ContentDAO dao, String fielditemid, String type) {

		String expression = "";
		if ("1".equals(type)) {
			expression = "1::0::|0::|";
		} else if ("2".equals(type)) {
			expression = "2::0::|0::|::1";
		} else {
			expression = "3::-";
		}
		String sql = "update fielditem set expression='" + expression
				+ "' where  itemid = '" + fielditemid + "'";
		try {
			dao.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 保存设置的项目
	 * 
	 * @param dao
	 * @param fielditemid
	 *            指标id
	 * @param expression
	 *            设置的项目条件
	 * @throws GeneralException
	 */
	public void saveProject(ContentDAO dao, String fielditemid,
			String expression) {
		String sql = "update fielditem set expression='" + expression
				+ "' where  itemid = '" + fielditemid.toUpperCase() + "'";
		try {
			dao.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList statList() {
		ArrayList statlist = new ArrayList();
		CommonData obj = new CommonData("0", ResourceFactory
				.getProperty("org.maip.number.seek"));
		statlist.add(obj);
		obj = new CommonData("1", ResourceFactory.getProperty("kq.formula.sum"));
		statlist.add(obj);
		obj = new CommonData("2", ResourceFactory.getProperty("kq.formula.min"));
		statlist.add(obj);
		obj = new CommonData("3", ResourceFactory.getProperty("kq.formula.max"));
		statlist.add(obj);
		obj = new CommonData("4", ResourceFactory
				.getProperty("kq.formula.average"));
		statlist.add(obj);

		return statlist;
	}

	public ArrayList condList() {
		ArrayList statlist = new ArrayList();
		CommonData obj = new CommonData("", "");
		statlist.add(obj);
		obj = new CommonData("个数", "个数");
		statlist.add(obj);
		obj = new CommonData("总和", "总和");
		statlist.add(obj);
		obj = new CommonData(ResourceFactory.getProperty("kq.formula.min"),
				ResourceFactory.getProperty("kq.formula.min"));
		statlist.add(obj);
		obj = new CommonData(ResourceFactory.getProperty("kq.formula.max"),
				ResourceFactory.getProperty("kq.formula.max"));
		statlist.add(obj);
		obj = new CommonData(ResourceFactory.getProperty("kq.formula.average"),
				ResourceFactory.getProperty("kq.formula.average"));
		statlist.add(obj);

		return statlist;
	}

	public ArrayList rangeList() {
		ArrayList statlist = new ArrayList();
		CommonData obj = new CommonData("", "");
		statlist.add(obj);
		obj = new CommonData("当前列表", "当前列表");
		statlist.add(obj);
		obj = new CommonData("当前人员库", "当前人员库");
		statlist.add(obj);

		return statlist;
	}

	/**
	 * 获取fielitem库中已构库的值，并形成selsect选择框架
	 * 
	 * @param type
	 *            类型
	 */
	public String selectOption(String type) {
		StringBuffer numer = new StringBuffer();
		ArrayList listset = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		for (int i = 0; i < listset.size(); i++) {
			FieldSet fieldset = (FieldSet) listset.get(i);
			ArrayList listitem = DataDictionary.getFieldList(fieldset
					.getFieldsetid(), Constant.USED_FIELD_SET);
			for (int j = 0; j < listitem.size(); j++) {
				FieldItem item = (FieldItem) listitem.get(j);
				String itemtype = item.getItemtype();
				if (itemtype.equals(type)) {
					numer.append("<option value='");
					numer.append(item.getItemdesc());
					numer.append("'>");
					numer.append(item.getItemdesc());
					numer.append("</option>");
				}
			}
		}
		return numer.toString();
	}

	/**
	 * 查询子集
	 * 
	 * @param dao
	 * @param itemtype
	 *            子集数据类型
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList functionList(ContentDAO dao, String type) {
		ArrayList retlist = new ArrayList();
		ArrayList listset = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		if ("code".equalsIgnoreCase(type)) {
			for (int i = 0; i < listset.size(); i++) {
				FieldSet fieldset = (FieldSet) listset.get(i);
				ArrayList listitem = DataDictionary.getFieldList(fieldset
						.getFieldsetid(), Constant.USED_FIELD_SET);
				if (listitem != null) {
					for (int j = 0; j < listitem.size(); j++) {
						FieldItem item = (FieldItem) listitem.get(j);
						if (item.isCode()) {

							CommonData obj = new CommonData(item.getItemid()
									+ ":" + item.getItemdesc(), "("
									+ fieldset.getFieldsetid() + ")-"
									+ item.getItemdesc());
							retlist.add(obj);
						}
					}
				}
			}
		} else {
			for (int i = 0; i < listset.size(); i++) {
				FieldSet fieldset = (FieldSet) listset.get(i);
				ArrayList listitem = DataDictionary.getFieldList(fieldset
						.getFieldsetid(), Constant.USED_FIELD_SET);
				if (listitem != null) {
					for (int j = 0; j < listitem.size(); j++) {
						FieldItem item = (FieldItem) listitem.get(j);
						String itemtype = item.getItemtype();
						if (itemtype.equals(type)) {
							CommonData obj = new CommonData(item.getItemid()
									+ ":" + item.getItemdesc(), "("
									+ fieldset.getFieldsetid() + ")-"
									+ item.getItemdesc());
							retlist.add(obj);
						}
					}
				}
			}
		}
		CommonData obj1 = new CommonData("", "");
		retlist.add(0, obj1);
		return retlist;
	}

	/**
	 * 查询薪资项目子集
	 * 
	 * @param dao
	 * @param itemtype
	 *            子集数据类型
	 * @param salaryid
	 *            薪资id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList functionList(ContentDAO dao, String itemtype,
			String salaryid, String tabid) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if ("all".equalsIgnoreCase(salaryid)) {
			sql
					.append("select fieldsetid,itemid,itemdesc,sortid from salaryset where itemtype='");
			if ("code".equals(itemtype)) {
				sql.append("A' and codesetid<>'0'");
			} else {
				sql.append(itemtype + "'");
			}
			sql
					.append(" group by fieldsetid,itemid,itemdesc,sortid order by sortid");
		} else {
			sql
					.append("select fieldsetid,itemid,itemdesc,sortid from salaryset where salaryid='");
			sql.append(salaryid);
			sql.append("' and itemtype='");
			if ("code".equals(itemtype)) {
				sql.append("A' and codesetid<>'0");
			} else {
				sql.append(itemtype);
			}
			sql
					.append("' group by fieldsetid,itemid,itemdesc,sortid order by sortid ");
		}
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid = dynabean.get("itemid").toString();
				String fieldsetid = dynabean.get("fieldsetid").toString();
				// FieldSet fieldset = new FieldSet(fieldsetid);
				CommonData obj = new CommonData(itemid + ":"
						+ dynabean.get("itemdesc").toString(), "(" + fieldsetid
						+ ")-" + dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommonData obj1 = new CommonData("", "");
		retlist.add(0, obj1);
		return retlist;
	}

	/**
	 * 临时变量项目子集
	 * 
	 * @param dao
	 * @param itemtype
	 *            子集数据类型
	 * @param salaryid
	 *            薪资id
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList midList(ContentDAO dao, String itemtype, String salaryid,
			String tabid) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if ("all".equalsIgnoreCase(salaryid)) {
			sql
					.append("select cname,ntype,chz,sorting from midvariable where ");
			if (tabid.trim().length() > 0) {
				sql.append("Templetid=");
				sql.append(tabid + " and ");
			}
			sql.append(" ntype=");
			sql.append(strTonum(itemtype));
			sql.append(" group by cname,ntype,chz,sorting order by sorting");
		} else {
			sql
					.append("select cname,ntype,chz,sorting from midvariable where ");
			if (tabid.trim().length() > 0) {
				sql.append("templetid=");
				sql.append(tabid);
			} else {
				sql.append("cstate=");
				sql.append(salaryid);
			}
			sql.append(" and ntype=");
			sql.append(strTonum(itemtype));
			sql.append(" group by cname,ntype,chz,sorting order by sorting");
		}
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				if ("4".equalsIgnoreCase(dynabean.get("ntype").toString())) {
					CommonData obj = new CommonData(dynabean.get("ntype")
							.toString()
							+ ":" + dynabean.get("chz").toString(), dynabean
							.get("chz").toString());
					retlist.add(obj);
				} else {
					CommonData obj = new CommonData(dynabean.get("cname")
							.toString()
							+ ":" + dynabean.get("chz").toString(), dynabean
							.get("chz").toString());
					retlist.add(obj);
				}
			}
			CommonData obj = new CommonData("", "");
			retlist.add(0, obj);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retlist;
	}

	/**
	 * 查询标准表
	 * 
	 * @param dao
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList standList(ContentDAO dao,UserView userView) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql
				.append("select id,name,hfactor,vfactor,s_hfactor,s_vfactor,item from gz_stand_history");
		sql
				.append(" where pkg_id in(select pkg_id from gz_stand_pkg where status='1')");
		String unitid = "XXXX";
		StringBuffer tt = new StringBuffer();
		if(userView.isSuper_admin())
		{
			unitid="";
			tt.append(" or 1=1 ");
		}
		else
		{
			if(userView.getUnit_id()!=null&&userView.getUnit_id().trim().length()>2)
			{
				if(userView.getUnit_id().length()==3)
				{
					unitid="";
					tt.append(" or 1=1 ");
				}
				else
				{
			    	unitid=userView.getUnit_id();
			    	String[] unit_arr = unitid.split("`");
			    	for(int i=0;i<unit_arr.length;i++)
			    	{
			    		 
			    		if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2) {
                            continue;
                        }
			    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
			    	}
				}
			}
			else{
				if(userView.getManagePrivCode()!=null&&userView.getManagePrivCode().trim().length()>0)
				{
					if(userView.getManagePrivCodeValue()==null|| "".equals(userView.getManagePrivCodeValue().trim()))
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else{
				    	unitid=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
				    	tt.append(" or b0110 like '%,"+userView.getManagePrivCodeValue()+"%'");
					}
				}
				else//没有范围
				{
					
				}
			}
		}
		if(tt.toString().length()>0)
		{
			if(userView.isSuper_admin()|| "".equals(unitid))
			{
				
			}else
			{
				sql.append(" and (");
				sql.append("("+tt.toString().substring(3)+")");
				sql.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "''")+"=''");
				sql.append(")");
			}
		}
		if("XXXX".equals(unitid))
		{
			sql.append(" and "+Sql_switcher.isnull("b0110", "''")+"=''");
		}
		sql.append(" order by id");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String hfactor = dynabean.get("hfactor").toString();
				hfactor = hfactor != null ? hfactor : "";
				String vfactor = dynabean.get("vfactor").toString();
				vfactor = vfactor != null ? vfactor : "";
				String s_hfactor = dynabean.get("s_hfactor").toString();
				s_hfactor = s_hfactor != null ? s_hfactor : "";
				String s_vfactor = dynabean.get("s_vfactor").toString();
				s_vfactor = s_vfactor != null ? s_vfactor : "";

				CommonData obj = new CommonData(hfactor + ":" + vfactor + ":"
						+ s_hfactor + ":" + s_vfactor + ":"
						+ dynabean.get("name").toString() + ":"
						+ dynabean.get("item").toString() + ":"
						+ dynabean.get("id").toString(), dynabean.get("id")
						.toString()
						+ "." + dynabean.get("name").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommonData obj1 = new CommonData("", "");
		retlist.add(0, obj1);
		return retlist;
	}
	/**
	 * 归属单位子集，应用于 取上月实发工资人数
	 * author GuoFeng
	 * @param dao
	 * @throws GeneralException
	 */
	public ArrayList belongtounitList(ContentDAO dao,UserView userView){
		ArrayList list = new ArrayList();
		try{
			CommonData obj1 = new CommonData("", "");
			list.add(obj1);
			
			StringBuffer sb = new StringBuffer();
			sb.append("select distinct itemid,itemdesc from salaryset where 1=1 and (codesetid ='UM' or codesetid='UN')");
			RowSet rs = dao.search(sb.toString());
			while(rs.next()){
				String codeitemid = rs.getString("itemid")==null?"":rs.getString("itemid");
				if("".equals(codeitemid)) {
                    continue;
                }
				String codename = rs.getString("itemdesc")==null?"":rs.getString("itemdesc");
				CommonData obj = new CommonData(codename, codename);
				list.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 查询标准表
	 * 
	 * @param dao
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList standidList(ContentDAO dao,UserView userView) {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql
				.append("select id,name,hfactor,vfactor,s_hfactor,s_vfactor,item from gz_stand");
		sql.append(" where ((nullif(hfactor,'') is not null or nullif(s_hfactor,'') is not null)");
		sql.append(" and  (nullif(vfactor,'') is not null or nullif(s_vfactor,'') is not null ) ) ");
		sql
				.append(" and ((nullif(hfactor,'') is not null or hfactor is  null) or (nullif(s_hfactor,'') is not null or s_hfactor is null))");
		sql
				.append(" and ((nullif(vfactor,'') is not null or vfactor is null) or (nullif(s_vfactor,'') is not null or s_vfactor is null))");
		sql.append(" and nullif(item,'') is not null and item is not null");
		//归属单位限制
		StringBuffer sql2 = new StringBuffer();
		sql2
				.append("select id from gz_stand_history");
		sql2
				.append(" where pkg_id in(select pkg_id from gz_stand_pkg where status='1')");
		
		ArrayList dylist = null;
		try {
			try {
				
			String unitid = "XXXX";
			StringBuffer tt = new StringBuffer();
			if(userView.isSuper_admin())
			{
				unitid="";
				tt.append(" or 1=1 ");
			}
			else
			{
				if(userView.getUnit_id()!=null&&userView.getUnit_id().trim().length()>2)
				{
					if(userView.getUnit_id().length()==3)
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else
					{
				    	unitid=userView.getUnit_id();
				    	String[] unit_arr = unitid.split("`");
				    	for(int i=0;i<unit_arr.length;i++)
				    	{
				    		if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2) {
                                continue;
                            }
				    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
				    	}
					}
				}
				else{
					if(userView.getManagePrivCode()!=null&&userView.getManagePrivCode().trim().length()>0)
					{
						if(userView.getManagePrivCodeValue()==null|| "".equals(userView.getManagePrivCodeValue().trim()))
						{
							unitid="";
							tt.append(" or 1=1 ");
						}
						else{
					    	unitid=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					    	tt.append(" or b0110 like '%,"+userView.getManagePrivCodeValue()+"%'");
						}
					}
					else//没有范围
					{
						
					}
				}
			}
			if(tt.toString().length()>0)
			{
				if(userView.isSuper_admin()|| "".equals(unitid))
				{
					
				}else
				{
					sql2.append(" and (");
					sql2.append("("+tt.toString().substring(3)+")");
					sql2.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "''")+"=''");
					sql2.append(")");
				}
			}
			if("XXXX".equals(unitid))
			{
				sql2.append(" and "+Sql_switcher.isnull("b0110", "''")+"=''");
			}
			if(dao.search(sql2.toString()).next()){
				sql.append(" and id in ( ");
				sql.append(sql2+" )");
			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String hfactor = dynabean.get("hfactor").toString();
				hfactor = hfactor != null ? hfactor : "";
				String vfactor = dynabean.get("vfactor").toString();
				vfactor = vfactor != null ? vfactor : "";
				String s_hfactor = dynabean.get("s_hfactor").toString();
				s_hfactor = s_hfactor != null ? s_hfactor : "";
				String s_vfactor = dynabean.get("s_vfactor").toString();
				s_vfactor = s_vfactor != null ? s_vfactor : "";
				String item = dynabean.get("item").toString();
				item = item != null ? item : "";
				boolean check = true;
				if (hfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary.getFieldItem(hfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (vfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary.getFieldItem(vfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (s_hfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary
							.getFieldItem(s_hfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (s_vfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary
							.getFieldItem(s_vfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (check) {
					CommonData obj = new CommonData(hfactor + ":" + vfactor
							+ ":" + s_hfactor + ":" + s_vfactor + ":"
							+ dynabean.get("name").toString() + ":"
							+ dynabean.get("item").toString() + ":"
							+ dynabean.get("id").toString(), dynabean.get("id")
							.toString()
							+ "." + dynabean.get("name").toString());
					retlist.add(obj);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommonData obj1 = new CommonData("", "");
		retlist.add(0, obj1);
		return retlist;
	}

	/**
	 * 查询薪资变化子集
	 * 
	 * @param itemtype
	 *            子集数据类型
	 * @param itemlist
	 *            子集集合
	 * @return retlist
	 */
	public ArrayList changeFunctionList(ArrayList itemlist, String itemtype) {
		ArrayList retlist = new ArrayList();
		for (int i = 0; i < itemlist.size(); i++) {
			FieldItem listitem = (FieldItem) itemlist.get(i);
			if (listitem != null) {
				if ("code".equalsIgnoreCase(itemtype)) {
					if (listitem.isCode()) {
						CommonData obj = new CommonData(listitem.getItemid()
								+ ":" + listitem.getItemdesc(), "("
								+ listitem.getFieldsetid() + ")-"
								+ listitem.getItemdesc());
						retlist.add(obj);
					}
				} else {
					if (listitem.getItemtype().equalsIgnoreCase(itemtype)) {
						// FieldSet fieldset = new
						// FieldSet(listitem.getFieldsetid());
						CommonData obj = new CommonData(listitem.getItemid()
								+ ":" + listitem.getItemdesc(), "("
								+ listitem.getFieldsetid() + ")-"
								+ listitem.getItemdesc());
						retlist.add(obj);
					}
				}
			}
		}
		CommonData obj1 = new CommonData("", "");
		retlist.add(0, obj1);
		return retlist;
	}

	/**
	 * 查询薪资变化代码子集
	 * 
	 * @param itemlist
	 *            子集集合
	 * @return retlist
	 */
	public String codeStr(ArrayList itemlist) {
		StringBuffer codestr = new StringBuffer();
		for (int i = 0; i < itemlist.size(); i++) {
			FieldItem listitem = (FieldItem) itemlist.get(i);
			if (listitem.isCode()) {
				codestr.append(listitem.getItemid() + ":");
				codestr.append(listitem.getItemdesc() + ":");
				codestr.append(listitem.getCodesetid() + ",");
			}
		}

		return codestr.toString();
	}

	/**
	 * 字符转数字
	 * 
	 * @param itemtype
	 *            子集数据类型
	 * @return retlist
	 */
	public String strTonum(String itemtype) {
		String num = "";
		if ("A".equalsIgnoreCase(itemtype)) {
			num = "2";
		} else if ("N".equalsIgnoreCase(itemtype)) {
			num = "1";
		} else if ("D".equalsIgnoreCase(itemtype)) {
			num = "3";
		} else if ("code".equalsIgnoreCase(itemtype)) {
			num = "4";
		}
		return num;

	}
}
