/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 18, 20065:39:16 PM
 * @author chenmengqing
 * @version 4.0
 */
public class TemplatePageBo {
	private Connection conn=null;
	private int tabid=-1;
	private int pageid=-1;
	private String title;
	private UserView userView = null;
	/**是否显示*/
	private boolean isprint=true;
	
	private HashMap sub_domain_map = null ;
	private HashMap field_name_map  = new HashMap();//存储人为改变类型的字段
	private String task_id="";
	private String isMobile ="0";//是否是移动页签  默认为0 0代表着不是移动页签 
	private boolean isShow = true;
	public String getIsMobile() {
        return isMobile;
    }
    public void setIsMobile(String isMobile) {
        this.isMobile = isMobile;
    }
    public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public TemplatePageBo(Connection conn,int tabid,int pageid) {
		this.conn=conn;
		this.tabid=tabid;
		this.pageid=pageid;
	}
	public TemplatePageBo(Connection conn,int tabid,int pageid,String task_id) {
		this.conn=conn;
		this.tabid=tabid;
		this.pageid=pageid;
		this.task_id = task_id;
	}
	public TemplatePageBo(Connection conn,int tabid,int pageid,String task_id,UserView userview) {
		this.conn=conn;
		this.tabid=tabid;
		this.pageid=pageid;
		this.task_id = task_id;
		this.userView = userview;
	}
	
	/**
	 * 取得变量表
	 * @return
	 */
	private HashMap getAllVariableHm()
	{
		/*
		StringBuffer strsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap hm=new HashMap();
		try
		{
	//		strsql.append("select * from MidVariable where nflag=0 and templetid= ");
	//		strsql.append(this.tabid);
			strsql.append("select * from midvariable where nflag=0 and templetId <> 0 and (templetId = "+this.tabid+" or cstate = '1')"); //包含共享临时变量 2014-02-22
			strsql.append(" order by sorting");			
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
			
				RecordVo vo=new RecordVo("midvariable");
				vo.setString("cname",rset.getString("cname"));
				vo.setString("chz",rset.getString("chz"));
				vo.setInt("ntype",rset.getInt("ntype"));
				vo.setString("cvalue",rset.getString("cValue"));
				String codesetid=rset.getString("codesetid");
				if(codesetid==null||codesetid.equalsIgnoreCase(""))
					codesetid="0";
				vo.setString("codesetid",codesetid);
				vo.setInt("fldlen",rset.getInt("fldlen"));
				vo.setInt("flddec",rset.getInt("flddec"));
				hm.put(rset.getString("cname"),vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}*/
		HashMap hm=new HashMap();
		if(TemplateStaticDataBo.getAllVariableHm(this.tabid, conn,false)!=null)
			hm=(HashMap)TemplateStaticDataBo.getAllVariableHm(this.tabid, conn,false);
		return hm;
	}
	 
	
	
	/**
	 * 求模板页中所有的指标项(变量、子集区域)列表
	 * @return 列表中存放的是FieldItem对象
	 */
	public ArrayList getAllFieldItem() throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			this.sub_domain_map = new HashMap();
			this.field_name_map = new HashMap();
			ArrayList celllist=getAllCell();
			
			String flag=null;//数据来源
			String temp=null;
			int n =0;
			/**所有变量*/
			HashMap var_hm=getAllVariableHm();
			for(int i=0;i<celllist.size();i++)
			{
				TemplateSetBo setbo=(TemplateSetBo)celllist.get(i);
				if(setbo.getField_name()!=null&& "K0119".equals(setbo.getField_name())){
					int f =0;
					f = f+1;
				}
				boolean flag3 = false;
				if(setbo.isSubflag() && !"F".equalsIgnoreCase(setbo.getFlag()))
				{
					FieldSet fieldset=DataDictionary.getFieldSetVo(setbo.getSetname());
					if(fieldset!=null)
					{
						FieldItem item=new FieldItem();
						item.setFieldsetid(setbo.getSetname());
						
						item.setItemid("t_"+setbo.getSetname().toLowerCase()/*+"_"+setbo.getChgstate()*/);//_(1|2)
					    item.setItemdesc(fieldset.getFieldsetdesc());
					    item.setItemtype("M");
					    /**插入子集*/
					    item.setFormula(setbo.getXml_param());
					    item.setNChgstate(setbo.getChgstate());
					    /**0指标，=1变量，=2子集区域*/
					    item.setVarible(2);
					    fieldlist.add(item);
					    this.sub_domain_map.put(""+n, setbo.getSub_domain_id());
					    this.sub_domain_map.put(""+n+"hz", setbo.getHz().replace("`", ""));
						n++;
					}
					
					continue;
				}
				flag=setbo.getFlag();
				if(flag==null|| "".equals(flag))
					continue;
				if("A".equalsIgnoreCase(flag)|| "B".equalsIgnoreCase(flag)|| "K".equalsIgnoreCase(flag))
				{
					FieldItem item=null;
					String field_name=setbo.getField_name()!=null?setbo.getField_name():"";//原来String field_name=setbo.getField_name()；Oracle库得到null值的话后面报错
					if("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name))
					{
						item=new FieldItem();
						item.setItemid(field_name);
						item.setItemdesc(setbo.getField_hz());
						item.setFieldsetid(setbo.getSetname());
						item.setItemtype(setbo.getField_type());
						item.setCodesetid(setbo.getCodeid()==null?"0":setbo.getCodeid());
						if(!"start_date".equalsIgnoreCase(field_name))
							item.setItemlength(50);
						item.setUseflag("1");
					}
					else
					{ 
						 
						if(DataDictionary.getFieldItem(setbo.getField_name()!=null?setbo.getField_name():"")!=null)
						{
							item=(FieldItem)DataDictionary.getFieldItem(setbo.getField_name()!=null?setbo.getField_name():"").clone();
						//	item.setItemdesc(setbo.getField_hz());
							
							if(!setbo.getField_type().equalsIgnoreCase(item.getItemtype())&&setbo.getHismode()==1) //数据字典与模板指标类型不一致  20140930 dengcan  xcs 特殊的数据  比如说条件定位等等，就更改了字段的类型 2014-10-9
							{
								throw GeneralExceptionHandler.Handle(new Exception(setbo.getField_hz()+" 在模板中定义的指标类型与指标体系不一致，请重新设置模板!"));
							}
						}
					}
					if(item!=null)
					{
						/**可以增加模板指标与字典表指标进行校验*/
						FieldItem tempitem=(FieldItem)item.cloneItem();
						tempitem.setNChgstate(setbo.getChgstate());
						tempitem.setFillable(setbo.isYneed());
						fieldlist.add(tempitem);
					}
				}
				else if ("V".equalsIgnoreCase(flag))
				{
					temp=setbo.getField_name()!=null?setbo.getField_name():"";
					RecordVo vo=(RecordVo)var_hm.get(temp);
					if(vo!=null)
					{
						FieldItem item=varVoToFieldItem(vo);
						fieldlist.add(item);
					}
				}
				else if("P".equalsIgnoreCase(flag))//照片
				{
					FieldItem item=new FieldItem();
					item.setItemid("photo");
					item.setItemdesc(!"".equals(setbo.getHz())?setbo.getHz().substring(0,setbo.getHz().lastIndexOf("`")):"photo");
					item.setFieldsetid("A00");
					item.setItemtype("L");
					item.setCodesetid("0");
					item.setNChgstate(setbo.getChgstate());
					item.setFillable(setbo.isYneed());
					fieldlist.add(item);
					item=new FieldItem();
					item.setItemid("ext");
					item.setItemdesc("ext");
					item.setFieldsetid("A00");
					item.setItemtype("A");
					item.setItemlength(10);
					item.setCodesetid("0");
					fieldlist.add(item);
					//60103 VFS+UTF-8：pc端，二维码入职扫描制作，插入附件和照片，上传附件后，提交的时候报错，如附件所示。
					item=new FieldItem();
					item.setItemid("fileid");
					item.setItemdesc("fileid");
					item.setFieldsetid("A00");
					item.setItemtype("A");
					item.setItemlength(200);
					item.setCodesetid("0");
					fieldlist.add(item);
					this.sub_domain_map.put(""+n, "");
					this.sub_domain_map.put(""+n+"hz", "");
					n++;
				}
				else if("S".equalsIgnoreCase(flag))//电子签章
				{
					FieldItem item=new FieldItem();
					item.setItemid("S_"+setbo.getPagebo().getPageid()+"_"+setbo.getGridno());//签章塞入S_pageid_gridId
					item.setItemdesc(setbo.getHz().replace("`",""));
					item.setFieldsetid("0");
					item.setItemtype("M");
					item.setCodesetid("0");
					fieldlist.add(item);
				}else if("F".equalsIgnoreCase(flag))//附件
				{
					FieldItem item=new FieldItem();
					if(this.userView!=null&&this.userView.getVersion()>=70){
						if("1".equals(setbo.getAttachmentType()))//个人附件
							item.setItemid("attachment_1");
						else//公共附件
							item.setItemid("attachment_0");
					}else{
						item.setItemid("attachment");
					}
					item.setItemdesc(!"".equals(setbo.getHz())?setbo.getHz().substring(0,setbo.getHz().lastIndexOf("`")):"attachment");
					item.setFieldsetid("0");
					item.setItemtype("A");
					item.setCodesetid("0");
					item.setItemlength(20);
					item.setNChgstate(setbo.getChgstate());
					item.setFillable(setbo.isYneed());
					item.setFormula(setbo.getAttachmentXml());//存放附件对应的sub_domain中xml，校验必填根据xml判断此分类是否上传了附件。
					fieldlist.add(item);
				}else{
					flag3 = true;
				}
				if(!flag3){
				this.sub_domain_map.put(""+n, setbo.getSub_domain_id());
				this.sub_domain_map.put(""+n+"hz", setbo.getHz().replace("`", ""));
				n++;
				}
					
			}//for i loop end.
		}
		catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
		return fieldlist;
	}
	

	/**
	 * 求模板页中所有的指标项(变量、子集区域)列表
	 * @hm 指标和单元格对应关系
	 * @return 列表中存放的是FieldItem对象
	 * 
	 */
	public ArrayList getAllFieldItem(HashMap hm)
	{
		this.sub_domain_map = new HashMap();
		this.field_name_map = new HashMap();
		ArrayList celllist=getAllCell();
		ArrayList fieldlist=new ArrayList();
		String flag=null;//数据来源
		String temp=null;
		int n =0;
		/**所有变量*/
		//hm=new HashMap();
		HashMap var_hm=getAllVariableHm();
		for(int i=0;i<celllist.size();i++)
		{
			boolean flag3 = false;
			TemplateSetBo setbo=(TemplateSetBo)celllist.get(i);
			if(setbo.isSubflag())//在是子集的情况下
			{
				FieldSet fieldset=DataDictionary.getFieldSetVo(setbo.getSetname());
				if(fieldset!=null)
				{
					FieldItem item=new FieldItem();
					item.setFieldsetid(setbo.getSetname());
					item.setItemid("t_"+setbo.getSetname().toLowerCase());//_(1|2)
				    item.setItemdesc(fieldset.getFieldsetdesc());
				    item.setItemtype("M");
				    item.setNChgstate(setbo.getChgstate());
				    /**0指标，=1变量，=2子集区域*/
				    item.setVarible(2);
				    fieldlist.add(item);
				    String sub_domain_id = setbo.getSub_domain_id();
				    if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
				    	sub_domain_id="_"+sub_domain_id;
				    hm.put(item.getItemid()+sub_domain_id+"_"+setbo.getChgstate(),setbo);
				    this.sub_domain_map.put(""+n, setbo.getSub_domain_id());
				    this.sub_domain_map.put(""+n+"hz", setbo.getHz().replace("`", ""));
					n++;
				}
				continue;
			}
			flag=setbo.getFlag();
			if(flag==null|| "".equals(flag))
				continue;
			if("A".equalsIgnoreCase(flag)|| "B".equalsIgnoreCase(flag)|| "K".equalsIgnoreCase(flag))//人员库  单位库  职位库
			{
				FieldItem item=null;
				String field_name=setbo.getField_name()!=null?setbo.getField_name():"";
				if("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name))
				{
					item=new FieldItem();
					item.setItemid(field_name);
					item.setItemdesc(setbo.getField_hz());
					item.setFieldsetid(setbo.getSetname());
					item.setItemtype(setbo.getField_type());
					if("codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name))
						item.setCodesetid("0");
					else if("codesetid".equalsIgnoreCase(field_name))
						item.setCodesetid("orgType");
					else
						item.setCodesetid(setbo.getCodeid()==null?"0":setbo.getCodeid());
					if(!"start_date".equalsIgnoreCase(field_name))
						item.setItemlength(50);
					item.setUseflag("1");
				}
				else
				{
					if(DataDictionary.getFieldItem(setbo.getField_name()!=null?setbo.getField_name():"")!=null)
					{
						item=(FieldItem)DataDictionary.getFieldItem(setbo.getField_name()!=null?setbo.getField_name():"").clone();
					//	item.setItemdesc(setbo.getField_hz());
					} 
				}
				
				if(item!=null)
				{
					/**可以增加模板指标与字典表指标进行校验*/
					FieldItem tempitem=(FieldItem)item.cloneItem();
					tempitem.setNChgstate(setbo.getChgstate());
					if("D".equalsIgnoreCase(tempitem.getItemtype())&&setbo.getChgstate()==1)
						tempitem.setFormat(getFormatByDis(setbo.getDisformat()));
					
					if("N".equalsIgnoreCase(tempitem.getItemtype())) //卡片控制数值指标的小数位显示
					{
						tempitem.setDecimalwidth(setbo.getDisformat());
						//tempitem.setFormat(getFormatByDis(setbo.getDisformat()));
					}
					 
					fieldlist.add(tempitem);
					 String sub_domain_id = setbo.getSub_domain_id();
					    if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
					    	sub_domain_id="_"+sub_domain_id;
				    hm.put(item.getItemid()+sub_domain_id+"_"+setbo.getChgstate(),setbo);					
				}
			}
			else if ("V".equalsIgnoreCase(flag))//临时变量
			{
				temp=setbo.getField_name()!=null?setbo.getField_name():"";
				RecordVo vo=(RecordVo)var_hm.get(temp);
				if(vo!=null)
				{
					FieldItem item=varVoToFieldItem(vo);
					fieldlist.add(item);
				}
			}
			else if("P".equalsIgnoreCase(flag))//照片
			{
				FieldItem item=new FieldItem();
				item.setItemid("photo");
				item.setItemdesc("photo");
				item.setFieldsetid("A00");
				item.setItemtype("L");
				item.setCodesetid("0");
				fieldlist.add(item);
				item=new FieldItem();
				item.setItemid("ext");
				item.setItemdesc("ext");
				item.setFieldsetid("A00");
				item.setItemtype("A");
				item.setItemlength(10);
				item.setCodesetid("0");
				fieldlist.add(item);
				this.sub_domain_map.put(""+n, "");
				this.sub_domain_map.put(""+n+"hz", "");
				n++;
			}
			else if("S".equalsIgnoreCase(flag))//电子签章
			{
				FieldItem item=new FieldItem();
				item.setItemid("signature");
				item.setItemdesc("signature");
				item.setFieldsetid("0");
				item.setItemtype("M");
				item.setCodesetid("0");
				fieldlist.add(item);
			}else if("F".equalsIgnoreCase(flag))//附件
			{
				FieldItem item=new FieldItem();
				item.setItemid("attachment");
				item.setItemdesc("attachment");
				item.setFieldsetid("0");
				item.setItemtype("A");
				item.setCodesetid("0");
				item.setItemlength(20);
				fieldlist.add(item);
			}
			else{
				flag3 = true;
			}
			if(!flag3){
			this.sub_domain_map.put(""+n, setbo.getSub_domain_id());
			this.sub_domain_map.put(""+n+"hz", setbo.getHz().replace("`", ""));
			n++;
			}
		}//for i loop end.
		return fieldlist;
	}	
	
	/**
	 * 6. 1991.12.3
	   7. 99.2.23
			1991.2
			1991.02
			98.2
			98.02
			一九九一年六月十一日
			一九九一年六月
			1990年2月10日
			1991年1月
			90年4月10日
			90年6月
			年限
			年份
			月份
			日份
			1991年01月
			1990年01月01日
			1990.01.01
	 * @param disFormat
	 * @return
	 */
	public String getFormatByDis(int disFormat)
	{
		String format="yyyy.MM.dd";
		if(disFormat==6)
				format="yyyy.MM.dd";
		else if(disFormat==7)
				format="yy.MM.dd";
		else if(disFormat==8||disFormat==9)
			format="yyyy.MM";
		else if(disFormat==10||disFormat==11)
			format="yy.MM";
		else if(disFormat==14||disFormat==23||disFormat==12)
			format="yyyy年MM月dd日";
		else if(disFormat==15||disFormat==22||disFormat==13)
			format="yyyy年MM月";
		else if(disFormat==16)
			format="yy年MM月dd日";
		else if(disFormat==17)
			format="yy年MM月";
		else if(disFormat==18)
			format="年限";
		else if(disFormat==19)
			format="yyyy";
		else if(disFormat==20)
			format="MM";
		else if(disFormat==21)
			format="dd";
		return format;
	}
	
	
	/**
	 * 变量转换成FieldItem
	 * @param vo
	 * @return
	 */
	private FieldItem varVoToFieldItem(RecordVo vo)
	{
		FieldItem item=new FieldItem();
		item.setItemdesc(vo.getString("chz"));
		item.setItemid(vo.getString("cname"));
		item.setCodesetid(vo.getString("codesetid"));
		item.setVarible(1);
		item.setFormula(vo.getString("cvalue"));
		item.setItemlength(vo.getInt("fldlen"));
		item.setDecimalwidth(vo.getInt("flddec"));
		if(vo.getInt("ntype")==1)
			item.setItemtype("N");
		else if(vo.getInt("ntype")==2)
			item.setItemtype("A");
		else if(vo.getInt("ntype")==3)
			item.setItemtype("D");
		else if(vo.getInt("ntype")==4)
			item.setItemtype("A");
		
		return item;
	}
	/**
	 * 取得对应标题内容
	 * @return
	 */
	public ArrayList getAllTitle()
	{
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			sql.append("select * from template_title where tabid=");
			sql.append(this.tabid);
			sql.append(" and pageid=");
			sql.append(this.pageid);
			rset=dao.search(sql.toString());
			while(rset.next())
			{
				TTitle title=new TTitle();
				title.setGridno(rset.getInt("gridno"));
				title.setPageid(rset.getInt("pageid"));
				title.setTabid(rset.getInt("tabid"));
				title.setFlag(rset.getInt("flag"));
				title.setFonteffect(rset.getInt("Fonteffect"));
				title.setFontname(rset.getString("Fontname"));
				title.setFontsize(rset.getInt("Fontsize"));
				title.setHz(rset.getString("hz")==null?"":rset.getString("hz"));
				title.setRtop(rset.getInt("rtop"));
				title.setRleft(rset.getInt("rleft"));
				title.setRwidth(rset.getInt("rwidth"));
				title.setRheight(rset.getInt("rheight"));
				title.setExtendattr(Sql_switcher.readMemo(rset,"extendattr"));
				list.add(title);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 求外边框的区域
	 */
	private Rectangle getBorderRect()
	{
		Rectangle rect=new Rectangle(0,0,0,0);
		/*
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;		
		try
		{
			sql.append("select min(rtop) as ltop,min(rleft) as lleft,");
			sql.append("max(rtop+rheight) as dtop,max(RLeft + RWidth) as dleft from template_set ");
			sql.append(" where tabid=");
			sql.append(this.tabid);
			sql.append(" and pageid=");
			sql.append(this.pageid);
			rset=dao.search(sql.toString());
			if(rset.next())
			{
				int x=rset.getInt("lleft");
				int y=rset.getInt("ltop");
				int width=rset.getInt("dleft")-x;
				int height=rset.getInt("dtop")-y;
				rect.setRect(x,y,width,height);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		*/ 
		if(TemplateStaticDataBo.getBorderRect(this.tabid,this.pageid, conn)!=null)
			rect=(Rectangle)TemplateStaticDataBo.getBorderRect(this.tabid,this.pageid, conn); 
		return rect;
	}
	/**
	 * 读所有单元格的信息
	 * @return 列表中存放的是TemplateSetBo对象
	 */
	public ArrayList getAllCell()
	{
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		String temp=null;
		try
		{
			HashMap var_hm=getAllVariableHm();
			Rectangle rect=getBorderRect();
			sql.append("select * from Template_Set where tabid=");
			sql.append(this.tabid);
			sql.append(" and pageid=");
			sql.append(this.pageid);
			sql.append(" order by rtop,rleft");
			rset=dao.search(sql.toString());
			ArrayList op_list=new ArrayList();
			
			
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			Document doc=null;
			Element element=null;
			String xpath="/sub_para/para";
			
			HashMap fieldmap=new HashMap();
			if(this.task_id!=null&&this.task_id.trim().length()>0&&!"0".equals(this.task_id)){
				fieldmap=	getFieldPriv(this.task_id,this.conn);
			}
			
			while(rset.next())
			{
				TemplateSetBo setbo=new TemplateSetBo(this.conn,display_e0122);
				setbo.setPagebo(this);
				setbo.setHz(nullToSpace(rset.getString("hz")));//设置表格的汉字描述
				setbo.setSetname(nullToSpace(rset.getString("setname")));//设置子集的代码
				setbo.setCodeid(nullToSpace(rset.getString("codeid")));//相关的代码类
				setbo.setField_hz(nullToSpace(rset.getString("Field_hz")));//字段的汉子描述
				setbo.setField_name(nullToSpace(rset.getString("Field_name")));//指标的代码
				String flag=rset.getString("Flag")==null?"":rset.getString("Flag");//数据源的标识（文本描述、照片......）
				if(!"V".equalsIgnoreCase(flag)&&!"S".equalsIgnoreCase(flag)&&!"F".equalsIgnoreCase(flag)&&rset.getString("Field_name")!=null&&rset.getString("Field_type")!=null&&rset.getString("subflag")!=null&& "0".equals(rset.getString("subflag"))&&rset.getString("Field_name").trim().length()>0&&rset.getString("Field_type").trim().length()>0){
					if("codesetid".equalsIgnoreCase(rset.getString("Field_name"))|| "codeitemdesc".equalsIgnoreCase(rset.getString("Field_name"))||
							"corcode".equalsIgnoreCase(rset.getString("Field_name"))|| "parentid".equalsIgnoreCase(rset.getString("Field_name"))|| "start_date".equalsIgnoreCase(rset.getString("Field_name"))){
						//这些特殊的字段的是不能从数据字典里获得的
					}else{
						FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name").trim());
						if(item==null){//数据字典里为空 2011 5 26 xieguiquan
							continue;
						}
						else {
                            setbo.setCodeid(item.getCodesetid());//有时候template_set中未更新过来，按照最新的走。20160708
                        }
					}
				}
			
				setbo.setFlag(rset.getString("Flag"));//设置数据源的标识
				setbo.setFormula(nullToSpace(Sql_switcher.readMemo(rset,"Formula")));//设置字段的计算公式
				setbo.setAlign(rset.getInt("Align"));//文字在单元格中的排列方式
				setbo.setDisformat(rset.getInt("DisFormat"));//设置数据的格式  1,2,3,4对数值型为数值精度 后面是对时间的控制	
				
				/**变量*/
				if("V".equalsIgnoreCase(flag))
				{
					RecordVo vo=(RecordVo)var_hm.get(rset.getString("Field_name"));
					if(vo!=null)
					{
						setbo.setDisformat(vo.getInt("flddec"));//如果是临时变量 那么要根据临时变量表里面的小数位数来设置
					}					
				}
				setbo.setChgstate(rset.getInt("ChgState"));//设置字段是变化前还是变化后

				setbo.setFonteffect(rset.getInt("Fonteffect"));//设置字体效果
				setbo.setFontname(rset.getString("FontName"));//设置字体名称
				setbo.setFontsize(rset.getInt("Fontsize"));//设置字体大小
				setbo.setHismode(rset.getInt("HisMode"));//设置历史定位方式
				if(Sql_switcher.searchDbServer()==2)
					setbo.setMode(rset.getInt("Mode_o"));
				else
					setbo.setMode(rset.getInt("Mode"));//多条记录的时候  那几种选择 (最近..最初..)
				setbo.setNsort(rset.getInt("nSort"));//相同指示顺序号
				setbo.setGridno(rset.getInt("gridno"));//单元格号
				setbo.setRcount(rset.getInt("Rcount"));//记录数 和HisMode 配合试用（标识最近（Rcount条））
				setbo.setRheight(rset.getInt("RHeight"));//设置单元格高度
				setbo.setRleft(rset.getInt("RLeft"));//单元格左边的坐标值
				setbo.setRwidth(rset.getInt("RWidth"));//单元格的宽度
				setbo.setRtop(rset.getInt("RTop"));//单元格上边坐标值
				setbo.setL(rset.getInt("L"));/**LBRT 代表着表格左下右上是否有线**/
				setbo.setB(rset.getInt("B"));				
				setbo.setR(rset.getInt("R"));
				setbo.setT(rset.getInt("T"));
				temp=rset.getString("subflag");//子表控制符 0：字段 1：子集
				if(temp==null|| "".equals(temp)|| "0".equals(temp))
					setbo.setSubflag(false);
				else
					setbo.setSubflag(true);
				//节点必填项
				if(this.task_id!=null&&this.task_id.trim().length()>0&&!"0".equals(this.task_id)){//这里应该是判断在流程结点中定义的必填项
					 
					if(fieldmap!=null&&fieldmap.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase())!=null&&"3".equals((String)fieldmap.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase()))){
						setbo.setYneed(true);
						
					}else{
						if(rset.getInt("yneed")==0)
							setbo.setYneed(false);
						else
							setbo.setYneed(true);
					
					}
				}else{

				if(rset.getInt("yneed")==0)
					setbo.setYneed(false);
				else
					setbo.setYneed(true);
				}
				String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
				//linbz 28653
				if("F".equalsIgnoreCase(rset.getString("Flag"))){//附件模拟子集
					setbo.setSubflag(true);
					setbo.setField_hz("A");
					setbo.setAttachmentXml(sub_domain);//保存附件设置，用于后面解析按分类查询附件xml中用于后面解析按分类查询附件
					sub_domain = this.getAcctch_domain(sub_domain,setbo);
				}
				setbo.setXml_param(sub_domain);
				//获得sub_domain_id
				String sub_domain_id="";
				setbo.setSub_domain_id(sub_domain_id);
				//为了兼容新人事异动提交数据 支持多个变化后子集 wangrd 20160816
				if(sub_domain!=null&&sub_domain.trim().length()>0/*&&"1".equals(""+rset.getInt("ChgState"))*/){
					try{
							doc=PubFunc.generateDom(sub_domain);;
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element)childlist.get(0);
								if(element.getAttributeValue("id")!=null){
									sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
										if ("1".equals(""+rset.getInt("ChgState")) || setbo.isSubflag() )
											setbo.setSub_domain_id(sub_domain_id);	
									}
							}
							}
					}catch(Exception e){
						
					}
				}
				setbo.setField_type(nullToSpace(rset.getString("Field_type")));
				if(!setbo.isSubflag()&&"1".equals(""+rset.getInt("ChgState"))&&flag!=null&&!"H".equals(flag.toUpperCase())){
				if(Sql_switcher.searchDbServer()==2){
				if(("2".equals(""+rset.getInt("HisMode")) /*&&("1".equals(""+rset.getInt("Mode_o"))||"3".equals(""+rset.getInt("Mode_o")))  */  )|| //2014-04-01 dengcan
						"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){//(序号定位&&(最近||最初)) || 条件定位||条件序号
					setbo.setField_type("M");
					if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
						if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
						this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
					}else{
						this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
					}
					}
					
				}
				}else{
				//	if(("2".equals(""+rset.getInt("HisMode"))&&("1".equals(""+rset.getInt("Mode"))||"3".equals(""+rset.getInt("Mode"))))||
					if("2".equals(""+rset.getInt("HisMode"))||"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){ //2014-04-01 dengcan
						setbo.setField_type("M");
						if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
							if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
							this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
						}else{
							this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
						}
						}
					}
				}
				}
				setbo.setRect(rect);//设置表格的区域
				if(rset.getString("nhide")!=null)
					setbo.setNhide(rset.getInt("nhide"));
				else
					setbo.setNhide(0);//打印还是隐藏 0：打印 1：隐藏
				//子集特殊处理一下  给field_hz重新赋值 20170712 hej
				if (setbo.isSubflag()) {
				    FieldSet fieldset=DataDictionary.getFieldSetVo(setbo.getSetname());
                    if(fieldset!=null){
                    	setbo.setField_hz(fieldset.getFieldsetdesc());
                    }
				}
				list.add(setbo);
				op_list.add(setbo.clone());
			}
			ArrayList new_setbo=new ArrayList();
			int b=0;
			int l=0;
			int r=0;
			int t=0;
			for(int i=0;i<op_list.size();i++)
			{
				TemplateSetBo cur_setbo =(TemplateSetBo)op_list.get(i);  
				
				b=getRlineForList(list,"b",cur_setbo.getB(),cur_setbo);
				l=getRlineForList(list,"l",cur_setbo.getL(),cur_setbo);
				r=getRlineForList(list,"r",cur_setbo.getR(),cur_setbo);
				t=getRlineForList(list,"t",cur_setbo.getT(),cur_setbo);
				cur_setbo.setB(b);					
				cur_setbo.setL(l);
				cur_setbo.setR(r);
				cur_setbo.setT(t);
				new_setbo.add(cur_setbo);
			}
			return new_setbo;			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 如果为null返回“”字符串
	 * @param value
	 * @return
	 */
	private String nullToSpace(String value)
	{
		if(value==null)
			return "";
		else 
			return value;
	}
	/**
	 * 获取单元格信息，并自动排序 zhaoxg add 2015-3-28
	 * @param insertDataCtrl 
	 * @param obj_id 
	 * @param dbpre 
	 * @return
	 */
	public int getCell(ArrayList pagelist,RowSet rs,UserView uv,float[] wh,int tp,ArrayList pgidlist,int ins_id, String insertDataCtrl, String dbpre, String obj_id)
	{
		ArrayList pglist=new ArrayList();//长度代表每页显示多少内容
		RowSet rset=null;
		String temp=null;
		int temp_top=0;//记录固定高度、行数子集实际高度和设计高度差值
		int top_margin = 0; // 当前页表格起始位置像素
		int bottom_margin=0; //当前页表格结束位置像素
		
		int top = 0;
		HashMap var_hm=getAllVariableHm();
		try
		{
			for(int s=0;s<pgidlist.size();s++){
				top=0;
				HashMap pgmap = (HashMap) pgidlist.get(s);
				this.pageid = Integer.parseInt((String)pgmap.get("pageid"));
				Rectangle rect=getBorderRect();
				rset=(RowSet) pgmap.get("context");
				RowSet roset=(RowSet) pgmap.get("title");
				
				HashMap positionMap = this.getPosition(pgmap, wh);
				top_margin = Integer.parseInt((String) positionMap.get("top")) ;//顶部位置只有翻页后才能生效，不翻页以具体画线位置为准
				bottom_margin = Integer.parseInt((String) positionMap.get("bottom")) ;//底部位置当子集超过实际画线即生效，否则以实际画线为准
				rset.beforeFirst();
				roset.beforeFirst();
				ArrayList titleList = new ArrayList(); //每页标题数据集
				while(roset.next())
				{
					TTitle title=new TTitle();
					title.setGridno(roset.getInt("gridno"));
					title.setPageid(roset.getInt("pageid"));
					title.setTabid(roset.getInt("tabid"));
					title.setFlag(roset.getInt("flag"));
					title.setFonteffect(roset.getInt("Fonteffect"));
					title.setFontname(roset.getString("Fontname"));
					title.setFontsize(roset.getInt("Fontsize"));
					title.setHz(roset.getString("hz")==null?"":roset.getString("hz"));
					title.setRtop(roset.getInt("rtop"));
					title.setRleft(roset.getInt("rleft"));
					title.setRwidth(roset.getInt("rwidth"));
					title.setRheight(roset.getInt("rheight"));
					title.setExtendattr(Sql_switcher.readMemo(roset,"extendattr"));
					titleList.add(title);
				}

				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				Document doc=null;
				Element element=null;
				String xpath="/sub_para/para";
				HashMap fieldmap=new HashMap();
				HashMap fieldmaps=new HashMap();
				if(this.task_id!=null&&this.task_id.trim().length()>0&&!"0".equals(this.task_id)){
					fieldmaps=	getFieldPriv(this.task_id,this.conn);
					fieldmap = getFieldPrivByNode(this.task_id);
				}
				ArrayList list=new ArrayList();//子集内容信息
				ArrayList celllist=new ArrayList();//每页显示的内容信息
				boolean isHaveTop = false;//是否给top赋值，即超过一页多少
				while(rset.next())
				{
					list=new ArrayList();//子集内容信息
					celllist=new ArrayList();//每页显示的内容信息
					TemplateSetBo setbo=new TemplateSetBo(this.conn,display_e0122);
					setbo.setPagebo(new TemplatePageBo(this.conn,this.tabid,rset.getInt("PageID")));
					setbo.setHz(rset.getString("hz"));//设置表格的汉字描述
					setbo.setSetname(rset.getString("setname"));//设置子集的代码
					setbo.setCodeid(rset.getString("codeid"));//相关的代码类
					setbo.setField_hz(rset.getString("Field_hz"));//字段的汉子描述
					setbo.setField_name(rset.getString("Field_name"));//指标的代码
					String flag=rset.getString("Flag")==null?"":rset.getString("Flag");//数据源的标识（文本描述、照片......）
					if(!"V".equalsIgnoreCase(flag)&&!"S".equalsIgnoreCase(flag)&&!"F".equalsIgnoreCase(flag)&&rset.getString("Field_name")!=null&&rset.getString("Field_type")!=null
							&&rset.getString("subflag")!=null&& "0".equals(rset.getString("subflag"))&&rset.getString("Field_name").trim().length()>0
							&&rset.getString("Field_type").trim().length()>0){
						if("codesetid".equalsIgnoreCase(rset.getString("Field_name"))|| "codeitemdesc".equalsIgnoreCase(rset.getString("Field_name"))||
								"corcode".equalsIgnoreCase(rset.getString("Field_name"))|| "parentid".equalsIgnoreCase(rset.getString("Field_name"))||
								"start_date".equalsIgnoreCase(rset.getString("Field_name"))){
							//这些特殊的字段的是不能从数据字典里获得的
						}else{
							FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name").trim());
							if(item==null){//数据字典里为空 2011 5 26 xieguiquan
								continue;
							}
						}
					}
				
					setbo.setFlag(rset.getString("Flag"));//设置数据源的标识
					setbo.setFormula(Sql_switcher.readMemo(rset,"Formula"));//设置字段的计算公式
					setbo.setAlign(rset.getInt("Align"));//文字在单元格中的排列方式
					setbo.setDisformat(rset.getInt("DisFormat"));//设置数据的格式  1,2,3,4对数值型为数值精度 后面是对时间的控制	
					
					/**变量*/
					if("V".equalsIgnoreCase(flag))
					{
						RecordVo vo=(RecordVo)var_hm.get(rset.getString("Field_name"));
						if(vo!=null)
						{
							setbo.setDisformat(vo.getInt("flddec"));//如果是临时变量 那么要根据临时变量表里面的小数位数来设置
						}					
					}
					setbo.setChgstate(rset.getInt("ChgState"));//设置字段是变化前还是变化后
					setbo.setFonteffect(rset.getInt("Fonteffect"));//设置字体效果
					setbo.setFontname(rset.getString("FontName"));//设置字体名称
					setbo.setFontsize(rset.getInt("Fontsize"));//设置字体大小
					setbo.setHismode(rset.getInt("HisMode"));//设置历史定位方式
					if(Sql_switcher.searchDbServer()==2)
						setbo.setMode(rset.getInt("Mode_o"));
					else
						setbo.setMode(rset.getInt("Mode"));//多条记录的时候  那几种选择 (最近..最初..)
					setbo.setNsort(rset.getInt("nSort"));//相同指示顺序号
					setbo.setGridno(rset.getInt("gridno"));//单元格号
					setbo.setRcount(rset.getInt("Rcount"));//记录数 和HisMode 配合试用（标识最近（Rcount条））
					setbo.setRheight(rset.getInt("RHeight"));//设置单元格高度
					setbo.setRleft(rset.getInt("RLeft"));//单元格左边的坐标值
					setbo.setRwidth(rset.getInt("RWidth"));//单元格的宽度
					setbo.setRtop(rset.getInt("RTop")+top+temp_top);//单元格上边坐标值
					setbo.setL(rset.getInt("L"));/**LBRT 代表着表格左下右上是否有线**/
					setbo.setB(rset.getInt("B"));
					setbo.setR(rset.getInt("R"));
					setbo.setT(rset.getInt("T"));
					temp=rset.getString("subflag");//子表控制符 0：字段 1：子集
					if(temp==null|| "".equals(temp)|| "0".equals(temp))
						setbo.setSubflag(false);
					else
						setbo.setSubflag(true);
					//节点必填项
					if(this.task_id!=null&&this.task_id.trim().length()>0&&!"0".equals(this.task_id)){//这里应该是判断在流程结点中定义的必填项
						if(fieldmaps!=null&&fieldmaps.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase())!=null
								&&"3".equals((String)fieldmaps.get((setbo.getField_name()+"_"+setbo.getChgstate()).toLowerCase()))){
							setbo.setYneed(true);
						}else{
							if(rset.getInt("yneed")==0)
								setbo.setYneed(false);
							else
								setbo.setYneed(true);
						}
					}else{
						if(rset.getInt("yneed")==0)
							setbo.setYneed(false);
						else
							setbo.setYneed(true);
					}
					String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
					if("F".equalsIgnoreCase(rset.getString("Flag"))){//附件模拟子集
						setbo.setSubflag(true);
						setbo.setField_hz("A");
						setbo.setAttachmentXml(sub_domain);//保存附件设置，用于后面解析按分类查询附件xml中用于后面解析按分类查询附件
						sub_domain = this.getAcctch_domain(sub_domain,setbo);
					}
					setbo.setXml_param(sub_domain);
					//获得sub_domain_id
					String sub_domain_id="";
					setbo.setSub_domain_id(sub_domain_id);
					boolean autoextend = false;//自动增加子集高度以适应内容 默认false
					if(sub_domain!=null&&sub_domain.trim().length()>0){
						try{
							doc=PubFunc.generateDom(sub_domain);;
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element)childlist.get(0);
								if(element.getAttributeValue("id")!=null&&("1".equals(""+rset.getInt("ChgState"))||setbo.isSubflag())){
									sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
									setbo.setSub_domain_id(sub_domain_id);	
								}
								if("true".equals(element.getAttributeValue("autoextend"))){
									autoextend = true;
								}
								doc=PubFunc.generateDom(sub_domain);;
								if(childlist!=null&&childlist.size()>0)
								{
									element=(Element)childlist.get(0);
									if(element.getAttributeValue("id")!=null&&("1".equals(""+rset.getInt("ChgState"))||setbo.isSubflag())){
										sub_domain_id=(String)element.getAttributeValue("id");
										if(sub_domain_id!=null&&sub_domain_id.trim().length()>0)
										setbo.setSub_domain_id(sub_domain_id);	
									}
									if("true".equals(element.getAttributeValue("autoextend"))){
										autoextend = true;
									}
								}
							}
						}catch(Exception e){
							
						}
					}
					setbo.setField_type(rset.getString("Field_type"));
					if(!setbo.isSubflag()&&"1".equals(""+rset.getInt("ChgState"))&&flag!=null&&!"H".equals(flag.toUpperCase())){
						if(Sql_switcher.searchDbServer()==2){
							if(("2".equals(""+rset.getInt("HisMode")))||"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){//(序号定位&&(最近||最初)) || 条件定位||条件序号
								setbo.setField_type("M");
								if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
									if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
									}else{
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
									}
								}
							}
						}else{
							if("2".equals(""+rset.getInt("HisMode"))||"3".equals(""+rset.getInt("HisMode"))||"4".equals(""+rset.getInt("HisMode"))){
								setbo.setField_type("M");
								if(setbo.getField_name()!=null&&setbo.getField_name().length()>0){
									if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+setbo.getSub_domain_id()+"_"+rset.getInt("ChgState"));
									}else{
										this.field_name_map.put(setbo.getField_name().toLowerCase()+"_"+rset.getInt("ChgState"), setbo.getField_name()+"_"+rset.getInt("ChgState"));
									}
								}
							}
						}
					}
					setbo.setRect(rect);//设置表格的区域
					if(rset.getString("nhide")!=null)
						setbo.setNhide(rset.getInt("nhide"));
					else
						setbo.setNhide(0);//打印还是隐藏 0：打印 1：隐藏
					ArrayList rememberlist = new ArrayList();
					if(!setbo.isSubflag())//如果是主集
					{
						if(setbo.getRheight()+setbo.getRtop()>bottom_margin&&(setbo.getRheight()<setbo.getRwidth()||"M".equals(setbo.getField_type()))){//宽大于高（这样不会是子集两侧的东西了）或者大文本超过一页直接换到下一页去，不截断显示了
							pagelist.add(pglist);
							this.changeBefforeHight(pglist, 0, 0,setbo.getRtop(),rememberlist);
							celllist = new ArrayList();
							for(int t=0;t<titleList.size();t++){
								TTitle title=(TTitle) titleList.get(t);
								celllist.add(title);
								celllist.add(list);
								celllist.add("title");
								pglist.add(celllist);
								celllist = new ArrayList();
							}
							celllist = new ArrayList();
							pglist = new ArrayList();
							
							//top = top_margin - rset.getInt("RTop");//换页之后取得实际距离与库中数据的差值，后面的数据只有加上这个差值就可以获得实际位置
							top = top_margin - rset.getInt("RTop")-temp_top;//换页之后取得实际距离与库中数据的差值，后面的数据只有加上这个差值就可以获得实际位置
							setbo.setRtop(top_margin);
							isHaveTop = false;
							
							celllist.add(setbo);
							celllist.add(list);
							pglist.add(celllist);
							celllist = new ArrayList();
							
							isHaveTop = false;
							list = new ArrayList();
	        				for(int t=0;t<rememberlist.size();t++){
	        					pglist.add(rememberlist.get(t));
	        				}
							this.changeAfterHight(pglist, 0,0,top_margin);
						}else{
							celllist.add(setbo);
							celllist.add(list);				
							pglist.add(celllist);
							celllist = new ArrayList();
						}
					}else{
						int rtop = setbo.getRtop();
						int fontsize;
						fontsize=setbo.getFontsize();
						if(fontsize>=8)
							fontsize=fontsize+1;
						if(fontsize<=6)
							fontsize=fontsize-1;
						String fonteffect=String.valueOf(setbo.getFonteffect());
						PdfPCell cell=null;
						String xmlparam=setbo.getXml_param();
						if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
							sub_domain_id="_"+setbo.getSub_domain_id();
						}
						String field_name="t_"+setbo.getSetname()+sub_domain_id+"_"+setbo.getChgstate();
						TSubSetDomain subdom=new TSubSetDomain(xmlparam);
				        subdom.reSetWidth(setbo.getRwidth());
				        float[] fcolumns=subdom.getColumns();
				        PdfPTable table = new PdfPTable(fcolumns);	
				        table.setTotalWidth(setbo.getRwidth());
				        table.setLockedWidth(true);			           
				        
				        boolean flag1 = false;
		                if(rs.isBeforeFirst()){
		                    if(rs.next()){
		                    	flag1 = true;    
		                    }
		                }  
		                String content="";
		                ArrayList RecordList=new ArrayList();
		                if(setbo.getSetname()!=null&&setbo.getSetname().indexOf("attachment")>-1){//附件模拟子集
		                	RecordList = this.getAttachRecordlist(ins_id,setbo.getSetname(),dbpre,obj_id,uv);
		                	//if(RecordList.size()==0){
		                		//sub_domain = this.getAcctch_domain(sub_domain,setbo,true);
		                		//setbo.setXml_param(sub_domain);
		                	//}
		                }else{
		                	content=Sql_switcher.readMemo(rs, field_name.toLowerCase());
		                	RecordList=subdom.getRecordPdfList(content);
		                }
		                String value="";
		                //自动缩放每行高度 及字体大小
		                int cell_height=20;
		                int cellHight = 0;
		                int titleHeight = 0;//表头的高度，存最高的列，用来判断指定行数的子集列高  zhaoxg add 2015-8-26
		                HashMap valueMap = new HashMap();//用来保存拆分开的一行数据的后半部分
				        if(subdom.isBcolhead())//表头
				        {
							int height = 0;
							int colheadheight = subdom.getColheadheight();
					        for(int i=0;i<subdom.getFieldfmtlist().size();i++)
					        {
					        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(i);    	
								Paragraph para=new Paragraph(fieldformat.getTitle(),FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
								/**
								 *必须按下面方法，增加段，要不然排列方式不生效
								 *通过"\n"进行换行处理 
								 */
								cell=new PdfPCell(para); 
								cell.setBorder(0);
		                        setSubSetHAlign(1,cell); //居中  
		                        setSubSetVAlign(1,cell); //居中 
								int lines = getLines(fieldformat.getTitle(),fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),fontsize);
								if(lines>height){
									height = lines;
								}
								if(subdom.getFieldfmtlist().size()-1==i){
									rtop = rtop+cell_height*(height);
									cellHight = cellHight+cell_height*(height);
									if(isHaveTop){
										top = top+cell_height*(height);												
									}
								}
								if(cell_height*lines>titleHeight){
									titleHeight = cell_height*lines;
								}
								if(colheadheight==0){
									cell.setFixedHeight(titleHeight);	
								}else{
									cell.setFixedHeight(titleHeight);	
									titleHeight = colheadheight;
								}
						        cell.setNoWrap(false);//
						        if(!subdom.isBvl())
						        	cell.setBorderWidthRight(1f);//表头没竖线有右边框
						        cell.setBorderWidthBottom(1f);//表头默认有下边框
						        table.addCell(cell);
					        }
							if(rtop+titleHeight>bottom_margin&&autoextend){
								pagelist.add(pglist);
								this.changeBefforeHight(pglist, 0, 0,setbo.getRtop(),rememberlist);
								celllist = new ArrayList();
								for(int t=0;t<titleList.size();t++){
									TTitle title=(TTitle) titleList.get(t);
									celllist.add(title);
									celllist.add(list);
									celllist.add("title");
									pglist.add(celllist);
									celllist = new ArrayList();
								}
								celllist = new ArrayList();
								pglist = new ArrayList();
								
								rtop = top_margin;
								top = top_margin-(rset.getInt("RTop"));//表头位置换页，相当于此表以下所有内容向上移动top_margin-(rset.getInt("RTop"))
								cellHight = 0;
								isHaveTop = true;
								list = new ArrayList();
		
								setbo = (TemplateSetBo) setbo.clone();
								setbo.setRtop(rtop);
							}
					        rtop+=titleHeight;//表头高度加上去
				        }
				        
				        /**内容区域*/
				        if(setbo.getNhide()==0)
				        {
				        	String state="2";
				        	String setname=setbo.getSetname();
							if(setbo.getFlag()!=null&& "A".equalsIgnoreCase(setbo.getFlag())|| "B".equalsIgnoreCase(setbo.getFlag())|| "K".equalsIgnoreCase(setbo.getFlag()))
							{
								if ("1".equals(insertDataCtrl)){
				                    state="2";
				                }
				                else {
			                		String astate=uv.analyseTablePriv(setname.toUpperCase());
			                		if("0".equals(astate))
			                			astate=uv.analyseTablePriv(setname.toUpperCase(),0);//员工自助权限
			                		if("0".equalsIgnoreCase(astate))
			                			state="0";
			                		
				                }
				                if(fieldmap!=null&&fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
		                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
		                			state = (String)fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase());
			                    	//}
								}
							}
				        	if(!"0".equals(state))
				        	{		
				        			int len = RecordList.size();
				        			int RecordSise = RecordList.size();
				        			int datarowcount = subdom.getDatarowcount();
				        			if(datarowcount>0&&!autoextend&&RecordSise>datarowcount){
				        				len = datarowcount;
				        			}else if(datarowcount>0&&!autoextend){
				        				len = datarowcount;
				        				for(int t=0;t<datarowcount-RecordSise;t++){
				        					RecordList.add(null);
				        				}
				        			}
				        			boolean isChangePage = false;//是否换页 zhaoxg add
							        for(int i=0;i<len;i++)
							        {
							        	if(subdom.getDatarowcount()>0&&!autoextend){
							        		int height = setbo.getRheight()-titleHeight;
							        		float h = Float.parseFloat(PubFunc.divide(height+"",subdom.getDatarowcount()+"",2));
									        HashMap map=(HashMap)RecordList.get(i);
									        for(int j=0;j<subdom.getFieldfmtlist().size();j++)
									        {
									        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(j);
									        	String name=fieldformat.getName().toLowerCase();
									        	String slop =fieldformat.getSlop();
									        	if(map==null||map.get(name)==null)
									        		value="";
									        	else
									        		value=(String)map.get(name);
												FieldItem item=DataDictionary.getFieldItem(name);
												if(item!=null&&!("F".equalsIgnoreCase(setbo.getFlag())))
												{
													if("A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid())))
														value=AdminCode.getCodeName(item.getCodesetid(), value);
													else if("D".equalsIgnoreCase(item.getItemtype())){
														value=value.replace(".", "-");
														if(slop!=null&&!"".equals(slop)){
															value =setbo.formatDateFiledsetValue(value, fieldformat.getPre(), Integer.parseInt(slop));
														}
													}
													String a_state="2";
													String astate = "0";
													if ("1".equals(insertDataCtrl)){
														astate="2";
									                }
									                else {
								                		astate=uv.analyseFieldPriv(item.getItemid());
								                		if("0".equals(astate))
								                			astate=uv.analyseFieldPriv(item.getItemid(),0);//员工自助权限
								                		if("0".equalsIgnoreCase(astate))
								                			a_state="0";
									                }
									                if(fieldmap!=null&&fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
							                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
							                			a_state = (String)fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase());
							                			//}
								                	}
													if("0".equals(a_state))
														value="";																						
												}
								                if (fontsize<10) cell_height=16;
								                if (fontsize<11) cell_height=18 ;
								                else if (fontsize>16) cell_height =25;
								                else if (fontsize>18) cell_height =30;
								                else if (fontsize>=20) cell_height =40;

								                int font_size=fontsize;
								                float rheight=h;
								                int sumLines = getLines(value,fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),font_size);
								                if(rheight/cell_height<sumLines)
								                {
								                    while(true)
								                    {
								                        cell_height=cell_height-1;
								                        font_size=font_size-1;
								                        sumLines = getLines(value,fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),font_size);
								                        if(rheight/cell_height>=sumLines)
								                        {
								                            break;
								                        }
								                        if (font_size <3) {
								                        	break;
								                        }
								                    }
								                }
										        fontsize = font_size;									
									        	Paragraph para=new Paragraph(value,FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
									        	fontsize=setbo.getFontsize();//用完重置，让后面的格重新算
												cell=new PdfPCell(para);  
												cell.setBorder(0);
												
												setSubSetHAlign(fieldformat.getAlign(),cell);	
												setSubSetVAlign(fieldformat.getValign(),cell);
												if(i==subdom.getDatarowcount()-1){
													if(subdom.isBhl())
														cell.setBorderWidthBottom(1f);//liuyz设置成0会造成有些页面上表格缺少底部边线。
													cell.setFixedHeight(h);
												}else{
													if(subdom.isBhl())
														cell.setBorderWidthBottom(1f);
													cell.setFixedHeight(h);//cell_height*lines
												}
										        cell.setNoWrap(false);
										        table.addCell(cell);
									        }
								        }else{
											rtop = rtop+cell_height;
											cellHight = cellHight+cell_height;//加一行高度判断
											int lows = 0;//本页剩余的行数
											if(rtop>bottom_margin&&autoextend){
												isChangePage = true;
												list.add(table);
												setbo.setRheight(cellHight-cell_height);//换页之前给子集一个高度，即留在上一页的高度
												celllist.add(setbo);
												celllist.add(list);
												pglist.add(celllist);
												this.changeBefforeHight(pglist, cellHight-cell_height, rset.getInt("RHeight"),setbo.getRtop(),rememberlist);
												pagelist.add(pglist);
												celllist = new ArrayList();
												for(int t=0;t<titleList.size();t++){
													TTitle title=(TTitle) titleList.get(t);
													celllist.add(title);
													celllist.add(list);
													celllist.add("title");
													pglist.add(celllist);
													celllist = new ArrayList();
												}
												celllist = new ArrayList();
												pglist = new ArrayList();
												
												rtop = top_margin;
												top = top_margin-(rset.getInt("RTop")+rset.getInt("RHeight"));
//												if(setbo.getRheight()>cellHight){
//													top = top+setbo.getRheight()-cellHight;	
//												}
												cellHight = 0;
												isHaveTop = true;
												list = new ArrayList();
												setbo = (TemplateSetBo) setbo.clone();
												setbo.setRtop(rtop);
												cellHight = cellHight + cell_height;//加一行高度，匹配上面
										        table = new PdfPTable(fcolumns);	
										        table.setTotalWidth(setbo.getRwidth());
										        table.setLockedWidth(true);
										        int height = 0;
										        for(int t=0;t<subdom.getFieldfmtlist().size();t++)
										        {
										        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(t); 
													Paragraph para=new Paragraph(fieldformat.getTitle(),FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
													/**
													 *必须按下面方法，增加段，要不然排列方式不生效
													 *通过"\n"进行换行处理 
													 */
													cell=new PdfPCell(para); 
													cell.setBorder(0);
							                        setSubSetHAlign(1,cell); //居中  
							                        setSubSetVAlign(1,cell); //居中 
													int lines = getLines(fieldformat.getTitle(),fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),fontsize);
													if(lines>height){
														height = lines;
													}
													if(subdom.getFieldfmtlist().size()-1==t){
														rtop = rtop+cell_height*(height);
														cellHight = cellHight+cell_height*(height);
														if(isHaveTop){
															top = top+cell_height*(height);												
														}
													}
											        cell.setFixedHeight(cell_height*lines);		
											        cell.setNoWrap(false);//
											        if(!subdom.isBvl())
											        	cell.setBorderWidthRight(1f);//表头没竖线有右边框
											        cell.setBorderWidthBottom(1f);//表头默认有下边框
											        table.addCell(cell);
										        }

//										        setbo.setRheight((RecordList.size()-i)*cell_height+height*cell_height);
											}else{
												lows = (int) ((bottom_margin-rtop)/cell_height);
												if(lows==0){
													lows=1;
												}
											}
											if(isHaveTop){
												top = top+cell_height;
											}
								        	HashMap map=(HashMap)RecordList.get(i);
								        	int height = 0;
									        for(int j=0;j<subdom.getFieldfmtlist().size();j++)
									        {
									        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(j);
									        	String name=fieldformat.getName().toLowerCase();
									        	String slop =fieldformat.getSlop();
									        	if(map.get(name)==null)
									        		value="";
									        	else
									        		value=(String)map.get(name);
												FieldItem item=DataDictionary.getFieldItem(name);
												if(item!=null&&!("F".equalsIgnoreCase(setbo.getFlag())))
												{
													if("A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid())))
														value=AdminCode.getCodeName(item.getCodesetid(), value);
													else if("D".equalsIgnoreCase(item.getItemtype())){
														value=value.replace(".", "-");
														if(slop!=null&&!"".equals(slop)){
															value =setbo.formatDateFiledsetValue(value, fieldformat.getPre(), Integer.parseInt(slop));
														}
													}
	
													String a_state="2";
													String astate = "0";
													if ("1".equals(insertDataCtrl)){
														astate="2";
									                }
									                else {
								                		astate=uv.analyseFieldPriv(item.getItemid());
								                		if("0".equals(astate))
								                			astate=uv.analyseFieldPriv(item.getItemid(),0);//员工自助权限
								                		if("0".equalsIgnoreCase(astate))
								                			a_state="0";
									                }
									                if(fieldmap!=null&&fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
							                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
							                			a_state = (String)fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase());
							                			//}
								                	}
													if("0".equals(a_state))
														value="";																								
												}
												int lines = getLines(value,fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),fontsize);
												if(lows<lines&&lows>0){
													if("D".equalsIgnoreCase(fieldformat.getFielditem().getItemtype())){
														value=value.replace(".", "-");
														if(fieldformat.getSlop()!=null&&!"".equals(fieldformat.getSlop())){
															value =setbo.formatDateFiledsetValue(value, fieldformat.getPre(), Integer.parseInt(slop));
														}
													}
													ArrayList lowsList = this.getNextUpChar(value, fieldformat.getWidth(), setbo, fontsize, lows,lines);
													value = (String) lowsList.get(0);
										        	Paragraph para=new Paragraph(value,FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
													cell=new PdfPCell(para);
													cell.setBorder(0);
													if(subdom.isBhl())
														cell.setBorderWidthBottom(1f);
													setSubSetHAlign(fieldformat.getAlign(),cell);	
													setSubSetVAlign(fieldformat.getValign(),cell);	
													if(lows>height){
														height = lows;
													}
													if(subdom.getFieldfmtlist().size()-1==j){
														rtop = rtop+cell_height*(height-1);
														cellHight = cellHight+cell_height*(height-1);
														if(isHaveTop){
															top = top+cell_height*(height-1);												
														}
													}
											        cell.setFixedHeight(cell_height*lows);	
											        cell.setNoWrap(false);//
											        table.addCell(cell);
											        if(lowsList.size()>1)
											        	valueMap.put(j+"", (String) lowsList.get(1));
												}else{
										        	Paragraph para=new Paragraph(value,FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
													cell=new PdfPCell(para);
													cell.setBorder(0);
			
													if(subdom.isBhl())
														cell.setBorderWidthBottom(1f);
													setSubSetHAlign(fieldformat.getAlign(),cell);	
													setSubSetVAlign(fieldformat.getValign(),cell);	
													if(lines>height){
														height = lines;
													}
													if(subdom.getFieldfmtlist().size()-1==j){//前面加过一行的高度用来判断，所以此处少加一行
														rtop = rtop+cell_height*(height-1);
														cellHight = cellHight+cell_height*(height-1);
														if(isHaveTop){
															top = top+cell_height*(height-1);
														}
													}
											        cell.setFixedHeight(cell_height*lines);							     
											        cell.setNoWrap(false);//
											        table.addCell(cell);
												}
									        }
											if(valueMap.size()>0){//一行拆两行，把剩下的后半行放到下一页前面
												isChangePage = true;
												list.add(table);
												setbo.setRheight(cellHight);//换页之前给子集一个高度，即留在上一页的高度
												celllist.add(setbo);
												celllist.add(list);
												pglist.add(celllist);
												this.changeBefforeHight(pglist, cellHight, rset.getInt("RHeight"),setbo.getRtop(),rememberlist);
												pagelist.add(pglist);
												celllist = new ArrayList();
												for(int t=0;t<titleList.size();t++){
													TTitle title=(TTitle) titleList.get(t);
													celllist.add(title);
													celllist.add(list);
													celllist.add("title");
													pglist.add(celllist);
													celllist = new ArrayList();
												}
												celllist = new ArrayList();
												pglist = new ArrayList();	

												rtop = top_margin;
												top = top_margin-(rset.getInt("RTop")+rset.getInt("RHeight"));
//												if(setbo.getRheight()>cellHight){
//													top = top+setbo.getRheight()-cellHight;	
//												}
												cellHight = 0;
												isHaveTop = true;
												list = new ArrayList();
												height=0;
												setbo = (TemplateSetBo) setbo.clone();
												setbo.setRtop(rtop);
												rtop = rtop+cell_height;//换页了，第一行没算进来，此处加上
										        table = new PdfPTable(fcolumns);	
										        table.setTotalWidth(setbo.getRwidth());
										        table.setLockedWidth(true);
										        for(int t=0;t<subdom.getFieldfmtlist().size();t++)
										        {
										        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(t);    	
													Paragraph para=new Paragraph(fieldformat.getTitle(),FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
													/**
													 *必须按下面方法，增加段，要不然排列方式不生效
													 *通过"\n"进行换行处理 
													 */
													cell=new PdfPCell(para);
													cell.setBorder(0);
													
							                        setSubSetHAlign(1,cell); //居中  
							                        setSubSetVAlign(1,cell); //居中 
													int lines = getLines(fieldformat.getTitle(),fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),fontsize);
													if(lines>height){
														height = lines;
													}
													if(subdom.getFieldfmtlist().size()-1==t){
														rtop = rtop+cell_height*(height);
														cellHight = cellHight+cell_height*(height);
														if(isHaveTop){
															top = top+cell_height*(height);												
														}
													}
											        cell.setFixedHeight(cell_height*lines);							     
											        cell.setNoWrap(false);//
											        if(!subdom.isBvl())
											        	cell.setBorderWidthRight(1f);//表头没竖线有右边框
											        cell.setBorderWidthBottom(1f);//表头默认有下边框
											        table.addCell(cell);
										        }
										        height=0;
										        for(int j=0;j<subdom.getFieldfmtlist().size();j++)
										        {
										        	TFieldFormat fieldformat=(TFieldFormat)subdom.getFieldfmtlist().get(j);
										        	String name=fieldformat.getName().toLowerCase();
										        	String slop =fieldformat.getSlop();
										        	value = (String) valueMap.get(j+"");
										        	value = value==null?"":value;
													FieldItem item=DataDictionary.getFieldItem(name);
													if(item!=null&&!("F".equalsIgnoreCase(setbo.getFlag())))//liuyz F:表示是附件,附件不需要判断员工权限
													{
														if("A".equalsIgnoreCase(item.getItemtype())&&(!"0".equalsIgnoreCase(item.getCodesetid())))
															value=AdminCode.getCodeName(item.getCodesetid(), value);
														else if("D".equalsIgnoreCase(item.getItemtype())){
															//value=value.replace(".", "-");
															//if(slop!=null&&!slop.equals("")){
															//	value =setbo.formatDateFiledsetValue(value, fieldformat.getPre(), Integer.parseInt(slop));
															//}
														}											
														String a_state="2";
														String astate = "0";
														if ("1".equals(insertDataCtrl)){
															astate="2";
										                }
										                else {
									                		astate=uv.analyseFieldPriv(item.getItemid());
									                		if("0".equals(astate))
									                			astate=uv.analyseFieldPriv(item.getItemid(),0);//员工自助权限
									                		if("0".equalsIgnoreCase(astate))
									                			a_state="0";
										                }
										                if(fieldmap!=null&&fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase())!=null){
								                			//if (!"0".equals(this.task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
								                			a_state = (String)fieldmap.get((setname.toLowerCase()+"_"+setbo.getChgstate()).toLowerCase());
								                			//}
									                	}
														if("0".equals(a_state))
															value="";																						
													}
													int lines = getLines(value,fieldformat.getWidth(),setbo.getFontname(),setbo.getFonteffect(),fontsize);
										        	Paragraph para=new Paragraph(value,FontFamilyType.getFont(setbo.getFontname(),fonteffect,fontsize));
													cell=new PdfPCell(para);  
													cell.setBorder(0);
	
												if(subdom.isBhl())
													cell.setBorderWidthBottom(1f);
												
													setSubSetHAlign(fieldformat.getAlign(),cell);	
													setSubSetVAlign(0,cell);	
													if(lines>height){
														height = lines;
													}
													if(subdom.getFieldfmtlist().size()-1==j){
														rtop = rtop+cell_height*(height);
														cellHight = cellHight+cell_height*(height);
														if(isHaveTop){
															top = top+cell_height*(height);												
														}
													}
											        cell.setFixedHeight(cell_height*lines);							     
											        cell.setNoWrap(false);//
											        table.addCell(cell);
										        }
										        valueMap = new HashMap();
											}
								        }
							        	if(isChangePage&&i==len-1)//换页走到最后，把新页高度重新写入
							        		setbo.setRheight(cellHight);
							        	//liuyz bug26502 26764 导出pdf设计高度比实际高度大一点，导致输出pdf格式不好看。如果子集的实际高度小于设计高度且差值小于行高2倍，就将下一个子集或者主集的top值改变。
							        	if(setbo.getRheight()>cellHight&&setbo.getRheight()-cellHight<cell_height*2&&i==len-1)
									    {
							        		top=top-(setbo.getRheight()-cellHight);
									    }
							        }
							    if(flag1)
							    	rs.previous();
							    //liuyz bug26502 26764 导出pdf设计高度比实际高度大一点，导致输出pdf格式不好看。如果子集的实际高度小于设计高度且差值小于行高2倍,且内容不为空，更改子集外框高度，使子集外框高度等于实际高度。
							    if(setbo.getRheight()>cellHight&&len>0&&setbo.getRheight()-cellHight<cell_height*2)
							    {
							    	setbo.setRheight(cellHight);
							    	updateHight(pglist,cellHight,rset.getInt("RHeight"),rset.getInt("RTop"));
							    }
								if(cellHight>setbo.getRheight()){//子集超过模块高度了，都需要重新算
									int hight = cellHight;
									cellHight = cellHight-setbo.getRheight();
								     if(cellHight>0&&!isHaveTop){//如果已经换页了，top重新赋值了，即isHaveTop为true 则不考虑此种情况
								    	 setbo.setRheight(hight);
								    	 top = top+cellHight;
								    	 updateHight(pglist,hight,rset.getInt("RHeight"),rset.getInt("RTop"));
								     }else{
								    	 setbo.setRheight(hight);
								    	 updateHight(pglist,hight,rset.getInt("RHeight"),rset.getInt("RTop"));
								     }
								}
								if(isChangePage)
									this.changeAfterHight(pglist, cellHight,cellHight,setbo.getRtop());
				        	}
				        }
	        			if(setbo.getRtop()+setbo.getRheight()>bottom_margin&&!(subdom.getDatarowcount()>0)){//&&RecordList.size()==0 //固定高度，指定行数不按此执行。
							list.add(table);
							setbo.setRheight(bottom_margin-setbo.getRtop());//换页之前给子集一个高度，即留在上一页的高度
							celllist.add(setbo);
							celllist.add(list);
							pglist.add(celllist);
							this.changeBefforeHight(pglist, bottom_margin-setbo.getRtop(), rset.getInt("RHeight"),setbo.getRtop(),rememberlist);
							pagelist.add(pglist);
							celllist = new ArrayList();
							for(int t=0;t<titleList.size();t++){
								TTitle title=(TTitle) titleList.get(t);
								celllist.add(title);
								celllist.add(list);
								celllist.add("title");
								pglist.add(celllist);
								celllist = new ArrayList();
							}
							celllist = new ArrayList();
							pglist = new ArrayList();
							rtop = top_margin;
							top = top_margin-(rset.getInt("RTop")+rset.getInt("RHeight"));//能放下表头且表内没数据则砍断放到页尾，相当于此表以下所有内容向上移动top_margin-(rset.getInt("RTop")+rset.getInt("RHeight"))
							
	        				for(int t=0;t<rememberlist.size();t++){
	        					pglist.add(rememberlist.get(t));
	        				}
							this.changeAfterHight(pglist, 0,0,rtop);
	        			}else{
	        				//liuyz  bug26967,bug26953 固定行高，指定行数，切页。
	        				boolean isChangePage=false;
	        				if(subdom.getDatarowcount()>0)
	        				{
	        					int old_temp_top=temp_top;
	        					if(table.getTotalHeight()>setbo.getRheight())
	        					{
	        						temp_top+=(int)Math.ceil(table.getTotalHeight())-setbo.getRheight();
	        						setbo.setRheight((int)Math.ceil(table.getTotalHeight()));
	        					}
	        					list.add(table);
	        					if(setbo.getRheight()+setbo.getRtop()>bottom_margin&&setbo.getRheight()<setbo.getRwidth()){//宽大于高（这样不会是子集两侧的东西了）或者大文本超过一页直接换到下一页去，不截断显示了
	    							pagelist.add(pglist);
	    							this.changeBefforeHight(pglist, 0, 0,setbo.getRtop(),rememberlist);
	    							celllist = new ArrayList();
	    							for(int t=0;t<titleList.size();t++){
	    								TTitle title=(TTitle) titleList.get(t);
	    								celllist.add(title);
	    								celllist.add(list);
	    								celllist.add("title");
	    								pglist.add(celllist);
	    								celllist = new ArrayList();
	    							}
	    							celllist = new ArrayList();
	    							pglist = new ArrayList();
	    							
	    							top = top_margin - rset.getInt("RTop")-old_temp_top;//换页之后取得实际距离与库中数据的差值，后面的数据只有加上这个差值就可以获得实际位置
	    							setbo.setRtop(top_margin);
	    							isHaveTop = false;
	    							
	    							celllist.add(setbo);
	    							celllist.add(list);
	    							pglist.add(celllist);
	    							celllist = new ArrayList();
	    							
	    							isHaveTop = false;
	    							list = new ArrayList();
	    	        				for(int t=0;t<rememberlist.size();t++){
	    	        					pglist.add(rememberlist.get(t));
	    	        				}
	    							this.changeAfterHight(pglist, 0,0,top_margin);
	    							isChangePage=true;
	    						}
	        				}
	        				if(!isChangePage)
	        				{
		        				list.add(table);
		        				celllist.add(setbo);
		        				celllist.add(list);				
		        				pglist.add(celllist);
		        				for(int t=0;t<rememberlist.size();t++){
		        					pglist.add(rememberlist.get(t));
		        				}
		        				this.changeAfterHight(pglist, cellHight,cellHight,setbo.getRtop());
		        				celllist = new ArrayList();
	        				}
	        			}
					}
				}
				temp_top=0;//记录固定高度、行数子集实际高度和设计高度差值
				if(pglist.size()>0){
					pagelist.add(pglist);
					celllist = new ArrayList();
					for(int t=0;t<titleList.size();t++){
						TTitle title=(TTitle) titleList.get(t);
						celllist.add(title);
						celllist.add(list);
						celllist.add("title");
						pglist.add(celllist);
						celllist = new ArrayList();
					}
					top=top_margin;
				}
				pglist = new ArrayList();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return top;	
	}
	/**
	 * 得到附件内容
	 * @param ins_id
	 * @param setname
	 * @param dbpre
	 * @param obj_id
	 * @param uv
	 * @return
	 */
	public ArrayList getAttachRecordlist(int ins_id, String setname, String dbpre, String obj_id, UserView uv) {
		ArrayList recordList = new ArrayList();
		StringBuffer sb = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset=null;
		try{
			if(ins_id!=0){//进入了审批流
				if("attachment_0".equals(setname)){//公共附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) ");
				}else if("attachment_1".equals(setname)&&obj_id.length()>0){//个人附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and t.attachmenttype=1");
					sb.append(" and t.objectid='");
					sb.append(obj_id);
					sb.append("'");
					if(StringUtils.isNotBlank(dbpre)){//infor_type=1
						sb.append(" and t.basepre='");
						sb.append(dbpre);
						sb.append("'");
					}
				}
			}else{//还未进入审批流
				if("attachment_0".equals(setname)){//公共附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
					sb.append(" and t.create_user='");
					sb.append(uv.getUserName());
					sb.append("' ");
				}else if("attachment_1".equals(setname)&&obj_id.length()>0){//个人附件
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and t.attachmenttype=1");
					sb.append(" and t.create_user='");
					sb.append(uv.getUserName());
					sb.append("' and t.objectid='");
					sb.append(obj_id);
					sb.append("'");
					
					if(StringUtils.isNotBlank(dbpre)){//infor_type=1
						sb.append(" and t.basepre='");
						sb.append(dbpre);
						sb.append("'");
					}
				}
			}
			if(sb.length()>0){
				sb.append(" order by file_id");
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					HashMap map = new HashMap();
					map.put("attachmentname", frowset.getString("name").replaceAll("--", "－－"));//-- 半角转为全角 以后有其他字符再做处理  26500 changxy  20170425
					if("attachment_1".equals(setname))
						map.put("sortname", frowset.getString("sortname"));
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					map.put("create_time", d_str);
					String name = frowset.getString("fullname");
					String user_name = frowset.getString("create_user");//下载不要
					if(StringUtils.isBlank(name))
						name = user_name;
					map.put("fullname", name);
					recordList.add(map);
				} //while loop end
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordList;
	}
	/**
	 * 得到附件的表格列头xml
	 * @param sub_domain 
	 * @param setbo 
	 * @param b 
	 */
	public String getAcctch_domain(String sub_domain, TemplateSetBo setbo) {
		String attach_domain = "";
		String attachmenttype = "";
		Document doc=null;
		Element element=null;
		String xpath="/sub_para/para";
		if(sub_domain!=null&&sub_domain.trim().length()>0){
			try{
				doc=PubFunc.generateDom(sub_domain);
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点  xpath="/sub_para/para";
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0){
					element=(Element)childlist.get(0);
					if(element.getAttributeValue("attachmentType")!=null){
						attachmenttype = element.getAttributeValue("attachmentType");
						setbo.setSetname("attachment_"+attachmenttype);
						setbo.setAttachmentType(attachmenttype);
					}else
						setbo.setSetname("attachment_0");
				}
			    }catch(Exception e){
					
				}
			}else{
				setbo.setSetname("attachment_0");
			}
		attach_domain+="<?xml version='1.0' encoding='GB2312'?>";
		attach_domain+="<sub_para>";
		if("1".equals(attachmenttype)){
			//if(isNull)
			//	attach_domain+="<para setname='attachment' hl='true' vl='false' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`sortname`fullname`create_time`' autoextend='true'/>";
			//else
				attach_domain+="<para setname='attachment' hl='true' vl='true' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`sortname`fullname`create_time`' autoextend='true'/>";
		}else{
			//if(isNull)
			//	attach_domain+="<para setname='attachment' hl='true' vl='false' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`fullname`create_time`' autoextend='true'/>";
			//else
				attach_domain+="<para setname='attachment' hl='true' vl='true' colhead='true' colheadheight='8' datarowcount='0' fields='attachmentname`fullname`create_time`' autoextend='true'/>";
		}
		attach_domain+="<field name='attachmentname' need='false' width='50' title='名称' default='' align='0' slop='0' pre='' valign='1'/>";
		if("1".equals(attachmenttype))
			attach_domain+="<field name='sortname' need='false' width='15' title='文件类型' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="<field name='fullname' need='false' width='15' title='创建人' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="<field name='create_time' need='false' width='20' title='创建时间' default='' align='0' slop='0' pre='' valign='1'/>";
		attach_domain+="</sub_para>";
		return attach_domain;
	}
	/**
	 * 
	 * @Title: getPosition   
	 * @Description: 根据每页信息获取该页标题外内容的上下起始位置 严格按照像素算的，如果控制上下差10个像素会不对
	 * @param @param pgmap
	 * @param @param pgmap wh[1]  0宽1高2顶3底4右5左
	 * @param @return 
	 * @return HashMap 
	 * @author:zhaoxg   
	 * @throws
	 */
	private HashMap getPosition(HashMap pgmap,float[] wh){
		HashMap map = new HashMap();
		try{
			int top = 0;
			int bottom = 0;
			int i=0;
			RowSet contextrs=(RowSet) pgmap.get("context");//内容区域所有具体模板位置值
			contextrs.beforeFirst();
			while(contextrs.next()){
				if(i==0){
					top = contextrs.getInt("RTop");//理论上第一个就是最小的，先把最小的赋值进去
				}
				if(contextrs.getInt("RTop")<top)//取最小的为头
					top = contextrs.getInt("RTop");
				if(contextrs.getInt("RTop")+contextrs.getInt("RHeight")>bottom){//最大为尾
					bottom = contextrs.getInt("RTop")+contextrs.getInt("RHeight");
				}
				i++;
			}
			
			i=0;
			RowSet titlers=(RowSet) pgmap.get("title");//标题具体模板位置值
			titlers.beforeFirst();
			/** 查找离内容区域最近的上标题 start*/
			int vtop = -1;
			int stop = -1;
			while(titlers.next()){
				if(titlers.getInt("rtop")+titlers.getInt("rheight")<top){//标题只能出现在头或者尾部，所以有比他低的标题就把范围缩进到标题下面
					int ctop = top - (titlers.getInt("rtop")+titlers.getInt("rheight"));
					if(vtop==-1)
						vtop = ctop;
					if(ctop<vtop){
						vtop = ctop;
						stop = titlers.getInt("rtop")+titlers.getInt("rheight");
					}
				}
				if(titlers.getInt("rtop")+titlers.getInt("rheight")>top){
					break;
				}
			}
			if(stop!=-1)
				top = stop+5;//暂时多加5个像素 感觉内容与上标题离得有点近
			/** 查找离内容区域最近的上标题 end*/
			titlers.beforeFirst();
			while(titlers.next()){
				if(titlers.getInt("rtop")>bottom){//标题只能出现在头或者尾部，所以有比他高的标题就把范围缩进到标题上面
					bottom = titlers.getInt("rtop");
					break;
				}
			}

//			System.out.println(top+"|||||"+bottom);
			map.put("top", top+"");
			map.put("bottom", bottom+"");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}
	/**
	 * 
	 * @Title: updateHight   
	 * @Description: 没换页把子集左侧的东西拉长跟子集一样高 
	 * @param @param pglist
	 * @param @param rheight 实际高度
	 * @param @param rh 库中存的高度
	 * @param @param rt 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	public void updateHight(ArrayList pglist,int rheight,int rh,int rt){
		for(int i=0;i<pglist.size();i++){
			ArrayList celllist = (ArrayList) pglist.get(i);
			if(celllist.size()<3){
				TemplateSetBo setbo=(TemplateSetBo) celllist.get(0);
				if(setbo.getRtop()<=rt&&setbo.getRtop()+setbo.getRheight()>=rh+rt){
//					if(rh==setbo.getRheight()){
						setbo.setRheight(setbo.getRheight()+rheight-rh);
						celllist.set(0, setbo);
						pglist.set(i, celllist);
//					}
				}
			}
		}
	}
	/**
	 * 
	 * @Title: changeBefforHight   
	 * @Description: 换页之前切断与其有交集的东西
	 * @param @param pglist
	 * @param @param rheight 页面实际高度
	 * @param @param rh 库中存的实际高度
	 * @param @param rt 页面的rtop
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	private void changeBefforeHight(ArrayList pglist,int rheight,int rh,int rt,ArrayList rememberlist){
		for(int i=0;i<pglist.size();i++){
			ArrayList celllist = (ArrayList) pglist.get(i);
			if(celllist.size()<3){//&&i!=pglist.size()-1
				TemplateSetBo setbo=(TemplateSetBo) celllist.get(0);
				if(!setbo.isSubflag()){
					if(setbo.getRtop()<=rt&&setbo.getRtop()+setbo.getRheight()>=rh+rt){
						if(setbo.getRtop()==rt&&rheight==0){//如果是当前元素整体被挤到下面去了，那么跟他左右对齐的东西全拿下去；否则截取相应长度打印
							pglist.remove(i);
						}else{
							TemplateSetBo _setbo = (TemplateSetBo) setbo.clone();
							_setbo.setRheight(rt-setbo.getRtop()+rheight);
							
							ArrayList _celllist = (ArrayList) celllist.clone();
							_celllist.set(0, _setbo);
							pglist.set(i, _celllist);
						}
						
						if(setbo.getRtop()+setbo.getRheight()>rt){//之前存储的元素只有超过当前元素才记住，否则应该都留在上页而不该拿下去
							TemplateSetBo bo = (TemplateSetBo) setbo.clone();
							bo.setRheight(bo.getRheight()+bo.getRtop()-(rt+rh));//存实际的差值，后面直接加上即可
							ArrayList list = new ArrayList();
							list.add(bo);
							list.add(new ArrayList());
							list.add("remember");//换页把需要记录的非子集记录存放到remember的list中方便换页后找到
							rememberlist.add(list);
						}
					}
				}
			}
		}
	}
	/**
	 * 
	 * @Title: changeAfterHight   
	 * @Description:  换页之后把子集左侧的东西带过来跟子集一样高
	 * @param @param pglist
	 * @param @param rheight
	 * @param @param rh
	 * @param @param rt 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	private void changeAfterHight(ArrayList pglist,int rheight,int rh,int rt){
		for(int i=0;i<pglist.size();i++){
			ArrayList celllist = (ArrayList) pglist.get(i);
			if(celllist.size()>2&& "remember".equals(celllist.get(2))){//换页后直接处理换页前记住的内容即可
				TemplateSetBo setbo=(TemplateSetBo) celllist.get(0);
				if(!setbo.isSubflag()&&setbo.getRheight()+rheight>0){//不是子集,且高度不可为0，否则就不需打印
					TemplateSetBo _setbo = (TemplateSetBo) setbo.clone();
					_setbo.setRheight(setbo.getRheight()+rheight);
					_setbo.setRtop(rt);
					ArrayList _celllist = new ArrayList();
					_celllist.add(_setbo);
					_celllist.add(new ArrayList());
					_celllist.add("");
					pglist.set(i, _celllist);
					pglist.add(_celllist);
				}else{
					pglist.remove(i);
				}
			}
		}
	}
    /**
     *子集水平单元格内容的排列方式
     * @param align =
     * @param cell
     */
    private void setSubSetHAlign(int align, PdfPCell cell) {

        if(align==0)   
        {
            cell.setHorizontalAlignment(0);//左    
        }
        else if(align==1)
        {
            cell.setHorizontalAlignment(1);//中
        }
        else if(align==2)
        {
            cell.setHorizontalAlignment(2);//右
        }
    }
    
    /**
     *子集垂直单元格内容的排列方式
     * @param align =
     * @param cell
     */
    private void setSubSetVAlign(int align, PdfPCell cell) {

        if(align==0)   
        {
            cell.setVerticalAlignment(4);//上
        }
        else if(align==1)
        {
            cell.setVerticalAlignment(5);//中
        }
        else if(align==2)
        {
            cell.setVerticalAlignment(6);//下
        }
    }
	   /**
     * 根据列宽计算出字符串折行的行数
     * @param str
     *            填入列中字符串
     * @param columnWidth
     *            列的固定宽度
     * @param fontName
     *            字体名称
     * @param fontEffect
     *            已设好的字体样式
     * @param fontSize
     *            字体大小
     * @return
     */
    public int getLines(String str, int columnWidth, String fontName,
            int fontEffect, int fontSize) 
    {
        int lines=1;
        int num = 0;//回车的个数
        try{
                BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = gg.createGraphics(); // 获得画布
  
                Font font = new Font(fontName, fontEffect, fontSize);
                g.setFont(font);
                float hzScale=1.0f;
                if (fontSize<=5) 
                    hzScale=2.3f;
                else if (fontSize<=7) 
                    hzScale=2.0f; 
                else if (fontSize<=9) 
                    hzScale=1.8f; 
                else if (fontSize<11) 
                    hzScale=1.5f; 
                else if (fontSize>14)
                    hzScale=2;          
                int awidth = g.getFontMetrics().stringWidth(str);
                int charlen = g.getFontMetrics().charWidth('c');
                int hzcharlen = g.getFontMetrics().charWidth('汉');
                
                awidth =charlen*str.getBytes().length; 
                int hzLen=0;
                if (str==null){  
                    str="";
                }  
                if (!"".equals(str)){
                	if(str.indexOf("\n")!=-1||str.indexOf("\r")!=-1){
                		String[] nstr = str.split("\n");
                		for(int i=0;i<nstr.length;i++){
                			if(i>0)
                				lines++;
                			String[] rstr = nstr[i].split("\r");
                			for(int j=0;j<rstr.length;j++){
                				if(j>0)
                					lines++;
                				int rwidth =charlen*rstr[j].getBytes().length;
                				int hzlength = 0;
                				for(int t=0;t<rstr[j].length();t++){
                                    char c =rstr[j].charAt(t); 
                                    if (String.valueOf(c).getBytes().length>1){
                                    	hzlength++;
                                        continue;
                                    }
                                    if(isHZChar(c)){                            
                                    	hzlength++;  
                                    }      
                				}
                				rwidth =rwidth+ Math.round(hzlength*hzScale);
                                lines = lines + rwidth/columnWidth;
                                if(rwidth/columnWidth>1)//多于一行就自增下
                                	lines++;
                			}
                		}
                	}else{
                        for(int i=0;i<str.length();i++)
                        {
                            char c =str.charAt(i); 
                            if (String.valueOf(c).getBytes().length>1){
                                hzLen++;
                                continue;
                            }
                            if(c=='\n'){
                            	num++;
                                continue;
                            }
                            if(c=='\r') { 
                            	num++;
                                continue;
                            }
                            if(isHZChar(c)){                            
                                hzLen++;  
                            }                          
                        }
                        
                        awidth =awidth+ Math.round(hzLen*hzScale);
                        lines = awidth/columnWidth;
                        lines++;
                	}

                    if (fontSize<14){                    
                        if (str.length()==hzLen){//全汉字
                            if (((awidth % columnWidth) *1.0f/columnWidth)>0.90) {
                                lines++;
                            }
                        }
                        else if (hzLen==0){//全非汉字
                            if (((awidth % columnWidth) *1.0f/columnWidth)<0.10) {
                                lines--;
                            }
                        }                        
                    }
                    if (lines>1){//字两边空白大，行数不够的问题
                        awidth=awidth+Math.round(lines*hzcharlen);
                        int alines = awidth/columnWidth;
                        int amod=awidth % columnWidth;
                        if (amod >0) alines++;
                        if (lines<alines) lines=alines;                       
                    }                   
                }           
        }
        catch(Exception e){
            e.printStackTrace();            
        }
        
        if (lines<1) lines=1;
        if(num>lines)lines=num;
        return lines;
    }
    /**
     * 取得上一页留下什么内容，下一页剩下什么内容
     * @param str
     * @param columnWidth
     * @param fontName
     * @param fontEffect
     * @param fontSize
     * @param lows  //留下行数
     * @return
     */
    public ArrayList getNextUpChar(String str, int columnWidth,TemplateSetBo setbo, int fontSize,int lows,int line){
    	ArrayList list = new ArrayList();
        int lines=1;
        String aa = str;
        int num = 0;//回车的个数
        try{
                BufferedImage gg = new BufferedImage(1, 1,
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = gg.createGraphics(); // 获得画布
  
                Font font = new Font(setbo.getFontname(), setbo.getFonteffect(), fontSize);
                g.setFont(font);
                float hzScale=1.0f;
                if (fontSize<=5) 
                    hzScale=2.3f;
                else if (fontSize<=7) 
                    hzScale=2.0f; 
                else if (fontSize<=9) 
                    hzScale=1.8f; 
                else if (fontSize<11) 
                    hzScale=1.5f; 
                else if (fontSize>14)
                    hzScale=2;          
                int awidth = g.getFontMetrics().stringWidth(str);
                int charlen = g.getFontMetrics().charWidth('c');
                int hzcharlen = g.getFontMetrics().charWidth('汉');
                
                awidth =charlen*str.getBytes().length; 
                int hzLen=0;
                if (str==null){  
                    str="";
                }  
                StringBuffer st = new StringBuffer();
                OK: 
                if (!"".equals(str)&&list.size()==0){              	
                	if(str.indexOf("\n")!=-1||str.indexOf("\r")!=-1){
                		String[] nstr = str.split("\n");
                		for(int i=0;i<nstr.length;i++){
                			if(i>0)
                				lines++;
                			if(lines>lows){
                				list.add(st.toString());
                				list.add(str.substring(st.length()));
                				break OK;
                			}
                			String[] rstr = nstr[i].split("\r");
                			for(int j=0;j<rstr.length;j++){
                				if(j>0)
                					lines++;
                    			if(lines>lows){
                    				list.add(st.toString());
                    				list.add(str.substring(st.length()));
                    				break OK;
                    			}
                				int rwidth =charlen*rstr[j].getBytes().length;
                				int hzlength = 0;
                				for(int t=0;t<rstr[j].length();t++){
                                    char c =rstr[j].charAt(t); 
                                    st.append(c);
                                    if(t==rstr[j].length()-1){
                                    	st.append("\r");
                                    }
                                    if (String.valueOf(c).getBytes().length>1){
                                    	hzlength++;
                                        continue;
                                    }
                                    if(isHZChar(c)){                            
                                    	hzlength++;  
                                    }      
                				}
                				rwidth =rwidth+ Math.round(hzlength*hzScale);
                                lines = lines + rwidth/columnWidth;
                    			if(lines>lows){
                    				list.add(st.toString());
                    				list.add(str.substring(st.length()));
                    				break OK;
                    			}
                                if(rwidth/columnWidth>1)//多于一行就自增下
                                	lines++;
                    			if(lines>lows){
                    				list.add(st.toString());
                    				if(str.length()>st.length())
                    					list.add(str.substring(st.length()));
                    				break OK;
                    			}
                			}
                            if(i==nstr.length-1){
                            	st.append("\n");
                            }
                		}
                		list.add(st.toString());
                	}else{
                		int n = line/lows;
                		boolean flag = true;
                        for(int i=0;i<str.length();i++)
                        {
                        	if(i>str.length()*lows/line&&flag){
                        		list.add(st.toString());
                        		st=new StringBuffer();
                        		flag=false;
                        	}
                            char c =str.charAt(i); 
                            st.append(c);                      
                        }
                        list.add(st.toString());
                	}                   
                }           
        }
        catch(Exception e){
            e.printStackTrace();            
        }
        return list;    
    }
    private  boolean isHZChar(char c)
    {
        boolean isCorrect =false;
        if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
        {   
          //字母,   数字   
            isCorrect =false;  
        }else if(c=='-'||c=='/'){
            isCorrect =true; 
        }else{   
          if(Character.isLetter(c))
          {   //中文   
              isCorrect =true; 
          }else{   //符号或控制字符   
              isCorrect =false; 
          }   
        } 
        return isCorrect;
    } 
	/**
	 * 重新取得线型，由于画线的原因
	 * @param tabid
	 * @param pageid
	 * @param flag
	 * @param line
	 * @param setbo
	 * @param dao
	 * @return
	 */
    public int  getRline(int tabid,int pageid ,String flag,int line,TemplateSetBo setbo,ContentDAO dao)
    {
    	if(line==0)
    		return line;
    	else
    	{
    		StringBuffer sql=new StringBuffer();
    		RowSet rs=null;
    		sql.append("select * from Template_Set where tabid="+tabid+" and pageid="+pageid+" and gridno<>"+setbo.getGridno()+"");
    	    try
    	    {   if("t".equals(flag))
    	        {
    	    	   sql.append(" and b=0");
    	    	   sql.append(" and (rtop+rheight)="+setbo.getRtop()+" ");	
    	    	   sql.append(" and ((rleft>="+setbo.getRleft()+" and (rleft+rwidth)<="+(setbo.getRleft()+setbo.getRwidth())+")");
   	    		   sql.append(" or(rleft<="+setbo.getRleft()+" and (rleft+rwidth)>="+(setbo.getRleft()+setbo.getRwidth())+"))");
   	    		   rs=dao.search(sql.toString());
	    	       if(rs.next())
	    	    	line=0;
    	        }else if("b".equals(flag))
    	        {
    	        	sql.append(" and t=0");    	    		
    	    		sql.append(" and rtop=("+(setbo.getRtop()+setbo.getRheight())+") ");
    	    		sql.append(" and ((rleft>="+setbo.getRleft()+" and (rleft+rwidth)<="+(setbo.getRleft()+setbo.getRwidth())+")");
    	    		sql.append(" or(rleft<="+setbo.getRleft()+" and (rleft+rwidth)>="+(setbo.getRleft()+setbo.getRwidth())+"))");
    	    		rs=dao.search(sql.toString());
    	    	    if(rs.next())
    	    	    	line=0;
    	    	}else if("l".equals(flag))
    	    	{
    	    		sql.append(" and r=0");
    	    		sql.append(" and (rleft+rwidth)="+setbo.getRleft()+"");
    	    		sql.append(" and ((rtop<="+setbo.getRtop()+" and (rtop+rheight)>="+(setbo.getRtop()+setbo.getRheight())+")");
    	    		sql.append(" or (rtop>="+setbo.getRtop()+" and (rtop+rheight)<="+(setbo.getRtop()+setbo.getRheight())+"))");
    	    		rs=dao.search(sql.toString());
    	    	    if(rs.next())
    	    	    	line=0;
    	    	}else if("r".equals(flag))
    	    	{
    	    		sql.append(" and l=0");
    	    		sql.append(" and rleft="+((setbo.getRleft()+setbo.getRwidth())));
    	    		sql.append("  and ((rtop<="+setbo.getRtop()+" and (rtop+rheight)>="+(setbo.getRtop()+setbo.getRheight())+")");
    	    		sql.append(" or (rtop>="+setbo.getRtop()+" and (rtop+rheight)<="+(setbo.getRtop()+setbo.getRheight())+"))");
    	    		rs=dao.search(sql.toString());
    	    		//System.out.println(sql.toString());
    	    	    if(rs.next())
    	    	    	line=0;
    	    	}
    	    }catch(Exception e)
    	    {
    	    	e.printStackTrace();
    	    }
    	}    	
    	return line; 
    }
    /**
     * 重新取得线型，由于画线的原因
     * @param list
     * @param flag
     * @param line
     * @param cur_setbo//当前操作对象
     * @return
     */
    public int  getRlineForList(ArrayList list,String flag,int line,TemplateSetBo cur_setbo)
    {
    	if(line==0)
    		return line;
    	else
    	{
    		float cur_rtop=cur_setbo.getRtop();//得到当前单元格的顶部
    		float cur_rheight=cur_setbo.getRheight();//得到当前单元格的高度
    		float cur_rleft=cur_setbo.getRleft();//得到当前单元格的左部
			float cur_rwidth=cur_setbo.getRwidth();////得到当前单元格的宽度
			TemplateSetBo setbo;  
    		float rtop=0;
    		float rheight=0;
    		float rleft=0;
    		float rwidth=0;
    		int b=0;
    		int t=0;
    		int r=0;
    		int l=0;
    		int cur_gridno=cur_setbo.getGridno();
    		int gridno=0;
    	    try
    	    {  
    	    	for(int i=0;i<list.size();i++)
        		{
    	    		setbo=(TemplateSetBo)list.get(i);  
        			rtop=setbo.getRtop();
        			rheight=setbo.getRheight();
        			rleft=setbo.getRleft();
        			rwidth=setbo.getRwidth();
        			gridno=setbo.getGridno();
        			if(cur_gridno==gridno)
        				continue;
        			if("t".equals(flag))
        	        {
        			   b=setbo.getB();//得到每一个单元格的下部        			   
         	    	   if(b==0)
         	    	   {
         	    		 if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
           	    	      {
         	    			 line=0;
       	    			     break;
       	    		      }
         	    	   }
        	        }else if("b".equals(flag))
        	        {
        	        	t=setbo.getT();
        	        	if(t==0)
        	        	{
        	        		if(rtop==(cur_rtop+cur_rheight)&&
        	        		    ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
        	        		     (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
        	        		    )
        	        		  )
        	        		{
        	        			line=0;
          	    			     break;
        	        		}
        	        	}        	        	
        	    	}else if("l".equals(flag))
        	    	{
        	    		r=setbo.getR();
        	    		if(r==0)
        	    		{
        	    			if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
        	    			{
        	    				line=0;
         	    			    break;
        	    			}
        	    		}        	    		
        	    	}else if("r".equals(flag))
        	    	{
        	    		l=setbo.getL();
        	    		if(l==0)
        	    		{
        	    			if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
        	    			{
        	    				line=0;
        	    			    break;
        	    			}
        	    		}
        	    	}
        		}
    	    	
    	    }catch(Exception e)
    	    {
    	    	e.printStackTrace();
    	    }
    	}    	
    	return line; 
    }
    
    /**
	 * 获得节点定义的指标权限
	 * @param task_id 
	 * @return
	 */
    public HashMap getFieldPrivByNode(String task_id){
    	HashMap _map=new HashMap();
    	Document doc=null;
        Element element=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		        String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
		        if("0".equals(task_id)) {
		        	//发起节点控制指标权限
		        	sql="select * from t_wf_node where tabid="+this.tabid+" and nodetype=1";
		        }
                RowSet rowSet=dao.search(sql);
                if(rowSet.next())
                {
                    String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
                    if(ext_param!=null&&ext_param.trim().length()>0)
                    {
                        doc=PubFunc.generateDom(ext_param);
                        String xpath="/params/field_priv/field";
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist=findPath.selectNodes(doc);   
                        if(childlist.size()==0){
                            xpath="/params/field_priv/field";
                             findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                             childlist=findPath.selectNodes(doc);
                        }
                        if(childlist!=null&&childlist.size()>0)
                        {
                            for(int i=0;i<childlist.size();i++)
                            {
                                element=(Element)childlist.get(i);
                                String editable="";
                                //0|1|2(无|读|写)
                                if(element!=null&&element.getAttributeValue("editable")!=null)
                                    editable=element.getAttributeValue("editable");
                                if(editable!=null&&editable.trim().length()>0)
                                {
                                    String columnname=element.getAttributeValue("name").toLowerCase();
                                    _map.put(columnname, editable);
                                }
                                
                            }
                        }
                    } 
                }  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
    }
    /**
	 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPriv(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					doc=PubFunc.generateDom(ext_param);; 
					String xpath="/params/field_priv/field";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist=findPath.selectNodes(doc);	
					if(childlist.size()==0){
						xpath="/params/field_priv/field";
						 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						 childlist=findPath.selectNodes(doc);
					}
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							String editable="";
							//0|1|2(无|读|写)
							if(element!=null&&element.getAttributeValue("editable")!=null)
								editable=element.getAttributeValue("editable");
							if(editable!=null&&editable.trim().length()>0)
							{
								String columnname=element.getAttributeValue("name").toLowerCase();
								if(columnname.endsWith("_2")){
									if("1".equals(editable))
										editable="0";
									String fillable = element.getAttributeValue("fillable");
									if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable))
										editable="3";
									_map.put(columnname, editable);
								}
								
							}
							
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	public boolean isIsprint() {
		return isprint;
	}
	public void setIsprint(boolean isprint) {
		this.isprint = isprint;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}


	public int getPageid() {
		return pageid;
	}


	public void setPageid(int pageid) {
		this.pageid = pageid;
	}


	public int getTabid() {
		return tabid;
	}


	public HashMap getSub_domain_map() {
		return sub_domain_map;
	}


	public void setSub_domain_map(HashMap sub_domain_map) {
		this.sub_domain_map = sub_domain_map;
	}


	public void setTabid(int tabid) {
		this.tabid = tabid;
	}


	public HashMap getField_name_map() {
		return field_name_map;
	}


	public void setField_name_map(HashMap field_name_map) {
		this.field_name_map = field_name_map;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	
}
