package ru.otus.homework1.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CustomClassVisitor extends ClassVisitor {

    public CustomClassVisitor(ClassWriter cw) {
        super(Opcodes.ASM5, cw);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        return new CustomMethodAdapter(Opcodes.ASM5, mv, access, name, descriptor);

    }
}
