package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class CandidateVindicateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String p0201="";
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			ContentDAO dao =new ContentDAO(this.getFrameconn());
			/**?,chenmengqing ?*/
			String p0209="";
			if(map!=null){
				p0201=(String)map.get("p0201");
				String str="select p0209 from p02 where p0201="+p0201;
				this.frowset=dao.search(str);
				if(this.frowset.next()){
					p0209=this.frowset.getString("p0209");
				}
			}
			
			ArrayList list = DataDictionary.getFieldList("P03",Constant.ALL_FIELD_SET);
			ArrayList candidateList = new ArrayList();
			for(int i=0;i<list.size();i++){
				FieldItem item =(FieldItem)list.get(i);
				Field field=(Field)item.cloneField();
			    if("p0201".equalsIgnoreCase(item.getItemid())|| "a0100".equalsIgnoreCase(item.getItemid())|| "nbase".equalsIgnoreCase(item.getItemid()))
			    	field.setVisible(false);
			    else
			    	field.setVisible(true);
			    if("05".equalsIgnoreCase(p0209)|| "06".equalsIgnoreCase(p0209))
			    {
			    	field.setReadonly(true);	    
			    }
			    else
			    {
				    if("a0101".equalsIgnoreCase(item.getItemid())|| "b0110".equalsIgnoreCase(item.getItemid())|| "e0122".equalsIgnoreCase(item.getItemid())|| "p0304".equalsIgnoreCase(item.getItemid()))
					      field.setReadonly(true);   
			    }
			    if("p0300".equalsIgnoreCase(item.getItemid()))
			    	field.setVisible(false);
				field.setSortable(true);
				if(!item.isVisible())
					field.setVisible(false);
				candidateList.add(field);
			
			}
			//过滤掉不需要的票数指标
			candidateList=getPrecs(dao,candidateList,p0201);
			ArrayList dbList = this.getDbList();
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<dbList.size();i++)
			{
				String pre=(String)dbList.get(i);
				if(i>0)
		    		buf.append(" union ");
				buf.append("select p.*,a.a0000,dbname.dbid from p03 p,"+pre+"A01 a,dbname where p0201='");
				buf.append(p0201);
				buf.append("'");
				buf.append(" and p.nbase='"+pre+"' and p.a0100=a.a0100 and p.nbase=dbname.pre");
				
			}
			
			Field newField = new Field("b", "显示卡片");
			newField.setReadonly(true);
			newField.setSortable(true);
			if(!"06".equalsIgnoreCase(p0209))
				newField.setVisible(false);
			candidateList.add(newField);
			
			StringBuffer sql=new StringBuffer();
			sql.append("select e.*,p0201 b from (");
			/*sql.append("(select p.*,a.a0000,dbname.dbid from p03 p,USRA01 a,dbname where p0201='");
			sql.append(p0201);
			sql.append("'");
			sql.append(" and p.nbase='usr' and p.a0100=a.a0100 and p.nbase=dbname.pre");
			sql.append(" union ");
			sql.append("select p.*,b.a0000,dbname.dbid from p03 p,RETA01 b,dbname where p0201='");
			sql.append(p0201);
			sql.append("'");
			sql.append(" and p.nbase='ret' and p.a0100=b.a0100 and p.nbase=dbname.pre");
			sql.append(" union ");
			sql.append("select p.*,c.a0000,dbname.dbid from p03 p,TRSA01 c,dbname where p0201='");
			sql.append(p0201);
			sql.append("'");
			sql.append(" and p.nbase='trs' and p.a0100=c.a0100 and p.nbase=dbname.pre ");
			sql.append(" union ");
			sql.append("select p.*,d.a0000,dbname.dbid from p03 p,OTHA01 d ,dbname where p0201='");
			sql.append(p0201);
			sql.append("'");
			sql.append(" and p.nbase='oth' and p.a0100=d.a0100 and p.nbase=dbname.pre");*/
			sql.append(buf.toString());
			sql.append(") e order by a0100");

			String tabname="p03";
			//System.out.println(tabname+"  "+sql.toString());
			this.getFormHM().put("tabname",tabname);
			this.getFormHM().put("sql",sql.toString());
			this.getFormHM().put("candidateList",candidateList);
			if("05".equals(p0209)|| "06".equals(p0209))
			{
			   this.getFormHM().put("state","1");
			}
			else if("01".equals(p0209)|| "09".equals(p0209))
			{
			   this.getFormHM().put("state","0");
			}
			this.getFormHM().put("whl_sql","where p0201="+p0201);
			this.getFormHM().put("p0201",p0201);
			if("05".equals(p0209))
			      this.getFormHM().put("have","1");
			else
				this.getFormHM().put("have","0");
			this.getFormHM().put("p0209",p0209);
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			this.getFormHM().put("privPre",bo.getPrivPre(this.userView.getPrivDbList()));
			/**自动编号*/
			autoNubmer(dao,p0201);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**自动编号*/
	private void autoNubmer(ContentDAO dao,String p0201) throws Exception{
		String xmlstr="";
		String autonum="";
		String sql="select extendattr from p02 where p0201="+p0201;
		this.frecset=dao.search(sql);
		if(this.frecset.next()){
			xmlstr=this.frecset.getString("extendattr");
		}
		if(xmlstr!=null&&xmlstr.length()>10){
			Document doc=DocumentHelper.parseText(xmlstr);
			Element root = doc.getRootElement();
			Element per_menu=root.element("personcode_menu");
			if(per_menu!=null)
				autonum=per_menu.getText();
		}
		this.getFormHM().put("autonum", autonum);
	}
	public String isHaveCandidate(String id){
		String have="no";
		String sql="select p0201 from p03 where p0201="+id;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				have="yes";
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return have;
	}
	public String getCommendName(String id){
		String name="";
		String sql="select p0203 from p02 where p0201="+id;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				name=this.frowset.getString("p0203");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
		
	}
	public ArrayList getDbList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql="select pre from dbname ";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				list.add(this.frowset.getString("pre"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**过滤新加统计票数指标是否显示*/
	public ArrayList getPrecs(ContentDAO dao,ArrayList candidateList,String p0201) throws Exception{
		ArrayList cList=new ArrayList();
		String extendattr="";
		this.frecset = dao.search("select extendattr from p02 where p0201="+p0201);
		if(this.frecset.next()){
			extendattr=this.frecset.getString("extendattr");
		}
		String bodys="";
		if(extendattr!=null&&extendattr.length()>10){
			Document doc=DocumentHelper.parseText(extendattr);
			Element root = doc.getRootElement();
			bodys=root.element("body_list").attributeValue("bodys");
		}
		
		String[] body_list=bodys.split(",");
		for (int i = 0; i < candidateList.size(); i++) {
			Field field=(Field)candidateList.get(i);
			if(field.getName().startsWith("c_")||field.getName().startsWith("C_")){
				field.setVisible(false);
				field.setReadonly(true);
				for (int j = 0; j < body_list.length; j++) {
					if(body_list[j]!=null&&body_list.length>0){
						String id="C_"+body_list[j].replaceAll("-", "X");
						if(id.equalsIgnoreCase(field.getName()))
							field.setVisible(true);
					}
				}
			}
			cList.add(field);
		}
		return cList;
	}
}
