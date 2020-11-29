package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class QueryAddressBookTrans extends IBusiness {


    public void execute() throws GeneralException {
        try{
            String nbase=(String)this.getFormHM().get("nbase");
            String code=(String)this.getFormHM().get("code");
            //this.getFormHM().remove("code");
            String kind=(String)this.getFormHM().get("kind");
            String columns=(String)this.getFormHM().get("columns");
            String select_name=(String)this.getFormHM().get("select_name");
            
            ArrayList fieldlist = (ArrayList)this.getFormHM().get("fieldlist");
            this.getFormHM().put("fieldlist", fieldlist);
             
            String query=(String)this.getFormHM().get("query");
            handlerQueryChar(fieldlist, query);
            String querylike=(String)this.getFormHM().get("querylike");
            if("1".equalsIgnoreCase(query))
                querylike = "";
            //zxj 20141023 组织机构树上选了节点query没有值，导致前台模糊查询选项不显示
            if (null == query || "".equals(query)) {
                query = "1";
            }
            this.getFormHM().remove("query");
            
            this.getFormHM().remove("select_name");
            
            if(querylike==null || querylike.length()<1)
                querylike="";
            else if ("on".equalsIgnoreCase(querylike))
                querylike = "1";
            
            StringBuffer sql=new StringBuffer();
            sql.append("select "+columns);
            StringBuffer where=new StringBuffer();
            //条件查询
            if("2".equalsIgnoreCase(query)){
                InfoUtils infoUtils=new InfoUtils();
                String whereTrem=infoUtils.getSql(this.userView,fieldlist,querylike,nbase,"1");
                where.append(whereTrem);
                String flag = "";
                if(whereTrem.indexOf("E01A1") != -1){
                	flag = "E01A1";
                }else if (whereTrem.indexOf("E0122") != -1) {
                	flag = "E0122";
				}else if (whereTrem.indexOf("B0110") != -1) {
					flag = "B0110";
				}
				if (flag.length() > 0) {
					for (int i = 0; i < fieldlist.size(); i++) {
						FieldItem item = (FieldItem) fieldlist.get(i);
						if (flag.equalsIgnoreCase(item.getItemid())) {
							code = item.getValue();
							break;
						}
					}
				} else {
                	code = "";
                }
            }
            
            if(where==null||where.length()<1)
                where.append("from "+nbase+"A01 where 1=1");
            
            if(kind==null||kind.length()<=0)
                kind="2";
            
            if(code==null||code.length()<=0)
                code="";
            //点树查询
			if ("1".equalsIgnoreCase(query)) {
				if ("1".equals(kind)) {
					where.append(" and e0122 like '" + code + "%'");
				}
				else if ("0".equals(kind)) {
					where.append(" and e01a1 like '" + code + "%'");
				}
				else {
					where.append(" and b0110 like '" + code + "%'");
				}
			}
            if(select_name!=null&&select_name.trim().length()>0)
            {
                //zxj20160215 jazz16601 去掉两边的空格,并且将安全原因转码的*转回
                select_name = select_name.trim().replace("＊", "*");
                select_name = PubFunc.getStr(select_name);
                InfoUtils infoUtils = new InfoUtils();
                String whereA0101 = infoUtils.whereA0101NoPriv(this.userView,this.getFrameconn(), nbase, select_name,"0");
                if(whereA0101!=null && whereA0101.length()>0)
                    where.append(" and "+whereA0101);
            }
            this.getFormHM().put("code", code);
            this.getFormHM().put("sqlstr", sql.toString());
            this.getFormHM().put("strwhere", where.toString());
            this.getFormHM().put("orderby", "order by a0000");
            this.userView.getHm().put("selfservice_sql", where.toString() + " order by a0000");
            this.getFormHM().put("querylike", querylike);
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    private void handlerQueryChar(ArrayList fieldList, String query) {
        for(int i = 0; i < fieldList.size(); i++)
        {
            FieldItem item = (FieldItem)fieldList.get(i);
            if(null == item.getValue() || "".equals(item.getValue()))
                continue;
            
            
            if ("A".equalsIgnoreCase(item.getItemtype()) || "M".equalsIgnoreCase(item.getItemtype())) {
                String value = item.getValue().trim().replaceAll("＊", "*").replaceAll("？", "?");
                if("1".equalsIgnoreCase(query))
                    item.setValue("");
                else
                    item.setValue(value);
                
                String viewValue = item.getViewvalue();
                if (viewValue != null) {
                     viewValue = viewValue.trim().replaceAll("＊", "*").replaceAll("？", "?");
                     if("1".equalsIgnoreCase(query))
                         item.setViewvalue("");
                     else
                         item.setViewvalue(viewValue);
                }
            }
        }
        
    }

}
