/*
 * Created on 2010-1-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class SearchPartyBusinessListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String a_code=(String)hm.get("a_code");
		this.getFormHM().put("return_code", a_code);
		String partylike = (String)this.getFormHM().get("partylike");
		partylike = partylike!=null&&partylike.length()>0?partylike:"0";
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		String query = (String)this.getFormHM().get("query");
		query = query!=null&&query.length()>0?query:"0";
		this.getFormHM().remove("query");
		String querylike = (String)this.getFormHM().get("querylike");
		querylike=querylike!=null&&querylike.length()>0?querylike:"0";
		ArrayList queryList = (ArrayList)this.getFormHM().get("selectfieldlist");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String param = (String) hm.get("param");
		param = param != null && param.length() > 0 ? param : "Y";
		
		String orderby = "";
		
		
		String table = "Y01";
		String tableidtoid = " on y01.y0100=codeitemid";
		if("Y".equalsIgnoreCase(param)){
			table = "Y01";
			tableidtoid = " on y01.y0100=codeitemid";
		}
		if("V".equalsIgnoreCase(param)){//团务
			table = "V01";
			tableidtoid = " on v01.v0100=codeitemid";
		}
		if("W".equalsIgnoreCase(param)){//工会
			table = "W01";
			tableidtoid = " on w01.w0100=codeitemid";
		}
		try
		{
			StringBuffer columns=new StringBuffer();
		    if(fieldstr!=null&&fieldstr.length()>0){			
		    	columns.append(fieldstr);
			}
			StringBuffer strsql=new StringBuffer();
			if("H".equalsIgnoreCase(param)){
				table = "H01";
				String codesetid ="";
				String codeitemid="";
				if(a_code.length()>2){
					codesetid = a_code.substring(0,2);
					codeitemid=a_code.substring(2);
				}else{
					codesetid = a_code;
				}
				StringBuffer select_str=new StringBuffer("select codeitemid "); 
				StringBuffer sqlfrom=new StringBuffer(" from H01");
				StringBuffer sqlwhere=new StringBuffer(" where codesetid='"+codesetid+"' and ");
				sqlwhere.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date "); 
				//sqlwhere.append("and codeitemid in (select H0100 from h01)"); 
				HashMap setMap = new HashMap();
				setMap.put("codeitem", null);
				setMap.put("H01", null);
				ArrayList fieldList = this.splitField(fieldstr,param);
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem fieldItem=(FieldItem)fieldList.get(i);
					String itemid=fieldItem.getItemid();
					itemid = "h0100".equals(itemid)?"codeitemdesc h0100":itemid;
					String setid = fieldItem.getFieldsetid();
					select_str.append(","+itemid);
					if(!setMap.containsKey(setid)){
						setMap.put(setid, null);
						sqlfrom.append(" left join "+setid+" on H01.H0100="+setid+".H0100");
						sqlwhere.append(" and ("+setid+".i9999=(select max(i9999) from "+setid+" t"+i+" where t"+i+".H0100="+setid+".H0100) or "+setid+".i9999 is null)");
					}
				}
				sqlfrom.append(" right join codeitem  on codeitem.codeitemid=H01.H0100 ");
				String sql = "select '1' from codeitem where codesetid='"+codesetid+"' and parentid = ";
				if(codeitemid==null || codeitemid.length()<1)
					sql+=" codeitemid ";
				else
					sql+=" '"+codeitemid+"' and codeitemid<>parentid ";
				sql+=" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date"; 
				ContentDAO dao = new ContentDAO(this.frameconn);
				frowset = dao.search(sql);
				if(frowset.next()){
				
					if("0".equals(partylike)){
						if(codeitemid == null || codeitemid.length()<1){
							sqlwhere.append(" and codeitem.parentid=codeitem.codeitemid");
						}else{
							sqlwhere.append(" and codeitem.parentid='"+codeitemid+"' and codeitem.parentid<>codeitem.codeitemid ");
						}
						
					}else{
						sqlwhere.append(" and codeitem.parentid like '"+codeitemid+"%' ");
						if(codeitemid != null && codeitemid.length()>0)
						     sqlwhere.append(" and codeitem.codeitemid<>'"+codeitemid+"'");
					}
				}else
					sqlwhere.append(" and codeitem.codeitemid='"+codeitemid+"'");
				strsql.append(select_str.append(sqlfrom).append(sqlwhere));
				orderby = " order by codeitem.codeitemid";//去掉codeitem.a0000条件，原因是a0000排序会导致层级显示混乱 guodd 2017-10-31
				
			}else{
					if(a_code!=null && a_code.trim().length()>2){
						if("0".equals(partylike)){
							strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where parentid='");
							strsql.append(a_code.substring(2));
							strsql.append("' and codeitemid<>parentid and  codesetid='" + a_code.substring(0,2) + "'");
						}else{
							strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codeitemid like '");
							strsql.append(a_code.substring(2));
							strsql.append("%' and codeitemid<>'"+a_code.substring(2)+"' and  codesetid='" + a_code.substring(0,2) + "'");
						}
					}
					else
					{
						int res_type = IResourceConstant.PARTY;
						if("V".equalsIgnoreCase(param))
							 res_type = IResourceConstant.MEMBER;
						if("H".equalsIgnoreCase(param));
							//res_type = IResourceConstant.;
						String codevalue = userView.getResourceString(res_type);
						    	   if(codevalue.length()<3){
						    		   if(userView.isSuper_admin()&&!userView.isBThreeUser())
						    			   codevalue="ALL";
						    		   else{
						    			   if("64".equals(codevalue)|| "65".equals(codevalue))
						    				   codevalue="ALL";
						    			   else
						    				   codevalue=""; 
						    		   }
						    	   }else{
						    		   codevalue=this.analyseManagePriv(codevalue);
						    		   if(codevalue.length()<1)
						    			   codevalue="ALL";
						    	   }
						if("ALL".equals(codevalue)){
							if("0".equals(partylike)){
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codeitemid=parentid and codesetid='" + a_code.substring(0,2) + "'");
							}else{
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codesetid='" + a_code.substring(0,2) + "'");
							}
						}else if(codevalue.length()==0){
							if("0".equals(partylike)){
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codeitemid=parentid and codesetid='" + a_code.substring(0,2) + "' and 1=2");
							}else{
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codesetid='" + a_code.substring(0,2) + "' and 1=2");
							}
						}else{
							if("0".equals(partylike)){
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codeitemid in ('"+codevalue+"') and codesetid='" + a_code.substring(0,2) + "'");
							}else{
								strsql.append("select distinct "+columns.toString()+",a0000 from "+table+" left join codeitem"+tableidtoid+" where codesetid='" + a_code.substring(0,2) + "' and ("+this.analyseManagePriv1(userView.getResourceString(res_type))+")");
							}
						}
					}
					strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					orderby = " order by codeitem.codeitemid"; //去掉codeitem.a0000条件，原因是a0000排序会导致层级显示混乱 guodd 2017-10-31
			}
			
			if("1".equals(query)){//点击的条件查询
				strsql.append(getWhere(queryList,querylike,strsql,table));
				this.getFormHM().put("isShowCondition", "block");
			}else{
				this.getFormHM().put("isShowCondition", "none");
			}
			cat.debug("-----strsql------>" + strsql.toString());
			String codemess="";
			CodeItem codeitem=AdminCode.getCode(a_code.substring(0,2).toUpperCase(), a_code.substring(2).toUpperCase(),5);
		    if(codeitem!=null)
				  codemess=codeitem.getCodename();
			if(codemess==null||codemess.length()<=0){
				String sql = "select codesetdesc from codeset where codesetid='"
					+ a_code.substring(0,2) + "'";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					codemess=this.frowset.getString("codesetdesc");
			}  
			if("TO".equals(a_code.toUpperCase()))
				codemess = "全部";
			this.getFormHM().put("codemess", codemess);
			this.getFormHM().put("sqlstr", strsql.toString());
			this.getFormHM().put("wherestr", "");
			this.getFormHM().put("columnstr", columns.toString());
			this.getFormHM().put("orderby", orderby);	
			this.getFormHM().put("isrefresh","no");
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}		
	}
	private String getWhere(ArrayList queryList,String querylike,StringBuffer strsql,String table)throws GeneralException{
		StringBuffer sb = new StringBuffer();
		HashMap map  = new HashMap();
		if(queryList==null||queryList.size()==0)
			return "";
		
		for(int i=0;i<queryList.size();i++){//已指标集分组指标
			FieldItem field = (FieldItem)queryList.get(i);
			if(map.containsKey(field.getFieldsetid())){
				((ArrayList)map.get(field.getFieldsetid())).add(field);
			}else{
				ArrayList list = new ArrayList();
				list.add(field);
				map.put(field.getFieldsetid(), list);
			}
		}
		for(Iterator i=map.keySet().iterator();i.hasNext();){
			String fieldsetid = (String)i.next();
			
			if("codeitem".equals(fieldsetid))
				continue;
			
			if(table.equalsIgnoreCase(fieldsetid)){
				ArrayList list = (ArrayList)map.get(fieldsetid);
				for(int n=0;n<list.size();n++){
					FieldItem field = (FieldItem)list.get(n);
					
					if("h0100".equals(field.getItemid())){
						if("1".equals(querylike)){
							if(!field.getViewvalue().equals(field.getValue()))
							  sb.append("(codeitem.codeitemid = '"+field.getValue()+"') and ");
							else
							  sb.append("(codeitem.codeitemdesc like '%"+field.getViewvalue()+"%') and ");
						}else
							if(!field.getViewvalue().equals(field.getValue()))
								  sb.append("(codeitem.codeitemid = '"+field.getValue()+"') and ");
							else
								  sb.append("(codeitem.codeitemdesc ='"+field.getViewvalue()+"') and ");
						continue;
					}
					
					if("A".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("0".equals(field.getCodesetid())){
							
							if("1".equals(querylike)){
								sb.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
							}else
								sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}else{
							sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}
					}else if("M".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("1".equals(querylike)){
							sb.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
						}else
							sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
					}else if("D".equals(field.getItemtype())){
						if((field.getValue()==null||field.getValue().length()==0)&&(field.getViewvalue()==null||field.getViewvalue().length()==0))
							continue;
						sb.append(analyFieldDate(field));
					}else if("N".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						sb.append(field.getItemid()+"="+field.getValue()+" and ");
					}
				}
			}else{
				if(strsql.toString().toUpperCase().indexOf((fieldsetid).toUpperCase())==-1){
					String uptablename=strsql.substring(strsql.indexOf("from")+5, strsql.indexOf("from")+8);//上一个连接的表名
					String temp=uptablename.substring(0, 1);
					strsql = strsql.replace(strsql.indexOf("from"), strsql.indexOf("from")+8, "from "+fieldsetid+" right join "+uptablename+" on "+uptablename+"."+temp+"0100="+fieldsetid+"."+temp+"0100");
				}
				ArrayList list = (ArrayList)map.get(fieldsetid);
				StringBuffer sbtemp=new StringBuffer();
				boolean f= false;
				for(int n=0;n<list.size();n++){
					FieldItem field = (FieldItem)list.get(n);
					if("A".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						f=true;
						if("0".equals(field.getCodesetid())){
							if("1".equals(querylike)){
								sbtemp.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
							}else
								sbtemp.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}else{
							if(field.getValue().indexOf("`")!=-1){
								sbtemp.append(field.getItemid()+" in ('#"+field.getValue().replaceAll("`", "','")+"') and ");
							}else
								sbtemp.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}
					}else if("M".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						f=true;
						if("1".equals(querylike)){
							sbtemp.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
						}else
							sbtemp.append(field.getItemid()+"='"+field.getValue()+"' and ");
					}else if("D".equals(field.getItemtype())){
						if((field.getValue()==null||field.getValue().length()==0)&&(field.getViewvalue()==null||field.getViewvalue().length()==0))
							continue;
						f=true;
						sbtemp.append(analyFieldDate(field));
					}else if("N".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						f=true;
						sbtemp.append(field.getItemid()+"="+field.getValue()+" and ");
					}
				}
				if(!f){
					sbtemp.insert(0, "((");
					sbtemp.append(fieldsetid+".i9999=(select max(i9999) from "+fieldsetid+" where "+table+"."+table+"00="+fieldsetid+"."+table+"00)) or (select max(i9999) from "+fieldsetid+" where "+table+"."+table+"00="+fieldsetid+"."+table+"00) is null) and ");
				}else{
					sbtemp.append(fieldsetid+".i9999=(select max(i9999) from "+fieldsetid+" where "+table+"."+table+"00="+fieldsetid+"."+table+"00) and ");
				}
				sb.append(sbtemp.toString());
			}
		}
		
		StringBuffer wherestr = new StringBuffer();
		
//		if(table.equals("H01")){
//			 
//			ArrayList itemlist = (ArrayList)map.get("codeitem");
//			for(int i=0;i<itemlist.size();i++){
//				FieldItem field = (FieldItem)itemlist.get(i);
//				
//				if(field.getValue() != null && field.getValue().length()>0){
//					wherestr.append(" and ");
//					if(querylike.equals("1"))
//					    wherestr.append(" codeitem."+field.getItemid()+" like '"+field.getValue()+"%' ");
//					else
//						wherestr.append(" codeitem."+field.getItemid()+" = '"+field.getValue()+"' ");
//				}
//			}
//		}
		
		
		if(sb.length()>4){
			wherestr.append(" and "+sb.substring(0, sb.length()-5));
		}
		return wherestr.toString();
	}
	
	private String analyFieldDate(FieldItem item) throws GeneralException{
		StringBuffer sb = new StringBuffer();
		String s_str_date=item.getValue();
        String e_str_date=item.getViewvalue();
        s_str_date=s_str_date.replaceAll("\\.","-");
        e_str_date=e_str_date.replaceAll("\\.","-"); 
        try{
	        Date s_date=DateStyle.parseDate(s_str_date);
	        Date e_date=DateStyle.parseDate(e_str_date);
	        if(s_str_date.length()>=8&&e_str_date.length()>=8){
	        	sb.append(item.getItemid()+" between "+Sql_switcher.dateValue(s_str_date)+" and "+Sql_switcher.dateValue(e_str_date)+" and ");
	        }else{
	        	if(s_str_date.length()>=8){
	        		sb.append(item.getItemid()+" >= "+Sql_switcher.dateValue(s_str_date)+" and ");
	        	}else if(e_str_date.length()>=8){
	        		sb.append(item.getItemid()+" <= "+Sql_switcher.dateValue(e_str_date)+" and ");
	        	}
	        }
        }catch(Exception e){
        	e.printStackTrace();
        	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
        }
		return sb.toString();
	}
	private String analyseManagePriv(String managed_str){
		if(managed_str.length()<3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1)
 						sb.append("','"+id.substring(2));
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1)
 							sb.append("','"+id.substring(2));
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "";
		else
			return sb.substring(3);
	}
	private String analyseManagePriv1(String managed_str){
		if(managed_str.length()<3)
			return "1=1";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1)
 						sb.append(" or codeitemid like '"+id.substring(2)+"%'");
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1)
 							sb.append(" or codeitemid like '"+id.substring(2)+"%'");
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "1=1";
		else
			return sb.substring(3);
	}
	
	private ArrayList splitField(String strfields,String param)
    {
        ArrayList list=new ArrayList();
        if(!"H".equals(param)){
	        FieldItem fielditem=new FieldItem();
	        fielditem.setItemid("codeitemdesc");
	        fielditem.setCodesetid("");
	        fielditem.setItemdesc(ResourceFactory.getProperty("column.name"));
	        fielditem.setItemtype("A");
	        fielditem.setFieldsetid("codeitem");
	        fielditem.setUseflag("1");
	        fielditem.setItemlength(30);
	        fielditem.setDisplaywidth(30);
	        list.add(fielditem);
        }
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone(); 
            	 if(item.getUseflag().length()>0&&!"0".equals(item.getUseflag())){
	                 list.add(item_0);
            	 }
            }
           
        }
        return list;
    }
	
}
