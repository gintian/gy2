package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title:InitInPreferencesTrans.java</p>
 * <p>Description>:InitInPreferencesTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 17, 2010 10:06:12 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SavePreferencesTrans extends IBusiness {
	public void execute() throws GeneralException {
		String p0201=(String) this.getFormHM().get("p0201");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			String extendattr=extXMLPreferences(dao,p0201);
			String sql="update p02 set extendattr='"+extendattr+"' where p0201="+p0201;
			dao.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//组装xml
	public String extXMLPreferences(ContentDAO dao,String p0201) throws Exception{
		String bodys=(String) this.getFormHM().get("bodys");
		bodys=bodys!=null&&bodys.length()>0?bodys.substring(0,bodys.length()-1):"";
		String codes=(String) this.getFormHM().get("codes");
		codes=codes!=null&&codes.length()>0?codes.substring(0,codes.length()-1):"";
		String counts=(String) this.getFormHM().get("counts");
		counts=counts!=null&&counts.length()>0?counts.substring(0,counts.length()-1):"";
		String dbpres=(String) this.getFormHM().get("dbpres");
		dbpres=dbpres!=null&&dbpres.length()>0?dbpres.substring(0,dbpres.length()-1):"";
		String footer=(String)this.getFormHM().get("footer");
		footer=footer!=null&&footer.length()>0?SafeCode.decode(footer):"";
		//构建指标
		addField(dao,bodys);
		
		String xmlstr="";
		String sql="select extendattr from p02 where p0201="+p0201;
		this.frecset=dao.search(sql);
		if(this.frecset.next()){
			xmlstr=this.frecset.getString("extendattr");
		}
		
		Document doc=null;
		if(xmlstr==null||xmlstr.length()<20)
			doc=DocumentHelper.createDocument();
		else
			doc=DocumentHelper.parseText(xmlstr);
		
		Element root = doc.getRootElement();
		if(root==null)
			root=doc.addElement("params");
		
		Element body_list=root.element("body_list");
		if(body_list==null)
			body_list=root.addElement("body_list");
		body_list.addAttribute("bodys", bodys);
		
		Element pos_list=root.element("pos_list");
		if(pos_list==null)
			pos_list=root.addElement("pos_list");
		pos_list.addAttribute("codes", codes);
		pos_list.addAttribute("counts", counts);
		
		Element nbase=root.element("nbase");
		if(nbase==null)
			nbase=root.addElement("nbase");
		nbase.setText(dbpres);
		
		Element paper_footer=root.element("paper_footer");
		if(paper_footer==null)
			paper_footer=root.addElement("paper_footer");
		paper_footer.setText(footer);
		//System.out.println(doc.asXML());
		return doc.asXML();
	}
	
	/**构建指标*/
	private void addField(ContentDAO dao,String bodys) throws SQLException{
		DbWizard db=new DbWizard(this.getFrameconn());
		String[] body_list=bodys.split(",");
		for (int i = 0; i < body_list.length; i++) {
			if(body_list[i]!=null&&body_list.length>0){
				String clm="C_"+body_list[i].replaceAll("-", "X");
				if(!db.isExistField("p03", clm,false)){
					dao.update("alter table p03 add "+clm+" int");
					this.frecset=dao.search("select itemid from t_hr_busifield where fieldsetid='P03' and itemid='"+clm+"'");
					if(!this.frecset.next()){
						try(
							ResultSet rs=dao.search("select name from per_mainbodyset where body_id="+body_list[i]);
						) {
							if (rs.next()) {
								String sql0 = "insert into t_hr_busifield(fieldsetid, itemid, displayid, itemtype, itemdesc,itemlength, decimalwidth, codesetid, displaywidth,state,useflag, keyflag, codeflag,ownflag) values(";
								sql0 += "'P03','" + clm + "',1" + i + ",'N','" + rs.getString("name") + "票数" + "',4,0,0,10,1,1,0,0,1)";
								dao.update(sql0);
								FieldItem fi = new FieldItem("P03", clm);
								fi.setItemdesc(rs.getString("name") + "票数");
								fi.setItemtype("N");
								DataDictionary.addFieldItem("P03", fi, 0);
							}
						}
					}
				}
			}
		}
	}
	
}
