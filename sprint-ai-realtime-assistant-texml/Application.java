package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static String OPENAI_API_KEY;
    public static String SYSTEM_MESSAGE = "You are a helpful and bubbly AI assistant who loves to chat about anything the user is interested about and is prepared to offer them facts.";
    public static String VOICE = "alloy";
    public static int PORT = 8000; // Default port

    public static void main(String[] args) {
        // Load environment variables
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
        String portEnv = dotenv.get("PORT");
        if (portEnv != null) {
            PORT = Integer.parseInt(portEnv);
        }

        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.out.println("Missing OpenAI API key. Please set it in the .env file.");
            System.exit(1);
        }

        SpringApplication.run(Application.class, args);
    }
}