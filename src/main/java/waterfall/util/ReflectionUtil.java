package waterfall.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ReflectionUtil {
    public static Set<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass)
            throws IOException, URISyntaxException, ClassNotFoundException {
        String path = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

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
            throws ClassNotFoundException {
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

    public static Set<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Set<Method> methods = new HashSet<>();

        for(Method method : clazz.getMethods())
            if(method.isAnnotationPresent(annotationClass))
                methods.add(method);

        return methods;
    }

    public static Set<Method> findAnnotatedMethodsInAnnotatedClasses(String packageName, Class<? extends Annotation> methodsAnnotationClass ,Class<? extends Annotation> classesAnnotationClass)
            throws IOException, URISyntaxException, ClassNotFoundException {
        Set<Class<?>> classes = findAnnotatedClasses(packageName, classesAnnotationClass);
        Set<Method> methods = new HashSet<>();

        for (Class<?> clazz : classes)
            methods.addAll(findAnnotatedMethods(clazz, methodsAnnotationClass));

        return methods;
    }
}
