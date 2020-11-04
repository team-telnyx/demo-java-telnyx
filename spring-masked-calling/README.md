<div align="center">

# Telnyx Java Masked Calling Starter

![Telnyx](../logo-dark.png)

Sample application demonstrating Java SDK Basics

</div>

## About

This application demonstrates:

* Receiving call-control webhooks from Telnyx
* Parsing contents from inbound webhooks
* Answering inbound calls and forwarding to predefined number
* Receiving messaging webhooks from Telnyx
* Building an outbound message request
* "Forwarding" The inbound text from the masked number

### Functionality

You will need access to two phones to call yourself for the demo.  At a high level this application provides a "masked" or "proxy" number in between two predefined phone numbers.

* When the **TELNYX** number receives an inbound call or SMS, it will look up the `from` (who iniated the call) based on the `telnyx` number
  * From there, it can determine who the compliment phone number for the mask should be, and will transfer the call or create a new text to that number
* ⚠️ (_UPDATE_) The application _ignores_ **A LOT** of call-control events that are not relevant to the core functionality. In a production system, your application should at minimum respond to **all** call-control events.

#### User A calling/texts User B via a masked/proxy number

```
{User A} ==(calls)==> {Telnyx Number} ==(transfers with "from" as the Telnyx Number)==> {User B}
{User A} ==(texts)==> {Telnyx Number} ==(creates new text "from" Telnyx Number)==> {User B}
```

#### User B calling/texts User A via a masked/proxy number

```
{User B} ==(calls)==> {Telnyx Number} ==(transfers with "from" as the Telnyx Number)==> {User A}
{User B} ==(texts)==> {Telnyx Number} ==(creates new text "from" Telnyx Number)==> {User A}
```

## Pre-Reqs

You will need to set up:

* [Telnyx Account](https://telnyx.com/sign-up?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)
* Completed the messaging [Learn & Build](https://portal.telnyx.com/#/app/messaging/learn-and-build) to get your:
  * [Phone Number](https://portal.telnyx.com/#/app/numbers/my-numbers)
  * [Messaging Profile](https://portal.telnyx.com/#/app/messaging)
* [Outbound Voice Profile](https://portal.telnyx.com/#/app/outbound-profiles) configured for you locality
* Ability to receive webhooks (with something like [ngrok](https://developers.telnyx.com/docs/v2/development/ngrok?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link))
* [Java](https://developers.telnyx.com/docs/v2/development/dev-env-setup?lang=java) installed

## Usage

The following environmental variables need to be set

| Variable              | Description                                                     | Example        |
|:----------------------|:----------------------------------------------------------------|:---------------|
| `TELNYX_API_KEY`      | Your [Telnyx API Key](https://portal.telnyx.com/#/app/api-keys) | `KEYloreimpus` |
| `USER_PHONE_NUMBER_A` | Phone number of 1 of the 2 users                                | `+17048675310` |
| `USER_PHONE_NUMBER_B` | Phone number of the other 1 of the 2 users                      | `+19198675309` |
| `TELNYX_PHONE_NUMBER` | Phone number mask (your telephone number)                       | `+19842550944` |

### .env file

This app uses the excellent [dotenv-java](https://github.com/cdimascio/dotenv-java) package to manage environment variables.

Make a copy of [`.env.sample`](./.env.sample) and save as `.env` and update the variables to match your creds.

```
TELNYX_API_KEY="KEYLoremIpsum"
USER_PHONE_NUMBER_A=+17048675310
USER_PHONE_NUMBER_B=+19198675309
TELNYX_PHONE_NUMBER=+19842550944
```

### Ngrok

This application is served on the port defined in the runtime environment (or in the `.env` file). Be sure to launch [ngrok](https://developers.telnyx.com/docs/v2/development/ngrok?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) for that port

```
./ngrok http 8000
```

> Terminal should look _something_ like

```
ngrok by @inconshreveable                                                                                                                               (Ctrl+C to quit)

Session Status                online
Account                       Little Bobby Tables (Plan: Free)
Version                       2.3.35
Region                        United States (us)
Web Interface                 http://127.0.0.1:4040
Forwarding                    http://your-url.ngrok.io -> http://localhost:8000
Forwarding                    https://your-url.ngrok.io -> http://localhost:8000

Connections                   ttl     opn     rt1     rt5     p50     p90
                              0       0       0.00    0.00    0.00    0.00
```

At this point you can point your application to generated ngrok URL + path  (Example: `http://{your-url}.ngrok.io/call-control/inbound`).

### Callback URLs For Telnyx Applications

| Callback Type                                  | URL                                          | Portal Config                                                                                     |
|:-----------------------------------------------|:---------------------------------------------|:--------------------------------------------------------------------------------------------------|
| Inbound Message Callback                       | `{ngrok-url}/messaging/inbound`              | Yes, in the [Messaging Profile](https://portal.telnyx.com/#/app/messaging)                        |
| Outbound Message Status Callback               | `{ngrok-url}/messaging/outbound`             | No, done at message creation time                                                                 |
| Inbound Call-Control Initiated Events Callback | `{ngrok-url}/call-control/inbound`           | Yes, in the [Call Control Application](https://portal.telnyx.com/#/app/call-control/applications) |
| Inbounc Call-Control Answer Events Callback    | `{ngrok-url}/call-control/inbound/answer`    | No, done at call time                                                                             |
| Outbound Call-Control Transfer Events Callback | `{ngrok-url}/call-control/outbound/transfer` | No, done at call time                                                                             |

### Install

Run the following commands to get started

```
$ git clone https://github.com/d-telnyx/demo-java-telnyx.git
$ cd spring-masked-calling
$ mvn clean install
```

## Run

Open your IDE and run the application to launch the spring server

* Text your Telnyx Number from one of the two phones, and see the message arrive _from_ the Telnyx number to the other phone (and vice-versa)
* Call your Telnyx Number from one of the two phones and receive a call _from_ the Telnyx number to your other phone
  * Answering the call will connect/bridge the two calls to talk to each other



## Development

Application was generated from SpringGenerators, see the [Help.md](HELP.md) for more information.

### ⚠️ FOR SPRING USERS

The SDK is built using [openapi-generator](https://github.com/OpenAPITools/openapi-generator), in order to use the deserializers, you need to include [jackson-databind-nullable](https://github.com/OpenAPITools/jackson-databind-nullable) (see [Stack Overflow](https://stackoverflow.com/questions/59524404/openapi-springboot-generator-jackson-no-string-argument-constructor-factory-meth))

#### Add to pom.xml

```xml
<dependency>
  <groupId>org.openapitools</groupId>
  <artifactId>jackson-databind-nullable</artifactId>
  <version>0.2.1</version>
</dependency>
```

#### Add to objectMapper

```java
@Autowired
void configureObjectMapper(final ObjectMapper mapper) {
    mapper.registerModule(new JsonNullableModule());
}
```
