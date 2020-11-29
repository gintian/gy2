package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>Title:SearchTemplateTreeTrans.java</p>
 * <p>Description>:获取模板树列表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 7, 2016 1:11:14 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class SearchTemplateTreeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		/* 模块ID
		 * 1、人事异动
		 * 2、薪资管理
		 * 3、劳动合同
		 * 4、保险管理
		 * 5、出国管理
		 * 6、资格评审
		 * 7、机构管理
		 * 8、岗位管理
		 * 9、业务申请（自助）
		 * 10、考勤管理
		 * 11、职称评审
		*/	
		String module_id = (String)this.getFormHM().get("module_id");
		/*调用的模块标识、返回模块标识
		 * 0：返回待办任务界面
		 * 1：返回已办任务界面
		 * 2：返回我的申请界面
		 * 3：返回任务监控界面
		 * 4:返回业务申请界面
		 * 。。。。。。。
		 * 11.首页待办
		 * 12、首页待办列表
		 * 13、关闭（来自第三方系统或邮件），提交后自动关闭
		 * 14、无关闭、返回按钮，提交后不跳转
		*/
		String return_flag = (String) this.getFormHM().get("return_flag");
		String sys_type = (String) this.getFormHM().get("sys_type");// 1：bs平台  2：移动平台  
		String tab_ids = (String) this.getFormHM().get("tab_ids");
		String isarchive = (String) this.getFormHM().get("isarchive");
		String flag_history=(String)this.getFormHM().get("history");
		boolean isfromhistory=false;
		if(StringUtils.isNotEmpty(flag_history)&&"true".equals(flag_history)&&"1".equals(module_id)) {
			isfromhistory=true;
		}
		if(tab_ids==null)
			tab_ids="";
		//我的申请
		String function_id = "010706,3300105,331015,3205,3216,3240105,3250105,3706,3716,3726,3736,38008,2306728,23110228";
		boolean myapply= TemplateFuncBo.haveFunctionIds(function_id, this.userView);
		/**是否定义了业务分类*/
        SubsysOperation subsysOperation=new SubsysOperation();
	    HashMap subsysmap = subsysOperation.getMap();
	    String bostype="false";//=true 按业务分类展示菜单树    =false 按模板展示菜单树
		StringBuffer strsql=new StringBuffer();
		
		String href = (String) this.getFormHM().get("href");
		boolean bflag = true;
		if(href==null|| "".equals(href))//未定义此参数时,控制是否要超链接操作
			bflag=false;
		
		RowSet rset=null;
		ArrayList list = new ArrayList();
		ArrayList modulelist = new ArrayList();
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			String unit_type=null;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unit_type==null|| "".equals(unit_type))
				unit_type="3";
			if(module_id!=null&&("3".equals(module_id)|| "5".equals(module_id)|| "6".equals(module_id)))
				unit_type="0";
			String operationcode=sysbo.getValue(Sys_Oth_Parameter.GOBROAD,"operationcode");
			
			if(!"11".equals(module_id))
			{
				
				//获取展示方式
				TemplateNavigationBo tnbo = new TemplateNavigationBo(this.frameconn,this.userView);
				bostype = checkBosType(module_id,subsysmap);
					//liuyz 考勤支持业务模版 begin
					ArrayList paramslist = new ArrayList();
					String openseal="0";
					openseal = tnbo.moduleid2staticid(module_id);
					if("10".equals(module_id))
					{
						paramslist=subsysOperation.getView_tag("30");
						if(paramslist.size()==0)
						{
							bostype="true";
						}
					}
				if("0".equals(isarchive))
					bostype = "false";
				if("true".equalsIgnoreCase(bostype)){
						openseal = tnbo.moduleid2staticid(module_id);
						String openseal_bak="0";
						if("60".equals(openseal)&&paramslist.size()==0)//考勤业务办理
						{
							TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn);
					        HashMap kqParamMap=tp.getDefineKqParamInfo(); 
					        if(kqParamMap.get("1")!=null) 
					        	paramslist.add(ResourceFactory.getProperty("general.template.overtimeApply"));
					        if(kqParamMap.get("2")!=null)
					        	paramslist.add(ResourceFactory.getProperty("general.template.leavetimeApply"));
					        if(kqParamMap.get("3")!=null)
					        	paramslist.add(ResourceFactory.getProperty("general.template.officetimeApply"));
					        openseal_bak=openseal;
						}else{
							SubsysOperation suo = new SubsysOperation(this.frameconn,this.userView);
							paramslist=suo.getChackView_tag("60".equals(openseal)?"30":openseal);
							openseal_bak= "60".equals(openseal)?"30":openseal;
						}
						for(int i=0;i<paramslist.size();i++){
							String params = PubFunc.encrypt("operationname="+paramslist.get(i)+"&staticid="+openseal_bak);
							modulelist.add(paramslist.get(i)+"`"+params);
						}
					//liuyz 考勤支持业务模版 end	 
					}else{
						if("22".equals(module_id))  //资格评审
				        {
				        	SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
				        	ArrayList sortlist = so.getView_tag("55");
				        	StringBuffer sql=new StringBuffer("");
				        	for(int i=0;i<sortlist.size();i++){
								String sortname = sortlist.get(i).toString();
								String select_id = so.getView_value("55",sortname);
								String[] select_ids = select_id.split(",");
								sql.setLength(0);
	
								sql.append("select TabId,Name from template_table  ");
								int nn=0;
								if(select_ids!=null&&select_ids.length>0)
								{
									sql.append(" where TabId in (");
									for(int j=0;j<select_ids.length;j++){
										sql.append("'"+select_ids[j]+"',");
										nn++;
									}
									sql.setLength(sql.length()-1);
									sql.append(")");
								}
								if(nn==0)
									continue;
								sql.append(" order by tabid");
								rset = dao.search(sql.toString());
								int n=0;
								while(rset.next()){
									map = new HashMap();
									map.put("id", rset.getString("tabid"));
									map.put("text", rset.getString("tabid")+":"+rset.getString("name"));
									map.put("icon", "/images/overview_obj.gif");
									list.add(map);	
								}
							}
				        }
				        else if("21".equals(module_id))  //劳动合同
				        {
				        	SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
				        	ArrayList sortlist = so.getView_tag("38");
				        	StringBuffer sql=new StringBuffer("");
				        	for(int i=0;i<sortlist.size();i++){
								String sortname = sortlist.get(i).toString();
								String select_id = so.getView_value("38",sortname);
								String[] select_ids = select_id.split(",");
								sql.setLength(0);
								
								sql.append("select TabId,Name from template_table  ");
								int nn=0;
								if(select_ids!=null&&select_ids.length>0)
								{
									sql.append(" where TabId in (");
									for(int j=0;j<select_ids.length;j++){
										sql.append("'"+select_ids[j]+"',");
										nn++;
									}
									sql.setLength(sql.length()-1);
									sql.append(")");
								}
								if(nn==0)
									continue;
								sql.append(" order by tabid");
								rset = dao.search(sql.toString());
								while(rset.next()){
									map = new HashMap();
									map.put("id", rset.getString("tabid"));
									map.put("text", rset.getString("tabid")+":"+rset.getString("name"));
									map.put("icon", "/images/overview_obj.gif");
									list.add(map);	
								}
							}
				        }
				        else if(!"9".equals(module_id))//业务申请不需要树结构
				        {
				        	String _static = "1";
				        	String res_flag = "7";
				        	if("1".equals(module_id)){//人事异动
				        		_static = "1";
				        	}else if("2".equals(module_id)){//2、薪资管理
				        		_static = "2";
				        		res_flag = "8";
				        	}else if("4".equals(module_id)){//4、保险管理
				        		_static = "8";
				        		res_flag = "17";
				        	}else if("7".equals(module_id)){//7、机构管理
				        		_static = "10";
				        		res_flag = "31";
				        	}else if("8".equals(module_id)){//8、岗位管理
				        		_static = "11";
				        		res_flag = "32";
				        	}
				        	String static_="static";
				        	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				        		static_="static_o";
				        	}
							strsql.append("select distinct a.operationcode,b.operationname ,operationid from ");
							strsql.append("template_table a ,operation b where a.operationcode=b.operationcode and b."+static_+"=");
							strsql.append(_static);
							strsql.append(" and (");			
							String[] units =unit_type.split(",");
							for(int i=0;i<units.length;i++)
							{
								strsql.append("a.flag ="+Integer.parseInt(units[i]));
								if(i<units.length-1)
									strsql.append(" or ");
							}
							strsql.append(")");
							strsql.append(" order by a.operationcode, operationid");
							rset=dao.search(strsql.toString());
							/**业务分类*/
							while(rset.next())
							{
				        		if(!bflag&&operationcode.equals(rset.getString("operationcode")))
				        			continue;
								ArrayList childrenList = this.getTemplates(module_id, rset.getString("operationcode"), res_flag, unit_type, bflag,isfromhistory);
								if(childrenList.size() > 0){
									map = new HashMap();
									//liuyz 2016-12-29 解决bug22982 bug产生原因tabid和operationcode相同，
									//map.put("id", rset.getString("operationcode"));
									map.put("text", rset.getString("operationname"));
									map.put("icon", "/images/open.png");
									//map.put("expanded", "true");//暂时不展开，如需展开直接解开注释即可 优化bug22966
									map.put("isCategory", "1");
									map.put("children", childrenList);
									list.add(map);		
								}
							}
				        }
					}
			}
			String themes = "default";
			UserView userView = this.getUserView();
			if(userView!=null){
				themes = SysParamBo.getSysParamValue("THEMES", userView.getUserName());//获得系统的模版
			}
			this.getFormHM().put("themes", themes);	
			this.getFormHM().put("data", list);
			this.getFormHM().put("moduledata", modulelist);//模板展示菜单树
			this.getFormHM().put("bostype", bostype);//展示方式
			this.getFormHM().put("module_id", module_id);
			this.getFormHM().put("tab_ids", tab_ids);
			this.getFormHM().put("return_flag", return_flag);
			this.getFormHM().put("sys_type", sys_type);
			this.getFormHM().put("myapply", myapply);
			this.getFormHM().put("ctrltask", this.getFunc(module_id, "ctrltask"));
			this.getFormHM().put("businessapply", this.getFunc(module_id, "businessapply"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	/**
	 * 解析加密串
	 * @param url
	 * @param str
	 * @return
	 */
	private HashMap getParamsByUrl(String url) {
		HashMap map = new HashMap();
		try {
			url = url.substring(1);
			if(url.indexOf("encryptParam")!=-1){
				url = url.replaceAll("b_query=link&encryptParam=", "");
				url = PubFunc.decrypt(url);
			}else{
				url = url.replaceAll("b_query=link", "");
			}
			
			String[] params = url.split("&");
			for(int i=0;i<params.length;i++){
				if(params[i].indexOf("=")!=-1){
					String[] temp = params[i].split("=");
					map.put(temp[0], temp[1]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 
	 * @Title: getTemplates   
	 * @Description:   获取模板树
	 * @param @param type
	 * @param @param module
	 * @param @param res_flag 
	 * @param @param unit_type
	 * @param @param bflag
	 * @param @param map
	 * @param @throws Exception 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	private ArrayList getTemplates(String type,String module,String res_flag,String unit_type,boolean bflag,boolean isfromHistory) throws Exception
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name,ctrl_para from template_table where operationcode='");
		strsql.append(module);
		strsql.append("' and (");
		String[] units =unit_type.split(",");
		for(int i=0;i<units.length;i++)
		{
			strsql.append(" flag ="+Integer.parseInt(units[i]));
			if(i<units.length-1)
				strsql.append(" or ");
		}
		strsql.append(") order by tabid");
		RowSet rset=null;
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{			
				if (!this.userView.isHaveResource(Integer.parseInt(res_flag), rset.getString("tabid")))
					continue;	
				HashMap paramMap = getTemplateParam(Sql_switcher.readMemo(rset,"ctrl_para"));				
				String isKq =(String)paramMap.get("isKq");
				//历史数据不控制考勤
				if(isfromHistory) {
					isKq="false";
				}
				if ("1".equals(type)){
					if ("true".equals(isKq)){
						continue;
					}
				}
				//liuyz 考勤支持业务模版
				if("10".equals(type))
				{
					if("false".equals(isKq))
					{
						continue;
					}
				}
				HashMap _map = new HashMap();
				_map.put("id", rset.getString("tabid"));
				_map.put("text", rset.getString("tabid")+":"+rset.getString("name"));
				_map.put("expanded", "true");
				if(!bflag)//加上超链接
				{
					if(isHaveMsg(this.userView,rset.getString("tabid"),this.frameconn))
						_map.put("icon", "/images/overview_n_obj.gif");
					else
						_map.put("icon", "/images/overview_obj.gif");
				}
				else
					_map.put("icon", "/images/overview_obj.gif");
				list.add(_map);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(rset);
		}
		return list;
	}
	/**检查是否是考勤使用的模板 及模板显示方式 为了提高效率 单独处理
	 * @param ctrl_para
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getTemplateParam(String ctrl_para)  throws GeneralException
	{
		HashMap map =new HashMap();
		try
		{
			map.put("isKq", "false");
			map.put("view", "list");				
			if(ctrl_para!=null && ctrl_para.trim().length()>0){		
				Document doc=null;
				Element element=null;
				String xpath="/params/sp_flag";
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
						if(_kq_type!=null&&_kq_type.trim().length()>0)
						{ 
							if(_kq_field_mapping!=null&&_kq_field_mapping.trim().length()>0)
							{
								map.put("isKq", "true");
							}
						}
					}
				}
				
				xpath="/params/init_view";
				findPath = XPath.newInstance(xpath);
				childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					if(element.getAttribute("view")!=null)
						map.put("view", (String)element.getAttributeValue("view"));
				}
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
	 * 消息库中是否存在对此模板的消息
	 * @return
	 */
	private boolean isHaveMsg(UserView userView,String tabid,Connection conn)
	{
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			StringBuffer buf=new StringBuffer();
			DbWizard dbw = new DbWizard(conn);
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(tabid);
			String filter_by_manage_priv="0"; //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
			RowSet rset=dao.search("select ctrl_para from template_table where tabid="+tabid);
			if(rset.next())
			{
				String sxml=Sql_switcher.readMemo(rset,"ctrl_para");       
				Document doc=null;
				Element element=null;
				if(sxml!=null&&sxml.trim().length()>0)
				{
					doc=PubFunc.generateDom(sxml);
					String xpath="/params/receive_notice";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist=findPath.selectNodes(doc);			
					if(childlist!=null&&childlist.size()>0)
					{
						element=(Element)childlist.get(0);
						 filter_by_manage_priv=(String)element.getAttributeValue("filter_by_manage_priv");
					}
				}
			}
			if(!userView.isSuper_admin()&& "1".equals(filter_by_manage_priv))
			{
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
								tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");				
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null )"); 
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");
			rset=dao.search(buf.toString());
			int nrec=0;
			if(rset.next())
				nrec=rset.getInt("nmax");
			if(nrec!=0)
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
	private String getRoleArr(UserView userView) {
		ArrayList rolelist= userView.getRolelist();//角色列表
	 	StringBuffer strrole=new StringBuffer();
	 	for(int i=0;i<rolelist.size();i++)
	 	{
	 		strrole.append("'");
	 		strrole.append((String)rolelist.get(i));
	 		strrole.append("'");
 			strrole.append(",");	 		
	 	}
	 	if(rolelist.size()>0)
	 	{
	 		strrole.setLength(strrole.length()-1);
	 	}
		return strrole.toString();
	}
	/**
	 * 根据权限号和模块号来确定页签是否显示
	 * @Title: getFunc   
	 * @Description:    
	 * @param @param type  模块号
	 * @param @param id 页签id
	 * @param @return 
	 * @return boolean 
	 * @author:zhaoxg   
	 * @throws
	 */
	private boolean getFunc(String type,String id){
		boolean flag = false;
		try{
			//VersionControl ver = new VersionControl();
			if("myapply".equals(id)){
				if("9".equals(type)&&this.userView.hasTheFunction("010706")){
					flag = true;
				}else if(("7".equals(type)&&this.userView.hasTheFunction("2306728"))||("8".equals(type)&&this.userView.hasTheFunction("23110228"))||("3".equals(type)&&this.userView.hasTheFunction("3300105"))||("6".equals(type)&&this.userView.hasTheFunction("331015"))
						||("1".equals(type)&&this.userView.hasTheFunction("3205"))||("5".equals(type)&&this.userView.hasTheFunction("3216"))
						||("2".equals(type)&&this.userView.hasTheFunction("3240105"))||("4".equals(type)&&this.userView.hasTheFunction("3250105"))
						||("11".equals(type)&&this.userView.hasTheFunction("38008"))){
					flag = true;
				}
			}else if("ctrltask".equals(id)){
				if("9".equals(type)){
					flag = false;
				}else if(("3".equals(type)&&this.userView.hasTheFunction("3300101"))||("6".equals(type)&&this.userView.hasTheFunction("331011"))
						||("1".equals(type)&&this.userView.hasTheFunction("3201"))||("5".equals(type)&&this.userView.hasTheFunction("3211"))
						||("2".equals(type)&&this.userView.hasTheFunction("3240102"))||("4".equals(type)&&this.userView.hasTheFunction("3250102"))
						||("10".equals(type)&&this.userView.hasTheFunction("27016"))||("11".equals(type)&&this.userView.hasTheFunction("38009"))
						||("12".equals(type)&&this.userView.hasTheFunction("4000402"))){
					flag = true;
				}else{ //机构、岗位 lis 20160825
					String fuctionIds = "4000402,3300101,331011,3201,3211,3240102,3250102,3701,3711,3721,3731,2306729,23110229";
					if(("7".equals(type) || "8".equals(type)) && TemplateFuncBo.haveFunctionIds(fuctionIds, this.userView)){
						flag = true;
					}
				}
			}else if("businessapply".equals(id)){
				if("9".equals(type)){
					flag = true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 获取展现方式
	 * @param module_id
	 * @param subsysmap
	 * @return
	 */
	private String checkBosType(String module_id, HashMap subsysmap) {
		String bostype = "false";
	    if("1".equals(module_id))
	    	bostype=(String)subsysmap.get("37");//37是人事异动
	    else if("2".equals(module_id))
	    bostype=(String)subsysmap.get("34");//34是薪资管理	
	    else if("3".equals(module_id)){
	    	bostype=(String)subsysmap.get("38");//38是劳动合同
	    	bostype = "true";
	    }
	    else if("4".equals(module_id)){
	    	bostype=(String)subsysmap.get("39");//39是保险变动
	    	//bostype = "true";//仅走业务模版
	    }
	    else if("5".equals(module_id)){
	    	bostype=(String)subsysmap.get("40");//40出国管理
	    	bostype = "true";
	    }
	    else if("6".equals(module_id)){
	    	bostype=(String)subsysmap.get("55");//资格评审（职称、任职资格等业务评审）
	    }
	    else if("7".equals(module_id))
	    	bostype=(String)subsysmap.get("56");//56组织机构
	    else if("8".equals(module_id))
	    	bostype=(String)subsysmap.get("57");//57岗位变动
	    else if("9".equals(module_id)){ //业务申请（自助）
	    	//bostype = "true";
	    }
	    else if("10".equals(module_id)){ //考勤业务办理
	    	bostype=(String)subsysmap.get("30");//liuyz 考勤支持业务模版
	    }
	    else if("11".equals(module_id)){
	    	bostype=(String)subsysmap.get("52");//职称评审
	    }
	    else if("12".equals(module_id)){
	    	bostype=(String)subsysmap.get("61");//证照管理
	    	bostype = "true";
	    }
	    if(bostype==null|| "".equalsIgnoreCase(bostype))
	    	bostype="false";
	    return bostype;
//	    else if(module_id.equals("3"))
//	    	bostype=(String)subsysmap.get("51");//警衔管理
//	    else if(module_id.equals("4"))
//	    	bostype=(String)subsysmap.get("53");//法官等级
//	    else if(module_id.equals("5"))
//	    	bostype=(String)subsysmap.get("40");//关衔管理    	
	   
	}
}
