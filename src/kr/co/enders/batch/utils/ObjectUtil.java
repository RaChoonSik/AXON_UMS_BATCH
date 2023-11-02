package kr.co.enders.batch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectUtil {

	/**
	 * clear / close등 오브젝트를 초기화 하는 공통 클래스
	 * ------------------------------------------------------
	 * 지원 클래스
	 * Map, Collection, InputStream, OutputStream
	 * @param objects
	 */
	@SuppressWarnings("rawtypes")
	public static void initObjcet(Object ...objects) {
		for (Object obj : objects) {
			if (obj == null) continue;
			
			List<Class<? extends Object>> classList = new ArrayList<Class<? extends Object>>();
			getSuperclassList(obj.getClass(), classList);
			
// 디버그용 
//			for (Class<? extends Object> class1 : classList) {
//				System.out.println(class1);
//			}
			
			try {
				if (obj instanceof ServerSocket) {
					((ServerSocket) obj).close();
				} else if (obj instanceof Socket) {
					((Socket) obj).close();
				} else if (obj instanceof ResultSet) {
					((ResultSet) obj).close();
				} else if (obj instanceof Connection) {
					((Connection) obj).close();
				} else if (obj instanceof PreparedStatement) {
					((PreparedStatement) obj).close();
				} else if (classList.contains(AbstractCollection.class)) {
					((AbstractCollection) obj).clear();
				} else if (classList.contains(AbstractMap.class)) {
					((AbstractMap) obj).clear();
				} else if (classList.contains(InputStream.class)) {
					((InputStream) obj).close();
				} else if (classList.contains(OutputStream.class)) {
					((OutputStream) obj).close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 해당 오브젝트의 부모클래스 리스트를 추출하여 리스트로 반환 (재귀)
	 * @param objClass
	 * @param classList Class형으로 저장
	 */
	private static void getSuperclassList(Class<? extends Object> objClass, List<Class<? extends Object>> classList) {
		Class<? extends Object> superClass = objClass.getSuperclass();
		if (superClass != null) {
			classList.add(superClass);
			getSuperclassList(superClass, classList);
		}
	}
	
	/**
	 * 해당 오브젝트의 최상위 부모클래스를 추출하여 클래스로 반환
	 * @param objClass
	 * @return 최상위 부모클래스의 클래스오브젝트
	 */
	private static Class<? extends Object> getTopSuperclass(Class<? extends Object> objClass) {
		Class<? extends Object> superClass = objClass.getSuperclass();
		if (superClass != null) {
			superClass = getTopSuperclass(superClass);
		} else {
			return objClass;
		}
		return superClass;
	}
		
	public static void main(String[] args) throws IOException {
//		List aaa = new ArrayList();
		
//		Map aaa = new HashMap();
//		InputStream aaa = new FileInputStream("c:\\autoexec.bat");
//		OutputStream aaa = new FileOutputStream("c:\\autoexec.bat");
//		ServerSocket aaa = new ServerSocket(1234);
		Set aaa = new HashSet();
		initObjcet(aaa);	
	}
}
