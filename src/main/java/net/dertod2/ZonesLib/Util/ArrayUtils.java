package net.dertod2.ZonesLib.Util;

/**
 * Shares different Methods to convert Primitive Arrays to Object Arrays
 * and<br />
 * Object Arrays to Primitive Arrays
 * 
 * @author DerTod2
 *
 */
public class ArrayUtils {

    public static Byte[] toObject(byte[] primitiveArray) {
        Byte[] objectArray = new Byte[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++)
            objectArray[i] = primitiveArray[i];

        return objectArray;
    }

    public static byte[] toPrimitive(Byte[] objectArray) {
        byte[] primitiveArray = new byte[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];

        return primitiveArray;
    }

    public static Integer[] toObject(int[] primitiveArray) {
        Integer[] objectArray = new Integer[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++)
            objectArray[i] = primitiveArray[i];

        return objectArray;
    }

    public static int[] toPrimitive(Integer[] objectArray) {
        int[] primitiveArray = new int[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];

        return primitiveArray;
    }

    public static Double[] toObject(double[] primitiveArray) {
        Double[] objectArray = new Double[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++)
            objectArray[i] = primitiveArray[i];

        return objectArray;
    }

    public static double[] toPrimitive(Double[] objectArray) {
        double[] primitiveArray = new double[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];

        return primitiveArray;
    }

    public static Long[] toObject(long[] primitiveArray) {
        Long[] objectArray = new Long[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++)
            objectArray[i] = primitiveArray[i];

        return objectArray;
    }

    public static long[] toPrimitive(Long[] objectArray) {
        long[] primitiveArray = new long[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];

        return primitiveArray;
    }

    public static Float[] toObject(float[] primitiveArray) {
        Float[] objectArray = new Float[primitiveArray.length];
        for (int i = 0; i < primitiveArray.length; i++)
            objectArray[i] = primitiveArray[i];

        return objectArray;
    }

    public static float[] toPrimitive(Float[] objectArray) {
        float[] primitiveArray = new float[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];

        return primitiveArray;
    }
}