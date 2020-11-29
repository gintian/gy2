package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;

public class SaveSetGroTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap ff=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)ff.get("optt");//是否从新初始化数据=1 是=2不
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
			String ctrl_type = (String)this.getFormHM().get("ctrl_type");
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");
			String spflagid = (String)this.getFormHM().get("spflagid");
			String planitem = (String)this.getFormHM().get("planitem");
			String realitem = (String)this.getFormHM().get("realitem");
			String classitem=(String)this.getFormHM().get("classitem");
			String balanceitem = (String)this.getFormHM().get("balanceitem");
			String flagitem = (String)this.getFormHM().get("flagitem");
			String formular=(String)this.getFormHM().get("formularStr");
			String salarySet=(String)this.getFormHM().get("salarySet");
			String ctrl_by_level=(String)this.getFormHM().get("ctrl_by_level");
		
			String deptid = (String)this.getFormHM().get("deptid");
			String orgid = (String)this.getFormHM().get("orgid");
			String contrlLevelId=(String)this.getFormHM().get("contrlLevelId");
			String ctrl_peroid=(String)this.getFormHM().get("ctrl_peroid");
			String amountAdjustSet=(String)this.getFormHM().get("amountAdjustSet");
			String amountPlanitemDescField=(String)this.getFormHM().get("amountPlanitemDescField");
			String  surplus_compute=(String)this.getFormHM().get("surplus_compute");//封存结余参与计算
			String fc_flag=(String)this.getFormHM().get("fc_flag");
			String ctrlAmountField=(String)this.getFormHM().get("ctrlAmountField");
			/* 安全问题：薪资总额参数：增加项目、公式，正确保存第一条计算公式后，新建第二条计算公式后，点击【保存】，系统将这两条计算公式都删除了（未转码引起的） xiaoyun 2014-10-16 start */
			planitem = PubFunc.keyWord_reback(planitem);
			realitem = PubFunc.keyWord_reback(realitem);
			classitem = PubFunc.keyWord_reback(classitem);
			balanceitem = PubFunc.keyWord_reback(balanceitem);
			flagitem = PubFunc.keyWord_reback(flagitem);
			
			formular = PubFunc.keyWord_reback(SafeCode.decode(formular));
			
			salarySet = PubFunc.keyWord_reback(salarySet);
			ctrl_by_level = PubFunc.keyWord_reback(ctrl_by_level);
			
			amountAdjustSet = PubFunc.keyWord_reback(amountAdjustSet);
			amountPlanitemDescField = PubFunc.keyWord_reback(amountPlanitemDescField);
			surplus_compute = PubFunc.keyWord_reback(surplus_compute);
			fc_flag = PubFunc.keyWord_reback(fc_flag);
			ctrlAmountField = PubFunc.keyWord_reback(ctrlAmountField);
			
			bo.setCtrl_by_level_value(ctrl_by_level);
			/* 安全问题：薪资总额参数：增加项目、公式，正确保存第一条计算公式后，新建第二条计算公式后，点击【保存】，系统将这两条计算公式都删除了（未转码引起的） xiaoyun 2014-10-16 end */
			StringBuffer sql=new StringBuffer();
			if(fc_flag!=null&&fc_flag.length()!=0){
				HashMap map = bo.getValuesMap();
				if(map.get("fc_flag")!=null){
					String flag=(String)map.get("fc_flag");
					if(flag.equalsIgnoreCase(fc_flag)){
						String setid =(String)map.get("setid");
						if(fieldsetid.equalsIgnoreCase(setid)){
							
						}else{
							sql.append("update ");
							sql.append(fieldsetid);
							sql.append(" set ");
							sql.append(fc_flag);
							sql.append("=2 where ");
							sql.append(fc_flag);
							sql.append(" is null or ");
							sql.append(fc_flag);
							sql.append("=''");
						}
					}else{
						sql.append("update ");
						sql.append(fieldsetid);
						sql.append(" set ");
						sql.append(fc_flag);
						sql.append("=2 where ");
						sql.append(fc_flag);
						sql.append(" is null or ");
						sql.append(fc_flag);
						sql.append("=''");
					}
				}else{
					sql.append("update ");
					sql.append(fieldsetid);
					sql.append(" set ");
					sql.append(fc_flag);
					sql.append("=2 where ");
					sql.append(fc_flag);
					sql.append(" is null or ");
					sql.append(fc_flag);
					sql.append("=''");
				}
			}
			if(sql.length()!=0){
				ContentDAO dao =new ContentDAO(this.frameconn);
				dao.update(sql.toString());
			}
			HashMap map = getFormularmap(formular);
			HashMap propertyMap = new HashMap();
			propertyMap.put("amountAdjustSet", amountAdjustSet);
			propertyMap.put("amountPlanitemDescField", amountPlanitemDescField);
			propertyMap.put("surplus_compute", surplus_compute);
			propertyMap.put("fc_flag", fc_flag);
			propertyMap.put("ctrl_field", ctrlAmountField);
			bo.setProperty(GzAmountXMLBo.Gz_amounts, "setid", fieldsetid, "sp_flag", spflagid, "ctrl_type", ctrl_type, "ctrl_peroid",ctrl_peroid,"","","",propertyMap);
			bo.setProperty(GzAmountXMLBo.Gz_amount,"orgid","un", "", "", "", "", "", "","","","",null);
			bo.setProperty(GzAmountXMLBo.Gz_check,"orgid",orgid,"deptid",deptid, "contrlLevelId", contrlLevelId, "", "", "","","",null);
			String[] plan=planitem.split("/");
			String[] real = realitem.split("/");
			String[] balance = balanceitem.split("/");
			String[] flag= flagitem.split("/");
			String[] classi=classitem.split("`");
			for(int i=0;i<plan.length;i++)
			{
				String formu = (String)(map.get(plan[i].toUpperCase())==null?"":map.get(plan[i].toUpperCase()));
				String className="";
				if(i<classi.length)
					className=classi[i];
				bo.setProperty(GzAmountXMLBo.ctrl_item, "planitem",plan[i],"realitem", real[i], "balanceitem",balance[i],"flag",flag[i],formu,"className",className,null);
			}
			this.save(salarySet, bo);
			bo.saveParameters();
			if("1".equals(opt))
			{
				GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
				if(fc_flag!=null&&fc_flag.length()!=0){
					gmb.setFc_flag(fc_flag);
				}
				int year=Calendar.getInstance().get(Calendar.YEAR);
				gmb.initSpField(fieldsetid, spflagid, String.valueOf(year));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public HashMap getFormularmap(String formular)
	{
		HashMap map = new HashMap();
		try
		{
			String[] temp = formular.split("`");
			for(int i =0;i<temp.length;i++)
			{
				String[] t = temp[i].split("#");
				if(t.length==2)
				{
		    		map.put(t[0].toUpperCase(),SafeCode.decode(t[1]==null?"":t[1]));
				}
				else
				{
					map.put(t[0].toUpperCase(),"");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void save(String salaryset,GzAmountXMLBo bo)
	{
		String[] temp = salaryset.split("`");
		for(int i =0;i<temp.length;i++)
		{
			String[] t = temp[i].split("#");
			if(t.length==2)
			{
	    		bo.setSalarySet(t[0],SafeCode.decode(t[1]==null?"":t[1]));
			}
			else
			{
				bo.setSalarySet(t[0], "");
			}
		}
	}
	

}
