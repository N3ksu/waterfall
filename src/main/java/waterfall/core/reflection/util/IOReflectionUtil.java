package waterfall.core.reflection.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class IOReflectionUtil {
    public static Set<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> classesAnnotationClass)
            throws Exception {
        String path = packageName.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = loader.getResources(path);

        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.toURI());

            if (directory.exists() && directory.isDirectory())
                findAndRetrieveAnnotatedClassesInDirectory(directory, packageName, classes, classesAnnotationClass);
        }

        return classes;
    }

    private static void findAndRetrieveAnnotatedClassesInDirectory(File directory, String packageName, Set<Class<?>> classes, Class<? extends Annotation> classesAnnotationClass)
            throws Exception {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                findAndRetrieveAnnotatedClassesInDirectory(file, packageName  + "." + file.getName(), classes, classesAnnotationClass);

            else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);

                if(clazz.isAnnotationPresent(classesAnnotationClass))
                    classes.add(clazz);
            }
        }
    }

    public static Set<Method> findAnnotatedMethodsInAnnotatedClasses(String packageName, Class<? extends Annotation> methodsAnnotationClass ,Class<? extends Annotation> classesAnnotationClass)
            throws Exception {
        Set<Class<?>> classes = findAnnotatedClasses(packageName, classesAnnotationClass);
        Set<Method> methods = new HashSet<>();

        for (Class<?> clazz : classes)
            methods.addAll(ReflectionUtil.findAnnotatedMethods(clazz, methodsAnnotationClass));

        return methods;
    }
}
