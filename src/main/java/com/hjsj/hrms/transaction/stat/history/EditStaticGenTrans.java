package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 常用统计编辑统计项
 * @author Owner
 *
 */
public class EditStaticGenTrans extends IBusiness{
	
	  public void execute() throws GeneralException 
	  {
		  String statid=(String)this.getFormHM().get("statid");		 
		  String opflag=(String)this.getFormHM().get("opflag");
		  String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		  if(opflag==null||opflag.length()<=0)
			  opflag="new";
		  String stat_name="";
		  String stype="";
		  String sformula="";
		  String hv="";
		  String type =(String)this.getFormHM().get("type");
		  String categories = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("categories");
		  categories=categories==null?"":com.hrms.frame.codec.SafeCode.decode(categories);
		  String grouptype = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("type_categories");//修改 统计条件分类 参数 wangb 20190705
		  ((HashMap)this.getFormHM().get("requestPamaHM")).remove("type_categories");
		  if(grouptype != null && "categories".equalsIgnoreCase(grouptype)){
			  this.formHM.put("categories", categories);
			  return;
		  }
		  ArrayList catelist = new ArrayList();
		  //(Id,Name,Flag,Type,InfoKind)
		  StringBuffer sql=new StringBuffer();
		  sql.append("select categories from  hr_hisdata_sname where infokind="+infor_Flag+" group by categories ");
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  StringBuffer hidcategories = new StringBuffer();
		  try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String temp = this.frowset.getString("categories");
				if(temp==null||temp.length()==0)
					continue;
				CommonData cd = new CommonData(temp,temp);
				catelist.add(cd);
				hidcategories.append(","+temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		  if("edit".equals(opflag))
		  {
			  if(statid==null||statid.length()<=0)
				  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			  sql.setLength(0);
			  sql.append("select name,stype,sformula,categories,hv,type,sbase  from  hr_hisdata_sname where id='"+statid+"'");
			  try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					stat_name=this.frowset.getString("name");
					stype=String.valueOf(this.frowset.getInt("stype"));
					categories = this.frowset.getString("categories");
					categories=categories==null?"":categories;
					sformula = Sql_switcher.readMemo(this.frowset,"sformula");
					hv = Sql_switcher.readMemo(this.frowset, "hv");
					//this.frowset.getString("hv");
					//hv = hv==null?"":hv;
					type = this.frowset.getString("type");
					type = type==null?"1":type;
					String sbase = this.frowset.getString("sbase");
					sbase = sbase==null?"":sbase;
					this.getSbase(sbase, dao);
				}else
					throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }else
		  {
			  statid="";
			  try {
				this.getSbase("", dao);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		  if(!"1".equals(type))
			  outTable(opflag,hv);
		  else
			  getItemList(dao);
		  this.getFormHM().put("stat_name", stat_name);
		  this.getFormHM().put("stype", stype);
		  this.getFormHM().put("infor_Flag", infor_Flag);
		  this.getFormHM().put("opflag", opflag);
		  this.formHM.put("catelist", catelist);
		  this.formHM.put("categories", categories);
		  this.formHM.put("hidcategories", hidcategories.length()>0?hidcategories.substring(1):"");
		  this.getFormHM().put("sformula", sformula);
		  this.getFormHM().put("type", type);
      }

	  private void outTable(String opflag,String hv){
			StringBuffer htablestr = new StringBuffer();
			StringBuffer vtablestr = new StringBuffer();
			String[] temp = hv.split("\\|");
			String h="",v="";
			if(temp.length==2){
				h=temp[0];
				v=temp[1];
			}
			htablestr.append("<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTablex\">");
			htablestr.append("<tr class=\"fixedHeaderTr1\">");
			htablestr.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" nowrap><input type=checkbox onclick=\"batch_select_filter(this,'h');\" title='全选'/></td>");
			htablestr.append("<td width=\"80%\" align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("kq.report.name")+"</td></tr>");
			
			vtablestr.append("<table width=\"100%\" border=\"0\" align=\"center\" class=\"ListTablex\">");
			vtablestr.append("<tr class=\"fixedHeaderTr1\">");
			vtablestr.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" nowrap><input type=checkbox onclick=\"batch_select_filter(this,'v');\" title='全选' /></td>");
			vtablestr.append("<td width=\"80%\" align=\"center\" class=\"TableRow\" nowrap>"+ResourceFactory.getProperty("kq.report.name")+"</td></tr>");
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sqlstr = "select id,name,stype,sformula from hr_hisdata_sname t1 where type='1' and exists (select t2.id from hr_hisdata_slegend t2 where t1.id=t2.id)";
			sqlstr+=" order by snorder";
			try {
				this.frowset=dao.search(sqlstr);
				int i=0;
				while(this.frowset.next()){
					String id = String.valueOf(this.frowset.getInt("id"));
					String name = this.frowset.getString("name");
					String stype = String.valueOf(this.frowset.getInt("stype"));
					String sformula = com.hrms.hjsj.utils.Sql_switcher.readMemo(this.frowset, "sformula");
					if(i%2==0){
						htablestr.append("<tr class=\"trShallow\" onMouseOver=\"javascript:tr_onclick(this,'')\" >");
					}else
						htablestr.append("<tr class=\"trDeep\" onMouseOver=\"javascript:tr_onclick(this,'DDEAFE')\" >");
					if(i%2==0){
						vtablestr.append("<tr class=\"trShallow\" onMouseOver=\"javascript:tr_onclick(this,'')\" >");
					}else
						vtablestr.append("<tr class=\"trDeep\" onMouseOver=\"javascript:tr_onclick(this,'DDEAFE')\" >");
					
					
					htablestr.append("<td class=\"RecordRow\" align=\"center\"");
					htablestr.append("\">");
					htablestr.append("<input type=\"checkbox\" onclick=\"boxfilter('h');\" stype=\""+stype+"\" sformula=\""+sformula+"\" name=\"");
					htablestr.append("h");
					htablestr.append("\" value=\""+id+"\" ");
					if("edit".endsWith(opflag)&&h.indexOf(id)!=-1){
						htablestr.append("checked");
					}
					htablestr.append(">");
					htablestr.append("</td><td class=\"RecordRow\"");
					htablestr.append("\">");
					htablestr.append(name);
					htablestr.append("</td></tr>");
					
					vtablestr.append("<td class=\"RecordRow\" align=\"center\"");
					vtablestr.append("\">");
					vtablestr.append("<input type=\"checkbox\" onclick=\"boxfilter('v');\" stype=\""+stype+"\" sformula=\""+sformula+"\" name=\"");
					vtablestr.append("v");
					vtablestr.append("\" value=\""+id+"\" ");
					if("edit".endsWith(opflag)&&v.indexOf(id)!=-1){
						vtablestr.append("checked");
					}
					vtablestr.append(">");
					vtablestr.append("</td><td class=\"RecordRow\"");
					vtablestr.append("\">");
					vtablestr.append(name);
					vtablestr.append("</td></tr>");
					i++;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			htablestr.append("</table>");
			vtablestr.append("</table>");
			this.getFormHM().put("hformulatable",htablestr.toString());
			this.getFormHM().put("vformulatable",vtablestr.toString());
		}
	  
	  private void getItemList(ContentDAO dao){
		  try{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from hr_emp_hisdata where 1=2");
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			ArrayList itemlist = new ArrayList();
			CommonData data = new CommonData("","");
			itemlist.add(data);
			for (int i = 1; i <= size; i++) {
				String itemid = rsmd.getColumnName(i).toUpperCase();
				if (itemid.length() < 4 || "nbase".equalsIgnoreCase(itemid)
						|| "b0110".equalsIgnoreCase(itemid)
						|| "e0122".equalsIgnoreCase(itemid)
						|| "e01a1".equalsIgnoreCase(itemid)
						|| "a0101".equalsIgnoreCase(itemid)
						|| "a0100".equalsIgnoreCase(itemid)
						|| "a0000".equalsIgnoreCase(itemid))
					continue;
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					if("N".equals(fielditem.getItemtype())){
						data = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
						itemlist.add(data);
					}
				}
			}
			this.getFormHM().put("itemlist", itemlist);
		  }catch(Exception e){e.printStackTrace();}
	  }

	  private void getSbase(String sbase,ContentDAO dao) throws SQLException{
		  
		  StringBuffer sb= new StringBuffer();
		  String str_value="";
			///String sql="select str_value from constant where upper(constant)='EMP_HISDATA_BASE'";
			RowSet rs =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");//zhangcq 设置的人员库
			if(rs.next()){
				ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
				str_value =xml.getTextValue("/Emp_HisPoint/Base");
			}else{
				//设置的人员库
				rs=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_BASE'");
				if(rs.next())
					str_value=rs.getString("str_value");
			}
		/*	this.frowset = dao.search(sql.toString());
			
			if (this.frowset.next()) {
				str_value = Sql_switcher.readMemo(this.frowset, "str_value");
			}*/
			if(str_value.length()<3){
				str_value="USR";
			}else
				str_value=str_value.toUpperCase();
			if(str_value.lastIndexOf(',')!=-1)
				str_value=str_value.substring(0,str_value.length()-1);
			String sql = "select dbname,pre from dbname where upper(pre) in('"+str_value.replace(",", "','")+"')";
			this.frowset = dao.search(sql.toString());
			for(int i=0;this.frowset.next();){
				String pre = this.frowset.getString("pre");
				if(this.userView.hasTheDbName(pre)){
					sb.append("<span style=width: 125px; float: left;margin-top: 2px;><input type=checkbox name=sbase ");
					if(sbase.indexOf(pre)!=-1){
						sb.append("checked");
					}
					sb.append(" value="+this.frowset.getString("pre")+" />"+this.frowset.getString("dbname")+"</span>");
					i++;
					if(i%3==0)
						sb.append("<br/>");
					else{
						sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					}
				}
			}
			this.getFormHM().put("sbasehtml", sb.toString());
			
	  }
}
