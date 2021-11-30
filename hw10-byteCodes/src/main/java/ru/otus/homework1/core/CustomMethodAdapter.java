package ru.otus.homework1.core;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import ru.otus.homework1.annotation.Log;

import java.io.PrintStream;

public class CustomMethodAdapter extends AdviceAdapter {

    private String name;
    private boolean trace;
    private boolean isStatic;
    private final String constantMethodText;

    public static final Type PRINT_STREAM = Type.getType(PrintStream.class);
    public static final Type OBJECT = Type.getType(Object.class);
    public static final Type STRING = Type.getType(String.class);
    public static final Type ARRAY_CHAR = Type.getType(char[].class);
    public static final Type STRING_BUILDER = Type.getType(StringBuilder.class);

    private static final String ANNOTATION_DESCRIPTOR = Type.getType(Log.class).getDescriptor();
    private static final String STRING_BUILDER_INTERNAL_NAME = STRING_BUILDER.getInternalName();
    public static final String PRINT_STREAM_INTERNAL_NAME = PRINT_STREAM.getInternalName();

    public CustomMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {

        super(api, methodVisitor, access, name, descriptor);

        this.mv = methodVisitor;
        this.name = name;
        this.isStatic = (access > Opcodes.ACC_STATIC);

        constantMethodText = "executed method: " + name + " params: ";

    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

        if(descriptor.equalsIgnoreCase(ANNOTATION_DESCRIPTOR) && visible){
            this.trace = true;
        }
        return super.visitAnnotation(descriptor, visible);

    }

    @Override
    public void visitCode() {

        super.visitCode();

        if (trace){
            trace();
        }

    }

    private void trace() {

        Type[] types = Type.getArgumentTypes(methodDesc);
        visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        String printLnDescriptor;
        if (types.length == 0){
            visitLdcInsn(constantMethodText + "()");
            printLnDescriptor = "(Ljava/lang/String;)V";
        } else {
            printLnDescriptor = "(Ljava/lang/Object;)V";
            initInlineStringBuilder();
            addConstantStringValueIntoInlineStringBuilder(constantMethodText + " (");
            addParametrsValuesIntoInlineStringBuilder(types);
            addConstantStringValueIntoInlineStringBuilder(")");
        }

        this.visitMethodInsn(INVOKEVIRTUAL,
                             PRINT_STREAM_INTERNAL_NAME,
                      "println",
                             printLnDescriptor,
                    false);

    }

    private void initInlineStringBuilder(){

        visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);

        visitInsn(DUP);

        visitMethodInsn(
                INVOKESPECIAL,
                STRING_BUILDER_INTERNAL_NAME,
                "<init>",
                "()V",
                false);
    }

    private void addConstantStringValueIntoInlineStringBuilder(String value){

        visitLdcInsn(value);

        visitMethodInsn(
                INVOKEVIRTUAL,
                STRING_BUILDER_INTERNAL_NAME,
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);

    }

    private void addParametrsValuesIntoInlineStringBuilder(Type[] types) {

        int idxOnStack = isStatic ? 0 : 1;
        for (int i = 0; i < types.length; i++){

            addParametrValueIntoInlineStringBuilder(idxOnStack, types[i], this);

            idxOnStack += types[i].getSize();

            if (i < (types.length - 1)) {
                addConstantStringValueIntoInlineStringBuilder(", ");
            }

        }
    }

    private void addParametrValueIntoInlineStringBuilder(int index, Type variableType, MethodVisitor visitor) {

        Type correctType = getTypeForInlineStringBuilder(variableType);

        if (correctType == null) {
            addConstantStringValueIntoInlineStringBuilder("unknown type");
        } else {

            visitor.visitVarInsn(correctType.getOpcode(ILOAD), index);

            visitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    STRING_BUILDER_INTERNAL_NAME,
                    "append",
                    Type.getMethodDescriptor(STRING_BUILDER, correctType),
                    false);

        }
    }

    private Type getTypeForInlineStringBuilder(Type type) {

        switch (type.getSort()) {
            case Type.BYTE:
            case Type.SHORT:
                return Type.INT_TYPE;
            case Type.INT:
            case Type.CHAR:
            case Type.DOUBLE:
            case Type.FLOAT:
            case Type.LONG:
            case Type.BOOLEAN:
                return type;
            case Type.ARRAY:
            case Type.OBJECT:
                if (ARRAY_CHAR.equals(type) || STRING.equals(type)) return type;
                return OBJECT;
            default:
                return null;
        }
    }

}
