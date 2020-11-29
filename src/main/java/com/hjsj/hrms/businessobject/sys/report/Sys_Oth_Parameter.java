package com.hjsj.hrms.businessobject.sys.report;

import com.hjsj.hrms.businessobject.sys.options.SearchTableCardConstantSet;
import com.hjsj.hrms.interfaces.report.ReportParamterVo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Sys_Oth_Parameter {
	/**定义参数类型*/
	public final static int UNITTYPE=0;//单位类型
	public final static int GOBROAD=1;//出国出境业务 
	public final static int GOBROADSUBSET=2;//出国出境业务子集

	public final static int MYSALARYS=3;
	public final static int MYSALARYS_SALARY=4;   
	public final static int SALARY=5;
	/**信息浏览卡片*/
	public final static int BOROWSE_CARD=6;
	/**兼职参数定义*/
	public final static int PART_TIME=7;
	/**信息录入单列，双列标识*/
	public final static int EDIT_COLUMNS=8;

	/** 人员信息是否需要审批     */
	public  final static int APPROVE_FLAG=10;

	public final static int SYNTHETIZE_BOUND=9;
	/**组织机构根节点描述*/
	public final static int ORG_ROOT_DESC=11; 
	/**单位编制控制和职位编制进行控制参数*/
	/*<workout unit="true|false" pos="true|false"/>*/
	public final static int WORKOUT=12;
	/**控制信息公告是否滚动**/
	public final static int WELCOME_MARQUEE=13;
	/** 人员信息浏览子集每页显示纪录数*/
	public final static int NUM_PER_PAGE=14;
	public final static int LIKN_P_WIDTH=15;
	public final static int LIKN_P_HEIGHT=16;
	public final static int LAWRULE_FILE_DAYS=17;
	public final static int STAT_ID=18;
	public final static int YKCARD_AUTO=19;
	public final static int DIARY=20;
	public final static int PHOTO=21;
	public final static int UPDISK=22;
	public final static int SUBUNITUP=36;
	public final static int EDITUPDISK=23;
	public final static int INPUTCHINFOR=24;//如果1：不直接入库；0为直接入库
	public final static int CONDISK=25;
	public final static int BROWSE_PHOTO=26;//人员信息浏览，默认为照片显示
	public final static int INFOSORT_BROWSE=27;//信息分类浏览员工信息  huaitao add 2008-03-13
	public final static int PINYIN_FIELD_SEARCH=28;//拼音简码指标查询  fengxibin add 2008-03-17
	public final static int COMMON_ROSTER=29;//常用花名册 huaitao add 2008-04-12
	/**机构信息浏览格式=0单列=1双列*/
	public final static int ORG_BROWSE_FORMAT=30;

	public final static int BIRTHDAY_WID=31;
	public final static int DISPLAY_E0122=32;//部门显示包含上X级名称
	public final static int UNITS=33; //人员信息必填项,单位;
	public final static int PLACE=34;
	public final static int VIRTUAL_ORG=37;//是否显示虚拟机构
	public final static int PHOTO_OTHER_VIEW=38;
	public final static int ANNOUNCE_DAYS=39;//设置公告栏中的公告项多少天内为new
	public final static int BLACKLIST_PER=40;//黑名单人员库
	public final static int BLACKLIST_FIELD=41;//黑名单人员指标
	public final static int BLACKLIST=42;//黑名单
	/**
	 * 身份证
	 */
	public final static int CHK_UNIQUENESS=35;//唯一性标识
	public final static int CHK_IdTYPE=51;//证件类别指标

	//机构划转、合并、撤销人员变动信息模板设置
	public final static int ORGANIZATION=43;
	public final static int GQUERY_COND=44;//人员分类条件

	public final static int BROWSE_SEARCH_STATE=45;////员工信息浏览查询项 0：隐藏，1：显示

	public final static int DAIRYINFOLIMIT=46;

	//---------------报表上报是否支持审批  zhaoxg 2013-1-25 --------------------
	public final static int ISAPPROVE = 47;
	public final static int APPROVEID = 48;
	//-----------------------------------------------------------------------
	public final static int STAFF_INFO_NUM = 49;
	public final static int COMPLEX_ID_NUM = 50;
	public final static int LOGLEVEL = 55;

	public final static String STAFF_INFO = "/param/stars_staff/staff_info";
	public final static String COMPLEX_ID = "/param/stars_staff/complex_id";
	public static DynaBean dynaBean;
	private Document doc;
	private Connection conn;

	private String xmlcontent="";

	public Sys_Oth_Parameter(Connection conn) {
		this.conn = conn;
		init();
		try
		{
			doc= PubFunc.generateDom(xmlcontent.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		//see();
	}

	public Sys_Oth_Parameter() {
		super();
	}
	
	//证件类型默认值
	public String getIdTypeValue() {
		String idTypeValue = "";
		try {
			//证件类型
			String id_type=this.getValue(Sys_Oth_Parameter.CHK_IdTYPE);
			if(StringUtils.isNotEmpty(id_type)) {
				FieldItem fieldItem = DataDictionary.getFieldItem(id_type,"A01");
				if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())) {
					CodeItem code = AdminCode.getCode(fieldItem.getCodesetid(), "01");
					if(code!=null) {
                        idTypeValue = "01";
                    }
					if(StringUtils.isEmpty(idTypeValue)) {
						code = AdminCode.getCode(fieldItem.getCodesetid(), "1");
						if(code!=null) {
                            idTypeValue = "1";
                        }
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idTypeValue;
	}

	/**
	 * 读参数的内容
	 *
	 */
	private void init()
	{

		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");	
		RowSet rs=null;
		PreparedStatement stm = null;
		ResultSet c_rs =null;
		if(TemplateStaticDataBo.getConstantStr("SYS_OTH_PARAM", this.conn)!=null) //20171113 邓灿，采用缓存解决并发下压力过大问题
        {
            xmlcontent=TemplateStaticDataBo.getConstantStr("SYS_OTH_PARAM", conn);
        }
		if(xmlcontent==null|| "".equals(xmlcontent))
		{
			xmlcontent=strxml.toString();
		}
		/*
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer();
		    if(Sql_switcher.searchDbServer()==Constant.KUNLUN)
		    	sql.append("select str_value from \"constant\"  where \"constant\"='SYS_OTH_PARAM'");
		    else
		    	sql.append("select str_value from constant where constant='SYS_OTH_PARAM'");
			 rs=dao.search(sql.toString());
			 if(rs.next())
				   xmlcontent=Sql_switcher.readMemo(rs, "str_value");	
			 
			
		}
		catch(Exception ex)
		{
			xmlcontent=strxml.toString();
			ex.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeIoResource(c_rs);
			PubFunc.closeIoResource(stm);
		}
		*/
	}

	public String getValue(int param_type,String property)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					value=element.getAttributeValue(property);	
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	/**
	 * 读取多个参数指标的值
	 * @param param_type
	 * @param property
	 * @return
	 */
	public String getValueS(int param_type,String property)
	{
		StringBuffer values=new StringBuffer();
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Iterator i = childlist.iterator();
				Element element=null;			
				while(i.hasNext())
				{
					element=(Element)i.next();				
					values.append(element.getAttributeValue(property)+",");	
				}
				if(values.toString()!=null&& values.toString().length()>0) {
                    values.setLength(values.length()-1);
                }
			}

			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}	
		if(values!=null&&values.length()>0&&!"null".equalsIgnoreCase(values.toString()))
		{
			return values.toString();		
		}else {
            return "";
        }

	}
	/**
	 * 读取多个参数指标的值
	 * @param param_type
	 * @param property
	 * @return
	 */
	public String getValueS(int param_type,String property,String property2)
	{
		StringBuffer values=new StringBuffer();
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Iterator i = childlist.iterator();
				Element element=null;			
				while(i.hasNext())
				{
					element=(Element)i.next();		
					values.append(element.getAttributeValue(property)+"`");	
					values.append(element.getAttributeValue(property2)!=null?element.getAttributeValue(property2):"");
					values.append(",");
				}
				if(values.toString()!=null&& values.toString().length()>0) {
                    values.setLength(values.length()-1);
                }
			}

			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return values.toString();		
	}
	/**
	 * 保存参数，先设置参数值，再保存
	 * @throws GeneralException
	 */
	public void saveParameter()throws GeneralException
	{

		StringBuffer strsql=new StringBuffer();
		try
		{
			if_SysConstant_Save("SYS_OTH_PARAM");
			StringBuffer buf=new StringBuffer();
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(doc));

			//ContentDAO dao=new ContentDAO(this.conn);
			//RecordVo vo=new RecordVo("constant");			
			boolean iscorrect=if_vo_Empty("SYS_OTH_PARAM");
			ArrayList list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			if(!iscorrect)
			{
				strsql.append("insert into constant(constant,str_value) values(?,?)");	

				list.add("SYS_OTH_PARAM");
				list.add(buf.toString());
				/*vo=new RecordVo("constant");　　//以后再查原因，其它备注型字段这处理没问题
				vo.setString("constant","SYS_OTH_PARAM");				
				vo.setString("str_value",buf.toString());
				dao.addValueObject(vo);*/
			}
			else
			{
				strsql.append("update constant set str_value=? where constant='SYS_OTH_PARAM'");

				switch(Sql_switcher.searchDbServer())
				{
				case Constant.MSSQL:
				{
					list.add(buf.toString());
					break;
				}
				case Constant.ORACEL:
				{
					/* pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
					          getBytes())), buf.length());*/
					list.add(buf.toString());
					break;
				}
				case Constant.DB2:
				{
					list.add(new InputStreamReader(new ByteArrayInputStream(buf.toString().
							getBytes())));
					break;
				}
				}

				/*
				vo.setString("constant","SYS_OTH_PARAM");
				vo.setString("str_value",buf.toString());	
				dao.updateValueObject(vo);*/
			}
			dao.update(strsql.toString(), list);
			//修改时，缓存也要更新  28020   wangb 20170527
			RecordVo vo=new RecordVo("CONSTANT");
			vo.setString("constant","SYS_OTH_PARAM");
			vo.setString("type",null );
			vo.setString("describe",null);
			vo.setString("str_value",buf.toString());
			ConstantParamter.putConstantVo(vo,"SYS_OTH_PARAM" );
			//bug 33959 wangb 20180115 add 清空constantStrMap 中SYS_OTH_PARAM缓存  
			TemplateStaticDataBo.refreshConstantStr("SYS_OTH_PARAM");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 读取参数的值
	 * @param param_type
	 * @return
	 */
	public String getValue(int param_type)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					value=element.getText();
				}
				if(value!=null&& "#".equals(value)) {
                    value="";
                }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;		
	}	

	/**
	 * 设置我的薪酬中行合计信息
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setSumItemValue(int param_type,String value){
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"/sumitem";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element("sumitem");
					element.setText(value);
					doc.getRootElement().getChild("mysalarys").addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					element.setText(value);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return bflag;
	}

	/**
	 * 获取我的薪酬中行合计字段信息
	 * @param param_type
	 * @return
	 */
	public String getSumItemValue(int param_type ){
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"/sumitem";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0){
					element=(Element)childlist.get(0);
					value=element.getText();
				}

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return value;
	}
	/**
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String property,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(value==null) {
            value="";
        }
		if("#".equals(value)) {
            value="";
        }
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					element.setAttribute(property,value);
					doc.getRootElement().addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					element.setAttribute(property,value);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				bflag=false;
			}
		}		
		return bflag;
	}	

	/**
	 * 设置节点text内容
	 * @param param_type
	 * @param value
	 * @return
	 */
	public boolean setValue(int param_type,String value)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					element.setText(value);
					doc.getRootElement().addContent(element);
				}
				else
				{
					element=(Element)childlist.get(0);
					element.setText(value);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}		  
		}
		return bflag;
	}
	/**
	 * 取得对应的参数在XML节点的名称
	 * @param param_type
	 * @return
	 */
	private String getElementName(int param_type)
	{
		String name="";
		switch(param_type)
		{
		case UNITTYPE:
			name="unittype";
			break;
		case GOBROAD:
			name="goabroad";
			break;
		case GOBROADSUBSET:
			name="goabroadsubset";
			break;
		case MYSALARYS:
			name="mysalarys";
			break;
		case MYSALARYS_SALARY:
			name="mysalarys/salary";
			break;
		case SALARY:
			name="salary";
			break;
		case BOROWSE_CARD:
			name="browser_card";
			break;
		case EDIT_COLUMNS:
			name="rownums";
			break;
		case PART_TIME:
			name="part_time";
			break;
		case SYNTHETIZE_BOUND:
			name="synthetize_bound";
			break;
		case APPROVE_FLAG:
			name="approve_flag";
			break;
		case ORG_ROOT_DESC:
			name="org_root_caption";
			break;
		case WORKOUT:
			name="workout";
			break;
		case WELCOME_MARQUEE:
			name="welcome_marquee";
			break;
		case NUM_PER_PAGE:
			name="num_per_page";
			break;
		case LIKN_P_HEIGHT:
			name="link_p_height";
			break;
		case    LIKN_P_WIDTH:
			name="link_p_width";
			break;
		case  LAWRULE_FILE_DAYS:
			name="lawrule_file_days";
			break;
		case  STAT_ID:
			name="stat_id";
			break;
		case YKCARD_AUTO:
			name="ykcard_auto";
			break;
		case DIARY:
			name="diary";
			break;
		case PHOTO:
			name="photo";
			break;
		case UPDISK:
			name="updisk";
			break;
		case SUBUNITUP:
			name="subunitup";
			break;
		case EDITUPDISK:
			name="editupdisk";
			break;
		case INPUTCHINFOR:
			name="inputchinfor";
			break;
		case CONDISK:
			name="condisk";
			break;
		case BROWSE_PHOTO:
			name="browse_photo";
			break;
		case INFOSORT_BROWSE:
			name = "infosort_browse";
			break;
		case PINYIN_FIELD_SEARCH:
			name="priyin_field_search";
			break;
		case COMMON_ROSTER:
			name = "common_roster";
			break;
		case ORG_BROWSE_FORMAT:
			name="org_browse_format";
			break;
		case BIRTHDAY_WID:
			name="birthday_wid";
			break;
		case DISPLAY_E0122:
			name = "display_e0122";
			break;
		case UNITS:
			name="units";
			break;
		case PLACE:
			name="place";
			break;
		case CHK_UNIQUENESS:
			name="chk_uniqueness";
			break;
		case VIRTUAL_ORG:
			name="virtual_org";
			break;
		case PHOTO_OTHER_VIEW:
			name="photo_other_view";
			break;
		case ANNOUNCE_DAYS:
			name="announce_days";
			break;
		case BLACKLIST_PER://黑名单人员库
			name="base";
			break;
		case BLACKLIST_FIELD://黑名单人员指标
			name="field";
			break;
		case BLACKLIST://黑名单
			name="blacklist";	
			break;
		case ORGANIZATION:
			name="organization";
			break;
		case GQUERY_COND:
			name="gquery_cond";
			break;
		case BROWSE_SEARCH_STATE:
			name="browse_search_state";
			break;
		case DAIRYINFOLIMIT:
			name="dairyinfolimit";
			break;
		case ISAPPROVE:
			name="IsApprove";
			break;
		case APPROVEID:
			name="ApproveId";
			break;
		case STAFF_INFO_NUM:
			name="staff_info";
			break;
		case COMPLEX_ID_NUM:
			name="complex_id";
			break;
		case CHK_IdTYPE:
			name="id_type";
			break;
		case LOGLEVEL:
			name="loglevel";
			break;
		}
		return name;
	}



	public ReportParamterVo ReadOneParameterXml(String xmlContent,String name)
	{
		String xpath="/param/pageoptions/report[@name='"+name+"']";
		ReportParamterVo revo=new ReportParamterVo();
		if(xmlContent!=null&&xmlContent.length()>0)
		{
			try
			{
				Document doc=PubFunc.generateDom(xmlContent);//读入xml
				XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点

				List childlist=reportPath.selectNodes(doc);  

				Iterator i = childlist.iterator();
				if(i.hasNext())
				{
					/**报表基本参数**/
					Element childR=(Element)i.next();
					revo.setName(childR.getAttributeValue("name")); //报表名称
					//Element attributes_item=childR.getChild("attributes");  zzk 紙張大小等屬性保存在report 节点下
					revo.setPagetype(childR.getAttributeValue("pagetype"));//报表纸张
					revo.setUnit(childR.getAttributeValue("unit"));//报表长度单位
					revo.setOrientation(childR.getAttributeValue("orientation"));//纸张方向
					revo.setTop(childR.getAttributeValue("top"));//报表头边距
					revo.setLeft(childR.getAttributeValue("left"));//报表左边距
					revo.setRight(childR.getAttributeValue("right"));//报表右边距				   
					revo.setBottom(childR.getAttributeValue("bottom"));//报表尾边距
					revo.setValue(childR.getAttributeValue("value"));//值
					revo.setWidth(childR.getAttributeValue("width"));//纸的长宽
					revo.setHeight(childR.getAttributeValue("height"));//纸的长宽				  			   
					/**节点,报表标题**/
					Element title_item=childR.getChild("title");
					revo.setTitle_c(title_item.getAttributeValue("content"));				  
					/**节点,报表表头**/
					Element head_item=childR.getChild("head");
					revo.setHead_c(head_item.getAttributeValue("content"));

					/**节点,报表表尾**/
					Element tile_item=childR.getChild("tile");
					revo.setTile_c(tile_item.getAttributeValue("content"));

					/**节点,报表表体**/
					Element body_item=childR.getChild("body");
					revo.setBody_c(body_item.getAttributeValue("content"));	
				}			    
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return revo;
	}

	public  String  WriteOutParameterXml(ReportParseVo parsevo,String name,UserView userView,Connection conn)
	{
		if(name==null||name.length()<=0) {
            return null;
        }
		String xmlContent=search_SYS_PARAMETER(conn);
		ReportParseXml reportParseXml=new ReportParseXml();
		ReportParamterVo revo=reportParseXml.getReportParamterVoFromReportParseVo(parsevo);
		if(xmlContent!=null&&xmlContent.length()>0)
		{
			try{
				Document doc=PubFunc.generateDom(xmlContent);//读入xml
				String xpath="/param/pageoptions/report[@name='"+name+"']";
				XPath reportPath = XPath.newInstance(xpath);// 取得根节点
				List childlist=reportPath.selectNodes(doc);
				Iterator t = childlist.iterator();
				if(t.hasNext())
				{
					Element childR=(Element)t.next();	    	  
					childR.setAttribute("name",name);
					//Element attributes_item=childR.getChild("attributes");
					childR.setAttribute("pagetype",revo.getPagetype());
					childR.setAttribute("unit",revo.getUnit());
					childR.setAttribute("orientation",revo.getOrientation());
					childR.setAttribute("top",revo.getTop());
					childR.setAttribute("left",revo.getLeft());
					childR.setAttribute("right",revo.getRight());
					childR.setAttribute("bottom",revo.getBottom());
					childR.setAttribute("value",revo.getValue());
					childR.setAttribute("width",revo.getWidth());
					childR.setAttribute("height",revo.getHeight());

					/**节点,报表标题**/
					Element title_item=childR.getChild("title");
					title_item.setAttribute("content",revo.getTitle_c());	   

					/**节点,报表表头**/
					Element head_item=childR.getChild("head");
					head_item.setAttribute("content",revo.getHead_c());		   

					/**节点,报表表尾**/
					Element tile_item=childR.getChild("tile");
					tile_item.setAttribute("content",revo.getTile_c());		  

					/**节点,报表表体**/
					Element body_item=childR.getChild("body");
					body_item.setAttribute("content",revo.getBody_c());			   
					/**节点，页脚**/			  		   

					XMLOutputter outputter = new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);  	           
					xmlContent=outputter.outputString(doc);          
				}else
				{
					xpath="/param/pageoptions";
					reportPath = XPath.newInstance(xpath);// 取得根节点
					List list=reportPath.selectNodes(doc);
					Iterator r = list.iterator();
					if(r.hasNext())
					{   
						Element childR=(Element)r.next();
						Element report = new Element("report");		
						report.setAttribute("name",name);
						Element attributes_item=new Element("attributes");		    	 		 
						attributes_item.setAttribute("pagetype",revo.getPagetype());
						attributes_item.setAttribute("unit",revo.getUnit());
						attributes_item.setAttribute("orientation",revo.getOrientation());
						attributes_item.setAttribute("top",revo.getTop());
						attributes_item.setAttribute("left",revo.getLeft());
						attributes_item.setAttribute("right",revo.getRight());
						attributes_item.setAttribute("bottom",revo.getBottom());
						attributes_item.setAttribute("value",revo.getValue());
						attributes_item.setAttribute("width",revo.getWidth());
						attributes_item.setAttribute("height",revo.getHeight());
//						标题
						Element title_item =new Element("title");	 	   
						title_item.setAttribute("content",revo.getTitle_c());
						/**节点,报表表头**/
						Element head_item = new Element("head");	 	   
						head_item.setAttribute("content",revo.getHead_c());		   
						/**节点,报表表尾**/
						Element tile_item=new Element("tile");
						tile_item.setAttribute("content",revo.getTile_c());		  
						/**节点,报表表体**/
						Element body_item=new Element("body");
						body_item.setAttribute("content",revo.getBody_c());	
						childR.addContent(report);	
						report.addContent(attributes_item);
						report.addContent(title_item);
						report.addContent(head_item);
						report.addContent(tile_item);
						report.addContent(body_item);
						XMLOutputter outputter = new XMLOutputter();
						Format format=Format.getPrettyFormat();
						format.setEncoding("UTF-8");
						outputter.setFormat(format);  	           
						xmlContent=outputter.outputString(doc);			  	           
					}else
					{
						xpath="/param";
						reportPath = XPath.newInstance(xpath);// 取得根节点
						list=reportPath.selectNodes(doc);
						r = list.iterator();
						if(r.hasNext())
						{   
							Element childR=(Element)r.next();
							Element pageoptions = new Element("pageoptions");
							Element report = new Element("report");		
							report.setAttribute("name",name);
							Element attributes_item=new Element("attributes");		    	 		 
							attributes_item.setAttribute("pagetype",revo.getPagetype());
							attributes_item.setAttribute("unit",revo.getUnit());
							attributes_item.setAttribute("orientation",revo.getOrientation());
							attributes_item.setAttribute("top",revo.getTop());
							attributes_item.setAttribute("left",revo.getLeft());
							attributes_item.setAttribute("right",revo.getRight());
							attributes_item.setAttribute("bottom",revo.getBottom());
							attributes_item.setAttribute("value",revo.getValue());
							attributes_item.setAttribute("width",revo.getWidth());
							attributes_item.setAttribute("height",revo.getHeight());
							//标题
							Element title_item =new Element("title");	 	   
							title_item.setAttribute("content",revo.getTitle_c());
							/**节点,报表表头**/
							Element head_item = new Element("head");	 	   
							head_item.setAttribute("content",revo.getHead_c());		   
							/**节点,报表表尾**/
							Element tile_item=new Element("tile");
							tile_item.setAttribute("content",revo.getTile_c());		  
							/**节点,报表表体**/
							Element body_item=new Element("body");
							body_item.setAttribute("content",revo.getBody_c());	
							childR.addContent(pageoptions);	
							pageoptions.addContent(report);
							report.addContent(attributes_item);
							report.addContent(title_item);
							report.addContent(head_item);
							report.addContent(tile_item);
							report.addContent(body_item);
							XMLOutputter outputter = new XMLOutputter();
							Format format=Format.getPrettyFormat();
							format.setEncoding("UTF-8");
							outputter.setFormat(format);  	           
							xmlContent=outputter.outputString(doc);	
						}else
						{
							Element param = new Element("param");
							Element pageoptions = new Element("pageoptions");
							Element report = new Element("report");		
							report.setAttribute("name",name);
							Element attributes_item=new Element("attributes");		    	 		 
							attributes_item.setAttribute("pagetype",revo.getPagetype());
							attributes_item.setAttribute("unit",revo.getUnit());
							attributes_item.setAttribute("orientation",revo.getOrientation());
							attributes_item.setAttribute("top",revo.getTop());
							attributes_item.setAttribute("left",revo.getLeft());
							attributes_item.setAttribute("right",revo.getRight());
							attributes_item.setAttribute("bottom",revo.getBottom());
							attributes_item.setAttribute("value",revo.getValue());
							attributes_item.setAttribute("width",revo.getWidth());
							attributes_item.setAttribute("height",revo.getHeight());
							//标题
							Element title_item =new Element("title");	 	   
							title_item.setAttribute("content",revo.getTitle_c());
							/**节点,报表表头**/
							Element head_item = new Element("head");	 	   
							head_item.setAttribute("content",revo.getHead_c());		   
							/**节点,报表表尾**/
							Element tile_item=new Element("tile");
							tile_item.setAttribute("content",revo.getTile_c());		  
							/**节点,报表表体**/
							Element body_item=new Element("body");
							body_item.setAttribute("content",revo.getBody_c());	
							param.addContent(pageoptions);	
							pageoptions.addContent(report);
							report.addContent(attributes_item);
							report.addContent(title_item);
							report.addContent(head_item);
							report.addContent(tile_item);
							report.addContent(body_item);
							Document myDocument = new Document(param);
							XMLOutputter outputter = new XMLOutputter();			                 
							Format format=Format.getPrettyFormat();
							format.setEncoding("UTF-8");
							outputter.setFormat(format);  	           
							xmlContent=outputter.outputString(myDocument);		
						}
					}		    	   
				}		 

			}catch(Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			xmlContent=createPageOptionsXML(parsevo,name,"","");
		}
		return xmlContent;
	}
	/**
	 * 检索常量表中纪录为KQ_PARAMETER的Constant得自短
	 * **/
	public String search_SYS_PARAMETER(Connection conn)
	{
		StringBuffer sb=new StringBuffer();
		sb.append("select Str_Value from constant where UPPER(Constant)='SYS_OTH_PARAM'");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;
		String xmlConstant="";
		try
		{
			rowSet=dao.search(sb.toString());
			if(rowSet.next())
			{
				xmlConstant=Sql_switcher.readMemo(rowSet,"Str_Value");				  
			}else
			{
				insert_XMLData(conn,"");
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return xmlConstant;
	}	 
	/**
	 * 添加纪录
	 * */
	public String insert_XMLData(Connection conn,String xmlContent)
	{
		ArrayList deletelist = new ArrayList();
		deletelist.add("SYS_OTH_PARAM");
		String deleteSQL="delete from constant where Constant=?";
		ContentDAO dao = new ContentDAO(conn);
		String xmltype="false";
		try
		{
			dao.delete(deleteSQL,deletelist);
			StringBuffer insertSQL=new StringBuffer();
			insertSQL.append("insert into constant (Constant,Type,Describe,Str_Value)");
			insertSQL.append(" values (?,?,?,?)");
			ArrayList insertlist=new ArrayList();
			insertlist.add("SYS_OTH_PARAM");
			insertlist.add("A");
			insertlist.add("系统参数");
			insertlist.add(xmlContent);
			dao.insert(insertSQL.toString(),insertlist);
			xmltype="ok";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return xmltype;
	}

	public String createPageOptionsXML(ReportParseVo parsevo,String name ,String type,String descript){
		ReportParseXml reportParseXml=new ReportParseXml();
		ReportParamterVo revo=reportParseXml.getReportParamterVoFromReportParseVo(parsevo);
		String temp = null;
		//根节点

		Element param = new Element("param");

		if(name!=null&&name.length()>0)
		{
			Element pageoptions = new Element("pageoptions");
			Element report = new Element("report");	
			report.setAttribute("name",name);
//			属性
			Element attributes_item =new Element("attributes");
			attributes_item.setAttribute("pagetype",revo.getPagetype()!=null?revo.getPagetype():"A4");
			attributes_item.setAttribute("unit",revo.getUnit()!=null?revo.getUnit():"px");
			attributes_item.setAttribute("orientation",revo.getOrientation()!=null?revo.getOrientation():"0");
			attributes_item.setAttribute("top",revo.getTop()!=null?revo.getTop():"");
			attributes_item.setAttribute("left",revo.getLeft()!=null?revo.getLeft():"");
			attributes_item.setAttribute("right",revo.getRight()!=null?revo.getRight():"");
			attributes_item.setAttribute("bottom",revo.getBottom()!=null?revo.getBottom():"");
			attributes_item.setAttribute("value",revo.getValue()!=null?revo.getValue():"");
			attributes_item.setAttribute("width",revo.getWidth()!=null?revo.getWidth():"210");
			attributes_item.setAttribute("height",revo.getHeight()!=null?revo.getHeight():"297");
			report.addContent(attributes_item);
			//标题
			Element title_item =new Element("title");	 	   
			title_item.setAttribute("content",revo.getTitle_c());
			report.addContent(title_item);
			/**节点,报表表头**/
			Element head_item = new Element("head");	 	   
			head_item.setAttribute("content",revo.getHead_c());		   
			report.addContent(head_item);
			/**节点,报表表尾**/
			Element tile_item=new Element("tile");
			tile_item.setAttribute("content",revo.getTile_c());		  
			report.addContent(tile_item);
			/**节点,报表表体**/
			Element body_item=new Element("body");
			body_item.setAttribute("content",revo.getBody_c());	
			report.addContent(body_item);
			pageoptions.addContent(report);			
			param.addContent(pageoptions);
		}
		Element unittype = new Element("unittype");	
		unittype.setAttribute("type",type);
		unittype.setAttribute("descript",descript);


		param.addContent(unittype);


		Document myDocument = new Document(param);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);

		/**System.out.println("*********创建XML**************");
			System.out.println(temp);
			System.out.println("********************");*/
		return temp;
	}
	/***********单位属性 **********/
	/**
	 * 得到单位属性
	 */
	public String getUnittype(String xmlContent)
	{
		if(xmlContent==null||xmlContent.length()<=0) {
            return "";
        }
		String type="";
		try{
			Document doc=PubFunc.generateDom(xmlContent);;//读入xml
			String xpath="/param/unittype";
			XPath reportPath = XPath.newInstance(xpath);// 取得根节点
			List childlist=reportPath.selectNodes(doc);
			Iterator t = childlist.iterator();
			if(t.hasNext())
			{
				Element childR=(Element)t.next();
				type=childR.getAttributeValue("type"); //报表名称
			}
		}catch(Exception e )
		{
			e.printStackTrace();
		}
		return type;
	} 
	/**
	 * 保存数据
	 * @param xmlContent
	 * @param type
	 */
	public void setUnittype(String xmlContent,String type,Connection conn)
	{
		if(type==null||type.length()<=0) {
            return;
        }

		String descript=getDescript(type);
		if(xmlContent!=null&&xmlContent.length()>0)
		{

			try{
				Document doc=PubFunc.generateDom(xmlContent);;//读入xml
				String xpath="/param/unittype";
				XPath reportPath = XPath.newInstance(xpath);// 取得根节点
				List childlist=reportPath.selectNodes(doc);
				Iterator t = childlist.iterator();
				if(t.hasNext())
				{
					Element childR=(Element)t.next(); 
					childR.setAttribute("type",type);
					childR.setAttribute("descript",descript);
					XMLOutputter outputter = new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);  	           
					xmlContent=outputter.outputString(doc);  

				}else
				{
					xpath="/param";
					reportPath = XPath.newInstance(xpath);// 取得根节点
					List list=reportPath.selectNodes(doc);
					Iterator r = list.iterator();
					if(r.hasNext())
					{   
						Element unittype=new Element("unittype");			               
						unittype.setAttribute("type",type);
						unittype.setAttribute("descript",descript);
						XMLOutputter outputter = new XMLOutputter();
						Format format=Format.getPrettyFormat();
						format.setEncoding("UTF-8");
						outputter.setFormat(format);  	           
						xmlContent=outputter.outputString(doc);		
					}        
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}else
		{
			ReportParseVo parsevo=new ReportParseVo ();
			xmlContent=createPageOptionsXML(parsevo,"",type,descript);
		}
		//System.out.println(xmlContent);
		insert_XMLData(conn,xmlContent);
	}
	private String getDescript(String type)
	{
		String descript="";
		if("1".equals(type))
		{
			descript=ResourceFactory.getProperty("sys.options.param.descript1");
		}else if("2".equals(type))
		{
			descript=ResourceFactory.getProperty("sys.options.param.descript2");
		}else if("3".equals(type))
		{
			descript=ResourceFactory.getProperty("sys.options.param.descript3");
		}else if("4".equals(type))
		{
			descript=ResourceFactory.getProperty("sys.options.param.descript4");
		}else if("5".equals(type))
		{
			descript=ResourceFactory.getProperty("sys.options.param.descript5");
		}
		return descript;
	}

	public void if_SysConstant_Save(String constant)
	{
		String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs=null;
		try
		{
			rs=dao.search(sql);		  
			if(!rs.next())
			{
				insertNewSys(constant);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		 
	}
	/**
	 * 插入新的
	 * @param constant
	 */
	public void insertNewSys(String constant)
	{
		String insert="insert into constant(Constant) values (?)";
		ArrayList list=new ArrayList();
		list.add(constant.toUpperCase());			
		ContentDAO dao = new ContentDAO(conn);
		try
		{
			dao.insert(insert,list);		  

		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	public boolean if_vo_Empty(String constant)
	{
		String sql="select * from constant where UPPER(Constant)='"+constant.toUpperCase()+"'";
		ContentDAO dao = new ContentDAO(conn);
		boolean is_correct=true;
		RowSet rs=null;
		try
		{
			rs=dao.search(sql);		  
			if(!rs.next())
			{
				is_correct=false; 
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return is_correct;
	}
	/**
	 * 设置节点的属性值
	 * @param param_type
	 * @param property
	 * @param value
	 * @return
	 */
	public boolean setValueS(int param_type,int param_type2,String property,ArrayList  values)
	{
		boolean bflag=true;
		String name=getElementName(param_type);
		String name2=getElementName(param_type2);
		int rm=0;
		if(!"".equals(name))
		{
			try
			{
				SearchTableCardConstantSet constantSet=new SearchTableCardConstantSet(this.conn);
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;

				if(childlist.size()==0)
				{
					Element element0=new Element(name);
					for(int i=0;i<values.size();i++)
					{
						rm=(int)Math.round(Math.random()*100);	
						element=new Element(name2);
						element.setAttribute(property,values.get(i).toString());
						String title=constantSet.getFieldsetdesc(values.get(i).toString());
						if(title==null||title.length()<=0) {
                            title="薪酬表";
                        }
						element.setAttribute("title",title+rm+(i+1));
						element0.addContent(element);
					}
					doc.getRootElement().addContent(element0);
				}
				else
				{
					Element element0=(Element)childlist.get(0);					
					for(int i=0;i<values.size();i++)
					{
						List list=element0.getChildren();
						Iterator r = list.iterator();
						String  n_value=values.get(i).toString();
						if(n_value==null||n_value.length()<=0) {
                            n_value="";
                        }
						while(r.hasNext())
						{   
							Element childR=(Element)r.next();
							String old_value=childR.getAttributeValue(property);

							if(old_value!=null&&old_value.equalsIgnoreCase(n_value)) {
                                continue;
                            }
						}
						element=new Element(name2);	
						rm=(int)Math.round(Math.random()*100);
						element.setAttribute(property,values.get(i).toString());
						String title=constantSet.getFieldsetdesc(values.get(i).toString());
						if(title==null||title.length()<=0) {
                            title="薪酬表";
                        }
						element.setAttribute("title",title+rm+(i+1));
						element0.addContent(element);
					}
					//doc.getRootElement().addContent(element0);
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				bflag=false;
			}
		}	
		//see();
		return bflag;
	}	
	/**
	 * 删除制定指标
	 * @param param_type
	 * @param property
	 * @param values
	 * @return
	 */
	public boolean removeContent(int param_type,int param_type2,String property,ArrayList  values)
	{
		boolean bflag=true;
		StringBuffer temp1=null;
		try
		{
			String name=getElementName(param_type);
			String name2=getElementName(param_type2);
			if(!"".equals(name))
			{

				/*String str_path="/param/"+name;
					XPath xpath=XPath.newInstance(str_path);
					List childlist=xpath.selectNodes(doc);
					Element element=null;
					if(childlist.size()!=0)
					{
						Element element0=(Element)childlist.get(0);	
						for(int i=0;i<values.size();i++)
						{
							System.out.println(values.get(i).toString());							
							element=new Element(name2);
							element.setAttribute(property,values.get(i).toString());
							//element0.removeContent(element);
							element0.removeContent(element);
						}
					}*/
				if(values!=null&&values.size()>0)
				{
					for(int i=0;i<values.size();i++)
					{
						String value=values.get(i).toString();
						if(value==null||value.length()<=0) {
                            value="";
                        }
						String value_arr[]=value.split("`");
						String value_1=value_arr[0];
						String value_2="";
						if(value_arr.length>1) {
                            value_2=value_arr[1];
                        }
						temp1 = new StringBuffer();
						temp1.append("/param/"+name+"/"+name2+"[(@"+property+"='"+value_1+"')");
						temp1.append(" and (@title='"+value_2+"')");
						temp1.append("]");						
						XPath xPath = XPath.newInstance(temp1.toString());
						Element TreeNode = (Element) xPath.selectSingleNode(doc);						
						if(TreeNode == null){
						}else{
							TreeNode.getParent().removeContent(TreeNode);
						}
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			bflag=true;
		}			
		return bflag;
	}
	public boolean removeAllContent(int param_type)
	{
		boolean bflag=true;
		StringBuffer temp1=null;
		try
		{
			String name=getElementName(param_type);
			if(!"".equals(name))
			{
				temp1 = new StringBuffer();
				temp1.append("/param/"+name);				    				
				XPath xPath = XPath.newInstance(temp1.toString());
				Element TreeNode = (Element) xPath.selectSingleNode(doc);						
				if(TreeNode == null){
				}else{
					TreeNode.getParent().removeContent(TreeNode);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			bflag=true;
		}			
		return bflag;
	}
	/**
	 * 指定指标值找到指标集
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @param getPro
	 * @return
	 */
	public String  getValue(int param_type,String property,String property_value,String getPro)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[@"+property+"='"+property_value+"']";				
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					if(getPro!=null&&getPro.length()>0)
					{
						value=element.getAttributeValue(getPro);
					}else
					{
						value=element.getText();
					}					
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;		
	}
	/**
	 * 得到孩子的指标
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @return
	 */
	public String getChildText(int param_type,String property,String property_value)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[@"+property+"='"+property_value+"']";

				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);					
					childlist=element.getChildren();
					if(childlist.size()!=0)
					{
						element=(Element)childlist.get(0);	
						value=element.getText();
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;	
	}
	/**
	 * 指定指标值找到指标集
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @param getPro
	 * @return
	 */
	public boolean  setValue(int param_type,String property,String property_value,String getPro,String value)
	{
		boolean isCorrect=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[@"+property+"='"+property_value+"']";				
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					if(getPro!=null&&getPro.length()>0)
					{
						element.setAttribute(getPro,value);
					}else
					{
						element.setText(value);
					}					
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				isCorrect=false;
			}
		}		
		return isCorrect;		
	}
	/**
	 * 得到孩子的指标
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @return
	 */
	public boolean setChildText(int param_type,String property,String property_value,String childName,String value)
	{
		boolean isCorrect=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[@"+property+"='"+property_value+"']";

				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);					
					childlist=element.getChildren();
					if(childlist.size()!=0)
					{
						element.setText(value);
					}else
					{
						Element childElement=new Element(childName);
						childElement.setText(value);
						element.addContent(childElement);
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				isCorrect =false;
			}
		}				
		return isCorrect;	
	}
	/**
	 * 指定指标值找到指标集
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @param getPro
	 * @return
	 */
	public String  getValue(int param_type,String property,String property_value,String property2,String property_value2,String getPro)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[(@"+property+"='"+property_value+"')";
				str_path=str_path+" and (@"+property2+"='"+property_value2+"')]";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					if(getPro!=null&&getPro.length()>0)
					{
						value=element.getAttributeValue(getPro);
					}else
					{
						value=element.getText();
					}					
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		//see();
		return value;				
	}
	/**
	 * 得到孩子的指标
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @return
	 */
	public String getChildText(int param_type,String property,String property_value,String property2,String property_value2)
	{
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[(@"+property+"='"+property_value+"')";
				str_path=str_path+" and (@"+property2+"='"+property_value2+"')]";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);					
					childlist=element.getChildren();
					if(childlist.size()!=0)
					{
						element=(Element)childlist.get(0);	
						value=element.getText();
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;	
	}
	public void see()
	{
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		System.out.println(outputter.outputString(doc));
	}
	/**
	 * 指定指标值找到指标集
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @param getPro
	 * @return
	 */
	public int  getCheckValue(int param_type,String property,String property_value,String property2,String property_value2)
	{
		int num=0;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[(@"+property+"='"+property_value+"')";
				str_path=str_path+" and (@"+property2+"='"+property_value2+"')]";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				num=childlist.size();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return num;		
	}
	/**
	 * 指定指标值找到指标集
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @param getPro
	 * @return
	 */
	public boolean  setValue(int param_type,String property,String property_value,String property2,String property_value2,String getPro,String value)
	{
		boolean isCorrect=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[(@"+property+"='"+property_value+"')";
				str_path=str_path+" and (@"+property2+"='"+property_value2+"')]";			
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					if(getPro!=null&&getPro.length()>0)
					{
						element.setAttribute(getPro,value);
					}else
					{
						element.setText(value);
					}					
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				isCorrect=false;
			}
		}		
		return isCorrect;		
	}
	/**
	 * 得到孩子的指标
	 * @param param_type
	 * @param property
	 * @param property_value
	 * @return
	 */
	public boolean setChildText(int param_type,String property,String property_value,String property2,String property_value2,String childName,String value)
	{
		boolean isCorrect=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name+"[(@"+property+"='"+property_value+"')";
				str_path=str_path+" and (@"+property2+"='"+property_value2+"')]";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);					
					childlist=element.getChildren();
					if(childlist.size()!=0)
					{
						element.setText(value);
					}else
					{
						Element childElement=new Element(childName);
						childElement.setText(value);
						element.addContent(childElement);
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				isCorrect =false;
			}
		}				
		return isCorrect;	
	}

	/**
	 * 将文件转化成字符串
	 * @param doc 文件
	 * @return
	 */
	public String docToString(){
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		// 创建的XML文件以字符串形式表示
		String docStr = outputter.outputString(doc);
		return docStr;
	}



	/**
	 * 排序
	 * @param param_type
	 * @param property
	 * @param property2
	 * @param property_list
	 * @return
	 */
	public boolean setOrderChildText(int param_type,String property,String property2,ArrayList property_list)
	{
		boolean bflag=true;
		String name=getElementName(param_type);		
		ArrayList childlist=new ArrayList();
		if(!"".equals(name))
		{
			try
			{
				for(int i=0;i<property_list.size();i++)
				{
					String values=property_list.get(i).toString();
					String[] one_Array=values.split("`");
					String setname=one_Array[0];
					String title="";
					if(one_Array.length>1) {
                        title=one_Array[1];
                    }
					String str_path="/param/"+name+"[(@"+property+"='"+setname+"')";
					str_path=str_path+" and (@"+property2+"='"+title+"')]";					
					XPath xpath=XPath.newInstance(str_path);
					Element element = (Element) xpath.selectSingleNode(doc);						
					if(element == null){
					}else{
						childlist.add(element);
						element.getParent().removeContent(element);
					}					
				}					
				String name2=getElementName(Sys_Oth_Parameter.MYSALARYS);		
				String str_path="/param/"+name2;
				XPath xpath=XPath.newInstance(str_path);
				List paramlist=xpath.selectNodes(doc);
				Element element=null;
				Element ChildR=null;
				if(paramlist.size()!=0)
				{
					element=(Element)paramlist.get(0);

					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							ChildR=(Element)childlist.get(i);
							element.addContent(ChildR);
						}
					}
				}	
			}			  
			catch(Exception ex)
			{
				ex.printStackTrace();
				bflag=false;
			}
		}				
		return bflag;
	}	

	/**
	 * 得到多个属性的值
	 * @param param_type
	 * @param list
	 * @return
	 */
	public HashMap getAttributeValues(int param_type,ArrayList list){
		HashMap map = new HashMap();
		String name=getElementName(param_type);
		if(!"".equals(name)){
			try{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				Element element=(Element)xpath.selectSingleNode(doc);
				if(element!=null){
					for(int i=0;i<list.size();i++){
						if(list.get(i)!=null&& "setid".equals(list.get(i).toString()))
						{
							if(element.getAttributeValue(list.get(i).toString())!=null&&element.getAttributeValue(list.get(i).toString()).length()>0)
							{
								FieldSet vo=DataDictionary.getFieldSetVo(element.getAttributeValue(list.get(i).toString())) ;
								if(vo!=null&& "1".equals(vo.getUseflag())) {
                                    map.put(list.get(i).toString(),element.getAttributeValue(list.get(i).toString()));
                                } else {
                                    map.put(list.get(i).toString(),"");
                                }
							}

						}else if(list.get(i)!=null&& "unit".equals(list.get(i).toString())||list.get(i)!=null&& "appoint".equals(list.get(i).toString())||list.get(i)!=null&& "pos".equals(list.get(i).toString())||list.get(i)!=null&& "dept".equals(list.get(i).toString())||list.get(i)!=null&& "order".equals(list.get(i).toString())||list.get(i)!=null&& "format".equals(list.get(i).toString()))
						{
							if(element.getAttributeValue(list.get(i).toString())!=null&&element.getAttributeValue(list.get(i).toString()).length()>0)
							{
								FieldItem  vo=DataDictionary.getFieldItem(element.getAttributeValue(list.get(i).toString())) ;
								if(vo!=null&& "1".equals(vo.getUseflag())) {
                                    map.put(list.get(i).toString(),element.getAttributeValue(list.get(i).toString()));
                                } else {
                                    map.put(list.get(i).toString(),"");
                                }
							}
						}else {
                            map.put(list.get(i).toString(),element.getAttributeValue(list.get(i).toString()));
                        }
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 得到一个属性的值
	 * @param param_type
	 * @param name
	 * @return
	 */
	public String getAttributeValues(int param_type,String  attributeName){

		String name=getElementName(param_type);
		String value="";
		if(!"".equals(name)){
			try{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				Element element=(Element)xpath.selectSingleNode(doc);
				if(element!=null){

					value=element.getAttributeValue(attributeName);

				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return value;
	}


	/**
	 * 设置带子标签的属性
	 */
	public boolean setValue(int param_type,String elementtype,String attributetname,String value){
		boolean bflag=true;
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()==0)
				{
					element=new Element(name);
					Element child = new Element("field");
					child.setAttribute("type",elementtype);
					child.setAttribute(attributetname,value);
					element.addContent(child);
					doc.getRootElement().addContent(element);
				}
				else
				{
					boolean b = true;
					element=(Element)childlist.get(0);
					List list = element.getChildren("field");
					for(int i=0;i<list.size();i++){
						Element element1 = (Element)list.get(i);
						if(element1.getAttributeValue("type").equalsIgnoreCase(elementtype)){
							element1.setAttribute(attributetname,value);
							b= false;
						}
					}
					if(b){
						Element child = new Element("field");
						child.setAttribute("type",elementtype);
						child.setAttribute(attributetname,value);
						element.addContent(child);
						doc.getRootElement().addContent(element);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
		}
		return bflag;
	}
	public String getCHKValue(int param_type,String attributettypevale,String attributettype){
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					List list = element.getChildren("field");
					for(int i=0;i<list.size();i++){
						Element element1 = (Element)list.get(i);
						String type =element1.getAttributeValue("type");
						if(type.equalsIgnoreCase(attributettypevale)){
							value = element1.getAttributeValue(attributettype);
						}
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;
	}

	public String getCHKValue(int param_type,String attributettypevale,String attributettype,String valid2){
		String value="";
		String name=getElementName(param_type);
		if(!"".equals(name))
		{
			try
			{
				String str_path="/param/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				Element element=null;
				if(childlist.size()!=0)
				{
					element=(Element)childlist.get(0);
					List list = element.getChildren("field");
					for(int i=0;i<list.size();i++){
						Element element1 = (Element)list.get(i);
						String type =element1.getAttributeValue("type");
						String valid = element1.getAttributeValue("valid");
						if(type.equalsIgnoreCase(attributettypevale)&&"1".equals(valid)){
							value = element1.getAttributeValue(attributettype);
						}
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return value;
	}
}
