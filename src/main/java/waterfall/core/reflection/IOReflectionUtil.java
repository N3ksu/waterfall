package waterfall.core.reflection;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public final class IOReflectionUtil {
    public static Set<Class<?>> findAnnotatedClassesInPackage(String packageName, Class<? extends Annotation> annotationClass)
            throws Exception {
        String path = packageName.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = loader.getResources(path);

        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.toURI());

            if (directory.exists() && directory.isDirectory())
                findAndRetrieveAnnotatedClassesInDirectory(directory, packageName, classes, annotationClass);
        }

        return classes;
    }

    private static void findAndRetrieveAnnotatedClassesInDirectory(File directory, String packageName, Set<Class<?>> classes, Class<? extends Annotation> annotationClass)
            throws Exception {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                findAndRetrieveAnnotatedClassesInDirectory(file, packageName  + "." + file.getName(), classes, annotationClass);

            else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                if(clazz.isAnnotationPresent(annotationClass))
                    classes.add(clazz);
            }
        }
    }

    public static Set<Method> findAnnotatedMethodsInPackage(String packageName, Class<? extends Annotation> methodsAnnotationClass, Class<? extends Annotation> classesAnnotationClass)
            throws Exception {
        Set<Class<?>> classes = findAnnotatedClassesInPackage(packageName, classesAnnotationClass);
        Set<Method> methods = new HashSet<>();

        for (Class<?> c : classes)
            methods.addAll(ReflectionUtil.findAnnotatedMethods(c, methodsAnnotationClass));

        return methods;
    }

    public static <T extends Annotation> Set<SimpleEntry<Method, T>> findAnnotatedMethodEntriesInPackage(String packageName, Class<T> methodsAnnotationClass, Class<? extends Annotation> classesAnnotationClass)
            throws Exception {
        Set<Class<?>> classes = findAnnotatedClassesInPackage(packageName, classesAnnotationClass);
        Set<SimpleEntry<Method, T>> entries = new HashSet<>();

        for (Class<?> c : classes)
            entries.addAll(ReflectionUtil.findAnnotatedMethodEntries(c, methodsAnnotationClass));

        return  entries;
    }
}
