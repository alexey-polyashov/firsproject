package gb_less_3_7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Tester {

    private class MethodContainer{
        int order;
        Method method;

        public MethodContainer(int order, Method method) {
            this.order = order;
            this.method = method;
        }

    }

    Comparator<MethodContainer> priorityComparator = new Comparator<MethodContainer>() {
        @Override
        public int compare(MethodContainer o1, MethodContainer o2) {
            return o1.order - o2.order;
        }
    };

    private Queue<MethodContainer> methodList = new PriorityQueue<>(priorityComparator);
    private boolean isBeforeMethod;
    private boolean isAfterMethod;

    public void start(String className) throws RuntimeException {

        try {
            Class<?> clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not '" + className + "' found");
        }

    }

    public void start(Class<?> clazz) throws RuntimeException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Method[] methods= clazz.getDeclaredMethods();

        for (Method m:methods) {
            if(m.isAnnotationPresent(BeforeSuite.class)){
                if(isBeforeMethod==true){
                    throw new RuntimeException("gb_less_3_7.BeforeSuite must be only one");
                }
                isBeforeMethod = true;
                methodList.add(new MethodContainer(-1,m));
            }
            if(m.isAnnotationPresent(AfterSuite.class)){
                if(isAfterMethod==true){
                    throw new RuntimeException("gb_less_3_7.AfterSuite must be only one");
                }
                isAfterMethod = true;
                methodList.add(new MethodContainer(11,m));
            }
            if(m.isAnnotationPresent(Test.class)){
                Test ann =  m.getAnnotation(Test.class);
                int pr = ann.priority();
                methodList.add(new MethodContainer(pr,m));
            }
        }

        Object testableClass = clazz.getDeclaredConstructor().newInstance();

        while(true){
            MethodContainer mc = methodList.poll();
            if(mc==null)break;
            mc.method.invoke(testableClass);
        }

    }

}
