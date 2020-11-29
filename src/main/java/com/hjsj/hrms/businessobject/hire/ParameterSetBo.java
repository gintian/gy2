package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.ServletContext;
import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 *  zxj 20160305 该类在7X版已废弃，
 *  请使用com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo
 */

@Deprecated
public class ParameterSetBo {
	private Connection conn;
	public static ArrayList cultureList=null;
	public static String license_agreement=null;
	public static String prompt_content=null;
	private String hire_obj_code="";//是招聘渠道代码值（校园招聘、社会招聘、内部招聘）
	private String hireState="0";//测评阶段 初试复试
	public String getHire_obj_code() {
		return hire_obj_code;
	}
	public void setHire_obj_code(String hireObjCode) {
		hire_obj_code = hireObjCode;
	}
	public String getHireState() {
		return hireState;
	}
	public void setHireState(String hireState) {
		this.hireState = hireState;
	}
	public ParameterSetBo(Connection conn)
	{
		this.conn=conn;
	}
	public String getLicense_agreement()
	{
		String licenseAgreement="";
		try
		{
			if(license_agreement!=null&&!"".equals(license_agreement)) {
                return license_agreement;
            }
    		String sql="select str_value from constant where UPPER(constant)='ZP_LICENSE_AGREEMENT'";
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	RowSet rs=null;
	    	rs=dao.search(sql);
    		while(rs.next())
	    	{
	    		licenseAgreement=rs.getString("str_value")==null?"":rs.getString("str_value").trim();
    		}
    		license_agreement=licenseAgreement;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return licenseAgreement;
	}
	public String getPrompt_content()
	{
		String promptContent="";
		try
		{
			if(!(prompt_content==null|| "".equals(prompt_content))) {
                return prompt_content;
            }
			String sql="select str_value from constant where UPPER(constant)='ZP_PROMPT_CONTENT'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				promptContent=rs.getString("str_value")==null?"":rs.getString("str_value");
			}
			prompt_content=promptContent;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return promptContent;
	}
	/**
	 * 取得 单位介绍情况信息列表
	 * @param userView
	 * @param orgFieldIDs
	 * @param contentType
	 * @return
	 */
	public ArrayList getOrgIntroList(UserView userView,String orgFieldIDs,String contentType,String type,String orgId,String orgName)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			StringBuffer sql=new StringBuffer("select organization.codeitemdesc,organization.codeitemid,organization.codesetid ");
			if(orgFieldIDs!=null&&orgFieldIDs.trim().length()>0) {
                sql.append(","+orgFieldIDs);
            }
			if(contentType!=null&&contentType.trim().length()>0) {
                sql.append(","+contentType);
            }
			sql.append(" from  b01,organization where b01.b0110=organization.codeitemid and (organization.codesetid='UN' or organization.codesetid='UM')");
			if(!"all".equalsIgnoreCase(type))
			{
		    	if("0".equals(type))
	     		{
	     			if(orgId!=null&&!"".equals(orgId))
	    			{
	    				sql.append(" and organization.codeitemid ='");
		    			sql.append(orgId+"'");
	    			}
	    			else
		    		{
		    			sql.append(" and organization.codeitemdesc like '%");
		    			sql.append(PubFunc.getStr(orgName)+"%' ");
	    			}
	    		}
			}
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between organization.start_date and organization.end_date ");
			sql.append(" order by organization.codeitemid,organization.a0000");
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("codeitemid",rs.getString("codeitemid"));
				abean.set("codeitemdesc",rs.getString("codeitemdesc"));
				abean.set("codesetid",rs.getString("codesetid"));
				if(contentType!=null&&contentType.trim().length()>0)
				{
					String contentTypeValue=rs.getString(contentType);
					//-----------------------------------------------
					if(contentTypeValue == null){
						abean.set("contentType","");
						abean.set("contentTypeValue","");
					}else{
					  abean.set("contentType",AdminCode.getCodeName("43",contentTypeValue));
					  abean.set("contentTypeValue", contentTypeValue);
					}
				}
				else
				{
					abean.set("contentType","");
					abean.set("contentTypeValue","");
				}
				
				if(orgFieldIDs!=null&&orgFieldIDs.trim().length()>0)
				{
					if(contentType!=null&&contentType.trim().length()>0)
					{
						String contentTypeValue=rs.getString(contentType);
						//---------------------------------------------
						if(contentTypeValue==null) {
                            abean.set("content","");
                        } else{
						if("0".equalsIgnoreCase(contentTypeValue))  //网址
						{
							String url=Sql_switcher.readMemo(rs,orgFieldIDs);
							if(url.length()>40)
							{
								StringBuffer buf = new StringBuffer();
								int length=url.length();
								int i=0;
								while(length>40)
								{
									buf.append(url.substring(0, 40)+"\r\n");
									i++;
									url=url.substring(40);
									length=url.length();
								}
								buf.append(url);
								url=buf.toString();
							}
							abean.set("content",url);
						}
						else {
                            abean.set("content","内容简介......");
                        }
						}
					}
					else {
                        abean.set("content","内容简介......");
                    }
					
				}
				else {
                    abean.set("content","");
                }
				
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 得到单位介绍指标列表
	 * @return
	 */
	public ArrayList getOrgFieldList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("","       "));
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search("select itemid,itemdesc from fielditem where fieldsetid='B01' and itemtype='M' and useflag='1'");
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到内容形式指标列表
	 * @return
	 */
	public ArrayList getContentTypeList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("","       "));
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search("select * from fielditem where UPPER(fieldsetid)= 'B01' and codesetid ='43' and useflag='1'");
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到内容形式列表
	 * @return
	 */
   public ArrayList getContentTypeValueList()
   {
	   ArrayList list = new ArrayList();
	   ContentDAO dao = new ContentDAO(this.conn);
	   list.add(new CommonData("","         "));
	   RowSet rs= null;
	   try{
		   rs=dao.search("select codeitemid,codeitemdesc from codeitem where codesetid='43'");
		   while(rs.next()){
			   list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return list;
	   
   }
	
	
	/**
	 * 取得招聘对象类型列表
	 * @return
	 */
	public ArrayList getCodeValueList()
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search("select * from codeitem where codesetid='35' order by codeitemid");
			while(rs.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("codeitemid",rs.getString("codeitemid"));
				abean.set("codeitemdesc",rs.getString("codeitemdesc"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//
		return list;
	}
	
	
	
	
	/**
	 * 取得登记表列表
	 * @return
	 */
	public ArrayList getRnameList()
	{
		ArrayList list=new ArrayList();
		
		String desc=ResourceFactory.getProperty("label.select.dot");
		CommonData vo=new CommonData("#",desc);
		list.add(vo);
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search("select * from rname where flagA='K'");
			while(rs.next())
			{
				CommonData a_vo=new CommonData(rs.getString("tabid"),rs.getString("name"));
				list.add(a_vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	/**
	 * 取得考核模板列表
	 * @return
	 */
	public ArrayList getPerTemplateList()
	{
		ArrayList list=new ArrayList();
		String desc=ResourceFactory.getProperty("label.select.dot");
		CommonData vo=new CommonData("#",desc);
		list.add(vo);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rs2 = null;
		
		try{
			HashMap map = new HashMap();//包含个性项目的目标模版去掉，
			rs = dao.search("select distinct template_id from per_template_item where kind=2");
			while(rs.next())
			{
				map.put(rs.getString(1).toUpperCase(), "1");
			}
			HashMap map2 = new HashMap();//包含个性项目的目标模版去掉，
			rs2 = dao.search("select * from per_template t join per_template_set s on t.template_setid = s.template_setid where subsys_id  in('35')");
			while(rs2.next())
			{
				map2.put(rs2.getString(1).toUpperCase(), "1");
			}
			rs=dao.search("select * from per_template t join per_template_set s on t.template_setid = s.template_setid where subsys_id = '33'");
			while(rs.next())
			{
				if(map.get(rs.getString("template_id").toUpperCase())!=null)
				{
					continue;
				}
				if(map2.get(rs.getString("template_id").toUpperCase())!=null)
				{
					continue;
				}
				String name=rs.getString("name");
				CommonData a_vo=new CommonData(rs.getString("template_id"),name);
				list.add(a_vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	
	
   public String getExistItem(String musterFieldIDs)
   {
	   String ids="";
	   try
	   {
		   StringBuffer whl=new StringBuffer("");
		   if(musterFieldIDs.indexOf("`")==-1)
			{
				whl.append(",'"+musterFieldIDs+"'");
			}
			else
			{
				String[] fields=musterFieldIDs.split("`");
				for(int i=0;i<fields.length;i++)
				{
					whl.append(",'"+fields[i]+"'");
				}
			}
		   String sql = "select * from fielditem where itemid in("+(whl.substring(1)).toUpperCase()+") and useflag='1'";
		   String sql2 = "select * from t_hr_busifield where  fieldsetid='Z03' and itemid in("+(whl.substring(1)).toUpperCase()+") and useflag='1'";
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql);
		   RowSet rs2 = dao.search(sql2);
		   HashMap hm=new HashMap();
		   while(rs.next())
		   {
			   hm.put(rs.getString("itemid").toLowerCase(), rs.getString("itemid"));
		   }
		   while(rs2.next())
		   {
			   hm.put(rs2.getString("itemid").toLowerCase(), rs2.getString("itemid"));
		   }
		   	String[] fields=musterFieldIDs.split("`");
		    for(int i=0;i<fields.length;i++){
		    	if(hm.get(fields[i].toLowerCase())==null)
		    	{
		    		continue;
		    	}
		    	else
		    	{
		    		ids+="`"+hm.get(fields[i].toLowerCase());
		    	}
		    }
		   if(whl.toString().length()>0)
		   {
			   ids=ids.substring(1);
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return ids;
   }
	
	/**
	 * 根据fielditemids 得到 其描述
	 * @param musterFieldIDs  a0101`a0100
	 * @param flag  0: fielditem  1:t_hr_busiField 
	 * @return
	 */
	public String getmusterFieldNames(String musterFieldIDs,int flag)
	{
		StringBuffer fieldNames=new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer whl=new StringBuffer("");
		if(musterFieldIDs==null||musterFieldIDs.trim().length()==0) {
            return fieldNames.toString();
        }
		String[] names;
		boolean yprsl=false;
		boolean ypljl=false;
		boolean opentime=false;
		if(musterFieldIDs.indexOf("`")==-1)
		{
			whl.append(",'"+musterFieldIDs+"'");
			if("yprsl".equalsIgnoreCase(musterFieldIDs)) {
                yprsl=true;
            }
			if("ypljl".equalsIgnoreCase(musterFieldIDs)) {
                ypljl=true;
            }
			if("opentime".equalsIgnoreCase(musterFieldIDs)) {
                opentime=true;
            }
			names=new String[1];
			names[0]=musterFieldIDs;
		}
		else
		{
			String[] fields=musterFieldIDs.split("`");
			for(int i=0;i<fields.length;i++)
			{
				if("yprsl".equalsIgnoreCase(fields[i])) {
                    yprsl=true;
                }
				if("ypljl".equalsIgnoreCase(fields[i])) {
                    ypljl=true;
                }
				if("opentime".equalsIgnoreCase(fields[i])) {
                    opentime=true;
                }
				whl.append(",'"+fields[i]+"'");
			}
			names=fields;
		}
		RowSet rs = null;
		RowSet rs2 = null;
		try{
			if(flag==0){
				rs=dao.search("select itemid,itemdesc from fielditem where itemid in ("+(whl.substring(1))+",'A0109') and useflag='1'");
			  rs2=dao.search("select itemid,itemdesc from t_hr_busifield where fieldsetid='Z03' and useflag='1' and state= '1'");
			}else{
				rs=dao.search("select itemid,itemdesc from t_hr_busiField where itemid in ("+(whl.substring(1))+") and useflag='1' and fieldsetid='Z03'");
				//rs2 = dao.search("select itemid,itemdesc from t_hr_busiField where itemid in ("+(whl.substring(1))+") and useflag='1'");
			}
		HashMap hm=new HashMap();
			while(rs.next())
			{
				hm.put(rs.getString("itemid").toLowerCase(),rs.getString("itemdesc"));
			}
			
			if(rs2!=null){
			
			while(rs2.next())
			{
				hm.put(rs2.getString("itemid").toLowerCase(),rs2.getString("itemdesc"));
			}
			}
			String str="B0110";
			hm.put(str.toLowerCase(),"单位");
				for(int i=0;i<names.length;i++)
				{
					if("opentime".equalsIgnoreCase(names[i])){
						fieldNames.append(",发布日期");
					}else if("yprsl".equalsIgnoreCase(names[i])){
						fieldNames.append(",应聘人数(推荐人数)");
					}else if("ypljl".equalsIgnoreCase(names[i])){
						fieldNames.append(",应聘(推荐)");
					}else{						
						fieldNames.append(","+(String)hm.get(names[i].toLowerCase()));
					}
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(fieldNames.toString().trim().length()>0) {
            return fieldNames.substring(1);
        } else {
            return "";
        }
	}
	/**
	 * 
	 * @Title: getPosListFieldSortNames
	 * @Description: TODO 得到外网列表显示指标排序的方式
	 * @param musterFieldIDs 从配置参数里面得到传过来的已有的指标以及排序方式
	 * @return String   
	 * @throws
	 */
	public String getPosListFieldSortNames(String musterFieldIDs){

        StringBuffer fieldNames=new StringBuffer("");
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer whl=new StringBuffer("");
        if(musterFieldIDs==null||musterFieldIDs.trim().length()==0) {
            return fieldNames.toString();
        }
        String[] names;
        String[] idAndSorts;//存放传过来的id和排序方式id:[desc||asc]
        HashMap sortMap=new HashMap();
//        boolean yprsl=false;
//        boolean ypljl=false;
        if(musterFieldIDs.indexOf(",")==-1)
        {
            idAndSorts=musterFieldIDs.split(":");
            whl.append(",'"+idAndSorts[0].trim()+"'");
//            if(idAndSorts[0].equalsIgnoreCase("yprsl"))
//                yprsl=true;
//            if(idAndSorts[0].equalsIgnoreCase("ypljl"))
//                ypljl=true;
            sortMap.put(idAndSorts[0].trim().toLowerCase(), idAndSorts[1]);//存放的是[KEY:VALUE][ID:SORT]
            names=new String[1];
            names[0]=musterFieldIDs;
        }
        else
        {
            String[] fields=musterFieldIDs.split(",");
            for(int i=0;i<fields.length;i++)
            {
                idAndSorts=fields[i].split(":");
//                if(idAndSorts[0].equalsIgnoreCase("yprsl"))
//                    yprsl=true;
//                if(idAndSorts[0].equalsIgnoreCase("ypljl"))
//                    ypljl=true;
                whl.append(",'"+idAndSorts[0].trim()+"'");
                sortMap.put(idAndSorts[0].trim().toLowerCase(), idAndSorts[1]);//存放的是[KEY:VALUE][ID:SORT]
            }
            names=fields;
        }
        RowSet rs = null;
        try{
           rs=dao.search("select itemid,itemdesc from t_hr_busiField where itemid in ("+(whl.substring(1))+") and useflag='1' and fieldsetid='Z03'");
           HashMap hm=new HashMap();
            while(rs.next())
            {
                String itemid=rs.getString("itemid").toLowerCase();
                String sort =(String) sortMap.get(itemid);
                String itemdesc=rs.getString("itemdesc");
                if("ASC".equalsIgnoreCase(sort)){
                    itemdesc=itemdesc+":升序";
                }else{
                    itemdesc=itemdesc+":降序";
                }
                hm.put(itemid,itemdesc);
            }
            for(int i=0;i<names.length;i++)
            {
                idAndSorts=names[i].split(":");
//                if(idAndSorts[0].equalsIgnoreCase("yprsl")||idAndSorts[0].equalsIgnoreCase("ypljl"))
//                    continue;
                fieldNames.append(","+(String)hm.get(idAndSorts[0].trim().toLowerCase()));
            }
//            if(yprsl){
//                String sort =(String) sortMap.get("yprsl");
//                if(sort.equalsIgnoreCase("ASC")){
//                    fieldNames.append(",应聘人数:升序");
//                }else{
//                    fieldNames.append(",应聘人数:降序");
//                }
//            }
//                
//            if(ypljl){
//                String sort =(String) sortMap.get("ypljl");
//                if(sort.equalsIgnoreCase("ASC")){
//                    fieldNames.append(",应聘:升序");
//                }else{
//                    fieldNames.append(",应聘:降序");
//                }
//            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(fieldNames.toString().trim().length()>0) {
            return fieldNames.substring(1);
        } else {
            return "";
        }
	}
	
	
	/**
	 * 动态产生评测打分信息表（zp_test_template） 和 评测结果表（zp_test_result）
	 * @param testTemplateID 考核模版id
	 * @return
	 */
	public boolean createEvaluatingTable(String testTemplateID)
	{
		boolean flag=true;
		DbWizard dbWizard=new DbWizard(this.conn);
		createZp_test_templateTable(dbWizard);
		
		/**查看该表中是否存在interview字段,然后为这个表增加一个主键interview**/
		if (!dbWizard.isExistField("zp_test_template", "interview",false)) {
			this.setInterviewPKAndValue(dbWizard, "zp_test_template");
		}
		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		dbmodel.reloadTableModel("zp_test_template");
		
		if(testTemplateID.indexOf("~")==-1&&!"#".equals(testTemplateID))
		{
			createZp_test_result(dbWizard,testTemplateID);
			if (!dbWizard.isExistField("zp_test_result_"+testTemplateID, "interview",false)) {
				this.setInterviewPKAndValue(dbWizard, "zp_test_result_"+testTemplateID);
			}
			dbmodel.reloadTableModel("zp_test_result_"+testTemplateID);
		}
		else
		{
			HashMap map=new HashMap();
			String[] temp=testTemplateID.split("~");
			for(int i=0;i<temp.length;i++)
			{
				if(map.get(temp[i])==null&&!"#".equals(temp[i]))
				{
					createZp_test_result(dbWizard,temp[i]);
					if (!dbWizard.isExistField("zp_test_result_"+temp[i], "interview",false)) {
						this.setInterviewPKAndValue(dbWizard, "zp_test_result_"+temp[i]);
					}
					dbmodel.reloadTableModel("zp_test_result_"+temp[i]);
					map.put(temp[i],"");
				}
				
			}
			
		}
		
		
		/**重新加载数据模型*/
		
		
		return flag;
	}
	
	
	
	/**
	 * 动态产生评测打分信息表（zp_test_template） 和 评测结果表（zp_test_result）  zzk 完全重新生成招聘测评结果表
	 * @param testTemplateID 考核模版id
	 * @return
	 */
	public boolean createEvaluatingTableAbsolutely(String testTemplateID)
	{
		boolean flag=true;
		DbWizard dbWizard=new DbWizard(this.conn);
		createZp_test_templateTable(dbWizard);
		if (!dbWizard.isExistField("zp_test_template", "interview",false)) {
			Table table=new Table("zp_test_template");
			Field obj = new Field("interview");
			obj.setDatatype(DataType.INT);
			obj.setNullable(true);
			obj.setKeyable(false);
			table.addField(obj);
			try {
				dbWizard.addColumns(table);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		}
		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		dbmodel.reloadTableModel("zp_test_template");
		
		if(testTemplateID.indexOf("~")==-1&&!"#".equals(testTemplateID))
		{
			createZp_test_resultAbsolutely(dbWizard,testTemplateID);
			if (!dbWizard.isExistField("zp_test_result_"+testTemplateID, "interview",false)) {
				Table table=new Table("zp_test_result_"+testTemplateID);
				Field obj = new Field("interview");
				obj.setDatatype(DataType.INT);
				obj.setNullable(true);
				obj.setKeyable(false);
				table.addField(obj);
				try {
					dbWizard.addColumns(table);
				} catch (GeneralException e) {
					e.printStackTrace();
				}
			}
			dbmodel.reloadTableModel("zp_test_result_"+testTemplateID);
		}
		else
		{
			HashMap map=new HashMap();
			String[] temp=testTemplateID.split("~");
			for(int i=0;i<temp.length;i++)
			{
				if(map.get(temp[i])==null&&!"#".equals(temp[i]))
				{
					createZp_test_resultAbsolutely(dbWizard,temp[i]);
					if (!dbWizard.isExistField("zp_test_result_"+temp[i], "interview",false)) {
						Table table=new Table("zp_test_result_"+temp[i]);
						Field obj = new Field("interview");
						obj.setDatatype(DataType.INT);
						obj.setNullable(true);
						obj.setKeyable(false);
						table.addField(obj);
						try {
							dbWizard.addColumns(table);
						} catch (GeneralException e) {
							e.printStackTrace();
						}
					}
					dbmodel.reloadTableModel("zp_test_result_"+temp[i]);
					map.put(temp[i],"");
				}
				
			}
			
		}
		
		
		/**重新加载数据模型*/
		
		
		return flag;
	}
	/**
	 * 获得所有模板后缀id
	 * @return
	 */
	public HashSet getHashSet(){
		HashSet set = new HashSet();
		try{
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet res=null;
		String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()); 
		/**ctrl_param  这个字段什么作用?**/
		String sql="select ctrl_param from z03 where Z0329<="+Sql_switcher.dateValue(date)+" and Z0331>="+Sql_switcher.dateValue(date)+" and Z0319='04' ";
		res=dao.search(sql);
		String ctrl_param="";
		String template="";
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
		HashMap map=parameterXMLBo.getAttributeValues();
		set=(HashSet) map.get("testTemplateID");
		 XPath xpath_=null;
		 Element ele =null;
		while(res.next()){
			ctrl_param = Sql_switcher.readMemo(res,"ctrl_param"); 
		    Document doc = PubFunc.generateDom(ctrl_param);
		    String xpath = "//content";
		    xpath_ = XPath.newInstance(xpath);
		    ele = (Element) xpath_.selectSingleNode(doc);
		    Element child;
		    if (ele != null){
				child = ele.getChild("template");
				if (child != null)
				{
					template = child.getAttributeValue("id");
					if(template.indexOf("#")==-1){
						set.add(template);
					}
				}
			}
		}
		
	}catch(Exception e)
		{
			e.printStackTrace();
		}
	return set;
	}		
	/**
	 * 计算考评打分中的考核结果(zp_test_result)
	 * @param templateID  //模版编号
	 * @param status      // 权重分值表识 0：分值 1：权重
	 */
	public void CalculateGradeResult(String templateID,String status,String a0100,String z0101)
	{
		ArrayList pointList=new ArrayList();
		ArrayList layItemList=new ArrayList();
		HashMap itemPoint=new HashMap();
//		分析绩效考核模版
		anaylseTemplateTable(pointList,layItemList,itemPoint,templateID,"");
		HashMap rankMap=getPointRank(templateID);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			HashSet set=this.getHashSet();
			Iterator it = set.iterator();
			if(this.hireState!=null&&this.hireState.trim().length()>0){//高级测评 删除等于当前测评阶段的
				while(it.hasNext()){
					dao.delete("delete from zp_test_result_"+it.next()+" where a0100='"+a0100+"' and interview="+this.hireState ,new ArrayList());
				}
			}else{
				while(it.hasNext()){
					dao.delete("delete from zp_test_result_"+it.next()+" where a0100='"+a0100+"' and interview=0" ,new ArrayList());
				}
			}
			rowSet=dao.search("select count(a0100_1) a,point_id from zp_test_template where a0100='"+a0100+"'and interview="+this.hireState+" group by point_id");
			HashMap numMap = new HashMap();
			while(rowSet.next()){
				int num=rowSet.getInt("a");
				String point_id=rowSet.getString("point_id");
				numMap.put(point_id.toLowerCase(), num+"");
			}
			StringBuffer sql_insert=new StringBuffer("insert into zp_test_result_"+templateID+" (a0100,z0101,interview");
			StringBuffer sql_select=new StringBuffer(" select  '"+a0100+"','"+z0101+"',"+this.hireState);
			StringBuffer sql_from=new StringBuffer("");
			
			for(int i=0;i<pointList.size();i++)
			{
				String temp=(String)pointList.get(i);
				String num="1";
				if(numMap.get(temp.toLowerCase())!=null)
				{
					num=(String)numMap.get(temp.toLowerCase());
				}
				sql_insert.append(",C_"+temp);
				sql_select.append(",c"+temp);
				sql_from.append(",( select sum(score)/"+num);
				if("1".equals(status))   //如果是权重模版
                {
                    sql_from.append("*"+(String)rankMap.get(temp));
                }
				sql_from.append(" c"+temp+" from zp_test_template where a0100='"+a0100+"' and point_id='"+temp+"' and interview="+this.hireState+" ) a"+i);
			}
			sql_insert.append(" ) "+sql_select.toString());
			sql_insert.append(" from ");
			sql_insert.append(sql_from.substring(1));
			dao.insert(sql_insert.toString(),new ArrayList());
			/* 计算总分 */
			rowSet=dao.search("select * from zp_test_result_"+templateID+" where a0100='"+a0100+"' and interview="+this.hireState);
			BigDecimal score=new BigDecimal("0");
			if(rowSet.next())
			{
				for(int i=0;i<pointList.size();i++)
				{
					String temp=(String)pointList.get(i);
					if(rowSet.getString("C_"+temp)!=null) {
                        score=score.add(new BigDecimal(rowSet.getString("C_"+temp)));
                    }
				}
			}
			BigDecimal one = new BigDecimal("1");
			String a_score=score.divide(one,1,BigDecimal.ROUND_HALF_UP).toString();
			dao.update("update zp_test_result_"+templateID+" set score="+a_score+" where a0100='"+a0100+"' and interview="+this.hireState);
			/**如果有高级测评的话，要将测评的结果关联到z05表中去begin**/
			ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
			HashMap map=bo2.getAttributeValues();
			String  score_item="";
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
	        for(int i=0;i<testTemplatAdvance.size();i++){
	            HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
	            String hire_obj_code=(String) advanceMap.get("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
	            String interview=(String) advanceMap.get("interview");//得到面试方式 1：初试 2：复试
	            if(hire_obj_code.equalsIgnoreCase(this.hire_obj_code)&&interview.equalsIgnoreCase(this.hireState)){//渠道相同且面试阶段相同就取他的关联指标
	            	score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
	            	break;
	            }
	        }
	        if(score_item.trim().length()>0){
	        	String sql="update z05 set "+score_item+"= "+score+" where a0100='"+a0100+"'";
	        	dao.update(sql);
	        }
	        /**如果有高级测评的话，要将测评的结果关联到z05表中去end**/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

	
	
	
	
	/**
	 * 根据考核模版得到各指标的权重
	 * @param templateID
	 * @return
	 */
	public HashMap getPointRank(String templateID)
	{
		HashMap rankMap=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select point_id,per_template_point.rank  from per_template_item,per_template_point where per_template_item.item_id=per_template_point.item_id  and  template_id='"+templateID+"'");
			while(rowSet.next())
			{
				rankMap.put(rowSet.getString("point_id"),rowSet.getString("rank"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rankMap;
	}
	
	
	/**
	 * 创建 评测结果表 
	 * @param dbWizard
	 * @param testTemplateID
	 */
	public void createZp_test_result(DbWizard dbWizard,String testTemplateID)
	{
		ArrayList pointList=new ArrayList();
		ArrayList layItemList=new ArrayList();
		HashMap itemPoint=new HashMap();
		Table table=new Table("zp_test_result_"+testTemplateID);
		if(!dbWizard.isExistTable(table.getName(),false))
		{
	    	//分析绩效考核模版
	     	anaylseTemplateTable(pointList,layItemList,itemPoint,testTemplateID,"");
		
	    	table.setCreatekey(true);
		    ArrayList fieldList=getZP_TEST_RESULT_TableFields(pointList,layItemList);
		    createTable(table,fieldList, dbWizard);		
		}
	}
	
	/**
	 * 创建 评测结果表 
	 * @param dbWizard
	 * @param testTemplateID
	 */
	public void createZp_test_resultAbsolutely(DbWizard dbWizard,String testTemplateID)
	{
		ArrayList pointList=new ArrayList();
		ArrayList layItemList=new ArrayList();
		HashMap itemPoint=new HashMap();
		Table table=new Table("zp_test_result_"+testTemplateID);
//		if(!dbWizard.isExistTable(table.getName(),false))
//		{
	    	//分析绩效考核模版
	     	anaylseTemplateTable(pointList,layItemList,itemPoint,testTemplateID,"");
		
	    	table.setCreatekey(true);
		    ArrayList fieldList=getZP_TEST_RESULT_TableFields(pointList,layItemList);
		    createTableAbsolutely(table,fieldList, dbWizard);		
//		}
	}
	/**
	 * 取得 评测结果表 的列表信息
	 * @param pointList
	 * @param layItemList
	 * @return
	 */
	public ArrayList getZP_TEST_RESULT_TableFields(ArrayList pointList,ArrayList layItemList)
	{
		
		ArrayList list=new ArrayList();
		Field aField0=getField(true,"A0100",ResourceFactory.getProperty("hire.parameterSet.interviewerNo"),"A",30,0);
		list.add(aField0);
		Field aField1=getField(true,"Z0101",ResourceFactory.getProperty("hire.parameterSet.engagePlanNo"),"A",10,0);
		list.add(aField1);
		
		Iterator t=pointList.iterator();
		Field aField=null;
		while(t.hasNext())
		{
			aField=getField(false,"C_"+(String)t.next(),ResourceFactory.getProperty("hire.parameterSet.factor"),"N",15,4);
			list.add(aField);
		}
		t=layItemList.iterator();
		while(t.hasNext())
		{
			ArrayList alist=(ArrayList)t.next();
			for(int i=0;i<alist.size();i++)
			{
				LazyDynaBean a_bean=(LazyDynaBean)alist.get(i);
				aField=getField(false,"T_"+(String)a_bean.get("item_id"),ResourceFactory.getProperty("hire.parameterSet.itemfactor"),"N",15,4);
				list.add(aField);
			}
		}
		
		aField=getField(false,"score",ResourceFactory.getProperty("hire.parameterSet.calculateMark"),"N",15,4);
		list.add(aField);
		return list;
	}
	
	
/************************************************************************************************************/	
	
	/**
	 * 分析考评模版
	 * @param pointList 考评表中低层(按顺序)指标列表
	 * @param layItemList 各层的 itemList
	 * @param itemPoint   各项目 包含的 指标号 or 项目号
	 * @param testTemplateID
	 */
	public void anaylseTemplateTable(ArrayList pointList,ArrayList layItemList,HashMap itemPoint,String testTemplateID,String privPointStr)
	{
		
		ArrayList allItemList=getAllItemList(testTemplateID);    //模版中包含的所有项目
		ArrayList allPointList=getAllPointList(testTemplateID,privPointStr);  //模版中包含的所有指标
		
		ArrayList tempList=new ArrayList();
		for(Iterator t=allItemList.iterator();t.hasNext();)
		{
			LazyDynaBean abean=(LazyDynaBean)t.next();
			if("".equals((String)abean.get("parent_id")))
			{
				tempList.add(abean);
			}
		}
		
		layItemList.add(tempList);
		anaylse(allItemList,allPointList,layItemList,itemPoint,tempList,pointList);
	}
	
	

	
	
	public void anaylse(ArrayList allItemList,ArrayList allPointList,ArrayList layItemList,HashMap itemPoint,ArrayList itemList,ArrayList pointList)
	{
		
		ArrayList  a_layList=new ArrayList();
		int i=0;
		for(int j=0;j<itemList.size();j++)
		{
			LazyDynaBean abean=(LazyDynaBean)itemList.get(j);
			ArrayList subList=findSubNode(allItemList,allPointList,itemPoint,(String)abean.get("item_id"),pointList);
			for(Iterator t2=subList.iterator();t2.hasNext();)
			{
				LazyDynaBean a_bean=(LazyDynaBean)t2.next();
				a_layList.add(a_bean);
				i++;
			}
		}
		if(i!=0)
		{
			layItemList.add(a_layList);
			anaylse(allItemList,allPointList,layItemList,itemPoint,a_layList,pointList);
		}
		
	}
	
	
	
	public ArrayList findSubNode(ArrayList allItemList,ArrayList allPointList,HashMap itemPoint,String item_id,ArrayList pointList)
	{
		ArrayList  a_layList=new ArrayList();
		ArrayList  subNodeList=new ArrayList();
		for(int i=0;i<allItemList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)allItemList.get(i);
			if(abean.get("parent_id")!=null&&((String)abean.get("parent_id")).equals(item_id))
			{
				a_layList.add(abean);
				subNodeList.add((String)abean.get("item_id"));
			}
		}
		
		if(a_layList.size()==0)
		{
			for(int i=0;i<allPointList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)allPointList.get(i);
				if(((String)abean.get("item_id")).equals(item_id))
				{
					subNodeList.add((String)abean.get("point_id"));
					pointList.add((String)abean.get("point_id"));
				}
			}
			itemPoint.put(item_id+"_P",subNodeList);
		}
		else
		{
			itemPoint.put(item_id+"_I",subNodeList);
			
		}
		return a_layList;
	}
	
	
	
/***********************************************************************************************************/	
	
	/**
	 * 得到模版中包含的所有指标
	 * @param testTemplateID
	 * @return
	 */
	public ArrayList getAllPointList(String testTemplateID,String privPointStr)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			String sql="select ptp.* from per_template_item pti,per_template_point ptp "
						+"where pti.item_id=ptp.item_id and pti.template_id='"+testTemplateID+"' ";
			if(privPointStr.length()>0)
			{
				sql+=" and UPPER(ptp.point_id) in('"+privPointStr.replaceAll(",","','").toUpperCase()+"')";
			}
			sql+=" order by ptp.seq ";
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("point_id",rowSet.getString("point_id"));
				abean.set("item_id",rowSet.getString("item_id"));
				abean.set("score",rowSet.getString("score"));
				abean.set("seq",rowSet.getString("seq"));
				abean.set("rank",rowSet.getString("rank"));
				
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 * 得到模版中包含的所有项目集合
	 * @param testTemplateID
	 * @return
	 */
	public ArrayList getAllItemList(String testTemplateID)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select * from per_template_item where template_id='"+testTemplateID+"'  order by seq");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("item_id",rowSet.getString("item_id"));
				if(rowSet.getString("parent_id")==null||rowSet.getString("parent_id").length()==0) {
                    abean.set("parent_id","");
                } else {
                    abean.set("parent_id",rowSet.getString("parent_id"));
                }
				abean.set("child_id",rowSet.getString("child_id"));
				abean.set("template_id",rowSet.getString("template_id"));
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				abean.set("seq",rowSet.getString("seq"));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	/**
	 * 创建 评测打分信息表
	 */
	public void createZp_test_templateTable(DbWizard dbWizard)
	{
		Table table=new Table("zp_test_template");
		if(!dbWizard.isExistTable(table.getName(),false))
		{
	    	table.setCreatekey(true);
	    	ArrayList fieldList=getZP_TEST_TEMPLATE_TableFields();
		    createTable(table,fieldList,dbWizard);
		}
	}
	
	
	
	/**
	 * 创建表
	 * @param table
	 * @param fieldList
	 * @param dbWizard
	 */
	public void createTable(Table table,ArrayList fieldList,DbWizard dbWizard)
	{
		try
		{
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				Field field=(Field)t.next();
				table.addField(field);
			}
			if(dbWizard.isExistTable(table.getName(),false))
			{						
				dbWizard.dropTable(table);				
			}
			dbWizard.createTable(table);	

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 创建表  只增加字段
	 * @param table
	 * @param fieldList
	 * @param dbWizard
	 */
	public void createTableAbsolutely(Table table,ArrayList fieldList,DbWizard dbWizard)
	{
		try
		{	boolean bool=false;
			for(Iterator t=fieldList.iterator();t.hasNext();)
			{
				Field field=(Field)t.next();
				if(!dbWizard.isExistField(table.getName(),field.getName(),false)){
					table.addField(field);
					bool=true;
				}
			}
			if(bool) {
                dbWizard.addColumns(table);// 更新列
            }
//			if(dbWizard.isExistTable(table.getName(),false))
//			{						
//				dbWizard.dropTable(table);				
//			}
//			dbWizard.createTable(table);	
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到评测打分信息表（zp_test_template）的列信息
	 * @return
	 */
	public ArrayList getZP_TEST_TEMPLATE_TableFields()
	{
		ArrayList list=new ArrayList();
		
		Field aField0=getField(true,"A0100_1",ResourceFactory.getProperty("hire.parameterSet.interviewerNo"),"A",30,0);
		list.add(aField0);
		Field aField1=getField(true,"Z0101",ResourceFactory.getProperty("hire.parameterSet.engagePlanNo"),"A",10,0);
		list.add(aField1);
		Field aField2=getField(true,"A0100",ResourceFactory.getProperty("hire.parameterSet.interviewerNo"),"A",30,0);
		list.add(aField2);
		Field aField3=getField(false,"score",ResourceFactory.getProperty("lable.performance.singleGrade.value"),"N",15,4);
		list.add(aField3);
		Field aField4=getField(false,"amount",ResourceFactory.getProperty("hire.parameterSet.metenumber"),"N",15,4);
		list.add(aField4);
		Field aField5=getField(true,"point_id",ResourceFactory.getProperty("hire.parameterSet.factor"),"A",30,0);
		list.add(aField5);
		Field aField6=getField(false,"degree_id",ResourceFactory.getProperty("hire.parameterSet.tagNumber"),"A",1,0);
		list.add(aField6);
		return list;
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param primaryKey	是否是主键
	 * @param fieldname     列名
	 * @param fieldDesc     列描述
	 * @param type          数据类型
	 * @param length        长度
	 * @param decimalLength 小数点位数
	 * @return
	 */
	public Field getField(boolean primaryKey,String fieldname,String fieldDesc,String type,int length,int decimalLength)
	{
		Field obj=new Field(fieldname,fieldDesc);
		if("A".equals(type))
		{	
			obj.setDatatype(DataType.STRING);
			obj.setKeyable(primaryKey);	
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);
			obj.setLength(length);
			
		}
		else if("M".equals(type))
		{
			obj.setDatatype(DataType.CLOB);
			obj.setKeyable(false);			
			obj.setVisible(true);
			obj.setAlign("left");					
		}
		else if("D".equals(type))
		{
			
			obj.setDatatype(DataType.DATE);
			obj.setKeyable(false);			
			obj.setVisible(true);												
		}	
		else if("N".equals(type))
		{
			obj.setDatatype(DataType.FLOAT);
			obj.setDecimalDigits(decimalLength);
			obj.setLength(length);							
			obj.setKeyable(primaryKey);		
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);								
		}	
		else if("I".equals(type))
		{		
			obj.setDatatype(DataType.INT);
			obj.setKeyable(primaryKey);		
			if(primaryKey) {
                obj.setNullable(false);
            } else {
                obj.setNullable(true);
            }
			obj.setVisible(true);	
		}		
		return obj;
	}
	public String getOrgFieldNames(String orgFieldIDs){
		StringBuffer str_b=new StringBuffer();
		try{
		    if(orgFieldIDs !=null || orgFieldIDs.trim().length()>0){
			    int in = orgFieldIDs.indexOf(",");
			    if(in == -1){
			    	 ContentDAO dao = new ContentDAO(this.conn);
			         RowSet rowSet=null;
			         rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid='B01' and itemtype='M' and itemid ='"+orgFieldIDs+"'  and useflag='1'");
			         while(rowSet.next()){
				         str_b.append("介绍指标:");
				         str_b.append(rowSet.getString("itemdesc"));
			         }
			
			    }else{
		             String[] strArr=orgFieldIDs.split(",");
		   
		             ContentDAO dao = new ContentDAO(this.conn);
		             RowSet rowSet=null;
		             rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid='B01' and itemtype='M' and itemid ='"+strArr[0]+"'  and useflag='1'");
		             while(rowSet.next()){
			             str_b.append("介绍指标:");
			             str_b.append(rowSet.getString("itemdesc"));
		             }
		             rowSet = dao.search("select * from fielditem where fieldsetid = 'b01' and codesetid ='43' and itemid='"+strArr[1]+"'  and useflag='1'");
		             while(rowSet.next()){
		        	     str_b.append("  ");
			             str_b.append("内容形式指标:");
			             str_b.append(rowSet.getString("itemdesc"));
		            }
			     }
		        
		       }else {
                str_b.append("");
            }
		}catch(Exception e){
	        e.printStackTrace();
       }
		       return str_b.toString();
	}
	public String getResumeFieldNames(String resumeFieldIds){
		StringBuffer str = new StringBuffer();
		StringBuffer sql = new StringBuffer();
		HashMap hm = new HashMap();
		RowSet rs = null;
		RowSet rs1 = null;
		if(resumeFieldIds != null && resumeFieldIds.trim().length()>0){
			if(resumeFieldIds.indexOf(",") != -1){
				String[] str_Arr = resumeFieldIds.split(",");
				for(int i=0;i<str_Arr.length;i++){
					sql.append(",'");
					sql.append(str_Arr[i]);
					sql.append("'");
				}
				try{
					ContentDAO dao = new ContentDAO(this.conn);
				    rs = dao.search("select itemid,itemdesc from fielditem where itemid in ("+sql.toString().substring(1)+")  and useflag='1'");
				    rs1 = dao.search("select itemid,itemdesc from t_hr_busifield where fieldsetid='Z03' and useflag='1' and state= '1'");
				    while(rs.next()){
				    	if(rs.getString("itemdesc")==null || rs.getString("itemdesc").trim().length()<=0)
				    	{
				    	continue;
				    	}
				    	else
				    	{
				          hm.put(rs.getString("itemid").toLowerCase(),rs.getString("itemdesc"));
				    	}
				    	
				    }
				    while(rs1.next()){
				    	if(rs1.getString("itemdesc")==null || rs1.getString("itemdesc").trim().length()<=0)
				    	{
				    	continue;
				    	}
				    	else
				    	{
				          hm.put(rs1.getString("itemid").toLowerCase(),rs1.getString("itemdesc"));
				    	}
				    	
				    }
				    for(int i=0;i<str_Arr.length;i++){
				    	if(hm.get(str_Arr[i].toLowerCase())==null)
				    	{
				    		continue;
				    	}
				    	else
				    	{
				    	str.append(",");
				    	str.append(hm.get(str_Arr[i].toLowerCase()));
				    	}
				    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				try{
					ContentDAO dao = new ContentDAO(this.conn);
					  String fieldsetid = "";
					  rs = dao.search("select fieldsetid from t_hr_busifield where itemid ='"+resumeFieldIds+"'");
					  if(rs.next()){
						  fieldsetid=rs.getString("fieldsetid");
					  }
					  if("z03".equalsIgnoreCase(fieldsetid)){
					    rs = dao.search("select itemid,itemdesc from t_hr_busifield where itemid ='"+resumeFieldIds+"' and fieldsetid='Z03' and useflag='1' and state= '1'");
					  }else{
					    rs = dao.search("select itemdesc from fielditem where itemid ='"+resumeFieldIds+"'  and useflag='1'");
					  }
					  while(rs.next()){
						str.append(",");
						str.append(rs.getString("itemdesc"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			if(StringUtils.isNotEmpty(str.toString())) {
                str.setLength(str.length() - 1);
            }
            
            return str.toString();
		}else {
            return "";
        }
		
	}
	public String getResumeStateFieldNames(String resumeStateFieldIds){
		StringBuffer str = new StringBuffer();
		StringBuffer sql= new StringBuffer();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		if(resumeStateFieldIds != null && resumeStateFieldIds.trim().length()>0){
			if(resumeStateFieldIds.indexOf(",") != -1){
				String[] str_Arr = resumeStateFieldIds.split(",");
				for(int i=0;i<str_Arr.length;i++){
					sql.append(",'");
					sql.append(str_Arr[i]);
					sql.append("'");
				}
				try{
					rs=dao.search("select itemdesc from fielditem where fieldsetid='A01' and codesetid='36' and itemid in ("+sql.toString().substring(1)+")  and useflag='1'");
					while(rs.next()){
						str.append(",");
						str.append(rs.getString("itemdesc"));
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}else{
				try{
					rs=dao.search("select itemdesc from fielditem where fieldsetid='A01' and codesetid='36' and itemid='"+resumeStateFieldIds+"' and useflag='1'");
					while(rs.next()){
						str.append(",");
						str.append(rs.getString("itemdesc"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return str.toString().substring(1);
		}else {
            return "";
        }
		
	}
	public ArrayList getPersonTypeList(){
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		String str=this.getSelectedA01Ids();
		CommonData cd=new CommonData("#","请选择... ");
		list.add(cd);
		String sql = "select itemid,itemdesc from fielditem where fieldsetid='A01' and codesetid='44' and useflag='1'";
		if(str.length()>0) {
            sql+=" and itemid not in("+str+")";
        }
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 简历回复指标列表
	 * @param selectedids
	 * @return
	 */
	public ArrayList getInterviewingRevertItemList(String selectedids){
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		String str=this.getSelectedA01Ids();
		CommonData cd=new CommonData("#","请选择... ");
		list.add(cd);
		String sql = "select itemid,itemdesc from fielditem where fieldsetid='A01' and UPPER(itemtype)='A' and codesetid!='0' and useflag='1'";
		if(str.length()>0) {
            sql+=" and UPPER(itemid) not in("+str+")";
        }
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public String getSelectedA01Ids(){
		StringBuffer str_b = new StringBuffer();
		String id_str = "";
		String str_a = "";
		String sql ="select str_value from constant where constant='ZP_FIELD_LIST'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				id_str = rs.getString("str_value");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(id_str != null && id_str.trim().length()>0){
			String[] str_Arr = id_str.split(",},");
			for(int i=0;i<str_Arr.length;i++){
				if(!"A01".equals(str_Arr[i].substring(0,str_Arr[i].indexOf('{')))){
				  continue;
				}else{
					str_a = str_Arr[i].substring(str_Arr[i].indexOf("{")+1);
					String[] str_Brr = str_a.split(",");
					for(int j=0;j<str_Brr.length;j++){
						if(str_Brr[j].indexOf("[")!=-1){
							str_b.append(",");
							str_b.append("'");
							str_b.append(str_Brr[j].substring(0,str_Brr[j].indexOf("[")));
							str_b.append("'");
						}else{
							str_b.append(",");
							str_b.append("'");
							str_b.append(str_Brr[j]);
							str_b.append("'");
						}
					}
				}
			}
			if(str_b.toString().length()>0){
				return str_b.toString().substring(1);
			}else{
				return "";
			}
			
		}else {
            return "";
        }
	}
	public ArrayList getParaNameListByParaValue(String paraValue,int flag){
		ArrayList list = new ArrayList();
		String[] str_Arr = null;
		StringBuffer str = new StringBuffer();
		HashMap hm = new HashMap();
		RowSet rs = null;
		RowSet rs1 = null;
		boolean yprsl=false;
		boolean ypljl=false;
		boolean opentime=false;
		if(paraValue.indexOf(",") != -1){
			str_Arr = paraValue.split(",");
			for(int i=0;i<str_Arr.length;i++){
				str.append(",'");
				str.append(str_Arr[i]);
				str.append("'");
			}
		}else if(paraValue.indexOf("`")!= -1){
			str_Arr = paraValue.split("`");
			for(int j=0;j<str_Arr.length;j++){
				str.append(",'");
				str.append(str_Arr[j]);
				str.append("'");
			}
		}else{
			str.append(",'");
			str.append(paraValue);
			str.append("'");
		}
		String s= str.toString();
		try{
		    ContentDAO dao = new ContentDAO(this.conn);
		    if(flag == 0) {
                rs=dao.search("select itemid,itemdesc from fielditem where itemid in ("+str.toString().substring(1)+")  and useflag='1' and fieldsetid='Z03'");
            }
		        rs1=dao.search("select itemid,itemdesc from t_hr_busifield where itemid in ("+str.toString().substring(1)+")  and useflag='1' and state= '1' and fieldsetid='Z03'");
		    if(flag == 1) {
                rs = dao.search("select itemid,itemdesc from t_hr_busiField where itemid in("+str.toString().substring(1)+") and useflag='1'  and fieldsetid='Z03' ");
            }
		    while(rs.next()){
		    	CommonData dataobj = new CommonData(rs.getString("itemid"),rs.getString("itemdesc"));
		    	hm.put((rs.getString("itemid")).toLowerCase(),dataobj);
		    }
		    while(rs1.next()){
		    	CommonData dataobj = new CommonData(rs1.getString("itemid"),rs1.getString("itemdesc"));
		    	hm.put((rs1.getString("itemid")).toLowerCase(),dataobj);
		    }
		    if(str_Arr != null){
		        for(int h=0;h<str_Arr.length;h++){
		        	if(hm.get(str_Arr[h].toLowerCase())!=null&&!"yprsl".equals(str_Arr[h].toLowerCase())
		        			&&!"ypljl".equals(str_Arr[h].toLowerCase())&&!"opentime".equals(str_Arr[h].toLowerCase())){
		        		list.add(hm.get(str_Arr[h].toLowerCase()));
		        	}else if("yprsl".equals(str_Arr[h].toLowerCase())){
				    	list.add(new CommonData("yprsl","应聘人数(推荐人数)"));
		        	}else if("ypljl".equals(str_Arr[h].toLowerCase())){
				    	list.add(new CommonData("ypljl","应聘(推荐)"));
		        	}else if("opentime".equals(str_Arr[h].toLowerCase())){
				    	list.add(new CommonData("opentime","发布日期"));
		        	}
		        }
		    }else{
		    	if(hm.get(paraValue.toLowerCase())!=null) {
                    list.add(hm.get(paraValue.toLowerCase()));
                }
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	public ArrayList getResumeStateFieldList(String str){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		list.add(new CommonData("#","请选择..."));
		try{
			String sql = "select itemid,itemdesc from fielditem where codesetid='36' and fieldsetid='A01' and useflag='1'";
			if(str !=null && str.trim().length()>0) {
                sql+="and itemid not in("+str+")";
            }
			ContentDAO dao = new ContentDAO(this.conn);
			rs= dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getResumeLevelFieldList(){
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		//String str=this.getSelectedA01Ids();
		CommonData cd=new CommonData("#","请选择... ");
		list.add(cd);
		String sql = "select codesetid,codesetdesc from codeset";
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("codesetid"),rs.getString("codesetdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public String getLicenseAgreementParameter()
	{
		String str="";
		try
		{
			String sql="select * from constant where UPPER(constant)='ZP_LICENSE_AGREEMENT'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String str_value=Sql_switcher.readMemo(rs, "str_value");
				if(str_value==null|| "".equals(str_value))
				{
					str=ResourceFactory.getProperty("hire.agreement.nodefinition");
				}
				else
				{
					str=ResourceFactory.getProperty("hire.agreement.definition");
				}
			}
			if(str==null|| "".equals(str)) {
                str=ResourceFactory.getProperty("hire.agreement.nodefinition");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getPromptContentParameter()
	{
		String str="";
		try
		{
			String sql="select * from constant where UPPER(constant)='ZP_PROMPT_CONTENT'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String str_value=Sql_switcher.readMemo(rs, "str_value");
				if(str_value==null|| "".equals(str_value))
				{
					str=ResourceFactory.getProperty("hire.prompt.nodefinition");
				}
				else
				{
					str=ResourceFactory.getProperty("hire.prompt.definition");
				}
			}
			if(str==null|| "".equals(str)) {
                str=ResourceFactory.getProperty("hire.prompt.nodefinition");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public ArrayList getCodeItem(String codesetid)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(codesetid==null|| "#".equals(codesetid))
			{
			  list.add(new CommonData("#","请选择..."));
			  return list;
			}
			String sql = "select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"'order by codeitemid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getCodeItemNet(String codesetid)
	{
		
		
		ArrayList list = new ArrayList();
		try
		{
			if(cultureList!=null) {
                return cultureList;
            }
			if(codesetid==null|| "#".equals(codesetid))
			{
			  list.add(new CommonData("#","请选择..."));
			  return list;
			}
			String sql = "select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"'order by codeitemid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}
			cultureList=list;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getResumeStaticNames(String resumeStaticIds){
		StringBuffer str= new StringBuffer();
		StringBuffer return_str = new StringBuffer();
		RowSet rs = null;
		String[] str_Arr=null;
		HashMap hm = new HashMap();
		if(resumeStaticIds.indexOf(",") != -1){
		    str_Arr = resumeStaticIds.split(",");
			for(int i=0;i<str_Arr.length;i++){
				str.append(",");
				str.append("'");
				str.append(str_Arr[i]);
				str.append("'");
			}
		}else{
			str.append(",");
			str.append("'");
			str.append(resumeStaticIds);
			str.append("'");
		}
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select itemid,itemdesc from fielditem where itemid in ("+str.toString().substring(1)+")");
			while(rs.next()){
				hm.put(rs.getString("itemid").toLowerCase(),rs.getString("itemdesc"));
			}
			if(str_Arr != null){
				for(int i=0;i<str_Arr.length;i++){
					String value = hm.get(str_Arr[i].toLowerCase())==null?"":(String)hm.get(str_Arr[i].toLowerCase());
					if(!"".equals(value)){
					    return_str.append(',');
					    return_str.append(hm.get(str_Arr[i].toLowerCase()));
					}
				}
			}else{
				return_str.append(",");
				return_str.append(hm.get(resumeStaticIds.toLowerCase()));
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(return_str == null || return_str.toString().trim().length()<=0) {
            return "";
        } else {
            return return_str.toString().substring(1);
        }
	}
	
	
	/**
	 * 取得招聘对象指标列表
	 */
	public ArrayList getHireObjectList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData("#","请选择..."));
		String sql = "select itemid,itemdesc from t_hr_busiField where UPPER(fieldsetid)='Z03' and UPPER(itemid)='Z0336' and codesetid = '35' and useflag='1'";
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet  rs =  null;
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取所有登记表
	 * @return
	 */
	public ArrayList getAllPreviewTable()
	{
		ArrayList list= new ArrayList();
		try
		{
			String sql = "select tabid,name from rname where upper(flaga) like 'A%' order by tabid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			list.add(new CommonData("#","请选择..."));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("tabid"),rs.getString("name")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getOrgRegisterTable()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select tabid,name from rname where upper(flaga) like 'B%' order by tabid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			list.add(new CommonData("#","请选择..."));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("tabid"),rs.getString("name")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getCommonQueryCondList(String type)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer strsql = new StringBuffer();
			strsql.append("select id,name,type from lexpr where type='");//
	        strsql.append(type);
	        strsql.append("' order by id");
	        ContentDAO dao = new ContentDAO(this.conn);
	        RowSet rs = null;
	        rs = dao.search(strsql.toString());
	       //list.add(new CommonData("","请选择..."));
	        while(rs.next())
	        {
	        	list.add(new CommonData(rs.getString("id"),rs.getString("id")+":"+rs.getString("name")));
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getSelectedCommonQueryCondList(String ids,String type)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(ids==null|| "".equals(ids)) {
                return list;
            }
			StringBuffer strsql = new StringBuffer();
			strsql.append("select id,name,type from lexpr where type='");//
	        strsql.append(type);
	        strsql.append("' and id in("+ids+") order by id");
	        ContentDAO dao = new ContentDAO(this.conn);
	        RowSet rs = null;
	        rs = dao.search(strsql.toString());
	        while(rs.next())
	        {
	        	list.add(new CommonData(rs.getString("id"),rs.getString("id")+":"+rs.getString("name")));
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getCommonQueryNames(ArrayList list)
	{
		String str="";
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				CommonData data = (CommonData)list.get(i);
				buf.append(data.getDataName());
				buf.append("<br>");
			}
			if(buf.length()>0) {
                str=buf.toString().substring(0,buf.length()-4);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getBusniessTemplateInfo(String ids)
	{
		String str="";
		try
		{
			StringBuffer buf = new StringBuffer("");
			if(ids==null|| "".equals(ids)) {
                return buf.toString();
            }
			String sql = " select tabid,name from template_table where tabid in("+ids+")";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				String t_n=rs.getString("tabid")+":"+rs.getString("name");
				buf.append(t_n+"<br>");	
			}
			if(buf.length()>0) {
                str=buf.toString().substring(0,buf.length()-4);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 取得简历状态的代码值（初试环节）
	 * @return
	 */
	public ArrayList getResumeCodeList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql="select codeitemid,codeitemdesc from codeitem where codesetid='36' and parentid=1 and codeitemid<>parentid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getSelectedResumeCodeList(String ids)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(ids==null|| "".equals(ids)) {
                return list;
            }
			StringBuffer id_buf=new StringBuffer();
			String[] arr=ids.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				id_buf.append(",'"+arr[i]+"'");
			}
			String sql="select codeitemid,codeitemdesc from codeitem where codesetid='36' and codeitemid in("+id_buf.toString().substring(1)+")";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getResumeCodeInfo(String ids)
	{
		String str="";
		try
		{
			StringBuffer buf = new StringBuffer("");
			if(ids==null|| "".equals(ids)) {
                return str;
            }
			StringBuffer id_buf=new StringBuffer();
			String[] arr=ids.split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				id_buf.append(",'"+arr[i]+"'");
			}
			if(id_buf.length()>0)
			{
		    	 String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='36' and codeitemid in ("+id_buf.toString().substring(1)+")";
			     ContentDAO dao = new ContentDAO(this.conn);
			     RowSet rs =dao.search(sql);
			     while(rs.next())
			     {
			    	 String t_n=rs.getString("codeitemid")+":"+rs.getString("codeitemdesc");
					 buf.append(t_n+"<br>");	
			     }
			     if(buf.length()>0) {
                     str=buf.toString().substring(0,buf.length()-4);
                 }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
//下面是备份，还原文件的方法
	/**
	 * 将文件压入压缩文件中
	 * @param onlyFileName  文件压缩进压缩包后是否需要带文件路径    true 只有文件名
	 */
	 public void fileToZip(File file,ZipOutputStream zos,boolean onlyFileName)throws Exception{
		 BufferedInputStream bis = null;
		 FileInputStream fis = null;
		 try
		 {
    		  //ZipEntry   ze   =   new   ZipEntry(new String(file.getPath().getBytes("GB2312")));
			 /**如果直接用getAbsolutePath()则压缩文件中将是绝对路径*/
    		String path = "";
    		if(!onlyFileName) {
                path = file.getAbsolutePath().replaceAll("\\\\","/");
            } else {
                path = file.getName();
            }
    		String[] s = path.split("/");	
    		/**gb 2312解决中文的文件名问题，要不出现乱码并且ZipEntry和ZipOutputStream要是org.apache.tools.zip包中的*/
			ZipEntry ze = new ZipEntry(new String(getPath(s).getBytes("GB2312")));
			zos.putNextEntry(ze);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis,1024);
			byte b[] = new byte[1024];
			int len;
			while ((len=fis.read(b))!=-1){   //bis 缓冲为什么不好使？
			  zos.write(b,0,len);
		    }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 } finally {
			 PubFunc.closeIoResource(bis);
			 PubFunc.closeIoResource(fis);
		 }
	 } 
	 /**
	  * 递归遍历所有目录
	  * @param file
	  * @param zos
	  * @throws Exception
	  */
	 public   void   directoryToZip(File   file,ZipOutputStream   zos)throws   Exception{  
		 try
		 {
    		  File   listfile[]   =   file.listFiles();   
    		  if(listfile.length==0){ 
    			  /**如果直接用getAbsolutePath()则压缩文件中将是绝对路径*/
    			  String path = file.getAbsolutePath().replaceAll("\\\\","/");
    			 
    			  String[] s = path.split("/");
    			  /**gb 2312解决中文的文件名问题，要不出现乱码并且ZipEntry和ZipOutputStream要是org.apache.tools.zip包中的*/
    	          ZipEntry   ze   =   new   ZipEntry(new String(getPath(s).getBytes("GB2312")));   
    	          zos.putNextEntry(ze);   
    	          return;   
    	      }   
    		  for(int   i=0;i<listfile.length;i++)
    		  {   
    	    	  File   lf   =   listfile[i];   
        		  if(lf.isFile())
        		  {   
        			  /**是文件的放入压缩流中*/
    	          	  this.fileToZip(lf,zos,false);   
        		  }
        		  else if(lf.isDirectory())
        		  { 
        			  /**递归遍历所有目录*/
            		  this.directoryToZip(lf,zos);   
        		  }   
	    	  }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	 public   String   inputZip(String inFileName,String outFileName)throws   Exception
	 {   
	     try {
             ServletContext context = SystemConfig.getServletContext();
             inFileName = context.getRealPath("/UserFiles");
         } catch (Exception e) {
         }
	     
		 FileOutputStream   fos   =   null;
		  BufferedOutputStream   bos   =   null;
		  ZipOutputStream   zos   =  null;
		 try
		 {
    		  File file = new  File(inFileName);   
    		  if(!file.exists()){
    		  	return "1";
    		  } 
    	      fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outFileName+".zip");
    		  bos = new BufferedOutputStream(fos,1024);
    		  zos = new ZipOutputStream(bos);
		    
    		  if(file.isFile()){   
    		  this.fileToZip(file,zos,false);   
    		  }else if(file.isDirectory()){   
    		  this.directoryToZip(file,zos);   
    		  }      		 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
		 	PubFunc.closeResource(zos);
		 	PubFunc.closeResource(bos);
		 	PubFunc.closeResource(fos);
		 }
		 return outFileName;
	 } 
	 /**
	  * 解决相对路径问题
	  * @param patharr
	  * @return
	  */
	 public String getPath(String[] patharr)
	 {
		 String path="";
		 try
		 {
			 int j=0;
			 for(int i=0;i<patharr.length;i++)
			 {
				 if("userfiles".equalsIgnoreCase(patharr[i]))
				 {
					 j=i;
					 break;
				 }
			 }
			 for(int i=0;i<patharr.length;i++)
			 {
				 if(i>=j)
				 {
					 path=path+patharr[i]+((i==patharr.length-1)?"":"\\");
				 }
			 }
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return path;
	 }
//下面是解压缩的方法
/**
 * 得到解压缩后的文件的存放目录
 */
  public String getParentPath(String path)
  {
	  String temp_path = "";
	  try
	  {
		  String temp = path.replaceAll("\\\\","/"); 
		  String[] s = temp.split("/");
		  for(int i=0;i<s.length;i++)
		  {
			  if(i!=s.length-1)
			  {
	     		  temp_path=temp_path+s[i]+"\\";
			  }
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return temp_path;
  }
  public void reductionFile(InputStream inputStream,String filePath)
  {
	  FileOutputStream fos=null;
	  BufferedOutputStream bos=null;
	  BufferedInputStream bis =null;
	  ZipInputStream   in = null;
	  try   
	  {   
		  in   =   new   ZipInputStream(inputStream);
		  int BUFFER = 2048;
		  java.util.zip.ZipEntry   entry   =   null;
		  while (( entry = in.getNextEntry())!=null)
		  {
			  //会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
				if (entry.isDirectory())
				{
				 new File(filePath, entry.getName()).mkdirs();
				 continue;
				}
				File file = new File(filePath, entry.getName());
				//加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
				//而这个文件所在的目录还没有出现过，所以要建出目录来。
				File parent = file.getParentFile();
				if(parent != null && (!parent.exists())){
				parent.mkdirs();
				}
				bis = new BufferedInputStream(in);
				 fos = new FileOutputStream(file);
				 bos = new BufferedOutputStream(fos,BUFFER);

				 int count;
				 byte data[] = new byte[BUFFER];
				 while ((count = bis.read(data, 0, BUFFER)) != -1)
				{
					 fos.write(data, 0, count); //bos  缓冲为什么不好使？
				}
			  	bos.close();
				fos.close();
				bis.close();

		  }
	    
	  }   
	  catch   (IOException   e)   {   
		  e.printStackTrace();
	  }  
	  finally
	  {
		  PubFunc.closeIoResource(bos);
		  PubFunc.closeIoResource(fos);
		  PubFunc.closeIoResource(bis);
		  PubFunc.closeIoResource(in);
	  }
  }
	    /**
	     * 将压缩文件解压到项目的对应目录下
	     * @param destfile 存放解压缩后的文件的目录
	     * @param formfile 源文件，即要解压的文件的路径
	     */
    public void unzip(String destfile, FormFile formfile) {
        int BUFFER = 2048;
        ZipFile zipFile = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        BufferedInputStream fbis = null;
        BufferedOutputStream fbos = null;
        FileOutputStream ffos = null;
        try {

            String tempname = "UserFiles.zip";
			ffos = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + tempname);
			fbis = new BufferedInputStream(formfile.getInputStream());
			fbos = new BufferedOutputStream(ffos, BUFFER);
			int c;
			byte d[] = new byte[BUFFER];
			while ((c = fbis.read(d, 0, BUFFER)) != -1) {
				fbos.write(d, 0, c);
			}
            String filePath = destfile;
            // System.out.println(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempname);
            zipFile = new ZipFile(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + tempname);
            // ZipInputStream in = new
            // ZipInputStream(formfile.getInputStream());
            Enumeration emu = zipFile.getEntries();
            while (emu.hasMoreElements()) {
                try {
                    ZipEntry entry = (ZipEntry) emu.nextElement();
                    // 会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
                    if (entry.isDirectory()) {
                        new File(filePath, entry.getName()).mkdirs();
                        continue;
                    }
                    // BufferedReader ain=new BufferedReader(new
                    // InputStreamReader(in));
                    bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    File file = new File(filePath, entry.getName());
                    // 加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
                    // 而这个文件所在的目录还没有出现过，所以要建出目录来。
                    File parent = file.getParentFile();
                    if (parent != null && (!parent.exists())) {
                        parent.mkdirs();
                    }
                    fos = new FileOutputStream(file);
                    bos = new BufferedOutputStream(fos, BUFFER);

                    int count;
                    byte data[] = new byte[BUFFER];
                    while ((count = bis.read(data, 0, BUFFER)) != -1) {
                        bos.write(data, 0, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        bos.flush();
                    }
                    if (fos != null) {
                        fos.flush();
					}
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(zipFile);
        	PubFunc.closeResource(bos);
        	PubFunc.closeResource(bis);
        	PubFunc.closeResource(fos);
        	PubFunc.closeResource(ffos);
        	PubFunc.closeResource(fbos);
        	PubFunc.closeResource(fbis);
        }
    }
		public String changeFormat(String value, String sep) {
			String str = "";
			try {
				if (value == null || "".equals(value)) {
                    return str;
                }
				String[] temp = value.split("-");
				if (temp.length == 3) {
                    str =temp[0] + sep  + temp[1]+sep + temp[2];
                }

			} catch (Exception e) {
				e.printStackTrace();
			}
			return str;
		}
		
		/**
		 * 初始化招聘数据
		 * @param timetype
		 * @param stime
		 * @param etime
		 * @throws GeneralException
		 */
		public void initHireData(String timetype,String stime,String etime,String tableStr,String setStr) throws GeneralException
		{
			try
			{
				String newStime=this.changeFormat(stime, ".");
				String newEtime=this.changeFormat(etime, ".");
				 
				DbWizard dbWizard=new DbWizard(this.conn);
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs=null; 
				/**招聘需求号*/
				StringBuffer z0301_where=new StringBuffer("select z0301 from z03 ");
				if(!"0".equals(timetype)) {
                    z0301_where.append(" where  z0307  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime));
                }
				/**用工需求表*/
				PositionStatBo psb = new PositionStatBo(this.conn); 
				 
				
				/**面试官安排表*/
				if(dbWizard.isExistTable("zp_examiner_arrange",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/ZP_EXAMINER_ARRANGE/")!=-1)
					{
						if("0".equals(timetype)) {
                            dao.delete("delete from zp_examiner_arrange", new ArrayList());
                        } else {
                            dao.delete("delete from zp_examiner_arrange  where z0501 in (select z0501 from z05 where z0301 in ( "+z0301_where.toString()+"  )) ", new ArrayList());
                        }
					}
				}
				
				
				/**面试安排表*/
				if(dbWizard.isExistTable("z05",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/Z05/")!=-1)
					{
						if("0".equals(timetype)) {
                            dao.delete("delete from z05", new ArrayList());
                        } else {
                            dao.delete("delete  from z05 where z0301 in ( "+z0301_where.toString()+"  )  ", new ArrayList());
                        }
					} 
				}
				
				/**人员职位对应表*/
				if(dbWizard.isExistTable("ZP_POS_TACHE",false))
				{ 
					if(tableStr.length()==0||tableStr.indexOf("/ZP_POS_TACHE/")!=-1)
					{
						if("0".equals(timetype)) {
                            dao.delete("delete from ZP_POS_TACHE", new ArrayList());
                        } else {
                            dao.delete("delete  from ZP_POS_TACHE where zp_pos_id in ( "+z0301_where.toString()+"  )  ", new ArrayList());
                        }
					}  
				}
				
				
				/**人才库*/
				if(dbWizard.isExistTable("zp_talents",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/ZP_TALENTS/")!=-1)
					{
						if("0".equals(timetype))
						{ 
			     			dao.delete(" delete from zp_talents", new ArrayList()); 
						}
						else
						{ 
							String sql="delete from zp_talents where  create_time  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime) ;
							dao.delete(sql, new ArrayList());  
						}
					}
				}
				/**浏览申请统计表*/
				if(dbWizard.isExistTable("ZP_STATIC_INFO",false))
				{ 
					if(tableStr.length()==0||tableStr.indexOf("/ZP_TALENTS/")!=-1)
					{
						if("0".equals(timetype))
						{ 
			    			dao.delete("delete from ZP_STATIC_INFO", new ArrayList()); 
						}
						else
						{ 
				    		dao.delete(" delete from ZP_STATIC_INFO where z0301 in ("+z0301_where.toString()+")", new ArrayList()); 
							 
						}
					}
				}
				
				/**招聘过程记录*/
				if(dbWizard.isExistTable("zp_process_history",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/ZP_PROCESS_HISTORY/")!=-1)
					{
						if("0".equals(timetype))
						{ 
			    			dao.delete("delete from zp_process_history", new ArrayList()); 
						}
						else
						{  
							dao.delete("delete from zp_process_history where   create_time  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime), new ArrayList()); 
						}
					}
				}
				
				
				/**招聘流程*/
				if(dbWizard.isExistTable("zp_flow_definition",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/ZP_FLOW_DEFINITION/")!=-1)
					{
						if("0".equals(timetype))
						{
			    			dao.delete("delete from zp_flow_definition", new ArrayList());
			    			dao.delete("delete from zp_flow_links", new ArrayList());
			    			dao.delete("delete from zp_flow_status", new ArrayList());
			    			dao.delete("delete from zp_flow_functions", new ArrayList());
			    			 
						}
						else
						{
						 
							StringBuffer whl=new StringBuffer(" link_id in (select id from zp_flow_links where flow_id in (select flow_id from zp_flow_definition  where    create_time  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+" ) )");
							dao.delete("delete from zp_flow_functions where "+whl.toString(), new ArrayList());
							dao.delete("delete from zp_flow_status where "+whl.toString(), new ArrayList());
							dao.delete("delete from zp_flow_links where flow_id in (select flow_id from zp_flow_definition  where    create_time  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+" )", new ArrayList());
							dao.delete("delete from zp_flow_definition  where   create_time  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+" ", new ArrayList());
		        		 
						}
					}
				}
				
				if(dbWizard.isExistTable("z03",false))
				{
					if(tableStr.length()==0||tableStr.indexOf("/Z03/")!=-1)
					{
						if("0".equals(timetype))
						{
							dao.delete("delete from z03 ",new ArrayList());
							dao.delete("delete from zp_members ",new ArrayList());
						}
						else
						{
							dao.delete("delete from z03 where  z0307  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime),new ArrayList());
							dao.delete("delete from zp_members where z0301 in ( "+z0301_where.toString()+" )",new ArrayList());
						}
						
					}
				}
				
				/**人员库与子集数据*/
				RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
				String dbname="";
				if(vo!=null) {
                    dbname=vo.getString("str_value");
                } else
				{
					throw GeneralExceptionHandler.Handle(new Exception("参数设置中没有设置应聘人才库"));
				}
				if(dbname==null|| "".equals(dbname)) {
                    throw GeneralExceptionHandler.Handle(new Exception("参数设置中没有设置应聘人才库"));
                }
				
				RowSet rowSet=null;
				rowSet=dao.search("select * from constant where constant='ZP_FIELD_LIST'");
				ArrayList list = new ArrayList();
				if(rowSet.next())
				{
					String temp=rowSet.getString("str_value");
					if(temp.trim().length()>0)
					{
						String[] temps=temp.split(",},");
						for(int i=0;i<temps.length;i++)
						{			
							String setid=temps[i].substring(0,temps[i].indexOf("{"));
							list.add(setid);
						}
					}
				}
				else
				{
					throw GeneralExceptionHandler.Handle(new Exception("没有设置招聘子集和子标项"));
				}
				 
				
				
				String sql_where="";
				if(!"0".equals(timetype)){ 
					sql_where="select a0100 from "+dbname+"A01 where    createTime  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+" ";
				}
				
				
				if(setStr!=null&&!"".equals(setStr))
				{
					boolean isDel=false;
		    		String[] setArr=setStr.split("/");
		     		for(int i=0;i<setArr.length;i++)
		    		{
		     			if(setArr[i]==null|| "".equals(setArr[i])|| "#".equals(setArr[i])) {
                            continue;
                        }
		     			if("a01".equalsIgnoreCase(setArr[i])) {
                            isDel=true;
                        } else
		     			{
			     			if("0".equals(timetype))
							{
				    			String setid=setArr[i];
				    			String tableName=dbname+setid;
				    			FieldSet set = DataDictionary.getFieldSetVo(setArr[i].toLowerCase());
				    			if("0".equals(set.getUseflag())) {
                                    continue;
                                }
				     			String dsql = " delete from "+tableName;
				     			dao.delete(dsql,new ArrayList());
							}
							else
							{ 
				    				String setid=(String)setArr[i];
				        			String tableName=dbname+setid;
				        			FieldSet set = DataDictionary.getFieldSetVo(setArr[i].toLowerCase());
					    			if("0".equals(set.getUseflag())) {
                                        continue;
                                    }
				         			String dsql = " delete from "+tableName+" where a0100 in ("+sql_where+")";
								    dao.delete(dsql, new ArrayList()); 
							}
		     			}
		    		}
		     		if(isDel)
		     		{
							/**清空简历照片*/
						if("0".equals(timetype))
						{
								String dsql=" delete from "+dbname+"a00 ";//where flag='P'//包括简历附件
								dao.delete(dsql, new ArrayList());
						}
						else
						{
								String dsql = " delete from "+dbname+"a00 where a0100 in ("+sql_where+") ";//and flag='P'包括简历附件
							    dao.delete(dsql, new ArrayList());
						}
						 
		     			
		     			if("0".equals(timetype)) {
                            dao.delete("delete from "+dbname+"A01 ",new ArrayList());
                        } else {
                            dao.delete(" delete from "+dbname+"A01 where    createTime  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+" ", new ArrayList());
                        }
		     		}
		     		
				}
				else
				{ 
					if(list.size()>0)
					{
						/**清空简历照片*/
						if("0".equals(timetype))
						{
							String dsql=" delete from "+dbname+"a00 ";//where flag='P'//包括简历附件
							dao.delete(dsql, new ArrayList());
						}
						else
						{
							String dsql = " delete from "+dbname+"a00 where a0100 in ("+sql_where+") ";//and flag='P'包括简历附件
						    dao.delete(dsql, new ArrayList());
						}
					}
					for(int i=0;i<list.size();i++)
					{
						String setid=(String)list.get(i);
						if("0".equals(timetype))
						{ 
			    			FieldSet set = DataDictionary.getFieldSetVo(setid.toLowerCase());
			    			if("0".equals(set.getUseflag())) {
                                continue;
                            }
			    			String tableName=dbname+setid;
			     			String dsql = " delete from "+tableName;
			     			dao.delete(dsql,new ArrayList());
						}
						else
						{
							if(!"A01".equalsIgnoreCase(setid))
							{
			    				
			    				FieldSet set = DataDictionary.getFieldSetVo(setid.toLowerCase());
				    			if("0".equals(set.getUseflag())) {
                                    continue;
                                }
			        			String tableName=dbname+setid;
			         			String dsql = " delete from "+tableName+" where a0100 in ( "+sql_where+" )";
							    dao.delete(dsql, new ArrayList());
							}
						}
					}
					if(list.size()>0&&!"0".equals(timetype)) {
                        dao.delete("delete from "+dbname+"A01 where    createTime  between "+Sql_switcher.dateValue(newStime)+" and "+Sql_switcher.dateValue(newEtime)+"",new ArrayList());
                    }
				}	
				/**清空外网访问次数统计*/
				RecordVo SYS_COUNTER=ConstantParamter.getConstantVo("SYS_COUNTER");
				if(SYS_COUNTER!=null)
				{
					SYS_COUNTER.setString("str_value", "0");
					dao.updateValueObject(SYS_COUNTER);
				}
			 
				
				 
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		/**
		 * 取得招聘中用到的表
		 * @return
		 */
		public ArrayList getTableList()
		{
			ArrayList list = new ArrayList();
			try
			{
				LazyDynaBean bean = null;
				 
				
				bean = new LazyDynaBean();
				bean.set("tablename","招聘职位表");
				bean.set("table","Z03");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("tablename","面试安排表");
				bean.set("table","Z05");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("tablename","面试官安排表");
				bean.set("table","zp_examiner_arrange");
				list.add(bean);
				

				bean = new LazyDynaBean();
				bean.set("tablename","应聘人员与职位对应关系表");
				bean.set("table","ZP_POS_TACHE");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("tablename","人才库");
				bean.set("table","zp_talents");
				list.add(bean);
				 
				bean = new LazyDynaBean();
				bean.set("tablename","招聘过程记录");
				bean.set("table","zp_process_history");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("tablename","招聘流程");
				bean.set("table","zp_flow_definition");
				list.add(bean);
				
				bean = new LazyDynaBean();
				bean.set("tablename","浏览申请统计表");
				bean.set("table","ZP_STATIC_INFO");
				list.add(bean);
				
				
				 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		/**
		 * 招聘用到的主集和子集
		 * @return
		 */
		public ArrayList getFieldSetList()
		{
			ArrayList list = new ArrayList();
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
				String dbname="";
				if(vo!=null) {
                    dbname=vo.getString("str_value");
                }
				RowSet rowSet=null;
				rowSet=dao.search("select * from constant where constant='ZP_FIELD_LIST'");
				StringBuffer buf = new StringBuffer("");
				if(rowSet.next())
				{
					String temp=rowSet.getString("str_value");
					if(temp.trim().length()>0)
					{
						String[] temps=temp.split(",},");
						for(int i=0;i<temps.length;i++)
						{			
							String setid=temps[i].substring(0,temps[i].indexOf("{"));
							buf.append(",'"+setid+"'");
						}
					}
				}
				LazyDynaBean bean = null;
				if(dbname!=null&&!"".equals(dbname))
				{
		    		if(buf.toString().length()>0)
		    		{
		    			rowSet = dao.search("select fieldsetid,customdesc from fieldset where UPPER(fieldsetid) in("+buf.toString().substring(1).toUpperCase()+") order by fieldsetid");
	    			    while(rowSet.next())
	    			    {
	    			    	bean = new LazyDynaBean();
	    			    	bean.set("setid",rowSet.getString("fieldsetid"));
	    			    	bean.set("setdesc", rowSet.getString("customdesc"));
	    			    	list.add(bean);
	    			    }
		    		}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		/**
		 * 取得职位最高工资列表（k01子集构库数值型指标）
		 * @return
		 */
		public ArrayList getPositionSalaryStandardItemList()
		{
			ArrayList list = new ArrayList();
			try
			{
				String sql = "select itemid,itemdesc from fielditem where UPPER(fieldsetid)='K01' and useflag='1' and UPPER(itemtype)='N' order by displayid";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		/**
		 * 取得已构库的人员子集列表，用于记录招聘面试过程
		 * @return
		 */
		public ArrayList getRemenberExamineSetList()
		{
			ArrayList list = new ArrayList();
			try
			{
				String sql = "select fieldsetid,customdesc from fieldset where UPPER(fieldsetid) like 'A%' and useflag='1' and UPPER(fieldsetid)<>'A01'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				list.add(new CommonData("","请选择..."));
				while(rs.next())
				{
					list.add(new CommonData(rs.getString("fieldsetid"),rs.getString("customdesc")));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		/**
		 * 从用工需求表取得对外应聘职位指标列
		 * @return
		 * @throws GeneralException 
		 */
		public ArrayList getForeignJobList() throws GeneralException
		{
			ArrayList list = new ArrayList();
			try
			{
				list.add(new CommonData("","请选择..."));
				ArrayList fieldItemList = DataDictionary.getFieldList("z03",Constant.USED_FIELD_SET);
				for(int i=0;i<fieldItemList.size();i++){
					FieldItem item=(FieldItem) fieldItemList.get(i);
					if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid())&&item.isVisible()){
						list.add(new CommonData(item.getItemid(),item.getItemdesc()));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return list;
		}
		
		/**
		 * 取得招聘过程记录子集中，根据指标类型分类的指标列表
		 * @param setid
		 * @return
		 */
		public HashMap getExamineInfoConfig(String setid)
		{
			HashMap map = new HashMap();
			try
			{
				ArrayList charList = new ArrayList();
				ArrayList codeList = new ArrayList();
				ArrayList dateList = new ArrayList();
				ArrayList cmlist = new ArrayList();
				String sql = "select itemtype,codesetid,itemid,itemdesc from fielditem where UPPER(fieldsetid)='"+setid.toUpperCase()+"' and useflag='1'";
			    ContentDAO dao = new ContentDAO(this.conn);
			    RowSet rs = dao.search(sql);
			    while(rs.next())
			    {
			    	String itemtype=rs.getString("itemtype");
			    	String codesetid = rs.getString("codesetid");
			    	String itemid=rs.getString("itemid");
			    	String itemdesc=rs.getString("itemdesc");
			    	if("D".equalsIgnoreCase(itemtype))
			    	{
			    		dateList.add(new CommonData(itemid,itemdesc));
			    	}
			    	if("A".equalsIgnoreCase(itemtype)&& "01".equalsIgnoreCase(codesetid))
			    	{
			    		codeList.add(new CommonData(itemid,itemdesc));
			    	}
			    	if(("A".equalsIgnoreCase(itemtype)&& "0".equalsIgnoreCase(codesetid))/*||itemtype.equalsIgnoreCase("M")*/)
			    	{
			    		charList.add(new CommonData(itemid,itemdesc));
			    	}
			    	if("M".equalsIgnoreCase(itemtype)||("A".equalsIgnoreCase(itemtype)&& "0".equalsIgnoreCase(codesetid)))//dml 2011-03-22
			    	//if(itemtype.equalsIgnoreCase("M"))
			    	{
			    		cmlist.add(new CommonData(itemid,itemdesc));
			    	}
			    }
			    map.put("dateList", dateList);
			    map.put("codeList", codeList);
			    map.put("charList", charList);
			    map.put("cmlist", cmlist);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}
		public ArrayList getZ03CharFieldList()
		{
			ArrayList list = new ArrayList();
			try
			{
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				list.add(new CommonData("","请选择..."));
				for(int i=0;i<z03list.size();i++)
				{
	     			FieldItem item=(FieldItem)z03list.get(i);
	     			if("A".equalsIgnoreCase(item.getItemtype()))
	     			{
	     				list.add(new CommonData(item.getItemid(),item.getItemdesc()));
	     			}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		public ArrayList getZ03CharFieldList2()
		{
			ArrayList list = new ArrayList();
			try
			{
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				list.add(new CommonData("","请选择..."));
				for(int i=0;i<z03list.size();i++)
				{
	     			FieldItem item=(FieldItem)z03list.get(i);
	     			if("A".equalsIgnoreCase(item.getItemtype()))
	     			{
	     				if("z0301".equalsIgnoreCase(item.getItemid())|| "z0319".equalsIgnoreCase(item.getItemid())|| "z0309".equalsIgnoreCase(item.getItemid())
	     						|| "z0317".equalsIgnoreCase(item.getItemid())|| "z0101".equalsIgnoreCase(item.getItemid())
	     						|| "state".equalsIgnoreCase(item.getItemid())|| "z0336".equalsIgnoreCase(item.getItemid())
	     						|| "z0316".equalsIgnoreCase(item.getItemid())|| "z0303".equalsIgnoreCase(item.getItemid())|| "z0305".equalsIgnoreCase(item.getItemid())
	     						|| "z0321".equalsIgnoreCase(item.getItemid())|| "z0325".equalsIgnoreCase(item.getItemid())|| "z0311".equalsIgnoreCase(item.getItemid()))//dml增加；过滤掉Z0303：所属单位，Z0305：所属部门，z0321：需求单位；z0325：需求部门；z0311：需求岗位；
                        {
                            continue;
                        }
	     				list.add(new CommonData(item.getItemid(),item.getItemdesc()));
	     			}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		//招聘专业代码  的list
		public ArrayList getHireMajorCodeList(){
			ArrayList list = new ArrayList();
			try
			{
				StringBuffer sql = new StringBuffer("");
				sql.append("select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'UM' and codesetid<>'UN'");
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql.toString());
				list.add(new CommonData("","请选择..."));
				while(rs.next())
				{
					list.add(new CommonData(rs.getString("codesetid"),rs.getString("codesetdesc")));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		public ArrayList getCodeFieldList(String fieldSetId,String codeSetId)
		{
			ArrayList list = new ArrayList();
			try
			{
				StringBuffer sql = new StringBuffer("");
				sql.append("select itemid,itemdesc from fielditem where UPPER(fieldsetid)='"+fieldSetId.toUpperCase()+"' and useflag='1' and codesetid='"+codeSetId.toUpperCase()+"'");
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql.toString());
				list.add(new CommonData("","请选择..."));
				while(rs.next())
				{
					list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		public String getSchoolPositionDesc(String posid)
		{
			String desc="";
			RowSet rs = null;
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				rs= dao.search("select codeitemid,codeitemdesc from organization where codesetid='@K' and UPPER(codeitemid)='"+posid.toUpperCase()+"'");
				while(rs.next())
				{
					desc=rs.getString("codeitemdesc");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return desc;
		}
		public HashMap getHireChannelList()
		{
			HashMap map = new HashMap();
			RowSet rs = null;
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				rs= dao.search("select codeitemid,codeitemdesc from codeitem where codesetid='35' and codeitemid<>'03' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by codeitemid");
				while(rs.next())
				{
					map.put(rs.getString("codeitemid").toUpperCase(),rs.getString("codeitemdesc"));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return map;
		}
		
		public LazyDynaBean getSchoolPositionInfo(String posID)
		{
			LazyDynaBean bean = new LazyDynaBean();
			RowSet rs = null;
			try
			{
				StringBuffer sql = new StringBuffer();
				ContentDAO dao  = new ContentDAO(this.conn);
				sql.append("select a.codeitemid,a.codeitemdesc,b.codeitemid parent,b.codeitemdesc parentdesc,b.codesetid from organization a,");
				sql.append("(select * from organization where codeitemid=(Select parentid from organization where codeitemid='"+posID+"')) b where ");
				sql.append(" a.parentid=b.codeitemid and a.codeitemid='"+posID+"'");
				rs=dao.search(sql.toString());
				String schoolPositionOrg="";
				String schoolPositionOrgDesc="";
				String schoolPositionDesc="";
				String schoolPositionId="";
				while(rs.next())
				{
					String parentid=rs.getString("parent");
					String id=rs.getString("codeitemid");
					id=id.substring(parentid.length());
					bean.set("schoolPositionId", id);
					bean.set("schoolPositionDesc", rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
					bean.set("schoolPositionOrgDesc", rs.getString("parentdesc")==null?"":rs.getString("parentdesc"));
					bean.set("schoolPositionOrg", rs.getString("parent")==null?"":rs.getString("parent"));
					bean.set("parentcodesetid",rs.getString("codesetid")==null?"":rs.getString("codesetid"));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				if(rs!=null)
				{
					try{
						rs.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return bean;
		}
		//判断招聘专业是否是字符型字段
		public String getIsCharField(String major){
			String str = "0";
			try{
				ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
				for(int i=0;i<z03list.size();i++){
	     			FieldItem item=(FieldItem)z03list.get(i);
	     			if("A".equalsIgnoreCase(item.getItemtype())){
	     				if(item.getItemid().equalsIgnoreCase(major)){//如果匹配到了
	     					if("0".equalsIgnoreCase(item.getCodesetid())){
	     						str = "1";
	     					}
	     					break;
	     				}
	     			}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return str;
		}
        /**
         * @throws GeneralException  
         * @Title: getItemList 
         * @Description: 得到面试安排信息表（Z05）数值型指标
         * @return ArrayList   数值型指标的字段组成的list
         * @throws 
        */
        public ArrayList getItemList() throws GeneralException {
            RowSet rs=null;
            ArrayList itemList = new ArrayList();
            CommonData firstdata=new CommonData("#","请选择指标");
            itemList.add(firstdata);
            try{
                ContentDAO dao  = new ContentDAO(this.conn);
                String sql="select * from z05 where 1=2";
                rs=dao.search(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int count = rsmd.getColumnCount();
                for(int i=1;i<=count;i++){
                    String itemid= rsmd.getColumnName(i);
                    FieldItem item=DataDictionary.getFieldItem(itemid);
                    if(item!=null&& "N".equalsIgnoreCase(item.getItemtype())){
                        String itemdesc=item.getItemdesc();
                        CommonData data=new CommonData(itemid,itemdesc);
                        itemList.add(data);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
            finally{
                if(rs!=null){
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return itemList;
        }
        /**
        * @Title: setInterviewPKAndValue 
        * @Description: TODO(查看该表中是否存在interview字段,如果存在判断是否为null如果为null,将其字段值置为0,然后为这个表增加一个主键interview) 
        * @param dbWizard
        * @param TableName 要设置的表名 
        * @return void 
        * @throws
         */
        public void setInterviewPKAndValue(DbWizard dbWizard,String TableName){
        	ContentDAO dao  = new ContentDAO(this.conn);
        	RowSet rs=null;
        	try{
        		//如果不存在这个字段
    			String sql=" alter table "+TableName+" add interview int default 0 not null";
    			dao.update(sql);
    			if(Sql_switcher.searchDbServer()==2){//如果是oracle 得到表的主键约束
    				sql="select DISTINCT(cu.CONSTRAINT_NAME) from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = '"+TableName.toUpperCase()+"'";
    				rs=dao.search(sql);
    				if(rs.next()){//先删除原有的主键约束
    					String constraintName=rs.getString(1);
    					sql="alter table "+TableName+" drop constraint "+constraintName;
    					dao.update(sql);
    				}
    				if("zp_test_template".equalsIgnoreCase(TableName)){//增加新的主键约束
						sql="alter table "+TableName+" add constraint pk_zp_test_template_interview primary key(A0100_1,A0100,Z0101,Point_id,interview)";
					}else{
						sql="alter table "+TableName+" add constraint pk_"+TableName+" primary key(A0100,Z0101,interview)";//表名太长导致约束名过长修改一下
						//sql="alter table "+TableName+" add constraint pk_"+TableName+"_interview primary key(A0100,Z0101,interview)";
					}
					dao.update(sql);
    			}else{//如果是sqlserver  同样要修改主键约束
    				sql="select name  from sysobjects where parent_obj =(select id from sysobjects where name ='"+TableName+"')and xtype='PK'";
    				rs=dao.search(sql);
    				if(rs.next()){//先删除原有的主键约束
    					String constraintName=rs.getString(1);
    					sql="alter table "+TableName+" drop constraint "+constraintName;
    					dao.update(sql);
    				}
    				if("zp_test_template".equalsIgnoreCase(TableName)){//增加新的主键约束，总共两中表
						sql="alter table "+TableName+" add constraint pk_zp_test_template_interview primary key(A0100_1,A0100,Z0101,Point_id,interview)";
					}else{
						sql="alter table "+TableName+" add constraint pk_"+TableName+" primary key(A0100,Z0101,interview)";//表名太长导致约束名过长修改一下
						//sql="alter table "+TableName+" add constraint pk_"+TableName+"_interview primary key(A0100,Z0101,interview)";
					}
					dao.update(sql);
    			}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	finally{
        		if(rs!=null){
        			try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
        		}
        	}

        }
        /**
    	 * @Title: getColumnList
    	 * @Description: 查询列表表头信息
    	 * @param
    	 * @return
    	 * @return ArrayList
    	 * @throws GeneralException
    	 */
    	public ArrayList getColumnList() throws GeneralException {

    		ArrayList list = new ArrayList();
    		ArrayList columnList = new ArrayList();
    		list.add("codeitemdesc"); // 单位名称
    		list.add("codeitemid"); // 机构id
    		list.add("codesetid");
    		list.add("contentType"); 
    		list.add("D010I"); // 网址内容
    		list.add("D0102"); //内容形式内容

    		try {
    			ColumnsInfo info = null;
    			for (int i = 0; i < list.size(); i++) {
    				FieldItem item = DataDictionary.getFieldItem((String) list
    						.get(i));
    				info = new ColumnsInfo();
    				if (item == null
    						&& "codeitemdesc".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("codeitemdesc");
    					info.setColumnType("A");
    					info.setColumnDesc("单位名称 ");
    					info.setEditableValidFunc("false");
    					info.setColumnWidth(150);
    				} else if ("codeitemid".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("codeitemid");
    					info.setColumnType("A");
    					info.setColumnDesc("单位id ");
    					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    				} else if ("codesetid".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("codesetid");
    					info.setColumnType("A");
    					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    				} else if ("contenttype".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("contenttype");
    					info.setColumnType("A");
    					info.setColumnDesc("内容形式");
    					info.setSortable(false);
    					info.setColumnWidth(150);
    					info.setRendererFunc("contentRender");
    				}else if ("D010I".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("D010I");
    					info.setColumnType("A");
    					info.setColumnDesc("内容");
    					info.setEditableValidFunc("false");
    					info.setSortable(false);
    					info.setColumnWidth(180);
    				}  else if ("D0102".equalsIgnoreCase((String) list.get(i))) {
    					info.setColumnId("D0102");
    					info.setColumnType("A");
    					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
    					// info.setEncrypted(true);
    					info.setColumnDesc("id");
    					info.setColumnLength(0);
    				}else {
    					throw new GeneralException("列表头定义错误");
    				}
    				columnList.add(info);
    			}// for end
    			info = new ColumnsInfo();
    			info.setColumnDesc("操作");
    			info.setColumnId("operator");
    			info.setDefaultValue("operator");
    			info.setEditableValidFunc("false");
    			info.setRendererFunc("operator");
    			info.setSortable(false);
    			info.setColumnWidth(80);
    			info.setTextAlign("center");
    			columnList.add(info);

    		} catch (Exception e) {
    			e.printStackTrace();
    			throw GeneralExceptionHandler.Handle(e);
    		}

    		return columnList;
    	}
    	/**
    	 * 取得 单位介绍情况信息列表sql语句
    	 * @param userView
    	 * @param orgFieldIDs
    	 * @param contentType
    	 * @return
    	 */
    	public String getOrgIntroSql(UserView userView,String orgFieldIDs,String contentType,String type,String orgId,String orgName)
    	{
    		StringBuffer sql = new StringBuffer("");
    		try{
    			sql = sql.append("select organization.codeitemdesc,organization.codeitemid,organization.codesetid,organization.a0000 ");
    			if(orgFieldIDs!=null&&orgFieldIDs.trim().length()>0) {
                    sql.append(","+orgFieldIDs+" D010I");
                }
    			if(contentType!=null&&contentType.trim().length()>0) {
                    sql.append(","+contentType+" contenttype");
                }
    			sql.append(" from  b01,organization where b01.b0110=organization.codeitemid and (organization.codesetid='UN' or organization.codesetid='UM')");
    			if(!"all".equalsIgnoreCase(type))
    			{
    		    	if("0".equals(type))
    	     		{
    	     			if(orgId!=null&&!"".equals(orgId))
    	    			{
    	    				sql.append(" and organization.codeitemid ='");
    		    			sql.append(orgId+"'");
    	    			}
    	    			else
    		    		{
    		    			sql.append(" and organization.codeitemdesc like '%");
    		    			sql.append(PubFunc.getStr(orgName)+"%' ");
    	    			}
    	    		}
    			}
    			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    			sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between organization.start_date and organization.end_date ");
    		}catch(Exception e){
    			e.printStackTrace();
    			return "";
    		}
    		return sql.toString();
    	}
}
