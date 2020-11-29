package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class PersonFilterTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String selectFields=(String)this.getFormHM().get("rightFields");
			String condid=(String)this.getFormHM().get("filterCondId");
			String tabid=(String)this.getFormHM().get("tabid");
			TemplateListBo bo2=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");
			String[] fields=selectFields.split(",");
			ArrayList list = new ArrayList();
			ArrayList fieldlist=new ArrayList();
			ArrayList personFilterList=(ArrayList) this.getFormHM().get("personFilterList");
			String expr="";
//			String expr=this.getFormHM().get("expr")==null?"":(String)this.getFormHM().get("expr");
//			String tempexpr = expr;
			for(int i=0;i<fields.length;i++)
			{
//			    if(tempexpr.equals(""))	
				expr +="*"+(i+1);
				String fieldname = fields[i];
				if (fieldname == null || "".equals(fieldname))
					continue;
//				boolean flag = false;
//				for(int j=0;j<personFilterList.size();j++){
//					Factor factor =(Factor)personFilterList.get(j);
//					if(factor.getFieldname().equalsIgnoreCase(fieldname)){
//						list.add(factor);
//						
//							/**选中的指标列表*/
//							CommonData vo=new CommonData();
//							vo.setDataName(factor.getHz());
//							vo.setDataValue(factor.getFieldname());
//							fieldlist.add(vo);	
//						
//						flag =true;
//					    break;
//					}
//					
//				}
//				if(flag)
//					continue;
				String copyfieldname = fieldname;
				LazyDynaBean bean = new LazyDynaBean();
			if(templateSetList!=null&&templateSetList.size()>0)
				for(int j=0;j<templateSetList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
					
					if(("0".equals(abean.get("isvar"))&&(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(fieldname))||
							("1".equals(abean.get("isvar"))&&abean.get("field_name").toString().trim().equalsIgnoreCase(fieldname))){
						
					//
					
						if("0".equals(abean.get("isvar"))){//不是临时变量
							if(fieldname.indexOf("_")!=-1)
								copyfieldname = fieldname.substring(0,fieldname.lastIndexOf("_"));
						FieldItem fielditem =	DataDictionary.getFieldItem(copyfieldname);
						if(fielditem==null){
							if("codesetid".equalsIgnoreCase(copyfieldname)|| "codeitemdesc".equalsIgnoreCase(copyfieldname)|| "corcode".equalsIgnoreCase(copyfieldname)|| "parentid".equalsIgnoreCase(copyfieldname)|| "start_date".equalsIgnoreCase(copyfieldname))
							{
								if(bo2.getBo().getInfor_type()!=1){
									fielditem=new FieldItem();
									fielditem.setItemid(fieldname);
									//fielditem.setItemdesc(setbo.getHz());
									fielditem.setFieldsetid(abean.get("setname").toString().trim());
									fielditem.setItemtype(abean.get("field_type").toString().trim());
									fielditem.setCodesetid((String)abean.get("codeid"));
									if(!"start_date".equalsIgnoreCase(copyfieldname))
										fielditem.setItemlength(50);
									fielditem.setUseflag("1");
								}else
									continue;
							}else{
								continue;
							}
						}
						   bean.set("itemid",fieldname);
			    		   if("2".equals(abean.get("chgstate")))
						   bean.set("itemdesc","拟["+abean.get("field_hz").toString().trim()+"]");
			    		   else
			    			   bean.set("itemdesc",abean.get("field_hz").toString().trim());   
			    		   bean.set("itemlength",""+fielditem.getItemlength());
			    		   bean.set("decwidth",""+fielditem.getDecimalwidth());
				    	   bean.set("codesetid",fielditem.getCodesetid());
			    		   bean.set("itemtype",fielditem.getItemtype());
						
						}else{
							LazyDynaBean bean2 = bo2.getAllVariableBean(fieldname);
							if(bean2!=null){
								   bean.set("itemid",fieldname);
					    		   bean.set("itemdesc",bean2.get("hz"));   
					    		   bean.set("itemlength",bean2.get("fldlen"));
					    		   bean.set("decwidth",bean2.get("flddec"));
						    	   bean.set("codesetid",bean2.get("codeid"));
					    		   bean.set("itemtype",bean2.get("field_type"));
							}
								
						}
							
						Factor factor = null;
						if (bean != null) {
							/**选中的指标列表*/
							CommonData vo=new CommonData();
							vo.setDataName((String)bean.get("itemdesc"));
							vo.setDataValue((String)bean.get("itemid"));
							fieldlist.add(vo);	
						}
						factor = new Factor(1);
						factor.setCodeid((String)bean.get("codesetid"));
						factor.setFieldname((String)bean.get("itemid"));
						factor.setHz((String)bean.get("itemdesc"));
						factor.setFieldtype((String)bean.get("itemtype"));
						String s = (String)bean.get("itemlength");
						factor.setItemlen(Integer.parseInt(bean.get("itemlength")==null?"0":(String)bean.get("itemlength")));
					
						factor.setItemdecimal(Integer.parseInt(bean.get("decwidth")==null?"0":(String)bean.get("decwidth")));
						factor.setOper("=");// default
						factor.setHzvalue("");
						factor.setValue("");

						list.add(factor);


						
							break;
						}
					
				}
					
					
				}	

			 if(expr.length()>0&&(expr.startsWith("*")||expr.startsWith("+")))
			expr = expr.substring(1);
			 this.getFormHM().put("issave","3");
			this.getFormHM().put("personFilterList",list); 
			this.getFormHM().put("selectedFieldList",fieldlist);
			this.getFormHM().put("tabid",tabid);
			this.getFormHM().put("rightFields",selectFields);
			this.getFormHM().put("filterCondId",condid);
			this.getFormHM().put("expr",expr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
