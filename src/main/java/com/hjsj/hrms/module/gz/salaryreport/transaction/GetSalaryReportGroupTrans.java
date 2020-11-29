package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
/**
 * 
 * <p>Title:GetSalaryReportGroupTrans.java</p>
 * <p>Description>:薪资报表分组</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 15, 2016 1:17:46 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetSalaryReportGroupTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			if(!StringUtils.isBlank(salaryid))
				salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String rsdtlid=(String)this.getFormHM().get("rsdtlid");
			String node=(String)this.getFormHM().get("node");
			node = PubFunc.keyWord_reback(node);
			String selectedIds=(String)this.getFormHM().get("selectedIds");
			String codeitemid = "";
			String codesetid = "";

			if("root".equalsIgnoreCase(node)){//根节点
				String code = this.getCodeitem(salaryid, rsdtlid);
				String[] _code = code.split("/");
				codesetid = _code[0];
				codeitemid = _code[1];
			}else{
				String[] _code = node.split("/");
				codesetid = _code[0];
				codeitemid = "'"+_code[1]+"'";
				
				if("un".equalsIgnoreCase(codesetid)){
					String code = this.getCodeitem(salaryid, rsdtlid);
					_code = code.split("/");
					codesetid = _code[0];
				}
			}

			ArrayList list =getChildList(codesetid,codeitemid,node);
			ArrayList treeList = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean();
			for (Iterator t = list.iterator(); t.hasNext();) {
				LazyDynaBean abean = (LazyDynaBean) t.next();
				String id = (String) abean.get("id");
				String name = (String) abean.get("name");
				bean = new LazyDynaBean();
				bean.set("id",id);
				
				bean.set("text", name);
				if(StringUtils.isNotBlank(selectedIds)&&!"null".equalsIgnoreCase(selectedIds)&&selectedIds.indexOf(id+",")!=-1)
					bean.set("checked", true);
				else
					bean.set("checked", false);
				if(id.length()>=2&& "un".equalsIgnoreCase(id.substring(0, 2))){
					bean.set("icon","/images/unit.gif");	
//					if(codesetid.equalsIgnoreCase("un"))
//						bean.set("checked", false);
				}else if(id.length()>=2&& "um".equalsIgnoreCase(id.substring(0, 2))){
					bean.set("icon","/images/dept.gif");	
				}else{
					bean.set("icon","/images/prop_ps.gif");	
				}
				treeList.add(bean);
			}
			this.getFormHM().put("data", treeList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 
	 * @Title: getCodeitem   
	 * @Description:获取分组节点
	 * @param @param salaryid
	 * @param @param rsdtlid
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	private String getCodeitem(String salaryid,String rsdtlid){
		String codeitemid = "";
		try{
			SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid,this.userView);
			String gzGroupCodesetid="";
			String gzGroupCodeitemid="-1";
			LazyDynaBean abean=gzbo.getGzDetailBydID(rsdtlid);
			String f_groupItem=(String)abean.get("fgroup");
			FieldItem item=DataDictionary.getFieldItem(f_groupItem.toLowerCase());
			if("B0110".equalsIgnoreCase(f_groupItem)|| "UN".equalsIgnoreCase(item.getCodesetid()))
			{
				if(!(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())))
					gzGroupCodeitemid=this.getUserView().getUnitIdByBusi("1");
				StringBuffer _gzGroupCodeitemid = new StringBuffer();
				ArrayList list = getTopCodeitemid(gzGroupCodeitemid);
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						_gzGroupCodeitemid.append(",");
						_gzGroupCodeitemid.append("'"+list.get(i)+"'");
					}
				}
				if(_gzGroupCodeitemid.length()>0){
					gzGroupCodeitemid = _gzGroupCodeitemid.toString().substring(1);
				}
				if(gzGroupCodeitemid.length()==0)
					gzGroupCodeitemid="-1";
				gzGroupCodesetid="UN";
			}
			else if("E0122".equalsIgnoreCase(f_groupItem)|| "UM".equalsIgnoreCase(item.getCodesetid()))
			{
				if(!(this.getUserView().isAdmin()&& "1".equals(this.getUserView().getGroupId())))
					gzGroupCodeitemid=this.getUserView().getUnitIdByBusi("1");
				StringBuffer _gzGroupCodeitemid = new StringBuffer();
				ArrayList list = getTopCodeitemid(gzGroupCodeitemid);
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						_gzGroupCodeitemid.append(",");
						_gzGroupCodeitemid.append("'"+list.get(i)+"'");
					}
				}
				if(_gzGroupCodeitemid.length()>0){
					gzGroupCodeitemid = _gzGroupCodeitemid.toString().substring(1);
				}
				if(gzGroupCodeitemid.length()==0)
					gzGroupCodeitemid="-1";
				gzGroupCodesetid="UM";
			}
			else
			{
				String codesetid=item.getCodesetid();
				gzGroupCodesetid=codesetid;
			}
			codeitemid = gzGroupCodesetid+"/"+gzGroupCodeitemid;
	
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeitemid;
	}
	/**
	 * 
	 * @Title: getChildList   
	 * @Description:获取分组子节点
	 * @param @param codesetid
	 * @param @param codeitemid
	 * @param @return 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	private ArrayList getChildList(String codesetid,String codeitemid,String node)
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		try
		{
			String sql="";
			if("UN".equals(codesetid))
			{
				if("-1".equals(codeitemid)|| "un`".equalsIgnoreCase(codeitemid))
					sql="select * from organization where parentid=codeitemid and codesetid='UN'";
				else if("root".equalsIgnoreCase(node)){
					sql="select * from organization where   codesetid='UN' and codeitemid = "+codeitemid+"";
				}else
					sql="select * from organization where   codesetid='UN' and parentid in ("+codeitemid+")  and    parentid<>codeitemid";
			}
			else if("UM".equals(codesetid))
			{
				if("-1".equals(codeitemid)|| "un`".equalsIgnoreCase(codeitemid))
					sql="select * from organization where parentid=codeitemid";
				else if("root".equalsIgnoreCase(node)){
					sql="select * from organization where   codesetid<>'@K' and codeitemid = "+codeitemid+"";
				}else
					sql="select * from organization where   codesetid<>'@K' and parentid in ("+codeitemid+")  and    parentid<>codeitemid";
			}
			else 
			{
				if("-1".equals(codeitemid)|| "un`".equalsIgnoreCase(codeitemid))
					sql="select * from codeitem where codesetid='"+codesetid+"' and parentid=codeitemid";
				else
					sql="select * from codeitem where codesetid='"+codesetid+"' and parentid in ("+codeitemid+") and parentid<>codeitemid";
			}
			
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			sql+=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by a0000";
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean aBean=new LazyDynaBean();
				aBean.set("id",rs.getString("codesetid")+"/"+rs.getString("codeitemid"));
				aBean.set("name",rs.getString("codeitemdesc"));
				list.add(aBean);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取得顶级机构节点
	 * @param b_units
	 * @return
	 * @throws GeneralException
	 */	
	public ArrayList getTopCodeitemid(String b_units) throws GeneralException{
		ArrayList valuelist = new ArrayList();
		ArrayList newlist = new ArrayList();
		try
		{
			String[] unitarr =b_units.split("`");
			for(int i=0;i<unitarr.length;i++)
			{
				String codeid=unitarr[i];
				if(codeid==null|| "".equals(codeid))
					continue;
				if(codeid!=null&&codeid.trim().length()>2)
				{
					String privCode = codeid.substring(0,2);
					String privCodeValue = codeid.substring(2);	
					boolean flag = true;
					for(int j=0;j<valuelist.size();j++){//取范围内最顶级的几个节点
						String obj = (String) valuelist.get(j);
						obj = "'"+obj;
						String value = "'"+privCodeValue;
						if(obj.indexOf(value)!=-1){
							valuelist.set(j, privCodeValue);
							flag = false;
						}
						if(value.indexOf(obj)!=-1){
							flag = false;
						}
					}
					if(flag){
						valuelist.add(privCodeValue);
					}
				}
			}
			HashSet set = new HashSet();
			set.addAll(valuelist);
			newlist.addAll(set);
		}		
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return newlist;
	}
}
