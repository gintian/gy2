package com.hjsj.hrms.businessobject.stat.crosstab;

import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 交叉表统计
 * <p>create time 2014.9.11</p>
 * @author genglz
 * @version 1.0
 */
public class CrossTabStat {
    
    /** 一级纵向维度 */
    private ArrayList vItems = new ArrayList();

    /** 一级横向维度 */
    private ArrayList hItems = new ArrayList();

    /** 全部纵向/横向、一级/二级维度 */
    private ArrayList allStatItems = new ArrayList();
    
    /** 行 */
    private ArrayList rows = new ArrayList();
    
    /** 列 */
    private ArrayList cols = new ArrayList();
    
    /** 人员库 */
    private String[] bases = new String[0];

    /** 常用查询条件 */
    private String queryExpr = "";
    private String queryFactor = "";
    
    private UserView userView;
    
    private Connection conn;
    
    /** 统计结果数据集 */
    private RowSet statData = null;
    
    /** 单元格数据 */
    private int[][] intValues = null;
    
    /** 列合计数据 */
    private int[] colSums = null;

    /** 行合计数据 */
    private int[] rowSums = null;
    
    /** 总计 */
    private int intTotal = 0;
    
    /** 是否机选纵向统计值 */
    private String htotal;
    
    /** 是否计算横向统计值 */
    private String vtotal;
    
    /**
     * 设置横向(列)、纵向(行)维度<br>
     * 一级维度: 1_+常用统计id; 二级维度: 2_+常用统计id
     * @param h 横向(列)
     * @param v 纵向(行)
     */
    public void setHVItems(String[] h, String[] v) {
        allStatItems.clear();
        setStatItems(h, hItems);
        setTabItems(hItems, cols);
        setStatItems(v, vItems);
        setTabItems(vItems, rows);
    }
    
    private void setStatItems(String[] s, ArrayList items) {
        items.clear();
        StatItem level1 = null;
        StatItem level2 = null;
        for(int i=0;i<s.length;i++){
            if(s[i].startsWith("1_")) {
                level1 = new StatItem();
                level1.load(s[i].substring(2));
                items.add(level1);
                allStatItems.add(level1);
            }
            else if(s[i].startsWith("2_")) {
                level2 = new StatItem();
                level2.load(s[i].substring(2));
                level1.addSubItem(level2);
                allStatItems.add(level2);
            }
        }
    }
    
    private void setTabItems(ArrayList stats, ArrayList tabItems) {
        tabItems.clear();
        for(int i=0;i<stats.size();i++){
            StatItem level1 = (StatItem)stats.get(i);
            for(int j=0;j<level1.getConds().size();j++) {
                StatCond cond1 = (StatCond)level1.getConds().get(j);
                if(level1.hasSubItems()) {
                    for(int k=0;k<level1.getSubItems().size();k++) {
                        StatItem level2 = (StatItem)level1.getSubItems().get(k);
                        for(int m=0;m<level2.getConds().size();m++) {
                            StatCond cond2 = (StatCond)level2.getConds().get(m);
                            CrossTabItem tabItem = new CrossTabItem();
                            tabItem.setLevel1(level1);
                            tabItem.setCond1(cond1);
                            tabItem.setLevel2(level2);
                            tabItem.setCond2(cond2);
                            tabItems.add(tabItem);
                        }
                    }
                }
                else {
                    CrossTabItem tabItem = new CrossTabItem();
                    tabItem.setLevel1(level1);
                    tabItem.setCond1(cond1);
                    tabItems.add(tabItem);
                }
            }
        }
    }
    
    public void setBases(String[] b) {
        bases = b;
    }
    
    public void setQueryCond(String expr, String factor) {
        queryExpr = expr;
        queryFactor = factor;
    }
    
    private String getStatFldName(StatItem stat) {
        return "C" + stat.getId();
    }
    private String getCondFldName(StatCond cond){
    		return "I" + cond.getId();
    }
    
    private String getSqlFld(StatItem stat, String base) {
        StringBuffer result = new StringBuffer();
        for(int j=0;j<stat.getConds().size();j++) {
            StatCond cond1 = (StatCond)stat.getConds().get(j);
            try {
//                if(status == 4)
//                    parser.setBself(true);
//                if(!parser.existError())
//                    throw new GeneralException(parser.getErrorInfo().toString());
//                fieldlist.addAll(parser.getFieldList());
//                if(ninfor == 1 && !bHistory)
//                {
//                    parser.setSuper_admin(super_admin);
//                    strsql.append(parser.getPrivSqlExpression());
//                } else
//                {
//                    strsql.append(parser.getSqlExpression());
//                }
                result.append(" ,case when ").
                append(getCondSql(cond1,base)).append(" then '").
                append(cond1.getName()).
                append("' end ").
                append(getStatFldName(stat)).
                append("_").
                append(getCondFldName(cond1));
            }
            catch (Exception e){
                
            }
        }
        return result.toString();
    }
    
    private String getSqlFlds(String base) {
        String flds = "'"+base+"' base";
        for(int j=0;j<allStatItems.size();j++){
            StatItem stat = (StatItem)allStatItems.get(j);
            flds += getSqlFld(stat, base);
        }
        return flds;
    }
    
    private ArrayList getSetList() {
        ArrayList sets = new ArrayList();
        sets.add(DataDictionary.getFieldSetVo("A01"));
        for(int i=0;i<allStatItems.size();i++){
            StatItem stat = (StatItem)allStatItems.get(i);
            for(int j=0;j<stat.getConds().size();j++) {
                StatCond cond1 = (StatCond)stat.getConds().get(j);
                FactorList parser = new FactorList(cond1.getExpr(), cond1.getFactor(), "", false, false, true, 1, userView.getUserName());
                try {
//                    if(status == 4)
//                        parser.setBself(true);
//                    if(!parser.existError())
//                        throw new GeneralException(parser.getErrorInfo().toString());
//                    fieldlist.addAll(parser.getFieldList());
//                    if(ninfor == 1 && !bHistory)
//                    {
//                        parser.setSuper_admin(super_admin);
//                        strsql.append(parser.getPrivSqlExpression());
//                    } else
//                    {
//                        strsql.append(parser.getSqlExpression());
//                    }
                    ArrayList flds = parser.getFieldList();
                    for(int k=0;k<flds.size();k++) {
                        FieldItem fld = (FieldItem)flds.get(k);
                        FieldSet set = DataDictionary.getFieldSetVo(fld.getFieldsetid());
                        if(sets.indexOf(set) == -1) {
                            sets.add(set);
                        }
                    }
                }
                catch (Exception e){
                    
                }
            }
        }
        return sets;
    }
    
    private String getSqlFrom(String base) {
        String result = base + "A01";
        ArrayList setlist = getSetList();
        
        for(int i=0;i<setlist.size();i++){
            FieldSet set = (FieldSet)setlist.get(i);
            if("A01".equals(set.getFieldsetid())) {
                continue;
            }
            
            switch (set.getInfoGroup().getInforid()) {
            case 1: {
                result += " LEFT JOIN (SELECT A0100,MAX(I9999) MAXI9999 FROM "+ base + set.getFieldsetid() +
                                        " GROUP BY A0100) MAX" + set.getFieldsetid() + 
                          " ON MAX"+ set.getFieldsetid() +".A0100="+base+"A01.A0100";
                result += " LEFT JOIN "+base + set.getFieldsetid() +
                          " ON "+base+set.getFieldsetid()+".A0100="+base+"A01.A0100 AND "+
                                 base+set.getFieldsetid()+".I9999=MAX"+set.getFieldsetid()+".MAXI9999";
                break;
            }
            case 2: {

                /* 机构的单独查询了，此处不再联查 guodd 2020-02-19
                if(set.isMainset()){
                    result += " LEFT JOIN B01 ON ("+base+"A01.B0110 = B01.B0110 or "+base+"A01.E0122 = B01.B0110 )";//漏了部门-导致多维统计数不对 wangb 2020-01-08
                }
                else {
                    result += " LEFT JOIN (SELECT B0110,MAX(I9999) MAXI9999 FROM "+ set.getFieldsetid() +
                                        " GROUP BY B0110) MAX" + set.getFieldsetid() + 
                              " ON (MAX"+ set.getFieldsetid() +".B0110="+base+"A01.B0110 OR MAX"+ set.getFieldsetid() +".B0110="+base+".A01.E0122 )";//漏了部门-导致多维统计数不对 wangb 2020-01-08
                    result += " LEFT JOIN "+set.getFieldsetid() +
                              " ON ("+set.getFieldsetid()+".B0110="+base+"A01.B0110 OR "+set.getFieldsetid()+".B0110="+base+".A01.E0122 ) AND "+//漏了部门-导致多维统计数不对 wangb 2020-01-08
                                     set.getFieldsetid()+".I9999=MAX"+set.getFieldsetid()+".MAXI9999";
                }
                 */
                break;
            }
            case 3: {
                if(set.isMainset()){
                    result += " LEFT JOIN K01 ON "+base+"A01.E01A1 = K01.E01A1";
                }
                else {
                    result += " LEFT JOIN (SELECT E01A1,MAX(I9999) MAXI9999 FROM "+ set.getFieldsetid() +
                                        " GROUP BY E01A1) MAX" + set.getFieldsetid() + 
                              " ON MAX"+ set.getFieldsetid() +".E01A1="+base+"A01.E01A1";
                    result += " LEFT JOIN "+set.getFieldsetid() +
                              " ON "+set.getFieldsetid()+".E01A1="+base+"A01.E01A1 AND "+
                                     set.getFieldsetid()+".I9999=MAX"+set.getFieldsetid()+".MAXI9999";
                }
                break;
            }
            }
        }
        return result;
    }
    
    private String getSqlWhere(String base) {
        String result = "";
        try {
        	String from = "";
            if(queryExpr != null && queryExpr.length() > 0) {
            	ArrayList fieldlist=new ArrayList();
            	from = userView.getPrivSQLExpression(queryExpr+"|"+queryFactor, base, false, false, true, fieldlist);
            }else{
            	if(userView.isSuper_admin()){
            		return "";
            	}
            	from = userView.getPrivSQLExpression(base, true);
            }
            result = base+"A01.A0100 in (select distinct " + base + "A01.A0100" + from + ")";
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 加载数据
     * @see #unload()
     */
    public void load() {
    		/**
    		 * 改此处bug分析实现逻辑异常痛苦，为方便后期其他人员维护时容易理解，特在此写上多维统计数据加载实现逻辑：
    		 * 一、sql 将多维统计横纵向每个 条件项 组到一起进行查询，符合条件的值为 条件项民称，不符合条件的为null
    		 * 		例如：横向 统计项分为：男、女；纵向统计项为：A部门、B部门
    		 * 		sql为： select count(cnt) ,男统计项id,女统计项id,A部门统计项id,B部门统计项id from (
    		 * 					select count(1) as cnt,
    		 * 					case when 男条件 then 男 end as 男统计项id,
    		 * 					case 女条件 then 女 end as 女统计项id,
    		 * 					case A部门条件 then A部门 end as A部门统计项id,
    		 * 					case B部门条件 then B部门 end as B部门统计项id 
    		 * 					from 人员库+A01（如果有需要join 子集）
    		 * 					where 人员权限范围条件 
    		 * 				) group by 男统计项id,女统计项id,A部门统计项id,B部门统计项id
    		 * 		获得的数据符合的项目条件的字段内有值，不符合的为null
    		 * 二、根据每个单元格对应的横纵统计项获取数据 （calcValues方法）：
    		 * 		循环sql查出的数据，然后读取每条 横纵向统计项的字段值，如果都不为null，说明符合，累加count值
    		 * 		例如：获取满足男和A部门的数据时，读取 男统计项id 字段，A部门统计项id 字段，如果两个字段值都不为null，
    		 * 			 说明这条数据符合这两个统计项，将count保存，然后继续循环做如上匹配，匹配上就累加。
    		 * 	    统计数据，有时统计项之间有包含关系，例如 B部门其实是A部门的子部门，
    		 * 		这时统计数据单纯靠已查询的数据累加计算得到的是错误的，数据会超出实际条数。
    		 * 		所以统计数据需要单独执行sql去查询
    		 */
        String statView = "";
        for(int i=0;i<bases.length;i++){
            String base = bases[i];
            String oneBaseFlds = getSqlFlds(base);
            String oneBaseFrom = getSqlFrom(base);
            String oneBaseWhere = getSqlWhere(base);
            String oneBaseSql = "select " + oneBaseFlds + " from " + oneBaseFrom;
            if(oneBaseWhere.length() > 0) {
                oneBaseSql += " where " + oneBaseWhere;
            }
            if(statView.length() > 0) {
                statView += " union all ";
            }
            statView += oneBaseSql;
        }

        String groupByFlds = "";
        for(int i=0;i<allStatItems.size();i++){
            StatItem stat = (StatItem)allStatItems.get(i);
            String statName = getStatFldName(stat);
            ArrayList condList = stat.getConds();
            for(int k=0;k<condList.size();k++){
	            	if(groupByFlds.length() > 0) {
                        groupByFlds += ",";
                    }
            		StatCond cond = (StatCond)condList.get(k);
            		groupByFlds += statName+"_"+getCondFldName(cond);
            }
        }
        
        String sql = "select count(*) cnt, " + groupByFlds + " from (" + statView + ") a " +
                     //" where " + cond +
                     " group by " + groupByFlds + 
                     " order by " + groupByFlds;
        try
        {
            ContentDAO dao = new ContentDAO(conn);
            if (statData != null) {
                statData.close();
                statData = null;
            }
            statData = dao.search(sql);
            calcValues();
        }catch (Exception  ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * 释放资源
     * @see #load()
     */
    public void unload() {
        try{
            if (statData != null){
                statData.close();
                statData = null;
            }
            intValues = null;
            colSums = null;
            rowSums = null;
        }catch (SQLException e){
            e.printStackTrace();  
        }
    }

    /**
     * 取值
     * @param col 从0开始, 如果col=列数则取合计值
     * @param row 从0开始, 如果row=行数则取合计值
     * @return
     */
    public int getIntValue(int col, int row) {
        if(col == cols.size() && row == rows.size()) {
            return intTotal;
        } else if(col == cols.size()) {
            return rowSums[row];
        } else if(row == rows.size()) {
            return colSums[col];
        } else {
            return intValues[row][col];
        }
    }

    /**
     * 列合计
     * @param col
     * @return
     */
    public int getColSum(int col) {
        if(col == cols.size()) {
            return intTotal;
        } else {
            return colSums[col];
        }
    }
    
    /**
     * 行合计
     * @param row
     * @return
     */
    public int getRowSum(int row) {
        if(row == rows.size()) {
            return intTotal;
        } else {
            return rowSums[row];
        }
    }
    
    /**
     * 总计
     * @return
     */
    public int getIntTotal() {
        return intTotal;
    }
    
    /**
     * 计算值
     * @param col 从0开始
     * @param row 从0开始
     * @return
     */
    private int calcIntValue(int col, int row) {
        if(statData == null) {
            return 0;
        }
        int result = 0;
        if(col >= cols.size() || row >= rows.size()) {
            return 0;
        }
        CrossTabItem colItem = (CrossTabItem)cols.get(col);
        CrossTabItem rowItem = (CrossTabItem)rows.get(row);
        try{
            if(statData.first()) {
                do{
                    if(locateRow(colItem, rowItem)){
                        result += statData.getInt("cnt");
                    }
                }
                while(statData.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 计算各个单元格值，包括行合计、列合计、总计
     */
    private void calcValues() {
    		//二维数组，保存对应坐标的表格单元格的值
        intValues = new int[rows.size()][cols.size()];
        
        intTotal = 0;
        for(int i=0;i<rows.size();i++){
            for(int j=0;j<cols.size();j++) {
            		//计算 坐标为[j,i]表格单元格 j为x坐标，i为y坐标
                intValues[i][j] = calcIntValue(j, i);
            }
        }
        
        //如果需要横向统计   设置了隐藏空行 没有横向统计 报错 wangb 20190731 bug 51129
        int vtotalCount = 0;
        rowSums = new int[rows.size()];
       // if("1".equals(vtotal)){
	        	for(int i=0;i<rows.size();i++){
	        		CrossTabItem row = (CrossTabItem)rows.get(i);
	        		//传入第i行的行统计项，以及横向所有统计项，计算出 第i行的统计数据
	        		rowSums[i] = calcSumValues(row,cols);
	        		vtotalCount+=rowSums[i];
	        	}
        	
       // }
        
        //如果需要纵向统计    设置了隐藏空列  没有纵向统计 报错 wangb 20190731 51129
        int htotalCount = 0;
        colSums = new int[cols.size()];
       // if("1".equals(htotal)){
        		for(int i=0;i<cols.size();i++){
        			CrossTabItem col = (CrossTabItem)cols.get(i);
        			//传入第i列的列统计项，以及纵向所有统计项，计算出 第i列的统计数据
        			colSums[i] = calcSumValues(col,rows);
        			htotalCount+=colSums[i];
        		}
       // }
        
		//计算总数，一般情况横向总值和纵向总值是相等的，但是如果有某两个统计项有包含关系时，
        //统计出的数据会多，横纵向都有这种情况的可能很小，取少的值，数据准确性更大 
//                intTotal = vtotalCount;
//                if(intTotal>htotalCount)
//                		intTotal = htotalCount;      
        /*
         *多维 横向和纵向都合并计算时，此处逻辑不对不能取小的值（有重复人员出现），需要重新计	
         */
        if("1".equalsIgnoreCase(this.vtotal) && "1".equalsIgnoreCase(this.htotal)) {
        	intTotal = clclSumvalues();
        }    
    }
    
    /**
     * 计算多维统计 横纵坐标 统计项 条件和 返回最终的合计值
     * @return
     */
    private int clclSumvalues() {
    	ArrayList lexprFactor=new ArrayList();
    	//纵向统计
    	ArrayList lexprFactorv = new ArrayList();
    	for(int i = 0 ; i < this.hItems.size(); i++) {
    		StatItem statItem = (StatItem) this.hItems.get(i);
    		ArrayList conds = statItem.getConds();
    		ArrayList subItems = statItem.getSubItems();
    		for(int j = 0 ; j < conds.size() ; j++) {
    			StatCond statCond = (StatCond) conds.get(j);
    			String factor = statCond.getFactor();
    			String expr = statCond.getExpr();
    			if(subItems != null && subItems.size() > 0) {
    				for(int m = 0 ; m < subItems.size() ; i++) {
    					StatItem subItem = (StatItem) subItems.get(m);
    					ArrayList subConds = subItem.getConds();
    					for(int n = 0 ; n < subConds.size() ; n++) {
    						StatCond subCond = (StatCond) subConds.get(n);
    						String subfactor = subCond.getFactor();
    						String subexpr = subCond.getExpr();
    						
    						ArrayList factorList = new ArrayList();
    						factorList.add( expr +"|"+ factor);
    						factorList.add( subexpr +"|"+ subfactor);
    						CombineFactor combinefactor=new CombineFactor();
    						String lexprFactorStr=combinefactor.getCombineFactorExpr(factorList,0);
    						StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
    						String strLexprv = "";
    						String strFactorv = "";
    						if(Stok.hasMoreTokens())
    						{
    							strLexprv=Stok.nextToken();
    							strFactorv=Stok.nextToken();
    						}
    						lexprFactorv.add(strLexprv + "|" + strFactorv);
    					}
    				}
    			}else {
    				lexprFactorv.add( expr +"|"+ factor);
    			}	
    		}
    	}
    	//合计公式
		CombineFactor combinefactorv=new CombineFactor();
		String lexprFactorStrv=combinefactorv.getCombineFactorExpr(lexprFactorv,1);
		StringTokenizer Stokv = new StringTokenizer(lexprFactorStrv, "|");
		String vLexprv = "";
		String vFactorv= "";
		if(Stokv.hasMoreTokens())
		{
			vLexprv=Stokv.nextToken();
			vFactorv=Stokv.nextToken();
		}
		lexprFactor.add(vLexprv + "|" + vFactorv);
    	
    	
    	//横向统计
    	ArrayList lexprFactorh = new ArrayList();
    	for(int i = 0 ; i < this.vItems.size(); i++) {
    		StatItem statItem = (StatItem) this.vItems.get(i);
    		ArrayList conds = statItem.getConds();
    		ArrayList subItems = statItem.getSubItems();
    		for(int j = 0 ; j < conds.size() ; j++) {
    			StatCond statCond = (StatCond) conds.get(j);
    			String factor = statCond.getFactor();
    			String expr = statCond.getExpr();
    			if(subItems != null && subItems.size() > 0) {
    				for(int m = 0 ; m < subItems.size() ; i++) {
    					StatItem subItem = (StatItem) subItems.get(m);
    					ArrayList subConds = subItem.getConds();
    					for(int n = 0 ; n < subConds.size() ; n++) {
    						StatCond subCond = (StatCond) subConds.get(n);
    						String subfactor = subCond.getFactor();
    						String subexpr = subCond.getExpr();
    						
    						ArrayList factorList = new ArrayList();
    						factorList.add( expr +"|"+ factor);
    						factorList.add( subexpr +"|"+ subfactor);
    						CombineFactor combinefactor=new CombineFactor();
    						String lexprFactorStr=combinefactor.getCombineFactorExpr(factorList,0);
    						StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
    						String strLexprv = "";
    						String strFactorv = "";
    						if(Stok.hasMoreTokens())
    						{
    							strLexprv=Stok.nextToken();
    							strFactorv=Stok.nextToken();
    						}
    						lexprFactorh.add(strLexprv + "|" + strFactorv);
    					}
    				}
    			}else {
    				lexprFactorh.add( expr +"|"+ factor);
    			}	
    		}
    	}
    	//合计公式
		CombineFactor combinefactorh=new CombineFactor();
		String lexprFactorStrh=combinefactorv.getCombineFactorExpr(lexprFactorh,1);
		StringTokenizer Stokh = new StringTokenizer(lexprFactorStrh, "|");
		String hLexprv = "";
		String hFactorv= "";
		if(Stokh.hasMoreTokens())
		{
			hLexprv=Stokh.nextToken();
			hFactorv=Stokh.nextToken();
		}
		lexprFactor.add(hLexprv + "|" + hFactorv);
		
		int totalValue = 0;
		String infokind = "1";
		CombineFactor combinefactor=new CombineFactor();
		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		String strLexpr = "";
		String strFactor = "";
		if(Stok.hasMoreTokens())
		{
			strLexpr=Stok.nextToken();
			strFactor=Stok.nextToken();
		}
		if(this.queryExpr!=null && this.queryFactor!=null)
		{
			String[] style=getCombinLexprFactor(strLexpr,strFactor,this.queryExpr,this.queryFactor);
		    if(style!=null && style.length==2)
		    {
		    	strLexpr=style[0];
		    	strFactor=style[1];
		    }
		}
		StatCondAnalyse cond = new StatCondAnalyse();
		String strHV = "";
		try {
			strHV = cond.getCondQueryString(
									strLexpr,
									strFactor,
									this.bases[0].toUpperCase(),
									false,
									this.userView.getUserName(),
									null,
									userView,infokind,true);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		//处理多维统计合计穿透，人数不对问题
		FactorList parser = new FactorList(strLexpr, strFactor, this.bases[0].toUpperCase(), true, false, true, 1, userView.getUserName());
		ArrayList fieldList = parser.getFieldList();
		StringBuffer whereSql = new StringBuffer();
		for(int f = 0 ; f < fieldList.size() ; f++) {
			FieldItem fieldItem =(FieldItem)fieldList.get(f);
			if("A01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
				continue;
			}
			whereSql.append(" "+this.bases[0].toUpperCase()+fieldItem.getFieldsetid()+".A0100 is not null and ");
		}
		if(whereSql.length()>0) {
			whereSql.setLength(whereSql.length()-4);
		}
		strHV = strHV.replaceAll("or I9999 IS NULL","or I9999 IS NULL and " + whereSql);
		
		StringBuffer sql = new StringBuffer();
		String tmpSql = "select "+Sql_switcher.isnull("count(1)","0")+" c "+ strHV;
		sql.append(tmpSql);
		if(this.bases.length > 1) {
			for(int i = 1 ; i < this.bases.length ; i++) {
				String dbname = this.bases[i].toUpperCase();
				sql.append(" union all ");
				sql.append(tmpSql.replaceAll(this.bases[0], dbname));
			}
		}
		ContentDAO dao = new ContentDAO(conn);
		try {
			if (statData != null) {
			    statData.close();
			    statData = null;
			}
			statData = dao.search(sql.toString());
			while(statData.next()) {
				totalValue += statData.getInt("c");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalValue;
		
    }
    
 
  //合并表达式
  	public String[] getCombinLexprFactor(String lexpr,String factor,String seclexpr,String secfactor)
  	{
  		String[] style=new String[2];
  		ArrayList lexprFactor=new ArrayList();
  		factor = PubFunc.keyWord_reback(factor);
  		lexprFactor.add(lexpr + "|" + factor);
  		lexprFactor.add(seclexpr + "|" + secfactor);
  		CombineFactor combinefactor=new CombineFactor();
  		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
  		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
  		if(Stok.hasMoreTokens())
  		{
  			style[0]=Stok.nextToken();
  			style[1]=Stok.nextToken();
  		}
  		return style;
  	}
    
    /**
     * 计算合计数据
     * @param mainItem
     * @param calItems
     * @return
     */
    private int calcSumValues(CrossTabItem mainItem,ArrayList calItems){
    		
    	    String nbase = "USR"; 
    		StringBuffer sumSql = new StringBuffer();
    		sumSql.append("select count(1) from ");
    		sumSql.append(getSqlFrom(nbase));
    		sumSql.append(" where ");
    		String whereSql = getSqlWhere(nbase);
    		if(whereSql.length()>0){
    			sumSql.append(whereSql+" and ");
    		}
    		/**mark 括号 one**/
    		sumSql.append(" (");
	    		//第一个条件
	    		StatCond sc= mainItem.getCond1();
	    		sumSql.append(getCondSql(sc,nbase));
	    		//第二个条件(多维统计同向可能有两层)
	    		StatCond sc2= mainItem.getCond2();
	    		if(sc2!=null){
	    			sumSql.append(" and ");
	    			sumSql.append(getCondSql(sc2,nbase));
	    		}
	    		/**mark 括号 two**/
	    		sumSql.append(" and (");
		    		for(int i=0;i<calItems.size();i++){
		    			StatCond cond1 = ((CrossTabItem)calItems.get(i)).getCond1();
		    			StatCond cond2 = ((CrossTabItem)calItems.get(i)).getCond1();
		    			
		    			sumSql.append(" (");
		    			sumSql.append(getCondSql(cond1,nbase));
		    			if(cond2!=null){
			    			sumSql.append(" and ");
			    			sumSql.append(getCondSql(cond2,nbase));
		    			}
		    			sumSql.append(" ) or ");
		    		}
		    		sumSql.delete(sumSql.length()-3, sumSql.length());
	    		
	    		/**mark 括号 two**/
	    		sumSql.append(" ) ");
    		
    		/**mark 括号 one**/
    		sumSql.append(" ) ");
    		
    		String sql = sumSql.toString();
    		sumSql.setLength(0);
    		for(int i=0;i<bases.length;i++){
              String base = bases[i];
              sumSql.append(sql.replace(nbase, base));
              sumSql.append(" union all ");
    		}
    		
    		sumSql.delete(sumSql.length()-10, sumSql.length());
    		
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		int value = 0;
    		try{
    			rs = dao.search(sumSql.toString());
    			while(rs.next()) {
                    value += rs.getInt(1);
                }
    		}catch(Exception e){
    			
    		}
    		return value;
    		
    }
    
    private String getCondSql(StatCond sc,String base){
    		FactorList parser = new FactorList(sc.getExpr(), sc.getFactor(), base, true, false, true, 1, userView.getUserName());
    		String sqlCond = "";
        try {
            boolean hasOrgInfo = false;

            ArrayList fields = parser.getFieldList();
            ArrayList set = new ArrayList();
            for(int i=0;i<fields.size();i++) {
                FieldItem fi = (FieldItem) fields.get(i);
                set.add(fi.getFieldsetid());
                if(fi.getFieldsetid().startsWith("B")) {
                    hasOrgInfo = true;
                }
            }

            if(hasOrgInfo){//单位部门的查询单独处理，因为要按b0110和e0122匹配，如果统一left join 处理会导致人数变多 guodd 2020-02-19
                StringBuffer sb = new StringBuffer();
                sb.append(base+"A01.a0100 in (select a0100 ");
                StatCondAnalyse cond = new StatCondAnalyse();
                String where = cond.getCondQueryString(sc.getExpr(),sc.getFactor(),base,false,userView.getUserName(),"",userView,"1",true,false);
                sb.append(where).append(") ");

                sqlCond = sb.toString();
            }else{
                sqlCond = parser.getSqlExpression();
                sqlCond = sqlCond.substring(sqlCond.indexOf(" WHERE ")+" WHERE ".length());
                /**
                 * 57815
                 * 为保证人数不丢失，统计使用 A01 left join XXX其他表。此时有个问题，就是如果关联的XXX没有此人的数据，left join就会把NUll数据插入到此表字段中
                 * 交叉统计分析是以值是不是Null来统计是是否符合条件的，本来null应该是不符合，但是条件分析器factorlist有个规则就是条件有<号时，认为是符合条件的，
                 * 这就导致统计时将 left join插入的假数据 统计进去了，导致统计值 变多。
                 * 此处添加 主键判断，如果关联表XXX 的主键为空，则证明这条数据其实是不存在的，不进行统计
                 * guodd 2020-02-13
                 */
                StringBuffer idSql = new StringBuffer();
                for(int i=0;i<fields.size();i++){
                    FieldItem fi = (FieldItem) fields.get(i);
                    String key;
                    if(fi.getFieldsetid().startsWith("A")){
                        key = base+fi.getFieldsetid()+".A0100";
                    }else if(fi.getFieldsetid().startsWith("K")){
                        key = fi.getFieldsetid()+".E01A1";
                    }else{
                        continue;
                    }
                    if(idSql.indexOf(key)!=-1) {
                        continue;
                    }
                    idSql.append(" and ").append(key).append(" is not null ");
                }

                sqlCond = sqlCond+idSql;
            }
        }
        catch (Exception e){
            
        }
    		return sqlCond;
    }
    
    /**
     * 某格涉及到维度数
     * @param col
     * @param row
     * @return
     */
    private int getStatItemCount(CrossTabItem col, CrossTabItem row) {
        int cnt = 0;
        if(col.getLevel2() != null && row.getLevel2() != null) {
            cnt = 4;
        } else if(col.getLevel2() != null || row.getLevel2() != null) {
            cnt = 3;
        } else {
            cnt = 2;
        }
        return cnt;
    }
        
    /**
     * 当前行是否满足格条件
     * @param col
     * @param row
     * @return
     */
    private boolean locateRow(CrossTabItem col, CrossTabItem row) throws SQLException {
    		/**
    		 * col 和 row 为当前校验单元格的列行统计项对象。
    		 * 多维统计 横向和纵向都可以定义两级复合统计。例如 横向 有两级统计条件，那么col里获取两级统计项分为为getCond1()、getCond2()
    		 * 
    		 */
    	
        if(!col.getCond1().getName().equals(statData.getString(getStatFldName(col.getLevel1())+"_"+getCondFldName(col.getCond1())))) {
            return false;
        }
        if(col.getCond2() != null && !col.getCond2().getName().equals(statData.getString(getStatFldName(col.getLevel2())+"_"+getCondFldName(col.getCond2())))) {
            return false;
        }
        if(!row.getCond1().getName().equals(statData.getString(getStatFldName(row.getLevel1())+"_"+getCondFldName(row.getCond1())))) {
            return false;
        }
        if(row.getCond2() != null && !row.getCond2().getName().equals(statData.getString(getStatFldName(row.getLevel2())+"_"+getCondFldName(row.getCond2())))) {
            return false;
        }
        return true;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public void setHtotal(String htotal) {
		this.htotal = htotal;
	}

	public void setVtotal(String vtotal) {
		this.vtotal = vtotal;
	}
    
}
