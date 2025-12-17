package waterfall.kernel.meta.util;

import waterfall.kernel.util.tuple.Pair;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public final class IOReflectionUtil {
    public static Set<Class<?>> findAnnotatedClasses
            (String packageName, Class<? extends Annotation> annotation)
            throws Exception {
        String path = packageName.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = loader.getResources(path);

        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.toURI());

            if (directory.exists() && directory.isDirectory())
                findAndRetrieveAnnotatedClasses(directory, packageName, classes, annotation);
        }

        return classes;
    }

    private static void findAndRetrieveAnnotatedClasses
            (File dir, String packageName, Set<Class<?>> classes, Class<? extends Annotation> annotation)
            throws Exception {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory())
                findAndRetrieveAnnotatedClasses(file, packageName  + "." + file.getName(), classes, annotation);

            else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                if(clazz.isAnnotationPresent(annotation))
                    classes.add(clazz);
            }
        }
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairs
            (String packageName, Class<? extends Annotation> classesAnnotationClass, Class<T> methodsAnnotationClass)
            throws Exception {
        Set<Class<?>> classes = findAnnotatedClasses(packageName, classesAnnotationClass);
        Set<Pair<Method, T>> pairs = new HashSet<>();

        for (Class<?> c : classes)
            pairs.addAll(ReflectionUtil.findMethodAndAnnotationPairs(c, methodsAnnotationClass));

        return  pairs;
    }
}
