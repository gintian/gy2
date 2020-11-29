package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:CreateCodeXml
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 16, 2005:5:08:57 PM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateCodeXml
{
    /** 代码类 */
    private String codesetid;

    /** 代码项 */
    private String codeitemid;

    /** 根据管理范围找第一层代码 */
    private String privflag;

    /*
         * 为了显示第一层为管理的最高层的节点
         */
    private String isfirstnode;
    
    private UserView userView;
    //区分招聘渠道
    private String hirechannel;
    
    private String isValidCtr="1"; //是否过滤有效无效。0不控制，1控制 默认控制
    
    private boolean vorg = false;
    
    private static boolean isHavA0000=false;//是否有a0000字段
    
    static{
    	RecordVo vo = new RecordVo("codeitem");
    	if(vo.hasAttribute("a0000"))
    		isHavA0000=true;
    }

    
    private boolean onlyLeafNode = false;
	/**
         * 
         */
    public CreateCodeXml(String codesetid, String codeitemid)
    {
     /**招聘外网扫描出有sql注入漏洞，将特殊字符替换*/
	this.codeitemid = PubFunc.getReplaceStr(codeitemid);
	this.codesetid = PubFunc.getReplaceStr(codesetid);
    }
    /**
     * 
     * @param codesetid  相关代码类
     * @param codeitemid 
     * @param privflag
     */
    public CreateCodeXml(String codesetid, String codeitemid, String privflag)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
    }

    public CreateCodeXml(String codesetid, String codeitemid, String privflag, String isfirstnode)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
	this.isfirstnode = isfirstnode;
    }
    
    public CreateCodeXml(String codesetid, String codeitemid, String privflag ,UserView userView)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
	this.userView = userView;
    }

    public String getHirechannel() {
		return hirechannel;
	}
	public void setHirechannel(String hirechannel) {
		this.hirechannel = hirechannel;
	}
	/** 求查询代码的字符串 */
    private String getQueryString(Connection conn)
    {   
    	//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
    	String codesetidL = "";
		String flag = "";
		if (this.codesetid.indexOf("_") != -1) {
			codesetidL = this.codesetid;
			String[] codesetidLs=codesetidL.split("_");
			if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
				this.codesetid=codesetidLs[0];
				flag=codesetidLs[1];
			}
		}
    	
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String backdate =sdf.format(new Date());
	StringBuffer str = new StringBuffer();
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    if ("UN".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
		str.append(this.codesetid);
		str.append("'");
	    } else if ("UM".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN') ");
	    } else if ("@K".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN' or codesetid='UM') ");
	    }
	    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	} else if ("1_".equals(this.codesetid.substring(0, 2)))
	{
	    String codeset = codesetid.replace("1_", "");
	    String[] rel = relatingcode(codeset);
	    str.append("select '" + codesetid + "' as codesetid,");
	    str.append(rel[1] + " as codeitemid," + rel[2] + " as codeitemdesc,");
	    str.append(rel[1] + " as childid from " + rel[0]);
		
	    if(!userView.isSuper_admin()&&new DbWizard(conn).isExistField(rel[0], "b0110", false)){
	    	str.append(TrainCourseBo.getUnitIdByBusiWhere(userView));
	    }else if("r50".equalsIgnoreCase(rel[0])){//关联在线课程表须加条件
	    	str.append(" where r5022='04'");
	    	if(!userView.isSuper_admin()){
		    	String tmpwhere = TrainCourseBo.getLessonByBusiWhere(userView);
		    	if(tmpwhere.length()<5)
		    		tmpwhere="";
		    	else{
			    	str.append(tmpwhere);
		    	}
	    	}
	    }
	}
	else if("@@".equalsIgnoreCase(this.codesetid))//人员库
	{
	    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname order by dbid");
	    return str.toString();
	}
	else
	{
        int codeFlag = getCodeFlag(this.codesetid);		
	    str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
	    str.append(this.codesetid);
	    str.append("'");
	    if("1".equals(this.isValidCtr)){
		    /**去掉已过期的 lizw 2012-02-29*/
		    if(codeFlag == 1)
		        str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		    else
		        //去掉设置为无效的 gdd 13-7-17 v6x
		        str.append(" and invalid<>0 ");
	    }
	}
	/** 所有的第一层代码值列表 */
	if (!"1_".equals(this.codesetid.substring(0, 2)))
	{
	    if (privflag == null || "".equals(privflag))
	    {
			if (this.isfirstnode != null && "1".equals(this.isfirstnode))
			{
			    if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
			    {
				str.append(" and parentid=codeitemid");
			    } else
			    {
				str.append(" and codeitemid='");
				str.append(codeitemid);
				str.append("'");
			    }
			} else {
				if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid)) {
					if (!("55".equalsIgnoreCase(this.codesetid) && "2".equalsIgnoreCase(flag))) {
						str.append(" and parentid=codeitemid");
						}
				} else {
				    if(codeitemid.indexOf("`")==-1) {
				        str.append(" and parentid<>codeitemid and ");
				        str.append(" parentid='");
				        str.append(codeitemid);
				        str.append("'");
				    } else {
                        StringBuffer tempSql=new StringBuffer("");
                        String[] temp=codeitemid.split("`");
                        for(int i=0;i<temp.length;i++) {
                            if(temp.length==1){
                                if("UN".equalsIgnoreCase(temp[i])) {
                                    tempSql.append(" or codeitemid=parentid");
                                } else {
                                    tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
                                }
                            } else {
                                tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
                            }
                        }
                        str.append(" and ( "+tempSql.substring(3)+" ) ");
				    }
				}
			}
	    }
	    else
	    // 根据管理范围过滤相应的节点内容
	    {
			if ("ALL".equals(this.codeitemid))
			{
			    str.append(" and parentid=codeitemid");
			} 
			else if (this.codeitemid == null || "".equals(this.codeitemid))
			{
			    str.append(" and 1=2");
			} 
			else
			{
				/***
				 * cmq changed at 20121001 for 
				 * 花名册中的手工选择的组织机构权限控制问题。
				 */
				String[] valuearr=StringUtils.split(codeitemid,"`");
				if(valuearr.length==1)
				{
					str.append(" and codeitemid in ('");
					str.append(codeitemid);
					str.append("')");
				}
				else
				{
					str.append(" and ");
					str.append(getMultiUnitQueryString("codeitemid",valuearr));
				}
			}
	    }
	}
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    str.append(" ORDER BY a0000,codeitemid ");
	}else if(!"@@".equalsIgnoreCase(this.codesetid))
	{
		if ("55".equalsIgnoreCase(this.codesetid)) {//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
			if ("1".equalsIgnoreCase(flag))
				str.append(" and not exists(select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
			else if ("2".equalsIgnoreCase(flag)){
				str.append(" and exists(select 1 from r50 where R5022='04'");
				if(!this.userView.isSuper_admin()) {
					String where = TrainCourseBo.getLessonByBusiWhere(this.userView);
					str.append(where);
				}
				backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
				str.append(" and "+Sql_switcher.year("R5030")+"*10000+"+Sql_switcher.month("R5030")+"*100+"+Sql_switcher.day("R5030")+"<="+backdate);
				str.append(" and "+Sql_switcher.year("R5031")+"*10000+"+Sql_switcher.month("R5031")+"*100+"+Sql_switcher.day("R5031")+">="+backdate);
				str.append(" and r50.codeitemid=codeitem.codeitemid)");
			}
			if("55_1".equalsIgnoreCase(codesetidL)|| "55_2".equalsIgnoreCase(codesetidL))
				this.codesetid = codesetidL;
		}
		if (!"1_".equals(this.codesetid.substring(0, 2)))
		{
			if(isHavA0000)
				str.append(" ORDER BY a0000,codeitemid ");
			else
				str.append(" ORDER BY codeitemid ");
		}else
		 str.append(" ORDER BY codeitemid ");
	}
	if (this.hirechannel!=null && this.hirechannel.trim().length()>0 
		&& ("UN".equalsIgnoreCase(this.codesetid)
			|| "UM".equalsIgnoreCase(this.codesetid)
			|| "@K".equalsIgnoreCase(this.codesetid)
			)
	   ){
		str = new StringBuffer("");
		str.append(" select distinct a0000,codesetid,codeitemid,codeitemdesc,null as childid from organization,z03 where Z0319='04' ");
		if(!"out".equalsIgnoreCase(this.hirechannel)&&!"headHire".equals(this.hirechannel)){//招聘外网增加猎头招聘,不需要查询招聘渠道的功能
			str.append(" and z0336='"+this.hirechannel+"'");
		}else{
			str.append("and  z03.z0336<>'03' ");
		}
		if("headHire".equals(this.hirechannel)){//如果是外网招聘（猎头）查询代码相关,要判断是否是猎头招聘
			str.append(" and z03.Z0373 ='1' ");
		}
		backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		str.append(" and "+Sql_switcher.year("z0329")+"*10000+"+Sql_switcher.month("z0329")+"*100+"+Sql_switcher.day("z0329")+"<="+backdate);
		str.append(" and "+Sql_switcher.year("z0331")+"*10000+"+Sql_switcher.month("z0331")+"*100+"+Sql_switcher.day("z0331")+">="+backdate);
		str.append(" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+backdate);
		str.append(" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+backdate);
		str.append(" and codesetid='"+this.codesetid+"'");
		if("UN".equalsIgnoreCase(this.codesetid)){
			str.append(" and z0321 is not null and codeitemid = z0321 ");
		}else if("UM".equalsIgnoreCase(this.codesetid)){
			str.append(" and z0325 is not null and codeitemid = z0325 ");
		}else if("@K".equalsIgnoreCase(this.codesetid)){
			str.append(" and z0311 is not null and codeitemid = z0311 ");
		}
        str.append(" order by a0000,codeitemid  ");
		this.codesetid = "XXXXXXXXXX";
	}
	return str.toString();
    }
    
    
    
    private int getCodeFlag(String codesetid){
    	int codeflag=-1;
    	String sql = " select validateflag from codeset where codesetid='"+codesetid+"'";
    	Connection conn = null;
    	ResultSet rs = null;
    	try{
    		conn = AdminDb.getConnection();
    		ContentDAO dao = new ContentDAO(conn);
    		rs = dao.search(sql);
    		if(rs.next())
    			codeflag = rs.getInt("validateflag");
    	}catch(SQLException e){
    	    e.printStackTrace();
    	} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
    		try{
    			if(conn !=null)
    				conn.close();
    			
    			if(rs != null)
    				rs.close();
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    		    
    	}
    	return codeflag;
    }
    
    public static String getCodeSetDesc(String codesetid){
    	String codesetdesc = "";
    	String sql = " select codesetdesc from codeset where codesetid='"+codesetid+"'";
    	Connection conn = null;
  
    	ResultSet rs = null;
    	try{
    		conn = AdminDb.getConnection();
    		ContentDAO dao = new ContentDAO(conn);
    		rs = dao.search(sql);
    		if(rs.next())
    			codesetdesc = rs.getString("codesetdesc");
    	}catch(SQLException e){
    	    e.printStackTrace();
    	} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
    		try{
    			if(conn !=null)
    				conn.close();
    			if(rs != null)
    				rs.close();
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    		    
    	}
    	return codesetdesc;
    }
    
    /**
     * (field in ('xxx','yyyy'));
     * @param field
     * @param values
     * @return
     */
    private String getMultiUnitQueryString(String field,String[] values)
    {
   	 StringBuffer buf=new StringBuffer();
   	 buf.append("(");
   	 buf.append(field);
	 buf.append(" in (");
   	 for(int i=0;i<values.length;i++)
   	 {
   		 if(i!=0)
   			 buf.append(",");
   		 buf.append(" '");
   		 buf.append(values[i]);
   		 buf.append("' ");		 
   	 }
   	 buf.append("))");
   	 return buf.toString();
    }    
    /** 
     * 求查询代码的字符串 增加管理范围筛选
     */
    private String getQueryStringsx(UserView userView)
    {

    	String codesetidL = "";
		String flag = "";
		if (this.codesetid.indexOf("_") != -1) {
			codesetidL = this.codesetid;
			String[] codesetidLs=codesetidL.split("_");
			if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
				this.codesetid=codesetidLs[0];
				flag=codesetidLs[1];
			}
		}
		
	StringBuffer str = new StringBuffer();
	String acodeid=userView.getManagePrivCode();
    String acodevalue=userView.getManagePrivCodeValue();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String backdate =sdf.format(new Date());
    String kqpriv = userView.getKqManageValue();
    if(kqpriv.length()>0 && kqpriv !=null)
    {
    	acodeid=kqpriv.substring(0,2);
        acodevalue=kqpriv.substring(2);
    }
    if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    if ("UN".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
		str.append(this.codesetid);
		str.append("'");
	    } else if ("UM".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN') ");
	    } else if ("@K".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN' or codesetid='UM') ");
	    }
	    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	} else if ("1_".equals(this.codesetid.substring(0, 2)))
	{
	    String codeset = codesetid.replace("1_", "");
	    String[] rel = relatingcode(codeset);
	    str.append("select '" + codesetid + "' as codesetid,");
	    str.append(rel[1] + " as codeitemid," + rel[2] + " as codeitemdesc,");
	    str.append(rel[1] + " as childid from " + rel[0]);
	}
	else if("@@".equalsIgnoreCase(this.codesetid))//人员库
	{
	    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname order by dbid");
	    return str.toString();
	}
	else
	{
	    str.append("select codesetid,codeitemid,codeitemdesc,childid,b0110 from codeitem where codesetid='");
	    str.append(this.codesetid);
	    str.append("'");
	}
	/** 所有的第一层代码值列表 */
	if (!"1_".equals(this.codesetid.substring(0, 2)))
	{
	    if (privflag == null || "".equals(privflag))
	    {
		if (this.isfirstnode != null && "1".equals(this.isfirstnode))
		{
		    if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
		    {
			str.append(" and parentid=codeitemid");
		    } else
		    {
			str.append(" and codeitemid='");
			str.append(codeitemid);
			str.append("'");
		    }
		} else
		{
			if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
			{
				if(userView.isSuper_admin())
				{
					 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
					    {
						str.append(" and parentid=codeitemid");
					    } else
					    {
						str.append(" and parentid<>codeitemid and parentid='");
						str.append(codeitemid);
						str.append("'");
					    }
				}else if("UN".equalsIgnoreCase(acodeid)&& "".equalsIgnoreCase(acodevalue))
				{
					 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
					    {
						     if (!("55".equalsIgnoreCase(this.codesetid) && "2".equalsIgnoreCase(flag))) {
								str.append(" and parentid=codeitemid");
							 }
					    } else
					    {
						str.append(" and parentid<>codeitemid and parentid='");
						str.append(codeitemid);
						str.append("'");
					    }
				}else
				{
					if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
				    {
//						 and parentid=codeitemid 
					str.append(" and codeitemid='");
					str.append(acodevalue+"' ");
				    } else
				    {
//				    str.append("and parentid ='"+acodevalue+"'");
					str.append(" and parentid<>codeitemid and parentid='");
					str.append(codeitemid);
					str.append("'");
				    }
				}
			}else
			{
				 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
				    {
					str.append(" and parentid=codeitemid");
				    } else
				    {
					str.append(" and parentid<>codeitemid and parentid='");
					str.append(codeitemid);
					str.append("'");
				    }
			}
			
		   
		}
	    } else
	    // 根据管理范围过滤相应的节点内容
	    {
		if ("ALL".equals(this.codeitemid))
		{
		    str.append(" and parentid=codeitemid");
		} else if (this.codeitemid == null || "".equals(this.codeitemid))
		{
		    str.append(" and 1=2");
		} else
		{
		    str.append(" and codeitemid='");
		    str.append(codeitemid);
		    str.append("'");
		}
	    }
	}
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    str.append(" ORDER BY a0000,codeitemid ");
	} else if(!"@@".equalsIgnoreCase(this.codesetid))
	{
		if ("55".equalsIgnoreCase(this.codesetid)) {//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
			if ("1".equalsIgnoreCase(flag))
				str.append(" and not exists(select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
			else if ("2".equalsIgnoreCase(flag)){
				str.append(" and exists(select 1 from r50 where R5022='04'");
				if(!this.userView.isSuper_admin()) {
					String where = TrainCourseBo.getLessonByBusiWhere(this.userView);
					str.append(where);
				}
				backdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
				str.append(" and "+Sql_switcher.year("R5030")+"*10000+"+Sql_switcher.month("R5030")+"*100+"+Sql_switcher.day("R5030")+"<="+backdate);
				str.append(" and "+Sql_switcher.year("R5031")+"*10000+"+Sql_switcher.month("R5031")+"*100+"+Sql_switcher.day("R5031")+">="+backdate);
				str.append(" and r50.codeitemid=codeitem.codeitemid)");
			}
			if("55_1".equalsIgnoreCase(codesetidL)|| "55_2".equalsIgnoreCase(codesetidL))
				this.codesetid = codesetidL;
		}
		if (!"1_".equals(this.codesetid.substring(0, 2))&&!"@@".equalsIgnoreCase(this.codesetid))
		{
			if(isHavA0000)
				str.append(" ORDER BY a0000,codeitemid ");
			else
				str.append(" ORDER BY codeitemid ");
		}else
		 str.append(" ORDER BY codeitemid ");
	}
	return str.toString();
    }
    /**
     * 当前对象是否有
     * @param priv_str ，用户已授权的管理范围串 ,UN2020,UN30,UM03030,
     * @param func_id
     * @return
     */
    private boolean haveTheOrgID(String priv_str,String org_id)
    {
    	priv_str=","+priv_str+",";
    	if(priv_str.indexOf(","+org_id+",")==-1)
    		return false;
    	else
    		return true;
    }    
    /**
     * 输出json代码树
     * @selectedstr  已选中的单位或部门节点
     * havechecked  解决人员范围授权只能选中单节点，但extTree控件有个弊端，如果未加载的子节点时判断不出来是否已选中  xuj add 2014-11-27
     * @return
     * @throws GeneralException
     */
    public String outJSonCodeTree(String selectedstr,String havechecked) throws GeneralException
    {
    	StringBuffer strsql = new StringBuffer();
    	ResultSet rset = null;
    	Connection conn = AdminDb.getConnection();
        StringBuffer tmp=new StringBuffer();
        StringBuffer buf=new StringBuffer();	   
    	try
    	{
    	    strsql.append(getQueryString(conn));
    	    ContentDAO dao = new ContentDAO(conn);
    	    rset = dao.search(strsql.toString());
        
	        String iconurl="";
    	    while (rset.next())
    	    {
        		String codesetid = rset.getString("codesetid");
        		String itemid = rset.getString("codeitemid");
        		if (itemid == null)
        		    itemid = "";
        		itemid = itemid.trim();        		
    	    	tmp.append("{id:'");
	            tmp.append(codesetid+itemid);
	            tmp.append("',text:'");
	            tmp.append(rset.getString("codeitemdesc"));
	            tmp.append("'");    
	            
    		    if (!itemid.equalsIgnoreCase(rset.getString("childid")))
  		          tmp.append(",leaf:false");
    		    else
  		          tmp.append(",leaf:true"); 
    		    if(!haveTheOrgID(selectedstr,codesetid+itemid)||"1".equals(havechecked))
    		      tmp.append(",checked:false");
    		    else
  		          tmp.append(",checked:true");     		    	
	    		if ("UN".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else if ("UM".equals(codesetid))
	    			iconurl="/images/dept.gif";
	    		else if ("@K".equals(codesetid))
	    			iconurl="/images/pos_l.gif";
	    		else if ("64".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else if ("65".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else
	    			iconurl="/images/table.gif";
	    		tmp.append(",icon:'");
	    		tmp.append(iconurl);
	    		tmp.append("'");   
	    		//tmp.append(",singleClickExpand:true");
   		
		        tmp.append("}");
		        tmp.append(",");
    	    }
	        if(tmp.length()>0)
	        {
	        	tmp.setLength(tmp.length()-1);
	        	buf.append("[");
	        	buf.append(tmp.toString());
	        	buf.append("]");
	        }

    	} catch (SQLException ee)
    	{
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	} finally
    	{
    	    try
    	    {
    		if (rset != null)
    		{
    		    rset.close();
    		}
    		
    		if (conn != null)
    		{
    		    conn.close();
    		}
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
    	return buf.toString();    	
    }
    public Integer getSelectFlag(ContentDAO dao) {
    	RowSet rs = null;
    	Integer leaf_only = 0;
    	try {
    		ArrayList valuelist = new ArrayList();
    		valuelist.add(codesetid);
			String sql =" select leaf_node from codeset where codesetid = ?";
			rs = dao.search(sql,valuelist);
			if(rs.next())
				leaf_only = rs.getInt("leaf_node");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return leaf_only;
	}
    /**
     * 输出xml串
     * @return
     * @throws GeneralException
     */
    public String outCodeTree() throws GeneralException
    {

	StringBuffer xmls = new StringBuffer();
	StringBuffer strsql = new StringBuffer();
	ResultSet rset = null;
	RowSet rs = null;
	Connection conn = AdminDb.getConnection();
	Element root = new Element("TreeNode");

	root.setAttribute("id", "$$00");
	root.setAttribute("text", "root");
	root.setAttribute("title", "codeitem");
	Document myDocument = new Document(root);
	try
	{
	    strsql.append(getQueryString(conn));
	    // strsql.append("select
	    // codesetid,codeitemid,codeitemdesc,childid from codeitem where
	    // codesetid='");
	    //          
	    // strsql.append(this.codesetid);
	    // /**所有的第一层代码值列表*/
	    // if(this.codeitemid==null||this.codeitemid.equals("")||this.codeitemid.equals("ALL"))
	    // {
	    // strsql.append("' and parentid=codeitemid");
	    // }
	    // else
	    // {
	    // strsql.append("' and parentid<>codeitemid and parentid='");
	    // strsql.append(codeitemid);
	    // strsql.append("'");
	    // }

	    // System.out.println("SQL="+strsql.toString());
	    ContentDAO dao = new ContentDAO(conn);
	    Integer  leaf_only = getSelectFlag(dao);
	    rset = dao.search(strsql.toString());
	    while (rset.next())
	    {
	        boolean flag =false;
		Element child = new Element("TreeNode");
		String itemid = rset.getString("codeitemid");
		if (itemid == null)
		    itemid = "";
		itemid = itemid.trim();
		String codesetid = rset.getString("codesetid");
		String codeitemdesc = rset.getString("codeitemdesc");
		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
		child.setAttribute("id", codesetid + itemid);
		child.setAttribute("text", codeitemdesc);
		child.setAttribute("title", itemid + ":" + codeitemdesc);
		
//		if (!itemid.equalsIgnoreCase(rset.getString("childid")))
//		    child.setAttribute("xml", "/system/get_code_tree.jsp?codesetid=" + this.codesetid/* rset.getString("codesetid") */+ "&codeitemid=" + itemid);
		//查询一下，判断是否有孩子节点
		String tempcodeitemid = getTempCodeItemid(codesetid,itemid);
		if(!"".equals(tempcodeitemid)){
			child.setAttribute("xml", "/system/get_code_tree.jsp?codesetid=" + this.codesetid/* rset.getString("codesetid") */+ "&codeitemid=" + itemid);
		}
		
		//leaf_only：只能选择末级代码项    =1 ：是 =0：否
		child.setAttribute("selectable","true");//默认可以选
		if(leaf_only == 1 && !"".equals(tempcodeitemid))//如果是只能选择末级代码项 且 当前节点为非叶子节点
			child.setAttribute("selectable","false");
		
		if("55_1".equalsIgnoreCase(codesetid) || "55".equalsIgnoreCase(codesetid)){
            rs = dao.search("select 1 from r50 where codeitemid='" + rset.getString("codeitemid") + "'");
            if(rs.next())
                flag =true;
        }
		
		if ("UN".equals(codesetid))
		    child.setAttribute("icon", "/images/unit.gif");
		else if ("UM".equals(codesetid))
		    child.setAttribute("icon", "/images/dept.gif");
		else if ("@K".equals(codesetid))
		    child.setAttribute("icon", "/images/pos_l.gif");
		else if ("55".equalsIgnoreCase(codesetid) || "55_1".equalsIgnoreCase(codesetid) || "55_2".equalsIgnoreCase(codesetid)) {
		    if(flag || "55_2".equalsIgnoreCase(codesetid))
                child.setAttribute("icon","/images/icon_wsx.gif");
            else
                child.setAttribute("icon","/images/book.gif");
		} else
		    child.setAttribute("icon", "/images/table.gif");
		root.addContent(child);
	    }

	    XMLOutputter outputter = new XMLOutputter();
	    Format format = Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	    outputter.setFormat(format);
	    xmls.append(outputter.outputString(myDocument));
	    // System.out.println("SQL=" +xmls.toString());
	} catch (SQLException ee)
	{
	    ee.printStackTrace();
	    GeneralExceptionHandler.Handle(ee);
	} finally
	{
	    try
	    {
		if (rset != null)
		{
		    rset.close();
		}
		
		if (conn != null)
		{
		    conn.close();
		}
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }

	}
	return xmls.toString();
    }
	/**
     * 查询下级节点并创建节点对象
     * @param multiple  是否添加多选框
     * @param doChecked 是否选中
     * @param expanded 展开下级节点，
     * @return
     * @throws GeneralException
     */
     //添加设置展开节点参数 changxy
    public ArrayList outCodeJSON(boolean multiple,boolean doChecked,String expanded) throws GeneralException{
    	String JSONStr = "";
    	StringBuffer strsql = new StringBuffer();
    	ResultSet rset = null;
    	Connection conn = AdminDb.getConnection();
    	
    	ArrayList  childrens = new ArrayList();
    	try
    	{
    	    strsql.append(getQueryString(conn));
    	    ContentDAO dao = new ContentDAO(conn);
    	    rset = dao.search(strsql.toString());
    	    while (rset.next())
    	    {
	    	    HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetid = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc.trim());
	    		treeitem.put("codesetid", codesetid);
	    		
	    		
	    		//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,itemid);
	    		if(!"".equals(tempcodeitemid))
	    			treeitem.put("leaf", Boolean.FALSE);
	    		else{
	    			treeitem.put("leaf", Boolean.TRUE);
	    		}
	    		
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/unit.gif");
			else if("UM".equals(codesetid))
					treeitem.put("icon","/images/dept.gif");
			else if("@K".equals(codesetid))
					treeitem.put("icon","/images/pos_l.gif");
			else{
				treeitem.put("qtip","代码："+itemid);
				
				//普通代码如果设置只能选叶子节点，并且当前节点不是叶子节点，将codesetid置为空
				if(this.onlyLeafNode && !"".equals(tempcodeitemid))
					treeitem.put("codesetid", "");
			}
		    	
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		//treeitem.put("icon", "/images/table.gif");
	    		if("true".equals(expanded)){ //true 全部展开
		    		treeitem.put("expanded",Boolean.TRUE);
		    		if(!"".equals(tempcodeitemid))
		    			treeitem.put("children", getleafJson(codesetid,itemid,multiple,doChecked)); //展开二级节点 changxy
	    		}else						//不展开下级
	    			treeitem.put("expanded",Boolean.FALSE);
	    		
	    		childrens.add(treeitem);
	    		
	    		
    	    }
    	    
    	    //如果不是机构或者不显示虚拟机构，返回
    	    if((!"UN".equals(codesetid) && !"UM".equals(codesetid) && !"@K".equals(codesetid)) || !this.vorg)
    	    		return childrens;
    	    
    	    String vsql = strsql.toString().replaceAll("organization", "vorganization");
    	    rset = dao.search(vsql);
    	    while (rset.next())
    	    {
	    	    HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetid = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc);
	    		treeitem.put("codesetid", codesetid);
	    		treeitem.put("orgtype", "vorg");
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/b_vroot.gif");
			else if("UM".equals(codesetid))
					treeitem.put("icon","/images/vdept.gif");
			else if("@K".equals(codesetid))
					treeitem.put("icon","/images/vpos_l.gif");
			else
				treeitem.put("qtip","代码："+itemid);
		    	
	    		//查询一下，判断是否有孩子节点
	    		treeitem.put("leaf", Boolean.FALSE);
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		
	    		childrens.add(treeitem);
	    		
	    		
    	    }
    	} catch (SQLException ee)
    	{
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	} finally
    	{
    	    try
    	    {
    		if (rset != null)
    		{
    		    rset.close();
    		}
    		
    		if (conn != null)
    		{
    		    conn.close();
    		}
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
    	
    	return childrens;
    }
    
    /**
     * 加载二级节点下的数据
     * changxy 20160612
     * 
     * */
    public ArrayList getleafJson(String codesetid,String codeitemid,Boolean multiple,Boolean doChecked)throws GeneralException {
    	//根据父节点查询数据
     	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate =sdf.format(new Date());
    	StringBuffer sbf=new StringBuffer();
    	sbf.append("select codesetid,codeitemid,codeitemdesc,childid from ");
    	if("@k".equals(codesetid)||"UM".equals(codesetid)||"UN".equals(codesetid))
    		sbf.append(" organization ");
    	else
    		sbf.append(" codeitem ");
    	sbf.append(" where  ");
    	sbf.append("codesetid=");
    	sbf.append("'");
    	sbf.append(codesetid);
    	sbf.append("'");
    	sbf.append(" and parentid=");
    	sbf.append("'");
    	sbf.append(codeitemid);
    	sbf.append("'");
    	sbf.append(" and codeitemid <>");
    	sbf.append("'");
    	sbf.append(codeitemid);
    	sbf.append("'");
    	sbf.append(" and ");
    	sbf.append( Sql_switcher.dateValue(backdate) +" between start_date and end_date");  //添加日期 changxy
    	sbf.append(" ORDER BY a0000,codeitemid ");
    	
    	ResultSet rset=null;
    	Connection conn=AdminDb.getConnection();
    	ContentDAO dao=new ContentDAO(conn);
    	ArrayList childrens=new ArrayList();
    	try {
			rset=dao.search(sbf.toString());
			while(rset.next()){
				HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetids = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc);
	    		treeitem.put("codesetid", codesetid);
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/unit.gif");
				else if("UM".equals(codesetid))
					treeitem.put("icon","/images/dept.gif");
				else if("@K".equals(codesetid))
					treeitem.put("icon","/images/pos_l.gif");
		    	
	    		//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,itemid);
	    		if(!"".equals(tempcodeitemid))
	    			treeitem.put("leaf", Boolean.FALSE);
	    		else{
	    			treeitem.put("leaf", Boolean.TRUE);
	    		}
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		
	    		//treeitem.put("icon", "/images/table.gif");
	    		childrens.add(treeitem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			 GeneralExceptionHandler.Handle(e);
		}finally{
			try
    	    {
    		if (rset != null)
    		{
    		    rset.close();
    		}
    		
    		if (conn != null)
    		{
    		    conn.close();
    		}
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }
		}
    	
    	return childrens;
    }
    /**
	* changxy
	*/
	public ArrayList outCodeJSON(boolean multiple,boolean doChecked) throws GeneralException{
    	String JSONStr = "";
    	StringBuffer strsql = new StringBuffer();
    	ResultSet rset = null;
    	Connection conn = AdminDb.getConnection();
    	
    	ArrayList  childrens = new ArrayList();
    	try
    	{
    	    strsql.append(getQueryString(conn));
    	    ContentDAO dao = new ContentDAO(conn);
    	    rset = dao.search(strsql.toString());
    	    while (rset.next())
    	    {
	    	    HashMap treeitem = new HashMap();
	    		String itemid = rset.getString("codeitemid");
	    		if (itemid == null)
	    		    itemid = "";
	    		itemid = itemid.trim();
	    		String codesetid = rset.getString("codesetid");
	    		String codeitemdesc = rset.getString("codeitemdesc");
	    		codeitemdesc = codeitemdesc==null||codeitemdesc.length()<1?"":codeitemdesc;
	    		treeitem.put("id", itemid);
	    		treeitem.put("text", codeitemdesc);
	    		treeitem.put("codesetid", codesetid);
	    		//设置图片
		    	if("UN".equals(codesetid))
		    		treeitem.put("icon","/images/unit.gif");
				else if("UM".equals(codesetid))
					treeitem.put("icon","/images/dept.gif");
				else if("@K".equals(codesetid))
					treeitem.put("icon","/images/pos_l.gif");
		    	
	    		//查询一下，判断是否有孩子节点
	    		String tempcodeitemid = getTempCodeItemid(codesetid,itemid);
	    		if(!"".equals(tempcodeitemid))
	    			treeitem.put("leaf", Boolean.FALSE);
	    		else{
	    			treeitem.put("leaf", Boolean.TRUE);
	    		}
	    		
	    		if(multiple)
	    			treeitem.put("checked", false);
	    		if(doChecked)
	    			treeitem.put("checked", true);
	    		//treeitem.put("icon", "/images/table.gif");
	    		//if(expanded.equals("true")) //true 全部展开
		    	//	treeitem.put("expanded",Boolean.TRUE);
	    		//else						//不展开下级
	    		//	treeitem.put("expanded",Boolean.FALSE);
	    		childrens.add(treeitem);
    	    }
    	} catch (SQLException ee)
    	{
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	} finally
    	{
    	    try
    	    {
    		if (rset != null)
    		{
    		    rset.close();
    		}
    		
    		if (conn != null)
    		{
    		    conn.close();
    		}
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
    	
    	return childrens;
    }
    
    
    /**
     * 输出xml串 增加管理范围筛选
     * @return
     * @throws GeneralException
     */
    public String outCodeTreesx(UserView userView) throws GeneralException
    {

	StringBuffer xmls = new StringBuffer();
	StringBuffer strsql = new StringBuffer();
	ResultSet rset = null;
	Connection conn = AdminDb.getConnection();
	Element root = new Element("TreeNode");

	root.setAttribute("id", "$$00");
	root.setAttribute("text", "root");
	root.setAttribute("title", "codeitem");
	Document myDocument = new Document(root);
	RowSet rs = null;
	try
	{

	    strsql.append(getQueryStringsx(userView));
	   
	    ContentDAO dao = new ContentDAO(conn);
	    rset = dao.search(strsql.toString());
	    while (rset.next())
	    {
	        boolean flag = false;
		Element child = new Element("TreeNode");
		String itemid = rset.getString("codeitemid");
		if (itemid == null)
		    itemid = "";
		itemid = itemid.trim();
		String codesetid = rset.getString("codesetid");
		child.setAttribute("id", codesetid + itemid);
		child.setAttribute("text",rset.getString("codeitemdesc")); //" "+rset.getString("codeitemdesc"));//xcs 2013-11-15
		child.setAttribute("title", itemid + ":" + rset.getString("codeitemdesc"));
		if (!itemid.equalsIgnoreCase(rset.getString("childid")))
		    child.setAttribute("xml", "/system/get_code_tree_filter.jsp?codesetid=" + this.codesetid/* rset.getString("codesetid") */+ "&codeitemid=" + itemid);
		
		if("55_1".equalsIgnoreCase(codesetid) || "55".equalsIgnoreCase(codesetid)){
            rs = dao.search("select 1 from r50 where codeitemid='" + rset.getString("codeitemid") + "'");
            if(rs.next())
                flag =true;
        }
		
		if ("UN".equals(codesetid))
		    child.setAttribute("icon", "/images/unit.gif");
		else if ("UM".equals(codesetid))
		    child.setAttribute("icon", "/images/dept.gif");
		else if ("@K".equals(codesetid))
		    child.setAttribute("icon", "/images/pos_l.gif");
		else if("54".equals(codesetid)||"55".equals(codesetid) || "55_1".equals(codesetid) || "55_2".equals(codesetid))
			if(userView!=null&&!userView.isSuper_admin()){
				String tmpb0110=rset.getString("b0110");
            	TrainCourseBo tbo = new TrainCourseBo(userView);
            	int isP = tbo.isUserParent(tmpb0110);
            	if("54".equals(codesetid)&&isP!=1&&isP!=3)
            		continue;
            	
            	if(isP==-1)
            		continue;
            	else if(isP==2) {
            	    if(flag || "55_2".equalsIgnoreCase(codesetid))
                        child.setAttribute("icon","/images/icon_wsx.gif");//上级图片 待定
                    else
                        child.setAttribute("icon","/images/book1.gif");//上级图片 待定
            	} else {
            	    if(flag || "55_2".equalsIgnoreCase(codesetid))
                        child.setAttribute("icon","/images/icon_wsx.gif");
                    else
                        child.setAttribute("icon","/images/book.gif");
            	}

			}else
				child.setAttribute("icon", "/images/book.gif");
		else
		    child.setAttribute("icon", "/images/table.gif");
		root.addContent(child);
	    }

	    XMLOutputter outputter = new XMLOutputter();
	    Format format = Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	    outputter.setFormat(format);
	    xmls.append(outputter.outputString(myDocument));
	    // System.out.println("SQL=" +xmls.toString());
	} catch (SQLException ee)
	{
	    ee.printStackTrace();
	    GeneralExceptionHandler.Handle(ee);
	} finally
	{
	    try
	    {
	        if (rs != null)
	        {
	            rs.close();
	        }
		if (rset != null)
		{
		    rset.close();
		}
		if (conn != null)
		{
		    conn.close();
		}
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }

	}
	return xmls.toString();
    }
    public String[] relatingcode(String codeset)
    {

	String[] rel = new String[3];
	ResultSet rset = null;
	Connection conn = null;
	try
	{
	    conn = AdminDb.getConnection();
	    ContentDAO dao = new ContentDAO(conn);
	    StringBuffer buf = new StringBuffer();
	    buf.append("select codetable,codevalue,codedesc from t_hr_relatingcode where");
	    buf.append(" codesetid='");
	    buf.append(codeset);
	    buf.append("'");
	    rset = dao.search(buf.toString());
	    String codetable = "";
	    String codevalue = "";
	    String codedesc = "";
	    while (rset.next())
	    {
		codetable = rset.getString("codetable");
		codetable = codetable != null ? codetable : "";
		codevalue = rset.getString("codevalue");
		codevalue = codevalue != null ? codevalue : "";
		codedesc = rset.getString("codedesc");
		codedesc = codedesc != null ? codedesc : "";
	    }
	    rel[0] = codetable;
	    rel[1] = codevalue;
	    rel[2] = codedesc;

	} catch (GeneralException e)
	{
	    e.printStackTrace();
	} catch (SQLException e)
	{
	    e.printStackTrace();
	} finally
	{
	    try
	    {
		if (rset != null)
		{
		    rset.close();
		}
		
		if (conn != null)
		{
		    conn.close();
		}
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }
	}
	return rel;
    }
    
    public String getTempCodeItemid(String codesetid,String codeitemid) throws GeneralException{
    	String str = "";
    	//用于区分课程分类与课程名称 cxg 2013-08-15 v6.x
    	String codesetidL = "";
		String flag = "";
		if (this.codesetid.indexOf("_") != -1) {
			codesetidL = this.codesetid;
			String[] codesetidLs=codesetidL.split("_");
			if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
				this.codesetid=codesetidLs[0];
				flag=codesetidLs[1];
			}
		}
		
		ResultSet rset = null;
		Connection conn = AdminDb.getConnection();
    	try{
    		ContentDAO dao = new ContentDAO(conn);
    		StringBuffer sb = new StringBuffer("");
    		sb.append("select codeitemid from ");
			if ("UN".equals(this.codesetid) || "UM".equals(this.codesetid) || "@K".equals(this.codesetid)) {
				sb.append("organization where codeitemid in (select codeitemid from organization where parentid='"+codeitemid+"' and parentid<>codeitemid) and (codesetid='");
				if ("UN".equals(this.codesetid)) {
					sb.append("UN')");
				} else if ("UM".equals(this.codesetid)) {
					sb.append("UM' or codesetid='UN')");
				} else if ("@K".equals(this.codesetid)) {
					sb.append("@K' or codesetid='UM' or codesetid='UN')");
				} 
				 String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				 sb.append(" and "+Sql_switcher.year("start_date")+"*10000+"+Sql_switcher.month("start_date")+"*100+"+Sql_switcher.day("start_date")+"<="+now);
				 sb.append(" and "+Sql_switcher.year("end_date")+"*10000+"+Sql_switcher.month("end_date")+"*100+"+Sql_switcher.day("end_date")+">="+now);
				 sb.append(" order by a0000,codeitemid");
			} else {
				sb.append("codeitem where codesetid='"+codesetid+"' and parentid='"+codeitemid+"' and codeitemid<>parentid");
				if ("55".equalsIgnoreCase(this.codesetid)) {
					if ("1".equalsIgnoreCase(flag))
						sb.append(" and not exists(select 1 from r50 where r50.codeitemid=codeitem.codeitemid)");
					else if ("2".equalsIgnoreCase(flag)){
						sb.append(" and exists(select 1 from r50  where R5022='04'");
						if(!this.userView.isSuper_admin()){
							String where = TrainCourseBo.getLessonByBusiWhere(this.userView);
							sb.append(where);
						}
						sb.append(" and r50.codeitemid=codeitem.codeitemid)");
					}
					if("55_1".equalsIgnoreCase(codesetidL)|| "55_2".equalsIgnoreCase(codesetidL))
						this.codesetid = codesetidL;
				}else{//添加 else：过滤无效的代码  guodd 15-02-06
					int codeFlag = getCodeFlag(this.codesetid);		
				    if("1".equals(this.isValidCtr)){
					    if(codeFlag == 1){
					    	String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					        sb.append(" and "+Sql_switcher.dateValue(now)+" between start_date and end_date ");
					    }else
					        sb.append(" and invalid=1 ");
				    }
				}
				if(isHavA0000)
					sb.append(" order by a0000,codeitemid");
				else
					sb.append(" order by codeitemid");
			}
    		rset = dao.search(sb.toString());
    		if(rset.next()){
    			str = rset.getString("codeitemid")==null?"":rset.getString("codeitemid");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    	    try
    	    {
    		if (rset != null)
    		{
    		    rset.close();
    		}
    	
    		if (conn != null)
    		{
    		    conn.close();
    		}
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
		return str;
    }

    public String getCodeitemid()
    {

	return codeitemid;
    }

    public void setCodeitemid(String codeitemid)
    {

	this.codeitemid = codeitemid;
    }

    public String getCodesetid()
    {

	return codesetid;
    }

    public void setCodesetid(String codesetid)
    {

	this.codesetid = codesetid;
    }
    
    public String getIsValidCtr() {
		return isValidCtr;
	}
	public void setIsValidCtr(String isValidCtr) {
		this.isValidCtr = isValidCtr;
	}
	public boolean isVorg() {
		return vorg;
	}
	public void setVorg(boolean vorg) {
		this.vorg = vorg;
	}
	public boolean isOnlyLeafNode() {
		return onlyLeafNode;
	}
	public void setOnlyLeafNode(boolean onlyLeafNode) {
		this.onlyLeafNode = onlyLeafNode;
	}
	
	

}
