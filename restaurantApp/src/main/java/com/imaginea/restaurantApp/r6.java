package com.imaginea.restaurantApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class r6 {

	private static List<String> menu_order_items = new ArrayList<>();
	private static ConcurrentHashMap<Integer, Float> RestaurantMenuTable = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, List<MenuEntry<String, Float>>> restructureMap = new ConcurrentHashMap<>();
	
	public static void main(String[] args) throws IOException {

		try {
			String csvFile = args[0];

			for (int i = 1; i < args.length; i++)
				menu_order_items.add(args[i]);

			InputStream is = new FileInputStream(new File(csvFile));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String str;
			while ((str = br.readLine()) != null) {
				final String line = str;
				new Thread() {
					public void run() {
						for (int i = 0; i < menu_order_items.size(); i++) {
							if (line.matches(".*\\b" + menu_order_items.get(i) + "\\b.*")){
								getRestaurants(line);
								break;
							}
						}
					}
				}.run();
			}
			
			br.close();
			
			Set<Entry<Integer, List<MenuEntry<String, Float>>>> menuEntries = restructureMap.entrySet();
			for (final Entry<Integer, List<MenuEntry<String, Float>>> entry : menuEntries) {
				new Thread() {
					public void run() {
						getAcceptableMenuCombinationsAndPrices(entry);
					}
				}.run();
			}

			try {

				Entry<Integer, Float> restaurant = null;
				for (Entry<Integer, Float> entry : RestaurantMenuTable.entrySet()) {
					if (restaurant == null || restaurant.getValue() > entry.getValue()) {
						restaurant = entry;
					}
				}

				System.out.println(restaurant.getKey() + " " + restaurant.getValue());

			} catch (Exception e) {
				System.out.println("Nil");
			}
		} catch (Exception e) {
			System.out.println("Enter proper input arguments");
		}
	}

	protected static void getRestaurants(String line) {

		//String[] p = line.split(",");
		
		List<String> lineItems = new ArrayList<>(Arrays.asList(line.split(",")));
		List<String> menuList = lineItems.subList(2, lineItems.size());
		String menuString = join(menuList, ",");

		try {
			List<MenuEntry<String, Float>> itemsList = restructureMap.get(Integer.parseInt(lineItems.get(0)));

			if (itemsList == null) {
				itemsList = new ArrayList<>();
				itemsList.add(new MenuEntry<String, Float>(menuString, Float.parseFloat(lineItems.get(1))));
				restructureMap.put(Integer.parseInt(lineItems.get(0)), itemsList);
			}

			else {
				if (!itemsList.contains(new MenuEntry<String, Float>(menuString, Float.parseFloat(lineItems.get(1)))))
					itemsList.add(new MenuEntry<String, Float>(menuString, Float.parseFloat(lineItems.get(1))));
			}
		}

		catch (Exception e) {

		}
	}

	protected static void getAcceptableMenuCombinationsAndPrices(Entry<Integer, List<MenuEntry<String, Float>>> entry) {

		ArrayList<Float> pricePerSelectedMenusCombination = new ArrayList<>();
		List<MenuEntry<String, Float>> menuList = entry.getValue();

		ArrayList<Set<Integer>> indexList = new ArrayList<>();
		List<Set<Integer>> newIndexList = new ArrayList<>();

		for (String keyword : menu_order_items) {
			LinkedHashSet<Integer> indices = new LinkedHashSet<>();
			for (int i = 0; i < menuList.size(); i++)
				if (menuList.get(i).getKey().matches(".*\\b" + keyword + "\\b.*"))
					indices.add(i);
			if (indices.size() > 0)
				indexList.add(indices);
		}

		//System.out.println(indexList);

		int menuDistribution = indexList.size();
		int singleMenuCatersAllUserItems;

		if (menuDistribution == menu_order_items.size()) {

			HashMap<Integer, Integer> menuDistro = new HashMap<>();
			Set<Integer> indicesList = indexList.get(0);			
			Iterator<Integer> indicesItr = indicesList.iterator();

			while(indicesItr.hasNext()) {
				int iValue = indicesItr.next();
				int count = 0;
				for (int j = 1; j < indexList.size(); j++) {
					Set<Integer> intermediateList = indexList.get(j);
					if (intermediateList.contains(iValue))
						menuDistro.put(iValue, ++count);
				}
			}
			
			LinkedHashSet<Integer> setOfIndividualMenus = new LinkedHashSet<Integer>();
			if (menuDistro.containsValue(menuDistribution - 1)) {
				setOfIndividualMenus = getMapKeysByValue(menuDistro, menuDistribution - 1);
			}
			//System.out.println(s);
			if (setOfIndividualMenus.size() > 0) {
				
					ListIterator<Set<Integer>> listItr = indexList.listIterator();

					while (listItr.hasNext()) {
						Set<Integer> interList = listItr.next();
						Iterator<Integer> interListItr = interList.iterator();
						while (interListItr.hasNext()) {
							int itrValue = interListItr.next().intValue();
							
							Iterator<Integer> itr = setOfIndividualMenus.iterator();
							while (itr.hasNext()) {
								singleMenuCatersAllUserItems = itr.next();
							if (itrValue == singleMenuCatersAllUserItems){
								interListItr.remove();
								break;
							}
							if(!pricePerSelectedMenusCombination.contains(entry.getValue().get(singleMenuCatersAllUserItems).getValue()))
								pricePerSelectedMenusCombination.add(entry.getValue().get(singleMenuCatersAllUserItems).getValue());
							
							//System.out.println(pricePerSelectedMenusCombination);						
							}
						}
						newIndexList.add(interList);
					}					
				
				if(RestaurantMenuTable.contains(entry.getKey()))
					RestaurantMenuTable.put(entry.getKey(), Math.min(Collections.min(pricePerSelectedMenusCombination), RestaurantMenuTable.get(entry.getKey())));
				
				else
					RestaurantMenuTable.put(entry.getKey(), Collections.min(pricePerSelectedMenusCombination));				
			}			

			else 
				newIndexList = indexList;
			
			//System.out.println(newIndexList);
			
			Set<Set<Integer>> cartesianProducts = cartesianProduct(new LinkedHashSet<>(newIndexList));			
			for (Set<Integer> index : cartesianProducts) {
				Set<Integer> indexDetailsForMenu = index;
				float totalPrice = 0;
				for (int indice : indexDetailsForMenu) {
					totalPrice = totalPrice	+ entry.getValue().get(indice).getValue();
				}
				if(!pricePerSelectedMenusCombination.contains(totalPrice))
					pricePerSelectedMenusCombination.add(totalPrice);
				
				//System.out.println(pricePerSelectedMenusCombination);
			}
			if(RestaurantMenuTable.contains(entry.getKey())){
				RestaurantMenuTable.put(entry.getKey(), Math.min(Collections.min(pricePerSelectedMenusCombination), RestaurantMenuTable.get(entry.getKey())));
			}
			
			else
				RestaurantMenuTable.put(entry.getKey(), Collections.min(pricePerSelectedMenusCombination));
			}
		//System.out.println(pricePerSelectedMenusCombination.size());
	}

	public static Set<Set<Integer>> cartesianProduct(Set<Set<Integer>> menuDistroSets) {
		Set<Set<Integer>> resultLists = new LinkedHashSet<Set<Integer>>();
		if (menuDistroSets.size() == 0) {
			resultLists.add(new LinkedHashSet<Integer>());
			return resultLists;
		} else {
			Iterator<Set<Integer>> setItr = menuDistroSets.iterator();
			Set<Integer> firstList = setItr.next();
			Set<Set<Integer>> subSets = new LinkedHashSet<>();
			while(setItr.hasNext()){
				subSets.add(setItr.next());
			}
			Set<Set<Integer>> remainingLists = cartesianProduct(subSets);
			
			for (Integer condition : firstList) {
				for (Set<Integer> remainingList : remainingLists) {
					Set<Integer> resultList = new LinkedHashSet<Integer>();
						resultList.add((Integer) condition);
						resultList.addAll(remainingList);
						resultLists.add(resultList);
				}
			}
		}
		return resultLists;
	}

	public static <T, E> LinkedHashSet<T> getMapKeysByValue(Map<T, E> map, E value) {

		LinkedHashSet<T> set = new LinkedHashSet<>();
		Iterator<Entry<T, E>> esItr = map.entrySet().iterator();

		while (esItr.hasNext()) {
			Entry<T, E> entry = esItr.next();
			if (entry.getValue().equals(value))
				set.add(entry.getKey());
		}
		return set;
	}
	
	public static String join(List<String> list, String conjunction)
	{
	   StringBuilder sb = new StringBuilder();
	   boolean first = true;
	   for (String item : list)
	   {
	      if (first)
	         first = false;
	      else
	         sb.append(conjunction);
	      sb.append(item);
	   }
	   return sb.toString();
	}

}