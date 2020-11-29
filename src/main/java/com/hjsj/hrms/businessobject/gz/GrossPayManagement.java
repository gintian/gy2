package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.interfaces.gz.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 *<p>Title:</p> 
 *<p>Description:对常量表constant的字段GZ_PARAMde操作</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class GrossPayManagement extends ConstantXml{
	private String formular;
	
	public GrossPayManagement(Connection conn, String constant) {
		super(conn, constant);
		// TODO Auto-generated constructor stub
	}
	public GrossPayManagement(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取按月变化子集
	 * @return list 按月变化子集的集合
	 * @throws GeneralException
	 */
	public ArrayList fieldsetList(){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		String strsql = "select fieldsetid,customdesc from fieldset where changeflag=1 and useflag=1 and fieldsetid like 'B%'";
		
		ArrayList dylist = null;
		list.add(new CommonData(" ","  "));
		try {
			dylist = dao.searchDynaList(strsql);
			int n=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("fieldsetid").toString(),dynabean.get("customdesc").toString());
				list.add(obj);
				n++;
			}
			/*if(n==0){
				CommonData obj=new CommonData("","");
				list.add(obj);
			}*/
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取按月变化子集中类型为数值型的子标
	 * @return list 按月变化子集的子标集合
	 * @throws GeneralException
	 */
	public ArrayList fielditemList(String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
		strsql.append(fieldsetid);
		strsql.append("' and useflag=1 and itemtype='N' and itemdesc<>'次数' order by displayid");
		
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(strsql.toString());
			int n=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				list.add(obj);
				n++;
			}
			if(n==0){
				CommonData obj=new CommonData("","");
				list.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取按月变化子集中类型为字符型的子标
	 * @return list 按月变化子集的子标集合
	 * @throws GeneralException
	 */
	public ArrayList spFlagList(String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
		strsql.append(fieldsetid);
		strsql.append("' and useflag=1 and itemtype='A' and codesetid='23' and itemlength>1");
		
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(strsql.toString());
			int n=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				list.add(obj);
				n++;
			}
			if(n==0){
				CommonData obj=new CommonData("","");
				list.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取按月变化子集的子标
	 * @return list 按月变化子集的子标集合
	 * @throws GeneralException
	 */
	public ArrayList fielditemList(String fieldsetid,String amount,String remain){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
		strsql.append(fieldsetid);
		strsql.append("' and useflag=1 and itemtype='N' and itemdesc<>'次数'");
		
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(strsql.toString());
			int n=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj = new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				list.add(obj);
				n++;
			}
			if(n==0){
				CommonData obj=new CommonData("","");
				list.add(obj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	//---------------------------------------------------
	/**
	 * 
	 * @param itemlist
	 * @return
	 */
	public String getSelectString(ArrayList itemlist)
	{
	  StringBuffer buf = new StringBuffer();
	  try
	  {
		 CommonData item = null;
		 buf.append("<select id=\"selectid\" style=\"width:130;\" name=\"selectid\" onclick=\"onLeave('#');\">");
		 for(int i=0;i<itemlist.size();i++)
		 {
			 item=(CommonData)itemlist.get(i);
			 buf.append("<option value=\""+(String)item.getDataValue()+"\">");
			 buf.append(item.getDataName());
			 buf.append("</option>");
		 }
		 buf.append("</select>");
	}
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return buf.toString();
		
	}
	private String salarySet="";
	//------------------------------
	public String getArrayString(ArrayList dataList)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer buff = new StringBuffer("");
			StringBuffer salary=new StringBuffer("");
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
				String planitem = (String)bean.get("planitem");
				String planitemdesc = (String)bean.get("planitemdesc");
				String realitem = (String)bean.get("realitem");
				String realitemdesc = (String)bean.get("realitemdesc");
				String balanceitem = (String)bean.get("balanceitem");
				String balanceitemdesc=(String)bean.get("balanceitemdesc");
				String flag=(String)bean.get("flag");
				String formular = (String)bean.get("formular");
				String salaryid=(String)bean.get("salaryid");
				String classname=(String)bean.get("classname");
				buf.append(planitem+"#"+planitemdesc+"#"+realitem+"#"+realitemdesc+"#"+balanceitem+"#"+balanceitemdesc+"#"+classname+"#"+flag);
				buf.append("`");
				buff.append(planitem+"#"+SafeCode.encode(formular));
				buff.append("`");
				salary.append(planitem+"#"+SafeCode.encode(salaryid));
				salary.append("`");
			}
			if(buf.length()>1)
			    buf.setLength(buf.length()-1);
			if(buff.length()>1)
				buff.setLength(buff.length()-1);
			if(salary.length()>1)
				salary.setLength(salary.length()-1);
			this.formular = buff.toString();
			this.salarySet=salary.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public String getFormularStr()
	{
		return this.formular;
	}
	/**
	 * 获取按月变化子集类型为数值型的子标
	 * @return list 按月变化子集类型为数值型的子标集合
	 * @throws GeneralException
	 */
	public ArrayList nList(String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select itemid,itemdesc from fielditem where fieldsetid='");
		strsql.append(fieldsetid);
		strsql.append("' and useflag=1 and itemtype='N' and itemdesc<>'次数'");

		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(strsql.toString());
			int n=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				list.add(obj);
				n++;
			}
			if(n==0){
				CommonData obj=new CommonData("","");
				list.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 增加参数设置
	* @param fieldsetid 子集id
	 * @param fielditemid 指标id
	 * @param nid 指标id
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String addParams(String fieldsetid,String fielditemid,String nid,String sp_flag){
		String  main = "";
		Element params = new Element("Params");
		
		Element gz_amount = new Element("Gz_amount");
				gz_amount.setAttribute("setid",fieldsetid);
				gz_amount.setAttribute("amount",fielditemid);
				gz_amount.setAttribute("remain",nid);
				gz_amount.setAttribute("sp_flag",sp_flag);
		ArrayList list = fielditemList(fieldsetid);
		for(int i=0;i<list.size();i++){
			CommonData obj = (CommonData)list.get(i);
			if(obj.getDataValue().length()>1){
				Element relation  = new Element("relation");
				relation.setAttribute("src",obj.getDataValue());
				relation.setAttribute("dest"," ");
				gz_amount.addContent(relation);
			}
		}
				
	   params.addContent(gz_amount);
						
		Document myDocument = new Document(params);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		main= outputter.outputString(myDocument);
		return main;
	}
	
	/**
	 * 增加参数设置
	 * @param fieldsetid 子集id
	 * @param fielditemid 指标id
	 * @param nid 指标id
	 * @return String xml格式形式字符串
	 * @throws GeneralException,JDOMException
	 */
	public String addGz_amount(String fieldsetid,String fielditemid,String nid,String sp_flag){
		String  main = "";
		init();
		try {
				XPath xPath = XPath.newInstance("/Params");
				List list=xPath.selectNodes(doc);
				Element params = null;
				for(Iterator t=list.iterator();t.hasNext();){
					params = (Element)t.next();
					Element gz_amount  = new Element("Gz_amount");
					gz_amount.setAttribute("setid",fieldsetid);
					gz_amount.setAttribute("amount",fielditemid);
					gz_amount.setAttribute("remain",nid);
					gz_amount.setAttribute("sp_flag",sp_flag);
					ArrayList setlist = fielditemList(fieldsetid);
					for(int i=0;i<setlist.size();i++){
						CommonData obj = (CommonData)setlist.get(i);
						if(obj.getDataValue().length()>1){
							Element relation  = new Element("relation");
							relation.setAttribute("src",obj.getDataValue());
							relation.setAttribute("dest"," ");
							gz_amount.addContent(relation);
						}
					}
					params.addContent(gz_amount);
				}

				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				main = outputter.outputString(doc);
				
		}catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return main;
	}
	
	/**
	 * 修改参数设置
	 * @param fieldsetid 子集id
	 * @param fielditemid 指标id
	 * @param nid 指标id
	 * @return boolean 判断是否修改成功 true成功,false不成功
	 * @throws GeneralException,JDOMException
	 */
	public boolean alertGzAmount(String fieldsetid,String fielditemid,String nid,String sp_flag){
		boolean judgment = false;
		String  main = "";
		if(xml.length()>1){
			try {
				init();
				XPath xPath = XPath.newInstance("/Params/Gz_amount");
				List list=xPath.selectNodes(this.doc);
				Element gz_amount = null;
				if(list.size()>0){
					for(Iterator t=list.iterator();t.hasNext();){
						gz_amount =(Element)t.next();
						gz_amount.removeAttribute("setid");
						gz_amount.removeAttribute("amount");
						gz_amount.removeAttribute("remain");
						gz_amount.removeAttribute("sp_flag");
						
						gz_amount.setAttribute("setid",fieldsetid);
						gz_amount.setAttribute("amount",fielditemid);
						gz_amount.setAttribute("remain",nid);
						gz_amount.setAttribute("sp_flag",sp_flag);
					}
					gz_amount.removeContent();
					ArrayList setlist = fielditemList(fieldsetid,fielditemid,nid);
					for(int i=0;i<setlist.size();i++){
						CommonData obj = (CommonData)setlist.get(i);
						if(obj.getDataValue().length()>1){
							Element relation  = new Element("relation");
							relation.setAttribute("src",obj.getDataValue());
							relation.setAttribute("dest"," ");
							gz_amount.addContent(relation);
						}
					}
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					main = outputter.outputString(this.doc);
				}else{
					main = addGz_amount(fieldsetid,fielditemid,nid,sp_flag);
				}
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			main = addParams(fieldsetid,fielditemid,nid,sp_flag);
		}
		judgment = alertXML(main);
		return judgment;
	}
	
	/**
	 * 修改指标参数设置
	 * @param fielditemid 指标id
	 * @param formula 公式
	 * @return boolean 判断是否修改成功 true成功,false不成功
	 * @throws JDOMException
	 */
	public boolean alertRelation(String fielditemid,String formula){
		boolean judgment = false;
		String  main = "";
		
		try {
			init();
			XPath xPath = XPath.newInstance("/Params/Gz_amount/relation");
			List list=xPath.selectNodes(this.doc);
			Element relation = null;
			for(Iterator t=list.iterator();t.hasNext();){
				relation =(Element)t.next();
				if(relation.getAttributeValue("src").equals(fielditemid)){
					relation.removeAttribute("dest");
						
					relation.setAttribute("dest",formula);
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			main = outputter.outputString(this.doc);
				
		}catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		judgment = alertXML(main);
		return judgment;
	}
	/**
	 * 生成薪资总额与薪资项目关系table
	 * @return String 
	 * @throws JDOMException
	 */
	public String viewRelationTable(){
		StringBuffer tableview = new StringBuffer();
		try{
			init();
			XPath xPath = XPath.newInstance("/Params/Gz_amount/relation");
			if(this.doc!=null){
				List list=xPath.selectNodes(this.doc);
				Element relation = null;
				if(list.size()>0){
					for(Iterator it=list.iterator();it.hasNext();){
						relation =(Element)it.next();
						String src = relation.getAttributeValue("src");
						FieldItem fielditem = DataDictionary.getFieldItem(src);
					  
						tableview.append("<tr><td class='RecordRow' nowrap>");
						tableview.append(fielditem.getItemdesc());
						tableview.append("</td><td class='RecordRow' nowrap>");
						tableview.append(relation.getAttributeValue("dest"));
						tableview.append("&nbsp;</td><td align='center' class='RecordRow' nowrap>");
						tableview.append("<a href='#' onclick='exebolishsubmit(\""+src+"\");'><img src='/images/edit.gif' border='0'></a>");
						tableview.append("</td></tr>");
					}
				}
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tableview.toString();
	}
	/**
	 * 获取公式
	 * @param fielditemid 指标id
	 * @return String 公式值
	 * @throws JDOMException
	 */
	public String getFormula(String fielditemid){
		String formula = "";
		try {
			init();
			XPath xPath = XPath.newInstance("/Params/Gz_amount/relation");
			List list=xPath.selectNodes(this.doc);
			Element relation = null;
			for(Iterator t=list.iterator();t.hasNext();){
				relation =(Element)t.next();
				if(relation.getAttributeValue("src").equals(fielditemid)){
					formula = relation.getAttributeValue("dest");
				}
			}		
		}catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return formula;
	}
	public ArrayList getContrlLevelList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("",""));
		for(int i=1;i<=5;i++)
		{
			list.add(new CommonData(i+"",i+""));
		}
		return list;
	}
	public ArrayList getOrgOrDeptListFromSalaryset(String codesetid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" select  distinct s.itemid,s.itemdesc from salaryset s,fielditem f where s.itemid = f.itemid and upper(f.itemtype)='A' and upper(f.codesetid)='"+codesetid.toUpperCase()+"' and upper(s.itemid) not in('E0122','B0110')");
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			list.add(new CommonData("","  "));
			rs = dao.search(sb.toString());
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getOrgOrDeptListFromSalaryset(String codesetid,String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" select  distinct s.itemid,s.itemdesc from salaryset s,fielditem f where s.itemid = f.itemid and upper(f.itemtype)='A' and upper(f.codesetid)='"+codesetid.toUpperCase()+"' and upper(s.itemid) not in('E0122','B0110') and salaryid="+salaryid);
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			list.add(new CommonData("","  "));
			rs = dao.search(sb.toString());
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getAmountPlanitemDescFieldList(String setid)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			list.add(new CommonData("","   "));
			if(setid==null|| "".equals(setid))
				return list;
			ContentDAO dao = new ContentDAO(this.conn);
			rs= dao.search("select itemid,itemdesc from fielditem where UPPER(fieldsetid)='"+setid.toUpperCase()+"' and useflag='1' and UPPER(itemtype)='A'");
			while(rs.next())
			{
				list.add(new CommonData(rs.getString(1),rs.getString(2)));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 取得封存状态指标列表
	 * @param setid
	 * @return
	 */
	public ArrayList getFc_flag_list(String setid)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search("select itemid,itemdesc from fielditem where UPPER(fieldsetid)='"+setid.toUpperCase()+"' and useflag='1' and codesetid='45'");
			list.add(new CommonData("","  "));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 判断下级部门和上级部门的值
	 * @param vo
	 * @param dao
	 * @param map
	 * @param changeList
	 * @return
	 */
	public String upValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type) {
		StringBuffer b0110buf = new StringBuffer("");
		for(int i=0;i<changeList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
			String b0 = (String)bean.get("b0110");
			b0110buf.append("'");
			b0110buf.append(b0);
			b0110buf.append("',");
		}
		if(b0110buf.toString().length()>0)
			b0110buf.setLength(b0110buf.length()-1);
		StringBuffer info = new StringBuffer(); 
		String setid=vo.getModelName()+"z0"; 
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer sqlgroup = new StringBuffer(" group by ");
		StringBuffer wheresql = new StringBuffer(" where b0110 = ");
		wheresql.append("(select parentid from organization where codeitemid = '");
		wheresql.append(vo.getString("b0110"));
		wheresql.append("' )");
		wheresql.append(" and ");
	    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    if("1".equals(ctrl_peroid))
	    {
	    }	
	    else if("2".equals(ctrl_peroid))
	    {
	    	String season = vo.getString("season");
	    	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
	    	 wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    }
	    else
     	    wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
	    //将总额参数设置的项目、公式设置取出其指标
		ArrayList list = voList(vo,map);
		//对这些指标进行sum和groupby的条件
		for(int i=0;i<list.size();i++){
			/*if(i==0)
			{
				wheresql.append(" group by ");
			}
			wheresql.append(list.get(i));*/
			/*if(i!=list.size()-1)
			{
				wheresql.append(",");
			}	*/	
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			sqlstr.append(",");
			sqlgroup.append(list.get(i));
			sqlgroup.append(",");
		}
		sqlgroup.setLength(sqlgroup.length()-1);
		//由于后面将group by去掉了，这里不需要max
		sqlstr.append("(select parentid from organization where codeitemid = '");
		sqlstr.append(vo.getString("b0110"));
		sqlstr.append("') as parentid ");
				
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		//由于该方法只是进行总额的比较，不涉及其他处理，所以这里不需要进行分组，分组的时候可能出现查出两条数据甚至多条等，这样其总额的数值就不对，sqlValue取得是是查出来的第一条数据
		//sqlstr.append(sqlgroup);
		try {
			/**父级单位或部门的总额*/
			RowSet rs = dao.search(sqlstr.toString());
			/**同级单位或部门的总和，与上面的总额比较*/
			String strsql = undersql(ctrl_peroid,vo,dao,vo.getString("b0110"),map,ctrl_type);
			StringBuffer b = new StringBuffer();
			b.append(strsql);
			 if(b0110buf.toString().length()>0)
			 {
				  b.append("  and  b0110 not in(");
				  b.append(b0110buf);
				  b.append(")");
			 }
			 //该查询算出所有不是选中节点的总额
			RowSet rs1 = dao.search(b.toString());
			double voValue = 0;
			//原先在里面进行if，但是如果是一条数据多次.next()，会报光标位置无效的错误
			boolean flag = false;
			if(rs1.next() == true)
				flag = true;
			//if(rs.next()&&rs1.next()),这个rs1取得就是除了所选的和非父级节点的所有机构的值
			if(rs.next()&&flag&&rs.getString("parentid")!=null){
				if(!rs.getString("parentid").equals(vo.getString("b0110"))){
					for(int i=0;i<list.size();i++){
						/**父部门的总额如果改变取改变后的，如果未改变，从数据库中取*/
						String dd=(String)list.get(i);
						double sqlValue = rs.getDouble(dd);
						double value=0;
						for(int j=0;j<changeList.size();j++)
						{
							LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
							String b0 = (String)bean.get("b0110");
							String itemid = (String)bean.get("itemid");
							//所选的包含parentid的直接去这个parentid的值
							if(rs.getString("parentid").equalsIgnoreCase(b0)&&itemid.equalsIgnoreCase(dd))
							{
								String tt=(String)bean.get(itemid.toLowerCase());
								if(tt==null|| "".equals(tt))
									tt="0";
								sqlValue=Double.parseDouble(tt);
							}
							//必须是parentid的开头&&不是parentid&&并且跟所选的机构同级
							if(b0.startsWith(rs.getString("parentid"))&&!b0.equals(rs.getString("parentid"))&&b0.length()==vo.getString("b0110").length())
							{
								 String yearmonth =(String)bean.get("year");
								 String ayear =yearmonth.substring(0,4);
								 String amonth=yearmonth.substring(5);
							     if(itemid.equalsIgnoreCase(dd)&&amonth.equals(month))
							     {
							    	
							    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
							     }
							}
						}
						//得到所有的总额所选的和未选得
						if(flag == true)
							voValue = rs1.getDouble(dd)+value/*+rs1.getDouble((String)list.get(i))*/;
						else
							voValue = value;
						//统计单位的所有的值和上级单位的值进行比较
						if(sqlValue<voValue){
							FieldItem fielditem = DataDictionary.getFieldItem(dd);
							String codeitem = "";
							String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}else{
								desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
								if(desc!=null&&desc.trim().length()>0){
									codeitem = desc;
								}
							}
							//ghgg
							String exception = "";
							if("0".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
							}
							else if("1".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							else
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							info.append(exception+",");
							info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.max.value")+"\n");
						}
					}
				}
			}
			else
			{
			   String desc=AdminCode.getCodeName("UN",vo.getString("b0110"));
			   if(desc==null|| "".equals(desc))
			   {
				   desc = AdminCode.getCodeName("UM", vo.getString("b0110"));
			   }
			   info.append(desc+ResourceFactory.getProperty("gz.acount.noparent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	/**
	 * 获取当前表计划总额字段名
	 * @param vo  
	 * @return ArrayList 
	 **/
	private ArrayList voList(RecordVo vo,HashMap map) {
		ArrayList volist = new ArrayList();
		ArrayList list = vo.getModelAttrs();
		String table = vo.getModelName();
		for(int i=0;i<list.size();i++){
			String itemid = (String)list.get(i);
			if(map.get(itemid.toLowerCase())!=null)
			{
	    		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
	    		if(fielditem!=null){
	    			if(fielditem.getFieldsetid().toLowerCase().equalsIgnoreCase(table)){
		    			if("N".equals(fielditem.getItemtype())){
		    				String aa = itemid.substring(itemid.length()-2,itemid.length()).toLowerCase();
		    				if(!"z1".equals(aa)){
		    					volist.add(list.get(i));
	    					}
	    				}
    				}
    			}
			}
			
    	}
		return volist;
	}
	public String getSeasonCondation(int season)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(season+","+(season+1)+","+(season+2));     
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 获取查询本部门下一级部门的sql语句
	 * @param vo  
	 * @return String 
	 **/
	private String undersql(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type) {
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String season = vo.getString("season");
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	private String undersql1(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type,String aaaa,String season) {
		String z0=aaaa;
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	public String upValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag) {
		StringBuffer b0110buf = new StringBuffer("");
		for(int i=0;i<changeList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
			String b0 = (String)bean.get("b0110");
			b0110buf.append("'");
			b0110buf.append(b0);
			b0110buf.append("',");
		}
		if(b0110buf.toString().length()>0)
			b0110buf.setLength(b0110buf.length()-1);
		StringBuffer info = new StringBuffer(); 
		String setid=vo.getModelName()+"z0"; 
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer sqlgroup = new StringBuffer(" group by ");
		StringBuffer wheresql = new StringBuffer(" where b0110 = ");
		wheresql.append("(select parentid from organization where codeitemid = '");
		wheresql.append(vo.getString("b0110"));
		wheresql.append("' )");
		wheresql.append(" and ");
	    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    if("1".equals(ctrl_peroid))
	    {
	    }	
	    else if("2".equals(ctrl_peroid))
	    {
	    	String season = vo.getString("season");
	    	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
	    	 wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    }
	    else
     	    wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
	    if(fc_flag!=null&&fc_flag.length()!=0){
	    	wheresql.append(" and ");
	    	wheresql.append(vo.getModelName()+"z1");
	    	wheresql.append("=");
	    	wheresql.append(vo.getString(vo.getModelName()+"z1"));
	    }
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			/*if(i==0)
			{
				wheresql.append(" group by ");
			}
			wheresql.append(list.get(i));*/
			/*if(i!=list.size()-1)
			{
				wheresql.append(",");
			}	*/	
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			sqlstr.append(",");
			sqlgroup.append(list.get(i));
			sqlgroup.append(",");
		}
		sqlgroup.setLength(sqlgroup.length()-1);
		
		/* 薪资总额-报批-后台sql报错 xiaoyun 2014-10-23 start */
		/*sqlstr.append("(select parentid from organization where codeitemid = '");
		sqlstr.append(vo.getString("b0110"));
		sqlstr.append("') as parentid ");*/
		sqlstr.append(" b0110 ");
		/* 薪资总额-报批-后台sql报错 xiaoyun 2014-10-23 end */
				
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
//		sqlstr.append(sqlgroup);//按年保存  下级单位和小于等于上级单位  这样写sql，每月不平均分就出现多条，那么就会比实际值小  比较的就不准确了   zhaoxg  2014-5-5
		/* 薪资总额-报批-后台sql报错 xiaoyun 2014-10-23 start */
		sqlstr.append(" group by b0110 ");
		/* 薪资总额-报批-后台sql报错 xiaoyun 2014-10-23 end */
		try {
			/**父级单位或部门的总额*/
			RowSet rs = dao.search(sqlstr.toString());
			/**同级单位或部门的总和，与上面的总额比较*/
			String strsql ="";
			if(fc_flag!=null&&fc_flag.length()!=0){
				strsql =this.undersql(ctrl_peroid, vo, dao, vo.getString("b0110"), map, ctrl_type, fc_flag);
			}else{
				strsql =undersql(ctrl_peroid,vo,dao,vo.getString("b0110"),map,ctrl_type);
			}
			StringBuffer b = new StringBuffer();
			b.append(strsql);
			 if(b0110buf.toString().length()>0)
			 {
				  b.append("  and  b0110 not in(");
				  b.append(b0110buf);
				  b.append(")");
			 }
			RowSet rs1 = dao.search(b.toString());
			if(rs.next()&&rs1.next()&&rs.getString("b0110")!=null){
				if(!rs.getString("b0110").equals(vo.getString("b0110"))){ //薪资总额-报批-后台sql报错 xiaoyun 2014-10-23
					for(int i=0;i<list.size();i++){
						/**父部门的总额如果改变取改变后的，如果未改变，从数据库中取*/
						String dd=(String)list.get(i);
						double sqlValue = rs.getDouble(dd);
						double value=0;
						for(int j=0;j<changeList.size();j++)
						{
							LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
							String b0 = (String)bean.get("b0110");
							String itemid = (String)bean.get("itemid");
							if(rs.getString("b0110").equalsIgnoreCase(b0)&&itemid.equalsIgnoreCase(dd)) //薪资总额-报批-后台sql报错 xiaoyun 2014-10-23
							{
								
								String yearmonth =(String)bean.get("year");
								String ayear =yearmonth.substring(0,4);
								String amonth=yearmonth.substring(5);
								
								if(amonth.equals(month))
								{
									//20141212 dengcan
									boolean flag=false;
									if(fc_flag!=null&&fc_flag.length()!=0){
										String z1=(String)bean.get(vo.getModelName()+"z1"); 
										if(z1.equals(vo.getString(vo.getModelName()+"z1")))
											flag=true;
									}
									else
										flag=true;
									if(flag)
									{
										String tt=(String)bean.get(itemid.toLowerCase());
										if(tt==null|| "".equals(tt))
											tt="0";
										sqlValue=Double.parseDouble(tt);
									}
								}
							}
							
							if(b0.startsWith(rs.getString("b0110"))&&!b0.equals(rs.getString("b0110"))&&b0.length()==vo.getString("b0110").length()) //薪资总额-报批-后台sql报错 xiaoyun 2014-10-23
							{
								 String yearmonth =(String)bean.get("year");
								 String ayear =yearmonth.substring(0,4);
								 String amonth=yearmonth.substring(5);
							     if(itemid.equalsIgnoreCase(dd)&&amonth.equals(month))
							     {
							    	 
								    //20141212 dengcan
									if(fc_flag!=null&&fc_flag.length()!=0){
												String z1=(String)bean.get(vo.getModelName()+"z1"); 
												if(z1.equals(vo.getString(vo.getModelName()+"z1")))
													value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
									}
									else 
										value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
							     }
							}
						}
						double voValue = rs1.getDouble(dd)+value/*+rs1.getDouble((String)list.get(i))*/;
						if(sqlValue<voValue){
							FieldItem fielditem = DataDictionary.getFieldItem(dd);
							String codeitem = "";
							String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}else{
								desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
								if(desc!=null&&desc.trim().length()>0){
									codeitem = desc;
								}
							}
							//ghgg
							String exception = "";
							if("0".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
							}
							else if("1".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							else
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							info.append(exception+",");
							info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.max.value")+"\n");
						}
					}
				}
			}
			else
			{
			   String desc=AdminCode.getCodeName("UN",vo.getString("b0110"));
			   if(desc==null|| "".equals(desc))
			   {
				   desc = AdminCode.getCodeName("UM", vo.getString("b0110"));
			   }
			   info.append(desc+ResourceFactory.getProperty("gz.acount.noparent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	/**
	 * 获取查询本部门下一级部门的sql语句
	 * @param vo  
	 * @return String 
	 **/
	private String undersql(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type,String fc_flag) {
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String season = vo.getString("season");
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		if(fc_flag!=null&&fc_flag.length()!=0){
			wheresql.append(" and ");
			wheresql.append(vo.getModelName()+"z1");
			wheresql.append("=");
			wheresql.append(vo.getString(vo.getModelName()+"z1"));
		}
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	/**
	 * 上层部门跟下层部门的值对比
	 * @param vo  
	 * @return String 
	 **/
	private String underValue1(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag) {
		StringBuffer info = new StringBuffer();
		ArrayList list = voList(vo,map);
		try {
			//System.out.println(undersql(vo,dao,"").toString());
			StringBuffer b0110buf = new StringBuffer("");
			for(int i=0;i<changeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
				String b0 = (String)bean.get("b0110");
				b0110buf.append("'");
				b0110buf.append(b0);
				b0110buf.append("',");
			}
			if(b0110buf.toString().length()>0)
				b0110buf.setLength(b0110buf.length()-1);
			String underSql ="";
			if(fc_flag!=null&&fc_flag.length()!=0){
				 underSql = this.undersql(ctrl_peroid, vo, dao, "", map, ctrl_type, fc_flag);
			}else{
				underSql = undersql(ctrl_peroid,vo,dao,"",map,ctrl_type);
			}
			
			StringBuffer s = new StringBuffer();
			s.append(underSql);
			if(b0110buf.toString().length()>0)
			{
				s.append( " and b0110 not in (");
				s.append(b0110buf);
				s.append(")");
			}
			String z0=vo.getString("aaaa");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			RowSet rs = dao.search(s.toString());
			String b0110 = vo.getString("b0110");
			int childlength=this.getChildLength(b0110);
			while(rs.next()){
				for(int i=0;i<list.size();i++){
					double value=0;
					String dd=(String)list.get(i);
					for(int j=0;j<changeList.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
						String b0 = (String)bean.get("b0110");
						String itemid = (String)bean.get("itemid");
						if(b0.startsWith(b0110)&&!b0.equalsIgnoreCase(b0110)&&b0.length()==childlength)
						{
							 String yearmonth =(String)bean.get("year");
							 String ayear =yearmonth.substring(0,4);
							 String amonth=yearmonth.substring(5);
						     if(itemid.equalsIgnoreCase(dd)&&month.equals(amonth))
						     {
						    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
						     }
						}
					}
					double sqlValue = rs.getDouble((String)list.get(i))+value;
					double voValue = vo.getDouble((String)list.get(i));
					
					if(sqlValue>voValue){
						FieldItem fielditem = DataDictionary.getFieldItem((String)list.get(i));
						String codeitem = "";
						String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
						if(desc!=null&&desc.trim().length()>0){
							codeitem = desc;
						}else{
							desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}
						}
						String exception = "";
						if("0".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
						}
						else if("1".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						else
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						info.append(exception+",");
						info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.min.value")+"\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	public int getChildLength(String parentid)
	{
		int length=0;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select codeitemid from organization where parentid='");
			buf.append(parentid+"' and codeitemid<>'"+parentid+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				String itemid = rs.getString("codeitemid");
				length=itemid.length();
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return length;
	}
	public String loopGetUnderValue(String ctrl_peroid,RecordVo vo,String b0110,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag,String aaaa,String season){
		StringBuffer info=new StringBuffer();
		StringBuffer sql=new StringBuffer();
		sql.append("select codeitemid from organization where parentid='");
		sql.append(b0110);
		sql.append("' and codeitemid<>'");
		sql.append(b0110);
		sql.append("'");
		RowSet rs=null;
		try {
			//System.out.println(undersql(vo,dao,"").toString());
			ArrayList list = voList(vo,map);
			StringBuffer b0110buf = new StringBuffer("");
			for(int i=0;i<changeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
				String b0 = (String)bean.get("b0110");
				b0110buf.append("'");
				b0110buf.append(b0);
				b0110buf.append("',");
			}
			if(b0110buf.toString().length()>0)
				b0110buf.setLength(b0110buf.length()-1);
			String underSql ="";
			if(fc_flag!=null&&fc_flag.length()!=0){
				 underSql = this.undersql1(ctrl_peroid, vo, dao, "", map, ctrl_type, fc_flag,aaaa,season);
			}else{
				underSql = undersql1(ctrl_peroid,vo,dao,"",map,ctrl_type,aaaa,season);
			}
			
			StringBuffer s = new StringBuffer();
			s.append(underSql);
			if(b0110buf.toString().length()>0)
			{
				s.append( " and b0110 not in (");
				s.append(b0110buf);
				s.append(")");
			}
			String z0=aaaa;
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			 rs = dao.search(s.toString());
			int childlength=this.getChildLength(b0110);
			while(rs.next()){
				for(int i=0;i<list.size();i++){
					double value=0;
					String dd=(String)list.get(i);
					for(int j=0;j<changeList.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
						String b0 = (String)bean.get("b0110");
						String itemid = (String)bean.get("itemid");
						if(b0.startsWith(b0110)&&!b0.equalsIgnoreCase(b0110)&&b0.length()==childlength)
						{
							 String yearmonth =(String)bean.get("year");
							 String ayear =yearmonth.substring(0,4);
							 String amonth=yearmonth.substring(5); 
						     if(itemid.equalsIgnoreCase(dd)&&month.equals(amonth))
						     {
						    	//20141212 dengcan
								if(fc_flag!=null&&fc_flag.length()!=0){
										String z1=(String)bean.get(vo.getModelName()+"z1"); 
										if(z1.equals(vo.getString(vo.getModelName()+"z1")))
											value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
								}
								else
									value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
						     }
						}
					}
					double sqlValue = rs.getDouble((String)list.get(i))+value;
					double voValue = vo.getDouble((String)list.get(i));
					
					if(sqlValue>voValue){
						FieldItem fielditem = DataDictionary.getFieldItem((String)list.get(i));
						String codeitem = "";
						String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
						if(desc!=null&&desc.trim().length()>0){
							codeitem = desc;
						}else{
							desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}
						}
						String exception = "";
						if("0".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+aaaa;
						}
						else if("1".equals(ctrl_peroid))
						{ 
							if(vo.getString(vo.getModelName()+"z0b")!=null&&vo.getString(vo.getModelName()+"z0b").trim().length()>0){ 
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}else{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0");
							}
						}
						else
						{
							if(vo.getString(vo.getModelName()+"z0b")!=null&&vo.getString(vo.getModelName()+"z0b").trim().length()>0){//dml 2011年7月22日11:35:48
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}else{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0");
							}
						}
						info.append(exception+",");
						info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.min.value")+"\n");
					}
				}
			}
			if(info.length()<1){
				rs=dao.search(sql.toString());
				ArrayList blist=new ArrayList();
				while(rs.next()){
					blist.add(rs.getString(1));
				}
				if(blist==null&&blist.size()==0){
					info.append("ok");
				}else{
					for(int k=0;k<blist.size();k++){
						if(!this.isLeafNode((String)blist.get(k), dao)){
							continue;
						}
						String vsql=this.getOwnSql(list, (String)blist.get(k), fc_flag, ctrl_peroid, vo,aaaa,season);
						RecordVo newV=this.getVo(vsql, ctrl_peroid, dao, list, ctrl_type, fc_flag, vo, (String)blist.get(k));
						if(newV!=null){
							info.append(this.loopGetUnderValue(ctrl_peroid, newV, (String)blist.get(k), dao, map, changeList, ctrl_type, fc_flag,aaaa,season));
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
				try {
					if(rs!=null){
						rs.close();
					}
				} catch (SQLException e) {
			
					e.printStackTrace();
				}
		}
		
		return info.toString();
	}
	public	String  underValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag){
		String info="";
		StringBuffer b0110buf = new StringBuffer("");
		String b0110=vo.getString("b0110");
		String aaaa=vo.getString("aaaa");
		String season=vo.getString("season");
		info=this.loopGetUnderValue(ctrl_peroid, vo, b0110, dao, map, changeList, ctrl_type, fc_flag,aaaa,season);
		return info;
	}
	public String getOwnSql(ArrayList list,String b0110,String fc_flag,String ctrl_peroid,RecordVo vo,String aaaa,String season){
		StringBuffer sql=new StringBuffer();
		String z0=aaaa;
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 ='");
		wheresql.append(b0110);
		wheresql.append("' ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		if(fc_flag!=null&&fc_flag.length()!=0){
			wheresql.append(" and ");
			wheresql.append(vo.getModelName()+"z1");
			wheresql.append("=");
			wheresql.append(vo.getString(vo.getModelName()+"z1"));
		}
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		return sqlstr.toString();
	}
	public RecordVo getVo(String sql,String ctrl_peroid,ContentDAO dao,ArrayList list,String ctrl_type,String fc_flag,RecordVo vo,String b0110){
		RecordVo vo1=null;
		RowSet rs=null;
		try {
			rs=dao.search(sql.toString());
			if(rs.next()){
				vo1=new RecordVo(vo.getModelName());
				for(int i=0;i<list.size();i++){
					vo1.setDouble((String)list.get(i), rs.getDouble((String)list.get(i)));
				}
				vo1.setString("b0110",b0110);
				vo1.setString(vo.getModelName()+"z1", vo.getString(vo.getModelName()+"z1"));
				vo1.setString(vo.getModelName()+"z0", vo.getString(vo.getModelName()+"z0"));
				if ("1".equals(ctrl_peroid)||"2".equals(ctrl_peroid)){
				    String fieldName=vo.getModelName()+"z0b";
				    String value=vo.getString(vo.getModelName()+"z0b");
				    vo1.getModelAttrs().add(fieldName);
				    vo1.setString(fieldName,value );
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			
				try {
					if(rs!=null){
						rs.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
		}
		return vo1;
	}
	public String getSalarySet() {
		return salarySet;
	}
	public void setSalarySet(String salarySet) {
		this.salarySet = salarySet;
	}
	private String undersql1(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type,String fc_flag,String aaaa,String season) {
		String z0=aaaa;
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid)){
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		if(fc_flag!=null&&fc_flag.length()!=0){
			wheresql.append(" and ");
			wheresql.append(vo.getModelName()+"z1");
			wheresql.append("=");
			wheresql.append(vo.getString(vo.getModelName()+"z1"));
		}
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	public boolean isLeafNode(String unitcode,ContentDAO dao){
		boolean flag=false;
		RowSet rs=null;
		StringBuffer sql=new StringBuffer();
		sql.append("select * from organization where parentid='");
		sql.append(unitcode);
		sql.append("'");
		sql.append(" and parentid<>codeitemid and (codesetid='UM' or codesetid='UN')");
		try {
			rs=dao.search(sql.toString());
			if(rs.next()){
				flag=true;
			}else{
				flag=false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
}
