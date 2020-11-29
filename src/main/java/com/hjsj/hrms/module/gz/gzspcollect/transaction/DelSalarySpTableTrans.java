package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTotalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DelSalarySpTableTrans 
 * 类描述： 删除薪资汇总明细
 * 创建人：zhaoxg
 * 创建时间：Dec 28, 2015 1:54:56 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 28, 2015 1:54:56 PM
 * 修改备注： 
 * @version
 */
public class DelSalarySpTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList datalist=(ArrayList) this.getFormHM().get("deletedata");
		String tablekey = (String) this.getFormHM().get("tablekey");
	//	tablekey = PubFunc.decrypt(SafeCode.decode(tablekey));
		DbSecurityImpl dbS = new DbSecurityImpl();
		String salaryid = tablekey.split("_")[1];
		SalaryTotalBo bo = new SalaryTotalBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
		String tablename = "salaryhistory";
		StringBuffer wheresql = new StringBuffer();
		ArrayList<ArrayList> data=new ArrayList<ArrayList>();
		String a00z0Value = "";
		try
		{
			StringBuffer sql = new StringBuffer();
			StringBuffer where = new  StringBuffer();
			String [] strlist=new String[5];
			sql.setLength(0);
			sql.append("delete from "+tablename+" where ");
			String a00z2="";
			int a00z3=0;
			for(int i=0;i<datalist.size();i++){
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				Iterator iter = map.entrySet().iterator();
				wheresql.append("or (");
				strlist=new String[4];
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
//					if("sp_flag".equalsIgnoreCase(key.toString())){
//						String tempval = val.toString().length()>0?val.toString().split("`")[0]:val.toString();
//						if(!("02".equals(tempval)||"07".equals(tempval))){
//							this.getFormHM().put("result",false);
//							this.getFormHM().put("hinttext","只能删除已报批或驳回的记录！");
//							return;
//						}
//					}
					if("a00z0".equalsIgnoreCase(key.toString())){
						a00z0Value= val.toString();
						where.append("and ");
						where.append(key.toString());
						where.append(" ="+Sql_switcher.dateValue(val.toString())+" ");
					}else if("a00z1".equalsIgnoreCase(key.toString())){
						strlist[2]= val.toString();
						where.append("and ");
						where.append(key.toString());
						where.append(" ='"+val.toString()+"' ");
					}else if("a0100_e".equalsIgnoreCase(key.toString())){
						strlist[1]= PubFunc.decrypt(val.toString());
						where.append("and ");
						where.append("a0100");
						where.append(" ='"+PubFunc.decrypt(val.toString())+"' ");
					}else if("NBASE1_e".equalsIgnoreCase(key.toString())){
						strlist[0]= PubFunc.decrypt(val.toString()).toLowerCase();
						where.append("and ");
						where.append("lower(nbase)");
						where.append(" ='"+PubFunc.decrypt(val.toString()).toLowerCase()+"' ");
					}else if(StringUtils.isBlank(a00z2)&& "a00z2".equalsIgnoreCase(key.toString())){
						a00z2= val.toString();
						if(a00z2.length()==7)
                            a00z2=a00z2+"-01";
					}else if(a00z3==0&&"a00z3".equalsIgnoreCase(key.toString())){
						a00z3=Integer.parseInt(val.toString());
					}
				}
				strlist[3]=salaryid;
				data.add(new ArrayList(Arrays.asList(strlist)));
				wheresql.append(where.substring(3));
				wheresql.append(")  ");
				where = new  StringBuffer();
			}
			//sql.append(" exists (select null from "+tablename+" b where "+tablename+".A0100=b.A0100 and "+tablename+".nbase=b.nbase and "+tablename+".A00Z0=b.A00Z0 and "+tablename+".A00z1=b.A00z1 and");
			if(wheresql.length()>0)
				sql.append("("+wheresql.substring(2)+")");
			//sql.append(")");
			sql.append(" and sp_flag in ('02','07') and salaryid= "+salaryid);
			
			String strSql="select curr_user,sp_flag from "+tablename +" where ("+wheresql.substring(2)+") and sp_flag not in ('07') and salaryid= " + salaryid;//获取不处于驳回状态的记录
			RowSet rs1= dao.search(strSql);
			while(rs1.next()){
				if(!(rs1.getString("curr_user")!=null&&rs1.getString("curr_user").equals(this.userView.getUserName())&&"02".equals(rs1.getString("sp_flag")))){
					this.getFormHM().put("result",false);
					this.getFormHM().put("hinttext","只能删除已报批（当前自己处理的数据）或驳回的记录！");
					return;
				}
			}
			String str="";
			StringBuffer strUserFlag=new StringBuffer();
			
			String linsql = "";
			linsql = sql.substring(6);//获取from及其后面的片段
			ArrayList list = new ArrayList();
			ArrayList mappinglist = new ArrayList();
			String _sql = "select userflag,a00z0,a00z1,nbase,a0100 "+linsql;
			RowSet rs = dao.search(_sql);
			while(rs.next()){
				RecordVo newVo=new RecordVo(rs.getString("userflag")+"_salary_"+salaryid);
				newVo.setDate("a00z0", rs.getDate("a00z0"));
				newVo.setInt("a00z1",rs.getInt("a00z1"));
				newVo.setString("nbase",rs.getString("nbase"));
				newVo.setString("a0100",rs.getString("a0100"));
				list.add(newVo);
				
				ArrayList list1=new ArrayList();
				list1.add(salaryid);
				list1.add(rs.getString("userflag").toLowerCase());
				list1.add(rs.getString("nbase").toLowerCase());
				//list1.add(rs.getDate("a00z0"));
				list1.add(rs.getInt("a00z1"));
				list1.add(rs.getString("a0100"));
				mappinglist.add(list1);

				strUserFlag.append(rs.getString("userflag")+",");
			}
			dao.deleteValueObject(list);//删除临时表

			str="delete from salaryhistory where lower(nbase)=? and a0100=? and a00z0="+Sql_switcher.dateValue(a00z0Value)+" and a00z1=? and salaryid=? and sp_flag in ('02','07') ";
			// 打开Wallet
			dbS.open(this.getFrameconn(), str);
			dao.batchUpdate(str, data);//删除历史表
	    	// 关闭Wallet
 			dbS.close(this.getFrameconn());
			
			str="delete from  salary_mapping where salaryid=? and (lower(userflag)=?  or userflag is null) and lower(nbase)=? and a00z0="+Sql_switcher.dateValue(a00z0Value)+"  and a00z1=?  and a0100=? ";
			dbS.open(this.getFrameconn(), str);
			dao.batchUpdate(str, mappinglist);//删除薪资发放数据的映射表
			dbS.close(this.getFrameconn());
			str="delete from  gz_tax_mx where salaryid=? and (lower(userflag)=?  or userflag is null ) and lower(nbase)=? and a00z0="+Sql_switcher.dateValue(a00z0Value)+"  and a00z1=?  and a0100=? ";
			dbS.open(this.getFrameconn(), str);
			dao.batchUpdate(str, mappinglist);//删除所得税表
			dbS.close(this.getFrameconn());
			bo.reCalculateTotal(linsql, 2);



			SalaryAccountBo salaryBo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo gzbo=salaryBo.getSalaryTemplateBo();
			PendingTask pt = new PendingTask();
			LazyDynaBean bean=null;
			StringBuffer strt=new StringBuffer();
			strt.append("select count(1) num  from salaryhistory where (sp_flag='02' or sp_flag='07') and ");
			strt.append(" curr_user='"+this.userView.getUserName()+"'");
			strt.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(a00z2)+" and a00z3=?");
			ArrayList parmList = new ArrayList();
			parmList.add(salaryid);
			parmList.add(a00z3);
			rs=dao.search(strt.toString(),parmList);
			if(rs.next()){
				int num=rs.getInt("num");
				if(num==0){
                    bean=new LazyDynaBean();
                    String[] temp=new String[3];
                    if(a00z2.indexOf("\\.")>=0)
                        temp=a00z2.split("\\.");
                    else
                        temp=a00z2.split("-");
                    bean.set("year", temp[0]);//年
                    bean.set("month", temp[1]);//月
                    bean.set("count", a00z3);//次数
                    bean.set("sql","null");
                    if(gzbo.getTemplatevo()!=null&&gzbo.getTemplatevo().getString("cstate")!=null&& "1".equals(gzbo.getTemplatevo().getString("cstate")))
                        bean.set("cstate","1");
                    else{
                        bean.set("cstate","0");
                    }
					bean=salaryBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"3");
					if("update".equals(bean.get("selfflag"))){
						pt.updatePending("G", "G"+bean.get("selfpending_id"), 100, "薪资审批", this.userView);
					}


			//		String sql="select * from gz_extend_log where salaryid="+salaryid+" and lower(username)='"+username.toLowerCase()+"' and "+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"="+temps[1]+" and a00z3="+ff_count;
				}
			}

			//删除之后若相应此薪资类别剩余数据全部是都是结束。那么将单据状态置为已结束
			String[] usersList = strUserFlag.toString().split(",");
			for (String s : usersList) {
				if (StringUtils.isBlank(s)) {
					continue;
				}
				String tableName = s + "_salary_" + salaryid;
				StringBuffer strs = new StringBuffer();
				strs.append("select ").append(Sql_switcher.isnull("count(*)", "0"))
						.append(" as num from ").append(tableName).append(" where sp_flag='06'");
				rs = dao.search(strs.toString());
				int num = 0;
				if (rs.next()) {
					num = rs.getInt("num");
				}
				if (num != 0) {
					strs.setLength(0);
					strs.append("select ").append(Sql_switcher.isnull("count(*)", "0"))
							.append(" as num from ").append(tableName).append(" where sp_flag<>'06'");
					rs = dao.search(strs.toString());
					if (rs.next()) {
						if (rs.getInt("num") == 0) {
							strs.setLength(0);
							strs.append("update gz_extend_log set sp_flag='06' where a00z2=").append(Sql_switcher.dateValue(a00z2)).append(" and a00z3=? and username=? ");
							list = new ArrayList();
							list.add(a00z3);
							list.add(s);
							dao.update(strs.toString(), list);
						}
					}
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("result",false);
			this.getFormHM().put("hinttext",e.toString());
		}
	}

}
