package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author xgq
 *@version 4.0
**/
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String sort = (String)hm.get("sorting");
		String tabid = (String)hm.get("tabid");
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		String sortstr [] = sort.split(",");
		int nsort=0;
//		System.out.println(sort);
		
		TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
		
		ArrayList templateSetList=bo.getAllCell();
		 try {

		for(int j=0;j<sortstr.length;j++){
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
			CommonData dataobj = null;
			if("0".equals(abean.get("isvar"))){
				if("2".equals(abean.get("chgstate"))){
					if("1".equals(abean.get("subflag").toString().trim())){
						 if(sortstr[j].equalsIgnoreCase(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim())){
							 nsort=nsort+10;
							 String gridno =""+abean.get("gridno");
						 if(gridno.length()==0|| "null".equalsIgnoreCase(gridno))
							continue;
						 String pageid =""+abean.get("pageid");
						 if(pageid.length()==0|| "null".equalsIgnoreCase(pageid))
							 continue;
						 RecordVo vo = new RecordVo("Template_Set");
						 vo.setInt("tabid", Integer.parseInt(tabid));
						 vo.setInt("pageid", Integer.parseInt(pageid));
						 vo.setInt("gridno", Integer.parseInt(gridno));
							vo = dao.findByPrimaryKey(vo);
							vo.setInt("nsort", nsort);
							dao.updateValueObject(vo);
							break;
					
						 }
					}else{
						String sub_domain_id = "";
						if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
							sub_domain_id = (String)abean.get("sub_domain_id");
						if(sub_domain_id!=null&&sub_domain_id.length()>0){
							sub_domain_id ="_"+sub_domain_id;
						}else{
							sub_domain_id="";
						}
						}
						 if(sortstr[j].equalsIgnoreCase(abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim())){
							 nsort=nsort+10;
							 String gridno =""+abean.get("gridno");
							 if(gridno.length()==0|| "null".equalsIgnoreCase(gridno))
									continue;
								 String pageid =""+abean.get("pageid");
								 if(pageid.length()==0|| "null".equalsIgnoreCase(pageid))
									 continue;
						 RecordVo vo = new RecordVo("Template_Set");
						 vo.setInt("tabid", Integer.parseInt(tabid));
						 vo.setInt("pageid", Integer.parseInt(pageid));
						 vo.setInt("gridno", Integer.parseInt(gridno));
							vo = dao.findByPrimaryKey(vo);
							vo.setInt("nsort", nsort);
							dao.updateValueObject(vo);
							break;
					
						 }
					}
					
						}else{
							if("1".equals(abean.get("subflag").toString().trim())){
								 if(sortstr[j].equalsIgnoreCase(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim())){
									 nsort=nsort+10;
									 String gridno =""+abean.get("gridno");
									 if(gridno.length()==0|| "null".equalsIgnoreCase(gridno))
											continue;
										 String pageid =""+abean.get("pageid");
										 if(pageid.length()==0|| "null".equalsIgnoreCase(pageid))
											 continue;
								 RecordVo vo = new RecordVo("Template_Set");
								 vo.setInt("tabid", Integer.parseInt(tabid));
								 vo.setInt("pageid", Integer.parseInt(pageid));
								 vo.setInt("gridno", Integer.parseInt(gridno));
									vo = dao.findByPrimaryKey(vo);
									vo.setInt("nsort", nsort);
									dao.updateValueObject(vo);
									break;
							
								 }
							}else{
								String sub_domain_id = "";
								if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
									sub_domain_id = (String)abean.get("sub_domain_id");
								if(sub_domain_id!=null&&sub_domain_id.length()>0){
									sub_domain_id ="_"+sub_domain_id;
								}else{
									sub_domain_id="";
								}
								}
							 if(sortstr[j].equalsIgnoreCase(abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim())){
								 nsort=nsort+10;
								 String gridno =""+abean.get("gridno");
								 if(gridno.length()==0|| "null".equalsIgnoreCase(gridno))
										continue;
									 String pageid =""+abean.get("pageid");
									 if(pageid.length()==0|| "null".equalsIgnoreCase(pageid))
										 continue;
							 RecordVo vo = new RecordVo("Template_Set");
							 vo.setInt("tabid", Integer.parseInt(tabid));
							 vo.setInt("pageid", Integer.parseInt(pageid));
							 vo.setInt("gridno", Integer.parseInt(gridno));
								vo = dao.findByPrimaryKey(vo);
								vo.setInt("nsort", nsort);
								dao.updateValueObject(vo);
								break;
						
							 }
							}
						}
					}else{
			
			 if(sortstr[j].equalsIgnoreCase(abean.get("field_name").toString())){
				 nsort=nsort+10;
				 String gridno =""+abean.get("gridno");
				 if(gridno.length()==0|| "null".equalsIgnoreCase(gridno))
						continue;
					 String pageid =""+abean.get("pageid");
					 if(pageid.length()==0|| "null".equalsIgnoreCase(pageid))
						 continue;
			 RecordVo vo = new RecordVo("Template_Set");
			 vo.setInt("tabid", Integer.parseInt(tabid));
			 vo.setInt("pageid", Integer.parseInt(pageid));
			 vo.setInt("gridno", Integer.parseInt(gridno));
				vo = dao.findByPrimaryKey(vo);
				vo.setInt("nsort", nsort);
				dao.updateValueObject(vo);
				break;
		
			 }
			}
			
		}
		}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		hm.put("info",sort);
	}
		
}