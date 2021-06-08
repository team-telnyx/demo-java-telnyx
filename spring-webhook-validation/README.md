<div align="center">

# Telnyx Java Webhook Validation

![Telnyx](../logo-dark.png)

Sample application validating webhooks with Java SDK

</div>

## Overview

In order to observe the webhook validation service in operation, we must:

* [Setup and run the spring messaging auto response demo](https://github.com/team-telnyx/demo-java-telnyx/tree/master/spring-messaging-auto-response), ensuring that we keep the `ngrok` session alive as we record incoming webhooks.
* Shut down the messaging auto response server
* Setup and run this project on port 8000 (follow instructions below)
* [From the (still running) ngrok web interface](http://127.0.0.1:4040), click to replay a recent webhook
* Note the successful receipt of the validated webhook
* Now repeat the process above, this time modifying the value of the `telnyx-signature-ed25519` field.
* Note the unsuccessful receipt of the invalidated webhook

## Setup

The following environmental variables need to be set

| Variable                      | Description                                                                                                                                            |
|:------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TELNYX_PUBLIC_KEY`           | Your [Telnyx API Key](https://portal.telnyx.com/#/app/api-keys/public-key?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) |

### .env file

This app uses the excellent [dotenv-java](https://github.com/cdimascio/dotenv-java) package to manage environment variables.

Make a copy of [`.env.sample`](./.env.sample) and save as `.env` and update the variables to match your creds.

```
TELNYX_PUBLIC_KEY="KEYLoremIpsum"
```

## Run

Open your IDE and run the application to launch the spring server.

The application works by proxying requests sent to endpoints defined in the Spring server.
