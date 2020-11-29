package com.hjsj.hrms.utils.components.defineformula.businessobject;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class GzStandardItemBo {
	Connection conn=null;
	private String hfactor="";
	private String s_hfactor="";
	private String vfactor="";
	private String s_vfactor="";
	private String item="";
	private String hcontent="";
	private String vcontent="";
	private String standID="";
	private String pkg_id="";
	private String opt="";
	private HashMap itemValueMap=new HashMap();
	private HashMap itemNameMap=new HashMap();
	private boolean isLocked=true;
	private UserView userView;
	public GzStandardItemBo()
	{
		
	}
	public GzStandardItemBo(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
	}
	
	public GzStandardItemBo(Connection con,String hfactor,String s_hfactor,String hcontent,String vfactor,String s_vfactor,String vcontent,String item,String standID,String opt,String pkg_id) throws GeneralException
	{
		try {
			this.conn=con;
			this.hfactor=hfactor;
			this.s_hfactor=s_hfactor;
			this.hcontent=hcontent;
			this.vfactor=vfactor;
			this.s_vfactor=s_vfactor;
			this.vcontent=vcontent;
			this.item=item;
			this.standID=standID;
			this.opt=opt;
			this.pkg_id=pkg_id;
			if("edit".equals(opt))
				this.itemValueMap=getGzItemMap();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	/**
	 * 取得有效标识
	 * @param pkg_id
	 * @return
	 */
	public int getFlag(String pkg_id)throws GeneralException
	{
		int flag=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select status from gz_stand_pkg where pkg_id="+pkg_id);
			if(rowSet.next())
				flag=Integer.parseInt(rowSet.getString("status"));
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	/**
	 * @author lis
	 * @Description: 获得标准表vo
	 * @date 2016-2-15
	 * @return
	 */
	public GzStandardItemVo getGzStandardItemVo()
	{
		GzStandardItemVo vo=new GzStandardItemVo();
		vo.setH_List(get_List(this.hfactor,this.s_hfactor,this.hcontent));
		vo.setV_List(get_List(this.vfactor,this.s_vfactor,this.vcontent));
		vo.setH_bottomColumn_num(get_bottomColumn_num(vo.getH_List()));
		vo.setV_bottomColumn_num(get_bottomColumn_num(vo.getV_List()));
		FieldItem item=DataDictionary.getFieldItem(this.item);
		vo.setResultItem(item);
		if(item!=null){
			if("N".equalsIgnoreCase(item.getItemtype()))
				vo.setResultItemType("N");
			else
			{	
				vo.setResultItemType("C");
				vo.setCodesetid(item.getCodesetid());
			}
		}else{
			vo.setResultItemType("N");
		}
		vo.setGzItemList(gzItemList(vo));
		vo.setHfactor(this.getHfactor());
		vo.setS_hfactor(this.getS_hfactor());
		vo.setVfactor(this.getVfactor());
		vo.setS_vfactor(this.getS_vfactor());
		vo.setItem(this.getItem());
		vo.setHcontent(this.getHcontent());
		vo.setVcontent(this.getVcontent());
		return vo;
	}
	
	/**
	 * @author lis
	 * @Description: 初始化获得横向和纵向指标map
	 * @date 2016-2-15
	 */
	public void  init() throws GeneralException
	{
		this.itemNameMap=getItemNameMap();
	}
	
	/**
	 * @author lis
	 * @Description: 横向指标值和纵向指标值
	 * @date 2016-2-15
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getGzItemMap() throws GeneralException
	{
		HashMap map=new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select * from gz_item_history where id='"+this.standID+"' and pkg_id='"+this.pkg_id+"'");
			while(rowSet.next())
			{
				
				String hvalue="#";
				if(rowSet.getString("hvalue")!=null&&rowSet.getString("hvalue").trim().length()>0)
					hvalue=rowSet.getString("hvalue");
			    String s_hvalue="#";
			    if(rowSet.getString("s_hvalue")!=null&&rowSet.getString("s_hvalue").trim().length()>0)
			    	s_hvalue=rowSet.getString("s_hvalue");
			    String vvalue="#";
			    if(rowSet.getString("vvalue")!=null&&rowSet.getString("vvalue").trim().length()>0)
			    	vvalue=rowSet.getString("vvalue");
			    String s_vvalue="#";
			    if(rowSet.getString("s_vvalue")!=null&&rowSet.getString("s_vvalue").trim().length()>0)
			    	s_vvalue=rowSet.getString("s_vvalue");
			    String standard="";
			    if(rowSet.getString("standard")!=null&&rowSet.getString("standard").trim().length()>0)
			    	standard=subZeroAndDot(rowSet.getString("standard"));
			    map.put((hvalue+"|"+s_hvalue+"|"+vvalue+"|"+s_vvalue).toLowerCase(),standard);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return map;
	}
    /**  
     * 使用java正则表达式去掉多余的.与0  zhaoxg add 2013-10-22
     * @param s  
     * @return   
     */  
    public static String subZeroAndDot(String s){   
        if(s.indexOf(".") > 0){   
            s = s.replaceAll("0+?$", "");//去掉多余的0   
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉   
        }   
        return s;   
    }  
	
	public String getMapKey(String hvalue,String s_hvalue,String vvalue,String s_vvalue)
	{
		StringBuffer key=new StringBuffer("");
		if(hvalue==null)
			key.append("#"+"|");
		else
			key.append(hvalue+"|");
		if(s_hvalue==null)
			key.append("#"+"|");
		else
			key.append(s_hvalue+"|");
		if(vvalue==null)
			key.append("#"+"|");
		else
			key.append(vvalue+"|");
		if(s_vvalue==null)
			key.append("#");
		else
			key.append(s_vvalue);
		return key.toString();
	}
	
	
	public void addItemVo(ArrayList hList,String vfactor,String s_vfactor,ArrayList list,String resultItemType,String codesetid)
	{
		
		if(hList.size()==0)
		{
			LazyDynaBean itemBean=new LazyDynaBean();
			if(vfactor!=null)
				itemBean.set("vvalue",vfactor);
			if(s_vfactor!=null)
				itemBean.set("s_vvalue",s_vfactor);			
			if("new".equals(this.opt)&&this.standID.length()==0)
			{
				itemBean.set("value","");
				itemBean.set("viewvalue","");
			}
			else
			{
				String key=getMapKey(null,null,vfactor,s_vfactor).toLowerCase();
				if(this.itemValueMap.get(key)!=null)
				{
					itemBean.set("value",(String)this.itemValueMap.get(key));
					if("C".equalsIgnoreCase(resultItemType))
					{
						itemBean.set("viewvalue",AdminCode.getCodeName(codesetid,(String)this.itemValueMap.get(key)));	
					}
				}
				else
				{
					itemBean.set("value","");
					itemBean.set("viewvalue","");
				}
			}
			list.add(itemBean);
			
		}
		else
		{
			for(int i=0;i<hList.size();i++)
			{
				LazyDynaBean a_LazyDynaBean=(LazyDynaBean)hList.get(i);
				String id=(String)a_LazyDynaBean.get("id");
				String isFactor=(String)a_LazyDynaBean.get("isFactor");
				String childNum=(String)a_LazyDynaBean.get("childNum");
				if("1".equals(isFactor)&&!"0".equals(childNum))
				{
					ArrayList s_factor_list=(ArrayList)a_LazyDynaBean.get("s_factor_list");
					for(int j=0;j<s_factor_list.size();j++)
					{
						LazyDynaBean a_LazyDynaBean2=(LazyDynaBean)s_factor_list.get(j);
						String id2=(String)a_LazyDynaBean2.get("id");
						
						LazyDynaBean itemBean=new LazyDynaBean();
						if(vfactor!=null)
							itemBean.set("vvalue",vfactor);
						if(s_vfactor!=null)
							itemBean.set("s_vvalue",s_vfactor);
						itemBean.set("hvalue",id);
						itemBean.set("s_hvalue",id2);
						if("new".equals(this.opt)&&this.standID.length()==0)
						{
							itemBean.set("value","");
							itemBean.set("viewvalue","");
						}
						else
						{
							String key=getMapKey(id,id2,vfactor,s_vfactor).toLowerCase();
							if(this.itemValueMap.get(key)!=null)
							{
								itemBean.set("value",(String)this.itemValueMap.get(key));
								if("C".equalsIgnoreCase(resultItemType))
								{
									itemBean.set("viewvalue",AdminCode.getCodeName(codesetid,(String)this.itemValueMap.get(key)));	
								}
							}
							else
							{
								itemBean.set("value","");
								itemBean.set("viewvalue","");
							}
						}
						list.add(itemBean);
					}
					
				}
				else
				{
					LazyDynaBean itemBean=new LazyDynaBean();
					if(vfactor!=null)
						itemBean.set("vvalue",vfactor);
					if(s_vfactor!=null)
						itemBean.set("s_vvalue",s_vfactor);
					
					String  a_hvalue="";
					String  a_shvalue="";
					if("1".equals(isFactor))
					{
						itemBean.set("hvalue",id);
						a_hvalue=id;
						a_shvalue=null;
					//	itemBean.set("s_hvalue",null);
					}
					else
					{
					//	itemBean.set("hvalue",null);
						itemBean.set("s_hvalue",id);
						a_hvalue=null;
						a_shvalue=id;
					}
					if("new".equals(this.opt)&&this.standID.length()==0)
					{
						itemBean.set("value","");
						itemBean.set("viewvalue","");
					}
					else
					{
						String key=getMapKey(a_hvalue,a_shvalue,vfactor,s_vfactor).toLowerCase();
						if(this.itemValueMap.get(key)!=null)
						{
							itemBean.set("value",(String)this.itemValueMap.get(key));
							if("C".equalsIgnoreCase(resultItemType))
							{
								itemBean.set("viewvalue",AdminCode.getCodeName(codesetid,(String)this.itemValueMap.get(key)));	
							}
						}
						else
						{
							itemBean.set("value","");
							itemBean.set("viewvalue","");
						}
						
					}
					list.add(itemBean);
				}			
			}
		}
	}
	
	
	public ArrayList gzItemList(GzStandardItemVo vo)
	{
		ArrayList itemList=new ArrayList();
		ArrayList hList=vo.getH_List();
		ArrayList vList=vo.getV_List();
		if(vList.size()==0)
		{
			
			addItemVo(hList,null,null,itemList,vo.getResultItemType(),vo.getCodesetid());
			
		}
		else
		{
			for(int i=0;i<vList.size();i++)
			{
				LazyDynaBean a_LazyDynaBean=(LazyDynaBean)vList.get(i);
				String id=(String)a_LazyDynaBean.get("id");
				String isFactor=(String)a_LazyDynaBean.get("isFactor");
				String childNum=(String)a_LazyDynaBean.get("childNum");
				if("1".equals(isFactor)&&!"0".equals(childNum))
				{
					ArrayList s_factor_list=(ArrayList)a_LazyDynaBean.get("s_factor_list");
					for(int j=0;j<s_factor_list.size();j++)
					{
						LazyDynaBean a_LazyDynaBean2=(LazyDynaBean)s_factor_list.get(j);
						String id2=(String)a_LazyDynaBean2.get("id");
						addItemVo(hList,id,id2,itemList,vo.getResultItemType(),vo.getCodesetid());
					}
					
				}
				else
				{
					if("1".equals(isFactor))
					{
						addItemVo(hList,id,null,itemList,vo.getResultItemType(),vo.getCodesetid());	
					}
					else
					{
						addItemVo(hList,null,id,itemList,vo.getResultItemType(),vo.getCodesetid());
					}
				}
			}
		}
		return itemList;
	}
	
	
	
	
	public int get_bottomColumn_num(ArrayList list)
	{
		int num=0;
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean a_LazyDynaBean=(LazyDynaBean)list.get(i);
			String isFactor=(String)a_LazyDynaBean.get("isFactor");
			String childNum=(String)a_LazyDynaBean.get("childNum");
			if("1".equals(isFactor)&&!"0".equals(childNum))
			{
				num+=Integer.parseInt(childNum);
			}
			else
			{
				num++;
			}
		}
		return num;
	}
	
	
	public ArrayList get_List(String factor,String s_factor,String content)
	{
		ArrayList list=new ArrayList();
		content = PubFunc.keyWord_reback(content);
		if(factor!=null&&s_factor!=null&&factor.trim().length()>0&&s_factor.trim().length()>0)
		{
			String[] temps=content.split(";");
			HashMap h_itemMap=(HashMap)this.itemNameMap.get(factor.toLowerCase());
			HashMap s_h_itemMap=(HashMap)this.itemNameMap.get(s_factor.toLowerCase());
			
			for(int i=0;i<temps.length;i++)
			{
				LazyDynaBean a_LazyDynaBean=new LazyDynaBean();
				String temp=temps[i];
				String id=temp.substring(0,temp.indexOf("["));
				String name="";
				if(h_itemMap.get(id)!=null)
					name=(String)h_itemMap.get(id);				
				a_LazyDynaBean.set("id",id);
				a_LazyDynaBean.set("name",name);
				a_LazyDynaBean.set("isFactor","1");  //1: hfactor  2:s_hfactor
				String s_hfactor_value=temp.substring(temp.indexOf("[")+1,temp.indexOf("]"));
				if(s_hfactor_value.length()==0)
				{
					a_LazyDynaBean.set("childNum","0");
					a_LazyDynaBean.set("s_factor_list",new ArrayList());
				}
				else
				{
					String[] temp2=s_hfactor_value.split(",");
					a_LazyDynaBean.set("childNum",String.valueOf(temp2.length));
					
					ArrayList s_factor_list=new ArrayList();
					for(int j=0;j<temp2.length;j++)
					{
						LazyDynaBean a_LazyDynaBean2=new LazyDynaBean();
						a_LazyDynaBean2.set("id",temp2[j]);
						if(s_h_itemMap.get(temp2[j].toLowerCase())!=null)
							a_LazyDynaBean2.set("name",(String)s_h_itemMap.get(temp2[j].toLowerCase()));
						else if(s_h_itemMap.get(temp2[j].toUpperCase())!=null)
							a_LazyDynaBean2.set("name",(String)s_h_itemMap.get(temp2[j].toUpperCase()));
						else
							a_LazyDynaBean2.set("name","");
						a_LazyDynaBean2.set("isFactor","2");  //1: factor  2:s_factor
						a_LazyDynaBean2.set("childNum","0");
						s_factor_list.add(a_LazyDynaBean2);
					}
					a_LazyDynaBean.set("s_factor_list",s_factor_list);
				}
				
				list.add(a_LazyDynaBean);
			}
		}
		else if(factor!=null&&factor.trim().length()>0)
		{
			String[] temps=content.split(";");
			HashMap h_itemMap=(HashMap)this.itemNameMap.get(factor.toLowerCase());
			for(int i=0;i<temps.length;i++)
			{
				LazyDynaBean a_LazyDynaBean=new LazyDynaBean();
				String temp=temps[i];
				String id="";
				if(temp.indexOf("[")==-1)
					id=temp;
				else
					id=temp.substring(0,temp.indexOf("["));
				
				String name="";
				if(h_itemMap.get(id)!=null)
					name=(String)h_itemMap.get(id);				
				a_LazyDynaBean.set("id",id);
				a_LazyDynaBean.set("name",name);
				a_LazyDynaBean.set("isFactor","1");  //1: hfactor  2:s_hfactor
				a_LazyDynaBean.set("childNum","0");
				a_LazyDynaBean.set("s_factor_list",new ArrayList());
				list.add(a_LazyDynaBean);
			}
		}
		else if(s_factor!=null&&s_factor.trim().length()>0)
		{

			HashMap s_h_itemMap=(HashMap)this.itemNameMap.get(s_factor.toLowerCase());
			String s_factor_value=content.substring(content.indexOf("[")+1,content.indexOf("]"));       
			String[] temp2=s_factor_value.split(",");
            for(int j=0;j<temp2.length;j++)
			{
					LazyDynaBean a_LazyDynaBean=new LazyDynaBean();
					a_LazyDynaBean.set("id",temp2[j]);
					if(s_h_itemMap.get(temp2[j].toLowerCase())!=null)
						a_LazyDynaBean.set("name",(String)s_h_itemMap.get(temp2[j].toLowerCase()));
					else if(s_h_itemMap.get(temp2[j].toUpperCase())!=null)
						a_LazyDynaBean.set("name",(String)s_h_itemMap.get(temp2[j].toUpperCase()));
					else
						a_LazyDynaBean.set("name","");
					a_LazyDynaBean.set("isFactor","2");  //1: factor  2:s_factor
					a_LazyDynaBean.set("childNum","0");
					a_LazyDynaBean.set("s_factor_list",new ArrayList());
					list.add(a_LazyDynaBean);
			}
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 获得横向和纵向指标
	 * @date 2016-2-15
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getItemNameMap() throws GeneralException
	{
		try {
			HashMap map=new HashMap();
			if(hfactor!=null&&hfactor.trim().length()>0)
				map.put(hfactor.toLowerCase(),getItemMap(hfactor));
			if(s_hfactor!=null&&s_hfactor.trim().length()>0)
				map.put(s_hfactor.toLowerCase(),getItemMap(s_hfactor));
			if(vfactor!=null&&vfactor.trim().length()>0)
				map.put(vfactor.toLowerCase(),getItemMap(vfactor));
			if(s_vfactor!=null&&s_vfactor.trim().length()>0)
				map.put(s_vfactor.toLowerCase(),getItemMap(s_vfactor));
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 获得代码名称
	 * @date 2016-2-15
	 * @param itemid
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getItemMap(String itemid) throws GeneralException
	{
		HashMap map=new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			FieldItem fieldItem=DataDictionary.getFieldItem(itemid);
			if(fieldItem!=null)
			{
				String type=fieldItem.getItemtype();
			    String codesetid=fieldItem.getCodesetid();
				if("A".equals(type)&&!"0".equals(codesetid))
				{
					String sql="select * from codeitem where codesetid='"+codesetid+"'";
					if("UN".equals(codesetid)|| "UM".equals(codesetid)|| "@K".equals(codesetid))   // 2008-01-24
						 sql="select * from organization where codesetid='"+codesetid+"'";
					
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
						map.put(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc"));
					}
				}
				else if("N".equals(type)|| "D".equals(type))
				{
					rowSet=dao.search("select * from gz_stand_date where item='"+itemid+"'");
					while(rowSet.next())
					{
						map.put(rowSet.getString("item_id"),rowSet.getString("description"));
					}
				}
			}
		}
		catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return map;
	}
	
	/**
	 * @author lis
	 * @Description: 是否合并单元格
	 * @date 2016-2-15
	 * @param list
	 * @return
	 */
	public boolean getIsSubItem(ArrayList list)
	{
		boolean flag=false;
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)list.get(i);
			String childNum=(String)abean.get("childNum");
			if(!"0".equals(childNum))
			{	flag=true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * @author lis
	 * @Description: 生成标准表HTML 
	 * @date 2016-2-15
	 * @param vo
	 * @return
	 * @throws GeneralException 
	 */
	public String getHtml(GzStandardItemVo vo) throws GeneralException
	{
		try {
			int h_bottomColumn_num=vo.getH_bottomColumn_num();
			String resultItemType=vo.getResultItemType();   // N ; C
			FieldItem resultItem=vo.getResultItem();
			String codesetid=vo.getCodesetid();
			
			StringBuffer html=new StringBuffer("");
			html.append("<table class='standard-table' cellpadding='0' cellspacing='0' >");
			html.append("<tr class='standard-header-ct'>");
			html.append("<td ");
			ArrayList h_List=vo.getH_List();
			ArrayList v_List=vo.getV_List();
			
			boolean is_h2=getIsSubItem(h_List);//是否合并横向
			boolean is_v2=getIsSubItem(v_List);//是否合并纵向
			if(is_h2)
			{
				html.append("  rowspan='2' ");			
			}
			if(is_v2)
			{
				html.append("  colspan='2' ");
			}
			html.append(" class='standard-column-header' ");
			html.append("  >&nbsp; </td>");
			
			
			if(h_List.size()==0)  //如果没选横栏
			{
				String itemid=vo.getItem();
				FieldItem item=DataDictionary.getFieldItem(itemid);
				String itemdesc = "";
				if(item!=null){
					itemdesc = item.getItemdesc();
				}
				html.append("<td class='standard-column-header'");
				
				
				html.append("  nowrap  >&nbsp;"+itemdesc+"&nbsp;</td>");
				h_bottomColumn_num=1;
			}
			else
			{
				//遍历横向指标
				for(int i=0;i<h_List.size();i++)
				{
					LazyDynaBean h_abean=(LazyDynaBean)h_List.get(i);
					String childNum=(String)h_abean.get("childNum");//获得当前指标下子栏目的数量
					String name="";
					if(h_abean.get("name")!=null)
						name=(String)h_abean.get("name").toString();
					if("0".equals(childNum))//没有子栏目
					{
						html.append("<td   class='standard-column-header'");
						
						if(is_h2)
							html.append("  rowspan='2' ");
						else
							html.append(" width='80' ");
						html.append("  nowrap >&nbsp;"+name+"&nbsp;</td>");
					}
					else
					{
						html.append("<td class='standard-column-header'  colspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
					}
				}
			}
			html.append("</tr>");
			//横向有子栏目
			if(is_h2)
			{
				html.append("<tr>");
				//遍历横向指标
				for(int i=0;i<h_List.size();i++)
				{
					LazyDynaBean h_abean=(LazyDynaBean)h_List.get(i);
					ArrayList s_factor_list=(ArrayList)h_abean.get("s_factor_list");
					//遍历当前指标的 子栏目指标
					for(int j=0;j<s_factor_list.size();j++)
					{
						LazyDynaBean s_h_abean=(LazyDynaBean)s_factor_list.get(j);
						String name="";
						if(s_h_abean.get("name")!=null)
							name=(String)s_h_abean.get("name").toString();
						html.append("<td  class='standard-column-header'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 end */
					}
				}
				html.append("</tr>");
			}
			
			int index=0;
			
			if(v_List.size()==0)//没有纵向指标
			{
				String itemid=vo.getItem();
				FieldItem item=DataDictionary.getFieldItem(itemid);
				String itemdesc = "";
				if(item!=null){
					itemdesc = item.getItemdesc();
				}
				html.append("<tr><td class='standard-column-header'");	
				html.append("  nowrap  >&nbsp;"+itemdesc+"&nbsp;</td>");
				for(int j=0;j<h_bottomColumn_num;j++)
				{
					html.append("<td class='standard-grid-cell' valign='middle'  >&nbsp;");
					if("N".equals(resultItemType))
					{
	                	 String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
	                     html.append(value);
					}
					else if("C".equals(resultItemType))
					{
						String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
						html.append(viewvalue);
					}
					html.append("&nbsp;</td>");
					index++;
				}
				html.append("</tr>");
				
			}
			else
			{
				//遍历纵向指标
				for(int i=0;i<v_List.size();i++)
				{
					LazyDynaBean v_abean=(LazyDynaBean)v_List.get(i);
					String name="";
					if(v_abean.get("name")!=null)
						name=(String)v_abean.get("name").toString();
					String childNum=(String)v_abean.get("childNum");
					if(!is_v2||("0".equals(childNum)&&is_v2))//当前纵向指标没有子栏目
					{
						html.append("<tr><td class='standard-column-header'");
						/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 end */
						if(is_v2&& "0".equals(childNum))
							html.append(" colspan='2' ");
						html.append("  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						//遍历当前行的值
						for(int j=0;j<h_bottomColumn_num;j++)
						{
							html.append("<td class='standard-grid-cell' valign='middle'  >&nbsp;");
							if("N".equals(resultItemType))
							{
	                        	 String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
	                             html.append(value);
							}
							else if("C".equals(resultItemType))
							{
								String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
								html.append(viewvalue);
							}
							html.append("&nbsp;</td>");
							index++;
						}
						html.append("</tr>");
					}
					else
					{
						//当前纵向指标有子栏目
						html.append("<tr ><td  class='standard-column-header'  rowspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						
						ArrayList s_factor_list=(ArrayList)v_abean.get("s_factor_list");
						//遍历当前指标下的子栏目指标
						for(int j=0;j<s_factor_list.size();j++)
						{
							LazyDynaBean s_v_abean=(LazyDynaBean)s_factor_list.get(j);
							String a_name="";
							if(s_v_abean.get("name")!=null)
								a_name=(String)s_v_abean.get("name").toString();
							if(j!=0)
							{
								html.append("<tr >");
							}
							html.append("<td  class='standard-column-header'  nowrap   >&nbsp;&nbsp;"+a_name+"&nbsp;&nbsp;</td>");
							//遍历当前行的值
							for(int e=0;e<h_bottomColumn_num;e++)
							{
	                            html.append("<td class='standard-grid-cell' width='80'  valign='middle' >&nbsp;");
	                            
	                            if("N".equals(resultItemType))
								{
	                            	 String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
	                                 html.append(value);
								}
								else if("C".equals(resultItemType))
								{
									String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
									html.append(viewvalue);
								}
	                           
								html.append("&nbsp;</td>");
								
								index++;
							}
							html.append("</tr>");
						}
					}
				}
			
			}
		
			html.append("</table>");
			
			return html.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public String getHcontent() {
		return hcontent;
	}

	public void setHcontent(String hcontent) {
		this.hcontent = hcontent;
	}

	public String getHfactor() {
		return hfactor;
	}

	public void setHfactor(String hfactor) {
		this.hfactor = hfactor;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getS_hfactor() {
		return s_hfactor;
	}

	public void setS_hfactor(String s_hfactor) {
		this.s_hfactor = s_hfactor;
	}

	public String getS_vfactor() {
		return s_vfactor;
	}

	public void setS_vfactor(String s_vfactor) {
		this.s_vfactor = s_vfactor;
	}

	
	public String getVcontent() {
		return vcontent;
	}

	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
	}

	public String getVfactor() {
		return vfactor;
	}

	public void setVfactor(String vfactor) {
		this.vfactor = vfactor;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}   
	public void setUserView(UserView userView)
	{
		this.userView=userView;
	}
}
