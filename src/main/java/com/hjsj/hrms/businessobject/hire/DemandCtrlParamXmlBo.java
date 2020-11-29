package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


/*
 * <?xml version='1.0' encoding="GB2312"?>
 * <content>
 * <answer_mail flag="false|true" template="1" />
 *  <simple>
 *  <fielditem name="AXXXX" s_value="01" e_value="05" flag="false"/>
 *  <fielditem name="AXXXY" s_value="01,20," e_value="05,03" flag="true"/>
 *  …
 *  </simple>
 *  <general>
 *  
 *  
 *  </general>
 * </content>
 * 
 * 
 */


public class DemandCtrlParamXmlBo {
	private Connection conn;
	private String xml="";
	private Document doc;
	private String param;
	
	public static void main(String[] arg)
	{
		try
		{
			DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo();
			StringBuffer xx=new StringBuffer("<?xml version='1.0' encoding=\"GB2312\"?><content>");
			xx.append("<answer_mail flag=\"false\" template=\"1\" />");
			xx.append("<simple>");
			xx.append("<fielditem name=\"AXXXX\" s_value=\"01\" e_value=\"05\" flag=\"false\"/>");
			xx.append("<fielditem name=\"AXXXX\" s_value=\"01,20\" e_value=\"05,03\" flag=\"true\"/>");
			xx.append("</simple>");
			xx.append("</content>");
			bo.xml=xx.toString();
			
			HashMap map=new HashMap();
			LazyDynaBean abean=new LazyDynaBean();
			abean.set("flag","a");
			abean.set("template","b");
			map.put("answer_mail",abean);
	//		bo.updateNode("answer_mail",map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public DemandCtrlParamXmlBo()
	{
		
	}
	
	public DemandCtrlParamXmlBo(Connection conn,String z0301)
	{
		this.conn=conn;
		this.param=z0301;
		init(z0301);
		this.initXML(z0301);
		try{
			doc = PubFunc.generateDom(xml);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
	}
	private void init(String z0301){
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<param>");
		temp_xml.append("</param>");		
		try{
			RecordVo vo=ConstantParamter.getRealConstantVo(z0301,this.conn);
			if(vo!=null) {
                xml=vo.getString("ctrl_param");
            }
			if(xml==null|| "".equals(xml)){
				xml=temp_xml.toString();
			}
			doc = PubFunc.generateDom(xml);
		}catch(Exception ex){
			xml=temp_xml.toString();
		}
	}
	
	
	/**
	 * 查询DB 获取XML文件字符串
	 */
	private void initXML(String z0301){
		if(!"-1".equalsIgnoreCase(z0301))
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try{	
				//常量表中查找rp_param常量
				rs=dao.search("select ctrl_param  from z03 where z0301='"+z0301+"'");
				if(rs.next()){
					//获取XML文件
					xml = Sql_switcher.readMemo(rs,"ctrl_param");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(xml==null|| "".equals(xml))
		{
			StringBuffer xx=new StringBuffer("<?xml version='1.0' encoding=\"GB2312\"?><content></content>");
			xml=xx.toString();
		}
	}
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (IOException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	
	
	public void createParamXml(String node,HashMap nodeMap,String z0301)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			Element element=new Element("content");
			if("simple".equalsIgnoreCase(node)|| "general".equalsIgnoreCase(node))
			{
				Element nodeElement = new Element(node);
				ArrayList list=(ArrayList)nodeMap.get(node);
				for(Iterator t=list.iterator();t.hasNext();)
				{
					LazyDynaBean abean=(LazyDynaBean)t.next();
					if(abean!=null)
					{
						Element itemElement = new Element("fielditem");
						itemElement.setAttribute("name",(String)abean.get("name"));
						String s_value=(String)abean.get("s_value");
						if(s_value==null) {
                            s_value="";
                        }
						String e_value=(String)abean.get("e_value");
						if(e_value==null) {
                            e_value="";
                        }
						itemElement.setAttribute("s_value",s_value);
						itemElement.setAttribute("e_value",e_value);
						itemElement.setAttribute("flag",(String)abean.get("flag"));
						itemElement.setAttribute("type",(String)abean.get("type"));
						
						nodeElement.addContent(itemElement);
					}
				}
				element.addContent(nodeElement);
			}
			if("answer_mail".equalsIgnoreCase(node))
			{
				Element nodeElement = new Element(node);
				LazyDynaBean abean=(LazyDynaBean)nodeMap.get(node);
				if(abean!=null)
				{
					nodeElement.setAttribute("flag",(String)abean.get("flag"));
					nodeElement.setAttribute("template",(String)abean.get("template"));
					element.addContent(nodeElement);
				}
			}
			Document myDocument = new Document(element);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			dao.update("update z03 set ctrl_param='"+outputter.outputString(myDocument)+"' where z0301='"+z0301+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public void updateNode(String node,HashMap nodeMap,String z0301)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			init();
			XPath xPath = XPath.newInstance("/content");
			Element element= (Element) xPath.selectSingleNode(this.doc);
			element.removeChild(node);
			if("simple".equalsIgnoreCase(node)|| "general".equalsIgnoreCase(node))
			{
				Element nodeElement = new Element(node);
				ArrayList list=(ArrayList)nodeMap.get(node);
				for(Iterator t=list.iterator();t.hasNext();)
				{
					LazyDynaBean abean=(LazyDynaBean)t.next();
					if(abean!=null)
					{
						Element itemElement = new Element("fielditem");
						itemElement.setAttribute("name",(String)abean.get("name"));
						String s_value=(String)abean.get("s_value");
						if(s_value==null) {
                            s_value="";
                        }
						String e_value=(String)abean.get("e_value");
						if(e_value==null) {
                            e_value="";
                        }
						
						itemElement.setAttribute("s_value",s_value);
						itemElement.setAttribute("e_value",e_value);
						itemElement.setAttribute("flag",(String)abean.get("flag"));
						itemElement.setAttribute("type",(String)abean.get("type"));
						
						nodeElement.addContent(itemElement);
					}
				}
				element.addContent(nodeElement);
			}
			if("answer_mail".equalsIgnoreCase(node))
			{
				Element nodeElement = new Element(node);
				LazyDynaBean abean=(LazyDynaBean)nodeMap.get(node);
				if(abean!=null)
				{
					nodeElement.setAttribute("flag",(String)abean.get("flag"));
					nodeElement.setAttribute("template",(String)abean.get("template"));
					element.addContent(nodeElement);
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			
			dao.update("update z03 set ctrl_param='"+outputter.outputString(this.doc)+"' where z0301='"+z0301+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	

	
	
	
	//获得特定用户XML文件元素的相关属性值集合
	public HashMap getAttributeValues(String node) throws GeneralException {
		HashMap hm = new HashMap();
		try
		{
			if(xml == null || "".equals(xml.trim())){
				return hm;
			}else{
				init();
				if("simple".equalsIgnoreCase(node)|| "general".equalsIgnoreCase(node))
				{
					XPath xPath = XPath.newInstance("/content/"+node+"/fielditem");
					ArrayList simpleList=new ArrayList();
					List list=xPath.selectNodes(this.doc);
					for(Iterator t=list.iterator();t.hasNext();)
					{
						Element simpleElement =(Element)t.next(); 
						if (simpleElement != null) 
						{
							LazyDynaBean abean=new LazyDynaBean();
							abean.set("name",simpleElement.getAttributeValue("name"));
							abean.set("s_value",simpleElement.getAttributeValue("s_value"));
							abean.set("e_value",simpleElement.getAttributeValue("e_value"));
							abean.set("flag",simpleElement.getAttributeValue("flag"));
							abean.set("type",simpleElement.getAttributeValue("type"));
							simpleList.add(abean);
						}
					}
					hm.put(node,simpleList);
				}
				if("answer_mail".equalsIgnoreCase(node))
				{
					XPath xPath = XPath.newInstance("/content/"+node);
					Element element = (Element) xPath.selectSingleNode(this.doc);
					if (element != null) {
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("flag",element.getAttributeValue("flag"));
						abean.set("template",element.getAttributeValue("template"));
						hm.put(node,abean);
					}
				}
				return hm;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return hm;
	}
	/**********************************************************************
	 * 
	 * LiZhenWei add 2009-05-12(编制控制)
	 ***********************************************************************/
	public DemandCtrlParamXmlBo(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 根据需求ID取得需求的信息
	 * @param z0301
	 * @return
	 */
	public HashMap getPoisitionInfo(String z0301)
	{
		HashMap map = new HashMap();
		try{
			StringBuffer buf = new StringBuffer("");
			String[] arr = z0301.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				buf.append(",'"+arr[i]+"'");
			}
			if(buf.toString().length()<=0) {
                return map;
            }
			StringBuffer sql = new StringBuffer("");
			sql.append("select z0301,z0321,z0325,z0313,z0316,z0311,z0336 from z03 ");//dml 2011-5-25 17:53:47
			sql.append(" where z0301 in ("+buf.toString().substring(1)+")");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("z0301", rs.getString("z0301"));
				bean.set("z0321", rs.getString("z0321"));
				bean.set("z0325", rs.getString("z0325")==null?"":rs.getString("z0325"));
				bean.set("z0313", (rs.getString("z0313")==null|| "".equals(rs.getString("z0313")))?"0":rs.getString("z0313"));
				bean.set("z0316", rs.getString("z0316"));
				bean.set("z0311", rs.getString("z0311")==null?"":rs.getString("z0311"));
				bean.set("z0336", rs.getString("z0336"));//dml 2011-5-25 17:53:47
				map.put(rs.getString("z0301"), bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得某部门或者单位下的所有需求的招聘人数
	 * @param value
	 * @param cloumn
	 * @param dao
	 * @return
	 */
	public int getZ03PersonCount(String value,String cloumn,ContentDAO dao,String z0301,int type,int countrs)
	{
		int count=0;
		try
		{
			/*String sql = " select sum(z0313) as z0313 from z03 where "+cloumn+" like '"+value+"%' and z0301<>'"+z0301+"' and z0316='01'";
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				count=rs.getInt("z0313");
			}*/
			/**已发布和暂停的，按照招聘订单来计算人数，因为有些人已经招到，已算入实有人数中*/
			StringBuffer sql = new StringBuffer("");
			sql.append("select z0301 from z03 where "+cloumn+" like '"+value+"%' and (z0316='01' or z0316='1')");
			sql.append(" and (z0319='04' or z0319='09')");
			StringBuffer suez03= new StringBuffer("");
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				suez03.append("'"+rs.getString("z0301")+"',");
			}
			int sueCount=0;
			if(suez03.toString().length()>0)
			{
				suez03.setLength(suez03.length()-1);
				String z04sql = "select count(*) from z04 where z0407 in ("+suez03.toString()+") and z0410='2'";
				RowSet z04rs = dao.search(z04sql);
				while(z04rs.next())
				{
					sueCount=z04rs.getInt(1);
				}
			}
			sql.setLength(0);
		    /**已发布和暂停的完毕*/
			/**已批的需求，算审核人数*/
			int ypCount =0;
			sql.append("select sum(z0315) as ypc from z03 where "+cloumn+" like '"+value+"%' and (z0316='01' or z0316='1')");
			sql.append(" and z0319='03' and z0301<>'"+z0301+"'");
			RowSet yprs = dao.search(sql.toString());
			while(yprs.next())
			{
				String plancount=yprs.getString("ypc")==null?"0":yprs.getString("ypc");
				ypCount=Integer.parseInt(PubFunc.round(plancount,0));
			}
			sql.setLength(0);
			/**已报批的需求，算需求人数*/
			int ybpCount=0;
			sql.append("select sum(z0313) as ybpc from z03 where "+cloumn+" like '"+value+"%' and (z0316='01' or z0316='1')");
			sql.append(" and z0319='02' and z0301<>'"+z0301+"'");
			RowSet ybprs = dao.search(sql.toString());
			while(ybprs.next())
			{
				String plancount=ybprs.getString("ybpc")==null?"0":ybprs.getString("ybpc");
				ybpCount=Integer.parseInt(PubFunc.round(plancount,0));
			}
			int thiscount=0;
			if(type==0)
			{
				sql.setLength(0);
				sql.append("select z0313 from z03 where z0301='"+z0301+"'");
		    	RowSet thisrs = dao.search(sql.toString());
		    	while(thisrs.next())
		    	{
	    			thiscount=thisrs.getInt("z0313");
	    		}
			}
			else
			{
				thiscount=countrs;
			}
			count = sueCount+ypCount+ybpCount+thiscount;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	public String getAllPre(ContentDAO dao)
	{
		StringBuffer pre=new StringBuffer();
		try{
			String sql = "select pre from dbname";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				pre.append(","+rs.getString("pre"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pre.toString().substring(1);
	}
	/***
	 * 判断职位需求人数是否超出编制控制
	 * @param z0301
	 * @param pos
	 * @param posInfoMap
	 * @return
	 * @throws GeneralException 
	 */
	public String isToSurpassBZ(String z0301s,PosparameXML pos,HashMap posInfoMap) throws GeneralException
	{
		StringBuffer message = new StringBuffer("");;
		try
		{
			String setid=pos.getValue(PosparameXML.AMOUNTS,"setid"); 
			FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
			if(fieldset == null) {
                throw new GeneralException("请到组织机构->参数设置->编制参数设置->设置参数!");
            }
			ContentDAO dao = new ContentDAO(this.conn);
			/**=1控制到部门，=0控制到单位*/
			String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
			ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
			String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
			if(dbs==null|| "".equals(dbs))
			{
				dbs = this.getAllPre(dao);
			}
			ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
			boolean f=true;
			String plan="";
			String condid="";
			/**编制参数中，取没定义条件的第一个参数*/
			for(int i=0;i<planitemlist.size();i++)
			{
		    	String planitem=(String)planitemlist.get(i);
	     		String valid = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitem,"flag");
		    	String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitem,"static");
		    	if(cond==null&&cond.trim().length()<=0){
		    		plan=planitem;
		    		condid=cond;
		    		f=false;
		    		break;
		    	}
			}
			if(f)
			{
				plan=(String)planitemlist.get(0);
		    	condid = pos.getChildValue(PosparameXML.AMOUNTS,setid,plan,"static");
			}
			//RecordVo vo=null;
			/*if(condid!=null&&!condid.equals(""))
			{
				vo = new RecordVo("LExpr");
				vo.setInt("id",Integer.parseInt(condid));
				vo = dao.findByPrimaryKey(vo);
			}
			*/
			String [] arr=z0301s.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String z0301=arr[i];
				LazyDynaBean  bean = (LazyDynaBean)posInfoMap.get(z0301);
				String z0325=(String)bean.get("z0325");
				String z0321=(String)bean.get("z0321");
				String z0311=(String)bean.get("z0311");
				String z0336=(String)bean.get("z0336");//dml 2011-5-25 17:53:47
				/**所有招聘需求中的需求人数*/
				int z03count=0;
				if("1".equals(ctrl_type))
				{
					if(!"".equals(z0325)) {
                        z03count=this.getZ03PersonCount(z0325, "z0325", dao,z0301,0,0);
                    } else {
                        z03count=this.getZ03PersonCount(z0321, "z0321", dao,z0301,0,0);
                    }
				}
				else {
                    z03count=this.getZ03PersonCount(z0321, "z0321", dao,z0301,0,0);
                }
				String [] preArr = dbs.split(",");
				/**实有人数*/
				int count=0;
				for(int j=0;j<preArr.length;j++)
				{
					StringBuffer buf = new StringBuffer();
					String nbase=preArr[j];
					String wherestr="";
					buf.append("select count("+nbase+"A01.A0100) as counts ");
					buf.append(" from "+nbase+"a01 where ");
					if("1".equals(ctrl_type))
					{
						if(!"".equals(z0325)) {
                            buf.append(" e0122 like '"+z0325+"%'");
                        } else {
                            buf.append(" b0110 like '"+z0321+"%'");
                        }
					}
					else
					{
						buf.append(" b0110 like '"+z0321+"%'");
					}
					RowSet rs = dao.search(buf.toString());
					while(rs.next())
					{
						count+=rs.getInt("counts");
					}
				}
				/**总编制人数*/
				int zcount=0;
				StringBuffer planBuffer=new StringBuffer();
				planBuffer.append(" select "+plan);
				planBuffer.append(" from "+fieldset.getFieldsetid()+" a where ");
				if("1".equals(ctrl_type))
				{
					if(!"".equals(z0325)) {
                        planBuffer.append(" b0110='"+z0325+"'");
                    } else {
                        planBuffer.append(" b0110='"+z0321+"'");
                    }
					
				}
				else
				{
					planBuffer.append(" b0110='"+z0321+"'");
				}
				if(!fieldset.isMainset()){
					planBuffer.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
				}
				RowSet rst = dao.search(planBuffer.toString());
				while(rst.next())
				{
					String plancount=rst.getString(plan)==null?"0":rst.getString(plan);
					zcount=Integer.parseInt(PubFunc.round(plancount,0));
				}
				/**超编了*/
				if((z03count+count)>zcount)
				{
					String un=AdminCode.getCodeName("UN",z0321);
					String um=AdminCode.getCodeName("UM",z0325);
					if("01".equals(z0336)){//dml 2011-5-25 17:53:47
						message.append(un+um+"需求人数超编，不能操作");
					}else{
						String K=AdminCode.getCodeName("@K",z0311);
						message.append(un+um+K+"需求人数超编，不能操作");
					}
					
					
				}
				
			}
	        
			
			if(message.toString().length()<=0) {
                message.append("0");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return message.toString();
	}
	
	public String isModToSurpassBZ(String z0301s,PosparameXML pos,HashMap posInfoMap,String z0313,String z0315)
	{
		StringBuffer message = new StringBuffer("");
		try
		{
			String setid=pos.getValue(PosparameXML.AMOUNTS,"setid"); 
			FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
			ContentDAO dao = new ContentDAO(this.conn);
			/**=1控制到部门，=0控制到单位*/
			String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
			ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
			String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
			if(dbs==null|| "".equals(dbs))
			{
				dbs = this.getAllPre(dao);
			}
			ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
			boolean f=true;
			String plan="";
			String condid="";
			/**编制参数中，取没定义条件的第一个参数*/
			for(int i=0;i<planitemlist.size();i++)
			{
		    	String planitem=(String)planitemlist.get(i);
	     		String valid = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitem,"flag");
		    	String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitem,"static");
		    	if(cond==null&&cond.trim().length()<=0){
		    		plan=planitem;
		    		condid=cond;
		    		f=false;
		    		break;
		    	}
			}
			if(f)
			{
				plan=(String)planitemlist.get(0);
		    	condid = pos.getChildValue(PosparameXML.AMOUNTS,setid,plan,"static");
			}
			String [] arr=z0301s.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String z0301=arr[i];
				LazyDynaBean  bean = (LazyDynaBean)posInfoMap.get(z0301);
				String z0325=(String)bean.get("z0325");
				String z0321=(String)bean.get("z0321");
				String z0311=(String)bean.get("z0311");
				String desc="审核人数";
				/**所有招聘需求中的需求人数*/
				int countrs=0;
				if(("0".equals(z0315)|| "".equals(z0315))&&!"".equals(z0313))
				{
					countrs=Integer.parseInt(z0313);
					desc="需求人数";
				}
				else if(!"".equals(z0315)) {
                    countrs=Integer.parseInt(z0315);
                }
				    
				int z03count=0;
				if("1".equals(ctrl_type)) {
                    if(!"".equals(z0325)) {
                        z03count = this.getZ03PersonCount(z0325, "z0325", dao, z0301, 1, countrs);
                    } else {
                        z03count = this.getZ03PersonCount(z0321, "z0321", dao, z0301, 1, countrs);
                    }
                } else {
                    z03count=this.getZ03PersonCount(z0321, "z0321", dao,z0301,1,countrs);
                }
				String [] preArr = dbs.split(",");
				/**实有人数*/
				int count=0;
				for(int j=0;j<preArr.length;j++)
				{
					StringBuffer buf = new StringBuffer();
					String nbase=preArr[j];
					String wherestr="";
					buf.append("select count("+nbase+"A01.A0100) as counts ");
					buf.append(" from "+nbase+"a01 where ");
					if("1".equals(ctrl_type))
					{
						if(!"".equals(z0325)) {
                            buf.append(" e0122 like '"+z0325+"%'");
                        } else {
                            buf.append(" b0110 like '"+z0321+"%'");
                        }
					}
					else
					{
						buf.append(" b0110 like '"+z0321+"%'");
					}
					RowSet rs = dao.search(buf.toString());
					while(rs.next())
					{
						count+=rs.getInt("counts");
					}
				}
				/**总编制人数*/
				int zcount=0;
				StringBuffer planBuffer=new StringBuffer();
				planBuffer.append(" select "+plan);
				planBuffer.append(" from "+fieldset.getFieldsetid()+" a where ");
				if("1".equals(ctrl_type))
				{
					if(!"".equals(z0325)) {
                        planBuffer.append(" b0110='"+z0325+"'");
                    } else {
                        planBuffer.append(" b0110='"+z0321+"'");
                    }
					
				}
				else
				{
					planBuffer.append(" b0110='"+z0321+"'");
				}
				if(!fieldset.isMainset()){
					planBuffer.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
				}
				RowSet rst = dao.search(planBuffer.toString());
				while(rst.next())
				{
					String plancount=rst.getString(plan)==null?"0":rst.getString(plan);
					zcount=Integer.parseInt(PubFunc.round(plancount,0));
				}
				/**超编了*/
				if((z03count+count)>zcount)
				{
					String un=AdminCode.getCodeName("UN",z0321);
					String um=AdminCode.getCodeName("UM",z0325);
					String K=AdminCode.getCodeName("@K",z0311);
					message.append(un+um+K+desc+"超编，不能操作");
				}
				
			}
	        
			
			if(message.toString().length()<=0) {
                message.append("0");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return message.toString();
	}
	/**
	 * 仅求编制内的需求用来判断工资总额控制
	 * @param z0301s
	 * @param z0316 =1编制内 =2编制外
	 * @return
	 */
	public String getGZInner(String z0301s,String z0316)
	{
		String str="";
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			String schoolPosition="";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0) {
                schoolPosition=(String)map.get("schoolPosition");
            }
			String [] arr = z0301s.split(",");
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				buf.append(",'"+arr[i]+"'");
			}
			StringBuffer sql = new StringBuffer("select z0301 from z03 where z0301 in("+buf.toString().substring(1)+") ");
			/**编制内*/
			if("1".equals(z0316)|| "01".equals(z0316))
			{
				sql.append(" and (z0316='1' or z0316='01')");
				if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					sql.append(" and z0336<>'01' ");
				}
			}
			/**编制外*/
			if("2".equals(z0316)|| "02".equals(z0316))
			{
				sql.append(" and ((z0316='2' or z0316='02')");
				if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					sql.append(" or ((z0316='1' or z0316='01') and z0336='01')");
				}
				sql.append(")");
			}
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs = dao.search(sql.toString());
		    while(rs.next())
		    {
		    	str+=","+rs.getString("z0301");
		    }
		    if(str.length()>0) {
                str=str.substring(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getBZInner(String z0301s,String z0316)
	{
		String str="";
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			String schoolPosition="";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0) {
                schoolPosition=(String)map.get("schoolPosition");
            }
			String [] arr = z0301s.split(",");
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				buf.append(",'"+arr[i]+"'");
			}
			StringBuffer sql = new StringBuffer("select z0301 from z03 where z0301 in("+buf.toString().substring(1)+") ");
			/**编制内*/
			if("1".equals(z0316)|| "01".equals(z0316))
			{
				sql.append(" and (z0316='1' or z0316='01')");
				/*if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					sql.append(" and z0336<>'01' ");
				}*/
			}
			/**编制外*/
			if("2".equals(z0316)|| "02".equals(z0316))
			{
				sql.append(" and ((z0316='2' or z0316='02')");
				/*if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					sql.append(" or ((z0316='1' or z0316='01') and z0336='01')");
				}*/
				sql.append(")");
			}
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs = dao.search(sql.toString());
		    while(rs.next())
		    {
		    	str+=","+rs.getString("z0301");
		    }
		    if(str.length()>0) {
                str=str.substring(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/***
	 * 判断是否超过工资总额控制
	 * @param z0301s
	 * @param gzAmountMap
	 * @param posInfoMap
	 * @param positionSalaryStandardItem
	 * @param amountParamFieldList
	 * @return
	 */
	public String isToSurpassGZ(String z0301s,HashMap gzAmountMap ,HashMap posInfoMap,String positionSalaryStandardItem)
	{
		StringBuffer message = new StringBuffer("");
		try
		{
			String setid = (String) gzAmountMap.get("setid");
			FieldSet ff=DataDictionary.getFieldSetVo(setid);
			if("0".equals(ff.getUseflag()))
			{
				message.append("工资总额子集未构库！");
				return message.toString();
			}
			
			/**=0控制到部门*/
			String ctrl_type = (String) gzAmountMap.get("ctrl_type");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList itemList = (ArrayList)gzAmountMap.get("ctrl_item");
			String[] arr=z0301s.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String z0301=arr[i];
				LazyDynaBean  bean = (LazyDynaBean)posInfoMap.get(z0301);
				String z0325=(String)bean.get("z0325");
				String z0321=(String)bean.get("z0321");
				String z0311=(String)bean.get("z0311");
				String z0313=(String)bean.get("z0313");
				BigDecimal z03BigDecimal=this.getZ03GZAmount(positionSalaryStandardItem, Integer.parseInt(((z0313==null|| "".equals(z0313))?"0":z0313)), z0311, dao);
				String orgvalue="";
				if("0".equals(ctrl_type))
				{
					if(!"".equals(z0325)) {
                        orgvalue=z0325;
                    } else {
                        orgvalue=z0321;
                    }
				}
				else {
                    orgvalue=z0321;
                }
				/*for(int j=0;j<itemList.size();j++)
				{*/
					LazyDynaBean itemBean=(LazyDynaBean)itemList.get(0);
					String flag = (String)itemBean.get("flag");
					if("0".equals(flag)) {
                        continue;
                    }
					String planitem=(String)itemBean.get("planitem");
					String realitem=(String)itemBean.get("realitem");
                    HashMap planRealMap = this.getPlanRealAmount(setid, orgvalue, planitem, realitem);
                    BigDecimal planBD=(BigDecimal)planRealMap.get("plan");
                    BigDecimal realBD=(BigDecimal)planRealMap.get("real");
                    if(realBD.add(z03BigDecimal).compareTo(planBD)==1)
                    {
                    	String un=AdminCode.getCodeName("UN",z0321);
    					String um=AdminCode.getCodeName("UM",z0325);
    					String K=AdminCode.getCodeName("@K",z0311);
    					message.append(un+"/"+um+"/"+K+"   超过工资总额数，不能操作！\t\r");
                    }
				/*}*/
			}
			if(message.toString().length()<=0) {
                message.append("0");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return message.toString();
	}
	public String isModToSurpassGZ(String z0301s,HashMap gzAmountMap ,HashMap posInfoMap,String positionSalaryStandardItem,String z0315,String z0313)
	{
		StringBuffer message = new StringBuffer("");
		try
		{
			String setid = (String) gzAmountMap.get("setid");
			FieldSet ff=DataDictionary.getFieldSetVo(setid);
			if("0".equals(ff.getUseflag()))
			{
				message.append("工资总额子集未构库！");
				return message.toString();
			}
			
			/**=0控制到部门*/
			String ctrl_type = (String) gzAmountMap.get("ctrl_type");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList itemList = (ArrayList)gzAmountMap.get("ctrl_item");
			String[] arr=z0301s.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String z0301=arr[i];
				LazyDynaBean  bean = (LazyDynaBean)posInfoMap.get(z0301);
				String z0325=(String)bean.get("z0325");
				String z0321=(String)bean.get("z0321");
				String z0311=(String)bean.get("z0311");
				String az0313="0";
				if("".equals(z0315)&&!"".equals(z0313)) {
                    az0313=z0313;
                } else if(!"".equals(z0315)) {
                    az0313=z0315;
                }
				BigDecimal z03BigDecimal=this.getZ03GZAmount(positionSalaryStandardItem, Integer.parseInt(((az0313==null|| "".equals(az0313))?"0":az0313)), z0311, dao);
				String orgvalue="";
				if("0".equals(ctrl_type))
				{
					if(!"".equals(z0325)) {
                        orgvalue=z0325;
                    } else {
                        orgvalue=z0321;
                    }
				}
				else {
                    orgvalue=z0321;
                }
				/*for(int j=0;j<itemList.size();j++)
				{*/
					LazyDynaBean itemBean=(LazyDynaBean)itemList.get(0);
					String flag = (String)itemBean.get("flag");
					if("0".equals(flag)) {
                        continue;
                    }
					String planitem=(String)itemBean.get("planitem");
					String realitem=(String)itemBean.get("realitem");
                    HashMap planRealMap = this.getPlanRealAmount(setid, orgvalue, planitem, realitem);
                    BigDecimal planBD=(BigDecimal)planRealMap.get("plan");
                    BigDecimal realBD=(BigDecimal)planRealMap.get("real");
                    if(realBD.add(z03BigDecimal).compareTo(planBD)==1)
                    {
                    	String un=AdminCode.getCodeName("UN",z0321);
    					String um=AdminCode.getCodeName("UM",z0325);
    					String K=AdminCode.getCodeName("@K",z0311);
    					message.append(un+"/"+um+"/"+K+"   超过工资总额数，不能操作！\t\r");
                    }
				/*}*/
			}
			if(message.toString().length()<=0) {
                message.append("0");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return message.toString();
	}
	/**
	 * 取得工资总额控制中上月实发额和这个月的计划额
	 * @param setid
	 * @param orgvalue
	 * @param planitem
	 * @param realitem
	 * @return
	 */
	public HashMap getPlanRealAmount(String setid,String orgvalue,String planitem,String realitem)
	{
		HashMap map = new HashMap();
		try
		{
			Calendar calendar = Calendar.getInstance();
			int month=calendar.get(Calendar.MONTH)+1;
            int year = calendar.get(Calendar.YEAR);
            StringBuffer sql = new StringBuffer("");
            sql.append("select a."+planitem+",b."+realitem+" from ");
            sql.append("(select "+planitem+",b0110 from "+setid+" where b0110='"+orgvalue+"' ");
            sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0"));
            sql.append("="+month+") a,(select "+realitem+",b0110 from "+setid+" where b0110='"+orgvalue+"'");
            sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+(month==1?(year-1):year));
            sql.append(" and "+Sql_switcher.month(setid+"z0")+"="+(month==1?12:(month-1)));
            sql.append(") b where a.b0110=b.b0110");
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = dao.search(sql.toString());
            BigDecimal plan=null;
            BigDecimal real=null;
            while(rs.next())
            {
                plan=new BigDecimal(rs.getString(planitem)==null?"0":rs.getString(planitem));
            	real=new BigDecimal(rs.getString(realitem)==null?"0":rs.getString(realitem));
            }
            if(plan==null)
            {
            	plan = new BigDecimal("0");
            	real = new BigDecimal("0");
            }
            map.put("plan", plan);
            map.put("real",real);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得招聘需求的最高工资总额
	 * @param positionSalaryStandardItem
	 * @param z0313
	 * @param z0311
	 * @param dao
	 * @return
	 */
	public BigDecimal getZ03GZAmount(String positionSalaryStandardItem,int z0313,String z0311,ContentDAO dao)
	{
		BigDecimal bd=null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select "+positionSalaryStandardItem+" from k01 where e01a1='"+z0311+"'");
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				String temp=rs.getString(positionSalaryStandardItem);
				if(temp==null) {
                    temp="0";
                }
				float temp_float = Float.parseFloat(temp);
				bd = new BigDecimal(String.valueOf(temp_float*z0313));
			}
			if(bd==null) {
                bd = new BigDecimal("0");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bd;
	}
	public void setAttributeValue(String str_path,String attributeName,String attributeValue){
		try{
			XPath xpath=XPath.newInstance(str_path);
			Element spElement=(Element)xpath.selectSingleNode(this.doc);
			if(spElement==null){
			
				String arr[] = str_path.split("/");
				if(arr!=null&&arr.length>0){
					for(int i=1;i<arr.length;i++){
						String path = "";
						for(int j=1;j<=i;j++){
							path+="/"+arr[j];
						}
						xpath=XPath.newInstance(path);
						Element bbElement=(Element)xpath.selectSingleNode(this.doc);
						if(bbElement==null){
							Element element=new Element(arr[i]);
							if(i==arr.length-1) {
                                element.setAttribute(attributeName, attributeValue);
                            }
							spElement.addContent(element);
						}else{
						    spElement = bbElement;
						}
					}
				}
			}else{
				spElement.setAttribute(attributeName, attributeValue);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}
	/**
	 * 保存xml格式的内容
	 *
	 */
	public void saveStrValue(){
		PreparedStatement pstmt = null;		
		StringBuffer strsql=new StringBuffer();
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{
			ifNoParameterInsert(param);
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));
			strsql.append("update z03 set ctrl_param=? where UPPER(z0301)='"+param+"'");
			pstmt = this.conn.prepareStatement(strsql.toString());	
			switch(Sql_switcher.searchDbServer()){
				 case Constant.MSSQL:
					  pstmt.setString(1, buf.toString());
					  break;
				 case Constant.ORACEL:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
				  case Constant.DB2:
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());
					  break;
			}
			// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 如果数据库中没有这个名称记录则插入
	 * @param param_name  
	 */
	public void ifNoParameterInsert(String param_name){
		  String sql="select * from z03 where UPPER(z0301)='"+param_name.toUpperCase()+"'";
		  ContentDAO dao = new ContentDAO(conn);
		  RowSet rs=null;
		  try{
			rs=dao.search(sql);		  
			  if(!rs.next()){
				  insertNewParameter(param_name);
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }		 
	}
	/**
	 * 插入
	 * @param param_name
	 */
	public void insertNewParameter(String param_name){
		String insert="insert into z03(z0301) values (?)";
		ArrayList list=new ArrayList();
		list.add(param_name.toUpperCase());			
		ContentDAO dao = new ContentDAO(conn);
		try{
			dao.insert(insert,list);		    
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	public String getNodeAttributeValue(String str_path,String attributeName){
		String value="";
		if(doc==null) {
            return value;
        }
		if(!"".equals(str_path)){
			try{
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0){
					element=(Element)childlist.get(0);
					value=element.getAttributeValue(attributeName);
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	
}
