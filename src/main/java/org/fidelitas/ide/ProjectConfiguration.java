package org.fidelitas.ide;

import java.io.*;
import java.util.*;

public class ProjectConfiguration
{
    private final String projectPath;
    private String language;
    private String buildSystem;
    private String buildTask;
    private String runTask;
    private final Map<String[], String[]> taskRegister = new HashMap<>();
    public ProjectConfiguration(String path)
    {
        projectPath = path;
        taskRegister
                .put(new String[]{"java", "gradle"}, new String[]{"./gradlew run","./gradlew build"});
    }
    public void setLanguageAndBuildSystem(String language, String buildSystem)
    {
        this.language = language;
        this.buildSystem = buildSystem;
    }
    public void configureProject()
    {
        String configFileName = projectPath + "/.codedit.txt";
        try
        {
            File file = new File(configFileName);
            if (file.createNewFile())
            {
                String[] key = new String[]{language, buildSystem};
                String[] values = taskRegister.get(key);
                if (values != null)
                {
                    runTask = values[0];
                    buildTask = values[1];

                    FileWriter fileWriter = new FileWriter(configFileName);
                    fileWriter.write("runTask=" + runTask);
                    fileWriter.write("\n");
                    fileWriter.write("buildTask=" + buildTask);
                    fileWriter.close();
                }
            }
            else
            {
                BufferedReader reader = new BufferedReader(new FileReader(configFileName));
                String line = reader.readLine();
                while (line != null)
                {
                    String[] parts = line.split("=");
                    String key = parts[0];
                    String value = parts[1];

                    if (key.startsWith("#"))
                        continue;

                    switch (key)
                    {
                        case "language" -> language = value;
                        case "buildSystem" -> buildSystem = value;
                        case "runTask" -> runTask = value;
                        case "buildTask" -> buildTask = value;
                        default -> {
                        }
                    }
                    line = reader.readLine();
                }
                reader.close();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void executeRunTask() throws IOException, InterruptedException
    {
        executeTask(runTask);
    }
    public void executeBuildTask() throws IOException, InterruptedException
    {
        executeTask(buildTask);
    }
    private void executeTask(String task) throws IOException, InterruptedException
    {
        String osName = System.getProperty("os.name").toLowerCase();
        List<String> command;
        if (osName.contains("windows"))
            command = Arrays.asList("cmd.exe", "/c");
        else
            command = Arrays.asList("/bin/sh", "-c");

        command.addAll(List.of(task.split(" ")));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(projectPath));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

        int exitValue = process.waitFor();
        if (exitValue != 0)
            System.out.println("Abnormal process termination");
    }
}