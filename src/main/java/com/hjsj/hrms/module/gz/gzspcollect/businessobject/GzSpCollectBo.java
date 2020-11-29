package com.hjsj.hrms.module.gz.gzspcollect.businessobject;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GzSpCollectBo 
 * 类描述： 薪资汇总审批业务类
 * 创建人：zhaoxg
 * 创建时间：Dec 9, 2015 2:28:12 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 9, 2015 2:28:12 PM
 * 修改备注： 
 * @version
 */
public class GzSpCollectBo {
	public UserView userView;
	public Connection conn;
	public GzSpCollectBo(UserView uv,Connection con){
		this.userView = uv;
		this.conn = con;
	}
	/**
	 * 获取汇总选中指标
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 * @throws SQLException 
	 */
	public String getColumns(String salaryid,String rightvalue,String collectPoint) throws SQLException{
		StringBuffer str = new StringBuffer();
		String text="机构名称";
		if(!"unum".equalsIgnoreCase(collectPoint)){
			FieldItem fs=DataDictionary.getFieldItem(collectPoint);
			if(fs!=null){
				text=fs.getItemdesc();
			}
		}
		str.append("[{");
		str.append("xtype: 'treecolumn',");
		str.append("text: '"+text+"',");
		str.append("sortable: false,");
		str.append("menuDisabled:true,");
		str.append("width:300,");
		str.append("locked: true,");
		str.append("renderer: spCollectScope.renderTree,");
		str.append("dataIndex: 'text'");
		str.append("},");
//		str.append("{");
//		str.append("xtype: 'gridcolumn',");
//		str.append("text: '审批标识',"); 
//		str.append("width:100,");
//		str.append("sortable: false,");
//		str.append("menuDisabled:false,");
//		str.append("align:'center',");
//		str.append("dataIndex: 'sp_flag'");
//		str.append("},");
		
//		str.append("{");
//		str.append("xtype: 'gridcolumn',"); 
//		str.append("text: '薪资明细（人数）',"); 
//		str.append("width:260,");
//		str.append("sortable: false,");
//		str.append("menuDisabled:false,");
//		str.append("align:'left',");		
//		str.append("dataIndex: 'desc'"); 
//		str.append("},");
		str.append("{");
		str.append("xtype: 'gridcolumn',");
		str.append("text: '人次',");
		str.append("width:50,");
		str.append("sortable: false,");
		str.append("menuDisabled:false,");
		str.append("align:'right',");
		str.append("dataIndex: 'num'"); 
		str.append("}");
		
		String strsql= "";
		if(this.hasPrivateScheme("salarysp_"+salaryid, this.userView.getUserName())){
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salarysp_"+salaryid+"' and is_share = '0' and username = '" + this.userView.getUserName() + "')  order by displayorder";
		}else {
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select Max(scheme_id) from t_sys_table_scheme where submoduleid = 'salarysp_"+salaryid+"' and is_share = '1') order by displayorder";
		}
		
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,decwidth from salaryset where salaryid=?");
//		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,decwidth,sortid order by sortid");
		ArrayList parmList = new ArrayList();
		parmList.add(salaryid);
		
		ArrayList list = new ArrayList();
		ArrayList ylist = new ArrayList();
		RowSet rs = null;
		try {
			rs = dao.search(strsql);
			while(rs.next()){
				FieldItem item=new FieldItem();
				item.setItemid(rs.getString("itemid").toUpperCase());
				item.setItemdesc(rs.getString("itemdesc"));
				item.setCodesetid("0");
				item.setItemtype("N");
				item.setFormula(rs.getString("displaywidth"));
				item.setAlign(rs.getString("align"));
				item.setFormat(rs.getString("is_lock")==null?"0":rs.getString("is_lock"));
				item.setClassname(rs.getString("is_order"));
				item.setValue(rs.getString("displaydesc"));
				item.setVisible("1".equals(rs.getString("is_display"))?true:false);
				item.setDecimalwidth(DataDictionary.getFieldItem(rs.getString("itemid")).getDecimalwidth());
				list.add(item);
			}
			rs = dao.search(sql.toString(),parmList);
			Map map = new HashMap();//存放salaryset中的itemid
			while(rs.next()){
				map.put(rs.getString("itemid").toLowerCase(), rs.getString("itemdesc"));
				FieldItem item=new FieldItem();
				item.setItemid(rs.getString("itemid").toUpperCase());
				item.setItemdesc(rs.getString("itemdesc"));
				item.setCodesetid("0");
				item.setItemtype("N");
				item.setAlign("right");
				//item.setItemlength(Integer.parseInt(rs.getString("Itemlength")));
				item.setDecimalwidth(Integer.parseInt(rs.getString("decwidth")));
				ylist.add(item);
			}
			if(list.size()>0){
				//栏目设置的list
				for(int i=0;i<list.size();i++){
					FieldItem item=(FieldItem) list.get(i);
					if(!item.isVisible()) {
						map.remove(item.getItemid().toLowerCase());
						continue;
					}
					//xiegh 20170510 add处理主界面列标题顺序问题  bug26869
					if(map.containsKey(item.getItemid().toLowerCase())){
						str.append(",{");
						str.append("xtype: 'numbercolumn',");
						if(StringUtils.isBlank(item.getItemdesc())){
							FieldItem item1=DataDictionary.getFieldItem(item.getItemid());
							str.append("text: '"+item1.getItemdesc()+"',");
						}else
							str.append("text: '"+item.getValue()+"',");
						str.append("width:"+item.getFormula()+",");
						boolean sortable = "1".equals(item.getClassname())?true:false;
						str.append("sortable: "+sortable+",");
						boolean islock = "1".equals(item.getFormat())?true:false;
						str.append("locked: "+islock+",");
						String align = "right";
						if("1".equals(item.getAlign())){
							align = "left";
						}else if("2".equals(item.getAlign())){
							align = "center";
						}
						str.append("align:'"+align+"',");
						str.append("dataIndex: '"+item.getItemid().toUpperCase()+"', ");
						//这里添加小数位
						String format = "";
						for(int j = 0; j < item.getDecimalwidth(); j++) {
							format += "0";
						}
						str.append("format:'0."+format+"' ");
						str.append("}");
					}
					map.remove(item.getItemid().toLowerCase());
				}
				//可能是新增的指标等，这样不在已保存的栏目设置里面，单独添加
				if(map.size() > 0) {
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object itemid = entry.getKey();
						Object itemdesc = entry.getValue();
						str.append(",{");
						str.append("xtype: 'numbercolumn',");
						str.append("text: '"+itemdesc.toString()+"',");
						str.append("width:100,");
						str.append("sortable: false,");
						str.append("align:'right',");
						str.append("dataIndex: '"+itemid.toString().toUpperCase()+"', ");
						//这里添加小数位
						String format = "";
						int decimalwidth = DataDictionary.getFieldItem(itemid.toString()).getDecimalwidth();
						for(int j = 0; j < decimalwidth; j++) {
							format += "0";
						}
						str.append("format:'0."+format+"' ");
						str.append("}");
					}
				}
			}else{
				for(int i=0;i<ylist.size();i++){
					FieldItem item=(FieldItem) ylist.get(i);
					str.append(",{");
					str.append("xtype: 'numbercolumn',");
					str.append("text: '"+item.getItemdesc()+"',");
					str.append("width:100,");
					str.append("sortable: false,");
					str.append("align:'right',");
					str.append("dataIndex: '"+item.getItemid().toUpperCase()+"', ");
					//这里添加小数位
					String format = "";
					for(int j = 0; j < item.getDecimalwidth(); j++) {
						format += "0";
					}
					str.append("format:'0."+format+"' ");
					str.append("}");
				}
			}
			str.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return str.toString();
	}
    /**
	 * 查看当前用户名在设置方案中是否存在私有方案
	 * @param username
	 * @return boolean 
	 * */
	public boolean hasPrivateScheme(String submoduleid, String username){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		// 是否存在私有记录
		String sqlForPrivate= "select * from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = 0 and username = '" + username + "'";
		RowSet rset=null;
	    try{
        	rset=dao.search(sqlForPrivate);
        	if(rset.next()){
        		flag = true;
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return flag;
	}
	/**
	 * 根据授权获取按钮
	 * @return
	 */
	public String getButtons(String salaryid,String collectPoint,String tar,boolean isAppeal,boolean isApprove,HashMap buttonMap,String sp_actor_str,String imodule){
		StringBuffer buttons = new StringBuffer();
		String str = "";
		try {
			if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240326")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250326"))) {
				buttons.append("{xtype:'button',text:'" + ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation") + "',");//功能导航
				buttons.append("menu:{items:[");
				if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240318")) || ("1".equals(imodule) && this.userView.hasTheFunction("325020303"))) {
					buttons.append("{text:'导出',");//功能导航
					buttons.append("menu:{items:[");
					buttons.append("{text:'导出汇总表',");//导出
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.downLoadTotal()");
					buttons.append("}},");
					buttons.append("{text:'导出明细表',");//导出
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.exportData()");
					buttons.append("}}");
					buttons.append("]}},");
				}
				if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240314")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250319"))) {
					buttons.append("{text:'导入明细表',");//导入数据
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.importAsTemplate()");
					buttons.append("}},");
				}
				if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240307")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250307"))) {
					buttons.append("{text:'" + ResourceFactory.getProperty("menu.gz.comparisonWithData") + "',");//数据比对(与上期数据比对)
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.changesMore()");
					buttons.append("}},");
				}

				if ((!"1".equals(imodule) && this.userView.hasTheFunction("32403002")) || ("1".equals(imodule) && this.userView.hasTheFunction("32503002"))) {

					StringBuffer str_Temp = new StringBuffer();
					if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240317")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250317"))) {
						str_Temp.append("{text:'" + ResourceFactory.getProperty("menu.gz.batch.import") + "',");//批量引入
						str_Temp.append("handler: function () {");
						str_Temp.append("spCollectScope.batchImport()");
						str_Temp.append("}},");
					}
					if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240316")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250317"))) {
						str_Temp.append("{text:'" + ResourceFactory.getProperty("menu.gz.sortman") + "',");//同步人员顺序
						str_Temp.append("handler: function () {");
						str_Temp.append("spCollectScope.syncgzemp()");
						str_Temp.append("}},");
					}
					if (str_Temp.length() > 0) {
						str_Temp.deleteCharAt(str_Temp.length() - 1);
						buttons.append("{text:'" + ResourceFactory.getProperty("menu.gz.batch") + "',");//批量处理
						buttons.append("menu:{items:[");
						buttons.append(str_Temp);
						buttons.append("]}},");
					}
				}
				if (!"1".equals(imodule) && this.userView.hasTheFunction("324031001")) {
					buttons.append("{text:'" + ResourceFactory.getProperty("menu.gz.updisk") + "',");//银行报盘
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.updisk()");
					buttons.append("}},");
				}
				if ((!"1".equals(imodule) && this.userView.hasTheFunction("324031002")) || ("1".equals(imodule) && this.userView.hasTheFunction("325031002"))) {
					buttons.append("{text:'" + ResourceFactory.getProperty("menu.gz.report") + "',");//薪资报表
					buttons.append("handler: function () {");
					buttons.append("spCollectScope.gzReport('" + salaryid + "');");
					buttons.append("}},");
				}
				buttons.deleteCharAt(buttons.length() - 1);
				buttons.append("]}},'-',");
			}
			if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240308")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250308"))) {
				buttons.append("{xtype:'button',id:'spcompute',text:'" + ResourceFactory.getProperty("button.computer") + "',");//计算
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.compute('0')");
				buttons.append("}},");
			}

			if ((!"1".equals(imodule) && this.userView.hasTheFunction("3240309")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250309"))) {
				buttons.append("{xtype:'button',id:'spcheck',text:'" + ResourceFactory.getProperty("button.audit") + "',");//审核
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.verify()");
				buttons.append("}},");
			}

			if ((((!"1".equals(imodule) && this.userView.hasTheFunction("3240311")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250311")))) && isAppeal) {

				String text = ResourceFactory.getProperty("button.appeal");
				if (sp_actor_str != null && sp_actor_str.trim().length() > 0)
					text += "[" + sp_actor_str.split("##")[1] + "]";
				buttons.append("{xtype:'button',id:'spappeal',text:'" + text + "',");//报批
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.appeal()");
				buttons.append("}},");
			}

			if (((!"1".equals(imodule) && this.userView.hasTheFunction("3240303")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250303")))) {
				buttons.append("{xtype:'button',id:'spreject',text:'" + ResourceFactory.getProperty("button.reject") + "',");//驳回
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.gzSpReject();");
				buttons.append("}},");
			}

			if (((!"1".equals(imodule) && (this.userView.hasTheFunction("3240312")) || ("1".equals(imodule) && this.userView.hasTheFunction("3250312")))) && isApprove) {
				buttons.append("{xtype:'button',id:'spapprove',text:'" + ResourceFactory.getProperty("button.approve") + "',");//批准
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.gzSpConfirm();");
				buttons.append("}},");
			}


			if ((!"1".equals(imodule) && this.userView.hasTheFunction("324031003")) || ("1".equals(imodule) && this.userView.hasTheFunction("325031003"))) {
				buttons.append("{xtype:'button',id:'spconfirm',text:'" + ResourceFactory.getProperty("button.apply") + ResourceFactory.getProperty("button.affirm") + "',");//审批确认
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.open_submit_dialog();");
				buttons.append("}},");
			}

			if((!"1".equals(imodule)&&this.userView.hasTheFunction("324031002"))||("1".equals(imodule)&&this.userView.hasTheFunction("325031002"))){
				SalaryReportBo salaryReportBo=new SalaryReportBo(this.conn,salaryid,this.userView);

				ArrayList<LazyDynaBean> reportList=salaryReportBo.listCommonReport(imodule,"1");
				StringBuffer buttonStr=new StringBuffer("{xtype:'button',id:'common_Report_button',");
				if(reportList.size()==1) {
					buttonStr.append("text:'"+reportList.get(0).get("text")+"',hidden:false,");
				}else {
					String hidden="false";
					if (reportList.size() == 0) {
						hidden = "true";
					}
					buttonStr.append("text:'常用报表<img style=\"\" src=\"/ext/ext6/resources/images/button/arrow.gif\"/>',hidden:" + hidden + ",");
				}
				buttonStr.append("handler:spCollectScope.openCommon_reportCombo");
				buttonStr.append("},");
				buttons.append(buttonStr);
			}


			String returnflag = (String)buttonMap.get("returnflag");
			if(!"noreturn".equalsIgnoreCase(returnflag)){
				buttons.append("{xtype:'button',text:'"+ResourceFactory.getProperty("button.leave")+"',");//返回
				buttons.append("handler: function () {");
				buttons.append("spCollectScope.back('" + tar + "');");
				buttons.append("}}");
			}

			str = "[" + buttons.toString() + "]";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 获取业务日期及次数
	 * @param salaryid
	 * @return
	 */
	public ArrayList getOperationDateListSP(int salaryid)
	{
		ArrayList list=new ArrayList();
		try
		{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,salaryid,this.userView);
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2,A00Z3 from salaryhistory where ");// and  ( sp_flag='06' or  sp_flag='03' )
			buf.append(" (  ( ( (AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"') and salaryid=?  order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList parmList = new ArrayList();
			parmList.add(salaryid+"");
			RowSet rset=dao.search(buf.toString(),parmList);
			while(rset.next())
			{
				HashMap map = new HashMap();
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				String[] temp = strdate.split("\\.");
				if(temp.length>0){
					map.put("id", SafeCode.encode(PubFunc.encrypt(strdate))+"#"+SafeCode.encode(PubFunc.encrypt(rset.getInt("A00Z3")+"")));
					map.put("name", temp[0]+"年"+temp[1]+"月"+"第"+rset.getInt("A00Z3")+"次");
				}
				list.add(map);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 发起人列表
	 * @param date
	 * @param salaryid
	 * @return
	 */
	public ArrayList getOperationCoundList(String date,String salaryid)
	{
		ArrayList list=new ArrayList();
		RowSet rset=null; 
		try
		{
			HashMap<String,String> user_a0100Map=new HashMap<String, String>();
			HashMap map = new HashMap();
			map.put("id", "all");
			map.put("name", "全部");
			list.add(map);
			if(date.length()==0|| "#".equals(date)){
				return list;
			}
			String bosdate = date.split("#")[0];
			String count = date.split("#")[1];
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
			StringBuffer buf=new StringBuffer();
			buf.append("select a0100,nbase,fullname,username from operuser  where   username in (");
			buf.append("select distinct UserFlag from salaryhistory where a00z2="+Sql_switcher.dateValue(PubFunc.decrypt(SafeCode.decode(bosdate)))+"  and a00z3=? and ");//and  ( sp_flag='06' or  sp_flag='03' )
			buf.append(" (  ( ( (AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' )  ) or curr_user='"+this.userView.getUserName()+"') and salaryid=?");
			buf.append(")");
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList parmList = new ArrayList();
			parmList.add(new Integer(PubFunc.decrypt(SafeCode.decode(count))));
			parmList.add(new Integer(salaryid));
			rset=dao.search(buf.toString(),parmList);
			
			HashMap nbaseMap = new HashMap();
			String usernames="";
			while(rset.next())
			{
				String a0100=rset.getString("a0100")!=null?rset.getString("a0100").trim():"";
				String nbase=rset.getString("nbase")!=null?rset.getString("nbase").trim():"";
				String fullname=rset.getString("fullname")!=null?rset.getString("fullname").trim():""; 
				String username=rset.getString("username")!=null?rset.getString("username").trim():""; 
				usernames+=",'"+username+"'";
				if(a0100.length()>0&&nbase.length()>0)
				{
					user_a0100Map.put((nbase + "," + a0100).toLowerCase(), StringUtils.isBlank(fullname)?username:fullname);
					nbaseMap.put(nbase, (nbaseMap.get(nbase)==null?"":nbaseMap.get(nbase))+",'"+a0100+"'");
				}else if(fullname.length()>0)
				{
					map = new HashMap();
					map.put("id", username);
					map.put("name", fullname);
					list.add(map);
				}else{
					map = new HashMap();
					map.put("id", username);
					map.put("name", username);
					list.add(map);
				}

			}
			Iterator iter = nbaseMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue(); 
				rset=dao.search("select a.a0101,b.username from "+key.toString()+"A01 a,operuser b where a.a0100=b.a0100 and a.a0100 in ("+val.toString().substring(1)+") and b.username in ("+usernames.substring(1)+")");
				while(rset.next()){
					map = new HashMap();
					map.put("id", rset.getString("username"));
					String name="";
					String a0100_ = rset.getString("a0100");
					if(user_a0100Map.containsKey((key.toString() + "," + a0100_).toLowerCase())){
						name = (String)user_a0100Map.get((key.toString() + "," + a0100_).toLowerCase());
						name += "("+ rset.getString("a0101")+")";
					}else{
						name=rset.getString("a0101");
					}
					map.put("name", name);
					list.add(map);
				}
			}
			//---------------查一遍自助用户 zhaoxg add 2016-12-20-----------------
			ArrayList dbList = DataDictionary.getDbpreList();
	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	        String username = "username";
	        String login_name = login_vo.getString("str_value");
	        int idx=login_name.indexOf(",");
	        if(idx==-1|| "#,".equalsIgnoreCase(login_name))
	        {
	        	
	        }else{
	        	username = login_name.substring(0,idx).length()==0?"username":login_name.substring(0,idx);
	        }
			for(int i=0;i<dbList.size();i++){
				StringBuffer sql = new StringBuffer();
				sql.append("select '"+username+"' username,a0101 from "+dbList.get(i)+"A01  where   '"+username+"' in (");
				sql.append("select distinct UserFlag from salaryhistory where a00z2="+Sql_switcher.dateValue(PubFunc.decrypt(SafeCode.decode(bosdate)))+"  and a00z3=? and ");//and  ( sp_flag='06' or  sp_flag='03' )
				sql.append(" (  ( ( (AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' )  ) or curr_user='"+this.userView.getUserName()+"') and salaryid=?");
				sql.append(")");
				if(usernames.length()>0)
					sql.append(" and username not in ("+usernames.substring(1)+")");
				rset=dao.search(sql.toString(),parmList);
				while(rset.next()){
					map = new HashMap();
					map.put("id", rset.getString("username"));
					map.put("name", rset.getString("a0101"));
					list.add(map);
				}
			}
			//-------------------------------------------------------------------
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return list;
	
	}
	/**
	 * 取得当前审批人默认的业务日期和发放次数
	 * @param salaryid
	 * @return
	 */
	public HashMap getBosdateAndCount(int salaryid){
		HashMap map = new HashMap();
		try{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,salaryid,this.userView);
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryhistory where ");// and  ( sp_flag='06' or  sp_flag='03' )
			buf.append(" (  ( ( (AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"') and salaryid=?  order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.conn);
			String a00z2 = "";
			ArrayList parmList = new ArrayList();
			parmList.add(salaryid+"");
			RowSet rset=dao.search(buf.toString(),parmList);
			if(rset.next()){
				a00z2 = PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				map.put("bosdate", a00z2);
			}
			buf.setLength(0);
			buf.append("select distinct A00Z3 from salaryhistory where  ");
			buf.append(" (  ( ( (AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"') and A00Z2=");
			buf.append(Sql_switcher.dateValue(a00z2)+"  and salaryid=? order by A00Z3");
			RowSet rs=dao.search(buf.toString(),parmList);
			if(rs.next()){
				map.put("count", rs.getString("A00Z3")==null?"":rs.getString("A00Z3"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取汇总指标关联的代码类
	 * @param salaryid
	 * @param collectPoint
	 * @return
	 */
	public String getCodeSet(String salaryid,String collectPoint){
		String codeset = "";
		try{
			String sql="select codesetid from salaryset where salaryid = ? and itemid=? ";
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList parmList = new ArrayList();
			parmList.add(salaryid);
			parmList.add(collectPoint);
			RowSet rs = dao.search(sql,parmList);
			if(rs.next()){
				codeset = rs.getString("codesetid");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeset;
	}
	/**
	 * 获取机构顶级节点
	 * @param salaryid
	 * @param codeset
	 * @param collectPoint
	 * @param bosdate
	 * @param count
	 * @return
	 */
	public ArrayList getTopCode(String salaryid,String codeset,String collectPointTemp,String bosdate,String count){
		ArrayList list = new ArrayList();
		boolean isUM=false;
		try{
			StringBuffer collectPoint=new StringBuffer();
			String e0122="",b0110="";
			if(collectPointTemp.indexOf("`")!=-1){
				b0110=collectPointTemp.split("`")[0];
				e0122=collectPointTemp.split("`")[1];
				collectPoint.append("nullif(");
				collectPoint.append(" case ");
				if(!"e0122".equalsIgnoreCase(e0122)&&!"b0110".equalsIgnoreCase(b0110)){//归属单位和部门均设置了
					collectPoint.append("  when  nullif("+b0110+",'') is not null  then "+b0110+" ");
					collectPoint.append("  when (nullif("+b0110+",'') is  null ) and nullif("+e0122+",'') is not null then "+e0122+" ");
					collectPoint.append("  when (nullif("+e0122+",'') is  null ) and (nullif("+b0110+",'') is null) and nullif(b0110,'') is not null then b0110 ");
					collectPoint.append(" else e0122 end ");
				}else if(!"e0122".equalsIgnoreCase(e0122)&& "b0110".equalsIgnoreCase(b0110)){//设置了归属部门，没设置归属单位
					collectPoint.append("  when nullif("+e0122+",'') is not null then "+e0122+" ");
					collectPoint.append("  when (nullif("+e0122+",'') is  null) and nullif(e0122,'') is not null then e0122 ");
					collectPoint.append(" else b0110 end ");
				}else if("e0122".equalsIgnoreCase(e0122)&&!"b0110".equalsIgnoreCase(b0110)){//没设置归属部门，设置了归属单位
					collectPoint.append("  when nullif("+b0110+",'') is not null then "+b0110+" ");
					collectPoint.append("  when (nullif("+b0110+",'') is null) and nullif(b0110,'') is not null then b0110 ");
					collectPoint.append(" else "+"e0122 end ");
				}else if("e0122".equalsIgnoreCase(e0122)&& "b0110".equalsIgnoreCase(b0110)){//啥都没设置
					collectPoint.append("  when nullif(b0110,'') is not null then b0110 ");
					collectPoint.append(" else "+"e0122 end ");
				}
				collectPoint.append(",'')");
			}else
				collectPoint.append(collectPointTemp);
			
			
			String tablename = "organization";
			String layer ="grade";
			if(!"UN".equalsIgnoreCase(codeset)&&!"UM".equalsIgnoreCase(codeset)&&!"@k".equalsIgnoreCase(codeset)&&!"".equalsIgnoreCase(codeset)){
				tablename = "codeitem";
				layer ="layer";
			}
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userView);
			StringBuffer buf=new StringBuffer();
			buf.append("select MIN("+Sql_switcher.length("codeitemid")+") lg from "+tablename+"  where  codeitemid in( ");
			buf.append("select "+collectPoint.toString()+" from salaryhistory where ");// and (sp_flag='06' or sp_flag='03')
			buf.append(" ((((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
			buf.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=?)");
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList parmList = new ArrayList();
			parmList.add(new Integer(salaryid));
			parmList.add(new Integer(count));
			RowSet rs = dao.search(buf.toString(), parmList);
			int lg = 0;
			if(rs.next()){
				lg = rs.getInt("lg");
			}
	
			StringBuffer str = new StringBuffer();
			str.append("select "+Sql_switcher.substr(collectPoint.toString() ,"1", lg+"")+" codeitemid from salaryhistory where ");  // and (sp_flag='06' or sp_flag='03')
			str.append(" (((((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
			str.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=?)");
			str.append(" group by "+Sql_switcher.substr(collectPoint.toString() ,"1", lg+"")+"");
			parmList = new ArrayList();
			parmList.add(new Integer(salaryid));
			parmList.add(new Integer(count));
			RowSet rs1 = dao.search(str.toString(),parmList);
			
			while(rs1.next()){
				list.add(rs1.getString("codeitemid"));
				if("organization".equalsIgnoreCase(tablename))
				{
					if(AdminCode.getCode("UM", rs1.getString("codeitemid"))!=null)
						isUM=true;
				} 	
			}
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(isUM&&list.size()>1)
			list = processCodeItemId(list);
		return list;
	}
	
	/**
	 * 当顶级汇总机构为多个单位下的部门时，需将顶级汇总单位换成叶子单位
	 * @param list
	 * @return
	 */
	private ArrayList processCodeItemId(ArrayList list) { 
		Set<String> unSetId = new LinkedHashSet<String>(); 
		for(int i =0;i<list.size();i++){
			String codeitemid= String.valueOf(list.get(i));
			if(StringUtils.isBlank(codeitemid) || "null".equalsIgnoreCase(codeitemid)) {
				unSetId.add(null);
				continue;
			}
			CodeItem item=AdminCode.getCode("UM", codeitemid);
			if(item==null)
				item=AdminCode.getCode("UN", codeitemid);
			 
			
			if(item==null) //不为机构ID
			{
				for(int j=1;j<codeitemid.length();j++){
					String _codeitemid = codeitemid.substring(0, codeitemid.length()-j);
					CodeItem _item=AdminCode.getCode("UM", _codeitemid);
					if(_item==null)
						_item=AdminCode.getCode("UN", _codeitemid);
					if(_item!=null)
					{
						item=_item;
						codeitemid=_codeitemid;
						break;
					}
					
				}
			}
			 
			if(item!=null&& "UN".equalsIgnoreCase(item.getCodeid()))
			{
				unSetId.add(item.getCodeitem());
			}
			else if(item!=null)
			{
				int n=0;
				while(true)
				{
					n++;
					if(n>10) //避免脏数据造成的死循环
						break;
					CodeItem parentItem=AdminCode.getCode("UM", item.getPcodeitem());
					if(parentItem==null)
						parentItem=AdminCode.getCode("UN", item.getPcodeitem());
					if(parentItem!=null&& "UN".equalsIgnoreCase(parentItem.getCodeid()))
					{
						unSetId.add(parentItem.getCodeitem());
						break;
					}
					if(parentItem!=null)
						item=parentItem.cloneItem();
					if(parentItem==null)
						break;
					
				}
			}
		}
	
		if(unSetId.size()==1)
			return list;
		else
		{
			ArrayList _list=new ArrayList();
			for(Iterator t=unSetId.iterator();t.hasNext();)
				_list.add((String)t.next());
			return _list;
		} 
	}
	/**
	 * 获取审批标记
	 * @param collectPoint
	 * @param salaryid
	 * @param bosdate
	 * @param count
	 * @param collectList
	 * @param privWhlStr
	 * @return
	 */
	public HashMap getSpFlag(String collectPoint,String salaryid,String bosdate,String count,ArrayList collectList,String privWhlStr,String cound){
		HashMap map = new HashMap();
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String userName = this.userView.getUserName();
			StringBuffer str=new StringBuffer();
			HashMap collectMap=new HashMap<Integer, ArrayList>();
			
			for(int i=0;i<collectList.size();i++){
				String collect=(String)collectList.get(i);
				if(StringUtils.isNotBlank(collect)&&!"null".equalsIgnoreCase(collect)){
					if(collectMap.containsKey(collect.length())){
						ArrayList list=(ArrayList)collectMap.get(collect.length());
						list.add(collect);
						
					}else{
						ArrayList list=new ArrayList<String>();
						list.add(collect);
						collectMap.put(collect.length(), list);
					}
				}else{
					collectMap.put(-1, null);
				}
			}
			
			Iterator iter = collectMap.entrySet().iterator();
			String strSql=" from salaryhistory where "
					+ "((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%"+userName+";%' )) or curr_user='"+userName+"')"
							+ "and salaryid='"+salaryid+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"' ";
			
			int bpnum = 0;//报批人数
			int _bhnum = 0;//驳回到操作人人数
			int bhnum = 0;//驳回人数
			int pznum = 0;//批准人数
			int jsnum = 0;//结束人数
			int selfbpnum = 0;//当前操作人经手报上去的人数且还没批准和结束
			String org="";
			String tempstr="";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				int key = (Integer)entry.getKey();
				ArrayList<String> val =(ArrayList<String>) entry.getValue();
				str.setLength(0);
				if(key!=-1){
					str.append(" select org ,sp_flag,curr_user,COUNT(sp_flag) num from (select "+Sql_switcher.substr(collectPoint, "1", key+"")+" as org,sp_flag,curr_user ");
					str.append(strSql);
					str.append(" ) history ");
					str.append(" where org in (");
					for(String s: val){
						str.append("'"+s+"',");
					}
					str.deleteCharAt(str.length()-1);
					str.append(") group by org,sp_flag,curr_user order by org ");
				}else{
					str.append(" select org ,sp_flag,curr_user,COUNT(sp_flag) num from (select "+collectPoint+" as org,sp_flag,curr_user ");
					str.append(strSql);
					str.append(" ) history where org is null");
					str.append(" group by org,sp_flag,curr_user ");
				}
				RowSet rs = dao.search(str.toString());
				tempstr="zero";
				bpnum = 0;//报批人数
				_bhnum = 0;//驳回到操作人人数
				bhnum = 0;//驳回人数
				pznum = 0;//批准人数
				jsnum = 0;//结束人数
				selfbpnum = 0;//当前操作人经手报上去的人数且还没批准和结束
				while(rs.next()){
					
					//while(rs.next()){
					String sp_flag = rs.getString("sp_flag");
					String curr_user = rs.getString("curr_user");
					org=rs.getString("org");
					if(StringUtils.isBlank(org))
						org="null";
					if("zero".equalsIgnoreCase(tempstr))
						tempstr=org;
					if(!tempstr.equalsIgnoreCase(org)){
						
						StringBuffer buf = new StringBuffer();
						if(bpnum+_bhnum>0)
						{
							buf.append("待批："+(bpnum+_bhnum)+"人&nbsp;&nbsp;");
							if(_bhnum>0){
								buf.append("驳回："+_bhnum+"人&nbsp;&nbsp;");
							}
						}
						if(selfbpnum>0)
						{
							buf.append("已报批："+selfbpnum+"人&nbsp;&nbsp;");
						}
						if(pznum>0)
						{
							buf.append("批准："+pznum+"人&nbsp;&nbsp;");
						}
						if(jsnum>0)
						{
							buf.append("结束："+jsnum+"人");
						}
						if(key==-1)
						{
							map.put("nullnum", buf.toString());
							map.put("nullcolor", "");
						}
						else
						{
							map.put(tempstr+"num", buf.toString());
							map.put(tempstr+"color", "");
						}
						if(bpnum>0)
						{
							if(key==-1)
								map.put("nullcolor", "dealto_green.gif");
							else
								map.put(tempstr+"color", "dealto_green.gif");
						}
						if(_bhnum>0){
							if(key==-1)
								map.put("nullcolor", "dealto.gif");
							else
								map.put(tempstr+"color", "dealto.gif");
						}
						tempstr=org;
						String bp = "0";//报批
						String _bh = "0";//驳回到操作人
						String bh = "0";//驳回
						String pz = "0";//批准
						String js = "0";//结束
						
						bpnum = 0;//报批人数
						_bhnum = 0;//驳回到操作人人数
						bhnum = 0;//驳回人数
						pznum = 0;//批准人数
						jsnum = 0;//结束人数
						selfbpnum = 0;//当前操作人经手报上去的人数且还没批准和结束
					}
					if("02".equals(sp_flag)&&userName.equals(curr_user)){//有当前操作人待审批的薪资数据
						bpnum+=rs.getInt("num");
						continue;
					}
					if("07".equals(sp_flag)&&userName.equals(curr_user)){//有驳回到操作人的薪资数据
						_bhnum+=rs.getInt("num");
						continue;
					}
					if("07".equals(sp_flag)){//有驳回的薪资数据
						bhnum+=rs.getInt("num");
						continue;
					}
					if("03".equals(sp_flag)){
						pznum+=rs.getInt("num");
						continue;
					}
					if("06".equals(sp_flag)){
						jsnum+=rs.getInt("num");
						continue;
					}
					selfbpnum+=rs.getInt("num");//上面几种状态都不是的那肯定就是当前人经手报上去的
					//}
					

				}
				
				StringBuffer buf = new StringBuffer();
				if(bpnum+_bhnum>0)
				{
					buf.append("待批："+(bpnum+_bhnum)+"人&nbsp;&nbsp;");
					if(_bhnum>0){
						buf.append("驳回："+_bhnum+"人&nbsp;&nbsp;");
					}
				}
				if(selfbpnum>0)
				{
					buf.append("已报批："+selfbpnum+"人&nbsp;&nbsp;");
				}
				if(pznum>0)
				{
					buf.append("批准："+pznum+"人&nbsp;&nbsp;");
				}
				if(jsnum>0)
				{
					buf.append("结束："+jsnum+"人");
				}
				if(StringUtils.isBlank(tempstr) || "null".equalsIgnoreCase(tempstr))
				{
					map.put("nullnum", buf.toString());
					map.put("nullcolor", "");
				}
				else
				{
					map.put(tempstr+"num", buf.toString());
					map.put(tempstr+"color", "");
				}
				if(bpnum>0)
				{
					if(StringUtils.isBlank(tempstr) || "null".equalsIgnoreCase(tempstr))
						map.put("nullcolor", "dealto_green.gif");
					else
						map.put(tempstr+"color", "dealto_green.gif");
				}
				if(_bhnum>0){
					if(StringUtils.isBlank(tempstr) || "null".equalsIgnoreCase(tempstr))
						map.put("nullcolor", "dealto.gif");
					else
						map.put(tempstr+"color", "dealto.gif");
				}
			}
			
			//if(!tempstr.equalsIgnoreCase(org)){
				
				
			//}
			
//			str.setLength(0);
//			str.append("select sp_flag,curr_user,COUNT(sp_flag) num from salaryhistory where ");// and (sp_flag='06' or sp_flag='03' or sp_flag='02')
//			str.append(" ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%"+userName+";%' )) or curr_user='"+userName+"')");
//			str.append(" and salaryid=? and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3=? and ");
//			if(collectList.get(i)==null){
//				str.append(" "+collectPoint+" is null");
//			}else
//				str.append(collectPoint+" like '"+collectList.get(i)+"%'");
//			
//			if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
//				str.append(" and UserFlag='"+cound+"'");
//			}
//			str.append(" group by sp_flag,curr_user ");
//			ArrayList parmList = new ArrayList();
//			parmList.add(new Integer(salaryid));
//			parmList.add(new Integer(count));
//			RowSet rs = dao.search(str.toString(),parmList);
			
//			for(int i=0;i<collectList.size();i++){
//				long stime2=System.currentTimeMillis();
//				
//				System.out.println("t "+(System.currentTimeMillis()-stime2));
//				String bp = "0";//报批
//				String _bh = "0";//驳回到操作人
//				String bh = "0";//驳回
//				String pz = "0";//批准
//				String js = "0";//结束
//				
//				int bpnum = 0;//报批人数
//				int _bhnum = 0;//驳回到操作人人数
//				int bhnum = 0;//驳回人数
//				int pznum = 0;//批准人数
//				int jsnum = 0;//结束人数
//				int selfbpnum = 0;//当前操作人经手报上去的人数且还没批准和结束
//				while(rs.next()){
//					String sp_flag = rs.getString("sp_flag");
//					String curr_user = rs.getString("curr_user");
//					if("02".equals(sp_flag)&&userName.equals(curr_user)){//有当前操作人待审批的薪资数据
//						bp = "1";
//						bpnum+=rs.getInt("num");
//						continue;
//					}
//					if("07".equals(sp_flag)&&userName.equals(curr_user)){//有驳回到操作人的薪资数据
//						_bh = "1";
//						_bhnum+=rs.getInt("num");
//						continue;
//					}
//					if("07".equals(sp_flag)){//有驳回的薪资数据
//						bh = "1";
//						bhnum+=rs.getInt("num");
//						continue;
//					}
//					if("03".equals(sp_flag)){
//						pz = "1";
//						pznum+=rs.getInt("num");
//						continue;
//					}
//					if("06".equals(sp_flag)){
//						js = "1";
//						jsnum+=rs.getInt("num");
//						continue;
//					}
//					selfbpnum+=rs.getInt("num");//上面几种状态都不是的那肯定就是当前人经手报上去的
//				}
//				
//				StringBuffer buf = new StringBuffer();
//				if(bpnum+_bhnum>0)
//				{
//					buf.append("待批："+(bpnum+_bhnum)+"人&nbsp;&nbsp;");
//					if(_bhnum>0){
//						buf.append("驳回："+_bhnum+"人&nbsp;&nbsp;");
//					}
//				}
//				if(selfbpnum>0)
//				{
//					buf.append("已报批："+selfbpnum+"人&nbsp;&nbsp;");
//				}
//				if(pznum>0)
//				{
//					buf.append("批准："+pznum+"人&nbsp;&nbsp;");
//				}
//				if(jsnum>0)
//				{
//					buf.append("结束："+jsnum+"人");
//				}
//				if(collectList.get(i)==null)
//				{
//					map.put("num", buf.toString());
//					map.put("color", "");
//				}
//				else
//				{
//					map.put(collectList.get(i)+"num", buf.toString());
//					map.put(collectList.get(i)+"color", "");
//				}
//				if(bpnum>0)
//				{
//					if(collectList.get(i)==null)
//						map.put("color", "dealto_green.gif");
//					else
//						map.put(collectList.get(i)+"color", "dealto_green.gif");
//				}
//				if(_bhnum>0){
//					if(collectList.get(i)==null)
//						map.put("color", "dealto.gif");
//					else
//						map.put(collectList.get(i)+"color", "dealto.gif");
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取设置了单位+部门的情况下汇总指标字段 用于select和group by中作为一个字段使用
	 * @param b0110
	 * @param e0122
	 * @return
	 */
	public String getCollectPointSql(String b0110,String e0122,String tablename){
		StringBuffer buf = new StringBuffer();
		if(tablename!=null&&tablename.trim().length()>0)
			tablename+=".";
		try{
			buf.append(" case ");
			if(!"e0122".equalsIgnoreCase(e0122)&&!"b0110".equalsIgnoreCase(b0110)){//归属单位和部门均设置了
				buf.append("  when  nullif("+tablename+b0110+",'') is not null  then "+tablename+b0110+" ");
				buf.append("  when (nullif("+tablename+b0110+",'') is  null ) and nullif("+tablename+e0122+",'') is not null then "+tablename+e0122+" ");
				buf.append("  when (nullif("+tablename+b0110+",'') is  null ) and (nullif("+tablename+e0122+",'') is null) and nullif("+tablename+"b0110,'') is not null then "+tablename+"b0110 ");
				buf.append(" else "+tablename+"e0122 end ");
			}else if(!"e0122".equalsIgnoreCase(e0122)&& "b0110".equalsIgnoreCase(b0110)){//设置了归属部门，没设置归属单位
				buf.append("  when nullif("+tablename+e0122+",'') is not null then "+tablename+e0122+" ");
				buf.append("  when (nullif("+tablename+e0122+",'') is  null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
				buf.append(" else "+tablename+"b0110 end ");
			}else if("e0122".equalsIgnoreCase(e0122)&&!"b0110".equalsIgnoreCase(b0110)){//没设置归属部门，设置了归属单位
				buf.append("  when nullif("+tablename+b0110+",'') is not null then "+tablename+b0110+" ");
				buf.append("  when (nullif("+tablename+b0110+",'') is null) and nullif("+tablename+"b0110,'') is not null then "+tablename+"b0110 ");
				buf.append(" else "+tablename+"e0122 end ");
			}else if("e0122".equalsIgnoreCase(e0122)&& "b0110".equalsIgnoreCase(b0110)){//啥都没设置
				buf.append("  when nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
				buf.append(" else "+tablename+"b0110 end ");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "nullif("+buf.toString()+",'')";
	}
	/**
	 * 薪资汇总审批获取需要操作的数据范围  zhaoxg add 2015-1-22
	 * @param record
	 * @param collectPoint
	 * @return
	 */
	public String getCollectSPPriv(SalaryTemplateBo gzbo,String record,String cound,String collectPoint,String salaryid,String bosdate,String count){
		StringBuffer str = new StringBuffer();
		try{
			StringBuffer buf=new StringBuffer();
			String b0110 = "b0110";
			String e0122 = "e0122";
			if("UNUM".equals(collectPoint)){//单位+部门
				SalaryCtrlParamBo ctrlparam = gzbo.getCtrlparam();
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(orgid.length()>0){
					b0110 = orgid;
				}
				if(deptid.length()>0){
					e0122 = deptid;
				}
				collectPoint = this.getCollectPointSql(b0110, e0122,"");
			}else //可能出现指标为空和null的
				collectPoint = "nullif("+collectPoint+",'')";
			
			String[] records = record.split("#");
			for(int i=0;i<records.length;i++){
				if(records[i]==null||records[i].trim().length()==0)
					continue;
				
				if("null".equals(records[i])){
					buf.append(" or  "+collectPoint+" is null");
				}else{
					buf.append(" or  "+collectPoint+" like '");
					buf.append(records[i]);
					buf.append("%'");
				}
			}
			if(!"sum".equalsIgnoreCase(record)&&buf.toString().length()>2)
				str.append(" and ("+buf.substring(3)+")");
			str.append(" and ( curr_user='"+this.userView.getUserName()+"') and (sp_flag='02' or sp_flag='07')");
			bosdate=bosdate.replaceAll("\\.","-");
			str.append(" and salaryid='"+salaryid+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3="+count+"");
			if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
				str.append(" and UserFlag='"+cound+"'");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();
	}
	/**
	 * 
	 * @Title: getCollectSubmitPriv   
	 * @Description: 薪资汇总审批，提交功能权限范围语句
	 * @param @param gzbo
	 * @param @param record
	 * @param @param cound
	 * @param @param collectPoint
	 * @param @param salaryid
	 * @param @param bosdate
	 * @param @param count
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String getCollectSubmitPriv(SalaryTemplateBo gzbo,String record,String cound,String collectPoint,String salaryid,String bosdate,String count){
		StringBuffer str = new StringBuffer();
		try{
			StringBuffer buf=new StringBuffer();
			String b0110 = "b0110";
			String e0122 = "e0122";
			if("UNUM".equals(collectPoint)){//单位+部门
				SalaryCtrlParamBo ctrlparam = gzbo.getCtrlparam();
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(orgid.length()>0){
					b0110 = orgid;
				}
				if(deptid.length()>0){
					e0122 = deptid;
				}
				collectPoint = this.getCollectPointSql(b0110, e0122,"");
			}else
				collectPoint = "nullif("+collectPoint+",'')";//可能出现指标为空和null的
			
			//提交的时候可以不选择进行提交
			if(StringUtils.isNotBlank(record)) {
				String[] records = record.split("#");
				for(int i=0;i<records.length;i++){
					if("null".equals(records[i])){//这是未维护项
						buf.append(" or  "+collectPoint+" is null");
					}else{
						buf.append(" or  "+collectPoint+" like '");
						buf.append(records[i]);
						buf.append("%'");
					}
				}
				if(!"sum".equalsIgnoreCase(record) && buf.length() > 0)
					str.append(" and ("+buf.substring(3)+")");
			}
			
			str.append(" and ((AppUser is null  "+gzbo.getWhlByUnits("salaryhistory", true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )");
			str.append(" and   sp_flag='03' ");
			// a00z2 索引，如果用to_date(XXXX, 'yyyy-MM-dd'),并且是等于的时候，则会全表扫描，数据量特别大的时候，直接执行不了，卡住
			str.append(" and salaryid='"+salaryid+"' and " + Sql_switcher.dateToChar("a00z2", "yyyy-MM-dd") + "='" + bosdate + "' and a00z3="+count+"");
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();
	}

	/**
	 *
	 * 获取待审批的数据条数 包括待批和退回的
	 * @param salaryid
	 * @param bosdate
	 * @param count
	 * @author ZhangHua
	 * @date 2019/11/28
	*/
	public int getRemainderNumber(String tableName, String salaryid, String bosdate, String count) throws GeneralException {

		StringBuilder str=new StringBuilder();
		str.append(" select count(*) as num from ");
		if("salaryhistory".equalsIgnoreCase(tableName)) {
			str.append("salaryhistory where ");
			str.append(" Curr_user = '" + this.userView.getUserName() + "'");
			str.append(" and   sp_flag in ('02','07') ");
			str.append(" and salaryid='" + salaryid + "'");
			str.append(" and a00z2=" + Sql_switcher.dateValue(bosdate) + " and a00z3=" + count );
		}else{
			str.append(tableName);
			str.append(" where sp_flag in ('01','07') ");
		}

		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		int num=0;
		try{
			rs=dao.search(str.toString());
			if(rs.next()){
				num=rs.getInt("num");
			}
		}catch (Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return num;
	}



}
