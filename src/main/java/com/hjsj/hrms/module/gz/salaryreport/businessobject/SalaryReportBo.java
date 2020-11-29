package com.hjsj.hrms.module.gz.salaryreport.businessobject;

import com.hjsj.hrms.businessobject.general.muster.hmuster.CustomReportBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.dataview.businessobject.DataViewBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SalaryReportBo {

	private UserView userView;
	private Connection conn=null;
	private String salaryid="";
	private String rsid="";
	private String rsdtlid="";
	private RecordVo reportdetailvo=null;

	/**
	 * 汇总表 签名表 分组分页打印使用
	 * 数组内存为待分页数据每页的最后一行行号，
	 * 在合计行或总计行插入方法处初始化
	 * zhanghua 2017-3-14
	 */
	private ArrayList<Integer> groupPageNum=null;

	/**
	 * 获取分组行号
	 * @return
	 */
	public ArrayList<Integer> getGroupPageNum() {

		return groupPageNum;

	}
	public void setGroupPageNum(ArrayList<Integer> groupPageNum) {
		this.groupPageNum = groupPageNum;
	}

	/**
	 * 写入 分组打印行号
	 * @param i
	 */
	public void addGroupPageNum(int i) {

		if(groupPageNum!=null)
			this.groupPageNum.add(i);
	}

	public RecordVo getReportdetailvo() {
		return reportdetailvo;
	}
	public void setReportdetailvo(RecordVo reportdetailvo) {
		this.reportdetailvo = reportdetailvo;
	}
	public SalaryReportBo(Connection cn,String salaryid,UserView userView)
	{
		this.conn = cn;
		this.userView = userView;
		this.salaryid = salaryid;
	}
	/**
	 *
	 * @param cn
	 * @param salaryid
	 * @param userView
	 * @param rsid
	 * @param rsdtlid
	 * @param isExport 是否为导出数据使用 true 是
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public SalaryReportBo(Connection cn,String salaryid,UserView userView,String rsid, String rsdtlid,Boolean isExport) throws GeneralException, SQLException
	{
		this.conn = cn;
		this.userView = userView;
		this.salaryid = salaryid;
		this.rsid = rsid;
		this.rsdtlid = rsdtlid;
		if(!"4".equals(this.rsid)){
			ContentDAO dao=new ContentDAO(this.conn);
			reportdetailvo=new RecordVo("reportdetail");
			reportdetailvo.setInt("rsid",Integer.parseInt(rsid));
			reportdetailvo.setInt("rsdtlid",Integer.parseInt(rsdtlid));
			reportdetailvo=dao.findByPrimaryKey(reportdetailvo);
			if(isExport)
				groupPageNum=new ArrayList<Integer>();

		}
	}
	/**
	 * 取得薪资报表 项目定义列表
	 * @param salarySetList
	 * @return
	 */
	public ArrayList getReportSalarySet(String stid,String rsdtlid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn,Integer.parseInt(this.salaryid),userView);
			String a01z0Flag = salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");
			StringBuffer exsist=new StringBuffer("");
			ContentDAO dao=new ContentDAO(this.conn);
			if(rsdtlid!=null&&rsdtlid.trim().length()>0)
			{
				String sql="select itemid,itemdesc from reportitem where stid="+this.salaryid+" and rsdtlid="+rsdtlid+" order by sortid";
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
//					LazyDynaBean a_bean=new LazyDynaBean();
//					a_bean.set("itemid",rowSet.getString("itemid"));
//					a_bean.set("itemdesc",rowSet.getString("itemdesc"));
//					a_bean.set("isSelected","1");
//					list.add(a_bean);
					exsist.append(",'"+rowSet.getString("itemid")+"'");
				}
			}
			LazyDynaBean abean=null;
			String asql = "select * from salaryset where salaryid="+this.salaryid;
		   //去掉这个条件，使得排序按照salaryset表中的顺序来，不会出错
		    //	asql+=" and UPPER(itemid) not in("+exsist.toString().substring(1).toUpperCase()+") ";
		    asql+=" order by sortid";
			rowSet = dao.search(asql);
			while(rowSet.next()){
				String itemid=(String)rowSet.getString("itemid");
				String itemdesc=(String)rowSet.getString("itemdesc");
				String itemtype=(String)rowSet.getString("itemtype");
				String initflag=(String)rowSet.getString("initflag");
				String isSelected="0";

				//对于设置停发标识不显示的报表相对应不显示停发标识
				if(("A01Z0".equalsIgnoreCase(itemid) && !"1".equals(a01z0Flag)) || "NBASE".equalsIgnoreCase(itemid)|| "A0100".equalsIgnoreCase(itemid)|| "A0000".equalsIgnoreCase(itemid))
					continue;

				if("2".equals(stid))
				{
					if("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))
							continue;
				}
				if(("3".equals(stid)|| "13".equals(stid))&&(!"N".equals(itemtype)|| "A00Z1".equalsIgnoreCase(itemid)))
					continue;
				LazyDynaBean a_bean=new LazyDynaBean();
				if(!"3".equals(initflag))
				{
					if(!(userView.isSuper_admin()|| "1".equals(userView.getGroupId())))
			    	{
			    		if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				    		continue;
			    	}
				}
				a_bean.set("itemid",itemid);
				a_bean.set("itemdesc",itemdesc);
				if(exsist.toString().length()>0 && exsist.toString().contains(itemid))
					a_bean.set("isSelected","1");
				else
					a_bean.set("isSelected",isSelected);

				list.add(a_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}finally
		{
			if(rowSet!=null)
			{
				try
				{
					rowSet.close();
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
	 * 分组指标列表
	 * @param isEmpty
	 * @return
	 */
	public ArrayList getGroupItemList(String isEmpty,String salaryid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from salaryset where salaryid="+salaryid+" and codesetid<>'0'";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean bean = new LazyDynaBean();
			if("1".equals(isEmpty)){
				bean = new LazyDynaBean();
				bean.set("id", "0");
				bean.set("name", "(空)");
				list.add(bean);
			}
			while(rowSet.next())
			{
				bean = new LazyDynaBean();
				bean.set("id", rowSet.getString("itemid"));
				bean.set("name", rowSet.getString("itemdesc"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	public String getIsGroup(String str)
	{
		String value="0";

		try {
			if(str!=null&&str.trim().length()>0)
			{
				Document doc = PubFunc.generateDom(str);
				XPath xPath = XPath.newInstance("/param/group");
				Element out_fields = (Element) xPath.selectSingleNode(doc);
				if(out_fields!=null)
				{
			    	value=out_fields.getAttributeValue("pagebreak");
			    	if("true".equalsIgnoreCase(value))
			    		value="1";
			    	//在未点击分组分页打印的时候进去选择分组指标直接保存仍出现分组分页打印勾选出错   缺陷 28535
			    	else if("false".equalsIgnoreCase(value))
			    		value="0";
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}
	public String analyseXML(String xml)
    {
    	String ownertype="0";
    	try
    	{
    		if(xml==null|| "".equals(xml))
    		{
    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
    		}
    		Document doc = PubFunc.generateDom(xml);
			Element root=doc.getRootElement();
	    	XPath xpath=XPath.newInstance("/"+root.getName()+"/owner");
    		Element element=(Element)xpath.selectSingleNode(doc);
    		if(element==null)
    			return ownertype;
    		else{
    			ownertype = element.getAttributeValue("type");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return ownertype;
    }
	/**
	 * 保存或修改 报表定义
	 * @param reportStyleID  表类id
	 * @param reportDetailID 表id
	 * @param f_groupItem    一级分组指标
	 * @param s_groupItem    二级分组指标
	 * @param isPrintWithGroup 是否按分组指标
	 * @param salaryReportName 报表名称
	 * @param right_fields     报表项目
	 * @param isGroup          是否分组打印
	 * @author
	 * @return
	 */
	public String saveOrUpdateRecord(String reportStyleID,String reportDetailID,String f_groupItem,String s_groupItem,String isPrintWithGroup,String salaryReportName,ArrayList rightList,HashMap itemOrderMap,String isGroup,String ownerType,UserView userView)
	{
		String a_reportDetailID="";
		RowSet rowSet=null;
		try
		{
			String opt="edit";
			ContentDAO dao=new ContentDAO(this.conn);
			if("".equals(reportDetailID))
			{
				opt="new";
				reportDetailID=String.valueOf(DbNameBo.getPrimaryKey("reportdetail","rsdtlid",this.conn));  //取得主键值
			}else{
				//移动栏目设置表中的汇总项位置
				this.moveColumnIndexFromTable_scheme(reportDetailID,f_groupItem);
			}

			RecordVo detailVo=new RecordVo("reportdetail");
			detailVo.setInt("rsid",Integer.parseInt(reportStyleID));
			detailVo.setInt("rsdtlid",Integer.parseInt(reportDetailID));
			detailVo.setInt("stid",Integer.parseInt(this.salaryid));
			detailVo.setString("rsdtlname",salaryReportName);
			detailVo.setInt("leftmargin",20);
			detailVo.setInt("rightmargin",20);
			detailVo.setInt("topmargin",20);
			detailVo.setInt("bottommargin",20);
			 ReportPageOptionsBo rpob=null;
			if("edit".equals(opt))
			{
				detailVo=dao.findByPrimaryKey(detailVo);
				detailVo.setString("rsdtlname",salaryReportName);//修改名称  zhaoxg 2013-6-27
				rpob=new ReportPageOptionsBo(this.conn,userView,reportStyleID,reportDetailID);
				rpob.init();
			}

			if(!"1".equals(reportStyleID))
			{
				detailVo.setInt("bgroup",Integer.parseInt(isPrintWithGroup));
				detailVo.setString("fgroup",f_groupItem);
				if("3".equals(reportStyleID)|| "13".equals(reportStyleID))
					detailVo.setString("sgroup",s_groupItem);
				if("0".equals(isPrintWithGroup))
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
                        Element param = new Element("param");
				    	Element onwerT=new Element("owner");
				    	onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	param.addContent(onwerT);
				    	Document myDocument = new Document(param);
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(myDocument);
				    	detailVo.setString("ctrlparam", temp);
					}
				}
				else if(("2".equals(reportStyleID)|| "12".equals(reportStyleID))&& "1".equals(isPrintWithGroup))
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				        group=new Element("group");
			    		if("0".equals(isGroup))
			    			group.setAttribute("pagebreak","False");
			    		else
			    			group.setAttribute("pagebreak","True");
			    		root.addContent(group);

			    		xpath = XPath.newInstance("/param/leftfooter");
			    		Element leftfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(leftfooter!=null)
			    			root.removeChild("leftfooter");
			    		leftfooter=new Element("leftfooter");
				    	leftfooter.setAttribute("lastpageonly","False");

				    	xpath = XPath.newInstance("/param/centerfooter");
			    		Element centerfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(centerfooter!=null)
			    			root.removeChild("centerfooter");
		    			centerfooter=new Element("centerfooter");
			    		centerfooter.setAttribute("lastpageonly","False");

			    		xpath = XPath.newInstance("/param/rightfooter");
			    		Element rightfooter=(Element)xpath.selectSingleNode(rpob.doc);
			    		if(rightfooter!=null)
			    			root.removeChild("rightfooter");
			    		rightfooter=new Element("rightfooter");
			    		rightfooter.setAttribute("lastpageonly","False");
			    		root.addContent(leftfooter);
			    		root.addContent(centerfooter);
			    		root.addContent(rightfooter);
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
				    	Element param = new Element("param");
			    		Element group=new Element("group");
			    		if("0".equals(isGroup))
			    			group.setAttribute("pagebreak","False");
			    		else
			    			group.setAttribute("pagebreak","True");
			    		Element onwerT=new Element("owner");
				    	onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	param.addContent(onwerT);
				    	Element leftfooter=new Element("leftfooter");
				    	leftfooter.setAttribute("lastpageonly","False");
		    			Element centerfooter=new Element("centerfooter");
			    		centerfooter.setAttribute("lastpageonly","False");
			    		Element rightfooter=new Element("rightfooter");
			    		rightfooter.setAttribute("lastpageonly","False");
			    		param.addContent(group);
			    		param.addContent(leftfooter);
			    		param.addContent(centerfooter);
			    		param.addContent(rightfooter);
			    		Document myDocument = new Document(param);
			    		XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
			    		outputter.setFormat(format);
				    	String temp= outputter.outputString(myDocument);
			    		detailVo.setString("ctrlparam", temp);
					}
				}else
				{
					if(rpob!=null)
					{
						Element root = rpob.doc.getRootElement();
						XPath xpath = XPath.newInstance("/param/owner");
						Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
						if(onwerT!=null)
							root.removeChild("owner");
						onwerT=new Element("owner");
						onwerT.setAttribute("type", ownerType);
				    	onwerT.setText(userView.getUserName());
				    	root.addContent(onwerT);
				    	xpath = XPath.newInstance("/param/group");
				    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
				    	if(group!=null)
				    		root.removeChild("group");
				    	XMLOutputter outputter = new XMLOutputter();
				    	Format format = Format.getPrettyFormat();
				    	format.setEncoding("UTF-8");
				     	outputter.setFormat(format);
				    	String temp= outputter.outputString(rpob.doc);
				    	detailVo.setString("ctrlparam", temp);
					}
					else
					{
			    		Element param = new Element("param");
			    		Element onwerT=new Element("owner");
			    		onwerT.setAttribute("type", ownerType);
			    		onwerT.setText(userView.getUserName());
			    		param.addContent(onwerT);
			    		Document myDocument = new Document(param);
			    		XMLOutputter outputter = new XMLOutputter();
			    		Format format = Format.getPrettyFormat();
			    		format.setEncoding("UTF-8");
			    		outputter.setFormat(format);
			    		String temp= outputter.outputString(myDocument);
			    		detailVo.setString("ctrlparam", temp);
					}
				}
			}
			else
			{
				if(rpob!=null)
				{
					Element root = rpob.doc.getRootElement();
					XPath xpath = XPath.newInstance("/param/owner");
					Element onwerT = (Element)xpath.selectSingleNode(rpob.doc);
					if(onwerT!=null)
						root.removeChild("owner");
					onwerT=new Element("owner");
					onwerT.setAttribute("type", ownerType);
			    	onwerT.setText(userView.getUserName());
			    	root.addContent(onwerT);
			    	xpath = XPath.newInstance("/param/group");
			    	Element group = (Element)xpath.selectSingleNode(rpob.doc);
			    	if(group!=null)
			    		root.removeChild("group");
			    	XMLOutputter outputter = new XMLOutputter();
			    	Format format = Format.getPrettyFormat();
			    	format.setEncoding("UTF-8");
			     	outputter.setFormat(format);
			    	String temp= outputter.outputString(rpob.doc);
			    	detailVo.setString("ctrlparam", temp);
				}
				else
				{
				    Element param = new Element("param");
					Element onwerT=new Element("owner");
					onwerT.setAttribute("type", ownerType);
					onwerT.setText(userView.getUserName());
					param.addContent(onwerT);
					Document myDocument = new Document(param);
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					String temp= outputter.outputString(myDocument);
					detailVo.setString("ctrlparam", temp);
				    detailVo.setInt("bgroup",0);
				}
			}
			if("new".equals(opt))
				dao.addValueObject(detailVo);
			else
				dao.updateValueObject(detailVo);
			StringBuffer where_str=new StringBuffer("");
			for(int i=0;i<rightList.size();i++)
				where_str.append(",'"+rightList.get(i)+"'");
			HashMap beanMap=new HashMap();
			rowSet = dao.search("select itemid,itemdesc,nwidth,itemfmt,align,sortid from reportitem where  rsdtlid="+reportDetailID+" and stid="+this.salaryid);
			while(rowSet.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemdesc",rowSet.getString("itemdesc"));
				bean.set("nwidth", rowSet.getString("nwidth"));
				bean.set("itemfmt", rowSet.getString("itemfmt")==null?"":rowSet.getString("itemfmt"));
				bean.set("align", rowSet.getString("align"));
				beanMap.put(rowSet.getString("itemid").toUpperCase(), bean);
			}
			dao.delete("delete from reportitem where rsdtlid="+reportDetailID+" and stid="+this.salaryid,new ArrayList());
			rowSet=dao.search("select * from salaryset where salaryid="+this.salaryid+" and UPPER(itemid) in ("+where_str.substring(1).toUpperCase()+") order by sortid");
			ArrayList recordList=new ArrayList();
			int order=1;
			//dd
			HashMap map = new HashMap();
			while(rowSet.next())
			{
				String itemid=rowSet.getString("itemid").toUpperCase();
				RecordVo vo=new RecordVo("reportitem");
				vo.setInt("rsdtlid",Integer.parseInt(reportDetailID));
				vo.setInt("stid",Integer.parseInt(this.salaryid));
				vo.setString("itemid",rowSet.getString("itemid").toUpperCase());
				int nwidth=0;
				int align=0;
				String itemfmt="";
				String itemdesc="";
				if(beanMap.get(itemid)!=null)
				{
				    LazyDynaBean bean = (LazyDynaBean)beanMap.get(itemid);
				    itemdesc=(String)bean.get("itemdesc");
				    nwidth=Integer.parseInt(((String)bean.get("nwidth")));
				    itemfmt=(String)bean.get("itemfmt");
				    align=Integer.parseInt(((String)bean.get("align")));
				}
				else
				{
			    	String itemtype=rowSet.getString("itemtype");
			    	int    decwidth=rowSet.getInt("decwidth");
			        itemdesc=rowSet.getString("itemdesc");
				    nwidth=rowSet.getInt("nwidth");
			    	if("D".equals(itemtype))
			    	{
			    		align=2;
				    	String itemlength = rowSet.getString("itemlength");
				    	switch (Integer.parseInt(itemlength)) {
							case 4:
								itemfmt = "yyyy";
								break;
							case 7:
								itemfmt = "yyyy.MM";
								break;
							case 10:
								itemfmt = "yyyy.MM.dd";
								break;
							case 16:
								itemfmt = "yyyy.MM.dd HH:mm";
								break;
							case 18:
								itemfmt = "yyyy.MM.dd HH:mm:ss";
								break;
							default:
								itemfmt = "yyyy.MM.dd";
								break;
						}
			    	}
			    	if("N".equals(itemtype))
			    	{
				    	align=2;
				    	itemfmt="0";
				    	if(decwidth>0)
				    	{
				    		itemfmt+=".";
					    	for(int i=0;i<decwidth;i++)
					    		itemfmt+="0";
				    	}

		    		}
				}
				vo.setString("itemdesc",itemdesc);
		    	vo.setInt("nwidth",nwidth);
				vo.setString("itemfmt",itemfmt);
				vo.setInt("align",align);
				map.put(rowSet.getString("itemid").toUpperCase(), vo);
			}
			for(int i=0;i<rightList.size();i++)
			{
				String itid=(String) rightList.get(i);
				if(itid==null|| "".equals(itid))
					continue;
				RecordVo vo = (RecordVo)map.get(itid.toUpperCase());
				vo.setInt("sortid",order);
				order++;
				recordList.add(vo);
			}
			dao.addValueObject(recordList);
			a_reportDetailID=reportDetailID;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_reportDetailID;
	}

	/**
	 * 根据报表编号得到工资报表信息
	 * @param rsdtlid
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getGzDetailBydID(String rsdtlid)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from reportdetail where rsdtlid="+rsdtlid;
			RowSet rowSet=dao.search(sql);
			ResultSetMetaData metaData=rowSet.getMetaData();
			int columnCount=metaData.getColumnCount();
			if(rowSet.next())
			{

				for(int i=1;i<=columnCount;i++)
				{
					String columnName=metaData.getColumnName(i).toLowerCase();
					if(rowSet.getString(columnName)!=null)
						abean.set(columnName,rowSet.getString(columnName));
					else
						abean.set(columnName,"");
				}

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return abean;
	}
	/**
	 *
	 * @Title: getGzAnalyseHeadDesc
	 * @Description:人员工资结构分析表表头
	 * @param @param baseid 统计项目
	 * @param @param itemid 统计指标
	 * @param @return
	 * @param @throws GeneralException
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList getGzAnalyseHeadDesc(String baseid,String itemid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			String itemdesc="";
			String codesetid="";
			if("e0122".equalsIgnoreCase(baseid))
			{
				itemdesc="部门名称";
				codesetid="UM";
			}
			else if("b0110".equalsIgnoreCase(baseid))
			{
				itemdesc="单位名称";
				codesetid="UN";
			}
			else
			{
				FieldItem item=DataDictionary.getFieldItem(baseid.toLowerCase());
				itemdesc=item.getItemdesc();
				codesetid=item.getCodesetid();
			}
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			ColumnsInfo columnsInfo = this.getColumnsInfo("baseid",codesetid, itemdesc, "A", 0,"", "10", false);
			columnsInfo.setCodesetId(codesetid);
			list.add(columnsInfo);
			list.add(this.getColumnsInfo("countmen","0", "人数", "N", 0,"", "10", false));
			list.add(this.getColumnsInfo("sumnumber","0", "总额", "N", item==null?2:item.getDecimalwidth(),"", "10", false));
			list.add(this.getColumnsInfo("percent","0", "比例（%）", "N", 2,"", "10", false));
			list.add(this.getColumnsInfo("average","0", "平均值", "N", item==null?2:item.getDecimalwidth(),"", "10", false));
			list.add(this.getColumnsInfo("minnumber","0", "最低值", "N", item==null?2:item.getDecimalwidth(),"", "10", false));
			list.add(this.getColumnsInfo("maxnumber","0", "最高值", "N", item==null?2:item.getDecimalwidth(),"", "10", false));


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 *
	 * @Title: getColumnsInfo
	 * @Description: 组装表头对象
	 * @param @param columnId
	 * @param @param codesetid
	 * @param @param columnDesc
	 * @param @param type
	 * @param @param dec
	 * @param @return
	 * @param @throws GeneralException
	 * @return ColumnsInfo
	 * @author:zhaoxg
	 * @throws
	 */
	public ColumnsInfo getColumnsInfo(String columnId,String codesetid, String columnDesc,String type,int dec,String fieldsetid, String itemLength,Boolean bgroup) throws GeneralException{
		ColumnsInfo columnsInfo = new ColumnsInfo();
		try{
			columnsInfo.setColumnId(columnId);
			columnsInfo.setColumnDesc(columnDesc);
			if(bgroup)
				columnsInfo.setCodesetId("0");
			else
				columnsInfo.setCodesetId(codesetid);
			columnsInfo.setColumnType(type);// 类型N|M|A|D
			if("N".equalsIgnoreCase(type))
				columnsInfo.setDefaultValue("0");

			if(!"人数".equals(columnDesc))
				columnsInfo.setColumnWidth(columnDesc.length()*20<100?100:columnDesc.length()*20);//显示列宽
			else
				columnsInfo.setColumnWidth(90);//显示列宽
			//这里为了使日期按指定的格式显示
			if(!StringUtils.isBlank(itemLength) && !"-1".equals(itemLength))
				columnsInfo.setColumnLength(Integer.parseInt(itemLength));// 显示长度
			else
				columnsInfo.setColumnLength(10);// 显示长度
			columnsInfo.setDecimalWidth(dec);// 小数位
			columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
			columnsInfo.setReadOnly(true);// 是否只读
			columnsInfo.setLocked(false);//是否锁列
			if(fieldsetid!=null&&fieldsetid.length()>0)
				columnsInfo.setFieldsetid(fieldsetid);
			else
				columnsInfo.setFieldsetid("");
			if(("3".equals(rsid)|| "13".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid)) && bgroup)
				columnsInfo.setRendererFunc("openSalaryReportScope.renderTo");
	    	if("D".equals(type) || "N".equals(type)|| "A00z1".equalsIgnoreCase(columnId)|| "A00z3".equalsIgnoreCase(columnId))//发放次数 归属次数右对齐
	    		columnsInfo.setTextAlign("right");

	    	if(("4".equals(rsid)|| "14".equals(rsid))&& "baseid".equalsIgnoreCase(columnId))
	    		columnsInfo.setRendererFunc("openSalaryReportScope.renderBaseid");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return columnsInfo;
	}
	public ColumnsInfo getColumnsInfo(String columnId,String codesetid, String columnDesc,String type,int columnWidth,int columnLength,boolean Locked,int dec,String fieldsetid) throws GeneralException{
		ColumnsInfo columnsInfo = new ColumnsInfo();
		try{
			columnsInfo.setColumnId(columnId);
			columnsInfo.setColumnDesc(columnDesc);
			columnsInfo.setCodesetId(codesetid);
			columnsInfo.setColumnType(type);// 类型N|M|A|D

			columnsInfo.setColumnWidth(columnWidth);//显示列宽

			columnsInfo.setColumnLength(columnLength);// 显示长度
			columnsInfo.setDecimalWidth(dec);// 小数位
			columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
			columnsInfo.setReadOnly(true);// 是否只读
			columnsInfo.setLocked(Locked);//是否锁列
			if(fieldsetid!=null&&fieldsetid.length()>0)
				columnsInfo.setFieldsetid(fieldsetid);
			else
				columnsInfo.setFieldsetid("");
			if("3".equals(rsid)|| "13".equals(rsid))
				columnsInfo.setRendererFunc("openSalaryReportScope.renderTo");
	    	if("D".equals(type) || "N".equals(type))
	    		columnsInfo.setTextAlign("right");

	    	if(("4".equals(rsid)|| "14".equals(rsid))&& "baseid".equalsIgnoreCase(columnId))
	    		columnsInfo.setRendererFunc("openSalaryReportScope.renderBaseid");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return columnsInfo;
	}





	public ArrayList<ColumnsInfo> getColumnsInfo(LinkedHashMap columnMap,String rsid,boolean bgroup){
		ArrayList<ColumnsInfo> columnsItem=new ArrayList<ColumnsInfo>();
		RowSet rs=null;
		try{
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn,Integer.parseInt(this.salaryid),userView);
			String a01z0Flag = salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");
			ContentDAO dao=new ContentDAO(this.conn);
			//若存在私有方案 则取私有，否则取公有
			String strSql= "select * from t_sys_table_scheme_item where scheme_id = ";
			strSql+= Sql_switcher.isnull(" (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salaryreport_"+rsdtlid+"' and is_share = '0' and username = '" + this.userView.getUserName() + "') "
					, " (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salaryreport_"+rsdtlid+"' and is_share = '1' ) ");
			strSql+= "  order by displayorder";
			rs=dao.search(strSql);
			if(rs.next())
				rs.beforeFirst();
			else {//如果没有私有或者共有栏目设置，则取薪资发放的私有或者共有发放  sunjian 2017-7-1
				strSql = "select * from t_sys_table_scheme_item where scheme_id = ";
				strSql += Sql_switcher.isnull(" (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salary_" + this.salaryid + "' and is_share = '0' and username = '" + this.userView.getUserName() + "') "
						, " (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salary_" + this.salaryid + "' and is_share = '1' ) ");
				strSql += "  order by displayorder";
				rs=dao.search(strSql);
			}
			while(rs.next()){
				FieldItem field=(FieldItem)columnMap.get(rs.getString("itemid").toLowerCase());
				if(field==null)
					continue;

				String Locked=rs.getString("is_lock")==null?"0":rs.getString("is_lock");
				//columnsItem.add(this.getColumnsInfo(rs.getString("itemid").toUpperCase(), field.getCodesetid(), StringUtils.isBlank(rs.getString("itemdesc"))?field.getItemdesc():rs.getString("itemdesc"), field.getItemtype(), Integer.parseInt(rs.getString("displaywidth")), 20, lick.equals("0")?false:true, 2, field.getFieldsetid()));
				ColumnsInfo columnsInfo = new ColumnsInfo();

				columnsInfo.setColumnId(rs.getString("itemid").toUpperCase());

				columnsInfo.setColumnDesc(StringUtils.isBlank(rs.getString("displaydesc"))?field.getItemdesc():rs.getString("displaydesc"));
				columnsInfo.setColumnRealDesc(field.getItemdesc());//原数据字典中列名。
				if(bgroup){
					columnsInfo.setCodesetId("0");
					columnsInfo.setFieldsetid("");
				}
				else
					columnsInfo.setCodesetId(field.getCodesetid());
				columnsInfo.setColumnType(field.getItemtype());// 类型N|M|A|D

				if("A00Z3".equalsIgnoreCase(field.getItemid())) {//发放次数的统计没必要是0
					columnsInfo.setDefaultValue("");
				}else if("N".equalsIgnoreCase(field.getItemtype()))
					columnsInfo.setDefaultValue("0");

				columnsInfo.setOrdertype(rs.getString("is_order"));
				columnsInfo.setColumnWidth(rs.getInt("displaywidth"));//显示列宽
				columnsInfo.setColumnLength(field.getItemlength());// 显示长度
				columnsInfo.setDecimalWidth(field.getDecimalwidth());// 小数位
				columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
				columnsInfo.setReadOnly(true);// 是否只读
				if(!"1".equals(rs.getString("is_display")))
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
				if("iscollect".equalsIgnoreCase(rs.getString("itemid")))
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
				if("signature".equalsIgnoreCase(rs.getString("itemid"))){
					columnsInfo.setFilterable(false);//取消过滤
					columnsInfo.setSortable(false);//取消排序
				}
				columnsInfo.setLocked("0".equals(Locked)?false:true);//是否锁列
				if(field.getFieldsetid()!=null&&field.getFieldsetid().length()>0)
					columnsInfo.setFieldsetid(field.getFieldsetid());
				else
					columnsInfo.setFieldsetid("");
				if(("3".equals(rsid)|| "13".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid)) && bgroup)
					if("n".equalsIgnoreCase(columnsInfo.getColumnType())&&columnsInfo.getDecimalWidth()>0)
						columnsInfo.setRendererFunc("openSalaryReportScope.numRenderTo");
					else
						columnsInfo.setRendererFunc("openSalaryReportScope.renderTo");
				String align=rs.getString("ALIGN")==null?"":rs.getString("ALIGN");
				if("1".equals(align))
					columnsInfo.setTextAlign("left");
				else if("2".equals(align))
					columnsInfo.setTextAlign("center");
				else if("3".equals(align))
					columnsInfo.setTextAlign("right");
		    	if(("4".equals(rsid)|| "14".equals(rsid))&& "baseid".equalsIgnoreCase(rs.getString("itemid")))
		    		columnsInfo.setRendererFunc("openSalaryReportScope.renderBaseid");
				columnsItem.add(columnsInfo);

			}
			if(columnsItem.size()!=0&&("3".equals(rsid)|| "13".equals(rsid))){
				ColumnsInfo column = this.getColumnsInfo("iscollect", "0", "是否是合计行", "A", 0,"","10",false);//用来判断是否改改变行颜色
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
				columnsItem.add(column);
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return columnsItem;
	}



	/**
	 *
	 * @Title: getGzAnalyseList
	 * @Description:人员工资结构分析表数据获取
	 * @param @param rsid 表类号
	 * @param @param baseid  统计项目
	 * @param @param itemid  统计指标
	 * @param @param tableName
	 * @param @param privsql 权限sql
	 * @param @return
	 * @return HashMap
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList getGzAnalyseList(String rsid,String baseid,String itemid,String tableName,String privsql,HashMap<String, Double> hm)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=null;
			double sumNumber=0d;
			String a_sql="select count(a0100)  from "+tableName+" where 1=1 "+privsql;
			RowSet rowSet=dao.search(a_sql);
			if(rowSet.next())
				sumNumber=rowSet.getDouble(1);

			StringBuffer sql=new StringBuffer("select (case when max(" + baseid + ")='' then '空' when max(" + baseid + ") is null then '空' else max(" + baseid + ") end) baseid,count(a0100) countmen,sum("+itemid+") sumnumber,");
			if(sumNumber!=0)
				sql.append("count(a0100)/"+sumNumber+"*100 percentnum");
			else
				sql.append("0 percentnum");//lyd 数据为空时sql报错 原：percent 识别为关键字 改：percentnum
			sql.append(",sum("+itemid+")/count(a0100) average,min("+itemid+") minnumber,max("+itemid+") maxnumber");
			sql.append(" from "+tableName+" where 1=1 "+privsql);
			if (Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2) {
				sql.append(" group by rollup("+baseid+")");
			} else {
				sql.append(" group by "+Sql_switcher.isnull(baseid, "''")+" with rollup ");
			}
			rs=dao.search(sql.toString());
			LazyDynaBean bean;
			BigDecimal decimal;
			//用来获取数据库中应该保留几位小数
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			while(rs.next()){
				bean=new LazyDynaBean();
				bean.set("baseid", rs.getString("baseid"));
				bean.set("countmen", rs.getDouble("countmen"));
				bean.set("sumnumber", rs.getDouble("sumnumber"));
				decimal=new BigDecimal(rs.getDouble("percentnum"));
				bean.set("percent", decimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
				bean.set("average",item == null ? String.format("%.2f", rs.getDouble("average")) : String.format("%." + item.getDecimalwidth() + "f", rs.getDouble("average")) );
				bean.set("minnumber", rs.getDouble("minnumber"));
				bean.set("maxnumber", rs.getDouble("maxnumber"));
				if(!rs.isLast())
					hm.put(rs.getString("baseid"), decimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
				dataList.add(bean);

			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataList;
	}


	/**
	 *
	 * @Title: getReportItem
	 * @Description:取得表类(数字类 or 代码)类型报表项目(人员结构工资分析表)
	 * @param @param flag
	 * @param @return
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList getReportItem(String flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if("N".equals(flag))
				sql="select itemid,itemdesc from salaryset where salaryid="+this.salaryid+" and itemtype='N' and UPPER(itemid)<>'A0000' and UPPER(itemid)<>'A00Z1' and UPPER(itemid)<>'A00Z3'  order by sortid";
			if("A".equals(flag))
				sql="select itemid,itemdesc from salaryset where salaryid="+this.salaryid+" and itemtype='A' and codesetid<>'0'  order by sortid";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				if("0".equals(this.userView.analyseFieldPriv(rowSet.getString("itemid"))))
					continue;
				abean=new LazyDynaBean();
				abean.set("value",rowSet.getString("itemid"));
				abean.set("name",rowSet.getString("itemdesc"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 *
	 * @Title: getTableHeadDescList
	 * @Description:取得表头描述
	 * @param @param rsid 报表种类编号
	 * @param @param rsdtlid 报表编号
	 * @param @return
	 * @param @throws GeneralException
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList getTableHeadDescList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn,Integer.parseInt(this.salaryid),userView);
			String a01z0Flag = salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select a.itemid,a.itemdesc,a.itemfmt,b.itemtype,b.codesetid,a.nwidth,a.align,c.initflag,b.decimalwidth,b.fieldsetid,b.itemlength from reportitem a left join fielditem b on UPPER(a.itemid)=UPPER(b.itemid) left join salaryset c on UPPER(a.itemid)=UPPER(c.itemid) and a.stid=c.salaryid where  a.stid="+this.salaryid+"  and a.Rsdtlid="+rsdtlid+"  order by a.sortid";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				String itemid=rowSet.getString("itemid").toLowerCase();
				//薪资属性设置不显示停发标识，报表对应不显示
				if("A01Z0".equalsIgnoreCase(itemid) && !"1".equals(a01z0Flag))
					continue;

				int initFlag=rowSet.getInt("initflag");
				if(initFlag!=3)
				{
			    	if(!(userView.isSuper_admin()|| "1".equals(userView.getGroupId())))
			    	{
			    		if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				    		continue;
			    	}
				}
				abean=new LazyDynaBean();
				abean.set("itemid",itemid);
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				String itemtype="A";
				String codesetid="0";
				String itemfmt="";
				if(rowSet.getString("itemfmt")!=null)
					itemfmt=rowSet.getString("itemfmt");
				if("a00z0".equals(itemid)|| "a00z2".equals(itemid))
				{
					itemtype="D";
				}
				else if("a00z1".equals(itemid)|| "a00z3".equals(itemid))
				{
					itemtype="A";
				}
				else if(rowSet.getString("itemtype")!=null)
				{
					itemtype=rowSet.getString("itemtype");
					codesetid=rowSet.getString("codesetid");
				}

				if("E01A1".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="@K";
				}
				else if("E0122".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="UM";
				}
				else if("B0110".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="UN";
				}
				else if("A01Z0".equalsIgnoreCase(itemid))
				{
					itemtype="A";
					codesetid="ZZ";
				}
				abean.set("itemtype",itemtype);
				abean.set("itemlength",rowSet.getString("itemlength") == null ? "-1" : rowSet.getString("itemlength"));
				abean.set("codesetid",codesetid);
				abean.set("itemfmt",itemfmt);
				abean.set("nwidth", rowSet.getString("nwidth"));
				abean.set("align",rowSet.getString("align"));
				abean.set("decimalwidth",rowSet.getString("decimalwidth")==null?"0":rowSet.getString("decimalwidth"));
				abean.set("fieldsetid", rowSet.getString("fieldsetid")==null?"":rowSet.getString("fieldsetid"));
				list.add(abean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 *
	 * @Title: getRecordList
	 * @Description:获取工资条，签名表，汇总表获取页面显示数据
	 * @param @param tablename
	 * @param @param privsql 权限sql
	 * @param @param groupValues 分组值
	 * @param @param model model=0工资发放进入，=1工资审批进入，=3，4是工资历史数据进入  3表示未归档 4表示归档。
	 * @param @return
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList getRecordList(String tablename,String privsql,String groupValues,String model)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

		    LazyDynaBean groupBean=null;
		    if("3".equals(rsid)|| "13".equals(rsid))
			    groupBean=getGroupBean();
			ArrayList tabHeadList=getTableHeadDescList();
			HashMap  groupSumRecords=new HashMap();    //分组汇总数据

			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("salary_"+salaryid);
			if(StringUtils.equalsIgnoreCase(model,"3") || StringUtils.equalsIgnoreCase(model,"4")){ //薪资历史数据
				tableCache = (TableDataConfigCache)userView.getHm().get("salaryHistory_"+salaryid);
			}
			String sortSql = tableCache.getSortSql().toLowerCase().replace("order by", "").trim();

			if("3".equals(rsid)|| "13".equals(rsid))
				groupSumRecords=this.getGroupSumRecord(groupValues, groupBean, tablename, privsql, tabHeadList);
			RowSet rowSet=null;
			StringBuffer buf_b = new StringBuffer("");

			String sql=this.getReportSql(groupValues, tabHeadList, groupBean, tablename, privsql);
			String order_str="";
			String groupField="";
			if(("2".equals(rsid)|| "12".equals(rsid))&&reportdetailvo.getInt("bgroup")==1)
			{
				groupField=reportdetailvo.getString("fgroup").toLowerCase();//签名表的分组指标
				//如果不加这个，导致页面排序指标和分组指标一致报错
				order_str=groupField;
			}
			else if("3".equals(rsid)|| "13".equals(rsid))
			{
				 String f_groupItem=(String)groupBean.get("f_groupItem");//第一分组词
				 String s_groupItem=(String)groupBean.get("s_groupItem");//第二分组词

				 if(f_groupItem!=null&&f_groupItem.trim().length()>0)
				 {
					 order_str+=f_groupItem+",";
				 }
				 if(s_groupItem!=null&&s_groupItem.trim().length()>0&&order_str.toUpperCase().indexOf(s_groupItem.toUpperCase())==-1)
				 {
					 order_str+=s_groupItem+",";
				 }
				 if(order_str.endsWith(","))
					 order_str = order_str.substring(0,order_str.length()-1);
			}
			else
			{
				//没有分组的情况，按照薪资表的顺序展示
				order_str=sortSql;
			}
			if(order_str.toUpperCase().indexOf(groupField.toUpperCase())!=-1&&groupField!=null&&!"".equals(groupField))
			{

			}
			else if(groupField!=null&&!"".equals(groupField))
			{
				order_str=" "+groupField + "," + sortSql;;
			}
			String group="";
			group=reportdetailvo.getString("fgroup").toLowerCase();
			if(!order_str.contains("b0110") && group != null && !"".equals(group) && "e0122".equalsIgnoreCase(group))
				rowSet=dao.search(sql+" order by "+  " b0110," + order_str);
			else
				rowSet=dao.search(sql+" order by "+order_str);
			boolean flag=true;
		    int num=0;
		    LazyDynaBean abean=null;
		    SimpleDateFormat df=null;
		    StringBuffer sumStr=new StringBuffer("");
		    boolean bool=false;
		    //-----------------------------------------------------------------
		    if(("2".equals(rsid)|| "12".equals(rsid))&&reportdetailvo.getInt("bgroup")==1)
		    {
		        group=reportdetailvo.getString("fgroup").toLowerCase();
		        boolean isB0110=false;
		    	LazyDynaBean t_bean=null;
		    	for(int i=0;i<tabHeadList.size();i++)
		    	{
		    		t_bean = (LazyDynaBean)tabHeadList.get(i);
		    		String itemid=(String)t_bean.get("itemid");
		    		if(itemid.equalsIgnoreCase(group))
		    		{
		    			bool = true;
		    		}else if("b0110".equalsIgnoreCase(itemid))
		    		{
		    			isB0110=true;
		    		}
		    		else
		    		{
		    			continue;
		    		}
		    	}
		    	if(!bool)
		    	{
		    		tabHeadList.add(this.getGroupBean2(group));
		    	}
		    	if("e0122".equalsIgnoreCase(group)&&!isB0110)
		    		tabHeadList.add(this.getGroupBean2("b0110"));
		    }
           //-------------------------------------------------------------------
		    String f_groupValue="key";
		    int a=0;
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			//将查找出的值放入list中
			while(rowSet.next()){
				num++;
				LazyDynaBean headBean=new LazyDynaBean();
				abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String aitemtype=(String)headBean.get("itemtype");
					String acodesetid=(String)headBean.get("codesetid");
					String itemfmt=(String)headBean.get("itemfmt");
					String value="";
					if("D".equalsIgnoreCase(aitemtype))
					{
						value=rowSet.getDate(aitemid)==null?"":rowSet.getDate(aitemid).toString();
					}
					else{
						value=rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid);
					}
					if(value==null&& "".equals(value))
					{
						abean.set(aitemid,"");
					}
					else
					{

						if("M".equals(aitemtype))
							abean.set(aitemid,Sql_switcher.readMemo(rowSet,aitemid));
						else
						{
							if("D".equals(aitemtype))
							{
								if("A00Z2".equalsIgnoreCase(aitemid))
									abean.set(aitemid,value);
								else {
									df=new SimpleDateFormat(itemfmt);
									abean.set(aitemid,rowSet.getDate(aitemid)==null?"":df.format(rowSet.getDate(aitemid)));
								}
							}
							else if("A".equals(aitemtype)&&!"0".equals(acodesetid))
							{
					    		abean.set(aitemid,rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
							}
							else if("N".equals(aitemtype))
							{
								if(rowSet.getString(aitemid)==null||Float.parseFloat(rowSet.getString(aitemid))==0)
									abean.set(aitemid,"");
								else
								{
									if(itemfmt==null||itemfmt.length()==0)
										abean.set(aitemid,rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
									else
									{
										DecimalFormat myformat =null;
										if(itemfmt.indexOf(".")==-1)
										{
											abean.set(aitemid,rowSet.getString(aitemid)==null?"":PubFunc.round(rowSet.getString(aitemid), 0));
										}
										else
										{
											if(itemfmt.substring(2).length()==1)
												myformat = new DecimalFormat("0.0");
											else if(itemfmt.substring(2).length()==2)
												myformat = new DecimalFormat("0.00");
											else if(itemfmt.substring(2).length()==3)
												myformat = new DecimalFormat("0.000");
											else if(itemfmt.substring(2).length()==4)
												myformat = new DecimalFormat("0.0000");
											abean.set(aitemid,myformat.format(rowSet.getDouble(aitemid)));
										}
									}
								}
							}
							else
								abean.set(aitemid,rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
						}

					}
					if(a==0&& "N".equals(aitemtype) )
						sumStr.append(",sum("+aitemid+") "+aitemid);


				}
				a++;
				if("3".equals(rsid)|| "13".equals(rsid))
				{
					 String f_groupItem=(String)groupBean.get("f_groupItem");
					 String s_groupItem=(String)groupBean.get("s_groupItem");
					 abean.set("num",rowSet.getString("num"));
					 String groupValue=rowSet.getString(f_groupItem);
					 resetGroupName(abean,rowSet,groupBean);
					 if(s_groupItem.length()>0)
					 {
						 if("key".equals(f_groupValue))
							 f_groupValue=groupValue;
						 if(f_groupValue!=null&&groupValue!=null&&!f_groupValue.equals(groupValue) && groupSumRecords.size() > 0)//逐条遍历，当值不一样的时候说明加入第二分组词的小合计
						 {
							 list.add((LazyDynaBean)groupSumRecords.get(f_groupValue));//在处理不一样的数据前插入前面几条记录的小合计
							 addGroupPageNum(list.size()-1);//插入合计行 行号
							 f_groupValue=groupValue;
						 }
					 }
				}
				if("2".equals(rsid)|| "12".equals(rsid))
				{
					abean.set("signature"," ");//插入签名列
					abean.set("iscollect","0");//插入标记列（是否加颜色等）
				}
			    list.add(abean);
			}
			if("3".equals(rsid)|| "13".equals(rsid))  //总计
			{
				if(!"key".equals(f_groupValue) && groupSumRecords.size() > 0){
					if("-1".equals(f_groupValue)){
						f_groupValue="";
					}
					list.add((LazyDynaBean)groupSumRecords.get(f_groupValue));
					list.add((LazyDynaBean)groupSumRecords.get("totals"));
					addGroupPageNum(list.size()-1);//插入合计行 行号
				}
				else if("key".equals(f_groupValue)&&groupSumRecords.get("totals")!=null)
					list.add((LazyDynaBean)groupSumRecords.get("totals"));
			}
			else if(("2".equals(rsid)|| "12".equals(rsid)))  //总计
			{
				StringBuffer p=new StringBuffer("");
				p.append(buf_b.toString());
				//总计，合计的增加
				if(("2".equals(rsid)|| "12".equals(rsid))&&reportdetailvo.getInt("bgroup")==1)
				{
					list=this.resetList(sumStr.toString(), list, tablename, tabHeadList, privsql, group);
				}
				else if(sumStr.length()>0)
				{
					StringBuffer buf=new StringBuffer(" from "+tablename+" where 1=1 "+p.toString());
					buf.append(privsql);
					rowSet=dao.search("select "+sumStr.substring(1)+buf.toString()+p.toString());
					LazyDynaBean headBean=new LazyDynaBean();
					if(rowSet.next())
					{
						abean=new LazyDynaBean();
						for(int i=0;i<tabHeadList.size();i++){
							headBean=(LazyDynaBean)tabHeadList.get(i);
							String aitemid=(String)headBean.get("itemid");
							String aitemtype=(String)headBean.get("itemtype");
							String value="";
							if("N".equals(aitemtype))
							{
								if(rowSet.getString(aitemid)!=null)
									value=rowSet.getString(aitemid);
							}
							abean.set(aitemid,value);
						}


					}
					abean.set("signature"," ");
					list.add(abean);
				}

				if(!StringUtils.isBlank(groupField)){//将汇总指标列转为文字 方便显示总计 合计。
					FieldItem fm=DataDictionary.getFieldItem(groupField);
					String codesetid=fm.getCodesetid();
					if(!StringUtils.isBlank(codesetid)){
						for(int i=0;i<list.size();i++){
							LazyDynaBean tempbean=(LazyDynaBean)list.get(i);
							String content="";
							String value=tempbean.get(groupField).toString();
							content=value;

							//将组织机构代码 转换成名称
							if("合计".equals(value) || "总计".equals(value)) {
								content = value;
							}
						    if("un".equalsIgnoreCase(codesetid)){
						        content = AdminCode.getCodeName("UN", value);
						        if(StringUtils.isBlank(content))
						            content = AdminCode.getCodeName("UM", value);
						    }
						    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
							{
								if("e0122".equalsIgnoreCase(groupField))
								{
									if(Integer.parseInt(display_e0122)==0)
										content=AdminCode.getCodeName("UM",value);
									else
									{
										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		content=item.getCodename();
						        		}
						    	    	else
						    	    	{
						    	    		content=AdminCode.getCodeName("UM",value);
						    	    	}
									}
								}else
									content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
							}else {
								content=AdminCode.getCodeName(codesetid,value);
							}

						    if(content==null || "".equals(content))
						    	content=value;


						    tempbean.set( groupField, content );



						}
					}
				}



			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return list;
	}
	/**
	 * 取得工资汇总表的分组信息
	 * @param rsid
	 * @param rsdtlid
	 * @return
	 */
	public LazyDynaBean getGroupBean()
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
		    String f_groupItem="";
		    String s_groupItem="";
		    String f_groupDesc="";
		    String s_groupDesc="";
		    String f_codesetid="";
		    String s_codesetid="";
			f_groupItem=(String)reportdetailvo.getString("fgroup").toLowerCase();
			s_groupItem=(String)reportdetailvo.getString("sgroup").toLowerCase();
			if(f_groupItem!=null&& "b0110".equalsIgnoreCase(f_groupItem))
				f_codesetid="UN";
			else if(f_groupItem!=null&& "e0122".equalsIgnoreCase(f_groupItem))
				f_codesetid="UM";
			if(s_groupItem!=null&& "b0110".equalsIgnoreCase(s_groupItem))
				s_codesetid="UN";
			else if(s_groupItem!=null&& "e0122".equalsIgnoreCase(s_groupItem))
				s_codesetid="UM";
			if(f_groupItem!=null&&f_groupItem.length()>0&&!"UN".equalsIgnoreCase(f_groupItem)&&!"UM".equalsIgnoreCase(f_groupItem))
		    {
	    		FieldItem item=DataDictionary.getFieldItem(f_groupItem.toLowerCase());
	    		f_codesetid=item.getCodesetid();
	    		f_groupDesc=item.getItemdesc();
		    }
		    if(s_groupItem!=null&&s_groupItem.length()>0&&!"UN".equalsIgnoreCase(s_groupItem)&&!"UM".equalsIgnoreCase(s_groupItem))
		    {
	    		FieldItem item=DataDictionary.getFieldItem(s_groupItem.toLowerCase());
	    		s_codesetid=item.getCodesetid();
	    		s_groupDesc=item.getItemdesc();
		    }
		    abean.set("f_groupItem", f_groupItem);
		    abean.set("s_groupItem", s_groupItem);
		    abean.set("f_codesetid", f_codesetid);
		    abean.set("s_codesetid", s_codesetid);
		    abean.set("f_groupDesc",f_groupDesc);
		    abean.set("s_groupDesc", s_groupDesc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	/**
	 *
	 * @Title: getGroupSumRecord
	 * @Description:求合计
	 * @param @param groupValues 分组范围
	 * @param @param groupBean 分组指标
	 * @param @param tablename
	 * @param @param privsql 权限sql
	 * @param @param tabHeadList
	 * @param @return
	 * @return HashMap
	 * @author:zhaoxg
	 * @throws
	 */
	public HashMap getGroupSumRecord(String groupValues,LazyDynaBean groupBean,String tablename,String privsql,ArrayList tabHeadList)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String f_groupItem=(String)groupBean.get("f_groupItem");
			String f_codesetid=(String)groupBean.get("f_codesetid");
			ArrayList<String> unitList=new ArrayList<String>();
			StringBuffer buf=new StringBuffer("");
			LazyDynaBean headBean=null;
			for(int i=0;i<tabHeadList.size();i++)
			{
				headBean=(LazyDynaBean)tabHeadList.get(i);
				String aitemid=(String)headBean.get("itemid");
				if("a00z1".equalsIgnoreCase(aitemid)|| "a00z3".equalsIgnoreCase(aitemid))
					buf.append(",max("+aitemid+") "+aitemid);
				else
				    buf.append(",sum("+aitemid+") "+aitemid);
			}
			String whl="";
			if(groupValues!=null&&groupValues.length()>0)
			{
				String[] temps=groupValues.split(",");
				StringBuffer buffer2=new StringBuffer("");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						String[] temps_2 = PubFunc.keyWord_reback(temps[i]).split("/");
						if("E0122".equalsIgnoreCase(f_groupItem))
						{
							if("UN".equalsIgnoreCase(temps_2[0])){
								unitList.add(temps_2[1]);
							}
						}
						buffer2.append(",'"+temps_2[1]+"'");
					}
				}
				if(buffer2.toString().trim().length()>0)
	    			whl=" and "+f_groupItem+" in ("+buffer2.substring(1)+") ";
				for(String str:unitList){
					whl+=" or "+f_groupItem+" like '"+str+"%' ";
	    		}
			}
			StringBuffer temp_sql=new StringBuffer("");
			String tp="";
			if(buf.toString().length()>0)
				tp=buf.substring(1)+",";
			temp_sql.append("select "+tp);
			temp_sql.append("count(a0100) num,"+f_groupItem);
			temp_sql.append(" from "+tablename+" where 1=1 "+privsql+" "+whl);
			temp_sql.append(" group by ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				temp_sql.append(" rollup ("+f_groupItem+")");
			}
			else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
			{
				temp_sql.append(f_groupItem+" with rollup");
			}
			RowSet rowSet=dao.search(temp_sql.toString());
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String itemtype=(String)headBean.get("itemtype");
					String itemfmt=(String)headBean.get("itemfmt");
					if(rowSet.getString(aitemid)!=null)
					{
						if("N".equals(itemtype))
						{
							if(Float.parseFloat( rowSet.getString(aitemid))==0)
								abean.set(aitemid, "");
							else
							{
								if(itemfmt==null||itemfmt.length()==0)
									abean.set(aitemid,rowSet.getString(aitemid));
								else
								{
									DecimalFormat myformat =null;
									if(itemfmt.indexOf(".")==-1)
									{
										abean.set(aitemid,PubFunc.round(rowSet.getString(aitemid), 0));
									}
									else
									{
										if(itemfmt.substring(2).length()==1)
											myformat = new DecimalFormat("0.0");
										else if(itemfmt.substring(2).length()==2)
											myformat = new DecimalFormat("0.00");
										else if(itemfmt.substring(2).length()==3)
											myformat = new DecimalFormat("0.000");
										else if(itemfmt.substring(2).length()==4)
											myformat = new DecimalFormat("0.0000");
										abean.set(aitemid,myformat.format(rowSet.getDouble(aitemid)));
									}
								}
							}
						}
						else if("A00Z3".equalsIgnoreCase(aitemid) || "A00Z1".equalsIgnoreCase(aitemid))
							abean.set(aitemid, "");
						else
							abean.set(aitemid, rowSet.getString(aitemid)==null?"":rowSet.getString(aitemid));
					}
					else
						abean.set(aitemid,"");
				}
				if(rowSet.getString("num")!=null&&Float.parseFloat(rowSet.getString("num"))!=0)
					abean.set("num",rowSet.getString("num")); //人数
				else
					abean.set("num",""); //人数
				String mapKey="-1";
				if(rowSet.getString(f_groupItem)!=null)
					mapKey=rowSet.getString(f_groupItem);
				else if(rowSet.isLast()){
					mapKey="totals";
				}
				if(rowSet.getString(f_groupItem)!=null)
				{
					if("UN".equalsIgnoreCase(f_codesetid)|| "UM".equalsIgnoreCase(f_codesetid))
					{
						String desc=AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem));
						if((desc==null|| "".equals(desc))&& "UN".equalsIgnoreCase(f_codesetid))
							desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
						if((desc==null|| "".equals(desc))&& "UM".equalsIgnoreCase(f_codesetid))
							desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
						abean.set(f_groupItem,desc);
					}
					else
				    	abean.set(f_groupItem,AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem)));
				}
				else if(rowSet.isLast())
					abean.set(f_groupItem,"总计");
				else
					abean.set(f_groupItem,"未维护项");
				abean.set("iscollect","1");//0：不是合计或小计行，1：是合计或小计行，需要变色
				map.put(mapKey,abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 *
	 * @Title: getReportSql
	 * @Description: 获取工资条，签名表，汇总表数据sql
	 * @param @param groupValues 汇总表的分组范围
	 * @param @param tabHeadList 表头列
	 * @param @param groupBean 工资汇总表的分组信息  第一分组词or第二分组词
	 * @param @param tablename 临时表or历史表
	 * @param @param privsql 权限sql片段
	 * @param @return
	 * @return String
	 * @author:zhaoxg
	 * @throws
	 */
	public String getReportSql(String groupValues,ArrayList tabHeadList,LazyDynaBean groupBean,String tablename,String privsql)
	{
		String sql="";
		try
		{
			if("1".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid))
			{
				StringBuffer buf=new StringBuffer(" from "+tablename+" where 1=1 ");
				buf.append(privsql);
				sql="select * "+buf.toString();
			}
			else if("3".equals(rsid)|| "13".equals(rsid))
			{
				String f_groupItem=(String)groupBean.get("f_groupItem");
				String s_groupItem=(String)groupBean.get("s_groupItem");
				boolean isHaveE0122=false;
				boolean isHaveB0110=false;
				StringBuffer buf=new StringBuffer("");
				LazyDynaBean headBean=null;

				ArrayList<String> unitList=new ArrayList<String>();
				for(int i=0;i<tabHeadList.size();i++)
				{
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					if("a00z1".equalsIgnoreCase(aitemid)|| "a00z3".equalsIgnoreCase(aitemid))
						buf.append(",max("+aitemid+") "+aitemid);
					else
				    	buf.append(",sum("+aitemid+") "+aitemid);
				}
				StringBuffer temp_sql=new StringBuffer("");
				String tp="";
				if(buf.toString().length()>0)
					tp=buf.substring(1)+",";
				temp_sql.append("select "+tp);
				if(f_groupItem!=null&&f_groupItem.length()>0)
				{
					if("e0122".equalsIgnoreCase(f_groupItem))
						isHaveE0122=true;
					if("b0110".equalsIgnoreCase(f_groupItem))
						isHaveB0110=true;
					FieldItem field= (FieldItem) DataDictionary.getFieldItem(f_groupItem).clone();
					if(field!=null&& "a".equalsIgnoreCase(field.getItemtype()))
						temp_sql.append(Sql_switcher.isnull("NULLIF("+f_groupItem+",'')","'-1'")+"as "+f_groupItem+",");
					else
						temp_sql.append(f_groupItem+",");
				}
				if(s_groupItem!=null&&s_groupItem.length()>0)
				{
					if("e0122".equalsIgnoreCase(s_groupItem))
						isHaveE0122=true;
					if("B0110".equalsIgnoreCase(s_groupItem))
						isHaveB0110=true;
					if(f_groupItem!=null&&f_groupItem.length()>0)
					{
						FieldItem field= (FieldItem) DataDictionary.getFieldItem(f_groupItem).clone();
						temp_sql.append(" case when NULLIF("+s_groupItem+",'') is null then ");
						if(field!=null&& "a".equalsIgnoreCase(field.getItemtype()))
							temp_sql.append(Sql_switcher.isnull("NULLIF("+f_groupItem+",'')","'-1'"));
						else
							temp_sql.append(f_groupItem);
						temp_sql.append(" else "+s_groupItem+" end as "+s_groupItem+",");
					}
					else
						temp_sql.append(s_groupItem+",");
				}
				temp_sql.append("count(a0100) num ,");
				if(!isHaveE0122)
					temp_sql.append(" max(e0122) as e0122,");
				if(!isHaveB0110)
					temp_sql.append(" max(b0110) as b0110, ");
				temp_sql.append("max(a0000) as a0000,max(dbid) as dbid ");
				temp_sql.append(" from "+tablename+" where 1=1 ");
				temp_sql.append(privsql);

				if(groupValues!=null&&groupValues.length()>0)
				{

					SalaryTemplateBo gzbo=new SalaryTemplateBo(conn, Integer.parseInt(salaryid), this.userView);
					String deptid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
					deptid = deptid != null ? deptid : "";
					String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
					orgid = orgid != null ? orgid : "";

					String[] temps=groupValues.split(",");
					StringBuffer buffer2=new StringBuffer("");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
						{
							String[] temps_2 = PubFunc.keyWord_reback(temps[i]).split("/");
							if("E0122".equalsIgnoreCase(f_groupItem))
							{
								if("UN".equalsIgnoreCase(temps_2[0])){
									unitList.add(temps_2[1]);
								}
							}
							buffer2.append(",'"+temps_2[1]+"'");
						}
					}
					if(buffer2.toString().trim().length()>0){
						if("E0122".equalsIgnoreCase(f_groupItem)&&StringUtils.isNotBlank(deptid))//添加对归属单位 归属部门的支持 zhanghua 2017-5-20
							temp_sql.append(" and ("+f_groupItem+" in ("+buffer2.substring(1)+")  or "+deptid+" in ("+buffer2.substring(1)+")) ");
		    			else if("B0110".equalsIgnoreCase(f_groupItem)&&StringUtils.isNotBlank(orgid))
		    				temp_sql.append(" and ("+f_groupItem+" in ("+buffer2.substring(1)+")  or "+orgid+" in ("+buffer2.substring(1)+")) ");
		    			else
		    				temp_sql.append(" and "+f_groupItem+" in ("+buffer2.substring(1)+") ");


			    		//temp_sql.append(" having "+f_groupItem+" in ("+buffer2.substring(1)+")");

			    		for(String str:unitList){
			    			temp_sql.append(" or "+f_groupItem+" like '"+str+"%' ");
			    			if(StringUtils.isNotBlank(deptid)){
			    				temp_sql.append(" or "+deptid+" like '"+str+"%' ");
			    			}
			    		}
					}
				}

				temp_sql.append("  group by  ");
				String temp="";
				if(s_groupItem!=null&&s_groupItem.length()>0) {
					//temp += "," + s_groupItem;
					FieldItem field= (FieldItem) DataDictionary.getFieldItem(f_groupItem).clone();
					temp+=", case when NULLIF("+s_groupItem+",'') is null then ";
					if(field!=null&& "a".equalsIgnoreCase(field.getItemtype()))
						temp+=Sql_switcher.isnull("NULLIF("+f_groupItem+",'')","'-1'");
					else
						temp+=f_groupItem;
					temp+=" else "+s_groupItem+" end ";
				}
				if(f_groupItem!=null&&f_groupItem.length()>0)
					temp+=","+Sql_switcher.isnull("NULLIF("+f_groupItem+",'')","'-1'");
				temp_sql.append(temp.substring(1));
				sql=temp_sql.toString();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql;
	}
	public LazyDynaBean getGroupBean2(String itemid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = "select s.* from salaryset s where UPPER(s.itemid) = '"+itemid.toUpperCase()+"'  and s.salaryid="+this.salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("itemid",itemid);
				bean.set("itemdesc",rs.getString("itemdesc"));
				bean.set("itemfmt","");
				bean.set("itemtype", rs.getString("itemtype"));
				bean.set("codesetid",rs.getString("codesetid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 *
	 * @Title: resetList
	 * @Description:签名表重新组织数据，把合计和总计添加进去
	 * @param @param sumStr 求和指标（数值型）
	 * @param @param recordList 非总计项的数据集合
	 * @param @param tablename
	 * @param @param tabHeadList
	 * @param @param privsql 权限sql
	 * @param @param group 分组指标
	 * @param @return
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	public ArrayList resetList(String sumStr,ArrayList recordList,String tablename,ArrayList tabHeadList,String privsql,String group)
	{
		ArrayList list=new ArrayList();
		//for(int i)
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			//拼接sql（该sql算出所有的按部门和单位分组的总和）
			StringBuffer buf=new StringBuffer(" from "+tablename+" where 1=1 "+privsql);
			boolean isE=false;
			if("e0122".equalsIgnoreCase(group))
			{
				isE=true;
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			String atemp="";
			if(sumStr!=null&&!"".equals(sumStr))
				atemp=sumStr.substring(1)+",";
			//如果不是以部门为单位，则取出所有的总计,多加个变量为了sql
			String groupBy = group;
			if(!isE){
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					groupBy = " rollup ("+group+")";
				}
				else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
				{
					groupBy = group+" with rollup";
				}
			}
			RowSet rowSet=dao.search("select "+atemp+(isE?"b0110,":"")+group+"  "+buf.toString()+" group by "+(isE?"b0110,":"")+groupBy);
			LazyDynaBean headBean=new LazyDynaBean();
			LazyDynaBean abean=null;
			HashMap map=new HashMap();
			String codesetid="";
			//将查出来的查询出来的值对应到每列上
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				for(int i=0;i<tabHeadList.size();i++){
					headBean=(LazyDynaBean)tabHeadList.get(i);
					String aitemid=(String)headBean.get("itemid");
					String aitemtype=(String)headBean.get("itemtype");
					String acodesetid=(String)headBean.get("codesetid");
					if("A00Z3".equalsIgnoreCase(aitemid) || "A00Z1".equalsIgnoreCase(aitemid))
						continue;
					if("b0110".equalsIgnoreCase(aitemid))
						acodesetid="UN";
					if("e0122".equalsIgnoreCase(aitemid))
						acodesetid="UM";

					if(codesetid.length()==0&&aitemid.toLowerCase().equals(group.toLowerCase()))
						codesetid=acodesetid;

					String value="";
					if("N".equals(aitemtype))
					{
						if(rowSet.getString(aitemid)!=null && !"A00Z3".equalsIgnoreCase(aitemid) && !"A00Z1".equalsIgnoreCase(aitemid))
							value=rowSet.getString(aitemid);
					}
					if(aitemid.equalsIgnoreCase(group))
					{
						value=rowSet.getString(aitemid);
					}
					abean.set(aitemid,value);
				}
				abean.set("signature"," ");
				//isE表示在group为e0122的时候即以部门为单位
				if(isE)
				{
					//将得到的LazyDynaBeanput进入map中，为了后面的查询获取
					//这里将查出来的所有的数据如果为空或者null的话全部置为"",后面在赋值的时候，全是""，不是null，导致数据出错
					String rowB = rowSet.getString("b0110");
					String rowG = rowSet.getString(group);
					if(StringUtils.isBlank(rowB)) {
						rowB = "";
					}
					if(StringUtils.isBlank(rowG)) {
						rowG = "";
					}
     				map.put(rowB+"/"+rowG, abean);
				}
				else{
					String rowG = "";
					//这里判断如果不是以部门为分组的时候设计key为总计，后面根据这个key进行查找value
					if(rowSet.isLast()) {
						rowG = "总计";
					}else {
						rowG = rowSet.getString(group);
						if(StringUtils.isBlank(rowG)) {
							rowG = "";
						}
					}

					map.put(rowG+"/", abean);
				}
			}
			HashMap map0=new HashMap();
			//在以部门为单位的时候，同时整理出以单位分组的数据
			if("e0122".equalsIgnoreCase(group))
			{
				StringBuffer buf0=new StringBuffer(" from "+tablename+" where 1=1 " +privsql);
				rowSet=dao.search("select "+atemp+" b0110 "+buf0.toString()+" group by b0110");
				abean=null;
				//循环将查出得值对应到各列中
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					for(int i=0;i<tabHeadList.size();i++){
						headBean=(LazyDynaBean)tabHeadList.get(i);
						String aitemid=(String)headBean.get("itemid");
						String aitemtype=(String)headBean.get("itemtype");
						String acodesetid=(String)headBean.get("codesetid");
						String value="";
						if("A00Z3".equalsIgnoreCase(aitemid) || "A00Z1".equalsIgnoreCase(aitemid))
							continue;
						if("N".equals(aitemtype))
						{
							if(rowSet.getString(aitemid)!=null)
								value=rowSet.getString(aitemid);

						}
						abean.set(aitemid,value);
					}
					abean.set("signature"," ");
					map0.put(rowSet.getString("b0110"), abean);
				}
			}
			String temp="-1";
			LazyDynaBean a_bean=null;
			LazyDynaBean old_bean=null;
			String old_b0110="";
			String old_e0122 = "";
			String e0122_value="";
			String b0110_value="";
			//进行合计或者总计的插入
			for(int i=0;i<recordList.size();i++)
			{
				a_bean=(LazyDynaBean)recordList.get(i);
				String group_value=(String)a_bean.get(group);
				b0110_value=(String)a_bean.get("b0110");
				if("e0122".equalsIgnoreCase(group))
			        e0122_value=(String)a_bean.get("e0122");
				if(StringUtils.isNotBlank(old_b0110))
					old_b0110=b0110_value;
				if("-1".equals(temp))
				{
					temp=group_value;
				}
				//在group为e0122时候，判断是否需要进行统计了
				if(isE&&temp.equalsIgnoreCase(group_value)&&old_b0110.equalsIgnoreCase(b0110_value))
				{
					list.add(a_bean);
				}
				//在group为除了e0122的时候，判断是否需要进行统计
				else if(!isE && temp.equalsIgnoreCase(group_value)) {
					list.add(a_bean);
				}
				else
				{
					LazyDynaBean temp_bean=null;
					//获取map中的值（可查看前面map的注释）
					if(isE)
						temp_bean=(LazyDynaBean)map.get(old_b0110+"/"+old_e0122);
					else
						temp_bean=(LazyDynaBean)map.get("".equals(temp)?"/":temp+"/");
					//算出总计和合计
					if(temp_bean!=null)
					{
//						if(group.equalsIgnoreCase("e0122"))
//						{
							/*if(old_bean.get("b0110")!=null)
								temp_bean.set("b0110",(String)old_bean.get("b0110"));*/
							temp_bean.set(group, "合计");
//						}else {
//							temp_bean.set(group, "总计");
//						}
						temp_bean.set("iscollect","1");
						temp_bean.set("signature"," ");

						list.add(temp_bean);
						this.addGroupPageNum(list.size()-1);
						//这里就是只有部门为单位的时候才有合计和总计两个
						if("e0122".equalsIgnoreCase(group))
						{
							if(b0110_value!=null&&!b0110_value.equals(old_b0110))
							{
					      		if(map0.get(old_b0110)!=null)
					    		{
					    			LazyDynaBean atemp_bean=(LazyDynaBean)map0.get(old_b0110);
					    			atemp_bean.set(group, "总计");
					    			atemp_bean.set("b0110",(String)old_bean.get("b0110"));
					    			atemp_bean.set("iscollect","2");
					    			list.add(atemp_bean);
					    		}
							}
						}
						//将各列的值插入到list中
						list.add(a_bean);
					}else{
						list.add(a_bean);
					}
					temp=group_value;
				}
				old_bean=a_bean;
				//这里是县循环出各行的值，再在下一个循环的时候算出总计或者合计，所以需要将旧值带入到下次循环
				old_e0122 = e0122_value;
				old_b0110 = b0110_value;
			}
			//获取最后一个数据的总计和合计（即上面每个循环循环的都是上一个old数据的合计和总计）
			LazyDynaBean temp_bean=(LazyDynaBean)map.get("".equals(temp)?null:temp);
			if(isE)
				temp_bean=(LazyDynaBean)map.get(old_b0110+"/"+temp);
			else
				temp_bean=(LazyDynaBean)map.get("".equals(temp)?"/":temp+"/");
			//按部门为单位时的合计
			if(temp_bean!=null)
			{
				/*if(group.equalsIgnoreCase("e0122"))
				{*/
					/*if(old_bean.get("b0110")!=null)
						temp_bean.set("b0110",(String)old_bean.get("b0110"));*/
					temp_bean.set(group, "合计");
				/*}else {
					temp_bean.set(group, "总计");
				}*/
				temp_bean.set("iscollect","1");
				this.addGroupPageNum(list.size()-1);
				list.add(temp_bean);
			}
			//按部门为单位时的单位名称的总计
			if("".equals(old_b0110)) {
				old_b0110 = null;
			}
			if("e0122".equalsIgnoreCase(group)&&map0.get(old_b0110)!=null)
			{
				temp_bean=(LazyDynaBean)map0.get(old_b0110);
				LazyDynaBean last_bean=(LazyDynaBean)recordList.get(recordList.size()-1);
				temp_bean.set(group, "总计");
				temp_bean.set("b0110",(String)last_bean.get("b0110"));
				temp_bean.set("signature"," ");
				temp_bean.set("iscollect","2");
				list.add(temp_bean);
			}else {
				//如果不是以部门为单位统计出所有的总计
				temp_bean=(LazyDynaBean)map.get("总计/");
				temp_bean.set(group, "总计");
				temp_bean.set("signature"," ");
				temp_bean.set("iscollect","2");
				list.add(temp_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 *
	 * @Title: resetGroupName
	 * @Description: 把第一分组词值改成实际的汉字描述，此处变化不定，内容自定义，所以不能用表格控件自动根据代码生成
	 * @param @param abean 当前的一条记录
	 * @param @param rowSet 结果集
	 * @param @param groupBean 分组指标
	 * @return void
	 * @author:zhaoxg
	 * @throws
	 */
	public void resetGroupName(LazyDynaBean abean,RowSet rowSet,LazyDynaBean groupBean)
	{
		try
		{
			String f_groupItem=(String)groupBean.get("f_groupItem");
		    String s_groupItem=(String)groupBean.get("s_groupItem");
		    String f_codesetid=(String)groupBean.get("f_codesetid");
		    String s_codesetid=(String)groupBean.get("s_codesetid");
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
		    if(s_groupItem.length()>0)
		    {
		    	if("UM".equalsIgnoreCase(s_codesetid)|| "UN".equalsIgnoreCase(s_codesetid))
		    	{
		    		String desc="";
		    		if("UN".equalsIgnoreCase(s_codesetid))
		    		{
		    			desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    			if(desc==null|| "".equals(desc))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    		}
		    		else
		    		{
		    			CodeItem ci = AdminCode.getCode("UM", rowSet.getString(s_groupItem), Integer.parseInt(display_e0122));
		    			if(ci!=null)
		    			{
		    				desc=ci.getCodename();
		    			}
		    			else
		    			{
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(s_groupItem));
		    				if(desc==null|| "".equals(desc))
		    					desc=AdminCode.getCodeName("UN", rowSet.getString(s_groupItem));
		    			}
		    		}
		    		abean.set(f_groupItem,desc);
		    	}
		    	else
		    	{
		        	abean.set(f_groupItem,AdminCode.getCodeName(s_codesetid, rowSet.getString(s_groupItem)));
		    	}
		    }
		    else
		    {
				if("UM".equalsIgnoreCase(f_codesetid)|| "UN".equalsIgnoreCase(f_codesetid))
		    	{
		    		String desc="";
		    		if("UN".equalsIgnoreCase(f_codesetid))
		    		{
		    			desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
		    			if(desc==null|| "".equals(desc))
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
		    		}
		    		else
		    		{
		    			CodeItem ci = AdminCode.getCode("UM", rowSet.getString(f_groupItem), Integer.parseInt(display_e0122));
		    			if(ci!=null)
		    			{
		    				desc=ci.getCodename();
		    			}
		    			else
		    			{
		    				desc=AdminCode.getCodeName("UM", rowSet.getString(f_groupItem));
		    				if(desc==null|| "".equals(desc))
		    					desc=AdminCode.getCodeName("UN", rowSet.getString(f_groupItem));
		    			}
		    		}
		    		abean.set(f_groupItem,desc);
		    	}
				else
		    	{
		        	abean.set(f_groupItem,AdminCode.getCodeName(f_codesetid, rowSet.getString(f_groupItem)));
		    	}
		    }
		    abean.set("iscollect","0");//0：不是合计或小计行，1：是合计或小计行，需要变色
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取薪资报表导出数据源。此处应和SalaryReportTrans中获取表数据方法一致
	 * @param model model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
	 * @param bosdate 业务日期
	 * @param count 发放次数
	 * @param UserFlag
	 * @param groupvalues 分组值
	 * @param baseid 统计项目
	 * @param itemid 统计指标
	 * @author:zhanghua	2017 03 07
	 * @return
	 */
	public ArrayList<LazyDynaBean> getExportReportDataList(String model,String bosdate,String count,String UserFlag,String groupvalues,String baseid,String itemid ){
		ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
		try{


			SalaryTemplateBo gzbo=
					new SalaryTemplateBo
					(this.conn,Integer.parseInt(salaryid),this.userView);

			//SalaryReportBo salarybo=new SalaryReportBo(this.conn,salaryid,this.userView,rsid, rsdtlid);
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryid));
			String priv = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag"); // 人员范围权限过滤标志 1：有
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET,"user");
			String tableName = "";
			if("0".equals(model))
			{
	    		if(manager.length()==0||manager.equalsIgnoreCase(this.userView.getUserName()))
	    			tableName=this.userView.getUserName()+"_salary_"+salaryid;
	    		else
	    			tableName=manager+"_salary_"+salaryid;
			}
			else if("1".equals(model)|| "3".equals(model))
			{
				tableName="salaryhistory";
			}
			else if("4".equals(model))
				tableName="salaryarchive";

			StringBuffer privsql = new StringBuffer();//权限sql 各表均用
			if(bosdate.length()==7)
				bosdate=bosdate+"-01";
			if("0".equals(model)){
				if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
					privsql.append(gzbo.getWhlByUnits(tableName, true));
				privsql.append(gzbo.getfilter(tableName));
				privsql.append(" and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
			}else if("1".equals(model)){
				privsql.append("  and ((((AppUser is null  "+gzbo.getWhlByUnits(tableName, true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
				privsql.append(" and salaryid='"+salaryid+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
				if(UserFlag!=null&& UserFlag.length()>0&&!"all".equalsIgnoreCase(UserFlag)){
					privsql.append(" and UserFlag='"+ UserFlag +"'");
				}
			}else if(StringUtils.equalsIgnoreCase("3",model)||StringUtils.equalsIgnoreCase("4",model)){// 薪资分析-薪资历史数据 3 未归档 4已归档
				privsql.append(" and salaryid = ").append(salaryid);
				privsql.append(gzbo.getUnitsPrivSql());
				privsql.append(gzbo.getHistoryFilter(tableName));
				privsql.append(" and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
			}



			if("1".equals(rsid)||"2".equals(rsid)||"3".equals(rsid)||"12".equals(rsid)||"13".equals(rsid)){
				dataList = getRecordList(tableName, privsql.toString(), groupvalues,model);
			}else
			{
				HashMap<String, Double> hm=new HashMap<String, Double>();
				dataList = getGzAnalyseList(rsid, baseid, itemid, tableName, privsql.toString(),hm);
			}


		}catch(Exception e){
			e.printStackTrace();
		}

		return dataList;


	}


	/***
	 * 获取工资单数据
	 * @param strList 表头
	 * @return
	 */
	public ArrayList<ArrayList> getRepotrDataList(TableDataConfigCache tableCache){
		ArrayList<ArrayList> dataList=new ArrayList<ArrayList>();
		try {
			String sql = (String)tableCache.get("combineSql")+tableCache.getSortSql();//此参数内是表格控件当前显示的完整sql
			ContentDAO dao=new ContentDAO(this.conn);

			RowSet rs=dao.search(sql);
			ArrayList rowList;
			while(rs.next()){
				rowList=new ArrayList<String>();
				for(Object o:tableCache.getDisplayColumns()){
					ColumnsInfo columnsInfo=(ColumnsInfo)o;
					if(columnsInfo.getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
						continue;

					if("signature".equals(columnsInfo.getColumnId())){
						rowList.add("");
					}
					else if("D".equalsIgnoreCase(columnsInfo.getColumnType())){
						String str=rs.getTimestamp(columnsInfo.getColumnId())==null?"":rs.getTimestamp(columnsInfo.getColumnId()).toString() ;
						/*if(str.length()>10)
							str=str.substring(0, 10);*/
						if(StringUtils.isNotBlank(str)) {
							if(columnsInfo.getColumnLength() == 18)
							{
								str=str.substring(0, 19);
							}
							else if(columnsInfo.getColumnLength() == 16)
							{
								str=str.substring(0, 16);
							}
							else if(columnsInfo.getColumnLength() == 7)
							{
								str=str.substring(0, 7);
							}
							else if(columnsInfo.getColumnLength() == 4)
							{
								str=str.substring(0, 4);
							}
							else
							{
								str=str.substring(0, 10);
							}
						}
						rowList.add(str);
					}
					else
						rowList.add(rs.getString(columnsInfo.getColumnId())==null?"":rs.getString(columnsInfo.getColumnId()));
				}


				dataList.add(rowList);
			}




		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dataList;
	}


	/**
	 * 取单位下所有部门
	 * @param unit 单位id
	 * @return
	 */
	private String getUmFromUn(String unit){
		StringBuilder str=new StringBuilder();
		try{
			String sql="select * from organization where   codesetid='UM' and parentid like '"+unit+"%'  and    parentid<>codeitemid";
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);

			while(rs.next()){
				str.append("'"+rs.getString("codeitemid")+"',");
			}
			str.deleteCharAt(str.length()-1);

		}catch(Exception e){
			e.printStackTrace();
		}

		return str.toString();
	}

	/**
	 * 将数据处理，即将列为代码的数据改成汉字，这样传到前台数据不会乱
	 * @Title: getDataToList
	 * @Description:
	 * @param @param list：传过来未经过处理的数据
	 * @param @param display_e0122：标记
	 * @param @param columnMap：列的map
	 * @param @param col：报表输出时候传过来的列
	 * @param @return
	 * @return ArrayList
	 * @authod :sunjian
	 * @time:2017-04-14
	 * @throws
	 */
	public ArrayList getDataToList(ArrayList<LazyDynaBean> list,  String display_e0122, LinkedHashMap columnMap, ArrayList<ColumnsInfo> col) {
		FieldItem field=new FieldItem();
		ArrayList list2 = new ArrayList();
		String groupField = null;
		//找出是哪个组，因为上个方法中已经做了处理导致group已经置为汉字，不去改变上个方法，这里只能进行再次获取，分组指标，如果colid是groupField直接取值
		if(("2".equals(rsid)|| "12".equals(rsid))&&reportdetailvo.getInt("bgroup")==1)
		{
			groupField=reportdetailvo.getString("fgroup").toLowerCase();//签名表的分组指标
		}
		//循环dataList，获取数据值到前台
		for(int rowNum=0;rowNum<list.size();rowNum++){
			//获取每一行
			LazyDynaBean tempbean=(LazyDynaBean)list.get(rowNum);
			//根据报表输出传过来的列数进行循环
			for(int colNum=0;colNum<col.size();colNum++){
				//获取每一行的每一列
				String colid=col.get(colNum).getColumnId();
				//获取对应的field，field可以判断数据的类型等等
				field=(FieldItem)columnMap.get(colid);
				String content="";
				String value="";
				//如果是新增的iscollect和colid是groupField直接取值
				if("iscollect".equalsIgnoreCase(colid) || colid.equalsIgnoreCase(groupField)) {
					value = tempbean.get(colid).toString();
					content = value;
					tempbean.set( colid, content );
					continue;
				}
				if(field != null ) {
					if(tempbean.get(colid)==null)
						continue;
					value = tempbean.get(colid).toString();

					//判断是否是字符型
					if("A".equalsIgnoreCase(field.getItemtype())){
						String codesetid=field.getCodesetid();

						if(!StringUtils.isBlank(codesetid)&&!"0".equals(codesetid)){
							//在为合计和总计的时候直接取值
							if("合计".equals(value) || "总计".equals(value)) {
								content = value;
								tempbean.set( colid, content );
								continue;
							}
							//单位
							else if("un".equalsIgnoreCase(codesetid)){
						        content = AdminCode.getCodeName("UN", value);
						        if(StringUtils.isBlank(content))
						            content = AdminCode.getCodeName("UM", value);
						    }
						    //部门
						    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
							{
								if("e0122".equalsIgnoreCase(colid))
								{
									if(Integer.parseInt(display_e0122)==0)
										content=AdminCode.getCodeName("UM",value);
									else
									{
										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		content=item.getCodename();
						        		}
						    	    	else
						    	    	{
						    	    		content=AdminCode.getCodeName("UM",value);
						    	    	}
									}
								}else
									content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);

						}else
							content=AdminCode.getCodeName(codesetid, value);

					}else {
						content = value;
					}
					}
					//日期型
					else if("D".equalsIgnoreCase(field.getItemtype())){
						//AdminCode
						content = value;
					}
					//数字型
					else if("N".equalsIgnoreCase(field.getItemtype())){
						FieldItem item=DataDictionary.getFieldItem(colid.toLowerCase());
						String colNumValue=value;
						//进行格式化
						if(colNumValue==null|| "".equals(colNumValue)) {
							colNumValue="0.00";

						}else if(item == null) {
							String.format("%.2f", Double.parseDouble(colNumValue));
						}else {
							int CLength = item.getDecimalwidth();
							colNumValue = String.format("%." + CLength + "f", Double.parseDouble(colNumValue));
						}
						content = colNumValue;
					}

				}
				//这里加这个判断是为了如果是代码型，但传过来的是汉字（以前经过处理的），导致AdminCode.getCodeName(codesetid, value);value为空
				if(StringUtils.isBlank(content)) {
					content = value;
				}
				tempbean.set( colid, content );
			}
			list2.add(tempbean);
		}
		return list2;
	}

	/**
	 * 由于在薪资类别同步结构的时候没有同步reportitem表这里进行同步
	 * @Title: synReportSet
	 * @Description:
	 * @param @param salaryid
	 * @return void
	 * @author sunj
	 * @date 2017-04-19
	 */
	public void synReportSet(String salaryid, String rstldid) {
		StringBuffer sff = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try {
			sff.append("SELECT r.RsDtlID,r.ITEMID,Max(s.ITEMDESC) as ITEMDESC,Max(s.NWIDTH) as NWIDTH,Max(s.ITEMLENGTH) as ITEMLENGTH,Max(s.DECWIDTH) as DECWIDTH,Max(s.ITEMTYPE) as ITEMTYPE from REPORTITEM r left join SALARYSET s on r.ITEMID = s.ITEMID ");
			sff.append(" WHERE (r.ITEMDESC <> s.ITEMDESC OR r.nWidth <> s.NWIDTH");
			sff.append(" OR ");
			//由于在时间类型的时候yyyy.MM.dd HH:mm:SS做特殊处理和SALARYSET中的itemlength比较长度
			sff.append(" (CASE WHEN s.ITEMTYPE  = 'D' THEN CASE WHEN (r.ITEMFMT = 'yyyy.MM.dd HH:mm:ss') THEN 18 ");

			if(Sql_switcher.searchDbServer() == 1) //sqlserver
				sff.append(" ELSE LEN(r.ITEMFMT)" );
			else if(Sql_switcher.searchDbServer() == 2)//ORACLE
				sff.append(" ELSE LENGTH(r.ITEMFMT)" );

			sff.append(" END ELSE s.ITEMLENGTH END) <> s.ITEMLENGTH");
			sff.append(" OR ");
			////在为0.的类型的时候即有小数位，判断REPO  RTITEM中的长度和SALARYSET中的DECWIDTH+2比较
			sff.append(" (CASE WHEN (r.ITEMFMT LIKE '0.%') THEN ");

			if(Sql_switcher.searchDbServer() == 1) //sqlserver
				sff.append(" LEN(r.ITEMFMT)" );
			else if(Sql_switcher.searchDbServer() == 2)//ORACLE
				sff.append(" LENGTH(r.ITEMFMT)" );

			sff.append(" ELSE (s.DECWIDTH + 2) END) <> (s.DECWIDTH + 2)) AND r.ITEMID NOT IN ('A00Z2','A00Z0','A00Z1','A00Z3') AND s.SALARYID = "+salaryid);
			sff.append(" GROUP BY r.RsDtlID,r.ITEMID ");

			ContentDAO dao=new ContentDAO(this.conn);
			//查询
			rs = dao.search(sff.toString());
			List<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
			while(rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("RSDTLID", rs.getInt("RSDTLID"));
				bean.set("ITEMID", rs.getString("ITEMID"));
				bean.set("ITEMDESC", rs.getString("ITEMDESC"));
				bean.set("NWIDTH", rs.getString("NWIDTH"));
				bean.set("ITEMLENGTH", rs.getString("ITEMLENGTH"));
				bean.set("DECWIDTH", rs.getString("DECWIDTH"));
				bean.set("ITEMTYPE", rs.getString("ITEMTYPE"));
				list.add(bean);
			}

			updateList(list);
			//这一步主要是为了在salarySet中删除了，而reportitem中还存在的删除
			sql.append("delete ");
			if(Sql_switcher.searchDbServer() == 1) //sqlserver
				sql.append(" r ");
			sql.append(" from REPORTITEM r WHERE  not exists (select * from SALARYSET s where r.ItemID=s.itemid and s.salaryid = " + salaryid + ") and r.RsDtlID = "+ rstldid +"");
			dao.delete(sql.toString(),new ArrayList());

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}

	/**
	 * 更新操作
	 * @Title: updateList
	 * @Description:
	 * @param @param list
	 * @param @return
	 * @return int
	 * @throws
	 */
	public void updateList(List<LazyDynaBean> list) {
		ContentDAO dao=new ContentDAO(this.conn);
		int flag = 0;
		try{
			for(int i = 0; i < list.size(); i++) {
				RecordVo detailVo=new RecordVo("REPORTITEM");
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				String itemtype = String.valueOf(bean.get("ITEMTYPE"));
				int decwidth = Integer.parseInt(String.valueOf(bean.get("DECWIDTH")));
				String itemlength = String.valueOf(bean.get("ITEMLENGTH"));
				String itemfmt = "";
				//将数值型改成0.0~~~
				if("N".equals(itemtype))
		    	{
			    	itemfmt="0";
			    	if(decwidth>0)
			    	{
			    		itemfmt+=".";
				    	for(int j=0;j<decwidth;j++)
				    		itemfmt+="0";
			    	}
			    //将日期型改成特殊格式
	    		}else if("D".equals(itemtype)) {
	    			switch (Integer.parseInt(itemlength)) {
						case 4:
							itemfmt = "yyyy";
							break;
						case 7:
							itemfmt = "yyyy.MM";
							break;
						case 10:
							itemfmt = "yyyy.MM.dd";
							break;
						case 16:
							itemfmt = "yyyy.MM.dd HH:mm";
							break;
						case 18:
							itemfmt = "yyyy.MM.dd HH:mm:ss";
							break;
						default:
							itemfmt = "yyyy.MM.dd";
							break;
	    			}
	    		}
				detailVo.setInt("rsdtlid", (Integer) bean.get("RSDTLID"));
				detailVo.setString("itemid", String.valueOf(bean.get("ITEMID")));
				dao.findByPrimaryKey(detailVo);
				detailVo.setString("nwidth", String.valueOf(bean.get("NWIDTH")));
				detailVo.setString("itemdesc", String.valueOf(bean.get("ITEMDESC")));
				if(!StringUtils.isBlank(itemfmt))
					detailVo.setString("itemfmt",itemfmt);

				dao.updateValueObject(detailVo);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: isHashInTable
	 * @Description: (根据表名得到scheme_id)如果没有取薪资发放的栏目设置
	 * @param tableName
	 *            薪资帐套临时表名称
	 * @throws GeneralException
	 */
	public int getSchemeId(String tableName) throws GeneralException {
		RowSet rowSet = null;
		int scheme_id = -1;
		if(StringUtils.isBlank(tableName))
			return scheme_id;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();

			//若存在私有方案 则取私有，否则取公有
			String strSql= "select scheme_id from t_sys_table_scheme_item where scheme_id = ";
			strSql+= Sql_switcher.isnull(" (select Max(scheme_id) from t_sys_table_scheme where submoduleid = ? and is_share = '0' and username = ?) "
					, " (select Max(scheme_id) from t_sys_table_scheme where submoduleid = ? and is_share = '1' ) ");
			strSql+= "  order by displayorder";
			list.add(tableName);
			list.add(this.userView.getUserName());
			list.add(tableName);
			rowSet=dao.search(strSql,list);
			if(rowSet.next())
				rowSet.beforeFirst();
			else {//如果没有私有或者共有栏目设置，则取薪资发放的私有或者共有发放
				strSql = "select scheme_id from t_sys_table_scheme_item where scheme_id = ";
				strSql += Sql_switcher.isnull(" (select Max(scheme_id) from t_sys_table_scheme where submoduleid = ? and is_share = '0' and username = ?) "
						, " (select Max(scheme_id) from t_sys_table_scheme where submoduleid = ? and is_share = '1' ) ");
				strSql += "  order by displayorder";
				list.clear();
				list.add("salary_" + this.salaryid);
				list.add(this.userView.getUserName());
				list.add("salary_" + this.salaryid);
				rowSet=dao.search(strSql,list);
			}

			if(rowSet.next()) {
				scheme_id = rowSet.getInt("scheme_id");
			}
			return scheme_id;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}

	/**
	 * 导出excel和pdf根据栏目设置取查找薪资项目
	 * @param schemeId
	 * @param display
	 * @param type 0:导出excel，1:导出pdf
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getItemsToMap(int schemeId,String display,int type)
			throws GeneralException {
		RowSet rowSet = null;
		try {
			HashMap map=new HashMap();
			ContentDAO dao = new ContentDAO(this.conn);
			int excelDisplayWidth = 0;//excel控件传的是int型
			float pdfDisplayWidth = 0f;//pdf没有控制，不过按照栏目设置的100显示的有点宽，这里限制了
			StringBuffer sql = new StringBuffer("select itemid,displaywidth,align from t_sys_table_scheme_item  where ");
			ArrayList list = new ArrayList();
			if(StringUtils.isNotBlank(display)){
				sql.append(" is_display=? and ");
				list.add(display);
			}
			sql.append(" scheme_id =? order by displayorder");
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			while (rowSet.next()) {
				map.put(rowSet.getString("itemid").toLowerCase() + "itemid",(String) rowSet.getString("itemid") );
				if(type == 0) {
					excelDisplayWidth = rowSet.getInt("displaywidth")*38;
					map.put(rowSet.getString("itemid").toLowerCase() + "displaywidth", excelDisplayWidth );
				}else {
					pdfDisplayWidth = (float) (rowSet.getInt("displaywidth")*0.8f);
					map.put(rowSet.getString("itemid").toLowerCase() + "displaywidth", pdfDisplayWidth );
				}
				map.put(rowSet.getString("itemid").toLowerCase() + "align", rowSet.getShort("align") );
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}

	/**
	 * 获取薪资报表树
	 * @param gz_module gz_module =0表示薪资，=1表示保险
	 * @param model 0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 18:15 2019/1/24
	 */
	public ArrayList<LazyDynaBean> listReportTree(String gz_module,String model) throws GeneralException {
		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		try{
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,Integer.parseInt(this.salaryid), this.userView);
			String manager=gzbo.getManager();
			boolean isNotManager=false;
			if(StringUtils.isNotBlank(manager)&&!this.userView.getUserName().equalsIgnoreCase(manager)&&!this.userView.isSuper_admin()){
				isNotManager=true;
			}
			ArrayList<LazyDynaBean> listReportStyle=this.getReportStyle(gz_module,isNotManager);
			ArrayList<LazyDynaBean> listReportdetail=this.getReportdetail(gz_module,isNotManager,model);

			for (LazyDynaBean styleBean : listReportStyle) {
				String rsid=(String)styleBean.get("id");
				ArrayList<LazyDynaBean> childList=new ArrayList<LazyDynaBean>();
				for (LazyDynaBean bean : listReportdetail) {
					if(rsid.equals(bean.get("rsid"))){
						childList.add(bean);
					}
				}
				styleBean.set("id","m"+rsid);
				styleBean.set("children",childList);

			}
			list=listReportStyle;
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;

	}

	/**
	 * 获取报表分类
	 * @param gz_module =0表示薪资，=1表示保险
	 * @param isNotManager 是否是账套管理员
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 17:20 2019/1/24
	 */
	private ArrayList<LazyDynaBean> getReportStyle(String gz_module,boolean isNotManager) throws GeneralException {
		RowSet rs=null;
		ArrayList list=new ArrayList();
		try{
			if(!isNotManager){
				ContentDAO dao=new ContentDAO(this.conn);
				String sql;
				if("0".equals(gz_module))
					sql="select rsid,rsname from reportstyle where rsid in (1,2,3,4) order by rsid";
				else
					sql="select rsid,rsname from reportstyle where rsid in (12,13) order by rsid";
				rs=dao.search(sql);
				while (rs.next()) {
					LazyDynaBean aBean = new LazyDynaBean();
					aBean.set("id", rs.getString("rsid"));
//					if("4".equals(rs.getString("rsid"))){
//						aBean.set("leaf", true);
//						aBean.set("iconCls","treeiconCls");
//					}
					aBean.set("text", rs.getString("rsname"));
					list.add(aBean);
				}
			}
			LazyDynaBean aBean=new LazyDynaBean();
			aBean.set("id","0");
			aBean.set("text","用户自定义表");
			list.add(aBean);
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 获取报表明细表名称
	 * @param gz_module =0表示薪资，=1表示保险
	 * @param isNotManager 是否是账套管理员
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 17:20 2019/1/24
	 */
	public ArrayList<LazyDynaBean> getReportdetail(String gz_module,boolean isNotManager,String model) throws GeneralException {
		RowSet rs=null;
		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo salaryTemplateBo
					=new com.hjsj.hrms.module.gz.salarytemplate.businessobject.SalaryTemplateBo(this.conn,this.userView);

			ArrayList<LazyDynaBean> common_report=salaryTemplateBo.listCommon_report(Integer.parseInt(this.salaryid),model);
			String sql="";
			if(!isNotManager){
				sql="select rsid,rsdtlid,rsdtlname,ctrlParam from reportdetail where stid="+this.salaryid;
				if("0".equals(gz_module))
					sql+=" And rsid in (1,2,3,4) order by rsid ";
				else
					sql+=" And rsid in (12,13) order by rsid ";
				rs=dao.search(sql);
				while (rs.next()) {
					LazyDynaBean aBean = new LazyDynaBean();
					String tabid=rs.getString("rsdtlid");
					String rsid= rs.getString("rsid");

					String xml = Sql_switcher.readMemo(rs, "ctrlParam");
					LazyDynaBean xmlbean= this.getReportDetailXML(xml);

					String reportType="";
					String reportName="";
					if(xmlbean!=null){
						reportType=(String)xmlbean.get("type");
						reportName=(String)xmlbean.get("username");
					}
					// 超级管理员允许看到所有的报表，包括私有
					if("0".equals(reportType) || this.userView.getUserName().equalsIgnoreCase(reportName) || this.userView.isSuper_admin())
					{
						String commonRepot="0";
						for (LazyDynaBean bean : common_report) {
							if(rsid.equals(bean.get("rsid"))&&tabid.equals(bean.get("tabid"))){
								commonRepot="1";
								break;
							}
						}
						aBean.set("id", tabid);
						aBean.set("rsid",rsid);
						aBean.set("commonRepot",commonRepot);
						aBean.set("text", rs.getString("rsdtlname"));
						//报表输出，公共报表应该允许超级用户进行修改/删除操作。
						if(!this.userView.isSuper_admin() && !this.userView.getUserName().equalsIgnoreCase(reportName)){
							//共享报表
							aBean.set("isShared","1");
						}else{
							//非共享 或自己创建的共享账套
							aBean.set("isShared","0");
						}
						aBean.set("leaf", true);
						aBean.set("iconCls","treeiconCls");
						list.add(aBean);
					}
				}
				LazyDynaBean aBean=new LazyDynaBean();
				aBean.set("id", "4");
				aBean.set("rsid","4");
				String commonRepot="0";
				for (LazyDynaBean bean : common_report) {
					if("4".equals(bean.get("rsid"))&&"4".equals(bean.get("tabid"))){
						commonRepot="1";
						break;
					}
				}
				aBean.set("commonRepot",commonRepot);
				aBean.set("text", "人员结构工资分析表");
				aBean.set("isShared","0");
				aBean.set("leaf", true);
				aBean.set("iconCls","treeiconCls");
				list.add(aBean);

			}
			if ("0".equals(gz_module))
				sql = "select * from muster_name where nModule=14 and (nPrint=-1 or nPrint=" + this.salaryid + ")  order by tabid";
			else if ("1".equals(gz_module))
				sql = "select * from muster_name where nModule=11 and (nPrint=-1 or nPrint=" + this.salaryid + ")  order by tabid";
			rs=dao.search(sql);
			while (rs.next()) {
				//用户自定义表，加上权限限制
				if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())||this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, rs.getString("tabid")))
				{
					LazyDynaBean aBean = new LazyDynaBean();
					String tabid=rs.getString("tabid");
					String commonRepot="0";
					for (LazyDynaBean bean : common_report) {
						if("0".equals(bean.get("rsid"))&&tabid.equals(bean.get("tabid"))){
							commonRepot="1";
							break;
						}
					}
					aBean.set("id",tabid);
					aBean.set("commonRepot",commonRepot);
					aBean.set("rsid", "0");
					aBean.set("text",rs.getString("cname"));
					aBean.set("leaf", true);
					aBean.set("iconCls","treeiconCls");
					aBean.set("reporttype","10");
					list.add(aBean);
				}
			}

			String nmodule="34";
			//保险
			if("1".equals(gz_module))
			{
				nmodule="39";
			}
			CustomReportBo bo = new CustomReportBo(this.conn,this.userView,nmodule);
			DataViewBo dataViewBo = new DataViewBo(this.conn,this.userView,nmodule);
			ArrayList<LazyDynaBean> crlist = bo.getCustomReportList();
			crlist.addAll(dataViewBo.createDataUrl());//获取所有的链接
			for(int j=0;j<crlist.size();j++)
			{
				LazyDynaBean aBean = new LazyDynaBean();
				LazyDynaBean bean = crlist.get(j);
				String report_type=(String)bean.get("report_type");

				if("3".equals(report_type))
				{
					RecordVo vo = (RecordVo)bean.get("vo");
					aBean.set("nmodule", vo.getInt("nmodule"));
					aBean.set("reporttype", "3");
				}else if("4".equals(report_type)) {
					if(bean.getMap().containsKey("url")&&StringUtils.isNotBlank((String)bean.get("url"))) {
						aBean.set("reporttype", "4");
						aBean.set("url", bean.get("url"));
					} else {
						continue;
					}
				}else if("0".equals(report_type)){
					if(StringUtils.isBlank((String)bean.get("ext"))){
						continue;
					}
					aBean.set("reporttype", "0");
				}else{
					continue;
				}
				String id=(String)bean.get("id");
				String link_tabid=(String)bean.get("link_tabid");

				String name=(String)bean.get("name");
				String tabid="t_"+id;
				String commonRepot="0";
				for (LazyDynaBean sbean : common_report) {
					if("0".equals(sbean.get("rsid"))&&tabid.equals(sbean.get("tabid"))){
						commonRepot="1";
						break;
					}
				}
				aBean.set("id",tabid);
				aBean.set("commonRepot",commonRepot);
				aBean.set("rsid", "0");
				aBean.set("text",name);
				aBean.set("leaf", true);
				aBean.set("tabid",StringUtils.isBlank(link_tabid)?id:link_tabid);

				aBean.set("iconCls","treeiconCls");
				list.add(aBean);
			}



		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}

	/**
	 * 解析薪资报表xml
	 * @param xml
	 * @return
	 */
	public LazyDynaBean getReportDetailXML(String xml)
	{
		LazyDynaBean bean=null;
		try
		{
			if(xml==null|| "".equals(xml))
			{
				xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
			}
			Document doc = PubFunc.generateDom(xml);
			Element root=doc.getRootElement();
			XPath xpath=XPath.newInstance("/"+root.getName()+"/owner");
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element==null)
				return bean;
			else{
				bean=new LazyDynaBean();
				String type = element.getAttributeValue("type");
				bean.set("type",type);
				bean.set("username",element.getText());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 获取常用报表列表
	 * @param gz_module =0表示薪资，=1表示保险
	 * @param model 0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 15:26 2019/1/28
	 */
	public ArrayList<LazyDynaBean> listCommonReport(String gz_module,String model) throws GeneralException {
		ArrayList<LazyDynaBean> reportList =new ArrayList<LazyDynaBean>();
		try {

			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn, Integer.parseInt(this.salaryid), this.userView);
			String manager = gzbo.getManager();
			boolean isNotManager = false;
			if (StringUtils.isNotBlank(manager) && !this.userView.getUserName().equalsIgnoreCase(manager) && !this.userView.isSuper_admin()) {
				isNotManager = true;
			}
			ArrayList<LazyDynaBean> list = this.getReportdetail(gz_module, isNotManager,model);
			for (LazyDynaBean bean : list) {
				if("1".equals(bean.get("commonRepot"))){
					reportList.add(bean);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return reportList;
	}

	/**
	 * 移动第一分组词在栏目设置中的位置
	 * @param reportDetailID 报表id
	 * @param f_groupItem 第一分组词
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 16:40 2019/5/23
	 */
	private void moveColumnIndexFromTable_scheme(String reportDetailID,String f_groupItem) throws GeneralException {
		if (StringUtils.isBlank(f_groupItem)) {
			return;
		}
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList list = new ArrayList();
			StringBuffer strSql = new StringBuffer();
			String submoduleid = "salaryreport_" + reportDetailID;
			int f_groupItem_index = -1;
			strSql.append("select ti.* from t_sys_table_scheme_item ti inner join t_sys_table_scheme ts on ti.scheme_id=ts.scheme_id ");
			strSql.append(" where ts.submoduleid=? order by displayorder ");
			list.add(submoduleid);
			rs = dao.search(strSql.toString(), list);
			int schemeId = -1;
			while (rs.next()) {
				schemeId = rs.getInt("scheme_id");
				if (rs.getString("itemid").equalsIgnoreCase(f_groupItem)) {
					f_groupItem_index = rs.getInt("displayorder");
				}

			}
			if (f_groupItem_index == 0 || schemeId == -1) {
				return;
			}
			strSql.setLength(0);
			strSql.append("update t_sys_table_scheme_item set displayorder=displayorder+1  where scheme_id=? and upper(itemid)<>? ");
			list.clear();
			list.add(schemeId);
			list.add(f_groupItem.toUpperCase());
			dao.update(strSql.toString(), list);
			if (f_groupItem_index == -1) {
				strSql.setLength(0);
				strSql.append("insert into  t_sys_table_scheme_item (scheme_id ,itemid ,displayorder ,displaydesc ,is_display " +
						",displaywidth ,align ,is_order ,is_sum ,itemdesc ,mergedesc ,is_lock ,is_fromdict ,is_removable ,fieldsetid)");
				strSql.append(" values (?,?,?,'',1,?,?,0,0,'','',0,null,0,?)");
				list.clear();
				FieldItem fieldItem = DataDictionary.getFieldItem(f_groupItem);
				list.add(schemeId);
				list.add(fieldItem.getItemid());
				list.add(0);
				list.add(fieldItem.getDisplaywidth() == 0 ? 100 : fieldItem.getDisplaywidth() * 15);
				list.add("N".equalsIgnoreCase(fieldItem.getItemtype()) ? 3 : 1);
				list.add(fieldItem.getFieldsetid());
				dao.update(strSql.toString(), list);
			} else {
				strSql.setLength(0);
				strSql.append("update t_sys_table_scheme_item set displayorder=0  where scheme_id=? and upper(itemid)=? ");
				list.clear();
				list.add(schemeId);
				list.add(f_groupItem.toUpperCase());
				dao.update(strSql.toString(), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}

	}



}
