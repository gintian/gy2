package com.hjsj.hrms.interfaces.report;

import com.hjsj.hrms.utils.PubFunc;

public class ReportParseXml {
   /**
    * 解析xml中传来的数据
    * @param xmlContent xml的内容
    * @param xpath 查找路径
    * @return ReportParseVo
    * */
   public ReportParseVo ReadOutParseXml(String xmlContent,String xpath)
   {
	   ReportParseVo parsevo= new ReportParseVo();//解析器vo
	   ReportParameterXml parameterXML = new ReportParameterXml();
	   ReportParamterVo paramtervo= parameterXML.ReadOutParameterXml(xmlContent,xpath);//xml参数vo
	   /**报表基本参数**/
	   if(paramtervo!=null)
	   {
		 parsevo.setName(paramtervo.getName());
	     
	     parsevo.setValue(paramtervo.getValue());
	     parsevo.setPagetype(paramtervo.getPagetype());
	     parsevo.setWidth(paramtervo.getWidth());
	     parsevo.setHeight(paramtervo.getHeight());
	     parsevo.setOrientation(paramtervo.getOrientation());
	     parsevo.setUnit(paramtervo.getUnit());
	     parsevo.setLeft(paramtervo.getLeft()!=null&&paramtervo.getLeft().length()>0?paramtervo.getLeft():"20");
	     parsevo.setRight(paramtervo.getRight()!=null&&paramtervo.getRight().length()>0?paramtervo.getRight():"20");
	     parsevo.setTop(paramtervo.getTop()!=null&&paramtervo.getTop().length()>0?paramtervo.getTop():"20");
	     parsevo.setBottom(paramtervo.getBottom()!=null&&paramtervo.getBottom().length()>0?paramtervo.getBottom():"20");
	     /**节点，页眉**/	     
	     /**节点,报表标题**/
	     String title_content=paramtervo.getTitle_c();
	     parsevo.setTitle_fb(parseContent("#fb",title_content));
	     parsevo.setTitle_fi(parseContent("#fi",title_content));
	     parsevo.setTitle_fu(parseContent("#fu",title_content));
	     parsevo.setTitle_fn(parseContent("#fn",title_content));
	     parsevo.setTitle_fz(parseContent("#fz",title_content));
	     parsevo.setTitle_h(parseContent("#fh",title_content));
	     parsevo.setTitle_fw(parseContent("#fw",title_content));
	     parsevo.setTitle_fs(parseContent("#fs",title_content));//删除线
	     parsevo.setTitle_fc(parseContent("#fc",title_content));//颜色
	     /**节点,报表表头**/
	     String head_content=paramtervo.getHead_c();
	     parsevo.setHead_c(parseContent("#c",head_content));
	     parsevo.setHead_d(parseContent("#d",head_content));
	     parsevo.setHead_e(parseContent("#e",head_content));
	     parsevo.setHead_u(parseContent("#u",head_content));
	     parsevo.setHead_p(parseContent("#p",head_content));
	     parsevo.setHead_t(parseContent("#t",head_content));
	     parsevo.setHead_fb(parseContent("#fb",head_content));
	     parsevo.setHead_fi(parseContent("#fi",head_content));
	     parsevo.setHead_fu(parseContent("#fu",head_content));
	     parsevo.setHead_fn(parseContent("#fn",head_content));
	     parsevo.setHead_fz(parseContent("#fz",head_content));
	     parsevo.setHead_h(parseContent("#fh",head_content));
	     parsevo.setHead_fw(parseContent("#fw",head_content));
	     parsevo.setHead_fs(parseContent("#fs",head_content));//删除
	     parsevo.setHead_fc(parseContent("#fc",head_content));//颜色
	     parsevo.setHead_flw(parseContent("#flw",head_content));//左上
	     parsevo.setHead_fmw(parseContent("#fmw",head_content));//左中
	     parsevo.setHead_frw(parseContent("#frw",head_content));//左下
	     /**节点,报表表尾**/
	     String tile_content=paramtervo.getTile_c();
	     parsevo.setTile_c(parseContent("#c",tile_content));
	     parsevo.setTile_d(parseContent("#d",tile_content));
	     parsevo.setTile_e(parseContent("#e",tile_content));
	     parsevo.setTile_u(parseContent("#u",tile_content));
	     parsevo.setTile_p(parseContent("#p",tile_content));
	     parsevo.setTile_t(parseContent("#t",tile_content));
	     parsevo.setTile_fb(parseContent("#fb",tile_content));
	     parsevo.setTile_fi(parseContent("#fi",tile_content));
	     parsevo.setTile_fu(parseContent("#fu",tile_content));
	     parsevo.setTile_fn(parseContent("#fn",tile_content));
	     parsevo.setTile_fz(parseContent("#fz",tile_content));
	     parsevo.setTile_h(parseContent("#fh",tile_content));
	     parsevo.setTile_fw(parseContent("#fw",tile_content));
	     parsevo.setTile_flw(parseContent("#flw",tile_content));
	     parsevo.setTile_frw(parseContent("#frw",tile_content));
	     parsevo.setTile_fmw(parseContent("#fmw",tile_content));
	     parsevo.setTile_fs(parseContent("#fs",tile_content));//删除
	     parsevo.setTile_fc(parseContent("#fc",tile_content));//颜色
	     /**节点,报表表体**/
	     String body_content=paramtervo.getBody_c();
	     parsevo.setBody_fb(parseContent("#fb",body_content));
	     parsevo.setBody_fi(parseContent("#fi",body_content));
	     parsevo.setBody_fu(parseContent("#fu",body_content));
	     parsevo.setBody_fn(parseContent("#fn",body_content));
	     parsevo.setBody_fz(parseContent("#fz",body_content));
	     parsevo.setBody_pr(parseContent("#pr",body_content));
	     parsevo.setBody_rn(parseContent("#rn",body_content));	
	     parsevo.setBody_fc(parseContent("#fc",body_content)); 
	     parsevo.setBody_dept(parseContent("#dept",body_content));
	     parsevo.setBody_pos(parseContent("#pos",body_content));
	     parsevo.setBody_gh(parseContent("#gh",body_content));
	     parsevo.setBody_kqfu(parseContent("#kqfu",body_content));
	     parsevo.setBody_tjxm(parseContent("#tjxm",body_content));
	     
	     
	   }
	   return parsevo;
   }
   /***
    * 解析前台传来的参数
    * 
    * 
    * */
   public String  WriteOutParseXml(String xmlContent,String xpath,ReportParseVo parsevo)
   {   
	   ReportParamterVo paramtervo=new ReportParamterVo();
	   if(parsevo!=null)
	   {
		   /**报表基本参数**/
		   paramtervo.setName(parsevo.getName()); //报表名称
		   
		   paramtervo.setPagetype(parsevo.getPagetype());//报表纸张
		   
		   paramtervo.setUnit(parsevo.getUnit());//报表长度单位
		   paramtervo.setOrientation(parsevo.getOrientation());//纸张方向
		   paramtervo.setTop(parsevo.getTop());//报表头边距
		   paramtervo.setLeft(parsevo.getLeft());//报表左边距
		   paramtervo.setRight(parsevo.getRight());//报表右边距				   
		   paramtervo.setBottom(parsevo.getBottom());//报表尾边距
		   paramtervo.setValue(parsevo.getValue());//值
		   paramtervo.setWidth(parsevo.getWidth());//纸的长宽
		   paramtervo.setHeight(parsevo.getHeight());
		  
		   /**节点,报表标题**/
		   StringBuffer title_content = new StringBuffer();
		   title_content.append(setFb(parsevo.getTitle_fb())+","+setFi(parsevo.getTitle_fi())+","+setFu(parsevo.getTitle_fu())+","+setFs(parsevo.getTitle_fs()));
		   title_content.append(","+setLableVale("#fn",parsevo.getTitle_fn()));
		   title_content.append(","+setLableVale("#fz",parsevo.getTitle_fz()));
		   title_content.append(","+setLableVale("#fh",parsevo.getTitle_h()));
		   title_content.append(","+replaceSignWrite("#fw",parsevo.getTitle_fw()));	
		   title_content.append(","+setLableVale("#fc",parsevo.getTitle_fc()));
		   paramtervo.setTitle_c(title_content.toString());
		   /**节点,报表表头**/
		   StringBuffer head_content = new StringBuffer();		  
		   head_content.append(PubFunc.hireKeyWord_filter_reback(parsevo.getHead_c())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getHead_d())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getHead_e()));
		   head_content.append(","+PubFunc.hireKeyWord_filter_reback(parsevo.getHead_p())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getHead_t())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getHead_u()));
		   head_content.append(","+setFb(parsevo.getHead_fb())+","+setFi(parsevo.getHead_fi())+","+setFu(parsevo.getHead_fu())+","+setFs(parsevo.getHead_fs()));
		   head_content.append(","+setLableVale("#fn",parsevo.getHead_fn()));
		   head_content.append(","+setLableVale("#fz",parsevo.getHead_fz()));
		   head_content.append(","+setLableVale("#fh",parsevo.getHead_h()));
		   head_content.append(","+setLableVale("#fc",parsevo.getHead_fc()));
		   head_content.append(","+replaceSignWrite("#fw",parsevo.getHead_fw()));
		   head_content.append(","+replaceSignWrite("#flw",parsevo.getHead_flw()));
		   head_content.append(","+replaceSignWrite("#fmw",parsevo.getHead_fmw()));
		   head_content.append(","+replaceSignWrite("#frw",parsevo.getHead_frw()));
		   
		   paramtervo.setHead_c(head_content.toString());
		   /**节点,报表表尾**/
		   StringBuffer tile_content = new StringBuffer();		   
		   tile_content.append(PubFunc.hireKeyWord_filter_reback(parsevo.getTile_c())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getTile_d())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getTile_e()));
		   tile_content.append(","+PubFunc.hireKeyWord_filter_reback(parsevo.getTile_p())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getTile_t())+","+PubFunc.hireKeyWord_filter_reback(parsevo.getTile_u()));
		   tile_content.append(","+setFb(parsevo.getTile_fb())+","+setFi(parsevo.getTile_fi())+","+setFu(parsevo.getTile_fu())+","+setFs(parsevo.getTile_fs()));
		   tile_content.append(","+setLableVale("#fn",parsevo.getTile_fn()));
		   tile_content.append(","+setLableVale("#fz",parsevo.getTile_fz()));
		   tile_content.append(","+setLableVale("#fh",parsevo.getTile_h()));
		   tile_content.append(","+setLableVale("#fc",parsevo.getTile_fc()));
		   tile_content.append(","+replaceSignWrite("#fw",parsevo.getTile_fw()));
		   tile_content.append(","+replaceSignWrite("#flw",parsevo.getTile_flw()));
		   tile_content.append(","+replaceSignWrite("#fmw",parsevo.getTile_fmw()));
		   tile_content.append(","+replaceSignWrite("#frw",parsevo.getTile_frw()));
		   paramtervo.setTile_c(tile_content.toString());
		   /**节点,报表表体**/
		   //#fb[0],#fi[0],#fu[0],#pr[0],#fn宋体,#fz10,		   
		   StringBuffer body_content = new StringBuffer();		   
		   body_content.append(setFb(parsevo.getBody_fb())+","+setFi(parsevo.getBody_fi())+","+setFu(parsevo.getBody_fu()));
		   body_content.append(",");
		   body_content.append(setDept(parsevo.getBody_dept()));
		   body_content.append(",");
		   body_content.append(setPos(parsevo.getBody_pos()));
		   body_content.append(",");
		   body_content.append(setGh(parsevo.getBody_gh()));
		   body_content.append(",");
		   body_content.append(setKqfu(parsevo.getBody_kqfu()));
		   body_content.append(",");
		   body_content.append(setTjxm(parsevo.getBody_tjxm()));
           body_content.append(","+setLableVale("#fn",parsevo.getBody_fn()));
           body_content.append(","+setLableVale("#fz",parsevo.getBody_fz())); 
           body_content.append(","+setLableVale("#fc",parsevo.getBody_fc())); 
           body_content.append(","+setPr(PubFunc.hireKeyWord_filter_reback(parsevo.getBody_pr())));
           body_content.append(","+setBody_Rn(PubFunc.hireKeyWord_filter_reback(parsevo.getBody_pr()),parsevo.getBody_rn()));           
           paramtervo.setBody_c(body_content.toString());		   
		   ReportParameterXml parameterXML = new ReportParameterXml();
		   xmlContent=parameterXML.WriteOutParameterXml(xmlContent,xpath,paramtervo);
		   
	   }
	   return xmlContent;
   }
   /***
    * 解析前台传来的参数
    * 
    * 
    * */
   public ReportParamterVo  getReportParamterVoFromReportParseVo(ReportParseVo parsevo)
   {   
	   ReportParamterVo paramtervo=new ReportParamterVo();
	   if(parsevo!=null)
	   {
		   /**报表基本参数**/
		   paramtervo.setName(parsevo.getName()); //报表名称
		   
		   paramtervo.setPagetype(parsevo.getPagetype());//报表纸张
		   
		   paramtervo.setUnit(parsevo.getUnit());//报表长度单位
		   paramtervo.setOrientation(parsevo.getOrientation());//纸张方向
		   paramtervo.setTop(parsevo.getTop());//报表头边距
		   paramtervo.setLeft(parsevo.getLeft());//报表左边距
		   paramtervo.setRight(parsevo.getRight());//报表右边距				   
		   paramtervo.setBottom(parsevo.getBottom());//报表尾边距
		   paramtervo.setValue(parsevo.getValue());//值
		   paramtervo.setWidth(parsevo.getWidth());//纸的长宽
		   paramtervo.setHeight(parsevo.getHeight());
		  
		   /**节点,报表标题**/
		   StringBuffer title_content = new StringBuffer();
		   title_content.append(setFb(parsevo.getTitle_fb())+","+setFi(parsevo.getTitle_fi())+","+setFu(parsevo.getTitle_fu())+","+setFs(parsevo.getTitle_fs()));
		   title_content.append(","+setLableVale("#fn",parsevo.getTitle_fn()));
		   title_content.append(","+setLableVale("#fz",parsevo.getTitle_fz()));
		   title_content.append(","+setLableVale("#fh",parsevo.getTitle_h()));
		   title_content.append(","+replaceSignWrite("#fw",parsevo.getTitle_fw()));	
		   title_content.append(","+setLableVale("#fc",parsevo.getTitle_fc()));
		   paramtervo.setTitle_c(title_content.toString());
		   
		   /**节点,报表表头**/
		   StringBuffer head_content = new StringBuffer();		  
		   head_content.append(parsevo.getHead_c()+","+parsevo.getHead_d()+","+parsevo.getHead_e());
		   head_content.append(","+parsevo.getHead_p()+","+parsevo.getHead_t()+","+parsevo.getHead_u());
		   head_content.append(","+setFb(parsevo.getHead_fb())+","+setFi(parsevo.getHead_fi())+","+setFu(parsevo.getHead_fu())+","+setFs(parsevo.getHead_fs()));
		   head_content.append(","+setLableVale("#fn",parsevo.getHead_fn()));
		   head_content.append(","+setLableVale("#fz",parsevo.getHead_fz()));
		   head_content.append(","+setLableVale("#fh",parsevo.getHead_h()));
		   head_content.append(","+setLableVale("#fc",parsevo.getHead_fc()));
		   head_content.append(","+replaceSignWrite("#fw",parsevo.getHead_fw()));
		   head_content.append(","+replaceSignWrite("#flw",parsevo.getHead_flw()));
		   head_content.append(","+replaceSignWrite("#fmw",parsevo.getHead_fmw()));
		   head_content.append(","+replaceSignWrite("#frw",parsevo.getHead_frw()));		   
		   paramtervo.setHead_c(head_content.toString());
		   		   /**节点,报表表尾**/
		   StringBuffer tile_content = new StringBuffer();		   
		   tile_content.append(parsevo.getTile_c()+","+parsevo.getTile_d()+","+parsevo.getTile_e());
		   tile_content.append(","+parsevo.getTile_p()+","+parsevo.getTile_t()+","+parsevo.getTile_u());
		   tile_content.append(","+setFb(parsevo.getTile_fb())+","+setFi(parsevo.getTile_fi())+","+setFu(parsevo.getTile_fu())+","+setFs(parsevo.getTile_fs()));
		   tile_content.append(","+setLableVale("#fn",parsevo.getTile_fn()));
		   tile_content.append(","+setLableVale("#fz",parsevo.getTile_fz()));
		   tile_content.append(","+setLableVale("#fh",parsevo.getTile_h()));
		   tile_content.append(","+setLableVale("#fc",parsevo.getTile_fc()));
		   tile_content.append(","+replaceSignWrite("#fw",parsevo.getTile_fw()));
		   tile_content.append(","+replaceSignWrite("#flw",parsevo.getTile_flw()));
		   tile_content.append(","+replaceSignWrite("#fmw",parsevo.getTile_fmw()));
		   tile_content.append(","+replaceSignWrite("#frw",parsevo.getTile_frw()));
		   paramtervo.setTile_c(tile_content.toString());
		   //System.out.println("报表表尾----------------->"+tile_content.toString());
		   /**节点,报表表体**/
		   //#fb[0],#fi[0],#fu[0],#pr[0],#fn宋体,#fz10,		   
		   StringBuffer body_content = new StringBuffer();		   
		   body_content.append(setFb(parsevo.getBody_fb())+","+setFi(parsevo.getBody_fi())+","+setFu(parsevo.getBody_fu()));
           body_content.append(","+setLableVale("#fn",parsevo.getBody_fn()));
           body_content.append(","+setLableVale("#fz",parsevo.getBody_fz())); 
           body_content.append(","+setLableVale("#fc",parsevo.getBody_fc())); 
           body_content.append(","+setPr(parsevo.getBody_pr()));
           body_content.append(","+setBody_Rn(parsevo.getBody_pr(),parsevo.getBody_rn()));           
           paramtervo.setBody_c(body_content.toString());   
          
	   }
	   return paramtervo;
   }
   /********以下方法对属性进行解析*************/
   public String setFb(String fb)
   {
	  fb = PubFunc.hireKeyWord_filter_reback(fb);
	  if(fb!=null&&fb.length()>0)
	  {
		  if("#fb[1]".equals(fb))
		   {
			   return fb;
		   }else
		   {
			   return "#fb[0]"; 
		   } 
	  } else
	  {
		  return "#fb[0]"; 
	  }
	   
   }
   public String setFi(String fi)
   {
	   fi = PubFunc.hireKeyWord_filter_reback(fi);
	   if(fi!=null&&fi.length()>0)
		  {
			  if("#fi[1]".equals(fi))
			   {
				   return fi;
			   }else
			   {
				   return "#fi[0]"; 
			   } 
		  } else
		  {
			  return "#fi[0]"; 
		  }
   }
   public String setFu(String fu)
   {
	   fu = PubFunc.hireKeyWord_filter_reback(fu);
	   if(fu!=null&&fu.length()>0)
		  {
			  if("#fu[1]".equals(fu))
			   {
				   return fu;
			   }else
			   {
				   return "#fu[0]"; 
			   } 
		  } else
		  {
			  return "#fu[0]"; 
		  }
   }
   public String setFs(String fs)
   {
	   fs = PubFunc.hireKeyWord_filter_reback(fs);
	   if(fs!=null&&fs.length()>0)
		  {
			  if("#fs[1]".equals(fs))
			   {
				   return fs;
			   }else
			   {
				   return "#fs[0]"; 
			   } 
		  } else
		  {
			  return "#fs[0]"; 
		  }
   }
   public String setPr(String pr)
   {
	   pr = PubFunc.hireKeyWord_filter_reback(pr);
	   if(pr!=null&&pr.length()>0)
	   {
		   	if("#pr[1]".equals(pr))
  	     	{
	  		   return pr;
	  	     }else{
	  		   return "#pr[0]"; 
	  	     } 
	   }else{
    	 return "#pr[0]"; 
	   }
	   
   }
   public String setDept(String dept)
   {
	   dept = PubFunc.hireKeyWord_filter_reback(dept);
	   if(dept!=null&&dept.length()>0)
	   {
    	 if("#dept[1]".equals(dept))
  	     {
  		   return dept;
  	     }else{
  		   return "#dept[0]"; 
  	     } 
	   }else{
    	 return "#dept[0]"; 
	   }
   }
   public String setPos(String pos)
   {
	   pos = PubFunc.hireKeyWord_filter_reback(pos);
	   if(pos!=null&&pos.length()>0)
	   {
    	 if("#pos[1]".equals(pos))
    	 {
  		   return pos;
  	     }else{
  		   return "#pos[0]"; 
  	     } 
	   }else{
		   return "#pos[0]"; 
	   }
	   
   }
   public String setGh(String gh)
   {
	   gh = PubFunc.hireKeyWord_filter_reback(gh);
	   if(gh!=null&&gh.length()>0){
    	 if("#gh[1]".equals(gh)){
  		   return gh;
  	     }else{
  		   return "#gh[0]"; 
  	     } 
     }else{
    	 return "#gh[0]"; 
     }
   }
   public String setKqfu(String kqfu)
   {
	   kqfu = PubFunc.hireKeyWord_filter_reback(kqfu);
	   if(kqfu!=null&&kqfu.length()>0)
	   {
    	 if("#kqfu[1]".equals(kqfu))
  	     {
  		   return kqfu;
  	     }else{
  		   return "#kqfu[0]"; 
  	     } 
	   }else{
    	 return "#kqfu[0]"; 
	   }
   }
   public String setTjxm(String tjxm)
   {
	   tjxm = PubFunc.hireKeyWord_filter_reback(tjxm);
	   if(tjxm!=null&&tjxm.length()>0)
	   {
    	 if("#tjxm[1]".equals(tjxm)){
  		   return tjxm;
  	     }else{
  		   return "#tjxm[0]"; 
  	     } 
     }else{
    	 return "#tjxm[0]"; 
     }
   }
   public String setBody_Rn(String pr,String rn)
   {
	   if(pr==null||pr.length()<=0)
	   {
		   return "#rn"; 
	   }
	   if("#pr[1]".equals(pr)&&rn.length()>0&&rn!=null)
	   {
		   return "#rn"+rn;
	   }else
	   {
		   return "#rn"; 
	   }
   }
   /***
    * 处理特殊标示
    * @param lable 标示
    * @param value
    * @return lable+value
    * **/
   public String setLableVale(String label,String value)
   {
	   String lablevale="";	   
	   if(value==null||value.length()<=0)
	   {
		   value="";
	   }
	   if("#fn".equals(label))
	   {
		   if(value!=null&&value.length()>0)
		   {
			   lablevale=label+value;
		   }else
		   {
			   lablevale=label+"宋体";
		   }
	   }else if("#fz".equals(label))
	   {
		   if(value!=null&&value.length()>0)
		   {
			   lablevale=label+value;
		   }else
		   {
			   lablevale=label+"12";
		   }
	   }else if("#fh".equals(label))
	   {
		   if(value!=null&&value.length()>0)
		   {
			   lablevale=label+value;
		   }else
		   {
			   lablevale=label+"40";
		   }
	   }else if("#fc".equals(label))
	   {
		   if(value!=null&&value.length()>0)
		   {
			   lablevale=label+value;
		   }else
		   {
			   lablevale=label+"#000000";
		   }
	   }else
	   {
		   lablevale=label+value;
	   }
	   return lablevale;
   }
   /**
    * @param lable 标示
    * @param xmlconter 属性内容
    * @return 属性内容中对应标示的值
    * */
   public String parseContent(String label,String xmlconter)
   {
    //	 #p,#c,#e,#u,#d,#t,#fb[0],#fi[0],#fu[0],#fn,#fz10
	    String label_value="";	    
	    if(xmlconter==null||xmlconter.length()<=0)
	    	return "";
	    if("#p".equals(label)|| "#c".equals(label)|| "#e".equals(label)|| "#u".equals(label)|| "#d".equals(label)|| "#t".equals(label))
	    {
	    	/******页眉,页脚特有属性******/
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		label_value=label;	    		
	    	}
	    }else if("#fb".equals(label)|| "#fi".equals(label)|| "#fu".equals(label)|| "#fs".equals(label)|| "#pr".equals(label)|| "#dept".equals(label)|| "#pos".equals(label)|| "#gh".equals(label)|| "#kqfu".equals(label)|| "#tjxm".equals(label))
	    {
	    	/******字体||黑体||斜体||下划线*******
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			String value=xmlconter.substring(i,r);
	    			String [] values=value.split("");
	    			label_value=values[5];
	    			
	    		}else
	    		{
	    			String value=xmlconter.substring(i);
	    			String [] values=value.split("");
	    			label_value=values[5];
	    		}	   		
	    	}*/ 
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			label_value=xmlconter.substring(i,r);
	    		}else
	    		{
	    			label_value=xmlconter.substring(i);	    			
	    		}	    		
	    	}
	    }else if("#fn".equals(label)|| "#fz".equals(label)|| "#fh".equals(label)|| "#rn".equals(label)|| "#fc".equals(label))
	    {
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			label_value=xmlconter.substring(i+3,r);
	    		}else
	    		{
	    			label_value=xmlconter.substring(i+3);	    			
	    		}	    		
	    	}
	    }else if("#fw".equals(label))
	    {
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			String value=xmlconter.substring(i+3,r);	    			
	    			label_value=value.replaceAll("#@",",");	    			
	    		}else
	    		{
	    			String value=xmlconter.substring(i+3);
	    			label_value=value.replaceAll("#@",",");
	    		}	    		
	    	}
	    }else if("#flw".equals(label)|| "#frw".equals(label)|| "#fmw".equals(label))
	    {
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			String value=xmlconter.substring(i+4,r);	    			
	    			label_value=value.replaceAll("#@",",");	    			
	    		}else
	    		{
	    			String value=xmlconter.substring(i+4);
	    			label_value=value.replaceAll("#@",",");
	    		}	    		
	    	}
	    }    
	    else{
	    	int i=xmlconter.indexOf(label);
	    	if(i!=-1)
	    	{
	    		int r=xmlconter.indexOf(",",i);
	    		if(r!=-1)
	    		{
	    			label_value=xmlconter.substring(i+3,r);
	    		}else
	    		{
	    			label_value=xmlconter.substring(i+3);	    			
	    		}	    		
	    	}	    	
	    }   
	   return label_value;
   } 
   public String replaceSignWrite(String fw,String conter)
   {	     
	   String label_value="";
	   if(conter!=null&&conter.length()>0){
	     int r=conter.indexOf(",");
	     if(r!=-1)
	     {		
		   String value=conter.replaceAll(",","#@");
		   label_value=fw+value;
	 		
	     }else
	     {
		   label_value=fw+conter;
	     }
	   }
	   return label_value;	   
   }  
   /**
    * 解析xml中传来的数据
    * @param xmlContent xml的内容
    * @param xpath 查找路径
    * @return ReportParseVo
    * */
   public ReportParseVo parameterToParse( ReportParamterVo paramtervo)
   {
	   ReportParseVo parsevo= new ReportParseVo();//解析器vo	   
	   /**报表基本参数**/
	   if(paramtervo!=null)
	   {
		 parsevo.setName(paramtervo.getName());
	     
	     parsevo.setValue(paramtervo.getValue());
	     parsevo.setPagetype(paramtervo.getPagetype());
	     parsevo.setWidth(paramtervo.getWidth());
	     parsevo.setHeight(paramtervo.getHeight());
	     parsevo.setOrientation(paramtervo.getOrientation());
	     parsevo.setUnit(paramtervo.getUnit());
	     parsevo.setLeft(paramtervo.getLeft()!=null&&paramtervo.getLeft().length()>0?paramtervo.getLeft():"20");
	     parsevo.setRight(paramtervo.getRight()!=null&&paramtervo.getRight().length()>0?paramtervo.getRight():"20");
	     parsevo.setTop(paramtervo.getTop()!=null&&paramtervo.getTop().length()>0?paramtervo.getTop():"20");
	     parsevo.setBottom(paramtervo.getBottom()!=null&&paramtervo.getBottom().length()>0?paramtervo.getBottom():"20");
	     /**节点，页眉**/	     
	     /**节点,报表标题**/
	     String title_content=paramtervo.getTitle_c();
	     parsevo.setTitle_fb(parseContent("#fb",title_content));
	     parsevo.setTitle_fi(parseContent("#fi",title_content));
	     parsevo.setTitle_fu(parseContent("#fu",title_content));
	     parsevo.setTitle_fn(parseContent("#fn",title_content));
	     parsevo.setTitle_fz(parseContent("#fz",title_content));
	     parsevo.setTitle_h(parseContent("#fh",title_content));
	     parsevo.setTitle_fw(parseContent("#fw",title_content));
	     parsevo.setTitle_fs(parseContent("#fs",title_content));//删除线
	     parsevo.setTitle_fc(parseContent("#fc",title_content));//颜色
	     /**节点,报表表头**/
	     String head_content=paramtervo.getHead_c();
	     parsevo.setHead_c(parseContent("#c",head_content));
	     parsevo.setHead_d(parseContent("#d",head_content));
	     parsevo.setHead_e(parseContent("#e",head_content));
	     parsevo.setHead_u(parseContent("#u",head_content));
	     parsevo.setHead_p(parseContent("#p",head_content));
	     parsevo.setHead_t(parseContent("#t",head_content));
	     parsevo.setHead_fb(parseContent("#fb",head_content));
	     parsevo.setHead_fi(parseContent("#fi",head_content));
	     parsevo.setHead_fu(parseContent("#fu",head_content));
	     parsevo.setHead_fn(parseContent("#fn",head_content));
	     parsevo.setHead_fz(parseContent("#fz",head_content));
	     parsevo.setHead_h(parseContent("#fh",head_content));
	     parsevo.setHead_fw(parseContent("#fw",head_content));
	     parsevo.setHead_fs(parseContent("#fs",head_content));//删除
	     parsevo.setHead_fc(parseContent("#fc",head_content));//颜色
	     parsevo.setHead_flw(parseContent("#flw",head_content));//左上
	     parsevo.setHead_fmw(parseContent("#fmw",head_content));//左中
	     parsevo.setHead_frw(parseContent("#frw",head_content));//左下
	     /**节点,报表表尾**/
	     String tile_content=paramtervo.getTile_c();
	     parsevo.setTile_c(parseContent("#c",tile_content));
	     parsevo.setTile_d(parseContent("#d",tile_content));
	     parsevo.setTile_e(parseContent("#e",tile_content));
	     parsevo.setTile_u(parseContent("#u",tile_content));
	     parsevo.setTile_p(parseContent("#p",tile_content));
	     parsevo.setTile_t(parseContent("#t",tile_content));
	     parsevo.setTile_fb(parseContent("#fb",tile_content));
	     parsevo.setTile_fi(parseContent("#fi",tile_content));
	     parsevo.setTile_fu(parseContent("#fu",tile_content));
	     parsevo.setTile_fn(parseContent("#fn",tile_content));
	     parsevo.setTile_fz(parseContent("#fz",tile_content));
	     parsevo.setTile_h(parseContent("#fh",tile_content));
	     parsevo.setTile_fw(parseContent("#fw",tile_content));
	     parsevo.setTile_flw(parseContent("#flw",tile_content));
	     parsevo.setTile_frw(parseContent("#frw",tile_content));
	     parsevo.setTile_fmw(parseContent("#fmw",tile_content));
	     parsevo.setTile_fs(parseContent("#fs",tile_content));//删除
	     parsevo.setTile_fc(parseContent("#fc",tile_content));//颜色
	     /**节点,报表表体**/
	     String body_content=paramtervo.getBody_c();
	     parsevo.setBody_fb(parseContent("#fb",body_content));
	     parsevo.setBody_fi(parseContent("#fi",body_content));
	     parsevo.setBody_fu(parseContent("#fu",body_content));
	     parsevo.setBody_fn(parseContent("#fn",body_content));
	     parsevo.setBody_fz(parseContent("#fz",body_content));
	     parsevo.setBody_pr(parseContent("#pr",body_content));
	     parsevo.setBody_rn(parseContent("#rn",body_content));	
	     parsevo.setBody_fc(parseContent("#fc",body_content)); 
	     
	   }
	   return parsevo;
   }
}
