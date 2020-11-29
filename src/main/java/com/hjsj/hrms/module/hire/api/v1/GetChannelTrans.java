package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.businessobject.sys.cms.Cms_ChannelBo;
import com.hjsj.hrms.module.hire.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GetChannelTrans  extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList list = new ArrayList();
		ResultSet rs = null;
		PositionBo pBo = new PositionBo(this.frameconn, this.userView);
		String type = (String) this.getFormHM().get("type");
		try {
			HashMap return_data = new HashMap();
			StringBuffer sql = new StringBuffer("");
			String content = "";
			if("content".equalsIgnoreCase(type)){
				String channelId = (String) this.getFormHM().get("channel_id");
				list.clear();
				list.add(channelId);
				sql.append("select content from t_cms_content where channel_id = ?");
				rs = dao.search(sql.toString(), list);
				
				if(rs.next())
					content = rs.getString("content");
				
				return_data.put("content", content);
				this.getFormHM().put("return_msg","");
	            this.getFormHM().put("return_code","success");
			}else if("list".equalsIgnoreCase(type)){
				String state = (String) this.getFormHM().get("state");
				Cms_ChannelBo chlbo=new Cms_ChannelBo(this.frameconn);
				ArrayList channelList = new ArrayList();
				ArrayList fristList = chlbo.getChildList("1");
				
				for (int i = 0; i < fristList.size(); i++) {
					HashMap map = new HashMap();
					LazyDynaBean listBean=(LazyDynaBean)fristList.get(i);
					String hireChannel = ""; 
					if("1".equalsIgnoreCase(state) && "0".equalsIgnoreCase((String)listBean.get("visible")))
						 continue;
					
					ArrayList contentList = chlbo.getContentList((String)listBean.get("channel_id"));
					map.put("id", (String)listBean.get("channel_id"));
					map.put("name", (String)listBean.get("name"));
					map.put("icon_url", (String)listBean.get("icon_url"));
					if(contentList != null){
						LazyDynaBean dynabean=(LazyDynaBean)contentList.get(0);
						String params = (String)dynabean.get("params");
					    map.put("link", (String)dynabean.get("out_url"));
					    map.put("params", params);
					    map.put("content_type", (String)dynabean.get("content_type"));
					    if(params.contains("hireChannel=")) {
					    	hireChannel = params.substring(params.indexOf("hireChannel=")+12);
					    	map.put("target", "_self");
					    }else if(dynabean.get("out_url")!=null&&StringUtils.isNotBlank((String)dynabean.get("out_url"))) {
					    	map.put("target", (String)dynabean.get("target"));
					    }else
					    	//内容只能本窗口打开
					    	map.put("target", "_self");
					    map.put("hireChannel", hireChannel);
					    map.put("content_id", (String)dynabean.get("content_id"));
					}else{
						 map.put("link", "");
						 map.put("params", "");
						 map.put("target", "");
						 map.put("content_id", "");
						 map.put("icon_url", "");
					}
				    ArrayList childList = chlbo.getChildList((String)listBean.get("channel_id"));
				
    				ArrayList childArray = new ArrayList();
    				for (int j = 0; j < childList.size(); j++) {
    				    hireChannel = "";
    					HashMap childmap = new HashMap();
    					listBean=(LazyDynaBean)childList.get(j);
    				    contentList = chlbo.getContentList((String)listBean.get("channel_id"));
    					childmap.put("id", (String)listBean.get("channel_id"));
    					childmap.put("name", (String)listBean.get("name"));
    					childmap.put("icon_url", (String)listBean.get("icon_url"));
    					childmap.put("content_type", (String)listBean.get("content_type"));
    					if(contentList != null){
    						LazyDynaBean dynabean=(LazyDynaBean)contentList.get(0);
    						childmap.put("link", (String)dynabean.get("out_url"));
    						String params = (String)dynabean.get("params");
    						childmap.put("params", params);
    						if(params.contains("hireChannel=")) {
                                hireChannel = params.substring(params.indexOf("hireChannel=")+12);
                                childmap.put("target", "_self");
                            }else
                                childmap.put("target", (String)dynabean.get("target"));
    						childmap.put("hireChannel", hireChannel);
    						childmap.put("target", (String)dynabean.get("target"));
    						childmap.put("content_id", (String)dynabean.get("content_id"));
    					}else{
    						childmap.put("link", "");
    						childmap.put("params", "");
    						childmap.put("target", "");
    						childmap.put("content_id", "");
    					}
    					childArray.add(childmap);
    				}
    				map.put("children", childArray);
    				//内容和链接都为空时不显示频道
    				if((contentList!=null&&contentList.size()>0)||(childArray!=null&&childArray.size()>0))
    					channelList.add(map);
				}

				return_data.put("channels", channelList);
				//注册截止时间
				boolean register_flag  = true;//默认是可以注册的
				String regEndTime = RecruitUtilsBo.getRegisterEndTime();
				//判断注册是否已截止
				if(StringUtils.isNotEmpty(regEndTime)) {
					String format = "yyyy-MM-dd HH:mm";
					Date endtime = DateUtils.getDate(regEndTime, format);
					Date now  =  new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(format);
					now = DateUtils.getDate(sdf.format(now), format);
					if(now.after(endtime)) {//如果过了截止时间
						register_flag = false;
					}
				}
				return_data.put("register_flag",register_flag);
				this.getFormHM().put("return_msg","");
				this.getFormHM().put("return_code","success");
			} else if("getNewPosition".equals(type)) {
			    String return_msg = "";
			    String return_code = "success";
			    ArrayList<String> channelIdList = (ArrayList) this.formHM.get("channelIdList");
			    int size = (Integer) (this.formHM.get("size")==null?2:this.formHM.get("size"));
			    ArrayList newPosList = pBo.getNewPositions(channelIdList, size);
			    if(CollectionUtils.isNotEmpty(newPosList)) {
			        return_code = newPosList.get(0).toString();
			        if("configFail".equals(return_code)) {
			            return_msg = "请在配置参数中，配置招聘对象指标！";
			        }else if("fail".equals(return_code)) {
			            return_msg = "数据处理异常！";
			        }else {
			            return_data.put("newPosList", newPosList);
			        }
			    }
                this.getFormHM().put("return_msg",return_msg);
                this.getFormHM().put("return_code",return_code);
			}
			this.getFormHM().put("return_data",return_data);
			
			}catch (Exception e) {
				this.getFormHM().put("return_code", "fail");
				this.getFormHM().put("return_msg",e.getMessage());
				e.printStackTrace();
			} finally {
				PubFunc.closeResource(rs);
			}
	}
}