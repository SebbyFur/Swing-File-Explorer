package src;

import java.io.*;

public class CustomSerializeObject {
    public static void serialize(Object object, String path, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            ;
        }
    }

    public static Object deserialize(String path, String fileName) {
        Object ret = null;
        try {
            FileInputStream fileIn = new FileInputStream(path + fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ret = in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return(ret);
    }
}