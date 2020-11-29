package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitGzSpCollectTreeTrans 
 * 类描述： 薪资汇总审批异步加载树节点
 * 创建人：zhaoxg
 * 创建时间：Dec 11, 2015 2:18:33 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 11, 2015 2:18:33 PM
 * 修改备注： 
 * @version
 */
public class InitGzSpCollectTreeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		RowSet rset=null;
		try{					
			GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
			String note = (String) this.getFormHM().get("node");
			String date_count = (String) this.getFormHM().get("date_count");
			String bosdate = "";
			String count = "";
			if(date_count!=null&&date_count.length()>0){
				bosdate = date_count.split("#")[0];
				bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
				count = date_count.split("#")[1];
				count = PubFunc.decrypt(SafeCode.decode(count));
			}
			String cound = (String) this.getFormHM().get("cound");
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String collectPoint = (String) this.getFormHM().get("collectPoint");//汇总指标
			SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			String selectcollectPoint = (String) this.getFormHM().get("selectcollectPoint");//汇总指标中已选指标
			String codeset = (String) this.getFormHM().get("codeset");
 			String record = (String) this.getFormHM().get("record");
			record = record==null?"":record;

			String b0110 = "b0110";
			String e0122 = "e0122";
			if("UNUM".equalsIgnoreCase(collectPoint)){//单位+部门
				String orgid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(orgid.length()>0){
					b0110 = orgid;
				}
				if(deptid.length()>0){
					e0122 = deptid;
				}
				collectPoint = spbo.getCollectPointSql(b0110, e0122,"");
			}else //可能出现指标为空和null的
				collectPoint = "nullif("+collectPoint+",'')";
			
			if(bosdate==null||bosdate.length()==0){
				HashMap dateMap = spbo.getBosdateAndCount(Integer.parseInt(salaryid));
				bosdate = (String) dateMap.get("bosdate");
				bosdate = bosdate==null?"":bosdate;
				count = (String) dateMap.get("count");
				count = count==null?"":count;
			}
			
			StringBuffer filterWhl=new StringBuffer();
			
			if(StringUtils.isNotBlank(count))
				filterWhl.append(" and A00Z3 = "+count);
			
			if(StringUtils.isNotBlank(bosdate))
				filterWhl.append(" and A00Z2 = "+Sql_switcher.dateValue(bosdate));
			
			ContentDAO dao=new ContentDAO(this.frameconn);
			ArrayList list = new ArrayList();	
			//由于按照部门为汇总指标的时候，部门取得是上级指标id，可能出现topList中存在不一样长度的情况，导致查询出错 sunjian 2017-7-14
			ArrayList lengthList = new ArrayList();
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo gzbo = bo.getSalaryTemplateBo();

			if("root".equals(note)){
				ArrayList topList =new ArrayList();
				if(!"".equals(bosdate)&&!"".equals(count))
					topList = spbo.getTopCode(salaryid, codeset, collectPoint, bosdate, count);
				if(topList.size()>0){
					this.userView.getHm().put("collectPoint", collectPoint);//明细表那面定位的时候用到
					StringBuffer buf = new StringBuffer();
					int length = 0;
					for(int i=0;i<topList.size();i++){
						if(topList.get(i)==null){
							buf.append(" or  "+collectPoint+" is null");
						}else{
							if(length == 0)
								length = topList.get(i).toString().length();
							else if(length != 0 && length != topList.get(i).toString().length()) {//判断是否由于topList的长度不同，如果不同，则添加在list中，下面sql根据length进行判断，所以这里根据不同length进行存储
								lengthList.add(length + ";" + buf);
								length = topList.get(i).toString().length();
								buf.setLength(0);
							}
							
							//length = topList.get(i).toString().length();
							//if(length == 0)
							buf.append(" or  "+collectPoint+" like '");
							buf.append(topList.get(i));
							buf.append("%'");
						}
						if(i + 1 == topList.size())
							lengthList.add(length + ";" + buf);
					}
					
					StringBuffer _str = new StringBuffer();
					StringBuffer str=new StringBuffer();
					//这里的排序要和组织机构机构编码排序一样，组织机构是按A0000排序，这里多加了个查询从innerjoin ORGANIZATION
					str.append("select res.collectPoint,res.num");
					String[] value = selectcollectPoint.split(",");						
					for(int i=0;i<value.length;i++){
						if(!"".equals(value[i])){
							_str.append(",sum("+Sql_switcher.isnull(value[i].toUpperCase(), "0")+"");
							_str.append(") "+value[i].toUpperCase()+"");
							//取出要查询的所有别名
							str.append(",res."+value[i].toUpperCase());
						}
					}	
					if(buf.length()>0){
						StringBuffer sb = new StringBuffer();
						String privWhlStr = gzbo.getWhlByUnits("salaryhistory", true);
						HashMap spflagMap = spbo.getSpFlag(collectPoint, salaryid, bosdate, count, topList, privWhlStr,cound);
						for(int k = 0; k < lengthList.size(); k++) {
							String[] arrLength = lengthList.get(k).toString().split(";");
							sb.setLength(0);
							sb.append(str);
							sb.append(" from (");
							sb.append("select "+Sql_switcher.substr(collectPoint ,"1", arrLength[0]+"")+" collectPoint,count(*) num "+_str+" from salaryhistory where ");// and (sp_flag='06' or sp_flag='03' or sp_flag='02')
							sb.append(" ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
							sb.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=? and  ("+arrLength[1].substring(3)+") ");
							if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
								sb.append(" and UserFlag='"+cound+"'");
								filterWhl.append(" and upper(salaryhistory.userFlag)= '"+cound.toUpperCase()+"'" );
							}
							sb.append(" group by "+Sql_switcher.substr(collectPoint ,"1", arrLength[0]+"")+" ) res  ");
							//str.append(" order by "+Sql_switcher.substr(collectPoint ,"1", length+"")+" ");
							//拼接sql
							ArrayList parmList = new ArrayList();
							parmList.add(salaryid);
							parmList.add(count);
							if("".equalsIgnoreCase(codeset)|| "un".equalsIgnoreCase(codeset)|| "um".equalsIgnoreCase(codeset))
								sb.append("left JOIN ORGANIZATION org ON res.COLLECTPOINT = org.CODEITEMID ORDER BY org.A0000 ASC");
							else{
								sb.append("left JOIN codeitem org ON org.codesetid=? and res.COLLECTPOINT = org.CODEITEMID  ORDER BY org.A0000,res.COLLECTPOINT ASC");
								parmList.add(codeset);
							}
							RowSet rs = dao.search(sb.toString(),parmList);						
							int j=0;
							while(rs.next()){
								HashMap map = new HashMap();
								String id = rs.getString("collectPoint");
								String text = "";
	//							if(codeset==null||codeset.length()==0){
	//								text = AdminCode.getCodeName("UN", id).length()==0?AdminCode.getCodeName("UM", id):AdminCode.getCodeName("UN", id);
	//							}else{
	//								text = id!=null?AdminCode.getCodeName(codeset, id):id;
	//							}
								text = this.getUpdesc(id, codeset);
								map.put("id", id==null||id.length()==0?"null":id);
								map.put("text", text);
	//							map.put("sp_flag", spflagMap.get(id));			
								if(id==null||id.length()==0)
									id="null";
								map.put("desc", spflagMap.get(id+"num"));
								map.put("color", spflagMap.get(id+"color"));//图片颜色
								map.put("num", rs.getString("num"));				
								map.put("iconCls", "no-icon");
								for(int i=0;i<value.length;i++){
									if(!"".equals(value[i])){
										FieldItem item=DataDictionary.getFieldItem(value[i]);
										map.put(value[i].toUpperCase(), PubFunc.round(rs.getString(value[i].toUpperCase()), item.getDecimalwidth()));
									}
								}
								if(topList.size()==1&&j==0&&("1".equals((String)this.getFormHM().get("inCount"))||"3".equals((String)this.getFormHM().get("inCount")))){
									map.put("expanded", "true");//有一个顶级节点则展开第一个 否则全都不展开
								}
								j++;
								list.add(map);
							}
						}
					}

					str.setLength(0);
					str.append("select count(a0100) num "+_str+" from salaryhistory where ");// and (sp_flag='06' or sp_flag='03' or sp_flag='02')
					str.append(" ((((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
					str.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=?");
					if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
						str.append(" and UserFlag='"+cound+"'");
					}
					ArrayList parmList = new ArrayList();
					parmList.add(salaryid);
					parmList.add(count);
					RowSet rs1 = dao.search(str.toString(),parmList);
					HashMap sumMap = new HashMap();
					sumMap.put("id", "sum");
					sumMap.put("text", ""+ResourceFactory.getProperty("planar.stat.total")+"");//合计
					sumMap.put("color", "");//图片颜色
					sumMap.put("leaf", "true");
					sumMap.put("iconCls", "no-icon");
					while(rs1.next()){
						sumMap.put("num", rs1.getString("num"));
						for(int i=0;i<value.length;i++){
							if(!"".equals(value[i])){
								FieldItem item=DataDictionary.getFieldItem(value[i]);
								sumMap.put(value[i].toUpperCase(), PubFunc.round(rs1.getString(value[i].toUpperCase()), item.getDecimalwidth()));
							}
						}
					}
					list.add(sumMap);
				}
			}else{
				String[] value = selectcollectPoint.split(",");
				StringBuffer _str = new StringBuffer();
				for(int i=0;i<value.length;i++){
					if(!"".equals(value[i])){
						_str.append(",sum("+Sql_switcher.isnull(value[i].toUpperCase(), "0")+"");
						_str.append(") "+value[i].toUpperCase()+"");
					}
				}
				String tableName = "codeitem";
				if("UN".equalsIgnoreCase(codeset)||"UM".equalsIgnoreCase(codeset)||"@K".equalsIgnoreCase(codeset)||(codeset==null||codeset.length()==0)){
					tableName = "organization";
				}
				StringBuffer sql = new StringBuffer();
				sql.append("select codeitemid,codesetid from "+tableName+" where parentid<>codeitemid and parentid = '"+note+"'");
				if(codeset!=null&&codeset.length()>0){
					if("UM".equalsIgnoreCase(codeset))
						sql.append(" and (codesetid = 'UN' or codesetid = 'UM')");
					else
						sql.append(" and codesetid = '"+codeset.toUpperCase()+"'");
				}else{				
					sql.append(" and (codesetid = 'UN' or codesetid = 'UM')");
				}
				rset = dao.search(sql.toString());
				ArrayList collectList = new ArrayList();
				StringBuffer buf = new StringBuffer();
				int length = 0;
				while(rset.next()){
					collectList.add(rset.getString("codeitemid"));
					length = rset.getString("codeitemid").length();
					buf.append(" or  "+collectPoint+" like '");
					buf.append(rset.getString("codeitemid"));
					buf.append("%'");
				}
				
				if(buf.length()>0){//下级没有了则不查询了
					StringBuffer str=new StringBuffer();
					String privWhlStr = gzbo.getWhlByUnits("salaryhistory", true);
					HashMap spflagMap = spbo.getSpFlag(collectPoint, salaryid, bosdate, count, collectList, privWhlStr,cound);
					str.append("select "+Sql_switcher.substr(collectPoint ,"1", length+"")+" collectPoint,count(*) num "+_str+" from salaryhistory where ");
					str.append(" ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"')");
					str.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=?");
					str.append(" and ("+buf.substring(3)+")");
					if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
						str.append(" and UserFlag='"+cound+"'");
						filterWhl.append(" and upper(salaryhistory.userFlag)= '"+cound.toUpperCase()+"' ");
					}
					str.append(" group by "+Sql_switcher.substr(collectPoint ,"1", length+"")+" ");
					str.append(" order by "+Sql_switcher.substr(collectPoint ,"1", length+"")+" ");
					ArrayList parmList = new ArrayList();
					parmList.add(salaryid);
					parmList.add(count);
					RowSet rs = dao.search(str.toString(),parmList);
					int j = 0;
					while(rs.next()){
						HashMap map = new HashMap();
						String id = rs.getString("collectPoint");
						String text = "";
						if(codeset==null||codeset.length()==0){
							text = AdminCode.getCodeName("UN", id).length()==0?AdminCode.getCodeName("UM", id):AdminCode.getCodeName("UN", id);
						}else{
							if("UM".equalsIgnoreCase(codeset))
								text = AdminCode.getCodeName("UN", id).length()==0?AdminCode.getCodeName("UM", id):AdminCode.getCodeName("UN", id);
							else
								text = id!=null?AdminCode.getCodeName(codeset, id):id;
						}
						map.put("id", id.substring(0, length));
						map.put("text", text);
//						map.put("sp_flag", spflagMap.get(id));
						map.put("desc", spflagMap.get(id+"num"));
						map.put("color", spflagMap.get(id+"color"));//图片颜色
						map.put("num", rs.getString("num"));
						map.put("iconCls", "no-icon");
						for(int i=0;i<value.length;i++){
							if(!"".equals(value[i])){
								FieldItem item=DataDictionary.getFieldItem(value[i]);
								map.put(value[i].toUpperCase(), PubFunc.round(rs.getString(value[i].toUpperCase()), item.getDecimalwidth()));
							}
						}
//						if(record.length()>0){
//							String[] records = record.split("#");
//							for(int i=0;i<records.length;i++){
//								int len = records[i].length();
//								if((len>=length&&id.equals(records[i].substring(0, length)))||(length>=len&&records[i].equals(id.substring(0, len)))){
//									map.put("expanded", "true");
//									break;
//								}
//							}
//						}
						j++;
						list.add(map);
					}
				}
			}
			this.getFormHM().put("data", list);
			filterWhl.append(" and ((((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"')");
			this.userView.getHm().put("gzsp_filterWhl",filterWhl.toString());//将过滤条件插入 用户自定义报表使用 zhanghua
			
			String inCount=(String)this.getFormHM().get("inCount");
			if(("4".equals(inCount)|| "1".equals(inCount)&&bosdate.length()>0&&count!=null)){//1为初次加载，4为切换业务日期
				LazyDynaBean bean=new LazyDynaBean();
				String[] temp=bosdate.split("\\.");
				bean.set("year", temp[0]);//年
				bean.set("month", temp[1]);//月
				bean.set("count", count);//次数
				LazyDynaBean _bean=bo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"4");
				PendingTask pt = new PendingTask();
				if("update".equals(_bean.get("flag"))){
					pt.updatePending("G", "G"+_bean.get("pending_id"), 2, "薪资审批", this.userView);
				}		
				if("update".equals(_bean.get("selfflag"))){
					pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	/**
	 * 获取id 上级代码（id按长度截取的 可能不是正对应代码 则取id长度减1的代码 取到为止）
	 * @param id
	 * @param codeset
	 * @return
	 */
	private String getUpdesc(String id,String codeset){
		String value = "";
		try{
			String str = "";
			if(id==null||id.length()==0){
				return "未维护项";
			}
			if(codeset==null||codeset.length()==0){
				str = AdminCode.getCodeName("UN", id).length()==0?AdminCode.getCodeName("UM", id):AdminCode.getCodeName("UN", id);
				if(str==null||str.length()==0){
					String _id = id.substring(0, id.length()-1);
					value = this.getUpdesc(_id, codeset);
				}else{
					value = str;
				}
			}else{
				str = id!=null?AdminCode.getCodeName(codeset, id):"";
				if(id!=null&&(str==null||str.length()==0)&&"UM".equalsIgnoreCase(codeset))
					str=AdminCode.getCodeName("UN", id);
				
				if(str==null||str.length()==0){
					String _id = id.substring(0, id.length()-1);
					value = this.getUpdesc(_id, codeset);
				}else{
					value = str;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
}
