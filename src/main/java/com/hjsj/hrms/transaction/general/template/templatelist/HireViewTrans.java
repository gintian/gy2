package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class HireViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		
		String tabid = (String)reqhm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		reqhm.remove("tabid");
		TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
		String hiddenItem = (String)this.getFormHM().get("hiddenItem");
		if(hiddenItem.length()>0&&!hiddenItem.endsWith(","))
			hiddenItem = hiddenItem+",";
		if(hiddenItem.length()>0)
			hiddenItem=","+hiddenItem;
		ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");

		StringBuffer hiretable = new StringBuffer();
		hiretable.append("<table width=\"100%\"  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"ListTableF\" style='border-left:0px;border-right:0px;border-top:0px;'>");
		hiretable.append("<tr class=\"fixedHeaderTr\">");
//		hiretable.append("<td width=\"10%\" align=\"center\" class=\"TableRow\" nowrap>&nbsp;</td>");
		hiretable.append("<td width=\"70%\" align=\"center\" class=\"TableRow\" style='border-left:0px;border-top:0px;' nowrap>指标名称&nbsp;</td>");
		hiretable.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" style='border-left:0px;border-top:0px;border-right:0px;' nowrap>状态&nbsp;</td></tr>");
		String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");
		//过滤掉相同指标
		String notequale=",";
		
		if(fieldSetSortStr!=null&&fieldSetSortStr.length()>0){

			String temp [] = fieldSetSortStr.split(",");
			for(int i=0;i<temp.length;i++){
				for(int j=0;j<templateSetList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
				
					//过滤掉单位职位异动的特殊指标
						if("codesetid".equalsIgnoreCase(abean.get("field_name").toString())|| "codeitemdesc".equalsIgnoreCase(abean.get("field_name").toString())|| "corcode".equalsIgnoreCase(abean.get("field_name").toString())|| "parentid".equalsIgnoreCase(abean.get("field_name").toString())|| "start_date".equalsIgnoreCase(abean.get("field_name").toString()))
							{
								break;
							}

					if(("0".equals(abean.get("isvar"))&&(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(temp[i]))||
							("1".equals(abean.get("subflag").toString().trim())&&(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()).equals(temp[i]))||("1".equals(abean.get("isvar"))&&abean.get("field_name").toString().trim().equalsIgnoreCase(temp[i]))){
						
						hiretable.append("<tr>");
//						hiretable.append("<td class=\"RecordRow\" nowrap>"+n+"</td>");
						if(abean.get("field_name").toString().trim().length()>0){
							if(notequale.indexOf(","+abean.get("field_name").toString().trim()+",")==-1){
								notequale+=abean.get("field_name").toString().trim()+",";
							}else{
								break;
							}
						}
						if("0".equals(abean.get("isvar"))&& "2".equals(abean.get("chgstate"))){

							if("1".equals(abean.get("subflag").toString().trim())){
								hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+"拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]"+"</td>");
							}else{
								hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+"拟["+abean.get("field_hz").toString().trim()+"]"+"</td>");
							}
						}else{
							if("1".equals(abean.get("subflag").toString().trim())){
								hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
							}else{
								hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
							}
							}
						hiretable.append("<td class=\"RecordRow\" style='border-right:0px;'nowrap>");
						
						int check =0;
						if("0".equals(abean.get("isvar"))){
						    if(hiddenItem!=null&&hiddenItem.indexOf(","+abean.get("field_name").toString().trim()+",")==-1)
							    check=1;
						    if(hiddenItem!=null&& "1".equals(abean.get("subflag")))
						    { 
						    	if(hiddenItem.indexOf(","+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+",")==-1)
						    	check=1;
						    	else
						    		check=0;	
						    }
						}else{
						    if(hiddenItem!=null&&hiddenItem.indexOf(","+abean.get("field_name").toString().trim()+",")==-1)
								check=1;
						}
						if("1".equals(abean.get("isvar"))){
								hiretable.append("<select name=\""+abean.get("field_name").toString().trim()+"\" size=1>");
								
						}else{
							if("1".equals(abean.get("subflag"))){
								hiretable.append("<select name=\""+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"\" size=1>");
							}else{
								hiretable.append("<select name=\""+abean.get("field_name").toString().trim()+"\" size=1>");
							}
							
						}
						//定义固定的指标  传参数 指标，templateSetList
						//定义区分变化前后的指标
						if(check>0){
							hiretable.append("<option value=\""+check+"\" selected>显示</option>");
							hiretable.append("<option value=\"0\">隐藏</option>");
						}else{

							hiretable.append("<option value=\"1\">显示</option>");
							hiretable.append("<option value=\"0\" selected>隐藏</option>");
						}
						hiretable.append("</select>");
						hiretable.append("</td></tr>");

						break;
					}
					
				}	
			}
		
		}else{
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
			if(abean.get("field_name").toString().trim().length()>0){
				if(notequale.indexOf(","+abean.get("field_name").toString().trim()+",")==-1){
					notequale+=abean.get("field_name").toString().trim()+",";
				}else{
					continue;
				}
			}
			//过滤掉单位职位异动的特殊指标
			if("codesetid".equalsIgnoreCase(abean.get("field_name").toString())|| "codeitemdesc".equalsIgnoreCase(abean.get("field_name").toString())|| "corcode".equalsIgnoreCase(abean.get("field_name").toString())|| "parentid".equalsIgnoreCase(abean.get("field_name").toString())|| "start_date".equalsIgnoreCase(abean.get("field_name").toString()))
				{
					continue;
				}
			hiretable.append("<tr>");
//			hiretable.append("<td class=\"RecordRow\" nowrap>"+n+"</td>");
			if("0".equals(abean.get("isvar"))&& "2".equals(abean.get("chgstate"))){

				if("1".equals(abean.get("subflag").toString().trim())){
					hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+"拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]"+"</td>");
				}else{
					hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+"拟["+abean.get("field_hz").toString().trim()+"]"+"</td>");
				}
			}else{
				if("1".equals(abean.get("subflag").toString().trim())){
					hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
				}else{
					hiretable.append("<td class=\"RecordRow\" style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
				}
				}
			hiretable.append("<td class=\"RecordRow\" style='border-right:0px;' nowrap>");
			
			int check =0;
			if("0".equals(abean.get("isvar"))){
			    if(hiddenItem!=null&&hiddenItem.indexOf(","+abean.get("field_name").toString().trim()+",")==-1)
				    check=1;
			   
			    if(hiddenItem!=null&& "1".equals(abean.get("subflag")))
			    { 
			    	if(hiddenItem.indexOf(","+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+",")==-1)
			    	check=1;
			    	else
			    		check=0;	
			    }
			    
			}else{
			    if(hiddenItem!=null&&hiddenItem.indexOf(","+abean.get("field_name").toString().trim()+",")==-1)
					check=1;
			}
			if("1".equals(abean.get("isvar"))){
				hiretable.append("<select name=\""+abean.get("field_name").toString().trim()+"\" size=1>");
				
		}else{
			if("1".equals(abean.get("subflag"))){
				hiretable.append("<select name=\""+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"\" size=1>");
			}else{
				hiretable.append("<select name=\""+abean.get("field_name").toString().trim()+"\" size=1>");
			}
			
		}
			//定义固定的指标  传参数 指标，templateSetList
			//定义区分变化前后的指标
			if(check>0){
				hiretable.append("<option value=\""+check+"\" selected>显示</option>");
				hiretable.append("<option value=\"0\">隐藏</option>");
			}else{

				hiretable.append("<option value=\"1\">显示</option>");
				hiretable.append("<option value=\"0\" selected>隐藏</option>");
			}
			hiretable.append("</select>");
			hiretable.append("</td></tr>");
//			n++;
		}
		}
	
		hiretable.append("</table>");
		

		hm.put("hire_table",hiretable.toString());
		hm.put("tabid",tabid);
	}
	
	

}
