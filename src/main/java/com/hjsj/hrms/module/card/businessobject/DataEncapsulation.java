/*
 * Created on 2005-5-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.module.card.businessobject;

import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-10:14:42:25</p>
 * @author Administrator
 * @version 1.0
 * 
 */
public class DataEncapsulation {
	StringBuffer sql=new StringBuffer();
	private UserView userview=null;
	private Connection conn=null;
	private HashMap<String,HashMap<Double,ArrayList<RGridView>> > gridMapList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>(); //页签：rtop+rleft:lists(rgrid) 多个
	private HashMap<String,HashMap<Double,ArrayList<RGridView>>>  gridTopIndexList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();//页签：rtop+rheight:lists(rgrid) 
	//当前单元格上方单元格参数集合
	private HashMap<String,HashMap<Double,ArrayList<RGridView>>>  lastGridTopList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();//页签：rtop-rheight:....
	
	
	
    public HashMap<String, HashMap<Double, ArrayList<RGridView>>> getLastGridTopList() {
		return lastGridTopList;
	}

	public void setLastGridTopList(HashMap<String, HashMap<Double, ArrayList<RGridView>>> lastGridTopList) {
		this.lastGridTopList = lastGridTopList;
	}

	public HashMap<String, HashMap<Double, ArrayList<RGridView>>> getGridTopIndexList() {
		return gridTopIndexList;
	}

	public void setGridTopIndexList(HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridTopIndexList) {
		this.gridTopIndexList = gridTopIndexList;
	}

	public DataEncapsulation() {
    	gridMapList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();
    	gridTopIndexList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();//页签：rtop+rheight:lists(rgrid) 
    	lastGridTopList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();
    }	

    public HashMap<String, HashMap<Double, ArrayList<RGridView>>> getGridMapList() {
		return gridMapList;
	}

	public void setGridMapList(HashMap<String, HashMap<Double, ArrayList<RGridView>>> gridMapList) {
		this.gridMapList = gridMapList;
	}

	public DataEncapsulation(UserView userview, Connection conn) {
        this.userview = userview;
        this.conn = conn;
        gridMapList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();
        gridTopIndexList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();
        lastGridTopList=new HashMap<String, HashMap<Double,ArrayList<RGridView>>>();
    }   

	//获得Grid的最右边的位置一定位显示时间的位置
	public int getMaxRight(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select max(rleft + rwidth) as rright from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			 int right=0;
			List rgridrightList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridrightList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridrightList.get(0);
				if("800".equals(disting_pt))
					right=(int) ((Float.parseFloat(rec.get("rright").toString())-263) * 800 / 1024);
				else	
					right=(int) (Float.parseFloat(rec.get("rright").toString())-263);
			}
			if(right<314)
				right=314;
			return right;
		} catch (Exception e) {
		} 
		return 0;
	}
//	获得Grid的最右边的位置一定位显示时间的位置
	public int getMaxRightRadio(int intTabid,int intPageId,String disting_pt)
	{
		sql.append("select max(rleft + rwidth) as rright from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int right=0;
			List rgridrightList =ExecuteSQL.executeMyQuery(sql.toString());
			sql.delete(0,sql.length());
			if(!rgridrightList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridrightList.get(0);
				if("800".equals(disting_pt))
					right=(int) ((Float.parseFloat(rec.get("rright").toString())-305) * 800 / 1024);
				else	
					right=(int) (Float.parseFloat(rec.get("rright").toString())-305);
			}
			return right;
		} catch (Exception e) {
		} 
		return 0;
	}
	public int getMaxRightddate(int intTabid,int intPageId,String disting_pt)
	{
		sql.append("select max(rleft + rwidth) as rright from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int right=0;
			List rgridrightList =ExecuteSQL.executeMyQuery(sql.toString());
			sql.delete(0,sql.length());
			if(!rgridrightList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridrightList.get(0);
				if("800".equals(disting_pt))
					right=(int) ((Float.parseFloat(rec.get("rright").toString())-245) * 800 / 1024);
				else	
					right=(int) (Float.parseFloat(rec.get("rright").toString())-245);
			}
			return right;
		} catch (Exception e) {
		} 
		return 0;
	}
	public int getMaxRightddateRadio(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select max(rleft + rwidth) as rright from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int right=0;
			List rgridrightList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridrightList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridrightList.get(0);
				if("800".equals(disting_pt))
					right=(int) ((Float.parseFloat(rec.get("rright").toString())-404) * 800 / 1024);
				else	
					right=(int) (Float.parseFloat(rec.get("rright").toString())-404);
			}
			if(right<173)
				right=173;
			return right;
		} catch (Exception e) {
		} 
		return 0;
	}
	public int getMaxRightddateSelect(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select max(rleft + rwidth) as rright from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int right=0;
			List rgridrightList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridrightList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridrightList.get(0);
				if("800".equals(disting_pt))
					right=(int) ((Float.parseFloat(rec.get("rright").toString())-554) * 800 / 1024);
				else	
					right=(int) (Float.parseFloat(rec.get("rright").toString())-554);
			}
			if(right<100)
				right=100;
			return right;
		} catch (Exception e) {
		} 
		return 0;
	}
	//获得Grid的最顶端的位置以显示页的位置
	public int getSmallestTop(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select min(rtop) as rtop from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int top=0;
			List rgridtopList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridtopList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridtopList.get(0);
				if("800".equals(disting_pt))
				  top=(int) (Float.parseFloat(rec.get("rtop").toString()) * 800 / 1024);
				else	
			      top=(int) (Float.parseFloat(rec.get("rtop").toString()));
			}
			int rPage_top=0;
			sql=new StringBuffer();
			sql.append("select min(rtop) as rtop from rPage where Tabid =");
			sql.append(intTabid);
		    sql.append(" and pageid=");		    
			sql.append(intPageId);
			ContentDAO dao=new ContentDAO(conn);
			RowSet rs=dao.search(sql.toString());			
			if(rs.next())
			{
				String rPage_top_str=rs.getString("rtop");
				if(rPage_top_str!=null&&rPage_top_str.length()>0)
				{
					if("800".equals(disting_pt))
						rPage_top=(int) (Float.parseFloat(rs.getString("rtop")) * 800 / 1024);
					else	
						rPage_top=(int) (Float.parseFloat(rs.getString("rtop")));
					if(rPage_top<top)
						top=rPage_top;
				}				
			}
			return top-22;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}
//	获得Grid的最顶端的位置以显示页的位置
	public int getDTownmostTop(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select max(rtop) as rtop from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int top=0;
			List rgridtopList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridtopList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridtopList.get(0);
				if("800".equals(disting_pt))
				  top=(int) (Float.parseFloat(rec.get("rtop").toString()) * 800 / 1024);
				else	
			      top=(int) (Float.parseFloat(rec.get("rtop").toString()));
			}	
			int rheight=0;
			sql=new StringBuffer();
			sql.append("select max(rheight) as rheight from RGrid where Tabid =");
			sql.append(intTabid);
		    sql.append(" and pageid=");		    
			sql.append(intPageId);
			sql.append(" and rtop="+top);
			ContentDAO dao=new ContentDAO(conn);
			RowSet rs=dao.search(sql.toString());			
			if(rs.next())
			{
				String rPage_top_str=rs.getString("rheight");
				if(rPage_top_str!=null&&rPage_top_str.length()>0)
				{
					if("800".equals(disting_pt))
						rheight=(int) (Float.parseFloat(rs.getString("rheight")) * 800 / 1024);
					else	
						rheight=(int) (Float.parseFloat(rs.getString("rheight")));
					top=top+rheight;
				}				
			}
			int rPage_top=0;
			sql=new StringBuffer();
			sql.append("select max(rtop) as rtop from rPage where Tabid =");
			sql.append(intTabid);
		    sql.append(" and pageid=");		    
			sql.append(intPageId);
			rs=dao.search(sql.toString());			
			if(rs.next())
			{
				String rPage_top_str=rs.getString("rtop");
				if(rPage_top_str!=null&&rPage_top_str.length()>0)
				{
					if("800".equals(disting_pt))
						rPage_top=(int) (Float.parseFloat(rs.getString("rtop")) * 800 / 1024);
					else	
						rPage_top=(int) (Float.parseFloat(rs.getString("rtop")));					
				}				
			}
			int rPage_height=0;
			sql=new StringBuffer();
			sql.append("select max(rheight) as rheight from rPage where Tabid =");
			sql.append(intTabid);
		    sql.append(" and pageid=");		    
			sql.append(intPageId);
			sql.append(" and rtop="+rPage_top);
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				String rPage_height_str=rs.getString("rheight");
				if(rPage_height_str!=null&&rPage_height_str.length()>0)
				{
					if("800".equals(disting_pt))
						rPage_height=(int) (Float.parseFloat(rs.getString("rheight")) * 800 / 1024);
					else	
						rPage_height=(int) (Float.parseFloat(rs.getString("rheight")));
					rPage_top=rPage_top+rPage_height;
				}				
			}
			if(top<rPage_top)
				top=rPage_top;
			return top+22;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}
    //获得Grid的最左边的位置以显示页的位置
	public int getSmallestLeft(int intTabid,int intPageId,String disting_pt,Connection conn)
	{
		sql.append("select min(rleft) as rleft from RGrid where Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		try {
			int left=0;
			List rgridleftList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rgridleftList.isEmpty())
			{
				LazyDynaBean rec=(LazyDynaBean)rgridleftList.get(0);
			   if("800".equals(disting_pt))
				  left=(int) (Float.parseFloat(rec.get("rleft").toString()) * 800 / 1024);
				else	
			      left=(int) (Float.parseFloat(rec.get("rleft").toString()));
			}
			return left;
		} catch (Exception e) {
		} 
		return 0;
	}
	//封装Rname表中的数据
	public List getRname(int strTabid,Connection conn) {
		sql.append("select * from rname where Tabid=");
		sql.append(strTabid);
		try {
			List rnameList =ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			return rnameList;
		} catch (Exception e) {
		} 
		return null;
	}
	//封装RTITLE中的数据
	public List getRtitle(int strTabid){
		sql.append("select * from rTitle where Tabid=");
		sql.append(strTabid);
		
		try {
			List rtitleList=ExecuteSQL.executeMyQuery(sql.toString());
			sql.delete(0,sql.length());
			return rtitleList;
		} catch (Exception e) {
		} 
		return null;
	}
	//封装Rpage表中的数据
	public List getRpage(int strTabid, int intPageId,Connection conn){
		sql.delete(0, sql.length());
		sql.append("select * from rPage where (Tabid=");
		sql.append(strTabid);
		sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(") order by rtop ,rleft");
		try {
			List  rpageList =ExecuteSQL.executeMyQuery(sql.toString(),conn);		
			List rgrids=new ArrayList();
			if(!rpageList.isEmpty())
			{
				int lastRtop=-1;//27483 组织机构-岗位说明书标题不完全对其，导出excel后  上下错误很多。
				for(int i=0;i<rpageList.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)rpageList.get(i);
					RPageView rpage = new RPageView();
					rpage.setFlag(rec.get("flag").toString()!=null?Integer.parseInt(rec.get("flag").toString()):0);
					rpage.setFonteffect(rec.get("fonteffect").toString());
					rpage.setFontname(rec.get("fontname").toString());
					rpage.setFontsize(rec.get("fontsize").toString());
					rpage.setGridno(rec.get("gridno").toString());
					rpage.setHz(rec.get("hz").toString());
					rpage.setPageid(rec.get("pageid").toString());
					rpage.setRheight(rec.get("rheight").toString());
					rpage.setRleft(rec.get("rleft").toString());
					if(lastRtop!=-1&&Math.abs(Integer.parseInt(rec.get("rtop").toString())-lastRtop)<=4)
						rpage.setRtop(lastRtop+"");
					else
						rpage.setRtop(rec.get("rtop").toString());
					rpage.setRwidth(rec.get("rwidth").toString());
					rpage.setTabid(rec.get("tabid").toString());
					rpage.setExtendAttr(rec.get("extendattr").toString());					
					rgrids.add(rpage);
					lastRtop=Integer.parseInt(rec.get("rtop").toString());
				}
			}
			return rgrids;
		} catch (Exception e) {
		}
		return null;
	}
//	封装Rgrid表中的数据
	public List getRgridIsbrokenline (int intTabid, int intPageId,Connection conn){
		String minleft="";
		String mintop="";
		sql.append("select min(rleft) as rleft,min(rtop) as rtop from RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(")");
    	List rgridminlefttop=ExecuteSQL.executeMyQuery(sql.toString(),conn);
		if(!rgridminlefttop.isEmpty())
		{
			LazyDynaBean rec=(LazyDynaBean)rgridminlefttop.get(0);
			minleft=rec.get("rleft")!=null?rec.get("rleft").toString():"";
			mintop=rec.get("rtop")!=null?rec.get("rtop").toString():"";
		}
		sql.delete(0,sql.length());
		sql.append("SELECT * FROM RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(" and (l=0 or t=0 or r=0 or b=0))");
			try {	
			List rgridList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			List rgrids=new ArrayList();
			if(!rgridList.isEmpty())
			{
				for(int i=0;i<rgridList.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)rgridList.get(i);
					RGridView rgrid = new RGridView();
					rgrid.setAlign(rec.get("align")!=null?rec.get("align").toString():"");
					rgrid.setB(rec.get("b")!=null?rec.get("b").toString():"");
					rgrid.setBsize(rec.get("bsize")!=null?rec.get("bsize").toString():"");
					rgrid.setCexpress(rec.get("cexpress")!=null?rec.get("cexpress").toString():"");
					rgrid.setIsView(getIsViewForCexpress(rgrid.getCexpress()));
					rgrid.setCHz(rec.get("hz")!=null?rec.get("hz").toString():"");
					rgrid.setCodeid(rec.get("codeid")!=null?rec.get("codeid").toString():"");
					rgrid.setCSetName(rec.get("setname")!=null?rec.get("setname").toString():"");
					rgrid.setField_hz(rec.get("field_hz")!=null?rec.get("field_hz").toString():"");
					rgrid.setField_name(rec.get("field_name")!=null?rec.get("field_name").toString():"");
					rgrid.setField_type(rec.get("field_type")!=null?rec.get("field_type").toString():"");
					rgrid.setFlag(rec.get("flag")!=null?rec.get("flag").toString():"");
					rgrid.setFonteffect(rec.get("fonteffect")!=null?rec.get("fonteffect").toString():"");
					rgrid.setFontName(rec.get("fontname")!=null?rec.get("fontname").toString():"");
					rgrid.setFontsize(rec.get("fontsize")!=null?rec.get("fontsize").toString():"");
					rgrid.setGridno(rec.get("gridno")!=null?rec.get("gridno").toString():"");
					rgrid.setL(rec.get("l")!=null?rec.get("l").toString():"");
					rgrid.setLsize(rec.get("lsize")!=null?rec.get("lsize").toString():"");
					rgrid.setMode(rec.get("mode")!=null?rec.get("mode").toString():"");
					//System.out.println("wlh" + rec.get("mode")!=null?rec.get("mode").toString():"");
					rgrid.setNHide(rec.get("nhide")!=null?rec.get("nhide").toString():"");
					rgrid.setPageId(rec.get("pageid")!=null?rec.get("pageid").toString():"");
					
					rgrid.setQuerycond(rec.get("querycond") !=null?rec.get("querycond").toString():"");
					rgrid.setR(rec.get("r")!=null?rec.get("r").toString():"");
	    			rgrid.setRcount(rec.get("rcount").toString()!=null?Integer.parseInt(rec.get("rcount").toString()):0);
					rgrid.setRheight(rec.get("rheight")!=null?rec.get("rheight").toString():"");
					rgrid.setRleft(rec.get("rleft")!=null?rec.get("rleft").toString():"");
					rgrid.setRsize(rec.get("rsize")!=null?rec.get("rsize").toString():"");
					rgrid.setRtop(rec.get("rtop")!=null?rec.get("rtop").toString():"");
					rgrid.setRwidth(rec.get("rwidth")!=null?rec.get("rwidth").toString():"");
					
					rgrid.setSL(rec.get("sl")!=null?rec.get("sl").toString():"");
					rgrid.setSlope(rec.get("slope").toString()!=null?Integer.parseInt(rec.get("slope").toString()):0);
					rgrid.setStrPre(rec.get("strpre")!=null?rec.get("strpre").toString():"");
					rgrid.setT(rec.get("t")!=null?rec.get("t").toString():"");
					rgrid.setTabid(rec.get("tabid")!=null?rec.get("tabid").toString():"");
					rgrid.setTsize(rec.get("tsize")!=null?rec.get("tsize").toString():"");
					rgrid.setMinrleft(minleft);
					rgrid.setMinrtop(mintop);
					rgrids.add(rgrid);
				}
			}
			return rgrids;
		} catch (Exception e) {
		} 
		return null;
	}
	
	//封装Rgrid表中的数据
	public List getRgridIsNotbrokenline (int intTabid, int intPageId,Connection conn){
		String minleft="";
		String mintop="";
		sql.append("select min(rleft) as rleft,min(rtop) as rtop from RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(")");
		List rgridminlefttop=ExecuteSQL.executeMyQuery(sql.toString(),conn);
		if(!rgridminlefttop.isEmpty())
		{
			LazyDynaBean rec=(LazyDynaBean)rgridminlefttop.get(0);
			minleft=rec.get("rleft")!=null?rec.get("rleft").toString():"";
			mintop=rec.get("rtop")!=null?rec.get("rtop").toString():"";
		}
		sql.delete(0,sql.length());
		sql.append("SELECT * FROM RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(" and l<>0 and r<>0 and t<>0 and b<>0)");
			try {	
			List rgridList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			List rgrids=new ArrayList();
			if(!rgridList.isEmpty())
			{
				for(int i=0;i<rgridList.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)rgridList.get(i);
					RGridView rgrid = new RGridView();
					rgrid.setAlign(rec.get("align")!=null?rec.get("align").toString():"");
					rgrid.setB(rec.get("b")!=null?rec.get("b").toString():"");
					rgrid.setBsize(rec.get("bsize")!=null?rec.get("bsize").toString():"");
					rgrid.setCexpress(rec.get("cexpress")!=null?rec.get("cexpress").toString():"");
					rgrid.setIsView(getIsViewForCexpress(rgrid.getCexpress()));
					rgrid.setCHz(rec.get("hz")!=null?rec.get("hz").toString():"");
					rgrid.setCodeid(rec.get("codeid")!=null?rec.get("codeid").toString():"");
					rgrid.setCSetName(rec.get("setname")!=null?rec.get("setname").toString():"");
					rgrid.setField_hz(rec.get("field_hz")!=null?rec.get("field_hz").toString():"");
					rgrid.setField_name(rec.get("field_name")!=null?rec.get("field_name").toString():"");
					rgrid.setField_type(rec.get("field_type")!=null?rec.get("field_type").toString():"");
					rgrid.setFlag(rec.get("flag")!=null?rec.get("flag").toString():"");
					rgrid.setFonteffect(rec.get("fonteffect")!=null?rec.get("fonteffect").toString():"");
					rgrid.setFontName(rec.get("fontname")!=null?rec.get("fontname").toString():"");
					rgrid.setFontsize(rec.get("fontsize")!=null?rec.get("fontsize").toString():"");
					rgrid.setGridno(rec.get("gridno")!=null?rec.get("gridno").toString():"");
					rgrid.setL(rec.get("l")!=null?rec.get("l").toString():"");
					rgrid.setLsize(rec.get("lsize")!=null?rec.get("lsize").toString():"");
					rgrid.setMode(rec.get("mode")!=null?rec.get("mode").toString():"");
					rgrid.setNHide(rec.get("nhide")!=null?rec.get("nhide").toString():"");
					rgrid.setPageId(rec.get("pageid")!=null?rec.get("pageid").toString():"");
					
					rgrid.setQuerycond(rec.get("querycond") !=null?rec.get("querycond").toString():"");
					rgrid.setR(rec.get("r")!=null?rec.get("r").toString():"");
	    			rgrid.setRcount(rec.get("rcount").toString()!=null?Integer.parseInt(rec.get("rcount").toString()):0);
					rgrid.setRheight(rec.get("rheight")!=null?rec.get("rheight").toString():"");
					rgrid.setRleft(rec.get("rleft")!=null?rec.get("rleft").toString():"");
					rgrid.setRsize(rec.get("rsize")!=null?rec.get("rsize").toString():"");
					rgrid.setRtop(rec.get("rtop")!=null?rec.get("rtop").toString():"");
					rgrid.setRwidth(rec.get("rwidth")!=null?rec.get("rwidth").toString():"");
					
					rgrid.setSL(rec.get("sl")!=null?rec.get("sl").toString():"");
					rgrid.setSlope(rec.get("slope").toString()!=null?Integer.parseInt(rec.get("slope").toString()):0);
					rgrid.setStrPre(rec.get("strpre")!=null?rec.get("strpre").toString():"");
					rgrid.setT(rec.get("t")!=null?rec.get("t").toString():"");
					rgrid.setTabid(rec.get("tabid")!=null?rec.get("tabid").toString():"");
					rgrid.setTsize(rec.get("tsize")!=null?rec.get("tsize").toString():"");
					rgrid.setSub_domain(rec.get("sub_domain")!=null?rec.get("sub_domain").toString():"");
					rgrid.setMinrleft(minleft);
					rgrid.setMinrtop(mintop);
					rgrids.add(rgrid);
				}
			}
			return rgrids;
		} catch (Exception e) {
		} 
		return null;
	}
	/** 
	 * 封装Rgrid表中的数据
	 * 按rtop,rleft排序
	 * @param intTabid
	 * @param intPageId
	 * @param conn
	 * @return
	 */
	public List getRgrid(int intTabid, int intPageId,Connection conn) throws Exception{
		String minleft="";
		String mintop="";
		sql.setLength(0);
		/*sql.append("select min(rleft) as rleft,min(rtop) as rtop from RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(")");
		List rgridminlefttop=ExecuteSQL.executeMyQuery(sql.toString(),conn);*/
		List rgridminlefttop=YkcardStaticBo.getRgridMinLeftTop(intTabid+"", intPageId+"", conn);
		if(!rgridminlefttop.isEmpty())
		{
			LazyDynaBean rec=(LazyDynaBean)rgridminlefttop.get(0);
			minleft=rec.get("rleft")!=null?rec.get("rleft").toString():"";
			mintop=rec.get("rtop")!=null?rec.get("rtop").toString():"";
		}
		/*sql.delete(0,sql.length());
		sql.append("SELECT * FROM RGrid	WHERE (Tabid =");
		sql.append(intTabid);
	    sql.append(" and pageid=");
		sql.append(intPageId);
		sql.append(") and rwidth<>0 order by rtop,rleft");*/
		//System.out.println(sql);
			try {	
		    ContentDAO dao=new ContentDAO(conn);
			//List rgridList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
		    List rgridList=YkcardStaticBo.getRgridList(intTabid+"", intPageId+"", conn);
		    //rs=dao.search(sql.toString());
			sql.delete(0,sql.length());
			List rgrids=new ArrayList();
			ArrayList op_list=new ArrayList();
			if(!rgridList.isEmpty())
			{
				for(int i=0;i<rgridList.size();i++)
				{
					LazyDynaBean rec=(LazyDynaBean)rgridList.get(i);
					RGridView rgrid = new RGridView();
					rgrid.setAlign(rec.get("align")!=null?rec.get("align").toString():"");					
					rgrid.setBsize(rec.get("bsize")!=null?rec.get("bsize").toString():"");
					rgrid.setCexpress(rec.get("cexpress")!=null?rec.get("cexpress").toString():"");
					rgrid.setIsView(getIsViewForCexpress(rgrid.getCexpress()));
					rgrid.setCHz(rec.get("hz")!=null?rec.get("hz").toString():"");
					rgrid.setCodeid(rec.get("codeid")!=null?rec.get("codeid").toString():"");
					rgrid.setCSetName(rec.get("setname")!=null?rec.get("setname").toString():"");
					rgrid.setField_hz(rec.get("field_hz")!=null?rec.get("field_hz").toString():"");
					rgrid.setField_name(rec.get("field_name")!=null?rec.get("field_name").toString():"");
					rgrid.setField_type(rec.get("field_type")!=null?rec.get("field_type").toString():"");
					rgrid.setFlag(rec.get("flag")!=null?rec.get("flag").toString():"");
					rgrid.setFonteffect(rec.get("fonteffect")!=null?rec.get("fonteffect").toString():"");
					rgrid.setFontName(rec.get("fontname")!=null?rec.get("fontname").toString():"");
					rgrid.setFontsize(rec.get("fontsize")!=null?rec.get("fontsize").toString():"");
					rgrid.setGridno(rec.get("gridno")!=null?rec.get("gridno").toString():"");					
					rgrid.setLsize(rec.get("lsize")!=null?rec.get("lsize").toString():"");
					switch(Sql_switcher.searchDbServerFlag())
					{
					  case Constant.MSSQL:
					  {
						  rgrid.setMode(rec.get("mode")!=null?rec.get("mode").toString():"");
						 break;
					  }
					  case Constant.DB2:
					  {
						 rgrid.setMode(rec.get("mode")!=null?rec.get("mode").toString():"");
						break;
					  }
					  case Constant.ORACEL:
					  {
						  rgrid.setMode(rec.get("mode_o")!=null?rec.get("mode_o").toString():"");
						  break;
					  }
					  case Constant.DAMENG:
					  {
						  rgrid.setMode(rec.get("mode_o")!=null?rec.get("mode_o").toString():"");
						  break;
					  }	  
					}					
					rgrid.setNHide(rec.get("nhide")!=null?rec.get("nhide").toString():"");
					rgrid.setPageId(rec.get("pageid")!=null?rec.get("pageid").toString():"");
					
					rgrid.setQuerycond(rec.get("querycond") !=null?rec.get("querycond").toString():"");
					
	    			rgrid.setRcount(rec.get("rcount").toString()!=null?Integer.parseInt(rec.get("rcount").toString()):0);
					rgrid.setRheight(rec.get("rheight")!=null?rec.get("rheight").toString():"");
					rgrid.setRleft(rec.get("rleft")!=null?rec.get("rleft").toString():"");
					rgrid.setRsize(rec.get("rsize")!=null?rec.get("rsize").toString():"");
					rgrid.setRtop(rec.get("rtop")!=null?rec.get("rtop").toString():"");
					rgrid.setRwidth(rec.get("rwidth")!=null?rec.get("rwidth").toString():"");
					
					rgrid.setSL(rec.get("sl")!=null?rec.get("sl").toString():"");
					rgrid.setSlope(rec.get("slope").toString()!=null?Integer.parseInt(rec.get("slope").toString()):0);
					rgrid.setStrPre(rec.get("strpre")!=null?rec.get("strpre").toString():"");
					
					rgrid.setTabid(rec.get("tabid")!=null?rec.get("tabid").toString():"");
					rgrid.setTsize(rec.get("tsize")!=null?rec.get("tsize").toString():"");					
					rgrid.setSub_domain(rec.get("sub_domain")!=null?rec.get("sub_domain").toString():"");
					rgrid.setSubflag(rec.get("subflag")!=null?rec.get("subflag").toString():"");
					rgrid.setB(rec.get("b")!=null?rec.get("b").toString():"");
					//rgrid.setB(getRline(intTabid,intPageId,"b",rgrid.getB(),rgrid,dao));					
					rgrid.setL(rec.get("l")!=null?rec.get("l").toString():"");
					//rgrid.setL(getRline(intTabid,intPageId,"l",rgrid.getL(),rgrid,dao));
					rgrid.setR(rec.get("r")!=null?rec.get("r").toString():"");
					//rgrid.setR(getRline(intTabid,intPageId,"r",rgrid.getR(),rgrid,dao));
					rgrid.setT(rec.get("t")!=null?rec.get("t").toString():"");
					//rgrid.setT(getRline(intTabid,intPageId,"t",rgrid.getT(),rgrid,dao));
					rgrid.setMinrleft(minleft);
					rgrid.setMinrtop(mintop);					
					rgrids.add(rgrid);
					RGridView op_rgrid=(RGridView)rgrid.clone();
					double index=0;
					index=Double.parseDouble(rec.get("rleft").toString())+Double.parseDouble(rec.get("rwidth").toString());
					if(gridMapList!=null&&gridMapList.get(intPageId+"")!=null) {
						HashMap<Double,ArrayList<RGridView>> gridlist=gridMapList.get(intPageId+"");
						if(gridlist!=null) {
							if(gridlist.get(index)!=null) {
								gridlist.get(index).add(op_rgrid);
							}else {
								ArrayList<RGridView> list=new ArrayList<RGridView>();
								list.add(op_rgrid);
								gridlist.put(index, list);
							}
							gridMapList.put(intPageId+"", gridlist);
						}else {
							gridlist=new HashMap<Double, ArrayList<RGridView>>();
							ArrayList<RGridView> list=new ArrayList<RGridView>();
							list.add(op_rgrid);
							gridlist.put(index, list);
							gridMapList.put(intPageId+"", gridlist);
						}
					}else {
						HashMap<Double,ArrayList<RGridView>> gridlist=new HashMap<Double, ArrayList<RGridView>>();
						ArrayList<RGridView> list=new ArrayList<RGridView>();
						list.add(op_rgrid);
						gridlist.put(index, list);
						gridMapList.put(intPageId+"", gridlist);
					}
					double topIndex=0;
					topIndex=Double.parseDouble(rec.get("rtop").toString());
					if(gridTopIndexList!=null&&gridTopIndexList.get(intPageId+"")!=null) {
						HashMap<Double,ArrayList<RGridView>> gridToplist=gridTopIndexList.get(intPageId+"");
						if(gridToplist!=null) {
							if(gridToplist.get(topIndex)!=null) {
								gridToplist.get(topIndex).add(op_rgrid);
							}else {
								ArrayList<RGridView> list=new ArrayList<RGridView>();
								list.add(op_rgrid);
								gridToplist.put(topIndex, list);
							}
							gridTopIndexList.put(intPageId+"", gridToplist);
						}else {
							gridToplist=new HashMap<Double, ArrayList<RGridView>>();
							ArrayList<RGridView> list=new ArrayList<RGridView>();
							list.add(op_rgrid);
							gridToplist.put(topIndex, list);
							gridTopIndexList.put(intPageId+"", gridToplist);
						}
					}else {
						HashMap<Double,ArrayList<RGridView>> gridToplist=new HashMap<Double, ArrayList<RGridView>>();
						ArrayList<RGridView> list=new ArrayList<RGridView>();
						list.add(op_rgrid);
						gridToplist.put(topIndex, list);
						gridTopIndexList.put(intPageId+"", gridToplist);
					}
					
					topIndex=Double.parseDouble(rec.get("rheight").toString())+Double.parseDouble(rec.get("rtop").toString());
					if(topIndex>0) {
						if(lastGridTopList!=null&&lastGridTopList.get(intPageId+"")!=null) {
							HashMap<Double,ArrayList<RGridView>> gridToplist=lastGridTopList.get(intPageId+"");
							if(gridToplist!=null) {
								if(gridToplist.get(topIndex)!=null) {
									gridToplist.get(topIndex).add(op_rgrid);
								}else {
									ArrayList<RGridView> list=new ArrayList<RGridView>();
									list.add(op_rgrid);
									gridToplist.put(topIndex, list);
								}
								lastGridTopList.put(intPageId+"", gridToplist);
							}else {
								gridToplist=new HashMap<Double, ArrayList<RGridView>>();
								ArrayList<RGridView> list=new ArrayList<RGridView>();
								list.add(op_rgrid);
								gridToplist.put(topIndex, list);
								lastGridTopList.put(intPageId+"", gridToplist);
							}
						}else {
							HashMap<Double,ArrayList<RGridView>> gridToplist=new HashMap<Double, ArrayList<RGridView>>();
							ArrayList<RGridView> list=new ArrayList<RGridView>();
							list.add(op_rgrid);
							gridToplist.put(topIndex, list);
							lastGridTopList.put(intPageId+"", gridToplist);
						}
					}					
					op_list.add(op_rgrid);
				}
			}
			
			List new_rgrids=new ArrayList();
			String b="";
			String l="";
			String r="";
			String t="";
			for(int i=0;i<op_list.size();i++)
			{
				RGridView rgrid =(RGridView)op_list.get(i);  
				b=getRlineForList(rgrids,"b",rgrid.getB(),rgrid,dao);
				l=getRlineForList(rgrids,"l",rgrid.getL(),rgrid,dao);
				r=getRlineForList(rgrids,"r",rgrid.getR(),rgrid,dao);
				t=getRlineForList(rgrids,"t",rgrid.getT(),rgrid,dao);
				rgrid.setB(b);					
				rgrid.setL(l);
				rgrid.setR(r);
				rgrid.setT(t);				
				new_rgrids.add(rgrid);
			}
			return new_rgrids;
			//return rgrids;
		} catch (Exception e) {
			e.printStackTrace();
		} finally
		{
			/*try {
				if(rs!=null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
		return null;
	}
	//获得Page页面的得大小
	public int getPagesize(int intTabid, int intPageId,Connection conn){
		sql.append("SELECT Rtop, RHeight FROM RGrid WHERE (Tabid =");
		sql.append(intTabid);
		sql.append(") AND (PageId =");
		sql.append(intPageId);
		sql.append(") AND (Rtop =  (SELECT MAX(rtop) FROM rgrid WHERE tabid =");
		sql.append(intTabid);
		sql.append(" AND pageid =");
		sql.append(intPageId);
		sql.append("))");
		int pagesize = 0;
		try {
			List rs = ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			if(!rs.isEmpty())
			{
				LazyDynaBean rec = (LazyDynaBean)rs.get(0);
				pagesize += Integer.parseInt(rec.get("rtop").toString());
				pagesize += Integer.parseInt(rec.get("rheight").toString());
				pagesize += 50;
			}			
		} catch (Exception e) {
		} 
		return pagesize;

	}
    //获得登记表的页数
	public List getPagecount(int intTabid,Connection conn) throws Exception {
		sql.append("select * from rTitle where Tabid=");
		sql.append(intTabid);
		try {
			List rs = ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			return rs;
		} catch (Exception e) {
		}
		return null;
	}
	//获得页面的表头信息
	public String getPageTitle(
	    int pageid,
		int flag,
		String hz,
		String nId,
		String userbase,
		int tabid,
		String infokind,String extendattr)
		throws Exception {
		String pagetitle = "";		
		sql.delete(0, sql.length());
		switch (flag) {
			case 0 :
				{
					if (hz.indexOf("-") != -1) {
						//for (int i = 0; i < hz.length(); i++)
							//pagetitle += "&shy;";
						//	pagetitle+="-";
						pagetitle = hz;
					} else if (hz.indexOf("——") != -1) {
						//for (int i = 0; i < hz.length(); i++)
						//	pagetitle+="-";
							//pagetitle += "&macr;";
						pagetitle = hz;
					} else {
						pagetitle = hz;
					}

					break;
				}
			case 1 :
				{
					
					int wcYear = Calendar.getInstance().get(Calendar.YEAR);
					//获得当前年
					int wcMonth =
						Calendar.getInstance().get(Calendar.MONTH) + 1;
					//获得当前月
					int wcDay = Calendar.getInstance().get(Calendar.DATE);
					//获得当前日
					String months="";
					String days="";
				    if(wcMonth<10)
						months="0"+wcMonth;
					else
						months=wcMonth+"";
					if(wcDay<10)
						days="0"+wcDay;
					else
						days=wcDay+"";
					if(extendattr!=null&&extendattr.length()>0)
					{
						String format="";
						String prefix="";
						if(extendattr.indexOf("<format>")!=-1&&extendattr.indexOf("</format>")!=-1)
						{
							format=extendattr.substring(extendattr.indexOf("<format>")+8,extendattr.indexOf("</format>"));
						}
						if(extendattr.indexOf("<prefix>")!=-1&&extendattr.indexOf("</prefix>")!=-1)
						{
							prefix=extendattr.substring(extendattr.indexOf("<prefix>")+8,extendattr.indexOf("</prefix>"));
						}
						if(format==null||format.length()<=0)
							format="4";
						String titlestr="";
						if(prefix!=null||prefix.length()<=0)
							prefix=prefix+"";
						else
							prefix="";
						int formatInt=Integer.parseInt(format);
						switch (formatInt) {
						   case 0 ://1991.12.3
						   {
							   pagetitle=prefix+wcYear + "." + wcMonth + "." + wcDay;
							   break;  
						   }
						   case 1 :// 1990.01.01
						   {
							   
							   pagetitle=prefix+wcYear + "." +months  + "." + days;  
							   break;  
						   }
						   case 2 ://2: 1990年2月10日
						   {
							   pagetitle=prefix+wcYear + "年" + wcMonth + "月" + wcDay+"日";
							   break;  
						   }
						   case 3 ://3: 1990年01月01日							  
						   {
							   pagetitle=prefix+wcYear + "年" +months  + "月" + days+"日";  
							 break;  
						   }
						   case 4 :// 4: 1991-12-3
						   {
							   pagetitle=prefix+wcYear + "-" +wcMonth  + "-" + wcDay; 
							   break;  
						   }
						   case 5 ://5: 1990-01-01
						   {
							   pagetitle=prefix+wcYear + "-" +months  + "-" + days; 
							   break;  
						   }
						   case 6 :
						   {
						       pagetitle=prefix+wcYear + "年" + wcMonth + "月";
                               break;  
						   }
						}
					}
					else
					   pagetitle = "制表日期:" + wcYear + "-" + wcMonth + "-" + wcDay;
					
					break;
				}
			case 2 :
				{
					Date date=new Date();
					pagetitle =DateUtils.format(date,"HH:mm:ss");						
					break;
				}
			case 3 :
				{
			      if("1".equalsIgnoreCase(infokind))
			      {
					sql.append("SELECT CreateUserName FROM ");
					sql.append(userbase);
					sql.append("A01 where A0100 ='");
					sql.append(nId);
					sql.append("'");
			      }
			      else if("2".equalsIgnoreCase(infokind))
			      {
			    	  sql.append("SELECT CreateUserName FROM ");
					  sql.append("B01 where b0110 ='");
					  sql.append(nId);
					  sql.append("'");
			      }else if("4".equalsIgnoreCase(infokind))
			      {
			    	  sql.append("SELECT CreateUserName FROM ");
					  sql.append("K01 where e01a1 ='");
					  sql.append(nId);
					  sql.append("'");
			      }
			      else
			      {
			    	    sql.append("SELECT CreateUserName FROM ");
						sql.append(userbase);
						sql.append("A01 where A0100 ='");
						sql.append(nId);
						sql.append("'");
			      }
					try {
						String prefix="";						
						if(extendattr.indexOf("<prefix>")!=-1&&extendattr.indexOf("</prefix>")!=-1)
						{
							prefix=extendattr.substring(extendattr.indexOf("<prefix>")+8,extendattr.indexOf("</prefix>"));
						}
						if(prefix==null||prefix.length()<=0)
							prefix="";//前缀符为空时不加默认制表人
						if(this.userview!=null)
						{
							pagetitle=prefix+/*":" +*/ this.userview.getUserFullName();
						}else
						{
							List rs =ExecuteSQL.executeMyQuery(sql.toString());						
							sql.delete(0,sql.length());
							if(!rs.isEmpty())
							{
								LazyDynaBean rec=(LazyDynaBean)rs.get(0);
								pagetitle=prefix+/*":" +*/ rec.get("createusername").toString();
							}
						}
						
					  } catch (Exception e) {
						  //e.printStackTrace();
					  }
					break;
				}
			case 4 :
				{
					sql.append("SELECT count(*) as pagecount FROM Rtitle WHERE Tabid =");
					sql.append(tabid);
					try {
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						sql.delete(0,sql.length());
						if (!rs.isEmpty()) {
							LazyDynaBean rec=(LazyDynaBean)rs.get(0);
							pagetitle = "共" + rec.get("pagecount").toString() + "页";
						}
					} catch (Exception e) {
					} 
					//System.out.println("sss" + pagetitle);
					break;
				}
			case 5 :
				{
				    pageid=pageid+1;
					pagetitle = "第" + pageid + "页";
					break;
				}
			case 6 :
				{
					
					break;
				}
		}
		return pagetitle;
	}
	public List GetSets(int intTabid,int intPageId,Connection conn)
	{
		try {
			return YkcardStaticBo.getGridSets(intTabid+"", intPageId+"", conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	  /*sql.delete(0, sql.length());
	  sql.append("SELECT ");
      sql.append("fieldSet.Displayorder, fieldSet.fieldSetId, fieldSet.fieldSetDesc,");
	  sql.append(" fieldSet.UseFlag, fieldSet.ModuleFlag, fieldSet.changeFlag, ");
	  sql.append("fieldSet.customdesc, fieldSet.reserveitem");
	  sql.append(" FROM fieldSet where fieldSet.fieldSetId in(Select RGrid.setName From ");
	  sql.append("RGrid WHERE (RGrid.Tabid = ");
	  sql.append(intTabid);
	  sql.append(") AND (RGrid.PageId = ");
	  sql.append(intPageId);
	  sql.append("))");
		   try{
			List fieldList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
		    return fieldList;
		   }catch(Exception e){
		   }
		   return null;*/
	  }
	public List GetSetsPDF(int intTabid,Connection conn)
	{
	  sql.delete(0, sql.length());
      sql.append("SELECT ");
      sql.append("fieldSet.Displayorder, fieldSet.fieldSetId, fieldSet.fieldSetDesc,");
	  sql.append(" fieldSet.UseFlag, fieldSet.ModuleFlag, fieldSet.changeFlag, ");
	  sql.append("fieldSet.customdesc, fieldSet.reserveitem");
	  sql.append(" FROM fieldSet where fieldSet.fieldSetId in(Select RGrid.setName From ");
	  sql.append("RGrid WHERE (RGrid.Tabid = ");
	  sql.append(intTabid);
	  sql.append("))");
		   try{
			List fieldList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
		    return fieldList;
		   }catch(Exception e){
		   }
		   return null;
	  }
	/**
	 * 生成字体样式,解决中文问题
	 * 
	 */
	public Font getFont(String fontfamilyname,String fontEffect,int fontSize)
	{
		BaseFont bfComic=null;		
		Font font=null;
		try
		{
			//字体效果 =0,=1 正常式样 =2,粗体 =3,斜体  =4,斜粗体	
	    	if(FontFamilyType.getFontFamilyTTF(fontfamilyname)==null)
				bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
			else
				bfComic=BaseFont.createFont(FontFamilyType.getFontFamilyTTF(fontfamilyname), BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);   	
			if("2".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+3, Font.BOLD);
			}
			if("3".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+3, Font.ITALIC);
			}
			if("4".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+3, Font.BOLD | Font.ITALIC);
			}
			else
			{
				font=new Font(bfComic,fontSize+3, Font.NORMAL);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return font;
	}
	/**
	 * 生成字体样式,解决中文问题
	 * 
	 */
	public Font getFont(String fontEffect,int fontSize)
	{
		Font font=null;
		try
		{
			//字体效果 =0,=1 正常式样 =2,粗体 =3,斜体  =4,斜粗体			
			BaseFont bfComic = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   //解决中文问题		
			if("2".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.BOLD);
			}
			if("3".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.ITALIC);
			}
			if("4".equals(fontEffect))
			{
				font=new Font(bfComic,fontSize+2, Font.BOLD | Font.ITALIC);
			}
			else
			{
				font=new Font(bfComic,fontSize+2, Font.NORMAL);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return font;
		
	}	
	/**
	 * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public  String round(String v,int scale){

        if(scale<0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
            }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
    }
	 /**
     * 根据人员库前缀和人员编码生成其对应的文件
     * 
     * @param userTable
     *            应用库 usra01
     * @param userNumber
     *            0000001 ,a0100
     * @param flag
     *            'P'照片
     * @param session
     * @return
     * @throws Exception
     */
    public  String createPhotoFile(String userTable, String userNumber,
            String flag,Connection conn) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;  
        ContentDAO dao = null;
        InputStream in = null;
        java.io.FileOutputStream fout = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");
           
            dao = new ContentDAO(conn);
            rs = dao.search(strsql.toString());
            if (rs.next()) {
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));             
                in = rs.getBinaryStream("Ole");                
                fout = new java.io.FileOutputStream(tempFile);                
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
                }
                fout.close();
               
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(fout);
        	PubFunc.closeResource(in);
        	PubFunc.closeResource(rs);
        }
        return filename;
    }
    
    public  String createTitlePhotoFile(int strTabid,int pageid, String gridno,String ext,Connection conn) throws Exception {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;  
        ContentDAO dao = null;
        java.io.FileOutputStream fout = null;
        InputStream in = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from rPage where (Tabid=");
            strsql.append(strTabid);
            strsql.append(" and gridno=");
            strsql.append(gridno);
            strsql.append(" and pageid="+pageid);
            strsql.append(")");
           
            dao = new ContentDAO(conn);
            rs = dao.search(strsql.toString());
            if (rs.next()) {
    
                tempFile = File.createTempFile("ykt_", ext,  new File(System.getProperty("java.io.tmpdir")));             
                in = rs.getBinaryStream("content");                
                fout = new java.io.FileOutputStream(tempFile);                
                int len;
                byte[] buf = new byte[1024];
            
                while ((len = in.read(buf, 0, 1024)) != -1) {
                    fout.write(buf, 0, len);
               
                }
                fout.close();
               
                filename= tempFile.getName();                
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(fout);
        	PubFunc.closeResource(in);
        	PubFunc.closeResource(rs);
            if (rs != null)
                rs.close();
        }
        return filename;
    }
    /* 处理虚线  */
	public Rectangle getRectangle(RGridView rgrid)
	{
		Rectangle borders = new Rectangle( 0.0F,  0.0F,  0.0F,  0.0F);	      
        //c.L,c.T,c.R,c.B
		String[] ltrb=new String[4];
		ltrb[0]=rgrid.getL();
		ltrb[1]=rgrid.getT();
		ltrb[2]=rgrid.getR();
		ltrb[3]=rgrid.getB();
		if("1".equals(ltrb[1]))
		{
				borders.setBorderWidthTop(0.6f);
				//borders.setBorderColorTop(new Color(0, 0, 0));	
		}
		else
				borders.setBorderWidthTop(0f);
									  
		if("1".equals(ltrb[3]))
		{
				borders.setBorderWidthBottom(0.6f);
				//borders.setBorderColorBottom(new Color(0, 0, 0));
		}
		else
				borders.setBorderWidthBottom(0f);
									  
		if("1".equals(ltrb[0]))
		{
				borders.setBorderWidthLeft(0.6f);
				//borders.setBorderWidthTop(0f);
				//borders.setBorderColorLeft(new Color(0, 0, 0));
		}
		else
				borders.setBorderWidthLeft(0f);
									  
		if("1".equals(ltrb[2]))
		{
				borders.setBorderWidthRight(0.6f);
				//borders.setBorderWidthTop(0f);
				//borders.setBorderColorRight(new Color(0, 0, 0));
		}
		else
				borders.setBorderWidthRight(0f);

		return borders;
	}	
    public String  getRline(int tabid,int pageid ,String flag,String line,RGridView rgrid,ContentDAO dao)
    {
    	if("0".equals(line))
    		return line;
    	else
    	{
    		StringBuffer sql=new StringBuffer();
    		RowSet rs=null;
    		sql.append("select 1 from RGrid where tabid="+tabid+" and pageid="+pageid+" and gridno<>'"+rgrid.getGridno()+"'");
    	    try
    	    {   if("t".equals(flag))
    	        {
    	    	   sql.append(" and b=0");
    	    	   sql.append(" and (rtop+rheight)="+rgrid.getRtop()+" ");	
    	    	   sql.append(" and ((rleft>="+rgrid.getRleft()+" and (rleft+rwidth)<="+(Float.parseFloat(rgrid.getRleft())+Float.parseFloat(rgrid.getRwidth()))+")");
   	    		   sql.append(" or(rleft<="+rgrid.getRleft()+" and (rleft+rwidth)>="+(Float.parseFloat(rgrid.getRleft())+Float.parseFloat(rgrid.getRwidth()))+"))");
	    		   //System.out.println(sql.toString());
   	    		   rs=dao.search(sql.toString());
	    	       if(rs.next())
	    	    	line="0";
    	        }else if("b".equals(flag))
    	        {
    	        	sql.append(" and t=0");    	    		
    	    		sql.append(" and rtop=("+(Float.parseFloat(rgrid.getRtop())+Float.parseFloat(rgrid.getRheight()))+") ");
    	    		sql.append(" and ((rleft>="+rgrid.getRleft()+" and (rleft+rwidth)<="+(Float.parseFloat(rgrid.getRleft())+Float.parseFloat(rgrid.getRwidth()))+")");
    	    		sql.append(" or(rleft<="+rgrid.getRleft()+" and (rleft+rwidth)>="+(Float.parseFloat(rgrid.getRleft())+Float.parseFloat(rgrid.getRwidth()))+"))");
    	    		rs=dao.search(sql.toString());
    	    	    if(rs.next())
    	    	    	line="0";
    	    	}else if("l".equals(flag))
    	    	{
    	    		sql.append(" and r=0");
    	    		sql.append(" and (rleft+rwidth)="+rgrid.getRleft()+"");
    	    		sql.append(" and ((rtop<="+rgrid.getRtop()+" and (rtop+rheight)>="+(Float.parseFloat(rgrid.getRtop())+Float.parseFloat(rgrid.getRheight()))+")");
    	    		sql.append(" or (rtop>="+rgrid.getRtop()+" and (rtop+rheight)<="+(Float.parseFloat(rgrid.getRtop())+Float.parseFloat(rgrid.getRheight()))+"))");
    	    		rs=dao.search(sql.toString());
    	    	    if(rs.next())
    	    	    	line="0";
    	    	}else if("r".equals(flag))
    	    	{
    	    		sql.append(" and l=0");
    	    		sql.append(" and rleft="+(Float.parseFloat(rgrid.getRleft())+Float.parseFloat(rgrid.getRwidth()))+" ");
    	    		sql.append("  and ((rtop<="+rgrid.getRtop()+" and (rtop+rheight)>="+(Float.parseFloat(rgrid.getRtop())+Float.parseFloat(rgrid.getRheight()))+")");
    	    		sql.append(" or (rtop>="+rgrid.getRtop()+" and (rtop+rheight)<="+(Float.parseFloat(rgrid.getRtop())+Float.parseFloat(rgrid.getRheight()))+"))");
    	    		rs=dao.search(sql.toString());
    	    		//System.out.println(sql.toString());
    	    	    if(rs.next())
    	    	    	line="0";
    	    	}
    	    }catch(Exception e)
    	    {
    	    	e.printStackTrace();
    	    }
    	}    	
    	return line; 
    }
    public String  getRlineForList(List list,String flag,String line,RGridView cur_rgrid,ContentDAO dao)
    {
    	if("0".equals(line))
    		return line;
    	else
    	{
    		float cur_rtop=cur_rgrid.getRtop()!=null&&cur_rgrid.getRtop().length()>0?Float.parseFloat(cur_rgrid.getRtop()):0;
    		float cur_rheight=cur_rgrid.getRheight()!=null&&cur_rgrid.getRheight().length()>0?Float.parseFloat(cur_rgrid.getRheight()):0;
    		float cur_rleft=cur_rgrid.getRleft()!=null&&cur_rgrid.getRleft().length()>0?Float.parseFloat(cur_rgrid.getRleft()):0;
			float cur_rwidth=cur_rgrid.getRwidth()!=null&&cur_rgrid.getRwidth().length()>0?Float.parseFloat(cur_rgrid.getRwidth()):0;
    		RGridView rgrid;  
    		float rtop=0;
    		float rheight=0;
    		float rleft=0;
    		float rwidth=0;
    		String b="";
    		String t="";
    		String r="";
    		String l="";
    		String cur_gridno=cur_rgrid.getGridno();
    		String gridno="";
    	    try
    	    {  
    	    	for(int i=0;i<list.size();i++)
        		{
        			rgrid=(RGridView)list.get(i);  
        			rtop=rgrid.getRtop()!=null&&rgrid.getRtop().length()>0?Float.parseFloat(rgrid.getRtop()):0;
        			rheight=rgrid.getRheight()!=null&&rgrid.getRheight().length()>0?Float.parseFloat(rgrid.getRheight()):0;
        			rleft=rgrid.getRleft()!=null&&rgrid.getRleft().length()>0?Float.parseFloat(rgrid.getRleft()):0;
        			rwidth=rgrid.getRwidth()!=null&&rgrid.getRwidth().length()>0?Float.parseFloat(rgrid.getRwidth()):0;
        			gridno=rgrid.getGridno();
        			if(cur_gridno.equals(gridno)) {
        			    continue;
        			}
        			if(!rgrid.getPageId().equals(cur_rgrid.getPageId())) {
        			    continue;
        			}
        			if("t".equals(flag))
        	        {
        			   b=rgrid.getB();        			   
         	    	   if(b!=null&& "0".equals(b))
         	    	   {
         	    		 if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)))
           	    	      {
         	    			 line="0";
       	    			     break;
       	    		      }
         	    	   }
         	    	   //单元格上方是子集时 当前单元格顶部边线置0
         	    	   if("1".equals(rgrid.getSubflag())) {
         	    	      if((rtop+rheight)==cur_rtop&&((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||(rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth))) {
         	    	         line="0";
                             break;
         	    	      }
         	    	   }
        	        }else if("b".equals(flag))
        	        {
        	        	t=rgrid.getT();
        	        	if(t!=null&& "0".equals(t))
        	        	{
        	        		if(rtop==(cur_rtop+cur_rheight)&&
        	        		    ((rleft>=cur_rleft&&rleft+rwidth<=cur_rleft+cur_rwidth)||
        	        		     (rleft<=cur_rleft&&rleft+rwidth>=cur_rleft+cur_rwidth)
        	        		    )
        	        		  )
        	        		{
        	        			line="0";
          	    			     break;
        	        		}
        	        	}        	        	
        	    	}else if("l".equals(flag))
        	    	{
        	    		r=rgrid.getR();
        	    		if(r!=null&& "0".equals(r))
        	    		{
        	    			if((rleft+rwidth)==cur_rleft&&((rtop<=cur_rtop&&(rtop+rheight)>=(cur_rtop+cur_rheight))||(rtop>=cur_rtop&&(rtop+rheight)<=(cur_rtop+cur_rheight))))
        	    			{
        	    				line="0";
         	    			    break;
        	    			}
        	    		}        	    		
        	    	}else if("r".equals(flag))
        	    	{
        	    		l=rgrid.getL();
        	    		if(l!=null&& "0".equals(l))
        	    		{
        	    			if(rleft==(cur_rleft+cur_rwidth)&&((rtop<=cur_rtop&&rtop+rheight>=cur_rtop+cur_rheight)||(rtop>=cur_rtop&&rtop+rheight<=cur_rtop+cur_rheight)))
        	    			{
        	    				line="0";
        	    			    break;
        	    			}
        	    		}
        	    	}
        		}
    	    	
    	    }catch(Exception e)
    	    {
    	    	e.printStackTrace();
    	    }
    	}    	
    	return line; 
    }
	public UserView getUserview() {
		return userview;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	public String getIsViewForCexpress(String cexpress)
	{
		String ISVIEW="";
		if(cexpress!=null&&cexpress.indexOf("<ISVIEW>")!=-1)
		{
			ISVIEW=cexpress.substring(cexpress.indexOf("<ISVIEW>")+8,cexpress.indexOf("</ISVIEW>"));			
		}                                               
		return ISVIEW;
	}
	
    /**
     * 分析浏览器类型及版本
     * @param requestUserAgent request.getHeader("user-agent")
     * @return
     */
	public static String analyseBrowser(String requestUserAgent) {
/*	  mozilla/4.0 (compatible; msie 7.0; windows nt 6.1; wow64; trident/5.0; slcc2; .net clr 2.0.50727; .net clr 3.5.30729; .net clr 3.0.30729; media center pc 6.0; .net4.0c)
	  mozilla/5.0 (linux; u; android 4.1.1; zh-cn; mi 2a build/jro03l) applewebkit/534.30 (khtml, like gecko) version/4.0 mobile safari/534.30
	  mozilla/5.0 (iphone; cpu iphone os 7_0_6 like mac os x) applewebkit/537.51.1 (khtml, like gecko) mobile/11b651
	  mozilla/5.0 (ipod touch; cpu iphone os 7_1 like mac os x) applewebkit/537.51.2 (khtml, like gecko) mobile/11d167
	  mozilla/5.0 (macintosh; intel mac os x 10_9_2) applewebkit/537.74.9 (khtml, like gecko) version/7.0.2 safari/537.74.9
      mozilla/5.0 (windows nt 6.1; wow64) applewebkit/534.57.2 (khtml, like gecko) version/5.1.7 safari/534.57.2
*/
	      String browser = "MSIE";
	      String agent = requestUserAgent.toLowerCase(); 
	      if(agent.indexOf("firefox")!=-1)
	          browser="Firefox";
	      else if(agent.indexOf("edge")!=-1)//增加判断edge浏览器标识
	    	  browser="edge";
	      else if(agent.indexOf("chrome")!=-1)
	          browser="Chrome";
	      else if(agent.indexOf("safari")!=-1 || agent.indexOf("applewebkit")!=-1) {
	          browser="Safari";
	          if(agent.indexOf("applewebkit/")!=-1) {
	              String ver = agent.substring(agent.indexOf("applewebkit/")+"applewebkit/".length());
	              if(ver != null && ver.indexOf(" ")!=-1)
	                  ver = ver.substring(0, ver.indexOf(" "));
	              if(ver != null)
	                  browser+=ver;
	          }
	      }	    
	      return browser;
	}
	
	/**
	 * 取第一个有权限的登记表
	 * @param flag A人员,B单位,K岗位...,"A,B,K"表示人员+单位+岗位
	 * @param perTemplateId 绩效模板号
	 * @return
	 */
	public int getFirstPrivCardTabId(String flag, String perTemplateId) {
	    int tabid=-1;
        sql.delete(0,sql.length());
        if("P".equalsIgnoreCase(flag))
        {
            StringBuffer strsql=new StringBuffer();
            if(perTemplateId!=null&&!"".equalsIgnoreCase(perTemplateId)){
                
                List rs=ExecuteSQL.executeMyQuery("SELECT tabids from per_template where template_id='"+perTemplateId+"'");
                String tabids="";
                
                if(!rs.isEmpty()&&rs.size()>0)
                {
                     LazyDynaBean rec=(LazyDynaBean)rs.get(0);
                     tabids=(String)rec.get("tabids");
                     if(tabids!=null&&tabids.length()>0)
                     {
                         String[] tabss =tabids.split(",");
                         strsql.append("SELECT tabid,name from Rname where flaga='");
                         strsql.append(flag);                          
                         strsql.append("' and tabid in(");
                         for(int i=0;i<tabss.length;i++)
                             strsql.append("'"+tabss[i]+"',");
                         strsql.setLength(strsql.length()-1);
                         strsql.append(")");
                     }else
                     {
                         strsql.append("SELECT tabid,name from Rname where flaga='");
                         strsql.append(flag);                         
                         strsql.append("' and 1=2");
                     }
                }
            }else
            {
                strsql.append("SELECT tabid,name from Rname where flaga='");
                strsql.append(flag);                  
                strsql.append("'");
            }
            sql.append(strsql.toString());
            sql.append(" order by tabid");
        }else if ("A,B,K".equals(flag)) {
            sql.append("SELECT tabid as tabid from Rname where FlagA in ('A','B','K')");
            sql.append(" order by flagA, tabid");
        }else
        {
            sql.append("SELECT tabid as tabid from Rname where FlagA='");
            sql.append(flag);
            sql.append("' order by tabid");
        }
        ContentDAO dao = new ContentDAO(conn);
        try{
            RowSet rowset=dao.search(sql.toString());
            while(rowset.next())
            {
                  if((userview.isHaveResource(IResourceConstant.CARD,rowset.getString("tabid")))  
                          /*|| userview.getUserName().equalsIgnoreCase("su")*/)
                  {
                     tabid = rowset.getInt("tabid");
                     break;
                  }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	    return tabid;
	}
	
	public String getCardFlag(int tabid) {
	    String flag = "";
        ContentDAO dao = new ContentDAO(conn);
        try{
            RowSet rowset=dao.search("select flaga from rname where tabid = " + tabid);
            if(rowset.next())
            {
                flag = rowset.getString("flaga");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	    return flag;
	}
	
}
