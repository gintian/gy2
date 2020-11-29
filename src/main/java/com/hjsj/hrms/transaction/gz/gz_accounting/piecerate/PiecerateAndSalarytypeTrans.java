package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PiecerateAndSalarytypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList setlist = new ArrayList();
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			ArrayList zblist = new ArrayList();
			String zhib1 = "";
			String zhib2 = "";
			RowSet rs1 = null;
			RowSet rs2 = null;
			String salaryid=(String)hm.get("salaryid");
			String gz_module=(String)hm.get("gz_module");
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
//			String valid =(String)hm.get("valid");
			String expression_str = PubFunc.keyWord_reback(SafeCode.decode((String)hm.get("expression_str")));
			String zhouq1 = (String)hm.get("zhouq1");
			String zhibiao = (String)hm.get("zhibiao");
			String str = (String)hm.get("str");
			if(zhouq1.length()<=0){
//        	valid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"valid");
        	zhouq1=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"period");
        	str=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"firstday");//月周期
        	expression_str=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"strExpression");
        	zhibiao=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"relation_field");
			}
        	if(zhibiao!=null&&!"".equals(zhibiao)){
        	String[] zb = zhibiao.split(",");
        	for(int i=0;i<zb.length;i++){
        		/* 安全问题：薪资类别-参数设置-属性-其他参数-计件薪资-设置-保存不上 xiaoyun 2014-10-15 start */
        		//String[] zb1 = zb[i].split("=");
        		String[] zb1 = PubFunc.keyWord_reback(zb[i]).split("=");
        		/* 安全问题：薪资类别-参数设置-属性-其他参数-计件薪资-设置-保存不上 xiaoyun 2014-10-15 end */
        		String sqll = "select itemid,itemdesc from t_hr_busifield where upper(fieldsetid) ='S05'  and itemid ='"+zb1[0]+"'";
        		String tsql = "select itemid,itemdesc from salaryset where salaryid ='"+salaryid+"' and itemid ='"+zb1[1]+"'";
        		rs1 = dao.search(sqll);
        		while(rs1.next()){
        			String itemid1 = rs1.getString("itemid");
        			String itemdesc1 = rs1.getString("itemdesc");
        			zhib1 = itemid1 +":"+ itemdesc1;
        		}
        		rs2 = dao.search(tsql);
        		while(rs2.next()){
        			String itemid2 = rs2.getString("itemid");
        			String itemdesc2 = rs2.getString("itemdesc");
        			zhib2 = itemid2 +":"+ itemdesc2;
        		}
				CommonData datavo = new CommonData(zhib1,zhib2);
				zblist.add(datavo);
        	}
        	}
			ArrayList fieldList = new ArrayList();
			String sqlstr = "select itemid,itemdesc from salaryset where salaryid ="+salaryid+" and itemtype = 'N'";
			ArrayList dylist = null;
			String excludeStr1=",Nbase,A0100,A0000,A01Z2,A00Z3,A00Z0,A00Z1,B0110,E0122,A0101,A0120,".toUpperCase();
			CommonData obj1=new CommonData("","");
			fieldList.add(obj1);
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				String itemdesc = dynabean.get("itemdesc").toString();
				if (excludeStr1.indexOf(","+itemid.toUpperCase()+",")>-1) {continue;}
				
				CommonData dataobj = new CommonData(itemid,itemid+":"+itemdesc);
				fieldList.add(dataobj);
			}
			ArrayList list=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
			CommonData obj2=new CommonData("","");
			setlist.add(obj2);
			String excludeStr=",Nbase,A0100,I9999,S0100,".toUpperCase();
			for (int i=0;i<list.size();i++) {
				FieldItem fielditem = (FieldItem) list.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (!"N".equals(fielditem.getItemtype())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
				CommonData datavo = new CommonData(fielditem.getItemid().toUpperCase(),fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				setlist.add(datavo);
			}
			this.getFormHM().put("setlist1", setlist);
			this.getFormHM().put("zblist", zblist);
			this.getFormHM().put("zhibiaolist", fieldList);
			this.getFormHM().put("sp_status", zhouq1);
			this.getFormHM().put("formula", SafeCode.encode(expression_str));
			this.getFormHM().put("yuezb", str);
		}catch(Exception e)
		{
			e.printStackTrace();
		} 
	}

}
