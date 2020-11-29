/**
 * 
 */
package com.hjsj.hrms.module.template.utils.javabean;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 25, 20065:33:25 PM
 * @author chenmengqing
 * @version 4.0
 */
public class TTitle implements Serializable{
	/**以"`"作为单元格内容换行*/
	private String hz;
	private int rtop;
	private int rleft;
	private int rwidth;
	private int rheight;
	private String fontname;
	private int fontsize;
	private int gridno;
	private int pageid;
	private int tabid;
	private int ins_id=0;
	/**
	 *  <image></image>
	 *  <format>0</format>
     *  <prefix></prefix>
	 */
	
	/**
	 *template_title加字段Content image类型(进模板设计自动加字段)
	 *template_title.flag=7表示图片
	 *template_title.extendattr:
	 *<image>
	 *     <ext>.JPG|.BMP</ext><stretch>拉伸True|False</stretch>
	 *     <transparent>透明True|False</transparent>
  	 *     <proportional>保持比例True|False</proportional>
	 *     <background>置底True(默认值)|置顶False</background>
	 *</image>
	 *<format>日期格式</format>
	 *<prefix>制表日期前缀</prefix>
	*/
	private String extendattr;
	/**
	 * =1,正常
	 * =2,粗体
	 * =3,斜体
	 * =4,粗斜体
	 */
	private int fonteffect;
	/**标题类型
	 * =0,文本	 
	 * =1,日期	 
	 * =2,时间	 
	 * =3,制表人	 
	 * =4,总页数	 
	 * =5,页码 
	 * =6,通知对象
	 */
	private int flag;
	private Connection con=null;
	
	public TTitle() {
		super();
	}
	/**
	 * 取得extendattr指标定义的样式内容
	 * @param strPattern
	 * @param extendattr
	 * @return
	 */
	public String getPattern(String strPattern,String extendattr)
	{
		int iS,iE;
		String result="";
		String sSP="<"+strPattern+">";
		iS=extendattr.indexOf(sSP);
		String sEP="</"+strPattern+">";
		iE=extendattr.indexOf(sEP);
		if(iS>=0 && iS<iE)
		{
			result=extendattr.substring(iS+sSP.length(), iE);
		}
		return result;
	}	
	
	/**
	 * 数字换算
	 * @param strV
	 * @param flag
	 * @return
	 */
	private String[] exchangNumToCn(int year,int month,int day)
	{
		String[] strarr=new String[3];
		StringBuffer buf=new StringBuffer();
		String value=String.valueOf(year);
		for(int i=0;i<value.length();i++)
		{
			switch(value.charAt(i))
			{
			case '1':
				buf.append("一");
				break;
			case '2':
				buf.append("二");
				break;
			case '3':
				buf.append("三");
				break;
			case '4':
				buf.append("四");
				break;
			case '5':
				buf.append("五");
				break;
			case '6':
				buf.append("六");
				break;
			case '7':
				buf.append("七");
				break;
			case '8':
				buf.append("八");
				break;
			case '9':
				buf.append("九");
				break;
			case '0':
				buf.append("零");
				break;
			}
		}
		strarr[0]=buf.toString();
		buf.setLength(0);
		switch(month)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;
		}
		strarr[1]=buf.toString();
		buf.setLength(0);
		switch(day)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;			
		case 13:
			buf.append("十三");
			break;			
		case 14:
			buf.append("十四");
			break;			
		case 15:
			buf.append("十五");
			break;			
		case 16:
			buf.append("十六");
			break;			
		case 17:
			buf.append("十七");
			break;			
		case 18:
			buf.append("十八");
			break;			
		case 19:
			buf.append("十九");
			break;			
		case 20:
			buf.append("二十");	
			break;			
		case 21:
			buf.append("二十一");
			break;			
		case 22:
			buf.append("二十二");	
			break;			
		case 23:
			buf.append("二十三");
			break;			
		case 24:
			buf.append("二十四");	
			break;			
		case 25:
			buf.append("二十五");
			break;			
		case 26:
			buf.append("二十六");	
			break;			
		case 27:
			buf.append("二十七");
			break;			
		case 28:
			buf.append("二十八");	
			break;			
		case 29:
			buf.append("二十九");
			break;			
		case 30:
			buf.append("三十");	
			break;			
		case 31:
			buf.append("三十一");				
			break;
		}		
		strarr[2]=buf.toString();
		return strarr;
	}		
	/**
	 * <!--
		日期格式：
		0: 1991.12.3
		1: 1990.01.01
		2: 1990年2月10日
		3: 1990年01月01日
		4: 1991-12-3
		5: 1990-01-01
		-->
		<format>日期格式</format>
		<prefix>制表日期前缀</prefix>
	 * @return
	 */
	private String getExchangeDate()
	{
		String fmt=getPattern("format",this.extendattr);
		String prefix=getPattern("prefix",this.extendattr);
		String tmp=formatDateValue(new Date(),prefix,Integer.parseInt(fmt));
		return tmp;
	}
	

	/**
	 * 格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	private String formatDateValue(Date date,String prefix,int nfmt)
	{
		StringBuffer buf=new StringBuffer();
		buf.append(prefix);
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		switch(nfmt)
		{
		case 0: //1991.12.3
		case 4:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 1://1992.02.01
		case 5:
			buf.append(year);
			buf.append(".");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10)
				buf.append(day);
			else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		case 2://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;	
		case 3://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10)
				buf.append(month);
			else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10)
				buf.append(day);
			else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;			
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		String tmp=buf.toString();
		if(nfmt==4||nfmt==5)
			tmp=tmp.replaceAll("/.", "-");
		return tmp;
	}	
	/**
	 * 转换
	 * @param userview
	 * @param pages 总页数
	 * @param curpage 　当前页数
	 * @return
	 */
	public String getOutText(UserView userview,int pages,int curpage)
	{
		StringBuffer strc=new StringBuffer();
		switch(flag)
		{
		case 1://日期
			if(this.extendattr==null||this.extendattr.length()==0)
			{
				strc.append(ResourceFactory.getProperty("hmuster.label.createTableDate"));
				strc.append(":");
				strc.append(DateStyle.dateformat(new Date(),"yyyy-MM-dd"));
			}
			else
			{
				strc.append(getExchangeDate());
			}
			break;
		case 2://时间
			strc.append(ResourceFactory.getProperty("hmuster.label.createTableTime"));
			strc.append(" ");		
			String datetime=DateStyle.getSystemTime();
			strc.append(datetime.substring(11));
			break;
		case 3://制表人
		//	strc.append(ResourceFactory.getProperty("hmuster.label.createTableMen"));
		//	strc.append(":");	
			if(this.extendattr==null||this.extendattr.length()==0)
			{
				strc.append(ResourceFactory.getProperty("hmuster.label.createTableMen"));
				strc.append(":");
			}
			else
			{
				strc.append(getPattern("prefix",this.extendattr));
			}
			if(this.ins_id!=0&&this.con!=null)
			{ 
				try { 
					ContentDAO dao=new ContentDAO(this.con);
					RowSet rset=dao.search("select actorname from t_wf_instance where ins_id="+this.ins_id);
					if(rset.next())
						strc.append(rset.getString(1));
				}
				catch (Exception e) {
			            e.printStackTrace();
			    } 
			}
			else
			{
				if(userview.getUserFullName()==null||userview.getUserFullName().length()==0)
					strc.append(userview.getUserName());
				else
					strc.append(userview.getUserFullName());
			}
			break;
		case 4://总页数
			strc.append(ResourceFactory.getProperty("hmuster.label.total"));
			strc.append(" ");
			strc.append(pages);
			strc.append(" ");
			strc.append(ResourceFactory.getProperty("hmuster.label.paper"));
			break;
		case 5://页码
			strc.append(ResourceFactory.getProperty("hmuster.label.d"));
			strc.append(" ");
			strc.append(curpage);
			strc.append(" ");
			strc.append(ResourceFactory.getProperty("hmuster.label.paper"));			
			break;
		case 6://通知对象
			strc.append("");
			break;
		default:
			strc.append(this.hz);
			break;
		}
		return strc.toString();
	}
	
	public String getOutText(UserView userview)
	{
		StringBuffer strc=new StringBuffer();
		switch(flag)
		{
		case 1://日期
			if(this.extendattr==null||this.extendattr.length()==0)
			{
				strc.append(ResourceFactory.getProperty("hmuster.label.createTableDate"));
				strc.append(":");
				strc.append(DateStyle.dateformat(new Date(),"yyyy-MM-dd"));
			}
			else
			{
				strc.append(getExchangeDate());				
			}
			break;
		case 2://时间
			strc.append(ResourceFactory.getProperty("hmuster.label.createTableTime"));
			strc.append(" ");	
			String datetime=DateStyle.getSystemTime();
			strc.append(datetime.substring(11));
			break;
		case 3://制表人
			if(this.extendattr==null||this.extendattr.length()==0)
			{
				strc.append(ResourceFactory.getProperty("hmuster.label.createTableMen"));
				strc.append(":");
			}
			else
			{
				strc.append(getPattern("prefix",this.extendattr));
			}
			if(this.ins_id!=0&&this.con!=null)
			{ 
				try { 
					ContentDAO dao=new ContentDAO(this.con);
					RowSet rset=dao.search("select actorname from t_wf_instance where ins_id="+this.ins_id);
					if(rset.next())
						strc.append(rset.getString(1));
				}
				catch (Exception e) {
			            e.printStackTrace();
			    } 
			}
			else
			{
				if(userview.getUserFullName()==null||userview.getUserFullName().length()==0)
					strc.append(userview.getUserName());
				else
					strc.append(userview.getUserFullName());
			}
			
			break;
		case 4://总页数
			strc.append(ResourceFactory.getProperty("hmuster.label.total"));
			try { 
				ContentDAO dao=new ContentDAO(this.con);
				RowSet rset=dao.search("select count(pageid) from template_page where tabid="+this.tabid);
				if(rset.next())
					strc.append(rset.getString(1));
				if(rset!=null)
					rset.close();
			}
			catch (Exception e) {
		            e.printStackTrace();
		    }  
			strc.append(ResourceFactory.getProperty("hmuster.label.paper"));
			break;
		case 5://页码
			strc.append(ResourceFactory.getProperty("hmuster.label.d"));
			strc.append(this.pageid+1);
			strc.append(ResourceFactory.getProperty("hmuster.label.paper"));			
			break;
		case 6://通知对象 ,打印才出现
//		case 4://总页数
//		case 5://页码
			strc.append("");
			break;	
		case 7: //照片
			 String ext=getPattern("ext",this.extendattr);
			 String fileName=createPhotoFile(ext);
			 if(fileName.length()==0||ext==null||ext.trim().length()==0)
				 strc.append(this.hz);
			 else
			 {
			    fileName=PubFunc.encrypt(fileName);
				strc.append("tp<img src='/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName+"' height='"+this.rheight+"' width='"+this.rwidth+"' />tp"); 
			 }
			break;
		default:
			strc.append(this.hz);
			break;
		}
		return strc.toString();
	}
	
	
	/**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(String ext) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        if(this.con==null)
        	return filename;
        InputStream in = null;
        java.io.FileOutputStream fout = null;
        try {
        	ContentDAO dao=new ContentDAO(this.con);
        	RowSet rset=dao.search("select content from template_title where tabid="+this.tabid+" and pageid="+this.pageid+" and gridno="+this.gridno);
            if (rset.next()) 
            {
                
            	
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,ext,
                        new File(System.getProperty("java.io.tmpdir")));             
                in = rset.getBinaryStream("content");                
                fout = new java.io.FileOutputStream(tempFile);                
                int len;
                byte[] buf = new byte[1024];
                if(in!=null){
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                }
                //fout.close();
               
                filename= tempFile.getName();                
            }
			if(rset!=null)
				rset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            PubFunc.closeIoResource(fout);
            PubFunc.closeIoResource(in);
        }
        return filename;
    }
	
	
	
	
	/**
	 * 求得标题字体信息
	 * @return
	 */
	private String getFontStyle()
	{
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		style.append(this.fontsize);
		style.append("pt");
		switch(this.fonteffect)
		{
		case 2:
			style.append(";font-weight:");
			style.append("bold");
			break;
		case 3:
			style.append(";font-style:");
			style.append("italic");			
			break;
		case 4:
			style.append(";font-weight:");
			style.append("bold");
			style.append(";font-style:");
			style.append("italic");				
			break;
		}
		return style.toString();
	}
	
	private String getTablePos()
	{
		StringBuffer strpos=new StringBuffer();
		strpos.append("table-layout:fixed;position:absolute;top:");
		strpos.append(this.rtop);
		strpos.append("px");
		
		
		if(this.flag==7) //照片
		{
			String  background=getPattern("background",this.extendattr); //照片 置底True(默认值)|置顶False
			if("False".equalsIgnoreCase(background))
				strpos.append(";z-index:1");
		}		
		strpos.append(";left:");
		strpos.append(this.rleft);
		strpos.append("px");
		strpos.append(";width:");
		strpos.append(this.rwidth+200);
		strpos.append("px");
		strpos.append(";height:");
		strpos.append(this.rheight);
		strpos.append("px");
		return strpos.toString();
	}
	
	 
	
	/**
	 * 创建HTML对象
	 * @param element
	 */
	public void createTitleView(Element element,UserView userview)
	{
		/**
		 * border=1
		 * style="position:absolute;top:211;left:147;width:83px;height:27px;table-layout:"fixed""
		 */
		Element table=new Element("table");
		table.setAttribute("border","0");
		table.setAttribute("style",getTablePos());
			
		Element tr=new Element("tr");
		Element td=new Element("td");
		td.setAttribute("valign","center");
		td.setAttribute("align","left");
		td.setAttribute("class","no_line");//没有边框线
		td.setAttribute("nowrap","false");
		Element font=new Element("font");
		font.setAttribute("face",this.fontname);
		font.setAttribute("style",getFontStyle());
		font.setText(getOutText(userview));
		td.addContent(font);
		//td.setText(getOutText(userview));
		tr.addContent(td);
		table.addContent(tr);
		element.addContent(table);
	}
	
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getFonteffect() {
		return fonteffect;
	}
	public void setFonteffect(int fonteffect) {
		this.fonteffect = fonteffect;
	}
	public String getFontname() {
		return fontname;
	}
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}
	public int getFontsize() {
		return fontsize;
	}
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}
	public String getHz() {
		return hz;
	}
	public void setHz(String hz) {
		this.hz = hz;
	}
	public int getRheight() {
		return rheight;
	}
	public void setRheight(int rheight) {
		this.rheight = rheight;
	}
	public int getRleft() {
		return rleft;
	}
	public void setRleft(int rleft) {
		this.rleft = rleft;
	}
	public int getRtop() {
		return rtop;
	}
	public void setRtop(int rtop) {
		this.rtop = rtop;
	}
	public int getRwidth() {
		return rwidth;
	}
	public void setRwidth(int rwidth) {
		this.rwidth = rwidth;
	}

	public String getExtendattr() {
		return extendattr;
	}

	public void setExtendattr(String extendattr) {
		this.extendattr = extendattr;
	}
	public int getGridno() {
		return gridno;
	}
	public void setGridno(int gridno) {
		this.gridno = gridno;
	}
	public int getPageid() {
		return pageid;
	}
	public void setPageid(int pageid) {
		this.pageid = pageid;
	}
	public int getTabid() {
		return tabid;
	}
	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	public Connection getCon() {
		return con;
	}
	public void setCon(Connection con) {
		this.con = con;
	}
	public int getIns_id() {
		return ins_id;
	}
	public void setIns_id(int ins_id) {
		this.ins_id = ins_id;
	}

}
