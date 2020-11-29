package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateTableParamBo {
	private Connection conn=null;
	/**模板对象内容*/
	private RecordVo table_vo=null;
	private int tabid=0;
	private UserView userview;
	private String kq_type="";            // kq_type:考勤方式 1:加班申请 q11  2:请假申请 q15   3：公出申请 q13
	private String kq_setid="";
	private String kq_field_mapping="";   // kq_field_mapping:指标对应关系
	
	
	
	public TemplateTableParamBo(int tabid,Connection conn)throws GeneralException
	{
		this.conn = conn;
		this.tabid = tabid;
		this.userview=userview;
		this.table_vo=readTemplate(tabid);
		initdata();	
	}
	
	
	public TemplateTableParamBo(Connection conn)throws GeneralException
	{
		this.conn = conn;  
	}
	
	
	/**初始化数据*/
	private void initdata()
	{
		String sxml=null;
		if(this.table_vo!=null)
		{
			sxml=this.table_vo.getString("ctrl_para");
			parse_xml_param(sxml);			
			 
		}
	}
	
	
	/**
	 * 获得加班申请关联的模板列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getOvertimeTemplateList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			RowSet rowSet=dao.search("select tabid,name,ctrl_para from Template_table  where "+_static+"=1");
			Document doc=null;
			Element element=null;
			String xpath="/params/sp_flag";
			while(rowSet.next())
			{
				String ctrl_para=Sql_switcher.readMemo(rowSet,"ctrl_para");
				if(ctrl_para==null||ctrl_para.trim().length()==0) {
                    continue;
                }
				doc=PubFunc.generateDom(ctrl_para);;
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("kq_type")!=null&&element.getAttribute("kq_field_mapping")!=null)
					{
						String _kq_type=((String)element.getAttributeValue("kq_type")).trim();
						String _kq_field_mapping=(String)element.getAttributeValue("kq_field_mapping"); 
						if(_kq_type!=null&& "1".equalsIgnoreCase(_kq_type.trim()))
						{ 
							if(_kq_field_mapping!=null&&_kq_field_mapping.trim().length()>0)
							{
								CommonData da=new CommonData(rowSet.getString("tabid"),rowSet.getString("name"));
								list.add(da);
							}
						}
					}
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	//获得定义了考勤参数的人事异动模板
	public HashMap getDefineKqParamInfo()throws GeneralException
	{
		String _static= "static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select tabid,ctrl_para from Template_table  where "+_static+"=1");
			Document doc=null;
			Element element=null;
			String xpath="/params/sp_flag";
			while(rowSet.next())
			{
				String tabid=rowSet.getString("tabid"); 
				String ctrl_para=Sql_switcher.readMemo(rowSet,"ctrl_para");
				if(ctrl_para==null||ctrl_para.trim().length()==0) {
                    continue;
                }
				doc=PubFunc.generateDom(ctrl_para);;
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("kq_type")!=null)
					{
						String _kq_type=((String)element.getAttributeValue("kq_type")).trim();
						if(_kq_type!=null&&_kq_type.trim().length()>0)
						{
							if(map.get(_kq_type)!=null)
							{
								ArrayList list=(ArrayList)map.get(_kq_type);
								list.add(tabid);
							}
							else
							{
								ArrayList list=new ArrayList();
								list.add(tabid);
								map.put(_kq_type,list);
							}
						}
					}
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	
	
	/**
	 * 获得定义了考勤参数的模板
	 * @type 0:所有  1:加班  2：请假  3:公出
	 * @return
	 * @throws GeneralException
	 */
	public String getAllDefineKqTabs(int type)throws GeneralException
	{
		String tabids="";
		HashMap kqParamMap=getDefineKqParamInfo(); 
		if(type==0)
		{
			if(kqParamMap.get("1")!=null)
			{
				ArrayList tabList=(ArrayList)kqParamMap.get("1");
				for(int i=0;i<tabList.size();i++) {
                    tabids+=","+(String)tabList.get(i);
                }
			}
			if(kqParamMap.get("2")!=null)
			{
				ArrayList tabList=(ArrayList)kqParamMap.get("2");
				for(int i=0;i<tabList.size();i++) {
                    tabids+=","+(String)tabList.get(i);
                }
			}
			if(kqParamMap.get("3")!=null)
			{
				ArrayList tabList=(ArrayList)kqParamMap.get("3");
				for(int i=0;i<tabList.size();i++) {
                    tabids+=","+(String)tabList.get(i);
                }
			}
		}
		else
		{
			if(kqParamMap.get(String.valueOf(type))!=null)
			{
				ArrayList tabList=(ArrayList)kqParamMap.get(String.valueOf(type));
				for(int i=0;i<tabList.size();i++) {
                    tabids+=","+(String)tabList.get(i);
                }
			}
		}
		return tabids;
	}
	
	
	//是否为人事异动模板定义了考勤参数
	public boolean isDefineKqParam()throws GeneralException
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			RowSet rowSet=dao.search("select tabid,ctrl_para from Template_table   where "+_static+"=1");
			Document doc=null;
			Element element=null;
			String xpath="/params/sp_flag";
			while(rowSet.next())
			{
				String tabid=rowSet.getString("tabid"); 
				String ctrl_para=Sql_switcher.readMemo(rowSet,"ctrl_para");
				if(ctrl_para==null||ctrl_para.trim().length()==0) {
                    continue;
                }
				doc=PubFunc.generateDom(ctrl_para);;
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("kq_type")!=null)
					{
						String _kq_type=((String)element.getAttributeValue("kq_type")).trim();
						if(_kq_type!=null&&_kq_type.trim().length()>0)
						{
							flag=true;
							break;
						}
					}
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return flag;
	}
	
	
	// 判断当前模板是否考勤模板
	public boolean isKqTempalte(int tabid)throws GeneralException
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			RowSet rowSet=dao.search("select ctrl_para from Template_table where "+_static+"=1 and TabId="+tabid);
			Document doc=null;
			Element element=null;
			String xpath="/params/sp_flag";
			if(rowSet.next())
			{
				String ctrl_para=Sql_switcher.readMemo(rowSet,"ctrl_para");
				if(ctrl_para==null||ctrl_para.trim().length()==0) {
                    return false;
                }
				doc=PubFunc.generateDom(ctrl_para);;
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("kq_type")!=null)
					{
						String _kq_type=((String)element.getAttributeValue("kq_type")).trim();
						if(_kq_type!=null&&_kq_type.trim().length()>0)
						{
							flag=true;
						}
					}
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
		return flag;
	}	
	/**
	 * 解释业务模板定义的参数
	 * @param sxml
	 * @return
	 */
	private boolean parse_xml_param(String sxml)
	{
		boolean bflag=true;
		Document doc=null;
		Element element=null;
		if(sxml==null|| "".equals(sxml)) {
            return false;
        }
		try
		{
			doc=PubFunc.generateDom(sxml);;
			String xpath="/params/sp_flag";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttribute("kq_type")!=null)
				{
					this.kq_type=((String)element.getAttributeValue("kq_type")).trim();
					// kq_type:考勤方式 1:加班申请 q11  2:请假申请 q15   3：公出申请 q13
					if("1".equals(this.kq_type)) {
                        this.kq_setid="q11";
                    } else if("2".equals(this.kq_type)) {
                        this.kq_setid="q15";
                    } else if("3".equals(this.kq_type)) {
                        this.kq_setid="q13";
                    } else if("4".equals(this.kq_type)) {
                        this.kq_setid="qxj";
                    }
					if(element.getAttribute("kq_field_mapping")!=null) {
                        this.kq_field_mapping=(String)element.getAttributeValue("kq_field_mapping");
                    }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bflag;
	}
	
	
	/**
	 * 取得业务模板的内容
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	private RecordVo readTemplate(int tabid)throws GeneralException
	{
		return TemplateUtilBo.readTemplate(tabid,this.conn);
	}

	public String getKq_type() {
		return kq_type;
	}

	public String getKq_setid() {
		return kq_setid;
	}

	public String getKq_field_mapping() {
		return kq_field_mapping;
	}

}
