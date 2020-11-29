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
public class LockView extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		
		String targetid = (String)hm.get("targetid");
		targetid=targetid!=null&&targetid.trim().length()>0?targetid:"";
		TemplateListBo bo=new TemplateListBo(this.getFormHM().get("tabid").toString(),this.getFrameconn(),this.userView);
		
		ArrayList templateSetList=bo.getAllCell();
//		ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");

		StringBuffer locktable = new StringBuffer();
		locktable.append("<table width='100%'  border='0' cellspacing='0'  align='top' cellpadding='0' class='ListTableF' style='border-left:0px;border-right:0px;border-top:0px;'>");
		locktable.append("<tr class='fixedHeaderTr'>");
//		locktable.append("<td width='10%' align='center' class='TableRow' nowrap>&nbsp;</td>");
		locktable.append("<td width='70%' align='center' class='TableRow' style='border-left:0px;border-top:0px;' nowrap>指标名称&nbsp;</td>");
		locktable.append("<td width='20%' align='center' class='TableRow' style='border-left:0px;border-top:0px;border-right:0px;' nowrap>状态&nbsp;</td></tr>");
		String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");
		String fieldSetSortStr2=fieldSetSortStr;
		String lockedItemStr ="";
		if(fieldSetSortStr2!=null&&fieldSetSortStr2.length()>0){
			if(fieldSetSortStr2.length()>0&&!fieldSetSortStr2.endsWith(","))
				fieldSetSortStr2 = fieldSetSortStr2+",";
			if(fieldSetSortStr2.indexOf(targetid+",")!=-1){
			String temp [] = fieldSetSortStr.split(",");
			for(int i=0;i<temp.length;i++){
				lockedItemStr += temp[i]+",";
				if(temp[i].equals(targetid)){
					
					break;
				}
			}
			}
		}else{
			for(int i=0;i<templateSetList.size();i++){
				LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
				if("0".equals(abean.get("isvar"))){
					if("1".equals(abean.get("subflag"))){
						lockedItemStr += abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+",";
						if((abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()).equalsIgnoreCase(targetid)){
						    break;
						}
					
					}else{
						lockedItemStr += abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()+",";
						if((abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(targetid)){
						    break;
						}
					}
				
				}else{
					
					lockedItemStr += abean.get("field_name").toString().trim()+",";
						if((abean.get("field_name").toString().trim()).equalsIgnoreCase(targetid)){
						break;
					} 
				   
				
			}
		}
		}
		if(lockedItemStr.length()<=0)
			lockedItemStr=targetid;
		if(lockedItemStr.length()>0&&!lockedItemStr.endsWith(","))
			lockedItemStr+=",";
		lockedItemStr=","+lockedItemStr;
		if("0".equals(this.getFormHM().get("value"))){
			lockedItemStr=lockedItemStr.replace(targetid+",", "");
		
		}
		if(fieldSetSortStr!=null&&fieldSetSortStr.length()>0){

			String temp [] = fieldSetSortStr.split(",");
			for(int i=0;i<temp.length;i++){
				for(int j=0;j<templateSetList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
					String sub_domain_id = "";
					if("0".equals(abean.get("isvar"))&&"1".equals(abean.get("chgstate"))&& "0".equals(abean.get("subflag").toString().trim())){
						if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
							sub_domain_id = (String)abean.get("sub_domain_id");
						if(sub_domain_id!=null&&sub_domain_id.length()>0){
							sub_domain_id ="_"+sub_domain_id;
						}else{
							sub_domain_id="";
						}
						}
					}
					if(("0".equals(abean.get("isvar"))&&(abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(temp[i]))||
							("1".equals(abean.get("subflag").toString().trim())&&(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()).equals(temp[i]))||("1".equals(abean.get("isvar"))&&abean.get("field_name").toString().trim().equalsIgnoreCase(temp[i]))){
						
						locktable.append("<tr>");
//						locktable.append("<td class='RecordRow' nowrap>"+n+"</td>");
						if("0".equals(abean.get("isvar"))&& "2".equals(abean.get("chgstate"))){

							if("1".equals(abean.get("subflag").toString().trim())){
								locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+"拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]"+"</td>");
							}else{
								locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+"拟["+abean.get("field_hz").toString().trim()+"]"+"</td>");
							}
						}else{
							if("1".equals(abean.get("subflag").toString().trim())){
								locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
							}else{
								locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
							}
						}
						locktable.append("<td class='RecordRow' style='border-right:0px;' nowrap>");
						
						int check =0;
						if("0".equals(abean.get("isvar"))){
						    
						    //默认，我改了，变为解锁，也就说 session没有默认指标 ，首先判断session是否有值没有走默认，有则走session
						    if(lockedItemStr.length()>0){
						    	if(lockedItemStr!=null&&lockedItemStr.indexOf(","+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+",")!=-1)
								    check=1;
						    	  else if(lockedItemStr!=null&& "1".equals(abean.get("subflag"))&&lockedItemStr.indexOf(","+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+",")!=-1)
								    	 check=1;
						    }else{
						    	check=0;
//						    	if(abean.get("chgstate").equals("1")&&(abean.get("field_name").toString().trim().equalsIgnoreCase("B0110")||abean.get("field_name").toString().trim().equalsIgnoreCase("E0122")||abean.get("field_name").toString().trim().equalsIgnoreCase("E01A1")||abean.get("field_name").toString().trim().equalsIgnoreCase("A0101")))
//						    		check=1;	
						    }
						}else{
						 
						    if(lockedItemStr.length()>0){
						    	   if(lockedItemStr!=null&&lockedItemStr.indexOf(abean.get("field_name").toString().trim()+",")!=-1)
										check=1;
						    }else{
						    	check=0;
//						    	if(abean.get("chgstate").equals("1")&&(abean.get("field_name").toString().trim().equalsIgnoreCase("B0110")||abean.get("field_name").toString().trim().equalsIgnoreCase("E0122")||abean.get("field_name").toString().trim().equalsIgnoreCase("E01A1")||abean.get("field_name").toString().trim().equalsIgnoreCase("A0101")))
//						    		check=1;	
						    }
						}
						if("1".equals(abean.get("isvar"))){
								locktable.append("<select name='"+abean.get("field_name").toString().trim()+"' onchange=selectFresh('"+abean.get("field_name").toString().trim()+"')  size=1>");
						}else{
							if("1".equals(abean.get("subflag"))){
								locktable.append("<select name='"+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"'  onchange=selectFresh('"+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"')  size=1>");
							}else{
								locktable.append("<select name='"+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+"' onchange=selectFresh('"+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+"')  size=1>");
							}
							
							
						}
						//定义固定的指标  传参数 指标，templateSetList
//						if(bo.isFixedTarget(abean)){
//							locktable.append("<option value='"+check+"' selected >锁定</option>");
//							locktable.append("<option value='0'>解锁</option>");
//							continue;
//						}
						//定义区分变化前后的指标
						if(check>0){
							locktable.append("<option value='0' >解锁</option>");
							locktable.append("<option  value='"+check+"' selected >锁定</option>");
						}else{

							locktable.append("<option value='0' selected>解锁</option>");
							locktable.append("<option value='1' >锁定</option>");
						}
						locktable.append("</select>");
						locktable.append("</td></tr>");

						break;
					}
					
				}	
			}
		
		}else{
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);

			locktable.append("<tr>");
//			locktable.append("<td class='RecordRow' nowrap>"+n+"</td>");
			
			if("0".equals(abean.get("isvar"))&& "2".equals(abean.get("chgstate"))){

				if("1".equals(abean.get("subflag").toString().trim())){
					locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+"拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]"+"</td>");
				}else{
					locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+"拟["+abean.get("field_hz").toString().trim()+"]"+"</td>");
				}
			}else{
				if("1".equals(abean.get("subflag").toString().trim())){
					locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
				}else{
					locktable.append("<td class='RecordRow' style='border-left:0px;' nowrap>"+abean.get("hz").toString().replaceAll("`","").trim()+"</td>");
				}
			}
			locktable.append("<td class='RecordRow' style='border-right:0px;' nowrap>");
			
			int check =0;
			String sub_domain_id = "";
			if("0".equals(abean.get("isvar"))&&"1".equals(abean.get("chgstate"))){
				if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
					sub_domain_id = (String)abean.get("sub_domain_id");
				if(sub_domain_id!=null&&sub_domain_id.length()>0){
					sub_domain_id ="_"+sub_domain_id;
				}else{
					sub_domain_id="";
				}
				}
			}
			if("0".equals(abean.get("isvar"))){
			    
			    //默认，我改了，变为解锁，也就说 session没有默认指标 ，首先判断session是否有值没有走默认，有则走session
				
			    if(lockedItemStr.length()>0){
			    	if(lockedItemStr!=null&&lockedItemStr.indexOf(","+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+",")!=-1)
					    check=1;
			    	  else if(lockedItemStr!=null&& "1".equals(abean.get("subflag"))&&lockedItemStr.indexOf(","+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+",")!=-1)
					    	 check=1;
			    }else{
			    	if("1".equals(abean.get("chgstate"))&&("B0110".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "E0122".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "E01A1".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "A0101".equalsIgnoreCase(abean.get("field_name").toString().trim())))
			    		check=1;	
			    }
			}else{
			 
			    if(lockedItemStr.length()>0){
			    	   if(lockedItemStr!=null&&lockedItemStr.indexOf(abean.get("field_name").toString().trim()+",")!=-1)
							check=1;
			    }else{
			    	if("1".equals(abean.get("chgstate"))&&("B0110".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "E0122".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "E01A1".equalsIgnoreCase(abean.get("field_name").toString().trim())|| "A0101".equalsIgnoreCase(abean.get("field_name").toString().trim())))
			    		check=1;	
			    }
			}
			if("1".equals(abean.get("isvar"))){
				locktable.append("<select name='"+abean.get("field_name").toString().trim()+"' onchange=selectFresh('"+abean.get("field_name").toString().trim()+"')  size=1>");
		}else{
			if("1".equals(abean.get("subflag"))){
				locktable.append("<select name='"+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"'  onchange=selectFresh('"+abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()+"')  size=1>");
			}else{
				locktable.append("<select name='"+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+"' onchange=selectFresh('"+abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim()+"')  size=1>");
			}
			
			
		}
			//定义固定的指标  传参数 指标，templateSetList
//			if(bo.isFixedTarget(abean)){
//				locktable.append("<option value='"+check+"' selected >锁定</option>");
//				locktable.append("<option value='0'>解锁</option>");
//				continue;
//			}
			//定义区分变化前后的指标
			if(check>0){
				locktable.append("<option value='0' >解锁</option>");
				locktable.append("<option  value='"+check+"' selected >锁定</option>");
			}else{

				locktable.append("<option value='0' selected>解锁</option>");
				locktable.append("<option value='1' >锁定</option>");
			}
			locktable.append("</select>");
			locktable.append("</td></tr>");
//			n++;
		}
		}
		locktable.append("</table>");
		
		
		this.getFormHM().put("infotable",locktable.toString());
	
	
	}


}
