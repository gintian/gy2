package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Description:绩效评估 计算公式</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p>
 * @author JinChunhai
 * @version 4.2
 */

public class ComputFormulaBo
{
	private String type = "";// total_formula 总分计算公式 xishu_formula 考核系数公式

	private Connection cn = null;

	private String plan_id = "";

	private UserView userview = null;

	public ComputFormulaBo(String _type, Connection _cn, String plan_id, UserView _userview)
	{
		this.type = _type;
		this.cn = _cn;
		this.plan_id = plan_id;
		this.userview = _userview;
	}

	
	/**
	 * 获得当前结构表中的字段
	 * @param planid
	 * @return
	 */
	public ArrayList getSelfFields(String planid,int flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.cn);
		try
		{
			FieldItem item = new FieldItem();
//			item.setItemid("score");
//			if(flag==1)
//				item.setItemdesc("本次得分"); //
//			if(flag==2)
//				item.setItemdesc("总分");
//			item.setItemtype("N");
//			item.setDecimalwidth(2);
//			item.setItemlength(12);
//			list.add(item);
			//zzk 2014/2/11  本次得分 总分 都是修正后的总分
			item.setItemid("score");
			item.setItemdesc("本次得分"); //
			item.setItemtype("N");
			item.setDecimalwidth(2);
			item.setItemlength(12);
			list.add(item);
			
			item = new FieldItem();
			item.setItemid("score");
			item.setItemdesc("总分"); //
			item.setItemtype("N");
			item.setDecimalwidth(2);
			item.setItemlength(12);
			list.add(item);
			
			item = new FieldItem();
			item.setItemid("body_id");
			item.setItemdesc("对象类别");
			item.setItemtype("N");
			item.setDecimalwidth(0);
			list.add(item);
			
			list.add(getFieldItem("a0101","考核对象名称","A"));
	
			item = new FieldItem();
			item.setItemid("e0122");
			item.setItemdesc("所属部门");
			item.setItemtype("A");
			item.setCodesetid("UM");
			list.add(item);			
			
			list.add(getFieldItem("minusScore","关键事件扣分","N"));
			list.add(getFieldItem("addScore","关键事件加分","N"));
			LoadXml loadXml = new LoadXml(this.cn, planid);
			Hashtable params = loadXml.getDegreeWhole();
			String WholeEvalMode = (String) params.get("WholeEvalMode");
			if("1".equals(WholeEvalMode)) {
                list.add(getFieldItem("whole_score","总体评价得分","N"));
            }
			list.add(getFieldItem("postrulescore","岗位标准分值","N"));
			list.add(getFieldItem("matesurmise","匹配度","N"));
						
			list.add(getFieldItem("exX_object","等级系数","N"));
			list.add(getFieldItem("exs_grpavg","组内平均分","N"));
			list.add(getFieldItem("exS_GrpMax","组内最高分","N"));
			list.add(getFieldItem("exS_GrpMin","组内最低分","N"));						
			
			if(flag==2)
			{
				list.add(getFieldItem("Ordering","排名","I"));
				list.add(getFieldItem("ex_GrpNum","组内对象数","I"));
				list.add(getFieldItem("org_ordering","部门排名","I"));
				list.add(getFieldItem("org_GrpNum","部门人数","I"));
				if("custom_formula".equalsIgnoreCase(this.type))
				{
					list.add(getFieldItem("resultdesc","等级","A"));				
				}
					
			}
			
			RecordVo vo = new RecordVo("per_plan");
			vo.setInt("plan_id", Integer.parseInt(planid));
			vo = dao.findByPrimaryKey(vo);
			String template_id =vo.getString("template_id");
			
			 //加指标
			 String sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
				    +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='" + template_id + "' "
			        +" order by pp.seq";
			 RowSet   rowSet = dao.search(sql);
			 while(rowSet.next())
			 {
				 	item = new FieldItem();
					item.setItemid("C_"+rowSet.getString("point_id"));
					item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("pointname")));
					item.setItemtype("N");
					item.setDecimalwidth(4);
					item.setItemlength(12);
					list.add(item);
			 }
			//项目
			 rowSet=dao.search("select * from  per_template_item where template_id='"+template_id+"'  order by seq");
			 while(rowSet.next())
			 {
				item = new FieldItem();
				item.setItemid("T_"+rowSet.getString("item_id"));
				item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("itemdesc")));
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				list.add(item);
			 }
			 
			 HashMap bodyMap=new HashMap();
			 rowSet=dao.search("select * from per_mainbodyset");
			 while(rowSet.next())
			 {
				 bodyMap.put(rowSet.getString("body_id"),rowSet.getString("name"));
			 }
			 
			LoadXml loadxml = new LoadXml(this.cn, plan_id, "");
			String KeepDecimal = (String) params.get("KeepDecimal");
			
			ArrayList planlist = loadxml.getRelatePlanValue("Plan");
			LazyDynaBean abean=null;
			for(int i=0;i<planlist.size();i++)
			{
				abean=(LazyDynaBean)planlist.get(i);
				String id=(String)abean.get("id");
				String Name=(String)abean.get("Name");
				String Type=(String)abean.get("Type");
				
				HashMap itemMap=new HashMap();
				rowSet=dao.search("select * from per_template_item where template_id=(select template_id from per_plan where plan_id="+id+")");
				while(rowSet.next())
				{ 
					itemMap.put(rowSet.getString("item_id"),rowSet.getString("itemdesc"));
				}
				HashMap pointMap=new HashMap();
				sql = "select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,po.fielditem,po.status,pp.score,po.Kh_content,po.Gd_principle from per_template_item pi,per_template_point pp,per_point po "
				    +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id=(select template_id from per_plan where plan_id="+id+") "
			        +" order by pp.seq";
			    rowSet = dao.search(sql);
			    while(rowSet.next())
				{ 
			    	pointMap.put(rowSet.getString("point_id").toLowerCase(),rowSet.getString("pointname"));
				}
				
				
				/**
				”Score,Grade,Avg,Max,Min,XiShu,Order,UMOrd,Body1,Body2,Body3,Item1，Item2，XXXXX_1” 
				分别表示“得分，等级，组平均分,组最高分,组最低分，绩效系数，组内排名，部门排名，类别1，类别2，类别3,项目1，项目2，指标XXXXX_1”。为空或没有本属性默认为“得分”。
				对应字段(plan_id用n表示)：
				“G_n,G_n_Grade, G_n_Avg, G_n_Max, G_n_Min, G_n_XiShu,  G_n_Order, G_n_UMOrd, G_n_B_Id, , G_n_I_Id, , G_n_P_Id,”
				 */
				String Menus=(String)abean.get("Menus");
				if(Menus!=null&&Menus.trim().length()>0)
				{
					String[] temps=Menus.split(",");
					for(int j=0;j<temps.length;j++)
					{
						String temp=temps[j].trim();
						if(temp.length()==0) {
                            continue;
                        }
						if("score".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id,Name+".得分","N"));
							//list.add(getFieldItem("ROUND(G_"+id+","+KeepDecimal+")",Name+".得分","N"));
						}
						else if("Grade".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_Grade",Name+".等级","A"));
						}
						else if("Avg".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_Avg",Name+".组平均分","N"));
						}
						else if("Max".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_Max",Name+".组最高分","N"));
						}
						else if("Min".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_Min",Name+".组最低分","N"));
						}
						else if("XiShu".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_XiShu",Name+".等级系数","N"));
						}
						else if("Order".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_Order",Name+".组内排名","I"));
							list.add(getFieldItem("G_"+id+"_GrpNum",Name+".组内对象数","I"));
						}
						else if("UMOrd".equalsIgnoreCase(temp))
						{
							list.add(getFieldItem("G_"+id+"_UMOrd",Name+".部门排名","I"));
							list.add(getFieldItem("G_"+id+"_UMNum",Name+".部门人数","I"));
						}
						else if(temp.indexOf("Body")!=-1)
						{
							String bodyid=temp.replaceAll("Body","");
							if(bodyMap.get(bodyid)!=null) {
                                list.add(getFieldItem("G_"+id+"_B_"+("-1".equals(bodyid)?"X1":bodyid),Name+"."+(String)bodyMap.get(bodyid),"I"));
                            }
						}
						else if(temp.indexOf("Item")!=-1)
						{
							String itemid=temp.replaceAll("Item","");
							if(itemMap.get(itemid)!=null) {
                                list.add(getFieldItem("G_"+id+"_I_"+itemid,Name+"."+(String)itemMap.get(itemid),"N"));
                            }
						}
						else  
						{
							if(pointMap.get(temp.toLowerCase())!=null) {
                                list.add(getFieldItem("G_"+id+"_P_"+temp,Name+"."+(String)pointMap.get(temp.toLowerCase()),"N"));
                            }
						}
					}
				}else{
					list.add(getFieldItem("G_"+id,Name+".得分","N"));
					//list.add(getFieldItem("ROUND(G_"+id+","+KeepDecimal+")",Name+".得分","N"));
					
				}				
			}
			
			String subsetMenus = loadxml.getRelatePlanSubSetMenuValue();
			if(subsetMenus!=null&&subsetMenus.trim().length()>0)
			{
				String[] temps=subsetMenus.split(",");
				for(int j=0;j<temps.length;j++)
				{
					String temp=temps[j].trim();
					if(temp.length()==0) {
                        continue;
                    }
				    FieldItem fielditem = DataDictionary.getFieldItem(temp);
				    String itemType = fielditem.getItemtype();
				    int decimalwidth = fielditem.getDecimalwidth();
				    if("M".equalsIgnoreCase(itemType)) {
                        itemType="A";
                    } else if("N".equalsIgnoreCase(itemType) && decimalwidth==0) {
                        itemType="I";
                    }
					list.add(getFieldItem(fielditem.getItemid(),fielditem.getItemdesc(),itemType));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return list;
	}
	 
	
	private FieldItem  getFieldItem(String id,String desc,String type) 
	{
		FieldItem item = new FieldItem();
		item.setItemid(id);
		item.setItemdesc(PubFunc.keyWord_reback(desc));
		item.setItemtype(type);
		if("N".equalsIgnoreCase(type))
		{
			item.setDecimalwidth(4);
			item.setItemlength(12);
		}
		else if("I".equalsIgnoreCase(type))
		{
			item.setItemtype("N");
			item.setDecimalwidth(0);
			item.setItemlength(10);
		}
		else if("A".equalsIgnoreCase(type))
		{
			item.setItemlength(50);
		}
		return item;
	}
	
	
	public ArrayList getSelectList()
	{
		ArrayList filelist = new ArrayList();
		RowSet rs = null;
		if ("total_formula".equalsIgnoreCase(this.type))
		{
			filelist.addAll(getSelfFields(plan_id,1));
			
		} else if ("xishu_formula".equalsIgnoreCase(this.type) || "custom_formula".equalsIgnoreCase(this.type))
		{

	/*		FieldItem item = new FieldItem();
			item.setItemid("score");
			item.setItemdesc("总分");
			item.setItemtype("N");
			item.setItemlength(8);
			item.setDecimalwidth(2);
			filelist.add(item);*/
			filelist.addAll(getSelfFields(plan_id,2));
			
		}
		else if ("PerformanceReport_nameFormula".equalsIgnoreCase(this.type))
		{
			FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			
			FieldItem item = new FieldItem();
			item.setItemid("b0110_cn");
			item.setItemdesc(ResourceFactory.getProperty("b0110.label"));
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

			item = new FieldItem();
			item.setItemid("e0122_cn");
			item.setItemdesc(PubFunc.keyWord_reback(fielditem.getItemdesc()));
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

			item = new FieldItem();
			item.setItemid("e01a1_cn");
			item.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

			item = new FieldItem();
			item.setItemid("a0101");
			item.setItemdesc("姓名");
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

			item = new FieldItem();
			item.setItemid("a0100");
			item.setItemdesc("人员编号");
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

			item = new FieldItem();
			item.setItemid("planname");
			item.setItemdesc("计划名称");
			item.setItemtype("A");
			item.setCodesetid("0");
			filelist.add(item);

		}
		try
		{
			if (rs != null) {
                rs.close();
            }
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return filelist;
	}

	/** 检查公式定义是否正确 */
	public String testformula(String formula) throws GeneralException
	{
		String errorInfo = "ok";
		ContentDAO dao = new ContentDAO(this.cn);
		if (formula != null && formula.trim().length() > 0)
		{
			String tablename = "per_result_" + this.plan_id;
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.cn);
			DBMetaModel dbmodel = new DBMetaModel(this.cn);

			if (!dbWizard.isExistField(tablename, "A0100", false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}
			
			try {
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id", Integer.parseInt(this.plan_id));
				vo = dao.findByPrimaryKey(vo);
				int object_type =vo.getInt("object_type");
				if (object_type == 2) { // 人员计划走下列代码
					String sqlstr = "update " + tablename + " set a0100=object_id";
						dao.update(sqlstr);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				String message=e.toString();
				if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
				{
					PubFunc.resolve8060(this.cn,tablename);
					throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
				}
				else {
                    throw GeneralExceptionHandler.Handle(e);
                }
			}
			YksjParser yp = null;
			if ("custom_formula".equalsIgnoreCase(this.type)) {
                yp=new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");
            } else if ("xishu_formula".equalsIgnoreCase(this.type) || "total_formula".equalsIgnoreCase(this.type)) {
                yp=new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
            }
		 
			// formula = formula.replaceAll("\\[", "");
			// formula = formula.replaceAll("\\]", "");
			yp.setCon(this.cn);
			boolean b = false;
			b = yp.Verify_where(formula.trim());

			if (b) // 校验通过
            {
                errorInfo = "ok";
            } else {
                errorInfo = yp.getStrError();
            }

			if (dbWizard.isExistField(tablename, "A0100", false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.dropColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}
		}
		return errorInfo;
	}
	/**
	 * 校验总分纠偏公式
	 * @param formula
	 * @return
	 * @throws GeneralException
	 */
	public String testDeviationFormula(String formula) throws GeneralException
	{
		String errorInfo = "ok";
		ContentDAO dao = new ContentDAO(this.cn);
		if (formula != null && formula.trim().length() > 0)
		{
			String tablename = "per_result_" + this.plan_id;
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.cn);
			DBMetaModel dbmodel = new DBMetaModel(this.cn);

			if (!dbWizard.isExistField(tablename, "A0100", false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.addColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}

			try {
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id", Integer.parseInt(this.plan_id));
				vo = dao.findByPrimaryKey(vo);
				int object_type =vo.getInt("object_type");
				if (object_type == 2) { // 人员计划走下列代码
					String sqlstr = "update " + tablename + " set a0100=object_id";
						dao.update(sqlstr);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				String message=e.toString();
				if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
				{
					PubFunc.resolve8060(this.cn,tablename);
					throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
				}
				else {
                    throw GeneralExceptionHandler.Handle(e);
                }
			}
			YksjParser yp = null;
			if ("custom_formula".equalsIgnoreCase(this.type)) {
                yp=new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");
            } else if ("xishu_formula".equalsIgnoreCase(this.type) || "total_formula".equalsIgnoreCase(this.type)) {
                yp=new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
            }
		 
			// formula = formula.replaceAll("\\[", "");
			// formula = formula.replaceAll("\\]", "");
			yp.setCon(this.cn);
			boolean b = false;
			b = yp.Verify_where(formula.trim());

			if (b) // 校验通过
            {
                errorInfo = "ok";
            } else {
                errorInfo = yp.getStrError();
            }

			if (dbWizard.isExistField(tablename, "A0100", false))
			{
				Field obj = new Field("A0100");
				obj.setDatatype(DataType.STRING);
				obj.setLength(8);
				obj.setKeyable(false);
				table.addField(obj);
				dbWizard.dropColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}
			//结果表增加字段“reviseScore”
			if (!dbWizard.isExistField(tablename, "reviseScore", false))
			{
				Field obj = new Field("reviseScore");
				obj.setDatatype(DataType.FLOAT);
				obj.setLength(8);
				obj.setDecimalDigits(2);
				//obj.setKeyable(true);
				table = new Table(tablename);
				table.addField(obj);
				dbWizard.addColumns(table);// 更新列
				dbmodel.reloadTableModel(tablename);
			}
		}
		return errorInfo;
	}

	/** 检查公式定义是否正确 */
	public String testformula2(String formula) throws GeneralException
	{
		String errorInfo = "ok";
		ContentDAO dao = new ContentDAO(this.cn);
		if (formula != null && formula.trim().length() > 0)
		{
			String tablename = "per_article";
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.cn);
			DBMetaModel dbmodel = new DBMetaModel(this.cn);
			boolean flag = false;
			if (!dbWizard.isExistField(tablename, "planname", false))
			{
				Field obj = new Field("planname");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);	
				flag = true;
			}
			if (!dbWizard.isExistField(tablename, "b0110_cn", false))
			{
				Field obj = new Field("b0110_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);	
				flag = true;
			}
			if (!dbWizard.isExistField(tablename, "e0122_cn", false))
			{
				Field obj = new Field("e0122_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField(tablename, "e01a1_cn", false))
			{
				Field obj = new Field("e01a1_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if(flag) {
                dbWizard.addColumns(table);// 更新列
            }
			
			try
			{
				String sqlstr = "";
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
				{
					sqlstr = "update per_article set planname=(select name from per_plan where plan_id=" + this.plan_id + ") where plan_id="+this.plan_id;
					dao.update(sqlstr);
					sqlstr = "update per_article set b0110_cn=(select codeitemdesc  from organization  where codeitemid=b0110)";
					dao.update(sqlstr);
					sqlstr = "update per_article set e0122_cn=(select codeitemdesc  from organization  where codeitemid=e0122)";
					dao.update(sqlstr);
					sqlstr = "update per_article set e01a1_cn=(select codeitemdesc  from organization  where codeitemid=e01a1)";
					dao.update(sqlstr);
					
				} else
				{
					sqlstr = "update per_article set planname = per_plan.name from per_plan where per_plan.plan_id=per_article.plan_id";
					dao.update(sqlstr);
					sqlstr = "update per_article set b0110_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.b0110";
					dao.update(sqlstr);
					sqlstr = "update per_article set e0122_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e0122";
					dao.update(sqlstr);
					sqlstr = "update per_article set e01a1_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e01a1";
					dao.update(sqlstr);
				}

			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}

			YksjParser yp = new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");

			yp.setCon(this.cn);
			boolean b = false;
			b = yp.Verify_where(formula.trim());

			if (b) // 校验通过
            {
                errorInfo = "ok";
            } else {
                errorInfo = yp.getStrError();
            }
			flag=false;
			if (dbWizard.isExistField(tablename, "planname", false))
			{
				Field obj = new Field("planname");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);	
				flag=true;
			}
			if (dbWizard.isExistField(tablename, " b0110_cn", false))
			{
				Field obj = new Field("b0110_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);
				flag=true;
			}
			if (dbWizard.isExistField(tablename, "e0122_cn", false))
			{
				Field obj = new Field("e0122_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);
				flag=true;
			}
			if (dbWizard.isExistField(tablename, "e01a1_cn", false))
			{
				Field obj = new Field("e01a1_cn");
				obj.setDatatype(DataType.STRING);
				obj.setLength(100);
				obj.setKeyable(false);
				table.addField(obj);
				flag=true;
			}
			if(flag) {
                dbWizard.dropColumns(table);// 更新列
            }
			dbmodel.reloadTableModel(tablename);
		}
		return errorInfo;
	}

	/**
	 * 语法分析对应公式为sql语句
	 * 
	 * @throws GeneralException
	 */
	public String getSqlByFormula2(String formula) throws GeneralException
	{
		String sql = "";

		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			if (formula != null && formula.trim().length() > 0)
			{
				String tablename = "per_article";
				Table table = new Table(tablename);
				DbWizard dbWizard = new DbWizard(this.cn);
				DBMetaModel dbmodel = new DBMetaModel(this.cn);
				boolean flag=false;
				if (!dbWizard.isExistField(tablename, "planname", false))
				{
					Field obj = new Field("planname");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (!dbWizard.isExistField(tablename, "b0110_cn", false))
				{
					Field obj = new Field("b0110_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (!dbWizard.isExistField(tablename, "e0122_cn", false))
				{
					Field obj = new Field("e0122_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (!dbWizard.isExistField(tablename, "e01a1_cn", false))
				{
					Field obj = new Field("e01a1_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if(flag) {
                    dbWizard.addColumns(table);// 更新列
                }
				dbmodel.reloadTableModel(tablename);
				
				try
				{
					String sqlstr = "";
					if (Sql_switcher.searchDbServer() == Constant.ORACEL)// 如果是ora库就要换一种写法了
					{
						sqlstr = "update per_article set planname=(select name from per_plan where plan_id=" + this.plan_id + ") where plan_id="+this.plan_id;
						dao.update(sqlstr);
						sqlstr = "update per_article set b0110_cn=(select codeitemdesc  from organization  where codeitemid=b0110)";
						dao.update(sqlstr);
						sqlstr = "update per_article set e0122_cn=(select codeitemdesc  from organization  where codeitemid=e0122)";
						dao.update(sqlstr);
						sqlstr = "update per_article set e01a1_cn=(select codeitemdesc  from organization  where codeitemid=e01a1)";
						dao.update(sqlstr);
						
					} else
					{
						sqlstr = "update per_article set planname = per_plan.name from per_plan where per_plan.plan_id=per_article.plan_id";
						dao.update(sqlstr);
						sqlstr = "update per_article set b0110_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.b0110";
						dao.update(sqlstr);
						sqlstr = "update per_article set e0122_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e0122";
						dao.update(sqlstr);
						sqlstr = "update per_article set e01a1_cn = organization.codeitemdesc from organization where organization.codeitemid=per_article.e01a1";
						dao.update(sqlstr);
					}

				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}

				YksjParser yp = new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", "");

				boolean b = false;
				b = yp.Verify_where(formula.trim());

				if (b) // 校验通过
                {
                    sql = yp.getSQL();
                } else {
                    throw new GeneralException(yp.getStrError());
                }

				yp.setVerify(false);
				yp.run(formula.trim(), this.cn, "", tablename);
				sql = yp.getSQL();
				flag=false;
				if (dbWizard.isExistField(tablename, "planname", false))
				{
					Field obj = new Field("planname");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (dbWizard.isExistField(tablename, " b0110_cn", false))
				{
					Field obj = new Field("b0110_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (dbWizard.isExistField(tablename, "e0122_cn", false))
				{
					Field obj = new Field("e0122_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if (dbWizard.isExistField(tablename, "e01a1_cn", false))
				{
					Field obj = new Field("e01a1_cn");
					obj.setDatatype(DataType.STRING);
					obj.setLength(100);
					obj.setKeyable(false);
					table.addField(obj);
					flag=true;
				}
				if(flag) {
                    dbWizard.dropColumns(table);// 更新列
                }
				dbmodel.reloadTableModel(tablename);
			}

		} catch (Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql;
	}

	/**
	 * 语法分析对应公式为sql语句
	 * 
	 * @throws GeneralException
	 */
	public String getSqlByFormula(String formula) throws GeneralException
	{
		String sql = "";

		try
		{

			ContentDAO dao = new ContentDAO(this.cn);
			if (formula != null && formula.trim().length() > 0)
			{
				String tablename = "per_result_" + this.plan_id;
				Table table = new Table(tablename);
				DbWizard dbWizard = new DbWizard(this.cn);
				DBMetaModel dbmodel = new DBMetaModel(this.cn);
				if (!dbWizard.isExistField(tablename, "A0100", false))
				{
					Field obj = new Field("A0100");
					obj.setDatatype(DataType.STRING);
					obj.setLength(8);
					obj.setKeyable(false);
					table.addField(obj);
					dbWizard.addColumns(table);// 更新列
					dbmodel.reloadTableModel(tablename);
				}
				try {
					RecordVo vo = new RecordVo("per_plan");
					vo.setInt("plan_id", Integer.parseInt(this.plan_id));
					vo = dao.findByPrimaryKey(vo);
					int object_type =vo.getInt("object_type");
					if (object_type == 2) { // 人员计划走下列代码
						String sqlstr = "update " + tablename + " set a0100=object_id";
							dao.update(sqlstr);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
				

				YksjParser yp = new YksjParser(this.userview, this.getSelectList(), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");

				// formula = formula.replaceAll("\\[", "");
				// formula = formula.replaceAll("\\]", "");
				yp.setCon(this.cn);//传数据库连接   zhaoxg add 2014-8-8
				boolean b = false;
				b = yp.Verify_where(formula.trim());

				if (b) // 校验通过
                {
                    sql = yp.getSQL();
                } else {
                    throw new GeneralException(yp.getStrError());
                }

				yp.setVerify(false);
				yp.run(formula.trim(), this.cn, "", tablename);
				// yp.run(formula.trim());
				sql = yp.getSQL();

				if (dbWizard.isExistField(tablename, "A0100", false))
				{
					Field obj = new Field("A0100");
					obj.setDatatype(DataType.STRING);
					obj.setLength(8);
					obj.setKeyable(false);
					table.addField(obj);
					dbWizard.dropColumns(table);// 更新列
					dbmodel.reloadTableModel(tablename);
				}
			}

		} catch (Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql;
	}
}
