package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.net.URLEncoder;
import java.util.HashMap;

public class ShowTabsetTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8881910717974854415L;

	public void execute() throws GeneralException {
		String categories = (String)this.getFormHM().get("categories");
		categories = ResourceFactory.getProperty(categories);
		String history = (String)this.getFormHM().get("history");
		history = history==null?"":history;
		categories = categories==null?"":categories;
		categories = categories.trim();
		history = history.trim();
		String infokind = (String)this.getFormHM().get("infokind");
		String chart_type =(String)this.getFormHM().get("chart_type");
		HashMap activetab = (HashMap)this.getFormHM().get("activetab");
		String label_enabled = (String)this.getFormHM().get("label_enabled");
		label_enabled = label_enabled==null||label_enabled.length()==0?"true":label_enabled;
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		//各页签是否显示返回按钮
		String showReturn = hm.get("showreturn") != null ? (String)hm.get("showreturn") : "1";
		    
		StringBuffer extjsitems = new StringBuffer("var tabs;");
		try{
			if(categories.length()==0){
				if(!activetab.containsKey("###")){
					activetab.put("###", new Integer(0));	
				}
			}else{
				if(!activetab.containsKey(categories)){
					activetab.put(categories, new Integer(0));	
				}
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(!"1".equals(history)){
				if(categories.length()==0){
					this.frowset=dao.search("select id,name,type from sname where (categories='' or categories is null) and infokind="+infokind/*+" and type='1'"*/ +" order by snorder");
				}else{
					this.frowset=dao.search("select id,name,type from sname where categories='"+categories+"' and infokind="+infokind/*+" and type='1'"*/+" order by snorder");
				}
				
				//extjsitems
				
				/**
				 * Ext.onReady(function(){
					var tabs = new Ext.TabPanel({
				        renderTo: document.body,
				        activeTab: 0,
				        enableTabScroll:true,
				        width:document.body.offsetWidth-9,
				        height:document.body.offsetHeight,
				        plain:true,
				        defaults:{autoScroll: true},
				        items:[{
			                title: '<font size=2>职称分布</font>',
			                html:'<iframe src="/general/static/commonstatic/statshowmsgchart.do?b_msgchart=link&statid=31&chart_type=11" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe>'
			            }]
				    });
				    if(itemshtml.length==2)
				    	tabs.hide();
				 });
				 */
				/* 去掉总裁桌面滚动条 xiaoyun 2014-5-15 start */
				extjsitems.append("Ext.onReady(function(){tabs = Ext.create('Ext.tab.Panel', {width:'99%', height:'100%',border :1,items:[");
				//extjsitems.append("Ext.onReady(function(){tabs = new Ext.TabPanel({renderTo: document.body, activeTab: 0,enableTabScroll:true,width:document.body.clientWidth,height:document.body.offsetHeight-1,plain:true,defaults:{autoScroll: false},border :false,resizeTabs:true,items:[");
				/* 去掉总裁桌面滚动条 xiaoyun 2014-5-15 end */
				boolean flag = true;
				while(this.frowset.next()){
					String id = this.frowset.getString("id");
					String name = this.frowset.getString("name");
					String type=this.frowset.getString("type");
					if(this.userView.isHaveResource(3, id)){
						if(!flag){
							extjsitems.append(",{title: '<font size=2>"+name.replaceAll("-", "—")+"</font>',");
							/* 领导桌面滚动条问题优化(水电八局) xiaoyun 2014-8-13 start */
							if("1".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshowmsgchart.do?b_msgchart=link&statid="+id+"&label_enabled="+label_enabled+"&chart_type="+chart_type+"&showreturn="+showReturn+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");//去掉了scrolling=\"no\"
							}else if("2".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshow.do?b_doubledata=data&infokind="+infokind+"&label_enabled="+label_enabled+"&statid="+id+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else{
								String url = "/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind;
								url = URLEncoder.encode(url, "GBK");
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url="+url+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
								//extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}
							
						}else{
							extjsitems.append("{title: '<font size=2>"+name.replaceAll("-", "—")+"</font>',");
							if("1".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshowmsgchart.do?b_msgchart=link&statid="+id+"&label_enabled="+label_enabled+"&chart_type="+chart_type+"&showreturn="+showReturn+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else if("2".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshow.do?b_doubledata=data&infokind="+infokind+"&label_enabled="+label_enabled+"&statid="+id+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else{
								String url = "/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind;
								url = URLEncoder.encode(url, "GBK");
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url="+url+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
								//extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}
							/* 领导桌面滚动条问题优化(水电八局) xiaoyun 2014-8-13 end */
						}
						flag =false;
					}
				}
				if(flag)
					extjsitems.append("{}");
				extjsitems.append("]});");
				if(flag){
					extjsitems.append("tabs.hide();");
				}
				
				extjsitems.append("var viewport = new Ext.Viewport({enableTabScroll:true,layout:'fit',padding: '5,0,0,0',items:[tabs]});viewport.show();");
				extjsitems.append("});");
			}else{
				if(categories.length()==0){
					this.frowset=dao.search("select id,name,type from hr_hisdata_sname where (categories='' or categories is null) and infokind="+infokind/*+" and type='1'"*/ +" order by snorder");
				}else{
					this.frowset=dao.search("select id,name,type from hr_hisdata_sname where categories='"+categories+"' and infokind="+infokind/*+" and type='1'"*/+" order by snorder");
				}
				//extjsitems//
				//extjsitems//
				
				/**
				 * Ext.onReady(function(){
					var tabs = new Ext.TabPanel({
				        renderTo: document.body,
				        activeTab: 0,
				        enableTabScroll:true,
				        width:document.body.offsetWidth-9,
				        height:document.body.offsetHeight,
				        plain:true,
				        defaults:{autoScroll: true},
				        items:[{
			                title: '<font size=2>职称分布</font>',
			                html:'<iframe src="/general/static/commonstatic/history/statshow.do?b_msgchart=link&statid=31&chart_type=11" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe>'
			            }]
				    });
				    if(itemshtml.length==2)
				    	tabs.hide();
				 });
				 */
				/* 去掉总裁桌面滚动条 xiaoyun 2014-5-15 start */
				extjsitems.append("Ext.onReady(function(){tabs = Ext.create('Ext.tab.Panel', { width:'99%', height:'100%',border :1,items:[");
				//extjsitems.append("Ext.onReady(function(){tabs = new Ext.TabPanel({renderTo: document.body, activeTab: 0,enableTabScroll:true,width:document.body.clientWidth,height:document.body.offsetHeight-1,plain:true,defaults:{autoScroll: false},border :false,resizeTabs:true,items:[");
				/* 去掉总裁桌面滚动条 xiaoyun 2014-5-15 end */
				boolean flag = true;
				while(this.frowset.next()){
					String id = this.frowset.getString("id");
					String name = this.frowset.getString("name");
					String type=this.frowset.getString("type");
					if(this.userView.isHaveResource(3, id)){
						if(!flag){
							extjsitems.append(",{title: '<font size=2>"+name.replaceAll("-", "—")+"</font>',");
							/* 领导桌面滚动条问题优化(水电八局) xiaoyun 2014-8-13 start */
							if("1".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/history/statshow.do?b_chart=chart&infokind="+infokind+"&statid="+id+"&label_enabled="+label_enabled+"&bidesk=true&showreturn="+showReturn+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else if("2".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshow.do?b_doubledata=data&infokind="+infokind+"&label_enabled="+label_enabled+"&statid="+id+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else{
								String url = "/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind;
								url = URLEncoder.encode(url,"GBK");
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url="+url+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
								//extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}
						}else{
							extjsitems.append("{title: '<font size=2>"+name.replaceAll("-", "—")+"</font>',");
							if("1".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/history/statshow.do?b_chart=chart&infokind="+infokind+"&statid="+id+"&label_enabled="+label_enabled+"&bidesk=true&showreturn="+showReturn+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else if("2".equals(type)){
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/static/commonstatic/statshow.do?b_doubledata=data&infokind="+infokind+"&label_enabled="+label_enabled+"&statid="+id+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}else{
								String url = "/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind;
								url = URLEncoder.encode(url,"GBK");
								extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url="+url+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
								//extjsitems.append("html:'<iframe name="+id+" src=\"/general/muster/hmuster/processBar.jsp?url=/general/deci/statics/crosstab.do?b_show=link`statid="+id+"`infokind="+infokind+"\"  width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>'}");
							}
							/* 领导桌面滚动条问题优化(水电八局) xiaoyun 2014-8-13 end */
						}
						flag =false;
					}
				}
				if(flag)
					extjsitems.append("{}");
				extjsitems.append("]});");
				if(flag){
					extjsitems.append("tabs.hide();");
				}
				
				extjsitems.append("var viewport = new Ext.Viewport({enableTabScroll:true,layout:'fit',padding: '5,0,0,0',items:[tabs]});viewport.show();");
				extjsitems.append("});");
			}
			
		}catch(Exception e){
			extjsitems.append("");
			e.printStackTrace();
		}finally{
			this.getFormHM().put("extjsitems", extjsitems.toString());
		}
	}
}
