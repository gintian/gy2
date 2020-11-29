/**
 * 
 */
package com.hjsj.hrms.client.card;

import java.util.ArrayList;
import java.util.Observable;

/**
 * @author chenmengqing
 *
 */
public class CardModel extends Observable {
	private ArrayList list=new ArrayList();
	
	public void add(String str)
	{
		this.list.add(str);
		setChanged();
		notifyObservers(str);
	}

}
