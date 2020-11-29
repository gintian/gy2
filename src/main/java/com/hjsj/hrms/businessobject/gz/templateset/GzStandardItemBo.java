package com.hjsj.hrms.businessobject.gz.templateset;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
	
	
    public GzStandardItemBo(Connection con)
    {
    	try
    	{
    		this.conn=con;
    	//	Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    	//	this.conn = DriverManager.getConnection( "jdbc:sqlserver://192.192.100.246:1433;databaseName=zl", "sa","");  		
   /* 		this.conn=con;
    		this.hfactor="A0405";
    		this.s_hfactor="A1905";
    		this.hcontent="1[1];2[1,2];3[2]";
    		//this.vfactor="C0105";
    		this.vfactor="A0114";
    		this.s_vfactor=null;
    	//	this.s_vfactor="A0107";
    	//	this.vcontent="01[1,2];02[];03[1,2]";
    		this.vcontent="11[];12[]";
    		this.item="C5805";
    		
    		int dddd=3343;
    		System.out.println(dddd+"fff");
    	//	this.item="A0157";*/
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
	
	public GzStandardItemBo(Connection con,String hfactor,String s_hfactor,String hcontent,String vfactor,String s_vfactor,String vcontent,String item,String standID,String opt,String pkg_id)
	{
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
		
	}
	
	
	
	public synchronized int   getGzStandId() throws GeneralException{
		int id=0;
		try
		{
			String strsql = "select max(id) from gz_stand";
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search(strsql);
			if(rowSet.next())
				id=rowSet.getInt(1);
			id++;
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return id;
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
	
	
	public boolean isPackageActive(String pkg_id)
	{
		boolean isActive=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select status from gz_stand_pkg where pkg_id="+pkg_id);
			if(rowSet.next())
			{
				if("1".equals(rowSet.getString("status")))
					isActive=true;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isActive;
	}
	
	public void saveSalaryStandard(GzStandardItemVo vo,String pkg_id,String gzStandardName,String opt,String standardID) throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo standVo=new RecordVo("gz_stand");
			RecordVo standHistoryVo=new RecordVo("gz_stand_history");
			boolean isActive=isPackageActive(pkg_id);  //当前历史沿革是否为启用
			
			int id=0;
			if("new".equalsIgnoreCase(opt))
			{
				id=getGzStandId();
			}
			else if("edit".equalsIgnoreCase(opt))
			{
				id=Integer.parseInt(standardID);
			}
			standVo.setInt("id",id);
			if("new".equalsIgnoreCase(opt))
				standVo.setString("name",gzStandardName);
		//	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String unit_type="";  //sysbo.getValue(Sys_Oth_Parameter.UNITTYPE);
			standVo.setString("unit_type",unit_type);
			standVo.setInt("flag",getFlag(pkg_id));
			standVo.setString("hfactor",vo.getHfactor());
			standVo.setString("hcontent",vo.getHcontent());
			standVo.setString("vfactor",vo.getVfactor());
			standVo.setString("vcontent",vo.getVcontent());
			standVo.setString("s_hfactor",vo.getS_hfactor());
			standVo.setString("s_vfactor",vo.getS_vfactor());
			standVo.setString("item",vo.getItem());
			if("new".equalsIgnoreCase(opt))
				dao.addValueObject(standVo);
			else if("edit".equalsIgnoreCase(opt))
			{
				if(isActive)
					dao.updateValueObject(standVo);
			}
			
			standHistoryVo.setInt("id",id);
			standHistoryVo.setInt("pkg_id",Integer.parseInt(pkg_id));
			if("new".equalsIgnoreCase(opt))
				standHistoryVo.setString("name",gzStandardName);
			standHistoryVo.setString("hfactor",vo.getHfactor());
			standHistoryVo.setString("hcontent",vo.getHcontent());
			standHistoryVo.setString("vfactor",vo.getVfactor());
			standHistoryVo.setString("vcontent",vo.getVcontent());
			standHistoryVo.setString("s_hfactor",vo.getS_hfactor());
			standHistoryVo.setString("s_vfactor",vo.getS_vfactor());
			standHistoryVo.setString("item",vo.getItem());
			Calendar cd=Calendar.getInstance();
			standHistoryVo.setDate("createtime",cd.getTime());
			if("new".equalsIgnoreCase(opt))
			{
	     		String value="";
	     		boolean noManage = false;
	     		if(this.userView.isSuper_admin())
	     			value="UN";
	     		else{
	         		String unit_id = this.userView.getUnit_id();
	        		if(unit_id!=null&&unit_id.trim().length()>2)
	        		{
		         		if("UN`".equalsIgnoreCase(unit_id))
		        		{
		         			value="UN";
		        		}
		         		else 
		        		{
		        			String arr[] = unit_id.split("`");
		    	    		value = arr[0].substring(2);
		         		}
		        	}
	        		else
	         		{
	        			if(this.userView.getManagePrivCode()==null|| "".equals(this.userView.getManagePrivCode()))
	        				noManage=true;
	        			if(this.userView.getManagePrivCode()!=null&&!"".equals(this.userView.getManagePrivCode()))
	        			{
	        		     	String codevalue = (this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue()))?"UN":this.userView.getManagePrivCodeValue();
	        		    	value = codevalue;
	        			}
	        		}
	     		}
	     		if(!noManage)//如果没有管理范围和操作单位，这个两个字段不赋值
	     		{
	            	if(standHistoryVo.hasAttribute("b0110"))
	            	{
	        	    	standHistoryVo.setString("b0110", "UN".equalsIgnoreCase(value)?null:(","+value));
	        		}
	         		if(standHistoryVo.hasAttribute("createorg"))
	        		{
	         			standHistoryVo.setString("createorg", value);
	        		}
	     		}
			}
			if("new".equalsIgnoreCase(opt))
				dao.addValueObject(standHistoryVo);
			else if("edit".equalsIgnoreCase(opt))
				dao.updateValueObject(standHistoryVo);
			
			
			ArrayList gzItemList=vo.getGzItemList();
			ArrayList itemList=new ArrayList();
			ArrayList itemHistoryList=new ArrayList();
			
			if("edit".equalsIgnoreCase(opt))
			{
				if(isActive)
				{
					dao.delete("delete from gz_item where id="+id,new ArrayList());
				}
				dao.delete("delete from gz_item_history where pkg_id="+pkg_id+" and id="+id,new ArrayList());
			}
			ArrayList _templist = new ArrayList();
			for(int i=0;i<gzItemList.size();i++)
			{
				LazyDynaBean itemBean=(LazyDynaBean)gzItemList.get(i);
//				RecordVo gz_item_vo=new RecordVo("gz_item");
//				RecordVo gz_item_history_vo=new RecordVo("gz_item_history");
//				gz_item_vo.setInt("id",id);
//				gz_item_vo.setString("hvalue",(String)itemBean.get("hvalue"));
//				gz_item_vo.setString("s_hvalue",(String)itemBean.get("s_hvalue"));
//				gz_item_vo.setString("vvalue",(String)itemBean.get("vvalue"));
//				gz_item_vo.setString("s_vvalue",(String)itemBean.get("s_vvalue"));
//				gz_item_vo.setString("standard",(String)itemBean.get("value"));
//				itemList.add(gz_item_vo);
//				gz_item_history_vo.setInt("id",id);
//				gz_item_history_vo.setInt("pkg_id",Integer.parseInt(pkg_id));
//				gz_item_history_vo.setString("hvalue",(String)itemBean.get("hvalue"));
//				gz_item_history_vo.setString("s_hvalue",(String)itemBean.get("s_hvalue"));
//				gz_item_history_vo.setString("vvalue",(String)itemBean.get("vvalue"));
//				gz_item_history_vo.setString("s_vvalue",(String)itemBean.get("s_vvalue"));
//				gz_item_history_vo.setString("standard",(String)itemBean.get("value"));
//				itemHistoryList.add(gz_item_history_vo);

				ArrayList templist = new ArrayList();
				templist.add((String)itemBean.get("hvalue"));
				templist.add((String)itemBean.get("s_hvalue"));
				templist.add((String)itemBean.get("vvalue"));
				templist.add((String)itemBean.get("s_vvalue"));
				templist.add((String)itemBean.get("value"));
				_templist.add(templist);
			}
			int num = _templist.size()/1000;//批量增加，提速  zhaoxg add 2013-11-4
			if(_templist.size()%1000!=0){
				num++;
			}
			String sql="insert into gz_item (id,hvalue,s_hvalue,vvalue,s_vvalue,standard) values ("+id+",?,?,?,?,?)";
			String _sql="insert into gz_item_history (id,pkg_id,hvalue,s_hvalue,vvalue,s_vvalue,standard) values ("+id+","+pkg_id+",?,?,?,?,?)";
			if("new".equalsIgnoreCase(opt))
			{
				ArrayList templist = null;
				for(int n=0;n<num;n++){
					templist = new ArrayList();
					for(int x=n*1000;x<(n+1)*1000;x++){
						if(x>=_templist.size()){
							break;
						}
						templist.add(_templist.get(x));
					}
					dao.batchInsert(sql, templist);
					dao.batchInsert(_sql, templist);
				}
//				dao.addValueObject(itemList);
//				dao.addValueObject(itemHistoryList);
			}
			else if("edit".equalsIgnoreCase(opt))
			{
				ArrayList templist = null;
				for(int n=0;n<num;n++){
					templist = new ArrayList();
					for(int x=n*1000;x<(n+1)*1000;x++){
						if(x>=_templist.size()){
							break;
						}
						templist.add(_templist.get(x));
					}
					if(isActive){
						dao.batchInsert(sql, templist);
					}
					dao.batchInsert(_sql, templist);
				}
//				if(isActive)
//					dao.addValueObject(itemList);
//				dao.addValueObject(itemHistoryList);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	
	/**
	 * 
	 * @param optType  //0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
	 * @param content
	 * @return
	 */
	public String updateColumnContent(String optType,String content,String parentid,String[] items ,String codeid)
	{
		StringBuffer content_str=new StringBuffer("");
		String[] temps=content.split(";");
		if("0".equals(optType)|| "2".equals(optType))
		{
				
				for(int j=0;j<items.length;j++)
				{
				    String itemid=items[j];
				    boolean isExist=false;
				    String tempContent="";
					for(int i=0;i<temps.length;i++)
					{
						String temp="";
						if(temps[i].indexOf("[")==-1)
							temp=temps[i];
						else
							temp=temps[i].substring(0,temps[i].indexOf("["));
						
						if(itemid.equals(temp))
						{
							isExist=true;
							tempContent=temps[i];
							break;
						}
					}
					if(isExist)
					{
						content_str.append(";"+tempContent);
					}
					else
						content_str.append(";"+itemid+"[]");
				}
			
		}
		else
		{
			ArrayList<CodeItem> codeItem=AdminCode.getCodeItemList(codeid);
			for(int i=0;i<temps.length;i++)
			{
				ArrayList list = new ArrayList();
				String temp=temps[i].substring(0,temps[i].indexOf("["));
				if(temp.equals(parentid)||temp.length()==0)
				{
					content_str.append(";"+temp+"[");
					StringBuffer str=new StringBuffer("");
					boolean is_=false;
					
					if(codeItem.size() == 0) {//可能传过来的不是代码型的，前台是按照顺序进行插入的，
						Arrays.sort(items);
						for(int p = 0; p < items.length; p++) {
							if("#".equals(items[p])|| "＃".equals(items[p]))
								is_=true;
							str.append("," + items[p]);
						}
					}else {
						//进行排序 zhanghua 2017-3-10
						HashMap map = new HashMap();
						for(int j=0;j<items.length;j++)
						{
							if("#".equals(items[j])|| "＃".equals(items[j]))
								is_=true;
							map.put( items[j].toLowerCase(),items[j].toLowerCase());
						}
						String value="";
						for(CodeItem code:codeItem){//参照 AdminCode.getCodeItemList 获取的代码顺序进行排序
							value=(String)map.get(code.getCodeitem().toLowerCase());
							if(!StringUtils.isBlank(value))
								str.append(","+value);
						}
					}
					//排序结束
					if(!is_&&items.length>0)
						content_str.append(str.substring(1));	
					content_str.append("]");
				}
				else
				{
					content_str.append(";"+temps[i]);
				}
			}
			
		}
		
		return content_str.substring(1);
	}
	
	
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
	
	
	
	public void  init()
	{
		this.itemNameMap=getItemNameMap();
	}
	
	
	public HashMap getGzItemMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_item_history where id='"+this.standID+"' and pkg_id='"+this.pkg_id+"'");
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
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
			//	System.out.println(this.standID+"  "+temp);
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
	
	
	
	public HashMap getItemNameMap()
	{
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
	}
	
	
	
	public HashMap getItemMap(String itemid)
	{
		HashMap map=new HashMap();
	    
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
					
					RowSet rowSet=dao.search(sql);
					while(rowSet.next())
					{
						map.put(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc"));
					}
					rowSet.close();
				}
				else if("N".equals(type)|| "D".equals(type))
				{
					RowSet rowSet=dao.search("select * from gz_stand_date where item='"+itemid+"'");
					while(rowSet.next())
					{
						map.put(rowSet.getString("item_id"),rowSet.getString("description"));
					}
					rowSet.close();
				}
			}
		}
		catch (Exception e) {
				e.printStackTrace();
		} 		
		return map;
	}
	
	
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
	 * 取得当前已选指标列表
	 * @param optType   0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
	 * @param gzStandardItemVo
	 * @return
	 */
	public ArrayList getSelectItemList(String optType,GzStandardItemVo gzStandardItemVo,String parentItemid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList factorList=new ArrayList();
			String factor="";
			String factorType="";
			String codesetid="";
			if("0".equals(optType))
			{
				factorList=gzStandardItemVo.getH_List();
				factor=gzStandardItemVo.getHfactor();
			}
			else if("1".equals(optType))
			{
				factorList=gzStandardItemVo.getH_List();
				factor=gzStandardItemVo.getS_hfactor();
			}
			else  if("2".equals(optType))
			{
				factor=gzStandardItemVo.getVfactor();
				factorList=gzStandardItemVo.getV_List();
			}
			else  if("3".equals(optType))
			{
				factor=gzStandardItemVo.getS_vfactor();
				factorList=gzStandardItemVo.getV_List();			
			}
			if(factor!=null&&factor.length()>0)
			{
			
				FieldItem fieldItem=DataDictionary.getFieldItem(factor);
				factorType=fieldItem.getItemtype();
				String temp="";
				String group = "";//gz_stand_date表中没a0000，所有按照item_id排序  zhaoxg add 2015-11-4
				StringBuffer sql=new StringBuffer("select * from ");
				if("A".equals(factorType)&&!"0".equals(fieldItem.getCodesetid()))
				{
					if("UN".equals(fieldItem.getCodesetid())|| "UM".equals(fieldItem.getCodesetid())|| "@K".equals(fieldItem.getCodesetid()))// 单位、部门、岗位不在codeitem表内，zhaoxg 2013-9-4 bug0038219
					{
						codesetid=fieldItem.getCodesetid();
						temp="codeitemid";
						sql.append(" organization  where codesetid='"+codesetid+"' ");
					}else{
						codesetid=fieldItem.getCodesetid();
						temp="codeitemid";
						sql.append(" codeitem  where codesetid='"+codesetid+"' ");
					}
					group = "a0000";
				}
				else
				{
					temp="item_id";
					sql.append(" gz_stand_date where item='"+factor+"' ");
					group = "item_id";
				}
				StringBuffer whl=new StringBuffer("");
				
				if("0".equals(optType)|| "2".equals(optType))
				{
					for(int i=0;i<factorList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)factorList.get(i);
						String id=(String)abean.get("id");
						whl.append(" or "+temp+"='"+id+"' ");
					}
				}
				else
				{
					for(int i=0;i<factorList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)factorList.get(i);
						String id=(String)abean.get("id");
						if(parentItemid.length()>0)
						{
							if(id.equals(parentItemid))
							{
								ArrayList s_factor_list=(ArrayList)abean.get("s_factor_list");
								for(int j=0;j<s_factor_list.size();j++)
								{
									LazyDynaBean a_bean=(LazyDynaBean)s_factor_list.get(j);
									String a_id=(String)a_bean.get("id");
									whl.append(" or "+temp+"='"+a_id+"' ");
								}
								break;
							}
						}
						else
						{
							whl.append(" or "+temp+"='"+id+"' ");
							
						}
						
					}
				}
				if(whl!=null&&whl.length()>0)
			     	sql.append(" and ( "+whl.substring(3)+") order by cast("+group+" as int)");
				else 
					sql.append(" and 1=2 ");
				RowSet rowSet=dao.search(sql.toString());
				while(rowSet.next())
				{
					if("A".equals(factorType)&&!"0".equals(fieldItem.getCodesetid()))
					{
						list.add(new CommonData(rowSet.getString("codeitemid"),rowSet.getString("codeitemdesc")));
					}
					else
					{
						list.add(new CommonData(rowSet.getString("item_id"),rowSet.getString("description")));
						
					}
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	//////////////////////************************   生成HTML      **************************////////////////////////////////////////
	
	
	
	
	
	public String getHtml(GzStandardItemVo vo)
	{
		int h_bottomColumn_num=vo.getH_bottomColumn_num();
		String resultItemType=vo.getResultItemType();   // N ; C
		FieldItem resultItem=vo.getResultItem();
		String codesetid=vo.getCodesetid();
		
		StringBuffer html=new StringBuffer("");
		if(this.isLocked)
			html.append("<table align='left'  id='tbl' class='common_border_color ListTable' border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
		else
			html.append("<table align='center'   class='ListTable common_border_color' >");
		html.append("<tr><td     ");
		ArrayList h_List=vo.getH_List();
		ArrayList v_List=vo.getV_List();
		
	//	LazyDynaBean h_abean0=(LazyDynaBean)h_List.get(0);
	//	LazyDynaBean v_abean0=(LazyDynaBean)v_List.get(0);
		boolean is_h2=getIsSubItem(h_List);
		boolean is_v2=getIsSubItem(v_List);
		if(is_h2)
		{
			html.append("  rowspan='2' ");			
		}
		if(is_v2)
		{
			html.append("  colspan='2' ");
		}
		if(this.isLocked)
			html.append(" class='cell_locked2  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;border-right: 1px solid;\"");
		else
			html.append(" class='RecordRow_self  common_background_color common_border_color' ");
		html.append("  >&nbsp; </td>");
		
		
		if(h_List.size()==0)  //如果没选横栏
		{
			String itemid=vo.getItem();
			FieldItem item=DataDictionary.getFieldItem(itemid);
			String itemdesc = "";
			if(item!=null){
				itemdesc = item.getItemdesc();
			}
			if(this.isLocked)
				/* 薪资标准-薪资标准表-编辑 上边框表格有重合的边框 xiaoyun 2014-9-22 start */
				//html.append("<td  width='80' class='header_locked  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
				html.append("<td  width='80' class='header_locked  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-right: 1px solid;\"  bgcolor='#f4f7f7' ");
				/* 薪资标准-薪资标准表-编辑 上边框表格有重合的边框 xiaoyun 2014-9-22 end */
			else
				html.append("<td  width='80' class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
			
			
			html.append("  nowrap  >&nbsp;"+itemdesc+"&nbsp;</td>");
			h_bottomColumn_num=1;
		}
		else
		{
			for(int i=0;i<h_List.size();i++)
			{
				LazyDynaBean h_abean=(LazyDynaBean)h_List.get(i);
				String childNum=(String)h_abean.get("childNum");
				String name="";
				if(h_abean.get("name")!=null)
					name=(String)h_abean.get("name").toString();
				if("0".equals(childNum))
				{
					if(this.isLocked)
						/* 薪资-薪资标准-编辑样式处理(去掉重合的边线) xiaoyun 2014-9-10 start */
						html.append("<td   class='header_locked_l  common_background_color common_border_color'style=\"border-left: none;border-top: none;border-right: 1px solid;\"  bgcolor='#f4f7f7' ");
						/* 薪资-薪资标准-编辑样式处理(去掉重合的边线) xiaoyun 2014-9-10 end */
						//html.append("<td   class='header_locked_l  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 start */
					else{
						html.append("<td   class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
					}
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 end */
						
					
					if(is_h2)
						html.append("  rowspan='2' ");
					else
						html.append(" width='80' ");
					html.append("  nowrap >&nbsp;"+name+"&nbsp;</td>");
				}
				else
				{
					if(this.isLocked)
						/* 薪资标准-薪资标准表-编辑 左边表格有重合的边框 xiaoyun 2014-9-22 start */
						//html.append("<td   class='header_locked  common_background_color common_border_color' bgcolor='#f4f7f7'  colspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						html.append("<td   class='header_locked  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-right: 1px solid;\" bgcolor='#f4f7f7'  colspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						/* 薪资标准-薪资标准表-编辑 左边表格有重合的边框 xiaoyun 2014-9-22 end */
					else
						html.append("<td  class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7'  colspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
				}
			}
		}
		html.append("</tr>");
		if(is_h2)
		{
			html.append("<tr>");
			for(int i=0;i<h_List.size();i++)
			{
				LazyDynaBean h_abean=(LazyDynaBean)h_List.get(i);
				ArrayList s_factor_list=(ArrayList)h_abean.get("s_factor_list");
				for(int j=0;j<s_factor_list.size();j++)
				{
					LazyDynaBean s_h_abean=(LazyDynaBean)s_factor_list.get(j);
					String name="";
					if(s_h_abean.get("name")!=null)
						name=(String)s_h_abean.get("name").toString();
					if(this.isLocked)
						/* 薪资标准-薪资标准表-编辑 页面调整 xiaoyun 2014-9-22 start */
						//html.append("<td   class='header_locked_l  common_background_color common_border_color'  width='80' bgcolor='#f4f7f7'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						html.append("<td   class='header_locked_l  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;border-right: 1px solid;\" width='80' bgcolor='#f4f7f7'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						/* 薪资标准-薪资标准表-编辑 页面调整 xiaoyun 2014-9-22 end */
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 start */
					else{
						//html.append("<td  class='RecordRow_self_lrt  common_background_color common_border_color' width='80' bgcolor='#f4f7f7'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
						html.append("<td  class='RecordRow_self  common_background_color common_border_color' width='80' bgcolor='#f4f7f7'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
					}
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 end */
				
				}
			}
			html.append("</tr>");
		}
		
		int index=0;
		
		if(v_List.size()==0)
		{
			String itemid=vo.getItem();
			FieldItem item=DataDictionary.getFieldItem(itemid);
			String itemdesc = "";
			if(item!=null){
				itemdesc = item.getItemdesc();
			}
			if(this.isLocked)
				html.append("<tr><td  class='cell_locked  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;border-right: 1px solid;\"  bgcolor='#f4f7f7' ");			
			else
				html.append("<tr><td  class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7' ");	
			html.append("  nowrap  >&nbsp;"+itemdesc+"&nbsp;</td>");
			for(int j=0;j<h_bottomColumn_num;j++)
			{
				html.append("<td class='RecordRow' width='80' valign='middle'  >&nbsp;");
				if("N".equals(resultItemType))
				{
					String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
					if(resultItem==null)
						continue;
					int decimalwidth=resultItem.getDecimalwidth();
					
					html.append("<input type='text' size='10' name='gzItemList["+index+"].value' onBlur='checkNum(this,"+decimalwidth+")'  value='"+value+"'  onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />");
				}
				else if("C".equals(resultItemType))
				{
					String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
					String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
					
					html.append("<input type='hidden' size='10' name='gzItemList["+index+"].value'   value='"+value+"' />");
					/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 start */
					//xiegh 20170515 bug13952 add：if (event.keyCode==8) delete_text(this,event); 屏蔽backspace键回退功能 ，并清除text框中的内容
					html.append("<input type='text' size='10' name='gzItemList["+index+"].viewvalue'  readonly='true' style='margin-top:-10px;valign:top;' value='"+viewvalue+"'   onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");' onkeydown='if (event.keyCode==8) delete_text(this,event);if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   />");
					//html.append("<input type='text' size='10' name='gzItemList["+index+"].viewvalue'  readonly='true'    value='"+viewvalue+"'   onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   />");
					/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 end */		
				//	html.append("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");'/>");
				}
				html.append("&nbsp;</td>");
				index++;
			}
			html.append("</tr>");
			
		}
		else
		{
			for(int i=0;i<v_List.size();i++)
			{
				LazyDynaBean v_abean=(LazyDynaBean)v_List.get(i);
				String name="";
				if(v_abean.get("name")!=null)
					name=(String)v_abean.get("name").toString();
				String childNum=(String)v_abean.get("childNum");
				if(!is_v2||("0".equals(childNum)&&is_v2))
				{
					if(this.isLocked)
						/* 薪资-薪资标准-薪资标准表样式处理(去掉重合的右边线) xiaoyun 2014-9-10 start */
						//html.append("<tr><td class='cell_locked_t  common_background_color common_border_color' bgcolor='#f4f7f7' ");
						html.append("<tr><td class='cell_locked_t  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;\"  bgcolor='#f4f7f7' ");
						/* 薪资-薪资标准-薪资标准表样式处理(去掉重合的右边线) xiaoyun 2014-9-10 end */
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 start */
					else{
						//html.append("<tr><td class='RecordRow_self_lbt  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
						html.append("<tr><td class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7' ");
					}
					/* 标识：4210 薪资发放-计算公式页面处理 xiaoyun 2014-9-5 end */
					if(is_v2&& "0".equals(childNum))
						html.append(" colspan='2' ");
					html.append("  nowrap  >&nbsp;"+name+"&nbsp;</td>");
					for(int j=0;j<h_bottomColumn_num;j++)
					{
						html.append("<td class='RecordRow' width='80' style=\"border-left: none;border-top: none;\"  valign='middle'  >&nbsp;");
						if("N".equals(resultItemType))
						{
							String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
							if(resultItem==null)
								continue;
							int decimalwidth=resultItem.getDecimalwidth();
							/* 薪资标准-薪资标准表-编辑 页面调整(输入框位置居中问题) xiaoyun 2014-9-10 start */
							html.append("<input type='text' size='10' style='margin-top:-10px;vertical-align:top;' name='gzItemList["+index+"].value' onBlur='checkNum(this,"+decimalwidth+")'  value='"+value+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />");
							/* 薪资标准-薪资标准表-编辑 页面调整(输入框位置居中问题) xiaoyun 2014-9-10 end */
						}
						else if("C".equals(resultItemType))
						{
							String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
							String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
							/* 薪资标准-薪资标准表-编辑 页面调整(输入框位置居中问题) xiaoyun 2014-9-10 start */
							html.append("<input type='hidden' size='10' style='margin-top:-10px;vertical-align:top;' name='gzItemList["+index+"].value'   value='"+value+"' />");
							html.append("<input type='text' size='10' style='margin-top:-10px;vertical-align:top;' name='gzItemList["+index+"].viewvalue'  readonly='true'    value='"+viewvalue+"'   onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");' onkeydown='if (event.keyCode==8) delete_text(this,event);if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'  />");
							/* 薪资标准-薪资标准表-编辑 页面调整(输入框位置居中问题) xiaoyun 2014-9-10 end */
						//	html.append("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");'/>");
						}
						html.append("&nbsp;</td>");
						index++;
					}
					html.append("</tr>");
				}
				else
				{
					
					if(this.isLocked)
						html.append("<tr ><td  class='cell_locked  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;\"  bgcolor='#f4f7f7' rowspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
					else
						html.append("<tr ><td  class='RecordRow_self  common_background_color common_border_color'  bgcolor='#f4f7f7'  rowspan='"+childNum+"'  nowrap  >&nbsp;"+name+"&nbsp;</td>");
					
					ArrayList s_factor_list=(ArrayList)v_abean.get("s_factor_list");
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
						if(this.isLocked)
							/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-9-22 start */
							//html.append("<td  class='cell_locked_t  common_background_color common_border_color' bgcolor='#f4f7f7'  nowrap   >&nbsp;&nbsp;"+a_name+"&nbsp;&nbsp;</td>");
							html.append("<td  class='cell_locked_t  common_background_color common_border_color' style=\"border-left: none;border-top: none;border-bottom: 1px solid;\" bgcolor='#f4f7f7'  nowrap   >&nbsp;&nbsp;"+a_name+"&nbsp;&nbsp;</td>");
						    /* 薪资标准-薪资标准表-编辑 xiaoyun 2014-9-22 end */
						else
							html.append("<td  class='RecordRow_self_lbt  common_background_color common_border_color'  bgcolor='#f4f7f7'  nowrap   >&nbsp;&nbsp;"+a_name+"&nbsp;&nbsp;</td>");
						for(int e=0;e<h_bottomColumn_num;e++)
						{
						    if(this.isLocked)
						        html.append("<td class='RecordRow' width='80' style=\"border-left: none;border-top: none;\" valign='middle' >&nbsp;");
                            else
                                html.append("<td class='RecordRow' width='80' style=\"border-left: none;border-top: none;\"  valign='middle' >&nbsp;");
							if("N".equals(resultItemType))
							{
								String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
								int decimalwidth=resultItem.getDecimalwidth();
								/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 start */
								//html.append("<input type='text' size='10'  name='gzItemList["+index+"].value'  onBlur='checkNum(this,"+decimalwidth+")'   value='"+value+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />");
								html.append("<input type='text' size='10' style='margin-top:-28px;valign:top;' name='gzItemList["+index+"].value'  onBlur='checkNum(this,"+decimalwidth+")'   value='"+value+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />");
								/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 end */
							}
							else if("C".equals(resultItemType))
							{
								String value=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("value");
								String viewvalue=(String)((LazyDynaBean)vo.getGzItemList().get(index)).get("viewvalue");
								html.append("<input type='hidden' size='10' name='gzItemList["+index+"].value'   value='"+value+"' />");
								/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 start */								
								html.append("<input type='text' size='10' name='gzItemList["+index+"].viewvalue' readonly='true' style='margin-top:-10px;vertical-align:top;' value='"+viewvalue+"'  onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");' onkeydown='if (event.keyCode==8) delete_text(this,event);if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />"); 
								//html.append("<input type='text' size='10' name='gzItemList["+index+"].viewvalue'  readonly='true'    value='"+viewvalue+"'  onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' />");
								/* 薪资标准-薪资标准表-编辑 xiaoyun 2014-10-16 end */
							//	html.append("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"gzItemList["+index+"].viewvalue\");'/>");
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
	}
	
	
	
    //////////////////////////****************      END     **********************////////////////////////////////////////
	
	
	
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
