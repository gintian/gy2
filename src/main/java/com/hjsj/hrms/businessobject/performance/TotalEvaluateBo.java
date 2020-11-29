package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class TotalEvaluateBo {

	private Connection conn = null;
	private UserView userView = null;
	private String plan_id = "";
	private String object_id = "";//考核对象
	private String gather_type = "";//采集数据的方式  0:网上  1:机读  2:网上+机读
	private String plan_type = "";//0:不记名 1:记名
	public TotalEvaluateBo(Connection conn,UserView userView,String plan_id,String object_id){
		this.conn = conn;
		this.userView = userView;
		this.plan_id = plan_id;
		this.object_id = object_id;
		this.gather_type = getGatherType();
		this.plan_type = getPlanType();
	}
	//得到该计划下所有的考核对象
	public ArrayList getEvaluate_object_list(){

		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			rowSet = dao.search("select object_id,a0101 from per_object where plan_id='"+plan_id+"' order by a0000");
			while(rowSet.next()){
				String object_id = rowSet.getString("object_id");
				String a0101 = rowSet.getString("a0101");
				CommonData obj=new CommonData(PubFunc.encryption(object_id),a0101);
				list.add(obj);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	
	}
	//得到总的数据
	public LinkedHashMap getDataMap(){

		LinkedHashMap map = new LinkedHashMap();
		try{
			if(!"1".equals(gather_type)){//如果不是机读
				RowSet rowSet = null;
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer sb = new StringBuffer();
				sb.append("select mainbody_id,a0101,description from per_mainbody where status=2 and plan_id='"+plan_id+"' and object_id='"+object_id+"' order by body_id,b0110,e0122");
				rowSet = dao.search(sb.toString());
				if("1".equals(plan_type)){//如果是记名计划
					while(rowSet.next()){
						String mainbody_id = rowSet.getString("mainbody_id");
						String a0101 = rowSet.getString("a0101");
						String description = rowSet.getString("description");
						if(description!=null && !"".equals(description)) {
                            map.put(mainbody_id, a0101+"`"+description);
                        }
					}
				}else{
					int i=1;
					while(rowSet.next()){
						String mainbody_id = rowSet.getString("mainbody_id");
						String description = rowSet.getString("description");
						if(description!=null && !"".equals(description)){
							map.put(mainbody_id, ResourceFactory.getProperty("lable.performance.mainbody")+i+"`"+description);
							i++;
						}
					}
				}
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	
	
	}
	//画出界面
	public String getEvaluateHtml(LinkedHashMap dataMap){
		StringBuffer  content = new StringBuffer();
		Set key = dataMap.keySet();
	    for (Iterator it = key.iterator(); it.hasNext();) {
	    	String s = (String) it.next();
	    	String[] temp = ((String)dataMap.get(s)).split("`");
	    	content.append(ResourceFactory.getProperty("lable.performance.perMainBody")+"：");
	    	content.append(temp[0]);
	    	content.append("\n\r");
	    	content.append(ResourceFactory.getProperty("lable.statistic.wholeeven")+"：");
	    	content.append(temp[1]);
	    	content.append("\n\r");
	    	content.append("\n\r");
	    }
	    return content.toString();
	}
	//得到数据采集类型
	public String getGatherType(){
		PerformanceImplementBo pb = new PerformanceImplementBo(conn);
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String gather_type=vo.getString("gather_type");
		return gather_type;
	}
	//得到该计划是否记名
	public String getPlanType(){
		PerformanceImplementBo pb = new PerformanceImplementBo(conn);
		RecordVo vo = pb.getPerPlanVo(plan_id);
		String plan_type=vo.getString("plan_type");
		return plan_type;
	}
}
