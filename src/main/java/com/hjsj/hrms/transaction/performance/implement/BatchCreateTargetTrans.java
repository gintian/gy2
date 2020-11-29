package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:BatchCreateTargetTrans.java</p>
 * <p>Description:考核实施批量生成目标卡</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-20 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */
public class BatchCreateTargetTrans extends IBusiness
{
	public void execute() throws GeneralException
	{

		String plan_id = (String) this.getFormHM().get("plan_id");
		ContentDAO dao = new ContentDAO(this.frameconn);

		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String object_type = String.valueOf(vo.getInt("object_type")); // 2：人员
		String template_id = vo.getString("template_id");
		
		StringBuffer obj = new StringBuffer();// 前台勾选的考核对象。不为空为只对选择人员批量生成，为空则为所有考核对象生成 chent 20170317 add
		String objectIDs = (String)this.getFormHM().get("objectIDs");
		if(!StringUtils.isEmpty(objectIDs)){
			String[] objs = objectIDs.split("`");
			for(int i=0;i<objs.length;i++)
			{
				String object_id = objs[i];
				if(object_id.trim().length()>0)
				{
					obj.append(",'"+object_id+"'");
				}
			}
		}
		
		String code=(String)this.getFormHM().get("code");
		String whl = "" ;//根据用户权限先得到一个考核对象的范围
		String privWhl = pb.getPrivWhere(userView);
		whl+=privWhl;
		if(code!=null)
		{
			if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
				whl+=" and b0110 like '"+code+"%'";
			else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
				whl+=" and e0122 like '"+code+"%'";
			
		}
		
		StringBuffer selKhObjs = new StringBuffer();
		// 取的考核对象或者团队负责人
		if ("2".equals(object_type))
			selKhObjs.append("select object_id as a0100,b0110,e0122,e01a1,a0101 from per_object where plan_id="+plan_id+" "+whl);
		else
		{
			selKhObjs.append("select o.object_id as a0100,o.b0110,o.e0122,m.e01a1,o.a0101 from per_mainbody m,per_object o where m.body_id=-1 and m.plan_id="+plan_id);
			selKhObjs.append(" and o.plan_id="+plan_id+" and o.object_id=m.object_id ");
			if(whl.length()>0)
		    {
				selKhObjs.append(" and o.object_id in (select object_id from per_object where plan_id="+plan_id+" "+whl+")");
		    }
		}
		// 前台勾选的考核对象。不为空为只对选择人员批量生成，为空则为所有考核对象生成 chent 20170317 add
		if(obj.length() > 0){
			selKhObjs.append(" and object_id in ("+obj.substring(1)+")");
		}
		
		this.getFormHM().put("msg", "");
		ArrayList<String> checklist = this.checkPerObject(plan_id, selKhObjs.toString());
		if(checklist.size() > 0){
			String msg = "", personmsg = "";
			
			for(int i=0; i<checklist.size()&&i<5; i++){
				String objectname = checklist.get(i);
				personmsg += objectname;
				if(i < checklist.size()-1 && i < 4){
					personmsg += ",";
				}
			}
			if(checklist.size() >= 5){
				personmsg += "等";
			}
			
			msg = personmsg+"已经被评价，不能生成目标卡！请选择没有被评价的考核对象。";
			this.getFormHM().put("msg", msg);
			return ;
		}
		
		ArrayList khObjList = new ArrayList();
		try
		{
			RowSet rs = dao.search(selKhObjs.toString());
			while (rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("a0100", rs.getString("a0100")==null?"":rs.getString("a0100"));
				bean.set("b0110", rs.getString("b0110")==null?"":rs.getString("b0110"));
				bean.set("e0122", rs.getString("e0122")==null?"":rs.getString("e0122"));
				bean.set("e01a1", rs.getString("e01a1")==null?"":rs.getString("e01a1"));
				bean.set("a0101", rs.getString("a0101")==null?"":rs.getString("a0101"));
				khObjList.add(bean);
			}
			
			//判断p04表中是否已为对象产生模板内的共性指标，没有则自动产生
			String point_explain_item = "";
			String point_evaluate_item = "";
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.frameconn);
			Hashtable ht_table=bo.analyseParameterXml();
			if(ht_table!=null)
			{
				if(ht_table.get("DescriptionItem")!=null)
					point_explain_item=(String)ht_table.get("DescriptionItem");
				
				if(ht_table.get("PrincipleItem")!=null)
					point_evaluate_item=(String)ht_table.get("PrincipleItem");
			}
			for (int j = 0; j < khObjList.size(); j++)
			{
				LazyDynaBean bean = (LazyDynaBean) khObjList.get(j);			
				executeP04_commonnessData(bean,vo,point_evaluate_item,point_explain_item);
			}		
			
			String status = "0";
			float template_score=0;
			String sqlStr = "select topscore,status from per_template where template_id=(select template_id from per_plan where plan_id="+plan_id+")";
			rs = dao.search(sqlStr);
			if (rs.next())
			{
				status = rs.getString(2);
				template_score = rs.getFloat(1);
			}
			
			
			// 先删除上次批量生成目标卡生成的记录
			// fromflag = "3"; 目标卡来源岗位职责			
			
//			pb.getPrivWhere(userView);						
			
			String delSql = "delete from p04 where plan_id=" + plan_id + " and fromflag=3 and ((chg_type <> 3) or (chg_type is null)) "+ pb.getPrivWhere(userView);
			if(obj.length() > 0){
				delSql += " and a0100 in ("+obj.substring(1)+")";
			}
			dao.delete(delSql, new ArrayList());

			ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
			String accordString = xml.getTextValue("/Per_Parameters/TargetPostDuty");
			String postSet = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "SubSet");
			String targetItem = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "TargetItem");
			HashMap itemIdMap = getItemid(template_id);
			
			LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = loadxml.getDegreeWhole();
			String dutyRule = (String) params.get("DutyRule");
			String[] temp1 = dutyRule.split("\\|");
			String _sql = "";
			if(temp1.length>1){
				boolean like = false;
//		    	if("1".equals(temp1[2]))
//		    		like=true;
		    	boolean history=false;
//		        if("1".equals(temp1[3]))
//		        	history=true;
				FactorList factorslist=new FactorList(temp1[0],PubFunc.getStr(temp1[1]),"Usr",history,like,false,0,userView.getUserId());
				factorslist.setSuper_admin(userView.isSuper_admin());
		        String sqlexp = factorslist.getSingleTableSqlExpression(postSet);
		        if(sqlexp!=null&&sqlexp.length()>0){
		        	_sql = " and "+sqlexp;
		        }
			}
			ArrayList p04voList = new ArrayList();
			for (int j = 0; j < khObjList.size(); j++)
			{
				LazyDynaBean bean = (LazyDynaBean) khObjList.get(j);
				String a0100 = (String) bean.get("a0100");
				String b0110 = (String) bean.get("b0110");
				String e0122 = (String) bean.get("e0122");
				String e01a1 = (String) bean.get("e01a1");
				String a0101 = (String) bean.get("a0101");

				String selPostSet = "select * from " + postSet + " where e01a1='" + e01a1 + "' "+_sql+" order by i9999";
				rs = dao.search(selPostSet);
				while (rs.next())
				{
					RecordVo p04Vo = new RecordVo("p04");
					IDGenerator idg = new IDGenerator(2, this.getFrameconn());
					String id = idg.getId("P04.P0400");
					p04Vo.setInt("p0400", Integer.parseInt(id));
					p04Vo.setInt("state", 0);
					p04Vo.setInt("itemtype", 0);
					p04Vo.setInt("fromflag", 3);
					p04Vo.setInt("plan_id", Integer.parseInt(plan_id));
					if ("2".equals(object_type))
					{
						p04Vo.setString("a0100", a0100);
						p04Vo.setString("b0110", b0110);
						p04Vo.setString("e0122", e0122);
						p04Vo.setString("e01a1", e01a1);
						p04Vo.setString("a0101", a0101);
						p04Vo.setString("nbase", "USR");
					}else
					{
						p04Vo.setString("b0110", a0100);
						p04Vo.setString("e0122", e0122);
						p04Vo.setString("a0101", a0101);
					}
					if("1".equals(status))//权重模板
					{
						p04Vo.setDouble("p0413", template_score);
						p04Vo.setDouble("p0415", 0);
					}
					else
					{
						p04Vo.setDouble("p0415", 1);
					}
					// 变动的
										
					if(targetItem!=null && targetItem.trim().length()>0)
					{
						FieldItem fielditem = DataDictionary.getFieldItem(targetItem);
						String useFlag = fielditem.getUseflag(); 
						if("0".equalsIgnoreCase(useFlag))
							throw new GeneralException("["+ fielditem.getItemdesc() +"]指标未构库,请构库后再进行此操作！");	
						String item = PubFunc.keyWord_reback(rs.getString(targetItem));
						if (rs.getString(targetItem) != null && itemIdMap.get(item)!=null)// 对应项目号
							p04Vo.setInt("item_id", ((Integer) itemIdMap.get(item)).intValue());
						else if (rs.getString(targetItem) != null && itemIdMap.get(rs.getString(targetItem))!=null)// 对应项目号
							p04Vo.setInt("item_id", ((Integer) itemIdMap.get(rs.getString(targetItem))).intValue());
						else 
							continue;
					}
					String item_id = "";
					if ("2".equals(object_type))
						item_id = (e01a1 + "_" + rs.getInt("i9999"));
					else
						item_id = (a0100 + "_" + rs.getInt("i9999"));
					
					String sql = "";
					if ("2".equals(object_type))
						sql = "select p0401 from p04 where plan_id=" + plan_id + " and a0100='" + a0100 + "' and ((chg_type <> 3) or (chg_type is null))";
					else
						sql = "select p0401 from p04 where plan_id=" + plan_id + " and b0110='" + a0100 + "' and ((chg_type <> 3) or (chg_type is null))";
					RowSet rowSet = dao.search(sql);
					String haveOrNo = "nodot";
					while (rowSet.next())
					{
						String p0401 = rowSet.getString("p0401");
						if(item_id.equalsIgnoreCase(p0401))
						{
							haveOrNo = "nodoted";
							break;
						}						
					}
					if("nodot".equalsIgnoreCase(haveOrNo))
					{
						if ("2".equals(object_type))
							p04Vo.setString("p0401", e01a1 + "_" + rs.getInt("i9999"));// 任务编码
						else
							p04Vo.setString("p0401", a0100 + "_" + rs.getInt("i9999"));// 任务编码
					}
					else
						continue;
//					p04Vo.setString("p0401", e01a1 + "_" + rs.getInt("i9999"));// 任务编码
					p04Vo.setInt("seq", rs.getInt("i9999"));
					// 岗位职责参数中设置的字段对应
					String[] accords = accordString.split(","); // P0407=K1502,P0419=K1503
					for (int m = 0; m < accords.length; m++)
					{
						if (accords[m].trim().length() > 0)
						{
							String replaceStr = "=";
							if(accords[m].trim().indexOf("＝") > -1){
								replaceStr = "＝";
							}
							String[] temp = accords[m].trim().split(replaceStr);
							String p04Field = temp[0].toLowerCase();
							String postSetField = temp[1].toLowerCase();
							FieldItem item = DataDictionary.getFieldItem(p04Field);								
							
							String itemtype = item.getItemtype();
							int decimalwidth = item.getDecimalwidth();
							String str = "";
							try{
								str = (String)rs.getString(postSetField);
							}catch(Exception e){
							}
							if ((str!= null) && (str.trim().length()>0))
							{
								if ("N".equalsIgnoreCase(itemtype))
								{
									if (decimalwidth == 0)
									{
										int value = Integer.parseInt(PubFunc.round(rs.getString(postSetField), 0));
										p04Vo.setInt(p04Field, value);
									} else
										p04Vo.setDouble(p04Field, rs.getDouble(postSetField));

								} else if ("A".equalsIgnoreCase(itemtype) || "M".equalsIgnoreCase(itemtype))
									p04Vo.setString(p04Field, rs.getString(postSetField));
								else if ("D".equalsIgnoreCase(itemtype))
									p04Vo.setDate(p04Field, rs.getDate(postSetField));
							}
						}
					}
					p04voList.add(p04Vo);
				}
			}
			dao.addValueObject(p04voList);
			
			if(rs!=null)
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("resultFlag", "0");
			throw GeneralExceptionHandler.Handle(e);
		}
		

		this.getFormHM().put("resultFlag", "1");
	}

	/**
	 * 判断p04表中是否已为对象产生模板内的共性指标，没有则自动产生
	 */
	public void  executeP04_commonnessData(LazyDynaBean bean,RecordVo vo,String point_evaluate_item,String point_explain_item)
	{
		try
		{
		    String object_id = (String) bean.get("a0100");
			String b0110 = (String) bean.get("b0110");
			String e0122 = (String) bean.get("e0122");
			String e01a1 = (String) bean.get("e01a1");
			String a0101 = (String) bean.get("a0101");
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql=new StringBuffer("select count(p0400) from P04 where plan_id="+vo.getInt("plan_id"));
			if(vo.getInt("object_type")==1||vo.getInt("object_type")==3||vo.getInt("object_type")==4)
				sql.append(" and b0110='"+object_id+"'");
			else if(vo.getInt("object_type")==2)
				sql.append(" and a0100='"+object_id+"'");
			sql.append(" and P0401 in (");
			sql.append("select ptp.point_id from per_template_item pti,per_template_point ptp,per_point pp");
			sql.append(" where pti.item_id=ptp.item_id and ptp.point_id=pp.point_id and pti.template_id='"+(String)vo.getString("template_id")+"')");
			
			RowSet rowSet=dao.search(sql.toString());
			int count=0;
			if(rowSet.next())
			{
				count=rowSet.getInt(1);
			}
			if(count==0)
			{
				sql.setLength(0);
				sql.append("insert into p04 (p0400,b0110,");
				if(vo.getInt("object_type")==2)
					sql.append("e0122,e01a1,nbase,a0100,a0101,");
				sql.append("p0401,p0407,p0413,p0415,plan_id,Item_id,fromflag,state,seq )values(?,?,");
				if(vo.getInt("object_type")==2)
					sql.append("?,?,?,?,?,");
				sql.append("?,?,?,?,?,?,?,?,?)");
				String sql_str="select ptp.point_id,ptp.score,ptp.rank,pp.pointname,ptp.item_id,ptp.seq from per_template_item pti,per_template_point ptp,per_point pp"
							+" where pti.item_id=ptp.item_id and ptp.point_id=pp.point_id and pti.template_id='"+(String)vo.getString("template_id")+"' order by ptp.seq";
				rowSet=dao.search(sql_str);
				ArrayList list=new ArrayList();

				while(rowSet.next())
				{
					ArrayList tempList=new ArrayList();
					IDGenerator idg=new IDGenerator(2,this.frameconn);
					String id=idg.getId("P04.P0400");
					tempList.add(new Integer(id));
					if(vo.getInt("object_type")==1||vo.getInt("object_type")==3||vo.getInt("object_type")==4)  //部门
						tempList.add(object_id);
					else if(vo.getInt("object_type")==2)//人员
					{				
						tempList.add(b0110);
						tempList.add(e0122);
						tempList.add(e01a1);
						tempList.add("USR");
						tempList.add(object_id);
						tempList.add(a0101);						
					}
					//tempList.add(String.valueOf(id));
					tempList.add(rowSet.getString("point_id"));
					tempList.add(rowSet.getString("pointname"));
					tempList.add(new Float(rowSet.getFloat("score")));
					tempList.add(new Float(rowSet.getFloat("rank")));
					tempList.add(new Integer(vo.getInt("plan_id")));
					tempList.add(new Integer(rowSet.getInt("item_id")));
					tempList.add(new Integer(2));
					tempList.add(new Integer(0));
					tempList.add(new Integer(rowSet.getInt("seq")));
					list.add(tempList);
				}
				dao.batchInsert(sql.toString(),list);
				
				//指标解释指标
				if(point_explain_item.trim().length()>0)
				{
					sql.setLength(0);
					sql.append("update p04 set "+point_explain_item+"=(select description from per_point where p04.p0401=per_point.point_id)");
					sql.append(" where ");
					if(vo.getInt("object_type")==1||vo.getInt("object_type")==3||vo.getInt("object_type")==4)  //部门
						sql.append(" b0110='"+object_id+"'");
					else if(vo.getInt("object_type")==2)//人员
						sql.append(" a0100='"+object_id+"'");
					sql.append(" and plan_id="+vo.getInt("plan_id"));
					dao.update(sql.toString());
				}
				
				//指标说明指标
				if(point_evaluate_item.trim().length()>0)
				{
					sql.setLength(0);
					sql.append("update p04 set "+point_evaluate_item+"=(select gd_principle from per_point where p04.p0401=per_point.point_id)");
					sql.append(" where ");
					if(vo.getInt("object_type")==1||vo.getInt("object_type")==3||vo.getInt("object_type")==4)  //部门
						sql.append(" b0110='"+object_id+"'");
					else if(vo.getInt("object_type")==2)//人员
						sql.append(" a0100='"+object_id+"'");
					sql.append(" and plan_id="+vo.getInt("plan_id"));
					dao.update(sql.toString());
				}
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public RecordVo getSelfVo(String object_id,String dbname)
	{
		RecordVo vo=new RecordVo(dbname.toLowerCase()+"a01");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setString("a0100",object_id);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	/** 取得计划模板下的所有项目 */
	public HashMap getItemid(String template_id) throws GeneralException
	{

		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			String sql = "select item_id,itemdesc from per_template_item where template_id='" + template_id + "' and child_id is null";
			RowSet rs = dao.search(sql);
			while (rs.next())
				map.put(rs.getString("itemdesc"), new Integer(rs.getInt("item_id")));
			
			if(rs!=null)
				rs.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	
	/**
	 * 判断考核对象是否已被评价且提交
	 * @param plan_id
	 * @param selKhObjs
	 * @return
	 */
	private ArrayList<String> checkPerObject(String plan_id, String selKhObjs) {
		ArrayList<String> list = new ArrayList<String>();
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct po.a0101 from per_mainbody pm,per_object po where pm.plan_id=? and status=2 and pm.object_id=po.object_id ");
			sql.append(" and pm.object_id in ( ");
				sql.append(" select a0100 from (");
				sql.append(selKhObjs);
				sql.append(") T");
			sql.append(")");
			
			ArrayList<String> sList = new ArrayList<String>();
			sList.add(plan_id);
			rs = dao.search(sql.toString(), sList);
			while (rs.next()){
				String objectname = rs.getString(1);
				list.add(objectname);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}
}
