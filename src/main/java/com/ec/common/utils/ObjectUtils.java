package com.ec.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

/**
 * 鐎电钖勫銉ュ徔缁拷
 *
 * @author liangzhongqiu
 * @date 2016楠烇拷8閺堬拷25閺冿拷
 * @time 娑撳﹤宕�9:42:11
 */
public abstract class ObjectUtils extends org.springframework.beans.BeanUtils{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);


	public static void copyNotNull2Object(Object source, Object target, String[] ignoreProperties)
			throws BeansException {
		copyNotNull2Object(source, target, null, ignoreProperties);
	}

	public static void copyNotNull2Object(Object source, Object target, Class<?> editable) throws BeansException {
		copyNotNull2Object(source, target, editable, null);
	}

	public static void copyNotNull2Object(Object source, Object target) throws BeansException {
		copyNotNull2Object(source, target, null, null);
	}

	/**
	 * 婢跺秴鍩楅棃鐐碘敄鐏炵偞锟窖冨煂閻╊喗鐖ｇ�电懓鍎�
	 * @param source
	 * @param target
	 * @param editable
	 * @param ignoreProperties
	 * @throws BeansException
	 */
	private static void copyNotNull2Object(Object source, Object target, Class<?> editable,
			String[] ignoreProperties) throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName()
						+ "] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null
					&& (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null || readMethod.getReturnType().getName().equals("java.lang.String")) {// 鏉╂瑩鍣烽崚銈嗘焽娴犮儰绗卾alue閺勵垰鎯佹稉铏光敄閿涘苯缍嬮悞鎯扮箹闁插奔绡冮懗鍊熺箻鐞涘奔绔存禍娑氬濞堝﹨顩﹀Ч鍌滄畱婢跺嫮鎮�
																												// 娓氬顩х紒鎴濈暰閺冭埖鐗稿蹇氭祮閹广垻鐡戠粵澶涚礉婵″倹鐏夐弰鐤璽ring缁鐎烽敍灞藉灟娑撳秹娓剁憰渚�鐛欑拠浣规Ц閸氾缚璐熺粚锟�
							boolean isEmpty = false;
							if (value instanceof Set) {
								Set s = (Set) value;
								if (s == null || s.isEmpty()) {
									isEmpty = true;
								}
							} else if (value instanceof Map) {
								Map m = (Map) value;
								if (m == null || m.isEmpty()) {
									isEmpty = true;
								}
							} else if (value instanceof List) {
								List l = (List) value;
								if (l == null || l.size() < 1) {
									isEmpty = true;
								}
							} else if (value instanceof Collection) {
								Collection c = (Collection) value;
								if (c == null || c.size() < 1) {
									isEmpty = true;
								}
							}
							if (!isEmpty) {
								Method writeMethod = targetPd.getWriteMethod();
								if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
									writeMethod.setAccessible(true);
								}
								writeMethod.invoke(target, value);
							}
						}
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}


	
	/**
	 * 閸掋倖鏌囩�电钖勯弰顖氭儊閺堝锟斤拷
	 * @param object
	 * @return
	 */
	public static boolean hasValue(Object object){
		if(object != null){
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for(;clazz != Object.class ; clazz = clazz.getSuperclass()){
				for(Field f: fields){
					f.setAccessible(true);
					try {
						if(f.get(object) != null){
							return true;
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {}
				}
			}
		}
		return false;
	}
	
    /**
     * 鐏忓攬ava鐎圭偘缍媌ean鏉烆剚宕查幋鎭廰shmap, java閸欏秴鐨犵亸鍡楃杽娓氬瀵查惃鍕杽娴ｆ彽ean鏉烆剚宕查幋鎭廰shmap<閹存劕鎲抽崣姗�鍣洪崥宥囆�,鐎圭偘绶ラ崠鏍ф倵閻ㄥ嫭鍨氶崨妯哄綁闁插繒娈戦崐锟�>
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HashMap<String, Object> objToHash(Object obj) throws IllegalArgumentException,IllegalAccessException {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        Class clazz = obj.getClass();
        List<Class> clazzs = new ArrayList<Class>();

        do {
            clazzs.add(clazz);
            clazz = clazz.getSuperclass();
        } while (!clazz.equals(Object.class));

        for (Class iClazz : clazzs) {
            Field[] fields = iClazz.getDeclaredFields();
            for (Field field : fields) {
                Object objVal = null;
                field.setAccessible(true);
                objVal = field.get(obj);
                hashMap.put(field.getName(), objVal);
            }
        }

        return hashMap;
    }
    
	/**
	 * 娴犲酣娉﹂崥鍫滆厬閼惧嘲褰囬弻鎰嚠鐠烇拷
	 * @param clazz
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>T getObjectFromList(Class<T> clazz,List<Object> objects){
		if(objects != null && clazz != null){
			for(Object object : objects){
				if((clazz.getName()).equals(object.getClass().getName())){
					return (T)object;
				}
			}
		}
		return null;
	}
	
    


	@Deprecated
	public static <T,M> void setPropertyValue(T source, M target){
		Class<? extends Object> sClazz  =  source.getClass();
		while (sClazz.getSuperclass() != null) {
			Field[] sFileds = sClazz.getDeclaredFields();
			for (Field sf : sFileds) {
				Class<? extends Object> tClazz = target.getClass();
				String sname = sf.getName();
				while (tClazz.getSuperclass() != null) {
					Field[] tFileds = tClazz.getDeclaredFields();
					for (Field tf : tFileds) {
						String tname = tf.getName();
						if (sname.equals(tname)) {
							String getMethodName = "get" + sname.substring(0, 1).toUpperCase() + sname.substring(1);
							String setMethodName = "set" + sname.substring(0, 1).toUpperCase() + sname.substring(1);
							try {
								Method getMethod = sClazz.getMethod(getMethodName);
								Object object = getMethod.invoke(source);
								if (object != null) {
									Method setMethod = tClazz.getMethod(setMethodName, new Class[]{tf.getType()});
									setMethod.invoke(target, new Object[]{object});
								}
							} catch (Exception e) {
								LOGGER.warn(e.getMessage(),e);
							}
						}
					}
					tClazz = tClazz.getSuperclass();
				}
			}
			sClazz = sClazz.getSuperclass();
		}
	}

}
