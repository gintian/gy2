package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ZpReportBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ExecuteZpNeedsExcelTRans extends IBusiness {
	public void execute() throws GeneralException {
		String lineFields=(String)this.getFormHM().get("lineFields");
		String lieFields=(String)this.getFormHM().get("lieFields");
		String resultFields=(String)this.getFormHM().get("resultFields");
		/**安全平台改造,将特殊字符还原回来**/
		lineFields=lineFields.replaceAll("／", "/");
		lieFields=lieFields.replaceAll("／", "/");
		resultFields=resultFields.replaceAll("／", "/");
		String whl_sql =PubFunc.decrypt((String)this.getFormHM().get("whl_sql"));//安全平台改造,将加密的sql解密回来(String)this.getFormHM().get("whl_sql");
		if(whl_sql!=null)
			whl_sql=PubFunc.keyWord_reback(whl_sql);
		String zpchanel=(String)this.getFormHM().get("zpchanel");
		zpchanel=zpchanel.replaceAll("／", "/");
		
		String chanelcode="";
		String chanelname="";
		if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
			chanelcode=zpchanel.split("/")[0];
			chanelname=zpchanel.split("/")[1];
		}else{
			chanelcode="-1";
			chanelname="-1";
		}
		//处理whl_sql
		if(whl_sql!=null&&!"".equals(whl_sql))
		{
			if(whl_sql.indexOf("order by")!=-1)
				whl_sql = whl_sql.substring(0,whl_sql.indexOf("order by"));
			whl_sql=" left join z01 on z03.z0101=z01.z0101 "+whl_sql;
		}
		if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
			whl_sql=whl_sql+" and z03.z0336='"+chanelcode+"' ";
		}else{
			
		}
		HashMap map=new HashMap();
		HashMap lieMap=new HashMap();
		HashMap resultMap=new HashMap();
		ZpReportBo zrb=new ZpReportBo(this.frameconn,this.userView);
		zrb.setFrowset(this.frowset);
		if(lineFields.length()!=0){
			map=zrb.getmap(lineFields);
		}
		if(lieFields.length()!=0){
			lieMap=zrb.getmap(lieFields);
		}
		if(resultFields.length()!=0){
			resultMap=zrb.getmap(resultFields);
		}
		String sql=zrb.analyse(map, resultMap, lieMap,whl_sql);
		String sql2=zrb.analyse(lieMap, resultMap,map,whl_sql );
//		HashMap tmap=zrb.anaLyse2(sql, map, lieMap, resultMap);
//		String html=zrb.getHtml(tmap, map, lieMap, resultMap);
//		this.getFormHM().put("reportHtml", html);

		//招聘需求汇总表
		String outputFile = "hire_" + this.userView.getUserName() + ".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		try
		{
			
			HashMap laMap=new HashMap();
			ArrayList lalist=new ArrayList();
			ArrayList flist=new ArrayList();
			HashMap lMap=new HashMap();
			int xx=0;	//
			int xx1=0;
			int yy=0;
			int yy1=0;

			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			//打印行表头信息
			ArrayList rowinforlist = new ArrayList();
			ArrayList colinforlist = new ArrayList();
			String []fields2=lieFields.substring(1).split("`");
			String []fields3=resultFields.substring(1).split("`");
			String tempgroup="";
			String tempgroup2="";
			String tempgroup3="";
			for(int i=0;i<fields2.length;i++){
				String []childfields=fields2[i].split("/");
				tempgroup2+=childfields[0]+",";
			}
			for(int i=0;i<fields3.length;i++){
				String []childfields=fields3[i].split("/");
				tempgroup3+=childfields[0]+",";
			}
			String []fields1=null;
			int fields1lenght = 0;
			int fields3lenght = fields3.length;
			if(lineFields.length()>1){
				fields1=lineFields.substring(1).split("`");
				fields1lenght=fields1.length;
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			int countx=fields2.length;
			
				int county=fields1lenght+1;
			
			if(fields1!=null){
			for(int i=0;i<fields1.length;i++){
				String []childfields=fields1[i].split("/");
				tempgroup+=childfields[0]+",";
			}
			}
			if(tempgroup.length()>0)
				tempgroup = tempgroup.substring(0,tempgroup.length()-1);
			if(tempgroup2.length()>0)
				tempgroup2 = tempgroup2.substring(0,tempgroup2.length()-1);
			if(tempgroup3.length()>0)
				tempgroup3 = tempgroup3.substring(0,tempgroup3.length()-1);
			HashMap map2 = new HashMap();//合并需要
			HashMap colmap = new HashMap();//计算数据单元格对应的列指标值
			int colnum =0;//出现多少列
			if(fields1!=null){
				HashMap zmap=new HashMap();
				String []tgr=tempgroup3.split(",");
				for(int mm=0;mm<tgr.length;mm++){
					String value = tgr[mm] ;
					if(!zrb.isCode(value)){
						HashMap map5 = zrb.getFieldsName(value);
						value =""+map5.get(value);
						zmap.put(tgr[mm], value);
					}
				}
			for(int i=fields1.length-1;i>=0;i--){
				String []childfields=fields1[i].split("/");
				this.frowset = dao.search(sql);
				LazyDynaBean bean =null;
				HashMap colmap2 = new HashMap();//计算数据单元格对应的列指标值
				int count1 = 0;
				    countx = fields2.length+1;
				    int countto_x=fields2.length+1;
				    int num=1;
				    String oldkeys="$#%$#&*%$$@&%$@*&@";
				while(this.frowset.next()){
					String tempg [] = tempgroup.split(",");
					String value = this.frowset.getString(childfields[0])==null?"":this.frowset.getString(childfields[0]);
					String keys="";
					for(int j=i;j>=0;j--){
						keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(keys.equals(oldkeys)){
						num++;
						continue;
					}else{
						if(i==fields1.length-1){
							countto_x+=fields3.length;
						}else{
								countto_x += Integer.parseInt(""+map2.get(keys))*fields3.length;
						}
						num=1;
						oldkeys = keys;
					}
					if(i==fields1.length-1){
						String keys2="";
						for(int j=0;j<fields1.length;j++){
							keys2+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
							keys2=keys2.trim();
						}
						if(colmap2!=null&&colmap2.get(keys2)!=null){
							countto_x =countx;
							continue;
						}
						colmap2.put(keys2, keys2);
						colmap.put(""+colnum, keys2);
					colnum++;
					keys="";
					for(int j=i-1;j>=0;j--){
						keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(map2!=null&&map2.get(keys)!=null){ //key 为上几层
						map2.put(keys, Integer.parseInt(""+map2.get(keys))+1+"");
					}else{
						map2.put(keys, "1");
					}
					}else{
						keys="";
						for(int j=0;j<=i;j++){
							keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
							keys=keys.trim();
						}
						if(colmap2!=null&&colmap2.get(keys)!=null){
							countto_x =countx;
							continue;
						}else{
							colmap2.put(keys, keys);
						}
							String keys4="";
							for(int j=i-1;j>=0;j--){
								keys4+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
								keys4=keys4.trim();
							}
							if(map2!=null&&map2.get(keys4)!=null){ //key 为上几层
								
								if(map2!=null&&map2.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4)!=null){
									map2.put(keys4, Integer.parseInt(""+map2.get(keys4))+ Integer.parseInt(""+map2.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4))+"");
								}else{
									map2.put(keys4, Integer.parseInt(""+map2.get(keys4))+1+"");
								}
							}else{
								if(map2!=null&&map2.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4)!=null){
									map2.put(keys4, map2.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4));
								}else{
									map2.put(keys4, "1");
								}
							}
					}
					int from_x =  countx;
					int to_x =countto_x-1;
					int from_y = county;
					int to_y = county;
					//代码型转值
					if(zrb.isCode(childfields[0])){
						HashMap map5 = zrb.getCodeName(childfields[0]);
						value =""+map5.get(value);
						if(value==null|| "null".equalsIgnoreCase(value))
							value="";
					}
					if(value==null|| "null".equalsIgnoreCase(value))
						value="";
					 bean = new LazyDynaBean();
					 bean.set("hz", value);
					 bean.set("from_x", ""+from_y);
					 bean.set("to_x", ""+to_y);
					 bean.set("from_y", ""+from_x);
					 bean.set("to_y", ""+to_x);
					 if(i==fields1.length-1){
						if(laMap.get(value)==null){
							lalist.add(value);
							laMap.put(value, bean);
							
						}else{
								
						}
						if(tgr.length==1){									 
							xx=countto_x;
							xx1=countto_x;
							yy=county;
							yy1=county;  
						}
						flist.add(bean);
						int l=flist.size()-1;
						int from=l*tgr.length;
						for(int mm=0;mm<tgr.length;mm++){
							if(lMap.get((String)zmap.get(tgr[mm]))!=null){
								HashMap tt=(HashMap)lMap.get((String)zmap.get(tgr[mm]));
								tt.put(String.valueOf(from+mm), value);
								lMap.put((String)zmap.get(tgr[mm]), tt);
							}else{
								HashMap tt=new HashMap();
								tt.put(String.valueOf(from+mm), value);
								lMap.put((String)zmap.get(tgr[mm]), tt);
							}
							if(lMap.get("ls")!=null	){
								HashMap ls=(HashMap)lMap.get("ls");
								ls.put(String.valueOf(from+mm), (String)zmap.get(tgr[mm]));
								lMap.put("ls", ls);
							}else{
								HashMap ls=new HashMap();
								ls.put(String.valueOf(from+mm), (String)zmap.get(tgr[mm]));
								lMap.put("ls", ls);
							}
						}
						
					}
					 rowinforlist.add(bean);
					 countx=countto_x;
				}
				county--;
				if(i==0){
					
				}
			}
			
			}
			//处理结果指标
			countx = fields2.length+1;
			int countto_x =fields2.length+1;
			county=fields1lenght+2;
			this.frowset = dao.search(sql);
			String oldkeys="$#%$#&*%$$@&%$@*&@";
			HashMap colmap2 = new HashMap();
			if(fields3lenght>1||fields1lenght==0){
				while(this.frowset.next()){
					String tempg [] = tempgroup3.split(",");
					if(fields1lenght>0){
					String keys="";
					String tempg2 [] = tempgroup.split(",");
					for(int j=tempg2.length-1;j>=0;j--){
						keys+=(this.frowset.getString(tempg2[j])==null?"":this.frowset.getString(tempg2[j]))+",";
						keys=keys.trim();
					}
					if(keys.equals(oldkeys)){
						continue;
					}else{
						oldkeys = keys;
					}
					String keys2="";
					for(int j=0;j<fields1.length;j++){
						keys2+=(this.frowset.getString(tempg2[j])==null?"":this.frowset.getString(tempg2[j]))+",";
						keys2=keys2.trim();
					}
					if(colmap2!=null&&colmap2.get(keys2)!=null){
						countto_x =countx;
						continue;
					}
					colmap2.put(keys2, keys2);
					}
					LazyDynaBean bean =null;
					for(int i=0;i<tempg.length;i++){
						String value = tempg[i] ;
					countto_x ++;
					int from_x =  countx;
					int to_x =countto_x-1;
					int from_y = county;
					int to_y = county;
					//非代码型转值
					if(!zrb.isCode(value)){
						HashMap map5 = zrb.getFieldsName(value);
						value =""+map5.get(value);
					}
					 bean = new LazyDynaBean();
					 bean.set("hz", value);
					 bean.set("from_x", ""+from_y);
					 bean.set("to_x", ""+to_y);
					 bean.set("from_y", ""+from_x);
					 bean.set("to_y", ""+to_x);
					 rowinforlist.add(bean);
					 countx=countto_x;
					}
					if(fields1lenght<=0){
						break;
					}
				}
				xx=countto_x;
				xx1=countto_x;
				yy=county;
				yy1=county;
				
			}
		
			String []tgr=tempgroup3.split(",");
			HashMap zmap=new HashMap();
			for(int mm=0;mm<tgr.length;mm++){
				String value = tgr[mm] ;
				if(!zrb.isCode(value)){
					HashMap map5 = zrb.getFieldsName(value);
						value =""+map5.get(value);
						zmap.put(tgr[mm], value);
				}
			}
			
			int fx=0;
			int fx1=0;
			int dx=0;
			int dx1=0;
			int dy=0;
			int dy1=0;
			int fy1=0;
			int fy=0;
			dx=xx;
			dx1=xx;
			dy=yy;
			dy1=yy;
			if(fields1!=null&&(fields1.length==1&&tgr.length==1)){
				LazyDynaBean ab=new LazyDynaBean();
				 ab.set("hz", "总计");
				 ab.set("from_x", ""+dy);
				 ab.set("to_x", ""+dy1);
				 ab.set("from_y", ""+dx);
				 ab.set("to_y", ""+dx1); 
				 rowinforlist.add(ab);
			}
			if(fields1!=null&&((fields1.length==1&&tgr.length>1)||fields1.length>1)){
			for(int mm=0;mm<tgr.length;mm++){
				String tempg [] = tempgroup.split(",");
				if(mm==0){
					if(tgr.length==1){
						fy=yy-tempg.length+1;
					}else{
						fy=yy-tempg.length;
					}
					fy1=yy1-1;
					fx=xx;
					fx1=xx+lalist.size();
				}
				LazyDynaBean ab1=new LazyDynaBean();
				 ab1.set("hz", (String)zmap.get(tgr[mm]));
				 ab1.set("from_x", ""+fy);
				 ab1.set("to_x", ""+fy1);
				 ab1.set("from_y", ""+fx);
				 ab1.set("to_y", ""+fx1); 
				 rowinforlist.add(ab1);
				 fx=fx1+1;
				 fx1=fx1+lalist.size()+1;
				for(int ii=0;ii<=lalist.size();ii++){
					LazyDynaBean ab=new LazyDynaBean();
					
					if(ii==0){
						 ab.set("hz", "总计");
						 ab.set("from_x", ""+dy);
						 ab.set("to_x", ""+dy1);
						 ab.set("from_y", ""+dx);
						 ab.set("to_y", ""+dx1); 
						 rowinforlist.add(ab);
						 dx=dx+1;
						 dx1=dx1+1;
					}else{
						ab.set("hz", (String)lalist.get(ii-1));
						 ab.set("from_x", ""+dy);
						 ab.set("to_x", ""+dy1);
						 ab.set("from_y", ""+dx);
						 ab.set("to_y", ""+dx1); 
						 rowinforlist.add(ab);
						 dx=dx+1;
						 dx1=dx1+1;
					}	
				}
			}
			}
			//填报日期和标题
			Calendar d=Calendar.getInstance();
			int yy2=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			String date ="制表日期：  "+yy2+"年"+mm+"月"+dd+"日";
			LazyDynaBean bean = new LazyDynaBean();
			 bean.set("hz", date);
			 bean.set("from_x", ""+1);
			 bean.set("to_x", ""+1);
			 bean.set("from_y", ""+0);
			 bean.set("to_y", ""+2);
			 rowinforlist.add(bean);
			String desc ="";
			if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
				 desc =chanelname+"(";
			}
			String tempg4 [] = tempgroup3.split(",");
			for(int a =0;a<tempg4.length;a++){
				if(zrb.isCode(tempg4[a])){
					FieldItem item=(FieldItem)zrb.getCodeMap().get(tempg4[a]);
						if(item!=null)
							desc+=item.getItemdesc()+"、" ;
				}else{
					desc+=zrb.getLisMap().get(tempg4[a])+"、" ;
				}
				
			}
			
			if(desc.length()>1)
				desc= desc.substring(0,desc.length()-1);
			if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
				 desc +=")";
			}
			String title=yy2+"年"+desc+"计划汇总表";
			 bean = new LazyDynaBean();
			 bean.set("hz", title);
			 bean.set("from_x", ""+0);
			 bean.set("to_x", ""+0);
			 bean.set("from_y", ""+9);
			 bean.set("to_y", ""+(countto_x+tgr.length*lalist.size()+8));
			 rowinforlist.add(bean);
		
			county= fields2.length;
			HashMap map3 = new HashMap();//合并需要
			HashMap rowmap = new HashMap();//计算数据单元格对应的行指标值
			
			int rolnum =0;//出现多少行
			for(int i=fields2.length-1;i>=0;i--){
				HashMap rowmap2 = new HashMap();//去掉重复指标
				String []childfields=fields2[i].split("/");
				this.frowset = dao.search(sql2);
				ResultSetMetaData mdata = this.frowset.getMetaData();
				int cout =mdata.getColumnCount();
				int count1 = 0;
				if(fields3lenght==1){
					if(fields1lenght==0){
						 countx = fields1lenght+3;
					}else{
						 countx = fields1lenght+2;	
					}
				   
				}else{
					countx = fields1lenght+3;
				}
				     countto_x=countx;
				    int num=1;
				     oldkeys="";
				     //序号，列指标名称
				     String itemdesc="";
				     if(zrb.isCode(childfields[0])){
							FieldItem item=(FieldItem)zrb.getCodeMap().get(childfields[0]);
								if(item!=null)
									itemdesc = item.getItemdesc();
						}else{
							itemdesc = ""+zrb.getLisMap().get(childfields[0]);
						}
				     bean = new LazyDynaBean();
					 bean.set("hz", itemdesc);
					 bean.set("from_x", ""+2);
					 bean.set("to_x", ""+(countx-1));
					 bean.set("from_y", ""+county);
					 bean.set("to_y", ""+county);
					 colinforlist.add(bean);
					
				while(this.frowset.next()){
					String tempg [] = tempgroup2.split(",");
					String value = this.frowset.getString(childfields[0])==null?"null":this.frowset.getString(childfields[0]);
					String keys="";
					for(int j=i;j>=0;j--){
						keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(keys.equals(oldkeys)){
						num++;
						continue;
					}else{
						if(i==fields2.length-1){
							countto_x++;
						}else{
								countto_x += Integer.parseInt(""+map3.get(keys));
						}
						num=1;
						oldkeys = keys;
					}
					keys="";
					for(int j=i-1;j>=0;j--){
						keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(i==fields2.length-1){
						String keys2="";
						for(int j=0;j<fields2.length;j++){
							keys2+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
							keys2=keys2.trim();
						}
						if(rowmap2!=null&&rowmap2.get(keys2)!=null){
							countto_x =countx;
							continue;
						}
					    rowmap2.put(keys2, keys2);
						rowmap.put(""+rolnum, keys2);
						rolnum++;
					if(map3!=null&&map3.get(keys)!=null){ //key 为上几层
						map3.put(keys, Integer.parseInt(""+map3.get(keys))+1+"");
					}else{
						map3.put(keys, "1");
					}
					}else{
						keys="";
						for(int j=0;j<=i;j++){
							keys+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
							keys=keys.trim();
						}
						if(rowmap2!=null&&rowmap2.get(keys)!=null){
							countto_x =countx;
							continue;
						}else{
							 rowmap2.put(keys, keys);
						}
						String keys4="";
						for(int j=i-1;j>=0;j--){
							keys4+=(this.frowset.getString(tempg[j])==null?"":this.frowset.getString(tempg[j]))+",";
							keys4=keys4.trim();
						}
						if(map3!=null&&map3.get(keys4)!=null){ //key 为上几层
							
							if(map3!=null&&map3.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4)!=null){
								map3.put(keys4, Integer.parseInt(""+map3.get(keys4))+ Integer.parseInt(""+map3.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4))+"");
							}else{
								map3.put(keys4, Integer.parseInt(""+map3.get(keys4))+1+"");
							}
						}else{
							if(map3!=null&&map3.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4)!=null){
								map3.put(keys4, map3.get((this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+","+keys4));
							}else{
								map3.put(keys4, "1");
							}
						}
				}

					//代码型转值
					if(zrb.isCode(childfields[0])){
						HashMap map5 = zrb.getCodeName(childfields[0]);
						value =""+map5.get(value.trim());
						if(value==null|| "null".equalsIgnoreCase(value))
							value="";
					}
					if(value==null|| "null".equalsIgnoreCase(value))
						value="";
					int from_x =  countx;
					int to_x =countto_x-1;
					int from_y = county;
					int to_y = county;
					 bean = new LazyDynaBean();
					 bean.set("hz", value);
					 bean.set("from_x", ""+from_x);
					 bean.set("to_x", ""+to_x);
					 bean.set("from_y", ""+from_y);
					 bean.set("to_y", ""+to_y);
					 colinforlist.add(bean);
					 countx=countto_x;
				}
				county--;
				 if(i==0){
					 if(fields3lenght==1){
							if(fields1lenght==0){
								 countx = fields1lenght+3;
							}else{
								 countx = fields1lenght+2;	
							}
						   
						}else{
							countx = fields1lenght+3;
						}
						     countto_x=countx;
					    bean = new LazyDynaBean();
						 bean.set("hz", "序号");
						 bean.set("from_x", ""+2);
						 bean.set("to_x", ""+(countx-1));
						 bean.set("from_y", ""+0);
						 bean.set("to_y", ""+0);
						 colinforlist.add(bean); 
						 for(int a=1;a<=rolnum;a++){
							 bean = new LazyDynaBean();
							 bean.set("hz", ""+a);
							 bean.set("from_x", ""+(countx-1+a));
							 bean.set("to_x", ""+(countx-1+a));
							 bean.set("from_y", ""+0);
							 bean.set("to_y", ""+0);
							 colinforlist.add(bean); 
						 }
						 bean = new LazyDynaBean();
						 bean.set("hz", ""+(rolnum+1));
						 bean.set("from_x", ""+(countx+rolnum));
						 bean.set("to_x", ""+(countx+rolnum));
						 bean.set("from_y", ""+0);
						 bean.set("to_y", ""+0);
						 colinforlist.add(bean); 
						 String tempg [] = tempgroup2.split(",");
						 bean = new LazyDynaBean();
						 bean.set("hz", "总计");
						 bean.set("from_x", ""+(countx+rolnum));
						 bean.set("to_x", ""+(countx+rolnum));
						 bean.set("from_y", ""+1);
						 bean.set("to_y", ""+tempg.length);
						 colinforlist.add(bean); 
				 }
			}
			executeTabHeader(rowinforlist,colinforlist,sheet,workbook);
			//获得所有的结果map
			HashMap rMap = new HashMap();
			HashMap zjMap=new HashMap();
			ArrayList result = new ArrayList();
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				//获得行指标
				String tempg [] = tempgroup.split(",");
				String tempg2 [] = tempgroup2.split(",");
				String tempg3 [] = tempgroup3.split(",");
				String keys ="";
				if(fields1lenght>0)
				for(int i=0;i<tempg.length;i++){
					keys+=(this.frowset.getString(tempg[i])==null?"":this.frowset.getString(tempg[i]))+",";
				}
				for(int i=0;i<tempg2.length;i++){
					keys+=(this.frowset.getString(tempg2[i])==null?"":this.frowset.getString(tempg2[i]))+",";
				}
				for(int i=0;i<tempg3.length;i++){
					String tempkeys = keys;
					tempkeys+=tempg3[i];
					int value = this.frowset.getInt(tempg3[i]);
				if(rMap!=null&&rMap.get(tempkeys)!=null){
						rMap.put(tempkeys,""+(Integer.parseInt(""+rMap.get(tempkeys))+value));	
				}else{
					rMap.put(tempkeys,""+value);
				}
				}
				
			}
			if(colnum==0)
				colnum=1;
			int col=0;
			for(int i =0;i<rolnum;i++){
				int ll=0;
				HashMap zrMap=new HashMap();
				String[] resultstr=null;
				if(fields1!=null&&((fields1.length==1&&tgr.length>1)||fields1.length>1)){
					resultstr=new String[colnum*fields3.length+(lalist.size()+1)*tgr.length];
				}
				if(fields1!=null&&(fields1.length==1&&tgr.length==1)){
					resultstr=new String[colnum*fields3.length+1];
				}
				if(fields1==null){
					resultstr=new String[colnum*fields3.length];
				}
				int lzj=0;
				for(int j=0;j<colnum;j++){
					//往结果集里放数据
					String keys ="";
					if(fields1lenght>0)
					if(colmap!=null&&colmap.get(""+j)!=null){
						keys+= colmap.get(""+j);
					}
					if(rowmap!=null&&rowmap.get(""+i)!=null){
						keys+= rowmap.get(""+i);
					}
					String tempg3 [] = tempgroup3.split(",");
					String value="";
					for(int a=0;a<tempg3.length;a++){
						String tempkeys = keys;
						tempkeys+=tempg3[a];
						if(rMap!=null&&rMap.get(tempkeys)!=null){
							value = ""+rMap.get(tempkeys);
							if("0".equals(value))
							value="";
							resultstr[j*fields3.length+a]=value;
						}else{
							value="";
							resultstr[j*fields3.length+a]=value;
						}
						if(value.trim().length()==0){
							lzj=lzj+0;
						}else{
							lzj=lzj+Integer.parseInt(value);
						}
						if(zjMap.get(String.valueOf(ll))!=null){
							int lk=Integer.parseInt((String)zjMap.get(String.valueOf(ll)));
							if(value.trim().length()==0){
								lk=lk+0;
							}else{
								lk=lk+Integer.parseInt(value);
							}
							zjMap.put(String.valueOf(ll), String.valueOf(lk));
						}else{
							int lk=0;
							if(value.trim().length()==0){
								lk=lk+0;
							}else{
								lk=lk+Integer.parseInt(value);
							}
							zjMap.put(String.valueOf(ll), String.valueOf(lk));
						}
						ll++;	
						if(fields1!=null&&((fields1.length==1&&tgr.length>1)||fields1.length>1)){
							HashMap ls=(HashMap)lMap.get("ls");
							String tr=(String)ls.get(String.valueOf(j*fields3.length+a));
							HashMap tb=(HashMap)lMap.get(tr);
							String hz=(String)tb.get(String.valueOf(j*fields3.length+a));
							if(zrMap.get(tr)!=null){
								HashMap tt=(HashMap)zrMap.get(tr);
								int lk=0;
								if(tt.get(hz)!=null){
									lk=Integer.parseInt((String)tt.get(hz));
								}else{
									
								}
								if(value.trim().length()==0){
									lk=lk+0;
								}else{
									lk=lk+Integer.parseInt(value);
								}
								tt.put(hz, String.valueOf(lk));
								zrMap.put(tr, tt);
							}else{
								HashMap tt=new HashMap();
								int lk=0;
								if(value.trim().length()==0){
									lk=lk+0;
								}else{
									lk=lk+Integer.parseInt(value);
								}
								tt.put(hz, String.valueOf(lk));
								zrMap.put(tr, tt);
							}
						}
					}
				}
				if(fields1!=null&&(fields1.length==1&&tgr.length==1)){
					if(lzj==0){
						resultstr[ll]=" ";
					}else{
						resultstr[ll]=String.valueOf(lzj);
					}
					if(zjMap.get(String.valueOf(ll))!=null){
						int lk=Integer.parseInt((String)zjMap.get(String.valueOf(ll)));
						lk=lk+lzj;
						zjMap.put(String.valueOf(ll), String.valueOf(lk));
					}else{
						int lk=0;
						lk=lk+lzj;
						zjMap.put(String.valueOf(ll), String.valueOf(lk));
					}
					col=++ll;
				}
				if(fields1!=null&&((fields1.length==1&&tgr.length>1)||fields1.length>1)){
				for(int j=0;j<tgr.length;j++){
					HashMap tt=(HashMap)zrMap.get((String)zmap.get(tgr[j]));
					int zj=0;
					
					for(int k=0;k<lalist.size();k++){
						zj=zj+Integer.parseInt((String)tt.get((String)lalist.get(k)));
					}
					
					if(zj==0){
						resultstr[ll]=" ";
					}else{
						resultstr[ll]=String.valueOf(zj);
					}
					if(zjMap.get(String.valueOf(ll))!=null){
						int lk=Integer.parseInt((String)zjMap.get(String.valueOf(ll)));
						lk=lk+zj;
						zjMap.put(String.valueOf(ll), String.valueOf(lk));
					}else{
						int lk=0;
						lk=lk+zj;
						zjMap.put(String.valueOf(ll), String.valueOf(lk));
					}
					ll++;
					for(int k=0;k<lalist.size();k++){
						if("0".equals((String)tt.get((String)lalist.get(k)))){
							resultstr[ll]=" ";
						}else{
							resultstr[ll]=(String)tt.get((String)lalist.get(k));
						}
						if(zjMap.get(String.valueOf(ll))!=null){
							int lk=Integer.parseInt((String)zjMap.get(String.valueOf(ll)));
							lk=lk+Integer.parseInt((String)tt.get((String)lalist.get(k)));
							zjMap.put(String.valueOf(ll), String.valueOf(lk));
						}else{
							int lk=0;
							lk=lk+Integer.parseInt((String)tt.get((String)lalist.get(k)));
							zjMap.put(String.valueOf(ll), String.valueOf(lk));
						}
						ll++;
					}
				}
				}
				col=ll;
				result.add(resultstr);
				
			}
			String[] resultstr=new String[col];
			for(int i=0;i<col;i++){
				resultstr[i]=(String)zjMap.get(String.valueOf(i));
			}
			result.add(resultstr);
			if(fields3lenght==1&&fields1lenght>0)
				fields1lenght=fields1lenght-1;	
			executeTabDataArea( result, fields1lenght+3,fields2.length+1,row,sheet,workbook );
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			/**安全平台,改造,防止任意文件下载漏洞**/
			this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outputFile)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(workbook);
		}
	
	}
	
	/**
	 * 表头
	 */
	public void executeTabHeader(ArrayList rowInfoList,ArrayList colInfoList,HSSFSheet sheet,HSSFWorkbook wb )
	{
		LazyDynaBean abean=null;
		
		for(int i=0;i<rowInfoList.size();i++)
		{
			abean=(LazyDynaBean)rowInfoList.get(i);
			int from_x=Integer.parseInt((String)abean.get("from_x"));
			short from_y=Short.parseShort((String)abean.get("from_y"));
		
			if(abean.get("to_x")!=null)
			{
				int to_x=Integer.parseInt((String)abean.get("to_x"));
				short to_y=Short.parseShort((String)abean.get("to_y"));
				String hz=(String)abean.get("hz");
				executeCell(from_x,from_y,to_x,to_y,hz,sheet,wb);
			}
		}
		for(int i=0;i<colInfoList.size();i++)
		{
			abean=(LazyDynaBean)colInfoList.get(i);
			int from_x=Integer.parseInt((String)abean.get("from_x"));
			short from_y=Short.parseShort((String)abean.get("from_y"));
			if(abean.get("to_x")!=null)
			{
				int to_x=Integer.parseInt((String)abean.get("to_x"));
				short to_y=Short.parseShort((String)abean.get("to_y"));
				String hz=(String)abean.get("hz");	
				executeCell(from_x,from_y,to_x,to_y,hz,sheet,wb);
			}
		}
	}
	/**
	 * 
	 * @param a  起始 x坐标
	 * @param b	 起始 y坐标
	 * @param c	 终止 x坐标
	 * @param d  终止 y坐标
	 * @param content 内容
	 * @param style	  表格样式
	 * @param fontEffect 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
	 public void executeCell(int a,short b,int c,short d,String content,HSSFSheet sheet,HSSFWorkbook wb)
	 {
		 try {
			 HSSFRow row = sheet.getRow(a);	
			 if(row==null)
				 row = sheet.createRow(a);
			 HSSFCell cell = row.createCell(b);
			 HSSFFont font = wb.createFont();
			 row.setHeight((short)400);
			 content=content.replaceAll("`","\r\n");
			 HSSFFont font2 = wb.createFont();
			 font2.setFontHeightInPoints((short) 10);
			 font2.setColor(HSSFFont.COLOR_NORMAL);
			 font2.setBold(true);
			 HSSFCellStyle a_style=wb.createCellStyle();
			 if(a==0){
				 font2.setFontHeightInPoints((short) 16);
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setFont(font2);
			 }else if(a==1) {
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setFont(font2);
			 }else{
				 a_style.setBorderBottom(BorderStyle.THIN);
				 a_style.setBottomBorderColor(HSSFColor.BLACK.index);
				 a_style.setBorderLeft(BorderStyle.THIN);
				 a_style.setLeftBorderColor(HSSFColor.BLACK.index);
				 a_style.setBorderRight(BorderStyle.THIN);
				 a_style.setRightBorderColor(HSSFColor.BLACK.index);
				 a_style.setBorderTop(BorderStyle.THIN);
				 a_style.setTopBorderColor(HSSFColor.BLACK.index);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 a_style.setWrapText( true );
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.CENTER);
				 a_style.setFont(font2);
			 }
			 
			 cell.setCellStyle(a_style);	
			 cell.setCellValue(new HSSFRichTextString(content));
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.createCell(b1);
				 cell.setCellStyle(a_style);
			 }
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 
				 row = sheet.getRow(a1);
				 if(row==null)
					 row = sheet.createRow(a1);
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.createCell(b1);
					 cell.setCellStyle(a_style);
					 b1++;
				 }
			 }
			 
			 if(b<=d)
			 {
				 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
			 }
			 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
			
	 }
	 /**
		 * 数据区
		 */
		public void executeTabDataArea(ArrayList resultList,int rowLayNum,int colLayNum,  HSSFRow row,HSSFSheet sheet,HSSFWorkbook wb )
		{
			int colNum=0;
			int rowNum=0;
			HSSFFont font = wb.createFont();
			if(resultList.size()>0)
			{
				for(int i=0;i<resultList.size();i++)
				{
					rowNum=0;
					String[] rowInfo=(String[])resultList.get(i);
					for(int j=0;j<rowInfo.length;j++)
					{
						String context="";										
					
						context=rowInfo[j];
						

						row = sheet.getRow(rowLayNum+i);
						if(row==null)
							row = sheet.createRow(rowLayNum+i);
						HSSFCell cell = row.createCell(Short.parseShort(String.valueOf(colLayNum+j)));
						 HSSFFont font3 = wb.createFont();
						 HSSFCellStyle a_style=wb.createCellStyle();
							a_style.setBorderBottom(BorderStyle.THIN);
							a_style.setBottomBorderColor(HSSFColor.BLACK.index);
							a_style.setBorderLeft(BorderStyle.THIN);
							a_style.setLeftBorderColor(HSSFColor.BLACK.index);
							a_style.setBorderRight(BorderStyle.THIN);
							a_style.setRightBorderColor(HSSFColor.BLACK.index);
							a_style.setBorderTop(BorderStyle.THIN);
							a_style.setTopBorderColor(HSSFColor.BLACK.index);
							a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
							a_style.setWrapText( true );
							a_style.setAlignment(HorizontalAlignment.RIGHT);
							a_style.setVerticalAlignment(VerticalAlignment.CENTER);
						
							cell.setCellStyle(a_style);	
						if(context.trim().length()>0)
						{
							
								cell.setCellValue(Double.parseDouble(context));
						}
					}
				}
			}
			else
			{}
		}
	
}
