package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * <p>Title:BrowseAffixTag.java</p>
 * <p>Description>:BrowseAffixTag.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 20, 2010 5:12:06 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class BrowseAffixTag extends BodyTagSupport {

    private String setId="";
    private String i9999;
	
	private String a0100="";
	private String nbase="";
	private String e01a1="";
	private String pertain_to="";//属于类别
	
	private String MainGuid;
	private String ChildGuid;
    private String dbFlag="A";//人员 单位 岗位 A B K
    //主集/子集附件是否只显示图片 =1:只显示图标；=0：显示文字和图标；默认=1
    private String onlyImg = "1";
    
    
	public String getSetId() {
		return setId;
	}
	public void setSetId(String setId) {
		this.setId = setId;
	}
	public String getI9999() {
		return i9999;
	}
	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getPertain_to() {
		return pertain_to;
	}
	public void setPertain_to(String pertain_to) {
		this.pertain_to = pertain_to;
	}
	
	public String getMainGuid() {
		return MainGuid;
	}
	public void setMainGuid(String mainGuid) {
		MainGuid = mainGuid;
	}
	public String getChildGuid() {
		return ChildGuid;
	}
	public void setChildGuid(String childGuid) {
		ChildGuid = childGuid;
	}
	public String getDbFlag() {
		return dbFlag;
	}
	public void setDbFlag(String dbFlag) {
		this.dbFlag = dbFlag;
	}
	
	public String getOnlyImg() {
        return onlyImg;
    }
    public void setOnlyImg(String onlyImg) {
        this.onlyImg = onlyImg;
    }
    
    public int doEndTag() throws JspException 
	{
		Connection conn=null;
		if(pertain_to==null||pertain_to.length()<=0)
			return SKIP_BODY;	
		try{
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
			
	        //zxj 20160420  jazz17990 可能是改自助申请修改信息安全时，a0100不传值，导致这里a0100的值是“a0100"
            if("a0100".equalsIgnoreCase(this.a0100)) 
                this.a0100 = userview.getA0100();
            
			if("post".equalsIgnoreCase(pertain_to))
			{
				positionManual(dao,this.nbase,this.a0100);
			}else if("task".equalsIgnoreCase(pertain_to))
			{
				taskManual(dao,nbase,a0100);
			}else if("ps".equalsIgnoreCase(pertain_to)){
				psitionManual(dao,this.a0100);
			}else if("job".equalsIgnoreCase(pertain_to)){
				jobcManual(dao,this.a0100);
			}else if ("record".equalsIgnoreCase(pertain_to)){
			    //员工管理信息浏览子集名称为空时，默认显示主集
			    setId = StringUtils.isEmpty(setId) ? "A01" : setId;
				whetherHasFileRecord(conn,userview,dao,nbase,setId,a0100,i9999);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return SKIP_BODY;	
	}
	private void jobcManual(ContentDAO dao, String a0100) {
		StringBuffer sql=new StringBuffer();
		RowSet rs=null;
		try {
			
			sql.setLength(0);
			sql.append("select ole,i9999,fileid from h00  where UPPER(flag) = 'K' ");
			sql.append("and h0100='"+a0100+"'");
			rs=dao.search(sql.toString());
			if(rs.next()){
				 String fileid = rs.getString("fileid"); 
				 int i9999=rs.getInt("i9999");
				 if(StringUtils.isNotEmpty(fileid))
				 {
					 pageContext.getOut().println("<a href=\"/pos/roleinfo/pos_dept_post?encryptParam="+PubFunc.encrypt("usertable=h00&usernumber="+a0100+"&i9999="+i9999));
					 pageContext.getOut().println("\">");
					 pageContext.getOut().println("<img src=\"/images/attach.gif\" border=0>");
					 pageContext.getOut().println("</a>");
				 }	 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}		
	}
	/**
	 * 人员任务说明书
	 * @param dao
	 * @param nbase
	 * @param a0100
	 */
	private void taskManual(ContentDAO dao,String nbase,String a0100)
	{
		if(nbase==null||nbase.length()<=0)
			return; 
		if(a0100==null||a0100.length()<=0)
			return; 
		
		Date date=new Date();
		int taskyear=DateUtils.getYear(date);		
		StringBuffer sql=new StringBuffer();
		sql.append("select ole,i9999,fileid from "+nbase+"a00  where UPPER(flag) = 'T'  ");
		sql.append("and  i9999=(select max(b.i9999) from "+nbase+"a00  b where b.a0100='"+a0100+"') ");
		sql.append("and a0100='"+a0100+"'");
		//sql.append(" and "+Sql_switcher.year("createtime")+"");
		sql.append("and "+Sql_switcher.year("createtime")+" >= "+taskyear+" and "+Sql_switcher.year("createtime")+" <="+(taskyear+1)+"");;
		RowSet rs=null;
		try {
			rs=dao.search(sql.toString());
			if(rs.next()){
				 String fileid = rs.getString("fileid"); 
				 int i9999=rs.getInt("i9999");
				 if(StringUtils.isNotEmpty(fileid))
				 {
					 pageContext.getOut().println("<a href=\"/pos/roleinfo/pos_task_book?encryptParam="+PubFunc.encrypt("usertable="+nbase+"a00&usernumber="+a0100+"&i9999="+i9999));
					 pageContext.getOut().println("\">");
					 pageContext.getOut().println("<img src=\"/images/view.gif\" border=0>");
					 pageContext.getOut().println("</a>");
				 }	 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
		    PubFunc.closeIoResource(rs);
		}		
	}
	/**
	 * 职位说明书
	 * @param dao
	 * @param e01a1
	 * @return
	 */
	private void positionManual(ContentDAO dao,String nbase,String a0100)
	{
		String e01a1="";
		InputStream in = null;
		StringBuffer sql=new StringBuffer();
		RowSet rs=null;
		sql.append("select e01a1 from "+nbase+"A01 where a0100='"+a0100+"'");		
		try {
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				e01a1=rs.getString("e01a1");
			}
			if(e01a1==null||e01a1.length()<=0)
				return; 
			sql.setLength(0);
			sql.append("select ole,i9999,fileid from k00  where UPPER(flag) = 'K'  ");
			//tianye update Sql
			sql.append("and  i9999=(select max(b.i9999) from k00 b where UPPER(flag) = 'K' and b.e01a1='"+e01a1+"') ");
			//sql.append("and  i9999=(select max(b.i9999) from k00 b where b.e01a1='"+e01a1+"') ");
			sql.append("and e01a1='"+e01a1+"'");
			rs=dao.search(sql.toString());
			if(rs.next()){
				 String fileid = rs.getString("fileid");
				 int i9999=rs.getInt("i9999");
				 if(StringUtils.isNotEmpty(fileid))
				 {
					 pageContext.getOut().println("<a href=\"/pos/roleinfo/pos_dept_post?encryptParam="+PubFunc.encrypt("usertable=k00&usernumber="+e01a1+"&i9999="+i9999));
					 pageContext.getOut().println("\">");
					 pageContext.getOut().println("<img src=\"/images/attach.gif\" border=0>");
					 pageContext.getOut().println("</a>");
				 }	 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			if(in != null)
			    PubFunc.closeResource(in);
		}		
	}
	
	/**
	 * 岗位管理职位说明书
	 * @param dao
	 * @param e01a1
	 * @return
	 */
	private void psitionManual(ContentDAO dao,String a0100)
	{
		StringBuffer sql=new StringBuffer();
		RowSet rs=null;
		try {
			
			sql.setLength(0);
			sql.append("select ole,i9999,fileid from k00  where UPPER(flag) = 'K' ");
			sql.append("and e01a1='"+a0100+"'");
			rs=dao.search(sql.toString());
			if(rs.next()){
//				 in = rs.getBinaryStream("ole");
				 String fileid=rs.getString("fileid");
				 int i9999=rs.getInt("i9999");
				 if(StringUtils.isNotEmpty(fileid))
				 {
					 pageContext.getOut().println("<a href=\"/pos/roleinfo/pos_dept_post?encryptParam="+PubFunc.encrypt("usertable=k00&usernumber="+a0100+"&i9999="+i9999));
					 pageContext.getOut().println("\">");
					 pageContext.getOut().println("<img src=\"/images/attach.gif\" border=0>");
					 pageContext.getOut().println("</a>");
				 }	 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			PubFunc.closeIoResource(rs);
		}		
	}
	
	/**
	 * @Title: whetherHasFileRecord   
     * @Description: 是否显示附件图标
	 * @param dao
	 * @param a0100
	 * @author: liuyang
	 */
	public void whetherHasFileRecord(Connection conn,UserView userview,ContentDAO dao,String nbase,String setId,String a0100,String i9999){
        RowSet rs=null;
        if(i9999==null||"".equals(i9999)){
        	i9999 = "0";
        }
        MultiMediaBo multimediaBo =new MultiMediaBo(conn,userview,dbFlag,nbase,setId,a0100,Integer.parseInt(i9999));
        StringBuffer sb = new StringBuffer();
        String kind = "";
        try {
            if(kind==""){
            	kind="6";
            }
            sb.setLength(0);
            sb.append("select COUNT(1) as num from hr_multimedia_file");
            //根据主集，子集记录查询
            sb.append(" where mainguid ='").append(multimediaBo.getMainGuid()).append("'");
            if (multimediaBo.isMainSet()){
                sb.append(" and (childguid ='' or childguid is null )");  
            }else 
            {
                sb.append(" and childguid ='").append(multimediaBo.getChildGuid()).append("'");
            }
            if ("A".equals(this.dbFlag)){          
                sb.append(" and upper(nbase) ='").append(this.nbase.toUpperCase()).append("'");
            }
            sb.append(" and A0100 ='").append(this.a0100).append("'");
            
            //根据当前用户权限查询分类
        	ArrayList filetypeList = multimediaBo.getPowerTypeList(dao, kind, this.a0100);
			for (int i = 0; i < filetypeList.size(); i++) {
				if(i==0){
					sb.append(" and (class ='").append(filetypeList.get(i)).append("'");
				}else {
					sb.append(" or class ='").append(filetypeList.get(i)).append("'");
				}
			}
			if(filetypeList.size()>0){
				sb.append(")");
			}
            sb.append(" and dbflag ='").append(this.dbFlag).append("'");
        
            rs = dao.search(sb.toString());
            if(rs.next()){
				int rows = rs.getInt("num");
				//查询出附件有记录，打印出附件图标
				if (rows > 0) {
					//查询主集的时候，子集GUID为空
					if(multimediaBo.getChildGuid()==null||"".equals(multimediaBo.getChildGuid())){
						//<a href="###"  onclick='multimediahref("${browseForm.userbase}","${a0100}");'><img src="/images/muli_view.gif" border=0></a>
						pageContext.getOut().println("<a href=\"###\" onclick=\'multimediahref(\""+nbase+"\",\""+a0100+"\");\'>");
						if("0" == this.onlyImg)
						    pageContext.getOut().println("附件<img align=\"absmiddle\" src=\"/images/muli_view.gif\" border=0>");
						else    
						    pageContext.getOut().println("<img align=\"absmiddle\" src=\"/images/muli_view.gif\" border=0>");
						
						pageContext.getOut().println("</a>");
					}else{
						//<a href="###"  onclick="multimediahref('${browseForm.userbase}','${browseForm.a0100}','<bean:write  name="element" property="string(i9999)" filter="true"/>');"><img src="/images/muli_view.gif" border=0></a>
						pageContext.getOut().println("<a href=\"###\" onclick=\'multimediahref(\""+nbase+"\",\""+a0100+"\",\""+i9999+"\");\'>");
						if("0" == this.onlyImg)
                            pageContext.getOut().println("附件<img align=\"absmiddle\" src=\"/images/muli_view.gif\" border=0>");
                        else    
                            pageContext.getOut().println("<img align=\"absmiddle\" src=\"/images/muli_view.gif\" border=0>");
						
						pageContext.getOut().println("</a>");
					}
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	try {
        		if(rs!=null)
        		{
        			rs.close();
        		}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
	}
	public String getE01a1() {
		return e01a1;
	}
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	
}
