package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        var configClasses = List.of(initialConfigClass);
        checkConfigClass(configClasses);
        processConfig(configClasses);
    }

    public AppComponentsContainerImpl(Class<?> ... initialConfigClasses) {
        var configClasses = Arrays.stream(initialConfigClasses).collect(Collectors.toList());
        checkConfigClass(configClasses);
        processConfig(configClasses);
    }

    public AppComponentsContainerImpl(String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        var configClasses =  reflections
                                                   .getTypesAnnotatedWith(AppComponentsContainerConfig.class)
                                                   .stream().collect(Collectors.toList());
        checkConfigClass(configClasses);
        processConfig(configClasses);
    }

    //region Processing

    private void processConfig(Class<?> configClass) {

        Object configInstance = getConfigInstance(configClass);

        Arrays.stream(configClass.getDeclaredMethods())
                .filter(o -> o.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(AppComponent.class).order()))
                .forEach(method -> {

                    Object appComponent = createComponent(configInstance, method);

                    appComponents.add(appComponent);
                    appComponentsByName.put(method.getAnnotation(AppComponent.class).name(), appComponent);

                });

    }

    private Object getConfigInstance(Class<?> configClass) {

        Object configInstance;
        try {
            Constructor<?> constructor = configClass.getConstructor();
            constructor.setAccessible(true);
            configInstance = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String
                                 .format("Not found default constructor %s", configClass.getName()));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error occurred during instantiation");
        }
        ;
        return configInstance;
    }

    private void processConfig(List<? extends Class<?>> configClasses){
        // Есть два варианта
        // - иметь сквозную нумерацию в рамках всех конфигураций
        // - иметь собственнцю нумерацию внутри конфигурации и учитывать нумерацию самой конфигурации (он и выбран)
        configClasses.stream()
                .sorted(Comparator.comparingInt(clazz->clazz.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    //region Processing

    //region Checkers

    private void  checkConfigClass(List<? extends Class<?>> initialConfigClasses){

        Set<String> existingNames = new HashSet<>();

        for (var clazz: initialConfigClasses){

            if (!clazz.isAnnotationPresent(AppComponentsContainerConfig.class)) {
                throw new IllegalArgumentException(String.format("Given class is not config %s", clazz.getName()));
            }

            Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method->method.isAnnotationPresent(AppComponent.class))
                    .forEach(method->{

                        var componentName = method.getDeclaredAnnotation(AppComponent.class).name();
                        if (existingNames.contains(componentName)){
                            throw new RuntimeException(String.format(
                                    "Error. There are more than one method with component name %s", componentName));
                        }
                        existingNames.add(componentName);

                    });

        }

    }

    //endregion

    //region AppComponents

    @Override
    @SuppressWarnings("all")
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) appComponents.stream()
                .filter(componentClass::isInstance)
                .findAny()
                .orElseThrow(() -> new RuntimeException(
                                String.format("No found instance of %s", componentClass.getName())));
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) Optional.ofNullable(appComponentsByName.get(componentName))
                .orElseThrow(()->new RuntimeException(
                        String.format("No found instance with component name %s", componentName)));
    }

    //endregion

    //region Wrappers

    private Object wrapException(Callable<?> action) {
        try {
            return action.call();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    //endregion

    //region Service

    private Object createComponent(Object configInstance, Method method) {

        return wrapException(()->{

            Object[] methodParametrs = Arrays
                    .stream(method.getParameterTypes())
                    .map(this::getAppComponent)
                    .toArray();

            return method.invoke(configInstance, methodParametrs);

        });
    }

    //endregion

}
