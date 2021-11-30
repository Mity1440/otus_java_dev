package ru.otus.homework1.core;

import org.objectweb.asm.*;
import ru.otus.homework1.model.Test;

import java.io.IOException;

public class CustomClassloader extends ClassLoader{

    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {

        try {

            ClassReader cr = new ClassReader(className);
            ClassWriter cw = new ClassWriter(cr,
                    ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            ClassVisitor visitor = new CustomClassVisitor(cw);

            cr.accept(visitor, ClassReader.EXPAND_FRAMES);

            byte classInByteArray[] = cw.toByteArray();

            return defineClass(className, classInByteArray, 0, classInByteArray.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.findClass(className);

    }

}
