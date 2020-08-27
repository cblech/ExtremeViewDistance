package net.fabricmc.example.renderer.util;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VertexBufferObject {
    private final int glReference;
    private int vertexCount;
    List<Factory.AttribPointerType> attribPointers = new ArrayList<>();

    private static VertexBufferObject currentlyBoundArrayBuffer = null;

    private VertexBufferObject() {
        glReference = GL15.glGenBuffers();
    }

    public static Factory factory() {
        return new Factory();
    }

    public void bind() {
        if (currentlyBoundArrayBuffer == this) {
            //return;
        }
        currentlyBoundArrayBuffer = this;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glReference);
    }

    public void bind(int target) {
        if (target == GL15.GL_ARRAY_BUFFER) {
            if (currentlyBoundArrayBuffer == this) {
                //return;
            }
            currentlyBoundArrayBuffer = this;
        }
        GL15.glBindBuffer(target, glReference);
    }

    public void unbind() {
        currentlyBoundArrayBuffer=null;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void draw(int mode) {
        //bind();
        GL11.glDrawArrays(mode, 0, vertexCount);
    }

    public void draw() {
        //bind();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
    }


    public final Data data = new Data();

    public int getGlRef() {
        return glReference;
    }

    public class Data {
        private List<Byte> bytes;

        public Data clear() {
            bytes = new ArrayList<>();
            vertexCount = 0;
            return this;
        }

        public Data put(Vector3f vector3f) {
            return putGeneric(new float[]{vector3f.getX(), vector3f.getY(), vector3f.getZ()});
        }

        public Data put(Vector4f vector4f) {
            return putGeneric(new float[]{vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW()});
        }

        private <T> Data putGeneric(T value) {
            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(value);
                }

                for (byte bt : b.toByteArray()) {
                    bytes.add(bt);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return this;
        }

        public Data next() {
            vertexCount++;
            return this;
        }

        public void push() {
            ByteBuffer bb = ByteBuffer.allocateDirect(bytes.size());

            for (Byte aByte : bytes) {
                bb.put(aByte);
            }

            bind();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bb, GL15.GL_STATIC_DRAW);

            int stride = 0;
            for (Factory.AttribPointerType attr : attribPointers) {
                stride += attr.count * attr.typeSize;
            }

            int pointer = 0;
            for (int i = 0; i < attribPointers.size(); i++) {
                Factory.AttribPointerType attr = attribPointers.get(i);

                GL20.glVertexAttribPointer(i, attr.count, attr.glType, false, stride, pointer);
                GL20.glEnableVertexAttribArray(i);
                pointer += attr.count * attr.typeSize;
            }
        }
    }

    public static class Factory {
        VertexBufferObject product;


        private Factory() {
            product = new VertexBufferObject();
        }

        public Factory addAttribPointer(AttribPointerType type) {
            product.attribPointers.add(type);
            return this;
        }

        public VertexBufferObject create() {
            return product;
        }

        private static final Map<Integer, Integer> glTypeSize;

        static {
            glTypeSize = new HashMap<>();
            glTypeSize.put(GL11.GL_BYTE, 1);
            glTypeSize.put(GL11.GL_UNSIGNED_BYTE, 1);
            glTypeSize.put(GL11.GL_SHORT, 2);
            glTypeSize.put(GL11.GL_UNSIGNED_SHORT, 2);
            glTypeSize.put(GL11.GL_INT, 4);
            glTypeSize.put(GL11.GL_UNSIGNED_INT, 4);
            glTypeSize.put(GL41.GL_FIXED, 4);
            glTypeSize.put(GL30.GL_HALF_FLOAT, 2);
            glTypeSize.put(GL11.GL_FLOAT, 4);
            glTypeSize.put(GL11.GL_DOUBLE, 8);
        }

        public enum AttribPointerType {
            VEC1F(GL11.GL_FLOAT, 1),
            VEC2F(GL11.GL_FLOAT, 2),
            VEC3F(GL11.GL_FLOAT, 3),
            VEC4F(GL11.GL_FLOAT, 4);

            int glType;
            int typeSize;
            int count;

            AttribPointerType(int glType, int count) {
                this.glType = glType;
                this.typeSize = glTypeSize.get(glType);
                this.count = count;
            }

        }
    }
}
