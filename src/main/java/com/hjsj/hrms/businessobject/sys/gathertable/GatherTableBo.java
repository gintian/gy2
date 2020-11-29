package com.hjsj.hrms.businessobject.sys.gathertable;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:库结构采集表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 26, 2008:3:51:30 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class GatherTableBo {
	private Connection conn = null;
	private RowSet frowset;
	
	public GatherTableBo(Connection a_con){
		this.conn=a_con;
		
	}
	
	/*
	 * 人员采集表生成
	 */
	public ArrayList userlist(String fieldA){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		StringBuffer buf = new StringBuffer();
		buf.append("select fieldsetid,customdesc from fieldset where fieldsetid like '"+fieldA+"%' and useflag IN ('1','2') order by displayorder");
		ContentDAO dao = new ContentDAO(this.conn);
		
		try {
			RowSet st = dao.search(buf.toString());
			while(st.next()){
				da = new CommonData();
				da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
				da.setDataValue(st.getString("fieldsetid"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/*
	 * 单位采集表生成
	 */
	public ArrayList unitslist(String fieldB,String fieldK){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		StringBuffer buf = new StringBuffer();
		buf.append("select fieldsetid,customdesc from fieldset where ((fieldsetid like '"+fieldB+"%') or (fieldsetid like '"+fieldK+"%')) and useflag IN ('1','2')");
		ContentDAO dao = new ContentDAO(this.conn);
		
		try {
			RowSet st = dao.search(buf.toString());
			while(st.next()){
				da = new CommonData();
				 // String 的形式获取此 ResultSet 对象的当前行中指定列的值
				da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
				da.setDataValue(st.getString("fieldsetid"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;
	}
	/**
	 * 指标解释
	 */
	public ArrayList indexlist(EncryptLockClient lockclient,UserView userView){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		StringBuffer buf = new StringBuffer();
		buf.append("select fieldsetid,customdesc from fieldset where useflag IN ('1','2') order by fieldsetid,displayorder");
		ContentDAO dao = new ContentDAO(this.conn);
		
		try {
			VersionControl ver_ctrl=new VersionControl();
			RowSet st = dao.search(buf.toString());
			while(st.next()){
				String fieldsetid = st.getString("fieldsetid");
				if(fieldsetid.startsWith("Y")||fieldsetid.startsWith("V")||fieldsetid.startsWith("W")){
					if(!lockclient.isHaveBM(31)) {
                        continue;
                    }
					if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350"))) {
                        continue;
                    }
				}
				if(fieldsetid.startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012"))) {
                    continue;
                }
				da = new CommonData();
				da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
				da.setDataValue(st.getString("fieldsetid"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 生成 采集表中指标解释 excel 表头
	 */
	public HashMap indexexportexcel(){
		HashMap map = new HashMap();
		try{
			map.put("indexexplain",ResourceFactory.getProperty("kjg.title.content"));
		}
		catch(Exception e){
			
		}
		return map;
	}
	/*
	 * 人员采集表与单位采集表 表头与详细信息
	 */
	public HashMap getparticular(String set,int udata){
		HashMap map = new HashMap();
		try{
			ArrayList list = new ArrayList();
			StringBuffer buf = new StringBuffer();
			StringBuffer sql= new StringBuffer("select fieldsetid,customdesc from fieldset where fieldsetid in(");
			if(set.indexOf("/")==-1){
				buf.append("'");
				buf.append(set);
				buf.append("'");
				sql.append(buf.toString());
			}else{
				String[] arr = set.split("/");
				for(int i=0;i<arr.length;i++){
					buf.append(",");
					buf.append("'");
					buf.append(arr[i]);
					buf.append("'");
				}
				sql.append(buf.toString().substring(1));
			}
			sql.append(")order by fieldsetid,displayorder");
			ContentDAO da = new ContentDAO(this.conn);
			this.frowset = da.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("fieldsetid",this.frowset.getString("fieldsetid"));
				bean.set("customdesc", this.frowset.getString("customdesc"));
				list.add(bean);  //有两个指标;id与name
			}
			//指标
			StringBuffer bufs = new StringBuffer();
			StringBuffer sql2 = new StringBuffer("select fieldsetid,codesetid,itemdesc from fielditem where fieldsetid in(");
			if(set.indexOf("/")==-1){
				bufs.append("'");
				bufs.append(set);
				bufs.append("'");
				sql2.append(buf.toString());
			}else{
				String[] arr = set.split("/");
				for(int i=0;i<arr.length;i++){
					bufs.append(",");
					bufs.append("'");
					bufs.append(arr[i]);
					bufs.append("'");
				}
				sql2.append(buf.toString().substring(1));
			}
			sql2.append(")and useflag='1' order by displayid");
			ContentDAO das = new ContentDAO(this.conn);
			this.frowset = das.search(sql2.toString());
			HashMap amap= new HashMap();
//			HashMap amaps= new HashMap();
//			ArrayList alist = new ArrayList();
			String ret="";
			while(this.frowset.next()){
				LazyDynaBean beans = new LazyDynaBean();
				beans.set("codesetid", this.frowset.getString("codesetid"));
				beans.set("itemdesc", this.frowset.getString("itemdesc"));
				beans.set("fieldsetid", this.frowset.getString("fieldsetid"));
				String sql1 = "select count(codesetid) AS aa from codeitem where codesetid='"+this.frowset.getString("codesetid")+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				rs= dao.search(sql1);
				if(rs.next()){
					ret = rs.getString(1);
				}
				int re = Integer.parseInt(ret);  //代码项个数
				
				if(re>udata||re==0&&re!=udata){       //根据udata值来判断是否要输出,re大于udata则不输出;
//					String s=" ";
//					LazyDynaBean d = new LazyDynaBean();
//					d.set("codeitemid", s);
//					d.set("codeitemdesc", s);
////					a.set("codesetid", this.frowset.getString("codesetid"));
					ArrayList slist = new ArrayList();
//					amap.put(frowset.getString("itemdesc").toUpperCase(), slist);
					amap.put(frowset.getString("itemdesc"), slist);
				}else if(re<udata||re==udata){
					String sqls = "select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"+this.frowset.getString("codesetid")+"'";
					ContentDAO daos = new ContentDAO(this.conn);
					RowSet rss = null;
					rss= daos.search(sqls);
					while(rss.next()){
						LazyDynaBean a = new LazyDynaBean();
						a.set("codeitemid", rss.getString("codeitemid"));
						a.set("codeitemdesc", rss.getString("codeitemdesc"));
						a.set("codesetid", rss.getString("codesetid"));
						if(amap.get(frowset.getString("itemdesc"))==null)
						{
							ArrayList slist = new ArrayList();
							slist.add(a);
							amap.put(frowset.getString("itemdesc"), slist); //把list放到map里
						}
						else
						{
							ArrayList slist=(ArrayList)amap.get(frowset.getString("itemdesc"));
							slist.add(a);
							amap.put(frowset.getString("itemdesc"), slist);
						}
//						alist.add(a);
					}

				}
				if(amap.get(frowset.getString("fieldsetid"))==null)
				{
					ArrayList setlist = new ArrayList();
					setlist.add(beans);
					amap.put(frowset.getString("fieldsetid"), setlist); //把list放到map里
				}
				else
				{
					ArrayList setlist=(ArrayList)amap.get(frowset.getString("fieldsetid"));
					setlist.add(beans);
					amap.put(frowset.getString("fieldsetid"), setlist);
				}
			}
			map.put("1", list);
			map.put("2",amap);
//			map.put("3",amaps);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
}
