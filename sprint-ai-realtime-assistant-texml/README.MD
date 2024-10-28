# Speech Assistant with Telnyx Voice and the OpenAI Realtime API (Java)

This application demonstrates how to use **Java**, [Telnyx Voice](https://developers.telnyx.com/docs/voice/programmable-voice/get-started) and [TeXML](https://developers.telnyx.com/docs/voice/programmable-voice/texml-setup), and [OpenAI's Realtime API](https://platform.openai.com/docs/) to make a phone call to speak with an AI Assistant.

The application opens WebSocket connections with the OpenAI Realtime API and Telnyx, and sends voice audio from one to the other to enable a two-way conversation.

This application uses the following Telnyx products in conjunction with OpenAI's Realtime API:

- **Voice** (TeXML, WebRTC)
- **Phone Numbers**

## Prerequisites

To use the app, you will need:

- **A Telnyx account.** You can [sign up for free](https://telnyx.com/sign-up).
- **Java Development Kit (JDK) 8 or higher.** Download from [here](https://www.oracle.com/java/technologies/javase-downloads.html) or use [OpenJDK](https://openjdk.java.net/).
- **Apache Maven.** To manage dependencies and build the project. Download from [here](https://maven.apache.org/download.cgi).
- **A Telnyx number with _Voice_ capabilities.** Find [a walkthrough to purchase a phone number here](https://support.telnyx.com/en/articles/4380325-search-and-buy-numbers).
- **An OpenAI account and an OpenAI API Key.** Sign up [here](https://platform.openai.com/).
- **OpenAI Realtime API access.**

## Local Setup Overview

This project can be summarized by the following steps:

1. **Set up Port Forwarding or a Tunneling Service.** Forward incoming traffic on the desired port to the internal address of your application. This can be done in various ways:
   - **Manually Port Forwarding** by configuring your router settings (not recommended due to potential security risks).
   - **Use a Tunneling Provider:**
     - [Ngrok](https://ngrok.com/).
     - [Expose](https://expose.dev/).
     - [Cloudflare Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/run-tunnel/).

     This guide demonstrates setting up the project using **ngrok**, but most tunneling providers would suffice by providing a safe, encrypted connection between your local machine and the internet. This is how your application will be able to communicate with the Telnyx and OpenAI ecosystems.

2. **Set Up Your Telnyx Account** with a successfully procured [phone number that has Voice capabilities](https://support.telnyx.com/en/articles/4380325-search-and-buy-numbers).

3. **Update and Fill Out the `.env` File,** making sure to remove the `.sample` suffix.

4. **Install the Dependencies and Packages** specified in the `pom.xml` file.

5. **Build and Run the Project!**

### Open an ngrok Tunnel

Port forwarding allows devices on a private network (LAN) to be accessed by devices on an external network (WAN) by directing incoming traffic from a specified port on the WAN to a specific device or service within the LAN. However, manually configuring port forwarding can expose your network to security risks.

An easier and safer way is to use a tunneling tool like **ngrok**. This avoids router configuration and provides encrypted connections between your local machine and the internet.

#### Open a Terminal and Run

```bash
ngrok http 8000
```

You should see output similar to:

```
ngrok by @inconshreveable

Session Status                online
Account                       Your Name (Plan: Free)
Version                       2.x.x
Region                        United States (us)
Web Interface                 http://127.0.0.1:4040
Forwarding                    http://ead8b6b4.ngrok.io -> http://localhost:8000
Forwarding                    https://ead8b6b4.ngrok.io -> http://localhost:8000

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

Once the tunnel is open and online, copy the `Forwarding` URL. It will look something like: `https://ead8b6b4.ngrok.io`.

Next, you'll need to configure Telnyx to route calls to this URL.

#### Adding the Public Proxy URL to Your Inbound Settings in the Mission Control Portal

1. **Navigate to Your TeXML Application:**
   - Go to the [TeXML Applications](https://portal.telnyx.com/#/call-control/texml) page in your Telnyx Mission Control Portal.

2. **Edit Your Application:**
   - Click the edit symbol [✎] next to your TeXML Application.

3. **Update the Webhook URL:**
   - In the **"App Info"** section, find the **"Send a webhook to the URL: (Required)"** field.
   - Paste the forwarding address from ngrok into the Webhook URL field.
   - **Add `/inbound`** to the end of the URL to direct the request to the webhook endpoint in your server.
     - Example: `https://ead8b6b4.ngrok.io/inbound`.

   > **Ensure that the Voice Method is set to `POST`.**

4. **Save Your Changes:**
   - Click **"Save"** to apply the changes.

**Note:** The `ngrok` command above forwards to a development server running on port `8000`, which is the default port configured in this application. If you override the `PORT` defined in your application, you will need to update the `ngrok` command accordingly.

Keep in mind that each time you run the `ngrok http` command, a new URL will be created, and you'll need to update it wherever it's referenced.

## Run the App

Once ngrok is running and Telnyx is configured properly, fill out the `.env.sample` file with your secrets.

> **Note:** Make sure to remove the `.sample` portion in the suffix and save it as just a `.env` file.

### Build and Run the Application

1. **Install Dependencies:**

   The project uses Maven to manage dependencies. Ensure you have Maven installed and set up properly.

   **Command:**

   ```bash
   mvn clean install
   ```

   This command will download all necessary dependencies specified in the `pom.xml` file and compile the project.

2. **Run the Application:**

   You can run the application directly using Maven or by executing the generated JAR file.

   **Option 1: Using Maven**

   ```bash
   mvn spring-boot:run
   ```

   **Option 2: Running the JAR File**

   After building the project, the JAR file will be located in the `target` directory.

   ```bash
   java -jar target/speech-assistant-0.0.1-SNAPSHOT.jar
   ```

   Replace `speech-assistant-0.0.1-SNAPSHOT.jar` with the actual name of your built JAR file if it's different.

3. **Verify the Application is Running:**

   The application should now be running on `http://localhost:8000`.

   You can verify this by visiting `http://localhost:8000/` in your browser. You should see a message:

   ```json
   {"message": "Telnyx Media Stream Server is running!"}
   ```

## Test the App

Now you should be able to call the phone number associated with your Telnyx TeXML application. When you call, you should be connected to the AI assistant, which will respond to your voice and engage in conversation.

## Project Structure

Here's a brief overview of the project's structure:

- **`src/main/java/com/example/demo`**: Contains the Java source code.
  - **`Application.java`**: The main entry point of the Spring Boot application.
  - **`RootController.java`**: Handles the root endpoint (`/`).
  - **`InboundController.java`**: Handles the inbound calls (`/inbound` endpoint).
  - **`WebSocketConfig.java`**: Configures the WebSocket handler.
  - **`MediaStreamHandler.java`**: Handles the WebSocket connections with Telnyx.
  - **`ClientSessionHandler.java`**: Manages individual sessions between Telnyx and OpenAI.
- **`src/main/resources`**: Contains resource files.
  - **`texml.xml`**: The TeXML file used for call instructions.
  - **`application.properties`**: Spring Boot application configurations.

## Environment Variables

The application uses a `.env` file to store sensitive information like the OpenAI API key and the port number.

Create a `.env` file in the root directory of your project (remove the `.sample` extension from `.env.sample`) and add the following:

```
OPENAI_API_KEY=your-openai-api-key
PORT=8000
```

Replace `your-openai-api-key` with your actual OpenAI API key.

## Dependencies

The project relies on several dependencies, which are managed via Maven:

- **Spring Boot Starter Web**: For building web applications.
- **Spring Boot Starter WebSocket**: For handling WebSocket connections.
- **Tyrus Standalone Client**: A WebSocket client for connecting to the OpenAI API.
- **Java-Dotenv**: For loading environment variables from a `.env` file.
- **Jackson Databind**: For processing JSON data.

These dependencies are specified in the `pom.xml` file.

## Important Notes

- **OpenAI Realtime API Access**: Ensure that your OpenAI account has access to the Realtime API and that your API key is valid.
- **Firewall and Network Settings**: Make sure your local machine allows incoming connections on the port you're using (default is `8000`).
- **Updating ngrok URL**: Remember to update the webhook URL in your Telnyx application settings whenever the ngrok URL changes.
- **Testing**: Use tools like Postman or curl to test your endpoints (`/` and `/inbound`) before making actual calls.

## Troubleshooting

- **Application Fails to Start**: Check for any errors in the console output. Ensure all dependencies are correctly installed and that your Java version is compatible.
- **Cannot Connect to OpenAI API**: Verify your internet connection and ensure your API key is correct.
- **No Response from AI Assistant**: Ensure that the WebSocket connections are established correctly and that there are no network issues blocking the connections.

## Additional Resources

- **Spring Boot Documentation**: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **Telnyx Developer Documentation**: [https://developers.telnyx.com/docs/v2/overview](https://developers.telnyx.com/docs/v2/overview)
- **OpenAI API Documentation**: [https://platform.openai.com/docs/](https://platform.openai.com/docs/)

## Conclusion

You have successfully set up a Java application that integrates Telnyx Voice services with the OpenAI Realtime API to create a speech assistant. You can now make a phone call and interact with an AI assistant over the phone!

If you encounter any issues or have questions, feel free to reach out to the community or consult the documentation provided by Telnyx and OpenAI.

**Happy coding!**
