package com.hjsj.hrms.transaction.sys.dbinit.indexexport;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:指标导出生成txt文件</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 8, 2008:4:30:36 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class ExportTxtTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String set=(String)this.getFormHM().get("set");
			String file="";
			if(set!=null||!"".equals(set)){
				HashMap map=getIndexTableInfo(set);
				ArrayList indexinfolist=(ArrayList)map.get("1");
				HashMap amap = (HashMap)map.get("2");
				file = getindexexporttxt(amap,indexinfolist);
				//xus 20/4/29 vfs改造
				file = PubFunc.encrypt(file);
//				file = SafeCode.encode(PubFunc.encrypt(file));//先加密再转码  jingq add2014.09.05
			}
			this.getFormHM().put("file",file);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private String getindexexporttxt(HashMap map,ArrayList infoList){
		String file="Fields.txt"; // 导出文件名字
		PrintWriter out=null;  //将格式化对象打印到一个文本输出流
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		try{
            //定义文件流
			fo = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+file);
			ow = new OutputStreamWriter(fo,"gbk"); //定义编码格式
			out = new PrintWriter(ow);
			out.println("\t\t\t"+"指标体系"+"\r\n");  //"\r\n"回车
			
			for(int i=0;i<infoList.size();i++){
				LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
				//指标为空不打印出来;
				ArrayList flist =(ArrayList)map.get((String)bean.get("fieldsetid"));
				if(flist!=null){
					out.println((String)bean.get("fieldsetid")+" : "+(String)bean.get("customdesc"));
					out.println("\r\n");
					out.println("序号"+"\t"+"代号"+"\t"+"状态"+"\t"+"类型"+"\t"+"长度"+"\t"+"精度"+"\t"+"代码"+"\t"+"名称");//添加数值型 精度列 12007 29945 wangb 20170519
					out.println("\r\n");
				}
				
				ArrayList fieldlist = (ArrayList)map.get((String)bean.get("fieldsetid")); //对应的fieldsetid
//				System.out.println("AAA = "+fieldlist);
				if(fieldlist!=null){
					for(int j=0;j<fieldlist.size();j++){
						LazyDynaBean abean = (LazyDynaBean)fieldlist.get(j);
//						out.println((String)abean.get("displayid")+"\t"+(String)abean.get("itemid")+"\t"+(String)abean.get("useflag")+"\t"+(String)abean.get("itemtype")+"\t"+(String)abean.get("itemlength")+"\t"+(String)abean.get("codesetid")+"\t"+(String)abean.get("itemdesc"));
						out.println((String)abean.get("displayid")+"\t"+(String)abean.get("itemid")+"\t"+(String)abean.get("useflag")+"\t"+(String)abean.get("itemtype")+"\t"+(String)abean.get("itemlength")+"\t"+(String)abean.get("decimalwidth")+"\t"+(String)abean.get("codesetid")+"\t"+(String)abean.get("itemdesc"));//添加精度列数据  wangb 20170726 12007 29945
					}
					out.println("\r\n");
				}
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			if(out!=null)
				out.close(); //关闭打印
			PubFunc.closeIoResource(ow);
		    PubFunc.closeIoResource(fo);
		}
		return file;
	}
	//查询出内容
	public HashMap getIndexTableInfo(String set){
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
		ContentDAO da = new ContentDAO(this.getFrameconn());
		this.frowset = da.search(sql.toString());
		while(this.frowset.next()){
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("fieldsetid",this.frowset.getString("fieldsetid"));
			bean.set("customdesc", this.frowset.getString("customdesc"));
			list.add(bean);  //有两个指标;id与name
		}
		//指标
		StringBuffer bufs = new StringBuffer();
		//添加查询数值型  精度字段 12007 wangb 20170519
		StringBuffer sql2 = new StringBuffer("select displayid,itemid,useflag,itemtype,itemlength,decimalwidth,codesetid,itemdesc,fieldsetid from fielditem where fieldsetid in(");
		if(set.indexOf("/")==-1){
			bufs.append("'");
			bufs.append(set);
			bufs.append("'");
			sql2.append(buf.toString());
		}else{
			String[] arrs = set.split("/");

			for(int i=0;i<arrs.length;i++){
				bufs.append(",");
				bufs.append("'");
				bufs.append(arrs[i]);
				bufs.append("'");
			}
			sql2.append(bufs.toString().substring(1));
		}
		sql2.append(")order by fieldsetid,displayid");
		ContentDAO das = new ContentDAO(this.getFrameconn());
		this.frowset = das.search(sql2.toString());
		HashMap amap= new HashMap();
		while(this.frowset.next()){
			LazyDynaBean beans = new LazyDynaBean();
			beans.set("displayid", this.frowset.getString("displayid"));
			beans.set("itemid", this.frowset.getString("itemid"));
			beans.set("useflag", this.frowset.getString("useflag"));
			beans.set("itemtype", this.frowset.getString("itemtype"));
			beans.set("itemlength", this.frowset.getString("itemlength"));
			beans.set("decimalwidth",this.frowset.getString("decimalwidth"));//添加查询数值型  精度字段 12007 wangb 20170519
			beans.set("codesetid", this.frowset.getString("codesetid"));
			beans.set("itemdesc", this.frowset.getString("itemdesc"));
			beans.set("fieldsetid", this.frowset.getString("fieldsetid"));
			if(amap.get(frowset.getString("fieldsetid").toUpperCase())==null)
			{
				ArrayList setlist = new ArrayList();
				setlist.add(beans);
				amap.put(frowset.getString("fieldsetid").toUpperCase(), setlist); //把list放到map里
			}
			else
			{
				ArrayList setlist=(ArrayList)amap.get(frowset.getString("fieldsetid").toUpperCase());
				setlist.add(beans);
				amap.put(frowset.getString("fieldsetid").toUpperCase(), setlist);
			}
			
		}
		map.put("1", list);
		map.put("2",amap);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
}
