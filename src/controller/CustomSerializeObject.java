package src.controller;

import java.io.*;

public class CustomSerializeObject {
    public static void serialize(Object object, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream("./" + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public static Object deserialize(String fileName) {
        Object ret = null;
        try {
            FileInputStream fileIn = new FileInputStream("./" + fileName);
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