package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:InitInPreferencesTrans.java</p>
 * <p>Description>:InitInPreferencesTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 17, 2010 10:06:12 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class InitInPreferencesTrans extends IBusiness {
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList dbprelist=null;
		ArrayList preslist=null;
		ArrayList codeslist=null;
		try {
			/**获取人员库列表*/
			dbprelist=getDbpreList(dao);
			/**获取投票人类别列表*/
			preslist=searchCheckBodyObjectList(dao,"0");
			/**获取推荐职务列表*/
			codeslist=searchCommendFieldList(dao);
			/**获取保存状态列表*/
			searchCheckList(dao);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("dbprelist", dbprelist);
		this.getFormHM().put("preslist", preslist);
		this.getFormHM().put("codeslist", codeslist);
	}
	
	/**获取人员库列表*/
	private ArrayList getDbpreList(ContentDAO dao) throws SQLException {
		ArrayList dbList=new ArrayList();
		String sql="select pre,dbname from dbname order by dbid";
		this.frecset=dao.search(sql);
		while(this.frecset.next()){
			CommonData obj=new CommonData();
			String pre=this.frecset.getString("pre");
			pre=pre==null?"":pre.toUpperCase();//cs人员库取值是大写
			obj.setDataName(pre);
			obj.setDataValue(this.frecset.getString("dbname"));
			dbList.add(obj);
		}
		return dbList;
	}

	/**
	 * 获取推荐职务列表
	 * 常量表constant='RM_Options' 中 commend_field=职务代码指标
	 * @throws SQLException
	 */
	public ArrayList searchCommendFieldList(ContentDAO dao) throws SQLException{
		ArrayList cfList=new ArrayList();
		String sql="select str_value from constant where constant ='RM_Options'";
		String commend_field="";
		String c_field="";
		this.frecset=dao.search(sql);
		if(this.frecset.next()){
			commend_field=this.frecset.getString("str_value");
		}
		commend_field=commend_field==null?"":commend_field;
		String[] cfs=commend_field.split("\r\n");
		for (int i = 0; i < cfs.length; i++) {
			String[] sfv=cfs[i].split("=");
			if(sfv.length>1&&"commend_field".equalsIgnoreCase(sfv[0]))
				c_field=sfv[1];
		}
		
		if(c_field!=null&&c_field.length()>0){
			FieldItem fi=DataDictionary.getFieldItem(c_field);
			if("@K".equalsIgnoreCase(fi.getCodesetid())||"UN".equalsIgnoreCase(fi.getCodesetid())||"UM".equalsIgnoreCase(fi.getCodesetid()))
				sql="select * from organization where codesetid='"+fi.getCodesetid()+"'";
			else
				sql="select * from codeitem where codesetid='"+fi.getCodesetid()+"'";
			this.frecset=dao.search(sql);
			while(this.frecset.next()){
				CommonData obj=new CommonData();
				obj.setDataName(this.frecset.getString("codeitemid"));
				obj.setDataValue(this.frecset.getString("codeitemdesc"));
				cfList.add(obj);
			}
		}
		return cfList;
	}
	/**获取投票人类别列表*/
	public ArrayList searchCheckBodyObjectList(ContentDAO dao,String body_type)
			throws GeneralException, SQLException {
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append(" select body_id,name from per_mainbodyset ");
		buf.append(" where "+Sql_switcher.isnull("body_type", "'0'"));
		buf.append(" = "+body_type);
		buf.append(" and body_id not in (-1,5) order by  seq ");
		this.frowset = dao.search(buf.toString());
		while (this.frowset.next()) {
			RecordVo vo = new RecordVo("per_mainbodyset");
			vo.setString("body_id", this.frowset.getString("body_id"));
			vo.setString("name", this.frowset.getString("name"));
			list.add(vo);
		}
		return list;
	}
	
	/**对应的保存值列表*/
	public void searchCheckList(ContentDAO dao)
			throws GeneralException, SQLException, DocumentException {
		String bodys="",codes="",counts="",dbpres="",footer="";
		String p0201 = (String)this.getFormHM().get("p0201");
		String extendattr="";
		String disabled="";
		this.frecset = dao.search("select extendattr,p0209 from p02 where p0201="+p0201);
		if(this.frecset.next()){
			extendattr=this.frecset.getString("extendattr");
			disabled=this.frecset.getString("p0209");
			disabled=disabled!=null&&("01".equals(disabled)||"09".equals(disabled))?"":"disabled";
		}
		//System.out.println(extendattr);
		if(extendattr!=null&&extendattr.length()>10){
			Document doc=DocumentHelper.parseText(extendattr);
			Element root = doc.getRootElement();
			bodys=root.element("body_list").attributeValue("bodys");
			Element pos_list = root.element("pos_list");
			codes = pos_list.attributeValue("codes");
			counts = pos_list.attributeValue("counts");
			dbpres = root.elementText("nbase");
			dbpres=dbpres==null?"":dbpres.toUpperCase();//cs人员库取值是大写
			footer = root.elementText("paper_footer");
		}
		
		//投票人类别
		List body_list=new ArrayList();
		if(bodys!=null&&bodys.length()>0){
			String[] bds=bodys.split(",");
			for (int i = 0; i < bds.length; i++) {
				if(bds[i]!=null&&bds[i].length()>0){
					body_list.add(bds[i]);
				}
			}
		}
		
		//推荐职务及人数
		List pos_list=new ArrayList();
		if(codes!=null&&codes.length()>0){
			String[] cds=codes.split(",");
			String[] cts=counts.split(",");
			String[] plist=null;
			for (int i = 0; i < cds.length; i++) {
				if(cds[i]!=null&&cds[i].length()>0){
					plist=new String[3];
					plist[0]=cds[i];
					plist[1]="checked";
					if(cts.length>i)
						plist[2]=cts[i];
					else
						plist[2]="";
					pos_list.add(plist);
				}
			}
		}
		
		//人员库
		List dbpre_list=new ArrayList();
		if(dbpres!=null&&dbpres.length()>0){
			String[] dbs=dbpres.split(",");
			for (int i = 0; i < dbs.length; i++) {
				if(dbs[i]!=null&&dbs[i].length()>0){
					dbpre_list.add(dbs[i]);
				}
			}
		}
		this.getFormHM().put("disabled", disabled);
		this.getFormHM().put("dbpre_list", dbpre_list);
		this.getFormHM().put("body_list", body_list);
		this.getFormHM().put("pos_list", pos_list);
		this.getFormHM().put("footer", footer);
	}
}
