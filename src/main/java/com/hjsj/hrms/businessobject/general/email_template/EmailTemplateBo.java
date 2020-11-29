package com.hjsj.hrms.businessobject.general.email_template;

import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * <p>Title:EmailTemplateBo.java</p>
 * <p>Decsription:定义薪资发放邮件模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-7 10:24:08</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class EmailTemplateBo {
	private Connection conn;
	public EmailTemplateBo()
	{
		
	}
	public EmailTemplateBo(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 取得模板id,最大+1
	 */
	public int getTemplateId()
	{
		int n=0;
		try
		{
			String sql ="select max(id) from email_name";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt(1);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n+1;
	}
	/**
	 * 取得邮件模板项目的id 规则 最大+1
	 * @param templateId
	 * @return
	 */
	public int getTemplateFieldId(String templateId)
	{
		int n=0;
		try
		{
			String sql ="select max(fieldid) from email_field where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n+1;
	
	}
	
	// 取得计划信息
    public RecordVo getPlanVo(String planid)
    {
		RecordVo vo = new RecordVo("per_plan");
		try
		{
		    vo.setInt("plan_id", Integer.parseInt(planid));
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
    
	public LazyDynaBean getTemplateInfo(String templateid)
	{
		LazyDynaBean bean  = new LazyDynaBean();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select address,subject,content from email_name where id="+templateid;
			rs=dao.search(sql);
			while(rs.next())
			{
				String emailField=rs.getString("address");
				if(emailField!=null&&emailField.trim().length()>0)
				{
					emailField=emailField.substring(0,emailField.indexOf(":")).trim();
				}
				bean.set("address",emailField);
				bean.set("subject", rs.getString("subject")==null?"":rs.getString("subject"));
				bean.set("content", rs.getString("content")==null?"":rs.getString("content"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return bean;
	}
	/**
	 * 取得邮件模板的邮件指标
	 * @param templateId
	 * @return
	 */
	public String getEmailField(String templateId)
	{
		String emailField="";
		try
		{
			String sql = "select address from email_name where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				emailField=rs.getString("address");
			}
			if(emailField!=null&&emailField.trim().length()>0)
			{
				emailField=emailField.substring(0,emailField.indexOf(":")).trim();
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return emailField;
	}
	public String getEmailFieldAndName(String templateId)
	{
		String emailField="";
		String fieldname="";
		try
		{
			String sql = "select address from email_name where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				emailField=rs.getString("address");
			}
			if(emailField!=null&&emailField.trim().length()>0)
			{
				fieldname=emailField.substring(emailField.indexOf(":")+1);
				emailField=emailField.substring(0,emailField.indexOf(":")).trim();
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return emailField+"#"+fieldname;
	}
	/**
	 * 取得邮件指标的fieldsetid
	 */
	public String getEmailFieldSetId(String itemid)throws GeneralException
	{
		String fieldsetid="";
		try
		{
			String sql = "select fieldsetid from fielditem where itemid='"+itemid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				fieldsetid=rs.getString("fieldsetid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(fieldsetid==null||fieldsetid.length()<=0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("模板邮件指标定义错误！"));
        }
		return fieldsetid;
	}
	public String getEmailFieldSetId2(String itemid)throws GeneralException
	{
		String fieldsetid="";
		try
		{
			String sql = "select fieldsetid from fielditem where itemid='"+itemid+"' and useflag=1";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				fieldsetid=rs.getString("fieldsetid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldsetid;
	}
	/**
	 * 人员子集
	 * @return Arraylist
	 */
	public ArrayList getPersonSubset(int type,ArrayList privFieldSetList)
	{
		ArrayList list = new ArrayList();
		if(type==1) {
            list.add(new CommonData("#",""));
        }
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<privFieldSetList.size();i++)
			{
				FieldSet str=(FieldSet)privFieldSetList.get(i);
				buf.append(",'");
				buf.append(str.getFieldsetid());
				buf.append("'");
			}
			if(buf.toString().trim().length()>0)
			{
	    		String sql="select fieldsetid,customdesc from fieldset where fieldsetid in("+buf.toString().substring(1)+") and useflag=1 order by displayorder";
	    		ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs =null;
    			rs=dao.search(sql);
	    		while(rs.next())
	     		{
	     			list.add(new CommonData(rs.getString("fieldsetid"),rs.getString("fieldsetid")+":"+rs.getString("customdesc")));
	    		}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getPrivSalarySetList(UserView view)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select salaryid,cname from salarytemplate order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				
					if(view.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid"))||view.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
					{
			         	CommonData cd = new CommonData(rs.getInt("salaryid")+"",rs.getInt("salaryid")+"."+rs.getString("cname"));
			        	list.add(cd);
					}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getSalaryItem(String salaryid,UserView view)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select itemid,itemdesc from salaryset where salaryid="+salaryid+" order by sortid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String itemid = rs.getString("itemid");
				if("a0000".equalsIgnoreCase(itemid)|| "a0100".equalsIgnoreCase(itemid)) {
                    continue;
                }
				String priv = view.analyseFieldPriv(itemid);
				if(!"0".equals(priv)|| "a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "a00z2".equalsIgnoreCase(itemid)|| "a00z3".equalsIgnoreCase(itemid))
				{
					list.add(new CommonData(itemid,rs.getString("itemdesc").replaceAll("\"", "\\\\\"")));
				}
			}
			list.addAll(this.getMidvariableList(salaryid, "0"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取工资套下的临时变量
	 * @param salaryid
	 * @param nflag
	 * @return
	 */
	public ArrayList getMidvariableList(String salaryid,String nflag)
	{
		ArrayList list = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		RowSet rs=null;
		try
		{
			
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rs.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rs.getString("chz"));
				item.setItemlength(rs.getInt("fldlen"));
				item.setDecimalwidth(rs.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rs, "cvalue"));
				item.setCodesetid(rs.getString("codesetid"));
				switch(rs.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			HashMap varMap = new HashMap();
			//过滤薪资类别  计算公式用不到的临时变量
			ArrayList formulaList=getFormulaList(1,salaryid);
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
	              String formula=((String)dbean.get("rexpr")).toLowerCase();
	              for(int j=0;j<fieldlist.size();j++)
	              {
	            	  item=(FieldItem)fieldlist.get(j);
	            	  String item_id=item.getItemid().toLowerCase();
	            	  if(item.getItemdesc()==null) {
                          continue;
                      }
	            	  String item_desc=item.getItemdesc().trim().toLowerCase();
	            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
	            	  {
	            		 // new_fieldList.add(item);
	            		  varMap.put(item_id, "1");
	            		  searchVar(fieldlist,item.getFormula(),varMap);
	            		  map.put(item_id, "1");
	            	  }
	              }
			}
			if (varMap.size() > 0) {
				Set keySet = varMap.keySet();
				StringBuffer _str = new StringBuffer("");
				for (Iterator t = keySet.iterator(); t.hasNext();) {
					_str.append(",'" + (String) t.next() + "'");
				}
				buf.setLength(0);
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate='");
				buf.append(salaryid);
				buf.append("')   and cname in ("+ _str.substring(1) + ")  order by sorting");
				rs = dao.search(buf.toString());
				while (rs.next()) {
					list.add(new CommonData(rs.getString("cname"),rs.getString("chz").replaceAll("\"","\\\\\"" )));
				
				}
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
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	/**
	 * 取得当前薪资类别计算公式列表
	 * @param flag =1 (有效计算公式) =-1全部的计算公式
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList(int flag,String salaryid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid=");
			buf.append(salaryid);
			/**过滤有效的计算公式*/
			if(flag==1) {
                buf.append(" and useflag=1");
            }
			buf.append(" order by salaryid,sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			list=dao.getDynaBeanList(rset);
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	public void searchVar(ArrayList midList, String formualr_str, HashMap varMap) {
		FieldItem item;
		for (int j = 0; j < midList.size(); j++) {
			item = (FieldItem) midList.get(j);
			String item_id = item.getItemid().toLowerCase();
			String item_desc = item.getItemdesc().trim().toLowerCase();
			String formula = item.getFormula();
			if (formualr_str.toLowerCase().indexOf(item_desc) != -1&& varMap.get(item_id) == null) {
				varMap.put(item_id, "1");
				searchVar(midList, formula, varMap);

			}

		}

	}
	
	/**
	 * 指标列表
	 * @param fieldsetid
	 * @return ArrayList
	 */
	public ArrayList getFieldItemList(int type,String fieldsetid,UserView view)
	{
		ArrayList list = new ArrayList();
		if(type==1) {
            list.add(new CommonData("#",""));
        }
		try
		{
			String sql = "select itemid,itemdesc from fielditem where fieldsetid='"+fieldsetid+"' and useflag = 1 order by itemid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			boolean b0110flag=false;
			boolean e0122flag=false;
			boolean e01a1flag=false;
			while(rs.next())
			{
				String itemid = rs.getString("itemid");
				String priv = view.analyseFieldPriv(itemid);
				if(!"0".equals(priv))
				{
	    			list.add(new CommonData(itemid,rs.getString("itemdesc").replaceAll("\"", "\\\\\"")));
	    			if("b0110".equalsIgnoreCase(itemid)) {
                        b0110flag=true;
                    }
	    			if("e0122".equalsIgnoreCase(itemid)) {
                        e0122flag=true;
                    }
	    			if("e01a1".equalsIgnoreCase(itemid)) {
                        e01a1flag=true;
                    }
				}
			}
			/**强制加入单位，职位，部门指标*/
			if(!b0110flag&& "a01".equalsIgnoreCase(fieldsetid)) {
                list.add(new CommonData("B0110","单位"));
            }
			if(!e0122flag&& "a01".equalsIgnoreCase(fieldsetid)) {
                list.add(new CommonData("E0122","部门"));
            }
			if(!e01a1flag&& "a01".equalsIgnoreCase(fieldsetid)) {
                list.add(new CommonData("E01a1","职位"));
            }
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得第一个人员子集id
	 * @return
	 */
	public String getFirstFieldSetid(ArrayList privFieldSetList)
	{
		String str="";
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<privFieldSetList.size();i++)
			{
				/*String str1=(String)privFieldSetList.get(i);
				String[] temp = str1.split(",");
				buf.append(",'");
				buf.append(temp[0].substring(temp[0].indexOf("=")+1));
				buf.append("'");*/
				com.hrms.hjsj.sys.FieldSet set = (com.hrms.hjsj.sys.FieldSet)privFieldSetList.get(i);
				set.getFieldsetid();
				buf.append(",'");
				buf.append(set.getFieldsetid().toUpperCase());
				buf.append("'");
			}
			if(buf.toString().trim().length()>0)
			{
	    		String sql = "select min(fieldsetid) from fieldset where upper(fieldsetid) in("+buf.toString().substring(1)+") and useflag=1";
	    		ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs = null;
    			rs=dao.search(sql);
	    		while(rs.next())
	    		{
	    			str=rs.getString(1);
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
	 * 取得浏览邮件的内容
	 * @param templateId
	 * @param a0100
	 * @param nbase
	 * @return
	 */
	public HashMap getBrowseEmailContent(String templateId,String a0100,String nbase,String tableName,String I9999)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select s.a0101,address,subject,content from email_content e,");
			sql.append(tableName+" s where e.a0100=s.a0100 and e.pre=s.nbase and e.id=");
			sql.append(templateId);
			sql.append(" and e.a0100='");
			sql.append(a0100);
			sql.append("' and e.pre='");
			sql.append(nbase+"'");
			sql.append(" and e.i9999="+I9999);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql.toString());
			while(rs.next())
			{
				map.put("address",rs.getString("address"));
				map.put("subject",rs.getString("subject"));
				map.put("content",rs.getString("content"));
				map.put("a0101",rs.getString("a0101"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取浏览邮件的附件列表
	 * @param templateId
	 * @return
	 */
	public ArrayList getBrowseEmailAttach(String templateId)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select filename from email_attach where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("filename",rs.getString("filename"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 日期格式列表
	 * @return
	 */
	public ArrayList getDateFormatList()
	{
		ArrayList dateFormatList = new ArrayList();
		try
		{
			CommonData vo = new CommonData("1","1:1980.07.09");
			dateFormatList.add(vo);
			vo = new CommonData("2","2:1980.7.9");
			dateFormatList.add(vo);
			vo = new CommonData("3","3:80.7.9");
			dateFormatList.add(vo);
			vo = new CommonData("4","4:1983.02");
			dateFormatList.add(vo);
			vo = new CommonData("5","5:1982.5");
			dateFormatList.add(vo);
			vo = new CommonData("6","6:80.07");
			dateFormatList.add(vo);
			vo = new CommonData("7","7:80.7");
			dateFormatList.add(vo);
			vo = new CommonData("8","8:1987年03月09日");
			dateFormatList.add(vo);
			vo = new CommonData("9","9:1982年7月9日");
			dateFormatList.add(vo);
			vo = new CommonData("10","10:1987年07月");
			dateFormatList.add(vo);
			vo = new CommonData("11","11:1987年7月");
			dateFormatList.add(vo);
			vo = new CommonData("12","12:80年07月09日");
			dateFormatList.add(vo);
			vo = new CommonData("13","13:80年7月9日");
			dateFormatList.add(vo);
			vo = new CommonData("14","14:80年7月");
			dateFormatList.add(vo);
			vo = new CommonData("15","15:年限");
			dateFormatList.add(vo);
			vo = new CommonData("16","16:年份");
			dateFormatList.add(vo);
			vo = new CommonData("17","17:月份");
			dateFormatList.add(vo);
			vo = new CommonData("18","18:日份");
			dateFormatList.add(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dateFormatList;
	}
	/**
	 * 取得模板的附件列表
	 * @param templateId
	 * @return
	 */
	public ArrayList getAttachList(String templateId)
	{
		ArrayList list=new ArrayList();
		try
		{
		    StringBuffer sql = new StringBuffer();
		    sql.append("select * from email_attach where id=");
		    sql.append(templateId);
		    sql.append(" order by attach_id");
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs= null;
//		    byte[] byt=null;
		    double total = 0.00d;
		    double v=1024d;
	    	double compareValue=0.00d;
		    rs=dao.search(sql.toString());
		    boolean flag=false;
		    while(rs.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("fileName",rs.getString("filename"));
		    	bean.set("istotal","no");
		    	bean.set("id",rs.getString("attach_id"));
		    	bean.set("templateId",templateId);
		    	String fileid = "";
		    	if(StringUtils.isBlank(rs.getString("fileid"))) {
		    		continue;
		    	}
		    	fileid = rs.getString("fileid");
		    	VfsFileEntity vfsFileEntity = VfsService.getFileEntity(fileid);
		    	
		    	double length = vfsFileEntity.getFilesize();
		    	total+=length;
		    	double fileLength=div(div(length,v,3),v,3);
		    	bean.set("fileLength",fileLength+"MB");
//		    	byt=rs.getBytes("attach");
//		    	if(byt==null)
//		    		continue;
//		    	double length=(double)byt.length;
//		    	double fileLength=div(div(length,v,2),v,2);
//		    	int compareResult=compare(fileLength,compareValue);
//		    	if(compareResult<=0)
//		    	{
//		    		bean.set("fileLength",String.valueOf(length)+ResourceFactory.getProperty("gz.email.attachbyte"));
//		    	}else
//		    	{
//		    	     bean.set("fileLength",String.valueOf(fileLength)+"MB");
//		    	}
		    	list.add(bean);
		    	flag=true;
		    }
		    if(flag)
		    {
    		    LazyDynaBean total_bean = new LazyDynaBean();
    		    total_bean.set("fileName","总计");
    		    double total_fileLength=div(div(total,v,2),v,2);
    		    int compareResult=compare(total_fileLength,compareValue);
    		    if(compareResult<=0)
		    	{
		    		total_bean.set("fileLength",String.valueOf(total)+ResourceFactory.getProperty("gz.email.attachbyte"));
		    	}else
		    	{
		    	    total_bean.set("fileLength",String.valueOf(total_fileLength)+"MB");
		    	}
    		    total_bean.set("istotal","yes");
     		    list.add(list.size(),total_bean);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	public double div(double v1,double v2,int scale) throws GeneralException
	{
		if(scale<0)
		{
			throw GeneralExceptionHandler.Handle(new Exception(" The scale must be a positive integer or zero"));
		}
		BigDecimal b1= new BigDecimal(Double.toString(v1));
		BigDecimal b2= new BigDecimal(Double.toString(v2));
		return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public int compare(double v1,double v2)
	{
		BigDecimal b1= new BigDecimal(Double.toString(v1));
		BigDecimal b2= new BigDecimal(Double.toString(v2));
		return b1.compareTo(b2);
	}
	/**
	 * 取得某一邮件模板的信息
	 * @param templateId
	 * @return
	 */
	public HashMap getTemplateInfoById(String templateId)
	{
		HashMap map = new HashMap();
		String name="";
		String content="";
		String subject="";
		String address="";
		try
		{
			String sql = "select * from email_name where id="+templateId;
			ContentDAO dao= new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql);
			while(rs.next())
			{
				name=rs.getString("name");
				content=rs.getString("content");
				subject=rs.getString("subject");
				address=rs.getString("address");
			}
			map.put("name",name);
			map.put("content",content);
			map.put("subject",subject);
			map.put("address",address);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 得到指标的类型
	 * @param itemid
	 * @return
	 */
	public HashMap getItemInfo(String itemid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select itemtype,codesetid,itemlength,decimalwidth from fielditem where itemid='"+itemid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			boolean flag=true;
			while(rs.next())
			{
				flag=false;
				map.put("itemtype",rs.getString("itemtype").toLowerCase());
				map.put("codesetid",rs.getString("codesetid").toLowerCase());
				map.put("itemlength", rs.getString("itemlength"));
				map.put("decimalwidth",rs.getString("decimalwidth"));
			}
			if(flag)
			{
				sql="select * from MidVariable where UPPER(cname)='"+itemid.toUpperCase()+"'";
				rs=dao.search(sql);
				while(rs.next())
				{
					int ntype=rs.getInt("ntype");
					String itemtype="A";
					if(ntype==1) {
                        itemtype="N";
                    } else if(ntype==2) {
                        itemtype="A";
                    } else if(ntype==3) {
                        itemtype="D";
                    } else if(ntype==4) {
                        itemtype="A";
                    }
					String codesetid=rs.getString("codesetid")==null?"0":rs.getString("codesetid");
					int itemlength=rs.getInt("fldlen");
					int decimalwidth=rs.getInt("flddec");
					map.put("itemtype",itemtype.toLowerCase());
					map.put("codesetid",codesetid.toLowerCase());
					map.put("itemlength", itemlength+"");
					map.put("decimalwidth",decimalwidth+"");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}
	public HashMap getItemInfoFromSalary(String itemid,String salaryid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select itemtype,codesetid,itemlength,decwidth from salaryset where salaryid="+salaryid+" and UPPER(itemid)='"+itemid.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			boolean flag=true;
			rs=dao.search(sql);
			while(rs.next())
			{
				flag=false;
				map.put("itemtype",rs.getString("itemtype").toLowerCase());
				map.put("codesetid",rs.getString("codesetid").toLowerCase());
				map.put("itemlength", rs.getString("itemlength"));
				map.put("decimalwidth",rs.getString("decwidth"));
			}
			if(flag)
			{
				sql="select * from MidVariable where UPPER(cname)='"+itemid.toUpperCase()+"'";
				rs=dao.search(sql);
				while(rs.next())
				{
					int ntype=rs.getInt("ntype");
					String itemtype="A";
					if(ntype==1) {
                        itemtype="N";
                    } else if(ntype==2) {
                        itemtype="A";
                    } else if(ntype==3) {
                        itemtype="D";
                    } else if(ntype==4) {
                        itemtype="A";
                    }
					String codesetid=rs.getString("codesetid")==null?"0":rs.getString("codesetid");
					int itemlength=rs.getInt("fldlen");
					int decimalwidth=rs.getInt("flddec");
					map.put("itemtype",itemtype.toLowerCase());
					map.put("codesetid",codesetid.toLowerCase());
					map.put("itemlength", itemlength+"");
					map.put("decimalwidth",decimalwidth+"");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 代码型指标的全部代码列表
	 * @param itemid
	 * @return
	 */
	public ArrayList getCodeList(String itemid)
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("#",""));
		try
		{
			boolean flag=true;
			StringBuffer sql = new StringBuffer();
			sql.append("select a.codeitemid,a.codeitemdesc from codeitem a,fielditem d ");
			sql.append(" where a.codesetid=d.codesetid and UPPER(d.itemid)='");
			sql.append(itemid.toUpperCase());
			sql.append("'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				flag=false;
				list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
			}	
			if(flag)
			{
				sql.setLength(0);
				sql.append("select a.codeitemid,a.codeitemdesc from codeitem a,MidVariable d ");
				sql.append(" where a.codesetid=d.codesetid and UPPER(d.cname)='");
				sql.append(itemid.toUpperCase());
				sql.append("'");
				rs=dao.search(sql.toString());
				while(rs.next()){
					list.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getEmailAddress(String pre ,String a0100,String emailField)
	{
		String email="";
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select ");
			buf.append(emailField);
			buf.append(" from ");
			buf.append(pre+"a01");
			buf.append(" where a0100='");
			buf.append(a0100);
			buf.append("'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				email=rs.getString(emailField);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return email;
	}
	/**
	 * 取得模板的subject,attach,address
	 * @param templateId
	 * @return
	 */
	public HashMap getSubject(String templateId)
	{
		HashMap map = new HashMap();
		try
		{
			String sql="select subject,attach,address from email_name where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put("subject",rs.getString("subject"));
				map.put("address",rs.getString("address"));
				map.put("attach",rs.getString("attach"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 取附件id，最大加一
	 * @return
	 */
	public int getAttachId()
	{
		int n=1;
		try
		{
			String sql = "select max(attach_id) from email_attach ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt(1);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 把附件基本信息保存到数据库
	 * @param templateId
	 * @param attach_id
	 * @param fileName
	 * @param fileid 
	 */
	public void insertEmail_attach(String templateId,String attach_id,String fileName, String fileid)
	{
		try
		{
			int index=fileName.lastIndexOf(".");
			String ext=fileName.substring(index+1,fileName.length());
			StringBuffer sql = new StringBuffer();
			sql.append("insert into email_attach(id,attach_id,filename,extname,fileid) values(");
			sql.append(templateId);
			sql.append(",");
			sql.append(attach_id);
			sql.append(",'");
			sql.append(fileName);
			sql.append("','");
			sql.append(ext);
			sql.append("','");
			sql.append(fileid);
			sql.append("')");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 文件存到数据库
	 * @param file
	 * @param attach_id
	 */
	public void updateEmail_attach(FormFile file,String attach_id)
	{
		  byte[] data = null;
		  Blob blob=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("email_attach");
		    vo.setInt("attach_id",Integer.parseInt(attach_id));
		    RecordVo a_vo =dao.findByPrimaryKey(vo);
		  
		    switch(Sql_switcher.searchDbServer())
		    {
		    case Constant.ORACEL:
		    	break;
		    default:
		    	data = file.getFileData();
		        a_vo.setObject("attach",data);
		        break;	
		    }
		  //  dao.updateValueObject(a_vo);
		   // dao.updateValueObject(a_vo);
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    {
		    	blob = getOracleBlob(file,attach_id);
		    	a_vo.setObject("attach",blob);	
		    }
		    dao.updateValueObject(a_vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			data=null;
			blob=null;
		}
		
	}
	/**
	 * oracle得到blob字段
	 * @param file
	 * @param attach_id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String attach_id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select attach from ");
		strSearch.append("email_attach ");
		strSearch.append(" where attach_id=");
		strSearch.append(attach_id);		
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append("email_attach");
		strInsert.append(" set attach=EMPTY_BLOB() where attach_id=");
		strInsert.append(attach_id);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
	    Blob blob = null; 
	    InputStream is = null;
	    try{
	      is = file.getInputStream();
	      blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is);
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{
	       PubFunc.closeIoResource(is);
	    }
		return blob;
	}
	public void deleteAttach(String attach_id, String userName)
	{
		RowSet rs = null;
		try
		{
			//xus vfs 删除文件
			String fileid = "";
			String sql = "select fileid from email_attach where attach_id="+attach_id;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next()) {
				if(StringUtils.isNotBlank(rs.getString("fileid"))) {
					fileid = rs.getString("fileid");
					VfsService.deleteFile(userName, fileid);
				}
			}
			
			String delsql = "delete from email_attach where attach_id="+attach_id;
			dao.delete(delsql,new ArrayList());	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 保存邮件模板选择的项目
	 * @param templateId
	 * @param fieldList
	 * @param flag
	 */
	public void addEmailField(int templateId,ArrayList fieldList,int flag)
	{
		try
		{
			HashMap map=isHaveThisRecord(templateId);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			ArrayList list = new ArrayList();
			ArrayList alist=new ArrayList();
			if(fieldList==null||fieldList.size()==0) {
                return;
            }
			for(int i=0;i<fieldList.size();i++)
			{
				String s=SafeCode.decode(((String)fieldList.get(i)==null|| "".equals((String)fieldList.get(i)))?"":(String)fieldList.get(i));
				if(s==null||s.trim().length()==0)
				{
					continue;
				}
				String[] arr=s.split("`");
				RecordVo vo = new RecordVo("email_field");
				vo.setInt("id",templateId);//arr[0]
				vo.setInt("fieldid",Integer.parseInt(arr[1]));
				vo.setString("fieldtitle",arr[2]);
				vo.setString("fieldtype",arr[3].toUpperCase());
				String fd=(arr[4]==null|| "".equals(arr[4])?"":arr[4].replaceAll("#","\""));
				if(Integer.parseInt(arr[9])==1)//formula
				{
					fd=PubFunc.keyWord_reback(arr[4]).replaceAll("#","\"");
				}
				vo.setString("fieldcontent",fd);
				vo.setInt("dateformat",Integer.parseInt(arr[5]));
				vo.setInt("fieldlen",Integer.parseInt(arr[6]));
				vo.setInt("ndec",Integer.parseInt(arr[7]));
				vo.setString("codeset",arr[8].toUpperCase());
				vo.setInt("nflag",Integer.parseInt(arr[9]));
				if(flag!=1&&map!=null)
				{
					if(map.get(arr[1])!=null)
					{
						sql.append("update email_field set fieldtitle=?,fieldtype='"+arr[3].toUpperCase()+"',");
						sql.append("fieldcontent=?,dateformat="+arr[5]+",fieldlen="+arr[6]+",ndec="+arr[7]);
						sql.append(",codeset='"+arr[8].toUpperCase()+"',");
						sql.append("nflag="+arr[9]);
						sql.append(" where id="+templateId+" and fieldid="+arr[1]);
						ArrayList updateList = new ArrayList();
						updateList.add(arr[2]);
						updateList.add(arr[4]==null|| "".equals(arr[4])?"":arr[4]);
						dao.update(sql.toString(),updateList);
						sql.setLength(0);
					}
					else
					{
						alist.add(vo);
					}
				}
				else
				{
		    		list.add(vo);
				}
			}
			if(flag==1)
			{
			    dao.addValueObject(list);
			}
			else
			{
				if(alist!=null&&alist.size()>0)
				{
					dao.addValueObject(alist);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteTemplateFields(String templateId)
	{
		try
		{
			String sql ="delete from email_field where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 用于判断数据库中是否已经存在前台传来的邮件模板项目,存在更新,不存在插入
	 * @param id
	 * @return
	 */
	public HashMap isHaveThisRecord(int id/*,int fieldid*/)
	{
		HashMap map = new HashMap();
		try
		{
			String sql ="select fieldid from email_field where id="+id/*+" and fieldid="+fieldid*/+" order by fieldid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("fieldid"),rs.getString("fieldid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	private RecordVo salaryTemplateVo;
	public void setSalaryTemplateVo(RecordVo vo)
	{
		this.salaryTemplateVo=vo;
	}
	/**
	 * 取得人员库列表
	 * @return
	 */
	public ArrayList getNbaseList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select pre,dbname from dbname order by dbid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			String cbase="";
			if(this.salaryTemplateVo!=null)
			{
				cbase=","+salaryTemplateVo.getString("cbase").toLowerCase();
			}
			while(rs.next())
			{
				if(!"".equals(cbase)&&(cbase.indexOf(","+rs.getString("pre").toLowerCase()+",")==-1)) {
                    continue;
                }
				list.add(new CommonData(rs.getString("pre"),rs.getString("dbname")));
			}
			list.add(0,new CommonData("#","      "));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	private String inserttime="";
	public String getInserttime() {
		return inserttime;
	}
	public void setInserttime(String inserttime) {
		this.inserttime = inserttime;
	}
	/**
	 * 将基本信息导入邮件内容表
	 * @param templateId 邮件模板id
	 * @param subject 邮件标题
	 * @param tableName 薪资表名
	 * @param code 单位或部门过滤
	 * @param email_field 邮件指标
	 * @param userView username
	 */
	public void exportPersonBaseIntoContent(String templateId,String subject,String tableName,String code,String email_field,UserView userView,String type,String selectedid,String beforeSql)
	{
		try
		{   
			updateTableCloumns();
	    	StringBuffer buf= new StringBuffer();
	    	String codesql=this.getCodeSql("a",code);
	    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String time=format.format(new Date());
	    	this.setInserttime(time);
	    	boolean ismain=false;
	    	FieldItem item = DataDictionary.getFieldItem(email_field.toLowerCase());
	    	if("A01".equalsIgnoreCase(item.getFieldsetid())) {
                ismain=true;
            }
      		buf.append("insert into email_content(");
      		buf.append("id,username,subject,send_ok,pre,a0000,a0100,b0110,e01a1,");
      		if(ismain) {
                buf.append(" address,");
            }
	    	buf.append(" send_time,I9999)select "+templateId+" as id,'");
	    	buf.append(userView.getUserName());
	    	buf.append("' as username,'"+subject+"' as subject,0 as send_ok,temp.*,");
	    	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                buf.append(" to_date('"+time+"','YYYY-MM-DD HH24:MI:SS') ");
            } else {
                buf.append("'"+time+"' ");
            }
	    	buf.append(" as send_time,0 as I9999 from (");
    		//----
	    	ArrayList preList = getNbaseList();
	    	String sql="";
	    	if("1".equals(type)&&selectedid!=null&&!"".equals(selectedid)) {
                sql=this.getSql(selectedid, "a");
            }
	    	for(int i=0;i<preList.size();i++)
	    	{
	    		CommonData data = (CommonData)preList.get(i);
	    		if("#".equals(data.getDataValue())) {
                    continue;
                }
	    		String pre = data.getDataValue();
	    		buf.append(" select");
	        	buf.append(" a.nbase as pre,u.a0000,u.a0100,u.b0110,u.e01a1 ");
	        	if(ismain) {
                    buf.append(",u."+email_field);
                }
        		buf.append(" from "+tableName+" a,"+pre+"a01 u where u.a0100=");
        		buf.append("a.a0100 and UPPER(a.nbase)='"+pre.toUpperCase()+"' ");
        		if("1".equals(type)&&selectedid!=null&&!"".equals(selectedid))
        		{
        			buf.append(" and ");
        	    	buf.append(sql);
        		}
        		else
        		{
            	    if(!(codesql==null|| "".equals(codesql)))
            	    {
             	    	buf.append(" and ");
            	    	buf.append(codesql);
             	    }
            	    if(beforeSql!=null&&!"".equals(beforeSql))
            	    {
            	    	if(beforeSql.toUpperCase().startsWith(" AND")||beforeSql.toUpperCase().startsWith("AND"))
            	    	{
            	        	buf.append(" and ");
            	        	buf.append("("+beforeSql.substring(4).toUpperCase().replaceAll(tableName.toUpperCase(), "a")+")");
            	    	}else{
            	    		buf.append(" and ");
            	        	buf.append("("+beforeSql.toUpperCase().replaceAll(tableName.toUpperCase(), "a")+")");
            	    	}
            	    }
        		}
        	    if(preList.size()-1!=i) {
                    buf.append(" union ");
                }
	    	}
     		
 
    		buf.append(") temp");
    		ContentDAO dao = new ContentDAO(this.conn);
    		dao.insert(buf.toString(),new ArrayList());
    		configI9999(null,templateId,time,dao);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	public void configI9999(String wid,String templateId,String time,ContentDAO dao)
	{
		try
		{
			/*update email_content set I9999=
				(select I9999 from(select max(i9999)+1 as I9999,a0100,pre from email_content group by a0100,pre) temp where email_content.a0100=temp.a0100 and email_content.pre=temp.pre)
				where 
				 email_content.send_time='2007-10-31 02:56:03'*/

			StringBuffer sql = new StringBuffer();
			sql.append("update email_content set I9999=");
			sql.append("(select I9999 from(select max(a.i9999)+1 as I9999,a.a0100,a.pre from email_content a,email_content b where a.a0100=b.a0100 and a.pre=b.pre and a.id = b.id  group by a.a0100,a.pre) temp ");
			sql.append(" where email_content.a0100=temp.a0100 and email_content.pre=temp.pre) where email_content.send_time=");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sql.append("to_date('"+time+"','yyyy-mm-dd hh24:mi:ss') ");
            } else {
                sql.append("'"+time+"' ");
            }
			sql.append(" and email_content.id="+templateId);
			if(wid!=null&&!"".equals(wid))
			{
				sql.append(" and email_content.wid="+wid);
			}
			else
			{
				sql.append(" and email_content.wid is null ");
			}
			dao.update(sql.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 统一解析code
	 * @param oth_name 字段所在表的别名
	 * @param codevalue
	 * @return
	 */
	public String getCodeSql(String oth_name,String codevalue)
	{
		StringBuffer sql = new StringBuffer("");
		try
		{
			if(!(codevalue==null|| "".equals(codevalue)))
			{
	    		String code=codevalue.substring(0,2);
	    		String value=codevalue.substring(2);
	    		if("UN".equalsIgnoreCase(code))
	    		{
	    			sql.append("("+oth_name);
	    			sql.append(".b0110 like'");
	    			sql.append(value);
	    			sql.append("%'");
	    			if(value==null|| "".equals(value))
	    			{
	    				sql.append(" or ");
	    				sql.append(oth_name);
	    				sql.append(".b0110 is null");
	    			}
	    			sql.append(")");
	    		}
	    		if("UM".equalsIgnoreCase(code))
    			{
    				sql.append("("+oth_name);
    				sql.append(".e0122 like '");
    				sql.append(value);
    				sql.append("%'");
    				if(value==null|| "".equals(value))
	    			{
	    				sql.append(" or ");
	    				sql.append(oth_name);
	    				sql.append(".e0122 is null");
	    			}
	    			sql.append(")");
    			}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	
	
	/**
	 * 取得指标的主集id
	 * @param itemid
	 * @return
	 */
	public String getFieldSetId(String itemid)
	{
		String sql = "select fieldsetid from fielditem where UPPER(itemid)=?";
		PreparedStatement pstmt = null;	
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		String fieldSet="";
		try{
			ArrayList paramList = new ArrayList();
			paramList.add(itemid.toUpperCase());
			//pstmt.setString(1, itemid.toUpperCase());
           // rs=pstmt.executeQuery();
			rs=dao.search(sql, paramList);
     		while(rs.next())
     		{
     		    fieldSet = rs.getString("fieldsetid"); 
     		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if(rs!=null)
				{
					rs.close();
				}
				if (pstmt != null) {
                    pstmt.close();
                }
			} catch (SQLException ee) {
				ee.printStackTrace();
			}	
		}
		return fieldSet;
	}
	
	
	
	/**
	 * 将实际的email地址更新到email-content表中
	 * @param emailField
	 * @param emailFieldSet
	 * @param tableName
	 * @param code
	 * @param templateId
	 */
	public void getEmailValue(String emailField,String emailFieldSet,String tableName,ArrayList list,String templateId)
	{
		StringBuffer sql = new StringBuffer();
		ArrayList sqllist = new ArrayList();
		ArrayList preList = getNbaseList();
		for(int i=0;i<preList.size();i++)
		{
			CommonData data = (CommonData)preList.get(i);
			String pre = data.getDataValue();
			if("#".equals(pre)) {
                continue;
            }
			for(int j=0;j<list.size();j++)
			{
				String str=(String)list.get(j);
				String nbase=str.substring(0,3);
				String a0100=str.substring(3);
				if(!pre.equalsIgnoreCase(nbase)) {
                    continue;
                }
				if("a01".equalsIgnoreCase(emailFieldSet))
				{
			    	sql.append(" update email_content set address=(select a."+emailField+" address from ");
			     	sql.append(pre+emailFieldSet+" a where a.a0100='"+a0100+"') where ");
				}
				else
				{
					sql.append(" update email_content set address=(select a."+emailField+" address from ");
			     	sql.append(pre+emailFieldSet+" a where a.a0100="+a0100+" and i9999=(select max(i9999) i9999 from "+pre+emailFieldSet+" b where b.a0100='"+a0100+"')) where ");
				}
				sql.append(" UPPER(pre)='"+nbase.toUpperCase()+"' and id="+templateId+" and a0100='"+a0100+"' and i9999=(select max(i9999) i9999");
				sql.append(" from email_content where UPPER(pre)='"+nbase.toUpperCase()+"'  and id="+templateId+" and a0100='"+a0100+"')");
			    sqllist.add(sql.toString());
			    sql.setLength(0);
			}  	
		} 
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			dao.batchUpdate(sqllist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得邮件模板的标题
	 * @param templateId
	 * @return
	 */
	public String getEmailTemplateSubject(String templateId)
	{
		String subject="";
		try
		{
			String sql = "select subject from email_name where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				subject=rs.getString("subject");
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return subject;
	
	}
	public ArrayList getTemplateFieldList(int templateId)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select * from email_field where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String str=String.valueOf(templateId)+"`"+
				rs.getString("fieldid")+"`"+rs.getString("fieldtitle")+"`"+
				rs.getString("fieldtype")+"`"+rs.getString("fieldcontent")+"`"+
				rs.getString("dateformat")+"`"+rs.getString("fieldlen")+"`"+
				rs.getString("ndec")+"`"+rs.getString("codeset")+"`"+rs.getString("nflag");
				bean.set("con", SafeCode.encode(str));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得模板指标或公式的信息
	 * @param templateId
	 * @return
	 */
	public ArrayList getTemplateFieldInfo(int templateId,int type)
	{
		ArrayList list = new ArrayList();
		RowSet rs=null;
		try
		{
			String sql = "select * from email_field where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id",String.valueOf(templateId));
				bean.set("fieldid",rs.getString("fieldid")==null?"":rs.getString("fieldid").trim());
				bean.set("fieldtitle",rs.getString("fieldtitle")==null?"":rs.getString("fieldtitle").trim());
				bean.set("fieldtype",rs.getString("fieldtype"));
				if(type==1)
				{
			    	  bean.set("fieldcontent",rs.getString("fieldcontent"));
				}
				else
				{
					bean.set("fieldcontent",rs.getString("fieldcontent"));
				}
				bean.set("dateformat",rs.getString("dateformat"));
				bean.set("fieldlen",rs.getString("fieldlen"));
				bean.set("ndec",rs.getString("ndec"));
				bean.set("codeset",rs.getString("codeset")==null?"":rs.getString("codeset").trim());
				bean.set("nflag",rs.getString("nflag"));
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	private ArrayList midVList = new ArrayList();
	private ArrayList allFielditemList = new ArrayList();
	/**
	 * 将由公式和指标组成的邮件内容换成实际内容
	 * @param templateFieldList 模板项目列表
	 * @param templateId 模板id
	 * @param emailContentA0100List模板人员
	 * @param content 模板内容
	 */
	public void updateEmailContent(String wid,ArrayList templateFieldList,String templateId,ArrayList emailContentA0100List,String content,UserView userview,int entry_type,String salaryid,String tableN)
	{
		Connection _con = null;
		try
		{   
			_con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(_con);
			
			String cont="";
			String sql="";
			HashMap salaryItemMap=null;
			this.allFielditemList=DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			//ArrayList salarysetlist=null;
			if(entry_type==1)
			{
				 salaryItemMap=this.getSalarySetMap(salaryid);
				 midVList = this.getMidVList(salaryid);
				 this.allFielditemList.addAll(midVList);
			}
			ArrayList sqlList = new ArrayList();
			ArrayList valueList = new ArrayList();
			for(int i=0;i<emailContentA0100List.size();i++)
			{
				ArrayList list = new ArrayList();
				String prea0100=(String)emailContentA0100List.get(i);
				if(entry_type==1)
				{
					cont=this.getFactContentFromSalaryTempTable(content, prea0100, templateFieldList, userview, tableN, salaryItemMap);
				}
				else
				{
				   cont=this.getFactContent(content,prea0100,templateFieldList,userview);
				}
				sql="update email_content set content=? where a0100='"+prea0100.substring(3)+"' and id="+templateId+" and pre='"+prea0100.substring(0,3)+"' and send_time=";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql+="to_date('"+this.getInserttime()+"','YYYY-MM-DD HH24:MI:SS') ";
                } else {
                    sql+="'"+this.getInserttime()+"' ";
                }
				if(wid!=null&&!"".equals(wid)) {
                    sql+=" and wid="+wid;
                } else {
                    sql+= " and wid is null";
                }
				sqlList.add(sql);
				
				list.add(cont);
				valueList.add(list);
//				dao.update(sql,list);
				sql="";
//				list.clear();
				cont="";
				if(sqlList.size()==300){
					dao.batchUpdate(sqlList, valueList);
					sqlList = new ArrayList();
					valueList = new ArrayList();
					list = new ArrayList(); 
					_con.close();
					if (_con != null) {
						_con.close();
					}
					_con = AdminDb.getConnection();
					dao = new ContentDAO(_con);
				}
			}
			if(sqlList.size()>=1){
				dao.batchUpdate(sqlList, valueList);
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(_con);
		}
	}
	/**
	 * 取得模板的人员所在的库和a0100
	 * @param templateId
	 * @return
	 */
	public ArrayList getEmailContentA0100(String templateId,String code,String tableName,String type,String selectedid,String codesql,String privSql,String beforeSql)
	{
		ArrayList  list = new ArrayList();
		RowSet rs= null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select s.nbase,s.a0100 from ");
			sql.append(tableName+" s where 1=1 ");
			if("1".equals(type)&&selectedid!=null&&!"".equals(selectedid))
			{
				String sqlstr=this.getSql(selectedid, "s");
				sql.append(" and "+sqlstr);
			}
			else
			{
				if(codesql!=null&&!"".equals(codesql))
				{

	                sql.append(" and "+codesql);
				}
				
				if(privSql!=null&&privSql.trim().length()>0)
				{
					sql.append(" and ");
					sql.append(privSql);
				}
				if(beforeSql!=null&&!"".equals(beforeSql))
        	    {
					if(beforeSql.toUpperCase().startsWith(" AND")||beforeSql.toUpperCase().startsWith("AND"))
					{
        	        	sql.append(" and ");
        	        	sql.append("("+beforeSql.substring(4).toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
					}else{
						sql.append(" and ");
        	        	sql.append("("+beforeSql.toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
					}
        	    }
			}
			sql.append(" and ");
			sql.append(" a00z1=(select max(a00z1) from "+tableName+" X where X.a0100=s.a0100 and X.nbase=s.nbase and X.a00z0=s.a00z0) ");
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				list.add(rs.getString("nbase")+rs.getString("a0100"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取得实际发送内容
	 * @param content
	 * @param prea0100
	 * @param fieldList
	 * @return
	 */
	public String getFactContent(String content,String prea0100,ArrayList fieldList,UserView uv)
	{
		String fact_content=content;
		try
		{
			String pre=prea0100.substring(0,3);
			String a0100=prea0100.substring(3);
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name=new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql=new StringBuffer();
			StringBuffer where_sql2= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			for(int i=0;i<fieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)fieldList.get(i);
				String id=(String)bean.get("id");
				String fieldtitle=(String)bean.get("fieldtitle");
				String fieldtype=((String)bean.get("fieldtype")).trim();
				String fieldcontent=(String)bean.get("fieldcontent");
				String fieldid=(String)bean.get("fieldid");
				String dateformat=(String)bean.get("dateformat");
				String fieldlen=(String)bean.get("fieldlen");
				String ndec=(String)bean.get("ndec");
				String codeset=(String)(bean.get("codeset")==null?"":bean.get("codeset"));
				String nflag=(String)bean.get("nflag");
				String replace="";//要被替换的内容
				String factcontent="";
				String setid="";
				if("0".equals(nflag))
				{
					replace="\\$"+fieldid+":"+fieldtitle.trim()+"\\$";
				}
				if("1".equals(nflag))
				{
					replace="\\#"+fieldid+":"+fieldtitle.trim()+"\\#";
				}
				/**指标和公式项目处理不同*/
				if("0".equals(nflag))//指标
				{
					String fieldsetid="";
					if("A0101".equalsIgnoreCase(fieldcontent.trim())|| "B0110".equalsIgnoreCase(fieldcontent.trim())|| "E0122".equalsIgnoreCase(fieldcontent.trim())|| "e01a1".equalsIgnoreCase(fieldcontent.trim())) {
                        fieldsetid="a01";
                    } else {
                        fieldsetid=getFieldSetId(fieldcontent.trim());
                    }
					if(fieldsetid==null||fieldsetid.length()<=0) {
                        continue;
                    }
					buf.append("select ");
					buf.append(fieldcontent);
					buf.append(" from ");
					buf.append(pre+fieldsetid);
					buf.append(" where ");
					/*if(isConf(fieldsetid))//是否用判断年月标识
					{
						buf.append("");
					}
					else
					{*/
						buf.append(pre+fieldsetid+".a0100='");
						buf.append(a0100+"'");
						if("a01".equalsIgnoreCase(fieldsetid))
						{
							
						}		
						else
						{
							buf.append(" and ");
						    buf.append(pre+fieldsetid+".i9999=(select max(i9999) from ");
						    buf.append(pre+fieldsetid);
						    buf.append(" where ");
						    buf.append(pre+fieldsetid+".a0100='");
						    buf.append(a0100+"')");
						}						
						rs=dao.search(buf.toString());
						while(rs.next())
						{			
							//  JinChunhai 2011.05.19  orcle库遇到日期型的字段后台会报错
							if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))  // 代码型
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("N".equalsIgnoreCase(fieldtype.trim()))  // 数值型按格式显示
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("D".equalsIgnoreCase(fieldtype.trim()))  // 日期型按格式显示
							{
								java.sql.Date dd=rs.getDate(fieldcontent.trim());
								if(dd!=null){
									SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
									factcontent=format.format(dd);
								}
								
							}else 
							{
								factcontent=rs.getString(fieldcontent.trim());
							}
							
//							factcontent=rs.getString(fieldcontent.trim());
						}
						
						if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))//代码型
						{
							factcontent=AdminCode.getCodeName(codeset,factcontent);
						}
						
						/**日期型按格式显示*/
						if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat))) {
                            factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
                        }
						/**数值型按格式显示*/
						if("N".equalsIgnoreCase(fieldtype.trim())) {
                            factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
                        }
						if(factcontent==null||factcontent.trim().length()==0)
			    		{
			    			if("N".equalsIgnoreCase(fieldtype.trim())) {
                                factcontent="0.0";
                            } else {
                                factcontent=" ";
                            }
			    		}
						fact_content=fact_content.replaceAll(replace,factcontent);
					    buf.setLength(0);
						table_name.setLength(0);
						where_sql.setLength(0);
						where_sql2.setLength(0);
					
				}
				if("1".equals(nflag))//公式
				{
					ArrayList list = new ArrayList();
					String zh_sql=getSql(fieldcontent,fieldtype,uv,list);
					if(zh_sql==null||zh_sql.length()<=0) {
                        continue;
                    }
					if("D".equalsIgnoreCase(fieldtype.trim()))//日期型是否直接查
					{
					    setid=getFieldSetId(zh_sql.trim());
						buf.append("select ");
						buf.append(zh_sql);
						buf.append(" from ");
						if(setid!=null&&!"".equals(setid)) {
                            buf.append(pre+setid);
                        } else {
                            buf.append(" usra01 ");
                        }
						buf.append(" where a0100='");
						buf.append(a0100+"'");
					}
					else
					{
			    		/*String[] temp_arr=zh_sql.split(",");*/
				    	if(list.size()>0)
			    		{
				    		buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
			    			for(int j=0;j<list.size();j++)
			    			{
			    				
					    		/*int index=temp_arr[j].lastIndexOf("(")+1;
					    		String itemid=temp_arr[j].substring(index,temp_arr[j].length());
					    		String fieldsetid ="";
					    		if(itemid.equalsIgnoreCase("B0110")||itemid.equalsIgnoreCase("E0122")||itemid.equalsIgnoreCase("e01a1"))
					    			fieldsetid="a01";
					    		else
					    	    	fieldsetid=getFieldSetId(itemid);//item.getFieldsetid();
					    		if(fieldsetid.length()==0)
					    			fieldsetid="a01";*/
			    				String fieldsetid = (String)list.get(j);
					    		String tableName=pre+fieldsetid;
					    		name_set.add(tableName);
					    		if(j!=0) {
                                    where_sql2.append(" and ");
                                }
					     		where_sql2.append(tableName+".a0100='");
					      		where_sql2.append(a0100+"'");
							//
					    		if("a01".equalsIgnoreCase(fieldsetid))
					    		{
					    		}
					    		else
					    		{
					    			if(j!=0) {
                                        where_sql.append(" and ");
                                    }
				    	    		where_sql.append(tableName);
					        		where_sql.append(".i9999=(");
				         			where_sql.append("select max(i9999) from ");
					        		where_sql.append(tableName+" "+tableName);
					        		where_sql.append(" where ");
					        		where_sql.append(tableName+".a0100='");
					        		where_sql.append(a0100+"') ");
					    		}
					    	}
				    		buf.append(name_set.toString().substring(1,name_set.toString().length()-1));
				    		buf.append(" where ");
				    		buf.append(where_sql);
					    	if(where_sql.length()>0) {
                                buf.append(" and ");
                            }
			    	    	buf.append(where_sql2);
			    		}
			    		else
			    		{
			    			buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
				    		buf.append("usra01 where ");
				    		buf.append("a0100='");
				    		buf.append(a0100+"'");
				    	}	
			    	}					
					rs=dao.search(buf.toString());
		    		while(rs.next())
		    		{
		    			if("D".equalsIgnoreCase(fieldtype))
		    			{
		    			/*
		    				if(setid==null || setid.trim().length()<=0 || setid.equals(""))
		    				{
		    	    			if(rs.getString(1)!=null)
		    	    			{
		    			             factcontent=rs.getString(1);
		    			    	}else
		    			    	{
		    			    		factcontent="";
		    		    		}
		    				}else */
		    				{
		    					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {// 对于sqlserver用getString()
									Date date = rs.getDate(1);
									if (date != null) {
										factcontent = date.toString();
									}
								} else {
									factcontent = rs.getString(1);
								}
		    					
//		    					if(rs.getDate(1)!=null)
//		    					{
//		    						java.sql.Date dd=rs.getDate(1);
//			    			        SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//			    			        factcontent=format.format(dd);
//		    					}
//		    					else
//		    					{
//		    						factcontent="";
//		    					}
		    				}
		    			}
		    			else {
                            factcontent=rs.getString(1);
                        }
		    		}
		    		
		    		/**日期型按格式显示*/
					if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat))) {
                        factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
                    }
					/**数值型按格式显示*/
					if("N".equalsIgnoreCase(fieldtype.trim())) {
                        factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
                    }
					if(factcontent==null||factcontent.trim().length()==0)
		    		{
		    			if("N".equalsIgnoreCase(fieldtype.trim())) {
                            factcontent="0.0";
                        } else {
                            factcontent=" ";
                        }
		    		}
				    fact_content=fact_content.replaceAll(replace,factcontent);
			    	buf.setLength(0);
			    	table_name.setLength(0);
			    	name_set.clear();
		    		where_sql.setLength(0);
		    		where_sql2.setLength(0);
				}
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fact_content;
	}
	public ArrayList getMidVList(String salaryid)
	{
		ArrayList fieldlist = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rset = dao.search(buf.toString());
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldlist;
	}
	/**
	 * 工资发放中直接从工资表里取数据,如果定义的公式和指标不在改工资套的项目中，内容不被替换
	 * @param content
	 * @param prea0100
	 * @param fieldList
	 * @param uv
	 * @param tableN
	 * @return
	 */
	public String getFactContentFromSalaryTempTable(String content,String prea0100,ArrayList fieldList,UserView uv,String tableN,HashMap salaryItemMap)
	{
		String fact_content=content;
		try
		{
			String pre=prea0100.substring(0,3);
			String a0100=prea0100.substring(3);
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name=new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql=new StringBuffer();
			StringBuffer where_sql2= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			for(int i=0;i<fieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)fieldList.get(i);
				String id=(String)bean.get("id");
				String fieldtitle=(String)bean.get("fieldtitle");
				String fieldtype=(String)bean.get("fieldtype");
				String fieldcontent=(String)bean.get("fieldcontent");
				String fieldid=(String)bean.get("fieldid");
				String dateformat=(String)bean.get("dateformat");
				String fieldlen=(String)bean.get("fieldlen");
				String ndec=(String)bean.get("ndec");
				String codeset=(String)(bean.get("codeset")==null?"":bean.get("codeset"));
				String nflag=(String)bean.get("nflag");
				String replace="";//要被替换的内容
				String factcontent="";
				String setid="";
				if("0".equals(nflag))
				{
					replace="\\$"+fieldid+":"+fieldtitle.trim()+"\\$";
				}
				if("1".equals(nflag))
				{
					replace="\\#"+fieldid+":"+fieldtitle.trim()+"\\#";
				}
				/**指标和公式项目处理不同*/
				if("0".equals(nflag))//指标
				{
					String fieldsetid="";
					
					if(salaryItemMap.get(fieldcontent.trim().toUpperCase())!=null)
					{
						buf.append("select ");
						if("N".equalsIgnoreCase(fieldtype)) {
                            buf.append(" sum");
                        } else {
                            buf.append(" max");
                        }
						buf.append("("+fieldcontent+") as "+ fieldcontent);
						buf.append(" from "+tableN);
						buf.append(" where a0100='"+a0100+"' and UPPER(nbase)='"+pre.toUpperCase()+"'");
						rs=dao.search(buf.toString());
						while(rs.next())
						{
							if("D".equalsIgnoreCase(fieldtype.trim())&&Sql_switcher.searchDbServer()==Constant.ORACEL)
							{
								Date dd=rs.getDate(fieldcontent.trim());
								if(dd!=null)
								{
									factcontent=dd.toString();
								}
							}
							else
							{
						    	factcontent=rs.getString(fieldcontent.trim());
							}
						}
						if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))//代码型
						{
							factcontent=AdminCode.getCodeName(codeset,factcontent);
						}
						
						/**日期型按格式显示*/
						if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat))) {
                            factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
                        }
						/**数值型按格式显示*/
						if("N".equalsIgnoreCase(fieldtype.trim())) {
                            factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
                        }
						if(factcontent==null||factcontent.trim().length()==0)
			    		{
			    			if("N".equalsIgnoreCase(fieldtype.trim())) {
                                factcontent="0.0";
                            } else {
                                factcontent=" ";
                            }
			    		}
						fact_content=fact_content.replaceAll(replace,factcontent);
					}
					
					buf.setLength(0);
					table_name.setLength(0);
					where_sql.setLength(0);
					where_sql2.setLength(0);	
				}
				if("1".equals(nflag))//公式
				{
					
					//String zh_sql=getSql(fieldcontent,fieldtype,uv);
					//ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					try
					{
					    YksjParser yp = new YksjParser(uv,this.allFielditemList,YksjParser.forNormal,getColumType(fieldtype.trim()),3,pre,"");
		    			yp.run(fieldcontent);
			    		String temp = yp.getSQL();//公式的结果
					/*String[] temp_arr=zh_sql.split(",");
			    	if(temp_arr.length>1)
		    		{
		    			for(int j=0;j<temp_arr.length-1;j++)
		    			{
				    		int index=temp_arr[j].lastIndexOf("(")+1;
				    		String itemid=temp_arr[j].substring(index,temp_arr[j].length());
				    		if(salaryItemMap.get(itemid.trim().toUpperCase())==null)
				    		{
				    			isHave=false;
				    			break;
				    		}
		    			}
		    		}*/
		     				buf.append("select ");
		    				if("N".equalsIgnoreCase(fieldtype)) {
                                buf.append(" sum");
                            } else {
                                buf.append(" max");
                            }
				     		buf.append("("+temp+") as T");
				    		buf.append(" from "+tableN);
				    		buf.append(" where a0100='"+a0100+"' and UPPER(nbase)='"+pre.toUpperCase()+"'");
			        		rs=dao.search(buf.toString());
		        	    	while(rs.next())
		    	        	{
		        	    		String str = "";
		        	    		if("D".equalsIgnoreCase(fieldtype)){
		        	    			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {// 对于sqlserver用getString()
										Date date = rs.getDate(1);
										if (date != null) {
											factcontent = date.toString();
										}
									} else {
										factcontent = rs.getString(1);
									}
		        	    			//str = rs.getString(1);//rs.getDate(1).toString();
		        	    		}else{
		        	    			str = rs.getString(1);
		        	    		}
		    	    	       if(str!=null)
		    	    	       {
		    		        	    factcontent=str;
		    		        	}else
		    		           {
		    		        	    factcontent="";
		    		           }	
		        		    }
						}
						catch(Exception e)
						{
							//factcontent="";
						}
		    		    if(factcontent!=null&&!"".equals(factcontent))
		    		    {
		            		/**日期型按格式显示*/
			        		if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat))) {
                                factcontent=this.getYMDFormat(Integer.parseInt(dateformat),factcontent);
                            }
			        		/**数值型按格式显示*/
			    	    	if("N".equalsIgnoreCase(fieldtype.trim())) {
                                factcontent=this.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
                            }
		    		    }
		    	    if(factcontent!=null&&!"".equals(factcontent)) {
                        fact_content=fact_content.replaceAll(replace,factcontent);
                    }
			    	buf.setLength(0);
			    	table_name.setLength(0);
			    	name_set.clear();
		    		where_sql.setLength(0);
		    		where_sql2.setLength(0);
				}
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fact_content;
	}
	private ArrayList getGzFieldList(String salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select * from salaryset where salaryid=");
			buf.append(salaryid);
			buf.append(" order by sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setFieldsetid(rset.getString("fieldsetid"));
				item.setItemid(rset.getString("itemid"));
				item.setItemdesc(rset.getString("itemdesc"));
				item.setItemtype(rset.getString("itemtype"));
				item.setItemlength(rset.getInt("itemlength"));
				item.setDisplaywidth(rset.getInt("nwidth"));
				item.setDecimalwidth(rset.getInt("decwidth"));
				item.setCodesetid(rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset,"formula"));
				item.setVarible(0);
				fieldlist.add(item);
			}//while loop end.
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return fieldlist;
	}
	public HashMap getSalarySetMap(String salaryid)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select itemid from salaryset where salaryid="+salaryid);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			while(rs.next())
			{
				map.put(rs.getString("itemid").toUpperCase(), "1");
			}
			String sql = "select cname,chz from midvariable where templetid=0 and(cstate="+salaryid+" or cstate is null) and nflag=0";
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("cname").toUpperCase(), "1");
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 判断子集中是否含有年月标识字段
	 * @param fieldsetid
	 * @return
	 */
	public boolean isConf(String fieldsetid){
		boolean  flag = false;
		ArrayList list = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem item = (FieldItem)list.get(i);
			if(item.getItemid().equalsIgnoreCase(fieldsetid+"z0")){
				flag=true;
				break;
			}
		}
		return flag ;
	}
	/**
	 * 将中文公式转换成sql
	 * @param c_expr
	 * @param varType
	 * @param uv
	 * @return
	 */
	public String getSql(String c_expr,String varType,UserView uv){
		String  temp = "";
		try{
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType.trim()),3,"usr","");
				yp.run(c_expr);
				temp = yp.getSQL();//公式的结果
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		return temp ;
	}
	
	public String getSql(String c_expr,String varType,UserView uv,ArrayList listset){
		String  temp = "";
		try{
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType.trim()),3,"usr","");
				yp.run(c_expr);
				temp = yp.getSQL();//公式的结果
				listset.addAll(yp.getUsedSets());
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		return temp ;
	}
	/**
	 * 得到数据类型
	 * @param type
	 * @return
	 */
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=IParserConstant.STRVALUE;
		}else if("D".equals(type)){
			temp=IParserConstant.DATEVALUE;
		}else if("N".equals(type)){
			temp=IParserConstant.FLOAT;
		}else if("L".equals(type)){
			temp=IParserConstant.LOGIC;
		}else{
			temp=IParserConstant.STRVALUE;
		}
		return temp;
	}
	/**
	 * 取得邮件模板的内容
	 * @param templateId
	 * @return
	 */
	public String getEmailContent(int templateId)
	{
		String content="";
		try
		{
			String sql = "select content from email_name where id="+templateId;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				content=rs.getString("content");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return content;
	}
	public HashMap sendMessage(int type,ArrayList emailContentList,String templateId,ArrayList attachList,String from,UserView userView,int flagtype,String mobile_field)throws Exception 
	{
		HashMap map = new HashMap();
		int isSucess=0;
		StringBuffer buf = new StringBuffer("");
		try
		{
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			   Calendar calendar = Calendar.getInstance();
			   String currenttime = String.valueOf(calendar.get(Calendar.YEAR))+"-"
			   +String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"
			   +String.valueOf(calendar.get(Calendar.DATE))+" "
			   +String.valueOf(calendar.get(Calendar.HOUR))+":"
			   +String.valueOf(calendar.get(Calendar.MINUTE))+":"
			   +String.valueOf(calendar.get(Calendar.SECOND));
			 updateTableCloumns();
			 ArrayList destlist = new ArrayList();
			 ArrayList alist = new ArrayList();
			 for(int i=0;i<emailContentList.size();i++)
			 {
				  int flag=1;
				  LazyDynaBean bean=(LazyDynaBean)emailContentList.get(i);
				  if(bean.get(mobile_field.toLowerCase())==null|| "".equals((String)bean.get(mobile_field.toLowerCase())))
				  {
					  if(buf!=null&&buf.length()>0)
					  {
						  buf.append("\r\n");
					  }
					  buf.append("------------------------");
					  buf.append((String)(bean.get("a0101")==null?"":bean.get("a0101")));
					  buf.append("------------------------");
					  buf.append("\r\n");
					  buf.append("电话号码为空");
					  flag=2;
					  this.configSend_ok(flag,templateId,(String)bean.get("a0100"),(String)bean.get("pre"),(String)bean.get("username"),(String)bean.get("I9999"));
					  continue;
				  }
				  if(buf!=null&&buf.length()>0)
				  {
					  buf.append("\r\n");
				  }
				  String content=(String)bean.get("content");
				  LazyDynaBean dyvo=new LazyDynaBean();
				  dyvo.set("sender",userView.getUserFullName());
				  dyvo.set("receiver",(String)bean.get("a0101"));
				  dyvo.set("phone_num",(String)bean.get(mobile_field.toLowerCase()));
				  content=content.replaceAll(" ", "");
				  content=content.replaceAll("\\r","");
				  content=content.replaceAll("\\n","");
				  content=content.replaceAll("\\r\\n","");
				  dyvo.set("msg",content);
				  destlist.add(dyvo);
				  alist.add(bean);
			   }
			 try
	     		{
	     			if(destlist!=null&&destlist.size()>0)
	     			{
	            		SmsBo smsbo=new SmsBo(this.conn);
		        		smsbo.batchSendMessage(destlist);
	     			}
	     		}
	     		catch(Exception e)
	     		{
	     			isSucess=2;
	     			 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.employResume.sendmessagefaild"))); 
	     		}
	     		if(isSucess==0&&alist.size()>0)
	     		{
	     			for(int i=0;i<alist.size();i++)
	     			{
	     				int flag=1;
	     				LazyDynaBean bean=(LazyDynaBean)alist.get(i);
	     				this.configSend_ok(flag,templateId,(String)bean.get("a0100"),(String)bean.get("pre"),(String)bean.get("username"),(String)bean.get("I9999"));
	     			}	
	     		}
	     		 if(buf!=null&&buf.toString().length()>0)
	  		   {
	  		      buf.append("\r\n"); 
	  		      buf.append("-----------------------------------------");
	  		      buf.append("发送时间:"+currenttime);
	  		      buf.append("-----------------------------------------");
	  		   }
	  		   String outName="EmailError.txt";
	  		   if(type==0&&buf.toString().length()>0)
	  		   {
	  	     	   FileOutputStream fileOut = null;
	  	     	   try{	  	     		   
	  	     		   fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
	  	     		   fileOut.write(buf.toString().getBytes());
	  	     	   }finally{
	  	     		   PubFunc.closeIoResource(fileOut);
	  	     	   }
//	  	    	   outName=outName.replace(".txt","#");
	  		   }
	  		   else
	  		   {
	  			   outName="1";
	  			   return null;//成功 返回null
	  		   }
	  		   map.put("flag", isSucess+"");
//			   map.put("file",SafeCode.encode(PubFunc.encrypt(outName)));
	  		   //20/3/17 xus vfs改造
			   map.put("file",PubFunc.encrypt(outName));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	/**
	 * 发送邮件
	 * @param emailContentList 对应选中模板的人员列表
	 * @param templateId 选中的模板
	 * @param attachList 模板的附件列表
	 * @param from 发送地址
	 * @throws Exception 
	 * @throws GeneralException 
	 */
	public HashMap sendEmail(int type,ArrayList emailContentList,String templateId,ArrayList attachList,String from,UserView userView,int flagtype) throws Exception /*throws GeneralException*/
	{
		HashMap map = new HashMap();
		int isSucess=0;
		StringBuffer buf = new StringBuffer("");
	   try
	   {
		   //send_no=0; //邮件末发
		  // send_ok=1; //邮件已发成功
		  // send_err=2; //邮件发送失败
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		   Calendar calendar = Calendar.getInstance();
		   String currenttime = String.valueOf(calendar.get(Calendar.YEAR))+"-"
		   +String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"
		   +String.valueOf(calendar.get(Calendar.DATE))+" "
		   +String.valueOf(calendar.get(Calendar.HOUR))+":"
		   +String.valueOf(calendar.get(Calendar.MINUTE))+":"
		   +String.valueOf(calendar.get(Calendar.SECOND));
		   updateTableCloumns();
		   //EMailBo bo=null;
		   AsyncEmailBo bo = new AsyncEmailBo(conn, userView);
		   int maxsend = 10;
			String max=getMaxSend();
			if(max == null || "".equals(max)||((int)Float.parseFloat(max))==0) {
                maxsend=10;
            } else {
                maxsend=Integer.parseInt(max);
            }
			String username="";
			String password="";
			String from_addr=from;
		   if(flagtype==0)
		   {
			  
		   }
		   ArrayList toAdd = new ArrayList();
		   ArrayList bodyList = new ArrayList();
		   int n=0;
	        if(emailContentList.size()%maxsend==0) {
                n=emailContentList.size()/maxsend;
            } else {
                n=emailContentList.size()/maxsend+1;
            }
	        //bo = new EMailBo(this.conn,true);
	        for(int j=0;j<n;j++){
	        /*
	         * 使用邮件服务器的配置，不使用管理员的邮箱，否则验证出错 guodd 2015-12-08
	         * 	try
	 		   {
	        	   boolean flag=true;
	 		       
	 		       LazyDynaBean bean = this.getFromAddrByUser(userView);
				   if(bean.get("email")!=null&&!((String)bean.get("email")).equals("")&&bean.get("pw")!=null&&!((String)bean.get("pw")).equals(""))
				   {
					   username=(String)bean.get("email");
					   password=(String)bean.get("pw");
					   from_addr=username;
					   bo.setUsername(username);
	 		    	   bo.setPassword(password);
	 		    	   if(bo.configTransfor())
	 		    	   {
	 		    		   flag=false;
	 		    	   }
				   }
				   if(flag)
				   {
					    bean = this.getOperuserBean(userView);
 		    		   if(bean.get("email")!=null&&!((String)bean.get("email")).equals("")&&bean.get("pw")!=null&&!((String)bean.get("pw")).equals(""))
 					   {
 						   username=(String)bean.get("email");
 						   password=(String)bean.get("pw");
 						   from_addr=username;
 						   bo.setUsername(username);
 		 		    	   bo.setPassword(password);
 		 		    	   if(bo.configTransfor())
 		 		    	   {
 		 		    		   flag=false;
 		 		    	   }
 					   }
				   }
	 		       if(flag)
	 		       {
	 		    	  try
	 		    	   {
	 		    		   bo= new EMailBo(this.conn,true,"");
	 		    		   from_addr=from;
	 		    	   }
	 		    	   catch(Exception ex)
	 		    	   {
	 		    		  isSucess=2;
	 		    	   }
	 		       }
	 		   }
	 		   catch(Exception e)
	 		   {
	 			   isSucess=2;
	 		   }
	 		   if(isSucess==2)
	 		   {
	 			  map.put("flag", isSucess+"");
	 			  return map;
	 		   }*/
		   for(int i=j*maxsend;i<(j+1)*maxsend&&i<emailContentList.size();i++)
		   {
			   int flag=1;
			   LazyDynaBean bean=(LazyDynaBean)emailContentList.get(i);
			   //System.out.println("address="+bean.get("address"));
			   if(bean.get("address")==null|| "".equals((String)bean.get("address"))||!isMail((String)bean.get("address")))
			   {
				   if(buf!=null&&buf.length()>0)
				   {
					   buf.append("\r\n");
				   }
				   buf.append("------------------------");
				   buf.append((String)(bean.get("a0101")==null?"":bean.get("a0101")));
				   buf.append("------------------------");
				   buf.append("\r\n");
				   buf.append("邮件地址为空");
				   flag=2;
				   this.configSend_ok(flag,templateId,(String)bean.get("a0100"),(String)bean.get("pre"),(String)bean.get("username"),(String)bean.get("I9999"));
				   continue;
			   }
			   if(buf!=null&&buf.length()>0)
			   {
				   buf.append("\r\n");
			   }
			   toAdd.add((String)bean.get("address"));
			   String content=(String)bean.get("content");
			   content=content.replaceAll("\r\n","<br>");
			   //不能替换空格，因为html模板会将节点中的空格替换，导致html代码全都显示到页面上了 guodd 2018-11-30
			   //content=content.replace(" ", "&nbsp;");
			   content=content.replace("\n","<br>");
			   content=content.replace("\r","<br>");
			   content=content.replace("><br>",">");
			   content=content.replace("> <br>",">");
			   content=content.replace(">  <br>",">");
			   bodyList.add(content);
			   boolean bool = true;
			   try
			   {
			         //bo.sendEqualEmail((String)bean.get("subject"),bodyList,attachList,from_addr,toAdd,null);
				   LazyDynaBean email = new LazyDynaBean();
				   email.set("toAddr", bean.get("address"));
				   email.set("subject",bean.get("subject"));
				   email.set("bodyText", content);
				   /*添加附件*/
				   email.set("attachList", attachList);
				   bo.sendSync(email);
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
				   flag=2;
				   bool = false;
			   }
			   if(bool)
			   {
				   /**发送成功的不放入文件中*/
				   //buf.append("邮件发送到"+(String)bean.get("address")+"成功");
			   }
			   else
			   {
				   buf.append("------------------------");
				   buf.append((String)(bean.get("a0101")==null?"":bean.get("a0101")));
				   buf.append("------------------------");
				   buf.append("\r\n");
				   buf.append("邮件发送到"+(String)bean.get("address")+"失败");
			   }
			   toAdd.clear();
			   bodyList.clear();
			   this.configSend_ok(flag,templateId,(String)bean.get("a0100"),(String)bean.get("pre"),(String)bean.get("username"),(String)bean.get("I9999"));
		   }
	        }
		   for(int k=0;k<attachList.size();k++)//将生成的附件删除
		   {
			   File file=new File((String)attachList.get(k));
			   if(file.exists()) {
                   file.delete();
               }
		   }
		   if(buf!=null&&buf.toString().length()>0)
		   {
		      buf.append("\r\n"); 
		      buf.append("-----------------------------------------");
		      buf.append("发送时间:"+currenttime);
		      buf.append("-----------------------------------------");
		   }
		   String outName="EmailError.txt";
		   if(type==0&&buf.toString().length()>0)
		   {
		       FileOutputStream fileOut = null ; 
		       try{
		           fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
	               fileOut.write(buf.toString().getBytes());
	               //fileOut.close();
//	               outName=outName.replace(".txt","#");
		       }catch(Exception e){
		           e.printStackTrace();
		       }
		       finally{
		          PubFunc.closeIoResource(fileOut);
		       }
	     	   
		   }
		   else
		   {
			   outName="1";
			   return null;//成功 返回null
		   }
		   map.put("flag", isSucess+"");
//		   map.put("file",SafeCode.encode(PubFunc.encrypt(outName)));
		   //20/3/17 xus vfs改造
		   map.put("file",PubFunc.encrypt(outName));
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
		   throw e;
	   }
	   return map;
		
	}
	public LazyDynaBean getFromAddrByUser(UserView userView)
	{
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(userView.getStatus()==0)//业务用户
			{
				boolean flag=true;
				if(userView.getA0100()!=null&&!"".equals(userView.getA0100()))
				{
					String emailField="";
					RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
			        if(stmp_vo==null) {
                        return bean;
                    }
			        String param=stmp_vo.getString("str_value");
			        if(param==null|| "#".equals(param)) {
                        return bean;
                    }
			        emailField=param;
			        String zpFld = "";
					String pwdFld="";
			        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
					if (login_vo != null)
					{
					    String login_name = login_vo.getString("str_value");
						int idx = login_name.indexOf(",");
						if (idx != -1)
						{
						    zpFld = login_name.substring(0, idx);
						    if(login_name.length()>idx) {
                                pwdFld=login_name.substring(idx+1);
                            }
						}
					 }
					if("".equals(pwdFld)|| "#".equals(pwdFld)) {
                        pwdFld="userpassword";
                    }
			        if(emailField!=null&&!"".equals(emailField))
			        {
			        	rs=dao.search("select "+emailField+","+pwdFld+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'");
			        	while(rs.next())
			        	{
			        		String addr=rs.getString(emailField)==null?"":rs.getString(emailField);
			        		String pw=rs.getString(pwdFld)==null?"":rs.getString(pwdFld);
			        		if(addr.length()>0&&pw.length()>0) {
                                flag=false;
                            }
			        		bean.set("email", addr);
							bean.set("pw",pw);
			        	}
			        }
				}
	    		if(flag)
		    	{
		    		rs=dao.search("select email,password from operuser where UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
			    	while(rs.next())
			    	{
			    		String addr=rs.getString("email")==null?"":rs.getString("email");
				    	String pw=rs.getString("password")==null?"":rs.getString("password");
				    	bean.set("email", addr);
				    	bean.set("pw",pw);
			    	}
		    	}
			}
			else
			{
				String emailField="";
				RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_EMAIL");
		        if(stmp_vo==null) {
                    return bean;
                }
		        String param=stmp_vo.getString("str_value");
		        if(param==null|| "#".equals(param)) {
                    return bean;
                }
		        emailField=param;
		        String zpFld = "";
				String pwdFld="";
		        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				if (login_vo != null)
				{
				    String login_name = login_vo.getString("str_value");
					int idx = login_name.indexOf(",");
					if (idx != -1)
					{
					    zpFld = login_name.substring(0, idx);
					    if(login_name.length()>idx) {
                            pwdFld=login_name.substring(idx+1);
                        }
					}
				 }
				if("".equals(pwdFld)|| "#".equals(pwdFld)) {
                    pwdFld="userpassword";
                }
		        if(emailField!=null&&!"".equals(emailField))
		        {
		        	rs=dao.search("select "+emailField+","+pwdFld+" from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"'");
		        	while(rs.next())
		        	{
		        		String addr=rs.getString(emailField)==null?"":rs.getString(emailField);
		        		String pw=rs.getString(pwdFld)==null?"":rs.getString(pwdFld);
		        		bean.set("email", addr);
						bean.set("pw",pw);
		        	}
		        }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
	public LazyDynaBean getOperuserBean(UserView userView)
	{
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
	    try
	    {
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	rs=dao.search("select email,password from operuser where UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
	    	while(rs.next())
	    	{
	    		String addr=rs.getString("email")==null?"":rs.getString("email");
		    	String pw=rs.getString("password")==null?"":rs.getString("password");
		    	bean.set("email", addr);
		    	bean.set("pw",pw);
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
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	    }
	    return bean;
	}

	public String getMaxSend() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	      //xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("max_send");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	/**
	 * 取得待发送邮件的人员
	 * @param templateId
	 * @return
	 */
   public ArrayList getEmailContentList(String type,String selectA0100,String templateId,UserView userview,String queryYearValue,String queryvalue,String tableName,String privSql,String mobile_field,String e_m_type,String beforeSql)
   {
	   ArrayList list = new ArrayList();
	   try
	   {
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = null;
		  StringBuffer sql = new StringBuffer();
		  
		  if("1".equalsIgnoreCase(type))
		  {
			  String[] temp=selectA0100.split("`");
			  for(int i=0;i<temp.length;i++)
			  {
				  sql.append("select e.id,e.pre,e.subject,e.address,e.content,e.a0100,e.username,e.I9999, s.a0101 ");
				  if("1".equals(e_m_type)) {
                      sql.append(",C."+mobile_field);
                  }
				  sql.append(" from email_content e,"+tableName+" s");
				  if("1".equals(e_m_type)) {
                      sql.append(","+temp[i].substring(0,3)+"A01 C ");
                  }
				  sql.append(" where e.id="+templateId);
				  sql.append(" and e.a0100='");
				  sql.append(temp[i].substring(3,temp[i].indexOf("~")));
				  sql.append("' and e.pre='");
				  sql.append(temp[i].substring(0,3));
				/*  sql.append("' and e.username='");
				  sql.append(userview.getUserName());*/
				  sql.append("' and e.I9999=");
				  sql.append("'"+temp[i].substring(temp[i].indexOf("~")+1)+"'");
				  if(!"0".equalsIgnoreCase(queryvalue))
				  {
			     	  sql.append(" and ");
			     	  sql.append(Sql_switcher.month("e.send_time"));
			     	  sql.append("=");
		    		  sql.append(queryvalue);
				  }
				  if(StringUtils.isNotBlank(queryYearValue)){
					  sql.append(" and ");
			     	  sql.append(Sql_switcher.year("e.send_time"));
			     	  sql.append("=");
		    		  sql.append(queryYearValue);
				  }
				  sql.append(" and e.a0100=s.a0100 and e.pre=s.nbase");
				  if("1".equals(e_m_type)) {
                      sql.append(" and e.a0100=C.a0100");
                  }
				  sql.append("  and a00z1=(select max(a00z1) from "+tableName+" X where X.a0100=s.a0100 and X.nbase=s.nbase and X.a00z0=s.a00z0) ");
				  rs= dao.search(sql.toString());
		    	  while(rs.next())
		    	  {
	    			  LazyDynaBean bean = new LazyDynaBean();
		    		  bean.set("subject",rs.getString("subject"));
		    		  /**邮件地址中如果有空格，将其去掉*/
		    		  bean.set("address",rs.getString("address")==null?"":rs.getString("address").replaceAll(" ", ""));
		    		  bean.set("content",rs.getString("content")==null?"":rs.getString("content"));
		    		  bean.set("a0100",rs.getString("a0100"));
		    		  bean.set("pre",rs.getString("pre"));
		    		  bean.set("id",rs.getString("id"));
		    		  bean.set("username",rs.getString("username")==null?"":rs.getString("username"));
		    		  bean.set("I9999",rs.getString("I9999")==null?"":rs.getString("I9999"));
		    		  bean.set("a0101",rs.getString("a0101")==null?"":rs.getString("a0101"));
		    		  if("1".equals(e_m_type)) {
                          bean.set(mobile_field.toLowerCase(), rs.getString(mobile_field)==null?"":rs.getString(mobile_field));
                      }
			    	  list.add(bean);
		    	  }
		    	  sql.setLength(0);
			  }
		  }
		  else
		  {
			  String tview="";
			  if("1".equals(e_m_type))
			  {
				  tview = this.getTableView(tableName, mobile_field);
			  }
			  sql.append("select e.id,e.pre,e.subject,e.address,e.content,e.a0100,e.username,e.I9999,s.a0101 ");
			  if(tview!=null&&tview.trim().length()>0) {
                  sql.append(",T."+mobile_field);
              }
			  sql.append(" from email_content e,"+tableName+" s ");
			  if(tview!=null&&tview.trim().length()>0) {
                  sql.append(",("+tview+") T");
              }
			  sql.append(" where e.id=");
			  sql.append(templateId);
			  if(!"0".equalsIgnoreCase(queryvalue))
			  {
		     	  sql.append(" and ");
		     	  sql.append(Sql_switcher.month("e.send_time"));
		     	  sql.append("=");
	    		  sql.append(queryvalue);
			  }
			  if(StringUtils.isNotBlank(queryYearValue)){
				  sql.append(" and ");
		     	  sql.append(Sql_switcher.year("e.send_time"));
		     	  sql.append("=");
	    		  sql.append(queryYearValue);
			  }
			 if(privSql!=null&&!"".equals(privSql))
			 {
				 sql.append(" and "+privSql);
			 }
			 if(beforeSql!=null&&!"".equals(beforeSql))
				{
					if(beforeSql.toUpperCase().startsWith(" AND")||beforeSql.toUpperCase().startsWith("AND"))
					{
			    		sql.append(" and ("+beforeSql.substring(4).toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
					}else{
						sql.append(" and ("+beforeSql.toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
					}
				}
			  sql.append(" and e.a0100=s.a0100 and e.pre = s.nbase ");
			  if(tview!=null&&tview.trim().length()>0)
			  {
				  sql.append(" and T.a0100=e.a0100 and T.nbase=e.pre ");
			  }
	    	  rs= dao.search(sql.toString());
	    	  while(rs.next())
	    	  {
    			  LazyDynaBean bean = new LazyDynaBean();
	    		  bean.set("subject",rs.getString("subject"));
	    		  bean.set("address",rs.getString("address")==null?"":rs.getString("address"));
	    		  bean.set("content",rs.getString("content"));
	    		  bean.set("a0100",rs.getString("a0100"));
	    		  bean.set("pre",rs.getString("pre"));
	    		  bean.set("id",rs.getString("id"));
	    		  bean.set("username",rs.getString("username"));
	    		  bean.set("I9999",rs.getString("I9999"));
	    		  bean.set("a0101",rs.getString("a0101")==null?"":rs.getString("a0101"));
	    		  if(tview!=null&&tview.trim().length()>0)
	    		  {
	    			  bean.set(mobile_field.toLowerCase(), rs.getString(mobile_field)==null?"":rs.getString(mobile_field));
	    		  }
		    	  list.add(bean);
	    	  }
		  }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return list;
   }
   public String getTableView(String tableName,String mobile_field)
   {
	   String sql = "";
	   RowSet rs = null;
	   try
	   {
		   ContentDAO dao  = new ContentDAO(this.conn);
		   rs = dao.search("select distinct nbase from "+tableName);
		   StringBuffer buf = new StringBuffer("");
		   while(rs.next())
		   {
			   String pre = rs.getString("nbase");
			   buf.append(" union All ");
			   buf.append(" (select a0100,'"+pre+"' as nbase,"+mobile_field+" from "+pre+"A01)");
			  
		   }
		   if(buf.toString().length()>0)
		   {
			   String str = buf.toString().substring(10);
			   sql = "select * from ("+str+") T ";
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
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
		   }
	   }
	   return sql;
   }
   /**
    * 将邮件模板的附件从数据库中取出，还原成文件，返回的是由文件名组成的列表
    * @param templateId
    * @return
    */
   public ArrayList getAttachFileName(String templateId)
   {
	   ArrayList list = new ArrayList();
	   InputStream is=null;
	   FileOutputStream fileOut=null;
	   try
	   {
		   String sql ="select filename,attach from email_attach where id="+templateId;
		   ContentDAO dao = new ContentDAO(this.conn);
		   byte buf[] = new byte[1024];
		   RowSet rs=null;
		   rs=dao.search(sql);
		   while(rs.next())
		   {
			   try{
				   is=rs.getBinaryStream("attach");
				   if(is==null)
				   {
					   continue;
				   }
				   String filename=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+rs.getString("filename");
				   File file = new File(filename);
				   if(file.exists()) {
                       if(file.delete()) {
                           file.createNewFile();
                       } else
                       {
                       }
                   }
				   if(!file.exists()) {
                       file.createNewFile();
                   }
				   fileOut = new FileOutputStream(filename);
				   int length;
				   while((length=is.read(buf))!=-1)
				   {
					   fileOut.write(buf,0,length);
					   fileOut.flush();
				   }
				   list.add(filename);
			   }finally{
				   PubFunc.closeResource(fileOut);
				   PubFunc.closeIoResource(is);
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
    * 验证邮件地址是否合法
    * @param email
    * @return
    */
	public boolean isMail(String email){
		   
		//String emailPattern ="^([a-z0-9A-Z]+[_]*[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String emailPattern	="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	    return  email.matches(emailPattern);
	}
	/**
	 * 取得系统邮件服务器设置的发送邮件的地址
	 * @return
	 * @throws GeneralException
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	      //xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	/**
	 * 将时间指标或公式按指定格式显示
	 * @param type
	 * @param time
	 * @return
	 */
	public String getYMDFormat(int type,String time)
	{
		String ret="";
		String year="";
		String month="";
		String day="";
		if(time==null||time.trim().length()<=0)
		{
			return ret;
		}
		if(time.length()>10)//是带时分秒的格式
		{
			int index=time.indexOf(" ");
			time=time.substring(0,index);
		}
		String separapor="";
		if(time.indexOf("-")!=-1) {
            separapor="-";
        } else if(time.indexOf(".")!=-1) {
            separapor=".";
        }
		year=time.substring(0,time.indexOf(separapor));
		month=time.substring(time.indexOf(separapor)+1,time.lastIndexOf(separapor));
		day=time.substring(time.lastIndexOf(separapor)+1);
		try
		{
			switch(type)
			{
			case 1:{
				ret=year+"."+month+"."+day;
				break;
			}
			case 2:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				if(Integer.parseInt(day)<10) {
                    day=day.substring(1);
                }
				ret=year+"."+month+"."+day;
				break;
			}
			case 3:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				if(Integer.parseInt(day)<10) {
                    day=day.substring(1);
                }
				year=year.substring(2);
				ret=year+"."+month+"."+day;
				break;
			}
			case 4:{
				ret=year+"."+month;
				break;
			}
			case 5:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				ret=year+"."+month; 
				break;
			}
			case 6:{
				year=year.substring(2);
				ret=year+"."+month;
				break;
			}
			case 7:{
				year=year.substring(2);
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				ret=year+"."+month;
				break;
			}
			case 8:{
				ret=year+ResourceFactory.getProperty("datestyle.year")
				    +month+ResourceFactory.getProperty("datestyle.month")
				    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 9:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				if(Integer.parseInt(day)<10) {
                    day=day.substring(1);
                }
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 10:{
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 11:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 12:{
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 13:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				if(Integer.parseInt(day)<10) {
                    day=day.substring(1);
                }
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month")
			    +day+ResourceFactory.getProperty("datestyle.day");
				break;
			}
			case 14:{
				if(Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				year=year.substring(2);
				ret=year+ResourceFactory.getProperty("datestyle.year")
			    +month+ResourceFactory.getProperty("datestyle.month");
				break;
			}
			case 15:{//年限
				int cYear=Calendar.getInstance().get(Calendar.YEAR);//当前年
				int cMonth=Calendar.getInstance().get(Calendar.MONTH)+1;//当前月
				int cDay=Calendar.getInstance().get(Calendar.DATE);//当前日
				ret=cYear-Integer.parseInt(year)+"";
				break;
			}
			case 16:{//年份
				ret=year;
				break;
			}
			case 17:{//月份
				if(month.length()>2&&Integer.parseInt(month)<10) {
                    month=month.substring(1);
                }
				ret=month;
				break;
			}
			case 18:{//日份
				if(day.length()>2&&Integer.parseInt(day)<10) {
                    day=day.substring(1);
                }
				ret=day;
				break;
			}
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 将数值型按指定的整数长度和小数长度显示
	 * @param integerlength 整数长度
	 * @param ndeclen 小数长度
	 * @param number 数值
	 * @return
	 */
	public String getNumberFormat(int integerlength,int ndeclen,String number)
	{
		String ret="";
		try
		{
			if(number==null||number.trim().length()<=0) {
                return ret;
            }
			/**为了不在数字前面补0，整数位只要1位*/
			/*String temp=this.getFormat(integerlength);
			if(temp.trim().length()<=0)
				temp="0";*/
			 String temp="0";
		     if(ndeclen>0)
		     {
		    	 String temp2=this.getFormat(ndeclen);
		    	 temp=temp+"."+temp2;
		     }
		     if(temp.length()>0)
		     {
		    	 DecimalFormat dcom= new DecimalFormat(temp);
		    	 ret=dcom.format(Double.parseDouble(number));
		     }else {
                 ret=number;
             }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * get format String
	 * @param len
	 * @return
	 */
	public String getFormat(int len)
	{
		String temp="";
		try
		{
			for(int i=0;i<len;i++)
			{
				temp+="0";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	/**
	 * 得到分页显示的sql
	 * @param codevalue
	 * @param templateId
	 * @param tableName
	 * @return
	 */
	public String getSelect_sqlAndWhere_sqlAndOrder_sql(String codevalue,String templateId,String tableName,String timesql,String username,String send_ok,String priv_mode,String privSql,String queryName,String order_by,String beforeSql)
	{
		StringBuffer sql = new StringBuffer();
		try
		{
			updateTableCloumns();
			//trim(I9999)
		    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    {
    			sql.append("select s.a0100,s.nbase,s.nbase||s.a0100||'~'||trim(I9999) as personid,s.b0110,s.e0122,s.a0101,a.id,a.send_ok,a.subject,I9999,address");
		    }
		    //ltrim(str(I9999))
		    else
		    {
		    	sql.append("select s.a0100,s.nbase,s.nbase+s.a0100+'~'+ltrim(str(I9999)) as personid,s.b0110,s.e0122,s.a0101,a.id,a.send_ok,a.subject,I9999,address");
		    }
    		sql.append("#");
			sql.append(" from "+tableName);
			sql.append(" s ");
			if(send_ok!=null&& "3".equals(send_ok)) {
                sql.append(" left join ");
            } else {
                sql.append(",");
            }
			sql.append(" (select e.id,e.a0100,e.pre,e.send_ok,e.subject,e.I9999,case when (address is null) then '' else address end as address from email_content e where ");
			if(!(templateId==null|| "".equals(templateId)))
			{
			      sql.append("e.id=");
			      sql.append(templateId);
			}
			else
			{
				sql.append(" e.id is null ");
			}
			if(!(timesql==null|| "".equals(timesql)))
			{
				sql.append(" and ");
			    sql.append(timesql);
			}
			if(send_ok!=null&&!"".equals(send_ok)&&!"3".equals(send_ok))
			{
				sql.append(" and e.send_ok=");
				sql.append(send_ok);
			}
			sql.append(" and e.i9999 is not null and e.send_time is not null ) a ");
			if(send_ok!=null&& "3".equals(send_ok))
			{
				sql.append(" on s.a0100=a.a0100 and a.pre=s.nbase ");
				sql.append(" where 1=1 ");
			}
			else
			{
				sql.append(" where s.a0100=a.a0100 and a.pre=s.nbase ");
				
			}
			if(codevalue!=null&&!"".equals(codevalue))
			{

                sql.append(" and "+codevalue);
			}
			
			if(privSql!=null&&privSql.trim().length()>0)
			{
				sql.append(" and ");
				sql.append(privSql);
			}
			if(queryName!=null&&!"".equals(queryName))
			{
				sql.append(" and ");
				sql.append(" s.a0101 like '"+queryName+"%'");
			}
			sql.append("  and a00z1=(select max(a00z1) from "+tableName+" X where X.a0100=s.a0100 and X.nbase=s.nbase and X.a00z0=s.a00z0) ");
			if(beforeSql!=null&&!"".equals(beforeSql))
			{
				if(beforeSql.toUpperCase().startsWith(" AND")||beforeSql.toUpperCase().startsWith("AND"))
				{
		    		sql.append(" and ("+beforeSql.substring(4).toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
				}else{
					sql.append(" and ("+beforeSql.toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
				}
			}
			sql.append("#");
			if(order_by==null|| "".equals(order_by)) {
                sql.append(" order by  s.dbid,s.a0000, s.A00Z0, s.A00Z1");
            } else {
                sql.append(" order by "+order_by);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	/**
	 * 删除人员的方法(可多删)
	 * @param a0100a
	 * @param id
	 */
   public void deletePersonFromEmail_content(String a0100s,String id,String username)
   {
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
		   String[] temp_arr=a0100s.split("`");
		   for(int i=0;i<temp_arr.length;i++)
		   {
			   if(temp_arr[i]==null|| "".equals(temp_arr[i])) {
                   continue;
               }
			   String nbasea0100=temp_arr[i].substring(0,temp_arr[i].indexOf("~"));
			   String i9999=temp_arr[i].substring(temp_arr[i].indexOf("~")+1);
			   if(i9999==null|| "".equals(i9999)) {
                   continue;
               }
			  String sql="delete from email_content where a0100 ='"+nbasea0100.substring(3)+"' and id="+id
			   +" and pre='"+nbasea0100.substring(0,3)+"' and I9999="+i9999;
			   dao.delete(sql,new ArrayList());
		   }	
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
   /**
    * 根据条件查找人员
    * @param column 加条件的列
    * @param value 条件值
    * @param templateId 模板号
    * @param code 单位/部门代码过滤
    * @return
    */
  public String getSearchPersonByCondSql(String column,String value,String templateId,String codeSql,String tableName,String timesql,String priv_mode,String privSql,String queryName,String order_by,String beforeSql)
  {
	  StringBuffer sql = new StringBuffer();
	  try
	  {
		  //trim(I9999)
		  if(Sql_switcher.searchDbServer() == Constant.ORACEL)
		  {
			    sql.append("select s.a0100,s.nbase,s.nbase||s.a0100||'~'||trim(I9999) as personid,s.b0110,s.e0122,s.a0101,a.id,a.send_ok,a.subject,I9999,address");
		  }
		  //ltrim(str(I9999))
		  else
		  {
	    	    sql.append("select s.a0100,s.nbase,s.nbase+s.a0100+'~'+ltrim(str(I9999)) as personid,s.b0110,s.e0122,s.a0101,a.id,a.send_ok,a.subject,I9999,address");
		  }
		    sql.append("#");
			sql.append(" from "+tableName);
			sql.append(" s ");
			if("3".equals(value)) {
                sql.append(" left join ");
            } else {
                sql.append(" , ");
            }
			sql.append(" (select e.id,e.a0100,e.pre,e.send_ok,e.subject,e.I9999,case when address is null then '' else address end as address from email_content e where e.id=");
			sql.append(templateId);
			if(!(timesql==null|| "".equals(timesql)))
			{
				sql.append(" and ");
			    sql.append(timesql);
			}
			if(!"3".equals(value)) {
                sql.append(" and e."+column+"='"+value+"' ");
            }
			sql.append(") a ");
			
			if("3".equals(value))
			{
				sql.append(" on s.a0100=a.a0100 and a.pre=s.nbase ");
				sql.append(" where 1=1 ");
			}
			else
			{
				sql.append(" where 1=1 and s.a0100=a.a0100 and a.pre=s.nbase  ");
			}
		
			if(!(codeSql==null|| "".equals(codeSql)))
			  {
				  sql.append(" and ");
				  sql.append(codeSql);
			  }
			if("1".equals(priv_mode))
			{
				if(privSql!=null&&privSql.trim().length()>0)
				{
					sql.append(" and "+privSql);
				}
			}
			if(queryName!=null&&!"".equals(queryName))
			{
				sql.append(" and s.a0101 like '"+queryName+"%'");
			}
			sql.append("  and a00z1=(select max(a00z1) from "+tableName+" X where X.a0100=s.a0100 and X.nbase=s.nbase and X.a00z0=s.a00z0) ");
			if(beforeSql!=null&&!"".equals(beforeSql))
			{
				if(beforeSql.toUpperCase().startsWith(" AND")||beforeSql.toUpperCase().startsWith("AND")) {
                    sql.append(" and ("+beforeSql.substring(4).toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
                } else {
                    sql.append(" and ("+beforeSql.toUpperCase().replaceAll(tableName.toUpperCase(), "s")+")");
                }
			}
			sql.append("#");
			if(order_by==null|| "".equals(order_by)) {
                sql.append(" order by  s.dbid,s.a0000, s.A00Z0, s.A00Z1");
            } else {
                sql.append(" order by "+order_by);
            }
		  //----------
		 //String codeSql = this.getCodeSql("s",code);
		 /* buf.append("select e.a0100,s.nbase,s.nbase+s.a0100 as personid,s.b0110,s.e0122,s.a0101,e.id,e.send_ok,e.subject ");
		  buf.append("#");
		  buf.append(" from email_content e,");
		  buf.append(tableName+" s where ");
		  buf.append(" e.id=");
		  buf.append(templateId);
		  if(!(timesql==null||timesql.equals("")))
		  {
			  buf.append(" and ");
			  buf.append(timesql);
		  }
		  if(!value.equals("3"))
		         buf.append(" and e."+column+"='"+value+"' ");
		  buf.append(" and e.a0100=s.a0100 and e.pre=s.nbase ");
		  if(!(codeSql==null||codeSql.equals("")))
		  {
			  buf.append(" and ");
			  buf.append(codeSql);
		  }
		  
		  buf.append("#");
		  buf.append(" order by e.id");*/
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return sql.toString();
  }
  public String getSql(String selectedid,String tablename)
  {
	  String sqlstr="";
	
	  try
	  {
		  StringBuffer s=new StringBuffer();
		  StringBuffer sql = new StringBuffer("");
		  String[] arr=selectedid.split(",");
		  String column="";
		  if("e".equalsIgnoreCase(tablename)) {
              column="pre";
          }
		  if("s".equalsIgnoreCase(tablename)|| "a".equalsIgnoreCase(tablename)) {
              column="nbase";
          }
		  for(int i=0;i<arr.length;i++)
		  {
			  if(arr[i]==null|| "".equals(arr[i])) {
                  continue;
              }
			  String[] str=arr[i].split("~");
			  sql.append(" or  (UPPER("+tablename+"."+column+")='"+str[0].substring(0, 3).toUpperCase()+"' and "+tablename+".a0100='"+str[0].substring(3)+"')");  
		  }
		 
		  if(sql.toString().length()>0)
		  {
			  sqlstr=sql.toString().substring(4);
			  s.append("(");
			  s.append(sqlstr);
			  s.append(")");
			  sqlstr=s.toString();
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return sqlstr;
  }
  /**
   * 导入数据前，删除满足条件的数据
   * @param templateId
   * @param username
   * @param code
   */
  public void deleteOldData(String templateId,String username,String code,String tableName,String type,String selectedid)
  {
	  try
	  {
		  StringBuffer sql = new StringBuffer();
		  ContentDAO dao = new ContentDAO(this.conn);
		  if("1".equals(type)&&selectedid!=null&&selectedid.length()>0)
		  {
			  String[] arr=selectedid.split(",");
			  ArrayList list = new ArrayList();
			  for(int i=0;i<arr.length;i++)
			  {
				  if(arr[i]==null|| "".equals(arr[i])) {
                      continue;
                  }
				  String[] str=arr[i].split("~");
				  sql.append(" delete from email_content where UPPER(pre)='"+str[0].substring(0, 3)+"' and a0100='"+str[0].substring(3)+"'");
				  list.add(sql.toString());
				  sql.setLength(0);
			  }
			  dao.batchUpdate(list);
		  }
		  else
		  {
	    	  sql.append("select a0100 from ");
	    	  sql.append(tableName);
		      sql.append(" s ");
	    	  String codeSql=this.getCodeSql("s",code);
		      if(!(codeSql==null|| "".equals(codeSql)))
		      {
	    		  sql.append(" where ");
	    		  sql.append(codeSql);
	    	  }
	    	  RowSet rs= null;
	    	  rs=dao.search(sql.toString());
	    	  sql.setLength(0);
		      while(rs.next())
	     	  {
	    		  sql.append(",'");
		    	  sql.append(rs.getString(1));
	    		  sql.append("'");
	    	  }
	    	  StringBuffer sql2= new StringBuffer();
    		  sql2.append("delete from email_content where a0100 in (");
    		  if(sql!=null&&sql.toString().length()>0)
    		  {
	    		  sql2.append(sql.toString().substring(1));
    		  }
    		  else
    		  {
			  sql2.append("''");
	    	  }
	    	  sql2.append(") and id=");
	    	  sql2.append(templateId);
	    	  dao.delete(sql2.toString(),new ArrayList());
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  /**
   * 将手动选择的人员导入emailcontent( no use)
   * @param templateId
   * @param subject
   * @param tableName
   * @param prea0100
   * @param email_field
   * @param userView
   */
  public void exportHandSelectPersons(String templateId,String subject,String prea0100,String email_field,UserView userView)
  {
	  try
	  {
		  updateTableCloumns();
		  StringBuffer a0100= new StringBuffer();
		  String[] temp=prea0100.split(",");
		  for(int i=0;i<temp.length;i++)
		  {
			  if("UN".equalsIgnoreCase(temp[i].substring(0,2))|| "UM".equalsIgnoreCase(temp[i].substring(0,2))|| "@k".equalsIgnoreCase(temp[i].substring(0,2))) {
                  continue;
              }
			  a0100.append(",'");
			  a0100.append(temp[i].substring(3));
			  a0100.append("'");
		  }
	      StringBuffer buf= new StringBuffer();
	      buf.append("insert into email_content(id,username,subject,send_ok,pre,a0000,a0100,b0110,e01a1)");
		  buf.append(" select "+templateId+" as id,'");
		  buf.append(userView.getUserName());
		  buf.append("' as username,'"+subject+"' as subject,0 as send_ok,temp.* from (");
	      buf.append(" select 'usr' as pre,u.a0000,u.a0100,u.b0110,u.e01a1 from ");
	      buf.append("usra01 u where u.a0100 in(");
	      buf.append((a0100==null||a0100.toString().trim().length()<=0)?"''":a0100.toString().substring(1));
	      buf.append(") and u.a0100 not in(select a0100 from email_content where id=");
	      buf.append(templateId+" and username='"+userView.getUserName()+"')");
	    	   
	      buf.append(" union ");
	      buf.append(" select 'trs' as pre,t.a0000,t.a0100,t.b0110,t.e01a1 from ");
	      buf.append("trsa01 t where t.a0100 in(");
	      buf.append((a0100==null||a0100.toString().trim().length()<=0)?"''":a0100.toString().substring(1));
	      buf.append(") and t.a0100 not in(select a0100 from email_content where id=");
	      buf.append(templateId+" and username='"+userView.getUserName()+"')");
		    
		  buf.append(" union ");
		  buf.append(" select 'ret' as pre,r.a0000,r.a0100,r.b0110,r.e01a1 from ");
	      buf.append("reta01 r where r.a0100 in(");
	      buf.append((a0100==null||a0100.toString().trim().length()<=0)?"''":a0100.toString().substring(1));
	      buf.append(") and r.a0100 not in(select a0100 from email_content where id=");
	      buf.append(templateId+" and username='"+userView.getUserName()+"')");
	    		
	      buf.append(" union ");
	      buf.append(" select 'oth' as pre,o.a0000,o.a0100,o.b0110,o.e01a1 from ");
	      buf.append("otha01 o where o.a0100 in(");
	      buf.append((a0100==null||a0100.toString().trim().length()<=0)?"''":a0100.toString().substring(1));
	      buf.append(") and o.a0100 not in(select a0100 from email_content where id=");
	      buf.append(templateId+" and username='"+userView.getUserName()+"')");
	      buf.append(") temp");
	      ContentDAO dao = new ContentDAO(this.conn);
	      dao.insert(buf.toString(),new ArrayList());
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  /**
	 * 权限范围内的人员库列表(手动查人用，现在没用了)
	 * @param dbpreList
	 */
  public String getPrivPre(ArrayList dbpreList) throws GeneralException
  {
  	StringBuffer preSql=new StringBuffer();
  	try
  	{
  		if(dbpreList.size()==0)
  		{
  			throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
  		}
  		for(int i=0;i<dbpreList.size();i++)
  		{
  			preSql.append(",");
  			preSql.append((String)dbpreList.get(i));
  		}
  		
  	}
  	catch(Exception e)
  	{
  		e.printStackTrace();
  		throw GeneralExceptionHandler.Handle(e);	
  	}
  	if(preSql.toString().trim().length()>0) {
        return preSql.toString().substring(1);
    } else {
        return "";
    }
  }
  /**
   * 当将选择的人员加入当前模板时，先删除选择人员的信息，在导入
   *@param selectids
   *@param userview
   *@param templateId
   */
  public void deleteSelectPersons(String selectids,UserView userview,String templateId)
  {
	  try
	  {
		 String[] temp=selectids.split("`"); 
		 ContentDAO dao =new ContentDAO(this.conn);
		 StringBuffer sql = new StringBuffer();
		 for(int i=0;i<temp.length;i++)
		 {
			sql.append(" delete from email_content where id=");
			sql.append(templateId);
			sql.append(" and pre='");
			sql.append(temp[i].substring(0,3));
			sql.append("' and a0100='");
			sql.append(temp[i].substring(3)+"'");
			/*sql.append("' and username='");
			sql.append(userview.getUserName());
			sql.append("'");*/
			dao.delete(sql.toString(),new ArrayList());
			sql.setLength(0);
		 }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	 
  }
  /**
   * 将选择的人员导入到email_content
   * @param templateId
   * @param subject
   * @param prea0100
   * @param email_field
   * @param userView
   */
  public void exportSelectPersons(String templateId,String subject,String prea0100,String email_field,UserView userView)
  {
	  try
	  {
		  updateTableCloumns();
		  StringBuffer buf= new StringBuffer();
		  String[] temp = prea0100.split("`");
		  ContentDAO dao = new ContentDAO(this.conn);
		  for(int i=0;i<temp.length;i++)
		  {
    		  buf.append("insert into email_content(id,username,subject,send_ok,pre,a0000,a0100,b0110,e01a1)");
    		  buf.append(" select "+templateId+" as id,'");
    		  buf.append(userView.getUserName());
    		  buf.append("' as username,'"+subject+"' as subject,0 as send_ok,temp.* from (");
    		  buf.append(" select '");
    		  buf.append(temp[i].substring(0,3)+"' as pre,u.a0000,u.a0100,u.b0110,u.e01a1 from ");
    		  buf.append(temp[i].substring(0,3)+"a01 u where u.a0100='");
    		  buf.append(temp[i].substring(3)+"') temp");
    		  dao.insert(buf.toString(),new ArrayList());
    		  buf.setLength(0);
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  
  }
  /**
   * 更新选择人员的email地址
   */
  public void updateSelectPersonsAddress(String selectid,String emailField,String emailFieldSet,String templateId,UserView userview)
  {
	  try
	  {
		  StringBuffer buf = new StringBuffer();
		  String[] temp = selectid.split("`");
		  ContentDAO dao = new ContentDAO(this.conn);
		  for(int i=0;i<temp.length;i++)
		  {
    		  buf.append("update email_content set address=(select ");
    		  buf.append(emailField);
    		  buf.append(" as address from ");
    		  buf.append(temp[i].substring(0,3)+emailFieldSet);
    		  buf.append(" a where a.a0100='");
    		  buf.append(temp[i].substring(3));
    		  buf.append("') where pre='");
    		  buf.append(temp[i].substring(0,3));
    		  buf.append("' and id=");
    		  buf.append(templateId);
    		  buf.append(" and a0100='");
    		  buf.append(temp[i].substring(3));
    		/*  buf.append("' and username='");
    		  buf.append(userview.getUserName());*/
    		  buf.append("'");
    		  dao.update(buf.toString());
    		  buf.setLength(0);
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
 /**
  * 设置邮件发送成功或失败的标志位
  * @param templateId
  * @param a0100
  * @param pre
  * @param username
  */
  public void configSend_ok(int flag,String templateId,String a0100,String pre,String username,String I9999)
  {
	  try
	  {
		  updateTableCloumns();
		  StringBuffer buf= new StringBuffer();
		  buf.append("update email_content set send_ok=");
		  buf.append(flag);
		  buf.append(" where id=");
		  buf.append(templateId);
		  buf.append(" and a0100='");
		  buf.append(a0100+"' and pre='");
		  buf.append(pre);
		 /* buf.append("' and username='");
		  buf.append(username);*/
		  buf.append("' and I9999=");
		  buf.append(I9999);
		  ContentDAO dao = new ContentDAO(this.conn);
		  dao.update(buf.toString());
		  
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  /**
	 * 删除模板
	 * @param id
	 * @param tableName
	 */
	public void deleteTemplate(String id,String tableName)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("delete from ");
			sql.append(tableName);
			sql.append(" where id in (");
			sql.append(id);
			sql.append(")");
			ContentDAO dao= new ContentDAO(this.conn);
			dao.delete(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得所有薪资发放的邮件模板
	 * @return ArrayList
	 */
	public ArrayList getEmailTemplateList(int type)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule=2 order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
			if(type==1)
			{
	    		if(list.size()==0)
	    		{
	    			list.add(new CommonData("","      "));
	    		}
	    		list.add(new CommonData("0",ResourceFactory.getProperty("label.gz.new")));
	    	}
			else
			{
				if(list.size()==1)
				{
		    		list.add(0,new CommonData("","      "));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getEmailTemplateList2(String nmodule,int type)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule="+nmodule+" order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
			if(type==1)
			{
	    		if(list.size()==0)
	    		{
	    			list.add(new CommonData("","      "));
	    		}
	    		list.add(new CommonData("0",ResourceFactory.getProperty("label.gz.new")));
	    	}
			else
			{
				if(list.size()==1)
				{
		    		list.add(0,new CommonData("","      "));
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
	 * 首次进入邮件发送页面,默认为id最小的模板
	 * @return
	 */
	public int getMinTemplateId(String nmodule)
	{
		int n=0;
		try
		{
			String sql = "select min(id) from email_name where nmodule="+nmodule;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt(1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	
	}
	/**
	 * 动态创建邮件发送标志列,并设置初始值
	 *
	 */
	public void updateTableCloumns()
	{
		try
		{
			String tablename="email_content";
			RecordVo vo = new RecordVo(tablename);
			Table table = new Table(tablename);
			int num=0;
			if(!vo.hasAttribute("send_ok"))//发送标识
			{
				Field obj=new Field("send_ok","send_ok");
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");				
				table.addField(obj);
				num++;
			}
			if(num>0)
			{
				DbWizard dbWizard=new DbWizard(this.conn);
				dbWizard.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tablename);
				/**新建send-ok列.将值赋为0*/
				String sql="update email_content set send_ok=0";
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(sql);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getMonthList()
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("0","全部"));
		try
		{
			for(int i=1;i<13;i++)
			{
				list.add(new CommonData(String.valueOf(i),String.valueOf(i)+"月份"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取邮件记录中设置到的年份
	 * @return
	 */
	public ArrayList getYearList()
	{
		ArrayList list = new ArrayList();
//		list.add(new CommonData("0","全部"));
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String strSql="";
			if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
                strSql="select distinct to_char(SEND_TIME,'YYYY') as y from email_content where SEND_TIME is not null ORDER BY y desc";
            } else {
                strSql="select distinct convert(varchar(4),SEND_TIME,20) as y from email_content where SEND_TIME is not null ORDER BY y  desc";
            }
			RowSet rs=dao.search(strSql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("y"),rs.getString("y")));
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 拼接时间sql
	 * @param year
	 * @param month
	 * @return
	 */
	public String queryRecordByTime(String year,String month)
	{
		StringBuffer buf= new StringBuffer("");
		try
		{
			if(StringUtils.isNotBlank(year)){
				buf.append(Sql_switcher.year("send_time"));
				buf.append("=");
				buf.append(year);
			}
			
			
			if(StringUtils.isNotBlank(month)&&!"0".equals(month)){
				if(buf.length()>0) {
                    buf.append(" and ");
                }
				buf.append(Sql_switcher.month("send_time"));
				buf.append("=");
				buf.append(month);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public void warnExportInfomation(String wid,ArrayList personList,String templateid,UserView userView,boolean issend)
	{
		try
		{
			if(personList==null||personList.size()==0) {
                return;
            }
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String time=format.format(new Date());
	    	this.setInserttime(time);
	    	StringBuffer buf = new StringBuffer();
	    	 ContentDAO dao = new ContentDAO(this.conn);
	    	String emailfield = getEmailField(templateid);
	    	String emailfieldset =getEmailFieldSetId(emailfield);
	    	/**得模板标题*/
    		String subject=getEmailTemplateSubject(templateid);
    		for(int i=0;i<personList.size();i++)
    		{
    			String str=(String)personList.get(i);
			    buf.append("insert into email_content(");
      		    buf.append("wid,id,username,subject,send_ok,pre,a0000,a0100,b0110,e01a1,send_time,I9999)");
	    	    buf.append(" select "+wid+" as wid,"+templateid+" as id,'");
	    	    buf.append(userView.getUserName());
	            buf.append("' as username,'"+subject+"' as subject,0 as send_ok,temp.*,");
	            if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    buf.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS') ");
                } else {
                    buf.append("'"+time+"' ");
                }
	            buf.append("as send_time,0 as I9999 from (");
	        	buf.append(" select '"); 
            	buf.append(str.substring(0, 3)+"' as pre,a0000,a0100,b0110,e01a1 from ");
            	buf.append(str.substring(0, 3)+"a01 where a0100 ='"+str.substring(3)+"'");
            	buf.append(") temp");
            	dao.update(buf.toString());
            	buf.setLength(0);
	        }
    		configI9999(wid,templateid,time,dao);
    		//ArrayList preList = this.getNbaseList();
    		StringBuffer sql = new StringBuffer();
    		
    		for(int i=0;i<personList.size();i++)
    		{
    			sql.append(" update email_content set address=");
        		sql.append("(select ");
        		sql.append(emailfield);
        		sql.append(" as address from (");
    			String pre = (String)personList.get(i);
        		sql.append(" select u.a0100,");
        		sql.append("u."+emailfield);
    	    	sql.append(" from ");
    	    	sql.append(pre.substring(0,3)+emailfieldset);
    	    	sql.append(" u,email_content e where e.a0100=u.a0100 and e.a0100='"+pre.substring(3)+"' and e.pre='"+pre.substring(0,3)+"' and e.id="+templateid+" and e.wid='"+wid+"'");
    	    	sql.append(" and e.I9999=(select I9999 from(select max(i9999) as I9999 from");
    	    	sql.append(" email_content where email_content.a0100='"+pre.substring(3)+"' and ");
    	    	sql.append("email_content.pre='"+pre.substring(0,3)+"' and email_content.wid='"+wid+"' and ");
    	    	sql.append("email_content.id="+templateid+" and ");
    	    	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql.append("to_char(email_content.send_time,'YYYY-MM-DD HH24:MI:SS')");
                } else {
                    sql.append(" email_content.send_time");
                }
    	    	sql.append("='"+time+"') temp )) t ) where ");
    	    	
    	    	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql.append("to_char(email_content.send_time,'YYYY-MM-DD HH24:MI:SS')");
                } else {
                    sql.append(" email_content.send_time");
                }
    	    	sql.append("='"+time+"' and email_content.id="+templateid+" and email_content.a0100='"+pre.substring(3)+"' and email_content.pre = '"+pre.substring(0,3)+"'");
    	    	//System.out.println(sql.toString());
    	    	dao.update(sql.toString());
    	    	sql.setLength(0);
    		}
    		
    		ArrayList list=getTemplateFieldInfo(Integer.parseInt(templateid),2);
	        String content=getEmailContent(Integer.parseInt(templateid));
	        updateEmailContent(wid,list,templateid,personList,content,userView,2,"","");
	        if(issend) {
                sendWarnMessage(wid,personList,templateid,userView);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList sendWarnMessage(String wid,ArrayList personList,String templateid,UserView userView)
	{
		ArrayList list = new ArrayList();
		try
		{
			  StringBuffer sql = new StringBuffer();
			  RowSet rs = null;
			  ContentDAO dao = new ContentDAO(this.conn);
			  for(int i=0;i<personList.size();i++)
			  {
				  String pre = (String)personList.get(i);
	    		  sql.append("select e.id,e.pre,e.subject,e.address,e.content,e.a0100,e.username,e.I9999 from email_content e where");
	    		  sql.append(" e.id="+templateid);
	    		  sql.append(" and e.a0100='");
	    		  sql.append(pre.substring(3));
	    		  sql.append("' and e.pre='");
		    	  sql.append(pre.substring(0,3));
		    	  sql.append("' and e.username='");
		     	  sql.append(userView.getUserName());
	    		  sql.append("' and e.I9999=");
	              sql.append("(select I9999 from(select max(i9999) as I9999 from email_content where email_content.a0100='"+pre.substring(3)+"' and email_content.pre='"+pre.substring(0,3)+"' and email_content.wid='"+wid+"' and email_content.id="+templateid+") temp) and e.id="+templateid);
	              rs= dao.search(sql.toString());
	        	  while(rs.next())
	        	  {
   		        	  LazyDynaBean bean = new LazyDynaBean();
	         		  bean.set("subject",rs.getString("subject"));
	         		  bean.set("address",rs.getString("address")==null?"":rs.getString("address"));
	         		  bean.set("content",rs.getString("content")==null?"":rs.getString("content"));
	         		  bean.set("a0100",rs.getString("a0100"));
	         		  bean.set("pre",rs.getString("pre"));
	    	     	  bean.set("id",rs.getString("id"));
	        		  bean.set("username",rs.getString("username"));
	    	    	  bean.set("I9999",rs.getString("I9999"));
	    	    	  bean.set("a0101","");
		        	  list.add(bean);
	          	  }
	        	  sql.setLength(0);
			  }
			ArrayList attachList=getAttachFileName(templateid);
			String fromAddress=getFromAddr();
			if(fromAddress==null|| "".equals(fromAddress)) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("errors.stmp_server.notdefine")));
            }
			sendEmail(1,list,templateid,attachList,fromAddress,userView,1);
	    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 清空邮件模板主体内容，（包括删除指标和公式的定义）
	 * @param id
	 */
	public void clearTemplateBody(String id)
	{
		try
		{
            if(id==null|| "".equals(id)) {
                return;
            }
			String sql = "delete from email_field where id="+id;
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getConstantList()
	{
		ArrayList list = new ArrayList();
		list.add(new CommonData("1",ResourceFactory.getProperty("label.mail.username")));
		list.add(new CommonData("2",ResourceFactory.getProperty("label.query.day")));
		list.add(new CommonData("3",ResourceFactory.getProperty("report.parse.t")));
		list.add(new CommonData("4",ResourceFactory.getProperty("hrms.b0110")));
		list.add(new CommonData("5",ResourceFactory.getProperty("hrms.e0122")));
		list.add(new CommonData("6",ResourceFactory.getProperty("label.gz.name")));
		list.add(new CommonData("7",ResourceFactory.getProperty("label.gz.autologonaddress")));
		return list;
	}
	public HashMap getFormulaInfo(String formulaID,String tid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select fieldtitle,fieldtype,ndec,fieldcontent,dateformat,fieldlen from email_field where fieldid='"+formulaID+"' and id="+tid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put("title",rs.getString("fieldtitle")==null?"":rs.getString("fieldtitle"));
				map.put("content",Sql_switcher.readMemo(rs,"fieldcontent"));
				map.put("type",rs.getString("fieldtype").trim());
				map.put("ndec",rs.getString("ndec")==null?"":rs.getString("ndec"));
				map.put("format",rs.getString("dateformat"));
				map.put("len",rs.getString("fieldlen"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * xus vfs 文件上传
	 * @param file
	 * @param attach_id
	 * @param templateId
	 * @param username 
	 * @return
	 */
	public String updFileToVfs(FormFile file, String username) {
		String fileid = "";
		InputStream is = null;
		try {
			//xus 20/4/28 vfs 改造
			//用户名
			//文件类型
			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
			//所属模块
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
			//文件所属类型
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
			//guidkey
			String guidkey = "";
			//文件流
			is = file.getInputStream();
			//文件名
			String filename = file.getFileName();
			
			fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, guidkey, is, filename, "", false);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(is);
		}
		return fileid;
	}

}