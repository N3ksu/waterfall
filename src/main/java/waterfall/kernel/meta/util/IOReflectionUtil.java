package waterfall.kernel.meta.util;

import waterfall.kernel.util.tuple.Pair;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public final class IOReflectionUtil {
    public static Set<Class<?>> findAnnotatedClassesInPackage(
            String packageName, Class<? extends Annotation> annotationClass)
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

    private static void findAndRetrieveAnnotatedClassesInDirectory(
            File directory, String packageName, Set<Class<?>> classes, Class<? extends Annotation> annotationClass)
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

    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairsInAnnotatedClassesInPackage(
            String packageName, Class<? extends Annotation> classesAnnotationClass, Class<T> methodsAnnotationClass)
            throws Exception {

        Set<Class<?>> classes = findAnnotatedClassesInPackage(packageName, classesAnnotationClass);
        Set<Pair<Method, T>> pairs = new HashSet<>();

        for (Class<?> c : classes)
            pairs.addAll(ReflectionUtil.findMethodAndAnnotationPairs(c, methodsAnnotationClass));

        return  pairs;
    }
}
