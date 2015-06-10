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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * Restaurant Application that accepts command line input of Restaurant and Menu Listing file
 * and the Menu order Details in the format filename[space]item1[space]item2... 
 * 
 */

public class RestaurantApp {

	static List<String> menu_order_items = new ArrayList<>();
	static HashMap<Integer, List<MenuEntry<String, Float>>> restructureMap = new HashMap<>();
	static ConcurrentHashMap<Integer, Float> RestaurantMenuTable = new ConcurrentHashMap<>();

	// In Windows Run: >java -cp e:\restaurantApp RestaurantApp sample_data.csv burger tofu_log

	public static void main(String[] args) throws IOException {

		try {
			String csvFile = args[0];

			for (int i = 1; i < args.length; i++)
				menu_order_items.add(args[i]);

			InputStream is = new FileInputStream(new File(csvFile));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			Predicate<String> p1 = (p) -> (menu_order_items.stream().anyMatch(keyword -> p.matches(".*\\b" + keyword + "\\b.*")));

			br.lines().parallel().filter(p1).forEach(firstPass);
			Set<Entry<Integer, List<MenuEntry<String, Float>>>> menuEntries = restructureMap.entrySet();
			menuEntries.stream().parallel().forEach(secondPass);

			br.close();

			// RestaurantMenuTable contains all restaurants that can serve the
			// order in various of their menu combinations for which
			// their corresponding prices are saved thereof

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

	public static int[] removeElement(int[] a, int del) {
		System.arraycopy(a, del + 1, a, del, a.length - 1 - del);
		return a;
	}

	/*
	 * Here we are restructuring the data from csv into a map in the following
	 * format upon removing those restaurants whose menu does not contain user
	 * request Format: map_entry: [rest_id, <Menu1,Value1>, <Menu2,Value2>, ...]
	 * here, Menu is each `line` from csv file and Value is the corresponding
	 * Price Each entry corresponds to a unique restaurant accordingly
	 */

	public static Consumer<? super String> firstPass = (line) -> {

		List<String> lineItems = new ArrayList<>(Arrays.asList(line.split(",")));
		List<String> menuList = lineItems.subList(2, lineItems.size());
		String menuString = String.join(",", menuList);

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
	};

	/*
	 * Here, the restructured data is consumed one entry at a time where it is
	 * scanned to retrieve the MenuItems from the Menus available in each entry.
	 * We may get here a single menu that may cater to all requested items or a
	 * group(distributed) menus that collectively may serve the user requests or
	 * perhaps no single restaurant may contain such menu collection that can
	 * serve the purpose in which case the result is empty
	 */

	public static Consumer<Entry<Integer, List<MenuEntry<String, Float>>>> secondPass = (element) -> {

		ArrayList<Float> pricePerSelectedMenusCombination = new ArrayList<>();
		List<MenuEntry<String, Float>> menuList = element.getValue();

		// This corresponds to the storage of menu distribution that
		// collectively serve the user
		CopyOnWriteArrayList<int[]> indexList = new CopyOnWriteArrayList<>();
		List<List<Integer>> newIndexList = new ArrayList<>();

		for (String keyword : menu_order_items) {
			int[] indices = IntStream.range(0, menuList.size())
					.filter(i -> menuList.get(i).getKey().contains(keyword))
					.toArray();
			if (indices.length > 0)
				indexList.add(indices);
		}

		int menuDistribution = indexList.size();
		int singleMenuCatersAllUserItems;

		if (menuDistribution == menu_order_items.size()) {
			{
				// System.out.println("menu: Single and distributed Menus are added to MenuTable");
				HashMap<Integer, Integer> menuDistro = new HashMap<>();

				// here we are reducing the menu distribution by separating
				// those menus that can independently serve
				// all the items requested

				int[] temp = indexList.get(0);

				// Identification of such Menus

				for (int y = 1; y < indexList.size(); y++) {
					int[] intermediateArray = indexList.get(y);

					for (int x = 0; x < temp.length; x++) {
						int count = 0;

						for (int z = 0; z < intermediateArray.length; z++) {

							if (temp[x] == intermediateArray[z])
								menuDistro.put(temp[x], ++count);
						}
					}
				}

				// Separation of such menus

				Set<Integer> setOfIndividualMenus = new HashSet<Integer>();
				if (menuDistro.containsValue(menuDistribution - 1)) {
					setOfIndividualMenus = getKeysByValue(menuDistro,
							menuDistribution - 1);
				}

				// Reduction of menus thereby if single menu found

				if (setOfIndividualMenus.size() > 0) {

					ListIterator<int[]> listItr = indexList.listIterator();

					while (listItr.hasNext()) {
						int[] interList = listItr.next();
						for (int a = 0; a < interList.length; a++) {
							int itrValue = interList[a];

							Iterator<Integer> itr = setOfIndividualMenus.iterator();

							while (itr.hasNext()) {
								singleMenuCatersAllUserItems = itr.next();
								if (itrValue == singleMenuCatersAllUserItems) {
									interList = removeElement(interList, itrValue);

									if (!pricePerSelectedMenusCombination
											.contains(element
													.getValue()
													.get(singleMenuCatersAllUserItems)
													.getValue()))
										pricePerSelectedMenusCombination
												.add(element
														.getValue()
														.get(singleMenuCatersAllUserItems)
														.getValue());
									break;
								}
							}

						}

						ArrayList<Integer> intArray = new ArrayList<>();
						for (int i = 0; i < interList.length; i++) {
							intArray.add(interList[i]);
						}

						newIndexList.add(intArray);
					}

					if (RestaurantMenuTable.contains(element.getKey()))
						RestaurantMenuTable.put(element.getKey(), Math.min(Collections.min(pricePerSelectedMenusCombination), RestaurantMenuTable.get(element.getKey())));

					else
						RestaurantMenuTable.put(element.getKey(), Collections.min(pricePerSelectedMenusCombination));

				}

				else {
					newIndexList = convert(indexList);
				}

				List<List<Integer>> menuDistroSets = newIndexList;

				List<List<Integer>> cartesianProducts = cartesianProduct(menuDistroSets);

				// Removing duplicate menu id entries while calculating the
				// collective price in serving the order distributed across menus

				List<List<Integer>> noDuplicatesInProducts = cartesianProducts
						.stream()
						.map(product -> product.stream().distinct()
								.collect(Collectors.toList()))
						.collect(Collectors.toList());

				// Establishing the total price per order for restaurant

				for (List<Integer> index : noDuplicatesInProducts) {
					List<Integer> indexDetailsForMenu = index;
					float totalPrice = 0;
					for (int indice : indexDetailsForMenu) {
						totalPrice = totalPrice	+ element.getValue().get(indice).getValue();
					}
					if (!pricePerSelectedMenusCombination.contains(totalPrice))
						pricePerSelectedMenusCombination.add(totalPrice);

					// System.out.println(pricePerSelectedMenusCombination);
				}
				if (RestaurantMenuTable.contains(element.getKey())) {
					RestaurantMenuTable.put(element.getKey(), Math.min(Collections.min(pricePerSelectedMenusCombination), RestaurantMenuTable.get(element.getKey())));
				}

				else
					RestaurantMenuTable.put(element.getKey(), Collections.min(pricePerSelectedMenusCombination));
			}
		}
	};

	public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
		List<List<T>> resultLists = new ArrayList<List<T>>();
		if (lists.size() == 0) {
			resultLists.add(new ArrayList<T>());
			return resultLists;
		} else {
			List<T> firstList = lists.get(0);
			List<List<T>> remainingLists = cartesianProduct(lists.subList(1,
					lists.size()));
			for (T condition : firstList) {
				for (List<T> remainingList : remainingLists) {
					ArrayList<T> resultList = new ArrayList<T>();
					resultList.add(condition);
					resultList.addAll(remainingList);
					resultLists.add(resultList);
				}
			}
		}
		return resultLists;
	}

	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream()
				.filter(entry -> Objects.equals(entry.getValue(), value))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	public static List<List<Integer>> convert(List<int[]> list) {
		return list
				.stream()
				.map(arr -> IntStream.of(arr).mapToObj(Integer::valueOf)
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}
}

final class MenuEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public MenuEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}