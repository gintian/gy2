package com.hjsj.hrms.businessobject.gz.sort;

import java.util.ArrayList;
/**
 *<p>Title:SortBo</p> 
 *<p>Description:排序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2009-3-24:上午11:03:54</p> 
 *@author lilinbing
 *@version 4.0
 */
public class SortBo {
	/**
	 * 插入排序(从大到小)
	 * @param data 整数数组
	 * @return
	 */
	public static int[] descSort(int[] data){
		int temp;
		for(int i=0;i<data.length;i++){
			for(int j=i;(j>0)&&(data[j]>data[j-1]);j--){
				temp = data[j];
				data[j]=data[j-1];
				data[j-1]=temp;
			}
		}
		return data;
	}
	/**
	 * 插入排序(从大到小)
	 * @param data 浮点型数组
	 * @return
	 */
	public static double[] descSort(double[] data){
		double temp;
		for(int i=0;i<data.length;i++){
			for(int j=i;(j>0)&&(data[j]>data[j-1]);j--){
				temp = data[j];
				data[j]=data[j-1];
				data[j-1]=temp;
			}
		}
		return data;
	}
	/**
	 * 插入排序(从大到小)
	 * @param data 整数ArrayList
	 * @return
	 */
	public static ArrayList descSort(ArrayList data){
		String temp="";
		for(int i=0;i<data.size();i++){
			for(int j=i;(j>0)&&(Integer.parseInt((String)data.get(j))>Integer.parseInt((String)data.get(j-1)));j--){
				temp = (String)data.get(j);
				data.set(j, data.get(j-1));
				data.set(j-1,temp);
			}
		}
		return data;
	}
	/**
	 * 插入排序(从大到小)
	 * @param data 浮点型ArrayList
	 * @return
	 */
	public static ArrayList descDoubleSort(ArrayList data){
		String temp="";
		for(int i=0;i<data.size();i++){
			for(int j=i;(j>0)&&(Double.parseDouble((String)data.get(j))>Double.parseDouble((String)data.get(j-1)));j--){
				temp = (String)data.get(j);
				data.set(j, data.get(j-1));
				data.set(j-1,temp);
			}
		}
		return data;
	}
	/**
	 * 插入排序(从小到大)
	 * @param data 整数数组
	 * @return
	 */
	public static int[] ascSort(int[] data){
		int temp;
		for(int i=0;i<data.length;i++){
			for(int j=i;(j>0)&&(data[j]<data[j-1]);j--){
				temp = data[j];
				data[j]=data[j-1];
				data[j-1]=temp;
			}
		}
		return data;
	}
	/**
	 * 插入排序(从小到大)
	 * @param data 整数ArrayList
	 * @return
	 */
	public static ArrayList ascSort(ArrayList data){
		String temp="";
		for(int i=0;i<data.size();i++){
			for(int j=i;(j>0)&&(Integer.parseInt((String)data.get(j))<Integer.parseInt((String)data.get(j-1)));j--){
				temp = (String)data.get(j);
				data.set(j, data.get(j-1));
				data.set(j-1,temp);
			}
		}
		return data;
	}
	/**
	 * 插入排序(从小到大)
	 * @param data 浮点型数组
	 * @return
	 */
	public static double[] ascSort(double[] data){
		double temp;
		for(int i=0;i<data.length;i++){
			for(int j=i;(j>0)&&(data[j]<data[j-1]);j--){
				temp = data[j];
				data[j]=data[j-1];
				data[j-1]=temp;
			}
		}
		return data;
	}
	/**
	 * 插入排序(从小到大)
	 * @param data 浮点型ArrayList
	 * @return
	 */
	public static ArrayList ascDoubleSort(ArrayList data){
		String temp="";
		for(int i=0;i<data.size();i++){
			for(int j=i;(j>0)&&(Double.parseDouble((String)data.get(j))<Double.parseDouble((String)data.get(j-1)));j--){
				temp = (String)data.get(j);
				data.set(j, data.get(j-1));
				data.set(j-1,temp);
			}
		}
		return data;
	}
}
