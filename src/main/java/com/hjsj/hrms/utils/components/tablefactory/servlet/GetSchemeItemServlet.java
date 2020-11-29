package com.hjsj.hrms.utils.components.tablefactory.servlet;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetSchemeItemServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		  String schemeItemKey = req.getParameter("schemeItemKey");
		  String node = req.getParameter("node");
		  ArrayList dataList = new ArrayList();
		  String outStr = "[]";
		  //如果是schemeItemKey==All 并且 node是root，说明加载全部指标,创建指标分类
		  if("ALL".equals(schemeItemKey) && "root".equals(node))
			  outStr = "[{text:'单位指标集',id:'B',leaf:false},{text:'岗位指标集',id:'K',leaf:false},{text:'人员指标集',id:'A',leaf:false}]";
		  else{
			  if(",A,B,K,".indexOf(","+node+",") != -1){
				  schemeItemKey = node;
				  node = "root";
			  }
			  dataList = getItemData(schemeItemKey,node);
			  dataList = field2Node(dataList);
			  outStr = JSON.toString(dataList);
		  }
		  
		  resp.setCharacterEncoding("UTF-8");
		  resp.getWriter().write("{children:"+outStr+"}");
		
	}

	
	private ArrayList getItemData(String schemeItemKey,String node){
		//如果node不是root说明是展开的子集，直接根据子集setid查询field
		if(!"root".equals(node)){
			return DataDictionary.getFieldList(node, Constant.USED_FIELD_SET);
		}
		
		// 获取人员信息集
		if("A".equals(schemeItemKey))
			  return DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
		// 获取单位信息集
		if("B".equals(schemeItemKey))
			  return DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		// 获取岗位信息集
		if("K".equals(schemeItemKey))
			  return DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
		
		//如果上面条件都不满足，说明是自定义数据，从数据map中获取数据
		if(TableFactoryBO.SchemeItemDataHM.containsKey(schemeItemKey))
            return (ArrayList)TableFactoryBO.SchemeItemDataHM.get(schemeItemKey);
		
		//走到这里说明schemeItemKey传值错误，返回空list
		return new ArrayList();
	}
	
	private ArrayList field2Node(ArrayList dataList){
		ArrayList treeItemList = new ArrayList();
		if(dataList.isEmpty())
			return dataList;
		//1是fieldset，2是fielditem
		int type = dataList.get(0) instanceof FieldSet ? 1 : 2;
		FieldItem fi = null;
		for(int i=0;i<dataList.size();i++){
			HashMap treeItem = new HashMap();
			Object o = dataList.get(i);
			if(type==1){
				treeItem.put("id",((FieldSet)o).getFieldsetid());
				treeItem.put("text",((FieldSet)o).getFieldsetdesc());
				treeItem.put("leaf", Boolean.FALSE);
			}else{
				fi = (FieldItem)o;
				if(("0").equals(fi.getState()))
					continue;
				treeItem.put("id",fi.getItemid());
				treeItem.put("text",fi.getItemdesc());
				treeItem.put("checked",Boolean.FALSE);
				treeItem.put("itemtype", fi.getItemtype());
				treeItem.put("leaf", Boolean.TRUE);
			}
			
			treeItemList.add(treeItem);
		}
		
		return treeItemList;
	}
}
