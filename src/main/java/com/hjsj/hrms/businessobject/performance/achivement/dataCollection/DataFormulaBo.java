package com.hjsj.hrms.businessobject.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>Title:DataFormulaBo.java</p>
 * <p>Description:根据公式计算考核指标实际值</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-08-09 10:23:41</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DataFormulaBo 
{

	private Connection con = null;
	private UserView userView = null;
	private String plan_id = ""; // 考核计划编号
	private RecordVo planVo = null;
	
	public DataFormulaBo(Connection a_con,UserView userView,String plan_id)
	{
		this.con = a_con;
		this.userView = userView;
		this.plan_id = plan_id;
		this.planVo = this.getPerPlanVo(plan_id);
		
	}
	
	
	
	
	
	/**
	 * 判断当前计划是是否出现指标评分优先取自关键事件选项 
	 * @return
	 */
	public boolean isShowScoreFromKey()
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			String template_id=this.planVo.getString("template_id");
			String sql="";
			if(this.planVo.getInt("object_type")!=2) //团队
			{
				sql="  select count(point_id) from  per_key_event where Object_type=1 and (status is null or status='03')  and nullif(point_id,'') is not null "+getMatchingTimeSql();
				sql+=" and point_id in (select  point_id  from per_template_item pi,per_template_point pp  where pi.item_id=pp.item_id    and template_id='"+template_id+"'  ) ";
				sql+=" and b0110 in (select object_id from per_object where plan_id="+this.plan_id+")";
			}
			else
			{
				sql="  select count(point_id) from  per_key_event where Object_type=2 and (status is null or status='03')   and nullif(point_id,'') is not null  "+getMatchingTimeSql();
				sql+=" and point_id in (select  point_id  from per_template_item pi,per_template_point pp  where pi.item_id=pp.item_id    and template_id='"+template_id+"'  ) ";
				sql+=" and a0100 in (select object_id from per_object where plan_id="+this.plan_id+")";
			}
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
					flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	  
    
    //取得计划与关键事件匹配的sql片断
    public String getMatchingTimeSql()
    {
    	StringBuffer whl=new StringBuffer("");
    	int cycle=this.planVo.getInt("cycle");  //考核周期
    	switch(cycle)
    	{
    		case 0:  //年度
    			whl.append(" and "+Sql_switcher.year("busi_date")+"="+this.planVo.getString("theyear"));
    			break;
    		case 1:  //半年度
    			whl.append(" and "+Sql_switcher.year("busi_date")+"="+this.planVo.getString("theyear"));
    			String thequarter=this.planVo.getString("thequarter");
    			if("01".equals(thequarter)) //上半年
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=1");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=6");
    			}
    			else if("02".equals(thequarter)) //下半年
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=7");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=12");
    			}
    			break;
    		case 2:  //季度
    			whl.append(" and "+Sql_switcher.year("busi_date")+"="+this.planVo.getString("theyear"));
    			String athequarter=this.planVo.getString("thequarter");
    			if("01".equals(athequarter)) //1季度
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=1");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=3");
    			}
    			if("02".equals(athequarter)) //2季度
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=4");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=6");
    			}
    			if("03".equals(athequarter)) //3季度
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=7");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=9");
    			}
    			if("04".equals(athequarter)) //4季度
    			{
    				whl.append(" and "+Sql_switcher.month("busi_date")+">=10");
    				whl.append(" and "+Sql_switcher.month("busi_date")+"<=12");
    			}
    			
    			break;
    		case 3:  //月度
    			whl.append(" and "+Sql_switcher.year("busi_date")+"="+this.planVo.getString("theyear"));
    			whl.append(" and "+Sql_switcher.month("busi_date")+"="+this.planVo.getString("themonth"));
    			break;
    		case 7:  //不定期
    			Date start_date=this.planVo.getDate("start_date");
    			Calendar sd=Calendar.getInstance();
    			sd.setTime(start_date);
    			Date end_date=this.planVo.getDate("end_date");
    			Calendar ed=Calendar.getInstance();
    			ed.setTime(end_date);
    			whl.append(" and ( "+Sql_switcher.year("busi_date")+">"+sd.get(Calendar.YEAR));
    			whl.append(" or ( "+Sql_switcher.year("busi_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("busi_date")+">"+(sd.get(Calendar.MONTH)+1)+" ) ");
    			whl.append(" or ( "+Sql_switcher.year("busi_date")+"="+sd.get(Calendar.YEAR)+" and "+Sql_switcher.month("busi_date")+"="+(sd.get(Calendar.MONTH)+1)+" and "+Sql_switcher.day("busi_date")+">="+sd.get(Calendar.DATE)+" ) ) ");
    			
    			whl.append(" and ( "+Sql_switcher.year("busi_date")+"<"+ed.get(Calendar.YEAR));
    			whl.append(" or ( "+Sql_switcher.year("busi_date")+"="+ed.get(Calendar.YEAR)+" and "+Sql_switcher.month("busi_date")+"<"+(ed.get(Calendar.MONTH)+1)+" ) ");
    			whl.append(" or ( "+Sql_switcher.year("busi_date")+"="+ed.get(Calendar.YEAR)+" and "+Sql_switcher.month("busi_date")+"="+(ed.get(Calendar.MONTH)+1)+" and "+Sql_switcher.day("busi_date")+"<="+ed.get(Calendar.DATE)+" ) ) ");
    			
    			break;
    	}
    	
    	return whl.toString();
    }      
	
		
	// 定义公式可选择的指标
	public ArrayList getSelectList()
	{
		ArrayList filelist = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			//加指标
			String sql = "select item_id,itemdesc from per_kpi_item order by item_id desc";
				       
			rowSet = dao.search(sql);
			while(rowSet.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rowSet.getString("item_id"));
				item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("itemdesc")));
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				filelist.add(item);
			}
					
			if (rowSet != null)
				rowSet.close();
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return filelist;
	}
	
	/**
     * 取得统一打分指标定义公式的KPI指标代码
     * @return list
     */
	public HashMap getPointFormulaList(ArrayList pointList)
	{
	
		HashMap keyMap = new HashMap();
		try
		{			
			// 解析公式
			YksjParser yp = new YksjParser(this.userView, this.getSelectList(), YksjParser.forSearch, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");		
			
			/** 考核指标 打分权限范围内的并且定义了计算公式的指标列表，统一打分指标 某计划的 */			 
//			ArrayList pointList = getPointList(); 
						
			for(int i = 0; i < pointList.size(); i++)
			{
	       		LazyDynaBean abean = (LazyDynaBean)pointList.get(i);
//	       		String point_id = (String)abean.get("point_id");		       							
//				String pointname = (String)abean.get("pointname");	
				String formula = (String)abean.get("formula");	
				
				ArrayList fieldList = yp.getFormulaFieldList1(formula);
				for(int j = 0; j < fieldList.size(); j++)
				{
					FieldItem fielditem = (FieldItem)fieldList.get(j);
					
					if(keyMap.get(fielditem.getItemid())==null)
					{
						keyMap.put(fielditem.getItemid(),fielditem.getItemdesc());
					}										
				}								
			}
					    						
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return keyMap;
	}
	
	/**
	 * 考核指标 打分权限范围内的并且定义了计算公式的指标列表， 统一打分指标 某计划的
	 */
	public ArrayList getPointList() throws GeneralException
	{

		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if (this.planVo.getInt("method")==2)// 目标管理的考核计划
		{
			// 取出指标(过滤出绩效的指标)包括个性和共性
			// 取出指标(过滤出绩效的指标)包括个性和共性
			sql.append("select per_point.point_id,per_point.pointname,per_point.seq,per_point.formula,per_point.pointctrl from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.plan_id);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1 and (per_point.pointtype=0 or per_point.pointtype is null ) ");
			
			sql.append(" union all ");
			
			sql.append("select pp.point_id,pp.pointname,pp.seq,pp.formula,pp.pointctrl from per_point pp join (");
			sql.append("select distinct p0401 from p04 where fromflag=2 and plan_id="+this.plan_id);
			sql.append(" and ((chg_type <> 3) or (chg_type is null)) ");
			sql.append(" and item_id in ");
			sql.append("(select item_id from per_template_item where kind=2 and template_id=(select template_id from per_plan where plan_id="+this.plan_id+"))");
			sql.append(") a on pp.point_id=a.p0401 and pp.status=1 and pp.pointkind=1 and (pp.pointtype=0 or pp.pointtype is null ) ");
			
		} else
		// 360考核计划
		{
			sql.append("select per_point.point_id,per_point.pointname,per_point.formula,per_point.pointctrl from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id='");
			sql.append(this.plan_id);
			sql.append("')) and per_point.status=1 and per_point.pointkind=1 and (per_point.pointtype=0 or per_point.pointtype is null ) order by per_point.seq");
		}
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(sql.toString());
			while (rs.next())
			{
				LazyDynaBean abean = new LazyDynaBean();
				String point_id = rs.getString("point_id")!=null?rs.getString("point_id"):"";
				String pointname = rs.getString("pointname")!=null?rs.getString("pointname"):"";
				String formula = rs.getString("formula");				
				
				//  过滤定量统一打分基本指标中的录分指标
				String Pointctrl=Sql_switcher.readMemo(rs,"pointctrl");
				HashMap map=PointCtrlXmlBo.getAttributeValues(Pointctrl);
				String computeRule=(String)map.get("computeRule");
				if(computeRule==null|| "0".equals(computeRule))
					continue;
				//  过滤定量统一打分基本指标中的无指标权限指标
				if(!(this.userView.isSuper_admin()))
				{
					if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id))
					{
						continue;
					}
				}
				//  过滤定量统一打分基本指标中的无计算公式指标
				if(formula==null || formula.trim().length()<=0)
					continue;
				
				abean.set("point_id", point_id);
				abean.set("pointname", pointname);
				abean.set("formula", formula);									
				list.add(abean);				
			}
			if (rs != null)
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();			
		}
		return list;
	}
	
	/**	
	 * 新建指标计算公式临时表
	 */
	public void builtKpiFormulaTable(ArrayList list)
	{		
		String itemidSql = "";
		String itemidSum = "";
		StringBuffer buff = new StringBuffer();	
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			
			//  新建临时表 
			String tablename = "t#_"+this.userView.getUserName()+"_per_cj";			
			DbWizard dbWizard = new DbWizard(this.con);	
			// 此临时表若存在就先drop掉
			if(dbWizard.isExistTable(tablename,false))
			{
				dbWizard.dropTable(tablename);				
			}			

			Table table = new Table(tablename);				
		    table.addField(getField("plan_id", "I", 4, false));
		    table.addField(getField("object_id", "A", 50, false));
		    	
		    HashMap keyMap = getPointFormulaList(list);	// 取得统一打分指标定义公式的KPI指标代码	
		    
//		    if(keyMap!=null && keyMap.size()>0)
		    {
				Set keySet=keyMap.keySet();
				java.util.Iterator t=keySet.iterator();
				int number = keyMap.size();
				int count = 0;
				while(t.hasNext())
				{
					count++;
					String item_id = (String)t.next();  //键值	    
					String itemdesc = (String)keyMap.get(item_id);   //value值   					
					itemidSql += ","+item_id;
					table.addField(getField(item_id, "F", 8, false));
						
					itemidSum += (",SUM("+ item_id +") "+ item_id);					
					if(count==number)
						buff.append(" case item_id when '"+ item_id +"' then actual_value else null end "+ item_id );
					else
						buff.append(" case item_id when '"+ item_id +"' then actual_value else null end "+ item_id +",");
				}		    	
			    dbWizard.createTable(table);
				
				
				// 向临时表中写入记录
				StringBuffer buf = new StringBuffer();	
				buf.append(" select id,object_id"+ itemidSum);			
				buf.append(" from (select '"+ this.plan_id +"' id,object_id ");	
				if(buff!=null && buff.toString().trim().length()>0)
					buf.append(" ,"+buff.toString());			
				buf.append(" from per_kpi_data where ");
				if(this.planVo.getInt("object_type")==2)
					buf.append(" object_type='2' ");
				else
					buf.append(" object_type='1' ");
				buf.append(" and status='03' and cycle='"+ this.planVo.getInt("cycle") +"' ");
				
				if(this.planVo.getInt("cycle")==0)  // 年度
					buf.append(" and theyear='"+ this.planVo.getString("theyear") +"' ");				
				else if(this.planVo.getInt("cycle")==1) // 半年	
				{
					if(this.planVo.getString("thequarter").indexOf("0")!=-1)					
						buf.append(" and theyear='"+ this.planVo.getString("theyear") +"' and thequarter='"+ this.planVo.getString("thequarter") +"' ");
					else
						buf.append(" and theyear='"+ this.planVo.getString("theyear") +"' and thequarter='0"+ this.planVo.getString("thequarter") +"' ");
				}
				else if(this.planVo.getInt("cycle")==2) // 季度			
					buf.append(" and theyear='"+ this.planVo.getString("theyear") +"' and thequarter='"+ this.planVo.getString("thequarter") +"' ");									
				else if(this.planVo.getInt("cycle")==3) // 月度			
					buf.append(" and theyear='"+ this.planVo.getString("theyear") +"' and themonth='"+ this.planVo.getString("themonth") +"' ");
				
				buf.append(" ) A GROUP BY id,object_id ");
	//			buf.append("  ");
				
				StringBuffer bufSql = new StringBuffer();	
				bufSql.append("insert into "+ tablename +" (plan_id,object_id"+ itemidSql +") "); // values (?,?"+ sqlValue +") ");
				bufSql.append(buf.toString());
				
				
				dao.insert(bufSql.toString(), new ArrayList());
		    }			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**	
	 *  根据指标计算公式计算出所有值并保存入库
	 */
	public void getFormulaSql(ArrayList pointList)
	{		
		String tablename = "t#_"+this.userView.getUserName()+"_per_cj";			
		RowSet rs = null;
		try
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
			String onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
//			if(onlyFild==null || onlyFild.length()<=0)
//				throw new GeneralException("系统没有指定唯一性指标！请指定唯一性指标！");
			
			ContentDAO dao = new ContentDAO(this.con);

			// 此临时表若不存在就不操作
			DbWizard dbWizard = new DbWizard(this.con);	
			if(dbWizard.isExistTable(tablename,false))
			{			
				YksjParser yp = new YksjParser(this.userView, this.getSelectList(), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");
				yp.setVerify(false);
				
				/**考核指标 打分权限范围内的并且定义了计算公式的指标列表， 统一打分指标 某计划的*/			 
	//			ArrayList pointList = getPointList(); 
							
				for(int i = 0; i < pointList.size(); i++)
				{
		       		LazyDynaBean abean = (LazyDynaBean)pointList.get(i);
		       		String point_id = (String)abean.get("point_id");		       							
					String formula = (String)abean.get("formula");	
					
					yp.run(formula.trim(), this.con, "", tablename);				
					
					DataCollectBo bo = new DataCollectBo(this.con, this.plan_id, point_id, this.userView);
					/** 获得某考核计划的管理权限范围内的考核对象 */
					ArrayList allObjs = bo.getKhObjs();	 
	
	/*				String objs=null;					
					String[] params=null;
					if(paramStr!=null && !paramStr.equals(""))
					    params=paramStr.split("&");
					if(params!=null && params.length>0 && params[0].trim().length()>0)
					    objs=params[0];	
					else
						return;   // 此时 界面没有考核对象 所以不做自动保存操作了
					ArrayList nowPageObjs = new ArrayList();
					for(int k=0;k<allObjs.size();k++)
					{
					    LazyDynaBean bean = (LazyDynaBean)allObjs.get(k);
					    String objectId = (String)bean.get("object_id");
					    if(objs.indexOf(objectId)!=-1)
					    {
							nowPageObjs.add(bean);
					    }		
					}
	*/				
					/** 基本指标计分规则 （0:录分｜ 1:简单｜2:分段｜3:排名） */
					String rule = bo.getRule();
					/** 指标类型 0|1|2:基本指标|加分指标|扣分指标 */				 
				    String pointype = bo.getTypeOfPoint();
					
					HashMap basicFenMap = bo.getBasicFen(); // 取得某计划下某考核指标的某考核对象的基本分 对于目标管理的计划取自p04表和人和指标有关				
					HashMap standardFenMap = bo.getStandardFens();  // 取得某计划下某考核指标的某考核对象的标准分				
									
					String paramStr = "";
					String objstr = "";
					String dfValue = "";
					String standardVal = "";
					String basicVal = "";
					String praticalVal = "";
					String addVal = "";
					String deducVal = "";
					
					for (int j = 0; j < allObjs.size(); j++)
					{
						LazyDynaBean bean = (LazyDynaBean) allObjs.get(j);
						
						String object_id = (String) bean.get("object_id");	
						String onlyName = "";
						if ((this.planVo.getInt("object_type")==2) && onlyFild != null && onlyFild.length()>0)
							onlyName = (String) bean.get(onlyFild);
						
						if(object_id!=null && object_id.trim().length()>0)
						{	
							// 基本分
							String basicScore = basicFenMap.get(object_id)==null?"0":(String) basicFenMap.get(object_id);
							// 标准分
							String standardScore = standardFenMap.get(object_id)==null?"0":(String) standardFenMap.get(object_id);
							// 实际值
							String praticalScore = "0";
							StringBuffer buff = new StringBuffer();	
							if ((this.planVo.getInt("object_type")==2) && onlyFild != null && onlyFild.length()>0)
								buff.append("select (" + yp.getSQL() + ") praticalVal from " + tablename + " where object_id='"+ onlyName +"' ");
							else
								buff.append("select (" + yp.getSQL() + ") praticalVal from " + tablename + " where object_id='"+ object_id +"' ");
							rs = dao.search(buff.toString());
							while (rs.next())
							{
								praticalScore = rs.getString("praticalVal")!=null?rs.getString("praticalVal"):"0";						
							}
							
							//基本型指标简单和分段计算规则的数据计算
							HashMap map = bo.basciPointCalcu(praticalScore,basicScore,standardScore);	
							String addScore = (String)map.get("addF");      // 加分
							String deducScore = (String)map.get("deducF");  // 扣分
							String dfScore = (String)map.get("objDF");    // 得分
							
							objstr+=object_id+"<@>";
							dfValue+=object_id+"_df="+dfScore+"<@>";			
							standardVal+=object_id+"_standard="+standardScore+"<@>";
							basicVal+=object_id+"_basic="+basicScore+"<@>";
							praticalVal+=object_id+"_pratical="+praticalScore+"<@>";
							addVal+=object_id+"_add="+addScore+"<@>";
							deducVal+=object_id+"_deduc="+deducScore+"<@>";		
						}
					}	
					paramStr = objstr+"&"+standardVal+"&"+praticalVal+"&"+basicVal+"&"+addVal+"&"+deducVal+"&"+dfValue;
					
					if(("0".equals(pointype)) && ("3".equals(rule)))// 排名的保存
					{
						StringBuffer standardVals = new StringBuffer(standardVal);
						StringBuffer praticalVals = new StringBuffer(praticalVal);
						StringBuffer basicVals = new StringBuffer(basicVal);
								
						bo.save2(standardVals.toString().split("<@>"), praticalVals.toString().split("<@>"), basicVals.toString().split("<@>"), allObjs);
						
					}else if (("0".equals(pointype)) && ("1".equals(rule) || "2".equals(rule))) // 简单｜分段的保存
					{
						
						String[] standardVals = standardVal.split("<@>");
						String[] praticalVals = praticalVal.split("<@>");
						String[] basicVals = basicVal.split("<@>");						
						String[] addVals = addVal.split("<@>");
						String[] deducVals = deducVal.split("<@>");
						String[] dfScores = dfValue.split("<@>");
														
						bo.save1(standardVals, praticalVals, basicVals, addVals, deducVals, dfScores, allObjs);					
					}
						
					//DataCollectBo.roundAndRemoveZero(basicVal, 3);					
	/*				// 用实际值批量更新per_gather_planId表中的T_XXX字段
					StringBuffer buff = new StringBuffer();	
					buff.append("update per_gather_" + this.plan_id + " set T_" + point_id + "= ");
					buff.append("(select (" + yp.getSQL() + ") from " + tablename + " where object_id='"+ object_id +"') where object_id='"+ object_id +"' ");
						
					dao.update(buff.toString());
	*/																		
				}
			}
			if (rs != null)
				rs.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
	
	/**
     * 取得考核计划信息
     * @return vo
     */
	public RecordVo getPerPlanVo(String planid)
	{
	
		RecordVo vo = new RecordVo("per_plan");
		try
		{
			if(planid==null || planid.trim().length()<=0)
				return null;
		    ContentDAO dao = new ContentDAO(this.con);
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
	}
	public HashMap hasDefine(ArrayList definelist){
		HashMap hasdefine=new HashMap();
		if(definelist!=null&&definelist.size()>0){
			for(int i=0;i<definelist.size();i++){
				LazyDynaBean bean =(LazyDynaBean)definelist.get(i);
				String id=(String)bean.get("id");
				hasdefine.put(id, bean);
			}
		}
		return hasdefine;
	}
	/**
	 * 绩效评估定义分值范围
	 * 取得该模板下所有指标关联指标
	 * */
	public ArrayList getlist(String planid,HashMap hasDefine){
		ArrayList list=new ArrayList();
		ArrayList biglist=new ArrayList();
		ArrayList biglist2=new ArrayList();
		StringBuffer sql=new StringBuffer();
		if (this.planVo.getInt("method")==2){// 目标考核
			sql.append("select  P.* from per_point P,(select distinct p0401 from p04 where plan_id = ");
			sql.append(planid);
			sql.append("  and fromflag =2 ) O where P.point_id = O.p0401 order by P.seq");
		}
		else{//360考核
			sql.append("select per_point.point_id,per_point.pointname,per_point.formula,per_point.pointctrl from per_template_point,per_point where ");
			sql.append("per_template_point.point_id=per_point.point_id and item_id in (select item_id from per_template_item where template_id=");
			sql.append("(select " + Sql_switcher.isnull("template_id", "''") + " from per_plan where plan_id=");
			sql.append(this.plan_id);
			sql.append("))order by per_point.seq");
		}
		RowSet rs = null;
		
		try {
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(sql.toString());
			while (rs.next())
			{
				boolean flag=false;
				LazyDynaBean abean = new LazyDynaBean();
				String point_id = rs.getString("point_id")!=null?rs.getString("point_id"):"";
				String pointname = rs.getString("pointname")!=null?rs.getString("pointname"):"";
				String formula = rs.getString("formula");	
				if(!(this.userView.isSuper_admin()))
				{
					if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD,point_id))
					{
						continue;
					}
				}
				abean.set("id", point_id);
				if(hasDefine!=null&&hasDefine.size()>0){
					if(hasDefine.get(point_id)!=null){
						LazyDynaBean abean1 =(LazyDynaBean)hasDefine.get(point_id);
						abean.set("minscore",(String)abean1.get("minscore")==null?"":(String)abean1.get("minscore"));
						abean.set("maxscore", (String)abean1.get("maxscore")==null?"":(String)abean1.get("maxscore"));
						flag=true;
					}else{
						abean.set("minscore", "");
						abean.set("maxscore", "");
					}
				}else{
					abean.set("minscore", "");
					abean.set("maxscore", "");
				}
				abean.set("pointname", pointname);
				abean.set("formula", formula);
				abean.set("type", "1");
				
				biglist.add(abean);	
				if(flag){
					biglist2.add(abean);	
				}
			}
			list.add(biglist);
			list.add(biglist2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
	}
}