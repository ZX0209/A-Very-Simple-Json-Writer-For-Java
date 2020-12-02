import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Json {
	public static String toJsonString(Object object) {
		if(null==object)return"null";
		Field[] fields = object.getClass().getDeclaredFields();
		if(fields==null||fields.length==0)return"{}";
		StringBuilder sb=new StringBuilder("{");
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Class<?> type = field.getType();
			sb.append("\"").append(type.getCanonicalName()).append(":").append(field.getName()).append("\":");
			try {
				Object value = field.get(object);
				if (String.class==type) {
					stringToJsonString((String)value,sb);
				}else if (Number.class.isAssignableFrom(type)||type==byte.class||type==short.class||type==char.class||
					type==int.class||type==float.class||type==double.class||type==long.class) {
					numberToJsonString((Number)value,sb);
				}else if (Boolean.class==type||boolean.class==type) {
					booleanToJsonString((Boolean)value,sb);
				}else if (Object[].class.isAssignableFrom(type)) {
					arrayToJsonString((Object[])value,sb);
				}else if (Iterable.class.isAssignableFrom(type)) {
					iterableToJsonString((Iterable<?>)value,sb);
				}else if (Map.class.isAssignableFrom(type)) {
					mapToJsonString((Map<?,?>)value,sb);
				}else {
					otherObjectToJsonString(value, sb);
				}
			} catch (IllegalArgumentException|IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.append("}").toString();
	}
	
	private static StringBuilder stringToJsonString(String string,StringBuilder sb) {
		if(null==string)return sb.append("null");
		return sb.append("\"").append(string.replace("\"","\\\"")).append("\"");
	}
	
	private static final NumberFormat numberFormat=NumberFormat.getInstance();
	static{numberFormat.setMaximumFractionDigits(20);}
	
	private static StringBuilder numberToJsonString(Number number,StringBuilder sb) {
		if(null==number)return sb.append("null");
		return number instanceof Integer?sb.append(number.intValue()):
			   number instanceof Double?sb.append(numberFormat.format(number)):
			   number instanceof Float?sb.append(numberFormat.format(number)):
			   number instanceof Long?sb.append(number.longValue()):
			   sb.append(number.intValue());
	}
	
	public static StringBuilder booleanToJsonString(Boolean bool,StringBuilder sb) {
		if(null==bool||!bool)return sb.append("false");
		return sb.append("true");
	}
	
	private static StringBuilder arrayToJsonString(Object[] array,StringBuilder sb) {
		if(null==array)return sb.append("[]");
		int length=Array.getLength(array);
		if(length==0)return sb.append("[]");
		sb.append("[");
		if(array instanceof String[]){
			for (int i = 0; i < length; i++) {
				stringToJsonString((String)Array.get(array, i),sb);
				sb.append(",");
			}
		}else if (array instanceof Number[]) {
			for (int i = 0; i < length; i++) {
				numberToJsonString((Number)Array.get(array, i),sb);
				sb.append(",");
			}
		}else {
			for (int i = 0; i < length; i++) {
				otherObjectToJsonString(Array.get(array, i),sb);
				sb.append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.append("]");
	}
	
	private static StringBuilder iterableToJsonString(Iterable<?> iterable,StringBuilder sb) {
		if(null==iterable)return sb.append("[]");
		Iterator<?> iterator = iterable.iterator();
		if(!iterator.hasNext())return sb.append("[]");
		sb.append("[");
		while (iterator.hasNext()) {
			 Object next = iterator.next();
			 otherObjectToJsonString(next,sb);
			 sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.append("]");
	}
	
	private static StringBuilder mapToJsonString(Map<?,?> map,StringBuilder sb) {
		if(null==map||map.isEmpty())return sb.append("{}");
		sb.append("{");
		Set<? extends Map.Entry<?,?>> entrySet = map.entrySet();
		for (Entry<?, ?> entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			sb.append("\"").append(null==key?"java.lang.Object":key.getClass().getCanonicalName()).append(":").append(value==null?"java.lang.Object":value.getClass().getCanonicalName()).append("/").append(null==key?"null":key.toString().replace("\"","\\\"")).append("\":");
			otherObjectToJsonString(entry.getValue(), sb);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.append("}");
	}
	
	private static StringBuilder otherObjectToJsonString(Object object,StringBuilder sb) {
		if(null==object)return sb.append("{}");
		if(object instanceof String)return stringToJsonString((String)object,sb);
		if(object instanceof Number)return numberToJsonString((Number)object,sb);
		if(object instanceof Object[])return arrayToJsonString((Object[])object,sb);
		if(object instanceof Iterable)return iterableToJsonString((Iterable<?>)object,sb);
		if(object instanceof Map)return mapToJsonString((Map<?,?>)object,sb);
		Field[] fields = object.getClass().getDeclaredFields();
		if(null==fields||fields.length==0)return sb.append("{}");
		sb.append("{");
		for (Field field : fields) {
			field.setAccessible(true);
			Class<?> type = field.getType();
			sb.append("\"").append(type.getCanonicalName()).append(":").append(field.getName()).append("\":");
			try {
				Object value = field.get(object);
				if (String.class==type) {
					stringToJsonString((String)value,sb);
				}else if (Number.class.isAssignableFrom(type)||type==byte.class||type==short.class||type==char.class||
					type==int.class||type==float.class||type==double.class||type==long.class) {
					numberToJsonString((Number)value,sb);
				}else if (Boolean.class==type||boolean.class==type) {
					booleanToJsonString((Boolean)value,sb);
				}else if (Object[].class.isAssignableFrom(type)) {
					arrayToJsonString((Object[])value,sb);
				}else if (Iterable.class.isAssignableFrom(type)) {
					iterableToJsonString((Iterable<?>)value,sb);
				}else if (Map.class.isAssignableFrom(type)) {
					mapToJsonString((Map<?,?>)value,sb);
				}else {
					otherObjectToJsonString(value, sb);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.append("}");
	}
	
	public static void main(String[] args) {
		System.out.println(toJsonString(new ComplexObject()));
	}
}


class ComplexObject{
	private Simple simple;
	private Map<?,?> map;
	private List<?> list;
	private Object[] objects;
	private String[] strings;
	private Integer[] integers;
	private Double[] doubles;
	private String string;
	private Integer integer;
	private Double doub;
	private Boolean bool;
	private int int0;
	private double double0;
	private boolean boolean0;
	public ComplexObject(){
		simple=new Simple();
		HashMap<Object,Object> hashMap = new HashMap<>();
		hashMap.put("hello", "world");
		hashMap.put(123, "456");
		hashMap.put(12.34, 12.34);
		hashMap.put("simple", new Simple());
		hashMap.put("hehe\"haha\"hehe", "world");
		map=hashMap;
		list=Stream.of("xx",123,null,456.78,"yyy",new Simple(),"abc").collect(Collectors.toList());
		objects=new Object[]{456.78,"yyy",new Simple(),"abc",101.22,"xxx"};
		strings=new String[]{"hello","abc","world",null,"bye"};
		integers=new Integer[]{100,22,0,null,36};
		doubles=new Double[]{100.0,22E-5,0.0,null,99.00,0.0002};
		string="hello";
		integer=null;
		doub=0.0002;
		bool=true;
		int0=10;
		double0=.111;
		boolean0=true;
	}
}

class Simple{
	private String name;
	private Integer num;
	private Double doub;
	private Boolean bool;
	public Simple(){
		name="Tom";
		num=123;
		doub=null;
		bool=true;
	}
}
