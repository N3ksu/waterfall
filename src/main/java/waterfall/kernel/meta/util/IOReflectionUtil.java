package waterfall.kernel.meta.util;

import waterfall.kernel.exception.technical.io.ResourceLoadException;
import waterfall.kernel.exception.technical.meta.IOClassNotFoundException;
import waterfall.kernel.util.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public final class IOReflectionUtil {
    public static Set<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotation) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classes = new HashSet<>();

        String path = packageName.replace(".", "/");

        try {
            Enumeration<URL> resources = loader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.toURI());

                if (directory.exists() && directory.isDirectory())
                    findAndRetrieveAnnotatedClasses(directory, packageName, classes, annotation);
            }
        } catch (IOException | URISyntaxException e) {
            throw new ResourceLoadException(path, e);
        }

        return classes;
    }

    private static void findAndRetrieveAnnotatedClasses(File directory, String packageName, Set<Class<?>> classes, Class<? extends Annotation> annotation) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                findAndRetrieveAnnotatedClasses(file, packageName  + "." + file.getName(), classes, annotation);

            else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new IOClassNotFoundException(e);
                }

                if(clazz.isAnnotationPresent(annotation))
                    classes.add(clazz);
            }
        }
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findAnnotatedMethods(String packageName, Class<? extends Annotation> cac, Class<T> mac) {
        Set<Class<?>> classes = findAnnotatedClasses(packageName, cac);
        Set<Pair<Method, T>> pairs = new HashSet<>();

        for (Class<?> clazz : classes)
            pairs.addAll(ReflectionUtil.findAnnotatedMethod(clazz, mac));

        return  pairs;
    }
}
