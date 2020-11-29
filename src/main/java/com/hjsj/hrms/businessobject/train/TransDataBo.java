package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>
 * Title:培训班
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2007-12-13 下午06:07:55
 * </p>
 * 
 * @author lilinbing
 * @version 4.0
 */
public class TransDataBo {
	private Connection conn;

	private String model; // 1.需求征集 2.需求审批3.培训班 4.考试计划

	public TransDataBo(Connection conn, String model) {
		this.conn = conn;
		this.model = model;
	}

	public TransDataBo(Connection conn) {
		this.conn = conn;
	}

	public TransDataBo() {
	}

	/**
	 * 前台显示字段
	 * 
	 * @return
	 */
	public ArrayList itemList() {
		ArrayList list = DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		ConstantXml constantbo = new ConstantXml(this.conn, "TR_PARAM");
		String viewname = constantbo.getValue("plan_mx");
		viewname = viewname != null && viewname.trim().length() > 0 ? viewname.toUpperCase() : "";

		if (viewname.trim().length() < 3) {
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				Field field = (Field) item.cloneField();
				if ("B0110".equalsIgnoreCase(item.getItemid())) {
					field.setReadonly(true); // 此字段为只读状态
				} else if ("E0122".equalsIgnoreCase(item.getItemid())) {
					field.setReadonly(true); // 此字段为只读状态
				} else if ("r3101".equalsIgnoreCase(item.getItemid())) {
					field.setVisible(false); // 此字段为只读状态
				} else if ("r3117".equalsIgnoreCase(item.getItemid())) {
					field.setReadonly(true); // 此字段为只读状态
				} else if ("r3118".equalsIgnoreCase(item.getItemid())) {
					field.setReadonly(true); // 此字段为只读状态
				} else if ("r3127".equalsIgnoreCase(item.getItemid())) { // 状态
					field.setReadonly(true); // 此字段为只读状态
				} else if ("1".equals(model)
						&& "r3131".equalsIgnoreCase(item.getItemid())) {
					field.setReadonly(true);
				}else if ("r3125".equalsIgnoreCase(item.getItemid())) {
					field.setVisible(false);
					Field field1= new Field("trainplan");
					field1.setLabel(field.getLabel());
					field1.setLength(field.getLength());
					field1.setCodesetid("0");
					field1.setVisible(true);
					field1.setReadonly(true);
					field1.setDatatype(field.getDatatype());
					itemlist.add(field1);
				}
				if ("0".equals(item.getState())) {
					field.setVisible(false);
				}
				itemlist.add(field);
			}
			Field field = new Field("model");
			field.setLabel("model");
			field.setDatatype(DataType.STRING);
			field.setReadonly(true); // 此字段为只读状态
			field.setVisible(false); // 此字段隐藏
			itemlist.add(field);

			
			//培训班和培训计划加载慢，不用联查r40表，所以注掉了
//			field = new Field("person");// 判断培训班是否有人
//			field.setLabel("person");
//			field.setDatatype(DataType.INT);
//			field.setReadonly(true); // 此字段为只读状态
//			field.setVisible(false); // 此字段隐藏
//			itemlist.add(field);
		} else {
			itemlist.addAll(paramList(viewname));
		}

		return itemlist;
	}

	public ArrayList paramList(String viewname) {
		ArrayList list = DataDictionary.getFieldList("r31",
				Constant.USED_FIELD_SET);
		ArrayList paramlist = new ArrayList();
		String[] itemarr = viewname.split(",");
		for (int j = 0; j < list.size(); j++) {
			FieldItem item = (FieldItem) list.get(j);
			for (int i = 0; i < itemarr.length; i++) {
				if (itemarr[i] == null) {
                    continue;
                }
				if (item.getItemid().equalsIgnoreCase(itemarr[i])) {
					if (item != null) {
						if ("0".equals(item.getUseflag())) {
                            continue;
                        }
						Field field = (Field) item.cloneField();
						if ("B0110".equalsIgnoreCase(item.getItemid())) {
							field.setReadonly(true); // 此字段为只读状态
						} else if ("E0122".equalsIgnoreCase(item.getItemid())) {
							field.setReadonly(true); // 此字段为只读状态
						} else if ("r3101".equalsIgnoreCase(item.getItemid())) {
							field.setVisible(false); // 此字段为只读状态
						} else if ("r3117".equalsIgnoreCase(item.getItemid())) {
							field.setReadonly(true); // 此字段为只读状态
						} else if ("r3118".equalsIgnoreCase(item.getItemid())) {
							field.setReadonly(true); // 此字段为只读状态
						} else if ("r3127".equalsIgnoreCase(item.getItemid())) { // 状态
							field.setReadonly(true); // 此字段为只读状态
						} else if ("1".equals(model)
								&& "r3131".equalsIgnoreCase(item.getItemid())) {
							field.setReadonly(true);
						} else if ("r3125".equalsIgnoreCase(item.getItemid())) {
							field.setVisible(false);
							Field field1 = new Field("trainplan");
							field1.setLabel(field.getLabel());
							field1.setLength(field.getLength());
							field1.setCodesetid("0");
							field1.setVisible(true);
							field1.setReadonly(true);
							field1.setDatatype(field.getDatatype());
							paramlist.add(field1);
						}
						if (item.getState() != null
								&& "0".equals(item.getState())) {
							field.setVisible(false);
						}
						paramlist.add(field);
					}
				}
			}
		}
		if (viewname.indexOf("B0110") == -1) {
			FieldItem item = DataDictionary.getFieldItem("B0110");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("E0122") == -1) {
			FieldItem item = DataDictionary.getFieldItem("E0122");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("R3125") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3125");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("R3118") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3118");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("R3101") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3101");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("R3127") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3127");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		if (viewname.indexOf("R3131") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3131");
			if (item != null) {
				Field field = (Field) item.cloneField();
				field.setReadonly(true); // 此字段为只读状态
				field.setVisible(false); // 此字段隐藏
				paramlist.add(field);
			}
		}
		Field field = new Field("model");
		field.setLabel("model");
		field.setDatatype(DataType.STRING);
		field.setReadonly(true); // 此字段为只读状态
		field.setVisible(false); // 此字段隐藏
		paramlist.add(field);

		//培训班和培训计划加载慢，不用联查r40表，所以注掉了
//		field = new Field("person");// 判断培训班是否有人
//		field.setLabel("person");
//		field.setDatatype(DataType.INT);
//		field.setReadonly(true); // 此字段为只读状态
//		field.setVisible(false); // 此字段隐藏
//		paramlist.add(field);
		return paramlist;
	}

	public ArrayList filedItemList() {
		ArrayList list = DataDictionary.getFieldList("r31",
				Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		ConstantXml constantbo = new ConstantXml(this.conn, "TR_PARAM");
		String viewname = constantbo.getValue("plan_mx");
		viewname = viewname != null && viewname.trim().length() > 0 ? viewname
				.toUpperCase() : "";

		if (viewname.trim().length() < 3) {
			itemlist = list;
		} else {
			itemlist.addAll(paramItemList(viewname));
		}

		return itemlist;
	}

	public ArrayList paramItemList(String viewname) {
		ArrayList list = DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
		ArrayList paramlist = new ArrayList();
		String[] itemarr = viewname.split(",");
		for (int j = 0; j < list.size(); j++) {
			FieldItem item = (FieldItem) list.get(j);
			for (int i = 0; i < itemarr.length; i++) {
				if (itemarr[i] == null) {
                    continue;
                }
				if (item.getItemid().equalsIgnoreCase(itemarr[i])) {
					if ("r3101".equalsIgnoreCase(item.getItemid())) {
						paramlist.add(0, item);
						continue;
					}
					if (item != null) {
						paramlist.add(item);
					}
				}
			}
		}
		if (viewname.indexOf("R3101") == -1) {
			FieldItem item = DataDictionary.getFieldItem("R3101");
			if (item != null) {
				paramlist.add(0, item);
			}
		}
		return paramlist;
	}

	/**
	 * 前台显示字段
	 * 
	 * @return
	 */
	public ArrayList itemListTrain() {
		ArrayList list = DataDictionary.getFieldList("r31",
				Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			FieldItem item = (FieldItem) list.get(i);
			Field field = (Field) item.cloneField();
			if ("B0110".equalsIgnoreCase(item.getItemid())) {
				field.setReadonly(true); // 此字段为只读状态
			} else if ("E0122".equalsIgnoreCase(item.getItemid())) {
				field.setReadonly(true); // 此字段为只读状态
			} else if ("r3101".equalsIgnoreCase(item.getItemid())) {
				field.setVisible(false); // 此字段为只读状态
			} else if ("r3117".equalsIgnoreCase(item.getItemid())) {
				field.setReadonly(true); // 此字段为只读状态
			} else if ("r3118".equalsIgnoreCase(item.getItemid())) {
				field.setReadonly(true); // 此字段为只读状态
			} else if ("r3127".equalsIgnoreCase(item.getItemid())) { // 状态
				field.setReadonly(true); // 此字段为只读状态
			} else if ("1".equals(model)
					&& "r3131".equalsIgnoreCase(item.getItemid())) {
				field.setReadonly(true);
			}else if ("r3125".equalsIgnoreCase(item.getItemid())) {
				field.setVisible(false);
				Field field1= new Field("trainplan");
				field1.setLabel(field.getLabel());
				field1.setLength(field.getLength());
				field1.setCodesetid("0");
				field1.setVisible(true);
				field1.setReadonly(true);
				field1.setDatatype(field.getDatatype());
				itemlist.add(field1);
			}
			if ("0".equals(item.getState())) {
				field.setVisible(false);
			}
			itemlist.add(field);
		}
		Field field = new Field("model");
		field.setLabel("model");
		field.setDatatype(DataType.STRING);
		field.setReadonly(true); // 此字段为只读状态
		field.setVisible(false); // 此字段隐藏
		itemlist.add(field);

		//培训班和培训计划加载慢，不用联查r40表，所以注掉了
//		field = new Field("person");// 判断培训班是否有人
//		field.setLabel("person");
//		field.setDatatype(DataType.INT);
//		field.setReadonly(true); // 此字段为只读状态
//		field.setVisible(false); // 此字段隐藏
//		itemlist.add(field);
		return itemlist;
	}

	/**
	 * 查询列 例如:select xxx,xxx,xxx,
	 */
	public String sqlColum() {
		StringBuffer sqlcloum = new StringBuffer();
		sqlcloum.append("select ");
		ArrayList list = itemList();
		for (int i = 0; i < list.size(); i++) {
			Field field = (Field) list.get(i);
			if("trainplan".equalsIgnoreCase(field.getName())) {
                continue;
            }
			if ("model".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("'" + model + "' as model");
            } else if ("person".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("(select count(R4001) from R40 where R4005=r31.R3101) as person");
            } else if ("r3125".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("r3125,(select R2502 from R25 where R2501=r31.R3125) as trainplan");
            } else {
                sqlcloum.append(field.getName());
            }
			if (i < list.size() - 1) {
				sqlcloum.append(",");
			}
		}
		return sqlcloum.toString();
	}
	/**
	 * 查询列 例如:select xxx,xxx,xxx,
	 */
	public String sqlColumTrain() {
		StringBuffer sqlcloum = new StringBuffer();
		sqlcloum.append("select ");
		ArrayList list = itemListTrain();
		for (int i = 0; i < list.size(); i++) {
			Field field = (Field) list.get(i);
			if ("trainplan".equalsIgnoreCase(field.getName())) {
                continue;
            }
			if ("model".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("'" + model + "' as model");
            } else if ("person".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("(select count(R4001) from R40 where R4005=r31.R3101) as person");
            } else if ("r3125".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("r3125,(select R2502 from R25 where R2501=r31.R3125) as trainplan");
            } else {
                sqlcloum.append(field.getName());
            }
			if (i < list.size() - 1) {
				sqlcloum.append(",");
			}
		}
		return sqlcloum.toString();
	}

	/**
	 * 查询条件
	 * 
	 * @param search
	 *            查询条件
	 * @param a_code
	 *            机构代码
	 * @param time
	 *            时间间隔
	 * @param spflag
	 *            审批标识
	 * @return
	 * @throws GeneralException 
	 */
	public String sqlWhere(String search, String a_code, String time,
			String spflag) throws GeneralException {
		StringBuffer sqlwhere = new StringBuffer();
		sqlwhere.append(" from r31 where 1=1 ");
		if (a_code != null && a_code.trim().length() > 2 && a_code.indexOf("UN`")==-1) {
			String[] tmp = a_code.split("`");
			StringBuffer tmpstr=new StringBuffer();
			for(int i=0;i<tmp.length;i++){
				a_code = tmp[i];
				if(i>0) {
                    tmpstr.append(" or ");
                }
				if ("UN".equalsIgnoreCase(a_code.substring(0, 2))) {
                    tmpstr.append("B0110 like '"
                            + a_code.substring(2, a_code.length()) + "%'");
                }
				if ("UM".equalsIgnoreCase(a_code.substring(0, 2))) {
                    tmpstr.append("E0122 like '"
                            + a_code.substring(2, a_code.length()) + "%'");
                }
			}
			if(tmpstr!=null&&tmpstr.length()>0) {
                sqlwhere.append(" and ("+tmpstr+")");
            }
		}else if (a_code == null || a_code.trim().length()<2) {
			sqlwhere.append(" and 1=2 ");
		}
		if (time != null && time.trim().length() > 2) {
			sqlwhere.append(" and " + time);
		}
		if (spflag != null && spflag.trim().length() > 1) {
			sqlwhere.append(" and R3127='" + spflag + "'");
		}
		if (search != null && search.trim().length() > 0) {
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				/*String sexpr = searcharr[0];
				sexpr = PubFunc.keyWord_reback(sexpr);
				String sfactor = searcharr[1];
				sfactor = PubFunc.keyWord_reback(sfactor);
				try {
					boolean blike = false;
					blike = searcharr[2] != null && searcharr[2].equals("1") ? true
							: false;
					FactorList factor = new FactorList(sexpr, sfactor, "",
							false, blike, true, 1, "su");
					String wherestr = factor.getSqlExpression();
					if (wherestr.indexOf("WHERE") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("WHERE") + 5, wherestr
										.length());
					else if (wherestr.indexOf("where") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("where") + 5, wherestr
										.length());
					//if (wherestr.indexOf("I9999") != -1)
					//	wherestr = wherestr.substring(0, wherestr
					//			.lastIndexOf("AND"));

					wherestr = wherestr.replaceAll("A01", "r31").replaceAll("r3100", "r3101");
					sqlwhere.append(" and (" + wherestr+")");
				} catch (GeneralException e) {
				}*/
				TrainCourseBo bo = new TrainCourseBo("r31");
				String searchstr = bo.getWhereStr(search);
				sqlwhere.append(searchstr);
			}
		}
		return sqlwhere.toString();
	}
	/**
	 * 查询条件
	 * 
	 * @param search
	 *            查询条件
	 * @param a_code
	 *            机构代码
	 * @param time
	 *            时间间隔
	 * @param spflag
	 *            审批标识
	 * @return
	 * @throws GeneralException 
	 */
	public String sqlWhere(UserView userview,String search, String a_code, String time,
			String spflag) throws GeneralException {
		StringBuffer sqlwhere = new StringBuffer();
		sqlwhere.append(" from r31 where 1=1 ");
		if (a_code != null && a_code.trim().length() >2) {
			if ("UN".equalsIgnoreCase(a_code.substring(0, 2))) {
                sqlwhere.append(" and B0110 like '"
                        + a_code.substring(2, a_code.length()) + "%'");
            }
			if ("UM".equalsIgnoreCase(a_code.substring(0, 2))) {
                sqlwhere.append(" and E0122 like '"
                        + a_code.substring(2, a_code.length()) + "%'");
            }
		}else{
			if(!userview.isSuper_admin()){
				if(a_code.trim().length()<1){
//					if(userview.getStatus()==0){
						//a_code = userview.getUnit_id();
						//a_code = PubFunc.getTopOrgDept(a_code);
						TrainCourseBo bo = new TrainCourseBo(userview);
						a_code = bo.getUnitIdByBusi();
						if(a_code.indexOf("UN`")==-1){
							String unitarr[] = a_code.split("`"); 
	//						String b0110str = "";
	//						String e0122str = "";
							String str="";
							for(int i=0;i<unitarr.length;i++){
								if(unitarr[i]!=null&&unitarr[i].trim().length()>2&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))){
	//									b0110str +="'"+unitarr[i].substring(2)+"',";
										str +="B0110 like '"+unitarr[i].substring(2)+"%' or ";
								}else{
									if(unitarr[i].trim().length()>2){
	//									e0122str +="'"+unitarr[i].substring(2)+"',";
										str +="E0122 like '"+unitarr[i].substring(2)+"%' or ";
									}
								}
							}
							if(str.length()>0){
								sqlwhere.append(" and ("+str.substring(0, str.lastIndexOf("or")-1)+")");
							}else{
								/**liwc 业务业务登陆 管理范围*/
//								if ("UN".equalsIgnoreCase(userview.getManagePrivCode()))
//									sqlwhere.append(" and B0110 like '"
//											+ userview.getManagePrivCodeValue() + "%'");
//								else if ("UM".equalsIgnoreCase(userview.getManagePrivCode()))
//									sqlwhere.append(" and E0122 like '"
//											+ userview.getManagePrivCodeValue() + "%'");
//								else
									//sqlwhere.append(" and 1=2");
									throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
							}
						}
//					}else{
//						if ("UN".equalsIgnoreCase(userview.getManagePrivCode()))
//							sqlwhere.append(" and B0110 like '"
//									+ userview.getManagePrivCodeValue() + "%'");
//						else if ("UM".equalsIgnoreCase(userview.getManagePrivCode()))
//							sqlwhere.append(" and E0122 like '"
//									+ userview.getManagePrivCodeValue() + "%'");
//						else
//							//sqlwhere.append(" and 1=2");
//							throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
//					}
				}
			}
		}
		if (time != null && time.trim().length() > 2) {
			sqlwhere.append(" and " + time);
		}
		if (spflag != null && spflag.trim().length() > 1) {
			sqlwhere.append(" and R3127='" + spflag + "'");
		}
		if (search != null && search.trim().length() > 0) {
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				/*String sexpr = searcharr[0];
				sexpr = PubFunc.keyWord_reback(sexpr);
				String sfactor = searcharr[1];
				sfactor = PubFunc.keyWord_reback(sfactor);
				sfactor = PubFunc.reBackWord(sfactor);
				try {
					boolean blike = false;
					blike = searcharr[2] != null && searcharr[2].equals("1") ? true
							: false;
					FactorList factor = new FactorList(sexpr, sfactor, "",
							false, blike, true, 1, "su");
					String wherestr = factor.getSqlExpression();
					if (wherestr.indexOf("WHERE") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("WHERE") + 5, wherestr
										.length());
					else if (wherestr.indexOf("where") != -1)
						wherestr = wherestr.substring(
								wherestr.indexOf("where") + 5, wherestr
										.length());
					//if (wherestr.indexOf("I9999") != -1)
					//	wherestr = wherestr.substring(0, wherestr
					//			.lastIndexOf("AND"));

					wherestr = wherestr.replaceAll("A01", "r31").replaceAll("r3100", "R3101");
					sqlwhere.append(" and (" + wherestr+")");
				} catch (GeneralException e) {
				}*/
				TrainCourseBo bo = new TrainCourseBo("r31");
				String searchstr = bo.getWhereStr(search);
				sqlwhere.append(searchstr);
			}
		}
		return sqlwhere.toString();
	}


	public ArrayList spFlagList() {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList spflaglist = new ArrayList();
		CommonData dataobj = new CommonData("00", "全部");
		spflaglist.add(dataobj);
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='23'");
			if ("1".equals(model)) {
                buf.append(" and codeitemid in('01','02','03','04','06','07')");
            } else if ("2".equals(model)) {
                buf.append(" and codeitemid in('02','03','04','06')");
            } else if ("3".equals(model)) {
                buf.append(" and codeitemid in('01','02','03','04','06','07','08','09')");
            } else if ("4".equals(model)) {
                buf.append(" and codeitemid in('01','04','05','06','09')");
            }
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				String codeitemid=rs.getString("codeitemid");
				String codeitemdesc=rs.getString("codeitemdesc");
				dataobj = new CommonData(codeitemid,codeitemdesc);
				spflaglist.add(dataobj);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return spflaglist;
	}

	public ArrayList flagList() {
		ArrayList spflaglist = new ArrayList();
		CommonData dataobj = new CommonData("08", "报审");
		spflaglist.add(dataobj);
		dataobj = new CommonData("02", "已报批");
		spflaglist.add(dataobj);
		dataobj = new CommonData("03", "已批");
		spflaglist.add(dataobj);
		dataobj = new CommonData("07", "驳回");
		spflaglist.add(dataobj);

		return spflaglist;
	}

	public ArrayList timeFlagList() {
		ArrayList timeflaglist = new ArrayList();
		CommonData dataobj = new CommonData("00", "全部");
		timeflaglist.add(dataobj);
		dataobj = new CommonData("01", "本年度");
		timeflaglist.add(dataobj);
		dataobj = new CommonData("02", "本季度");
		timeflaglist.add(dataobj);
		dataobj = new CommonData("03", "本月份");
		timeflaglist.add(dataobj);
		dataobj = new CommonData("04", "某时间段");
		timeflaglist.add(dataobj);
		return timeflaglist;
	}

	public String timesSql(String timeflag, String startime, String endtime) {
		StringBuffer timesql = new StringBuffer();
		WeekUtils wu = new WeekUtils();
		if ("01".equals(timeflag)) {
			timesql.append("R3119='"
					+ DateUtils.getYear(wu.strTodate(wu.strDate())) + "'");
		} else if ("02".equals(timeflag)) {
			timesql.append("R3119='"
					+ DateUtils.getYear(wu.strTodate(wu.strDate())) + "'");
			timesql.append(" and R3120='0"
					+ DateUtils.getQuarter(wu.strTodate(wu.strDate())) + "'");
		} else if ("03".equals(timeflag)) {
			int month = DateUtils.getMonth(wu.strTodate(wu.strDate()));
			timesql.append("R3119='"
					+ DateUtils.getYear(wu.strTodate(wu.strDate())) + "'");
			if (month > 9) {
                timesql.append(" and R3121='" + month + "'");
            } else {
                timesql.append(" and R3121='0" + month + "'");
            }
		} else if ("04".equals(timeflag)) {
			 boolean f = false;
			if (startime != null && startime.trim().length() > 3) {
				f = true;
				//timesql.append(getDataValue("R3118", ">=", startime));
				String[] date=startime.split("-");
				if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
					date[1]="0"+date[1];
				}
				if(Sql_switcher.searchDbServer() == Constant.ORACEL){
					timesql.append(" concat(r3119,nvl(r3121,"+date[1]+"))>="+date[0]+date[1]);
				}else{
					timesql.append(" (r3119+isnull(r3121,"+date[1]+"))>="+date[0]+date[1]);
				}
			}
			
			  if(f && endtime != null && endtime.trim().length() > 3) {
                  timesql.append(" and ");
              }
			  
			if (endtime != null && endtime.trim().length() > 3) {
				String[] date=endtime.split("-");
				if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
					date[1]="0"+date[1];
				}
				if(Sql_switcher.searchDbServer() == Constant.ORACEL){
					timesql.append(" concat(r3119,nvl(r3121,"+date[1]+"))<="+date[0]+date[1]);
				}else{
					timesql.append(" (r3119+isnull(r3121,"+date[1]+"))<="+date[0]+date[1]);
				}
			}
			//if (endtime != null && endtime.trim().length() > 3) {
			//	timesql.append(getDataValue("R3118", "<=", endtime));
			//}
			
		}
		return timesql.toString();
	}

	public String getDataValue(String fielditemid, String operate, String value) {
		StringBuffer a_value = new StringBuffer("");
		if (value.length() > 0) {
			String[] tempvalue = value.split("-");
			if (tempvalue.length == 1) {
				value = value + "-01-01";
			}
			if (tempvalue.length == 2) {
				if (tempvalue[1].length() == 1) {
					value = tempvalue[0] + "-0" + tempvalue[1] + "-01";
				} else {
					value = value + "-01";
				}
			}
			if (tempvalue.length == 3) {
				if (tempvalue[1].length() == 1) {
					tempvalue[1] = "0" + tempvalue[1];
				}
				if (tempvalue[2].length() == 1) {
					tempvalue[2] = "0" + tempvalue[2];
				}
				value = tempvalue[0] + "-" + tempvalue[1] + "-" + tempvalue[2];
			}
			try {
				if ("=".equals(operate)) {
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid) + operate
							+ value.substring(0, 4) + " and ");
					a_value.append(Sql_switcher.month(fielditemid) + operate
							+ value.substring(5, 7) + " and ");
					a_value.append(Sql_switcher.day(fielditemid) + operate
							+ value.substring(8));
					a_value.append(" ) ");
				} else {
					if (">=".equals(operate)) {
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid) + ">"
								+ value.substring(0, 4) + " or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + ">"
								+ value.substring(5, 7) + " ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + "="
								+ value.substring(5, 7) + " and "
								+ Sql_switcher.day(fielditemid) + ">="
								+ value.substring(8));
						a_value.append(") ) ");
					} else if ("<=".equals(operate)) {
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid) + "<"
								+ value.substring(0, 4) + " or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + "<"
								+ value.substring(5, 7) + " ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + "="
								+ value.substring(5, 7) + " and "
								+ Sql_switcher.day(fielditemid) + "<="
								+ value.substring(8));
						a_value.append(") ) ");
					} else {
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid) + operate
								+ value.substring(0, 4) + " or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + operate
								+ value.substring(5, 7) + " ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid) + "="
								+ value.substring(0, 4) + " and "
								+ Sql_switcher.month(fielditemid) + "="
								+ value.substring(5, 7) + " and "
								+ Sql_switcher.day(fielditemid) + operate
								+ value.substring(8));
						a_value.append(") ) ");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return a_value.toString();
	}

	/*
	 * 获得标准学时内容 @author fanzhiguo
	 */
	public String getXML() {

		String content = "";
		String sqlStr = "SELECT * FROM Constant WHERE Constant = 'TR_PARAM'";

		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sqlStr);
			if (rs.next()) {
                content = Sql_switcher.readMemo(rs, "str_value");
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/*
	 * 获得标准学时内容 @author fanzhiguo
	 */
	public String getStudyHour() {
		String hour = "";
		String xmlContent = this.getXML();
		if ("".equals(xmlContent)) {
            return hour;
        }
		try {
			Document doc = PubFunc.generateDom(xmlContent);
			String xpath = "//param";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			if (ele == null) {
                return hour;
            }

			hour = ele.getChildText("StudyHour");
			if (hour == null) {
                hour = "";
            }
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hour;
	}

	/**
	 * 更新课时
	 * 
	 * @author fanzhiguo
	 */
	public void saveStuHour(String theHour) {
		String xmlContent = this.getXML();
		if ("".equals(xmlContent))// 新增
        {
            insertStuHour(theHour);
        } else
			// 更新
        {
            updateStuHour(theHour);
        }
	}

	/**
	 * 更新课时
	 * 
	 * @author fanzhiguo
	 */
	public void insertStuHour(String theHour) {
		Element root = new Element("param");
		Element child = new Element("StudyHour");
		child.setText(theHour);
		root.addContent(child);

		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(myDocument);

		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "";
		try {

			sql = "insert into constant(constant,str_value) values ('TR_PARAM','"
					+ xmlContent + "')";
			dao.insert(sql, new ArrayList());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新课时
	 * 
	 * @author fanzhiguo
	 */
	public void updateStuHour(String theHour) {
		String xmlContent = this.getXML();
		Element root = null;
		Document doc = null;
		try {
			doc = PubFunc.generateDom(xmlContent);
			String xpath = "//param";
			XPath xpath_ = XPath.newInstance(xpath);
			root = (Element) xpath_.selectSingleNode(doc);
			Element child = root.getChild("StudyHour");
			if (child != null) {
                child.setText(theHour);
            } else {
				child = new Element("StudyHour");
				child.setText(theHour);
				root.addContent(child);
			}

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		xmlContent = outputter.outputString(doc);

		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "";
		try {
			sql = "update constant set str_value='" + xmlContent
					+ "' where constant = 'TR_PARAM'";
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * 自动计算课时
	 * 
	 * @param stuHour－设置好的标准学时
	 */
	public void autoCalculateHour(String stuHour, String priFldValue) {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("update r31");
			sql.append(" set r3112 = (case when ");
			sql.append(Sql_switcher.diffSecond("r3116", "r3115"));
			sql.append("=0");
			sql.append(" then ");
			sql.append(stuHour);
			sql.append(" when ");
			sql.append(Sql_switcher.diffDays("r3116", "r3115") + "=0");
			sql.append(" then ");
			sql.append(Sql_switcher.diffHours("r3116", "r3115"));
			sql.append(" else ");
			sql.append("(" + Sql_switcher.diffDays("r3116", "r3115") + "+1)*" + stuHour);
			sql.append(" end)");
			sql.append(" where r3115 is not null and r3116 is not null and r3127<>'06'");
			if (!"".equals(priFldValue)) {
                sql.append(" and r3101='" + priFldValue + "'");
            }
			
			dao.update(sql.toString());
			//陈旭光:自动计算课时时同时更新学院信息中的课时
			sql.delete(0, sql.length());
			sql.append("update r40 set r4008=(");
			sql.append("select r31.r3112 from r31 where r40.r4005=r31.r3101");
			sql.append(") where r40.r4005 in(");
			sql.append("select r3101 from r31 where r31.r3115 is not null and r31.r3116 is not null and r31.r3127<>'06'");			
			if (!"".equals(priFldValue)) {
                sql.append(" and R31.R3101='" + priFldValue + "')");
            } else {
                sql.append(")");
            }
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取sql语句中所需要的指标
	 * @param items 所需要的指标的list list不为空
	 * @return
	 * @throws GeneralException 
	 * @throws GeneralException
	 */
	public String getSqlItems(ArrayList items) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			for (int i = 0; i < items.size(); i++) {
				String itemid = (String) items.get(i);
				sql.append(itemid);
				sql.append("=?");
				if (i + 1 < items.size()) {
					sql.append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
	/**
	 * 将更改的指标的值进行格式转换与校验
	 * @param items 指标的list  
	 * @param itemValues 指标的值的list items与itemValues两个list中的值一一对应
	 * @return
	 * @throws GeneralException 
	 * @throws GeneralException
	 */
	public ArrayList getSqlItemValues(ArrayList items, ArrayList itemValues) throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			for (int i = 0; i < itemValues.size(); i++) {
				String itemid = (String) items.get(i);
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				String itemvalue = (String) itemValues.get(i);
				itemvalue = SafeCode.decode(itemvalue);
				if ("D".equalsIgnoreCase(fielditem.getItemtype())) {
					if (itemvalue != null && itemvalue.trim().length() > 0) {
						Date date = DateUtils.getSqlDate(itemvalue, "yyyy-MM-dd");
						list.add(date);
					} else {
						list.add(null);
					}
				} else if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
					if (itemvalue.trim().length() == 0) {
						list.add(null);
					} else {
						list.add(itemvalue);
					}
				} else {
					itemvalue = PubFunc.splitString(itemvalue, fielditem.getItemlength());
					list.add(itemvalue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
}
