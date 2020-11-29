package com.hjsj.hrms.transaction.performance.evaluation.set_import;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveImportTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <pCreate time:Jun 19, 2008:4:48:48 PM</p> 
 * @author JinChunhai
 * @version 5.0
 */
public class SaveImportTrans extends IBusiness
{

	private HashMap orgParentMap = null;
	
	public void execute() throws GeneralException
	{
		String planid = (String) this.getFormHM().get("planid");
		LoadXml loadxml = new LoadXml(this.frameconn, planid, "");
		ArrayList list = (ArrayList) this.getFormHM().get("list");
		if (list == null)// 一个计划也不引入
			list = new ArrayList();
		
		PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
		HashMap oldRelaPlanMap = new HashMap();//上次设定的关联计划
		HashMap newRelaPlanMap = new HashMap();//本次设定的关联计划
		int x = 0;
//		ArrayList oldRelaPlanlist = loadxml.getRelatePlanValue("Plan", "ID");
		// 得到原来的关联计划，需要在per_result_xxx中删除相应的字段G_planid
//		for (int i = 0; i < oldRelaPlanlist.size(); i++)
//		{
//			String relaPlan = oldRelaPlanlist.get(i).toString();
//			oldRelaPlanMap.put(relaPlan, relaPlan);
//			
//			String planMenus = loadxml.getRelatePlanMenuValue(relaPlan).replaceAll(",", "`");			
//			this.updateTable(planid, relaPlan, planMenus, "drop");
//		}
		//本次设定的关联计划
		ArrayList planList = new ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			String temp = (String) list.get(i);
			if(temp.endsWith(":"))
				temp = temp.trim() + "Score";
			String[] temp1 = temp.trim().split(":");
			newRelaPlanMap.put(temp1[0], temp1[1]);
			planList.add(temp1[0]);
		}
		  
		//删除原先设置的引入计划相关字段
		String tablename = "per_result_" + planid;		
		DbWizard dbWizard = new DbWizard(this.frameconn);
		ArrayList oldRelaPlanlist = loadxml.getRelatePlanValue("Plan");
		LazyDynaBean abean=null;
		for(int i=0;i<oldRelaPlanlist.size();i++)
		{
			Table table = new Table(tablename);
			abean=(LazyDynaBean)oldRelaPlanlist.get(i);
			String id=(String)abean.get("id");	
			String Menus=(String)abean.get("Menus");
			oldRelaPlanMap.put(id, Menus);
			if(newRelaPlanMap.get(id)==null)	
			{				
				boolean flag = false;
				if(Menus!=null&&Menus.trim().length()>0)
				{
					String[] temps=Menus.split(",");
					for(int j=0;j<temps.length;j++)
					{
						String temp=temps[j].trim();
						if(temp.length()==0)
							continue;
						if("score".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id, false))
							{
								Field obj = new Field("G_"+id);
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							} 
						}
						else if("Grade".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Grade", false))
							{
								Field obj = new Field("G_"+id+"_Grade");	
								obj.setDatatype(DataType.STRING);
								obj.setLength(50);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}
						}
						else if("Avg".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Avg", false))
							{
								Field obj = new Field("G_"+id+"_Avg");
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}
						}
						else if("Max".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Max", false))
							{
								Field obj = new Field("G_"+id+"_Max");
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}  
						}
						else if("Min".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Min", false))
							{
								Field obj = new Field("G_"+id+"_Min");
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}
						}
						else if("XiShu".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_XiShu", false))
							{
								Field obj = new Field("G_"+id+"_XiShu");
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}
						}
						else if("Order".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Order", false))
							{
								Field obj = new Field("G_"+id+"_Order");	
								obj.setDatatype(DataType.INT);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							} 
						}
						else if("UMOrd".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_UMOrd", false))
							{
								Field obj = new Field("G_"+id+"_UMOrd");	
								obj.setDatatype(DataType.INT);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							} 
						}
						else if("Mark".equalsIgnoreCase(temp))
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_Mark", false))
							{
								Field obj = new Field("G_"+id+"_Mark");	
								obj.setDatatype(DataType.STRING);
								obj.setLength(50);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}   
						}
						else if(temp.indexOf("Body")!=-1)
						{
							String bodyid=temp.replaceAll("Body","");
							if (dbWizard.isExistField(tablename, "G_"+id+"_B_"+("-1".equals(bodyid)?"X1":bodyid), false))
							{
								Field obj = new Field("G_"+id+"_B_"+("-1".equals(bodyid)?"X1":bodyid));
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}
						}
						else if(temp.indexOf("Item")!=-1)
						{
							String itemid=temp.replaceAll("Item","");
							if (dbWizard.isExistField(tablename, "G_"+id+"_Item"+itemid, false))
							{
								Field obj = new Field("G_"+id+"_Item"+itemid);
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							} 
						}
						else  
						{
							if (dbWizard.isExistField(tablename, "G_"+id+"_"+temp, false))
							{
								Field obj = new Field("G_"+id+"_"+temp);
								obj.setDatatype(DataType.FLOAT);
								obj.setLength(12);
								obj.setDecimalDigits(6);
								obj.setKeyable(false);
								table.addField(obj);
								flag = true;
							}  
						}
					}
				}else{
					if (dbWizard.isExistField(tablename, "G_"+id, false))
					{
						Field obj = new Field("G_"+id);
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} 
				}
				if (flag)
					dbWizard.dropColumns(table);// 更新列
			}			
		}
		Table table = new Table(tablename);
		for (int i = 0; i < planList.size(); i++)
		{
			String plan_id = (String) planList.get(i);		
			if(oldRelaPlanMap.get(plan_id)==null)
			{
				table = new Table(tablename);
				if (!dbWizard.isExistField(tablename, "G_"+plan_id, false))
				{
					Field obj = new Field("G_"+plan_id);
					obj.setDatatype(DataType.FLOAT);
					obj.setLength(12);
					obj.setDecimalDigits(6);
					obj.setKeyable(false);
					table.addField(obj);
					dbWizard.addColumns(table);// 更新列
				} 
				newRelaPlanMap.put(plan_id, "");
			}else
			{
				String menus = (String)oldRelaPlanMap.get(plan_id);
				newRelaPlanMap.put(plan_id, menus);//保留原来的相关字段的设置
			}				
		}

		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		ArrayList relatelist = new ArrayList();
		sql.append("select * from per_plan  where 1=2 ");
		if (planList.size() > 0)
		{
			sql.append(" or plan_id in (");

			for (int i = 0; i < planList.size(); i++)
			{
				sql.append( planList.get(i).toString().trim() + ",");
			}
			sql.setLength(sql.length() - 1);
			sql.append(") order by plan_id asc");
		}
		try
		{
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
			{
				String relaPlan = frowset.getString("plan_id");
				String planMenus = (String) newRelaPlanMap.get(relaPlan);
//				planMenus = planMenus.replaceAll("`", ",");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("ID", frowset.getString("plan_id"));
				bean.set("Name", frowset.getString("name"));
				bean.set("Type", frowset.getString("object_type"));
				bean.set("Menus", planMenus);
				relatelist.add(bean);
//				this.updateTableData(dao, planid, relaPlan, planMenus);
			}
			ArrayList idlist = new ArrayList();
			idlist.add("ID");
			idlist.add("Name");
			idlist.add("Type");
			idlist.add("Menus");
			loadxml.saveRelatePlanValue("Plan", idlist, relatelist);
			
			orgParentMap = this.getParentIds();
			for(int i=0;i<relatelist.size();i++)
			{
				LazyDynaBean  bean =  (LazyDynaBean)relatelist.get(i);
				String relaPlan = (String)bean.get("ID");
				String planMenus = (String) newRelaPlanMap.get(relaPlan);
//				this.updateTableData(dao, planid, relaPlan, planMenus);
				// 更新per_result_planid表中调整后的表结构的"子集"字段的值
				pb.updateSubset(planid);
				
				// 更新per_result_planid表中调整后的表结构的"引入计划"的字段的值
				pb.updateResultTable(planid);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		this.getFormHM().put("mess", "ok");
	}

	/** 更新数据 */
	public void updateTableData(ContentDAO dao, String planid, String relaPlan, String planMenus) throws GeneralException
	{
		String tableName = "per_result_" + planid;
		StringBuffer sqlstr = new StringBuffer();
		try
		{
//			if (!planMenus.equals(""))// 联通专版
//			{
//				String[] menus = planMenus.split(",");
//				for (int j = 0; j < menus.length; j++)
//				{
//
//					String menu = menus[j];
//					if (menu.equalsIgnoreCase("Score"))
//					{
//						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "=");
//						sqlstr.append("(select per_result_" + relaPlan + ".score from per_result_" + relaPlan);
//						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
//					} else if (menu.equalsIgnoreCase("Order"))
//					{
//						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "_Order=");
//						sqlstr.append("(select per_result_" + relaPlan + ".ordering from per_result_" + relaPlan);
//						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
//					} else if (menu.equalsIgnoreCase("Grade"))
//					{
//						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "_Grade=");
//						sqlstr.append("(select per_result_" + relaPlan + ".resultdesc from per_result_" + relaPlan);
//						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
//					} else if (menu.equalsIgnoreCase("Avg"))
//					{
//						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "_Avg=");
//						sqlstr.append("(select per_result_" + relaPlan + ".exS_GrpAvg from per_result_" + relaPlan);
//						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
//					} else if (menu.equalsIgnoreCase("XiShu"))
//					{
//						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "_XiShu=");
//						sqlstr.append("(select per_result_" + relaPlan + ".exX_object from per_result_" + relaPlan);
//						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
//					} else if (menu.substring(0, 4).equalsIgnoreCase("Body"))
//					{
//						// sqlstr.append("update " + tableName + " set " + tableName
//						// + ".G_" + relaPlan + "_B_"+menu.substring(4)+"=");
//						// sqlstr.append("per_result_" + relaPlan + ".exX_object
//						// from per_result_" + relaPlan);
//						// sqlstr.append(" where " + tableName +
//						// ".object_id=per_result_" + relaPlan + ".object_id");
//						// 暂时不加了，王建华说这个功能是联通专版
//
//					}
//					if (sqlstr.length() > 0)
//					{
//						dao.update(sqlstr.toString());
//					}
//				}
//			} else
			{
				int object_type = this.getPlanVo(planid).getInt("object_type");
				int object_type_relaPlan = this.getPlanVo(relaPlan).getInt("object_type");
				if (object_type == 2) // 人员类型计划可以引入所有类型的计划
				{
					sqlstr.setLength(0);
					ArrayList objectList = new ArrayList();
					HashMap relaPlanMap = new HashMap();
					sqlstr.append("select object_id,e0122,b0110 from " + tableName);
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next())
					{
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("object_id", this.frowset.getString(1));
						String e0122 = this.frowset.getString(2);
						String b0110 = this.frowset.getString(3);	
						if(e0122==null && b0110!=null)
							e0122=b0110;
						else if(e0122==null && b0110==null)
							continue;
						bean.set("e0122", e0122);
						objectList.add(bean);
					}

					sqlstr.setLength(0);
					sqlstr.append("select object_id,score from per_result_" + relaPlan);
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next())
						relaPlanMap.put(this.frowset.getString(1), new Double(this.frowset.getDouble(2)));

					if (object_type_relaPlan == 2)// 人员引入人员
					{
						sqlstr.setLength(0);
						sqlstr.append("update " + tableName + " set " + tableName + ".G_" + relaPlan + "=");
						sqlstr.append("(select per_result_" + relaPlan + ".score from per_result_" + relaPlan);
						sqlstr.append(" where " + tableName + ".object_id=per_result_" + relaPlan + ".object_id)");
						dao.update(sqlstr.toString());
					} else
					// 人员引入团队
					{
						sqlstr.setLength(0);
						sqlstr.append("update " + tableName + " set G_" + relaPlan + "=? where object_id=? ");
						ArrayList dataList = new ArrayList();
						for (int i = 0; i < objectList.size(); i++)
						{
							LazyDynaBean bean = (LazyDynaBean) objectList.get(i);
							String object_id = (String) bean.get("object_id");
							String e0122 = (String) bean.get("e0122");
							Double score = (Double) relaPlanMap.get(e0122);						
							String temp = e0122;
							while (score == null)
							{							
								temp = (String) orgParentMap.get(temp);
								if (temp == null)
									break;
								score = (Double) relaPlanMap.get(temp);						
							}
							if (score != null)
							{
								ArrayList list = new ArrayList();
								list.add(score);
								list.add(object_id);								
								dataList.add(list);
							}
						}
						dao.batchUpdate(sqlstr.toString(), dataList);
					}
				} else
				// 非人员类型的计划只能引入非人员类型的计划
				{
					sqlstr.setLength(0);
					ArrayList objectList = new ArrayList();
					HashMap relaPlanMap = new HashMap();
					sqlstr.append("select object_id from " + tableName);
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next())
					{
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("object_id", this.frowset.getString(1));
						objectList.add(bean);
					}

					sqlstr.setLength(0);
					sqlstr.append("select object_id,score from per_result_" + relaPlan);
					this.frowset = dao.search(sqlstr.toString());
					while (this.frowset.next())
						relaPlanMap.put(this.frowset.getString(1), new Double(this.frowset.getDouble(2)));

					sqlstr.setLength(0);
					sqlstr.append("update " + tableName + " set G_" + relaPlan + "=? where object_id=? ");
					ArrayList dataList = new ArrayList();
					for (int i = 0; i < objectList.size(); i++)
					{
						LazyDynaBean bean = (LazyDynaBean) objectList.get(i);
						String object_id = (String) bean.get("object_id");
						Double score = (Double) relaPlanMap.get(object_id);
						String temp = object_id;
						while (score == null)
						{							
							temp = (String) orgParentMap.get(temp);
							if (temp == null)
								break;
							score = (Double) relaPlanMap.get(temp);						
						}
						if (score != null)
						{
							ArrayList list = new ArrayList();	
							list.add(score);
							list.add(object_id);
							dataList.add(list);
						}
					}
					dao.batchUpdate(sqlstr.toString(), dataList);
				}
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public HashMap getParentIds()
	{

		HashMap parentId = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String sql = "select codeitemid,parentid from organization where parentid!=codeitemid ";
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
				parentId.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return parentId;
	}

	public RecordVo getPlanVo(String planid) throws GeneralException
	{

		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", planid);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return vo;
	}

	/** 更新表结构 */
	public void updateTable(String planid, String relaPlan, String planMenus, String type) throws GeneralException
	{
		String tableName = "per_result_" + planid;
		Table table = new Table(tableName);
		DbWizard dbWizard = new DbWizard(this.frameconn);
		boolean flag = false;
		if (!"".equals(planMenus))
		{
			String[] menus = planMenus.split("`");
			for (int j = 0; j < menus.length; j++)
			{
				String menu = menus[j];
				if ("Score".equalsIgnoreCase(menu))
				{
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan, false))
					{
						Field obj = new Field("G_" + relaPlan);
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan, false))
					{
						Field obj = new Field("G_" + relaPlan);
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				} else if ("Order".equalsIgnoreCase(menu))
				{
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan + "_Order", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Order");
						obj.setDatatype(DataType.INT);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan + "_Order", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Order");
						obj.setDatatype(DataType.INT);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				} else if ("Grade".equalsIgnoreCase(menu))
				{
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan + "_Grade", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Grade");
						obj.setDatatype(DataType.STRING);
						obj.setLength(50);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan + "_Grade", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Grade");
						obj.setDatatype(DataType.STRING);
						obj.setLength(50);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				} else if ("Avg".equalsIgnoreCase(menu))
				{
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan + "_Avg", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Avg");
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan + "_Avg", false))
					{
						Field obj = new Field("G_" + relaPlan + "_Avg");
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				} else if ("XiShu".equalsIgnoreCase(menu))
				{
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan + "_XiShu", false))
					{
						Field obj = new Field("G_" + relaPlan + "_XiShu");
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan + "_XiShu", false))
					{
						Field obj = new Field("G_" + relaPlan + "_XiShu");
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				} else if ("Body".equalsIgnoreCase(menu.substring(0, 4)))
				{
					String bodyid = menu.substring(4);
					if ("add".equals(type) && !dbWizard.isExistField(tableName, "G_" + relaPlan + "_B_" + ("-1".equals(bodyid)?"X1":bodyid), false))
					{
						Field obj = new Field("G_" + relaPlan + "_B_" + ("-1".equals(bodyid)?"X1":bodyid));
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					} else if ("drop".equals(type) && dbWizard.isExistField(tableName, "G_" + relaPlan + "_B_" + ("-1".equals(bodyid)?"X1":bodyid), false))
					{
						Field obj = new Field("G_" + relaPlan + "_B_" + ("-1".equals(bodyid)?"X1":bodyid));
						obj.setDatatype(DataType.FLOAT);
						obj.setLength(12);
						obj.setDecimalDigits(6);
						obj.setKeyable(false);
						table.addField(obj);
						flag = true;
					}
				}
			}
		}
		else
		{
			if (!dbWizard.isExistField(tableName, "G_" + relaPlan, false))
			{
				Field obj = new Field("G_" + relaPlan);
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(12);
				obj.setDecimalDigits(6);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
		}
		if (flag)
		{
			if ("drop".equals(type))
				dbWizard.dropColumns(table);
			else if ("add".equals(type))
				dbWizard.addColumns(table);
		}

	}
}
