package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.transaction.mobileapp.utils.searchcode.SearchCodeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchCodeItemList.java</p>
 * <p>Description>:查询代码树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-11-7 下午04:11:59</p>
 * <p>@version: 1.0</p>
 * <p>@author:chenmq
 */
public class SearchCodeItemList extends IBusiness {

	
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap map=this.getFormHM();		
		try{
			UserView userView = this.getUserView();
			Connection conn = this.getFrameconn();
			SearchCodeBo searchCodeBo = new SearchCodeBo(conn, userView);

			String codesetid=(String)map.get("codesetid");	
			String itemid=(String)map.get("itemid");	   
			// 是查询第一级代码项,还是查询当前选中代码项的下级代码项
			String flag=(String)map.get("flag");
			flag = flag==null||flag.length()==0?"0":flag;
		   if("manager".equals(codesetid)){//扩展，展示业务用户组
			   this.createManagerList(map,searchCodeBo,itemid,flag);
		   }else{//基本代码树
			   this.createList(map,codesetid,itemid,flag);
		   }
		  
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);	
		}		

	}
	
	/**
	 * 
	 * @Title: createManagerList   
	 * @Description:业务用户组处理  
	 * @param map
	 * @param searchCodeBo
	 * @param groupID
	 * @param flag
	 * @return void    
	 * @throws GeneralException
	 */
	private void createManagerList(HashMap map, SearchCodeBo searchCodeBo, String userName, String flag) throws GeneralException {
		 // 输入的代码名称
		userName = userName==null||userName.length()==0?"":userName;
		// 业务用户展示控制：0不控制显示数据库中username字段、1走fullname->a0100->username显示
	   String privflag = (String)map.get("privflag");
	   privflag = privflag==null||privflag.length()==0?"":privflag;
	   List list = searchCodeBo.searchManagerInfoList(userName,flag,privflag);
	   map.put("codelist", list);
	}
	
	/**
	 * 
	 * @Title: createList   
	 * @Description: 代码树处理   
	 * @param map
	 * @param codesetid
	 * @param itemid
	 * @param flag
	 * @return void    
	 * @throws GeneralException
	 */
	private void createList(HashMap map, String codesetid, String itemid, String flag) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			/**
			 * 代码项层级,可选
			 */
			// String level=(String)map.get("level");

			/**
			 * 机构编码是否权限控制
			 * 0不控制,1按管理范围控制,2走userView.getUnitIdByBusi("4")机构业务操作单位——》操作单位——》管理范围 ，3其它业务范围,来自busicode
			 */
			String privflag = (String) map.get("privflag");
			String privcode = "UN`";
			if ("1".equals(privflag)) {
				String codeset = this.getUserView().getManagePrivCode();
				String codevalue = this.getUserView().getManagePrivCodeValue();
				codevalue = codevalue == null || codevalue.length() == 0 ? "`" : codevalue;
				privcode = codeset + codevalue;
			} else if ("2".equals(privflag)) {
				privcode = this.userView.getUnitIdByBusi("4");
			} else if ("3".equals(privflag)&&map.get("busicode")!=null) { //20170605 dengcan ,扩展机构选择树
				privcode = this.userView.getUnitIdByBusi((String)map.get("busicode"));
			}
			String sql = "";
			/** =3时,依据输入代码名称进行模糊查询 */
			if ("3".equalsIgnoreCase(flag))
				sql = getQueryStringByName(codesetid, itemid, privcode, privflag);
			else
				sql = getQueryString(Integer.parseInt(flag), codesetid, itemid, privcode, privflag);
			this.frowset = dao.search(sql);
			ArrayList list = new ArrayList();
			while (this.frowset.next()) {
				// codesetid,codeitemid,codeitemdesc,parentid
				HashMap hm = new HashMap();
				hm.put("codeitemid", this.frowset.getString("codeitemid"));
				hm.put("codeitemdesc", this.frowset.getString("codeitemdesc"));
				hm.put("parentid", this.frowset.getString("parentid"));
				hm.put("codesetid", this.frowset.getString("codesetid"));
				/**
				 * 判断当前节点是否有子节点 模糊查询时,节点不允许展开
				 */
				if ("3".equalsIgnoreCase(flag)) {
					hm.put("hc", "0");
				} else {
					if (this.frowset.getString("codeitemid").equalsIgnoreCase(this.frowset.getString("childid")))
						hm.put("hc", "0");
					else
						hm.put("hc", "1");
				}
				list.add(hm);
			}
			map.put("codelist", list);
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 
	 * 依据换行符组织查询表达式,like行数受限
	 * codeitemdesc like "%xxx%"
	 * @param itemdesc
	 * @return
	 */
	private String combineItemdesc(String itemdesc)
	{
		if(itemdesc.length()==0)
			return "";
		StringBuffer buf=new StringBuffer();
		String[] values=StringUtils.split(itemdesc,"\n");
		buf.append(" and (");		
		for(int i=0;i<values.length;i++)
		{
    		if(i!=0)
    			buf.append(" or ");
			buf.append(" codeitemdesc like '%");
			buf.append(values[i]);
			buf.append("%'");
		}
		buf.append(")");
		return buf.toString();
	}
	/**
	 * 依据当前输入的代码项名称查询代码项
	 * @param codesetid   代码类
	 * @param itemdesc    多个值
	 * @param privcode    权限范围
	 * @return
	 */
	public String getQueryStringByName(String codesetid,String itemdesc,String privcode,String privflag)
	{
		if(itemdesc==null)
			itemdesc="";
		if(privcode==null)
			privcode="";	
		
	    StringBuffer str=new StringBuffer();
	    boolean border=false;
	    if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
	    {
	      border=true;
	      if("UN".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where codesetid='");
	          str.append(codesetid);
	          str.append("'");
	      }
	      else if("UM".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
	          str.append(codesetid);
	          str.append("' or codesetid='UN') ");

	      }
	      else if ("@K".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
	          str.append(codesetid);
	          str.append("' or codesetid='UN' or codesetid='UM') ");

	      }

	      /**组织机构历史点控制*/
	      String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	      str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	      /**加上组织机构过滤,下一步考虑多个范围情况*/      
	     /* if(privcode.length()!=0)
	      {
	    	  String[] privcodes = privcode
	    	 String codevalue=privcode.substring(2);
	    	 if(!privcode.equalsIgnoreCase("UN"))
	    	 {
	    		 if(itemdesc.length()==0)
	    		 {
	    			 str.append(" and codeitemid = '");
	    		 	str.append(codevalue);
	    		 	str.append("'");
	    		 }
	    	 }
	      }*/
	      str.append(this.getPrivsql(privcode, itemdesc,privflag));
	    }
	    else//其它代码类
	    {
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	int validateflag=1;
			try {
				this.frecset = dao.search("SELECT validateflag FROM CodeSet where CodeSetId='"+codesetid+"'");
				if (this.frecset.next()) {
					validateflag = this.frecset.getInt("validateflag");
				}
			} catch (Exception ex) {
			}
	    	
	    		str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from codeitem where codesetid='");
	    		str.append(codesetid);
	    		str.append("'");
	    		if(1==validateflag){//记录历史
	    			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	    		      str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	    		}else{//有效无效
	    			str.append(" and invalid=1");
	    		}
	    }
	    
        if(!(itemdesc.length()==0))
        {
      	  str.append(combineItemdesc(itemdesc));
        }
	    /**
	     * 现阶段针对组织机构进行排序
	     */
	    if(border)
	    	str.append(" order by a0000");

	    return str.toString();
		
	}
	/**
	 * 查询当前节点下的代码项语句
	 * @param flag        0|1,第一级|下一级
	 * @param codesetid   代码类
	 * @param parent_id   父代码项
	 * @param privcode    管理范围
	 * @return
	 */
	public String getQueryString(int flag,String codesetid,String parent_id,String privcode,String privflag)
	{
		if(parent_id==null)
			parent_id="";
		if(privcode==null)
			privcode="";		
	    StringBuffer str=new StringBuffer();
	    boolean border=false;
	    if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
	    {
	      border=true;
	      if("UN".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where codesetid='");
	          str.append(codesetid);
	          str.append("'");
	      }
	      else if("UM".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
	          str.append(codesetid);
	          str.append("' or codesetid='UN') ");
	          if(!(parent_id.length()==0)&&(flag==0))
	          {
	        	  str.append(" and parentid='");
	        	  str.append(parent_id);
	        	  str.append("'");
	        	  str.append(" and codeitemid<>'");
	        	  str.append(parent_id);
	        	  str.append("'");
	          }
	      }
	      else if ("@K".equalsIgnoreCase(codesetid))
	      {
	          str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from organization where (codesetid='");
	          str.append(codesetid);
	          str.append("' or codesetid='UN' or codesetid='UM') ");
	          if(!(parent_id.length()==0) && (flag==0))
	          {
	        	  str.append(" and parentid='");
	        	  str.append(parent_id);
	        	  str.append("'");
	          }
	      }

	      /**组织机构历史点控制*/
	      String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	      str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	      /**加上组织机构过滤,下一步考虑多个范围情况*/      
	      /*if(flag==0&&privcode.length()!=0)
	      {
	    	 String codevalue=privcode.substring(2);
	    	 if(!privcode.equalsIgnoreCase("UN"))
	    	 {
	    		 if(parent_id.length()==0)
	    		 {
	    			 str.append(" and codeitemid = '");
	    		 	str.append(codevalue);
	    		 	str.append("'");
	    		 }
	    	 }
	      }*/
	      if(flag==0)
	    	  str.append(getPrivsql(privcode,parent_id,privflag));
	    }
	    else//其它代码类
	    {
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	int validateflag=1;
			try {
				this.frecset = dao.search("SELECT validateflag FROM CodeSet where CodeSetId='"+codesetid+"'");
				if (this.frecset.next()) {
					validateflag = this.frecset.getInt("validateflag");
				}
			} catch (Exception ex) {
			}
	    		str.append("select codesetid,codeitemid,codeitemdesc,childid,parentid from codeitem where codesetid='");
	    		str.append(codesetid);
	    		str.append("'");
	    		if(1==validateflag){//记录历史
	    			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	    		      str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	    		}else{//有效无效
	    			str.append(" and invalid=1");
	    		}
	    }

	    /**代码查询*/
	    if(flag==0)
	    {
	    	if(!("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))){
		    	if(parent_id.length()==0)
		    	  str.append(" and parentid=codeitemid");
		    	else
		    	{
		  	      str.append(" and parentid<>codeitemid and parentid='");
			      str.append(parent_id);
			      str.append("'");	
		    	}
	    	}
	    }
	    else
	    {
	      str.append(" and parentid<>codeitemid and parentid='");
	      str.append(parent_id);
	      str.append("'");
	    }
	    /**
	     * 现阶段针对组织机构进行排序
	     */
	    if(border)
	    	str.append(" order by a0000");

	    return str.toString();
	  }
	
	/**
	 * 
	 * @param privcode
	 * @param keyword 用于标示是否是flag=3过来的
	 * @param privflag
	 * @return
	 */
	private String getPrivsql(String privcode,String keyword,String privflag){
		StringBuffer str = new StringBuffer();
		if(!this.userView.isSuper_admin()&&!"0".equals(privflag)){
	    	  if(privcode.length()>2){
			    	 String[] privcodes = privcode.split("`");
			    	 str.append(" and (1=2");
			    	 for(int i=0,n=privcodes.length;i<n;i++){
				    	 String priv = privcodes[i];
				    	 String codevalue=priv.substring(2);
				    	 if(!"UN".equalsIgnoreCase(priv))
				    	 {
				    		 if(keyword.length()==0)
				    		 {
				    			str.append(" or codeitemid = '");
				    		 	str.append(codevalue);
				    		 	str.append("'");
				    		 }else{
				    			 str.append(" or codeitemid like '");
					    		 str.append(codevalue);
					    		 str.append("%'"); 
				    		 }
				    	 }else{
				    		 str.append(" or parentid=codeitemid"); 
				    	 }
			    	 }
			    	 str.append(")");
		      }else{
		    	  str.append(" and 1=2");
		      }
	      }else if(keyword.length()==0){
	    	  str.append(" and parentid=codeitemid");
	      }
		return str.toString();
	}

}
