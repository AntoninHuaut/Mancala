package fr.antoninhuaut.mancala;

import com.jdotsoft.jarloader.JarClassLoader;

public class MainLauncher {

    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("fr.antoninhuaut.mancala.AppFX", args);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
