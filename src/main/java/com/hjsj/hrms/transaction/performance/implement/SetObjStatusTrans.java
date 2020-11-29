package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:设置考核主体/对象 状态 ajax
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Apr 17, 2008
 * </p>
 * 
 * @author dengcan
 * @version 4.0
 */
public class SetObjStatusTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		RowSet rs = null;
		try
		{
			// 1:设置考核对象类别 2：设置考核主体是否必打分 3：设置考核主体的权限 4：恢复默认指标/项目权限 5：删除考核主体
			// 6:考核关系中设置考核对象/主体类别 8.为目标管理计划设置项目权限 9:更新顺序  11：测试是否存在参与分发到结束前的考核计划的考核对象
			// 12:考核实施移动记录 13:更新考核计划的顺序字段 14：检验所选考核对象是否属于考核计划 15.黏贴考核主体 16 考核等级移动记录
			// 17.绩效评估计算公式引入计划设置 18.考核实施设置动态项目权重/分值 19.考核实施中判断考核对象是否设置了考核对象类别
			// 20.制定目标卡 修改任务的内容 21.制定目标卡 保存目标卡指标的值 22.制定目标卡 修改目标卡指标的分值或者权重
			// 25.验证机读计划是否有机读数据 26.数据采集删除考核主体 28.自助 我的目标 引入上级目标卡 31:更新顺序 37：结果反馈 zhaoxg add
			String opt = (String) this.getFormHM().get("opt");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if ("1".equals(opt))
			{
				DbWizard dbWizard = new DbWizard(this.getFrameconn());				
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String typeid = (String) this.getFormHM().get("typeid");
				dao.update("update per_object set body_id=" + typeid + " where plan_id=" + plan_id + " and object_id='" + object_id + "'");
				//如果绩效结果表存在的话也在此更新绩效结果表中的考核对象类型
				String tablename = "per_result_"+plan_id;
				Table table = new Table(tablename);
				if(dbWizard.isExistTable(tablename, false))
    			{
					if (!dbWizard.isExistField(tablename, "Body_id",false))
					{
					    Field obj = new Field("Body_id");
					    obj.setDatatype(DataType.INT);
						obj.setKeyable(false);
					    table.addField(obj);
					    dbWizard.addColumns(table);// 更新列
					}
					
					StringBuffer buf = new StringBuffer();
				    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				    {
				    	buf.append("update per_result_"+plan_id +" set body_id=(select body_id from per_object where ");
				    	buf.append("per_result_"+plan_id +".object_id=per_object.object_id and per_object.plan_id="+plan_id+")");
				    }else  if (Sql_switcher.searchDbServer() == Constant.MSSQL)
				    {
				    	buf.append("update per_result_"+plan_id +" set body_id=per_object.body_id from per_object where per_result_"+plan_id +".object_id=per_object.object_id and per_object.plan_id="+plan_id);
				    }					
					dao.update(buf.toString());
    			}
				
			} else if ("2".equals(opt))
			{

				String fillctrl = (String) this.getFormHM().get("fillctrl");
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbody_id = (String) this.getFormHM().get("mainbody_id");
				dao.update("update per_mainbody set fillctrl=" + fillctrl + " where  mainbody_id='" + mainbody_id + "' and plan_id=" + plan_id
						+ " and object_id='" + object_id + "'");
			} else if ("3".equals(opt))//JinChunhai修改
			{
				String value = (String) this.getFormHM().get("value");
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbody_id = (String) this.getFormHM().get("mainbody_id");
				String pointid = (String) this.getFormHM().get("pointid");
				
				
				ArrayList item_ids = (ArrayList) this.getFormHM().get("item_ids");
				if(item_ids==null||item_ids.size()<=0)
				{
					dao.update("update per_pointpriv_" + plan_id + " set C_" + pointid + "=" + value + " where mainbody_id='" + mainbody_id + "' and object_id='"
							+ object_id + "'");
				}
				else
				{
					String values = (String) this.getFormHM().get("value");
					for (int i = 0; i < item_ids.size(); i++)
					{
						String temps = (String) item_ids.get(i);
						String[] temp = temps.split(":");
						String mainbody_ids = temp[0];
						String object_ids = temp[1];
						String pointids = temp[2];
						String plan_ids = temp[3];								
						dao.update("update per_pointpriv_" + plan_ids + " set C_" + pointids + "=" + values + " where mainbody_id='" + mainbody_ids + "' and object_id='"
								+ object_ids + "'");
					}
				}
				
			} else if ("4".equals(opt))
			{
				String power_type = (String) this.getFormHM().get("power_type");
				String objs = (String) this.getFormHM().get("objs");
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn());
				if(objs==null && object_id!=null)//指标/项目权限划分中恢复权限 采用回复当前一个考核对象权限的方式			
					bo.recoverPriv(plan_id, object_id,power_type);
				else if(objs!=null)//考核实施 考核主体菜单下 恢复默认指标/项目权限 采用批量恢复选中的所有考核对象的权限
				{
					String[] objsArray = objs.split("@");
					for (int i = 0; i < objsArray.length; i++)
					{
						if (objsArray[i].length() > 0)
							bo.recoverPriv(plan_id, objsArray[i],power_type);
					}
				}
				this.getFormHM().put("power_type", power_type);
			} else if ("5".equals(opt))
			{String power_type = (String) this.getFormHM().get("power_type");
				String templateid = (String) this.getFormHM().get("templateid");
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbodyids = (String) this.getFormHM().get("mainbodyids");
				String[] temps = mainbodyids.split("`");
				String[] ids = new String[temps.length];
				// StringBuffer whl=new StringBuffer("");
				for (int i = 0; i < temps.length; i++)
				{
					String[] temp = temps[i].split(":");
					String mainbodyid = temp[0];
					String body_id = temp[1];
					ids[i] = mainbodyid + ":" + object_id + ":" + body_id;
				}
				// whl.append(",'"+temps[i]+"'");
				//				
				// boolean isSelf=false;
				// this.frowset=dao.search("select body_id from per_plan_body
				// where body_id=5
				// and plan_id="+plan_id);
				// if(this.frowset.next())
				// {
				// isSelf=true;
				// }
				// if(isSelf)
				// {
				// String sql = "select * from per_mainbody where body_id<>5 and
				// plan_id="+plan_id+" and object_id='"+object_id+"' and
				// mainbody_id in
				// ("+whl.substring(1)+")";
				// this.frowset=dao.search(sql);
				// whl.setLength(0);
				// while(this.frowset.next())
				// whl.append(",'"+this.frowset.getString("mainbody_id")+"'");
				// }
				//				
				// if(whl.length()>0)
				// {
				// dao.update("delete from per_mainbody where
				// plan_id="+plan_id+" and
				// object_id='"+object_id+"' and mainbody_id in
				// ("+whl.substring(1)+")");
				// dao.update("delete from per_pointpriv_"+plan_id+" where
				// object_id='"+object_id+"' and mainbody_id in
				// ("+whl.substring(1)+")");
				// DbWizard dbWizard=new DbWizard(this.getFrameconn());
				// if(dbWizard.isExistTable("per_table_"+plan_id,false))
				// dao.update("delete from per_table_"+plan_id+" where
				// object_id='"+object_id+"'
				// and mainbody_id in ("+whl.substring(1)+")");
				// }
				// // 删除绩效面谈中记录
				// String delSql = "DELETE FROM per_interview WHERE plan_id =
				// "+plan_id+" AND
				// NOT EXISTS(SELECT * FROM per_mainbody B WHERE
				// B.plan_id="+plan_id;
				// delSql+=" AND per_interview.plan_id = B.plan_id AND
				// per_interview.mainbody_id
				// = B.mainbody_id AND per_interview.object_id = B.object_id)";
				// dao.update(delSql);

				// 和指定考核主体中的删除共用一个方法啦
				PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
				bo.delKhMainBody(ids, plan_id);
				bo.agreeSubjectNumber(plan_id, object_id, "per_pointpriv_"+plan_id);
				this.getFormHM().put("object_id", object_id);
				this.getFormHM().put("plan_id", plan_id);
				this.getFormHM().put("templateid", templateid);

			} else if ("6".equals(opt))
			{
				String object_id = (String) this.getFormHM().get("object_id");
				String typeid = (String) this.getFormHM().get("typeid");typeid=typeid.length()==0?"null":typeid;
				String type = (String) this.getFormHM().get("type");
				String mainbody_id = (String) this.getFormHM().get("mainbody_id");
				if ("obj".equalsIgnoreCase(type))
					dao.update("update per_object_std set obj_body_id=" + typeid + " where  object_id='" + object_id + "'");
				else if ("body".equalsIgnoreCase(type))
					dao.update("update per_mainbody_std set body_id=" + typeid + " where  object_id='" + object_id + "' and mainbody_id='" + mainbody_id
							+ "'");
			} else if ("7".equals(opt))
			{
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String kh_relations = (String) this.getFormHM().get("kh_relations");
				dao.update("update per_object set kh_relations=" + kh_relations + " where plan_id=" + plan_id + " and object_id='" + object_id + "'");

				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn(),this.userView);
				RecordVo vo = pb.getPerPlanVo(plan_id);
				String method = String.valueOf(vo.getInt("method"));
				String status = String.valueOf(vo.getInt("status"));
				if ("2".equals(method) && "5".equals(status))// 目标计划暂停状态修改考核关系要删除目标卡内容
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select * from per_object where plan_id=" + plan_id);
					buf.append(" and object_id='" + object_id + "'");
					buf.append(" and  NOT (sp_flag IS NULL) AND (sp_flag<>'03')");

					this.frowset = dao.search(buf.toString());
					if (this.frowset.next())// 考核对象处于流程中调整考核关系要进行初始化流程的操作
						pb.ClearTargetEvaluation(object_id, plan_id);
				}
			} else if ("32".equals(opt))//批量设置考核关系
			{
				String plan_id = (String) this.getFormHM().get("plan_id");
				String kh_relations = (String) this.getFormHM().get("kh_relations");
				dao.update("update per_object set kh_relations=" + kh_relations + " where plan_id=" + plan_id );

				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn(),this.userView);
				RecordVo vo = pb.getPerPlanVo(plan_id);
				String method = String.valueOf(vo.getInt("method"));
				String status = String.valueOf(vo.getInt("status"));
				if ("2".equals(method) && "5".equals(status))// 目标计划暂停状态修改考核关系要删除目标卡内容
				{
					StringBuffer buf = new StringBuffer();
					buf.append("select * from per_object where plan_id=" + plan_id);
					buf.append(" and  NOT (sp_flag IS NULL) AND (sp_flag<>'03')");

					this.frowset = dao.search(buf.toString());
					if (this.frowset.next())// 考核对象处于流程中调整考核关系要进行初始化流程的操作
						pb.ClearTargetEvaluation(null, plan_id);
				}
			} 
			else if ("8".equals(opt))//JinChunhai修改
			{
				String body_ids = (String) this.getFormHM().get("body_id");
				String object_ids = (String) this.getFormHM().get("object_id");
				String plan_ids = (String) this.getFormHM().get("plan_id");
				String item_idss = (String) this.getFormHM().get("item_id");
				String item_values = (String) this.getFormHM().get("item_value");
				
				ArrayList item_ids = (ArrayList) this.getFormHM().get("item_ids");
				if(item_ids==null||item_ids.size()<=0)
				{
					String sql = "update PER_ITEMPRIV_" + plan_ids + " set C_" + item_idss + "=" + item_values + " where object_id='" + object_ids + "'and  body_id="
						+ body_ids;
					dao.update(sql);
				}
				else
				{
					String item_value = (String) this.getFormHM().get("item_value");
					for (int i = 0; i < item_ids.size(); i++)
					{
						String temps = (String) item_ids.get(i);
						String[] temp = temps.split(":");
						String object_id = temp[0];
						String body_id = temp[1];
						String item_id = temp[2];
						String plan_id = temp[3];								
						String sql = "update PER_ITEMPRIV_" + plan_id + " set C_" + item_id + "=" + item_value + " where object_id='" + object_id + "'and  body_id="
								+ body_id;
						dao.update(sql);
					}
				}
			} else if ("9".equals(opt))
			{
				String seq = (String) this.getFormHM().get("seq");
				seq = (seq == null || (seq != null && seq.trim().length() == 0)) ? "null" : seq;
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbody_id = (String) this.getFormHM().get("mainbody_id");
				dao.update("update per_mainbody set seq=" + seq + " where  mainbody_id='" + mainbody_id + "' and plan_id=" + plan_id + " and object_id='"
						+ object_id + "'");
			} else if ("10".equals(opt))
			{// 判断所选考核主体已参与目标卡的审批流程,如删除,则考核对象的目标卡状态将被初始化！您确定要删除所选考核主体吗？
				String templateid = (String) this.getFormHM().get("templateid");
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbodyids = (String) this.getFormHM().get("mainbodyids");
				String[] temps = mainbodyids.split("`");
				String[] ids = new String[temps.length];
				for (int i = 0; i < temps.length; i++)
				{
					String[] temp = temps[i].split(":");
					String mainbodyid = temp[0];
					String body_id = temp[1];
					ids[i] = mainbodyid + ":" + object_id + ":" + body_id;
				}
				String isHave = "0";
				for (int i = 0; i < ids.length; i++)
				{
					String id = ids[i];// mainbody_id:objectID:body_id
					String[] temp = id.split(":");
					String mainBody = temp[0];

					StringBuffer delBuf = new StringBuffer();
					delBuf.append("select * from per_object where plan_id=" + plan_id);
					delBuf.append(" and object_id='" + object_id + "'");
					delBuf.append(" and currappuser='" + mainBody + "'");
					delBuf.append(" and  NOT (sp_flag IS NULL) AND (sp_flag<>'03')");
					this.frowset = dao.search(delBuf.toString());
					if (this.frowset.next())
						isHave = "1";
				}

				this.getFormHM().put("isHave", isHave);
				this.getFormHM().put("mainbodyids", mainbodyids);
				this.getFormHM().put("object_id", object_id);
				this.getFormHM().put("plan_id", plan_id);
				this.getFormHM().put("templateid", templateid);
			}else if("11".equals(opt))
			{
				String objs = (String)this.getFormHM().get("objs");
				PerRelationBo bo = new PerRelationBo(this.frameconn);	
				HashMap joinedObjs = bo.getJoinedObjs();
				StringBuffer isExistjoinedObj = new StringBuffer();
				String[] temps = objs.split("@",-1);
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].length()==0)
						continue;
					else
					{
						if(joinedObjs.get(temps[i])!=null)
						{
						    if(i>4){
						        isExistjoinedObj.append("...");
						        break;
                            }
						    if(i>1){
                                isExistjoinedObj.append("、");
                            }
                            isExistjoinedObj.append(joinedObjs.get(temps[i]));
						}
					}
				}
				this.getFormHM().put("isExistjoinedObj", isExistjoinedObj.toString());
			}
			else if("12".equals(opt))
			{
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String move = (String) this.getFormHM().get("move");
				String code = (String) this.getFormHM().get("code");
				String codeset = (String) this.getFormHM().get("codeset");
				
				if(!"-1".equals(code))
				{
					if(AdminCode.getCode("UM",code)!=null)
						codeset="UM";
					else if(AdminCode.getCode("UN",code)!=null)
						codeset="UN";
				}
				
				
				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
				pb.moveRecord(plan_id,object_id,move,code,codeset);
			}else if("13".equals(opt))
			{
				String a0000 = (String) this.getFormHM().get("a0000");
				String plan_id = (String) this.getFormHM().get("planId");
				String sql = "update per_plan set a0000="+a0000+" where plan_id="+plan_id;
				dao.update(sql);
			}else if("14".equals(opt))
			{
				String object_copy=(String) this.getFormHM().get("object_copy");
//				String object_past=(String) this.getFormHM().get("object_past");
//				String object_type=(String) this.getFormHM().get("object_type");
				String plan_id=(String) this.getFormHM().get("plan_id");
				String info="";
				
				StringBuffer buf = new StringBuffer();
//				if(object_type.equals("2"))
//				{
//					buf.append("select a0101 from "+object_past.substring(0,3)+"A01 where ");					
//					buf.append(" a0100 not in (select object_id from per_object where plan_id=");
//					buf.append(plan_id);
//					buf.append(") and a0100 in (");
//				}else
//				{
//					buf.append("select codeitemdesc from organization where codeitemid not in ( ");					
//					buf.append("select object_id from per_object where plan_id=");
//					buf.append(plan_id);
//					buf.append(") and codeitemid in (");
//				}
//			
//				String[] object_array = object_past.split(",");
//				for(int i=0;i<object_array.length;i++)
//				{					
//					if(object_array[i].length()>0)
//						buf.append("'"+(object_type.equals("2")?object_array[i].substring(3):object_array[i].substring(2))+"',");
//				}
//				buf.setLength(buf.length()-1);
//				buf.append(")");
				buf.append("select count(*) from per_mainbody where plan_id="+plan_id+" and object_id='"+object_copy+"'");
				
				this.frowset = dao.search(buf.toString());
//				while(this.frowset.next())
//					info+=this.frowset.getString(1)+",";
				if(this.frowset.next())
					if(this.frowset.getInt(1)==0)
						info="所选考核对象没有设置考核主体！";
//				if(info.length()>0)
//					info=info.substring(0,info.length()-1)+" 不是该计划的考核对象，请重新选择！";
				this.getFormHM().put("object_copy", object_copy);
//				this.getFormHM().put("object_past", object_past);
				this.getFormHM().put("plan_id", plan_id);
//				this.getFormHM().put("object_type", object_type);
				this.getFormHM().put("info", info);
			}else if("15".equals(opt))
			{
				String object_copy=(String) this.getFormHM().get("object_copy");
				String object_past=(String) this.getFormHM().get("object_past");
				object_past=PubFunc.keyWord_reback(object_past);//zzk 2013/11/29
//				String object_type=(String) this.getFormHM().get("object_type");
				String plan_id=(String) this.getFormHM().get("plan_id");
				
				HashMap mainBodyCopyed = new HashMap();
				mainBodyCopyed.put("objectID", object_copy);
				mainBodyCopyed.put("planID", plan_id);
				String[] object_array = object_past.split(",");
				
				PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
				for(int i=0;i<object_array.length;i++)
				{					
					if(object_array[i].length()>0)
					{
//						String objectPast = object_type.equals("2")?object_array[i].substring(3):object_array[i].substring(2);
						bo.pasteKhMainBody(mainBodyCopyed,plan_id,object_array[i].replaceAll("'", ""));
					}					
				}	
				this.getFormHM().put("flag", "1");
			}
			else if("16".equals(opt))
			{
				String num1 = (String) this.getFormHM().get("num");
				String move = (String) this.getFormHM().get("move");
				
				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
				pb.moveRecord2(num1, move);
			}
			else if("17".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				LoadXml loadxml = new LoadXml(this.frameconn, planid, "");				
				
				//总分计算公式
				ArrayList exprrelatelist = new ArrayList();
				
				PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);			
				exprrelatelist = pb.getExprrelatelist(planid,"0",loadxml,"yinru");
				
				this.getFormHM().put("list", exprrelatelist);								
				
			}else if("18".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String item_id = (String) this.getFormHM().get("item_id");
				String objTypeId = (String) this.getFormHM().get("objTypeId");
				String theValue =  (String) this.getFormHM().get("theValue");
				if(theValue==null||theValue.trim().length()==0)
					theValue="0";
				String sql = "update per_dyna_item set Dyna_value=? where plan_id="+planid+" and body_id="+objTypeId+" and Item_id="+item_id;
				try
				{
					ArrayList list = new ArrayList();
					list.add(new BigDecimal(theValue));
					dao.update(sql,list);
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}		
			}else if("19".equals(opt))
			{	
				//是否可以设置动态项目权重(分值)或者是否可以设置主体评分范围  只有当设置的考核对象设置了考核对象类别才可以
				String planid = (String) this.getFormHM().get("planid");
				String planStatus = (String) this.getFormHM().get("planStatus");
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn(),this.userView,planid);	
				boolean canSetDynaItem = pb.testCanSetDynaItem();
				if(canSetDynaItem)
				{
					this.getFormHM().put("canSetDynaItem","true");
					this.getFormHM().put("planid",planid);
					this.getFormHM().put("planStatus",planStatus);
				}else
				{
					this.getFormHM().put("canSetDynaItem","false");
					this.getFormHM().put("planid",planid);
					this.getFormHM().put("planStatus",planStatus);
				}				
			}else if("20".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String p0401_value = (String) this.getFormHM().get("p0401_value");
				String objCode = (String) this.getFormHM().get("objCode");
				String theValue =  (String) this.getFormHM().get("theValue");
				theValue = SafeCode.decode(theValue);
				
				String sql = "update p04 set p0407=? where plan_id="+planid+" and upper(p0401)='"+p0401_value.toUpperCase()+"' ";
				String tempCode = objCode.substring(0, 1);
				if("p".equalsIgnoreCase(tempCode))
					sql+=" and a0100='"+objCode.substring(1)+"'";
				else if("u".equalsIgnoreCase(tempCode))
					sql+=" and b0110='"+objCode.substring(2)+"'";
				try
				{
					ArrayList list = new ArrayList();
					list.add(theValue);
					dao.update(sql,list);
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}		
			}else if("21".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String p0401_value = (String) this.getFormHM().get("p0401_value");
				String objCode = (String) this.getFormHM().get("objCode");
				String theValue =  (String) this.getFormHM().get("theValue");
				theValue = SafeCode.decode(theValue);
				String targetPointCol =  (String) this.getFormHM().get("targetPointCol");
				targetPointCol = targetPointCol.toLowerCase();
				String sql = "update p04 set "+targetPointCol+"=? where plan_id="+planid+" and upper(p0401)='"+p0401_value.toUpperCase()+"' ";
				String tempCode = objCode.substring(0, 1);
				if("p".equalsIgnoreCase(tempCode))
					sql+=" and a0100='"+objCode.substring(1)+"'";
				else if("u".equalsIgnoreCase(tempCode))
					sql+=" and b0110='"+objCode.substring(2)+"'";
				try
				{
					ArrayList list = new ArrayList();
					FieldItem fieldItem = DataDictionary.getFieldItem(targetPointCol);
					String itemtype = fieldItem.getItemtype();
					int decwidth = fieldItem.getDecimalwidth();
					if ("N".equals(itemtype))
						theValue = PubFunc.round(theValue, decwidth);
					
					if ("N".equals(itemtype)&&decwidth==0)
						list.add(new Integer(theValue));
					else if ("N".equals(itemtype)&&decwidth>0)
						list.add(new BigDecimal(theValue));
					else if ("D".equals(itemtype))
					{
						if(theValue.trim().length()>0)
							list.add(java.sql.Date.valueOf(theValue));
						else
							list.add(null);
					}						
					else  if ("A".equals(itemtype))
						list.add(theValue);
					else  if ("M".equals(itemtype))
					{
						theValue = SafeCode.decode(theValue);
						list.add(theValue);
					}
						
					
					dao.update(sql,list);
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}		
			}else if("22".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String p0401_value = (String) this.getFormHM().get("p0401_value");
				String objCode = (String) this.getFormHM().get("objCode");
				String theValue =  (String) this.getFormHM().get("theValue");
				String type =  (String) this.getFormHM().get("type");
				String col="p0413";
				if("rank".equalsIgnoreCase(type))
					col="p0415";
				else if("score".equalsIgnoreCase(type))
					col="p0413";
				String sql = "update p04 set "+col+"=? where plan_id="+planid+" and upper(p0401)='"+p0401_value.toUpperCase()+"' ";
				String tempCode = objCode.substring(0, 1);
				if("p".equalsIgnoreCase(tempCode))
					sql+=" and a0100='"+objCode.substring(1)+"'";
				else if("u".equalsIgnoreCase(tempCode))
					sql+=" and b0110='"+objCode.substring(2)+"'";
				try
				{
					ArrayList list = new ArrayList();
					list.add(new BigDecimal(theValue.trim().length()==0?"0":theValue));
					dao.update(sql,list);
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}		
			}else if("23".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String p0401_value = (String) this.getFormHM().get("p0401_value");
				String objCode = (String) this.getFormHM().get("objCode");
				String item_id = (String) this.getFormHM().get("item_id");

				String sql = "delete from p04  where plan_id="+planid+" and upper(p0401)='"+p0401_value.toUpperCase()+"'";
				if(!"-1".equals(item_id)){
					sql += " and item_id="+item_id;
				}
				String tempCode = objCode.substring(0, 1);
				if("p".equalsIgnoreCase(tempCode))
					sql+=" and a0100='"+objCode.substring(1)+"'";
				else if("u".equalsIgnoreCase(tempCode))
					sql+=" and b0110='"+objCode.substring(2)+"'";
				try
				{
					dao.delete(sql, new ArrayList());
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}		
			}else if("24".equals(opt))
			{
				String planid_past = (String) this.getFormHM().get("planid_past");
				String planid_copy = (String) this.getFormHM().get("planid_copy");
				String objCode = (String) this.getFormHM().get("objCode");
				ArrayList objList = (ArrayList) this.getFormHM().get("objList");
				ArrayList copyObjList = new ArrayList();//被复制的考核对象
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
				String privWhl = pb.getPrivWhere(userView);				
				
				StringBuffer buf = new StringBuffer();
				buf.append("select object_id from per_object  where plan_id="+planid_past+" "+privWhl);
				buf.append("and (");
				for(int i=0;i<objList.size();i++)
				{
					String object_id = (String)objList.get(i);
					buf.append("object_id='"+object_id+"' or ");
				}
				buf.setLength(buf.length()-3);
				buf.append(")");
				this.frowset=dao.search(buf.toString());
				while(this.frowset.next())				
					copyObjList.add(this.frowset.getString("object_id"));
				  
				KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid_past,"targetCard");	
				bo.importLastTargetCard(planid_copy, copyObjList);	
				this.getFormHM().put("objCode", "p"+objList.get(0));
			}else if("25".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String sql = "select gather_type from per_plan  where plan_id="+planid;
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					if(this.frowset.getInt(1)==1)//机读
					{
						sql = "select count(*) from per_mainbody  where plan_id="+planid;
						this.frowset=dao.search(sql);
						if(this.frowset.next())
						{
							if(this.frowset.getInt(1)==0)
							{
								this.getFormHM().put("flag", "0");
								return;
							}
						}
					}
				}
				LoadXml parameter_content = new LoadXml(this.getFrameconn(), planid);
				Hashtable params = parameter_content.getDegreeWhole();
				String handEval = (String) params.get("HandEval");				
				if (handEval != null && "TRUE".equalsIgnoreCase(handEval))// 启动录入结果
					this.getFormHM().put("handScore", "1");	
				else
					this.getFormHM().put("handScore", "0");	
				
				this.getFormHM().put("flag", "1");	
			}else if("26".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String object_id = PubFunc.decrypt((String) this.getFormHM().get("object_id"));
				String mainbody_id = PubFunc.decrypt((String) this.getFormHM().get("mainbody_id"));
				String delFlag = (String) this.getFormHM().get("delFlag");
				DbWizard dbWizard = new DbWizard(this.frameconn);
				String sql ="";
				if("1".equals(delFlag))//删除考核主体
				{
					sql="delete from per_mainbody where  plan_id="+planid+" and mainbody_id='"+mainbody_id+"'";
					dao.delete(sql, new ArrayList());
					
					if (dbWizard.isExistTable("per_table_" + planid, false))//相应的主体指标权限没有
					{
						sql="delete from per_table_" +planid+" where  mainbody_id='"+mainbody_id+"'";
						dao.delete(sql, new ArrayList());
					}					
				}else if("2".equals(delFlag))//删除该对象数据
				{
					sql="delete from per_mainbody where  plan_id="+planid+" and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'";
					dao.delete(sql, new ArrayList());
					
					if (dbWizard.isExistTable("per_table_" + planid, false))//相应的主体指标权限没有
					{
						sql="delete from per_table_" +planid+" where  mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'";
						dao.delete(sql, new ArrayList());
					}	
				}
			}else if("27".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String object_id = PubFunc.decrypt((String) this.getFormHM().get("object_id"));
				String body_id = (String) this.getFormHM().get("body_id");				
				IDGenerator idg = new IDGenerator(2, this.frameconn);
				String id = new Integer(idg.getId("per_mainbody.id")).toString();
				String sql="insert into per_mainbody(id,object_id,mainbody_id,a0101,body_id,plan_id,status)";
				sql+="values ("+id+",'"+object_id+"','"+id+"','',"+body_id+","+planid+",0)";
				dao.insert(sql,  new ArrayList());
				
			}else if("28".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String pastObjId = (String) this.getFormHM().get("pastObjId");
				String copyObjId = (String) this.getFormHM().get("copyObjId");
				KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),planid);
				bo.delOldTarget(planid, pastObjId);
			
				String[] copyobjIds=copyObjId.replaceAll("／", "/").split("/");
				bo.pastObjTarget(copyobjIds[1],planid,pastObjId, copyobjIds[0]);	
			
			}else if("29".equals(opt))
			{
				String planid = (String) this.getFormHM().get("planid");
				String theValue = (String) this.getFormHM().get("theValue");
				String object_id = (String) this.getFormHM().get("object_id");
				theValue = SafeCode.decode(theValue);				
				String sql="update per_result_" +planid+" set director=? where object_id='"+object_id+"'";
				try
				{
					ArrayList list = new ArrayList();
					list.add(theValue);
					dao.update(sql,list);
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}	
			}else if("30".equals(opt))
			{
				String planid = PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("plan_id")));
				ArrayList mainbodys = (ArrayList) this.getFormHM().get("mainbodys");
				String object_id = PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("object_id")));
				
				String sql="update per_mainbody  set status=1 where object_id='"+object_id+"' and plan_id="+planid+" and mainbody_id=?";
				try
				{
					ArrayList list = new ArrayList();
					for(int i=0;i<mainbodys.size();i++)
					{
						String mainbody_id = (String)mainbodys.get(i);
						ArrayList list1 = new ArrayList();
						list1.add(mainbody_id);
						list.add(list1);
					}
					
					dao.batchUpdate(sql,list);
					this.getFormHM().put("flag", "1");
				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}	
			}else if ("31".equals(opt))
			{
				String sp_seq = (String) this.getFormHM().get("sp_seq");
				sp_seq = (sp_seq == null || (sp_seq != null && sp_seq.trim().length() == 0)) ? "null" : sp_seq;
				String object_id = (String) this.getFormHM().get("object_id");
				String plan_id = (String) this.getFormHM().get("plan_id");
				String mainbody_id = (String) this.getFormHM().get("mainbody_id");
				dao.update("update per_mainbody set sp_seq=" + sp_seq + " where  mainbody_id='" + mainbody_id + "' and plan_id=" + plan_id + " and object_id='"
						+ object_id + "'");
			}else if("33".equals(opt)){//zhaoxg add 2014-3-11 批量设置必打分
				String fillctrl = (String) this.getFormHM().get("fillctrl");
				String str = (String) this.getFormHM().get("str");
				String[] _str=str.split("\\|");
				for(int i=0;i<_str.length;i++){
					if (_str[i] != null && _str[i].length() > 0) {
						String[] temp=_str[i].split(":");
						String object_id = temp[1];
						String plan_id = temp[2];
						String mainbody_id = temp[0];
						dao.update("update per_mainbody set fillctrl=" + fillctrl + " where  mainbody_id='" + mainbody_id + "' and plan_id=" + plan_id
								+ " and object_id='" + object_id + "'");							
					}
				}
			}else if("34".equals(opt)){//zhaoxg add 2014-3-11 批量设置打分权限 目标管理
				String item_values = (String) this.getFormHM().get("item_value");
				String str = (String) this.getFormHM().get("str");
				String[] _str=str.split("\\|");
				for(int i=0;i<_str.length;i++){
					String[] temp=_str[i].split(":");
					for(int j=0;j<temp.length;j++){
						String object_ids = temp[0];
						String body_ids = temp[1];
						String plan_ids = temp[3];
						String item_idss = temp[2];
						String sql = "update PER_ITEMPRIV_" + plan_ids + " set C_" + item_idss + "=" + item_values + " where object_id='" + object_ids + "'and  body_id="
						+ body_ids;
						sql += "AND body_id NOT IN (SELECT body_id FROM per_plan_body pd WHERE pd.plan_id=" + plan_ids + " AND opt=1)";
						dao.update(sql);
					}
				}
			}else if("35".equals(opt)){//zhaoxg add 2014-3-11 批量设置打分权限 360
				String value = (String) this.getFormHM().get("value");
				String str = (String) this.getFormHM().get("str");
				String[] _str=str.split("\\|");
				for(int i=0;i<_str.length;i++){
					String[] temp=_str[i].split(":");
					for(int j=0;j<temp.length;j++){
						String mainbody_id = temp[0];
						String object_id = temp[1];
						String plan_id = temp[3];
						String pointid = temp[2];
						dao.update("update per_pointpriv_" + plan_id + " set C_" + pointid + "=" + value + " where mainbody_id='" + mainbody_id + "' and object_id='"
								+ object_id + "'");
					}
				}
			}
			else if("36".equals(opt))
			{
				String plan_id = (String) this.getFormHM().get("plan_id");
				String seq = (String) this.getFormHM().get("seq");
				String move = (String) this.getFormHM().get("move");
				
				ExamPlanBo bo = new ExamPlanBo(plan_id,this.getFrameconn());
				String tempTable = "t#des_review";
				// 移动记录
				bo.moveRecord(tempTable, seq, move);				
				// 保存描述性评议项设置
			    bo.saveHighSet(tempTable);
			}else if("37".equals(opt)){
				String plan_id = (String) this.getFormHM().get("planid");
				String sql = "update per_plan set feedback=1 where plan_id="+plan_id;
				dao.update(sql);
				this.getFormHM().put("flag", "1");
			}else if("38".equals(opt)){//haosl 20170215 add 查找选人控件中推荐显示的人员（ 赋值考核主体）
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
				String plan_id = (String) this.getFormHM().get("plan_id");
				String object_copy=(String) this.getFormHM().get("object_copy");
				String objectType=(String) this.getFormHM().get("objectType");
				String privWhl = pb.getPrivWhere(userView);				
				StringBuffer buf = new StringBuffer();
				buf.append("select object_id from per_object where object_id not in('"+object_copy+"') and plan_id="+plan_id+" "+privWhl);
				rs = dao.search(buf.toString());
				List<String> list = new ArrayList<String>();
				while(rs.next()){
					String object_id = rs.getString("object_id");
					if(StringUtils.isBlank(object_id))
						continue;
					if("1".equals(objectType)) {
						String unit = AdminCode.getCodeName("UN", object_id);
						String dept = AdminCode.getCodeName("UM", object_id);
						String post =  AdminCode.getCodeName("@K", object_id);
						if(StringUtils.isNotBlank(unit))
							list.add(PubFunc.encrypt("UN"+object_id));
						else if(StringUtils.isNotBlank(dept))
							list.add(PubFunc.encrypt("UM"+object_id));
						else if(StringUtils.isNotBlank(post))
							list.add(PubFunc.encrypt("@K"+object_id));
					}else if("2".equals(objectType)) {
						list.add(PubFunc.encrypt("Usr"+object_id));
					}else if("3".equals(objectType)) {
						list.add(PubFunc.encrypt("UN"+object_id));
					}else if("4".equals(objectType)) {
						list.add(PubFunc.encrypt("UM"+object_id));
					}
						
				}
				this.getFormHM().put("objectids", list);
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}

}
