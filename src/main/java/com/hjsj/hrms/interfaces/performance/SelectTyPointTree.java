package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class SelectTyPointTree {
	private String flag;
	private String pointsetid;
	private UserView userview;
	private  String subsys_id;
	private String unitcode;
	private HashMap selectmap;
	private static String pointsetids="";//指标分类范围
	public SelectTyPointTree(String flag,String pointsetid,UserView userview,String subsys_id ,String unitcode){
		this.flag=flag;
		this.pointsetid=pointsetid;
		this.userview=userview;
		this.subsys_id=subsys_id;
		this.unitcode=unitcode;
		this.selectmap=this.getSelectMap();
	}
	public String getXmlTree()throws GeneralException{
		StringBuffer xmls=new StringBuffer();
		Element root=new Element("TreeNode");
		root.setAttribute("id", "00");
 		root.setAttribute("text", "root");
 		root.setAttribute("title", "organization");
 		Document myDocument = new Document(root);
 		ArrayList list=this.getInforlist();
 		for(Iterator t=list.iterator();t.hasNext();){
 			LazyDynaBean abean=(LazyDynaBean)t.next();
 			Element child = new Element("TreeNode");
 			
 			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String aflag=(String)abean.get("aflag");
			
			if("-1".equals(aflag)){
				codeitemid="1_"+codeitemid;
			}else
				codeitemid="2_"+codeitemid;
			child.setAttribute("id",codeitemid);
			child.setAttribute("text", codeitemdesc);
			child.setAttribute("title", codeitemdesc);
			child.setAttribute("href", "");
			child.setAttribute("target", "mil_body"); 
			
			if(!"-1".equals(aflag))
				child.setAttribute("icon","/images/open1.png");
			else
				child.setAttribute("icon","/images/lock_co_1.gif");
			String c_xml="/performance/kh_system/kh_field/create_newpointtree.jsp?unitcode="+this.unitcode+"&flag="+aflag+"&pointsetid="+codeitemid.substring(2)+"&subsys_id="+this.subsys_id;
			if(!"-1".equals(aflag))
            	child.setAttribute("xml", c_xml);
			root.addContent(child);
 		}
 		XMLOutputter outputter = new XMLOutputter();
 		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));
		return xmls.toString();
	}
	public ArrayList getInforlist(){
		ArrayList alist=new ArrayList();
		ResultSet rs=null;
		ResultSet res=null;
		Connection con=null;
		String inner="";
		String fields="";
		String point_id="";
		String pointSet_id="";
		String pointSetids="";
		if("-1".equals(this.flag)){
			return alist;	
		}
		
		try {
			con=AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			ContentDAO dao1 = new ContentDAO(con);
//			if(this.pointsetid.equals("0")){
//				//status=1（考核主体用对这个指标打分）=0（考核主体不用对这个指标打分）默认值为0   0:定性要点；1:定量要点对于定量指标，可以设置一个对应关系
//				String sql="select distinct(pointsetid) from per_point where pointkind='1' and status='1'";
//				rs=st.executeQuery(sql);
//				while(rs.next()){
//					inner+=this.getpointsetid(con, rs.getString("pointsetid"))+",";	
//				}
//				if("".equals(inner) || inner==null)
//					return alist;
//				inner=inner.substring(0,inner.length()-1);
//			}
//			
			if("0".equals(this.pointsetid)){
				//status=1（考核主体用对这个指标打分）=0（考核主体不用对这个指标打分）默认值为0   0:定性要点；1:定量要点对于定量指标，可以设置一个对应关系
				String sql="select distinct pointsetid from per_point where pointkind='1' and status='1'";
				String aql="";
				rs=dao.search(sql);
				while(rs.next()){
					aql="select point_id  from per_point where pointsetid='"+rs.getString("pointsetid")+"'";
					res=dao1.search(aql);
					while(res.next()){
						point_id=res.getString("point_id");
//						if(this.selectmap.get(point_id)!=null){
//							continue;
//						}
						if(this.userview.isAdmin()||this.userview.isHaveResource(IResourceConstant.KH_FIELD,point_id)){
							pointSet_id+="'"+rs.getString("pointsetid")+"',";
							pointSetids+=rs.getString("pointsetid")+",";
							break;
						}
					}
				}
				String pointSetid[]=pointSetids.split(",");
				for(int i=0;i< pointSetid.length;i++){
					pointSet_id=this.getParentPointSetId(pointSetid[i], con, pointSet_id);
				}
				if(pointSet_id.length()>0)
					pointSet_id=pointSet_id.substring(0, pointSet_id.length()-1);
				else
					pointSet_id="''";
				pointsetids=pointSet_id;
			
			}
			
			StringBuffer stb=new StringBuffer("");
			if("0".equals(this.flag)){//flag 0 还有子指标分类  1无
				stb.append("select * from per_pointset where ");
				if("0".equals(this.pointsetid)){
					stb.append(" parent_id is null and UPPER(subsys_id)='"+this.subsys_id.toUpperCase()+"' and validflag='1' and pointsetid in("+pointsetids+") order by seq");
				}else{
					stb.append(" parent_id="+this.pointsetid+" and UPPER(subsys_id)='" + this.subsys_id.toUpperCase()+"' and validflag='1' and pointsetid in("+pointsetids+") order by seq");
				}
			}else{
				stb.append("select * from per_point where pointsetid='"+this.pointsetid+"' and pointkind='1' and status='1' and validflag='1' order by seq");
			}
			rs=dao.search(stb.toString());
			while(rs.next()){
				LazyDynaBean bean=new LazyDynaBean();
				if("0".equals(this.flag)){
//					if(!this.userview.isSuper_admin()&&!this.userview.getGroupId().equals("1")){
//							if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
//								continue;
//					}
					if(!"0".equals(pointsetid)){
						if(this.isTongyi(con, rs.getString("pointsetid"))){
							bean.set("codeitemid", rs.getString("pointsetid"));
							bean.set("codeitemdesc", rs.getString("pointsetname"));
							String childid=rs.getString("child_id")==null?"":rs.getString("child_id");
							if(childid.length()>0)
								bean.set("aflag","0");
							else{
								bean.set("aflag", "1");
							}
						}else{
							continue;
						}
					}else{
						bean.set("codeitemid", rs.getString("pointsetid"));
						bean.set("codeitemdesc", rs.getString("pointsetname"));
						String childid=rs.getString("child_id")==null?"":rs.getString("child_id");
						if(childid.length()>0)
							bean.set("aflag","0");
						else{
							bean.set("aflag", "1");
						}
					}
					
				}else{
					if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId())){
						if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
							continue;
					}
					if(this.selectmap.get(rs.getString("point_id"))!=null){
						continue;
					}
					bean.set("codeitemid",rs.getString("point_id"));
 					bean.set("codeitemdesc",rs.getString("pointname"));
 					bean.set("aflag","-1");
				}
				alist.add(bean);
			}
			if("0".equals(flag)){
				String sql="select * from per_point where pointsetid='"+this.pointsetid+"' and pointkind='1' and status='1' order by seq";
				rs=dao.search(sql);
				while(rs.next()){
					LazyDynaBean bean=new LazyDynaBean();
					if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId()))
 					{
 						if(!this.userview.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
 							continue;
 					}
					if(this.selectmap.get(rs.getString("point_id"))!=null){
						continue;
					}
					bean.set("codeitemid", rs.getString("point_id"));
					bean.set("codeitemdesc",rs.getString("pointname"));
 					bean.set("aflag","-1");
 					alist.add(bean);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
				try {
					if(rs!=null){
						rs.close();
					}
					if(res!=null){
						rs.close();
					}
					if(con!=null){
						con.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}		
		}
		StringBuffer stb=new StringBuffer("");
		
		return alist;
	}
	/**
	 * 递归 取得权限范围内指标的所有上级分类
	 * @param pointSetId
	 * @param conn
	 * @param stmt
	 * @param res
	 * @param pointSet_id
	 */
	public String getParentPointSetId(String pointSetId,Connection conn,String pointSet_id ){
		String sql="select parent_id from per_pointset where pointsetid='"+pointSetId+"'";
		ResultSet res=null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			res=dao.search(sql);
			while(res.next()){
				pointSetId=res.getString("parent_id");
				if(pointSetId!=null){
					pointSet_id+="'"+pointSetId+"',";
					pointSet_id=this.getParentPointSetId(pointSetId, conn, pointSet_id);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
					if(res!=null)
						res.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return pointSet_id;
		}

	public HashMap getSelectMap(){
		 HashMap map = new HashMap();
 	     ResultSet rs = null;	
 		 Connection conn=null;
 		 try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(conn);
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
		    String pointcode_menu=(String)ht.get("pointcode_menu");
		    StringBuffer sqlt=new StringBuffer("");
		    String unitcode="";
		    if(this.unitcode.indexOf("UN")!=-1||this.unitcode.indexOf("UM")!=-1){
		    	unitcode=this.unitcode.substring(3,this.unitcode.length()-1);
		    }
		    else{
		    	unitcode=unitcode=this.unitcode.substring(1,this.unitcode.length()-1);
		    }
		    sqlt.append("select "+pointcode_menu+" from " +pointset_menu+" where b0110='"+unitcode+"'");
		    rs=dao.search(sqlt.toString());
		    while(rs.next()){
		    	map.put(rs.getString(pointcode_menu), "1");
		    }
		   //本级没有查找上一级直到找到根节点
		    if(map.size()==0){
		    	
		    	HashMap hm=this.getmap(conn, unitcode, pointcode_menu, pointset_menu);
		    	if(hm.size()==0){
		    		return map;
		    	}else{
		    		return hm;
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
				try {
					if(rs!=null){
						rs.close();
					}
					if(conn!=null&&!conn.isClosed()){
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return map;
	}
	public String getpointsetid(Connection con,String pointsetid){
		String pointset_id="";
		ResultSet rst= null;
		try {
			ContentDAO dao = new ContentDAO(con);
			rst=dao.search("select * from per_pointset where pointsetid='"+pointsetid+"'");
			if(rst.next()){
				if(rst.getString("parent_id")!=null){
					return getpointsetid(con,rst.getString("parent_id"));
				}else{
					return pointsetid;
				}
			}else{
				return pointsetid;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
				try {
					if(rst!=null){
						rst.close();
					}
//					if(con!=null){
//						con.close();
//					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		return pointset_id;
	}
	public boolean isTongyi(Connection con,String pointSetid){
		boolean flag=true;
		ResultSet rs=null;
		
		try {
			String sql="select * from per_pointset where parent_id='"+pointSetid+"'";
			ContentDAO dao = new ContentDAO(con);
			rs=dao.search(sql);
			while(rs.next()){
				if(isTongyi(con,rs.getString("pointsetid"))){
					 return flag;
				}else{
					continue;
				}
			}
		
				sql="select * from per_point where pointsetid='"+pointSetid+"' and pointkind='1' and status='1'";
				rs=dao.search(sql);
				if(rs.next()){
					return flag;
				}
				else{
					flag=false;
					return flag;
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
				try {
					if(rs!=null)
					{
						rs.close();
					}
//					if(con!=null){
//						con.close();
//					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
		}
		return flag;
	}
	public HashMap getmap(Connection con,String codeitemid,String pointid,String tablename){
		HashMap hm=new HashMap();
		ResultSet rst=null;
		try {
			String b0110="";
			ContentDAO dao = new ContentDAO(con);
			String sql="select "+pointid+" from "+tablename+" where b0110='";
			String sql2="select parentid from organization where codeitemid='"+codeitemid+"'and parentid<> codeitemid";
			rst=dao.search(sql2);
			if(rst.next()){
				b0110=rst.getString("parentid");
				sql+=b0110+"'";
				rst=dao.search(sql);
				while(rst.next()){
					hm.put(rst.getString(pointid), "1");
				}
				if(hm.size()==0){
					return getmap(con,b0110,pointid,tablename);
				}else
					return hm;
			}else{
				return hm;
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}finally{
			
			try {
				if(rst!=null)
				{
					rst.close();
				}
//				if(con!=null){
//					con.close();
//				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
		return hm;
	}
}
