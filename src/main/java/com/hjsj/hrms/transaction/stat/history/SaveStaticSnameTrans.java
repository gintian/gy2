package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.transaction.stat.SformulaXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveStaticSnameTrans  extends IBusiness{
	
	  public void execute() throws GeneralException 
	  {
		  String statid=(String)this.getFormHM().get("statid");		 
		  String opflag=(String)this.getFormHM().get("opflag");
		  String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		  if(opflag==null||opflag.length()<=0)
			  opflag="new";
		  String stat_name=(String)this.getFormHM().get("stat_name");
		  String stype=(String)this.getFormHM().get("stype");
		  if(stype==null||stype.length()<=0)
			  stype="0";
		  String categories = (String)this.getFormHM().get("categories");
		  // 35753 解密之前校验是否为null
		  if(StringUtils.isNotEmpty(categories)) {
			  categories = com.hrms.frame.codec.SafeCode.decode(categories);
			  categories = categories.trim();
		  }
		  String sformula = (String)this.getFormHM().get("sformula");
		  String type=(String)this.getFormHM().get("type");
		  String hv = (String)this.getFormHM().get("hv");
		  String sbase = (String)this.getFormHM().get("sbase");
		  String minvalue = (String)this.getFormHM().get("minvalue");
		  String maxvalue = (String)this.getFormHM().get("maxvalue");
		  String valve = (String)this.getFormHM().get("valve");
		  String valvetype = (String)this.getFormHM().get("valvetype");
		  if("save".equals(opflag)){
			  try{
				  sformula=com.hrms.frame.codec.SafeCode.decode(sformula);
				  //liuy 2014-12-23 6235：安徽高速：组织机构-单位管理-信息维护-统计分析-统计方式设置（设置完取不出来） start
				  sformula = PubFunc.keyWord_reback(sformula);
				  //liuy end
				  //liubq 2015-10-17 存储阀值 start
				  if(minvalue!=null&&maxvalue!=null&valve!=null)
					  saveValve(valve,statid,valvetype);
				  //liubq 2015-10-17 
				  String title = (String)this.getFormHM().get("title");
				  String id = (String)this.getFormHM().get("id");
				  String decimalwidth=(String)this.getFormHM().get("decimalwidth");
				  SformulaXml xml = new SformulaXml(this.frameconn,statid);
				  this.getFormHM().put("id",xml.setValue(id, title, sformula,type,decimalwidth));
				  ArrayList ids = (ArrayList)this.getFormHM().get("ids");
				  xml.orders(ids);
				  xml.saveStrValue();
				  this.getFormHM().put("opflag", "true");
			  }catch(Exception e){
				  this.getFormHM().put("opflag", "false");
				  e.printStackTrace();
			  }
		  }else if("delete".equals(opflag)){
			  try{
				  String id = (String)this.getFormHM().get("id");
				  SformulaXml xml = new SformulaXml(this.frameconn,statid);
				  xml.delValue(id);
				  xml.saveStrValue();
				  this.getFormHM().put("opflag", "true");
			  }catch(Exception e){
				  this.getFormHM().put("opflag", "false");
				  e.printStackTrace();
			  }
		  }else if ("get".equals(opflag)){
			  try{
				  SformulaXml xml = new SformulaXml(this.frameconn,statid);
				  String id = (String)this.getFormHM().get("id");
				  Element element = xml.getElement(id);
				  this.getFormHM().put("opflag", "true");
				  this.getFormHM().put("title", element.getAttributeValue("title"));
				  this.getFormHM().put("sformula", SafeCode.encode(element.getText()));
				  this.getFormHM().put("type", element.getAttributeValue("type"));
				  String tmpdecimalwidth=element.getAttributeValue("decimalwidth");
				  tmpdecimalwidth=(tmpdecimalwidth==null||tmpdecimalwidth.length()==0)?"2":tmpdecimalwidth;
				  this.getFormHM().put("decimalwidth", tmpdecimalwidth);
			  }catch(Exception e){
				  this.getFormHM().put("opflag", "false");
				  e.printStackTrace();
			  }
		  }else if("edit".equals(opflag))
		  {
			 
			  StringBuffer sql=new StringBuffer();
			  ArrayList paralist=new ArrayList();
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  String grouptype = (String)this.getFormHM().get("type_categories");//修改 统计条件分类 参数     
			  this.getFormHM().remove("type_categories");
			  String old_categories = (String)this.getFormHM().get("old_categories");//原 统计分类名称
			  old_categories = com.hrms.frame.codec.SafeCode.decode(old_categories);
			  old_categories = old_categories.trim();
			  if(grouptype != null && "categories".equalsIgnoreCase(grouptype)){//修改统计分类名称   wangb 20190705
				 try {
					sql.append("update hr_hisdata_sname set categories=? where categories=?");
					paralist.add(categories);
					paralist.add(old_categories);
					int i = dao.update(sql.toString(), paralist);
					if(i > 0 )
						this.getFormHM().put("opflag", "true");
					else
						this.getFormHM().put("opflag", "false");
				  } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.getFormHM().put("opflag", "false");
				  }
				  this.getFormHM().put("text", categories);
				  return;
			  }
			  if(statid==null||statid.length()<=0)
				  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			  sql.append("update hr_hisdata_sname set name='"+stat_name+"',stype="+stype+",categories='"+categories+"',sformula='"+sformula+"',hv='"+hv+"',sbase='"+sbase+"' where id='"+statid+"'");
			  try {
				dao.update(sql.toString());
				String uid=statid;
				this.getFormHM().put("uid", uid);
				this.getFormHM().put("text", stat_name);
				this.getFormHM().put("action", "");
				this.getFormHM().put("xml", "");
				this.getFormHM().put("opflag", "true");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.getFormHM().put("opflag", "false");
			}
		  }else
		  {
			    ArrayList paralist=new ArrayList();	
			    StringBuffer sql=new StringBuffer();
			    String id=getMaxId();
			    String snorder = this.getSnorder();
				sql.append("insert into hr_hisdata_sname(Id,Name,stype,sformula,type,InfoKind,categories,hv,sbase,snorder)values(?,?,?,?,?,?,?,?,?,?)");
				paralist.add(new Integer(id));
				paralist.add(stat_name);
				paralist.add(new Integer(stype));
				paralist.add(sformula);
				paralist.add(type);
				paralist.add(new Integer(infor_Flag));
				paralist.add(categories);
				paralist.add(hv);
				paralist.add(sbase);
				paralist.add(new Integer(snorder));
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				try
				{
					dao.insert(sql.toString(),paralist);
					StringBuffer action=new StringBuffer();	
					if("1".equals(type)){
						action.append("statshow.do?b_chart=chart&statid=" + id+"&type="+type);
					}else{
						action.append("statshow.do?b_doubledata=chart&statid=" + id+"&type="+type);
					}
					StringBuffer xml=new StringBuffer();
					xml.append("/com/workbench/stat/history/statitemtree?tablename=slegend&");	
					xml.append( "parentid="+id+"&target=mmil_body");
					String uid=id;
					this.getFormHM().put("uid", uid);
					this.getFormHM().put("text", stat_name);
					this.getFormHM().put("action", action.toString());
					this.getFormHM().put("xml", xml.toString());	
					this.getFormHM().put("opflag", "true");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.getFormHM().put("opflag", "false");
				}
		  }
	  }
	  private String getMaxId()throws GeneralException
		{
			int nid=1;
			StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from hr_hisdata_sname");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					nid=this.frowset.getInt("nmax");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
	   	       throw GeneralExceptionHandler.Handle(ex);			
			}
			return String.valueOf(nid);
		}
	  
	  private String getSnorder()throws GeneralException
		{
			int nid=1;
			StringBuffer sql=new StringBuffer("select max(snorder)+1 as nmax from hr_hisdata_sname");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					nid=this.frowset.getInt("nmax");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
	   	       throw GeneralExceptionHandler.Handle(ex);			
			}
			return String.valueOf(nid);
		}
	  
	  /**
		 * <?xml version="1.0" encoding="GB2312"?>
			<settings>
				<minValue>最小值公式</minValue>
				<maxValue>最大值公式</maxValue>
				<valves>
					<valve>阀值1公式</valve>
					<valve>阀值2公式</valve>
					<valve>阀值3公式</valve>
					<valve>阀值4公式</valve>
				</valves>
			</settings>
		 * 
		 * 
		 * */
		private void saveValve(String valve,String snameid,String valvetype){			
			StringBuffer temp_xml=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.frameconn);
			temp_xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
			temp_xml.append("<settings>");
			temp_xml.append("<minValue>");
			temp_xml.append("0");
			temp_xml.append("</minValue>");
			temp_xml.append("<maxValue>");
			temp_xml.append("1.5");
			temp_xml.append("</maxValue>");
			temp_xml.append("<valves>");

				temp_xml.append("<valve>");
				temp_xml.append(valve);
				temp_xml.append("</valve>");
				temp_xml.append("<valve>");
				temp_xml.append("1");
				temp_xml.append("</valve>");
				temp_xml.append("<valve>");
				temp_xml.append("1.1");
				temp_xml.append("</valve>");

			temp_xml.append("</valves>");
			temp_xml.append("</settings>");
			try{
				dao.update("update slegend set valve='"+temp_xml.toString()+"' where id="+snameid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

}
