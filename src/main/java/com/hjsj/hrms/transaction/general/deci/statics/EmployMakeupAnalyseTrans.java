/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Owner
 *
 */
public class EmployMakeupAnalyseTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/*加载应前缀库过滤条件*/
		//loadprivdb();
		loadstatlist();
	}	
	/*加载统计条件项*/
	private void loadstatlist() throws GeneralException 
	{
		String categories = (String)this.getFormHM().get("categories");
		this.getFormHM().put("categories","");
		HashMap reqHM = (HashMap)this.getFormHM().get("requestPamaHM");
		String statid = (String)reqHM.get("statid");
		String showstatname = (String) reqHM.get("showstatname");
		String showcharttype = (String) reqHM.get("showcharttype");
		String char_type = (String) reqHM.get("char_type");
		reqHM.remove("char_type");
		reqHM.remove("showcharttype");
		reqHM.remove("showstatname");
		reqHM.remove("statid");
		//liuy 2014-10-17 4109菜单定制中，填写了一个错误的链接，点击“增员分布”，后台报错，且页面显示了一个别的常用统计的饼形图 start
		if(statid!=null){			
			boolean flag=getStatItem(statid);
			if(!flag){
				throw new GeneralException("配置的统计项不存在！");
			}
		}
	    //liuy end
		String where = "";
		if(categories!=null && categories.length()>0){
			where  = " and categories='"+categories+"'";
		}else if(statid !=null && statid.length()>0){
			where  = " and id="+statid;
		}
		where +=" and type<>3 ";
		ArrayList statlist=new ArrayList();
		StringBuffer  statsql=new StringBuffer();
		statsql.append("select id,name,type from SName where infokind=1 ");
		statsql.append(where);
		statsql.append(" order by snorder");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			ArrayList chartTypeList=new ArrayList();
			if(char_type==null||"".equals(char_type)){//未传图例形状特征，可选所有
				chartTypeList = StatDataEncapsulation.getChartTypeList();
			}
			char_type = char_type==null||"".equals(char_type)?"11":char_type;//未传图例形状特征，默认立体直方图
			char_type = new String(char_type.getBytes("ISO-8859-1"));
			char_type = char_type.replaceAll("，", ",");
			if(chartTypeList.size()==0){//liuy 2015-3-30 8454:领导桌面：人员结构分析图页面中，图形分类下拉框中存在重复的立体直方图
				CommonData cda = null;
				String charType[] = char_type.split(",");
				for(int i = 0;i < charType.length;i++){
					if("11".equals(charType[i])){
						cda=new CommonData("11",ResourceFactory.getProperty("static.figure.vertical_bar"));
						chartTypeList.add(cda);
					}else if("20".equals(charType[i])){
						cda=new CommonData("20",ResourceFactory.getProperty("static.figure.pie"));
						chartTypeList.add(cda);
					}else if("1000".equals(charType[i])){
						cda=new CommonData("1000",ResourceFactory.getProperty("static.figure.line"));
						chartTypeList.add(cda);
					}else if("40".equals(charType[i])){
						cda=new CommonData("40",ResourceFactory.getProperty("static.figure.bar_line"));
						chartTypeList.add(cda);
					}
				}
			}
			if(char_type.indexOf(",")!=-1){
				char_type = char_type.substring(0, char_type.indexOf(","));
			}
			this.getFormHM().put("char_type", char_type);
			this.getFormHM().put("chartTypeList", chartTypeList);
			
			int i=0;
		    this.frowset=dao.search(statsql.toString());
		    while(this.frowset.next())
		    {
		    	if((userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
	    		{
		    	    CommonData data=new CommonData();
		    	    data.setDataName(this.getFrowset().getString("name"));
		    	    data.setDataValue(this.getFrowset().getString("id"));
		    	   	statlist.add(data);
		    	   	if(i==0)
			    	{
			    		this.getFormHM().put("statid",this.getFrowset().getString("id"));
			    		if("1".equals(this.getFrowset().getString("type")))
			    			this.getFormHM().put("isonetwostat","1");
			    		else
			    			this.getFormHM().put("isonetwostat","2");
			    		
			    	}
			    	i++;
	    		}		    	
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("showcharttype",showcharttype);
		this.getFormHM().put("showstatname",showstatname);
		this.getFormHM().put("statlist",statlist);
		this.getFormHM().put("statlistsize",statlist.size()+"");
	}
	
	/*
	 *根据id判断该统计项是否存在 
	 */
	private boolean getStatItem(String statid){
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sqlName="select * from sname where id="+statid;
		try {
			this.frowset=dao.search(sqlName);
			if(this.frowset.next())
			{
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
}
