package com.hjsj.hrms.businessobject.org;

import com.hjsj.hrms.businessobject.hire.FilterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldSet;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddOrgInfo {
      private Connection conn;
	  public AddOrgInfo()
	  {
		  
	  }
	  public AddOrgInfo(Connection conn)
	  {
		  this.conn=conn;
	  }
	  public HashMap getVOrgMess(String code)
	    {
	    	StringBuffer strsql=new StringBuffer();
			strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype ");
			strsql.append(" FROM vorganization "); 		
			strsql.append(" WHERE ");
			strsql.append(" codeitemid='"+code+"'");
			HashMap hash=new HashMap();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=null;
			try
			{
				rs=dao.search(strsql.toString());
				if(rs.next())
				{
					hash.put("codeitemdesc",rs.getString("codeitemdesc"));
					hash.put("codeitemid",code);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally
		     {
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		     }  
			return hash;
	    }
	    public String getChildCodeitemid(String codeitem,String code,int len)
	    {
	    	String childtiem="";
	    	if(codeitem.length()>code.length())
	    	{
	    		
	    		childtiem=getAddChildItem(codeitem,code,len);    		
	    	}else
	    	{
	    		if(len>=3)
	    			//childtiem="001";
                {
                    childtiem="01";
                } else if(len<3&&len>=2) {
                    childtiem="01";
                } else if(len<2&&len>=1) {
                    childtiem="1";
                } else {
                    childtiem=codeitem;
                }
	    		//childtiem=code+childtiem;
	    	}
	    	return childtiem;
	    }
	  /**
	     * 如果有同级的编码那么+1;
	     * @param item
	     * @param code
	     * @return
	     */
	    public String getAddChildItem(String item,String code, int len)
	    {
	    	String childitme=item.substring(code.length());		
			ArrayList list=new ArrayList();
			StringBuffer num_str=new StringBuffer();
			boolean isCorrect=true;
			int i=1;
			while(isCorrect)
			{
				if(i>childitme.length()) {
                    break;
                }
				String one_s=childitme.substring(childitme.length()-i,childitme.length()-i+1);
				if(one_s==null||one_s.length()<=0) {
                    break;
                }
				if(!java.lang.Character.isDigit(one_s.charAt(0))){
					isCorrect=false;
					break;
				 }else
				 {
					 list.add(one_s);
					 i++;
				 }
			}
			if(i==1&&!isCorrect) {
                return item;
            }
			for(int r=list.size()-1;r>=0;r--)
			{
				num_str.append(list.get(r));
			}
			int num=Integer.parseInt(num_str.toString());
			num=num+1;
			String num_s=num+"";
			int zone_len=num_str.length()-num_s.length();
			num_str.delete(0,num_str.length());
			for(int r=0;r<zone_len;r++)
			{
				num_str.append("0");
			}
			num_str.append(num_s);
			childitme=item.substring((item.length()-childitme.length()),(item.length()-list.size()));
			childitme=childitme+num_str.toString();		
		    return childitme;
	    }
	    /**
	     * 过滤机构管理，信息维护，过滤子集指标
	     * @param infoSetList
	     * @return
	     */
	    public  List filtrationFieldSet(List infoSetList)
	    {
	    	if(infoSetList==null||infoSetList.size()<=0) {
                return infoSetList;
            }
	    	List newlist=new ArrayList();
	    	FilterSetBo bo = new FilterSetBo(this.conn);
	    	bo.putParameters("constant","constant", "GZ_PARAM", 0, "", "/Params/ins_base_set", 1, ",", "str_value", 1,1);
			bo.putParameters("constant","constant", "GZ_PARAM", 0, "", "/Params/base_set", 1, ",", "str_value", 1,1);
	    	HashMap fieldSetMap=bo.getFieldSetMap();
	    	Iterator it = fieldSetMap.keySet().iterator();
	    	StringBuffer strb=new StringBuffer();
			while (it.hasNext()) {
				String temp = (String) it.next();
				strb.append(temp+",");
			}
			if(strb==null||strb.length()<=0) {
                return infoSetList;
            }
			for(int i=0;i<infoSetList.size();i++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(i);
				if(strb.toString().toUpperCase().indexOf(fieldset.getFieldsetid().toUpperCase())==-1) {
                    newlist.add(fieldset);
                }
			}
			return newlist;
	    }
	    /**
	     * 过滤指定子集
	     * @param infoSetList
	     * @param xzzeSetid
	     * @return
	     */
	    public  List filtrationFieldSet(List infoSetList,String setid)
	    {
	    	if(infoSetList==null||infoSetList.size()<=0) {
                return infoSetList;
            }
	    	List newlist=new ArrayList();	    	
			if(setid==null||setid.length()<=0) {
                return infoSetList;
            }
			for(int i=0;i<infoSetList.size();i++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(i);
				if(setid.toString().toUpperCase().indexOf(fieldset.getFieldsetid().toUpperCase())==-1) {
                    newlist.add(fieldset);
                }
			}
			return newlist;
	    }
	    /**
	     * 过滤机构管理，信息维护，过滤字段指标
	     * @param infoFieldViewList
	     * @return
	     */
	    public List filtrationFieldItem(List infoFieldViewList)
	    {

	    	if(infoFieldViewList==null||infoFieldViewList.size()<=0) {
                return infoFieldViewList;
            }
	    	List newlist=new ArrayList();
	    	try
			{
	    		String contentTypeField="";				
				ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
				String IDs=parameterXMLBo.getBriefParaValue();
				if(IDs!=null&&IDs.trim().length()>0){
					if(IDs.indexOf(",")!=-1){
				       String str_a[] = IDs.split(",");				      
				       contentTypeField=str_a[1];
					}
				}
				FilterSetBo bo = new FilterSetBo(this.conn);
				bo.putValue(contentTypeField, 0);
				HashMap fieldMap = bo.getFieldMap();
				Iterator it = fieldMap.keySet().iterator();
		    	StringBuffer strb=new StringBuffer();
				while (it.hasNext()) {
					String temp = (String) it.next();
					strb.append(temp+",");
				}
				if(strb==null||strb.length()<=0) {
                    return infoFieldViewList;
                }
				for(int i=0;i<infoFieldViewList.size();i++)
				{
					FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
					
					if(strb.indexOf(fieldItemView.getItemid().toUpperCase())!=-1 && "43".equals(fieldItemView.getCodesetid())){
						//过滤掉 系统参数》单位介绍》内容形式指标  并且 此指标关联了43代码类  的指标
					}else {
                        newlist.add(fieldItemView);
                    }
				}
			}catch(Exception e)
			{
				  e.printStackTrace();
			}
			return newlist;
	    }
	    
	    public synchronized String GetNext(String src,String des)
		{
	    	String result="";
	    	if((src==null || src.trim().length()<=0)&&(des==null ||des.trim().length()<=0)) {
                return "01";
            }
			int nI,nTag;
			String ch;			
			nTag=1;    //进位为1
			src = src==null?"":src;
			src=src.toUpperCase();
			for(nI=src.length();nI>des.length();nI--)
			{
				ch=src.substring(nI-1,nI);
				if(nTag==1) {
                    ch=GetNextChar(ch);
                }
				result=ch+result;
				if("0".equals(ch) && !"0".equals(src.subSequence(nI-1,nI)))
				{
					nTag=1;
				}
				else
				{
					nTag=0;
				}
				
			}	
			if(result.length()<1) {
                result="01";
            }
			return result;
		}
		
		public String  GetNextChar(String ch)                   //获得下一个进位
		{
			String result="";
			switch(ch.charAt(0))
			{
				case '0':
				{
					result="1";
					break;
				}
				case '1':
				{
					result="2";
					break;
				}
				case '2':
				{
					result="3";
					break;
				}
				case '3':
				{
					result="4";
					break;
				}
				case '4':
				{
					result="5";
					break;
				}
				case '5':
				{
					result="6";
				   break;
				}
				case '6':
				{
					result="7";
					break;
				}
				case '7':
				{
					result="8";
					break;
				}
				case '8':
				{
					result="9";
					break;
				}
				case '9':
				{
					result="A";
					break;
				}
				case 'A':
				{
					result="B";
					break;
				}
				case 'B':
				{
					result="C";
					break;
				}
				case 'C':
				{
					result="D";
					break;
				}
				case 'D':
				{
					result="E";
					break;
				}
				case 'E':
				{
					result="F";
					break;
				}
				case 'F':
				{
					result="G";
					break;
				}
				case 'G':
				{
					result="H";
					break;
				}
				case 'H':
				{
					result="I";
					break;
				}
				case 'I':
				{
					result="J";
					break;
				}
				case 'J':
				{
					result="K";
					break;
				}
				case 'K':
				{
					result="L";
					break;
				}
				case 'L':
				{
					result="M";
					break;
				}
				case 'M':
				{
					result="N";
					break;
				}
				case 'N':
				{
					result="O";
					break;
				}
				case 'O':
				{
					result="P";
					break;
				}
				case 'P':
				{
					result="Q";
					break;
				}
				case 'Q':
				{
					result="R";
					break;
				}
				case 'R':
				{
					result="S";
					break;
				}
				case 'S':
				{
					result="T";
					break;
				}
				case 'T':
				{
					result="U";
					break;
				}
				case 'U':
				{
					result="V";
					break;
				}
				case 'V':
				{
					result="W";
					break;
				}
				case 'W':
				{
					result="X";
					break;
				}
				case 'X':
				{
					result="Y";
					break;
				}
				case 'Y':
				{
					result="Z";
					break;
				}
				case 'Z':
				{
					result="0";
					break;
				}
			}
		  return result;	
		}
		public synchronized int getLayer(String parentid,String codeitemid,String codesetid) throws Exception{
			ResultSet rs = null;
			int layer=0;
			try{
				String sql = "select codesetid,layer from organization where codeitemid='"+parentid+"' union select codesetid,layer from vorganization where codeitemid='"+parentid+"'";
				ContentDAO dao =new ContentDAO(this.conn);
				rs = dao.search(sql);
				if(rs.next()){
					layer = rs.getInt("layer");
					if(layer!=0){
						String pcodesetid=rs.getString("codesetid");
						if(pcodesetid.equalsIgnoreCase(codesetid)){
							layer++;
						}else{
							layer=1;
						}
					}
				}else{
					layer=1;
				}
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				if(rs!=null) {
                    rs.close();
                }
			}
			return layer;
		}
		public void executeInitLayer() throws Exception {
			// TODO Auto-generated method stub
			StringBuffer sql = new StringBuffer();
			ContentDAO dao =new ContentDAO(this.conn);
			try {
				sql.append(SetLayerNull("organization"));
				dao.update(sql.toString());
				sql.delete(0,sql.length());
				sql.append(InitLayer("organization"));
				dao.update(sql.toString());
				sql.delete(0,sql.length());
				int i=1;
				while(true){
					sql.append(NextLayer("organization",i));
					int j = dao.update(sql.toString());
					if(j==0) {
                        break;
                    }
					i++;
					sql.delete(0,sql.length());
				}
				sql.delete(0,sql.length());
				sql.append(SetLayerNull("vorganization"));
				dao.update(sql.toString());
				sql.delete(0,sql.length());
				sql.append(InitLayer("vorganization"));
				dao.update(sql.toString());
				sql.delete(0,sql.length());
				i=1;
				while(true){
					sql.append(NextLayer("vorganization",i));
					int j = dao.update(sql.toString());
					if(j==0) {
                        break;
                    }
					i++;
					sql.delete(0,sql.length());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		private String SetLayerNull(String tbname){
			String sql = "update "+tbname+" set layer = null";
			return sql;
		}
		private String InitLayer(String tbname){
			String sql = "update "+tbname+" set layer=1 where (codeitemid=parentid) or "+
		    " not (parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid))";
			return sql;
		}
		private String NextLayer(String tbname,int lay){
			String sql = "update "+tbname+" set layer='"+(lay+1)+"' where codeitemid<>parentid and "+
		       " parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid and B.layer='"+lay+"')";;
		    return sql;
		}
}
