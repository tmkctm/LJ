package LJava;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MapComparatorByValue<T> implements Comparator<T> {	
	Map<T,Integer> sourceMap;		
	public MapComparatorByValue(HashMap<T,Integer> m) {
		sourceMap=m;
	}		
	@Override
	public int compare(T a, T b) {
		if (sourceMap.get(a)>sourceMap.get(b)) return -1;
		else if (sourceMap.get(a)<sourceMap.get(b)) return 1;
		int aHash=a.hashCode();
		int bHash=b.hashCode();
		if (aHash>bHash) return -1;
		else if (aHash<bHash) return 1;
		return 0;
	}
}