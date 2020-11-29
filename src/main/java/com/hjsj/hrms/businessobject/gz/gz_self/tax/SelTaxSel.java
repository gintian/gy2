package com.hjsj.hrms.businessobject.gz.gz_self.tax;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelTaxSel {
//	计税时间下拉选择框
	public String getTax_DateSelStr(ContentDAO dao,String tax_date,UserView uv) throws GeneralException{
		SelfTaxSQL sts=new SelfTaxSQL();
		StringBuffer sbselstr=new StringBuffer();
		sbselstr.append("<select name='tax_date' onchange='seletetaxdate();'>");
		if(sts.isGz_tax_mxExist(dao)){
//			FieldItem tfit=DataDictionary.getFieldItem("tax_date");
			ArrayList dynalist=dao.searchDynaList("select distinct declare_tax from gz_tax_mx where a0100='"+uv.getA0100()+"' and flag=1 order by declare_tax desc");
			sbselstr.append("<option value='0' >");
			sbselstr.append(ResourceFactory.getProperty("label.select"));
			sbselstr.append("</option>");
			Map tempMap=new HashMap();
			for(int i=0;i<dynalist.size();i++){
				DynaBean dynabean=(DynaBean) dynalist.get(i);
				String date=(String)dynabean.get("declare_tax");
				if(date!=null&&date.length()>=8){
					String[] tempdate=date.split("-");
					StringBuffer sb=new StringBuffer();
					for(int j=0;j<tempdate.length;j++){
						if(j==0){
							sb.append(tempdate[0]);
						}else{
							if(tempdate[j].length()<2){
								sb.append("-0"+tempdate[j]);
							}else{
								sb.append("-"+tempdate[j]);
							}
						}
					}
					String fromdate=sb.toString();
//					System.out.println(fromdate);
					
					
					if(!tempMap.containsKey(fromdate.substring(0,7))){
						tempMap.put(fromdate.substring(0,7),fromdate.substring(0,7));
					
						if(tax_date.equals(fromdate.substring(0,7))){
							sbselstr.append("<option value='"+fromdate.substring(0,7)+"' selected='selected'>");
							sbselstr.append(fromdate.substring(0,7));
							sbselstr.append("</option>");
						}else{
							sbselstr.append("<option value='"+fromdate.substring(0,7)+"'>");
							sbselstr.append(fromdate.substring(0,7));
							sbselstr.append("</option>");
						}
					}
				}
			}
		}
		sbselstr.append("</select>");
		return sbselstr.toString();
	}
//	按年选择下拉框
	public String getYearSelStr(ContentDAO dao,String year,UserView uv) throws GeneralException{
		SelfTaxSQL sts=new SelfTaxSQL();
		StringBuffer sbselstr=new StringBuffer();
		sbselstr.append("<select name='nian' onchange='seletetyear();'>");
		if(sts.isGz_tax_mxExist(dao)){
			String sqlyy=Sql_switcher.year("declare_tax");
			ArrayList dynalist=dao.searchDynaList("select distinct ("+sqlyy+") as nian from gz_tax_mx where a0100='"+uv.getA0100()+"'  and flag=1  order by nian desc");
			sbselstr.append("<option value='0' >");
			sbselstr.append(ResourceFactory.getProperty("label.select"));
			sbselstr.append("</option>");
			for(int i=0;i<dynalist.size();i++){
				DynaBean dynabean=(DynaBean) dynalist.get(i);
				String yy=(String) dynabean.get("nian");
				if(yy!=null&&yy.length()>=4)
				if(year.equals(((String)dynabean.get("nian")))){
					sbselstr.append("<option value='"+yy+"' selected='selected'>");
					sbselstr.append(((String)dynabean.get("nian")));
					sbselstr.append("</option>");
				}else{
					sbselstr.append("<option value='"+dynabean.get("nian")+"'>");
					sbselstr.append(((String)dynabean.get("nian")));
					sbselstr.append("</option>");
				}
			}
		}
		sbselstr.append("</select>");
		return sbselstr.toString();
	}
}
