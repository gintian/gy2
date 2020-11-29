package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.ZpReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ExecuteZpNeedsHtmlTRans extends IBusiness {
	public void execute() throws GeneralException {
		String lineFields=(String)this.getFormHM().get("lineFields");
		String lieFields=(String)this.getFormHM().get("lieFields");
		String resultFields=(String)this.getFormHM().get("resultFields");
		ArrayList chanelList=(ArrayList) this.getFormHM().get("chanelList");
		/**安全平台改造,将特殊字符还原回来**/
		lineFields=lineFields.replaceAll("／", "/");
		lieFields=lieFields.replaceAll("／", "/");
		resultFields=resultFields.replaceAll("／", "/");
		/**安全平台改造,将加密的sql进行解密回来PubFunc.decrypt(ori_str)**/
		String whl_sql =PubFunc.decrypt((String)this.getFormHM().get("whl_sql"));
		//处理whl_sql
		String zpchanel=(String)this.getFormHM().get("zpchanel");
		zpchanel=zpchanel.replaceAll("／", "/");
		this.getFormHM().put("zpchanel", zpchanel);
		String chanelcode="";
		String chanelname="";
		if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
			chanelcode=zpchanel.split("/")[0];
			chanelname=zpchanel.split("/")[1];
		}else{
			chanelcode="-1";
			chanelname="-1";
		}
		
		if(whl_sql!=null&&!"".equals(whl_sql))
		{
			if(whl_sql.indexOf("order by")!=-1)
				whl_sql = whl_sql.substring(0,whl_sql.indexOf("order by"));
			whl_sql=" left join z01 on z03.z0101=z01.z0101 "+whl_sql;
		}
		if(zpchanel!=null&&zpchanel.length()!=0&&!"-1".equalsIgnoreCase(zpchanel)){
			whl_sql=whl_sql+" and z03.z0336='"+chanelcode+"'";
		}
		
//		HashMap map=new HashMap();
//		HashMap lieMap=new HashMap();
//		HashMap resultMap=new HashMap();
		ZpReportBo zrb=new ZpReportBo(this.frameconn,this.userView);
		zrb.setFrowset(this.frowset);
//		if(lineFields.length()!=0){
//			map=zrb.getmap(lineFields);
//		}
//		if(lieFields.length()!=0){
//			lieMap=zrb.getmap(lieFields);
//		}
//		if(resultFields.length()!=0){
//			resultMap=zrb.getmap(resultFields);
//		}
//		int le=Integer.parseInt((String)lieMap.get("length"));
		StringBuffer sql2=new StringBuffer();
		
		ContentDAO dao=new ContentDAO(this.frameconn);
		String html="";
		String html2="";
//		HashMap lpoMap=new HashMap();
		try {
//			String sql=zrb.analyse(map, resultMap, lieMap);
//			lpoMap=this.getMap(sql, lieMap, le);			
//			HashMap ll=new HashMap();
//			this.exeMap(le, 0, lpoMap, ll);
//			if(ll!=null){
//				zrb.setPosMap(ll);
//			}
			
			//HashMap tmap=zrb.anaLyse2(sql, map, lieMap, resultMap);
			//html=zrb.getHtml(tmap, map, lieMap, resultMap);
			zrb.setZpchanel(chanelname);
			html=zrb.getHtml2(lineFields, lieFields, resultFields,whl_sql);
			if(html!=null&&html.trim().length()!=0){
				this.getFormHM().put("noexcel", "");
			}else{//dml 2011-6-21 15:32:07
				String tempgroup2="";
				String tempgroup3="";
				
				String []fields2=lieFields.substring(1).split("`");
				String []fields1=lineFields.substring(1).split("`");
				for(int i=0;i<fields1.length;i++){
					String []childfields=fields1[i].split("/");
					tempgroup3+=childfields[2]+",";
				}
				tempgroup3=tempgroup3.substring(0,tempgroup3.length()-1);
				
				for(int i=0;i<fields2.length;i++){
					String []childfields=fields2[i].split("/");
					tempgroup2+=childfields[2]+",";
				}tempgroup2=tempgroup2.substring(0, tempgroup2.length()-1);
				html=html+"查找不到符合行条件"+tempgroup3+"和列条件"+tempgroup2+"的";
				if("-1".equalsIgnoreCase(chanelname)){
					html+="结果！";
				}else{
					html+=chanelname+"结果！";
				}
				this.getFormHM().put("noexcel", "123");
			}
			//System.out.println(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("reportHtml", html);
		
	}
	
//	public  void exeMap(int lent,int pos,HashMap poMap,HashMap posMap){
//		if(pos!=lent){
//			if(pos==0){
//				posMap.put(String.valueOf(pos), poMap.get(String.valueOf(pos)));
//				this.exeMap(lent, pos+1, poMap, posMap);
//			}else{
//				ArrayList poList=(ArrayList)poMap.get(String.valueOf(pos));
//				ArrayList pareList=(ArrayList)posMap.get(String.valueOf(pos-1));
//				ArrayList list=new ArrayList();
//				for(int i=0;i<pareList.size();i++){
//					for(int k=0;k<poList.size();k++){
//						String parent=(String)pareList.get(i);
//						parent=parent+"/"+poList.get(k);
//						list.add(parent);
//					}
//				}
//				posMap.put(String.valueOf(pos), list);
//				this.exeMap(lent, pos+1, poMap, posMap);
//			}
//		}else{
//			return;
//		}
//	}
//	public HashMap getMap(String sql ,HashMap lieMap,int len){
//		ContentDAO dao=new ContentDAO(this.frameconn);
//		ArrayList flList=null;
//		HashMap tem=new HashMap();
//		HashMap lpoMap=new HashMap();
//		
//		HashMap lMap=new HashMap();
//		try {
//			this.frowset=dao.search(sql.toString());
//			
//				while(this.frowset.next()){
//					for(int i=0;i<len;i++){
//						if(lMap.get(String.valueOf(i))!=null){
//							tem=(HashMap)lpoMap.get(String.valueOf(i));
//							if(tem!=null){
//								if(tem.get(this.frowset.getString((String)lieMap.get(String.valueOf(i))))!=null){
//									continue;
//								}
//								flList=(ArrayList)lMap.get(String.valueOf(i));
//							}else{
//								flList=(ArrayList)tem.get("sx");
//							}
//						}else{
//							flList=new ArrayList();
//							
//						}
//						tem.put(this.frowset.getString((String)lieMap.get(String.valueOf(i))), "1");
//						lpoMap.put(String.valueOf(i), tem);
//						flList.add(this.frowset.getString((String)lieMap.get(String.valueOf(i))));
//						lMap.put(String.valueOf(i), flList);
//					}
//					
//				}
//				
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return lMap;
//	}

}
