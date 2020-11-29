package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.module.statistical.businessobject.StatisticalService;
import com.hjsj.hrms.module.statistical.businessobject.impl.StatisticalServiceImpl;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * 常用统计编辑统计项
 * @author Owner
 *
 */
public class EditStaticGenTrans extends IBusiness{
	
	  public void execute() throws GeneralException 
	  {
		  String statid=(String)this.getFormHM().get("statid");		 
		  String opflag=(String)this.getFormHM().get("opflag");
		  String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		  
		  if(opflag==null||opflag.length()<=0)
			  opflag="new";
		  String stat_name="";
		  String find="";
		  String photo="";
		  String org_filter = "";
		  String categories = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("categories");
		  categories=categories==null?"":com.hrms.frame.codec.SafeCode.decode(categories);
		  String grouptype = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("type");//修改 统计条件分类 参数 wangb 20190704
		  ((HashMap)this.getFormHM().get("requestPamaHM")).remove("type");
		  if(grouptype != null && "categories".equalsIgnoreCase(grouptype)){
			  this.formHM.put("categories", categories);
			  return;
		  }
		  ArrayList catelist = new ArrayList();
		  //(Id,Name,Flag,Type,InfoKind)
		  StringBuffer sql=new StringBuffer();
		  sql.append("select categories from  sname where infokind="+infor_Flag+" group by categories ");
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  StringBuffer hidcategories = new StringBuffer();
		  try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String temp = this.frowset.getString("categories");
				if(temp==null||temp.length()==0)
					continue;
				if(!"su".equalsIgnoreCase(userView.getUserName())){
					sql.setLength(0);
					sql.append("select id from  sname where infokind="+infor_Flag+" and categories='"+temp+"'");
					this.frecset = dao.search(sql.toString());
					boolean flag = false;
					while(this.frecset.next()){
						String id =String.valueOf(this.frecset.getInt("id"));
						if(userView.isHaveResource(IResourceConstant.STATICS,id)){
							flag = true;
							break;
						}
					}
					if(!flag)
						continue;
				}
				CommonData cd = new CommonData(temp,temp);
				catelist.add(cd);
				hidcategories.append(","+temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String viewtype = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("viewtype");
		viewtype=viewtype==null?"":com.hrms.frame.codec.SafeCode.decode(viewtype);
		ArrayList viewtypelist = new ArrayList();
		  String type = "";
		  if("edit".equals(opflag))
		  {
			  if(statid==null||statid.length()<=0)
				  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			  sql.setLength(0); 
			  sql.append("select name,flag,categories,viewtype,type,photo,org_filter  from  sname where id=?");
			  try {
				this.frowset=dao.search(sql.toString(),Arrays.asList(statid));
				if(this.frowset.next())
				{
					stat_name=this.frowset.getString("name");
					find=this.frowset.getString("flag");
					categories = this.frowset.getString("categories");
					viewtype = this.frowset.getString("viewtype");
					type = this.frowset.getString("type"); 
					//常用统计 新增回显图标功能 wangbs 20190327
					photo = this.frowset.getString("photo"); 
					org_filter = String.valueOf(this.frowset.getInt("org_filter")); 
				}else
					throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			  } catch (SQLException e) {
				e.printStackTrace();
			  }
			  if("1".equalsIgnoreCase(type)){
					viewtypelist.add(new CommonData("11","平面直方图"));
					viewtypelist.add(new CommonData("20","平面圆饼图"));
					viewtypelist.add(new CommonData("55","雷达图"));
					viewtypelist.add(new CommonData("1000","平面折线图"));
			  }else if("2".equalsIgnoreCase(type)){
					viewtypelist.add(new CommonData("299","柱状图"));
					viewtypelist.add(new CommonData("11","折线图"));
					viewtypelist.add(new CommonData("33","堆叠柱状图"));
			  }
		  }else
		  {
			statid="";
			type = "1";
			//viewtypelist.add(new CommonData("12","立体直方图"));
			viewtypelist.add(new CommonData("11","平面直方图"));
			//viewtypelist.add(new CommonData("5","立体圆饼图"));
			viewtypelist.add(new CommonData("20","平面圆饼图"));
			viewtypelist.add(new CommonData("55","雷达图"));
			viewtypelist.add(new CommonData("1000","平面折线图"));
			/*viewtypelist.add(new CommonData("42","仪表盘"));//这三个图有问题，由于要封板了  暂时先注释了 20170911
			viewtypelist.add(new CommonData("44","双刻度仪表盘"));
			viewtypelist.add(new CommonData("43","水银柱"));*/
		  }

		  //获取统计分析图标的名称数组
		  StatisticalService statisticalService = new StatisticalServiceImpl();		  
		  String realPath = SystemConfig.getServletContext().getRealPath("/");
		  if(realPath == null) {
			  try {
				realPath=SystemConfig.getServletContext().getResource("/").getPath();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		  }
		  List iconList = statisticalService.getStatisticalIconName(realPath);
		  this.getFormHM().put("stat_name", stat_name);
		  this.getFormHM().put("type", type); 
		  this.getFormHM().put("photo", photo);
		  this.getFormHM().put("org_filter", org_filter);
		  this.getFormHM().put("iconList", iconList); 
		  this.getFormHM().put("findlike", find);
		  this.getFormHM().put("infor_Flag", infor_Flag);
		  this.getFormHM().put("opflag", opflag);
		  this.formHM.put("catelist", catelist);
		  this.formHM.put("categories", categories);
		  this.formHM.put("viewtype", viewtype);
		  this.formHM.put("viewtypelist", viewtypelist);
		  this.formHM.put("hidcategories", hidcategories.length()>0?hidcategories.substring(1):"");
      }

}
