package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InitConfigTrans extends IBusiness {

	public void execute() throws GeneralException {

		String uniqueitem="";
		String backdate = (String)this.getFormHM().get("backdate");
		String backname = (String)this.getFormHM().get("backname");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "select create_date,description from hr_hisdata_list where id=(select max(id) from hr_hisdata_list)";
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList fields = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		ArrayList querylist = new ArrayList();
		String dbcond = "";
		String uplevel="0";
		String pageShowOnly = "a0100";
		try {
			if(backdate==null||backdate.length()<10){
				this.frowset = dao.search(sql);
				if (this.frowset.next()) {
					java.sql.Date date = this.frowset.getDate("create_date");
					backdate = sdf.format(date);
					backname = this.frowset.getString("description");//得到第一次的时点名称
					this.getFormHM().put("ifbackup", "1");
				} else {
					this.getFormHM().put("ifbackup", "0");
				}
			}else{
				this.getFormHM().put("ifbackup", "1");
			}
			
			String queryfieldstr = "";
			String xmlbasestr = "";
			String snap_fields="";
			RowSet rs =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
			if(rs.next()){
				ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
				snap_fields =xml.getTextValue("/Emp_HisPoint/Struct");
				queryfieldstr =xml.getTextValue("/Emp_HisPoint/Query");
				xmlbasestr =xml.getTextValue("/Emp_HisPoint/Base");
			}else{
		           //设置的快照指标
                rs = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
                if(rs.next())
                    snap_fields=rs.getString("str_value");
				//设置的查询指标
				rs=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_QUERY'");
				if(rs.next())
					queryfieldstr=rs.getString("str_value");
				//设置的人员库
				rs=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_BASE'");
				if(rs.next())
					xmlbasestr=rs.getString("str_value");
			}	
			snap_fields =","+snap_fields.toUpperCase()+",";
  
			queryfieldstr =queryfieldstr.toUpperCase();

			
			if(!queryfieldstr.startsWith(",")){
				queryfieldstr=","+queryfieldstr;
			}
			queryfieldstr=queryfieldstr.replaceAll(",A0101", "");
			/*sql = "select str_value from constant where upper(constant)='EMP_HISDATA_STRUCT'";
			this.frowset = dao.search(sql);
			String fieldstr = "";
			if (this.frowset.next()) {
				fieldstr = Sql_switcher.readMemo(frowset, "str_value").toUpperCase();
			}*/
			sql = "select snap_fields from hr_hisdata_list where create_date="+Sql_switcher.dateValue(backdate);
			this.frowset = dao.search(sql);
			String fieldstr = "";
			if (this.frowset.next()) {
				fieldstr = Sql_switcher.readMemo(frowset, "snap_fields").toUpperCase();
			}

			if(!fieldstr.endsWith(","))
				fieldstr=fieldstr+",";
			String[] str_values=fieldstr.split(",");
			for(int i=0;i<str_values.length;i++){
				if(str_values[i].length()!=5)
					continue;
				FieldItem fielditem = DataDictionary.getFieldItem(str_values[i].toLowerCase());
				if (fielditem == null||!"1".equals(fielditem.getUseflag()))
					fieldstr=fieldstr.replaceAll(str_values[i]+",", "");
				//if (snap_fields.indexOf(","+fieldstr.toUpperCase()+",")<0){
				if (snap_fields.indexOf(","+str_values[i].toUpperCase()+",")<0){//zgd 2014-6-26 单个指标对比（前面是所有指标作为整体，和顺序有关，不对）
				    fieldstr=fieldstr.replaceAll(str_values[i]+",", ""); 
				}
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//省份证
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
			String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			if(chkvalid==null)
				 chkvalid="0";
			 if(uniquenessvalid==null)
				 chkvalid="0";
			 if(uniquenessvalid==null)
				 uniquenessvalid="";
			 String chkcheck="",uniquenesscheck="";

			 if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
				 chkcheck="";
			 }
			 else{
				 chkcheck="checked";
			 }
			 if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
				 uniquenesscheck="";
			 }
			 else{
				 uniquenesscheck="checked";
			 }
			StringBuffer setdb=new StringBuffer();
			if(chk==null)
				 chk="";
			if(onlyname==null)
				 onlyname = "";
			
			 if(onlyname.length()>0&& "checked".equals(uniquenesscheck)){
	            uniqueitem=onlyname.toLowerCase();
			 }else if(chk.length()>0&& "checked".equals(chkcheck)){
				uniqueitem=chk.toLowerCase();
			}else{
				uniqueitem="a0100";
			}
			
			uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
	    	if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	    	
	    	if (fieldstr != null && fieldstr.length() > 0) {
	    	    if(!fieldstr.startsWith(","))
	    	        fieldstr=","+fieldstr.toUpperCase();
	    	    
	    	    if(fieldstr.endsWith(","))
	    	        fieldstr=fieldstr.substring(0,fieldstr.length()-1);
	    	    
	    	    fieldstr = fieldstr.replaceAll(",A0100", "").replaceAll(",A0101", "").replaceAll(",B0110", "").replaceAll(",E0122", "").replaceAll(",E01A1", "").replaceAll(","+uniqueitem.toUpperCase(), "");
	    	    //判断身份证指标或唯一性指标在快照数据表中是否存在
	    	    DbWizard dbwizard = new DbWizard(this.frameconn);
	    	    boolean hasUniuqeitem = dbwizard.isExistField("hr_emp_hisdata", uniqueitem, false);
	    	    //zxj 20151112 补上157行去掉的uniqueitem jazz-14338
	    	    String itemIds = ",A0100,B0110,E0122,E01A1,A0101,";
	    	    if (hasUniuqeitem && !itemIds.contains("," + uniqueitem.toUpperCase() + ",")) {
	    	        pageShowOnly = uniqueitem;
	    	        strsql.append("select "+ uniqueitem+",B0110,E0122,E01A1,A0101"+ fieldstr);
	    	        columns.append(","+uniqueitem+",B0110,E0122,E01A1,A0101"+fieldstr);
	    	        fieldstr=",B0110,E0122,E01A1,A0101,"+ uniqueitem.toUpperCase() + fieldstr;
	    	    } else {
	    	        uniqueitem = "a0100";
	    	        strsql.append("select A0100,B0110,E0122,E01A1,A0101"+ fieldstr);
	    	        columns.append("A0100,B0110,E0122,E01A1,A0101"+fieldstr);
	    	        fieldstr=",A0100,B0110,E0122,E01A1,A0101,"+ fieldstr;
	    	    }
	    	} else {
	    	    fieldstr = ",B0110,E0122,E01A1,A0101";
	    	    strsql.append("select "+uniqueitem+",B0110,E0122,E01A1,A0101");
	    	    columns.append(","+uniqueitem+",B0110,E0122,E01A1,A0101");
	    	}
	    	
			String[] f = fieldstr.split(",");
			for (int i = 0; i < f.length; i++) {
				String itemid = f[i];
				if(itemid.length()<4)
					continue;
						FieldItem fieldItem_O = DataDictionary.getFieldItem(itemid);
						if(null != fieldItem_O){
							
							FieldItem fieldItem = (FieldItem) fieldItem_O.clone();
						
						
						if (!"0".equals(this.userView.analyseFieldPriv(
								fieldItem.getItemid()))) {
							fieldItem
							.setDisplaywidth(fieldItem.getDisplaywidth() * 12);
							//if("A0101,B0110,E0122,E01A1".indexOf(itemid)!=-1)
								fieldItem.setVisible(true);
								fields.add(fieldItem);
						}
						}
			}
			
			
			f = queryfieldstr.split(",");
			for (int i = 0; i < f.length; i++) {
				String itemid = f[i];
				if(itemid.length()<4)
					continue;
						FieldItem fieldItem_O = DataDictionary.getFieldItem(itemid);
						if(null != fieldItem_O){							
							FieldItem fieldItem = (FieldItem) fieldItem_O.clone();
						
						
						if (!"0".equals(this.userView.analyseFieldPriv(
								fieldItem.getItemid()))) {
							fieldItem
							.setDisplaywidth(fieldItem.getDisplaywidth() * 12);
							//if("A0101,B0110,E0122,E01A1".indexOf(itemid)!=-1)
							if(fieldItem.getCodesetid()!=null&&!"0".equals(fieldItem.getCodesetid()))
			            	 {
			            		 int count=getCodeSetidChildLen(fieldItem.getCodesetid(),dao);
			            		 fieldItem.setItemlength(count);       //用于页面代码项的显示样式 liwc     		
			            		 int maxLv=getCodeitemMaxLevel(fieldItem.getCodesetid(),dao);
			            		 if(maxLv > 1)
			            			 fieldItem.setItemlength(999);            		
			            	 }
								fieldItem.setVisible(true);
								querylist.add(fieldItem);
						}
						}
			}
			

			if (xmlbasestr.length()>1) {
				fieldstr = xmlbasestr.toUpperCase();
			}else{
				fieldstr="USR,";
			}
			dbcond = "select pre,dbname from dbname where pre in('###'";
			ArrayList dblist = userView.getPrivDbList();
			for(int i=0;i<dblist.size();i++){
				String pre = ((String)dblist.get(i)).toUpperCase();
				if(fieldstr.indexOf(pre)!=-1)
					dbcond+=",'"+(String)dblist.get(i)+"'";
			}
			dbcond+=") order by dbid";
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.getFormHM().put("browsefields", fields);
			this.getFormHM().put("strsql", strsql.toString());
			this.getFormHM().put("columns", columns.toString());
			this.getFormHM().put("backdate", backdate);
			this.getFormHM().put("backname", backname);
			this.getFormHM().put("isShowCondition", "none");
			this.getFormHM().put("orglike", "1");
			this.getFormHM().put("uniqueitem", uniqueitem.toLowerCase());
			this.getFormHM().put("pageShowOnly", pageShowOnly);
			this.getFormHM().put("queryfieldlist",querylist);
			this.getFormHM().put("dbcond", dbcond);
			this.getFormHM().put("uplevel", uplevel);
			this.getUserView().getHm().put("staff_sql", "");
		}
	}
	/**
	 * 获取代码项子集个数
	 * @param codesetid
	 * @param dao
	 * @return
	 */
	private int getCodeSetidChildLen(String codesetid,ContentDAO dao)
    {
    	String sql="select count(*) aa from codeitem where codesetid = '"+codesetid+"'";
    	int count=0;
    	try
    	{
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
    			count=this.frowset.getInt("aa");
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return count;
    }
	
	/**
	 * 获取某代码类有多少层代码项
	 * 
	 * @param codesetid
	 *            代码类的编号
	 * @param conn
	 *            数据库链接
	 * @return
	 */
	private int getCodeitemMaxLevel(String codesetid, ContentDAO dao) {
		String sql = "select max(layer) layer from codeitem where codesetid=?";
		RowSet rs = null;
		int layer = 1;
		try {
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(codesetid);
			rs = dao.search(sql, paramList);
			if (rs.next())
				layer = rs.getInt("layer");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return layer;
	}
}
