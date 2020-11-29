package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTotalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DelSalaryTableTrans 
 * 类描述：薪资发放删除薪资数据 
 * 创建人：zhaoxg
 * 创建时间：Jun 27, 2015 4:37:11 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 27, 2015 4:37:11 PM
 * 修改备注： 
 * @version
 */
public class DelSalaryTableTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList datalist=(ArrayList) this.getFormHM().get("deletedata");
	//	String tablename = (String) this.getFormHM().get("tablekey");
	//	tablename = PubFunc.decrypt(SafeCode.decode(tablename));

		DbSecurityImpl dbS = new DbSecurityImpl();
		//根据表格控件id获取薪资表名
		String tablename="";
		//String tablekey= (String) this.getFormHM().get("tablekey");
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
//		String [] stritem=tablekey.split("_");
//		if(StringUtils.isNotBlank(tablekey)&&stritem.length>1){
//		}
		tablename=new SalaryTemplateBo(this.frameconn, Integer.parseInt(salaryid), this.userView).getGz_tablename();
		if(StringUtils.isBlank(tablename)) {
			return;
		}
		
		//String salaryid = tablename.split("_salary_")[1];
		SalaryTotalBo bo = new SalaryTotalBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
		RowSet rs=null;
		String whereSql = "";
		try
		{
			StringBuffer sql = new StringBuffer();
			StringBuffer where = new  StringBuffer();
			StringBuffer wheresql = new StringBuffer();
			
			ArrayList<ArrayList> data=new ArrayList<ArrayList>();
			String [] strlist=new String[4];
			String a00z2="",a00z3="";
			sql.setLength(0);
			sql.append("delete from "+tablename+" where  ");
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
//						if(!("01".equals(tempval)||"07".equals(tempval))){
//							this.getFormHM().put("result",false);
//							this.getFormHM().put("hinttext","只能删除起草或驳回的记录！");
//							return;
//						}
//					}
					if("a00z0".equalsIgnoreCase(key.toString())){
						strlist[2]= val.toString();
						where.append("and ");
						where.append(key.toString());
						where.append(" ="+Sql_switcher.dateValue(val.toString())+" ");
					}else if("a00z1".equalsIgnoreCase(key.toString())){
						strlist[3]= val.toString();
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
					}else if(StringUtils.isBlank(a00z2)&&"a00z2".equalsIgnoreCase(key.toString()))
						a00z2=val.toString();
					else if(StringUtils.isBlank(a00z3)&&"a00z3".equalsIgnoreCase(key.toString()))
						a00z3=val.toString();
				}
				data.add(new ArrayList(Arrays.asList(strlist)));
				wheresql.append(where.substring(3));
				wheresql.append(")  ");
				where = new  StringBuffer();
			}
			//sql.append(" exists (select null from "+tablename+" b where "+tablename+".A0100=b.A0100 and "+tablename+".nbase=b.nbase and "+tablename+".A00Z0=b.A00Z0 and "+tablename+".A00z1=b.A00z1 and");
			if(wheresql.length()>0)
				sql.append("("+wheresql.substring(2)+")");
//			sql.append(")");
			sql.append(" and sp_flag in ('01','07')");
			
			String strSql="select count(1) as num from "+tablename +" where ("+wheresql.substring(2)+") and sp_flag not in ('01','07')";//获取不处于起草或驳回状态的记录
			rs= dao.search(strSql);
			if(rs.next())
				if(rs.getInt("num")>0){
					throw GeneralExceptionHandler.Handle(new Exception("只能删除起草或驳回的记录！"));
//					this.getFormHM().put("result",false);
//					this.getFormHM().put("hinttext","只能删除起草或驳回的记录！");
//					return;

				}
			SalaryAccountBo salaryBo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo tempbo=salaryBo.getSalaryTemplateBo();
			String a00z0str="";
			if(Sql_switcher.searchDbServer()==2)
				a00z0str=" to_date(?,'yyyy-mm-dd') ";
			else
				a00z0str=" CONVERT(varchar(20), ?, 23) ";
			String manager=tempbo.getManager();
			String str="delete from "+tablename+" where lower(nbase)=? and a0100=? and a00z0="+a00z0str+" and a00z1=?";
			// 打开Wallet
			dbS.open(this.getFrameconn(), str);
			dao.batchUpdate(str, data);
			// 关闭Wallet
			dbS.close(this.getFrameconn());
			
			
			for(ArrayList array:data){
				array.add(salaryid);
			}
			
			
			//删除对应关系表
			str="delete from salary_mapping where ";
			if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
				str+=" lower(userflag)='"+manager.toLowerCase()+"' ";
	    	else
	    		str+="  lower(userflag)='"+this.userView.getUserName().toLowerCase()+"'";
			str=str+" and lower(nbase)=? and a0100=? and a00z0="+a00z0str+" and a00z1=?  and salaryid=? ";
			// 打开Wallet
			dbS.open(this.getFrameconn(), str);
			dao.batchUpdate(str,data);
	    	// 关闭Wallet
			dbS.close(this.getFrameconn());
			
			
			
			//删除税率明细中的记录
	     	DbWizard dbw=new DbWizard(this.getFrameconn());
			if(dbw.isExistTable("gz_tax_mx", false))
			{	
				if(!dbw.isExistField("gz_tax_mx", "UserFlag"))
				{
					Table tbl=new Table("gz_tax_mx");
					Field field=new Field("UserFlag","UserFlag");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				
		    	str="delete from gz_tax_mx where  lower(nbase)=? and a0100=? and a00z0="+a00z0str+" and a00z1=? and salaryid=? ";
		    	if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
		    		str+=" and ( lower(userflag)='"+manager.toLowerCase()+"' or userflag is null )";
		    	else
		    		str+=" and ( lower(userflag)='"+this.userView.getUserName().toLowerCase()+"' or userflag is null )";
		    	
		    	// 打开Wallet
				dbS.open(this.getFrameconn(), str);
				dao.batchUpdate(str,data);
		    	// 关闭Wallet
				dbS.close(this.getFrameconn());
		    	
				str="delete from salaryhistory where  lower(nbase)=? and a0100=? and a00z0="+a00z0str+" and a00z1=? and salaryid=? ";
		    	if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
		    		str+=" and lower(userflag)='"+manager.toLowerCase()+"' ";
		    	else
		    		str+=" and  lower(userflag)='"+this.userView.getUserName().toLowerCase()+"'";
		    	str+=" and sp_flag<>'06'";
		    	
		    	// 打开Wallet
				dbS.open(this.getFrameconn(), str);
				dao.batchUpdate(str,data);
		    	// 关闭Wallet
				dbS.close(this.getFrameconn());
			}
			
			whereSql = sql.substring(6);//获取from及其后面的片段
			bo.reCalculateTotal(whereSql, 2);

			PendingTask pt = new PendingTask();
			LazyDynaBean bean=null;
			if(tempbo.getManager()!=null&&tempbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(tempbo.getManager()))//共享非管理员
				where.append(tempbo.getWhlByUnits(tablename,true));

			StringBuffer strt=new StringBuffer();
			strt.append("select count(1) as num from "+tablename+" where sp_flag='07'"+where);
			rs=dao.search(strt.toString());
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
					if(tempbo.getTemplatevo()!=null&&tempbo.getTemplatevo().getString("cstate")!=null&& "1".equals(tempbo.getTemplatevo().getString("cstate")))
						bean.set("cstate","1");
					else{
						bean.set("cstate","0");
					}
					bean=salaryBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"5");
					if("update".equals(bean.get("selfflag"))){
						pt.updatePending("G", "G"+bean.get("selfpending_id"), 100, "薪资发放", this.userView);
					}
				}
			}
			//如果剩余数据都为已结束状态，则修改gz_extend_log中单据状态为已结束 zhanghua 2018年4月18日 17:07:21
			boolean isEnd=false;
			strt.setLength(0);
			strt.append(" select sum((case when sp_flag='06' then 1 else 0 end)) as num, count(1) as totalNum from "+tablename);
			rs=dao.search(strt.toString());
			if(rs.next()) {
				int num = rs.getInt("num");
				int totalNum = rs.getInt("totalNum");
				if(num==totalNum&&totalNum>0){
					strt.setLength(0);
					strt.append("update gz_extend_log set sp_flag='06',isredo=0 where ( sp_flag='01' or sp_flag='05' ) and salaryid=? and upper(username)=?");
					ArrayList paralist=new ArrayList();
					paralist.add(salaryid);
					paralist.add(manager.length()>0?manager.toUpperCase():this.userView.getUserName().toUpperCase());
					if(dao.update(strt.toString(),paralist)>0);
						isEnd=true;
				}
			}
			this.getFormHM().put("isEnd",isEnd);

		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
//			this.getFormHM().put("result",false);
//			this.getFormHM().put("hinttext",e.toString());
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
}
