package com.hjsj.hrms.module.template.templatetoolbar.selfdefineflow.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SelfDefineFlowBo {
    private Connection conn=null;
    private UserView userview;
    /**模板号*/
    private int tab_id=-1;
    /**流程实例号*/
    private int ins_id=0;
    /**流程任务号*/
    private int task_id=0;
    /**节点编号*/
    private int node_id=0;
    
    
    ContentDAO dao=null;
    
 public SelfDefineFlowBo(Connection conn, UserView userview,
                int tab_id,int task_id,int ins_id,int node_id
                 )throws GeneralException {      
        this.conn = conn;
        this.tab_id = tab_id;
        this.task_id = task_id;
        this.ins_id = ins_id;
        this.node_id = node_id;
        this.userview=userview;
        dao = new ContentDAO(this.conn);

    }
    /**   
     * @Title: getCommonSqlWhere   
     * @Description: 公用sql条件   
     * @param @return
     * @param @throws GeneralException 
     * @return String    
     * @throws   
    */
    private String getCommonSqlWhere() throws GeneralException {
        
        StringBuffer strWhere = new StringBuffer();
        strWhere.append(" tabid=");
        strWhere.append(String.valueOf(this.tab_id));
        strWhere.append(" and task_id=");
        strWhere.append(String.valueOf(this.task_id));
        strWhere.append(" and ins_id=");
        strWhere.append(String.valueOf(this.ins_id));
        strWhere.append(" and node_id=");
        strWhere.append(String.valueOf(this.node_id));
        if (this.ins_id == -1) {
            strWhere.append(" and create_user='");
            strWhere.append(this.userview.getUserName());
            strWhere.append("'");

        } else {
            ;
        }
        return strWhere.toString();
    }
    /**   
     * @Title: getMaxLevel   
     * @Description:返回当前审批层级的最大层级号   
     * @param @return
     * @param @throws GeneralException 
     * @return int    
     * @throws   
    */
    private int getMaxLevel() throws GeneralException {
        int id = 0;
        try { 
            String sql="select max(sp_level) as id from t_wf_node_manual where "
                       + this.getCommonSqlWhere()
                       + " and bs_flag = '1' ";
     
            RowSet rSet =dao.search(sql);
            if (rSet.next()){//
               id = rSet.getInt(1); 
            }
            rSet.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return id;
    }
    
    /**
     * 根据模板id及其他相关内容,将符合条件的全部删除,为保存做准备
     * @throws GeneralException
     */
    public void delAllByTabId() throws GeneralException{
    	try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList dellist = new ArrayList();
			StringBuffer sb = new StringBuffer();
			String sqlWhere = getCommonSqlWhere();
			sb.append("delete from t_wf_node_manual where ");
			sb.append(sqlWhere);
			dao.delete(sb.toString(), dellist);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * @Title: initFlow   
     * @Description:初始化审批层级    
     * @return
     * @throws GeneralException
     */
    public ArrayList initFlow() throws GeneralException{
    	ArrayList listFlow = new ArrayList();
		try {
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList queryList = new ArrayList();
			
			//查询审批层级开始
			StringBuffer sb = new StringBuffer();
			String sqlWhere = getCommonSqlWhere();
			sb.append("select sp_level,actorid,actorname from t_wf_node_manual where ");
			sb.append(sqlWhere);
			sb.append(" and bs_flag = '1' ");
			sb.append(" order by sp_level,seq");
			rs = dao.search(sb.toString(), queryList);
			int maxLevel = this.getMaxLevel();
			
			ArrayList<HashMap<String,String>> tempList = new ArrayList<HashMap<String,String>>();//将获取的结果集保存在一个list集合中
			while(rs.next()){
				String uId = rs.getString("actorid");//人员库前缀+人员编号
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("sp_level", rs.getString("sp_level"));//审批层级编号
				map.put("actorid", PubFunc.encrypt(uId));
				map.put("actorname", rs.getString("actorname"));//人员名
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				String imgpath = photoImgBo.getPhotoPathLowQuality(uId.substring(0,3),uId.substring(3));//获取人员的头像地址
				map.put("imgSrc", imgpath);//头像地址
				tempList.add(map);
			}
			for (int j = 1; j <= maxLevel; j++) {//遍历层级,将同一层级的人员放在同一个list中
				ArrayList list = new ArrayList();
				for (int i = 0; i < tempList.size(); i++) {
					String level = tempList.get(i).get("sp_level");
					if(String.valueOf(j).equalsIgnoreCase(level)){
						list.add(tempList.get(i));
						//System.out.println(j+"\t"+tempList.get(i).get("actorname"));
					}
				}
				listFlow.add(list);
			}
			//System.out.println(listFlow);
			
			if(rs!=null)
                rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listFlow;
    }
    
    /**
     * @Title: initReport   
     * @Description:初始化报备    
     * @return
     * @throws GeneralException
     */
    public ArrayList initReport() throws GeneralException{
    	ArrayList listReport = new ArrayList();
    	try {
    		RowSet rs = null;
    		ContentDAO dao = new ContentDAO(this.conn);
    		ArrayList queryList = new ArrayList();
    		
    		//查询报备开始
    		StringBuffer sb = new StringBuffer();
    		String sqlWhere = getCommonSqlWhere();
    		sb = new StringBuffer();
    		sb.append("select sp_level,actorid,actorname from t_wf_node_manual where ");
    		sb.append(sqlWhere);
    		sb.append(" and bs_flag = '3' ");
    		sb.append(" order by sp_level,seq");
    		rs = dao.search(sb.toString(), queryList);
    		while(rs.next()){
    			String level = rs.getString("sp_level");
    			String uId = rs.getString("actorid");//人员库前缀+人员编号
    			HashMap map = new HashMap();
    			map.put("sp_level", level);//审批层级编号
    			map.put("actorid", PubFunc.encrypt(uId));//将编码后的员工编号放入map中
    			map.put("actorname", rs.getString("actorname"));//人员名
    			PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
    			String imgpath = photoImgBo.getPhotoPathLowQuality(uId.substring(0,3),uId.substring(3));//获取人员的头像地址
    			map.put("imgSrc", imgpath);//头像地址
    			listReport.add(map);
    		}
    		//System.out.println(listReport);
    		
    		if(rs!=null)
    			rs.close();
    	} catch (Exception e) {
    		// TODO
    		e.printStackTrace();
    	}
    	return listReport;
    }
    /**   
     * @Title: addFlow   
     * @Description:新增审批人员    
     * @param @param bs_flag
     * @param @param idAndNames
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public void addFlow(String bs_flag,ArrayList flowId) throws GeneralException {
        try {    
        	for (int i = 0; i < flowId.size(); i++) {
        		ArrayList tList = (ArrayList) flowId.get(i);
				for (int j = 0; j < tList.size(); j++) {
					JSONObject jObject = JSONObject.fromObject(tList.get(j));//强转成json对象
					String id = PubFunc.decrypt(jObject.get("id").toString());//员工编号
					String name = SafeCode.decode(jObject.get("name").toString());//员工姓名
					
					ArrayList filedList = new ArrayList();  
		            LazyDynaBean fieldBean=null;
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "tabid");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", String.valueOf(this.tab_id));
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "node_id");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", "0");
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "task_id");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", "0");
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "ins_id");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", "-1");
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "create_user");
		            fieldBean.set("type", "A");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", this.userview.getUserName());
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "bs_flag");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", bs_flag);
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "sp_level");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", String.valueOf(i+1));
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "actor_type");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", "1");
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "actorid");
		            fieldBean.set("type", "A");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", id);
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "actorname");
		            fieldBean.set("type", "A");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", name);
		            filedList.add(fieldBean);
		            
		            fieldBean= new LazyDynaBean();
		            fieldBean.set("itemid", "seq");
		            fieldBean.set("type", "N");
		            fieldBean.set("decimal", "0");
		            fieldBean.set("value", String.valueOf(j+1));    
		            filedList.add(fieldBean);
		            
		            DbNameBo.insertNewRecord("t_wf_node_manual","id",this.conn,filedList);
				}
			}
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**   
     * @Title: addReport   
     * @Description:新增报备人员    
     * @param @param bs_flag
     * @param @param idAndNames
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public void addReport(String bs_flag,ArrayList reportId) throws GeneralException {
    	try {    
    		for (int i = 0; i < reportId.size(); i++) {
    			JSONObject jObject = JSONObject.fromObject(reportId.get(i));//强转成json对象
				String id = PubFunc.decrypt(jObject.get("id").toString());//员工编号
				String name = SafeCode.decode(jObject.get("name").toString());//员工姓名
				
				ArrayList filedList = new ArrayList();  
    			LazyDynaBean fieldBean=null;
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "tabid");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", String.valueOf(this.tab_id));
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "node_id");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", "0");
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "task_id");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", "0");
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "ins_id");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", "-1");
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "create_user");
    			fieldBean.set("type", "A");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", this.userview.getUserName());
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "bs_flag");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", bs_flag);
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "sp_level");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", "1");
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "actor_type");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", "1");
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "actorid");
    			fieldBean.set("type", "A");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", id);
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "actorname");
    			fieldBean.set("type", "A");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", name);
    			filedList.add(fieldBean);
    			
    			fieldBean= new LazyDynaBean();
    			fieldBean.set("itemid", "seq");
    			fieldBean.set("type", "N");
    			fieldBean.set("decimal", "0");
    			fieldBean.set("value", String.valueOf(i+1));    
    			filedList.add(fieldBean);
    			
    			DbNameBo.insertNewRecord("t_wf_node_manual","id",this.conn,filedList);
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }   
    
}
