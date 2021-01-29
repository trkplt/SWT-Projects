package org.iMage.Course;

import org.iMage.plugins.PluginForJmjrst;
import org.jis.Main;
import org.kohsuke.MetaInfServices;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Random;

@MetaInfServices
public class JavaCrashCourse extends PluginForJmjrst {
    private Main main;
    private final List<String> versions;

    public JavaCrashCourse() {
        versions = List.of("Java 8", "Java 9", "Java 10", "Java 11", "Java 12", "Java 13", "Java 14");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getNumberOfParameters() {
        return versions.size();
    }

    @Override
    public void init(Main main) {
        this.main = main;
        System.out.printf(main.mes.getString("Messages.4"), this.getNumberOfParameters());
    }

    @Override
    public void run() {
        Random randomGen = new Random();
        int random = randomGen.nextInt(versions.size());
        String version = versions.get(random);
        switch (version) {
            case "Java 8", "Java 9", "Java 10", "Java 11", "Java 12", "Java 13" -> System.out.println("Running late");
            case "Java 14" -> System.out.println("Keeping updated");
            default -> System.out.printf("%s(%s)", this.getName(), this.getNumberOfParameters());
        }
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void configure() {
        JTextArea textArea = new JTextArea();

        versions
                .stream()
                .forEach((version) -> {
                    textArea.append(version + System.getProperty("line.separator"));
                });

        textArea.setEditable(false);

        JOptionPane.showMessageDialog(null, textArea, main.mes.getString("Menu.18") + " "
                + this.getName(), JOptionPane.PLAIN_MESSAGE);
    }
}
