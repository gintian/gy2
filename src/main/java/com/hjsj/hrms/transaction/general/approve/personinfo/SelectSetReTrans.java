package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.ApproveSQLStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectSetReTrans extends IBusiness {
	private ArrayList itemidlist=new ArrayList();
	private ArrayList codeidlist = new ArrayList();
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		UserView uv =this.getUserView();
		HashMap hm = this.getFormHM();
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		if(reqhm!=null)
		{
		   String state=(String) reqhm.get("state");
		   String pdbflag =(String) reqhm.get("pdbflag");
		   String abkflag = (String) hm.get("abkflag");
		   String unitid = (String) hm.get("unitid");
		   String departmentid=(String )hm.get("departmentid");
		   String userid=(String )hm.get("userid");
		   String setid = (String) reqhm.get("setid");
		   String ff=(String)reqhm.get("ff");
		   String fr=(String)reqhm.get("fr");
		   reqhm.remove("ff");
		   reqhm.remove("fr");
		   if(ff==null)
		    	ff="b";
		   hm.put("ff",ff);
		   ArrayList itemdesclist = this.getItemlist(uv,setid);
		   ArrayList itemidlist = this.getItemidlist();
		   String tablename = setid;
		   if("a".equalsIgnoreCase(abkflag)){
			   if(pdbflag==null){
				 pdbflag=(String) reqhm.get("dataname");
			   }
			   tablename = pdbflag+setid;
		   }
		   if(reqhm.containsKey("a_code")){
			   String a_code=(String) reqhm.get("a_code");	
			   hm.put("a_code",a_code);
			   if(a_code.startsWith("UN")||a_code.startsWith("un")){
				   unitid=a_code.substring(2,a_code.length());
			   }else if(a_code.startsWith("UM")||a_code.startsWith("um")||a_code.startsWith("@K")||a_code.startsWith("@k")){
				  departmentid=a_code.substring(2,a_code.length());
			   }else{
				   if(a_code.length()>3)
				      userid=a_code.substring(3,a_code.length());
			   }
			   reqhm.remove("a_code");
		   }
		   String[] sqlStr=null;		 
	       if(fr!=null&& "1".equals(fr)){
			   sqlStr=ApproveSQLStr.getRetStr1(this.userView,itemidlist,unitid,departmentid,userid,tablename,setid,state);
		   }else{
			   sqlStr=ApproveSQLStr.getRetStr(this.userView,itemidlist,unitid,departmentid,userid,tablename,setid,state);
		   }
		
	       hm.put("itemdesclist",itemdesclist);
		   //hm.put("itemidlist",DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET));	
	       if("A00".equalsIgnoreCase(setid))
	    	   hm.put("itemidlist",getA00FieldList()); 
	       else	   
		     hm.put("itemidlist",uv.getPrivFieldList(setid));
		   
		   hm.put("codeidlist",this.getCodeidlist());
		   hm.put("sql",sqlStr[0]);
		   hm.put("where",sqlStr[1]);
		   hm.put("column",sqlStr[2]);
		   hm.put("state",state);
		   hm.put("pdbflag",pdbflag);
		}else{
			String state=(String) hm.get("state");
			String dataname=(String) hm.get("dataname");
			String orgid=(String) hm.get("orgid");
			hm.remove("state");
			hm.remove("dataname");
			hm.remove("orgid");
			this.getItemlist(uv,"A01");			
			String sqlsss="";
			if("4".equals(state)|| "5".equals(state)){
				sqlsss=getResultSql(dataname,orgid,state);
			}else{
				String[] sqlStr=ApproveSQLStr.getRetStr1(this.userView,this.getItemidlist(),orgid,null,null,dataname+"A01","A01",state);
				sqlsss=sqlStr[0]+" "+sqlStr[1];
			}
			ArrayList relist=dao.searchDynaList(sqlsss);
			StringBuffer sbhtml=new StringBuffer();
			sbhtml.append("<table align='center' border='1' width='100%' cellspacing='0' cellpadding='0' class='ListTable'>");
			sbhtml.append("<tr class='TableRow'>");
			sbhtml.append("<td height='50' align='center' class='RecordRow' nowrap>");
			sbhtml.append("姓名");
			sbhtml.append("</td>");
			/* 需要单位和部门的时候再这里打开
			sbhtml.append("<td height='50'align='center' class='RecordRow' nowrap>");
			sbhtml.append("单位名称");
			sbhtml.append("</td>");
			sbhtml.append("<td height='50'align='center' class='RecordRow' nowrap>");
			sbhtml.append("部门");
			sbhtml.append("</td>");
			*/
//			sbhtml.append("<td height='50'align='center' class='RecordRow' nowrap>");
//			sbhtml.append("职位名称");
//			sbhtml.append("</td>");
			sbhtml.append("</tr>");
			for(int i=0;i<relist.size();i++){
				DynaBean dynabean=(DynaBean) relist.get(i);
				sbhtml.append("<tr>");
				sbhtml.append("<td align='center' class='RecordRow' nowrap>");
				sbhtml.append("<a href='#' onclick='openpage(&quot;"+dynabean.get("a0100")+"&quot;)'>");
				sbhtml.append(dynabean.get("a0101"));
				sbhtml.append("</a>");
				sbhtml.append("&nbsp;</td>");
				/*需要单位名称和部门的时候再这里打开
				sbhtml.append("<td align='center' class='RecordRow' nowrap>");
				String b0110=(String) dynabean.get("b0110");
				
				sbhtml.append(AdminCode.getCodeName("UN",b0110));
				sbhtml.append("&nbsp;</td>");
				sbhtml.append("<td align='center' class='RecordRow' nowrap>");
				sbhtml.append(AdminCode.getCodeName("UM",(String) dynabean.get("e0122")));
				sbhtml.append("&nbsp;</td>");
				*/
//				sbhtml.append("<td align='center' class='RecordRow' nowrap>");
//				sbhtml.append(AdminCode.getCodeName("@K",(String) dynabean.get("e012a1")));
//				sbhtml.append("&nbsp</td>");;
				sbhtml.append("</tr>");
			}
			sbhtml.append("</table>");
			hm.put("tableStr",sbhtml.toString());
//			System.out.println(sbhtml.toString());
		}
	}
	public ArrayList getItemlist(UserView uv,String setid){
		ArrayList itemidlist=new ArrayList();
		ArrayList itemdesclist=new ArrayList();
		ArrayList codeidlist = new ArrayList();
		ArrayList fieldlist=new ArrayList();
		if("A00".equalsIgnoreCase(setid))
		{
			fieldlist=getA00FieldList();
		}else
		{
			fieldlist = uv.getPrivFieldList(setid);			
		}
		for(int i = 0;i<fieldlist.size();i++){
			FieldItem f =(FieldItem) fieldlist.get(i);
			String itemid=f.getItemid();
			String itemdesc=f.getItemdesc();
			itemidlist.add(itemid);
			itemdesclist.add(itemdesc);
			String codeid = f.getCodesetid();
			codeidlist.add(codeid);
//			System.out.println(f.getItemid()+f.getItemdesc());
		}
		this.codeidlist = codeidlist;
		this.setItemidlist(itemidlist);
		return itemdesclist;
	}
	public ArrayList getItemidlist() {
		return itemidlist;
	}
	public void setItemidlist(ArrayList itemidlist) {
		this.itemidlist = itemidlist;
	}
	public ArrayList getCodeidlist() {
		return codeidlist;
	}
	public void setCodeidlist(ArrayList codeidlist) {
		this.codeidlist = codeidlist;
	}
	/**
	 * 查看多个子集申请人
	 * @param tablename
	 * @param orgid
	 * @param state
	 * @return
	 * @throws GeneralException
	 */
    private String getResultSql(String tablename,String orgid,String state) throws GeneralException
    {
    	String sqlc="select * from organization where codeitemid like '"+orgid+"%' and codesetid='UN'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
    	ArrayList tetlist=dao.searchDynaList(sqlc);
		String orgs="b0110";
		if(tetlist.size()==0){
			orgs="e0122";
		}
    	StringBuffer sql=new StringBuffer();
    	sql.append("select a0100,a0101 from "+tablename+"A01");
    	sql.append(" where "+orgs+"='"+orgid+"'");
    	sql.append(" and a0100 in (select distinct(a0100) from(");
    	ArrayList fieldlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
    	for(int r=0;r<fieldlist.size();r++){    		
				FieldSet fs=(FieldSet)fieldlist.get(r);
				sql.append("(select a0100 from  "+tablename+""+fs.getFieldsetid()+" uu where state='"+state+"')");
				if(r<fieldlist.size()-1)
					sql.append(" union ");
	    }	
    	sql.append(") a");
    	sql.append(")");
    	//System.out.println(sql.toString());
    	return sql.toString();
    }
    private ArrayList getA00FieldList()
    {
    	ArrayList list=new ArrayList();
    	FieldItem field=new FieldItem();
		field.setItemdesc("名称");
		field.setItemid("title");
		field.setItemtype("A");
		field.setCodesetid("0");
		list.add(field);
		/*field=new FieldItem();
		field.setItemdesc("状态");
		field.setItemid("state");
		field.setItemtype("A");
		field.setCodesetid("0");
		list.add(field);*/
		/*field=new FieldItem();
		field.setItemdesc("员工编号");
		field.setItemid("a0100");
		field.setItemtype("A");
		field.setCodesetid("0");
		list.add(field);*/
		return list;
    }
}

