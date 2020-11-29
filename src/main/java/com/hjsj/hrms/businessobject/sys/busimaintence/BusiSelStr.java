package com.hjsj.hrms.businessobject.sys.busimaintence;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class BusiSelStr {
	private String sysvalue;
	public String getCodeStr(ContentDAO dao,String sel) throws GeneralException{
		StringBuffer sbsel=new StringBuffer();
		sbsel.append("<select id=\"code\" name=\"busiFieldVo.string(codesetid)\"  onKeypress=\"press(this);\" onchange=\"getcodelen();\" style=\"width:150px\">");
		ArrayList codesetlist=this.getCodesetList(dao);
		sbsel.append("<option value=\"\">");
		sbsel.append("");
		sbsel.append("</option>");
		String[] tempsel=sel.split("/");
		for(int i=0;i<codesetlist.size();i++){
			DynaBean dynabean=(DynaBean) codesetlist.get(i);
			String codesetid=(String) dynabean.get("codesetid");
			String codesetdesc=(String) dynabean.get("codesetdesc");
			String len=(String) dynabean.get("codesetlen");
			if(tempsel[0].equalsIgnoreCase(codesetid)){
				sbsel.append("<option value=\""+codesetid+"/"+len+"\" selected=\"selected\">");
			}else{
				sbsel.append("<option value=\""+codesetid+"/"+len+"\">");
			}
			sbsel.append("("+codesetid+")"+codesetdesc);
			sbsel.append("</option>");
		}
		sbsel.append("<option value=\"newcode\">");
		sbsel.append(ResourceFactory.getProperty("codemaintence.codeitem.add")); //新建代码
		sbsel.append("</option>");
		sbsel.append("</select>");
		return sbsel.toString();
	}
	public ArrayList getFieldItemList(){
		ArrayList sysfieldlist=AdminCode.getCodeItemList() ;
		return sysfieldlist;
	}
	public ArrayList getCodesetList(ContentDAO dao) throws GeneralException{
		
		String sql="select * from codeset order by codesetid asc";
		ArrayList codesetlist=(ArrayList) ExecuteSQL.executeMyQuery(sql);
		ArrayList recodesetlist=new ArrayList();
		for(int i=0;i<codesetlist.size();i++){
			DynaBean dynabean=(DynaBean) codesetlist.get(i);
			String codesetid=(String) dynabean.get("codesetid");
			dynabean.set("codesetlen",this.getChildLen(dao,codesetid));
			recodesetlist.add(dynabean);
		}
		return recodesetlist;
	}
	//关联代码长度
	public String getChildLen(ContentDAO dao,String codesetid) throws GeneralException {
		String len = "";
		try {
//			RowSet rs = dao
//					.search("select max(displaywidth) as len from fielditem where codesetid='"
//							+ codesetid + "'");
//			if (rs.next()) {
//				int is = rs.getInt("len");
//				len = new Integer(is).toString();
//			}
			if("@K".equals(codesetid)|| "UM".equals(codesetid)|| "UN".equals(codesetid)){
//				RowSet rs = dao.search("select max("+Sql_switcher.length("codeitemid")+") as len from Organization where codesetid = '"+codesetid+"'");
//				if(rs.next()){
//					int is = rs.getInt("len");
//					len = new Integer(is).toString();
//				}
				len = "30";
			} else if ("@@".equals(codesetid)){
				len = "3";
			}else {
				RowSet rs = dao
				.search("select MAX("+Sql_switcher.length("codeitemid")+") as len from codeitem where codesetid='"
						+ codesetid + "'");
					if (rs.next()) {
						int is = rs.getInt("len");
						len = new Integer(is).toString();
					}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return len;
	}
	public String getRelatingCode(ContentDAO dao,String sel) throws GeneralException,SQLException{
		StringBuffer sbsel=new StringBuffer();
		sbsel.append("<select id=\"rcode\" name=\"busiFieldVo.string(reserveitem)\" onchange=\"getrelatingcodelen();\" style=\"width:150px\">");
//		sbsel.append("<select id=\"rcode\" name=\"busiFieldVo.string(itemlength)\" onchange=\"getrelatingcodelen();\">");
		RowSet rs=this.getRelatingCodeList(dao);
		sbsel.append("<option value=\"\">");
		sbsel.append("");
		sbsel.append("</option>");
		String[] tempsel=sel.split("/");
		while(rs.next()){
			String itemdesc=rs.getString("itemdesc");
			String codesetid=rs.getString("codesetid");
			String itemlength=rs.getString("itemlength");
			if(tempsel[0].equalsIgnoreCase(codesetid)){
				sbsel.append("<option value=\""+codesetid+"/"+itemlength+"\" selected=\"selected\">");
			}else{
				sbsel.append("<option value=\""+codesetid+"/"+itemlength+"\">");
			}
			sbsel.append(codesetid+" "+itemdesc);
			sbsel.append("</option>");
		}
		//String dev_flag=SystemConfig.getProperty("dev_flag");
		
		try{
			String dev_flag=SystemConfig.getProperty("dev_flag");
			if(dev_flag==null|| "".equals(dev_flag)|| "0".equals(dev_flag))
			{
				
			}
			else
			{
		    	sbsel.append("<option value=\"newrelating\">");
		    	sbsel.append(ResourceFactory.getProperty("codemaintence.codeset.add")); //新建代码类
			    sbsel.append("</option>");
			}
			sbsel.append("</select>");
		}catch(Exception e){
			
		}
		return sbsel.toString();
	}
	public RowSet getRelatingCodeList(ContentDAO dao) throws SQLException{
//		String sql="select * from t_hr_relatingcode thr left join" +
//				" (select * from t_hr_busifield) thb on thr.codevalue=" +
//				"thb.itemid and thr.codetable=thb.fieldsetid";
	    StringBuffer sql = new StringBuffer();
		sql.append("select itemdesc,codesetid,MAX(itemlength) as itemlength"); 
		sql.append(" from (select thb.itemdesc,thr.codesetid,fds.itemlength from t_hr_relatingcode thr left join");
		sql.append(" (select * from t_hr_busifield) thb on thr.codedesc=");
		sql.append("thb.itemid and thr.codetable=thb.fieldsetid");
		sql.append(" left join (select * from t_hr_busifield) fds on  thr.codevalue=fds.itemid) A");
		sql.append(" group by itemdesc,codesetid order by codesetid");
		
		RowSet rs=dao.search(sql.toString());
		return rs;
	}
	public String getDateSel(String len){
		StringBuffer sbdate=new StringBuffer();
		sbdate.append("<select name=\"date\" onchange=\"getdatelen();\" style=\"width:150px;\">");
		//sbdate.append("<option value=\"\">");
		//sbdate.append("");
		//sbdate.append("</option>");
		if("18".equals(len)){
			sbdate.append("<option value=\"18\" selected=\"selected\">YYYY.MM.DD HH:MM:SS</option>");
		}else{
			sbdate.append("<option value=\"18\">YYYY.MM.DD HH:MM:SS</option>");
		}
		if("16".equals(len)){
			sbdate.append("<option value=\"16\" selected=\"selected\">YYYY.MM.DD HH:MM</option>");
		}else{
			sbdate.append("<option value=\"16\">YYYY.MM.DD HH:MM</option>");
		}
		if("10".equals(len)){
			sbdate.append("<option value=\"10\" selected=\"selected\">YYYY.MM.DD</option>");
		}else{
			sbdate.append("<option value=\"10\">YYYY.MM.DD</option>");
		}
		if("7".equals(len)){
			sbdate.append("<option value=\"7\" selected=\"selected\">YYYY.MM</option>");
		}else{
			sbdate.append("<option value=\"7\">YYYY.MM</option>");
		}
		if("4".equals(len)){
			sbdate.append("<option value=\"4\" selected=\"selected\">YYYY</option>");
		}else{
			sbdate.append("<option value=\"4\">YYYY</option>");
		}
		
		sbdate.append("</select>");

		
		return sbdate.toString();
	}
	public static String  getInfoGroup(String[] classper){
		String sql="select * from informationclass";
		StringBuffer sbcheck=new StringBuffer();
		ArrayList infolist = (ArrayList) ExecuteSQL.executeMyQuery(sql);
		HashMap chm=new HashMap();
		for(int j=0;j<classper.length;j++){
			String classpre=classper[j];
			chm.put(classpre,classpre);
		}
		for(int i=0;i<infolist.size();i++){
			DynaBean dynabean=(DynaBean) infolist.get(i);
			String classpre=(String) dynabean.get("classpre");
			String classname=(String)dynabean.get("classname");		
				if(chm.containsKey(classpre)){
					sbcheck.append("<input type='checkbox' name='classper' value='"+classpre+"' checked='true'/> "+classname);
				}else{
					sbcheck.append("<input type='checkbox' name='classper' value='"+classpre+"'/> "+classname);
				}
			
		}
		
		return sbcheck.toString();
	}
	//导入指标头
	public static String getGroup(String abkflag){
		StringBuffer sbsel=new StringBuffer();
		sbsel.append("<select name='abkfalg' onchange='getfield();'>");
		if("a".equals(abkflag)|| "A".equals(abkflag)){
			if("a".equals(abkflag)){             //A,B,K改为大写;ora库不支持小写;
				sbsel.append("<option value='A' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='A'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}
			if("b".equals(abkflag)){
				sbsel.append("<option value='B' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='B'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}
			if("k".equals(abkflag)){
				sbsel.append("<option value='K' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='K'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}		
			sbsel.append("<select>");
		}else if("b".equals(abkflag)|| "B".equals(abkflag)){
			if("b".equals(abkflag)){
				sbsel.append("<option value='B' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='B'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}
			if("a".equals(abkflag)){             //A,B,K改为大写;ora库不支持小写;
				sbsel.append("<option value='A' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='A'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}
			if("k".equals(abkflag)){
				sbsel.append("<option value='K' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='K'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}		
			sbsel.append("<select>");
		}else if("k".equals(abkflag)|| "K".equals(abkflag)){
			if("k".equals(abkflag)){
				sbsel.append("<option value='K' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='K'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.postinformation"));	sbsel.append("</option>");
			}
			if("a".equals(abkflag)){             //A,B,K改为大写;ora库不支持小写;
				sbsel.append("<option value='A' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='A'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.userinformation"));	sbsel.append("</option>");
			}
			if("b".equals(abkflag)){
				sbsel.append("<option value='B' selected='selected'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}else{
				sbsel.append("<option value='B'>");	sbsel.append(ResourceFactory.getProperty("kjg.title.unitinformation"));	sbsel.append("</option>");
			}
			sbsel.append("<select>");
		}
		
		return sbsel.toString();		
	}
	public static String getFieldset(String abkflag,String fieldesetid){
		//String sql="select distinct fieldsetid  from fielditem where fieldsetid like'"+abkflag+"%' and useflag='1'";
		//【7407】系统管理：库结构/业务字典，导入指标弹出框中，显示的是主集指标，但是显示的却是子集名称  jingq upd 2015.02.11
		String sql="select fieldsetid  from fieldset where fieldsetid like'"+abkflag+"%' order by displayorder";
		ArrayList fieldlist = (ArrayList) ExecuteSQL.executeMyQuery(sql);
		StringBuffer sbsel=new StringBuffer();
		sbsel.append("<select name='itemid' onchange='getitem();'>");
		for(int i=0;i<fieldlist.size();i++){
			DynaBean dynabean=(DynaBean) fieldlist.get(i);
			String fieldset=(String) dynabean.get("fieldsetid");
			FieldSet fielditem=DataDictionary.getFieldSetVo(fieldset);
			if(fielditem!=null){
				String fielddesc=fielditem.getFieldsetdesc();
				if(fieldset.equals(fieldesetid)){
					sbsel.append("<option value='"+fieldset+"' selected='selected'>");
				}else{
					sbsel.append("<option value='"+fieldset+"'>");
				}
				sbsel.append(fielddesc);
				sbsel.append("</option>");
			}
			
		}
		sbsel.append("</select>");
		
		return sbsel.toString();
		
	}
	public ArrayList getSubsys(ContentDAO dao,String name) throws GeneralException{
		String sql="select * from t_hr_subsys where is_available='1'";
		if(name!=null&&name.length()>0){
			sql="select * from t_hr_subsys where is_available='1' and id='"+name+"'";
		}
		ArrayList dynabeanlist=dao.searchDynaList(sql);
		ArrayList syselist=new ArrayList();
		
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean) dynabeanlist.get(i);
			if(i==0){
				this.setSysvalue((String)dynabean.get("id"));
			}
			CommonData dataobj = new CommonData((String) dynabean.get("id"), (String)dynabean.get("name"));
			syselist.add(dataobj);
		}
		
		return syselist;
	}
	public ArrayList getzijiStr(ContentDAO dao,String sysvalue,String operation) throws GeneralException{
		String sql="select *  from t_hr_busiTable where id='"+sysvalue+"' and useflag='"+operation+"' order by displayorder";
		
		ArrayList zijilist=new ArrayList();
		ArrayList dynabeanlist=dao.searchDynaList(sql);
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
//			CommonData dataobj = new CommonData((String) dynabean.get("fieldsetid"), (String)dynabean.get("fieldsetdesc"));
			CommonData dataobj = new CommonData((String) dynabean.get("fieldsetid"), (String)dynabean.get("customdesc"));
			zijilist.add(dataobj);
		}
		return zijilist;
	}
	public ArrayList getzijiStr(ContentDAO dao,String sysvalue) throws GeneralException{
		String sql="select *  from t_hr_busiTable where id='"+sysvalue+"'";
		
		ArrayList zijilist=new ArrayList();
		ArrayList dynabeanlist=dao.searchDynaList(sql);
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
//			CommonData dataobj = new CommonData((String) dynabean.get("fieldsetid"), (String)dynabean.get("fieldsetdesc"));
			CommonData dataobj = new CommonData((String) dynabean.get("fieldsetid"), (String)dynabean.get("customdesc"));
			zijilist.add(dataobj);
		}
		return zijilist;
	}
	public String getSysvalue() {
		return sysvalue;
	}
	public void setSysvalue(String sysvalue) {
		this.sysvalue = sysvalue;
	}
	public ArrayList getItemStr(ContentDAO dao,String zijivalue) throws GeneralException{
		String sql="select * from t_hr_busifield where fieldsetid='"+zijivalue+"'";
		ArrayList itemlist=new ArrayList();
		ArrayList dynabeanlist=dao.searchDynaList(sql);
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			CommonData dataobj = new CommonData((String) dynabean.get("itemid"), (String)dynabean.get("itemdesc"));
			itemlist.add(dataobj);
		}
		return itemlist;
	}
	public ArrayList getMList(Connection conn)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name,description from t_hr_subsys where  is_available='1'";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				CommonData data = new CommonData(rs.getString("id"),rs.getString("name"));
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getMname(Connection conn,String id)
	{
		String list ="";
		try
		{
			String sql = "select id,name,description from t_hr_subsys where  is_available='1' and id='"+id+"'";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				list = rs.getString("name");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/*
	 * 计算指标代号
	 */
	public String getSetid(Connection conn,String userType)
	{
		String setid = "";
		String ret="";
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select fieldsetid,(select MAX(fieldsetid) from t_hr_BusiTable) as AAA from t_hr_BusiTable";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next()){
				list.add(rs.getString(1));
				ret = rs.getString(2);
			}
			setid = makeSerialnum(ret,userType,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return setid;
	}
	/*
	 * 根据userType获取id
	 * jingq 2015.01.26
	 */
	private int num = 0;
	public String makeSerialnum(String str,String userType,ArrayList list){
		if(num<=100){
			num++;
			String ascii = "";
			String index = "";
			if(str==""||str==null){
				if("".equals(userType)||"0".equals(userType)){
					ascii = "ZA0";
				} else if("1".equals(userType)){
					ascii = "Z02";
				}
			} else {
				index = str.substring(0,1);
				String strs = str.substring(0, 3);
				char[] chars = strs.toCharArray();
				if(chars.length==3){
					if("".equals(userType)||"0".equals(userType)){//用户模式
						if(chars[1]=='W'){
							if(chars[2]=='9'){
								chars[2] = 'A';
							} else if(chars[2]=='Z'){
								chars[2] = '0';
								chars[1] = 'A';
								chars[0] = (char) (chars[0]+1);
							} else {
								chars[2] = (char) (chars[2]+1);
							}
						} else if(chars[1]>='0'&&chars[1]<='9'){
							chars[2] = '0';
							chars[1] = 'A';
						} else {
							if(chars[2]=='9'){
								chars[2] = 'A';
							} else if(chars[2]=='Z'){
								chars[2] = '0';
								chars[1] = (char) (chars[1]+1);
							} else {
								chars[2] = (char) (chars[2]+1);
							}
						}
					} else if("1".equals(userType)){//开发模式
						if(chars[1]=='9'){
							if(chars[2]=='9'){
								chars[2] = 'A';
							} else if(chars[2]=='Z'){
								chars[2] = '0';
								chars[1] = '0';
								chars[0] = (char) (chars[0]+1);
							} else {
								chars[2] = (char) (chars[2]+1);
							}
						} else if(chars[1]>='0'&&chars[1]<'9'){
							if(chars[2]=='9'){
								chars[2] = 'A';
							} else if(chars[2]=='Z'){
								chars[2] = '0';
								chars[1] = (char) (chars[1]+1);
							} else {
								chars[2] = (char) (chars[2]+1);
							}
						} else {
							chars[2] = '0';
							chars[1] = '0';
							chars[0] = (char) (chars[0]+1);
						}
					}
				}
				ascii = String.valueOf(chars);
			}
			if(ascii.indexOf("[")!=-1){
				if("".equals(userType)||"0".equals(userType)){
					ascii = index+"A0";
					ascii = makeSerialnum(ascii,userType,list);
				} else if("1".equals(userType)){
					ascii = index+"00";
					ascii = makeSerialnum(ascii,userType,list);
				}
			}
			if(list.contains(ascii)){
				ascii = makeSerialnum(ascii,userType,list);
			}
			return ascii;
		} else {
			return "";
		}
	}
	
	public int getMaxDisplayOrder(Connection conn)
	{
		int n=0;
		try
		{
			String sql = "select MAX(displayorder) as displayorder from t_hr_BusiTable ";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("displayorder");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n+1;
	}
	public boolean isHaveFieldsetid(String setdesc,String fieldsetid,Connection conn)
	{
		boolean flag = false;
		try
		{
			String sql = "select fieldsetid from t_hr_BusiTable where UPPER(fieldsetid)='"+fieldsetid.toUpperCase()+"' or UPPER(fieldsetdesc)='"+setdesc+"'";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs = dao.search(sql);
			if(rs.next())
			{
				flag = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public boolean isDeleteFieldSet(String fieldSetid,Connection conn)
	{
		boolean flag = true;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select useflag from t_hr_BusiTable where UPPER(fieldsetid)='"+fieldSetid.toUpperCase()+"'";
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				if("1".equalsIgnoreCase(rs.getString("useflag"))) {
                    flag=false;
                }
			}
			if(flag)
			{
				sql = " delete from t_hr_BusiTable where UPPER(fieldsetid)='"+fieldSetid+"'";
				dao.delete(sql,new ArrayList());
				sql = "delete from t_hr_BusiField where UPPER(fieldsetid)='"+fieldSetid+"'";
				dao.delete(sql,new ArrayList());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	public void createFieldByChangeFlag(String mid,String fieldsetid,String changeflag,Connection conn)
	{
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			if(!"35".equalsIgnoreCase(mid)&&("1".equalsIgnoreCase(changeflag)|| "2".equalsIgnoreCase(changeflag)))//按月变化子集
			{
				RecordVo vo = new RecordVo("t_hr_busifield");
				vo.setString("fieldsetid",fieldsetid);
				vo.setString("itemid",fieldsetid.toUpperCase()+"Z0");
				vo.setInt("display",0);
				vo.setString("itemdesc", ResourceFactory.getProperty("hmuster.label.nybs"));//年月标识
				vo.setString("itemtype","D");
				vo.setInt("itemlength", 10);
				vo.setInt("decimalwidth",0);
				vo.setString("codesetid","0");
				vo.setInt("displaywidth",14);
				vo.setString("useflag","0");
				vo.setString("ownflag","1");
				dao.addValueObject(vo);
				RecordVo vo2 = new RecordVo("t_hr_busifield");
				vo2.setString("fieldsetid",fieldsetid);
				vo2.setString("itemid",fieldsetid.toUpperCase()+"Z1");
				vo2.setInt("display",1);
				vo2.setString("itemdesc", ResourceFactory.getProperty("hmuster.label.counts")); //次数
				vo2.setString("itemtype","N");
				vo2.setInt("itemlength",3);
				vo2.setInt("decimalwidth",0);
				vo2.setString("codesetid","0");
				vo2.setInt("displaywidth",10);
				vo2.setString("useflag","0");
				vo2.setString("ownflag","1");
				dao.addValueObject(vo2);		
			}
			if("35".equalsIgnoreCase(mid)&&("1".equalsIgnoreCase(changeflag)|| "2".equalsIgnoreCase(changeflag)))
			{
				RecordVo vo = new RecordVo("t_hr_busifield");
				vo.setString("fieldsetid",fieldsetid);
				vo.setString("itemid",fieldsetid.toUpperCase()+"Z0");
				vo.setInt("display",0);
				vo.setString("itemdesc", ResourceFactory.getProperty("hmuster.label.nybs"));//年月标识
				vo.setString("itemtype","D");
				vo.setInt("itemlength", 10);
				vo.setInt("decimalwidth",0);
				vo.setString("codesetid","0");
				vo.setInt("displaywidth",14);
				vo.setString("useflag","0");
				vo.setString("ownflag","1");
				vo.setString("state","1");
				vo.setString("keyflag","1");
				vo.setString("codeflag","0");
				dao.addValueObject(vo);
				RecordVo vo2 = new RecordVo("t_hr_busifield");
				vo2.setString("fieldsetid",fieldsetid);
				vo2.setString("itemid","Nbase");
				vo2.setInt("display",1);
				vo2.setString("itemdesc", ResourceFactory.getProperty("label.query.dbpre")); //人员库
				vo2.setString("itemtype","A");
				vo2.setInt("itemlength", 3);
				vo2.setInt("decimalwidth",0);
				vo2.setString("codesetid","@@");
				vo2.setInt("displaywidth",10);
				vo2.setString("useflag","0");
				vo2.setString("ownflag","1");
				vo2.setString("state","1");
				vo2.setString("keyflag","1");
				vo2.setString("codeflag","0");
				dao.addValueObject(vo2);
				RecordVo vo3 = new RecordVo("t_hr_busifield");
				vo3.setString("fieldsetid",fieldsetid);
				vo3.setString("itemid","CondID");
				vo3.setInt("display",3);
				vo3.setString("itemdesc", ResourceFactory.getProperty("gz.formula.project")); //项目
				vo3.setString("itemtype","N");
				vo3.setInt("itemlength", 8);
				vo3.setInt("decimalwidth",0);
				vo3.setString("codesetid","0");
				vo3.setInt("displaywidth",10);
				vo3.setString("useflag","0");
				vo3.setString("ownflag","1");
				vo3.setString("state","1");
				vo3.setString("keyflag","1");
				vo3.setString("codeflag","0");
				dao.addValueObject(vo3);
				RecordVo vo4 = new RecordVo("t_hr_busifield");
				vo4.setString("fieldsetid",fieldsetid);
				vo4.setString("itemid","CondClassCode");
				vo4.setInt("display",2);
				vo4.setString("itemdesc", ResourceFactory.getProperty("conlumn.mediainfo.info_title"));  //分类
				vo4.setString("itemtype","A");
				vo4.setInt("itemlength", 50);
				vo4.setInt("decimalwidth",0);
				vo4.setString("codesetid","0");
				vo4.setInt("displaywidth",10);
				vo4.setString("useflag","0");
				vo4.setString("ownflag","1");
				vo4.setString("state","1");
				vo4.setString("keyflag","1");
				vo4.setString("codeflag","0");
				dao.addValueObject(vo4);
				RecordVo vo5 = new RecordVo("t_hr_busifield");
				vo5.setString("fieldsetid",fieldsetid);
				vo5.setString("itemid","DataSrc");
				vo5.setInt("display",4);
				vo5.setString("itemdesc", ResourceFactory.getProperty("kjg.title.datasources"));  //数据来源
				vo5.setString("itemtype","A");
				vo5.setInt("itemlength", 2);
				vo5.setInt("decimalwidth",0);
				vo5.setString("codesetid","52");
				vo5.setInt("displaywidth",10);
				vo5.setString("useflag","0");
				vo5.setString("ownflag","1");
				vo5.setString("state","1");
				vo5.setString("keyflag","1");
				vo5.setString("codeflag","0");
				dao.addValueObject(vo5);
			}
		}
		catch(Exception e)
		{
			
		}
	}
	public boolean isExist(String itemid,String setid,String itemdesc,Connection conn)
	{
		boolean flag = true;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select * from t_hr_busifield ");
			buf.append(" where UPPER(itemdesc)='");
			buf.append(itemdesc.toUpperCase()+"' and UPPER(itemid)<>'");
			buf.append(itemid.toUpperCase()+"'");
			buf.append(" and UPPER(fieldsetid)='"+setid.toUpperCase()+"'");
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				flag = false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/*
	 * 子集按displayorder排序
	 */
	public int initial(String mid,Connection conn){
		String ret="";
		int re = 0;
		try{
			String sql = "SELECT MAX(displayorder) AS AAA FROM t_hr_busitable WHERE id like '"+mid+"%'";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs= dao.search(sql);
			if (rs.next()) {
                ret = rs.getString(1);
            }
				if(ret==null||"null".equals(ret))
				{
					re=0;
				}else{
					re = Integer.parseInt(ret)+1;
				}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return re;
	}
	/*
	 * 指标按 displayid 排序 
	 */
	public int initfield(String setid,Connection conn){
		String ret="";
		int res = 0;
		try{
			String sql="SELECT MAX(displayid) AS AAA FROM t_hr_busifield WHERE fieldsetid='"+setid+"'";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs= dao.search(sql);
			if(rs.next()){
				ret = rs.getString(1);
				if(ret==null||"null".equals(ret))
				{
					res=0;
				}else{
					res = Integer.parseInt(ret)+1;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	/*
	 * 插入子集
	 */
	public void setmuster(String userType,String SetId,String mid,String changeflag,String setdesc,int Sid,int Sids,Connection conn){
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer sql3 = new StringBuffer();
		if(!"35".equalsIgnoreCase(mid)){
			sql.append("insert into t_hr_busitable (fieldsetid,id,fieldsetdesc,customdesc,useflag,changeflag,displayorder,ownflag)");
			sql.append(" values('");
			sql.append(SetId);
			sql.append("','");
			sql.append(mid);
			sql.append("','");
			sql.append(setdesc);
			sql.append("','");
			sql.append(setdesc);
			sql.append("','");
			sql.append(0);
			sql.append("','");
			sql.append(changeflag);
			sql.append("','");
			sql.append(Sid);
			sql.append("','");
			sql.append(userType);
			sql.append("')");
			if("1".equals(changeflag)|| "2".equals(changeflag)){
				try {
					String dev=SystemConfig.getProperty("dev_flag");
				
				//StringBuffer sqls = new StringBuffer();
					sql2.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
					sql2.append(" values('");
					sql2.append(SetId);
					sql2.append("','");
					sql2.append(SetId+"Z0");
					sql2.append("','");
					sql2.append(Sids);
					sql2.append("','");
					sql2.append("D");
					sql2.append("','");
					sql2.append(ResourceFactory.getProperty("hmuster.label.nybs"));  
					sql2.append("','");
					sql2.append(10);
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append("NULL");
					sql2.append("','");
					sql2.append(14);
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append("NULL");
					sql2.append("','");
					sql2.append("NULL");
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append("NULL");
					sql2.append("','");
					sql2.append(0);
					sql2.append("','");
					sql2.append(dev);
					sql2.append("')");
					sql3.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
					sql3.append(" values('");
					sql3.append(SetId);
					sql3.append("','");
					sql3.append(SetId+"Z1");
					sql3.append("','");
					sql3.append(Sids+1);
					sql3.append("','");
					sql3.append("N");
					sql3.append("','");
					sql3.append(ResourceFactory.getProperty("hmuster.label.counts"));
					sql3.append("','");
					sql3.append(3);
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append("NULL");
					sql3.append("','");
					sql3.append(10);
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append("NULL");
					sql3.append("','");
					sql3.append("NULL");
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append("NULL");
					sql3.append("','");
					sql3.append(0);
					sql3.append("','");
					sql3.append(dev);
					sql3.append("')");
					try {
						dao.insert(sql2.toString(), new ArrayList());
						FieldItem fielditem = new FieldItem();
						fielditem.setFieldsetid(SetId);
				        fielditem.setItemid((SetId+"Z0").toLowerCase());
				        fielditem.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs").replace("\"", "“"));
				        fielditem.setCodesetid("0");
				        fielditem.setExplain("");
				        fielditem.setDecimalwidth(0);
				        fielditem.setDisplaywidth(10);
				        fielditem.setUseflag("0");
				        fielditem.setFillable(false);
				        String state = "1";
				        fielditem.setState(state);
				        if ("0".equalsIgnoreCase(state)) {
                            fielditem.setVisible(false);
                        }
						fielditem.setItemlength(10);
					    fielditem.setItemtype("D");
						DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
						dao.insert(sql3.toString(), new ArrayList());
						
						fielditem = new FieldItem();
						fielditem.setFieldsetid(SetId);
				        fielditem.setItemid((SetId+"Z1").toLowerCase());
				        fielditem.setItemdesc(ResourceFactory.getProperty("hmuster.label.counts").replace("\"", "“"));
				        fielditem.setCodesetid("0");
				        fielditem.setExplain("");
				        fielditem.setDecimalwidth(0);
				        fielditem.setDisplaywidth(10);
				        fielditem.setUseflag("0");
				        fielditem.setFillable(false);
				        state = "1";
				        fielditem.setState(state);
				        if ("0".equalsIgnoreCase(state)) {
                            fielditem.setVisible(false);
                        }
						fielditem.setItemlength(3);
					    fielditem.setItemtype("N");
						DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				dao.insert(sql.toString(), new ArrayList());
				
				FieldSet fieldset = new FieldSet(SetId);
		        fieldset.setFieldsetid(SetId.toUpperCase());
		        fieldset.setFieldsetdesc(setdesc.replaceAll("\"","“"));
		        fieldset.setCustomdesc(setdesc.replaceAll("\"","“"));
		        fieldset.setUseflag("0");
		        fieldset.setChangeflag(setdesc);
		        fieldset.setDisplayorder(Sid);
				DataDictionary.addFieldSet(SetId.toUpperCase(), fieldset);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("35".equalsIgnoreCase(mid)){
			try{
				RecordVo vo = new RecordVo("t_hr_busitable");
				vo.setString("fieldsetid", SetId);
				vo.setString("id", mid);
				vo.setString("fieldsetdesc", setdesc);
				vo.setString("customdesc", setdesc);
				vo.setString("useflag", "0");
				vo.setString("changeflag", changeflag);
				vo.setInt("displayorder", Sid);
				vo.setString("ownflag", userType);
				dao.addValueObject(vo);
				FieldSet fieldset = new FieldSet(SetId);
		        fieldset.setFieldsetid(SetId.toUpperCase());
		        fieldset.setFieldsetdesc(setdesc.replaceAll("\"","“"));
		        fieldset.setCustomdesc(setdesc.replaceAll("\"","“"));
		        fieldset.setUseflag("0");
		        fieldset.setChangeflag(setdesc);
		        fieldset.setDisplayorder(Sid);
				DataDictionary.addFieldSet(SetId.toUpperCase(), fieldset);
				if("1".equals(changeflag)|| "2".equals(changeflag)){
					RecordVo vo1 =new RecordVo("t_hr_busifield");
					vo1.setString("fieldsetid",SetId);
					vo1.setString("itemid",SetId.toUpperCase()+"Z0");
					vo1.setInt("displayid", 0);
					vo1.setString("itemdesc", ResourceFactory.getProperty("hmuster.label.nybs"));
					vo1.setString("itemtype","D");
					vo1.setInt("itemlength", 10);
					vo1.setInt("decimalwidth",0);
					vo1.setString("codesetid","0");
					vo1.setInt("displaywidth",14);
					vo1.setString("useflag","0");
					vo1.setString("ownflag", userType);
					vo1.setString("state","1");
					vo1.setString("keyflag","1");
					vo1.setString("codeflag","0");
					dao.addValueObject(vo1);
					FieldItem fielditem = new FieldItem();
					fielditem.setFieldsetid(SetId);
			        fielditem.setItemid((SetId+"Z0").toLowerCase());
			        fielditem.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs").replace("\"", "“"));
			        fielditem.setCodesetid("0");
			        fielditem.setExplain("");
			        fielditem.setDecimalwidth(0);
			        fielditem.setDisplaywidth(10);
			        fielditem.setUseflag("0");
			        fielditem.setFillable(false);
			        String state = "1";
			        fielditem.setState(state);
			        if ("0".equalsIgnoreCase(state)) {
                        fielditem.setVisible(false);
                    }
					fielditem.setItemlength(10);
				    fielditem.setItemtype("D");
					DataDictionary.addFieldItem(SetId.toUpperCase(), fielditem, 0);
					
					RecordVo vo2 = new RecordVo("t_hr_busifield");
					vo2.setString("fieldsetid",SetId);
					vo2.setString("itemid","Nbase");
					vo2.setInt("displayid",1);
					vo2.setString("itemdesc", ResourceFactory.getProperty("label.query.dbpre")); //人员库
					vo2.setString("itemtype","A");
					vo2.setInt("itemlength", 3);
					vo2.setInt("decimalwidth",0);
					vo2.setString("codesetid","@@");
					vo2.setInt("displaywidth",10);
					vo2.setString("useflag","0");
					vo2.setString("ownflag",userType);
					vo2.setString("state","1");
					vo2.setString("keyflag","1");
					vo2.setString("codeflag","0");
					dao.addValueObject(vo2);

					fielditem = new FieldItem();
					fielditem.setFieldsetid(SetId);
			        fielditem.setItemid("nbase".toLowerCase());
			        fielditem.setItemdesc(ResourceFactory.getProperty("label.query.dbpre").replace("\"", "“"));
			        fielditem.setCodesetid("@@");
			        fielditem.setExplain("");
			        fielditem.setDecimalwidth(0);
			        fielditem.setDisplaywidth(10);
			        fielditem.setUseflag("0");
			        fielditem.setFillable(false);
			        state = "1";
			        fielditem.setState(state);
			        if ("0".equalsIgnoreCase(state)) {
                        fielditem.setVisible(false);
                    }
					fielditem.setItemlength(3);
				    fielditem.setItemtype("A");
					DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
					
					RecordVo vo3 = new RecordVo("t_hr_busifield");
					vo3.setString("fieldsetid",SetId);
					vo3.setString("itemid","CondID");
					vo3.setInt("displayid",3);
					vo3.setString("itemdesc", ResourceFactory.getProperty("gz.formula.project")); //项目
					vo3.setString("itemtype","N");
					vo3.setInt("itemlength", 8);
					vo3.setInt("decimalwidth",0);
					vo3.setString("codesetid","0");
					vo3.setInt("displaywidth",10);
					vo3.setString("useflag","0");
					vo3.setString("ownflag",userType);
					vo3.setString("state","1");
					vo3.setString("keyflag","1");
					vo3.setString("codeflag","0");
					dao.addValueObject(vo3);
					
					fielditem = new FieldItem();
					fielditem.setFieldsetid(SetId);
			        fielditem.setItemid("condid".toLowerCase());
			        fielditem.setItemdesc(ResourceFactory.getProperty("gz.formula.project").replace("\"", "“"));
			        fielditem.setCodesetid("0");
			        fielditem.setExplain("");
			        fielditem.setDecimalwidth(0);
			        fielditem.setDisplaywidth(10);
			        fielditem.setUseflag("0");
			        fielditem.setFillable(false);
			        state = "1";
			        fielditem.setState(state);
			        if ("0".equalsIgnoreCase(state)) {
                        fielditem.setVisible(false);
                    }
					fielditem.setItemlength(8);
				    fielditem.setItemtype("N");
					DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
					
					
					
					RecordVo vo4 = new RecordVo("t_hr_busifield");
					vo4.setString("fieldsetid",SetId);
					vo4.setString("itemid","CondClassCode");
					vo4.setInt("displayid",2);
					vo4.setString("itemdesc", ResourceFactory.getProperty("conlumn.mediainfo.info_title"));  //分类
					vo4.setString("itemtype","A");
					vo4.setInt("itemlength", 50);
					vo4.setInt("decimalwidth",0);
					vo4.setString("codesetid","0");
					vo4.setInt("displaywidth",10); 
					vo4.setString("useflag","0");
					vo4.setString("ownflag",userType);
					vo4.setString("state","1");
					vo4.setString("keyflag","1");
					vo4.setString("codeflag","0");
					dao.addValueObject(vo4);
					
					fielditem = new FieldItem();
					fielditem.setFieldsetid(SetId);
			        fielditem.setItemid("CondClassCode".toLowerCase());
			        fielditem.setItemdesc(ResourceFactory.getProperty("conlumn.mediainfo.info_title").replace("\"", "“"));
			        fielditem.setCodesetid("0");
			        fielditem.setExplain("");
			        fielditem.setDecimalwidth(0);
			        fielditem.setDisplaywidth(10);
			        fielditem.setUseflag("0");
			        fielditem.setFillable(false);
			        state = "1";
			        fielditem.setState(state);
			        if ("0".equalsIgnoreCase(state)) {
                        fielditem.setVisible(false);
                    }
					fielditem.setItemlength(50);
				    fielditem.setItemtype("A");
					DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
					
					RecordVo vo5 = new RecordVo("t_hr_busifield");
					vo5.setString("fieldsetid",SetId);
					vo5.setString("itemid","DataSrc");
					vo5.setInt("displayid",4);
					vo5.setString("itemdesc", ResourceFactory.getProperty("kjg.title.datasources"));  //数据来源
					vo5.setString("itemtype","A");
					vo5.setInt("itemlength", 2);
					vo5.setInt("decimalwidth",0);
					vo5.setString("codesetid","52");
					vo5.setInt("displaywidth",10);
					vo5.setString("useflag","0");
					vo5.setString("ownflag",userType);
					vo5.setString("state","1");
					vo5.setString("keyflag","1");
					vo5.setString("codeflag","0");
					dao.addValueObject(vo5);
					
					fielditem = new FieldItem();
					fielditem.setFieldsetid(SetId);
			        fielditem.setItemid("DataSrc".toLowerCase());
			        fielditem.setItemdesc(ResourceFactory.getProperty("kjg.title.datasources").replace("\"", "“"));
			        fielditem.setCodesetid("52");
			        fielditem.setExplain("");
			        fielditem.setDecimalwidth(0);
			        fielditem.setDisplaywidth(10);
			        fielditem.setUseflag("0");
			        fielditem.setFillable(false);
			        state = "1";
			        fielditem.setState(state);
			        if ("0".equalsIgnoreCase(state)) {
                        fielditem.setVisible(false);
                    }
					fielditem.setItemlength(2);
				    fielditem.setItemtype("A");
					DataDictionary.addFieldItem((SetId).toUpperCase(), fielditem, 0);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/*
	 * 更新子集,0=一般 1=按月 2=按年
	 */
	public void rebirth(String fieldsetid,String changeflag,int Sids,String mid,Connection conn) throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		 try{
			String dev=SystemConfig.getPropertyValue("dev_flag");
			 if(dev==null||dev.length()<=0) {
                 dev="0";
             }
			if(!"35".equalsIgnoreCase(mid)){
				if("30".equalsIgnoreCase(mid))// 业务字典 id 30号 考勤管理 指标修改 子集类型时，不做任何处理
                {
                    return;
                }
				if("0".equals(changeflag)){
					
					StringBuffer buf = new StringBuffer();
					buf.append("delete from t_hr_busifield where ");
					buf.append("itemid= '"+fieldsetid+"Z0' or itemid= '"+fieldsetid+"Z1'");
					dao.delete(buf.toString(), new ArrayList());
				}else if("1".equals(changeflag)|| "2".equals(changeflag)){
					StringBuffer bufs = new StringBuffer();
					StringBuffer bufse = new StringBuffer();
					RowSet rs = null;
					String srs = null;
					int nState = 0;//1:有Z0    2:有Z1  3:有Z0和Z1
					boolean bRet = true;
					String sql = "select itemid from t_hr_busifield where itemid='"+fieldsetid+"Z0' or itemid='"+fieldsetid+"Z1'";
					rs = dao.search(sql);
					if(rs.next()&&bRet ==true){
						 srs =rs.getString(1);
						 if(srs.equals(fieldsetid+"Z0")){
							 if(nState == 0) {
                                 nState = 1;
                             } else if(nState == 2) {
                                 nState = 3;
                             }
						 }else if(srs.equals(fieldsetid+"Z1")){
							 if(nState == 1) {
                                 nState = 3;
                             } else if(nState == 0) {
                                 nState = 2;
                             }
						 }
						 while(rs.next()){
							 srs =rs.getString(1);
							 if(srs.equals(fieldsetid+"Z0")){
								 if(nState == 0) {
                                     nState = 1;
                                 } else if(nState == 2) {
                                     nState = 3;
                                 }
							 }else {
								 if(nState == 1) {
                                     nState = 3;
                                 } else if(nState == 0) {
                                     nState = 2;
                                 }
							 }
						 }
						 if(nState == 1){
							 bufse.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
								bufse.append(" values('");
								bufse.append(fieldsetid);
								bufse.append("','");
								bufse.append(fieldsetid+"Z1");
								bufse.append("','");
								bufse.append(Sids+1);
								bufse.append("','");
								bufse.append("N");
								bufse.append("','");
								bufse.append(ResourceFactory.getProperty("hmuster.label.counts")); //次数
								bufse.append("','");
								bufse.append(3);
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append("NULL");
								bufse.append("','");
								bufse.append(10);
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append("NULL");
								bufse.append("','");
								bufse.append("NULL");
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append("NULL");
								bufse.append("','");
								bufse.append(0);
								bufse.append("','");
								bufse.append(dev);
								bufse.append("')");
								dao.insert(bufse.toString(), new ArrayList());
						 }else if(nState == 2){
							 bufs.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
								bufs.append(" values('");
								bufs.append(fieldsetid);
								bufs.append("','");
								bufs.append(fieldsetid+"Z0");
								bufs.append("','");
								bufs.append(Sids);
								bufs.append("','");
								bufs.append("D");
								bufs.append("','");
								bufs.append(ResourceFactory.getProperty("hmuster.label.nybs")); //年月标识
								bufs.append("','");
								bufs.append(10);
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append("NULL");
								bufs.append("','");
								bufs.append(14);
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append("NULL");
								bufs.append("','");
								bufs.append("NULL");
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append("NULL");
								bufs.append("','");
								bufs.append(0);
								bufs.append("','");
								bufs.append(dev);
								bufs.append("')");
								dao.insert(bufs.toString(), new ArrayList());
						 }
					}else{
//					StringBuffer bufs = new StringBuffer();
//					StringBuffer bufse = new StringBuffer();
					bufs.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
					bufs.append(" values('");
					bufs.append(fieldsetid);
					bufs.append("','");
					bufs.append(fieldsetid+"Z0");
					bufs.append("','");
					bufs.append(Sids);
					bufs.append("','");
					bufs.append("D");
					bufs.append("','");
					bufs.append(ResourceFactory.getProperty("hmuster.label.nybs")); //年月标识
					bufs.append("','");
					bufs.append(10);
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append("NULL");
					bufs.append("','");
					bufs.append(14);
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append("NULL");
					bufs.append("','");
					bufs.append("NULL");
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append("NULL");
					bufs.append("','");
					bufs.append(0);
					bufs.append("','");
					bufs.append(dev);
					bufs.append("')");
					bufse.append("insert into t_hr_busifield (fieldsetid,itemid,displayid,itemtype,itemdesc,itemlength,decimalwidth,codesetid,expression,displaywidth,reserveitem,auditingformula,auditinginformation,state,useflag,keyflag,itemmemo,codeflag,ownflag)");
					bufse.append(" values('");
					bufse.append(fieldsetid);
					bufse.append("','");
					bufse.append(fieldsetid+"Z1");
					bufse.append("','");
					bufse.append(Sids+1);
					bufse.append("','");
					bufse.append("N");
					bufse.append("','");
					bufse.append(ResourceFactory.getProperty("hmuster.label.counts")); //次数
					bufse.append("','");
					bufse.append(3);
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append("NULL");
					bufse.append("','");
					bufse.append(10);
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append("NULL");
					bufse.append("','");
					bufse.append("NULL");
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append("NULL");
					bufse.append("','");
					bufse.append(0);
					bufse.append("','");
					bufse.append(dev);
					bufse.append("')");
					dao.insert(bufs.toString(), new ArrayList());
					dao.insert(bufse.toString(), new ArrayList());
					}
				}
			}else if("35".equalsIgnoreCase(mid)){
				if("0".equalsIgnoreCase(changeflag)){
					StringBuffer buf = new StringBuffer();
					buf.append("delete from t_hr_busifield where ");
					buf.append("fieldsetid='"+fieldsetid+"' and itemid in ('NBase','CondClassCode','CondID','DataSrc')");
					dao.delete(buf.toString(), new ArrayList());
				}else if("1".equalsIgnoreCase(changeflag)|| "2".equalsIgnoreCase(changeflag)){
					RowSet rs = null;
					String sql="select itemid from t_hr_busifield where fieldsetid='"+fieldsetid+"' and upper(itemid) in ('"+fieldsetid.toUpperCase()+"Z0','NBASE','CONDCLASSCODE','CONDID','DATASRC') order by displayid";
					rs = dao.search(sql);
					String Z0 = "";
					String NBase = "";
					String CondClassCode = "";
					String CondID = "";
					String DataSrc = "";
						while(rs.next()){
							String str=rs.getString(1);
							if(str.equalsIgnoreCase(fieldsetid+"Z0")){
								Z0=str;
							}
							if("NBase".equalsIgnoreCase(str)){
								NBase=str;
							}
							if("CondClassCode".equalsIgnoreCase(str)){
								CondClassCode=str;
							}
							if("CondID".equalsIgnoreCase(str)){
								CondID=str;
							}
							if("DataSrc".equalsIgnoreCase(str)){
								DataSrc=str;
							}
						}
						if(Z0==""){
							RecordVo vo1 =new RecordVo("t_hr_busifield");
							vo1.setString("fieldsetid",fieldsetid);
							vo1.setString("itemid",fieldsetid.toUpperCase()+"Z0");
							vo1.setInt("displayid", Sids);
							vo1.setString("itemdesc", ResourceFactory.getProperty("hmuster.label.nybs"));
							vo1.setString("itemtype","D");
						 	vo1.setInt("itemlength", 10);
						 	vo1.setInt("decimalwidth",0);
						 	vo1.setString("codesetid","0");
						 	vo1.setInt("displaywidth",14);
						 	vo1.setString("useflag","0");
						 	vo1.setString("ownflag", dev);
						 	vo1.setString("state","1");
							vo1.setString("keyflag","1");
							vo1.setString("codeflag","0");
							dao.addValueObject(vo1);
							Sids++;
						}
						if(NBase==""){
							RecordVo vo2 = new RecordVo("t_hr_busifield");
							vo2.setString("fieldsetid",fieldsetid);
							vo2.setString("itemid","Nbase");
							vo2.setInt("displayid",Sids);
							vo2.setString("itemdesc", ResourceFactory.getProperty("label.query.dbpre")); //人员库
							vo2.setString("itemtype","A");
							vo2.setInt("itemlength", 3);
							vo2.setInt("decimalwidth",0);
							vo2.setString("codesetid","@@");
							vo2.setInt("displaywidth",10);
							vo2.setString("useflag","0");
							vo2.setString("ownflag",dev);
							vo2.setString("state","1");
							vo2.setString("keyflag","1");
							vo2.setString("codeflag","0");
							dao.addValueObject(vo2);
							Sids++;
						}
						if(CondClassCode==""){
							RecordVo vo4 = new RecordVo("t_hr_busifield");
							vo4.setString("fieldsetid",fieldsetid);
							vo4.setString("itemid","CondClassCode");
							vo4.setInt("displayid",Sids);
							vo4.setString("itemdesc", ResourceFactory.getProperty("conlumn.mediainfo.info_title"));  //分类
							vo4.setString("itemtype","A");
							vo4.setInt("itemlength", 50);
							vo4.setInt("decimalwidth",0);
							vo4.setString("codesetid","0");
							vo4.setInt("displaywidth",10); 
							vo4.setString("useflag","0");
							vo4.setString("ownflag",dev);
							vo4.setString("state","1");
							vo4.setString("keyflag","1");
							vo4.setString("codeflag","0");
							dao.addValueObject(vo4);
							Sids++;
						}
						if(CondID==""){
							RecordVo vo3 = new RecordVo("t_hr_busifield");
							vo3.setString("fieldsetid",fieldsetid);
							vo3.setString("itemid","CondID");
							vo3.setInt("displayid",Sids);
							vo3.setString("itemdesc", ResourceFactory.getProperty("gz.formula.project")); //项目
							vo3.setString("itemtype","N");
							vo3.setInt("itemlength", 8);
							vo3.setInt("decimalwidth",0);
							vo3.setString("codesetid","0");
							vo3.setInt("displaywidth",10);
							vo3.setString("useflag","0");
							vo3.setString("ownflag",dev);
							vo3.setString("state","1");
							vo3.setString("keyflag","1");
							vo3.setString("codeflag","0");
							dao.addValueObject(vo3);
							Sids++;
						}
						if(DataSrc==""){
							RecordVo vo5 = new RecordVo("t_hr_busifield");
							vo5.setString("fieldsetid",fieldsetid);
							vo5.setString("itemid","DataSrc");
							vo5.setInt("displayid",Sids);
							vo5.setString("itemdesc", ResourceFactory.getProperty("kjg.title.datasources"));  //数据来源
							vo5.setString("itemtype","A");
							vo5.setInt("itemlength", 2);
							vo5.setInt("decimalwidth",0);
							vo5.setString("codesetid","52");
							vo5.setInt("displaywidth",10);
							vo5.setString("useflag","0");
							vo5.setString("ownflag",dev);
							vo5.setString("state","1");
							vo5.setString("keyflag","1");
							vo5.setString("codeflag","0");
							dao.addValueObject(vo5);
							Sids++;
						}
				}
			}
		 }catch(SQLException e){
			 e.printStackTrace();
		 }
	}
}