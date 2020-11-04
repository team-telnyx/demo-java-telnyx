<div align="center">

# Telnyx Java Phone Number Search and Order

![Telnyx](../logo-dark.png)

Sample application demonstrating Java SDK Basics

</div>

## About

This application demonstrates:

* Searching available number inventory
* Creating a number reservation
* Ordering a number to your Telnyx Account and assigning a [Messaging Profile](https://portal.telnyx.com/#/app/messaging)
* Receiving webhook once order is complete

## Pre-Reqs

You will need to set up:

* [Telnyx Account](https://telnyx.com/sign-up?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)
* Completed the messaging [Learn & Build](https://portal.telnyx.com/#/app/messaging/learn-and-build) to get your:
  * [Messaging Profile](https://portal.telnyx.com/#/app/messaging)
* Ability to receive webhooks (with something like [ngrok](https://developers.telnyx.com/docs/v2/development/ngrok?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link))
* [Java](https://developers.telnyx.com/docs/v2/development/dev-env-setup?lang=java) installed

## Usage

The following environmental variables need to be set

| Variable                      | Description                                                                                                                                 |
|:------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------|
| `TELNYX_API_KEY`              | Your [Telnyx API Key](https://portal.telnyx.com/#/app/api-keys?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) |
| `TELNYX_MESSAGING_PROFILE_ID` | Your [Messaging Profile ID](https://portal.telnyx.com/#/app/messaging).                                                                     |

### .env file

This app uses the excellent [dotenv-java](https://github.com/cdimascio/dotenv-java) package to manage environment variables.

Make a copy of [`.env.sample`](./.env.sample) and save as `.env` and update the variables to match your creds.

```
TELNYX_API_KEY="KEYLoremIpsum"
TELNYX_MESSAGING_PROFILE_ID=4001756a-a44b-424a-b73a-8ab676598fc3
```

### Callback URLs For Telnyx Applications

| Callback Type            | URL                          |
|:-------------------------|:-----------------------------|
| Number Order Complete | `{ngrok-url}/numbers/orders` |

### Install

Run the following commands to get started

```
$ git clone https://github.com/d-telnyx/demo-java-telnyx.git
$ cd spring-phonenumber-search-and-order
$ mvn clean install
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

At this point you can point your application to generated ngrok URL + path  (Example: `http://{your-url}.ngrok.io/Callbacks/Voice/Inbound`).

## Run

Open your IDE and run the application to launch the spring server.

The application works by proxying requests sent to endpoints defined in the Spring server.

### Endpoints

#### Search Inventory

Search the Telnyx Inventory by creating a `GET` request to your server and providing relevant query parameters.

```http
GET http://your-url.ngrok.io/numbers?countryCode=US&state=IL&city=Chicago&limit=10 HTTP/1.1
```

| Query Parameter | Description                                                           | Example   |
|:----------------|:----------------------------------------------------------------------|:----------|
| `countryCode`   | The country to search                                                 | `US`      |
| `state`         | State to search, correlates to `AdministrativeArea` in the Telnyx API | `IL`      |
| `city`          | City to search, correlates to `Locality` in the Telnyx API            | `Chicago` |
| `limit`         | Quantity of numbers to search                                         | `10`      |

##### Example

```bash
$ curl --location --request GET 'http://{your-url}.ngrok.io/numbers?countryCode=US&state=IL&city=Chicago&limit=2'
```

> Responds

```json
["+17733372901","+17733372986"]
```

#### Create Reservation

It's generally recommended to create a reservation any time there is a delay between searching and ordering phone numbers.  Reserving the phone numbers holds them for you to order for a brief period of time. Typically reservations are used any time an "end user" is selecting a phone number from a set.

The example only shows reserving a single phone number at a time; however multiple numbers can be reserved at once.

```http
POST http://your-url.ngrok.io/reservations HTTP/1.1
Content-Type: application/json; charset=utf-8

{
    "phoneNumber": "+19198675309"
}
```

| Parameter     | Description                 | Example        |
|:--------------|:----------------------------|:---------------|
| `phoneNumber` | The phone number to reserve | `+19198675309` |

##### Example

```bash
$ curl --location --request POST 'http://your-url.ngrok.io/reservations' \
--header 'Content-Type: application/json' \
--data-raw '{
    "phoneNumber": "+13125790590"
}'
```

> Responds

```json
{
  "id": "bdc55003-39ae-479a-adf5-2580e2e36acc",
  "record_type": "number_reservation",
  "phone_numbers": [
    {
      "id": "13d95cb3-8173-4f3b-8ed0-618cb3e335fd",
      "record_type": "reserved_phone_number",
      "phone_number": "+19198675309m",
      "status": "pending",
      "created_at": "2020-11-04T02:29:15.400162Z",
      "updated_at": "2020-11-04T02:29:15.400162Z",
      "expired_at": "2020-11-04T02:59:15.371680Z",
      "errors": ""
    }
  ],
  "status": "pending",
  "customer_reference": null,
  "created_at": "2020-11-04T02:29:15.400162Z",
  "updated_at": "2020-11-04T02:29:15.400162Z"
}
```

#### Create Order

Once a number has been selected to order and **optionally** reserved, you can order the number by creating a `POST` request to your server.
The example only shows ordering a single phone number at a time; however multiple numbers can be ordered at once.

The example also assigns the `Messaging Profile Id` to the phone number when the order completes. This reduces the need for additional API requests to immediately start using the phone number.

```http
POST http://your-url.ngrok.io/numbers HTTP/1.1
Content-Type: application/json; charset=utf-8

{
    "phoneNumber": "+19198675309"
}
```

| Parameter     | Description                 | Example        |
|:--------------|:----------------------------|:---------------|
| `phoneNumber` | The phone number to order | `+19198675309` |

#### Receive the Callback

Phone number ordering at Telnyx is an asynchronous process and Telnyx can be configured to send a [Webhook](https://developers.telnyx.com/docs/api/v2/numbers/Number-Orders#createNumberOrder) when the number order is complete.

Head to your [Account > Notifications > Settings](https://portal.telnyx.com/#/app/account/notifications/settings) and configure the following:

* Create a new Notification Profile and name it `Order Callbacks`
* Create a new Notification Channel
  * Select the recently created profile
  * Select the `Webhook` as notification type
  * Set the Destination as `http://your-url.ngrok.io/numbers/orders`
* Create a new Notification Setting
  * Set the Event "When a number order completes"
  * Set the Profile as the recently created profile

When the order completes, your server will receive a callback and print the number order to console.

```
class PhoneNumber {
    id: b56ce33d-97cf-41a5-b3e5-0ca76d42e062
    recordType: number_order_phone_number
    phoneNumber: +18287428820
    regulatoryGroupId: null
    regulatoryRequirements: []
    requirementsMet: true
    status: success
}
```

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
