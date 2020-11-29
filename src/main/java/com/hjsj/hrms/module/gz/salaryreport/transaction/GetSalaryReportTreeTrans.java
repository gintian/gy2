package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.businessobject.general.muster.hmuster.CustomReportBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.dataview.businessobject.DataViewBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * <p>Title:GetSalaryReportTreeTrans.java</p>
 * <p>Description>:获取薪资报表树结构</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 13, 2016 5:08:43 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class GetSalaryReportTreeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String rsid=(String) this.getFormHM().get("node");//节点id
			rsid = PubFunc.keyWord_reback(rsid);
			rsid = "root".equalsIgnoreCase(rsid)?"":rsid.substring(0,rsid.indexOf("#"));
			String gz_module=(String) this.getFormHM().get("gz_module");//=0表示薪资，=1表示保险
			String model = (String) this.getFormHM().get("model");//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
			if("0".equals(model)){//薪资发放加密了，没办法 在这转下
				gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
			}
			String salaryid=(String) this.getFormHM().get("salaryid");
			salaryid=PubFunc.decrypt(SafeCode.decode(salaryid));
			ArrayList treeList = new ArrayList();
			LazyDynaBean treebean=new LazyDynaBean();
			ArrayList list =getChildList(rsid,gz_module,salaryid,model);
			for (Iterator t = list.iterator(); t.hasNext();) {
				LazyDynaBean abean = (LazyDynaBean) t.next();
				// 创建子元素
				treebean=new LazyDynaBean();
				// 设置子元素属性
				String id = (String) abean.get("id");
				String name = (String) abean.get("name").toString();
				if(!"".equals(rsid))
				{
					/**报表*/
					if(id.indexOf("m")==-1)
					{
						treebean.set("id", id+"#1");
						treebean.set("text",name);
						treebean.set("leaf", "true");
						//在修改的时候加两个参数分别为是否有修改权限和是否有删除权限1为审批，0为工资发放
						if("0".equals(gz_module) && "1".equals(model)) 
							treebean.set("href", "javascript:salaryReportScope.showEidtButton(" + this.userView.hasTheFunction("32403100102") + "," + this.userView.hasTheFunction("32403100103") + ")");
						else if("0".equals(gz_module) && "0".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showEidtButton(" + this.userView.hasTheFunction("32402050302") + "," + this.userView.hasTheFunction("32402050303") + ")");
						//gz_module = 1保险
						else if("1".equals(gz_module) && "1".equals(model)) 
							treebean.set("href", "javascript:salaryReportScope.showEidtButton(" + this.userView.hasTheFunction("32503100202") + "," + this.userView.hasTheFunction("32503100203") + ")");
						else if("1".equals(gz_module) && "0".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showEidtButton(" + this.userView.hasTheFunction("32502050302") + "," + this.userView.hasTheFunction("32502050303") + ")");
						else
							treebean.set("href", "javascript:salaryReportScope.showEidtButton('true','true')");
					}
					/**名册*/
					else
					{
						treebean.set("id", id.substring(1)+"#1");
						treebean.set("text",name);
						treebean.set("leaf", "true");
						treebean.set("href", "javascript:salaryReportScope.showOpenButton()");
					}
					treebean.set("icon","/images/table.gif");
					treeList.add(treebean);
				}
				/**父表类*/
				else
				{
					treebean.set("id",id+"#0");
					treebean.set("text", name);
					if(!"4".equals(id)&&!"0".equals(id))
					{
						//在修改的时候加两个参数分别为是否有修改新增权限
						if("0".equals(gz_module) && "1".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showNewButton(" + this.userView.hasTheFunction("32403100101") + ")");
						else if("0".equals(gz_module) && "0".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showNewButton(" + this.userView.hasTheFunction("32402050301") + ")");
						//1为保险
						else if("1".equals(gz_module) && "1".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showNewButton(" + this.userView.hasTheFunction("32503100201") + ")");
						else if("1".equals(gz_module) && "0".equals(model))
							treebean.set("href", "javascript:salaryReportScope.showNewButton(" + this.userView.hasTheFunction("32502050301") + ")");
						else
							treebean.set("href", "javascript:salaryReportScope.showNewButton('true')");
					}
					else
					{
						if("4".equals(id))
							treebean.set("href", "javascript:salaryReportScope.showOpenButton()");
						else 
							treebean.set("href", "javascript:salaryReportScope.showOpenButton2()");
					}
					/**有新建权限时全部显示，否则当有下级时才显示*/
					if(!"0".equals(id) && !"4".equals(id))
					{
						if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId()))
						{
							if(!this.userView.hasTheFunction("32713080201")&&
								!this.userView.hasTheFunction("32712050301")&&
								!this.userView.hasTheFunction("32703090201")&&
								!this.userView.hasTheFunction("32702050301")&&
								!this.userView.hasTheFunction("32503100201")&&
								!this.userView.hasTheFunction("32502050301")&&
								!this.userView.hasTheFunction("32403100101")&&
								!this.userView.hasTheFunction("32402050301")&&!this.isHaveChild(id,this.userView.getUserName(),salaryid))
							{
								continue;
							}					
						}
					}
					treebean.set("icon","/images/prop_ps.gif");	
//					treebean.set("expanded", "true");
					treeList.add(treebean);
				}
			}
			if(rsid!=null&& "0".equals(rsid))
			{
				String nmodule="34";
				//保险
				if("1".equals(gz_module))
				{
					nmodule="39";
				}
				CustomReportBo bo = new CustomReportBo(this.frameconn,this.userView,nmodule);
				DataViewBo dataViewBo = new DataViewBo(this.frameconn,this.userView,nmodule);
				ArrayList<LazyDynaBean> crlist = bo.getCustomReportList();
				crlist.addAll(dataViewBo.createDataUrl());//获取所有的链接
				for(int j=0;j<crlist.size();j++)
				{
					treebean=new LazyDynaBean();
					LazyDynaBean bean = (LazyDynaBean)crlist.get(j);
					String report_type=(String)bean.get("report_type");
					String urlLink=(String)bean.get("url");
					String ntype="0";//区分自定义表，还是花名册
					if("3".equals(report_type))
					{
						RecordVo vo = (RecordVo)bean.get("vo");
						int muster_nmodule=vo.getInt("nmodule");
						if(muster_nmodule==3)//人员名册
						{
							ntype="3";
						}
						else if(muster_nmodule==21)//机构名册
						{
							ntype="21";
						}else if(muster_nmodule==41)//职位
						{
							ntype="41";
						}
					}else if("4".equals(report_type)) {
						if(StringUtils.isNotBlank(urlLink)) //对于有url的
							ntype="51";//简单名册
						else 
							continue;
					}
					String link_tabid=(String)bean.get("link_tabid");
					String id=(String)bean.get("id");
					String ext=(String)bean.get("ext");
					String name=(String)bean.get("name");
					if("0".equals(ntype))
					{
						treebean.set("id", id);
					}
					else if("51".equals(ntype))//简单报表
					{
						treebean.set("id", (String)bean.get("url"));
					}
					else
					{
						treebean.set("id", link_tabid);
					}
					treebean.set("text",name);
					if("0".equals(ntype)){
						if(".html".equalsIgnoreCase(ext)|| ".htm".equalsIgnoreCase(ext)){
							treebean.set("href", "javascript:salaryReportScope.showOpenCustomButton()");
						}
						else if(".xls".equalsIgnoreCase(ext)|| ".xlsx".equalsIgnoreCase(ext)|| ".xlt".equalsIgnoreCase(ext)|| ".xltx".equalsIgnoreCase(ext))
						{
							treebean.set("href", "javascript:salaryReportScope.showOpenCustomXLSButton()");
						}
					}else if("3".equals(ntype)){
						treebean.set("href", "javascript:salaryReportScope.showOpenMusterOneButton()");
					}else if("21".equals(ntype)){
						treebean.set("href", "javascript:salaryReportScope.showOpenMusterTwoButton()");
					}else if("41".equals(ntype)){
						treebean.set("href", "javascript:salaryReportScope.showOpenMusterThreeButton()");
					}else if("51".equals(ntype)) {
						treebean.set("href", "javascript:salaryReportScope.showSimpleMusterButton()");
					}
					treebean.set("icon","/images/table.gif");
					treebean.set("leaf", "true");
					treeList.add(treebean);
				}
			}
			this.getFormHM().put("data", treeList);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

		}
	}
	private ArrayList getChildList(String rsid,String gz_module,String salaryid,String model)
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs =null;	
		try
		{
			String sql="";
			if("".equals(rsid))
			{
				SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid), this.userView);
				String manager=gzbo.getManager();
				//只针对薪资发放界面
				if("0".equals(model) && StringUtils.isNotBlank(manager)&&!this.userView.getUserName().equalsIgnoreCase(manager)&&!this.userView.isSuper_admin())//共享非管理员为数据上报页面进入。仅能看到自定义报表 zhanghua 2017-5-31
					sql="";
				else if("0".equals(gz_module))
					sql="select * from reportstyle where rsid in (1,2,3,4) order by rsid";
				else
					sql="select * from reportstyle where rsid in (12,13)";
			}
			else{

				if(!"0".equals(rsid))
					sql="select * from reportdetail where rsid="+rsid+" and stid="+salaryid;
				else
				{
					if("0".equals(gz_module))
						sql="select * from muster_name where nModule=14 and (nPrint=-1 or nPrint="+salaryid+")  order by tabid";
					else if("1".equals(gz_module))
						sql="select * from muster_name where nModule=11 and (nPrint=-1 or nPrint="+salaryid+")  order by tabid";
				}
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(StringUtils.isNotBlank(sql)){
				rs=dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean aBean=new LazyDynaBean();
					if("".equals(rsid))
					{
				    	aBean.set("id",rs.getString("rsid"));
				    	aBean.set("name",rs.getString("rsname"));
					    list.add(aBean);
					}
					else
					{
						if(!"0".equals(rsid)){
							String xml = Sql_switcher.readMemo(rs, "ctrlParam");
							boolean  flag= this.analyseXML(xml, this.userView.getUserName());
							if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())||flag)
							{
						    	aBean.set("id",rs.getString("rsdtlid"));
						    	aBean.set("name",rs.getString("rsdtlname"));
						    	list.add(aBean);
							}
						}
						else
						{
							if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())||this.userView.isHaveResource(IResourceConstant.HIGHMUSTER, rs.getString("tabid")))//用户自定义表，加上权限限制
							{
					    		aBean.set("id","m"+rs.getString("tabid"));
				    			aBean.set("name",rs.getString("cname"));
				    			list.add(aBean);
							}
						}
					}
					
				}
			}
			
			if("".equals(rsid))
			{
				LazyDynaBean aBean=new LazyDynaBean();
				aBean.set("id","0");
				aBean.set("name","用户自定义表");
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
	public boolean isHaveChild(String id,String userName,String salaryid)
	{
		boolean isHave=false;
		ResultSet rs = null;	
		try
		{
			String sql="select * from reportdetail where rsid="+id+" and stid="+salaryid;
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs=dao.search(sql);
			while(rs.next())
			{
				String xml=Sql_switcher.readMemo(rs,"ctrlParam");
				boolean flag = this.analyseXML(xml, userName);
				if(flag)
				{
					isHave=true;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isHave;
	}
    public boolean analyseXML(String xml,String userName)
    {
    	boolean flag = true;
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
    			return flag;
    		else{
    			String type = element.getAttributeValue("type");
    			if("1".equals(type))
    			{
    				String text = element.getText();
    				if(text.equalsIgnoreCase(userName))
    					flag=true;
    				else
    					flag=false;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }
}
